package com.poker.game.poker;

import com.agneya.util.Utils;

import com.golconda.game.Presence;

import com.poker.game.PokerPresence;
import com.poker.game.util.Hand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;


public class Pot {
  static Logger _cat = Logger.getLogger(Pot.class.getName());

  public Pot(String potName, double rakePerc, double maxRake, Poker p) {
    _name = potName;
	_rakePercent = rakePerc;
	_maxRake = maxRake;
    _high_winners = new Vector();
    _low_winners = new Vector();
    _p = p;
  }

  public void addContender(PokerPresence contender, double amount) {
	_contenders.add(contender);
	if (amount <= 0) return;
    amount = Utils.getRounded(amount);
    _val += amount;
    _rake = updateRake();
  }

  public PokerPresence[] contenders() {
    return (PokerPresence[]) _contenders.toArray(new PokerPresence[] {});
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    this._name = name;
  }

  public double getVal() {
    return _val + _uncalled_bet - _rake;
  }

  public String getValString() {
    return Utils.getRoundedString(_val + _uncalled_bet - _rake);
  }

  public double getRake() {
    return _rake;
  }

  public void addVal(double amt) {
    assert amt >= 0:"Amount added to worth cannot be less than zero " + amt;
    _val += amt;
    _rake = updateRake();
  }

  public void  moveMoneyToRakedPot(){
    _cat.finest("Uncalled bet = " + _uncalled_bet);
    _val += _uncalled_bet;
    _uncalled_bet=0;
    _rake = updateRake();
  }

  public void addUnCalledBet(double amt) {
    assert amt >= 0:"Amount added to worth cannot be less than zero " + amt;
    _uncalled_bet += amt;
  }

  public void addPot(Pot p) {
    _val += p._val;
    _uncalled_bet += p._uncalled_bet;
    _rake = updateRake();
  }

  public void remove(Presence p) {
    _contenders.remove(p);
  }

  public void addHighWinners(Presence p, double _highAmount, long combi) {
    PotWinner pw = new PotWinner();
    pw._presence = p;
    p.addToWin(_highAmount);
    p.setShowdown();
    p.unsetBroke();
    pw._name = p.name();
    pw._pos = p.pos();
    pw._best_combination =combi;
    pw._winAmountHigh = Utils.getRoundedString(_highAmount);
    _high_winners.add(pw);
  }

  public PotWinner[] highWinners() {
    return (PotWinner[])_high_winners.toArray(new PotWinner[_high_winners.size()]);
  }

  public void addLowWinners(Presence p, double _lowAmount, long combi) {
    PotWinner pw = new PotWinner();
    pw._presence = p;
    p.addToWin(_lowAmount);
    p.setShowdown();
    p.unsetBroke();
    pw._name = p.name();
    pw._pos = p.pos();
    pw._best_combination =combi;
    pw._winAmountLow = Utils.getRoundedString(_lowAmount);
    _low_winners.add(pw);
  }

  public PotWinner[] lowWinners() {
    return (PotWinner[])_low_winners.toArray(new PotWinner[_low_winners.size()]);
  }

  public double updateRake() {
	    if (_contenders.size() <=1 ) return 0;
	    //_cat.finest("....Fraction=" + _rakePercent);
	    double rk = _rakePercent * _val;
	    //_cat.finest("....Tmp Rake=" + rk);
	    //_cat.finest("....Val=" + _val);
	    //round it off to low 25c
	    int minUnit;
	    minUnit = 1;
	    double rake = (rk - (rk % minUnit)) / 100.00;
	    //_cat.finest("....Rake=" + rake);

	    if (_maxRake < rake) {
	      return _maxRake;
	    }
	    else {
	      return rake;
	    }
	  }


  public void resetContenders() {
    _contenders = new ArrayList();
  }

  private List<PokerPresence> _contenders = new ArrayList<PokerPresence>();
  private String _name;
  private double _val = 0;
  private double _uncalled_bet = 0;
  private double _rake = 0;
  protected double _rakePercent;
  protected double _maxRake;
  protected Poker _p;

  private Vector _high_winners;
  private Vector _low_winners;

