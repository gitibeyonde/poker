package com.poker.game.poker.pokerimpl;

import com.poker.game.PokerGameType;

import java.util.Observer;


public class RealHoldem   extends Holdem {

  public RealHoldem(String name, int minPlayers, int maxPlayers,
                int rake, double[] maxRake, String[] affiliate, String[] partner, Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner, stateObserver);
    _type = new PokerGameType(PokerGameType.Real_Holdem);
  }
  public RealHoldem(String name, int minPlayers, int maxPlayers,
                int rake, double maxRake, String[] affiliate, String[] partner, Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner, stateObserver);
    _type = new PokerGameType(PokerGameType.Real_Holdem);
  }

}
