package bap.texas.common.message;

import java.util.HashMap;



public class Response
    extends Event {

  public static final int R_CONNECT = 1;
  public static final int R_LOGIN = 2;
  public static final int R_HTBT = 3;
  public static final int R_TABLELIST = 4;
  public static final int R_PLAYER_REMOVED = 5;
  public static final int R_TABLEDETAIL = 6;
  public static final int R_MOVE = 7;
  public static final int R_U3 = 8;
  public static final int R_REGISTER = 9;
  public static final int R_PING = 10;

  // TURN_DEAF = 11
  public static final int R_ADMIN = 12;
  public static final int R_CONFIG = 14;
  public static final int R_LOGOUT = 15;
  public static final int R_MESSAGE = 16;

  // tourny
  public static final int R_TOURNYLIST = 17;
  public static final int R_TOURNYDETAIL = 18;
  public static final int R_TOURNYREGISTER = 19;
  public static final int R_BUYCHIPS = 20;
  public static final int R_TOURNYMYTABLE = 21;

  //
  public static final int R_TOURNYSTARTS = 22;
  public static final int R_U1 = 23;
  public static final int R_U4 = 24;
  public static final int R_U5 = 25;

  public static final int R_SIT_OUT = 26;
  public static final int R_SIT_IN = 27;
  public static final int R_PREFERENCES = 28;
  public static final int R_GET_CHIPS_INTO_GAME = 29;
  public static final int R_WAITER = 30;
  public static final int R_CARD = 32;
  public static final int R_BANNER = 33;
  public static final int R_VOTE = 34;
  public static final int R_TABLE_CLOSED = 35;
  public static final int R_TABLE_OPEN = 36;
  public static final int R_PLAYER_SEARCH = 37;
  public static final int R_WAIT_FOR_BLINDS = 38;
  public static final int R_TOURNYUNREGISTER = 39;
  public static final int R_RESET_ALL_IN = 40;
  public static final int R_BUY_TICKET = 41;
  public static final int R_BINGOROOMDETAIL = 42;
  public static final int R_TICKET=43;
  public static final int R_BINGOROOMLIST=44;
  
  
  
  public static final int R_KILL_HANDLER = 97;
  public static final int R_BASIC = 98;
  public static final int R_UNKNOWN = 99;

  int _result;

  /**
   * name of the response
   */
  public int _response_name = R_BASIC;

  public static final int E_FAILURE = 0;
  public static final int E_SUCCESS = 1;
  public static final int E_AUTHENTICATE = 2;
  public static final int E_SHUTTING = 3;
  public static final int E_USER_EXISTS = 4;
  public static final int E_SUSPEND = 5;
  public static final int E_STARTING = 7;
  public static final int E_ALREADY_REGISTERED = 8;
  public static final int E_REGISTERATION_CLOSED = 9;
  public static final int E_STOPPING_GAME = 10;
  public static final int E_SUSPENDING_GAME = 11;
  public static final int E_ALREADY_LOGGED = 12;
  public static final int E_IP_REUSE = 14;
  public static final int E_BROKE = 15;
  public static final int E_NONEXIST = 16;
  public static final int E_JOINED = 17;
  public static final int E_PARTIALLY_FILLED = 18;
  public static final int E_CARD_FAILED = 19;
  public static final int E_PLAYER_REMOVED = 21;
  public static final int E_DISCONNECTED = 22;
  public static final int E_LOGGED_IN_AT_DIFF_LOCATION = 23;
  public static final int E_WIN_VIOLATED = 24;
  public static final int E_LOSS_VIOLATED = 25;
  public static final int E_OVER_SPENDING = 26;
  public static final int E_BUY_IN_NOT_ALLOWED = 27;
  public static final int E_VOTES_EXHAUSTED = 28;
  public static final int E_REGISTER = 29;
  public static final int E_AFF_PLAYER_REGISTER_FAILED = 30;
  public static final int E_PROVIDER_AUTHETICATION = 31;
  public static final int E_PROVIDER_BALANCE = 32;
  public static final int E_DB_FAILURE = 33;
  public static final int E_IP_RESTRICTED = 34;
  public static final int E_AFFILIATE_MISMATCH = 35;
  public static final int E_BANNED = 36;
  public static final int E_GAME_NOT_ALLOWED = 37;
  public static final int E_BUYIN_NOT_ALLOWED_BETWEEN_GAMES = 38;
  public static final int E_UNABLE_TO_JOIN = 39;
  public static final int E_REMOVED_FROM_WAITING=40;
  public static final int E_NOT_REGISTERED=41;
  public static final int E_NOT_SITTING_ON_THIS_TABLE=42;
  public static final int E_RESET_ALL_IN_FAILED=43;

  public static final int E_BONUS_CODE_NOT_VALID_NOW=44;
  public static final int E_BONUS_CODE_EXPIRED=45;
  public static final int E_BONUS_CODE_DOES_NOT_EXISTS=46;

  public Response(String str) {
    super(str);
    _session = (String) _hash.get("CSID");
    _response_name = Integer.parseInt( (String) _hash.get("RNAME"));
    _result = Integer.parseInt( (String) _hash.get("CR"));
  }

  public Response(HashMap<String, String> str) {
    super(str);
    _session = (String) _hash.get("CSID");
    _response_name = Integer.parseInt( (String) _hash.get("RNAME"));
    _result = Integer.parseInt( (String) _hash.get("CR"));
  }

  public Response(int result, int rname) {
    _result = result;
    _response_name = rname;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("CSID=").append(_session).append("&RNAME=")
        .append(_response_name).append("&CR=").append(_result);
    return str.toString();
  }

  public String session() {
    return _session;
  }

  public void session(String session) {
    _hash.put("CSID", session);
    _session = session;
  }

  public int getResult() {
    return _result;
  }

  public int responseName() {
    return _response_name;
  }

  public boolean equal(Response r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
