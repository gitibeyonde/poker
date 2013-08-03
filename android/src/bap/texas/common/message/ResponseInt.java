package bap.texas.common.message;

import java.util.HashMap;


public class ResponseInt
    extends Response {
  int _intVal;

  public ResponseInt(int result, int rname, int intVal) {
    super(result, rname);
    _intVal = intVal;
  }

  public ResponseInt(String com) {
    super(com);
    _intVal = Integer.parseInt( (String) _hash.get("IV"));

  }

  public ResponseInt(HashMap<String, String> com) {
    super(com);
    _intVal = Integer.parseInt( (String) _hash.get("IV"));
  }

  public int getIntVal() {
    return _intVal;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&IV=").append(_intVal);
    return str.toString();
  }

}
