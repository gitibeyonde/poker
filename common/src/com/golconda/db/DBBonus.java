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

public class DBBonus {
        // set the category for logging
        static Logger _cat = Logger.getLogger(DBBonus.class.getName());
        /**USERID_FK	varchar(36)	latin1_swedish_ci		No	None		 	 	 	 	 	 	 
    	TYPE	int(11)			No	None		 	 	 	 	 	 	
    	AMOUNT	decimal(10,0)			No	None		 	 	 	 	 	 	
    	BONUS_TS	datetime			No	None		 	 	 	 	 	 	
    	INCREMENTAL_AMOUNT	decimal(10,0)			No	None		 	 	 	 	 	 	
    	CASHED_TILL_TS	datetime			No	None		 	 	 	 	 	 	
    	SCHEDULAR_RUN_TS	timestamp		on update CURRENT_TIMESTAMP	No	CURRENT_TIMESTAMP	on update CURRENT_TIMESTAMP	 	 	 	 	 	 	
    	ADJUSTMENT_AMOUNT	decimal(10,0)			No	None		 	 	 	 	 	 	
    	EXPIRY_DATE**/
        
       
        public String _userid;
        public int _type;
        public String _typeString; // Friend Bonus | Deposit Bonus
        public double _bonus_amount;
        public Timestamp _bonus_ts;
        public double _incremental_bonus;
        public double _bonus_paid;
        public Timestamp _cashed_till_ts;
        public double _unclaimed_bonus;
        public Timestamp _schedular_run_ts;
        public double _adjustment_amount;
        public Timestamp _expiry_ts;
        
        public String _status; //UNCLAIMED|EXPIRED

		public static final String USERID;
        public static final String TYPE;
        public static final String BONUS_AMOUNT;
        public static final String BONUS_TS;
        public static final String INCREMENTAL_AMOUNT;
        public static final String BONUS_PAID;
        public static final String CASHED_TILL_TS;
        public static final String UNCLAIMED_BONUS;
        public static final String SCHEDULAR_RUN_TS;
        public static final String ADJUSTMENT_AMOUNT;
        public static final String EXPIRY_DATE;
       
        public static final String  BONUS_TABLE = "T_BONUS";

        static {
        	USERID = "USERID_FK";
        	TYPE = "TYPE";
        	BONUS_AMOUNT = "BONUS_AMOUNT";
        	BONUS_TS = "BONUS_TS";
        	INCREMENTAL_AMOUNT = "INCREMENTAL_AMOUNT";
        	BONUS_PAID = "BONUS_PAID";
        	CASHED_TILL_TS = "CASHED_TILL_TS";
        	UNCLAIMED_BONUS = "UNCLAIMED_BONUS";
        	SCHEDULAR_RUN_TS = "SCHEDULAR_RUN_TS";
        	ADJUSTMENT_AMOUNT = "ADJUSTMENT_AMOUNT";
        	EXPIRY_DATE = "EXPIRY_DATE";
        }
        
        public String getUserid(){        return _userid;    }        
        public void setUserid(String str){        _userid = str;    }    
        public int getType(){       return _type;    }        
        public void setType(int str){        _type = str;    }
        public String getTypeString() {	
        	if(_type == 1)	{
        		return "Friend Bonus";
        		}
        	if(_type == 2)	{
        		return "Deposit Bonus";
        		}
        		return "Unknown";
        	}
		public double getBonusAmount(){       return _bonus_amount;    }        
        public void setBonusAmount(double str){        _bonus_amount = str;    }
        public Date getBonusTs(){        return _bonus_ts;    }
        public void setBonusTs(Timestamp str){        _bonus_ts = str;    }       
        public String getBonusTsString(){        
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
               return sdf.format(_bonus_ts);  
         }
        public double getIncrementalBonusAmount(){       return _incremental_bonus;    }        
        public void setIncrementalBonusAmount(double str){        _incremental_bonus = str;    }
       
