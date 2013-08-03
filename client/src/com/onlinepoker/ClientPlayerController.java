package com.onlinepoker;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.golconda.game.PlayerStatus;
import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.onlinepoker.skin.RoomSkin;


//import com.agneya.util.Utils;

public abstract class ClientPlayerController implements PlayersConst {
  static Logger _cat = Logger.getLogger(ClientPlayerController.class.getName());


  /** signalizate then NULL-player */
  protected boolean nullPlayer = true;
  protected boolean show = false;
  /** the component which proced painint */
  protected JComponent _owner = null;
  /** state of player */
  protected int _visibility_state = PLAYER_VISIBLE;
  protected int step = 1;
  protected boolean b = true;
  private float _alpha;
  protected int visibleMessageTact = 0;
  /** Player position */
  protected int _position = -1;
  protected int _rel_position = -1;
  /** Player sex */
  protected char playerSex = ' ';
  private String _name="";  // used as an id
    
  /** players coordinates */
  protected Point playerPlace = null;
  protected Point playerBublesCoords = null;
  protected Point playerNamePos = null;

  //by rk
  protected Point playerNotePos = null;
  protected Rectangle plrNotebounds;
  protected ImageIcon noteIcon ;//= _skin.getNoteIconEmptyImg();//Utils.getIcon(ClientConfig.NOTE_EMPTY); //NOTE_EMPTY, NOTE_PRESENT both same size so consider one

  protected int h = 0, w = 0;
  protected int h1 = 0, w1 = 0;
  protected int x_name = 0, y_name = 0, w_name = 0, h_name = 0;
  protected Rectangle bounds;
  
  /** _playerModel & view of the player */
  protected ClientPlayerModel _playerModel = null;
  protected ClientPlayerView _view = null;
  protected RoomSkin _skin = null;
  protected ImageIcon playerSkin;
  protected ImageIcon namePlate;

  protected ClientPokerModel _pokerModel = null;
  protected static ImageIcon placeInaccessible = null;
  /** Player chips */
  protected Chip[] _roundBetChips;
  /** Player cards */
  protected PlCard[] _player_cards;
  protected long _hand;
  public int cardscount = 0;

  /** Create empty player */
  public ClientPlayerController(ClientPokerModel pm, RoomSkin skin, int position,
                                JComponent _owner) { 
    _pokerModel = pm;
    _roundBetChips = new Chip[0];
    _player_cards = new PlCard[0];
    _hand=0;
    _skin = skin;
    _position = position;
    this._owner = _owner;
    nullPlayer = true;
    bounds = new Rectangle();
    noteIcon = _skin.getNoteIconEmptyImg();
  }


  /** Create client player */
  public ClientPlayerController(ClientPokerModel pm, ClientPlayerModel _playerModel, RoomSkin skin,
                                JComponent _owner, int position) {
    _pokerModel = pm;
    _position = position;
    this._owner = _owner;
    _skin = skin;
    this.playerSex = _playerModel.getSex();
    _name = _playerModel.getPlayerName();
    this._playerModel = new ClientPlayerModel(_playerModel);
    assert _playerModel != null : "Model is null";
    this.playerSex = _playerModel.getSex();
    noteIcon = _skin.getNoteIconEmptyImg();
  }
  
  public void refresh(ClientPlayerModel nm, ClientPlayerModel me){
    _playerModel.refreshPlayerModel(nm);
    if (me==null){
      if (_playerModel.isActive()){ // to filter out dealing cards to fold or out
        setPocketCards(_playerModel.getCards());
      }
    }
    else {
        if (_position != me.getPlayerPosition()){ //don't update cards if they are mine as hand is coming next
          setPocketCards(_playerModel.getCards());
        }
    }
    setRoundBetChips(_playerModel._roundBet);
  }
  
  public void refreshHand(Card[] crd, Cards my_open_cards){
    setPocketCards(crd);
          /// check if this card was their earlier and set select status if it was open
        for (int i = 0; i < my_open_cards.size(); i++) {
          for (int k = 0; k < _player_cards.length; k++) {
              //_cat.finest("Checking card " + tpc[k].getCard());
              if ((_player_cards[k].getIndex() == my_open_cards.getCard(i+1).getIndex()) && 
                          (_player_cards[k].getIndex() != Card.CLOSED_CARD) ) {
                  _cat.finest("Selected card " + my_open_cards.getCard(i+1));
                      _player_cards[k].isSelected = true;
              }
          }
        }
     _owner.repaint();
  }

  public void clear(){
      _roundBetChips = new Chip[0];
      _player_cards = new PlCard[0];
 }
    
