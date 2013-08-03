package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.Move;
import com.golconda.game.resp.Response;

import com.poker.game.PokerPresence;

import java.util.logging.Logger;


public class MessagingImpl
    extends AbstractPlayerMessageBase
    implements Move {

  // set the category for logging
  static Logger _cat = Logger.getLogger(MessagingImpl.class.getName());

  public MessagingImpl(String gameId, PokerPresence p, String s) {
    super(gameId, p);
    this.message = s;
  }

  public String message() {
    return message;
  }

  public byte id() {
    return 9;
  }

  public double amount(){return 0;}
  public long move(){return -11;}
    
    
  public Response interpret() {
    Game g = Game.game(gameId());
   return g.chat(player(), message);
  }

  public synchronized int mesgid() {
    return++mesgId;
  }

  String message;

  static volatile int mesgId;
}
