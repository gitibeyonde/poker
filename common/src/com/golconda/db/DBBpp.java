package com.golconda.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.ConnectionManager;

public class DBBpp {
	 static Logger _cat = Logger.getLogger(DBBpp.class.getName());
/*
 * `USERID_FK` varchar(36) NOT NULL,
  `CUMULATIVE_BPP` int(11) NOT NULL,
  `CURRENT_BPP` int(11) NOT NULL,
  `LAST_UPDATE_TS` timestamp 
 */
	public static final String USERID;
	public static final String CUMULATIVE_BPP;
	public static final String CURRENT_BPP;
	public static final String LAST_UPDATE_TS;
	public static final String BPP_TABLE = "T_BPP";
	
	static{
		USERID = "USERID_FK";
		CUMULATIVE_BPP ="CUMULATIVE_BPP";
		CURRENT_BPP ="CURRENT_BPP";
		LAST_UPDATE_TS = "LAST_UPDATE_TS";
	}
	
	public String userid;
	public double cumu_bpp;
	public double cur_bpp;
	public Timestamp last_update_ts;
	
	public String getUserid() {	return userid;	}
	public void setUserid(String userid) {	this.userid = userid;	}
	
	public double getCumu_bpp() {	return cumu_bpp;	}
	public void setCumu_bpp(double cumuBpp) {	cumu_bpp = cumuBpp;	}
	
	public double getCur_bpp() {	return cur_bpp;	}
	public void setCur_bpp(double curBpp) {	cur_bpp = curBpp;	}
	
	public Timestamp getLastUpdateTs() {	 return last_update_ts;	}
	public void getLastUpdateTs(Timestamp ts) { this.last_update_ts = ts;	}
	
