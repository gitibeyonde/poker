package com.poker.common.message;

import com.golconda.message.Command;

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

  public CommandInt(HashMap com) {
    super(com);
    _intVal = Integer.parseInt( (String) _hash.get("IV"));
  }

  public int getIntVal() {
    return _intVal;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&IV=").append(_intVal);
    return str.toString();
  }

}
