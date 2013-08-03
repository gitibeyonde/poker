package bap.texas.common.message;

import java.util.HashMap;


public class CommandTournyRegister
    extends Command {
  String _tid, _uid;

  public CommandTournyRegister(String session, String tid, String uid) {
    super(session, Command.C_TOURNYREGISTER);
    _tid = tid;
    _uid = uid;
  }

  public CommandTournyRegister(String str) {
    super(str);
    _tid = (String) _hash.get("TID");
    _uid = (String) _hash.get("UID");
  }

  public CommandTournyRegister(HashMap<String, String> str) {
    super(str);
    _tid =  (String) _hash.get("TID");
    _uid = (String) _hash.get("UID");
  }

  public String getTournyName() {
    return _tid;
  }

  public String getUserName() {
    return _uid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid  + "&UID=" + _uid;
  }

}
