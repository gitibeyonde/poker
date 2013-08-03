package bap.texas.util;

public class PlayerStatus {

  public static final long NONE = 0;
  // active states
  public static final long DEALER = 1;
  public static final long SMALL_BLIND = 2;
  public static final long BIG_BLIND = 4;
  public static final long ANTE = 8;

  public static final long SHILL = 16;
  public static final long SEEN = 32;
  public static final long SNG_PRESENCE = 64;
  public static final long MTT_PRESENCE = 128;

  // 3 longermediate states
  public static final long FOLDED = 256;
  public static final long ALLIN = 512;
  public static final long OPTOUT = 1024;
  public static final long ABPOSTED = 2048;

  // 4 inactive states 16 -32
  public static final long NEW = 4096;
  public static final long BROKE_OUT = 8192;
  public static final long WAIT_FOR_BLINDS = 16384;
  public static final long SINGLE_MOVE = 32768;

  public static final long SITOUTNEXTGAME = 65536;
  public static final long ALLIN_ADJUSTED = 131072;
  public static final long MISSED_BB = 262144;
  public static final long MISSED_SB = 524288;

  public static final long REMOVED = 1048576;
  public static final long SITTINGOUT = 2097152;
  public static final long SITBETBLINDS = 4194304;
  public static final long BROKE = 8388608;

  //7
  public static final long RESP_REQ = 16777216;
  public static final long RAISE_REQ = 33554432;
  public static final long SHOWDOWN = 67108864;
  public static final long WINLOSSVIOLATED = 134217728;

  //8
  public static final long VOTED_OFF = 268435456;
  public static final long DISCONNECTED = 536870912;
  public static final long RECONNECTED = 1073741824;
  public static final long MORNING = 2147483648L;
  
  public static final long AFTERNOON = 4294967296L;
  public static final long EVENING = 8589934592L;

  // filter out sitin, fold, optout, all-in, removed, broke, sittingout,
  public static final long M_INACTIVE = 0x18F06700; //keeping all-in player as inactive
  public static final long M_RESET =    0x07DFDF0F0;

  private static String[] strings = new String[] {
      "dealer", "small-blind", "big-blind", "ante",
      "shill", "seen", "sngpresence", "mttpresence",
      "folded", "allin", "optout", "ab-posted",
      "new", "broke-out", "wait-for-blinds", "single-move",
      "sitoutnextgame", "allin-adj", "missed-bb", "missed-sb",
      "removed", "sittingout", "sitting-bet-blinds", "broke",
      "resp-req", "raise_req", "showdown", "win-loss-violated",
      "voted-off", "disconnected", "reconnected", "morning",
      "afternoon", "evening", "u1"
  };

  static long[] values = new long[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824, 2147483648L,
      4294967296L, 8589934592L, 17179869184L, 34359738368L
  };

  private long intVal;

  private PlayerStatus(long intVal) {
    this.intVal = intVal;
  }

  public static long intValue(String str) {
    for (int i = 0; i < strings.length; i++) {
      if (str.equals(strings[i])) {
        return values[i];
      }
    }
    return -1;
  }

  public static String stringValue(long mvId) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (mvId == values[i]) {
        return strings[i];
      }
      else if ( (mvId & values[i]) == values[i]) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      return "";
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static boolean idValid(long mvId) {
    if (mvId > 12 || mvId < 0) {
      return false;
    }
    return true;
  }

  public static boolean isSitOut(long status)
  {
	  return (status & PlayerStatus.M_INACTIVE)==SITTINGOUT;
  }
  public static boolean isAllIn(long status)
  {
	  return (status & PlayerStatus.M_INACTIVE)==ALLIN;
  }
  public static boolean isFold(long status)
  {
	  return (status & PlayerStatus.M_INACTIVE)==FOLDED;
  }
  
  public static boolean isNoRequest(long status)
  {
	  return (status & PlayerStatus.ABPOSTED) == 0;
  }
  
  public static boolean isRequest(long status)
  {
	  return (status & PlayerStatus.M_INACTIVE) == 0;
  }
  
  public static boolean isOptout(long status){
	  return (status & PlayerStatus.OPTOUT) == 0;
  }
  
  public static boolean isActive(long status) {
    return (status & PlayerStatus.M_INACTIVE) == 0; // filter out sitin, fold, optout, all-in, removed, broke, sittingout,
  }

  public static boolean isNew(long status) {
    return (status & PlayerStatus.NEW) > 0;
  }

  public static boolean isShill(long status) {
    return (status & PlayerStatus.SHILL) > 0;
  }

  public static boolean isBetweenBlinds(long status) {
    return (status & PlayerStatus.SITBETBLINDS) > 0;
  }

  public long intVal() {
    return intVal;
  }

  // implement this method ...
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public int hashCode() {
    return super.hashCode();
  }




  public static void main(String[] argv) {
    System.out.println("Player Status =" + stringValue(33556484));
   // System.out.println("Player Status =" + stringValue(0x8602700));
   // System.out.println("Player Status =" + stringValue(4096));
    System.out.println("Player Status =" + stringValue(16779272));
    System.out.println("Player Status =" + stringValue(2056));
  }


}
