package bap.texas.common.message;

import java.util.HashMap;

public class CommandTournyDetail
    extends Command {
  String _tid;

  public CommandTournyDetail(String session, String tid) {
    super(session, Command.C_TOURNYDETAIL);
    _tid = tid;
  }

  public CommandTournyDetail(String str) {
    super(str);
    _tid = ( (String) _hash.get("TID"));

  }

  public CommandTournyDetail(HashMap<String, String> str) {
    super(str);
    _tid = ( (String) _hash.get("TID"));

  }

  public String getTournyId() {
    return _tid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid;
  }

}
