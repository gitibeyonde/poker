package com.poker.game.poker;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;
import com.agneya.util.Utils;

import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;
import com.golconda.game.PlayerStatus;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;

import com.poker.common.interfaces.SitnGoInterface;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.pokerimpl.Holdem;
import com.poker.game.poker.pokerimpl.HoldemTourny;
import com.poker.game.poker.pokerimpl.OmahaHi;
import com.poker.game.poker.pokerimpl.Stud;
import com.poker.game.poker.pokerimpl.TermHoldem;
import com.poker.game.poker.Pot.PotWinner;
import com.poker.game.util.CardUtils;
import com.poker.game.util.HandOps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


//import com.poker.common.db.GameRunLog;


public class PokerResponse
    implements Response {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(PokerResponse.class.getName());
  transient static Auditor _auditor;
  static Configuration _conf;
  static boolean _logPlayGames = false;

  public PokerResponse(Poker g) {
    _g = g;
    int pos = 0;
    _allPlayers = (PokerPresence[]) g.allPlayers(pos);
    _observers = g.observers();
    _responseId = g.responseId();
    _broadcast = "";
    ++_msgGID;
    try {
      _conf = Configuration.instance();
      _logPlayGames = _conf.getBoolean("Auditor.Log.PlayGame");
    }
    catch (ConfigurationException e) {
      _cat.log(Level.WARNING, "Configuration exception ", e);
    }
    _auditor  = Auditor.instance();
  }

  /***
   * Response interface implementation
   */

  public Game getGame() {
    return _g;
  }

  public void addRecepient(Presence p) {
    _map.put(p, "no-command-set-yet");
  }

  public void addRecepients(Presence[] p) {
    for (int k = p.length; --k > -1; ) {
      _map.put(p[k], "no-command-set-yet");
    }
  }

  public void addObservers(Presence[] observers) {
    for (int j = 0; j < observers.length; j++) {
      _observers.add(observers[j]);
    }
  }

  public PokerPresence[] observers() {
    PokerPresence[] pl = new PokerPresence[_observers.size()];
    return (PokerPresence[]) _observers.toArray(pl);
  }

  public PokerPresence removeRecepient(Presence p) {
    return (PokerPresence) _map.remove(p);
  }

  public boolean recepientExists(Presence p) {
    return _map.containsKey(p);
  }

  public void setResponse(String command) {
    _response = command;
  }

  public void broadcast(Presence[] p, String command) {
    //Object v ;
    _broadcast += command;
    if (p == null) {
      return;
    }
    for (int k = p.length; --k > -1; ) {
      if (_map.containsKey(p[k])) {
        _map.put(p[k], command + "," + _map.get(p[k])); // string concat ?
      }
      else {
        _map.put(p[k], command);
      }
    }
  }

  public void setCommand(Presence p, String command) {
    if (_map.containsKey(p)) {
      command = _map.get(p) + "," + command;
    }
    _map.put(p, command);
  }

  public String getBroadcast() {
    return _broadcast; //...
    // revisit later
  }

  public String getCommand(Presence p) {
    return (String) _map.get(p);
  }

  public PokerPresence[] recepients() {
    PokerPresence[] pl = new PokerPresence[_map.size()]; // optimistic size prediction
    return (PokerPresence[]) _map.keySet().toArray(pl);
  }

  /***
   * Response interface implementation ends
   */

  public void logStart() {
    StringBuilder xmlLog = new StringBuilder();

    if (_g.type().isReal() || _g.type().isTourny() || _logPlayGames) {
      xmlLog.append("<hand  dealer=\"").append(_g.dealer().pos());
      xmlLog.append("\">\n");
      xmlLog.append("<ts value=\"").append(new java.sql.Timestamp(System.
          currentTimeMillis()).toString());
      xmlLog.append("\"/>\n");
      xmlLog.append(playerDetailsForAuditXML().toString());
      xmlLog.append("</hand>");

      _auditor.write(_g.name(), _g.grid(), xmlLog.toString(), false);
    }
  }

  public void log() {
    StringBuilder xmlLog = new StringBuilder();

    if (_g.type().isReal() || _g.type().isTourny() || _logPlayGames) {
      xmlLog.append("<hand comm=\"").append(_g.communityCardsString());
      xmlLog.append("\"  rake=\"").append(_g.rake());
      xmlLog.append("\"  dealer=\"").append(_g.dealer().pos());
      xmlLog.append("\">\n");
      xmlLog.append("<ts value=\"").append(new java.sql.Timestamp(System.
          currentTimeMillis()).toString());
      xmlLog.append("\"/>\n");

      xmlLog.append("<last-move value=\"").append(cache_lastMove == null ? "," :
                                                  cache_lastMove.substring(10));
      xmlLog.append("\"/>\n");

      /**xmlLog.append("<next-move value=\"").append(cache_nextMove == null ? "," :
                                                 cache_nextMove.substring(10));
             xmlLog.append("\"/>");**/

      xmlLog.append(_g._pots.size() == 0 ? "" : potDetailsXML().toString());
      xmlLog.append(playerDetailsForAuditXML().toString());
      xmlLog.append("</hand>");

      _auditor.write(_g.name(), _g.grid(), xmlLog.toString(), false);
    }
  }

  protected void logGameOver() {
    if (_g.type().isReal() || _g.type().isTourny() || _logPlayGames) {
      _auditor.write(_g.name(), _g.grid(), winnersDetailXML().toString(), true);
    }
  }

  /*
    Enhances the command string with information that the poker normally
    would not either have or concern itself with.
   Interface Enahncer has method enhance( String command, Player invokee, Game g )
   */
  public String enhanceCommand(String command, PokerPresence p,
                               Game g /*Enhancer e*/) {
    return null; // e.enhance( command, invokee, g )
  }

  protected StringBuilder header() {
    StringBuilder buf = new StringBuilder().append("grid=").append(_g.grid()).append(",");
    buf.append("affiliate=").append(_g.affiliateString()).append(",");
    buf.append("partners=").append(_g.partnerString()).append(",");
    buf.append("response-id=").append(_responseId).append(",");
    buf.append("msgGID=").append(_msgGID).append(",");
    buf.append("name=").append(_g.name()).append(",");
    buf.append("type=").append(_g.type().intVal()).append(",");
    buf.append("stack=").append(_g.getStack()).append(",");
    //buf.append("rake=").append(Utils.getRoundedString(_g.rake())).append(",");
    buf.append("max-players=").append(_g.maxPlayers()).append(",");
    buf.append("min-players=").append(_g.minPlayers()).append(",");
    buf.append("average-pot=").append( Utils.getRoundedDollarCent(_g.averagePot())).append(",");
    buf.append("flop=").append(_g.flopPlayers()).append(",");
    buf.append("hands-hour=").append(_g.numHandsPerHour()).append(",");
    buf.append("max-rounds=").append(_g.maxRounds()).append(",");
    buf.append("running=").append(_g.isRunning()).append(",");
    //buf.append("rank=").append(_g.rank()).append(",");
    if (_g.type().isSitnGo()) {
      buf.append("limit=").append( ( ( 
      SitnGoInterface) _g).limit()).append(",");
      buf.append("buyin=").append(Utils.getRoundedDollarCent( ( (SitnGoInterface) _g).buyIn())).append(",");
      buf.append("fees=").append(Utils.getRoundedDollarCent( ( (SitnGoInterface)_g).fees())).append(",");
      buf.append("chips=").append(Utils.getRoundedDollarCent( ( (SitnGoInterface)_g).chips())).append(",");
      buf.append("state=").append( ( (SitnGoInterface) _g).state()).append(",");
      buf.append("level=").append( ( (SitnGoInterface) _g).level()).append(",");
      buf.append("tournyid=").append( ( (SitnGoInterface) _g).tournyId()).append(",");
    }
    else if (_g.type().isTPoker()){ 
    	buf.append("bench=").append( ( ( 
    	      TermHoldem) _g)._isBench).append(",");
    }
    buf.append("ab=").append(Utils.getRoundedDollarCent(_g.abValue())).append(",");
    buf.append("min-bet=").append(Utils.getRoundedDollarCent(_g.minBet())).append(",");
    buf.append("max-bet=").append(Utils.getRoundedDollarCent(_g.maxBet())).append(",");

    // append blind/ante
    buf.append(_g.abString()).append(",");
    return buf;
  }

  protected StringBuilder miniHeader() {
    StringBuilder buf = new StringBuilder().append("grid=").append(_g.grid()).append(",");
      buf.append("name=").append(_g.name()).append(",");
    buf.append("type=").append(_g.type().intVal()).append(",");
    buf.append("response-id=").append(_responseId).append(",");
    buf.append("msgGID=").append(_msgGID).append(",");
    buf.append("rake=").append(Utils.getRoundedString(_g.rake())).append(",");
    buf.append("ab=").append(Utils.getRoundedDollarCent(_g.abValue())).append(
        ",");
    buf.append("min-bet=").
        append(Utils.getRoundedDollarCent(_g.minBet())).append(",").append(
            "max-bet=").append(Utils.getRoundedDollarCent(_g.maxBet())).
        append(",");
    buf.append("max-players=").append(_g.maxPlayers()).append(",");
    buf.append("min-players=").append(_g.minPlayers()).append(",");
    buf.append("partners=").append(_g.partnerString()).append(",");
    if (_g.type().isSitnGo()) {
        buf.append("tournyid=").append( ( (SitnGoInterface) _g).tournyId()).append(",");
      }
    // append blind/ante
    /**buf.append(_g.abString()).append(",");**/
    return buf;
  }

  protected StringBuilder registeredPlayerDetail() {
    if (_g instanceof HoldemTourny) {
      HoldemTourny ht = (HoldemTourny) _g;
      Iterator rp = ht.invited().iterator();
      StringBuilder buf = new StringBuilder();
      buf.append("registered-player=");
      while (rp.hasNext()) {
        buf.append(rp.next()).append("|");
      }
      buf.deleteCharAt(buf.length() - 1);
      return buf.append(",");
    }
    else {
      return new StringBuilder();
    }
  }

  protected StringBuilder playerHandDetails(PokerPresence p) {
    StringBuilder buf = new StringBuilder();
    //if (!_g.type().isTeenPatti() || _g.type().isTeenPatti() && p.isSeen()) {
    if (p.getHand().cardCount() > 0) {
      buf.append("hand=").append(p.getHand().getAllCardsString());
      buf.append(",");
    }
    //}
    return buf;
  }

  protected StringBuilder playerTargetPosition(PokerPresence p) {
    StringBuilder buf = new StringBuilder();
    buf.append("target-position=").append(p.pos()).append(",");
    return buf;
  }

  protected StringBuilder playerDetails() {
    StringBuilder buf = new StringBuilder();
    int len = _allPlayers.length;
    if (len == 0) {
      return buf;
    }
    buf.append("player-details=");
    for (int i = 0; i < len; i++) {
      if (_allPlayers[i] != null) {
        PokerPresence pp = (PokerPresence) _allPlayers[i];
        buf.append(pp.pos()).append("|");
        buf.append(pp.netWorthString()).append("|");
        buf.append(pp.currentRoundBetRoundedString()).append("|");
        buf.append(pp.name()).append("|");
        buf.append(pp.status()).append("|");
        buf.append(pp.gender()).append("|");
        buf.append(pp.rank()).append("|");
        buf.append(pp.avatar()).append("|");
        buf.append(pp.city()).append("|");
        buf.append(pp.getHand().getCardsString());
        buf.append("`");
      }
    }
    buf.deleteCharAt(buf.length() - 1).append(",");
    buf.append("waiters=");
    for (ListIterator li = _g._waiters.listIterator(); li.hasNext(); ) {
      PokerPresence p = (PokerPresence) li.next();
      buf.append(p.name()).append("`");
    }
    return buf.deleteCharAt(buf.length() - 1).append(",");
  }

  protected StringBuilder playerDetails_() {
    StringBuilder buf = new StringBuilder();
    int len = _allPlayers.length;
    if (len == 0) {
      return buf;
    }
    buf.append("player-details=");
    for (int i = 0; i < len; i++) {
      if (_allPlayers[i] != null) {
        PokerPresence pp = (PokerPresence) _allPlayers[i];
        buf.append(pp.pos()).append("|");
        buf.append(pp.netWorthString()).append("|");
        buf.append(pp.currentRoundBetRoundedString()).append("|");
        buf.append(pp.name()).append("|");
        buf.append(pp.status()).append("|");
        buf.append(pp.gender()).append("|");
        buf.append(pp.rank()).append("|");
        buf.append(pp.avatar()).append("|");
        buf.append(pp.city()).append("|");
        buf.append(pp.getHand().getAllCardsString());
        buf.append("`");
      }
    }
    buf.deleteCharAt(buf.length() - 1).append(",");
    buf.append("waiters=");
    for (ListIterator li = _g._waiters.listIterator(); li.hasNext(); ) {
      PokerPresence p = (PokerPresence) li.next();
      buf.append(p.name()).append("`");
    }
    return buf.deleteCharAt(buf.length() - 1).append(",");
  }

  protected StringBuilder playerDetailsForAuditXML() {
    StringBuilder buf = new StringBuilder();
    int len = _allPlayers.length;
    if (len == 0) {
      return buf;
    }
    for (int i = 0; i < len; i++) {
      if (_allPlayers[i] != null) {
        PokerPresence pp = (PokerPresence) _allPlayers[i];
        buf.append("<player name=\"").append(pp.name());
        buf.append("\" pos=\"").append(pp.pos());
        buf.append("\" worth=\"").append(pp.netWorthString());
        buf.append("\" bet=\"").append(pp.currentRoundBetRoundedString());
        buf.append("\" hand=\"").append(pp.getHand().getAllCardsString());
        buf.append("\" status=\"").append(PlayerStatus.stringValue(pp.status()));
        buf.append("\"/>\n");
      }
    }
    return buf;
  }

// based on current.
  protected StringBuilder lastMoveDetails() {
    if (_g.current() != null && _g.current().isLastMove()) {
      StringBuilder buf = new StringBuilder().append("last-move=").append(_g.
          current().pos()).append("|").append(_g.current().name()).append("|").
          append(new PokerMoves(_g.current().lastMove()).stringValue()).append("|");
      if (_g.current().lastMove() == PokerMoves.JOIN) {
        buf.append(Utils.getRoundedString(_g.current().getAmtAtTable()));
      }
      else if (_g.current().lastMove() == PokerMoves.OPT_OUT ||
               _g.current().lastMove() == PokerMoves.LEAVE ||
               _g.current().lastMove() == PokerMoves.NONE) {
        buf.append("0.00");
      }
      else {
        buf.append(_g.current().betValueString());
      }
      cache_lastMove = buf;
      return buf.append(",");
    }
    else {
      return new StringBuilder("");
    }
  }

  protected StringBuilder nextMoveJoin(PokerPresence p, int pos) {
    StringBuilder buf = new StringBuilder("next-move=");
    p.setPos(pos);
    setBet(buf, pos, PokerMoves.JOIN, 10 * _g.minBet());
    setBet(buf, pos, PokerMoves.CANCEL, 0);
    buf.deleteCharAt(buf.length() - 1);
    buf.append(",");
    return buf;
  }

  protected StringBuilder abDetails() {
    try {
      _g._nextMove = 0;
      StringBuilder buf = new StringBuilder();
      //_cat.finest("ABDetails for game type = " + _g.type());
      _g._blinds_over_toggle = Poker.BLINDS;
      buf.append("next-move=");
      
      
      if (_g.type().isHoldem()) {
        Holdem dm = (Holdem) _g;
        if (_g.nextForSB() != null) {
          _g._nextPlayer = _g.nextForSB();

          if (_g._nextPlayer != null && _g._nextPlayer.isRemoved()) { //blind player leaves
            PokerPresence[] pl = _g._table.eligiblePlayers();
            // at time of an optout only eligible are in active state
            if (pl.length < _g._minPlayers) {
              /**_runAgain =**/
              _g._inProgress = false;
              for (int i = 0; i < pl.length; i++) {
                //_cat.finest("Current Round Bet =" + pl[i].currentRoundBet() +
                          // ", Amt at table =" + pl[i].getAmtAtTable());
                pl[i].returnRoundBet();
                pl[i].setNew();
              }
              _g.update(_g, GameStateEvent.GAME_OVER);
              return new StringBuilder("next-move=-1|wait|-21,");
            }
            _g._table.setAB();
            _g._nextPlayer = _g.nextForSB();
          } // end blind player removed
          if (_g.type().isTourny() &&
              _g._nextPlayer.getAmtAtTable() < dm.smallBlindVal()) {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.ALL_IN,
                   _g._nextPlayer.getAmtAtTable());
          }
          else {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.SMALL_BLIND,
                   dm.smallBlindVal());
          }
        }
        else if (dm.nextForBB() != null) {
          _g._nextPlayer = dm.nextForBB();
          //_cat.finest("Next for BB = " + _g._nextPlayer);
          if (_g._nextPlayer.isRemoved()) { //blind player leaves
            PokerPresence[] pl = _g._table.eligiblePlayers();
            // at time of an optout only eligible are in active state
            if (pl.length < _g._minPlayers) {
              /**_runAgain =**/
              _g._inProgress = false;
              for (int i = 0; i < pl.length; i++) {
                //_cat.finest("Current Round Bet =" + pl[i].currentRoundBet() +
                           //", Amt at table =" + pl[i].getAmtAtTable());
                pl[i].returnRoundBet();
                pl[i].setNew();
              }
              _g.update(_g, GameStateEvent.GAME_OVER);
              return new StringBuilder("next-move=-1|wait|-22,");
            }
            _g._table.setNewBB();
            _g._nextPlayer = dm.nextForBB();
          } // end blind player removed
          if (_g._nextPlayer.isMissedBB()) {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.SBBB,
                   dm.bigBlindVal() + dm.smallBlindVal());
          }
          else if (_g._nextPlayer.isMissedSB()) {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.SMALL_BLIND,
                   dm.smallBlindVal());
          }
          else if (_g._nextPlayer.isBB()) {
            if (_g.type().isTourny() &&
                _g._nextPlayer.getAmtAtTable() < dm.bigBlindVal()) {
              setBet(buf, _g._nextPlayer.pos(), PokerMoves.ALL_IN,
                     _g._nextPlayer.getAmtAtTable());
            }
            else {
              setBet(buf, _g._nextPlayer.pos(), PokerMoves.BIG_BLIND,
                     dm.bigBlindVal());
            }
          }
          else {
            _cat.log(Level.WARNING, "No BB " + _g._nextPlayer);
            throw new IllegalStateException("Blind player " + _g._nextPlayer);
          }
        }
      }
      
      
      else if (_g.type().isOmaha()) {
        OmahaHi oh = (OmahaHi) _g;
        if (_g.nextForSB() != null) {
          _g._nextPlayer = _g.nextForSB();
          if (_g._nextPlayer != null && _g._nextPlayer.isRemoved()) { //blind player leaves
            PokerPresence[] pl = _g._table.eligiblePlayers();
            // at time of an optout only eligible are in active state
            if (pl.length < _g._minPlayers) {
              /**_runAgain =**/
              _g._inProgress = false;
              for (int i = 0; i < pl.length; i++) {
                //_cat.finest("Current Round Bet =" + pl[i].currentRoundBet() +
                          // ", Amt at table =" + pl[i].getAmtAtTable());
                pl[i].returnRoundBet();
                pl[i].setNew();
              }
              _g.update(_g, GameStateEvent.GAME_OVER);
              return new StringBuilder("next-move=-1|wait|-21,");
            }
            _g._table.setAB();
            _g._nextPlayer = _g.nextForSB();
          } // end blind player removed
          if (_g.type().isTourny() &&
              _g._nextPlayer.getAmtAtTable() < oh.smallBlindVal()) {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.ALL_IN,
                   _g._nextPlayer.getAmtAtTable());
          }
          else {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.SMALL_BLIND,
                   oh.smallBlindVal());
          }
        }
        else if (oh.nextForBB() != null) {
          _g._nextPlayer = oh.nextForBB();
          //_cat.finest("Next for BB = " + _g._nextPlayer);
          if (_g._nextPlayer.isRemoved()) { //blind player leaves
            PokerPresence[] pl = _g._table.eligiblePlayers();
            // at time of an optout only eligible are in active state
            if (pl.length < _g._minPlayers) {
              /**_runAgain =**/
              _g._inProgress = false;
              for (int i = 0; i < pl.length; i++) {
                //_cat.finest("Current Round Bet =" + pl[i].currentRoundBet() +
                          // ", Amt at table =" + pl[i].getAmtAtTable());
                pl[i].returnRoundBet();
                pl[i].setNew();
              }
              _g.update(_g, GameStateEvent.GAME_OVER);
              return new StringBuilder("next-move=-1|wait|-22,");
            }
            _g._table.setNewBB();
            _g._nextPlayer = oh.nextForBB();
          } // end blind player removed
          if (_g._nextPlayer.isMissedBB()) {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.SBBB,
                   oh.bigBlindVal() + oh.smallBlindVal());
          }
          else if (_g._nextPlayer.isMissedSB()) {
            setBet(buf, _g._nextPlayer.pos(), PokerMoves.SMALL_BLIND,
                   oh.smallBlindVal());
          }
          else if (_g._nextPlayer.isBB()) {
            if (_g.type().isTourny() &&
                _g._nextPlayer.getAmtAtTable() < oh.bigBlindVal()) {
              setBet(buf, _g._nextPlayer.pos(), PokerMoves.ALL_IN,
                     _g._nextPlayer.getAmtAtTable());
            }
            else {
              setBet(buf, _g._nextPlayer.pos(), PokerMoves.BIG_BLIND,
                     oh.bigBlindVal());
            }
          }
        }
        else {
          _cat.log(Level.WARNING, "No BB " + _g._nextPlayer);
          throw new IllegalStateException("Blind player " + _g._nextPlayer);
        }
      }
      
      
      else if (_g.type().isStud()) {
        Stud ss = (Stud) _g;
        _g._nextPlayer = ss.nextForAnte();
        if (_g._nextPlayer == null || _g._nextPlayer.isRemoved()) { //blind player leaves
          PokerPresence[] pl = _g._table.eligiblePlayers();
          // at time of an optout only eligible are in active state
          if (pl.length < _g._minPlayers) {
            /**_runAgain =**/
            _g._inProgress = false;
            for (int i = 0; i < pl.length; i++) {
              //_cat.finest("Current Round Bet =" + pl[i].currentRoundBet() +
                        // ", Amt at table =" + pl[i].getAmtAtTable());
              pl[i].returnRoundBet();
              pl[i].setNew();
            }
            _g.update(_g, GameStateEvent.GAME_OVER);
            return new StringBuilder("next-move=-1|wait|-21,");
          }
          else {
            return nextMoveDetails();
          }
        } //blind player leaves
        setBet(buf, _g._nextPlayer.pos(), PokerMoves.ANTE, ss.anteVal());
      }

           
        
      /**
       * For tourny there is no Opt-Out
       */
      if (!_g.type().isTourny() && !_g.type().isTPoker()) {
        if (_g._nextPlayer != null){
          setBet(buf, _g._nextPlayer.pos(), PokerMoves.OPT_OUT, 0.0);
        }
      }
      else if (_g._nextMove == PokerMoves.ALL_IN){
            _g._nextMovePlayer.unsetSB();
            _g._nextMovePlayer.unsetBB();
      }
      else {
        _g._nextPlayer.setSingleMove();
      }
      buf.deleteCharAt(buf.length() - 1);
      buf.append(",");
      cache_ab = buf;
      return buf;
    }

    catch (Exception e) {
      _cat.log(Level.WARNING, "Exception during AB ");
      e.printStackTrace();
      return new StringBuilder("next-move=-1|wait|-3,");
    }
  }

    

