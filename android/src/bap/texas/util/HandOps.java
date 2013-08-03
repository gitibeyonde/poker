package bap.texas.util;

import java.util.Vector;


/**
 *
 *
 * @author not attributable
 * @version 1.0
 */
public class HandOps {
	
	public static int HOLDEM=1;

	

	public static long getHandFromCards(Vector<Card>  cv){
	    long hand = 0x0L;
	    long mask = 0x1L;
	    for (int i = 0; i < cv.size(); i++) {
	        long cl = mask << (cv.get(i).index);
	        hand |= cl;
	    }
	    return hand;
	}
	
  private static String mapHighCard(int pos) {
    String card = "";
    switch (pos) {
      case 1:
        card = "two";
        break;
      case 2:
        card = "three";
        break;
      case 3:
        card = "four";
        break;
      case 4:
        card = "five";
        break;
      case 5:
        card = "six";
        break;
      case 6:
        card = "seven";
        break;
      case 7:
        card = "eight";
        break;
      case 8:
        card = "nine";
        break;
      case 9:
        card = "ten";
        break;
      case 10:
        card = "Jack";
        break;
      case 11:
        card = "Queen";
        break;
      case 12:
        card = "King";
        break;
      case 13:
        card = "Ace";
        break;
      default:
        card = "Invalid Card !!";
        new Exception().printStackTrace();
        System.out.println("Invalid position sent to mapHighCard() : " + pos);
        break;
    }
    return card;
  }

  public HandOps() {
  }

  private static String getHandValue(int value, boolean isHigh) {
    //System.out.println("value = " + value);
    StringBuffer handStrength = new StringBuffer();
    //long value = -1;
    if (isHigh) {
      //value = valueOf(hand);
      if (value > 0) {
        handStrength.append(" ");
      }
      else {
        handStrength.append(" -na-");
      }
    }
    else {
      //value = valueOfLow(hand);
      if (value > 0) {
        handStrength.append(" (low)");
      }
      else {
        handStrength.append(" -na-");
      }
    }

    if (! (value > 0)) {
      return handStrength.toString();
    }
    if (isRoyalFlush(value)) {
      handStrength.append("a Royal Flush");
    }
    else if (isStraightFlush(value)) {
      handStrength.append("a straight: ");
      handStrength.append(mapHighCard( (int) (value - 2700000)));
      handStrength.append(" high");
    }
    else if (is4OfAKind(value)) {
      //(<return-value> - 2600000)%14 will give <position of the kicker>
      //(<return-value> - 2600000 -(<position of the four of a kind>)/14 will give <pos of the four of a kind>
      handStrength.append("four ");
      int card5pos = (int) ( (value - 2600000) % 14);
      int fourOfaKindPos = (int) ( (value - 2600000L - card5pos)) / 14;
      handStrength.append(mapHighCard(fourOfaKindPos));
      handStrength.append("s and a ");
      handStrength.append(mapHighCard(card5pos));
      handStrength.append(" kicker");
    }
    else if (isFullHouse(value)) {
      //(<return-value> - 2500000)%14 will give <position of the 3 card pos>
      //(<return-value> - 2500000 -(<position of the 3 card pos>)/14 will give <position of the 2 card card>
      handStrength.append("full house: three of ");
      int threeCardPos = (int) ( (value - 2500000) % 14);
      handStrength.append(mapHighCard(threeCardPos));
      handStrength.append("s: and two of ");
      handStrength.append(
          mapHighCard( (int) ( (value - 2500000 - threeCardPos) / 14)));
      handStrength.append("s");
    }
    else if (isFlush(value)) {
      //<return_value> - 2400000 = <short value of the flush>
      short tmp = (short) (value - 2400000);
      handStrength.append("a flush: ");
      handStrength.append(mapHighCard(LongOps.highCardPos(tmp)));
      handStrength.append(" high");
    }
    else if (isStraight(value)) {
      //(2300000 - <return_value> ) will give the position of the high card.
      //2299991 a low ace straight i.e A,2,3,4,5
      if (value > 2299991) {
        handStrength.append("a Straight: ");
        handStrength.append(mapHighCard(13 - (int) (2300000 - value)));
        handStrength.append(" high");
      }
      else {
        handStrength.append("a Straight, Low Ace");
      }
    }
    else if (is3OfAKind(value)) {
      //(<return_value> - 2200000) % 14 will give <position for 3 of a kind>
      //((<return_value> - 2200000 - <position for 3 of a kind>))/14 will give <short for the other 2 cards>
      handStrength.append("three ");
      int pos3OfKind = (int) (value - 2200000) % 14;
      short otherCard = (short) ( (value - 2200000 - pos3OfKind) / 14);
      handStrength.append(mapHighCard(pos3OfKind));
      handStrength.append("s and ");
      handStrength.append(mapHighCard(LongOps.highCardPos(otherCard)));
    }
    else if (is2Pair(value)) {
      //(<return-value> - 2000000)%14 = <pos for kicker card>
      //(<return-value> - 2000000 - <short for the two pair>)/14 = <short for the two pair>
      handStrength.append("two pairs: ");
      int posKicker = (int) ( (value - 2000000) % 14);
      short twoPair = (short) ( (value - posKicker - 2000000) / 14);
      int tmpHighPos = LongOps.highCardPos(twoPair);
      handStrength.append(mapHighCard(tmpHighPos));
      handStrength.append("s and ");
      short cardMask = 0x1 << 12;
      cardMask >>= 13 - tmpHighPos;
      twoPair ^= cardMask;
      tmpHighPos = LongOps.highCardPos(twoPair);
      handStrength.append(mapHighCard(tmpHighPos));
      handStrength.append("s: with a ");
      handStrength.append(mapHighCard(posKicker));
      handStrength.append(" kicker");
    }
    else if (is1Pair(value)) {
      //(<return_value> - 2000000)%14 = <position of one pair>
      //((<return-value> - 2000000 - (<other three cards>))/14 = <other three cards>
      handStrength.append("a pair of ");
      int onePairPos = (int) ( (value - 1700000) % 14);
      //short otherCards = (short) ( (value - onePairPos - 1700000) / 14);
      handStrength.append(mapHighCard(onePairPos));
      handStrength.append("s");
      //handStrength += mapHighCard(highCardPos(otherCards));
    }
    else if (islowHand(value)) {
      handStrength.append(mapHighCard(LongOps.highCardPos( (short) (value - 1000000))-1));
      handStrength.append(" low");
    }
    else {
      handStrength.append("High card - ");
      handStrength.append(mapHighCard(LongOps.highCardPos( (short) value)));
    }
    //__logFile.Debug("END - CHand::getHandValue()");
    return handStrength.toString();
  }


