package com.poker.game.gamemsgimpl;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.Move;
import com.golconda.game.resp.Response;

import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;
import com.poker.game.poker.pokerimpl.TermHoldem;

import java.util.logging.Logger;


public class MoveImpl
    extends AbstractPlayerMessageBase
    implements Move {

  // set the category for logging

  static Logger _cat = Logger.getLogger(MoveImpl.class.getName());

  public MoveImpl(String gameId, PokerPresence p, long move, double amt) {
    super(gameId, p);
    this.move = move;
    this.amt = amt;
  }

  public MoveImpl(String gameId, int grid, PokerPresence p, long move, double amt) {
    super(gameId, p);
    this.move = move;
    this.amt = amt;
    this.grid = grid;
  }

  public MoveImpl(String gameId, PokerPresence p, long move, double amt, String md) {
    super(gameId, p);
    this.move = move;
    this.amt = amt;
    this.move_details = md;
  }

  public long move() {
    return move;
  }

  public double amount() {
    return amt;
  }

  public String moveDetails() {
    return move_details;
  }

  public byte id() {
    return 2;
  }
    
    
  public synchronized Response interpret() {
    Game g = Game.game(gameId());
    // poker moves
    if ( (PokerMoves.POKER_MASK & move) > 0) {
      Poker pg = (Poker) g;

      if (!player().isResponseReq()) {
        _cat.warning("Move not expected from this client " + player());
        return pg.illegalMove(player(), move);
      }

      if (grid > 0 && this.grid != pg.grid()) {
        _cat.warning("Move not expected from this client for this hand " + player());
        return pg.illegalMove(player(), move);
      }

      if (!g.checkNextMove(new PokerMoves(move), amt, player())) {
        _cat.severe(" MoveVal=" +
                   new PokerMoves(move == 0 ? PokerMoves.NONE : move).stringValue()
                   + " : " + " amt: " + amt + " Expected moves = "
                   +
                   (g._nextMove != 0 ? new PokerMoves(g._nextMove).stringValue() :
                    "UNKNOWN MOVE") +
                   ", amt " +
                   g._nextMoveAmt[new PokerMoves(move).intIndex()][0] + "-"
                   + g._nextMoveAmt[new PokerMoves(move).intIndex()][1]
                   + " Expected Player = " + pg._nextMovePlayer +
                   "PokerMoves send by " + player());
        return pg.illegalMove(player(), move);
      }

      g.setMarker(player());
      g.setCurrent((PokerPresence) player());

      //the player has responded in stipulated time with a right move reset resp req
      player().unsetResponseReq();

      if (move == PokerMoves.BET) {
        return pg.bet(player(), amt);
      }
      else if (move == PokerMoves.CALL) {
        return pg.call(player(), amt);
      }
      else if (move == PokerMoves.RAISE) {
        return pg.raise(player(), amt);
      }
      else if (move == PokerMoves.FOLD) {
        return pg.fold(player());
      }
      else if (move == PokerMoves.CHECK) {
        return pg.check(player());
      }
      else if (move == PokerMoves.BIG_BLIND) {
        return pg.postBigBlind(player(), amt);
      }
      else if (move == PokerMoves.SMALL_BLIND) {
        return pg.postSmallBlind(player(), amt);
      }
      else if (move == PokerMoves.ANTE) {
        return pg.postAnte(player(), amt);
      }
      else if (move == PokerMoves.ALL_IN) {
        return pg.allIn(player(), amt);
      }
      else if (move == PokerMoves.BET_POT) {
        return pg.betPot(player(), amt);
      }
      else if (move == PokerMoves.OPT_OUT) {
        return pg.optOut(player());
      }
      else if (move == PokerMoves.BRINGIN) {
        return pg.bringIn(player(), amt);
      }
      else if (move == PokerMoves.BRINGINH) {
        return pg.bringIn(player(), amt);
      }
      else if (move == PokerMoves.SBBB) {
        return pg.postSBBB(player(), amt);
      }
      else {
        throw new IllegalArgumentException("Illegal poker move: " + move);
      }
    }
    else {
      throw new IllegalArgumentException("Illegal move: " + move);
    }
  }

  public synchronized int mvid() {
    return++mvId;
  }

  double amt;
  String move_details;

  long move;
  long grid = -1;

  static volatile int mvId;
}
