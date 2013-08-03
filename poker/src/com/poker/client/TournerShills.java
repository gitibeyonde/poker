package com.poker.client;

import java.util.Vector;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.io.IOException;

import com.poker.common.message.*;
import com.agneya.util.*;
import com.poker.client.Bot;
import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;
import com.golconda.game.GameType;
import com.golconda.message.*;

import com.poker.game.PokerGameType;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Shill client
 *
 */
public class TournerShills implements Runnable {
    // set the category for logging
    static Logger _cat = Logger.getLogger(TournerShills.class.getName());

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
    static Player dummy = null;
    static int pn_count;
    static HashMap torneyBotsMap = new HashMap();
    static HashMap torneysRunningMap = new HashMap();
    Vector BotVector = new Vector();
    Vector remBots = new Vector();
    int _maxBots = 1000;
    Random random = new Random();

    public TournerShills() {
        _players = new Vector();
        //initialize the RNG
        _rng = new Rng();
    }

/*
     public void addBotsFromDB() throws Exception {
      HashSet bots = BotData.getAvailableBotData(_maxBots);
      _cat.finest("_maxBots = " + _maxBots);
      pn = new String[_maxBots];
      int bc = bots.size();
      Iterator it = bots.iterator();
      int j = 0;
      for (; j < bc && it.hasNext(); j++) {
        BotData bd = (BotData) it.next();
        BotVector.add(bd.getBotId());
      }
      pn = (String[]) BotVector.toArray(pn);
      pn_count = pn.length;
    }
 */
    public void getBotsFromDB(){
    	try {
			String[] playersFromDB = DBPlayer.getBots();
			//pn_count = playersFromDB.length;
			System.out.println("players from db "+ playersFromDB.length);
			
			 for (int i = 0; i < playersFromDB.length; i++) {
	             String p = playersFromDB[i];
	             BotVector.add(p);
	         }
	         pn_count = BotVector.size();
	         remBots.addAll(BotVector);
		} catch (DBException e) {
			e.printStackTrace();
		}
    }
    
     public void getBotsFromStringArray() throws Exception {
         pn_count = player_names.length;
         //System.out.println("player names "+pn_count);
         pn_count = 0;
         for (int i = 0; i < player_names.length; i++) {
             String p = player_names[i];
             BotVector.add(p);
         }
         pn_count = BotVector.size();
         remBots.addAll(BotVector);
      }

