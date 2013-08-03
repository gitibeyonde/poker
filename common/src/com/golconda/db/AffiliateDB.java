package com.golconda.db;

import com.agneya.util.ConnectionManager;
import com.agneya.util.FormattingUtils;

import com.golconda.db.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.logging.Logger;

public class AffiliateDB {
	// set the category for logging
    static Logger _cat = Logger.getLogger(AffiliateDB.class.getName());

    public String _uid;
    public String _offer; //CPA | REVSHARE
    public String _status;
    public String _url;
    public String _email;
    public String _comment;
    public byte[] _logo;
    public String _referId;
    public String _heard_from;
    public String _heard_from_others;
    public Timestamp _create_ts;
    public double _max_loss;
    public double _max_win;
    public String _locale;
    public double _abs_comm;
    public double _abs_comm_paidout;
    public double _comm;
    public double _comm_paidout;
    public int _level;
    public int _children_count;
    public String _type;
    public double _credit_available;
    public double _max_credit;
    public double _min_credit;
    public String _parent;

    public static final String AFFILIATE_TYPE_WEB="Website";
    public static final String AFFILIATE_TYPE_EMAIL="Email";
    public static final String AFFILIATE_TYPE_ONLINE="Online";
    public static final String AFFILIATE_TYPE_OFFLINE="Offline";
    public static final String AFFILIATE_TYPE_ALL="All";
    public static final String AFFILIATE_TYPE_CREDIT="Credit";
    public static final String AFFILIATE_WEBAPPS="WebApps";
    
    public static final String STATUS_APPLIED="APPLIED";    
    public static final String STATUS_REVIEWED="REVIEWED";  
    public static final String STATUS_APPROVED="APPROVED";  
    public static final String STATUS_REJECTED="REJECTED";
    
    public static final String OFFER_CPA="CPA";  
    public static final String OFFER_MGR="MGR";
    public static final String OFFER_WEBAPPS="WebApps";
    
    final String USERID_FK="USERID_FK";      
    final String OFFER="OFFER";    
    final String STATUS="AFF_STATUS";  
    final String URL="URL";  
    final String EMAIL="EMAIL";
    final String COMMENT="COMMENT";
    final String LOGO="LOGO";
    final String REFERRER_ID_FK="REFERRER_ID_FK";
    final String HEARD_FROM_FK="HEARD_FROM_FK";
    final String HEARD_FROM_OTHER="HEARD_FROM_OTHER";
    final String REG_OTHER_AFF_PROG="REG_OTHER_AFF_PROG";
    final String CREATE_TS="CREATE_TS";
    final String MAX_LOSS="MAX_LOSS";
    final String MAX_WIN="MAX_WIN";
    final String LOCALE="LOCALE";
    final String TYPE="AFF_TYPE";
    final String CREDIT_AVAILABLE="CREDIT_AVAILABLE";
    final String MAX_CREDIT="MAX_CREDIT";
    final String MIN_CREDIT="MIN_CREDIT";
    final String ABS_COMM="ABS_COMM";
    final String ABS_COMM_PAIDOUT="ABS_COMM_PAIDOUT";
    final String COMM="COMM";
    final String COMM_PAIDOUT="COMM_PAIDOUT";
    final String LEVEL="AFF_LEVEL";
    final String CHILDREN_COUNT="CHILDREN_COUNT";
    final String PARENT="PARENT";


