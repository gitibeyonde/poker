package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.PlayerMessage;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.AbstractPlayerMessageBase;


/**
 * Created by IntelliJ IDEA. User: aprateek Date: Apr 16, 2004 Time: 11:22:37 AM To
 * change this template use File | Settings | File Templates.
 */
public class ObserveGameImpl
    extends AbstractPlayerMessageBase
    implements PlayerMessage {

  public ObserveGameImpl(PokerPresence p, String gameId) {
    super(gameId, p);
  }

  public byte id() {
    return 4;
  }
    
   
  public Response interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    return g.observe(player());
  }

}
