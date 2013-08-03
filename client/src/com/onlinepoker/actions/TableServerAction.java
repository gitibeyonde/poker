package com.onlinepoker.actions;

public class TableServerAction extends Action {
	private String _name;
	public TableServerAction(int id, int target) {
		super(id, ACTION_TYPE_TABLE_SERVER, target);
	}
	
	//by rk
	public TableServerAction(int id, int target, String nm) {
		super(id, ACTION_TYPE_TABLE_SERVER, target);
		this._name = nm;
	}
	
	public TableServerAction(int id) {
		this(id, -1);
	}

	public void handleAction(ActionVisitor v) {
		v.handleTableServerAction(this);
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}
	
	

}
