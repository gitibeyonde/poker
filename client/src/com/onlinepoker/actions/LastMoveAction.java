package com.onlinepoker.actions;


/**
 * This is the last move action.
 * @author Yuriy Guskov
 */
public class LastMoveAction extends Action {

        private double cur_bet, bet, amt_at_table;
        private boolean _me=false;

        public LastMoveAction(int id, int target, double cur_bet, double bet, double amt_at_table, boolean me) {
                super(id, ACTION_TYPE_LASTMOVE, target);
                this.bet = bet;
                this.cur_bet = cur_bet;
                this.amt_at_table = amt_at_table;
                _me = me;
        }

        public void setType(int type) {
          this.type = type;
        }

        public double getCurrentBet() { return cur_bet; }
        public double getRoundBet() { return bet; }
        public double getAmountAtTable() { return amt_at_table; }
        public boolean isMe() {return _me; };


        public String toString() {
        	StringBuilder sb = new StringBuilder();
        	sb.append(super.toString());
        	sb.append(" ");
        	sb.append(getCurrentBet());
        	sb.append(", pos= ");
        	sb.append(target);
        	sb.append(", round-bet=");
        	sb.append(bet);
        	sb.append(", tableAmt=");
        	sb.append(amt_at_table);
        	sb.append(", id=");
        	sb.append(id);
        	return sb.toString();
//                return super.toString() + " " + getCurrentBet() +
//                ", pos= " + target +", round-bet=" + bet + ", tableAmt=" + amt_at_table+", id="+id;
        }

        public void handleAction(ActionVisitor v) {
                v.handleBettingAction(this);
        }

}
