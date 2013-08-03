package com.onlinepoker.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.poker.common.interfaces.TournyInterface;
import com.poker.common.message.TournyEvent;

/**
 * Lobby table model for Cricket game
 * @hibernate.subclass
 *    discriminator-value="1"
 */

public class LobbyTournyModel
    extends LobbyTableModel {

  static Logger _cat = Logger.getLogger(LobbyTournyModel.class.getName());

  protected double _fees = 0;
  protected double _buyin = 0;
  protected Date _date;
  protected int _level;
  protected int _chips;
  protected int _limit;
  protected int _bounty;
  protected int _state;
  protected int _delta;
  protected int _min_players;
  protected int _max_players;
  protected String _tourny_type;
  protected String _next_level;
  protected String _stake;
  private Calendar schedule;
  private int tournamentLevel = -1;
  private int tournyPlayerCount = 0;
  private int tournyMaxPlayerCount = 90;

  public LobbyTournyModel(TournyEvent ge) {
    super(ge);
    name = ge.name();
    _fees = Double.parseDouble(ge.get("fees"));
    _buyin = Double.parseDouble(ge.get("buy-in"));
    _level = Integer.parseInt(ge.get("level"));
    _chips = (int)Double.parseDouble(ge.get("chips"));
    _limit = Integer.parseInt(ge.get("limit"));
    if(ge.get("bounty") != null)_bounty = Integer.parseInt(ge.get("bounty"));
    _state = Integer.parseInt(ge.get("state"));
    _delta = Integer.parseInt(ge.get("delta"));
    _stake = ge.get("stakes");
    _next_level = ge.get("next_level");
    _tourny_type = ge.get("type");
    if(ge.get("min_players") != null)
    {
    	_min_players = Integer.parseInt(ge.get("min_players"));
    	_max_players= Integer.parseInt(ge.get("max-players"));
    }
    String pstr = ge.get("player");
    if(pstr != null && !pstr.equals(""))
    {
	    String pstr1[] = pstr.split("\\|");
	    tournyPlayerCount = pstr1.length;
    }
    try {
        _date = ge.date();
        schedule = Calendar.getInstance();
        schedule.setTime(_date);
    }
    catch (ParseException e){
        e.printStackTrace();
    }
  }

  public void update(TournyEvent ge){
	  name = ge.name();
	    _fees = Double.parseDouble(ge.get("fees"));
	    _buyin = Double.parseDouble(ge.get("buy-in"));
	    _level = Integer.parseInt(ge.get("level"));
	    _chips = (int)Double.parseDouble(ge.get("chips"));
	    _limit = Integer.parseInt(ge.get("limit"));
	    _tourny_type = ge.get("type");
	    if(ge.get("bounty") != null)_bounty = Integer.parseInt(ge.get("bounty"));
	    _state = Integer.parseInt(ge.get("state"));
	    _delta = Integer.parseInt(ge.get("delta"));
	    _stake = ge.get("stakes");
	    _next_level = ge.get("next_level");
	    try {
	        _date = ge.date();
	        schedule = Calendar.getInstance();
	        schedule.setTime(_date);
	    }
	    catch (ParseException e){
	        e.printStackTrace();
	    }
  }


  /**
   * Copies.
   */

  /**public void copyWithoutSettings(LobbyTableModel model) {
    super.copyWithoutSettings(model);
    LobbyTournyModel cricketModel = (LobbyTournyModel) model;
    this.lowBet = cricketModel.lowBet;
    this.highBet = cricketModel.highBet;
    this.smallBlind = cricketModel.smallBlind;
    this.bigBlind = cricketModel.bigBlind;
    this.playersPerFlop = cricketModel.playersPerFlop;
    this.tournamentLevel = cricketModel.tournamentLevel;
  }

  public void copy(LobbyTableModel model) {
    super.copy(model);
  }**/

  public String getTournamentType() {
    return _tourny_type;
  }
  
  public double getTournamentBuyIn() {
    return _buyin;
  }
  
  public double getTournamentFee() {
    return _fees;
  }
  
  public final int getTournamentPlayerCount() {
    return tournyPlayerCount;
  }
  
  public final int getTournamentMaxPlayers() {
    return tournyMaxPlayerCount;
  }
//  
//  public final int getTournamentMinPlayers() {
//    return _min_players;
//  }

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

  public boolean equalsByFields(Object obj) {
    if (obj instanceof LobbyTournyModel) {
      LobbyTournyModel table = (LobbyTournyModel) obj;
      return
          super.equalsByFields(table);
    }
    else {
      return false;
    }
  }

  
  public String getStateString(){
    switch(_state){
      case TournyInterface.NOEXIST:
        return "no-exist";
      case TournyInterface.CREATED:
        return "Created";
      case TournyInterface.DECL:
        return "Declared";
      case TournyInterface.REG:
        return "Registering";
      case TournyInterface.JOIN:
        return "Waiting for Players";
      case TournyInterface.START:
        return "Starting now";
      case TournyInterface.RUNNING:
        return "Running";
      case TournyInterface.END:
        return "Winners Declared";
      case TournyInterface.FINISH:
        return "Winners Declared";
      case TournyInterface.CLEAR:
        return "Over";
      default:
        return "unknown";
    }
  }

  public int getTournamentLimit() {
	return _limit;
  }

  public int getLevel(){
    return _level;
  }
  
  public int getMinPlayers(){
    return _min_players;
  }
  
  public int getMaxPLayers(){
    return _max_players;
  }
  
  public String getNextLevel(){
    return _next_level;
  }
  
  public String getStakes(){
      return _stake;
  }
  
  public int getDelta(){
	  return _delta;
  }
  
  public int getChips(){
	  return _chips;
  }
  public String getSchedule(){
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("KK:mm a - MMM dd z");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      return simpleDateFormat.format(schedule.getTime());
  }
  public String getDate(){
    SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd 'at' HH:mm z", Locale.US);
    //_cat.finest("Date = " + _date);
    return sdf.format(_date);
  }

  /**public int isValid() {
    return super.isValid();
  }**/

}
