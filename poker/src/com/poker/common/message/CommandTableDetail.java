package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandTableDetail
    extends Command {
  String _tid;

  public CommandTableDetail(String session, String tid) {
    super(session, Command.C_TABLEDETAIL);
    _tid = tid;
  }

  public CommandTableDetail(String str) {
    super(str);
    _tid = (String) _hash.get("TID");

  }

  public CommandTableDetail(HashMap str) {
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
