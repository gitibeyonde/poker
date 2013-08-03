package com.poker.client;

import com.poker.game.PokerGameType;

import java.util.Enumeration;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class is used to monitor/manage the players
 * @version 1.0
 */
public class BotMonitor
    extends TimerTask implements Runnable {
  transient static Logger log = Logger.getLogger(BotMonitor.class.getName());
  private static Object _dummy = new Object();
  static int _dead_players = 0;
  static final long THIRTY_MINS = 3000000;
  long cur_time = 0;

  public BotMonitor() {
    log.finest("Bot monitor constructor");
  }

  public void run() {
    long lastDummyTableListTS = System.currentTimeMillis();
    log.finest("Running the bot monitor");
    try {
      // try to bring back disconnected players
      Enumeration enumer1 = ShillServer.__standby.elements();
      for (int j = _dead_players; j > 0 && enumer1.hasMoreElements(); j--) {
        Player p = (Player) enumer1.nextElement();
        _dead_players--;
        log.warning(ShillServer.__standby.size() +
                 " Trying to connect a dead player replacement " + p);
                ShillServer.__standby.remove(p);
                ShillServer.__players.put(p._name, p);
        p.connect();
      }

      Enumeration enumer = ShillServer.__players.elements();
      for (; enumer.hasMoreElements(); ) {
        Player p = (Player) enumer.nextElement();
        process(p);
        cur_time = System.currentTimeMillis();
        if (cur_time - p._last_move_time > THIRTY_MINS &&
            p._currState ==ShillConstants.PLR_JOINED) {
          log.info(p._name + " as last move time is more than 10 mins " + p);
          p._currState = ShillConstants.PLR_IDLE;
          p._currSitoutCount = -10;
          p._last_move_time = cur_time;
          p.leave();
        }

        //ensure _dummy is not dead
        if (ShillServer.__shillServer._dummy._dead) {
                    ShillServer.__shillServer._dummy._channel = null;
                    ShillServer.__shillServer._dummy.connect();
          lastDummyTableListTS = System.currentTimeMillis();
        }
        else if ( (System.currentTimeMillis() - lastDummyTableListTS) >
                 60000) {
          lastDummyTableListTS = System.currentTimeMillis();
                    ShillServer.__shillServer._dummy.getTableList(ShillServer.__shillServer._dummy._pgTypes);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void process(Player _p) {
    try {
      int player_state;
      synchronized (_dummy) {
        player_state = _p._currState;
        if (player_state == ShillConstants.PLR_DISCONNECTED) {
          log.finest("Found a disconnected bot " + _p);
          //do connect
          Thread.currentThread().sleep(1000);
          _p.connect();
        }
        else if (player_state == ShillConstants.PLR_OBSERVING) {
          _p.join(_p._tmpTid, _p._tmpJoinPos, _p._minBet);
          _p._tmpJoinPos = -1;
          _p._tmpTid = null;
          _p._tmpTableType = 0;
        }
        else if (player_state == ShillConstants.PLR_JOINED) {
          //check if enough chips are available
          if (!_p._moneyToTable) {
            if (PokerGameType.isReal(_p._joinedGameType) &&
                (_p._total_real_chips < (6 * _p._minBet))) {
              new Exception("Not enough Chips " + _p.toString()).printStackTrace();
              //_p.buyChips(0, (60+(Math.random() * 40)) * _p._minBet);
            }
            else if (PokerGameType.isPlay(_p._joinedGameType) &&
                     (_p._total_play_chips < (6 * _p._minBet))) {
              new Exception("Not enough Chips " + _p.toString()).printStackTrace();
              _p.getMoneyIntoGame(_p._minBet);////__p.buyChips((60+(Math.random() * 40)) * _p._minBet, 0);
            }
          }
        }
        else if (player_state == ShillConstants.PLR_IDLE) {
          log.finest("Found a idle player " + _p + ", currentsitoutcount=" + _p._currSitoutCount + ", _sitoutCount="+ _p._sitoutCount);
          _p._currSitoutCount++;
          _p._currGamesPlayed = 0;
          if (_p._currSitoutCount > _p._sitoutCount ||
              cur_time - _p._leave_time > THIRTY_MINS) { //tmp: sit out for 3 games
            _p._currState = ShillConstants.PLR_JOINED;
            _p._currSitoutCount = 0;
            log.finest("Trying to seat a sitting out playerr " + _p);ShillServer.getJoinParameters(_p);
          }
        }
        else if (player_state == ShillConstants.PLR_SITOUT) {
          if (_p._tableChips < 6 * _p._minBet) {
           if ( !_p._moneyToTable){
                log.info(" Found a sitting out player getting money " + _p);
                _p.getMoneyIntoGame(_p._minBet);
           }
          }
          else {
            log.finest(" Found a sitting out player sitting in" + _p);
            _p.sitIn();
          }
        }
        else if (player_state == ShillConstants.REMOVE_BOT) {
          log.log(Level.WARNING, "Removing bot " + _p);
          _dead_players++;
                    ShillServer.__players.remove(_p._name);
          _p.leave();
          _p.logout();
        }
      }
    }
    catch (Exception e) {
      log.severe(e.toString());
      e.printStackTrace();
    }
  }
}
