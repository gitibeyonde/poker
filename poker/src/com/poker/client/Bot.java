package com.poker.client;

import com.golconda.game.PlayerStatus;
import com.golconda.game.util.ActionConstants;
import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.golconda.game.util.HandEvaluator;
import com.golconda.message.GameEvent;

import com.poker.common.message.CommandMove;
import com.poker.game.util.CardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;


/**
 *
 */
public class Bot
    implements ActionConstants { //extends ServerPlayer {
  transient static Logger log = Logger.getLogger(Bot.class.getName());

  public static final int NOVICE = 1;
  public static final int EASY = 2;
  public static final int NORMAL = 3;
  public static final int HARD = 4;
  public static final int TOUGH = 5;

  /*
       - BLINDS = small blind and big blind positions
       - EARLY = next two players after BLINDS
      -  MIDDLE = next three players after EARLY
      -  LATE = next two players after MIDDLE
      -  DEALER = one person on dealer button
   This may be tricky to figure out since not all games are 10 players. Maybe figure it out based on which is most important to know in highest to lowest order.  The order is: DEALER, BLINDS, MIDDLE, EARLY, LATE. Something like this could work:
   if(I\uFFFDm dealer button?)
      DEALER;
      else if(I\uFFFDm blinds? )
      BLINDS;
      else if (!enough players to figure out middle, early and late?)
      MIDDLE;
      else if(I\uFFFDm early?)
           EARLY;
            else LATE;
   */

  public static final int BLINDS = 0;
  public static final int EARLY = 1;
  public static final int MIDDLE = 2;
  public static final int LATE = 3;
  public static final int DEALER = 4;

  //private BotProfile botProfile;

  boolean isRaiseOccured = false;
  boolean isRaised = false;
  int raiseCount = 0;
  boolean firstToAct = true;
  boolean firstToActLocked = false;
  double current_pot = 0;

  Cards _cards = null;
  private List _boardCards = new ArrayList();

  int _posType = -1;
  public String _name = null;
  public int _pos = -99;
  public int _joinedGameType = -1;
  public int group=1;
  public double _minBet = 0;
  public double _maxBet = 0;
  public int _maxPlayers = 0;
  public int _minPlayers = 0;
  public int _maxRounds = 0;
  public double _small_blind = 0;
  public double _big_blind = 0;

  public Bot(String name) {
    // for testing! DO NOT REMOVE
    //super(name, null);
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public int getBotPosition() {
    return _pos;
  }

  public void setBotPosition(int botPos) {
    _pos = botPos;
  }

  public Cards getCards() {
    return _cards;
  }

  public int getHandRank() {
    return HandEvaluator.rankHand(getCards());
  }

  public int getBoardRank() {
    return HandEvaluator.rankHand(getBoard());
  }

  public int getBoardAndHandRank() {
    return HandEvaluator.rankHand(getBoardAndHand());
  }

  public int getHandRankValue() {
    return HandEvaluator.nameHandValue(getCards());
  }

  public int getBoardRankValue() {
    return HandEvaluator.nameHandValue(getBoard());
  }

  public int getBoardAndHandRankValue() {
    return HandEvaluator.nameHandValue(getBoardAndHand());
  }

  public Cards getBoard() {
    //List bc = getModel().getBoardCards();
    Cards c = new Cards(false);
    for (Iterator it = _boardCards.iterator(); it.hasNext(); ) {
      Card card = (Card) it.next();
      c.addCard(card);
    }
    return c;
  }

  public Cards getBoardAndHand() {
    //List bc = getModel().getBoardCards();
    Cards h = getCards();
    if (h==null){
      return new Cards(false);
    }
    Cards c = new Cards(getCards());

    for (Iterator it = _boardCards.iterator(); it.hasNext(); ) {
      Card card = (Card) it.next();
      c.addCard(card);
    }
    return c;
  }

  /**
   * Calculates maximum rank if one arbitrary card would be added
   * @return
   */
  public Card getChanceCard(Cards cards) {
    int max = getChanceRank();
    for (int i = 0; i < Card.NUM_SUITS * Card.NUM_RANKS; i++) {
      Card c = new Card(i,false);
      if (!cards.has(c)) {
        cards.addCard(c);
        int rank = HandEvaluator.rankHand(cards);
        if (Math.abs(rank - max) < 6) {
          return c;
        }
        cards.removeCard();
      }
    }
    return null;
  }

  /**
   * Calculates maximum rank if one arbitrary card would be added
   * @return
   */
  public int getChanceRank(Cards cards) {
    int max = getHandRank();
    for (int i = 0; i < Card.NUM_SUITS * Card.NUM_RANKS; i++) {
      Card c = new Card(i,false);
      if (!cards.has(c)) {
        cards.addCard(c);
        int rank = HandEvaluator.rankHand(cards);
        //System.out.println(cards + "= " + rank);
        if (rank > max) {
          max = rank;
        }
        cards.removeCard();
      }
    }
    return max;
  }

  /**
   * Calculates maximum rank if one arbitrary card would be added
   * @return
   */
  public int getChanceRankCount(Cards cards) {
    int max = getChanceRank(cards);
    int count = 0;
    for (int i = 0; i < Card.NUM_SUITS * Card.NUM_RANKS; i++) {
      Card c = new Card(i,false);
      if (!cards.has(c)) {
        cards.addCard(c);
        int rank = HandEvaluator.rankHand(cards);
        if (Math.abs(rank - max) < 6) {
          count++;
        }
        cards.removeCard();
      }
    }
    return count;
  }

  /**
   * Calculates maximum rank if one arbitrary card would be added
   * @return
   */
  public int getChanceRank() {
    return getChanceRank(getCards());
  }

  /**
   * Calculates maximum rank if one arbitrary card would be added.
   * Differs from {@link int getChanceRank() getChanceRank} that takes
   * into consideration the most probable maximum rank.
   * @return
   */
  public int getDistributedChanceRank() {
    Cards cards = getCards();
    TreeMap ranks = new TreeMap();
    int low = Integer.MAX_VALUE, high = Integer.MIN_VALUE;
    for (int i = 0; i < Card.NUM_SUITS * Card.NUM_RANKS; i++) {
      cards.addCard(new Card(i,false));
      int rank = HandEvaluator.rankHand(cards);
      if (rank < low) {
        low = rank;
      }
      if (rank > high) {
        high = rank;
      }
      boolean isNewValue = true;
      for (Iterator it = ranks.keySet().iterator(); it.hasNext(); ) {
        Integer keyObj = (Integer) it.next();
        int key = keyObj.intValue();
        if (rank > key - Card.NUM_RANKS * 2 &&
            rank < key + Card.NUM_RANKS * 2) {
          int value = ( (Integer) ranks.get(keyObj)).intValue();
          ranks.put(keyObj, new Integer(value + 1));
          isNewValue = false;
        }
      }
      if (isNewValue) {
        ranks.put(new Integer(rank), new Integer(1));
      }
      cards.removeCard();
    }
    int maxDepth = 3; // change if needed
    while (true) {
      long rank = 0;
      int count = 0;
      int considered = 0;
      for (Iterator it = ranks.keySet().iterator(); it.hasNext(); ) {
        Integer keyObj = (Integer) it.next();
        int key = keyObj.intValue();
        if (key < low || key > high) {
          continue;
        }
        considered++;
        int value = ( (Integer) ranks.get(keyObj)).intValue();
        rank += key * value;
        count += value;
        //Logger.log(Level.INFO, key + "->" + value);
      }
      int avg = (int) (rank / count);
      if (considered < 4) {
        return avg;
      }
      //Logger.log(Level.INFO, "" + avg + "=" + rank + "/"+ count + "(" + low + "," + high + ")");
      int count1 = 0;
      int count2 = 0;
      for (Iterator it = ranks.keySet().iterator(); it.hasNext(); ) {
        Integer keyObj = (Integer) it.next();
        int key = keyObj.intValue();
        int value = ( (Integer) ranks.get(keyObj)).intValue();
        if (key < avg) {
          count1 += value;
        }
        else {
          count2 += value;
        }
      }
      if (Math.abs(count1 - count2) < 5) {
        return avg;
      }
      if (count1 > count2) {
        high = avg;
      }
      if (count2 > count1) {
        low = avg;
      }
    }
  }

  public Card getSignificantCard(Cards c) {
    Cards without1 = new Cards(c);
    Cards without2 = new Cards(c);
    Cards all = new Cards(c);
    Cards my = getCards();
    for (int i = 0; i < my.size(); i++) {
      Card card = my.getCard(i + 1);
      if (i == 0) {
        without2.addCard(card);
      }
      else {
        without1.addCard(card);
      }
      all.addCard(card);
    }
    //System.out.println("va: " + all + " v1: " + without1 + " v2: " + without2);
    int v1 = HandEvaluator.rankHand(without1);
    int v2 = HandEvaluator.rankHand(without2);
    int va = HandEvaluator.rankHand(all);
    int count = 2;
    Card card = my.getCard(2);
    if (Math.abs(va - v1) <= Card.NUM_RANKS * 2) {
      count--;
    }
    card = my.getCard(1);
    if (Math.abs(va - v2) <= Card.NUM_RANKS * 2) {
      count--;
      //System.out.println(all + "= " + va + " " + without1 + "= " + v1 + " " + without2 + "= " + v2);
    }
    return count == 1 ? card : null;
  }

  public int getSignificantCardCount(Cards c) {
    Cards without1 = new Cards(c);
    Cards without2 = new Cards(c);
    Cards all = new Cards(c);
    Cards my = getCards();
    for (int i = 0; i < my.size(); i++) {
      Card card = my.getCard(i + 1);
      if (i == 0) {
        without2.addCard(card);
      }
      else {
        without1.addCard(card);
      }
      all.addCard(card);
    }
    //System.out.println("va: " + all + " v1: " + without1 + " v2: " + without2);
    int v1 = HandEvaluator.rankHand(without1);
    int v2 = HandEvaluator.rankHand(without2);
    int va = HandEvaluator.rankHand(all);
    int count = 2;
    if (Math.abs(va - v1) <= Card.NUM_RANKS * 2) {
      count--;
    }
    if (Math.abs(va - v2) <= Card.NUM_RANKS * 2) {
      count--;
      //System.out.println(all + "= " + va + " " + without1 + "= " + v1 + " " + without2 + "= " + v2);
    }
    return count;
  }

  public int getPreflopGroup(int level) {
    group = evaluatePreflopGroup();
    if (level == NORMAL) {
      return 1;
    }
    else if (level == HARD) {
      if (group >= 3) {
        return 2;
      }
    }
    return group;
  }

  /*
      Hand Rankings Preflop:
      \uFFFD	Group 1: AA KK
      \uFFFD	Group 2: QQ JJ AKs
      \uFFFD	Group 3: TT AQs AJs KQs AK
      \uFFFD	Group 4: 99 KTs QJs KJs ATs AQ
      \uFFFD	Group 5: A8s KQ 88 QTs A9s AT AJ JTs
      \uFFFD	Group 6: 77 Q9s KJ QJ JTs A7s A6s A5s A4s A3s A2s  J9s T9s K9s KT QT
      \uFFFD	Group 7: 66 J8s 98s T8s 44 J9 43s 75s T9 33 98 64s 22 K8s K7s K6s K5s K4s K3s K2s Q8s 55 87s 97s
      \uFFFD	Group 8: 87 53s A9 Q9 76 42s 32s 96s 85s J8 J7s 65 54 74s K9 T8 76 65s 54s 86s
      \uFFFD	Group 9: A8 A7 A6 A5 A4 A3 A2 K8 K7 Q7 Q6 J7
      \uFFFD	Group 10: All other hands that don\uFFFDt fit in the above categories.
   */
  public int evaluatePreflopGroup() {
    try {
      int rank = HandEvaluator.rankHand(getCards());
      int rankValue = HandEvaluator.nameHandValue(rank);
      int cr1 = getCards().getCard(1).getRank();
      int cr2 = getCards().getCard(2).getRank();
      int maxcr = Math.max(cr1, cr2);
      int mincr = Math.min(cr1, cr2);
      boolean suited = getCards().getCard(1).getSuit() ==
          getCards().getCard(2).getSuit();
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.KING)) {
        return 1;
      }
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.JACK) ||
          (maxcr == Card.ACE && mincr >= Card.KING && suited)) {
        return 2;
      }
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.TEN) ||
          (maxcr == Card.ACE && mincr >= Card.JACK && suited) ||
          (maxcr == Card.ACE && mincr >= Card.KING) ||
          (maxcr == Card.KING && mincr >= Card.QUEEN && suited)) {
        return 3;
      }
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.NINE) ||
          (maxcr == Card.ACE && mincr >= Card.TEN && suited) ||
          (maxcr == Card.ACE && mincr >= Card.QUEEN) ||
          (maxcr == Card.KING && mincr >= Card.TEN && suited) ||
          (maxcr == Card.QUEEN && mincr >= Card.JACK && suited)) {
        return 4;
      }
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.EIGHT) ||
          (maxcr == Card.ACE && mincr >= Card.EIGHT && suited) ||
          (maxcr == Card.ACE && mincr >= Card.TEN) ||
          (maxcr == Card.KING && mincr >= Card.QUEEN) ||
          (maxcr == Card.QUEEN && mincr >= Card.TEN && suited) ||
          (maxcr == Card.JACK && mincr >= Card.TEN && suited)) {
        return 5;
      }
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.SEVEN) ||
          (maxcr == Card.ACE && mincr >= Card.TWO && suited) ||
          (maxcr == Card.KING && mincr >= Card.NINE && suited) ||
          (maxcr == Card.KING && mincr >= Card.TEN) ||
          (maxcr == Card.QUEEN && mincr >= Card.NINE && suited) ||
          (maxcr == Card.QUEEN && mincr >= Card.TEN) ||
          (maxcr == Card.JACK && mincr >= Card.NINE && suited) ||
          (maxcr == Card.TEN && mincr >= Card.NINE && suited)) {
        return 6;
      }
      if ( (rankValue == HandEvaluator.PAIR && cr1 >= Card.TWO) ||
          (maxcr == Card.KING && mincr >= Card.TWO && suited) ||
          (maxcr == Card.QUEEN && mincr >= Card.EIGHT && suited) ||
          (maxcr == Card.JACK && mincr >= Card.EIGHT && suited) ||
          (maxcr == Card.JACK && mincr >= Card.NINE) ||
          (maxcr == Card.TEN && mincr >= Card.EIGHT && suited) ||
          (maxcr == Card.TEN && mincr >= Card.NINE) ||
          (maxcr == Card.NINE && mincr >= Card.SEVEN && suited) ||
          (maxcr == Card.NINE && mincr >= Card.EIGHT) ||
          (maxcr == Card.EIGHT && mincr >= Card.SEVEN && suited) ||
          (maxcr == Card.SEVEN && mincr >= Card.FIVE && suited) ||
          (maxcr == Card.SIX && mincr >= Card.FOUR && suited) ||
          (maxcr == Card.FOUR && mincr >= Card.THREE && suited)) {
        return 7;
      }
      if ( (maxcr == Card.ACE && mincr >= Card.NINE) ||
          (maxcr == Card.KING && mincr >= Card.NINE) ||
          (maxcr == Card.QUEEN && mincr >= Card.NINE) ||
          (maxcr == Card.JACK && mincr >= Card.SEVEN && suited) ||
          (maxcr == Card.JACK && mincr >= Card.EIGHT) ||
          (maxcr == Card.TEN && mincr >= Card.EIGHT) ||
          (maxcr == Card.NINE && mincr >= Card.SIX && suited) ||
          (maxcr == Card.EIGHT && mincr >= Card.FIVE && suited) ||
          (maxcr == Card.EIGHT && mincr >= Card.SEVEN) ||
          (maxcr == Card.SEVEN && mincr >= Card.FOUR && suited) ||
          (maxcr == Card.SEVEN && mincr >= Card.SIX) ||
          (maxcr == Card.SIX && mincr >= Card.FIVE) ||
          (maxcr == Card.FIVE && mincr >= Card.THREE && suited) ||
          (maxcr == Card.FIVE && mincr >= Card.FOUR) ||
          (maxcr == Card.FOUR && mincr >= Card.TWO && suited) ||
          (maxcr == Card.THREE && mincr >= Card.TWO && suited)) {
        return 8;
      }
      if ( (maxcr == Card.ACE && mincr >= Card.TWO) ||
          (maxcr == Card.KING && mincr >= Card.SEVEN) ||
          (maxcr == Card.QUEEN && mincr >= Card.SIX) ||
          (maxcr == Card.JACK && mincr >= Card.SEVEN)) {
        return 9;
      }
    }
    catch (Exception e) {
      //log.info(e.getMessage() +               "at com.agneya.client.bots.Bot.evaluatePreflopGroup");
    }
    return 10;
  }

  public static boolean isCombination(Cards boardAndHand, Cards board,
                                      int combination) {
    return HandEvaluator.nameHandValue(boardAndHand) == combination &&
        HandEvaluator.rankHand(boardAndHand) > HandEvaluator.rankHand(board) &&
        !HandEvaluator.nameHand(boardAndHand).equals(HandEvaluator.nameHand(
            board));
  }

  /*
   Hand Rankings Flop/Turn:
   \uFFFD	Group 1: Nuts
   \uFFFD	Group 2: Straight Flush, Four of a kind
   \uFFFD	Group 3: Full House
   \uFFFD	Group 4: Flush
   \uFFFD	Group 5: Straight
   \uFFFD	Group 6: Three of a Kind/Set/Straight Flush Draw
   \uFFFD	Group 7: Two Pair
   \uFFFD	Group 8: Top Pair/Open Ended Straight Draw/Double Belly Buster Straight Draw/Flush Draw
   \uFFFD	Group 9: Non Top Pair/Two Overcards
   \uFFFD	Group 10: High Card/Gutshot Straight Draw/Backdoor Flush Draw
   */
  public int evaluateFlopTurnGroup() {
    Cards h = getCards(), b = getBoard(), bh = getBoardAndHand();
    int boardRank = getBoardRankValue();
    int rankValue = getBoardAndHandRankValue();
    if (CardUtils.isNuts(b, bh)) {
      return 1;
    }
    if (isCombination(bh, b, HandEvaluator.STRAIGHTFLUSH) ||
        isCombination(bh, b, HandEvaluator.FOURKIND)) {
      return 2;
    }
    if (isCombination(bh, b, HandEvaluator.FULLHOUSE)) {
      return 3;
    }
    if (isCombination(bh, b, HandEvaluator.FLUSH)) {
      return 4;
    }
    if (isCombination(bh, b, HandEvaluator.STRAIGHT)) {
      return 5;
    }
    if (isCombination(bh, b, HandEvaluator.THREEKIND) ||
        CardUtils.isSet(h, b, bh) || CardUtils.isStraightFlushDraw(bh, b)) {
      return 6;
    }
    if (isCombination(bh, b, HandEvaluator.TWOPAIR)) {
      return 7;
    }
    if (CardUtils.isTopPair(bh) && boardRank != HandEvaluator.PAIR) {
      return 8;
    }
    if (CardUtils.isOpenEndedStraightDraw(bh, b) ||
        CardUtils.isDoubleBellyBusterStraightDraw(bh, b) ||
        CardUtils.isFlushDraw(bh, b)) {
      return 9;
    }
    if (rankValue == HandEvaluator.PAIR ||
        CardUtils.getOvercardCount(h, b) == 2) {
      return 10;
    }
    if (CardUtils.isGutshotStraightDraw(bh, b) ||
        CardUtils.isBackdoorFlushDraw(bh, b)) {
      return 11;
    }
    if (h!=null && CardUtils.isHighCard(h, b)) {
      return 12;
    }
    return 13;
  }

  /*
   Hand Rankings River:
   \uFFFD	Group 1: Nuts
   \uFFFD	Group 2: Straight Flush, Four of a kind
   \uFFFD	Group 3: Full House
   \uFFFD	Group 4: Flush
   \uFFFD	Group 5: Straight
   \uFFFD	Group 6: Three of a Kind/Set
   \uFFFD	Group 7: Two Pair
   \uFFFD	Group 8: Top Pair
   \uFFFD	Group 9: Non Top Pair
   \uFFFD	Group 10: High Card
   */
  public int evaluateRiverGroup() {
    Cards h = getCards(), b = getBoard(), bh = getBoardAndHand();
    int boardRank = getBoardRankValue();
    int rankValue = getBoardAndHandRankValue();
    if (CardUtils.isNuts(b, bh)) {
      return 1;
    }
    if (isCombination(bh, b, HandEvaluator.STRAIGHTFLUSH) ||
        isCombination(bh, b, HandEvaluator.FOURKIND)) {
      return 2;
    }
    if (isCombination(bh, b, HandEvaluator.FULLHOUSE)) {
      return 3;
    }
    if (isCombination(bh, b, HandEvaluator.FLUSH)) {
      return 4;
    }
    if (isCombination(bh, b, HandEvaluator.STRAIGHT)) {
      return 5;
    }
    if (isCombination(bh, b, HandEvaluator.THREEKIND) ||
        CardUtils.isSet(h, b, bh)) {
      return 6;
    }
    if (isCombination(bh, b, HandEvaluator.TWOPAIR)) {
      return 7;
    }
    if (CardUtils.isTopPair(bh) && boardRank != HandEvaluator.PAIR) {
      return 8;
    }
    if (rankValue == HandEvaluator.PAIR) {
      return 9;
    }
    if (CardUtils.isHighCard(h, b)) {
      return 10;
    }
    return 13;
  }

  public boolean isException(Cards c) {
    if (c.size() > 4 && isRaiseOccured) {
      int[] cardSuits = new int[Card.NUM_SUITS];
      int[] cardRanks = new int[Card.NUM_RANKS];
      for (int i = 0; i < c.size(); i++) {
        Card card = c.getCard(i + 1);
        cardSuits[card.getSuit()]++;
        cardRanks[card.getRank()]++;
      }
      boolean is4Suit = false;
      boolean is4Kind = false;
      boolean is4Straight = false;
      for (int i = 0; i < Card.NUM_SUITS; i++) {
        if (cardSuits[i] >= 4) {
          is4Suit = true;
          break;
        }
      }
      for (int i = 0; i < Card.NUM_RANKS; i++) {
        if (cardRanks[i] >= 4) {
          is4Kind = true;
          break;
        }
      }
      int s = -1;
      for (int i = 0; i < Card.NUM_RANKS + 2; i++) {
        int j = i >= Card.NUM_RANKS ? i - Card.NUM_RANKS : i;
        if (cardSuits[j] > 0) {
          if (s == -1) {
            s = j;
          }
          else if (j - s + 1 == 4) {
            is4Straight = true;
            break;
          }
        }
        else {
          s = -1;
        }
      }
      Cards pc = getCards();
      int maxRank = Math.max(pc.getCard(1).getRank(), pc.getCard(2).getRank());
      Cards all = new Cards(pc);
      all.addCards(c);
      int value = HandEvaluator.nameHandValue(HandEvaluator.rankHand(all));
      if (is4Suit &&
          value != HandEvaluator.FLUSH &&
          value != HandEvaluator.FULLHOUSE) {
        return true;
      }
      if (is4Kind &&
          maxRank < Card.JACK) {
        return true;
      }
      if (is4Straight &&
          value != HandEvaluator.FLUSH &&
          value != HandEvaluator.FULLHOUSE &&
          value != HandEvaluator.STRAIGHT) {
        return true;
      }
    }
    return false;
  }

  public int getGroup(int level, Cards c) {
    group = evaluateGroup(level, c);
    if (level == NORMAL) {
      if (group <= 4) {
        return 4;
      }
      if (group <= 7) {
        return 7;
      }
      return 0;
    }
    else if (level == HARD) {
      if (group <= 4) {
        return 1;
      }
      if (group <= 7) {
        return 5;
      }
    }
    return group;
  }

  public int evaluateGroup(int level, Cards c) {
    Cards all = new Cards(getCards());
    all.addCards(c);
    int rank = HandEvaluator.rankHand(all);
    int value = HandEvaluator.nameHandValue(rank);
    int valueRank = HandEvaluator.nameHandValueRank(rank);
    int significant = getSignificantCardCount(c);
    Card c1 = getCards().getCard(1);
    Card c2 = getCards().getCard(2);
//if (all.size() > 1) {
// groups 8-9 could be 6-7
    if (value == HandEvaluator.STRAIGHTFLUSH ||
        ( (value == HandEvaluator.FOURKIND ||
           value == HandEvaluator.FIVEKIND) && significant > 0)) {
      return 1;
    }
    if (value == HandEvaluator.FULLHOUSE ||
        ( (value == HandEvaluator.FLUSH ||
           value == HandEvaluator.STRAIGHT) && significant > 1)) {
      return 2;
    }
    if ( (value == HandEvaluator.THREEKIND && significant > 0) ||
        (value == HandEvaluator.TWOPAIR && significant > 1)) {
      return 3;
    }
    if ( (value == HandEvaluator.FLUSH ||
          value == HandEvaluator.STRAIGHT) && significant > 0) {
      return 4;
    }
    if ( (value == HandEvaluator.TWOPAIR && significant > 0) ||
        (value == HandEvaluator.PAIR && valueRank > Card.TEN && significant > 0)) {
      return 5;
    }
    if ( (value == HandEvaluator.PAIR &&
          valueRank > Card.FIVE && valueRank < Card.JACK &&
          significant > 0) ||
        (c1.getRank() > Card.TEN && c2.getRank() > Card.TEN)) {
      return 6;
    }
    if ( (value == HandEvaluator.PAIR &&
          valueRank >= Card.TWO && valueRank < Card.SIX &&
          significant > 0) ||
        (c1.getRank() > Card.JACK || c2.getRank() > Card.JACK)) { // high card Q..A
      return 7;
    }
//}
    int chanceRank = getChanceRank(all);
    int chanceRankCount = getChanceRankCount(all);
    Card chanceCard = getChanceCard(all);
    c.addCard(chanceCard);
    significant = getSignificantCardCount(c);
    Card significantCard = getSignificantCard(c);
//System.out.println(significant + " " + significantCard);
//System.out.println(chanceRank + " " + HandEvaluator.nameHandValue(chanceRank));
    if ( (HandEvaluator.nameHandValue(chanceRank) == HandEvaluator.STRAIGHT &&
          chanceRankCount > 4) ||
        (HandEvaluator.nameHandValue(chanceRank) == HandEvaluator.FLUSH &&
         significant > 1)) {
      return 8;
    }
    if ( (HandEvaluator.nameHandValue(chanceRank) == HandEvaluator.STRAIGHT &&
          chanceRankCount < 5) ||
        (HandEvaluator.nameHandValue(chanceRank) == HandEvaluator.FLUSH &&
         significant > 0 && significantCard.getRank() > Card.NINE)) {
      return 9;
    }
    return 0;
  }

  public int getProbableTurn(double p1, double p2, int a1, int a2, int a3) {
    double r = Math.random();
    if (r > p1) {
      if (r > p2) {
        return a3;
      }
      else {
        return a2;
      }
    }
    else {
      return a1;
    }
  }

  public int getProbableTurn(double p1, int a1, int a2) {
    if (p1 >= 1) {
      return a1;
    }
    if (Math.random() > p1) {
      return a2;
    }
    else {
      return a1;
    }
  }

  public boolean isProbable(double p) {
    return Math.random() > p;
  }

  /*
       public void actionPerformed(Action a) {
      //
      int id = a.getId();
      isRaiseOccured =
              id == BET ||
              id == RAISE;
      if (isRaiseOccured) raiseCount++;
      if (!firstToActLocked &&
              (isRaiseOccured ||
              id == ActionConstants.CHECK ||
              id == CALL ||
              id == FOLD ||
              id == ALLIN))
          firstToAct = false;
      if (id == NEW_HAND ||
              id == FLOP ||
              id == TURN ||
              id == RIVER) {
          isRaiseOccured = false;
          isRaised = false;
          raiseCount = 0;
          firstToAct = true;
          firstToActLocked = false;
      }
      if (id == BET_REQUEST) {
          BotManager.getInstance().addMessage(getTableTopic(), issueAction((BetRequestAction) a));
          isRaiseOccured = false;
          firstToActLocked = true;
      }
       }
   */
  //StringBuilder logString;

  /**
   * Main method for all level bots
   * @param level
   * @param a
   * @return
   */

  public synchronized String issueAction(int level, GameEvent ge) { //BetRequestAction a) {
    //////log.finest("issueAction(int level, GameEvent ge) :: START " + ge);
    if (_pos < 0) {
      return null;
    }
    //int stage = getModel().getStage();
    int stage = -1;
    Card bot_hand[] = ge.getHand();
    Card comm_hand[] = ge.getCommunityCards();
    //log.finest("_joinedGameType = " + _joinedGameType);
    /**
     * If Game type not holdem use random moves
     */
    /**
     * GAME TYPE HOLDEM
     */

    int commCardCount = comm_hand == null ? 0 : comm_hand.length;
    //log.finest("commCardCount = " + commCardCount);
    switch (commCardCount) {
      case 0:
        //log.finest("stage is PREFLOP");
        stage = PREFLOP;
        break;
      case 3:
        //log.finest("stage is FLOP");
        stage = FLOP;
        break;
      case 4:
        //log.finest("stage is TURN");
        stage = TURN;
        break;
      case 5:
        //log.finest("stage is RIVER");
        stage = RIVER;
        break;
      default:
        //log.finest("stage is UNKNOWN");
        stage = -1;
        break;
    }

    if (stage == -1) {
      log.warning("stage is UNKNOWN , something wrong......");
      return null;
    }

    String pd[][] = ge.getPlayerDetails();
    String pots[][] = ge.getPot();
    double bot_worth = 0, bot_bet = 0, total_pot = 0;

    if (bot_hand != null) {
      Card crds[] = bot_hand;
      if (_cards == null) {
        _cards = new Cards(false);
      }
      else {
        _cards.clear();
      }
      for (int i = 0; i < crds.length; i++) {
        _cards.addCard(crds[i]);
      }
    //System.out.println("Cards=" + _cards.stringValue());
    }
    if (comm_hand != null) {
      Card c_crds[] = comm_hand;
      if (_boardCards == null) {
        _boardCards = new ArrayList();
      }
      else {
        _boardCards.clear();
      }
      for (int i = 0; i < c_crds.length; i++) {
        _boardCards.add(c_crds[i]);
        //System.out.println("BoardCards=" + c_crds[i]);
      }
    }

    int activePlayers = 0;
    for (int i = 0; i < pd.length; i++) {
      if (Integer.parseInt(pd[i][0]) == _pos) {
        bot_worth = Double.parseDouble(pd[i][1]);
        bot_bet = Double.parseDouble(pd[i][2]);
      }
      if (PlayerStatus.isActive(Long.parseLong(pd[i][4]))) {
        activePlayers++;
      }
    }
    //log.finest("activePlayers = " + activePlayers);
    //log.finest("bot_worth = " + bot_worth);
    //log.finest("bot_bet = " + bot_bet);
    //log.finest("current_pot = " + current_pot);
    //log.finest("total_pot = " + total_pot);
    if (pots != null) {
      for (int i = 0; i < pots.length; i++) {
        total_pot += Double.parseDouble(pots[i][1]);
      }
    }
    //set-check control flags....
    if (current_pot != total_pot) {
      isRaiseOccured = false;
      raiseCount = 0;
      firstToAct = true;
      current_pot = total_pot;
    }
    //log.finest("total_pot = " + total_pot);

    int action = issueAction(level, stage, ge.getMove(), _maxBet, total_pot,
                             activePlayers);
    //log.finest("action = " + action);
    boolean isThere = false;
    String moves[][] = ge.getMove();
    String amt = null;
    int mv;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call") && action == CALL) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_CALL;
      }
      else if (moves[i][1].equals("check") && (action == CHECK || action == FOLD)) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_CHECK;
      }
      else if (moves[i][1].equals("bet") && action == BET) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_BET;
      }
      else if (moves[i][1].equals("raise") && action == RAISE) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_RAISE;
      }
      else if (moves[i][1].equals("fold") && action == FOLD) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_FOLD;
      }
      else if (moves[i][1].equals("big-blind") && action == BIG_BLIND) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_BIGBLIND;
      }
      else if (moves[i][1].equals("small-blind") && action == SMALL_BLIND) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_SMALLBLIND;
      }
      else if (moves[i][1].equals("sbbb") && action == SB_BB) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_SBBB;
      }
      else if (moves[i][1].equals("bringin") && action == BRINGIN) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_BRING_IN;
      }
    
      else if (moves[i][1].equals("wait") && action == WAIT) {
        isThere = true;
        return null;
      }
      if (isThere) {
        //log.finest("issueAction(int level, GameEvent ge) :: END and move is "+moves[i][1]);
        return moves[i][1];
      }
    }
    //moves did not match....so do some goodals....
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("check") && action == CALL) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_CHECK;
      }
      else if (moves[i][1].equals("call") && action == CHECK) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_CALL;
      }
      else if (moves[i][1].equals("bet") && action == RAISE) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_BET;
      }
      else if (moves[i][1].equals("raise") && action == BET) {
        isThere = true;
        amt = moves[i][2];
        mv = CommandMove.M_RAISE;
      }
      if (isThere) {
        //log.finest("issueAction(int level, GameEvent ge) :: END MOVE IS "+moves[i][1]);
        if (moves[i][1].equals("raise") || moves[i][1].equals("bet")) {
          isRaiseOccured = true;
          raiseCount++;
        }
        return moves[i][1];
      }
    }
    if (!isThere) {
      //log.finest(          "SHOULD NOT COME HERE action = " + action +", GE is "+ge);
      if (moves[0][1].equals("raise") || moves[0][1].equals("bet")) {
        isRaiseOccured = true;
        raiseCount++;
      }
      return moves[0][1];
    }

    log.warning(
        "return NULL; (CANNOT COME HERE)- issueAction(int level, GameEvent ge) :: END");
    return null;
    /*
             // allowable actions from bet request
             int[] actions = a.getActions();
             for (int i = 0; i < actions.length; i++)
        switch (actions[i]) {
            case ActionConstants.CHECK:
                actions[i] = CALL;
                break;
            case BET:
                actions[i] = RAISE;
                break;
        }
      boolean isThere = false;
      if (actions != null)
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == action) {
                isThere = true;
                break;
            }
        }
             if (!isThere) {
        if (action == RAISE) action = CALL;
        if (action == CALL) action = FOLD;
             }
     logString.append(getName() + " action2 = " + Action.actionToString(action));

             int amount = a.getCall();
             if (action == FOLD && amount == 0) action = CALL;
             if (action == RAISE) amount += a.getMinBet();
             //Logger.log("bot_bet", amount + " > " + bankRoll + " ? " + amountIn);
             if (amount > bankRoll) {
        action = ALLIN;
        amount = bankRoll;
             }
             logString.append(getName() + " action3 = " + Action.actionToString(action) + "\n\n");
             if (SharedConstants.IS_LOGGED)
        Logger.log("ai", logString.toString());
             isRaised = action == RAISE;
     BettingAction response = new BettingAction(action, BOARD_TARGET, amount);
             response.setGuid(a.getGuid());
             return response;
     */
  }

  public void setPositionType(String pd[][], final int dealerPos, int playerPos) {
    if (dealerPos == playerPos) {
      _posType = com.poker.client.Bot.DEALER;
      return;
    }
    Vector activePlayers = new Vector();
    for (int i = 0; i < pd.length; i++) {
      if (PlayerStatus.isActive(Long.parseLong(pd[i][4]))) {
        activePlayers.add(pd[i]);
      }
    }
    if (activePlayers.size() == 0) {
      return;
    }
    String[][] av = (String[][]) activePlayers.toArray(new String[activePlayers.size()][6]);

    Arrays.sort(av, new Comparator() {
      public int compare(Object o1, Object o2) {
        String[] av1 = (String[]) o1;
        String[] av2 = (String[]) o2;
        int result = Integer.parseInt(av1[0]) - Integer.parseInt(av2[0]) + dealerPos;
        //System.out.println(av1[3] + ", " + av1[0] + ":" + av2[3] + ", "  + av2[0] + "=" + result);
        return result;
      }
    });

    int player_pos_idx = -1;

    for (int j = 0; j < av.length; j++) {
      if (Integer.parseInt(av[j][0]) == playerPos) {
        player_pos_idx = j;
      }
    }

    firstToAct = false;
    if (dealerPos == playerPos) {
      _posType = com.poker.client.Bot.DEALER;
    }
    else if (player_pos_idx <= 2) {
      _posType = com.poker.client.Bot.BLINDS;
      if (player_pos_idx == 1) {
        firstToAct = true;
      }
    }
    else if (player_pos_idx >= 7) {
      _posType = com.poker.client.Bot.LATE;
    }
    else if (player_pos_idx >= 5) {
      _posType = com.poker.client.Bot.MIDDLE;
    }
    else if (player_pos_idx >= 2) {
      _posType = com.poker.client.Bot.EARLY;
    }
  }

  public int getPositionType() {
    /*
      switch (getType()) {
          case PLAYER_BUTTON: return DEALER;
          case PLAYER_SMALL_BLIND: case PLAYER_BIG_BLIND: return BLINDS;
          case PLAYER_EARLY: return EARLY;
          case PLAYER_LATE: return LATE;
      }
      return MIDDLE;
     */
    return _posType;
  }

  public int issueAction(int level, int stage, String moves[][], double bet,
                         double pot, int pc) {
    /*
             int group = stage == PREFLOP ? evaluatePreflopGroup() :
            (stage == RIVER ? evaluateRiverGroup() : evaluateFlopTurnGroup());
             int amountToCall = getController().getAmountToCall(this);
             int action = amountToCall == 0 && stage != PREFLOP ? ActionConstants.CHECKED : FOLD;
     double callOffset = (double) amountToCall / getController().getTable().getBet();
     double potOdds = (double) getController().getTable().getPot() / amountToCall;
             int callOffsetMax = 0;
             int raiseMax = 0;
             boolean condition = false;
             int position = getPosition();
             int playersIn = getModel().getPlayingPlayerCount();
             int moneyPlayersIn = getModel().getMoneyPlayerCount();
     */
    /*
      Cards c = getCards();
      if (c == null || c.size() < 2) {
          //log.finest("Bot cards is null");
          //log.finest("issueAction(int level, int stage, String moves[][], double bet, double pot, int pc) :: END");
          return FOLD;
      }
     */
    switch (level) {
      case NOVICE:
        switch (stage) {
          case PREFLOP:
            return issueFishPreflopAction(moves, bet);
          case FLOP:
            return issueFishFlopAction(moves, bet);
          case TURN:
            return issueFishTurnAction(moves, bet);
          case RIVER:
            return issueFishRiverAction(moves, bet);
        }
      case EASY:
        switch (stage) {
          case PREFLOP:
            return issueCallingPreflopAction(moves, bet);
          case FLOP:
            return issueCallingFlopAction(moves, bet, pc);
          case TURN:
            return issueCallingTurnAction(moves, bet);
          case RIVER:
            return issueCallingRiverAction(moves, bet, pc);
        }
      case NORMAL:
        switch (stage) {
          case PREFLOP:
            return issueLooseAggressivePreflopAction(moves, bet, pc);
          case FLOP:
            return issueLooseAggressiveFlopAction(moves, bet, pc);
          case TURN:
            return issueLooseAggressiveTurnAction(moves, bet, pc);
          case RIVER:
            return issueLooseAggressiveRiverAction(moves, bet, pc);
        }
      case HARD:
        switch (stage) {
          case PREFLOP:
            return issueRockPreflopAction(moves, bet, pc);
          case FLOP:
            return issueRockFlopAction(moves, bet, pot);
          case TURN:
            return issueRockTurnAction(moves, bet, pot);
          case RIVER:
            return issueRockRiverAction(moves, bet, pot);
        }
      case TOUGH:
        switch (stage) {
          case PREFLOP:
            return issueWinningPreflopAction(moves, bet, pc);
          case FLOP:
            return issueWinningFlopAction(moves, bet, pot);
          case TURN:
            return issueWinningTurnAction(moves, bet, pot);
          case RIVER:
            return issueWinningRiverAction(moves, bet, pc);
        }
    }
    return 0;
  }

  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // Following methods in one class because it's just
  // comfortable to edit them in one place.
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  /**
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	3
   Group3:	CALL	4	-
   Group4:	CALL	4	-
   Group5:	CALL	4	-
   Group6:	CALL	2	-
   Group7:	CALL	2	-
   Group8:	CALL	2	-
   Group9:	CALL	2	-
   Group10:	CALL	.5	-
   Examples of how the above should be transformed into code:
   Group1: if(raise_number < 4) raise; else call;
   Group2: if(raise_number < 3) raise; else call;
   Group10: if(call_offset <= .5) call; else fold;
   */
  public int issueFishPreflopAction(String moves[][], double bet) {
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    group = evaluatePreflopGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    int action = FOLD;
    double callOffset = amountToCall / bet;
    int callOffsetMax = 0;
    int raiseMax = 0;
    //log.finest(getName() + " FISH PREFLOP: GROUP=" + group + " CALL OFFS=" +              callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 5) {
      if (group == 1) {
        raiseMax = 4;
      }
      else if (group == 2) {
        raiseMax = 3;
      }
      callOffsetMax = 4;
    }
    else if (group <= 9) {
      callOffsetMax = 2;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    else if (group == 10) {
      if (callOffset <= 0.5) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	RAISE	4	3
   Group5:	RAISE	4	2
   Group6:	RAISE	4	2
   Group7:	RAISE	4	2
   Group8:	RAISE	4	1
   Group9:	CALL	4	-
   Group10:	CALL	2	-
   Group11:	CALL	2	-
   Group12:	CALL	1	-
   Examples of how the above should be transformed into code:
   Group8: if(raise_number < 1) raise; else call;
   Group10: if(call_offset <= 2) call; else fold;
   */
  public int issueFishFlopAction(String moves[][], double bet) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;
    int callOffsetMax = 0;
    int raiseMax = 0;
    //log.finest(getName() + " FISH FLOP: GROUP=" + group + " CALL OFFS=" +
              //callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 9) {
      if (group <= 3) {
        raiseMax = 4;
      }
      else if (group == 4) {
        raiseMax = 3;
      }
      else if (group <= 7) {
        raiseMax = 2;
      }
      else if (group == 8) {
        raiseMax = 1;
      }
      callOffsetMax = 4;
    }
    else if (group >= 9 && group <= 10) {
      callOffsetMax = 2;
    }
    else if (group == 12) {
      callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	if(fullhouse_possible){ if(call_offset <= 2) call; else fold;
   }else { if(raise_number < 2) raise; else call; }
   Group5:	if(fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call; }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 3) raise; else call; }
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call; }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number == 0) raise; else call; }
   Group9:	CALL	4	-
   Group10:	CALL	2	-
   Group11:	CALL	2	-
   Group12:	CALL	1	-
   Examples of how the above should be transformed into code:
   Group3: if(raise_number < 4) raise; else call;
   Group11: if(call_offset <=2) call; else fold;
   Group12: if(call_offset <=1) call; else fold;
   */
  public int issueFishTurnAction(String moves[][], double bet) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }

    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //log.finest(getName() + " FISH TURN: GROUP=" + group + " CALL OFFS=" +              callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 3) {
      raiseMax = 4;
      callOffsetMax = 4;
    }
    else if (group >= 4 && group <= 8) {
      if (group == 6) {
        raiseMax = 3;
      }
      else if (group == 8) {
        raiseMax = 1;
      }
      else {
        raiseMax = 2;
      }
      callOffsetMax = 2;
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      callOffsetMax = 4;
    }
    else if (group >= 10 && group <= 11) {
      callOffsetMax = 2;
    }
    else if (group == 12) {
      callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 4 && group <= 8) {
      if (condition) {
        if (callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        action = raiseCount < raiseMax ? RAISE : CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	if(fullhouse_possible){ if(call_offset <= 3) call; else fold;
   }else{ if(raise_number < 2) raise; else call; }
   Group5:	if(fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call; }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 3) raise; else call; }
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call; }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number == 0) raise; else call; }
   Group9:	CALL	2	-
   Group10:	CHECKED	-	-
   Examples of how the above should be transformed into code:
   Group9: if(call_offset <=2) call; else fold;
   Group10: fold; // means they would attempt a check if possible but fold if someone bet.
   */
  public int issueFishRiverAction(String moves[][], double bet) {
    group = evaluateRiverGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    double callOffset = amountToCall / bet;

    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //log.finest(getName() + " FISH RIVER: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 3) {
      raiseMax = 4;
      callOffsetMax = 4;
    }
    else if (group >= 4 && group <= 8) {
      if (group == 6) {
        raiseMax = 3;
      }
      else if (group == 8) {
        raiseMax = 1;
      }
      else {
        raiseMax = 2;
      }
      if (group == 4) {
        callOffsetMax = 3;
      }
      else {
        callOffsetMax = 2;
      }
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      callOffsetMax = 2;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 4 && group <= 8) {
      if (condition) {
        if (callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        action = raiseCount < raiseMax ? RAISE : CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	3
   Group3:	RAISE	4	2
   Group4:	CALL	4	-
   Group5:	CALL	3	-
   Group6:	CALL	3	-
   Group7:	CALL	2	-
   Group8:	CALL	1	-
   Group9:	CALL	1	-
   Group10:	CALL	.5	-
   */
  public int issueCallingPreflopAction(String moves[][], double bet) {
    group = evaluatePreflopGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    int action = FOLD;
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    //log.finest(getName() + " CALLING PREFLOP: GROUP=" + group + " CALL OFFS=" +              callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 4) {
      if (group == 1) {
        raiseMax = 4;
      }
      else if (group == 2) {
        raiseMax = 3;
      }
      else if (group == 3) {
        raiseMax = 2;
      }
      callOffsetMax = 4;
    }
    else if (group >= 5 && group <= 6) {
      callOffsetMax = 3;
    }
    else if (group == 7) {
      callOffsetMax = 2;
    }
    else if (group >= 8 && group <= 9) {
      callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    else if (group == 10) {
      if (callOffset <= 0.5) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	3
   Group4:	RAISE	4	2
   Group5:	RAISE	4	2
   Group6:	//  check/call 50% of the time and bet 50% of the time from positions BLINDS, EARLY
// raise 50% of the time and call 50% of the time from other positions
   if(my_position == BLINDS || my_position == EARLY){if(Random Distribution 50% && raise_number < 4) raise; else call;
   }else{if(Random Distribution 50% && raise_number < 4) raise; else call;}
   Group7:	RAISE	4	2
   Group8:	RAISE	4	2
   Group9:	// call always, raise 50% of the time up to two bets if there more then 4 people in
   if(players_in > 4){
       if(Random Distribution 50%){if(raise_number < 2) raise; else call;
       }else{call;}
   }else{ call;}
   Group10:	CALL	1	1
   Group11:	// call 50% of the time, fold 50% of the time if there are more then 3 players
   if(players_in > 3){
       if(call_offset <= 2){ Distribution (call 50%/fold 50%)}
       else{fold;}
   }else{fold;}
   * remember fold means try checking then fold if someone bets.
   Group12:	// call 50% of the time fold 50% of the time
   if(Random Distribution (50%) && call_offset <= 1){call;}
   else{ fold;}
   */
  public int issueCallingFlopAction(String moves[][], double bet, int pc) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    //int playersIn = getModel().getPlayingPlayerCount();
    int playersIn = pc;
    //log.finest(getName() + " CALLING FLOP: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 8) {
      if (group <= 2) {
        raiseMax = 4;
      }
      else if (group == 3) {
        raiseMax = 3;
      }
      else if (group >= 4) {
        raiseMax = 2;
      }
      callOffsetMax = 4;
    }
    else if (group == 9) {
      raiseMax = 2;
    }
    else if (group == 10) {
      raiseMax = 1;
      callOffsetMax = 1;
    }
    else if (group == 11) {
      callOffsetMax = 2;
    }
    else if (group == 12) {
      callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group == 6) {
      action = isProbable(0.5) && raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (group == 9) {
      if (playersIn > 4) {
        if (isProbable(0.5)) {
          action = raiseCount < raiseMax ? RAISE : CALL;
        }
        else {
          action = CALL;
        }
      }
      else {
        action = CALL;
      }
    }
    else if (group >= 11 && group <= 12) {
      if (playersIn > 3) {
        if (callOffset <= callOffsetMax && isProbable(0.5)) {
          action = CALL;
        }
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	3	2
   Group4:	if(fullhouse_possible){ if(call_offset <= 2) call; else fold;
   }else{if(raise_number < 2) raise; else call;}
   Group5:	if(fullhouse_possible || flush_possible){if(call_offset <= 2) call; else fold;
   }else{if(raise_number < 2) raise; else call;}
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 3) raise; else call;}
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call;}
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 1) call; else fold;
   }else{ if(raise_number == 0) raise; else call;}
   Group9:	CALL	2	-
   Group10:	CALL	1	-
   Group11:	CHECKED	-	-
   Group12:	CHECKED	 - 	-
   */
  public int issueCallingTurnAction(String moves[][], double bet) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //log.finest(getName() + " CALLING TURN: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 2) {
      raiseMax = 4;
      callOffsetMax = 4;
    }
    else if (group >= 3 && group <= 8) {
      if (group == 6) {
        raiseMax = 3;
      }
      else if (group == 8) {
        raiseMax = 1;
      }
      else {
        raiseMax = 2;
      }
      if (group == 3) {
        callOffsetMax = 3;
      }
      else if (group == 8) {
        callOffsetMax = 1;
      }
      else {
        callOffsetMax = 2;
      }
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      callOffsetMax = 2;
    }
    else if (group == 10) {
      callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 4 && group <= 8) {
      if (condition) {
        if (callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        action = raiseCount < raiseMax ? RAISE : CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	2
   Group3:	RAISE	3	2
   Group4:	if(fullhouse_possible){ if(call_offset <= 2) call; else fold;
   }else{if(raise_number < 2) raise; else call;}
   Group5:	if(fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call;}
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 3) raise; else call;}
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 2) call; else fold;
   }else{ if(raise_number < 2) raise; else call;}
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){ if(call_offset <= 1) call; else fold;
   }else{ if(raise_number == 0) raise; else call;}
   Group9:	if(players_in == 2){
       if(raise_number <= 1){ Distribution (call 50%/fold 50%)
       }else{fold;}
   }else{fold;}
   * remember fold means try checking then fold if someone bets.
   Group10:	CHECKED	-	-
   */
  public int issueCallingRiverAction(String moves[][], double bet, int pc) {
    group = evaluateRiverGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = -1;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //int playersIn = getModel().getPlayingPlayerCount();
    int playersIn = pc;
    //log.finest(getName() + " CALLING RIVER: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 2) {
      raiseMax = group == 1 ? 4 : 2;
      callOffsetMax = 4;
    }
    else if (group >= 3 && group <= 8) {
      if (group == 6) {
        raiseMax = 3;
      }
      else if (group == 8) {
        raiseMax = 1;
      }
      else {
        raiseMax = 2;
      }
      if (group == 3) {
        callOffsetMax = 3;
      }
      else if (group == 8) {
        callOffsetMax = 1;
      }
      else {
        callOffsetMax = 2;
      }
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      callOffsetMax = 2;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 4 && group <= 8) {
      if (condition) {
        if (callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        action = raiseCount < raiseMax ? RAISE : CALL;
      }
    }
    else if (group == 9) {
      if (playersIn == 2 && raiseCount <= raiseMax && isProbable(0.5)) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	RAISE	4	4
   Group5:	RAISE	4	4
   Group6:	RAISE	4	4
   Group7:	RAISE	4	4
   Group8:	// raise in late position if only one non blind player has called so far
   if(my_position == DEALER || my_position == LATE){
     if(money_players_in <=3 && raise_number <= 1) raise; else fold;
   }else{
// call in blinds if price is right
     if(my_position == BLINDS && call_offset <= 1) call; else fold;
   }
   Group9:	// raise in late position if only one non blind player has called so far
   if(my_position == DEALER || my_position == LATE){
     if(money_players_in <=3 && raise_number <= 1) raise; else fold;
   }else{
// call in blinds if price is right
     if(my_position == BLINDS && call_offset <= 1) call; else fold;
   }
   Group10:	// raise in late position if only one non blind player has called so far
   if(my_position == DEALER || my_position == LATE){
     if(money_players_in <=3 && raise_number <= 1) raise; else fold;
   }else{
// call in blinds if price is right
     if(my_position == BLINDS && call_offset <= .5) call; else fold;
   }
   */
  public int issueLooseAggressivePreflopAction(String moves[][], double bet,
                                               int pc) {
    group = evaluatePreflopGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    int action = FOLD;
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    int position = getPositionType();
    int moneyPlayersIn = pc;
    //log.debug(getName() + " AGGRESS PREFLOP: GROUP=" + group + " CALL OFFS=" + callOffset + " RAISE CNT=" + raiseCount);
    //log.finest(getName() + " AGGRESS PREFLOP: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 7) {
      raiseMax = callOffsetMax = 4;
    }
    else if (group >= 8 && group <= 10) {
      raiseMax = callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 8 && group <= 10) {
      if (position == DEALER || position == LATE) {
        if (moneyPlayersIn <= 3 && raiseCount <= raiseMax) {
          action = RAISE;
        }
      }
      else {
        if (group == 10) {
          if (position == BLINDS && callOffset <= 0.5) {
            action = CALL;
          }
        }
        else if (position == BLINDS && callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	RAISE	4	4
   Group5:	RAISE	4	4
   Group6:	RAISE	4	4
   Group7:	RAISE	4	4
   Group8:	RAISE	4	4
   Group9:	RAISE	4	4
   Group10:	if(straight_possible || fullhouse_possible || flush_possible){
       // bet 50% of the time from late/dealer pos if no one bets
       // if they check raise he folds
       if(my_position != DEALER && my_position != LATE) fold; break;
       if(Random 50% && raise_number == 0) raise; else fold;
   }else{
       // raise with low pair 50% of the time if 1 bet or less, other 50% calls.
       // if they check raise he call will their raise
       // won\uFFFDt call more then 2 bets total
       if(Random 50% && raise_number <= 1) raise;
       else if(call_offset <= 1 && raise_number <= 2) call;
       else fold;
   }
   Group11:	// fold if straight, flush or fullhouse possible
// if more then 3 players in, raise 50% of the time fold 50%
   if(straight_possible || fullhouse_possible || flush_possible) fold; break;
   if(players_in > 3 && Random 50%) raise; else fold;
   Group12:	// always bluff in dealer pos if no one bets.
// call 2 bets total max if they check raise.
// don\uFFFDt call a normal bet, only call a check raise if I first bet (max 2 total bets).
   if(my_position != DEALER) fold; break;
   if(raise_number == 0)raise;
   else if(raise_number ==2 && call_offset == 1) call;
   else fold;
   */
  public int issueLooseAggressiveFlopAction(String moves[][], double bet,
                                            int pc) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    int position = getPositionType();
    int playersIn = pc;
    //log.finest(getName() + " AGGRESS FLOP: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 9) {
      raiseMax = callOffsetMax = 4;
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group == 10) {
      if (isStraightPossible() || isFullHousePossible() || isFlushPossible()) {
        if ( (position == DEALER || position == LATE) && isProbable(0.5) &&
            raiseCount == 0) {
          action = RAISE;
        }
      }
      else {
        if (isProbable(0.5) && raiseCount <= 1) {
          action = RAISE;
        }
        else if (callOffset <= 1 && raiseCount <= 2) {
          action = CALL;
        }
      }
    }
    else if (group == 11) {
      if (!isStraightPossible() && !isFullHousePossible() && !isFlushPossible()) {
        if (playersIn > 3 && isProbable(0.5)) {
          action = RAISE;
        }
      }
    }
    else if (group == 12) {
      if (position == DEALER) {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (raiseCount == 2 && callOffset == 1) {
          action = CALL;
        }
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	if(fullhouse_possible){
       // raise if 1 or less bets. if 2+ bets call in increments of 1 only
       if(raise_number <= 1) raise; else if(call_offset <= 1) call; else fold;
   }else{
       // raise if 2 or less bets. if 3+ bets, call in increments of 1
       if(raise_number <= 2) raise; else if(call_offset <= 1) call; else fold;
   }
   Group5:	if(fullhouse_possible || flush_possible){
       // raise if 1 or less bets. if 2+ bets call in increments of 1 only
       if(raise_number <= 1) raise; else if(call_offset <= 1) call; else fold;
   }else{
       // raise if 2 or less bets. if 3+ bets, call in increments of 1
       if(raise_number <= 2) raise; else if(call_offset <= 1) call; else fold;
   }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){
       // caps it 50% of time, otherwise calls any amount
       if(Random 50% && raise_number <= 3) raise; else call;
   }else{
       // caps it, else calls any amount
       if(raise_number <= 3) raise; else call;
   }
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){
       // caps it 50% of time, otherwise calls any amount
       if(Random 50% && raise_number <= 3) raise; else call;
   }else{
       // caps it, else calls any amount
       if(raise_number <= 3) raise; else call;
   }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){
       // raises if 1 bet or less, calls above that in increments of 1 only
       if(raise_number <= 1) raise; else if(call_offset <= 1)call; else fold;
   }else{
       // raises if 1 bet or less, calls any amount above that
       if(raise_number <= 1)raise; else call;
   }
   Group9:	if(fullhouse_possible){
       if(raise_number <=1) raise; else call;
   }else{
       if(raise_number <= 3) raise; else call;
   }
   Group10:	if(straight_possible || fullhouse_possible || flush_possible){
       // bet 50% of the time from late/dealer pos if no one bets
       // if they check raise he folds
       if(my_position != DEALER && my_position != LATE) fold; break;
       if(Random 50% && raise_number == 0) raise; else fold;
   }else{
       // bet if no one has, call 2 bets max, in increments of 1
       if(raise_number == 0) raise;
       else if(call_offset <= 1 && raise_number <= 2) call;
       else fold;
   }
   Group11:	// bluff 50% of time if no one bets, if they check raise he calls max 2 bets
   if(my_position != DEALER && my_position != LATE) fold; break;
   if(Random 50% && raise_number == 0) raise;
   else if(call_offset == 1 && raise_number == 2) call;
   else fold;
   Group12:	// bluff 50% of time if no one bets, if they check raise he folds
   if(my_position != DEALER && my_position != LATE) fold; break;
   if(Random 50% && raise_number == 0) raise; else fold;
   */
  public int issueLooseAggressiveTurnAction(String moves[][], double bet,
                                            int pc) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    int position = getPositionType();
    //log.finest(getName() + " AGGRESS TURN: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group >= 1 && group <= 3) {
      raiseMax = callOffsetMax = 4;
    }
    else if (group == 4) {
      condition = isFullHousePossible();
    }
    else if (group == 5) {
      condition = isFullHousePossible() || isFlushPossible();
    }
    else if (group >= 6 && group <= 10) {
      condition = isStraightPossible() || isFullHousePossible() ||
          isFlushPossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 4 && group <= 5) {
      if (condition) {
        if (raiseCount <= 1) {
          action = RAISE;
        }
        else if (callOffset <= 1) {
          action = CALL;
        }
      }
      else {
        if (raiseCount <= 2) {
          action = RAISE;
        }
        else if (callOffset <= 1) {
          action = CALL;
        }
      }
    }
    else if (group >= 6 && group <= 7) {
      if (condition) {
        if (isProbable(0.5) && raiseCount <= 3) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
      }
      else {
        if (raiseCount <= 3) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
      }
    }
    else if (group == 8) {
      if (condition) {
        if (raiseCount <= 1) {
          action = RAISE;
        }
        else if (callOffset <= 1) {
          action = CALL;
        }
      }
      else {
        if (raiseCount <= 1) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
      }
    }
    else if (group == 9) {
      if (isFullHousePossible()) {
        action = raiseCount <= 1 ? RAISE : CALL;
      }
      else {
        action = raiseCount <= 3 ? RAISE : CALL;
      }
    }
    else if (group == 10) {
      if (condition) {
        if ( (position == DEALER || position == LATE) && isProbable(0.5) &&
            raiseCount == 0) {
          action = RAISE;
        }
      }
      else {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (callOffset <= 1 && raiseCount <= 2) {
          action = CALL;
        }
      }
    }
    else if (group == 11) {
      if (position == DEALER || position == LATE) {
        if (isProbable(0.5) && raiseCount == 0) {
          action = RAISE;
        }
        else if (callOffset == 1 && raiseCount == 2) {
          action = CALL;
        }
      }
    }
    else if (group == 12) {
      if ( (position == DEALER || position == LATE) && isProbable(0.5) &&
          raiseCount == 0) {
        action = RAISE;
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	if(fullhouse_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 2) call;
       else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group5:	if(fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets,
       // increments of 1)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 2) call;
       else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 2) call;
       else fold;

   }else{
       if(raise_number <= 3) raise; else call;
   }
   Group7:	if(straight || fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number <= 3) raise; else call;
   }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets,
       // increments of 1)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number == 0) raise; else call;
   }
   Group9:	// always bet/call up to 2 bets with only 2 people in
   if(players_in == 2 && raise _number == 0) {
      raise; break;
   }else{
      call; break;
   }
// always bet in dealer position if no bets yet, and call a check raise, but not a regular bet
   if(raise_number == 0 && my_position == DEALER) raise;
   else if(raise_number == 2 && call_offset <= 1) call;
   else fold;
   Group10:	// 20% of time totally bluff headsup on the end\uFFFD
// bet in dealer position if no bets yet, 2 players in, and don\uFFFDt call a check raise or regular bet
   if(raise_number == 0 && players_in == 2 && my_position == DEALER && Random 20%) raise; else fold;
   */
  public int issueLooseAggressiveRiverAction(String moves[][], double bet,
                                             int pc) {
    group = evaluateRiverGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    int position = getPositionType();
    int playersIn = pc;
    //log.finest(getName() + " AGGRESS RIVER: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group >= 1 && group <= 3) {
      raiseMax = callOffsetMax = 4;
    }
    else if (group == 4) {
      condition = isFullHousePossible();
    }
    else if (group == 5) {
      condition = isFullHousePossible() || isFlushPossible();
    }
    else if (group >= 6 && group <= 10) {
      if (group == 6) {
        callOffset = 2;
      }
      else if (group == 7) {
        callOffset = 1;
      }
      condition = isStraightPossible() || isFullHousePossible() ||
          isFlushPossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group >= 4 && group <= 5) {
      if (condition) {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (raiseCount <= 2 && callOffset <= 2) {
          action = CALL;
        }
      }
      else {
        if (raiseCount < 2) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
      }
    }
    else if (group >= 6 && group <= 7) {
      if (condition) {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (raiseCount <= 2 && callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        if (raiseCount <= 3) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
      }
    }
    else if (group == 8) {
      if (condition) {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (raiseCount <= 2 && callOffset <= 1) {
          action = CALL;
        }
      }
      else {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
      }
    }
    else if (group == 9) {
      if (playersIn == 2) {
        action = raiseCount == 0 ? RAISE : CALL;
      }
      else {
        if (raiseCount == 0 && position == DEALER) {
          action = RAISE;
        }
        else if (raiseCount == 2 && callOffset <= 1) {
          action = CALL;
        }
      }
    }
    else if (group == 10) {
      if (raiseCount == 0 && playersIn == 2 && position == DEALER &&
          isProbable(0.2)) {
        action = RAISE;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	// raise 50% of the time in late position if no one has made it 2 bets yet
   if(my_position == LATE || my_position == DEALER){
      if(raise_number < 2 && Random Distribution 50%) raise;
      else if(call_offset <= 3) call; else fold;
   }else{ if(call_offset <= 3) call; else fold;}
   Group4:	// if in late position for 2 or less bets
   if(my_position == LATE || my_position == BLINDS || my_position == DEALER){
       if(call_offset <= 2){ call;}
       else{fold;}
   }else{ fold;}
   Group5:	// call in late position or out of blinds call offset 1
   if(my_position == LATE || my_position == BLINDS || my_position == DEALER){
       if(call_offset <= 1){ call;}
       else{ fold;}
   }else{ fold;}
   * remember fold means try checking then fold if someone bets.
   Group6:	// call in late position or out of blinds call offset 1 if more then 4 players are in already with money
   if(my_position == LATE || my_position == BLINDS || my_position == DEALER){
       if(call_offset <= 1 && money_players_in >= 4){ call;}
       else{ fold;}
   }else{fold;}
   Group7:	CALL	0	-
   Group8:	CALL	0	-
   Group9:	CALL	0	-
   Group10:	CALL	0	-
   Examples of how the above should be transformed into code:
   Group10: if(call_offset <=0) call; else fold; // only call if it doesn\uFFFDt cost anymore (aka big blind no raise)
   */
  public int issueRockPreflopAction(String moves[][], double bet, int pc) {
    group = evaluatePreflopGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    int action = FOLD;
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    int position = getPositionType();
    int moneyPlayersIn = pc;
    //log.finest(getName() + " ROCK PREFLOP: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 2) {
      raiseMax = 4;
      callOffsetMax = 4;
    }
    else if (group == 3) {
      raiseMax = 2;
      callOffsetMax = 3;
    }
    else if (group == 4) {
      callOffsetMax = 2;
    }
    else if (group >= 5 && group <= 6) {
      callOffsetMax = 1;
      /* generate action */
    }
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group == 3) {
      if (position == LATE || position == DEALER) {
        if (raiseCount < raiseMax && isProbable(0.5)) {
          action = RAISE;
        }
        else {
          action = callOffset <= callOffsetMax ? CALL : ActionConstants.CHECK;
        }
      }
      else {
        action = callOffset <= callOffsetMax ? CALL : ActionConstants.CHECK;
      }
    }
    else if (group >= 4 && group <= 5) {
      if (position == LATE || position == DEALER || position == BLINDS) {
        action = callOffset <= callOffsetMax ? CALL : ActionConstants.CHECK;
      }
      else {
        action = ActionConstants.CHECK;
      }
    }
    else if (group == 6) {
      if (position == LATE || position == DEALER || position == BLINDS) {
        action = callOffset <= callOffsetMax && moneyPlayersIn >= 4 ? CALL :
            ActionConstants.CHECK;
      }
      else {
        action = ActionConstants.CHECK;
      }
    }
    if (action == ActionConstants.CHECK && amountToCall > 0) {
      action = FOLD;
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	//  check/call 50% of the time and bet 50% of the time from positions BLINDS, EARLY
// raise 50% of the time and call 50% of the time from other positions
   if(my_position == BLINDS || my_position == EARLY){
      if(Random Distribution 50% && raise_number < 4) raise; else call;
   }else{
     if(Random Distribution 50% && raise_number < 4) raise; else call;
   }
   Group2:	//  check/call 50% of the time and bet 50% of the time from positions BLINDS, EARLY
// raise 50% of the time and call 50% of the time from other positions
   if(my_position == BLINDS || my_position == EARLY){
      if(Random Distribution 50% && raise_number < 4) raise; else call;
   }else{
     if(Random Distribution 50% && raise_number < 4) raise; else call;
   }
   Group3:	RAISE	3	3
   Group4:	if(fullhouse_possible){
       if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group5:	if(fullhouse_possible || flush_possible){
       if(call_offset <= 2) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 2) call; else fold;
   }else{
       if(raise_number < 3) raise; else call;
   }
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number <= 1) raise; else call;
   }
   Group9:	// don\uFFFDt draw flush/straight if fullhouse already possible
   if(fullhouse_possible){
       fold;
   }else{
       if(pot_odds > 4) call; else fold;
   }
   Group10:	CHECKED	-	-
   Group11:	CHECKED	-	-
   Group12:	CHECKED	-	-
   */
  public int issueRockFlopAction(String moves[][], double bet, double pot) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    //double potOdds = (double) getController().getTable().getPot() / amountToCall;
    double potOdds = pot / amountToCall;
    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //log.finest(getName() + " ROCK FLOP: GROUP=" + group + " CALL OFFS=" +
           //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 2) {
      raiseMax = 4;
    }
    else if (group == 3) {
      raiseMax = callOffsetMax = 3;
    }
    else if (group >= 4 && group <= 8) {
      if (group == 6) {
        raiseMax = 3;
      }
      else if (group == 8) {
        raiseMax = 1;
      }
      else {
        raiseMax = 2;
      }
      if (group >= 5 && group <= 6) {
        callOffsetMax = 2;
      }
      else {
        callOffsetMax = 1;
      }
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      condition = isFullHousePossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group <= 2) {
      action = isProbable(0.5) && raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (group >= 4 && group <= 8) {
      if (condition) {
        if (callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        action = raiseCount < raiseMax ? RAISE : CALL;
      }
    }
    else if (group == 9) {
      if (!condition && potOdds > 4) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	3	2
   Group4:	if(fullhouse_possible){
       if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number < 2) raise; else if(call_offset <= 2) call; else fold;
   }
   Group5:	if(fullhouse_possible || flush_possible){
       if(call_offset <= 2) call; else fold;
   }else{
       if(raise_number < 2) raise; else if(call_offset <= 2) call; else fold;
   }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 2) call; else fold;
   }else{
       if(raise_number < 3) raise; else call;
   }
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 1 && raise_number <= 2) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){
       // call only if it is 2 bets or less and in increments of 1
       if(call_offset <= 1 && raise_number <= 2) call; else fold;
   }else{
       // bet if no one has yet
       if(raise_number == 0)
   raise;
       // call only if it is 2 bets or less and in increments of 1
       else if(call_offset <= 1 && raise_number <= 2)
   call;
       else fold;
   }
   Group9:	// don\uFFFDt draw flush/straight if fullhouse already possible
   if(fullhouse_possible){
       fold; // check/fold
   }else{
       if(pot_odds > 4) call; else fold;
   }
   Group10:	CHECKED	-	-
   Group11:	CHECKED	-	-
   Group12:	CHECKED	-	-
   */
  public int issueRockTurnAction(String moves[][], double bet, double pot) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    double potOdds = pot / amountToCall;
    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //log.finest(getName() + " ROCK TURN: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 2) {
      raiseMax = 4;
    }
    else if (group == 3) {
      raiseMax = 2;
      callOffsetMax = 3;
    }
    else if (group >= 4 && group <= 8) {
      if (group == 6) {
        raiseMax = 3;
      }
      else {
        raiseMax = 2;
      }
      if (group >= 5 && group <= 6) {
        callOffsetMax = 2;
      }
      else {
        callOffsetMax = 1;
      }
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      condition = isFullHousePossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group <= 2) {
      action = isProbable(0.5) && raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (group >= 4 && group <= 7) {
      if (condition) {
        if (callOffset <= callOffsetMax) {
          action = CALL;
        }
      }
      else {
        if (raiseCount < raiseMax) {
          action = RAISE;
        }
        else {
          if (group >= 4 && group <= 5 && callOffset <= 2) {
            action = CALL;
          }
          else {
            action = CALL;
          }
        }
      }
    }
    else if (group == 8) {
      if (condition) {
        if (callOffset <= callOffsetMax && raiseCount <= raiseMax) {
          action = CALL;
        }
      }
      else {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (callOffset <= callOffsetMax && raiseCount <= raiseMax) {
          action = CALL;
        }
      }
    }
    else if (group == 9) {
      if (!condition && potOdds > 4) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	2
   Group3:	RAISE	3	2
   Group4:	if(fullhouse_possible){
       if(call_offset <= 1 && raise_number <=2) call; else fold;
   }else{
       if(raise_number < 2) raise; else if(call_offset <= 2) call; else fold;
   }
   Group5:	if(fullhouse_possible || flush_possible){
       if(call_offset <= 1 && raise_number <=2) call; else fold;
   }else{
       if(raise_number < 2) raise; else if(call_offset <= 2) call; else fold;
   }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group7:	if(straight_possible || fullhouse_possible || flush_possible){
       if(call_offset <= 1 && raise_number <= 2) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){
       // call only if it is 2 bets or less and in increments of 1
       if(call_offset <= 1 && raise_number <= 2) call; else fold;
   }else{
       // bet if no one has yet
       if(raise_number == 0)
   raise;
       // call only if it is 2 bets or less and in increments of 1
       else if(call_offset <= 1 && raise_number <= 2)
   call;
       else fold;
   }

   Group9:	CHECKED	-	-
   Group10:	CHECKED	-	-
   */
  public int issueRockRiverAction(String moves[][], double bet, double pot) {
    group = evaluateRiverGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    double potOdds = pot / amountToCall;
    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    //log.finest(getName() + " ROCK RIVER: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group <= 2) {
      raiseMax = 4;
      if (group == 2) {
        raiseMax = 2;
      }
    }
    else if (group == 3) {
      raiseMax = 2;
      callOffsetMax = 3;
    }
    else if (group >= 4 && group <= 8) {
      raiseMax = 2;
      callOffsetMax = 1;
      if (group == 4) {
        condition = isFullHousePossible();
      }
      else if (group == 5) {
        condition = isFullHousePossible() || isFlushPossible();
      }
      else {
        condition = isFullHousePossible() || isFlushPossible() ||
            isStraightPossible();
      }
    }
    else if (group == 9) {
      condition = isFullHousePossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    if (group <= 2) {
      action = isProbable(0.5) && raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (group >= 4 && group <= 7) {
      if (condition) {
        if (group == 6) {
          if (callOffset <= callOffsetMax) {
            action = CALL;
          }
        }
        else {
          if (callOffset <= callOffsetMax && raiseCount <= raiseMax) {
            action = CALL;
          }
        }
      }
      else {
        if (raiseCount < raiseMax) {
          action = RAISE;
        }
        else {
          if (group >= 4 && group <= 5 && callOffset <= 2) {
            action = CALL;
          }
          else {
            action = CALL;
          }
        }
      }
    }
    else if (group == 8) {
      if (condition) {
        if (callOffset <= callOffsetMax && raiseCount <= raiseMax) {
          action = CALL;
        }
      }
      else {
        if (raiseCount == 0) {
          action = RAISE;
        }
        else if (callOffset <= callOffsetMax && raiseCount <= raiseMax) {
          action = CALL;
        }
      }
    }
    else if (group == 9) {
      if (!condition && potOdds > 4) {
        action = CALL;
      }
    }
    return action;
  }

  /*
   Option	Call Offset 	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	// always raise to 3 bets, if 3 bets already raise 50% of time/call 50%
   if(raise_number <= 2) raise;
   else if(raise_number == 3 && Random 50%) raise;
   else call;
   Group4:	// raise in dealer/late position if no one has yet
   if(my_position == DEALER || my_position == LATE){
    if(raise_number < 2){ raise; break; }
   }
// call up to 2 bets, to call 3+ bets 5 people have to be in already
   if(call_offset <= 2)call;
   else if(call_offset >=3 && money_players_in >= 5) call;
   else fold;
   Group5:	// raise to 2 bets in dealer position 50% of the time if no one has yet
   if(my_position == DEALER && Random 50% && raise_number < 2){ raise; break; }
// raise to 2 bets in late position 50% of the time if no one has yet
   if(my_position == LATE && Random 50% && raise_number < 2){ raise; break; }
// call 1 bet at a time, call 2 bets if 6+ people are in
   if(call_offset <= 1) call;
   else if(call_offset >= 2 && money_players_in >= 6) call;
   else fold;
   Group6:	// if 6+ players in and I\uFFFDm on button, raise to 2 bets 50% of the time
   if(money_players_in >=6 && raise_number < 2 && my_position == DEALER && Random 50%){
     raise;
     break;
   }
// call 1 bet increments if 6+ players, or if im in blind or dealer positions in 1 bet increments only
   if(money_players_in >= 6 && call_offset <= 1) call;
   else if(my_position == DEALER && call_offset <= 1) call;
   else if(my_position == BLINDS && call_offset <= 1) call;
   else fold;
   Group7:	// if 7+ players in and I\uFFFDm on button, raise to 2 bets 50% of the time
   if(money_players_in >=7 && raise_number < 2 && my_position == DEALER && Random 50%){
     raise;
     break;
   }
// call 1 bet increments if 6+ players, or if im in blind or dealer positions in 1 bet increments only
   if(money_players_in >= 6 && call_offset <= 1) call;
   else if(my_position == DEALER && call_offset <= 1) call;
   else if(my_position == BLINDS && call_offset <= 1) call;
   else fold;
   Group8:	// call in 1 bet increments if 7+ players and im in blinds position
   if(money_players_in >= 7 && call_offset <= 1 && my_position == BLINDS) call;
   else fold;
   Group9:	CHECKED	-	-
   Group10:	CHECKED	-	-
   */
  public int issueWinningPreflopAction(String moves[][], double bet, int pc) {
    group = evaluatePreflopGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);
    int action = FOLD;
    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    int position = getPositionType();
    int moneyPlayersIn = pc;
    //log.finest(getName() + " WINNING PREFLOP: GROUP=" + group + " CALL OFFS=" +
           //   callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group >= 1 && group <= 2) {
      raiseMax = callOffsetMax = 4;
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    switch (group) {
      case 3:
        if (raiseCount <= 2) {
          action = RAISE;
        }
        else if (raiseCount == 3 && isProbable(0.5)) {
          action = RAISE;
        }
        else {
          action = CALL;
        }
        break;
      case 4:
        if (position == DEALER || position == LATE) {
          if (raiseCount < 2) {
            action = RAISE;
          }
        }
        else {
          if (callOffset <= 2) {
            action = CALL;
          }
          else if (callOffset >= 3 && moneyPlayersIn >= 5) {
            action = CALL;
          }
        }
        break;
      case 5:
        if ( (position == DEALER || position == LATE) && isProbable(0.5) &&
            raiseCount < 2) {
          action = RAISE;
        }
        else {
          if (callOffset <= 1) {
            action = CALL;
          }
          else if (callOffset >= 2 && moneyPlayersIn >= 6) {
            action = CALL;
          }
        }
        break;
      case 6:
      case 7:
        if (moneyPlayersIn >= group && raiseCount < 2 && position == DEALER &&
            isProbable(0.5)) {
          action = RAISE;
        }
        else {
          if (moneyPlayersIn >= 6 && callOffset <= 1) {
            action = CALL;
          }
          else if ( (position == DEALER || position == BLINDS) &&
                   callOffset <= 1) {
            action = CALL;
          }
        }
        break;
      case 8:
        if (moneyPlayersIn >= 7 && callOffset <= 1 && position == BLINDS) {
          action = CALL;
        }
        break;
      case 9:
      case 10:
        if (amountToCall == 0) {
          action = ActionConstants.CHECK;
        }
        break;
    }
    //log.finest(getName() + " WINNING PREFLOP: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount +" and action is "+action);
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	4
   Group4:	RAISE	4	3
   Group5:
// fullhouse isn\uFFFDt possible if I have a straight on flop	// raise to 1 bet if no one has bet yet, call in increments of 1
   if(flush_possible){
       if(raise_number == 0) raise;
       else if(call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group6:	// raise to 2 bets max, call in increments of 1
   if(straight_possible || fullhouse_possible || flush_possible){
       if(raise_number <= 1) raise;
       else if(call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number <= 3) raise; else call;
   }
   Group7:	// I have 2 pair and the board is paired. Call 1 bet increments, max 2 bets.
   if(fullhouse_possible && call_offset <= 1 && raise_number <= 2){
     call;
     break;
   }else{
     fold;
     break;
   }
// raise to 1 bet if no one has bet yet, call in increments of 1
   if(straight_possible || flush_possible){
       if(raise_number == 0) raise;
       else if(call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number < 3) raise; else call;
   }
   Group8:
// full house not possible if I have top pair (it would be 2 pair then)	// bet if no one has, call in increments of 1
   if(straight_possible || flush_possible){
       if(raise_number == 0) raise;
       else if(call_offset <= 1) call;
       else fold;
   }else{
       // raises/bets if 1 bet, calls 2 bet increments
       if(raise_number <= 1) raise;
       else if(call_offset <=2) call;
       else fold;
   }
   Group9:	// fullhouse may be out there already, so only draw in increments of 1
   if(fullhouse_possible){
       if(call_offset <= 1) call; else fold;
   }else{
      // in late/dealer position raise 50% of time to 2 bets max
      if(my_position == DEALER || my_position == LATE){
         if(Random 50% && raise_number <= 1){
            raise;
         }
      // raise to 1 bet if no one has yet 50% of the time
      }else if(Random 50% && raise_number == 0){
         raise;
      // call if pot odds are good
      }else if(pot_odds >= 3){
         call;
      }else {
         fold;
      }
   }
   Group10:
   Full house not possible if I have a pair, it would be 2 pair then.	// if I\uFFFDm getting good pot odds call if no flush/straight possible
   if(!straight_possible && !flush_possible && pot_odds >= 5)call; else fold;
   Group11:	// fold if straight, flush or fullhouse possible
// if pot odds great call 50% of the time
   if(straight_possible || fullhouse_possible || flush_possible) fold; break;
   if(pot_odds >= 6 && Random 50%) call; else fold;
   Group12:	CHECKED	-	-
   */
  public int issueWinningFlopAction(String moves[][], double bet, double pot) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    double potOdds = pot / amountToCall;
    int callOffsetMax = 0;
    int raiseMax = 0;
    int position = getPositionType();
    //log.finest(getName() + " WINNING FLOP: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group >= 1 && group <= 4) {
      callOffset = 4;
      raiseMax = group == 4 ? 3 : 4;
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    switch (group) {
      case 5:
        if (isFlushPossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (raiseCount < 2) {
            action = RAISE;
          }
          else {
            action = CALL;
          }
        }
        break;
      case 6:
        if (isStraightPossible() || isFullHousePossible() || isFlushPossible()) {
          if (raiseCount <= 1) {
            action = RAISE;
          }
          else if (callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (raiseCount <= 3) {
            action = RAISE;
          }
          else {
            action = CALL;
          }
        }
        break;
      case 7:
        if (isFullHousePossible()) {
          if (callOffset <= 1 && raiseCount <= 2) {
            action = CALL;
          }
        }
        else {
          if (isStraightPossible() || isFlushPossible()) {
            if (raiseCount == 0) {
              action = RAISE;
            }
            else if (callOffset <= 1) {
              action = CALL;
            }
          }
          else {
            if (raiseCount < 3) {
              action = RAISE;
            }
            else {
              action = CALL;
            }
          }
        }
        break;
      case 8:
        if (isStraightPossible() || isFlushPossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (raiseCount <= 1) {
            action = RAISE;
          }
          else if (callOffset <= 2) {
            action = CALL;
          }
        }
        break;
      case 9:
        if (isFullHousePossible()) {
          if (callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (position == DEALER || position == LATE) {
            if (isProbable(0.5) && raiseCount <= 1) {
              action = RAISE;
            }
          }
          else if (isProbable(0.5) && raiseCount == 0) {
            action = RAISE;
          }
          else if (potOdds >= 3) {
            action = CALL;
          }
        }
        break;
      case 10:
        if (!isStraightPossible() && !isFlushPossible() && potOdds >= 5) {
          action = CALL;
        }
        break;
      case 11:
        if (!isStraightPossible() && !isFullHousePossible() && !isFlushPossible()) {
          if (potOdds >= 6 && isProbable(0.5)) {
            action = CALL;
          }
        }
        break;
      case 12:
        if (amountToCall == 0) {
          action = ActionConstants.CHECK;
        }
        break;
    }
    //log.finest(getName() + " WINNING FLOP: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount +" and action is "+action);

    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	3
   Group4:	if(fullhouse_possible){
       // raise to 1 bet. if 1+ bets call in increments of 1 only
       if(raise_number == 0) raise; else if(call_offset <= 1) call; else fold;
   }else{
       // raise if 1 or less bets. if 2+ bets, call in increments of 1
       if(raise_number <= 1) raise; else if(call_offset <= 1) call; else fold;
   }
   Group5:	// raise to 1 bet if no one has bet yet, call in increments of 1
   if(flush_possible || fullhouse_possible){
       if(raise_number == 0) raise; else if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group6:	// raise to 1 bets max, call in increments of 1
   if(straight_possible || fullhouse_possible || flush_possible){
       if(raise_number == 0) raise; else if(call_offset <= 1) call; else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group7:	// I have 2 pair and the board is paired. Call 1 bet total 50% of the time
   if(fullhouse_possible && raise_number <= 1 && Random 50%){
     call;
     break;
   }else{
     fold;
     break;
   }
// raise to 1 bet if no one has bet yet, call in increments of 1
   if(straight_possible || flush_possible){
       if(raise_number == 0) raise;
       else if(call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group8:	// bet if no one has, call in increments of 1, max 2 bets
   if(straight_possible || flush_possible){
       if(raise_number == 0) raise;
       else if(call_offset <= 1 && raise_number <= 2) call;
       else fold;
   }else{
       // bet if no one has, call in increments of 1, max 2 bets
       if(raise_number == 0) raise;
       else if(call_offset <= 1 && raise_number <= 2) call;
       else fold;
   }
   Group9:	// fullhouse may be out there already, so call in increments of 1, max 2 bets
   if(fullhouse_possible){
       if(call_offset <= 1 && raise_number <= 2) call; else fold;
   }else{
      // in late/dealer position raise 20% of time to 2 bets max
      if(my_position == DEALER || my_position == LATE){
         if(Random 20% && raise_number <= 1){
            raise;
         }
      // raise to 1 bet if no one has yet 50% of the time
      }else if(Random 50% && raise_number == 0){
         raise;
      // call if pot odds are good
      }else if(pot_odds >= 3){
         call;
      }else {
         fold;
      }
   }
   Group10:	// if I\uFFFDm getting good pot odds call if no flush/straight possible
   if(!straight_possible && !flush_possible && pot_odds >= 6)call; else fold;
   Group11:	// fold if straight, flush or fullhouse possible
// if pot odds great call 50% of the time
   if(straight_possible || fullhouse_possible || flush_possible) fold; break;
   if(pot_odds >= 11 && Random 50%) call; else fold;
   Group12:	CHECKED	-	-
   */
  public int issueWinningTurnAction(String moves[][], double bet, double pot) {
    group = evaluateFlopTurnGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;
    double callOffset = amountToCall / bet;

    double potOdds = pot / amountToCall;
    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    int position = getPositionType();
    //log.finest(getName() + " WINNING TURN: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group >= 1 && group <= 3) {
      raiseMax = group == 3 ? 3 : 4;
      callOffset = 4;
    }
    if (group == 5) {
      condition = isFullHousePossible() || isFlushPossible();
    }
    else if (group == 6) {
      condition = isStraightPossible() || isFullHousePossible() ||
          isFlushPossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    switch (group) {
      case 4:
        if (isFullHousePossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (raiseCount <= 1) {
            action = RAISE;
          }
          else if (callOffset <= 1) {
            action = CALL;
          }
        }
        break;
      case 5:
      case 6:
        if (condition) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (raiseCount < 2) {
            action = RAISE;
          }
          else {
            action = CALL;
          }
        }
        break;
      case 7:
        if (isFullHousePossible()) {
          if (raiseCount <= 1 && isProbable(0.5)) {
            action = CALL;
          }
        }
        else {
          if (isStraightPossible() || isFlushPossible()) {
            if (raiseCount == 0) {
              action = RAISE;
            }
            else if (callOffset <= 1) {
              action = CALL;
            }
          }
          else {
            if (raiseCount < 2) {
              action = RAISE;
            }
            else {
              action = CALL;
            }
          }
        }
        break;
      case 8:
        if (isStraightPossible() || isFlushPossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1 && raiseCount <= 2) {
            action = CALL;
          }
        }
        else {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1 && raiseCount <= 2) {
            action = CALL;
          }
        }
        break;
      case 9:
        if (isFullHousePossible()) {
          if (callOffset <= 1 && raiseCount <= 2) {
            action = CALL;
          }
        }
        else {
          if (position == DEALER || position == LATE) {
            if (isProbable(0.2) && raiseCount <= 1) {
              action = RAISE;
            }
          }
          else if (isProbable(0.5) && raiseCount == 0) {
            action = RAISE;
          }
          else if (potOdds >= 3) {
            action = CALL;
          }
        }
        break;
      case 10:
        if (!isStraightPossible() && !isFlushPossible() && potOdds >= 6) {
          action = CALL;
        }
        break;
      case 11:
        if (!isStraightPossible() && !isFullHousePossible() && !isFlushPossible()) {
          if (potOdds >= 11 && isProbable(0.5)) {
            action = CALL;
          }
        }
        break;
      case 12:
        if (amountToCall == 0) {
          action = ActionConstants.CHECK;
        }
        break;
    }
    //log.finest(getName() + " WINNING TURN: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount +" and action is "+action);
    return action;
  }

  /*
   Option	Call Offset	Raise Max
   Group1:	RAISE	4	4
   Group2:	RAISE	4	4
   Group3:	RAISE	4	3
   Group4:	if(fullhouse_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 2) call;
       else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group5:	if(fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets,
       // increments of 1)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 2) call;
       else fold;
   }else{
       if(raise_number < 2) raise; else call;
   }
   Group6:	if(straight_possible || fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 2) call;
       else fold;

   }else{
       if(raise_number <= 3) raise; else call;
   }
   Group7:	if(straight || fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 1) call;
       else fold;
   }else{
       if(raise_number <= 3) raise; else call;
   }
   Group8:	if(straight_possible || fullhouse_possible || flush_possible){
       // bet if no one has yet, call if I bet and they raise (max 2 bets,
       // increments of 1)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 1) call;
       else fold;
   }else{
       // bet if no one has yet, call if I bet and they raise (max 2 bets)
       if(raise_number == 0) raise;
       else if(raise_number <= 2 && call_offset <= 1) call;
       else fold;
   }
   Group9:	// call one bet if it is just 2 players in (heads up)
   if(players_in == 2 && call_offset <= 1) call; else fold;
   Group10:	CHECKED	-	-
   */
  public int issueWinningRiverAction(String moves[][], double bet, int pc) {
    group = evaluateRiverGroup();
    //log.finest("Group = " + group);
    //int amountToCall = getController().getAmountToCall(this);

    //double callOffset = (double) amountToCall / getController().getTable().getBet();
    double amountToCall = 0;
    for (int i = 0; i < moves.length; i++) {
      if (moves[i][1].equals("call")) {
        amountToCall = Double.parseDouble(moves[i][2]);
      }
    }
    int action = amountToCall == 0 ? ActionConstants.CHECK : FOLD;

    double callOffset = amountToCall / bet;

    int callOffsetMax = 0;
    int raiseMax = 0;
    boolean condition = false;
    int playersIn = pc;
    //log.finest(getName() + " WINNING RIVER: GROUP=" + group + " CALL OFFS=" +
             // callOffset + " RAISE CNT=" + raiseCount);
    /* define max params */
    if (group >= 1 && group <= 3) {
      raiseMax = group == 3 ? 3 : 4;
      callOffset = 4;
    }
    if (group == 5) {
      condition = isFullHousePossible() || isFlushPossible();
    }
    else if (group == 6) {
      condition = isStraightPossible() || isFullHousePossible() ||
          isFlushPossible();
    }
    /* generate action */
    if (raiseMax > 0) {
      action = raiseCount < raiseMax ? RAISE : CALL;
    }
    else if (callOffsetMax > 0) {
      if (callOffset <= callOffsetMax) {
        action = CALL;
      }
    }
    switch (group) {
      case 4:
        if (isFullHousePossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (raiseCount <= 2 && callOffset <= 2) {
            action = CALL;
          }
        }
        else {
          if (raiseCount < 2) {
            action = RAISE;
          }
          else {
            action = CALL;
          }
        }
        break;
      case 5:
      case 6:
        if (condition) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (raiseCount <= 2 && callOffset <= 2) {
            action = CALL;
          }
        }
        else {
          if ( (group == 5 && raiseCount < 2) ||
              (group == 6 && raiseCount <= 3)) {
            action = RAISE;
          }
          else {
            action = CALL;
          }
        }
        break;
      case 7:
        if (isStraightPossible() || isFullHousePossible() || isFlushPossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (raiseCount <= 2 && callOffset <= 1) {
            action = CALL;
          }
        }
        else {
          if (raiseCount <= 3) {
            action = RAISE;
          }
          else {
            action = CALL;
          }
        }
        break;
      case 8:
        if (isStraightPossible() || isFullHousePossible() || isFlushPossible()) {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1 && raiseCount <= 2) {
            action = CALL;
          }
        }
        else {
          if (raiseCount == 0) {
            action = RAISE;
          }
          else if (callOffset <= 1 && raiseCount <= 2) {
            action = CALL;
          }
        }
        break;
        /*
         Group9:	// call one bet if it is just 2 players in (heads up)
         if(players_in == 2 && call_offset <= 1) call; else fold;
         */
      case 9:
        if (playersIn == 2 && callOffset <= 1) {
          action = CALL;
        }
        break;
      case 10:
        if (amountToCall == 0) {
          action = ActionConstants.CHECK;
        }
        break;
    }
    //log.finest(getName() + " WINNING RIVER: GROUP=" + group + " CALL OFFS=" +
            //  callOffset + " RAISE CNT=" + raiseCount +" and action is "+action);

    return action;
  }

  public int issuePreflopAction(int level) {
    group = getPreflopGroup(level);
    //log.finest("Group = " + group);
    switch (group) {
      case 1:
        return RAISE;
      case 2:
        if (!firstToAct && !isRaiseOccured) {
          return RAISE;
        }
        else {
          return getProbableTurn(0.5, CALL, RAISE);
        }
      case 3:
        if (isRaiseOccured) {
          return CALL;
        }
        else {
          return getProbableTurn(0.5, CALL, RAISE);
        }
      case 4:
        return CALL;
      default:
        if (firstToAct) {
          return getProbableTurn(0.8, 0.9, FOLD, CALL, RAISE);
        }
        else {
          if (isRaiseOccured) {
            return getProbableTurn(0.8, FOLD, CALL);
          }
          else {
            return CALL;
          }
        }
    }
  }

  public int issueBaseAction(int level, int stage) {
    //List bc = getModel().getBoardCards();
    Cards c = new Cards(false);
    for (Iterator it = _boardCards.iterator(); it.hasNext(); ) {
      Card card = (Card) it.next();
      c.addCard(card);
    }
    group = getGroup(level, c);
    if (isException(c)) {
      return FOLD;
    }
    switch (stage) {
      case FLOP:
        return issueBaseFlopAction(level, group);
      case TURN:
        return issueBaseTurnAction(level, group);
      case RIVER:
        return issueBaseRiverAction(level, group);
    }
    return CALL;
  }

  public int issueBaseFlopAction(int level, int group) {
    switch (group) {
      case 1:
      case 2:
      case 3:
        if (firstToAct) {
          if (isRaiseOccured) {
            return RAISE;
          }
          else {
            return getProbableTurn(0.9, RAISE, CALL);
          }
        }
        else {
          return RAISE;
        }
      case 5:
      case 8:
        if (firstToAct) {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return RAISE;
          }
        }
        else {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return RAISE;
          }
        }
      case 6:
      case 9:
        if (firstToAct) {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
        else {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.5, CALL, RAISE);
          }
        }
      case 7:
        if (firstToAct) {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.9, CALL, RAISE);
          }
        }
        else {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.5, CALL, RAISE);
          }
        }
      default:
        if (firstToAct) {
          if (isRaiseOccured) {
            return getProbableTurn(0.9, FOLD, CALL);
          }
          else {
            return getProbableTurn(0.9, CALL, RAISE);
          }
        }
        else {
          if (isRaiseOccured) {
            return getProbableTurn(0.9, FOLD, CALL);
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
    }
  }

  public int issueBaseTurnAction(int level, int group) {
    switch (group) {
      case 1:
      case 2:
      case 3:
        if (firstToAct) {
          if (isRaiseOccured) {
            return RAISE;
          }
          else {
            return getProbableTurn(0.9, RAISE, CALL);
          }
        }
        else {
          return RAISE;
        }
      case 4:
      case 5:
      case 8:
        if (firstToAct) {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return RAISE;
          }
        }
        else {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return RAISE;
          }
        }
      case 6:
      case 9:
        if (firstToAct) {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
        else {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
      case 7:
        if (firstToAct) {
          if (isRaiseOccured) {
            return getProbableTurn(0.8, CALL, FOLD);
          }
          else {
            return CALL;
          }
        }
        else {
          if (isRaiseOccured) {
            return getProbableTurn(0.8, CALL, FOLD);
          }
          else {
            return CALL;
          }
        }
      default:
        if (firstToAct) {
          if (isRaiseOccured) {
            return getProbableTurn(0.9, FOLD, CALL);
          }
          else {
            return getProbableTurn(0.9, CALL, RAISE);
          }
        }
        else {
          if (isRaiseOccured) {
            return getProbableTurn(0.9, FOLD, CALL);
          }
          else {
            return getProbableTurn(0.9, CALL, RAISE);
          }
        }
    }
  }

  public int issueBaseRiverAction(int level, int group) {
    switch (group) {
      case 1:
      case 2:
      case 3:
        return RAISE;
      case 4:
      case 5:
        if (firstToAct) {
          return CALL;
        }
        else {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return RAISE;
          }
        }
      case 6:
        if (firstToAct) {
          if (isRaiseOccured) {
            return CALL;
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
        else {
          if (isRaiseOccured) {
            return getProbableTurn(0.75, CALL, FOLD);
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
      case 7:
        if (firstToAct) {
          if (isRaiseOccured) {
            return getProbableTurn(0.5, CALL, FOLD);
          }
          else {
            return CALL;
          }
        }
        else {
          if (isRaiseOccured) {
            return getProbableTurn(0.75, FOLD, CALL);
          }
          else {
            return CALL;
          }
        }
      default:
        if (firstToAct) {
          if (isRaiseOccured) {
            return FOLD;
          }
          else {
            return getProbableTurn(0.9, CALL, FOLD);
          }
        }
        else {
          if (isRaiseOccured) {
            return FOLD;
          }
          else {
            return getProbableTurn(0.75, CALL, RAISE);
          }
        }
    }
  }

  /*
      public Action issueAction(BetRequestAction a) {
          //Logger.log().println(getName() + ": ISSUE ACTION");
          //Logger.printStackTrace();
   int action = isRaiseOccured ? FOLD : getProbableTurn(0.9, CALL, RAISE);
          int amount = a.getMinBet();
          if (amount > bankRoll) {
              action = ALLIN;
              amount = bankRoll;
          }
   BettingAction response = new BettingAction(action, BOARD_TARGET, amount);
          response.setGuid(a.getGuid());
          return response;
      }
   */
  public boolean isBot() {
    return true;
  }

  /**
   * Gets the bot's profile.
   */
  /*
       public final BotProfile getBotProfile() {
      return botProfile;
       }
   */
  public boolean isFullHousePossible() {
    return CardUtils.isFullHousePossible(getBoardAndHand(), getBoard());
  }

  public boolean isFlushPossible() {
    return CardUtils.isFlushPossible(getBoardAndHand(), getBoard());
  }

  public boolean isStraightPossible() {
    return CardUtils.isStraightPossible(getBoardAndHand(), getBoard());
  }

  public static void main(String args[]) throws Exception {
    final int dealerPos = 3;
    //player-details=1|4998.00|0.00|P1|0|0|__'__`
    //2|97900.00|100.00|P2|33556483|0|__'__`//
    //3|99800.00|200.00|P3|33556484|0|__'__`
    //4|99800.00|200.00|P4|50333700|0|__'__
    String pd[][] = {
        {"1", "100.00", "0.00", "P1", "0", "0", "__'__"}, {"2", "100.00", "0.00", "P2", "0", "0", "__'__"},
        {"3", "100.00", "0.00", "P3", "0", "0", "__'__"}, {"4", "100.00", "0.00", "P4", "0", "0", "__'__"},
        {"5", "100.00", "0.00", "P5", "0", "0", "__'__"}, {"6", "100.00", "0.00", "P6", "0", "0", "__'__"}
    };

    Vector activePlayers = new Vector();
    for (int i = 0; i < pd.length; i++) {
      if (PlayerStatus.isActive(Integer.parseInt(pd[i][4]))) {
        activePlayers.add(pd[i]);
        //System.out.println(pd[i][3]);
      }
    }
    if (activePlayers.size() == 0) {
      return;
    }
    String[][] av = (String[][]) activePlayers.toArray(new String[activePlayers.
        size()][6]);

    Arrays.sort(av, new Comparator() {
      public int compare(Object o1, Object o2) {
        String[] av1 = (String[]) o1;
        String[] av2 = (String[]) o2;
        int result = Integer.parseInt(av1[0]) - Integer.parseInt(av2[0]) + dealerPos;
        System.out.println(av1[3] + ", " + av1[0] + ":" + av2[3] + ", "  + av2[0] + "=" + result);
        return result;
      }
    });

    for (int i=0;i<av.length;i++){
        System.out.println(av[i][3]);
    }

    return;
  }

}
