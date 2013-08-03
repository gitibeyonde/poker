package com.poker.game.poker;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TournySchedular
extends TimerTask
{
  // set the category for logging
  static Logger _cat = Logger.getLogger(TournySchedular.class.getName());

  ConcurrentHashMap _map;

  public TournySchedular(ConcurrentHashMap map) {
    _map=map;
  }

  public void run(){
    Enumeration trn = _map.elements();
    while(trn.hasMoreElements() ){
      try {
        ( (Tourny) trn.nextElement()).stateSwitch();
      }
      catch (Throwable t){
        t.printStackTrace();
        _cat.log(Level.WARNING, "Tourny failed ", t);
      }
    }
  }

}