	public String getLastUpdateTsString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(last_update_ts);
	}
	 
	public static synchronized DBBpp get(String uid) throws DBException {
		 Connection conn = null;
         PreparedStatement ps = null;
         DBBpp db = null;
         try {
             StringBuilder sb = new StringBuilder("select ");
             sb.append(USERID).append(", ");
             sb.append(CUMULATIVE_BPP).append(", ");
             sb.append(CURRENT_BPP).append(", ");
             sb.append(LAST_UPDATE_TS);
             sb.append(" from ").append(BPP_TABLE);
             sb.append(" where ");
             sb.append(USERID).append("=?");
             conn = ConnectionManager.getConnection("GameEngine");
             ps = conn.prepareStatement(sb.toString());
             ps.setString(1, uid);
             _cat.finest(sb.toString()+"  "+ uid);
             ResultSet r = ps.executeQuery();
             while (r.next()) {
                 db = new DBBpp();
                 db.userid = r.getString(USERID);
                 db.cumu_bpp = r.getDouble(CUMULATIVE_BPP);
                 db.cur_bpp= r.getDouble(CURRENT_BPP);
                 db.last_update_ts = r.getTimestamp(LAST_UPDATE_TS);
             }            
             r.close(); 
             ps.close();
             
             conn.close();
         } catch (SQLException e) {
             _cat.log( Level.SEVERE,"Unable to get DBBpp details " + e.getMessage(), e);

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
             throw new DBException(e.getMessage() + 
                                   " -- while retriving DBBpp details");
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
       //  DBBpp[] va = (DBBpp[])v.toArray(new DBBpp[v.size()]);
         /* java.util.Arrays.sort(va, new Comparator<DBBonus>() {
                public int compare(DBBonus o1, DBBonus o2) {
                     return o1._order - o2._order;
                }
              });*/
         return db;
         } 
	public static synchronized DBBpp[] getAllBPP(String pattern) throws DBException {
		 Connection conn = null;
        PreparedStatement ps = null;
        Vector v = new Vector();
        DBBpp db = null;
        try {
        	StringBuilder sb = new StringBuilder("select ");
            sb.append(USERID).append(", ");
            sb.append(CUMULATIVE_BPP).append(", ");
            sb.append(CURRENT_BPP).append(", ");
            sb.append(LAST_UPDATE_TS);
            sb.append(" from ").append(BPP_TABLE);
            sb.append(" where ");
            sb.append(USERID).append(" like '%"+pattern+"%'");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                db = new DBBpp();
                db.userid = r.getString(USERID);
                db.cumu_bpp = r.getDouble(CUMULATIVE_BPP);
                db.cur_bpp= r.getDouble(CURRENT_BPP);
                db.last_update_ts = r.getTimestamp(LAST_UPDATE_TS);
                v.add(db);
            }            
            r.close(); 
            ps.close();
            
            conn.close();
        } catch (SQLException e) {
            _cat.log( Level.SEVERE,"Unable to get DBBpp details " + e.getMessage(), e);

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
            throw new DBException(e.getMessage() + 
                                  " -- while retriving DBBpp details");
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
        return (DBBpp[])v.toArray(new DBBpp[v.size()]);
        
	}
	
	 public synchronized int save() throws DBException {
		 PreparedStatement ps = null;
         Connection  conn = null;
         int r = -1;
        try {
              conn = ConnectionManager.getConnection("GameEngine");
              conn.setAutoCommit(true);
              StringBuilder sb = new StringBuilder("replace into T_BPP");
              sb.append("(");
              sb.append(USERID).append(", ");
              sb.append(CUMULATIVE_BPP).append(", ");
              sb.append(CURRENT_BPP).append(", ");
              sb.append(LAST_UPDATE_TS).append(")");
              sb.append(" values ( ?, ?, ?, ?)");
             ps = conn.prepareStatement(sb.toString());
             ps.setString(1, userid);
             ps.setDouble(2, cumu_bpp);
             ps.setDouble(3, cur_bpp);
             ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
             r = ps.executeUpdate();
             _cat.info(sb.toString());
             ps.close();
             conn.close();
         } catch (SQLException e) {
             _cat.log( Level.SEVERE,"Unable to save T_Bpp " + 
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
                                   " -- T_Bpp");
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
    public synchronized int update() throws DBException {
		 PreparedStatement ps = null;
         Connection  conn = null;
         int r = -1;
        try {
              conn = ConnectionManager.getConnection("GameEngine");
              conn.setAutoCommit(true);
              StringBuilder sb = new StringBuilder("update T_BPP set ");
              sb.append(CUMULATIVE_BPP).append(" = ?, ");
              sb.append(CURRENT_BPP).append(" = ?, ");
              sb.append(LAST_UPDATE_TS).append(" = ? ");
              sb.append(" where ");
              sb.append(USERID).append("= ?");
             ps = conn.prepareStatement(sb.toString());
             ps.setDouble(1, cumu_bpp);
             ps.setDouble(2, cur_bpp);
             ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
             ps.setString(4, userid);
             r = ps.executeUpdate();
             _cat.info(sb.toString());
             ps.close();
             conn.close();
         } catch (SQLException e) {
             _cat.log( Level.SEVERE,"Unable to update T_Bpp " + 
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
                                   " -- T_Bpp");
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
	 
	 //*****this method is not using right now.*****//
	 public static synchronized int resetBPP(String userid) throws DBException {
		 PreparedStatement ps = null;
         Connection  conn = null;
         int r = -1;
        try {
              conn = ConnectionManager.getConnection("GameEngine");
              conn.setAutoCommit(true);
              StringBuilder sb = new StringBuilder("update T_BPP set ");
              sb.append(CUMULATIVE_BPP).append(" = ?, ");
              sb.append(CURRENT_BPP).append(" = ?, ");
              sb.append(LAST_UPDATE_TS).append(" = ? ");
              sb.append(" where ");
              sb.append(USERID).append("= ?");
             ps = conn.prepareStatement(sb.toString());
             ps.setDouble(1, 0);
             ps.setDouble(2, 0);
             ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
             ps.setString(4, userid);
             r = ps.executeUpdate();
             _cat.info(sb.toString());
             ps.close();
             conn.close();
         } catch (SQLException e) {
             _cat.log( Level.SEVERE,"Unable to reset T_Bpp " + 
                        e.getMessage(), e);

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
                                   " -- T_Bpp");
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
}
	 
