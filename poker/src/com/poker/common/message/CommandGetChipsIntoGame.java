package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandGetChipsIntoGame
    extends Command {
  private double _chips;
  private String _tid;

  public CommandGetChipsIntoGame(String session, String tid, double chips) {
    super(session, C_GET_CHIPS_INTO_GAME);
    _chips = chips;
    _tid = tid;
  }

  public CommandGetChipsIntoGame(HashMap str) {
    super(str);
    _tid =  (String) _hash.get("TID");
    _chips = Double.parseDouble( (String) _hash.get("CHPS"));
  }

  public double getChips() {
    return _chips;
  }

  public String getTableId() {
    return _tid;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&CHPS=").append(_chips).append("&TID=").append(_tid);
    return str.toString();
  }

  public boolean equal(CommandGetChipsIntoGame r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
