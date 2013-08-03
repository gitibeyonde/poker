package com.golconda.message;

import com.golconda.game.util.Card;

import java.util.HashMap;
import java.util.Iterator;


public class GameEvent {
  protected HashMap _ge;

  public GameEvent() {}
  

  public GameEvent(GameEvent ge) {
    _ge = ge._ge;
  }

  public GameEvent(String ge) {
    init(ge);
  }

  public void init(String ge) {
    _ge = new HashMap();
    if (ge==null || ge.equals("null")) {
      return;
    }
    String[] nv = ge.toString().split(",");
    int i = 0;
    for (; i < nv.length; i++) {
      int ind = nv[i].indexOf("=");
      if (ind == -1) {
        continue;
      }
      String name = nv[i].substring(0, ind).trim();
      String value = nv[i].substring(ind + 1).trim();
      //System.out.println("Game Event name = " + name + ",   value = " + value);
      _ge.put(name, value);
    }
  }

  public String get(String name) {
    return (String) _ge.get(name);
  }
    public String getKeno() {
       return (String) _ge.get("Keno");
     }


     public String[] getCasinoPlayer() {
       String p = (String) _ge.get("player-details");
       if (p == null || p.length() <= 1) {
         return null;
       }
       String plr_det_elem[] = p.split("\\|");
       return plr_det_elem;
     }

     public String[] getCasinoDealer(){
       String p = (String) _ge.get("dealer");
       if (p == null || p.length() <= 1) {
         return null;
       }
       String plr_det_elem[] = p.split("\\|");
       return plr_det_elem;
     }

     public String getResult() {
       return (String) _ge.get("result");
     }
     
