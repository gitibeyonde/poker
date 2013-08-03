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

public class DBAnnouncements {
        // set the category for logging
        static Logger _cat = Logger.getLogger(DBAnnouncements.class.getName());
    //HEADING               		nvarchar(20) NOT null PRIMARY KEY,
    // 	DISP_ORDER         	        integer NOT null,
    // 	BODY_TEXT			text,
    //         SUBMIT_TS	 		datetime null
       
        public String _heading;
        public int _order;
        public String _body_text;
        public Timestamp _ts;

        public static final String HEADING;
        public static final String ORDER;
        public static final String BODY_TEXT;
        public static final String TIMESTAMP;

        public static final String  ANNOUNCEMENTS_TABLE = "T_ANNOUNCEMENTS";

        static {
              HEADING = "HEADING";
              ORDER = "DISP_ORDER";
              BODY_TEXT = "BODY_TEXT";
              TIMESTAMP = "SUBMIT_TS";
        }
        
        public String getHeading(){        return _heading;    }
        public void setHeading(String str){        _heading = str;    }    
        public int getOrder(){        return _order;    }
        public void setOrder(int str){        _order = str;    }
        public String getBodyText(){        return _body_text;    }
        public void setBodyText(String str){        _body_text = str;    }
       public Date getLastUpdateTs(){        return _ts;    }
       public void setLastUpdateTs(Timestamp str){        _ts = str;    }
       public String getLastUpdateTsString(){        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
              return sdf.format(_ts);  
        }
        
        
        
        public DBAnnouncements() {
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
                  sb.append(ANNOUNCEMENTS_TABLE).append("( ");
                  sb.append(HEADING).append(",");
                  sb.append(ORDER).append(",");
                  sb.append(BODY_TEXT).append(",");
                  sb.append(TIMESTAMP).append(")");
                  sb.append(" values ( ?, ?, ?, ?)");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, _heading);
                  ps.setInt(2, _order);
                  ps.setString(3, _body_text);
                  ps.setTimestamp(4, _ts=new java.sql.Timestamp(System.currentTimeMillis()));
                  _cat.info(this.toString());
                  r = ps.executeUpdate();
                  ps.close();
                  
                  conn.close();
              } catch (SQLException e) {
                  _cat.log( Level.SEVERE,"Unable to save ANNOUNCEMENTS_TABLE " + 
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
          
            public synchronized int update() throws DBException {
                PreparedStatement ps = null;
                Connection  conn = null;
                int r = -1;
               try {
              //new Exception().printStackTrace();
                     conn = ConnectionManager.getConnection("GameEngine");
                     conn.setAutoCommit(true);
                     StringBuilder sb = new StringBuilder("update ");
                    sb.append(ANNOUNCEMENTS_TABLE).append(" set ");
                    sb.append(ORDER).append("=?, ");
                    sb.append(BODY_TEXT).append("=?, ");
                    sb.append(TIMESTAMP).append("=? where ");
                    sb.append(HEADING).append("=?");
                    ps = conn.prepareStatement(sb.toString());
                    ps.setInt(1, _order);
                    ps.setString(2, _body_text);
                    ps.setTimestamp(3, _ts=new java.sql.Timestamp(System.currentTimeMillis()));
                    ps.setString(4, _heading);
                    _cat.info(this.toString());
                    r = ps.executeUpdate();
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to save wallet " + 
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
            

            public static synchronized DBAnnouncements[] get() throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                DBAnnouncements sp = null;
                Vector<DBAnnouncements> v = new Vector<DBAnnouncements>();
                try {
                   // new Exception().printStackTrace();
                	StringBuilder sb = new StringBuilder("select ");
                    sb.append(HEADING).append(",  ");
                    sb.append(ORDER).append(",  ");
                    sb.append(BODY_TEXT).append(",  ");
                    sb.append(TIMESTAMP);
                    sb.append(" from ").append(ANNOUNCEMENTS_TABLE);
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                        sp = new DBAnnouncements();
                        sp._heading = r.getString(HEADING);
                        sp._order = r.getInt(ORDER);
                        sp._body_text = r.getString(BODY_TEXT);
                        sp._ts = r.getTimestamp(TIMESTAMP);
                        v.add(sp);
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
                 DBAnnouncements[] va = v.toArray(new DBAnnouncements[v.size()]);
                 java.util.Arrays.sort(va, new Comparator<DBAnnouncements>() {
                       public int compare(DBAnnouncements o1, DBAnnouncements o2) {
                            return o1._order - o2._order;
                       }
                     });
                return va;
            }
            

                public synchronized DBAnnouncements get(String cid) throws DBException {
                    Connection conn = null;
                    PreparedStatement ps = null;
                    try {
                       // new Exception().printStackTrace();
                    	StringBuilder sb = new StringBuilder("select ");
                        sb.append(ORDER).append(",  ");
                        sb.append(BODY_TEXT).append(",  ");
                        sb.append(TIMESTAMP);
                        sb.append(" from ").append(ANNOUNCEMENTS_TABLE);
                        sb.append(" where ").append(HEADING).append("='").append(cid).append("'");
                        conn = ConnectionManager.getConnection("GameEngine");
                        ps = conn.prepareStatement(sb.toString());
                        _cat.finest(sb.toString());
                        ResultSet r = ps.executeQuery();
                        if (r.next()) {
                            _heading = cid;
                            _order = r.getInt(ORDER);
                            _body_text = r.getString(BODY_TEXT);
                            _ts = r.getTimestamp(TIMESTAMP);
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
                    return this;
                }

              public boolean delete(String heading) throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                	StringBuilder sb = new StringBuilder("delete ");
                  sb.append(" from ").append(ANNOUNCEMENTS_TABLE).append(" where ");
                  sb.append(HEADING).append("=?");
                  conn = ConnectionManager.getConnection("GameEngine");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, heading);
                  boolean r = ps.execute();
                    ps.close();
                    conn.close();
                    return r;
                }
                catch (SQLException e) {
                  _cat.severe("Error in getting game " + e.getMessage());
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
              }
        
            
        
    }
