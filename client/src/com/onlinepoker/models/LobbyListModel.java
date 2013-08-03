package com.onlinepoker.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.onlinepoker.SharedConstants;
import com.onlinepoker.proxies.LobbyModelsChangeListener;

/**
 * Abstract class of Lobby's tables. Are basic for any lobby room
 */
public abstract class LobbyListModel
implements TableModel, LobbyModelsChangeListener {


  public static final int SIMPLE_TABLE = 0;
  public static final int HOLDEM_TABLE = 1;
  public static final int OMAHA_TABLE = 2;
  public static final int STUD_TABLE = 3;
  public static final int TOURNAMENT_TABLE = 4;
  public static final int MIXED_TABLE = 5;
  public static final int OMAHA_Real_TABLE = 6;
  public static final int STUD_Real_TABLE = 7;
  public static final int SITNGO_TABLE = 8;
  public static final int TOURNAMENT_Real_TABLE = 9;

  protected List rows = new ArrayList();
  private List listeners = new ArrayList();
  private String[] columnNames = null;

  public LobbyListModel(String[] columnNames) {
    this.columnNames = columnNames;
  }

  public int getID() {
    return (SIMPLE_TABLE);
  }

  public int getRowCount() {
    return rows.size();
  }

  public final int getColumnCount() {
    return columnNames.length;
  }

  public final String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  public final Class getColumnClass(int columnIndex) {
    return String.class;
  }

  public final boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  public final void setValueAt(Object aValue, int rowIndex, int columnIndex) {
  }

  public final void addTableModelListener(TableModelListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  public final void removeTableModelListener(TableModelListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  protected final void fireModelEvent(int row, int type) {
    TableModelEvent event
        = row >= 0 ? new TableModelEvent(this, row, row,
                                         TableModelEvent.ALL_COLUMNS,
                                         type) :
        new TableModelEvent(this);
    for (Iterator i = listeners.iterator(); i.hasNext(); ) {
      TableModelListener listener = (TableModelListener) i.next();
      listener.tableChanged(event);
    }
  }

  public void clear() {
    rows.clear();
    fireModelEvent( -1, TableModelEvent.DELETE);
  }

  public String getModelNameAt(int n) {
    if (n >= 0 && n < rows.size()) {
      return ( (LobbyTableModel) rows.get(n)).getName();
    }
    return "";
  }

  /**
   * This method called by LobbyServerProxy when application receives
   * changed poker table states.
   */
  public void tableListUpdated(LobbyTableModel[] changes) {
    //throw new IllegalStateException("TableListUpdate of either holdem or tourny should be called");
  }

  protected boolean isTableModelMustDelete(int state) {
    return state != LobbyTableModel.ONLINE;
  }
  
  protected boolean isTournyModelMustDelete(int state) {
	    return state != LobbyTournyModel.ONLINE;
	  }
  
  public void adUpdated() {}

//  ??
  public abstract Object getValueAt(int rowIndex, int columnIndex);

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public abstract boolean isLobbyTableAcceptable(LobbyTableModel table);

  public abstract boolean isLobbyTableAcceptable(LobbyTournyModel table);
  /**
   * Returns Lobby Table Model in specified row.
   */
  public LobbyTableModel getTableModelAtRow(int rowIndex) {
    return rowIndex < rows.size() ? (LobbyTableModel) rows.get(rowIndex) : null;
  }
  
  public LobbyTournyModel getTournyModelAtRow(int rowIndex) {
	    return rowIndex < rows.size() ? (LobbyTournyModel) rows.get(rowIndex) : null;
	  }

  /**
   * Finds Lobby Table Model by its poker table id.
   */
  public LobbyTableModel getModelByTableId(String tableName) {
    LobbyTableModel result = null;
    for (Iterator i = rows.iterator(); i.hasNext(); ) {
      LobbyTableModel lobbyTable = (LobbyTableModel) i.next();
      if (lobbyTable.getName() == tableName) {
        result = lobbyTable;
        break;
      }
    }
    return result;
  }


  protected String formatMoney(double money) {
    return SharedConstants.moneyToString(money);
  }
  public void setSelModel(ListSelectionModel lsm)
  {
	  setSelModel(lsm);
  }


}
