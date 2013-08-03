package com.poker.shills;

import com.golconda.db.ModuleType;
import com.golconda.game.GameStateEvent;
import com.golconda.game.resp.Response;
import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.poker.game.PokerPresence;
import com.poker.game.poker.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class BotGame extends TimerTask implements Observer {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotGame.class.getName());
    public static Hashtable<String, BotGame> _game_registry = new Hashtable<String, BotGame>();
    private Queue<BotMove> _move_queue = new ConcurrentLinkedQueue<BotMove>();
    public static final int RANDOM = 1;
    public static final int FULL = 2;
    public static final int SPARSE = 3;
    public static final int DENSE = 4;
    public int _type=0;
    Poker _pg;
    public HashMap<String, BotPlayer> _bpv;
    public boolean _keepRunning = true;
    Timer _t;

    public BotGame(Poker pg, int type){
        _pg = pg;
        _type = type;
        _bpv = new  HashMap<String, BotPlayer>();
        _t = new Timer("BotGame");
    }

    public static BotGame getBotGame(String name){
        return _game_registry.get(name);
    }

    public static Collection<BotGame> getBotGames(){
        return _game_registry.values();
    }

    public boolean isReal(){
        return _pg.type().isReal();
    }

    public boolean isRandom(){
        return _pg.type().isRandomBotGame();
    }

    public boolean isFull(){
        return _pg.type().isBotGame();
    }

    public synchronized void update(Observable o, Object arg) {
        if (arg == GameStateEvent.GAME_SETUP) {
            if (_keepRunning){
                Poker pg = (Poker) o;
                for (PokerPresence p: pg.allPlayers(0)){
                    BotPlayer bp = _bpv.get(p.name());
                    if (bp == null) continue;
                    if (Math.random() > 0.5 && p.getAmtAtTable() < 6 * pg.minBet()){
                        _cat.finest("^^^BROKE " + bp);
                        int pos = p.pos();
                        p.setDisconnected();
                        _cat.finest(p.getGameName() + "Removing " + p);
                        Response r = pg.leave(p, false);
                        p.leaveTable(new ModuleType(ModuleType.POKER));
                        p.unsetState();
                        bp.removePresence(p);
                        if (!(r instanceof LeaveResponse) ){
                            _cat.finest("NOT LEAVE FOLD=" + r.getBroadcast());
                            deliverResponse((PokerResponse) r);
                        }
                        bp.release();
                        _bpv.remove(p.name());
                        addMove(new BotMove(this, pos));
                    }
                }
            }
        }
        else if (arg == GameStateEvent.GAME_POSTRUN){
            if (!_keepRunning){
                _pg.removeObserver(this);
                _game_registry.remove(_pg.name());
                Poker pg = (Poker) o;
                deliverResponse(new GameStartResponse(pg));
                for (PokerPresence p: pg.allPlayers(0)){
                    BotPlayer bp = _bpv.get(p.name());
                    if (bp == null) continue;
                    p.setDisconnected();
                    _cat.finest(p.getGameName() + ":Removing " + p);
                    pg.removeClean(p);
                    p.leaveTable(new ModuleType( ModuleType.POKER));
                    p.setRemoved();
                    p.unsetState();
                    bp.removePresence(p);
                    bp.release();
                    _bpv.remove(p.name());
                    deliverResponse(new LeaveResponse(pg, p.pos()));
                }
                pg.prepareForNewRun(); // if the game can be started else mark all as new
                pg.postWin();
                deliverResponse(new GameDetailsResponse(pg));
                _cat.info(_pg.name() + "POSTWIN players=" + _bpv.size());
                for (BotPlayer bpp: _bpv.values())_cat.info(_pg.name() + " players=" + bpp);
                _t.cancel();
            }
        }

    }

    public boolean stop(){
        _cat.info("STOPPING" + this._pg.name()) ;
        _keepRunning = false;
        return true;
    }

    public synchronized boolean start(){
        if (_game_registry.get(_pg.name()) != null){
            _cat.info("This game is already running " + _pg.name());
            for (BotPlayer bpp: _bpv.values())_cat.info(_pg.name() + " players=" + bpp);
            return true;
        }
        _keepRunning = true;
        _game_registry.put(_pg.name(), this) ;
        _pg.stateObserver(this);
        if (_type == RANDOM){
            _cat.info("Starting bot game " + _pg);
            for (int i=0;i<_pg.maxPlayers();i++){
                if (Math.random() > 0.5){
                    BotPlayer bp = BotList.getNext(this);
                    PokerPresence p = bp.createPresence(_pg._name);// create presence
                    p.setPos(i);
                    PokerResponse r = (PokerResponse)_pg.join(p, bp._initialChips);
                    if (_pg.validatePresenceOnTable(p)){
                        _cat.finest(_pg.name() + "Seating=" + p);
                        _bpv.put(bp.name(), bp);
                    }
                    else {
                        _cat.info("Failed Seating=" + p + " on game " + _pg);
                    }
                    deliverResponse(r);
                }
            }
        }
        else if (_type == FULL){
            _cat.info("Starting bot game " + _pg);
            //check if table is empty
            if (_pg.getAllPlayerCount() > 0){
                _cat.warning("Table is not empty");
                return false;
            }
            for (int i=0;i<_pg.maxPlayers();i++){
                BotPlayer bp = BotList.getNext(this);
                PokerPresence p = bp.createPresence(_pg._name);// create presence
                if (_pg.getPlayerAtPos(i) != null){
                    _cat.warning("Another player sitting at this position");
                    continue;
                }
                p.setPos(i);
                PokerResponse r = (PokerResponse)_pg.join(p, bp._initialChips);
                if (_pg.validatePresenceOnTable(p)){
                    _cat.finest(_pg.name() + " Seating=" + p);
                    _bpv.put(bp.name(), bp);
                }
                else {
                    _cat.info("Failed Seating=" + p + " on game " + _pg);
                }
                deliverResponse(r);
            }
        }
        _cat.info(this._pg.toString());
        _t.scheduleAtFixedRate(this, 1000, 2000);
        return true;
    }


    public void deliverResponse(PokerResponse r) {
        GameEvent ge = new GameEvent();
        ge.init(r.getBroadcast());
        String moves[][] = ge.getMove();
        if (ge.getNextMoveString().equals("none")){
            _cat.info(_pg + " NONE=" + _bpv.size());
            return;
        }
        int move_pos =  Integer.parseInt(moves[0][0]);
        int move_id= BotUtils.getMoveId(moves[0][1]);
        if (move_pos == -1 || move_id == Command.M_WAIT){
            _cat.finest("Wait rcvd " + ge.toString());
            return;
        }
        //get poker presence at pos
        PokerPresence p  = _pg.getPlayerAtPos(move_pos);
        _cat.finest("Name " + (p != null ? p.name() : "none") + " move=" + ge.getNextMoveString());
        try {
            // update observers about move
            com.poker.server.GameProcessor.deliverResponse(r);
            _cat.finest("Last move=" + ge.getGameName() + ", " + ge.getLastMoveString() + ", " + ge.getNextMoveString() + ", " + ge.getWinnerString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private synchronized void  seatPlayer(BotGame bg, int pos){
        if (!_keepRunning)return;
        try {
            Poker pg = bg._pg;
            BotPlayer bp = BotList.getNext(this);
            PokerPresence p = bp.createPresence(pg._name);// create presence
            int new_pos=-1;
            if (pg.type().isRandomBotGame()){
                for (int i=0;i<10;i++){
                    new_pos = BotData.randomInt(pg.maxPlayers());
                    PokerPresence pp = pg.getPlayerAtPos(new_pos);
                    if (pp == null){
                        break;
                    }
                }
            }
            p.setPos(new_pos == -1 ? pos : new_pos);
            PokerResponse r = (PokerResponse)pg.join(p, bp._initialChips);
            if (pg.validatePresenceOnTable(p)){
                _cat.finest(pg.name() + " Seating=" + p);
                _bpv.put(bp.name(), bp);
            }
            else {
                _cat.info("Failed Seating=" + p + " on game " + pg);
            }
            if (!(r instanceof SitInResponse)){
                _cat.finest(pos + " = " + (new_pos == -1 ? pos : new_pos) + " " + pg.type().isRandomBotGame() + " for " + p.toString());
                _cat.finest("--->" + r.getBroadcast());
                deliverResponse(r);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
            _cat.severe("Error while seating player in " + bg._pg.name() + " in pos " + pos);
            for (BotPlayer bpp: _bpv.values())_cat.info(_pg.name() + " players=" + bpp);
        }
    }

    public void addMove(BotMove bm){
        if (bm._bp != null  || bm._pos != -1){
            _cat.finest("QUEUE ADD Move=" + (bm._ge != null ? bm._ge.getNextMoveString() : "") + ", " + bm._pos);
            _move_queue.add(bm);
        }
    }

    @Override
    public synchronized void run() {
        try {
            BotMove bm = _move_queue.poll();
            while (bm != null){
                _cat.finest("QUEUE REMOVED Move=" + (bm._ge != null ? bm._ge.getNextMoveString() : "") + ", " + bm._pos);
                if (bm._pos == -1) {
                    Thread.sleep(BotData.randomInt(BotData.NET_DELAY + BotData.randomInt(BotData.MOVE_DELAY )));
                    bm.executeMove();
                }
                else {
                    seatPlayer(bm._bg, bm._pos);
                }
                bm = _move_queue.poll();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
