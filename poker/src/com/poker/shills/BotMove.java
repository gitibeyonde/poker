package com.poker.shills;

import com.golconda.db.ModuleType;
import com.golconda.game.resp.Response;
import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.poker.game.poker.LeaveResponse;
import com.poker.game.poker.Poker;
import com.poker.game.PokerPresence;
import com.poker.game.poker.PokerResponse;

import java.util.logging.Logger;

public class BotMove {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotMove.class.getName());
    BotPlayer _bp;
    PokerPresence _p;
    BotGame _bg;
    GameEvent _ge;
    public PokerResponse _r;
    int _pos;
    long _time;


    public BotMove(PokerPresence p, BotGame pg, PokerResponse r){
        _bg = pg;
        if (p!=null){
            _p = p;
            if (_p.isShill()){
                _bp = BotList.find(_p.name());
            }
        }
        _ge = new GameEvent();
        _ge.init(r.getBroadcast());
        _r = r;
        _pos=-1;
        _time = System.currentTimeMillis();
    }

    public BotMove(BotGame pg, int pos){
        //sit move
        _bg = pg;
        _pos=pos;
        _time = System.currentTimeMillis();
    }

    public void executeMove(){
        String moves[][] = _ge.getMove();
        int move_pos =  Integer.parseInt(moves[0][0]);
        int move_id= BotUtils.getMoveId(moves[0][1]);
        if (move_pos == -1 || move_id == Command.M_WAIT){
            _cat.finest("Wait rcvd " + _ge.toString());
            return;
        }

        _bp._life--;
        _Move mv = this.getBestMove(moves);
        _bg._pg.setMarker(_p);
        _bg._pg.setCurrent(_p);

        //the player has responded in stipulated time with a right move reset resp req
        _p.unsetResponseReq();
        //_cat.info("Player hand = "+ _p.getHand().getAllCardsString() + " Comm cards =" + _pg.getCommunityCards().length );
        Response r=null;
        switch (mv._id){
            case Command.M_SMALLBLIND:
                r = _bg._pg.postSmallBlind(_p, mv._amount);
                break;
            case Command.M_BIGBLIND:
                r = _bg._pg.postBigBlind(_p, mv._amount);
                break;
            case Command.M_CHECK:
                r = _bg._pg.check(_p);
                break;
            case Command.M_CALL:
                r = _bg._pg.call(_p, mv._amount);
                break;
            case Command.M_RAISE:
                r = _bg._pg.raise(_p, mv._amount);
                break;
            case Command.M_BET:
                r = _bg._pg.bet(_p, mv._amount);
                break;
            case Command.M_FOLD:
                if ( _bp._life < 0 || _p.getAmtAtTable() < 2 * _bg._pg.minBet()){
                    _cat.finest("BROKE OR DEAD " + _bp);
                    int pos = _p.pos();
                    _p.setDisconnected();
                    _cat.finest(_p.getGameName() + " Removing " + _p);
                    r = _bg._pg.leave(_p, false);
                    _p.leaveTable(new ModuleType( ModuleType.POKER));
                    _p.unsetState();
                    _bp.removePresence(_p);
                    _cat.finest("PokerResponse=" + r.getBroadcast());
                    if (!(r instanceof LeaveResponse) ){
                        _cat.finest("NOT LEAVE FOLD=" + r.getBroadcast());
                        _bg.deliverResponse((PokerResponse)r);
                    }
                    r = null;
                    _bg._bpv.remove(_p.name());
                    _bp.release();
                    _bg.addMove(new BotMove(_bg, pos));
                }
                else {
                    r = _bg._pg.fold(_p);
                }
                //if (_pg.getAllPlayerCount() < 9 || _pg.getAllPlayerCount() > 10){
                //    throw new IllegalStateException("Count=" + _pg.getAllPlayerCount() + ", " + _pg.getPlayerCount() );
                //}
                break;
            case Command.M_ALL_IN:
                r = _bg._pg.allIn(_p, mv._amount);
                break;
            case Command.M_LEAVE:
                r = _bg._pg.leave(_p, false);
                _bg.addMove(new BotMove(_bg, _p.pos()));
                break;
            default:
                _cat.warning("Move not found " + _ge.getNextMoveString());
        }
        if (r != null && r instanceof PokerResponse){
            _cat.finest("PokerResponse=" + r.getBroadcast() + " " + _bp);
            _bg.deliverResponse((PokerResponse)r);
        }
    }

    public _Move getBestMove(String moves[][]){
        int move_pos =  Integer.parseInt(moves[0][0]);
        PokerPresence move_p  = _bg._pg.getPlayerAtPos(move_pos);
        int hr = BotUtils.handRank(_bg._pg, move_p);  // % rank
        //_cat.info("Rank =" + hr);
        double move_amount = 0;
        if (!moves[0][2].contains("-")) {
            move_amount = Double.parseDouble(moves[0][2]);
        } else {
            String val[] = moves[0][2].split("-");
            double val1 = Double.parseDouble(val[0]);
            double val2 = Double.parseDouble(val[1]);
            move_amount = val1 + BotData.randomInt((int)val2);
        }
        int move_id= BotUtils.getMoveId(moves[0][1]);

        String move_string = _ge.getNextMoveString();
        if ((move_string.contains("small-blind") || move_string.contains("big-blind"))  ){
            move_id = BotUtils.getMoveId(moves[0][1]);
            move_amount = Double.parseDouble(moves[0][2]);
        }
        else if (hr < 20 && (move_string.contains("raise") || move_string.contains("bet") )){
            // gor for raise
            for (int i=0;i<moves.length;i++){
                move_id = BotUtils.getMoveId(moves[i][1]);
                move_amount = Double.parseDouble(moves[i][2]);
                if (move_id == Command.M_RAISE || move_id == Command.M_BET || move_id == Command.M_ALL_IN){
                    break;
                }
            }
        }
        else if (hr < 60 && (move_string.contains("call") || move_string.contains("check"))) {
            // gor for raise
            for (int i=0;i<moves.length;i++){
                move_id = BotUtils.getMoveId(moves[i][1]);
                move_amount = Double.parseDouble(moves[i][2]);
                if (move_id == Command.M_CALL || move_id == Command.M_CHECK){
                    break;
                }
            }
        } else if (hr > 80 &&  move_string.contains("check")) {
            // gor for raise
            for (int i=0;i<moves.length;i++){
                move_id = BotUtils.getMoveId(moves[i][1]);
                move_amount = Double.parseDouble(moves[i][2]);
                if (move_id == Command.M_CHECK){
                    break;
                }
            }
        }
        else if (move_string.contains("fold") || move_string.contains("check")){
            //fold
            for (int i=0;i<moves.length;i++){
                move_id = BotUtils.getMoveId(moves[i][1]);
                move_amount = Double.parseDouble(moves[i][2]);
                if (move_id == Command.M_FOLD || move_id == Command.M_CHECK){
                    break;
                }
            }
        }

        return new _Move(move_id, move_amount);
    }


    class _Move {
        public _Move(int id, double amt){
            _id = id;
            _amount = amt;
        }
        public int _id;
        public double _amount;
    }

}
