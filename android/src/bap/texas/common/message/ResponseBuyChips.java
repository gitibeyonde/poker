package bap.texas.common.message;

import java.util.HashMap;

import bap.texas.util.*;


public class ResponseBuyChips
    extends Response {
  private double _uw;
  private double _ub;
  private double _urw;
  private double _urb;
  private String _tid;

  public ResponseBuyChips(int result, double uw, double br, double urw,
                          double rbr) {
    super(result, R_BUYCHIPS);
    _uw = uw;
    _ub = br;
    _urw = urw;
    _urb = rbr;
  }

  public ResponseBuyChips(int result, double uw, double br, double urw,
                          double rbr, String tid) {
    super(result, R_BUYCHIPS);
    _uw = uw;
    _ub = br;
    _urw = urw;
    _urb = rbr;
    _tid = tid;
  }

  public ResponseBuyChips(HashMap<String, String> str) {
    super(str);
    if (getResult() == 1) {
      _uw = Double.parseDouble( (String) _hash.get("UW"));
      _ub = Double.parseDouble( (String) _hash.get("UB"));
      _urw = Double.parseDouble( (String) _hash.get("URW"));
      _urb = Double.parseDouble( (String) _hash.get("URB"));
      _tid =  (String) (_hash.get("TID"));
    }
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
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&UW=").append(Utils.getRoundedString(_uw));
    str.append("&UB=").append(Utils.getRoundedString(_ub));
    str.append("&URW=").append(Utils.getRoundedString(_urw));
    str.append("&URB=").append(Utils.getRoundedString(_urb));
    str.append("&TID=").append(_tid);

    return str.toString();
  }

  public boolean equal(ResponseBuyChips r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
