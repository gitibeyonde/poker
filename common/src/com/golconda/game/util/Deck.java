package com.golconda.game.util;


import java.util.Random;
import java.util.logging.Logger;


public class Deck {

  static Logger _cat = Logger.getLogger(Deck.class.getName());

  public static int NUM_CARDS = 52;
  private Card[] cards = new Card[NUM_CARDS];
  private int position; // top of deck
  private Random r = new Random();
  private Random r2 = new Random();
  boolean _jocker;

  /**
   * Constructor.
   */
  public Deck(boolean jocker) {
    position = 0;
    if (jocker)    NUM_CARDS  =54;
      _jocker = jocker;
    for (int i = 0; i < NUM_CARDS; i++) {
      cards[i] = new Card(i, jocker);
    }
  }

  /**
   * Constructor w/ shuffle seed.
   * @param seed the seed to use in randomly shuffling the deck.
   */
  public Deck(long seed, boolean jocker) {
    this(jocker);
      _jocker = jocker;
    if (seed == 0) {
      seed = System.currentTimeMillis();
    }
    r.setSeed(seed);
    r2.setSeed(seed / 2 + NUM_CARDS);
  }

  /**
   * Constructor w/ shuffle seed.
   * @param seed the seed to use in randomly shuffling the deck.
   */
  public Deck(long seed, long seed2, boolean jocker) {
    this(jocker);
    if (seed == 0) {
      seed = System.currentTimeMillis();
    }
    r.setSeed(seed);
    r2.setSeed(seed2);
  }

  /**
   * Places all cards back into the deck.
   * Note: Does not sort the deck.
   */
  public synchronized void reset() {
    position = 0;
  }

  /**
   * Shuffles the cards in the deck.
   */
  public synchronized void shuffle() {
    Card tempCard;
    int i, j;
    for (i = 0; i < NUM_CARDS; i++) {
      j = i + randInt(NUM_CARDS - i);
      tempCard = cards[j];
      cards[j] = cards[i];
      cards[i] = tempCard;
    }
    position = 0;
  }

  /**
   * Obtain the next card in the deck.
   * If no cards remain, a null card is returned
   * @return the card dealt
   */
  public synchronized Card deal() {
    return (position < NUM_CARDS ? cards[position++] : null);
  }

  /**
   * Obtain the next card in the deck.
   * If no cards remain, a null card is returned
   * @return the card dealt
   */
  public synchronized Card dealCard() {
    return extractRandomCard();
  }

  /**
   * Find position of Card in Deck.
   */
  public synchronized int findCard(Card c) {
    int i = position;
    int n = c.getIndex();
    while (i < NUM_CARDS && n != cards[i].getIndex()) {
      i++;
    }
    return (i < NUM_CARDS ? i : -1);
  }

  private synchronized int findDiscard(Card c) {
    int i = 0;
    int n = c.getIndex();
    while (i < position && n != cards[i].getIndex()) {
      i++;
    }
    return (n == cards[i].getIndex() ? i : -1);
  }

  /**
   * Remove all cards in the given hand from the Deck.
   */
  public synchronized void extractHand(Cards h) {
    for (int i = 1; i <= h.size(); i++) {
      this.extractCard(h.getCard(i));
    }
  }

  /**
   * Remove a card from within the deck.
   * @param c the card to remove.
   */
  public synchronized void extractCard(Card c) {
    int i = findCard(c);
    if (i != -1) {
      Card t = cards[i];
      cards[i] = cards[position];
      cards[position] = t;
      position++;
    }
    else {
      _cat.severe("*** ERROR: could not find card " + c);
      Thread.dumpStack();
    }
  }

  /**
   * Remove and return a randomly selected card from within the deck.
   */
  public synchronized Card extractRandomCard() {
    int pos = position + randInt2(NUM_CARDS - position);
    Card c = cards[pos];
    cards[pos] = cards[position];
    cards[position] = c;
    position++;
    return c;
  }

  /**
   * Return a randomly selected card from within the deck without removing it.
   */
  public synchronized Card pickRandomCard() {
    return cards[position + randInt(NUM_CARDS - position)];
  }

  /**
   * Place a card back into the deck.
   * @param c the card to insert.
   */
  public synchronized void replaceCard(Card c) {
    int i = findDiscard(c);
    if (i != -1) {
      position--;
      Card t = cards[i];
      cards[i] = cards[position];
      cards[position] = t;
    }
  }

  /**
   * Obtain the position of the top card.
   * (the number of cards dealt from the deck)
   * @return the top card index
   */
  public synchronized int getTopCardIndex() {
    return position;
  }

  /**
   * Obtain the number of cards left in the deck
   */
  public synchronized int cardsLeft() {
    return NUM_CARDS - position;
  }

  /**
   * Obtain the card at a specific index in the deck.
   * Does not matter if card has been dealt or not.
   * If i < topCardIndex it has been dealt.
   * @param i the index into the deck (0..51)
   * @return the card at position i
   */
  public synchronized Card getCard(int i) {
    return cards[i];
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("* ");
    for (int i = 0; i < position; i++) {
      s.append(cards[i].toString() + " ");
    }
    s.append("\n* ");
    for (int i = position; i < NUM_CARDS; i++) {
      s.append(cards[i].toString() + " ");
    }
    return s.toString();
  }

  private int randInt(int range) {
    return (int) (r.nextDouble() * range);
  }

  private int randInt2(int range) {
    return (int) (r2.nextDouble() * range);
  }

}
