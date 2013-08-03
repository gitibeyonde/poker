package com.onlinepoker.lobby;

import com.onlinepoker.actions.ChatAction;
import com.onlinepoker.models.LobbyTableModel;

/**
 * It is a lobby tables change subscriber interface.
 * @author Abhi
 */

public interface LobbyModelsChangeListener {

  public void holdemTableListUpdated(LobbyTableModel[] changes);

  public void omahaTableListUpdated(LobbyTableModel[] changes);
  
  public void studTableListUpdated(LobbyTableModel[] changes);
  
  public void adUpdated();

  public void chatRcvd(ChatAction chat);

//  public void professionalPlayerList(DBPlayer []v);
}