    public void doBettingWin(Card[] c){
      for (int i=0;i<c.length;i++){
            for (int k = 0; k < _player_cards.length; k++) {
              if ((_player_cards[k].getIndex() == c[i].getIndex()) && 
                  (_player_cards[k].getIndex() != c[i].CLOSED_CARD)) {
                      _player_cards[k].isSelected=true;
                  }
            }
      }
        
    }
    
  public abstract void setPocketCards(Card[] c);  
  
  public abstract void setPocketCards(Cards c);  
  
  public abstract void addPocketCard(Card c);

  protected void clearPocketCards() {
      Rectangle r = getCardsArea();
      _player_cards = new PlCard[0];
      _hand=0;
      if (r != null) {
          _owner.repaint(r);
      }
  }
  

    protected Rectangle getCardsArea() {
        Rectangle r = null;
        Point pos = null;

        for (int i = 0; i < _player_cards.length; i++) {
            pos = _player_cards[i].getPos();
            if (r == null) {
                r = new Rectangle(pos.x, pos.y, 1, 1);
            } else {
                r.add(pos.x, pos.y);
            }
            if (_player_cards[i].isValid()) {
                r.add(pos.x + _skin.getCardOpenWidth(), 
                      pos.y + _skin.getCardOpenHeight());
            } else {
                r.add(pos.x + _skin.getCardCloseWidth(), 
                      pos.y + _skin.getCardCloseHeight());
            }
        }
        return r;
    }



    protected Rectangle getCurArea() {
        Rectangle r = new Rectangle(bounds);
        Point pos = null;

        for (int i = 0; i < _roundBetChips.length; i++) {
            pos = _roundBetChips[i].getPos();
            r.add(pos.x, pos.y);
            r.add(pos.x + _skin.getChipWidth(), pos.y + _skin.getChipWidth());
        }

        Rectangle rr = Utils.getChipsArea(_roundBetChips);
        if (rr != null) {
            r.add(rr);
        }
        rr = getCardsArea();
        if (rr != null) {
            r.add(rr);
        }
        return r;
    }

    protected int getPocketCardsCount() {
        return _player_cards == null ? 0 : _player_cards.length;
    }


    protected PlCard[] getPocketCards() {
       return _player_cards;
     }

    public long getHand(){
        return _hand;
    }

  public void setSelected(boolean selected) {
    if (_playerModel != null) {
      if (selected != _playerModel.isSelected()) {
        _playerModel.setSelected(selected);
        refresh();
      }
    }
  }
  public void setSelectedResize(boolean selected) {
	    if (_playerModel != null) {
	      if (selected != _playerModel.isSelected()) {
	        _playerModel.setSelected(selected);
	        refresh();
	      }
	    }
	    _owner.repaint();
	  }

  public void setState(int st) {
    _visibility_state = st;
    if (_visibility_state == PLAYER_VISIBLE) {
      _alpha = 1.0f;
    }
    else if (_visibility_state == PLAYER_GAUZY) {
      _alpha = 0.6f;
    }
    else if (_visibility_state == PLAYER_BLINKING) {
      step = 1;
      b = true;
      update();
      return;
    }
    _view.setAlpha(_alpha);
  }

  public Point getChipsPlace() {
    return _view.getChipsPos();
  }

  public void say(String message) {
    _cat.finest("Say = " + message);
    _view.setMessage(message);
    visibleMessageTact = 30;//20
    update();
  }
  
  public void setAmtAtTable(double betAmount) {
    if (!nullPlayer) {
      _playerModel.setAmtAtTable(betAmount);
      refreshNameLabel();
    }
  }
    
    public void showWinning(double chips){
    	//setRoundBetChips(chips);comment by rk, functionality added below
    	//below code commented by rk, because win amount is blinking to last player.
    	/*if (!nullPlayer) {
	      _playerModel.setRoundBet(chips);
	      Rectangle r = Utils.getChipsArea(_roundBetChips);
	      _roundBetChips = Chip.MoneyToOneColumnChips(chips, _view.getChipsPos().x,
	                                      _view.getChipsPos().y, _skin.getChips(),
	                                      _owner);
	      if (r != null) {
	        _owner.repaint(r);
	      }
	    }*/
	    refreshNameLabel();
    }


    public double getAmtAtTable() {
    if (nullPlayer) {
      return 0;
    }
    else {
      return (_playerModel.getAmtAtTable());
    }
  }

  public void setRoundBetChips(double curb) {
    if (!nullPlayer) {
      _playerModel.setRoundBet(curb);
      Rectangle r = Utils.getChipsArea(_roundBetChips);
      _roundBetChips = Chip.MoneyToChips(curb, _view.getChipsPos().x,
                                      _view.getChipsPos().y, _skin.getChips(),
                                      _owner,_skin);
      if (r != null) {
        _owner.repaint(r);
      }
    }
    refreshNameLabel();
  }

