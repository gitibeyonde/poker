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
public interface PresenceSelector {

  PokerPresence selectPresence(Constraint c);

  PokerPresence selectPrevPresence(Constraint c);

  PokerPresence[] select(Constraint c);

  /***
   *        startPos is excluded
   ***/
  void startPos(int pos);

}
