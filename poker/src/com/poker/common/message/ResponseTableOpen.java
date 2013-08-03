package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseTableOpen
    extends Response {
  private String _tid;
  private int _pos;
  private String _ge;

 

  public ResponseTableOpen(String tid, int pos, String ge) {
    super(1, R_TABLE_OPEN);
    _tid = tid;
    _pos = pos;
    _ge = ge;
  }

  public ResponseTableOpen(HashMap str) {
    super(str);
    _tid = (String) _hash.get("TID");
    _pos = Integer.parseInt((String) _hash.get("POS"));
    _ge = (String) _hash.get("GE");
  }
  
  public String getTableName(){
      return _tid;
  }
  
  public int getPosition(){
      return _pos;
  }

  public String getGE(){
      return _ge;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&TID=").append(_tid);
    str.append("&POS=").append(_pos);
    str.append("&GE=").append(_ge);
    return str.toString();
  }

  public boolean equal(ResponseTableOpen r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
