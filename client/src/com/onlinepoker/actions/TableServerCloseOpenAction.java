package com.onlinepoker.actions;

import com.golconda.message.GameEvent;
import com.onlinepoker.Pot;

public class TableServerCloseOpenAction extends TableServerAction {

	private GameEvent ge;
	private int pos;
	private String old_tid;
	

	public TableServerCloseOpenAction(String old_tid, GameEvent ge) {
		super(MANUAL_GRACEFUL_SHUTDOWN);
		this.ge = ge;
		this.pos = pos;
		this.old_tid = old_tid;
		
	}


	public GameEvent getGe() {
		return ge;
	}


	public void setGe(GameEvent ge) {
		this.ge = ge;
	}


	public int getPos() {
		return pos;
	}


	
	public String getOldTid() {
		return old_tid;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ge=");
		sb.append(ge);
		sb.append(", old_tid=");
		sb.append(old_tid);
		sb.append(", pos");
		sb.append(pos);
		return sb.toString();
//		return "ge="+ge+", old_tid="+old_tid+", pos"+pos;
	}


	
	

	
}
