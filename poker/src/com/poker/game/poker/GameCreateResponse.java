package com.poker.game.poker;


public class GameCreateResponse
    extends PokerResponse {

  StringBuilder buf = new StringBuilder();

  public GameCreateResponse(Poker g) {
    super(g);
    buf.append(header()).append(",").append("size-players=").append(g.
        maxPlayers()).append(",").append("min-players=").append(g.minPlayers()).
        append(",").append("average-pot=").append(g.averagePot()).append(",").append(
        "min-raise=").append(g.minBet()).append(",").append("size-bet=").append(
        g.maxBet()).append(",").append("size-rounds=").append(g.maxRounds());
    broadcast(_allPlayers, buf.toString());
  }

}
