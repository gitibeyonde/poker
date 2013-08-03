package bap.texas;

import java.util.HashMap;
import java.util.logging.Logger;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import bap.texas.util.Card;
import bap.texas.util.Utils;


/**
 * This class holds graphical resources needed to draw the poker table.
 * It implements the Sigleton pattern.
 */
public abstract class RoomSkin
    implements java.io.Serializable {

	/////////////////////////////
	public int _maxPlayer=9;
	public static int _height;
	public static int _width;

	public static final int BOX_WIDTH = 97;
	public static final int BOX_HEIGHT = 73;
	public static final int AVATAR_WIDTH = 67;
	public static final int AVATAR_HEIGHT = 77;
	public static final int COMM_CARD_WIDTH = 37;
	public static final int COMM_CARD_HEIGHT = 54;
	public static final int CARD_WIDTH = 33;
	public static final int CARD_HEIGHT = 40;
	public static final int POT_WIDTH = 46;
	public static final int POT_HEIGHT = 43;
	public final static int CHIP_WIDTH=18;
	public final static int CHIP_HEIGHT=15;
	public static final int BUBBLE_WIDTH = 87;
	public static final int BUBBLE_HEIGHT = 14;
	public static final int BANG_WIDTH = 20;
	public static final int BANG_HEIGHT = 30;
	public static final int DBUT_WIDTH = 17;
	public static final int DBUT_HEIGHT = 17;
	public static final int SEAT_WIDTH = 50;
	public static final int SEAT_HEIGHT = 50;

	public static int _dx;
	public static int _dy;
	
	public static int _ccx;
	public static int _ccy;
	
	public static int _potx;
	public static int _poty;
	
	public static int _roundx;
	public static int _roundy;

	public static int _winx;
	public static int _winy;
	
	public Point[] playerCoordinates;
	public Point[] coinCoordinates;
	
	public RoomSkin(int mp){
		_maxPlayer = mp;
	}
	
	public void setDimensions(int w, int h){
		
		_height = 320;
		_width = 480;
		_dx =  240;//_width/2;
		_dy = 40;//_height/8;
		_ccx = (int)(_width/2 - COMM_CARD_WIDTH * 2.5 - 6);
		_ccy = _height/2 - COMM_CARD_HEIGHT - 3 ;
		_potx = _width/2 - POT_WIDTH -15;
		_poty = _height/2 + POT_HEIGHT/2 ;
		_roundx = _ccx + 90;
		_roundy = _ccy - 5;
		_winx = _potx - 50;
		_winy = _poty + 30;
	}
	public abstract int getSP(int pos);
	public abstract Point getPlayerCoordinates(int pos);
	public abstract Point getCoinCoordinates(int pos);
	///////////////////////////////////////////
	
 /* static Logger _cat = Logger.getLogger(RoomSkin.class.getName());

  public int _roomSize;
  
  protected  int [][] _pmap;
  protected  int cardConstClose[][];
  protected  int cardConstOpen[][];
  protected  int mansCoords[][];
  protected  int girlsCoords[][];
  protected  int chipsPlaces[][];
  protected  int namesPlaces[][];
  protected  int bublesPlaces[][];
  protected  int dealerChipPlace[][];


  *//** Images *//*
  protected ImageIcon dealerIcon = null;
  protected ImageIcon background = Utils.getIcon("images/board_bg.jpg");
  protected ImageIcon closeCard = null;
  protected ImageIcon studCloseCard = null;
  protected ImageIcon openCard = null;
  protected ImageIcon openColorCard = null;
  protected ImageIcon[][] players = new ImageIcon[10][2];
  protected ImageIcon namePlate;
  protected ImageIcon chips = null;
  protected ImageIcon moveTimer = null;
  protected ImageIcon bubles = null;
  protected ImageIcon speakIcon = null;
  protected ImageIcon placeInaccessibleIcon = null;
  protected ImageIcon bottonPanelBackground = null;
  protected ImageIcon dialogBackground = null;
  
  protected ImageIcon calc;

  *//** Coordinats *//*
  protected Point heapPlace;
  protected Point centerCardsPlace;
  protected Point discardCardsPlace;
  protected Point dialerCardsPlace;
  protected Point[] playerCardsPlacesOpen = new Point[10];
  protected Point[] playerCardsPlacesClose = new Point[10];
  protected Point[][] playersPlace = new Point[10][2];
  protected Point[][] namePlace = new Point[10][2];
  protected Point[] playersBubleCoords = new Point[10];
  protected int[] bublesOrientation = new int[10];
  protected Point[] chipsPlace = new Point[10];
  protected Point[] dialerChip = new Point[10];
  protected Rectangle dialerManPos;
  protected static HashMap smallCards = new HashMap();
  protected static ImageIcon smallCloseCard = Utils.getIcon("images/closedCard.png");

  *//** Bubles orientation constants *//*
  public final int BUBLE_LEFT = 0;
  public final int BUBLE_RIGHT = 1;

  *//** font color for room's labels *//*
  protected Color fontColor;
  
  public RoomSkin(int mp) {
    _roomSize=mp;
    
    dialogBackground = Utils.getIcon("images/moneyreq_dialog.jpg");
    closeCard = Utils.getIcon("images/closedCard.png");
    namePlate = Utils.getIcon("images/plate_main_black.png");
    studCloseCard = Utils.getIcon("images/studCloseCard.png");
    openCard = Utils.getIcon("images/cards_black&red.png");
    openColorCard = Utils.getIcon("images/cards_color.png");
    bubles = Utils.getIcon("images/bubbles.png");
    speakIcon = Utils.getIcon("images/speak.gif");
    placeInaccessibleIcon = Utils.getIcon("images/placeInaccessible.gif");
    bottonPanelBackground = Utils.getIcon("images/bg_copy.png");
    calc = Utils.getIcon("images/calc.png");
    
    chips = Utils.getIcon("images/my_chips.png");
    moveTimer = Utils.getIcon("images/timer.png");
    dealerIcon = Utils.getIcon("images/my_chip_dealer.png");

  }

  public final ImageIcon getBoard() {
    return (background);
  }

	public final static ImageIcon getSmallCardImage(Card c){
	    return (ImageIcon)smallCards.get(c.toString());
	}
    
    public final static ImageIcon getSmallCloseCardImage(){
        return (ImageIcon)smallCloseCard;
    }

  public final ImageIcon getCloseCard() {
    return (closeCard);
  }

  public final ImageIcon getStudCloseCard() {
    return (studCloseCard);
  }

  public final ImageIcon getThisPlayersSkin() {
	  ImageIcon icon = null;
	  if(ServerProxy._avatar != null && !ServerProxy._avatar.equals(""))
	  {
		  if(ServerProxy._avatar.length() < 3)ServerProxy._avatar += ".png" ;
	  }
	  else ServerProxy._avatar = "0.png";
	  try {
		icon = Utils.getIcon("images/avatars/"+ServerProxy._avatar);
	  } 
	  catch (NullPointerException e) {
		return Utils.getIcon("images/avatars/0.png");
	}
	  return icon;
  }
  
  public final ImageIcon getPlayersSkin(String avatar) {
	  ImageIcon icon = null;
	  if(avatar != null && !avatar.equals(""))
	  {
		  if(avatar.length() < 3)avatar += ".png" ;
	  }
	  else avatar = "0.png";
	  try {
			icon = Utils.getIcon("images/avatars/"+avatar);
	  } 
	  catch (NullPointerException e) {
		return Utils.getIcon("images/avatars/0.png");
	  }
	  return icon;
  }

  public final ImageIcon getOpenCards() {
	return ServerProxy._settings.isFourColorCards()?openColorCard:openCard;
  }

  public final ImageIcon getChips() {
    return (chips);
  }
  
  public final ImageIcon getNamePlate(){
      return namePlate;
  }
  

  public final ImageIcon getMoveTimer() {
    return (moveTimer);
  }

  public final Point getHeapPlace() {
    return (heapPlace);
  }

  public final Point getCenterCardsPlace() {
    return (centerCardsPlace);
  }

  public final Point getDiscardCardsPlace() {
    return (discardCardsPlace);
  }
  
  public final Point getDialerCardsPlace() {
    return (dialerCardsPlace);
  }


  public final ImageIcon getBublesIcon() {
    return (bubles);
  }

  public final ImageIcon getSpeakIcon() {
    return (speakIcon);
  }

  public final ImageIcon getPlaceInaccessibleIcon() {
    return (placeInaccessibleIcon);
  }

  public final ImageIcon getBottonPanelBackground() {
    return (bottonPanelBackground);
  }

  public final ImageIcon getDialogBackground() {
    return (dialogBackground);
  }
     
  public final Rectangle getDealerRect() {
    return dialerManPos;
  }

  public final ImageIcon getCalc(){
      return calc;
  }
  

  public final Color getFontColor() {
    return fontColor;
  }

  public final ImageIcon getDealerIcon() {
    return dealerIcon;
  }

  
  public abstract Point getPlayerCardsPlaceOpen(int ps, char sex);

  public abstract Point getPlayerCardsPlaceClose(int ps, char sex);

 public abstract Point getPlayerPlace(int ps, char sex);

  public abstract Point getChipsPlace(int ps, char sex);

  public abstract Point getPlayersBublesCoords(int ps, char sex);

  public abstract Point getNamePos(int ps, char sex);
    
  public abstract int getBublesOrientation(int ps);

  public abstract Point getDealerPos(int ps);

  public abstract Point getOpenCardPos(int ps);
  
 
 */
  
}
