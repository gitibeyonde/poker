package bap.texas.util;

public class Constants {

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
	  public static final int SHOWDOWN_ACTION = 14;
	  
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
	  
	  
	  
	  public static final int M_OPEN = 0;
	  public static final int M_CHECK = 1;
	  public static final int M_CALL = 2;
	  public static final int M_RAISE = 3;
	  public static final int M_FOLD = 4;
	  public static final int M_PICK = 5;
	  public static final int M_DUMP = 6;
	  public static final int M_JOIN = 8;
	  public static final int M_LEAVE = 9;
	  public static final int M_SIT_IN = 10;
	  public static final int M_OPT_OUT = 11;
	  public static final int M_WAIT = 12;
	  public static final int M_BIGBLIND = 14;
	  public static final int M_SMALLBLIND = 15;
	  public static final int M_BET = 16;
	  public static final int M_ANTE = 17;
	  public static final int M_ALL_IN = 18;
	  public static final int M_BRINGIN = 19;
	  public static final int M_SBBB = 20;
	  public static final int M_BET_POT = 21;
	  public static final int M_CANCEL_JOIN = 22;
	  public static final int M_COMPLETE = 23;
}
