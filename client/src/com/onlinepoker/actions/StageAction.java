package com.onlinepoker.actions;

public class StageAction extends Action {

	public StageAction(int id, int target) {
		super(id, ACTION_TYPE_STAGE, target);
	}
	
	public StageAction(int id) {
		this(id, -1);
	}

	public void handleAction(ActionVisitor v) {
		v.handleStageAction(this);
	}

}
