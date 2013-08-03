package com.onlinepoker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import com.golconda.game.PlayerStatus;
import com.golconda.game.util.ActionConstants;
import com.golconda.game.util.Card;
import com.golconda.message.GameEvent;
import com.onlinepoker.actions.Action;
import com.onlinepoker.actions.BetRequestAction;
import com.onlinepoker.actions.BettingAction;
import com.onlinepoker.actions.CardAction;
import com.onlinepoker.actions.CashierAction;
import com.onlinepoker.actions.ChatAction;
import com.onlinepoker.actions.LastMoveAction;
import com.onlinepoker.actions.PlayerJoinAction;
import com.onlinepoker.actions.TableServerAction;
import com.onlinepoker.actions.TotalBetAction;
import com.onlinepoker.actions.WinAction;
import com.onlinepoker.lobby.LobbyUserImp;
import com.onlinepoker.server.ServerMessagesListener;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.util.MessageFactory;
import com.onlinepoker.util.MyHandHistoryDialog;
import com.onlinepoker.util.MyPlayerNoteDialog;
import com.onlinepoker.util.OddsDialog;
import com.onlinepoker.util.Statistics;
import com.poker.game.PokerGameType;
import com.poker.game.util.Hand;



/// DDDDDDD	import com.onlinepoker.client.util.AdminUtil;

public class ClientPokerModel implements Painter, Runnable, ActionConstants {
    static Logger _cat = Logger.getLogger(ClientPokerModel.class.getName());
    public long _grid = 0;
    public long _prev_grid;
    public String _prev_name;
    public String name;
    private int state;
    protected PokerGameType gameType;
    protected double minBet, maxBet, fees;
    protected int minPlayer, maxPlayer;

    protected ClientPlayerModel[] _playersMod;
    protected double bet = 0;
    protected Pot[] pot = null;
    protected TotalBet tb = null;
    protected MessageBanner mb = null;
    protected double rake = 0;
    protected String _infoMsgBanner = "";
    
    private MyHandHistoryDialog hh_dialog;
    private MyPlayerNoteDialog player_note;
	
    /** to make winning combination available in paint() */
    Card[] winnerCards;
    String _openHands;
    String[] _openHands1;
    Card[] openHands;
    protected Vector openCards = new Vector();

    /** ClientPoker View of MVC struct */
    private ClientPokerView _view = null;

    /** Room _skin */
    protected RoomSkin _skin;

    /** Cards params and icons */
    private Point centerCardsPlace;
    private Point discardCardsPlace;
    private Point dialerCardsPlace;
    private ImageIcon closeCard;
    //private ImageIcon openCard;


    /** Root component (_owner) */
    protected ClientPokerController _owner;

    /** Players */
    protected ClientPlayerController[] _players;

    /** Chips on a desk */
    protected Vector movingChips = new Vector();
    protected double movingChipsAmt = 0.0;
    
    /** Rake Chips on a desk */
    protected Vector<Chip> rakeChips = new Vector<Chip>();

    /** Cards on a desk */
    public Vector<PlCard> deskCards = new Vector<PlCard>();
    long _cc=0;
    private Vector<PlCard> _discards = new Vector<PlCard>();//discarded cards

    /** Service variable for optration with moving pots chip */
    private int addingPot = 0;

    /** Bottom panel - user input and output */
    protected BottomPanel _bottomPanel;

    /** Self commented name */
    private DealerChip dealerChip = null; //earliar it was static so chip moving on table 
    									 //	when open 2 tables made it as non-static by rk
    
    /** Vector with cards for dialing */
    private Vector dealingCardVector = new Vector();
    private List movingCards;
    private ConcurrentHashMap playerCards = new ConcurrentHashMap();

    /** Timer for dealing cards */
    private Timer utilTimer = new Timer();

    /** Must _view old hand id  */
    public boolean showNamePlateAgain = true;

    /** if we wait for server response on SIT_IN action - waitRespone == true */
    private boolean waiting_for_response = false;
    private int _reservedPosition = -1;
    private boolean proceeded = false;

    /** check for tournament games */
    private boolean isTournamentRunning;
    private String[] _partners;
    
    
    public ClientPlayerModel _me=null;
    GameEvent _ge;
    MoveTimer _moveTimer=null;
    Timer _mt=null;
    Timer _moveBgTimer = null;
    static OddsDialog od;
    boolean fullscreen = false;
    int _moveBgX = 0, _moveBgY = 0, temp = 0;;
    double _rake;
    
    //resize code
    public final static int CUNSTROCTOR=0;
	private final static int RESIZE=1;
    /**for Stats*/
    int round = -1;
    int foldedRound = -1;
    boolean isFolded = false;
  boolean isquickFolded = false;
    Statistics _statistics;
    public ClientPokerModel(GameEvent ge, RoomSkin _skin, 
                            ClientPokerController _owner, 
                            BottomPanel _bottomPanel) {
        this._skin = _skin;
        this._owner = _owner;
        this._bottomPanel = _bottomPanel;
        this.movingCards = new ListWithPoster(_owner);
        _players = new ClientPlayerController[_skin._roomSize]; 
        _ge = ge;
        _grid = ge.getGameRunId();
        name = ge.get("name");
        int type=ge.getType();
        if (type !=-1){
            gameType = new PokerGameType(type);
        }
        name = ge.get("name");
        minBet = ge.getMinBet();
        maxBet = ge.getMaxBet();
        minPlayer=ge.getMinPlayers();
        maxPlayer=ge.getMaxPlayers();
        _statistics = new Statistics();

        //resize code
        refreshPokerModel(ge,CUNSTROCTOR);
        //refreshPokerModel(ge);
    }
    //resize code
    int sel_pos = -1;
    public void refreshPokerModel(GameEvent ge, int from) {
        _cat.finest(this.toString()); // Desk cards
        if(ge != null){
	        _cat.fine("GLOBAL ge "+ge.toString());
		    name = ge.getGameName();
		    _grid = ge.getGameRunId();
		    _ge=ge;
	        state = ONLINE;
	        Card[] cards = ge.getCommunityCards();
	        deskCards.clear();
	        centerCardsPlace = _skin.getCenterCardsPlace();
	        //System.out.println("in cpm center cards place "+centerCardsPlace.x+","+centerCardsPlace.y);
	        _discards.clear();
	        tb = null;// to clear Total bet Banner
	        discardCardsPlace = _skin.getDiscardCardsPlace();
	        dialerCardsPlace = _skin.getDialerCardsPlace();
	        //openCard = _skin.getOpenCards();
	        closeCard = _skin.getCloseCard();
	
	        for (int i = 0; cards != null && i < cards.length; i++) {
	        	deskCards.add(new PlCard(centerCardsPlace/*, openCard*/, cards[i], i, _owner,_skin));
	        }
	         
	        if (ge.getDealerPosition() >= 0 && ge.getDealerPosition() < 10) {
	           dealerChip = new DealerChip(ge.getDealerPosition(), _skin, _owner);
	        }
	        
	        
	        //	Players at the desk
	        String[][] plrs = ge.getPlayerDetails();
	        _playersMod = null;//by rk, for GC
	        _playersMod = new ClientPlayerModel[_skin._roomSize];
	        for (int i = 0; plrs != null && i < plrs.length; i++) {
	            int pos = Integer.parseInt(plrs[i][0]);
	            _playersMod[pos] = new ClientPlayerModel(plrs[i]);  
	            if(_playersMod[(pos)].getStatus() == 0)_playersMod[(pos)].setStatus(PlayerStatus.NEW);
	        }
	        //resize code
	        if(from==CUNSTROCTOR)
				refreshPlayersModel(_playersMod, null);
			else if(from==RESIZE){
				refreshPlayersModelResize(_playersMod, null);
				refreshMyHand(ge.getHand());
		        //refreshPlayersModel(_playersMod, null);//by rk
			}
		        
	        //refreshPlayersModel(_playersMod, null);
	        if (gameType.isMTTTourny()){
	            _partners = ge.getPartners();
	        }
	        _owner.repaint();
        }
    }

    /**public void refreshPokerModel(GameEvent ge) {
    	//_cat.severe("ClientPokerModel.refreshPokerModel");
        _cat.finest(this.toString()); // Desk cards
	    name = ge.getGameName();
	    _grid = ge.getGameRunId();
	    _ge=ge;
        state = ONLINE;
        Card[] cards = ge.getCommunityCards();
        deskCards.clear();
        centerCardsPlace = _skin.getCenterCardsPlace();
        _discards.clear();
        tb = null;// to clear Total bet Banner
        discardCardsPlace = _skin.getDiscardCardsPlace();
        dialerCardsPlace = _skin.getDialerCardsPlace();
        openCard = _skin.getOpenCards();
        closeCard = _skin.getCloseCard();

        for (int i = 0; cards != null && i < cards.length; i++) {
        	deskCards.add(new PlCard(centerCardsPlace, openCard, cards[i], i, _owner));
        }
         
        if (ge.getDealerPosition() >= 0 && ge.getDealerPosition() < 10) {
           dealerChip = new DealerChip(ge.getDealerPosition(), _skin, _owner);
        }
        
        //	Players at the desk
        String[][] plrs = ge.getPlayerDetails();
        _playersMod = new ClientPlayerModel[_skin._roomSize];
        for (int i = 0; plrs != null && i < plrs.length; i++) {
            int pos = Integer.parseInt(plrs[i][0]);
            _playersMod[pos] = new ClientPlayerModel(plrs[i]);  
            if(_playersMod[(pos)].getStatus() == 0)_playersMod[(pos)].setStatus(PlayerStatus.NEW);
        }
	        
        refreshPlayersModel(_playersMod, null);
        if (gameType.isMTTTourny()){
            _partners = ge.getPartners();
        }
        _owner.repaint();
    }*/
   
