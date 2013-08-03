package com.poker.game.poker.test;

import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;

import com.poker.game.poker.Poker;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class LoggingObserver
    implements Observer {

  public void update(Observable o, Object arg) {
    //System.out.println( "update o-p" );
    System.out.println(o.getClass().getName() + " -- " + arg.getClass().getName());
    System.out.println(GameStateEvent.stringVal( (GameStateEvent) arg));
    if (arg == GameStateEvent.GAME_BEGIN) {
       Game g = (Game) o;
       if (g instanceof Poker) {
         com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;
         // setup the game run Id
         g.grid(4);
         g.startTime(Calendar.getInstance());
       }
     }

  }
}