        public double getBonusPaid() {return _bonus_paid;		}
		public void setBonusPaid(double bonusPaid) {	_bonus_paid = bonusPaid;	}
		public Date getCashedTillTs(){        return _cashed_till_ts;    }
        public void setCashedTillTs(Timestamp str){        _cashed_till_ts = str;    }       
        public String getCashedTillTsString(){        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
              return sdf.format(_cashed_till_ts);  
        }
        public Date getSchedularRunTs(){        return _schedular_run_ts;    }
        public void setSchedularRunTs(Timestamp str){        _schedular_run_ts = str;    }       
        public String getSchedularRunTsString(){        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
              return sdf.format(_schedular_run_ts);  
        }
        public double getAdjustmentAmount(){       return _adjustment_amount;    }        
        public void setAdjustmentAmount(double str){        _adjustment_amount = str;    }
       public Date getExpiryTs(){        return _expiry_ts;    }
       public void setExpiryTs(Timestamp str){        _expiry_ts = str;    }       public String getExpiryTsString(){        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
              return sdf.format(_expiry_ts);  
        }
       public double getUnclaimedBonus() {	return _unclaimed_bonus;	}
       public void setUnclaimedBonus(double unclaimedBonus) {	_unclaimed_bonus = unclaimedBonus;	}
       public String getstatus() {
    	   if( System.currentTimeMillis() > _expiry_ts.getTime())
    		   return "EXPIRED";
    	   else
    		   return "UNCLAIMED";
    	   }
		public void setstatus(String status) {	_status = status;	}
        
        public DBBonus() {
        }
        
        /**USERID = "USERID";
    	TYPE = "TYPE";
    	BONUS_AMOUNT = "BONUS_AMOUNT";
    	BONUS_TS = "BONUS_TS";
    	INCREMENTAL_AMOUNT = "INCREMENTAL_AMOUNT";
    	CASHED_TILL_TS = "CASHED_TILL_TS";
    	SCHEDULAR_RUN_TS = "SCHEDULAR_RUN_TS";
    	ADJUSTMENT_AMOUNT = "ADJUSTMENT_AMOUNT";
    	EXPIRY_DATE = "EXPIRY_DATE";**/
          public synchronized int save() throws DBException {
              PreparedStatement ps = null;
              Connection  conn = null;
              int r = -1;
             try {
            //new Exception().printStackTrace();
                   conn = ConnectionManager.getConnection("GameEngine");
                   conn.setAutoCommit(true);
                   StringBuilder sb = new StringBuilder("replace into ");
                  sb.append(BONUS_TABLE).append("( ");
                  sb.append(USERID).append(",");
                  sb.append(TYPE).append(",");
                  sb.append(BONUS_AMOUNT).append(",");
                  sb.append(BONUS_TS).append(",");
                  sb.append(INCREMENTAL_AMOUNT).append(",");
                  sb.append(BONUS_PAID).append(",");
                  sb.append(CASHED_TILL_TS).append(",");
                  sb.append(UNCLAIMED_BONUS).append(",");
                  sb.append(SCHEDULAR_RUN_TS).append(",");
                  sb.append(ADJUSTMENT_AMOUNT).append(",");
                  sb.append(EXPIRY_DATE).append(")");
                  sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, _userid);
                  ps.setInt(2, _type);
                  ps.setDouble(3, _bonus_amount);
                  ps.setTimestamp(4, _bonus_ts == null ? _bonus_ts=new java.sql.Timestamp(System.currentTimeMillis()) : _bonus_ts);
                  ps.setDouble(5, _incremental_bonus);
                  ps.setDouble(6, _bonus_paid);
                  ps.setTimestamp(7, _cashed_till_ts == null ? _cashed_till_ts=new java.sql.Timestamp(System.currentTimeMillis()) : _cashed_till_ts);
                  ps.setDouble(8, _unclaimed_bonus);
                  ps.setTimestamp(9, _schedular_run_ts == null ? _schedular_run_ts=new java.sql.Timestamp(System.currentTimeMillis()) : _schedular_run_ts);
                  ps.setDouble(10, _adjustment_amount);
                  ps.setTimestamp(11, _expiry_ts);
                  _cat.info(this.toString());
                  r = ps.executeUpdate();
                  ps.close();
                  
                  conn.close();
              } catch (SQLException e) {
                  _cat.log( Level.SEVERE,"Unable to save BONUS entry " + 
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
                                        " -- while saving bonus entry");
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
                     StringBuilder sb = new StringBuilder("update T_BONUS set ");
                     //sb.append(USERID).append(" = ?, ");
                     //sb.append(TYPE).append(" = ?, ");
                     //sb.append(BONUS_AMOUNT).append(" = ?, ");
                     //sb.append(BONUS_TS).append(" = ?, ");
                     //sb.append(INCREMENTAL_AMOUNT).append(" = ?, ");
                     sb.append(BONUS_PAID).append(" = ?, ");
                     sb.append(CASHED_TILL_TS).append(" = ?, ");
                     sb.append(UNCLAIMED_BONUS).append(" =?, ");
                     sb.append(SCHEDULAR_RUN_TS).append(" = ? ");
                     //sb.append(ADJUSTMENT_AMOUNT).append(" = ?, ");
                     //sb.append(EXPIRY_DATE).append(" = ? ");
                     sb.append(" where ");
                     sb.append(USERID).append(" = ? ");
                    _cat.info(sb.toString()+"   "+_userid);
                    int i=1;
                    ps = conn.prepareStatement(sb.toString());
                    //ps.setString(i++, _userid);
                    //ps.setInt(i++, _type);
                    ps.setDouble(i++,_bonus_paid);
                    ps.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
                    ps.setDouble(i++, _unclaimed_bonus);
                    ps.setTimestamp(i++, new java.sql.Timestamp(System.currentTimeMillis()));
                    //ps.setDouble(i++, _adjustment_amount);
                    //ps.setTimestamp(i++, _expiry_ts == null ? new Timestamp(System.currentTimeMillis()) : _expiry_ts);
                   
                    ps.setString(i++, _userid);
                    r = ps.executeUpdate();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to update T_BONUS " + 
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
                                          " -- T_BONUS");
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
            
