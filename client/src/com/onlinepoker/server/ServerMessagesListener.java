package com.onlinepoker.server;


/**
 * It is a Table Server message subscriber interface.
 * @author Kom
 */

public interface ServerMessagesListener {

  public void serverMessageReceived(String tid, Object actions);
  
  
}
