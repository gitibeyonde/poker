package com.poker.game;

import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;
import com.golconda.game.Game;
import com.golconda.game.Player;

import com.poker.common.db.DBPokerPlayer;


public class PokerPlayer extends Player {
    public DBPokerPlayer _dbPokerPlayer;
    
    public PokerPlayer(String session) {    
        super(session);
    }

   
        // TO BE USED ONLY FOR TESTING
      public PokerPlayer(String name, double playChips, double realChips) {
        super(name, playChips, realChips);
        _dbPokerPlayer = new DBPokerPlayer();
        try {
            _dbPokerPlayer.get(name);
        }
        catch (DBException e){
            // if entry does not exists create it
             _dbPokerPlayer.init();
             try {
                _dbPokerPlayer.save();
             }
             catch (DBException ex){
                 ex.printStackTrace();
             }
        }
      }


      public PokerPresence presence(String tid) {
    	  try {
    		  return (PokerPresence) _presence_registry.get( tid);
    	  }
    	  catch (Exception e){
    		  return null;
    	  }
      }

      public PokerPresence createPresence(String tid) {
        PokerPresence p = (PokerPresence) _presence_registry.get(tid);
        if (p == null) {
          p = new PokerPresence(tid, this);
          _presence_registry.put(tid, p);
        }
        if (win_loss_violated != 0 && Game.game(p.getGameName()).type().isReal() &&
            !Game.game(p.getGameName()).type().isTourny()) { //if real game set violation
          p.setWinLossViolated();
        }
        return p;
      }


    @Override
    public String toString() {
        return super.toString();
    }


}
