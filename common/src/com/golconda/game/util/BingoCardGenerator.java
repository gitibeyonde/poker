package com.golconda.game.util;

import com.agneya.util.Rng;


public class BingoCardGenerator {
  BingoCard _deck[];
  int _dsize;
  Rng _rng;

  public BingoCardGenerator(int dsize) {
    _dsize = dsize;
    _deck = new BingoCard[_dsize];
    for (int i = 0; i < _dsize; i++) {
      _deck[i] = new BingoCard();
    }
    init_cards(_dsize, _deck);
    _rng = new Rng();
  }

  private void init_cards(int size, BingoCard d[]) {
    int i, count, found;
    short num;
    int rand;
    for (int index = 0; index < size; index++) {
      //num = random();
      for (i = 0; i < 5; i++) { // loop to initialize size cards
        found = 0;
        rand = _rng.nextIntBetween(1, 16);
        num = BingoCard._bingo_map[rand]; // with range limits for each column
        if ( (d[index]._b & num) == num) {
          // found
          i--;
          continue;
        }
        d[index]._b += num;
        d[index]._bingo[i] = rand;
      }
      for (i = 0; i < 5; i++) { // loop to initialize size cards
        found = 0;
        rand = _rng.nextIntBetween(1, 16);
        num = BingoCard._bingo_map[rand]; // with range limits for each column
        if ( (d[index]._i & num) == num) {
          // found
          i--;
          continue;
        }
        d[index]._i += num;
        d[index]._bingo[i + 5] = rand + 15;
      }
      for (i = 0; i < 5; i++) { // loop to initialize size cards
        if (i==2)i++;
        found = 0;
        rand = _rng.nextIntBetween(1, 16);
        num = BingoCard._bingo_map[rand]; // with range limits for each column
        if ( (d[index]._n & num) == num) {
          // found
          i--;
          continue;
        }
        d[index]._n += num;
        d[index]._bingo[i + 10] = rand + 30;
      }
      for (i = 0; i < 5; i++) { // loop to initialize size cards
        found = 0;
        rand = _rng.nextIntBetween(1, 16);
        num = BingoCard._bingo_map[rand]; // with range limits for each column
        if ( (d[index]._g & num) == num) {
          // found
          i--;
          continue;
        }
        d[index]._g += num;
        d[index]._bingo[i + 15] = rand + 45;
      }
      for (i = 0; i < 5; i++) { // loop to initialize size cards
        found = 0;
        rand = _rng.nextIntBetween(1, 16);
        num = BingoCard._bingo_map[rand]; // with range limits for each column
        if ( (d[index]._o & num) == num) {
          // found
          i--;
          continue;
        }
        d[index]._o += num;
        d[index]._bingo[i + 20] = rand + 60;
      }
    }

  }

  public BingoCard card(int i) {
    return _deck[i];
  }

  public BingoCard[] allCards(){
    return _deck;
  }

  public String toString() {
    String str = "";
    for (int i = 0; i < _dsize; i++) {
      str += _deck[i].toString() + "\n";
    }
    return str;
  }

  char get_letter(int callNum) {
    char letter;
    if (callNum < 16) {
      return letter = 'B';
    }
    if (callNum > 15 && callNum < 31) {
      return letter = 'I';
    }
    if (callNum > 30 && callNum < 46) {
      return letter = 'N';
    }
    if (callNum > 45 && callNum < 61) {
      return letter = 'G';
    }
    return letter = 'O';
  }

  public static void main(String args[]) {
    BingoCardGenerator d = new BingoCardGenerator(10);
    System.out.println(d);
  }

}
