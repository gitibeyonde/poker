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
import com.poker.common.message.CommandTournyDetail;
import com.poker.common.message.CommandTournyMyTable;
import com.poker.common.message.CommandTournyRegister;
import com.poker.common.message.ResponseConfig;
import com.poker.common.message.ResponseFactory;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponsePing;
import com.poker.common.message.ResponseTableList;
import com.poker.common.message.ResponseTournyDetail;
import com.poker.common.message.ResponseTournyList;
import com.poker.common.message.ResponseTournyMyTable;
import com.poker.common.message.TournyEvent;

import java.io.IOException;

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


//import com.agneya.nio.*;
//import com.poker.game.poker.*;

/**
 * Shill client
 *
 */

public class Tourners
    implements Runnable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Tourners.class.getName());

  Vector _players;
  ResponseTableList _rtd;
  Selector _selector;
  int _pcount;
  Rng _rng;
  String _server;
  Response _responseStack[];
  int _htbt;
  boolean _inProgress;
  int DELAY_MS = 0;

  Configuration _conf = null;
  /**
   * runner listens to responses from the poker server
   */
  private Thread _runner = null;
  boolean _keepListening;

  public static final int NOEXIST = 0;
  public static final int CREATED = 1;
  public static final int DECL = 2;
  public static final int REG = 4;
  public static final int JOIN = 8;
  public static final int START = 16;
  public static final int RUNNING = 32;
  public static final int END = 64;
  public static final int FINISH = 128;
  public static final int CLEAR = 256;

  public Tourners() {
    _players = new Vector();
    //initialize the RNG
    _rng = new Rng();
  }

  public void startShills() throws Exception {
    //initialize the vector with player data
    // random for now
    _inProgress = false;
    _conf = Configuration.getInstance();
    _pcount = Integer.parseInt( (String) _conf.get("TournyPlayer.Count"));
    DELAY_MS = Integer.parseInt( (String) _conf.get("TournyPlayer.Delay"));
    _server = (String) _conf.get("Network.server.ip");
    _htbt = _conf.getInt("Server.maintenance.heartbeat");
    long last_registered_time = System.currentTimeMillis();
    _selector = Selector.open();
    Player dummy = new Player("dummy", "dummy");
    _players.add(dummy);
    ResponseTournyList rtl = dummy.getTournyList();
    int tc = rtl.getTournyCount();
    TournyEvent[] tourny = new TournyEvent[tc];
    for (int i = 0; i < tc; i++) {
      tourny[i] = rtl.getTourny(i);
      _cat.info(tourny[i].toString());
    }

    // start a heartbeat timer thread
    HeartBeat hb = new HeartBeat();
    Timer t = new Timer();
    t.schedule(hb, 0, _htbt);

    /**
     * DECLARATION AND INFO
     */
    // get the details for the first tournament
    ResponseTournyDetail rtd = dummy.getTournyDetail(tourny[0].name());
    TournyEvent te = new TournyEvent(rtd.getTournyEvent());

    _cat.info(te.toString());

   

    while (te.state() < REG) {
      Thread.currentThread().sleep(10000);
      rtd = dummy.getTournyDetail(tourny[0].name());
      te = new TournyEvent(rtd.getTournyEvent());
    }
    /**
     * REGISTRATION
     */
    _cat.finest("-----REGISTRATION OPEN " + te);

    int pn_count = player_names.length * player_sep.length * player_ext.length;
    if (_pcount > pn_count) {
      _cat.warning(_pcount +
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

    Player p;
    for (_pcount--; _pcount >= 0; _pcount--) {
      p = new Player(pn[_pcount], pn[_pcount]);
      // get the tablelist from the server
      try {
        Response r = p.login();
        if (r.getResult() == 29) {
          // try to register the player
          r = p.register();
          if (r.getResult() == 0) {
            throw new IllegalStateException("Registration failed");
          }
        }
        _players.add(p);
      }
      catch (Exception ex) {
        _cat.log(Level.WARNING, "FATAL " + ex.getMessage() + " For player " + p._name);
        //remove this player from list
        //p.kill();  TODO
      }
    }
    _cat.warning(_players.size() + "  Players connected and logged in time = " +
              (System.currentTimeMillis() - last_registered_time));
    // register
    Enumeration pen = _players.elements();
    while (pen.hasMoreElements()) {
      Response r = ( (Player) pen.nextElement()).registerTourny(tourny[0].name());
    }

    while (te.state() <= REG) {
      Thread.currentThread().sleep(20000);
      rtd = dummy.getTournyDetail(tourny[0].name());
      te = new TournyEvent(rtd.getTournyEvent());
    }

    /**
     *  JOINING
     */
    _cat.info("----------JOINING");

    // seat players
    String games = rtd.getTournyEvent();
    GameEvent ge = new GameEvent();

    // Start the nio thread
    pen = _players.elements();
    while (pen.hasMoreElements()) {
      ( (Player) pen.nextElement()).setSelectorForRead();
    }

    last_registered_time = System.currentTimeMillis();
    _runner = new Thread(this);
    _runner.start();

    Thread.currentThread().sleep(10000);


      _cat.info(games);
      ge.init(games);
      String[] regp = ge.get("player").split("\\|");
      for (int j = 0; j < regp.length; j++) {
        Player rp = getPlayer(regp[j].split("`")[0]);
        if (rp == null) {
          continue;
        }
        rp.goToMyTable(tourny[0].name());
        Thread.currentThread().sleep(100);
      }

    _cat.info("---------THREAD STARTED");
  }

  public Player getPlayer(String name) {
    // register
    Enumeration pen = _players.elements();
    while (pen.hasMoreElements()) {
      Player p = (Player) pen.nextElement();
      if (p._name.equals(name)) {
        return p;
      }
    }
    return null;
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
            ResponseFactory rf = new ResponseFactory(p.read());
            Response r = rf.getResponse();
            //_cat.finest(r);
            while (r != null) {
              if (r._response_name == Response.R_MOVE) {
                p.processMove( (ResponseGameEvent) r);
              }
              else if (r._response_name == Response.R_MESSAGE) {
                ResponseMessage rm = (ResponseMessage) r;
                _cat.info("Message =" + rm.getMessage());
              }
              else if (r._response_name == Response.R_LOGOUT) {
                _players.remove(p);
                _cat.info("Removed " + p);
              }
              else if (r._response_name == Response.R_TABLE_CLOSED) {
                _cat.info("CLOSING TABLE");
              }
              else if (r._response_name == Response.R_TABLE_OPEN) {
                _cat.info("OPENING TABLE");
              }
              else if (r._response_name == Response.R_TABLEDETAIL) {
                _cat.info("TABLE DETAILS");
              }
              else if (r._response_name == Response.R_TOURNYDETAIL) {
                _cat.info("DETAILS");
              }
              else if (r._response_name == Response.R_TOURNYMYTABLE) {
                _cat.finest(p._name + " GoToTable Response " + r);
                if (r.getResult() == 1) {
                  ResponseTournyMyTable gr = (ResponseTournyMyTable) r;
                  p._tid = gr.getGameTid();
                  p._pos = gr.getPosition();
                }
              }
              else {
                _cat.log(Level.WARNING, 
                    "SHILL: FATAL: Unknown event received from the server " + r);
              }
              rf = new ResponseFactory(p.read());
              r = rf.getResponse();
            }
          }
        }
        Thread.currentThread().sleep(10);
      }
      catch (IOException ex) {
        _cat.log(Level.WARNING, " Removing player " + p);
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
    int _seq = 0;
    String _session = null;

    public Player(String name, String pass) throws Exception {
      Configuration _conf = Configuration.getInstance();
      String _server = (String) _conf.get("Network.server.ip");
      int _port =  _conf.getInt("Network.port");
      _channel = SocketChannel.open(new InetSocketAddress(_server, _port));
      _channel.configureBlocking(true);
      String session = connect(_channel);
      _name = name;
      _password = pass;
      Command ge = new Command("null", Command.C_CONNECT);
      _cat.finest("Player connected req = " + ge.toString());
      write(ge.toString());
      Response gr = new Response(read());
      _cat.finest("Player connected resp = " + gr);
      _session = gr.session();
    }

    public String connect(SocketChannel _channel) throws Exception {
      //System.out.println("Connecting ....");
      Command ge = new Command("null", Command.C_CONNECT);
      _cat.finest("Connect req " + ge.toString());
      write(ge.toString());
      String s = read();
      _cat.finest("Connect resp " + s);
      Response r = new Response(s);
      return r.session();
    }

    public synchronized void write(String str) throws Exception {
      ByteBuffer header = ByteBuffer.allocate(8);
      header.putInt(_seq++);
      header.putInt(str.length());
      header.flip();
      _channel.write(header);
      _channel.write(ByteBuffer.wrap(str.getBytes()));
      //_cat.finest("Write " + str);
    }

    public String read() throws IOException {
      String com = null;
      ByteBuffer h = ByteBuffer.allocate(8);
      h.clear();
      int r = _channel.read(h);
      if (r == 0) {
        return null;
      }
      while (h.hasRemaining()) {
        _channel.read(h);
        try {
          Thread.currentThread().sleep(1);
        }
        catch (InterruptedException e) {
          //ignore
        }
      }
      h.flip();
      int _seq = h.getInt();
      int _len = h.getInt();
      ByteBuffer b = ByteBuffer.allocate(_len);
      b.clear();
      int l = _channel.read(b);
      while (b.hasRemaining()) {
        _cat.warning("recvd partial = " + l);
        _channel.read(b);
        try {
          Thread.currentThread().sleep(1);
        }
        catch (InterruptedException e) {
          //ignore
        }
      }
      // read complete
      b.flip();
      com = new String(b.array());
      // queue the command for processing
      //_cat.finest("Read " + com);
      return com;
    }

    public Response login() throws Exception {
      _channel.configureBlocking(true);
      CommandLogin ge = new CommandLogin(_session, _name, _password);
      _cat.finest(_name + " Login Command " + ge);
      write(ge.toString());
      Response gr = new Response(read());
      _cat.finest(_name + " Login Response " + gr);
      return gr;
    }

    public Response logout() throws Exception {
      _channel.configureBlocking(true);
      Command ge = new Command(_session, Command.C_LOGOUT);
      _cat.finest(_name + " Logout Command " + ge);
      write(ge.toString());
      Response gr = new Response(read());
      _cat.finest(_name + " Logout Response " + gr);
      return gr;
    }

    public Response config() throws Exception {
      _channel.configureBlocking(true);
      Command ge = new Command(_session, Command.C_CONFIG);
      _cat.finest(_name + " Config Command " + ge);
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(read());
      ResponseConfig gr = (ResponseConfig) rf.getResponse();
      _cat.finest(_name + " Config Response " + gr);
      return gr;
    }

    public Response registerTourny(String tid) throws Exception {
      _channel.configureBlocking(true);
      CommandTournyRegister ge = new CommandTournyRegister(_session, tid, _name);
      _cat.finest(_name + " Tourny Register Command " + ge);
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(read());
      Response gr = (Response) rf.getResponse();
      _cat.finest(_name + " Tourny Register Response " + gr);
      return gr;
    }

    public Response register() throws Exception {
      _channel.configureBlocking(true);
      CommandRegister ge = new CommandRegister(_session, _name, _password,
                                               _name + "@test.com", (byte) 1,
                                               "", "");
      _cat.finest(_name + " Register Command " + ge);
      write(ge.toString());
      Response gr = new Response(read());
      _cat.finest(_name + " Register Response " + gr);
      return gr;
    }

    public Response addObserver(String tid) throws Exception {
      _channel.configureBlocking(true);
      CommandTableDetail ge = new CommandTableDetail(_session, tid);
      _cat.finest(_name + " Add Observer Command " + ge);
      write(ge.toString());
      Response gr = new Response(read());
      _cat.finest(_name + " Add Observer Response " + gr);
      return gr;
    }

    public void goToMyTable(String tid) throws Exception {
      CommandTournyMyTable ge = new CommandTournyMyTable(_session, tid);
      _cat.finest(_name + " GoToTable Command " + ge);
      write(ge.toString());
    }

    public void join(String tid, int pos) throws Exception {
      _channel.configureBlocking(true);
      _tid = tid;
      _pos = pos;
      CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, 0, tid);
      ge.setPlayerPosition(pos);
      _cat.finest(_name + " Join Command " + ge);
      write(ge.toString());
    }

    public void processMove(ResponseGameEvent rge) throws Exception {
      GameEvent ge = new GameEvent();
      ge.init(rge.getGameEvent());
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
      if (last_move.length > 3 && last_move[2].equals("move") &&
          last_move[1].equals(_name)) {
        _pos = Integer.parseInt(last_move[0]);
        _tid = ge.getGameName();
        _cat.info("Moved to a different table " + this +ge);
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
            int mi;
            if (moves.length == 2) {
              if (random_move < 40) {
                mi = 0;
              }
              else {
                mi = 1;
              }
            }
            else if (moves.length == 3) {
              if (random_move < 70) {
                mi = 0;
              }
              else if (random_move < 97) {
                mi = 1;
              }
              else {
                mi = 2;
              }
            }
            else if (moves.length == 4) {
              if (random_move < 60) {
                mi = 0;
              }
              else if (random_move < 85) {
                mi = 1;
              }
              else if (random_move < 86) {
                mi = 2;
              }
              else {
                mi = 3;
              }
            }
            else {
              mi = 0;
            }

            String mov = moves[mi][1];
            String amt_str = moves[mi][2];
            double amt = -1;
            if (amt_str != null && amt_str.length() > 0) {
              int index = amt_str.indexOf("-");
              if (index == -1 || index == 0) {
                amt = Double.parseDouble(amt_str);
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
          //_cat.finest("Wait received");
        }
        else {
          _cat.log(Level.WARNING, "Illegal position received from the server");
        }
      }

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
      else {
        mov_id = Command.M_ILLEGAL;
      }
      if (mov_id != Command.M_ILLEGAL) {
        _cat.finest(this._name + "  Making a move = " + mov + ", id = " + mov_id +
                   ", amt = " + amt + ", tid = " + _tid);
        CommandMove cm = new CommandMove(_session, mov_id, amt, _tid);
        cm.setPlayerPosition(_pos);
        write(cm.toString());
      }
      else {
        _cat.log(Level.WARNING, "ILLEGAL MOVE");
      }
    }

    public void setSelectorForRead() throws Exception {
      _channel.configureBlocking(false);
      _channel.register(_selector, SelectionKey.OP_READ, this);
    }

    public ResponseTournyList getTournyList() throws Exception {
      _channel.configureBlocking(true);
      Command ge = new Command(_session, Command.C_TOURNYLIST);
      //_cat.finest("TournyList Command " + ge);
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(read());
      ResponseTournyList gr = (ResponseTournyList) rf.getResponse();
      //_cat.info(_name + " TournyList Response " + gr);
      return gr;
    }

    public ResponseTournyDetail getTournyDetail(String name) throws Exception {
      _channel.configureBlocking(true);
      CommandTournyDetail ge = new CommandTournyDetail(_session, name);
      _cat.finest("Tourny Detail Command " + ge);
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(read());
      _cat.finest(_name + " Tourny Detail Response " + rf.getResponse());
      ResponseTournyDetail gr = null;
      try {
        gr = (ResponseTournyDetail) rf.getResponse();
      }
      catch (Exception e) {

      }
      //_cat.finest(_name + " Tourny Detail Response " + gr);
      return gr;
    }

    public Response buyChips() throws Exception {
      CommandBuyChips ge = new CommandBuyChips(_session, 50, 0);
      _cat.finest(_name + " Buy Chips  Command " + ge);
      write(ge.toString());
      Response gr = new Response(read());
      _cat.finest(_name + " Buy Chips Response " + gr);
      return gr;
    }

    public void heartBeat() throws Exception {
      if (_session != null) {
        Command ge = new Command(_session, Command.C_HTBT);
        write(ge.toString());
      }
    }

    public void ping() throws Exception {
      if (_session != null) {
        Command ge = new Command(_session, Command.C_PING);
        write(ge.toString());
        ResponseFactory rf = new ResponseFactory(read());
        ResponsePing gr = (ResponsePing) rf.getResponse();
        _cat.finest(_name + " Ping " + gr);
      }
    }

    private boolean addPlayer(String name, String password, String gid, int pos) throws
        Exception {
      Player p = new Player(name, password);
      Response r = p.login();
      if (r.getResult() == 2) {
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
      p.buyChips();

      p.setSelectorForRead();
      p.join(gid, pos);
      return true;
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
      return sb.toString();
    }

  } // end class Player

  public class HeartBeat
      extends TimerTask {

    public void run() {
      Player p = null;
      // send a heartbeat message
      Enumeration e = _players.elements();
      for (; e.hasMoreElements(); ) {
        p = (Player) e.nextElement();

        try {
          p.heartBeat();
          //p.ping();
        }
        catch (Exception ex) {
          _cat.log(Level.WARNING, "Error in sending HTBT " + p);
          _players.remove(p);
        }

      }

    }

  } // end HeartBeat class

  public static void main(String[] args) throws Exception {
    Tourners shill = new Tourners();
    shill.startShills();

  }

  private static String[] player_names = {
      "sandy", "ranjini",
      "anilk", "lewis", "adam", "nazemi", "ben",
      "bob", "dylon", "asterix", "obelix", "dogmatix",
      "larry", "john", "bestgirl", "coolcat", "hamilton", "bobby",
      "harry", "tommy", "johhney", "sweety", "sasha", "wildbill", "bill",
      "bushy", "ruth", "Scott", "tiger", "roger",
      "amanda", "loverboy", "dick", "alice", "shaw", "blackie", "carol",
      "lady", "Mavarick", "puller", "cat", "sandra", "doug", "denise", "oliver",
      "amy", "spiderman", "barbie", "shaina", "headly",
      "batgirl", "danny", "fanny", "clinton", "james", "lovergirl",
      "taxidriver", "pilot", "margarita", "deadly", "inafix", "maria",
      "cool", "tom", "bush", "olli", "polli", "peter",
      "cinderella", "tony", "pony", "rolly", "jellyfish", "coolcow"
  };
  private static String[] player_sep = {
      "", " ", "-", "_"
  };
  private static String[] player_ext = {
      "1", "me", "uno", "00",
  };

}