  public String[][] getPlayerDetails() {
    String plrs[][] = null;
    try {
      String p = (String) _ge.get("player-details");
      if (p.length() <= 1) {
        return null;
      }
      String plrs_det[] = p.split("`");
      plrs = new String[plrs_det.length][10];
      for (int i = 0; i < plrs_det.length; i++) {
        String str = new String(plrs_det[i]);
        String plr_det_elem[] = str.split("\\|");
        //System.out.println(str);
        for (int j = 0; j < plr_det_elem.length; j++) {
          plrs[i][j] = plr_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return plrs;
  }
  
  public void setPlayerDetails(String str)
  {
	  _ge.put("player-details", str);
  }


  public String[][] getCricPlayerDetails() {
    String plrs[][] = null;
    try {
      String p = (String) _ge.get("player-details");
      if (p.length() <= 1) {
        return null;
      }
      String plrs_det[] = p.split("`");
      plrs = new String[plrs_det.length][8];
      for (int i = 0; i < plrs_det.length; i++) {
        String str = new String(plrs_det[i]);
        String plr_det_elem[] = str.split("\\|");
        System.out.println(str);
        for (int j = 0; j < plr_det_elem.length; j++) {
          plrs[i][j] = plr_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return plrs;
  }

  public String[] getWaitersDetails() {
    String wtr_str = (String) _ge.get("waiters");
    if (wtr_str != null) {
      return wtr_str.split("`");
    }
    else {
      return null;
    }
  }

  public String[][] getTournyWinners() {
    String plrs[][] = null;
    try {
      String winners_det[] = ((String) _ge.get("winners")).split("`");
      plrs = new String[winners_det.length][3];
      for (int i = 0; i < winners_det.length; i++) {
        String str = new String(winners_det[i]);
        String plr_det_elem[] = str.split("\\|");
        for (int j = 0; j < plr_det_elem.length; j++) {
          plrs[i][j] = plr_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return plrs;
  }

  public int getTournyState() {
    String ts = (String) _ge.get("state");
    if (ts != null) {
      return Integer.parseInt(ts);
    }
    return -1;
  }

  public int getTargetPosition() {
    String ts = (String) _ge.get("target-position");
    if (ts != null) {
      return Integer.parseInt(ts);
    }
    return -1;
  }

  public String getPlayerDetailsString() {
    String pd = ((String) _ge.get("player-details"));
    //System.out.println("GE=" + toString() + "\nP=" + pd +  "\nP1=" + (String) _ge.get("player-details1"));
    if (pd == null) {
      pd = "none";
    }
    return pd;
  }

  public String[][] getMove() {
    String mvs[][] = null;
    try {
      String mvs_det[] = ((String) _ge.get("next-move")).split("`");
      mvs = new String[mvs_det.length][3];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }

  public void setNextMove(String nm) {
    _ge.put("next-move", nm);
  }

  public String[][] getCricMove() {
    String mvs[][] = null;
    try {
      String mvs_det[] = ((String) _ge.get("next-move")).split("`");
      mvs = new String[mvs_det.length][4];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }

  public String getNextMoveString() {
    String pd = ((String) _ge.get("next-move"));
    if (pd == null) {
      pd = "none";
    }
    return pd;
  }

  public String getIllegalMoveString() {
    String illMove = null;
    illMove = ((String) _ge.get("illegal-move"));
    return illMove;
  }

  public String getLastMoveString() {
    String pd = ((String) _ge.get("last-move"));
    if (pd == null) {
      pd = "none";
    }
    return pd;
  }

  public double getRaiseAmount() {
    double amt = 0;
    String[] lmv = ((String) _ge.get("last-move")).split("\\|");
    //System.out.println("Last move name =" + lmv[1] + " of amount " + lmv[2]);
    String last_move_position = lmv[0];
    String current_move_position = (String) _ge.get("target-position");
    if (current_move_position == null) {
      return 0;
    }
    String last_player_bet = "0";
    String current_player_bet = "0";
    String[][] plr_det = getPlayerDetails();
    for (int i = 0; i < plr_det.length; i++) {
      if (last_move_position.equals(plr_det[i][0])) {
        last_player_bet = plr_det[i][2];
      }
      if (current_move_position.equals(plr_det[i][0])) {
        current_player_bet = plr_det[i][2];
      }
    }
    //System.out.println("Last player bet = " + last_player_bet +  "  Current player bet = " + current_player_bet);
    amt = Double.parseDouble(last_player_bet) -
          Double.parseDouble(current_player_bet);
    return amt;
  }

  public String[] getRegisteredPlayer() {
    return ((String) _ge.get("registered-player")).split("\\|");
  }

  public String[][] getOpenHands(){
    String mvs[][] = null;
    try {
      String mvs_det[] = ((String) _ge.get("open-hands")).split("`");
      mvs = new String[mvs_det.length][2];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }

  public String[][] getWinner() {
    String mvs[][] = null;
    try {
      String mvs_det[] = ((String) _ge.get("winner")).split("`");
      mvs = new String[mvs_det.length][7];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }

  public String getWinnerString() {
    return ((String) _ge.get("winner"));
  }

  public int getBettingRound() {
  try {
    return Integer.parseInt(((String) _ge.get("round")));
  }
  catch(Exception e){
      return -1;
  }
  }
      
  public Card[] getCommunityCards() {
    Card c[] = null;
    try {
      String str = ((String) _ge.get("community-cards")).trim();
      if (str.length() < 2) {
        return null;
      }
      String[] c_str = str.split("'");
      c = new Card[c_str.length];
      for (int i = 0; i < c_str.length; i++) {
        c[i] = new Card(c_str[i]);
        c[i].setIsOpened(true);
      }
    }
    catch (Exception e) {
        //e.printStackTrace();
    }
    return c;
  }
    
    public Card[] getPrevCommunityCards() {
        Card c[] = null;
        try {
        String str = ((String) _ge.get("prev_community-cards")).trim();
        if (str.length() < 2) {
          return null;
        }
        String[] c_str = str.split("'");
        c = new Card[c_str.length];
        for (int i = 0; i < c_str.length; i++) {
          c[i] = new Card(c_str[i]);
          c[i].setIsOpened(true);
        }
        }
        catch (Exception e) {
          //e.printStackTrace();
        }
        return c;
    }
    
  public Card[] getHand(String[] hand) {
    Card c[] = null;
    try {
      String[] c_str = hand[1].split("`");
      c = new Card[c_str.length];
      for (int i = 0; i < c_str.length; i++) {
        c[i] = new Card(c_str[i]);
      }
    }
    catch (Exception e) {
        //e.printStackTrace();
    }
    return c;
  }

  public Card[] getHand() {  // this is being used by poker hand
    Card c[] = null;
    try {
      String[] c_str = get("hand").split("'");  
      c = new Card[c_str.length];
      for (int i = 0; i < c_str.length; i++) {
        c[i] = new Card(c_str[i]);
        c[i].setIsOpened(true);
      }
    }
    catch (Exception e) {
        //e.printStackTrace();
    }
    return c;
  }


  public Card[] getCasinoHand() {  // this is being used by poker hand
    Card c[] = null;
    try {
      String[] hand = get("hand").split("\\|");
      String[] c_str = hand[1].split("'");  
      c = new Card[c_str.length];
      for (int i = 0; i < c_str.length; i++) {
        c[i] = new Card(c_str[i]);
      }
    }
    catch (Exception e) {
        //e.printStackTrace();
    }
    return c;
  }

  public int getDealtCards() {
	String p[][] = getPlayerDetails();
    if (p == null) {
      return -1;
    }
    int cl = 0;
    try {
      for (int i = 0; i < p.length; i++) {
        int ccl = p[i][9].split("'").length;
        if (ccl > cl) {
          cl = ccl;
        }
      }
    }
    catch (Exception e) {

    }
    return cl;
  }

  public String[] getHandParams() {
    String hand = ( (String) _ge.get("hand"));
    if (hand != null && hand.length() > 0) {
      String[] h_str = hand.split("\\|");
      return h_str;
    }
    return null;
  }

  public String[] getDealerHandParams() {
    String hand = ( (String) _ge.get("dealer"));
    if (hand != null && hand.length() > 0) {
      String[] h_str = hand.split("\\|");
      return h_str;
    }
    return null;
  }

  public Card[] getDealerHand() {
    Card c[] = null;
    String hand = ( (String) _ge.get("dealer"));
    if (hand != null && hand.length() > 0) {
      String[] h_str = hand.split("\\|");
      String[] c_str = h_str[1].split("`");
      c = new Card[c_str.length];
      for (int i = 0; i < c_str.length; i++) {
        c[i] = new Card(c_str[i]);
      }
    }
    return c;
  }
      
  public double getRake() {
    String r = (String) get("rake");
    return r != null ? Double.parseDouble(r) : 0;
  }

  public double getMinBet() {
    String r = (String) get("min-bet");
    return r != null ? Double.parseDouble(r) : -1;
  }

  public double getMaxBet() {
    String r = (String) get("max-bet");
    return r != null ? Double.parseDouble(r) : -1;
  }

  public String getTournyId() {
    return (String) _ge.get("tournyid");
  }
  
  public String getGameName() {
    return (String) _ge.get("name");
  }

  public int getGameRunId() {
    int dp = -1;
    String dps = (String) _ge.get("grid");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public int getMsgGid() {
    int dp = -1;
    String dps = (String) _ge.get("msgGID");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public int getResponseId() {
    int dp = -1;
    String dps = (String) _ge.get("response-id");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public String getPotString() {
    return (String) _ge.get("pots");
  }

  public String[][] getPot() {
    String pts[][] = null;
    try {
      String pts_det[] = ((String) _ge.get("pots")).split("`");
      pts = new String[pts_det.length][2];
      for (int i = 0; i < pts_det.length; i++) {
        String str = new String(pts_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          pts[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return pts;
  }

  public int getDealerPosition() {
    int dp = -1;
    String dps = (String) _ge.get("dealer-pos");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public int getMaxPlayers() {
    return Integer.parseInt((String) _ge.get("max-players"));
  }

  public int getMinPlayers() {
    return Integer.parseInt((String) _ge.get("min-players"));
  }

  public String[] getPartners() {
    String[] partners = null;
    String plyrs = ((String) _ge.get("partners"));
    if (!(plyrs == null || plyrs.endsWith("null")) && plyrs.length() > 1) {
      partners = plyrs.split("\\|");
    }
    return partners;
  }

  public int getType() {
    String type = (String) _ge.get("type");
    if (type != null) {
      return Integer.parseInt(type);
    }
    else {
      return -1;
    }
  }

  
  public boolean getGameState() {
	  boolean bool = false;
	  String type = (String) _ge.get("running");
	  if(type.equals("true"))bool = true;
      return bool;
  }
  
  public String toString() {
    Iterator e = _ge.keySet().iterator();
    StringBuilder str = new StringBuilder();
    for (; e.hasNext(); ) {
      String key = (String) e.next();
      str.append(key).append("=").append(_ge.get(key)).append(",");
    }
    return str.toString();
  }

  public static void main(String[] args) {

    String str = " ";
    String[] c_str = str.split("'");
    Card[] c = new Card[c_str.length];
    for (int i = 0; i < c_str.length; i++) {
      c[i] = new Card(c_str[i]);
      System.out.println(c[i]);
    }

    /**
        String plrs_det[] = new String("1|23.90|3.00|p1|16777|0|__'__`1|23.90|3.00|p1|16777|0|").split("`");

          String[][] plrs = new String[plrs_det.length][7];
          for (int i = 0; i < plrs_det.length; i++) {

            String str = new String(plrs_det[i]);
            System.out.println(str);
            String plr_det_elem[] = str.split("\\|");
            for (int j = 0; j < plr_det_elem.length; j++) {
              System.out.println(plr_det_elem[j]);
              plrs[i][j] = plr_det_elem[j];
            }
          }**/
  }

}
