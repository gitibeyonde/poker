package com.poker.game.util;

import com.agneya.util.LongOps;

import com.poker.game.PokerGameType;


public class HandComparator {
    public HandComparator() {
    }

    private static int idx = 0;

    /**
     *
     **/
    public static long[] compareGameHand(long PlayerHand1, long PlayerHand2, 
                                         long communityHand, int gameType, 
                                         boolean high) {
        long[] result = new long[4];
        result[0] = -99;
        result[1] = -99;
        result[2] = -99;
        result[3] = -99;

        long val1[] = valueOf(PlayerHand1, communityHand, gameType);
        long val2[] = valueOf(PlayerHand2, communityHand, gameType);

        //System.out.println("Hand 1=" + (val1[0]) + ":" + (val1[1]) + ":" + Hand.stringValue(val1[2]) + ":" + Hand.stringValue(val1[3]));
        //System.out.println("Hand 2=" + (val2[0]) + ":" + (val2[1]) + ":" + Hand.stringValue(val2[2]) + ":" + Hand.stringValue(val2[3]));

        return compareGameHandValues(val1, val2);
    }

    public static long[] compareGameHandValues(long[] val1, long[] val2) {
        long[] result = new long[4];
        result[0] = -99;
        result[1] = -99;
        result[2] = -99;
        result[3] = -99;

        if (HandOps.isRoyalFlush(val1[0]) && HandOps.isRoyalFlush(val2[0])) {
            result[0] = 0;
            result[2] = val1[2];

        } else if (HandOps.isStraightFlush((int)val1[0]) && 
                   HandOps.isStraightFlush((int)val2[0])) {
            result[0] = (val1[0] > val2[0]) ? 1 : (val1[0] < val2[0]) ? -1 : 0;
            result[2] = 
                    (val1[0] > val2[0]) ? val1[2] : (val1[0] < val2[0]) ? val2[2] : 
                                                    val1[2];

        } else if (HandOps.is4OfAKind((int)val1[0]) && 
                   HandOps.is4OfAKind((int)val2[0])) {
            int pos_v1 = ((int)val1[0] - 2600000) % 14;
            int pos_v2 = ((int)val2[0] - 2600000) % 14;
            result[0] = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
            result[2] = 
                    (pos_v1 > pos_v2) ? val1[2] : (pos_v1 < pos_v2) ? val2[2] : 
                                                  val1[2];
            if (result[0] == 0) {
                int pos2_v1 = ((int)val1[0] - 2600000 - pos_v1) / 14;
                int pos2_v2 = ((int)val2[0] - 2600000 - pos_v2) / 14;
                result[0] = 
                        (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
                result[2] = 
                        (pos2_v1 > pos2_v2) ? val1[2] : (pos2_v1 < pos2_v2) ? 
                                                        val2[2] : val1[2];
            }

        } else if (HandOps.isFullHouse((int)val1[0]) && 
                   HandOps.isFullHouse((int)val2[0])) {
            int pos_v1 = ((int)val1[0] - 2500000) % 14;
            int pos_v2 = ((int)val2[0] - 2500000) % 14;
            result[0] = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
            result[2] = 
                    (pos_v1 > pos_v2) ? val1[2] : (pos_v1 < pos_v2) ? val2[2] : 
                                                  val1[2];
            if (result[0] == 0) {
                int pos2_v1 = ((int)val1[0] - 2500000 - pos_v1) / 14;
                int pos2_v2 = ((int)val2[0] - 2500000 - pos_v2) / 14;
                result[0] = 
                        (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
                result[2] = 
                        (pos2_v1 > pos2_v2) ? val1[2] : (pos2_v1 < pos2_v2) ? 
                                                        val2[2] : val1[2];
            }
        } else if (HandOps.isFlush((int)val1[0]) && 
                   HandOps.isFlush((int)val2[0])) {
            result[0] = (val1[0] > val2[0]) ? 1 : (val1[0] < val2[0]) ? -1 : 0;
            result[2] = 
                    (val1[0] > val2[0]) ? val1[2] : (val1[0] < val2[0]) ? val2[2] : 
                                                    val1[2];

        } else if (HandOps.isStraight((int)val1[0]) && 
                   HandOps.isStraight((int)val2[0])) {
            result[0] = (val1[0] > val2[0]) ? 1 : (val1[0] < val2[0]) ? -1 : 0;
            result[2] = 
                    (val1[0] > val2[0]) ? val1[2] : (val1[0] < val2[0]) ? val2[2] : 
                                                    val1[2];

        } else if (HandOps.is3OfAKind((int)val1[0]) && 
                   HandOps.is3OfAKind((int)val2[0])) {
            int pos_v1 = ((int)val1[0] - 2200000) % 14;
            int pos_v2 = ((int)val2[0] - 2200000) % 14;
            result[0] = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
            result[2] = 
                    (pos_v1 > pos_v2) ? val1[2] : (pos_v1 < pos_v2) ? val2[2] : 
                                                  val1[2];
            if (result[0] == 0) {
                int pos2_v1 = ((int)val1[0] - 2200000 - pos_v1) / 14;
                int pos2_v2 = ((int)val2[0] - 2200000 - pos_v2) / 14;
                result[0] = 
                        (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
                result[2] = 
                        (pos2_v1 > pos2_v2) ? val1[2] : (pos2_v1 < pos2_v2) ? 
                                                        val2[2] : val1[2];
            }

        } else if (HandOps.is2Pair((int)val1[0]) && 
                   HandOps.is2Pair((int)val2[0])) {
            int pos_v1 = ((int)val1[0] - 2000000) / 14;
            int pos_v2 = ((int)val2[0] - 2000000) / 14;
            result[0] = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
            result[2] = 
                    (pos_v1 > pos_v2) ? val1[2] : (pos_v1 < pos_v2) ? val2[2] : 
                                                  val1[2];
            if (result[0] == 0) {
                int pos2_v1 = ((int)val1[0] - 2000000) % 14;
                int pos2_v2 = ((int)val2[0] - 2000000) % 14;
                result[0] = 
                        (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
                result[2] = 
                        (pos2_v1 > pos2_v2) ? val1[2] : (pos2_v1 < pos2_v2) ? 
                                                        val2[2] : val1[2];
            }
        } else if (HandOps.is1Pair((int)val1[0]) && 
                   HandOps.is1Pair((int)val2[0])) {
            int pos_v1 = ((int)val1[0] - 1700000) % 14;
            int pos_v2 = ((int)val2[0] - 1700000) % 14;
            result[0] = (pos_v1 > pos_v2) ? 1 : (pos_v1 < pos_v2) ? -1 : 0;
            result[2] = 
                    (pos_v1 > pos_v2) ? val1[2] : (pos_v1 < pos_v2) ? val2[2] : 
                                                  val1[2];
            if (result[0] == 0) {
                int pos2_v1 = ((int)val1[0] - 1700000 - pos_v1) / 14;
                int pos2_v2 = ((int)val2[0] - 1700000 - pos_v2) / 14;
                result[0] = 
                        (pos2_v1 > pos2_v2) ? 1 : (pos2_v1 < pos2_v2) ? -1 : 0;
                result[2] = 
                        (pos2_v1 > pos2_v2) ? val1[2] : (pos2_v1 < pos2_v2) ? 
                                                        val2[2] : val1[2];
            }
        } else if (HandOps.isBasic((int)val1[0]) && 
                   HandOps.isBasic((int)val2[0])) {
            result[0] = (val1[0] > val2[0]) ? 1 : (val1[0] < val2[0]) ? -1 : 0;
            result[2] = 
                    (val1[0] > val2[0]) ? val1[2] : (val1[0] < val2[0]) ? val2[2] : 
                                                    val1[2];
        } else {
            result[0] = (val1[0] > val2[0]) ? 1 : (val1[0] < val2[0]) ? -1 : 0;
            result[2] = 
                    (val1[0] > val2[0]) ? val1[2] : (val1[0] < val2[0]) ? val2[2] : 
                                                    val1[2];
        }
        if (val1[1] == -1 && val2[1] == -1) {
            result[1] = -99;
            result[3] = -99;
        } else if (val1[1] > -1 && val2[1] == -1) {
            result[1] = 1;
            result[3] = val1[3];
        } else if (val1[1] == -1 && val2[1] > -1) {
            result[1] = -1;
            result[3] = val2[3];
        } else if (HandOps.islowHand((int)val1[1]) && 
                   HandOps.islowHand((int)val2[1])) {
            result[1] = (val1[1] > val2[1]) ? -1 : (val1[1] < val2[1]) ? 1 : 0;
            result[3] = 
                    (val1[1] > val2[1]) ? val2[3] : (val1[1] < val2[1]) ? val1[3] : 
                                                    val1[3];
        }
        return result;
    }


    /**
     *
     * @param playerHand
     * @param communityHand
     * @param gameType
     * @return
     */
    public static long[] bestHandOf5(long playerHand, long communityHand, 
                                     int gameType) {
        long result[] = valueOf(playerHand, communityHand, gameType);
        long bestHand[] = new long[2];
        bestHand[0] = -99;
        bestHand[1] = -99;
        if (((gameType & PokerGameType.HOLDEM) == gameType) || 
            ((gameType & PokerGameType.OMAHAHI) == gameType) || 
            ((gameType & PokerGameType.STUDHI) == gameType)) {
            bestHand[0] = result[2];
        } else if (((gameType & PokerGameType.OMAHAHILO) == gameType) || 
                   ((gameType & PokerGameType.STUDHILO) == gameType)) {
            bestHand[0] = result[2];
            bestHand[1] = result[3];
        }

        return bestHand;
    }


    public static long[] valueOf(long ph, long ch, int type) {
        long[] result = new long[4];
        result[0] = -99;
        result[1] = -99;
        result[2] = -99;
        result[3] = -99;

        if (((type & PokerGameType.HOLDEM) == type) || 
            ((type & PokerGameType.STUDHI) == type)) {
            long hands[] = getCombinations(ph | ch, 5);
            long tmp_result = (hands.length > 0) ? hands[0] : 0;
            for (int i = 1; i < hands.length; i++) {
                int compare_result = 
                    HandOps.compareHand(tmp_result, hands[i], true);
                result[0] = 
                        (compare_result >= 0) ? HandOps.valueOfHighHand(tmp_result) : 
                        HandOps.valueOfHighHand(hands[i]);
                tmp_result = (compare_result >= 0) ? tmp_result : hands[i];
                result[2] = tmp_result;
            }
        } else if (((type & PokerGameType.STUDHILO) == type)) {
            long hands[] = getCombinations(ph | ch, 5);
            long tmp_result_high = (hands.length > 0) ? hands[0] : 0;
            long tmp_result_low = (hands.length > 0) ? hands[0] : 0;
            for (int i = 0; i < hands.length; i++) {
                int compare_result = 
                    HandOps.compareHand(tmp_result_high, hands[i], true);
                result[0] = 
                        (compare_result >= 0) ? HandOps.valueOfHighHand(tmp_result_high) : 
                        HandOps.valueOfHighHand(hands[i]);
                tmp_result_high = 
                        (compare_result >= 0) ? tmp_result_high : hands[i];
                result[2] = tmp_result_high;

                compare_result = 
                        HandOps.compareHand(tmp_result_low, hands[i], false);
                result[1] = 
                        (compare_result >= 0) ? HandOps.valueOfLowHand(tmp_result_low) : 
                        HandOps.valueOfLowHand(hands[i]);
                tmp_result_low = 
                        (compare_result >= 0) ? tmp_result_low : hands[i];
                result[3] = tmp_result_low;
            }
        } else if (((type & PokerGameType.OMAHAHI) == type)) {
            long plr_values[] = getCombinations(ph, 2);
            long cm_values[] = getCombinations(ch, 3);
            long hands[] = new long[plr_values.length * cm_values.length];
            int j = 0;
            for (int i = 0; i < plr_values.length; i++) {
                for (int k = 0; k < cm_values.length; k++) {
                    hands[j++] = plr_values[i] | cm_values[k];
                }
            }
            long tmp_result = (hands.length > 0) ? hands[0] : 0;
            for (int i = 1; i < hands.length; i++) {
                int compare_result = 
                    HandOps.compareHand(tmp_result, hands[i], true);
                result[0] = 
                        (compare_result >= 0) ? HandOps.valueOfHighHand(tmp_result) : 
                        HandOps.valueOfHighHand(hands[i]);
                tmp_result = (compare_result >= 0) ? tmp_result : hands[i];
                result[2] = tmp_result;
            }
        } else if (((type & PokerGameType.OMAHAHILO) == type)) {
            long plr_values[] = getCombinations(ph, 2);
            long cm_values[] = getCombinations(ch, 3);
            long hands[] = new long[plr_values.length * cm_values.length];
            int j = 0;
            for (int i = 0; i < plr_values.length; i++) {
                for (int k = 0; k < cm_values.length; k++) {
                    hands[j++] = plr_values[i] | cm_values[k];
                }
            }
            long tmp_result_high = (hands.length > 0) ? hands[0] : 0;
            long tmp_result_low = (hands.length > 0) ? hands[0] : 0;
            for (int i = 0; i < hands.length; i++) {
                int compare_result = 
                    HandOps.compareHand(tmp_result_high, hands[i], true);
                result[0] = 
                        (compare_result >= 0) ? HandOps.valueOfHighHand(tmp_result_high) : 
                        HandOps.valueOfHighHand(hands[i]);
                tmp_result_high = 
                        (compare_result >= 0) ? tmp_result_high : hands[i];
                result[2] = tmp_result_high;

                compare_result = 
                        HandOps.compareHand(tmp_result_low, hands[i], false);
                result[1] = 
                        (compare_result >= 0) ? HandOps.valueOfLowHand(tmp_result_low) : 
                        HandOps.valueOfLowHand(hands[i]);
                tmp_result_low = 
                        (compare_result >= 0) ? tmp_result_low : hands[i];
                result[3] = tmp_result_low;
            }
        }
        //boolean isLow = HandOps.islowHand((int)result[1]);
        //System.out.println("Is low " + isLow);
        //System.out.print("HighValueof="+ Hand.stringValue(result[2]));
        //System.out.println("--------LowValueof="+ (isLow ? Hand.stringValue(result[3]) : "None"));
        //System.out.println("final result[1] = " + result[1]);
        return result;
    }


    private static synchronized long[] getCombinations(long hand, int nos) {
        assert LongOps.getHighs(hand) <= 9 : Hand.stringValue(hand);
        idx = 0;
        int values_size = 1;
        int n = LongOps.getHighs(hand);
        //System.out.println("no of highs = " + n);
        int nos_fact = 1;
        for (int i = n; i > (n - nos); i--) {
            values_size *= i;
        }
        //System.out.println("values_size = " + values_size);
        for (int i = 1; i <= nos; i++) {
            nos_fact *= i;
        }
        //System.out.println("values_size / nos_fact = " + values_size / nos_fact);
        long values[] = new long[(values_size / nos_fact)];
        //long values2[] = new long[ (values_size / nos_fact)];
        //long single_mask[] = new long[LongOps.getHighs(hand)];
        constructArray(0, hand, nos, values);
        return values;
    }

    private static void constructArray(long seed, long value, int nos, 
                                       long[] result) {
        //System.out.println("constructArray()");
        //System.out.println("seed=" + seed + ", value=" + value + ", nos=" + nos);
        long masked_value = (seed ^ value);
        long high_pos_mask = 0x1L << 51;
        int pos;
        long tmp1;
        while (masked_value > 0) {
            //System.out.println("in while");
            pos = LongOps.highCardPos(masked_value);
            tmp1 = (high_pos_mask >>> (52 - pos));
            long tmp_val = seed | tmp1;
            //System.out.println("tmp_val = " + tmp_val);
            if (LongOps.getHighs(tmp_val) == nos) {
                boolean add = true;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] == tmp_val) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    //System.out.println("idx = " + idx);
                    //System.out.println("adding val = " + tmp_val);
                    result[idx++] = tmp_val;
                }
            } else {
                constructArray(tmp_val, value, nos, result);
            }
            masked_value ^= tmp1;
            //System.out.println("masked_value = " + masked_value);
        }
    }

    /**
    private static long[] getCombinations_old(long hand, int nos) {
      //System.out.println("Hand.java::BEGIN - getCombinations()");
      long tmp_hand = 0;
      long high_pos_mask = 0x1L << 51;
      long curr_mask = 0x0L;
      long blank_high = 0x0L;
      long blank_sec_high = 0x0L;
      int values_size = 1;
      int n = LongOps.getHighs(hand);
      //System.out.println("no of highs = " + n);
      int nos_fact = 1;
      for (int i = n; i > (n - nos); i--) {
        values_size *= i;
      }
      //System.out.println("values_size = " + values_size);
      for (int i = 1; i <= nos; i++) {
        nos_fact *= i;
      }
      //System.out.println("values_size / nos_fact = " + values_size / nos_fact);
      long values[] = new long[ (values_size / nos_fact)];
      //long values[] = new long[30000];
      tmp_hand = hand;
      //System.out.println("starting tmp_hand = " + tmp_hand);
      for (int i = 0; i < values.length; i++) {
        curr_mask = 0x0L;
        if (LongOps.getHighs(tmp_hand) == nos) {
          //System.out.println("LongOps.getHighs(tmp_hand) == nos");
          values[i] = tmp_hand;
          //System.out.println("Adding to array = " + tmp_hand);
          if (nos > 2) {
            if ( (LongOps.getHighs(blank_sec_high) + LongOps.getHighs(tmp_hand)) ==
                (LongOps.getHighs(hand) - LongOps.getHighs(blank_high))) {
              blank_high |= (high_pos_mask >>> (52 - Hand.highCardPos(tmp_hand)));
              //System.out.println("blank_high = " + blank_high);
              blank_sec_high = 0x0L;
              //System.out.println("resetting blank_sec_high to 0");
              tmp_hand = (blank_high ^ hand);
              //System.out.println("new tmp_hand = " + tmp_hand);
            }
            else {
              //blank out the 2nd high pos
              tmp_hand = (blank_sec_high ^ (blank_high ^ hand));
              long tmp1 = tmp_hand ^
                  (high_pos_mask >>> (52 - Hand.highCardPos(tmp_hand)));
              long next_sec_blank = (high_pos_mask >>>
                                     (52 - Hand.highCardPos(tmp1)));
              blank_sec_high |= next_sec_blank;
              //System.out.println("blank_sec_high = " + blank_sec_high);
              tmp_hand ^= next_sec_blank;
              //System.out.println("new tmp_hand = " + tmp_hand);
            }
          }
          else {
            //if the no of cards is not more than 2, there is no need for blank_sec_high
            blank_high |= (high_pos_mask >>> (52 - Hand.highCardPos(tmp_hand)));
            tmp_hand = (blank_high ^ hand);
          }
        }
        else {
          for (int k = 0; k < nos; k++) {
            //System.out.println("here");
            int pos = Hand.highCardPos(tmp_hand);
            //System.out.println("pos = " + pos);
            long tmp1 = (high_pos_mask >>> (52 - pos));
            curr_mask |= tmp1;
            //System.out.println("curr_mask = " + curr_mask);
            tmp_hand ^= tmp1;
            //System.out.println("tmp_hand = " + tmp_hand);
          }
          if (LongOps.getHighs(curr_mask) == nos) {
            values[i] = curr_mask;
            //System.out.println("Adding to array = " + curr_mask);
            //mask out the low bit from curr_mask
          }
          int tmp_low_pos = Hand.lowCardPos(curr_mask);
          //creat the new number
          tmp_hand |= (curr_mask ^ (0x1L << (tmp_low_pos - 1)));
          //System.out.println("new tmp_hand = " + tmp_hand);
        }
      }
      return values;
    }**/


}
