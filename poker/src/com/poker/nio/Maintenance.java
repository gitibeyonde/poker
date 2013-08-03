package com.poker.nio;

import com.agneya.util.Configuration;

import com.golconda.game.Game;
import com.golconda.game.resp.Response;

import com.poker.common.interfaces.TournyInterface;
import com.poker.common.interfaces.TournyTableInterface;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseMessage;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.InformWaiterResponse;
import com.poker.game.poker.Poker;
import com.poker.game.poker.TournyController;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;

import java.util.Enumeration;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
class Maintenance
    extends TimerTask {
  static Logger _cat = Logger.getLogger(Maintenance.class.getName());
  int _timeout;
  int _rqs;

  /* Construction ************************************************/
  /**
   * Initializes a server maintenace object.
   * @param cm The ConnectManager to be maintained.
   */
  public Maintenance(int to, int qSize) throws Exception {
    _timeout = to;
    _rqs = Configuration.instance().getInt("Server.handler.response.queue.size");
  }

  /* Run() *******************************************************/
  /**
   * Main loop wakes up occasionally maintenanceMethod().
   */
  public void run() {
    testForExpiredClients();
  }

  /**
   * Locates and removes expired clients.
   */
  public final void testForExpiredClients() {
    try {
      _cat.finest("Maintenance thread " + _timeout + " Number of handlers = " +
                 Handler._registry.size());
      Enumeration e = Handler._registry.elements();
      while (e.hasMoreElements()) {
        boolean kill = false;
        Handler c = (Handler) e.nextElement();
        Client pc = c.attachment();
        GamePlayer gp = null;
        if (pc instanceof GamePlayer) {
          gp = (GamePlayer) pc;
        }

        long timeSinceRead = System.currentTimeMillis() - c._last_read_time;
        long timeSinceWrite = System.currentTimeMillis() - c._last_write_time;
        /*_cat.finest(c._id + " RI = " + timeSinceRead + " WI = " +
                   timeSinceWrite +
                   " CT = " + System.currentTimeMillis());*/
        if (c._dead) { // Forget about the clint...
          //_cat.warning("Client " + c + " killed");
          c.kill();
          c.setDisconnected();
          continue;
        } // if state died // Forget about the clint...

        if (timeSinceRead > 4 * _timeout) {
          // Forget about the client...
          _cat.info("Client " + c + " expired... read=" +
                    (timeSinceRead / 1000));
          // Remove connection and signal the player to terminate and
          // destroy the session
        }
        else if (timeSinceRead > 6 * _timeout) {
          _cat.warning("Client " + c + " overdue...read=" + (timeSinceRead / 1000));
          kill = true;
          //c.setDisconnected();
        }

        if (timeSinceWrite > 180 * _timeout) {
          // Forget about the client...
         // _cat.warning("Client " + c + " expired... write=" +
         //           (timeSinceRead / 1000));
          try {
            GameProcessor.broadcastPlayer(gp.session(),
                                    com.agneya.util.Base64.encodeString(
                                        "Connection is being closed because of long inactivity"));
          }
          catch (Throwable t) {}

          // Remove connection and signal the player to terminate and
          // destroy the session
          kill = true;
        }
        else if (timeSinceWrite > 90 * _timeout) {
          _cat.info("Client " + c + " overdue... write=" +   (timeSinceRead / 1000));
        }

        /**if (c.comQSize() == _rqs) {
          _cat.info("Client " + c + " command queue ready to burst");
        }
        else if (c.comQSize() > _rqs) {
          _cat.warning("Client " + c + " command queue burst " + c);
          //_cat.warning(c._com);
          //c.setDisconnected();
          kill = true;
        }**/ //TODO


        if (c.isDisconnected()) {
          _cat.finest("The handler is disconnected " + c._id);
          kill = true;
        }

        boolean gridOver = true; // Whether GP has a tourny presence
        if (gp != null && kill) {
          // check if there is a presence on tourny
          for (Enumeration enumt = gp.getPresenceList(); enumt.hasMoreElements(); ) {
            PokerPresence p = (PokerPresence)enumt.nextElement();
            if (p.isTournyPresence()) {
              _cat.finest("Tourny presence " + p);
              String tid = p._tid;
              TournyInterface trny = null;
              if (p.isMTTPresence()) {
                trny = TournyController.instance().getTourny(tid);
              }
              else if (p.isSNGPresence()) {
                trny = (TournyInterface) Game.game(tid);
              }
              else {
                throw new IllegalStateException("Invalid tourny id");
              }
              if (trny != null && !trny.tournyOver()) {
                if (!c.isDisconnected()) {
                  c.setDisconnected();
                  _cat.info(p + "has presence on tourny ");
                }
              }
              else {
                _cat.info("Marking tourny client as dead as tourny is over " + p);
                p.player().removePresence(p);
              }
            }
            else { // not present on tourny
              Game g = Game.game(p.getGameName());
              if (g != null && !g.isGRIDOver(p.getGRID())) {
                gridOver = false;
              }
              if (!gridOver && p.isResponseReq()) {
                gridOver = false;
                if (p.getGameName() != null) {
                  _cat.info(p + "  playing on   " + p.getGameName());
                  long move = g._nextMove;
                  double[][] amt = g._nextMoveAmt;
                  if ( (move & PokerMoves.JOIN) > 0 && g instanceof Poker) {
                    _cat.info("the waiter did not respond to the join move" + p);
                    Poker pg = (Poker) g;
                    p.unsetRaiseReq();
                    pg.waiterRemove(p);
                  }
                  else if (move > 0) {
                    _cat.info(" Move = " + new PokerMoves(move).stringValue());
                    Response r = gp.leaveGameOnly(p, true);
                    GameProcessor.deliverResponse(r);
                    //_cat.info(p + "---" + r);
                    //gp.deliver(new ResponseGameEvent(1, r.getBroadcast()));
                    _cat.info("Killed " + p);
                    p.player().removePresence(p);
                  }
                }
                else {
                  _cat.warning("Invalid game id " + p);
                }
              }
              if (gridOver) {
                _cat.info(" Marking client as dead " + p);
                gp.kill(p, true);
              }
              else {
                _cat.info("The game is still running " + p);
              }
            } // END NOT TOURNY PRESENCE
          }
        }

        /**
         if (kill && !tournyPresence && gridOver) { // clear up the game player as no TP
                  if (gp != null) {
         com.golconda.message.Response r = new com.golconda.
                        message.Response(com.golconda.message.Response.
                                         E_DISCONNECTED,
                                         com.golconda.message.Response.
                                         R_LOGOUT);
                    _cat.info("Sending disconnect & Killing " + gp);
                    gp.deliver(r);
                    gp.kill();
                  }
                }**/

        //check if the attached gameplayer is active
        if (gp == null) {
          _cat.info("GP == null killing=" + c);
          c.kill();
          continue;
        }
        else if (kill && gp.presenceCount() <= 0) { // check if there is any presence
          _cat.info("GP no presence =" + gp);
          gp.kill();
          c.kill();
          continue;
        }

        // check if presence is making moves on time
        for (Enumeration enumt = gp.getPresenceList(); enumt.hasMoreElements(); ) {
          PokerPresence t = (PokerPresence)enumt.nextElement();
          if (t.isObserver()) {
            continue;
          }
          Game g = Game.game(t.getGameName());
          if (! (g instanceof Poker)) {
            continue;
          }
          Poker pg = (Poker) g;
          if (g == null && !t.isTournyPresence()) {
            _cat.log(Level.WARNING, "Removing presence as game is null " + t);
            gp.removePresence(t);
            continue;
          }
          if (t.isIdleGCViolated() && !t.isTournyPresence() && !t.isShill() &&
              pg.allPlayers(0).length == pg.maxPlayers()) {
            _cat.warning("Violated idle game count " + t);
            gp.deliver(new ResponseMessage(1, gp.name() + "You are removed from the table because you have been sitting idle for more than 10 hands"));
            gp.kill(t, false);
            continue;
          }
          if (t.isResponseReq()) {
            if ( (System.currentTimeMillis() - t._start_wait) >  2 * _timeout) {
              _cat.info("Time exceeded for move " + gp);
              if (t.getGameName() != null) {
                _cat.finest(gp + "  playing on   " + t.getGameName());
                long move = g._nextMove;
                double[][] amt = g._nextMoveAmt;
                if (move > 0) {
                  _cat.info(" Move = " + new PokerMoves(move).stringValue());
                  if (g.type().isTourny()) {
                    gp.handler().makeMove(t.getGameName(), g.grid(), move, amt);
                  }
                }
              }
              else {
                _cat.log(Level.WARNING, "Removing presence as game id is invalid " + t);
                gp.removePresence(t);
              }
              if (!g.type().isTourny()) {
                Response r = gp.leaveGameOnly(t, true);
                GameProcessor.deliverResponse(r);
                _cat.info(gp + "---" + r.getBroadcast());
                gp.deliver(new ResponseGameEvent(1, r.getBroadcast()));
                //_cat.log(Level.WARNING, "Killed " + gp);
                //gp.kill(t);
              }
            } // response required exceeded timeout
          } // response required
        }

        /**Enumeration pl = gp.getPresenceList();
        while (pl.hasMoreElements()) {
          _cat.finest(pl.nextElement());
        }**/
      } //END MAIN WHILE LOOP

      /** DEBUG
             Enumeration pl = GamePlayer.getGPList();
             while (pl.hasMoreElements()) {
        _cat.finest(pl.nextElement());
             }
       **/
      // Game maintenance

      Game g[] = Game.listAll();
      for (int i = 0; i < g.length; i++) { // LOOP THRU ALL GAMES
        if (g[i] instanceof Poker) {
          Poker pg = (Poker) g[i];
          PokerPresence[] v = pg.allPlayers( -1);

          if (g[i].type().isTourny()) {
            TournyTableInterface tti = (TournyTableInterface) g[i];
            if (tti.tournyWaiting()) {
              Response r = new GameDetailsResponse(pg, false);
              _cat.finest("Sending game details response " + r.getBroadcast());
              GameProcessor.deliverResponse(r);
            }
          }
          else { // remove players with null handlers if not tourny players
            for (int j = 0; j < v.length; j++) {
              _cat.finest("PokerPresence = " + v[j]);
              if (v[j].player() instanceof GamePlayer) {
                GamePlayer gp = (GamePlayer) v[j].player();
                if (gp.handler() == null || gp.handler().isKilled()) {
                  v[j].setDisconnected();
                  _cat.log(Level.WARNING, "Found player with null handler " + gp);
                  gp.kill(v[j], false);
                }
              }
            }
          }

          PokerPresence[] vw = pg.getWaiters();
          int response_awaited = 0;
          for (int j = 0; j < vw.length; j++) {
            _cat.finest("Waiter PokerPresence = " + vw[j]);
            if (vw[j].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) vw[j].player();
              if (vw[j].isResponseReq()) {
                _cat.finest("Waiting for join response from " + vw[j]);
                response_awaited++;
              }
              if (gp.handler() == null || gp.handler().isKilled()) {
                //_cat.log(Level.WARNING, "Found waiter with null handler " + gp);
                pg.waiterRemove(vw[j]);
                gp.kill(vw[j], false);
              }
            }
          }

          if (pg.allPlayers( -1).length + response_awaited < pg.maxPlayers() && pg.haveWaiters()) {
            // send the join to next waiter
            PokerPresence waiter = pg.waiterLookup();
            if (waiter != null){
              Response r = new InformWaiterResponse(waiter, pg,
                  pg.getNextVacantPosition());
              GamePlayer gp = (GamePlayer) waiter.player();
              gp.deliver(new ResponseGameEvent(1, r.getCommand(waiter)));
              waiter.setResponseReq();
              _cat.info(waiter.name() + " Delivering " + r.getCommand(waiter));
            }
          }

          PokerPresence[] vo = pg.getObservers();
          for (int j = 0; j < vo.length; j++) {
            _cat.finest("Observer PokerPresence = " + vo[j]);
            if (vo[j].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) vo[j].player();
              if (gp.handler() == null || gp.handler().isKilled()) {
                //_cat.log(Level.WARNING, "Found observer with null handler " + gp);
                pg.waiterRemove(vo[j]);
                gp.kill(vo[j], false);
              }
            }
          }

          if (g[i].lastMoveDelta() > 3 * _timeout) {
            // refresh table
            Response r = new GameDetailsResponse(pg, false);
            _cat.finest("Sending game details response " + r.getBroadcast());
            GameProcessor.deliverResponse(r);
            pg.setTimeDelta();
          }
          else if (g[i].lastMoveDelta() > 5 * _timeout) {
            PokerPresence[] pv = pg.allPlayers( -1);
            if (pv.length > pg.minPlayers()) {
              _cat.log(Level.WARNING, "Game stuck " + g[i]);
              // try to restart
              pg.start();
              for (int k = 0; k < pv.length; k++) {
                _cat.warning("Players stuck=" + pv[k]);
              }
            }
            else if (pv.length < pg.minPlayers() && pv.length > 0) {
              // refresh table
              Response r = new GameDetailsResponse(pg, false);
              _cat.finest("Sending game details response " + r.getBroadcast());
              GameProcessor.deliverResponse(r);
              pg.setTimeDelta();
            }
          }
          else if (g[i].lastMoveDelta() > 10 * _timeout) {
            pg.destroy();
          }

        }
      } // END LOOP ALL GAMES

      /**
       if ( (System.currentTimeMillis() - g[i]._last_move_ts) > 30 * _timeout ) {
        _cat.log(Level.WARNING, "Stopping the game " + g[i].id() +
       " as it was stopped for some time and there are no players");
       } **/

      System.gc();
    }
    catch (Throwable e) {
      // MUST catch Exception, otherwise the main-thread will
      // stop processing incoming requests!!!
      _cat.log(Level.WARNING, "Unexpected Throwable while running maintenance thread ",
                 e);
    }

  } //testforExpiredClients
}
