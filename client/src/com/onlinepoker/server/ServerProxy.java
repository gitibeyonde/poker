package com.onlinepoker.server;

import java.awt.Component;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.agneya.util.Base64;
import com.golconda.db.PlayerPreferences;
import com.golconda.game.util.ActionConstants;
import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;
import com.onlinepoker.ClientRoom;
import com.onlinepoker.ClientSettings;
import com.onlinepoker.actions.Action;
import com.onlinepoker.actions.BettingAction;
import com.onlinepoker.actions.CashierAction;
import com.onlinepoker.actions.ChatAction;
import com.onlinepoker.actions.ErrorAction;
import com.onlinepoker.actions.MessageAction;
import com.onlinepoker.actions.TableServerAction;
import com.onlinepoker.lobby.LobbyUserImp;
import com.onlinepoker.lobby.SplashWindow;
import com.onlinepoker.lobby.tourny.SNGTournyMessagesListener;
import com.onlinepoker.lobby.tourny.TournyMessagesListener;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.LobbyTournyModel;
import com.onlinepoker.proxies.LobbyInfoListener;
import com.onlinepoker.proxies.LobbyModelsChangeListener;
import com.onlinepoker.proxies.PingDetailsListener;
import com.onlinepoker.proxies.broadcastMessageListener;
import com.onlinepoker.util.BlinkLabel;
import com.onlinepoker.util.CredentialManager;
import com.poker.common.message.CommandBuyChips;
import com.poker.common.message.CommandGetChipsIntoGame;
import com.poker.common.message.CommandInt;
import com.poker.common.message.CommandJoinPool;
import com.poker.common.message.CommandLogin;
import com.poker.common.message.CommandMessage;
import com.poker.common.message.CommandMove;
import com.poker.common.message.CommandRegister;
import com.poker.common.message.CommandString;
import com.poker.common.message.CommandTableDetail;
import com.poker.common.message.CommandTableList;
import com.poker.common.message.CommandTournamentList;
import com.poker.common.message.CommandTournyDetail;
import com.poker.common.message.CommandTournyMyTable;
import com.poker.common.message.CommandTournyRegister;
import com.poker.common.message.CommandTournyUnRegister;
import com.poker.common.message.ResponseBuyChips;
import com.poker.common.message.ResponseConfig;
import com.poker.common.message.ResponseFactory;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseGetChipsIntoGame;
import com.poker.common.message.ResponseLogin;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponsePing;
import com.poker.common.message.ResponseString;
import com.poker.common.message.ResponseTableCloseOpen;
import com.poker.common.message.ResponseTableDetail;
import com.poker.common.message.ResponseTableList;
import com.poker.common.message.ResponseTableOpen;
import com.poker.common.message.ResponseTablePing;
import com.poker.common.message.ResponseTournyDetail;
import com.poker.common.message.ResponseTournyList;
import com.poker.common.message.ResponseTournyMyTable;
import com.poker.common.message.TournyEvent;
import com.poker.game.PokerGameType;


public class ServerProxy implements Runnable {

  
/***
   * Player's static identity declaration
   */
  static public String _password;
  static public String _name, _avatar, _city;
  static boolean _authenticated, _dummy_login = false;
  static boolean _dead;
  static long _last_read_time;
  static long _last_write_time;
  public int _disconnect_count;
  public BlinkLabel bl = null;
  static double _play_worth, _real_worth;
  static int _type;
  static int _preferences = -1;
  static public int _gender = -1;
  static long HTBT_INTERVAL = 30000;
  static long REFRESH_INTERVAL = 5000;
  public static ResponseTableList _rtl = null; //last table list update
  public static ResponseTournyList _rtyl = null; //last table list update
  public static ClientSettings _settings = null;
  static Component _owner;
  //for loading Player comments into a MAP
  //static ClientRoom _clientRoom;
  public static long _server_time;
  public static int _active_tables, _active_players;
  boolean _deadDialogBox = false;

  //String _poll = "Holdem";
  //resize code
  //private GameEvent global_ge=null;

  // End player info declaration

  /**
   * Connection handling declaration
   */
  static Logger _cat = Logger.getLogger(ServerProxy.class.getName());
  static Object _dummy = new Object();
  static Selector _selector;
  static Command _out;
  static SocketChannel _channel;
  static String _session = null;
  static ByteBuffer _h, _b, _o;
  static int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
  static String _comstr;
  static Vector _waiters;
//  public static LobbyTableModel gameList[];
 // public static LobbyTournyModel tournyList[];
  
  // End connection handling declaration
  public static Timer _rtlTimer ;
  static ServerProxy _serverProxy;
  static ConcurrentHashMap<String, ActionFactory> _action_registry;
//  public SNGTournyLobby sngTournyLobby = null;
  static boolean keepLooking = true;
  
  public void setOwner(Component jf) {
    _owner = jf;
  }


  private ServerProxy(String ip, int port) throws Exception {
    try{
    	_channel = SocketChannel.open(new InetSocketAddress(ip, port));
	    _channel.configureBlocking(true);
	    _session = connect(_channel);
	    _selector = Selector.open();
	    _action_registry = new ConcurrentHashMap();
	    _waiters = new Vector();
	    keepLooking=true;
    }catch (Exception e) {
    	_cat.finest("Socket Connection failed for "+ip+" and port: "+port);
    	e.printStackTrace();
	}
  }

  public static ServerProxy getInstance(String ip, int port, Component jf) throws Exception {
    if (_serverProxy == null) {
      synchronized (_dummy) {
        if (_serverProxy == null) {
          _serverProxy = new ServerProxy(ip, port);
        }
      }
    }
    _serverProxy.setOwner(jf);
    return _channel != null?_serverProxy:null;
  }

  public static ServerProxy getInstance() {
    assert _serverProxy != null:"Server Proxy is not appropriately initialized"; return
        _serverProxy;
  }
  
  
  
    public static void killInstance() {
      try {
         keepLooking=false;
         _channel.close();
         _selector.close();
         _channel=null;
         _selector=null;
         _serverProxy=null;
      }
      catch (Exception e){
          e.printStackTrace();
      }
     }

  void reset(int tid) {
    ((ActionFactory) _action_registry.get(tid)).reset();
  }

  public ActionFactory getAF(String tid) {
	if(tid == null)return null;
	return ((ActionFactory) _action_registry.get(tid));
  }
  
  /*public void moveAF(String oldtid, String newtid){
	  ActionFactory af = ((ActionFactory) _action_registry.get(oldtid));
	  _action_registry.put(newtid, af);
	  _action_registry.remove(oldtid);
  }
*/
  public void createActionFactory(String tid, int pos) {
    ActionFactory af = getAF(tid);
    if (af == null) {
      // create a action registry entry
      af = new ActionFactory(tid);
      af._joined = true;
      af._pos = pos;
      _action_registry.put("" + tid, af);
      _cat.fine("Received the details for a table with presence");
    }

  }

