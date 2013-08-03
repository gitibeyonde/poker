package com.poker.server;

import com.agneya.util.Configuration;
import com.agneya.util.Utils;

import com.golconda.db.*;
import com.golconda.game.Game;

import com.golconda.net.NWServer;

import com.poker.common.db.DBInitGames;
import com.poker.common.db.DBInitGames.GameRow;
import com.poker.common.db.DBInitGames.MTTRow;
import com.poker.common.db.DBInitGames.SitNGoRow;
import com.poker.game.PokerGameType;
import com.poker.game.poker.Poker;
import com.poker.game.poker.TournyController;
import com.poker.game.poker.pokerimpl.Holdem;
import com.poker.game.poker.pokerimpl.HoldemSitnGo;
import com.poker.game.poker.pokerimpl.OmahaHi;
import com.poker.game.poker.pokerimpl.OmahaHiLo;
import com.poker.game.poker.pokerimpl.OmahaSitnGo;
import com.poker.game.poker.pokerimpl.RealHoldem;
import com.poker.game.poker.pokerimpl.RealHoldemSitnGo;
import com.poker.game.poker.pokerimpl.RealOmahaHi;
import com.poker.game.poker.pokerimpl.RealOmahaHiLo;
import com.poker.game.poker.pokerimpl.RealOmahaSitnGo;
import com.poker.game.poker.pokerimpl.RealStudHi;
import com.poker.game.poker.pokerimpl.RealStudHiLo;
import com.poker.game.poker.pokerimpl.RealTermHoldem;
import com.poker.game.poker.pokerimpl.Stud;
import com.poker.game.poker.pokerimpl.StudHiLo;
import com.poker.game.poker.pokerimpl.TermHoldem;
import com.poker.net.GameController;
import com.poker.nio.Acceptor;
import com.poker.shills.BotSchedule;

import java.io.File;
import java.io.FileInputStream;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


/**
 * PokerServer
 *
 * The PokerServer runs three main threads.  The first
 * is the user interface thread.  It gives you a simple
 * interface that responds to text commands, and quit.
 * The second is the ClientManager Thread.  It creates a
 * SocketServer and waits...  The last is a maintenance thread
 * that cleans up dead games and the such.
 */
public class GameServer {
  // set the category for logging
  static Logger _cat = Logger.getLogger(GameServer.class.getName());

  // set the configuration object
  static Configuration _conf;

  public static int _state;
  public static ConcurrentHashMap _chat_subs;
  static String _rootpath = System.getProperty("CONFDIR");

  public final static int STARTING = 0;
  public final static int NORMAL = 1;
  public final static int SHUTTING = 2;
  public final static int SUSPEND = 3;
  public final static int STOPPED = 4;

  private static Acceptor _a = null;
  public static int _client_version = 10000;

  public static void main(String[] args) throws Exception {
    System.out.println(
        "Started Game Server Version - 2.5 (Major update in Feb 2011)  on " +
        new java.util.Date(System.currentTimeMillis()));

    // start the server and set the state as normal
    _state = STARTING;
    _conf = Configuration.instance();
    
    doHomeKeeping();
    
    //start the admin server
    NWServer serv = new NWServer("com.poker.net.CommandProcessor");
    serv.startServer(_conf.getInt("Admin.Network.port"));
    _client_version = _conf.getInt("Client.version");

    // initialize the curse word hashtable
    _chat_subs = new ConcurrentHashMap();
    Properties curses = new Properties();
    curses.load(new FileInputStream(new File(_rootpath + "chat_subs.properties")));
    for (Enumeration e = curses.keys(); e.hasMoreElements(); ) {
      Object key = e.nextElement();
      _chat_subs.put(key, curses.get(key));
    }

    createGamesFromConf();
    createSitnGoFromConf();
    createTournyFromMainConf();

    //createGamesFromDB();
    //createSitnGoFromDB();
    //createTournyFromDB();

    BotSchedule bs = BotSchedule.getInstance();
    bs.startScheduler();

    // start the nio server
    startAcceptorThread();

    GameController gc = new GameController(Configuration.instance().getProperty(
      "Admin.Network.server.ip"), Configuration.instance().getInt(
        "Admin.Network.port"));
    //gc.consoleLoop();
    //System.exit(0);

  }


