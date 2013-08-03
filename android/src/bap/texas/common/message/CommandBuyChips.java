package bap.texas.common.message;

import java.util.HashMap;


public class CommandBuyChips
    extends Command {
  private double _realChips;
  private double _playChips;
  private int _tid = -1;

  public CommandBuyChips(String session, double playChips, double realChips) {
    super(session, C_BUYCHIPS);
    _realChips = realChips;
    _playChips = playChips;
  }

  public CommandBuyChips(String session, double playChips, double realChips,
                         int tid) {
    super(session, C_BUYCHIPS);
    _realChips = realChips;
    _playChips = playChips;
    _tid = tid;
  }

  public CommandBuyChips(HashMap<String, String> str) {
    super(str);
    _realChips = Double.parseDouble( (String) _hash.get("RLCHPS"));
    _playChips = Double.parseDouble( (String) _hash.get("PLCHPS"));
    _tid = Integer.parseInt( (String) (_hash.get("TID") == null ? "-1" :
                                       _hash.get("TID")));
  }

  public double getRealChips() {
    return _realChips;
  }

  public double getPlayChips() {
    return _playChips;
  }

  public int getGid() {
    return _tid;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&PLCHPS=").append(_playChips).append("&RLCHPS=").append(
        _realChips).append("&TID=").append(_tid);
    return str.toString();
  }

  public boolean equal(CommandBuyChips r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
