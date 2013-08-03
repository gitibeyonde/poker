package com.agneya.util;
import java.security.SecureRandom;


public class Rng
    implements java.io.Serializable {

  private SecureRandom randomGenerator;

  public Rng() {
	  try {
		  randomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
		  randomGenerator.nextInt();
	  }
	  catch(Exception e){
		  randomGenerator = new SecureRandom();
	  }
  };

  public int nextIntLessThan(int max) {
    if (max == 0) {
      return 0;
    }
    return nextBetween(0, max);
  }

  public int nextIntBetween(int min, int max) {
    if (max == min) {
      return min;
    }
    return nextBetween(min, max);
  }
  
    public double doubleValueBetween(double min, double max){
      if (min > max){
        throw new IllegalStateException("min val is more than max");
      }
      return randomGenerator.nextDouble() * (max-min) + min;
    }
    
  protected int nextBetween(int min, int max) {
    int next = min + randomGenerator.nextInt(max - min);
    return next;
  }

  public String getNewSessionId() {
    return String.valueOf(randomGenerator.nextLong());
  }

 public int getNewIdentifier() {
      return randomGenerator.nextInt();
    }
    
      public long getNewId() {
        return randomGenerator.nextLong();
      }

  public static void main(String args[]) {
    //Rng rng = new Rng();
    //for (int i = 0; i < 10000; i++) {
      //System.out.print(rng.nextIntBetween(0, 2) + ",");
    //}
    int j=0;
    for (int i=0;i<100;i++){
      j=(int)(Math.random()* 52) + 1;
      System.out.print(j + " ");
    }
  }
}
