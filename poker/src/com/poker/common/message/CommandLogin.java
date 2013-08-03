package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandLogin
    extends Command {
  String _name;
  String _password;
  String _token;
  String _affiliate;

  public CommandLogin(String session, String name, String pass, String token) {
    super(session, Command.C_LOGIN);
    _name = name;
    _password = pass;
    _token = token;
    _affiliate="admin";
  }

  public CommandLogin(String session, String name, String pass, String token,
                      String aff) {
    super(session, Command.C_LOGIN);
    _name = name;
    _password = pass;
    _token = token;
    _affiliate = aff;
  }

  public CommandLogin(String session, String name, String pass) {
    super(session, Command.C_LOGIN);
    _name = name;
    _password = pass;
  }

  public CommandLogin(String com) {
    super(com);
    _name = (String) _hash.get("UN");
    _password = (String) _hash.get("UP");
    _token = (String) _hash.get("UT");
    _affiliate = (String) _hash.get("AFF");
  }

  public CommandLogin(HashMap com) {
    super(com);
    _name = (String) _hash.get("UN");
    _password = (String) _hash.get("UP");
    _token = (String) _hash.get("UT");
    _affiliate = (String) _hash.get("AFF");
  }

  public String getUserName() {
    return _name;
  }

  public String getPassword() {
    return _password;
  }

  public String getToken() {
    return _token == null ? "" : _token;
  }

  public String getAffiliate() {
    return _affiliate == null ? "" : _affiliate.split("\\|")[0];
  }

  public String getProvider() {
    try {
      return _affiliate == null ? "" : _affiliate.split("\\|")[1];
    }
    catch (Exception e) {
      return "";
    }
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&UN=").append(_name).append("&UP=").append(_password).append(
        "&UT=").append(_token).append(
        "&AFF=").append(_affiliate);
    return str.toString();
  }

}
