package com.poker.game.poker;


public class CollectABResponse
    extends PokerResponse {

  public CollectABResponse(Poker g) {
    super(g);
    buf = new StringBuilder().append(miniHeader());
    buf.append("dealer-pos=").append(g.dealer().pos());
    buf.append(",").append(lastMoveDetails()).append(abDetails()).append(playerDetails());
    broadcast(_allPlayers, buf.toString());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
    }
    log();
  }

  private StringBuilder buf;
}
