package com.poker.client;

import java.net.ServerSocket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.Configuration;
import com.agneya.util.Rng;
import com.golconda.db.DBPlayer;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;
import com.golconda.net.NWProcessor;
import com.golconda.net.event.AdminEvent;
import com.poker.common.message.ResponseFactory;
import com.poker.common.message.ResponseGetChipsIntoGame;
import com.poker.common.message.ResponseLogin;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponseTableDetail;
import com.poker.common.message.ResponseTableList;
import com.poker.common.message.ResponseTableOpen;
import com.poker.game.PokerGameType;


/**
 * This Class is used to launch a independant shill server which can hook up to
 * a game-server. The Shill Server can be managed from a remote machine which
 * runs a ShillManager. The shill server based on some heuristics launches and
 * manages the players connected to the game server.
 *
 * @version $Version$
 */
public class ShillServer
    implements Runnable, NWProcessor {
  //static stuff
  public static ShillServer __shillServer = null;
  transient static Logger log = Logger.getLogger(ShillServer.class.getName());
  static Configuration __conf = null;
  static Selector __selector = null;
  public static Hashtable __players = null;
  public static Vector __standby = null;
  static int __tc;
  static TableMap[] __tm;
  public static int players_connected = 0;
  int _maxBots; //.....?
  Player _dummy;
  //max bots this shillserver can seat on a table.
  static Hashtable maxBotLimit = new Hashtable();
  static Hashtable minBet = new Hashtable();
  Thread _runner = null;
  ServerSocket _shillSrvSocket;
  boolean _runShillServer = true;
  private boolean _listening = true;
  public ShillServerData _srvData = null;
  public boolean isGUI = false;
  public boolean isRegistered = false;
  private final int maxCountToPing = 3500;
  private int currPingCount = 0;
  Rng _rng = new Rng();

  public static ShillServer getInstance() {
    //make __shillMgr a singleton
    if (ShillServer.__shillServer == null) {
            ShillServer.__shillServer = new ShillServer();
            ShillServer.__shillServer.log.info("Shill Server Instantiated");
    }
    return ShillServer.__shillServer;
  }

  public void addBotsFromDB() throws Exception {
	  //written by rk, to add bots from DB not from string array defined in this class
	  player_names = DBPlayer.getBots();
	  System.out.println("available bots from DB "+player_names.length);
	  if (player_names.length < _maxBots){
      	System.out.println("Only " + player_names.length + " players available while max bots is " + _maxBots);
	  }
    Vector vn = new Vector();
    for (int i = 0; i < player_names.length; i++) {
         vn.add(player_names[i] );
    }

    for (int i = 0; i < _maxBots; i++) {
        Player p = new Player((String)vn.remove(_rng.nextIntBetween(0, vn.size())));
        ShillServer.__players.put(p._name, p);
    }
        int remaining = player_names.length - _maxBots;
        if (remaining < 1){
        	System.out.println("No standby players");
        	return;
        }
	  for (int i = 0; i < remaining; i++) {
	      Player p = new Player((String)vn.remove(_rng.nextIntBetween(0, vn.size())));
	      ShillServer.__standby.add(p);
	  }
   
  }

  public ShillServer() {
    try {
      isGUI = false;
            ShillServer.__selector = Selector.open();

            ShillServer.__conf = Configuration.getInstance();
            ShillServer.__players = new Hashtable();
            ShillServer.__standby = new Vector();
		      //ShillServer.__tm = tableMap();
//		    _maxBots = Integer.parseInt( (String) __conf.get("Shill.Density"));
            //added by rk, for bot monitor dev.
            int min_density = Integer.parseInt( (String) __conf.get("Shill.Density.Min"));
            int max_density = Integer.parseInt( (String) __conf.get("Shill.Density.Max"));
            _maxBots = _rng.nextIntBetween(min_density, max_density);
            System.out.println("max bots allowed "+_maxBots);

      //Initialise the server data here, get available bots from the
      // server
      //and initialise them
      _dummy = new Player("dummy");
      try {
        _dummy.connect();
      }
      catch (Exception e) {
        log.log(Level.WARNING, "Dummy unable to connect -- exiting ", e);
        System.exit( -1);
      }
      // populate the player hash from the database
      addBotsFromDB();

      //Start heartbeat thread
      log.finest("Starting Heartbeat");
      HeartBeat hb = new HeartBeat();
      Timer t = new Timer();
      t.schedule(hb, 0, __conf.getInt("Server.maintenance.heartbeat"));

      //start the bot monotoring thread
      Timer tim = new Timer();
      tim.schedule(new BotMonitor(), 0,
                   __conf.getInt("Server.maintenance.heartbeat") * 2);

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }


  public void run() {
    int toggler = 0;
    try {
      //the controller thread..
    ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 200, 60L, TimeUnit.SECONDS,
               new LinkedBlockingQueue<Runnable>());
      while (_runShillServer) {
        try {
          Player p = null;
          SelectionKey key = null;
                    ShillServer.__selector.selectNow();
          // Now we deal with our incoming data / completed writes...
          Set keys = ShillServer.__selector.selectedKeys();
          Iterator<SelectionKey> it = keys.iterator();
          while (it.hasNext()) {
            key = (SelectionKey) it.next();
            it.remove();
            try {
              if (key.isReadable()) {
                p = (Player) key.attachment();
                String response = p.read();
                if (response == null) {
                  continue;
                }
                ResponseFactory rf = new ResponseFactory(
                    response);
                Response r = rf.getResponse();

                //MOVE RESPONSE
                if (r._response_name == Response.R_MOVE) {
                  //log.finest("Response Move from Server for "
                   //         + p._name + " :: " + r.toString());
                  p._rge.add(r);
                  //p.processMove(); //single threaded
                  // processing

                 tpe.execute(p);
                 
                }
                //CONNECT RESPONSE
                else if (r._response_name == Response.R_CONNECT) {
                  //log.info("Connect resp " + r);
                  if (r.getResult() == 1) {
                    //log.finest("connect response - success");
                    p._session = r.session();
                    p._currState = ShillConstants.PLR_CONNECTED;

                    if (p._name.equals("dummy")) {
                      p.getTableList(_dummy._pgTypes);
                    }
                    else {
                      p.login();
                    }
                  }
                  else {
                    log.warning("Connect Command Failed removing bot " + p);
                    p._currState = ShillConstants.REMOVE_BOT;
                  }
                }
                //LOGIN RESPONSE
                else if (r._response_name == Response.R_LOGIN) {
                  log.info(p._name + " Login Response " + r);
                  if (r.getResult() == 1) {
                    p._total_play_chips = ( (ResponseLogin) r)
                        .getPlayWorth();
                    p._total_real_chips = ( (ResponseLogin) r)
                        .getRealWorth();
                    p._currState = ShillConstants.PLR_LOGGED;
                    getJoinParameters(p);
                  }
                  else if (r.getResult() == Response.E_REGISTER) {
                    p._currState = ShillConstants.PLR_NEEDS_REGISTRATION;
                    p.register();
                  }
                  else {
                    p._currState = ShillConstants.REMOVE_BOT;
                    log.warning("Setting bot for removal as login failed " + p);
                  }
                }
                //REGISTER RESPONSE
                else if (r._response_name == Response.R_REGISTER) {
                  log.info(p._name + " Register Response "
                           + r);
                  if (r.getResult() == 1) {
                    p._total_play_chips = ( (ResponseLogin) r)
                        .getPlayWorth();
                    p._total_real_chips = ( (ResponseLogin) r)
                        .getRealWorth();

                    p._currState = ShillConstants.PLR_LOGGED;
                    getJoinParameters(p);
                  }
                  else {
                    p._currState = ShillConstants.REMOVE_BOT;
                    log.warning("Setting bot for removal as registration failed " +
                             p);
                  }
                }
                // TABLELIST RESPONSE
                else if (r._response_name == Response.R_TABLELIST) {
                  if (r.getResult() == 1) {
                    ResponseTableList rtl = (ResponseTableList) r;
                    if (p._name.equals("dummy")) {
                      tableMap(rtl);
                      //log.finest("tableMap refreshed " + r);
                    }
                  }
                  else {
                    p._currState = ShillConstants.REMOVE_BOT;
                    log.warning(p._name
                             + " Remove bit TableList Response Failed " + p);
                  }
                }
                // BUY CHIPS RESPONSE
                else if (r._response_name == Response.R_BUYCHIPS) {
                  log.severe(p._name + " R_BUYCHIPS "   + r);
                }
              
                //GET CHIPS INTO GAME RESPONSE
                else if (r._response_name == Response.R_GET_CHIPS_INTO_GAME) {
                  log.severe(p._name
                           + " Get Chips into Game Response "
                           + r);
                  p._moneyToTable = false;
                  if (r.getResult() == 1) {
                    p._tableChips = ( (ResponseGetChipsIntoGame) r)
                        .getAmount();
                  }
                  else if (r.getResult() == 38) {
                    //ignore
                  }
                  else {
                    p._currState = ShillConstants.REMOVE_BOT;
                    log.warning("R_GET_CHIPS_INTO_GAME" + r);
                    log.warning(
                        "Setting bot for removal as money to table failed " +
                        p);
                  }
                }
                //by rk
                else if (r._response_name == Response.R_TABLE_OPEN){
                    //spawn this table
                	log.severe("R_TABLE_OPEN "+r.toString());
                	if ( (r.getResult() == 1)
                           // && (p._tmpJoinPos > -1)
                           /* && (p._tmpTid != null)*/) {
                	 ResponseTableOpen rto = (ResponseTableOpen)r;
                	 String tid = rto.getTableName();
                     int pos = rto.getPosition();
                     GameEvent ge = new GameEvent();
                     String str_ge = rto.getGE();
                     if (str_ge == null) {
                         return;
                       }
                     ge.init(str_ge);
                     double min_bet = (ge.get("min-bet") != null) ? Double
                             .parseDouble(ge.get("min-bet"))
                             : 0;
                         double max_bet = (ge.get("max-bet") != null) ? Double
                             .parseDouble(ge.get("min-bet"))
                             : 0;
                         p._joinedGameType = ge.getType();
                         String tmp_str = null;
                         tmp_str = ge.get("small-blind");
                         if (tmp_str != null) {
                           p._small_blind = Double
                               .parseDouble(tmp_str);
                         }
                         tmp_str = ge.get("big-blind");
                         if (tmp_str != null) {
                           p._big_blind = Double
                               .parseDouble(tmp_str);
                         }
                         tmp_str = ge.get("max-bet");
                         if (tmp_str != null) {
                           p._maxBet = Double
                               .parseDouble(tmp_str);
                         }
                         tmp_str = ge.get("min-bet");
                         if (tmp_str != null) {
                           p._minBet = Double
                               .parseDouble(tmp_str);
                         }
                         tmp_str = ge.get("max-players");
                         if (tmp_str != null) {
                           p._maxPlayers = Integer
                               .parseInt(tmp_str);
                         }
                         tmp_str = ge.get("min-players");
                         if (tmp_str != null) {
                           p._minPlayers = Integer
                               .parseInt(tmp_str);
                         }
                         tmp_str = ge.get("max-rounds");
                         if (tmp_str != null) {
                           p._maxRounds = Integer
                               .parseInt(tmp_str);
                         }
                         p._currState = ShillConstants.PLR_OBSERVING;
                         String[][] pd = ge.getPlayerDetails();
                         if (pd != null && pd.length ==
                             ge.getMaxPlayers()) {
                           log.info("Max players sitting on this table");
                           //can add this bot to waiting list
                           //p.waitList(p._tmpTid);

                         }
                         else {
                           if ( (PokerGameType.isReal(p._tmpTableType)) ?
                               (p._total_real_chips > (80 * min_bet))
                               : (p._total_play_chips > (80 * min_bet))) {
                             //p.join(p._tmpTid, p._tmpJoinPos, p._minBet);
                            // p.joinPool(p._tmpTid , p._minBet);
                        	   p.addObserver(tid);
                        	   log.severe("player added as observer");
                             p._tmpJoinPos = -1;
                             p._tmpTid = null;
                             p._tmpTableType = 0;
                           }
                           else {
                             log.warning(PokerGameType.isPlay(p._tmpTableType) + "Not enough chips " + p);
                             /**p.buyChips(
                                 (GameType
                                  .isPlay(p._tmpTableType)) ? (80 * min_bet)
                                 : 0,
                                 (GameType
                                  .isReal(p._tmpTableType)) ? (80 * min_bet)
                                 : 0);**/
                           }
                         }
                	}
                  }
                
                //TABLEDETAIL RESPONSE (OBSERVER RESPONSE)
                else if (r._response_name == Response.R_TABLEDETAIL) {
                  //i.e Observer response
                  log.info(p._name
                           + " Observe Response Response "
                           + r);
                  if ( (r.getResult() == 1)
                      && (p._tmpJoinPos > -1)
                      && (p._tmpTid != null)) {
                    ResponseTableDetail rtd = (ResponseTableDetail) r;
                    GameEvent ge = new GameEvent();
                    String str_ge = rtd.getGameEvent();
                    if (str_ge == null) {
                      return;
                    }
                    ge.init(str_ge);
                    double min_bet = (ge.get("min-bet") != null) ? Double
                        .parseDouble(ge.get("min-bet"))
                        : 0;
                    double max_bet = (ge.get("max-bet") != null) ? Double
                        .parseDouble(ge.get("min-bet"))
                        : 0;
                    p._joinedGameType = ge.getType();
                    String tmp_str = null;
                    tmp_str = ge.get("small-blind");
                    if (tmp_str != null) {
                      p._small_blind = Double
                          .parseDouble(tmp_str);
                    }
                    tmp_str = ge.get("big-blind");
                    if (tmp_str != null) {
                      p._big_blind = Double
                          .parseDouble(tmp_str);
                    }
                    tmp_str = ge.get("max-bet");
                    if (tmp_str != null) {
                      p._maxBet = Double
                          .parseDouble(tmp_str);
                    }
                    tmp_str = ge.get("min-bet");
                    if (tmp_str != null) {
                      p._minBet = Double
                          .parseDouble(tmp_str);
                    }
                    tmp_str = ge.get("max-players");
                    if (tmp_str != null) {
                      p._maxPlayers = Integer
                          .parseInt(tmp_str);
                    }
                    tmp_str = ge.get("min-players");
                    if (tmp_str != null) {
                      p._minPlayers = Integer
                          .parseInt(tmp_str);
                    }
                    tmp_str = ge.get("max-rounds");
                    if (tmp_str != null) {
                      p._maxRounds = Integer
                          .parseInt(tmp_str);
                    }
                    p._currState = ShillConstants.PLR_OBSERVING;

                    String[][] pd = ge.getPlayerDetails();
                    if (pd != null && pd.length ==
                        ge.getMaxPlayers()) {
                      log.info("Max players sitting on this table");
                      //can add this bot to waiting list
                      //p.waitList(p._tmpTid);

                    }
                    else {
                      if ( (PokerGameType.isReal(p._tmpTableType)) ?
                          (p._total_real_chips > (80 * min_bet))
                          : (p._total_play_chips > (80 * min_bet))) {
                        p.join(p._tmpTid, p._tmpJoinPos, p._minBet);
                       // p.joinPool(p._tmpTid , p._minBet);
                        p._tmpJoinPos = -1;
                        p._tmpTid = null;
                        p._tmpTableType = 0;
                      }
                      else {
                        log.warning(PokerGameType.isPlay(p._tmpTableType) + "Not enough chips " + p);
                        /**p.buyChips(
                            (GameType
                             .isPlay(p._tmpTableType)) ? (80 * min_bet)
                            : 0,
                            (GameType
                             .isReal(p._tmpTableType)) ? (80 * min_bet)
                            : 0);**/
                      }
                    }
                  }
                  else {
                    log.warning(p._name
                             + " TableList Detailed Failed " + p);
                    //p._currState = ShillConstants.REMOVE_BOT;
                  }
                }

                //WAITING LIST RESPONSE
                else if (r._response_name == Response.R_WAITER) {
                  log.info(p._name
                           + " WaitingList Response "
                           + r);
                  if (r.getResult() == 1) {
                    p._currState = ShillConstants.PLR_WAITING;
                  }
                }

                //MESSAGE RESPONSE
                else if (r._response_name == Response.R_MESSAGE) {
                  ResponseMessage rm = (ResponseMessage) r;
                  log.finest("Message =" + rm.getMessage());
                  if (isGUI) {
                    //log.finest("Passing to GUI");

                    String nvpairs[] = rm.toString().split(
                        "&");
                    String tmp_str[] = nvpairs[3]
                        .split(",");
                    String tmp_str1[] = tmp_str[1]
                        .split("=");
                    String plr = tmp_str1[1];
                    String mesg = rm.getMessage();
                  }
                }

                //LOGOUT RESPONSE
                else if (r._response_name == Response.R_LOGOUT) {
                  //note: the server doesn't seem to be
                  // sending out this message back
                  p._currState = ShillConstants.REMOVE_BOT;
                  if (r.getResult() == 1) {
                    log.info("Player " + p._name
                             + "has logged out.");
                  }
                  else if (r.getResult() == 22) {
                    p.logout();
                  }
                  else {
                    p._currState = ShillConstants.REMOVE_BOT;
                    log.warning("logout attempt failed for "
                             + p._name);
                  }
                }
                //SIT_OUT
                else if (r._response_name == Response.R_SIT_OUT) {
                  log.info("sit out " + p._name);
                }
                //SIT_OUT
                else if (r._response_name == Response.R_SIT_IN) {
                  log.info("sit in " + p._name);
                  p._currState = ShillConstants.PLR_JOINED;
                }
                else if (r._response_name == Response.R_PING) {
                  //ping
                }
                //UNKNOWN RESPONSE
                else {
                  log
                      .warning(
                          "SHILL: FATAL: Unknown event received from the server "
                          + r);
                }
              }
            }
            catch (CancelledKeyException cke) {
              log.log(Level.WARNING, "Cancelled Key " + p);
              continue;
            }
            catch (Exception ex) {
              ex.printStackTrace();
              log.warning(ex + ", " + p);
              continue;
            }
          }

          Thread.sleep(100);
        }
        catch (Exception ex) {
          log.log(Level.WARNING, "Fatal error in the outer while : ", ex);
          ex.printStackTrace();
          continue;
        }

      } //end while;
      log.log(Level.WARNING, "SHILLSERVER STOPPED");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public AdminEvent process(AdminEvent ae) {
    return new AdminEvent(processCommand(ae.toString()));
  }

  public String processCommand(String str) {
    String resp = processCommand(new ShillManagerCommand(str));
    log.info(resp);
    return resp;
  }

  public String processCommand(ShillManagerCommand mgrCmd) {
    log.info("Command rcvd = " + mgrCmd);
    log.info("cmd = " + mgrCmd.cmd);
    log.info("uid = " + mgrCmd.uid);
    String response = "";
    try {
      //make changes in the _srvData
      switch (mgrCmd.cmd) {

        case ShillConstants.STATUS:

          //status -a|-s <server-ip> [-u <user-id>]
          if (mgrCmd.uid == null) {
            response = __shillServer.toString();
            // Gui enabled.
         
          }
          else {
            response = __shillServer
                .toPlayerString(mgrCmd.uid);
           
          }
          return response;

        case ShillConstants.ADD_BOT:

          //addbot -s <server-ip> [[-t <game-type>]|[-g
          // <game-id>]] [-q <player-iq>] [-u <user-id>]
          // [-i <idle-value>] [-o <sitout-value>] [-l
          // <max-loss>] [-w <max-win>]"
          String msgStr = null;

          //add bot
          //REMOVED
          response = msgStr;
        
          return response;

        case ShillConstants.MODIFY_BOT:
          msgStr = null;

          //modify bot
          if (modifyBotData(mgrCmd)) {
            msgStr = "Successfully Modified data of Bot "
                + mgrCmd.uid;
          }
          else {
            msgStr = "Failed to Modify BotData of "
                + mgrCmd.uid;
          }
          response = (msgStr);
         

          return response;

        case ShillConstants.REMOVE_BOT:
          Vector rm_users = new Vector();

          //rmbot -s <server-ip> [-u <user-id>] | [-g
          // <game-id>] | [-q <player-iq>] | [-t
          // <game-type>]");
          //if uid is specified remove bot with that uid
          if (mgrCmd.uid != null) {
            //get the Player and log him out
            Player plr = (Player) (ShillServer.__players
                                   .get(mgrCmd.uid));
            if (plr != null) {
              log
                  .info("Recieved explicit command to remove bot "
                        + plr._name
                        + " ...so logging out ");
              plr.logout();
            }

            msgStr = "Stopped Bot";
            //remove from _players and change status in
            // db to 0
            if (removeBot(mgrCmd.uid)) {
              msgStr += " and Successfully removed the Bot "
                  + mgrCmd.uid;
              rm_users.add(mgrCmd.uid);
            }
            else {
              msgStr = "but Failed to remove the Bot "
                  + mgrCmd.uid;
            }
            response = "message=" + (msgStr);
          
          }
          //if iq or gametype is specified remove all
          // bots with that iq or gametype
          else if (mgrCmd.iq > 0 || mgrCmd.gameTypes > 0) {
           //REMOVED
          }
          //else remove all bots
          else {
            for (Enumeration enumer = ShillServer.__players
                 .keys(); enumer.hasMoreElements(); ) {
              String userid = (String) enumer
                  .nextElement();
              //get the Player and log him out
              Player plr = (Player) (ShillServer.__players
                                     .get(userid));
              log
                  .info("Recieved explicit command to remove bot "
                        + plr._name
                        + " ...so logging out ");
              plr.logout();
              msgStr = "Stopped Bot";
              //remove from _players and change
              // status in db to 0
              if (removeBot(userid)) {
                msgStr += " and Successfully removed the Bot "
                    + userid;
                rm_users.add(userid);
              }
              else {
                msgStr = "but Failed to remove the Bot "
                    + userid;
              }
              response = "message=" + (msgStr);
            }
          }
          return response;

        case ShillConstants.STOP_BOT:

          //stopbot -a|-s <server-ip> [-u <user-id>] [-d
          // <delay in sec>]");
          if (mgrCmd.uid == null) {
            //stop all bots on server
            int count = 0;
            for (Enumeration enumer = ShillServer.__players
                 .keys(); enumer.hasMoreElements(); ) {
              String userid = (String) enumer
                  .nextElement();
              Player plr = (Player) (ShillServer.__players
                                     .get(userid));
              //get the Player and log him out
              log
                  .info("Recieved explicit command to stop all bots "
                        + plr._name
                        + " ...so logging out ");
              plr.logout();
              response = "message=" + ("Successfully Stopped the Bot "
                                       + mgrCmd.uid);
            }
          }
          else {
            //get the Player and log him out
            Player plr = (Player) (ShillServer.__players
                                   .get(mgrCmd.uid));
            plr.logout();
            response = "message=" + ("Successfully Stopped the Bot "
                                     + mgrCmd.uid);
        
          }
          return response;

        case ShillConstants.STOP:

          //assuming that remove all bots has been
          // called.
          //this abruptly stops the shillserver
          _runShillServer = false;
          response = "message=" + ("Successfully Stopped the ShillServer.");
          return response;
        default:
          return response;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return response;
  }

  //method to modify bot data depending on botid
  private boolean modifyBotData(ShillManagerCommand mgrCmd) {
    boolean flag = false;
    try {

      boolean dbEnabled = (__conf.getProperty("ShillServer.dbenabled")
                           .equals("true")) ? true : false;
    

      Player p = (Player)ShillServer.__players.get(mgrCmd.uid);
      p._iq = mgrCmd.iq;
      p._gTypes = mgrCmd.gameTypes;
      p._idleCount = mgrCmd.idleVal;
      p._pgTypes = mgrCmd.pref_gameTypes;
      p._winCeil = mgrCmd.maxWin;
      p._lossCeil = mgrCmd.maxLoss;
      p._sitoutCount = mgrCmd.sitOutVal;
      p._nGames = mgrCmd.gamesPerSession;
            ShillServer.__players.put(p._name, p);
      flag = true;

    }
    catch (Exception ex) {
      System.out.println("Failed to modify botdata....");
      ex.printStackTrace();
      flag = false;
    }
    return flag;
  }

  //remove from _players and change status in db to 0
  private boolean removeBot(String bot) {
    try {
     
      //remove from _players
            ShillServer.__players.remove(bot);
    }
    catch (Exception ex) {
      System.out.println("Failed to remove bot and update status");
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  public void init() throws Exception {
    //Send registration message to ShillServerManager.
    __shillServer._runner = new Thread(__shillServer);
    __shillServer._runner.start();

    //start the admin server
    /**NWServer serv = new NWServer(this);
    serv.startServer(Configuration.instance().getInt("ShillServer.mgr.port"));**/
    log.info("Starting Shill server admin listener on port " +
             Configuration.instance().getInt("ShillServer.mgr.port"));
  }

  public static void main(String[] args) {
    try {
      ShillServer ss = ShillServer.getInstance();
      ss.init();

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public class HeartBeat
      extends TimerTask {

    /**
     * Send heartbeat if player is not a LOGGEDOUT player and is not a dead
     * player. otherwise mark for logout.
     */
    public void run() {
      Player p = null;
      try {
        _dummy.ping();
        Enumeration e = __players.elements();
        for (; e.hasMoreElements(); ) {
          p = (Player) e.nextElement();
          if (p._currState == ShillConstants.PLR_CONNECTED) {
            p.ping();
          }
        }
      }
      catch (Exception ex) {
        //do nothing
        log.log(Level.WARNING, ex.toString(), ex);
        if (p != null) {
          p._currState = ShillConstants.REMOVE_BOT;
          log.warning("Removing bot as htbt fails");
        }
      }
    }
  } // end HeartBeat class




  /** ********************************* */
  synchronized static void getJoinParameters(Player p) throws Exception {
    log.info("__tc = " + __tc);
    boolean doExit = false;
    //synchronized(__tm){
    for (int i = 0; i < __tc; i++) {
       log.info("Player Type=" + p._pgTypes + " Seating " + p._name);
       log.info("Table= " + __tm[i]);
      if (__tm[i]._isPrivate || (__tm[i]._gtype & p._pgTypes) == 0) {
        log.finest("Game Type not same or Private table");
        continue;
      }
      // use real tables with less stakes
      // REAL TABLE
      log.info("Min bet = " + __tm[i]._minbet + " , game type = " + __tm[i]._gtype);
      //by rk kept condition *temp*
      if (/*__tm[i]._gtype >= 256 &&*/ __tm[i]._gtype == 32 && (__tm[i]._minbet > 0.5)) {
          continue;
        }

      // loop thru all the tables // 
      if ( (__tm[i]._gtype & p._pgTypes) > 0 ) {

                                               /* || ( __tm[i]._gtype == 8192 && (__tm[i]._gname.contains((String) __conf.get("Required.Table1"))
    		                                  || __tm[i]._gname.contains((String) __conf.get("Required.Table2"))
    		                                  || __tm[i]._gname.contains((String) __conf.get("Required.Table3"))
    		                                  || __tm[i]._gname.contains((String) __conf.get("Required.Table4"))
    		                                  || __tm[i]._gname.contains((String) __conf.get("Required.Table5")))
    		                                  || p._name.equals("dummy")) ) {         */

        // check if additional bots can be seated
        // loop thru all the seats
        
        log.info("Players Seated=" + __tm[i]._pc +" Max bots =" + __tm[i]._bots );
        int maxPlrs = __tm[i]._gtype == /*32*/8192 ? __tm[i]._bots * 30: __tm[i]._bots;
        if ( (__tm[i]._pc <  maxPlrs)) {
          int pos = -1;
          for (int k = 0; k < 9; k++) {
            //make 10 attempts to sit
            pos = (int) (Math.random() * __tm[i]._maxPlayer);
            log.info("pos "+pos);
            if (__tm[i]._playerDetails[pos]  == -1 &&__tm[i]._gtype != /*32*/8192 ) {
              //position occupied
              continue;
            }
            else {
              p._tmpJoinPos = pos;
            // p._tmpTid = "004 "+__tm[i]._gname;//  //*temp*
              //by rk,
              p._tmpTid = __tm[i]._gname;
              //p.joinPool(p._tmpTid, 20);
              p._tmpTableType = __tm[i]._gtype;
              p._tableNum = i;
              __tm[i]._playerDetails[pos] = -1;
              __tm[i]._pc++;
              break;
            }
          }
          log.finest(" tid = " + __tm[i]._gname + " pos = " + pos);
          if ( (p._tmpTid != null)/* && (p._tmpJoinPos != -1)*/) {
        	  //System.out.println(__tm[i]._gname+","+__tm[i]._minbet);
        	  //p.joinPool(p._tmpTid, __tm[i]._minbet*20);/////////40
              p.addObserver(p._tmpTid);
        	  
            doExit = true;
            log.finest("SEATED----------------" + p._name);
            break;
          }
        }
        else {
          //log.finest("empty pos not found.....");
          continue;
        }

      }
      else {
        //log.finest("else _pgTypes does not match");
        continue;
      }
      if (doExit) {
        break;
      }
    }
    if (!doExit && !p._name.equals("dummy")) {
      p._currState = ShillConstants.REMOVE_BOT;
      log.warning("Unable to seat removing " +   p);

    }
    //log.finest("getJoinParameters():: END");
  }
  
  
  /** ********************************** */

  public String toPlayerString(String uid) {
    if (uid == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder("ss=");
    sb.append("IP");
    Player p = (Player) __players.get(uid);
    if (p == null) {
      return null;
    }
    sb.append("&bot=").append(p.toString());
    log.finest("Sending data for player " + uid + " = " + sb.toString());
    return sb.toString();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("ss=");
    int tmp_nosPlrs = ShillServer.__players.size();
    sb.append("IP").append("&botc=").append(tmp_nosPlrs);
    if (tmp_nosPlrs > 0) {
      sb.append("&bots=");
    }
    Enumeration enumer = ShillServer.__players.keys();
    int count = 0;
    for (; enumer.hasMoreElements(); ) {
      if ( (count++) > 0) {
        sb.append(",");
      }
      Player p = (Player)ShillServer.__players.get( (String) enumer
          .nextElement());
      sb.append(p.toString());
    }
    log.finest("Sending data = " + sb.toString());
    return sb.toString();
  }

  public Vector getBotsForTable(String tid) {
    Vector result = null;
    Enumeration ennumr = __players.elements();
    for (; ennumr.hasMoreElements(); ) {
      Player p = (Player) ennumr.nextElement();
      if (p._tid.equals(tid)) {
        if (result == null) {
          result = new Vector();
        }
        result.add(p._name);
      }
      //hack
      if (result != null && result.size() >= 10) {
        break;
      }
    }
    return result;
  }

  public Player getPlayer(String name) {
    return (Player) __players.get(name);
  }
  
  

      public TableMap[] tableMap(ResponseTableList rtl) throws Exception {
          __tc = rtl.getGameCount();
          __tm = new TableMap[__tc];
          for (int i = 0; i < ShillServer.__tc; i++) {
          GameEvent ge = rtl.getGameEvent(i);
          __tm[i] = new TableMap(ge);
         // System.out.println(__tm[i]);
        }
        return __tm;
      }
      
      public class TableMap {
          public String _gname;
          public int _gtype;
          public int _maxPlayer, _minPlayer;
          public double _minbet, _maxbet;
          public int[] _playerDetails;
          public int _pc;
          public boolean _isReal, _isPrivate;
          public int _bots;
          
          
          public TableMap(GameEvent ge){
                log.finest(ge.toString());
                
              _minbet = ge.getMinBet();      
              _maxbet = ge.getMaxBet();
              _maxPlayer = ge.getMaxPlayers();
              _minPlayer = ge.getMinPlayers();
              
              _gtype = ge.getType();
              if (_gtype >= 256 || (_gtype & PokerGameType.TPOKER) > 0) { // REAL GAME
                _isReal=true;
                _bots = _maxPlayer;
              }
              else {
                _bots = _rng.nextIntBetween(0, _maxPlayer-1);///2 instead -1
              }
                
                _gname = ge.getGameName();
                _maxPlayer = ge.getMaxPlayers();
                _playerDetails =  new int[_maxPlayer];
                String pd[][] = ge.getPlayerDetails();
                if (pd != null) {
                  for (int j = 0; j < pd.length; j++) {
                    int pp = Integer.parseInt(pd[j][0]);
                    log.finest(pd[j][1] + "==" + pd[j][0]);
                    _playerDetails[pp] = -1; // set it to occupied
                  }
                    _pc = pd.length;
                }
                log.finest(this.toString());
          }
          
                     
         public String toString(){
             return _gname + ", maxp" + _maxPlayer + ", gt=" + _gtype + ", mb=" + _minbet + ", mxb=" + _maxbet + ", pc=" + _pc;
         }

      }
      //30 players, given by frank
      private static String[] player_names_new = {
    	  "a0mt3zcqs", "a11enw", "pepito23", "d1must", "d84atverums", "gabby_1983", 
    	  "gaika_lizko", "iatethedisco", "iassonsi", "ice_wind", "larabia", "lucalia", 
    	  "lylixi", "mickeysgirl619", "maracujina", "malizauon", "nastya_ak", "orihalk", 
    	  "o0larize0o", "policexgirl", "saardema", "selfish_jorn", "tesinasucar", "tdegenaro", 
    	  "texascoyote", "u2jazz", "uzzlita", "vaanelo", "vibukiheli", "waandei", 
    	  "victoriasecret",
      };
      	//before editing this list have a look at addBotsFromDB()
          private static String[] player_names = {
             "pinkie", "laila", "reenu", "sanhita", "barbie", "sangeetha", "sheetal",
             "roma", "teenie", "janu", "archana", "kalpana", "lucie", "swetha", "arty",
             "sunaina", "shama", "meeru", "rajini", "poonam", "hema", "swetha", "aishwaria",
             "teena", "sush", "sushmita", "kajol", "reena", "rainu", "ritu",
             "sainaz", "rinku", "meena", "sonali", "meena", "kavita", "savitha", "sonpari",
             "kavitha", "mahima", "gurtu", "jatt", "panjabi", "tola",
              "wang", "jason", "lee", "TaJia", "MunkEne", "Hyang", "NeunGil",
              "SunLee", "HaHa", "alauk", "lyuk", "JinJung", "hwagho", "teatler",
              "jangsang", "hajaka", "seobul", "bulwork", "kartar", "salmon",
              "bogosip", "kungfu", "foolee", "naese", "naeserung", "naseer",
              "jayanta", "gokhele", "hillapede", "atulram", "BangEYa",
              "kora", "tapori", "chamcha", "MeChi", "Chimung", "salsa",
              "katara", "yadav", "garth", "charak", "fienmann", "frankenstien",
              "rotiful", "HanBang", "Japani", "jalpari", "seudo", "workoman",
              "tarkari", "harito", "talliyo", "jain", "painer", "looper",
              "animtor", "sabzi", "gobi", "zuno", "emcsq", "tmcs", "patloo",
              "CIELIZON", "REDNECK", "caerweddin", "abid", "farhad", "reshma",
              "ihyardgnomes", "Getto", "Dragon", "milua", "mrlui", "draw",
              "cake", "justu", "PapiRico", "nike", "myaestro", "sheriff",
              "mrsen", "hisham", "sydrew", "mammaw", "angie", "yogiubear",
              "reebok", "ali", "bulldog", "dalmations", "Cadet", "Mellow",
              "matty", "teresa", "cathy", "warok", "phantom", "optron",
              "dark", "thenuts", "tootie", "salem", "abu", "board", "lifer",
              "scorn", "nipper", "koop", "suma", "SUMO", "bone", "boney",
              "kikin", "luckyshoe", "candee", "mondya", "marios", "willms",
              "tripper", "hearty", "lefto", "megabass", "sony", "sania",
              "keeper",  "mohamd", "baba", "osama", "gillary",
              "corflu", "larry", "john", "bestgirl", "cat", "hamilton", "bobby",
              "harry", "tommy", "johhney", "sweety", "sasha", "wildbill", "bill",
              "bushy", "roger", "Herd",
              "amanda", "loverboy", "dick", "alice", "shaw", "blackie", "carol",
              "lady", "Mavarick", "puller", "sandra", "doug", "denise", "oliver",
              "amy",  "spiderman", "barbie", "shaina", "lewis", "headly",
              "danny", "fanny", "clinton", "james", "lovergirl",
              "driver", "pilot",  "margarita", "deadly", "inafix", "maria",
              "cool", "tom", "bush", "olli", "polli", "peter", "jughead",
              "cindy", "tony", "pony", "rolly", "fish", "poker", "surya",
              "jini","sandeep", "krishan","jagdish", "robert",
              "hulbert", "joe", "scott", "rayn", "deo", "anurag", "dixit",
              "ruth", "parker", "karyga", "luna", "honda", "dash", "ford",
              "motor", "chor", "gianni", "karan", "vivek", "pallavi",
              "neena", "srinivas", "lakshman", "colluder", "mafia", "gangs",
              //"bhola","batman","tiger", "batgirl", "alex", "jack",
              //"guest2", "guest3", "guest4", "guest123", "guest124", "guest7", "guest8",
          };
         

    }
