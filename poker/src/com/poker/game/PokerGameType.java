package com.poker.game;

import com.golconda.game.GameType;


public class PokerGameType extends GameType {

  //Poker moves1
  public static final int Play_Holdem = 1;
  public static final int Play_OmahaHi = 2;
  public static final int Play_OmahaHiLo = 4;
  public static final int Play_Stud = 8;

  //2
  public static final int Play_StudHiLo = 16;
  public static final int Play_TermHoldem = 32;
  public static final int Play_Badugi = 64;
  public static final int PU = 128;

  //3
  public static final int Real_Holdem = 256;
  public static final int Real_OmahaHi = 512;
  public static final int Real_OmahaHiLo = 1024;
  public static final int Real_Stud = 2048;

  //4
  public static final int Real_StudHiLo = 4096;
  public static final int Real_TermHoldem = 8192;
  public static final int Real_Badugi = 16384;
  public static final int RU = 32768;

  //5
  public static final int HoldemSitnGo = 65536;
  public static final int OmahaHiSitnGo = 131072;
  public static final int Real_HoldemSitnGo = 262144;
  public static final int Real_OmahaHiSitnGo = 524288;

  //6
  public static final int HoldemTourny = 1048576;
  public static final int OmahaHiTourny = 2097152;
  public static final int Real_HoldemTourny = 4194304;
  public static final int Real_OmahaHiTourny = 8388608;

  //7
  public static final int BOT_TABLE = 16777216;
  public static final int RANDOM_BOT_TABLE = 33554432;
  public static final int U3 = 67108864;
  public static final int U4 = 134217728;


  public static final int REAL_POKER = 0xCCFF00;
  public static final int PLAY_POKER = 0x3300FF;
  public static final int POKER = 0xFFFFFF;


  public static final int REGULAR_POKER_GAME = 0xFFFF;


  private static String[] strings = new String[] {
      "Play-Holdem", "Play-OmahaHi", "Play-OmahaHiLo", "Play-StudHi",
      "Play-StudHiLo","Play-TermHoldem", "Play_Badugi", "PU",
      "Real-Holdem", "Real-OmahaHi", "Real-OmahaHiLo", "Real-StudHi",
      "Real-StudHiLo", "Real-TermHoldem", "Real_Badugi", "RU",
      "HoldemSitnGo", "OmahaSitnGo", "Real-HoldemSitnGo", "Real-OmahaSitnGo",
      "HoldemTourny", "OmahaTourny", "Real-HoldemTourny", "Real-OmahaTourny",
      "bot-table", "u2", "u3", "u4",
      "u4", "u5", "u6"
  };

  static int[] values = new int[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824
  };

  public static boolean isReal(int gameType) {
  	return ((gameType & REAL_POKER) == gameType)?true:false;
  }

  public static boolean isPlay(int gameType) {
  	return ((gameType & PLAY_POKER) == gameType)?true:false;
  }


  public PokerGameType(int intVal) {
    super(intVal);
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      if (intVal == values[i]) {
        return strings[i];
      }
      else if ( (intVal & values[i]) == values[i]) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      throw new IllegalArgumentException("Invalid GameType : " + intVal);
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static void main(String[] argv) {
  }

  public int intVal() {
    return intVal;
  }

  public static final int HOLDEM = Play_Holdem | Real_Holdem | HoldemTourny | HoldemSitnGo | Real_HoldemTourny | Real_HoldemSitnGo | Play_TermHoldem | Real_TermHoldem;

  public boolean isHoldem() {
    return (intVal & HOLDEM) > 0;
  }
  
  public static final int OMAHA = Play_OmahaHi | Play_OmahaHiLo | Real_OmahaHi | Real_OmahaHiLo | OmahaHiSitnGo | Real_OmahaHiSitnGo | OmahaHiTourny | Real_OmahaHiTourny;
  public boolean isOmaha() {
    return (intVal & OMAHA) > 0;
  }

  public static final int STUD = Play_Stud | Play_StudHiLo | Real_Stud | Real_StudHiLo;

  public boolean isStud() {
    return (intVal & STUD) > 0;
  }

  public static final int OMAHAHI = Play_OmahaHi  | Real_OmahaHi | OmahaHiSitnGo | Real_OmahaHiSitnGo | OmahaHiTourny | Real_OmahaHiTourny;
  public boolean isOmahaHi() {
    return (intVal & OMAHAHI) > 0;
  }

  public static final int STUDHI = Play_Stud |  Real_Stud ;
  public boolean isStudHi() {
    return (intVal & STUDHI) > 0;
  }

  public static final int OMAHAHILO = Play_OmahaHiLo | Real_OmahaHiLo;
  public boolean isOmahaHiLo() {
    return (intVal & OMAHAHILO) > 0;
  }

  public static final int STUDHILO = Play_StudHiLo  | Real_StudHiLo;;
  public boolean isStudHiLo() {
    return (intVal & STUDHILO) > 0;
  }
  public static final int TPOKER = Play_TermHoldem  | Real_TermHoldem;;
  public boolean isTPoker(){
    return (intVal & TPOKER)>0;
  }

  public boolean isRegularGame(){
    return (intVal & REGULAR_POKER_GAME)>0;
  }

  public boolean isMTTTourny() {
    return (intVal & 0xF00000) > 0;
  }

  public boolean isBotGame() {
    return (intVal & BOT_TABLE) > 0;
  }

  public boolean isRandomBotGame() {
    return (intVal & RANDOM_BOT_TABLE) > 0;
  }

  public boolean isSitnGo(){
    return (intVal & 0xF0000) > 0;
  }

  public boolean isTourny(){
    return (intVal & 0xFF0000)>0;
  }

  public boolean isPlay() {
    return (intVal & PLAY_POKER) > 0;
  }

  public boolean isReal() {
         return (intVal & REAL_POKER) > 0 ;
   }

  public boolean equals(GameType g){
    return g.intVal == intVal;
  }

}
