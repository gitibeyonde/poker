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

public class DBCollection {
    // set the category for logging
    static Logger _cat = Logger.getLogger(DBCollection.class.getName());
    //     NAME               		nvarchar(32) NOT null,
    // 	TYPE				nvarchar(32),
    // 	SHORT_DESCRIPTION		nvarchar(500),
    // 	DESCRIPTION			text,
    // 	AMOUNT				int,
    // 	IMAGE_TN			blob,
    // 	IMAGE				blob,
    // 	CREATE_TS			datetime
    String _displayName;
    String _itemName;
    int _number;
    int _userItemid;
    protected String _type;
    String _short_description;
    String _description;
    int _amount;
    byte[] _image_tn;
    byte[] _image;
    Timestamp _create_ts;

    public static final String USERID;
    public static final String ITEM_NAME;
    public static final String TYPE;
    public static final String SHORT_DESCRIPTION;
    public static final String DESCRIPTION;
    public static final String AMOUNT;
    public static final String IMAGE_TN;
    public static final String IMAGE;
    public static final String CREATE_TS;

    public static final String ITEMS_TABLE = "T_ITEMS";
    public static final String USER_ITEM_TABLE = "T_USER_ITEMS";

    static {
        USERID = "USERID";
        ITEM_NAME = "NAME";
        TYPE = "TYPE";
        SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
        DESCRIPTION = "DESCRIPTION";
        AMOUNT = "AMOUNT";
        IMAGE_TN = "IMAGE_TN";
        IMAGE = "IMAGE";
        CREATE_TS = "CREATE_TS";
    }

    public String getDisplayName() {
        return _displayName;
    }

