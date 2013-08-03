package com.poker.net;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.Base64;
import com.agneya.util.Utils;
import com.golconda.db.DBException;
import com.golconda.db.DBPlayerUtil;
import com.golconda.db.PlayerPreferences;
import com.golconda.game.Game;
import com.golconda.game.Player;
import com.golconda.game.Presence;
import com.golconda.message.Response;
import com.golconda.net.NWProcessor;
import com.golconda.net.event.AdminEvent;
import com.poker.common.message.ResponseInt;
import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;
import com.poker.game.poker.Tourny;
import com.poker.game.poker.TournyController;
import com.poker.game.poker.pokerimpl.Holdem;
import com.poker.game.poker.pokerimpl.HoldemSitnGo;
import com.poker.game.poker.pokerimpl.OmahaHi;
import com.poker.game.poker.pokerimpl.OmahaHiLo;
import com.poker.game.poker.pokerimpl.OmahaSitnGo;
import com.poker.game.poker.pokerimpl.RealHoldem;
import com.poker.game.poker.pokerimpl.RealOmahaHi;
import com.poker.game.poker.pokerimpl.RealOmahaHiLo;
import com.poker.game.poker.pokerimpl.RealStudHi;
import com.poker.game.poker.pokerimpl.RealStudHiLo;
import com.poker.game.poker.pokerimpl.RealTermHoldem;
import com.poker.game.poker.pokerimpl.Stud;
import com.poker.game.poker.pokerimpl.StudHiLo;
import com.poker.game.poker.pokerimpl.TermHoldem;
import com.poker.nio.Client;
import com.poker.nio.Handler;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;
import com.poker.server.GameServer;
import com.poker.server.LogObserver;


