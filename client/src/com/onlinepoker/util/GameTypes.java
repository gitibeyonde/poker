package com.onlinepoker.util;

public class GameTypes {
	public static final int GAMETYPES_COLUMN1 = 1;
	public static final int GAMETYPES_COLUMN2 = 2;
	public static final int GAMETYPES_COLUMN3 = 3;
	public static final int GAMETYPES_COLUMN4 = 4;
	
	public static final int TYPE_RINGGAME = 0;
	public static final int TYPE_SITNGO = 1;
	public static final int TYPE_TOURNAMENT = 2;
	public static final int TYPE_TERMINALPOKER = 3;
	
	public static final int TYPE_RINGGAME_0_HOLDEM = 0;
	public static final int TYPE_RINGGAME_0_OMAHAHI = 1;
	public static final int TYPE_RINGGAME_0_OMAHAHILO =2;
	public static final int TYPE_RINGGAME_0_STUD = 3;
	public static final int TYPE_RINGGAME_0_STUDHILO = 4;
	//public static final int TYPE_RINGGAME_0_MIXED = 7;
	
	public static final int TYPE_RINGGAME_1_ALLTYPES = 0;
	public static final int TYPE_RINGGAME_1_FIXEDLIMIT = 1;
	public static final int TYPE_RINGGAME_1_POTLIMIT = 2;
	//public static final int TYPE_RINGGAME_1_MIXEDLIMIT = 3;
	public static final int TYPE_RINGGAME_1_NOLIMIT = 3;
	
	public static final int TYPE_SITNGO_0_ALL = 0;
	public static final int TYPE_SITNGO_0_1TABLE = 1;
	public static final int TYPE_SITNGO_0_6HANDED = 2;
	public static final int TYPE_SITNGO_0_HEADSUP = 3;
	
	public static final int TYPE_SITNGO_1_HOLDEM = 0;
	public static final int TYPE_SITNGO_1_OMAHAHI = 1;
	public static final int TYPE_SITNGO_1_OMAHAHILO = 2;
	public static final int TYPE_SITNGO_1_STUD = 3;
	public static final int TYPE_SITNGO_1_STUDHILO = 4;
	//public static final int TYPE_SITNGO_1_MIXED = 21;
	
	public static final int TYPE_SITNGO_2_ALLTYPES = 0;
	public static final int TYPE_SITNGO_2_FIXEDLIMIT = 1;
	public static final int TYPE_SITNGO_2_POTLIMIT = 2;
	//public static final int TYPE_SITNGO_2_MIXEDLIMIT = 3;
	public static final int TYPE_SITNGO_2_NOLIMIT = 3;
	
	public static final int TYPE_TOURNAMENT_0_ALL = 0;
	public static final int TYPE_TOURNAMENT_0_CASH = 1;
	public static final int TYPE_TOURNAMENT_0_GUARANTEE = 2;
	public static final int TYPE_TOURNAMENT_0_SATELLITE = 3;
	
	public static final int TYPE_TOURNAMENT_1_HOLDEM = 0;
	public static final int TYPE_TOURNAMENT_1_OMAHAHI = 1;
	public static final int TYPE_TOURNAMENT_1_OMAHAHILO = 2;
	public static final int TYPE_TOURNAMENT_1_STUD = 3;
	public static final int TYPE_TOURNAMENT_1_STUDHILO = 4;
	//public static final int TYPE_TOURNAMENT_1_MIXED = 35;
	
	public static final int TYPE_TOURNAMENT_2_ALLTYPES = 0;
	public static final int TYPE_TOURNAMENT_2_FIXEDLIMIT = 1;
	public static final int TYPE_TOURNAMENT_2_POTLIMIT = 2;
	//public static final int TYPE_TOURNAMENT_2_MIXEDLIMIT = 3;
	public static final int TYPE_TOURNAMENT_2_NOLIMIT = 3;
	
