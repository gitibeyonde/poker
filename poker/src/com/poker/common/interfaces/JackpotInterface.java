package com.poker.common.interfaces;

import com.poker.common.db.DBJackpot;


public interface JackpotInterface {


  final static int ROYAL_FLUSH=1;
  final static int STRAIGHT_FLUSH=2;
  final static int QUAD_ACE=4;
  final static int QUAD_KING=8;
  final static int QUAD_QUEEN=16;
  final static int QUAD_JACK=32;
  final static int QUAD_TEN=64;
  final static int QUAD_NINE=128;
  final static int QUAD_EIGHT=256;
  final static int QUAD_SEVEN=512;
  final static int DOUBLE_HIT=1024;
  final static int TRIPLE_HIT=2048;
  final static int BAD_BEAT=4096;
  final static int HIGH_HAND=8192;

  final static String[] _jname = { "Royal Flush", "Straight Flush", "Quad Ace", "Quad King",
      "Quad Queen", "Quad Jack", "Quad Ten", "Quad Nine",
      "Quad Eight", "Quad Seven", "Double Hit", "Triple Hit",
      "BAD BEAT", "HIGH HAND"};

  final static int[] _jval = { 1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384
  };

  public final static int _win_percent[] = {
      30, 16, 6, 6,
      6, 6, 5, 5,
      4, 4, 4, 4,
      4, 4, 0
  };


  public DBJackpot[] current();

  public DBJackpot current(int jval);

}
