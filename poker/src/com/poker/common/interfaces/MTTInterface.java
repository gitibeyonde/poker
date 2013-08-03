package com.poker.common.interfaces;

import com.golconda.game.Presence;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;


public interface MTTInterface extends TournyTableInterface {
  public String name();

  public Response details();

  public Response details(Presence p);

  public PokerPresence[] allPlayers(int startPos);

  public boolean tournyOver();

  public boolean tournyWaiting();

  public int limit();

  public void setArgs(double minBet, double maxBet,
                      double smallBlind, double bigBlind);

  public Response start();

  public int getPlayerCount();

  public PokerPresence[] getPlayerList();

  public boolean handOver();

  public void invite(String[] p);

  public void addInvite(Presence p);

  public boolean isInvited(String name);

  public void setInquirer(Presence player);

  public PokerPresence getPresenceOnTable(String name);

  public int getNextVacantPosition();

  public void destroyTournyTable();

  public Response moveToTable(Presence p, String tid) ;

  public int startCount();

  public void setCurrent(Presence p);
  
  public int state();
}
