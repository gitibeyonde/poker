package com.golconda.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;



import com.agneya.util.ConnectionManager;
import com.agneya.util.Rng;

public class DBAdminLog {
	 static Logger _cat = Logger.getLogger(DBAdminLog.class.getName());

	    static Rng _rng = new Rng();
	    protected  String sessionid;
	    protected  String userid;
	    protected  Timestamp ts;
	    protected  String description;
	    
	    public String getSessionid() {			return sessionid;		}

		public void setSessionid(String sessid) {	this.sessionid = sessid;		}

		public String getUserid() {			return userid;		}

		public void setUserid(String userid) {	this.userid = userid;		}

		public Timestamp getTs() {		return ts;		}

		public void setTs(Timestamp ts) {	this.ts = ts;		}

		public String getDescription() {		return description;		}

		public void setDescription(String des) {	this.description = des;		}

		public static final String SESSIONID;
	    public static final String USERID;
	    public static final String TIMESTAMP;
	    public static final String DESCRIPTION;
	    static {
	    	SESSIONID = "SESSION_ID";
	    	USERID = "USERID";
	    	TIMESTAMP = "TIME_STAMP";
	    	DESCRIPTION = "DESCRIPTION";
	    }
	    
	    DBAdminLog(String sid, String uid, Timestamp time, String desc){
	    	sessionid = sid;
	    	userid = uid;
	    	ts = time;
	    	description = desc;
	    }
	    public DBAdminLog() {
			// TODO Auto-generated constructor stub
		}
	    
	    public  void save(){
	    	Connection conn = null;
	        PreparedStatement ps = null;
	        Vector dbp = new Vector();
	        System.out.println(sessionid+","+userid+","+description);
	        /*FacesContext ctx = FacesContext.getCurrentInstance();
          	String sid = (String)ctx.getExternalContext().getSessionMap().get("mysessionid");
          	String uid = (String)ctx.getExternalContext().getSessionMap().get("displayName");
	        */int res = -1;
	        try {
	            conn = ConnectionManager.getConnection("GameEngine");
	            StringBuilder sb = new StringBuilder("insert into T_ADMINLOG(");
	            sb.append("SESSION_ID, USERID, TIME_STAMP, DESCRIPTION) ");
	            sb.append("values(?, ?, ?, ?)");
	            ps = conn.prepareStatement(sb.toString());
	            ps.setString(1, sessionid);
	            ps.setString(2, userid);
	            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
	            ps.setString(4, description);
	           res = ps.executeUpdate();
	            _cat.info(sb.toString());
	           
	            ps.close();
	            conn.close();
	        } catch (SQLException e) {
	            _cat.log(Level.SEVERE, 
	                     "Unable to save Admin log " + e.getMessage(), e);

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
	    }
	        
//	        public static void main(String a[]){
//	        	saveLog( "name=value");
//	        	
//	        }
	    
	    

}
