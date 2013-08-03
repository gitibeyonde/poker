package com.poker.shills;

import com.golconda.db.*;
import com.golconda.game.Game;
import com.poker.game.poker.PokerResponse;
import com.golconda.message.GameEvent;
import com.poker.game.PokerPlayer;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;


public class BotPlayer extends PokerPlayer {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotPlayer.class.getName());


    public int _life=-1;
    public BotGame _bg;
    public double _initialChips;


    public BotPlayer(String name){
        super(name);
        this._shill = true;
        _life =  (BotData.randomInt(BotData.LIFE) + 10);
    }

    public void attach(BotGame bg){
        _bg = bg;
    }

    public void release(){
        _life =  -1 * BotData.randomInt(BotData.LIFE);
        _cat.finest("Releasing player " + this);
        BotList.putBack(this);
    }

    public static void release(String name){
        BotPlayer bp = BotList.find(name);
        bp.release();
    }


    public String toString(){
        return super.toString() + " life=" + _life;
    }


}
