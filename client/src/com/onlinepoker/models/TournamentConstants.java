package com.onlinepoker.models;

public interface TournamentConstants {

	public class Level {
		public String name;
		public int minGame;
		public int maxGame;
		public int lowBet;
		public int highBet;
		public int smallBlind;
		public int bigBlind;
		
		public Level(String name, int minGame, int maxGame,
					 int lowBet,	int highBet, int smallBlind, int bigBlind) {
			this.name = name;
			this.minGame = minGame;
			this.maxGame = maxGame;
			this.lowBet = lowBet;
			this.highBet = highBet;
			this.smallBlind = smallBlind;
			this.bigBlind = bigBlind;
		}
		
	}
	
	static final int MAX = Integer.MAX_VALUE;
	
	public static final String[] LEVELS_COLUMN_NAMES = {"Level", "From Game", "To Game", "Low Bet", "High Bet", "Small Blind", "Big Blind"};
	public static final Level[] LEVELS = {
		//		Level		Games		Limits			Blinds 
		new Level("I",		1,	10,		15,		30,		10,		15), 
		new Level("II",		11, 20,		25,		50,		15,		25),
		new Level("III",	21,	30, 	50,		100,	25,		50),
		new Level("IV",		31,	40,		100,	200,	50,		100), 
		new Level("V",		41,	50,		200,	400,	100,	200), 
		new Level("VI",		51,	60,		400,	800,	200,	400), 
		new Level("VII",	61,	70, 	600,	1200,	300,	600), 
		new Level("VIII",	71,	MAX,	1000,	2000,	500,	1000)
	};
}
