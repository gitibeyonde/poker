package com.poker.game.poker;

import com.golconda.game.PlayerStatus;
import com.golconda.game.Presence;

import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.logging.Logger;


public class Table
    implements Serializable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Table.class.getName());

  private int _size;
  private int _count;
  private String _tid;
  boolean _last_game_heads_up = false;

  private int[] _positions;
  private PokerPresence[] _players;
  // when game is on
  private PokerPresence _dealer, _sbPlayer, _bbPlayer;
  private int _nextbb;
  private Poker _g;

  public Table(String tid, int size, Poker g) {
    _g = g;
    _size = size;
    _tid = tid;
    _players = new PokerPresence[_size];
    _positions = new int[_size];
    for (int i = 0; i < _size; i++) {
      _positions[i] = -1;
    }
    _count = 0;
  }

  public PokerPresence getPlayerAtPos(int i){
      return _players[i];
  }

  public int nextVacant() {
    int vacant = -1;
    for (int i = 0; i < _size; i++) {
      if (_positions[i] != -1) {
        vacant = i;
        break;
      }
    }
    return vacant;
  }

  public synchronized boolean join(Presence p) {
    // check if this player is already sitting on the table
    for (int i = 0; i < _players.length; i++) {
      if (_players[i] != null && _players[i].name().equals(p.name())) {
        _cat.warning("The player is already sitting on the table " + p);
        _players[i]=(PokerPresence)p;
        p.setPos(i);
        return true;
      }
    }
    if (p.pos() >= _players.length){
      return false;
    }
    else if (p == null || p.pos() >= _size || p.pos() < 0) {
      return false;
    }
    else if (_players[p.pos()] != null){
        if (_players[p.pos()].name().equals(p.name()) || _players[p.pos()].isRemoved() ){
        	_players[p.pos()] = (PokerPresence)p;
        	return true;
        }
        else {
            _cat.severe("Another player sitting on this pos " + p + " player seated=" + _players[p.pos()]);
        	return false;
        }
    }
    else {
      _players[p.pos()] = (PokerPresence)p;
      _positions[p.pos()] = _count++;
      return true;
    }
  }

  public synchronized void remove(Presence p) {
    int pos = p.pos();

    p.resetRoundBet();
    p.setRemoved();
    ((PokerPresence)p).getHand().reset();

    if (_players[pos] == null && _positions[pos] == -1) {
      _cat.warning("The presence is already removed " + p);
      return;
    }

    if (_players[pos] == null || !p.name().equals(_players[pos].name())) {
      _cat.warning(p + "is not the seated player on this position " + " \n Seated player = " + _players[pos]);
      //look for player
      return;
    }

    int joinPos = _positions[pos];
    if (joinPos == -1) {
      _cat.warning("The presence is already removed " + p);
      return;
    }
    --_count;
    _players[pos] = null;
    _positions[pos] = -1;
    for (int i = 0; i < _positions.length; i++) {
      if (_positions[i] > joinPos) {
        --_positions[i];
      }
      else if (_positions[i] == joinPos && joinPos != -1) {
        _cat.warning("Two players cannot have same join Pos " + joinPos + " player " +  _players[i]);
      }
    }
  }

  public PokerPresence selectPresence(PresenceSelector s, int startPos, Constraint c) {
    s.startPos(startPos);
    return s.selectPresence(c);
  }

  public PokerPresence[] selectPlayers(PresenceSelector s, int startPos,
                                  Constraint c) {
    s.startPos(startPos);
    return s.select(c);
  }

  // listing starts from the position of current player
  public PokerPresence[] allCompanions(PokerPresence p) {
    return findCompanions(p, false);
  }

  public PokerPresence[] activeCompanions(Presence p) {
    return findCompanions(p, true);
  }

  private PokerPresence[] findCompanions(final Presence plyr,
                                    final boolean onlyActive) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p != plyr &&
            (!onlyActive ||
             (p.isActive() || p.isNew()));
      }
    };
    return selectPlayers(selector(), plyr.pos(), c);
  }

  public PokerPresence[] allPlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && !p.isRemoved();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence[] newPlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null && p.isNew();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence[] inActivePlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null &&
            (!p.isActive());
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence[] foldedPlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null &&
            (p.isFolded());
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public PokerPresence dealer() {
    return _dealer;
  }

  public PokerPresence sbPlayer() {
    return _sbPlayer;
  }

  public PokerPresence bbPresence() {
    return _bbPlayer;
  }

  public PokerPresence nextActiveToBB() {
    return _g.nextActive(_nextbb);
  }

  /*
            To designate new dealer
   */
  public synchronized void setDealer() {
	if (_g.type().isTPoker()) {
	      _dealer = firstJoinee();
	      return;
	}
	if (_g.type().isStud()) {
      _dealer = prev(_g.nextActiveOrNewOrSitIn( -1).pos());
      return;
    }
    if (_g.type().isStud()) {
      _dealer = prev(_g.nextActiveOrNewOrSitIn( -1).pos());
      return;
    }
    if (!_g.type().isOmaha() && !_g.type().isHoldem()) {
      if (_dealer == null) {
        // no dealer allocated yet
        PokerPresence firstJoinee = firstJoinee();
        _dealer = firstJoinee;
      }
      else {
        _dealer.unsetDealer();
        _dealer = _g.nextActiveOrNewOrSitInAndNotBetweenBlinds(_dealer.pos());
      }
      _dealer.setDealer();
      return;
    }
    // FOR HOLDEM AND OMAHA

    // if dealer is null
    if (_dealer == null) { // FRESH GAME
      // no dealer allocated yet
      PokerPresence firstJoinee = firstJoinee();
      firstJoinee.setDealer();
      _dealer = firstJoinee;
      _dealer.setDealer();
      return;
    }

    boolean sitting_between_blinds = false;
    if (_sbPlayer != null && _bbPlayer != null) {
      // unset between blinds for all players between dealer and SB
      // before advancing the dealer button
      if (!_last_game_heads_up && _sbPlayer.isActive() &&
          !_sbPlayer.isRemoved() && !_sbPlayer.isNew()) {
        unsetBetweenBlindsDealerToSB();
      }
    }
    else {
      PokerPresence[] v = allPlayers(_dealer.pos());
      for (int i = 0; i < v.length; i++) {
        v[i].unsetBetweenBlinds();
      }
    }

    markEligiblesActive(_g._minBet);
    _g._players = eligiblePlayers();

    _dealer.unsetDealer();
    // if the game is heads-up and the last game was also heads-up
    //the dealer position will move to bb player
    //otherwise dealer will not change
    if (_last_game_heads_up) {
      PokerPresence[] v = _g._players;
      if (v.length == 2) { // this game is also heads up
        if (_bbPlayer != null && _bbPlayer.isActive() &&
            !_bbPlayer.isRemoved() && !_bbPlayer.isNew()) {
          // dealer is not null and small blind player is also there
          //_cat.finest( 
          //"_last_game_heads_up dealer is not null and big blind player is also there");
          _dealer = _bbPlayer;
        }
        else {
          //_cat.finest("_last_game_heads_up small blind and bb player has left");
          _dealer = _g.nextActiveOrNewOrSitIn(_dealer.pos());
        }
      }
      else {
        //_cat.finest(_dealer.name() +
                  // " last game heads up, current game NOT heads up " +
                  // sitting_between_blinds);

        // last game heads up, but not the current one
        if (sitting_between_blinds) {
          if (_bbPlayer != null && _bbPlayer.isActive()) {
            _dealer = _g.prev(_bbPlayer.pos());
          }
          else {
            _dealer = _g.nextActiveOrNewOrSitIn(_dealer.pos());
          }
          //_cat.finest("New player is the dealer " + _dealer);
        }
        else {
          // do nothing hold the dealer button
          //set the default showdown position
        }
      }
      _g.showdownPos(_g.nextActiveOrNewOrSitIn(_dealer.pos()).pos());
      _dealer.setDealer();

      return;
    } /// ends heads up game
    else { // LAST GAME IS A REGULAR GAME
      PokerPresence[] v = _g._players;
      //_cat.finest("Player size " + v.length + " BB player = " + _bbPlayer);
      if (v.length == 2 && _bbPlayer != null && _bbPlayer.isActive() &&
          !_bbPlayer.isRemoved() && !_bbPlayer.isNew()) { // this game is heads-up
        _dealer = _bbPlayer;
        //_cat.finest("dealer is not null and small blind player is also there " +
                  // _dealer);
      }
      else if (_sbPlayer != null && _sbPlayer.isActive() &&
               !_sbPlayer.isRemoved() && !_sbPlayer.isNew()) {
        // dealer is not null and small blind player is also there
        _dealer = _sbPlayer;
        //_cat.finest("dealer is not null and small blind player is also there " +
                  // _dealer);
      }
      else if (v.length == 2) { //heads up
        _dealer = _g.nextActiveOrNewOrSitIn(_dealer.pos());
        //_cat.finest("small blind has left and game is heads up " + _dealer);
      }
      else { //regular game but small blind player left
        // donot change the dealer
      }
      _dealer.setDealer();
      return;
    }
  }

  /**
   * BLINDS
   * @param g Poker
   */

  public synchronized void setAB() {
    if (!_g.type().isOmaha() && !_g.type().isHoldem()) {
      return;
    }
    assert _dealer != null:"Dealer cannot be null while assigning blinds";

    if (_sbPlayer != null) {
      _sbPlayer.unsetSB();
    }
    if (_bbPlayer != null) {
      _bbPlayer.unsetSB();
    }

    if (_g.type().isTPoker()){
    	_sbPlayer = _g.nextActiveForTermPokerBlinds(_dealer.pos());
        _bbPlayer = _g.nextActiveForTermPokerBlinds(_sbPlayer.pos());
        _sbPlayer.setSB();
        _bbPlayer.setBB();
        _sbPlayer.unsetMissedBB();
        _sbPlayer.unsetMissedSB();
        _bbPlayer.unsetMissedBB();
        _bbPlayer.unsetMissedSB();

        _nextbb = _bbPlayer.pos();

        //set the default showdown position
        _g.showdownPos(_sbPlayer.pos());

        return;
    }

    if (_g.getPlayerCount() == 2) {
      _cat.info("heads up game");
      // Heads on game
      if (_dealer.isActive() || _dealer.isNew()) {
        _sbPlayer = _dealer;
        _bbPlayer = _g.nextActiveOrNewOrSitInOrWaitForBB(_dealer.pos());
      }
      else {
        throw new IllegalStateException("Dealer cannot be inactive or new ");
      }
      _last_game_heads_up = true;
    }
    else {
      _last_game_heads_up = false;
      if ( (_sbPlayer == null || !_sbPlayer.isActive()) &&
          (_bbPlayer == null || !_bbPlayer.isActive())) {
        _cat.info("game is just starting " + _dealer.pos());
        _sbPlayer = _g.nextActiveOrNewOrSitIn(_dealer.pos());
        _cat.info("SB Pos " + _sbPlayer.pos());
        _bbPlayer = _g.nextActiveOrNewOrSitInOrWaitForBB(_sbPlayer.pos());
      }
      else {
        if (_bbPlayer != null && _bbPlayer.isActive() && !_bbPlayer.isRemoved() &&
            !_bbPlayer.isNew()) {
          _cat.info("BB Player is active " + _bbPlayer);
          _sbPlayer = _bbPlayer;
          _cat.info("SB Player = " + _sbPlayer);
          _bbPlayer = _g.nextActiveOrNewOrSitInOrWaitForBB(_sbPlayer.pos());
          _cat.info("BB Player = " + _bbPlayer);
        }
        else {
          _cat.info("BB Player player has left or is broke " + _bbPlayer);
          //check if the BB player has left, the next player becomes between blinds if he is new or sitin
          boolean new_between_blinds = false;

          if (_bbPlayer == null || !_bbPlayer.isActive()) {
            int startPos = _nextbb;
            PokerPresence na = _g.nextActive(_nextbb);
            if (na != null) {
              int endPos = na.pos();
              PokerPresence v[] = selectGroup(startPos, endPos);
              for (int i = 0; i < v.length; i++) {
                if (v[i].isNew() || v[i].isSittingOut() || v[i].isBrokeOut()) {
                  v[i].setBetweenBlinds();
                  new_between_blinds = true;
                }
              }
            }
          }

          if (new_between_blinds) {
            _g._players = eligiblePlayers();
          }

          if (_g._players.length == 2) {
            _sbPlayer = _dealer;
            _last_game_heads_up = true;
          }
          else {
            // mark the next new or sitin players as sitting between blinds
            _sbPlayer = _g.nextActive(_dealer.pos());
            _cat.info("SB Player NA = " + _sbPlayer);
            if (_sbPlayer == null) {
              _sbPlayer = _g.nextActiveOrNewOrSitIn(_dealer.pos());
              _cat.info("SB Player = " + _sbPlayer);
            }
          }
          _bbPlayer = _g.nextActiveOrNewOrSitInOrWaitForBB(_sbPlayer.pos());
          _cat.info("BB Player = " + _bbPlayer);
        }
      }
    }
    _sbPlayer.setSB();
    _bbPlayer.setBB();
    _sbPlayer.unsetMissedBB();
    _sbPlayer.unsetMissedSB();
    _bbPlayer.unsetMissedBB();
    _bbPlayer.unsetMissedSB();

    _nextbb = _bbPlayer.pos();

    //set the default showdown position
    _g.showdownPos(_sbPlayer.pos());

    //_cat.finest("Small Blind " + _sbPlayer);
    //_cat.finest("Big Blind " + _bbPlayer);
    //_cat.finest("Next BB " + _nextbb);

  }

  public synchronized void setNewBB() {
    //_cat.finest("Prev Big Blind " + _bbPlayer);
    if (!_g.type().isOmaha() && !_g.type().isHoldem()) {
      return;
    }

    assert _dealer != null:"Dealer cannot be null while assigning blinds";

    if (_g.getPlayerCount() == 2) {
      _last_game_heads_up = true;
      _dealer = _sbPlayer;
    }

    _bbPlayer = _g.nextActiveOrNewOrSitIn(_bbPlayer.pos());
    // Since the big blind jumps, and new player between old and new position
    // should now be marked as between blinds
    PokerPresence v[] = selectGroup(_nextbb, _bbPlayer.pos());
    for (int i = 0; i < v.length; i++) {
      if (v[i].isNew() || v[i].isSittingOut() || v[i].isBrokeOut()) {
        v[i].setBetweenBlinds();
      }
    }

    _bbPlayer.unsetMissedBB();
    _bbPlayer.unsetMissedSB();
    _bbPlayer.unsetSB();
    _bbPlayer.setBB();
    _nextbb = _bbPlayer.pos();
    //_cat.finest("Big Blind " + _bbPlayer);

  }

  public boolean bigBlindPosted() {
    return _bbPlayer.isABPosted();
  }

  public PokerPresence opener() {
    PokerPresence[] active = allPlayers(dealer().pos());
    for (int i = active.length - 1; i > -1; i--) {
      if (active[i].isNew()) {
        if (i == active.length - 1) {
          return active[0];
        }
        else {
          return active[i - 1];
        }
      }
    }
    throw new IllegalStateException("Openerd not determinable");
  }

