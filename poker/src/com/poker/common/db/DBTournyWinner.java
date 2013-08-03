package com.poker.common.db;

import com.agneya.util.ConnectionManager;

import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;
import com.golconda.game.Player;

import com.poker.common.interfaces.TournyInterface;
import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;
import com.poker.server.GamePlayer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class DBTournyWinner {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBTournyWinner.class.getName());

  public String gameId;
  public PokerGameType type;
  private String userId[];
  private int rank[];
  private double amount[];
  private String session[];
  private DBPlayer dbp[];
  public double buyin, fees, chips;
  public String user;
  public int his_rank;
  public double amt_won;
  public java.util.Date ts;
  public String regUserId;


  public static final String TOURNY_NAME_FK;
  public static final String GAME_TYPE_ID_FK;
  public static final String USER_ID_FK;
  public static final String RANK;
  public static final String AMOUNT;
  public static final String TIMESTAMP;

  public static final String BUYIN;
  public static final String FEES;
  public static final String CHIPS;
  public static final String REGISTER_TS;

  static {
    TOURNY_NAME_FK = "TOURNY_NAME_FK";
    GAME_TYPE_ID_FK = "GAME_TYPE_ID_FK";
    USER_ID_FK = "USER_ID_FK";
    RANK = "RANK";
    AMOUNT = "AMOUNT";
    TIMESTAMP = "TIMESTAMP";
    BUYIN = "BUYIN";
    FEES = "FEES";
    CHIPS = "CHIPS";
    REGISTER_TS = "REGISTER_TIMESTAMP";

  }
  
  public DBTournyWinner(){}

  public DBTournyWinner(TournyInterface sin) {
    gameId = sin.name();
    type = sin.type();
    PokerPresence[] p = sin.winners();
    userId = new String[p.length];
    rank = new int[p.length];
    amount = new double[p.length];
    session = new String[p.length];
    dbp = new DBPlayer[p.length];
    for (int i = sin.winners().length - 1, j = 1; i >= 0 && p[i] != null; i--, j++) {
      _cat.finest(p[i].toString());
      userId[i] = p[i].name();
      rank[i] = j;
      amount[i] = sin.prize(j, sin.winners().length);
      Player gp = p[i].player();
      session[i]= ((GamePlayer)gp).session();
      dbp[i] = p[i].player().getDBPlayer();
    }
  }
  	
  	public String getGameId() {	return gameId;}
	public void setGameId(String gameId) {		this.gameId = gameId;	}
	
	public String getUser() {		return user;	}
	public void setUser(String user) {		this.user = user;	}
	
	public double getAmtWon() {		return amt_won;	}
	public void setAmtWon(double amtWon) {		amt_won = amtWon;	}
	
	public java.util.Date getTs() {		return ts;	}
	public void setTs(java.util.Date ts) {		this.ts = ts;	}
	
	public String getTsString() {		return ts.toString();	}
	public int getHisRank() {		return his_rank;	}

	public void setHisRank(int hisRank) {		his_rank = hisRank;	}
	
	public String getRegUserId() {		return regUserId;	}
	public void setRegUserId(String regUserId) {		this.regUserId = regUserId;	}

	public double getBuyin() {		return buyin;	}
	public void setBuyin(double buyin) {		this.buyin = buyin;	}
	
	public double getFees() {		return fees;	}
	public void setFees(double fees) {		this.fees = fees;	}
	
	public double getChips() {		return chips;	}
	public void setChips(double chips) {		this.chips = chips;	}

	public int save() throws DBException {    Connection conn = null;    PreparedStatement ps = null;

    int r = -1;
      try {
        StringBuilder sb = new StringBuilder("insert into T_TOURNY_WINNERS ( ");
        sb.append(TOURNY_NAME_FK).append(",");
        sb.append(GAME_TYPE_ID_FK).append(",");
        sb.append(USER_ID_FK).append(",");
        sb.append(RANK).append(",");
        sb.append(AMOUNT).append(",");
        sb.append(TIMESTAMP).append(")");
        sb.append(" values ( ?, ?, ?, ?, ?, ? )");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        for (int i = 0; i < userId.length; i++) {
          ps.setString(1, gameId);
          ps.setInt(2, type.intVal());
          ps.setString(3, userId[i]);
          ps.setInt(4, rank[i]);
          ps.setDouble(5, amount[i]);
          ps.setTimestamp(6,
                          new java.sql.Timestamp(System.currentTimeMillis()));
          r = ps.executeUpdate();
          //if (amount[i] > 0){
            //dbp[i].addToRealChips(session[i], amount[i], "mtt");
          //}
          _cat.finest(this.toString());
        }
        ps.close();
        conn.close();
      }
      catch (Exception e) {
        _cat.log(Level.WARNING, "Unable to save  Tourny" + e.getMessage(), e);
        _cat.warning(this.toString());

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
        throw new DBException(e.getMessage() + " -- declaring winners for tourny");
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

    public static DBTournyWinner[] getTournyResultForPlayer(String gid)  { //last 100
      Connection conn = null;
      PreparedStatement ps = null;
      try {
        StringBuilder sb = new StringBuilder("select *");
        sb.append(" from T_TOURNY_WINNERS where ");
        sb.append(USER_ID_FK).append("=?   order by  TIMESTAMP desc limit 100");
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, gid);
        _cat.finest(sb.toString());
        ResultSet r = ps.executeQuery();
        Vector<DBTournyWinner>v = new Vector<DBTournyWinner>();
        while (r.next()) {
          DBTournyWinner dbt = new DBTournyWinner();
          dbt.gameId = r.getString(TOURNY_NAME_FK);
          dbt.his_rank = r.getInt(RANK);
          dbt.amt_won = r.getDouble(AMOUNT);
          dbt.ts = r.getTimestamp(TIMESTAMP);
          v.add(dbt);
        }
          r.close();
          ps.close();
          conn.close();
          return v.toArray(new DBTournyWinner[v.size()]);
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
        return new DBTournyWinner[0];
    }


  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("TournyWinners:\n");
    str.append("GameId = ").append(gameId).append("\n");
    for (int i = 0; i < userId.length; i++) {
      str.append("User = ").append(userId[i]).append("-").append(rank[i]).
          append("-").append(amount[i]).append("\n");
    }
    return (str.toString());
  }

  public DBTournyWinner[] getMTTWinners(String tournyName) throws DBException {
	    Connection conn = null;
	    PreparedStatement ps = null;
	    Vector w_List=new Vector();
	    System.out.println("in DBMTTW tableName="+tournyName);
	      try {
	        StringBuilder sb = new StringBuilder("select * from T_TOURNY_WINNERS ");
	        sb.append("where TOURNY_NAME_FK like '%"+tournyName+"%'");
	        System.out.println(sb.toString());
	       // _cat.finest(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	          ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBTournyWinner _winner=new DBTournyWinner();
	        	  _winner.gameId =rs.getString(TOURNY_NAME_FK);
	        	  _winner.user = rs.getString(USER_ID_FK); //from DB USER_ID 
	        	  _winner.his_rank = rs.getInt(RANK);
	        	  _winner.ts = rs.getTimestamp(TIMESTAMP);
	        	  _winner.amt_won = rs.getDouble(AMOUNT);
	        	  w_List.add(_winner);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  MTT" + e.getMessage(), e);
	       // _cat.warning(this.toString());
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
	        throw new DBException(e.getMessage() +
	                              " -- retriving  winners for mtt");
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
return (DBTournyWinner[])w_List.toArray(new DBTournyWinner[w_List.size()]); 
	  }
  
  public static DBTournyWinner[] getTournyRegisterUsers(String tournyName) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    Vector reg_list=new Vector();  
	    try {
	        StringBuilder sb = new StringBuilder("select * from T_REGISTERED_TOURNY ");
	        sb.append("where TOURNY_NAME_FK like '%"+tournyName+"%' order by REGISTER_TIMESTAMP desc");
	        System.out.println(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	          ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBTournyWinner _reg=new DBTournyWinner();
	        	  _reg.gameId = rs.getString(TOURNY_NAME_FK);
	        	  _reg.regUserId = rs.getString(USER_ID_FK); 
	        	  _reg.gameId =rs.getString(TOURNY_NAME_FK);
	        	  _reg.buyin = rs.getDouble(BUYIN);
	        	  _reg.fees = rs.getDouble(FEES);
	        	  _reg.chips = rs.getDouble(CHIPS);
	        	  _reg.ts = rs.getTimestamp(REGISTER_TS);
	        	  reg_list.add(_reg);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  Tourny regester users" + e.getMessage(), e);
	       // _cat.warning(this.toString());
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
	        throw new DBException(e.getMessage() +
	                              " -- retriving  regester users for Tourny");
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
	      return (DBTournyWinner[])reg_list.toArray(new DBTournyWinner[reg_list.size()]); 
	  }
  
  public static DBTournyWinner[] getCancelTournyRegisterUsers(String tournyName) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    Vector reg_list=new Vector();  
	    try {
	        StringBuilder sb = new StringBuilder("select * from T_REGISTERED_TOURNY ");
	        sb.append("where TOURNY_NAME_FK like binary '%"+tournyName+"%' order by REGISTER_TIMESTAMP desc");
	        System.out.println(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	          ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBTournyWinner _reg=new DBTournyWinner();
	        	  _reg.gameId = rs.getString(TOURNY_NAME_FK);
	        	  _reg.regUserId = rs.getString(USER_ID_FK); 
	        	  _reg.gameId =rs.getString(TOURNY_NAME_FK);
	        	  _reg.buyin = rs.getDouble(BUYIN);
	        	  _reg.fees = rs.getDouble(FEES);
	        	  _reg.chips = rs.getDouble(CHIPS);
	        	  _reg.ts = rs.getTimestamp(REGISTER_TS);
	        	  reg_list.add(_reg);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  Tourny regester users" + e.getMessage(), e);
	       // _cat.warning(this.toString());
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
	        throw new DBException(e.getMessage() +
	                              " -- retriving  regester users for Tourny");
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
	      return (DBTournyWinner[])reg_list.toArray(new DBTournyWinner[reg_list.size()]); 
	  }
  public static DBTournyWinner[] getTournyRegisterUsers(String tournyName, Timestamp t1, Timestamp t2) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    Vector reg_list=new Vector();  
	    try {
	        StringBuilder sb = new StringBuilder("select * from T_REGISTERED_TOURNY ");
	        sb.append("where TOURNY_NAME_FK like '%"+tournyName+"%' and ");
	        sb.append("REGISTER_TIMESTAMP between date(?) and date(?) ");
	        sb.append("order by REGISTER_TIMESTAMP desc");
	        System.out.println(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	        ps.setTimestamp(1,t1);
	        ps.setTimestamp(2,t2);
	          ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBTournyWinner _reg=new DBTournyWinner();
	        	  _reg.regUserId = rs.getString(USER_ID_FK); 
	        	  _reg.gameId =rs.getString(TOURNY_NAME_FK);
	        	  _reg.buyin = rs.getDouble(BUYIN);
	        	  _reg.fees = rs.getDouble(FEES);
	        	  _reg.chips = rs.getDouble(CHIPS);
	        	  _reg.ts = rs.getTimestamp(REGISTER_TS);
	        	  reg_list.add(_reg);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  Tourny regester users" + e.getMessage(), e);
	       // _cat.warning(this.toString());
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
	        throw new DBException(e.getMessage() +
	                              " -- retriving  regester users for Tourny");
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
	      return (DBTournyWinner[])reg_list.toArray(new DBTournyWinner[reg_list.size()]); 
	  }

  public static synchronized double getUserFeesSum(String userid, Timestamp t1, Timestamp t2) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    double fees_sum = 0;
	    try {
	        StringBuilder sb = new StringBuilder("select sum(FEES) from T_REGISTERED_TOURNY ");
	        sb.append("where USER_ID_FK like binary '%"+userid+"%' and ");
	        sb.append("REGISTER_TIMESTAMP between date(?) and date(?)");
	        System.out.println(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	        ps.setTimestamp(1,t1);
	        ps.setTimestamp(2,t2);
	          ResultSet rs  = ps.executeQuery();
	          if(rs.next()){
	        	  fees_sum = rs.getDouble(1);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive MTT fees sum" + e.getMessage(), e);
	       // _cat.warning(this.toString());
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
	        throw new DBException(e.getMessage() +
	                              " -- retriving MTT fees sum");
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
	      return fees_sum; 
	  }


}
