package com.poker.common.interfaces;

import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface SitnGoInterface {

  public final static int TABLE_OPEN = 1;
  public final static int HAND_RUNNING = 2;
  public final static int TABLE_CLOSED = 4;
  public final static int WAIT_TIME = 10000;
  public final static int START_TIME1 = 15000;
  public final static int START_TIME2 = 60000;
  
  public final static int THREAD_START = 1;
  public final static int THREAD_REFRESH = 2;
  

  public PokerPresence[] winners();

  public PokerGameType type();
  
  public int state();

  public int level();

  public String name();

  public String tournyId();

  public double buyIn();

  public double fees();

  public int limit();

  public double prize(int i, int mp);

  public long time();

  public double chips();

  public PokerPresence[] getPlayerList();
  
  public boolean don();
  
  public int tourbo();
}
