package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandVote
    extends Command {
  private int _vote_count;
  private int _vote_type;
  private String _for;
  private String _tid;

  public static int POSITIVE = 1;
  public static int NEGATIVE = -1;

  public CommandVote(String session, String tid, String fr, int vt) {
    super(session, C_VOTE);
    _for = fr;
    _tid = tid;
    _vote_type = vt;
  }

  public CommandVote(HashMap str) {
    super(str);
    _for = (String) _hash.get("FOR");
    _tid =  (String) (_hash.get("TID"));
    _vote_type = Integer.parseInt( (String) (_hash.get("VT") == null ? "-1" :
                                             _hash.get("VT")));
  }

  public String getTid() {
    return _tid;
  }

  public String forPlayer() {
    return _for;
  }

  public int getVoteType() {
    return _vote_type;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&FOR=").append(_for).append("&TID=").append(_tid);
    str.append("&VT=").append(_vote_type);
    return str.toString();
  }

  public boolean equal(CommandVote r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
