package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseTournyMyTable
    extends Response {
  private String _gid;
  private int _pos;

  public ResponseTournyMyTable(int result, String tid, int pos) {
    super(result, R_TOURNYMYTABLE);
    _gid = tid;
    _pos = pos;
  }

  public ResponseTournyMyTable(String str) {
    super(str);
    _gid =  (String) _hash.get("GID");
    _pos = Integer.parseInt( (String) _hash.get("POS"));
  }

  public ResponseTournyMyTable(HashMap str) {
    super(str);
    _gid = (String) _hash.get("GID");
    _pos = Integer.parseInt( (String) _hash.get("POS"));
  }

  public String getGameTid() {
    return _gid;
  }

  public int getPosition() {
    return _pos;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&GID=").append(_gid);
    str.append("&POS=").append(_pos);
    return str.toString();
  }

  public boolean equal(ResponseTournyMyTable r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
