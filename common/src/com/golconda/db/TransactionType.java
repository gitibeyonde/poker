package com.golconda.db;

public class TransactionType {

  //Play 
  public static final long Play_Deposit = 1;
  public static final long Play_Payout = 2;
  public static final long Play_Registration_Bonus = 4;
  public static final long Play_U2 = 8;

  //2
  public static final long Play_Tourny_BuyinFees = 16;
  public static final long Play_Tourny_Win = 32;
  public static final long Play_SnG_BuyinFees = 64;
  public static final long Play_SnG_Win = 128;

  //3
  public static final long Play_On_Table = 256;
  public static final long Play_From_Table = 512;
  public static final long Play_Transfer_In = 1024;
  public static final long Play_Transfer_Out = 2048;

  //4
  public static final long Play_Bonus = 4096;
  public static final long Play_Bonus_Converted = 8192;
  public static final long Play_U7 = 16384;
  public static final long Play_U8 = 32768;

  //Real
  public static final long Real_Deposit = 65536;
  public static final long Real_Payout = 131072;
  public static final long Real_Registration_Bonus = 262144;
  public static final long Real_U2 = 524288;

  //6
  public static final long Real_Tourny_BuyinFees = 1048576;
  public static final long Real_Tourny_Win = 2097152;
  public static final long Real_SnG_BuyinFees = 4194304;
  public static final long Real_SnG_Win = 8388608;

  //7
  public static final long Real_On_Table = 16777216;
  public static final long Real_From_Table = 33554432;
  public static final long Real_Transfer_In = 67108864;
  public static final long Real_Transfer_Out = 134217728;

    //8
    public static final long Real_Bonus = 268435456;
    public static final long Real_Bonus_Converted = 536870912;
    public static final long U22 = 1073741824;
    public static final long Real_Tourny_BuyinFees_Refund = 2147483648L;
    
    //9
    public static final long Friend_Bonus = 4294967296L;
    public static final long Friend_Bonus_Converted = 8589934592L;
    public static final long Deposit_Bonus = 17179869184L;
    public static final long Deposit_Bonus_Converted = 34359738368L;
   
    //10
    public static final long Rakeback_Payout = 68719476736L;
    public static final long Commision_Receved = 137438953472L;
    public static final long Commision_Payout = 274877906944L;
    public static final long Real_Rejected_Payout = 549755813888L;
 
    public static final long PLAY = 0xFFFF;
    public static final long REAL = 0xFFFF0000;


  private static String[] strings = new String[] {
      "Play Deposit", "Play Payout", "Play reg bonus", "uu",
      "Play Tourny Buyin & Fees","Play Tourny Win", "Play SnG Buyin & Fees", "Play SnG Win",
      "Play money on table", "Play money from table", "Play Transfer In", "Play Transfer Out",
      "Play Bonus", "Play Bonus converted", "UU", "RU",
      "Real Deposit", "Real Payout", "Real reg bonus", "TU2",
      "Real Tourny Buyin & Fees", "Real Tourny Win", "Real SnG Buyin & Fees", "Real SnG Win",
      "Real money on table", "Real money from table", "Real Transfer In", "Real Transfer Out",
      "Real Bonus", "Real Bonus converted", "U22", "Real_Tourny_BuyinFees_Refund",
      "Friend_Bonus", "Friend_Bonus_Converted", "Deposit_Bonus", "Deposit__Bonus_Converted",
      "Rakeback_Payout", "Commision Receved", "Commision Payout", "Real Rejected Payout"
  };

  static long[] values = new long[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824,2147483648L,
      4294967296L, 8589934592L, 17179869184L, 34359738368L, 
      68719476736L, 137438953472L, 274877906944L    
  };

  public static boolean isReal(long iv) {
  	return ((iv & REAL) == iv)?true:false;
  }

  public static boolean isPlay(long iv) {
  	return ((iv & PLAY) == iv)?true:false;
  }


      public long intVal;
    
  public TransactionType(long iv) {
      intVal = iv;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      if (intVal == values[i]) {
        return strings[i];
      }
      else if ( (intVal & values[i]) == values[i]) {
        buf.append(strings[i]).append("|`");
      }
    }
    if (buf.length() == 0) {
      throw new IllegalArgumentException("Invalid TransactionType : " + intVal);
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static void main(String[] argv) {
  }

  public long intVal() {
    return intVal;
  }

  public boolean equals(TransactionType g){
    return g.intVal == intVal;
  }

  public boolean isGame() {
        return ((intVal & (Play_On_Table | Real_On_Table)) == intVal)?true:false;
  }

  public boolean isDeposit(){
      return ((intVal & (Play_Deposit | Real_Deposit)) == intVal)?true:false;
  }
    public boolean isPayout(){
        return ((intVal & (Play_Payout | Real_Payout)) == intVal)?true:false;
    }
    public boolean isBonus(){
        return ((intVal & (Real_Bonus | Play_Bonus | Real_Bonus_Converted | 
        Play_Bonus_Converted | Play_Transfer_Out | Play_Transfer_In | 
        Real_Transfer_Out | Real_Transfer_In | 
        Play_Registration_Bonus | Real_Registration_Bonus)) == intVal)?true:false;
    }

}
