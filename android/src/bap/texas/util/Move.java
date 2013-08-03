package bap.texas.util;


public class Move {

	public static String JOIN;
	public static String LEAVE;
	public static String SIT_IN;
	public static String OPT_OUT;
	public static String WAIT;
	public static String BIGBLIND;
	public static String SMALLBLIND;
	public static String BET;
	public static String ANTE;
	public static String ALL_IN;
	public static String CALL;
	public static String RAISE;
	public static String CHECK;
	public static String FOLD;

	public int _move;
	public int _bet;
	
	public Move(int m, int b){
		_move = m;
		_bet = b;
	}
	
	public String toString(){
		return getName();
	}
	
	
	public String getName(){
		switch(_move){
			/**case Constants.M_JOIN: return TexasHoldem.JOIN;
			case Constants.M_LEAVE: return TexasHoldem.LEAVE;
			case Constants.M_SIT_IN: return TexasHoldem.SIT_IN;
			case Constants.M_OPT_OUT: return TexasHoldem.OPT_OUT;
			case Constants.M_WAIT: return TexasHoldem.WAIT;
			case Constants.M_BIGBLIND: return TexasHoldem.BIGBLIND + _bet;
			case Constants.M_SMALLBLIND: return TexasHoldem.SMALLBLIND + _bet;
			case Constants.M_BET: return TexasHoldem.BET + _bet;
			case Constants.M_ANTE: return TexasHoldem.ANTE;
			case Constants.M_ALL_IN: return TexasHoldem.ALL_IN;
			case Constants.M_CALL: return TexasHoldem.CALL + _bet;
			case Constants.M_RAISE: return TexasHoldem.RAISE + _bet;
			case Constants.M_CHECK: return TexasHoldem.CHECK;
			case Constants.M_FOLD: return TexasHoldem.FOLD;**/
			default: return "none";
		}
	}

}