    public class TourneysMonitor extends TimerTask {
        public void run() {
            try {
                _cat.info("---------IN TOURNEY MONITOR No of the tourneys registered " +
                          torneyBotsMap.size() + " Players size is " + _players.size());
                ResponseTournyList rtl = null;
                if (dummy == null) {
                    dummy = new Player("tdummy", "tdummy");
                    _players.add(dummy);
                }
                rtl = dummy.getTournyList();
                int tc = rtl.getTournyCount();
                for (int i = 0; i < tc; i++) {
                    TournyEvent tony = rtl.getTourny(i);
                    String tonyId = tony.name();
                    Vector players = null;
                    if ((tony.state() == REG /**&& tony.getPlayerCount() > 0**/) &&
                        !torneyBotsMap.containsKey(tonyId)) {
                        _cat.info(" Registering players into tourney " +
                                  tony.name());
                        //_pcount = (int)( Math.random()* Integer.parseInt((String) _conf.get("TournyPlayer.Count"))) + 4;
                        _pcount =  Integer.parseInt((String) _conf.get("TournyPlayer.Count"));
                        
                       //System.out.println("player count "+_pcount);
                        Player p=null;
                        players = new Vector();
                        String rplayer = "";
                        for (_pcount--; _pcount >= 0; _pcount--) {
                            if(remBots.size()<=0){
                                remBots.removeAllElements();
                                remBots.addAll(BotVector);
                            }
                            int rn = random.nextInt(remBots.size());
                            if(tony.state()!=REG)break;
                            if (pn_count <= 0) {
                                pn_count = remBots.size();
                            }
                            try {
                                rplayer = (String)remBots.get(rn);
                                p = new Player(rplayer, rplayer);
                            } catch (Exception e) {
                            	e.printStackTrace();
                                _cat.severe("Exception in constructor of Player"+e);
                                Thread.currentThread().sleep(100);
                                _pcount++;
                                continue;
                            }
                            try {
                                Response r = p.login();
                                _cat.info(" Player " + rplayer +
                                          " Login response is : " + r.getResult());
                                if (r.getResult() == Response.E_REGISTER) {
                                    r = p.register();
                                }
                                if (r.getResult() != Response.E_SUCCESS) {
                                    _pcount++;
                                    continue;
                                }
                                r = p.registerTourny(tony.name());
                                _cat.info(" Tourney register response for " +
                                          rplayer + " is " + r.getResult() +
                                          " now tourney players size is " +
                                          players.size() +
                                          " and total tourners are " +
                                          _players.size());
                                if (r.getResult() ==
                                    Response.E_REGISTERATION_CLOSED) {
                                    break;
                                }
                                if (r.getResult() == Response.E_BROKE) {
                                    _pcount++;
                                    continue;
                                }
                                p._joinedGameType = PokerGameType.HoldemTourny;
                                players.add(p);
                                remBots.remove(rn);
                                _players.add(p);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                _cat.severe("FATAL " + ex.getMessage() +
                                           " For player " + p._name);
                            }
                        }
                        torneyBotsMap.put(tonyId, players);
                        _cat.info(players.size() + "  Tourners registered in " +
                                  tony.name());

                    } else if (tony.state() == JOIN && torneyBotsMap.get(tonyId) != null) {
                        _cat.info(" Tourners joining tables in Tourney " +
                                  tony.name());
                        players = (Vector) torneyBotsMap.get(tonyId);
                        Enumeration pen = players.elements();
                        while (pen.hasMoreElements()) {
                            Player tp = (Player) pen.nextElement();
                            if(_players.contains(tp)){
                                tp.setSelectorForRead();
                                try{
                                    tp.goToMyTable(tony.name());
                                }catch(Exception e){
                                    e.printStackTrace();
                                    _players.remove(tp);
                                    continue;
                                }
                            }
                        }
                        torneysRunningMap.put(tonyId,players);
                        torneyBotsMap.remove(tonyId);
                        torneyBotsMap.put(tonyId, null);
                    } else if ((tony.state() == FINISH || tony.state() == NOEXIST ||
                                tony.state() == CREATED || tony.state() == DECL) &&
                               torneyBotsMap.containsKey(tonyId)) {
                        if(torneysRunningMap.get(tonyId)==null){
                            torneyBotsMap.remove(tonyId);
                        }
                        players = (Vector)torneysRunningMap.get(tonyId);
                        Enumeration pen = players.elements();
                        while (pen.hasMoreElements()) {
                            Player tp = (Player) pen.nextElement();
                            if(_players.contains(tp)){
                                try{
                                    tp.logout();
                                    remBots.add(tp);
                                }catch(Exception e){
                                    e.printStackTrace();
                                    _players.remove(tp);
                                    continue;
                                }
                            }
                        }
                        torneysRunningMap.remove(tonyId);
                        torneyBotsMap.remove(tonyId);
                        _cat.info("---------removed tourney from MAPS");
                    }
                } //for
            }catch (Exception ex) {
                ex.printStackTrace();
                _cat.severe("Error IN TourneyMonitor Thread");
            } //catch
            if(torneyBotsMap.size()<=0){
                remBots.removeAllElements();
                remBots.addAll(BotVector);
            }
        } // run

    } //end of TourneysMonitor class