  public static String getHandValue(long bestHand, int gameType) {
    String result = "";
    if ( ( (gameType & HOLDEM) == gameType)  ) {
      result = getHandValue(valueOfHighHand(bestHand), true);
    }
    /**else if ( ( (gameType & PokerGameType.STUDHILO) == gameType) ||
             ( (gameType & PokerGameType.OMAHAHILO) == gameType)
             ) {
      result = getHandValue(valueOfHighHand(bestHand), true);
      result += getHandValue(valueOfLowHand(bestHand), false);
    }**/
    return result;
  }

  public static String getHandValue(long bestHand, boolean isHigh) {
    String result = "";
    if (isHigh) {
      result = getHandValue(valueOfHighHand(bestHand), true);
    }
    else {
      // the ace if present in a low hand is shifted to 0 position
      result = getHandValue(valueOfLowHand(bestHand), false);
    }
    return result;
  }
  /**
   * @param hand1 - 5 Card hand
   * @param hand2 - 5 Card hand
   * @param high
   * @return If hand1 is a better hand than hand2 returns 1 if  they are same
   * zero is returned, -1 if hand2 is better
   */
  public static int compareHand(long hand1, long hand2, boolean high) {
    int result = -99;
    if (high) {//HIGH
      //System.out.println("HIGH HAND CALC");
      int val1 = valueOfHighHand(hand1);
      int val2 = valueOfHighHand(hand2);
      if (isRoyalFlush(val1) && isRoyalFlush(val2)) {
        result = 0;
      }
      else if (isStraightFlush(val1) && isStraightFlush(val2)) {
        result = (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
      }
      else if (is4OfAKind(val1) && is4OfAKind(val2)) {
        int pos_v1 = (val1 - 2600000) % 14;
        int pos_v2 = (val2 - 2600000) % 14;
        result = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
        if (result == 0) {
          int pos2_v1 = (val1 - 2600000 - pos_v1) / 14;
          int pos2_v2 = (val2 - 2600000 - pos_v2) / 14;
          result = (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
        }
      }
      else if (isFullHouse(val1) && isFullHouse(val2)) {
        int pos_v1 = (val1 - 2500000) % 14;
        int pos_v2 = (val2 - 2500000) % 14;
        result = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
        if (result == 0) {
          int pos2_v1 = (val1 - 2500000 - pos_v1) / 14;
          int pos2_v2 = (val2 - 2500000 - pos_v2) / 14;
          result = (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
        }
      }
      else if (isFlush(val1) && isFlush(val2)) {
        result = (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
      }
      else if (isStraight(val1) && isStraight(val2)) {
        result = (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
      }
      else if (is3OfAKind(val1) && is3OfAKind(val2)) {
        int pos_v1 = (val1 - 2200000) % 14;
        int pos_v2 = (val2 - 2200000) % 14;
        result = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
        if (result == 0) {
          int pos2_v1 = (val1 - 2200000 - pos_v1) / 14;
          int pos2_v2 = (val2 - 2200000 - pos_v2) / 14;
          result = (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
        }
      }
      else if (is2Pair(val1) && is2Pair(val2)) {
        //System.out.println("both are two pairs values = " + val1 + ", " + val2);
        int kpos_v1 = (val1 - 2000000) % 14;
        int kpos_v2 = (val2 - 2000000) % 14;

        int pos2_v1 = (val1 - 2000000 - kpos_v1) / 14;
        int pos2_v2 = (val2 - 2000000 - kpos_v2) / 14;
        result = (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
        if (result == 0) {
          result = (kpos_v1 > kpos_v2) ? 1 : (kpos_v1 < kpos_v2) ? -1 : 0;
        }
      }
      else if (is1Pair(val1) && is1Pair(val2)) {
        int pos_v1 = (val1 - 1700000) % 14;
        int pos_v2 = (val2 - 1700000) % 14;
        result = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
        if (result == 0) {
          int pos2_v1 = (val1 - 1700000 - pos_v1) / 14;
          int pos2_v2 = (val2 - 1700000 - pos_v2) / 14;
          result = (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
        }
      }
      else if (isBasic(val1) && isBasic(val2)) {
        result = (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
      }
      else {
        result = (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
      }
    }
    else { //LOW
      //System.out.print("LOW HAND CALC ==");
      int val1 = valueOfLowHand(hand1);
      //System.out.print("Val1=" + val1);
      //System.out.print("-" + islowHand(val1));
      int val2 = valueOfLowHand(hand2);
      //System.out.print("<> Val2=" + val2);
      //System.out.println("-" + islowHand(val2));
      if (val1 == -1 && val2 == -1) {
        result = 0;
      }
      else if (val1 > -1 && val2 == -1) {
        return 1;
      }
      else if (val1 == -1 && val2 > -1) {
        return -1;
      }
      else if (islowHand(val1) && islowHand(val2)) {
        //System.out.println("Val1= " + val1 + ", Val2=" + val2);
        result = (val1 > val2) ? -1 : (val1 < val2) ? 1 : 0;
      }
      else if (islowHand(val1)) {
        result = 1;
      }
      else if (islowHand(val2)) {
        result = -1;
      }
      else {
        result = -99;
      }
    }
    return result;
  }

  /*
     Value of given hand
   */

  public static int valueOfHighHand(long hand) {
    //System.out.println("High Hand = " + hand);
    if (LongOps.getHighs(hand) < 5) {
      //throw new IllegalArgumentException("Cards in Hand: " +
      //                                  LongOps.getHighs(hand));
      //REMOVE THIS PATCH
      return -1000000;
    }
    int val = 0;
    if ( (val = royalFlush(hand)) != -1) {
      //System.out.println("royalFlush");
      return val; // @todo arbitrary values...define
    }
    if ( (val = straightFlush(hand)) != -1) {
      //System.out.println("straightFlush");
      return val;
    }
    if ( (val = fourOfAKind(hand)) != -1) {
      //System.out.println("fourOfAKind");
      return val;
    }
    if ( (val = fullHouse(hand)) != -1) {
      //System.out.println("fullHouse");
      return val;
    }
    if ( (val = flush(hand)) != -1) {
      //System.out.println("flush");
      return val;
    }
    if ( (val = straight(hand)) != -1) {
      //System.out.println("straight");
      return val;
    }
    if ( (val = threeOfAKind(hand)) != -1) {
      //System.out.println("threeOfAKind");
      return val;
    }
    if ( (val = twoPair(hand)) != -1) {
      //System.out.println("twoPair");
      return val;
    }
    if ( (val = onePair(hand)) != -1) {
      //System.out.println("onePair");
      return val;
    }
    //System.out.println("basicValue");
    return basicValue(hand); // size possible is 13
  }

  public static int valueOfLowHand(long hand) {
    //System.out.println("LOW Hand = " + hand);
    if (LongOps.getHighs(hand) < 2) {
      new Exception("valueOfLowHand(long hand): Less than 2 cards passed to this method").printStackTrace();
      //throw new IllegalArgumentException("Cards in Hand: " +
      //LongOps.getHighs(hand));
      //REMOVE THIS PATCH
      return -1000000;
    }
    return lowHand(hand);
  }

  public static boolean isRoyalFlush(long val) {
    boolean result = false;
    result = (val == 2800000) ? true : false;
    return result;
  }

  public static boolean isStraightFlush(int val) {
    boolean result = false;
    result = ( (val >= 2700001) && (val <= 2700013)) ? true : false;
    return result;
  }

  public static boolean is4OfAKind(int val) {
    boolean result = false;
    result = ( (val >= 2600015) && (val <= 2600195)) ? true : false;
    return result;
  }

  public static boolean isFullHouse(int val) {
    boolean result = false;
    result = ( (val >= 2500015) && (val <= 2500195)) ? true : false;
    return result;
  }

  public static boolean isFlush(int val) {
    boolean result = false;
    result = ( (val >= 2400031) && (val <= 2407936)) ? true : false;
    return result;
  }

  public static boolean isStraight(int val) {
    boolean result = false;
    result = ( (val >= 2299991) && (val <= 2300000)) ? true : false;
    return result;
  }

  public static boolean is3OfAKind(int val) {
    boolean result = false;
    result = ( (val >= 2200042) && (val <= 2286029)) ? true : false;
    return result;
  }

  public static boolean is2Pair(int val) {
    boolean result = false;
    result = ( (val >= 2000000) && (val <= 2086027)) ? true : false;
    return result;
  }

  public static boolean is1Pair(int val) {
    boolean result = false;
    result = ( (val >= 1700000) && (val <= 1800362)) ? true : false;
    return result;
  }

  public static boolean islowHand(int val) {
    boolean result = false;
    result = ( (val >= 1000031) && (val <= 1000255)) ? true : false;
    return result;
  }

  public static boolean isBasic(int val) {
    boolean result = false;
    result = ( (val >= 31) && (val <= 7936)) ? true : false;
    return result;
  }

  //2,800,000
  public static int royalFlush(long hand) {
    final short rfMask = (short) (0x1F << 8); // royal flush mask
    //short[] suits = getSuits(hand);
    short[] suits = getSuits(hand);
    for (int k = 0; k < suits.length; k++) {
      if (removeExtraCards(suits[k], 5, true) == rfMask) {
        return 2800000;
      }
    }
    return -1;

  }

  /*
    @todo can be optimized further by employing block move and search.
   Current implementation is acceptable too, since we know size length
   todo of check can be 13

   */

//(<return_value> - 2700000) will give the position of the high
//card in straight Flush
//base( 13 to 1)  + 2,700,000
  public static int straightFlush(long hand) {
    //short mask = 0x1 << 12;
    short[] suits = getSuits(hand);
    for (int k = 0; k < suits.length; k++) {
      short tmp_suit = suits[k];
      short fMask = 0x1F; // straight flush mask
      while (tmp_suit >= fMask) {
        if ( (tmp_suit & fMask) == fMask) {
          return (2700000 + LongOps.highCardPos(fMask));
        }
        else {
          fMask <<= 1;
        }
      }
    }
    return -1;
  }

//(<return-value> - 2600000)%14 will give <position of the four of a kind>
//(<return-value> - 2600000 -(<position of the four of a kind>)/14 will give <pos of the fifth card>
//base( 195 to 15 )  + 2,600,000
  public static int fourOfAKind(long hand) {
    short[] suits = getSuits(hand);
    //byte highs = LongOps.getHighs(hand);
    short fourOfAKind = 0x1FFF; //-1
    short fourOfAKind_OR = 0x0;
    for (int k = 0; k < suits.length; k++) {
      fourOfAKind &= suits[k];
      fourOfAKind_OR |= suits[k];
    }
    return (LongOps.getHighs(fourOfAKind) == 1) ?
        (LongOps.highCardPos(fourOfAKind) * 14) +
        LongOps.highCardPos(removeExtraCards( (short) (fourOfAKind_OR ^ fourOfAKind), 1, true)) +
        2600000 : -1;
  }

//(<return-value> - 2500000)%14 will give <position of the 3 card pos>
//(<return-value> - 2500000 -(<position of the 3 card pos>)/14 will give <position of the 2 card card>
//base( 195 to 15 )  + 2,500,000
  static int fullHouse(long hand) {
    short[] suits = getSuits(hand);
    //byte highs = LongOps.getHighs(hand);
    short fullHouse = 0x0;
    short xor = 0x0;
    for (int k = 0; k < suits.length; k++) {
      fullHouse |= suits[k];
      xor ^= suits[k]; //will contain "single card" or "3 of a kind"
    }
    /*****/
    if ( (LongOps.getHighs(hand) - LongOps.getHighs(fullHouse)) >= 3) {
      //its a full house
      int twoCardPos = LongOps.highCardPos(fullHouse ^ xor);
      int threeCardsPos = 0;
      int count = 0;
      short threeCardMask;
      int xor_highs = LongOps.getHighs(xor);
      for (int i = 0; i <= xor_highs; i++) {
        count = 0;
        threeCardMask = 0x1 << 12;
        threeCardsPos = LongOps.highCardPos(xor);
        //System.out.println("xor = " + xor);
        //System.out.println("threeCardsPos = " + threeCardsPos);
        threeCardMask >>>= (13 - threeCardsPos);
        //System.out.println("threeCardMask = " + threeCardMask);
        for (int j = 0; j < suits.length; j++) {
          if ( (suits[j] & threeCardMask) == threeCardMask) {
            count++;
            //System.out.println("count = " + count);
          }
        }
        if (count >= 3) {
          //System.out.println("break");
          break;
        }
        if ( (xor & threeCardMask) == threeCardMask) {
          xor ^= threeCardMask;
        }
        if (xor == 0) {
          return -1;
        }
      }
      return (twoCardPos * 14) + threeCardsPos + 2500000;
    }
    return -1;

    /*****/
    /*
         int twoCardPos = -1;
         int threeCardsPos = -1;
         if (xor > 0 && ~xor > 0) {
      twoCardPos = highCardPos( (short)~xor);
      threeCardsPos = 0;
      int count = 0;
      for (int i = 0; i <= LongOps.getHighs(xor); i++) {
        count = 0;
        short threeCardMask = 0x1 << 12;
        threeCardsPos = highCardPos(xor);
        threeCardMask >>>= threeCardsPos;
        for (int j = 0; j < suits.length; j++) {
          if ( (suits[j] & threeCardMask) == threeCardMask) {
            count++;
          }
        }
        if (count >= 3) {
          break;
        }
        xor ^= threeCardMask;
      }
         }
         return LongOps.getHighs(fullHouse) == (highs - 3) ?
        (twoCardPos * 14) + threeCardsPos + 2500000 : -1;
     */
  }

//<return_value> - 2400000 = <short value of the flush>
//base( 7936 to 31 )  + 2,400,000
  static int flush(long hand) {
    short[] suits = getSuits(hand);
    for (int k = 0; k < suits.length; k++) {
      if (LongOps.getHighs(suits[k]) >= 5) {
        return (removeExtraCards(suits[k], 5, true) + 2400000); //550,000
      }
    }
    return -1;

  }

//2300000 to 2299992 (representing a high ace straight and  a high 6 straight respectively).
//13 - (2300000 - <return_value> ) will give the rank of the high card.
//2299991 a low ace straight i.e A,2,3,4,5
//base( 0 to 8 )  + 2,300,000  , 2,299,991 for ace low
  static int straight(long hand) {
    final int mask = 0x1F << 8;
    short straight = 0x0;
    short[] suits = getSuits(hand);
    for (int k = 0; k < suits.length; k++) {
      straight |= suits[k];
    }
    //check for low ace 0x100F;
    final short lowAceMask = 0x100F;
    if ( (lowAceMask & straight) == lowAceMask) {
      return (2300000 - 9);
    }
    for (int pos = 0; (mask >>> pos) >= 0x1F; pos++) {
      if ( ( (mask >>> pos) & straight) == (mask >>> pos)) {
        int tmp = 2300000 - pos;
        return tmp; //pos can be 0 to 8
      }
    }
    return -1;
  }

//(<return_value> - 2200000) % 14 will give <position for 3 of a kind>
//((<return_value> - 2200000 - <short for the other 2 cards>))/14 will give <short for the other 2 cards>
//base (86,017 to 42)  + 2,200,000
//2,200,000 to 2,286,017
  static int threeOfAKind(long hand) {
    short[] suits = getSuits(hand);
    byte highs = LongOps.getHighs(hand);
    short threeOfAKind = 0x0;
    short xor = 0x0;
    for (int k = 0; k < suits.length; k++) {
      threeOfAKind |= suits[k];
      xor ^= suits[k];
    }
    short other_cards = threeOfAKind; //save for later use below.
    boolean bthreeOfAKind = ( (threeOfAKind & xor) == xor) &&
        (LongOps.getHighs(xor) <= (highs - 2)) ? true : false;
    if (bthreeOfAKind) {
      //System.out.println("threeOfAKind = " + threeOfAKind);
      int loops = 0;
      int max_loops = LongOps.getHighs(threeOfAKind);
      while (loops < max_loops) {
        int count = 0; //reinitialise
        short card_mask = 0x1 << 12;
        int bit_pos = 13 - LongOps.highCardPos(threeOfAKind);
        card_mask >>>= bit_pos;
        //System.out.println("card_mask = " + card_mask);
        threeOfAKind ^= card_mask; //reset bthreeOfAKind
        //System.out.println("resetting threeOfAKind = " + threeOfAKind);
        for (int k = 0; k < suits.length; k++) {
          short result = 0x0;
          result = (short) (card_mask & suits[k]);
          if (LongOps.getHighs(result) == 1) {
            count++;
            //System.out.println("count = " + count);
          }
        }
        if (count == 3) {
          other_cards ^= card_mask;
          return 2200000 + LongOps.highCardPos(card_mask) +
              removeExtraCards(other_cards, 2, true) * 14;
        }
        loops++;
      }
      return -1; //shouldnt come here
    }
    else {
      return -1;
    }
  }

//(<return-value> - 2000000)%14 = <pos for kicker card>
//(<return-value> - 2000000 - <pos for kicker card>)/14 = <short for the two pair>
//Range = 2000000 to 2086027
  static int twoPair(long hand) {
    short[] suits = getSuits(hand);
    byte highs = LongOps.getHighs(hand);
    short twoPair = 0X0;
    short xor = 0X0;
    for (int k = 0; k < suits.length; k++) {
      twoPair |= suits[k];
      xor ^= suits[k];
    }
    if (LongOps.getHighs(twoPair) <= (highs - 2) &&
        LongOps.getHighs(xor) <= (highs - 4)) {
      //System.out.println("highs = " + highs);
      //System.out.println("LongOps.getHighs(twoPair) = " + LongOps.getHighs(twoPair));
      //System.out.println("LongOps.getHighs(xor)" + LongOps.getHighs(xor));
      int kicker;
      short pairs = (short) (twoPair & ~xor);
      short twoPairMask = 0x0;
      if (LongOps.getHighs(pairs) > 2) {
        while (LongOps.getHighs(twoPairMask) < 2) {
          short tmp_mask = 0x1 << 12;
          tmp_mask >>>= (13 - LongOps.highCardPos(pairs));
          twoPairMask |= tmp_mask;
          //System.out.println("twoPairMask = " + twoPairMask);
          pairs ^= tmp_mask;
        }
        int tmp1 = LongOps.highCardPos(pairs);
        int tmp2 = LongOps.highCardPos(xor);
        kicker = (tmp1 > tmp2) ? tmp1 : tmp2;
      }
      else {
        kicker = LongOps.highCardPos(xor);
        twoPairMask = (short) (twoPair & (~xor));
      }
      //int val = kicker + (twoPairMask * 14) + 2000000;
      return (kicker + (twoPairMask * 14) + 2000000);
    }
    else {
      return -1;
    }
    /*
         return LongOps.getHighs(twoPair) <= (highs - 2) &&
        LongOps.getHighs(xor) <= (highs - 4) ?
        highCardPos(xor) + (twoPair & ~xor) * 14 + 2000000 : -1;
     */
  }

//(<return_value> - 1700000)%14 = <position of one pair>
//((<return-value> - 1700000 - (<position of one pair>))/14 = <other three cards>
//Range 1700000 to 1800362
  public static int onePair(long hand) {
    short[] suits = getSuits(hand);
    byte highs = LongOps.getHighs(hand);
    short onePair = 0x0;
    short xor = 0x0;
    for (int k = 0; k < suits.length; k++) {
      onePair |= suits[k];
      xor ^= suits[k];
    }
    //System.out.println("getOtherCards(xor, 3, true) = " + getOtherCards(xor, 3, true));
    //System.out.println("highCardPos( (short) (onePair & ~xor))" + highCardPos( (short) (onePair & ~xor)));
    return LongOps.getHighs(onePair) <= (highs - 1) &&
        LongOps.getHighs(xor) <= (highs - 2) ?
        removeExtraCards(xor, 3, true) * 14 + LongOps.highCardPos( (short) (onePair & ~xor)) +
        1700000 : -1;

  }

//1007936 to 1000031
//(<return_value> - 1000000) contains short value for low hand
//base ( 7936 to 31 )
  static int lowHand(long hand) {
    //ignore straight and flush
    if (fourOfAKind(hand) == -1 && fullHouse(hand) == -1 &&
        threeOfAKind(hand) == -1 && twoPair(hand) == -1 &&
        onePair(hand) == -1) {
      //System.out.println("Calculating low hand for " + Hand.stringValue(hand));
      //get the five lowest cards (Ace is  considered as the lowest)
      short[] suits = getSuits(hand);
      short lowHand = 0x0;
      for (int k = 0; k < suits.length; k++) {
        lowHand |= suits[k];  // no pairs
      }

      short value = removeExtraCards(lowHand, 5, false);
      //System.out.println("Value=" + value + ", lowHand=" + lowHand);
      if (value == -1) {
        return -1;
      }
      // move the ace to position 0 if present by doing a cyclic shift
      if (LongOps.highCardPos( (short) (value)) == 13) {
        value ^= (0x1 << 12);
        value <<= 1; //shift
        value |= 1; //add a low ace
      }
      else {
        value <<= 1; //shift
      }

      int pos = LongOps.highCardPos( (short) (value));

      //above K,Q,J,10,9 are not allowed in a low hand
      //System.out.println("lowHand()::pos = " + pos + ", Value=" + value);
      return (pos > 8) ? -1 : value + 1000000;
    }
    else {
      //System.out.println("no low hand as fourOfAKind or fullHouse or threeOfAKind or or twoPair or onePair was found");
      return -1;
    }
  }

//7936 to 31
//<return_value> = short for five cards
//base( 7936 to 31 )
  static int basicValue(long hand) {
    short[] suits = getSuits(hand);
    short aggr = 0x0; // aggregate
    for (int k = 0; k < suits.length; k++) {
      aggr |= suits[k];
    }
    return removeExtraCards(aggr, 5, true);

  }

  /*
   @todo At a later time merge all the poker point implementations.
   @todo Current way of having separate( though ) repetitive way is very usdeful for
   @todo debugging.
   */

  static short removeExtraCards(short otherCards, int nos, boolean high) {
    short value = 0x0;
    if (otherCards > 0) {
      if (high) {
        short mask = 0x1 << 12;
        int pos = 0;
        for (int i = 0; i < nos; i++) {
          if (otherCards > 0) {
            pos = 13 - LongOps.highCardPos(otherCards);
            value |= ( (mask >>> pos));
            otherCards ^= (mask >>> pos);
          }
        }
      }
      else {
        //Since ace is low get that first
        int pos = 0;
        short mask = 0x1 << 12;
        if ( (otherCards & mask) == mask) { // there is an ace
          value |= mask;
          otherCards ^= mask;
          nos--;
        }
        mask = 0x1;
        for (int i = 0; i < nos; i++) {
          if (otherCards > 0) {
            pos = LongOps.firstBitPos(otherCards);
            value |= (mask << pos);
            otherCards ^= (mask << pos);
          }
        }
      }
    }
    return value;
  }

  public static final short[] getSuits(long hand) {
    short[] suits = new short[4];
    short mask = 0x1FFF; // SUIT_MASK
    for (int k = 0; k < 4; k++) {
      suits[k] = (short) (hand >>> (k * 13) & mask);
    }
    return suits;
  }

  public static void main(String args[]){
    System.out.println(removeExtraCards((short)0x3F, 5, false));
    System.out.println(removeExtraCards((short)0x3F, 5, true));
  }

}
