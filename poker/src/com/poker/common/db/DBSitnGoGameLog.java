package com.poker.common.db;

import com.agneya.util.ConnectionManager;

import com.golconda.db.DBException;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class DBSitnGoGameLog {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBSitnGoGameLog.class.getName());

  private String tournyId;
  private long gameRunId;
  private String gameId;
  private int gameType;
  private java.util.Date startTime;
  private java.util.Date endTime;
  private double rake;
  private double pot;
  private double start_worth;
  private double end_worth;
  private String sessionId;
  private String displayName;
  private int position;
  private double winAmount;

  public static final String TOURNY_NAME;
  public static final String GAME_RUN_ID;
  public static final String GAME_NAME;
  public static final String GAME_TYPE;
  public static final String START_TIME;
  public static final String END_TIME;
  public static final String RAKE;
  public static final String POT;
  public static final String START_WORTH;
  public static final String END_WORTH;
  public static final String SESSION_ID;
  public static final String POSITION;
  public static final String WIN_AMOUNT;
  public static final String USER_ID;
  Connection _conn;

  static {
    TOURNY_NAME = "TOURNY_NAME_FK";
    GAME_RUN_ID = "GAME_RUN_ID_SEQ_PK";
    SESSION_ID = "SESSION_ID_FK";
    POSITION = "PLAYER_POSITION";
    WIN_AMOUNT = "WIN_AMOUNT";
    USER_ID = "USER_ID_FK";
    START_TIME = "START_TIME";
    END_TIME = "END_TIME";
    RAKE = "RAKE";
    POT = "POT";
    START_WORTH = "START_WORTH";
    END_WORTH = "END_WORTH";
    GAME_NAME = "GAME_NAME_FK";
    GAME_TYPE = "GAME_TYPE_ID_FK";
  }
  public DBSitnGoGameLog(){}
  
  public DBSitnGoGameLog(String trnyId, String gid, long grid, int type) {
    tournyId = trnyId;
    gameRunId = grid;
    gameId = gid;
    gameType = type;
    _modified = true;
  }

  public Statement startBatch() throws DBException {
    try {
      _cat.finest("Starting transaction ---");
      _conn = ConnectionManager.getConnection("GameEngine");
      _conn.setAutoCommit(false);
      return _conn.createStatement();
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Unable to get Connection" + e.getMessage(), e);
      throw new DBException(e.getMessage() + " --Unable to get Connection");
    }
  }

  public void save(Statement ps) throws DBException {
    StringBuilder sb = new StringBuilder();
    _cat.finest("Adding batch ---" + this);
    try {
      sb.append(
          "insert into T_SITNGO_PER_PLAYER (");
      sb.append(TOURNY_NAME).append(",");
      sb.append(GAME_RUN_ID).append(",");
      sb.append(GAME_NAME).append(",");
      sb.append(GAME_TYPE).append(",");
      sb.append(SESSION_ID).append(",");
      sb.append(USER_ID).append(",");
      sb.append(POSITION).append(",");
      sb.append(WIN_AMOUNT).append(",");
      sb.append(POT).append(",");
      sb.append(START_WORTH).append(",");
      sb.append(END_WORTH).append(",");
      sb.append(START_TIME).append(",");
      sb.append(END_TIME).append(",");
      sb.append(RAKE).append(")");
      sb.append(" values ('");
      sb.append(tournyId).append("' ,");
      sb.append(gameRunId).append(" ,'");
      sb.append(gameId).append("' ,'");
      sb.append(gameType).append("' ,'");
      sb.append(sessionId).append("' ,'");
      sb.append(displayName).append("' ,");
      sb.append(position).append(" ,");
      sb.append(winAmount).append(" ,");
      sb.append(pot).append(" ,");
      sb.append(start_worth).append(" ,");
      sb.append(end_worth).append(" ,'");
      sb.append(new java.sql.Timestamp(startTime.getTime())).append("' ,'");
      sb.append(new java.sql.Timestamp(endTime.getTime())).append("' ,");
      sb.append(rake).append(")");
      _cat.finest(sb.toString());
      ps.addBatch(sb.toString());
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Unable to save  GRS" + e.getMessage(), e);
      _cat.warning(sb.toString());

      try {
        if (ps != null) {
          ps.getConnection().rollback();
          ps.close();
          ps = null;
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage() + " ---Unable to save  GRS");
    }
    finally {
      try {
        if (ps != null) {
          ps.getConnection().rollback();
          ps.close();
          ps = null;
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }
  }

  public int commitBatch(Statement ps) throws DBException {
    try {
      _cat.finest("Committting batch ---");
      int r[] = ps.executeBatch();
      _conn.commit();
      ps.close();
      _conn.close();
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Unable to save  GRS" + e.getMessage(), e);

      try {
        if (ps != null) {
          ps.getConnection().rollback();
          ps.close();
          ps = null;
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage() + " ---Unable to save  GRS");
    }
    finally {
      try {
        if (ps != null) {
          ps.getConnection().rollback();
          ps.close();
          ps = null;
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }

    return 0;
  }



   public String getTournyId() {
     return tournyId;
   }

   public void setTournyId(String v) {
     if (tournyId != v) {
       this.tournyId = v;
       setModified(true);
     }
  }
  /**
   * Get the game_run_id
   *
   * @return String
   */
  public long getGameRunId() {
    return gameRunId;
  }

  /**
   * Set the value of game_run_id
   *
   * @param v new value
   */
  public void setGameRunId(long v) {
    if (gameRunId != v) {
      this.gameRunId = v;
      setModified(true);
    }
  }

  /**
   * Get the game_id
   *
   * @return String
   */
  public String getGameId() {
    return gameId;
  }

  /**
   * Set the value of game_id
   *
   * @param v new value
   */
  public void setGameId(String v) {
    if (gameId != v) {
      this.gameId = v;
      setModified(true);
    }

  }

  /**
   * Get the GameType
   *
   * @return String
   */
  public int getGameType() {
    return gameType;
  }

  /**
   * Set the value of gameType
   *
   * @param v new value
   */
  public void setGameType(int v) {
    if (gameType != v) {
      this.gameType = v;
      setModified(true);
    }

  }

  public java.util.Date getStartTime() {
    return startTime;
  }

  public void setStartTime(java.util.Date v) {
    if (startTime == null || !startTime.toString().equals(v.toString())) {
      this.startTime = v;
      setModified(true);
    }

  }
  public String getStartTimeString() {    
	  return startTime.toString();  
  }
  
  public java.util.Date getEndTime() {
    return endTime;
  }

  public void setEndTime(java.util.Date v) {
    if (endTime == null || !endTime.toString().equals(v.toString())) {
      this.endTime = v;
      setModified(true);
    }
  }
  public String getEndTimeString() {    
  	  return endTime.toString();  
    }

  /**
   * Get the rake
   *
   * @return String
   */
  public double getRake() {
    return rake;
  }

  /**
   * Set the value of rake
   *
   * @param v new value
   */
  public void setRake(double v) {
    if (rake != v) {
      BigDecimal bg = new BigDecimal(v);
      bg = bg.setScale(2, bg.ROUND_HALF_UP);
      this.rake = bg.doubleValue();
      setModified(true);
    }
  }

  /**
   * Get the pot
   *
   * @return String
   */
  public double getPot() {
    return pot;
  }

  /**
   * Set the value of pot
   *
   * @param v new value
   */
  public void setPot(double v) {
    if (pot != v) {
      BigDecimal bg = new BigDecimal(v);
      bg = bg.setScale(2, bg.ROUND_HALF_UP);
      this.pot = bg.doubleValue();
      setModified(true);
    }
  }

  /**
   * Get the DispName
   *
   * @return String
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Set the value of DispName
   *
   * @param v new value
   */
  public void setDisplayName(String v) {
    if (displayName == null || !displayName.equals(v)) {
      this.displayName = v;
      setModified(true);
    }

  }

  /**
   * Get the SessionId
   *
   * @return String
   */
  public String getSessionId() {
    return sessionId;
  }

  /**
   * Set the value of SessionId
   *
   * @param v new value
   */
  public void setSessionId(String v) {
    if (sessionId == null || !sessionId.equals(v)) {
      this.sessionId = v;
      setModified(true);
    }

  }

  public double getStartWorth() {
    return start_worth;
  }

  public void setStartWorth(double v) {
    if (start_worth != v) {
      BigDecimal bg = new BigDecimal(v);
      bg = bg.setScale(2, bg.ROUND_HALF_UP);
      this.start_worth = bg.doubleValue();
      setModified(true);
    }
  }

  public double getEndWorth() {
    return end_worth;
  }

  public void setEndWorth(double v) {
    if (end_worth != v) {
      BigDecimal bg = new BigDecimal(v);
      bg = bg.setScale(2, bg.ROUND_HALF_UP);
      this.end_worth = bg.doubleValue();
      setModified(true);
    }
  }

  public double getWinAmount() {
    return winAmount;
  }

  public void setWinAmount(double v) {
    if (winAmount != v) {
      this.winAmount = v;
      setModified(true);
    }
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int v) {
    if (position != v) {
      this.position = v;
      setModified(true);
    }
  }

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("DBSitnGoGameLog:\n");
    str.append("TournyId = ").append(getTournyId()).append("\n");
    str.append("GameRunId = ").append(getGameRunId()).append("\n");
    str.append("GameId = ").append(getGameId()).append("\n");
    str.append("StartTime = ").append(getStartTime()).append("\n");
    str.append("EndTime = ").append(getEndTime()).append("\n");
    str.append("Rake = ").append(getRake()).append("\n");
    str.append("Pot = ").append(getPot()).append("\n");
    str.append("Start = ").append(getStartWorth()).append("\n");
    str.append("End = ").append(getEndWorth()).append("\n");
    str.append("SessionId = ").append(getSessionId()).append("\n");
    str.append("DisplayName = ").append(getDisplayName()).append("\n");
    str.append("Position = ").append(getPosition()).append("\n");
    str.append("WinAmount = ").append(getWinAmount()).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;
  
  public  static DBSitnGoGameLog[]  getLogsForDuration(String tournyName,Timestamp t1, Timestamp t2){
      Connection conn = null;
      PreparedStatement ps = null;
       Vector _list = new Vector();

      int pref = -1;
      try {
          conn = ConnectionManager.getConnection("GameEngine");
          //conn.setAutoCommit(false);
          StringBuilder sb = new StringBuilder("select * from T_SITNGO_PER_PLAYER where ");
          sb.append("TOURNY_NAME_FK like '%"+tournyName+"%' and ");
          sb.append("end_time between date(?) and date(?) ");
          sb.append("group by GAME_RUN_ID_SEQ_PK limit 200");
          ps = conn.prepareStatement(sb.toString());
          ps.setTimestamp(1, t1);
          ps.setTimestamp(2, t2);
          _cat.finest(sb.toString());
          ResultSet r = ps.executeQuery();
          while(r.next()){
               DBSitnGoGameLog dp= new DBSitnGoGameLog();
               dp.tournyId = r.getString(TOURNY_NAME);
               dp.gameRunId = r.getLong(GAME_RUN_ID);
               dp.gameId = r.getString(GAME_NAME);
               dp.position = r.getInt(POSITION);
               dp.gameType = r.getInt(GAME_TYPE);
               dp.winAmount = r.getDouble(WIN_AMOUNT);
               dp.startTime = r.getTimestamp(START_TIME);
               dp.endTime = r.getTimestamp(END_TIME);
               dp.rake = r.getDouble(RAKE);
               dp.pot = r.getDouble(POT);
               dp.start_worth = r.getDouble(START_WORTH);
               dp.end_worth = r.getDouble(END_WORTH);
               dp.sessionId = r.getString(SESSION_ID);
               dp.displayName = r.getString(USER_ID);
               dp.position = r.getInt(POSITION);
               
               _list.add(dp);
          }
          r.close();
          ps.close();
          conn.close();
      } catch (SQLException e) {
          _cat.log( Level.SEVERE,"Unable to get details " + e.getMessage(), e);

          try {
              if (ps != null) {
                  ps.close();
              }
              if (conn != null) {
                  conn.rollback();
                  conn.close();
              }
          } catch (SQLException se) {
              //ignore
          }
      } finally {
          try {
              if (ps != null) {
                  ps.close();
              }
              if (conn != null) {
                  conn.close();
              }
          } catch (SQLException se) {
              //ignore
          }
      }  
      return (DBSitnGoGameLog [])_list.toArray(new DBSitnGoGameLog[_list.size()]);
  }
  
  public  static DBSitnGoGameLog[]  getLogsForGame(String tournyName){
      Connection conn = null;
      PreparedStatement ps = null;
       Vector _list = new Vector();

      int pref = -1;
      try {
          conn = ConnectionManager.getConnection("GameEngine");
          //conn.setAutoCommit(false);
          StringBuilder sb = new StringBuilder("select * from T_SITNGO_PER_PLAYER where ");
          sb.append("TOURNY_NAME_FK like '%"+tournyName+"%' ");
          sb.append("group by GAME_RUN_ID_SEQ_PK");
          ps = conn.prepareStatement(sb.toString());
          System.out.println(sb.toString());
          _cat.finest(sb.toString());
          ResultSet r = ps.executeQuery();
          while(r.next()){
               DBSitnGoGameLog dp= new DBSitnGoGameLog();
               dp.tournyId = r.getString(TOURNY_NAME);
               dp.gameRunId = r.getLong(GAME_RUN_ID);
               dp.gameId = r.getString(GAME_NAME);
               dp.position = r.getInt(POSITION);
               dp.gameType = r.getInt(GAME_TYPE);
               dp.winAmount = r.getDouble(WIN_AMOUNT);
               dp.startTime = r.getTimestamp(START_TIME);
               dp.endTime = r.getTimestamp(END_TIME);
               dp.rake = r.getDouble(RAKE);
               dp.pot = r.getDouble(POT);
               dp.start_worth = r.getDouble(START_WORTH);
               dp.end_worth = r.getDouble(END_WORTH);
               dp.sessionId = r.getString(SESSION_ID);
               dp.displayName = r.getString(USER_ID);
              
               
               _list.add(dp);
          }
          r.close();
          ps.close();
          conn.close();
      } catch (SQLException e) {
          _cat.log( Level.SEVERE,"Unable to get details " + e.getMessage(), e);

          try {
              if (ps != null) {
                  ps.close();
              }
              if (conn != null) {
                  conn.rollback();
                  conn.close();
              }
          } catch (SQLException se) {
              //ignore
          }
      } finally {
          try {
              if (ps != null) {
                  ps.close();
              }
              if (conn != null) {
                  conn.close();
              }
          } catch (SQLException se) {
              //ignore
          }
      }  
      return (DBSitnGoGameLog [])_list.toArray(new DBSitnGoGameLog[_list.size()]);
  }
}
