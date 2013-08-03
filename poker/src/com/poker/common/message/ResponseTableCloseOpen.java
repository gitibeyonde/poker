package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseTableCloseOpen
    extends Response {
  private String _close_tid;
  private String _open_tid;
  private int _pos;
  private String _ge;

 

  public ResponseTableCloseOpen(String close_tid, String open_tid, int pos, String ge) {
    super(1, R_TABLE_OPEN_CLOSE);
    _close_tid = close_tid;
    _open_tid = open_tid;
    _pos = pos;
    _ge = ge;
    if (ge==null || ge.length() < 10){
    	new Exception().printStackTrace();
    }
  }

  public ResponseTableCloseOpen(HashMap str) {
    super(str);
    _close_tid = (String) _hash.get("CLOSE_TID");
    _open_tid = (String) _hash.get("OPEN_TID");
    _pos = Integer.parseInt((String) _hash.get("POS"));
    _ge = (String) _hash.get("GE");
  }
  
  public String getCloseTableName(){
      return _close_tid;
  }

  public String getOpenTableName(){
      return _open_tid;
  }
  public int getPosition(){
      return _pos;
  }

  public String getGE(){
      return _ge;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&CLOSE_TID=").append(_close_tid);
    str.append("&OPEN_TID=").append(_open_tid);
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
