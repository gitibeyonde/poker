package com.poker.game.poker;

import com.agneya.util.Base64;
import com.agneya.util.Utils;

import com.golconda.db.DBException;
import com.golconda.db.DBGame;
import com.golconda.db.GameSequence;
import com.golconda.game.GameStateEvent;
import com.golconda.game.Player;
import com.golconda.game.resp.Response;

import com.poker.common.db.DBTourny;
import com.poker.common.db.TournySequence;
import com.poker.common.interfaces.MTTInterface;
import com.poker.common.interfaces.TournyInterface;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseInt;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponseString;
import com.poker.common.message.ResponseTableDetail;
import com.poker.common.message.ResponseTableOpen;
import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;
import com.poker.game.poker.pokerimpl.HoldemTourny;
import com.poker.game.poker.pokerimpl.OmahaTourny;
import com.poker.game.poker.pokerimpl.RealHoldemTourny;
import com.poker.game.poker.pokerimpl.RealOmahaTourny;
import com.poker.server.GamePlayer;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tourny extends Observable implements TournyInterface {
    // set the category for logging
    static Logger _cat = Logger.getLogger(Tourny.class.getName());

    String _name;
    String _base_name;
    PokerGameType _type;
    Set<String> _registered;
    MTTInterface[] _ht;
    int _maxP;
    Observer _stateObserver;
    protected int _limit, _tourbo; // 0-10, 1-5, 2-3
    protected double _fees;
    protected double _buyin;
    protected double _chips;
    protected long _time;
    protected int[] _sc = new int[5];
    protected int _ji;
    protected int _state = NOEXIST;
    protected Calendar _sd;
    public Vector<PokerPresence> _winner;
    DBTourny _dbt;
    int _seated_player = 0;
    Vector<PokerPresence> _seated_player_list;
    int _ifin = 0;
    int _time_delta=9999999;

    public Tourny(String name, int type, int limit, int tourbo, int[] schedule, 
                  double buy_ins, double fees, int chips, int maxP, int di, 
                  int ri, int ji, Observer stateObserver) {
        _base_name = name;
        _type = new PokerGameType(type);
        _stateObserver = stateObserver;
        _sc = schedule;
        _limit = limit;
        _tourbo = tourbo;
        _buyin = buy_ins;
        _fees = fees;
        _chips = chips;
        _maxP = maxP;
        _ji = ji;
        _seated_player_list = new Vector<PokerPresence>();
    }

    public String initNewTourny() throws DBException {
        // init schedule time
        getScheduleDate();
        if (_sd.getTimeInMillis() < System.currentTimeMillis()) return null; /// don't create a trourny as the date is past
        int id = (10000 * _sd.get(Calendar.DAY_OF_YEAR) ) + ( 100 * _sd.get(Calendar.HOUR_OF_DAY)) + _sd.get(Calendar.MINUTE);
        _name = id + "_" + _base_name;
        _counter = 0;
        _hand_level = 0;
        _seated_player = 0;
        _seated_player_list = new Vector<PokerPresence>();
        _dbt = 
        new DBTourny( _type.intVal, _limit, _name, _buyin, _fees, _sc, _ji, _ji, _ji);
        _dbt.save();
        _cat.finest(this.toString());
        return _name;
    }
    
    public String name(){ return _name; }

    public void stateSwitch() {
        try {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTimeZone(TimeZone.getTimeZone("GMT"));
            rightNow.set(Calendar.SECOND, 0);
        
           // _cat.finest("Right now=" + Utils.getFormattedDate(rightNow) + ", TT=" + 
            //           Utils.getFormattedDate(getScheduleDate())); 
            _time_delta = delta(rightNow, getScheduleDate());

            //_cat.finest("T^=" + _time_delta + " state=" + _state);
            // tournament is created
            switch (_state) {
            case NOEXIST:
                _state = REG;
                _winner = new Vector<PokerPresence>();
                _registered = Collections.synchronizedSet(new HashSet<String>());
                _cat.finest("DECL " + this);
                break;
            case REG:
                if (_time_delta < _ji) {
                    startWait();
                    _cat.finest("JOIN " + this);
                }
                break;
            case JOIN:

                // send a message to all the connected clients
                Enumeration<?> e = GamePlayer.getGPList();
                while (e.hasMoreElements()) {
                    GamePlayer gp = (GamePlayer)e.nextElement();
                    Iterator<String> i = _registered.iterator();
                    while (i.hasNext()) {
                        String pl_name = (String)i.next();
                        if (pl_name.equals(gp.name())) {
                            // send the alert
                            ResponseString rts = 
                                new ResponseString(1, com.golconda.message.Response.R_TOURNYSTARTS, 
                                                _name);
                            gp.deliver(rts);
                        }
                    }
                }
                if (_time_delta < 1) {
                    startGame();
                    _cat.finest("START " + this);
                }
                break;
            case START:
                _state = RUNNING;
                _time = System.currentTimeMillis();
                break;
            case RUNNING:
                _cat.finest("RUNNING START " + this);
                keepRunning();
                _cat.finest("RUNNING END " + this);
                break;
            case END:
                declareWinners();
                _cat.finest("Tournament ended --------------------\n" + this);
                _stateObserver.update(this, GameStateEvent.MTT_OVER);
                break;
            case FINISH:
                _cat.finest("FINISH...");
                if (_ifin == FINISH_CYCLES) {
                    TournyController.instance().removeTourny(_name);
                    initNewTourny();
                    TournyController.instance().addTourny(this);
                    _sd = Calendar.getInstance();
                    _state = NOEXIST;
                    _ifin = 0;
                }
                _ifin++;
                break;
            default:
                _cat.warning("Erroneous tourny state " + _name);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            _cat.warning(ex.getMessage());
        }
    }

    protected Calendar getScheduleDate() {
        Calendar sd = Calendar.getInstance();
        sd.setTimeZone(TimeZone.getTimeZone("GMT"));
        sd.set(Calendar.SECOND, 0);
        //_cat.finest("Curr time=" + Utils.getFormattedDate(sd));
        if (_state == NOEXIST) {
            if (_sc[0] != -1 && _sc[1] != -1) {
                sd.set(Calendar.MINUTE, _sc[0]);
                sd.set(Calendar.HOUR_OF_DAY, _sc[1]);
            } else if (_sc[1] == -1) {
                //System.out.println("Minute = " + sd.get(Calendar.MINUTE));
                sd.set(Calendar.MINUTE, 
                       _sc[0] * (sd.get(Calendar.MINUTE) / _sc[0] + 1) + 
                       _sc[0]);
                //System.out.println("Minute = " + sd.get(Calendar.MINUTE));
            } else {
                sd.set(Calendar.HOUR_OF_DAY, _sc[1]);
            }

            if (_sc[2] == -1) {
                // check if the time is already past
                Calendar now = Calendar.getInstance();
                now.setTimeZone(TimeZone.getTimeZone("GMT"));
                //_cat.finest("Delta=" + delta(now, sd) + ", ji=" + _ji);
                if (delta(now, sd) < 10 + _ji) {
                    sd.add(Calendar.DATE, 1);
                }
                //_cat.finest("SD=" + Utils.getFormattedDate(sd));
            } else {
                sd.set(Calendar.DATE, _sc[2]);
            }

            if (_sc[3] != -1) {
                sd.set(Calendar.MONTH, _sc[3] - 1);
            }
            if (_sc[4] != -1) {
                sd.set(Calendar.YEAR, _sc[4]);
            }
            _sd = sd;
        } else {
            sd = _sd;
        }
        return sd;
    }

    protected void schedule(int[] s) {
        _sc = s;
    }

    protected boolean isValid() {
        return true;
    }

    public boolean register(Player p) {
        //_cat.finest("Registering " + p.name() + ", size=" + _registered.size());
        if (_registered.contains(p.name())) {
            return false;
        } else {
            _registered.add(p.name());
            return true;
        }
    }

    public boolean register(String name) {
        //_cat.finest("Registering " + name + ", size=" + _registered.size());
        if (_registered.contains(name)) {
            return false;
        } else {
            _registered.add(name);
            return true;
        }
    }

    public boolean unRegister(Player p) {
        //_cat.finest("Registering " + p.name() + ", size=" + _registered.size());
        if (_registered.contains(p.name())) {
            _registered.remove(p.name());
            return true;
        } else {
            return false;
        }
    }

    public boolean unRegister(String p) {
        //_cat.finest("Registering " + p.name() + ", size=" + _registered.size());
        if (_registered.contains(p)) {
            _registered.remove(p);
            return true;
        } else {
            return false;
        }
    }
    
    public Calendar date() {
        return _sd;
    }

    public void startWait() throws DBException {
        int tnos = (_registered.size() + _maxP - 1) / _maxP;
        _cat.finest("Registered players = " + _registered.size() + 
                   " table created = " + tnos);
        // add registered players to the games
        Object[] preg = _registered.toArray();
        int pcount = preg.length;
        String[][] players = new String[tnos][];
        int[] pl_size = new int[tnos];
        for (int j = 0; j < _maxP; j++) {
            for (int i = 0; i < tnos; i++) {
                if (pcount == 0) {
                    break;
                }
                pl_size[i]++;
                pcount--;
            }
        }
        for (int i = 0; i < tnos; i++) {
            players[i] = new String[pl_size[i]];
        }
        pcount = preg.length;
        for (int j = 0; j < _maxP; j++) {
            for (int i = 0; i < tnos; i++) {
                if (pcount == 0) {
                    break;
                }
                players[i][j] = (String)preg[--pcount];
            }
        }
        for (int i = 0; i < tnos; i++) {
            for (int j = 0; j < players[i].length; j++) {
                _cat.finest("table=" + i + ",player=" + players[i][j]);
            }
        }
        // set the players for respective game
        _ht = new HoldemTourny[tnos];
        // start the HoldemTournys
        for (int i = 0; i < tnos; i++) {
            DBGame dbg = null;
            //int id = GameSequence.getNextGameId();
            String aff[] = { "admin" };
            if (_type.intVal == PokerGameType.HoldemTourny){
                _ht[i] = new HoldemTourny(_name + "-" + i, _limit, 2, _maxP, aff, _stateObserver, this);
            } else if (_type.intVal == PokerGameType.Real_HoldemTourny) {
            	_ht[i] = new RealHoldemTourny(_name + "-" + i, _limit, 2, _maxP, aff, _stateObserver, this);
            } else if (_type.intVal == PokerGameType.OmahaHiTourny) {
            	_ht[i] = new OmahaTourny(_name + "-" + i, _limit, 2, _maxP, aff, _stateObserver, this);
            } else if (_type.intVal == PokerGameType.Real_OmahaHiTourny) {
            	_ht[i] = new RealOmahaTourny(_name + "-" + i, _limit, 2, _maxP, aff, _stateObserver, this);
            } else {
                throw new IllegalStateException("Only holdem tournies are supported " + _type);
            }
            _ht[i].setArgs(5, 10, 2, 5);
            //_cat.finest("CREATING TABLES" + _ht[i]);
            _ht[i].invite(players[i]);

            //dbg = new DBGame(1048576, _name +"-"+ id, "", aff, null, -1, 2, _maxP, 5, 10, 2, 5, 4, 4);
            //dbg.save();
        }
        _state = JOIN;
    }

    public Response myTable(GamePlayer gp) throws DBException {
        Response r = null;
        if (isWaiting() || isPlaying()) {
            boolean _already_sitting = false;
            for (int i = 0; i < _ht.length; i++) {
                if (_ht[i].isInvited(gp.name())) {
                    PokerPresence p;
                    if ((p = _ht[i].getPresenceOnTable(gp.name())) != null) {
                        _cat.finest("Found presence " + p);
                        _already_sitting = true;
                        p.unsetDisconnected();
                        gp.movePresence(p, _ht[i].name());
                        return _ht[i].details(p);
                    }
                }
            }
            for (int i = 0; i < _ht.length; i++) {
                if (_ht[i].isInvited(gp.name())) {
                    // check if he is already sitting
                    PokerPresence p = 
                        (PokerPresence)gp.createPresence(_ht[i].name());
                    p.setMTTPresence(_name);
                    // seat the player on this table
                    int pos = _ht[i].getNextVacantPosition();
                    if (pos != -1) {
                        // seat this person
                        //gp.addWatch(p.getGID());
                        r = gp.addGame(p, pos, 0);
                        //DEBUG
                        _seated_player++;
                        _seated_player_list.add(p);
                        _cat.finest("Seating player " + p.name() + " at pos " + p.pos() + " in game " + r.getGame().name());
                    }
                    break;
                }
            }
        }
        return r;
    }

    public void startGame() throws Exception {
        if (_ht.length == 0) {
            _state = END;
            return;
        }
        _state = START;
        int _deb_pc = 0;
        // send the appropriate responses to the players on game start
        for (int k = 0; k < _ht.length; k++) {
            _deb_pc += _ht[k].allPlayers(-1).length;
            _cat.finest("STARTING GAME " + _ht[k].name());
            com.poker.server.GameProcessor.deliverResponse(_ht[k].start());
        }
        _cat.finest("Total players=" + _deb_pc);
        _cat.finest("Seated players=" + _seated_player);
    }

    public void keepRunning() throws Exception {
        //DEBUG
        int _deb_wins = _winner.size();
        int _deb_pc = 0;

        double total_chips = 0;
        ConcurrentHashMap<String, PokerPresence> _deb_pay_hash = new ConcurrentHashMap<String, PokerPresence>();
        for (int k = 0; k < _ht.length; k++) {
            PokerPresence[] v = _ht[k].allPlayers(-1);
            if (v != null) {
                _deb_pc += v.length;
            }
            for (int j = 0; j < _ht[k].allPlayers(-1).length; j++) {
                _deb_pay_hash.put(_ht[k].allPlayers(-1)[j].name(), 
                                  _ht[k].allPlayers(-1)[j]);
                total_chips += _ht[k].allPlayers(-1)[j].getAmtAtTable();
            }
        }
        _cat.info("Total chips = " + total_chips);

        if ((_deb_wins + _deb_pc) != _seated_player) {
        	ConcurrentHashMap<String, PokerPresence> _deb_win_hash = new ConcurrentHashMap<String, PokerPresence>();
            for (int i = 0; i < _deb_wins; i++) {
                _deb_win_hash.put(((PokerPresence)_winner.get(i)).name(), 
                                  ((PokerPresence)_winner.get(i)));
            }

            for (int m = 0; m < _seated_player_list.size(); m++) {
                PokerPresence p = (PokerPresence)_seated_player_list.get(m);
                if (_deb_win_hash.get(p.name()) == null && 
                    _deb_pay_hash.get(p.name()) == null) {
                    //_cat.info("MISSING PRESENCE" + p);
                }

                if (_deb_win_hash.get(p.name()) != null && 
                    _deb_pay_hash.get(p.name()) != null) {
                    //_cat.info("EXTRA PRESENCE" + p);
                }

            }
        }
        _counter++;
        
        if (_tourbo == TOURBO_HYPER && time() > _hand_level * TOURBO_HYPER_TIME) {
        	_hand_level++;
        }
        else if (_tourbo == TOURBO_TOURBO && time() > _hand_level * TOURBO_TOURBO_TIME) {
        	_hand_level++;
        } 
        else if ( time() > _hand_level * TOURBO_NORMAL_TIME){
        	_hand_level++;
        }
        
        _hand_level = _hand_level > 30 ? 30 : _hand_level;
        if (_ht.length == 0 || 
            (_ht.length == 1 && _ht[0].getPlayerCount() <= 1)) {
            _state = END;
            _cat.finest("Marking the end of tourny as one table and less than 1 eligible players ");
            if (_ht[0] != null && _ht[0].getPlayerCount() == 1) {
                addWinner(_ht[0].getPlayerList()[0]);
                _cat.finest("Last remaining player players " + 
                           _ht[0].getPlayerList()[0]);
            }
        } else {
            _ht = mergeTablesBFLR(_ht);
            _cat.finest("Merged the tables ....." + _ht.length);
            // start the game
            // send the appropriate responses to the players on game start
            for (int k = 0; k < _ht.length; k++) {
                if (_ht[k].handOver()) {
                    _cat.finest("STARTING the game after merge " + _ht[k].name());
                    Response r = _ht[k].start();
                    if (r instanceof GameDetailsResponse) {
                        _cat.log(Level.WARNING, "The table failed to start " + _ht[k]);
                        //continue;
                        // remove this table
                        if (_ht[k].allPlayers(-1).length == 0 || 
                            _ht[k].startCount() > 2000) {
                            _ht[k].destroyTournyTable();
                            MTTInterface[] del_list =  new MTTInterface[_ht.length - 1];
                            for (int l = 0, m = 0; l < _ht.length - 1; 
                                 l++, m++) {
                                if (m == k) {
                                    m++;
                                }
                                del_list[l] = _ht[m];
                            }
                            _ht = del_list;
                        }
                    } else {
                        /**PokerPresence[] v = _ht[k].allPlayers( -1);
                         for (int j = 0; j < v.length; j++) {
              GamePlayer gp = (GamePlayer) v[j].player();
              gp.deliver(new ResponseGameEvent(1, r.getCommand(v[j])));
              //_cat.finest(gp + " response " + r.getCommand(v[j]));
                         }**/
                        com.poker.server.GameProcessor.deliverResponse(r);
                    }
                    if (k >= _ht.length) {
                        break;
                    }
                } else {
                    //_cat.finest("Hand not over " + _ht[k]);
                }
            }
            _state = RUNNING;
        }
    }

    public void addWinner(PokerPresence pr) {
        Player p = (Player)pr.player();
        if (p instanceof GamePlayer) {
            GamePlayer gp = (GamePlayer)p;
            StringBuilder sbuf = 
                new StringBuilder("type=broadcast");
            sbuf.append(",name=").append(pr.getGID());
            sbuf.append(",message=").append(Base64.encodeString("Congratulation " + 
                                            gp.name() + 
                                            "! Your position is " + 
                                            (_seated_player - 
                                             _winner.size()) + 
                                            " among " + 
                                            _seated_player + 
                                            " players. Your hand histories will be mailed to you"));
            ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
            gp.deliver(rm);
        }
        _winner.add(pr);
    }

    public void declareWinners() throws Exception {
        assert _ht.length <= 1 :  "There should be only one table remaining after the tournament is over";
        if (_ht.length == 1) {
            PokerPresence[] v = _ht[0].allPlayers(-1);
            for (int i = 0; i < v.length; i++) {
                Player p = v[i].player();
                _cat.finest("Removing player from last table ..." + v[i]);
                if (p instanceof GamePlayer) {
                    GamePlayer gp = (GamePlayer)p;
                    StringBuilder buf = new StringBuilder();
                    buf.append("type=broadcast,name=").append(_ht[0].name()).append(",message=").append(com.agneya.util.Base64.encodeString("The table is closed or you are removed form the table"));
                    gp.deliver(new ResponseMessage(1, buf.toString()));
                    //v[i].lastMove(PokerMoves.LEAVE);
                    _ht[0].setInquirer(v[i]);
                    _ht[0].setCurrent(v[i]);
                    //SitInResponse gdr = new SitInResponse(_ht[0], -78);
                    //gp.deliver(new ResponseGameEvent(1, gdr.getCommand(v[i])));
                    //_cat.finest("Sending " + gdr.getCommand(v[i]) + "  to " + v[i]);
                }
            }
            _ht[0].destroyTournyTable();
            _cat.finest("Destroyed .." + _ht[0]);

            // TODO log the winners in DB and
            // add money to the real bankroll
            _cat.info("Winner Count = " + _winner.size());
            Enumeration<PokerPresence> i = _winner.elements();
            while (i.hasMoreElements()) {
                _cat.info("Winner = " + i.nextElement());
            }
        }
        /**
     * init next run
     */
        getScheduleDate();
        _state = FINISH;
    }

    public MTTInterface[] mergeTablesBFLR(MTTInterface[] iht) throws Exception {
        _cat.finest("Start merge...");
        if (iht.length <= 1) {
            _cat.finest("Single Table remaining " + iht[0]);
            return iht;
        }

        int total_players_in_game = 0;

        for (int k = 0; k < iht.length; k++) {
            //_cat.info("Game State = " + iht[k]);
            total_players_in_game += iht[k].getPlayerCount();
            _cat.info("Total PokerPresence on " + iht[k].name() + " is " +  iht[k].getPlayerCount() + " State = " + iht[k].state());
            /** PokerPresence v[] = iht[k].getPlayerList();
           for (int l = 0; l < v.length; l++) {
             //_cat.finest("PokerPresence = " + v[l]);
             if (v[l].player() == null) {
               _cat.log(Level.WARNING, "Player null");
             }
           }**/
        }

        /**
         * Then remove the tables with lowest number of players, if these can
         * be accomodated on other tables
         */
         //
         int _tables_required = (total_players_in_game + _maxP - 1) / _maxP;
        _cat.finest("Total players " + total_players_in_game + " Tables required " + _tables_required);

        Vector<PokerPresence> _relocated_players = new Vector<PokerPresence>();
        int k = iht.length - 1;
        if (_tables_required < iht.length) { //TABLES NEED TO BE ELIMINATED
            _cat.finest((iht.length - _tables_required) + " TABLES NEED TO BE ELIMINATED");
            /** find out the players who will move **/
            //
            for (;k >= _tables_required; k--) {
                MTTInterface _to_be_removed = iht[k];
                if (!_to_be_removed.handOver()) {
                    _cat.finest("Hand not over on " + _to_be_removed.name());
                    continue;
                }
                _cat.finest("Hand over removing " + _to_be_removed.name());
                PokerPresence[] r = iht[k].getPlayerList();
                for (int l = 0; r != null && l < r.length; l++) {
                    _cat.finest("Relocating player " + r[l]);
                    _relocated_players.add(r[l]);
                }
                _cat.finest("Destroying Game " + iht[k].name());
                _to_be_removed.destroyTournyTable();
                iht[k] = null;
            }
         

            // accomodate them on remaining tables in ascending order of player strength
            for (int j = 0; j < _relocated_players.size(); j++) {
                _cat.finest("Tourny Moving " + _relocated_players.get(j));
                boolean _deb_seated = false;
                for (int i = 0; i < _tables_required; i++) {
                    PokerPresence p = (PokerPresence)_relocated_players.get(j);
                    // try to accomodate this player on the table
                    int pos = iht[i].getNextVacantPosition();
                    _cat.finest("Vacant position on " + iht[i] + " is " + pos);
                    if (pos == -1) {
                        continue;
                    } else {
                        // seat this person
                        iht[i].addInvite(p);
                        p.setPos(pos);
                        ((GamePlayer)p.player()).deliver(new ResponseTableOpen(iht[i].name(), p.pos(), iht[i].details(p).getCommand(p)));
                        Response r = iht[i].moveToTable(p, iht[i].name());
                        ((GamePlayer)p.player()).deliver(new ResponseGameEvent(1,  r.getCommand(p)));
                        _cat.finest("Tourny Moved " + p + " sending " + r.getCommand(p));
                        _deb_seated = true;
                    }
                    break;
                }
                if (!_deb_seated) {
                    new Exception(_name +  " Unable to seat player").printStackTrace();
                }
            }
            // Return the remaining tables
            Vector<MTTInterface> vht = new Vector<MTTInterface>();
            for (int i = 0; i < iht.length; i++) {
                if (iht[i] != null) {
                    vht.add(iht[i]);
                }
            }
            return (MTTInterface[])vht.toArray(new MTTInterface[vht.size()]);
        } else {
            return iht;
        }
    }

    public boolean isPlaying() {
        return _state == START || _state == RUNNING;
    }

    public boolean isRegOpen() {
        return _state == REG;
    }

    public boolean isWaiting() {
        return _state == JOIN;
    }
    public boolean isStarting() {
        return _state == START;
    }

    public boolean tournyOver() {
        return _state >= END;
    }

    public MTTInterface getGame(PokerPresence p) {
        int i;
        for (i = 0; i < _ht.length; i++) {
            if (_ht[i].isInvited(p.name())) {
                break;
            }
        }
        return _ht[i];
    }

    public MTTInterface[] listAll() {
        return _ht;
    }

    public int delta(Calendar d2, Calendar d1) {
        int delta = 0;

        delta = 
                518400 * (d1.get(Calendar.YEAR) - d2.get(Calendar.YEAR)) + 1440 * 
                (d1.get(Calendar.DAY_OF_YEAR) - d2.get(Calendar.DAY_OF_YEAR)) + 
                60 * 
                (d1.get(Calendar.HOUR_OF_DAY) - d2.get(Calendar.HOUR_OF_DAY)) + 
                d1.get(Calendar.MINUTE) - d2.get(Calendar.MINUTE);
        return delta;
    }

    public String stringValue() {
        StringBuilder buf = new StringBuilder();
        buf.append("name=").append(_name);
        buf.append(",date=").append(Utils.getFormattedDate(_sd));
        buf.append(",delta=").append(_time_delta);
        buf.append(",type=").append(_type);
        buf.append(",limit=").append(_limit);
        buf.append(",buy-in=").append(Utils.getRoundedDollarCent(_buyin));
        buf.append(",fees=").append(Utils.getRoundedDollarCent(_fees));
        buf.append(",pool=").append(Utils.getRoundedDollarCent(_buyin * _seated_player_list.size()));
        buf.append(",time=").append(System.currentTimeMillis() - _time);
        buf.append(",level=").append(_hand_level);
        buf.append(",stakes=").append(_level_limits[_hand_level][0]).append("/").append(_level_limits[_hand_level][1]);
        buf.append(",next_level=").append(_level_limits[_hand_level + 1][0]).append("/").append(_level_limits[_hand_level + 1][1]);
        buf.append(",chips=").append(_chips);
        buf.append(",intervals=").append(_ji);
        buf.append(",state=").append(_state).append(",");
        if (isRegOpen() || isWaiting() || isStarting()) {
            if (_registered.size() > 0) {
                buf.append("player=");
                Iterator<String> enumt = _registered.iterator();
                while (enumt.hasNext()) {
                    buf.append(((String)enumt.next())).append("`");
                    buf.append(_chips).append("|");
                }
                buf.deleteCharAt(buf.length() - 1);
            }
        } else if (isPlaying()) {
            if (_seated_player_list.size() > 0) {
                PokerPresence v[] = 
                    (PokerPresence[])_seated_player_list.toArray(new PokerPresence[_seated_player_list.size()]);
                // sort the all-in players in descending order of  their hand strength
                java.util.Arrays.sort(v, new Comparator<Object>() {
                            public int compare(Object o1, Object o2) {
                                return (int)(((PokerPresence)o2).getAmtAtTable() - 
                                             ((PokerPresence)o1).getAmtAtTable());
                            }
                        });
                buf.append("player=");
                for (int i = 0; i < v.length; i++) {
                    buf.append(v[i].name()).append("`");
                    buf.append((int)v[i].getAmtAtTable()).append("|");
                }
                buf.deleteCharAt(buf.length() - 1);
            }
        } else if (tournyOver()) {
            if (_winner.size() > 0) {
                buf.append("mttwinners=");
                for (int i = _winner.size() - 1, j = 1; i >= 0; i--, j++) {
                    buf.append(((PokerPresence)_winner.get(i)).name()).append("`").append(prize(j,_seated_player_list.size())).append("|");
                }
                buf.deleteCharAt(buf.length() - 1);
            }
        }
        return buf.toString();
    }

    public String toString() {
        return stringValue();
    }

    public DBTourny dbTourny() {
        return _dbt;
    }

    public double buyIn() {
        return _buyin;
    }

    public double fees() {
        return _fees;
    }
    
    public PokerGameType type(){
    	return _type;
    }
    
    public long time(){
        return _sd.getTimeInMillis();
    }

    /// INTERFACE METHODS

    public PokerPresence[] winners() {
        PokerPresence[] v = new PokerPresence[_winner.size()];
        for (int i = _winner.size() - 1; i >= 0; i--) {
            v[i] = ((PokerPresence)_winner.get(i));
        }
        return v;
    }

    public int state() {
        return _state;
    }

    public double chips() {
        return _chips;
    }

    public double prize(int i, int total) {
        if (total < 28) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.5);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.3);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.2);
            default:
                return 0.00;
            }
        } else if (total < 46) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.4);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.24);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.16);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.12);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.08);
            default:
                return 0.00;
            }
        } else if (total < 101) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.3);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.2);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.12);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.10);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.08);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.065);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.055);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.45);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            default:
                return 0.00;
            }
        } else if (total < 201) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.3);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.2);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.1190);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.08);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.065);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.05);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.026);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.017);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.012);

            default:
                return 0.00;
            }
        } else if (total < 301) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.275);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.175);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.116);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.08);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.06);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.045);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.026);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.017);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.012);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.007);
            default:
                return 0.00;
            }
        } else if (total < 401) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.25);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.16);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.111);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.08);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.06);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.045);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.026);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.017);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.012);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.007);
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return Utils.getRounded(_winner.size() * _buyin * 0.005);

            default:
                return 0.00;
            }
        } else if (total < 501) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.25);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.1540);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.105);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.07);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.055);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.045);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.026);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.017);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.011);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.007);
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return Utils.getRounded(_winner.size() * _buyin * 0.005);
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
                return Utils.getRounded(_winner.size() * _buyin * 0.004);

            default:
                return 0.00;
            }
        } else if (total < 601) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.25);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.15);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.0955);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.07);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.055);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.045);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.026);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.017);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.011);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.006);
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return Utils.getRounded(_winner.size() * _buyin * 0.0045);
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
                return Utils.getRounded(_winner.size() * _buyin * 0.0035);
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
                return Utils.getRounded(_winner.size() * _buyin * 0.0035);

            default:
                return 0.00;
            }
        } else if (total < 801) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.25);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.143);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.092);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.069);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.055);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.045);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.035);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.025);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.016);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.011);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.006);
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return Utils.getRounded(_winner.size() * _buyin * 0.0045);
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
                return Utils.getRounded(_winner.size() * _buyin * 0.0035);
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
                return Utils.getRounded(_winner.size() * _buyin * 0.0025);
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return Utils.getRounded(_winner.size() * _buyin * 0.0025);

            default:
                return 0.00;
            }
        } else if (total < 1001) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.25);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.14);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.09);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.065);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.054);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.044);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.034);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.024);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.0155);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.01);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.0055);
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return Utils.getRounded(_winner.size() * _buyin * 0.004);
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
                return Utils.getRounded(_winner.size() * _buyin * 0.003);
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
                return Utils.getRounded(_winner.size() * _buyin * 0.0025);
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return Utils.getRounded(_winner.size() * _buyin * 0.0025);
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
                return Utils.getRounded(_winner.size() * _buyin * 0.0020);

            default:
                return 0.00;
            }
        } else if (total < 1201) {
            switch (i) {
            case 1:
                return Utils.getRounded(_winner.size() * _buyin * 0.25);
            case 2:
                return Utils.getRounded(_winner.size() * _buyin * 0.14);
            case 3:
                return Utils.getRounded(_winner.size() * _buyin * 0.09);
            case 4:
                return Utils.getRounded(_winner.size() * _buyin * 0.065);
            case 5:
                return Utils.getRounded(_winner.size() * _buyin * 0.053);
            case 6:
                return Utils.getRounded(_winner.size() * _buyin * 0.043);
            case 7:
                return Utils.getRounded(_winner.size() * _buyin * 0.033);
            case 8:
                return Utils.getRounded(_winner.size() * _buyin * 0.023);
            case 9:
                return Utils.getRounded(_winner.size() * _buyin * 0.015);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return Utils.getRounded(_winner.size() * _buyin * 0.009);
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                return Utils.getRounded(_winner.size() * _buyin * 0.0045);
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                return Utils.getRounded(_winner.size() * _buyin * 0.0035);
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
                return Utils.getRounded(_winner.size() * _buyin * 0.003);
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
                return Utils.getRounded(_winner.size() * _buyin * 0.0025);
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return Utils.getRounded(_winner.size() * _buyin * 0.0025);
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
                return Utils.getRounded(_winner.size() * _buyin * 0.0020);
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
                return Utils.getRounded(_winner.size() * _buyin * 0.0015);

            default:
                return 0.00;
            }
        } else {
            throw new IllegalStateException("More than expected players");
        }

    }

    int _counter;
    public int _hand_level;
    int[] _prize_distribution = { 50, 30, 20 };
    public int[][] _level_limits = 
    { { 30, 60 }, { 40, 80 }, { 50, 100 }, { 60, 120 }, { 80, 160 }, 
      { 100, 200 }, { 120, 240 }, { 150, 300 }, { 200, 400 }, { 250, 500 }, 
      { 300, 600 }, { 400, 800 }, { 500, 1000 }, { 600, 1200 }, { 800, 1600 }, 
      { 1000, 2000 }, { 1200, 2400 }, { 1500, 3000 }, { 2000, 4000 }, 
      { 2500, 5000 }, { 3000, 6000 }, { 4000, 8000 }, { 5000, 10000 }, 
      { 6000, 12000 }, { 15000, 30000 }, { 20000, 40000 }, { 25000, 50000 }, 
      { 30000, 60000 }, { 40000, 80000 }, { 50000, 100000 }, 
      { 80000, 160000 },  { 100000, 200000 },  { 150000, 300000 } ,  
      { 200000, 400000 }  };

    public static void main(String[] args) throws Exception {
        int sch[] = { 10, -1, -1, -1, -1 };
        Tourny t = 
            new Tourny("hullA", 32, -1, 1, sch, 0, 0, 800, 6, 1, 1, 1, (Observer)null);
        int nsch[] = { 4, -1, -1, -1, -1 };
        Calendar sc = t.getScheduleDate();

        System.out.println(sc.get(Calendar.MINUTE));
        System.out.println(sc.get(Calendar.HOUR_OF_DAY));
        System.out.println(sc.get(Calendar.DATE));
        System.out.println(sc.get(Calendar.MONTH));
        System.out.println(sc.get(Calendar.YEAR));

    }
}
