package com.golconda.game.util;

public interface ActionConstants {
    
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int CLOSING = 2;
    public static final int DELETED = 3;
    public static final int PURGED = 4;
    public final static int TABLE_OPEN = 1;
    public final static int HAND_RUNNING = 2;
    public final static int TABLE_CLOSED = 4;
    
  public static final int BLACKJACK = 1;
  public static final int CRAPS = 2;
  public static final int BACCARAT = 3;
  public static final int ROULETTE = 4;
  public static final int VIDEO_POKER = 5;
  public static final int SLOTS = 6;
  public static final int KENO = 7;
  public static final int HOLDEM = 8;

  public static final int DEALER = 1;
  public static final int PLAYER = 2;
  public static final int TABLE = 3;

  // stages
  public static final int PREGAME = 0;
  public static final int START_GAME = 1; //ACTION_TYPE_STAGE
  public static final int SMALL_BLIND = 2; //ACTION_TYPE_BETTING
  public static final int BIG_BLIND = 3; //ACTION_TYPE_BETTING
  public static final int PREFLOP = 4; //ACTION_TYPE_STAGE
  public static final int FLOP = 5; //ACTION_TYPE_STAGE
  public static final int TURN = 6; //ACTION_TYPE_STAGE
  public static final int RIVER = 7; //ACTION_TYPE_STAGE
  public static final int FOURTH_STREET = 8; //ACTION_TYPE_STAGE
  public static final int SHOWDOWN = 9;
  public static final int END_GAME = 10; //ACTION_TYPE_STAGE
  public static final int DEALING = 11; //ACTION_TYPE_CARD
  public static final int TMP_CONSTANT = 12;

  // game actions
  public static final int FOLD = 100; //ACTION_TYPE_BETTING
  public static final int CALL = 101; //ACTION_TYPE_BETTING
  public static final int CHECK = 102; //ACTION_TYPE_BETTING
  public static final int ALLIN = 103; //ACTION_TYPE_BETTING
  public static final int RAISE = 104; //ACTION_TYPE_BETTING
  public static final int BET = 105; //ACTION_TYPE_BETTING
  public static final int WIN = 106; //ACTION_TYPE_STAGE
  public static final int PRE_WIN = 107; //ACTION_TYPE_STAGE
  public static final int CHECK_FOLD = 108; //ACTION_TYPE_BETTING
  public static final int CHECK_CALL = 109; //ACTION_TYPE_BETTING
  public static final int CHECK_CALL_ANY = 110; //ACTION_TYPE_BETTING
  public static final int RAISE_ANY = 111; //ACTION_TYPE_BETTING
  public static final int NO_PREBET = 112; //ACTION_TYPE_BETTING
  public static final int SB_BB = 113; //ACTION_TYPE_BETTING
  public static final int ANTE = 114; //ACTION_TYPE_BETTING
  public static final int BRINGIN = 115; //ACTION_TYPE_BETTING
  public static final int WAIT = 116; //ACTION_TYPE_BETTING
 public static final int MORNING = 117; //ACTION_TYPE_BETTING
 public static final int AFTERNOON = 118; //ACTION_TYPE_BETTING
 public static final int EVENING = 119; //ACTION_TYPE_BETTING
 public static final int HIT = 120;
 public static final int STAND = 121;
 public static final int SURRENDER = 122;
 public static final int INSURANCE = 123;
 public static final int DOUBLE_DOWN = 124;
 public static final int SPLIT = 125;
 public static final int CANCEL = 126;
 public static final int BRING_IN_HIGH = 127;
 public static final int PLAY_SEEN = 128;
 public static final int CALL_SHOW = 129;
 public static final int SHOW = 130;
 public static final int OPEN_ONE = 131;
 public static final int OPEN_TWO = 132;
 public static final int OPEN_THREE = 133;
 public static final int PING = 134;
 public static final int DD = 135;
 public static final int HALF = 136;
 public static final int FULL = 137;
 public static final int CASINO_PLAY = 138;
 public static final int TOURNAMENT_WIN = 139;
 public static final int BONUS = 140;
 public static final int POST_WIN = 141;
 public static final int BID = 142;
 public static final int PASS = 143;
 public static final int CLEAR = 144;



