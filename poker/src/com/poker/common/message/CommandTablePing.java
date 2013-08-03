package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandTablePing
    extends Command {
  String _tid;

  public CommandTablePing(String session, String tid) {
    super(session, Command.C_TABLEPING);
    _tid = tid;
  }

  public CommandTablePing(String str) {
    super(str);
    _tid = (String) _hash.get("TID");

  }

  public CommandTablePing(HashMap str) {
    super(str);
    _tid = (String) _hash.get("TID");

  }

  public String getTableId() {
    return _tid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid;
  }

}