    public static void doHomeKeeping(){
        try {
          DBTransactionScratchPad[] dbt = DBTransactionScratchPad.fetch(1);
          for (int i=0;i<dbt.length;i++){
              DBPlayer dbp = new DBPlayer();
              dbp.get(dbt[i]._userid);
              dbp.chipsHouseKeeping(dbt[i]._game_name, dbt[i]._game_type, dbt[i]._module, dbt[i]._play, dbt[i]._real, dbt[i]._session);
          }
        }
        catch (DBException e){
            _cat.info(e.getMessage());
        }
    }


  public static boolean isShutting() {
    return _state == SHUTTING;
  }

  public static void startAcceptorThread() throws Exception {
    // start the poker server thread which listens to the poker clients
    _a = new Acceptor();
    _a.startServer("com.poker.server.GameProcessor");
    _state = NORMAL;
  }

  public static void stopAcceptorThread() throws Exception {
    if (_a != null) {
      _a.stopServer();
      _state = SHUTTING;
    }
  }



  public static String createGames(String type, String name, double minbet,
                                double maxbet, int minp, int maxp, int rake,
                                double maxRake[], String[] affiliate,
                                String[] partner, int rank, String stack) throws DBException {

    Poker g = null;
    LogObserver lob = LogObserver.instance();
    DBGame dbg = null;
    int gameTypeId = -1;
    double sb[] = Utils.integralDivide(minbet, 2);
    double ante[] = Utils.integralDivide(minbet, 5);
    if (type.equals("Holdem")) {
      g = new Holdem(name, minp, maxp, rake, maxRake, affiliate, partner,
                     lob);
      ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
      gameTypeId = PokerGameType.Play_Holdem;
      _cat.finest(name + "  affiliate=" + affiliate[0]) ;
      if ("bot".equals(affiliate[0])){
          gameTypeId = PokerGameType.BOT_TABLE | PokerGameType.Play_Holdem;
          g.type(new PokerGameType(gameTypeId));
      }
      else  if ("random".equals(affiliate[0])){
          gameTypeId = PokerGameType.RANDOM_BOT_TABLE | PokerGameType.Play_Holdem;
          g.type(new PokerGameType(gameTypeId));
      }
    }
    else if (type.equals("TermHoldem")) {
        g = new TermHoldem(name, minp, maxp, rake, maxRake, affiliate, partner,
                       lob);
        ((TermHoldem) g).setArgs(minbet, maxbet, sb[1], minbet);
        gameTypeId = PokerGameType.Play_Holdem;
      }
    else if (type.equals("OmahaHi")) {
      g = new OmahaHi(name, minp, maxp, rake, maxRake, affiliate, partner,
                      lob);
      ((OmahaHi) g).setArgs(minbet, maxbet, sb[1], minbet);
      gameTypeId = PokerGameType.Play_OmahaHi;
    }
    else if (type.equals("OmahaHiLo")) {
      g = new OmahaHiLo(name, minp, maxp, rake, maxRake, affiliate, partner,
                        lob);
      ((OmahaHiLo) g).setArgs(minbet, maxbet, sb[1], minbet);
      gameTypeId = PokerGameType.Play_OmahaHiLo;
    }
    else if (type.equals("StudHi")) {
      g = new Stud(name, minp, maxp, rake, maxRake, affiliate, partner, lob);
      ((Stud) g).setArgs(minbet, maxbet, ante[1], minbet);
      gameTypeId = PokerGameType.Play_Stud;
    }
    else if (type.equals("StudHiLo")) {
      g = new StudHiLo(name, minp, maxp, rake, maxRake, affiliate, partner,
                       lob);
      ((StudHiLo) g).setArgs(minbet, maxbet, ante[1], minbet);
      gameTypeId = PokerGameType.Play_StudHiLo;
    }
    else if (type.equals("RealHoldem")) {
      g = new RealHoldem(name, minp, maxp, rake, maxRake, affiliate,
                         partner, lob);
      ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
      gameTypeId = PokerGameType.Real_Holdem;
        _cat.finest(name + "  affiliate=" + affiliate[0]) ;
        if ("bot".equals(affiliate[0])){
            gameTypeId = PokerGameType.BOT_TABLE | PokerGameType.Real_Holdem;
            g.type(new PokerGameType(gameTypeId));
        }
        else  if ("random".equals(affiliate[0])){
            gameTypeId = PokerGameType.RANDOM_BOT_TABLE | PokerGameType.Real_Holdem;
            g.type(new PokerGameType(gameTypeId));
        }
    }
    else if (type.equals("RealTermHoldem")) {
        g = new RealTermHoldem(name, minp, maxp, rake, maxRake, affiliate,
                           partner, lob);
        ((Holdem) g).setArgs(minbet, maxbet, sb[1], minbet);
        gameTypeId = PokerGameType.Real_Holdem;
    }
    else if (type.equals("RealOmahaHi")) {
      g = new RealOmahaHi(name, minp, maxp, rake, maxRake, affiliate,
                          partner, lob);
      ((RealOmahaHi) g).setArgs(minbet, maxbet, sb[1], minbet);
      gameTypeId = PokerGameType.Real_OmahaHi;
    }
    else if (type.equals("RealOmahaHiLo")) {
      g = new RealOmahaHiLo(name, minp, maxp, rake, maxRake, affiliate,
                            partner, lob);
      ((RealOmahaHiLo) g).setArgs(minbet, maxbet, sb[1], minbet);
      gameTypeId = PokerGameType.Real_OmahaHiLo;
    }
    else if (type.equals("RealStudHi")) {
      g = new RealStudHi(name, minp, maxp, rake, maxRake, affiliate,
                         partner, lob);
      ((RealStudHi) g).setArgs(minbet, maxbet, ante[1], minbet);
      gameTypeId = PokerGameType.Real_Stud;
    }
    else if (type.equals("RealStudHiLo")) {
      g = new RealStudHiLo(name, minp, maxp, rake, maxRake, affiliate,
                           partner, lob);
      ((RealStudHiLo) g).setArgs(minbet, maxbet, ante[1], minbet);
      gameTypeId = PokerGameType.Real_StudHiLo;
    }

    if (rank != -1) {
      g.setRank(rank);
    }

    _cat.info("Game created " + g);
    return name;
  }


