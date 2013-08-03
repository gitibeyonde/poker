package com.poker.game.poker;

import com.poker.game.PokerMoves;


public class IllegalReqResponse
    extends PokerResponse {

  public IllegalReqResponse(Poker g) {
    super(g);
    buf.append(miniHeader()).append(lastMoveDetails()).append(potDetails());
    buf.append(playerDetails()).append("illegal-move=").append(new PokerMoves(g.inquirer().lastMove()).
        stringValue());
    // get the first waiter and ask him to join
    setCommand(g.inquirer(), buf.toString());
  }

  StringBuilder buf = new StringBuilder();
}
