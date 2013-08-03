package bap.texas.common.message;

import java.util.*;

public class TournyEvent {
  Hashtable<String, String> _thash;
  String _t;

  public TournyEvent(String t) {
    _t = t;
    _thash = new Hashtable<String, String>();
    String[] nv = t.split(",");
    int i = 0;
    for (; i < nv.length; i++) {
      int ind = nv[i].indexOf("=");
      if (ind == -1) {
        continue;
      }
      String name = nv[i].substring(0, ind).trim();
      String value = nv[i].substring(ind + 1).trim();
      //System.out.println("Game Event name = " + name + ",   value = " + value);
      _thash.put(name, value);
    }
  }
  
  public String get(String s){
      return (String) _thash.get(s);
  }

  public String name() {
    return (String) _thash.get("name");
  }

  public int state() {
    return Integer.parseInt( (String) _thash.get("state"));
  }

  public Date date() throws java.text.ParseException {
    //24 Aug 2004 06:05:32 GMT = d MMM yyyy HH:mm:ss Z"
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
        "yyyy.MM.dd-HH:mm Z");
    return sdf.parse( (String) _thash.get("date"));
  }

  public double getBuyIn() {
    String bi = ( (String) _thash.get("buy-in"));
    return Double.parseDouble(bi);
  }

  public double getFees() {
    String bi = ( (String) _thash.get("fees"));
    return Double.parseDouble(bi);
  }
      

  public double getPool() {
    String bi = ( (String) _thash.get("pool"));
    return Double.parseDouble(bi);
  }
    public int getPlayerCount() {
          String plrs = (String) _thash.get("player");
          if( plrs == null ) {
              return 0;
          }
          
          String[] players =  plrs.split("\\|");
        return players.length;
      }
       
  public String toString() {
    return _t;
  }

  public static void main(String[] args) throws Exception {
    String ds = "24 Aug 2004 06:05:32 GMT";
    String df = "d MMM yyyy HH:mm:ss Z";
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(df);
    Date d = sdf.parse(ds);
    System.out.println(d);

    Date cd = new Date();

    System.out.println(cd.getTime() - d.getTime());

  }

}