  public static void createGamesFromConf() throws Exception {
    String[] game_map = {
        "TermHoldem", "Holdem", "OmahaHi", "OmahaHiLo", "StudHi", "StudHiLo",
        "RealTermHoldem", "RealHoldem", "RealOmahaHi", "RealOmahaHiLo", "RealStudHi",
        "RealStudHiLo", "MonteCarloHoldem",
    };
    for (int i = 0; i < game_map.length; i++) {
      int gn = 1;
      String game_str = game_map[i] + "." + "Game" + gn;
      //_cat.finest(game_str + "=" + (String) _conf.get(game_str));
      while ( (String) _conf.get(game_str) != null) {
        int game_count = _conf.getInt(game_str);
        //_cat.finest(game_str + " Game count = " + game_count);
        String game_name = (String) _conf.get(game_str + ".Name");
        double minbet = _conf.getDouble(game_str + ".Bet.Min");
        double maxbet = _conf.getDouble(game_str + ".Bet.Max");
        int minP = _conf.getInt(game_str + ".MinP");
        int maxP = _conf.getInt(game_str + ".MaxP");
        int rake = _conf.getInt(game_str + ".Rake");
        //double maxRake = _conf.getDouble(game_str + ".MaxRake");
        String mrs = (String) _conf.get(game_str + ".MaxRake");
        String []mral = mrs.split("\\|");
        double maxRake[] = new double[maxP];
        int j=0, k=0;
        for (;k<mral.length && j<maxP;k++, j++){
          maxRake[j]=Double.parseDouble(mral[k]);
          j++;
          if (j>=maxP)break;
          maxRake[j]=Double.parseDouble(mral[k]);
        }
        for (int l=k;l<maxP;l++){
          maxRake[l]=maxRake[k];
        }

        String aff_arr = (String) _conf.get(game_str + ".Affiliate");
        String frnd_arr = (String) _conf.get(game_str + ".Partners");
        int rank = _conf.getInt(game_str + ".RANK");

        if (frnd_arr != null && frnd_arr.trim().length() < 2) {
          frnd_arr = null;
        }
        if (aff_arr != null && aff_arr.trim().length() < 2) {
          aff_arr = null;

        }
        DecimalFormat df = new DecimalFormat("000");
        
        for (int n = 0; n < game_count; n++){
        	if (game_count > 1){
                createGames(game_map[i], df.format(n) + " " + game_name, minbet, maxbet, minP, maxP,
                        rake, maxRake, aff_arr != null ? aff_arr.split("\\|") : null,
                        frnd_arr != null ? frnd_arr.split("\\|") : null, rank, "normal");
        	}
        	else {
        		createGames(game_map[i], game_name, minbet, maxbet, minP, maxP,
                         rake, maxRake, aff_arr != null ? aff_arr.split("\\|") : null,
                         frnd_arr != null ? frnd_arr.split("\\|") : null, rank, "normal");
        	}
        }
        gn++;
        game_name = game_map[i] + "_" + gn;
        game_str = game_map[i] + "." + "Game" + gn;
        _cat.finest(game_str + "=" + (String) _conf.get(game_str));
      }
    }
  }