    //resize code
    protected void refreshPlayersModelResize(ClientPlayerModel[] cpm, 
			ClientPlayerModel me){
    	//new Exception("resize").printStackTrace();
		for (int i = 0; i < cpm.length; i++) {
			_players[i]=null;
			if (cpm[i] != null) 
			{
				if (_players[i] != null)
				{
					//System.out.println("_players[i] != null");
					if (!_players[i].getPlayerName().equals(cpm[i].getPlayerName())){
						_players[i] = PlayerControllerFactory.getPlayerController(gameType, this,cpm[i], _skin, _owner, i);
					}else {
						_players[i].refresh(cpm[i], me);
					}
				}
				else 
				{
					//System.out.println("_players[i] == null");
					_players[i] = PlayerControllerFactory.getPlayerController(gameType, this,cpm[i], _skin, _owner, i);
					
//					if(i == sel_pos){
//						System.out.println("selected player $$$$$$$$$$$$$$$$$$ "+i+", "+_players[i].getPlayerName());
//						_players[i].setSelected(true);
//						//cpm[sel_pos].setSelected(true);
//					}
				}
			}
			else 
			{
				
				_players[i] = PlayerControllerFactory.getPlayerController(gameType, this, _skin, i,_owner);
			}
			
			if ( i == _reservedPosition)
			{
				_players[i].setShow(true);
			}
		}
		
		_bottomPanel.updatePlayerState(getClientPlayerState());
	} 
    public void rePositionPlayerModel(ClientPlayerModel[] cpm, ClientPlayerModel me) {
    	//new Exception("rePositionPlayerModel").printStackTrace();
    	refreshPlayersModel(cpm,me);
    	_owner.repaint();
    }
    int plrCount = 0;
    protected void refreshPlayersModel(ClientPlayerModel[] cpm, 
    		ClientPlayerModel me){
    	//new Exception("ClientPlayerModel.refreshPlayersModel()").printStackTrace();
    	plrCount = 0;
    	for (int i = 0; i < cpm.length; i++) {
			if (cpm[i] != null) {
				//_cat.finest("model name=" + cpm[i].getPlayerName() + " controller = " +_players[i]);
				// check if the controller exists
				if (_players[i] != null){
					if (!_players[i].getPlayerName().equals(cpm[i].getPlayerName())){
					_players[i] = PlayerControllerFactory.getPlayerController(gameType, this, 
								cpm[i], _skin, _owner, i);
					}
					else {
						_players[i].refresh(cpm[i], me);
						//by rk, when player amount on table <= 2 * BB then don't show quickFold button
						if(me != null && me.getAmtAtTable() <= 2 * minBet){
							//System.out.println(me._name+", "+me._amtAtTable+",minbet "+minBet);
							_bottomPanel.quickFoldVisible(false);
						}
					}
					if(gameType.isTPoker()){
						if(PlayerStatus.isQuickFolded(_players[i].getPlayerState())){
							//_cat.severe("if   "+ _players[i].getPlayerName()+" status "+PlayerStatus.stringValue(_players[i].getPlayerState()));
						}else{
							plrCount++;
							//_cat.severe("else "+ _players[i].getPlayerName()+" status "+PlayerStatus.stringValue(_players[i].getPlayerState()));
						}
					}
				} else {
					_players[i] = PlayerControllerFactory.getPlayerController(gameType, this, cpm[i], _skin, _owner, i); 
					//_cat.finest("Player="+ _players[i]);
					}
				}
				else {
					_players[i] =
					PlayerControllerFactory.getPlayerController(gameType, this, _skin, i, _owner);
					//_cat.finest("Player="+ _players[i]);
				}
				if ( i == _reservedPosition){
				_players[i].setShow(true);
				}
			}
    	//_cat.severe("player count "+plrCount);
    	if(gameType.isTPoker() && (plrCount == 2 || plrCount == 1)){
			_bottomPanel.quickFoldVisible(false);
		}
		_bottomPanel.updatePlayerState(getClientPlayerState());

    }
    
    //by rk, this method is used when close_open command came from server.
//    public void refreshPokerModelWhenCloeseOpen(GameEvent ge) {
//    	//_cat.severe("ClientPokerModel.refreshPokerModel");
//    	//new Exception().printStackTrace();
//        _cat.finest(this.toString()); // Desk cards
//	    name = ge.getGameName();
//	    _grid = ge.getGameRunId();
//	    _ge=ge;
//        state = ONLINE;
//        Card[] cards = ge.getCommunityCards();
//        deskCards.clear();
//        centerCardsPlace = _skin.getCenterCardsPlace();
//        _discards.clear();
//        tb = null;// to clear Total bet Banner
//        discardCardsPlace = _skin.getDiscardCardsPlace();
//        dialerCardsPlace = _skin.getDialerCardsPlace();
//        openCard = _skin.getOpenCards();
//        closeCard = _skin.getCloseCard();
//
//        for (int i = 0; cards != null && i < cards.length; i++) {
//        	deskCards.add(new PlCard(centerCardsPlace, openCard, cards[i], i, _owner));
//        }
//         
//        if (ge.getDealerPosition() >= 0 && ge.getDealerPosition() < 10) {
//           dealerChip = new DealerChip(ge.getDealerPosition(), _skin, _owner);
//        }
//        
//        //	Players at the desk
//        String[][] plrs = ge.getPlayerDetails();
//        _playersMod = new ClientPlayerModel[_skin._roomSize];
//        for (int i = 0; plrs != null && i < plrs.length; i++) {
//            int pos = Integer.parseInt(plrs[i][0]);
//            _playersMod[pos] = new ClientPlayerModel(plrs[i]);  
//            if(_playersMod[(pos)].getStatus() == 0)_playersMod[(pos)].setStatus(PlayerStatus.NEW);
//        }
//	        
//       //refreshPlayersModel(_playersMod, null);//commented by rk
//        if (gameType.isMTTTourny()){
//            _partners = ge.getPartners();
//        }
//        _owner.repaint();
//    }

    
    protected void refreshMyHand(Card []crd){
        for (int i=0;i<_players.length;i++){
        	if(_me == null)return;
        	if (_players[i]._position == _me.getPlayerPosition()){
                _players[i].refreshHand(crd, _me.getCards());
            }
        }
    }
    protected void refreshDeskCards(Card []c){
    	//System.out.println("refreshDeskCards");
    	deskCards.clear();
    	for (int i = 0; i < c.length; i++) {
    		//System.out.println("Card is - " + c[i].toString());
            boolean card_exists = false;
        	deskCards.add(new PlCard(centerCardsPlace/*, openCard*/, c[i], i, _owner,_skin));
        }
    }

    public void hideDealerPos() {
    	//System.out.println("hide dealer pos ");
    	dealerChip = null;//new DealerChip(ge.getDealerPosition(), _skin, _owner);
    }
    
    public void refreshDealerPos() {
    	if(_ge.getDealerPosition() > -1){ 
    		dealerChip = null;//by rk, for GC
    	dealerChip = new DealerChip(_ge.getDealerPosition(), _skin, _owner);
    	}
    	//System.out.println("refresh dealer pos on "+_ge.getGameName()+" - "+dealerChip+" - "+_ge.getDealerPosition());
    }
    
    public final String getName() {
        return name;
    }

    public final PokerGameType getGameType() {
        return gameType;
    }

    public double getMinBuyIn() {
    	//		Min. buy-in   Max. buy-in           By default offered buy-in 
    	//PL:    20BB           100BB                       60BB 
    	//FL:    20BB       Whole user's amount             60BB 
    	//NL:    20BB           100BB                       60BB 
    	return minBet * 20;
    }

    public double getDefaultBuyIn() {
    	//		Min. buy-in   Max. buy-in           By default offered buy-in 
    	//PL:    20BB           100BB                       60BB 
    	//FL:    20BB       Whole user's amount             60BB 
    	//NL:    20BB           100BB                       60BB 
    	return minBet * 60;
    }
    
    public double getMaxBuyIn() {
    	//		Min. buy-in   Max. buy-in           By default offered buy-in 
    	//PL:    20BB           100BB                       60BB 
    	//FL:    20BB       Whole user's amount             60BB 
    	//NL:    20BB           100BB                       60BB 
    	return maxBet > 0 ?minBet * 100000:minBet * 100;
    }

    public double getCurrentBet() {
        return bet;
    }

    public void setCurrentBet(double bet) {
        this.bet = bet;
    }

    public Pot[] getPot() {
        return pot;
    }

    public void setPot(Pot[] pot) {
        this.pot = pot;
        if (pot != null)
        for (int i=0;i<pot.length;i++){
        	pot[i].setView(_skin, _owner);
        }
        refreshPot();
    }
    
    public void setTotalBet(TotalBet tb1) {
    	this.tb = tb1;
        tb.setView(_skin, _owner);
    }
    
    public void clearRakeAmount() {
    	tb.clearRake();
    }
    
    public void setMessageBanner(String message) {
        mb = new MessageBanner(message);
        mb.setView(_skin, _owner);
        mb.startTimer();
    }
    //by rk
    public void setMessageBannerOnTable(String message) {
        mb = new MessageBanner(message);
        mb.setView(_skin, _owner);
        //mb.startTimer();
    }
    //by rk
    public void removeMessageBanner() {
    	if(mb != null){
    		//_owner.remove(mb.ajaxLoaderBar);
    		mb.removeBanner();
    		mb = null;
    	}
        
    }

