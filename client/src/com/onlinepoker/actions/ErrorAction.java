package com.onlinepoker.actions;

public class ErrorAction extends Action {

	public ErrorAction(int id, int target) {
		super(id, ACTION_TYPE_ERROR, target);
	}
	
	public ErrorAction(int id) {
		this(id, -1);
	}

	public void handleAction(ActionVisitor v) {
		v.handleErrorAction(this);
	}

}
