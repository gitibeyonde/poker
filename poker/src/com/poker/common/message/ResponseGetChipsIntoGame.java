package com.poker.common.message;

import com.agneya.util.Utils;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseGetChipsIntoGame
    extends Response {
  String _tid;
  double _amt;
  private double _uw;
  private double _ub;
  private double _urw;
  private double _urb;

  public ResponseGetChipsIntoGame(int result, String tid, double amt, double uw,
                                  double br, double urw, double rbr) {
    super(result, R_GET_CHIPS_INTO_GAME);
    _tid = tid;
    _amt = amt;
    _uw = uw;
    _ub = br;
    _urw = urw;
    _urb = rbr;
  }

  public ResponseGetChipsIntoGame(String com) {
    super(com);
    if (getResult() == 1 ) {
      _tid =  (String) _hash.get("TID");
      _amt = Double.parseDouble( (String) _hash.get("AMT"));
      _uw = Double.parseDouble( (String) _hash.get("UW"));
      _ub = Double.parseDouble( (String) _hash.get("UB"));
      _urw = Double.parseDouble( (String) _hash.get("URW"));
      _urb = Double.parseDouble( (String) _hash.get("URB"));
    }
  }

  public ResponseGetChipsIntoGame(HashMap com) {
    super(com);
    if (getResult() == 1 ) {
      _tid = (String) _hash.get("TID");
      _amt = Double.parseDouble( (String) _hash.get("AMT"));
      _uw = Double.parseDouble( (String) _hash.get("UW"));
      _ub = Double.parseDouble( (String) _hash.get("UB"));
      _urw = Double.parseDouble( (String) _hash.get("URW"));
      _urb = Double.parseDouble( (String) _hash.get("URB"));
    }
  }

  public String getGameId() {
    return _tid;
  }

  public double getAmount() {
    return _amt;
  }
  

  public double getPlayWorth() {
    return _uw;
  }

  public double getPlayBankRoll() {
    return _ub;
  }

  public double getRealWorth() {
    return _urw;
  }

  public double getRealBankRoll() {
    return _urb;
  }


  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&TID=").append(_tid).append("&AMT=").append(Utils.
        getRoundedString(_amt));
    str.append("&UW=").append(Utils.getRoundedString(_uw));
    str.append("&UB=").append(Utils.getRoundedString(_ub));
    str.append("&URW=").append(Utils.getRoundedString(_urw));
    str.append("&URB=").append(Utils.getRoundedString(_urb));
    return str.toString();
  }

}