    public double getRake() {
        return (double)rake;
    }

    public void setRake(double rake) {
        this.rake = rake;
    }

    public ClientPlayerModel[] getPlayers() {
        return _playersMod;
    }

    public void setPlayers(ClientPlayerModel[] _players) {
        this._playersMod = _players;
    }

    public void setPlayerAt(ClientPlayerModel pm, int i) {
        _playersMod[i] = pm;
    }

    public ClientPlayerController[] getClientPlayers() {
        return _players;
    }

    public void setView(ClientPokerView _view) {
        this._view = _view;
    }

    public void refreshPot() {
    	if (pot != null)
    	for (int i=0;i<pot.length;i++){
    		pot[i].refresh();
    	}
    }
    
    public void clearPot(){
        setPot(null);
    }
    
    public void clearTotalBetBanner(){
    	tb = null;
    }


    /** UPDATE call all need-to-update elements. If element think, that it must be repainted -
     * it call "repaint(...)" method.
     * Then VM will be free - it call paint(g) method of controller - {_view + model}
     *
     */
    public void update() {
    	//new Exception("update()").printStackTrace();
        /** Update dealer chip */
    	
        if (dealerChip != null) {
            dealerChip.update();
        }

        /** Update _players */
        for (int i = 0; i < _players.length; i++) {
            if (_players[i] == null) {
                continue;
            }
            _players[i].update();
        }

        /** Update desc's cards */
        for (int i = 0; i < deskCards.size(); i++) {
            ((PlCard)deskCards.get(i)).update();
        }
        
        //_cat.finest("UPDATING DESK CARDS");
         Thread t = new Thread(this);
         t.run();
    }

    public void run() {
        boolean flag = false;
      //_cat.finest("Moving chips size=" + movingChips.size());
        /** Update moving pot chips */
        while (movingChips.size() > 0) {
            Rectangle r = null;
            ((Chip)movingChips.get(0)).setMovingChipsAmt(movingChipsAmt);
            for (int i = movingChips.size() - 1; i >= 0; i--) {
                Chip chip = ((Chip)movingChips.get(i));
                if (chip.isValid()) {
                    chip.update();
                } else {
                    if (r == null) {
                        r = chip.getRealCoords();
                    } else {
                        r.add(chip.getRealCoords());
                    }
                    movingChips.remove(i);
                    flag = true;
                }
            }
            if (flag) {
                _owner.repaint(r.x, r.y, r.width +100, r.height +150);
            }
            try {
                Thread.currentThread().sleep(45);//50
            } catch (Exception e) {
            }
            
        } // while moving chips
        movingChipsAmt = 0;
        /** Update moving rake chips */
        while (rakeChips.size() > 0) {
            Rectangle r = null;
            for (int i = rakeChips.size() - 1; i >= 0; i--) {
                Chip chip = ((Chip)rakeChips.get(i));
                if (chip.isValid()) {
                    chip.update();
                } else {
                    if (r == null) {
                        r = chip.getRealCoords();
                    } else {
                        r.add(chip.getRealCoords());
                    }
                    rakeChips.remove(i);
                    flag = true;
                }
            }
            if (flag) {
                _owner.repaint(r.x, r.y, r.width, r.height);
            }
            try {
                Thread.currentThread().sleep(25);
            } catch (Exception e) {
            }
        } // while moving rake chips
       
        //_cat.finest("UPDATING MOVING CARDS " + movingCards.size());
        /** Update moving cards **/
        while (movingCards.size() > 0) {
            for (int i = movingCards.size() - 1; i >= 0; i--) {
                MovingVisibleCard mvc = (MovingVisibleCard)movingCards.get(i);
                PlCard plCard = mvc.plCard;
                if (plCard.isValid()) {
                    plCard.update();
                } else {
                    Rectangle r = plCard.getBounds();
                    _players[mvc.pos].addPocketCard(new Card(mvc.index,false));
                    movingCards.remove(i);
                    _owner.repaint(r);
                    flag = true;
                }
                
            }
            try {
                Thread.currentThread().sleep(10);
            } catch (Exception e) {
            }
        } //end while
    }
    
    //resize code
    public void resize(GameEvent ge, RoomSkin skin){
		_ge=ge;
		//System.out.println("ClientPokerModel");
		_skin=skin;
		this._bottomPanel.resize(_skin);
		if(_moveTimer!=null){
			_moveTimer.resize(_skin);
		}
		if(tm != null){
			tm.resize(_skin);
		}
		if(mb != null){
			mb.resize(_skin);
		}
		refreshPokerModel(_ge,RESIZE);
		setPot(pot);
		_owner.repaint();
	}
    JComponent _c;
	Graphics _g;
	public void set(JComponent c, Graphics g){
		_c=c;
		_g=g;
	}

    public void paint(JComponent c, Graphics g) {
    	//new Exception("paint()").printStackTrace();
    	//resize code
    	set(c,g);
    	if(_moveBgX != 0)
    	{
    		//System.out.println("moving bg "+_moveBgX);
    		_owner._board.paintIcon(c, g, _moveBgX, _moveBgY);
    	}
        /** Draw dialer chip */
        if (dealerChip != null) {
            dealerChip.paint(_owner, g);
        }
       
        /** draw discarded cards */
	    for (int i = 0; i < _discards.size(); i++) {
	       g.drawString("Discarded cards ...", 300, 210);
	       ((PlCard)_discards.get(i)).paintSmall(c, g);
	    }
	    
	    /** Draw moving cards */
	    for (int i = 0; i < movingCards.size(); i++) {
	        ((MovingVisibleCard)movingCards.get(i)).plCard.paint(_owner, g);
	    }
         
         /** Draw _players */
        for (int i = 0; i < _players.length; i++) {
        	if (_players[i] != null) {
                _players[i].paint(_owner, g);
            }
            
        }

        if (_moveTimer != null){
        	_moveTimer.paint(c, g);
        }
        
        
        
        /** Draw community cards */
        boolean isPaintedHigh = false;
        int x = 0;
        for (int i = 0; i < deskCards.size(); i++) {
            PlCard crd = (PlCard)deskCards.get(i);
            if (winnerCards != null) {
                for (int j = 0; j < winnerCards.length; j++) {
                    if (winnerCards[j].getIndex() == 
                        crd.getCard().getIndex()) {
                        //_cat.finest("winnerCards[j]: " + winnerCards[j]);
                        crd.paint(_owner, g, x);
                        isPaintedHigh = true;
                    }
                }
            }
            if (!isPaintedHigh) {
                crd.paint(_owner, g);
            }
            isPaintedHigh = false;
        }
        
        if (pot != null)
        for (int i=0;i<pot.length;i++){
        	pot[i].paint(_owner, g);
        }
        
        /** Draw Total Bet Banner */
        if(tb != null)
        {
        	tb.paint(_owner, g);
        }	
        	
        	
        
        /** Draw Message Banner */
        if(mb!= null && mb.visible)
        {
        	mb.paint(_owner, g);
        	
        }
        //by rk
        /** Draw Timer Message Banner */
        if(tm != null && tm.visible)
        {
        	tm.paint(_owner, g);
        	
        }
        
        //****************************************************
        for (int i = 0; i < movingChips.size(); i++) {
            Chip wc = ((Chip)movingChips.get(i));
            wc.paint(_owner, g);  
            if (i == movingChips.size() - 1) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial Narrow", Font.BOLD, 14));
               
            }
        } 
        
