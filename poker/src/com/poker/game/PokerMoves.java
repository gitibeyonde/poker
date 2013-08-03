package com.poker.game;

import com.golconda.game.Moves;


public class PokerMoves extends Moves {

    //Poker moves
    //###1
    public static final long CHECK = 1;
    public static final long CALL = 2;
    public static final long RAISE = 4;
    public static final long FOLD = 8;

    //###2
    public static final long SBBB = 16;
    public static final long BRINGIN = 32;
    public static final long ALL_IN = 64;
    public static final long ANTE = 128;

    //###3
    public static final long OPT_OUT = 256;
    public static final long BET = 512;
    public static final long BIG_BLIND = 1024;
    public static final long SMALL_BLIND = 2048;

    //###4
    public static final long WAIT = 4096;
    public static final long NONE = 8192;
    public static final long LEAVE = 16384;
    public static final long MOVE = 32768;

    //###5
    public static final long JOIN = 65536;
    public static final long CANCEL = 131072;
    public static final long BET_POT = 262144;
    public static final long BRINGINH = 524288;


    //###6
    public static final long COMPLETE = 1048576;
    public static final long MUCK = 2097152;
    public static final long SHOW = 4194304;
    public static final long U = 8388608;

    //### 7
    public static final long CALL_SHOW = 0x1000000;
    public static final long NOT_INVITED = 0x2000000;
    public static final long JOIN_FAILED = 0x4000000;
    public static final long EVENING = 0x8000000;

    public static final long POKER_MASK = 0xFFFFFFF;

    // VARIOUS MASKS === to be overridden
    public long RESP_REQ = 0xFFF0FF0FFFL;


    static {
        strings = 
                new String[] { "check", "call", "raise", "fold", 
                                "sb-bb", "bringin", "all-in", "ante", 
                                "opt-out", "bet", "big-blind",  "small-blind", 
                                "wait", "none", "leave", "move", 
                               "join", "cancel", "bet-pot", "bringinh", 
                               "complete", "muck", "show", "unused", 
                               "call_show", "not_invited", "join-failed", "evening"
                               };

        values = 
                new long[] { 1, 2, 4, 8, 
                            16, 32, 64, 128, 
                            256, 512, 1024, 2048, 
                             4096, 8192, 16384, 32768, 
                             65536, 131072, 262144,  524288, 
                             1048576, 2097152, 4194304, 8388608,
                            16777216, 33554432, 67108864, 134217728,
                             };

    }

    public PokerMoves(long iv) {
        super(iv);
    }

    public boolean responseRequired() {
        //System.out.println("IntVal = " + intVal() + ", RESP_REQ=" +RESP_REQ );
        return (intVal() & RESP_REQ) > 0;
    }


}
