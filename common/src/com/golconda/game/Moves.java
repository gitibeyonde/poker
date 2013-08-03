package com.golconda.game;

public abstract class Moves {
  protected long intVal;
  public static final long NONE = -1;

  public static long[] values;
  public static String[] strings;
    
  public abstract boolean responseRequired();
    
    
  protected Moves(long intVal) {
    this.intVal = intVal;
  }
  
   public long intVal(){
       return intVal;
   }
      

  // implement this method ...
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public int hashCode() {
    return super.hashCode();
  }
  
   public int intIndex() {
       byte index = -1;
       while (intVal > 0) {
         index++;
         intVal >>>= 1;
       }
       return index;
   }

  public String stringValue() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      if (intVal == values[i]) {
        return strings[i];
      }
      else if ( (intVal & values[i]) > 0) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      throw new IllegalArgumentException("Invalid mvId : " + intVal);
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }
}
