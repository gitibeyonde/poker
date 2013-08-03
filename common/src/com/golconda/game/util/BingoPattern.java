package com.golconda.game.util;

import java.util.BitSet;


public class BingoPattern {
  BitSet _p;
  String _name;

  public BingoPattern(String name, char p[]){
    _p = new BitSet(25);
    _p.set(12); // middle bit is always set
    for (int i=0;i<5; i++){
      if (p[i] == 'X')_p.set(i);
      if (p[5+i] == 'X')_p.set(5+i);
      if (p[10+i] == 'X')_p.set(10+i);
      if (p[15+i] == 'X')_p.set(15+i);
      if (p[20+i] == 'X')_p.set(20+i);
    }
    _name = name;
  }

  public String toString() {
    String str = "\n" + _name + "\n";
    for (int i = 0; i < 25; i+=5) {
      str += (_p.get(i) ? " X " : " _ ") + (_p.get(1+i) ? " X " : " _ ") +
          (_p.get(2+i) ? " X " : " _ ") + (_p.get(3+i) ? " X " : " _ ") +
          (_p.get(4+i) ? " X " : " _ ") + "\n";
    }
    return str;
  }

  public BitSet pattern(){
    return _p;
  }

  public String stringValue() {
    StringBuilder str = new StringBuilder(_name);
   str.append('`');
    for (int i = 0; i < 25; i+=5) {
      str.append(_p.get(i) ? "X|" : "_|").append(_p.get(1+i) ? "X|" : "_|").
          append(_p.get(2+i) ? "X|" : "_|").append(_p.get(3+i) ? "X|" : "_|").
          append(_p.get(4+i) ? "X|" : "_|");
    }
    return str.deleteCharAt(str.length()-1).toString();
  }

}
