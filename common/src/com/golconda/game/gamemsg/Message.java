package com.golconda.game.gamemsg;

import com.golconda.game.resp.Response;

import java.io.Serializable;


public interface Message extends Serializable {

  public byte id();

  public String gameId();

  public Response interpret();
  

}
