package com.poker.client;

import com.agneya.util.Configuration;

import com.agneya.util.Rng;

import com.golconda.game.util.Card;
import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;

import com.poker.common.message.CommandGetChipsIntoGame;
import com.poker.common.message.CommandInt;
import com.poker.common.message.CommandJoinPool;
import com.poker.common.message.CommandLogin;
import com.poker.common.message.CommandMove;
import com.poker.common.message.CommandRegister;
import com.poker.common.message.CommandString;
import com.poker.common.message.CommandTableDetail;
import com.poker.common.message.CommandTableList;
import com.poker.common.message.ResponseConfig;
import com.poker.common.message.ResponseFactory;
import com.poker.common.message.ResponseGameEvent;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Player extends Bot implements Runnable {

  transient static Logger log = Logger.getLogger(Player.class.getName());

  public static final int RAISE_COUNT = 1;
  public static final int CHECK_COUNT = 1;
  String _password;
  public String _tid;
  public int _grid = -99;
  Command _out;
  SocketChannel _channel = null;
  String _session = null;
  ByteBuffer _h, _b, _o;
  public int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
  String _comstr;
  protected boolean _dead;
  protected long _last_read_time;
  protected long _last_write_time;
  protected long _last_move_time;
  double _total_play_chips = 0;
  double _total_real_chips = 0;
  double _tableChips = 0;
  Vector _rge = new Vector();
  boolean _isPlrPropChanged;

  //player properties
  public int _iq;
  public int _gTypes; //game types the bot can play
  public int _pgTypes; //prefred game type....
  public int _nGames;
  public int _gender;
  public int _currGamesPlayed;
  public int _delay;
  public float _lossCeil;
  public float _winCeil;
  public int _currState;
  public int _idleCount;
  public int _currIdleCount;
  public int _sitoutCount;
  public int _currSitoutCount;
  protected long _leave_time;
  boolean _isLogout;
  boolean _moneyToTable;
  int _buyInReqAmt = 0;
  int _tmpJoinPos = -1;
  String _tmpTid;
  int _tmpTableType = 0;
  int _raiseCount = 0;
  int _checkCount = 0;
  int _tableNum = -1;
  
  static Rng _rng = new Rng();

  protected static String _ctype = "NORMAL";
  private final static byte[] TERMINATOR = {
      38, 84, 61, 90, 13, 10, 0};
  private final static int TERMINATOR_SIZE = 7;
  static int DELAY_MS = 3000; //x

  static {
    try {
      _ctype = (String) Configuration.getInstance().get("Client.Type");
      DELAY_MS = Configuration.getInstance().getInt("Shill.Delay");
    }
    catch (Exception e) {
      System.out.println("Clienttype missing in the conf");
    }
  }

  /**public Player(String name, String pass) throws Exception {
    _name = name;
    _password = pass;
    _currState = ShillConstants.PLR_DISCONNECTED;
    _idleCount = 0;
    _currIdleCount = 0;
    _currSitoutCount = 0;
    _currGamesPlayed = 0;
    _leave_time = System.currentTimeMillis();
    _last_read_time = System.currentTimeMillis();
    _last_write_time = System.currentTimeMillis();
    _last_move_time= System.currentTimeMillis();
    _isPlrPropChanged = false;
    _isLogout = false;
    _moneyToTable = false;
    _pgTypes = 0xFFFF;
    _isLogout = false;
  }**/

  public Player(String name) throws Exception {
    super(name);
    _password = name;
    _iq = _rng.nextIntBetween(1,5);
    _gTypes =0xFFFFFF;
    _pgTypes =0xFFFFF;
    _nGames = _rng.nextIntBetween(40, 100);
    _gender = 1;
    _currGamesPlayed = 0;
    _delay = 10; //in sec
    _lossCeil = 9999999;
    _winCeil = 9999999;
    _isPlrPropChanged = false;
    _currState = ShillConstants.PLR_DISCONNECTED;
    _idleCount = 8;
    _sitoutCount = 4;
    _currIdleCount = 9;
    _currSitoutCount = 0;
    _leave_time = System.currentTimeMillis();
    _last_read_time = System.currentTimeMillis();
    _last_write_time = System.currentTimeMillis();
    _last_move_time= System.currentTimeMillis();
    _isLogout = false;
    _moneyToTable = false;
  }

  public String getSession() {
    return _session;
  }

  public String getBotId() {
    return _name;
  }

  /**
    public void reopenChannel() throws Exception {
      log.info("Reopen channel for player " + _name);
      connect();
      _tid = -99;
      _grid = -99;
      _pos = -99;
      _wlen = -1;
      _wseq = 0;
      _rlen = -1;
      _rseq = -1;
      _tableChips = 0;
      _total_play_chips = 0;
      _total_real_chips = 0;
    }
   }
   **/

  //public String connect(SocketChannel _channel) throws Exception {
  public void connect() throws Exception {
    try {
      if (_channel == null) {
        Configuration __conf = Configuration.getInstance();
        log.finest(_name + ":: Connecting to server...." +
                  __conf.get("Network.server.ip"));
        String _server = (String) __conf.get("Network.server.ip");
        int _port = __conf.getInt("Network.port");
        _currState = ShillConstants.PLR_CONNECTING;
        _currIdleCount = 0;
        _channel = SocketChannel.open(new InetSocketAddress(
            _server, _port));
        //_channel.socket().setSoTimeout(2000);
                ShillServer.players_connected++;
        log.finest("Players connected = " + ShillServer.players_connected);
        //_channel.configureBlocking(true);
        _channel.configureBlocking(false);
        //IMPORTANT - make it non-blocking from here on
        setSelectorForRead();
        //log.finest("before connect");
        Command ge = new Command("null", Command.C_CONNECT);

        write(ge.toString());
        //log.info("comand sent to server.");
      }
    }
    catch (Exception e) {
      log.log(Level.WARNING, "Setting bot for removal " + _name + e.getMessage());
      _currState = ShillConstants.REMOVE_BOT;
    }
  }

  public boolean readHeader() throws IOException {
    if (_ctype.equals("FLASH")) {
      return true;
    }
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
      //log.fatal(_name + " Partial header read " + r);
      if (r == -1) {
        _dead = true;
        log
            .warning(_name
                   + " Marking the client dead as the channel is closed  ");
      }
      return false;
    }
    _h.flip();
    _rseq = _h.getInt();
    _rlen = _h.getInt();
    _h = null;
    return true;
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

  public String readFull() {
    String s = read();
    while (s == null) {
      s = read();
      try {
        Thread.currentThread().sleep(200);
      }
      catch (Exception e) {
        e.printStackTrace();
        //ignore
      }
    }
    return s;
  }

  public synchronized String read() {
    _last_read_time = System.currentTimeMillis();
    try {
      if (_dead) {
        return null;
      }
      if (readHeader() && readBody()) {
        return _comstr;
      }
    }
    catch (IOException e) {
      log.warning(_name + " Marking client as dead ");
      _dead = true;
      return null;
    }
    catch (Exception e) {
      log.warning(_name + " Garbled command ");
      _dead = true;
      return null;
    }
    return null;
  }

  private void resetRead() {
    _h = null;
    _b = null;
    _rlen = -1;
    _rseq = -1;
  }

  public synchronized boolean write(String str) {
    _last_write_time = System.currentTimeMillis();
    //System.out.println(str);
    try {
      if (_ctype.equals("FLASH")) {
        _o = ByteBuffer.allocate(str.length() + TERMINATOR_SIZE);
        _o.put(str.getBytes());
        _o.put(TERMINATOR);
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
      _o = null;
      _dead = false;
    }
    catch (IOException e) {
      _dead = true;
      _currState = ShillConstants.REMOVE_BOT;
      log.warning(_name
               +
               " Marking client as dead because of IOException during write");
      return false;
    }
    return true;
  }

  //public Response login() throws Exception {
  public void login() throws Exception {
    CommandLogin ge = new CommandLogin(_session, _name, _password, "807");
    log.info(_name + " Login Command " + ge);
    _currState = ShillConstants.PLR_LOGGING;
    write(ge.toString());
  }

  public void logout() throws Exception {
    //log.finest("logout():: START");
    if (_name.equals("dummy")) {
      new Exception().printStackTrace();
    }
    Command ge = new Command(_session, Command.C_LOGOUT);
    log.info(_name + " Logout Command " + ge);
    if (_channel != null) {
      write(ge.toString());
      //cleaning parameters here instead of in the response
      //as this is more safe
    }
    _currState = ShillConstants.PLR_DISCONNECTED;
        ShillServer.players_connected--;
    _currGamesPlayed = 0;
    if (_channel != null) {
      _channel.close();
      _channel = null;
      _pos = -1;
      _tid = null;
      _grid = -97;
    }

    _session = null;
    _pos = -1;
    _tid = null;
    _grid = -1;
    //log.finest("logout():: END");
  }

  public void turnDeaf() throws Exception {
    CommandString ge = new CommandString(_session, Command.C_TURN_DEAF, _tid);
    _pos = -91;
    _tid = null;
    _tmpTid = null;
    _tmpJoinPos = -1;
    _currState = ShillConstants.PLR_LOGGED;
    log.info(_name + " TURN DEAF " + ge);
    write(ge.toString());
  }

  public Response config() throws Exception {
    _channel.configureBlocking(true);
    Command ge = new Command(_session, Command.C_CONFIG);
    log.info(_name + " Config Command " + ge);
    write(ge.toString());
    ResponseFactory rf = new ResponseFactory(readFull());
    ResponseConfig gr = (ResponseConfig) rf.getResponse();
    log.info(_name + " Config Response " + gr);
    return gr;
  }

  //public Response register() throws Exception {
  public void register() throws Exception {
    CommandRegister ge = new CommandRegister(_session, _name,
                                             _password, _name + "@test.com",
                                             _gender, "", "");
    log.info(_name + " Register Command " + ge);
    _currState = ShillConstants.PLR_REGISTERING;
    write(ge.toString());
  }

  //public Response addObserver(int tid) throws Exception {
  public void addObserver(String tid) throws Exception {
    _currState = ShillConstants.PLR_REQ_OBSERVE;
    CommandTableDetail ge = new CommandTableDetail(_session, tid);
    log.info(_name + " Add Observer Command " + ge);

    write(ge.toString());
  }


  public void join(String tid, int pos, double mb) throws Exception {
	 // new Exception().printStackTrace();
    if (mb == 0) {
      return;
    }
    _tid = tid;
    _pos = pos;
    CommandMove join = new CommandMove(_session, Command.M_SIT_IN,
                                       ((60+(Math.random() * 40)) * mb), tid);
    join.setPlayerPosition(pos);
    log.severe(_name + " Join Command " + join);
    //new Exception().printStackTrace();
    _currState = ShillConstants.PLR_JOINING;
    write(join.toString());
  }
  
//by rk
  public void joinPool(String poolName, double value) throws Exception {
	  //new Exception().printStackTrace();
	  if(value == 0){
		  return;
	  }
	  CommandJoinPool ge = new CommandJoinPool(_session, Command.C_JOIN_POOL, poolName, value);
	    log.severe(_name + " Pool Join Command " + ge);
	    
	    _last_write_time = System.currentTimeMillis();
	    write(ge.toString());
  }

  public void leave() {
    try {
      CommandMove leave = new CommandMove(_session, Command.M_LEAVE, 0,
                                          _tid);
      leave.setPlayerPosition(_pos);
      log.info(_name + " Leave Command " + leave);
      write(leave.toString());
      //hack.. as server is not sending back the confirmation
            ShillServer.__tm[_tableNum]._playerDetails[_pos] = 0;
            ShillServer.__tm[_tableNum]._pc--;
      _pos = -87;
      _tid = null;
      _tmpJoinPos = -1;
      _tmpTid = null;
    }
    catch (Exception e) {
      //log.fatal(e.getMessage());
    }
  }

  //get money from BR into total chips
  public void sitOut() throws Exception {
    CommandString ge = new CommandString(_session, Command.C_SIT_OUT, _tid);
    log.info(_name + " Sit Out  Command " + ge);
    _last_move_time=System.currentTimeMillis();
    _currState = ShillConstants.PLR_SITOUT;
    write(ge.toString());
  }

  //get money from BR into total chips
  public void sitIn() throws Exception {
    if (_tid==null){
      log.info(this + " Invalid tid during sit in...marking bot for removal");
      _currState = ShillConstants.REMOVE_BOT;
      return;
    }
    _last_move_time=System.currentTimeMillis();
    CommandString ge = new CommandString(_session, Command.C_SIT_IN, _tid);
    log.severe(_name + " Sit In  Command " + ge);
    write(ge.toString());
  }


  //get money from total chips into a table
  public void getMoneyIntoGame(double mb) {
    if (_tid == null) {
      _currState = ShillConstants.REMOVE_BOT;
      log.warning(_name + " getMoneyIntoGame--tid -ive " + _tid);
      return;
    }
    CommandGetChipsIntoGame ge;
    ge = new CommandGetChipsIntoGame(_session, _tid, ((60+(Math.random() * 40)) * mb));
    log.severe(_name + " Get Chips into game Command " + ge);
    _moneyToTable = true;
    write(ge.toString());
  }


  public void waitList(int tid) {
    CommandInt cmd;
    cmd = new CommandInt(_session, Command.C_WAITER, tid);
    log.finest(_name + " Add To Waiting List Command " + cmd);
    _currState = ShillConstants.PLR_REQ_WAITING;
    write(cmd.toString());
  }

  public void move(String mov, double amt, Card[] crd, double mb) throws Exception {
    int mov_id = -99;
    StringBuilder md=new StringBuilder();
    if (mov.equals("join")) {
      mov_id = Command.M_JOIN;
    }
    else if (mov.equals("open")) {
      mov_id = Command.M_OPEN;
    }
    else if (mov.equals("check")) {
      mov_id = Command.M_CHECK;
      _checkCount++;
    }
    else if (mov.equals("call")) {
      mov_id = Command.M_CALL;
    }
    else if (mov.equals("raise")) {
      mov_id = Command.M_RAISE;
      _raiseCount++;
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
    }
    else if (mov.equals("small-blind")) {
      mov_id = Command.M_SMALLBLIND;
    }
    else if (mov.equals("big-blind")) {
      mov_id = Command.M_BIGBLIND;
    }
    else if (mov.equals("sb-bb")) {
        mov_id = Command.M_SBBB;
      }
    else if (mov.equals("bet")) {
      mov_id = Command.M_BET;
    }
    else if (mov.equals("ante")) {
      mov_id = Command.M_ANTE;
    }
     
    else if (mov.equals("all-in")) {
      mov_id = Command.M_ALL_IN;
    }
    else if (mov.equals("bet-pot")) {
      mov_id = Command.M_BET_POT;
    }
    else if (mov.equals("bringin")) {
      mov_id = Command.M_BRING_IN;
    }
    else if (mov.equals("complete")) {
      mov_id = Command.M_COMPLETE;
    }
        else {
      mov_id = Command.M_ILLEGAL;
    }
    
    // if the move amount is 8 times the minbet fold
    if (amt > (5 + (Math.random() *10) )*mb){
        mov_id=Command.M_FOLD;
        amt=0;
    }
    
    if (mov_id != Command.M_ILLEGAL && mov_id != Command.M_WAIT
        && _tid != null && _pos > -1) {
      log.finest(_name + "Making a move = " + mov + ", id = " + mov_id
              + ", amt = " + amt + ", tid = " + _tid);
      CommandMove cm = new CommandMove(_session, mov_id, amt, _tid, _grid, md.toString());
      cm.setPlayerPosition(_pos);
      _last_move_time= System.currentTimeMillis();
      write(cm.toString());
      //log.debug(cm.toString());
    }
    else {
      log.log(Level.WARNING, "ILLEGAL MOVE " + this._session + this._name);
      log.log(Level.WARNING, "Move Scuttled: move = " + mov + ", id = " + mov_id
                + ", amt = " + amt + ", tid = " + _tid + ", pos = "
                + _pos);
    }
  }

  public void processMove() throws Exception {
    GameEvent ge = new GameEvent();
    ResponseGameEvent rge=null;
    for (int i = 0; i < _rge.size(); i++) {
      try {
        Object o=_rge.remove(0);
        if (o instanceof ResponseGameEvent){
          rge = ( (ResponseGameEvent) o);
        }else {
          log.warning("Non game event " + o);
          return;
        }
      }catch (Exception e){
        log.log(Level.WARNING, "Invalid game event ", e);
        return;
      }
      String str_ge = rge.getGameEvent();
      if (str_ge == null) {
        return;
      }
      ge.init(str_ge);
      processMove(ge);
    }

  }

  public synchronized void processMove(GameEvent ge) throws Exception {

    _tid = ge.getGameName();
    _grid = ge.getGameRunId();

    String tp = ge.get("target-position");
    //log.finest("target position = " + tp + ", _pos = " + _pos);
    //log.debug(ge);

    _minBet = ge.getMinBet() > 0 ? ge.getMinBet(): _minBet;

    if (tp == null) {
      return;
    }
    _pos = Integer.parseInt(tp);
    //set the players pos-type
    String my_name = "";
    String pd[][] = ge.getPlayerDetails();
    //update bot worth
    for (int i = 0; pd!=null&& i < pd.length; i++) {
      String name = pd[i][3];
      long status = Long.parseLong(pd[i][4]);
      double tableChips = Double.parseDouble(pd[i][1]);
      Player p = (Player)ShillServer.__players.get(name);
      if (p==null)continue;
      if ((status & 2097152) > 1){
        p._currState = ShillConstants.PLR_SITOUT;
      }
      if (tableChips < 6 * _minBet && p._currState != ShillConstants.PLR_SITOUT) {
        log.info(pd[i][3] + " money on table is less, getting more chips");
        p.sitOut();
      }
      if (_pos == Integer.parseInt(pd[i][0])) {
        my_name = pd[i][3];
        _tableChips=tableChips;
      }
    }

    //log.debug(my_name + "--" + ge);

    if (!_name.equals(my_name)) {
      log.log(Level.WARNING, "No player with the name " + _name);
      return;
    }

    //log.fatal(_name + " Player " + my_name);

    //check for join,cancel moves..i.e moves for waiting player
    if (_currState == ShillConstants.PLR_WAITING) {
      String moves[][] = ge.getMove();
      //log.finest("moves[0][1] = " + moves[0][1]);
      if (moves[0][1] == "join") {
        move(moves[0][1], Double.parseDouble(moves[0][2]), null, _minBet);
      }
    }

    String winner[][] = ge.getWinner();
    if (winner != null && winner.length > 0) {
      _currGamesPlayed++;
      _raiseCount = 0;
      _checkCount = 0;
    }

    String[] last_move = ge.getLastMoveString().split("\\|");

    //check if this player made last move
    if (last_move.length > 3 && last_move[1].equals(_name)) {
      if (last_move[2].equals("leave")) {
        _pos = -99;
        _tid = null;
        _tmpJoinPos = -1;
        _tmpTid = null;
        _currState = ShillConstants.PLR_IDLE;
        log.info("Left table " + this);
        _leave_time = System.currentTimeMillis();
        //ShillServer.getJoinParameters(this);
        return;
      }
      else if (last_move[2].equals("join")) {
        //cross check the pos and reset...actually not needed.
        _pos = Integer.parseInt(last_move[0]);
        _currState = ShillConstants.PLR_JOINED;
        log.finest("Join Succeded for Player " + _name + " at Pos "
                  + _pos);
        return;
      }
      else if (last_move[2].equals("none")) {
        log.finest("Join failed for Player " + _name + " at Pos " + _pos
                  + " ,  ..so logging out the player");
        logout();
        return;
      }
    } //player made last move

    if (ge.getIllegalMoveString() != null) {
      log.log(Level.WARNING, "ILLEGAL MOVE made by " + _name
                + "response from server :: "
                + ge.getIllegalMoveString());
      //logout();
      return;
    }

    log.fine(ge.toString());
    //log.finest("target position = " + tp + ", _pos = "+ _pos);
    //moves
    String moves[][] = ge.getMove();
    if (moves != null && (Integer.parseInt(moves[0][0]) == _pos)
        && !moves[0][1].equals("wait")) {
      //log.debug(moves[0][1] + "-" + _name);

      //doing blinds needs no logic...
      if (moves[0][1].equals("small-blind")) {
        Thread.currentThread().sleep(4000);
        move(moves[0][1], Double.parseDouble(moves[0][2]), null, _minBet);
        return;
      }

      if (moves[0][1].equals("big-blind")) {
        Thread.currentThread().sleep(1000);
        move(moves[0][1], Double.parseDouble(moves[0][2]), null, _minBet);
        return;
      }
      if (moves[0][1].equals("sb-bb")) {
          Thread.currentThread().sleep(1000);
          move(moves[0][1], Double.parseDouble(moves[0][2]), null, _minBet);
          return;
        }

      if (moves[0][1].equals("ante")) {
        Thread.currentThread().sleep(1000);
        move(moves[0][1], Double.parseDouble(moves[0][2]), null, _minBet);
        return;
      }

      if (moves[0][1].equals("bringin")) {
        Thread.currentThread().sleep(1000);
        move(moves[0][1], Double.parseDouble(moves[0][2]), null, _minBet);
        return;
      }

      int dealer_pos = ge.getDealerPosition();
      setPositionType(pd, dealer_pos, _pos);
      String selectedMove = issueAction(_iq, ge);
      if (selectedMove==null){
        selectedMove = moves[0][1];
      }
      if (_raiseCount > RAISE_COUNT && selectedMove.equals("raise")) {
        selectedMove = moves[0][1];
        //log.info("Raise count exceeded " + selectedMove);
      }
      if (_checkCount > CHECK_COUNT && selectedMove.equals("check")) {
        selectedMove = moves[1][1];
        //log.info("Check count exceeded " + selectedMove);
      }

      //log.finest("selected move = " + selectedMove);
      if (selectedMove == null) {
        return;
      }
      for (int i = 0; i < moves.length; i++) {
        if (moves[i][1].equals(selectedMove)) {

          //cleanly logout the player if marked
          if (selectedMove.equals("opt-out")) {
            log.info(
                "Optout "
                + _name
                + " leaves.");
            _currState = ShillConstants.PLR_IDLE;
            leave();
            log.finest(_currGamesPlayed + " Left table " + this);
            break;
          }
          if (_currGamesPlayed >= _nGames) {
            if (selectedMove.equals("fold") || selectedMove.equals("check")) {
              log.info(
                  "Games per_session exceeded "
                  + _name
                  + " leaves.");
              _currState = ShillConstants.PLR_IDLE;
              leave();
              log.warning(_currGamesPlayed + " Left table " + this);
              //ShillServer.getJoinParameters(this);
              break;
            }
          }
          String mov = selectedMove;
          double amt = -1;
          double maxRaiseAmt = 0;
          if (moves[i][2].indexOf("-") == -1) {
              mov = moves[i][1];
              amt = Double.parseDouble(moves[i][2]);
          } else {
              String val[] = moves[i][2].split("-");
              mov = moves[i][1];
              amt = Double.parseDouble(val[0]);
              maxRaiseAmt = Double.parseDouble(val[1]);
          }
          /**if(_maxBet<=0 && group<2 &&(selectedMove.equals("raise") || selectedMove.equals("bet"))){
              double all_pot_in = 0;
              for(int j=0;j<moves.length;j++){
                  if(moves[j][1].equals("all-in")||moves[j][1].equals("bet-pot")){
                      all_pot_in = Double.parseDouble(moves[j][2]);
                      break;
                  }
              }

              amt=amt+(maxRaiseAmt/7)*(7-group);
              if(amt>=all_pot_in){
                  mov = _maxBet==0?"bet-pot":"all-in";
                  amt = all_pot_in;
              }
          }**/

          //delay the move.
          Thread.currentThread().sleep((int)(Math.random() * DELAY_MS));
          move(mov, amt, ge.getHand(), _minBet);
          break;
        } //make the selected move
      }

    }
  }

  public void setSelectorForRead() throws Exception {
    _channel.configureBlocking(false);
    _channel.register(ShillServer.__selector, SelectionKey.OP_READ, this);
  }

  //public ResponseTableList getTableList(int filter) throws Exception {
  public void getTableList(int filter) throws Exception {
    CommandTableList ge = new CommandTableList(_session, filter, "admin", _name); //0xFFFF
    log.info("TableList Command " + ge);
    _currState = ShillConstants.PLR_REQUESTING_TL;
    write(ge.toString());
  }

  public void heartBeat() throws Exception {
    if (_session != null) {
      Command ge = new Command(_session, Command.C_HTBT);
      write(ge.toString());
      //log.log(Level.WARNING, "Sent heartbeat for " + _name);
    }
  }

  public void ping() throws Exception {
    if (_session != null) {
      Command ge = new Command(_session, Command.C_PING);
      write(ge.toString());
    }
  }

  public String toConfigString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_name).append("|").append(_iq).append("|")
        .append(_gTypes).append("|").append(_pgTypes).append("|")
        .append(_nGames).append("|").append(_lossCeil).append("|")
        .append(_winCeil).append("|").append(_delay).append("|")
        .append(_idleCount).append("|").append(_sitoutCount)
        .append("|");
    return sb.toString();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_name).append("|").append(_iq).append("|")
        .append(_gTypes).append("|").append(_pgTypes).append("|")
        .append(_nGames).append("|").append(_lossCeil).append("|")
        .append(_winCeil).append("|").append(_delay).append("|")
        .append(_idleCount).append("|").append(_sitoutCount)
        .append("|").append(_currGamesPlayed).append("|").append(
            _currIdleCount).append("|")
        .append(_currSitoutCount).append("|").append(_currState)
        .append("|").append(_dead).append("|").append(_tid).append(
            "|").append(_pos);
      sb.append(", RC=" + _total_real_chips + ", PC=" + _total_play_chips);
    return sb.toString();
  }

  public void run() {
    try {
      processMove();
    }
    catch (Exception ex) {
      log.log(Level.WARNING, "Process Move Exception", ex);
      ex.printStackTrace();
    }
  }

} // end class Player
