package com.golconda.game.util;

import com.agneya.util.Rng;

import java.util.Vector;


public class TambolaPatternDict {
  Vector _dict;
  static TambolaPatternDict _pd=null;
  static Object _dummy = new Object();

  public static TambolaPatternDict instance(){
     if (_pd == null) {
       synchronized (_dummy) {
         if (_pd == null) {
           _pd = new TambolaPatternDict();
         }
       }
     }
     return _pd;
   }


  private TambolaPatternDict() {
    _dict = new Vector();
    init();
  }

  void init(){// pattern X
    char blank[] = {
        '_', '_', '_', '_', '_' ,
        '_', '_', '_', '_', '_' ,
        '_', '_', '_', '_', '_' ,
        '_', '_', '_', '_', '_' ,
        '_', '_', '_', '_', '_'
    };

    char full[] = {
        'X', 'X', 'X', 'X', 'X' ,
        'X', 'X', 'X', 'X', 'X' ,
        'X', 'X', 'X', 'X', 'X' ,
        'X', 'X', 'X', 'X', 'X' ,
        'X', 'X', 'X', 'X', 'X'
    };

    // pattern X
    char x[] = {
        'X', '_', '_', '_', 'X' ,
        '_', 'X', '_', 'X', '_' ,
        '_', '_', 'X', '_', '_' ,
        '_', 'X', '_', 'X', '_' ,
        'X', '_', '_', '_', 'X'
    };
    _dict.add(new BingoPattern("exx", x));

    // pattern Z
    char z[] = {
        'X', 'X', 'X', 'X', 'X' ,
        '_', '_', '_', 'X', '_' ,
        '_', '_', 'X', '_', '_' ,
        '_', 'X', '_', '_', '_' ,
        'X', 'X', 'X', 'X', 'X'
    };
    _dict.add(new BingoPattern("zed", z));

    // pattern k
    char k[] = {
        'X', '_', '_', 'X', 'X' ,
        'X', '_', 'X', '_', '_' ,
        'X', 'X', '_', '_', '_' ,
        'X', '_', 'X', '_', '_' ,
        'X', '_', '_', 'X', 'X'
    };
    _dict.add(new BingoPattern("kay", k));

    char snake[] = {
        '_', 'X', 'X', '_', '_' ,
        'X', '_', '_', '_', '_' ,
        '_', 'X', 'X', 'X', '_' ,
        '_', '_', '_', '_', 'X' ,
        '_', '_', 'X', 'X', '_'
    };
    _dict.add(new BingoPattern("snake", snake));

    char zen[] = {
        '_', '_', 'X', '_', '_' ,
        '_', 'X', '_', 'X', '_' ,
        'X', '_', '_', '_', 'X' ,
        '_', 'X', '_', 'X', '_' ,
        '_', '_', 'X', '_', '_'
    };
    _dict.add(new BingoPattern("zen", zen));


    char swastika[] = {
        'X', 'X', '_', '_', 'X' ,
        '_', '_', 'X', '_', 'X' ,
        '_', 'X', 'X', 'X', '_' ,
        'X', '_', 'X', '_', '_' ,
        'X', '_', '_', 'X', 'X'
    };
    _dict.add( new BingoPattern("swastika", swastika));


    char sporty[] = {
        'X', 'X', 'X', 'X', 'X' ,
        '_', '_', 'X', '_', '_' ,
        '_', '_', 'X', '_', '_' ,
        '_', 'X', '_', 'X', '_' ,
        'X', '_', '_', '_', 'X'
    };
    _dict.add(new BingoPattern("sporty", sporty));

  }

  public BingoPattern getRandomPattern(){
    Rng rng = new Rng();
   return (BingoPattern)_dict.elementAt(rng.nextIntBetween(0, _dict.size()));
 }



}
