package com.golconda.db;

import com.agneya.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoginSession {
    // set the category for logging
    static transient Logger _cat = 
        Logger.getLogger(LoginSession.class.getName());

    /** The value for the dispName field */
    private String displayName;

    /** The value for the sessionId field */
    private String sessionId;
    private String affiliateId;

    /** The value for the ip field */
    private String ip;
    private String bonus_code;

    private double startWorth;
    private double endWorth;
    private int games;
    private int gamesWon;
    private double winAmount;
    private double wagered;

    /** The value for the loginTime field */
    private java.util.Date loginTime;

    /** The value for the logoutTime field */
    private java.util.Date logoutTime;

    /** The value for games played */
    private long games_pl;

    /** The value for games won */
    private long games_won;

    /** The value for games lost */
    private long games_lost;

    /** The value for amount won/lost */
    private double amount_wl;

    /** The value for amount bet */
    private double amount_bet;

    /** the column name for the DISPLAY_NAME field */
    public static final String DISPLAY_NAME;

    /** the column name for the SESSION_ID field */
    public static final String SESSION_ID;

    /** the column name for the IP field */
    public static final String IP;
    public static final String REG_BONUS_CODE;
    public static final String START_WORTH;
    public static final String END_WORTH;
    public static final String GAMES;
    public static final String GAMES_WON;
    public static final String WIN_AMOUNT;
    public static final String WAGERED;

    /** the column name for the LOGIN_TIME field */
    public static final String LOGIN_TIME;

    /** the column name for the LOGOUT_TIME field */
    public static final String LOGOUT_TIME;
    public static final String AFFILIATE_ID_FK;

    static {
        DISPLAY_NAME = "USERID";
        SESSION_ID = "SESSION_ID";
        IP = "IP";
        REG_BONUS_CODE = "REG_BONUS_CODE";
        START_WORTH = "START_WORTH";
        END_WORTH = "END_WORTH";
        GAMES = "GAMES_PLAYED";
        GAMES_WON = "GAMES_WON";
        WIN_AMOUNT = "TOTAL_WIN_AMOUNT";
        WAGERED = "TOTAL_WAGERED_AMOUNT";
        LOGIN_TIME = "START_TIME";
        LOGOUT_TIME = "END_TIME";
        AFFILIATE_ID_FK = "AFFILIATE_ID_FK";
    }

    public LoginSession() {
    }

    public LoginSession(String dname) {
        displayName = dname;
    }

    public synchronized int save() throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
        if (_modified) {
            try {
                StringBuilder sb = 
                    new StringBuilder("insert into T_LOGIN_SESSION_LIVE ( ");
                sb.append(DISPLAY_NAME).append(",");
                sb.append(SESSION_ID).append(",");
                sb.append(AFFILIATE_ID_FK).append(",");
                sb.append(IP).append(",");
                sb.append(REG_BONUS_CODE).append(",");
                sb.append(START_WORTH).append(",");
                sb.append(END_WORTH).append(",");
                sb.append(GAMES).append(",");
                sb.append(GAMES_WON).append(",");
                sb.append(WIN_AMOUNT).append(",");
                sb.append(WAGERED).append(",");
                sb.append(LOGIN_TIME).append(",");
                sb.append(LOGOUT_TIME).append(")");
                sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                _cat.finest(sb.toString());
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, displayName);
                ps.setString(2, sessionId);
                ps.setString(3, affiliateId);
                ps.setString(4, ip);
                ps.setString(5, bonus_code);
                ps.setDouble(6, startWorth);
                ps.setDouble(7, endWorth);
                ps.setInt(8, games);
                ps.setInt(9, gamesWon);
                ps.setDouble(10, winAmount);
                ps.setDouble(11, wagered);
                ps.setTimestamp(12, 
                                new java.sql.Timestamp(loginTime.getTime()));
                ps.setTimestamp(13, 
                                new java.sql.Timestamp(logoutTime == null ? System.currentTimeMillis() : 
                                                       logoutTime.getTime()));
                r = ps.executeUpdate();
                ps.close();
                //conn.commit();
                conn.close();
                _cat.finest(this.toString());
            } catch (SQLException e) {
                _cat.log(Level.SEVERE, 
                         "Unable to save login session " + e.getMessage(), e);
                _cat.severe(this.toString());
                try {
                    if (ps != null) {
                        ps.close();
                        ps = null;
                    }
                    if (conn != null) {
                        conn.rollback();
                        conn.close();
                        conn = null;
                    }
                } catch (SQLException se) {
                    //ignore
                }
                throw new DBException(e.getMessage() + 
                                      "-----Unable to save login session ");
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                        ps = null;
                    }
                    if (conn != null) {
                        conn.close();
                        conn = null;
                    }
                } catch (SQLException se) {
                    //ignore
                }
            }

        }
        return r;
    }

    public synchronized int updateLogout() throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
        try {
            StringBuilder sb = 
                new StringBuilder("update T_LOGIN_SESSION_LIVE set ");
            sb.append(GAMES).append(" = ?, ");
            sb.append(GAMES_WON).append(" = ?, ");
            sb.append(WIN_AMOUNT).append(" = ?, ");
            sb.append(END_WORTH).append(" = ?, ");
            sb.append(WAGERED).append(" = ?, ");
            sb.append(LOGOUT_TIME).append(" = ? ");
            sb.append(" where ");
            sb.append(SESSION_ID).append("= ? ");
            _cat.finest(sb.toString());
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, games);
            ps.setInt(2, gamesWon);
            ps.setDouble(3, winAmount);
            ps.setDouble(4, endWorth);
            ps.setDouble(5, wagered);
            ps.setTimestamp(6, 
                            new java.sql.Timestamp(logoutTime == null ? System.currentTimeMillis() : 
                                                   logoutTime.getTime()));

            ps.setString(7, sessionId);
            r = ps.executeUpdate();
            ps.close();
            //conn.commit();
            conn.close();
            _cat.finest(this.toString());
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Session " + e.getMessage(), e);
            _cat.severe(this.toString());

            if (e.getMessage().endsWith("New request is not allowed to start because it should come with valid transaction descriptor")) {
                ConnectionManager.clearPool(conn);
            } else {
                try {
                    if (ps != null) {
                        ps.close();
                        ps = null;
                    }
                    if (conn != null) {
                        conn.close();
                        conn = null;
                    }
                } catch (SQLException se) {
                    //ignore
                }
            }
            throw new DBException(e.getMessage() + 
                                  "---Unable to update Player's Session ");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException se) {
                //ignore
            }
        }

        return r;
    }

    public synchronized void updateAddBatch(Statement ps) throws DBException {

        int r = -1;
        try {
            StringBuilder sb = 
                new StringBuilder("update T_LOGIN_SESSION_LIVE set ");
            sb.append(GAMES).append(" = ").append(games).append(", ");
            sb.append(GAMES_WON).append(" = ").append(gamesWon).append(", ");
            sb.append(WIN_AMOUNT).append(" = ").append(winAmount).append(", ");
            sb.append(LOGOUT_TIME).append(" = '").append(new Timestamp(System.currentTimeMillis())).append("', ");
            sb.append(WAGERED).append(" = ").append(wagered).append(" ");
            sb.append(" where ");
            sb.append(SESSION_ID).append(" ='").append(sessionId).append("'");
            _cat.finest(sb.toString());
            ps.addBatch(sb.toString());
        } catch (SQLException e) {
            try {
                if (ps != null) {
                    ps.getConnection().rollback();
                    ps.close();
                    ps = null;
                }
            } catch (SQLException se) {
                //ignore
            }
            throw new DBException(e.getMessage() + " ---Unable to save  GRS");
        } finally {
            try {
                if (ps != null) {
                    ps.getConnection().rollback();
                    ps.close();
                    ps = null;
                }
            } catch (SQLException se) {
                //ignore
            }
        }
    }

    public static LoginSession[] getLogins(Date date) {
        Connection conn = null;
        PreparedStatement ps = null;
        LoginSession sp = null;
        Vector v = new Vector();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // new Exception().printStackTrace();
            StringBuilder sb = new StringBuilder("select *");
            sb.append(" from T_LOGIN_SESSION_LIVE where date(START_TIME)=?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, sdf.format(date));
            _cat.finest(sb.toString() + sdf.format(date) + date.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new LoginSession();
                sp.displayName = r.getString(DISPLAY_NAME);
                sp.sessionId = r.getString(SESSION_ID);
                //sp.module = new ModuleType(r.getInt(MODULE));
                sp.ip = r.getString(IP);
                sp.loginTime = r.getTimestamp(LOGIN_TIME);
                sp.logoutTime = r.getTimestamp(LOGOUT_TIME);
                sp.startWorth = r.getDouble(START_WORTH);
                sp.endWorth = r.getDouble(END_WORTH);
                v.add(sp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get transaction " + e.getMessage(), e);

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
        return (LoginSession[])v.toArray(new LoginSession[v.size()]);
    }

    public static LoginSession[] getLogins(String user, int count) {
        Connection conn = null;
        PreparedStatement ps = null;
        LoginSession sp = null;
        Vector v = new Vector();
        try {
            // new Exception().printStackTrace();
            StringBuilder sb = new StringBuilder("select *");
            sb.append(" from T_LOGIN_SESSION_LIVE where ");
            sb.append(DISPLAY_NAME).append("=? order by START_TIME desc limit ").append(count);
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new LoginSession();
                sp.displayName = user;
                sp.sessionId = r.getString(SESSION_ID);
                //sp.module = new ModuleType(r.getInt(MODULE));
                sp.ip = r.getString(IP);
                sp.loginTime = r.getTimestamp(LOGIN_TIME);
                sp.logoutTime = r.getTimestamp(LOGOUT_TIME);
                sp.startWorth = r.getDouble(START_WORTH);
                sp.endWorth = r.getDouble(END_WORTH);
                v.add(sp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get transaction " + e.getMessage(), e);

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
        return (LoginSession[])v.toArray(new LoginSession[v.size()]);
    }


    /**
     * Get the DispName
     *
     * @return String
     */
    public String getDispName() {
        return displayName;
    }

    /**
     * Set the value of DispName
     *
     * @param v new value
     */
    public void setDispName(String v) {
        if (displayName == null || !displayName.equals(v)) {
            this.displayName = v;
            setModified(true);
        }

    }

    /**
     * Get the SessionId
     *
     * @return String
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Set the value of SessionId
     *
     * @param v new value
     */
    public void setSessionId(String v) {
        if (sessionId == null || !sessionId.equals(v)) {
            this.sessionId = v;
            setModified(true);
        }

    }

    public String getAffiliateId() {
        return affiliateId;
    }


    public void setAffiliateId(String v) {
        if (affiliateId == null || !affiliateId.equals(v)) {
            this.affiliateId = v;
            setModified(true);
        }

    }

    /**
     * Get the Ip
     *
     * @return String
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set the value of Ip
     *
     * @param v new value
     */
    public void setIp(String v) {
        if (ip == null || !ip.equals(v)) {
            this.ip = v;
            setModified(true);
        }
    }

    public String getBonusCode() {
        return bonus_code;
    }

    public void setBonusCode(String v) {
        if (bonus_code == null || !bonus_code.equals(v)) {
            this.bonus_code = v;
            setModified(true);
        }
    }

    public double getStartWorth() {
        return startWorth;
    }

    public void setStartWorth(double v) {
        if (startWorth != v) {
            this.startWorth = v;
            setModified(true);
        }
    }

    public double getEndWorth() {
        return endWorth;
    }

    public void setEndWorth(double v) {
        if (endWorth != v) {
            this.endWorth = v;
            setModified(true);
        }
    }

    public int getGames() {
        return games;
    }

    public void setGames(int v) {
        if (games != v) {
            this.games = v;
            setModified(true);
        }
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int v) {
        if (gamesWon != v) {
            this.gamesWon = v;
            setModified(true);
        }
    }

    public double getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(double v) {
        if (winAmount != v) {
            this.winAmount = v;
            setModified(true);
        }
    }

    public double getWagered() {
        return wagered;
    }

    public void setWagered(double v) {
        if (wagered != v) {
            this.wagered = v;
            setModified(true);
        }
    }

    /**
     * Get the LoginTime
     *
     * @return Date
     */
    public java.util.Date getLoginTime() {
        return loginTime;
    }

    /**
     * Set the value of LoginTime
     *
     * @param v new value
     */
    public void setLoginTime(java.util.Date v) {
        this.loginTime = v;
        setModified(true);
    }

    /**
     * Get the LogoutTime
     *
     * @return Date
     */
    public java.util.Date getLogoutTime() {
        return logoutTime;
    }

    /**
     * Set the value of LogoutTime
     *
     * @param v new value
     */
    public void setLogoutTime(java.util.Date v) {
        this.logoutTime = v;
        setModified(true);
    }
    
    public String getStringLoginTime(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	return sdf.format(loginTime);
    }
    public String getStringLogoutTime(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	return sdf.format(logoutTime);
    }
    
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("LoginSession:\n");
        str.append("DispName = ").append(getDispName()).append("\n");
        str.append("SessionId = ").append(getSessionId()).append("\n");
        str.append("Ip = ").append(getIp()).append("\n");
        str.append("StartWorth = ").append(getStartWorth()).append("\n");
        str.append("EndWorth = ").append(getEndWorth()).append("\n");
        str.append("Games = ").append(getGames()).append("\n");
        str.append("GamesWon = ").append(getGamesWon()).append("\n");
        str.append("WinAmount = ").append(getWinAmount()).append("\n");
        str.append("Wagered = ").append(getWagered()).append("\n");
        str.append("LoginTime = ").append(getLoginTime()).append("\n");
        str.append("LogoutTime = ").append(getLogoutTime()).append("\n");
        return (str.toString());
    }

    public void setModified(boolean val) {
        _modified = val;
    }

    public boolean _modified = false;

}
