package com.onlinepoker.actions;

import com.onlinepoker.Pot;
import com.onlinepoker.TotalBet;

public class TotalBetAction extends TableServerAction {

	private TotalBet tb;
	
	public TotalBetAction(TotalBet tb) {
		super(TOTALBET_INFO);
		this.tb = tb;
		
	}

	public TotalBet getTotalBet() { return tb; }

	
}