    public String getId(){ return _uid; } public void setId(String t){ _uid = t; }
    public String getOffer(){ return _offer; } public void setOffer(String t){ _offer = t; }
    public String getStatus(){ return _status; } public void setStatus(String t){ _status = t; }
    public String getUrl(){ return _url; } public void setUrl(String t){ _url = t; }
    public String getEmail(){ return _email; } public void setEmail(String t){ _email = t; }
    public String getComment(){ return _comment; } public void setComment(String t){ _comment = t; }
    public byte[] getLogo(){        return _logo;    }
          public void setLogo(byte[] str){        
           if (str != null) _logo = str;    
          }
    public String getReferId(){ return _referId; } public void setReferId(String t){ _referId = t; }
    public String getHeardFrom(){ return _heard_from; } public void setHeardFrom(String t){ _heard_from = t; }
    public String getHeardFromOthers(){ return _heard_from_others; } public void setHeardFromOthers(String t){ _heard_from_others = t; }
    public Timestamp getCreateTs(){        return _create_ts;    }       public void setCreateTs(Timestamp str){        _create_ts = str;    }
    public double getMaxLoss() { return _max_loss; } public void setMaxLoss(double m){ _max_loss = m; }
    public double getMaxWin() { return _max_win; } public void setMaxWin(double m){ _max_win = m; }
    public double getAbsComm() { return _abs_comm; } public void setAbsComm(double m){ _abs_comm = m; }
    public double getComm() { return _comm; } public void setComm(double m){ _comm = m; }
    public double getAbsCommPaidout() { return _abs_comm_paidout; } public void setAbsCommPaidout(double m){ _abs_comm_paidout = m; }
    public String getLocale(){ return _locale; } public void setLocale(String t){ _locale = t; }
    public double getCreditAvail() { return _credit_available; } public void setCreditAvail(double m){ _credit_available = m; }
    public double getMaxCredit() { return _max_credit; } public void setMaxCredit(double m){ _max_credit = m; }
    public double getMinCredit() { return _min_credit; } public void setMinCredit(double m){ _min_credit = m; }
    public String getParent(){ return _parent; } public void setParent(String t){ _parent = t; }
    public String getType(){ return _type; } public void setType(String t){ _type = t; }
    public int getLevel(){ return _level; } public void setLevel(int t){ _level = t; }
    public int getChildCount(){ return _children_count; } public void setChildCount(int t){ _children_count = t; }


    public AffiliateDB(){
        
    }

    public AffiliateDB(String id)  throws DBException  {
        get(id);
    }
    
    
    public void get(String uid) throws DBException {
      Connection conn = null;
      PreparedStatement ps = null;
      try {
    	  StringBuilder sb = new StringBuilder("select ");
        sb.append(USERID_FK).append(" ,");
        sb.append(OFFER).append(" ,");
        sb.append(STATUS).append(" ,");
        sb.append(URL).append(" ,");
        sb.append(EMAIL).append(" ,");
        sb.append(COMMENT).append(" ,");
        sb.append(LOGO).append(" ,");
        sb.append(REFERRER_ID_FK).append(" ,");
        sb.append(HEARD_FROM_FK).append(" ,");
        sb.append(HEARD_FROM_OTHER).append(" ,");
        sb.append(REG_OTHER_AFF_PROG).append(" ,");
        sb.append(CREATE_TS).append(" ,");
        sb.append(MAX_LOSS).append(" ,");
        sb.append(MAX_WIN).append(" ,");
        sb.append(LOCALE).append(" ,");
        sb.append(TYPE).append(" ,");
        sb.append(CREDIT_AVAILABLE).append(" ,");
        sb.append(MAX_CREDIT).append(" ,");
        sb.append(MIN_CREDIT).append(" ,");
        sb.append(ABS_COMM).append(" ,");
        sb.append(ABS_COMM_PAIDOUT).append(" ,");
        sb.append(COMM).append(" ,");
        sb.append(COMM_PAIDOUT).append(" ,");
        sb.append(LEVEL).append(" ,");
        sb.append(CHILDREN_COUNT).append(" ,");
        sb.append(PARENT);
        sb.append(" from T_AFFILIATE where USERID_FK= ?");
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, uid);
        _cat.fine(sb.toString());
        ResultSet r = ps.executeQuery();
        if (r.next()) {
            _uid = r.getString(USERID_FK);
            _offer =r.getString(OFFER);
            _status = r.getString(STATUS);
            _url = r.getString(URL);
            _email = r.getString(EMAIL);
            _comment = r.getString(COMMENT);
            _logo = r.getBytes(LOGO);
            _referId =r.getString(REFERRER_ID_FK);
            _heard_from =r.getString(HEARD_FROM_FK);
            _heard_from_others =r.getString(HEARD_FROM_OTHER);
            _create_ts =r.getTimestamp(CREATE_TS);
            _max_loss =r.getInt(MAX_LOSS);
            _max_win =r.getInt(MAX_WIN);
            _locale =r.getString(LOCALE);
            _type =r.getString(TYPE);
            _abs_comm =r.getDouble(ABS_COMM);
            _abs_comm_paidout =r.getDouble(ABS_COMM_PAIDOUT);
            _comm =r.getDouble(COMM);
            _comm_paidout =r.getDouble(COMM_PAIDOUT);
            _level =r.getInt(LEVEL);
            _children_count =r.getInt(CHILDREN_COUNT);
            _type =r.getString(TYPE);
            _credit_available =r.getDouble(CREDIT_AVAILABLE);
            _max_credit =r.getDouble(MAX_CREDIT);
            _min_credit =r.getDouble(MIN_CREDIT);
            _parent =r.getString(PARENT);
        }
          r.close();
          ps.close();
          conn.close();
      }
      catch (SQLException e) {
        _cat.severe("Error in getting blog "+ e.getMessage());
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
        throw new DBException(e.getMessage() + " -- while getting Affiliate");
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
       return save(null);
   }
    
