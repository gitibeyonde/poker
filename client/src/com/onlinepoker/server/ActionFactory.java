package com.onlinepoker.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.golconda.game.GameType;
import com.golconda.game.PlayerStatus;
import com.golconda.game.util.ActionConstants;
import com.golconda.game.util.Card;
import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;
import com.onlinepoker.ClientPlayerModel;
import com.onlinepoker.Pot;
import com.onlinepoker.TotalBet;
import com.onlinepoker.actions.Action;
import com.onlinepoker.actions.BetRequestAction;
import com.onlinepoker.actions.CardAction;
import com.onlinepoker.actions.CashierAction;
import com.onlinepoker.actions.ErrorAction;
import com.onlinepoker.actions.InfoAction;
import com.onlinepoker.actions.LastMoveAction;
import com.onlinepoker.actions.MessageAction;
import com.onlinepoker.actions.NextMoveAction;
import com.onlinepoker.actions.PlayerJoinAction;
import com.onlinepoker.actions.SimpleAction;
import com.onlinepoker.actions.StageAction;
import com.onlinepoker.actions.TableServerAction;
import com.onlinepoker.actions.TableServerCloseOpenAction;
import com.onlinepoker.actions.TotalBetAction;
import com.onlinepoker.actions.TournamentWinAction;
import com.onlinepoker.actions.WinAction;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.skin.RoomSkinFactory;
import com.poker.game.PokerGameType;


/**
 * This class converts the GameEvent to various actions in the client
 */
public class ActionFactory {
    static Logger _cat = Logger.getLogger(ActionFactory.class.getName());
    public double _minBet, _maxBet, _buyin;
    public int _maxPlayer, _minPlayer;
    public GameType _type;
    public int _pos = -1;
    public int _rel_pos = -1;
    //private static int _mypos = -1;
    public String _tid;
    public int _dealer_pos;
    Card[] _hand = null;
    int _ccards = -1;
    int _last_betting_round = -97;
    public int _dealt_cards = -1;
    public int _grid = -99;
    public ServerProxy _player;
    public boolean _joined;
    public boolean _winner_animated = false;
    private LinkedList _gameEventQueue;
    private LinkedList _actionQueue;
    private long _startDelay;
    public double _money_at_table;
    Card[] _openHand = null; //
    public int betRound;
    public GameEvent old_ge = new GameEvent("oldge");
    
    private GameEvent global_ge=null;
    
    private boolean _refreshWhenCloseOpen = false;
//    static{
//    	_cat.setLevel(Level.OFF);
//    }

    public void reset() {
        _cat.finest("RESETTING STATE");
        _pos = -1;
        _tid = null;
        _hand = null;
        _dealt_cards = 0;
        _ccards = -1;
        _grid = -1;
        _joined = false;
        _dealer_pos = -1;
        _openHand = null;
    }
    //by rk
    public void resetMoneyAtTable(){
    	_money_at_table = 0.0;
    }

    public ActionFactory(String tid) {
        _tid = tid;
        _gameEventQueue = new LinkedList();
        _actionQueue = new LinkedList();
    }

    public String getTid() {
        return _tid;
    }

    public synchronized void addGameEvent(GameEvent ge) {
        _gameEventQueue.add(ge);
    }

    public GameEvent fetchGameEvent() {
        return (GameEvent)_gameEventQueue.removeFirst();
    }

    public synchronized void addAction(Object action) {
        _actionQueue.add(action);
    }

    public synchronized void addPriorityAction(Object action) {
        _actionQueue.addFirst(action);
    }

    public Object fetchAction() {
        try {
            Object oa = (Object)_actionQueue.getFirst();
            if (oa instanceof SimpleAction && 
                ((SimpleAction)oa).getId() == ActionConstants.DELAY) {
                if (_startDelay == -1) {
                    _startDelay = System.currentTimeMillis();
                    //_cat.finest("Start delay ..." + _startDelay);
                }

                else if ((System.currentTimeMillis() - _startDelay) > ((SimpleAction)oa).getDelayMills()) {
                	//System.out.println("Delay time: "+((SimpleAction)oa).getDelayMills()+" - "+((SimpleAction)oa).getDesc());
                    _actionQueue.removeFirst(); //discard the delay
                    _startDelay = -1;
                }
                return null;
            } else {
//            	if(oa instanceof TableServerCloseOpenAction)
//            		_cat.severe("Close Table "+((TableServerCloseOpenAction)oa).getOldTid());
//            	else if(oa instanceof StageAction && ((StageAction)oa).getId() == ActionConstants. START_GAME)
//            		_cat.severe("New Game is starting");
            	return (Object)_actionQueue.removeFirst();
            }
        } catch (java.util.NoSuchElementException e) {
            //ignore
        }
        return null;
    }


    public SimpleAction DELAY(int delayMills,String desc) {
        return new SimpleAction(ActionConstants.DELAY, 
                                ActionConstants.ACTION_TYPE_STAGE, delayMills, desc);
    }

    public SimpleAction UPDATE() {
        return new SimpleAction(ActionConstants.UPDATE, 
                                ActionConstants.ACTION_TYPE_STAGE);
    }
    
