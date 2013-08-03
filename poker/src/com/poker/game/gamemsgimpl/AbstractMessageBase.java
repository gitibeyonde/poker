package com.poker.game.gamemsgimpl;

import com.golconda.game.gamemsg.Message;


public abstract class AbstractMessageBase
    implements Message {

  public AbstractMessageBase(String gameId) {
    this.gameId = gameId;
  }

  public String gameId() {
    return gameId;
  }

  String gameId;

}
