package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseInt
    extends Response {
  int _intVal;

  public ResponseInt(int result, int rname, int intVal) {
    super(result, rname);
    _intVal = intVal;
  }

  public ResponseInt(String com) {
    super(com);
    _intVal = Integer.parseInt( (String) _hash.get("IV"));

  }

  public ResponseInt(HashMap com) {
    super(com);
    _intVal = Integer.parseInt( (String) _hash.get("IV"));
  }

  public int getIntVal() {
    return _intVal;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&IV=").append(_intVal);
    return str.toString();
  }

}
