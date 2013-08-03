package com.poker.game.util;

import com.agneya.util.LongOps;
import com.agneya.util.Rng;
import com.agneya.util.Utils;

import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;

import com.poker.game.PokerGameType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Calendar;
import java.util.Comparator;
import java.util.logging.Logger;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
/*Calcualtes the odds of each hand winning given the hands and gametype */
public class PokerOddPageGenerator {
  static Logger _cat = Logger.getLogger(PokerOddPageGenerator.class.getName());

  public Player[] getPlayerHands(String p_hands) {
    String cards[] = p_hands.split("\\|");
    int pc = cards.length / 2;
    Player[] ph = new Player[pc];
    for (int i = 0; i < pc; i++) {
      long h = Hand.getHandFromStr2(cards[i * 2] + "," + cards[ (i * 2) +
                                    1]);
      ph[i] = new Player(h, i);
    }
    return ph;
  }

  public static Hand getCommHand(String chand) {
    return new Hand(Hand.getHandFromStr2(chand.replace('|', ',')));
  }

  public static Player[] getProbability(Player prob[]) {
    double sum = 0;
    for (int i = 0; i < prob.length; i++) {
      sum = sum + prob[i]._winCount;
    }
    for (int i = 0; i < prob.length; i++) {
      prob[i]._percent_win = Utils.getRounded( (prob[i]._winCount * 100.00) /
                                              sum);
    }
    // sort the all-in players in ascending order of  their position
    java.util.Arrays.sort(prob, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ( ( (Player) o1)._pos - ( (Player) o2)._pos);
      }
    });
    return prob;
  }

  public Player[] calcProbability(String phands, String Chand, int gametype) {
    Player[] pH = getPlayerHands(phands);
    Hand cH;
    if (Chand.equals("")) {
      cH = new Hand();
    }
    else {
      cH = getCommHand(Chand);
    }
    _cat.finest(phands);
    _cat.finest(Chand);
    PokerOdds(pH, cH, gametype);
    return getProbability(pH);
  }

  public static long gettime() {
    return Calendar.getInstance().getTimeInMillis();
  }

  public void PokerOdds(Player[] ph, Hand ch, int gametype) {
    //_cat.finest("CH=" + ch.getAllCardsString());
    if (ch.cardCount() >= 3) { //after flop
      Deck deck = new Deck();
      for (int k = 0; k < ph.length; k++) {
        deck.remove(ph[k]._hand);
      }
      deck.remove(ch.cards);

      while (deck.size() > 0) {
        Hand sh = ch.copy();
        long dc = deck.drawCards(1);
        if (Math.random() > 0.99) {
          continue;
        }
        sh.addOpenCard(dc);
        //_cat.finest("SH Adding=" + sh.getAllCardsString());
        if (sh.cardCount() == 5) {
          findWinners(ph, sh, gametype);
        }
        else if (sh.cardCount() == 4) {
          Deck deck2 = new Deck();
          for (int k = 0; k < ph.length; k++) {
            deck2.remove(ph[k]._hand);
          }
          deck2.remove(sh.cards);
          while (deck2.size() > 0) {
            Hand sh2 = sh.copy();
            dc = deck2.drawCards(1);
            if (Math.random() > 0.8) {
              continue;
            }
            sh2.addOpenCard(dc);
            //_cat.finest("SH2 Adding=" + sh2.getAllCardsString());
            findWinners(ph, sh2, gametype);
          }
        }
        else {
          _cat.finest("FATAL");
        }
      }
    }
    else if (ch.cardCount() == 0) { // pre flop
      for (int i = 0; i < 200 / ph.length; i++) {
        Deck deck = new Deck();
        for (int k = 0; k < ph.length; k++) {
          deck.remove(ph[k]._hand);
        }
        while (deck.size() >= 5) {
          long dc = deck.drawRandomCards(5);
          Hand sh = new Hand(dc);
          //_cat.finest("SH Adding=" + sh.getAllCardsString());
          findWinners(ph, sh, gametype);
        }
      }
    }
    else {
      _cat.finest("FATAL");
    }
  }

  public void findWinners(Player[] v, final Hand cc, int gt) {
    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) HandComparator.compareGameHand( ( (Player) o2)._hand,
                                      ( (Player) o1)._hand,
                                      cc.getCards(), PokerGameType.HOLDEM, true)[0];
      }
    });

    double winner_count = 1.00;
    for (int i = 0; i < v.length - 1; i++) {
      if (HandComparator.compareGameHand(v[i]._hand,
                           v[i + 1]._hand,
                           cc.getCards(), PokerGameType.HOLDEM, true)[0] != 0L) {
        break;
      }
      winner_count++;
    }

    double win = 1.00 / winner_count;
    for (int i = 0; i < winner_count; i++) {
      v[i].incrWinCount(win);
    }
  }

  public void generatePages() throws IOException {
    String dirname="C:/tools/eclipse/workspace/octopus/web/map/hand_strength/";



    int idx=0;
     int r=0;int cten=0;
    boolean close=false;
    String findex;
    PrintWriter ipw=null;
    findex = dirname + "index0.htm";
    ipw = new PrintWriter(new BufferedWriter(new FileWriter(
            findex)));

    for (int i=0;i<3900;i++){
      int p = i/100;
      int q = i/10;
      if (r != q){
        r = q;
        cten=0;
      }
     Deck d = new Deck();
     long comm = d.drawRandomCards(5);
     long pc = d.drawRandomCards(2);
     long bcards[] = HandComparator.bestHandOf5(pc, comm, 1);
     Card[] crds = CardUtils.toCardsArray(bcards[0], 0xFFFFFFFFFL);
     Cards best_combination = new Cards(false);
     best_combination.addCards(crds);
     String hs= HandOps.getHandValue(bcards[0], true);

     System.out.println(best_combination.openStringValue());
     System.out.println(hs);
     String fname=dirname + search_string[q] + "_" + cten + ".htm";

     StringBuilder html= new StringBuilder();
     html.append("<head>");

     html.append("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
     html.append("</head>");

     html.append("<body bgcolor=\"#CC6633\" leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">");

    html.append("<TABLE width=800 height=\"25\" border=0 align=center cellPadding=0 cellSpacing=0 background=\"images/Nav_BG.gif\" summary=\"winner poker player menu\">");
    html.append("          <TR> ");
    html.append("            <TD width=\"100\"> <DIV align=center><a href=\"/\" title=\"Poker Home\" class=\"GreenLinks\">Poker Home</a></DIV></TD>");
    html.append("            <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("            <TD width=120> <DIV align=center><a href=\"gettingstarted.html\" title=\"Start to Play Poker\" class=\"GreenLinks\">Start to Play</a></DIV></TD>");
    html.append("            <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("            <TD width=100> <DIV align=center><a href=\"poker-school.html\" title=\"Poker School\" class=\"GreenLinks\">Poker School</a></DIV></TD>");
    html.append("           <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("            <TD width=90> <DIV align=center><a href=\"poker-tournaments.html\" title=\"Poker Tournaments\" class=\"GreenLinks\">Tournaments</a></DIV></TD>");
    html.append("            <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("            <TD> <DIV align=center><a href=\"http://www.doingpoker.com\" title=\"Play Free Poker\" class=\"GreenLinks\"><strong>Winner ");
    html.append("              Poker Player is a play-for-free website</strong></a></DIV></TD>");
    html.append("          </TR>");
    html.append("      </TABLE> ");

     html.append("<p>&nbsp;</p>");
     html.append("<table border=\"0\"  width=\"600\" id=\"table1\" cellspacing=\"0\" cellpadding=\"0\" background=\"../img/pokcaltable.gif\" height=\"318\">");
     html.append("        <!-- MSTableType=\"nolayout\" -->");
     html.append("        <tr>");
     html.append("                <td>&nbsp;</td>");
     html.append("                <td width=\"44\">&nbsp;</td>");
     html.append("                <td width=\"128\">&nbsp;</td>");
     html.append("                <td width=\"42\">&nbsp;</td>");
     html.append("                <td>&nbsp;</td>");
     html.append("        </tr>");
     html.append("        <tr>");
     html.append("                <td>&nbsp;</td>");
     html.append("                <td width=\"44\">");

    Card[] comm_crds = CardUtils.toCardsArray(comm, 0xFFFFFFFFFL);
    html.append(cardNameString(comm_crds[0]));
    html.append("</td><td width=\"128\">");
    html.append(cardNameString(comm_crds[1]));
    html.append(cardNameString(comm_crds[2]));
    html.append(cardNameString(comm_crds[3]));
    html.append("</td><td width=\"42\">");
    html.append(cardNameString(comm_crds[4]));


    html.append("      <td>&nbsp;</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("      <td height=\"92\">&nbsp;</td>");
    html.append("      <td width=\"44\" height=\"92\">&nbsp;</td>");
    html.append("      <td width=\"128\" height=\"92\" valign=\"bottom\">");

    Card[] pc_crds = CardUtils.toCardsArray(pc, 0xFFFFFFFFFL);
    html.append(cardNameString(pc_crds[0]));
    html.append(cardNameString(pc_crds[1]));


    html.append("    </td><td width=\"42\" height=\"92\">&nbsp;</td>");
    html.append("    <td height=\"92\">&nbsp;</td>");
    html.append("</tr>");
    html.append("</table>");
    html.append("<p>&nbsp;</p>");
    html.append("<p><font size=\"4\">Hand Strength:</font> <u><i><font size=\"4\">");

    html.append(hs);

    html.append("</font></i></u></p>");
    html.append("    <p>&nbsp;</p>");
    html.append("    <table border=\"0\" width=\"47%\" id=\"table2\" cellspacing=\"0\" cellpadding=\"0\">");
    html.append("            <!-- MSTableType=\"nolayout\" -->");
    html.append("            <tr>");
    html.append("                    <td width=\"114\"><font size=\"4\">Combination:</font></td>");
    html.append("                    <td>");
    html.append("                    <p align=\"center\">");



    html.append(cardNameString(crds[0]));
    html.append(cardNameString(crds[1]));
    html.append(cardNameString(crds[2]));
    html.append(cardNameString(crds[3]));
    html.append(cardNameString(crds[4]));


    html.append(" </td>");
    html.append("            </tr>");
    html.append("    </table>");
    html.append("&nbsp;&nbsp;&nbsp;<p><i><font size=\"1\">");
    for (int j=0;j<10;j++){
      html.append("<a href=\""+ search_string[p+j] + "_" + cten + ".htm" + "\">" + search_string[(p + j)] + "</a>");
    }
    html.append("</font></i></p>");

    html.append("<table border=\"0\" cellpadding=\"0\" align=\"center\" cellspacing=\"0\" summary=\"winner poker player main table\">");
    html.append("       <tr> ");
    html.append("              <td align=\"left\">");
    html.append("                <a href=\"http://www.doingpoker.com\" class=\"BigLinks\" title=\"Click Here to play poker\"><strong>Click Here</strong></a> ");
    html.append("                <a href=\"http://www.doingpoker.com\"><img src=\"images/spade.gif\" alt=\"Play Poker Now\" width=\"40\" height=\"51\" border=\"0\"></a>");
    html.append("               </td>");
    html.append("            </tr>");
    html.append("     </table> ");



    html.append("     <TABLE width=800 height=\"25\" border=0 align=\"center\" cellPadding=0 cellSpacing=0 background=\"images/Nav_BG.gif\" summary=\"winner poker player menu\">");
    html.append("            <TR> ");
    html.append("              <TD width=\"100\"> <DIV align=center><a href=\"poker-hands.html\" class=\"GreenLinks\" title=\"Poker Hands\">Poker Hands</a></DIV></TD>");
    html.append("              <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("              <TD width=120> <DIV align=center><a href=\"poker-affiliates.html\" class=\"GreenLinks\" title=\"Poker Affiliates\">Poker Affiliates</a></font></DIV></TD>");
    html.append("              <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("              <TD width=100> <DIV align=center><a href=\"poker-links.html\" class=\"GreenLinks\" title=\"Poker Links\">Poker Links</a></font></DIV></TD>");
    html.append("              <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("              <TD width=90> <DIV align=center><a href=\"play-poker.html\" class=\"GreenLinks\" title=\"Play Poker\">Play Poker </a></font></DIV></TD>");
    html.append("              <TD width=5><IMG height=22 alt=\"\" src=\"images/Nav_Break.gif\" width=5></TD>");
    html.append("              <TD> <DIV align=center><a href=\"http://www.doingpoker.com\" class=\"GreenLinks\" title=\"Wiiner Poker playeris a play for free site\"><strong>Winner ");
    html.append("                  Poker Player is a play-for-free website</strong></a></DIV></TD>");
    html.append("            </TR>");
    html.append("    </TABLE>");


    PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(fname)));
    pw.print(html.toString());
    pw.close();

    ipw.print("<a href=\""+ search_string[q] + "_" + cten + ".htm" + "\">" + search_string[q] + "</a>\n\r");

    if (idx != p){
      ipw.close();
      idx = p;
      findex = dirname + "index" + idx + ".htm";
      ipw = new PrintWriter(new BufferedWriter(new FileWriter(
          findex)));
    }
    cten++;
   }
  }


  public String cardNameString(Card c){
    String str="<img border=\"0\" src=\"../img/Large/";
    if (c.getSuit()==c.CLUBS){
      str+="Club/";
    }else if(c.getSuit()==c.DIAMONDS){
      str+="Diamond/";
    }else if(c.getSuit()==c.HEARTS){
      str+="Heart/";
    }else if(c.getSuit()==c.SPADES){
      str+="Spade/";
    }

    str += c.getRankChar(c.getRank());

    return str + ".gif\" width=\"42\" height=\"59\">";
  }

  public class Player {
    public long _hand;
    public int _pos;
    public double _winCount;
    public double _percent_win;

    public Player(long h, int p) {
      _hand = h;
      _pos = p;
      _winCount = 0;
    }

    public void incrWinCount(double i) {
      _winCount += i;
    }

    public String toString() {
      return _percent_win + "(" + _pos + ")";
    }
  }

  public class Deck {

    protected long _deck;
    protected int _pos = 0;

    public Deck() {
      _deck = Long.valueOf(
          "0000000000001111111111111111111111111111111111111111111111111111", 2).
          longValue();
    }

    // @todo : make true random
    public long drawCards(int count) {
      long hand = 0L;
      while (count > 0) {
        assert _deck != 0:" Trying to draw cards from an empty deck";
        long mask = 1L << _pos;
        if ( (mask & _deck) == 0) {
          ;
        }
        else {
          hand |= mask;
          --count; _deck &= ~mask;
        }
        _pos++;
      }
      return hand;
    }

    // @todo : make true random
    protected long drawRandomCards(int count) {
      Rng rng = new Rng();
      long hand = 0L;
      while (count > 0) {
        assert _deck != 0:" Trying to draw cards from an empty deck";
        int pos =
            rng.nextIntBetween(0, 52);
        long mask = 1L << pos;
        if ( (mask & _deck) == 0) {
          ;
        }
        else {
          hand |= mask;
          --count; _deck = _deck & (~mask);
        }
      }
      //_cat.finest("Drawing cards " + hand);
      return hand;
    }

    public void remove(long cards) {
      _deck &= ~cards;
    }

    public int size() {
      return LongOps.getHighs(_deck);
    }

  }

  public static void main(String[] argv) throws Exception {
    long now = System.currentTimeMillis();
    PokerOddPageGenerator ppg = new PokerOddPageGenerator();
    ppg.generatePages();

    System.out.println(System.currentTimeMillis() - now);

  }

  private static final String []search_string = {
      "poker", "online poker", "strip poker", "poker chips", "party poker",
      "play poker", "poker game", "video poker", "poker table", "free poker",
      "pacfic poker", "internet poker",
      "poker room", "poker rule", "free online poker", "casino poker",
      "online poker game", "paradise poker",
      "poker tournament", "poker star", "world series of poker", "poker site",
      "world poker tour", "holdem poker",
      "poker strategy", "texas holdem poker", "poker hands", "full tilt poker",
      "free poker game",
      "online poker rooms", "poker software", "empire poker", "absolute poker",
      "play online poker",
      "free strip poker", "pai gow poker", "poker stars.net", "gambling poker",
      "joker poker", "poker download",
      "poker forum", "texas hold em poker", "live poker", "pacific poker",
      "online poker review", "poker superstars",
      "free video poker", "poker odds", "online poker site", "poker card",
      "free texas holdem poker",
      "online poker cheat", "texas poker", "online video poker",
      "play video poker",
      "the outcast of poker flat by bret harte", "poker book",
      "free online poker game", "world poker", "poker guide",
      "poker money", "clay poker chips", "poker blog", "poker card game",
      "poker tip", "fantasy poker chips",
      "stud poker", "online poker tournament", "party poker bonus",
      "party poker bonus code", "poker video",
      "video strip poker", "hold em poker", "poker supply",
      "caribbean stud poker", "poker chip set",
      "online casino poker", "draw poker", "play free poker",
      "multiplayer poker", "custom poker chips", "poker titan",
      "black jack poker", "poker table supply", "play free poker online",
      "fulltilt poker", "caribbean poker", "how to build a poker table",
      "online strip poker", "video poker game",
      "free texas holdem poker download", "video poker home",
      "three card poker",
      "virtual poker", "free poker tournament", "how to win at poker",
      "poker site en.wikipedia.org", "strip poker game",
      "poker set", "yahoo poker", "casino online", "casino", "casino internet",
      "casino game", "online casino gambling", "casino free game",
      "casino free", "best casino online", "casino las vegas",
      "casino gambling", "casino on net", "casino consultant", "casino poker",
      "casino gambling internet",
      "casino vegas", "casino gaming", "bonus casino", "atlantic casino city",
      "casino free game online", "casino free online",
      "casino slot", "casino harrahs", "best casino online", "casino grand",
      "casino royale", "casino rama", "casino morongo",
      "casino virtual", "casino pechanga", "online casino game",
      "casino mohegan sun", "casino on line", "casino reno",
      "casino foxwoods", "casino hotel", "casino online review",
      "casino gambling internet online", "casino deposit no",
      "casino chip", "casino windsor", "casino eagle soaring",
      "casino hotel las vegas", "casino niagara", "casino download",
      "online gambling casino", "casino tropicana", "casino stone turning",
      "best casino gambling online", "casino portal",
      "casino strategy", "casino pala", "best casino directory online",
      "casino horseshoe", "book casino sport", "casino station",
      "best casino", "casino mississippi", "casino winstar", "casino indiana",
      "casino hard rock", "bonus casino online",
      "casino guide", "casino offshore", "argosy casino", "casino palm",
      "casino lake mystic", "casino hooters", "casino tunica",
      "casino online top", "casino louisiana", "casino hollywood",
      "casino gambling online", "casino venetian", "casino indian",
      "casino player", "casino show tv", "casino directory", "casino fallsview",
      "casino links", "capri casino isle", "casino jackpot",
      "ameristar casino", "casino falls niagara", "black casino jack",
      "california casino", "baccarat casino", "arizona casino",
      "barona casino", "casino gamble", "bonus casino deposit no",
      "casino free slot", "casino online slot", "casino roulette",
      "casino net", "casino foxwood", "casino malibu.ru r.php",
      "casino online poker", "casino resort", "casino charles lake",
      "atlantic casino city hotel", "black jack", "jack black",
      "online black jack", "internet black jack", "play black jack",
      "free black jack", "black jack online", "black jack game",
      "casino black jack", "black jack gambling", "black jack strategy",
      "black jack rule", "play black jack online free",
      "online casino black jack", "free black jack game", "black jack poker",
      "strip black jack", "free online black jack", "black jack tip",
      "virtual black jack", "black jack pizza", "java black jack",
      "black casino jack machine online slot", "yourbestonlinecasino.com",
      "black jack table", "roulette black jack craps",
      "play free black jack", "black jack download", "roulette black jack",
      "free online poker", "online poker game",
      "online poker rooms", "play online poker", "online poker review",
      "online poker site", "online poker cheat",
      "online video poker", "free online poker game", "online poker tournament",
      "online casino poker", "play free poker online",
      "online strip poker", "casino machine online online poker room slot",
      "yourbestonlinecasino.com", "free online strip poker",
      "online video poker game", "online roulette poker", "live online poker",
      "texas holdem poker online", "online poker tool",
      "free online video poker", "online poker gambling", "poker tournament",
      "online poker tournament", "free poker tournament",
      "las vegas poker tournament", "texas holdem poker tournament strategy",
      "free online poker tournament", "atlantic city poker tournament",
      "freeroll poker tournament", "internet poker tournament", "party poker",
      "party poker bonus", "party poker bonus code",
      "party poker cheat", "free party poker", "bonus code deposit party poker",
      "party poker.com", "home poker party",
      "party poker com", "party poker download", "bonuscodes party poker",
      "party poker net", "party poker.net", "party poker sign up bonus code",
      "cheat money party play poker", "party poker code", "strip poker party",
      "party poker scanner", "online party poker",
      "party poker deposit bonus", "party poker signup bonus",
      "planning a poker party", "free party poker money",
      "free party poker", "party poker deposit code", "paradise poker",
      "bonus code paradise poker", "paradise poker scam",
      "net paradise poker", "cheat paradise poker", "download paradise poker",
      "bonus paradise poker", "poker room",
      "online poker rooms", "casino machine online online poker room slot",
      "yourbestonlinecasino.com", "online poker room review",
      "poker room review", "free poker rooms", "strip poker",
      "free strip poker", "video strip poker", "online strip poker",
      "strip poker game", "free online strip poker", "free strip poker game",
      "play online poker", "play video poker",
      "play free poker", "play free poker online", "learn how to play poker",
      "play money poker", "play free video poker",
      "online poker game", "free poker game", "free online poker game",
      "poker card game", "video poker game", "strip poker game",
      "free video poker game", "online video poker game",
      "betting directsportbetcom game poker sports", "free strip poker game",
      "texas holdem poker game", "poker game download", "crazy game of poker",
      "free poker game download", "internet poker game",
      "free texas holdem poker game", "poker game rule", "home poker game",
      "yahoo poker game", "poker casino game", "free video poker",
      "online video poker", "play video poker", "poker video",
      "video strip poker", "video poker game", "video poker home",
      "free video poker game", "online video poker game",
      "free online video poker", "video poker betting", "video poker machine",
      "free video strip poker", "video poker strategy", "play free video poker",
      "texas holdem poker", "free texas holdem poker",
      "free texas holdem poker download", "texas holdem poker online",
      "texas holdem poker strategy", "texas holdem poker game",
      "texas holdem poker tournament strategy", "texas holdem poker rule",
      "free online texas holdem poker", "free texas holdem poker game",
      "texas holdem poker table", "online gambling", "online casino gambling",
      "internet casino gambling online",
      "online gambling casino", "best online casino gambling",
      "online gambling bonus", "gambling casino online",
      "online gambling site", "online sports gambling",
      "online casino gambling directory", "online gambling resource",
      "online betting sports gambling", "free gambling money for online casino",
      "free online gambling", "online roulette gambling",
      "online gambling news", "online gambling directory",
      "online gambling software", "online gambling game",
      "online gambling guide",
      "online gambling portal", "football online gambling",
      "best online gambling", "black casino gambling jack online online poker",
      "room yourbestonlinecasino.com", "online poker gambling",
      "online gambling strategy",
      "black gambling jack machine online online slot",
      "yourbestonlinecasino.com",
      "black black casino gambling jack jack online",
      "yourbestonlinecasino.com", "online gambling index",
      "online gambling offer", "gambling online r",
      "gambling online recommendation", "gambling getting online started",
      "online gambling legal", "gambling gambling online online resource",
      "online casino gambling sports betting",
      "online gambling rule", "online gambling forum",
      "online gambling payouts",
  };

}