	public static final double CG_REAL_Filter_MICRO_MIN = 0.01;
	public static final double CG_REAL_Filter_MICRO_MAX = 1.0;
	public static final double CG_REAL_Filter_LOW_MIN = 1.0;
	public static final double CG_REAL_Filter_LOW_MAX = 5.0;
	public static final double CG_REAL_Filter_MEDIUM_MIN = 5.0;
	public static final double CG_REAL_Filter_MEDIUM_MAX = 10.0;
	public static final double CG_REAL_Filter_HIGH_MIN = 10.0;
	public static final double CG_REAL_Filter_HIGH_MAX = -1;
	
	public static final double CG_PLAY_Filter_MICRO_MIN = 0.01;
	public static final double CG_PLAY_Filter_MICRO_MAX = 1.0;
	public static final double CG_PLAY_Filter_LOW_MIN = 1.0;
	public static final double CG_PLAY_Filter_LOW_MAX = 5.0;
	public static final double CG_PLAY_Filter_MEDIUM_MIN = 5.0;
	public static final double CG_PLAY_Filter_MEDIUM_MAX = 10.0;
	public static final double CG_PLAY_Filter_HIGH_MIN = 10.0;
	public static final double CG_PLAY_Filter_HIGH_MAX = -1;
	
	public static final double SNG_REAL_Filter_MICRO_MIN = 0;
	public static final double SNG_REAL_Filter_MICRO_MAX = 3.0;
	public static final double SNG_REAL_Filter_LOW_MIN = 5.0;
	public static final double SNG_REAL_Filter_LOW_MAX = 15.0;
	public static final double SNG_REAL_Filter_MEDIUM_MIN = 15.0;
	public static final double SNG_REAL_Filter_MEDIUM_MAX = 35.0;
	public static final double SNG_REAL_Filter_HIGH_MIN = 35.0;
	public static final double SNG_REAL_Filter_HIGH_MAX = -1;
	
	public static final double SNG_PLAY_Filter_MICRO_MIN = 100.0;
	public static final double SNG_PLAY_Filter_MICRO_MAX = 100.0;
	public static final double SNG_PLAY_Filter_LOW_MIN = 100.0;
	public static final double SNG_PLAY_Filter_LOW_MAX = 250.0;
	public static final double SNG_PLAY_Filter_MEDIUM_MIN = 250;
	public static final double SNG_PLAY_Filter_MEDIUM_MAX = 1000;
	public static final double SNG_PLAY_Filter_HIGH_MIN = 1000;
	public static final double SNG_PLAY_Filter_HIGH_MAX = 1000;
	
	
	public static final double MTT_REAL_Filter_MICRO_MIN = 0;
	public static final double MTT_REAL_Filter_MICRO_MAX = 3.0;
	public static final double MTT_REAL_Filter_LOW_MIN = 5.0;
	public static final double MTT_REAL_Filter_LOW_MAX = 15.0;
	public static final double MTT_REAL_Filter_MEDIUM_MIN = 15.0;
	public static final double MTT_REAL_Filter_MEDIUM_MAX = 35.0;
	public static final double MTT_REAL_Filter_HIGH_MIN = 35.0;
	public static final double MTT_REAL_Filter_HIGH_MAX = -1;
	
	public static final double MTT_PLAY_Filter_MICRO_MIN = 100.0;
	public static final double MTT_PLAY_Filter_MICRO_MAX = 100.0;
	public static final double MTT_PLAY_Filter_LOW_MIN = 100.0;
	public static final double MTT_PLAY_Filter_LOW_MAX = 250.0;
	public static final double MTT_PLAY_Filter_MEDIUM_MIN = 250;
	public static final double MTT_PLAY_Filter_MEDIUM_MAX = 1000;
	public static final double MTT_PLAY_Filter_HIGH_MIN = 1000;
	public static final double MTT_PLAY_Filter_HIGH_MAX = 1000;
	
	
}
