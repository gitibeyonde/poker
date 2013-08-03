package com.poker.common.db;

import com.agneya.util.ConnectionManager;

import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;

import com.poker.common.interfaces.TournyInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBTourny {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBTourny.class.getName());

  protected int gameType;
  protected int limit;
  protected int tournyState;
  public String tournyName;
  public double buyIn1;
  public double buyIn2;
  public double chips;
  protected int schedule0;
  protected int schedule1;
  protected int schedule2;
  protected int schedule3;
  protected int schedule4;
  protected int declInt;
  protected int regInt;
  protected int joinInt;
  public java.util.Date lastModified;
  protected String userId;

  public static final String TOURNY_NAME;
  public static final String GAME_TYPE_ID_FK;
  public static final String LIMIT;
  public static final String TOURNY_STATE;
  public static final String BUY_IN_0;
  public static final String BUY_IN_1;
  public static final String SCHEDULE_0;
  public static final String SCHEDULE_1;
  public static final String SCHEDULE_2;
  public static final String SCHEDULE_3;
  public static final String SCHEDULE_4;
  public static final String DECL_INT;
  public static final String REG_INT;
  public static final String JOIN_INT;
  public static final String LAST_MODIFIED_TS;

  /**
   * Tourny register
   */
  public static final String USER_ID;
  public static final String TOURNY_NAME_FK;
  public static final String BUYIN;
  public static final String FEES;
  public static final String CHIPS;
  public static final String REGISTER_TS;

  static {
	  GAME_TYPE_ID_FK = "GAME_TYPE_ID_FK";
    LIMIT = "LIMIT_TYPE";
    TOURNY_NAME = "TOURNEY_NAME";
    TOURNY_STATE = "TOURNEY_STATE";
    BUY_IN_0 = "BUY_IN_0";
    BUY_IN_1 = "BUY_IN_1";
    SCHEDULE_0 = "SCHEDULE_0";
    SCHEDULE_1 = "SCHEDULE_1";
    SCHEDULE_2 = "SCHEDULE_2";
    SCHEDULE_3 = "SCHEDULE_3";
    SCHEDULE_4 = "SCHEDULE_4";
    DECL_INT = "DECL_INT";
    REG_INT = "REG_INT";
    JOIN_INT = "JOIN_INT";
    LAST_MODIFIED_TS = "LAST_MODIFIED_TIMESTAMP";
    USER_ID = "USER_ID_FK";
    TOURNY_NAME_FK = "TOURNY_NAME_FK";
    BUYIN = "BUYIN";
    FEES = "FEES";
    CHIPS = "CHIPS";
    REGISTER_TS = "REGISTER_TIMESTAMP";
  }

  public static final short RUNNING = 1;
  public static final short STOPPED = 1;
  
  public DBTourny(){}

  public DBTourny(int type, int lim, String name, double buyin, double fees, int[] sch, int di,
                  int ri, int ji) {
    gameType = type;
    limit = lim;
    tournyName = name;
    buyIn1 = buyin;
    buyIn2 = fees;
    schedule0 = sch[0];
    schedule1 = sch[1];
    schedule2 = sch[2];
    schedule3 = sch[3];
    schedule4 = sch[4];
    declInt = di;
    regInt = ri;
    joinInt = ji;
    lastModified = new java.util.Date();
    _modified = true;
  }

  public DBTourny(String gid) throws DBException {
    get(gid);
  }

  public boolean get(String gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuilder sb = new StringBuilder("select *");
      sb.append(" from T_TOUNRY_LIVE where ");
      sb.append(TOURNY_NAME).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, gid);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        gameType = r.getInt(GAME_TYPE_ID_FK);
        tournyName = r.getString(TOURNY_NAME);
        r.close();
        ps.close();
        conn.close();
        return true;
      }
      else {
        r.close();
        ps.close();
        conn.close();
        return false;
      }
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "ERROR in fetching Tourny creation", e);
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.rollback();
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage());
    }
    finally {
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }

  }

  public int save() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    if (_modified) {
      try {
        StringBuilder sb = new StringBuilder("insert into T_TOURNY_LIVE ( ");
        sb.append(GAME_TYPE_ID_FK).append(",");
        sb.append(LIMIT).append(",");
        sb.append(TOURNY_NAME).append(",");
        sb.append(BUY_IN_0).append(",");
        sb.append(BUY_IN_1).append(",");
        sb.append(SCHEDULE_0).append(",");
        sb.append(SCHEDULE_1).append(",");
        sb.append(SCHEDULE_2).append(",");
        sb.append(SCHEDULE_3).append(",");
        sb.append(SCHEDULE_4).append(",");
        sb.append(DECL_INT).append(",");
        sb.append(REG_INT).append(",");
        sb.append(JOIN_INT).append(",");
        sb.append(LAST_MODIFIED_TS).append(")");
        sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        int i = 1;
        ps.setInt(i++, gameType);
        ps.setInt(i++, limit);
        ps.setString(i++, tournyName);
        ps.setDouble(i++, buyIn1);
        ps.setDouble(i++, buyIn2);
        ps.setInt(i++, schedule0);
        ps.setInt(i++, schedule1);
        ps.setInt(i++, schedule2);
        ps.setInt(i++, schedule3);
        ps.setInt(i++, schedule4);
        ps.setInt(i++, declInt);
        ps.setInt(i++, regInt);
        ps.setInt(i++, joinInt);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(lastModified == null ?
                                               System.currentTimeMillis() :
                                               lastModified.getTime()));
        r = ps.executeUpdate();
        _cat.finest(this.toString());
        ps.close();
        conn.close();
      }
      catch (SQLException e) {
        _cat.log(Level.WARNING, "Unable to save Toruny " + e.getMessage(), e);
        _cat.warning(this.toString());
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.rollback();
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
        throw new DBException(e.getMessage());
      }
      finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
      }

    }
    return r;
  }

  /**public int updateState() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    if (_modified) {
      try {
        StringBuilder sb = new StringBuilder(
            "update t_login_session_live set ");
        sb.append(TOURNY_STATE).append(" = ? ");
        sb.append(" where ");
        sb.append(TOURNY_NAME).append("= ? ");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setInt(1, tournyState);
        ps.setString(2, tournyId);
        r = ps.executeUpdate();
        ps.close();
        conn.close();
        _cat.finest(this.toString());
      }
      catch (SQLException e) {
        _cat.log(Level.WARNING, "Unable to update Player's Session " + e.getMessage(), e);
        _cat.warning(this.toString());
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.rollback();
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
        throw new DBException(e.getMessage());
      }
      finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
      }

    }
    return r;
  }**/

  public int register(DBPlayer gp, String session, TournyInterface tin) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    userId = gp.getDisplayName();
    int r = -1;
    try {

    	if (tin.type().isReal()){
    		r=gp.buyRealTournyChips(session, tin.buyIn() + tin.fees(), tin.name(), tin.type().toString());
    	}
    	else {
    		r=gp.buyPlayTournyChips(session, tin.buyIn() + tin.fees(), tin.name(), tin.type().toString());
    	}
    
      if (r <= -1){
        return r;
      }
        
      StringBuilder sb = new StringBuilder(
          "replace into T_REGISTERED_TOURNY ( ");
      sb.append(USER_ID).append(",");
      sb.append(GAME_TYPE_ID_FK).append(",");
      sb.append(TOURNY_NAME_FK).append(",");
      sb.append(BUYIN).append(",");
      sb.append(FEES).append(",");
      sb.append(CHIPS).append(",");
      sb.append(REGISTER_TS).append(")");
      _cat.finest(sb.toString());
      sb.append(" values ( ?, ?, ?, ?, ?, ?, ? )");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, userId);
      ps.setInt(2, gameType);
      ps.setString(3, tournyName);
      ps.setDouble(4, tin.buyIn());
      ps.setDouble(5, tin.fees());
      ps.setDouble(6, tin.chips());
      ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
      r = ps.executeUpdate();
      _cat.finest(userId + ", " + tournyName + ", " + r);
      ps.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Unable to save tourny registration info " + e.getMessage(), e);
      _cat.warning(this.toString());
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.rollback();
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage());
    }
    finally {
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }

    return r;
  }


  public int unRegister(DBPlayer gp, String session, TournyInterface tin) throws DBException {
      Connection conn = null;
      PreparedStatement ps = null;

      userId = gp.getDisplayName();
      int r = -1;
      try {
        conn = ConnectionManager.getConnection("GameEngine");
        conn.setAutoCommit(false);

        if (tin.type().isReal()){
        	r=gp.returnRealTournyChips(session, tin.buyIn() + tin.fees(), tin.name(), tin.type().toString());
        }
        else {
        	r=gp.returnPlayTournyChips(session, tin.buyIn() + tin.fees(), tin.name(), tin.type().toString());
        }

        if (r == -1){
          conn.rollback();
          conn.close();
          return r;
        }

        StringBuilder sb = new StringBuilder(
            "delete from T_REGISTERED_TOURNY where ");
        sb.append(USER_ID).append("= ? and ");
        sb.append(TOURNY_NAME_FK).append(" = ?");
        _cat.finest(userId + ", " + tournyName);
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, userId);
        ps.setString(2, tournyName);
        r = ps.executeUpdate();
        ps.close();
        conn.commit();
        conn.close();
      }
      catch (SQLException e) {
        _cat.log(Level.WARNING, "Unable to delete tourny registration " + e.getMessage(), e);
        _cat.warning(this.toString());
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.rollback();
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
        throw new DBException(e.getMessage());
      }
      finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
      }
      return r;
  }
  
    public DBTourny[] getTournyRegisteredForPlayer(String gid)  { //last 100
      Connection conn = null;
      PreparedStatement ps = null;
      try {
        StringBuilder sb = new StringBuilder("select *");
        sb.append(" from T_REGISTERED_TOURNY where ");
        sb.append(USER_ID).append("=?   order by  register_timestamp desc limit 100");
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, gid);
        _cat.finest(sb.toString());
        ResultSet r = ps.executeQuery();
        Vector<DBTourny>v = new Vector<DBTourny>();
        while (r.next()) {
          DBTourny dbt = new DBTourny();
          dbt.tournyName = r.getString(TOURNY_NAME_FK);
          dbt.buyIn1 = r.getDouble(BUYIN);
          dbt.buyIn2 = r.getDouble(FEES);
          dbt.lastModified = r.getTimestamp(REGISTER_TS);
          dbt.chips = r.getDouble(CHIPS);
          v.add(dbt);
        }
          r.close();
          ps.close();
          conn.close();
          return v.toArray(new DBTourny[v.size()]);
      }
      catch (SQLException e) {
        _cat.log(Level.WARNING, "ERROR in fetching Tourny creation", e);
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.rollback();
            conn.close();
          }
        }
        catch (SQLException se) {
            se.printStackTrace();
          //ignore
        }
      }
      finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
      }
        return new DBTourny[0];
    }


  public static int getTournyChips(String userId, String gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int chips = -1;
    try {
      StringBuilder sb = new StringBuilder("select *");
      sb.append(" from T_REGISTERED_TOURNY where ");
      sb.append(TOURNY_NAME_FK).append("= ?  and ");
      sb.append(USER_ID).append("=? ");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, gid);
      ps.setString(2, userId);
      _cat.finest(sb + ", " + gid + " ," + userId);
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        chips = r.getInt(CHIPS);
        r.close();
        ps.close();
        conn.close();
      }
      else {
        r.close();
        ps.close();
        conn.close();
      }
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Unable to update Player's Play Wallet " + e.getMessage(), e);
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.rollback();
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage());
    }
    finally {
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }

    _cat.finest("Tourny chips = " + chips);
    return chips;
  }
  
    public static int register(String userId, String tid) throws DBException {
      Connection conn = null;
      PreparedStatement ps = null;

      int r = -1;
      try {

        StringBuilder sb = new StringBuilder(
            "replace into T_REGISTERED_TOURNY ( ");
        sb.append(USER_ID).append(",");
        sb.append(TOURNY_NAME_FK).append(",");
        sb.append(BUYIN).append(",");
        sb.append(FEES).append(",");
        sb.append(CHIPS).append(",");
        sb.append(REGISTER_TS).append(")");
        _cat.finest(sb.toString());
        sb.append(" values ( ?, ?, ?, ?, ?, ? )");
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, userId);
        ps.setString(2, tid);
        ps.setDouble(3, 0);
        ps.setDouble(4, 0);
        ps.setDouble(5, 0);
        ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        r = ps.executeUpdate();
        _cat.finest(userId + ", " + tid + ", " + r);
        ps.close();
        conn.close();
      }
      catch (SQLException e) {
        _cat.log(Level.WARNING, "Unable to save tourny registration info " + e.getMessage(), e);
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.rollback();
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
        throw new DBException(e.getMessage());
      }
      finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
      }

      return r;
    }

      public static int unRegister(String userId, String tn) throws DBException {
          Connection conn = null;
          PreparedStatement ps = null;

          int r = -1;
          try {
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = new StringBuilder(
                "delete from T_REGISTERED_TOURNY where ");
            sb.append(USER_ID).append("= ? and ");
            sb.append(TOURNY_NAME_FK).append(" = ?");
            _cat.finest(userId + ", " + tn);
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, userId);
            ps.setString(2, tn);
            r = ps.executeUpdate();
            ps.close();
            conn.close();
          }
          catch (SQLException e) {
            _cat.log(Level.WARNING, "Unable to delete tourny registration " + e.getMessage(), e);
            try {
              if (ps != null) {
                ps.close();
              }
              if (conn != null) {
                conn.rollback();
                conn.close();
              }
            }
            catch (SQLException se) {
              //ignore
            }
            throw new DBException(e.getMessage());
          }
          finally {
            try {
              if (ps != null) {
                ps.close();
              }
              if (conn != null) {
                conn.close();
              }
            }
            catch (SQLException se) {
              //ignore
            }
          }
          return r;
      }
      

      public static String[] getRegisteredPlayers(String gid) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector v = new Vector();

        int chips = -1;
        try {
          StringBuilder sb = new StringBuilder("select ");
          sb.append(USER_ID);
          sb.append(" from T_REGISTERED_TOURNY where ");
          sb.append(TOURNY_NAME_FK).append("= ? ");
          conn = ConnectionManager.getConnection("GameEngine");
          ps = conn.prepareStatement(sb.toString());
          ps.setString(1, gid);
          _cat.finest(sb + ", " + gid );
          ResultSet r = ps.executeQuery();
          while (r.next()) {
            v.add(r.getString(USER_ID));
          }
            r.close();
            ps.close();
            conn.close();
        }
        catch (SQLException e) {
          _cat.log(Level.WARNING, "Unable to update Player's details " + e.getMessage(), e);
          try {
            if (ps != null) {
              ps.close();
            }
            if (conn != null) {
              conn.rollback();
              conn.close();
            }
          }
          catch (SQLException se) {
            //ignore
          }
          throw new DBException(e.getMessage());
        }
        finally {
          try {
            if (ps != null) {
              ps.close();
            }
            if (conn != null) {
              conn.close();
            }
          }
          catch (SQLException se) {
            //ignore
          }
        }

        _cat.finest("Tourny chips = " + chips);
        return (String [])v.toArray(new  String[v.size()]);
      }

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("DBTourny:\n");
    str.append("TournyID = ").append(tournyName).append("\n");
    str.append("TournyName = ").append(tournyName).append("\n");
    str.append("GameType = ").append(gameType).append("\n");
    str.append("UserID = ").append(userId).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