            public static synchronized DBBonus[] get() throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                DBBonus db = null;
                Vector v = new Vector();
                try {
                	StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(TYPE).append(",  ");
                    sb.append(BONUS_AMOUNT).append(",  ");
                    sb.append(BONUS_TS).append(",  ");
                    sb.append(INCREMENTAL_AMOUNT).append(",  ");
                    sb.append(BONUS_PAID).append(",  ");
                    sb.append(CASHED_TILL_TS).append(",  ");
                    sb.append(UNCLAIMED_BONUS).append(", ");
                    sb.append(SCHEDULAR_RUN_TS).append(",  ");
                    sb.append(ADJUSTMENT_AMOUNT).append(",  ");
                    sb.append(EXPIRY_DATE);
                    sb.append(" from ").append(BONUS_TABLE);
                    /*sb.append(" where ");
                    sb.append("date(?)");
                    sb.append(" <= ");
                    sb.append(EXPIRY_DATE);*/
                    sb.append(" order by BONUS_TS ASC"); //ASC take care of two bonuses release older before newer
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                   // ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                        db = new DBBonus();
                        db._userid = r.getString(USERID);
                        db._type = r.getInt(TYPE);
                        db._bonus_amount = r.getDouble(BONUS_AMOUNT);
                        db._bonus_ts = r.getTimestamp(BONUS_TS);
                        db._incremental_bonus= r.getDouble(INCREMENTAL_AMOUNT);
                        db._bonus_paid = r.getDouble(BONUS_PAID);
                        db._cashed_till_ts = r.getTimestamp(CASHED_TILL_TS);
                        db._unclaimed_bonus = r.getDouble(UNCLAIMED_BONUS);
                        db._schedular_run_ts = r.getTimestamp(SCHEDULAR_RUN_TS);
                        db._adjustment_amount = r.getDouble(ADJUSTMENT_AMOUNT);
                        db._expiry_ts = r.getTimestamp(EXPIRY_DATE);
                        v.add(db);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBBonus details " + e.getMessage(), e);

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
                                          " -- while retriving DBBonus details");
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
                DBBonus[] va = (DBBonus[])v.toArray(new DBBonus[v.size()]);
                /* java.util.Arrays.sort(va, new Comparator<DBBonus>() {
                       public int compare(DBBonus o1, DBBonus o2) {
                            return o1._order - o2._order;
                       }
                     });*/
                return va;
            }
           
            public static DBBonus[] getBonusDetails(String pattern) throws DBException {
            	Connection conn = null;
                PreparedStatement ps = null;
                DBBonus db = null;
                Vector v = new Vector();
                try {
                	StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(TYPE).append(",  ");
                    sb.append(BONUS_AMOUNT).append(",  ");
                    sb.append(BONUS_TS).append(",  ");
                    sb.append(INCREMENTAL_AMOUNT).append(",  ");
                    sb.append(BONUS_PAID).append(",  ");
                    sb.append(CASHED_TILL_TS).append(",  ");
                    sb.append(UNCLAIMED_BONUS).append(",  ");
                    sb.append(SCHEDULAR_RUN_TS).append(",  ");
                    sb.append(ADJUSTMENT_AMOUNT).append(",  ");
                    sb.append(EXPIRY_DATE);
                    sb.append(" from ").append(BONUS_TABLE);
                    sb.append(" where ");
                    sb.append(USERID).append(" like '%"+pattern+"%'");
                    sb.append(" order by USERID_FK desc");
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                        db = new DBBonus();
                        db._userid = r.getString(USERID);
                        db._type = r.getInt(TYPE);
                        db._bonus_amount = r.getDouble(BONUS_AMOUNT);
                        db._bonus_ts = r.getTimestamp(BONUS_TS);
                        db._incremental_bonus= r.getDouble(INCREMENTAL_AMOUNT);
                        db._bonus_paid = r.getDouble(BONUS_PAID);
                        db._cashed_till_ts = r.getTimestamp(CASHED_TILL_TS);
                        db._unclaimed_bonus = r.getDouble(UNCLAIMED_BONUS);
                        db._schedular_run_ts = r.getTimestamp(SCHEDULAR_RUN_TS);
                        db._adjustment_amount = r.getDouble(ADJUSTMENT_AMOUNT);
                        db._expiry_ts = r.getTimestamp(EXPIRY_DATE);
                        v.add(db);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBBonus details " + e.getMessage(), e);

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
                                          " -- while retriving DBBonus details");
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
                DBBonus[] va = (DBBonus[])v.toArray(new DBBonus[v.size()]);
                /* java.util.Arrays.sort(va, new Comparator<DBBonus>() {
                       public int compare(DBBonus o1, DBBonus o2) {
                            return o1._order - o2._order;
                       }
                     });*/
                return va;
            }
            
            public static DBBonus get(String uid) throws DBException {
            	Connection conn = null;
                PreparedStatement ps = null;
                DBBonus db = null;
                try {
                	StringBuilder sb = new StringBuilder("select ");
                    sb.append(USERID).append(",  ");
                    sb.append(TYPE).append(",  ");
                    sb.append(BONUS_AMOUNT).append(",  ");
                    sb.append(BONUS_TS).append(",  ");
                    sb.append(INCREMENTAL_AMOUNT).append(",  ");
                    sb.append(BONUS_PAID).append(",  ");
                    sb.append(CASHED_TILL_TS).append(",  ");
                    sb.append(UNCLAIMED_BONUS).append(",  ");
                    sb.append(SCHEDULAR_RUN_TS).append(",  ");
                    sb.append(ADJUSTMENT_AMOUNT).append(",  ");
                    sb.append(EXPIRY_DATE);
                    sb.append(" from ").append(BONUS_TABLE);
                    sb.append(" where ");
                    sb.append(USERID).append(" like binary '%"+uid+"%'");
                    sb.append(" order by USERID_FK desc");
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    if (r.next()) {
                        db = new DBBonus();
                        db._userid = r.getString(USERID);
                        db._type = r.getInt(TYPE);
                        db._bonus_amount = r.getDouble(BONUS_AMOUNT);
                        db._bonus_ts = r.getTimestamp(BONUS_TS);
                        db._incremental_bonus= r.getDouble(INCREMENTAL_AMOUNT);
                        db._bonus_paid = r.getDouble(BONUS_PAID);
                        db._cashed_till_ts = r.getTimestamp(CASHED_TILL_TS);
                        db._unclaimed_bonus = r.getDouble(UNCLAIMED_BONUS);
                        db._schedular_run_ts = r.getTimestamp(SCHEDULAR_RUN_TS);
                        db._adjustment_amount = r.getDouble(ADJUSTMENT_AMOUNT);
                        db._expiry_ts = r.getTimestamp(EXPIRY_DATE);
                    }            
                    r.close(); 
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get DBBonus details " + e.getMessage(), e);

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
                                          " -- while retriving DBBonus details");
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
                /* java.util.Arrays.sort(va, new Comparator<DBBonus>() {
                       public int compare(DBBonus o1, DBBonus o2) {
                            return o1._order - o2._order;
                       }
                     });*/
                return db;
            }

              public boolean delete(String heading) throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                	StringBuilder sb = new StringBuilder("delete ");
                  /**sb.append(" from ").append(NEWS_TABLE).append(" where ");
                  sb.append(HEADING).append("=?");**/
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
                  throw new DBException(e.getMessage() + " -- while getting news");
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
              /*right now it is not using*/
              public synchronized int resetBonus() throws DBException {
                  PreparedStatement ps = null;
                  Connection  conn = null;
                  int r = -1;
                 try {
                       conn = ConnectionManager.getConnection("GameEngine");
                       conn.setAutoCommit(true);
                       StringBuilder sb = new StringBuilder("update T_BONUS set ");
                       //sb.append(USERID).append(" = ?, ");
                       //sb.append(TYPE).append(" = ?, ");
                       sb.append(BONUS_AMOUNT).append(" = ? ");
                       //sb.append(BONUS_TS).append(" = ?, ");
                       //sb.append(INCREMENTAL_AMOUNT).append(" = ?, ");
                       //sb.append(BONUS_PAID).append(" = ?, ");
                       //sb.append(CASHED_TILL_TS).append(" = ?, ");
                       //sb.append(UNCLAIMED_BONUS).append(" =?, ");
                       //sb.append(SCHEDULAR_RUN_TS).append(" = ? ");
                       //sb.append(ADJUSTMENT_AMOUNT).append(" = ?, ");
                       //sb.append(EXPIRY_DATE).append(" = ? ");
                       sb.append(" where ");
                       sb.append(USERID).append(" = ? ");
                      _cat.info(sb.toString()+"   "+_userid);
                      int i=1;
                      ps = conn.prepareStatement(sb.toString());
                      //ps.setString(i++, _userid);
                      //ps.setInt(i++, _type);
                      ps.setDouble(i++,_bonus_amount);
                      //ps.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
                      //ps.setDouble(i++, _unclaimed_bonus);
                      //ps.setTimestamp(i++, new java.sql.Timestamp(System.currentTimeMillis()));
                      //ps.setDouble(i++, _adjustment_amount);
                      //ps.setTimestamp(i++, _expiry_ts == null ? new Timestamp(System.currentTimeMillis()) : _expiry_ts);
                      ps.setString(i++, _userid);
                      r = ps.executeUpdate();
                      ps.close();
                      conn.close();
                  } catch (SQLException e) {
                      _cat.log( Level.SEVERE,"Unable to reset T_BONUS " + 
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
                                            " -- T_BONUS");
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
