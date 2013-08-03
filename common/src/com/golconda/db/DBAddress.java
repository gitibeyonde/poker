package com.golconda.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.agneya.util.ConnectionManager;


public class DBAddress {
    static Logger _cat = Logger.getLogger(DBAddress.class.getName());
//ADDRESS_ID_SEQ_PK    integer,
//       ADDRESS_TYPE_ID_FK   integer NOT null,
//       USER_ID_FK           nvarchar(20) NOT null,
//       STREET1              nvarchar(20) null,
//       STREET2              VARCHAR(20) null,
//       CITY                 nvarchar(20) null,
//       STATE                nvarchar(20) null,
//       COUNTRY              nvarchar(60) null,
//       ZIP                  nvarchar(20) null,
//       PHONE_HOME           nvarchar(20) null,
//       PHONE_OFFICE         nvarchar(20) null,
//       PHONE_MOBILE         nvarchar(20) null,
//       PHONE2               nvarchar(20) null,
//       NAME_USED            nvarchar(60) null,
//       FAX        
    public int _id;
    public String _uid;
    public String _type="billing";  // billing, shipping
    public String _street1;
    public String _street2;
    public String _city;
    public String _state;
    public String _country;
    public String _zip;
    public String _phoneHome;
    public String _phoneOffice;
    public String _phoneMobile;
    public String _phone2;
    public String _fax;
    public String _nameUsed;
    public String _countryCode;
    public String _phoneNo;

	final String ADDRESS_ID_SEQ_PK="ADDRESS_ID_SEQ_PK";
    final String USER_ID_FK="USER_ID_FK";
    final String ADDRESS_TYPE_ID_FK="ADDRESS_TYPE_ID_FK";
    final String STREET1="STREET1";
    final String STREET2="STREET2";
    final String CITY="CITY";
    final String STATE="STATE";
    final String COUNTRY="COUNTRY";
    final String ZIP="ZIP";
    final String PHONE_HOME="PHONE_HOME";
    final String PHONE_MOBILE="PHONE_MOBILE";
    final String PHONE2="PHONE2";
    final String FAX="FAX";
    final String NAME_USED="NAME_USED";
    
    public final static String ADDRESS_TYPE_BILLING="billing";    
    public final static String ADDRESS_TYPE_SHIPPING="shipping";   
    public final static  String ADDRESS_TYPE_AGENT="agent";  // non editable by the player
    
    public void setUid(String s) {        this._uid=s;    }
    public String getUid(){        return _uid;    }
    public String getType(){    	return _type ;    }
    public void setType(String t){    	this._type=t;
    }
    public String getStreet1(){    	return _street1;    }
    public void setStreet1(String street1){
    	this._street1=street1;
    }
    public String getStreet2(){    	return _street2;    }
    public void setStreet2(String street2){
    	this._street2=street2;
    }
    public String getCity(){    	return _city;    }
    public void setCity(String city){
    	this._city=city;
    }
    public String getState(){    	return _state ;    }
    public void setState(String state){
    	this._state=state;
    }
    public String getCountry(){    	return _country;    }
    public void setCountry(String country){
    	this._country=country;
    }
    public String getZip(){    	return _zip;    }
    public void setZip(String zip){
    	this._zip=zip;
    }
    
   
	public String getCountryCode() {
		//System.out.println("getCountryCode()"+this._countryCode);
    	return this._countryCode;	
    }
	public void setCountryCode(String countryCode) {
		//System.out.println("setCountryCode()"+this._countryCode);
		this._countryCode = countryCode;
	}
	public String getPhoneNo() {
		return _phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		//System.out.println("_phoneNo="+_phoneNo);
		_phoneNo = phoneNo;
	}

    public String getPhoneHome(){ 
    	return this._phoneHome;    
    }
    public void setPhoneHome(String phoneHome ){
    	//System.out.println("_phoneHome setter in DBAddress="+_phoneHome);
    	this._phoneHome =phoneHome;
    }
    public String getPhoneOffice(){    	return _phoneOffice;    }
    public void setPhoneOffice(String phoneOffice){
    	this._phoneOffice=phoneOffice;
    }
    public String getPhoneMobile(){    	return _phoneMobile;    }
    public void setPhoneMobile(String phoneMobile){
    	this._phoneMobile=phoneMobile;
    }
    public String getPhone2(){    	return _phone2;    }
    public void setPhone2(String phone2){
    	this._phone2=phone2;
    }
    public String getFax(){    	return _fax;    }
    public void setFax(String fax){
    	this._fax=fax;
    }
    public String getNameUsed(){    	return _nameUsed;    }
    public void setNameUsed(String nameUsed){
    	this._nameUsed=nameUsed;
    }
    public DBAddress(){
        
    }
   

    public DBAddress(int id)  throws DBException  {
        get(id);
    }
    
