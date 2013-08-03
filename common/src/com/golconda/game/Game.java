package com.golconda.game;

import com.agneya.util.Configuration;
import com.agneya.util.Rng;
import com.agneya.util.Utils;

import com.golconda.game.gamemsg.Message;
import com.golconda.game.resp.Response;

import java.io.Serializable;

import java.util.*;
import java.util.logging.Logger;


public abstract class Game
    extends Observable
    implements Serializable {

  static {
    try {
      Configuration conf = Configuration.instance();
      int timeout = conf.getInt("Server.maintenance.heartbeat");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // set the category for logging
  static Logger _cat = Logger.getLogger(Game.class.getName());

  public abstract Response details();

  public abstract Response details(Presence p);

  public abstract Response summary();

  public abstract Response join(Presence p, double amount);

  public abstract Response start();

  public abstract void destroy();

  public abstract Response leave(Presence p, boolean timeout);

  public abstract Response observe(Presence p);

  public abstract Response leaveWatch(Presence p);

  // to promote a poker observer to player status
  public abstract Response promoteToPlayer(Presence observer, double amount);

  public abstract Presence[] allPlayers(int startPos);

  public abstract Presence[] activePlayers(int startPos);

  public abstract Presence[] activePlayers();

  public abstract Presence[] inActivePlayers(int startPos);

  public abstract Presence[] newPlayers(int startPos);

  public abstract Response chat(Presence p, String message);
  
  public abstract Response gameOverResponse(Presence p);

  public Game() {
    _rng = new Rng();
  }

  public String name() {
    return _name;
  }

  public long grid() {
    return _grid;
  }

  public void grid(long grid) {
    _grid = grid;
    _responseId = 0;
  }

  public Calendar startTime() {
    return _start;
  }

  public void startTime(Calendar d) {
    _start = d;
  }

  public static Game game(String gameName) {
    return (Game) map.get(gameName);
  }

  public static Game[] listAll() {
    return (Game[]) map.values().toArray(new Game[] {});
  }

  public static Game remove(String gameName) {
    synchronized (Game.class) {
      return (Game) map.remove(gameName);
    }
  }

  public static Game remove(Game g) {
    return Game.remove(g.name());
  }

  public static void removeAll(Collection<Game> c) {
    for (Game g : c) {
      map.remove(g.name());
    }
  }

  public static void add(Game g) {
    g._last_move_ts = System.currentTimeMillis();
    synchronized (Game.class) {
      map.put(g.name(), g);
    }
  }

  public void removeObserver(Presence p){
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      Presence op = (Presence) i.next();
      if (op.name().equals(p.name())) {
        _observers.remove(op);
        break;
      }
    }
  }

  public void setTimeDelta(){
    _last_move_ts = System.currentTimeMillis();
  }

  public long lastMoveDelta(){
    return System.currentTimeMillis() - _last_move_ts;
  }

  public static Response handle(Message m) {
    Game g = (Game) map.get("" + m.gameId());
    if (g == null) {
      _cat.severe(m.gameId() + " game does not exists ");
      return null;
    }
    synchronized (g) {
      g._last_move_ts = System.currentTimeMillis();
      return m.interpret();
    }
  }

  public void setInquirer(Presence player) {
    _inquirer = player;
  }

  public Presence inquirer(){
    return _inquirer;
  }

  public void setMarker(Presence player) {
    _marker = player;
  }

  public Presence marker() {
    return _marker;
  }

  public void setCurrent(Presence player) {
    _current = player;
  }

  public Presence current() {
    return _current;
  }

  public void stateObserver(Observer obs) {
    if (obs != null) {
        _stateObserver.add(obs);
    }
  }

    public void removeObserver(Observer obs) {
        if (obs != null) {
            _stateObserver.remove(obs);
        }
    }

  public List<Observer> stateObserver() {
    return _stateObserver;
  }

  public void update(Game g, GameStateEvent e){
      // make a copy and update as in an update the element may be removed
      List<Observer> copy = new ArrayList<Observer>(_stateObserver);
      for (Observer o: copy){
          o.update(g, e);
      }
  }

  public int responseId() {
    return ++_responseId;
  }

  public abstract GameType type();

  /*
     kill signals to immediately end the game and remove it from list of hosted games.
     If a run is in progress, participants are refunded bets.
   */
  public static synchronized void kill(Game g) {
    synchronized (g) {
      Game.remove(g);
      g.destroy();
    }

  }

  public static synchronized void killAll() {
    Game[] games = Game.listAll();
    for (int i = 0; i < games.length; i++) {
      Game.kill(games[i]);
    }
  }

  public void suspend() {
    _keepRunning = false;
  }

  public Response resume() {
    _keepRunning = true;
    return this.start();
  }

  public boolean isRunning() {
    return _inProgress;
  }

  public boolean isSuspended() {
    return !_keepRunning;
  }

  public String tournyId() {
    return _tournyId;
  }

    

  public void initNextMove(int move_index, long move_val, boolean isRespReq, double start, double end) {
    //int index = move.intIndex();//Moves.intIndex(move);
    _nextMoveAmt[move_index][0] = start;
    _nextMoveAmt[move_index][1] = end;
    _nextMovePlayer = _nextPlayer;
    _nextMove += move_val;
    if (isRespReq) {
      _nextMovePlayer.setResponseReq();
    }
  }

  public boolean checkNextMove(Moves move, double amt, Presence p) {
    int index = move.intIndex();
    double start = _nextMoveAmt[index][0];
    double end = _nextMoveAmt[index][1];
    amt = Utils.getRounded(amt);
    //_cat.finest("Start = " + start + ", End =" + end);
    if (p.equals(_nextMovePlayer) && (move.intVal() & _nextMove) == move.intVal()) {
      if (end == 0.0) {
        return amt == Utils.getRounded(start);
      }
      else {
        return amt <= Utils.getRounded(end) && amt >= Utils.getRounded(start);
      }
    }
    else {
      return false;
    }
  }

  public String[] affiliate() {
    return (String[]) _affiliate.toArray(new String[_affiliate.size()]);
  }

  public StringBuilder affiliateString() {
    StringBuilder sb = new StringBuilder();
    for (Iterator i = _affiliate.iterator(); i.hasNext(); ) {
      sb.append(i.next()).append("|");
    }
    return sb.length() > 1 ? sb.deleteCharAt(sb.length() - 1) : sb;
  }

  public void initAffiliate(String[] aff) {
    //if (aff != null )
        for (int i = 0; i < aff.length; i++) {
          _affiliate.add(aff[i]);
        }
  }

  public boolean isAffiliate(String aff) {
    for (Iterator i = _affiliate.iterator(); i.hasNext(); ) {
      if (i.next().equals(aff)) {
        return true;
      }
    }
    return false;
  }

  public String[] partner() {
    return (String[]) _partner.toArray(new String[_partner.size()]);
  }

  public StringBuilder partnerString() {
    StringBuilder sb = new StringBuilder();
    for (Iterator i = _partner.iterator(); i.hasNext(); ) {
      sb.append(i.next()).append("|");
    }
    return sb.length() > 1 ? sb.deleteCharAt(sb.length() - 1) : sb;
  }

  public void initPartner(String[] frnd) {
    for (int i = 0; frnd != null && i < frnd.length; i++) {
        if (frnd[i].length() > 2){
            System.out.println("Game adding partner = " + frnd[i]);
            _partner.add(frnd[i]);
        }
    }
  }

  public void resetPartners() {
    _partner = Collections.synchronizedSet(new HashSet());
  }

  public boolean isPartner(String part) {
    for (Iterator i = _partner.iterator(); i.hasNext(); ) {
      if (i.next().equals(part)) {
        return true;
      }
    }
    return false;
  }

  public boolean isInvited(String name) {
    if (_partner.size() == 0) {
      return true; // can play uninvited
    }
    else {
      _cat.info("Should be a invited player");
      return _partner.contains(name);
    }
  }

  public boolean isPrivate(String name) {
    if (_partner.size() == 0) {
      return false; // can play uninvited
    }
    else {
      return _partner.contains(name);
    }
  }

  public Set invited() {
    return _partner;
  }

  public void invite(String[] p) {
    for (int i = 0; i < p.length; i++) {
      _partner.add(p[i]);
    }
  }

  public void addInvite(Presence p) {
    _partner.add(p.name());
  }

  public void removeInvite(Presence p) {
    _partner.remove(p.name());
  }

  public void resetInvite() {
    _partner = Collections.synchronizedSet(new HashSet());
  }

  public void setRank(int rank) {
    _rank = rank;
  }

  public int rank() {
    return _rank;
  }

  public boolean isGRIDOver(long grid){
    return (_grid != grid) || !_inProgress;
  }

  protected Rng _rng;
  public String _name=""; //= "Some-Poker";
  protected long _grid=-1;
  protected Calendar _start;
  protected final long _creation_time = System.currentTimeMillis();;
  public long _last_move_ts;

  protected Presence _inquirer;
  private Presence _current;
  public Presence _nextPlayer;
  public long _nextMove;
  public double[][] _nextMoveAmt = new double[64][2];
  public Presence _nextMovePlayer;
  public static HashMap<String, Game> map = new HashMap<String, Game>();
  private List<Observer> _stateObserver = new ArrayList<Observer>();
  private Presence _marker; // player *next* to marker will play next

  public boolean _keepRunning = true; // by default a game will run
  public volatile boolean _inProgress;
  protected String _tournyId;


  protected Set _observers = Collections.synchronizedSet(new HashSet());
  private Set _affiliate = Collections.synchronizedSet(new HashSet());
  private Set _partner = Collections.synchronizedSet(new HashSet());

  // game level
  private int _rank = -1;

  // debug var
  volatile int _responseId;

  public static void main(String[] args) {
    double amt1 = 10.020000003;
    double amt2 = 10.02;

    System.out.println( (Utils.getRounded(amt1) == Utils.getRounded(amt2) ?
                         "True" : "False"));

  }

}
