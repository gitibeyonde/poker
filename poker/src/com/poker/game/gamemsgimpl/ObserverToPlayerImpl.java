package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.ObserverToPlayer;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.AbstractPlayerMessageBase;


/**
 * Created by IntelliJ IDEA. User: aprateek Date: Apr 16, 2004 Time: 11:51:13 AM To
 * change this template use File | Settings | File Templates.
 */
public class ObserverToPlayerImpl
    extends AbstractPlayerMessageBase
    implements ObserverToPlayer {

  public ObserverToPlayerImpl(String gameId, PokerPresence observer, double amount) {
    super(gameId, observer);
    _amount = amount;
  }

  public byte id() {
    return 6;
  }
    
    
  public Response interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    g.setCurrent(player());
    return g.promoteToPlayer(player(), _amount);
  }

  double _amount=0; //requested amount

}
