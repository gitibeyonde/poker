package com.poker.game.poker.pokerimpl;

import com.agneya.util.Utils;

import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Card;

import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.CollectABResponse;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.GameOverResponse;
import com.poker.game.poker.GameStartResponse;
import com.poker.game.poker.MoveResponse;
import com.poker.game.poker.Poker;
import com.poker.game.poker.Pot;
import com.poker.game.poker.SitInResponse;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;

import java.util.Comparator;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Holdem
    extends Poker {

  // set the category for logging
  static Logger _cat = Logger.getLogger(Holdem.class.getName());

  public synchronized Response start() {
	if(_blindsPosted){
        _cat.info("STARTING " + _inProgress);
	     return null;
	}
    _cat.finest("STARTING " + _inProgress);
    _currentPot = null;
    prepareForNewRun();
    if (reRunCondition()) {
      _inProgress = true;
      return new CollectABResponse(this);
    }
    else {
      _inProgress = false;
      gameCleanup();
      return new SitInResponse(this, 9);
    }
  }

  public Holdem(String name, int minPlayers, int maxPlayers,
                int rake, double[] maxRake, String[] affiliate,
                String[] partner,
                Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
          stateObserver);
    _type = new PokerGameType(PokerGameType.Play_Holdem);
  }

  public Holdem(String name, int minPlayers, int maxPlayers,
                int rake, double maxRake, String[] affiliate, String[] partner,
                Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
          stateObserver);
    _type = new PokerGameType(PokerGameType.Play_Holdem);
  }

  public void setArgs(double minBet, double maxBet,
                      double smallBlind, double bigBlind) {
    this._minBet = minBet;
    this._maxBet = maxBet;
    this.smallBlind = smallBlind;
    this.bigBlind = bigBlind;
    this._maxRounds = 4;
    _cat.finest(this.toString());
  }

  public void prepareForNewRun() {
    setupNewRun();
    _blindsPosted = false;
  }

  public Response abOverResponse() {
    PokerPresence[] ap = activePlayers();
    if (ap == null || ap.length==0) {
      return new GameDetailsResponse(this);
    }
    if (ap.length < 2) {
      //return the ante
      _inProgress = false;

      for (int i = 0; i < ap.length; i++) {
        ap[i].returnRoundBet();
        ap[i].setNew();
      }
      //for (int i = 0; i < _pots.size(); i++) {
      Pot pot = (Pot) _pots.get(0);
      double win[] = Utils.integralDivide(pot.getVal(),
                                          ap.length);
      for (int k = 0; ap != null && k < ap.length; k++) {
        pot.addHighWinners(ap[k], win[k],
                           HandComparator.bestHandOf5(ap[k].getHand().
            getCards(), Hand.getHandFromCardArray(_communityCards.getCards()), type().intVal())[0]);
        ap[k].unsetBroke();
        _cat.finest("Winner = " + ap[k] + " Pot = " + ( (Pot) _pots.get(0)));
      }
      //}
      new GameOverResponse(this); // log winner
      return new GameStartResponse(this);
    }

    _cat.finest("Blinds-over");
    // check if the presence has 0 cards
    PokerPresence pv[] = (PokerPresence[]) activePlayersIncludingAllIns();
    
    if (pv.length >= 1 && pv[0].getHand().cardCount() >= 1){
    	//cards are already dealt
    	new Exception(this.toString()).printStackTrace();
    }
    else {
    	dealCloseCards( pv , 2);
    }
    return new MoveResponse(this);
  }

  public double abValue() {
    return smallBlind;
  }

  public String abString() {
    StringBuilder buf = new StringBuilder().append("small-blind=").append(
        smallBlindVal()).append(",").append("big-blind=").append(bigBlindVal());
    return buf.toString();
  }

  public Response postAnte(Presence p, double amt) {
    throw new IllegalStateException("Ante posted in the game of Holdem");
  }

  public Response bringIn(Presence p, double amt) {
    throw new IllegalStateException("Bring In in the game of Holdem");
  }
  
  @Override
	public Response quickFold(Presence op) {
	  throw new IllegalStateException("Bring In in the game of Holdem");  
  }

  public Response postSBBB(Presence p, double amt) {
    p.deductMoney(smallBlind); //deduct small blind and send it to pot
    _currentPot.addVal(smallBlind);
    post(p, amt - smallBlind, PokerMoves.SBBB);
    p.setABPosted();
    if (abOver()) {
      _cat.finest("Blinds Over");
      return abOverResponse();
    }
    return new CollectABResponse(this);
  }

  public Response postBigBlind(Presence p, double amt) {
    post(p, amt, PokerMoves.BIG_BLIND);
    p.setABPosted();
    _blindsPosted = true;
    if (abOver()) {
      _cat.finest("Blinds Over");
      return abOverResponse();
    }
    return new CollectABResponse(this);
  }

  public Response postSmallBlind(Presence p, double amt) {
    // @todo code to validate. if( moveOK( ) ) then process
    if (p.isMissedSB()) {
      p.deductMoney(amt); //deduct small blind and send it to pot
      _currentPot.addVal(amt);
      p.setABPosted();
    }
    else {
      post(p, amt, PokerMoves.SMALL_BLIND);
      p.setABPosted();
    }
    if (abOver()) {
      _cat.finest("Blinds Over");
      return abOverResponse();
    }
    return new CollectABResponse(this);
  }


  public Response optOut(Presence p) {
    if (!p.isRemoved()) {
      p.lastMove(PokerMoves.OPT_OUT);
    }
    p.setOptOut();
    p.setBetweenBlinds();
    p.setSitOutNextGame();
    p.setSittingOut();
    p.resetRoundBet();
    p.setGameEndWorth();
    if (_currentPot != null) {
      _currentPot.remove(p);
      remove(p);
    }
    PokerPresence[] pl = activePlayers();
    // at time of an optout only eligible are in active state
    if (pl == null) {
      return new GameDetailsResponse(this);
    }
    if (pl != null && pl.length < _minPlayers) {
      // this game cannot be started, return money
      StringBuilder winMesg = new StringBuilder("winner=");
      for (int i = 0; i < pl.length; i++) {
        winMesg.append("main|").append(pl[i].pos()).append("|");
        winMesg.append(pl[i].name()).append("|");
        winMesg.append(pl[i].currentRoundBet()).append("|");
        winMesg.append("||0`");
        pl[i].returnRoundBet();
        pl[i].setNew();
        pl[i].addToWin(pl[i].currentRoundBet()); //****
      }
      // check if a game can be restarted
      postRun(); //****
      _inProgress = false;
      gameCleanup();
      // try restarting the game
      Response r = start();
      r.broadcast(_players, winMesg.deleteCharAt(winMesg.length() - 1).toString());
      postWin(); //****
      return r;
    }
    if (!_blindsPosted) {
      if (p.isSB()) {
        p.setMissedSB();
        if (pl.length == 2) {
          // heads-up
          _table.setDealer();
        }
        _table.setAB();
      }
      else
      if (p.isBB()) {
        if (!p.isNew()) {
          p.setMissedBB();
        }
        _table.setNewBB();
      }
    }
    if (abOver()) {
      return abOverResponse();
    }
    else {
      return new CollectABResponse(this);
    }
  }

  public boolean abOver() {
    return nextForBB() == null && nextForSB() == null;
  }
  
  Card[] _cc = null;

  @Override
  public void initNextBettingRound() {
    _table.commitTotalBet(_players);
    _loops = 0;
    _currentRoundBet = 0;
    _isRoundOpen = true;
    switch (_bettingRound) {
      case 0:
    	_cc = drawCards(5);
    	
		PokerPresence[] v = _currentPot.contenders();
		if (v.length > 1){
			for (int i=0;i<5;i++){
			    // sort the all-in players in descending order of  their hand strength
			    java.util.Arrays.sort(v, new Comparator<PokerPresence>() {
			      public int compare(PokerPresence o1, PokerPresence o2) {
			        return (int) HandComparator.compareGameHand(o2.getHand().getCards(),
			            o1.getHand().getCards(), Hand.getHandFromCardArray(_cc), PokerGameType.HOLDEM, true)[0];
			      }
			    }); 
			    if (v[0].isShill()){
			    	break;
			    }
			    else {
			    	_cc = drawCards(5);
			    }
			}
		}
        _communityCards.addCard(_cc[0]); //flop
        _communityCards.addCard(_cc[1]); //flop
        _communityCards.addCard(_cc[2]); //flop
        _cat.finest("Round =0, CC = " + _communityCards.openStringValue());
        break; // do nothing *flop* cards alread dealt
      case 1:
        _communityCards.addCard(_cc[3]); //turn
        _cat.finest("Round =1, CC = " + _communityCards.openStringValue());
        break;
      case 2:
          _communityCards.addCard(_cc[4]); //river
        _cat.finest("Round =2, CC = " + _communityCards.openStringValue());
        break;
      default:
        _cat.log(Level.WARNING, "Illegal state");
        throw new IllegalStateException(_bettingRound + "");
        //break; // verify if > 4 is error
    }
    ++_bettingRound; // round over ...
  }
  
  @Override
  public void attachWinners(Pot p) {
    PokerPresence[] v = p.contenders();
    if (v.length == 0) {
      _cat.finest("No contenders");
      return;
    }
    if (v.length == 1) {
      p.addHighWinners(v[0], p.getVal(), 0L);
      if (!v[0].isAllIn())v[0].unsetShowdown();
      _cat.finest("Single contenders " + v[0]);
      return;
    }

    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) HandComparator.compareGameHand( ( (PokerPresence) o2).getHand().
            getCards(),
            ( (PokerPresence) o1).getHand().getCards(),
            communityCards(), PokerGameType.HOLDEM, true)[0];
      }
    });

    Vector winner = new Vector();
    v[0].setShowdown();
    winner.add(v[0]);
    for (int i = 0; i < v.length - 1; i++) {
      if (HandComparator.compareGameHand(v[i].getHand().getCards(),
                                         v[i + 1].getHand().getCards(),
                                         communityCards(), PokerGameType.HOLDEM, true)[0] !=
          0L) {
        break;
      }
      else {
        v[i + 1].setShowdown();
        winner.add(v[i + 1]);
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

    _cat.finest("Showdown Position=" + showdownPos());
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
          _cat.finest("Showdown=" + _players[i]);
          high_index = j;
          break;
        }
      }
    }
  }


  public double smallBlindVal() {
    return smallBlind;
  }

  public double bigBlindVal() {
    return bigBlind;
  }

  // current bet value
  double bigBlind;
  double smallBlind;
  boolean _blindsPosted;

}
