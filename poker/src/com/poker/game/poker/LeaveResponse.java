package com.poker.game.poker;


public class LeaveResponse
    extends PokerResponse {

  public LeaveResponse(Poker g, int pos) {
    super(g);
    buf.append(miniHeader()).append(lastMoveDetails()).append(potDetails());
    buf.append(playerDetails());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                  buf.toString() + "next-move=-1|wait|-12," + playerTargetPosition(_allPlayers[j]).toString());
    }
  }


  StringBuilder buf = new StringBuilder();
}
