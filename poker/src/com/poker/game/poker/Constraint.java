package com.poker.game.poker;

import com.poker.game.PokerPresence;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface Constraint {
   boolean satisfy(PokerPresence p);
}
