package com.onlinepoker.models;

import com.golconda.message.GameEvent;
import com.onlinepoker.Copyable;
import com.onlinepoker.SharedConstants;
import com.onlinepoker.Validable;
import com.onlinepoker.ValidationConstants;
import com.poker.common.interfaces.SitnGoInterface;
import com.poker.common.message.TournyEvent;
import com.poker.game.PokerGameType;


public class LobbyTableModel

    implements java.io.Serializable, PokerConstants {


  public static final int OFFLINE = 0;
  public static final int ONLINE = 1;
  public static final int CLOSING = 2;
  public static final int DELETED = 3;
  public static final int PURGED = 4;

  public final static int TABLE_OPEN = 1;
  public final static int HAND_RUNNING = 2;
  public final static int TABLE_CLOSED = 4;


  protected String name;
  protected boolean gameStatus;
  protected int gameType;
  protected int numPlayers;
  protected int playerCount = 0;
  protected double rakeRate = 0;
  protected double shortMaxRake = 0;
  protected double longMaxRake = 0;
  protected int gameLimitType ;




protected double minBet = 0;
  protected double maxBet = 0;
  protected double minBuyIn = 0;
  protected double maxBuyIn = 0;
  protected double flop = 0;
  protected double plrperflop = 0;
  protected boolean isProPlayer = false;
  private String stack = null;
  protected int chips = 0;
  
  protected double fee = 0; // the correct name for the former buyCasino
  protected double prizePool;
  private String roomSkinClassName = null;
  private int state;
  protected int tourny_state;
  protected double handsPerHour = 0;
  protected double averagePot = 0;
  private int waiterCount = 0;
  private boolean isDeleted = false;
  private String bench_state;
  private boolean isPrivate = false;
  private int userId;
  public String[][] _players;
  public String[] _waiters;
  public String[] _partners;
  
  
  public LobbyTableModel() {

  }



  public LobbyTableModel(GameEvent ge) {
	name = ge.getGameName();
    gameType = ge.getType();
    minBet = ge.getMinBet();
    maxBet = ge.getMaxBet();
    numPlayers = ge.getMaxPlayers();
    if(ge.get("running")!= null)
    {
    	gameStatus = ge.getGameState();
    }
    if(ge.getPlayerDetails()!= null)
    {
    	playerCount = ge.getPlayerDetails().length;
    	_players = ge.getPlayerDetails();
    }
    if(ge.getPartners() != null)_partners = ge.getPartners();
    if(new PokerGameType(gameType).isTPoker())
    {
    	bench_state = ge.get("bench");
    }
    state=ONLINE;
    this.fee = 0;

  }

  public LobbyTableModel(TournyEvent ge) {
	  	name = ge.get("name");
	    state=ONLINE;
	    fee = ge.getFees();
	  }
  public LobbyTableModel(LobbyTableModel model) {
    copy(model);
  }


  public LobbyTableModel( String name, int gameType, boolean playRealMoney,int numPlayers, double rakeRate, double shortMaxRake,
                         double longMaxRake, int gameLimitType,double minBuyIn,double maxBuyIn,double fee,double avgPot,double handsPerHour
                         ,double flop, boolean isPro, String stack, int chips) {

   
    this.name = name;
    this.gameType = gameType;
    this.numPlayers = numPlayers;
    this.rakeRate = rakeRate;
    this.shortMaxRake = shortMaxRake;
    this.longMaxRake = longMaxRake;
    this.gameLimitType = gameLimitType;
    this.minBuyIn = minBuyIn;
    this.maxBuyIn = maxBuyIn;
    this.fee = fee;
    this.averagePot = avgPot;
    this.handsPerHour = handsPerHour;
    this.flop = flop;
    this.isProPlayer = isPro;
    this.stack = stack;
    this.chips = chips;
  }



  /**

   * Copies.

   */

  public void copyWithoutSettings(LobbyTableModel model) {
    name = model.name;
    gameType = model.gameType;
    numPlayers = model.numPlayers;
    playerCount = model.playerCount;
    rakeRate = model.rakeRate;
    shortMaxRake = model.shortMaxRake;
    longMaxRake = model.longMaxRake;
    gameLimitType = model.gameLimitType;
    minBuyIn = model.minBuyIn;
    maxBuyIn = model.maxBuyIn;
    fee = model.fee;
    prizePool = model.prizePool;
    roomSkinClassName = model.roomSkinClassName;
    state = model.state;
    handsPerHour = model.handsPerHour;
    averagePot = model.averagePot;
    flop = model.flop;
    isProPlayer = model.isProPlayer;
    stack = model.stack;
    userId = model.userId;
    waiterCount = model.waiterCount;
  }



  public void copy(LobbyTableModel model) {
    copyWithoutSettings(model);
    settings = model.settings != null ? ( (Copyable) model.settings).makeCopy() : null;
    printSettings();
  }



  private void printSettings() {
    if (settings instanceof java.util.HashMap) {
      System.out.println("settings is HashMap");
    }
  }


  public boolean isPrivate() {
	return _partners != null;
  }

  
  public void setPrivate(boolean isPrivate) {
	this.isPrivate = isPrivate;
  }



  public final String getName() {
    return name;
  }
  public final void setName(String newValue) {
    name = newValue.trim();
  }


  public final int getGameType() {
    return gameType;
  }


  public boolean isGameStatus() {
	return gameStatus;
  }

  public void setGameStatus(boolean gameStatus) {
	this.gameStatus = gameStatus;
  }
  
  public final void setGameType(int newValue) {
    gameType = newValue;
  }

  public final boolean isRealMoneyTable() {
    return new PokerGameType(gameType).isReal();
  }

  public final int getPlayerCapacity() {
    return numPlayers;
  }
  public void setPlayerCapacity(int newValue) {
    numPlayers = newValue;
  }
  public final int getPlayerCount() {
    return playerCount;
  }
  public final void setPlayerCount(int playerCount) {
    this.playerCount = playerCount < 0 ? 0 : playerCount;
  }
  public final double getRakeRate() {
    return rakeRate;
  }
  public final void setRakeRate(double rakeRate) {
    this.rakeRate = rakeRate;
  }
  public final double getShortMaxRake() {
    return shortMaxRake;
  }
  public final void setShortMaxRake(double shortMaxRake) {
    this.shortMaxRake = shortMaxRake;
  }
  public final double getLongMaxRake() {
    return longMaxRake;
  }
  public final void setLongMaxRake(double longMaxRake) {
    this.longMaxRake = longMaxRake;
  }
  public final int getGameLimitType() {
    return gameLimitType;
  }
  public final void setGameLimitType(int gameLimitType) {
    this.gameLimitType = gameLimitType;
  }
  public final double getMinBuyIn() {
    return minBuyIn;
  }
  public final double getMaxBuyIn() {
    return maxBuyIn;
  }
  public final double getMinBet() {
    return minBet;
  }
  public final double getMaxBet() {
    return maxBet;
  }
  public double getFlop() {
	return flop;
  }
  public void setFlop(double flp) {
	flop = flp;
  }
  public double getPlayerPerFlop() {
	return plrperflop;
  }
  public void setPlayerPerFlop(double flp) {
	  plrperflop = flp;
  }
  public boolean isProPlayer() {
	return isProPlayer;
  }
  public void setIsProPlayer(boolean proPlayerExist) {
	this.isProPlayer = proPlayerExist;
  }
  public String getStack() {
	  return stack;
  }
  public void setStack(String stack) {
	  this.stack = stack;
  }
  public void setTournyChips(int chips) {
	  this.chips = chips;
  }
  public int getTournyChips() {
	  return chips;
  }
  public final double getFee() {
    return fee;
  }
  public final void setFee(double newValue) {
    fee = newValue;
  }
  public final double getPrizePool() {
    return prizePool;
  }
  public final void setPrizePool(double newValue) {
    prizePool = newValue;
  }
  public final String getRoomSkinClassName() {
    return roomSkinClassName;
  }
  public final void setRoomSkinClassName(String newValue) {
    roomSkinClassName = newValue;
  }
  public final int getState() {
    return state;
  }
  public void setState(int newValue) {
    state = newValue;
  }
  public final boolean isTournamentGame() {
    return (gameLimitType == TOURNAMENT);
  }
  public boolean isTournamentOver() {
    return tourny_state == TABLE_CLOSED;
  }
  public boolean isTournamentStarted() {
    return tourny_state == HAND_RUNNING;
  }
  public boolean isAcceptingPlayers() {
    return tourny_state == this.TABLE_OPEN;
  }
  public boolean isBench() {
	  return bench_state.equals("true")? true:false;
  }
  public void setBench(String bench) {
	  bench_state = bench;
  }
  
  public String getStateString(){
    switch(state){
      case SitnGoInterface.TABLE_OPEN:
        return "Registering";
      case SitnGoInterface.TABLE_CLOSED:
        return "Declared";
      case SitnGoInterface.HAND_RUNNING:
        return "Running";
      default:
        return "unknown";
    }
  }

  public boolean equalsByFields(Object obj) {
    if (obj instanceof LobbyTableModel) {
      LobbyTableModel table = (LobbyTableModel) obj;
      return
          SharedConstants.equals(name, table.name) &&
          gameType == table.gameType &&
          numPlayers == table.numPlayers &&
          rakeRate == table.rakeRate &&
          shortMaxRake == table.shortMaxRake &&
          longMaxRake == table.longMaxRake &&
          gameLimitType == table.gameLimitType &&
          minBuyIn == table.minBuyIn &&
          maxBuyIn == table.maxBuyIn &&
          fee == table.fee &&
          prizePool == table.prizePool &&
          SharedConstants.equals(roomSkinClassName, table.roomSkinClassName) &&
          state == table.state &&
          SharedConstants.equals(settings, table.settings);
    }
    else {
      return false;
    }
  }


  public boolean equals(Object obj) {
    if (obj instanceof LobbyTableModel) {
      return name == ( (LobbyTableModel) obj).getName();
    }
    else {
      return false;
    }
  }

  public double getAveragePot() {
    return averagePot;
  }

  public double getHandsPerHour() {
    return handsPerHour;
  }


  public void setAveragePot(double d) {
    averagePot = d;
  }


  public void setHandsPerHour(double d) {
    handsPerHour = d;
  }

  private Object settings;

  public Object getSettings() {
    return settings;
  }

  public void setSettings(Object settings) {
    this.settings = settings;
    printSettings();
  }



  public boolean isDeleted() {
    return isDeleted;
  }



  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }


  public final int getUserId() {
    return userId;
  }

  public final void setUserId(int userId) {
    this.userId = userId;
  }


  public final int getWaiterCount() {
    return waiterCount;
  }


  public final void setWaiterCount(int waiterCount) {
    if (waiterCount < 0) {
      waiterCount = 0;
    }
    this.waiterCount = waiterCount;
  }



  public String gameLimitTypeToString() {
	
    if(gameLimitType == 0) return "PL";
    else if(gameLimitType == -1) return "NL";
    else return "FL";
	

  }



  public int isValid() {

    if ( (getShortMaxRake() > getLongMaxRake()) && !isTournamentGame()) {

      return ValidationConstants.SHORT_RAKE_LT_LONG_RAKE;

    }

    if (minBuyIn == 0) {

      return ValidationConstants.MIN_BUYIN_NE_0;

    }

    if (settings instanceof Validable) {

      return ( (Validable) settings).isValid(this);

    }

    else {

      return ValidationConstants.IS_VALID;

    }

  }



  public boolean isPurged() {

    return state == PURGED;

  }



  public void setPurged() {

    state = PURGED;

  }





  public String[][] getPlayerDetails(){

    return _players;

  }



  public String[] getWaitersDetails(){

     return _waiters;

  }



}