    public int save(Connection conn) throws DBException {
     int r = -1;
     PreparedStatement ps = null;
    boolean autocommit = conn != null;

       try {
    	   StringBuilder sb = new StringBuilder("insert into T_AFFILIATE ( ");
         sb.append(USERID_FK).append(",");
         sb.append(OFFER).append(",");
         sb.append(STATUS).append(",");
         sb.append(URL).append(",");
         sb.append(EMAIL).append(",");
         sb.append(COMMENT).append(",");
         sb.append(LOGO).append(",");
         sb.append(REFERRER_ID_FK).append(",");
         sb.append(HEARD_FROM_FK).append(",");
         sb.append(HEARD_FROM_OTHER).append(",");
         //sb.append(REG_OTHER_AFF_PROG).append(",");
         sb.append(CREATE_TS).append(",");
         sb.append(MAX_LOSS).append(",");
         sb.append(MAX_WIN).append(",");
         sb.append(LOCALE).append(",");
         sb.append(TYPE).append(",");
         sb.append(CREDIT_AVAILABLE).append(",");
         sb.append(MAX_CREDIT).append(",");
         sb.append(MIN_CREDIT).append(",");
         sb.append(ABS_COMM).append(",");
         sb.append(ABS_COMM_PAIDOUT).append(",");
         sb.append(COMM).append(",");
         sb.append(COMM_PAIDOUT).append(",");
         sb.append(LEVEL).append(",");
         sb.append(CHILDREN_COUNT).append(",");
         sb.append(PARENT).append(")");
      
           sb.append(
               " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
         _cat.fine(sb.toString());
        conn = conn == null ? ConnectionManager.getConnection("GameEngine") : conn;
         ps = conn.prepareStatement(sb.toString());
         int i = 1;
         ps.setString(i++, _uid);
         ps.setString(i++, _offer);
         ps.setString(i++, _status);
         ps.setString(i++, _url);
         ps.setString(i++, _email);
         ps.setString(i++, _comment);
         ps.setBytes(i++, _logo);
         ps.setString(i++, _referId);
         ps.setString(i++, _heard_from);
         ps.setString(i++, _heard_from_others);
         ps.setTimestamp(i++,
                         new java.sql.Timestamp(System.currentTimeMillis()));
         ps.setDouble(i++, _max_loss);
         ps.setDouble(i++, _max_win);
         ps.setString(i++, _locale);
         ps.setString(i++, _type);
         ps.setDouble(i++, _credit_available);
         ps.setDouble(i++, _max_credit);
         ps.setDouble(i++, _max_credit);
         ps.setDouble(i++, _abs_comm);
         ps.setDouble(i++, _abs_comm_paidout);
         ps.setDouble(i++, _comm);
         ps.setDouble(i++, _comm_paidout);
         ps.setInt(i++, _level);
         ps.setInt(i++, _children_count);
         ps.setString(i++, _parent);
         
         r = ps.executeUpdate();
         // if it is private table update the player list too
         _cat.fine(this.toString());
         ps.close();
        if (autocommit)
            conn.close();
       }
       catch (SQLException e) {
         _cat.severe("Unable to save  AFFILIATE" + e.getMessage());

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
         throw new DBException(e.getMessage() + " -- while saving affiliate");
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


    
      public int update() throws DBException {
        int r = -1;
        Connection conn = null;
        PreparedStatement ps = null;

          try {
        	  StringBuilder sb = new StringBuilder("replace into T_AFFILIATE ( ");
                sb.append(USERID_FK).append(",");
                sb.append(OFFER).append(",");
                sb.append(STATUS).append(",");
                sb.append(URL).append(",");
                sb.append(EMAIL).append(",");
                sb.append(COMMENT).append(",");
                sb.append(LOGO).append(",");
                sb.append(REFERRER_ID_FK).append(",");
                sb.append(HEARD_FROM_FK).append(",");
                sb.append(HEARD_FROM_OTHER).append(",");
                //sb.append(REG_OTHER_AFF_PROG).append(",");
                sb.append(CREATE_TS).append(",");
                sb.append(MAX_LOSS).append(",");
                sb.append(MAX_WIN).append(",");
                sb.append(LOCALE).append(",");
                sb.append(TYPE).append(",");
                sb.append(CREDIT_AVAILABLE).append(",");
                sb.append(MAX_CREDIT).append(",");
                sb.append(MIN_CREDIT).append(",");
                sb.append(ABS_COMM).append(",");
                sb.append(ABS_COMM_PAIDOUT).append(",");
                sb.append(COMM).append(",");
                sb.append(COMM_PAIDOUT).append(",");
                sb.append(LEVEL).append(",");
                sb.append(CHILDREN_COUNT).append(",");
                sb.append(PARENT).append(")");
            
                 sb.append(
                     " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
               _cat.fine(sb.toString());
               conn = ConnectionManager.getConnection("GameEngine");
               ps = conn.prepareStatement(sb.toString());
               int i = 1;
                ps.setString(i++, _uid);
                ps.setString(i++, _offer);
                ps.setString(i++, _status);
                ps.setString(i++, _url);
                ps.setString(i++, _email);
                ps.setString(i++, _comment);
                ps.setBytes(i++, _logo);
                ps.setString(i++, _referId);
                ps.setString(i++, _heard_from);
                ps.setString(i++, _heard_from_others);
                ps.setTimestamp(i++,
                           new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setDouble(i++, _max_loss);
                ps.setDouble(i++, _max_win);
                ps.setString(i++, _locale);
                ps.setString(i++, _type);
                ps.setDouble(i++, _credit_available);
                ps.setDouble(i++, _max_credit);
                ps.setDouble(i++, _min_credit);
                ps.setDouble(i++, _abs_comm);
                ps.setDouble(i++, _abs_comm_paidout);
                ps.setDouble(i++, _comm);
                ps.setDouble(i++, _comm_paidout);
                ps.setInt(i++, _level);
                ps.setInt(i++, _children_count);
                ps.setString(i++, _parent);
            
            r = ps.executeUpdate();
            // if it is private table update the player list too
            _cat.fine(this.toString());
            conn.commit();
            ps.close();
            conn.close();
          }
          catch (SQLException e) {
            _cat.severe("Unable to save  BLOG" + e.getMessage());

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
        return r;
      }
      //not using right now
      public double getRake(String uid) throws DBException{
			 Connection conn = null;
			 PreparedStatement ps = null;
			 double rake=0;
			try{
				StringBuilder sb = new StringBuilder("SELECT SUM(RAKE)");
				 sb.append(" FROM T_PLAYER_PER_GRS P,T_USER U");
				 sb.append(" WHERE U.USERID=P.USER_ID_FK");
				 sb.append(" AND U.AFFILIATE_ID_FK=?");
			 	conn = ConnectionManager.getConnection("GameEngine");
		        ps = conn.prepareStatement(sb.toString());
		        ps.setString(1, uid);
		        _cat.fine(sb.toString());
		        ResultSet r = ps.executeQuery();
		        if (r.next()) {
		        	rake=r.getDouble(1);
		        	System.out.println("..rake.."+rake);
		        }
		        r.close();
		          ps.close();
		          conn.close();
		      }
		      catch (SQLException e) {
		        _cat.severe("Error in getting blog "+ e.getMessage());
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
		        throw new DBException(e.getMessage() + " -- while getting Affiliate");
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
		      return FormattingUtils.getRounded(rake);
			}
    
}
