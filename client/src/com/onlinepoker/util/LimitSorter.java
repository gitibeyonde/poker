package com.onlinepoker.util;

import com.onlinepoker.models.LobbyHoldemModel;
import com.onlinepoker.models.LobbyOmahaModel;
import com.onlinepoker.models.PokerConstants;


public class LimitSorter {
	private int gameType_col1, gameType_cg_col3;
	private static int gameType_cg_col4;
	public static LobbyHoldemModel[] getHoldemLimitedList(LobbyHoldemModel[] model)
	  {
		  LobbyHoldemModel[] newltm = new LobbyHoldemModel[model.length];
		  for (int v = 0; v < model.length; v++) 
		  {
			 if(gameType_cg_col4 == GameTypes.TYPE_RINGGAME_1_NOLIMIT)
			 {
				  if(model[v].getGameLimitType() == PokerConstants.NO_LIMIT)
				  newltm[v] = model[v];
			 }
			 else if(gameType_cg_col4 == GameTypes.TYPE_RINGGAME_1_POTLIMIT)
			 {
				 if(model[v].getGameLimitType() == PokerConstants.POT_LIMIT)
	    		 newltm[v] = model[v];
			 }
			 else if(gameType_cg_col4 == GameTypes.TYPE_RINGGAME_1_FIXEDLIMIT)
			 {
				 if(model[v].getGameLimitType() == PokerConstants.REGULAR)
	    		 newltm[v] = model[v];
			 }
			 else
			 {
				 newltm[v] = model[v];
			 }
	      }
		  return newltm;
	  }
	  public static  LobbyOmahaModel[] getOmahaLimitedList(LobbyOmahaModel[] model)
	  {
		  LobbyOmahaModel[] newltm = new LobbyOmahaModel[model.length];
		  for (int v = 0; v < model.length; v++) 
		  {
			 if(gameType_cg_col4 == GameTypes.TYPE_RINGGAME_1_NOLIMIT)
			 {
				  if(model[v].getGameLimitType() == PokerConstants.NO_LIMIT)
				  newltm[v] = model[v];
			 }
			 else if(gameType_cg_col4 == GameTypes.TYPE_RINGGAME_1_POTLIMIT)
			 {
				 if(model[v].getGameLimitType() == PokerConstants.POT_LIMIT)
	    		 newltm[v] = model[v];
			 }
			 else if(gameType_cg_col4 == GameTypes.TYPE_RINGGAME_1_FIXEDLIMIT)
			 {
				 if(model[v].getGameLimitType() == PokerConstants.REGULAR)
	    		 newltm[v] = model[v];
			 }
			 else
			 {
				 newltm[v] = model[v];
			 }
	      }
		  return newltm;
	  }
}
