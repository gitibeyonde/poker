package com.golconda.db;

public class RoleMask {

      public static final int PLAYER = 1;
      public static final int AFFILIATE = 2;
      public static final int U1 = 4;
      public static final int U2 = 8;
    
      public static final int ADMIN = 16;
      public static final int USER_ADMIN = 32;
      public static final int FINANCE_REPORTER = 64;
      public static final int RISK_MANAGER = 128;
    
      public static final int FINANCE_TASKS = 256;
      public static final int CS_TASKS = 512;
      public static final int SETTINGS = 1024;
      public static final int GAMES_MANAGER = 2048;
    
      public static final int ITEM_MANAGER = 4096;
      public static final int CS1 = 8192;
      public static final int CS2 = 16384;
      public static final int CS3 = 32768;
    
      public static final int RS0 = 65536;
      public static final int RC1 = 131072;
      public static final int RC2 = 262144;
      public static final int RC3 = 524288;

    public static final int ADMIN_MASK = 0XFFF2;
      

      private static String[] strings = new String[] {
          "Player", "Affiliate", "u10", "u11",
          "Admin", "User Admin", "Reports(Finance)", "Risk Management",
          "Finance(Tasks)", "CS(Tasks)", "Settings", "Game Manager",
          "Item Manager", "u9", "u10", "u11",
          "uu", "u9", "u10", "u11"
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

      public RoleMask(int intVal) {
        this.intVal = intVal;
      }
      
      public int intValue(){
          return intVal;
      }
      
    public String toString() {
      StringBuilder buf = new StringBuilder();
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
        StringBuilder buf = new StringBuilder();
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

      public static boolean isAffiliate(int status) {
        return (status & RoleMask.AFFILIATE)> 0;
      }
      
      public  boolean isAffiliate() {
          return (intVal & RoleMask.AFFILIATE)> 0;
      }
      
      public boolean isAdmin() {
        return (intVal & RoleMask.ADMIN)> 0;
      }

      public boolean isUserAdmin() {
        return (intVal & RoleMask.USER_ADMIN)> 0;
      }

      public boolean isFinanceReporter() {
        return (intVal & RoleMask.FINANCE_REPORTER)> 0;
      }

      public boolean isRiskManager() {
        return (intVal & RoleMask.RISK_MANAGER)> 0;
      }

      public boolean isFinanceAdmin() {
        return (intVal & RoleMask.FINANCE_TASKS)> 0;
      }

      public boolean isCSAgent() {
        return (intVal & RoleMask.CS_TASKS)> 0;
      }

      public boolean isSettingsManager() {
        return (intVal & RoleMask.SETTINGS)> 0;
      }

      public boolean isGamesManager() {
        return (intVal & RoleMask.GAMES_MANAGER)> 0;
      }


      public boolean isItemManager() {
        return (intVal & RoleMask.ITEM_MANAGER)> 0;
      }

      public boolean isAnyAdmin() {
        return (intVal & RoleMask.ADMIN_MASK)> 0;
      }

}
