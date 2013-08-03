package com.golconda.game.util;

import com.golconda.game.util.Card;
import java.util.Vector;



public class Cards
    implements java.io.Serializable {

  static int MAX_CARDS = 52;

  private Card[] cards;
  boolean _jocker;

  public Cards(boolean jocker) {
    if (jocker)    MAX_CARDS  =54;
    _jocker = jocker;
    cards = new Card[MAX_CARDS + 1];
    for (int i = 0; i < cards.length; i++) {
      cards[i] = new Card(jocker);
    }
    cards[0].index = 0;
  }

  public Cards(String s, boolean jocker) {
    this(jocker);
    for (int i = 0; i < s.length(); i += 2) {
      addCard(new Card(s.charAt(i), s.charAt(i + 1)));
    }
  }

    public Cards(String s, String sep, boolean jocker) {
      this(jocker);
      try {
          int sep_len = sep.length();
          for (int i = 0; i < s.length(); i += 2 + sep_len) {
            addCard(new Card(s.charAt(i), s.charAt(i + 1)));
          }
      }
      catch(Exception e){
          e.printStackTrace();
          System.out.println(s + " sep=" + sep);
      }
    }


  /**
   * Duplicate an existing hand.
   * @param h the hand to clone.
   */
  public Cards(Cards h) {
    cards = new Card[MAX_CARDS + 1];
    for (int i = 0; i < cards.length; i++) {
      cards[i] = new Card(h._jocker);
    }
    cards[0].index = h.size();
    for (int i = 0; i < cards[0].index; i++) {
      cards[i + 1] = h.cards[i + 1];
    }
  }
  
  public Cards copy(){
      Cards c = new Cards(_jocker);
      c.addCards(getCards());
      //System.out.println("OCards=" + this + " Copy =" + c);
      c.cards[0] = cards[0].copy();
      return c;
  }
  
  /**
   * Get the size of the hand.
   * @return the number of cards in the hand
   */
  public int size() {
    return cards[0].index;
  }

  /**
   * Clears all cards
   */
  public void clear() {
    cards[0].index = 0;
  }

  /**
   * Remove the last card in the hand.
   */
  public void removeCard() {
    if (cards[0].index > 0) {
      cards[0].index--;
    }
  }

      public boolean removeCard(Card c) {
        if (cards[0].index== 0) return false;
        boolean found=false; 
        for (int i=0;i<cards[0].index; i++){
           if (!found && cards[i+1].equals(c)){
               found=true;
           }
           if (found){
               cards[i+1]=cards[i+2];
           }
        }
        if (found){
          if (cards[0].index > 0) {
            cards[0].index--;
          }
        }
        return found;
      }

  /**
   * Remove the all cards from the hand.
   */
  public void makeEmpty() {
    cards[0].index = 0;
  }

  public boolean addCards(Cards c) {
    boolean b = true;
    for (int i = 0; i < c.size(); i++) {
      b = b && addCard(c.getCard(i + 1).copy());
    }
    return b;
  }

  /**
   * Add a card to the hand. (if there is room)
   * @param c the card to add
   * @return true if the card was added, false otherwise
   */
  public boolean addCard(Card c) {
    if (c == null) {
      return false;
    }
    if (cards[0].index == MAX_CARDS) {
      return false;
    }
    cards[0].index++;
    //System.out.println("OC=" + c + "Copy C=" + c.copy());
    cards[cards[0].index] = c.copy();
    //System.out.println("Card="+ cards[cards[0].index]);
    return true;
  }

  public boolean addCards(Card[] c) {
    if (c==null)return false;
    for (int i = 0; i < c.length; i++) {
      if (!addCard(c[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Add a card to the hand. (if there is room)
   * @param i the index value of the card to add
   * @return true if the card was added, false otherwise
   */
  public boolean addCard(int i) {
    if (cards[0].index == MAX_CARDS) {
      return false;
    }
    cards[0].index++;
    cards[cards[0].index].index = i;
    return true;
  }

  public boolean has(Card c) {
    for (int i = 0; i < size(); i++) {
      if (c.index == cards[i + 1].index) {
        return true;
      }
    }
    return false;
  }

  public boolean has(int index) {
    for (int i = 0; i < size(); i++) {
      if (index == cards[i + 1].index) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the a specified card in the hand
   * @param pos the position (1..n) of the card in the hand
   * @return the card at position pos
   */
  public Card getCard(int pos) {
    return pos > 0 && pos <= cards[0].index ? cards[pos] : null;
  }

    public Card peek(){
        if (cards[0].index==0){
            return null;
        }
        else {
            return cards[cards[0].index];
        }
    }

    public Card peekFirst(){
        if (cards[0].index==0){
            return null;
        }
        else {
            return cards[1];
        }
    }

  /**
   * Add a card to the hand. (if there is room)
   * @param c the card to add
   * @return true if the card was added, false otherwise
   */
  public void setCard(int pos, Card c) {
    if (cards[0].index < pos) {
      return;
    }
    cards[pos].index = c.index;
  }

  /**
   * Obtain the array of card indexes for this hand.
   * First element contains the size of the hand.
   * @return array of card indexs (size = MAX_CARDS+1)
   */
  public int[] getCardArray() {
    int[] result = new int[MAX_CARDS + 1];
    for (int i = 0; i < cards[0].index + 1; i++) {
      result[i] = cards[i].index;
    }
    return result;
  }
  
    public void setOpen(){
      for (int i=1; i < cards[0].index+1; i++) {
        cards[i].setIsOpened(true);;
      }
    }

  public Card[] getCards(){
    Vector crds = new Vector();
    for (int i=1; i < cards[0].index+1; i++) {
      crds.add(cards[i]);
    }
    return (Card [])crds.toArray(new Card[crds.size()]);
  }

  
     /**
      * Bubble Sort the hand to have cards in descending order, but card index.
      * Used for database indexing.
      */
     public void sortSuits() {
       boolean flag = true;
       //System.out.println(this);
       while (flag) {
         flag = false;
         for (int i = 1; i < cards[0].index; i++) {
           int r1 = cards[i].getIndex();
           int r2 = cards[i+1].getIndex();
           if (r1 < r2) {
             //System.out.println(r1 + "-" +cards[i] + " < " + r2 + "-" +cards[i + 1]);
             flag = true;
             Card t = cards[i];
             cards[i] = cards[i + 1];
             cards[i + 1] = t;
           }
           //System.out.println(this);
         }
       }
     }

  /**
   * Bubble Sort the hand to have cards in descending order, but card index.
   * Used for database indexing.
   */
  public void sort(boolean highAce) {
    boolean flag = true;
    //System.out.println(this);
    while (flag) {
      flag = false;
      for (int i = 1; i < cards[0].index; i++) {
        int r1 = cards[i].getRank() == 12 && !highAce ? -1 : cards[i].getRank();
        int r2 = cards[i+1].getRank() == 12 && !highAce ? -1 : cards[i+1].getRank();
        int r1ind = cards[i].getRank() == 12 && !highAce ? -1 : cards[i].getIndex();
        int r2ind = cards[i+1].getRank() == 12 && !highAce ? -1 : cards[i+1].getIndex();
        if (r1 < r2 || (r1==r2 && r1ind < r2ind)) {
          //System.out.println(cards[i] + " < " + cards[i + 1]);
          flag = true;
          Card t = cards[i];
          cards[i] = cards[i + 1];
          cards[i + 1] = t;
        }
        //System.out.println(this);
      }
    }
  }
    
  /**
   * Get a string representation of this Hand.
   */
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < cards[0].index; i++) {
      s.append(getCard(i + 1)).append("'");
    }
    return s.toString();
  }

  /**
   * Get a string representation of this Hand.
   */
  public String stringValue() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < cards[0].index; i++) {
      Card c = getCard(i + 1);
      s.append(c.isOpened() ? c.toString() : "__").append("'");
    }
    if (cards[0].index > 0) {
      return s.deleteCharAt(s.length() - 1).toString();
    }
    else {
      return "";
    }
  }

  /**
   * Get a string representation of this Hand.
   */
  public String openStringValue() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < cards[0].index; i++) {
      s.append(getCard(i + 1)).append("'");
    }
    if (cards[0].index > 0) {
     return s.deleteCharAt(s.length() - 1).toString();
   }
   else {
     return "";
   }
  }
  
    public boolean equals(Cards cv){
        cv.sort(false);
        sort(false);
        return openStringValue().equals(cv.openStringValue());
    }
    
  public static void main(String args[]){
      Cards cv = new Cards("7C9C6D8D", false);
      System.out.println(cv);
      cv.sort(false);
      System.out.println(cv);
      //7H'AD'8S'AC'3C'TH'7C'QC'5H'AS'6S'TD<<<>>>>AC'AD'AS'QC'TD'TH'8S'7C'7H'6S'5H'3C
      cv = new Cards("7H'AD'8S'AC'3C'TH'7C'QC'5H'AS'6S'TD", "'", false);
      cv.sort(false);
      System.out.println(cv.openStringValue());
      cv.sort(false);
      System.out.println(cv.openStringValue());
      cv = new Cards("AC'AD'AS'QC'TD'TH'8S'7C'7H'6S'5H'3C", "'", false);
      cv.sort(false);
      System.out.println(cv.openStringValue());
      cv.sort(false);
      System.out.println(cv.openStringValue());
  }
    
}
