package bap.texas.common.message;

import java.util.HashMap;


public class ResponseConfig
    extends Response {
  private String _cf;

  public ResponseConfig(int result, String config) {
    super(result, R_CONFIG);
    _cf = config;
  }

  public ResponseConfig(HashMap<String, String> str) {
    super(str);
    _cf = (String) _hash.get("CNF");
  }

  public String getConfig() {
    return _cf;
  }

  public void setConfig(String ge) {
    _cf = ge;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&CNF=").append(_cf);
    return str.toString();
  }

  public boolean equal(ResponseConfig r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
