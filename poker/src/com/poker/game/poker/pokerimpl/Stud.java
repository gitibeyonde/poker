package com.poker.game.poker.pokerimpl;

import com.agneya.util.LongOps;
import com.agneya.util.Utils;

import com.golconda.game.Presence;
import com.golconda.game.resp.Response;

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
import java.util.logging.Logger;


public class Stud
    extends Poker {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Stud.class.getName());

  public Response start() {
    _cat.finest("STARTING");
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

  public Stud(String name, int minPlayers, int maxPlayers,
              int rake, double[] maxRake, String[] affiliate, String[] partner,
              Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
          stateObserver);
    _type = new PokerGameType(PokerGameType.Play_Stud);
  }

  public Stud(String name, int minPlayers, int maxPlayers,
              int rake, double maxRake, String[] affiliate, String[] partner,
              Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
          stateObserver);
    _type = new PokerGameType(PokerGameType.Play_Stud);
  }

  public void setArgs(double minBet, double maxBet, double ante,
                      double bringin) {
    this._minBet = minBet;
    this._maxBet = maxBet;
    this.ante = ante;
    this.bringin = bringin;
    this._maxRounds = 5;
    _cat.finest(this.toString());
  }

  public void prepareForNewRun() {
    _pair_hand=false;
    setupNewRun();
    // all the active players whould post ante
    _table.markEligiblesForAnte();
  }

  public Response abOverResponse() {
    _cat.finest("Ante-over");
    PokerPresence[] ap = (PokerPresence [])activePlayers();
    if (ap == null || ap.length==0) {
      return new GameDetailsResponse(this);
    }
    if (ap.length < _minPlayers) {
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
    // create the pot after ante's are over
    // antes are collected by the house
    procRoundOver();
    dealCloseCards( (PokerPresence[]) activePlayersForCards(), 2); // 2 face down cards
    dealOpenCards( (PokerPresence[]) activePlayersForCards(), 1); // 1 face up cards-door card/third street
    /**
     * Find out the player with lowest open card he will do a bring in.
     */

    int lowest_card_index = 0;
    int low = Hand.cardStrength(ap[0].getHand().getOpenCards());
    for (int i = 0; i < ap.length - 1; i++) {
      int curr = Hand.cardStrength(ap[i + 1].getHand().getOpenCards());
      if (curr < low) {
        lowest_card_index = i + 1;
        low = curr;
      }
      _cat.info(ap[i].name() + "=" + ap[i].getHand().cardCount() + " , " +
                ap[i].getHand().stringValue());
      _cat.info("open =" + ap[i].getHand().getOpenCards());
    }

    /**
     * Set the marker to the player previous to the player with lowest open card
     */
    //setMarker(_table.prev(ap[lowest_card_index].pos()));
    _stud_marker = ap[lowest_card_index];
    return new MoveResponse(this);
  }

  public double abValue() {
    return ante;
  }

  public String abString() {
    StringBuilder buf = new StringBuilder().append("ante=").append(anteVal());
    return buf.toString();
  }

  public Response postAnte(Presence p, double amt) {
    // @todo code to validate. if( moveOK( ) ) then process( )
    //p.active( true ) ;
    post(p, amt, PokerMoves.ANTE);
    p.setABPosted();
    if (abOver()) {
      // check if all the antes have been posted
      return abOverResponse();
    }
    return new CollectABResponse(this);
  }

  public Response bringIn(Presence p, double amt) {
    return bettingResponse(p, amt, PokerMoves.BRINGIN);
  }


  public Response postSBBB(Presence p, double amt) {
    throw new IllegalStateException(
        "SBBB posted in the game of Seven Card Stud");
  }

  public Response postSmallBlind(Presence p, double amt) {
    throw new IllegalStateException(
        "Small blind posted in the game of Seven Card Stud");
  }

  public Response postBigBlind(Presence p, double amt) {
    throw new IllegalStateException(
        "Big blind posted in the game of Seven Card Stud");
  }
  
  @Override
	public Response quickFold(Presence op) {
	  throw new IllegalStateException("Bring In in the game of Holdem");  
  }

  public Response optOut(Presence p) {
    if (!p.isRemoved()) {
      p.lastMove(PokerMoves.OPT_OUT);
    }
    p.setOptOut();
    p.setSitOutNextGame();
    p.setSittingOut();
    if (_currentPot != null) {
      _currentPot.remove(p);
    }
    PokerPresence[] pl = activePlayers();
    // at time of an optout only eligible are in active state
    if (pl.length < _minPlayers) {
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
    if (abOver()) {
      return abOverResponse();
    }
    return new CollectABResponse(this);
  }

  public void initNextBettingRound() {
    //currentPot.addToWorth(currentRoundBet - rake);
    _table.commitTotalBet(_players);
    _loops = 0;
    _currentRoundBet = 0;
    _isRoundOpen = true;
    PokerPresence[] ap = activePlayersForCards();
    switch (_bettingRound) {
      case 0: // fourth street
        dealOpenCards(ap, 1);
        break; // do nothing *flop* cards already dealt
      case 1: // fifth street
        dealOpenCards(ap, 1);
        break;
      case 2: // sixth street

        // There is a possibility that the deck might get exhausted in the final round
        // In case the deck has less cards then number of players then deal a community card
        if (_deck.cardsRemaining() <= ap.length) {
        	_communityCards.addCards(drawCards(1));
        	Hand.getHandFromCardArray(_communityCards.getCards());
        }
        else {
          dealOpenCards(ap, 1);
        }
        break;
      case 3: // seventh street
        if (_deck.cardsRemaining() <= ap.length) {
        	_communityCards.addCards(drawCards(1));
        	Hand.getHandFromCardArray(_communityCards.getCards());
        }
        else {
          dealCloseCards(ap, 1);
        }
        break;
      default:
        throw new IllegalStateException(_bettingRound + "");
    }

    /**
     * Find out the player with highest open card he will do a bring in.
     */
    ap = activePlayers();
    if (ap.length > 1) {
      int highest_card_index = 0;
      for (int i = 0; i < ap.length - 1; i++) {
        if (Hand.compareStudOpenCards(ap[highest_card_index].getHand().
                                      getOpenCards(),
                                      ap[i + 1].getHand().getOpenCards()) == -1) {
          highest_card_index = i + 1;
        }
        _cat.info(ap[i].name() + "=" + ap[i].getHand().cardCount() + " , " +
                  ap[i].getHand().stringValue());
        _cat.info("open =" + ap[i].getHand().getOpenCards());
      }

      /**
       * Set the marker to the player previous to the player with lowest open card
       */
      //setMarker(_table.prev(ap[highest_card_index].pos()));
      _stud_marker = ap[highest_card_index];
    }
    ++_bettingRound; // round over ...
  }

  public boolean abOver() {
    return nextForAnte() == null;
  }

  public void attachWinners(Pot p) {
    PokerPresence[] v = p.contenders();
    if (v.length == 0) {
      return;
    }
    if (v.length == 1) {
      p.addHighWinners(v[0], p.getVal(), 0L);
      if (!v[0].isAllIn())v[0].setShowdown();
      _cat.finest("Single contenders " + v[0]);
      return;
    }

    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) HandComparator.compareGameHand( ( (PokerPresence) o2).getHand().
            getCards(),
            ( (PokerPresence) o1).getHand().getCards(),
            communityCards(), PokerGameType.STUDHI, true)[0];
      }
    });

    Vector winner = new Vector();
    v[0].setShowdown();
    winner.add(v[0]);
    for (int i = 0; i < v.length - 1; i++) {
      if (HandComparator.compareGameHand(v[i].getHand().getCards(),
                                         v[i + 1].getHand().getCards(),
                                         communityCards(), PokerGameType.STUDHI, true)[0] !=
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

    int l = _players.length;
    int st = 0;
    int e = l;
    for (int i = 0; i < l; i++) {
      if ( ( (PokerPresence) _players[i]).pos() == showdownPos()) {
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
          high_index = j;
          break;
        }
      }
    }
  }

  public double anteVal() {
    return ante;
  }
  public double binginVal() {
    return bringin;
  }


  public PokerPresence studMarker() {
    return _stud_marker == null ? this.nextActiveToDealer() : _stud_marker;
  }

  // current bet value
  double ante, bringin;
  public boolean _pair_hand=false;
  PokerPresence _stud_marker = null;
}