    /**
     * Returns actions which needs to be fired when a game event is received
     */
    public void processGameEvent(GameEvent ge, int event) {
    	 //_cat.severe("OLD----> "+old_ge.toString());
     	
    	//_cat.severe(event+"--"+ge.toString());
        boolean lm_shown = false;
        if (ge == null) {
        	_cat.fine("processGameEvent GE null");
        	return;
        }
        global_ge = ge;
        _player = ServerProxy.getInstance();
        boolean new_game=false;
        /**
         * 
         * * Tournament Win Action
         */
        /*TournamentWinAction twa = getTournyWinAction(ge);
        if (twa != null) {
        	//System.out.println("pge Tournament Winners "+twa.toString());
            addAction(twa);
            addAction(UPDATE());
        }*/
     
        /**
         *  Win Action
         */
        Object[] wa = getWinner(ge);
        if (wa != null) {
        	if(!_winner_animated)
        	{
        		_winner_animated = true;
	            Object[] ba = getLastMove(ge);
	            if (ba != null) {
//	            	_cat.info("Last Move in win action : "+ge.getLastMoveString());
	                for (int i = 0; i < ba.length; i++) {
	                    addAction(ba[i]);
	                    addAction(DELAY(300, "delay when win animation last-move"));//by rk
	                    lm_shown = true;
	                }
	                addAction(UPDATE());
	                //addAction(DELAY(300, "wa"));//1000 comment by rk
	            }
	            /**
	             * CommunityCards Action
	             */
	            // if community cards number has changed...the animate
	            Card cc[] = ge.getPrevCommunityCards();
	            if (cc != null && cc.length != _ccards){// && cc.length != _ccards) {
	                _cat.finest("Previous Communuity Working :- " + (cc.length - _ccards));
	                StageAction sa = new StageAction(ActionConstants.FLOP);
	                addAction(sa);            
	                Object ia = getInfoAction(ge);
	                if (ia != null) {
	                    addAction(ia);
	                    _cat.finest("INFO ACTION " + ia);
	                }
	                addAction(UPDATE());
	                _cat.finest("FLOP ACTION " + sa);
	                if (_ccards==-1 || (cc.length - _ccards)==5){
	                    // no cards were dealt
	                	_cat.fine("no cards dealt");
	                    Card ac[] = { cc[0], cc[1], cc[2] };
	                    CardAction cca = 
	                        new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
	                                       ac);
	                    addAction(cca); // set the community cards every time to stop tearing
	                    addAction(UPDATE());
	                    addAction(DELAY(300, "comm card action1"));
	                    
	                    Card act[] = { cc[0], cc[1], cc[2], cc[3] };
	                    cca = 
	                        new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
	                                       act);
	                    addAction(cca); // set the community cards every time to stop tearing
	                    addAction(UPDATE());
	                    addAction(DELAY(300, "comm card action2"));
	                    
	                    Card acr[] = { cc[0], cc[1], cc[2], cc[3], cc[4] };
	                    cca = 
	                        new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
	                                       acr);
	                    addAction(cca); // set the community cards every time to stop tearing
	                    addAction(UPDATE());
	                    addAction(DELAY(300, "comm card action3"));
	                }
	                else if ((cc.length - _ccards)==2){
	                   
	                    Card act[] = { cc[0], cc[1], cc[2], cc[3] };
	                    CardAction cca = 
	                        new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
	                                       act);
	                    addAction(cca); // set the community cards every time to stop tearing
	                    addAction(UPDATE());
	                    addAction(DELAY(300, "comm card action4"));
	                    
	                    Card acr[] = { cc[0], cc[1], cc[2], cc[3], cc[4] };
	                    cca = 
	                        new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
	                                       acr);
	                    addAction(cca); // set the community cards every time to stop tearing
	                    addAction(UPDATE());
	                    addAction(DELAY(300, "comm card action5"));
	                }
	                else if ((cc.length - _ccards)==1){
	                    
	                    Card acr[] = { cc[0], cc[1], cc[2], cc[3], cc[4] };
	                    CardAction cca = 
	                        new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
	                                       acr);
	                    addAction(cca); // set the community cards every time to stop tearing
	                    addAction(UPDATE());
	                    addAction(DELAY(300, "comm card action6"));
	                }
	              
	            } // END community cards present
	            
	          
	            StageAction psa = new StageAction(ActionConstants.PRE_WIN);
	            addAction(psa);              
	            addAction(UPDATE());
	            Object ia = getInfoAction(ge);
	            if (ia != null) {
	            	addAction(ia);
	                addAction(UPDATE());
	                _cat.finest("INFO ACTION " + ia);
	            }
	            addAction(DELAY(300, "after pre-win")); //900 comment by rk
	            
	            for (int i = 0; i < wa.length; i++) {
//	            	_cat.severe("winner loop wa.length "+wa.length);
	            	addAction(wa[i]);
	                addAction(UPDATE());
	                addAction(DELAY(500, "delay between show winner cards"));//500 //300 in between winners by rk
	            }
	            //addAction(DELAY(300, "after winning action"));//by rk
	            StageAction pwa = new StageAction(ActionConstants.POST_WIN);
	            addAction(pwa);
	            addAction(UPDATE());
	            addAction(DELAY(100,"after post-win")); //by rk earliar 900
	            _dealt_cards = 0;
	            StageAction sa = new StageAction(ActionConstants.END_GAME);
	            addAction(sa);
	             new_game=true;              
	            _ccards = -1;
        	}
            else
        	{
                _dealt_cards = 0;
                
                StageAction sa = new StageAction(ActionConstants.END_GAME);
                addAction(sa);
                 new_game=true;              
                _ccards = -1;
                
 	            return;
        	}
        }// WIN ACTION
        
        
         //_cat.finest("Grid = " + _grid);
         if (_grid != ge.getGameRunId()) {
        	 init(ge);    
             _dealt_cards = 0;
             //addPriorityAction(ge);
             addAction(ge);
             new_game=true;
              _winner_animated = false;
         }
        
         
      /**
       * LastMove Action
       */
         Object[] ba = getLastMove(ge);
         PlayerJoinAction _lpja = null;
         if (ba != null && !lm_shown) {
             for (int i = 0; i < ba.length; i++) {
                 if (ba[i] instanceof PlayerJoinAction){
                	_lpja = (PlayerJoinAction)ba[i];
                 }
             }
         }

         if (_lpja == null){
	        // Get this players detail
        	 String cpd[] = getClientPlayer(ge);
	        if (cpd != null) {
	            ClientPlayerModel plrm = new ClientPlayerModel(cpd);
	            plrm.setPlayerPosition(VIRTUAL(plrm.getPlayerPosition()));
	            plrm.setPlayerRelPosition(VIRTUAL(plrm.getPlayerPosition()));
//	            _cat.info("UPDATING PLAYER MODEL - "+event+", " + plrm);
	            addAction(plrm);
	            _money_at_table = plrm.getAmtAtTable();
///////////// this below code moved in lastplayerjoin action below///////////////
//				by rk for reposition after change table
//	            //_cat.severe("_refreshWhenCloseOpen "+_refreshWhenCloseOpen);
//	            if(_type.isTPoker() && _refreshWhenCloseOpen){
//	            	_cat.severe("reposition when changing table, event= "+event);
//	            	ClientPlayerModel[] cpm = getPlayerModels(ge);
//	                addAction(cpm);
//	                _refreshWhenCloseOpen = false;
/////////////}///////////////////////////////////////////////////////////////////
	        }
         }
         
       //here dealt cards code
        
        if (event != 
            Response.R_MOVE) { // if move response then update the poker and player model
            return;
        }

         /**
          * for show name plates.. fold,raise,bet plates,....
          */
        if (ba != null && !lm_shown) {
            for (int i = 0; i < ba.length; i++) {
            	//this if, Player leave action
            	if(((ba[i] instanceof TableServerAction) && ((TableServerAction)ba[i]).getId() == ActionConstants.PLAYER_LEAVE)){
            		addPriorityAction(ba[i]);
                }else if (!(ba[i] instanceof LastMoveAction)){//this else if, for all Actions
                    addPriorityAction(ba[i]);
                    addAction(DELAY(200,"for show plate when not LMA")); //commented by rk //earliar 900
                }else {
                	//if((ba[i] instanceof LastMoveAction) && ((LastMoveAction)ba[i]).getId()==ActionConstants.FOLD){
                	if(ba[i] instanceof LastMoveAction && "none".equals(ge.getNextMoveString())){
                		//check LM player PlayerStatus, if status is quickfold then don't add action
                		//here some player quickfolded and last-move coming as same but no next-move
                		String [][]ps = ge.getPlayerDetails();
                		if(ps!=null){
	                		String lm = ge.getLastMoveString();
	                			try {
									if (!"none".equals(lm)) {
										_cat.fine("opp player quickFolded");
										String lml[] = lm.split("\\|");
										int pos = Integer.parseInt(lml[0]);
										if (PlayerStatus.isQuickFolded(Long
												.parseLong(ps[pos][4]))) {
											_cat.fine("player quick folded and no need to add action");
										}
									}
								} catch (ArrayIndexOutOfBoundsException e) {
									_cat.fine("ArrayIndexOutOfBoundsException");
								}
                		}
                	}else{
	                	addAction(ba[i]);
	                	addAction(DELAY(400, "for show plate when LMA")); //commented by rk //earliar 900
                	}
                    //_cat.severe("show plate when not LMA"+((Action)ba[i]).getTarget());
                    //_cat.severe("Action = "+((Action)ba[i]).toString());
                }
            }
        }

        /**
         * Dealt cards action, when dealer dealt the cards
         */
        //this dealt cards action written by rk, earliar it was below before START_GAME action
        int dc = ge.getDealtCards();
        if (dc > 0 && dc != _dealt_cards) {
            CardAction ca = 
                new CardAction(ActionConstants.ACTION_TYPE_CARD, ActionConstants.ACTION_TYPE_STAGE, 
                               dc - _dealt_cards);
            addAction(ca);
            _cat.fine("Dealt cards");
            addAction(UPDATE());
            addAction(DELAY(900, "dealtcards")); //300 //900
            
            _dealt_cards = dc;
        }
        int betrnd = ge.getBettingRound();
        betRound = betrnd;
        //System.out.println("AF.betrnd "+betrnd);
        if (betrnd != -1 && _last_betting_round != betrnd){
            if (betrnd==0){
                StageAction sa = new StageAction(ActionConstants.PREFLOP);
                addAction(sa);
                addAction(UPDATE());
                _cat.fine("PRE-FLOP ACTION " + sa);
            }
            else if (betrnd==1){
                StageAction sa = new StageAction(ActionConstants.FLOP);
                addAction(sa);
                addAction(UPDATE());
                _cat.fine("FLOP ACTION " + sa);
            }else if (betrnd==2) {
                StageAction sa = new StageAction(ActionConstants.TURN);
                addAction(sa);
                addAction(UPDATE());
                _cat.fine("TURN ACTION " + sa);
            } else if (betrnd==3) {
                StageAction sa = new StageAction(ActionConstants.RIVER);
                addAction(sa);
                addAction(UPDATE());
                _cat.fine("RIVER ACTION " + sa);
            }else if (betrnd==4) {
                StageAction sa = new StageAction(ActionConstants.FOURTH_STREET);
                addAction(sa);
                addAction(UPDATE());
                _cat.fine("FOURTH STREET ACTION " + sa);
            }
            else {
                throw new IllegalStateException("Unknown round " + betrnd);
            }
            _last_betting_round = betrnd;
        }
        
        if (wa==null && ba != null){//if there is a win action the pot is cleared
            Object ia = getInfoAction(ge);
            if (ia != null) {
                addAction(ia);
                addAction(UPDATE());
                _cat.finest("INFO ACTION " + ia);
            }
        }
        
        /**
         * Adding dealer button on table
         */
        int dp = ge.getDealerPosition();
        if (_dealer_pos != dp) {
        	//_cat.severe("last pos "+_dealer_pos+" ,new dealer position "+dp+", VIRTUAL(dp) "+VIRTUAL(dp)+", table id "+ge.getGameName()+" ################");
            _dealer_pos = dp;
            addAction(new SimpleAction(ActionConstants.SET_BUTTON, VIRTUAL(dp)));
        }
        
        /**
         * Community cards showing
         */
        Card cc[] = ge.getCommunityCards();
        //System.out.println("community-cards "+ge.getCommunityCardsString());
        if (cc != null){// && cc.length != _ccards) {
        	//_cat.severe("Communuity Working : " + cc.length);
            _ccards = cc.length;
            CardAction cca = 
                new CardAction(ActionConstants.NO_SHOWDOWN, ActionConstants.BOARD_TARGET, 
                               cc);
            addAction(cca); // set the community cards every time to stop tearing
            addAction(UPDATE());
            addAction(DELAY(500,"community working")); //1000 //1800
        } // END community cards present

