package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.LeaveWatch;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.AbstractPlayerMessageBase;


public class LeaveWatchImpl
    extends AbstractPlayerMessageBase
    implements LeaveWatch {

  public LeaveWatchImpl(PokerPresence p, String gameId) {
    super(gameId, p);
  }

  public byte id() {
    return 5;
  }
    
   
  public Response interpret() {
    Game g =  Game.game(gameId());
    // no marker handling here
    return g.leaveWatch(player());
  }

}
