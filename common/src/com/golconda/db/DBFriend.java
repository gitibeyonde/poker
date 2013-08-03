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

public class DBFriend {
    // set the category for logging
    static Logger _cat = Logger.getLogger(DBFriend.class.getName());

    public static String MODULE_LOBBY="LOBBY";
    public static String MODULE_CARS="CARS";

    String _displayName;
    String _friendName;
    String _type="GENERAL";    
    String _status="NONE";     
    String _module=MODULE_LOBBY; 
    java.sql.Timestamp _addedTs; 
    boolean _added;
    
    DBPlayer _dbPlayer=null;
    
    static String USERID_FK ;    
    static String FRIEND_NAME ;    
    static String FRIEND_TYPE;    
    static String STATUS;      
    static String MODULE;    
    static String ADDED_TS;    
    static String ADDED;
    
    static {
        USERID_FK  = "USERID_FK";
        FRIEND_NAME  = "FRIEND_NAME";
        FRIEND_TYPE  = "FRIEND_TYPE";
        STATUS   = "STATUS";
        MODULE   = "MODULE";
        ADDED_TS  = "ADDED_TS";
        ADDED = "ADDED";
    }
        
    public DBFriend() {
    }
    
    public void setDisplayName(String v) {        _displayName = v;    }    public String getDisplayName() {        return _displayName;    }
    public void setFriendName(String fid){_friendName=fid;}public String getFriendName(){return _friendName;}
    public void setType(String fid){_type=fid;}public String getType(){return _type;}
    public void setStatus(String fid){_status=fid;}public String getStatus(){return _status;}
    public void setModule(String fid){_module=fid;}public String getModule(){return _module;}
    public void setAdded(){ _added = true; } public void unsetAdded(){ _added = false; } public void setAdded(boolean b){ _added = b; }
    public Timestamp getAddedTs() {        return _addedTs;    }     public void setAddedTs(Timestamp t) {   _addedTs = t;    } 
    
