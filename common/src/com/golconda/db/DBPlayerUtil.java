package com.golconda.db;

import com.agneya.util.ConnectionManager;

import com.agneya.util.MD5;
import com.agneya.util.Password;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

import javax.management.relation.Role;


public class DBPlayerUtil extends DBPlayer {
    public DBPlayerUtil() {
    }


    public static synchronized boolean exists(String user) throws DBException {
        if (user == null) {
            return false;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            StringBuilder sb = 
                new StringBuilder("select userid from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
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


    public static DBPlayer[] getLatestPlayPlayers(int count) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        int pref = -1;
        try {
            /**
               * Create a transactio in the DBWallet
               */
            conn = ConnectionManager.getConnection("GameEngine");
            //conn.setAutoCommit(false);
            StringBuilder sb = 
                new StringBuilder("select * from T_USER where emailid not like '%test.com' order by  reg_timestamp desc limit " + 
                                 count);
            ps = conn.prepareStatement(sb.toString());
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.city = r.getString(CITY);
                dp.gender = r.getInt(GENDER);
                dp.avatar = r.getString(USER_AVATAR);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }


    public static String generateValidationCode(String name) {
        Connection conn = null;
        PreparedStatement ps = null;

        String vid = null;
        try {
            vid = _rng.getNewSessionId();

            StringBuilder sb = new StringBuilder("update T_USER set ");
            sb.append(VALIDATEID).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString() + vid);
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, vid);
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "Validation failed " + e.getMessage(), e);

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
        return vid;
    }

    public static int validateEmail(String displayName, String vid) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {

            StringBuilder sb = new StringBuilder("select ");
            sb.append(VALIDATEID);
            sb.append(" from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary  ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, displayName);
            _cat.finest(sb.toString());
            String pe = null;
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                pe = r.getString(VALIDATEID);
            }
            r.close();
            ps.close();

            if (pe == null) {
                _cat.severe("No player by this userid " + displayName);
                conn.close();
                return -1;
            }
            if (!pe.equals(vid)) {
                _cat.severe(vid + " Validation code did not match " + pe);
                return -2;
            }

            sb = new StringBuilder("update T_USER set ");
            sb.append(VALIDATEID).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString() + vid);
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, "true");
            ps.setString(2, displayName);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "validate update failed " + e.getMessage(), 
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
        return 1;
    }

    public static String generatePassword(String name, String email) {
        Connection conn = null;
        PreparedStatement ps = null;

        String new_password = null;
        try {

            StringBuilder sb = new StringBuilder("select ");
            sb.append(EMAIL_ID);
            sb.append(" from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary  ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            _cat.finest(sb.toString());
            String pe = null;
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                pe = r.getString(EMAIL_ID);
            }
            r.close();
            ps.close();

            if (pe == null) {
                _cat.severe("Email and userid did not match for player " + 
                            name);
                conn.close();
                return "__FAILED_";
            }
            if (!pe.equals(email)) {
                return "__AUTH_FAILED_";
            }

            Password p = new Password();
            p.generatePassword();
            new_password = p.getPassword();

            sb = new StringBuilder("update T_USER set ");
            sb.append(PASSWORD).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString() + new_password);
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, MD5.encode(new_password));
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "Password update failed " + e.getMessage(), 
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
        return new_password;
    }

    /* --by rk-- */
    public static String generateNewPassword(String name, String email) {
        Connection conn = null;
        PreparedStatement ps = null;

        String new_password = null;
        try {

            Password p = new Password();
            p.generatePassword();
            new_password = p.getPassword();
            
            System.out.println("New generated password="+new_password);
        } catch (Exception e) {
            _cat.log(Level.SEVERE, "New Password generation failed " + e.getMessage(), 
                     e);

        } finally {
            
        }
        return new_password;
    }

    public static String changePassword(String name, String old_password, 
                                        String new_password) {
        Connection conn = null;
        PreparedStatement ps = null;
        System.out.println("OP=" + old_password + ", NP=" + new_password);
        String op = null;
        try {
            StringBuilder sb = new StringBuilder("select ");
            sb.append(PASSWORD);
            sb.append(" from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary  ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            _cat.finest(sb.toString());
            System.out.println(sb.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                op = r.getString(PASSWORD);
            }
            r.close();
            ps.close();

            if (op == null) {
                _cat.severe("Unable to get password from the DB for player " + 
                            name);
                conn.close();
                return "__FAILED_";
            }
            if (!MD5.encode(old_password).equals(op)) {
                return "__AUTH_FAILED_";
            }

            sb = new StringBuilder("update T_USER set ");
            sb.append(PASSWORD).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            System.out.println(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, MD5.encode(new_password));
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get  player " + e.getMessage(), e);

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
        return new_password;
    }
    
    public static String changePwd(String name, String old_password, String new_password) {
			Connection conn = null;
			PreparedStatement ps = null;
			System.out.println("OP=" + old_password + ", NP=" + new_password);
			String op = null;
			try {
			StringBuilder sb = new StringBuilder("select ");
			sb.append(PASSWORD);
			sb.append(" from T_USER where ");
			sb.append(DISPLAY_NAME).append(" like binary  ?");
			conn = ConnectionManager.getConnection("GameEngine");
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, name);
			_cat.finest(sb.toString());
			System.out.println(sb.toString());
			ResultSet r = ps.executeQuery();
			if (r.next()) {
			op = r.getString(PASSWORD);
			}
			r.close();
			ps.close();
			
			if (op == null) {
			_cat.severe("Unable to get password from the DB for player " + 
			name);
			conn.close();
			return "__USER_NOT_EXIST_";
			}
			if (!MD5.encode(old_password).equals(op)) {
			return "__OLD_PWD_WRONG_";
			}
			
			sb = new StringBuilder("update T_USER set ");
			sb.append(PASSWORD).append(" = ? ");
			sb.append(" where ");
			sb.append(DISPLAY_NAME).append("= ?");
			_cat.finest(sb.toString());
			System.out.println(sb.toString());
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, MD5.encode(new_password));
			ps.setString(2, name);
			ps.executeUpdate();
			ps.close();
			conn.close();
			} catch (SQLException e) {
			_cat.log(Level.SEVERE, 
			"Unable to get player " + e.getMessage(), e);
			
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
			return "__DB_EXCEPTION_";
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
			return new_password;
	}



    public static String changePassword(String name, String new_password) {
        Connection conn = null;
        PreparedStatement ps = null;
        System.out.println("NP=" + new_password);
        String op = null;
        try {
            StringBuilder sb = new StringBuilder("update T_USER set ");
            sb.append(PASSWORD).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, MD5.encode(new_password));
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "Unable to change player " + e.getMessage(), 
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
        return new_password;
    }


    public static void modifyPreferences(String name, int new_pref, 
                                         boolean add) {
        Connection conn = null;
        PreparedStatement ps = null;

        int pref = -1;
        try {
            /**
       * Create a transactio in the DBWallet
       */
            StringBuilder sb = new StringBuilder("select ");
            sb.append(PREFERENCES);
            sb.append(" from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary ?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                pref = r.getInt(PREFERENCES);
            }
            r.close();
            ps.close();

            if (pref == -1) {
                _cat.severe("Unable to get prefrences from the DB for player " + 
                            name);
                conn.close();
                return;
            }

            sb = new StringBuilder("update T_USER set ");
            sb.append(PREFERENCES).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, add ? (pref | new_pref) : (pref & ~new_pref));
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to ban/unban player " + e.getMessage(), e);

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


    public static DBPlayer[] getTopPlayPlayers(int count) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        int pref = -1;
        try {
            /**
        * Create a transactio in the DBWallet
        */
            conn = ConnectionManager.getConnection("GameEngine");
            //conn.setAutoCommit(false);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_YEAR, 1);
            SimpleDateFormat simpleDateFormat = 
                new SimpleDateFormat("yyyy/MM/dd");
            String now = simpleDateFormat.format(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, -7);
            String week_back = simpleDateFormat.format(c.getTime());

            StringBuilder sb = 
                new StringBuilder("select * from T_USER where emailid not like '%test.com' and ");
            sb.append(" reg_timestamp between '");
            sb.append(week_back).append("' and '").append(now);
            sb.append("' order by  play_chips desc limit " + count);
            ps = conn.prepareStatement(sb.toString());
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.city = r.getString(CITY);
                dp.gender = r.getInt(GENDER);
                dp.avatar = r.getString(USER_AVATAR);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }

    public static int updatePlayerRank(String uid, 
                                       int rank) throws DBException {
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
            sb.append(RANK).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ?");
            _cat.finest(sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, rank);
            ps.setString(2, uid);
            r = ps.executeUpdate();
            ps.close();
            //ConnectionManager.returnConnection(conn, "GameEngine");
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Rank " + e.getMessage(), e);

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


    public int updatePreferences() throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
        try {
            StringBuilder sb = new StringBuilder("update T_USER  set ");
            sb.append(PREFERENCES).append(" = ? ");
            sb.append(" where ");
            sb.append(DISPLAY_NAME).append("= ? ");
            _cat.finest(sb.toString());
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, preferences.intVal());
            ps.setString(2, displayName);
            r = ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.close();
            _cat.finest(this.toString());
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's preferences " + e.getMessage(), 
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


    public static DBPlayer[] getSearchPlayers(String pattern, 
                                              PlayerPreferences pf) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        int pref = -1;
        try {
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = 
                new StringBuilder("select * from T_USER where userid  like '%" + 
                                 pattern + "%' ");
            sb.append("order by userid");
            if (pf != null) {
                sb.append("and ").append(PREFERENCES).append(" & ").append(pf.intVal()).append(" > 0");
            }
            sb.append(" limit 200");
            ps = conn.prepareStatement(sb.toString());
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.emailId = r.getString(EMAIL_ID);
                dp.fname = r.getString(FIRST_NAME);
                dp.lname = r.getString(LAST_NAME);
                dp.location = r.getString(LOCATION);
                dp.city = r.getString(CITY);
                dp.affiliate = r.getString(AFFILIATE);
                dp.gender = r.getInt(GENDER);
                dp.dob = r.getDate(DOB);
                dp.affiliate = r.getString(AFFILIATE);
                dp.colluder = r.getInt(COLLUDER);
                dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.rank = r.getInt(RANK);
                dp.retentionFactor = r.getInt(RETENTION_FACTOR);
                dp.regTs = r.getTimestamp(REG_TS);
                dp.bonusCode = r.getString(BONUS_CODE);
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
                //piId = r.getInt(PI_ID);
                //profileId = r.getInt(PROFILE_ID);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
                dp.realChips = r.getDouble(REAL_CHIPS);
                dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
                dp.rakeBack =r.getInt(RAKE_BACK);
                dp.avatar = ("".equals(r.getString(USER_AVATAR)) || (r.getString(USER_AVATAR) == null))?"1" :r.getString(USER_AVATAR) ;
                dp.emailValidated = r.getString(VALIDATEID);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }

    public static DBPlayer[] getAllAdmins() {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        int pref = -1;
        try {
            /**
        * Create a transactio in the DBWallet
        */
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = 
                new StringBuilder("select * from T_USER where role_mask & 0xFFFF0 > 0 ");
            ps = conn.prepareStatement(sb.toString());
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }


    public static DBPlayer[] getAllPlayers() {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        int pref = -1;
        try {
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = new StringBuilder("select * from T_USER ");
            ps = conn.prepareStatement(sb.toString());
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.emailId = r.getString(EMAIL_ID);
                dp.fname = r.getString(FIRST_NAME);
                dp.lname = r.getString(LAST_NAME);
                dp.location = r.getString(LOCATION);
                dp.city = r.getString(CITY);
                dp.affiliate = r.getString(AFFILIATE);
                dp.gender = r.getInt(GENDER);
                dp.dob = r.getDate(DOB);
                dp.affiliate = r.getString(AFFILIATE);
                dp.colluder = r.getInt(COLLUDER);
                dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.rank = r.getInt(RANK);
                dp.retentionFactor = r.getInt(RETENTION_FACTOR);
                dp.regTs = r.getTimestamp(REG_TS);
                dp.bonusCode = r.getString(BONUS_CODE);
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
                //piId = r.getInt(PI_ID);
                //profileId = r.getInt(PROFILE_ID);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
                dp.realChips = r.getDouble(REAL_CHIPS);
                dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
                dp.avatar = r.getString(USER_AVATAR);
                dp.emailValidated = r.getString(VALIDATEID);
                dp.rakeBack = r.getInt(RAKE_BACK);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }
    
    public static int addTransaction(DBPlayer p, String session, ModuleType mod, double amt, double play, double real, long trans_type, String comment) throws DBException  {
        DBTransaction dbt = new DBTransaction();
        dbt._type = new TransactionType(trans_type);
        dbt._userid = p.getDisplayName();
        dbt._affiliateid = p.getAffiliate();
        dbt._session = session;
        dbt._bonus_code = p.getBonusCode();
        if (mod==null) mod=new ModuleType(ModuleType.LOBBY);
        dbt._module = mod;
        dbt._currency = "POINTS";
        dbt._comment = comment;
        dbt._amount = amt;
        dbt._playChips = play;
        dbt._realChips = real;
        dbt._points = 0;
        
        return dbt.saveTrans();
    }
    public static DBPlayer[] getAgentPlayersList(String affiliateName, String pattern) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();
		try {
		conn = ConnectionManager.getConnection("GameEngine");
		StringBuilder sb = new StringBuilder("select * from T_USER where affiliate_id_fk=?");
		sb.append(" and userid like '%" +  pattern  +"%'");
		sb.append(" and ");
		sb.append("ROLE_MASK=?");
		ps = conn.prepareStatement(sb.toString());
		ps.setString(1,affiliateName);
		ps.setInt(2,RoleMask.PLAYER);
		_cat.info(sb.toString());
		ResultSet r = ps.executeQuery();
		while (r.next()) {
		DBPlayer dp = new DBPlayer();
		dp.displayName = r.getString(DISPLAY_NAME);
		dp.emailId = r.getString(EMAIL_ID);
		dp.fname = r.getString(FIRST_NAME);
		dp.lname = r.getString(LAST_NAME);
		dp.location = r.getString(LOCATION);
		dp.city = r.getString(CITY);
		dp.affiliate = r.getString(AFFILIATE);
		dp.gender = r.getInt(GENDER);
		dp.dob = r.getDate(DOB);
		dp.colluder = r.getInt(COLLUDER);
		dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
		dp.rank = r.getInt(RANK);
		dp.retentionFactor = r.getInt(RETENTION_FACTOR);
		dp.regTs = r.getTimestamp(REG_TS);
		dp.bonusCode = r.getString(BONUS_CODE);
		dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
		//piId = r.getInt(PI_ID);
		//profileId = r.getInt(PROFILE_ID);
		dp.playChips = r.getDouble(PLAY_CHIPS);
		dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
		dp.realChips = r.getDouble(REAL_CHIPS);
		dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
		dp.avatar = r.getString(USER_AVATAR);
		dp.emailValidated = r.getString(VALIDATEID);
		dbp.add(dp);
		}
		r.close();
		ps.close();
		conn.close();
		} catch (SQLException e) {
		_cat.log(Level.SEVERE,"Unable to get players " + e.getMessage(), e);
		
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
		return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
		}
    
    public static DBPlayer[] getSubAgents(String affiliateName, String pattern) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();
		try {
		conn = ConnectionManager.getConnection("GameEngine");
		StringBuilder sb = new StringBuilder("select * from T_USER where affiliate_id_fk=?");
		sb.append(" and userid like '%"+pattern+"%'");
		sb.append(" and role_mask=").append(RoleMask.PLAYER + RoleMask.AFFILIATE);
		ps = conn.prepareStatement(sb.toString());
		ps.setString(1,affiliateName);
		_cat.info(sb.toString());
		ResultSet r = ps.executeQuery();
		while (r.next()) {
		DBPlayer dp = new DBPlayer();
		dp.displayName = r.getString(DISPLAY_NAME);
		dp.emailId = r.getString(EMAIL_ID);
		dp.fname = r.getString(FIRST_NAME);
		dp.lname = r.getString(LAST_NAME);
		dp.location = r.getString(LOCATION);
		dp.city = r.getString(CITY);
		dp.affiliate = r.getString(AFFILIATE);
		dp.gender = r.getInt(GENDER);
		dp.dob = r.getDate(DOB);
		dp.colluder = r.getInt(COLLUDER);
		dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
		dp.rank = r.getInt(RANK);
		dp.retentionFactor = r.getInt(RETENTION_FACTOR);
		dp.regTs = r.getTimestamp(REG_TS);
		dp.bonusCode = r.getString(BONUS_CODE);
		dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
		//piId = r.getInt(PI_ID);
		//profileId = r.getInt(PROFILE_ID);
		dp.playChips = r.getDouble(PLAY_CHIPS);
		dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
		dp.realChips = r.getDouble(REAL_CHIPS);
		dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
		dp.avatar = r.getString(USER_AVATAR);
		dp.emailValidated = r.getString(VALIDATEID);
		dbp.add(dp);
		}
		r.close();
		ps.close();
		conn.close();
		} catch (SQLException e) {
		_cat.log(Level.SEVERE,"Unable to get players " + e.getMessage(), e);
		
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
		return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
		}
    public static DBPlayer[] getSubAgentsAndPlayers(String affiliateName) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        int pref = -1;
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder sb =  new StringBuilder("select * from T_USER where affiliate_id_fk=? ");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1,affiliateName);
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
               /* dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));*/
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get agents " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }
    
   
    
    
    public static DBPlayer[] getReferrarDetails(String refName) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        try {
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = new StringBuilder("select * from T_USER ");
            sb.append("WHERE USERID=?");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, refName);
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.emailId = r.getString(EMAIL_ID);
                dp.fname = r.getString(FIRST_NAME);
                dp.lname = r.getString(LAST_NAME);
                dp.location = r.getString(LOCATION);
                dp.city = r.getString(CITY);
                dp.affiliate = r.getString(AFFILIATE);
                dp.gender = r.getInt(GENDER);
                dp.dob = r.getDate(DOB);
                dp.affiliate = r.getString(AFFILIATE);
                dp.colluder = r.getInt(COLLUDER);
                dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.rank = r.getInt(RANK);
                dp.retentionFactor = r.getInt(RETENTION_FACTOR);
                dp.regTs = r.getTimestamp(REG_TS);
                dp.bonusCode = r.getString(BONUS_CODE);
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
                //piId = r.getInt(PI_ID);
                //profileId = r.getInt(PROFILE_ID);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
                dp.realChips = r.getDouble(REAL_CHIPS);
                dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
                dp.avatar = r.getString(USER_AVATAR);
                dp.emailValidated = r.getString(VALIDATEID);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }

    public static DBPlayer[] getAbsolutePlayer(String absName) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        try {
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = new StringBuilder("select * from T_USER ");
            sb.append("WHERE USERID=?");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, absName);
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.emailId = r.getString(EMAIL_ID);
                dp.fname = r.getString(FIRST_NAME);
                dp.lname = r.getString(LAST_NAME);
                dp.location = r.getString(LOCATION);
                dp.city = r.getString(CITY);
                dp.affiliate = r.getString(AFFILIATE);
                dp.gender = r.getInt(GENDER);
                dp.dob = r.getDate(DOB);
                dp.affiliate = r.getString(AFFILIATE);
                dp.colluder = r.getInt(COLLUDER);
                dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.rank = r.getInt(RANK);
                dp.retentionFactor = r.getInt(RETENTION_FACTOR);
                dp.regTs = r.getTimestamp(REG_TS);
                dp.bonusCode = r.getString(BONUS_CODE);
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
                //piId = r.getInt(PI_ID);
                //profileId = r.getInt(PROFILE_ID);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
                dp.realChips = r.getDouble(REAL_CHIPS);
                dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
                dp.avatar = r.getString(USER_AVATAR);
                dp.emailValidated = r.getString(VALIDATEID);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }
    
    public static DBPlayer[] getSearchByFullName(String firstName,String lastName) {
        Connection conn = null;
        PreparedStatement ps = null;
        Vector dbp = new Vector();

        try {
            conn = ConnectionManager.getConnection("GameEngine");

            StringBuilder sb = new StringBuilder("select * from T_USER ");
            sb.append("WHERE FIRSTNAME LIKE '%" + firstName + "%' ");
            sb.append("AND LASTNAME LIKE '%" + lastName + "%' " );
            ps = conn.prepareStatement(sb.toString());
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DBPlayer dp = new DBPlayer();
                dp.displayName = r.getString(DISPLAY_NAME);
                dp.emailId = r.getString(EMAIL_ID);
                dp.fname = r.getString(FIRST_NAME);
                dp.lname = r.getString(LAST_NAME);
                dp.location = r.getString(LOCATION);
                dp.city = r.getString(CITY);
                dp.affiliate = r.getString(AFFILIATE);
                dp.gender = r.getInt(GENDER);
                dp.dob = r.getDate(DOB);
                dp.affiliate = r.getString(AFFILIATE);
                dp.colluder = r.getInt(COLLUDER);
                dp.role_mask = new RoleMask(r.getInt(ROLE_MASK));
                dp.rank = r.getInt(RANK);
                dp.retentionFactor = r.getInt(RETENTION_FACTOR);
                dp.regTs = r.getTimestamp(REG_TS);
                dp.bonusCode = r.getString(BONUS_CODE);
                dp.preferences = new PlayerPreferences(r.getInt(PREFERENCES));
                //piId = r.getInt(PI_ID);
                //profileId = r.getInt(PROFILE_ID);
                dp.playChips = r.getDouble(PLAY_CHIPS);
                dp.playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
                dp.realChips = r.getDouble(REAL_CHIPS);
                dp.realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
                dp.avatar = r.getString(USER_AVATAR);
                dp.emailValidated = r.getString(VALIDATEID);
                dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer[])dbp.toArray(new DBPlayer[dbp.size()]);
    }
    
    

}
