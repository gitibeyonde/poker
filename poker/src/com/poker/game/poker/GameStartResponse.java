package com.poker.game.poker;

import com.poker.common.interfaces.SitnGoInterface;
import com.poker.game.PokerPresence;


public class GameStartResponse
    extends PokerResponse {

  StringBuilder buf = new StringBuilder();

  public GameStartResponse(Poker g) {
    super(g);
    buf.append(header()).append(potDetails()).append(prevCommunityCards()).append(
        "community-cards=,");
    buf.append(g.reRunCondition() ?
               abDetails().append("dealer-pos=").append(_g.dealer().pos()).
               append(",") :
               new StringBuilder(",next-move=-1|wait|")).append(g._inProgress ? "-5," : "-9,");

    buf.append(lastMoveDetails()).append(playerDetails()).append(winnersDetail());

    if (g.type().isSitnGo()) {
      SitnGoInterface sin = (SitnGoInterface) g;
      buf.append(",state=");
      buf.append(sin.state());
      if (sin.state() == sin.TABLE_CLOSED) {
        PokerPresence[] winners = sin.winners();
        buf.append(",winners=");
        for (int i = winners.length - 1, j = 1; i >= 0; i--, j++) {
          buf.append(j).append("|").append(sin.prize(j, g._maxPlayers)).append("|").append(
              winners[i].name()).append("`");
        }
        if (winners.length > 0){
          buf.deleteCharAt(buf.length() - 1);
        }
      }
    }

    broadcast(_allPlayers, buf.toString());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                 playerTargetPosition(_allPlayers[j]).toString());
    }

    if (g._inProgress)
    	logStart();
  }
}
