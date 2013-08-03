package com.onlinepoker.proxies;

import com.onlinepoker.actions.Action;

/**
 * It is a lobby tables change subscriber interface.
 * @author Kom
 */

public interface LobbyInfoListener {

  public void serverLobbyResponse(Action act);

}
