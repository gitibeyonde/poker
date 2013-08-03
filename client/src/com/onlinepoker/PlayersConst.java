package com.onlinepoker;

public interface PlayersConst {
  /** Visible state */
  public final static int PLAYER_VISIBLE = 1;
  public final static int PLAYER_BLINKING = 2;
  public final static int PLAYER_GAUZY = 4;
  public final static int PLAYER_PLACE_INACCESSIBLE = 8;

  /** State for input dialog type */
  public final static int BUTTON_NONE = 1;
  public final static int BUTTON_FOLD_CHECK_BET = 2;
  public final static int BUTTON_FOLD_CALL_RAISE = 4;
}
