package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.onlinepoker.skin.RoomSkin;
import com.poker.game.util.Hand;


public class HoldemPlayerController extends ClientPlayerController {
  static Logger _cat = Logger.getLogger(HoldemPlayerController.class.getName());

    private ImageIcon closeCard;
    //private ImageIcon openCard;

    protected Point playerCardsPlaceOpen = null;
    protected Point playerCardsPlaceClose = null;

    /** Create empty player */
    public HoldemPlayerController(ClientPokerModel pm, RoomSkin skin, 
                                  int position, JComponent owner) {
        super(pm, skin, position, owner);
        playerSkin = skin.getThisPlayersSkin();
        setupCoord(skin, position);
        _view = 
//                new ClientPlayerView(playerSkin, namePlate, playerPlace, skin.getChipsPlace(position, 
//                                                                                 playerSex), 
//                                     skin.getSpeakIcon(), skin.getBublesIcon(), 
//                                     playerBublesCoords, 
//                                     skin.getBublesOrientation(position), 
//                                     null,pm._owner._clientRoom.getType()); // model
        	//resize code
        	new ClientPlayerView(playerSkin, namePlate, playerPlace, skin.getChipsPlace(position, 
			                    playerSex), 
								skin.getSpeakIcon(), skin.getBublesIcon(), 
								playerBublesCoords, 
								skin.getBublesOrientation(position), 
								null,pm._owner._clientRoom.getGameType(), skin); // model
        
    	


        setState(PLAYER_GAUZY);
        refresh();
    }


    /** Create client player */
    public HoldemPlayerController(ClientPokerModel pm, ClientPlayerModel model, 
                                  RoomSkin skin, JComponent owner, int position) {
        super(pm, model, skin, owner, position);
        playerSkin = skin.getPlayersSkin(model.getAvatar());
        setupCoord(skin, position);
        
        nullPlayer = false;
        _playerModel.setNameBounds(x_name, y_name, w_name, h_name);
        _view = 
//                new ClientPlayerView(playerSkin, namePlate, playerPlace, skin.getChipsPlace(position, 
//                                                                                 model.getSex()), 
//                                     skin.getSpeakIcon(), skin.getBublesIcon(), 
//                                     playerBublesCoords, 
//                                     skin.getBublesOrientation(position), 
//                                     this._playerModel,pm._owner._clientRoom.getType()); // model
        	//resize code
        	new ClientPlayerView(playerSkin, namePlate, playerPlace, skin.getChipsPlace(position, 
								model.getSex()), 
								skin.getSpeakIcon(), skin.getBublesIcon(), 
								playerBublesCoords, 
								skin.getBublesOrientation(position), 
								this._playerModel,pm._owner._clientRoom.getGameType(),skin); // model

        _roundBetChips = 
                Chip.MoneyToChips(this._playerModel.getRoundBet(), _view.getChipsPos().x, 
                                  _view.getChipsPos().y, skin.getChips(), 
                                  owner, skin);

        setPocketCards(model.getCards());
        refresh();
    }


    private void setupCoord(RoomSkin skin, int num) {
        playerPlace = skin.getPlayerPlace(num, playerSex);
        //playerSkin = skin.getPlayersSkin();
        // player skin is taking from the clientplayermodel if it is there otherwise picking from the serverproxy
        namePlate=skin.getNamePlate();
        w = playerSkin.getIconWidth();
        h = playerSkin.getIconHeight();
        bounds = new Rectangle(playerPlace.x, playerPlace.y, w, h);
        playerBublesCoords = skin.getPlayersBublesCoords(num, playerSex);

        this.playerCardsPlaceClose = 
                new Point(skin.getPlayerCardsPlaceClose(num, playerSex));
        closeCard = skin.getCloseCard();
        this.playerCardsPlaceOpen = 
                new Point(skin.getPlayerCardsPlaceOpen(num, playerSex));
        //openCard = skin.getOpenCards();
        w1 = 80;
        h1 = 44;
        playerNamePos = skin.getNamePos(num, playerSex);
        x_name = playerNamePos.x;
        y_name = playerNamePos.y;
        Point p=skin.getNamePosSize();
        w_name = p.x; //181;
        h_name = p.y; //111;
        if (placeInaccessible == null) {
            placeInaccessible = skin.getPlaceInaccessibleIcon();
        }
        
      //by rk, for playerNote icon
        playerNotePos = skin.getPlrNotePos(num);
    }

