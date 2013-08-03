package com.onlinepoker.actions;


/**
 * This is the bet requst Action.
 * @author Yuriy Guskov
 */
public class BetRequestAction
    extends Action {

  private int[] actions;
  private double[] amount;
  private double[] amount_limit;
  private double minBet;
  private double maxBet;

  public BetRequestAction(int id, int target, double minBet, double maxBet,
                          int[] actions, double[] amount, double[] amount_limit) {
    super(id, ACTION_TYPE_BETTING, target);
    this.amount = amount;
    this.amount_limit = amount_limit;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.actions = actions;
  }

  public BetRequestAction(int id, double minBet, double maxBet,
                          int[] actions, double amount[], double[] amount_limit) {
    super(id, ACTION_TYPE_BETTING);
    this.amount = amount;
    this.amount_limit = amount_limit;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.actions = actions;
  }

  public double[] getAmount() {
    return amount;
  }

  public double[] getAmountLimit() {
    return amount_limit;
  }

  public double getMinBet() {
    return minBet;
  }

  public double getMaxBet() {
    return maxBet;
  }

  public int[] getAction() {
    return actions;
  }

  public boolean isInActions(int action) {
    if (actions != null) {
      for (int i = 0; i < actions.length; i++) {
        if (actions[i] == action) {
          return true;
        }
      }
    }
    return false;
  }

  public double getAmount(int move) {
    for (int i = 0; i < actions.length; i++) {
      if (move == actions[i]) {
        return amount[i];
      }
    }
    return -1;
  }

  public double getAmountLimit(int move) {
    for (int i = 0; i < actions.length; i++) {
      if (move == actions[i]) {
        return amount_limit[i];
      }
    }
    return -1;
  }

  public String toString() {
	  StringBuilder sb = new StringBuilder();
	  sb.append(super.toString());
	  sb.append(" ");
	  sb.append("(");
	  sb.append(minBet);
	  sb.append(":");
	  sb.append(maxBet);
	  sb.append(") Actions=");
	  
//    String str = super.toString() + " " + "(" +
//        minBet + ":" +
//        maxBet + ") Actions=";
    for (int i = 0; i < actions.length; i++) {
	  sb.append(actions[i]);
  	  sb.append(":");
  	  sb.append(amount[i]);
  	  sb.append("~");
  	  sb.append(amount_limit[i]);
  	  sb.append(",");
      //str += actions[i] + ":" + amount[i] + "~" + amount_limit[i] + ",";
    }
    return sb.toString();
  }

  public void handleAction(ActionVisitor v) {
    v.handleBettingAction(this);
  }

}
