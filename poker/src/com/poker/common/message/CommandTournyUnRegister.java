package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandTournyUnRegister
    extends Command {
  String _tid, _uid;

  public CommandTournyUnRegister(String session, String tid, String uid) {
    super(session, Command.C_TOURNYUNREGISTER);
    _tid = tid;
    _uid = uid;
  }

  public CommandTournyUnRegister(String str) {
    super(str);
    _tid =  ( (String) _hash.get("TID"));
    _uid =  ( (String) _hash.get("UID"));

  }

  public CommandTournyUnRegister(HashMap str) {
    super(str);
    _tid =  ( (String) _hash.get("TID"));
    _uid =  ( (String) _hash.get("UID"));


  }

  public String getTournyName() {
    return _tid;
  }

  public String getUserName() {
    return _uid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid + "&UID=" + _uid;
  }

}
