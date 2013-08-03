package com.poker.common.message;

import com.agneya.util.Base64;

import com.golconda.message.GameEvent;
import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseMessage
    extends Response {
  private String _gm;
  String _tid=null;
  String _type=null;
  private String _fm=null;

  public ResponseMessage(int result, String gm) {
    super(result, R_MESSAGE);
    _fm = gm;
  }

  public ResponseMessage(int result, String gm, String tid) {
    super(result, R_MESSAGE);
    _gm = gm;
    _tid = tid;
    _type = "chat";
  }

  public ResponseMessage(int result, String gm, String tid, String type) {
    super(result, R_MESSAGE);
    _gm = gm;
    _tid = tid;
    _type = type;
  }

  public ResponseMessage(HashMap str) {
    super(str);
    _gm = (String) _hash.get("GM");
  }

  public String getType() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));
    return ge.get("type");
  }

  public String getGameId() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));

    return ge.get("name");
  }

  public String getMessage() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));
    return new String(Base64.decode(ge.get("message")));
  }

  public String toString() {
	  StringBuilder str = new StringBuilder(super.toString());
	  if (_fm == null){
	    str.append("&GM=").append("message=").append(_gm);
	    if (_tid != null) str.append(",name=").append(_tid);
	    if (_type != null) str.append(",type=").append(_type);
	  }
	  else {
		  str.append("&GM=").append(_fm);
		  if (_tid != null) str.append(",name=").append(_tid);
		  if (_type != null) str.append(",type=").append(_type);
	  }
    return str.toString();
  }

  public boolean equal(ResponseMessage r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
