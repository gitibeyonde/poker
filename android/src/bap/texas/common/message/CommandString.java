package bap.texas.common.message;

import java.util.HashMap;


public class CommandString
    extends Command {
  String _strVal;

  public CommandString(String session, int cname, String strVal) {
    super(session, cname);
    _strVal = strVal;
  }

  public CommandString(String com) {
    super(com);
    _strVal = (String) _hash.get("SV");

  }

  public CommandString(HashMap<String, String> com) {
    super(com);
    _strVal = (String) _hash.get("SV");
  }

  public String getStringVal() {
    return _strVal;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&SV=").append(_strVal);
    return str.toString();
  }

}