//public final PresenceSelector SELECTOR_IMPL = new SelectorImpl();

  public PresenceSelector selector() {
    return new SelectorImpl();
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

  public PokerPresence next(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(PokerPresence p) {
        return p != null;
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return selectPresence(s, pos, c);
  }

  public PokerPresence[] selectGroup(int startPos, int endPos) {
    int start = startPos + 1;
    int end = endPos;
    ArrayList v = new ArrayList();
    if (start < end) {
      for (int i = start; i < end; i++) {
        if (_players[i] == null) {
          continue;
        }
        v.add(_players[i]);
      }
    }
    else if (end < start) {
      for (int i = start; i < _players.length; i++) {
        if (_players[i] == null) {
          continue;
        }
        v.add(_players[i]);
      }
      for (int i = 0; i < end; i++) {
        if (_players[i] == null) {
          continue;
        }
        v.add(_players[i]);
      }
    }
    v.trimToSize();
    return (PokerPresence[]) (v.toArray(new PokerPresence[] {}));
  }

  public boolean onTable(Presence p) {
    return! (p == null || p.pos() >= _size ||
             p.pos() < 0 ||
             _players[p.pos()] != p);
  }

  public boolean onTable(String p) {
    if (_players == null) {
      return false;
    }
    for (int i = 0; i < _players.length; i++) {
      if (_players[i] != null && _players[i].name() != null &&
          _players[i].name().equals(p)) {
        return true;
      }
    }

    return false;
  }

// PokerPresence who was first to occupy a position on the table
// could be an observer, who decided to sit on a talbe and observe and would
// be prompted every time about the moves that should be made.
  public PokerPresence firstJoinee() {
    PokerPresence[] p = allPlayers( -1);
    for (int i = 0; i < p.length; i++) {
      if (_positions[p[i].pos()] == 0) {
        return p[i];
      }
    }
    _cat.warning("First joinee has to be present " + p.length);
    return p[0];
  }

  public void markEligiblesForAnte() {
    PokerPresence[] all = eligiblePlayers();
    for (int i = 0; i < all.length; i++) {
      all[i].unsetABPosted();
      all[i].setAnte();
    }
  }

  public void commitTotalBet(PokerPresence[] all) {
    for (int j = 0; j < all.length; j++) {
      all[j].resetRoundBet();
      all[j].setGameEndWorth();
    }
  }

  public void postRun() {
    PokerPresence[] p = allPlayers( -1);
    for (int i = 0; i < p.length; i++) {
      p[i].postRun();
    }
  }

  public void postWin() {
    PokerPresence[] p = allPlayers( -1);
    for (int i = 0; i < p.length; i++) {
      p[i].postWin();
    }
  }

  public void postWinTourny() {
    PokerPresence[] p = allPlayers( -1);
    for (int i = 0; i < p.length; i++) {
      p[i].postWin();
      if (p[i].getAmtAtTable() <= 0) {
        p[i].lastMove(PokerMoves.LEAVE);
        p[i].unsetResponseReq();
        _g.observe(p[i]);
        remove(p[i]);
        p[i].setRemoved();
      }
    }
  }

  public void markEligiblesActive(double minBet) {
    PokerPresence[] p = allPlayers( -1);
    unsetBetweenBlindsBBToDealer();
    //BROKE
    for (int i = 0; i < p.length; i++) {
      //_cat.info("Prev state Player = " + p[i] + ", " + PlayerStatus.stringValue(p[i].status()) + ", " + p[i].getAmtAtTable());
      if (!p[i].isBroke() && p[i].isBrokeOut()) {
        p[i].unsetBrokeOut();
        if (isBetweenBlinds(p[i])) {
          p[i].setBetweenBlinds();
          p[i].setNew();
          _cat.finest("Sitting next to SB  " + p[i]);
        }
        else {
          p[i].unsetBetweenBlinds();
        }
      }
      if (p[i].isBrokeOut()) {
        // if the dealer button crosses over set the player as new player
        checkAndSetForButtonMovement(p[i]);
      }
      else if (!p[i].isBrokeOut()) {
        if (_g.type().isTourny()) {
          if (p[i].getAmtAtTable() <= 1) {
            p[i].setBroke();
            p[i].setBrokeOut();
          }
        }
        else {
          if (p[i].getAmtAtTable() < 2 * minBet) {
            p[i].setBroke();
            p[i].setBrokeOut();
            checkAndSetForButtonMovement(p[i]);
            _cat.finest("Setting Broke " + p[i]);
          }
        }
      }
      // SITTING OUT
      if (p[i].isSitOutNextGame()) {
        p[i].setSittingOut();
        // if the dealer button crosses over set the player as new player
        checkAndSetForButtonMovement(p[i]);
      }
      else if (p[i].isSittingOut()) {
        p[i].unsetSittingOut();
        //check if the player is between blinds
        if (isBetweenBlinds(p[i])) {
          p[i].setBetweenBlinds();
          p[i].setNew();
          _cat.finest("Sitting next to SB  " + p[i]);
        }
        else {
          p[i].unsetBetweenBlinds();
        }
      }
      //_cat.info("Now Player = " + p[i]);
    }

    for (int i = 0; i < p.length; i++) { // reset all players state before new game
      // donot move the line below as some of the above depends of these statuses
      p[i].resetPlayerForNewGame(); // folded/allin/optout/abposted/allinadjusted/SB/BB is erased
    }
    for (int i = 0; i < p.length; i++) {
      double vote_per = p[i].vote() * 100;
      vote_per = vote_per / p.length;
      if (vote_per > 70) {
        p[i].setVotedOff();
      }
      p[i].incrIdleGC();
      //_cat.finest(p[i].toString());
      if (p[i].isRemoved() || p[i].isQickFold()) {
        this.remove(p[i]);
      }
      if (!_g.type().isTourny()) {
        if (p[i].isNew()) {
          p[i].setBB();
          if (p[i].getAmtAtTable() < minBet * 2.00) {
            p[i].setBroke();
          }
        }
        if (p[i].isDisconnected()) {
          p[i].setRemoved();
          this.remove(p[i]);
        }
      }
      else {
        p[i].unsetNew();
      }
      _cat.finest("Player = " + p[i]);
    }
  }

  public synchronized PokerPresence[] eligiblePlayers() {
    if (_g.type().isTourny()) {
      Constraint c = new Constraint() {
        public boolean satisfy(PokerPresence p) {
          return p != null &&
              (p.isActive() || p.isNew()) &&
              !p.isBetweenBlinds() && !p.isBroke();
          // no win-loss violated for tourny and no sitting out
        }
      };
      return selectPlayers(selector(), -1, c);
    }
    else {
      Constraint c = new Constraint() {
        public boolean satisfy(PokerPresence p) {
          return p != null &&
              (p.isActive() || p.isNew()) &&
              !p.isBetweenBlinds() && !p.isBroke() && !p.isWinLossViolated() &&
              !p.isSittingOut() && !p.isVotedOff() && !p.isRemoved() &&
              !p.isDisconnected() && !p.isWaitForBlinds();
        }
      };
      return selectPlayers(selector(), -1, c);
    }
  }

  public PokerPresence getWaitForBlinds(int startPos, int endPos) {
    //_cat.finest("Start=" + startPos + ", endPos=" + endPos);
    PokerPresence v[] = selectGroup(startPos, endPos);
    for (int i = 0; i < v.length; i++) {
      if (v[i].isWaitForBlinds() || v[i].isNew()) {
        _cat.finest("getWaitForBlinds=" + v[i]);
        return v[i];
      }
    }
    return null;
  }

  public boolean isBetweenBlinds(Presence tp) {
    int startPos = 0;
    int endPos =0;
    // if both sbplayer and bbplayer have left then no one is sitting between blinds
    if ( (_bbPlayer == null || _bbPlayer.isRemoved() || _bbPlayer.isNew()) &&
        (_sbPlayer == null || _sbPlayer.isRemoved() || _sbPlayer.isNew())) {
      return false;
    }
    else if ( (_bbPlayer == null || _bbPlayer.isRemoved() || _bbPlayer.isNew())) {
      // bb player has left
      // check if the new player is sitting next to small blind player
      if (tp.pos() == _nextbb || prev(tp.pos()).pos() == _sbPlayer.pos()) {
        return true;
      }
      else {
        return false;
      }
    }
    else if (_sbPlayer == null || _sbPlayer.isRemoved() || _sbPlayer.isNew()){
      // small blind player has left
      startPos = _dealer.pos();
    }
    else {
      startPos = _sbPlayer.pos();
    }

    endPos = _bbPlayer.pos();
    //_cat.finest(tp.name() + ", Start=" + startPos + ", endPos=" + endPos);
    PokerPresence v[] = selectGroup(startPos, endPos);
    for (int i = 0; i < v.length; i++) {
      //_cat.finest("Check Player = " + v[i]);
      if (v[i].name().equals(tp.name())) {
        //_cat.finest("Player between blinds Blinds=" + v[i]);
        return true;
      }
    }
    return false;
  }

  public boolean unsetBetweenBlindsBBToDealer() {
    boolean sitting_between_blinds = false;
    if (_dealer == null || _bbPlayer == null) {
      return false;
    }
    int startPos = _bbPlayer.pos();
    int endPos = _dealer.pos();
    //_cat.finest("Start=" + startPos + ", endPos=" + endPos);
    PokerPresence v[] = selectGroup(startPos, endPos);
    for (int i = 0; i < v.length; i++) {
      v[i].unsetBetweenBlinds();
      sitting_between_blinds = true;
      //_cat.finest("Unsetting between blinds =" + v[i]);
    }
    return sitting_between_blinds;
  }

  public boolean unsetBetweenBlindsDealerToSB() {
    boolean sitting_between_blinds = false;
    if (_dealer == null) {
      return false;
    }
    int startPos = _dealer.pos();
    int endPos = _sbPlayer.pos();
    //_cat.finest("Start=" + startPos + ", endPos=" + endPos);
    PokerPresence v[] = selectGroup(startPos, endPos);
    for (int i = 0; i < v.length; i++) {
      v[i].unsetBetweenBlinds();
      sitting_between_blinds = true;
      //_cat.finest("Unsetting between blinds =" + v[i]);
    }
    return sitting_between_blinds;
  }

  public void checkAndSetForButtonMovement(Presence p) {
    // if the dealer button crosses over set the player as new player
    PokerPresence pp = _g.prev(p.pos());
    if (pp != null) {
      if (pp.isSB() && !p.isMissedBB()) {

        p.setMissedSB();
        //_cat.finest("Sitting next to SB  " + p);
      }
      else if (_g.prev(pp.pos()) != null && _g.prev(pp.pos()).isSB()) {
        p.setMissedBB();
        p.unsetMissedSB();
        //_cat.finest("Sitting next to BB  " + p);
      }
    }
  }

  public void eliminateDisconnectedPlayers() {
    for (int i = 0; i < _players.length; i++) {
      if (_players[i] == null) {
        continue;
      }
      if (_players[i].isDisconnected()) {
        _cat.info("Removing disconnected players " + _players[i]);
        remove(_players[i]);
      }

    }
  }

  public synchronized void removeAll() {
    _size = 0;
    _count = 0;
    _positions = new int[] {};
    _players = null;
  }

  class SelectorImpl
      implements PresenceSelector {

    public void startPos(int pos) {
      this.pos = pos;
    }

    int startPos() {
      return pos;
    }

    public PokerPresence selectPresence(Constraint c) {
      if (_players==null)return null;
      int l = _players.length;
      int e = l;
      int st = startPos() + 1;
      for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
        if (c.satisfy(_players[i])) {
          return _players[i];
        }
      }
      return null;
    }

    public PokerPresence selectPrevPresence(Constraint c) {
      if (_players==null)return null;
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

    public PokerPresence[] select(Constraint c) {
      if (_players==null){
    	  _cat.warning("Presence is null " + this);
    	  _players = new PokerPresence[_size];
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

    private int pos;
  }

  public static void main(String args[]) {
    String plr[] = new String[10];
    plr[0] = "0";
    plr[1] = "1";
    plr[2] = "2";
    plr[3] = "3";
    plr[4] = "4";
    plr[5] = "5";
    plr[6] = "6";
    plr[7] = "7";
    plr[8] = "8";
    plr[9] = "9";

    int start = 5 + 1;
    int end = 2;

    if (start < end) {
      for (int i = start; i < end && plr[i] != null; i++) {
        System.out.println(plr[i]);
      }
    }
    else if (start == end) {
      //return null;
    }
    else if (end < start) {
      for (int i = start; i < plr.length && plr[i] != null; i++) {
        System.out.println(plr[i]);
      }
      for (int i = 0; i < end && plr[i] != null; i++) {
        System.out.println(plr[i]);
      }
    }

  }

}
