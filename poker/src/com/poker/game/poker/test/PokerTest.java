package com.poker.game.poker.test;

import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;
import com.golconda.game.resp.Response;

import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by IntelliJ IDEA. User: aprateek Date: May 24, 2004 Time: 2:48:47 PM To
 * change this template use File | Settings | File Templates.
 */
public class PokerTest {

  public static void p(String s) {
    System.out.print(s + "\n");
  }

  public static void p(StringBuilder buf) {
    p(buf.toString());
  }

  public static void p(String tid, Response r, PokerPresence p) {
    if ( r instanceof com.poker.game.poker.IllegalReqResponse){
      throw new IllegalStateException(r.getCommand(p));
    }
    String clazz = r.getClass().getName();
    p(System.currentTimeMillis() + "-------- Move by: " +
      (p == null ? "NULL" : p.name()));
    p("-------- Move: " + (p == null ? "NONE" : new PokerMoves(p.lastMove()).stringValue()));
    p(clazz);
    PokerPresence[] pl = (PokerPresence[]) r.recepients();
    for (int i = 0; i < pl.length; i++) {
      p("player : name=" + pl[i].name() + ", " + r.getCommand(pl[i]));
    }

  }

  public static void p(Response r) {
    if ( r instanceof com.poker.game.poker.IllegalReqResponse){
      throw new IllegalStateException();
    }
    String clazz = r.getClass().getName();
    p("--------");
    p(clazz);
    PokerPresence[] pl = (PokerPresence[]) r.recepients();
    for (int i = 0; i < pl.length; i++) {
      p("player : name=" + pl[i].name() + ", " + r.getCommand(pl[i]));
    }
    pl = (PokerPresence[]) r.observers();
    p("broadcast: " + r.getBroadcast());
    pl = (PokerPresence[]) r.observers();
    for (int i = 0; i < pl.length; i++) {
      p("Observers: " + pl[i].name());
    }
  }

  public void pp(Response r, PokerPresence p) {
    // do nothing ...
  }

  public void pp(Response r) {
    // do nothing
  }

}