    public void startShills() throws Exception {
        //initialize the vector with player data
        // random for now
        _inProgress = false;
        _conf = Configuration.getInstance();
        _pcount = (int)(Math.random() * Integer.parseInt((String) _conf.get("TournyPlayer.Count"))) + 4;
        DELAY_MS = Integer.parseInt((String) _conf.get("TournyPlayer.Delay"));
        _server = (String) _conf.get("Network.server.ip");
        _htbt = _conf.getInt("Server.maintenance.heartbeat");
        long last_registered_time = System.currentTimeMillis();
        _selector = Selector.open();
        dummy = new Player("tdummy", "tdummy");
        _players.add(dummy);
        ResponseTournyList rtl = dummy.getTournyList();
        int tc = rtl.getTournyCount();
        TournyEvent[] tourny = new TournyEvent[tc];
        for (int i = 0; i < tc; i++) {
            tourny[i] = rtl.getTourny(i);
            _cat.info(tourny[i].toString());
        }
        // start a heartbeat timer thread
        //getting bots from string array
//        getBotsFromStringArray();
        //getting bots from DB
        getBotsFromDB();
        HeartBeat hb = new HeartBeat();
        Timer t = new Timer();
        t.schedule(hb, 0, _htbt);

        //starting tourney monitor
        TourneysMonitor tm = new TourneysMonitor();
        Timer ttm = new Timer();
        ttm.schedule(tm, 0, 10000);

        _runner = new Thread(this);
        _runner.start();

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
                        String response = p.read();
                        if (response == null) {
                            continue;
                        }
                        ResponseFactory rf = new ResponseFactory(response);
                        Response r = rf.getResponse();
                        if (r._response_name == Response.R_MOVE) {
                            p._rge.add((ResponseGameEvent) r);
                            if(!p.running)
                                new Thread(p).start();
                        } else if (r._response_name == Response.R_CONNECT) {
                        } else if (r._response_name == Response.R_MESSAGE) {
                            ResponseMessage rm = (ResponseMessage) r;
                            _cat.info("Message =" + rm.getMessage());
                        } else if (r._response_name == Response.R_LOGOUT) {
                            _players.remove(p);
                            _cat.info("Removed " + p);
                        } else if (r._response_name ==
                                   Response.R_TABLE_CLOSED) {
                            _cat.info("CLOSING TABLE");
                        } else if (r._response_name ==
                                   Response.R_TABLE_OPEN) {
                            _cat.info("OPENING TABLE");
                        } else if (r._response_name ==
                                   Response.R_TABLEDETAIL) {
                            _cat.info("TABLE DETAILS");
                        } else if (r._response_name ==
                                   Response.R_TOURNYDETAIL) {
                            _cat.info("DETAILS");
                        } else if (r._response_name ==
                                   Response.R_TOURNYMYTABLE) {
                            _cat.info(p._name + " GoToTable Response " + r);
                            if (r.getResult() == 1) {
                                ResponseTournyMyTable gr = (
                                        ResponseTournyMyTable) r;
                                p._tid = gr.getGameTid();
                                p._pos = gr.getPosition();
                            }
                        } else if (r._response_name ==
                                   Response.R_TOURNYSTARTS) {
                        } else {
                            _cat.severe(
                                    "SHILL: FATAL: Unknown event received from the server " +
                                    r);
                        }
                    }
                }
                Thread.currentThread().sleep(10);
            } catch (IOException ex) {
                _cat.severe(" Removing player " + p);
                ex.printStackTrace();
                key.cancel();
                _players.remove(p);
                continue;
            } catch (Exception ex) {
                _cat.severe("Logging out the errant player "+ ex);
                key.cancel();
                _players.remove(p);
                try {
                    p.logout();
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

   
    public class Player extends Bot implements Runnable {
        String _password;
        String _name;
        public String _tid;
        public int _grid = -99;
        Command _out;
        SocketChannel _channel;
        int _seq = 0;
        String _session = null;
        Vector _rge = new Vector();
        boolean running = false;
        int _iq = random.nextInt(6);

        public void run() {
            ResponseGameEvent rge=null;
            running = true;
            while(_rge.size()>0) {
              try {
                rge =( (ResponseGameEvent) _rge.remove(0));
                processMove(rge);
              }catch (Exception e){
                  running = false;
                  _cat.severe("Invalid game event "+e);
                  e.printStackTrace();
                  return;
              }
            }
            running = false;
        }

        public Player(String name, String pass) throws Exception {
            super(name);
            Configuration _conf = Configuration.getInstance();
            String _server = (String) _conf.get("Network.server.ip");
            int _port = _conf.getInt("Network.port");
            _channel = SocketChannel.open(new InetSocketAddress(_server, _port));
            _channel.configureBlocking(true);
            //String session = connect(_channel);
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

        public synchronized String read() throws IOException {
            String com = null;
            ByteBuffer h = ByteBuffer.allocate(8);
            h.clear();
            int r = _channel.read(h);
            if (r <= 0) {
                return null;
            } while (h.hasRemaining()) {
                _cat.info("recvd partial header");
                r = _channel.read(h);
                if(r==-1) return null;
                try {
                    Thread.currentThread().sleep(1);
                } catch (InterruptedException e) {
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
                _cat.finest("recvd partial body= " + l);
                _channel.read(b);
                try {
                    Thread.currentThread().sleep(1);
                } catch (InterruptedException e) {
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

        public void logout() throws Exception {
            Command ge = new Command(_session, Command.C_LOGOUT);
            _cat.finest(_name + " Logout Command " + ge);
            write(ge.toString());
        }

/*        public Response config() throws Exception {
            _channel.configureBlocking(true);
            Command ge = new Command(_session, Command.C_CONFIG);
            _cat.finest(_name + " Config Command " + ge);
            write(ge.toString());
            ResponseFactory rf = new ResponseFactory(read());
            ResponseConfig gr = (ResponseConfig) rf.getResponse();
            _cat.finest(_name + " Config Response " + gr);
            return gr;
        }*/

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
                    _name + "@test.com", (byte) Math.round(Math.random()),
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
            _tid = ge.getGameName();
            _grid = ge.getGameRunId();
            _maxBet = ge.get("max-bet")!=null?(Double
                          .parseDouble(ge.get("max-bet"))):0;
            String tp = ge.get("target-position");
            //log.debug("target position = " + tp + ", _pos = " + _pos);
            //log.debug(ge);

            if (tp == null) {
                return;
            }
            _pos = Integer.parseInt(tp);

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

            //_cat.finest("RECEIVED " + this +"\n GE=" + ge);
            
            //moves
            String moves[][] = ge.getMove();

            if (moves != null && (Integer.parseInt(moves[0][0]) == _pos)
                && !moves[0][1].equals("wait")) {
                //log.debug(moves[0][1] + "-" + _name);

                //doing blinds needs no logic...
                if (moves[0][1].equals("small-blind")) {
                    move(moves[0][1], Double.parseDouble(moves[0][2]));
                    return;
                }
                if (moves[0][1].equals("big-blind")) {
                    move(moves[0][1], Double.parseDouble(moves[0][2]));
                    return;
                }
                if (moves[0][1].equals("ante")) {
                    move(moves[0][1], Double.parseDouble(moves[0][2]));
                    return;
                }
                if (moves[0][1].equals("bringin")) {
                    move(moves[0][1], Double.parseDouble(moves[0][2]));
                    return;
                }
                String pd[][] = ge.getPlayerDetails();
                int dealer_pos = ge.getDealerPosition();
                setPositionType(pd, dealer_pos, _pos);
                String selectedMove = issueAction(_iq, ge);
                if (selectedMove == null) {
                    selectedMove = moves[0][1];
                }
                int pos = Integer.parseInt(moves[0][0]);
                    _tid = ge.getGameName();
                    // calculate the hand strength
                    //int strength = ge.getHandStrength();
                    //_cat.finest("Hand strength for " + _name + " is " + strength);
        
                    String mov = selectedMove;
                    double amt = -1;
                    int i = 0;
                    double maxRaiseAmt = 0;
                    for (; i < moves.length; i++) {
                        if (moves[i][1].equals(selectedMove)) {
                            if (moves[i][2].indexOf("-") == -1) {
                                mov = moves[i][1];
                                amt = Double.parseDouble(moves[i][2]);
                            } else {
                                String val[] = moves[i][2].split("-");
                                mov = moves[i][1];
                                amt = Double.parseDouble(val[0]);
                            }
                            double all_pot_in = 0;
                            if(_maxBet<=0 && group<7 &&(selectedMove.equals("raise") || selectedMove.equals("bet"))){
                                for(int j=0;j<moves.length;j++){
                                    if(moves[j][1].equals("all-in")||moves[j][1].equals("bet-pot")){
                                        all_pot_in = Double.parseDouble(moves[j][2]);
                                        break;
                                    }
                                }
        
                                amt=amt+(maxRaiseAmt/7)*(7-group);
                                if(amt>=all_pot_in){
                                    selectedMove = _maxBet==0?"bet-pot":"all-in";
                                    i=0;continue;
                                }
                            }
                            break;
                        }
                    }
                    _cat.finest(_name+": move= "+mov+", amt= "+amt+" ,group = "+group+",possible-moves = "+ge.getNextMoveString()+",cards "+ge.get("hand")+",ccards = "+ge.get("community-cards")+" GE="+ge);
                    if (mov == null) {
                        _cat.severe("Mov is null and making "+moves[0][1]);
                        mov = moves[0][1];
                        amt = Double.parseDouble(moves[0][2]);
                    }
                    Thread.currentThread().sleep(((int)(DELAY_MS*Math.random()))+1000);
                    move(mov, amt);
                }
            }

        public void move(String mov, double amt) throws Exception {
            int mov_id = -99;
            if (mov.equals("join")) {
                mov_id = Command.M_JOIN;
            } else if (mov.equals("open")) {
                mov_id = Command.M_OPEN;
            } else if (mov.equals("check")) {
                mov_id = Command.M_CHECK;
            } else if (mov.equals("call")) {
                mov_id = Command.M_CALL;
            } else if (mov.equals("raise")) {
                mov_id = Command.M_RAISE;
            } else if (mov.equals("fold")) {
                mov_id = Command.M_FOLD;
            } else if (mov.equals("draw-cards")) {
                mov_id = Command.M_PICK;
            } else if (mov.equals("drop-cards")) {
                mov_id = Command.M_DUMP;
            } else if (mov.equals("join")) {
                mov_id = Command.M_JOIN;
            } else if (mov.equals("leave")) {
                mov_id = Command.M_LEAVE;
            } else if (mov.equals("sit-in")) {
                mov_id = Command.M_SIT_IN;
            } else if (mov.equals("opt-out")) {
                mov_id = Command.M_OPT_OUT;
            } else if (mov.equals("wait")) {
                mov_id = Command.M_WAIT;
                return;
            } else if (mov.equals("small-blind")) {
                Thread.currentThread().sleep((int)(DELAY_MS));
                mov_id = Command.M_SMALLBLIND;
            } else if (mov.equals("big-blind")) {
                Thread.currentThread().sleep((int)(DELAY_MS));
                mov_id = Command.M_BIGBLIND;
            } else if (mov.equals("bet")) {
                mov_id = Command.M_BET;
            } else if (mov.equals("ante")) {
                mov_id = Command.M_ANTE;
            } else if (mov.equals("all-in")) {
                mov_id = Command.M_ALL_IN;
            } else if (mov.equals("bet-pot")) {
                mov_id = Command.M_BET_POT;
            } else {
                mov_id = Command.M_ILLEGAL;
            }
            if (mov_id != Command.M_ILLEGAL) {
                _cat.finest(this._name + "  Making a move = " + mov + ", id = " +
                           mov_id +
                           ", amt = " + amt + ", tid = " + _tid);
                CommandMove cm = new CommandMove(_session, mov_id, amt, _tid);
                cm.setPlayerPosition(_pos);
                write(cm.toString());
            } else {
                _cat.severe("ILLEGAL MOVE");
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
            //_cat.finest(_name + " TournyList Response " + gr);
            return gr;
        }

        public ResponseTournyDetail getTournyDetail(String name) throws Exception {
            _channel.configureBlocking(true);
            CommandTournyDetail ge = new CommandTournyDetail(_session, name);
            //_cat.finest("Tourny Detail Command " + ge);
            write(ge.toString());
            ResponseFactory rf = new ResponseFactory(read());
            //_cat.finest(_name + " Tourny Detail Response " + rf._com);
            ResponseTournyDetail gr = null;
            try {
                gr = (ResponseTournyDetail) rf.getResponse();
            } catch (Exception e) {

            }
            //_cat.finest(_name + " Response Tourny Detail " + gr);
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

        private boolean addPlayer(String name, String password, String gid,
                                  int pos) throws
                Exception {
            Player p = new Player(name, password);
            Response r = p.login();
            if (r.getResult() == 2) {
                // try to register the player
                r = p.register();
                if (r.getResult() == 0) {
                    _cat.severe("Registration failed " + p);
                }
            } else if (r.getResult() != 1) {
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


    public class HeartBeat extends TimerTask {

        public void run() {
            Player p = null;
            // send a heartbeat message
            Enumeration e = _players.elements();
            for (; e.hasMoreElements(); ) {
                p = (Player) e.nextElement();

                try {
                    p.heartBeat();
                    //p.ping();
                } catch (Exception ex) {
                    _cat.severe("Error in sending HTBT " + p);
                    _players.remove(p);
                }

            }

        }

    } // end HeartBeat class


    public static void main(String[] args) throws Exception {
        TournerShills shill = new TournerShills();
        shill.startShills();

    }

    private static String[] player_names = {
                                           "Abayomi","adar","ADIN","Adrian", "bone54", "aniket_aol",
                                           "afton","Aidan","ailbhe","ainsley", "agneya34", "pandu_gandu",
                                           "avital","azaria","Bailey","bela", "bhabi", "surabhi", "suman_udan",
                                           "bliss","Blythe","Bozard","Bradley", "savitabhabi", "madhura", "robbie67",
                                           "breck","Brett","bryn","Cameron", "OMGwas", "parsi", "boo_border",
                                           "chelsea","CODY","Corey","Corliss", "patton", "jew_hater", "wsopboss2",
                                           "dacey","Dada","sai","Dakota", "obama21", "phool", "NPT_player", "manmohan80",
                                           "drew","EDEN","Esme","Evelyn", "osamaa", "fook32", "WSOP_king", "chidamabaram",
                                           "faine","Fernley","Gale","Ganan", "laddu", "alice_chen", "saddam_hussain",
                                           "helaku","hiawatha","Hilary","Hina", "saw34", "choon_chew", "musharraf",
                                           "juniper","Kalani","Keeley","Kelby", "cindy51", "peter_parker", "kiyanio",
                                           "kelly","kelsey","keola","kerem", "kelly23", "ocean14", "saniyalz",
                                           "mangi","Lann","LEE","Mackenzie", "sweet16", "ocean16", "sanjayz",
                                           "madison","Mahaska","Makani", "panjoor", "maaki", "emporer", "hillao555",
                                           "morgan","mosi","mun-hee","Murphy", "super007", "bahanki", "vgoyal00",
                                           "nailah","Nedaviah","Ngozi","NOEL", "calvinPrick", "terito", "must_ram",
                                           "Pili","Quaashie","Quimby","Quinn", "wshit", "uddlee", "massod", "george90",
                                           "Robin","Roni","Safa","sage", "woo", "shaolin21", "jack_chan", "popat21",
                                           "Selah","Shannon","Shappa","Shelby", "woo32", "wang", "doorito", "clinton_bill",
                                           "tiernan","TIKI","Toby","Tracy", "master34", "muchmuch", "munchall",
                                           "tuyen","Tyler","Vaitafe","Valentine", "junkjunk", "jafsafrang", "english77",
                                           "babak42", "breck", "johnson23", "michelle", "hillary56", "talnikov44",
                                           "sadak23", "mohammad", "mullah786", "kaleem", "dabeer", "aryan32",
                                           
                                            "Abayomi21","adar33","ADINZ","Adrian_rat", "bone87", "aniket_al",
                                            "aftons","Aidans","ailbhel","ainsle_zy", "agneya36", "pandu_gand",
                                            "avitals","azariaen","Bailey_boy","bela_ma", "bhabi_t", "surabhis", "sumanudan",
                                            "blisss","Blythes","Bozard_saf","Bradley_king", "savita_bhabi", "madhura_selam", "robbie69",
                                            "brecks","Bretts","bryns","Camerons", "OMGwas_fck", "parsi_warsi", "booborder",
                                            "chelseas","CODYs","Coreys","Corlisss", "pattons", "jewhater", "wsopboss6",
                                            "daceys","Dadas","sais","Dakotas", "obama25", "phools", "NPTplayer", "manmohan82",
                                            "drews","EDEN_g","Esmeil","Evelyn_eve", "osama", "fook42", "WSOPs_king", "chidamabar",
                                            "faines","Fernly","Gales","Ganen", "laddus", "alice_chan", "saddamhussain",
                                            "helakus","hiawathan","Hilarys","Hinam", "saw37", "choonchew", "musharraf_paky",
                                            "junipers","Kalanis","Keelay","Kalby", "cindy55", "peterparker", "kiyanios",
                                            "kellys","kelseys","keolas","keram", "kelly27", "ocean13", "saniyals",
                                            "mangis","Lanne","LEEs","Mackenze", "sweet18", "ocean11", "sanjayzs",
                                            "madisons","Maheska","Makeni", "panjoer", "meaki", "emperer", "hillao515",
                                            "morgans","masi","munhee","Murphys", "super009", "bahanka", "vgoyal99",
                                            "nailak","Nedaviak","Ngozis","NOELs", "calvin_Prick", "tereeto", "mustram",
                                            "Pilis","Quaaehie","Quemby","Quenn", "wtshit", "uddlees", "massad", "george92",
                                            "Robins","Ronis","Safas","sages", "wooo", "shaolin23", "jackchan", "popat23",
                                            "Seleh","Shannan","Sheppa","Shalby", "wooo32", "woang", "dooorito", "clintonbill",
                                            "tiernen","TIKIs","Tobys","Tracys", "master38", "much_much", "munchalls",
                                            "tuyan","Tylar","Vaitafes","Valentines", "junk_junk", "jaf_safrang", "english79",
                                            "babak52", "breck11", "johnson28", "michelles", "hillary46", "talnikov94",
                                            "sadak27", "mohammad_bin", "mullahs786", "kaleam", "dabear", "aryan36"
    
    						};
   
}