  public void createActionFactory(String tid) {

    ActionFactory af = getAF(tid);
    if (af == null) {
      // create a action registry entry
      af = new ActionFactory(tid);
      _action_registry.put("" + tid, af);
      _cat.fine("Received the details for a table without presence");
    }

  }
  
//by rk
  public void removeActionFactory(String tid){
	  _action_registry.remove(tid);
  }
	  
  public String connect(SocketChannel _channel) throws Exception {
    Command ge = new Command("null", Command.C_CONNECT);
    _cat.finest("Connect req " + ge.toString());
    write(ge.toString());
    String s = readFull();
    _cat.finest("Connect resp " + s);
    Response r = new Response(s);
    //System.out.println(r.toString());
    if (r._response_name == Response.R_CONNECT && r.getResult() == Response.E_IP_REUSE) {
        JOptionPane.showMessageDialog(_owner,
                   "there is already a login from your ip", "INFO",
           JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
       }
    _authenticated = false;
    return r.session();
  }

  public Response login(String name, String pass) throws Exception {
    _name = name;
    _password = pass;
    _channel.configureBlocking(true);
    String ver = SplashWindow.VERSION_LABEL;
    String verNum = ver.substring(ver.indexOf("v")+1).replace(".","");
    //System.out.println(verNum);
    CommandLogin ge = new CommandLogin(_session, _name, _password, verNum,
                                       "admin");
    _cat.finest(_name + " Login Command " + ge);
    write(ge.toString());
    _last_write_time = System.currentTimeMillis();
    ResponseFactory rf = new ResponseFactory(readFull());
    Response gr = (Response) rf.getResponse();
    //System.out.println("Login Response from server "+gr.toString());
    if (gr.getResult() == 1 || gr.getResult() == 12) {
       	 
    	if(((ResponseLogin)gr).getRealLossLimit() == 1.0){
       		 JOptionPane.showMessageDialog(_owner,
                        "Your are using old client version. Please update Your client", "INFO",
                    		JOptionPane.INFORMATION_MESSAGE);
       		 System.exit(-1);
       	 }
       	 
    	
    	
      _play_worth = ((ResponseLogin) gr).getPlayWorth();
      _real_worth = ((ResponseLogin) gr).getRealWorth();
      _city = ((ResponseLogin) gr).getCity();
      _gender = ((ResponseLogin) gr).getGender();
      _avatar = ((ResponseLogin) gr).getAvtar();
      _preferences = ((ResponseLogin) gr).getPreferences();
        loadClientSettings();
      _authenticated = true;
      _cat.finest(_name + " Login Response " + gr);
      _channel.configureBlocking(false);
      setSelectorForRead();
      // start the event thread
      Thread _runner = new Thread(this);
      _runner.start();
      startTLThread();
      return gr;
    }
    return null;
  }
  
  public void dummyLogin() throws Exception {

	_authenticated = true;
	_name = "Guest";
	_avatar = "1";
	startWatch(null);
	startTLThread();
	startRefreshTableListThread();
	ping();
	    
  }

  public void startWatch(GameEvent ge) throws Exception {
    _last_write_time = System.currentTimeMillis();
      _channel.configureBlocking(false);
      setSelectorForRead();
      _preferences = CredentialManager.getPreferences();
      loadClientSettings();
      // start the event thread
      Thread _runner = new Thread(this);
      _runner.start();
  }

  public void logout() throws Exception {
    Command ge = new Command(_session, Command.C_LOGOUT);
    _cat.finest(_name + " Logout Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  public void run() {
    _cat.finest("Starting the run loop");
    ServerProxy p = null;
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
            p = (ServerProxy) key.attachment();
            
            String response = p.read();
            p = null;//by rk, for GC
            key = null;//by rk, for GC
            if (response == null) {
              continue;
            }
            ResponseFactory rf = new ResponseFactory(response);
            Response r = rf.getResponse();
            rf = null;//by rk, for GC
            if(r._response_name != Response.R_TABLELIST && r._response_name != Response.R_TOURNYLIST && r._response_name != Response.R_PING
            		&& r._response_name != Response.R_TOURNYDETAIL)
            {
            	_cat.fine(r.toString());
            }
            if (r._response_name == Response.R_TABLELIST) {
            	_rtl = (ResponseTableList) r;
            	Vector<GameEvent> totallist = new Vector<GameEvent>();
//            	_cat.severe("TABKELIST RESPONSE ");
            	//_cat.severe("TABKELIST RESPONSE= "+_rtl.getGameCount()+", "+_rtl.toString());
            	for (int v = 0; v < _rtl.getGameCount(); v++) {
            		GameEvent ge = _rtl.getGameEvent(v);
            		totallist.add(ge);
            		ge = null;//by rk, for GC
            	}
            	int m=0;
            	LobbyTableModel[] gameList = new LobbyTableModel[_rtl.getGameCount()];//by rk gameList made local
                for (Iterator<GameEvent> l=totallist.iterator(); l.hasNext();) {
                  GameEvent ge = l.next();
                  gameList[m++] = new LobbyTableModel(ge);
                  ge = null;//by rk, for GC
//                  if(!"none".equals(ge.getPlayerDetailsString()))
//                      System.out.println(ge.getPlayerDetailsString());
                }
                totallist.clear();//by rk, for GC
                totallist = null;//by rk, for GC
            
                 fireTableChangeEvent(gameList);
                 gameList = null;//by rk, for GC
                 //System.out.println("Sit n Go: "+gameList.length);
            }
            else if (r._response_name == Response.R_TOURNYLIST) {
            	_rtyl = (ResponseTournyList) r;
            	Vector<TournyEvent> tlist = new Vector<TournyEvent>();
            	for (int v = 0; v < _rtyl.getTournyCount(); v++) {
            		//TournyEvent te = _rtyl.getTourny(v);
            		//te.get
            		//PokerGameType pgt = new PokerGameType(te.get("name"));
            		tlist.add(_rtyl.getTourny(v));
            	}
            	LobbyTournyModel[] tournyList = new LobbyTournyModel[tlist.size()];
                int m=0;
                for (Iterator<TournyEvent> l=tlist.iterator(); l.hasNext();m++) {
                	TournyEvent te = l.next();
                	tournyList[m] = new LobbyTournyModel(te);
                }
                tlist.clear();//by rk, for GC
                tlist = null;//by rk, for GC
                fireTournyChangeEvent(tournyList); // update tournament table list listeners
                tournyList = null;//by rk for GC
           }
            else if (r._response_name == Response.R_TABLE_OPEN){
              //spawn this table
              ResponseTableOpen rto = (ResponseTableOpen)r;
              _cat.finest("R_TABLE_OPEN "+r.toString());
              String tid = rto.getTableName();
              int pos = rto.getPosition();
              String gev = rto.getGE();
              if(!"".equals(gev.trim())){
	              GameEvent ge = new GameEvent(gev);
	              ClientRoom cr = new ClientRoom(_serverProxy,ge, new JFrame());
	              LobbyUserImp.vTPokerRooms.add(cr);
	              ActionFactory af = getAF(tid);
	              if (af == null) {
	                // create a action registry entry
	                af = new ActionFactory(tid);
	                _action_registry.put("" + tid, af);
	                _cat.log(Level.FINE, "Received the details for a table without presence");
	              }
	              af.processGameEvent(ge, Response.R_TABLEDETAIL);
	              _serverProxy.addServerActionListener(tid, cr.getClientPokerController());
	              _serverProxy.addServerMessageListener(tid, cr.getClientPokerController());
	              ge = null;//by rk, for GC
              }else{
            	  JOptionPane.showMessageDialog(_owner,
                          "Please try again to join pool", "INFO",
                      		JOptionPane.INFORMATION_MESSAGE);
              }
            }
            else if (r._response_name == Response.R_TABLE_CLOSED){
            	ResponseString rs = (ResponseString)r;
            	String tid = rs.getStringVal();
            	ActionFactory af = getAF(tid);
            	if(af != null)
            	{
            		af.closeRoom();
            		System.out.println("R_TABLE_CLOSED closed AF for "+tid);
            	}
                _cat.severe("table is closed");
            }
            else if (r._response_name == Response.R_TABLE_OPEN_CLOSE){
                ResponseTableCloseOpen rto = (ResponseTableCloseOpen)r;
                _cat.finest("Response when R_TABLE_OPEN_CLOSE = "+rto.toString());
                String old_tid = rto.getCloseTableName();
                createActionFactory(rto.getOpenTableName(), rto.getPosition());
                	GameEvent ge = new GameEvent(rto.getGE());
	               // int pos = rto.getPosition();
	                ActionFactory af = getAF(old_tid);
	                if (af != null) {
	                	af.closeOpenRoom(old_tid, ge);
	                }
	                ge = null;//by rk, for GC
            }
            else if (r._response_name == Response.R_TOURNYDETAIL) {
            	//System.out.println("R_TOURNYDETAIL: "+r.toString());
            	ResponseTournyDetail rtd = (ResponseTournyDetail) r;
            	//System.out.println(rtd.toString());
                TournyEvent ge = new TournyEvent(rtd.getTournyEvent());
                // check if user has presence on this table
                try {
                  fireTournyMessagesEvent(ge.name(), rtd);
                }catch (Exception e){
                  // THE tourny does not exists
                	e.printStackTrace();
                  JOptionPane.showMessageDialog(_owner,
                                                "The tourny is over !.", "ERROR",
                                        JOptionPane.ERROR_MESSAGE);
                }
              }
              else if (r._response_name == Response.R_TOURNYREGISTER) {
            	  if (r.getResult() == Response.E_ALREADY_REGISTERED) {
            		  JOptionPane.showMessageDialog(_owner,
                              "Already Registered", "WARNING",
                      JOptionPane.WARNING_MESSAGE);
            	  }
            	  else if (r.getResult() == Response.E_REGISTERATION_CLOSED) {
            		  JOptionPane.showMessageDialog(_owner,
                              "Registrations Closed", "INFO",
                      JOptionPane.INFORMATION_MESSAGE);
            	  }
            	  else if (r.getResult() == Response.E_SUCCESS) 
            	  {
            		  JOptionPane.showMessageDialog(_owner,
                              "Successfully Registered", "INFO",
                      JOptionPane.INFORMATION_MESSAGE);
//	                ResponseTournyDetail rtd = (ResponseTournyDetail) r;
//	                fireTournyMessagesEvent(rtd.getTournyEvent(), rtd);
            	  }
            	  else if (r.getResult() == Response.E_FAILURE) 
            	  {
            		  JOptionPane.showMessageDialog(_owner,
                              "Registration Failed", "INFO",
                      JOptionPane.INFORMATION_MESSAGE);
	                
            	  }
              }
              else if (r._response_name == Response.R_TOURNYMYTABLE) {
            	  //System.out.println("Response R_TOURNYMYTABLE= " + r);
            	  if (r.getResult() == Response.E_FAILURE) 
            	  {
            		JOptionPane.showMessageDialog(_owner,
                              "You are not Registered in Tournament", "INFO",
                    JOptionPane.INFORMATION_MESSAGE);
	              }
            	  else
            	  {
            		ResponseTournyMyTable rtd = (ResponseTournyMyTable) r;
	                // check if user has presence on this table
	                //System.out.println("R_TOURNYMYTABLE "+rtd.toString());
	                fireTournyMessagesEvent(rtd.getGameTid().substring(0,rtd.getGameTid().indexOf("-")), rtd);
            	  }
              }
              else if (r._response_name == Response.R_PLAYER_REMOVED){
                ResponseString rtd = (ResponseString) r;
                _cat.fine("Removing player");
                ActionFactory af = getAF(rtd.getStringVal());
                af.addAction(new TableServerAction(ActionConstants.PLAYER_LEAVE, af._pos));
                System.out.println("You are removed from this table, please close the window");
          	  //fireServerMessageEvent("You are removed from this table, please close the window");
              }
              else if (r._response_name == Response.R_TABLEDETAIL) 
              {
                if(r.getResult()== Response.E_NONEXIST){
                    JOptionPane.showMessageDialog(_owner,
                        "This game does not exist on the server, please refresh your game list.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
                  _cat.fine("Game does not exists on the server");
                  System.exit(-1);
                }
              ResponseTableDetail rtd = (ResponseTableDetail) r;
              GameEvent ge = new GameEvent();
              ge.init(rtd.getGameEvent());
              // check if user has presence on this table
              String tid = ge.getGameName();
              if(tid != null)
              {
	              ActionFactory af = getAF(tid);
	              if (af == null) {
	                // create a action registry entry
	                af = new ActionFactory(tid);
	                _action_registry.put("" + tid, af);
	               // _cat.log(Level.WARNING, "Received the details for a table without presence");
	              }
	              //af.processGameEvent(ge, Response.R_TABLEDETAIL);
	              af.resizeProcessGameEvent(ge, Response.R_TABLEDETAIL);
	              ge = null;//by rk, for GC
              }
            }
            else if (r._response_name == Response.R_MOVE) {
              //_cat.info(r.toString());
              if (r.getResult() == Response.E_BROKE) {
                ResponseString rint = (ResponseString) r;
                String tid = rint.getStringVal();
                ActionFactory af = getAF(tid);
                Object[] a = new Object[1];
                a[0] = new ErrorAction(ActionConstants.UNSUFFICIENT_FUND);
                if (af != null) {
                  fireServerActionEvent(tid, a);
                }
              }
              else if (r.getResult() == Response.E_OVER_SPENDING){
                ResponseString rint = (ResponseString) r;
                String tid = rint.getStringVal();
                ActionFactory af = getAF(tid);
                Object[] a = new Object[1];
                a[0] = new ErrorAction(ActionConstants.UNSUFFICIENT_FUND);
                if (af != null) {
                	fireServerActionEvent(tid, a);
                }
              }
              else if (r.getResult() == Response.E_GAME_NOT_ALLOWED){
  //           	fireServerMessageEvent("");
            	  System.out.println(r.toString());
             	JOptionPane.showMessageDialog(_owner,
                         "Unable to find a suitable table in the pool !, Please close table", "ERROR",
                         JOptionPane.ERROR_MESSAGE);
             	
             	_cat.log(Level.FINE, "Unable to find a suitable table in the pool !");
             }
              else if (r.getResult() == Response.E_POS_NOT_ALLOWED){
            	  //           	fireServerMessageEvent("");
            	             	JOptionPane.showMessageDialog(_owner,
            	                         "Position not allowed", "ERROR",
            	                         JOptionPane.ERROR_MESSAGE);
            	             	
            	             	_cat.log(Level.INFO, "Position not allowed");
            	             }
              else if (r.getResult() == Response.E_ALREADY_LOGGED){
            	  //           	fireServerMessageEvent("");
            	        System.out.println(r.toString());
//            	             	JOptionPane.showMessageDialog(_owner,
//            	                         "Wait for server response", "ERROR",
//            	                         JOptionPane.ERROR_MESSAGE);
            	             	
            	             	_cat.log(Level.INFO, "E_ALREADY_LOGGED");
            	             }
              else {
            	ResponseGameEvent rge = (ResponseGameEvent) r;
                GameEvent ge = new GameEvent();
                ge.init(rge.getGameEvent());
                String tid = ge.getGameName();
                ActionFactory af = getAF(tid);
                //resize code
                //global_ge=ge;
                if (af != null) {
                	//System.out.println("\nMOVE to "+af.getTid());
                  af.processGameEvent(ge, Response.R_MOVE);
                }
                ge = null;//by rk, for GC
              }
            }
            else if (r._response_name == Response.R_TABLEPING) {
            	ResponseTablePing rtp = (ResponseTablePing) r;
            	GameEvent ge = new GameEvent();
            	_cat.finest("TABLEPING "+rtp.toString());
                ge.init(rtp.getGameEvent());
                String tid = ge.getGameName();
            	fireSNGTournyMessagesEvent(tid, rtp);
            	ge = null;//by rk, for GC
            }
            else if (r._response_name == Response.R_MESSAGE) {
                final ResponseMessage rm = (ResponseMessage) r;
                Object o = new Object[1];
                if (rm.getType().equals("chat")) 
                {
	                o = new ChatAction(rm.getMessage());              
	                String tid = rm.getGameId();
	                fireServerActionEvent(tid, o);
	            }
	            else if(rm.getType().equals("broadcast")){
	            	o = new MessageAction(rm.getMessage());   
	            	if(rm.getGameId() == null)
	            	{
	            		System.out.println("broadcast with no tid "+rm.getMessage());
	            		firebroadcastMessageEvent(rm.getMessage());
	            	}
	            	else
	            	{
	            		System.out.println("broadcast with tid "+rm.getGameId());
	            		String tid = rm.getGameId();
		                fireServerMessageEvent(tid,rm.getMessage());
	            	}
	            }
	            else if(rm.getType().equals("lobby"))
	            {
	            	//fireServerMessageEvent(rm.getGameId(), rm.getMessage());
	            	/** by rk */
	            	String tid = rm.getGameId();
	            	 ActionFactory af = getAF(tid);
	                 if (af != null) {
	                 	af.showMessage(tid, rm.getMessage());
	                 }
	            	fireServerActionEvent(rm.getGameId(), new MessageAction(rm.getMessage()));

	            	//R_MESSAGE: CSID=8930957908366853784&RNAME=16&CR=1&
	            	//GM=message=message=WW91IGFyZSBtb3ZlZCB0byBlbXB0eSB0YWJsZSBiZWNhdXNlIHlvdSByYW4gb3V0IG9mIG1vbmV5 LiBZb3UgY2FuIGpvaW4gYWdhaW4gIQ== ,
	            	//name=01 Fun Diamond Pool,
	            	//type=lobby 
	            	
	            	/*JOptionPane.showMessageDialog(_owner,
	                		rm.getMessage(), "INFO",
	                        JOptionPane.INFORMATION_MESSAGE);*/
	            	
		        	
	            }
            }
            else if (r._response_name == Response.R_BUYCHIPS) {
              ResponseBuyChips rbc = (ResponseBuyChips) r;
              _cat.finest("Buy chips response  " + rbc);
              if (r.getResult() == 1) {
            	_real_worth = rbc.getRealWorth();
                _play_worth = rbc.getPlayWorth();
              }
              else {
                fireLobbyInfoListener(new ErrorAction(ActionConstants.
                    UNSUFFICIENT_FUND));
                _cat.log(Level.WARNING, "Buy Chip failed");
              }
            }
            else if (r.getResult() == Response.E_BUYIN_NOT_ALLOWED_BETWEEN_GAMES){
 //           	fireServerMessageEvent("Buychips not allowed between games");
            	JOptionPane.showMessageDialog(_owner,
                        "Buychips not allowed between games", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            	_cat.log(Level.WARNING, "Buy Chips not allowed between games");
            }
            else if (r._response_name == Response.R_GET_CHIPS_INTO_GAME) {
              ResponseGetChipsIntoGame rbc = (ResponseGetChipsIntoGame) r;
              _cat.fine("Get chips into game response  " + rbc);
              
              if (r.getResult() == 1) {
            	  _real_worth = rbc.getRealWorth();
                  _play_worth = rbc.getPlayWorth();
                  String tid = rbc.getGameId();
                  ActionFactory af = getAF(tid);
                  af.processCashierAction(new CashierAction(af._pos, rbc.getAmount()));
                }
                else {
                	
                  fireLobbyInfoListener(new ErrorAction(ActionConstants.UNSUFFICIENT_FUND));
                  _cat.log(Level.FINE, "Get Chips into game failed");
                }
            }
            else if (r._response_name == Response.R_LOGOUT) {
              _cat.fine("Logged out " + r.toString()); 
//              if(r.getResult() == Response.E_LOGGED_IN_AT_DIFF_LOCATION)
//              fireServerMessageEvent("You have opened another table, Currently the software supports only single poker table.");
              JOptionPane.showMessageDialog(_owner,
                        "You have opened another table, Currently the software supports only single poker table.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
              //key.cancel();
              System.exit(-1);
            }
            else if (r._response_name == Response.R_WAITER) {
            	 _cat.fine(" Waiting response " + r.toString());
                 Response rint = (Response) r;
              _cat.fine(" Waiting response " + rint);
              if (rint.getResult() == Response.E_PARTIALLY_FILLED) {
                _cat.log(Level.WARNING, "Seats are available");
                JOptionPane.showMessageDialog(_owner,"Seats are available", "INFO",
                                      JOptionPane.INFORMATION_MESSAGE);
              }
              else {
                _cat.fine("Successfuly added to the waiting list");
                fireLobbyInfoListener(new ErrorAction(ActionConstants.
                    ADD_TO_WAITERS));
              }
            }
            else if (r._response_name == Response.R_DONT_MUCK) {
                ResponseString rs = (ResponseString) r;
                if(rs.getResult() == 1){
                	_settings.setMuckLosingCards(false);
                }
            	_cat.fine("R_DONT_MUCK "+rs.toString());
            }
            else if (r._response_name == Response.R_MUCK_CARDS) {
                ResponseString rs = (ResponseString) r;
                if(rs.getResult() == 1){
                	_settings.setMuckLosingCards(true);
                }
            	_cat.fine("R_MUCK_CARDS= "+rs.toString());
            	
            }
            else if (r._response_name == Response.R_PING) {
                ResponsePing rs = (ResponsePing) r;
            	//_cat.info(rs.toString());
                _cat.finest("PING "+rs.toString());
            	_server_time = rs.getTime();
            	_active_players = rs.getActivePlayers();
            	_active_tables = rs.getActiveTables();
            	firePingDetailsEvent(_server_time, _active_tables, _active_players);
            }
            else if (r._response_name == Response.R_SIT_IN) {
            	ResponseString rs = (ResponseString) r;
             _cat.finest(rs.toString());
             String tid = rs.getStringVal();
                ActionFactory af = getAF(tid);
                af.sitin();

            }
            else if (r._response_name == Response.R_SIT_OUT) {
                ResponseString rs = (ResponseString) r;
             _cat.finest(rs.toString());
             String tid = rs.getStringVal();
                ActionFactory af = getAF(tid);
                af.sitout();

            }
//            else if (r._response_name == Response.R_PREFERENCES) {
//            	System.out.println(r.toString());
//            	ResponseInt gr = new ResponseInt(1, Response.R_PREFERENCES, r.getResult());
//                
//
//            }
            else {
              _cat.log(Level.WARNING, 
                  "SHILL: FATAL: Unknown event received from the server " +
                  r._response_name);
            }
            r = null;//by rk, for GC
          }
        }
        // go thru action registries
        Enumeration en = _action_registry.elements();
        while (en.hasMoreElements()) {
          ActionFactory af = (ActionFactory) en.nextElement();
          Object ac = af.fetchAction();
          fireServerActionEvent(af.getTid(), ac);
//          if(ac != null){
//        	  String act = ac.getClass().getName();
//        	  if(ac instanceof Action){
//        		 act = Action.actionToString(((Action)ac));
//        	  }else if(ac instanceof GameEvent){
//        		  act = ((GameEvent)ac).toString();
//        	  }
//        	  _cat.severe("ACTION "+act);
//          }
        }
        Thread.currentThread().sleep(10);
      }
      catch (Throwable ex) {
        ex.printStackTrace();
        _cat.log(Level.FINE, "Exceptional condition, will continue ", ex);
        // ignore
      }
    }
  }//run() close


  /**************************************************************************
   * Various listeners and related methods
   **************************************************************************/

  private ConcurrentHashMap serverActionListener = new ConcurrentHashMap();
  private List lobbyModelChangeListener = new ArrayList();
  private List lobbyInfoListener = new ArrayList();
  private List pingDetailsListener = new ArrayList();
  private List broadcastMessageListener = new ArrayList();
  private ConcurrentHashMap serverMessageListener = new ConcurrentHashMap();
  protected ConcurrentHashMap tournyMessagesListener = new ConcurrentHashMap();
  protected ConcurrentHashMap sngTournyMessagesListener = new ConcurrentHashMap();
  
  /**
   * Adds LobbyModelsChangeListener.
   */
  public final void addLobbyModelChangeListener(LobbyModelsChangeListener
                                                changesListener) {
    synchronized (lobbyModelChangeListener) {
      lobbyModelChangeListener.add(changesListener);
    }
  }

  /**
   * Removes LobbyModelsChangeListener.
   */
  public final void removeLobbyModelChangeListener(LobbyModelsChangeListener
      changesListener) {
    synchronized (lobbyModelChangeListener) {
      lobbyModelChangeListener.remove(changesListener);
    }
  }

  /**
   * Notifies all listeners.
   */
  
    
  public void fireTableChangeEvent(LobbyTableModel[] changes) {
	    synchronized (lobbyModelChangeListener) {
	      for (Iterator i = lobbyModelChangeListener.iterator(); i.hasNext(); ) {

	        LobbyModelsChangeListener listner
	            = (LobbyModelsChangeListener) i.next();
	        listner.tableListUpdated(changes);
	      }
	    }
	  }
  
  /**
   * Notifies all listeners.
   */
  private void fireTournyChangeEvent(LobbyTournyModel[] changes) {
    synchronized (lobbyModelChangeListener) {
      for (Iterator i = lobbyModelChangeListener.iterator(); i.hasNext(); ) {

        LobbyModelsChangeListener listner
            = (LobbyModelsChangeListener) i.next();
        //System.out.println("sp "+changes.length);
        listner.tournyListUpdated(changes);
      }
    }
  }
  /**
   * Adds LobbyInfoChangeListener.
   */
  public final void addLobbyInfoListenerListener(LobbyInfoListener
                                                 changesListener) {
    synchronized (lobbyInfoListener) {
      lobbyInfoListener.add(changesListener);
    }
  }

  /**
   * Removes LobbyInfoChangeListener.
   */
  public final void removeLobbyInfoListenerListener(LobbyInfoListener
      changesListener) {
    synchronized (lobbyInfoListener) {
      lobbyInfoListener.remove(changesListener);
    }
  }
  /**
   * Notifies LobbyInfoListener listener.
   */
  private void fireLobbyInfoListener(Action resp) {
    synchronized (lobbyInfoListener) {
      for (Iterator i = lobbyInfoListener.iterator(); i.hasNext(); ) {

        LobbyInfoListener listner
            = (LobbyInfoListener) i.next();
        listner.serverLobbyResponse(resp);
      }
    }
  }
  
  
  
  /**
   * Adds PingDetailsListener.
   */
  public final void addPingDetailsListener(PingDetailsListener
                                                 changesListener) {
    synchronized (pingDetailsListener) {
    	pingDetailsListener.add(changesListener);
    }
  }

  /**
   * Removes PingDetailsListener.
   */
  public final void removePingDetailsListener(PingDetailsListener
      changesListener) {
    synchronized (pingDetailsListener) {
    	pingDetailsListener.remove(changesListener);
    }
  }
  /**
   * Notifies PingDetailsListener listener.
   */
  public void firePingDetailsEvent(long time, int at, int ap) {
	  synchronized (pingDetailsListener) {
	      for (Iterator i = pingDetailsListener.iterator(); i.hasNext(); ) {
	    	  
	    	  PingDetailsListener listner
	            = (PingDetailsListener) i.next();
	        listner.pingDetailsReceived(time, at, ap);
	        
	      }
	  }
  }
  
  
  /**
   * Adds broadcastMessageListener.
   */
  public final void addbroadcastMessageListener(broadcastMessageListener
                                                 changesListener) {
    synchronized (broadcastMessageListener) {
    	broadcastMessageListener.add(changesListener);
    }
  }

  /**
   * Removes broadcastMessageListener.
   */
  public final void removebroadcastMessageListener(broadcastMessageListener
      changesListener) {
    synchronized (broadcastMessageListener) {
    	broadcastMessageListener.remove(changesListener);
    }
  }
  /**
   * Notifies PingDetailsListener listener.
   */
  public void firebroadcastMessageEvent(String msg) {
	  synchronized (broadcastMessageListener) {
	      for (Iterator i = broadcastMessageListener.iterator(); i.hasNext(); ) {
	    	  
	    	  broadcastMessageListener listner
	            = (broadcastMessageListener) i.next();
	        listner.broadcastMessageReceived(msg);
	        
	      }
	  }
  }
  
  /**
   * Add ServerMessageListener. for messageBanner
   */
  public final void addServerMessageListener(String tid, ServerMessagesListener changesListener) {
	serverMessageListener.put(tid,changesListener);
  }
  
  public final void removeServerMessageListener(String tid) {
	serverMessageListener.remove(tid);
  }
  
  
  /**
   * Notifies ServerMessageListener listener.
   */
  private void fireServerMessageEvent(String tid,String message) 
  {
	
		ServerMessageListener listner = (ServerMessageListener) serverMessageListener.get(tid);
		listner.serverMessageReceive(message);
		
  }
  /**
   * Add ServerMessageListener. 
   */
  public final void addServerActionListener(String tid,
    ServerMessagesListener changesListener) {
	//_cat.info("Adding watch on table = " + tid);
	serverActionListener.put(tid, changesListener);
	try {
		watchOnTable(tid);
	}
	catch (Exception e) {
		_cat.log(Level.FINE, "Exception while adding server message listener ", e);
	}
  }
//by rk
  public void removeServerActionListener(String tid){
	  serverActionListener.remove(tid);
  }
  /**
   * Notifies ServerMessageListener listener.
   */
  private void fireServerActionEvent(String tid, Object actions) {
	if (actions == null) {
		return;
	}
	ServerMessagesListener listner = (ServerMessagesListener)
	  serverActionListener.get(tid);
	if(listner == null)return;
	listner.serverMessageReceived(tid, actions);
	if(actions instanceof  com.onlinepoker.actions.Action){
		com.onlinepoker.actions.Action action = 
            (com.onlinepoker.actions.Action)actions;
//		_cat.severe("featching ACTION from QUEUE "+tid+" - "+action.getTarget()+" - "
//						+action.actionToString(action.getId()));
	}
  }

  
  public double moneyInPlay()
  {
	  Enumeration en = _action_registry.elements();
      double money_at_table = 0;
      //System.out.println();
      while (en.hasMoreElements()) {
        ActionFactory af = (ActionFactory) en.nextElement();
        fireServerActionEvent(af.getTid(), af.fetchAction());
 //       System.out.println("action factory "+af.getTid()+", "+af._type);
        if(af._type != null ){
        	money_at_table += af._money_at_table;
        	//System.out.println("money at table: "+af.getTid()+", "+af._money_at_table+", dealer pos "+af._dealer_pos);
        	//System.out.println(af.getTid()+" - "+af._joined);
        }
      }
	  return money_at_table;
  }
  
  public void stopWatchOnTable(String tid) {
    try {
      removeObserver(tid);
      ActionFactory ar = getAF(tid);
      //ar.reset();
      _action_registry.remove(tid);
    }
    catch (Exception e) {
    	
      _cat.log(Level.FINE, "Unable to stop watch on table", e);
    }
  }

  public void stopWatchOnTable() {
    try {
      for (Enumeration enumer = _action_registry.elements();
                                enumer.hasMoreElements(); ) {
        ActionFactory af = (ActionFactory) enumer.nextElement();
        removeObserver(af._tid);
        _action_registry.remove(af._tid);
      }
    }
    catch (Exception e) {
      _cat.log(Level.FINE, "Unable to stop watch on table", e);
    }
  }

  public void watchOnTable(String tid) {
    try {
      addObserver(tid);
    }
    catch (Exception e) {
      _cat.log(Level.FINE, "Unable to start watch on table", e);
    }
  }
  
  public void addWatchOnTourny(String tid, TournyMessagesListener changesListener) {
    try {
      tournyDetails(tid);
      addTournyMessageListener(tid, changesListener);
    }
    catch (Exception e) {
      _cat.fine("Unable to start watch on table"+  e);
    }
  }
  
  public void removeWatchOnTourny(String tid) {
    try {
      removeObserver(tid);
      removeTournyMessageListener(tid);
    }
    catch (Exception e) {
      _cat.fine("Unable to stop watch on table"+  e);
    }
  }
  
  public final void addTournyMessageListener(String tid,
	          TournyMessagesListener changesListener) {
	synchronized (tournyMessagesListener) {
		tournyMessagesListener.put(tid, changesListener);
	}
  }
  
  public final void removeTournyMessageListener(String tid) {
	synchronized (tournyMessagesListener) {
		tournyMessagesListener.remove(tid);
	}
  }
  
  protected void fireTournyMessagesEvent(String tid, Object actions) {
	    if (actions == null) {
	      return;
	    }
       
	    TournyMessagesListener listner = (TournyMessagesListener)
	                                     tournyMessagesListener.get(tid);
	    listner.tournyMessageReceived(actions);
  }
  
  public void addWatchOnSNGTourny(String tid, SNGTournyMessagesListener changesListener) {
    try {
      
      addSNGTournyMessageListener(tid, changesListener);
    }
    catch (Exception e) {
      System.out.println("Unable to start watch on table"+  e);
    }
  }
  public final void addSNGTournyMessageListener(String tid, SNGTournyMessagesListener changesListener) {
	synchronized (sngTournyMessagesListener) {
		sngTournyMessagesListener.put(tid, changesListener);
		try {
			watchOnTable(tid);
		}
		catch (Exception e) {
			_cat.log(Level.FINE, "Exception while adding sng tourny message listener ", e);
		}
	}
  }
  protected void fireSNGTournyMessagesEvent(String tid, Object actions) {
    if (actions == null) {
      return;
    }
    SNGTournyMessagesListener listner = (SNGTournyMessagesListener)
                                     sngTournyMessagesListener.get(tid);
    listner.sngTournyMessageReceived(actions);
  }
  
  
  


  /***************************************************************************
   * NEW METHODS TO BE RESOLVED
   ***************************************************************************/
  /**
   * Load client settings from persistent store.
   * @return ClientSettings - player preferences.
   */
  public ClientSettings loadClientSettings() {
    _cat.finest("Client Settings preferences " + PlayerPreferences.stringValue(_preferences));
    _settings = new ClientSettings(_preferences);
    return _settings;
  }


  /**
   * Store client settings in persistent store.
   * @param settings - player preferences.
   */
  public void storeClientSettings(ClientSettings settings) {
	updateClientSettingsAtServer(settings.intVal());
    _settings = settings;
  }

  public double realWorth() {
    return _real_worth;
  }

  public double playWorth() {
    return _play_worth;
  }
  
  public double getWorth(){
      if (new PokerGameType(_type).isReal()){
          return _real_worth;
      }
      else {
          return _play_worth;
      }
  }
  //resize code
  public void gameProcess(GameEvent ge){
	  ActionFactory af = getAF(ge.getGameName());
      if (af != null) {
        af.resizeProcessGameEvent(ge, Response.R_MOVE);
      }
  }
  //commented by rk, it cause table hang when plr open 2 tables and try to resize one table
//  public GameEvent getGlobal_ge(){
//	  return global_ge;
//  }
//  
//  public void setGlobal_ge(GameEvent ge){
//	  this.global_ge = ge;
//  }
  public String getTicket() {
    return _session;
  }

  public String getAd() {
    new Exception().printStackTrace();
    return "";
  }

  public boolean isWait(String tid) {
    if (_waiters.contains(tid)) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean isLoggedIn() {
    return _authenticated;
  }

  public boolean isRegistered() {
    new Exception().printStackTrace();
    return true;
  }

  public void leaveTable(String tid) {
	CommandMove ge = new CommandMove(_session, CommandMove.M_LEAVE, 0, tid);
	//new Exception(_name+" from table "+tid).printStackTrace();
    _cat.finest(_name + " Leave Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void waitTable(String tid) {
    //new Exception().printStackTrace();
    CommandString ge = new CommandString(_session, CommandMove.C_WAITER, tid);
    _cat.finest(_name + " Wait Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void sitOutTable(String tid) {
	  System.out.println("sp.sitoutTable");
    CommandString ge = new CommandString(_session, Command.C_SIT_OUT, tid);
    _cat.finest(_name + " Move Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void sitInTable(String tid) {
    CommandString ge = new CommandString(_session, Command.C_SIT_IN, tid);
    _cat.finest(_name + " Move Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void muckCards(String tid) {
    CommandString ge = new CommandString(_session, Command.C_MUCK_CARDS, tid);
   // System.out.println(_name + " Move Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  

  public void dontMuck(String tid) {
    CommandString ge = new CommandString(_session, Command.C_DONT_MUCK, tid);
    //System.out.println(_name + " dont muck " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  public void ping() {
    Command ge = new Command(_session, Command.C_PING );
    _cat.finest(" Ping Command write " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  // this method is calling before login 
  public void pingBlocking() {
    Command ge = new Command(_session, Command.C_PING );
    _cat.finest(" Ping Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
    ResponseFactory rf = new ResponseFactory(readFull());
    ResponsePing rs = (ResponsePing) rf.getResponse();
	_cat.finest(rs.toString());
	_server_time = rs.getTime();
	_active_players = rs.getActivePlayers();
	_active_tables = rs.getActiveTables();
	firePingDetailsEvent(_server_time, _active_tables, _active_players);
  }
  
  
  public void quickFold(String tid) {
	  CommandString ge = new CommandString(_session, Command.C_QUICK_FOLD, tid);
	    _cat.finest(_name + " quickfold " + ge);
	    _last_write_time = System.currentTimeMillis();
	    write(ge.toString());
  }
  
  public void sendToServer(String tid, Object o) {
    _cat.finest("Action received " + o);
    if (o instanceof BettingAction) {
      BettingAction ba = (BettingAction) o;
      //String betAmt = String.valueOf(ba.getBet());
      _cat.finest("Bet in BettingAction  = " + ba.getBet());
      double actualBet = ba.getBet();
      CommandMove ge = new CommandMove(_session, ActionFactory.getMove(ba.getId()),
                                       actualBet, tid, 0, ba.getMD());
      ge.setPlayerPosition(ba.getTarget());
      _cat.finest(_name + " Move Command " + ge.toString());
      _last_write_time = System.currentTimeMillis();
      boolean sent = write(ge.toString());
      _cat.fine("actualBet  = " + actualBet);
      _cat.fine("sent to server "+sent);
    }
    else if (o instanceof ChatAction) {
      CommandMessage cm = new CommandMessage(_session,
                                             Base64.encodeString(((ChatAction) o).
          getChatString()), tid);
      _cat.finest(_name + " Chat Command " + cm);
      _last_write_time = System.currentTimeMillis();
      write(cm.toString());
    }
    else if (o instanceof CashierAction) {
      /*
          new CashierAction(clientPokerController.getPlayerNo(), value)
       */
      CommandGetChipsIntoGame ge = new CommandGetChipsIntoGame(_session, tid,
          ((CashierAction) o).getAmount());
      _cat.finest(_name + " get Chips  Command in sendToServer() " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }
  }

  public static void removeFromWaiters() {
    new Exception().printStackTrace();

  }

  public static void removeAllWaiters() {
    //new Exception().printStackTrace();
  }

  public void addToWaiters(String tid) {
    waitTable(tid);
  }

  public void startTLThread() {
    // start a heartbeat timer thread
    TLThread hb = new TLThread();
    Timer t = new Timer();
    t.schedule(hb, 0, HTBT_INTERVAL);
  }
  public void startRefreshTableListThread() {
    // start a refresh table list timer thread
    RTLThread rtl = new RTLThread();
    _rtlTimer = new Timer();
    _rtlTimer.schedule(rtl, 0, REFRESH_INTERVAL);
  }
  
  public void stopRefreshTableListThread() {
    // start a refresh table list timer thread
    if(_rtlTimer != null)_rtlTimer.cancel();
  }


      /********************************************************************************
       * Repetitive tasks
       *****************************************************************************/

      public class TLThread extends TimerTask {

        public void run() {
          try {
            heartBeat();
          }
          catch (Exception ex) {
            //do nothing
            _cat.warning("Exception" + ex);
          }
        }
      } // end HeartBeat class
      int _gccount = 0;
      public class RTLThread extends TimerTask {
    	  boolean bool = true;
          public void run() {
            try {
            	bool = !bool;
            	if(bool)
            	{
            		getTounamentList();
            	}
            	else
            	{	
            		getTableList();
            	}
                ping();
                if(_gccount++ == 60){
                	 Runtime rt = Runtime.getRuntime();
                	 StringBuilder sb = new StringBuilder();
                     sb.append("Available Free Memory: ").append(rt.freeMemory());
                     System.runFinalization();
                     System.gc();
                     sb.append("\nAvailable Free Memory: ").append(rt.freeMemory());
                     _gccount =0;
                     _cat.fine(sb.toString());
                }
                _disconnect_count++;
                //_cat.severe("_disconnect_count "+_disconnect_count);
                if(_disconnect_count > 6)
                {
//                	new Exception("There is disconnection in your Network").printStackTrace();
                 _cat.fine("There is disconnection in your Network, _disconnect_count "+_disconnect_count);
//                 JOptionPane.showMessageDialog(_owner,
//                            "There is disconnection in your Network, Please check your Network connection", "ERROR",
//                                          JOptionPane.ERROR_MESSAGE);
//                     // System.exit(-1); commented by rk
                 //added by rk
                	if(bl == null){
	                	bl = new BlinkLabel("");
	                	bl.createAndShowUI();
                	}else{
                		bl.bringToFront();
                	}
                }
            }
            catch (Exception ex) {
              //do nothing
              _cat.warning("Exception" + ex);
            }
          }
        } // end HeartBeat class


  public String toString() {
    StringBuilder sb = new StringBuilder("Player=[Name=");
    sb.append(_name);
    sb.append(", Session=");
    sb.append(_session);
    sb.append(", Game count=");
    sb.append(_action_registry.size());
    sb.append(", Worth=");
    sb.append(_play_worth);
    sb.append(":");
    sb.append(_real_worth);
    return sb.toString();
  }


  /********************************************************************************
   * Requests to the server
   *****************************************************************************/

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
                                             _name + "@test.com", (byte) 0, "",
                                             "");
    _cat.finest(_name + " Register Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
    Response gr = new Response(readFull());
    _cat.finest(_name + " Register Response " + gr);
    return gr;
  }

  public void addObserver(String tid) throws Exception {
	//new Exception("addObserver").printStackTrace();
    CommandTableDetail ge = new CommandTableDetail(_session, tid);
    _cat.finest(_name + " Add Observer Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  public void tournyDetails(String tid) throws Exception {
    CommandTournyDetail ge = new CommandTournyDetail(_session, tid);
    _cat.finest(_name + " GETTING TOURNY DETAILS " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void tournyRegister(String tid) throws Exception {
    CommandTournyRegister ge = new CommandTournyRegister(_session, tid);
    ge.setUserName(_name);
    _cat.finest(_name + " TOURNY REGISTRATION " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  public void tournyUnRegister(String tid) throws Exception {
    CommandTournyUnRegister ge = new CommandTournyUnRegister(_session, tid, _name);
    _cat.finest(_name + " TOURNY UN REGISTRATION " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void tournyMyTable(String tid) throws Exception {
    CommandTournyMyTable ge = new CommandTournyMyTable(_session, tid);
    _cat.finest(_name + " TOURNY MY TABLE " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  
  public void removeObserver(String tid) throws Exception {
    CommandString ge = new CommandString(_session, Command.C_TURN_DEAF, tid);
    _cat.finest(_name + " Remove Observer Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void joinTable(String tid, int pos, double value) throws Exception {
	ActionFactory af = getAF(tid);
    if (af != null && af._joined) {
    	JOptionPane.showMessageDialog(_owner,
                "You are already joined, please close and join again", "WARNING",
                JOptionPane.WARNING_MESSAGE);
    	return;
    }
    CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, value, tid);
    ge.setPlayerPosition(pos);
    _cat.finest(_name + " Join Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  //by rk
  public void joinPool(String poolName, double value) throws Exception {
	  CommandJoinPool ge = new CommandJoinPool(_session, Command.C_JOIN_POOL, poolName, value);
	    _cat.finest(_name + " Pool Join Command " + ge);
	    _last_write_time = System.currentTimeMillis();
	    write(ge.toString());
  }
  
  public void buyChips(double playchips, double realChips) {
    CommandBuyChips ge;
    ge = new CommandBuyChips(_session, playchips, realChips);
    //_requested_chips = value;
    _cat.finest(_name + " Buy Chips  Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void getMoneyIntoGame(String tid, double value) {
    CommandGetChipsIntoGame ge;
    ge = new CommandGetChipsIntoGame(_session, tid, value);
    _cat.finest(_name + " Get Chips  Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void updateClientSettingsAtServer(int settings) {
	CommandInt ci = new CommandInt(_session, Command.C_PREFERENCES, settings);
    _cat.finest("updateClientSettingsAtServer: "+ci.toString());
    write(ci.toString());
  }

  public void setSelectorForRead() throws Exception {
    _channel.configureBlocking(false);
    _channel.register(_selector, SelectionKey.OP_READ, this);
  }

  public void getTableList() throws IOException {
    CommandTableList ge = new CommandTableList(_session, PokerGameType.REGULAR_POKER_GAME | PokerGameType.HoldemSitnGo | PokerGameType.OmahaHiSitnGo);
    //_cat.info("TableList Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  public void getTounamentList() throws IOException {
    CommandTournamentList ge = new CommandTournamentList(_session, PokerGameType.REGULAR_POKER_GAME);
    //_cat.finest("TournamentList Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }
  
  public void heartBeat() throws Exception {
    if (_session != null) {
      Command ge = new Command(_session, Command.C_HTBT);
      _cat.finest("HTBT" + ge.toString());
      write(ge.toString());
    }
  }


  /********************************************************************************
   * Channel read write methods
   *****************************************************************************/

  public boolean readHeader() throws IOException {
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
        _cat.warning(_name +
                   " Marking the client dead as the channel is closed  ");
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

  private void resetRead() {
    _h = null;
    _b = null;
    _rlen = -1;
    _rseq = -1;
  }

  public synchronized boolean write(String str) {
	  try {
     
        _o = ByteBuffer.allocate(8 + str.length());
        _o.putInt(_wseq++);
        _o.putInt(str.length());
        _o.put(str.getBytes());

      _o.flip();
      int l = _channel.write(_o);
      
      while (_o.remaining() != 0) {
        _channel.write(_o);
      }
      _o = null;
      _dead = false;
    }
    catch (IOException e) {
      _cat.warning(_name +
                " Marking client as dead because of IOException during write");
      if(!_deadDialogBox)
      {
    	  if(this.bl != null)
    	  this.bl.closeBox();//by rk
    	  JOptionPane.showMessageDialog(_owner,
    	    "There is a disconnection ! Restart to play.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
    	  _deadDialogBox = true;
    	  System.exit(-1); //added by rk
      } 
      return false;
    }
    return true;
  }




} // end class Player
