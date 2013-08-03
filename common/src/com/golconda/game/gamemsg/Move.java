package com.golconda.game.gamemsg;

import com.golconda.game.gamemsg.PlayerMessage;


public interface Move
    extends PlayerMessage {

  public long move();

  public double amount();
}
