package com.golconda.game;

import java.util.logging.Logger;

import com.agneya.util.Utils;
import com.golconda.db.ModuleType;

public class Presence  {
	static Logger _cat = Logger.getLogger(Presence.class.getName());
	
    protected String _gname;
    protected long _grid = -1;
    public String _tid;
    protected double _startWorth, _endWorth, _addedChips;
    protected double _amount_at_table = 0;
    protected double _betValue, _currentRoundBet, _totalHandBet;
    public int _idleGC;
    public static final int MAX_IDLE_GC = 14;
    public static final int MAX_IDLE_TIME = 40 * 60 * 1000;

    //poker specific
    protected long _ps = PlayerStatus.NEW;
    protected long _lastMove = Moves.NONE;
    public long _start_wait = -1;
    int _pos;
    protected Player _player;
    int _state = NONE;
    int _vote;

    public Presence(String gid, Player p) {
        _gname = gid;
        _player = p;
        //      _cat.finest(p + ", " + gid + ", " + Game.game(_gname).type() + "");
        if (p.shill()) {
            setShill();
        }
    }//ADD WATCH

    /// JOIN
      public double getAmtAtTable() {
          return Utils.getRounded(_amount_at_table);
      }
      
      public void addAddedChips() {
          _amount_at_table += _addedChips;
          _addedChips = 0;
      }
      
    
      public void joinTable(double amt, ModuleType mod) {
          GameType gt = Game.game(_gname).type();
          if (gt.isRegularGame()) {
              if (Game.game(_gname).type().isReal()) {
                   assert _player.realWorth() >= amt : " worth=" + _player.realWorth() + " bring in =" +  amt;
                   _player._dbPlayer.realChipsToTable(_gname, Game.game(_gname).type().intVal(), mod, amt, _player._session);
                   _cat.finest("joinTable real=name"+name() + ", amt=" + amt);
              } else {
                  assert _player.playWorth() >= amt : " worth=" + _player.playWorth() + " bring in =" + amt;
                  _player._dbPlayer.playChipsToTable(_gname, Game.game(_gname).type().intVal(), mod, amt, _player._session);
                  _cat.finest("joinTable play=name"+name() + ", amt=" + amt);
              }
             _amount_at_table = amt;
          } else if (gt.isTourny()) {
              setTournyWorth(amt);
          } else {
              throw new IllegalStateException("Unknown game type " + Game.game(_gname).type());
          }
          //_cat.finest(_dbPlayer);
      }
      

      public void leaveTable(ModuleType mod) {
         setGameEndWorth();
          Game g = Game.game(_gname);
          if (g == null) {
              throw new IllegalStateException("Associated game is null, cannot remove chips");
          }
          if (_amount_at_table > 0 && !isQickFold()){
	          if (g.type().isTourny()) {
	              // do nothing
	          } else if (g.type().isReal()) {
	               _player._dbPlayer.realChipsFromTable(_gname, Game.game(_gname).type().intVal(), mod, _amount_at_table, _player._session);
                   _cat.finest("leaveTable real: name=" + name() + ", amt=" + _amount_at_table);
	          } else {
	              _player._dbPlayer.playChipsFromTable(_gname, Game.game(_gname).type().intVal(), mod, _amount_at_table, _player._session);
                  _cat.finest("leaveTable play: name=" + name() + ", amt=" + _amount_at_table + ", " + this._player.playWorth());
	          }
          }
          _amount_at_table = 0;
          _addedChips=0;
      }  ///LEAVE
      
    // this can be overridden
    public boolean buyIn(double amt, boolean active){ 
        _amount_at_table += amt;
    	return false;
    }

    public void buyIn(double amt){ 
        _amount_at_table += amt;
    }
      
    public double getGameStartWorth() {
        // System.out.println(_name + " Start Worth " + _startWorth);
        return _startWorth;
    }

    public void gameStart(long grid) {
        _startWorth = getAmtAtTable();
        _endWorth = _startWorth;
        _amount_at_table += _addedChips;
        _addedChips = 0;
        
        resetIdleGC();
        setGRID(_grid);
        //if (_name.equals("abhi")){
        // System.out.println(_name + " Setting Start Worth " + _endWorth);
        //}
    }

    public double getGameEndWorth() {
        //System.out.println(_name + " End Worth " + _endWorth);
        return _endWorth;
    }

