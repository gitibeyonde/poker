package com.poker.game.poker;

import com.agneya.util.Configuration;

import com.golconda.db.DBException;
import com.golconda.game.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Observer;
import java.util.Timer;


public class TournyController {
	ConcurrentHashMap _map;
  static Object _obj = new Object();
  static TournyController _tc = null;

  public static TournyController instance() {
    if (_tc == null) {
      synchronized (_obj) {
        if (_tc == null) {
          _tc = new TournyController();
          return _tc;
        }
        else {
          return _tc;
        }
      }
    }
    return _tc;
  }

  private TournyController() {
    _map = new ConcurrentHashMap();
    int timer = 20000;
    try {
      timer = Configuration.instance().getInt("Tourny.Schedular.Interval");
    }
    catch (Exception e) {

    }
    Timer t = new Timer();
    t.schedule(new TournySchedular(_map), 0, timer);
  }

  public Tourny[] listAll() {
    return (Tourny[]) _map.values().toArray(new Tourny[] {});
  }

  public Tourny getTourny(String id) {
	  if (_map == null)return null;
    return (Tourny) _map.get(id);
  }

  public boolean tournyOver(int id){
    return ((Tourny) _map.get(id + "")).tournyOver();
  }

  public String addTourny(String name, int type, int limit, int tourbo, int[] schedule, double buy_ins,
                       double fees, int chips, int maxP, int di, int ri, int ji, Observer lob) throws DBException {
    Tourny t = new Tourny(name, type, limit, tourbo, schedule, buy_ins, fees, chips, maxP, di, ri, ji, lob);
    String id = t.initNewTourny();
    if (id==null)return null;
    _map.put(id, t);
    return id;
  }

  public String addTourny(Tourny t) {
    _map.put(t._name, t);
    return t._name;
  }

  public void removeTourny(String tid) {
    _map.remove(tid);
  }

  public boolean registerPlayer(String tid, Player p) {
    return ( (Tourny) _map.get(tid)).register(p);
  }

}