        for (int i = 0; i < rakeChips.size(); i++) {
            Chip wc = ((Chip)rakeChips.get(i));
            wc.paint(_owner, g);  
            if (i == rakeChips.size() - 1) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial Narrow", Font.BOLD, 11));
               
            }
        } 
       
        // _players open cards
        for (int j = 0; j < openCards.size(); j++) {
            PlCard crd1 = (PlCard)openCards.get(j);
            crd1.paint(_owner, g);
            _cat.finest("open hands paint working" + crd1);
        }
       
        
    }

    public void mouseMoved(int mouseX, int mouseY) {
        boolean procceded = false;
        int currPlayer = _owner.getMyPlayerNo();
        for (int i = 0; i < _players.length; i++) {
            if (_players[i] != null && i != currPlayer) {
            	procceded = 
                        _players[i].mouseOver(mouseX, mouseY, getClientPlayerState(), 
                                             procceded, 
                                             (!isWaitingForResponse()));
            }
            if (i==_reservedPosition){
            	//System.out.println("_reservedPosition "+_reservedPosition);
            	_players[i].setShow(true);
            }
        }
        _owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   
        Rectangle handHistory = new Rectangle(0, 0, 100, 20);
        if (handHistory.contains(mouseX, mouseY)){
            _owner.setCursor(new Cursor(Cursor.HAND_CURSOR));   
        }
    }

    public int getSittingPlayerCount() {
        int count = 0;
        for (int i = 0; i < _players.length; i++) {
            if (_players[i]._playerModel != null && _players[i]._playerModel.isSitting()) {
                count++;
            }
        }
        return count;
    }

    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        _cat.finest("MOUSE clicked ...");
        
         //## for player comments ##
         
        int playerNo = _owner.getMyPlayerNo();
        for (int i = 0; i < _players.length; i++) {
            if (_players[i] == null || proceeded == true) {
               continue;
             }
            _players[i].mouseClick(mouseX, mouseY);
            if ((_players[i].getBounds().contains(mouseX, mouseY) 
            		|| _players[i].getPlayerNotePos().contains(mouseX, mouseY)) &&  i != playerNo) {
                // --- proced mouse click on player i
                _cat.finest("Mouse clicked on " + i);
                if (!e.isPopupTrigger()) {
                    _cat.finest("getClientPlayerState() " +  getClientPlayerState());
                    if (getClientPlayerState()  == PlayerStatus.NONE) {
                        if (_players[i].isNullPlayer() && !isWaitingForResponse()) {
                            //chekc if player is invited
                        	//System.out.println(_owner._serverProxy._name+" invited "+ gameType.isMTTTourny()+ isPartner(_owner._serverProxy._name));
                        	if (!gameType.isTourny() || gameType.isSitnGo() || (gameType.isMTTTourny() && isPartner(_owner._serverProxy._name)) ){
                                _reservedPosition = i;
                                _players[i].setShow(true);
                                setWaitingForResponse(_bottomPanel.inviteToSeat(i, 0,
                                                                               gameType.isReal()?_owner._serverProxy.realWorth():_owner._serverProxy.playWorth(), 
                                                                               getMinBuyIn(), 
                                                                               getMaxBuyIn(), 
                                                                               fees, 
                                                                               gameType.isTourny(), 
                                                                               maxBet > 
                                                                               1 ? 
                                                                               true : 
                                                                               false));
                                _owner._clientRoom.getClientRoomFrame().toFront();
                            }
                        }
                    } else if (PlayerStatus.isActive(getClientPlayerState())) {
                    	//player note
                        _players[i].invertSpeak();
                        if(!_players[i].isNullPlayer()){
                        	if(player_note == null){
                            player_note = MessageFactory.getPlayerNoteWindow("Player Note",_players[i].getPlayerName());
                        	}else{
                        		if(player_note.getPlayerName().equals(_players[i].getPlayerName())){
                        			player_note.dispose();
                        			player_note = MessageFactory.getPlayerNoteWindow("Player Note",_players[i].getPlayerName());
                        		}else{
                        			player_note.dispose();
                        			player_note = MessageFactory.getPlayerNoteWindow("Player Note",_players[i].getPlayerName());
                        		
                        		}
                        	}
                        		
                        }
                        _cat.fine("Invite to chat player #" + i);
                    }
                    break;
                } else {
                    _cat.finest("Popup ");
                    proceeded = true;
                    procedPopUp(i, (Component)_owner._clientRoom, _players[i], e);
                    break;
                }
            }
        }
        Rectangle r;
        //## lobby menu buttons for SitnGo ##
        if(gameType.isSitnGo() || gameType.isMTTTourny())
        {
        	r = new Rectangle(0, 0, 100, 20);
	        if (r.contains(mouseX, mouseY)){
	        	//_bottomPanel.frameToFront();
	        }
	        
        	r = new Rectangle(200, 0, 100, 20);
	        if (r.contains(mouseX, mouseY)){
	        	_bottomPanel.frameToFront();
	        }
	        
	        r = new Rectangle(300, 0, 100, 20);
	        if (r.contains(mouseX, mouseY)){
	            // check if i am sitting
	           if (_me == null) {
	        	   JFrame f = (JFrame)_owner._clientRoom.getClientRoomFrame();
	   				JOptionPane.showInternalMessageDialog(f.getContentPane(),
	                    "Wait, Player is not in active state", "ERROR",
	                                  JOptionPane.ERROR_MESSAGE);
	                _owner._clientRoom.getClientRoomFrame().toFront();
	            }
	            else
	            {
	            	_bottomPanel.openStats(_statistics);
	            	_owner._clientRoom.getClientRoomFrame().toFront();//_bottomPanel.crToFront();
	            }
	        }
        }
        else
        {
        	//### Get Chips ######
	        r = new Rectangle(0, 0, (int)(65*_skin._ratio_x), (int)(20*_skin._ratio_y));
	        if (r.contains(mouseX, mouseY)){
	        	if (_me == null) {
	        		JFrame f = (JFrame)_owner._clientRoom.getClientRoomFrame();
	   				JOptionPane.showInternalMessageDialog(f.getContentPane(),
	        	        "Wait, Player is not in active state", "ERROR",
	                                  JOptionPane.ERROR_MESSAGE);
	        		
	        	}
	        	else
	        	{
	        		System.out.println();
	        		//System.out.println("AMT at table: "+_owner._model._players[_owner.getMyPlayerNo()].getAmtAtTable());
	        		_bottomPanel.buyInRequest(0.0, _owner._model._players[_owner.getMyPlayerNo()].getAmtAtTable(),
	        				(gameType.isReal())?_owner._serverProxy.realWorth():_owner._serverProxy.playWorth(), //_owner.getMyWorth(), //by rk, here getMyWorth returning play amount only
						  _owner._model.getMinBuyIn(), 
						  _owner._model.getMaxBuyIn(), 
						  _owner._model.fees, 
						  _owner._model.gameType.isTourny(), 
						  _owner._model.maxBet > 1 ? true : false, 
						  null,_owner._model.getName());
	        		//_owner._clientRoom.getClientRoomFrame().toFront();
	        		//_bottomPanel.crToFront();
	        	}
	        }
	        
	        r = new Rectangle(_skin.getOptions().x/*200*/, 0, (int)(30*_skin._ratio_x), (int)(20*_skin._ratio_y));
	        if (r.contains(mouseX, mouseY)){
	        	_owner.tryExit();
	        }
	        
	        r = new Rectangle(_skin.getLobby().x/*300*/, 0, (int)(30*_skin._ratio_x), (int)(20*_skin._ratio_y));
	        if (r.contains(mouseX, mouseY)){
	        	_bottomPanel.frameToFront();
	        }
	        //## for Stats ##
	        r = new Rectangle(_skin.getStats().x/*400*/, 0, (int)(24*_skin._ratio_x), (int)(20*_skin._ratio_y));
	        if (r.contains(mouseX, mouseY)){
	            // check if i am sitting
	           if (_me == null) {
	        	   	JFrame f = (JFrame)_owner._clientRoom.getClientRoomFrame();
	   				JOptionPane.showInternalMessageDialog(f.getContentPane(), "Wait, Player is not in active state", "ERROR",
	                                  JOptionPane.ERROR_MESSAGE);
	   			
	   			}
	            else
	            {
	            	_cat.fine("stats clicked");
	            	_bottomPanel.openStats(_statistics);
	            	_owner._clientRoom.getClientRoomFrame().toFront();
	            }
	        }
        }
        //## for Last Hand menu ##
        r = new Rectangle(_skin.getLastHand().x/*100*/, 0, (int)(40*_skin._ratio_x), (int)(20*_skin._ratio_y));
        if (r.contains(mouseX, mouseY))
        {
//        	System.out.println("prev gid=" + _prev_name +" gid=" + name + "& prev grid=" + _prev_grid + "grid=" + _grid +"&pid=" +  ServerProxy._name);
        	JFrame f = (JFrame)_owner._clientRoom.getClientRoomFrame();
			if(_prev_grid > 0)
        	{
        	
	            try {
	            	//Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler " + "http://saintbet.supergameasia.com/s/handhistory?gid=" + name + "&grid=" + _prev_grid + "&pid=" +  ServerProxy._name);
	            	if(hh_dialog == null || !hh_dialog.reply.equals("opened"))
            		{
	            		if(gameType.isTPoker() && _prev_name == null)
            			{
	            			JOptionPane.showInternalMessageDialog(f.getContentPane(),
	        	                    "There is no last hand, play atleast 1 game", "WARNING",
	        	                                  JOptionPane.WARNING_MESSAGE);
	            			return;
            			}
	            		//System.out.println(_prev_grid+" - "+_grid);
	            		StringBuilder sb = new StringBuilder();
	            		hh_dialog = MessageFactory.getHandHistoryWindow(_owner._clientRoom.getClientRoomFrame(),
	            				sb.append((gameType.isTPoker()?_prev_name:name)).append("-").append(_prev_grid).append(" Instant Hand History").toString() ,
	            				(gameType.isTPoker()?_prev_name:name),
	            				_prev_grid);
//	            		hh_dialog = MessageFactory.getHandHistoryWindow(_owner._clientRoom.getClientRoomFrame(),
//			            				(gameType.isTPoker()?_prev_name:name)+"-"+_prev_grid+" Instant Hand History" ,
//			            				(gameType.isTPoker()?_prev_name:name),
//			            				_prev_grid);
	            		_owner._clientRoom.getClientRoomFrame().getLayeredPane().add(hh_dialog, JLayeredPane.POPUP_LAYER);
            		}
	        		
	            }
	            catch (Exception ioe){
	                ioe.printStackTrace();
	            }
               //new MessagePopup("http://67.211.101.97:8080/bluespade/s/handhistory?gid=" + name + "&grid=" + _prev_grid + "&pid=" +  ServerProxy._name);
	        	
        	}
        	else
        	{
        		JOptionPane.showInternalMessageDialog(f.getContentPane(),
	                    "There is no last hand, play atleast 1 game", "WARNING",
	                                  JOptionPane.WARNING_MESSAGE);
        	}
        }
        //## for Lobby menu ##
        r = new Rectangle(_skin.getLobby().x/*625*/, 0, (int)(30*_skin._ratio_x), (int)(20*_skin._ratio_y));
        if (r.contains(mouseX, mouseY)){
        	_bottomPanel.frameToFront();
        }
        //## for EXIT menu ##
        r = new Rectangle(_skin.getExit().x/*625*/, 0, (int)(20*_skin._ratio_x), (int)(20*_skin._ratio_y));
        if (r.contains(mouseX, mouseY) && LobbyUserImp.fromBrowser){
        	_owner.tryExit();
        }
        //## for Full Screen ##
        r = new Rectangle(700, 0, 60, 20);
        if (r.contains(mouseX, mouseY) && fullscreen){
        		 _owner._clientRoom.setFullScreen(false);
        	 fullscreen = !fullscreen;
        }
        //## for closing window ##
        r = new Rectangle(760, 0, 50, 20);
        if (r.contains(mouseX, mouseY) && fullscreen){
        	_owner.tryExit();
        }
        
    }
    
    protected void procedPopUp(int i, Component room, 
                               ClientPlayerController controller, 
                               MouseEvent e) {
    }

    protected void proceedPopUpDealer(Component room, MouseEvent e) {
    }


    /**
     * Test, is client's player has <code>no</code> number
     */
    public boolean isMyPlayerNo(int no) {
        return (no == _owner.getMyPlayerNo());
    }

    public double getPlayerMoneyAtTable() {
        //  	int n = getPlayerNoByName(_owner.getClientPlayerName());
        int n = _owner.getMyPlayerNo();
        if (n < 0) {
            return -1;
        }
        return _players[n].getAmtAtTable();
    }

    public void doGameStart() {
        winnerCards = null;
        if (gameType.isTourny()) {
            isTournamentRunning = true;
        }
        //_bottomPanel.sitInOut.setVisible(false);
        _bottomPanel.checkTournamentButtons(gameType.isTourny(), 
                                           isTournamentRunning);
    }

    /**
     * move all name plates player's chips to desk's pot
     * for all _players
     */
    public void moveBackground() {
        
        //_owner._board.
    	MoveBGTask rtl = new MoveBGTask();
        _moveBgTimer = new Timer();
    	_moveBgTimer.schedule(rtl, 0, 1);
    }

    /**
     * move all player's chips to desk's pot
     * for all _players
     */
    public void moveAllBetsToCenter() {
        int oldVectorSize = movingChips.size();
        for (int i = 0; i < _players.length; i++) {
        	if(_players[i] != null){
	            Chip[] chips = _players[i].getRoundBetChips();
	            //_cat.finest("Chips=" + chips.length);
	            for (int j = 0; j < chips.length; j++) {
	                movingChips.add(chips[j]);
	            }
	            _players[i].setRoundBetChips(0);
        	}
        }
        int potlen = pot == null ? 0 : pot.length;
        for (int i = oldVectorSize; i < movingChips.size(); i++) {
            ((Chip)movingChips.get(i)).startMove(_skin.getHeapPlace().x, 
                                                 _skin.getHeapPlace().y - _skin.getPotLength(potlen));//potlen
        }
        //_cat.finest("Moving chips to the pot ..");
        
    }
    
    /**
     * move rake chips to dealer
     */
    public void moveRakeToDealer() {
        int oldVectorSize = rakeChips.size();
//        System.out.println("rake: "+_rake+" getRake() "+getRake());
        if(_rake == getRake())return;
        _rake = getRake();
        Chip[] chips = Chip.MoneyToChips(getRake(), _skin.getHeapPlace().x - 100, _skin.getHeapPlace().y,_skin.getChips(),
                _owner, _skin);
        for (int j = 0; j < chips.length; j++) {
        	rakeChips.add(chips[j]);
        }
            
        for (int i = oldVectorSize; i < rakeChips.size(); i++) {
            ((Chip)rakeChips.get(i)).startMove(_skin.getHeapPlace().x - 100, _skin.getHeapPlace().y - 100);
        	
        }
        
        
        //_cat.finest("Moving chips to the pot ..");
        
    }


    /**
     * move all player's chips to desk's pot
     * only for one player
     */
    public void moveAllBetsToCenterForPlayer(int playerNo) {
        int oldVectorSize = movingChips.size();
        Chip[] chips = _players[playerNo].getRoundBetChips();
        for (int j = 0; j < chips.length; j++) {
            movingChips.add(chips[j]);
            //movingChipsAmt += ((Chip)chips[j]).getMoneyValue();
        }
        _players[playerNo].setRoundBetChips(0);
        int potlen = pot == null ? 0 : pot.length;
        for (int i = oldVectorSize; i < movingChips.size(); i++) {
            ((Chip)movingChips.get(i)).startMove(_skin.getHeapPlace().x, 
                                                 _skin.getHeapPlace().y- _skin.getPotLength(potlen)*50);//potlen
        }
        
    }

    public void resetAllBets() {
        for (int i = 0; i < _players.length; i++) {
            _players[i].setRoundBetChips(0);
        }
    }


    /* ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| */
    /* |||||||||||||||||||||||||||||||| Action reaction |||||||||||||||||||||||||||| */
    /* ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| */

    /**
     * move some of pot chips to some player. BettingAction.Bet - how many bax move;
     * BettingAction.Target - who destination of chips
     */
     ClientPlayerController winPlayer;
     double winAmt=0;
    public synchronized  void doBettingWin(Action action) {
        WinAction wa = (WinAction)action;
        resetAllBets(); /// player bets
        winnerCards = 
                wa.getCombination(); // to make winning combination available in paint()
        int winnerPosition = wa.getTarget();
        winAmt = wa.getPot();
        // animate this players card
        for (int i=0;i<_players.length;i++){
            if (_players[i]._position == winnerPosition){
                _players[i].doBettingWin(winnerCards);
            }
        }

        if (action.getTarget() < 0) {
            refreshPot();
            return;
        }

        winPlayer = _players[action.getTarget()];
        refreshPot();

        Chip[] chipsToMove = 
            Chip.MoneyToOneColumnChips(winAmt, _skin.getHeapPlace().x, 
                                       _skin.getHeapPlace().y, _skin.getChips(), 
                                       _owner);
        _cat.finest("Pot=" + winAmt + " Winner=" + winPlayer.getPlayerName() + 
                    chipsToMove.length);

        ///reset pot chips
        clearPot();
        _rake = 0;
        clearTotalBetBanner();
        int i = 0;
        for (; i < chipsToMove.length; i++) {
            chipsToMove[i].startMove(winPlayer.getChipsPlace().x, winPlayer.getChipsPlace().y);
            movingChips.add(chipsToMove[i]);
            movingChipsAmt += ((Chip)chipsToMove[i]).getMoneyValue();
        }
        /* this clap sound, commented as per client request 
         * 
         * if (_me  == winPlayer._playerModel && ServerProxy._settings.isSound()){
            SoundManager.playEffect(SoundManager.WIN);
        }
        else if(ServerProxy._settings.isSound()){
            SoundManager.playEffect(SoundManager.WIN2);
        }*/
    }
    
    public void showWinning(){
        winPlayer.showWinning(winAmt);
    }


    public synchronized void doShowDown(CardAction action) {
        int pos = action.getTarget();
        Card[] showdown_cards = action.getCards();
        //_cat.severe("Winner Cards " + pos);
        for (int i = 0; i < _players.length; i++) {
            if (_players[i] != null && _players[i].getPlayerPosition() == pos) {
                _players[i].setPocketCards(showdown_cards);
            }
        }

        //_openHands = wa.getOpenHands();
        if (_openHands != null) {
            _openHands1 = step1(_openHands);

            if (_openHands1.length != 0) {
                for (int k = 0; k < _openHands1.length; k++) {
                    _cat.finest("_openHands: " + _openHands1[k]);
                    if (!_openHands1[k].equals(null)) {
                        _cat.finest("_openHands* " + _openHands1[k]);
                        step2(_openHands1[k]);
                    }
                }
            }
        }
        _owner.repaint();
    }
    public synchronized void doDiscards(CardAction action) {
        Card[] dcards = action.getCards();
        _discards.clear();
        for (int i=0;i<dcards.length;i++){
            _discards.add(new PlCard(discardCardsPlace/*, openCard*/, closeCard, 
                                     dcards[i], i, _owner,_skin));
        }
    }
    
    
    public String[] step1(String s) {
        String[] str = s.split("`");
        return str;
    }

    public void step2(String s) {
        String[] s1 = s.split("\\|");
        if (s1.length != 0) {
            _cat.finest("Excep|" + s1[0] + "|");
            int a = Integer.parseInt(s1[0]);
            step3(s1[1], a);
        }
    }

    public void step3(String s, int a) {
        String[] s2 = s.split("'");
        int b = a;
        Card c[] = new Card[2];
        _cat.finest("pos---------> " + b);
        for (int i = 0; i < s2.length; i++) {
            c[i] = new Card(s2[i]);
            openCards.add(new PlCard((_skin.getOpenCardPos(b))/*, openCard*/, 
                                     closeCard, c[i], i, _owner,_skin));

            _cat.finest("open cards----------> " + c[i]);
        }
    }

    public void doDecisionTimeout() {
        if (lastSendedBetAction == null) {
            return;
        }
        double money = lastSendedBetAction.getBet();
        if (money == 0) {
            return;
        }
        int playerNo = _owner.getMyPlayerNo();
        if (playerNo < 0 || _players[playerNo].isNullPlayer()) {
            return;
        }
    }
    
    public void startNextMoveTimer(int pos){
    	stopNextMoveTimer(); //by rk, stop move timer before starting new one if any
    	//stopGraceCounter(); //by rk, stop grace counter timer before start MoveTimer if any
    	_moveTimer = new MoveTimer(_skin, _owner, pos, ClientPlayerModel.MALE, this);
    	//_cat.severe("startNextMoveTimer() "+pos);
    	 _mt = new Timer();
    	 _mt.schedule(_moveTimer, 0, 625); //for 625ms one frame, total frames are 17 //1000
    	 startGraceCounter(pos);
    }
    
    public void stopNextMoveTimer(){
    	if (_moveTimer != null){
    		//_cat.severe("stopNextMoveTimer()");
    		_moveTimer.cancel();
       	 	_moveTimer=null;
       	 	//by rk
       	 	if(_mt != null){
       	 		_mt.cancel();
       	 		_mt = null;
       	 	}
    	}
    	stopGraceCounter();//by rk, to stop grace counter timer if any
    }
    
    //by rk, for implementing disconnection grace time
    TimerMessage tm = null;
    public void startGraceCounter(int pos){
    	//_cat.severe("startGraceCounter() "+ pos);
    	tm = new TimerMessage(pos);
    	tm.setView(_skin, _owner, this);
    	tm.startTimer();
    }
    
    public void stopGraceCounter(){
    	if(tm !=null){
    		//_cat.severe("stopGraceCounter()");
    		tm.stopTimer();
    		tm = null;
    	}
    }
    
    public void stopGraceCounterByPos(int pos){
    	if(tm !=null){
    		_cat.fine("stopGraceCounterByPos() "+ pos);
    		tm.stopTimer();
    		tm = null;
    	}
    }
    
    private BettingAction lastSendedBetAction = null;

    public void setLastSendBetAction(BettingAction ba) {
    	lastSendedBetAction = null; //by rk, for GC
        lastSendedBetAction = ba;
    }

    /**
     * add chips to table or to player
     */
    static int playerPos;

    public void doBetting(Action action) {
        if (action instanceof BetRequestAction) {
            BetRequestAction brq = (BetRequestAction)action;
            _cat.fine("BET REQUEST ACTION = " + brq + " Id=" + brq.getId());
            switch (brq.getId()) {
                   case ActionConstants.BET_REQUEST:
                	   //if(! isquickFolded) //in term poker if player Quick folds don't render bet request buttons to that player
                        _bottomPanel.getBetRequest(brq);
                        break;
                    case ActionConstants.BIG_BLIND_REQUEST:
                    case ActionConstants.SMALL_BLIND_REQUEST:
                    case ActionConstants.BOTH_BLIND_REQUEST:
                    case ActionConstants.MISSED_BIG_BLIND_REQUEST:
                    case ActionConstants.MISSED_SML_BLIND_REQUEST:
                    case ActionConstants.SHOWDOWN_REQUEST:
                        _cat.fine("Blind request " + brq);
                break;
            }
        }

        else if (action instanceof LastMoveAction) {
            LastMoveAction ba = (LastMoveAction)action;
           // _cat.severe("LAST MOVE ACTION = " + ba);
            int no = ba.getTarget();
            playerPos = no;
            //      _cat.finest("ba.getTarget():no " + no);
            //      _cat.finest("ba.getId():id " + ba.getId());
            setSayMessage(no, ba.getId(), ba.getCurrentBet());
        }
    }

    protected void setSayMessage(int playerNo, int actionID, double amt) {
        switch (actionID) {

        case ActionConstants.BET:
            _players[playerNo].say("Bet €" + amt);
            break;

        case ActionConstants.CALL:
            _players[playerNo].say("Call €" + amt);
            break;

        case ActionConstants.CHECK:
            _players[playerNo].say("Check");
            break;

        case ActionConstants.ALLIN:
            _players[playerNo].say("AllIn €" + amt);
            break;

        case ActionConstants.RAISE:
            _players[playerNo].say("Raise €" + amt);
            break;

        case ActionConstants.FOLD:
            _players[playerNo].say("Fold");
            break;

        case ActionConstants.ANTE:
            _players[playerNo].say("Ante €" + amt);
            break;
            
        case ActionConstants.SMALL_BLIND:
            _players[playerNo].say("S Blnd");
            break;

        case ActionConstants.BIG_BLIND:
            _players[playerNo].say("B Blnd");
            break;

        case ActionConstants.SB_BB:
            _players[playerNo].say("SB-BB");
            break;
            
        case ActionConstants.MORNING:
            _players[playerNo].say("Morning");
            break;

        case ActionConstants.AFTERNOON:
            _players[playerNo].say("Afternoon");
            break;

        case ActionConstants.EVENING:
            _players[playerNo].say("Evening");
            break;
        
        default:
            _cat.severe("Unknown move");
        }
       
    }

    public void doUpdateStates(int[] states) {
        try {
            _cat.finest("doUpdateStates(int[] states)");
            _cat.finest("_players.length = " + _players.length);
            for (int i = 0; i < states.length; i++) {
                System.out.println(_players[i].getPlayerName()+" [" + i + "] = " + states[i]);
                if (_players[i].isNullPlayer() && states[i] != -1) {
                    _cat.info("!!! Have action with ID != -1, but player model is NullPlayer");
                    continue;
                }
                if (!_players[i].isNullPlayer() && states[i] == -1) {
                    _cat.info("!!! Have action with ID == -1, but player model is NOT NullPlayer");
                    continue;
                }
                if (_players[i].getPlayerState() != states[i]) {
                    _players[i].setPlayerState(states[i]);
                    _players[i].refresh();
                }
            }
        } catch (Exception e) {
            _cat.log(Level.WARNING, "EEEEEXXXXXXXXX = " + e);
        }
    }


    /**
     * Clear the desk (delete all cards, all chips and other);
     */
    public void doClearDesk() {
        setPot(null);
        deskCards.clear();
        _discards.clear();
        playerCards.clear();
        movingCards.clear();
        for (int i = 0; i < _players.length; i++) {
        	if(_players[i] != null){
	            _players[i].clear();
	            _players[i]._pokerModel.stopNextMoveTimer();  // to stop the timer when he leaves
        	}
        }
        _owner.repaint();
    }


    /**
     *  add card to action.target.
     */
    public void doCardAction(Action action) {
        assert action instanceof CardAction : "Bad card action " + action;
        
        CardAction cardAction = (CardAction)action;
        Card[] c = cardAction.getCards();
        if (c == null) {
            c = new Card[] { null };
        }
        _cc = Hand.getHandFromCardArray(c);
        if (cardAction.getTarget() == ActionConstants.BOARD_TARGET) {
            for (int i = 0; i < c.length; i++) {
                //_cat.finest("Card is - " + c[i].toString());
                boolean card_exists = false;
                for (int j = 0; j < deskCards.size(); j++) {
                    if (((PlCard)deskCards.get(j)).getIndex() == c[i].getIndex()) {
                        card_exists = true;
                        //_cat.finest("Card exists " + c[i].toString());
                        break;
                    }
                }
                if (!card_exists) {
                    //_cat.finest("Adding card " + c[i]);
                    deskCards.add(new PlCard(centerCardsPlace/*, openCard*/,  c[i], deskCards.size(), 
                                             _owner,_skin));
                    if (ServerProxy._settings.isSound()) {
                        _owner.tryPlayEffect(SoundManager.FLOP_DEALING);
                    }
                }
            }
        } else {
            //_cat.finest("Card player postion = " + cardAction.getTarget());
            int playerNo = cardAction.getTarget();
            ClientPlayerController player = _players[playerNo];
            if (action.getId() == ActionConstants.NO_SHOWDOWN) {
                player.clearPocketCards();
                return;
            }
            //_cat.finest("These are hand cards for this player ");
            if (c != null) {
                _cat.fine("CARDS-> " + c.length);
                player.setPocketCards(c);
                playerCards.put(SharedConstants.INT_ARRAY[playerNo], c);
            } else {
                Card[] cards = new Card[] { new Card(false), new Card(false) };
                playerCards.put(SharedConstants.INT_ARRAY[playerNo], cards);
            }
        }
    }


    /**
     * Have DEALING action. -> all _players starting getting one card per time
     * fill vector (dealingCardVector) with sorted cards
     */
    public void pushBackCards(int dealt_card_size) {
        dealingCardVector.clear();
        for (int j = 0; j < dealt_card_size; j++) {
            for (int i = 0; i < _players.length; i++) {
                if (_players[i] != null && _players[i].isNullPlayer() || 
                    PlayerStatus.isActive(_players[i]._playerModel.getStatus()) || 
                    (PlayerStatus.isNew(_players[i]._playerModel.getStatus()) && 
                     !PlayerStatus.isBetweenBlinds(_players[i]._playerModel.getStatus()) &&
                     !PlayerStatus.isBroke(_players[i]._playerModel.getStatus())&&
                     !PlayerStatus.isOptOut(_players[i]._playerModel.getStatus())&&
                     !PlayerStatus.isSittingOut(_players[i]._playerModel.getStatus())
                    )
                     
                
                ) {
                    if (!_players[i].isNullPlayer() && _players[i]._playerModel.isActive()) {
                    	//System.out.println(_players[i]._playerModel._name+"---"+_players[i]._playerModel.isActive());
                        Card[] cards = 
                            (Card[])(playerCards.get(SharedConstants.INT_ARRAY[i]));
                        dealingCardVector.add(new MovingCard(cards == null ? 
                                                             null : cards[j], 
                                                             i));
                    }
                }
            }
        }
        playerCards.clear();
        int size = dealingCardVector.size();
        AddCardTimerTask timerTask;
        for (int i = 0; i < size; i++) {
            timerTask = 
                    new AddCardTimerTask((MovingCard)dealingCardVector.get(i), 
                                         i >= size / 2);
            if (i != 0) {
                utilTimer.schedule(timerTask, 20 * (i));
            } else {
                timerTask.doRun();
            }
        }
        dealingCardVector.clear();
    }


    /**
     * add one card to queue of moving card
     * @param card
     */
    private synchronized void addMovingCard(MovingCard card, boolean levelUp) {
        Card c = card.card;
        //_cat.finest("Card is " + c.getRankChar(c.getRank()) + c.getSuitChar(c.getSuit()));
        int index = c.getIndex();
        PlCard plCard = 
            new PlCard(dialerCardsPlace/*, openCard*/, closeCard, new Card(false), 0, 
                       _owner,_skin);
        Point moveTo = 
            _skin.getPlayerCardsPlaceClose(card.pos, ClientPlayerModel.MALE);
        if (levelUp) {
            plCard.startMove(moveTo.x - 5, moveTo.y - 5);
        } else {
            plCard.startMove(moveTo.x, moveTo.y);
        }
        movingCards.add(new MovingVisibleCard(plCard, card.pos, index));
    }

    public void startNewHand() {
        //if (getClientPlayerState() == PlayerModel.PLAY) {
        if (PlayerStatus.isActive(getClientPlayerState())) {
            _bottomPanel.cardShowButton();
        }
    }


    /**
     * some player (or client player) is fold.
     * @param action
     */
    public void doFold(Action action) {
        int no = action.getTarget(); // TARGET int no = action.getTarget() - 1;
       //new Exception(action.getClass().getName()+" -- "+action.getType()).printStackTrace();
        if (no < 0 || no >= maxPlayer) {
            _cat.fine("Error (ClientPokerModel) target not in 1.. !!! Target = " + (no));
            return;
        }
        setSayMessage(no, ActionConstants.FOLD, 0);
        _players[no].clearPocketCards();
        playerCards.remove(SharedConstants.INT_ARRAY[no]);
        if (isMyPlayerNo(no)) {
            //**	if (_owner.getClientPlayerName().equals(_players[no].getPlayerName())) {
            //*//	  bottonPanel.setPlayerPlay(false);
        	//System.out.println("##############################when folded Amount at Table "+_me.getAmtAtTable()+" , initial amt "+_owner.startAmt);
        	foldedRound = round;
        	//System.out.println("foldedRound = "+foldedRound);
        	
        	this.isFolded = true;
        	//_cat.severe("QF CPM isFolded "+isFolded);
        	//setStats(_owner.startAmt, _me.getAmtAtTable()); // true means player "folded"
        	//setRound(round);
            _cat.fine("ClientPokerModel.doFold()");
            checkLeftButtonsState();
        }
    }
    
    public void setStats(double startAmt, double endAmt){
    	double win = endAmt - startAmt;
    	//System.out.println("startAmt= "+startAmt+" ,endAmt= "+endAmt+", round= "+round);
    	if(round == 3){
    			_statistics.won_amt_showdown += win;
    			if(win > 0.0)_statistics.won_showdown++;
    	}else if(round == 2){
    		if(!isFolded){
    			_statistics.won_amt_river += win;
    			if(win > 0.0)_statistics.won_river++;
    		}else{
    			_statistics.won_amt_river += win;
    		}
    	}else if(round == 1){
    		if(!isFolded){
    			_statistics.won_amt_turn += win;
	        	if(win > 0.0)_statistics.won_turn++;
    		}else{
    			_statistics.won_amt_turn += win;
    		}
        }else if(round == 0){
        	if(!isFolded){
        		_statistics.won_amt_flop += win;
	        	if(win > 0.0)_statistics.won_flop++;
        	}else{
        		_statistics.won_amt_flop += win;
        	}
        }else if(round == -1){
        	if(!isFolded){
        		_statistics.won_amt_preflop += win;
	        	if(win > 0.0)_statistics.won_preflop++;
        	}else{
        		_statistics.won_amt_preflop += win;
        	}
        }
    }
    public void setRound(int rnd){
    	this.round = rnd;
    	//System.out.println(round+"  ################## round ###################");
    }
    public int getRound(){
    	return round;
    }

    protected void checkLeftButtonsState() {
        _cat.finest("checkLeftButtonsState() called");
        if (_bottomPanel.haveToSitOut()) {
            doSitOut(new TableServerAction(ActionConstants.PLAYER_SITOUT, 
                                           _owner.getMyPlayerNo())); // TARGET  _owner.getPlayerNo() + 1));
        }
        if (_bottomPanel.haveToLeave()) {
            System.out.println("checkLeftButtonsState have to leave table");
        	_bottomPanel._serverProxy.leaveTable(_owner._model.name);
            Action action = 
                new TableServerAction(ActionConstants.PLAYER_LEAVE, _owner.getMyPlayerNo()); // TARGET  _owner.getPlayerNo() + 1);
            doLeave(action);
        }
    }


    /**
     * some player (or client player) join.
     * @param action
     */
    public void doJoin(Action action) {
        if (!(action instanceof PlayerJoinAction)) {
            return;
        }
        PlayerJoinAction joinAction = (PlayerJoinAction)action;
        int pos = action.getTarget();
        ClientPlayerModel joinedPlayer = joinAction.getPlayer();
        boolean me = ((PlayerJoinAction)action).isMe();
        if (!me) {
            // Not ME
        	//_cat.info("Opposite Player Model " + joinedPlayer +  "  Sitting at " + pos + " _owner pos =" + _owner.getMyPlayerNo());
            
            _players[pos] =
                    joinedPlayer == null ? null : 
			PlayerControllerFactory.getPlayerController(gameType, this, joinedPlayer, 
			_skin,  _owner,  pos);
			_players[pos].refresh();
		} else {
			_players[pos] = 
			PlayerControllerFactory.getPlayerController(gameType, this, joinedPlayer, 
			_skin, _owner, pos);
            _me = joinedPlayer;  
            _cat.finest("Getting Player Model " + joinedPlayer +  "  Sitting at " + pos /*+ " _owner pos =" + _owner.getMyPlayerNo()*/);
            if (isMyPlayerNo(pos)) {
                _cat.finest("STATE is = " + joinedPlayer.getStatus());
                _bottomPanel.haveJoin(joinedPlayer.getAllInCount());
                //_bottomPanel.sitInOut.setVisible(true);
            }
            //_players[pos].refresh();
            setWaitingForResponse(false);
        }
    } 
    /**
     * some player (or client player) make SitIn action
     * @param action
     */
    public void doSitIn(Action action) {
    	new Exception().printStackTrace();
        _cat.finest("doSitIn() called");
        int playerSitNo = 
            action.getTarget(); // TARGET  int playerSitNo = action.getTarget() - 1;
        _cat.finest("Target Player = " + playerSitNo);
        if (_players[playerSitNo].isNullPlayer()) {
            return;
        }
        //_players[playerSitNo].setPlayerState(PlayerModel.PLAY);
        _players[playerSitNo].refresh();
        if (isMyPlayerNo(playerSitNo)) {
            setWaitingForResponse(false);
        }
    }

    public void doNeedsSitIn(TableServerAction action) {
        int playerNo = action.getTarget();
        if (_players[playerNo].isNullPlayer()) {
            _cat.log(Level.WARNING, 
                     "Player to NeedsSitIn is NullPlayer. Action = " + action);
            return;
        }

        /*
     // -sn-
      _players[playerNo].setPlayerState(
      action.getId() == ActionConstants.PLAYER_NEEDS_SITIN_FALSE
      ? PlayerModel.SIT
      : PlayerModel.WAIT);
      _players[playerNo].refresh();
     */
    }


    //TableServerAction a = new TableServerAction(IS_ACCEPTING, isAccepting ? 1 : 0);

    /**public void doAcceptingChange(TableServerAction action) {
    _cat.finest("doAcceptingChange action target = " + action.getTarget());
    _owner._clientRoom.setWaiterCount(1 - action.getTarget());
    int wc = _owner._clientRoom.getWaiterCount();
    _cat.finest("Waiters count = " + wc);
  }**/


    /**
     * some player (or client player) make SitOut action
     * @param action
     */
    public void doSitOut(Action action) {
    	//new Exception("doSitOut "+ action.getId()).printStackTrace();
        _cat.finest("doSitOut() called");
        int playerSitNo = 
            action.getTarget(); // TARGET  int playerSitNo = action.getTarget() - 1;
        _cat.finest("targer pos = " + playerSitNo);
        if (playerSitNo < 0 || playerSitNo >= _players.length || 
            _players[playerSitNo] == null || 
            _players[playerSitNo].isNullPlayer()) {
            return;
        }

        // -sn-
        //_players[playerSitNo].setPlayerState(PlayerModel.SIT);
        ////_players[playerSitNo].refresh();
        if (isMyPlayerNo(playerSitNo)) {
            sitOutCurrentPlayer();
        }
        moveAllBetsToCenterForPlayer(playerSitNo);
        _players[playerSitNo].clear();
        _players[playerSitNo].refresh();
    }

    protected void sitOutCurrentPlayer() {
        _cat.finest("--- isPlayerNo(playerSitNo) call from ClientPokerModel.doSitOut(action) ---");
        //**	if (_owner.getClientPlayerName().equals(_players[playerSitNo].getPlayerName())) {
        //setClientPlayerState(PlayerModel.SIT);
        if (_owner.getPlayersPlayCount() <= 1) {
            checkLeftButtonsState();
        }
    }


    /**
     * some player (or client player) leave from room with table
     * @param action
     */
    public void doLeave(Action action) {
        int pos = 
            action.getTarget(); // TARGET  int playerSitNo = action.getTarget() - 1;
       // _cat.info("doLeave getTarget = " + action.getTarget()+" -my pos "+_owner.getMyPlayerNo());
        if (pos < 0 || pos >= _players.length || _players[pos] == null || 
            _players[pos].isNullPlayer()) {
            return;
        }
        _cat.finest("pos "+pos);
        if (isMyPlayerNo(pos)) { // This player leaves
        	_cat.info("doleave pos: "+pos +" MyPos: "+ _owner.getMyPlayerNo());
            if ( !gameType.isTourny() && !gameType.isTPoker()){
                _cat.finest("STATE is = " + _players[pos]._playerModel.getStatus());
                moveAllBetsToCenterForPlayer(pos);                
                _players[pos] =  PlayerControllerFactory.getPlayerController(gameType, this, _skin, pos, _owner);
                _players[pos].clear();
                _players[pos].refresh();
                //_owner._clientRoom.closeRoom(); comment by rk
            }
            else {        
                _players[pos] =  PlayerControllerFactory.getPlayerController(gameType, this, _skin, pos, _owner);
                _players[pos].clear();
                _players[pos].refresh();
                _players[pos]._pokerModel.stopNextMoveTimer();
                _me = null;
                _cat.fine("doleave: making _me null");
//                JFrame f = (JFrame)_owner._clientRoom.getClientRoomFrame();
//   				JOptionPane.showMessageDialog(f.getContentPane(),
//	                    "Game is over, closing the table.", "INFO",
//                        JOptionPane.INFORMATION_MESSAGE);
//                _owner._clientRoom.closeRoomForce();
                
            }
        }
//        _bottomPanel.sitInOut.setText("Sit In");
//        _bottomPanel.sitInOut.setVisible(true);
    }

    public void doSetCur(int no) {
        for (int i = 0; i < _players.length; i++) {
        	
            if (_players[i] != null && !_players[i].isNullPlayer() && no != i) {
                _players[i].setSelected(false);
            }
        }
        if (no >= 0 && _players[no] != null) {
            _players[no].setSelected(true);
        }
    }
    

    public void doSetButton(int num) {
    	//_cat.severe("doSetButton "+num+",dealerChip "+dealerChip+", table id "+name+" ###############################");
        if (num >= 0 && dealerChip != null) {
        	dealerChip.moveToPos(num);
        } else if (_skin != null && _owner != null && num >= 0 && num < 10) {
            _cat.finest("Setting dealer button");
            dealerChip = new DealerChip(num, _skin, _owner);
        }
    }

    public void doCashierRecive(Action action) {
    	CashierAction ca = (CashierAction)action;
        int target = ca.getTarget();
        /*instead of this _players[target].isNullPlayer() 
         _players[_skin.getDefalutPos(_skin._roomSize)].isNullPlayer() 
        because player position will be default position */
       // new Exception(ca.toString()+", "+ca.getAmount()+", "+target+", "+_players[_skin.getDefalutPos(_skin._roomSize)].isNullPlayer()).printStackTrace();
        if (target < 0 || target >= _skin._roomSize || _players[_skin.getDefalutPos(_skin._roomSize)].isNullPlayer() || 
            ca.getAmount() == -1) {
        	if(!gameType.isTPoker()){
            _bottomPanel.appendLog("Get Chips into game failed");
            _cat.warning("doCashierRecive, target = " + target+" amount = "+ca.getAmount());
        	}
            return;
        }
        ClientPlayerController player = _players[target];
        _bottomPanel.updatePlayerState(getClientPlayerState());
        player.update();
    }

    public void doChatRecive(Action action) {
        ChatAction ca = (ChatAction)action;
        int target = ca.getTarget();
        if ((target >= 0 && !_players[target].isMute()) || (target == -1)) {
            _bottomPanel.appendChat(ca.getChatString());
        }
    }

    public void updateNullPlayerSex(char clientSex) {
        for (int i = 0; i < _players.length; i++) {
            if (_players[i].isNullPlayer() && 
                _players[i].getSex() != clientSex) {
                _players[i] = 
                        PlayerControllerFactory.getPlayerController(gameType, this, _skin, i, _owner);
            }
        }
    }

    public void repaintRectangles(Rectangle r1, Rectangle r2) {
        if (r1 != null && r2 != null) {
            r1.add(r2);
            _owner.repaint(r1);
        } else if (r1 != null) {
            _owner.repaint(r1);
        } else if (r2 != null) {
            _owner.repaint(r2);
        }
    }

    private class MovingCard {
        public Card card;
        public int pos;

        MovingCard(Card card, int pos) {
            this.card = (card == null) ? new Card(false) : card;
            this.pos = pos;
        }
    }

    private class MovingVisibleCard {
        public PlCard plCard;
        public int pos;
        public int index;

        MovingVisibleCard(PlCard plCard, int pos, int index) {
            this.plCard = plCard;
            this.index = index;
            this.pos = pos;
        }
    }

    abstract class SwingTimerTask extends java.util.TimerTask {
        public abstract void doRun();

        public void run() {
        	new Exception("SwingTimerTask "+Thread.currentThread());
            if (!EventQueue.isDispatchThread()) {
                EventQueue.invokeLater(this);
            } else {
                doRun();
            }
        }
    }

    class AddCardTimerTask extends SwingTimerTask {
        MovingCard movingCard;
        boolean levelUp;

        public AddCardTimerTask(MovingCard movingCard, boolean levelUp) {
            this.movingCard = movingCard;
            this.levelUp = levelUp;
        }

        public void doRun() {
            //_cat.finest("doRun()::");
            //new Exception().printStackTrace();
            addMovingCard(movingCard, levelUp);
            if (ServerProxy._settings.isSound()) {
                _owner.tryPlayEffect(SoundManager.CARDS_DEALING);
            }
        }
    }

    class MoveBGTask extends TimerTask
    {
      int count = 0;
      
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (count < 30)
	        {
				_moveBgX -= 25;
	          count++;
	        }
	        else
	        {
	        	_moveBgX = 0;
	          _moveBgTimer.cancel();
	        }
			_owner.repaint();
		}
	
    }
   
    public boolean isWaitingForResponse() {
        return waiting_for_response;
    }

    public void setWaitingForResponse(boolean b) {
    	waiting_for_response = b;
        _reservedPosition = b ? _reservedPosition : -1;
        proceeded = b;
    }
    
    
	public void setDealerChip(int _pos) {
		this.dealerChip = null;
		dealerChip = new DealerChip(_pos, _skin, _owner);
	}
	public void setTotalBetBanner(GameEvent ge){
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
	        if(total_amt != 0){
		        this.tb = null;
		        tb = new TotalBet(total_amt, ge.getRake());
		        tb.setView(_skin, _owner);
	        }
	}

	public void placeOccupied(Action action) {
        _cat.log(Level.WARNING, 
                 "Place " + action.getTarget() + " is occupied");
        setWaitingForResponse(false);
    }


    /**
     * @return
     */
    public Vector getDeskCards() {
        return deskCards;
    }
    
    public boolean isPartner(String str){
        if (gameType.isTourny()){
            for (int i=0;i<_partners.length;i++){
            	if (_partners[i].equals(str)){
                    return true;
                }
            }
            return false;
        }
        else return false;
    }

    public boolean readyToAction() {
        return movingCards.isEmpty();
    }

    class ListWithPoster implements List {
        List list;
        ServerMessagesListener listener;

        public ListWithPoster(ServerMessagesListener listener) {
            list = new Vector();
            this.listener = listener;
        }

        public int size() {
            return list.size();
        }

        public void clear() {
            boolean wasEmpty = list.isEmpty();
            list.clear();
            check();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public Object[] toArray() {
            return list.toArray();
        }

        public Object get(int index) {
            return list.get(index);
        }

        public Object remove(int index) {
            Object object = list.remove(index);
            check();
            return object;
        }

        public void add(int index, Object element) {
            list.add(index, element);
        }

        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        public boolean add(Object o) {
            return list.add(o);
        }

        public boolean contains(Object o) {
            return list.contains(o);
        }

        public boolean remove(Object o) {
            boolean b = list.remove(o);
            check();
            return b;
        }

        public boolean addAll(int index, Collection c) {
            return list.addAll(index, c);
        }

        public boolean addAll(Collection c) {
            return list.addAll(c);
        }

        public boolean containsAll(Collection c) {
            return list.containsAll(c);
        }

        public boolean removeAll(Collection c) {
            boolean b = list.removeAll(c);
            return b;
        }

        public boolean retainAll(Collection c) {
            boolean b = list.retainAll(c);
            check();
            return false;
        }

        public Iterator iterator() {
            return list.iterator();
        }

        public List subList(int fromIndex, int toIndex) {
            return list.subList(fromIndex, toIndex);
        }

        public ListIterator listIterator() {
            return list.listIterator();
        }

        public ListIterator listIterator(int index) {
            return list.listIterator(index);
        }

        public Object set(int index, Object element) {
            return list.set(index, element);
        }

        public Object[] toArray(Object[] a) {
            return list.toArray(a);
        }

        private void check() {
            if (list.isEmpty()) {
                listener.serverMessageReceived("", null);
            }
        }
    }


    public long getClientPlayerState() {
        int playerNo = _owner.getMyPlayerNo();
        if (playerNo >= 0 && _players[playerNo] != null) {
//        	System.out.println("getClientPlayerState: "+playerNo+"---"+_players[playerNo].getPlayerName()
//        			+" status: "+PlayerStatus.stringValue(_players[playerNo].getPlayerState()));
            return _players[playerNo].getPlayerState();
        }
        return PlayerStatus.NONE;
    }
    
    public String toString(){
    	StringBuilder sb = new StringBuilder();
        return sb.append(name).append(", ").append(_grid).append(", ").append(name).toString();
    }

}
