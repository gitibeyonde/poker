package com.poker.game.poker;

import com.poker.game.PokerPresence;


public class InformWaiterResponse
    extends PokerResponse {

  public InformWaiterResponse(PokerPresence waiter, Poker g, int pos) {
    super(g);
    buf.append(miniHeader());
    buf.append(playerDetails());
    if (waiter != null) {
      setCommand(waiter, buf.toString() + nextMoveJoin(waiter, pos) + "target-position=-9");
      _cat.warning("Waiter = " + waiter + ", cmd=" + buf.toString() + nextMoveJoin(waiter, pos) + "target-position=-9");
    }

  }
  StringBuilder buf = new StringBuilder();
}
