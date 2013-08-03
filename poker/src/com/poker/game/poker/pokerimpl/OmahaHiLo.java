package com.poker.game.poker.pokerimpl;

import com.agneya.util.Utils;

import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;

import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Pot;
import com.poker.game.util.CardUtils;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;
import com.poker.game.util.HandOps;

import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;


public class OmahaHiLo
    extends OmahaHi {
  // set the category for logging
  static Logger _cat = Logger.getLogger(OmahaHiLo.class.getName());

  public OmahaHiLo(String name, int minPlayers, int maxPlayers,
                   int rake, double[] maxRake, String[] affiliate,
                   String[] partner, Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
          stateObserver);
    _type = new PokerGameType(PokerGameType.Play_OmahaHiLo);
  }

  public OmahaHiLo(String name, int minPlayers, int maxPlayers,
                   int rake, double maxRake, String[] affiliate,
                   String[] partner, Observer stateObserver) {
    super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
          stateObserver);
    _type = new PokerGameType(PokerGameType.Play_OmahaHiLo);
  }

  public void attachWinners(Pot p) {
    PokerPresence[] v = p.contenders();
    if (v.length == 0) {
      return;
    }

    if (v.length == 1) {
      p.addHighWinners(v[0], p.getVal(), 0L);
      v[0].setShowdown();
      _cat.finest("Single contenders " + v[0]);
      return;
    }

    Vector v_high = new Vector();
    v_high.add(v[0]);
    PokerPresence high = v[0];
    // HIGH HANDS
    for (int i = 0; i < v.length - 1; i++) {
      long result[] = HandComparator.compareGameHand(high.getHand().getCards(),
          v[i + 1].getHand().getCards(),
          communityCards(), PokerGameType.OMAHAHILO, true);

      if (result[0] == 0) {
        v_high.add(v[i + 1]);
      }
      else if (result[0] == -1) {
        v_high.clear();
        v_high.add(v[i + 1]);
        high = v[i + 1];
      }
    }

    // LOW HAND
    // check for low hands
    Vector low_hands = new Vector();
    for (int i = 0; i < v.length; i++) {
      long cmp[] = HandComparator.valueOf(v[i].getHand().getCards(),
                                          communityCards(),
                                          PokerGameType.OMAHAHILO);
      if (HandOps.islowHand( (int) cmp[1])) {
        low_hands.add(v[i]);
        _cat.finest("Low hands = " + v[i]);
      }
    }

    Vector v_low = new Vector();
    if (low_hands.size() >= 2) {
      PokerPresence lv[] = (PokerPresence[]) low_hands.toArray(new PokerPresence[low_hands.
          size()]);

      v_low.add(lv[0]);
      PokerPresence low = lv[0];

      for (int i = 0; i < lv.length - 1; i++) {
        long[] result = HandComparator.compareGameHand(low.getHand().getCards(),
            lv[i + 1].getHand().getCards(),
            communityCards(),
            PokerGameType.OMAHAHILO, false);
        if (result[1] == 0) {
          v_low.add(lv[i + 1]);
        }
        else if (result[1] == -1) {
          v_low.clear();
          v_low.add(lv[i + 1]);
          low = lv[i + 1];
        }
      }
    }
    else if (low_hands.size() == 1) {
      PokerPresence lv[] = (PokerPresence[]) low_hands.toArray(new PokerPresence[low_hands.
          size()]);

      v_low.add(lv[0]);
    }
    else {
      // no low winner
    }

    _cat.info("Pot= " + p);
    if (v_low.size() == 0) {
      // NO LOW HAND
      double hwin[] = Utils.integralDivide(p.getVal(), v_high.size());
      for (int i = 0; i < v_high.size(); i++) {
        PokerPresence pw = (PokerPresence) v_high.get(i);
        long combi = HandComparator.bestHandOf5(pw.
                                                getHand().getCards(),
                                                Hand.getHandFromCardArray(_communityCards.getCards()),
                                                type().intVal())[
            0];

        p.addHighWinners(pw, hwin[i], combi);
        pw.setShowdown();

        Card[] crds = CardUtils.toCardsArray(combi, 0xFFFFFFFFFL);
        Cards best_combination = new Cards(false);
        best_combination.addCards(crds);

        _cat.finest("Winner High= " + pw.name() + ", " + hwin[i] + ", " +
                   best_combination.openStringValue());

      }
    }
    else { // BOTH HI-LO WINNERS
      double split_pot[] = Utils.integralDivide(p.getVal(), 2);
      double hwin[] = Utils.integralDivide(split_pot[0], v_high.size());
      for (int i = 0; i < v_high.size(); i++) {
        PokerPresence pw = (PokerPresence) v_high.get(i);
        long combi = HandComparator.bestHandOf5(pw.
                                                getHand().getCards(),
                                                Hand.getHandFromCardArray(_communityCards.getCards()),
                                                type().intVal())[
            0];
        p.addHighWinners(pw, hwin[i], combi);
        pw.setShowdown();

        Card[] crds = CardUtils.toCardsArray(combi, 0xFFFFFFFFFL);
        Cards best_combination = new Cards(false);
        best_combination.addCards(crds);

        _cat.finest("Winner High= " + pw.name() + ", " + hwin[i] + ", " +
                   best_combination.openStringValue());
      }

      double lwin[] = Utils.integralDivide(split_pot[1], v_low.size());
      for (int i = 0; i < v_low.size(); i++) {
        PokerPresence pw = (PokerPresence) v_low.get(i);
        long combi = HandComparator.bestHandOf5(pw.
                                                getHand().getCards(),
                                                Hand.getHandFromCardArray(_communityCards.getCards()),
                                                type().intVal())[
            1];

        p.addLowWinners(pw, lwin[i], combi);

        Card[] crds = CardUtils.toCardsArray(combi, 0xFFFFFFFFFL);
        Cards best_combination = new Cards(false);
        best_combination.addCards(crds);

        _cat.finest("Winner Low= " + pw.name() + ", " + lwin[i] + ", " +
                   best_combination.openStringValue());
      }
    }

  }

}
