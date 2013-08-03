package bap.texas.common.message;

import java.util.HashMap;


public class CommandInt
    extends Command {
  int _intVal;

  public CommandInt(String session, int cname, int intVal) {
    super(session, cname);
    _intVal = intVal;
  }

  public CommandInt(String com) {
    super(com);
    _intVal = Integer.parseInt( (String) _hash.get("IV"));

  }

  public CommandInt(HashMap<String, String> com) {
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
