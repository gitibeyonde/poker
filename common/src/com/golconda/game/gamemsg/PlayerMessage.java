package com.golconda.game.gamemsg;

import com.golconda.game.Presence;


public interface PlayerMessage
    extends Message {

  public Presence player();

}
