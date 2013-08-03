package bap.texas.util;


import java.lang.String;



public class Card
 implements java.io.Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = 153453453464L;
public static final int CLUBS = 0;
public static final int DIAMONDS = 1;
public static final int HEARTS = 2;
public static final int SPADES = 3;

public static final int BAD_CARD = -1;
public static final int CLOSED_CARD = -1;
public static final int TWO = 0;
public static final int THREE = 1;
public static final int FOUR = 2;
public static final int FIVE = 3;
public static final int SIX = 4;
public static final int SEVEN = 5;
public static final int EIGHT = 6;
public static final int NINE = 7;
public static final int TEN = 8;
public static final int JACK = 9;
public static final int QUEEN = 10;
public static final int KING = 11;
public static final int ACE = 12;
public static final int CLOSE = -2;

public static final int _2C = 0;
public static final int _3C = 1;
public static final int _4C = 2;
public static final int _5C = 3;
public static final int _6C = 4;
public static final int _7C = 5;
public static final int _8C = 6;
public static final int _9C = 7;
public static final int _TC = 8;
public static final int _JC = 9;
public static final int _QC = 10;
public static final int _KC = 11;
public static final int _AC = 12;
public static final int _2D = 13;
public static final int _3D = 14;
public static final int _4D = 15;
public static final int _5D = 16;
public static final int _6D = 17;
public static final int _7D = 18;
public static final int _8D = 19;
public static final int _9D = 20;
public static final int _TD = 21;
public static final int _JD = 22;
public static final int _QD = 23;
public static final int _KD = 24;
public static final int _AD = 25;
public static final int _2H = 26;
public static final int _3H = 27;
public static final int _4H = 28;
public static final int _5H = 29;
public static final int _6H = 30;
public static final int _7H = 31;
public static final int _8H = 32;
public static final int _9H = 33;
public static final int _TH = 34;
public static final int _JH = 35;
public static final int _QH = 36;
public static final int _KH = 37;
public static final int _AH = 38;
public static final int _2S = 39;
public static final int _3S = 40;
public static final int _4S = 41;
public static final int _5S = 42;
public static final int _6S = 43;
public static final int _7S = 44;
public static final int _8S = 45;
public static final int _9S = 46;
public static final int _TS = 47;
public static final int _JS = 48;
public static final int _QS = 49;
public static final int _KS = 50;
public static final int _AS = 51;

public static final int NUM_SUITS = 4;
public static final int NUM_RANKS = 13;
public static final int NUM_CARDS = NUM_SUITS * NUM_RANKS; // for jocker

/*private */
public int index;
private boolean _isOpened = false;
private boolean _isSelected = false;

/**
* Constructor -- makes an empty card.
*/
public Card() {
 index = -1;
}

/**
* Constructor.
* @param rank face value of the card
* @param suit suit of the card
*/
public Card(int rank, int suit) {
 index = toIndex(rank, suit);
}

/**
* Constructor.
* Creates a Card from an integer index {0..51}
* @param index integer index of card between 0 and 51
*/
public Card(int index) {
 if (index >= 0 && index < NUM_CARDS) {
   this.index = index;
 }
 else {
   this.index = CLOSED_CARD;
 }
}

public Card(String s) {
 if (s.length() == 2) {
   index = charsToIndex(s.charAt(0), s.charAt(1));
 }
}

public Card copy(){
   Card c = new Card(index);
   c.setIsOpened(isOpened());
     return c;
}

/**
* Constructor.
* Creates a card from its character based representation.
* @param rank the character representing the card's rank
* @param rank the character representing the card's suit
*/
public Card(char rank, char suit) {
 index = charsToIndex(rank, suit);
}

private int charsToIndex(char rank, char suit) {
 int r = -1;
 switch (Character.toLowerCase(rank)) {
   case '2':
     r = TWO;
     break;
   case '3':
     r = THREE;
     break;
   case '4':
     r = FOUR;
     break;
   case '5':
     r = FIVE;
     break;
   case '6':
     r = SIX;
     break;
   case '7':
     r = SEVEN;
     break;
   case '8':
     r = EIGHT;
     break;
   case '9':
     r = NINE;
     break;
   case 't':
     r = TEN;
     break;
   case 'j':
     r = JACK;
     break;
   case 'q':
     r = QUEEN;
     break;
   case 'k':
     r = KING;
     break;
   case 'a':
     r = ACE;
     break; 
   case '_':
     r = CLOSE;
     break;
 }
 int s = -1;
 switch (Character.toLowerCase(suit)) {
   case 'h':
     s = HEARTS;
     break;
   case 'd':
     s = DIAMONDS;
     break;
   case 's':
     s = SPADES;
     break;
   case 'c':
     s = CLUBS;
     break;
   case '_':
     r = CLOSE;
     break;
 }
 if (s != -1 && r != -1) {
   return toIndex(r, s);
 }
 else {
   return CLOSED_CARD;
 }
}