//////////Below code commented by rk to avoid 4-cards showing while dealt and written after LastMoveAction..
//        int dc = ge.getDealtCards();
//        if (dc > 0 && dc != _dealt_cards) {
//            CardAction ca = 
//                new CardAction(ActionConstants.ACTION_TYPE_CARD, ActionConstants.ACTION_TYPE_STAGE, 
//                               dc - _dealt_cards);
//            addAction(ca);
//            _cat.info("### dealt cards ###");
//            addAction(UPDATE());
//            addAction(DELAY(300, "dealtcards")); //900
//            _dealt_cards = dc;
//////// }   /////////////////////////////////////////////////////////
       
        /**
         * New Game
         */
        if (new_game){
            // NEW GAME
        	//_cat.severe("New Game Strarting");
            StageAction sa = new StageAction(ActionConstants.START_GAME);
            addAction(sa);
        }
        
        /**
         * ClientPlayerModel[] action
         **/
/////////By rk, keep cond to not to re-position when me folds, b'z it re-position on old table
//        String ss = ge.getLastMoveString();
//        if (ss != null && !ss.startsWith("-1|none|") && !ss.equals("none")) {
//            String ssa[] = ss.split("\\|");
//            int move = getAction(ssa[2]);
//            String nm = ssa[1];
//            if(_type.isTPoker()){
//            	if(!(_player._name.equals(nm) && move == ActionConstants.FOLD )){
//	            	 ClientPlayerModel[] cpm = getPlayerModels(ge);
//	                 if(cpm !=null)
//	                 addAction(cpm);
//            	}else{
//            		//_cat.severe("LM## me folded so no need to add CPM action "+ss.toString());
//            	}
//            }else{
            	ClientPlayerModel[] cpm = getPlayerModels(ge);
                if(cpm !=null){
                //_cat.severe("ClientPlayerModel[], event= "+event);
                addAction(cpm);
                }
