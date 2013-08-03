package com.onlinepoker.actions;

public class SidebarInfoAction extends TableServerAction {

    private String info;
	
	public SidebarInfoAction(String info) { 
		super(0);//SIDEBAR_INFO);
		this.info = info;
	}

	public String getInfo() { return info; }
	
}