    public int save() throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
            try {
            	StringBuilder sb = new StringBuilder("replace into T_FRIENDS (");
                sb.append(USERID_FK).append(",");
                sb.append(FRIEND_NAME).append(",");
                sb.append(FRIEND_TYPE).append(",");
                sb.append(STATUS).append(",");
                sb.append(MODULE).append(",");
                sb.append(ADDED_TS).append(",");
                sb.append(ADDED).append(")");
                sb.append(" values (");
                sb.append(" ?, ?, ?, ?, ?, ?, ?)");
                _cat.finest(sb.toString());
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, _displayName);
                ps.setString(2, _friendName);
                ps.setString(3, _type);
                ps.setString(4, _status);
                ps.setString(5, _module);
                ps.setTimestamp(6, 
                                new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setBoolean(7, _added);
                
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
    
    
    public static boolean createAddRequest(String displayName, String frndName, String mod){
        DBFriend dbf = new DBFriend();
        dbf.setDisplayName(displayName);
        dbf.setFriendName(frndName);
        dbf.setModule(mod);
        dbf.unsetAdded();
        try {
            dbf.save();
        }
        catch (DBException e){
            e.printStackTrace();
            return false;
        }        
        
        return true;
    }
    
    public static boolean confirmFriend(String displayName, String frndName, ModuleType mt){
        DBFriend dbf = new DBFriend();
        dbf.setDisplayName(displayName);
        dbf.setFriendName(frndName);
        dbf.setModule(mt.toString());
        dbf.setAdded();
        try {
            dbf.save();
        }
        catch (DBException e){
            e.printStackTrace();
            return false;
        }
        DBFriend dbf2 = new DBFriend();
        dbf2.setDisplayName(frndName);
        dbf2.setFriendName(displayName);
        dbf2.setModule(mt.toString());
        dbf2.setAdded();
        try {
            dbf2.save();
        }
        catch (DBException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    public static boolean removeFriend(String displayName, String frndName, ModuleType mt){
        DBFriend dbf = new DBFriend();
        dbf.setDisplayName(displayName);
        dbf.setFriendName(frndName);
        dbf.setModule(mt.toString());
        try {
            dbf.delete();
        }
        catch (DBException e){
            e.printStackTrace();
            return false;
        }
        DBFriend dbf2 = new DBFriend();
        dbf2.setDisplayName(frndName);
        dbf2.setFriendName(displayName);
        dbf2.setModule(mt.toString());
        try {
            dbf2.delete();
        }
        catch (DBException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static DBFriend[] getFriendsList(String dn){  
        Connection conn = null;
        PreparedStatement ps = null;  
        Vector v = new Vector();
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder buf = new StringBuilder("select friend_name, friend_type, module, status, added_ts from T_FRIENDS where userid_fk like binary '" + dn + "' and added=1");
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
        return (DBFriend[])v.toArray(new DBFriend[v.size()]); 
    }
    
    public static DBFriend[] getFriendsList(String dn, String module){  
        Connection conn = null;
        PreparedStatement ps = null;  
        Vector v = new Vector();
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder buf = new StringBuilder("select friend_name, friend_type, module, status, added_ts from T_FRIENDS where userid_fk like binary '" + dn + "' and added=1  and module='" + module +"'");
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
        return (DBFriend[])v.toArray(new DBFriend[v.size()]); 
    }
    
    
    public static DBFriend[] getFriendsAddRequest(String fn, ModuleType mt){  
        Connection conn = null;
        PreparedStatement ps = null;  
        Vector v = new Vector();
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder buf = new StringBuilder("select userid_fk, friend_type, module, status from T_FRIENDS where friend_name like binary '" + fn + "' and added=0 and module='" + mt.toString() +"'");
            ps = conn.prepareStatement(buf.toString());
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()){
                DBFriend dbp = new DBFriend();
                dbp.setFriendName(fn);
                dbp.setDisplayName(rs2.getString(1));
                dbp.setType(rs2.getString(2));
                dbp.setModule(rs2.getString(3));
                dbp.setStatus(rs2.getString(4));
                dbp._dbPlayer = new DBPlayer();
                dbp._dbPlayer.get(dbp.getDisplayName());
                v.add(dbp);
            }
            rs2.close();
            ps.close();
            conn.close();
        } catch (DBException e) {
            _cat.log( Level.SEVERE,"Unable to get player " + e.getMessage(), e);
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
        return (DBFriend[])v.toArray(new DBFriend[v.size()]); 
    }
    
        
    
    public static DBFriend[] getFriendsAddRequestFromGames(String fn){  
        Connection conn = null;
        PreparedStatement ps = null;  
        Vector v = new Vector();
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder buf = new StringBuilder("select userid_fk, friend_type, module, status from T_FRIENDS where friend_name like binary '" + fn + "' and added=0 and module!='lobby' and module!='webcam'");
            ps = conn.prepareStatement(buf.toString());
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()){
                DBFriend dbp = new DBFriend();
                dbp.setFriendName(fn);
                dbp.setDisplayName(rs2.getString(1));
                dbp.setType(rs2.getString(2));
                dbp.setModule(rs2.getString(3));
                dbp.setStatus(rs2.getString(4));
                dbp._dbPlayer = new DBPlayer();
                dbp._dbPlayer.get(dbp.getDisplayName());
                v.add(dbp);
            }
            rs2.close();
            ps.close();
            conn.close();
        } catch (DBException e) {
            _cat.log( Level.SEVERE,"Unable to get player " + e.getMessage(), e);
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
        return (DBFriend[])v.toArray(new DBFriend[v.size()]); 
    }
    
        
        
        public DBPlayer getDBPlayer(){
            if (_dbPlayer == null ){
                _dbPlayer = new DBPlayer();
                try {
                    _dbPlayer.get(_friendName);
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
                        sb.append(" from T_FRIENDS where ");
                        sb.append(USERID_FK).append("=? and ");
                        sb.append(FRIEND_NAME).append("=?");
                        conn = ConnectionManager.getConnection("GameEngine");
                        ps = conn.prepareStatement(sb.toString());
                        ps.setString(1, _displayName);
                        ps.setString(2, _friendName);
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
                  
        
        
             
}


