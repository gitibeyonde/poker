package com.poker.game.poker;

public class GameOverResponse
    extends PokerResponse {

  StringBuilder buf = new StringBuilder();

  // dummy response to log the winner

  public GameOverResponse(Poker g) {
    super(g);
    lastMoveDetails();
    log();
    logGameOver();
  }


  // Game over in opt-out stage

}
