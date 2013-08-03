package com.poker.server;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;
import com.agneya.util.Utils;

import com.golconda.db.DBException;
import com.golconda.db.DBTransactionScratchPad;
import com.golconda.db.GameRunSequence;
import com.golconda.db.LoginSession;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;

import com.poker.common.db.DBSitnGoGameLog;
import com.poker.common.db.DBSitnGoWinner;
import com.poker.common.db.DBTournyGameLog;
import com.poker.common.db.DBTournyWinner;
import com.poker.common.db.GameRunSession;
import com.poker.common.interfaces.MTTInterface;
import com.poker.common.interfaces.SitnGoInterface;
import com.poker.common.interfaces.TournyInterface;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;
import com.poker.game.poker.Tourny;

import java.io.Serializable;

import java.sql.Statement;

import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LogObserver implements Observer, Serializable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(LogObserver.class.getName());

  private static Object _dummy = new Object();
  private static LogObserver _lob = null;
  private static GameRunSequence _grs = null;
  private static Configuration _conf;
  private static boolean _logPlayGames = false;

  public static LogObserver instance() {
    if (_lob == null) {
      synchronized (_dummy) {
        if (_lob == null) {
          _lob = new LogObserver();
        }
      }
    }
    return _lob;
  }

  private LogObserver() {
    try {
      _grs = new GameRunSequence();
      _conf = Configuration.instance();
      _logPlayGames = _conf.getBoolean("Auditor.Log.PlayGame");
    }
    catch (ConfigurationException e) {
      _cat.log(Level.WARNING, "Configuration exception ", e);
    }
    catch (DBException e) {
      _cat.log(Level.WARNING, "DB exception ", e);
    }

  }

  /**
   * the update is called when a new game run
   * at the beginning of a new game run update the game id for all the clients
   * associated with the new game run
   *
   * @param o Observable
   * @param arg Object
   */
  public synchronized void update(Observable o, Object arg) {
    try {
      if (arg == GameStateEvent.GAME_BEGIN) {
        Game g = (Game) o;
        if (g instanceof Poker) {
          com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;
          // setup the game run Id
          int seq = _grs.getNextGameRunId();
          g.grid(seq);
          _cat.finest("Sequence = " + seq);
          g.startTime(Calendar.getInstance());
        }
      }
      else if (arg == GameStateEvent.SITNGO_START) {
        if (o instanceof SitnGoInterface) {
          SitnGoInterface sg = (SitnGoInterface) o;
          PokerPresence v[] = sg.getPlayerList();
          //process winners
          for (int i = 0; i < v.length; i++) {
        	_cat.info("Registering " + v[i].name());
            DBSitnGoWinner.register(v[i].name(), sg);
          }
        }
      }
      else if (arg == GameStateEvent.FLOP) { 
	     updateScratch((Game)o);
      }
      else if (arg == GameStateEvent.TURN) {
 	     updateScratch((Game)o);
      }
      else if (arg == GameStateEvent.RIVER) {
 	     updateScratch((Game)o);
      }
      else if (arg == GameStateEvent.GAME_OVER) {
        Game g = (Game) o;
        updateScratch(g);
        if (g instanceof Poker && !(g instanceof MTTInterface) &&
            !(g instanceof SitnGoInterface)) {
          com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;
          if ((pg.type().isReal() || _logPlayGames) && pg.abOver()) {
           // PokerPresence[] players = pg.getCurrentAndLeftPlayerList();
        	  PokerPresence[] players = pg.activePlayersForRake();
            int pcount = players.length;
            if (pcount == 0) {
              return;
            }
            GameRunSession grs = new GameRunSession(g.name(), g.grid(), g.type().intVal());
            grs.setEndTime(new Timestamp(System.currentTimeMillis()));
            grs.setStartTime(new Timestamp(g.startTime().getTimeInMillis()));
            //************STARTING BATCH
             Statement grs_stat = grs.startBatch();
            //TODO Rake rounding
            double rake[] = Utils.integralDivide3Precs(pg.rake(), pcount);
            for (int i = 0; i < pcount; i++) {
              //if (players[i].isShill()) continue; // donot log bots hand
              if (players[i].player() instanceof GamePlayer) {
                GamePlayer gp = (GamePlayer) players[i].player();
                _cat.finest("GEW=" + players[i].getGameEndWorth() + ", GSW=" +
                           players[i].getGameStartWorth() + ", GP=" + gp);
                double winAmt = Utils.getRounded(players[i].getGameEndWorth() -
                                                 players[i].getGameStartWorth() -
                                                 players[i].getGameAddedChips());
                if (pg.type().isReal() && players[i].player().getDBPlayer() != null) { //if real game set violation
                  //players[i].player().addWin(winAmt);
                  int lv = players[i].player().getLimitViolation();
                  players[i].player().winLossViolated(lv);
                  if (lv != 0) {
                    players[i].setWinLossViolated();
                    _cat.finest("Setting win loss violated for " + players[i]);
                  }
                  //gp.getDBPlayer().updateRealChips();
                  _cat.finest("Saving GRS = " + players[i]);
                }
                grs.setDisplayName(gp.name());
                grs.setPosition(players[i].pos());
                grs.setPot(pg.totalPot());
                grs.setStartWorth(players[i].getGameStartWorth());
                grs.setEndWorth(players[i].getGameEndWorth());
                grs.setWinAmount(winAmt);
                grs.setSessionId(gp.session());
                grs.setRake(rake[i]);
                // ADDING BACTH 
                grs.save(grs_stat);
                  
                /**
                 * Modify login session
                 * The game might be over because this player has left, in that
                 * case do not update his login session as it is already updated
                 */
                // Login session should not be updated here as player maybe playing
                // several games

                if (!gp.isDead()) {
                  LoginSession ls = gp.loginSession();
                  if (ls != null) { // this might have been set null while kill in gameplayer
                    ls.setGames(ls.getGames() + 1);
                    //ls.setEndWorth(gp.realWorth()); this end worth is wrong as player maybe playing on other tables
                    if (winAmt > 0) {
                      ls.setGamesWon(ls.getGamesWon() + 1);
                      ls.setWinAmount(ls.getWinAmount() + winAmt);
                      ls.setWagered(ls.getWagered() + pg.totalPot() + rake[i] - winAmt);
                      _cat.finest(gp.name() + " Setting win amt = " + winAmt);
                    }
                    else {
                      ls.setWagered(ls.getWagered() - winAmt);
                    }
                    //ls.updateAddBatch(grs_stat); do not update here but update when he leaves the table
                  }
                }

              }
            } // log for each player
            // COMMITTING BATCH
            grs.commitBatch(grs_stat);
          } // log only if the game is real
        }
        else if (g instanceof SitnGoInterface) {
          com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;

          PokerPresence[] players = pg.getCurrentAndLeftPlayerList();
          int pcount = players.length;

          DBSitnGoGameLog tgl = new DBSitnGoGameLog(g.tournyId(), g.name(), g.grid(), g.type().intVal());
          tgl.setEndTime(new Date());
          tgl.setStartTime(g.startTime().getTime());
          //************STARTING BATCH
           Statement grs_stat = tgl.startBatch();
           for (int i = 0; i < pcount; i++) {
            if (players[i].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) players[i].player();
              _cat.finest("TGEW=" + players[i].getGameEndWorth() + ", TGSW=" +  players[i].getGameStartWorth() + ", TGP=" + gp);
              double winAmt = Utils.getRounded(players[i].getGameEndWorth() -
                                               players[i].getGameStartWorth());

              tgl.setDisplayName(gp.name());
              tgl.setPosition(players[i].pos());
              tgl.setPot(pg.totalPot());
              tgl.setStartWorth(players[i].getGameStartWorth());
              tgl.setEndWorth(players[i].getGameEndWorth());
              tgl.setWinAmount(winAmt);
              tgl.setSessionId(gp.session());
              tgl.setRake(0);
              // ADDING BACTH
              tgl.save(grs_stat);
            }
          } // log for each player
          // COMMITTING BATCH
          tgl.commitBatch(grs_stat);
        }
        else if (g instanceof MTTInterface) {
          com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;

          PokerPresence[] players = pg.getCurrentAndLeftPlayerList();
          int pcount = players.length;

          DBTournyGameLog tgl = new DBTournyGameLog(g.tournyId(), g.name(),
              g.grid(), g.type().intVal());
          tgl.setEndTime(new Date());
          tgl.setStartTime(g.startTime().getTime());
          //************STARTING BATCH
           Statement grs_stat = tgl.startBatch();
           for (int i = 0; i < pcount; i++) {
            if (players[i].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) players[i].player();
              _cat.finest("TGEW=" + players[i].getGameEndWorth() + ", TGSW=" + players[i].getGameStartWorth() + ", TGP=" + gp);
              double winAmt = Utils.getRounded(players[i].getGameEndWorth() -
                                               players[i].getGameStartWorth() -
                                               players[i].getGameAddedChips());

              tgl.setDisplayName(gp.name());
              tgl.setPosition(players[i].pos());
              tgl.setPot(pg.totalPot());
              tgl.setStartWorth(players[i].getGameStartWorth());
              tgl.setEndWorth(players[i].getGameEndWorth());
              tgl.setWinAmount(winAmt);
              tgl.setSessionId(gp.session());
              tgl.setRake(0);
              // ADDING BACTH
              tgl.save(grs_stat);
            }
          } // log for each player
          // COMMITTING BATCH
          tgl.commitBatch(grs_stat);
        }
      }
      else if (arg == GameStateEvent.SITNGO_OVER) {
        Game g = (Game) o;
        if (g instanceof SitnGoInterface) {
          //process winners
          DBSitnGoWinner sitgo = new DBSitnGoWinner((SitnGoInterface) g);
          sitgo.save();
        }
      }
      else if (arg == GameStateEvent.MTT_OVER) {
        Tourny g = (Tourny) o;
        if (g instanceof Tourny) {
          //process winners
          DBTournyWinner trny = new DBTournyWinner((TournyInterface) g);
          trny.save();
        }
      }


    }
    catch (DBException e) {
        e.printStackTrace();
      _cat.log(Level.WARNING, "Unable to update state in DB", e);
    }
    catch (Exception e) {
        e.printStackTrace();
      _cat.log(Level.WARNING, "Unable to update state", e);
    }

  } // end update
  
  // update scratch
  private void updateScratch(Game g) throws DBException{
	  if (g instanceof Poker && !(g instanceof MTTInterface) && !(g instanceof SitnGoInterface)) {
          com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;
          if ((pg.type().isReal() || _logPlayGames) && pg.abOver()) {
        	  PokerPresence[] players = pg.getCurrentAndLeftPlayerList();
            int pcount = players.length;
            if (pcount == 0) {
                return;
            }
            for (int i = 0; i < pcount; i++) {
                //if (players[i].isShill()) continue; // donot log bots hand
                if (players[i].player() instanceof GamePlayer) {
                  GamePlayer gp = (GamePlayer) players[i].player();
			          // update the scratch pad String usr, int type, String name, int mod, String session, double play, double real
			          if (pg.type().isReal()){
			          	DBTransactionScratchPad.updateScratchTransaction(gp.name(), pg.type().intVal(), players[i].getGameName(), ModuleType.POKER, gp.session(), 0, players[i].getGameEndWorth());
			          }
			          else {
			          	DBTransactionScratchPad.updateScratchTransaction(gp.name(), pg.type().intVal(), players[i].getGameName(), ModuleType.POKER, gp.session(), players[i].getGameEndWorth(), 0);
			          }
                }
            }
          }
      }
  }

} //end LoggingObserver
