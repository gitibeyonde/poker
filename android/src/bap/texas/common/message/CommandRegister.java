package bap.texas.common.message;

import java.util.HashMap;


public class CommandRegister
    extends Command {
  String _name;
  String _fname;
  String _lname;
  String _password;
  String _email;
  String _dob;
  String _city;
  String _country;
  String _zip;
  int _gender;
  String _bonus_code;
  String _source;
  String _affiliate;

  public CommandRegister(String session, String name, String pass, String email,
                         int gender, String bc, String dob) {
    super(session, Command.C_REGISTER);
    _name = name;
    _password = pass;
    _email = email;
    _gender = gender;
    _bonus_code = bc;
    _source = "pokerking";
    _affiliate = "admin";
    _dob = dob;
  }

  /**public CommandRegister(String session, String name, String pass, String email,
                         String fname, String lname, String city, String country, String zip,
                         int gender, String bc, String sc, String aff) {
    super(session, Command.C_REGISTER);
    _name = name;
    _password = pass;
    _email = email;
    _gender = gender;
    _bonus_code = bc;
    _source = sc;
    _affiliate = aff;
    _fname = fname;
    _lname = lname;
    _city = city;
    _country = country;
    _zip = zip;
  }**/

  /**public CommandRegister(String session, String name, String pass, String email,
                         int gender, String bc, String sc) {
    super(session, Command.C_REGISTER);
    _name = name;
    _password = pass;
    _email = email;
    _gender = gender;
    _bonus_code = bc;
    _source = sc;
    _affiliate = "admin";
  }**/

  /**public CommandRegister(String session, String name, String pass, String email,
                         String fname, String lname, String city, String country, String zip,
                         int gender, String bc, String sc) {
    super(session, Command.C_REGISTER);
    _name = name;
    _password = pass;
    _email = email;
    _gender = gender;
    _bonus_code = bc;
    _source = sc;
    _affiliate = "admin";
    _fname = fname;
    _lname = lname;
    _city = city;
    _country = country;
    _zip = zip;
  }**/

  public CommandRegister(String str) {
    super(str);
    _name = (String) _hash.get("UN");
    _fname = (String) _hash.get("FN");
    _lname = (String) _hash.get("LN");
    _password = (String) _hash.get("UP");
    _email = (String) _hash.get("UE");
    _dob = (String) _hash.get("DOB");
    _city = (String) _hash.get("CIT");
    _country = (String) _hash.get("CNT");
    _zip = (String) _hash.get("ZIP");
    _gender = Byte.parseByte( (String) _hash.get("UG"));
    _bonus_code = (String) _hash.get("BC");
    _source = (String) _hash.get("SC");
    _affiliate = (String) _hash.get("AFF");
  }

  public CommandRegister(HashMap<String, String> str) {
    super(str);
    _name = (String) _hash.get("UN");
    _fname = (String) _hash.get("FN");
    _lname = (String) _hash.get("LN");
    _password = (String) _hash.get("UP");
    _email = (String) _hash.get("UE");
    _dob = (String) _hash.get("DOB");
    _city = (String) _hash.get("CIT");
    _country = (String) _hash.get("CNT");
    _zip = (String) _hash.get("ZIP");
    _gender = Byte.parseByte( (String) _hash.get("UG"));
    _bonus_code = (String) _hash.get("BC");
    _source = (String) _hash.get("SC");
    _affiliate = (String) _hash.get("AFF");
  }

  public String getUserName() {
    return _name;
  }

  public String getUserFirstName() {
    return _fname;
  }

  public String getUserLastName() {
    return _lname;
  }

  public String getPassword() {
    return _password;
  }

  public String getEmail() {
    return _email;
  }

  public String getCity() {
    return _city;
  }

  public String getCountry() {
    return _country;
  }

  public String getZip() {
    return _zip;
  }

  public String getBonusCode() {
    return _bonus_code;
  }

  public String getSource() {
    return _source;
  }

  public String getDob() {
    return _dob;
  }
  public int getGender() {
    return _gender;
  }

  public String getAffiliate() {
    return _affiliate;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&UN=").append(_name);
    str.append("&FN=").append(_fname);
    str.append("&LN=").append(_lname);
    str.append("&UP=").append(_password);
    str.append("&UE=").append(_email);
    str.append("&DOB=").append(_dob);
    str.append("&CIT=").append(_city);
    str.append("&CNT=").append(_country);
    str.append("&ZIP=").append(_zip);
    str.append("&UG=").append(_gender);
    str.append("&BC=").append(_bonus_code);
    str.append("&SC=").append(_source);
    str.append("&AFF=").append(_affiliate);
    return str.toString();
  }

}
