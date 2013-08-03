package com.poker.game.util;
  
  public class OddsPlayer {
    public long _hand, _dhand;
    public String _name;
    public int _pos;
    public double _winCount;
    public double _percent_win;
    public boolean _me;

    public OddsPlayer(long h, String n, int p, boolean me) {
      _hand = h;
      _pos = p;
      _name = n;
      _winCount = 0;
      _me = me;
    }

    public void incrWinCount(double i) {
      _winCount += i;
    }

    public String toString() {
      return _percent_win + "(" + _pos + ")";
    }
  }