    public void setGameEndWorth() {
        if (isRemoved()) {
            return; //do not change the end worth if player left
        }
        _endWorth = getAmtAtTable();
        //if (_name.equals("abhi")){
        //System.out.println(_name + " Setting end Worth " + _endWorth);
        //System.out.println(PlayerStatus.stringValue(_ps));
        //}
    }
    
    

     public double getWorth() {
         if (_player._dbPlayer == null) {
             throw new IllegalStateException("DBPlayer is null, cannot get worth");
         }
         if (Game.game(_gname).type().isReal()) {
             return _player.realWorth();
         } else {
             return _player.playWorth();
         }
     }


    public double getGameAddedChips() {
        return _addedChips;
    }
    
    public String betValueString() {
        return Utils.getRoundedString(_betValue);
    }

    public double betValue() {
        return _betValue;
    }

    public void betValue(double val) {
        _betValue = val;
    }

    public void addToWin(double val) {
        _amount_at_table += val;
    }

    // names are not matching but totalBet and currentRoundBet are a pair

    public double currentRoundBet() {
        return _currentRoundBet;
    }

    public String currentRoundBetRoundedString() {
        return Utils.getRoundedString(_currentRoundBet);
    }

    public void currentRoundBet(double amt) {
        amt = Utils.getRounded(amt);
        _totalHandBet += amt;
        _currentRoundBet += amt;
        _amount_at_table -= amt;
    }

    public double getTotalHandBet() {
        return Utils.getRounded(_totalHandBet);
    }

    public void resetRoundBet() {
        _currentRoundBet = 0;
    }

    public void returnRoundBet() {
        _amount_at_table += _currentRoundBet;
        _currentRoundBet = 0;
    }

    public void returnRoundBet(double amt) {
        _amount_at_table += amt;
        _currentRoundBet -= amt;
    }


    public void deductMoney(double amt) {
        _amount_at_table -= amt;
    }

    public double netWorth() {
        return _amount_at_table;
        /* - _currentRoundBet); Since the curret bet is immediately deducted from the table amt*/
    }

    public String netWorthString() {
        return Utils.getRoundedString(_amount_at_table);
    }

     private void setTournyWorth(double amt) {
         _amount_at_table = amt;
     }
     
         
    public void setGameName(String gid) {
        _gname = gid;
    }

    public String getGameName() {
        return _gname;
    }

        public String getGID() {
            return _gname;
        }

    public long getGRID() {
        return _grid;
    }

    public void setGRID(long grid) {
        if (grid < 0) {
            return;
        }
        _grid = grid;
    }

    public String name() {
        return _player._name;
    }

    public int gender() {
        return _player._gender;
    }

    public int rank() {
        return _player._rank;
    }
    
    public String avatar(){
    	return _player._avatar;
    }
    
    public String city(){
    	return _player._city;
    }

    public void addNegativeVote() {
        _vote--;
    }

    public void addPositiveVote() {
        _vote++;
    }

    public int vote() {
        return _vote;
    }

    public void incrIdleGC() {
        _idleGC++;
    }

    public void resetIdleGC() {
        _idleGC = 0;
    }

    public boolean isIdleGCViolated() {
        return _idleGC >= MAX_IDLE_GC;
    }

    public Player player() {
        return _player;
    }

   
    public void lastMove(long mvId) {
        _lastMove = mvId;
    }

    public long lastMove() {
        return _lastMove;
    }

    public void resetLastMove() {
        _lastMove = Moves.NONE;
    }

    public boolean isLastMove() {
        return _lastMove != Moves.NONE;
    }

   
    public int pos() {
        return _pos;
    }

    public void setPos(int pos) {
        _pos = pos;
    }

    public void postRun() {
        _currentRoundBet = 0;
    }

    public void postWin() {
        _betValue = 0;
        _totalHandBet = 0;
        _lastMove = Moves.NONE;
        unsetShowdown();
    }

    /*************** PLAYER STATUS **************************/
    public

    long status() {
        return _ps;
    }

    // PLAYER STATE AT START Of GAME

    public void setDealer() {
        _ps |= PlayerStatus.DEALER;
    }

    public boolean isDealer() {
        return (_ps & PlayerStatus.DEALER) > 0;
    }

    public void unsetDealer() {
        _ps &= ~PlayerStatus.DEALER;
    }

    public void setSB() {
        _ps |= PlayerStatus.SMALL_BLIND;
        setRaiseReq();
    }

