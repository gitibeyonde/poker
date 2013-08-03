package com.poker.game.util;

import com.agneya.util.LongOps;

import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.golconda.game.util.HandEvaluator;


/**
 * Created by IntelliJ IDEA.
 * User: yuriy
 * Date: Nov 4, 2003
 * Time: 11:52:08 AM
 * To change this template use Options | File Templates.
 */
public class CardUtils {

    public static int getPairRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._2C);
        return HandEvaluator.rankHand(cards);
    }

    public static int getAcePairRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._AC);
        cards.addCard(Card._AC);
        return HandEvaluator.rankHand(cards);
    }

    public static int getKingPairRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._KC);
        cards.addCard(Card._KC);
        return HandEvaluator.rankHand(cards);
    }

    public static int getTwoPairsRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._2C);
        cards.addCard(Card._3C);
        cards.addCard(Card._3C);
        return HandEvaluator.rankHand(cards);
    }

    public static int getThreeOfKindRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._2C);
        cards.addCard(Card._2C);
        return HandEvaluator.rankHand(cards);
    }

    public static int getStraightRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._3D);
        cards.addCard(Card._4C);
        cards.addCard(Card._5C);
        cards.addCard(Card._6C);
        return HandEvaluator.rankHand(cards);
    }

    public static int getFlushRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._4C);
        cards.addCard(Card._5C);
        cards.addCard(Card._6C);
        cards.addCard(Card._7C);
        return HandEvaluator.rankHand(cards);
    }

    public static int getFullHouseRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._2D);
        cards.addCard(Card._2H);
        cards.addCard(Card._3C);
        cards.addCard(Card._3D);
        return HandEvaluator.rankHand(cards);
    }

    public static int getFourOfKindRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._2D);
        cards.addCard(Card._2H);
        cards.addCard(Card._2S);
        return HandEvaluator.rankHand(cards);
    }

    public static int getStraightFlushRank() {
        Cards cards = new Cards(false);
        cards.addCard(Card._2C);
        cards.addCard(Card._3C);
        cards.addCard(Card._4C);
        cards.addCard(Card._5C);
        cards.addCard(Card._6C);
        return HandEvaluator.rankHand(cards);
    }

    public static boolean isStraightPossible(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.STRAIGHT) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++)
            for (int j = 0; j < Card.NUM_CARDS; j++) {
                if (boardAndHand.has(i) || boardAndHand.has(j)) continue;
                c.addCard(i);
                c.addCard(j);
                if (HandEvaluator.nameHandValue(c) == HandEvaluator.STRAIGHT) return true;
                c.removeCard();
                c.removeCard();
            }
        return false;
    }

    public static boolean isFlushPossible(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.FLUSH) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++)
            for (int j = 0; j < Card.NUM_CARDS; j++) {
                if (boardAndHand.has(i) || boardAndHand.has(j)) continue;
                c.addCard(i);
                c.addCard(j);
                if (HandEvaluator.nameHandValue(c) == HandEvaluator.FLUSH) return true;
                c.removeCard();
                c.removeCard();
            }
        return false;
    }

    public static boolean isFullHousePossible(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.FULLHOUSE) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++)
            for (int j = 0; j < Card.NUM_CARDS; j++) {
                if (boardAndHand.has(i) || boardAndHand.has(j)) continue;
                c.addCard(i);
                c.addCard(j);
                if (HandEvaluator.nameHandValue(c) == HandEvaluator.FULLHOUSE) return true;
                c.removeCard();
                c.removeCard();
            }
        return false;
    }

    public static boolean isSuited(Cards c) {
        return c.size() > 1 ? c.getCard(1).getSuit() == c.getCard(2).getSuit() : false;
    }

    public static int highCard(Cards c) {
        return c.size() > 1 ? Math.max(c.getCard(1).getRank(), c.getCard(2).getRank()) : 0;
    }

    public static int getOvercardCount(Cards c, Cards board) {
        if (c!=null && c.size() > 1) {
            int count = 0;
            for (int i = 1; i < 3; i++) {
                int max = c.getCard(i).getRank();
                for (int j = 0; j < board.size(); j++) {
                    Card card = board.getCard(j + 1);
                    if (max < card.getRank()) break;
                    if (j == board.size() - 1) count++;
                }
            }
            return count;
        } else
            return 0;
    }

    public static boolean isSequential(Cards c) {
        if (c.size() > 1) {
            int diff = Math.abs(c.getCard(1).getRank() - c.getCard(2).getRank());
            return diff <= 1 || diff == Card.ACE;
        } else
            return false;
    }

    public static boolean isSuitedConnector(Cards c) {
        return isSuited(c) && isSequential(c);
    }

    /*
    Nuts: the best possible hand.  For example AK and the flop is JTQ.
    Or having AA and the flop, turn and river are: AAKKK.
    In some cases the nuts can change as the hand progresses.
    An example of this would be holding AA and the flop is AQ2.
    Right now the best possible hand is what you have, AA.
    If the turn card is a T, then you no longer have the nuts.  The nuts are now KJ.
    */
    public static boolean isNuts(Cards board, Cards boardAndHand) {
        Cards c = board;
        int max = 0;
        for (int i = 0; i < Card.NUM_CARDS; i++)
            for (int j = 0; j < Card.NUM_CARDS; j++) {
                if (boardAndHand.has(i) || boardAndHand.has(j)) continue;
                c.addCard(i);
                c.addCard(j);
                max = Math.max(max, HandEvaluator.rankHand(c));
                c.removeCard();
                c.removeCard();
            }
        return HandEvaluator.rankHand(boardAndHand) >= max;
    }

    public static boolean isPocketPair(Cards c) {
        return HandEvaluator.nameHandValue(c) == HandEvaluator.PAIR;
    }

    public static boolean isTopPair(Cards boardAndHand) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(c) != HandEvaluator.PAIR) return false;
        int idx = 0;
        int max = 0;
        for (int i = 0; i < c.size(); i++) {
            int r = c.getCard(i + 1).getRank();
            if (r > max) {
                max = r;
                idx = i;
            }
        }
        Cards c2 = new Cards(false);
        for (int i = 0; i < c.size(); i++) {
            if (i != idx) {
                c2.addCard(c.getCard(i + 1));
            }
        }
        return HandEvaluator.nameHandValue(c2) != HandEvaluator.PAIR;
    }

    /*
    Flush Draw: four of one suit. Country I hold AsKs and the flop is 4s5sQh.
    I have two cards to draw a spade to complete the flush draw.
    */
    public static boolean isFlushDraw(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.FLUSH) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++) {
            if (boardAndHand.has(i)) continue;
            c.addCard(i);
            if (HandEvaluator.nameHandValue(c) == HandEvaluator.FLUSH) return true;
            c.removeCard();
        }
        return false;
    }

    /*
    Backdoor Flush Draw: a flush draw in which both cards have to come to complete
    the hand � both the turn card and river card have to be of the same suit. Country
    I have AsKs and the flop is 4s9hTh.  I have to hit both spades in a row.
    */
    public static boolean isBackdoorFlushDraw(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.FLUSH) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++)
            for (int j = 0; j < Card.NUM_CARDS; j++) {
                if (boardAndHand.has(i) || boardAndHand.has(j)) continue;
                c.addCard(i);
                c.addCard(j);
                if (HandEvaluator.nameHandValue(c) == HandEvaluator.FLUSH) return true;
                c.removeCard();
                c.removeCard();
            }
        return false;
    }

    /*
    Open Ended Straight Draw: This is when four cards in succession are on the flop.
    For example I hold 9T and the flop is 78Q.  Notice that a 6 or J would give me the
    straight.  That means I have 8 outs total (four 6s and four Js).
    */
    /*
    Gutshot Straight Draw: This is a straight draw when only one card can complete the
    hand instead of two like the open ended. For example if I hold AT and the flop is QJ4.
    Now only the K will give me a straight and I just have 4 �outs�.
    */
    public static boolean isOpenEndedStraightDraw(Cards boardAndHand, Cards board) {
        return isOneCardStraightDraw(boardAndHand, board);
    }

    public static boolean isGutshotStraightDraw(Cards boardAndHand, Cards board) {
        return isOneCardStraightDraw(boardAndHand, board);
    }

    public static boolean isOneCardStraightDraw(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.STRAIGHT) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++) {
            if (boardAndHand.has(i)) continue;
            c.addCard(i);
            if (HandEvaluator.nameHandValue(c) == HandEvaluator.STRAIGHT) return true;
            c.removeCard();
        }
        return false;
    }

    /*
    Double Belly Buster Straight Draw: This is a straight draw that has two cards to complete
    so it has the same outs as an Open Ended Straight.  For example if I hold T6 and the
    flop is Q98.  Notice that a 7 or a J would give me the straight (8 outs total).
    */
    public static boolean isDoubleBellyBusterStraightDraw(Cards boardAndHand, Cards board) {
        Cards c = boardAndHand;
        if (HandEvaluator.nameHandValue(board) == HandEvaluator.STRAIGHT) return false;
        for (int i = 0; i < Card.NUM_CARDS; i++)
            for (int j = 0; j < Card.NUM_CARDS; j++) {
                if (boardAndHand.has(i) || boardAndHand.has(j)) continue;
                c.addCard(i);
                c.addCard(j);
                if (HandEvaluator.nameHandValue(c) == HandEvaluator.STRAIGHT) return true;
                c.removeCard();
                c.removeCard();
            }
        return false;
    }

    /*
    Straight Flush Draw: Both a straight and flush draw at the same time. For example JsTs
    and the flop is 8s9s4h.
    */
    public static boolean isStraightFlushDraw(Cards boardAndHand, Cards board) {
        return isFlushDraw(boardAndHand, board) && isBackdoorFlushDraw(boardAndHand, board);
    }

    public static boolean isSet(Cards c, Cards board, Cards boardAndHand) {
        return HandEvaluator.nameHandValue(c) == HandEvaluator.PAIR &&
                HandEvaluator.nameHandValue(board) != HandEvaluator.THREEKIND &&
                HandEvaluator.nameHandValue(boardAndHand) == HandEvaluator.THREEKIND;
    }

    public static boolean isHighCard(Cards c, Cards board) {
        boolean result = true;
        int max = Math.max(c.getCard(1).getRank(), c.getCard(2).getRank());
        for (int i = 0; i < board.size(); i++)
            if (board.getCard(i + 1).getRank() > max) {
                result = false;
                break;
            }
        return result;
    }


    public synchronized static long toLongHand(Card[] crd){
      long hnd=0L;
      for (int i=0;i<crd.length;i++){
        Card c = crd[i];
        hnd |= (long) Math.pow(2, c.getIndex());
      }
      return hnd;
    }
    
    public synchronized static Card[] toCardsArray(long cards, long visibility) {
      if (cards<=0)return null;
      Card[] cs = new Card[LongOps.getHighs(cards)];
      int cc = 0;
      long vis_crds = cards & visibility;
      short crds = (short) (vis_crds & Hand.CLUBS_MASK);
      byte pos = 0;
      short posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 0);
          cs[cc].setIsOpened(true);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      crds = (short) ( (vis_crds & Hand.DIAMONDS_MASK) >>> 13);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 1);
          cs[cc].setIsOpened(true);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      crds = (short) ( (vis_crds & Hand.HEARTS_MASK) >>> 26);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 2);
          cs[cc].setIsOpened(true);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      crds = (short) ( (vis_crds & Hand.SPADES_MASK) >>> 39);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 3);
          cs[cc].setIsOpened(true);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      // closed cards

      long invis_crds = cards & ~ (visibility);
      crds = (short) (invis_crds & Hand.CLUBS_MASK);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 0);
          cs[cc].setIsOpened(false);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      crds = (short) ( (invis_crds & Hand.DIAMONDS_MASK) >>> 13);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 1);
          cs[cc].setIsOpened(false);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      crds = (short) ( (invis_crds & Hand.HEARTS_MASK) >>> 26);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 2);
          cs[cc].setIsOpened(false);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }

      crds = (short) ( (invis_crds & Hand.SPADES_MASK) >>> 39);
      pos = 0;
      posMask = 1; // cards of a type have last 13 bits valid ..
      while (posMask <= crds) {
        if ( (posMask & crds) > 0) {
          cs[cc] = new Card(pos, 3);
          cs[cc].setIsOpened(false);
          cc++;
        }
        pos++;
        posMask <<= 1;
      }
      return cs;
    }

}
