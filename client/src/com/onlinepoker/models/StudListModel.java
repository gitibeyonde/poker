package com.onlinepoker.models;
// FOR LOBBY

import javax.swing.event.TableModelEvent;

import com.onlinepoker.SharedConstants;
import com.onlinepoker.resources.Bundle;
import com.poker.game.PokerGameType;

/**
 * Class for managing hold'em table in Lobby.
 */
public class StudListModel
    extends LobbyListModel {
	
  protected static String[] columnNames = {
      Bundle.getBundle().getString("table"),
      Bundle.getBundle().getString("stakes"),
      Bundle.getBundle().getString("type"),
      Bundle.getBundle().getString("players"),
      //Bundle.getBundle().getString("wait"),
      Bundle.getBundle().getString("players.per.flop"),
      Bundle.getBundle().getString("avgpot"),
      Bundle.getBundle().getString("hands.per.hr"),
      
  };

  /** Constructor TableModel with column names
   *  Some rows are filled automaticly
   */
  public StudListModel() {
    super(columnNames);
  }

  public StudListModel(String[] columnNamesX) {
    super(columnNamesX);
  }

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public boolean isLobbyTableAcceptable(LobbyTableModel table) {
    return (table instanceof LobbyStudModel) &&
         !table.isTournamentGame();
  }

  /** return value for JTable. Will is changed for Holdem, Tournament, ...
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    LobbyStudModel one = (LobbyStudModel) rows.get(rowIndex);
    if (one.getName() == null || "".equals(one.getName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return one.getName()+"-pro:"+one.isProPlayer;
//      case 1:
//        return "  " + (one.isRealMoneyTable() ?
//                       Bundle.getBundle().getString("money") :
//                       Bundle.getBundle().getString("points"));
      case 1:
    	  if(one.getGameType() == PokerGameType.Play_Stud || one.getGameType() == PokerGameType.Play_StudHiLo)
    		  return one.getLowBet() / 2 + "/" +(one.getLowBet())+"-pro:"+one.isProPlayer;
    	  else if(one.getGameType() == PokerGameType.Real_Stud || one.getGameType() == PokerGameType.Real_StudHiLo)
    		  return one.getLowBet() / 2 + "/" +(one.getLowBet())+"-pro:"+one.isProPlayer;
    	  
      case 2:
    	  String s = (one.getPlayerCapacity()== 2?"2":one.getPlayerCapacity()== 6?"6":"");
    	  s+=one.getStack().equals("deep")?"d":(one.getStack().equals("shallow")?"s":"");
    	  switch (one.getGameLimitType()) {
	        case PokerConstants.NO_LIMIT:
	          s += "NL";
	          break;
	        case PokerConstants.POT_LIMIT:
	          s += "PL";
	          break;
	        default:
	          s += "FL";
	        break;
	      }return s+"-pro:"+one.isProPlayer;
          
      case 3:
        return "  " +
            (Integer.toString(one.getPlayerCount()) + "/" +
             Integer.toString(one.getPlayerCapacity()))+"-pro:"+one.isProPlayer;
//      case 4:
//          return "  " ;
      case 4:
          return SharedConstants.chipToMoneyString(one.getAveragePot())+"-pro:"+one.isProPlayer;
      case 5:
        return SharedConstants.percentsToIntString(one.getFlop())+"-pro:"+one.isProPlayer;
      case 6:
        return SharedConstants.intToString(one.getHandsPerHour())+"-pro:"+one.isProPlayer;
    }
    return "Err";
  }

  /**
     * This method called by LobbyServerProxy when application receives
     * changed poker table states.
     */
  public void studTableListUpdated(LobbyTableModel[] changes) {
      synchronized (this) {
        for (int i = 0; i < changes.length; i++) {
          if (isLobbyTableAcceptable(changes[i])&& changes[i].getGameLimitType() != PokerConstants.TOURNAMENT) {
            //System.out.println("STUD Updated = " + changes[i].getName()  + ", " + changes[i].getGameLimitType());
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
              //}
            }
          }
        }
      }
    }

  public LobbyStudModel getOneRow(int rowIndex) {
    return ( (LobbyStudModel) rows.get(rowIndex));
  }

  public int getID() {
    return (STUD_TABLE);
  }

	@Override
	public boolean isLobbyTableAcceptable(LobbyTournyModel table) {
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
	public void tournyListUpdated(LobbyTournyModel[] changes) {
		// TODO Auto-generated method stub
		
	}




}
