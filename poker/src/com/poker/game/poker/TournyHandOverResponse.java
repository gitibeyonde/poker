package com.poker.game.poker;

import com.poker.game.PokerPresence;


public class TournyHandOverResponse
    extends PokerResponse {

  StringBuilder buf = new StringBuilder();

  public TournyHandOverResponse(Poker g, PokerPresence[] showdown_lv) {
    super(g);
    buf.append(miniHeader()).append(prevCommunityCards()).append(potDetails());
    buf.append(new StringBuilder(",next-move=-1|wait|-77,"));

    buf.append(lastMoveDetails()).append(playerDetails()).append(winnersDetail());
    broadcast(g.allPlayers(-1), buf.toString());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                 playerTargetPosition(_allPlayers[j]).toString());
    }
  }
}
