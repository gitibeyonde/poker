package com.golconda.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.ConnectionManager;

public class DBRakeback {
        // set the category for logging
        static Logger _cat = Logger.getLogger(DBRakeback.class.getName());
       
        public String _userid;
        public double _rake_generated;
        public double _deductions;
        public double _net_revenue;
        public double _rakeback_amt;
        public double _lastmonth_rakeback_amt;
        public Timestamp _last_updated_ts;
        /*
         * DEDUCTIONS` decimal(10,2) NOT NULL DEFAULT '0.00',
  `NET_REVENUE` decimal(10,2) NOT NULL,*/
		public static final String USERID;
        public static final String RAKE_GENERATED;
        public static final String DEDUCTIONS;
        public static final String NET_REVENUE;
        public static final String RAKE_BACK;
        public static final String LAST_MONTH_RAKE_BACK;
        public static final String LAST_UPDATED_TS;
       
        public static final String  RAKEBACK_TABLE = "T_RAKEBACK";

        static {
        	USERID ="USER_ID_FK";
        	RAKE_GENERATED = "RAKE_GENERATED";
        	DEDUCTIONS = "DEDUCTIONS";
        	NET_REVENUE = "NET_REVENUE";
        	RAKE_BACK = "RAKE_BACK_AMOUNT";
        	LAST_MONTH_RAKE_BACK = "LAST_MONTH_RAKE_BACK";
        	LAST_UPDATED_TS = "LAST_UPDATED_TS";
        }
        
        public String getUserid() {		return _userid;		}
		public void setUserid(String userid) {		_userid = userid;	}
		public double getRakeGenerated() {	return _rake_generated;	}
		public void setRakeGenerated(double rakeGenerated) {_rake_generated = rakeGenerated;}
		public double getDeductions() {	return _deductions;		}
		public void setDeductions(double deductions) {	_deductions = deductions;	}
		public double getNetRevenue() {	return _net_revenue;	}
		public void setNetRevenue(double netRevenue) {	_net_revenue = netRevenue;	}
		public double getRakebackAmt() {return _rakeback_amt;	}
		public void setRakebackAmt(double rakebackAmt) {_rakeback_amt = rakebackAmt;}
		public double getLastMonthRakebackAmt() {return _lastmonth_rakeback_amt;	}
		public void setLastMonthRakebackAmt(double rakebackAmt) {_lastmonth_rakeback_amt = rakebackAmt;}
		public Timestamp getLastUpdatedTs() {	return _last_updated_ts;	}
		public void setLastUpdatedTs(Timestamp lastUpdatedTs) {	_last_updated_ts = lastUpdatedTs;	}
		public String getLastUpdatedTsString(){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(_last_updated_ts);
		}
		public DBRakeback() {
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
                  sb.append(RAKEBACK_TABLE).append("( ");
                  sb.append(USERID).append(",");
                  sb.append(RAKE_GENERATED).append(",");
                  sb.append(DEDUCTIONS).append(",");
                  sb.append(NET_REVENUE).append(",");
                  sb.append(RAKE_BACK).append(",");
                  sb.append(LAST_MONTH_RAKE_BACK).append(",");
                  sb.append(LAST_UPDATED_TS).append(")");
                  sb.append(" values ( ?, ?, ?, ?, ?)");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, _userid);
                  ps.setDouble(2, _rake_generated);
                  ps.setDouble(3, _deductions);
                  ps.setDouble(4, _net_revenue);
                  ps.setDouble(5, _rakeback_amt);
                  ps.setDouble(6, _lastmonth_rakeback_amt);
                  ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
                  _cat.info(this.toString());
                  r = ps.executeUpdate();
                  ps.close();
                  
