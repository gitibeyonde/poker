package com.golconda.game;

import com.golconda.db.DBPlayer;

import java.io.Serializable;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class Player {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(Player.class.getName());

  protected String _name;
  protected String _avatar;
  protected String _city;
  protected int _gender;
  protected int _rank;
  protected String _session;
  boolean _isAuthenticated = false;
  protected ConcurrentHashMap _presence_registry;
  public DBPlayer _dbPlayer;
  protected boolean _shill = false;
  protected int win_loss_violated = 0;
  protected int _votes;
  
  public Player(){}
  
  public Player(String session){
      _session = session;
      _name="";
     _presence_registry = new ConcurrentHashMap();
  }
    
  public void attach(String name, DBPlayer dbp) {
    _name = name;
    _dbPlayer = dbp;
    _votes = 3;
  }

    // ONLY FOR TESTING
  public Player(String name, double playChips, double realChips) {
    _name = name;
    _presence_registry = new ConcurrentHashMap();
    _votes = 3;
    _dbPlayer = new DBPlayer(playChips, realChips);
    new Exception("Use this constructor only for testing").printStackTrace();
  }

public String name() {return _name;}public void name(String name) {this._name = name;}

public void gender(int gender) {_gender = gender;}public int gender() {return _gender;}
public void avatar(String avtr) {_avatar = avtr;}public String avatar() {return _avatar;}
public void city(String avtr) {_city = avtr;}public String city() {return _city;}
public void rank(int rank) {_rank = rank;}public int rank() {return _rank;}
public void shill(boolean isbot) {_shill = isbot;}public boolean shill() {return _shill;}
public String session(){ return _session; }


  public void winLossViolated(int i) {
    win_loss_violated = i;
    //set win loss violated for all presences
    if (win_loss_violated != 0) {
      for (Enumeration enumt = _presence_registry.elements();
           enumt.hasMoreElements(); ) {
        Presence p = (Presence)enumt.nextElement();
        if (Game.game(p.getGameName()).type().isReal() &&
            !Game.game(p.getGameName()).type().isTourny()) { //if real game set violation
          p.setWinLossViolated();
        }
      }
    }
  }

  public boolean areVotesAvailable() {
    return _votes > 0;
  }

  public void decrVote() {
    _votes--;
  }

  public int winLossViolated() {
    return win_loss_violated;
  }

  public int getLimitViolation() {
    return _dbPlayer.getLimitViolation();
  }

  public DBPlayer getDBPlayer() {
    return _dbPlayer;
  }

  public void setDBPlayer(DBPlayer p) {
    _dbPlayer = p;
  }

  public Presence presence(String tid) {
    return (Presence) _presence_registry.get(tid);
  }

  public Presence createPresence(String tid) {
    Presence p = (Presence) _presence_registry.get(tid);
    if (p == null) {
      p = new Presence(tid, this);
    }
    if (win_loss_violated != 0 && Game.game(p.getGameName()).type().isReal() &&
        !Game.game(p.getGameName()).type().isTourny()) { //if real game set violation
      p.setWinLossViolated();
    }
    _presence_registry.put(tid, p);
    return p;
  }

  public void movePresence(Presence p, String tid) {
    removePresence(p);
    p.setGameName(tid);
    _presence_registry.put(p.getGameName(), p);
  }

  public void removePresence(Presence p) {
    _presence_registry.remove(p.getGameName());
  }

  public int presenceCount() {
    return _presence_registry.size();
  }

  public double realWorth() {
    return _dbPlayer.getRealChips();
  }


  public double playWorth() {
    return _dbPlayer.getPlayChips();
  }

  @Override
  public String toString() {
    return "name=" + _name + ", " + (_shill ? "bot" : "real");
  }

 
}
