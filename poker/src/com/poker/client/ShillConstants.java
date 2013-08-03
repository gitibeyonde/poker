package com.poker.client;

public class ShillConstants {

  //IQ Levels
  public static final int IQ_BEGINER_0 = 1;
  public static final int IQ_BEGINER_1 = 2;
  public static final int IQ_BEGINER_2 = 3;
  public static final int IQ_AMATURE_0 = 4;
  public static final int IQ_AMATURE_1 = 5;
  public static final int IQ_AMATURE_2 = 6;
  public static final int IQ_PROFESSIONAL_0 = 7;
  public static final int IQ_PROFESSIONAL_1 = 8;
  public static final int IQ_PROFESSIONAL_2 = 9;
  public static final int IQ_PROFESSIONAL_3 = 10;

  //Game Types
  public static final int TEXAS_HOLDEM = 1;
  public static final int OMAHA_HI = 2;
  public static final int OMAHA_HILO = 4;
  public static final int SEVENSTUD_HI = 8;
  public static final int SEVENSTUD_HILO = 16;
  public static final int ALL_GAMES = 31;

  //Manager Commands
  public static final int STOP = 1;
  public static final int STATUS = 2;
  public static final int ADD_PLAYER = 3;
  public static final int REMOVE_PLAYER = 4;
  public static final int MODIFY_PLAYER = 5;
  public static final int STOP_PLAYER = 6;

  public static final int ADD_BOT = 3;
  public static final int REMOVE_BOT = 4;
  public static final int MODIFY_BOT = 5;
  public static final int STOP_BOT = 6;

  //Player States
  public static final int PLR_DISCONNECTED = 1;
  public static final int PLR_CONNECTING = 2;
  public static final int PLR_CONNECTED = 3;
  public static final int PLR_LOGGING = 4;
  public static final int PLR_LOGGED = 5; //PLR_IDLE - earlier
  public static final int PLR_REQUESTING_TL = 6;
  public static final int PLR_NEEDS_REGISTRATION = 7;
  public static final int PLR_REGISTERING = 8;
  public static final int PLR_REQ_OBSERVE = 9;
  public static final int PLR_OBSERVING = 10;
  public static final int PLR_JOINING = 11;
  public static final int PLR_JOINED = 12;
  public static final int PLR_REQ_WAITING = 13;
  public static final int PLR_WAITING = 14;
  public static final int PLR_IDLE = 15;

  //public static final int PLR_ACTIVE = 11;
  public static final int PLR_SITOUT = 16;

  // Player Levels.
  public final static int FISH = 1;
  public final static int CALL = 2;
  public final static int LOOSE = 3;
  public final static int ROCK = 4;
  public final static int WIN = 5;

  public static final String BOT_LEVELS[] = new String[] {
      "Fish Bot", "Calling Bot", "Loose Bot",
      "Rock Bot", "Winning Bot"
  };

  private ShillConstants() {
  }
}