                  conn.close();
              } catch (SQLException e) {
                  _cat.log( Level.SEVERE,"Unable to save Rakeback entry " + 
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
                  }
                  throw new DBException(e.getMessage() + 
                                        " -- while saving Rakeback entry");
              } finally {
                  try {
                      if (ps != null) {
                          ps.close();
                      }
                      if (conn != null) {
                          conn.close();
                      }
                  } catch (SQLException se) {
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
                     StringBuilder sb = new StringBuilder("update T_RAKEBACK set ");
                     sb.append(USERID).append(" = ?, ");
                     sb.append(RAKE_GENERATED).append(" = ?, ");
                     sb.append(DEDUCTIONS).append(" = ?, ");
                     sb.append(NET_REVENUE).append(" = ?, ");
                     sb.append(RAKE_BACK).append(" = ?, ");
                     sb.append(LAST_MONTH_RAKE_BACK).append(" = ?, ");
                     sb.append(LAST_UPDATED_TS).append(" = ? ");
                     sb.append(" where ");
                     sb.append(USERID).append(" = ? ");
                    _cat.info(sb.toString());
                    int i=1;
                    ps = conn.prepareStatement(sb.toString());
                    ps.setString(i++, _userid);
                    ps.setDouble(i++, _rake_generated);
                    ps.setDouble(i++, _deductions);
                    ps.setDouble(i++, _net_revenue);
                    ps.setDouble(i++, _rakeback_amt);
                    ps.setDouble(i++, _lastmonth_rakeback_amt);
                    ps.setTimestamp(i++, new java.sql.Timestamp(System.currentTimeMillis()));
                   
                    ps.setString(i++, _userid);
                    r = ps.executeUpdate();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to update T_RAKEBACK " + 
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
                                          " -- T_RAKEBACK");
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
            
            public static synchronized DBRakeback[] get() throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                DBRakeback db = null;
                Vector v = new Vector();
                try {
                    StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(RAKE_GENERATED).append(",  ");
                    sb.append(DEDUCTIONS).append(",  ");
                    sb.append(NET_REVENUE).append(",  ");
                    sb.append(RAKE_BACK).append(",  ");
                    sb.append(LAST_MONTH_RAKE_BACK).append(",");
                    sb.append(LAST_UPDATED_TS);
                    sb.append(" from ").append(RAKEBACK_TABLE);
                    
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                        db = new DBRakeback();
                        db._userid = r.getString(USERID);
                        db._rake_generated = r.getDouble(RAKE_GENERATED);
                        db._deductions = r.getDouble(DEDUCTIONS);
                        db._net_revenue = r.getDouble(NET_REVENUE);
                        db._rakeback_amt= r.getDouble(RAKE_BACK);
                        db._lastmonth_rakeback_amt = r.getDouble(LAST_MONTH_RAKE_BACK);
                        db._last_updated_ts = r.getTimestamp(LAST_UPDATED_TS);
                        v.add(db);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBRakeback details " + e.getMessage(), e);

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
                                          " -- while retriving DBRakeback details");
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
                DBRakeback[] va = (DBRakeback[])v.toArray(new DBRakeback[v.size()]);
               
                return va;
            }
            
            public static synchronized DBRakeback[] getMonthlyRake(String pattern) throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                DBRakeback db = null;
                Vector v = new Vector();
                try {
                    StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(RAKE_GENERATED).append(",  ");
                    sb.append(DEDUCTIONS).append(",  ");
                    sb.append(NET_REVENUE).append(",  ");
                    sb.append(RAKE_BACK).append(",  ");
                    sb.append(LAST_MONTH_RAKE_BACK).append(",");
                    sb.append(LAST_UPDATED_TS);
                    sb.append(" from ").append(RAKEBACK_TABLE);
                    sb.append(" where ");
                    sb.append(USERID).append(" like '%"+pattern+"%'");
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                        db = new DBRakeback();
                        db._userid = r.getString(USERID);
                        db._rake_generated = r.getDouble(RAKE_GENERATED);
                        db._deductions = r.getDouble(DEDUCTIONS);
                        db._net_revenue = r.getDouble(NET_REVENUE);
                        db._rakeback_amt= r.getDouble(RAKE_BACK);
                        db._lastmonth_rakeback_amt = r.getDouble(LAST_MONTH_RAKE_BACK);
                        db._last_updated_ts = r.getTimestamp(LAST_UPDATED_TS);
                        v.add(db);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBRakeback details " + e.getMessage(), e);

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
                                          " -- while retriving DBRakeback details");
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
                DBRakeback[] va = (DBRakeback[])v.toArray(new DBRakeback[v.size()]);
               
                return va;
            }
            public  boolean get(String uid) throws DBException {
            	boolean result = false;
            	Connection conn = null;
                PreparedStatement ps = null;
               
                try {
                    StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(RAKE_GENERATED).append(",  ");
                    sb.append(DEDUCTIONS).append(",  ");
                    sb.append(NET_REVENUE).append(",  ");
                    sb.append(RAKE_BACK).append(",  ");
                    sb.append(LAST_MONTH_RAKE_BACK).append(",");
                    sb.append(LAST_UPDATED_TS);
                    sb.append(" from ").append(RAKEBACK_TABLE);
                    sb.append(" where ");
                    sb.append(USERID).append(" like binary '%"+uid+"%'");
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    if (r.next()) {
                        _userid = r.getString(USERID);
                        _rake_generated = r.getDouble(RAKE_GENERATED);
                        _deductions = r.getDouble(DEDUCTIONS);
                        _net_revenue = r.getDouble(NET_REVENUE);
                        _rakeback_amt= r.getDouble(RAKE_BACK);
                        _lastmonth_rakeback_amt = r.getDouble(LAST_MONTH_RAKE_BACK);
                        _last_updated_ts = r.getTimestamp(LAST_UPDATED_TS);
                        result = true;
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBRakeback details " + e.getMessage(), e);

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
                                          " -- while retriving DBRakeback details");
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
                //DBBonus[] va = (DBBonus[])v.toArray(new DBBonus[v.size()]);
               return result;
            }

            public  static DBRakeback getUserRevenue(String uid) throws DBException {
            	Connection conn = null;
                PreparedStatement ps = null;
                DBRakeback db = null;
                try {
                    StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(RAKE_GENERATED).append(",  ");
                    sb.append(DEDUCTIONS).append(",  ");
                    sb.append(NET_REVENUE).append(",  ");
                    sb.append(RAKE_BACK).append(",  ");
                    sb.append(LAST_MONTH_RAKE_BACK).append(",");
                    sb.append(LAST_UPDATED_TS);
                    sb.append(" from ").append(RAKEBACK_TABLE);
                    sb.append(" where ");
                    sb.append(USERID).append(" like binary '%"+uid+"%'");
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                    	db = new DBRakeback();
                        db._userid = r.getString(USERID);
                        db._rake_generated = r.getDouble(RAKE_GENERATED);
                        db._deductions = r.getDouble(DEDUCTIONS);
                        db._net_revenue = r.getDouble(NET_REVENUE);
                        db._rakeback_amt= r.getDouble(RAKE_BACK);
                        db._lastmonth_rakeback_amt = r.getDouble(LAST_MONTH_RAKE_BACK);
                        db._last_updated_ts = r.getTimestamp(LAST_UPDATED_TS);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBRakeback details " + e.getMessage(), e);

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
                                          " -- while retriving DBRakeback details");
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
               
                return db;
            }
            public  static double getRevenueSum(String list) throws DBException {
            	Connection conn = null;
                PreparedStatement ps = null;
                DBRakeback db = null;
                double d=0;
                try {
                    StringBuilder sb = new StringBuilder("select ");
                    sb.append("SUM(NET_REVENUE)"); 
                    sb.append(" from ").append(RAKEBACK_TABLE);
                    sb.append(" where ");
                    sb.append(USERID).append(" in("+list+")");
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    if (r.next()) {
                    	d = r.getDouble(1);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBRakeback details " + e.getMessage(), e);

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
                                          " -- while retriving DBRakeback details");
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
               
                return d;
            }
              public boolean delete(String uid) throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                  StringBuilder sb = new StringBuilder("delete ");
                  sb.append(" from ").append(RAKEBACK_TABLE).append(" where ");
                  sb.append(USERID).append("=?");
                  conn = ConnectionManager.getConnection("GameEngine");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, uid);
                  boolean r = ps.execute();
                    ps.close();
                    conn.close();
                    return r;
                }
                catch (SQLException e) {
                  _cat.severe("Error in getting user details " + e.getMessage());
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
                  throw new DBException(e.getMessage() + " -- while getting userid");
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
              
              public static synchronized DBTransaction[] getRakebackPayouts(String pattern, Timestamp t1, Timestamp t2) {
                  Connection conn = null;
                  PreparedStatement ps = null;
                  DBTransaction sp = null;
                  Vector v = new Vector();
                  try {
                     // new Exception().printStackTrace();
                      StringBuilder sb = new StringBuilder("select *");
                      sb.append(" from ").append("T_TRANSACTION").append(" where ");
                      sb.append("TRANSACTION_TYPE=?");
                      sb.append(" and ");
                      sb.append("USER_ID_FK like '%"+pattern+"%'");
                      sb.append(" and ");
                      sb.append("TRANSACTION_TIMESTAMP");
                      sb.append(" between date(?) and date(?)");
                      sb.append(" order by TRANSACTION_TIMESTAMP desc");
                      conn = ConnectionManager.getConnection("GameEngine");
                      ps = conn.prepareStatement(sb.toString());
                      ps.setLong(1, TransactionType.Rakeback_Payout);
                      ps.setTimestamp(2, t1);
                      ps.setTimestamp(3, t2);
                      _cat.finest(sb.toString());
                      ResultSet r = ps.executeQuery();
                      while (r.next()) {
                          sp = new DBTransaction();
                          sp._userid = r.getString("USER_ID_FK");
                          sp._id = r.getInt("TRANSACTION_ID");
                          sp._ts = r.getTimestamp("TRANSACTION_TIMESTAMP");
                          sp._status = r.getString("TRANSACTION_STATUS");
                          sp._type = new TransactionType(r.getLong("TRANSACTION_TYPE"));
                          sp._session =  r.getString("SESSION_ID_FK");
                          sp._affiliateid = r.getString("AFFILIATE_ID_FK");
                          sp._bonus_code = r.getString("BONUS_CODE");
                          sp._module = new ModuleType( r.getInt("MODULE"));
                          sp._playChips = r.getDouble("PLAY_CHIPS");
                          sp._realChips = r.getDouble("REAL_CHIPS");
                          sp._points = r.getDouble("POINTS");
                          sp._amount = r.getDouble("AMOUNT");
                          sp._currency = r.getString("CURRENCY");
                          sp._comment = r.getString("TRANSACTION_COMMENT");
                          v.add(sp);
                      }             
                      r.close();
                      ps.close();
                      conn.close();
                  } catch (SQLException e) {
                      _cat.log( Level.SEVERE,"Unable to get payout transactions " + e.getMessage(), e);
              
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
                  return (DBTransaction[])v.toArray(new DBTransaction[v.size()]);
              }
    }

