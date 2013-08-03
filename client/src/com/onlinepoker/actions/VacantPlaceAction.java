package com.onlinepoker.actions;


/**
 * <p>This class is a message sent from Table Controller
 * to warn waiting player about a vacant place.</p>
 * @version 1.0
 */

public class VacantPlaceAction extends TableServerAction {

  private long claimStartTime;
  /**
   * Constructor.
   */
  public VacantPlaceAction(long claimStartTime) {
  	//int id, int type, int target
    super(WAITER_CAN_JOIN);
    this.claimStartTime = claimStartTime;
  }

  /**
   * Time when registration attemp started.
   */
  public final long getClaimStartTime() {
    return claimStartTime;
  }


}
