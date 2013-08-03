package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponsePing
    extends Response {
  long _time;
  int _active_players;
  int _active_tables;

  public ResponsePing(int at, int ap) {
    super(1, Response.R_PING);
    _time = System.currentTimeMillis();
    _active_tables = at;
    _active_players = ap;
  }

  public ResponsePing(String com) {
    super(com);
    _time = Long.parseLong( (String) _hash.get("TIME"));
    _active_tables = Integer.parseInt( (String) _hash.get("AT"));
    _active_players = Integer.parseInt( (String) _hash.get("AP"));

  }

  public ResponsePing(HashMap com) {
    super(com);
    _time = Long.parseLong( (String) _hash.get("TIME"));
    _active_tables = Integer.parseInt( (String) _hash.get("AT"));
    _active_players = Integer.parseInt( (String) _hash.get("AP"));
  }

  public long getTime() {
    return _time;
  }

  public int getActivePlayers() {
    return _active_players;
  }

  public int getActiveTables() {
    return _active_tables;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&TIME=").append(_time);
    str.append("&AT=").append(_active_tables);
    str.append("&AP=").append(_active_players);
    return str.toString();
  }

}
