package com.poker.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.ConnectionManager;
import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;
import com.golconda.game.Player;
import com.poker.common.interfaces.SitnGoInterface;
import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;
import com.poker.server.GamePlayer;


// SQLSERVER/ORACLE

public class DBSitnGoWinner {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBSitnGoWinner.class.getName());

  private String gameId;
  private PokerGameType type;
  public String tournyId;
  public double buyin, fees, chips;
  private String userId[];
  private int rank[];
  private double amount[];
  private String session[];
  private DBPlayer dbp[];
  private Timestamp ts;
  
  private String regUserId;
  private String winnerName;
  private int wRank;;
  private double prizeAmt;

  public static final String GAME_NAME_FK;
  public static final String GAME_TYPE_ID_FK;
  public static final String USER_ID_FK;
  public static final String RANK;
  public static final String AMOUNT;
  public static final String TIMESTAMP;
 

  // Register
  /**
   * Tourny register
   */
  public static final String USER_ID;
  public static final String GAME_RUN_ID;
  public static final String BUYIN;
  public static final String FEES;
  public static final String CHIPS;
  public static final String REGISTER_TS;

  static {
    GAME_NAME_FK = "GAME_NAME_FK";
    GAME_TYPE_ID_FK = "GAME_TYPE_ID_FK";
    USER_ID_FK = "USER_ID_FK";
    RANK = "RANK";
    AMOUNT = "AMOUNT";
    TIMESTAMP = "TIMESTAMP";
    USER_ID = "USER_ID_FK";
    GAME_RUN_ID = "GAME_RUN_ID";
    BUYIN = "BUYIN";
    FEES = "FEES";
    CHIPS = "CHIPS";
    REGISTER_TS = "REGISTER_TIMESTAMP";
  }
  public DBSitnGoWinner(){}

  public DBSitnGoWinner(SitnGoInterface sin) {
    gameId = sin.name();
    type = sin.type();
    tournyId = sin.tournyId();
    buyin = sin.buyIn();
    fees = sin.fees();
    chips = sin.chips();
    PokerPresence[] p = sin.winners();
    userId = new String[p.length];
    rank = new int[p.length];
    amount = new double[p.length];
    session = new String[p.length];
    dbp = new DBPlayer[p.length];
    for (int i = sin.winners().length - 1, j = 1; i >= 0 && p[i] != null; i--, j++) {
      userId[i] = p[i].name();
      rank[i] = j;
      amount[i] = sin.prize(j, sin.getPlayerList().length);
      Player gp = p[i].player();
      session[i] = ( (GamePlayer) gp).session();
      dbp[i] = p[i].player().getDBPlayer();
    }
    _cat.finest(this.toString());
    _modified = true;
  }

  public int save() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    if (_modified) {
      try {
        StringBuilder sb = new StringBuilder("insert into T_SITNGO_WINNERS ( ");
        sb.append(GAME_NAME_FK).append(",");
        sb.append(GAME_TYPE_ID_FK).append(",");
        sb.append(GAME_RUN_ID).append(",");
        sb.append(USER_ID_FK).append(",");
        sb.append(RANK).append(",");
        sb.append(AMOUNT).append(",");
        sb.append(TIMESTAMP).append(")");
        sb.append(" values ( ?, ?, ?, ?, ?, ?, ? )");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        conn.setAutoCommit(true);
        ps = conn.prepareStatement(sb.toString());
        for (int i = 0; i < userId.length; i++) {
          ps.setString(1, gameId);
          ps.setInt(2, type.intVal());
          ps.setString(3, tournyId);
          ps.setString(4, userId[i]);
          ps.setInt(5, rank[i]);
          ps.setDouble(6, amount[i]);
          ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
          r = ps.executeUpdate();
          dbp[i].addRealSnGWin(session[i], amount[i], gameId, type.toString());
          _cat.info("Addig winner " + userId[i]);
        }
        ps.close();
        conn.close();
      }
      catch (Exception e) {
    	  e.printStackTrace();
        _cat.log(Level.WARNING, "Unable to save  SitNGo" + e.getMessage(), e);
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
        throw new DBException(e.getMessage() +
                              " -- declaring winners for sit n go");
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

  public static int register(String userId, SitnGoInterface sin) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      StringBuilder sb = new StringBuilder(
          "insert into T_REGISTERED_SITNGO ( ");
      sb.append(USER_ID).append(",");
      sb.append(GAME_NAME_FK).append(",");
      sb.append(GAME_RUN_ID).append(",");
      sb.append(GAME_TYPE_ID_FK).append(",");
      sb.append(BUYIN).append(",");
      sb.append(FEES).append(",");
      sb.append(CHIPS).append(",");
      sb.append(REGISTER_TS).append(")");
      _cat.finest(sb.toString());
      sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ? )");
      _cat.finest(userId + ", " + sin.name() + ", " + sin.tournyId());
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, userId);
      ps.setString(2, sin.name());
      ps.setString(3, sin.tournyId());
      ps.setInt(4, sin.type().intVal());
      ps.setDouble(5, sin.buyIn());
      ps.setDouble(6, sin.fees());
      ps.setDouble(7, sin.chips());
      ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
      r = ps.executeUpdate();
      //conn.commit();
      ps.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.log(Level.WARNING, "Unable to register the player for sitngo" + e.getMessage(), e);
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

  public static DBSitnGoWinner[] getSngRegisterUsers(String gameName) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    Vector reg_list=new Vector();  
	    try {
	        StringBuilder sb = new StringBuilder("select * from T_REGISTERED_SITNGO ");
	        sb.append("where GAME_NAME_FK like '%"+gameName+"%' order by REGISTER_TIMESTAMP desc");
	        System.out.println(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	          ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBSitnGoWinner _reg=new DBSitnGoWinner();
	        	  _reg.regUserId = rs.getString(USER_ID); 
	        	  _reg.gameId =rs.getString(GAME_NAME_FK);
	        	  _reg.tournyId = rs.getString(GAME_RUN_ID);
	        	  _reg.buyin = rs.getDouble(BUYIN);
	        	  _reg.fees = rs.getDouble(FEES);
	        	  _reg.chips = rs.getDouble(CHIPS);
	        	  _reg.ts = rs.getTimestamp(REGISTER_TS);
	        	  reg_list.add(_reg);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  SitNGo regester users" + e.getMessage(), e);
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
	                              " -- retriving  regester users for sit n go");
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
  return (DBSitnGoWinner[])reg_list.toArray(new DBSitnGoWinner[reg_list.size()]); 
	  }
  
  public static DBSitnGoWinner[] getSngRegisterUsersForDuration(String gameName, Timestamp t1, Timestamp t2) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    Vector reg_list=new Vector();  
	    try {
	        StringBuilder sb = new StringBuilder("select * from T_REGISTERED_SITNGO ");
	        sb.append("where GAME_NAME_FK like '%"+gameName+"%' and " );
	        sb.append("REGISTER_TIMESTAMP between date(?) and date(?) ");
	        sb.append("order by REGISTER_TIMESTAMP desc");
	        System.out.println(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	        ps.setTimestamp(1,t1);
	        ps.setTimestamp(2,t2);
	        ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBSitnGoWinner _reg=new DBSitnGoWinner();
	        	  _reg.regUserId = rs.getString(USER_ID); 
	        	  _reg.gameId =rs.getString(GAME_NAME_FK);
	        	  _reg.tournyId = rs.getString(GAME_RUN_ID);
	        	  _reg.buyin = rs.getDouble(BUYIN);
	        	  _reg.fees = rs.getDouble(FEES);
	        	  _reg.chips = rs.getDouble(CHIPS);
	        	  _reg.ts = rs.getTimestamp(REGISTER_TS);
	        	  reg_list.add(_reg);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  SitNGo regester users" + e.getMessage(), e);
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
	                              " -- retriving  regester users for sit n go");
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
  return (DBSitnGoWinner[])reg_list.toArray(new DBSitnGoWinner[reg_list.size()]); 
	  }
  
  public DBSitnGoWinner[] getSngWinners(String tableName) throws DBException {
	    Connection conn = null;
	    PreparedStatement ps = null;
	    Vector w_List=new Vector();
	    //System.out.println("in DBSngW tableName="+tableName);
	      try {
	        StringBuilder sb = new StringBuilder("select * from T_SITNGO_WINNERS ");
	        sb.append("where GAME_NAME_FK like '%"+tableName+"%'");
	        System.out.println(sb.toString());
	       // _cat.finest(sb.toString());
	        conn = ConnectionManager.getConnection("GameEngine");
	        ps = conn.prepareStatement(sb.toString());
	          ResultSet rs  = ps.executeQuery();
	          while(rs.next()){
	        	  DBSitnGoWinner _winner=new DBSitnGoWinner();
	        	  _winner.gameId =rs.getString(GAME_NAME_FK);
	        	  _winner.tournyId = rs.getString(GAME_RUN_ID);
	        	  _winner.winnerName = rs.getString(USER_ID); //from DB USER_ID 
	        	  _winner.wRank = rs.getInt(RANK);
	        	  _winner.ts = rs.getTimestamp(TIMESTAMP);
	        	  _winner.prizeAmt = rs.getDouble(AMOUNT);
	        	  w_List.add(_winner);
	        	  //System.out.println("winnerName="+_winner.winnerName);
	          }
	      }
	      catch (Exception e) {
	        _cat.log(Level.WARNING, "Unable to retrive  SitNGo" + e.getMessage(), e);
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
	                              " -- retriving  winners for sit n go");
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
  return (DBSitnGoWinner[])w_List.toArray(new DBSitnGoWinner[w_List.size()]); 
	  }

  public static synchronized double getUserFeesSum(String userid, Timestamp t1, Timestamp t2) throws DBException{
	  Connection conn = null;
	    PreparedStatement ps = null;
	    double fees_sum = 0;
	    try {
	        StringBuilder sb = new StringBuilder("select sum(FEES) from T_REGISTERED_SITNGO ");
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
	        _cat.log(Level.WARNING, "Unable to retrive SNG fees sum" + e.getMessage(), e);
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
	                              " -- retriving SNG fees sum");
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
  
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("SitnGoWinners:\n");
    str.append("GameId = ").append(gameId).append("\n");
    for (int i = 0; i < userId.length; i++) {
      str.append("User = ").append(userId[i]).append("-").append(rank[i]).
          append("-").append(amount[i]).append("\n");
    }
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

	public String getGameId() {		return gameId;	}
	public void setGameId(String gameId) {		this.gameId = gameId;	}
	
	public String getTournyId() {		return tournyId;	}
	public void setTournyId(String tournyId) {		this.tournyId = tournyId;	}
	
	public String[] getUserId() {		return userId;	}
	public void setUserId(String[] userId) {		this.userId = userId;	}
	
	public int[] getRank() {		return rank;	}
	public void setRank(int[] rank) {		this.rank = rank;	}
	
	public double[] getAmount() {		return amount;	}
	public void setAmount(double[] amount) {		this.amount = amount;	}
	
	public Timestamp getTs() {		return ts;	}
	public void setTs(Timestamp ts) {		this.ts = ts;	}
	
	public String getTsString() {		return ts.toString();	}
	
	public String getWinnerName() {		return winnerName;	}
	public void setWinnerName(String winnerName) {		this.winnerName = winnerName;	}
	
	public int getwRank() {		return wRank;	}
	public void setwRank(int wRank) {		this.wRank = wRank;	}
	
	public double getPrizeAmt() {		return prizeAmt;	}
	public void setPrizeAmt(double prizeAmt) {		this.prizeAmt = prizeAmt;	}
	
	public String getRegUserId() {		return regUserId;	}
	public void setRegUserId(String regUserId) {		this.regUserId = regUserId;	}

	public double getBuyin() {		return buyin;	}
	public void setBuyin(double buyin) {		this.buyin = buyin;	}
	
	public double getFees() {		return fees;	}
	public void setFees(double fees) {		this.fees = fees;	}
	
	public double getChips() {		return chips;	}
	public void setChips(double chips) {		this.chips = chips;	}
	
}
