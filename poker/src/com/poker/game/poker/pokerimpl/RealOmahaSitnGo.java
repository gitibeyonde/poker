package com.poker.game.poker.pokerimpl;

import com.agneya.util.Base64;
import com.agneya.util.Utils;

import com.golconda.db.DBException;
import com.golconda.db.DBGame;
import com.golconda.db.DBPlayer;
import com.golconda.db.GameSequence;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;
import com.golconda.game.Player;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Cards;
import com.golconda.game.util.MyDeck;

import com.poker.common.interfaces.SitnGoInterface;
import com.poker.common.interfaces.TournyInterface;
import com.poker.common.interfaces.TournyTableInterface;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseMessage;
import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.CollectABResponse;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.GameOverResponse;
import com.poker.game.poker.GameStartResponse;
import com.poker.game.poker.IllegalReqResponse;
import com.poker.game.poker.LeaveResponse;
import com.poker.game.poker.MoveResponse;
import com.poker.game.poker.Pot;
import com.poker.game.poker.SitInResponse;
import com.poker.game.poker.Table;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RealOmahaSitnGo
    extends OmahaSitnGo {
  // set the category for logging
  static Logger _cat = Logger.getLogger(OmahaSitnGo.class.getName());


  public RealOmahaSitnGo(String name, int minPlayers, int maxPlayers,
                     int limit, int tourbo,
                     String[] affiliate,
                     Observer stateObserver) {
    super(name, minPlayers, maxPlayers, limit, tourbo, affiliate, stateObserver);
    _type = new PokerGameType(PokerGameType.Real_OmahaHiSitnGo);
  }

  @Override
  public synchronized Response join(Presence p, double amount) {
    _cat.finest(_inProgress + ", " + _state + "Joining  " + p);
    _inquirer = p;
    if (_inProgress || _state == TABLE_CLOSED) {
      _cat.finest("TABLE_CLOSED");
      return new IllegalReqResponse(this);
    }
    // check if there is a waiting list
    if (_waiters.contains(p)) {
      _cat.warning("Waiter joins " + p);
      _waiters.remove(p);
    }
    if (!isInvited(p.name())) {
      p.lastMove(PokerMoves.NONE);
      return new SitInResponse(this, -7);
    }
    if (!_table.join(p)) { // couldnt be added for whatever reason
      p.lastMove(PokerMoves.NONE);
      return new GameDetailsResponse(this);
    }
    _observers.remove(p);
    p.setNew();
    p.resetPlayerForNewGame();
    p.unsetSitOutNextGame();
    p.unsetSittingOut();
    p.unsetRemoved();
    p.unsetMissedBB();
    p.unsetMissedSB();
    p.unsetBetweenBlinds();
    p.unsetDisconnected();
    p.resetIdleGC();
    p.lastMove(PokerMoves.JOIN);
    p.setPlayer();
    p.setGRID(_grid);
    p.lastMove(PokerMoves.JOIN);
    p.setPlayer();
    p.setSNGPresence(name());
    setCurrent(p);
    _cat.finest("Joined " + p);
    // deduct buyin+fees
    try {
        // deduct buyin+fees
        DBPlayer plr = p.player().getDBPlayer();
        plr.buyRealSnGChips(p.player().session(), _fees + _buyIn, name(), type().toString());
    }
    catch (Exception dbe) {
      _cat.log(Level.WARNING, "SitnGoTourny " + this +" failed to join " + p, dbe);
      p.lastMove(PokerMoves.NONE);
      return new SitInResponse(this, -79);
    }

    p.joinTable(chips(), new ModuleType(ModuleType.POKER));
    p.unsetBroke();

    if (!reRunCondition()) {
      _cat.finest("Not enough player " + _table.eligiblePlayers().length);
      return new SitInResponse(this, _inProgress ? 12 : 9);
    }
    else {
      return start();
    }
  }

  // a player cannot leave a tourny

  @Override
  public synchronized Response leave(Presence p, boolean timeout) {
    p.setDisconnected();
    _cat.finest("Leaving Tourny " + p);
    Response r = new LeaveResponse(this, p.pos());
    if (p.equals(this._nextMovePlayer) && _inProgress == true &&
        p.isResponseReq()) {
      _cat.info("This is the next move player " + p);
      if ( (PokerMoves.CHECK & this._nextMove) > 0) {
        _cat.info("Player checked " + p);
        r = check(p);
      }
      else
      if ( (PokerMoves.FOLD & this._nextMove) > 0) {
        _cat.info("Player folded " + p);
        r = fold(p);
      }
      else if ( (PokerMoves.SMALL_BLIND & this._nextMove) > 0) {
        _cat.info("Player small blind " + p);
        r = postSmallBlind(p, smallBlind);
      }
      else if ( (PokerMoves.BIG_BLIND & this._nextMove) > 0) {
        _cat.info("Player big blind " + p);
        r = postBigBlind(p, bigBlind);
      }
      else if ( (PokerMoves.ALL_IN & this._nextMove) > 0) {
        _cat.info("Player all-in " + p);
        r = allIn(p, bigBlind);
      }
      return r;
    }
    else if (_state == TABLE_OPEN || _state == TABLE_CLOSED) {
    	if (!p.isRemoved()){
	    	 try {
			    DBPlayer plr = p.player().getDBPlayer();
			    plr.returnRealSnGChips(p.player().session(), _fees + _buyIn, name(), type().toString());
			}
			catch (Exception dbe) {
			  _cat.log(Level.WARNING, "SitnGoTourny " + this +" failed to leave " + p, dbe);
			  p.lastMove(PokerMoves.NONE);
			  return new SitInResponse(this, -79);
			}
		      _cat.finest("Removing player ");
		      p.lastMove(PokerMoves.LEAVE);
		      p.unsetResponseReq();
		      _observers.remove(p);
		      _waiters.remove(p);
		      _table.remove(p);
		      remove(p);
		      p.setRemoved();
    	}
    }
    return  new GameDetailsResponse(this);
  }

}
