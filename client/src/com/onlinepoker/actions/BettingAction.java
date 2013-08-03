package com.onlinepoker.actions;


/**
 * This is the betting Action.
 * @author Yuriy Guskov
 */
public class BettingAction extends Action {

	private double bet;
	private boolean isAllIn = false;
        private String md="";

	public BettingAction(int id, int target, double bet, boolean isAllIn) {
		super(id, ACTION_TYPE_BETTING, target);
		this.bet = bet;
		this.isAllIn = isAllIn;
	}

	public BettingAction(int id, int target, double bet) {
		this(id, target, bet, false);
	}
        
        public BettingAction(int id, int target, double bet, String mdet) {
                 this(id, target, bet, false);
                 md = mdet;
         }

	public BettingAction(int id, int target) {
		this(id, target, 0, false);
	}

        public void setType(int type) {
          this.type = type;
        }

	public double getBet() { return bet; }

	public boolean isAllIn() { return isAllIn; }


        public String getMD() { return md;}

        public String toString() {
        	StringBuilder sb = new StringBuilder();
      	    sb.append(super.toString());
      	    sb.append(" ");
      	    sb.append(getBet());
      	    sb.append(" (");
      	    sb.append(target);
      	    sb.append(") ");
      	    sb.append(md);
      	    return sb.toString();
//                return super.toString() + " " + getBet() +
//                " (" + target + ") " + md;
        }

	public void handleAction(ActionVisitor v) {
		v.handleBettingAction(this);
	}

}
