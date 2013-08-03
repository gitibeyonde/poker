package com.poker.common.message;

import com.agneya.util.Utils;
import com.golconda.message.Command;

import java.util.HashMap;


public class CommandJoinPool
    extends Command {
  String _strVal;
  double _amt;

  public CommandJoinPool(String session, int cname, String strVal, double amt) {
    super(session, cname);
    _strVal = strVal;
    _amt = amt;
  }

  public CommandJoinPool(String com) {
    super(com);
    _strVal = (String) _hash.get("POOL");
    _amt = Double.parseDouble((String) _hash.get("AMT"));
  }

  public CommandJoinPool(HashMap com) {
    super(com);
    _strVal = (String) _hash.get("POOL");
    _amt = Double.parseDouble((String) _hash.get("AMT"));
  }

  public String getPoolName() {
    return _strVal;
  }
  
  public double getAmount(){
	  return _amt;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&POOL=").append(_strVal);
    str.append("&AMT=").append(Utils.getRounded(_amt));
    return str.toString();
  }

}
