package bap.texas.common.message;

import java.util.*;

public class ResponseString
    extends Response {
  String _val;

  public ResponseString(int result, int rname, String val) {
    super(result, rname);
    _val = val;
  }

  public ResponseString(String com) {
    super(com);
    _val = (String) _hash.get("IV");

  }

  public ResponseString(HashMap<String, String> com) {
    super(com);
    _val = (String) _hash.get("IV");
  }

  public String getStringVal() {
    return _val;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&IV=").append(_val);
    return str.toString();
  }

}
