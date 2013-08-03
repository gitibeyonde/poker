package com.poker.game.util;

import com.golconda.game.util.Card;

import java.util.Arrays;


public class HandOddCalculator {

public static final int ONE_PAIR = 1;
public static final int TWO_PAIR = 2;
public static final int THREE_OF_A_KIND = 3;
public static final int FULLHOUSE = 4;
public static final int FOUR_OF_A_KIND = 5;
public static final int FLUSH = 6;
public static final int STRAIGHT = 7;
public static final int STRAIGHT_FLUSH = 8;
public static final int ROYAL_FLUSH = 9;

int noOfCards;

//count of various cards, posCounter[0] = no of Ace, posCounter[12]= no. of K.
int posCounter[];

private int paircount;
private int tripletcount;
private int quadruplecount;

private int suitedpair = 0;
private int suitedtriplet = 0;
private int suitedquadruplet = 0;
private int suitedquintuplet = 0;

private double prob;
private double wprob; // weighted prob , only for one pair, two pair, threeOfaKind full house.

// weights for each card, depends on card strength.
// weight for A is 13/13 while for deuce is 1/13 and K is 12/13.
private final double weights[] = new double[] {
    1.0, 0.077, 0.154, 0.231, 0.308, 0.385, 0.462, 0.538,
    0.615, 0.692, 0.769, 0.846, 0.923};

//private Log log = LogFactory.getLog(HandOddCalculator.class);

public HandOddCalculator() {

}

/**
 * Given a set of cards gets the no. of pairs, triplets or
quadruplets .
 * Also calculates the max. no. of cards that are of the same color.
 * @param card long
 */
private void initializeCounts(long card) {

  noOfCards = 0;
  paircount = 0;
  tripletcount = 0;
  quadruplecount = 0;

  suitedpair = 0;
  suitedtriplet = 0;
  suitedquadruplet = 0;
  suitedquintuplet = 0;

  prob = 0;
  posCounter = new int[13];
  int[] suitCount = new int[4];

  short[] suitcards = HandOps.getSuits(card);

  for (int i = 0; i < suitcards.length; i++) {
    long k = 1L;
    if (suitcards[i] != 0) {
      for (int x = 0; x < 13; x++, k = k << 1) {
        if ( (k & suitcards[i]) > 0) {
          if (x == 12) {
            x = 0;
          }
          posCounter[x + 1]++;
          suitCount[i]++;
          noOfCards++;
        }
      }
    }
  }

  for (int i = 0; i < posCounter.length; i++) {

    if (posCounter[i] == 2) {
      paircount++;
    }
    else if (posCounter[i] == 3) {
      tripletcount++;
    }
    else if (posCounter[i] == 4) {
      quadruplecount++;
    }
  }

  for (int i = 0; i < suitCount.length; i++) {
    if (suitCount[i] == 2) {
      suitedpair++;
    }
    else if (suitCount[i] == 3) {
      suitedtriplet++;
    }
    else if (suitCount[i] == 4) {
      suitedquadruplet++;
    }
    else if (suitCount[i] == 5) {
      suitedquintuplet++;
    }
  }
}

private double probForStraight(long cards) {

  int[] dis = new int[10];
  int prev = 0;

  for (int i = 0, j = 0; i < posCounter.length; i++) {
    if (posCounter[i] != 0) {
      if (prev != 0) {
        dis[j] = i - prev;
        j++;
      }
      prev = i;
    }
  }

  if (check4pattern(dis, new int[] {1, 1, 1, 1})) {
    return 1.0;
  }

  else {
    // pre flop.
    if ( (noOfCards == 2) && (paircount == 0)) {
      int dist = dis[0];

      if (dist <= 4) {
        // three cards apart.
        if (dist == 4) {
          prob = 0.003265; // p1 = 4.4.4/50C3
          return prob;
        }
        // two cards apart.
        else if (dist == 3) {
          if ( (posCounter[0] == 1) || (posCounter[12] == 1)) {
            prob = 0.003265;
            return prob;
          }
          else {
            prob = 0.00653; // p2 = 2*p1
            return prob;
          }
        }
        // one card apart.
        else if (dist == 2) {
          if ( (posCounter[0] == 1 || posCounter[12] == 1)) {
            prob = 0.003265;
            return prob;
          }
          else if (posCounter[1] == 1 || posCounter[11] == 1) {
            prob = 0.00653;
            return prob;
          }
          else {
            prob = 0.0098; // p3= 3*p1;
            return prob;
          }
        }
        // consecutive cards
        else if (dist == 1) {
          if (posCounter[0] == 1 || posCounter[12] == 1) {
            prob = 0.003265;
            return prob;
          }
          else if ( (posCounter[0] == 0 && posCounter[1] == 1) ||
                   (posCounter[12] == 0 && posCounter[11] == 1)) {
            prob = 0.00653;
            return prob;
          }
          else if ( (posCounter[1] == 0 && posCounter[2] == 1) ||
                   (posCounter[11] == 0 && posCounter[10] == 1)) {
            prob = 0.0098;
            return prob;
          }
          else {
            prob = 0.0131;
            return prob;
          }
        }
      }
      else {
        return 0;
      }
    }
    // 5 cards all distinct.
    else if (noOfCards == 5  && tripletcount == 0 &&
             quadruplecount == 0) {

      if (check4pattern(dis, new int[] {1, 1, 1})) {

        if ( (posCounter[0] == 1 && posCounter[1] == 1) ||
            (posCounter[11] == 1 && posCounter[12] == 1)) {
          prob = 0.1647; // p = 1-(43C2/47C2)
          return prob;
        }
        else {
          // prob = 1-(39C2/47C2)
          prob = 0.3145;
        }
      }
      else if (check4pattern(dis, new int[] {2, 1, 1}) ||
               check4pattern(dis, new int[] {1, 2, 1}) ||
               check4pattern(dis, new int[] {1, 1, 2})) {
        prob = 0.1647; //p= 1-43C2/47C2
      }
      else if (check4pattern(dis, new int[] {1, 1})) {
        prob = 0.026; // 8C2/47C2
      }
    }
    else if (noOfCards == 6 && paircount == 0 && tripletcount == 0 &&
             quadruplecount == 0) {
      if (check4pattern(dis, new int[] {1, 1, 1})) {

        if ( (posCounter[0] == 1 && posCounter[1] == 1) ||
            (posCounter[11] == 1 && posCounter[12] == 1)) {
          prob = .0869; // p=4/46
          return prob;
        }
        else {
          prob = 0.174; // p = 8/46;
          return prob;
        }
      }
      else if (check4pattern(dis, new int[] {2, 1, 1}) ||
               check4pattern(dis, new int[] {1, 2, 1}) ||
               check4pattern(dis, new int[] {1, 1, 2})) {
        prob = .0869; // p=4/46;
        return prob;
      }
    }

  }
  return prob;
}

private double probForFlush(long cards) {
  if (suitedquintuplet == 1) {
    prob = 1;
  }
  else if (noOfCards == 2 && suitedpair == 1) {
    // Probability of making flush on flop. p = (11C3)/(50C3)
    prob = 0.00842;
  }
  //after flop
  else if (noOfCards == 5 && suitedquadruplet == 1) {
    // Probability of making a flush on turn. p = 9/47
    prob = 0.1915;
  }
  else if (noOfCards == 5 && suitedtriplet == 1) {
    // Probability of hitting a flush by river. p = 10C2/47C2
    prob = 0.041628;
  }
  // after turn.
  else if (noOfCards == 6 && suitedquadruplet == 1) {
    // Prob. to hit a flush in river. p = 9/46
    prob = 0.1956;
  }
  else {
    return 0;
  }

  return prob;
}

/**
 * Calculates the probability of getting One Pair at any stage of the
game.
 * i.e. preflop, afterflop, turn, river.
 * @return double
 */
private double probForOnlyOnePair(long cards) {

  if (paircount == 1) {
    prob = 1;
  }

  else if (paircount == 0 && tripletcount == 0 && quadruplecount ==
0) {
    if (noOfCards == 2) {
      //  prob = (6C1)*(44C2)/(50C3);
      prob = 0.2896;
    }
    // after flop. getting a pair in turn.
    else if (noOfCards == 5) {
      prob = 0.3191; // prob = 5 * 3 / (52 - 5);
    }
    //turn
    else if (noOfCards == 6) {
      prob = .3913; //prob = 6 * 3 / (52 - 6);
    }
    // river
    else {
      prob = 0;
      // No Chance now.
    }
  }
  return prob;
}

private double probForFullHouse(long cards) {
  if ( (paircount == 1 && tripletcount == 1) || (tripletcount == 2))
{
    return 1;
  }
  else {
    //preflop
    if (noOfCards == 2 && paircount == 1) {
      //  prob = { (2C1)*12*(4C2) + 12*(4C3) }/(50C3).
      // where nCr is the no. of ways of selecting r items out of n distinct items.
      prob = 0.009796;
    }
    // after flop.
    else if (noOfCards == 5 && paircount == 2 && tripletcount == 0) {
      prob = 0.0851; //prob = 4 / 47;
    }
    else if (noOfCards == 5 && paircount == 0 && tripletcount == 1) {
      prob = 0.1276; //prob = 6 / 47;
    }
    //after turn
    else if (noOfCards == 6 && paircount == 3) {
      prob = 0.1304; //prob = 6 / 46;
    }
    else if (noOfCards == 6 && paircount == 2) {
      prob = 0.0869; //prob = 4 / 46;
    }
    else if (noOfCards == 6 && tripletcount == 1 && paircount == 0) {
      prob = 0.1956; //prob = 9 / 46;
    }
    // river
    else {
      prob = 0;
      // No Chance now.
    }
  }
  return prob;
}

private double probForTwoPairs(long cards) {

  if (paircount == 2) {
    return 1;
  }

  else {
    //preflop
    if (noOfCards == 2 && paircount == 1) {
      // prob = { 12*(4C2)*44 } / (50C3)
      prob = 0.1616;
    }
    // after flop.
    else if (noOfCards == 5 && paircount == 1) {
      prob = 0.2553; // prob = 12 / 47;
    }
    //after turn
    else if (noOfCards == 6 && paircount == 1) {
      prob = 0.3478; // prob = 16 / 46;
    }
    // river
    else {
      prob = 0;
      // No Chance now.
    }
  }
  return prob;
}

private double probForThreeOfaKind(long cards) {
  if ( tripletcount >= 1) {
    return 1;
  }

  else {
    //preflop
    if (noOfCards == 2 && paircount == 1) {
      //  prob = { (2C1)*12*(4C2) + 12*(4C3) }/(50C3).
      // where nCr is the no. of ways of selecting r items out of n distinct items.
      prob = 0.009796;
    }
    // after flop.
    else if(noOfCards == 5 && paircount == 1 ) {
      prob = 0.04255; // prob = 2/47
    }
    else if (noOfCards == 5 && paircount == 2 ) {
      prob = 0.0851; //prob = 4 / 47;
    }
    //after turn
    else if (noOfCards == 6 && paircount == 3) {
      prob = 0.1304; //prob = 6 / 46;
    }
    else if (noOfCards == 6 && paircount == 2) {
      prob = 0.08695; // prob = 4 / 46;
    }
    // river
    else {
      prob = 0;
      // No Chance now.
    }
  }
  return prob;
}

private double probForFourOfAKind(long cards) {
  if (quadruplecount == 1) {
    return 1;
  }

  else {
    //preflop
    if (noOfCards == 2 && paircount == 1) {
      //  prob = 1*48/(50C3)
      // where nCr is the no. of ways of selecting r items out of n distinct items.
      prob = 0.002449;
    }
    else if (noOfCards == 2 && paircount == 0) {
      // prob = 2/50C3
      prob = 0.000102;
    }
    // after flop.
    else if (noOfCards == 5 && tripletcount == 1) {
      // prob = 1/47
      prob = 0.0213; ;
    }
    //after turn
    else if (noOfCards == 6 && tripletcount == 1) {
      prob = 0.0217; //prob = 1 / 46;
    }
    else if (noOfCards == 6 && tripletcount == 2) {
      prob = 0.0435; //prob = 2 / 46;
    }
    // river
    else {
      prob = 0;
      // No Chance now.
    }
  }
  return prob;
}

/**
 * Takes an array of integers and an array of pattern
 * Checks for existence of the pattern in the array.
 * @param a int[]
 * @return boolean
 */
private boolean check4pattern(int[] a, int[] pattern) {

  if (a.length < pattern.length) {
    return false;
  }
  else {
    for (int i = 0; i < a.length - pattern.length + 1; i++) {
      int[] sub = new int[pattern.length];
      for (int j = 0; j < sub.length; j++) {
        sub[j] = a[i + j];
      }
      if (Arrays.equals(sub, pattern)) {
        return true;
      }
    }
  }
  return false;
}

public double getHandOdds(long cards, int type) {

  double prob = 0;
  initializeCounts(cards);

  switch (type) {
    case HandOddCalculator.ONE_PAIR:
      prob = probForOnlyOnePair(cards);
      break;
    case HandOddCalculator.TWO_PAIR:
      prob = probForTwoPairs(cards);
      break;
    case HandOddCalculator.THREE_OF_A_KIND:
      prob = probForThreeOfaKind(cards);
      break;
    case HandOddCalculator.FULLHOUSE:
      prob = probForFullHouse(cards);
      break;
    case HandOddCalculator.FOUR_OF_A_KIND:
      prob = probForFourOfAKind(cards);
      break;
    case HandOddCalculator.FLUSH:
      prob = probForFlush(cards);
      break;
    case HandOddCalculator.STRAIGHT:
      prob = probForStraight(cards);
      break;
  }
  return prob;
}

public double[] getAllHandOdds(long cards) {

  double[] prob = new double[10];
  initializeCounts(cards);

  return prob;
}

public static void main(String[] args) {
  long cards;
  HandOddCalculator hoc = new HandOddCalculator();
  cards = (long) (Math.pow(2, Card._2D) + Math.pow(2, Card._3D) +
                  Math.pow(2, Card._4S) + Math.pow(2, Card._4D) +
                  Math.pow(2, Card._JD));
  //cards = (long) (Math.pow(2, Card._2D) + Math.pow(2, Card._3S));
  hoc.initializeCounts(cards);

  System.out.println("paircount = " + hoc.paircount);
  System.out.println("tripletcount = " + hoc.tripletcount);
  System.out.println("quadruplecount = " + hoc.quadruplecount);
  System.out.println("suitedpair = " + hoc.suitedpair);
  System.out.println("suitedquadruplet = " + hoc.suitedquadruplet);

  for (int i = 0; i < hoc.posCounter.length; i++) {
    System.out.println("pos[" + i + "] = " + hoc.posCounter[i]);
  }
  System.out.println("No Of Cards = " + hoc.noOfCards);

  // One Pair
  //cards = (long) (Math.pow(2, Card._2D) + Math.pow(2, Card._3D));

  long st = System.currentTimeMillis();
  System.out.println("Prob of straight flush  = " +  hoc.getHandOdds(cards, HandOddCalculator.STRAIGHT_FLUSH));
  System.out.println("Prob of 4ofakind  = " +  hoc.getHandOdds(cards, HandOddCalculator.FOUR_OF_A_KIND));
  System.out.println("Prob of full house  = " +  hoc.getHandOdds(cards, HandOddCalculator.FULLHOUSE));
  System.out.println("Prob of flush  = " +  hoc.getHandOdds(cards, HandOddCalculator.FLUSH));
  System.out.println("Prob of straight  = " +  hoc.getHandOdds(cards, HandOddCalculator.STRAIGHT));
  System.out.println("Prob of 3ofakind  = " +  hoc.getHandOdds(cards, HandOddCalculator.THREE_OF_A_KIND));
  System.out.println("Prob of two pair  = " +  hoc.getHandOdds(cards, HandOddCalculator.TWO_PAIR));
  System.out.println("Prob of one pair  = " +  hoc.getHandOdds(cards, HandOddCalculator.ONE_PAIR));
  long et = System.currentTimeMillis();
  System.out.println("time = " + (et -st));

}
}