  // auxiliary actions
  public static final int BET_REQUEST = 200; //ACTION_TYPE_BETTING
  public static final int BIG_BLIND_REQUEST = 201; //ACTION_TYPE_BETTING
  public static final int SMALL_BLIND_REQUEST = 202; //ACTION_TYPE_BETTING
  public static final int BOTH_BLIND_REQUEST = 203;
  public static final int MISSED_BIG_BLIND_REQUEST = 204; //ACTION_TYPE_BETTING
  public static final int MISSED_SML_BLIND_REQUEST = 205; //ACTION_TYPE_BETTING
  public static final int SHOW_CARD = 206; //ACTION_TYPE_CARD
  public static final int SHOW_SHOWDOWN_CARD = 207;
  public static final int SHOW_WINNER_CARD = 208; //ACTION_TYPE_SIMPLE
  public static final int SET_CURRENT = 209; //ACTION_TYPE_SIMPLE
  public static final int SHOWDOWN_REQUEST = 210; //ACTION_TYPE_BETTING
  public static final int YOUR_TURN = 211;
  public static final int TABLE_INFO = 212; //ACTION_TYPE_TABLE_SERVER
  public static final int WAITER_CAN_JOIN = 213; //ACTION_TYPE_TABLE_SERVER
  public static final int CHAT = 214; //ACTION_TYPE_STAGE
  public static final int NEW_HAND = 215; //ACTION_TYPE_TABLE_SERVER
  public static final int CASHIER = 216; //ACTION_TYPE_STAGE
  public static final int NO_SHOWDOWN = 217;
  public static final int HIDE_CARD = 218;
  public static final int PAUSE = 219;
  public static final int PLAYER_WAITS_BIG_BLIND = 220; //ACTION_TYPE_SIMPLE
  public static final int PLAYER_WAITS_DEALER_PASSES = 221; //ACTION_TYPE_SIMPLE
  public static final int DUMP = 222; //ACTION_TYPE_SIMPLE
  public static final int DECK = 223; //ACTION_TYPE_SIMPLE
  public static final int DISCARD = 224; //ACTION_TYPE_SIMPLE
  public static final int PICK = 225; //ACTION_TYPE_SIMPLE
  public static final int BACCARAT_CARDS = 226; //ACTION_TYPE_SIMPLE
  public static final int ROCKET = 227; //ACTION_TYPE_SIMPLE
  public static final int BOMB = 228;
  public static final int NEXT_MOVE = 229;
  public static final int TOTALBET_INFO = 230; //ACTION_TYPE_TABLE_SERVER
  
  // Agneya NEW
  public static final int PLAYER_MESSAGE = 251; //ACTION_TYPE_STAGE
  public static final int MAKE_POT = 252;
  public static final int SET_BUTTON = 253; //ACTION_TYPE_SIMPLE
  public static final int SET_COP = 254; //ACTION_TYPE_SIMPLE

  // END NEW
  // server actions
  public static final int PLAYER_REGISTERED = 300;
  public static final int PLAYER_UNREGISTERED = 301;
  public static final int PLAYER_JOIN = 302;
  public static final int PLAYER_LEAVE = 303; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_SITIN = 304; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_SITOUT = 305; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_REJOIN = 306;
  public static final int PLAYER_NEEDS_SITOUT = 307; //ACTION_TYPE_TABLE_SERVER
  public static final int SESSION_TIMEOUT = 308;
  public static final int IMMEDIATE_SHUTDOWN = 309; //ACTION_TYPE_TABLE_SERVER
  public static final int GRACEFUL_SHUTDOWN = 310; //ACTION_TYPE_TABLE_SERVER
  public static final int ADD_TO_WAITERS = 311;
  public static final int REMOVE_FROM_WAITERS = 312;
  public static final int STARTUP = 314;
  public static final int CHANGE_STATE = 315;
  public static final int MANUAL_IMMEDIATE_SHUTDOWN = 316; //ACTION_TYPE_TABLE_SERVER
  public static final int MANUAL_GRACEFUL_SHUTDOWN = 317; //ACTION_TYPE_TABLE_SERVER
  public static final int MANUAL_STARTUP = 318; //ACTION_TYPE_TABLE_SERVER
  public static final int MANUAL_CHANGE_STATE = 319; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_KICKED_OUT = 320; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_NEEDS_SITIN_TRUE = 321; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_NEEDS_SITIN_FALSE = 322; //ACTION_TYPE_TABLE_SERVER
  public static final int IS_ACCEPTING = 323; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_POST_JOIN = 324;
  public static final int PLAYER_REMOVE = 325;
  public static final int PLAYER_POST_REMOVE = 326;
  public static final int SIDEBAR_INFO = 327; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_NONE = 329; //ACTION_TYPE_SIMPLE
  public static final int PLAYER_SEAT_AVAILABLE = 330; //ACTION_TYPE_SIMPLE

