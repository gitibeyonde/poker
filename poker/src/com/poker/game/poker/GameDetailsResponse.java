package com.poker.game.poker;

import com.poker.common.interfaces.SitnGoInterface;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker.LastLeft;
import com.poker.game.poker.pokerimpl.TermPokerPool;


public class GameDetailsResponse extends PokerResponse {

  StringBuilder buf = new StringBuilder();

  public GameDetailsResponse(Poker g) {
    super(g);
    buf.append(header()).append(registeredPlayerDetail());
    if (g._inProgress) {
      buf.append("dealer-pos=").append(g.dealer() == null ? -1 : g.dealer().pos()).
          append(",").append(lastMoveDetails()).append(potDetails()).append(
          communityCards());
    }
    buf.append(playerDetails());
    if (g.type().isSitnGo()) {
      SitnGoInterface sin = (SitnGoInterface) g;
      buf.append("state=");
      buf.append(sin.state());
      if (sin.state() == sin.TABLE_CLOSED) {
        PokerPresence[] winners = sin.winners();
        buf.append(",winners=");
        for (int i = winners.length - 1, j = 1; i >= 0; i--, j++) {
          buf.append(j).append("|").append(sin.prize(j, g._maxPlayers)).append(
              "|").append(winners[i].name()).append("`");
        }
      }
    }

    //set the details appropriately
    if (g.inquirer() == null) {
      broadcast(_allPlayers, buf.toString());
    }
    else { // check if it had left recently
      LastLeft ll;
      if ((ll = g.isLastLeft(g.inquirer())) != null && g.maxBet() <= 0) {
        buf.append("llworth=").append(ll._chips);
      }

      setCommand(g.inquirer(), buf.toString());

      if (g._inProgress && g.validatePresenceInGame(g.inquirer().name())) {
        setCommand(g.inquirer(), playerTargetPosition(g.inquirer()).toString());
        setCommand(g.inquirer(), playerHandDetails(g.inquirer()).toString());
      }

    }
  }

  public GameDetailsResponse(Poker g, boolean show_last_move) {
    super(g);
    buf.append(miniHeader()).append(registeredPlayerDetail());

    if (g._inProgress) {
      buf.append("dealer-pos=").append(g.dealer() == null ? -1 : g.dealer().pos()).
          append(",").append(potDetails()).append(communityCards());
      if (show_last_move) {
        buf.append(lastMoveDetails());
        g.resetLastMove();
      }
      else {
        buf.append("last-move=-1|none|-9,");
      }
    }
    buf.append("next-move=-1|wait|-9,");

    buf.append(playerDetails());
    if (g.type().isSitnGo()) {
      SitnGoInterface sin = (SitnGoInterface) g;
      buf.append("state=");
      buf.append(sin.state());
      if (sin.state() == sin.TABLE_CLOSED) {
        PokerPresence[] winners = sin.winners();
        buf.append(",winners=");
        for (int i = winners.length - 1, j = 1; i >= 0; i--, j++) {
          buf.append(j).append("|").append(sin.prize(j, g._maxPlayers)).append(
              "|").append(winners[i].name()).append("`");
        }
      }
    }
    broadcast(_allPlayers, buf.toString());

    if (g._inProgress) {
      for (int j = 0; j < _allPlayers.length; j++) {
        setCommand(_allPlayers[j],
                   playerTargetPosition(_allPlayers[j]).toString());
      }

      PokerPresence[] actives = (PokerPresence[]) g.activePlayersIncludingAllIns();
      for (int j = 0; j < actives.length; j++) {
        setCommand(actives[j], playerHandDetails(actives[j]).toString());
      }
    }
  }
  

}
