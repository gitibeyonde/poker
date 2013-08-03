package com.onlinepoker;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.golconda.game.util.ActionConstants;
import com.golconda.game.util.Card;
import com.golconda.message.GameEvent;
import com.onlinepoker.actions.Action;
import com.onlinepoker.actions.ActionVisitor;
import com.onlinepoker.actions.BetRequestAction;
import com.onlinepoker.actions.CardAction;
import com.onlinepoker.actions.ErrorAction;
import com.onlinepoker.actions.InfoAction;
import com.onlinepoker.actions.LastMoveAction;
import com.onlinepoker.actions.MessageAction;
import com.onlinepoker.actions.NewHandAction;
import com.onlinepoker.actions.NextMoveAction;
import com.onlinepoker.actions.SimpleAction;
import com.onlinepoker.actions.StageAction;
import com.onlinepoker.actions.TableServerAction;
import com.onlinepoker.actions.TableServerCloseOpenAction;
import com.onlinepoker.actions.TotalBetAction;
import com.onlinepoker.server.ServerMessageListener;
import com.onlinepoker.server.ServerMessagesListener;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.util.ClientFrameInterface;
import com.poker.game.PokerGameType;


public class ClientPokerController extends JPanel implements MouseMotionListener, MouseListener, 
                                                             PlayersConst, 
                                                             ServerMessagesListener, 
                                                             ServerMessageListener, 
                                                             ActionVisitor
                                                             {

    static Logger _cat =  Logger.getLogger(ClientPokerController.class.getName());
    
//    static{
//    	_cat.setLevel(Level.INFO);
//    }
    /** Players parametrs */
    public  ClientFrameInterface _clientRoom;

    /** Model and View of a desk */
    protected ClientPokerModel _model = null;
    protected ClientPokerView _view = null;
    ClientPlayerModel[] cpms ;
    public ServerProxy _serverProxy;

    /** Skin of this room */
    protected RoomSkin _skin = null;

    /** Input/Output manipulation organs */
    protected BottomPanel _bottomPanel = null;
    protected ResourceBundle _bundle;

    /** _background */
    protected ImageIcon _board;
    private boolean _dealCards = false;
    public boolean _realign_plrs = false;
    GameEvent _ge;
    
    /** title of window */
    public String _title;
    
    /** for stats*/
    double startAmt = 0.0;
    double endAmt = 0.0;

    public ClientPokerController(GameEvent ge, RoomSkin _skin, 
                                 BottomPanel _bottomPanel, 
                                 ClientFrameInterface _clientRoom) {
        _cat.finest("Creating client poker controller");
        this._ge = ge;
        this._skin = _skin;
        this._bottomPanel = _bottomPanel;
        this._clientRoom = _clientRoom;
        _bottomPanel.setClientPokerController(this);

        addMouseMotionListener(this);
        addMouseListener(this);
        this._board = _skin.getBoard();
        setPreferredSize(new Dimension(_board.getIconWidth(), _board.getIconHeight()));
        setBorder(null);
        _model = new ClientPokerModel(ge, _skin, this, _bottomPanel);
        _view = new ClientPokerView(_model, _skin);
        _model.setView(_view);
        _serverProxy = _clientRoom.getLobbyServer();
        //_serverProxy.addServerMessageListener(ge.getGameName(),this);
       // Thread t = new Thread(this);
       // t.start();
    }

    public void serverMessageReceived(String tid, Object actions) {
        if (actions == null || ! tid.equals(this._model.getName())) {
        	if (actions!=null)
        		if(actions instanceof  com.onlinepoker.actions.Action){
        			com.onlinepoker.actions.Action action = 
        	            (com.onlinepoker.actions.Action)actions;
        		_cat.info(tid + " Tid is not same for action " + this._model.getName() + actions.getClass().getName()+ action.actionToString(action.getId()));
        		}
        		return;
        }
        handleNextEvent(actions);
    }
    
    public void serverMessageReceive(String message) {
    	_model.setMessageBanner(message);
		repaint();
	}
    
    protected void handleNextEvent(Object o) {
    	//_cat.severe("handleNextEvent");
        if (o instanceof SimpleAction && 
            ((SimpleAction)o).getId() == ActionConstants.UPDATE) {
            if (_model != null) {
            	_model.update();
            }
            return;
        }
        if(cpms != null && _model._me != null && !_realign_plrs)
    	{
        	//_cat.severe("cpms != null && _model._me != null && !_realign_plrs, rePositionPlayerModel()");
        	_model.rePositionPlayerModel(cpms, _model._me);
    	}
        if (o instanceof GameEvent) {
            //_cat.info("GameEvent " + o);
        	if(!_model.gameType.isTPoker() && ((GameEvent)o).getGameState()){
        		_model._prev_grid = _model._grid;
        	}
        	// when player present on the table stop refreshing with the game event, 
        	// because players should refresh with the real positions 
            if(_model._me == null){
            	setPokerModel((GameEvent)o);
            	//_cat.severe("setPokerModel");
            	}
            //setPokerModel((GameEvent)o);
            updateTitle((GameEvent)o);
            return;
        }
        
        if (o instanceof ClientPlayerModel){
            //_cat.severe("ClientPlayerModel");
            _model._me = (ClientPlayerModel)o;
            return;
        }
        
        if (o instanceof ClientPlayerModel[]){
        	//_cat.severe("ClientPlayerModel[]");
//        	_cat.info("Updating all models myself " + _model._me);
        	cpms = (ClientPlayerModel[])o;
            if (_model._me != null){
             cpms[_model._me.getPlayerPosition()] = _model._me;   
            	 _realign_plrs= true;
             }
            //by rk 
            if(_model.gameType.isTPoker()){
            	//_cat.severe("rePositionPlayersModel");
            	_model.refreshPlayersModel(cpms, _model._me);
            	_model.rePositionPlayerModel(cpms, _model._me);//by rk for reposition when change table
            }else{
            	//_cat.severe("refreshPlayersModel");
            	_model.refreshPlayersModel(cpms, _model._me);
            }
            return;
        }
       
        if (o instanceof Card[]){
            // my cards
            _model.refreshMyHand((Card[])o);
            return;
        }
        
        if (!(o instanceof  com.onlinepoker.actions.Action)){
            _cat.fine("Wrong type");
            throw new IllegalStateException("Wrong type" + o.getClass().toString());
        }

        com.onlinepoker.actions.Action action = (com.onlinepoker.actions.Action)o;
        if (action != null && action.getId() != ActionConstants.PLAYER_JOIN) {
            appendActionToLog(action);
        }
        if (_model != null) {
            action.handleAction(this);
        }
    }

    public void handleSimpleAction(SimpleAction action) {
        int soundNo = -1;
        switch (action.getId()) {
        case ActionConstants.PLAYER_WAITS_BIG_BLIND:
            _bottomPanel.appendLog(MessageFormat.format(_bundle.getString("player.waits.big.blind"), 
                                                       new Object[] { getPlayerName(action) }));
            break;

        case ActionConstants.PLAYER_WAITS_DEALER_PASSES:
            _bottomPanel.appendLog(MessageFormat.format(_bundle.getString("player.waits.dealer.passes"), 
                                                       new Object[] { getPlayerName(action) }));
            break;

        case ActionConstants.DECISION_TIMEOUT:
            _model.doDecisionTimeout();
            _bottomPanel.appendLog(MessageFormat.format(_bundle.getString("player.timed.out"), 
                                                       new Object[] { getPlayerName(action) }));
            break;

        case ActionConstants.SET_CURRENT:


            //_cat.finest("CURRENT SET " + action.getTarget());
            _model.doSetCur(action.getTarget()); // TARGET _model.doSetCur(action.getTarget() - 1);
            break;

        case ActionConstants.SET_BUTTON:
            _model.doSetButton(action.getTarget());//tid added by rk // TARGET  _model.doSetButton(action.getTarget() - 1);
            break;
        default:
            _cat.fine("       don't processing - " + action + " [" + 
                      action.getId() + "|" + action.getType() + "|" + 
                      action.getTarget() + "]");
        }
        ;
        if (_serverProxy._settings.isSound()) {
            tryPlayEffect(soundNo);
        }
        handleDefaultAction(action);
    }

    public void handleStageAction(StageAction action) {
        int soundNo = -1;
       // _cat.severe(Action.actionToString(action.getId()));
        switch (action.getId()) {
        case ActionConstants.WIN:
            soundNo = SoundManager.CHIPS_DRAGGING;
            _model.doBettingWin(action);
            break;

            /**case ActionConstants.TOURNAMENT_WIN:
        _model.doTournamentWin(action);
        break;**/

        case ActionConstants.CHAT:
            _model.doChatRecive(action);
            break;

        case ActionConstants.CASHIER:
        	_model.doCashierRecive(action);
            break;

        case ActionConstants.START_GAME:
        	//***for stats***
        	if(_model._me != null){
	        	endAmt = _model._me.getAmtAtTable();
	        	//System.out.println("Amt at Table @end "+endAmt);
	        	if(!_model.isFolded && startAmt > 0.0){
	        		_model.setStats(startAmt, endAmt);
	        	}else if(_model.isFolded){
	        		_model.setRound(_model.foldedRound);
	        		_model.setStats(startAmt, endAmt);
	        		_model.isFolded = false;
	        	}
	        	startAmt = _model._me.getAmtAtTable();
	        	//System.out.println("Amt at Table @start "+startAmt);
        	}
            soundNo = SoundManager.SHUFFLE_DECK;
            _model.doGameStart();
            _model.doClearDesk();
            _model.clearTotalBetBanner();
            _model.refreshDealerPos();
            _dealCards = true;
            //if cond by rk
            //written in appendActionToLog()
//            if(_model._grid > _model._prev_grid){
//            	StringBuilder sb = new StringBuilder();
//            	_bottomPanel.appendLog(sb.append("New Hand ").append(_model._grid).append(" on table ").append(_model.name).toString());
//            	//_model.removeMessageBanner();//by rk
//            	//System.out.println("message banner removed");
//            }
            if(_model.isquickFolded)_model.isquickFolded = false;
            _bottomPanel.updateRebuyChipsStatus();
            break;

        case ActionConstants.END_GAME:
        	_model.doClearDesk();
            _model.checkLeftButtonsState();
            //by rk, added for previous hand history for Terminal poker and ring games, 
            //so that player able to see last hand even join in middle of game.
            if(_model.gameType.isTPoker()){
            	_model.removeMessageBanner();
            	_model.setMessageBannerOnTable("Waiting for players");
            	if(_model._grid != -1){
            		_model._prev_grid = _model._grid;
            		_model._prev_name = _model.name;
            	}
            }else{
            	_model._prev_grid = _model._grid;
            }
            //_bottomPanel.deselectOtherCheckBoxes(null);
            break;

        case ActionConstants.FLOP:
        	
        	
        case ActionConstants.TURN:
        	
        	
        case ActionConstants.RIVER:
        	
        	
        case ActionConstants.FOURTH_STREET:
            _model.moveAllBetsToCenter();
            _model.moveRakeToDealer();
            soundNo = SoundManager.CHIPS_DRAGGING;
            //for Stats
            int aId = action.getId();
        	int rnd = -1;
        	if(aId == ActionConstants.PREFLOP){
        		rnd = -1;
        	}else if(aId == ActionConstants.FLOP){
        		_model._statistics.saw_flop++;
        		rnd = 0;
        	}else if(aId == ActionConstants.TURN){
        		_model._statistics.saw_turn++;
        		rnd = 1;	
        	}else if(aId == ActionConstants.RIVER){
        		_model._statistics.saw_river++;
        		rnd = 2;
        	}
        	_model.setRound(rnd);
        	
            break;
            
        case ActionConstants.PRE_WIN:
        	//REMOVE MOVES for TPOKER
            //if(_model.gameType.isTPoker()){
            	_bottomPanel.hideMoveButtons();
            //	 _cat.severe("################################## HIDING MOVE BUTTONS ###################");
            	if(_model != null)
                	_model.stopNextMoveTimer();
            	if(_model.getGameType().isTPoker()){
            		_model._bottomPanel.quickFoldVisible(false);
            	}
           // }
            _cat.fine("Moving Bets to Center");
            _model.moveAllBetsToCenter();
            //_model.moveRakeToDealer();
            _bottomPanel.deselectOtherCheckBoxes(null);
            soundNo = SoundManager.CHIPS_DRAGGING;
            break;
            
        case ActionConstants.POST_WIN:
            _model.showWinning();
            //for Stats
            int c_size = _model.deskCards.size();
//            System.out.println(c_size+" -winnercards length "+_model.winnerCards.length);
//        	if(c_size == 5 && _model.winnerCards.length == 5){
//        		_model._statistics.saw_showdown++;
//        		_model.setRound(3);
//        	}else 
        	if(c_size == 5){
        		_model._statistics.saw_showdown++;
        		_model.setRound(3);
        	}else if(c_size == 4)
        		_model.setRound(1);
        	else if(c_size == 3)
        		_model.setRound(0);
        	else if(c_size == 0)
        		_model.setRound(-1);
            break;
        

        case ActionConstants.PLAYER_MESSAGE:
            _cat.fine("Broadcasted Msg: " + 
                        ((MessageAction)action).getClientMessage());
//            MessagePopup m = 
//                new MessagePopup(_clientRoom, ((MessageAction)action).getClientMessage());
//            m.setResizable(false);
            String _serverMessage = ((MessageAction)action).getClientMessage();
            _model._infoMsgBanner = _serverMessage;
            _model.removeMessageBanner();
            _model.setMessageBannerOnTable(_serverMessage);//by rk
           // _model.setMessageBanner(_serverMessage);//commented by rk
            break;
        case ActionConstants.PREFLOP:
        	_model.setRound(-1);
            break;
        default:
            _cat.fine("no processing - " + action + " [" + 
                      action.getId() + "|" + action.getType() + "|" + 
                      action.getTarget() + "]");
        }
        if (_serverProxy._settings.isSound()) {
            tryPlayEffect(soundNo);
        }
        handleDefaultAction(action);
    }

    public void handleCardAction(CardAction action) {
        //_cat.info("_dealCards=" + _dealCards + ", CardAction=" + action);
        Card[] cards = ((CardAction)action).getCards();
        switch(action.getId()){
        case  ActionConstants.SHOW_CARD:
            if ( cards != null && 
            cards.length > 1) {
                _cat.fine("Show players card");
                _model.doCardAction(action);
            }
            break;
        case ActionConstants.ACTION_TYPE_CARD:
        case ActionConstants.ACTION_TYPE_STAGE:
            if (_dealCards) {
                //_cat.info("Deal Cards..." + action.getCardsCount());
                _model.pushBackCards(action.getCardsCount());
                if(_model.getGameType().isTPoker())
                    _model._bottomPanel.quickFoldVisible(true);
            }
            break;
        case ActionConstants.SHOW_SHOWDOWN_CARD:
        case ActionConstants.SHOW_WINNER_CARD:
            _model.doShowDown(action);
            break;
        case ActionConstants.DISCARD:
        	_model.doDiscards(action);
        	_cat.finest("after discard");
            break;
        default:
            //_cat.info("COMMUNITY CARDS cards");
            _model.doCardAction(action);
            if (_serverProxy._settings.isSound()) {
                tryPlayEffect(SoundManager.CARDS_DEALING);
            }
        }
        handleDefaultAction(action);
    }

    public void handleBettingAction(Action action) {
        int soundNo = -1;
        if(cpms != null && _model._me != null && !_realign_plrs)
    	{
        	_model.rePositionPlayerModel(cpms, _model._me);
    	}
        
        switch (action.getId()) {

        case ActionConstants.NEW_HAND:
            NewHandAction nha = (NewHandAction)action;
            _model.doUpdateStates(nha.getStates());
            _model.startNewHand();
            _model.clearTotalBetBanner();
            _model.refreshDealerPos();
            break;

        case ActionConstants.ALLIN:
        case ActionConstants.FOLD:
        case ActionConstants.CALL:
        case ActionConstants.CHECK:
        case ActionConstants.RAISE:
        case ActionConstants.ANTE:
        case ActionConstants.SB_BB:
        case ActionConstants.BET:
        case ActionConstants.SMALL_BLIND:
        case ActionConstants.BIG_BLIND:
        	if(_model.getGameType().isTPoker() && (action.getId() == ActionConstants.SMALL_BLIND || action.getId() == ActionConstants.BIG_BLIND ||  action.getId() == ActionConstants.SB_BB )){
        		_model.removeMessageBanner();//by rk
                _model.refreshDealerPos();	
            }
        	//_cat.info("-------------------------- CALL / CHECK / FOLD");
            _model.setLastSendBetAction(null);
            if (action instanceof BetRequestAction) {
                _cat.fine("BET REQUEST ACTION = " + action);
                if(_model.getGameType().isTPoker()){
            		_model.removeMessageBanner();
                }
                haveBetAction(action);
                soundNo = SoundManager.PIG;
            }

            else if (action instanceof LastMoveAction) {
                LastMoveAction lma = (LastMoveAction)action;
                _model.setCurrentBet(lma.getCurrentBet());
                _model._players[lma.getTarget()].setRoundBetChips(lma.getCurrentBet());
                _model._players[lma.getTarget()].setAmtAtTable(lma.getAmountAtTable());
                _model.refreshPot();
                // player selected false after he make any action
                _model._players[lma.getTarget()].setSelected(false);
                _cat.finest("LASTACTION=" + lma);
                //if (!_serverProxy._name.equals(playerName)) {
                    haveBetAction(action);
               //}
            }
            _bottomPanel.normalizePanel();
            break;

        default:
            //_cat.info("handleBettingAction()::default case");
            haveBetAction(action);
            break;
        }
        if (_serverProxy._settings.isSound()) {
            tryPlayEffect(soundNo);
        }
        handleDefaultAction(action);
    }

    public void handleTableServerAction(TableServerAction action) {
        switch (action.getId()) {
        case ActionConstants.PLAYER_REGISTERED:
            break;

        case ActionConstants.PLAYER_UNREGISTERED:
            break;

        case ActionConstants.PLAYER_JOIN:

        case ActionConstants.PLAYER_REJOIN:
            _model.doJoin(action);
            break;

//        case ActionConstants.PLAYER_SITIN:
//            _model.doSitIn(action);
//            break;

//        case ActionConstants.PLAYER_SITOUT:
//        	new Exception(" ActionConstants.PLAYER_SITOUT").printStackTrace();
//            //_model.doSitOut(action);
//            break;

        case ActionConstants.PLAYER_LEAVE:
        	_cat.fine("ActionConstants.PLAYER_LEAVE "+action.getTarget());
            _model.doLeave(action);
            break;

        case ActionConstants.TABLE_INFO:
            if (_model._me != null)
                _bottomPanel.setInfoStrip(action, _model.getPlayerMoneyAtTable());
            _model.setPot(((InfoAction)action).getPot());
            _model.setRake(((InfoAction)action).getRake());
            _model.refreshPot();
            break;
            
        case ActionConstants.TOTALBET_INFO:
        	_model.setRake(((TotalBetAction)action).getTotalBet().getRake());
        	_model.setTotalBet(((TotalBetAction)action).getTotalBet());
            break;

        case ActionConstants.IMMEDIATE_SHUTDOWN:

        case ActionConstants.GRACEFUL_SHUTDOWN:
        	_model.removeMessageBanner();
            _clientRoom.closeRoomForce();
            break;
            
        case ActionConstants.MANUAL_GRACEFUL_SHUTDOWN:
        	_clientRoom.closeOpenRoom(((TableServerCloseOpenAction)action).getOldTid(), ((TableServerCloseOpenAction)action).getGe());
            _bottomPanel.unCheckAll();//by rk to avoid auto-checks on new table
            _bottomPanel.deselectOtherCheckBoxes(null);
            if(_model.isquickFolded)_model.isquickFolded = false;
            if(_bottomPanel.quickFold.isVisible()){
            _bottomPanel.quickFoldVisible(false);
            }
            break;

        case ActionConstants.PLAYER_NEEDS_SITIN_TRUE:

        case ActionConstants.PLAYER_NEEDS_SITIN_FALSE:
            _model.doNeedsSitIn(action);
            break;

            /** case ActionConstants.IS_ACCEPTING:
        _model.doAcceptingChange(action);
        break;**/

        default:
            ;
        }
        handleDefaultAction(action);
    }

    public void handleErrorAction(ErrorAction action) {
        switch (action.getId()) {
        case ActionConstants.PLACE_OCCUPIED:
            _model.placeOccupied(action);
            break;
        }
        ;
        handleDefaultAction(action);
    }

    public void handleDefaultAction(Action action) {
    }
    
    public void updateTitle(GameEvent ge) {
    	
    	_model.name = ge.getGameName();
    	_model._grid = ge.getGameRunId();
    	_model.minBet = ge.getMinBet();
    	_model.maxBet = ge.getMaxBet();
    	
        PokerGameType pgt = new PokerGameType(ge.getType());
            	
    	StringBuilder sbuf = new StringBuilder();
    	if(pgt.isTourny())sbuf.append(ge.getTournyId());
    	else sbuf.append((_model.getName().startsWith("0") || _model.getName().startsWith("1") || _model.getName().startsWith("2") ||
    			_model.getName().startsWith("3") || _model.getName().startsWith("4") || _model.getName().startsWith("5"))?_model.getName().substring(4):_model.getName());
        sbuf.append(" : hand id = [").append(_model.name).append( "-").append(_model._grid).append("], Stakes=");
        sbuf.append("€" ).append(SharedConstants.doubleToString(_model.minBet / 2))
        .append("/"+SharedConstants.doubleToString(_model.minBet));
        
        if (_model.maxBet ==0){
            sbuf.append(  " PL,"); 
        }
        else if (_model.maxBet == -1){
            sbuf.append( " NL,");
        }
        sbuf.append("v1.01.17R")
        .append("  Good luck ")
        .append(_serverProxy._name);
        
//        _title = sbuf.toString(); 
//    	_clientRoom.setTitle(
//        _title +"v1.1.3"+"  Good luck " +  _serverProxy._name);
    	_clientRoom.setTitle(sbuf.toString());
        repaint();
    }

      /*public void updateTitle(ClientPokerModel model) {
        _clientRoom.setTitle(
            _title + "   Good luck " +  _serverProxy._name);
      }*/
      
    protected void setPokerModel(GameEvent ge) {
    	//_cat.severe("setPokerModel()");
    	//resize code
    	_model.refreshPokerModel(ge,_model.CUNSTROCTOR);
    	//_model.refreshPokerModel(ge);
    	_view = null;//by rk, for GC
        _view = new ClientPokerView(_model, _skin);
        _model.setView(_view);
        repaint();
    }
//    //by rk, used when close_open command came from server, to avoid player blinking on new table
//    protected void setPokerModelWhenCloseOpen(GameEvent ge) {
//    	_model.refreshPokerModelWhenCloeseOpen(ge);
//        _view = new ClientPokerView(_model, _skin);
//        _model.setView(_view);
//        repaint();
//    }

    public ClientPokerView getView() {
        return (_view);
    }

    public ClientPokerModel getModel() {
        return (_model);
    }

    public int getPlayersPlayCount() {
        ClientPlayerController[] models = _model.getClientPlayers();
        int count = 0;
        for (int i = 0; i < models.length; i++) {
            if (isPlayerPlay(models[i])) {
                count++;
            }
        }
        //   _cat.finest("count: " + count);
        return count;
    }

    private boolean isPlayerPlay(ClientPlayerController _model) {
        return _model != null && !_model.isNullPlayer() && _model._playerModel.isActive();
    }
    //resize code
    public void resize(GameEvent ge, RoomSkin skin){
    	//new Exception("resizeing ").printStackTrace();
    	//System.out.println("ClientPokerController");
//    	 _model = new ClientPokerModel(_ge, _skin, this, _bottomPanel);
//         _view = new ClientPokerView(_model, _skin);
    	_skin=skin;
    	//_bottomPanel.resize(skin);//commented by rk, bottompanel is resizing in _model.resize() so no need here
        this._board = _skin.getBoard();
        if (_view != null)
        _view.resize(skin);
      //  if(ClientRoom.lastGameEvent!=null)serverMessageReceived(ClientRoom.lastGameEvent);
        if (_model!=null){
        _model.resize(ge,skin);
        _model.update();
        }
//        _model.update();
        
        //if(ClientRoom.lastGameEvent!=null)serverMessageReceived(ge);
        //String tid = ge.getGameName();
        
        repaint();
    }
    //by rk, for reposition players when table is resized
    public void rePositionPlrs(ClientPlayerModel[] cpm, ClientPlayerModel me){
    	//_model.rePositionPlayerModel(cpm, me);
    	_model.refreshPlayersModelResize(cpm, me);
    }
    
    int counter=0, diff;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        _board.paintIcon((Component)_clientRoom, g, 0, 0);
        if (_view != null)     _view.paint(this, g);
        if (_model!=null) _model.paint(this, g);
    }
  

    public double getMyWorth() {
        return _serverProxy.getWorth();
    }
   
    public double getAmtAtTable() {
        return _serverProxy.getWorth();
    }

    public int getMyPlayerNo() {
        if (_model == null) {
            _cat.finest("Model is null ");
            return -1;
        }
        String clientPlayerName = _serverProxy._name;
        if (clientPlayerName==null)return -1;
        ClientPlayerController[] players = _model.getClientPlayers();
        //_cat.finest("Player name = " + clientPlayerName);
        for (int i = 0; i < _skin._roomSize; i++) {
            //_cat.finest("Player = " + players[i]);
            if (players[i] != null && players[i].isNullPlayer() == false && 
                clientPlayerName.equals(players[i].getPlayerName())) {
                return i;
            }
        }
        return -1;
    }
    
    public ClientPlayerController getMyPlayer() {
        if (_model == null) {
            _cat.finest("Model is null ");
            return  null;
        }
        String clientPlayerName = _serverProxy._name;
        ClientPlayerController[] players = _model.getClientPlayers();
        for (int i = 0; i < _skin._roomSize; i++) {
            if (players[i] != null && players[i].isNullPlayer() == false && 
                clientPlayerName.equals(players[i].getPlayerName())) {
                //_cat.finest("Player name = " + clientPlayerName);
                return players[i];
            }
        }
        return null;
    }
    
    String playerName;

    public void appendActionToLog(Action a) {
        switch (a.getId()) {
        case ActionConstants.BET_REQUEST:
            _bottomPanel.logItsYourTurn(_serverProxy._name);
            break;
        case ActionConstants.START_GAME:
        	if(!_model.gameType.isTPoker())
        	_bottomPanel.appendSep();
            _model.doClearDesk();
            break;
        default:
//        	String s = a.toMessage(getPlayerName(a)); commented by rk
        	String s = "";
        	//if cond added by rk, to show "player name" leaves in chat pannel for term poker
        	if(_model.gameType.isTPoker() && a instanceof TableServerAction && a.getId() == ActionConstants.PLAYER_LEAVE ){
        		TableServerAction ac = (TableServerAction)a;
            	s = a.toMessage(ac.getName());
            }else{
            	s = a.toMessage(getPlayerName(a));
            }
            //*********************************************
            playerName = getPlayerName(a); // It will have the current Player name.
            //*********************************************
            if (s.length() > 0) {
                //_cat.finest("Player name = " + s);
            	if((a.getId() == ActionConstants.SMALL_BLIND || a.getId() == ActionConstants.SB_BB) && _model.gameType.isTPoker()){
            		StringBuilder sb = new StringBuilder();
                	_bottomPanel.appendSep();
                	_bottomPanel.appendLog(sb.append("New Hand ").append(_model._grid).append(" on table ").append(_model.name).toString());
                	_bottomPanel.appendLog(s.toString());
            	}else{
                    _bottomPanel.appendLog(s.toString());
            	}
            }
        }
    }

    public String getPlayerName(Action a) {
        int target = a.getTarget(); // TARGET int target = a.getTarget() - 1;
        boolean isTherePlayer = 
            target >= 0 && _model != null && target < _model.getPlayers().length;
        ClientPlayerController player = null;
        if (isTherePlayer) {
            player = _model.getClientPlayers()[target];
        }
        isTherePlayer = player != null;
        if (isTherePlayer) {
            return player.getPlayerName();
        } else {
        	StringBuilder sb = new StringBuilder();
            return sb.append("(").append(target).append(")").toString();
        }
    }

    public void tryExit() {
        _cat.fine("tryExit");
       SoundManager.loopTest();
        _clientRoom.closeRoom();
    }

    public Dimension getPreferredSize() {
        return new Dimension(_board.getIconWidth(), 
                             _board.getIconHeight());
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            mouseClicked(e);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        mouseMoved(new MouseEvent(this, -1, -1, -1, -999, -999, 0, false));
    }

    public void mouseMoved(MouseEvent e) {
        if (_model == null || _view == null) {
            return;
        }
        _model.mouseMoved(e.getX(), e.getY());
    }

    public void mouseClicked(MouseEvent e) {
        if (_model == null || _view == null) {
            return;
        }
        _model.mouseClicked(e);
    }


    protected void haveBetAction(com.onlinepoker.actions.Action action) {
        int soundNo = -1;
        if (action.getId() == ActionConstants.FOLD) {
            _model.doFold(action);
            if(_model.isMyPlayerNo(action.getTarget())){
            	appendActionToLog(action);
            }
        } else {
        	//_cat.severe("in CPC haveBetAction");
        	//new Exception("in haveBetAction").printStackTrace();
            _model.doBetting(action);
        }

//        if (action.getTarget() == getMyPlayerNo()) {
//            _bottomPanel.deselectOtherCheckBoxes(null);
//        }

        int id = action.getId();
        
        if (action instanceof NextMoveAction){
        	//_model._playersMod[action.getTarget()].
        	_model.startNextMoveTimer(action.getTarget());
        }
        else if (action instanceof BetRequestAction) {
            soundNo = SoundManager.YOUR_TURN;
        }

        else if (action instanceof LastMoveAction) {
            switch (id) {
	            case ActionConstants.CALL:
	                soundNo = SoundManager.CHIPS_BETTING;
	                break;
	
	            case ActionConstants.CHECK:
	                soundNo = SoundManager.CHECK;
	                break;
	
	            case ActionConstants.FOLD:
	                soundNo = SoundManager.CARDS_FOLDING;
	                break;
	
	            default:
	                soundNo = SoundManager.CHIPS_BETTING;
	                break;
	            }
            _model.stopNextMoveTimer();
        }

        if (soundNo >= 0 && 
            _serverProxy._settings.isSound()) { // && !_model.getClientPlayerName().equalsIgnoreCase(playerName)) {
            //      if(!_serverProxy._name.equals(playerName)){
            tryPlayEffect(soundNo);
            //      }

            //      _cat.finest("############### SOUND..........###############:  " + playerName +" :"+ id);// + "_serverProxy._name" +_serverProxy._name);
        }
    }

    protected void tryPlayEffect(int effect) {
    	if (effect >= 0 && _bottomPanel != null && _serverProxy._settings.isSound() && 
            !_clientRoom.isWindowClosing()) {
            SoundManager.playEffect(effect);
        }
    }

    protected void tryPlayEffectRep(int effect) {
        if (effect >= 0 && _bottomPanel != null && _serverProxy._settings.isSound() && 
            !_clientRoom.isWindowClosing()) {
            SoundManager.playEffectRepeatable(effect);
        }
    }


    /**
     * @return money that player take on the desk
     */
    public double getPlayerCurrentBet() {
        int playerNo = getMyPlayerNo();
        if (playerNo < 0) {
            try {
                throw new Exception("Invalid user!!! Can't dedicate money for unknown user");
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            return -1;
        }
        return _model._players[playerNo].getPlayerChips();
    }

    public String getTableName() {
        return _model.getName();
    }

	
   /**
   *
   * Agneya NEW FUNC
   *
   * This will display the message which admin has send to this table
   */
    /*
    public void showMessagePopup(String msg, int wdth, int hth) {
      new MessagePopup(wdth, hth, msg);
    }
   */
    // END NEW FUNC
}
