package bap.texas.common.message;

import java.util.*;

import bap.texas.util.*;

public class ResponseTableList
    extends Response {
  private int _gameCnt = 0;
  private int _playerCnt = 0;
  private double _pot = 0;
  private double _jamt = 0;
  private String _jackpot;
  private String[] _games;
  private int _cnt;
  private String _mcjch;
  private String _mcjw;

  public ResponseTableList(int result, String[] games) {
    super(result, R_TABLELIST);
    _cnt = games.length;
    _games = games;
  }

  public ResponseTableList(String str) {
    super(str);
    _gameCnt = Integer.parseInt( (String) _hash.get("GC"));
    _playerCnt = Integer.parseInt( (String) _hash.get("PC"));
    _pot = Double.parseDouble( (String) _hash.get("POT"));
    _jamt = Double.parseDouble( (String) _hash.get("JAMT"));
    _jackpot = (String) _hash.get("MCJPOT");
    _mcjch = (String) _hash.get("MCJCH");
    _mcjw = (String) _hash.get("MCJW");
    _cnt = Integer.parseInt( (String) _hash.get("GMCNT"));
    _games = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _games[i] = (String) _hash.get("G" + i);
    }
  }

  public ResponseTableList(HashMap<String, String> str) {
    super(str);
    _gameCnt = Integer.parseInt( (String) _hash.get("GC"));
    _playerCnt = Integer.parseInt( (String) _hash.get("PC"));
    _pot = Double.parseDouble( (String) _hash.get("POT"));
    _jamt = Double.parseDouble( (String) _hash.get("JAMT"));
    _jackpot =  (String) _hash.get("MCJPOT");
    _mcjch = (String) _hash.get("MCJCH");
    _mcjw = (String) _hash.get("MCJW");
    _cnt = Integer.parseInt( (String) _hash.get("GMCNT"));
    _games = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _games[i] = (String) _hash.get("G" + i);
    }
  }

  public void setDetails(int gc, int pc, double pot, double jamt, String jpot, String mch,
                         String mcw) {
    _gameCnt = gc;
    _playerCnt = pc;
    _pot = pot;
    _jamt = jamt;
    _jackpot = jpot;
    _mcjch = mch;
    _mcjw = mcw;
  }

  public int getGameCount() {
    return _cnt;
  }

  public String getGame(int i) {
    return _games[i];
  }

  public GameEvent getGameEvent(int i) {
    GameEvent ge = new GameEvent();
    ge.init(_games[i]);
    return ge;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&GC=").append(_gameCnt);
    str.append("&PC=").append(_playerCnt);
    str.append("&POT=").append(Utils.getRoundedString(_pot));
    str.append("&JAMT=").append(Utils.getRoundedString(_jamt));
    str.append("&MCJPOT=").append(_jackpot);
    str.append("&MCJCH=").append(_mcjch);
    str.append("&MCJW=").append(_mcjw);
    str.append("&GMCNT=").append(_cnt);
    for (int i = 0; i < _cnt; i++) {
      str.append("&G").append(i).append("=").append(_games[i]);
    }
    return str.toString();
  }

  public boolean equal(ResponseTableList r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