  public Chip[] getRoundBetChips() {
    return _roundBetChips;
  }

  public long getPlayerState() {
	if (!nullPlayer) {
      return _playerModel.getStatus();
    }
    else {
      //return PlayerModel.NONE;
      return PlayerStatus.NONE;
    }
  }

  public void setPlayerState(int state) {
	    if (!nullPlayer) {
      _playerModel.setStatus(state);
      refresh();
      _cat.info("set Player state = " + state);
    }
  }


//////////////////////////
  public int getPlayerPosition() {
    return (_position);
  }

  public double getPlayerChips() {
    if (!nullPlayer) {
      return (_playerModel.getRoundBet());
    }
    else {
      return 0;
    }
  }

  public Rectangle getBounds() {
    return (bounds);
  }
  
  //by rk, for player note icon
  public Rectangle getPlayerNotePos() {
	  plrNotebounds = new Rectangle(playerNotePos.x, playerNotePos.y, 
			  noteIcon.getIconWidth(), noteIcon.getIconHeight());
    return (plrNotebounds);
  }

  public boolean isNullPlayer() {
    return (nullPlayer);
  }

  public void refresh() {
    _owner.repaint(playerPlace.x, playerPlace.y, w, h);
//    _cat.finest(" Position: " + pokerModel.playerPos +
//               "Refreshed (x, y): (" + playerPlace.x + ", " + playerPlace.y +
//               ")");
    refreshNameLabel();
  }

  public void refreshNameLabel() {
    _owner.repaint(x_name - 1, y_name - 10, w_name + 1, h_name + 1);
  }

  public void refreshBuble() {
    _owner.repaint(playerBublesCoords.x, playerBublesCoords.y, w1, h1);
  }
  
  public void refreshNamePlate() {
	  _view.refresh();
  }

  public void update() {
    if (_visibility_state == PLAYER_BLINKING) {
      if (b) {
        step++;
      }
      else {
        step--;
      }
      if (step >= 10) {
        b = false;
      }
      if (step <= 0) {
        b = true;
      }
      _alpha = 0.3f + 0.07f * step;
      _view.setAlpha(_alpha);
    }
    refresh();
    if (visibleMessageTact > 0) {
      visibleMessageTact -= 10;
      _view.setVisibleTack(visibleMessageTact);
      if (visibleMessageTact <= 0) {
        refresh();
      }
      refreshNamePlate();
    }
  }

  public void setShow(boolean show) {
    this.show = show;
    refresh();
  }

  public boolean getShow() {
//      System.out.println("getShow() = " + show);
    return (show);
  }
  
  public boolean mouseClick(int mouseX, int mouseY){
      // sub class action
      return false;
  }

  public boolean mouseOver(int mouseX, int mouseY, long clientPlayerState,
                           boolean procceded, boolean canSeat) {
    boolean flag = false;
    if (bounds.contains(mouseX, mouseY)) {
//    	System.out.println("mouseover nullPlayer:"+nullPlayer+" procceded: "+procceded+
//    			" clientPlayerState "+clientPlayerState+" getShow() "+getShow()+" canSeat: "+canSeat);
      if (nullPlayer && procceded == false) {
        if ((clientPlayerState == PlayerStatus.NONE) && !getShow()) {
          if (canSeat) {
        	setState(PLAYER_GAUZY);
          }
          else {
            setState(PLAYER_PLACE_INACCESSIBLE);
          }
          setShow(true);
        }
      }
      else {
        if (getShow()) {
          setShow(false);
        }
// --- mouse on existing player
        showSpeak(true);
      }
      flag = true;
    }
    else {
      if (nullPlayer) {
// --- mouse out from NULL_player
        if (getShow()) {
          setShow(false);
        }
      }
      else {
// --- mouse out from existing player
        showSpeak(false);
      }
    }
    if (flag || procceded) {
      return true;
    }
    else {
      return false;
    }
  }

  private void showSpeak(boolean mustShow) {
    if (_playerModel != null) {
      if (_playerModel.isMustPaintSpeaker() != mustShow) {
        _playerModel.setMustPaintSpeaker(mustShow);
        refreshBuble();
      }
    }
  }

  public void invertSpeak() {
    if (_playerModel != null) {
      _playerModel.setMute(!_playerModel.isMute());
      refreshBuble();
    }
  }

  public String getPlayerName() {
    return _name;
  }

  public void setPlayerName(String name) {
    if (_playerModel != null) {
      _playerModel.setName(name);
    }
  }

  public void paint(JComponent c, Graphics g) {
  }

  public char getSex() {
    return playerSex;
  }

  public boolean isMute() {
    if (_playerModel != null) {
      return _playerModel.isMute();
    }
    return true;
  }
  
  public String toString(){
      return _name;
  }


}
