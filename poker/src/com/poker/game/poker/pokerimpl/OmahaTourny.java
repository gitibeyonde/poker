package com.poker.game.poker.pokerimpl;

import com.agneya.util.Utils;

import com.golconda.db.ModuleType;
import com.golconda.game.GameStateEvent;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Cards;
import com.golconda.game.util.MyDeck;

import com.poker.common.interfaces.MTTInterface;
import com.poker.common.message.ResponseGameEvent;
import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.CollectABResponse;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.GameOverResponse;
import com.poker.game.poker.LeaveResponse;
import com.poker.game.poker.MoveResponse;
import com.poker.game.poker.Pot;
import com.poker.game.poker.SitInResponse;
import com.poker.game.poker.Tourny;
import com.poker.game.poker.TournyHandOverResponse;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;


public class OmahaTourny
    extends OmahaHi implements MTTInterface {

  // set the category for logging
  static Logger _cat = Logger.getLogger(OmahaTourny.class.getName());

  public Response start() {
    PokerPresence[] pl = _table.eligiblePlayers();
    // check if the players
    if (pl.length >= _minPlayers) { // minplayers ==2
      _state = HAND_RUNNING;
      _inProgress = true; //@todo : cases where inprogress will be false.
      _currentPot = null;
      prepareForNewRun();
      _cat.info("STARTING " + name());
      _startCount=0;
      return new CollectABResponse(this);
    }
    else if (pl.length == 1) {
      _inProgress = false;
      _players = pl;
      // special case when the table has single player
      _cat.info("Single player waiting .." + pl[0]);
      if (_state != HAND_INIT) {
        _t.addWinner(pl[0]);
        _state = HAND_NOSTART;
        _cat.info("TABLE CLOSED " + name() + " ," + pl[0]);
        _inquirer = pl[0];
        _table.remove(pl[0]);
        pl[0].unsetRemoved(); // special case for tourny
      }
      _startCount++;
      return new GameDetailsResponse(this);
    }
    else {
      _players = pl;
      _state = HAND_NOSTART;
      _inProgress = false;
      _startCount++;
      return new GameDetailsResponse(this);
    }
  }

  public OmahaTourny(String name, int limit, int minPlayers, int maxPlayers,
                      String[] affiliate,
                      Observer stateObserver, Tourny tourny) {
    super( name, minPlayers, maxPlayers, 0, 0, affiliate, null,
          stateObserver);
   _type = new PokerGameType(PokerGameType.OmahaHiTourny);
    _keepRunning = false;
    _t = tourny;
    _limit = limit;
    _rakePercent = 0;
    _validMaxRake = 0;
  }

  public boolean reRunCondition() {
    return _keepRunning &&
        (_table.eligiblePlayers().length > _minPlayers || _inProgress);
    // any other validations
  }

  public void setupNewRun() {
    _table.markEligiblesActive(_minBet);
    _players = _table.eligiblePlayers();
    if (_players.length < _minPlayers) {
      _inProgress = false;
      _cat.finest("Unable to start the game ");
      return;
    }

    this._minBet = _t._level_limits[_t._hand_level][0];
    if (_limit >=1 ){
      this._maxBet = _t._level_limits[_t._hand_level][1];
    }
    else {
      this._maxBet = _limit;
    }

    double sb[] = Utils.integralDivide(_minBet, 2);
    this.smallBlind = sb[1];
    this.bigBlind = (int) _minBet;
    _cat.info("Doubling Bets " + this);
    _flop[_flopIndex] = -1;
    _deck = new MyDeck(false);
    _bettingRound = R_PREFLOP;
    _raiseCount = 0;
    _loops = 0;
    _currentRoundBet = 0;
    _inquirer = null;

    _table.setDealer();
    _cat.finest("Dealer = " + dealer());

    _table.setAB();
    setMarker(_table.dealer());
    _cat.finest("Setting up new run dealer = " + _table.dealer());

    if (_currentPot == null) {
      _pots = new ArrayList();
      _currentPot = new Pot("main", 0, 0, this);
      _pots.add(_currentPot);
      for (int i = 0; _players != null && i < _players.length; i++) {
        _currentPot.addContender(_players[i], 0); // only potential contender. No more.
        _cat.finest("Adding contenders " + _players[i]);
      }
    }

    resetInvite();
    for (int i = 0; i < _players.length; i++) {
      _players[i].gameStart(_grid);
      addInvite(_players[i]);
      //_cat.finest("Eligible players " + _players[i]);
    }
    // what about dealer designation ?
    update(this, GameStateEvent.GAME_BEGIN);
    if (_keepRunning) {
      _inProgress = true;
    }
  }

  public void postRun() {
    // set the stakes
    this._minBet = _t._level_limits[_t._hand_level][0];
    if (_limit >=1 ){
      this._maxBet = _t._level_limits[_t._hand_level][1];
    }
    else {
      this._maxBet = _limit;
    }
    double sb[] = Utils.integralDivide(_minBet, 2);
    this.smallBlind = sb[1];
    this.bigBlind = (int) _minBet;
    _table.commitTotalBet(_players);
    _table.postRun(); //@todo :  impl of prepare for new run marks all as active
    update(this, GameStateEvent.GAME_OVER);
    _cat.finest("Game Over -----------------------------");
  }

  public void postWin() {
    refreshPlayerStatus();
    _communityCards = new Cards(false);
    _table.postWinTourny();
    _left.clear();
    _pots = new ArrayList();
    _currentPot = new Pot("main", 0, 0, this);
    _pots.add(_currentPot);
    for (int i = 0; _players != null && i < _players.length; i++) {
      _currentPot.addContender(_players[i], 0); // only potential contender. No more.
      _cat.finest("Setup new Run Adding contenders " + _players[i]);
    }
  }

  public void refreshPlayerStatus() {
    PokerPresence[] v = _table.allPlayers( -1);
    int _deb_enlen = v.length;
    int _deb_count = 0;
    for (int i = 0; i < v.length; i++) {
      _cat.info("Refresh " + v[i]);
      if (v[i].getAmtAtTable() <= 0) {
        _deb_count++;
        _cat.info("BROKE " + v[i]);
        _t.addWinner(v[i]);
        //v[i].lastMove(PokerMoves.LEAVE);
        this.setInquirer(v[i]);
        setCurrent(v[i]);
        SitInResponse gdr = new SitInResponse(this, -78);
        GamePlayer gp = (GamePlayer) v[i].player();
        gp.deliver(new ResponseGameEvent(1, gdr.getCommand(v[i])));
        _cat.finest("Sending " + gdr.getCommand(v[i]) + "  to " + v[i]);
        _table.remove(v[i]);
        _observers.add(v[i]);
        v[i].unsetRemoved(); // special case for tourny
      }
    }
    _table.markEligiblesActive(_minBet);
    _players = _table.eligiblePlayers();
    //_cat.warn(_deb_enlen + "==" + _deb_count + " +" +  _players.length);
    if (_deb_enlen != _deb_count + _players.length) {
      new Exception("The winner count slipped here").printStackTrace();
    }
    resetPartners();
    for (int i = 0; i < _players.length; i++) {
      addInvite(_players[i]);
      _cat.info("AddInvite " + _players[i]);
    }
  }

  public void destroyTournyTable() {
    PokerPresence[] v = allPlayers( -1);
    for (int i = 0; v != null && i < v.length; i++) {
      GamePlayer gp = (GamePlayer) v[i].player();
      gp.deliver(new com.poker.common.message.ResponseString(1,
          com.golconda.message.Response.R_TABLE_CLOSED,
          name()));
      _cat.finest("Destroy = " + v[i]);
    }
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      PokerPresence p = (PokerPresence) i.next();
      GamePlayer gp = (GamePlayer) p.player();
      gp.deliver(new com.poker.common.message.ResponseString(1,
          com.golconda.message.Response.R_TABLE_CLOSED,
          name()));
      //_cat.finest("Destroy Observer = " + p);
    }
    super.remove(this);
  }

  public Response abOverResponse() {
    // special case when all but one or all players are all in and game cannot proceed

    PokerPresence[] active = activePlayersForMove(dealer().pos());
    PokerPresence[] apiain = activePlayersIncludingAllIns(dealer().pos());
    PokerPresence[] aip = allInPlayers(dealer().pos());

    double allInRefVal = 0;
    for (int k = 0; aip != null && k < aip.length; k++) {
      if (aip[k].currentRoundBet() > allInRefVal) {
        allInRefVal = aip[k].currentRoundBet();
        _cat.finest(" Max AllIn =" + allInRefVal);
      }
    }
    double refVal = 999999999999999999L;
    for (int k = 0; k < active.length; k++) {
      if (active[k].currentRoundBet() < refVal) {
        refVal = active[k].currentRoundBet();
        _cat.finest(" Min Bet Ref Val =" + refVal);
      }
    }

    if (apiain.length >= 2 && active.length <= 1 &&
        ( (refVal - allInRefVal) > -0.001)) {
      _cat.info("Special case when all but one or all players are all in");
      // check if the active player has more bet than all-in players
      procRoundOver();
      if (apiain[0].getHand().cardCount() == 0) {
        //deal hand card ASSUMES THE GAME IS HOLDEM
        dealCloseCards(activePlayersIncludingAllIns(), 2);
        try {
          GameProcessor.deliverResponse(new GameDetailsResponse(this, false));
        }
        catch (Exception e) {
          //ignore
        }

      }
      while (_bettingRound < (_maxRounds - 1)) {
        initNextBettingRound();
        try {
          GameProcessor.deliverResponse(new GameDetailsResponse(this, false));
        }
        catch (Exception e) {
          //ignore
        }

      }
      return gameOverResponse(_inquirer);
    }

    PokerPresence[] ap = activePlayers();
    if (ap == null) {
      return new GameDetailsResponse(this);
    }

    _cat.finest("Blinds-over");
    dealCloseCards( (PokerPresence[]) activePlayersIncludingAllIns(), 2);
    return new MoveResponse(this);
  }

  public Response allIn(PokerPresence p, double amt) {
    p.setAllIn();
    p.setShowdown();
    // check if this all-in is during blinds
    if (p.isSB() && !p.isABPosted()) {
      // during SB
      post(p, amt, PokerMoves.ALL_IN);
      _cat.finest("Allin during SB");
      p.setABPosted();
      return new CollectABResponse(this);
    }
    else if (p.isBB() && !p.isABPosted()) {
      post(p, amt, PokerMoves.ALL_IN);
      p.setABPosted();
      _cat.finest("Allin during BB");
      _blindsPosted = true;
      if (abOver()) {
        _cat.finest("Blinds Over");
        return abOverResponse();
      }
      else {
        return new CollectABResponse(this);
      }
    }
    _cat.finest("Allin == " + p);
    return bettingResponse(p, amt, PokerMoves.ALL_IN);
  }

  public Response join(PokerPresence p, double amount) {
    _cat.finest("Joining  Tourny " + p);
    if (!_table.join(p)) { // couldnt be added for whatever reason
      return new GameDetailsResponse(this);
    }
    _cat.finest("Joined Tourny " + p);
    _observers.remove(p);
    p.setPlayer();
    p.setNew();
    p.lastMove(PokerMoves.JOIN);
    _inquirer = p;
    setCurrent(p);
    p.joinTable(amount, new ModuleType(ModuleType.POKER));
    p.unsetBroke();

    Response r = new SitInResponse(this, 6);
    _cat.finest(r.getBroadcast());
    return r;
  }

  // a player cannot leave a tourny
  public synchronized Response leave(PokerPresence p, boolean timeout) {
    p.setDisconnected();
    _cat.finest("Leaving Tourny " + p);
    Response r = new LeaveResponse(this, p.pos());
    if (p.equals(this._nextMovePlayer) && _inProgress == true && p.isResponseReq()) {
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
    return null;
  }

  public void attachWinners(Pot p) {
    PokerPresence[] v = p.contenders();
    if (v.length == 0) {
      _cat.finest("No contenders");
      return;
    }
    if (v.length == 1) {
      p.addHighWinners(v[0], p.getVal(), 0L);
      _cat.finest("Single contenders " + v[0]);
      return;

    }

    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) HandComparator.compareGameHand( ( (PokerPresence) o2).getHand().
            getCards(),
            ( (PokerPresence) o1).getHand().getCards(),
            communityCards(), PokerGameType.OMAHA, true)[0];
      }
    });

    Vector winner = new Vector();
    v[0].setShowdown();
    winner.add(v[0]);
    _cat.finest("Winner = " + v[0]);
    for (int i = 0; i < v.length - 1; i++) {
      if (HandComparator.compareGameHand(v[i].getHand().getCards(),
                                         v[i + 1].getHand().getCards(),
                                         communityCards(), PokerGameType.OMAHA, true)[0] !=
          0L) {
        break;
      }
      else {
        v[i + 1].setShowdown();
        winner.add(v[i + 1]);
        _cat.finest("Winner = " + v[i + 1]);
      }
    }

    double hwin[] = Utils.integralDivide(p.getVal(), winner.size());
    for (int i = 0; i < winner.size(); i++) {
      PokerPresence pw = (PokerPresence) winner.get(i);
      p.addHighWinners(pw, hwin[i], HandComparator.bestHandOf5(pw.
          getHand().getCards(), Hand.getHandFromCardArray(_communityCards.getCards()), type().intVal())[0]
          );
      _cat.finest("Winner = " + winner.get(i));
    }

    //_cat.info("Showdown Position=" + showdownPos());
    int l = _players.length;
    int st = 0;
    int e = l;
    for (int i = 0; i < l; i++) {
      if (_players[i].pos() == showdownPos()) {
        st = i;
        break;
      }
    }
    int high_index = v.length;
    int j;
    for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
      j = high_index;
      for (j--; j >= 0; j--) {
        if (v[j].pos() == _players[i].pos()) {
          _players[i].setShowdown();
          //_cat.info("Showdown=" + _players[i]);
          high_index = j;
          break;
        }
      }
    }
  }

  public Response moveToTable(Presence p, String tid) {
    _cat.finest("Moving  " + p);
    p.player().movePresence(p, tid);
    if (!_table.join(p)) { // couldnt be added for whatever reason
      return new GameDetailsResponse(this);
    }
    _players = _table.allPlayers(-1);
    _observers.remove(p);
    _cat.finest("Moved " + p);
    _inquirer = p;
    p.setNew();
    p.lastMove(PokerMoves.MOVE);
    setCurrent(p);
    Response r = new SitInResponse(this, false);
    _cat.finest(r.getBroadcast());
    return r;
  }

  // @todo : winners on a per pot basis
  public Response gameOverResponse(PokerPresence p) {
    _cat.finest("Entering game over response " + p);
    declarePotWinners();
    new GameOverResponse(this); /// for loggin winner
    PokerPresence showdown_lv[]=null;
    if(this.activePlayersIncludingAllIns().length > 1){
      showdown_lv = showdownPlayers(showdownPos()); // some player may have left or is ineligible
    }
    postRun();
    _inProgress = false;
    _state = HAND_OVER;
    Response r = new TournyHandOverResponse(this, showdown_lv);
    postWin();
    return r;
  }


  /**
   * No rake for tournaments
   *
   * @param potVal double
   * @param rv double
   * @param currRake double
   * @return double
   */
  public double currRakeValue(double potVal, double rv, double currRake) {
    return 0;
  }

  public void addWinner(PokerPresence p) {
    _t.addWinner(p);
  }

  public boolean isInWinner(PokerPresence p) {
    return _t._winner.contains(p);
  }

  public boolean started() {
    return _state == HAND_RUNNING;
  }

  public boolean handOver() {
    return _state == HAND_OVER || _state == HAND_NOSTART;
  }

  public boolean tournyOver() {
    return _t.tournyOver();
  }

  public boolean tournyWaiting() {
    return _t.isWaiting();
  }

  public int limit(){
    return _limit;
  }

  public int startCount(){
    return _startCount;
  }

  public boolean canHandStart() {
    return _state != HAND_NOSTART;
  }
  
  public int state(){
      return _state;
  }

  int _state = HAND_INIT;
  final static int HAND_INIT = 1;
  final static int HAND_OVER = 1;
  final static int HAND_RUNNING = 2;
  final static int HAND_NOSTART = 4;
  int _limit=-1;
  public int _startCount=0;

  Tourny _t;

  public String toString() {
    return super.toString() + ", State=" + _state;
  }

}
