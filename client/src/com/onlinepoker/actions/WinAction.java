package com.onlinepoker.actions;

import com.golconda.game.util.Card;


public class WinAction
    extends StageAction {
  private String name;
  private double pot;
  private String potName;
  private Card[] combination;
  private String winMsg;
  private String str;
  private String[][] openHands;
  private int betRound;

  public WinAction
      (int target, String name, double pot, Card[] combination, String winMsg, String[][] openHands, int betrnd) {
    super(WIN, target);
    this.name = name;
    this.pot = pot;
    this.combination = combination;
    this.winMsg = winMsg;
    str = "";
    this.openHands = openHands;
    this.betRound = betrnd;
  }

  public String[][] getOpenHands() {
    return openHands;
  }
  
  public int getBetRound(){
	  return betRound;
  }

  public String getName() {
    return name;
  }

  public double getPot() {
    return pot;
  }

  public Card[] getCombination() {
    return combination;
  }

  public String getCombinationString() {
    for (int i = 0; i < combination.length; i++) {
      str += combination[i] + "  ";
    }
    return str;
  }

  public String getWinMsg() {
    return winMsg;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("WIN: ")
        .append(name)
        .append(" ")
        .append(pot)
        .append(" with ")
        .append(combination)
        .append(" Hand Strength ");
    //                  .append(winMsg);
    return s.toString();
  }

  public void setPotName(String potName){
    this.potName = potName;
  }

  public String getPotName(){
    return potName;
  }

}
