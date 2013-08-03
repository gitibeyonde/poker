package com.onlinepoker.actions;
/**
 * Agneya NEW CLASS
 *
 * this action will deliver the message to the appropriate client
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ServerMessageAction
    extends StageAction {

  private String _cm;

  public ServerMessageAction(int target, String cm) {
    super(PLAYER_MESSAGE, target);
    this._cm = cm;
  }

  public ServerMessageAction(String cm) {
    super(PLAYER_MESSAGE, -1);
    this._cm = cm;
  }

  public String getClientMessage() {
    return _cm;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("MESSAGE: ").append(_cm).
        append(" > ").append(target);
    return s.toString();
  }

}
