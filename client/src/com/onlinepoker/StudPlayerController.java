package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.onlinepoker.skin.RoomSkin;


public class StudPlayerController extends ClientPlayerController {

    private ImageIcon closeCard;
    //private ImageIcon openCard;

    protected Point playerCardsPlaceOpen = null;
    protected Point playerCardsPlaceClose = null;

    /** Create empty player */
    public StudPlayerController(ClientPokerModel pm, RoomSkin skin, 
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
						null,pm._owner._clientRoom.getGameType(),skin); // model
        setState(PLAYER_GAUZY);
        refresh();
    }


    /** Create client player */
    public StudPlayerController(ClientPokerModel pm, ClientPlayerModel model, 
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
        _roundBetChips = Chip.MoneyToChips(this._playerModel.getRoundBet(), _view.getChipsPos().x,
                                          _view.getChipsPos().y, skin.getChips(),
                                          owner,_skin);

        setPocketCards(model.getCards());
        refresh();
    }


    private void setupCoord(RoomSkin skin, int num) {
        playerPlace = skin.getPlayerPlace(num, playerSex);
        //playerSkin = skin.getPlayersSkin();
        namePlate=skin.getNamePlate();
        w = playerSkin.getIconWidth();
        h = playerSkin.getIconHeight();
        bounds = new Rectangle(playerPlace.x, playerPlace.y, w, h);
        playerBublesCoords = skin.getPlayersBublesCoords(num, playerSex);

        this.playerCardsPlaceClose = 
                new Point(skin.getPlayerCardsPlaceOpen(num, playerSex));
        closeCard = skin.getStudCloseCard();
        this.playerCardsPlaceOpen = 
                new Point(skin.getPlayerCardsPlaceOpen(num, playerSex));
        //openCard = skin.getOpenCards();
        w1 = 80;
        h1 = 44;
        playerNamePos = skin.getNamePos(num, playerSex);
        x_name = playerNamePos.x;
        y_name = playerNamePos.y;
        w_name = 181;
        h_name = 111;
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
        double amount = 0.0;
        int i = 0;

        for (; i < _roundBetChips.length; i++) {
            _roundBetChips[i].paint(c, g);
            amount += _roundBetChips[i].getMoneyValue();
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial Narrow", Font.PLAIN, 11));

        String amtString = com.agneya.util.Utils.getRoundedString(amount);
        if (i > 0) {
            g.drawString("€" + amtString, _roundBetChips[i - 1].endPos.x - 10, 
                         _roundBetChips[i - 1].endPos.y + 30);
        }
        for (i = 0; i < _player_cards.length; i++) {
            _player_cards[i].paint(c, g);
        }
        //if (_position >= 4) {
            //              Players view
            if (_visibility_state == PLAYER_PLACE_INACCESSIBLE) {
                Graphics gcopy = 
                    g.create(playerPlace.x + 40, playerPlace.y + 55, 32, 32);
                placeInaccessible.paintIcon(c, gcopy, 0, 0);
                gcopy.dispose();
            } else {
                _view.paint(c, g);
            }
        //}

        
    }


        public void setPocketCards(Cards modelCards) {
            PlCard[] newCards = new PlCard[modelCards.size()];
            //_cat.finest("Size=" + modelCards.size());
            for (int i = 0; i < modelCards.size(); i++) {
                _cat.finest("Cards=" + modelCards.getCard(i+1));
                newCards[i] = 
                        new PlCard(new Point(playerCardsPlaceOpen.x, playerCardsPlaceOpen.y), 
                                   /*openCard,*/ closeCard, closeCard, 
                                   modelCards.getCard(i + 1), i, _owner,_skin);
               
                // set the card as open
                newCards[i].refresh();
            }
            _player_cards = newCards;
        }


        public void setPocketCards(Card[] pocket_cards) {
            Cards modelCards = new Cards(false);
            modelCards.addCards(pocket_cards);
            setPocketCards(modelCards);
        }


        public void addPocketCard(Card card) {
            PlCard[] newCards = new PlCard[_player_cards.length + 1];
            System.arraycopy(_player_cards, 0, newCards, 0, _player_cards.length);
            _cat.finest("Add Card PlCards[] " + card.toString());
            newCards[newCards.length - 1] = 
                    new PlCard(new Point(playerCardsPlaceClose.x, 
                                         playerCardsPlaceClose.y), /*openCard, */
                               closeCard, closeCard, card, newCards.length - 1, 
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
            for (int i = 0; i < _player_cards.length; i++) {
                _player_cards[i].refresh();
            }
        }

}
