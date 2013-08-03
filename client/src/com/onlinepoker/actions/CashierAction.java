package com.onlinepoker.actions;

/**
 * Call to cashier action.
 * @author Halt
 * @author Kom
 */
public class CashierAction extends StageAction {

  double amount = 0;

  public CashierAction(int target, double amount) {
    super(CASHIER, target);
    this.amount = amount;
  }

  public double getAmount() {
    return amount;
  }

  public int getSeat() {
    return target;
  }

  public String toString() {
    return "Call to cashier for " + amount;
  }
}


