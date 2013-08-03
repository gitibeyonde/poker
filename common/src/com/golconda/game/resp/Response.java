package com.golconda.game.resp;

import com.golconda.game.Game;
import com.golconda.game.Presence;

import java.io.Serializable;


public interface Response extends Serializable {

  public Game getGame();

  void addRecepient(Presence p);

  void addRecepients(Presence[] p);

  void addObservers(Presence[] p);

  Presence[] observers();

  Presence removeRecepient(Presence p);

  public boolean recepientExists(Presence p);

  void broadcast(Presence[] p, String command);

  void setCommand(Presence p, String command);

  public String getBroadcast();

  public String getCommand(Presence p);

  public Presence[] recepients();

  public boolean success();

}