    public void setDisplayName(String str) {
        _displayName = str;
    }

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String str) {
        _itemName = str;
    }

    public String getType() {
        return _type;
    }

    public void setType(String str) {
        _type = str;
    }

    public String getShortDescription() {
        return _short_description;
    }

    public void setShortDescription(String str) {
        _short_description = str;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String str) {
        _description = str;
    }

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int str) {
        _amount = str;
    }
    public int getNumber() {
        return _number;
    }

    public void setNumber(int str) {
        _number = str;
    }
    public byte[] getImage() {
        return _image_tn;
    }

    public void setImage(byte[] str) {
        if (str != null)
            _image = str;
    }

    public byte[] getImageIcon() {
        return _image;
    }

    public void setImageIcon(byte[] str) {
        if (str != null)
            _image_tn = str;
    }

    public Date getCreateUpdateTs() {
        return _create_ts;
    }

    public void setCreateUpdateTs(Timestamp str) {
        _create_ts = str;
    }

    public String getCreateUpdateTsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(_create_ts);
    }

    public DBCollection() {
    }


    public synchronized int save() throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
        try {
            //new Exception().printStackTrace();
            conn = ConnectionManager.getConnection("GameEngine");
            conn.setAutoCommit(true);
            StringBuilder sb = new StringBuilder("replace into ");
            sb.append(ITEMS_TABLE).append("( ");
            sb.append(ITEM_NAME).append(",");
            sb.append(TYPE).append(",");
            sb.append(SHORT_DESCRIPTION).append(",");
            sb.append(DESCRIPTION).append(",");
            sb.append(AMOUNT).append(",");
            sb.append(IMAGE_TN).append(",");
            sb.append(IMAGE).append(",");
            sb.append(CREATE_TS).append(")");
            sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?)");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, _itemName);
            ps.setString(2, _type);
            ps.setString(3, _short_description);
            ps.setString(4, _description);
            ps.setInt(5, _amount);
            ps.setBytes(6, _image_tn);
            ps.setBytes(7, _image);
            ps.setTimestamp(8, 
                            _create_ts = new java.sql.Timestamp(System.currentTimeMillis()));
            _cat.info(this.toString());
            r = ps.executeUpdate();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, 
                     "Unable to save ITEM_TABLE " + e.getMessage(), e);
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

    public synchronized int update() throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
        try {
            //new Exception().printStackTrace();
            conn = ConnectionManager.getConnection("GameEngine");
            conn.setAutoCommit(true);
            StringBuilder sb = new StringBuilder("update ");
            sb.append(ITEMS_TABLE).append(" set ");
            sb.append(TYPE).append("=?, ");
            sb.append(SHORT_DESCRIPTION).append("=?, ");
            sb.append(DESCRIPTION).append("=?, ");
            sb.append(AMOUNT).append("=?, ");
            sb.append(CREATE_TS).append("=?, ");
            sb.append(IMAGE_TN).append("=?, ");
            sb.append(IMAGE).append("=? where ");
            sb.append(ITEM_NAME).append("=?");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, _type);
            ps.setString(2, _short_description);
            ps.setString(3, _description);
            ps.setInt(4, _amount);
            ps.setTimestamp(5, 
                            _create_ts = new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setBytes(6, _image_tn);
            ps.setBytes(7, _image);
            ps.setString(8, _itemName);
            _cat.info(this.toString());
            r = ps.executeUpdate();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, "Unable to save news " + e.getMessage(), e);
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
            throw new DBException(e.getMessage() + " -- news");
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


    public static synchronized DBCollection[] getNoImage(String type) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        DBCollection sp = null;
        Vector<DBCollection> v = new Vector<DBCollection>();
        try {
            // new Exception().printStackTrace();
        	StringBuilder sb = new StringBuilder("select ");
            sb.append(ITEM_NAME).append(",  ");
            sb.append(SHORT_DESCRIPTION).append(",  ");
            sb.append(DESCRIPTION).append(",  ");
            sb.append(AMOUNT).append(",  ");
            //sb.append(IMAGE_TN).append(",  ");
            //sb.append(IMAGE).append(",  ");
            sb.append(CREATE_TS);
            sb.append(" from ").append(ITEMS_TABLE);
            sb.append(" where ").append(TYPE).append("=? ");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, type);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new DBCollection();
                sp._type = type;
                sp._itemName = r.getString(ITEM_NAME);
                sp._short_description = r.getString(SHORT_DESCRIPTION);
                sp._description = r.getString(DESCRIPTION);
                sp._amount = r.getInt(AMOUNT);
                //sp._image_tn = r.getBytes(IMAGE_TN);
                //sp._image = r.getBytes(IMAGE);
                sp._create_ts = r.getTimestamp(CREATE_TS);
                v.add(sp);
            }
            r.close();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, e.getMessage(), e);

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

        return v.toArray(new DBCollection[v.size()]);
    }

    public static synchronized byte[] getImageIcon(String name) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        byte[] tn = null;
        try {
            // new Exception().printStackTrace();
        	StringBuilder sb = new StringBuilder("select ");
            sb.append(IMAGE_TN);
            sb.append(" from ").append(ITEMS_TABLE);
            sb.append(" where ").append(ITEM_NAME).append("=? ");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                tn = r.getBytes(IMAGE_TN);
            }
            r.close();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, e.getMessage(), e);

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

        return tn;
    }

    public static synchronized byte[] getImage(String name) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        byte[] tn = null;
        try {
            // new Exception().printStackTrace();
        	StringBuilder sb = new StringBuilder("select ");
            sb.append(IMAGE);
            sb.append(" from ").append(ITEMS_TABLE);
            sb.append(" where ").append(ITEM_NAME).append("=? ");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            _cat.finest(sb.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                tn = r.getBytes(IMAGE);
            }
            r.close();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, e.getMessage(), e);

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

        return tn;
    }


    public static boolean delete(String name) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
        	StringBuilder sb = new StringBuilder("delete ");
            sb.append(" from ").append(ITEMS_TABLE).append(" where ");
            sb.append(ITEM_NAME).append("=?");
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            boolean r = ps.execute();
            ps.close();
            conn.close();
            return r;
        } catch (SQLException e) {
            _cat.severe("Error in getting game " + e.getMessage());
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
            throw new DBException(e.getMessage() + " -- while getting news");
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


    public static DBCollection[] getUserItems(String dn, 
                                       String type) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        DBCollection sp = null;
        Vector<DBCollection> v = new Vector<DBCollection>();
        try {
            // new Exception().printStackTrace();
        	StringBuilder sb = new StringBuilder("select ");
            sb.append(USER_ITEM_TABLE).append(".ID").append(",  ");
            sb.append(USER_ITEM_TABLE).append(".NUMBER").append(",  ");
            sb.append(ITEMS_TABLE).append(".").append(ITEM_NAME).append(",  ");
            sb.append(ITEMS_TABLE).append(".").append(SHORT_DESCRIPTION).append(",  ");
            sb.append(ITEMS_TABLE).append(".").append(DESCRIPTION).append(",  ");
            sb.append(ITEMS_TABLE).append(".").append(AMOUNT);
            sb.append(" from ").append(ITEMS_TABLE).append(" , ").append(USER_ITEM_TABLE);
            sb.append(" where T_ITEMS.NAME=T_USER_ITEMS.ITEM_NAME and T_USER_ITEMS.USERID=? and T_USER_ITEMS.TYPE=? ");
            System.out.println(sb.toString());
            conn = ConnectionManager.getConnection("GameEngine");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, dn);
            ps.setString(2, type);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                sp = new DBCollection();
                sp._displayName = dn;
                sp._type = type;
                sp._userItemid = r.getInt("ID");
                sp._number = r.getInt("NUMBER");
                sp._itemName = r.getString(ITEM_NAME);
                sp._short_description = r.getString(SHORT_DESCRIPTION);
                sp._description = r.getString(DESCRIPTION);
                sp._amount = r.getInt(AMOUNT);
                v.add(sp);
            }
            r.close();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE, e.getMessage(), e);

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

        return v.toArray(new DBCollection[v.size()]);
    }


    public static int addUserItem(String user, String item, String type, int num) throws DBException {
        PreparedStatement ps = null;
        Connection conn = null;
        int r = -1;
        try {
            //new Exception().printStackTrace();
            conn = ConnectionManager.getConnection("GameEngine");
            conn.setAutoCommit(true);
            StringBuilder sb = new StringBuilder("insert into ");
            sb.append(USER_ITEM_TABLE).append("( ");
            sb.append(USERID).append(",");
            sb.append("ITEM_NAME,");
            sb.append("NUMBER,");
            sb.append(TYPE).append(",");
            sb.append(CREATE_TS).append(")");
            sb.append(" values ( ?, ?, ?, ?, ?)");
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, user);
            ps.setString(2, item);
            ps.setInt(3, num);
            ps.setString(4, type);
            ps.setTimestamp(5,  new java.sql.Timestamp(System.currentTimeMillis()));
            r = ps.executeUpdate();
            ps.close();

            conn.close();
        } catch (SQLException e) {
            _cat.log(Level.SEVERE,  e.getMessage(), e);

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
    

        public static boolean deleteUserItem(int id) throws DBException {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
            	StringBuilder sb = new StringBuilder("delete ");
                sb.append(" from ").append(USER_ITEM_TABLE).append(" where ");
                sb.append("ID").append("=?");
                conn = ConnectionManager.getConnection("GameEngine");
                ps = conn.prepareStatement(sb.toString());
                ps.setInt(1, id);
                boolean r = ps.execute();
                ps.close();
                conn.close();
                return r;
            } catch (SQLException e) {
                _cat.severe( e.getMessage());
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
        }



}
