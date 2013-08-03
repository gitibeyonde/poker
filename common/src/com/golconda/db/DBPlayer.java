package com.golconda.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;
import com.agneya.util.ConnectionManager;
import com.agneya.util.MD5;
import com.agneya.util.Rng;
import com.agneya.util.Utils;


// SQLSERVER/ORACLE

public class DBPlayer {
    // set the category for logging
    static Logger _cat = Logger.getLogger(DBPlayer.class.getName());

    static Rng _rng = new Rng();

    public static final String FIRST_NAME;
    public static final String LAST_NAME;
    public static final String DISPLAY_NAME;
    public static final String PASSWORD;
    public static final String EMAIL_ID;
    public static final String GENDER;
    public static final String DOB;
    public static final String RANK;
    public static final String LOCATION;
    public static final String STATUS;
    public static final String CITY;
    public static final String AFFILIATE;
    public static final String COLLUDER;
    public static final String REMEMBER;
    public static final String RETENTION_FACTOR;
    public static final String REG_TS;
    public static final String ROLE_MASK;
    public static final String BONUS_CODE;
    public static final String PREFERENCES;

    public static final String PLAY_CHIPS;
    public static final String PLAY_CHIPS_TS;

    public static final String POINTS;
    public static final String POINTS_TS;

    public static final String REAL_CHIPS;
    public static final String REAL_CHIPS_TS;

    public static final String SECURITY_Q;
    public static final String SECURITY_P;
        
    public static final String USER_AVATAR;
    public static final String VALIDATEID;
    public static final String RAKE_BACK;


    protected String displayName;
    protected String password;
    protected String emailId;
    protected String fname;
    protected String lname;
    protected int gender;
    protected String city;
    protected String location;
    protected String status;
    protected java.util.Date dob;
    protected String affiliate;
    protected String provider;
    protected RoleMask role_mask= new RoleMask(RoleMask.PLAYER);
    protected int rank;
    protected int colluder;
    protected boolean remember;
    protected int retentionFactor;
    protected java.sql.Timestamp regTs;
    protected String referrer;
    protected int piId;
    protected int profileId;
    protected String bonusCode;
    protected PlayerPreferences preferences=new PlayerPreferences(PlayerPreferences.DEFAULT_MASK);

    protected double playChips;
    protected java.sql.Timestamp playChipsTs;

    protected double realChips;
    protected java.sql.Timestamp realChipsTs;

    protected double points;
    protected java.sql.Timestamp pointsTs;
    
    protected String security_q;
    protected String security_p;
    
    protected String avatar;
    protected String emailValidated;
    protected int rakeBack;

	DBAddress address;


    static {
        FIRST_NAME = "FIRSTNAME";
        LAST_NAME = "LASTNAME";
        DISPLAY_NAME = "USERID";
        PASSWORD = "PASSWORD";
        EMAIL_ID = "EMAILID";
        GENDER = "GENDER";
        DOB = "DOB";
        RANK = "RANK";
        REMEMBER = "REMEMBER";
        AFFILIATE = "AFFILIATE_ID_FK";
        COLLUDER = "COLLUDER_FACTOR";
        RETENTION_FACTOR = "RETENTION_FACTOR";
        REG_TS = "REG_TIMESTAMP";
        ROLE_MASK = "ROLE_MASK";
        BONUS_CODE = "REG_BONUS_CODE";
        PREFERENCES = "PREFERENCES";
        LOCATION = "LOCATION";
        STATUS = "MYSTATUS";
        CITY = "CITY";
        PLAY_CHIPS = "PLAY_CHIPS";
        PLAY_CHIPS_TS = "PLAY_CHIPS_TIMESTAMP";

        REAL_CHIPS = "REAL_CHIPS";
        REAL_CHIPS_TS = "REAL_CHIPS_TIMESTAMP";
        
        POINTS = "POINTS";
        POINTS_TS = "POINTS_TIMESTAMP";
        
        SECURITY_Q = "SECURITY_Q";
        SECURITY_P = "SECURITY_P";
        
        USER_AVATAR = "USER_AVATAR";
        VALIDATEID = "VALIDATEID";
        RAKE_BACK= "RAKEBACK";
    }

    static boolean _logPlayGames = false;
    public static short PLAYER_ROLE=1;
    public static short AFFILIATE_ROLE=2;
    
    
    static {
        try {
            _logPlayGames = 
                    Configuration.getInstance().getBoolean("Auditor.Log.PlayGame");
        } catch (ConfigurationException e) {
            _cat.log( Level.SEVERE,"Configuration exception ", e);
        }
    }

    public DBPlayer() {
    }
    
    // ONly for testing
    public DBPlayer(double pc, double rc){
        playChips = pc;
        realChips = rc;
    }

    public DBPlayer(String user, String pass) {
        displayName = user;
        password = MD5.encode(pass);
    }

    
    public String getEmailValidate(){       return emailValidated;   }    public void setEmailValidate(String vid){        emailValidated=vid;    }

    public int getLimitViolation() {
        /**if (duration == null || (max_win < 100 && max_loss < 100)) {
      return 0;
         }
         if (win > max_win) {
      // limit violated
      return 1;
         }
         else
         if (win < max_loss) {
      return -1;
         }
         else {**/
        return 0;
        //}
    }
    
   
    
   
	
	
    public String getGenderString(){ return gender ==0 ? "Female" : "Male"; }  public void setGenderString(String c){ gender = c.equals("Female") ? 0 : 1 ;}
             
    public void setDisplayName(String v) {        displayName = v;    }    public String getDisplayName() {        return displayName;    }
    public String getPassword() {        return password;    }    public void setPassword(String v) {        password = v;    }
    public String getFirstName() {        return fname;    }    public void setFirstName(String v) {            this.fname = v;    }
    public String getLastName() {        return lname;    }    public void setLastName(String v) {           this.lname = v;    }
    public String getCity() {        return city;    }    public void setCity(String v) {      if(v != null)  v = v.trim();            this.city = v;    }
    public String getLocation() {        return location==null ? "GMT" : location.trim();    }
    public void setLocation(String v) {        if (v==null)return;        v = v.trim();            this.location = v;    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String v) { 
    	if(v != null){
    		v = v.trim();        
    		this.avatar = v;
    	}else{
    		this.avatar = "1";
    	}
    }
    public String getStatus() {        return (status==null || status.length() < 1 ? "What's on your mind?" : status);    }
    public void setStatus(String v) {        if (v==null)return;        v = v.trim();            this.status = v;    }
    public String getEmailId() {        return emailId;    }    public void setEmailId(String v) {     this.emailId = v;    }
    public String getAffiliate() {        return affiliate;    }    public void setAffiliate(String v) {            this.affiliate = v;    }
    public String getBonusCode() {        return bonusCode;    }    public void setBonusCode(String v) {            this.bonusCode = v;    }
    public int getGender() {        return gender;    }    public void setGender(int v) {            this.gender = v;    }
    public java.util.Date getDob() {        return dob;    }    public void setDob(java.util.Date v) {            this.dob = v;    }
    public int getColluder() {        return colluder;    } public void setColluder(int v) {            this.colluder = v;    }
    public RoleMask getRoles() {        return role_mask;    }    public void setRoles(RoleMask v) {            this.role_mask = v;    }
    public boolean getRemember() {        return remember;    }    public void setRemember(boolean v) {            this.remember = v;    }
    public int getRetentionFactor() {        return retentionFactor;    }    public void setRetentionFactor(int v) {            this.retentionFactor = v;    }
    public PlayerPreferences getPreferences() { return preferences;}public void setPreferences(PlayerPreferences v) {this.preferences = v;}
    public void setPreferences(int v) {this.preferences = new PlayerPreferences(v);}
    public java.util.Date getRegTs() {        return regTs;    }    public void setRegTs(long v) {        this.regTs = new java.sql.Timestamp(v);    }
    public String getReferrer() {        return referrer;    }    public void setReferrer(String v) {            this.referrer = v;    }
    public int getPiId() {        return piId;    }    public void setPiId(int v) {            this.piId = v;    }
    public int getRank() {        return rank;    }    public void setRank(int v) {            this.rank = v;    }
    public int getProfileId() {        return profileId;    }    public void setProfileId(int v) {            this.profileId = v;    }

