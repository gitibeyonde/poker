package com.golconda.db;

public class ModuleType {

            public static final int POKER = 1;
            public static final int CASINO = 2;
            public static final int BINGO = 4;
            public static final int REGIONAL = 8;
            
            public static final int CARS = 16;            
            public static final int WEBCAM = 32;          
            public static final int JIVE = 64;        
            public static final int BLUESPADE = 128;
            
            
            public static final int LOBBY = 256;
         
          private static String[] strings = new String[] {
              "poker", "casino", "bingo", "regional",
              "cars", "webcam", "jive", "bluespade",
              "lobby", "u9", "u10", "u11"
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

          public ModuleType(int intVal) {
            this.intVal = intVal;
          }
          
          public int intVal(){
              return intVal;
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

          public boolean isPoker() {
            return (intVal & ModuleType.POKER)> 0;
          }

        public boolean isCasino() {
          return (intVal & ModuleType.CASINO)> 0;
        }

    public boolean isRegional() {
      return (intVal & ModuleType.REGIONAL)> 0;
    }
          public String toString(){
              return stringValue(intVal);
          }

}
