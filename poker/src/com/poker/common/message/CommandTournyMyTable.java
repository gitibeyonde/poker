package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandTournyMyTable
    extends Command {
  String _tid;

  public CommandTournyMyTable(String session, String tid) {
    super(session, Command.C_TOURNYMYTABLE);
    _tid = tid;
  }

  public CommandTournyMyTable(String str) {
    super(str);
    _tid =  (String) _hash.get("TID");

  }

  public CommandTournyMyTable(HashMap str) {
    super(str);
    _tid = (String) _hash.get("TID");

  }

  public String getTournyId() {
    return _tid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid;
  }

}