    public String getSecurityQ() {        return security_q;    }    public void setSecurityQ(String v) {     this.security_q = v;    }
    public String getSecurityP() {        return security_p;    }    public void setSecurityP(String v) {            this.security_p = v;    }
    
    public java.util.Date getPointsTs() {             return pointsTs;         }
    public void setPoints(double points) {	this.points = points;	}

	public double getPoints() {             return points;         }
    public String getPointsString() {             return Utils.getRoundedDollarCent(points);         }
    public int getRakeBack() {		return rakeBack;	}
	public void setRakeBack(int rakeBack) {		this.rakeBack = rakeBack;	}
    public String getRegTsString() { 
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(regTs);
    }
    
    public String getDobString() { 
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(dob);    
    }
    
    public boolean get(String user, String passwd, 
                       String affl) throws DBException {

        boolean player_authenticated = false;
        _cat.finest("Affiliate =" + affl);

        // try the default login
        setAffiliate(affl);
        player_authenticated = get(user, passwd);
        //if (!passwd.equals("facebook") && !affl.equals(affiliate) && !affiliate.equals("admin")) {
          //  player_authenticated = false;
        //}
        return player_authenticated;
    }
    
    
  
    public synchronized boolean get(String user, 
                                    String pass) throws DBException {
        if (user == null || pass == null || user.equals("null") || pass.equals("null")) {
            return false;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder("select *");
            sb.append(" from T_USER where ");
            sb.append(DISPLAY_NAME).append(" like binary ? and ");
            sb.append(PASSWORD).append("=?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            ps.setString(2, MD5.encode(pass));
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                displayName = user;
                password = pass;
                try {
	                emailId = r.getString(EMAIL_ID);
	                fname = r.getString(FIRST_NAME);
	                lname = r.getString(LAST_NAME);
	                location = r.getString(LOCATION);
	                status = r.getString(STATUS);
	                city = r.getString(CITY);
	                if (!r.getString(AFFILIATE).equals(affiliate)) {
	                    _cat.info("Affiliate in the database " + 
	                               r.getString(AFFILIATE) + 
	                               " is not same as the passed affiliate " + 
	                               affiliate+"   user "+displayName);
	                }
	                affiliate = r.getString(AFFILIATE);
	                gender = r.getInt(GENDER);
	                dob = r.getDate(DOB);
	                //affiliate = r.getString(AFFILIATE);
	                colluder = r.getInt(COLLUDER);
	                role_mask = new RoleMask(r.getInt(ROLE_MASK));
	                rank = r.getInt(RANK);
	                remember = r.getBoolean(REMEMBER);
	                retentionFactor = r.getInt(RETENTION_FACTOR);
	                regTs = r.getTimestamp(REG_TS);
	                bonusCode = r.getString(BONUS_CODE);
	                preferences = new PlayerPreferences( r.getInt(PREFERENCES));
	                //piId = r.getInt(PI_ID);
	                //profileId = r.getInt(PROFILE_ID);
	                playChips = r.getDouble(PLAY_CHIPS);
	                playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);
	
	                realChips = r.getDouble(REAL_CHIPS);
	                realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
	
	                points = r.getDouble(POINTS);
	                pointsTs = r.getTimestamp(POINTS_TS);
	
	                security_q = r.getString(SECURITY_Q);
	                security_p = r.getString(SECURITY_P);
	                                
	                avatar = r.getString(USER_AVATAR);
	                emailValidated = r.getString(VALIDATEID);
                }
                catch (Exception e){
                    r.close();
                    ps.close();
                    conn.close();
                	return false;
                }


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
            _cat.log( Level.SEVERE,"Unable to get player " + e.getMessage(), e);
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
                password = r.getString(PASSWORD);
                emailId = r.getString(EMAIL_ID);
                fname = r.getString(FIRST_NAME);
                lname = r.getString(LAST_NAME);
                gender = r.getInt(GENDER);
                dob = r.getDate(DOB);
                if (!r.getString(AFFILIATE).equals(affiliate)) {
                    _cat.info("Affiliate in the database " + 
                               r.getString(AFFILIATE) + 
                               " is not same as the passed affiliate " + 
                               affiliate+"   user "+displayName);
                }
                location = r.getString(LOCATION);
                status = r.getString(STATUS);
                city = r.getString(CITY);
                affiliate = r.getString(AFFILIATE);
                colluder = r.getInt(COLLUDER);
                role_mask = new RoleMask(r.getInt(ROLE_MASK));
                rank = r.getInt(RANK);
                retentionFactor = r.getInt(RETENTION_FACTOR);
                remember = r.getBoolean(REMEMBER);
                regTs = r.getTimestamp(REG_TS);
                bonusCode = r.getString(BONUS_CODE);
                preferences = new PlayerPreferences( r.getInt(PREFERENCES));
                //piId = r.getInt(PI_ID);
                //profileId = r.getInt(PROFILE_ID);
                playChips = r.getDouble(PLAY_CHIPS);
                playChipsTs = r.getTimestamp(PLAY_CHIPS_TS);

                realChips = r.getDouble(REAL_CHIPS);
                realChipsTs = r.getTimestamp(REAL_CHIPS_TS);
                
                points = r.getDouble(POINTS);
                pointsTs = r.getTimestamp(POINTS_TS);

                security_q = r.getString(SECURITY_Q);
                security_p = r.getString(SECURITY_P);
                                
                avatar = r.getString(USER_AVATAR);
                emailValidated = r.getString(VALIDATEID);

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
            _cat.log( Level.SEVERE,"Unable to get player " + e.getMessage(), e);
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
  

    public boolean checkPassword(String pass) {
        return MD5.encode(pass).equals(password);
    }

   
    public int register(double pc, double rc, ModuleType mt) throws DBException {
        /** ALready initialized
             int[] winloss = getWinLoss(affiliate == null ? "admin" : affiliate);
             max_win = winloss[0];
             max_loss = winloss[1]; **/
        playChips = pc;
        realChips = rc;
        return save(mt);
    }
    
  
    public int save(ModuleType mt) throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
            try {
                StringBuilder sb = new StringBuilder("insert into T_USER (");
                sb.append(DISPLAY_NAME).append(",");
                sb.append(PASSWORD).append(",");
                sb.append(EMAIL_ID).append(",");
                sb.append(FIRST_NAME).append(",");
                sb.append(LAST_NAME).append(",");
                sb.append(GENDER).append(",");
                sb.append(DOB).append(",");
                sb.append(LOCATION).append(",");
                sb.append(STATUS).append(",");
                sb.append(CITY).append(",");
                sb.append(AFFILIATE).append(",");
                sb.append(COLLUDER).append(",");
                sb.append(RANK).append(",");
                sb.append(REMEMBER).append(",");
                sb.append(RETENTION_FACTOR).append(",");
                sb.append(ROLE_MASK).append(",");
                sb.append(REG_TS).append(",");
                sb.append(BONUS_CODE).append(",");
                sb.append(PREFERENCES).append(",");
                sb.append(PLAY_CHIPS).append(",");
                sb.append(PLAY_CHIPS_TS).append(",");
                sb.append(REAL_CHIPS).append(",");
                sb.append(REAL_CHIPS_TS).append(",");
                sb.append(POINTS).append(",");
                sb.append(POINTS_TS).append(",");
                sb.append(SECURITY_Q).append(",");
                sb.append(SECURITY_P).append(",");
                sb.append(USER_AVATAR).append(",");
                sb.append(RAKE_BACK).append(")");
                sb.append(" values (");
                sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ? , ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                _cat.finest(sb.toString());
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, displayName);
                ps.setString(2, MD5.encode(password));
                ps.setString(3, emailId);
                ps.setString(4, fname);
                ps.setString(5, lname);
                ps.setInt(6, gender);
                ps.setTimestamp(7, 
                                new java.sql.Timestamp(dob == null ? 0 : dob.getTime()));
                ps.setString(8, location);
                ps.setString(9, status);
                ps.setString(10, city);
                if (affiliate == null ){
                        affiliate="admin";
                }
                ps.setString(11, affiliate);
                ps.setInt(12, colluder);
                ps.setInt(13, rank);
                ps.setBoolean(14, remember);
                ps.setInt(15, retentionFactor);
                ps.setInt(16, role_mask.intValue() ==0 ? PLAYER_ROLE : role_mask.intValue());
                ps.setTimestamp(17, 
                                new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(18, bonusCode);
                ps.setInt(19, preferences.intVal());
                ps.setDouble(20, playChips);
                ps.setTimestamp(21, 
                                playChipsTs = new java.sql.Timestamp(System.currentTimeMillis()));

                ps.setDouble(22, realChips);
                ps.setTimestamp(23, 
                                realChipsTs = new java.sql.Timestamp(System.currentTimeMillis()));
                                
                ps.setDouble(24, points);
                ps.setTimestamp(25, 
                                pointsTs = new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(26, security_q);
                ps.setString(27, security_p);
                                    
                ps.setString(28, avatar);
                ps.setInt(29, rakeBack);
                
                _cat.finest(this.toString());
                r = ps.executeUpdate();
                ps.close();
                
            
                 if (playChips > 0)
                    DBTransaction.addTransaction(this, "register", new ModuleType(ModuleType.LOBBY),  playChips, playChips, realChips, TransactionType.Play_Registration_Bonus, "registration play bonus");                 
                  if (realChips > 0)
                    DBTransaction.addTransaction(this, "register", new ModuleType(ModuleType.LOBBY),  realChips, playChips, realChips, TransactionType.Real_Registration_Bonus, "registration real bonus");    
                  
                
                // if bonus code is not null then update the bonus
                //if (bonusCode != null && bonusCode.trim().length() > 3) {
                //    calBonus(displayName, bonusCode, 0, 0);
                //}
                // check if this person has been referred, update the status
                updateInvite(mt);
                
                
            } catch (SQLException e) {
                //e.printStackTrace();
                _cat.severe("Unable to save Player info " + e.getMessage() + 
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
                throw new DBException(e.getMessage() + 
                                      " -- while insertinf user " + 
                                      displayName);
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


    public synchronized void updateInvite(ModuleType mt) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection("GameEngine");
            StringBuilder sb = new StringBuilder("update T_TELL_A_FRIEND set ");
            sb.append("STATUS").append(" = ?, ");
            sb.append("TIME_STAMP").append(" = ? ");
            sb.append(" where ");
            sb.append("TO_MAIL").append("= ?");
            //_cat.info(sb.toString() + emailId);
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, "Registered");
            ps.setTimestamp(2, 
                            new java.sql.Timestamp(dob == null ? 0 : dob.getTime()));
            ps.setString(3, emailId);
            ps.executeUpdate();
            ps.close();
            
            // select the userid of the person who referred this guy
             StringBuilder sb2 = new StringBuilder("select from_userid_fk from T_TELL_A_FRIEND");
             sb2.append(" where ");
             sb2.append("TO_MAIL").append("= ?");
             //_cat.info(sb2.toString() + emailId);
             ps = conn.prepareStatement(sb2.toString());
             ps.setString(1, emailId);
             ResultSet rs = ps.executeQuery();
             String friends_displayName=null;
             if (rs.next()){
                 friends_displayName = rs.getString(1);
             }
             ps.close();           
             conn.close();
            
            if (friends_displayName != null){
                DBFriend.confirmFriend(displayName, friends_displayName, mt);
            }
           
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to update Player's Play Wallet " + e.getMessage(), 
                     e);
            _cat.severe(this.toString());

            if (e.getMessage().endsWith("New request is not allowed to start because it should come with valid transaction descriptor")) {
                ConnectionManager.clearPool(conn);
            } else {
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
    
    public synchronized int update() throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
            try {
            	System.out.println("values in DBPlayer.update()="+displayName+","+city+","+fname+","+lname+","+emailId+","+dob+","+gender+","+avatar);
                // provider ?
                conn = ConnectionManager.getConnection("GameEngine");
                StringBuilder sb = new StringBuilder("update T_USER set ");
                sb.append(FIRST_NAME).append(" = ?, ");
                sb.append(LAST_NAME).append(" = ?, ");
                sb.append(EMAIL_ID).append(" = ?, ");
                sb.append(GENDER).append(" = ?, ");
                sb.append(DOB).append(" = ?, ");
                sb.append(LOCATION).append(" = ?, ");
                sb.append(STATUS).append(" = ?, ");
                sb.append(CITY).append(" = ?, ");
                sb.append(AFFILIATE).append(" = ?, ");
                sb.append(COLLUDER).append(" = ?, ");
                sb.append(RANK).append(" = ?, ");
                sb.append(REMEMBER).append(" = ?, ");
                sb.append(RETENTION_FACTOR).append(" = ?, ");
                sb.append(ROLE_MASK).append(" = ?, ");
                sb.append(BONUS_CODE).append(" = ?, ");
                sb.append(PREFERENCES).append(" = ?, ");
                sb.append(USER_AVATAR).append(" = ?, ");
                sb.append(RAKE_BACK).append(" = ? ");
                sb.append(" where ");
                sb.append(DISPLAY_NAME).append("= ?");
                _cat.finest(sb.toString());
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, fname);
                ps.setString(2, lname);
                ps.setString(3, emailId);
                ps.setInt(4, gender);
                ps.setTimestamp(5, 
                                new java.sql.Timestamp(dob == null ? 0 : dob.getTime()));
                ps.setString(6, location);
                ps.setString(7, status);
                ps.setString(8, city);
                ps.setString(9, affiliate == null ? "admin" : affiliate);
                ps.setInt(10, colluder);
                ps.setInt(11, rank);
                ps.setBoolean(12, remember);
                ps.setInt(13, retentionFactor);
                //System.out.println("...........rolemask before........"+role_mask.intValue());
                ps.setInt(14, role_mask.intValue() ==0 ? PLAYER_ROLE : role_mask.intValue());
               // System.out.println("...........rolemask after........"+role_mask.intValue());
                ps.setString(15, bonusCode);
                ps.setInt(16, preferences.intVal());                                    
                ps.setString(17, avatar);
                ps.setInt(18, rakeBack);
                ps.setString(19, displayName);
                
                _cat.finest("Update" + this);
                r = ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                _cat.log( Level.SEVERE,"Unable to update Player's Play Wallet " + 
                          e.getMessage(), e);
                _cat.severe(this.toString());

                if (e.getMessage().endsWith("New request is not allowed to start because it should come with valid transaction descriptor")) {
                    ConnectionManager.clearPool(conn);
                } else {
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
    
    public int updateUserAvatar() throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;

        int r = -1;
            try {
                StringBuilder sb = new StringBuilder("update T_USER set ");
                sb.append(USER_AVATAR).append(" = ? ");
                sb.append(" where ");
                sb.append(DISPLAY_NAME).append("= ?");
                _cat.finest(sb.toString());
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, avatar);
                ps.setString(2, displayName);
                r = ps.executeUpdate();
                ps.close();
                conn.close();
                _cat.finest(r + " Update Avatar =" + avatar);
            } catch (SQLException e) {
                _cat.log( Level.SEVERE,"Unable to update Player's Play Wallet " + 
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
         public int securityUpdate() throws DBException {
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
                 sb.append(SECURITY_Q).append(" = ?, ");
                 sb.append(SECURITY_P).append(" = ? ");
                 sb.append(" where ");
                 sb.append(DISPLAY_NAME).append("= ?");
                 _cat.finest(sb.toString());
                 ps = conn.prepareStatement(sb.toString());
                 ps.setString(1, security_q);
                 ps.setString(2, security_p);
                 ps.setString(3, displayName);
                 r = ps.executeUpdate();
                 ps.close();
                 conn.close();
             } catch (SQLException e) {
                 _cat.log( Level.SEVERE,"Unable to update Player's Win Loss " + e.getMessage(), 
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
         

             public int miniUpdate() throws DBException {
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
                     sb.append(STATUS).append(" = ?, ");
                     sb.append(REMEMBER).append(" = ?, ");
                     sb.append(RETENTION_FACTOR).append(" = ?, ");
                     sb.append(PREFERENCES).append(" = ?, ");
                     sb.append(ROLE_MASK).append(" = ?, ");
                     sb.append(RANK).append(" = ? ");
                     sb.append(" where ");
                     sb.append(DISPLAY_NAME).append("= ?");
                     _cat.finest(sb.toString());
                     ps = conn.prepareStatement(sb.toString());
                     ps.setString(1, status);
                     ps.setBoolean(2, remember);
                     ps.setInt(3, retentionFactor);
                     ps.setInt(4, preferences.intVal());
                     ps.setInt(5, role_mask.intValue());
                     ps.setInt(6, rank);
                     ps.setString(7, displayName);
                     r = ps.executeUpdate();
                     ps.close();
                     conn.close();
                 } catch (SQLException e) {
                     _cat.log( Level.SEVERE,"Unable to update Player's Win Loss " + e.getMessage(), 
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

         public synchronized boolean refreshChips() throws DBException {
             Connection conn = null;
             PreparedStatement ps = null;
             try {
                 StringBuilder sb = new StringBuilder("select *");
                 sb.append(" from T_USER where ");
                 sb.append(DISPLAY_NAME).append(" like binary ?");
                 conn = ConnectionManager.getConnection("GameEngine");
                 ps = conn.prepareStatement(sb.toString());
                 ps.setString(1, displayName);
                 ResultSet r = ps.executeQuery();
                 if (r.next()) {
                     bonusCode = r.getString(BONUS_CODE);
                     preferences = new PlayerPreferences( r.getInt(PREFERENCES));
                     playChips = r.getDouble(PLAY_CHIPS);
                     realChips = r.getDouble(REAL_CHIPS); 
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
                 _cat.log( Level.SEVERE,"Unable to get player " + e.getMessage(), e);
                 _cat.info(this.toString());

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
        

    public static DBPlayer[] getSearchPlayers(String pattern){
        Connection conn = null;
        PreparedStatement ps = null;
         Vector dbp = new Vector();

        int pref = -1;
        try {
            /**
        * Create a transactio in the DBWallet
        */
            conn = ConnectionManager.getConnection("GameEngine");
             
            StringBuilder sb = new StringBuilder("select * from T_USER where userid  like '%" + pattern + "%'");
            //sb.append("or city  like '%" + pattern + "%'");
            //sb.append("or status  like '%" + pattern + "%'");
           // sb.append(" and emailid not like '%test.com' limit 100");
            ps = conn.prepareStatement(sb.toString());
            _cat.info(sb.toString());
            ResultSet r = ps.executeQuery();
            while(r.next()){
                 DBPlayer dp = new DBPlayer();
                 dp.displayName = r.getString(DISPLAY_NAME);
                 dp.playChips = r.getDouble(PLAY_CHIPS);
                 dp.realChips = r.getDouble(REAL_CHIPS);
                 dp.city = r.getString(CITY);
                 dp.gender = r.getInt(GENDER);
                 dp.avatar = r.getString(USER_AVATAR);
                 dbp.add(dp);
            }
            r.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            _cat.log( Level.SEVERE,"Unable to get top players " + e.getMessage(), e);

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
        return (DBPlayer [])dbp.toArray(new DBPlayer[dbp.size()]);
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////
    ////////TRANSACTIONS
    ///////////////////////////////////////////////////////////////////////////////
     public void returnRejectedPayout( double amount, String session) {
        try {
         if (amount <= 0)return;
           long tt = -1;
           double playAmount =0, realAmount=0;
          ModuleType mt = null;
          refreshChips();
             
          realAmount = amount;
                  
           tt = TransactionType.Real_Rejected_Payout;
           mt = new ModuleType(ModuleType.LOBBY);
           realChips += amount;
               
           updateChips();
           
           DBTransaction.addTransaction(this, session, mt, amount, playAmount, realAmount, tt, "Gold points returned to lobby on rejected payout");
        } catch (DBException e) {
         _cat.severe("Retunring back of chip failed " + this.displayName);
        }
        }
        
         
    
    ///////////////////////////PLAY/////////////////////////////////////////////////

      public void setPlayChipsShill(double v) {
          v = Utils.getRounded(v);
              this.playChips = v;
      }
      
    public void playChipsTransferIn(double v, String session, ModuleType t, String transferPlayer) {
        v = Utils.getRounded(v);
        playChips += v;
        try {
            updatePlayChips();
            DBTransaction.addTransaction(this, session, t, v, playChips, realChips, TransactionType.Play_Transfer_In, "play money transferred from " + transferPlayer);
        } catch (DBException e) {
            _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
        }
    }

      public String playChipsTransfer(double v, String session, ModuleType t, String transferPlayer) {
        //System.out.println("Amt=" + v + ", Cur chips=" + playChips);
          v = Utils.getRounded(v);
          playChips -= v;
        //System.out.println("Amt=" + v + ", Cur chips=" + playChips);
          try {
              updatePlayChips();
              DBTransaction.addTransaction(this, session,  t, v, playChips, realChips, TransactionType.Play_Transfer_Out, "play money transferred to " + transferPlayer);
              DBPlayer tdb = new DBPlayer();
              tdb.get(transferPlayer);
              tdb.playChipsTransferIn(v, session, t, displayName);
              return tdb.emailId;
          } catch (DBException e) {
              _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
          }
          return null;
      }
      
      public void playChipsDeposit(double v, String session) {
          v = Utils.getRounded(v);
          System.out.println("playChips "+playChips);
          playChips += v;
          System.out.println("playChips "+playChips);
          try {
              updatePlayChips();
            DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.LOBBY), v, playChips, realChips, TransactionType.Play_Deposit, "play money deposit");
          } catch (DBException e) {
              _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
          }
      }

    public void playChipsPayout(double v, String session) {
        v = Utils.getRounded(v);
        playChips -= v;
        try {
            updatePlayChips();
            DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.LOBBY), v, playChips, realChips, TransactionType.Play_Payout, "play money payout");
        } catch (DBException e) {
            _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
        }
    }
    
    public void playChipsToTable(String gid, int gt, ModuleType mod, double v, String session) {
      try {
            refreshChips();
            playChips -= v;
          updatePlayChips();
          DBTransactionScratchPad.addScratchTransaction(displayName, gt, gid, mod.intVal(), session, v, 0, DBTransactionScratchPad.TYPE_PLAY_BUYIN);
          DBTransaction.addTransaction(this, session, mod, v, playChips, realChips, TransactionType.Play_On_Table, "uid=" + displayName + ",gname=" + gid +  ",session=" + session + ",gtype=" + gt + ",mod=" + mod.toString());
      } catch (DBException e) {
          _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
      }
    }
    
    public void playChipsBuyIn(String gid, int gt, ModuleType mod, double v, String session) {
      v = Utils.getRounded(v);
      playChips -= v;
      try {
          updatePlayChips();
          DBTransactionScratchPad.addScratchTransaction(displayName, gt, gid, mod.intVal(), session, v, 0, DBTransactionScratchPad.TYPE_PLAY_REBUY);
          DBTransaction.addTransaction(this, session, mod, v, playChips, realChips, TransactionType.Play_On_Table, "uid=" + displayName + ",gname=" + gid +  ",session=" + session + ",gtype=" + gt + ",mod=" + mod.toString());
      } catch (DBException e) {
          _cat.severe("Chip refresh  of " + v + " failed for " +  this.displayName);
      }
    }
    
    public void playChipsFromTable(String gid, int gt, ModuleType mod, double v, String session) {
      try {
         refreshChips();
          playChips += v;
          updatePlayChips();
          DBTransactionScratchPad[] prev = DBTransactionScratchPad.fetch(displayName, gid, session);  
          double total=0;
          //total amount brought in
           for (int i=0;i<prev.length;i++){
               total+=prev[i]._real;
           }
          DBTransaction.addTransaction(this, session, mod, v, playChips, realChips, TransactionType.Play_From_Table, "uid=" + displayName + ",gname=" + gid +  ",session=" + session + ",gtype=" + gt + ",mod=" + mod.toString());
      }
      catch (DBException e) {
          _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
      }
    }
    public java.util.Date getPlayChipsTs() {
        return playChipsTs;
    }
    
    public double getPlayChips() {
        return Utils.getRounded(playChips);
    }
    public String getPlayChipsString() {
        return Utils.getRoundedDollarCent(playChips);
    }
    
  public int updatePlayChips() throws DBException {
      Connection conn = null;
      PreparedStatement ps = null;

      int r = -1;
          try {
        	  //System.out.println("..displayName in DBPlayer="+displayName);
              StringBuilder sb = new StringBuilder("update T_USER set ");
              sb.append(PLAY_CHIPS).append(" = ?, ");
              sb.append(PLAY_CHIPS_TS).append(" = ? ");
              sb.append(" where ");
              sb.append(DISPLAY_NAME).append("= ?");
              _cat.finest(sb.toString());
              conn = ConnectionManager.getConnection("GameEngine");
              ps = conn.prepareStatement(sb.toString());
              ps.setDouble(1, playChips);
              ps.setTimestamp(2, playChipsTs = new java.sql.Timestamp(System.currentTimeMillis()));
              ps.setString(3, displayName);
              r = ps.executeUpdate();
              ps.close();
              conn.close();
              _cat.finest(r + " Update playChips =" + playChips);
          } catch (SQLException e) {
              _cat.log( Level.SEVERE,"Unable to update Player's Play Wallet " + 
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


        /////////////////////////////////REAL/////////////////////////////////////


         public void setRealChipsShill(double v) {
             v = Utils.getRounded(v);
                 this.realChips = v;
         }


    public void realChipsTransferIn(double v, String session, ModuleType t, String transferPlayer) {
        v = Utils.getRounded(v);
        realChips += v;
        try {
            updateRealChips();
            DBTransaction.addTransaction(this, session, t, v, playChips, realChips, TransactionType.Real_Transfer_In, "real money transferred from " + transferPlayer);
        } catch (DBException e) {
            _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
        }
    }

      public String realChipsTransfer(double v, String session, ModuleType t, String transferPlayer) {
          v = Utils.getRounded(v);
          realChips -= v;
          try {
              updateRealChips();
              DBTransaction.addTransaction(this, session, t, v, playChips, realChips, TransactionType.Real_Transfer_Out, "real money transferred to " + transferPlayer);
              DBPlayer tdb = new DBPlayer();
              tdb.get(transferPlayer);
              tdb.realChipsTransferIn(v, session, t, displayName);
              return tdb.emailId;
          } catch (DBException e) {
              _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
          }
          return null;
      }
      


         public void realChipsDeposit(double v, String session, ModuleType t) {
             v = Utils.getRounded(v);
             realChips += v;
             try {
                 updateRealChips();
                DBTransaction.addTransaction(this, session, t, v, playChips, realChips, TransactionType.Real_Deposit, "real money deposit");
             } catch (DBException e) {
                 _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
             }
         }

         public void realChipsPayout(double v, String session) {
           v = Utils.getRounded(v);
           realChips -= v;
           try {
               updateRealChips();
               DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.LOBBY), v, playChips, realChips, TransactionType.Real_Payout, "real money payout");
           } catch (DBException e) {
               _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
           }
         }
         
         public void realChipsToTable(String gid,  int gt, ModuleType mod, double v, String session) {
             try {            
                refreshChips();
                realChips -= v;
                 updateRealChips();
                 DBTransactionScratchPad.addScratchTransaction(displayName, gt, gid, mod.intVal(),  session, 0, v, DBTransactionScratchPad.TYPE_REAL_BUYIN);
                 DBTransaction.addTransaction(this, session, mod, v, playChips, realChips, TransactionType.Real_On_Table, "uid=" + displayName + ",gname=" + gid +  ",session=" + session + ",gtype=" + gt + ",mod=" + mod.toString());
             } catch (DBException e) {
                 _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
             }
         }
    
        public void realChipsBuyIn(String gid, int gt, ModuleType mod, double v, String session) {
            v = Utils.getRounded(v);
            realChips -= v;
            try {
                updateRealChips();
                DBTransactionScratchPad.addScratchTransaction(displayName, gt, gid, mod.intVal(), session, 0, v, DBTransactionScratchPad.TYPE_REAL_REBUY);
                DBTransaction.addTransaction(this, session, mod, v, playChips, realChips, TransactionType.Real_On_Table, "uid=" + displayName + ",gname=" + gid +  ",session=" + session + ",gtype=" + gt + ",mod=" + mod.toString());
            } catch (DBException e) {
                _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
            }
        }
        
         public void realChipsFromTable(String gid, int gt, ModuleType mod, double v, String session) {
           try {
                refreshChips();
               realChips += v;
               updateRealChips();
               DBTransactionScratchPad[] prev = DBTransactionScratchPad.fetch(displayName, gid, session);  
               double total=0;
               //total amount brought in
                for (int i=0;i<prev.length;i++){
                    total+=prev[i]._real;
                }
               DBTransaction.addTransaction(this, session, mod, v, playChips, realChips, TransactionType.Real_From_Table, "uid=" + displayName + ",gname=" + gid +  ",session=" + session + ",gtype=" + gt + ",mod=" + mod);
           }
           catch (DBException e) {
               _cat.severe("Chip refresh  of " + v + " failed for " + this.displayName);
           }
         }

      public java.util.Date getRealChipsTs() {
          return realChipsTs;
      }
      
      public double getRealChips() {
          return Utils.getRounded(realChips);
      }
    public String getRealChipsString() {
               return Utils.getRoundedDollarCent(realChips);
         }

      public int updateRealChips() throws DBException {
          Connection conn = null;
          PreparedStatement ps = null;
    
          int r = -1;
              try {
                  StringBuilder sb = new StringBuilder("update T_USER set ");
                  sb.append(REAL_CHIPS).append(" = ?, ");
                  sb.append(PREFERENCES).append(" = ?, ");
                  sb.append(REAL_CHIPS_TS).append(" = ? ");
                  sb.append(" where ");
                  sb.append(DISPLAY_NAME).append("= ?");
                  _cat.finest(sb.toString());
                  conn = ConnectionManager.getConnection("GameEngine");
                  conn.setAutoCommit(false);
                  ps = conn.prepareStatement(sb.toString());
                  ps.setDouble(1, realChips);
                  ps.setInt(2, preferences.intVal());
                  realChipsTs = 
                          new java.sql.Timestamp(System.currentTimeMillis());
                  ps.setTimestamp(3, realChipsTs);
                  ps.setString(4, displayName);
                  r = ps.executeUpdate();
                  ps.close();
                  //ConnectionManager.returnConnection(conn, "GameEngine");
                  conn.commit();
                  conn.close();
                  _cat.finest("Update" + this);
              } catch (SQLException e) {
                  _cat.log( Level.SEVERE,"Unable to update Player's Play Wallet " + 
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
      
      
  //    #####################################################################################
//###############################################################################################
      
    public void chipsHouseKeeping(String gid, int gt,  ModuleType mod, double play, double real, String session) {
        //System.out.println("Real Amt=" + real + ", Cur chips=" + realChips);
        try {
            refreshChips();
            double total_play=0, total_real=0;            
            DBTransactionScratchPad[] prev = DBTransactionScratchPad.fetch(displayName, gid, session);
            //total amount brought in
            for (int i=0;i<prev.length;i++){
                total_play+=prev[i]._play;
                total_real+=prev[i]._real;
                //System.out.println("Fetched real = " + total_real);
            }
            if (play > 0){
	            playChips += play;
	            updatePlayChips(); 
	              DBTransaction.addTransaction(this, session, mod, play, playChips, realChips , TransactionType.Play_From_Table, "uid=" + displayName + ",gname=" + gid + ",session=" + session + ",gtype=" + gt + ",mod=" + mod);
            }
            if (real > 0){
                realChips += real;
                updateRealChips();               
                  DBTransaction.addTransaction(this, session, mod, real, playChips, realChips , TransactionType.Real_From_Table, "uid=" + displayName + ",gname=" + gid + ",session=" + session + ",gtype=" + gt + ",mod=" + mod);
            }
        }
        catch (DBException e) {
            _cat.severe("Chip refresh  of " + play+ ", " +real + " failed for " + this.displayName);
        }
    }


  public int updateChips() throws DBException {
      Connection conn = null;
      PreparedStatement ps = null;

      int r = -1;
          try {
              StringBuilder sb = new StringBuilder("update T_USER set ");
              sb.append(PLAY_CHIPS).append(" = ?, ");
              sb.append(PLAY_CHIPS_TS).append(" = ? ");
              sb.append(REAL_CHIPS).append(" = ?, ");
              sb.append(PREFERENCES).append(" = ?, ");
              sb.append(REAL_CHIPS_TS).append(" = ? ");
              sb.append(" where ");
              sb.append(DISPLAY_NAME).append("= ?");
              _cat.finest(sb.toString());
              conn = ConnectionManager.getConnection("GameEngine");
              ps = conn.prepareStatement(sb.toString());
              ps.setDouble(1, playChips);
              playChipsTs = new java.sql.Timestamp(System.currentTimeMillis());
              ps.setTimestamp(2, playChipsTs);
              ps.setDouble(3, realChips);
              ps.setInt(4, preferences.intVal());
              realChipsTs =   new java.sql.Timestamp(System.currentTimeMillis());
              ps.setTimestamp(5, realChipsTs);
              ps.setString(6, displayName);
              r = ps.executeUpdate();
              ps.close();
              conn.close();
              _cat.finest(r + " Update chips =" + playChips);
          } catch (SQLException e) {
              _cat.log( Level.SEVERE,"Unable to update Player's Wallet " + 
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




   //////////////////////////BONUS////////////////////////////////////////////////
   
    /**
     * Promotion and Commission
     *
     * type 0 - user register bonus, 1 - Deposit bonus
     * @return int
     */
    public double calBonus(String userid, String bonus_code, double amnt, 
                           int type) throws DBException {
        double total = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuilder sb = new StringBuilder();

        if (type == 0 || type == 1) {

            try {
                sb.append("SELECT OFFER_TYPE_ID_FK FROM T_PROMOTION WHERE PROMO_NAME");
                sb.append("=?");

                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setString(1, bonus_code);
                _cat.finest(sb.toString());
                String offerId = null;
                ResultSet r = ps.executeQuery();
                if (r.next()) {
                    offerId = r.getString("OFFER_TYPE_ID_FK");
                }
                r.close();
                ps.close();

                if (offerId != null) {
                    int offer_percent = 0;
                    double offer_max = 0;
                    double offer_amount = 0;
                    double offer_register = 0;

                    sb = new StringBuilder();
                    sb.append("SELECT OFFER_PERCENT,OFFER_MAX,OFFER_AMOUNT,OFFER_REGISTER ");
                    sb.append("FROM T_OFFER_TYPE WHERE OFFER_TYPE_ID_PK");
                    sb.append("=?");

                    ps = conn.prepareStatement(sb.toString());
                    ps.setString(1, offerId);
                    _cat.finest(sb.toString());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        offer_percent = rs.getInt("OFFER_PERCENT");
                        offer_max = rs.getDouble("OFFER_MAX");
                        offer_amount = rs.getDouble("OFFER_AMOUNT");
                        offer_register = rs.getDouble("OFFER_REGISTER");
                    }
                    if (type == 0) {
                        total = offer_register;
                    } else if (type == 1) {
                        if (offer_amount > 0) {
                            total = offer_amount;
                        } else {
                            total = (amnt * offer_percent);
                            if (total > 0) {
                                total = total / 100;
                            }
                            if (total > offer_max) {
                                total = offer_max;
                            }
                        }
                    }

                    rs.close();
                    ps.close();
                }
                conn.close();
                if (total > 0) {
                    updateUserBonus(userid, total);
                }
            } catch (SQLException e) {
                _cat.log( Level.SEVERE,"Unable to calculate bonus " + e.getMessage(), e);
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
                                      " -- while calculating bonus");
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
        return total;
    }



        public void updateUserBonus(String userid, 
                                    double amnt) throws DBException {
            Connection conn = null;
            PreparedStatement ps = null;

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE T_USER SET BINGO_BONUS_AMOUNT = BINGO_BONUS_AMOUNT + ");
            sb.append("? WHERE USERID").append("=?");

            try {
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setDouble(1, amnt);
                ps.setString(2, userid);
                _cat.finest(sb.toString());

                int r = ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                _cat.log( Level.SEVERE,"Unable to update bonus in user table " + 
                           e.getMessage(), e);
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
                throw new DBException(e.getMessage() + " -- while updating bonus");
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

     //public static final int E_BONUS_CODE_NOT_VALID_NOW=44;
     //public static final int E_BONUS_CODE_EXPIRED=45;
     //public static final int E_BONUS_CODE_DOES_NOT_EXISTS=46;

     /*** public static int validateBonusCode(String bonus_code) throws DBException {
         int result = 0;
         Connection conn = null;
         PreparedStatement ps = null;
         StringBuilder sb = new StringBuilder();

         try {
             sb.append("SELECT start_date, expiry_date FROM T_PROMOTION WHERE PROMO_NAME");
             sb.append("=?");

             conn = ConnectionManager.getConnection("GameEngine");
             ps = conn.prepareStatement(sb.toString());
             ps.setString(1, bonus_code);
             _cat.finest(sb);
             ResultSet r = ps.executeQuery();
             java.sql.Timestamp sd, ed;
             if (r.next()) {
                 sd = r.getTimestamp("START_DATE");
                 ed = r.getTimestamp("EXPIRY_DATE");

                 long curr = System.currentTimeMillis();
                 if (curr < sd.getTime()) {
                     result = Response.E_BONUS_CODE_NOT_VALID_NOW;
                 } else if (curr > ed.getTime()) {
                     result = Response.E_BONUS_CODE_EXPIRED;
                 } else {
                     result = 1;
                 }
             } else {
                 result = Response.E_BONUS_CODE_DOES_NOT_EXISTS;
             }
             r.close();
             ps.close();
             conn.close();
         } catch (SQLException e) {
             _cat.severe("Unable to validate promotion " + e.getMessage(), e);
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
                                   " -- while calculating bonus");
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
         return result;
     }**/



      public int updatePreferences() throws DBException {
          Connection conn = null;
          PreparedStatement ps = null;

          int r = -1;
          try {
              StringBuilder sb = new StringBuilder("update T_USER  set ");
              sb.append(PREFERENCES).append(" = ? ");
              sb.append(" where ");
              sb.append(DISPLAY_NAME).append("= ? ");
              //_cat.finest(sb.toString());
              conn = ConnectionManager.getConnection("GameEngine");
              ps = conn.prepareStatement(sb.toString());
              ps.setInt(1, preferences.intVal());
              ps.setString(2, displayName);
              r = ps.executeUpdate();
              ps.close();
              conn.close();
          } catch (SQLException e) {
              _cat.log( Level.SEVERE,"Unable to update Player's preferences " + 
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
                                    " -- while updating preferences");
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
      

      public int updateRole() throws DBException {
          Connection conn = null;
          PreparedStatement ps = null;

          int r = -1;
          try {
              StringBuilder sb = new StringBuilder("update T_USER  set ");
              sb.append(ROLE_MASK).append(" = ? ");
              sb.append(" where ");
              sb.append(DISPLAY_NAME).append("= ? ");
              //_cat.finest(sb.toString());
              conn = ConnectionManager.getConnection("GameEngine");
              ps = conn.prepareStatement(sb.toString());
              ps.setInt(1, role_mask.intValue());
              ps.setString(2, displayName);
              r = ps.executeUpdate();
              ps.close();
              conn.close();
          } catch (SQLException e) {
              _cat.log( Level.SEVERE,"Unable to update Player's role_mask " + 
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
                                    " -- while updating role_mask");
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
      
    public int delete() throws DBException {
           Connection conn = null;
           PreparedStatement ps = null;
           int r = -1;
           try {
               StringBuilder sb = new StringBuilder("delete from T_USER  ");
               sb.append(" where ");
               sb.append(DISPLAY_NAME).append("= ? ");
               //_cat.finest(sb.toString());
               conn = ConnectionManager.getConnection("GameEngine");
               ps = conn.prepareStatement(sb.toString());
               ps.setString(1, displayName);
               r = ps.executeUpdate();
               ps.close();
               conn.close();
           } catch (SQLException e) {
               _cat.log( Level.SEVERE,"Unable to delete player " + 
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



    /////////////////SNG TRANSCATIONS//////////////////////////////////////////////////

          

         public int buyPlaySnGChips(String session,  double amount, String tournyId, String gt) throws DBException {
             PreparedStatement ps = null;

             int r = -1;
             if (playChips < amount) {
                 return r;
             }
             playChips -= amount;
             r = updatePlayChips();
             DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
            		 TransactionType.Play_SnG_BuyinFees, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
             return r;
         }

         public int returnPlaySnGChips(String session, double amount, String tournyId, String gt) throws DBException {
             PreparedStatement ps = null;

             playChips += amount;
             updatePlayChips();
             DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
            		 TransactionType.Play_Transfer_In, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
             return 1;
         }
         
         public int addPlaySnGWin(String session, double amount, String tournyId, String gt) throws DBException {
             Connection conn = null;
             PreparedStatement ps = null;

             playChips += amount;
             DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
           		  TransactionType.Play_SnG_Win, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
             return 1;
         }
         

         public int buyRealSnGChips(String session,  double amount, String tournyId, String gt) throws DBException {
             PreparedStatement ps = null;

             int r = -1;
             if (realChips < amount) {
                 return r;
             }
             realChips -= amount;
             r = updateRealChips();
             DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
            		 TransactionType.Real_SnG_BuyinFees, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
             return r;
         }

         public int returnRealSnGChips(String session, double amount, String tournyId, String gt) throws DBException {
             PreparedStatement ps = null;

             realChips += amount;
             updateRealChips();
             DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
            		 TransactionType.Real_Transfer_In, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
             return 1;
         }
         
         public int addRealSnGWin(String session, double amount, String tournyId, String gt) throws DBException {
             Connection conn = null;
             PreparedStatement ps = null;

             realChips += amount;
             DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
           		  TransactionType.Real_SnG_Win, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
             return 1;
         }
         
         /////////////// ENd SNG//////////////////////////////////////////

   
         
         
         

         /////////////////MTT TRANSCATIONS//////////////////////////////////////////////////

               

              public int buyPlayTournyChips(String session,  double amount, String tournyId, String gt) throws DBException {
                  PreparedStatement ps = null;

                  int r = -1;
                  if (playChips < amount) {
                      return r;
                  }
                  playChips -= amount;
                  r = updatePlayChips();
                  DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
                 		 TransactionType.Play_Tourny_BuyinFees, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
                  return r;
              }

              public int returnPlayTournyChips(String session, double amount, String tournyId, String gt) throws DBException {
                  PreparedStatement ps = null;

                  playChips += amount;
                  updatePlayChips();
                  DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
                 		 TransactionType.Play_Transfer_In, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
                  return 1;
              }
              
              public int addPlayTournyWin(String session, double amount, String tournyId, String gt) throws DBException {
                  Connection conn = null;
                  PreparedStatement ps = null;

                  playChips += amount;
                  DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
                		  TransactionType.Play_Tourny_Win, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
                  return 1;
              }
              

              public int buyRealTournyChips(String session,  double amount, String tournyId, String gt) throws DBException {
                  PreparedStatement ps = null;

                  int r = -1;
                  if (realChips < amount) {
                      return r;
                  }
                  realChips -= amount;
                  r = updateRealChips();
                  DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
                 		 TransactionType.Real_Tourny_BuyinFees, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
                  return r;
              }

              public int returnRealTournyChips(String session, double amount, String tournyId, String gt) throws DBException {
                  PreparedStatement ps = null;

                  realChips += amount;
                  updateRealChips();
                  DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
                 		 TransactionType.Real_Transfer_In, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
                  return 1;
              }
              
              public int addRealTournyWin(String session, double amount, String tournyId, String gt) throws DBException {
                  Connection conn = null;
                  PreparedStatement ps = null;

                  realChips += amount;
                  DBTransaction.addTransaction(this, session, new ModuleType(ModuleType.POKER), amount, playChips, realChips , 
                		  TransactionType.Real_Tourny_Win, "uid=" + displayName + ",gname=" + tournyId + ",session=" + session + ",gtype=" + gt);
                  return 1;
              }
              
              /////////////// ENd TOURNY//////////////////////////////////////////

         
         
         
         
         
         
         
         
         
         
         
         
    public static void main(String[] args) throws Exception {

        //registration
        DBPlayer newp = new DBPlayer("coolcat1", "coolcat1");
        newp.setColluder(0);
        newp.setDob(Calendar.getInstance().getTime());
        newp.setEmailId("coolcat@test.com");
        newp.setGender(0);
        newp.setBonusCode("Daily007");
        newp.setReferrer("NEWSPAPER");
        newp.setRegTs(System.currentTimeMillis());
        newp.setRetentionFactor(10);
        _cat.info("test");
        //System.out.println(newp);
        //System.out.println(DBPlayer.validateBonusCode("BonusPromo"));
        //newp.save();
        /**DBPlayer bp = new DBPlayer();
       if (bp.get("abhido", "abhido")) {
      System.out.println(bp);
      System.out.println(bp.getAllInTs());
      System.out.println(bp.resetAllIn());
       }
       else {
      System.out.println("Player does not exists");
       }**/
    }

     public String toString() {
         StringBuilder str = new StringBuilder();
         str.append("Player:\n");
         str.append("DispName = ").append(getDisplayName()).append("\n");
         str.append("Password = ").append(getPassword()).append("\n");
         str.append("EmailId = ").append(getEmailId()).append("\n");
         str.append("Affiliate = ").append(getAffiliate()).append("\n");
         str.append("Provider = ").append(provider).append("\n");
         str.append("Gender = ").append(getGender()).append("\n");
         str.append("Dob = ").append(getDob()).append("\n");
         str.append("RoleMask = ").append(getRoles()).append("\n");
         str.append("Colluder = ").append(getColluder()).append("\n");
         str.append("RetentionFactor = ").append(getRetentionFactor()).append("\n");
         str.append("BonusCode = ").append(getBonusCode()).append("\n");
         str.append("Preferences = ").append(getPreferences()).append("\n");
         str.append("RegTs = ").append(getRegTs()).append("\n");
         str.append("Referrer = ").append(getReferrer()).append("\n");
         str.append("PiId = ").append(getPiId()).append("\n");
         str.append("ProfileId = ").append(getProfileId()).append("\n");
         str.append("playChips = ").append(getPlayChips()).append("\n");
         str.append("playChipsTs = ").append(getPlayChipsTs()).append("\n");
         str.append("realChips = ").append(getRealChips()).append("\n");
         str.append("realChipsTs = ").append(getRealChipsTs()).append("\n");

         return (str.toString());
     }
/////////////updating POINTS of user//////////////////////////
     
     public int updatePoints() throws DBException {
         Connection conn = null;
         PreparedStatement ps = null;

         int r = -1;
             try {
           	  //System.out.println("..displayName in DBPlayer="+displayName);
                 StringBuilder sb = new StringBuilder("update T_USER set ");
                 sb.append(POINTS).append(" = ?, ");
                 sb.append(POINTS_TS).append(" = ? ");
                 sb.append(" where ");
                 sb.append(DISPLAY_NAME).append("= ?");
                 _cat.finest(sb.toString());
                 conn = ConnectionManager.getConnection("GameEngine");
                 ps = conn.prepareStatement(sb.toString());
                 ps.setDouble(1, points);
                 ps.setTimestamp(2, pointsTs = new java.sql.Timestamp(System.currentTimeMillis()));
                 ps.setString(3, displayName);
                 r = ps.executeUpdate();
                 ps.close();
                 conn.close();
                 _cat.finest(r + " Update POINTS =" + points);
             } catch (SQLException e) {
                 _cat.log( Level.SEVERE,"Unable to update Player's POINTS " + 
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
     public String getEuroSymbol(){
    	 return "\u20AC";
     }
     

     public static synchronized String[] getBots() throws DBException {
                Connection conn = null;
         PreparedStatement ps = null;
         Vector player_names = new Vector();
         try {
             StringBuilder sb = new StringBuilder("select *");
             sb.append(" from T_USER where ");
             sb.append(EMAIL_ID).append(" like ?");
             conn = ConnectionManager.getConnection("GameEngine");
             ps = conn.prepareStatement(sb.toString());
             ps.setString(1, "%@test.com");
             ResultSet r = ps.executeQuery();
             while (r.next()) {
                 player_names.add(r.getString(DISPLAY_NAME));
                // _cat.finest(this.toString());
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
         return (String[])player_names.toArray(new String[player_names.size()]);
     }
     
     
     
}
