package bap.texas.common.message;

import java.util.*;
//import com.poker.game.GameSummary;

public class ResponseTableDetail
    extends Response {
  private String _gameEvent;
  private String _ip;

  public ResponseTableDetail(int result, String ge) {
    super(result, R_TABLEDETAIL);
    _gameEvent = ge;
  }

  public ResponseTableDetail(String str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public ResponseTableDetail(HashMap<String, String> str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public String getGameEvent() {
    return _gameEvent;
  }

  public void setGameEvent(String ge) {
    _gameEvent = ge;
  }

  public void setIp(String ip) {
    _ip = ip;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&IP=").append(_ip);
    str.append("&GE=").append(_gameEvent);
    return str.toString();
  }

  public boolean equal(ResponseTableDetail r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
