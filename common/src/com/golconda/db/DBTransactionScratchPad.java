package com.golconda.db;

import com.agneya.util.ConnectionManager;
import com.agneya.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class DBTransactionScratchPad {
  // set the category for logging
  static Logger _cat = Logger.getLogger(DBTransactionScratchPad.class.getName());
  //USER_ID_FK		          nvarchar(20) NOT null,
  //      GAME_ID_FK           integer null,
  //      GAME_TYPE_ID_FK      integer null,
  // GAME_NAME         nvarchar(32) null,
  //      SESSION_ID_FK       nvarchar(32),
  //        TRANSACTION_TYPE       nvarchar(20),
  //      AMOUNT		   decimal not null,
  //      TIMESTAMP	  datetime NOT null,
  // 
 
  public String _userid;
  public int _game_type;
  public String _game_name;
  public ModuleType _module;
  public String _session;
  public String _type;
  public double _play, _real;
  public String _currency;
  public String _status;
  public String _comment;
  public Timestamp _ts;


  public static final String DISPLAY_NAME;
  public static final String GAME_TYPE;
  public static final String GAME_NAME;
  public static final String MODULE;
  public static final String SESSION;
  public static final String TYPE;
  public static final String STATUS;
  public static final String PLAY_CHIPS;
  public static final String REAL_CHIPS;
  public static final String TIMESTAMP;

  public static final String SCRATCH_TABLE =
      "T_GAME_TRANSACTION_SCRATCH_PAD";

  static {
        DISPLAY_NAME = "USER_ID_FK";
        GAME_TYPE = "GAME_TYPE_ID_FK";
        GAME_NAME = "GAME_NAME";
        MODULE = "MODULE";
        SESSION = "SESSION_ID_FK";
        TYPE = "TRANSACTION_TYPE";
        STATUS = "STATUS";
        PLAY_CHIPS = "PLAY_CHIPS";
        REAL_CHIPS = "REAL_CHIPS";
        TIMESTAMP = "TIMESTAMP";
  }

  public static String TYPE_PLAY_BUYIN="PLAY_BUYIN";
  public static String TYPE_PLAY_REBUY="PLAY_REBUY";

  public static String TYPE_REAL_BUYIN="REAL_BUYIN";
  public static String TYPE_REAL_REBUY="REAL_REBUY";

  private DBTransactionScratchPad() {
  }
  
    public static int updateScratchTransaction(String usr, int type, String name, int mod, String session, double play, double real) throws DBException  {
        DBTransactionScratchPad dbt = new DBTransactionScratchPad();
        dbt._userid = usr;
        dbt._game_type = type;
        dbt._game_name = name;
        dbt._module = new ModuleType(mod);
        dbt._session = session;
        dbt._play = play;
        dbt._real = real;
        _cat.finer(dbt.toString());
        return dbt.update();
    }
  
  public static int addScratchTransaction(String usr, int type, String name, int mod, String session, double play, double real, String trans_type) throws DBException  {
      DBTransactionScratchPad dbt = new DBTransactionScratchPad();
      dbt._userid = usr;
      dbt._game_type = type;
      dbt._game_name = name;
      dbt._module = new ModuleType(mod);
      dbt._session = session;
        dbt._play = play;
        dbt._real = real;
      dbt._type = trans_type;
      _cat.finer(dbt.toString());
      return dbt.save();
  }
  

  private synchronized int save() throws DBException {
      PreparedStatement ps = null;
      Connection  conn = null;
      int r = -1;
     try {
    //new Exception().printStackTrace();
           conn = ConnectionManager.getConnection("GameEngine");
           conn.setAutoCommit(true);
          StringBuilder sb = new StringBuilder("insert into ");
          sb.append(SCRATCH_TABLE).append("( ");
          sb.append(DISPLAY_NAME).append(",");
          sb.append(GAME_TYPE).append(",");
          sb.append(GAME_NAME).append(",");
          sb.append(MODULE).append(",");
          sb.append(SESSION).append(",");
          sb.append(TYPE).append(",");
          sb.append(STATUS).append(",");
          sb.append(PLAY_CHIPS).append(",");
          sb.append(REAL_CHIPS).append(",");
          sb.append(TIMESTAMP).append(")");
          sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          ps = conn.prepareStatement(sb.toString());
          ps.setString(1, _userid);
          ps.setInt(2, _game_type);
          ps.setString(3, _game_name);
          ps.setInt(4, _module.intVal());
          ps.setString(5, _session);
          ps.setString(6, _type);
          ps.setString(7, _status);
          ps.setDouble(8, _play);
          ps.setDouble(9, _real);
          ps.setTimestamp(10, _ts=new java.sql.Timestamp(System.currentTimeMillis()));
          _cat.info(this.toString());
          r = ps.executeUpdate();
          ps.close();
          
          conn.close();
      } catch (SQLException e) {
          _cat.log( Level.SEVERE,"Unable to save scratch " + 
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
  
    private synchronized int update() throws DBException {
        PreparedStatement ps = null;
        Connection  conn = null;
        int r = -1;
       try {
      //new Exception().printStackTrace();
             conn = ConnectionManager.getConnection("GameEngine");
             conn.setAutoCommit(true);
            StringBuilder sb = new StringBuilder("update ");
            sb.append(SCRATCH_TABLE).append(" set ");
            sb.append(PLAY_CHIPS).append("=?, ");
            sb.append(REAL_CHIPS).append("=?, ");
            sb.append(TIMESTAMP).append("=? where ");
            sb.append(DISPLAY_NAME).append("=? and ");
            sb.append(GAME_NAME).append("=? and ");
            sb.append(SESSION).append("=?");
            ps = conn.prepareStatement(sb.toString());
            ps.setDouble(1, _play);
            ps.setDouble(2, _real);
            ps.setTimestamp(3, _ts=new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(4, _userid);
            ps.setString(5, _game_name);
            ps.setString(6, _session);
            _cat.info(this.toString());
            r = ps.executeUpdate();
            ps.close();
            
            conn.close();
        } catch (SQLException e) {
            _cat.log( Level.SEVERE,"Unable to save scratch " + 
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




    public static synchronized DBTransactionScratchPad[] fetch(String user, String gname, String session) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        DBTransactionScratchPad sp = null;
        Vector v = new Vector();
        try {
           // new Exception().printStackTrace();
            StringBuilder sb = new StringBuilder("select *");
            sb.append(" from ").append(SCRATCH_TABLE).append(" where ");
            sb.append(DISPLAY_NAME).append("=? and ");
            sb.append(GAME_NAME).append("=? and ");
            sb.append(SESSION).append("=?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            ps.setString(2, gname);
            ps.setString(3, session);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new DBTransactionScratchPad();
                sp._userid = user;
                sp._game_type = r.getInt(GAME_TYPE);
                sp._game_name = gname;
                sp._module = new ModuleType(r.getInt(MODULE));
                sp._session = session;
                sp._type = r.getString(TYPE);
                sp._play = r.getDouble(PLAY_CHIPS);
                sp._real = r.getDouble(REAL_CHIPS);
                sp._status = r.getString(STATUS);
                sp._ts = r.getTimestamp(TIMESTAMP);
                v.add(sp);
            }             
            r.close();
            ps.close();
            
            sb = new StringBuilder("delete from ").append(SCRATCH_TABLE).append(" where ");
            sb.append(DISPLAY_NAME).append("=? and ");
            sb.append(GAME_NAME).append("=? and ");
            sb.append(SESSION).append("=?");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            ps.setString(2, gname);
            ps.setString(3, session);
            ps.execute();
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
            throw new DBException(e.getMessage() + 
                                  " -- while retriving db player");
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
        return (DBTransactionScratchPad[])v.toArray(new DBTransactionScratchPad[v.size()]);
    }
    
    public static synchronized DBTransactionScratchPad[] fetch(int module) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        DBTransactionScratchPad sp = null;
        Vector v = new Vector();
        try {
           // new Exception().printStackTrace();
            StringBuilder sb = new StringBuilder("select *");
            sb.append(" from ").append(SCRATCH_TABLE).append(" where ");
            sb.append(MODULE).append("=?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, module);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new DBTransactionScratchPad();
                sp._userid = r.getString(DISPLAY_NAME);
                sp._game_type = r.getInt(GAME_TYPE);
                sp._game_name = r.getString(GAME_NAME);
                sp._module = new ModuleType(r.getInt(MODULE));
                sp._session = r.getString(SESSION);
                sp._type = r.getString(TYPE);
                sp._play = r.getDouble(PLAY_CHIPS);
                sp._real = r.getDouble(REAL_CHIPS);
                sp._status = r.getString(STATUS);
                sp._ts = r.getTimestamp(TIMESTAMP);
                v.add(sp);
            }             
            r.close();
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
            throw new DBException(e.getMessage() + 
                                  " -- while retriving db player");
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
        return (DBTransactionScratchPad[])v.toArray(new DBTransactionScratchPad[v.size()]);
    }
    

        public static synchronized DBTransactionScratchPad[] getTransaction(String user) {
            Connection conn = null;
            PreparedStatement ps = null;
            DBTransactionScratchPad sp = null;
            Vector v = new Vector();
            try {
               // new Exception().printStackTrace();
                StringBuilder sb = new StringBuilder("select *");
                sb.append(" from ").append(SCRATCH_TABLE).append(" where ");
                sb.append(DISPLAY_NAME).append("=? ");
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, user);
                _cat.finest(sb.toString());
                ResultSet r = ps.executeQuery();
                while (r.next()) {
                    sp = new DBTransactionScratchPad();
                    sp._userid = user;
                    sp._game_type = r.getInt(GAME_TYPE);
                    sp._game_name = r.getString(GAME_NAME);
                    sp._module = new ModuleType(r.getInt(MODULE));
                    sp._session =  r.getString(SESSION);
                    sp._type = r.getString(TYPE);
                    sp._play = r.getDouble(PLAY_CHIPS);
                    sp._real = r.getDouble(REAL_CHIPS);
                    sp._status = r.getString(STATUS);
                    sp._ts = r.getTimestamp(TIMESTAMP);
                    v.add(sp);
                }             
                r.close();
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
            return (DBTransactionScratchPad[])v.toArray(new DBTransactionScratchPad[v.size()]);
        }
   
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("DBTransactionScratchPad: " + "\n");
    str.append("DisplayName = ").append(_userid).append("\n");
    str.append("Play = ").append(_play).append("\n");
    str.append("Real = ").append(_real).append("\n");
    str.append("Game Type = ").append(_type).append("\n");
    str.append("Game Name = ").append(_game_name).append("\n");
    str.append("Module = ").append(_module).append("\n");
    str.append("Session = ").append(_session).append("\n");
    str.append("Status = ").append(_status).append("\n");
    str.append("TimeStamp = ").append(_ts).append("\n");
    str.append("Comment = ").append(_comment).append("\n");
    return (str.toString());
  }

  

}

