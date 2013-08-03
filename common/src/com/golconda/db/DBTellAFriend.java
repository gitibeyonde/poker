package com.golconda.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.ConnectionManager;

public class DBTellAFriend {
    // set the category for logging
    static Logger _cat = Logger.getLogger(DBFriend.class.getName());

    public static String MODULE_LOBBY="LOBBY";
    public static String MODULE_CARS="CARS";
/*
 * `TO_NAME` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `TO_MAIL` varchar(30) CHARACTER SET utf8 NOT NULL,
  `STATUS` varchar(15) CHARACTER SET utf8 DEFAULT NULL,
  `TIME_STAMP` datetime DEFAULT NULL,
  `FROM_USERID_FK` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `FROM_NAME` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `FROM_EMAIL` varchar(30) CHARACTER SET utf8 DEFAULT NULL,
 */
    String _to_name;
    String _to_mail;
    String _status="NONE"; 
    String _from_userid;
    String _from_name;
    String _from_mail;
    java.sql.Timestamp _addedTs; 
    DBPlayer _dbPlayer=null;
    
    
    

	static String TO_NAME ;    
    static String TO_MAIL ;    
    static String STATUS;
    static String TIME_STAMP; 
    static String FROM_USERID;    
    static String FROM_NAME;    
    static String FROM_EMAIL;
    
    static {
    	TO_NAME = "TO_NAME";
    	TO_MAIL = "TO_MAIL";
    	STATUS = "STATUS";
    	TIME_STAMP = "TIME_STAMP";
    	FROM_USERID = "FROM_USERID_FK";
    	FROM_NAME = "FROM_NAME";
    	FROM_EMAIL = "FROM_EMAIL";
    }

    public String getToname() {	return _to_name;	}
	public void setToname(String toName) {	_to_name = toName;	}
	public String getTomail() {	return _to_mail;	}
	public void setTomail(String toMail) {	_to_mail = toMail;}
	public String getStatus() {	return _status;	}
	public void setStatus(String status) {	_status = status;	}
	public String getFromuserid() {	return _from_userid;	}
	public void setFromuserid(String fromUserid) {	_from_userid = fromUserid;	}
	public String getFromname() {	return _from_name;}
	public void setFromname(String fromName) {	_from_name = fromName;	}
	public String getFrommail() {	return _from_mail;}
	public void setFrommail(String fromMail) {	_from_mail = fromMail;	}
	public java.sql.Timestamp getAddedTs() {	return _addedTs;}
	public void setAddedTs(java.sql.Timestamp addedTs) {	_addedTs = addedTs;	}    
    
