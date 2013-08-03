package com.onlinepoker.models;


import javax.swing.event.TableModelEvent;

import com.onlinepoker.resources.Bundle;

/**
 * Class for managing hold'em table in Lobby.
 */
public class TournamentListModel
    extends LobbyListModel {


  protected static String[] columnNames = {
	  Bundle.getBundle().getString("date"),
	  Bundle.getBundle().getString("game"),
	  Bundle.getBundle().getString("type"),
	  Bundle.getBundle().getString("buyin"),
	  Bundle.getBundle().getString("status"),
	  Bundle.getBundle().getString("players"),
//      Bundle.getBundle().getString("table"),
//      Bundle.getBundle().getString("buyin"),
//      Bundle.getBundle().getString("starting.at"),
//      Bundle.getBundle().getString("time.left"),
//      Bundle.getBundle().getString("currently"),
//      Bundle.getBundle().getString("state"),
//      Bundle.getBundle().getString("stakes"),
//      Bundle.getBundle().getString("chips")
  };

  /**
   * Constructor TableModel with column names
   * Some rows are filled automaticly
   */
  public TournamentListModel() {
    super(columnNames);
  }

  public TournamentListModel(String[] columnNamesX) {
    super(columnNamesX);
  }

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public boolean isLobbyTableAcceptable(LobbyTournyModel table) {
	  return (table instanceof LobbyTournyModel); 
  }

  /**
   * return value for JTable. Will is changed for Holdem, Tournament, ...
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    LobbyTournyModel one = (LobbyTournyModel) rows.get(rowIndex);
    if (one.getName() == null || "".equals(one.getName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return one.getSchedule();
      case 1:
        return one.getName().substring(one.getName().indexOf("_")+1);//one.getFee() + "/" + one.getMinBuyIn();
      case 2:
	    if(one.getTournamentLimit() == 0) return "PL";
	    else if(one.getTournamentLimit() == -1) return "NL";
	    else return "FL";
      
      case 3:
        return one.getTournamentBuyIn()+ "+"+one.getTournamentFee();//(one.getDelta() > 0 ? one.getDelta() > 60 ?one.getDelta()/60+"hr "+one.getDelta()%60+"m":one.getDelta()%60+"m" :"Running");
     
      case 4:
          return one.getStateString();
          
      case 5:
        return one.getTournamentPlayerCount();
      
      
        
    }
    return "Err";
  }
  	
  @Override
  public void tableListUpdated(LobbyTableModel[] changes) {
  	// TODO Auto-generated method stub
  	
  }
  
   public String getName(int row_number) {
    if ( (rows.size() <= row_number) || (row_number < 0)) {
      return "";
    }
    return ( (LobbyTableModel) rows.get(row_number)).getName();
  }

  public LobbyTournyModel getOneRow(int rowIndex) {
    return ( (LobbyTournyModel) rows.get(rowIndex));
  }

  /* (non-Javadoc)
   * @see com.onlinepoker.client.LobbyListModel#getID()
   */
  public int getName() {
    return (TOURNAMENT_TABLE);
  }


	
	@Override
	public boolean isLobbyTableAcceptable(LobbyTableModel table) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void holdemTableListUpdated(LobbyTableModel[] changes) {
		// TODO Auto-generated method stub
		
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

	public void tournamentListUpdated(LobbyTournyModel[] changes) {
	  synchronized (this) {
    	for (int i = 0; i < changes.length; i++) {
          if (isLobbyTableAcceptable(changes[i]))//&& changes[i].getGameLimitType() != PokerConstants.TOURNAMENT) 
          {
            int index = rows.indexOf(changes[i]);
            boolean forDel = isTableModelMustDelete(changes[i].getState());
            if (index >= 0) {
              if (!forDel) {
                rows.set(index, changes[i]);
                fireModelEvent(index, TableModelEvent.UPDATE);
              }
              else {
                rows.remove(index);
                fireModelEvent(index, TableModelEvent.DELETE);
              }
            }
            else if (!forDel) {
              //if (changes[i].getState() == LobbyTableModel.ONLINE) {
              if (rows.add(changes[i])) {
                fireModelEvent(rows.size() - 1, TableModelEvent.INSERT);
              }
            }
          }
        }
      }
	}

	@Override
	public void tournyListUpdated(LobbyTournyModel[] changes) {
		// TODO Auto-generated method stub
		
	}

}
