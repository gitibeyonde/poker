package com.poker.game.poker;


public class GameSummaryResponse
    extends PokerResponse {

  StringBuilder buf = new StringBuilder();

  public GameSummaryResponse(Poker g) {
    super(g);
    buf.append(miniHeader());
    buf.append(playerDetails());

    //set the details appropriately
    broadcast(null, buf.toString());

  }

}
