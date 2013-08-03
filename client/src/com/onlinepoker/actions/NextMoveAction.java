package com.onlinepoker.actions;

import com.golconda.game.util.ActionConstants;

public class NextMoveAction extends Action {
	
  /**
	 * 
	 */
	private static final long serialVersionUID = -7023463512199589511L;

public NextMoveAction(int pos) {
		super(ActionConstants.NEXT_MOVE, ACTION_TYPE_BETTING, pos);
		// TODO Auto-generated constructor stub
	}

	  public void handleAction(ActionVisitor v) {
	    v.handleBettingAction(this);
	  }

}
