package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseTournyList
    extends Response {
  private String[] _tournaments;
  private int _cnt;

  public ResponseTournyList(int result, String[] tournaments) {
    super(result, R_TOURNYLIST);
    _cnt = tournaments.length;
    _tournaments = tournaments;
  }

  public ResponseTournyList(String str) {
    super(str);
    _cnt = Integer.parseInt( (String) _hash.get("TNCNT"));
    _tournaments = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _tournaments[i] = (String) _hash.get("T" + i);
    }
  }

  public ResponseTournyList(HashMap str) {
    super(str);
    _cnt = Integer.parseInt( (String) _hash.get("TNCNT"));
    _tournaments = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _tournaments[i] = (String) _hash.get("T" + i);
    }
  }

  public int getTournyCount() {
    return _cnt;
  }

  public TournyEvent getTourny(int i) {
    return new TournyEvent(_tournaments[i]);
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&TNCNT=").append(_cnt);
    for (int i = 0; i < _cnt; i++) {
      str.append("&T").append(i).append("=").append(_tournaments[i]);
    }
    return str.toString();
  }

  public boolean equal(ResponseTournyList r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
