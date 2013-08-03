package com.poker.common.db;

import com.agneya.util.ConnectionManager;

import com.agneya.util.MD5;

import com.agneya.util.Utils;

import com.golconda.db.DBException;
import com.golconda.db.DBTransaction;
import com.golconda.db.ModuleType;
import com.golconda.db.PlayerPreferences;
import com.golconda.db.RoleMask;

import com.golconda.db.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DBPokerPlayer {
    // set the category for logging
    static Logger _cat = Logger.getLogger(DBPokerPlayer.class.getName());


    public static final String DISPLAY_NAME;
    public static final String WIN;
    public static final String MAX_LOSS;
    public static final String MAX_WIN;
    public static final String DURATION;
    public static final String ALL_IN;
    public static final String ALL_IN_TS;

    protected String displayName;
    protected int max_loss;
    protected int max_win;
    protected java.sql.Timestamp duration;
    protected int allIn;
    protected java.sql.Timestamp allInTs;
    protected double win = 0;

    static {
        DISPLAY_NAME = "USERID";
        MAX_LOSS = "MAX_LOSS";
        MAX_WIN = "MAX_WIN";
        DURATION = "DURATION";
        ALL_IN = "ALL_IN";
        ALL_IN_TS = "ALL_IN_TS";
        WIN = "WIN";
    }

    public void setDisplayName(String v) {
        displayName = v;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getWinLimit() {
        return max_win;
    }

    public void setWinLimit(int v) {
        this.max_win = v;
    }

    public int getLossLimit() {
        return max_loss;
    }

    public void setLossLimit(int v) {
        this.max_loss = v;
    }

    public long getDuration() {
        return duration == null ? -1 : duration.getTime();
    }

    public void setDuration(long v) {
        this.duration = new java.sql.Timestamp(v);
    }

    public int getAllIn() {
        return allIn;
    }

    public void setAllIn(int v) {
        this.allIn = v;
    }

    public java.util.Date getAllInTs() {
        return allInTs;
    }

    public double getWin() {
        return win;
    }

    public void setWin(double v) {
        v = Utils.getRounded(v);
        this.win = v;
    }

    public void addWin(double v) {
        this.win += Utils.getRounded(v);
    }


    public DBPokerPlayer() {
    }
    
    public void init(){
        allIn=2;
        max_win = 9999999;
        max_loss = 99999999;
    }


    public synchronized boolean get(String user) throws DBException {
        if (user == null) {
            return false;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder("select *");
            sb.append(" from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary  ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                displayName = user;
                win = r.getDouble(WIN);
                max_loss = r.getInt(MAX_LOSS);
                max_win = r.getInt(MAX_WIN);
                duration = r.getTimestamp(DURATION);
                allIn = r.getInt(ALL_IN);
                allInTs = r.getTimestamp(ALL_IN_TS);

                r.close();
                ps.close();
                conn.close();
                _cat.finest(this.toString());
                return true;
            } else {
                r.close();
                ps.close();
                conn.close();
                return false;
            }
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "Unable to get player " + e.getMessage(), 
                     e);
            _cat.severe(this.toString());

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

    }


    public int save() throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
        try {
            StringBuilder sb = new StringBuilder("insert into T_USER (");
            sb.append(DISPLAY_NAME).append(",");
            sb.append(MAX_LOSS).append(",");
            sb.append(MAX_WIN).append(",");
            sb.append(WIN).append(",");
            sb.append(DURATION).append(",");
            sb.append(ALL_IN).append(",");
            sb.append(ALL_IN_TS).append(")");
            sb.append(" values (");
            sb.append(" ?, ?, ?, ?, ?, ?, ? )");
            _cat.finest(sb.toString());
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, displayName);
            ps.setInt(2, max_loss);
            ps.setInt(3, max_win);
            ps.setDouble(4, win);
            ps.setTimestamp(5, duration);
            ps.setInt(6, allIn);
            ps.setTimestamp(7, 
                            new java.sql.Timestamp(System.currentTimeMillis()));

            _cat.finest(this.toString());
            r = ps.executeUpdate();
            ps.close();


        } catch (SQLException e) {
            e.printStackTrace();
            _cat.severe("Unable to save Player info " + e.getMessage() + this);

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
                                  " -- while insertinf user " + displayName);
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

    public boolean resetAllIn() {
        Connection conn = null;
        PreparedStatement ps = null;

        if ((System.currentTimeMillis() - allInTs.getTime()) < 86400000) {
            return false;
        }

        if (allIn == 2) {
            return true;
        }

        allIn = 2;
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            //conn.setAutoCommit(false);

            StringBuilder sb = new StringBuilder("update T_USER set ");
            sb.append(ALL_IN).append(" = ? ,");
            sb.append(ALL_IN_TS).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, allIn);
            ps.setTimestamp(2, 
                            new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(3, displayName);

            int r = ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.close();
            _cat.finest("AllIn " + allIn + "Player = " + this);
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Play Wallet " + e.getMessage(), 
                     e);
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
            _cat.severe(e.getMessage() + " -- update of allin in DB failed");
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

        return true;
    }

    public int decrAllIn() {
        if (allIn == 0) {
            return -1;
        }
        Connection conn = null;
        PreparedStatement ps = null;

        allIn--;
        int r = -1;

        try {
            /**
                     * Update all in DB
                     */
            conn = ConnectionManager.getConnection("GameEngine");
            //conn.setAutoCommit(false);

            StringBuilder sb = new StringBuilder("update T_USER set ");
            sb.append(ALL_IN).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, allIn);
            ps.setString(2, displayName);
            r = ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.close();
            _cat.finest("AllIn " + allIn + "Player = " + this);
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Play Wallet " + e.getMessage(), 
                     e);
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
            _cat.severe(e.getMessage() + " -- update of allin in DB failed");
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

    public int[] getWinLoss(String affiliate_id) throws DBException {
        if (affiliate_id == null) {
            return null;
        }
        Connection conn = null;
        int[] winloss = new int[2];
        PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder("select max_win, max_loss ");
            sb.append(" from T_AFFILIATE where ");
            sb.append("USERID_FK like binary ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, affiliate_id);
            _cat.finest(sb + affiliate_id);
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                winloss[0] = r.getInt(1);
                winloss[1] = r.getInt(2);
                max_loss = winloss[0];
                max_win = winloss[1];
                r.close();
                ps.close();
                conn.close();
            }
            return winloss;
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "Unable to get player " + e.getMessage(), 
                     e);

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

    }


    public int updateWinLoss() throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
        try {
            /**
            * Create a transactio in the DBWallet
            */
            conn = ConnectionManager.getConnection("GameEngine");
            //conn.setAutoCommit(false);

            StringBuilder sb = new StringBuilder("update T_USER set ");
            sb.append(MAX_WIN).append(" = ?, ");
            sb.append(MAX_LOSS).append(" = ?, ");
            sb.append(WIN).append(" = ?, ");
            sb.append(DURATION).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, max_win);
            ps.setInt(2, max_loss);
            ps.setDouble(3, win);
            ps.setTimestamp(4, duration);
            ps.setString(5, displayName);
            r = ps.executeUpdate();
            ps.close();
            //ConnectionManager.returnConnection(conn, "GameEngine");
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Win Loss " + e.getMessage(), 
                     e);

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


    public static int updateWinLoss(String uid, int max_win, 
                                    int max_loss) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
        try {
            /**
           * Create a transactio in the DBWallet
           */
            conn = ConnectionManager.getConnection("GameEngine");
            //conn.setAutoCommit(false);

            StringBuilder sb = new StringBuilder("update T_USER set ");
            sb.append(MAX_WIN).append(" = ?, ");
            sb.append(MAX_LOSS).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, max_win);
            ps.setInt(2, max_loss);
            ps.setString(3, uid);
            r = ps.executeUpdate();
            ps.close();
            //ConnectionManager.returnConnection(conn, "GameEngine");
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Win Loss " + e.getMessage(), 
                     e);

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


    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Player:\n");
        str.append("DispName = ").append(getDisplayName()).append("\n");
        str.append("All-In = ").append(getAllIn()).append("\n");
        str.append("All-In TS = ").append(getAllInTs()).append("\n");
        str.append("max_win = ").append(getWinLimit()).append("\n");
        str.append("max_loss = ").append(getLossLimit()).append("\n");
        str.append("win = ").append(getWin()).append("\n");
        str.append("duration = ").append(getDuration()).append("\n");

        return (str.toString());
    }


}
