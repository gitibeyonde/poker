package com.poker.game.util;

import com.agneya.util.LongOps;
import com.agneya.util.Rng;
import com.agneya.util.Utils;

import com.poker.game.PokerGameType;

import java.util.Calendar;
import java.util.Comparator;
import java.util.logging.Logger;


/*Calcualtes the odds of each hand winning given the hands and gametype */
public class PokerOdds {
  static Logger _cat = Logger.getLogger(PokerOdds.class.getName());

  public OddsPlayer[] getPlayerHands(String p_hands) {
    String cards[] = p_hands.split("\\|");
    int pc = cards.length / 2;
    OddsPlayer[] ph = new OddsPlayer[pc];
    for (int i = 0; i < pc; i++) {
        if (cards[i*2].equals("__")){
            ph[i] = new OddsPlayer(0,"", i, false); 
        }
        else {
          long h = Hand.getHandFromStr2(cards[i * 2] + "," + cards[ (i * 2) + 1]);
          ph[i] = new OddsPlayer(h,"", i, true);
        }
    }
    return ph;
  }

  public static long getCommHand(String chand) {
    return Hand.getHandFromStr2(chand.replace('|', ','));
  }

  public static OddsPlayer[] getProbability(OddsPlayer prob[]) {
    double sum = 0;
    for (int i = 0; i < prob.length; i++) {
      //_cat.finest(" win count " + prob[i]._winCount + ", sum =" + sum);
      sum = sum + prob[i]._winCount;
    }
    for (int i = 0; i < prob.length; i++) {
      //_cat.finest(" win count " + prob[i]._winCount + ", sum =" + sum);
      prob[i]._percent_win = Utils.getRounded( (prob[i]._winCount * 100.00) /
                                              sum);
    }
    // sort the all-in players in ascending order of  their position
    java.util.Arrays.sort(prob, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ( ( (OddsPlayer) o1)._pos - ( (OddsPlayer) o2)._pos);
      }
    });
    return prob;
  }

  public OddsPlayer[] calcProbability(String phands, String chand, int gametype) {
    OddsPlayer[] pH = getPlayerHands(phands);
    long cH;
    if (chand.equals("")) {
      cH = 0;
    }
    else {
      cH = getCommHand(chand);
    }
    _cat.finest(phands);
    _cat.finest(chand);
    for (int i=0;i<50;i++){
        pokerOdds(pH, cH, gametype);
    }
    return getProbability(pH);
  }
  
    public OddsPlayer[] calcProbability(OddsPlayer[] pH, long cH, int gametype) {
      for (int i=0;i<25;i++){
          pokerOdds(pH, cH, gametype);
      }
      return getProbability(pH);
    }

  public static long gettime() {
    return Calendar.getInstance().getTimeInMillis();
  }

  public void pokerOdds(OddsPlayer[] ph, long ch, int gametype) {
    Deck deck = new Deck();
    for (int k = 0; k < ph.length; k++) {
        deck.remove(ph[k]._hand);
    }
    deck.remove(ch);
    
    // put cards in the comm
     int ch_count = LongOps.getHighs(ch);
     //_cat.finest("Drawing comm cards " + (5-ch_count));
     ch |= deck.drawCards(5 - ch_count);
     
     //_cat.finest ("Comm hand=" + new Hand(ch).stringValue());
    
    //deal cards to player if required
     for (int i=0;i<ph.length;i++){
         if (ph[i]._hand == 0){
             ph[i]._dhand = deck.drawCards(2);
             //_cat.finest("Dealt =" + new Hand(ph[i]._dhand).stringValue());
         }
         else {
             ph[i]._dhand = ph[i]._hand;
         }
     }
      findWinners(ph, ch, gametype);
  }

  public void findWinners(OddsPlayer[] v, final long cc, int gt) {
    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) HandComparator.compareGameHand( ( (OddsPlayer) o2)._dhand,
                                      ( (OddsPlayer) o1)._dhand,
                                      cc, PokerGameType.HOLDEM, true)[0];
      }
    });

    double winner_count = 1.00;
    for (int i = 0; i < v.length - 1; i++) {
      if (HandComparator.compareGameHand(v[i]._dhand,
                           v[i + 1]._dhand,
                           cc, PokerGameType.HOLDEM, true)[0] != 0L) {
        break;
      }
      winner_count++;
    }

    double win = 1.00 / winner_count;
    //_cat.finest("win=" + win);
    for (int i = 0; i < winner_count; i++) {
      v[i].incrWinCount(win);
    }
  }

 

  public class Deck {

    protected long _deck;
    protected int _pos = 0;

    public Deck() {
      _deck = Long.valueOf(
          "0000000000001111111111111111111111111111111111111111111111111111", 2).
          longValue();
    }

      protected long drawCards(int count) {
        long hand = 0L;
        int samples = 1000;
        while (count > 0 && samples-- > 0) {
          //_cat.info("Deck size = " + LongOps.getHighs(_deck));
          assert LongOps.getHighs(_deck) >= 1:" 1 cards remaining ";
          //
          int pos = (int) (
              Math.random() * 52) + 1;
          //_rng.nextIntBetween(0, 52); //changed as it blocks here on secure randomw
          long mask = 1L << pos;
          if ((mask & _deck) == 0) {
            ;
          }
          else {
            hand |= mask;
            --count; _deck = _deck & (~mask);
          }
        }
        if (samples <= 0) {
          throw new IllegalStateException("Unable to draw card from " + _deck);
        }
        return hand;
      }

    public void remove(long cards) {
      _deck &= ~cards;
    }

    public int size() {
      return LongOps.getHighs(_deck);
    }

  }

  public static void main(String[] argv) throws Exception {
    long now = System.currentTimeMillis();
    PokerOdds po = new PokerOdds();
    OddsPlayer probs[];
    
    probs = po.calcProbability("2d|3d|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__", "4s|4d|jd", 1); //
    System.out.println(" 53, 47  ");
    for (int i = 0; i < probs.length; i++) {
        System.out.print(probs[i] + ", ");
    }
    
    System.exit(0);
  
  System.out.println(System.currentTimeMillis() - now);
    probs = po.calcProbability("as|2d|9c|tc|ad|2s", "", 1); //
    System.out.println(" 53, 47  ");
    for (int i = 0; i < probs.length; i++) {
      System.out.print(probs[i] + ", ");
    }
    System.out.println(System.currentTimeMillis() - now);

    probs = po.calcProbability("ah|qs|kd|tc", "ad|qd|td", 1); // 52.4 -- 47.6
    System.out.println(" 52.5 , 47.6 ");
    for (int i = 0; i < probs.length; i++) {
      System.out.print(probs[i] + ", ");
    }
    System.out.println(System.currentTimeMillis() - now);

    now = System.currentTimeMillis();
    probs = po.calcProbability("ac|as|8d|8h", "5c|6c|7c", 1); // 76.7 -- 23.3
    System.out.println(" 76.7 , 23.3 ");
    for (int i = 0; i < probs.length; i++) {
      System.out.print(probs[i] + ", ");
    }
    System.out.println(System.currentTimeMillis() - now);

    now = System.currentTimeMillis();
    probs = po.calcProbability("js|jc|6s|6c", "", 1); // 81.6 -- 18.4
    System.out.println(" 81.6 , 18.4 ");
    for (int i = 0; i < probs.length; i++) {
      System.out.print(probs[i] + ", ");
    }
    System.out.println(System.currentTimeMillis() - now);

    now = System.currentTimeMillis();
    probs = po.calcProbability("ac|kc|qd|jd|ah|as|7c|7d|4h|5h", "", 1);
    System.out.println(" 10-11, 18, 37-38, 14-15, 17 ");
    for (int i = 0; i < probs.length; i++) {
      System.out.print(probs[i] + ", ");
    }
    System.out.println(System.currentTimeMillis() - now);

  }

}
