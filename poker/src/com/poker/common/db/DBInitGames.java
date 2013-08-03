package com.poker.common.db;

import com.agneya.util.ConnectionManager;

import com.golconda.db.DBException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBInitGames {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBInitGames.class.getName());

  public static final String INVITED;
  public static final String RAKE;
  public static final String RAKE2;
  public static final String RAKE4;
  public static final String RAKE6;
  public static final String RAKE8;
  public static final String RAKE10;

  public static final String BUYIN;
  public static final String FEES;
  public static final String CHIPS;

  public static final String SCHEDULE_0;
  public static final String SCHEDULE_1;
  public static final String SCHEDULE_2;
  public static final String SCHEDULE_3;
  public static final String SCHEDULE_4;
  public static final String DECL_INT;
  public static final String REG_INT;
  public static final String JOIN_INT;

  public static final String MOVE_TIME;
  public static final String GAME_TYPE;
  public static final String GAME_NAME;
  public static final String LIMIT;
  public static final String TOURBO;
  public static final String LOW_RANK;
  public static final String HIGH_RANK;
  public static final String AFFILIATE_ID;
  public static final String PARTNER1_AFFILIATE_ID;
  public static final String PARTNER2_AFFILIATE_ID;
  public static final String MIN_PLAYERS;
  public static final String MAX_PLAYERS;
  public static final String AB1;
  public static final String AB2;
  public static final String MIN_RAISE;
  public static final String MAX_BET;
  public static final String GAME_STATE;
  public static final String PRIVATE_GAME;
  public static final String LAST_MODIFIED;
  public static final String DON;
  public static final String STACK;
  public static final String TOURNY_TYPE;
  public static final String GUARANTEED;
  public static final String PRIZE_POOL;
  static {
    INVITED = "INVITED";
    RAKE = "RAKE_PERCENT";
    RAKE2 = "MAX_RAKE1";
    RAKE4 = "MAX_RAKE2";
    RAKE6 = "MAX_RAKE3";
    RAKE8 = "MAX_RAKE4";
    RAKE10 = "MAX_RAKE5";

    BUYIN = "BUYIN";
    FEES = "FEES";
    CHIPS = "CHIPS";

    SCHEDULE_0 = "SCHEDULE_0";
    SCHEDULE_1 = "SCHEDULE_1";
    SCHEDULE_2 = "SCHEDULE_2";
    SCHEDULE_3 = "SCHEDULE_3";
    SCHEDULE_4 = "SCHEDULE_4";
    DECL_INT = "DECL_INT";
    REG_INT = "REG_INT";
    JOIN_INT = "JOIN_INT";

    MOVE_TIME = "MOVE_TIME";
    GAME_TYPE = "GAME_TYPE_ID_FK";
    GAME_NAME = "GAME_NAME";
    LIMIT = "LIMIT_TYPE";
    TOURBO = "TOURBO";
    LOW_RANK = "LOW_RANK";
    HIGH_RANK = "HIGH_RANK";
    AFFILIATE_ID = "AFFILIATE_ID_FK";
    PARTNER1_AFFILIATE_ID = "PARTNER1_AFFILIATE_ID_FK";
    PARTNER2_AFFILIATE_ID = "PARTNER2_AFFILIATE_ID_FK";
    MIN_PLAYERS = "MIN_PLAYERS";
    MAX_PLAYERS = "MAX_PLAYERS";
    AB2 = "AB2";
    AB1 = "AB1";
    MIN_RAISE = "MIN_RAISE";
    MAX_BET = "MAX_BET";
    GAME_STATE = "GAME_STATE";
    PRIVATE_GAME = "PRIVATE_GAME";
    LAST_MODIFIED = "LAST_MODIFIED_TIMESTAMP";
    DON ="DON";
    STACK = "STACK";
    TOURNY_TYPE = "TOURNY_TYPE";
    GUARANTEED = "GUARANTEED";
    PRIZE_POOL = "GUARANTEED_PRIZE_POOL";
  }

  public static final short RUNNING = 1;
  public static final short STOPPED = 1;

  public static final String MTT = "T_TOURNY";
  public static final String SITNGO = "T_SITNGO";
  public static final String GAMES = "T_GAME";

  public GameRow[] getGames() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    Vector v = new Vector();
    try {
      StringBuilder sb = new StringBuilder("select *");
      sb.append(" from ");
      sb.append(GAMES);
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        GameRow g = new GameRow();
        g.gameType = r.getInt(GAME_TYPE);
        g.gameName = r.getString(GAME_NAME);
        g.minPlayers = r.getInt(MIN_PLAYERS);
        g.maxPlayers = r.getInt(MAX_PLAYERS);
        g.ab1 = r.getDouble(AB1);
        g.ab2 = r.getDouble(AB2);
        g.minRaise = r.getDouble(MIN_RAISE);
        g.maxBet = r.getDouble(MAX_BET);
        g.rake_percent = r.getDouble(RAKE);
        g.rake2 = r.getDouble(RAKE2);
        g.rake4 = r.getDouble(RAKE4);
        g.rake6 = r.getDouble(RAKE6);
        g.rake8 = r.getDouble(RAKE8);
        g.rake10 = r.getDouble(RAKE10);
        g.move_time = r.getInt(MOVE_TIME);
        g.affiliates = r.getString(AFFILIATE_ID);
        g.partner1_affiliates = r.getString(PARTNER1_AFFILIATE_ID);
        g.partner2_affiliates = r.getString(PARTNER2_AFFILIATE_ID);
        g.private_table = r.getShort(PRIVATE_GAME);
        String plrs = r.getString("INVITED");
        if (plrs != null) g.players = plrs.split("|");
        g.high_rank = r.getInt(HIGH_RANK);
        g.low_rank = r.getInt(LOW_RANK);
        g.lastModified = r.getTimestamp(LAST_MODIFIED);
        g.gameState = r.getShort(GAME_STATE);
        v.add(g);
      }
      r.close();
      ps.close();
      conn.close();

    }
    catch (SQLException e) {
      e.printStackTrace();
      _cat.log(Level.WARNING, "Error in getting game ", e);
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
      throw new DBException(e.getMessage() + " -- while getting Game");
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
    return (GameRow[]) v.toArray(new GameRow[v.size()]);
  }

  

  public SitNGoRow[] getSitNGo() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    Vector v = new Vector();
    try {
      StringBuilder sb = new StringBuilder("select *");
      sb.append(" from ");
      sb.append(SITNGO);
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        SitNGoRow g = new SitNGoRow();
        g.gameType = r.getInt(GAME_TYPE);
        g.gameName = r.getString(GAME_NAME);
        g.limit = r.getInt(LIMIT);
        g.tourbo = r.getInt(TOURBO);
        g.tourbo = g.tourbo == 0 ? 1 : g.tourbo;
        g.maxPlayers = r.getInt(MAX_PLAYERS);
        g.buyin = r.getDouble(BUYIN);
        g.fees = r.getDouble(FEES);
        g.chips = r.getInt(CHIPS);
        g.move_time = r.getInt(MOVE_TIME);
        g.affiliates = r.getString(AFFILIATE_ID);
        g.partner1_affiliates = r.getString(PARTNER1_AFFILIATE_ID);
        g.partner2_affiliates = r.getString(PARTNER2_AFFILIATE_ID);
        g.private_table = r.getShort(PRIVATE_GAME);
        String plrs = r.getString("INVITED");
        if (plrs!= null) g.players = plrs.split("|");
        g.lastModified = r.getTimestamp(LAST_MODIFIED);
        g.gameState = r.getShort(GAME_STATE);
        g.don =r.getString(DON);
        v.add(g);
      }
      r.close();
      ps.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Error in getting game ", e);
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
      throw new DBException(e.getMessage() + " -- while getting Game");
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
    return (SitNGoRow[]) v.toArray(new SitNGoRow[v.size()]);
  }

  public MTTRow[] getMTT() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    Vector v = new Vector();
    try {
      StringBuilder sb = new StringBuilder("select *");
      sb.append(" from ");
      sb.append(MTT);
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        MTTRow g = new MTTRow();
        g.gameType = r.getInt(GAME_TYPE);
        g.gameName = r.getString(GAME_NAME);
        g.limit = r.getInt(LIMIT);
        g.tourbo = r.getInt(TOURBO);
        g.tourbo = g.tourbo == 0 ? 1 : g.tourbo;

        g.maxPlayers = r.getInt(MAX_PLAYERS);

        g.schedule0 = r.getInt(SCHEDULE_0);
        g.schedule1 = r.getInt(SCHEDULE_1);
        g.schedule2 = r.getInt(SCHEDULE_2);
        g.schedule3 = r.getInt(SCHEDULE_3);
        g.schedule4 = r.getInt(SCHEDULE_4);
        g.declInt = r.getInt(DECL_INT);
        g.joinInt = r.getInt(JOIN_INT);
        g.regInt = r.getInt(REG_INT);

        g.buyin = r.getDouble(BUYIN);
        g.fees = r.getDouble(FEES);
        g.chips = r.getInt(CHIPS);
        g.move_time = r.getInt(MOVE_TIME);
        g.affiliates = r.getString(AFFILIATE_ID);
        g.partner1_affiliates = r.getString(PARTNER1_AFFILIATE_ID);
        g.partner2_affiliates = r.getString(PARTNER2_AFFILIATE_ID);
        g.private_table = r.getShort(PRIVATE_GAME);
        String plrs = r.getString("INVITED");
        if (plrs != null)  g.players = plrs.split("|");
        g.lastModified = r.getTimestamp(LAST_MODIFIED);
        g.gameState = r.getShort(GAME_STATE);
        v.add(g);
      }
      r.close();
      ps.close();
      conn.close();

    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Error in getting game ", e);
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
      throw new DBException(e.getMessage() + " -- while getting Game");
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
    return (MTTRow[]) v.toArray(new MTTRow[v.size()]);
  }

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("DBInitGames:\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

  public class GameRow {
    public String gameName;
    public int gameType;
    public double ab1, ab2;
    public double minRaise, maxBet;
    public int minPlayers, maxPlayers;
    public double rake_percent, rake2, rake4, rake6, rake8, rake10;
    public int high_rank = 0;
    public int low_rank = 0;
    public int move_time;
    public String affiliates;
    public String partner1_affiliates;
    public String partner2_affiliates;
    public short gameState;
    public short private_table;
    public java.util.Date lastModified;
    public String[] players;
    public String stack;

    public double[] maxRake(){
      double mr[] = { rake2, rake4, rake6, rake8, rake10 };
      return mr;
    }

    public String[] getAffilaites(){
      String []aff = { affiliates };
      return aff;
    }

    public String[] getPartners(){
      String pt[] = {};
      return pt;
    }

    public String toString() {
      return gameName + ", " + gameType + ", " + minRaise + ", " + maxBet +
          ", " + minPlayers + ", " + maxPlayers + ", " + rake_percent;
    }
  }

  public class SitNGoRow {
    public String gameName;
    public int gameType;
    public int limit, tourbo;
    public double buyin, fees;
    public  int chips;
    public int maxPlayers;
    public int move_time;
    public String affiliates;
    public String partner1_affiliates;
    public String partner2_affiliates;
    public short gameState;
    public short private_table;
    public java.util.Date lastModified;
    public String[] players;
    public int low_rank,high_rank;
    public String don;

    public String toString() {
      return gameName + ", " + gameType + ", " + buyin + ", " + fees + ", " +
          chips + ", " + maxPlayers + ", " + move_time;
    }

  }

  public class MTTRow {
    public String gameName;
    public int gameType;
    public int limit, tourbo;
    public double buyin, fees;
    public int chips;
    public int schedule0;
    public int schedule1;
    public int schedule2;
    public int schedule3;
    public int schedule4;
    public int declInt;
    public int regInt;
    public int joinInt;
    public int maxPlayers;
    public int move_time;
    public String affiliates;
    public String partner1_affiliates;
    public String partner2_affiliates;
    public short gameState;
    public short private_table;
    public java.util.Date lastModified;
    public String[] players;
    public int lr,hr;
    public String tourny_type;
    public String guaranteed;
    public double prize_pool;

    public int[] schedule(){
      int sch[] = { schedule0, schedule1, schedule2, schedule3, schedule4 };
      return sch;
    }

    public String toString() {
      return gameName + ", " + tourbo + ", " + gameType + ", " + buyin + ", " + fees + ", " +
          chips + ", " + maxPlayers + ", " + move_time +
          " , " + schedule0 + ":" + schedule1 + ":" + schedule2 + ":" +
          schedule3 + ":" + schedule4 + ", " + declInt + ":" + regInt + ":" +
          joinInt;
    }
  }
  
  public int saveGames(GameRow gr) throws DBException {
	    int r = -1;
	    Connection conn = null;
	    PreparedStatement ps = null;

	    //if (_modified) {
	      try {
	    	  StringBuilder sb = new StringBuilder("insert into T_GAME ( ");
	          sb.append(GAME_NAME).append(",");
	          sb.append(GAME_TYPE).append(",");
	          sb.append("AB1").append(","); //AB1, AB2,
	          sb.append("AB2").append(",");
	          sb.append(MIN_RAISE).append(",");
	          sb.append(MAX_BET).append(",");
	          sb.append(AFFILIATE_ID).append(",");
	          sb.append(PARTNER1_AFFILIATE_ID).append(",");
	          sb.append(PARTNER2_AFFILIATE_ID).append(",");
	          sb.append(PRIVATE_GAME).append(",");
	          sb.append("INVITED").append(",");
	          sb.append(MIN_PLAYERS).append(",");
	          sb.append(MAX_PLAYERS).append(",");
	          sb.append("RAKE_PERCENT").append(",");
	          sb.append("MAX_RAKE1").append(",");
	          sb.append("MAX_RAKE2").append(",");
	          sb.append("MAX_RAKE3").append(",");
	          sb.append("MAX_RAKE4").append(",");
	          sb.append("MAX_RAKE5").append(",");
	          sb.append("MOVE_TIME").append(",");
	          sb.append(LOW_RANK).append(",");
	          sb.append(HIGH_RANK).append(",");
	          sb.append(GAME_STATE).append(",");
	          sb.append("START_DATE").append(",");
	          sb.append("END_DATE").append(",");
	          sb.append(STACK).append(",");
	          sb.append(LAST_MODIFIED).append(")");
	          sb.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
	          _cat.finest(sb.toString());
	          conn = ConnectionManager.getConnection("GameEngine");
	          conn.setAutoCommit(true);
	          ps = conn.prepareStatement(sb.toString());
	          int i = 1;
	          ps.setString(i++, gr.gameName);
	          ps.setInt(i++, gr.gameType);
	          ps.setDouble(i++, gr.ab1);
	          ps.setDouble(i++, gr.ab2);
	          ps.setDouble(i++, gr.minRaise);
	          ps.setDouble(i++, gr.maxBet);
	          ps.setString(i++, gr.affiliates);
	          ps.setString(i++, gr.partner1_affiliates);
	          ps.setString(i++, gr.partner2_affiliates);
	          ps.setInt(i++, gr.private_table);
	          String players_str="|";
	         /* for (int j=0;j<gr.players.length;j++){
	              players_str+=gr.players[j] + "|";
	          }*/
	          ps.setString(i++, players_str);
	          ps.setInt(i++, gr.minPlayers);
	          ps.setInt(i++, gr.maxPlayers);
	          ps.setInt(i++, 0);
	          ps.setInt(i++, 0);
	          ps.setInt(i++, 0);
	          ps.setInt(i++, 0);
	          ps.setInt(i++, 0);
	          ps.setInt(i++, 0);
	          ps.setInt(i++, 30);
	          ps.setInt(i++, gr.low_rank);
	          ps.setInt(i++, gr.high_rank);
	          ps.setInt(i++, gr.gameState);
	          Calendar c = Calendar.getInstance();
	          ps.setTimestamp(i++, new java.sql.Timestamp(c.getTimeInMillis()));
	          c.add(Calendar.MONTH, 1);
	          ps.setTimestamp(i++,  new java.sql.Timestamp(c.getTimeInMillis()));
	          ps.setString(i++, gr.stack);
	          ps.setTimestamp(i++, 
	                          new java.sql.Timestamp(gr.lastModified == null ? 
	                                                 0 : 
	                                                 gr.lastModified.getTime()));

	          r = ps.executeUpdate();
	          _cat.finest(this.toString());
	          ps.close();
	          conn.close();
	      }catch (MySQLIntegrityConstraintViolationException pk) {
	         r = -2;
	         pk.printStackTrace();
	      }catch (SQLException e) {
	          _cat.log(Level.SEVERE, "Unable to save  Game" + e.getMessage(), 
	                   e);
	          _cat.severe(this.toString());

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
	          throw new DBException(e.getMessage() + 
	                                " -- while saving Game");
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
	    //}
	  return r;
	  }
  public int saveSNG(SitNGoRow sr) throws DBException{
	  int res=-1;
	  Connection conn = null;
	    PreparedStatement ps = null;
	   // if (_modified) {
	    try {
	    	System.out.println("game name="+sr.gameName);
	    	StringBuilder sb = new StringBuilder("insert into ");
	    	sb.append(SITNGO).append("(");
	        sb.append(GAME_NAME).append(",");
	        sb.append(GAME_TYPE).append(",");
	        sb.append(LIMIT).append(",");
	        sb.append(BUYIN).append(",");
	        sb.append(TOURBO).append(",");
	        sb.append(FEES).append(",");
	        sb.append(CHIPS).append(",");
	        sb.append(MAX_PLAYERS).append(",");
	        sb.append(MOVE_TIME).append(",");
	        sb.append(AFFILIATE_ID).append(",");
	        sb.append(PARTNER1_AFFILIATE_ID).append(",");
	        sb.append(PARTNER2_AFFILIATE_ID).append(",");
	        sb.append(PRIVATE_GAME).append(",");
	        sb.append(LOW_RANK).append(",");
	        sb.append(HIGH_RANK).append(",");
	        sb.append(GAME_STATE).append(",");
	        sb.append(LAST_MODIFIED).append(",");
	        sb.append(DON).append(",");
	        sb.append(INVITED);
	        sb.append(")");
	        sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	      conn = ConnectionManager.getConnection("GameEngine");
	      ps = conn.prepareStatement(sb.toString());
	      _cat.finest(sb.toString());
	      int i = 1;
	        ps.setString(i++,sr.gameName );
	        ps.setInt(i++,sr.gameType );
	        ps.setInt(i++,sr.limit );
	        ps.setDouble(i++, sr.buyin );
	        ps.setInt(i++, sr.tourbo);
	        ps.setDouble(i++,sr.fees );
	        ps.setDouble(i++,sr.chips );
	        ps.setInt(i++, sr.maxPlayers );
	        ps.setInt(i++, sr.move_time);
	        ps.setString(i++,sr.affiliates!=null ? sr.affiliates : "admin" );
	        ps.setString(i++,sr.partner1_affiliates );
	        ps.setString(i++, sr.partner2_affiliates);
	        ps.setInt(i++, sr.private_table);
	        ps.setInt(i++, sr.low_rank);
	        ps.setInt(i++, sr.high_rank);
	        ps.setShort(i++, sr.gameState);
	        ps.setTimestamp(i++,
                    new java.sql.Timestamp(sr.lastModified == null ?
                                           System.currentTimeMillis() :
                                           sr.lastModified.getTime()));
	       
	        ps.setString(i++, sr.don);
	        String players_str="|";
	          /*for (int j=0;j<sr.players.length;j++){
	              players_str+=sr.players[j] + "|";
	          }*/
	        ps.setString(i++, players_str);
	        res = ps.executeUpdate();
	        _cat.finest(this.toString());
	        ps.close();
	      conn.close();
	    }catch (MySQLIntegrityConstraintViolationException pk) {
	    	res = -2;
	          _cat.log(Level.WARNING, "Unable to save Toruny - PK " + pk.getMessage(), pk);
		      _cat.warning(this.toString());
	    }catch (SQLException e) {
	      _cat.log(Level.WARNING, "Error in save SitnGo game ", e);
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
	      throw new DBException(e.getMessage() + " -- while saving SitnGo game");
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
	   // }
	    return res;
  }
  
  public int saveMTT(MTTRow mr) throws DBException {
	    Connection conn = null;
	    PreparedStatement ps = null;

	    int r = -1;
	   // if (_modified) {
	      try {
	        StringBuilder sb = new StringBuilder("insert into T_TOURNY ( ");
	        sb.append(GAME_NAME).append(",");
	        sb.append(GAME_TYPE).append(",");
	        sb.append(LIMIT).append(",");
	        sb.append(BUYIN).append(",");
	        sb.append(TOURBO).append(",");
	        sb.append(FEES).append(",");
	        sb.append(CHIPS).append(",");
	        sb.append(MAX_PLAYERS).append(",");
	        sb.append(SCHEDULE_0).append(",");
	        sb.append(SCHEDULE_1).append(",");
	        sb.append(SCHEDULE_2).append(",");
	        sb.append(SCHEDULE_3).append(",");
	        sb.append(SCHEDULE_4).append(",");
	        sb.append(DECL_INT).append(",");
	        sb.append(REG_INT).append(",");
	        sb.append(JOIN_INT).append(",");
	        sb.append(MOVE_TIME).append(",");
	        sb.append(TOURNY_TYPE).append(",");
	        sb.append(GUARANTEED).append(",");
	        sb.append(PRIZE_POOL).append(",");
	        sb.append(AFFILIATE_ID).append(",");
	        sb.append(PARTNER1_AFFILIATE_ID).append(",");
	        sb.append(PARTNER2_AFFILIATE_ID).append(",");
	        sb.append(PRIVATE_GAME).append(",");
	        sb.append(INVITED).append(",");
	        sb.append(LOW_RANK).append(",");
	        sb.append(HIGH_RANK).append(",");
	        sb.append(GAME_STATE).append(",");
	        sb.append(LAST_MODIFIED).append(")");
	        sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
	        		"?, ?, ?, ?, ?, ?, ?, ?, ?)");
	        _cat.finest(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	        int i = 1;
	        ps.setString(i++, mr.gameName);
	        ps.setInt(i++, mr.gameType);
	        ps.setInt(i++, mr.limit);
	        ps.setDouble(i++, mr.buyin);
	        ps.setInt(i++, mr.tourbo);
	        ps.setDouble(i++, mr.fees);
	        ps.setDouble(i++, mr.chips);
	        ps.setInt(i++, mr.maxPlayers);
	        ps.setInt(i++, mr.schedule0);
	        ps.setInt(i++, mr.schedule1);
	        ps.setInt(i++, mr.schedule2);
	        ps.setInt(i++, mr.schedule3);
	        ps.setInt(i++, mr.schedule4);
	        ps.setInt(i++, mr.declInt);
	        ps.setInt(i++, mr.regInt);
	        ps.setInt(i++, mr.joinInt);
	        ps.setInt(i++, mr.move_time);
	        ps.setString(i++, mr.tourny_type);
	        ps.setString(i++, mr.guaranteed);
	        ps.setDouble(i++, mr.prize_pool);
	        ps.setString(i++, mr.affiliates);
	        ps.setString(i++, mr.partner1_affiliates);
	        ps.setString(i++, mr.partner2_affiliates);
	        ps.setInt(i++, mr.private_table);
	        String players_str="|";
	          /*for (int j=0;j<mr.players.length;j++){
	              players_str+=mr.players[j] + "|";
	          }*/
	        ps.setString(i++, players_str);
	        ps.setInt(i++, mr.lr);
	        ps.setInt(i++, mr.hr);
	        ps.setInt(i++, mr.gameState);
	        ps.setTimestamp(i++,
	                        new java.sql.Timestamp(mr.lastModified == null ?
	                                               System.currentTimeMillis() :
	                                               mr.lastModified.getTime()));
	        r = ps.executeUpdate();
	        _cat.finest(this.toString());
	        ps.close();
	        conn.close();
	      }catch (MySQLIntegrityConstraintViolationException pk) {
	          r = -2;
	          _cat.log(Level.WARNING, "Unable to save Toruny - PK " + pk.getMessage(), pk);
		      _cat.warning(this.toString());
	      }catch (SQLException e) {
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
	  //  }
	    return r;
	  }
  
  public int deleteGame(String gameName) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    int r = -1;
	  try{
		  StringBuilder sb = new StringBuilder("delete from ");
		  sb.append(GAMES);
		  sb.append(" WHERE ");
		  sb.append(GAME_NAME);
		  sb.append("=?");
		  _cat.finest(sb.toString());
		  System.out.println(sb.toString());
		  conn = ConnectionManager.getConnection("GameEngine");
	      ps = conn.prepareStatement(sb.toString());
	      ps.setString(1, gameName);
	      r =ps.executeUpdate();
	      
	        ps.close();
	        conn.close();
	      }
	      catch (SQLException e) {
	        _cat.log(Level.WARNING, "Unable to delete game " + e.getMessage(), e);
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
	  //  }
	    return r;
	  }
  
  public int deleteMTT(String tournyName) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    int r = -1;
	  try{
		  StringBuilder sb = new StringBuilder("delete from ");
		  sb.append(MTT);
		  sb.append(" WHERE ");
		  sb.append(GAME_NAME);
		  sb.append("=?");
		  _cat.finest(sb.toString());
		  System.out.println(sb.toString());
		  conn = ConnectionManager.getConnection("GameEngine");
	      ps = conn.prepareStatement(sb.toString());
	      ps.setString(1, tournyName);
	      r =ps.executeUpdate();
	        ps.close();
	        conn.close();
	      }
	      catch (SQLException e) {
	        _cat.log(Level.WARNING, "Unable to delete MTT " + e.getMessage(), e);
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
	  //  }
	    return r;
	  }
  public int deleteSNG(String gameName) throws DBException{
	  	Connection conn = null;
	    PreparedStatement ps = null;
	    int r = -1;
	  try{
		  StringBuilder sb = new StringBuilder("delete from ");
		  sb.append(SITNGO);
		  sb.append(" WHERE ");
		  sb.append(GAME_NAME);
		  sb.append("=?");
		  _cat.finest(sb.toString());
		  System.out.println(sb.toString());
		  conn = ConnectionManager.getConnection("GameEngine");
	      ps = conn.prepareStatement(sb.toString());
	      ps.setString(1, gameName);
	      r =ps.executeUpdate();
	        ps.close();
	        conn.close();
	      }
	      catch (SQLException e) {
	        _cat.log(Level.WARNING, "Unable to delete SnG " + e.getMessage(), e);
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
	  //  }
	    return r;
	  }
  public static void main(String args[]) throws Exception {

    DBInitGames dbi = new DBInitGames();
    GameRow[] g = dbi.getGames();

    for (int i = 0; i < g.length; i++) {
      System.out.println(g[i]);
    }

    SitNGoRow[] sg = dbi.getSitNGo();

    for (int i = 0; i < sg.length; i++) {
      System.out.println(sg[i]);
    }

    MTTRow[] mt = dbi.getMTT();

    for (int i = 0; i < mt.length; i++) {
      System.out.println(mt[i]);
    }
  }

}
