package com.poker.game.poker;

import com.poker.game.PokerPresence;


public class MoveResponse
    extends PokerResponse {
  public MoveResponse(Poker g) {
      super(g);
      buf.append(miniHeader());

      buf.append("dealer-pos=").append(g.dealer().pos()).append(",").append(
          lastMoveDetails()).append(nextMoveDetails()).append(potDetails()).
          append(communityCards());
        buf.append("round=").append(g.bettingRound()).append(",");

      broadcast(_allPlayers, buf.append(playerDetails()).toString());

      for (int j = 0; j < _allPlayers.length; j++) {
        setCommand(_allPlayers[j],
                   playerTargetPosition(_allPlayers[j]).toString());
      }

      PokerPresence[] actives = (PokerPresence[]) g.activePlayersIncludingAllIns();
      for (int j = 0; j < actives.length; j++) {
        setCommand(actives[j],
                   playerHandDetails(actives[j]).toString());
      }

      log();
      if (g._resetLastPokerMoves) {
        _cat.finest("Resetting last moves...");
        g.resetLastMove();
        g._resetLastPokerMoves = false;
      }
  }
  
  StringBuilder buf = new StringBuilder();
}
