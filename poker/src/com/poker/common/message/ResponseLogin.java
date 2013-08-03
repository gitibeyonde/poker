package com.poker.common.message;

import com.agneya.util.Utils;

import com.golconda.message.Response;

import java.util.HashMap;
import java.util.Vector;


public class ResponseLogin
    extends Response {
  private int _ug;
  private double _uw;
  private String _avtr;
  private double _urw;
  private String _city;
  private int _prf;
  private int _rank;
  private int _all_in;
  private double _loss_limit;
  private Vector _tidv;
  private Vector _posv;

  public ResponseLogin(int result, int ug, double uw, String avtr, double urw,
                       String city, int prf, int rank, int all_in, double rll) {
    super(result, R_LOGIN);
    _ug = ug;
    _uw = uw;
    _avtr = avtr;
    _urw = urw;
    _city = city;
    _prf = prf;
    _rank = rank;
    _all_in = all_in;
    _loss_limit = rll;
  }

  public ResponseLogin(int result, int ug, double uw, String avtr, double urw,
                       String city, int prf, int rank, int all_in, double rll, Vector tidv,
                       Vector posv) {
    super(result, R_LOGIN);
    _ug = ug;
    _uw = uw;
    _avtr = avtr;
    _urw = urw;
    _city = city;
    _prf = prf;
    _rank = rank;
    _all_in = all_in;
    _tidv = tidv;
    _posv = posv;
    _loss_limit = rll;
  }

  /**
   * This constructor is used to create a register response
   */
  public ResponseLogin(boolean isReg, int result, int ug, double uw, String avtr,
                       double urw, String city, int prf, int rank, int all_in, double rll) {
    super(result, R_REGISTER);
    _ug = ug;
    _uw = uw;
    _avtr = avtr;
    _urw = urw;
    _city = city;
    _prf = prf;
    _rank = rank;
    _all_in = all_in;
    _loss_limit = rll;
  }

  public ResponseLogin(HashMap str) {
    super(str);
    if (getResult() == 1 ) {
      _ug = Integer.parseInt( (String) _hash.get("UG"));
      _uw = Double.parseDouble( (String) _hash.get("UW"));
      _avtr =  (String) _hash.get("AVTR");
      _urw = Double.parseDouble( (String) _hash.get("URW"));
      _city = (String) _hash.get("CIT");
      _prf = Integer.parseInt( (String) _hash.get("PRF"));
      _rank = Integer.parseInt( (String) _hash.get("RK"));
      _all_in = Integer.parseInt( (String) _hash.get("AI"));
      _loss_limit = Double.parseDouble( (String) _hash.get("RLL"));
    }
  }

  public double getPlayWorth() {
    return _uw;
  }

  public String getAvtar() {
    return _avtr;
  }

  public double getRealWorth() {
    return _urw;
  }

  public String getCity() {
    return _city;
  }

  public int getGender() {
    return _ug;
  }

  public int getPreferences() {
    return _prf;
  }

  public int getRank() {
    return _rank;
  }

  public int getAllIn() {
    return _all_in;
  }

  public double getRealLossLimit() {
    return _loss_limit;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&UG=").append(_ug);
    str.append("&UW=").append(Utils.getRoundedString(_uw));
    str.append("&AVTR=").append(_avtr);
    str.append("&URW=").append(Utils.getRoundedString(_urw));
    str.append("&CIT=").append(_city);
    str.append("&PRF=").append(_prf);
    str.append("&RK=").append(_rank);
    str.append("&AI=").append(_all_in);
    str.append("&RLL=").append(Utils.getRoundedString(_loss_limit));
    if (_tidv != null) {
      str.append("&TCNT=").append(_tidv.size());
      for (int i = 0; _tidv != null && i < _tidv.size(); i++) {
        str.append("&TABLE" +
            i).append("=").append(_tidv.get(i)).append("|").append(_posv.get(i));
      }
    }
    return str.toString();
  }

  public boolean equal(ResponseLogin r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
