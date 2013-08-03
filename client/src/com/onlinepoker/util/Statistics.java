package com.onlinepoker.util;

public class Statistics {
	public int saw_flop;
	public int saw_turn;
	public int saw_river;
	public int saw_showdown;
	
	public  int won_preflop;
	public  int won_flop;
	public  int won_turn;
	public  int won_river;
	public  int won_showdown;
	
	public  double won_amt_preflop;
	public  double won_amt_flop;
	public  double won_amt_turn;
	public  double won_amt_river;
	public  double won_amt_showdown;
	
	
	public  void ressetStats()
	{
		saw_flop = 0;
		saw_turn = 0;
		saw_river = 0;
		saw_showdown = 0;
		
		won_preflop = 0;
		won_flop = 0;
		won_turn = 0;
		won_river = 0;
		won_showdown = 0;
		
		won_amt_preflop = 0.0; 
		won_amt_flop = 0.0;
		won_amt_turn = 0.0;
		won_amt_river = 0.0;
		won_amt_showdown = 0.0;
	}
}