  public static String createSitnGo(String name, int type, int limit, int tourbo, double buyIn,
                                 double fees, double chips, int maxP, String don) throws DBException {
    Game g = null;
    LogObserver lob = LogObserver.instance();
    String aff[] = {
                   "admin"};
    if (type == PokerGameType.HoldemSitnGo) {
      g = new HoldemSitnGo(name, 2, maxP, limit, tourbo, aff, lob);
      ((HoldemSitnGo) g).setArgs(buyIn, fees, chips, don.equals("yes") ? true : false);
    }
    else if (type == PokerGameType.OmahaHiSitnGo) {
      g = new OmahaSitnGo(name, 2, maxP, limit, tourbo, aff, lob);
      ((OmahaSitnGo) g).setArgs(buyIn, fees, chips, don.equals("yes") ? true : false);
    }
    else if (type == PokerGameType.Real_HoldemSitnGo) {
        g = new RealHoldemSitnGo(name, 2, maxP, limit, tourbo, aff, lob);
        ((RealHoldemSitnGo) g).setArgs(buyIn, fees, chips, don.equals("yes") ? true : false);
      }
    else if (type == PokerGameType.Real_OmahaHiSitnGo) {
        g = new RealOmahaSitnGo(name, 2, maxP, limit, tourbo, aff, lob);
        ((RealOmahaSitnGo) g).setArgs(buyIn, fees, chips, don.equals("yes") ? true : false);
      }

    _cat.finest("Game created " + g + "--" + buyIn + ", " + fees);
    return name;

  }


  public static String createSitnGo(String name, String type, int limit, int tourbo, double buyIn,
                                 double fees, int maxP) throws DBException {
    Game g = null;
    LogObserver lob = LogObserver.instance();
    String aff[] = {
                   "admin"};
    if (type.equals("HoldemSitnGo")) {
      g = new HoldemSitnGo(name, 2, maxP, limit, tourbo, aff, lob);
      ((HoldemSitnGo) g).setArgs(buyIn, fees, 1500, false);
    }
    else if (type.equals("OmahaHiSitnGo")) {
      g = new OmahaSitnGo(name, 2, maxP, limit, tourbo, aff, lob);
      ((OmahaSitnGo) g).setArgs(buyIn, fees, 1500, false);
    }

    _cat.finest("Game created " + g + "--" + buyIn + ", " + fees);
    return name;

  }
  
