package com.golconda.game;

public class GameStateEvent {

  private GameStateEvent(int val) {
    intVal = val;
  }

  public static final GameStateEvent GAME_OVER = new GameStateEvent(0);

    public static final GameStateEvent GAME_SETUP = new GameStateEvent(1);

  public static final GameStateEvent GAME_BEGIN = new GameStateEvent(2);


  public static final GameStateEvent PRE_FLOP = new GameStateEvent(3);

  public static final GameStateEvent FLOP = new GameStateEvent(4);

  public static final GameStateEvent TURN = new GameStateEvent(5);

  public static final GameStateEvent RIVER = new GameStateEvent(6);

    public static final GameStateEvent GAME_POSTRUN = new GameStateEvent(7);

  public static final GameStateEvent SITNGO_OVER = new GameStateEvent(10);

  public static final GameStateEvent MTT_OVER = new GameStateEvent(11);

  public static final GameStateEvent SITNGO_START = new GameStateEvent(12);


    public static final GameStateEvent UNUSED = new GameStateEvent(50);

  public static final GameStateEvent UNKNOWN = new GameStateEvent(99);

  public int intValue() {
    return intVal;
  }

  public int hashCode() {
    return intVal;
  }

  public boolean equals(Object o) {
    return ( (o.getClass() == this.getClass()) &&
            ( (GameStateEvent) o).intVal == this.intVal);
  }

  public static String stringVal(GameStateEvent event) {
    switch (event.intVal) {
      case 0:
        return "GAME_OVER";
      case 1:
        return "GAME_SETUP";
      case 2:
        return "GAME_BEGIN";
      case 3:
        return "PRE_FLOP";
      case 4:
        return "FLOP";
      case 5:
        return "TURN";
      case 6:
        return "RIVER";

      case 10:
        return "SITNGO_OVER";
      case 11:
        return "MTT_OVER";
      case 12:
        return "SITNGO_START";

      case 50:
        return "UNUSED";

      default:
        return "UNKNOWN";
    }
  }

  int intVal;
}
