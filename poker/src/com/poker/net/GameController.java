package com.poker.net;

import com.agneya.util.Base64;
import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;

import com.golconda.net.NWClient;
import com.golconda.net.event.AdminEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class GameController {
  NWClient _clnt = null;

  public static final int maxReconnectTimes = 10;
  public static int reconnectTimes = 0;
  private static GameController __gameCtrl = null;

  // private static  Log log = LogFactory.getLog("#### GameController ####");

  public GameController(String addr, int port) throws IOException {
    _clnt = new NWClient();
    System.out.println("ip=" + addr + ", port=" + port);
    _clnt.connect(addr, port);
  }
  

    public GameController() throws IOException, ConfigurationException {
      _clnt = new NWClient();
        Configuration c = Configuration.instance();
      _clnt.connect(c.getProperty("Poker.Admin.Network.server.ip"),
                                        Integer.parseInt(
                                            c.getProperty("Poker.Admin.Network.port")));
    }

  public static synchronized GameController getInstance() throws
      ConfigurationException, IOException {
    Configuration c = Configuration.instance();

    if (__gameCtrl == null) {
      //log.finest("Inside initializing Game Controller....");
      __gameCtrl = new GameController(c.getProperty("Poker.Admin.Network.server.ip"),
                                      Integer.parseInt(
                                          c.getProperty("Poker.Admin.Network.port")));
      reconnectTimes = 0;
      //log.finest("Admin.Network.server.ip...."+c.getProperty("Admin.Network.server.ip")+"........Admin.Network.port...."+c.getProperty("Admin.Network.port"));
    }
    else {
      if (! (__gameCtrl._clnt.isConnected())) {
        __gameCtrl._clnt.close();
        __gameCtrl = null;
        __gameCtrl = new GameController(c.getProperty("Poker.Admin.Network.server.ip"),
                                        Integer.parseInt(c.getProperty(
                                            "Poker.Admin.Network.port")));
      }
    }
    return __gameCtrl;
  }

  public void refreshConnection() {
    try {
      Configuration c = Configuration.instance();

      if (__gameCtrl == null) {
        //log.finest("Inside initializing Game Controller....");
        __gameCtrl = new GameController(c.getProperty("Admin.Network.server.ip"),
                                        Integer.parseInt(
                                            c.getProperty("Admin.Network.port")));
        reconnectTimes = 0;
        //log.finest("Admin.Network.server.ip...."+c.getProperty("Admin.Network.server.ip")+"........Admin.Network.port...."+c.getProperty("Admin.Network.port"));
      }
      else {
        if (! (__gameCtrl._clnt.isConnected())) {
          __gameCtrl._clnt.close();
          __gameCtrl = null;
          __gameCtrl = new GameController(c.getProperty(
              "Admin.Network.server.ip"),
                                          Integer.parseInt(c.getProperty(
                                              "Admin.Network.port")));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void close() throws IOException {
    if (__gameCtrl != null) {
      __gameCtrl._clnt.close();
      __gameCtrl = null;
    }
  }

  public AdminEvent threads() throws IOException {
    _clnt.send(new String("command-name=threads"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent summary() throws IOException {
    _clnt.send(new String("command-name=summary"));
    return new AdminEvent(_clnt.get());
  }
  /***************************PLAYER***************************************/

  public AdminEvent playerCount() throws IOException {
    _clnt.send(new String("command-name=player_count_all"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent rPlayerCount() throws IOException {
    _clnt.send(new String("command-name=rplayer_count_all"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent playerCount(String gid) throws IOException {
    _clnt.send(new String("command-name=player_count_all_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent rPlayerCount(String gid) throws IOException {
    _clnt.send(new String("command-name=rplayer_count_all_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activePlayerCount() throws IOException {
    _clnt.send(new String("command-name=player_count_active"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activeRPlayerCount() throws IOException {
    _clnt.send(new String("command-name=rplayer_count_active"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activePlayerCount(String gid) throws IOException {
    _clnt.send(new String("command-name=player_count_active_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activeRPlayerCount(String gid) throws IOException {
    _clnt.send(new String("command-name=rplayer_count_active_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent playerDetails(String gid) throws IOException {
    _clnt.send(new String("command-name=player_detail_all_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }
  

  public AdminEvent playerDetailsForAdmin(String gid) throws IOException {
    _clnt.send(new String("command-name=player_detail_admin_all_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }


  public AdminEvent playerDetail(String userid, String gid) throws IOException {
    _clnt.send(new String("command-name=player_detail_gid&userid=" + userid + "&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent rPlayerDetail(String gid) throws IOException {
    _clnt.send(new String("command-name=rplayer_detail_all_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent playerDetails() throws IOException {
    _clnt.send(new String("command-name=player_detail_all"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent playerDetail(String userid) throws IOException {
    _clnt.send(new String("command-name=player_detail&userid=" + userid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent rPlayerDetail() throws IOException {
    _clnt.send(new String("command-name=rplayer_detail_all"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activePlayerDetail(String gid) throws IOException {
    _clnt.send(new String("command-name=player_detail_active_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activeRPlayerDetail(String gid) throws IOException {
    _clnt.send(new String("command-name=rplayer_detail_active_gid&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activePlayerDetail() throws IOException {
    _clnt.send(new String("command-name=player_detail_active"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent activeRPlayerDetail() throws IOException {
    _clnt.send(new String("command-name=rplayer_detail_active"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent removePlayer(String gid, String playerName) throws
      IOException {
    _clnt.send(new String("command-name=remove_player&gid=" + gid +
                          "&playerid=" + playerName));
    return new AdminEvent(_clnt.get());
  }

  /*******************************GAME ****************************************/

  public AdminEvent gameCount() throws IOException {
    _clnt.send(new String("command-name=game_count"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent create() {
    return null;
  }

  public AdminEvent stop() {
    return null;
  }

  public AdminEvent gameDetail(String gid) throws IOException {
    _clnt.send(new String("command-name=game_detail&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent gameDetail() throws IOException {
    _clnt.send(new String("command-name=game_detailall"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent sngDetail() throws IOException {
    _clnt.send(new String("command-name=sng_detailall"));
    return new AdminEvent(_clnt.get());
  }
  public AdminEvent suspend(String gid) throws IOException {
    _clnt.send(new String("command-name=suspend&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent enablePlayerChat(String name) throws IOException {
    _clnt.send(new String("command-name=enable_chat&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent disablePlayerChat(String name) throws IOException {
    _clnt.send(new String("command-name=disable_chat&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent ban(String name) throws IOException {
    _clnt.send(new String("command-name=ban&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent unban(String name) throws IOException {
    System.out.println("in unban method");
    _clnt.send(new String("command-name=unban&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent suspend() throws IOException {
    _clnt.send(new String("command-name=suspendall"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent resume(String gid) throws IOException {
    _clnt.send(new String("command-name=resume&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent resume() throws IOException {
    _clnt.send(new String("command-name=resumeall"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent destroy(String gid) throws IOException {
    _clnt.send(new String("command-name=destroy&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent destroy() throws IOException {
    _clnt.send(new String("command-name=destroyall"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent broadcast(String message) throws IOException {
    _clnt.send(new String("command-name=broadcastall&message=" + message));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent broadcastGame(String gid, String message) throws IOException {
    _clnt.send(new String("command-name=broadcast&gid=" + gid + "&message=" +
                          message));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent broadcastAffiliate(String message, String affiliate) throws
      IOException {
    System.out.println(new String("command-name=broadcast&affiliate=" +
                                  affiliate
                                  + "&message=" +
                                  message));

    _clnt.send(new String("command-name=broadcast&affiliate=" +
                          affiliate
                          + "&message=" +
                          message));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent broadcastPlayer(String tid, String player_session, String message) throws
      IOException {
    _clnt.send(new String("command-name=message&session=" +
                          player_session
                          + "&message=" +
                          message+"&tid="+tid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent remove(String name, String gid) throws IOException {
    _clnt.send(new String("command-name=remove&gid=" + gid + "&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent mttRemove(String gid) throws IOException {
    _clnt.send(new String("command-name=mttremove&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent mttRegister(String gid, String name) throws IOException {
    _clnt.send(new String("command-name=mttregister&gid=" + gid + "&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent gameStart(String gid) throws IOException {
    _clnt.send(new String("command-name=gamestart&gid=" + gid));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent mttList() throws IOException {
     _clnt.send(new String("command-name=mttlist&mttlist=mttlist"));
     return new AdminEvent(_clnt.get());
  }
  public AdminEvent disable(String name) throws IOException {
    _clnt.send(new String("command-name=disable_chat&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent enable(String name) throws IOException {
    _clnt.send(new String("command-name=enable_chat&name=" + name));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent startServer() throws IOException {
    _clnt.send(new String("command-name=server-start"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent stopServer() throws IOException {
    _clnt.send(new String("command-name=server-stop"));
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent createGame(String[] params) throws IOException {
    String type = params[1];
    StringBuilder sb = new StringBuilder("command-name=create&type=").append(type);
    if (type.equals("holdem") || type.equals("omahahi") || type.equals("terminalholdem") ||
        type.equals("omahahilo") || type.equals("studhi") ||
        type.equals("studhilo") || type.equals("realholdem") ||
        type.equals("realomahahi") || type.equals("realomahahilo") ||
        type.equals("realstudhilo")|| type.equals("realterminalholdem") ) {
      String name = params[2];
      double minbet = Double.parseDouble(params[3]);
      double maxbet = Double.parseDouble(params[4]);
      int minP = Integer.parseInt(params[5]);
      int maxP = Integer.parseInt(params[6]);
      int rake = Integer.parseInt(params[7]);
      int maxRake = Integer.parseInt(params[8]);
      int conversion = Integer.parseInt(params[9]);
      String stack = params[10];
      String partner = params[11];
//      int hr = 0, lr = 0;
//      if (params.length > 12) {
//        hr = Integer.parseInt(params[12] == null ? "-1" : params[11]);
//        lr = Integer.parseInt(params[13] == null ? "-1" : params[12]);
//      }

      sb.append("&name=").append(name).append("&minbet=").append(minbet);
      sb.append("&maxbet=").append(maxbet).append("&maxp=").append(maxP);
      sb.append("&minp=").append(minP).append("&rake=").append(rake);
      sb.append("&max-rake=").append(maxRake);
      sb.append("&stack=").append(stack);
      sb.append("&conversion=").append(conversion).append("&affiliate=admin");
      sb.append("&partner=").append(partner);
    }
    _clnt.send(sb.toString());
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent createSitnGo(String[] params) throws IOException {
    int limit =Integer.parseInt(params[4]);
	double buyin = Double.parseDouble(params[5]);
    double fees = Double.parseDouble(params[6]);
    int chips = Integer.parseInt(params[7]);
    int tourbo = Integer.parseInt(params[8]);
    int maxP = Integer.parseInt(params[9]);
    StringBuilder sb = new StringBuilder("command-name=create&type=sitngo&buyin=");
    sb.append(buyin);
    sb.append("&name=").append(params[2]);
    sb.append("&game_type=").append(params[3]);
    sb.append("&limit=").append(limit);
    sb.append("&fees=").append(fees);
    sb.append("&chips=").append(chips);
    sb.append("&tourbo=").append(tourbo);
    sb.append("&pcount=").append(maxP);
    sb.append("&don=").append(params[10]);
    System.out.println("Sending ... " + sb.toString());
    _clnt.send(sb.toString());
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent createTourny(String[] params) throws IOException {
	int limit =Integer.parseInt(params[4]);
	int tourbo = Integer.parseInt(params[10]);
    int declInt = Integer.parseInt(params[11]);
    int regInt = Integer.parseInt(params[12]);
    int joinInt = Integer.parseInt(params[13]);
    double prize_pool = Double.parseDouble(params[16]);
    StringBuilder sb = new StringBuilder("command-name=create&type=mtt&name=");
    sb.append(params[2]);
    sb.append("&game_type=").append(params[3]);
    sb.append("&limit=").append(limit);
    sb.append("&buyin=").append(params[5]);
    sb.append("&fees=").append(params[6]);
    sb.append("&chips=").append(params[7]);
    sb.append("&maxp=").append(params[8]);
    sb.append("&schedule=").append(params[9]);
    sb.append("&tourbo=").append(tourbo);
    sb.append("&decl=").append(declInt);
    sb.append("&reg=").append(regInt);
    sb.append("&join=").append(joinInt);
    sb.append("&tourny_type=").append(params[14]);
    sb.append("&guaranteed=").append(params[15]);
    sb.append("&prize_pool=").append(prize_pool);
    sb.append("&affiliate=").append(params[17]);
    System.out.println("Sending ... " + sb.toString());
    _clnt.send(sb.toString());
    return new AdminEvent(_clnt.get());
  }

  public AdminEvent refreshBankRoll(String playerid) throws IOException {
    _clnt.send(new String("command-name=refresh-bankroll&name=" +
                          playerid));
    return new AdminEvent(_clnt.get());
  }

      public void process(String s){
        try {
            if (s.equals("shutdown")) {
             _clnt.send(new String("command-name=server-shutdown"));
             System.out.println(new AdminEvent(_clnt.get()));
           }
        }
          catch (Exception e) {
            e.printStackTrace();
          }
      }


  /* Console loop methods ****************************************/
  /**
   * Reads lines (words actually) and performs the appropriate
   * response...
   */
  public void consoleLoop() {
    System.out.print(
        "GameController application.  Type a command at the '>' prompt,\n" +
        "'quit' to end the server, or 'help' to get a list of commands.\n");
    // Drop into the loop.
    BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
    boolean ok = true;
    while (ok) {
      try {
        String s;
        System.out.print("> ");
        System.out.flush();
        s = dis.readLine();
        System.out.println(s);
        if (s.equals("quit")) {
          ok = false;
        }
        else if (s.equals("threads")) {
          System.out.println(threads());
        }
        else if (s.equals("summary")) {
            System.out.println(threads());
        }
        else if (s.startsWith("refresh-bankroll")) {
          String[] param = s.split(" ");
          if (param.length < 1) {
            System.out.println(
                "refresh-bankroll [player-name]        - refreshes the player bank roll \n\n");
          }
          else {
            System.out.println(refreshBankRoll(param[1]));
          }
        }
        else if (s.equals("exit-server")) {
          _clnt.send(new String("command-name=exit-server"));
        }
        else if (s.equals("server start")) {
          _clnt.send(new String("command-name=server-start"));
          System.out.println(new AdminEvent(_clnt.get()));
        }
        else if (s.equals("server stop")) {
          _clnt.send(new String("command-name=server-stop"));
          System.out.println(new AdminEvent(_clnt.get()));
        }
        else if (s.equals("server shutdown")) {
          _clnt.send(new String("command-name=server-shutdown"));
          System.out.println(new AdminEvent(_clnt.get()));
        }

        else if (s.startsWith("rplayer count")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "rplayer count|detail active|all  [gid] - Summary of players.\n");
          }
          else {
            if (param[2].equals("all")) {
              if (param.length < 4) {
                System.out.println(rPlayerCount());
              }
              else {
                System.out.println(rPlayerCount(param[3]));
              }
            }
            else if (param[2].equals("active")) {
              if (param.length < 4) {
                System.out.println(activeRPlayerCount());
              }
              else {
                System.out.println(activeRPlayerCount(param[3]));
              }
            }
          }
        }
        else if (s.startsWith("rplayer detail")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "rplayer count|detail active|all  [gid] - Summary of players.\n");
          }
          else {
            if (param[2].equals("all")) {
              if (param.length < 4) {
                System.out.println(rPlayerDetail());
              }
              else {
                System.out.println(rPlayerDetail(param[3]));
              }
            }
            else if (param[2].equals("active")) {
              if (param.length < 4) {
                System.out.println(activeRPlayerDetail());
              }
              else {
                System.out.println(activePlayerDetail(param[3]));
              }
            }
          }
        }

        else if (s.startsWith("player count")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "player count|detail active|all  [gid] - Summary of players.\n");
          }
          else {
            if (param[2].equals("all")) {
              if (param.length < 4) {
                System.out.println(playerCount());
              }
              else {
                System.out.println(playerCount(param[3]));
              }
            }
            else if (param[2].equals("active")) {
              if (param.length < 4) {
                System.out.println(activePlayerCount());
              }
              else {
                System.out.println(activePlayerCount(param[3]));
              }
            }
          }
        }
        else if (s.startsWith("player detail")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "player count|detail active|all|userid  [gid] - Summary of players.\n");
          }
          else {
            if (param[2].equals("all")) {
              if (param.length < 4) {
                System.out.println(playerDetails());
              }
              else {
                System.out.println(playerDetails(param[3]));
              }
            }
            else if (param[2].equals("active")) {
              if (param.length < 4) {
                System.out.println(activePlayerDetail());
              }
              else {
                System.out.println(activePlayerDetail(param[3]));
              }
            }
            else {
              if (param.length < 4) {
                System.out.println(playerDetail(param[2]));
              }
              else {
                System.out.println(playerDetail(param[2], param[3]));
              }
            }
          }
        }
        else if (s.startsWith("game detail")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "game count|detail|suspend|resume|destroy  [gid]|all - Summary of games.\n");
          }
          else {
            if (param[2].equals("all")) {
              System.out.println(gameDetail());
            }
            else {
              System.out.println(gameDetail(s.split(" ")[2]));
            }
          }
        }
        else if (s.startsWith("game count")) {
          System.out.println(gameCount());
        }

        else if (s.startsWith("broadcastAff")) {
          System.out.println(s);
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "broadcastAff AffiliateId message  - send the message to affilaite's players \n");
          }
          else {
            System.out.println(broadcastAffiliate(Base64.
                                                  encodeString(s.substring(s.indexOf(
                param[2]))), param[1]));
          }
        }

        else if (s.startsWith("broadcast")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "broadcast [gid]|all  - send the message to\n");
          }
          else {
            if (param[1].equals("all")) {
              System.out.println(broadcast(Base64.encodeString(s.substring(s.indexOf(
                  param[2])))));
            }
            else {
              System.out.println(broadcastGame(param[1],
                                           Base64.
                                           encodeString(s.substring(s.indexOf(param[2])))));
            }
          }
        }
        else if (s.startsWith("message")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "message sid \"Message\"  - send the message to player\n");
          }
          else {
            System.out.println(broadcastPlayer(param[1], param[2],
                                         Base64.
                                         encodeString(s.substring(s.indexOf(param[3])))));
          }
        }

        else if (s.startsWith("remove")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "remove name gid  - remove syntax\n");
          }
          else {
            System.out.println(remove(param[1], param[2]));
          }
        }

        else if (s.startsWith("gamestart")) {
          String[] param = s.split(" ");
          if (param.length < 2) {
            System.out.println(
                "gamestart gid  - remove syntax\n");
          }
          else {
            System.out.println(gameStart(param[1]));
          }
        }
        else if (s.startsWith("mttremove")) {
          String[] param = s.split(" ");
          if (param.length < 2) {
            System.out.println(
                "mttremove tid  - remove syntax\n");
          }
          else {
            System.out.println(mttRemove(param[1]));
          }
        }
        else if (s.startsWith("mttregister")) {
          String[] param = s.split(" ");
          if (param.length < 3) {
            System.out.println(
                "mttregister tid name  - remove syntax\n");
          }
          else {
            System.out.println(mttRegister(param[1], param[2]));
          }
        }
        else if (s.startsWith("mttlist")) {
          System.out.println(mttList());
        }
        else if (s.startsWith("ban")) {
          String[] param = s.split(" ");
          System.out.println(ban(param[1]));
        }
        else if (s.startsWith("unban")) {
          String[] param = s.split(" ");
          System.out.println(unban(param[1]));
        }
        else if (s.startsWith("chat enable")) {
          System.out.println(enablePlayerChat(s.split(" ")[2]));
        }
        else if (s.startsWith("chat disable")) {
          System.out.println(disablePlayerChat(s.split(" ")[2]));
        }
        else if (s.startsWith("game suspend")) {
          if (s.split(" ")[2].equals("all")) {
            System.out.println(suspend());
          }
          else {
            System.out.println(suspend(s.split(" ")[2]));
          }
        }
        else if (s.startsWith("game resume")) {
          if (s.split(" ")[2].equals("all")) {
            System.out.println(resume());
          }
          else {
            System.out.println(resume(s.split(" ")[2]));
          }
        }
        else if (s.startsWith("game destroy")) {
          if (s.split(" ")[2].equals("all")) {
            System.out.println(destroy());
          }
          else {
            System.out.println(destroy(s.split(" ")[2]));
          }
        }
        else if (s.startsWith("create mtt")) {
          String[] params = s.split(" ");
          if (params.length < 7) {
            System.out.println(
                "create mtt <name> <buy-in> <fees> <maxp> <schedule>  <decl-interval> <reg-interval> <join-interval>");
          }
          else {
            System.out.println(createTourny(params));
          }
        }
        else if (s.startsWith("create sitngo")) {
          String[] params = s.split(" ");
          if (params.length < 4) {
            System.out.println(
                "create sitngo <name> <buy-in> <fees> <max-players>");
          }
          else {
            System.out.println(createSitnGo(params));
          }
        }
        else if (s.startsWith("create")) {
          String[] params = s.split(" ");
          if (params.length < 8) {
            System.out.println(
                "create <game-type>  <name> <minbet> <maxbet> <minp> <maxp>  <rake%> <max-rake>  <affiliate-id>");
          }
          else {
            if (params[1].equals("holdem") || params[1].equals("omahahi") || params[1].equals("terminalholdem") ||
                params[1].equals("omahahilo") || params[1].equals("studhi") ||
                params[1].equals("studhilo") || params[1].equals("realholdem") ||
                params[1].equals("realomahahi") ||
                params[1].equals("realomahahilo") ||
                params[1].equals("realstudhi") ||
                params[1].equals("realstudhilo") || params[1].equals("realterminalholdem")) {
              System.out.println(createGame(params));
            }
          }
        }
        else if (s.length() > 0) {
          System.out.print("Help for GameServer Controller Commands:\n\n" +
                           "threads                               - Summary of server side threads.\n\n" +
                           "player count|detail active|all|userid  [gid|]- Summary of players.\n\n" +
                           "rplayer count|detail active|all  [gid|]- Summary of players.\n\n" +
                           "message [sid]  \"Message\"            - Send the message to required player \n\n" +
                           "remove name gid  - removes a player from the game\n\n" +
                           "ban name gid  - removes a player from the game\n\n" +
                           "unban name gid  - removes a player from the game\n\n" +
                           "chat disable|enable name  - enable or disable this player chat\n\n" +
                           "game count|detail|suspend|resume|destroy  [gid]|all - Summary of games.\n\n" +
                           "create game-type minBet maxBet minP max P rake rake_percent affiliate partner high_rank low_rank...            - Create a game where \n" +
                           "                                        type=holdem|omahahi|omahahilo|studhi|studhilo|realholdem..\n" +
                           "                                        affiiate and partners are lists represented as val1|val2.. are game params \n\n" +
                           "                                        high_rank and low_rank are integers from 0 - 100 and are optional \n\n" +
                           "create sitngo name buyin fees pCount       - Create a sitngo tourny\n\n" +
                           "server [start]|[shutdown]|[stop]      - Start and stop the server\n\n" +
                           "broadcast [gid|all|sid] \"Message\"     - Send the message to required tables\n\n" +
                           "broadcastAff affiliateId \"Message\"     - Send the message to affiliate's players tables\n\n" +
                           "exit-server                           - Kill the server\n\n" +
                           "refresh-bankroll [player-name]        - refreshes the player bank roll \n\n" +
                           "card [card-no] [player-name] [amount] - associates the player with a charged card \n\n" +
                           "mttlist                               - tournament listing \n\n" +
                           "mttremove tid                         - remove tournament \n\n" +
                           "mttregister tid name                  - tourny register \n\n" +
                           "gamestart gid                         - try starting the game again \n\n" +
                           "quit                                  - Exits the server controller\n\n");
        }
      }
      catch (Throwable e) {
        refreshConnection();
        e.printStackTrace();
      }
    }
  }
  
   /*** public static void main(String args[]) throws Exception {
        GameController pc = new GameController("127.0.0.1", 9895);
        System.out.println(pc.summary());
        System.out.println(pc.gameDetail());
    }***/
    

    public static void main(String args[]) throws Exception {
      GameController gc = new GameController(Configuration.instance().getProperty(
          "Admin.Network.server.ip"), Configuration.instance().getInt("Admin.Network.port"));
      if (args.length==0){
          gc.consoleLoop();
          System.exit(0);
      }
      else {
         gc.process(args[0]);
      }
    }
    
}
