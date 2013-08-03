package bap.texas.util;

public class PlayerPreferences {

  // active states
  public static final int BIGSYMBOLS = 1;
  public static final int AUTOPOSTBLIND = 2;
  public static final int WAITFORBIGBLIND = 4;
  public static final int SHOWBESTCARDS = 8;

  public static final int MUCKLOSINGCARDS = 16;
  public static final int RANDOMDELAY = 32;
  public static final int SHILL = 64;
  public static final int AFFILIATE = 128;

  public static final int CHAT_PLAYER = 256;
  public static final int CHAT_WIN = 512;
  public static final int CHAT_GENERAL = 1024;
  public static final int CHAT_DEALER = 2048;

  public static final int CHAT_CARDS = 4096;
  public static final int CHAT_HAND = 8192;
  public static final int SOUND = 16384;
  public static final int ANIMATION = 32768;

  public static final int PLAYER_TAGGING = 65536;
  public static final int BRING_ALL_TO_TABLE = 131072;
  public static final int ADMIN = 262144;
  public static final int u4 = 524288;



  /// THESE PREFERENCES ARE USED BY SERVER
  public static final int DISABLE_CHAT = 1048576;
  public static final int BANNED_PLAYER = 2097152;
  public static final int UU1 = 4194304;
  public static final int UU2 = 8388608;

  //7
  public static final int GOLD_PLAYERS = 16777216;
  public static final int SILVER_PLAYERS = 33554432;
  public static final int COPPER_PLAYERS = 67108864;
  public static final int VIP = 134217728;


  public static final int PLAYER_PREF_MASK=0xFFFFF;
  public static final int DEFAULT_MASK=0;
  public static final int GROUPED_PLAYERS=0xF000000;

  private static String[] strings = new String[] {
      "big-symbols", "autopost-blind", "wait-for-bb", "show-best-cards",
      "muck-loosing-hand", "random-delay", "shill", "",
      "player-chat", "win-chat", "general-chat", "dealer-chat",
      "cards-chat", "chat-hand", "sound", "animation",
      "player-tagging", "bring all to table", "admin", "u7",
      "disable", "banned", "u10", "u11",
      "Gold", "Silver", "Copper", "VIP",
      "u8", "u9", "u10", "u11"
  };

  static int[] values = new int[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824};

  private int intVal;

  public PlayerPreferences(int intVal) {
    this.intVal = intVal;
  }
  

  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (intVal == values[i]) {
        return strings[i];
      }
      else if ( (intVal & values[i]) == values[i]) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      return "";
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static int intValue(String str) {
    for (int i = 0; i < strings.length; i++) {
      if (str.equals(strings[i])) {
        return values[i];
      }
    }
    return 0;
  }

  public static String stringValue(int mvId) {
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

  public static boolean idValid(int mvId) {
    if (mvId > 12 || mvId < 0) {
      return false;
    }
    return true;
  }

  public static boolean isAutoPostBlinds(int status) {
    return (status & PlayerPreferences.AUTOPOSTBLIND)> 0;
  }

  public static boolean isMuckCards(int status) {
    return (status & PlayerPreferences.MUCKLOSINGCARDS) > 0;
  }

  public static boolean isWaitForBB(int status) {
    return (status & PlayerPreferences.WAITFORBIGBLIND) >0;
  }

  public static boolean isSound(int status) {
    return (status & PlayerPreferences.SOUND)> 0;
  }

  public static boolean isAnimate(int status) {
    return (status & PlayerPreferences.ANIMATION)> 0;
  }

  public static boolean isDisableChat(int status) {
    return (status & PlayerPreferences.DISABLE_CHAT) >0;
  }

  public static boolean isBannedPlayer(int status) {
    return (status & PlayerPreferences.BANNED_PLAYER) >0;
  }


  public static void main(String[] argv) {
    //System.out.println("Player Status =" + stringValue(2096896));
    System.out.println("Player Status =" + stringValue(130946));
    //int i = 2227970 & ~PlayerPreferences.BANNED_PLAYER;
    //System.out.println(i + "Player Status =" + stringValue(i));
//    System.out.println("Player Status =" + stringValue(1179394));
  }

  public int intVal() {
    return intVal;
  }

  // implement this method ...
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public int hashCode() {
    return super.hashCode();
  }
  

  public  boolean isBannedPlayer() {
    return (intVal & PlayerPreferences.BANNED_PLAYER) >0;
  }



}
