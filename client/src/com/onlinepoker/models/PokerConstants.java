package com.onlinepoker.models;

public interface PokerConstants {

  // game types
  public static final int POKER_TYPE_OMAHA = 0;
  public static final int POKER_TYPE_HOLDEM = 1;
  public static final int POKER_TYPE_STUD = 2;
  public static final int POKER_TYPE_TOURNAMENT = 64;

  // game limits
  public static final int REGULAR = 0; // high-low
  public static final int NO_LIMIT = 1;
  public static final int POT_LIMIT = 2;
  public static final int TOURNAMENT = 3;

  // player types
  public static final int PLAYER_REGULAR = 0;
  public static final int PLAYER_BUTTON = 1;
  public static final int PLAYER_SMALL_BLIND = 2;
  public static final int PLAYER_BIG_BLIND = 3;
  public static final int PLAYER_EARLY = 4;
  public static final int PLAYER_MIDDLE = 5;
  public static final int PLAYER_LATE = 6;

}
