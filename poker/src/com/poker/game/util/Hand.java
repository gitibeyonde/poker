package com.poker.game.util;

import com.agneya.util.LongOps;

import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;

import java.io.Serializable;


public class Hand
    implements Serializable {

  //@debug only
  static String EOL = System.getProperty("line.separator");

  long cards = 0L;
  long visibility = 0L; // a set bit -> card is invisible
  Cards _hand;

  /*
    An empty hand. Cards can now be added. Useful for testing and debug
   */
  public Hand() {
    reset();
  }

  public Hand copy() {
    Hand nh = new Hand(_hand.getCards());
    nh.visibility = visibility;
    nh.cards = cards;
    return nh;
  }

  public void reset() {
    cards = 0L;
    visibility = 0L;
    _hand = new Cards(false);
  }

  public Hand(long initialDraw) {
    reset();
    cards |= initialDraw; // cards = initialDraw, but to care for default bits for agents
    visibility = 0L;
    _hand.addCards(CardUtils.toCardsArray(cards, visibility));
  }

  public Hand(long initialDraw, long vis) {
    reset();
    cards |= initialDraw; // cards = initialDraw, but to care for default bits for agents
    visibility = vis;
    _hand.addCards(CardUtils.toCardsArray(cards, visibility));
  }

  public Hand(Card[] c) {
    reset();
    _hand.addCards(c);
    cards = getHandFromCardArray(c);
  }

  public void addCard(long crd, boolean visible) {
    cards |= crd;
    if (visible) {
      visibility |= crd;
    }
    else {
      ; // by default a card is face down
    }
    _hand.addCards(CardUtils.toCardsArray(crd, visible ? 1L : 0L));
  }

  public void addOpenCard(long crd) {
    cards |= crd;
    visibility |= crd;
    _hand.addCards(CardUtils.toCardsArray(crd, 0xFFFFFFFFFFFFFFFFL));
  }

  public void addCloseCard(long crd) {
    cards |= crd;
    _hand.addCards(CardUtils.toCardsArray(crd, 0L));
  }

  public long getCards() {
    return cards;
  }

  public String getCardsString() {
    return _hand.stringValue();
  }

  public String getAllCardsString() {
    return _hand.openStringValue();
  }

  public long getOpenCards() {
    return cards & visibility;
  }

  public long getCloseCards() {
    return cards & ~visibility;
  }

  public long getVisibility() {
    return visibility;
  }

  public int cardCount() {
    return LongOps.getHighs(cards);
  }

  public Card[] getCardsArray() {
    return CardUtils.toCardsArray(cards, visibility);
  }

  public String stringValue() {
    //int cardCount = getHighs( this.cards );
    long vis_card = cards & visibility;
    StringBuilder buf = new StringBuilder().append("FUP=|").append(clubsString(
        vis_card).length() > 1 ? clubsString(vis_card).toString() + "|" : "").
        append(diamondsString(vis_card).length() > 1 ?
               diamondsString(vis_card).toString() + "|" :
               "").append(heartsString(vis_card).length() > 1 ?
                          heartsString(vis_card).toString() + "|" :
                          "").append(spadesString(vis_card).length() > 1 ?
                                     spadesString(vis_card).toString() + "|" :
                                     "");
    long invis_card = cards & (~visibility);
    buf.append("  FD=|").append(clubsString(invis_card).length() > 1 ?
                                clubsString(invis_card).toString() + "|" : "").
        append(diamondsString(invis_card).length() > 1 ?
               diamondsString(invis_card).toString() + "|" :
               "").append(heartsString(invis_card).length() > 1 ?
                          heartsString(invis_card).toString() + "|" :
                          "").append(spadesString(invis_card).length() > 1 ?
                                     spadesString(invis_card).toString() + "|" :
                                     "");
    return buf.toString();
  }

  public static String stringValue(long cards) {
    //int cardCount = getHighs( this.cards );
    long vis_card = cards;
    StringBuilder buf = new StringBuilder().append("FUP=|").append(clubsString(
        vis_card).length() > 1 ? clubsString(vis_card).toString() + "|" : "").
        append(diamondsString(vis_card).length() > 1 ?
               diamondsString(vis_card).toString() + "|" :
               "").append(heartsString(vis_card).length() > 1 ?
                          heartsString(vis_card).toString() + "|" :
                          "").append(spadesString(vis_card).length() > 1 ?
                                     spadesString(vis_card).toString() + "|" :
                                     "");

    return buf.toString();
  }

  public static StringBuilder clubsString(long hand) {
    short cards = (short) (hand & CLUBS_MASK);
    return new StringBuilder("C-").append(cardNames(cards));
  }

  public static StringBuilder diamondsString(long hand) {
    short cards = (short) ( (hand & DIAMONDS_MASK) >>> 13);
    return new StringBuilder("D-").append(cardNames(cards));

  }

  public static StringBuilder heartsString(long hand) {
    short cards = (short) ( (hand & HEARTS_MASK) >>> 26);
    return new StringBuilder("H-").append(cardNames(cards));

  }

  public static StringBuilder spadesString(long hand) {
    short cards = (short) ( (hand & SPADES_MASK) >>> 39);
    return new StringBuilder("S-").append(cardNames(cards));

  }

  public static StringBuilder cardNames(short cards) {
    StringBuilder buf = new StringBuilder();
    byte pos = 0;
    short posMask = 1; // cards of a type have last 13 bits valid ..
    while (posMask <= cards) {
      if ( (posMask & cards) > 0) {
        buf.append(cardNameMap[pos] + ", ");
      }
      pos++;
      posMask <<= 1;
    }
    return buf;
  }

  /**
   * 2 of club is less than 2 of diamonds which is less than 2 of hearts
   */

  public static int cardStrength(long card) {
    assert LongOps.getHighs(card) == 1:"Card strength expects single card";
    /**
     * Find out the suite of the card
     */
    int a = 0;
    short colourless_card = 0;
    if ( (card & CLUBS_MASK) > 0) {
      a = 1;
      colourless_card = (short) card;
    }
    else if ( ( (card & DIAMONDS_MASK) > 0)) {
      a = 2;
      colourless_card = (short) (card >>> 13);
    }
    else if ( ( (card & HEARTS_MASK) > 0)) {
      a = 3;
      colourless_card = (short) (card >>> 26);
    }
    else if ( ( (card & SPADES_MASK) > 0)) {
      a = 4;
      colourless_card = (short) (card >>> 39);
    }

    assert colourless_card != 0:"Unknown card";

    byte pos = 0;
    short posMask = 1; // cards of a type have last 13 bits valid ..
    while (posMask <= colourless_card) {
      pos++;
      posMask <<= 1;
    }
    return a + (pos) * 4;
  }

  public static long CLUBS_MASK = 0x1FFF;

  public static long DIAMONDS_MASK = CLUBS_MASK << 13;

  public static long HEARTS_MASK = CLUBS_MASK << 26;

  public static long SPADES_MASK = CLUBS_MASK << 39;

  //Array with 13 elements
  // positional map with the cards

  static String[] cardNameMap = {
      "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k", "a"};

  /**
        "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "jack", "queen", "king", "ace"};
   **/


  private static String cardPosToString(int pos) {
    String card = null;
    switch (pos) {
      case 0:
        card = "sa";
        break;
      case 1:
        card = "sk";
        break;
      case 2:
        card = "sq";
        break;
      case 3:
        card = "sj";
        break;
      case 4:
        card = "s10";
        break;
      case 5:
        card = "s9";
        break;
      case 6:
        card = "s8";
        break;
      case 7:
        card = "s7";
        break;
      case 8:
        card = "s6";
        break;
      case 9:
        card = "s5";
        break;
      case 10:
        card = "s4";
        break;
      case 11:
        card = "s3";
        break;
      case 12:
        card = "s2";
        break;
      case 13:
        card = "ha";
        break;
      case 14:
        card = "hk";
        break;
      case 15:
        card = "hq";
        break;
      case 16:
        card = "hj";
        break;
      case 17:
        card = "h10";
        break;
      case 18:
        card = "h9";
        break;
      case 19:
        card = "h8";
        break;
      case 20:
        card = "h7";
        break;
      case 21:
        card = "h6";
        break;
      case 22:
        card = "h5";
        break;
      case 23:
        card = "h4";
        break;
      case 24:
        card = "h3";
        break;
      case 25:
        card = "h2";
        break;
      case 26:
        card = "da";
        break;
      case 27:
        card = "dk";
        break;
      case 28:
        card = "dq";
        break;
      case 29:
        card = "dj";
        break;
      case 30:
        card = "d10";
        break;
      case 31:
        card = "d9";
        break;
      case 32:
        card = "d8";
        break;
      case 33:
        card = "d7";
        break;
      case 34:
        card = "d6";
        break;
      case 35:
        card = "d5";
        break;
      case 36:
        card = "d4";
        break;
      case 37:
        card = "d3";
        break;
      case 38:
        card = "d2";
        break;
      case 39:
        card = "ca";
        break;
      case 40:
        card = "ck";
        break;
      case 41:
        card = "cq";
        break;
      case 42:
        card = "cj";
        break;
      case 43:
        card = "c10";
        break;
      case 44:
        card = "c9";
        break;
      case 45:
        card = "c8";
        break;
      case 46:
        card = "c7";
        break;
      case 47:
        card = "c6";
        break;
      case 48:
        card = "c5";
        break;
      case 49:
        card = "c4";
        break;
      case 50:
        card = "c3";
        break;
      case 51:
        card = "c2";
        break;
      default:
        break;
    }
    return card;
  }

  public static String getStrFromHand(long hand) {
    long mask = 2251799813685248L;
    int pos = 0;
    StringBuilder sb = new StringBuilder();
    boolean comma = false;
    while (pos < 52) {
      if ( (hand & mask) == mask) {
        //System.out.println("pos = " + pos);
        if (comma) {
          sb.append(",");
        }
        sb.append(cardPosToString(pos));
        comma = true;
      }
      mask = mask >>> 1;
      pos++;
    }
    return sb.toString();
  }
    public static long getHandFromCardArray(Card[] crd) {
    
      long hand = 0x0L;
      long mask = 0x1L;
      for (int i = 0; i < crd.length; i++) {
        if (crd[i].toString().equalsIgnoreCase("2C")) {
          hand |= (mask << 0);
        }
        else if (crd[i].toString().equalsIgnoreCase("3C")) {
          hand |= (mask << 1);
        }
        else if (crd[i].toString().equalsIgnoreCase("4C")) {
          hand |= (mask << 2);
        }
        else if (crd[i].toString().equalsIgnoreCase("5C")) {
          hand |= (mask << 3);
        }
        else if (crd[i].toString().equalsIgnoreCase("6C")) {
          hand |= (mask << 4);
        }
        else if (crd[i].toString().equalsIgnoreCase("7C")) {
          hand |= (mask << 5);
        }
        else if (crd[i].toString().equalsIgnoreCase("8C")) {
          hand |= (mask << 6);
        }
        else if (crd[i].toString().equalsIgnoreCase("9C")) {
          hand |= (mask << 7);
        }
        else if (crd[i].toString().equalsIgnoreCase("TC")) {
          hand |= (mask << 8);
        }
        else if (crd[i].toString().equalsIgnoreCase("JC")) {
          hand |= (mask << 9);
        }
        else if (crd[i].toString().equalsIgnoreCase("QC")) {
          hand |= (mask << 10);
        }
        else if (crd[i].toString().equalsIgnoreCase("KC")) {
          hand |= (mask << 11);
        }
        else if (crd[i].toString().equalsIgnoreCase("AC")) {
          hand |= (mask << 12);
        }
        else if (crd[i].toString().equalsIgnoreCase("2D")) {
          hand |= (mask << 13);
        }
        else if (crd[i].toString().equalsIgnoreCase("3D")) {
          hand |= (mask << 14);
        }
        else if (crd[i].toString().equalsIgnoreCase("4D")) {
          hand |= (mask << 15);
        }
        else if (crd[i].toString().equalsIgnoreCase("5D")) {
          hand |= (mask << 16);
        }
        else if (crd[i].toString().equalsIgnoreCase("6D")) {
          hand |= (mask << 17);
        }
        else if (crd[i].toString().equalsIgnoreCase("7D")) {
          hand |= (mask << 18);
        }
        else if (crd[i].toString().equalsIgnoreCase("8D")) {
          hand |= (mask << 19);
        }
        else if (crd[i].toString().equalsIgnoreCase("9D")) {
          hand |= (mask << 20);
        }
        else if (crd[i].toString().equalsIgnoreCase("TD") ) {
          hand |= (mask << 21);
        }
        else if (crd[i].toString().equalsIgnoreCase("JD")) {
          hand |= (mask << 22);
        }
        else if (crd[i].toString().equalsIgnoreCase("QD")) {
          hand |= (mask << 23);
        }
        else if (crd[i].toString().equalsIgnoreCase("KD")) {
          hand |= (mask << 24);
        }
        else if (crd[i].toString().equalsIgnoreCase("AD")) {
          hand |= (mask << 25);
        }
        else if (crd[i].toString().equalsIgnoreCase("2H")) {
          hand |= (mask << 26);
        }
        else if (crd[i].toString().equalsIgnoreCase("3H")) {
          hand |= (mask << 27);
        }
        else if (crd[i].toString().equalsIgnoreCase("4H")) {
          hand |= (mask << 28);
        }
        else if (crd[i].toString().equalsIgnoreCase("5H")) {
          hand |= (mask << 29);
        }
        else if (crd[i].toString().equalsIgnoreCase("6H")) {
          hand |= (mask << 30);
        }
        else if (crd[i].toString().equalsIgnoreCase("7H")) {
          hand |= (mask << 31);
        }
        else if (crd[i].toString().equalsIgnoreCase("8H")) {
          hand |= (mask << 32);
        }
        else if (crd[i].toString().equalsIgnoreCase("9H")) {
          hand |= (mask << 33);
        }
        else if (crd[i].toString().equalsIgnoreCase("TH")) {
          hand |= (mask << 34);
        }
        else if (crd[i].toString().equalsIgnoreCase("JH")) {
          hand |= (mask << 35);
        }
        else if (crd[i].toString().equalsIgnoreCase("QH")) {
          hand |= (mask << 36);
        }
        else if (crd[i].toString().equalsIgnoreCase("KH")) {
          hand |= (mask << 37);
        }
        else if (crd[i].toString().equalsIgnoreCase("AH")) {
          hand |= (mask << 38);
        }
        else if (crd[i].toString().equalsIgnoreCase("2S")) {
          hand |= (mask << 39);
        }
        else if (crd[i].toString().equalsIgnoreCase("3S")) {
          hand |= (mask << 40);
        }
        else if (crd[i].toString().equalsIgnoreCase("4S")) {
          hand |= (mask << 41);
        }
        else if (crd[i].toString().equalsIgnoreCase("5S")) {
          hand |= (mask << 42);
        }
        else if (crd[i].toString().equalsIgnoreCase("6S")) {
          hand |= (mask << 43);
        }
        else if (crd[i].toString().equalsIgnoreCase("7S")) {
          hand |= (mask << 44);
        }
        else if (crd[i].toString().equalsIgnoreCase("8S")) {
          hand |= (mask << 45);
        }
        else if (crd[i].toString().equalsIgnoreCase("9S")) {
          hand |= (mask << 46);
        }
        else if (crd[i].toString().equalsIgnoreCase("TS")) {
          hand |= (mask << 47);
        }
        else if (crd[i].toString().equalsIgnoreCase("JS")) {
          hand |= (mask << 48);
        }
        else if (crd[i].toString().equalsIgnoreCase("QS")) {
          hand |= (mask << 49);
        }
        else if (crd[i].toString().equalsIgnoreCase("KS")) {
          hand |= (mask << 50);
        }
        else if (crd[i].toString().equalsIgnoreCase("AS")) {
          hand |= (mask << 51);
        }
        else {
          //System.out.println("Invalid Card in the argument " + crd[i].toString());
        }
        if (hand != 0) {
        }
        else {
          //System.out.println("Hand cannot be empty");
        }
      }
      return hand;
    }
    
  public static long getHandFromStr(String str) {
    String data[] = str.split(",");
    long hand = 0x0L;
    long mask = 0x1L;
    for (int i = 0; i < data.length; i++) {
      if (data[i].trim().equalsIgnoreCase("c2")) {
        hand |= (mask << 0);
      }
      else if (data[i].trim().equalsIgnoreCase("c3")) {
        hand |= (mask << 1);
      }
      else if (data[i].trim().equalsIgnoreCase("c4")) {
        hand |= (mask << 2);
      }
      else if (data[i].trim().equalsIgnoreCase("c5")) {
        hand |= (mask << 3);
      }
      else if (data[i].trim().equalsIgnoreCase("c6")) {
        hand |= (mask << 4);
      }
      else if (data[i].trim().equalsIgnoreCase("c7")) {
        hand |= (mask << 5);
      }
      else if (data[i].trim().equalsIgnoreCase("c8")) {
        hand |= (mask << 6);
      }
      else if (data[i].trim().equalsIgnoreCase("c9")) {
        hand |= (mask << 7);
      }
      else if (data[i].trim().equalsIgnoreCase("c10") ||
               data[i].trim().equalsIgnoreCase("ct")) {
        hand |= (mask << 8);
      }
      else if (data[i].trim().equalsIgnoreCase("cj")) {
        hand |= (mask << 9);
      }
      else if (data[i].trim().equalsIgnoreCase("cq")) {
        hand |= (mask << 10);
      }
      else if (data[i].trim().equalsIgnoreCase("ck")) {
        hand |= (mask << 11);
      }
      else if (data[i].trim().equalsIgnoreCase("ca")) {
        hand |= (mask << 12);
      }
      else if (data[i].trim().equalsIgnoreCase("d2")) {
        hand |= (mask << 13);
      }
      else if (data[i].trim().equalsIgnoreCase("d3")) {
        hand |= (mask << 14);
      }
      else if (data[i].trim().equalsIgnoreCase("d4")) {
        hand |= (mask << 15);
      }
      else if (data[i].trim().equalsIgnoreCase("d5")) {
        hand |= (mask << 16);
      }
      else if (data[i].trim().equalsIgnoreCase("d6")) {
        hand |= (mask << 17);
      }
      else if (data[i].trim().equalsIgnoreCase("d7")) {
        hand |= (mask << 18);
      }
      else if (data[i].trim().equalsIgnoreCase("d8")) {
        hand |= (mask << 19);
      }
      else if (data[i].trim().equalsIgnoreCase("d9")) {
        hand |= (mask << 20);
      }
      else if (data[i].trim().equalsIgnoreCase("d10") ||
               data[i].trim().equalsIgnoreCase("dt")) {
        hand |= (mask << 21);
      }
      else if (data[i].trim().equalsIgnoreCase("dj")) {
        hand |= (mask << 22);
      }
      else if (data[i].trim().equalsIgnoreCase("dq")) {
        hand |= (mask << 23);
      }
      else if (data[i].trim().equalsIgnoreCase("dk")) {
        hand |= (mask << 24);
      }
      else if (data[i].trim().equalsIgnoreCase("da")) {
        hand |= (mask << 25);
      }
      else if (data[i].trim().equalsIgnoreCase("h2")) {
        hand |= (mask << 26);
      }
      else if (data[i].trim().equalsIgnoreCase("h3")) {
        hand |= (mask << 27);
      }
      else if (data[i].trim().equalsIgnoreCase("h4")) {
        hand |= (mask << 28);
      }
      else if (data[i].trim().equalsIgnoreCase("h5")) {
        hand |= (mask << 29);
      }
      else if (data[i].trim().equalsIgnoreCase("h6")) {
        hand |= (mask << 30);
      }
      else if (data[i].trim().equalsIgnoreCase("h7")) {
        hand |= (mask << 31);
      }
      else if (data[i].trim().equalsIgnoreCase("h8")) {
        hand |= (mask << 32);
      }
      else if (data[i].trim().equalsIgnoreCase("h9")) {
        hand |= (mask << 33);
      }
      else if (data[i].trim().equalsIgnoreCase("h10") ||
               data[i].trim().equalsIgnoreCase("ht")) {
        hand |= (mask << 34);
      }
      else if (data[i].trim().equalsIgnoreCase("hj")) {
        hand |= (mask << 35);
      }
      else if (data[i].trim().equalsIgnoreCase("hq")) {
        hand |= (mask << 36);
      }
      else if (data[i].trim().equalsIgnoreCase("hk")) {
        hand |= (mask << 37);
      }
      else if (data[i].trim().equalsIgnoreCase("ha")) {
        hand |= (mask << 38);
      }
      else if (data[i].trim().equalsIgnoreCase("s2")) {
        hand |= (mask << 39);
      }
      else if (data[i].trim().equalsIgnoreCase("s3")) {
        hand |= (mask << 40);
      }
      else if (data[i].trim().equalsIgnoreCase("s4")) {
        hand |= (mask << 41);
      }
      else if (data[i].trim().equalsIgnoreCase("s5")) {
        hand |= (mask << 42);
      }
      else if (data[i].trim().equalsIgnoreCase("s6")) {
        hand |= (mask << 43);
      }
      else if (data[i].trim().equalsIgnoreCase("s7")) {
        hand |= (mask << 44);
      }
      else if (data[i].trim().equalsIgnoreCase("s8")) {
        hand |= (mask << 45);
      }
      else if (data[i].trim().equalsIgnoreCase("s9")) {
        hand |= (mask << 46);
      }
      else if (data[i].trim().equalsIgnoreCase("s10") ||
               data[i].trim().equalsIgnoreCase("st")) {
        hand |= (mask << 47);
      }
      else if (data[i].trim().equalsIgnoreCase("sj")) {
        hand |= (mask << 48);
      }
      else if (data[i].trim().equalsIgnoreCase("sq")) {
        hand |= (mask << 49);
      }
      else if (data[i].trim().equalsIgnoreCase("sk")) {
        hand |= (mask << 50);
      }
      else if (data[i].trim().equalsIgnoreCase("sa")) {
        hand |= (mask << 51);
      }
      else {
        //System.out.println("Invalid Card in the argument " + data[i]);
      }
      if (hand != 0) {
      }
      else {
        //System.out.println("Hand cannot be empty");
      }
    }
    return hand;
  }

  public static long getHandFromStr2(String str) {
    String data[] = str.split(",");
    long hand = 0x0L;
    long mask = 0x1L;
    for (int i = 0; i < data.length; i++) {
      if (data[i].trim().equalsIgnoreCase("2c")) {
        hand |= (mask << 0);
      }
      else if (data[i].trim().equalsIgnoreCase("3c")) {
        hand |= (mask << 1);
      }
      else if (data[i].trim().equalsIgnoreCase("4c")) {
        hand |= (mask << 2);
      }
      else if (data[i].trim().equalsIgnoreCase("5c")) {
        hand |= (mask << 3);
      }
      else if (data[i].trim().equalsIgnoreCase("6c")) {
        hand |= (mask << 4);
      }
      else if (data[i].trim().equalsIgnoreCase("7c")) {
        hand |= (mask << 5);
      }
      else if (data[i].trim().equalsIgnoreCase("8c")) {
        hand |= (mask << 6);
      }
      else if (data[i].trim().equalsIgnoreCase("9c")) {
        hand |= (mask << 7);
      }
      else if (data[i].trim().equalsIgnoreCase("10c") ||
               data[i].trim().equalsIgnoreCase("tc")) {
        hand |= (mask << 8);
      }
      else if (data[i].trim().equalsIgnoreCase("jc")) {
        hand |= (mask << 9);
      }
      else if (data[i].trim().equalsIgnoreCase("qc")) {
        hand |= (mask << 10);
      }
      else if (data[i].trim().equalsIgnoreCase("kc")) {
        hand |= (mask << 11);
      }
      else if (data[i].trim().equalsIgnoreCase("ac")) {
        hand |= (mask << 12);
      }
      else if (data[i].trim().equalsIgnoreCase("2d")) {
        hand |= (mask << 13);
      }
      else if (data[i].trim().equalsIgnoreCase("3d")) {
        hand |= (mask << 14);
      }
      else if (data[i].trim().equalsIgnoreCase("4d")) {
        hand |= (mask << 15);
      }
      else if (data[i].trim().equalsIgnoreCase("5d")) {
        hand |= (mask << 16);
      }
      else if (data[i].trim().equalsIgnoreCase("6d")) {
        hand |= (mask << 17);
      }
      else if (data[i].trim().equalsIgnoreCase("7d")) {
        hand |= (mask << 18);
      }
      else if (data[i].trim().equalsIgnoreCase("8d")) {
        hand |= (mask << 19);
      }
      else if (data[i].trim().equalsIgnoreCase("9d")) {
        hand |= (mask << 20);
      }
      else if (data[i].trim().equalsIgnoreCase("10d") ||
               data[i].trim().equalsIgnoreCase("td")) {
        hand |= (mask << 21);
      }
      else if (data[i].trim().equalsIgnoreCase("jd")) {
        hand |= (mask << 22);
      }
      else if (data[i].trim().equalsIgnoreCase("qd")) {
        hand |= (mask << 23);
      }
      else if (data[i].trim().equalsIgnoreCase("kd")) {
        hand |= (mask << 24);
      }
      else if (data[i].trim().equalsIgnoreCase("ad")) {
        hand |= (mask << 25);
      }
      else if (data[i].trim().equalsIgnoreCase("2h")) {
        hand |= (mask << 26);
      }
      else if (data[i].trim().equalsIgnoreCase("3h")) {
        hand |= (mask << 27);
      }
      else if (data[i].trim().equalsIgnoreCase("4h")) {
        hand |= (mask << 28);
      }
      else if (data[i].trim().equalsIgnoreCase("5h")) {
        hand |= (mask << 29);
      }
      else if (data[i].trim().equalsIgnoreCase("6h")) {
        hand |= (mask << 30);
      }
      else if (data[i].trim().equalsIgnoreCase("7h")) {
        hand |= (mask << 31);
      }
      else if (data[i].trim().equalsIgnoreCase("8h")) {
        hand |= (mask << 32);
      }
      else if (data[i].trim().equalsIgnoreCase("9h")) {
        hand |= (mask << 33);
      }
      else if (data[i].trim().equalsIgnoreCase("10h") ||
               data[i].trim().equalsIgnoreCase("th")) {
        hand |= (mask << 34);
      }
      else if (data[i].trim().equalsIgnoreCase("jh")) {
        hand |= (mask << 35);
      }
      else if (data[i].trim().equalsIgnoreCase("qh")) {
        hand |= (mask << 36);
      }
      else if (data[i].trim().equalsIgnoreCase("kh")) {
        hand |= (mask << 37);
      }
      else if (data[i].trim().equalsIgnoreCase("ah")) {
        hand |= (mask << 38);
      }
      else if (data[i].trim().equalsIgnoreCase("2s")) {
        hand |= (mask << 39);
      }
      else if (data[i].trim().equalsIgnoreCase("3s")) {
        hand |= (mask << 40);
      }
      else if (data[i].trim().equalsIgnoreCase("4s")) {
        hand |= (mask << 41);
      }
      else if (data[i].trim().equalsIgnoreCase("5s")) {
        hand |= (mask << 42);
      }
      else if (data[i].trim().equalsIgnoreCase("6s")) {
        hand |= (mask << 43);
      }
      else if (data[i].trim().equalsIgnoreCase("7s")) {
        hand |= (mask << 44);
      }
      else if (data[i].trim().equalsIgnoreCase("8s")) {
        hand |= (mask << 45);
      }
      else if (data[i].trim().equalsIgnoreCase("9s")) {
        hand |= (mask << 46);
      }
      else if (data[i].trim().equalsIgnoreCase("10s") ||
               data[i].trim().equalsIgnoreCase("ts")) {
        hand |= (mask << 47);
      }
      else if (data[i].trim().equalsIgnoreCase("js")) {
        hand |= (mask << 48);
      }
      else if (data[i].trim().equalsIgnoreCase("qs")) {
        hand |= (mask << 49);
      }
      else if (data[i].trim().equalsIgnoreCase("ks")) {
        hand |= (mask << 50);
      }
      else if (data[i].trim().equalsIgnoreCase("as")) {
        hand |= (mask << 51);
      }
      else {
        //System.out.println("Invalid Card in the argument " + data[i]);
      }
      if (hand != 0) {
      }
      else {
        //System.out.println("Hand cannot be empty");
      }
    }
    return hand;
  }

  public static long getHandFromStr3(String str) {
    String data[] = str.split("'");
    long hand = 0x0L;
    long mask = 0x1L;
    for (int i = 0; i < data.length; i++) {
      if (data[i].trim().equalsIgnoreCase("2c")) {
        hand |= (mask << 0);
      }
      else if (data[i].trim().equalsIgnoreCase("3c")) {
        hand |= (mask << 1);
      }
      else if (data[i].trim().equalsIgnoreCase("4c")) {
        hand |= (mask << 2);
      }
      else if (data[i].trim().equalsIgnoreCase("5c")) {
        hand |= (mask << 3);
      }
      else if (data[i].trim().equalsIgnoreCase("6c")) {
        hand |= (mask << 4);
      }
      else if (data[i].trim().equalsIgnoreCase("7c")) {
        hand |= (mask << 5);
      }
      else if (data[i].trim().equalsIgnoreCase("8c")) {
        hand |= (mask << 6);
      }
      else if (data[i].trim().equalsIgnoreCase("9c")) {
        hand |= (mask << 7);
      }
      else if (data[i].trim().equalsIgnoreCase("10c") ||
               data[i].trim().equalsIgnoreCase("tc")) {
        hand |= (mask << 8);
      }
      else if (data[i].trim().equalsIgnoreCase("jc")) {
        hand |= (mask << 9);
      }
      else if (data[i].trim().equalsIgnoreCase("qc")) {
        hand |= (mask << 10);
      }
      else if (data[i].trim().equalsIgnoreCase("kc")) {
        hand |= (mask << 11);
      }
      else if (data[i].trim().equalsIgnoreCase("ac")) {
        hand |= (mask << 12);
      }
      else if (data[i].trim().equalsIgnoreCase("2d")) {
        hand |= (mask << 13);
      }
      else if (data[i].trim().equalsIgnoreCase("3d")) {
        hand |= (mask << 14);
      }
      else if (data[i].trim().equalsIgnoreCase("4d")) {
        hand |= (mask << 15);
      }
      else if (data[i].trim().equalsIgnoreCase("5d")) {
        hand |= (mask << 16);
      }
      else if (data[i].trim().equalsIgnoreCase("6d")) {
        hand |= (mask << 17);
      }
      else if (data[i].trim().equalsIgnoreCase("7d")) {
        hand |= (mask << 18);
      }
      else if (data[i].trim().equalsIgnoreCase("8d")) {
        hand |= (mask << 19);
      }
      else if (data[i].trim().equalsIgnoreCase("9d")) {
        hand |= (mask << 20);
      }
      else if (data[i].trim().equalsIgnoreCase("10d") ||
               data[i].trim().equalsIgnoreCase("td")) {
        hand |= (mask << 21);
      }
      else if (data[i].trim().equalsIgnoreCase("jd")) {
        hand |= (mask << 22);
      }
      else if (data[i].trim().equalsIgnoreCase("qd")) {
        hand |= (mask << 23);
      }
      else if (data[i].trim().equalsIgnoreCase("kd")) {
        hand |= (mask << 24);
      }
      else if (data[i].trim().equalsIgnoreCase("ad")) {
        hand |= (mask << 25);
      }
      else if (data[i].trim().equalsIgnoreCase("2h")) {
        hand |= (mask << 26);
      }
      else if (data[i].trim().equalsIgnoreCase("3h")) {
        hand |= (mask << 27);
      }
      else if (data[i].trim().equalsIgnoreCase("4h")) {
        hand |= (mask << 28);
      }
      else if (data[i].trim().equalsIgnoreCase("5h")) {
        hand |= (mask << 29);
      }
      else if (data[i].trim().equalsIgnoreCase("6h")) {
        hand |= (mask << 30);
      }
      else if (data[i].trim().equalsIgnoreCase("7h")) {
        hand |= (mask << 31);
      }
      else if (data[i].trim().equalsIgnoreCase("8h")) {
        hand |= (mask << 32);
      }
      else if (data[i].trim().equalsIgnoreCase("9h")) {
        hand |= (mask << 33);
      }
      else if (data[i].trim().equalsIgnoreCase("10h") ||
               data[i].trim().equalsIgnoreCase("th")) {
        hand |= (mask << 34);
      }
      else if (data[i].trim().equalsIgnoreCase("jh")) {
        hand |= (mask << 35);
      }
      else if (data[i].trim().equalsIgnoreCase("qh")) {
        hand |= (mask << 36);
      }
      else if (data[i].trim().equalsIgnoreCase("kh")) {
        hand |= (mask << 37);
      }
      else if (data[i].trim().equalsIgnoreCase("ah")) {
        hand |= (mask << 38);
      }
      else if (data[i].trim().equalsIgnoreCase("2s")) {
        hand |= (mask << 39);
      }
      else if (data[i].trim().equalsIgnoreCase("3s")) {
        hand |= (mask << 40);
      }
      else if (data[i].trim().equalsIgnoreCase("4s")) {
        hand |= (mask << 41);
      }
      else if (data[i].trim().equalsIgnoreCase("5s")) {
        hand |= (mask << 42);
      }
      else if (data[i].trim().equalsIgnoreCase("6s")) {
        hand |= (mask << 43);
      }
      else if (data[i].trim().equalsIgnoreCase("7s")) {
        hand |= (mask << 44);
      }
      else if (data[i].trim().equalsIgnoreCase("8s")) {
        hand |= (mask << 45);
      }
      else if (data[i].trim().equalsIgnoreCase("9s")) {
        hand |= (mask << 46);
      }
      else if (data[i].trim().equalsIgnoreCase("10s") ||
               data[i].trim().equalsIgnoreCase("ts")) {
        hand |= (mask << 47);
      }
      else if (data[i].trim().equalsIgnoreCase("js")) {
        hand |= (mask << 48);
      }
      else if (data[i].trim().equalsIgnoreCase("qs")) {
        hand |= (mask << 49);
      }
      else if (data[i].trim().equalsIgnoreCase("ks")) {
        hand |= (mask << 50);
      }
      else if (data[i].trim().equalsIgnoreCase("as")) {
        hand |= (mask << 51);
      }
      else {
        //System.out.println("Invalid Card in the argument " + data[i]);
      }
      if (hand != 0) {
      }
      else {
        //System.out.println("Hand cannot be empty");
      }
    }
    return hand;
  }

  /**
   * @return If hand1 is a better hand1 than hand2 returns 1 if  they are same
   * zero is returned, -1 if hand2 is better
   */
  public static int compareStudOpenCards(long hand1, long hand2) {
    if (LongOps.getHighs(hand1) != LongOps.getHighs(hand2)) {
      return 0;
    }
    if (LongOps.getHighs(hand1) == 2) {
      return compare2StudCards(hand1, hand2);
    }
    else if (LongOps.getHighs(hand1) == 3) {
      return compare3StudCards(hand1, hand2);
    }
    else if (LongOps.getHighs(hand1) == 4) {
      return compare4StudCards(hand1, hand2);
    }
    else {
      throw new IllegalStateException("Invalid number of cards " +
                                      LongOps.getHighs(hand1));
    }
  }

  public static int compare2StudCards(long hand1,
                                      long hand2) {

    Hand h1 = new Hand(hand1);
    Hand h2 = new Hand(hand2);
    Card[] c1 = h1.getCardsArray();
    Card[] c2 = h2.getCardsArray();

    // check if hand1 has a pair
    if (c1[0].getRank() == c1[1].getRank() &&
        c2[0].getRank() == c2[1].getRank()) {
      // hand1 and hand 2 has a pair
      return cmp(c1[0].getRank(), c2[0].getRank());
    }
    else if (c1[0].getRank() == c1[1].getRank()) {
      return 1; //hand1 is higher
    }
    else if (c2[0].getRank() == c2[1].getRank()) {
      return -1; //hand2 is higher
    }
    else {
      //no pair
      short[] suit1 = HandOps.getSuits(hand1);
      short[] suit2 = HandOps.getSuits(hand2);

      int handVal1 = 0;
      for (int i = 0; i < suit1.length; i++) {
        handVal1 += suit1[i];
      }

      int handVal2 = 0;
      for (int i = 0; i < suit2.length; i++) {
        handVal2 += suit2[i];
      }

      return cmp(handVal1, handVal2);
    }
  }

  public static int compare4StudCards(long hand1,
                                      long hand2) {

    Hand h1 = new Hand(hand1);
    Hand h2 = new Hand(hand2);
    Card[] c1 = h1.getCardsArray();
    Card[] c2 = h2.getCardsArray();

    // check for 4 same cards
    //no pair
    short[] suit1 = HandOps.getSuits(hand1);
    short[] suit2 = HandOps.getSuits(hand2);

    int hand1_has_four = suit1[0] & suit1[1] & suit1[2] & suit1[3];
    int hand2_has_four = suit2[0] & suit2[1] & suit2[2] & suit2[3];

    if (hand1_has_four > 0 && hand2_has_four > 0) { // all 4 cards are same
      return cmp(hand1_has_four, hand2_has_four);
    }
    else if (hand1_has_four > 1) {
      return 1;
    }
    else if (hand2_has_four > 1) {
      return -1;
    }
    else { // no 4 of a kind
      int hand1_has_three = (suit1[0] & suit1[1] & suit1[2]) |
          (suit1[3] & suit1[1] & suit1[2]) | (suit1[3] & suit1[0] & suit1[2]) |
          (suit1[3] & suit1[0] & suit1[1]);
      int hand2_has_three = (suit2[0] & suit2[1] & suit2[2]) |
          (suit2[3] & suit2[1] & suit2[2]) | (suit2[3] & suit2[0] & suit2[2]) |
          (suit2[3] & suit2[0] & suit2[1]);

      // check for 3 pairs
      // check if hand1 has a pair
      if (hand1_has_three > 1 && hand2_has_three > 1) {
        // hand1 and hand 2 has a 3 same cards
        return cmp(hand1_has_three, hand2_has_three);
      }
      else if (hand1_has_three > 1) {
        return 1; //hand1 is higher
      }
      else if (hand2_has_three > 1) {
        return -1; //hand2 is higher
      }
      else { //no 3 same cards
        // check for pair
        int hand1_has_two = (suit1[0] & suit1[1]) |
            (suit1[0] & suit1[2]) | (suit1[0] & suit1[3]) |
            (suit1[1] & suit1[3]) | (suit1[2] & suit1[3]) |
            (suit1[1] & suit1[2]);
        int hand2_has_two = (suit2[0] & suit2[1]) |
            (suit2[0] & suit2[2]) | (suit2[0] & suit2[3]) |
            (suit2[1] & suit2[3]) | (suit2[2] & suit2[3]) |
            (suit2[1] & suit2[2]);

        // check for 2 pairs
        if (LongOps.getHighs(hand1_has_two) == 2 &&
            LongOps.getHighs(hand2_has_two) == 2) {
          // both hand1 and hand2 have 2 2 pairs
          return cmp(hand1_has_two, hand2_has_two);
        }
        else if (LongOps.getHighs(hand1_has_two) == 2) {
          return 1;
        }
        else if (LongOps.getHighs(hand2_has_two) == 2) {
          return -1;
        }
        else {
          // no 2 pairs check for single pair
          if (LongOps.getHighs(hand1_has_two) == 1 &&
              LongOps.getHighs(hand2_has_two) == 1) {
            // hand1 and hand 2 has a pair
            // TODO if the 2 pair is same
            if (hand1_has_two == hand2_has_two) {
              //System.out.println("Single pair same strength");
              suit1 = HandOps.getSuits(hand1);
              suit2 = HandOps.getSuits(hand2);

              int handVal1 = 0;
              for (int i = 0; i < suit1.length; i++) {
                handVal1 += suit1[i];
              }

              int handVal2 = 0;
              for (int i = 0; i < suit2.length; i++) {
                handVal2 += suit2[i];
              }
              return cmp(handVal1, handVal2);
            }
            else {
              return cmp(hand1_has_two, hand2_has_two);
            }
          }
          else if (LongOps.getHighs(hand1_has_two) == 1) {
            return 1; //hand1 is higher
          }
          else if (LongOps.getHighs(hand2_has_two) == 1) {
            return -1; //hand2 is higher
          }
          else {
            //no pair
            suit1 = HandOps.getSuits(hand1);
            suit2 = HandOps.getSuits(hand2);

            int handVal1 = 0;
            for (int i = 0; i < suit1.length; i++) {
              handVal1 += suit1[i];
            }

            int handVal2 = 0;
            for (int i = 0; i < suit2.length; i++) {
              handVal2 += suit2[i];
            }
            return cmp(handVal1, handVal2);
          }
        }
      }
    }
  }

  public static int compare3StudCards(long hand1,
                                      long hand2) {

    Hand h1 = new Hand(hand1);
    Hand h2 = new Hand(hand2);
    Card[] c1 = h1.getCardsArray();
    Card[] c2 = h2.getCardsArray();

    //no pair
    short[] suit1 = HandOps.getSuits(hand1);
    short[] suit2 = HandOps.getSuits(hand2);

    int hand1_has_three = (suit1[0] & suit1[1] & suit1[2]);
    int hand2_has_three = (suit2[0] & suit2[1] & suit2[2]);
    if (hand1_has_three > 1 && hand2_has_three > 1) {
      // hand1 and hand 2 has a 3 same cards
      return cmp(hand1_has_three, hand2_has_three);
    }
    else if (hand1_has_three > 1) {
      return 1; //hand1 is higher
    }
    else if (hand2_has_three > 1) {
      return -1; //hand2 is higher
    }
    else { //no 3 same cards
      // check for pair
      int hand1_has_two = (suit1[0] & suit1[1]) |
          (suit1[0] & suit1[2]) | (suit1[0] & suit1[3]) |
          (suit1[1] & suit1[3]) | (suit1[2] & suit1[3]) |
          (suit1[1] & suit1[2]);
      int hand2_has_two = (suit2[0] & suit2[1]) |
          (suit2[0] & suit2[2]) | (suit2[0] & suit2[3]) |
          (suit2[1] & suit2[3]) | (suit2[2] & suit2[3]) |
          (suit2[1] & suit2[2]);

      //System.out.println(hand1_has_two + " No 3 cards same "+ hand2_has_two);

      // no 2 pairs check for single pair
      if (LongOps.getHighs(hand1_has_two) == 1 &&
          LongOps.getHighs(hand2_has_two) == 1) {
          //System.out.println("Both have a pair");
        // hand1 and hand 2 has a pair
        // TODO if the 2 pair is same
        if (hand1_has_two == hand2_has_two) {
          //System.out.println("Single pair same strength");
          suit1 = HandOps.getSuits(hand1);
          suit2 = HandOps.getSuits(hand2);

          int handVal1 = 0;
          for (int i = 0; i < suit1.length; i++) {
            handVal1 += suit1[i];
          }

          int handVal2 = 0;
          for (int i = 0; i < suit2.length; i++) {
            handVal2 += suit2[i];
          }
          return cmp(handVal1, handVal2);
        }
        else {
          return cmp(hand1_has_two, hand2_has_two);
        }
      }
      else if (LongOps.getHighs(hand1_has_two) == 1 ) {
        return 1; //hand1 is higher
      }
      else if (LongOps.getHighs(hand2_has_two) == 1) {
        return -1; //hand2 is higher
      }
      else { //no pair
        int handVal1 = 0;
        for (int i = 0; i < suit1.length; i++) {
          handVal1 += suit1[i];
        }

        int handVal2 = 0;
        for (int i = 0; i < suit2.length; i++) {
          handVal2 += suit2[i];
        }
        return cmp(handVal1, handVal2);
      }
    } // no 3 cards same
    //throw new IllegalStateException("Should no come here ");
  }


  /**
   * @return If hand1 is a better hand1 than hand2 returns 1 if  they are same
   * zero is returned, -1 if hand2 is better
   */
  public static int compareTeenPattiCards(long hand1, long hand2) {
    if (LongOps.getHighs(hand1) != LongOps.getHighs(hand2)) {
      return 0;
    }

    assert LongOps.getHighs(hand1) == 3:
        "Their should be 3 cards in teen patti";
    Hand h1 = new Hand(hand1);
    Hand h2 = new Hand(hand2);
    Card[] c1 = h1.getCardsArray();
    Card[] c2 = h2.getCardsArray();

    if (LongOps.getHighs(hand1) == 3) {
      // check for 3 pairs
      // check if hand1 has a pair
      if (c1[0].getRank() == c1[1].getRank() &&
          c1[1].getRank() == c1[2].getRank() &&
          c2[0].getRank() == c2[1].getRank() &&
          c2[1].getRank() == c2[2].getRank()) {
        // hand1 and hand 2 has a 3 same cards
        return cmp(c1[0].getRank(), c2[0].getRank());
      }
      else if (c1[0].getRank() == c1[1].getRank() &&
               c1[1].getRank() == c1[2].getRank()) {
        return 1; //hand1 is higher
      }
      else if (c1[0].getRank() == c1[1].getRank() &&
               c2[1].getRank() == c2[2].getRank()) {
        return -1; //hand2 is higher
      }
      else {
        //no 3 same cards
        // check for pair
        if ( (c1[0].getRank() == c1[1].getRank() ||
              c1[1].getRank() == c1[2].getRank() ||
              c1[0].getRank() == c1[2].getRank()) &&
            (c2[0].getRank() == c2[1].getRank() ||
             c2[1].getRank() == c2[2].getRank() ||
             c2[0].getRank() == c2[2].getRank())
            ) {
          // hand1 and hand 2 has a pair
          if (c1[0].getRank() != c2[0].getRank()) {
            return cmp(c1[0].getRank(), c2[0].getRank());
          }
          else { // same pair
            //no pair
            short[] suit1 = HandOps.getSuits(hand1);
            short[] suit2 = HandOps.getSuits(hand2);

            int handVal1 = 0;
            for (int i = 0; i < suit1.length; i++) {
              handVal1 += suit1[i];
            }
            int handVal2 = 0;
            for (int i = 0; i < suit2.length; i++) {
              handVal2 += suit2[i];
            }
            return cmp(handVal1, handVal2);
          }
        }
        else if (c1[0].getRank() == c1[1].getRank() ||
                 c1[1].getRank() == c1[2].getRank() ||
                 c1[0].getRank() == c1[2].getRank()) {
          return 1; //hand1 is higher
        }
        else if (c2[0].getRank() == c2[1].getRank() ||
                 c2[1].getRank() == c2[2].getRank() ||
                 c2[0].getRank() == c2[2].getRank()) {
          return -1; //hand2 is higher
        }
        else {
          //no pair
          short[] suit1 = HandOps.getSuits(hand1);
          short[] suit2 = HandOps.getSuits(hand2);

          int handVal1 = 0;
          for (int i = 0; i < suit1.length; i++) {
            handVal1 += suit1[i];
          }

          int handVal2 = 0;
          for (int i = 0; i < suit2.length; i++) {
            handVal2 += suit2[i];
          }
          return cmp(handVal1, handVal2);
        }
      }

    }
    return 0;
  }

  private static int cmp(int i1, int i2) {
    if (i1 == i2) {
      return 0;
    }
    return i1 > i2 ? 1 : -1;
  }

	public void setOpen() {
		visibility = 0xFFFFFFFFFFFFFFFFL;
		_hand.setOpen();
	}

}

/*
  0-12    LSB for Club : 1FFF
  13-25   LSB for Diamond : 1FFF << 13
  26-38   LSB for Heart : 1FFF << 26
  39-52   LSB for  Spade : 1FFF << 39
 */