    public boolean isSB() {
        return (_ps & PlayerStatus.SMALL_BLIND) > 0;
    }

    public void unsetSB() {
        _ps &= ~PlayerStatus.SMALL_BLIND;
    }

    public void setBB() {
        _ps |= PlayerStatus.BIG_BLIND;
        setRaiseReq();
    }

    public boolean isBB() {
        return (_ps & PlayerStatus.BIG_BLIND) > 0;
    }

    public void unsetBB() {
        _ps &= ~PlayerStatus.BIG_BLIND;
    }

    public void setAnte() {
        _ps |= PlayerStatus.ANTE;
    }

    public boolean isAnte() {
        return (_ps & PlayerStatus.ANTE) > 0;
    }

    public void unsetAnte() {
        _ps &= ~PlayerStatus.ANTE;
    }

    ///////////////RUNTIME STATE

    public void setFolded() {
        _ps |= PlayerStatus.FOLDED;
    }

    public boolean isFolded() {
        return (_ps & PlayerStatus.FOLDED) > 0;
    }

    public void setAllInAdjusted() {
        _ps |= PlayerStatus.ALLIN_ADJUSTED;
    }

    public void unsetAllInAdjusted() {
        _ps &= ~PlayerStatus.ALLIN_ADJUSTED;
    }

    public boolean isAllInAdjusted() {
        return (_ps & PlayerStatus.ALLIN_ADJUSTED) > 0;
    }

    public void setAllIn() {
        _ps |= PlayerStatus.ALLIN;
    }

    public void unsetAllIn() {
        _ps &= ~PlayerStatus.ALLIN;
    }

    public boolean isAllIn() {
        return (_ps & PlayerStatus.ALLIN) > 0;
    }

    public void setOptOut() {
        _ps |= PlayerStatus.OPTOUT;
    }

    public void unsetOptOut() {
        _ps &= ~PlayerStatus.OPTOUT;
    }

    public boolean isOptOut() {
        return (_ps & PlayerStatus.OPTOUT) > 0;
    }

    public void setABPosted() {
        _ps |= PlayerStatus.ABPOSTED;
        _ps &= ~PlayerStatus.MISSED_SB;
        _ps &= ~PlayerStatus.MISSED_BB;
        unsetNew();
    }

    public boolean isABPosted() {
        return (_ps & PlayerStatus.ABPOSTED) > 0;
    }

    public void unsetABPosted() {
        _ps &= ~PlayerStatus.ABPOSTED;
    }

    public void setBetweenBlinds() {
        _ps |= PlayerStatus.SITBETBLINDS;
    }

    public boolean isBetweenBlinds() {
        return (_ps & PlayerStatus.SITBETBLINDS) > 0;
    }

    public void unsetBetweenBlinds() {
        _ps &= ~PlayerStatus.SITBETBLINDS;
    }
    public void setQickFold() {
        _ps |= PlayerStatus.QUICK_FOLDED;
      }

      public boolean isQickFold() {
        return (_ps & PlayerStatus.QUICK_FOLDED) > 0;
      }

      public void unsetQickFold() {
        _ps &= ~PlayerStatus.QUICK_FOLDED;
      }

      public void setAfternoon() {
        _ps |= PlayerStatus.AFTERNOON;
      }

      public boolean isAfternoon() {
        return (_ps & PlayerStatus.AFTERNOON) > 0;
      }

      public void unsetAfternoon() {
        _ps &= ~PlayerStatus.AFTERNOON;
      }


      public void setEvening() {
        _ps |= PlayerStatus.EVENING;
      }

      public boolean isEvening() {
        return (_ps & PlayerStatus.EVENING) > 0;
      }

      public void unsetEvening() {
        _ps &= ~PlayerStatus.EVENING;
      }


    public boolean isSeen() {
        return (_ps & PlayerStatus.SEEN) > 0;
    }

    public void setSeen() {
        _ps |= PlayerStatus.SEEN;
    }

    public void unsetSeen() {
        _ps &= ~PlayerStatus.SEEN;
    }

    public boolean isNew() {
        return (_ps & PlayerStatus.NEW) > 0;
    }

    public void setNew() {
        _ps |= PlayerStatus.NEW;
    }

    public void unsetNew() {
        _ps &= ~PlayerStatus.NEW;
    }

    public void setSitOutNextGame() {
        _ps |= PlayerStatus.SITOUTNEXTGAME;
    }

