package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


//import com.poker.game.GameSummary;

public class ResponseTablePing
    extends Response {
  private String _gameEvent;
  private String _ip;

  public ResponseTablePing(int result, String ge) {
    super(result, R_TABLEPING);
    _gameEvent = ge;
  }

  public ResponseTablePing(String str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public ResponseTablePing(HashMap str) {
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
    StringBuilder str = new StringBuilder(super.toString());
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
