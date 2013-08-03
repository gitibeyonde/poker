package com.poker.game.poker;

import com.agneya.util.Utils;

import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;
import com.golconda.game.Player;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.golconda.game.util.MyDeck;

import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.util.Hand;
import com.poker.server.GameProcessor;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// This is a generic poker interface


public abstract class Poker extends Game implements Serializable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Poker.class.getName());

  /**
   * Game interface implementation
   */
  public Response details() {
    _inquirer = null;
    return new GameDetailsResponse(this);
  }

  /**
   * Game interface implementation
   */
  public Response details(Presence p) {
    _inquirer = p;
    return new GameDetailsResponse(this);
  }

  /**
   * Game interface implementation
   */
  public Response summary() {
    _inquirer = null;
    return new GameSummaryResponse(this);
  }

  /*
      PokerPresence joins a table. May or may not play a poker
   */
  public synchronized Response join(Presence p, double amount) {
    _cat.finest(name() + "Joining  " + p);
    _inquirer = p;
    LastLeft ll;
    //if ((ll = isLastLeft(p)) != null && !p.isShill()) {
     // _cat.info("The player has left within on hour");
      //check if he brings in more than or equal to the worth he left with
      /**if (ll._chips > p.getAmtAtTable()) {
        //player is not bringin in good amount
        return new SitInResponse(this, -11);
             }**/
    //}
    // check if there is a waiting list
    if (_waiters.contains(p)) {
      _cat.warning("Waiter joins " + p);
      _waiters.remove(p);
    }
    else if (_waiters.size() > 0) { // the waiter is not in list
      // there are waiters, donot let this player join
      return new SitInResponse(this, -9);
    }
    if (!isInvited(p.name())) {
      p.lastMove(PokerMoves.NOT_INVITED);
      return new SitInResponse(this, -7);
    }
    if (!_table.join(p)) { // could not be added for whatever reason
      _cat.finest(name() + "Joining  Failed" + p);
      p.lastMove(PokerMoves.JOIN_FAILED);
      return new SitInResponse(this, -8);
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
    p.joinTable(amount, new ModuleType(ModuleType.POKER));
    p.unsetBroke();
    setCurrent(p);
    if (_players != null && _players.length >= 2 && activePlayers().length >= 2 && !_type.isStud()) {
      if (_table.isBetweenBlinds(p)) {
        //_cat.finest("Sitting next to SB or Dealer  or Bet Blinds " + p);
        p.setBetweenBlinds();
      }
    }
    _cat.finest(name() + " Joined  " + p);
    if (_inProgress || !reRunCondition()) {
      _cat.info(name() + " Joined  " + p.name() + ", " + _inProgress + ", ReRun = " + reRunCondition());
      return new SitInResponse(this, _inProgress ? 12 : 9);
    }
    else {
      return start();
    }
  }

  public void destroy() {
    PokerPresence[] pl = allPlayers( -1);
    for (int i = 0; pl != null && i < pl.length; i++) {
      pl[i].resetRoundBet();
      pl[i].leaveTable( new ModuleType(ModuleType.POKER));
      pl[i].setRemoved();
      pl[i].unsetState();
      Player p = pl[i].player();
      p.removePresence(pl[i]);
    }
    _table.removeAll();
    super.remove(this);
  }

  public Response observe(Presence p) {
	_cat.finest("Observe " + p);
    // check if this presence is already there observe
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      PokerPresence op = (PokerPresence) i.next();
      if (op.name().equals(p.name())) {
        //_cat.finest("Already an observer " + p + "\n Older = " + op);
        _observers.remove(op);
        break;
      }
    }
    _observers.add(p);
    p.setObserver();

    return details(p);
  }

  public Response leaveWatch(Presence p) {
    //_cat.finest("Removing observer " + p);
    _inquirer = p;
    _observers.remove(p);
    p.setRemoved();
    _waiters.remove(p);
    return new PokerResponse(this);
  }

  // to promote a poker observer to player status
  public synchronized Response promoteToPlayer(Presence observer, double amount) {
    if (type().isRegularGame() && (observer.getWorth() < amount)) { /// the ratio should be same as checked in SIT_IN in game processor
      observer.lastMove(PokerMoves.JOIN_FAILED);
      _cat.warning(observer.getWorth() + " , " + amount + " the amount is less than the players worth " + observer);
      return new SitInResponse(this, -10);
    }
    return join(observer, amount);
  }

  /*
   As a result of leaving, the marker does not change. Only current changes
   */
  public synchronized Response leave(Presence p, boolean timeout) {
    _inquirer = p;
    p.lastMove(PokerMoves.LEAVE);
    p.setGameEndWorth();
    p.unsetResponseReq();
    _observers.remove(p);
    _waiters.remove(p);
    flushLastLeft(p);

    Response r;

    if (maxBet() <= 0) {
      _last_left.add(new LastLeft(p, p.getAmtAtTable()));
    }

    if (!validatePresenceInGame(p.name())) {
      _table.remove(p);
      //_cat.finest("Removing player not in game");
      return new LeaveResponse(this, p.pos());
    }

    _cat.finest("Next move player " + this._nextMovePlayer);
    if (p.equals(this._nextMovePlayer) && _inProgress) {
      _cat.finest("This is the next move player " + p);
      // If the left player was supposed to make a move
      if ((PokerMoves.FOLD & this._nextMove) > 0) {
        if (timeout && ((PokerPresence)p).isAllInAvailable()) {
          p.setDisconnected();
          r = allInDisconnect(p, 0); // do a all-in instead of fold
          _cat.info("Player did a ALLIN 0 " + p);
        }
        else {
          r = fold(p);
          _table.remove(p);
          remove(p);
          _cat.finest("Player folded " + p);
        }
      }
      else if ((PokerMoves.OPT_OUT & this._nextMove) > 0) {
        r = optOut(p);
        _table.remove(p);
        remove(p);
        _cat.info("Player optout " + p);
      }
      else if ((PokerMoves.BRINGIN & this._nextMove) > 0) {
        p.setDisconnected();
        r = allInDisconnect(p, Utils.integralDivide(this._minBet, 2)[0]);
        _cat.info("Player bringin " + p);
      }
      else if ((PokerMoves.WAIT & this._nextMove) > 0) {
        _table.remove(p);
        remove(p);
        r = new LeaveResponse(this, p.pos());
      }
      else {
    	_cat.fine("Unrecognized move " + new PokerMoves(_nextMove).stringValue());
        throw new IllegalStateException("Unrecognized move at leave " +  new PokerMoves(_nextMove).stringValue());
        //r = new LeaveResponse(this, p.pos());
      }
      return r;
    }
    if (_inProgress) {
      if (_currentPot != null) {
        //_cat.finest("Removing player form the live game");
        _currentPot.addVal(p.currentRoundBet());
        p.resetRoundBet();
        _currentPot.remove(p);
        p.setRemoved();
      }
      PokerPresence[] v = (PokerPresence [])activePlayersIncludingAllIns();
      if (v == null) {
        return new GameDetailsResponse(this);
      }
      if (v.length < 2) {
        _inProgress = false;
        if (abOver() && ((PokerPresence)p).getHand().cardCount() > 0 && _currentPot != null) {
          _cat.info("Leaving blinds over " + p);
          procRoundOver();
        }
        else {
          // blinds are not over or cards are not distributed
          // return the bets and stop the game
          for (int i = 0; i < v.length; i++) {
            v[i].returnRoundBet();
            v[i].unsetResponseReq();
            _cat.info("Returning bets blind not over " + v[i]);
          }
        }
        r = gameOverResponse(p);
      }
      else {
        r = new LeaveResponse(this, p.pos());
      }
      _table.remove(p);
      remove(p);
      return r;
    } // In active
    else if (type().isRegularGame()) { // game is not in progress
      gameCleanup();
      _table.remove(p);
      remove(p);
      _cat.finest("Removing player game not in progress");
      return new LeaveResponse(this, p.pos());
    }
    else {
      // tourny..
      //_cat.finest("Tourny presence keeping it " + p);
      return new LeaveResponse(this, p.pos());
    }
  }


  public void removeClean(Presence p) {
      Vector<PokerPresence> v = new Vector<PokerPresence>();
      for (int i = 0; _players != null && i < _players.length; i++) {
          if (!_players[i].name().equals(p.name())) {
              v.add(_players[i]);
          }
      }
      if (_currentPot != null) {
          _currentPot.remove(p);
      }
      _table.remove(p);

      _players = (PokerPresence[]) v.toArray(new PokerPresence[v.size()]);
  }

  protected void remove(Presence p) {
    Vector<PokerPresence> v = new Vector<PokerPresence>();
    for (int i = 0; _players != null && i < _players.length; i++) {
      if (!_players[i].name().equals(p.name())) {
        v.add(_players[i]);
      }
      else {
        _left.add(_players[i]);
        _cat.info("Removed " + _players[i]);
      }
    }
    if (_currentPot != null) {
      _currentPot.remove(p);
    }

    _players = (PokerPresence[]) v.toArray(new PokerPresence[v.size()]);
  }

  public PokerPresence[] allPlayers(int startPos) {
    return _table.allPlayers( -1);
  }

  public PokerPresence[] activePlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isActive() && !p.isNew() && !p.isWaitForBlinds();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence[] activePlayers() {
    PokerPresence p = marker() == null ? current() : marker();
    if (p==null) return null;
    return activePlayers(p.pos());
  }

  public PokerPresence[] inActivePlayers(int startPos) {
    return _table.inActivePlayers(startPos);
  }

  public PokerPresence[] newPlayers(int startPos) {
    return _table.newPlayers(startPos);
  }

  public PokerPresence[] foldedPlayers(int startPos) {
    //return _table.foldedPlayers(startPos);
    // There are players who were folded and removed from table
    // so these players will not be there in the table list
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isFolded();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public Response chat(Presence p, String message) {
    if (p == null) {
      return new MessagingResponse(this, message);
    }
    else {
      return new MessagingResponse(this, p, message);
    }
  }

  /**
   * Game interface implementation ENDS
   */


  /***
   * Abstract Methods
   */

  public abstract void prepareForNewRun();

  public abstract Response abOverResponse();

  protected abstract void attachWinners(Pot p);

  public abstract boolean abOver();

  public abstract double abValue(); // returns small-blind or Ante

  public abstract String abString();

  public abstract Response postAnte(Presence p, double amt);

  public abstract Response bringIn(Presence p, double amt);

  public abstract Response postBigBlind(Presence p, double amt);

  public abstract Response postSmallBlind(Presence p, double amt);

  public abstract Response postSBBB(Presence p, double amt);

  public abstract Response optOut(Presence p);

  // END Abstract methods
  public boolean waiterAdd(Presence p) {
    if (!_waiters.contains(p) && !validatePresenceInGame(p.name())) {
      _cat.warning("Waiter added to the table " + p);
      _waiters.add(p);
      p.setWaiter();
      return true;
    }
    else {
      _cat.warning("Waiter already there " + p);
      return false;
    }
  }

  public PokerPresence[] getWaiters() {
    return (PokerPresence[]) _waiters.toArray(new PokerPresence[_waiters.size()]);
  }

  public boolean haveWaiters() {
    return _waiters.size() > 0;
  }

  public PokerPresence[] getObservers() {
    return (PokerPresence[]) _observers.toArray(new PokerPresence[_observers.size()]);
  }

  public synchronized int getNextVacantPosition() {
    if (getPlayerCount() < _maxPlayers) {
      PokerPresence[] v = _table.allPlayers( -1);
      // the player can be accommodated find an empty slot
      boolean pos_occupied;
      int pos;
      for (pos = 0; pos < _maxPlayers; pos++) {
        pos_occupied = false;
        for (int i = 0; i < v.length; i++) {
        	//_cat.info("getNextVacantPosition player = " + v[i]);
            if (pos == v[i].pos()) {
              pos_occupied = true;
              break;
            }
        }
        if (!pos_occupied) {
          // a position has been found which is not occupied
          break;
        }
      }
      return pos > _maxPlayers ? -1 : pos;
    }
    return -1;
  }

  public void waiterRemove(Presence p) {
    _waiters.remove(p);
  }

  public PokerPresence waiterLookup() {
    if (_waiters == null || _waiters.size() == 0) {
      return null;
    }
    try {
      for (int i = 0; i < _waiters.size(); i++) {
        PokerPresence p = (PokerPresence) _waiters.get(i);
        if (p.isResponseReq()) {
          continue;
        }
        else {
          return p;
        }
      }
    }
    catch (NoSuchElementException e) {
      return null;
    }
    return null;
  }

  public Poker(String name, int minPlayers, int maxPlayers, int rake,
               double[] maxRake, String[] affiliate, String[] partner,
               Observer stateObserver) {
    assert stateObserver != null:"State Observer can not be null"; 
    //_cat.finest("Rake%=" + rake + " Max =" + maxRake[0]);
    _name = name;
    _minPlayers = minPlayers;
    _maxPlayers = maxPlayers;
    _rakePercent = rake;
    _maxRake = maxRake;
    stateObserver(stateObserver);
    _table = new Table(_name, maxPlayers, this);
    initAffiliate(affiliate);
    initPartner(partner);
    add(this);
  }

  public Poker(String name, int minPlayers, int maxPlayers, int rake,
               double maxRake, String[] affiliate, String[] partner,
               Observer stateObserver) {
    assert stateObserver != null:"State Observer can not be null"; 
    //_cat.finest("Rake%=" + rake + " Max =" + maxRake);
    _name = name;
    _minPlayers = minPlayers;
    _maxPlayers = maxPlayers;
    _rakePercent = rake;
    _maxRake = new double[maxPlayers];
    for (int i = 0; i < maxPlayers; i++) {
      _maxRake[i] = maxRake;
    }
    stateObserver(stateObserver);
    _table = new Table(_name, maxPlayers, this);
    initAffiliate(affiliate);
    initPartner(partner);
    add(this);
  }

  public boolean reRunCondition() {
    //@todo : each player should have minimum funds. also at time of observerToPresence
    // actives returned by following are only those with minimum bet amount with them
    // as only those had been designated as active at end of a run
    return _keepRunning && _table.eligiblePlayers().length >= _minPlayers;
    // any other validations
  }

  public void gameCleanup() {
    _pots = new ArrayList();
    _currentPot = new Pot("main", _rakePercent, _validMaxRake, this);
    _pots.add(_currentPot);
    _bettingRound = R_PREFLOP;
    _loops = 0;
    _currentRoundBet = 0;
    _inquirer = null;
    _communityCards = new Cards(false);
  }

  public synchronized void setupNewRun() {
    flushLastLeft();
    _table.markEligiblesActive(_minBet);
    _players = _table.eligiblePlayers();
    update(this, GameStateEvent.GAME_SETUP);
    if (_players.length < _minPlayers) {
      PokerPresence[] v = _table.allPlayers( -1);
      for (int i = 0; i < v.length; i++) {
        v[i].unsetBetweenBlinds();
        v[i].unsetWaitForBlinds();
      }
      _table.markEligiblesActive(_minBet);
      _players = _table.eligiblePlayers();
      if (_players.length < _minPlayers) {
        _cat.finest("Game cannot be started");
        _inProgress = false;
        _players = null;
        return;
      }
    }
    _flop[_flopIndex] = -1;
    _deck = new MyDeck(false);
    _bettingRound = R_PREFLOP;
    _uncalledBet = false;
    _uncalledRaise = false;
    _betValue = 0;
    _raiseCount = 0;
    _lastRaise = 0;
    _loops = 0;
    _currentRoundBet = 0;
    _inquirer = null;
    //_cat.warning("mr=" + _maxRake.length + " mp=" + _players.length);
    _validMaxRake = _maxRake.length < _players.length ?
                    _maxRake[_maxRake.length - 1] :
                    _maxRake[_players.length - 1];

    _table.setDealer();
    _cat.finest("Dealer = " + dealer());
    _showdownPos = dealer().pos();

    _table.setAB();
    setMarker(_table.dealer());

    /**
     * Some more players may have become eligible because of moving of
     * dealer button across them and due to assignment of bbblind and smblind
     */
    _players = _table.eligiblePlayers();

    if (_currentPot == null) {
      _pots = new ArrayList();
      _currentPot = new Pot("main", _rakePercent, _validMaxRake, this);
      _pots.add(_currentPot);
    }

    // what about dealer designation ?
    update(this, GameStateEvent.GAME_BEGIN);
    
    for (int i = 0; _players != null && i < _players.length; i++) {
      if (_players[i] == null) {
        _cat.log(Level.WARNING, "Player is null ");
        continue;
      }
      _currentPot.addContender(_players[i], 0); // only potential contender. No more.
      _players[i].gameStart(_grid);
      _cat.finest("Setup new Run Adding contenders " + _players[i]);
    }

    _cat.finest("Setting up new run dealer = " + _table.dealer());
    if (_keepRunning) {
      _inProgress = true;
    }
  }

  public void postRun() {
    //average pot
    _averagePot[_avePotIndex] =  totalPot();
    _avePotIndex = _avePotIndex == 9 ? 0 : _avePotIndex + 1;
    int j = 0;
    double sum = 0;
    for (int i = 0; i < 10; i++) {
      if (_averagePot[i] > 0) {
        sum += _averagePot[i];
        //_cat.info("Pot=" + _averagePot[i]);
        j++;
      }
    }
    _averagePotValue = j == 0 ? 0 : sum / j;
    //flop
    _flopIndex = _flopIndex == 9 ? 0 : _flopIndex + 1;
    j = 0;
    int fsum = 0;
    for (int i = 0; i < 10; i++) {
      if (_flop[i] > 0) {
        fsum += _flop[i];
        j++;
      }
    }
    _flopPlayers = j == 0 ? 0 : fsum / j;
    _table.commitTotalBet(_players);
    _table.postRun(); //@todo :  impl of prepare for new run marks all as active
    update(this, GameStateEvent.GAME_OVER);
    _numOfHands++;
    _cat.finest(name() + "--Game Over AVE-POT=" + _averagePotValue + ", FLOP=" +_flopPlayers );
  }

  public void postWin() {
    _communityCards = new Cards(false);
    _table.postWin();
    _left.clear();
    _pots = new ArrayList();
    _currentPot = new Pot("main", _rakePercent, _validMaxRake, this);
    _pots.add(_currentPot);
    // remove disconnected players from table
    _table.eliminateDisconnectedPlayers();
    for (int i = 0; _players != null && i < _players.length; i++) {
      _currentPot.addContender(_players[i], 0); // only potential contender. No more.
      //_cat.finest("Setup new Run Adding contenders " + _players[i]);
    }
  }

  // @todo : winners on a per pot basis
  public Response gameOverResponse(Presence p) {
    //_cat.finest("Entering game over response " + p);
    declarePotWinners();
    new GameOverResponse(this); /// for loggin winner
    postRun();
    _inProgress = false;
    prepareForNewRun(); // if the game can be started else mark all as new
    update(this, GameStateEvent.GAME_POSTRUN);
    Response r = new GameStartResponse(this);
    postWin();
    return r;
  }

  public Response bet(Presence p, double amt) {
    if (_bettingRound == R_RIVER) {
      showdownPos(p.pos());
    }
    _lastRaise = amt;
    _uncalledBet = true;
    return bettingResponse(p, amt, PokerMoves.BET);
  }

  public Response complete(Presence p, double amt) {
    if (_bettingRound == R_RIVER) {
      showdownPos(p.pos());
    }
    _lastRaise = amt - _call_amount;
    _uncalledBet = true;
    return bettingResponse(p, amt, PokerMoves.COMPLETE);
  }

  public Response call(Presence p, double amt) {
    _uncalledBet = false;
    _uncalledRaise = false;
    return bettingResponse(p, amt, PokerMoves.CALL);
  }

  public Response raise(Presence p, double amt) {
    if (_bettingRound == R_RIVER) {
      showdownPos(p.pos());
    }
    _lastRaise = amt - _call_amount;
    _raiseCount++;
    _uncalledBet = false;
    _uncalledRaise = true;
    if (p.getAmtAtTable() <= amt) {
      return allIn(p, amt);
    }
    else {
      return bettingResponse(p, amt, PokerMoves.RAISE);
    }
  }

  public Response allIn(Presence p, double amt) {
    // amt is the amount of money betted.
    // amt is essentially same as the players worth at this time.
    p.setAllIn();
    p.setShowdown();
    _cat.finest("Allin == " + p);
    return bettingResponse(p, amt, PokerMoves.ALL_IN);
  }

  public Response betPot(Presence p, double amt) {
    if (_bettingRound == R_RIVER) {
      showdownPos(p.pos());
    }
    _lastRaise = amt - _call_amount;
    _raiseCount++;
    _uncalledBet = false;
    _uncalledRaise = true;
    return bettingResponse(p, amt, PokerMoves.BET_POT);
  }

  public Response check(Presence p) {
    return bettingResponse(p, 0, PokerMoves.CHECK);
  }

  abstract public Response quickFold(Presence p);

  public Response fold(Presence p) {
    p.setFolded();
    ((PokerPresence)p).resetHand();
    return bettingResponse(p, 0, PokerMoves.FOLD);
  }


  public Response illegalMove(Presence p, long move) {
    p.lastMove(move);
    _inquirer = p;
    return new IllegalReqResponse(this);
  }

  public Response allInDisconnect(Presence p, double amt) {
    // amt is the amount of money betted.
    // amt is essentially same as the players worth at this time.
    p.setAllIn();
    ((PokerPresence)p).decrAllIn();
    p.setShowdown();
    //_cat.finest("Allin == " + p);
    return bettingResponse(p, amt, PokerMoves.ALL_IN);
  }

  public Response bettingResponse(Presence p, double amt, long mvId) {
    post(p, amt, mvId);
    if (loopOver(p)) {
      ////_cat.finest("Loop over ____________________________________" + _loops);
      ++_loops; 
      if (bettingRoundOver(p)) {
        switch (_bettingRound) { // COMPLETE
          case R_PREFLOP:
            update(this, GameStateEvent.PRE_FLOP);
            break;
          case R_FLOP:
            PokerPresence[] v = activePlayers(0);
            if (_players.length > 0){
              _flop[_flopIndex] = v != null ? v.length :  0;
            }
            update(this, GameStateEvent.FLOP);
            break; // do nothing *flop* cards alread dealt
          case R_TURN:
            update(this, GameStateEvent.TURN);
            break;
          case R_RIVER:
            update(this, GameStateEvent.RIVER);
            break;
          default:
            update(this, GameStateEvent.UNKNOWN);
        }
        //_cat.finest("Betting round Over=" + _bettingRound + ", maxRnds=" +
                   //_maxRounds);
        _isRoundOver = true;
        _resetLastPokerMoves = true;
        _lastRaise = 0;
        ////_cat.finest(">>>>>>>>>Total Rake = " + rake());
        procRoundOver();
        ////_cat.finest("<<<<<<<<Total Rake = " + rake());
        if (gameOver()) {
          return gameOverResponse(p);
        }
        _raiseCount = 0;
        initNextBettingRound();

        switch (_bettingRound) { // START
          case R_PREFLOP:
            break;
          case R_FLOP:
            // non flop round if there is any unraked money
            //make it raked
            _currentPot.moveMoneyToRakedPot();
            break; // do nothing *flop* cards alread dealt
          case R_TURN:
            break;
          case R_RIVER:
            break;
          default:
            update(this, GameStateEvent.UNKNOWN);
        }

      }
    }
    return new MoveResponse(this);
  }

  public void resetLastMove() {
    PokerPresence[] v = allPlayers( -1);
    for (int i = 0; i < v.length; i++) {
      v[i].lastMove(PokerMoves.NONE);
    }
  }

  /**
   * A round is over. Calculate pot/side pot
   */
  public void procRoundOver() {
    PokerPresence[] actives = activePlayers();
    PokerPresence[] allin = allInsNotAdjustedPlayers(marker().pos());
    PokerPresence[] folded = foldedPlayers(marker().pos());

    if (allin == null || allin.length == 0) { //  NO ALL IN PLAYERS

      // Collect the money from folded players
      for (int i = 0; folded != null && i < folded.length; i++) {
        if (_currentPot != null) {
          _cat.finest("Betting round is preflop " + (_bettingRound == R_PREFLOP));
          if (_bettingRound == R_PREFLOP) {
            _currentPot.addUnCalledBet(folded[i].currentRoundBet());
          }
          else {
            _currentPot.addVal(folded[i].currentRoundBet());
          }
          _cat.finest("Folding player " + folded[i] + "\nPot " + _currentPot);
        }
        for (int j = 0; j < _pots.size(); j++) { // removing him from all pots
          ((Pot) _pots.get(j)).remove(folded[i]);
        }
        folded[i].resetRoundBet();
        folded[i].setGameEndWorth();
      } // END Collect money from folded players

      double roundAmt = 0;
      for (int i = 0; actives !=null && i < actives.length; i++) {
        roundAmt += actives[i].currentRoundBet();
        _cat.finest(actives[i] + ", Round bet " + actives[i].currentRoundBet() +" , Total = " + roundAmt);
      }
      // reset the current Round Bet
      _currentRoundBet = 0;
      if (_currentPot != null) {
        if (_bettingRound == R_PREFLOP) {
          _currentPot.addUnCalledBet(roundAmt);
          _cat.finest("Pre flop " + roundAmt);
        }
        else {
          if (_uncalledBet) {
            _currentPot.addUnCalledBet(roundAmt);
            _cat.finest("addUnCalledBet " + roundAmt);
          }
          else if (_uncalledRaise) {
            //called amount to be raked, uncalled amount not
            _cat.finest("uncalledRaise amt " + _call_amount + ", BetValue=" + roundAmt);
            _currentPot.addUnCalledBet(_call_amount);
            _currentPot.addVal(roundAmt - _call_amount);
          }
          else {
            _currentPot.addVal(roundAmt);
          }
        }
        _table.commitTotalBet(_players);
        _cat.finest(_currentPot.toString());
        // Charge the players required amount
      }
      return;
    } // END NO ALL IN PLAYERS
    
    if ((actives == null && actives.length ==0 ) && (allin != null && allin.length == 1)) { //  ONE ALL IN PLAYERS AND REST FOLDED
    	for (int i = 0; i < allin.length; i++) {
    		_cat.finest("Allins game ends=" + allin[i]);
    	}
    	double calledBet=0;
    	 
        // Collect the money from folded players
        for (int i = 0; folded != null && i < folded.length; i++) {
          if (_currentPot != null) {
            _cat.finest("Betting round is preflop " + (_bettingRound == R_PREFLOP));
            if (_bettingRound == R_PREFLOP) {
              _currentPot.addUnCalledBet(folded[i].currentRoundBet());
            }
            else {
              _currentPot.addVal(folded[i].currentRoundBet());
            }
            if (calledBet < folded[i].currentRoundBet()){
            	calledBet = folded[i].currentRoundBet();
            }
            _cat.finest("Folding player " + folded[i] + "Pot " + _currentPot);
          }
          for (int j = 0; j < _pots.size(); j++) { // removing him from all pots
            ((Pot) _pots.get(j)).remove(folded[i]);
          }
          folded[i].resetRoundBet();
          folded[i].setGameEndWorth();
        } // END Collect money from folded players
        
        _cat.finest("Called Bet " + calledBet);

        if (allin.length == 1){
        	 //deduct the called bet from the allin player and send rest to the pot
            _currentPot.addVal(calledBet);
            _currentPot.addUnCalledBet(allin[0].currentRoundBet() - calledBet);
        }
        else {
        	calledBet = allin[0].currentRoundBet();
	        for (int i = 0; i < allin.length; i++) {
	        	if (calledBet >  allin[i].currentRoundBet()){
	        		 calledBet = allin[i].currentRoundBet();
	        	}
	         }
	        // add the calledBet to the pot as raked
	        for (int i = 0; i < allin.length; i++) {
	        	if (calledBet >=  allin[i].currentRoundBet()){
	        		_currentPot.addVal(allin[i].currentRoundBet());
	        	}
	        	else {
	                _currentPot.addVal(calledBet);
	                _currentPot.addUnCalledBet(allin[i].currentRoundBet() - calledBet);
	        	}
	         }
        }
      
        // reset the current Round Bet
        _currentRoundBet = 0;
        
        return;
      } // END ONE ALL IN PLAYERS
    
    
    else {
      // sort the all-in players in ascending order of  their all-in amount
      java.util.Arrays.sort(allin, new Comparator() {
        public int compare(Object o1, Object o2) {
          return (int) (((PokerPresence) o1).currentRoundBet() * 100 -
                        ((PokerPresence) o2).currentRoundBet() * 100);
        }
      });
      for (int i = 0; i < allin.length; i++) {
        _cat.finest("CRB=" + allin[i].currentRoundBet() + " name =" +allin[i].name());
      }

      double totalAllIn = 0;
      ArrayList allInPots = new ArrayList();
      double amt = 0;

      /**
       * Create as many pot as there were different current round bets
       */
      for (int i = 0, start = 0; i < allin.length; i++, start = i) {
        while (i < allin.length - 1 && allin[i].currentRoundBet() == allin[i + 1].currentRoundBet()) {
        	_cat.finest(allin[i].currentRoundBet() + ", " + allin[i] + " allin equals " + allin[i + 1]);
        	++i;
        }
        amt = allin[i].currentRoundBet() - totalAllIn;
        
        _cat.finest("amount of all-ins = " + amt + ", prev allin level = " + totalAllIn + ", ALLINCRB=" + allin[i].currentRoundBet());
        
        if (amt < -0.01) {
          _cat.log(Level.WARNING, "CRB < TALL_IN" + amt + " gid/grid " + name() + "/" + grid());
        }

        Pot p = new Pot((_pots.size() - 1 + allInPots.size()) == 0 ? "main" : "side-" + (_pots.size() - 1 + allInPots.size()), _rakePercent, _validMaxRake, this);
        
        // add all-in players as pot contender
        for (int j = i; j >= start; j--) {
          _cat.finest(amt + " adding all-in " + allin[j]);
          p.addContender(allin[j], amt);
        }
        // add higher all-ins also
        for (int j = i + 1; j < allin.length; j++) {
          _cat.finest(amt + " adding higher all-ins " + allin[j]);
          p.addContender(allin[j], amt);
        }
        // add all the active players as pot contender
        for (int j = 0; j < actives.length; j++) {
          _cat.finest(amt + " adding actives " + actives[j]);
          p.addContender(actives[j], amt);
        }
        // add folded players as pot contender if the bet allows that
        for (int k = 0; k < folded.length; k++) {
          _cat.finest(amt + " adding folded " + folded[k]);
          // remove the required bet from this folded player
          double required_amount = folded[k].currentRoundBet() - totalAllIn;
          _cat.finest("Required amount" + required_amount + ", Amount=" + amt + ", Folded player CRB=" + folded[k].currentRoundBet());
          double amt_added;
          if (required_amount > amt) {
            amt_added = amt;
          }
          else if (required_amount > 0) {
            amt_added = required_amount;
          }
          else {
            amt_added = 0;
          }
          _cat.finest(folded[k].currentRoundBet() + ", " + amt_added);
          p.addVal(amt_added);
        }

        _cat.finest("POT==" + p);
        allInPots.add(p);
        totalAllIn += amt;
      }

      // reset all ins
      for (int i = 0; i < allin.length; i++) {
        allin[i].setAllInAdjusted();
      }
      /**
       * Create new main pot active pot
       */
      Pot mainPot = new Pot("side-" + (_pots.size() - 1 + allInPots.size()),
                            _rakePercent, _validMaxRake, this); // create a new side pot
      // add active players as contender for main pot
      _cat.finest("Current Rnd Bet " + this._currentRoundBet);
      for (int j = 0; j < actives.length; j++) {
        _cat.finest(actives[j] + ", " + actives[j].currentRoundBet());
        mainPot.addContender(actives[j],  actives[j].currentRoundBet() - totalAllIn);
      }

      // add money from folded players to the main pot
      for (int k = 0; k < folded.length; k++) {
        _cat.finest(folded[k] + ", " + folded[k].currentRoundBet());
        // remove the required bet from this folded player
        if (folded[k].currentRoundBet() > totalAllIn) {
          mainPot.addVal(folded[k].currentRoundBet() - totalAllIn);
        }
      }

      _cat.finest("Main pot " + mainPot);
      /**
       * Add the value of current pot to the first all-in
       */
      Pot firstAllInPot = (Pot) allInPots.get(0);
      firstAllInPot.addPot(_currentPot); // contenders are same
      _cat.finest("First all in pot " + firstAllInPot);

      // add the all in pots
      _pots.addAll(allInPots);
      // set the current pot and add it to _pots
      _pots.remove(_currentPot);
      _currentPot = mainPot;
      _pots.add(_currentPot);
      //
      _table.commitTotalBet(_players);
      _currentRoundBet = 0;

      /*
       Main pot has been split and side pots have been created.
       A side pot is associated with players who have contributed towards it.
       After this association player bets in the game are committed.
       */
      //Iterator i = _pots.iterator();
      //while (i.hasNext()) {
        //_cat.finest("POTS=" + i.next());
      //}
    }
  }

  public void post(Presence p, double amt, long mvId) {
    assert amt >= 0:"The amount is " + amt + " for " + p.name() + ", for move " +
        new PokerMoves(mvId).stringValue(); 
        
    _cat.finest(new PokerMoves(mvId).stringValue() + ", " + amt + ", " + p);
    if (amt > 0.001) {
      _isRoundOpen = false;
    }
    if ((mvId & (PokerMoves.BIG_BLIND | PokerMoves.SMALL_BLIND | PokerMoves.SBBB)) == 0) { //no blinds
      p.unsetRaiseReq();
    }
    p.unsetNew();
    if (mvId == PokerMoves.FOLD && p.isDisconnected() && !p.isTournyPresence()) {
      p.lastMove(PokerMoves.LEAVE);
    }
    else {
      p.lastMove(mvId);
    }
    p.currentRoundBet(amt);
    p.betValue(p.currentRoundBet());
    _currentRoundBet += amt;
  }

  public void declarePotWinners() {
    //_cat.finest("----------------------------------------------------");
    for (int i = 0; _players != null && i < _players.length; i++) {
      _players[i].showDownHand(_players[i].getHand().copy());
      //_cat.finest(_players[i].getHand().getAllCardsString());
    }
    //_cat.finest(_communityCards.stringValue());
    for (int j = 0; j < _pots.size(); j++) {
      Pot pot = (Pot) _pots.get(j);
      if (pot == null || pot.getVal() < 0.001) {
        continue;
      }
      //_cat.finest("Pot = " + pot);
      attachWinners(pot);
    }
  }

  public boolean loopOver(Presence p) {
    //_cat.finest("------------------Checking loop over ");
    /***PokerPresence tv[] = _table.allPlayers( -1);
         for (int k = 0; k < tv.length; k++) {
      //_cat.finest(" On table =" + tv[k]);
         }
         tv = _players;
         for (int k = 0; tv != null && k < tv.length; k++) {
      //_cat.finest(" On Game =" + tv[k]);
         }***/

    PokerPresence[] active = activePlayersForMove(0);
    PokerPresence[] aip = allInPlayers(0);
    PokerPresence[] apiain = activePlayersIncludingAllIns(0);

    if (active == null) {
      return true;
    }

    // check for exceptional conditions

    if (!abOver()) {
      _cat.finest("Blinds are not over " + active.length);
      return false;
    }
    if (nextActive() == null) {
      _cat.finest("Next Active player is null");
      return true;
    }
    if (p.lastMove() == PokerMoves.FOLD && apiain.length <= 1) {
      _cat.finest("The player has folded and there is only one remaining player");
      return true;
    }
    if (p.lastMove() == PokerMoves.LEAVE && apiain.length <= 1) {
      _cat.finest("The player has left and there is only one remaining player");
      return true;
    }

    double allInRefVal = 0;
    for (int k = 0; k < aip.length; k++) {
      _cat.finest(" ALL Ins =" + aip[k]);
      if (aip[k].currentRoundBet() > allInRefVal) {
        allInRefVal = aip[k].currentRoundBet();
        _cat.finest(" Max AllIn =" + allInRefVal);
      }
    }
    double refVal = 999999999;
    for (int k = 0; k < active.length; k++) {
      _cat.finest(" Actives =" + active[k]);
      if (active[k].currentRoundBet() < refVal) {
        refVal = active[k].currentRoundBet();
        _cat.finest(" Min Ref Val =" + refVal);
      }
    }
    // if everyone bets equal amount
    boolean equal_bets = true;
    for (int k = 0; k < active.length; k++) {
      if (Math.abs(active[k].currentRoundBet() - refVal) > 0.001) {
        equal_bets = false;
        _cat.finest(" Bets not equal ....");
        break;
      }
    }

    boolean every_body_checks = true;
    for (int k = 0; k < active.length; k++) {
      if (active[k].lastMove() != PokerMoves.CHECK) {
        every_body_checks = false;
        //_cat.finest("Everybody does not checks ....");
        break;
      }
    }

    PokerPresence next_active = nextActive();
    PokerPresence next_active_dealer = nextActiveorAllIn(dealer().pos());
    _cat.finest(" marker = " + marker() + " current=" + current());
    _cat.finest(" Marker =" + marker().name() + " Dealer =" + dealer().name());
    _cat.finest(" Marker next = " + next_active.name() + " Dealer next =" + next_active_dealer.name());

    if (aip.length > 0) { //p.lastMove() == PokerMoves.ALL_IN && apiain.length == 1) {
      if ((refVal - allInRefVal) > -0.001 && equal_bets && !next_active.isRaiseReq()) {
        _cat.finest(refVal + " Some player has done all-in and the bets are more than that ... loop over");
        return true;
      }
      else {
        if (apiain.length == 1) {
          _cat.finest("One active player");
          return true;
        }
        else {
          _cat.finest(refVal + " Some player has done all-in and there is remaining player with less bet or unequal bets");
          return false;
        }
      }
    }

    if (apiain.length == 1) {
      _cat.finest("One active player");
      return true;
    }

    /**
     * check for various conditions, which may indicate that the
     * loop is over
     */

    if (every_body_checks && aip.length == 0) {
      _cat.finest("loop over as everyone checks");
      return true;
    }

    boolean loop_over = false;
    if (dealer().isActive()) {
      if ((marker().pos() == dealer().pos())) {
        // if dealer is active,
        //then marker should be equal to dealer for loop to end
        loop_over = true;
        _cat.finest("loop over as the dealer is active and marker and dealer are same " + marker().pos());
      }
    }
    else if (next_active.pos() == next_active_dealer.pos()) { // dealer is inactive
      loop_over = true;
      _cat.finest(_loops + " dealer next and marker next are equal " + next_active.pos());
    }

    if ((loop_over && !next_active.isRaiseReq() && equal_bets) ||
        (equal_bets && (refVal > 0.01))) {
      if (_loops == 0 && _bettingRound == R_PREFLOP && next_active.isRaiseReq()) {
        _cat.finest(" this is a blind round and first loop,");
        return false;
      }
      else {
        _cat.finest(_loops + " Loop Over due to equal bets Ref Val " + refVal);
        return true;
      }
    }
    return false;
  }

  public boolean bettingRoundOver(Presence p) {
    _cat.finest("----------------------bettingRoundOver " + _bettingRound + " loops = " + _loops);
    if (_loops == _max_loops && maxBet() > 0) { //@todo : configurable value
      _cat.finest("Max loops " + _max_loops);
      return true;
    }
    PokerPresence[] active = activePlayersForMove(dealer().pos());
    PokerPresence[] apiain = activePlayersIncludingAllIns(dealer().pos());
    PokerPresence[] aip = allInPlayers(dealer().pos());

    if (active == null) {
      return true;
    }

    if (p.lastMove() == PokerMoves.FOLD && apiain.length <= 1) {
      _cat.finest("The player has folded and there is only one remaining player");
      return true;
    }
    if (p.lastMove() == PokerMoves.LEAVE && apiain.length <= 1) {
      _cat.finest("The player has left and there is only one remaining player");
      return true;
    }

    double allInRefVal = 0;
    for (int k = 0; aip != null && k < aip.length; k++) {
      if (aip[k].currentRoundBet() > allInRefVal) {
        allInRefVal = aip[k].currentRoundBet();
        _cat.finest(" Max AllIn =" + allInRefVal);
      }
    }
    double refVal = 999999999;
    for (int k = 0; k < active.length; k++) {
      if (active[k].currentRoundBet() < refVal) {
        refVal = active[k].currentRoundBet();
        _cat.finest(" Min Bet Ref Val =" + refVal);
      }
    }
    boolean all_checked = true;
    for (int k = 0; k < active.length; k++) {
      if (active[k].lastMove() != PokerMoves.CHECK) {
        all_checked = false;
        break;
      }
    }
    // if everyone bets equal amount
    boolean equal_bets = true;
    for (int k = 0; k < active.length; k++) {
      if (Math.abs(active[k].currentRoundBet() - refVal) > 0.001) {
        _cat.finest(" CurrRound Bet " + active[k]);
        equal_bets = false;
        break;
      }
    }

    if (aip.length > 0) { //p.lastMove() == PokerMoves.ALL_IN && apiain.length == 1) {
      if ((refVal - allInRefVal) > -0.001 && equal_bets) {
        _cat.finest(refVal +" Some player has done all-in and the bets are more than that ... loop over");
        return true;
      }
      else {
        _cat.finest(refVal + "Some player has done all-in and there is remaining player with less bet or unequal bets ");
        return false;
      }
    }

    if (nextActive() == null) {
      _cat.finest("Next Active player is null");
      return true;
    }

    if (all_checked && aip.length == 0) {
      _cat.finest("Betting Round over all checked " + _bettingRound);
      return true;
    }
    if (all_checked && aip.length >= 1 && allInRefVal > 0) {
      _cat.finest(" Some player did all-in but their all-in was greater than the bet value");
      return false;
    }

    if (_isRoundOpen) {
      _cat.finest("Round open");
      return false;
    }

    if (equal_bets && refVal > 0.01) {
      if ( /*last_player_all_in*/aip.length > 0) {
        if (Math.abs(refVal - p.currentRoundBet()) < 0.001) {
          _cat.finest("last player did all-in but his all-in was less than the bet value");
          return true;
        }
        else {
          _cat.finest("last player did all-in but his all-in was greater than the bet value");
          return false;
        }
      }
      else {
        _cat.finest("Round over bet equal to " + refVal);
        return equal_bets;
      }
    }
    _cat.finest("Betting round not over");
    return false;
  }

  public abstract void initNextBettingRound();

  public boolean gameOver() {
    if (_bettingRound == _maxRounds - 1) {
      return true;
    }
    // special case when all but one or all players are all in and game cannot proceed
    PokerPresence vaip[] = activePlayersIncludingAllIns();
    if (vaip.length >= 2 &&
        activePlayersForMove(dealer().pos()).length <= 1) {
      _cat.info("Special case when all but one or all players are all in");
      if (vaip[0].getHand().cardCount() == 0 &&
    		  vaip[1].getHand().cardCount() == 0) {
        //deal hand card ASSUMES THE GAME IS HOLDEM
        if (type().isHoldem()) {
          dealOpenCards(vaip, 2);
        }
        else if (type().isOmaha()) {
          dealOpenCards(vaip, 4);
        } 
      }

      // open all players card
      for (PokerPresence p: vaip){
    	  p.getHand().setOpen();
    	  p.setShowdown();
      }
      try {
          GameProcessor.deliverResponse(new GameDetailsResponse(this, false));
	  }
	  catch (Exception e) {
	        e.printStackTrace();
	  }
	     
      boolean show_last_move=true;
      while (_bettingRound < (_maxRounds - 1)) {
        initNextBettingRound();
        try {
          GameProcessor.deliverResponse(new GameDetailsResponse(this, show_last_move));
          show_last_move=false;
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      return true;
    }
    if (activePlayers().length == 1) {
      _cat.finest("Game over: One player left " + activePlayers()[0]);
      // only one player left, he will be the winner if the other person has folded
      if (_currentPot != null) {
        _currentPot.addUnCalledBet(activePlayers()[0].currentRoundBet());
      }
      PokerPresence pa = activePlayers()[0];
      pa.resetRoundBet();
      pa.setGameEndWorth();
      if (!pa.isAllIn())pa.unsetShowdown();
      return true;
    }
    else if (activePlayers().length == 0) {
      //_cat.finest("Game over: Noplayer left ");
      return true;
    }
    return false;
  }

  public PresenceSelector selector() {
    return new GameSelector();
  }

  public PokerPresence selectPresence(PresenceSelector s, int startPos, Constraint c) {
    s.startPos(startPos);
    return s.selectPresence(c);
  }

  public PokerPresence[] selectPlayers(PresenceSelector s, int startPos, Constraint c) {
    s.startPos(startPos);
    return s.select(c);
  }

  public PokerPresence prev(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null;
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return s.selectPrevPresence(c);
  }

  public PokerPresence next() {
    PokerPresence p = marker() == null ? current() : marker();
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null;
      }
    };
    PresenceSelector s = selector();
    s.startPos(p.pos());
    return selectPresence(s, p.pos(), c);
  }

  public PokerPresence[] activePlayersForMove(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isActive() && !p.isNew() && !p.isAllIn();
        // this condition should exactly match
        // nextActive
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence[] activePlayersForCards() {
    PokerPresence p = marker() == null ? current() : marker();
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && (p.isActive() || p.isAllIn()) && !p.isNew();
        // this condition should exactly match
        // nextActive
      }
    };
    return selectPlayers(selector(), p.pos(), c);
  }

  /*
            Excludes startPos and include the endPos
   */
  public PokerPresence[] activePlayers(int startPos, int endPos) {
    PokerPresence[] p = activePlayers(startPos);
    ArrayList l = new ArrayList();
    for (int k = 0; k < p.length; k++) {
      l.add(p[k]);
      if (p[k].pos() == endPos) {
        return (PokerPresence[]) l.toArray(new PokerPresence[] {});
      }
    }
    return (PokerPresence[]) l.toArray(new PokerPresence[] {});
  }

  public PokerPresence nextActive(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isActive() && !p.isNew() && !p.isAllIn() &&
            !p.isWaitForBlinds();
        // this condition should exactly be same as
        //"activePlayers() method
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return s.selectPresence(c);
  }
  

  public PokerPresence nextActiveForTermPokerBlinds(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isActive();
        // this condition should exactly be same as
        //"activePlayers() method
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return s.selectPresence(c);
  }

  public PokerPresence nextActiveToBB() {
    return _table.nextActiveToBB();
  }

  public PokerPresence nextActive() {
    PokerPresence p = marker() == null ? current() : marker();
    return nextActive(p.pos());
  }

  public PokerPresence nextActiveToDealer() {
    return nextActive(dealer().pos());
  }

  public PokerPresence nextActiveorAllIn(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && (p.isActive() || p.isAllIn()) && !p.isNew();
      }
    };
    PresenceSelector s = selector();
    s.startPos(startPos);
    return s.selectPresence(c);
  }

  public PokerPresence[] allInsNotAdjustedPlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isAllIn() && !p.isAllInAdjusted();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence[] activePlayersIncludingAllIns() {
    PokerPresence p = marker() == null ? current() : marker();
    return activePlayersIncludingAllIns(p.pos());
  }

  public PokerPresence[] activePlayersIncludingAllIns(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && (p.isActive() && !p.isNew()) || p.isAllIn(); //&&  !p.isBetweenBlinds();
      }
    };
    return selectPlayers(selector(), pos, c);
  }

  public PokerPresence[] allInPlayers(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && (p.isAllIn() && !p.isAllInAdjusted()); //&&  !p.isBetweenBlinds();
      }
    };
    return selectPlayers(selector(), pos, c);
  }

  public PokerPresence[] showdownPlayers(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isShowdown();
      }
    };
    return selectPlayers(selector(), pos, c);
  }

  public PokerPresence nextForAnte() {
    PokerPresence p = marker() == null ? current() : marker();
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && !p.isABPosted() && !p.isBroke() &&
            !p.isWinLossViolated() && !p.isRemoved() && p.isAnte() &&
            !p.isOptOut();
      }
    };
    PresenceSelector s = selector();
    s.startPos(p.pos());
    return s.selectPresence(c);
  }

  public PokerPresence nextForBB() {
    PokerPresence p = marker() == null ? current() : marker();
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && !p.isABPosted() && !p.isBroke() &&
            !p.isWinLossViolated() && !p.isRemoved() &&
            (p.isBB() || p.isMissedBB() || p.isMissedSB()) && !p.isOptOut() &&
            !p.isBetweenBlinds() && !p.isWaitForBlinds();
      }
    };
    PresenceSelector s = selector();
    s.startPos(p.pos());
    return s.selectPresence(c);
  }

  public PokerPresence nextForSB() {
    PokerPresence p = marker() == null ? current() : marker();
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && !p.isABPosted() && !p.isBroke() &&
            !p.isWinLossViolated() && !p.isRemoved() && p.isSB() && !p.isOptOut() &&
            !p.isBetweenBlinds();
      }
    };
    PresenceSelector s = selector();
    s.startPos(p.pos());
    return s.selectPresence(c);
  }

  public PokerPresence nextActiveOrNewOrSitInAndNotBetweenBlinds(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null &&
            ((p.isActive() || p.isWaitForBlinds()) && !p.isBetweenBlinds());
      }
    };
    return selectPresence(selector(), startPos, c);
  }

  public PokerPresence nextActiveOrNewOrSitIn(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && (p.isActive() && !p.isBetweenBlinds());
      }
    };
    return selectPresence(selector(), startPos, c);
  }

  public PokerPresence nextActiveOrNewOrSitInOrWaitForBB(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null &&
            ((p.isActive() || p.isWaitForBlinds()) && !p.isBetweenBlinds());
      }
    };
    PokerPresence p = selectPresence(selector(), startPos, c); // this is active
    //there might be an inactive presence waiting for blinds on the table
    PokerPresence pw = _table.getWaitForBlinds(startPos, startPos);
    if (pw == null) {
      _cat.finest("nextActiveOrNewOrSitInOrWaitForBB=" + p);
      return p;
    }
    else {
      pw.unsetWaitForBlinds();
      pw.unsetBetweenBlinds();
      _cat.finest("pw nextActiveOrNewOrSitInOrWaitForBB=" + pw);
      return pw;
    }
  }

  // @todo : make true random
  protected Card[] drawCards(int count) {
	  assert _deck.cardsRemaining()< count : "Deck is empty card left " + _deck.cardsRemaining();
	  Card []cv = new Card[count];
	  for (int i=0;i<count;i++){
		  cv[i] = _deck.deal();
	  }
	  return cv;
  }

  protected void dealOpenCards(Presence[] p, int count) {
    for (int i = 0; i < p.length; i++) {
      ((PokerPresence)p[i]).addOpenCards(drawCards(count));
    }
  }

  protected void dealCloseCards(Presence[] p, int count) {
    assert p.length <= 10:" More than 10 players sitting on this table " +
        p.length; for (int i = 0; i < p.length; i++) {
      ((PokerPresence)p[i]).addCloseCards(drawCards(count));
    }
  }

  public PokerPresence inquirer() {
    return (PokerPresence)_inquirer;
  }

  public int getAllPlayerCount() {
	  PokerPresence v[] = _table.allPlayers( -1);
	  return v == null ? 0 : v.length;
  }
  

  public int getPlayerCount() {
	  if (_players==null)return 0;
	  return _players.length;
  }

  public PokerPresence getPlayerAtPos(int i){
      if (i<0 || i > this._maxPlayers){
          return null;
      }
      return _table.getPlayerAtPos(i);
  }

  public PokerPresence[] getPlayerList() {
    return _players;
  }

  public PokerPresence[] getCurrentAndLeftPlayerList(){
    Set allP = Collections.synchronizedSet(new HashSet(_maxPlayers));
    for (Iterator i=_left.iterator(); i.hasNext();){
      allP.add(i.next());
    }
    PokerPresence v[] = allPlayers( -1);
    for (int i = 0; i < v.length; i++) {
      allP.add(v[i]);
    }
    return (PokerPresence [])allP.toArray(new PokerPresence[allP.size()]);
  }

  
  public PokerPresence[] activePlayersForRake() {
	    PokerPresence p = marker() == null ? current() : marker();
	    Constraint c = new Constraint() {
	      public boolean satisfy(PokerPresence p) {
	        return p != null && (p.isActive() || p.isAllIn() || p.isFolded()) && !p.isNew();
	        // this condition should exactly match
	        // nextActive
	      }
	    };
	    return selectPlayers(selector(), p.pos(), c);
	  }
  
  public boolean validatePresenceOnTable(Presence p) {
    return _table.onTable(p);
  }

  public PokerPresence getPresenceOnTable(String name) {
    PokerPresence v[] = allPlayers( -1);
    for (int i = 0; i < v.length; i++) {
      if (v[i].name().equals(name)) {
        return v[i];
      }
    }
    return null;
  }

  public PokerPresence getPresenceInGame(String name) {
    for (int i = 0; i < _players.length; i++) {
      if (_players[i].name().equals(name)) {
        return _players[i];
      }
    }
    return null;
  }

  public synchronized boolean validatePresenceInGame(String p) {
    if (_players == null) {
      return false;
    }
    for (int i = 0; i < _players.length; i++) {
      //_cat.finest(" validatePresenceInGame=" + _players[i].name());
      if (_players[i].name().equals(p)) {
        return true;
      }
    }
    return false;
  }

  public boolean onTable(String p) {
    return _table.onTable(p);
  }

  public PokerPresence dealer() {
    return _table.dealer();
  }

  public int maxPlayers() {
    return _maxPlayers;
  }

  public int minPlayers() {
    return _minPlayers;
  }

  public double minBet() {
    return _minBet;
  }

  public double maxBet() {
    return _maxBet;
  }

  public int maxRounds() {
    return _maxRounds;
  }

  public synchronized double rake() {
    double r = 0;
    for (int i = 0; i < _pots.size(); i++) {
      r += ((Pot) _pots.get(i)).getRake();
    }
    return r;
  }

  public Set observers() {
    return _observers;
  }

  public String name() {
    return _name;
  }

  public double currentPot() {
    return _currentPot == null ? 0 : _currentPot.getVal();
  }

  public String currentPotValueString() {
    return _currentPot == null ? "0.00" : _currentPot.getValString();
  }

  public double totalPot() {
    double total = 0;
    for (int i = 0; i < _pots.size(); i++) {
      Pot p = (Pot) _pots.get(i);
      total += p.getVal();
    }
    return total;
  }

  public double totalRake() {
    double total = 0;
    for (int i = 0; i < _pots.size(); i++) {
      Pot p = (Pot) _pots.get(i);
      total += p.getRake();
    }
    return total;
  }

  public double averagePot() {
    return _averagePotValue;
  }

  public int flopPlayers() {
    return _flopPlayers;
  }

  public int numHandsPerHour() {
    long delta = (System.currentTimeMillis() - _creation_time);
    if (delta < 600000){
    	return _numOfHands * 10;
    }
    else {
      long factor = delta / 600000;
      return (int)((_numOfHands / factor) * 10);
    }
  }

  public long communityCards() {
    return Hand.getHandFromCardArray(_communityCards.getCards());
  }

  public Card[] getCommunityCards(){
      return _communityCards.getCards();
  }

  public String communityCardsString() {
    return _communityCards.toString();
  }

  public List<Pot> pots() {
    assert _pots != null:"Game pot null " + name(); return _pots;
  }

  public double currentBet() {
    return _currentRoundBet;
  }

  public int showdownPos() {
    return _showdownPos;
  }

  public void showdownPos(int sdp) {
    _showdownPos = sdp;
  }

  public void flushLastLeft() {
    ArrayList toRemove = new ArrayList(_last_left.size());
    for (Iterator i = _last_left.iterator(); i.hasNext(); ) {
      LastLeft ll = (LastLeft) i.next();
      if (ll.isOldEnough()) {
        toRemove.add(ll);
      }
    }
    _last_left.removeAll(toRemove);
  }

  public void flushLastLeft(Presence p) {
    ArrayList toRemove = new ArrayList(_last_left.size());
    for (Iterator i = _last_left.iterator(); i.hasNext(); ) {
      LastLeft ll = (LastLeft) i.next();
      if (ll._p.name().equals(p.name())) {
        toRemove.add(ll);
        break;
      }
    }
    _last_left.removeAll(toRemove);
  }

  public LastLeft isLastLeft(Presence p) {

    for (Iterator i = _last_left.iterator(); i.hasNext(); ) {
      LastLeft ll = (LastLeft) i.next();
      if (ll.equals(p)) {
        return ll;
      }
    }
    return null;
  }
  
  public PokerPresence marker(){
      return (PokerPresence)super.marker();
  }

    public PokerPresence current(){
        return (PokerPresence)super.current();
    }

  public PokerGameType type(){
      return _type;
  }

   public  void type(PokerGameType type){
       _type = type;
   }
  
  public int bettingRound(){
      return _bettingRound;
  }
  
  public void setNormalStack(){
	  _stack = "normal";
  }

  public void setDeepStack(){
	  _stack = "deep";
  }

  public void setShallowStack(){
	  _stack = "shallow";
  }
  
  public String getStack(){
	  return _stack;
  }
 

  ///*************************************************************************

   public int _max_loops = 4;

   protected Vector _last_left = new Vector();
  protected LinkedList _waiters = new LinkedList();
  protected double _currentRoundBet;
  protected boolean _isRoundOpen;
  protected boolean _isRoundOver;
  protected boolean _resetLastPokerMoves;
  protected boolean _gameOver;
  public  PokerGameType _type;
  private String _stack="normal"; //DECORATION
  protected int _minPlayers;
  protected int _maxPlayers;

  protected int _loops = 0;
  protected PokerPresence[] _players;
  protected Set<PokerPresence> _left = Collections.synchronizedSet(new HashSet<PokerPresence>(_maxPlayers));

  protected double _minBet;
  protected double _maxBet;
  protected int _maxRounds;
  protected double _rakePercent;
  protected double[] _maxRake;
  protected double _validMaxRake;
  protected double _rakeRoundAmount;
  protected double _call_amount;
  protected double _averagePot[] = new double[10];
  protected double _averagePotValue;
  protected int _avePotIndex = 0;
  protected int _flop[] = new int[10];
  protected int _flopIndex = 0;
  protected int _flopPlayers = 0;
  private int _numOfHands = 0;
  protected int _bettingRound;
  protected int _showdownPos;

  public static final int R_PREFLOP = 0;
  public static final int R_FLOP = 1;
  public static final int R_TURN = 2;
  public static final int R_RIVER = 3;

  public int _raiseCount = 0;
  public double _lastRaise = 0;
  public boolean _uncalledBet = false;
  public boolean _uncalledRaise = false;
  public double _betValue = 0;

  protected MyDeck _deck;
  protected Cards _communityCards = new Cards(false);

  protected Table _table = null;
  protected ArrayList _pots = new ArrayList();
  protected Pot _currentPot = null;

  public static final double POT_LIMIT = 0;
  public static final double NO_LIMIT = -1;
  public static final int MAX_RAISES = 3;
  public static final int HUP_MAX_RAISES = 7;
  public static final int NLPL_MAX_RAISES = 20;

  public int _blinds_over_toggle = 0;
  public final static int BLINDS = 0;
  public final static int BLINDS_TRANSITION = 1;
  public final static int BLINDS_OVER = 2;

  public String toString() {
    return "name=" + _name + ",type=" + _type + ",loops=" +
        _max_loops + ",players=" + _minPlayers + "/" + _maxPlayers + ",bets=" +
        _minBet + "/" + _maxBet + ", TrnId " + _tournyId + ", Partners=" +
        partnerString() + ", Affiliate=" + affiliateString();
  }

  class GameSelector implements PresenceSelector {

    public void startPos(int pos) {
      this.pos = pos;
    }

    int startPos() {
      int st = searchPos(_players, pos);
      if (st < 0) {
        _cat.info(st + " The postion searching for is not found " + pos);
      }
      return st < 0 ? 0 : st; // if st < 0, no player at that position exists (has left)
    }

    public PokerPresence selectPresence(Constraint c) {
      if (_players == null) {
        return null;
      }
      int l = _players.length;
      ////_cat.finest("Players length=" + l);
      int e = l;
      int st = startPos() + 1;
      //  //_cat.finest("Start pos=" + st);
      for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
//        //_cat.finest("select PokerPresence = " + _players[i]);
        if (c.satisfy(_players[i])) {
          //        //_cat.finest("selected PokerPresence = " + _players[i]);
          return _players[i];
        }
      }
      return null;
    }

    public PokerPresence[] select(Constraint c) {
      if (_players == null) {
        return null;
      }
      int l = _players.length; // length
      int e = l;
      ArrayList v = new ArrayList();
      int st = startPos() + 1; // tentative start pos
      for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
        if (c.satisfy(_players[i])) {
          v.add(_players[i]);
        }
      }
      v.trimToSize();
      return (PokerPresence[]) (v.toArray(new PokerPresence[] {}));
    }

    public PokerPresence selectPrevPresence(Constraint c) {
      if (_players == null) {
        return null;
      }
      int l = _players.length; // length
      int e = l;
      int st = startPos() - 1; // tentative start pos
      for (int i = (st == -1 ? (l - 1) : st); --e > -1;
          i = (--i == -1 ? (l - 1) : i)) {
        if (c.satisfy(_players[i])) {
          return _players[i];
        }
      }
      return null;
    }

    public int searchPos(PokerPresence[] a, int key) {
      for (int i = a.length - 1; i >= 0; i--) {
        if (a[i].pos() <= key) {
          return i;
        }
      }
      return a.length - 1;
    }

    private int pos;
  }

  static long LEAVE_WAIT = 30 * 60 * 1000;

  public class LastLeft {
    Presence _p;
    long _time;
    double _chips;

    public LastLeft(Presence p, double chips) {
      _p = p;
      _time = System.currentTimeMillis() + LEAVE_WAIT;
      _chips = chips;
    }

    public boolean isOldEnough() {
      return System.currentTimeMillis() > _time;
    }

    public boolean equals(Presence p) {
      return p.name().equals(_p.name());
    }
  }

  public static void main(String args[]) {
	  double amount =  9.759999999999998;
	  double val = 9.76;
	  if (val < Utils.getRounded(amount)){
		  System.out.println("Samll");
	  }
	  else if ( val > Utils.getRounded(amount) ){
		  System.out.println("Big");
	  }
	  else {
		  System.out.println("Equal");
	  }
	  System.out.println("val=" + val);
	  System.out.println("amount=" + val);
  }

}
