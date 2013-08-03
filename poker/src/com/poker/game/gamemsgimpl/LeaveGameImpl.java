package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.LeaveGame;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.AbstractPlayerMessageBase;


public class LeaveGameImpl
    extends AbstractPlayerMessageBase
    implements LeaveGame {

  public LeaveGameImpl(String gameId, PokerPresence p, boolean timeout) {
    super(gameId, p);
    _timeout = timeout;
  }

  public byte id() {
    return 3;
  }
    
    
  public Response interpret() {
    Game g =  Game.game(gameId());
    g.setCurrent(player()); // only the current changes. marker does not.
    // marker not set here
    return g.leave(player(), _timeout);
  }

  private boolean _timeout;
}
