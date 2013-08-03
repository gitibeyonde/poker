package com.poker.game.poker;

import com.agneya.util.Base64;
import com.golconda.game.Presence;


public class MessagingResponse
    extends PokerResponse {

  public MessagingResponse(Poker g, Presence p, String message) {
    super(g);
    buf.append("type=chat,name=").append(g.name());
    buf.append(",player=").append(p.name());
    buf.append(",message=").append(message);
    buf.append(",tid=").append(g.name());
    /*
     Assume that all the players on the table will be interested, except those that
       have explicityly opted out.
     */
    //
    broadcast(_allPlayers/*g.table.allCompanions(p)*/, buf.toString());
  }

  public MessagingResponse(Poker g, String message) {
    super(g);
    buf.append("type=broadcast,name=");
    buf.append(g.name());
    buf.append(",message=").append(message);
    buf.append(",tid=").append(g.name());
    /*
     Assume that all the players on the table will be interested, except those that
       have explicityly opted out.
     */
    //
    broadcast(_allPlayers, buf.toString());
  }

  StringBuilder buf = new StringBuilder();
}