public class CommandProcessor
    implements NWProcessor {
  static Logger _cat = Logger.getLogger(CommandProcessor.class.getName());
  public CommandProcessor() {
  }

  public AdminEvent process(AdminEvent ae) {
    String cn = ae.get("command-name").trim();
    _cat.info("Admin Command  " + ae + ", command=" + cn);
    AdminEvent resp = null;
    try {
            if (cn.equals("threads")) {
              resp = new AdminEvent(threads());
            }
            else if (cn.equals("summary")) {
            	resp = new AdminEvent(summary());
            }
            else if (cn.equals("server-start")) {
              try {
                com.poker.server.GameServer.startAcceptorThread();
              }
              catch (Exception e) {
                e.printStackTrace();
                return new AdminEvent("server=start-failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("server=started");
            }
            else if (cn.equals("server-stop")) {
              try {
                GameProcessor.destroyAll();
                GameServer.stopAcceptorThread();
                com.poker.game.poker.Auditor.flush();
              }
              catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
                return new AdminEvent("server=stop-failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("server=stopped");
              System.out.println("Server stopped -- destroyed.........");
              System.exit(0);
            }
            else if (cn.equals("server-shutdown")) {
              try {
                GameProcessor.broadcast(Base64.encodeString("Server Booting. After completing the current hand, the table will be closed."));
                GameProcessor.suspendAll();
                GameServer._state = GameServer.SUSPEND;
                boolean game_not_up = true;
                int count = 0;
                ArrayList toRemove = new ArrayList(Game.map.size());
                while (game_not_up && count < 50) {
                  boolean game_over = true;
                  Iterator i = Game.map.values().iterator();
                  while (i.hasNext()) {
                    Game g = (Game) i.next();
                    if (g._inProgress == true) {
                      Thread.currentThread().sleep(30000);
                      count++;
                      game_over = false;
                    }
                    else {
                      if (g instanceof Poker) {
                        Poker pg = (Poker) g;
                        _cat.finest("Poker game stopped " + pg);
                        PokerPresence[] pl = pg.allPlayers( -1);
                        for (int j = 0; pl != null && j < pl.length; j++) {
                          Player p = pl[j].player();
                          _cat.finest("Killing presence_ " + p);
                          if (p instanceof GamePlayer) {
                            GamePlayer gp = (GamePlayer) p;
                            gp.deliver(new com.poker.common.message.ResponseString(1,
                                com.golconda.message.Response.R_TABLE_CLOSED,
                                pl[j].getGameName()));
                            gp.kill(pl[j], false);
                            gp.kill(pl[j], true);
                          }
                        }
                        toRemove.add(pg);
                      }
                    }
                  }
                  game_not_up = !game_over;
                }
        
                Game.removeAll(toRemove);
                if (game_not_up) {
                  _cat.log(Level.WARNING, "Some of the games are stuck...kill them manually ");
                  Iterator i = Game.map.values().iterator();
                  while (i.hasNext()) {
                    Game g = (Game) i.next();
                    _cat.warning(g.toString());
                  }
                }
        
                ConcurrentHashMap h = Handler.registry();
                for (Enumeration enumt = h.elements(); enumt.hasMoreElements(); ) {
                  Handler t = (Handler) enumt.nextElement();
                  Client attach = t.attachment();
                  if (attach instanceof GamePlayer) {
                    GamePlayer gp = (GamePlayer) attach;
                    _cat.finest("Killing player_ " + gp);
                    gp.kill();
                  }
                }
                System.out.println("Server stopped gracefully.........");
                GameServer._state = GameServer.SHUTTING;
                com.poker.game.poker.Auditor.flush();
              }
              catch (Exception e) {
                e.printStackTrace();
                return new AdminEvent("server=stop-failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("server=stopped");
            }
        
            else if (cn.equals("rplayer_count_active")) {
              resp = new AdminEvent("active_player=" + activeRPlayerCount());
            }
            else if (cn.equals("rplayer_count_active_gid")) {
              resp = new AdminEvent("active_player=" +
                                    activeRPlayerCount(ae.get("gid")));
            }
            else if (cn.equals("rplayer_detail_active")) {
              resp = new AdminEvent(activeRPlayerDetail());
            }
            else if (cn.equals("rplayer_detail_active_gid")) {
              resp = new AdminEvent(activeRPlayerDetail(ae.get("gid")));
            }
        
            else if (cn.equals("player_count_all")) {
              resp = new AdminEvent("total_player=" + playerCount());
            }
            else if (cn.equals("player_count_all_gid")) {
              resp = new AdminEvent("total_player=" + playerCount(ae.get("gid")));
            }
            else if (cn.equals("player_count_active")) {
              resp = new AdminEvent("active_player=" + activePlayerCount());
            }
            else if (cn.equals("player_count_active_gid")) {
              resp = new AdminEvent("active_player=" +   activePlayerCount(ae.get("gid")));
            }
            else if (cn.equals("player_detail_all")) {
              resp = new AdminEvent(playerDetails());
            }
            else if (cn.equals("player_detail_all_gid")) {
              resp = new AdminEvent(playerDetails(ae.get("gid")));
            }
            else if (cn.equals("player_detail_admin_all_gid")) {
              resp = new AdminEvent(playerDetails(ae.get("gid")));
            }
            else if (cn.equals("player_detail_adminall_gid")) {
              resp = new AdminEvent(playerDetails(ae.get("gid")));
            }
        
            else if (cn.equals("player_detail")) {
              resp = new AdminEvent(playerDetail(ae.get("userid")));
            }
            else if (cn.equals("player_detail_gid")) {
              resp = new AdminEvent(playerDetail(ae.get("userid"), ae.get("gid")));
            }
            else if (cn.equals("player_detail_active")) {
              resp = new AdminEvent(activePlayerDetail());
            }
            else if (cn.equals("player_detail_active_gid")) {
              resp = new AdminEvent(activePlayerDetail(ae.get("gid")));
            }
            else if (cn.equals("remove_player")) {
              removePlayer(ae.get("gid"), ae.get("playerid"));
              resp = new AdminEvent("Removed_Player=" + ae.get("playerid"));
            }
            else if (cn.equals("disable_chat")) {
              disablePlayerChat(ae.get("name"));
              resp = new AdminEvent("Disable_Chat=" + ae.get("name"));
            }
            else if (cn.equals("enable_chat")) {
              enablePlayerChat(ae.get("name"));
              resp = new AdminEvent("Enable_Chat=" + ae.get("name"));
            }
            else if (cn.equals("ban")) {
              ban(ae.get("name"));
              resp = new AdminEvent("Ban=" + ae.get("name"));
            }
            else if (cn.equals("unban")) {
              unban(ae.get("name"));
              resp = new AdminEvent("Unban=" + ae.get("name"));
            }
            else if (cn.equals("game_count")) {
              resp = new AdminEvent("game_count=" + Game.listAll().length);
            }
            else if (cn.equals("game_detailall")) {
              resp = new AdminEvent(gameDetail());
            }
            else if (cn.equals("sng_detailall")) {
                resp = new AdminEvent(sngDetail());
              }
            else if (cn.equals("game_detail")) {
              resp = new AdminEvent(gameDetail(ae.get("gid")));
            }
            else if (cn.equals("suspend")) {
              String gid = ae.get("gid");
              GameProcessor.suspend(gid);
              resp = new AdminEvent("suspended=" + gid);
            }
            else if (cn.equals("suspendall")) {
              GameProcessor.suspendAll();
              resp = new AdminEvent("suspended=all");
            }
            else if (cn.equals("resume")) {
              String gid = ae.get("gid");
              try {
                GameProcessor.resume(gid);
              }
              catch (Exception e) {
                e.printStackTrace();
                return new AdminEvent("resumed=failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("resumed=" + gid);
            }
            else if (cn.equals("resumeall")) {
              try {
                GameProcessor.resumeAll();
              }
              catch (Exception e) {
                e.printStackTrace();
                _cat.finest(new AdminEvent("resumed=failed&mesg=" + e.getMessage()).toString());
                return new AdminEvent("resumed=failed&mesg=" + e.getMessage());
              }
              _cat.finest(new AdminEvent("resumed=all").toString());
              resp = new AdminEvent("resumed=all");
            }
            else if (cn.equals("destroy")) {
              String gid = ae.get("gid");
              try {
                GameProcessor.destroy(gid);
              }
              catch (Exception e) {
                e.printStackTrace();
                return new AdminEvent("destroy=failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("destroyed=" + gid);
            }
            else if (cn.equals("destroyall")) {
              try {
                GameProcessor.destroyAll();
              }
              catch (Exception e) {
                e.printStackTrace();
                return new AdminEvent("resumed=failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("destroyed=all");
            }
            else if (cn.equals("remove")) {
              String gid = ae.get("gid");
              String name = ae.get("name");
              try {
                GameProcessor.removePresence(name, gid);
              }
              catch (Exception e) {
                e.printStackTrace();
                return new AdminEvent("remove=failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("removed=" + gid);
            }
        
            else if (cn.equals("gamestart")) {
              String gid = ae.get("gid");
              Game g = Game.game(gid);
        
              if (g == null) {
                resp = new AdminEvent("gamestart=failed&msg=does not exists");
              }
              else {
                g.start();
                resp = new AdminEvent("gamestart=" + gid);
              }
            }
            else if (cn.equals("mttremove")) {
              String gid = ae.get("gid");
              TournyController tc = TournyController.instance();
              Tourny t = tc.getTourny(gid);
              if (t == null) {
                resp = new AdminEvent("removedmtt=failed&msg=does not exists");
              }
              else {
                tc.removeTourny(gid);
                resp = new AdminEvent("removedmtt=" + gid);
              }
            }
            else if (cn.equals("mttlist")) {
              TournyController tc = TournyController.instance();
              Tourny t[] = tc.listAll();
              StringBuilder sbuf = new StringBuilder("TL=");
              for (int i = 0; i < t.length; i++) {
                sbuf.append(t[i].stringValue()).append("&");
              }
              resp = new AdminEvent("mttlist=" + t.length + "&" + sbuf.toString());
            }
            else if (cn.equals("mttregister")) {
              String gid = ae.get("gid");
              String name = ae.get("name");
              TournyController tc = TournyController.instance();
              Tourny t = tc.getTourny(gid);
              if (t == null) {
                resp = new AdminEvent("mttregister=failed&msg=does not exists");
              }
              else {
                try {
                  t.register(name);
                  _cat.finest("Found " + t);
                }catch (Exception e){
                  e.printStackTrace();
                }
                resp = new AdminEvent("mttregister=success&msg=Successfully registered " + name);
              }
            }
            else if (cn.equals("create")) {
              String type = ae.get("type");
              if (type.equals("holdem") || type.equals("omahahi") || type.equals("terminalholdem") ||
                  type.equals("omahahilo") || type.equals("studhi") ||
                  type.equals("studhilo") || type.equals("realholdem") ||
                  type.equals("realomahahi") ||
                  type.equals("realomahahilo") || type.equals("realstudhi") ||
                  type.equals("realstudhilo") || type.equals("realterminalholdem")) {
                String name = ae.get("name");
                double minbet = Double.parseDouble(ae.get("minbet"));
                double maxbet = Double.parseDouble(ae.get("maxbet"));
                int minP = Integer.parseInt(ae.get("minp"));
                int maxP = Integer.parseInt(ae.get("maxp"));
                int rake = Integer.parseInt(ae.get("rake"));
                String stack = ae.get("stack");
                //double maxRakeVal = Double.parseDouble(ae.get("max-rake"));
                String mrs = (String) ae.get("max-rake");
                _cat.finest("Rake = " + mrs);
                String[] mral = mrs.split("\\|");
                double maxRake[] = new double[maxP];
                int j = 0, k = 0;
                for (; k < mral.length && j < maxP; k++, j++) {
                  maxRake[j] = Double.parseDouble(mral[k]);
                  j++;
                  if (j >= maxP) {
                    break;
                  }
                  maxRake[j] = Double.parseDouble(mral[k]);
                  _cat.finest("Rake = " + mral[k]);
                }
                for (int l = k; l < maxP; l++) {
                  maxRake[l] = maxRake[k];
                }
        
                String affiliate = ae.get("affiliate");
                String partner = ae.get("partner");
                int rank = Integer.parseInt(ae.get("high_rank") == null ? "-1" :
                                            ae.get("high_rank"));
                int lr = Integer.parseInt(ae.get("low_rank") == null ? "-1" :
                                          ae.get("low_rank"));
        
                try {
                  createGames(type, name, minbet,
                      maxbet, minP, maxP, rake, maxRake,
                      affiliate == null || affiliate.trim().equals("") ? null :
                      affiliate.split("\\|"),
                      partner == null || partner.trim().equals("") ? null :
                      partner.split("\\|"), rank, stack);
                }
                catch (Exception e) {
                  e.printStackTrace();
                  return new AdminEvent("create=failed&mesg=" + e.getMessage());
                }
                  return new AdminEvent("created=" + name);
              }
              else if (type.equals("bingo")) {
                throw new IllegalStateException("Not implemented--Bingo");
              }
              else if (type.equals("mtt")) {
                  try {
                	  String name = ae.get("name");
  	                String game_type = ae.get("game_type");
  	                String tourny_type =  ae.get("tourny_type");
  	                String gauranteed =  ae.get("guaranteed");
  	                String schedule = ae.get("schedule");
  	                double buyin = Double.parseDouble(ae.get("buyin"));
  	                double fees = Double.parseDouble(ae.get("fees"));
  	                double prize_pool = Double.parseDouble(ae.get("prize_pool"));
  	                int chips = Integer.parseInt(ae.get("chips"));
  	                int limit = Integer.parseInt(ae.get("limit"));
  	                int tourbo = Integer.parseInt(ae.get("tourbo"));
  	                int maxP = Integer.parseInt(ae.get("maxp"));
  	                int declInt = Integer.parseInt(ae.get("decl"));
  	                int regInt = Integer.parseInt(ae.get("reg"));
  	                int joinInt = Integer.parseInt(ae.get("join"));
	        
	                String schp[] = schedule.split(":");
	                if (schp.length != 5) {
	                  return new AdminEvent("create=failed&mesg=Incorrect schedule");
	                }
	                int sc[] = new int[5];
	                for (int i = 0; i < 5; i++) {
	                  sc[i] = (schp[i].equals("*")) ? -1 : Integer.parseInt(schp[i]);
	                }
	        
	                int gtype=PokerGameType.HoldemTourny;
                	  if (game_type.equals("holdem")){
  	                	gtype=PokerGameType.HoldemTourny;
  	                }
  	                else if ( game_type.equals("omahahi") ){
  	                	gtype = PokerGameType.OmahaHiTourny;
  	                }else if ( game_type.equals("realomahahi") ){
	                	gtype = PokerGameType.Real_OmahaHiTourny;
	                }
	                else if ( game_type.equals("realholdem") ){
	                	gtype = PokerGameType.Real_HoldemTourny;
	                }
  	                else {
  	                	return new AdminEvent("create=failed&mesg=Unknown gametype-" + game_type);
  	                }
                	  com.poker.server.GameServer.createTourny(name, gtype, tourny_type, gauranteed, prize_pool, limit, tourbo, sc, buyin, fees,chips ,maxP, declInt, regInt, joinInt);
	                  //com.poker.server.GameServer.createTourny(name, gtype, limit, tourbo, sc,
	                   //   buyin, fees,chips ,maxP, declInt, regInt, joinInt);
	                  resp = new AdminEvent("created=" + name);
                }
                catch (Exception e) {
                  e.printStackTrace();
                  return new AdminEvent("create=failed&mesg=" + e.getMessage());
                }
              }
              else if (type.equals("sitngo")) {
                try {
	                String name = ae.get("name");
	                String game_type = ae.get("game_type");
	                double buyin = Double.parseDouble(ae.get("buyin"));
	                double fees = Double.parseDouble(ae.get("fees"));
	                int chips = Integer.parseInt(ae.get("chips"));
	                int pcount = Integer.parseInt(ae.get("pcount"));
	                int tourbo = Integer.parseInt(ae.get("tourbo"));
	                int limit = Integer.parseInt(ae.get("limit"));
	                String don = ae.get("don");
	                int gtype=PokerGameType.HoldemSitnGo;
	                if (game_type.equals("holdem")){
	                	gtype=PokerGameType.HoldemSitnGo;
	                }
	                else if ( game_type.equals("omahahi") ){
	                	gtype = PokerGameType.OmahaHiSitnGo;
	                }
	                else if ( game_type.equals("realomahahi") ){
	                	gtype = PokerGameType.Real_OmahaHiSitnGo;
	                }
	                else if ( game_type.equals("realholdem") ){
	                	gtype = PokerGameType.Real_HoldemSitnGo;
	                }
	                else {
	                	return new AdminEvent("create=failed&mesg=Unknown gametype-" + game_type);
	                }
	                com.poker.server.GameServer.createSitnGo(name, gtype, limit, tourbo, buyin, fees, chips, pcount, don);
	                resp = new AdminEvent("created=" + name);
                }
                catch (Exception e) {
                  return new AdminEvent("create=failed&mesg=" + e.getMessage());
                }
              }
            }
            else if (cn.equals("broadcast")) {
              String aff = ae.get("affiliate");
              try {
                if (aff == null || aff.length() < 1) {
                  String gid = ae.get("gid");
                  GameProcessor.broadcastGame(gid, ae.get("message"));
                  resp = new AdminEvent("broadcastedtoGame=" + gid);
                }
                else {
                  GameProcessor.broadcastAffiliate(ae.get("message"), aff);
                  resp = new AdminEvent("broadcastedtoAffiliateGames=" + aff);
                }
              }
              catch (Exception e) {
                return new AdminEvent("broadcast=failed&mesg=" + e.getMessage());
              }
            }
            else if (cn.equals("message")) {
              String tid = ae.get("tid");
              String session = ae.get("session");
              try {
                GameProcessor.broadcastPlayer(tid, session, ae.get("message"));
              }
              catch (Exception e) {
                return new AdminEvent("message=failed&mesg=" + e.getMessage());
              }
              resp = new AdminEvent("broadcastedToPlayerSession=" + session);
            }
            else if (cn.equals("exit-server")) {
              System.exit(0);
            }
            else if (cn.equals("broadcastall")) {
              GameProcessor.broadcast(ae.get("message"));
              resp = new AdminEvent("broadcasted=all");
            }
            else if (cn.equals("refresh-bankroll")) {
              String name = ae.get("name");
              resp = new AdminEvent("result=" + refreshBankroll(name));
            }
            else {
                throw new IllegalStateException("Unknown command " + cn);
            }
    }
    catch (Throwable t){
        t.printStackTrace();
            resp = new AdminEvent("message=failed&mesg=" + t.getMessage());
    }
    _cat.info(resp.toString());
    return resp;
  }



      public static String createGames(String type, String name, double minbet,
                                    double maxbet, int minp, int maxp, int rake,
                                    double maxRake[], String[] affiliate,
                                    String[] partner, int rank, String stack) throws DBException {

        Poker g = null;
        LogObserver lob = LogObserver.instance();
        int gameTypeId = -1;
        double sb[] = Utils.integralDivide(minbet, 2);
        double ante[] = Utils.integralDivide(minbet, 5);
       if (type.equals("holdem")) {
          g = new Holdem(name, minp, maxp, rake, maxRake, affiliate, partner,
                         lob);
          ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
          gameTypeId = PokerGameType.Play_Holdem;
        }
       else if (type.equals("terminalholdem")) {
           g = new TermHoldem(name, minp, maxp, rake, maxRake, affiliate, partner,
                          lob);
           ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
           gameTypeId = PokerGameType.Play_TermHoldem;
         }
        else if (type.equals("omahahi")) {
          g = new OmahaHi(name, minp, maxp, rake, maxRake, affiliate, partner,
                          lob);
          ((OmahaHi) g).setArgs(minbet, maxbet, sb[1], minbet);
          gameTypeId = PokerGameType.Play_OmahaHi;
        }
        else if (type.equals("omahahilo")) {
          g = new OmahaHiLo(name, minp, maxp, rake, maxRake, affiliate, partner,
                            lob);
          ((OmahaHiLo) g).setArgs(minbet, maxbet, sb[1], minbet);
          gameTypeId = PokerGameType.Play_OmahaHiLo;
        }
        else if (type.equals("studhi")) {
          g = new Stud(name, minp, maxp, rake, maxRake, affiliate, partner, lob);
          ((Stud) g).setArgs(minbet, maxbet, ante[1], minbet);
          gameTypeId = PokerGameType.Play_Stud;
        }
        else if (type.equals("studhilo")) {
          g = new StudHiLo(name, minp, maxp, rake, maxRake, affiliate, partner,
                           lob);
          ((StudHiLo) g).setArgs(minbet, maxbet, ante[1], minbet);
          gameTypeId = PokerGameType.Play_StudHiLo;
        }
        else if (type.equals("realtermholdem")) {
          g = new RealTermHoldem(name, minp, maxp, rake, maxRake, affiliate,
                             partner, lob);
          ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
          gameTypeId = PokerGameType.Real_Holdem;
        }
        else if (type.equals("realholdem")) {
            g = new RealHoldem(name, minp, maxp, rake, maxRake, affiliate,
                               partner, lob);
            ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
            gameTypeId = PokerGameType.Real_Holdem;
          }
        else if (type.equals("realomahahi")) {
          g = new RealOmahaHi(name, minp, maxp, rake, maxRake, affiliate,
                              partner, lob);
          ((RealOmahaHi) g).setArgs(minbet, maxbet, sb[1], minbet);
          gameTypeId = PokerGameType.Real_OmahaHi;
        }
        else if (type.equals("realomahahilo")) {
          g = new RealOmahaHiLo(name, minp, maxp, rake, maxRake, affiliate,
                                partner, lob);
          ((RealOmahaHiLo) g).setArgs(minbet, maxbet, sb[1], minbet);
          gameTypeId = PokerGameType.Real_OmahaHiLo;
        }
        else if (type.equals("realstudhi")) {
          g = new RealStudHi(name, minp, maxp, rake, maxRake, affiliate,
                             partner, lob);
          ((RealStudHi) g).setArgs(minbet, maxbet, ante[1], minbet);
          gameTypeId = PokerGameType.Real_Stud;
        }
        else if (type.equals("realstudhilo")) {
          g = new RealStudHiLo(name, minp, maxp, rake, maxRake, affiliate,
                               partner, lob);
          ((RealStudHiLo) g).setArgs(minbet, maxbet, ante[1], minbet);
          gameTypeId = PokerGameType.Real_StudHiLo;
        }
          else {
              throw new IllegalStateException("Unknown game type " + type);
          }

        if (rank != -1) {
          g.setRank(rank);
        }
        
        if (stack.equals("deep")){
        	g.setDeepStack();
        }
        else if (stack.equals("shallow")){
        	g.setShallowStack();
        }
        else {
        	g.setNormalStack();
        }


        //DBGame dbg = new DBGame(gameTypeId, name, "", null,                     
          //              partner, -1,
           //             minp, maxp, minbet, maxbet,
           //             sb[1], minbet, 4, 4);
        
        //dbg.save();
        // save the game in T_GAME
        //dbg.savePrivateGame();
        _cat.info("Game created " + g);
        return name;

      }

  /*
   * List the threads running in the server.
   */
  private static String threads() {
    Thread[] tarray = new Thread[Thread.activeCount() + 10];
    int ThreadCount = Thread.enumerate(tarray);
    StringBuilder sb = new StringBuilder("thread-count=").append(ThreadCount).
        append("&");
    for (int i = 0; i < ThreadCount; i++) {
      sb.append("thread-number").append(i).append("=").append(tarray[i].
          toString()).append("|").append( (tarray[i].isAlive() ? " Alive (" :
                                           " Dead (")).append("|").append(
                                               tarray[i].getClass().getName()).
          append("&");
    }
    return sb.toString();
  }
  
  private static String summary(){
    StringBuilder sb = new StringBuilder();
    int ap=0, at=0, allt=0;
    long last_hand=0; String last_game="";
	long total_games_played=0;
	
	Game[] gm = Game.listAll();
	allt = gm.length;
    for (int j = 0; j < allt; j++) {
      if (! (gm[j] instanceof Poker)) {
        continue;
      }
      if (last_hand < gm[j].grid()){
    	  last_hand = gm[j].grid();
    	  last_game = gm[j].name();
      }
      PokerPresence[] p = (PokerPresence[])gm[j].allPlayers( -1);
      if (p.length > 0){
    	  at++;
    	  ap+=p.length;
      }
    }
   
    
    sb.append("total-tables=").append(allt);
    sb.append("&active-tables=").append(at);
    sb.append("&active-players=").append(ap);
    if (at==0){
	    sb.append("&last-hand=na");
	    sb.append("&total_games_played=na");
    }
    else {
	    sb.append("&last-hand=").append(last_game).append("-").append(last_hand);
	    sb.append("&total_games_played=").append(last_hand-10000);
    }
    
    return sb.toString();
  }

  /*************************************PLAYER*********************************/
  private static String activePlayerDetail(String gid) {
    PokerPresence[] p = (PokerPresence[])Game.game(gid).activePlayers();
    StringBuilder buf = new StringBuilder("count=" + p.length);
    for (int i = 0; i < p.length; i++) {
      buf.append("&player").append(i).append("=").append(p[i].name()).append(
          "|");
      buf.append(gid).append("|");
      buf.append(p[i].netWorthString()).append("|");
      buf.append(p[i].currentRoundBetRoundedString()).append("|");
      buf.append(p[i].gender()).append("|");
      buf.append(p[i].status()).append("|");
      buf.append(p[i].getHand().getAllCardsString()).append("|");

      // if he is a game player
      if (p[i].player() instanceof GamePlayer) {
        GamePlayer gp = (GamePlayer) p[i].player();
        buf.append(gp.handler()._id).append("|");
        buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
        buf.append(gp.getPreferences());
      }
    }
    return buf.toString();
  }

  private static String activeRPlayerDetail(String gid) {
    PokerPresence[] p =(PokerPresence[])Game.game(gid).activePlayers();
    StringBuilder buf = new StringBuilder("count=" + p.length);
    for (int i = 0; i < p.length && !p[i].isShill(); i++) {
      buf.append("&player").append(i).append("=").append(p[i].name()).append(
          "|");
      buf.append(gid).append("|");
      buf.append(p[i].netWorthString()).append("|");
      buf.append(p[i].currentRoundBetRoundedString()).append("|");
      buf.append(p[i].gender()).append("|");
      buf.append(p[i].status()).append("|");
      buf.append(p[i].getHand().getAllCardsString()).append("|");

      // if he is a game player
      if (p[i].player() instanceof GamePlayer) {
        GamePlayer gp = (GamePlayer) p[i].player();
        buf.append(gp.handler()._id).append("|");
        buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
        buf.append(gp.getPreferences());
      }
    }
    return buf.toString();
  }

  private static String activePlayerDetail() {
    Game[] gm = Game.listAll();
    StringBuilder buf = new StringBuilder("game_count=" + gm.length);
    for (int j = 0; j < gm.length; j++) {
      PokerPresence[] p = (PokerPresence[])gm[j].activePlayers();
      buf.append("&player_count").append(j).append("=").append(p.length);
      for (int i = 0; i < p.length; i++) {
        buf.append(",player").append(i).append("=").append(p[i].name()).append(
            "|");
        buf.append(gm[j].name()).append("|");
        buf.append(p[i].netWorthString()).append("|");
        buf.append(p[i].currentRoundBetRoundedString()).append("|");
        buf.append(p[i].gender()).append("|");
        buf.append(p[i].status()).append("|");
        buf.append(p[i].getHand().getAllCardsString()).append("|");

        // if he is a game player
        if (p[i].player() instanceof GamePlayer) {
          GamePlayer gp = (GamePlayer) p[i].player();
          buf.append(gp.handler()._id).append("|");
          buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
          buf.append(gp.getPreferences());
        }
      }
    }
    return buf.toString();
  }

  private static String activeRPlayerDetail() {
    Game[] gm = Game.listAll();
    StringBuilder buf = new StringBuilder("game_count=" + gm.length);
    for (int j = 0; j < gm.length; j++) {
      PokerPresence[] p = (PokerPresence[])gm[j].activePlayers();
      buf.append("&player_count").append(j).append("=").append(p.length);
      for (int i = 0; i < p.length && !p[i].isShill(); i++) {
        buf.append(",player").append(i).append("=").append(p[i].name()).append(
            "|");
        buf.append(gm[j].name()).append("|");
        buf.append(p[i].netWorthString()).append("|");
        buf.append(p[i].currentRoundBetRoundedString()).append("|");
        buf.append(p[i].gender()).append("|");
        buf.append(p[i].status()).append("|");
        buf.append(p[i].getHand().getAllCardsString()).append("|");

        // if he is a game player
        if (p[i].player() instanceof GamePlayer) {
          GamePlayer gp = (GamePlayer) p[i].player();
          buf.append(gp.handler()._id).append("|");
          buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
          buf.append(gp.getPreferences());
        }
      }
    }
    return buf.toString();
  }

  private static String playerDetails(String gid) {
    PokerPresence[] p = (PokerPresence[])Game.game(gid).allPlayers( -1);
    StringBuilder buf = new StringBuilder("count=" + p.length);
    for (int i = 0; i < p.length; i++) {
      buf.append("&player").append(i).append("=").append(p[i].name()).append(
          "|");
      buf.append(gid).append("|");
      buf.append(p[i].netWorthString()).append("|");
      buf.append(p[i].currentRoundBetRoundedString()).append("|");
      buf.append(p[i].gender()).append("|");
      buf.append(p[i].status()).append("|");
      buf.append(p[i].getHand().getAllCardsString()).append("|");
      // if he is a game player
      if (p[i].player() instanceof GamePlayer) {
        GamePlayer gp = (GamePlayer) p[i].player();
        buf.append(gp.handler()._id).append("|");
        buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
        buf.append(gp.getPreferences());
      }
    }
    return buf.toString();
  }

  private static String playerDetails() {
    Game[] gm = Game.listAll();
    StringBuilder buf = new StringBuilder();
    buf.append("game-count=").append(gm.length);
    for (int j = 0; j < gm.length; j++) {
      if (! (gm[j] instanceof Poker)) {
        continue;
      }
      PokerPresence[] p = (PokerPresence[])gm[j].allPlayers( -1);
      buf.append("&player").append(j).append("=count=").append(p.length);
      for (int i = 0; i < p.length; i++) {
        buf.append(",player").append(i).append("=").append(p[i].name()).append(
            "|");
        buf.append(p[i].getGameName()).append("|");
        buf.append(p[i].netWorthString()).append("|");
        buf.append(p[i].currentRoundBetRoundedString()).append("|");
        buf.append(p[i].gender()).append("|");
        buf.append(p[i].status()).append("|");
        buf.append(p[i].getHand().getAllCardsString()).append("|");
        // if he is a game player denise|22753|292.00|28.00|0|2068|4049560754407434296|127.0.0.1|130818
        if (p[i].player() instanceof GamePlayer) {
          GamePlayer gp = (GamePlayer) p[i].player();
          buf.append(gp.handler()._id).append("|");
          buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
          buf.append(gp.getPreferences());
        }
      }
    }
    return buf.toString();
  }

  private static String playerDetail(String userid) {
    GamePlayer gp = GamePlayer.getPlayer(userid);
    if (gp == null) {
      return "count=0";
    }
    StringBuilder buf = new StringBuilder();
    int i = 0;
    Enumeration ep = gp.getPresenceList();
    for (; ep.hasMoreElements(); ) {
      PokerPresence p = (PokerPresence) ep.nextElement();
      if (p.isRemoved())continue;
      i++;
      buf.append("player").append(i).append("=").append(p.name()).append(
          "|");
      buf.append(p.getGameName()).append("|");
      buf.append(p.netWorthString()).append("|");
      buf.append(p.currentRoundBetRoundedString()).append("|");
      buf.append(p.gender()).append("|");
      buf.append(p.status()).append("|");
      buf.append(p.getHand().getAllCardsString()).append("|");
      // if he is a game player denise|22753|292.00|28.00|0|2068|4049560754407434296|127.0.0.1|130818
      buf.append(gp.handler()._id).append("|");
      buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
      buf.append(gp.getPreferences()).append("&");
    }
    buf.append("count=").append(i);
    System.out.println(buf.toString());
    return buf.toString();
  }

  private static String playerDetail(String userid, String gid) {
    GamePlayer gp = GamePlayer.getPlayer(userid);
    if (gp == null) {
      return "player=";
    }
    StringBuilder buf = new StringBuilder();
    int i = 0;
    Enumeration ep = gp.getPresenceList();
    boolean found = false;
    for (; ep.hasMoreElements(); i++) {
      PokerPresence p = (PokerPresence) ep.nextElement();
      if (p.getGameName() == gid) {
        buf.append("player").append("=").append(p.name()).append(
            "|");
        buf.append(p.getGameName()).append("|");
        buf.append(p.netWorthString()).append("|");
        buf.append(p.currentRoundBetRoundedString()).append("|");
        buf.append(p.gender()).append("|");
        buf.append(p.status()).append("|");
        buf.append(p.getHand().getAllCardsString()).append("|");
        // if he is a game player denise|22753|292.00|28.00|0|2068|4049560754407434296|127.0.0.1|130818
        buf.append(gp.handler()._id).append("|");
        buf.append(gp.handler().inetAddress().getHostAddress()).append("|");
        buf.append(gp.getPreferences());
        found = true;
        break;
      }
    }
    if (!found) {
      return "player=";
    }
    return buf.toString();
  }

  private static int playerCount() {
    Game[] g = Game.listAll();
    int total_player = 0;
    for (int i = 0; i < g.length; i++) {
      if (g[i] instanceof Poker) {
        _cat.finest(g[i].toString());
        total_player += g[i].allPlayers( -1).length;
      }
    }
    return total_player;
  }

  private static String refreshBankroll(String name) {
    GamePlayer p = GamePlayer.getPlayer(name);
    if (p == null) {
      return "false";
    }
    return Boolean.toString(p.refreshBankroll());
  }

  private static int playerCount(String gid) {
    return Game.game(gid).allPlayers( -1).length;
  }

  private static int activePlayerCount() {
    Game[] g = Game.listAll();
    int total_player = 0;
    for (int i = 0; i < g.length; i++) {
      Presence v[] = g[i].activePlayers();
      if (v != null){
        total_player += g[i].activePlayers().length;
      }
    }
    return total_player;
  }

  private static int activeRPlayerCount() {
    Game[] g = Game.listAll();
    int total_player = 0;
    for (int i = 0; i < g.length; i++) {
      PokerPresence v[] = (PokerPresence[])g[i].activePlayers();
      for (int j = 0; j < v.length; j++) {
        if (!v[j].isShill()) {
          total_player++;
        }
      }
    }
    return total_player;
  }

  private static int activePlayerCount(String gid) {
    Game g = Game.game(gid);
    return g.activePlayers().length;
  }

  private static int activeRPlayerCount(String gid) {
    int total_player = 0;
    PokerPresence v[] = (PokerPresence[])Game.game(gid).activePlayers();
    for (int j = 0; j < v.length; j++) {
      if (!v[j].isShill()) {
        total_player++;
      }
    }
    return total_player;
  }
  
  private static String sngDetail() {
	    Game[] g = Game.listAll();
	    StringBuilder sb = new StringBuilder("game-count=").append(g.length).append("&");
	    for (int i = 0; i < g.length; i++) {
	    	if (g[i] instanceof HoldemSitnGo) {  
				HoldemSitnGo sg = (HoldemSitnGo)g[i];
				
				sb.append("game").append(i).append("=");
				sb.append(sg.name()).append("|");
				String status="INITIALIZING";
				if (sg.state() == sg.TABLE_OPEN){
				  status = "REGISTERING";
				}
				else if (sg.state() == sg.HAND_RUNNING){
				  status = "RUNNING";
				}
				else if (sg.state() == sg.TABLE_CLOSED){
					status = "CLOSED";
				}
				  
				  sb.append(status).append("|");
				  _cat.finest(sb.toString());
				sb.append("Sitngo|").append(sg.type()).append("|");
				sb.append(sg.maxPlayers()).append("|");
				sb.append(sg.getPlayerCount()).append("|");
				sb.append(sg.buyIn()).append("|");
				sb.append(sg.fees()).append("|");
				sb.append(sg.chips()).append("|");
				sb.append(sg.limit()).append("|");
				sb.append(sg.level()).append("|");
				sb.append(sg.don()).append("|");
				sb.append(sg.tourbo());
				sb.append("&");
		    }
	    	else if (g[i] instanceof OmahaSitnGo){
	    		OmahaSitnGo sg = (OmahaSitnGo)g[i];
			
				sb.append("game").append(i).append("=");
				sb.append(sg.name()).append("|");
				String status="INITIALIZING";
				if (sg.state() == sg.TABLE_OPEN){
				  status = "REGISTERING";
				}
				else if (sg.state() == sg.HAND_RUNNING){
				  status = "RUNNING";
				}
				else if (sg.state() == sg.TABLE_CLOSED){
					status = "CLOSED";
				}
				  
				sb.append(status).append("|");
				_cat.finest(sb.toString());
				sb.append("Sitngo|").append(sg.type()).append("|");
				sb.append(sg.maxPlayers()).append("|");
				sb.append(sg.getPlayerCount()).append("|");
				sb.append(sg.buyIn()).append("|");
				sb.append(sg.fees()).append("|");
				sb.append(sg.chips()).append("|");
				sb.append(sg.limit()).append("|");
				sb.append(sg.level()).append("|");
				sb.append(sg.don()).append("|");
				sb.append(sg.tourbo());
				sb.append("&");
	    	}
	    }
	    return sb.toString();
	  }


  

  /**********************************GAME**************************************/
  //Arjun|UP|Poker|Play-Holdem|3|-1.0|0.5|0.15000000000000002|3.35|0|1|4.9399999999999995|27
  private static String gameDetail() {
    Game[] g = Game.listAll();
    StringBuilder sb = new StringBuilder("game-count=").append(g.length).append(
        "&");
    for (int i = 0; i < g.length; i++) {
      sb.append("game").append(i).append("=");
      sb.append(g[i].name()).append("|");
      String status="WAITING";
      if (g[i].isSuspended()){
    	  status = "SUSPENDED";
      }
      else if (g[i].isRunning()){
    	  status = "RUNNING";
      }
      else {
    	  status = "WAITING";
      }
      
      sb.append(status).append("|");
      _cat.finest(sb.toString());
      if (g[i] instanceof Poker) {
        Poker gp = (Poker) g[i];
        sb.append("Poker|").append(gp.type()).append("|");
        sb.append(gp.getPlayerCount()).append("|");
        sb.append(gp.maxBet()).append("|");
        sb.append(gp.minBet()).append("|");
        sb.append(Utils.getRoundedDollarCent(gp.rake())).append("|");
        sb.append(gp.currentPotValueString()).append("|");
        sb.append(gp.getWaiters().length).append("|");
        sb.append(gp.flopPlayers()).append("|");
        sb.append(Utils.getRoundedDollarCent(gp.averagePot())).append("|");
        sb.append(gp.numHandsPerHour());
        sb.append("&");
      }
      else {
        sb.append("&");
      }
    }
    return sb.toString();
  }

  private static String gameDetail(String gid) {
    StringBuilder sb = new StringBuilder();
    Game g = Game.game(gid);
    if (g == null) {
      return sb.toString();
    }
    sb.append("game").append("=");
    sb.append(g.name()).append("|");
    String status="WAITING";
    if (g.isSuspended()){
  	  status = "SUSPENDED";
    }
    else if (g.isRunning()){
  	  status = "RUNNING";
    }
    else {
  	  status = "WAITING";
    }
    
    sb.append(status).append("|");

    if (g instanceof Poker) {
      Poker gp = (Poker) g;
      sb.append("Poker|").append(gp.type()).append("|");
      sb.append(gp.getPlayerCount()).append("|");
      sb.append(gp.maxBet()).append("|");
      sb.append(gp.minBet()).append("|");
      sb.append(Utils.getRoundedDollarCent(gp.rake())).append("|");
      sb.append(gp.currentPotValueString()).append("|");
      sb.append(gp.getWaiters().length).append("|");
      sb.append(gp.flopPlayers()).append("|");
      sb.append(Utils.getRoundedDollarCent(gp.averagePot())).append("|");
      sb.append(gp.numHandsPerHour());
    }
    _cat.finest(sb.toString());
    return sb.toString();
  }

  private static void removePlayer(String gid, String dispName) {
    Game game = Game.game(gid);
    PokerPresence players[] =(PokerPresence[]) game.allPlayers( -1);
    for (int i = 0; i < players.length; i++) {
      if (players[i].name().equals(dispName)) {
        ( (GamePlayer) players[i].player()).leaveGameOnly(players[i], false);
      }
    }
  }

  private static String disablePlayerChat(String name) {
    GamePlayer p = GamePlayer.getPlayer(name);
    _cat.finest(p.toString());
    DBPlayerUtil.modifyPreferences(name, PlayerPreferences.DISABLE_CHAT, true);
    if (p != null && p.handler() != null) {
      int i = p.disableChat();
      ResponseInt gr = new ResponseInt(1, Response.R_PREFERENCES, i);
      p.handler().putResponse(gr);
    }
    return "success";
  }

  private static String enablePlayerChat(String name) {
    GamePlayer p = GamePlayer.getPlayer(name);
    _cat.finest(p.toString());
    DBPlayerUtil.modifyPreferences(name, PlayerPreferences.DISABLE_CHAT, false);
    if (p != null && p.handler() != null) {
      int i = p.enableChat();
      ResponseInt gr = new ResponseInt(1, Response.R_PREFERENCES, i);
      p.handler().putResponse(gr);
    }
    return "success";
  }

  private static String ban(String name) {
    GamePlayer p = GamePlayer.getPlayer(name);
    _cat.finest(p.toString());
    DBPlayerUtil.modifyPreferences(name, PlayerPreferences.BANNED_PLAYER, true);
    if (p != null && p.handler() != null) {
      p.ban();
    }
    return "success";
  }

  private static String unban(String name) {
    GamePlayer p = GamePlayer.getPlayer(name);
    _cat.finest(p.toString());
    DBPlayerUtil.modifyPreferences(name, PlayerPreferences.BANNED_PLAYER, false);
    if (p != null && p.handler() != null) {
      p.unban();
    }
    return "success";
  }

}
