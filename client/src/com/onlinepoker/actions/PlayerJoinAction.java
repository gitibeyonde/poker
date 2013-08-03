package com.onlinepoker.actions;

import com.golconda.game.util.ActionConstants;

import com.onlinepoker.ClientPlayerModel;


/**
 * <p>This class is simple message sended from Table Server Implementation
 * to Table Controller when new player registers on the table or when
 * a registerd player leaves the table.</p>
 * @version 1.0
 */

public class PlayerJoinAction
    extends TableServerAction {

  private ClientPlayerModel player;
  private double cash;
  private boolean me;

  /**
   * Constructor.
   */
  public PlayerJoinAction(int seat, ClientPlayerModel player, boolean isMe) {
    //int id, int type, int target
    super(ActionConstants.PLAYER_JOIN, seat);
    this.player = player;
    me = isMe;
  }

  /**
   * Constructor.
   */
  public PlayerJoinAction(int seat, double cash) {
    super(ActionConstants.PLAYER_JOIN, seat);
    this.cash = cash;
  }

  /**
   * Gets the player model. Note that the player model can be null in case of
   * 'player leave' action.
   */
  public final ClientPlayerModel getPlayer() {
    return player;
  }

  public final int getSeat() {
    return super.getTarget();
  }

  public final boolean isMe() {
    return me;
  }

  public final double getCash() {
    return cash;
  }

}
