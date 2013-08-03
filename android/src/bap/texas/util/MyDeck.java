package bap.texas.util;

import java.util.Random;
import java.util.Vector;


public class MyDeck {
    
    Vector<Integer> _deck = new Vector<Integer>(); // tho jockers in the deck
    static Random _r =	new Random();
    
    //52 ir red joker
    //53 is blck joker
    public MyDeck() {
        //initialize the deck
         Vector<Integer> v = new Vector<Integer>();
         for (int i=1;i<53;i++){
             v.add(new Integer(i));
         }
         for (int i=0;i<52;i++){
             //draw a random card 
        	 Integer c = v.remove(_r.nextInt(v.size()));
             _deck.add(c);
         }
    }
    
    
    public void reset(){
        //initialize the deck
        Vector<Integer> v = new Vector<Integer>();
        _deck = new Vector<Integer>(); 
        for (int i=1;i<53;i++){
            v.add(new Integer(i));
        }
        for (int i=0;i<52;i++){
            //draw a random card 
        	Integer c = v.remove(_r.nextInt(v.size()));
            _deck.add(c);
        }
    }
   
    
    public Integer deal(){
        if (_deck.size()==0){
            reset();
        }
        Integer c= _deck.remove(0);
        //Log.w("MyDeck deal", c +"");
        return c;
    }
  
  
    public int cardsRemaining(){
        return _deck.size();
    }
    
    
}