    public void unsetSitOutNextGame() {
        _ps &= ~PlayerStatus.SITOUTNEXTGAME;
        _ps &= ~PlayerStatus.WAIT_FOR_BLINDS;
    }

    public boolean isSitOutNextGame() {
        return (_ps & PlayerStatus.SITOUTNEXTGAME) > 0;
    }

    public void setSittingOut() {
        _ps |= PlayerStatus.SITTINGOUT;
    }

    public void unsetSittingOut() {
        _ps &= ~PlayerStatus.SITTINGOUT;
    }

    public boolean isSittingOut() {
        return (_ps & PlayerStatus.SITTINGOUT) > 0;
    }

    public void setWaitForBlinds() {
        _ps |= PlayerStatus.WAIT_FOR_BLINDS;
        _ps &= ~PlayerStatus.SITOUTNEXTGAME;
        _ps &= ~PlayerStatus.SITTINGOUT;
    }

    public void unsetWaitForBlinds() {
        _ps &= ~PlayerStatus.WAIT_FOR_BLINDS;
    }

    public boolean isWaitForBlinds() {
        return (_ps & PlayerStatus.WAIT_FOR_BLINDS) > 0;
    }

    public void setVotedOff() {
        _ps |= PlayerStatus.VOTED_OFF;
    }

    public void unsetVotedOff() {
        _ps &= ~PlayerStatus.VOTED_OFF;
    }

    public boolean isVotedOff() {
        return (_ps & PlayerStatus.VOTED_OFF) > 0;
    }

    public void setSitin() {
        unsetSitOutNextGame();
    }

    public void setBroke() {
        _ps |= PlayerStatus.BROKE;
    }

    public boolean isBroke() {
        return (_ps & PlayerStatus.BROKE) > 0;
    }

    public void unsetBroke() {
        _ps &= ~PlayerStatus.BROKE;
    }

    public void setBrokeOut() {
        _ps |= PlayerStatus.BROKE_OUT;
    }

    public boolean isBrokeOut() {
        return (_ps & PlayerStatus.BROKE_OUT) > 0;
    }

    public void unsetBrokeOut() {
        _ps &= ~PlayerStatus.BROKE_OUT;
    }

    public void setSingleMove() {
        _ps |= PlayerStatus.SINGLE_MOVE;
    }

    public boolean isSingleMove() {
        return (_ps & PlayerStatus.SINGLE_MOVE) > 0;
    }

    public void unsetSingleMove() {
        _ps &= ~PlayerStatus.SINGLE_MOVE;
    }

    public void setRemoved() {
        _ps |= PlayerStatus.REMOVED;
    }

    public boolean isRemoved() {
        return (_ps & PlayerStatus.REMOVED) > 0;
    }

    public void unsetRemoved() {
        _ps &= ~PlayerStatus.REMOVED;
    }

    public boolean isActive() {
        return (_ps & PlayerStatus.M_INACTIVE) == 
            0; // filter out sitin, sitbetblinds, fold,
        // optout, all-in, removed, broke, sittingout,wait for blinds
    }

    public void resetPlayerForNewGame() {
        _ps &= PlayerStatus.M_RESET;
    }

    public void resetPS() {
        _ps = PlayerStatus.NEW;
    }

    public void setResponseReq() {
        _ps |= PlayerStatus.RESP_REQ;
        _start_wait = System.currentTimeMillis();
    }

    public boolean isResponseReq() {
        return (_ps & PlayerStatus.RESP_REQ) > 0;
    }

    public void unsetResponseReq() {
        _ps &= ~PlayerStatus.RESP_REQ;
        _start_wait = -1;
    }

    public void setRaiseReq() {
        _ps |= PlayerStatus.RAISE_REQ;
        _start_wait = System.currentTimeMillis();
    }

    public boolean isRaiseReq() {
        return (_ps & PlayerStatus.RAISE_REQ) > 0;
    }

    public void unsetRaiseReq() {
        _ps &= ~PlayerStatus.RAISE_REQ;
        _start_wait = -1;
    }

    public void setShowdown() {
        _ps |= PlayerStatus.SHOWDOWN;
    }

    public boolean isShowdown() {
        return (_ps & PlayerStatus.SHOWDOWN) > 0;
    }

    public void unsetShowdown() {
        _ps &= ~PlayerStatus.SHOWDOWN;
    }

