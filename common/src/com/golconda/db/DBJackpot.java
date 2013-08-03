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


public class DBJackpot {
        // set the category for logging
        static Logger _cat = Logger.getLogger(DBJackpot.class.getName());
       
        String _name;
        String _description;
        double _amount;
        long _duration;
        Timestamp _start_time;
        Timestamp _next_draw;
        String _last_winner;

        public static final String NAME;
        public static final String DESCRIPTION;
        public static final String AMOUNT;
        public static final String DURATION;
        public static final String START_TIME;
        public static final String NEXT_DRAW;
        public static final String WINNER;

        public static final String  JACKPOTS_TABLE = "T_JACKPOTS";

        static {
              NAME = "NAME";
              DESCRIPTION = "DESCRIPTION";
              AMOUNT = "AMOUNT";
              DURATION = "DURATION";
              START_TIME = "START_TIME";
              NEXT_DRAW = "NEXT_DRAW";
              WINNER = "USERID";
        }
        
        public String getName(){        return _name;    }
        public void setName(String str){        _name = str;    }            
        public String getDescription(){        return _description;    }
        public void setDescription(String str){        _description = str;    }    
        public double getAmount(){        return _amount;    }
        public void setAmount(double str){        _amount = str;    }  
        public long getDuration(){        return _duration;    }
        public void setDuration(long str){        _duration = str;    }
        public Date getStartTs(){        return _start_time;    }
        public void setStartTs(Timestamp str){        _start_time = str;    }
        public Date getNextDrawTs(){        return _next_draw;    }
        public void setNextDrawTs(Timestamp str){        _next_draw = str;    }
        public String getStartTsString(){        
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
              return sdf.format(_start_time);  
        }
        
        public String getWinner(){        return _last_winner;    }
        public void setWinner(String str){        _last_winner = str;    }     
        
        
        public DBJackpot() {
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
                  sb.append(JACKPOTS_TABLE).append("( ");
                  sb.append(NAME).append(",");
                  sb.append(DESCRIPTION).append(",");
                  sb.append(AMOUNT).append(",");
                  sb.append(DURATION).append(",");
                  sb.append(START_TIME).append(",");
                  sb.append(NEXT_DRAW).append(",");
                  sb.append(WINNER).append(")");
                  sb.append(" values ( ?, ?, ?, ?, ?, ?, ?)");
                  ps = conn.prepareStatement(sb.toString());
                  ps.setString(1, _name);
                  ps.setString(2, _description);
                  ps.setDouble(3, _amount);
                  ps.setLong(4, _duration);
                  ps.setTimestamp(5, _start_time);
                  ps.setTimestamp(6, _next_draw);
                  ps.setString(7, _last_winner);
                  _cat.info(this.toString());
                  r = ps.executeUpdate();
                  ps.close();
                  
                  conn.close();
              } catch (SQLException e) {
                  _cat.log( Level.SEVERE, e.getMessage(), e);
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
                    sb.append(JACKPOTS_TABLE).append(" set ");
                    sb.append(AMOUNT).append("=?, ");
                    sb.append(START_TIME).append("=?, ");
                    sb.append(NEXT_DRAW).append("=? where ");
                    sb.append(NAME).append("=?");
                    ps = conn.prepareStatement(sb.toString());
                    ps.setDouble(1, _amount);
                    ps.setTimestamp(3, _start_time);
                    ps.setTimestamp(4, _next_draw);
                    ps.setString(5, _name);
                    _cat.info(this.toString());
                    r = ps.executeUpdate();
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to save news " + 
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
                                          " -- news");
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
            

            public static synchronized DBJackpot[] get() throws DBException {
                Connection conn = null;
                PreparedStatement ps = null;
                DBJackpot sp = null;
                Vector<DBJackpot> v = new Vector<DBJackpot>();
                try {
                   // new Exception().printStackTrace();
                	StringBuilder sb = new StringBuilder("select ");
                    sb.append(NAME).append(",  ");
                    sb.append(DESCRIPTION).append(",  ");
                    sb.append(AMOUNT).append(",  ");
                    sb.append(DURATION).append(",  ");
                    sb.append(START_TIME).append(",  ");
                    sb.append(NEXT_DRAW).append(",  ");
                    sb.append(WINNER);
                    sb.append(" from ").append(JACKPOTS_TABLE);
                    conn = ConnectionManager.getConnection("GameEngine");
                    ps = conn.prepareStatement(sb.toString());
                    _cat.finest(sb.toString());
                    ResultSet r = ps.executeQuery();
                    while (r.next()) {
                        sp = new DBJackpot();
                        sp._name = r.getString(NAME);
                        sp._description = r.getString(DESCRIPTION);
                        sp._amount = r.getDouble(AMOUNT);
                        sp._duration = r.getLong(DURATION);
                        sp._start_time = r.getTimestamp(START_TIME);
                        sp._next_draw = r.getTimestamp(NEXT_DRAW);
                        sp._last_winner = r.getString(WINNER);
                        v.add(sp);
                    }             
                    r.close();
                    ps.close();
                    
                    conn.close();
                } catch (SQLException e) {
                    _cat.log( Level.SEVERE,"Unable to get jckpot " + e.getMessage(), e);

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
                 DBJackpot[] va = v.toArray(new DBJackpot[v.size()]);
                return va;
            }
            
            public String toString(){
                return _name +"," + _description + ", " + _amount + ", " + _duration + ", " + _start_time + ", " + _next_draw + ", " + _last_winner;
            }
            
            public static void main(String args[])  throws Exception 
            {
                DBJackpot dbj[] = DBJackpot.get();
                
                for (int i=0;i<dbj.length;i++){
                    //dbj[i]._start_time = new Timestamp(System.currentTimeMillis());
                   // dbj[i]._next_draw = new Timestamp(System.currentTimeMillis());
                   // dbj[i]._amount += 100;
                    if (dbj[i]._name.equals("Hourly Bonanza")){
                        dbj[i]._duration = 1 * 60 * 60 * 1000;
                    }
                    else if (dbj[i]._name.equals("Daily Draw")){
                        dbj[i]._duration = 24 * 60 * 60 * 1000;
                    }
                    else if (dbj[i]._name.equals("Weekly bumber")){
                        dbj[i]._name = "Weekly Bumper";
                        dbj[i]._duration = 24*7 * 60 * 60 * 1000;
                    }
                    else if (dbj[i]._name.equals("Monthly Jackpot")){
                        dbj[i]._duration = 24*7 *2 * 60 * 60 * 1000;
                    }
                    System.out.println(dbj[i]);
                   dbj[i].save();
                    
                }
            }
            
        
    }
