package com.onlinepoker.actions;

public class SimpleAction extends Action {
	
	public SimpleAction(int id, int target) {
		super(id, ACTION_TYPE_SIMPLE, target);
	}
	
	public SimpleAction(int id) {
		this(id, -1);
	}
	
	public SimpleAction(int id, int target, int delayMills, String desc) {
		super(id, ACTION_TYPE_SIMPLE, target);
		this.delayMills = delayMills;
		this.desc = desc;
	}
	
	public void handleAction(ActionVisitor v) {
		v.handleSimpleAction(this);
	}
	
	private int delayMills;
	public int getDelayMills() {
		return delayMills;
	}
	
	private String desc;
	public String getDesc(){
		return desc;
	}

}