// @todo : move logic to poker object
// based on marker
  protected StringBuilder nextMoveDetails() {
    if (_g._blinds_over_toggle == Poker.BLINDS && _g.abOver()) {
      _g._blinds_over_toggle = Poker.BLINDS_TRANSITION;
    }

    // NEXT MOVE
    // find the next move player
    if (_g._loops == 0 && _g.currentBet() == 0 && _g.dealer() != null &&
        _g._isRoundOver && _g._bettingRound > 0) {
      _cat.finest("New betting round started ");
      _g._isRoundOver = false; // as next round has started
      // if it is stud game then next move goes to highest card holder
      if (_g.type().isStud()) {
        _g._nextPlayer = ( (Stud) _g).studMarker();
      }
      else {
        _g._nextPlayer = _g.nextActiveToDealer();
      }
    }
    else if (_g._blinds_over_toggle == Poker.BLINDS_TRANSITION) {
      // if it is stud game then next move goes to highest card holder
      if (_g.type().isStud()) {
        _g._nextPlayer = ( (Stud) _g).studMarker();
      }
      else {
        _g._nextPlayer = _g.nextActiveToBB();
      }
      _cat.info("BLINDS TRANSITION " + _g._nextPlayer);
    }
    else {
      _g._nextPlayer = _g.nextActive();
    }
    
    _cat.finest("NMP=" + _g._nextPlayer);

    if (_g._nextPlayer == null) {
      cache_nextMove = new StringBuilder("next-move=-1|wait|-1,");
      return cache_nextMove;
    }
    else if (_g._inProgress == false) {
      cache_nextMove = new StringBuilder("next-move=-1|wait|-2,");
      return cache_nextMove;
    }

    _g._nextMove = 0;

    if (!_g.abOver()) {
      cache_nextMove = abDetails();
      return cache_nextMove;
    }
      
    StringBuilder buf = new StringBuilder();

    if (_g.type().isStud()) {
      buf.append("marker=").append( ( (Stud) _g).studMarker().pos());
    }
    else {
      buf.append("marker=").append(_g.nextActiveToDealer().pos());
    }

    buf.append(",next-move=");
    PokerPresence movePlayer = (PokerPresence)_g._nextPlayer;
    _cat.finest("Next PLAYER = " + movePlayer);
    int pos = movePlayer.pos();
    double pW = movePlayer.netWorth();
    int apc = _g.activePlayersForMove(_g.dealer().pos()).length;

    /**
     * Check which round is it ? and then fix the bet
     */
    double bet_allowed;
    boolean fold_allowed = true;

    if (_g._bettingRound > 1 && _g.maxBet() > 0) {
      bet_allowed = _g.maxBet();
    }
    else {
        bet_allowed = _g.minBet();
    }
    _cat.finest(movePlayer.name() + ", Bet allowed = " + bet_allowed + ", loops=" +_g._loops +" , bet round=" + _g._bettingRound + " Game CRB = " +_g.currentBet());

    /**
     * CHECK AND BET
     */
    if (_g._isRoundOpen || (_g._loops == 0 && _g.currentBet() == 0)) {
      /**
       * This is a fresh loop as currentRoundBet equals zero
       * What if every one has check in the previous round
       */
      _cat.finest("Fresh round check and bet allowed " + _g.type());
      if (_g.type().isStud() && _g._bettingRound == 0) {
        //bring in
        setBet(buf, pos, PokerMoves.BRINGIN, ((Stud)_g).binginVal());
        setBet(buf, pos, PokerMoves.COMPLETE, bet_allowed);
        fold_allowed = false;
      }
      else {
        setBet(buf, pos, PokerMoves.CHECK, 0.0);
        if (pW >= bet_allowed) {
          double maxBet;
          if (_g.maxBet() == _g.POT_LIMIT) {
            double maxPossible = _g.currentPot() +
                _g.currentBet();
            if (pW >= maxPossible) {
              setBet(buf, pos, PokerMoves.BET, bet_allowed, maxPossible);
              setBet(buf, pos, PokerMoves.BET_POT, maxPossible);
            }
            else {
              setBet(buf, pos, PokerMoves.BET, bet_allowed, pW);
              setBet(buf, pos, PokerMoves.ALL_IN, pW);
            }
          }
          else if (_g.maxBet() == _g.NO_LIMIT) {
            maxBet = pW;
            setBet(buf, pos, PokerMoves.BET, bet_allowed, maxBet);
            setBet(buf, pos, PokerMoves.ALL_IN, maxBet);
          }
          else {
            maxBet = 2 * bet_allowed;
            // for stud if the player has a pair, the show raise also
            if (_g.type().isStudHi() && _g._bettingRound == 1) {
              int pair = HandOps.onePair(movePlayer.getHand().getOpenCards());
              if (pair > 0 && pW > maxBet) {
                // the player has a pair
                setBet(buf, pos, PokerMoves.BET, bet_allowed, maxBet);
                ((Stud)_g)._pair_hand=true;
              }
              else {
                setBet(buf, pos, PokerMoves.BET, bet_allowed);
              }
            }
            else {
              setBet(buf, pos, PokerMoves.BET, bet_allowed);
            }
          }
        }
        else if (pW > 0) {
          setBet(buf, pos, PokerMoves.ALL_IN, pW);
        }
      }
    }
    else { // SECOND LOOP
      // CALL AND RAISE
      PokerPresence[] apiallin = _g.activePlayersIncludingAllIns();
      _g._betValue = 0;
      for (int i = 0; i < apiallin.length; i++) {
        if (_g._betValue < apiallin[i].currentRoundBet()) {
          _g._betValue = apiallin[i].currentRoundBet();
          _cat.finest("Player List " + apiallin[i]);
        }
      }

      _cat.finest("max_bet_on_table = " + _g._betValue +" , move player cur bet = " + movePlayer.currentRoundBet());

      _g._call_amount = _g._betValue - movePlayer.currentRoundBet();

      assert _g._call_amount >= 0:"Call amount cannot be negative";

      double minR, maxR;
      boolean halfBet = false;

      _cat.finest(">>>>>>>   CRB=" + _g._betValue + ", Bet Allowed=" + bet_allowed + ", call=" + _g._call_amount + " last Raised=" + _g._lastRaise + " bettingRnd=" + _g._bettingRound);

      if (_g._betValue + _g._call_amount <= bet_allowed) {
        minR = bet_allowed + _g._call_amount;
        halfBet = true;
        //_cat.finest("HALF BET raise=" + minR);
      }
      else {
        if (_g.maxBet() == _g.POT_LIMIT || _g.maxBet() == _g.NO_LIMIT) {
          minR = _g._call_amount +
              (bet_allowed <= _g._lastRaise ? _g._lastRaise :
               bet_allowed);
        }
        else {
          minR = bet_allowed + _g._call_amount;
        }
      }

      if (pW <= 0.001) {

        // PLAYER WORTH is less than zero
        _cat.info("Player's worth is less than zero " + _g._nextPlayer);
        setBet(buf, pos, PokerMoves.ALL_IN, 0);

      }
      else
      if (pW <= _g._call_amount) {

        // PLAYER WORTH Is less than the call amount
        setBet(buf, pos, PokerMoves.ALL_IN, pW);

      }
      else if (pW < minR) {

        //PLAYER WORTH is less than minimum raise
        setBet(buf, pos, _g._call_amount == 0 ? PokerMoves.CHECK : PokerMoves.CALL,
               _g._call_amount);
        if (apc > 1) {
          setBet(buf, pos, PokerMoves.ALL_IN, pW);
        }

      }
      else {

        //Player worth is more than min Raise allowed
        setBet(buf, _g._nextPlayer.pos(),
               _g._call_amount == 0 ? PokerMoves.CHECK : PokerMoves.CALL,
               _g._call_amount);

        if (apc > 1) { // If this is not the only player active

          if (_g._loops < _g._max_loops - 1 && _g.maxBet() > 0 &&
              (_g._raiseCount < _g.MAX_RAISES ||
               (_g._table._last_game_heads_up &&
                _g._raiseCount < _g.HUP_MAX_RAISES))) {
            // RAISE ALLOWED FOR LIMIT GAMES
            maxR = minR;
            if (halfBet) {
              setBet(buf, pos,
                     _g.type().isStud() && _g._bettingRound == _g.R_PREFLOP ?
                     PokerMoves.COMPLETE : PokerMoves.RAISE, bet_allowed);
            }
            else {
               if (_g.type().isStudHi() && ((Stud)_g)._pair_hand && _g._bettingRound==1){
                 if (_g._lastRaise == _g.maxBet()){
                   minR = _g._call_amount +_g._lastRaise;
                   setBet(buf, pos, PokerMoves.RAISE, minR);
                 }
                 else {
                   setBet(buf, pos, PokerMoves.RAISE, minR, minR + _g._lastRaise);
                 }
               }
               else {
                 setBet(buf, pos, PokerMoves.RAISE, minR);
               }
            }
          } // RAISE ALLOWED

          if (_g.maxBet() == _g.POT_LIMIT) { // UNLIMITED RAISES FOR NL/PL
            _cat.finest("POT _ LIMIT  cal amt=" + _g._call_amount + " POT=" + _g.totalPot() + " CRB=" + _g.currentBet());
            maxR = _g._call_amount * 2 + _g.totalPot() + _g.currentBet();
            if (pW < maxR) {
              if (halfBet) {
                setBet(buf, pos, PokerMoves.RAISE, bet_allowed, pW);
              }
              else if (_g._raiseCount < _g.NLPL_MAX_RAISES) { // Limiting Raises in NL/PL
                if (pW - minR < bet_allowed) {
                  setBet(buf, pos, PokerMoves.RAISE, minR);
                }
                else {
                  setBet(buf, pos, PokerMoves.RAISE, minR, pW);
                }
              }
              setBet(buf, pos, PokerMoves.ALL_IN, pW);
            }
            else {
              if (halfBet) {
                setBet(buf, pos, PokerMoves.RAISE, bet_allowed, maxR);
              }
              else if (_g._raiseCount < _g.NLPL_MAX_RAISES) { // Limiting Raises in NL/PL
                setBet(buf, pos, PokerMoves.RAISE, minR, maxR);
                setBet(buf, pos, PokerMoves.BET_POT, maxR);
              }
            }
          }
          else if (_g.maxBet() == _g.NO_LIMIT) {
            maxR = pW;
            if (halfBet) {
              setBet(buf, pos, PokerMoves.RAISE, bet_allowed, pW);
            }
            else if (_g._raiseCount < _g.NLPL_MAX_RAISES) { // Limiting Raises in NL/PL
              if (pW - minR < bet_allowed) {
                setBet(buf, pos, PokerMoves.RAISE, minR);
              }
              else {
                setBet(buf, pos, PokerMoves.RAISE, minR, pW);
              }
            }
            setBet(buf, pos, PokerMoves.ALL_IN, pW);
          } // UNLIMITED RAISES FOR NL/PL

        } // More than 1 active player
      }

    }
   
    if (fold_allowed) {
      setBet(buf, pos, PokerMoves.FOLD, 0);
    }

    buf.deleteCharAt(buf.length() - 1);
    buf.append(",");

    if (_g._blinds_over_toggle == Poker.BLINDS_TRANSITION) {
      _g._blinds_over_toggle = Poker.BLINDS_OVER;
    }
    cache_nextMove = new StringBuilder(buf.toString());
    return buf;
  }

  protected StringBuilder potDetails() {
    StringBuilder buf = new StringBuilder().append("pots=");
    for (int i = 0; i < _g._pots.size(); i++) {
      Pot p = (Pot) (_g._pots.get(i));
      buf.append(p.getName()).append("|").append(p.getValString()).append("`");
    }
    return buf.deleteCharAt(buf.length() - 1).append(",");
  }

  protected StringBuilder potDetailsXML() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < _g._pots.size(); i++) {
      Pot p = (Pot) (_g._pots.get(i));
      buf.append("<pots ").append("name=\"").append(p.getName()).append(
          "\" value=\"").append(p.getValString()).append("\"/>\n");
    }
    return buf;
  }

  protected StringBuilder communityCards() {
    if (_g._communityCards.size() == 0) {
      return new StringBuilder();
    }
    else {
      StringBuilder buf = new StringBuilder("community-cards=").append(_g.
          communityCardsString())
          .append(",");
      return buf;
    }
  }

  protected StringBuilder prevCommunityCards() {
    StringBuilder buf = new StringBuilder("prev_community-cards=").append(_g.
        communityCardsString())
        .append(",");
    return buf;
  }


  protected StringBuilder winnersDetail() {
    StringBuilder b = new StringBuilder();
    b.append("winner=");
    List pots = _g.pots();
    _cat.info(pots.size() + "");
    for (int i = 0; i < pots.size(); i++) {
      Pot pot = (Pot) pots.get(i);
      PotWinner[] p = pot.highWinners();
      if (p == null) {
        continue;
      }
      for (int k = 0; p != null && k < p.length; k++) {
        _cat.info("HW=" + p[k]);
        b.append(pot.getName()).append("|");
        b.append(p[k]._pos).append("|");
        b.append(p[k]._name).append("|");
        b.append(p[k]._winAmountHigh).append("|");
        //if (_g.showdownPlayers(_g.showdownPos()) != null) { WHY THIS CHECK ???
        	_cat.finest("HW Player=" + ((PokerPresence)p[k]._presence).name() + ", sdwn=" + ((PokerPresence)p[k]._presence).isShowdown() + ", dmck=" +  ((PokerPresence)p[k]._presence).isDontMuck());
	      	  if (((PokerPresence)p[k]._presence).isShowdown() || ((PokerPresence)p[k]._presence).isDontMuck()
	      			  ||  ((PokerPresence)p[k]._presence).isAllIn()) {
		          b.append(((PokerPresence)p[k]._presence).showDownHand().getAllCardsString()).append("|");
		          Card[] crds = CardUtils.toCardsArray(p[k]._best_combination, 0xFFFFFFFFFL);
		          Cards best_combination = new Cards(false);
		          best_combination.addCards(crds);
		          b.append(best_combination.openStringValue()).append("|");
		          b.append(HandOps.getHandValue(p[k]._best_combination, true));
	      	  }
	      	  else {
        		  b.append("||");
	      	  }
       /** }
        else {
          b.append("||");
        }**/
        b.append("`");
      }

      p = pot.lowWinners();
      if (p == null) {
        continue;
      }
      for (int k = 0; p != null && k < p.length; k++) {
        _cat.info("LW=" + p[k]);
        b.append(pot.getName()).append("|");
        b.append(p[k]._pos).append("|");
        b.append(p[k]._name).append("|");
        b.append(p[k]._winAmountLow).append("|");
        //if (_g.showdownPlayers(_g.showdownPos()) != null) {  WHY THIS CHECK ???
		    _cat.finest("Player=" + ((PokerPresence)p[k]._presence) + ", sdwn=" + ((PokerPresence)p[k]._presence).isShowdown() + ", dmck=" +  ((PokerPresence)p[k]._presence).isDontMuck());
        	  if (((PokerPresence)p[k]._presence).isShowdown() || ((PokerPresence)p[k]._presence).isDontMuck()
	      			  ||  ((PokerPresence)p[k]._presence).isAllIn()) {
		          b.append(((PokerPresence)p[k]._presence).showDownHand().getAllCardsString()).append("|");
		          Card[] crds = CardUtils.toCardsArray(p[k]._best_combination, 0xFFFFFFFFFL);
		          Cards best_combination = new Cards(false);
		          best_combination.addCards(crds);
		          b.append(best_combination.openStringValue()).append("|");
		          b.append(HandOps.getHandValue(p[k]._best_combination, false));
        	  }
        	  else {
        		  b.append("||");
        	  }
        /**}
        else {
          b.append("||");
        }**/
        b.append("`");
      }

    }
    b.deleteCharAt(b.length() - 1).append(",");

	  b.append("open-hands=");
	  for (int i = 0;  i < _allPlayers.length; i++) {
		    PokerPresence pp = (PokerPresence) _allPlayers[i];
		    if (pp.showDownHand() == null)
		    	continue;
		    _cat.finest("Player=" + pp.name() + ", sdwn=" + pp.isShowdown() + ", dmck=" +  pp.isDontMuck());
		    if (pp.isShowdown() || pp.isDontMuck() || pp.isAllIn()) {
			      _cat.info("OH Checking for show down = " + pp);
			      if (!pp.showDownHand().getAllCardsString().equals("")){
				      b.append(pp.pos()).append("|");
				      b.append(pp.showDownHand().getAllCardsString());
				      b.append("`");
			      }
		    }
	  }
    _cat.info(b.toString());
    return b.deleteCharAt(b.length() - 1);
  }

  protected StringBuilder winnersDetailXML() {
    StringBuilder b = new StringBuilder();
    List pots = _g.pots();
    _cat.info(pots.size() + "");
    for (int i = 0; i < pots.size(); i++) {
      Pot pot = (Pot) pots.get(i);
      PotWinner[] p = pot.highWinners();
      if (p == null) {
        continue;
      }
      for (int k = 0; p != null && k < p.length; k++) {
        _cat.info("HW=" + p[k]);
        b.append("<winner pot_name=\"").append(pot.getName());
        b.append("\" name=\"").append(p[k]._name);
        b.append("\" amount=\"").append(p[k]._winAmountHigh);
        Card[] crds = CardUtils.toCardsArray(p[k]._best_combination,
                                             0xFFFFFFFFFL);
        Cards best_combination = new Cards(false);
        best_combination.addCards(crds);
        b.append("\" combination=\"").append(best_combination.openStringValue());
        b.append("\"/>\n");
      }
      p = pot.lowWinners();
      if (p == null) {
        continue;
      }
      for (int k = 0; p != null && k < p.length; k++) {
        _cat.info("LW=" + p[k]);
        b.append("<winner pot_name=\"").append(pot.getName());
        b.append("\" name=\"").append(p[k]._name);
        b.append("\" amount=\"").append(p[k]._winAmountLow);
        Card[] crds = CardUtils.toCardsArray(p[k]._best_combination,
                                             0xFFFFFFFFFL);
        Cards best_combination = new Cards(false);
        best_combination.addCards(crds);
        b.append("\" combination=\"").append(best_combination.openStringValue());
        b.append("\"/>\n");
      }

    }
    _cat.info(b.toString());
    return b;
  }

  private void setBet(StringBuilder buf, int pos, long move, double amt1) {
    buf.append(pos).append("|").append(new PokerMoves(move).stringValue()).append("|").
        append(Utils.getRoundedString(amt1)).append("`");
    _g.initNextMove(new PokerMoves(move).intIndex(), move, new PokerMoves(move).responseRequired(), amt1, 0.0);
    //_cat.finest(_g._nextMovePlayer + " Move = " + new PokerMoves(move).stringValue());
  }

  private void setBet(StringBuilder buf, int pos, long move, double amt1,
                      double amt2) {
    if (amt1 == amt2) {
      setBet(buf, pos, move, amt1);
    }
    else {
      buf.append(pos).append("|").append(new PokerMoves(move).stringValue()).append("|")
          .append(Utils.getRoundedString(amt1)).append("-").append(Utils.
          getRoundedString(amt2)).append("`");
      _g.initNextMove(new PokerMoves(move).intIndex(), move, new PokerMoves(move).responseRequired(), amt1, amt2);
      //_cat.finest(_g._nextMovePlayer + " Move = " + new PokerMoves(move).stringValue() +
                 //" amt1=" + amt1 + " amt2 = " + amt2);

    }
  }

  public boolean success() {
    return true;
  }

//@todo string operations and method signatures to have StringBuilder instead of string
  int gameId;
  transient Poker _g;
  HashMap _map = new HashMap();
  PokerPresence[] _allPlayers;
  String _broadcast;
  String _response;
  Set _observers;
// Cache variables
  transient StringBuilder cache_lastMove, cache_nextMove, cache_ab;

  int _responseId;
  volatile static int _msgGID = 0;

}
