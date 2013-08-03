package com.onlinepoker.models;
// FOR LOBBY

import com.onlinepoker.SharedConstants;
import com.onlinepoker.proxies.LobbyModelsChangeListener;
import com.onlinepoker.resources.Bundle;

/**
 * Class for managing hold'em table in Lobby.
 */
public class TableListModel
    extends LobbyListModel implements LobbyModelsChangeListener{
	
  protected static String[] columnNames = {
      Bundle.getBundle().getString("table"),
      Bundle.getBundle().getString("stakes"),
      Bundle.getBundle().getString("type"),
      Bundle.getBundle().getString("players"),
      //Bundle.getBundle().getString("wait"),
      Bundle.getBundle().getString("avgpot"),
      Bundle.getBundle().getString("players.per.flop"),
      Bundle.getBundle().getString("hands.per.hr"),
      
  };

  /** Constructor TableModel with column names
   *  Some rows are filled automaticly
   */
  public TableListModel() {
    super(columnNames);
  }

  public TableListModel(String[] columnNamesX) {
    super(columnNamesX);
  }

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
   
   public boolean isLobbyHoldemTableAcceptable(LobbyTableModel table) {
    return (table instanceof LobbyHoldemModel) &&
         !table.isTournamentGame();
  }
   
  public boolean isLobbyTableAcceptable(LobbyTableModel table) {
    return (table instanceof LobbyTableModel) &&
         !table.isTournamentGame();
  }

  /** return value for JTable. Will is changed for Holdem, Tournament, ...
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
	  LobbyTableModel one = (LobbyTableModel) rows.get(rowIndex);
    if (one.getName() == null || "".equals(one.getName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return one.getName();

      case 1:
          return one.getMinBet()+ "/" +(one.getMaxBet()> 0 ? one.getMaxBet(): 2 * one.getMinBet());
        
        case 2:
      	  String s = "";
      	  s += one.gameLimitTypeToString();
          return s;
          
      case 3:
        return "  " +
            (Integer.toString(one.getPlayerCount()) + "/" +
             Integer.toString(one.getPlayerCapacity()));
//      case 4:
//          return "  " ;
      
      case 4:
          return "  " + SharedConstants.chipToMoneyString(one.getAveragePot());
      
      case 5:
        return "  " + SharedConstants.percentsToIntString(one.getPlayerCount());
      
      case 6:
        return "  " + SharedConstants.intToString(one.getHandsPerHour());
    }
    return "Err";
  }

 
    
  public LobbyTableModel getOneRow(int rowIndex) {
    return ( (LobbyTableModel) rows.get(rowIndex));
  }

  public int getID() {
    return (HOLDEM_TABLE);
  }

  public boolean isLobbyTableAcceptable(LobbyTournyModel table) {
	// TODO Auto-generated method stub
	return false;
  }
  
  /**
   * This method called by LobbyServerProxy when application receives
   * changed poker table states.
   */
  	public void tableListUpdated(LobbyTableModel[] changes) {
    }
	
	@Override
	public void omahaTableListUpdated(LobbyTableModel[] changes) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sitnGoTableListUpdated(LobbyTableModel[] changes) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void studTableListUpdated(LobbyTableModel[] changes) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void tournyListUpdated(LobbyTournyModel[] changes) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void holdemTableListUpdated(LobbyTableModel[] changes) {
		// TODO Auto-generated method stub
		
	}
  
}
