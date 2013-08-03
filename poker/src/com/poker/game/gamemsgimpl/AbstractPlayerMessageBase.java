package com.poker.game.gamemsgimpl;

import com.golconda.game.gamemsg.PlayerMessage;

import com.poker.game.PokerPresence;


public abstract class AbstractPlayerMessageBase
    extends AbstractMessageBase
    implements PlayerMessage {

  public AbstractPlayerMessageBase(String gameId, PokerPresence p) {
    super(gameId);
    this.p = p;
  }

  public PokerPresence player() {
    return p;
  }

  private PokerPresence p;
}