  public String toString() {
    String str = " Pot: name=" + _name + ", Value=" + _val +
                            ", Rake=" + _rake;
    Iterator i = _contenders.iterator();
    while (i.hasNext()) {
      str += ( (Presence) i.next()).name() + " | ";
    }
    return str;
  }

  public class PotWinner {
    public Presence _presence;
    public String _name;
    public int _pos;
    public String _winAmountLow, _winAmountHigh;
    public long _best_combination;

    public String toString(){
      return _name + ", " + Hand.stringValue(_best_combination);
    }
  }

}











/**public double updateRake() {
if (_p._type.isPlay() || _val==0 || _p._bettingRound == _p.R_PREFLOP || _p.type().isTourny())return 0;
double rake_chips=0;
double money_in_pot=0;
double max_rake=0;
if (_p._maxBet == 0 || _p._maxBet == -1) {
	  //NL PL
	  if (_p._minBet < 0.10){
		  if (_p._players.length <= 3){
			  rake_chips = 0.01;
			  money_in_pot = 0.15;
			  max_rake = 0.5;
		  }
		  else if (_p._players.length <= 5){
			  rake_chips = 0.01;
			  money_in_pot = 0.15;
			  max_rake = 1.00;
		  }
		  else {
			  rake_chips = 0.01;
			  money_in_pot = 0.15;
			  max_rake = 2.00;
		  }
	  }
	  else if (_p._minBet < 0.25){
		  if (_p._players.length <= 3){
			  rake_chips = 0.01;
			  money_in_pot = 0.20;
			  max_rake = 1.00;
		  }
		  else if (_p._players.length <= 5){
			  rake_chips = 0.01;
			  money_in_pot = 0.20;
			  max_rake = 2.00;
		  }
		  else {
			  rake_chips = 0.01;
			  money_in_pot = 0.20;
			  max_rake = 3.00;
		  }
	  }

	  else {
		  if (_p._players.length <= 3){
			  rake_chips = 0.05;
			  money_in_pot = 1.00;
			  max_rake = 1.00;
		  }
		  else if (_p._players.length <= 5){
			  rake_chips = 0.05;
			  money_in_pot = 1.00;
			  max_rake = 2.00;
		  }
		  else {
			  rake_chips = 0.05;
			  money_in_pot = 1.00;
			  max_rake = 3.00;
		  }
	  }
}
else {
	//FIXED LIMIT GAMES
	  if (_p._minBet < 0.20){
		  if (_p._players.length <= 3){
			  rake_chips = 0.01;
			  money_in_pot = 0.20;
			  max_rake = 1.00;
		  }
		  else if (_p._players.length <= 5){
			  rake_chips = 0.01;
			  money_in_pot = 0.20;
			  max_rake = 2.00;
		  }
		  else {
			  rake_chips = 0.01;
			  money_in_pot = 0.20;
			  max_rake = 3.00;
		  }
	  }
	  else if (_p._minBet < 0.75){
		  if (_p._players.length <= 3){
			  rake_chips = 0.05;
			  money_in_pot = 1.0;
			  max_rake = 0.25;
		  }
		  else {
			  rake_chips = 0.05;
			  money_in_pot = 1.0;
			  max_rake = 0.5;
		  }
	  }
	  else if (_p._minBet < 2){
		  if (_p._players.length <= 3){
			  rake_chips = 0.25;
			  money_in_pot = 5.0;
			  max_rake = 0.5;
		  }
		  else {
			  rake_chips = 0.25;
			  money_in_pot = 5.0;
			  max_rake = 1.0;
		  }
	  }
	  else {
		  if (_p._players.length <= 3){
			  rake_chips = 0.5;
			  money_in_pot = 10.0;
			  max_rake = 1.00;
		  }
		  else if (_p._players.length <= 5){
			  rake_chips = 1.0;
			  money_in_pot = 20.0;
			  max_rake = 2.00;
		  }
		  else {
			  rake_chips = 1.0;
			  money_in_pot = 20.0;
			  max_rake = 3.00;
		  }
	  }
} 
int mult_fact = (int)(_val/money_in_pot);
double rk = mult_fact * rake_chips;
if (rk > max_rake){
	  return max_rake;
}
else {
	  return rk;
}
} **/
