package com.golconda.db; 

import com.agneya.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBMessages {
        // set the category for logging
        static Logger _cat = Logger.getLogger(DBMessages.class.getName());
        public String _to_user, _from_user;
        public String _subject, _message;
        public int _id, _status;
        public Timestamp _ts;

        public static final String ID, TO_USER, FROM_USER;
        public static final String SUBJECT, MESSAGE;
        public static final String SEND_TS, STATUS;

        public static final String  MESSAGES_TABLE = "T_MESSAGES";

        static {
              ID = "ID";
              TO_USER = "TO_USERID";
              FROM_USER = "FROM_USERID";
              SUBJECT = "SUBJECT";
              MESSAGE = "MESSAGE";
              STATUS = "STATUS";
              SEND_TS = "SEND_TS";
        }  
        
        public int getId(){        return _id;    }  public void setId(int str){        _id = str;    }
        
        public String getToUserid(){        return _to_user;    }   public void setToUserid(String str){        _to_user = str;    }    
        public String getFromUserid(){        return _from_user;    }   public void setFromUserid(String str){        _from_user = str;    }    
        public int getStatus(){        return _status;    }  public void setStatus(int str){        _status = str;    }
        public String getSubject(){        return _subject;    }   public void setSubject(String str){        _subject = str;    }
        public String getMessage(){        return _message;    }   public void setMessage(String str){        _message = str;    }
       public Date getSentTs(){        return _ts;    } public void setSentTs(Timestamp str){        _ts = str;    }
       public String getSentTsString(){        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
              return sdf.format(_ts);  
        }
        
        
        
        public DBMessages() {
        }
        

          public synchronized int save() throws DBException {
              PreparedStatement ps = null;
              Connection  conn = null;
              int r = -1;
             try {
            //new Exception().printStackTrace();
                   conn = ConnectionManager.getConnection("GameEngine");
                   conn.setAutoCommit(true);
                   StringBuilder sb = new StringBuilder("replace into ");
                  sb.append(MESSAGES_TABLE).append("( ");
                  sb.append(TO_USER).append(",");
                  sb.append(FROM_USER).append(",");
                  sb.append(SUBJECT).append(",");
                  sb.append(MESSAGE).append(",");
                  sb.append(STATUS).append(",");
                  sb.append(SEND_TS).append(")");
                  sb.append(" values ( ?, ?, ?, ?, ?, ?)");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, _to_user);
                  ps.setString(2, _from_user);
                  ps.setString(3, _subject);
                  ps.setString(4, _message);
                  ps.setInt(5, _status);
                  ps.setTimestamp(6, _ts=new java.sql.Timestamp(System.currentTimeMillis()));
                  _cat.info(this.toString());
                  r = ps.executeUpdate();
                  ps.close();
                  
                  conn.close();
              } catch (SQLException e) {
                  _cat.log( Level.SEVERE,"Unable to save MESSAGES_TABLE " +   e.getMessage(), e);
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
                                        " -- while saving casino");
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
       
          

                public static synchronized DBMessages[] get(String to_user) throws DBException {
                    Connection conn = null;
                    PreparedStatement ps = null;
                    Vector v = new Vector();
                    try {
                       // new Exception().printStackTrace();
                    	StringBuilder sb = new StringBuilder("select *");
                        sb.append(" from ").append(MESSAGES_TABLE);
                        sb.append(" where ").append(TO_USER).append("=? limit 20");
                        conn = ConnectionManager.getConnection("GameEngine");
                        ps = conn.prepareStatement(sb.toString());
                        ps.setString(1, to_user);
                        _cat.finest(sb.toString());
                        ResultSet r = ps.executeQuery();
                        while (r.next()) {
                            DBMessages msg = new DBMessages();
                            msg._id = r.getInt(ID);
                            msg._to_user = to_user;
                            msg._from_user = r.getString(FROM_USER);
                            msg._subject = r.getString(SUBJECT);
                            msg._message = r.getString(MESSAGE);
                            msg._status = r.getInt(STATUS);
                            msg._ts = r.getTimestamp(SEND_TS);
                            v.add(msg);
                        }             
                        r.close();
                        ps.close();
                        
                        conn.close();
                    } catch (SQLException e) {
                        _cat.log( Level.SEVERE,"Unable to get casino " + e.getMessage(), e);

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
                                              " -- while retriving db casino");
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
                    return (DBMessages[])v.toArray(new DBMessages[v.size()]);
                }

              public boolean delete(int id) throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                	StringBuilder sb = new StringBuilder("delete ");
                  sb.append(" from ").append(MESSAGES_TABLE).append(" where ");
                  sb.append(ID).append("=?");
                  conn = ConnectionManager.getConnection("GameEngine");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setInt(1, id);
                  boolean r = ps.execute();
                    ps.close();
                    conn.close();
                    return r;
                }
                catch (SQLException e) {
                  _cat.severe("Error in deleting message " + e.getMessage());
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
                  throw new DBException(e.getMessage() + " -- Error in deleting message");
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
