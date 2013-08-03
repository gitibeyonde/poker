package com.poker.game.poker.pokerimpl;

import com.poker.game.PokerGameType;

import java.util.Observer;


public class RealOmahaHi  extends OmahaHi {

  public RealOmahaHi(String name, int minPlayers, int maxPlayers,
                int rake, double[] maxRake, String[] affiliate, String[] partner, Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner, stateObserver);
    _type = new PokerGameType(PokerGameType.Real_OmahaHi);
  }

  public RealOmahaHi(String name, int minPlayers, int maxPlayers,
                int rake, double maxRake, String[] affiliate, String[] partner, Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner, stateObserver);
    _type = new PokerGameType(PokerGameType.Real_OmahaHi);
  }

}
