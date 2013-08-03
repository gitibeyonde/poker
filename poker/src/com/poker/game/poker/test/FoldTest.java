package com.poker.game.poker.test;

import com.golconda.game.Game;
import com.golconda.game.gamemsg.Message;
import com.golconda.game.resp.Response;

import com.poker.game.PokerMoves;
import com.poker.game.PokerPlayer;
import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.GameDetailsImpl;
import com.poker.game.gamemsgimpl.MoveImpl;
import com.poker.game.gamemsgimpl.ObserveGameImpl;
import com.poker.game.gamemsgimpl.ObserverToPlayerImpl;
import com.poker.game.poker.pokerimpl.Holdem;


public class FoldTest
    extends PokerTest {

  PokerPlayer pl0, pl1, pl2, pl3, pl4, pl5, pl6, pl7;
  PokerPresence p0, p1, p2, p3, p4, p5, p6, p7;

  public void initTable() {
    // message to create a new poker with id 1
    // @todo : max rounds chaged to 2 from 4

    String aff[] = {
        "admin"};
    Holdem om = new Holdem( "Holdem", 2, 10, 2, 2, aff, null,
                           new LoggingObserver());
    om.setArgs(200, 400, 100, 200);
    // Game.create
    // create players. do not have to join the poker at this stage
    Response r;

    pl0 = new PokerPlayer("P0", 12000, 0);
    pl1 = new PokerPlayer("P1", 12000, 0);
    pl2 = new PokerPlayer("P2", 12000, 0);
    pl3 = new PokerPlayer("P3", 12000, 0);
    pl4 = new PokerPlayer("P4", 12000, 0);
    pl5 = new PokerPlayer("P5", 12000, 0);
    pl6 = new PokerPlayer("P6", 12000, 0);
    pl7 = new PokerPlayer("P7", 12000, 0);
    p0 = pl0.createPresence("Holdem");
    p0.setPos(0);
    p1 = pl1.createPresence("Holdem");
    p1.setPos(1);
    p2 = pl2.createPresence("Holdem");
    p2.setPos(2);
    p3 = pl3.createPresence("Holdem");
    p3.setPos(3);
    p4 = pl4.createPresence("Holdem");
    p4.setPos(4);
    p5 = pl5.createPresence("Holdem");
    p5.setPos(5);
    p6 = pl6.createPresence("Holdem");
    p6.setPos(6);
    p7 = pl7.createPresence("Holdem");
    p7.setPos(7);

    pp(Game.handle(new ObserveGameImpl(p0, "Holdem")), p0);
    pp(Game.handle(new ObserveGameImpl(p2, "Holdem")), p2);
    pp(Game.handle(new ObserveGameImpl(p1, "Holdem")), p1);
    pp(Game.handle(new ObserveGameImpl(p3, "Holdem")), p3);
    pp(Game.handle(new ObserveGameImpl(p4, "Holdem")), p4);
    pp(Game.handle(new ObserveGameImpl(p5, "Holdem")), p5);
    pp(Game.handle(new ObserveGameImpl(p6, "Holdem")), p6);
    pp(Game.handle(new ObserveGameImpl(p7, "Holdem")), p7);

    p("Holdem", Game.handle(new ObserverToPlayerImpl("Holdem", p1, 12000)), p1);
    p("Holdem", Game.handle(new ObserverToPlayerImpl("Holdem", p2, 12000)), p2);
    Message m2 = new GameDetailsImpl("Holdem", p1);
    r = Game.handle(m2);
    // All new. Post BB. Dealer is 1. Order 1, 2, 3.
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.SMALL_BLIND, 100.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.BIG_BLIND, 200.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.CALL, 100.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CHECK, 0.0)), p2);
    // deal cards to players

    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.FOLD, 0.0)), p2);

    System.out.println(
        "\n\n\n\n 1111111###############NEW GAME##############\n");
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.SMALL_BLIND, 100.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.BIG_BLIND, 200.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CALL, 100.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.CHECK, 0.0)), p1);
    // deal cards to players

    p("Holdem", Game.handle(new ObserverToPlayerImpl("Holdem", p0, 12000)), p0);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.BET, 200.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CALL, 200.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.BET, 400.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CALL, 400.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.BET, 400.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CALL, 400.0)), p2);

    System.out.println("\n\n\n\n 22222###############NEW GAME##############\n");

    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.SMALL_BLIND, 100.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.BIG_BLIND, 200.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.CALL, 100.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CHECK, 0.0)), p2);

    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.FOLD, 0.0)), p2);

    System.out.println("\n\n\n\n 33333###############NEW GAME##############\n");

    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.SMALL_BLIND, 100.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p0, PokerMoves.BIG_BLIND, 200.0)), p0);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.CALL, 200.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.CALL, 100.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p0, PokerMoves.CHECK, 0.0)), p0);

    //p2.setDontMuck();
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.BET, 200.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p0, PokerMoves.FOLD, 0.0)), p0);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.CALL, 200.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.BET, 400.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.CALL, 400.0)), p1);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p2, PokerMoves.BET, 400.0)), p2);
    p("Holdem", Game.handle(new MoveImpl("Holdem", p1, PokerMoves.FOLD, 0.0)), p1);
  }

  public static void main(String argv[]) throws Exception {
    new FoldTest().initTable();
  }

}