  public static String createSitnGo(SitNGoRow sgr) throws DBException {
    String name = sgr.gameName;
    double buyIn = sgr.buyin;
    double fees = sgr.fees;
    int pcount = sgr.maxPlayers;
    int limit = sgr.limit;
    int tourbo = sgr.tourbo;
    int type = sgr.gameType;
    double chips = sgr.chips;
    String don = sgr.don;
    _cat.finest("Created sitngo " + name + type);
    return createSitnGo(name, type, limit, tourbo, buyIn, fees, chips, pcount, don);
  }

  //public static String createSitnGo(String name, String type, int limit, int tourbo, double buyIn,
     //     double fees, int maxP) throws DBException {
  
  public static void createSitnGoFromConf() throws Exception {
	  String[] game_map = {
		        "HoldemSitnGo", "OmahaSitnGo", "Real_HoldemSitnGo", "Real_OmahaSitnGo",
		    };
		    for (int i = 0; i < game_map.length; i++) {
		      int gn = 1;
		      String game_str = game_map[i] + "." + "Game" + gn;
		      _cat.finest(game_str + "=" + (String) _conf.get(game_str));
		      while ( (String) _conf.get(game_str) != null) {
		        int game_count = _conf.getInt(game_str);
		        _cat.finest(game_str + " Game count = " + game_count);
		        String game_name = (String) _conf.get(game_str + ".Name");
		        int buyIn = _conf.getInt(game_str + ".BuyIn");
		        int limit = _conf.getInt(game_str + ".Limit");
		        int turbo = _conf.getInt(game_str + ".Tourbo");
		        int fees = _conf.getInt(game_str + ".Fees");
		        int maxP = _conf.getInt(game_str + ".MaxP");

		        
		        for (int n = 0; n < game_count; n++,
		        	createSitnGo(game_name, game_map[i], limit, turbo, buyIn, fees, maxP)) {
		          ;
		        }
		        gn++;
		        game_name = game_map[i] + "_" + gn;
		        game_str = game_map[i] + "." + "Game" + gn;
		        _cat.fine(game_str + "=" + (String) _conf.get(game_str));
		      }
		    }
  }
  

public static void createTournyFromConf() throws Exception {
    DBInitGames dbi = new DBInitGames();
    MTTRow[] mt = dbi.getMTT();

    for (int i = 0; i < mt.length; i++) {
      if (mt[i].gameState == 0) {
        continue;
      }
      _cat.finest(mt[i].toString());
      createTourny(mt[i]);
    }
  }

  public static String createTourny(String name, int type, String tourny_type, String gauranteed, double prize_pool, int limit, int tourbo,
                                 int[] schedule, double buyin, double fees, int chips,
                                 int maxP, int decl, int reg, int join) throws
      DBException {

    LogObserver lob = LogObserver.instance();
    TournyController tc = TournyController.instance();
    return tc.addTourny(name, type, limit, tourbo,  schedule, buyin, fees, chips, maxP, decl, reg, join, lob);
  }

