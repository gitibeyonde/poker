package com.poker.game;

import com.agneya.util.Utils;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.Player;
import com.golconda.game.PlayerStatus;
import com.golconda.game.Presence;
import com.golconda.game.util.Card;

import com.poker.common.db.DBPokerPlayer;
import com.poker.game.poker.Poker;
import com.poker.game.util.Hand;


public class PokerPresence extends Presence {
    public Hand _showDownHand;
    Hand _hand = new Hand(); // 0L
    volatile long[] _strength = new long[2];

    public PokerPresence(String gid, PokerPlayer p) {
        super(gid, p);
    }

    /**
     * visibility = true // cards are face up
     * visibility = false // cards are face down
     *
     **/
    public void addCards(long cards, boolean visibility) {
        _hand.addCard(cards, visibility);
    }

    public void addOpenCards(Card[] cards) {
        _hand.addOpenCard(Hand.getHandFromCardArray(cards));
    }

    public void addCloseCards(Card[] cards) {
        _hand.addCloseCard(Hand.getHandFromCardArray(cards));
    }

    public Hand getHand() {
        return _hand;
    }

    public void resetHand() {
        _hand.reset();
    }
    

    public void postRun() {
        super.postRun();
        _hand.reset(); // 0L
    }

    public Hand showDownHand() {
        return _showDownHand;
    }

    public void showDownHand(Hand h) {
        _showDownHand = h;
    }

    public void postWin() {
        super.postWin();
        _showDownHand = new Hand();
    }
    

    public void setHand(Card[] crds){
      _hand = new Hand(crds);
    }


      public void addWin(double win) {
        ((PokerPlayer)_player)._dbPokerPlayer.addWin(win);
      }

    public boolean isAllInAvailable() {
        /**try {
            return ((PokerPlayer)_player)._dbPokerPlayer.getAllIn() > 0;
        }
        catch (Exception e){
            return true;
        }  **/
        return false;
    }

    public void decrAllIn() {
        ((PokerPlayer)_player)._dbPokerPlayer.decrAllIn();
    }
    
    @Override
    public boolean buyIn(double amt, boolean active) {
  	    Poker p = (Poker)Game.game(this.getGameName());
  	    double max_buy_in = p.minBet() * 200;
		 if ((p.maxBet() > 0) || ((p.maxBet() <= 0) && (( _amount_at_table + _addedChips) < max_buy_in))){
		  	  if (active){
			          amt = Utils.getRounded(amt);
			          _addedChips += amt; //chisp were added this game
		  	  }
		  	  else {
		  		if (Game.game(_gname).type().isRegularGame()) {
	                if (Game.game(_gname).type().isReal() && _player.realWorth() >= amt) {
	                     _player._dbPlayer.realChipsBuyIn(_gname, Game.game(_gname).type().intVal(), new ModuleType( ModuleType.POKER), amt, _player.session());
	                     _amount_at_table += amt;
	                     _addedChips = 0;
	                } else if(_player.playWorth() >= amt) {
	                      _player._dbPlayer.playChipsBuyIn(_gname, Game.game(_gname).type().intVal(),  new ModuleType( ModuleType.POKER), amt, _player.session());
	                      _amount_at_table += amt;
	                      _addedChips = 0;
	                }
	                else {
	                	throw new IllegalStateException("Player buying more than allowed " + Game.game(_gname).type());
	                }
	            } else if (Game.game(_gname).type().isTourny()) {
	                //setTournyWorth(amt);
	            } else {
	                throw new IllegalStateException("Unknown game type " + Game.game(_gname).type());
	            }
		  	  }
		  	  return true;
		 }
		 else {
			return false; 
		 }
    }
    
    @Override
    public void addAddedChips() {
  	    if (_addedChips > 0){
            if (Game.game(_gname).type().isRegularGame()) {
                if (Game.game(_gname).type().isReal() && _player.realWorth() >= _addedChips) {
                     _player._dbPlayer.realChipsBuyIn(_gname, Game.game(_gname).type().intVal(), new ModuleType( ModuleType.POKER), _addedChips, _player.session());
                     _amount_at_table += _addedChips;
                     _addedChips = 0;
                } else if(_player.playWorth() >= _addedChips) {
                      _player._dbPlayer.playChipsBuyIn(_gname, Game.game(_gname).type().intVal(),  new ModuleType( ModuleType.POKER), _addedChips, _player.session());
                      _amount_at_table += _addedChips;
                      _addedChips = 0;
                }
            } else if (Game.game(_gname).type().isTourny()) {
                //setTournyWorth(amt);
            } else {
                throw new IllegalStateException("Unknown game type " + Game.game(_gname).type());
            }
        }
        else {
        	// rollback
            _addedChips = 0;
        }
    }
    
  
    
    @Override
    public void gameStart(long grid) {
        _startWorth = getAmtAtTable();
        _endWorth = _startWorth;
        resetIdleGC();
        setGRID(_grid);
        
        if (_addedChips > 0){
	        Poker p = (Poker)Game.game(this.getGameName());
	        double max_buy_in = p.minBet() * 200;
	        if ((p.maxBet() > 0) || ((p.maxBet() <= 0) && (( _amount_at_table + _addedChips) < max_buy_in))){
	            if (Game.game(_gname).type().isRegularGame()) {
	                if (Game.game(_gname).type().isReal() && _player.realWorth() >= _addedChips) {
	                     _player._dbPlayer.realChipsBuyIn(_gname, Game.game(_gname).type().intVal(), new ModuleType( ModuleType.POKER), _addedChips, _player.session());
	                     _amount_at_table += _addedChips;
	                     _addedChips = 0;
	                } else if(_player.playWorth() >= _addedChips) {
	                      _player._dbPlayer.playChipsBuyIn(_gname, Game.game(_gname).type().intVal(),  new ModuleType( ModuleType.POKER), _addedChips, _player.session());
	                      _amount_at_table += _addedChips;
	                      _addedChips = 0;
	                }
	            } else if (Game.game(_gname).type().isTourny()) {
	                //setTournyWorth(amt);
	            } else {
	                throw new IllegalStateException("Unknown game type " + Game.game(_gname).type());
	            }
	        }
	        else {
	        	// rollback
	            _addedChips = 0;
	        }
        }
        //if (_name.equals("abhi")){
        // System.out.println(_name + " Setting Start Worth " + _endWorth);
        //}
    }
    
    
    
    
    
    public String toString() {
        return super.toString() + 
            (((PokerPlayer)_player)._dbPokerPlayer != null ? ((PokerPlayer)_player)._dbPokerPlayer.getWin() + "" : "__");
    }
}
