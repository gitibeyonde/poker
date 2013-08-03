package com.onlinepoker.actions;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.golconda.game.util.ActionConstants;
import com.golconda.game.util.Card;
import com.onlinepoker.SharedConstants;
import com.onlinepoker.resources.Bundle;


public abstract class Action
    implements ActionConstants, java.io.Serializable {
  protected long[] guid;
  protected int id;
  protected int type;
  protected int target;

static Logger _cat = Logger.getLogger(Action.class.getName());

  public Action(int id, int type, int target) {
    guid = new long[] {
        System.currentTimeMillis(), Math.round(Math.random() * Long.MAX_VALUE)};
    this.id = id;
    this.type = type;
    this.target = target;
  }

  public Action(int id, int type) {
    this(id, type, -1);
  }

//    public Action(int id) {
//        this(id, ACTION_TYPE_SIMPLE);
//    }

  public long[] getGuid() {
    return guid;
  }

  public void setGuid(long[] guid) {
    this.guid = guid;
  }

  public boolean equalsByGuid(long[] guid) {
    if (guid == null || this.guid == null) {
      return false;
    }
    for (int i = 0; i < guid.length; i++) {
      if (guid[i] != this.guid[i]) {
        return false;
      }
    }
    return true;
  }

  public static String guidToString(long[] guid) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < guid.length; i++) {
      s.append(guid[i]);
    }
    return s.toString();
  }

  public int getId() {
    return id;
  }

  public int getType() {
    return type;
  }

  public int getTarget() {
    return target;
  }

  public String toMessage(String playerName) {
    return toMessage(false, playerName);
  }

  public String toMessage(boolean isForHistory, String playerName) {
    ResourceBundle bundle = Bundle.getBundle();
    //if (bundle == null)
    StringBuilder s = new StringBuilder();
    switch (type) {
      case ActionConstants.ACTION_TYPE_LASTMOVE:
        LastMoveAction ba = (LastMoveAction)this;
        switch (id) {
        
          case ActionConstants.SMALL_BLIND:
            s.append(bettingActionToString(ba, "do.sb", playerName, bundle));
            break;

          case ActionConstants.BIG_BLIND:
            s.append(bettingActionToString(ba, "do.bb", playerName, bundle));
            break;

          case ActionConstants.FOLD:
            s.append(tableServerActionToString("do.fold", playerName, bundle));
            break;

          case ActionConstants.CALL:
            s.append(bettingActionToString(ba, "do.call", playerName, bundle));
            break;

          case ActionConstants.CHECK:
            s.append(bettingActionToString(true, ba, "do.check", playerName,
                                           bundle));
            break;

          case ActionConstants.ALLIN:
            s.append(bettingActionToString(ba, "do.allin", playerName, bundle));
            break;

          case ActionConstants.RAISE:
            s.append(bettingActionToString(ba, "do.raise", playerName, bundle));
            break;

          case ActionConstants.BET:
            s.append(bettingActionToString(ba, "do.bet", playerName, bundle));
            break;

          case ActionConstants.NEW_HAND:
            s.append(MessageFormat.format(bundle.getString("stage.start"),
                                          new Object[] {"" + ba.getRoundBet()}));
        }
        break;

      case ActionConstants.ACTION_TYPE_STAGE:
        switch (id) {
        case ActionConstants.PREFLOP:
          break; 
        case ActionConstants.FLOP:
        	  //Statistics.saw_flop++;
        	  s.append(bundle.getString("stage.flop"));
        	  break;

          case ActionConstants.TURN:
        	  //Statistics.saw_turn++;
        	  s.append(bundle.getString("stage.turn"));
            break;

          case ActionConstants.RIVER:
        	  //Statistics.saw_river++;
        	  s.append(bundle.getString("stage.river"));
            break;

          case ActionConstants.SHOWDOWN:
        	  //Statistics.saw_showdown++;
        	  s.append(bundle.getString("stage.showdown"));
            break;
          
          case ActionConstants.START_GAME:
              break;
          case ActionConstants.END_GAME:
            break;

          case ActionConstants.WIN:
            WinAction wa = (WinAction)this;
            if (wa.getTarget() <= 0) {
              break;
            }
            s.append(MessageFormat.format(bundle.getString("do.win"),
                                          new Object[] {playerName,
                                          SharedConstants.chipToMoneyString(wa.
                getPot())}));
            if (wa.getCombination().length > 0) {
              if (wa.getCombination().length > 1) {
                s.append(" from the " + wa.getPotName() +
                         " with ").append(wa.getWinMsg());
              }
              else {
                s.append(" from the " + wa.getPotName());
//                  append(wa.getCombinationString());
              }
            }
            int betRound = wa.getBetRound();
     /*       if(playerName.equals(ServerProxy._name)){
            	if(ServerProxy._name.equals(wa.getName())){
            		double winAmt = wa.getPot() - Statistics.bet;
            		System.out.println("winAmt "+winAmt+" = "+wa.getPot() +", "+Statistics.bet);
	            	if(betRound == -1){ //PREFLOP
	            		Statistics.won_preflop++;
	            		Statistics.won_amt_preflop +=winAmt;
	            	}else if(betRound == 0){ //FLOP
	            		Statistics.won_flop++;
	            		Statistics.won_amt_flop +=winAmt;
	            	}else if(betRound == 1){ //TURN
	            		Statistics.won_turn++;
	            		Statistics.won_amt_turn +=winAmt;
	            	}else if(betRound == 2){ //RIVER
	            		Statistics.won_river++;
	            		Statistics.won_amt_river +=winAmt;
	            	}else if(betRound == 3){ //RIVER
	            		Statistics.won_showdown++;
	            		Statistics.won_amt_showdown +=winAmt;
	            	}
            	}else{
            		System.out.println("not winner "+betRound);
            		if(betRound == -1){ //PREFLOP
	            		Statistics.won_preflop--;
	            		Statistics.won_amt_preflop -=Statistics.bet;
	            	}else if(betRound == 0){ //FLOP
	            		Statistics.won_flop--;
	            		Statistics.won_amt_flop -=Statistics.bet;
	            	}else if(betRound == 1){ //TURN
	            		Statistics.won_turn--;
	            		Statistics.won_amt_turn -=Statistics.bet;
	            	}else if(betRound == 2){ //RIVER
	            		Statistics.won_river--;
	            		Statistics.won_amt_river -=Statistics.bet;
	            	}else if(betRound == 2){ //RIVER
	            		Statistics.won_showdown--;
	            		Statistics.won_amt_showdown -=Statistics.bet;
	            	}
            	}
            	Statistics.round = -1;
            	Statistics.bet = 0;
            }*/
            break;
        }
        break;

      case ActionConstants.ACTION_TYPE_TABLE_SERVER:
        switch (id) {
          case ActionConstants.PLAYER_REGISTERED:
            s.append(tableServerActionToString("do.register", playerName,
                                               bundle));
            break;

          case ActionConstants.PLAYER_UNREGISTERED:
            s.append(tableServerActionToString("do.unregister", playerName,
                                               bundle));
            break;

          case ActionConstants.PLAYER_JOIN:
            s.append(tableServerActionToString("do.join", playerName, bundle));
            break;

          case ActionConstants.PLAYER_LEAVE:
            s.append(tableServerActionToString("do.leave", playerName, bundle));
            break;

          case ActionConstants.PLAYER_SITIN:
        	  s.append(tableServerActionToString("do.sitin", playerName, bundle));
            break;

          case ActionConstants.PLAYER_SITOUT:
            s.append(tableServerActionToString("do.sitout", playerName, bundle));
            break;

          case ActionConstants.PLAYER_REJOIN:
            s.append(tableServerActionToString("do.rejoin", playerName, bundle));
            break;
        }
        break;
      case ActionConstants.ACTION_TYPE_BETTING:
          switch (id) {
            case ActionConstants.FOLD:
            	s.append(tableServerActionToString("do.fold", playerName, bundle));
              break;
          }
          break;
      default:
        switch (id) {
          case ActionConstants.SHOW_CARD:
          case DEALING:
            CardAction ca = (CardAction)this;
            Card[] c = ca.getCards();
            if (c == null || c.length == 0) {
              break;
            }
            if (getTarget() == ActionConstants.BOARD_TARGET || isForHistory) {
              if (isForHistory && playerName != null) {
                String dealString = id == SHOW_CARD ? "do.showdown" :
                    "do.deal.for";
                s.append(MessageFormat.format(bundle.getString(dealString),
                                              new Object[] {playerName}));
              }
              else {
                s.append(bundle.getString("do.deal"));
              }
              s.append(": [");
              if (c != null) {
                for (int ic = 0; ic < c.length; ic++) {
                  s.append(c[ic]);
                  if (ic != c.length - 1) {
                    s.append(",");
                  }
                }
              }
              s.append("]");
            }
            break;
        }
    }
    return s.toString();
  }

  public StringBuilder bettingActionToString(BettingAction ba, String res,
                                            String playerName,
                                            ResourceBundle bundle) {
    return bettingActionToString(false, ba, res, playerName, bundle);
  }

  public StringBuilder bettingActionToString(LastMoveAction ba, String res,
                                            String playerName,
                                            ResourceBundle bundle) {
    return bettingActionToString(false, ba, res, playerName, bundle);
  }

  public StringBuilder bettingActionToString(boolean isCall, BettingAction ba,
                                            String res, String playerName,
                                            ResourceBundle bundle) {
    String msg = isCall ?
        tableServerActionToString(res, playerName, bundle) :
        MessageFormat.format(bundle.getString(res), new Object[] {
                             playerName,
                             SharedConstants.chipToMoneyString(ba.getBet())});
    return new StringBuilder().append(msg).
        append(ba.isAllIn() && ba.getId() != ActionConstants.ALLIN ?
               " " + bundle.getString("do.andallin") : "");
  }

  public StringBuilder bettingActionToString(boolean isCall, LastMoveAction ba,
                                            String res, String playerName,
                                            ResourceBundle bundle) {
    String msg = isCall ?
        tableServerActionToString(res, playerName, bundle) :
        MessageFormat.format(bundle.getString(res), new Object[] {
                             playerName,
                             SharedConstants.chipToMoneyString(ba.getRoundBet())});
    return new StringBuilder().append(msg);
  }

  public String tableServerActionToString(String res, String playerName,
                                          ResourceBundle bundle) {
    return MessageFormat.format(bundle.getString(res), new Object[] {playerName});
  }

  public static String actionToString(Action action) {
    /*switch (action.id) {
        case PLAYER_SITIN:
            return "Player sitin. Seat # " + action.target;
        case PLAYER_SITOUT:
            return "Player sitout. Seat # " + action.target;
        default:*/
    return actionToString(action.id);
    //}
  }

  public static String actionToString(int id) {
    switch (id) {
      case PREGAME:
        return "Pregame";
      case START_GAME:
        return "Start game";
      case SMALL_BLIND:
        return "Small Blind";
      case BIG_BLIND:
        return "Big Blind";
      case PREFLOP:
        return "PREFLOP";
      case FLOP:
        return "Flop";
      case TURN:
        return "Turn";
      case RIVER:
        return "River";
      case SHOWDOWN:
        return "Showdown";
      case END_GAME:
        return "End game";
      case DEALING:
        return "Dealing";
      case FOLD:
        return "Fold";
      case CALL:
        return "Call";
      case CHECK:
        return "Check";
      case ALLIN:
        return "All-in";
      case RAISE:
        return "Raise";
      case BET:
        return "Bet";
      case WIN:
        return "Win";
      //case TOURNAMENT_WIN:
        //return "Tournament win";
      case CHECK_FOLD:
        return "Check/Fold";
      case CHECK_CALL:
        return "Check/Call";
      case CHECK_CALL_ANY:
        return "Check/Call any";
      case RAISE_ANY:
        return "Raise any";
      case NO_PREBET:
        return "No prebet";
      case BET_REQUEST:
        return "Bet request";
      case BIG_BLIND_REQUEST:
        return "Big blind request";
      case SMALL_BLIND_REQUEST:
        return "Small blind request";
      case BOTH_BLIND_REQUEST:
        return "Both blind request";
      case MISSED_BIG_BLIND_REQUEST:
        return "Missed big blind request";
      case MISSED_SML_BLIND_REQUEST:
        return "Missed small blind request";
      case SHOW_CARD:
        return "Show card";
      case SHOW_WINNER_CARD:
        return "Show winner s";

      case MAKE_POT:
        return "Make pot";
      case SET_BUTTON:
        return "Set button";
      case SET_CURRENT:
        return "Set current";
      case SHOWDOWN_REQUEST:
        return "Showdown request";
      case SHOW_SHOWDOWN_CARD:
        return "show showdown cards";
      case TABLE_INFO:
        return "Table info";
      case WAITER_CAN_JOIN:
        return "Waiter can join";
      case CHAT:
        return "Chat";
      case PLAYER_MESSAGE:
        return "Player message";
      case NEW_HAND:
        return "New hand";
      case YOUR_TURN:
        return "Your turn";
      case NO_SHOWDOWN:
        return "No showdown";
      case HIDE_CARD:
        return "Hide card";
      case PLAYER_REGISTERED:
        return "Player registered";
      case PLAYER_UNREGISTERED:
        return "Player unregistered";
      case PLAYER_JOIN:
        return "Player join";
      case PLAYER_LEAVE:
        return "Player leave";
      case PLAYER_REJOIN:
        return "Player rejoin";
      case PLAYER_NEEDS_SITOUT:
        return "Player needs sitout";
      case ADD_TO_WAITERS:
        return "Add to waiters";
      case REMOVE_FROM_WAITERS:
        return "Remove from waiters";
      case IMMEDIATE_SHUTDOWN:
        return "Immediate shutdown";
      case SESSION_TIMEOUT:
        return "Session timeout";
      case GRACEFUL_SHUTDOWN:
        return "Graceful shutdown";
      case NO_MORE_WAITING:
        return "No more waiting";
      case STARTUP:
        return "Startup";
      case CHANGE_STATE:
        return "Change state";
      case MANUAL_IMMEDIATE_SHUTDOWN:
        return "Manual immediate shutdown";
      case MANUAL_GRACEFUL_SHUTDOWN:
        return "Manual graceful shutdown";
      case MANUAL_STARTUP:
        return "Manual startup";
      case MANUAL_CHANGE_STATE:
        return "Manual change state";
      case PLACE_OCCUPIED:
        return "Place occupied";
      case UNSUFFICIENT_FUND:
        return "Not enough money to play";
      case DECISION_TIMEOUT:
        return "Decision timeout";
      case SAME_REMOTE_HOST:
        return
            "There is another player at the table connected from this remote address";
      case THERE_IS_CLAIM:
        return "There is a waiter claim on the table !";
      case ALREADY_WAIT:
        return "Player already waits or sits at the table!";
      case TABLE_IS_OFFLINE:
        return "This table is turned off !";
      case CASHIER_UNAVAIBLE:
        return "Cashier function is not avaible for this table !";
      case UNKNOWN_ERROR:
        return "Unknown error !";
      case UNKNOWN_SESSION:
        return "Unknown session id !";
        // Agneya FIX 30
      case PLAYER_NEEDS_SITIN_TRUE:
        return "Player sitting in";
      case PLAYER_NEEDS_SITIN_FALSE:
        return "Player sitting out";
      case ANTE:
        return "Ante";
        //case DEL_BOT:
        //    return "Del bot";
        //case REPLACE_BOT:
        //    return "Replace bot";
      case IS_ACCEPTING:
        return "Is accepting";
      case PRE_WIN:
          return "PRE_WIN";
      case POST_WIN:
          return "POST_WIN";
      case UPDATE:
          return "UPDATE";
      case DELAY:
          return "DELAY";
      case NEXT_MOVE:
          return "NEXT_MOVE";
      case TOTALBET_INFO:
          return "TOTALBET_INFO";
        default:
        return id+"Unknown id: " + id;
    }
  }
  
  public String actionTypeToString(int _type){
	  switch(_type){
	  
	  case ActionConstants.ACTION_TYPE_SIMPLE :
	  return "ACTION_TYPE_SIMPLE";
	  case ActionConstants.ACTION_TYPE_STAGE :
	  return "ACTION_TYPE_STAGE";
	  case ActionConstants.ACTION_TYPE_CARD :
	  return "ACTION_TYPE_BETTING";
	  case ActionConstants.ACTION_TYPE_BETTING :
	  return "";
	  case ActionConstants.ACTION_TYPE_TABLE_SERVER :
	  return "ACTION_TYPE_TABLE_SERVER";
	  case ActionConstants.ACTION_TYPE_ERROR :
	  return "ACTION_TYPE_ERROR";
	  case ActionConstants.ACTION_TYPE_PREBETTING :
	  return "ACTION_TYPE_PREBETTING";
	  case ActionConstants.ACTION_TYPE_LASTMOVE :
	  return "ACTION_TYPE_LASTMOVE";
	  case ActionConstants.ACTION_TYPE_KENO_PLAY :
	  return "ACTION_TYPE_KENO_PLAY";
	  case ActionConstants.ACTION_TYPE_KENO_RESULT :
	  return "ACTION_TYPE_KENO_RESULT";
	  case ActionConstants.ACTION_TYPE_BACCARAT_PLAY :
	  return "ACTION_TYPE_BACCARAT_PLAY";
	  case ActionConstants.ACTION_TYPE_BACCARAT_RESULT :
	  return "ACTION_TYPE_BACCARAT_RESULT";
	  case ActionConstants.ACTION_TYPE_BLACKJACK_RESULT :
	  return "ACTION_TYPE_BLACKJACK_RESULT";
	  case ActionConstants.ACTION_TYPE_BINGO_CALLED :
	  return "ACTION_TYPE_BINGO_GET_TICKETS";
	  case ActionConstants.ACTION_TYPE_BINGO_GET_TICKETS :
	  return "ACTION_TYPE_BINGO_GET_TICKETS";
	  case ActionConstants.ACTION_TYPE_SLOTS_RESULT :
	  return "ACTION_TYPE_SLOTS_RESULT";
	  case ActionConstants.ACTION_TYPE_SLOTS_PLAY :
	  return "ACTION_TYPE_SLOTS_PLAY";
	  case ActionConstants.ACTION_TYPE_VP_RESULT :
	  return "ACTION_TYPE_VP_RESULT";
	  case ActionConstants.ACTION_TYPE_VP_PLAY :
	  return "ACTION_TYPE_VP_PLAY";
	  case ActionConstants.ACTION_TYPE_CRAPS_PLAY :
	  return "ACTION_TYPE_CRAPS_PLAY";
	  case ActionConstants.ACTION_TYPE_TAMBOLA_CALLED :
	  return "ACTION_TYPE_TAMBOLA_CALLED";
	  case ActionConstants.ACTION_TYPE_TAMBOLA_GET_TICKETS :
	  return "ACTION_TYPE_TAMBOLA_GET_TICKETS";
	  case ActionConstants.ACTION_TYPE_HOLDEM_RESULT :
	  return "ACTION_TYPE_HOLDEM_RESULT";
	  default : return "Unknown Action "+_type;
	  }
  }

  public String toString() {
    return actionToString(this);
  }

  public abstract void handleAction(ActionVisitor v);

//	public void handleAction(ActionVisitor v) {
//		v.handleDefaultAction(this);
//	}
}
