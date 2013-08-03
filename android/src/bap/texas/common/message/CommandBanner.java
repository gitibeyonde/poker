package bap.texas.common.message;


import java.util.HashMap;


public class CommandBanner
    extends Command {
  String _location;
  int _bid;

  public CommandBanner(String session, String loc, int bid) {
    super(session, Command.C_BANNER);
    _location = loc;
    _bid = bid;
  }

  public CommandBanner(String str) {
    super(str);
    _location = (String) _hash.get("LOC");
    _bid = Integer.parseInt( (String) _hash.get("BID"));
  }

  public CommandBanner(HashMap<String, String> str) {
    super(str);
    _location = (String) _hash.get("LOC");
    _bid = Integer.parseInt( (String) _hash.get("BID"));
  }

  public String getLocation() {
    return _location;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&LOC=").append(_location);
    str.append("&BID=").append(_bid);
    return str.toString();
  }

}
