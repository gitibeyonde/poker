package com.onlinepoker.server;


/**
 * It is a Table Server message subscriber interface.
 * @author Kom
 */

public interface ServerMessageListener {

  public void serverMessageReceive(String message);
  
}
