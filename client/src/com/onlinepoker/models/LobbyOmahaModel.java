package com.onlinepoker.models;

import com.golconda.message.GameEvent;
import com.onlinepoker.ValidationConstants;


public class LobbyOmahaModel
    extends LobbyTableModel {

  protected double smallBlind = 0;
  protected double bigBlind = 0;
  private int tournamentLevel = -1;
  private double playersPerFlop = 0;
  protected String stack = "";
  
  /**
   * Constructor.
   */
  public LobbyOmahaModel() {
    gameType = 2;//new PokerGameType(POKER_TYPE_HOLDEM);
  }

  public LobbyOmahaModel(GameEvent ge) {
    super(ge);
    name = ge.getGameName();
    numPlayers = Integer.parseInt(ge.get("max-players"));
    _players = ge.getPlayerDetails();
    _waiters = ge.getWaitersDetails();
    playerCount = _players == null ? 0 : _players.length;

    minBet = Double.parseDouble(ge.get("min-bet"));
    maxBet = Double.parseDouble(ge.get("max-bet"));

    if (ge.getType() == 64) {
      gameLimitType = PokerConstants.TOURNAMENT;
      minBuyIn = Double.parseDouble(ge.get("buyin"));
      maxBuyIn = Double.parseDouble(ge.get("buyin"));
      tourny_state = Integer.parseInt(ge.get("state"));
      tournamentLevel = Integer.parseInt(ge.get("level"));
    }
    else {
      gameLimitType = (maxBet > 0) ? PokerConstants.REGULAR :
          (maxBet == 0) ? PokerConstants.POT_LIMIT : PokerConstants.NO_LIMIT;
      //change the buyins according to specs
      minBuyIn = 20 * minBet;
      maxBuyIn = 100 * minBet;
    }

    smallBlind = Double.parseDouble(ge.get("small-blind"));
    bigBlind = Double.parseDouble(ge.get("big-blind"));
    stack = ge.get("stack");
    
    gameType = ge.getType();
    this.numPlayers = numPlayers;
    this.rakeRate = ge.getRake();
    this.shortMaxRake = 0;
    this.longMaxRake = 0;
    this.fee = 0;
    averagePot = Double.parseDouble(ge.get("average-pot"));
    handsPerHour = Double.parseDouble(ge.get("hands-hour"));
    flop = Double.parseDouble(ge.get("flop"));
    playersPerFlop = playerCount == 0 ? 0:flop > playerCount?100:(flop / playerCount)*100;
    String plyr[][] = ge.getPlayerDetails();
    for(int i=0; plyr != null && i < plyr.length; i++)
    {
    	String plr[] = plyr[i];
    	if(Integer.parseInt(plr[6]) > 0)
		{
    		isProPlayer = true;
    		break;
		}
    }
  }

  /**
   * Constructor.
   */
  public LobbyOmahaModel(LobbyTableModel model,
                          double lowBet,
                          double highBet,
                          double smallBlind,
                          double bigBlind
                          ) {
    super(model.getName(),
          model.getGameType(),
          model.isRealMoneyTable(),
          model.getPlayerCapacity(),
          model.getRakeRate(),
          model.getShortMaxRake(),
          model.getLongMaxRake(),
          model.getGameLimitType(),
          model.getMinBuyIn(),
          model.getMaxBuyIn(),
          model.getFee(),
          model.getAveragePot(),
          model.getHandsPerHour(),
          model.getPlayerPerFlop(),
          model.isProPlayer(),
          model.getStack(),
          model.getTournyChips()
          );
    this.minBet = lowBet;
    this.maxBet = highBet;
    this.smallBlind = smallBlind;
    this.bigBlind = bigBlind;
  }

  /**
   * Constructor.
   */
  public LobbyOmahaModel(int id,
                          String name,
                          int gameType,
                          boolean playRealMoney,
                          int numPlayers,
                          double rakeRate,
                          double shortMaxRake,
                          double longMaxRake,
                          int potLimit,
                          double lowBet,
                          double highBet,
                          double smallBlind,
                          double bigBlind,
                          double minBuyIn,
                          double maxBuyIn,
                          double fee,
                          double avgPot,
                          double handsPerHour,
                          double flop,
                          boolean ispro,
                          String stack,
                          int chips,
                          Integer distrId
                          ) {
    super(name, gameType, playRealMoney, numPlayers,
          rakeRate, shortMaxRake, longMaxRake, potLimit, minBuyIn, maxBuyIn,
          fee,avgPot,handsPerHour,flop, ispro, stack, chips);
//    setGameLimit(gameLimit);
    this.minBet = lowBet;
    this.maxBet = highBet;
    this.smallBlind = smallBlind;
    this.bigBlind = bigBlind;
  }

  /**
   * Constructor.
   */
  public LobbyOmahaModel(LobbyOmahaModel model) {
    copy(model);
  }

  /**
   * Copies.
   */

  public void copyWithoutSettings(LobbyTableModel model) {
    super.copyWithoutSettings(model);
    LobbyOmahaModel omahaModel = (LobbyOmahaModel) model;
    this.minBet = omahaModel.minBet;
    this.maxBet = omahaModel.maxBet;
    this.smallBlind = omahaModel.smallBlind;
    this.bigBlind = omahaModel.bigBlind;
    this.playersPerFlop = omahaModel.playersPerFlop;
    this.tournamentLevel = omahaModel.tournamentLevel;
  }

  public void copy(LobbyTableModel model) {
    super.copy(model);
  }

  /**
   * Gets the min bet on the table.
   * @hibernate.property
   */
  public final double getLowBet() {
    return minBet;
  }

  /**
   * Sets the min bet on the table.
   */
  public final void setLowBet(double newValue) {
	  minBet = newValue;
  }

  /**
   * Gets the max bet on the table.
   * @hibernate.property
   */
  public final double getHighBet() {
    return maxBet;
  }

  /**
   * Sets the max bet on the table.
   */
  public final void setHighBet(double newValue) {
	  maxBet = newValue;
  }

  public String getStack() {
	  return stack;
  }

  public void setStack(String stack) {
	  this.stack = stack;
  }
  
  /**
   * Gets the small blind on the table.
   * @hibernate.property
   */
  public final double getSmallBlind() {
    return smallBlind;
  }

  /**
   * Sets the small blind on the table.
   */
  public final void setSmallBlind(double newValue) {
    smallBlind = newValue;
  }

  /**
   * Gets the big blind on the table.
   * @hibernate.property
   */
  public final double getBigBlind() {
    return bigBlind;
  }

  /**
   * Sets the big blind on the table.
   */
  public final void setBigBlind(double newValue) {
    bigBlind = newValue;
  }

  /**
   * Gets the index of tournament level in TournamentConstants interface.
   * If game is not a tournament return -1.
   */
  public final int getTournamentLevel() {
    return tournamentLevel;
  }

  /**
   * Sets the index of tournament level in TournamentConstants interface.
   * If the game is tournament game and new value not equals the stored
   * value updates lowBet, highBet, smallBlind, bigBlind
   */
  public final void setTournamentLevel(int value) {
    this.tournamentLevel = value;
  }

  /**
   * @return
   */
  public double getPlayersPerFlop() {
    return playersPerFlop;
  }

  /**
   * @param d
   */
  public void setPlayersPerFlop(double d) {
    //Logger.log("playersPerFlop: " + d);
    playersPerFlop = d;
  }

  public boolean equalsByFields(Object obj) {
    if (obj instanceof LobbyOmahaModel) {
      LobbyOmahaModel table = (LobbyOmahaModel) obj;
      return
          super.equalsByFields(table) &&
          minBet == table.minBet &&
          maxBet == table.maxBet &&
          smallBlind == table.smallBlind &&
          bigBlind == table.bigBlind;
    }
    else {
      return false;
    }
  }

  public int isValid() {
    if (minBet > maxBet) {
      return ValidationConstants.LOW_BET_LT_HIGH_BET;
    }
    if (minBet / 2 > smallBlind) {
      return ValidationConstants.SMALL_BLIND_GT_LOW_BET_DIV_2;
    }
    if (minBet > bigBlind) {
      return ValidationConstants.BIG_BLIND_GT_LOW_BET;
    }
    if (minBuyIn < bigBlind) {
      return ValidationConstants.MIN_BUYIN_GT_BIG_BLIND;
    }
    if (isTournamentGame()) {
      if (minBuyIn != maxBuyIn) {
        return ValidationConstants.MIN_BUYIN_EQ_MAX_BUYIN_FOR_TOURNAMENT;
      }
    }
    else {
      if ( (gameLimitType != PokerConstants.REGULAR) && (minBuyIn >= maxBuyIn)) {
        return ValidationConstants.MIN_BUYIN_LT_MAX_BUYIN_FOR_NON_TOURNAMENT;
      }
    }
    return super.isValid();
  }

}
