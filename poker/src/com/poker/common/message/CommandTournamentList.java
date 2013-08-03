package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandTournamentList
    extends Command {
  int _type;
  String _affiliate;
  String _player;
  int _level;

  public CommandTournamentList(String session, int type, String aff, String plyrs,
                          int level) {
    super(session, Command.C_TOURNYLIST);
    _type = type;
    _affiliate = aff;
    _player = plyrs;
    _level = level;
  }

  public CommandTournamentList(String session, int type, String aff, String plyrs) {
    super(session, Command.C_TOURNYLIST);
    _type = type;
    _affiliate = aff;
    _player = plyrs;
    _level = -1;
  }

  public CommandTournamentList(String session, int type) {
    super(session, Command.C_TOURNYLIST);
    _type = type;
    _affiliate = "admin";
    _player = null;
    _level = -1;
  }

  public CommandTournamentList(String str) {
    super(str);
    String mask = (String) _hash.get("MASK");
    if (mask == null) {
      _type = 0XFFFF;
    }
    else {
      _type = Integer.parseInt(mask);
    }
    _affiliate = (String) _hash.get("AFFILIATE");
    _player = (String) _hash.get("PLAYER");
    _level = Integer.parseInt( (String) (_hash.get("LEVEL") == null ? "-1" :
                                         _hash.get("LEVEL")));
  }

  public CommandTournamentList(HashMap str) {
    super(str);
    String mask = (String) _hash.get("MASK");
    if (mask == null) {
      _type = 0XFFFF;
    }
    else {
      _type = Integer.parseInt(mask);
    }
    _affiliate = (String) _hash.get("AFFILIATE");
    _player = (String) _hash.get("PLAYER");
    _level = Integer.parseInt( (String) (_hash.get("LEVEL") == null ? "-1" :
                                         _hash.get("LEVEL")));
  }

  public int getType() {
    return _type;
  }

  public String getAffiliate() {
    return _affiliate;
  }

  public String getPlayer() {
    return _player;
  }

  public int getLevel() {
    return _level;
  }

  public String toString() {
    return super.toString() + "&MASK=" + _type + "&AFFILIATE=" + _affiliate +
        "&PLAYER=" + _player + "&LEVEL=" + _level;
  }

}