/**
* Return the integer index for this card.
* @return the card's index value
*/
public int getIndex() {
 return index;
}

/**
* Change the index of the card.
* @param index the new index of the card
*/
public void setIndex(int index) {
 this.index = index;
}

/**
* convert a rank and a suit to an index
* @param rank the rank to convert
* @param suit the suit to convert
* @return the index calculated from the rank and suit
*/
public static int toIndex(int rank, int suit) {
 return (NUM_RANKS * suit) + rank;
}

/**
* Change this card to another. This is more practical
* than creating a new object for optimization reasons.
* @param rank face value of the card
* @param suit suit of the card
*/
public void setCard(int rank, int suit) {
 index = toIndex(rank, suit);
}

/**
* Obtain the rank of this card
* @return rank
*/
public int getRank() {
 return (int) (index % NUM_RANKS);
}

public int getLowBJRank() {
 if (getRank() == ACE) {
   return 1;
 }
 else if (getRank() >= 8) {
   return 10;
 }
 else {
   return getRank() + 2;
 }
}

public int getHighBJRank() {
 if (getRank() == ACE) {
   return 11;
 }
 else if (getRank() >= 8) {
   return 10;
 }
 else {
   return getRank() + 2;
 }
}

public int getBaccaratRank() {
  if (getRank() == ACE) {
    return 1;
  }
  else if (getRank() >= 8) {
    return 0;
  }
  else {
    return getRank() + 2;
  }
}


/**
  * Returns whether the card passed in is a non-Ace face card: a ten, jack, queen, or king.
  */
 public boolean isNonAceFaceCard() {
     int value = getRank();
     return (value == KING )
          || (value == QUEEN )
          || (value == JACK )
          ||  (value == TEN );
 }

/**
* Obtain the rank of this card
* @return rank
*/
public static int getRank(int i) {
 return (int) (i % NUM_RANKS);
}

/**
* Obtain the suit of this card
* @return suit
*/
public int getSuit() {
 int s = (int) (index / NUM_RANKS);
 if (s >=4 ){
	 System.out.println("Suit=" + s);
	 System.out.println("Index=" + index);
	 System.out.println("NUM_RANKS=" + NUM_RANKS);
	 System.exit(-1);
 }
 return s;
}

/**
* Obtain a String representation of this Card
* @return A string for this card
*/
public String toString() {
 StringBuffer s = new StringBuffer();
 if (index == -1) {
   s.append("__");
 }
 else {
   s.append(getRankChar(getRank()));
   s.append(getSuitChar(getSuit()));
 }
 return s.toString();
}

public static char getRankChar(int r) {
 switch (r) {
   case ACE:
     return 'A';
   case KING:
     return 'K';
   case QUEEN:
     return 'Q';
   case JACK:
     return 'J';
   case TEN:
     return 'T';
   default:
     return Character.forDigit(r + 2, Character.MAX_RADIX);
 }
}

public static char getSuitChar(int s) {
 switch (s) {
   case HEARTS:
     return 'H';
   case DIAMONDS:
     return 'D';
   case CLUBS:
     return 'C';
   case SPADES:
     return 'S';
   default:
     return ' ';
 }
}

public static String getSuitStr(int s) {
 switch (s) {
   case HEARTS:
     return "HEARTS";
   case DIAMONDS:
     return "DIAMONDS";
   case CLUBS:
     return "CLUBS";
   case SPADES:
     return "SPADES";
   default:
     return " ";
 }
}

public boolean equals(Card c){
   if (c!=null && c.index == index){
       return true;
   }
   else {
       return false;
   }
}

public boolean isOpened() {
 return _isOpened;
}

public void setIsOpened(boolean isOpened) {
 _isOpened = isOpened;
}


   public boolean isSelected() {
     return _isSelected;
   }

   public void setIsSelected(boolean sel) {
     _isSelected = sel;
   }
}
