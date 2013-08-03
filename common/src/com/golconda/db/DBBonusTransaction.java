package com.golconda.db;

import com.agneya.util.ConnectionManager;
import com.agneya.util.DBUtils;

import com.agneya.util.SharedConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class DBBonusTransaction {
  // set the category for logging
  static Logger _cat = Logger.getLogger(DBBonusTransaction.class.getName());
  
  public int _id;
  public Timestamp _ts;
  public String _userid;
  public String _session;
  public String _affiliateid;
  public String _bonus_code;
  public ModuleType _module;
  public double _realChips;
  public double _playChips;
  public double _points;
  public double _amount;
  public TransactionType _type;
  public String _currency;
  public String _status="COMPLETED";  //COMPLETED, FAILED, APPROVED, PENDING
  public String _comment;


  public static final String TRANSACTION_ID;
  public static final String STATUS;
  public static final String TRANSACTION_TS;
  public static final String DISPLAY_NAME;
  public static final String SESSION;
  public static final String AFFILIATE;
  public static final String BONUS_CODE;
  public static final String MODULE;
  public static final String REAL_CHIPS;
  public static final String PLAY_CHIPS;
  public static final String POINTS;
  public static final String AMOUNT;
  public static final String TYPE;
  public static final String CURRENCY;
  public static final String COMMENT;

  public static final String TRANSACTION_TABLE =
      "T_BONUS_TRANSACTION";

  static {
  	TRANSACTION_ID = "TRANSACTION_ID";
        TRANSACTION_TS = "TRANSACTION_TIMESTAMP";
        STATUS = "TRANSACTION_STATUS";
        TYPE = "TRANSACTION_TYPE";
        DISPLAY_NAME = "USER_ID_FK";
        SESSION = "SESSION_ID_FK";
        AFFILIATE = "AFFILIATE_ID_FK";
        MODULE = "MODULE";
        BONUS_CODE = "BONUS_CODE";
        REAL_CHIPS = "REAL_CHIPS";
        PLAY_CHIPS = "PLAY_CHIPS";
        POINTS = "POINTS";
        AMOUNT = "AMOUNT";
        CURRENCY = "CURRENCY";
        COMMENT = "TRANSACTION_COMMENT";
  }

  public static String STATUS_COMPLETED="COMPLETED";
  public static String STATUS_FAILED="FAILED";
  public static String STATUS_APPROVED="APPROVED";
  public static String STATUS_PENDING="PENDING";
  
  public static String CURRENCY_USD = "POINTS";

//
// public int _id;
// public Timestamp _ts;
// public String _userid;
// public String _session;
// public String _affiliateid;
// public String _bonus_code;
// public double _realChips;
// public double _playChips;
// public double _points;
// public double _amount;
// public TransactionType _type;
// public String _currency;
// public String _status="COMPLETED";  //COMPLETED, FAILED, APPROVED, PENDING
// public String _comment;

     public String getDisplayName() {        return _userid;    }    
    public int getTransactionId() {        return _id;    }  public void setTransactionId(int i){}
    public String getSession() {        return _session;    } 
    public double getAmount() {        return _amount;    } 
    public ModuleType getModule() {        return _module;    } 
    public double getFinalPlayChips() {        return _playChips;    } 
    public double getFinalRealChips() {        return _realChips;    } 
    public double getFinalPoints() {        return _points;    } 
    public TransactionType getTransactionType(){  return _type;   }
    public String getType(){  return _type.toString();   }
    public String getComments() { return _comment; }
    public String getTs() { 
              SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                return sdf.format(_ts);
    }    
    public String getStatus() {	return _status;	}
	public void setStatus(String status) {	_status = status;	}
	public String getAmountString() {        return SharedConstants.doubleToString(_amount);    } 
    public String getFinalPlayChipsString() {        return SharedConstants.doubleToString(_playChips);    } 
    public String getFinalRealChipsString() {        return SharedConstants.doubleToString(_realChips);    } 
    public String getFinalPointsString() {        return SharedConstants.doubleToString(_points);    } 
    public String getCurrency() {	return _currency;	}
	public void setCurrency(String currency) {	_currency = currency;	}
public DBBonusTransaction() {
  }
  
  public static int addTransaction(DBPlayer p, String session, ModuleType mod, double amt, double play, double real, double points, long trans_type, String comment) throws DBException  {
	  DBBonusTransaction dbt = new DBBonusTransaction();
      dbt._type = new TransactionType(trans_type);
      dbt._userid = p.getDisplayName();
      dbt._affiliateid = p.getAffiliate();
      dbt._session = session;
      dbt._bonus_code = p.getBonusCode();
      if (mod==null) mod=new ModuleType(ModuleType.LOBBY);
      dbt._module = mod;
      dbt._currency = CURRENCY_USD;
      dbt._comment = comment;
      dbt._amount = amt;
      dbt._playChips = play;
      dbt._realChips = real;
      dbt._points = points;
      return dbt.save();
  }
  
  

  private synchronized int save() throws DBException {
      PreparedStatement ps = null;
      Connection  conn = null;
      int r=-1;
     try {
           conn = ConnectionManager.getConnection("GameEngine");
           conn.setAutoCommit(true);
           StringBuilder sb = new StringBuilder("insert into ");
          sb.append(TRANSACTION_TABLE).append("( ");
          sb.append(TRANSACTION_TS).append(",");
          sb.append(TYPE).append(",");
          sb.append(STATUS).append(",");
          sb.append(DISPLAY_NAME).append(",");
          sb.append(SESSION).append(",");
          sb.append(AFFILIATE).append(",");
          sb.append(BONUS_CODE).append(",");
          sb.append(MODULE).append(",");
          sb.append(PLAY_CHIPS).append(",");
          sb.append(REAL_CHIPS).append(",");
          sb.append(POINTS).append(",");
          sb.append(AMOUNT).append(",");
          sb.append(CURRENCY).append(",");
          sb.append(COMMENT).append(")");
          sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          ps = conn.prepareStatement(sb.toString());
          ps.setTimestamp(1,
                          _ts= new java.sql.Timestamp(System.currentTimeMillis()));
          ps.setLong(2, _type.intVal);
          ps.setString(3, _status);
          ps.setString(4, _userid);
          ps.setString(5, _session);
          ps.setString(6, _affiliateid);
          ps.setString(7, _bonus_code);
          ps.setInt(8, _module.intVal());
          ps.setDouble(9, _playChips);
          ps.setDouble(10, _realChips);
          ps.setDouble(11, _points);
          ps.setDouble(12, _amount);
          ps.setString(13, _currency);
          ps.setString(14, _comment);
          //_cat.finest(this.toString());
          r = ps.executeUpdate();
          ps.close();
          conn.close();
      } catch (SQLException e) {
          _cat.log( Level.SEVERE,"Unable to update Bonus Transaction " + 
                     e.getMessage(), e);
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
                                " -- whilerefreshing bankroll");
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
      return r;
  }
  

    public static synchronized DBBonusTransaction[] getTransaction(String user) {
        Connection conn = null;
        PreparedStatement ps = null;
        DBBonusTransaction sp = null;
        Vector v = new Vector();
        try {
           // new Exception().printStackTrace();
        	StringBuilder sb = new StringBuilder("select *");
            sb.append(" from ").append(TRANSACTION_TABLE).append(" where ");
            sb.append(DISPLAY_NAME).append("=? order by TRANSACTION_TIMESTAMP desc limit 200");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new DBBonusTransaction();
                sp._userid = user;
                sp._id = r.getInt(TRANSACTION_ID);
                sp._ts = r.getTimestamp(TRANSACTION_TS);
                sp._status = r.getString(STATUS);
                sp._type = new TransactionType(r.getLong(TYPE));
                sp._session =  r.getString(SESSION);
                sp._affiliateid = r.getString(AFFILIATE);
                sp._bonus_code = r.getString(BONUS_CODE);
                sp._module = new ModuleType( r.getInt(MODULE));
                sp._playChips = r.getDouble(PLAY_CHIPS);
                sp._realChips = r.getDouble(REAL_CHIPS);
                sp._points = r.getDouble(POINTS);
                sp._amount = r.getDouble(AMOUNT);
                sp._currency = r.getString(CURRENCY);
                sp._comment = r.getString(COMMENT);
                v.add(sp);
            }             
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log( Level.SEVERE,"Unable to get transaction " + e.getMessage(), e);
    
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
        return (DBBonusTransaction[])v.toArray(new DBBonusTransaction[v.size()]);
    }


        public static synchronized DBBonusTransaction[] getAllTransaction(int mt) {
            Connection conn = null;
            PreparedStatement ps = null;
            DBBonusTransaction sp = null;
            Vector v = new Vector();
            try {
               // new Exception().printStackTrace();
            	StringBuilder sb = new StringBuilder("select *");
                sb.append(" from ").append(TRANSACTION_TABLE).append(" where ");
                sb.append(MODULE).append("=? order by TRANSACTION_TIMESTAMP desc limit 200");
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setInt(1, mt);
                _cat.finest(sb.toString());
                ResultSet r = ps.executeQuery();
                while (r.next()) {
                    sp = new DBBonusTransaction();
                    sp._userid = r.getString(DISPLAY_NAME);
                    sp._id = r.getInt(TRANSACTION_ID);
                    sp._ts = r.getTimestamp(TRANSACTION_TS);
                    sp._status = r.getString(STATUS);
                    sp._type = new TransactionType(r.getInt(TYPE));
                    sp._session =  r.getString(SESSION);
                    sp._affiliateid = r.getString(AFFILIATE);
                    sp._bonus_code = r.getString(BONUS_CODE);
                    sp._module = new ModuleType( r.getInt(MODULE));
                    sp._playChips = r.getDouble(PLAY_CHIPS);
                    sp._realChips = r.getDouble(REAL_CHIPS);
                    sp._points = r.getDouble(POINTS);
                    sp._amount = r.getDouble(AMOUNT);
                    sp._currency = r.getString(CURRENCY);
                    sp._comment = r.getString(COMMENT);
                    v.add(sp);
                }             
                r.close();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                _cat.log( Level.SEVERE,"Unable to get transaction " + e.getMessage(), e);
        
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
            return (DBBonusTransaction[])v.toArray(new DBBonusTransaction[v.size()]);
        }

  public String toString() {
	  StringBuilder str = new StringBuilder();
    str.append("WalletTransaction: " + _id + "\n");
    str.append("DisplayName = ").append(_userid).append("\n");
    str.append("Play chips = ").append(_playChips).append("\n");
    str.append("Real Chips = ").append(_realChips).append("\n");
    str.append("Points = ").append(_points).append("\n");
    str.append("Amount = ").append(_amount).append("\n");
    str.append("Operation = ").append(_type).append("\n");
    str.append("Session = ").append(_session).append("\n");
    str.append("Affiliate = ").append(_affiliateid).append("\n");
    str.append("BonusCode = ").append(_bonus_code).append("\n");
    str.append("Module = ").append(_module).append("\n");
    str.append("Status = ").append(_status).append("\n");
    str.append("TimeStamp = ").append(_ts).append("\n");
    str.append("Comment = ").append(_comment).append("\n");
    return (str.toString());
  }

  

}
