package com.golconda.game.util;

import com.agneya.util.Rng;

import java.util.Vector;

public class MyDeck {
    
    Vector<Card> _deck = new Vector<Card>(); // tho jockers in the deck
    boolean _jocker_allowed=false;
    static Rng _rng = new Rng();
    
    //52 ir red jocker
    //53 is blck jocker
    public MyDeck(boolean t) {
        _jocker_allowed=t;
        //initialize the deck
         Vector<Card> v = new Vector<Card>();
         if(_jocker_allowed){
             for (int i=0;i<54;i++){
                 v.add(new Card(i,_jocker_allowed));
             }
             for (int i=0;i<54;i++){
                 //draw a random card 
                 Card c = v.remove(_rng.nextIntLessThan(v.size()));
                 _deck.add(c);
             }
         }
         else {
             for (int i=0;i<52;i++){
                 v.add(new Card(i,_jocker_allowed));
             }
             for (int i=0;i<52;i++){
                 //draw a random card 
                 Card c = v.remove(_rng.nextIntLessThan(v.size()));
                 _deck.add(c);
             }
 
         }
    }
    
    
    public void reset(){
        //initialize the deck
        Vector<Card> v = new Vector<Card>();
        if(_jocker_allowed){
            for (int i=0;i<54;i++){
                v.add(new Card(i,_jocker_allowed));
            }
            for (int i=0;i<54;i++){
                //draw a random card 
                Card c = v.remove(_rng.nextIntLessThan(v.size()));
                _deck.add(c);
            }
        }
        else {
            for (int i=0;i<52;i++){
                v.add(new Card(i,_jocker_allowed));
            }
            for (int i=0;i<52;i++){
                //draw a random card 
                Card c = v.remove(_rng.nextIntLessThan(v.size()));
                _deck.add(c);
            }
        
        }
    }
    
    public MyDeck(Cards cv){
        Card[] cs = cv.getCards();
        for (int i=0;i<cs.length;i++){
            //System.out.println(i + ">>>" + cs[i]);
            _deck.add(cs[i]);
        }
    }
    
    public Card deal(){
        if (_deck.size() == 0){
            reset();
        }
        Card c= _deck.remove(0);
        return c;
    }
    
    public Card peek(){
        if (_deck.size() == 0){
            reset();
        }
        Card c= _deck.get(0);
        return c;
    }
    
    public Cards dealCloseCards(int cnt){
        if (_deck.size() < cnt){
            reset();
        }
        Cards cs = new Cards(_jocker_allowed);
        for (int i=0;i<cnt;i++){
            Card c = deal();
            c.setIsOpened(false);
            cs.addCard(c);
        }
        return cs;
    }
    public Cards dealOpenCards(int cnt){
        if (_deck.size() < cnt){
            reset();
        }
        Cards cs = new Cards(_jocker_allowed);
        for (int i=0;i<cnt;i++){
            Card c = deal();
            c.setIsOpened(true);
            cs.addCard(c);
        }
        return cs;
    }
    
    public int cardsRemaining(){
        return _deck.size();
    }
    
    public void addCards(Card[] c){
    	for (int i=0;i<c.length;i++){
    		_deck.add(c[i]);
    	}
    }
    
    public static void main(String args[]){
        MyDeck d = new MyDeck(true);
        for (int i=0;i<5;i++){
            System.out.print(d.deal());
        }
        System.out.print(d.peek());
        System.out.print(d.deal());
        System.out.print(d.peek());
       
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();       
       System.exit(0);
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<55;i++){
            System.out.print(d.deal());
        }
        System.out.println();
        d.reset();
        for (int i=0;i<10;i++){
            System.out.print(d.dealOpenCards(5) + ",");
        }
        
    }
    
    
}
