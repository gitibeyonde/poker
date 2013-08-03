package com.onlinepoker.util;

import com.poker.game.util.Hand;

public class Winner {

	private String potName;
	private String winnerName;
	private double amount;
	private String combination;
	private String[] acombination;
	private long lcombination;
	
	public String getPotName() {
		return this.potName;
	}

	public void setPotName(String potName) {
		this.potName = potName;
	}	
	
	public String getWinnerName() {
		return this.winnerName;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}	
	
	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}	
	
	public String getCombination() {
		return this.combination;
	}
	public String[] getACombination() {
		return this.acombination;
	}
	public long getLCombination() {
		return this.lcombination;
	}
	public Winner(){
		
	}
	
	public Winner(String potName, String winnerName, double amount, String combi){
		setPotName(potName);
		setWinnerName(winnerName);
		setAmount(amount);
		combination=combi;
		lcombination = Hand.getHandFromStr3(combi);
		acombination = combi.split("'");
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(winnerName)
		.append(", ")
		.append(amount)
		.append(", ")
		.append(combination);
		return sb.toString();
//		return winnerName + ", " + amount + ", " + combination;
	}
}
	
	
	