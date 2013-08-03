package com.golconda.game.util;

import java.io.Serializable;

import java.util.BitSet;
import java.util.logging.Logger;


public class BingoCard implements Serializable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(BingoCard.class.getName());

  public int _id;
  int _bingo[];
  BitSet _daub;
  short _b,_i, _n,_g,_o;
  public boolean match;

  public BingoCard() {
    _bingo=new int[25];
    _daub = new BitSet(25);
    _daub.set(12);
    _b=_i=_n=_g=_o=0;
    match=false;
  }

  public String toString() {
     return stringValue().toString() ;
   }

   public static BingoCard parseBingoCard(String str){
     BingoCard bc = new BingoCard();
     String[] rows=str.split("`");
     for (int i=0; i<rows.length; i++){
       String[] cells = rows[i].split("\\|");
       for (int j=0;j<cells.length; j++){
         bc._bingo[(i*5)+j]=Integer.parseInt(cells[j]);
       }
     }
     return bc;
   }

   public void daub(int called){
     int i=0;
     for (;i<25;i++){
       if (_bingo[i] == called ){
         _bingo[i] = -1 * _bingo[i];
         _cat.finest("Card=" + this);
         break;
       }
     }
     _daub.set(i);
   }

  public boolean patternMatch(BingoPattern bp) {
    _cat.finest(bp.pattern().toString());
    _cat.finest(_daub.toString());
    for (int i=0;i<25;i++){
      if (bp.pattern().get(i) ){
        if (!_daub.get(i)){
          return false;
        }
      }
    }
    match=true;
    return true;
  }

  public StringBuilder stringValue() {
    StringBuilder sb = new StringBuilder();
    for (int i=0;i<25;i++){
      sb.append(_bingo[i]).append("|");
    }
    return sb.deleteCharAt(sb.length() -1);
   }


  public static final short[] _bingo_map={ 0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384};


  public boolean equals(BingoCard c){
    for (int i=0;i<25;i++){
      if (Math.abs(_bingo[i]) != Math.abs(c._bingo[i]))return false;
    }
    return true;
  }

}
