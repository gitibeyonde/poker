package com.onlinepoker.proxies;

import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.LobbyTournyModel;


public interface LobbyModelsChangeListener {

  public void tableListUpdated(LobbyTableModel[] changes);
  
  public void tournyListUpdated(LobbyTournyModel[] changes);

  public void adUpdated();

  public void holdemTableListUpdated(LobbyTableModel[] changes);

  public void omahaTableListUpdated(LobbyTableModel[] changes);

  public void studTableListUpdated(LobbyTableModel[] changes);

  public void sitnGoTableListUpdated(LobbyTableModel[] changes);

  
}