  public static String createTourny(MTTRow mt) throws DBException {
    String name = mt.gameName;
    LogObserver lob = LogObserver.instance();
    TournyController tc = TournyController.instance();
    _cat.finest("Created tourny " + mt.gameName);
    return tc.addTourny(mt.gameName, mt.gameType, mt.limit, mt.tourbo, mt.schedule(),
                        mt.buyin, mt.fees, mt.chips, mt.maxPlayers, mt.declInt,
                        mt.regInt, mt.joinInt, lob);

  }
  
  
  public static void createTournyFromMainConf() throws DBException {
        int i=1;
        String game_str = "Tournament" + i;
        while ( (String) _conf.get(game_str) != null) {
          String name = (String)_conf.get(game_str);
          _cat.finest(game_str + " Tname = " + name);

          int type =  _conf.getInt(game_str + ".Type");
          int tourbo =  _conf.getInt(game_str + ".Tourbo");
          int limit =  _conf.getInt(game_str + ".Limit");
          int maxp =  _conf.getInt(game_str + ".MaxP");
          int chips =  _conf.getInt(game_str + ".Chips");
          double buyin =_conf.getDouble(game_str + ".BuyIn");
          double fees =_conf.getDouble(game_str + ".Fees");
          String ssch = (String)_conf.get(game_str + ".Schedule");
          String s[] = ssch.split(":");
          int sch[] = new int[5];
          sch[0] = Integer.parseInt(s[0]);
          sch[1] = Integer.parseInt(s[1]);
          sch[2] = Integer.parseInt(s[2]);
          sch[3] = Integer.parseInt(s[3]);
          sch[4] = Integer.parseInt(s[4]);
          
          int joinInt = _conf.getInt(game_str + ".Schedule.JoinInterval");
          createTourny(name, type, "normal", "no", 0.0, limit, tourbo, sch, buyin, fees, chips, maxp, joinInt, joinInt, joinInt);
          game_str = "Tournament" + ++i;
      }
  }
  
  /**
   *  Create games from DB tables
   */
  
  
  public static void createGamesFromDB() throws DBException {
      DBInitGames dbg = new DBInitGames();
      GameRow[] gv = dbg.getGames();
      LogObserver lob = LogObserver.instance();
      double maxRake[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      for (int i=0;i<gv.length;i++){
    	  GameRow game = gv[i];
          if (game.maxPlayers == -1 || game.gameType <= 0) continue; // non poker game
              String type=null;
              if (game.gameType == 1) type="Holdem";
              else if(game.gameType == 2) type="OmahaHi";
              else if(game.gameType == 4) type="OmahaHiLo";
              else if(game.gameType == 8) type="StudHi";
              else if(game.gameType == 16) type="StudHiLo";
              else if(game.gameType == 32) type="TermHoldem";
              else if (game.gameType == 256) type="RealHoldem";
              else if(game.gameType == 512) type="RealOmahaHi";
              else if(game.gameType == 1024) type="RealOmahaHiLo";
              else if(game.gameType == 2048) type="RealStudHi";
              else if(game.gameType == 4096) type="RealStudHiLo";
              else if(game.gameType == 8192) type="RealTermHoldem";
              else throw new IllegalStateException("Unknown type = " + game.gameType);
              _cat.info("Creating game from DB " + game);
              String [] aff = { "admin"};
              createGames(type, game.gameName, game.minRaise, game.maxBet, game.minPlayers, game.maxPlayers,
                              0, maxRake, aff, game.players, 0, game.stack);
      }
      
  }
  
  
  public static void createSitnGoFromDB() throws Exception {
	    DBInitGames dbi = new DBInitGames();
	    SitNGoRow[] sgr = dbi.getSitNGo();

	    for (int i = 0; i < sgr.length; i++) {
	    	_cat.finest("Gettign sitngo " + sgr[i]);
	      //if (sgr[i].gameState == 0) {
	       // continue;
	      //}
	      createSitnGo(sgr[i]);
	    }
	  }

	 public static void createTournyFromDB() throws Exception {
	    DBInitGames dbi = new DBInitGames();
	    MTTRow[] mt = dbi.getMTT();

	    for (int i = 0; i < mt.length; i++) {
	    	_cat.finest("Gettign tourny " + mt[i]);
	      //if (mt[i].gameState == 0) {
	      //  continue;
	      //}
	      _cat.finest(mt[i].toString());
	      createTourny(mt[i]);
	    }
	  }

} // end PokerServer
