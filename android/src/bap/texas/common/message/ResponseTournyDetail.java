package bap.texas.common.message;

import java.util.*;

public class ResponseTournyDetail
    extends Response {
  private String _tournyEvent;
  private String[] _games;
  private int _cnt;

  public ResponseTournyDetail(int result, String te, String[] ge) {
    super(result, R_TOURNYDETAIL);
    _tournyEvent = te;
    if (ge != null) {
      _cnt = ge.length;
    }
    else {
      _cnt = 0;
    }
    _games = ge;
  }

  public ResponseTournyDetail(String str) {
    super(str);
    _tournyEvent = (String) _hash.get("TE");
    _cnt = Integer.parseInt( (String) _hash.get("GMCNT"));
    _games = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _games[i] = (String) _hash.get("G" + i);
    }
  }

  public ResponseTournyDetail(HashMap<String, String> str) {
    super(str);
    _tournyEvent = (String) _hash.get("TE");
    _cnt = Integer.parseInt( (String) _hash.get("GMCNT"));
    _games = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _games[i] = (String) _hash.get("G" + i);
    }

  }

  public String getTournyEvent() {
    return _tournyEvent;
  }

  public void setTournyEvent(String ge) {
    _tournyEvent = ge;
  }

  public String[] getTournyGameEvent() {
    return _games;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&TE=").append(_tournyEvent);
    str.append("&GMCNT=").append(_cnt);
    for (int i = 0; i < _cnt; i++) {
      str.append("&G").append(i).append("=").append(_games[i]);
    }
    return str.toString();
  }

  public boolean equal(ResponseTournyDetail r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
