package com.agneya.util;

/**
 * Created by IntelliJ IDEA. User: aprateek Date: Apr 23, 2004 Time: 2:03:55 PM To
 * change this template use File | Settings | File Templates.
 */
public class LongOps {

  private LongOps() {

  }

  // ofcourse, this table could have been generated dynamically ...
  static byte[] highMap = {
      0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};

  /*
    Counts the 1s(=highs) that occur in a long
    Four bit recusion unfolded.
   */
  public static byte getHighs(long l) {
    byte count = 0;
    while (l > 0) {
      byte i = (byte) (l & 0xF);
      count += highMap[i];
      l >>>= 4;
    }
    return count;
  }

  public static byte getHighs(byte b) {
    byte count = 0;
    while (b > 0) {
      byte i = (byte) (b & 0xF);
      count += highMap[i];
      b >>>= 4;
    }
    return count;
  }

  public static byte getHighs(short s) {
    byte count = 0;
    while (s > 0) {
      byte i = (byte) (s & 0xF);
      count += highMap[i];
      s >>>= 4;
    }
    return count;
  }

  // position starts from 0
  public static int firstBitPos(long l) {
    if (l <= 0) {
      return -1;
    }
    long mask = 0x1L; //compile time
    int pos;
    for (pos = 0; (l & (mask << pos)) == 0; pos++) {
      //do nothing
    }
    //System.out.println("END - Hand::lowCardPos(long)");
    return pos;
  }

  public static int firstBitPos(int l) {
   if (l <= 0) {
     return 0;
   }
   int mask = 0x1; //compile time
   int pos;
   for (pos = 0; (l & (mask << pos)) == 0; pos++) {
     //do nothing
   }
   //System.out.println("END - Hand::lowCardPos(long)");
   return pos;
 }


 public static int highCardPos(long hand) {
   //assert hand != 0:"Suit cannot be zero";
   if (hand <= 0) {
     return 0;
   }
   long mask = 0x1L << 51; //compile time
   //System.out.println("mask = " + mask);
   short pos;
   for (pos = 0; (hand & (mask >>> pos)) == 0; pos++) {
     ;
   }
   return 52 - pos;
 }

 public static int highCardPos(short suit) {
   //assert suit != 0:"Suit cannot be zero";
   if (suit <= 0) {
     return 0;
   }
   short mask = 0x1 << 12; //compile time
   short pos;
   for (pos = 0; (suit & (mask >>> pos)) == 0; pos++) {
     ;
   }
   return 13 - pos;
 }

}