//            }
//        }
//////////commented by rk, and re-write above //////////////////
//        ClientPlayerModel[] cpm = getPlayerModels(ge);
//        if(cpm !=null)
//        addAction(cpm);
////////////////////////////////////////////////////////////////
        /**
         * PLAYER_JOIN action
         **/
        if (_lpja != null){
        	addAction(_lpja);
///////  	if(_lpja.isMe()){
//	           // _refreshWhenCloseOpen = true;
////**** earliar written in Last Move Action (lpja == null) cond, so player re-position to next GE,now written here
//	            if(_type.isTPoker() /*&& _refreshWhenCloseOpen*/){
//	            	_cat.severe("reposition when changing table, event= "+event);
//	            	ClientPlayerModel[] cpm1 = getPlayerModels(ge);
//	               addAction(cpm1);
//	               // _refreshWhenCloseOpen = false;
//	            }
////*******************************************************************
//        	}
//        	_cat.severe("Player join "+_lpja.getSeat()+",name= "+
//        			_lpja.getPlayer().getPlayerName()+",Pos= "+
//        			_lpja.getPlayer().getPlayerPosition()+",relPos= "+
//        			_lpja.getPlayer().getPlayerRelPosition());
        }
        /**
         * Showing player Hand cards
         */
        Card[] hand = ge.getHand();
        addAction(hand);
        //_cat.severe("hand cards action");
        //System.out.println("hand cards "+ge.getHandCardsString());
        addAction(UPDATE());//by rk
        int nmpos = getNextMovePos(ge);
        if (nmpos != -1) {
            addAction(new SimpleAction(ActionConstants.SET_CURRENT, VIRTUAL(nmpos)));
//            _cat.info("Next Move Position "+getNextMovePos(ge)+" VPos: "+VIRTUAL(nmpos));
        }
        
        /**
         * BetRequestAction
         */
        BetRequestAction bra = getMyNextMove(ge);
        if (bra != null) {
//        	_cat.info("My Next Move Action: "+ge.getNextMoveString());
            addAction(bra);
            addAction(UPDATE());
        }
        
        /**
         * NextMoveAction
         */
        NextMoveAction nm = getNextMove(ge);
        if (nm != null) {
//        	_cat.info("Next Move Action: "+ge.getNextMoveString());
        	addAction(nm);
            addAction(UPDATE());
        }
        old_ge = ge;
    }
    //resize code
    public void resizeProcessGameEvent(GameEvent ge, int event) {
    	processGameEvent(ge, event);
    }
    public GameEvent getGlobal_ge(){
  	  return global_ge;
    }
    

    public void init(GameEvent ge) {
        _grid = ge.getGameRunId();
        _tid = ge.getGameName();
        _type = new PokerGameType(ge.getType());
        if (ge.getType() != -1) {
            _cat.finest("INITIALIZED GE");
            // initialize table fundamentals if available
            _minPlayer = Integer.parseInt(ge.get("min-players"));
            _maxPlayer = Integer.parseInt(ge.get("max-players"));
            if (_maxPlayer <=0)_maxPlayer=10;
            //if (ge.getType() == 1 || ge.getType() == 256 ||    ge.getType() == 2 || ge.getType() == 4 || ge.getType() == 128 || ge.getType() == 512 || ge.getType() == 1024) {
          
                _minBet = Double.parseDouble(ge.get("min-bet"));
                _maxBet = Double.parseDouble(ge.get("max-bet"));
           if (ge.getType() == PokerGameType.HoldemTourny || ge.getType() == PokerGameType.OmahaHiTourny) {
                _buyin = 
                        Double.parseDouble(ge.get("buyin") == null ? "0" : ge.get("buyin"));
            } 
        }
    }

    public void processCashierAction(CashierAction ca){
    	addPriorityAction(ca);
    }
    
    
    public ClientPlayerModel[] getPlayerModels(GameEvent ge) {
      //      Players at the desk
      String[][] plrs = ge.getPlayerDetails();
      ClientPlayerModel[] playersMod = new ClientPlayerModel[_maxPlayer];
      for (int i = 0; plrs != null && i < plrs.length; i++) {
    	  //_cat.severe("before VIRTUAL POS "+plrs[i][0]+" - "+plrs[i][3]);
          int pos = VIRTUAL(Integer.parseInt(plrs[i][0]));
          plrs[i][0] = pos+"";
          playersMod[(pos)] = new ClientPlayerModel(plrs[i]); 
         // _cat.severe("after VIRTUAL POS "+plrs[i][0]+" - "+plrs[i][3]);
      }
      return playersMod;
  } 
  
  public Card[] getDiscards(GameEvent ge){
      Card[] ret;
      String dc = ge.get("discards");
      if (dc != null){
          String[] dca = dc.split("'");
          _cat.finest("Discards str=" + dc);
          ret = new Card[dca.length];
          for (int i=0;i<dca.length;i++){
              ret[i] = new Card(dca[i]);
              _cat.finest("Discards=" + ret[i].toString());
          }
          return ret;
      }
      return null;
  }
    
    
    public    int getNextMovePos(GameEvent ge) {
        int pos = -1;
        String nm[][] = ge.getMove();
        if (nm != null) {
            pos = Integer.parseInt(nm[0][0]);
        }
        return pos;
    }
    
    public void sitin(){
        addPriorityAction(new TableServerAction(ActionConstants.PLAYER_SITIN, _pos));
    }

    public void sitout(){
        addPriorityAction(new TableServerAction(ActionConstants.PLAYER_SITOUT, _pos));
    }

    public void closeRoom(){
        addAction(new TableServerAction(ActionConstants.GRACEFUL_SHUTDOWN, _pos));
    }
    
    public void closeOpenRoom(String old_tid,GameEvent ge){
    	addAction(new TableServerCloseOpenAction(old_tid,ge));
    	//addAction(DELAY(1000, "for close old table"));
    	
    }
    public void showMessage(String tid, String message){
    	addAction(DELAY(100, "showing message banner"));
    	addAction(new MessageAction(message));
    }
    public InfoAction getInfoAction(GameEvent ge) {
    	String[][] plrs = ge.getPlayerDetails();
        String pd[] = getClientPlayer(ge);
        double raise_amt = 0, bet_amount = 0, total_amt = 0;
        for (int i = 0; plrs != null && i < plrs.length; i++) {
        	total_amt += Double.parseDouble(plrs[i][2]);
        }
        if (pd != null) {
            bet_amount = Double.parseDouble(pd[2]);
            raise_amt = ge.getRaiseAmount();
        }

        String pots[][] = ge.getPot();//,pots=main|17.00
        Pot[] np = new Pot[1];
        if (pots != null){
        	np = new Pot[pots.length];
        	for (int i=0;i<pots.length;i++){
        		np[i] = new Pot(pots[i][0], Double.parseDouble(pots[i][1]), total_amt);
        	}
        }
        else {
        	np[0] = new Pot("", 0, total_amt);
        }
        
        InfoAction ia = 
            new InfoAction(bet_amount, np, ge.getRake(), (raise_amt >  0) ? true : false, raise_amt);
        return ia;

    }
    
    public TotalBetAction getTotalBetAction(GameEvent ge) {
    	String[][] plrs = ge.getPlayerDetails();////player-details=0|21.62|0.00|NeunGil|16|1|0||null|`
    	double total_amt = 0;
        for (int i = 0; plrs != null && i < plrs.length; i++) {
        	total_amt += Double.parseDouble(plrs[i][2]);
        }
        
        String pots[][] = ge.getPot();//,pots=main|17.00
        if (pots != null){
        	for (int i=0;i<pots.length;i++){
        		total_amt += Double.parseDouble(pots[i][1]);
        	}
        }
              
        TotalBetAction ia = 
            new TotalBetAction(new TotalBet(total_amt, ge.getRake()));
        return ia;

    }

    public BetRequestAction getMyNextMove(GameEvent ge) {
        int targetPos = 
            Integer.parseInt(ge.get("target-position") == null ? "-1" : 
                             ge.get("target-position"));
        if (targetPos == -1) {
            return null;
        }
        String nm[][] = ge.getMove();
        if (nm != null) {
            int pos = Integer.parseInt(nm[0][0]);
            int move = getAction(nm[0][1]);
            if (move == ActionConstants.UNKNOWN_ERROR) {
                return null;
            }

            if (move == ActionConstants.IS_ACCEPTING) {
                // this player can join the table
                //open table
            }

            // Loop through all the moves
            int[] actions = new int[nm.length];
            double[] amount = new double[nm.length];
            double[] amount_limit = new double[nm.length];

            // create the Bet Request Action
            //System.out.println("Next move " + move + ", pos=" + _pos + 
                        //", targetPos= " + targetPos + ", joined=" + _joined);
            if (_joined && targetPos == pos) {
            	String t[][] = nm;
                SortedMap<Integer, String> sm = new TreeMap<Integer, String>();
                for(int i =0; i <nm.length;i++){
                    sm.put(getAction(nm[i][1]), nm[i][2]);
        	}
        	Iterator iterator = sm.keySet().iterator();
        	int i = 0;
        	while (iterator.hasNext()) {
        		  int key = ((Integer)iterator.next()).intValue();
        		  String value = sm.get(key);
        		  //System.out.println(key +" - "+value);
        		  actions[i] = key;
        		  if (value.indexOf("-") == -1) {
                      amount[i] = Double.parseDouble(value);
                    amount_limit[i] = -99; //indicates no slider
                } else {
                      String str = value.split("-")[0];
                      String end = value.split("-")[1];
                    amount[i] = Double.parseDouble(str);
                    //note: >0(range), ==0(potlimit), ==-1(nolimit)
                    amount_limit[i] = Double.parseDouble(end);
                }
        		  i++;
            }

                /*for (int i = 0; i < nm.length; i++) {
                    actions[i] = getAction(nm[i][1]);
                    if (nm[i][2].indexOf("-") == -1) {
                        amount[i] = Double.parseDouble(nm[i][2]);
                        amount_limit[i] = -99; //indicates no slider
                    } else {
                        String str = nm[i][2].split("-")[0];
                        String end = nm[i][2].split("-")[1];
                        amount[i] = Double.parseDouble(str);
                        //note: >0(range), ==0(potlimit), ==-1(nolimit)
                        amount_limit[i] = Double.parseDouble(end);
                    }
                }*/
                BetRequestAction bra = 
                    new BetRequestAction(ActionConstants.BET_REQUEST, VIRTUAL(pos), 
                                         _minBet, _maxBet, actions, amount, 
                                         amount_limit);
                /*if(_player.getAF(_tid) == null)
                {
                	System.out.println();
                	_player.sendToServer(_tid, 
                		new BettingAction(ActionConstants.FOLD,_pos,0));
                    
                }*/
                _cat.finest(pos + " Next Move = " + bra);
                return bra;
            }
        }
        return null;
    }
    
    public NextMoveAction getNextMove(GameEvent ge) {
    	String nm[][] = ge.getMove();
        if (nm != null) {
            String move = nm[0][1];
            if (move.equals("none") || move.equals("wait"))return null;
            int pos = Integer.parseInt(nm[0][0]);
            return new NextMoveAction(VIRTUAL(pos));
        }
        return null;
    }

    public Object[] getLastMove(GameEvent ge) {  
    	//_cat.severe("Last Move : "+ge.getLastMoveString());
        int targetPos = 
        Integer.parseInt(ge.get("target-position") == null ? "-1" : 
            ge.get("target-position"));
        String[][] plrs = ge.getPlayerDetails();
        Object[] a = new Object[1];
        String lm = ge.getLastMoveString();
        //System.out.println(lm);
        if (lm != null && !lm.startsWith("-1|none|") && !lm.equals("none")) {
            String lml[] = lm.split("\\|");
            int pos = Integer.parseInt(lml[0]);
            int move = getAction(lml[2]);
            double amt = Double.parseDouble(lml[3]);
            //System.out.println(lml[1] + ", position=" + pos + ", Last Move = " +  lml[2] + ", amount =" + amt);
            String lmp[] = null;
            int i = -1;
            boolean last_move_player_present = false;
            for (i = 0; plrs != null && i < plrs.length; i++) {
                if (pos == Integer.parseInt(plrs[i][0])) {
//                    _cat.severe(" Player " + plrs[i][3] +   " makes the last move "+ lm);
                    lmp = plrs[i];
                    last_move_player_present = true;
                    break;
                }
            } // getting the last move player as lmp

            if (move == ActionConstants.PLAYER_JOIN) {
            	if (_player._name.equals(lml[1])) { // this player joins
            		_rel_pos = pos;
            		_pos = _rel_pos;
                    _joined = true;
                    //----loading to the virtual position-----
                    lmp[0] = VIRTUAL(Integer.parseInt(lmp[0]))+"";
                   //----------------------------------------
// commented by rk, as it causing wrong chip count in lobby                    
//                    if(!_type.isTourny())
//                    {
//	                    if (_type.isPlay() && ServerProxy._play_worth > amt){
//	                    	ServerProxy._play_worth -= amt;
//	                    }
//	                    else if(_type.isReal() &&ServerProxy._real_worth > amt){
//	                    	ServerProxy._real_worth -= amt;
//	                    	new Exception("_real_worth in AF "+ServerProxy._real_worth).printStackTrace();
//	                    }
//                    }
                    if (targetPos != pos){
                    }
                    a[0] = new PlayerJoinAction(VIRTUAL(pos), new ClientPlayerModel(lmp), true);
                    _cat.finest("virtual pos lmp[0] "+lmp[0]+",VIRTUAL(pos) "+VIRTUAL(pos));
                    //_cat.severe("present table id= "+_tid+", new player join on table= "+ge.getGameName());
            		
                }
                else{
                	a[0] = new PlayerJoinAction(VIRTUAL(pos), new ClientPlayerModel(lmp), false);
                	//_cat.severe("present table id= "+_tid+", new player join on table= "+ge.getGameName());
                }
                return a;
            } else if (move == ActionConstants.PLAYER_LEAVE || move == ActionConstants.PLAYER_SITOUT) {
            	PokerGameType pgt = new PokerGameType(ge.getType());
                if(pgt.isTPoker()) //if added by rk
            	{
                	a[0] = new TableServerAction(ActionConstants.PLAYER_LEAVE, VIRTUAL(pos), lml[1]);//3rd parameter added by rk, to show in chat message
                	_cat.fine("Leave "+VIRTUAL(pos)+", name "+lml[1]);
                	return a;
            	}else{
                	a[0] = new TableServerAction(ActionConstants.PLAYER_LEAVE, VIRTUAL(pos));
                	return a;
            	}
                
            } else if (move == ActionConstants.PLACE_OCCUPIED) {
                a[0] = new ErrorAction(ActionConstants.PLACE_OCCUPIED, VIRTUAL(pos));
            } // LAST MOVE -- NONE
            else { // ALL OTHER MOVES
                //String pots[][] = ge.getPot();
                //TODO integrate all the pots -- support for multinple pots...? -sn-
                // int id, int target, double cur_bet, double pot, double rake, double bet, double amt_at_table, boolean me)
            	PokerGameType pgt = new PokerGameType(ge.getType());
                if (!last_move_player_present && !_type.isTourny() && !pgt.isTPoker()){                    
                    JOptionPane.showInternalMessageDialog(_player._owner,
                        "Unable to seat player ! Please, leave table and try again.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                Object tba = getTotalBetAction(ge);
                if (tba != null) {
                    addAction(tba);
                    addAction(UPDATE());
                    _cat.finest("INFO ACTION " + tba);
                }
                
                LastMoveAction ba = null;
				try {
					//by rk
	                if(last_move_player_present == false && move == ActionConstants.FOLD){
	                	String[][] oldPlrs = old_ge.getPlayerDetails();
	                	if(oldPlrs != null){
		                	ba = new LastMoveAction(move, VIRTUAL(pos), amt, 
				                					Double.parseDouble(oldPlrs[i][2]),
				                					Double.parseDouble(oldPlrs[i][1]),
				                					VIRTUAL(pos) == this._pos);
		                	_cat.finest("LastMoveAction is FOLD when lm player not present");
	                	}
	                }else{
	                	ba = new LastMoveAction(move, VIRTUAL(pos), amt, 
                                       Double.parseDouble(plrs[i][2]), 
                                       Double.parseDouble(plrs[i][1]), 
                                       VIRTUAL(pos) == this._pos );
	                }
				} catch (Exception e) {
					
				}
                _cat.fine("Last Move = " + ba );
                a[0] = ba;
                return a;
            }
        } //last move is not none
        return null;
    }


    //TODO - Handle multiple winners tooo...

    public Object[] getWinner(GameEvent ge) {
        Vector action = new Vector();
        String[][] winner = ge.getWinner();
        String[][] open = ge.getOpenHands();
        if (open != null) {
            for (int j = 0; j < open.length; j++) {
                _cat.finest("Open hands : " + open[j][0] + ", " + open[j][1]);
                int pos = Integer.parseInt(open[j][0]);
                if(open[j][1] == null)break;
                String crds[] = open[j][1].split("'");
                Card[] openHand = new Card[crds.length];
                for (int k = 0; k < crds.length; k++) {
                    openHand[k] = new Card(crds[k]);
                    openHand[k].setIsOpened(true);
                }
                CardAction oca = 
                    new CardAction(ActionConstants.SHOW_SHOWDOWN_CARD, VIRTUAL(pos), 
                                   openHand);
               // _cat.severe("Show Open Cards " + oca);
                addAction(oca);
            }
        }

        if (winner == null) {
            return null;
        }

        Card[] combination = new Card[0];
        String winMsg = "Everyone else folded";
        Card[] hand = null;
        if (winner != null) {
            int idx = 0;
            for (int i = 0; i < winner.length; i++) {
                int pos = Integer.parseInt(winner[i][1]);
                String name = winner[i][2];
                double amt = Double.parseDouble(winner[i][3]);
                _cat.finest("In winner Loop " + winner[i][4]);

                if (winner[i][4] != null) {
                    String[] card_array = winner[i][4].split("'");
                    hand = new Card[card_array.length];
                    for (int j = 0; j < hand.length; j++) {
                        hand[j] = new Card(card_array[j]);
                        _cat.finest("Winning Hand =" + hand[j]);
                    }

                    card_array = winner[i][5].split("'");
                    combination = new Card[card_array.length];
                    for (int j = 0; j < combination.length; j++) {
                        combination[j] = new Card(card_array[j]);
                        _cat.finest("Winning Combination =" + combination[j]);
                    }

                    winMsg = winner[i][6];
                   // System.out.println("winMsg "+winMsg);

                    if (hand != null && hand.length > 1) {
                        CardAction cardsa = 
                            new CardAction(ActionConstants.SHOW_WINNER_CARD, 
                            		VIRTUAL(pos), hand);
                        action.add(cardsa);
                    }

                }
                
                WinAction winnera = 
                    new WinAction(VIRTUAL(pos), name, amt, combination, winMsg, open, betRound);

                winnera.setPotName(winner[i][0]);
                _cat.finest("Winaction = " + winnera);
                action.add(winnera);
            }
        }
        return (Object[])action.toArray(new Object[action.size()]);
    }
    
    public TournamentWinAction getTournyWinAction(GameEvent ge) {
        String twinners[][] = ge.getTournyWinners();
        if (twinners != null) {
        	System.out.println("getTorurnyWinAction "+twinners.toString());
            String[] names = new String[10];
            double[] sums = new double[10];
            for (int i = 0; i < twinners.length; i++) {
                sums[i] = Double.parseDouble(twinners[i][1]);
                names[i] = twinners[i][2];
               
            }

            TournamentWinAction twa = 
                new TournamentWinAction(ActionConstants.TOURNAMENT_WIN, names, 
                                        sums);
            return twa;
        }
        return null;
    }

    public String[] getClientPlayer(GameEvent ge) {
    	if (_type.isTourny()){
    		_pos = ge.getTargetPosition();
    	}
    	if (_pos == -1)return null;
        String pd[][] = ge.getPlayerDetails();
        for (int i = 0; pd != null && i < pd.length; i++) {
            if (Integer.parseInt(pd[i][0]) ==  _pos) { 
            // _cat.finest("Player Details" + pd[i]);
            	_joined = true;
                return pd[i];
            }
        }
        return null;
    }
    
    /*************************************************************************
     *  MAPPINGS
     *************************************************************************/
    public static int getAction(String mov) {
        int mov_id;
        _cat.finest("getAction(String mov) = " + mov);
        if (mov.equals("join")) {
            mov_id = ActionConstants.PLAYER_JOIN;
        } else if (mov.equals("move")) {
            mov_id = ActionConstants.PLAYER_JOIN;
        }else if (mov.equals("check")) {
            mov_id = ActionConstants.CHECK;
        } else if (mov.equals("call")) {
            mov_id = ActionConstants.CALL;
        } else if (mov.equals("raise")) {
            mov_id = ActionConstants.RAISE;
        } else if (mov.equals("fold")) {
            mov_id = ActionConstants.FOLD;
        } else if (mov.equals("leave")) {
            mov_id = ActionConstants.PLAYER_LEAVE;
        } else if (mov.equals("sit-in")) {
            mov_id = ActionConstants.PLAYER_SITIN;
        } else if (mov.equals("opt-out")) {
            mov_id = ActionConstants.PLAYER_SITOUT;
        } else if (mov.equals("small-blind")) {
            mov_id = ActionConstants.SMALL_BLIND;
        } else if (mov.equals("big-blind")) {
            mov_id = ActionConstants.BIG_BLIND;
        } else if (mov.equals("bet")) {
            mov_id = ActionConstants.BET;
        } else if (mov.equals("ante")) {
            mov_id = ActionConstants.ANTE;
        }
        else if (mov.equals("morning")) {
          mov_id = ActionConstants.MORNING;
        }
        else if (mov.equals("afternoon")) {
          mov_id = ActionConstants.AFTERNOON;
        }
        else if (mov.equals("evening")) {
          mov_id = ActionConstants.EVENING;
        } else if (mov.equals("all-in")) {
            mov_id = ActionConstants.ALLIN;
        } else if (mov.equals("bringin")) {
            mov_id = ActionConstants.BRINGIN;
        } else if (mov.equals("sb-bb")) {
            mov_id = ActionConstants.SB_BB;
        } else if (mov.equals("none")) {
            mov_id = ActionConstants.PLAYER_NONE;
        } else {
            mov_id = ActionConstants.UNKNOWN_ERROR;
        }
        return mov_id;
    }

    public static int getMove(int action) {
        _cat.finest("############################################# action = " + 
                    action);
        int mov_id;
        if (action == ActionConstants.PLAYER_JOIN) {
            mov_id = Command.M_JOIN;
        } else if (action == ActionConstants.CHECK) {
            mov_id = Command.M_CHECK;
        } else if (action == ActionConstants.CALL) {
            mov_id = Command.M_CALL;
        } else if (action == ActionConstants.RAISE) {
            mov_id = Command.M_RAISE;
        } else if (action == ActionConstants.FOLD) {
            mov_id = Command.M_FOLD;
        } else if (action == ActionConstants.PLAYER_LEAVE) {
            mov_id = Command.M_LEAVE;
        } else if (action == ActionConstants.PLAYER_SITIN) {
            mov_id = Command.M_JOIN;
        } else if (action == ActionConstants.PLAYER_SITOUT) {
            _cat.finest("#################### else if (action == ActionConstants.PLAYER_SITOUT) #################################");
            mov_id = Command.M_OPT_OUT;
        } else if (action == ActionConstants.SMALL_BLIND) {
            mov_id = Command.M_SMALLBLIND;
        } else if (action == ActionConstants.BIG_BLIND) {
            mov_id = Command.M_BIGBLIND;
        } else if (action == ActionConstants.BET) {
            mov_id = Command.M_BET;
        } else if (action == ActionConstants.ALLIN) {
            mov_id = Command.M_ALL_IN;
        } else if (action == ActionConstants.BRINGIN) {
            mov_id = Command.M_BRING_IN;
        } else if (action == ActionConstants.SB_BB) {
            mov_id = Command.M_SBBB;
        } else if (action == ActionConstants.ANTE) {
            mov_id = Command.M_ANTE;
        }
        else {
            mov_id = Command.M_NONE;
        }
        _cat.finest("move id = " + mov_id);
        return mov_id;
    }
    
    public int VIRTUAL(int pos) {
    	if(!_joined || pos == -1)return pos;
    	int defaultPos;
    	//RoomSkin skin = RoomSkinFactory.getRoomSkin(_maxPlayer);
    	//defaultPos = skin.getDefalutPos(_maxPlayer);
    	defaultPos = RoomSkin.getDefalutPos(_maxPlayer);
    	int rel_pos = pos - _rel_pos;
		if (rel_pos < 0)rel_pos += _maxPlayer;
		rel_pos += defaultPos;
		if(rel_pos > (_maxPlayer-1))rel_pos -= _maxPlayer;
		
		//System.out.println("pos= "+pos+", virtual pos= "+rel_pos);
		
		return rel_pos;
    }
    	
}
