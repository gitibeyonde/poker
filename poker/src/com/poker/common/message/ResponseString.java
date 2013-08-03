package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseString
    extends Response {
  String _val;

  public ResponseString(int result, int rname, String val) {
    super(result, rname);
    _val = val;
  }

  public ResponseString(String com) {
    super(com);
    _val = (String) _hash.get("IV");

  }

  public ResponseString(HashMap com) {
    super(com);
    _val = (String) _hash.get("IV");
  }

  public String getStringVal() {
    return _val;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&IV=").append(_val);
    return str.toString();
  }

}
