package com.golconda.message;

import java.util.HashMap;


public class Command
    extends Event {
  protected int _cname;

  public static final int C_CONNECT = 1;
  public static final int C_LOGIN = 2;
  public static final int C_HTBT = 3;
  public static final int C_TABLELIST = 4;
  public static final int C_TABLEPING = 5;
  public static final int C_TABLEDETAIL = 6;
  public static final int C_MOVE = 7;
  public static final int C_U3 = 8;
  public static final int C_REGISTER = 9;
  public static final int C_PING = 10;
  public static final int C_TURN_DEAF = 11;
  public static final int C_ADMIN = 12;
  public static final int C_CONFIG = 14;
  public static final int C_LOGOUT = 15;
  public static final int C_MESSAGE = 16;

  // tourny
  public static final int C_TOURNYLIST = 17;
  public static final int C_TOURNYDETAIL = 18;
  public static final int C_TOURNYREGISTER = 19;
  public static final int C_BUYCHIPS = 20;
  public static final int C_TOURNYMYTABLE = 21;

  // bingo room
  public static final int C_TOURNYSTARTS = 22;
  public static final int C_MUCK_CARDS = 23;
  public static final int C_DONT_MUCK = 24;
  public static final int C_U5 = 25;

  public static final int C_SIT_OUT = 26;
  public static final int C_SIT_IN = 27;
  public static final int C_PREFERENCES = 28;
  public static final int C_GET_CHIPS_INTO_GAME = 29;
  public static final int C_WAITER = 30;
  public static final int C_U7 = 32;
  public static final int C_BANNER = 33;
  public static final int C_VOTE = 34;
  public static final int C_TABLE_CLOSED = 35;
  public static final int C_TABLE_OPEN = 36;
  public static final int C_PLAYER_SEARCH = 37;
  public static final int C_WAIT_FOR_BLINDS = 38;
  public static final int C_TOURNYUNREGISTER = 39;
  public static final int C_RESET_ALL_IN = 40;
  public static final int C_JOIN_POOL = 41;
  public static final int C_QUICK_FOLD = 42;
  public static final int C_TICKET=43;
  public static final int C_BINGOROOMLIST=44;
  
  public static final int C_KILL_HANDLER = 97;

  // MOVES
  public static final int M_OPEN = 0;
  public static final int M_CHECK = 1;
  public static final int M_CALL = 2;
  public static final int M_RAISE = 3;
  public static final int M_FOLD = 4;
  public static final int M_PICK = 5;
  public static final int M_DUMP = 6;
  public static final int M_U = 7;

  // AUXILIARY MOVES
  public static final int M_JOIN = 8;
  public static final int M_LEAVE = 9;
  public static final int M_SIT_IN = 10;
  public static final int M_OPT_OUT = 11;
  public static final int M_WAIT = 12;
  public static final int M_BIGBLIND = 14;
  public static final int M_SMALLBLIND = 15;
  public static final int M_BET = 16;
  public static final int M_ANTE = 17;
  public static final int M_ALL_IN = 18;
  public static final int M_BRING_IN = 19;
  public static final int M_SBBB = 20;
  public static final int M_BET_POT = 21;
  public static final int M_CANCEL_JOIN = 22;
  public static final int M_COMPLETE = 23;
  public static final int M_MUCK = 24;
  public static final int M_SHOW = 25;
  public static final int M_BRING_IN_HIGH = 29;
  public static final int M_HIT = 30;
  public static final int M_STAND = 31;
  public static final int M_SURRENDER = 32;
  public static final int M_INSURANCE = 33;
  public static final int M_DOUBLE_DOWN = 34;
  
  //CASINO MOVES
  public static final int M_CASINO_PLAY = 50;

  

  
  
  
  public static final int M_PING = 101;


  public static final int M_NONE = 999;
  public static final int M_ILLEGAL = 999;

  public static final int A_ILLEGAL = 999;

  public Command(String session, int cname) {
    _session = session;
    _cname = cname;
    _hash.put("CSID", session);
    _hash.put("CN", String.valueOf(cname));
  }

  public Command(String str) {
    super(str);
    _session = (String) _hash.get("CSID");
    _cname = Integer.parseInt( (String) _hash.get("CN"));
  }

  public Command(HashMap hash) {
    super(hash);
    _session = (String) _hash.get("CSID");
    _cname = Integer.parseInt( (String) _hash.get("CN"));
  }

  public int getCommandName() {
    return _cname;
  }

  public String session() {
    return _session;
  }

  public void session(String s) {
    _hash.put("CSID", s);
    _session = s;
  }

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("CSID=").append(_session).append("&CN=").append(_cname);
    return str.toString();
  }

}
