package com.poker.common.db;

import com.agneya.util.ConnectionManager;
import com.agneya.util.LongOps;

import com.poker.common.interfaces.JackpotInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class DBJackpot {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBJackpot.class.getName());

  public int jackId;
  public String userId;
  public int gameId;
  public long gameRunId;
  public double amount;
  public String jackpotName;

  /*
        CREATE TABLE T_JACKPOT_WINNERS (
          WINNER_ID_FK 		INTEGER IDENTITY(1,1) PRIMARY KEY,
          JACKPOT_NAME		NVARCHAR(16),
          TIMESTAMP		DATETIME,
          GAME_ID_FK 		INTEGER NOT NULL,
          GAME_RUN_ID 		INTEGER NOT NULL,
          USER_ID_FK		NVARCHAR(20) NOT NULL,
          AMOUNT			MONEY
   );
   */

  public static final String JACKPOT_NAME;
  public static final String JACKPOT_DESC;
  public static final String TIMESTAMP;
  public static final String GAME_ID_FK;
  public static final String GAME_RUN_ID;
  public static final String USER_ID_FK;
  public static final String AMOUNT;

  static {
    JACKPOT_NAME = "JACKPOT_NAME";
    JACKPOT_DESC = "JACKPOT_DESC";
    TIMESTAMP = "TIMESTAMP";
    GAME_ID_FK = "GAME_ID_FK";
    GAME_RUN_ID = "GAME_RUN_ID";
    USER_ID_FK = "USER_ID_FK";
    AMOUNT = "AMOUNT";
  }

  public DBJackpot() {
  }

  public static synchronized int winner(int jval, int gid, long grid,
                           String user,
                           double amt, double total) {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      String jname = JackpotInterface._jname[LongOps.firstBitPos(jval)];
      _cat.finest("jval=" + jval + ", jname=" + jname+ ", gid=" + gid + ", grid=" + grid + ", usre=" + user);
      StringBuilder sb = new StringBuilder("insert into T_JACKPOT_WINNERS ( ");
      sb.append(JACKPOT_NAME).append(",");
      sb.append(TIMESTAMP).append(",");
      sb.append(GAME_ID_FK).append(",");
      sb.append(GAME_RUN_ID).append(",");
      sb.append(USER_ID_FK).append(",");
      sb.append(AMOUNT).append(")");
      sb.append(" values ( ?, ?, ?, ?, ?, ? )");
      _cat.finest(sb.toString());
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, jname);
      ps.setTimestamp(2,
                      new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setInt(3, gid);
      ps.setLong(4, grid);
      ps.setString(5, user);
      ps.setDouble(6, amt);
      r = ps.executeUpdate();

      //update the jackpot value
      sb = new StringBuilder("update T_JACKPOT_CURRENT set ");
      sb.append(TIMESTAMP).append("=").append("? , ");
      sb.append(USER_ID_FK).append("=").append("? , ");
      sb.append(AMOUNT).append("=").append("? ");
      sb.append(" where ").append(JACKPOT_NAME).append("= ?");
      _cat.finest(sb.toString());
      ps = conn.prepareStatement(sb.toString());
      ps.setTimestamp(1,
                      new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setString(2, user);
      ps.setDouble(3, total);
      ps.setString(4, jname);
      r = ps.executeUpdate();

      ps.close();
      conn.close();
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "Unable to save  Jackpot winners" + e.getMessage(), e);

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
      // throw new DBException(e.getMessage() +
      //                      " -- declaring winners for jackpot");
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

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("JackpotWinners:\n");
    str.append("Name = ").append(jackpotName).append("\n");
    str.append("GameId = ").append(gameId).append("\n");
    str.append("User = ").append(userId).append("\n");
    str.append("Amount = ").append(amount).append("\n");
    return (str.toString());
  }

  /*
        create table T_JACKPOT_POOL (
         GAME_RUN_ID_SEQ_PK   integer NOT null,
         GAME_ID_FK           integer NOT null,
          JACKPOT_NAME		nvarchar(16),
         AMOUNT		    money,
         TIMESTAMP	    datetime NOT null


   create table T_JACKPOT_CURRENT (
           JACKPOT_NAME		nvarchar(16) not null primary key,
           AMOUNT			money,
           TIMESTAMP		datetime NOT null
   )

   */

  public static int addJackpotPool(Connection conn, int gid, long grid,
                                   String jname, double amt) {
    boolean flag = false;
    PreparedStatement ps = null;
    int r = -1;
    try {
      StringBuilder sb = new StringBuilder("insert into T_JACKPOT_POOL ( ");
      sb.append(JACKPOT_NAME).append(",");
      sb.append(TIMESTAMP).append(",");
      sb.append(GAME_ID_FK).append(",");
      sb.append(GAME_RUN_ID).append(",");
      sb.append(AMOUNT).append(")");
      sb.append(" values ( ?, ?, ?, ?, ? )");
      _cat.finest(sb.toString());

      if (conn == null) {
        flag = true;
        conn = ConnectionManager.getConnection("GameEngine");
      }

      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, jname);
      ps.setTimestamp(2,
                      new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setInt(3, gid);
      ps.setLong(4, grid);
      ps.setDouble(5, amt);
      r = ps.executeUpdate();
      ps.close();

      if (flag){
        conn.close();
      }
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "Unable to add to Jackpot pool" + e.getMessage(), e);

      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null && flag) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
      // throw new DBException(e.getMessage() +
      //                      " -- declaring winners for jackpot");
    }
    finally {
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null && flag) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }
    return r;
  }
  public static int updateJackpotCurrent(Connection conn,
                                    int jackpot, double amt, double total) {
     boolean flag = false;
     PreparedStatement ps = null;
     _cat.finest("Jp=" + LongOps.firstBitPos(jackpot) + ", Amount=" + amt + ", Total=" + total);
     int r = -1;
     try {
       String jname = JackpotInterface._jname[LongOps.firstBitPos(jackpot)];
       if (conn == null) {
         flag = true;
         conn = ConnectionManager.getConnection("GameEngine");
       }
       //update the jackpot value
       StringBuilder sb = new StringBuilder("update T_JACKPOT_CURRENT set ");
       sb.append(TIMESTAMP).append("=").append("? , ");
       sb.append(AMOUNT).append("=").append("? ");
       sb.append(" where ").append(JACKPOT_NAME).append("= ?");
       _cat.finest(sb + jname + total);
       ps = conn.prepareStatement(sb.toString());
       ps.setTimestamp(1,
                       new java.sql.Timestamp(System.currentTimeMillis()));
       ps.setDouble(2, total);
       ps.setString(3, jname);
       r = ps.executeUpdate();
       ps.close();

       if (flag){
         conn.close();
       }
     }
     catch (Exception e) {
       _cat.log(Level.WARNING, "Unable to update to Jackpot current" + e.getMessage(), e);

       try {
         if (ps != null) {
           ps.close();
         }
         if (conn != null && flag) {
           conn.close();
         }
       }
       catch (SQLException se) {
         //ignore
       }
       // throw new DBException(e.getMessage() +
       //                      " -- declaring winners for jackpot");
     }
     finally {
       try {
         if (ps != null) {
           ps.close();
         }
         if (conn != null && flag) {
           conn.close();
         }
       }
       catch (SQLException se) {
         //ignore
       }
     }
     return r;
   }

  public synchronized static DBJackpot[] addToPool(int gid, long grid,
                                      double amt, DBJackpot[] dbj)
 {
   Connection conn=null;
   try {
     conn = ConnectionManager.getConnection("GameEngine");
     conn.setAutoCommit(false);
     for (int i = 0; dbj!= null && i < dbj.length; i++) {
       int jval = JackpotInterface._jval[i];
       double percent = (double) JackpotInterface._win_percent[i] / 100.00;
       double per_jp_amt = percent * amt;
       dbj[i].amount += per_jp_amt;

       _cat.finest("jval=" + i + ", %=" + percent + ", per jp=" + per_jp_amt + " total=" + dbj[i].amount);
       updateJackpotCurrent(conn, jval, per_jp_amt, dbj[i].amount);
     }
     addJackpotPool(conn, gid, grid, "JACKPOT", amt);
     conn.commit();
   }
    catch (Exception e) {
      _cat.log(Level.WARNING, "Unable to add to Jackpot pool" + e.getMessage(), e);

      try {

        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
      // throw new DBException(e.getMessage() +
      //                      " -- declaring winners for jackpot");
    }
    finally {
      try {
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }
     return dbj;
  }

  /*
        create table T_JACKPOT_CURRENT (
                JACKPOT_NAME		nvarchar(16) not null primary key,
                AMOUNT			money,
                TIMESTAMP		datetime NOT null
        )

   */

  public static DBJackpot[] readJackpotPool() {
    Vector v = new Vector();
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuilder sb = new StringBuilder(
          "select JACKPOT_NAME, USER_ID_FK, AMOUNT from T_JACKPOT_CURRENT");
      _cat.finest(sb.toString());
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ResultSet r = ps.executeQuery();
      while (r.next()) {
          DBJackpot dbj = new DBJackpot();
          dbj.jackpotName = r.getString("JACKPOT_NAME");
          dbj.amount = r.getDouble("AMOUNT");
          dbj.userId = r.getString("USER_ID_FK");
          v.add(dbj);
          _cat.finest(dbj.toString());
      }
      r.close();
      ps.close();
      conn.close();
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "Unable to query Jackpot pool" + e.getMessage(), e);

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
      // throw new DBException(e.getMessage() +
      //                      " -- declaring winners for jackpot");
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
    DBJackpot []dbj =  (DBJackpot [])v.toArray(new DBJackpot[v.size()]);

    java.util.Arrays.sort(dbj, new Comparator() {
      public int compare(Object o1, Object o2) {
        return 1; // sort the jackpots in an order
      }
    });
    return dbj;
  }

  public static void main(String args[]) throws Exception {
    //GET RESERVE
    DBJackpot[] jpt = readJackpotPool();
    for (int i = 0; i < jpt.length; i++) {
      System.out.println(jpt[i]);
    }


    DBJackpot.addToPool( 123, (long) 12345, 1, jpt);

    // DECLARE WINNER
    // set it in the DB
    DBJackpot.winner(JackpotInterface.QUAD_SEVEN, 123, 12345,
                     "poker",
                     234, 3004 - 234);

  }

}