  // error actions
  public static final int PLACE_OCCUPIED = 400; //ACTION_TYPE_ERROR
  public static final int UNSUFFICIENT_FUND = 401; //ACTION_TYPE_ERROR
  public static final int DECISION_TIMEOUT = 402;
  public static final int NO_MORE_WAITING = 403;

  /**
   * Player tried to register on table where another player from the same
   * remote host already has registered.
   */
  public static final int SAME_REMOTE_HOST = 404; //ACTION_TYPE_ERROR

  /**
   * Player tried to join table and there is a wait claim.
   */
  public static final int THERE_IS_CLAIM = 405; //ACTION_TYPE_ERROR

  /** Player have already staied in waiting list */
  public static final int ALREADY_WAIT = 406;

  /** Table not in ONLINE mode */
  public static final int TABLE_IS_OFFLINE = 407; //ACTION_TYPE_ERROR

  /** Cannot call to cashier for this game type (tournament) */
  public static final int CASHIER_UNAVAIBLE = 408; //ACTION_TYPE_ERROR

  /** Something strange happened  */
  public static final int UNKNOWN_ERROR = 409; //ACTION_TYPE_ERROR

  /** There is no such player session  */
  public static final int UNKNOWN_SESSION = 410;
  public static final int DELAY = 420;
  public static final int UPDATE = 421;

  // action types
  public static final int ACTION_TYPE_SIMPLE = 1000; //	ok
  public static final int ACTION_TYPE_STAGE = 1001; //	ok
  public static final int ACTION_TYPE_CARD = 1002; //	ok
  public static final int ACTION_TYPE_BETTING = 1003; //	ok
  public static final int ACTION_TYPE_TABLE_SERVER = 1004; //	ok
  public static final int ACTION_TYPE_ERROR = 1005; //	ok
  public static final int ACTION_TYPE_PREBETTING = 1006; //	--
  public static final int ACTION_TYPE_LASTMOVE = 1007; //
 public static final int ACTION_TYPE_KENO_PLAY = 1008; //
 public static final int ACTION_TYPE_KENO_RESULT = 1009; //
 public static final int ACTION_TYPE_BACCARAT_PLAY = 1010; //
 public static final int ACTION_TYPE_BACCARAT_RESULT = 1011; //
 public static final int ACTION_TYPE_BLACKJACK_RESULT = 1012; //
 public static final int ACTION_TYPE_BINGO_CALLED = 1013;
 public static final int ACTION_TYPE_BINGO_GET_TICKETS = 1014;
 public static final int ACTION_TYPE_SLOTS_RESULT = 1015; //
 public static final int ACTION_TYPE_SLOTS_PLAY = 1016; //
 public static final int ACTION_TYPE_VP_RESULT = 1017; //
 public static final int ACTION_TYPE_VP_PLAY = 1018; //
 public static final int ACTION_TYPE_CRAPS_RESULT = 1019; //
  public static final int ACTION_TYPE_CRAPS_PLAY = 1020; //
  public static final int ACTION_TYPE_ROULETTE_RESULT = 1019; //
  public static final int ACTION_TYPE_ROULETTE_PLAY = 1020; //
  public static final int ACTION_TYPE_TAMBOLA_CALLED = 1021;
  public static final int ACTION_TYPE_TAMBOLA_GET_TICKETS = 1022; 
  public static final int ACTION_TYPE_HOLDEM_RESULT = 1023; //
  public static final int ACTION_TYPE_DIZHU_OPEN = 1024; //

 
  public static final int BOARD_TARGET = -1;
  public static final int DEALER_TARGET = 0;

}