    public void clear() {
        super.clear();
        Rectangle r = getCurArea();

        _roundBetChips = new Chip[0];
        _player_cards = new PlCard[0];

        if (!nullPlayer) {
            _playerModel.setRoundBet(0);
        }
        _owner.repaint(r);
    }

    public void paint(JComponent c, Graphics g) {
        if (nullPlayer && !show) {
            return;
        }

        //      Players closed cards
        for (int i = 0; i < _player_cards.length; i++) {
            if (_player_cards[i].getIndex() == Card.CLOSED_CARD) {
                _player_cards[i].paint(c, g);
               
            }
        }
        
        
        // *********    Players chips and amount   ************
        int i = 0;

        for (; i < _roundBetChips.length; i++) {
        	_roundBetChips[i].setSkin(_skin);
            _roundBetChips[i].paint(c, g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial Narrow", Font.PLAIN, _skin._ratio_x > 1?12:11));
        if (i > 0) {
        	try {
				String amtString = com.agneya.util.Utils.getRoundedString(this._playerModel.getRoundBet());
				g.drawString("€" + amtString, _roundBetChips[i - 1].endPos.x - 10, 
				             _roundBetChips[i - 1].endPos.y + (int)(30*_skin._ratio_y));
			} catch (Exception e) {
				_cat.info("i->"+i);
				//e.printStackTrace();
			}
        } 
     // paint all cards
        for (i = 0; i < _player_cards.length; i++) {
              _player_cards[i].paint(c, g);
              //_cat.debug("Printing player cards " + _player_cards[i] + " pos =" + _playerModel._pos);  
        }
            if (_visibility_state == PLAYER_PLACE_INACCESSIBLE) {
                Graphics gcopy = 
                    g.create(playerPlace.x + 40, playerPlace.y + 55, 32, 32);
                placeInaccessible.paintIcon(c, gcopy, 0, 0);
                gcopy.dispose();
            } else {
                try {
					_view.paint(c, g);
				} catch (Exception e) {
				}
            }
    }
        public void setPocketCards(Cards modelCards) {
            PlCard[] tpc = _player_cards;
            PlCard[] newCards = new PlCard[modelCards.size()];
            _hand = Hand.getHandFromCardArray(modelCards.getCards());
            //_cat.finest("Size=" + modelCards.size());
            for (int i = 0; i < modelCards.size(); i++) {
                //_cat.finest("Cards=" + modelCards.getCard(i+1));
                newCards[i] = 
                        new PlCard(new Point(playerCardsPlaceOpen.x, playerCardsPlaceOpen.y), 
                                   /*openCard,*/ closeCard, 
                                   modelCards.getCard(i + 1), i, _owner,_skin);
                /// check if this card was their earlier and set select status accordingly
                for (int k = 0;tpc != null && k < tpc.length; k++) {
                    //_cat.finest("Checking card " + tpc[k].getCard());
                    if ((newCards[i].getIndex() == tpc[k].getIndex()) && 
                        (newCards[i].getIndex() != Card.CLOSED_CARD)) {
                        if (tpc[k].isSelected) {
                            newCards[i].isSelected = true;
                        }
                    }
                }
                // set the card as open
            }
            _player_cards = newCards;
            _owner.repaint();
        }


        public void setPocketCards(Card[] pocket_cards) {
            Cards modelCards = new Cards(false);
            modelCards.addCards(pocket_cards);
            setPocketCards(modelCards);
        }


        public void addPocketCard(Card card) {
            PlCard[] newCards = new PlCard[_player_cards.length + 1];
            System.arraycopy(_player_cards, 0, newCards, 0, _player_cards.length);
            //_cat.finest("Add Card PlCards[] " + card.toString());
            newCards[newCards.length - 1] = 
                    new PlCard(new Point(playerCardsPlaceClose.x, 
                                         playerCardsPlaceClose.y), /*openCard,*/ 
                               closeCard, card, newCards.length - 1, 
                               _owner,_skin);
            for (int i = 0; i < _player_cards.length; i++) {
                //_cat.finest("Checking card " + _player_cards[i].getIndex());
                if ((card.getIndex() == _player_cards[i].getIndex()) && 
                    (card.getIndex() != Card.CLOSED_CARD)) {
                    if (_player_cards[i].isSelected) {
                        newCards[newCards.length - 1].isSelected = true;
                    }
                }
            }
            _player_cards = newCards;
            newCards[newCards.length - 1].refresh();
        }

}