    public void get(String uname) throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
        	StringBuilder sb = new StringBuilder("select ");
          sb.append(USER_ID_FK).append(" ,");
          sb.append(ADDRESS_TYPE_ID_FK).append(" ,");
          sb.append(STREET1).append(" ,");
          sb.append(STREET2).append(" ,");
          sb.append(CITY).append(" ,");
          sb.append(STATE).append(" ,");
          sb.append(COUNTRY).append(" ,");
          sb.append(ZIP).append(" ,");
          sb.append(PHONE_HOME).append(" ,");
          sb.append(PHONE_MOBILE).append(" ,");
          sb.append(PHONE2).append(" ,");
          sb.append(FAX).append(" ,");
          sb.append(NAME_USED);
          sb.append(" from T_ADDRESS where USER_ID_FK= ?");
          conn = ConnectionManager.getConnection("GameEngine");
          ps = conn.prepareStatement(sb.toString());
          ps.setString(1, uname);
          _cat.fine(sb.toString());
          ResultSet r = ps.executeQuery();
          if (r.next()) {
              //_id = id;
              _uid = r.getString(USER_ID_FK);
              _type = r.getString(ADDRESS_TYPE_ID_FK);
              _street1 =r.getString(STREET1);
              _street2 =r.getString(STREET2);
              _city =r.getString(CITY);
              _state =r.getString(STATE);
              _country =r.getString(COUNTRY);
              _zip =r.getString(ZIP);
              _phoneHome =r.getString(PHONE_HOME);
              if(_phoneHome !=null){
              _countryCode = (String)_phoneHome.substring(0,_phoneHome.indexOf("|"));
              _countryCode =_countryCode!=""?_countryCode:null;
  			  _phoneNo = (String)_phoneHome.substring(_phoneHome.indexOf("|")+1,_phoneHome.length());
              _phoneNo = _phoneNo!=""?_phoneNo:null;
              }
  			  _phoneMobile =r.getString(PHONE_MOBILE);
              _phone2 =r.getString(PHONE2);
              _fax =r.getString(FAX);
              _nameUsed =r.getString(NAME_USED);
          }
            r.close();
            ps.close();
            conn.close();
        }
        catch (SQLException e) {
          _cat.severe("Error in getting  address  "+ e.getMessage());
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
          throw new DBException(e.getMessage() + " -- while getting  affiliate request");
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
    
    public void get(int id) throws DBException {
      Connection conn = null;
      PreparedStatement ps = null;
      try {
    	  StringBuilder sb = new StringBuilder("select ");
        sb.append(USER_ID_FK).append(" ,");
        sb.append(ADDRESS_TYPE_ID_FK).append(" ,");
        sb.append(STREET1).append(" ,");
        sb.append(STREET2).append(" ,");
        sb.append(CITY).append(" ,");
        sb.append(STATE).append(" ,");
        sb.append(COUNTRY).append(" ,");
        sb.append(ZIP).append(" ,");
        sb.append(PHONE_HOME).append(" ,");
        sb.append(PHONE_MOBILE).append(" ,");
        sb.append(PHONE2).append(" ,");
        sb.append(FAX).append(" ,");
        sb.append(NAME_USED);
        sb.append(" from T_ADDRESS where ADDRESS_ID_SEQ_PK= ?");
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setInt(1, id);
        _cat.fine(sb.toString());
        ResultSet r = ps.executeQuery();
        if (r.next()) {
            _id = id;
            _uid = r.getString(USER_ID_FK);
            _type = r.getString(ADDRESS_TYPE_ID_FK);
            _street1 =r.getString(STREET1);
            _street2 =r.getString(STREET2);
            _city =r.getString(CITY);
            _state =r.getString(STATE);
            _country =r.getString(COUNTRY);
            _zip =r.getString(ZIP);
            _phoneHome =r.getString(PHONE_HOME);
            _phoneMobile =r.getString(PHONE_MOBILE);
            _phoneMobile =r.getString(PHONE_MOBILE);
            _phone2 =r.getString(PHONE2);
            _fax =r.getString(FAX);
            _nameUsed =r.getString(NAME_USED);
        }
          r.close();
          ps.close();
          conn.close();
      }
      catch (SQLException e) {
        _cat.severe("Error in getting  address  "+ e.getMessage());
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
        throw new DBException(e.getMessage() + " -- while getting  affiliate request");
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
           
   public int save() throws DBException {
       return save(null);
   }
    
    public int save(Connection conn) throws DBException {
     int r = -1;
    boolean autocommit = conn != null;
     PreparedStatement ps = null;
       try {
    	   System.out.println("values in DBAddress="+_uid+","+_type+","+_street1+","+_phoneHome
    			   				+","+_city+","+_country);
    	   StringBuilder sb = new StringBuilder("replace into T_ADDRESS ( ");
         sb.append(USER_ID_FK).append(",");
         sb.append(ADDRESS_TYPE_ID_FK).append(",");
         sb.append(STREET1).append(",");
         sb.append(STREET2).append(",");
         sb.append(CITY).append(",");
         sb.append(STATE).append(",");
         sb.append(COUNTRY).append(",");
         sb.append(ZIP).append(",");
         sb.append(PHONE_HOME).append(",");
         sb.append(PHONE_MOBILE).append(",");
         sb.append(PHONE2).append(",");
         sb.append(FAX).append(",");
         sb.append(NAME_USED).append(")");
      
         sb.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
         _cat.fine(sb.toString());
        conn = conn == null ? ConnectionManager.getConnection("GameEngine") : conn;
         ps = conn.prepareStatement(sb.toString());
         int i = 1;
         ps.setString(i++, _uid);
         ps.setString(i++, _type);
         ps.setString(i++, _street1);
         ps.setString(i++, _street2);
         ps.setString(i++, _city);
         ps.setString(i++, _state);
         ps.setString(i++, _country);
         ps.setString(i++, _zip);
         if(_countryCode != null && _phoneNo != null){
             _phoneHome = _countryCode+"|"+_phoneNo;
             ps.setString(i++, _phoneHome);
         }else{
        	 ps.setString(i++, _phoneHome=null); 
         }
         ps.setString(i++, _phoneMobile);
         ps.setString(i++, _phone2);
         ps.setString(i++, _fax);
         ps.setString(i++, _nameUsed);
         
         r = ps.executeUpdate();
         // if it is private table update the player list too
         _cat.fine(this.toString());
         ps.close();
        if (autocommit)
            conn.close();
       }
       catch (SQLException e) {
         _cat.severe("Unable to save  affiliate request" + e.getMessage());

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
         throw new DBException(e.getMessage() + " -- while getting  affiliate request");
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
     return r;
    }
    
    public int update(Connection conn) throws DBException {
        int r = -1;
       boolean autocommit = conn != null;
        PreparedStatement ps = null;
          try {
       	   System.out.println("values in DBAddress="+_uid+","+_type+","+_street1+","+_phoneHome
       			   				+","+_city+","+_country);
       	StringBuilder sb = new StringBuilder("update T_ADDRESS set ");
            sb.append(USER_ID_FK).append(" = ?, ");
            sb.append(ADDRESS_TYPE_ID_FK).append(" = ?, ");
            sb.append(STREET1).append(" = ?, ");
            sb.append(STREET2).append(" = ?, ");
            sb.append(CITY).append(" = ?, ");
            sb.append(STATE).append(" = ?, ");
            sb.append(COUNTRY).append(" = ?, ");
            sb.append(ZIP).append(" = ?, ");
            sb.append(PHONE_HOME).append(" = ?, ");
            sb.append(PHONE_MOBILE).append(" = ?, ");
            sb.append(PHONE2).append(" = ?, ");
            sb.append(FAX).append(" = ?, ");
            sb.append(NAME_USED).append(" = ? ");
            sb.append(" where ");
            sb.append(USER_ID_FK).append("= ?");
            _cat.finest(sb.toString());
           conn = conn == null ? ConnectionManager.getConnection("GameEngine") : conn;
            ps = conn.prepareStatement(sb.toString());
            int i = 1;
            ps.setString(i++, _uid);
            ps.setString(i++, _type);
            ps.setString(i++, _street1);
            ps.setString(i++, _street2);
            ps.setString(i++, _city);
            ps.setString(i++, _state);
            ps.setString(i++, _country);
            ps.setString(i++, _zip);
            if(_countryCode != null && _phoneNo != null){
             _phoneHome = _countryCode+"|"+_phoneNo;
            }
            ps.setString(i++, _phoneHome);
            ps.setString(i++, _phoneMobile);
            ps.setString(i++, _phone2);
            ps.setString(i++, _fax);
            ps.setString(i++, _nameUsed);
            ps.setString(i++,_uid );
            
            r = ps.executeUpdate();
            // if it is private table update the player list too
            _cat.fine(this.toString());
            ps.close();
           if (autocommit)
               conn.close();
          }
          catch (SQLException e) {
            _cat.severe("Unable to save  affiliate request" + e.getMessage());

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
            throw new DBException(e.getMessage() + " -- while getting  affiliate request");
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
        return r;
       }
     
      public  void reset(){
    	  	_id=0;
    	  	_uid="";
	  		_type="";  
	  		_street1="";
	  		_street2="";
	  		_city="";
	  		_state="";
	  		_country="";
	  		_zip="";
	  		_phoneHome="";
	  		_phoneOffice="";
	  		_phoneMobile="";
	  		_phone2="";
	  		_fax="";
	  		_nameUsed="";
	  		_phoneNo=null;
	  		_countryCode =null;
      }
     
      public String getReset(){
    	  reset();
    	  return "";
      }
      public String getUserAddress(){
      	   try {
      		get(_uid);
      		System.out.println("values in DBAddress.getUserAddress()="+_country+","+_street1+","+_phoneHome);
      		
      	} catch (DBException e) {
      		e.printStackTrace();
      	}
      	   return "";
      	   
         }
}