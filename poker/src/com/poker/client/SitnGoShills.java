package com.poker.client;

import com.agneya.util.Configuration;
import com.agneya.util.Rng;

import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;

import com.poker.common.message.CommandBuyChips;
import com.poker.common.message.CommandLogin;
import com.poker.common.message.CommandMove;
import com.poker.common.message.CommandRegister;
import com.poker.common.message.CommandTableDetail;
import com.poker.common.message.CommandTableList;
import com.poker.common.message.ResponseConfig;
import com.poker.common.message.ResponseFactory;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseLogin;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponseTableDetail;
import com.poker.common.message.ResponseTableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Shill client
 *
 */

public class SitnGoShills
    implements Runnable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(SitnGoShills.class.getName());

  Vector _players;
  ResponseTableList _rtd;
  Selector _selector;
  int _density;
  Rng _rng;
  int DELAY_MS = 0;
  int DENSITY = 0;
  Response _responseStack[];
  int _htbt;
  Player _dummy;
  int _tc;
  TableMap[] _tm;



  private final static byte[] HTTP_HEADER = {
      71, 69, 84, 32, 47, 32, 72, 84, 84, 80, 47, 49, 46, 48, 10};
  private final static int HTTP_HEADER_SIZE = 15;


  private final static byte[] TERMINATOR = {
       38, 84, 61, 90, 13, 10, 0};

  private final static int TERMINATOR_SIZE = 7;
  protected static String _ctype;

  Configuration _conf = null;
  /**
   * runner listens to responses from the poker server
   */
  private Thread _runner = null;
  boolean _keepListening;

  public SitnGoShills() {
    _players = new Vector();
    //initialize the RNG
    _rng = new Rng();
  }

  public void startShills() throws Exception {
    //initialize the vector with player data
    // random for now
    _conf = Configuration.getInstance();
    DELAY_MS = Integer.parseInt( (String) _conf.get("Shill.Delay"));
    _density = Integer.parseInt( (String) _conf.get("Shill.Density"));
    _htbt = _conf.getInt("Server.maintenance.heartbeat");
    _ctype = (String) _conf.get("Client.Type");
    _cat.finest(_ctype);
    _responseStack = new Response[8 * _density];
    _selector = Selector.open();
    _dummy = new Player("scott", "poker");
    // start a heartbeat timer thread
    HeartBeat hb = new HeartBeat();
    Timer t = new Timer();
    t.schedule(hb, 0, _htbt / 2);
    int pn_count = player_names.length * player_sep.length * player_ext.length;
    _tc = tableCount();
    int pcount = _tc * _density;
    if (pcount > pn_count) {
      _cat.warning(pcount +
                 " Player names not available - Unable to initialize players " +
                 pn_count);
    }
    String pn[] = new String[pn_count];
    pn_count = 0;
    for (int j = 0; j < player_sep.length; j++) {
      for (int k = 0; k < player_ext.length; k++) {
        for (int i = 0; i < player_names.length; i++) {
          pn[pn_count++] = player_names[i] + player_sep[j] + player_ext[k];
        }
      }
    }
    _tm = tableMap();
      if (_tm.length == 0) {
        _cat.log(Level.WARNING, "There are no poker games running");
        System.exit( -1);
      }
      int pos;
      String gid;
      for (pcount--; pcount >= 0; pcount--) {
        int k = _rng.nextIntBetween(0, _tc);
        gid = _tm[k]._gname;
        pos = _rng.nextIntBetween(0, _tm[k]._maxPlayer);
        int i = 0;
        boolean expelled = false;
        int tried = 0;
        _cat.finest("table gname" + _tm[k]._gname);
        _cat.finest("table player details=" + _tm[k]._playerDetails[i]);
        while (_tm[k]._playerDetails.length < _tm[k]._maxPlayer && _tm[k]._playerDetails[i] == -1) {
           _cat.finest("table max players=" + _tm[k]._maxPlayer);
          pos = _rng.nextIntBetween(0, _tm[k]._maxPlayer);
          i++;
          tried++;
          if (!seatVacant(_tm[k]) && tried == 10) {
            _cat.log(Level.WARNING, "Unable to seat the player " + pn[pcount] +  " on the table " + gid);
            expelled = true;
            tried = 0;
            break;
          }
        }
        if (!expelled) {
          addPlayer(pn[pcount], pn[pcount], gid, pos);
        }
        _tm[k]._playerDetails[pos] = -1; // mark this as occupied
      }
    _runner = new Thread(this);
    _runner.start();
  }

  public void run() {
    boolean keepLooking = true;
    Player p = null;
    SelectionKey key = null;
    while (keepLooking) {
      try {
        _selector.selectNow();
        // Now we deal with our incoming data / completed writes...
        Set keys = _selector.selectedKeys();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
          key = (SelectionKey) i.next();
          i.remove();
          if (key.isReadable()) {
            int top = 0;
            p = (Player) key.attachment();
            String response = p.read();
            if (response == null) {
              continue;
            }
            ResponseFactory rf = new ResponseFactory(response);
            Response r = rf.getResponse();
            if (r.getResult() != 1) {
              continue;
            }
            if (r._response_name == Response.R_MOVE) {
              p.processMove( (ResponseGameEvent) r);
            }
            else if (r._response_name == Response.R_MESSAGE) {
              ResponseMessage rm = (ResponseMessage) r;
              _cat.info("Message =" + rm.getMessage());
            }
            else if (r._response_name == Response.R_LOGOUT) {
              _cat.warning(" Logged out " + p);
              key.cancel();
              _players.remove(p);
            }
            else if (r._response_name == Response.R_TABLEDETAIL) {
              ResponseTableDetail rm = (ResponseTableDetail) r;
              _cat.info("Message =" + rm);
            }
            else if (r._response_name == Response.R_GET_CHIPS_INTO_GAME) {
              //
            }

            else {
              _cat.log(Level.WARNING, 
                  "SHILL: FATAL: Unknown event received from the server " + r);
            }
          }
        }
        Thread.currentThread().sleep(10);
      }
      catch (IOException ex) {
        _cat.log(Level.WARNING, " Removing player ", ex);
        key.cancel();
        _players.remove(p);
      }
      catch (Exception ex) {
        _cat.log(Level.WARNING, "Logging out the errant player ", ex);
        key.cancel();
        _players.remove(p);
        try {
          p.logout();
        }
        catch (Exception e) {}
      }
    }
  }

  /**
   * Player class
   *
   *
   * @author Your Name
   * @version
   */
  public class Player {
    String _password;
    String _name;
    public String _tid;
    public int _pos = -99;
    Command _out;
    SocketChannel _channel;
    String _session = null;
    ByteBuffer _h, _b, _o;
    public int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
    String _comstr;
    protected boolean _dead;
    protected long _last_read_time;
    protected long _last_write_time;
    double _play_chips = 0, _real_chips = 0;

    public Player(String name, String pass) throws Exception {
      Configuration _conf = Configuration.getInstance();
      String _server = (String) _conf.get("Network.server.ip");
      int _port = _conf.getInt("Network.port");
      _channel = SocketChannel.open(new InetSocketAddress(_server, _port));
      _channel.configureBlocking(true);
      _session = connect();
      _name = name;
      _password = pass;
    }

    public String connect() throws Exception {
      //System.out.println("Connecting ....");
      Command ge = new Command("null", Command.C_CONNECT);
      _cat.finest("Connect req " + ge.toString());
      write(ge.toString());
      String s = readFull();
      _cat.finest("Connect resp " + s);
      Response r = new Response(s);
      return r.session();
    }

    public String reconnect() throws Exception {
      //System.out.println("ReConnecting ....");
      Command ge = new Command(_session, Command.C_CONNECT);
      _cat.finest("Connect req " + ge.toString());
      write(ge.toString());
      String s = readFull();
      _cat.finest("Connect resp " + s);
      Response r = new Response(s);
      return r.session();
    }

    public String readFull() {
      //_cat.finest("Read full");
      String s = read();
      while (s == null) {
        s = read();
        try {
          Thread.currentThread().sleep(200);
        }
        catch (Exception e) {
          //ignore
        }
      }
      return s;
    }

    public synchronized String read() {
      try {
        if (_dead) {
          return null;
        }
        _last_read_time = System.currentTimeMillis();
        if (readHeader() && readBody()) {
          return _comstr;
        }
      }
      catch (IOException e) {
        _cat.warning(_name + " Marking client as dead ");
        _dead = true;
        return null;
      }
      catch (Exception e) {
        _cat.warning(_name + " Garbled command ");
        _dead = true;
        return null;
      }
      return null;
    }

    public boolean readHeader() throws IOException {
      if (_ctype.equals("FLASH")) {
        return true;
      }
      else if (_ctype.equals("HTTP")) {
        int r = 0;
        if (_rlen != -1) {
          return true;
        }
        if (_h == null) {
          _h = ByteBuffer.allocate(HTTP_HEADER_SIZE);
          _h.clear();
        }
        r = _channel.read(_h);
        if (_h.hasRemaining()) {
          _cat.finest(_name + "  Partial header read " + r);
          if (r == -1) {
            _dead = true;
            _cat.warning(_name +
                       " Marking the client dead as the channel is closed  ");
          }
          return false;
        }
        _h.flip();
        for (int i = 0; i < HTTP_HEADER_SIZE; i++) {
          byte b = _h.get();
          _cat.finest(Byte.toString(b));
        }
        _rlen = HTTP_HEADER_SIZE;
        _h = null;
        return true;
      }
      else {
        int r = 0;
        if (_rlen != -1) {
          return true;
        }
        if (_h == null) {
          _h = ByteBuffer.allocate(8);
          _h.clear();
        }
        r = _channel.read(_h);
        if (_h.hasRemaining()) {
          _cat.finest(_name + "  Partial header read " + r);
          if (r == -1) {
            _dead = true;
            _cat.warning(_name +
                       " Marking the client dead as the channel is closed  ");
          }
          return false;
        }
        _h.flip();
        _rseq = _h.getInt();
        _rlen = _h.getInt();
        _h = null;
        //_cat.finest(" Len = " + _rlen);
        return true;
      }
    }

    public boolean readBody() throws IOException {
      int r = 0;
      if (_ctype.equals("FLASH")) {
        _b = ByteBuffer.allocate(1);
        _b.clear();
        byte[] buf = new byte[1024];
        int i = 0;
        r = 0;
        while ( (r = _channel.read(_b)) != -1) {
          if ( (buf[i] = _b.array()[0]) == (byte) 0) {
            break;
          }
          i++;
          _b.clear();
        }
        if (r == -1) {
          _dead = true;
        }
        if (i > 0) {
          _comstr = new String(buf, 0, i - 2, "UTF-8");
        }
        resetRead();
      }
      else if (_ctype.equals("HTTP")) {
        _b = ByteBuffer.allocate(1);
        _b.clear();
        byte[] buf = new byte[1024];
        int i = 0;
        r = 0;
        StringBuilder temp_buf = new StringBuilder();
        while ( (r = _channel.read(_b)) != -1) {
          if ( (buf[i] = _b.array()[0]) == (byte) 0) {
            break;
          }
          if (i == 1023) {
            temp_buf.append(new String(buf, 0, i, "UTF-8"));
            i = 0;
          }
          else {
            i++;
          }
          _b.clear();
        }
        if (r == -1) {
          _dead = true;
        }
        if (i > 0) {
          _comstr = temp_buf.append(new String(buf, 0, i, "UTF-8")).toString();
        }
        else {
          _comstr = temp_buf.toString();
        }
        resetRead();
      }
      else {
        if (_b == null) {
          _b = ByteBuffer.allocate(_rlen);
          _b.clear();
        }
        r = _channel.read(_b);
        if (_b.hasRemaining()) {
          if (r == -1) {
            _dead = true;
          }
          return false;
        }
        _b.flip(); // read complete
        _comstr = new String(_b.array());
        resetRead();
      }
      return true;
    }

    private void resetRead() {
      _h = null;
      _b = null;
      _rlen = -1;
      _rseq = -1;
    }

    public synchronized boolean write(String str) {
      //_cat.finest("In write");
      try {
        if (_ctype.equals("FLASH")) {
          _o = ByteBuffer.allocate(str.length() + TERMINATOR_SIZE);
          _o.put(str.getBytes());
          _o.put(TERMINATOR);
        }
        else if (_ctype.equals("HTTP")) {
          _o = ByteBuffer.allocate(HTTP_HEADER_SIZE + str.length() + 1);
          _o.put(HTTP_HEADER);
          _o.put(str.getBytes());
          _o.put((byte)0);
        }
        else {
          _o = ByteBuffer.allocate(8 + str.length());
          _o.putInt(_wseq++);
          _o.putInt(str.length());
          _o.put(str.getBytes());
        }
        _o.flip();
        int l = _channel.write(_o);
        while (_o.remaining() != 0) {
          _channel.write(_o);
        }
        //_cat.finest("Written = " + str);
        _o = null;
        _dead = false;
      }
      catch (IOException e) {
        _dead = true;
        _cat.warning(_name +
                  " Marking client as dead because of IOException during write");
        e.printStackTrace();
        return false;
      }
      return true;
    }

    public Response login() throws Exception {
      _channel.configureBlocking(true);
      CommandLogin ge = new CommandLogin(_session, _name, _password, "admin",
                                         "807");
      _cat.finest(_name + " Login Command " + ge);
      write(ge.toString());
      _last_write_time = System.currentTimeMillis();
      ResponseFactory rf = new ResponseFactory(readFull());
      Response gr = (Response) rf.getResponse();
      if (gr.getResult() == 1) {
        _play_chips = ( (ResponseLogin) gr).getPlayWorth();
        _real_chips = ( (ResponseLogin) gr).getRealWorth();
      }
      _cat.finest(_name + " Login Response " + gr);
      return gr;
    }

    public void logout() throws Exception {
      Command ge = new Command(_session, Command.C_LOGOUT);
      _cat.finest(_name + " Logout Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }

    public void turnDeaf() throws Exception {
      Command ge = new Command(_session, Command.C_TURN_DEAF);
      _cat.finest(_name + " TURN DEAF " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }

    public Response config() throws Exception {
      _channel.configureBlocking(true);
      Command ge = new Command(_session, Command.C_CONFIG);
      _cat.finest(_name + " Config Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(readFull());
      ResponseConfig gr = (ResponseConfig) rf.getResponse();
      _cat.finest(_name + " Config Response " + gr);
      return gr;
    }

    public Response register() throws Exception {
      _channel.configureBlocking(true);
      CommandRegister ge = new CommandRegister(_session, _name, _password,
                                               _name + "@test.com", (byte) 0,
                                               "", "");
      _cat.finest(_name + " Register Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      Response gr = new Response(readFull());
      _cat.finest(_name + " Register Response " + gr);
      return gr;
    }

    public Response addObserver(String tid) throws Exception {
      _channel.configureBlocking(true);
      CommandTableDetail ge = new CommandTableDetail(_session, tid);
      _cat.finest(_name + " Add Observer Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      Response gr = new Response(readFull());
      _cat.finest(_name + " Add Observer Response " + gr);
      return gr;
    }

    public void join(String tid, int pos) throws Exception {
      _tid = tid;
      _pos = pos;
      CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, 0, tid);
      ge.setPlayerPosition(pos);
      _cat.finest(_name + " Join Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }

    public Response buyChips() throws Exception {
      CommandBuyChips ge;
      ge = new CommandBuyChips(_session, 2100, 2100);
      _cat.finest(_name + " Buy Chips  Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      Response gr = new Response(readFull());
      _cat.finest(_name + " Buy Chips Response " + gr);
      return gr;
    }

    public void move(String mov, double amt) throws Exception {
      int mov_id = -99;
      if (mov.equals("join")) {
        mov_id = Command.M_JOIN;
      }
      else if (mov.equals("open")) {
        mov_id = Command.M_OPEN;
      }
      else if (mov.equals("check")) {
        mov_id = Command.M_CHECK;
      }
      else if (mov.equals("call")) {
        mov_id = Command.M_CALL;
      }
      else if (mov.equals("raise")) {
        mov_id = Command.M_RAISE;
      }
      else if (mov.equals("fold")) {
        mov_id = Command.M_FOLD;
      }
      else if (mov.equals("draw-cards")) {
        mov_id = Command.M_PICK;
      }
      else if (mov.equals("drop-cards")) {
        mov_id = Command.M_DUMP;
      }
      else if (mov.equals("join")) {
        mov_id = Command.M_JOIN;
      }
      else if (mov.equals("leave")) {
        mov_id = Command.M_LEAVE;
      }
      else if (mov.equals("sit-in")) {
        mov_id = Command.M_SIT_IN;
      }
      else if (mov.equals("opt-out")) {
        mov_id = Command.M_OPT_OUT;
      }
      else if (mov.equals("bet-pot")) {
        mov_id = Command.M_BET_POT;
      }
      else if (mov.equals("wait")) {
        mov_id = Command.M_WAIT;
        return;
      }
      else if (mov.equals("small-blind")) {
        mov_id = Command.M_SMALLBLIND;
      }
      else if (mov.equals("big-blind")) {
        mov_id = Command.M_BIGBLIND;
      }
      else if (mov.equals("bet")) {
        mov_id = Command.M_BET;
      }
      else if (mov.equals("complete")) {
        mov_id = Command.M_COMPLETE;
      }
      else if (mov.equals("ante")) {
        mov_id = Command.M_ANTE;
      }
      else if (mov.equals("all-in")) {
        mov_id = Command.M_ALL_IN;
      }
      else if (mov.equals("bringin")) {
        mov_id = Command.M_BRING_IN;
      }
      else if (mov.equals("bringinh")) {
        mov_id = Command.M_BRING_IN_HIGH;
      }
      else if (mov.equals("sbbb")) {
        mov_id = Command.M_SBBB;
      }
      /**
             else if (mov.equals("play-seen")) {
       mov_id = Command.M_PLAY_SEEN;
             }
             else if (mov.equals("open-one")) {
       mov_id = Command.M_OPEN_ONE;
             }
             else if (mov.equals("open-two")) {
       mov_id = Command.M_OPEN_TWO;
             }
             else if (mov.equals("open-three")) {
       mov_id = Command.M_OPEN_THREE;
             }
             else if (mov.equals("dd")) {
       mov_id = Command.M_DD;
             }
             else if (mov.equals("ping")) {
       mov_id = Command.M_PING;
             }
             else if (mov.equals("half")) {
       mov_id = Command.M_HALF;
             }
             else if (mov.equals("full")) {
       mov_id = Command.M_FULL;
             }**/

      else {
        mov_id = Command.M_ILLEGAL;
      }
      if (mov_id != Command.M_ILLEGAL) {
        _cat.info(this +"  Making a move = " + mov + ", id = " + mov_id +
                  ", amt = " + amt + ", tid = " + _tid);
        CommandMove cm = new CommandMove(_session, mov_id, amt, _tid);
        cm.setPlayerPosition(_pos);
        _last_write_time = System.currentTimeMillis();
        write(cm.toString());
      }
      else {
        _cat.log(Level.WARNING, "ILLEGAL MOVE");
      }
    }

    public void processMove(ResponseGameEvent rge) throws Exception {
      GameEvent ge = new GameEvent();
      ge.init(rge.getGameEvent());
      _cat.finest("RECEIVED " + this +"\n GE=" + ge);
      /**
       * check if the last move was join, if yes update the position and gid
       */
      String[] last_move = ge.getLastMoveString().split("\\|");
      if (last_move.length > 3 && last_move[2].equals("leave") &&
          last_move[1].equals(_name)) {
        _pos = -1;
        _tid = null;
        _cat.info("Left table " + this +ge);
        return;
      }
      if (last_move.length > 3 && last_move[2].equals("join") &&
          last_move[1].equals(_name)) {
        _pos = Integer.parseInt(last_move[0]);
        _tid = ge.getGameName();
        _cat.info("Changed position " + this +ge);
      }

      int target_pos = Integer.parseInt(ge.get("target-position") != null ?
                                        ge.get("target-position") : "-22");
      if (target_pos != _pos) {
        return;
      }

      //moves
      String moves[][] = ge.getMove();
      if (moves != null && moves.length > 0) {
        // take the first move
        int pos = Integer.parseInt(moves[0][0]);
        if (pos != -1) {
          if (pos == _pos) {
            _tid = ge.getGameName();
            // calculate the hand strength
            //int strength = ge.getHandStrength();
            //_cat.finest("Hand strength for " + _name + " is " + strength);
            int random_move = _rng.nextIntLessThan(100);
            int mi = 0;
            if (moves.length <= 2) { // No opt out
              mi = 0;
            }
            else if (moves.length == 3) {
              if (random_move < 40) {
                mi = 0;
              }
              else if (random_move < 80) {
                mi = 1;
              }
              else {
                if (moves[2][1].equals("play-seen")) {
                  mi = 2;
                }
                else {
                  mi = 0;
                }
              }
            }
            else if (moves.length == 4) { // All-in in NL/PL games
              if (random_move < 50) {
                mi = 0;
              }
              else if (random_move < 80) {
                mi = 1;
              }
              else {
                if (moves[3][1].equals("play-seen")) {
                  mi = 3;
                }
                else {
                  mi = 1;
                }
              }

            }
            else if (moves.length == 5) { // All-in in NL/PL games
              if (random_move < 40) {
                mi = 0;
              }
              else if (random_move < 40) {
                mi = 1;
              }
              else if (random_move < 60) {
                mi = 2;
              }
              else if (random_move < 80) {
                mi = 3;
              }
              else {
                mi = 4;
              }
            }
            else {
              _cat.log(Level.WARNING, "Wrong count of moves" + moves.length);
            }

            String mov = moves[mi][1];
            String amt_str = moves[mi][2];
            double amt = -1;
            _cat.finest(" Move by " + _name + " is " + mov + ", " + amt_str +
                       ", game=" + _tid + ", position=" + _pos + " ," + ge);
            if (amt_str != null && amt_str.length() > 0) {
              int index = amt_str.indexOf("-");
              if (index == -1) {
                amt = Double.parseDouble(amt_str);
              }
              else if (index == 0) {
                _cat.log(Level.WARNING, "Illegal move amount Move by " + _name + " is " +
                           mov + ", " + amt_str +
                           ", game=" + _tid + ", position=" + _pos + " ," +
                           ge);
              }
              else {
                _cat.finest("Range's first = " + amt_str.substring(0, index));
                amt = Double.parseDouble(amt_str.substring(0, index));
              }
            }
            move(mov, amt);
            Thread.currentThread().sleep(DELAY_MS);
          }
        }
        else if (moves[0][1].equals("wait")) {
          _cat.info("Wait received");
        }
        else {
          _cat.log(Level.WARNING, "Illegal position received from the server");
        }
      }
    }

    public void setSelectorForRead() throws Exception {
      _channel.configureBlocking(false);
      _channel.register(_selector, SelectionKey.OP_READ, this);
    }

    public ResponseTableList getTableList() throws Exception {
      CommandTableList ge = new CommandTableList(_session, 0xF0000);
      _cat.finest("TableList Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(readFull());
      ResponseTableList gr = (ResponseTableList) rf.getResponse();
      _cat.info(_name + " TableList Response " + gr.getGameCount());
      return gr;
    }

    public void heartBeat() throws Exception {
      if (_session != null) {
        Command ge = new Command(_session, Command.C_HTBT);
        write(ge.toString());
      }
    }

    public String toString() {
      StringBuilder sb = new StringBuilder("Player=[Name=");
      sb.append(_name);
      sb.append(", Session=");
      sb.append(_session);
      sb.append(", Game=");
      sb.append(_tid);
      sb.append(", Position=");
      sb.append(_pos);
      sb.append(", Worth=");
      sb.append(_play_chips);
      return sb.toString();
    }

  } // end class Player

  public class HeartBeat
      extends TimerTask {

    public void run() {
      try {
        Vector to_remove = new Vector();
        // send a heartbeat message
        Enumeration e = _players.elements();
        for (; e.hasMoreElements(); ) {
          Player p = (Player) e.nextElement();
          if (!p._dead) {
            p.heartBeat();
          }
          else {
            //remove the player
            to_remove.add(p);
          }
          long currTime = System.currentTimeMillis();
          if (currTime - p._last_read_time > 400000 &&
              currTime - p._last_write_time > 400000) {
            to_remove.add(p);
          }
        }
        e = to_remove.elements();
        for (; e.hasMoreElements(); ) {
          Player p = (Player) e.nextElement();
          _cat.info("Disconnecting idle player " + p);
          p.logout();
          //remove the player
          _players.remove(p);
        }
      }
      catch (Exception ex) {
        //do nothing
        _cat.log(Level.WARNING, "Exception", ex);
      }
    }
  } // end HeartBeat class

  public static void main(String[] args) throws Exception {
    SitnGoShills shill = new SitnGoShills();
    shill.startShills();
  }

  private static String[] player_names = {
      "larry", "john", "bestgirl", "coolcat", "hamilton", "bobby",
      "harry", "tommy", "johhney", "sweety", "sasha", "wildbill", "bill",
      "bushy", "ruth", "Scott", "tiger", "roger", "Herd",
      "amanda", "loverboy", "dick", "alice", "shaw", "blackie", "carol",
      "lady", "Mavarick", "puller", "cat", "sandra", "doug", "denise",
      "oliver",
      "amy", "spiderman", "barbie", "shaina", "lewis", "headly",
      "batgirl", "danny", "fanny", "clinton", "james", "lovergirl",
      "taxidriver", "pilot", "margarita", "deadly", "inafix", "maria",
      "cool", "tom", "bush", "olli", "polli", "peter",
      "cinderella", "tony", "pony", "rolly", "jellyfish", "coolcow"
  };
  private static String[] player_sep = {
      "V", "I", "U", "O"
  };
  private static String[] player_ext = {
      "us", "cool", "Z", "uno", "00",
      "007", "VV", "mix", "hi", "rrr"
  };

  public boolean seatVacant(TableMap tm) {
    for (int i = 1; i < tm._maxPlayer; i++) {
      if (tm._playerDetails[i] != -1) {
        return true;
      }
    }
    return false;
  }

  public int tableCount() throws Exception {
    ResponseTableList rtl = null;
    rtl = _dummy.getTableList();
    return rtl.getGameCount();
  }

  private boolean addPlayer(String name, String password, String gid, int pos) throws
      Exception {
    Player p = new Player(name, password);
    Response r = p.login();
    if (r.getResult() == 2 || r.getResult() == 29) {
      // try to register the player
      r = p.register();
      if (r.getResult() == 0) {
        _cat.log(Level.WARNING, "Registration failed " + p);
      }
    }
    else if (r.getResult() != 1) {
      return false;
    }
    _players.add(p);
    r = p.addObserver(gid);
    //buy chips
    p.setSelectorForRead();
    p.join(gid, pos);
    return true;
  }

  private void consoleLoop() {
    System.out.print(
        "GameController application.  Type a command the the '>' prompt,\n" +
        "'quit' to end the server, or 'help' to get a list of commands.\n");
    // Drop into the loop.

     BufferedReader dis
           = new BufferedReader(new InputStreamReader(System.in));
    boolean ok = true;
    while (ok) {
      try {
        String s;
        System.out.print("> ");
        System.out.flush();
        s = dis.readLine();
        if (s.equals("quit")) {
          ok = false;
        }
        else if (s.startsWith("add")) {

        }
        else if (s.startsWith("remove")) {

        }
        else if (s.startsWith("list")) {

        }
        else if (s.length() > 0) {
          System.out.print("Help for GameServer Controller Commands:\n\n" +
                           "add name passord <game-id> <position> - add a shill on specified game\n" +
                           "remove name|<game-id>|all - remove shill from specified game\n" +
                           "list <game-id>|all - list shills playing on a particular table\n" +
                           "quit    - Exits the server killing all games\n");
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


          public TableMap[] tableMap() throws Exception {
            ResponseTableList rtl = null;
            rtl = _dummy.getTableList();
            //p.config();
            // revise the table count
            int tc = rtl.getGameCount();
            TableMap[] tm = new TableMap[tc];
            for (int i = 0; i < tc; i++) {
            	GameEvent ge = rtl.getGameEvent(i);
              	_cat.finest("Game=" + ge);
                if ( (ge.getType() & 0xF0000) == 0  ) {
                  continue;
                }
                tm[i] = new TableMap(ge);
             // System.out.println(tm[i]);
            }
            return tm;
          }
          
        
          public class TableMap {
              public String _gname;
              public int _maxPlayer, _minPlayer;
              public double _buyin;
              public int[] _playerDetails;
              
              
              public TableMap(GameEvent ge){
                    _cat.finest("Table map ge " + ge.toString());
                    _gname = ge.getGameName();
                    _cat.finest("Gname=" + _gname);
                    _maxPlayer = ge.getMaxPlayers();
                    _cat.finest("max player=" + _maxPlayer);
                    _playerDetails =  new int[_maxPlayer];
                    _buyin = (int)Double.parseDouble((String)ge.get("buyin"));
                    String pd[][] = ge.getPlayerDetails();
                    if (pd != null) {
                      for (int j = 0; j < pd.length; j++) {
                        int pp = Integer.parseInt(pd[j][0]);
                        _cat.finest(pd[j][1] + "==" + pd[j][0]);
                        _playerDetails[pp] = -1; // set it to occupied
                      }
                    }
              }
              
                         
             public String toString(){
                 return _gname + ", " + _maxPlayer + ", " + _buyin;
             }

          }
          
        

    }