    public DBTellAFriend() {
    }
    
    
    public int save() throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
            try {
                StringBuilder sb = new StringBuilder("replace into T_TELL_A_FRIEND (");
                sb.append(TO_NAME).append(",");
                sb.append(TO_MAIL).append(",");
                sb.append(STATUS).append(",");
                sb.append(TIME_STAMP).append(",");
                sb.append(FROM_USERID).append(",");
                sb.append(FROM_NAME).append(",");
                sb.append(FROM_EMAIL).append(")");
                sb.append(" values (");
                sb.append(" ?, ?, ?, ?, ?, ?, ?)");
                _cat.finest(sb.toString());
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, _to_name);
                ps.setString(2, _to_mail);
                ps.setString(3, _status);
                ps.setTimestamp(4, 
                        new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(5, _from_userid);
                ps.setString(6, _from_name);
                ps.setString(7, _from_mail);
                
                _cat.finest(this.toString());
                r = ps.executeUpdate();
                ps.close();
                
            } catch (SQLException e) {
                e.printStackTrace();
                _cat.severe(e.getMessage() + 
                           this);

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
                throw new DBException(e.getMessage());
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
    
    public int updateStatus() throws DBException{
    	Connection conn = null;
        PreparedStatement ps = null;
    	int res = -1;
    	try{
    	StringBuilder sb = new StringBuilder("update T_TELL_A_FRIEND set ");
        sb.append(STATUS).append(" = ?, ");
        sb.append(TIME_STAMP).append(" = ? ");
        sb.append(" where ");
        sb.append(TO_NAME).append("= ?");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, _status);
        ps.setTimestamp(2, _addedTs = new java.sql.Timestamp(System.currentTimeMillis()));
        ps.setString(3, _to_name);
        res = ps.executeUpdate();
        ps.close();
        conn.close();
        _cat.finest(res + " Update STATUS =" + _status);
    } catch (SQLException e) {
        _cat.log( Level.SEVERE,"Unable to update Player's POINTS " + 
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
    	return res;
    }
    
   
    
   
    
    public static DBTellAFriend[] getFriendsList(String dn){  
        Connection conn = null;
        PreparedStatement ps = null;  
        Vector v = new Vector();
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder buf = new StringBuilder("select friend_name, friend_type, module, status, added_ts from T_Tell_A_FRIEND where userid_fk like binary '" + dn + "' and added=1");
            ps = conn.prepareStatement(buf.toString());
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()){
                DBFriend dbp = new DBFriend();
                dbp.setDisplayName(dn);
                dbp.setFriendName(rs2.getString(1));
                dbp.setType(rs2.getString(2));
                dbp.setModule(rs2.getString(3));
                dbp.setStatus(rs2.getString(4));
                dbp.setAddedTs(rs2.getTimestamp(5));
                v.add(dbp);
            }
            rs2.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log( Level.SEVERE,"Unable to get player " + e.getMessage(), e);

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
        return (DBTellAFriend[])v.toArray(new DBTellAFriend[v.size()]); 
    }
    
        public DBPlayer getDBPlayer(){
            if (_dbPlayer == null ){
                _dbPlayer = new DBPlayer();
                try {
                    _dbPlayer.get(_to_name);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return _dbPlayer;
        }
        
    
            public boolean delete() throws DBException {
                    Connection conn = null;
                    PreparedStatement ps = null;
                    try {
                        StringBuilder sb = new StringBuilder("delete ");
                        sb.append(" from T_Tell_A_FRIEND where ");
                        sb.append(FROM_NAME).append("=? and ");
                        sb.append(TO_NAME).append("=?");
                        conn = ConnectionManager.getConnection("GameEngine");
                        ps = conn.prepareStatement(sb.toString());
                        ps.setString(1,_from_name);
                        ps.setString(2, _to_name);
                        boolean r = ps.execute();
                        ps.close();
                        conn.close();
                        return r;
                    }
                    catch (SQLException e) {
                      _cat.severe("Error in deleting view " + e.getMessage());
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
                      throw new DBException(e.getMessage() + " -- while deleting view");
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
                  
            public static DBTellAFriend[] getFriendsList() throws DBException{  
            	 Connection conn = null;
                 PreparedStatement ps = null;  
                 Vector v = new Vector();
                 
                     ResultSet rs2;
					try {
						conn = ConnectionManager.getConnection("GameEngine");
						 StringBuilder buf = new StringBuilder("select ");
						 buf.append("TO_NAME, TO_MAIL, STATUS, TIME_STAMP, FROM_USERID_FK, FROM_NAME, FROM_EMAIL ");
						 buf.append("from T_TELL_A_FRIEND");
						 ps = conn.prepareStatement(buf.toString());
						 rs2 = ps.executeQuery();
						 while (rs2.next()){
							 DBTellAFriend dbp = new DBTellAFriend();
						     dbp.setToname(rs2.getString(1));
						     dbp.setTomail(rs2.getString(2));
						     dbp.setStatus(rs2.getString(3));
						     dbp.setAddedTs(rs2.getTimestamp(4));
						     dbp.setFromuserid(rs2.getString(5));
						     dbp.setFromname(rs2.getString(6));
						     dbp.setFrommail(rs2.getString(7));
						     v.add(dbp);
						 }
						 rs2.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
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
                return (DBTellAFriend[])v.toArray(new DBTellAFriend[v.size()]); 
            }
        
             
}


