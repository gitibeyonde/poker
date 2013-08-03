package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandMessage
    extends Command {
  String _message;
  String _tid;

  public CommandMessage(String session, String message, String tid) {
    super(session, Command.C_MESSAGE);
    _message = message;
    _tid = tid;
  }

  public CommandMessage(String com) {
    super(com);
    _tid = (String) _hash.get("TID");
    _message = (String) _hash.get("MSG");

  }

  public CommandMessage(HashMap com) {
    super(com);
    _tid = (String) _hash.get("TID");
    _message = (String) _hash.get("MSG");

  }

  public String message() {
    return _message;
  }

  public String getTableId() {
    return _tid;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&MSG=").append(_message).append("&TID=").append(_tid);
    return str.toString();
  }

}
