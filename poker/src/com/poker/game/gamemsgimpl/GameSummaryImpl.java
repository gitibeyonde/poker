package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.GameDetails;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;


public class GameSummaryImpl
    extends AbstractPlayerMessageBase
    implements GameDetails {

  public GameSummaryImpl(String gameId, PokerPresence p) {
    super(gameId, p);
  }

  public byte id() {
    return 1;
  }
   
  public Response interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    return g.summary();
  }

}