    public void setDontMuck() {
        _ps |= PlayerStatus.DONT_MUCK;
    }

    public boolean isDontMuck() {
        return (_ps & PlayerStatus.DONT_MUCK) > 0;
    }

    public void unsetDontMuck() {
        _ps &= ~PlayerStatus.DONT_MUCK;
    }

    
    
    public void setMissedBB() {
        _ps |= PlayerStatus.MISSED_BB;
    }

    public boolean isMissedBB() {
        return (_ps & PlayerStatus.MISSED_BB) > 0;
    }

    public void unsetMissedBB() {
        _ps &= ~PlayerStatus.MISSED_BB;
    }

    public void setMissedSB() {
        _ps |= PlayerStatus.MISSED_SB;
    }

    public boolean isMissedSB() {
        return (_ps & PlayerStatus.MISSED_SB) > 0;
    }

    public void unsetMissedSB() {
        _ps &= ~PlayerStatus.MISSED_SB;
    }

    public void setShill() {
        _ps |= PlayerStatus.SHILL;
    }

    public boolean isShill() {
        return (_ps & PlayerStatus.SHILL) > 0;
    }

    public void setWinLossViolated() {
        _ps |= PlayerStatus.WINLOSSVIOLATED;
    }

    public boolean isWinLossViolated() {
        return (_ps & PlayerStatus.WINLOSSVIOLATED) > 0 && 
            Game.game(_gname).type().isReal(); //if real game set violation;
    }

    public void unsetWinLossViolated() {
        _ps &= ~PlayerStatus.WINLOSSVIOLATED;
    }

    public void setDisconnected() {
        _ps |= PlayerStatus.DISCONNECTED;
    }

    public boolean isDisconnected() {
        return (_ps & PlayerStatus.DISCONNECTED) > 0;
    }

    public void unsetDisconnected() {
        _ps &= ~PlayerStatus.DISCONNECTED;
    }

    public void setReconnected() {
        _ps |= PlayerStatus.RECONNECTED;
    }

    public boolean isReconnected() {
        return (_ps & PlayerStatus.RECONNECTED) > 0;
    }

    public void unsetReconnected() {
        _ps &= ~PlayerStatus.RECONNECTED;
    }

    public void setMTTPresence(String tid) {
        _ps |= PlayerStatus.MTT_PRESENCE;
        _tid = tid;
    }

    public boolean isMTTPresence() {
        return (_ps & PlayerStatus.MTT_PRESENCE) > 0;
    }

    public void unsetMTTPresence() {
        _ps &= ~PlayerStatus.MTT_PRESENCE;
    }

    public void setSNGPresence(String tid) {
        _ps |= PlayerStatus.SNG_PRESENCE;
        _tid = tid;
    }

    public boolean isSNGPresence() {
        return (_ps & PlayerStatus.SNG_PRESENCE) > 0;
    }

    public void unsetSNGPresence() {
        _ps &= ~PlayerStatus.SNG_PRESENCE;
    }

    public boolean isTournyPresence() {
        return (_ps &  (PlayerStatus.SNG_PRESENCE | PlayerStatus.MTT_PRESENCE)) > 0;
    }

    /***************************** PLAYER STATUS ********************************/
    public boolean equals(Object o) {
        Presence p = (Presence)o;
        if (p != null && p.name().equals(_player._name) && p._gname == _gname) {
            return true;
        } else {
            return false;
        }
    }

    // Player State

    public int getState() {
        return _state;
    }

    public boolean isObserver() {
        return (_state & OBSERVER) > 0;
    }

    public void setObserver() {
        _state = OBSERVER;
    }

    public boolean isPlayer() {
        return (_state & PLAYER) > 0;
    }

    public void setPlayer() {
        _state = PLAYER;
    }

    public boolean isWaiter() {
        return (_state & WAITER) > 0;
    }

    public void setWaiter() {
        _state = WAITER;
    }

    public void unsetState() {
        _state = NONE;
    }

    static final int NONE = 0;
    static final int OBSERVER = 1;
    static final int PLAYER = 2;
    static final int WAITER = 4;

    public String toString() {
        return player() + ", TableAmt=" + getAmtAtTable() +  ", Gid=" + _gname + ", Grid=" + _grid + 
            ", pos=" + _pos + " State=" + _state + ", PS=" + 
            PlayerStatus.stringValue(_ps);
    }

}

