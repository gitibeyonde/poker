package com.poker.common.interfaces;

import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;

import java.util.Calendar;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface TournyInterface {

  public static final int NOEXIST = 0;
  public static final int CREATED = 1;
  public static final int DECL = 2;
  public static final int REG = 4;
  public static final int JOIN = 8;
  public static final int START = 16;
  public static final int RUNNING = 32;
  public static final int END = 64;
  public static final int FINISH = 128;
  public static final int CLEAR = 256;

  public static final int FINISH_CYCLES = 10;
  

  static final int TOURBO_NORMAL_TIME = 10 * 60 * 1000;  // 10 minutes
  static final int TOURBO_TOURBO_TIME = 5 * 60 * 1000;  // 10 minutes
  static final int TOURBO_HYPER_TIME = 3 * 60 * 1000;  // 10 minutes
  
  static final int TOURBO_NORMAL = 0;  // 10 minutes
  static final int TOURBO_TOURBO = 1;  // 10 minutes
  static final int TOURBO_HYPER = 2;  // 10 minutes

  public PokerPresence[] winners();

  public int state();

  public String name();
  
  public PokerGameType type();

  public double buyIn();

  public double fees();

  public double chips();
  
  public long time();

  public double prize(int i, int size);

  public boolean tournyOver();
}
