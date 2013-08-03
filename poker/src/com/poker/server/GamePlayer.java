package com.poker.server;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;

import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;
import com.golconda.db.LoginSession;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.db.PlayerPreferences;
import com.golconda.game.resp.Response;

import com.poker.common.message.ResponseBuyChips;
import com.poker.game.PokerGameType;
import com.poker.game.PokerPlayer;
import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.GameDetailsImpl;
import com.poker.game.gamemsgimpl.LeaveGameImpl;
import com.poker.game.gamemsgimpl.LeaveWatchImpl;
import com.poker.game.gamemsgimpl.ObserveGameImpl;
import com.poker.game.gamemsgimpl.ObserverToPlayerImpl;
import com.poker.nio.Client;
import com.poker.nio.Handler;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GamePlayer
    extends PokerPlayer implements Client {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(GamePlayer.class.getName());

  /**
   * boolean: true if client is authenticated
   */
  private boolean _isAuthenticated = false;
  private boolean _dead = false;
  private Handler _handler;
  protected LoginSession _loginSession;
  static Configuration _conf;
  static boolean _logPlayGames = false;
  protected static ConcurrentHashMap _gp_registry;

  static {
    _gp_registry = new ConcurrentHashMap();
  }

  public GamePlayer(Handler h) {
    super(h._id);
    _handler = h;
    _session = h._id;
    try {
      _conf = Configuration.instance();
      _logPlayGames = _conf.getBoolean("Auditor.Log.PlayGame");
    }
    catch (ConfigurationException e) {
      _cat.log(Level.WARNING, "Configuration exception ", e);
    }
  }

  public static Enumeration getGPList() {
    return _gp_registry.elements();
  }

  public int disableChat() {
    int new_pref = _dbPlayer.getPreferences().intVal() | PlayerPreferences.DISABLE_CHAT;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int enableChat() {
    int new_pref = _dbPlayer.getPreferences().intVal() & ~PlayerPreferences.DISABLE_CHAT;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int ban() {
    int new_pref = _dbPlayer.getPreferences().intVal() | PlayerPreferences.BANNED_PLAYER;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int unban() {
    int new_pref = _dbPlayer.getPreferences().intVal() &
        ~PlayerPreferences.BANNED_PLAYER;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int getPreferences() {
    return _dbPlayer.getPreferences().intVal();
  }

  public void setHandler(Handler h) {
    _handler = h;
    _session = h._id;
  }

  public boolean isDisconnected() {
    return _handler.isDisconnected();
  }

  public void attach(String name, DBPlayer dbp) {
    super.attach(name, dbp);
	 _cat.info("Creating a game player " + name);
    _gp_registry.put(name, this);
  }

  public void name(String name) {
    this._name = name;
  }

  public static GamePlayer getPlayer(String name) {
    return (GamePlayer) _gp_registry.get(name);
  }

  public Response addWatch(String tid) {
    PokerPresence p = createPresence(tid);
    _cat.finest("Adding watch " + p);
    p.unsetDisconnected();
    if (p.isPlayer()) {
      /**if (p.isResponseReq()) {
        return com.golconda.game.Game.handle(new GameDetailsImpl(tid, p)); // TODO: send a proper error message
      }
      else {**/
        return com.golconda.game.Game.handle(new GameDetailsImpl(tid, p));
      //}
    }
    else {
      return com.golconda.game.Game.handle(new ObserveGameImpl(p, tid));
    }
  }

  public Response addGame(PokerPresence p, int pos, double amt) throws DBException {
    _cat.info("Setting " + p + " amt = " + amt);
    p.setPos(pos);
    String gid = p.getGameName();

    if (((PokerGameType)Game.game(gid).type()).isMTTTourny()) {
      p.setMTTPresence(gid);
    }
    else if (((PokerGameType)Game.game(gid).type()).isSitnGo()) {
      p.setSNGPresence(gid);
    }
    _cat.info("Setting " + p + " in Game = " + gid);
    return com.golconda.game.Game.handle(new ObserverToPlayerImpl(gid, p, amt));
  }

  public Response leaveGameOnly(PokerPresence p, boolean timeout) {
    if (p.isPlayer()) {
      //Game g = Game.game(p.getGID());
      if (!p.isTournyPresence()) {
        //p.unsetResponseReq();
        p.setDisconnected();
        Response r = com.golconda.game.Game.handle(new LeaveGameImpl(p.getGameName(), p, timeout));
        try {
          if (!timeout) { // if timeout do not convert the player to observer
            p.leaveTable(new ModuleType( ModuleType.POKER));
            addWatch(p.getGameName());
          }
          _cat.info("Removing " + p);
        }
        catch (Exception e) {
          _cat.log(Level.WARNING, "The db player is null while removing chips " + p);
        }
        return r;
      }
      else {
        _cat.info("Keeping " + p + " for tourny " + p.getGameName());
        Response r = com.golconda.game.Game.handle(new LeaveGameImpl(p.getGameName(), p, timeout));
        return r;
      }
    }
    else {
      return new com.poker.game.poker.GameDetailsResponse( (com.poker.game.poker.Poker) Game.game(p.getGameName()));
    }
  }

  /**
   * This request is made from turnDeaf and is a explicit request that player wants
   * to leave this game
   */
  public Response leaveGameAndWatch(PokerPresence p) {
	if (p.isQickFold()){
	  _cat.warning("------------------not removing player as he is quick fold ");
	  return null;
	}
	else if (p.isTournyPresence()) {
      _cat.finest("Tourny presence left " + p);
      p.setDisconnected();
      Response r = com.golconda.game.Game.handle(new LeaveGameImpl(p.getGameName(), p, false));
      return r;
    }
    else {
      if (p.isPlayer()) {
        p.setDisconnected();
        _cat.info("Removing " + p + " from game " + p.getGameName());
        Response r = com.golconda.game.Game.handle(new LeaveGameImpl(p.getGameName(), p, false));
        p.leaveTable(new ModuleType( ModuleType.POKER));
        p.unsetState();
        _presence_registry.remove(this);
        return r;
      }
      else {
        return leaveWatch(p);
      }
    }
  }

  public Response leaveWatch(PokerPresence p) {
    if (p.isQickFold()){
    	 _cat.warning("------------------not removing player as he is quick fold ");
    	 return null;
    }
    else if (p.isObserver() || p.isWaiter()) {
      _cat.info("Removing observer " + p);
      //new Exception().printStackTrace();
      _presence_registry.remove(this);
      p.unsetState();
      return com.golconda.game.Game.handle(new LeaveWatchImpl(p,
          p.getGameName()));
    }
    else {
      _cat.info("Removing a non observer " + p.name() + " from game " +
                p.getGameName());
      return new com.poker.game.poker.GameDetailsResponse( (com.poker.
          game.poker.Poker) Game.game(p.getGameName()));
    }
  }

  public void deliver(com.golconda.message.Response r) {
    if (_dead) {
      return;
    }
    _handler.putResponse(r);
  }

  public void deliverProxy(com.golconda.message.Response r) {
    _handler.writeProxy(r);
  }

  public boolean isDead() {
    return _dead;
  }

  /**
   * kill the client, remove it from the hashtable and set the state to DIED
   */
  public synchronized void kill() {
    if (_dead) {
      return;
    }
    boolean hasTournyPresence = false;
    for (Enumeration enumt = _presence_registry.elements();
         enumt.hasMoreElements(); ) {
      try {
        PokerPresence presence = (PokerPresence) enumt.nextElement();
        kill(presence, false);
        if (presence.isTournyPresence()) {
          hasTournyPresence = true;
        }
      }
      catch (Exception e) {
        _cat.log(Level.WARNING, "Name=" + name(), e);
      }
    } // remove player from all tables he is playing
    if (_loginSession != null) {
      try {
        //_dbPlayer.updateChips();
        _loginSession.setEndWorth(_dbPlayer.getRealChips());
        _loginSession.setLogoutTime(new java.util.Date());
        _loginSession.updateLogout();
      }
      catch (Exception e) {
        _cat.warning(e.getMessage());
      }
    }
    if (!hasTournyPresence) {
      _dead = true;
      _gp_registry.remove(_name);
      _handler.kill();
      _cat.finest("Killing " + this);
    }
  }

  /**
   *
   */

  public void kill(PokerPresence p, boolean forceKill) {
    if (forceKill) {
      p.player().removePresence(p);
    }
    try {
      if (Game.game(p.getGameName()) != null) {
        if (p.isPlayer()) {
          Game g = Game.game(p.getGameName());
          // p is a player remove him from the game
          if (g.type().isTPoker()) {
              Response r = this.leaveGameOnly(p, true);
              //new Exception().printStackTrace();
              p.unsetDisconnected();
              GameProcessor.deliverResponse(r);
              p.setDisconnected();
              _cat.info("Removing presence " + p + " from game " +  p.getGameName());
          }
          else if (!g.type().isTourny()) {
            Response r = this.leaveGameOnly(p, false);
            //new Exception().printStackTrace();
            p.unsetDisconnected();
            GameProcessor.deliverResponse(r);
            p.setDisconnected();
            _cat.info("Removing presence " + p + " from game " +  p.getGameName());
          }
          else { //it is a tourny
            // check if player or handler is null
            p.setDisconnected();
            _cat.finest("Setting the presence as disconnected .." + p);
            //_handler.setDisconnected();
          }
        }
        else if (p.isObserver()) {
          // no response is generated
          leaveWatch(p);
          //new Exception().printStackTrace();
          _cat.info("Removing observer " + p + "from game " +
                    p.getGameName());
        }
        else {
          p.player().removePresence(p);
        }
      }
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "Name=" + name(), e);
    }
  }

  public long getGRID(int tid) {
    return ( (PokerPresence) _presence_registry.get("" + tid)).getGRID();
  }

  public void setGRID(int tid, long grid) {
    ( (PokerPresence) _presence_registry.get("" + tid)).setGRID(grid);
  }

  /**
   *
   */

  public Enumeration getPresenceList() {
    return _presence_registry.elements();
  }

  public int getPresenceCount() {
    return _presence_registry.size();
  }

  /**
   *
   */

  public boolean refreshBankroll() {
    try {
      _dbPlayer.refreshChips();
      ResponseBuyChips gr = new ResponseBuyChips(1,
                                                 _dbPlayer.getPlayChips(),
                                                 0,
                                                 _dbPlayer.getRealChips(),
                                                 0);
      _cat.finest(gr.toString());
      _handler.putResponse(gr);
      return true;
    }
    catch (DBException e) {
    	_cat.warning(e.getMessage() + this._name + _dbPlayer);
      return false;
    }
  }

  public void setAuthenticated() {
    _isAuthenticated = true;
  }

  public void unsetAuthenticated() {
    _isAuthenticated = false;
  }

  public boolean isAuthenticated() {
    return _isAuthenticated;
  }

  public void handler(Handler h) {
    _handler = h;
  }

  public Handler handler() {
    return _handler;
  }

  public LoginSession loginSession() {
    return _loginSession;
  }

  public void loginSession(LoginSession ls) {
    _loginSession = ls;
  }

  public String session() {
    return _session;
  }

  public String toString() {
    return super.toString() + ", PresenceCount=" + _presence_registry.size() +
        ", Dead=" + _dead;
  }

}
