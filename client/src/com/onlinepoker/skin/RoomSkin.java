package com.onlinepoker.skin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.golconda.game.util.Card;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.server.ServerProxy;


/**
 * This class holds graphical resources needed to draw the poker table.
 * It implements the Sigleton pattern.
 */
public abstract class RoomSkin
    implements java.io.Serializable {

  static Logger _cat = Logger.getLogger(RoomSkin.class.getName());
  boolean isFourcolorcards = ServerProxy._settings.isFourColorCards();//?openColorCard:openCard;
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
  //by rk
  protected  int playerNotePlace[][];
  
  /** Images */
  protected ImageIcon dealerIcon = Utils.getIcon("images/my_chip_dealer.png");
  protected ImageIcon background = Utils.getIcon("images/common/board_bg.jpg");
  protected ImageIcon closeCard = Utils.getIcon("images/common/closedCard.png");
  protected ImageIcon studCloseCard = Utils.getIcon("images/common/studCloseCard.png");
  //protected ImageIcon openCard = Utils.getIcon("images/cards_black&red.png");
  //protected ImageIcon openColorCard = Utils.getIcon("images/cards_color.png");
  protected ImageIcon[][] players = new ImageIcon[10][2];
  protected ImageIcon namePlate = Utils.getIcon("images/plate_main_black.png");
  protected ImageIcon chips = Utils.getIcon("images/my_chips.png");
  protected ImageIcon moveTimer = Utils.getIcon("images/timer.png");
  protected ImageIcon bubles = null;
  protected ImageIcon speakIcon = Utils.getIcon("images/speak.gif");
  protected ImageIcon placeInaccessibleIcon = Utils.getIcon("images/placeInaccessible.gif");
  protected ImageIcon bottonPanelBackground = Utils.getIcon("images/bg_copy.png");
  protected ImageIcon dialogBackground = Utils.getIcon("images/moneyreq_dialog.jpg");
  //by rk 
  protected ImageIcon disconnected_plate = Utils.getIcon("images/plate_disconnected.png");
  protected ImageIcon reconnecting_plate_on = Utils.getIcon("images/plate_connecting.png");
  protected ImageIcon reconnecting_plate_off = Utils.getIcon("images/plate_connecting_off.png");
  protected ImageIcon icon_on = Utils.getIcon(ClientConfig.COUNTER_BG_ON);
  protected ImageIcon icon_off = Utils.getIcon(ClientConfig.COUNTER_BG_OFF);
  protected ImageIcon moveButton = Utils.getIcon(ClientConfig.IMG_BUTTON_EN);
  protected ImageIcon checkBox_en = Utils.getIcon(ClientConfig.IMG_CHECK_EN);
  protected ImageIcon checkBox_de = Utils.getIcon(ClientConfig.IMG_CHECK_DE);
  
  public double roomWidth=800, roomHeight=626;
  
  
  //resize code
   StringBuilder sb = new StringBuilder("images/avatars/").append(ServerProxy._avatar).append(".png");
   protected ImageIcon _avatar = Utils.getIcon(sb.toString());//Utils.getIcon("images/avatars/0.png");
	//sb = null;//by rk, for GC
  
  protected ImageIcon calc;

  /** Coordinats */
  protected Point heapPlace = new Point(325, 310);
  protected Point centerCardsPlace = new Point(250, 210);
  protected Point dialerCardsPlace = new Point(380, 175);
  protected Point discardCardsPlace = new Point(285, 220);
  
  protected Point[] playerCardsPlacesOpen = new Point[10];
  protected Point[] playerCardsPlacesClose = new Point[10];
  protected Point[][] playersPlace = new Point[10][2];
  protected Point[][] namePlace = new Point[10][2];
  protected Point[] playersBubleCoords = new Point[10];
  protected int[] bublesOrientation = new int[10];
  protected Point[] chipsPlace = new Point[10];
  protected Point[] dialerChip = new Point[10];
  protected Rectangle dialerManPos;
//by rk
  protected Point[] plrNotePlace = new Point[10];
  protected Point namePosition = new Point(45,44);
  protected Point notePosition = new Point(8,40);
  protected Point cityPosition = new Point(142,53);
  protected Point chipsPosition = new Point(150,79);
  protected Point statusPosition = new Point(85,79);
  protected Point menuSize = new Point(800,50);
  
  protected Point tourneyInfo = new Point(25,15);//getchips
  protected Point lastHand = new Point(125,15);//Last hand
  protected Point options = new Point(225,15);//Options
  protected Point stats = new Point(325,15);//Stats
  protected Point rebuy = new Point(425,15);
  protected Point lobby = new Point(550,15);
  protected Point exit = new Point(750,15);
  protected Point chipsMenu = new Point(650,15);
  protected Point handIdLabelSize = new Point(800,20);
  protected Point handIdLabelPosion = new Point(645,15);
  
  protected static ConcurrentHashMap smallCards = new ConcurrentHashMap();

  /** Bubles orientation constants */
  public final int BUBLE_LEFT = 0;
  public final int BUBLE_RIGHT = 1;

  /** font color for room's labels */
  protected Color fontColor;
  
  //resize code
  private ImageIcon playerIcon = null;
  private ImageIcon namePlateActive = Utils.getIcon("images/plate_active.png");
  private ImageIcon namePlateDisconnected = Utils.getIcon("images/plate_disconnected.png");
  private ImageIcon namePlateSmallBlind = Utils.getIcon("images/plate_small_blind.png");
  private ImageIcon namePlateBigBlind = Utils.getIcon("images/plate_big_blind.png");
  private ImageIcon namePlateAnte = Utils.getIcon("images/plate_ante.png");
  private ImageIcon namePlateCheck = Utils.getIcon("images/plate_check.png");
  private ImageIcon namePlateCall = Utils.getIcon("images/plate_call.png");
  private ImageIcon namePlateBet = Utils.getIcon("images/plate_bet.png");
  private ImageIcon namePlateRaise = Utils.getIcon("images/plate_raise.png");
  private ImageIcon namePlateFold = Utils.getIcon("images/plate_fold.png");
  private ImageIcon namePlateAllIn = Utils.getIcon("images/plate_all_in.png");
  private ImageIcon namePlateReserved = Utils.getIcon("images/plate_reserved.png");
  private ImageIcon namePlateTime = Utils.getIcon("images/plate_time.png");
  private ImageIcon namePlateTimeOut = Utils.getIcon("images/plate_time_out.png");
  //private ImageIcon bublesIcon = null;
  private ImageIcon noteIconEmpty = Utils.getIcon("images/note-empty.png");
  private ImageIcon noteIconPresent = Utils.getIcon("images/note-present.png");
  public ImageIcon imageLobbyMenuBarBG = Utils.getIcon("images/menubar_bg.png");
  
  protected static ImageIcon smallCloseCard = Utils.getIcon("images/common/closedCard.png");
  
  
  public double _ratio_x=1;
  public double _ratio_y=1;
  
  public static final int CHIP_WIDTH = 22;
  public static final int CHIP_HEIGHT = 22;
  
  public static final int CYCLE_COUNT = 40;
  public static final int CARD_CLOSE_WIDTH = 37;
  public static final int CARD_CLOSE_HEIGHT = 54;
  
  public static final int CARD_OPEN_WIDTH = 54;
  public static final int CARD_OPEN_HEIGHT = 78;
  public static final int CARD_EXPOSED_WIDTH = 40;
  public static final int CARD_GAP = 1;
  
  public static final int NAME_PLATE_WIDTH=181;
  public static final int NAME_PLATE_HEIGHT=111;
  
  public static final int TIMER_WIDTH = 101;
  public static final int TIMER_HEIGHT = 8;
	
  public static final int DEALER_CHIP_WIDTH = 34;
  public static final int DEALER_CHIP_HEIGHT = 28;
  
  public final int ROOM_WIDTH = 800;
  public final int ROOM_HEIGHT = 626;
  
  
  
  public int chipWidth = CHIP_WIDTH;
  public int chipHeight = CHIP_HEIGHT;
  public int cycleCount = CYCLE_COUNT;
  public int cardCloseWidth = CARD_CLOSE_WIDTH;
  public int cardCloseHeight = CARD_CLOSE_HEIGHT;
  public int cardOpenWidth = CARD_OPEN_WIDTH;
  public int cardOpenHeight = CARD_OPEN_HEIGHT;
  public int cardExposedWidth = CARD_EXPOSED_WIDTH;
  public int cardGap = CARD_GAP;
  public int namePlateWidth = NAME_PLATE_WIDTH;
  public int namePlateHeight = NAME_PLATE_HEIGHT;
  public int timerWidth = TIMER_WIDTH;
  public int timerHeight = TIMER_HEIGHT;
  public int dealerChipWidth = DEALER_CHIP_WIDTH;
  public int dealerChipHeight = DEALER_CHIP_HEIGHT;
  
  //double curr_ratio_x, curr_ratio_y;
  //ReSizeImage _rsi;
  
  int startx = 5, w1 = 80, w2 = 400 + 90 + 35 - 10 - 20, w3 = 225 + 20;
  int starty = 10, he = 120;
  
  public RoomSkin(int mp) {
	  
	//System.out.println("RoomSkin(mp) constructor "+_ratio_x+", "+_ratio_y);
	//new Exception(this+"").printStackTrace();
	_roomSize=mp;
//    background = Utils.getIcon("images/common/board_bg.jpg");
//    dialogBackground = Utils.getIcon("images/moneyreq_dialog.jpg");
//    closeCard = Utils.getIcon("images/common/closedCard.png");
//    namePlate = Utils.getIcon("images/plate_main_black.png");
//    studCloseCard = Utils.getIcon("images/common/studCloseCard.png");
//    openCard = Utils.getIcon("images/cards_black&red.png");
//    openColorCard = Utils.getIcon("images/cards_color.png");
//    speakIcon = Utils.getIcon("images/speak.gif");
//    placeInaccessibleIcon = Utils.getIcon("images/placeInaccessible.gif");
//    bottonPanelBackground = Utils.getIcon("images/bg_copy.png");
//    chips = Utils.getIcon("images/my_chips.png");
//    moveTimer = Utils.getIcon("images/timer.png");
//    dealerIcon = Utils.getIcon("images/my_chip_dealer.png");
//    disconnected_plate = Utils.getIcon("images/plate_disconnected.png");
//    reconnecting_plate_on = Utils.getIcon("images/plate_connecting.png");
//    reconnecting_plate_off = Utils.getIcon("images/plate_connecting_off.png");
//    icon_on = Utils.getIcon(ClientConfig.COUNTER_BG_ON);
//    icon_off = Utils.getIcon(ClientConfig.COUNTER_BG_OFF);
//    StringBuilder sb = new StringBuilder("images/avatars/").append(ServerProxy._avatar).append(".png");
//    _avatar = Utils.getIcon(sb.toString());//Utils.getIcon("images/avatars/0.png");
//    			sb = null;//by rk, for GC
//    imageLobbyMenuBarBG = Utils.getIcon("images/menubar_bg.png");
//    namePlateDisconnected = Utils.getIcon("images/plate_disconnected.png");
//    namePlateActive = Utils.getIcon("images/plate_active.png");
//    namePlateSmallBlind = Utils.getIcon("images/plate_small_blind.png");
//    namePlateBigBlind = Utils.getIcon("images/plate_big_blind.png");
//    namePlateCheck = Utils.getIcon("images/plate_check.png");
//    namePlateCall = Utils.getIcon("images/plate_call.png");
//    namePlateBet = Utils.getIcon("images/plate_bet.png");
//    namePlateRaise = Utils.getIcon("images/plate_raise.png");
//    namePlateFold = Utils.getIcon("images/plate_fold.png");
//    namePlateAllIn = Utils.getIcon("images/plate_all_in.png");
//    namePlateAnte = Utils.getIcon("images/plate_ante.png");
//    namePlateTime = Utils.getIcon("images/plate_time.png");
//    namePlateTimeOut = Utils.getIcon("images/plate_time_out.png");
//    namePlateReserved = Utils.getIcon("images/plate_reserved.png");
//    noteIconEmpty = Utils.getIcon("images/note-empty.png");
//    noteIconPresent = Utils.getIcon("images/note-present.png");
//    smallCloseCard = Utils.getIcon("images/common/closedCard.png");
//    moveButton = Utils.getIcon(ClientConfig.IMG_BUTTON_EN);
    //bubles = Utils.getIcon("images/bubbles.png");
    //calc = Utils.getIcon("images/calc.png");

//    chipWidth = CHIP_WIDTH;
//    chipHeight = CHIP_HEIGHT;
//    cycleCount = CYCLE_COUNT;
//    cardCloseWidth = CARD_CLOSE_WIDTH;
//    cardCloseHeight = CARD_CLOSE_HEIGHT;
//    cardOpenWidth = CARD_OPEN_WIDTH;
//    cardOpenHeight = CARD_OPEN_HEIGHT;
//    cardExposedWidth = CARD_EXPOSED_WIDTH;
//    cardGap = CARD_GAP;
//    namePlateWidth = NAME_PLATE_WIDTH;
//    namePlateHeight = NAME_PLATE_HEIGHT;
//    timerWidth = TIMER_WIDTH;
//    timerHeight = TIMER_HEIGHT;
//    dealerChipWidth = DEALER_CHIP_WIDTH;
//    dealerChipHeight = DEALER_CHIP_HEIGHT;
    
//    namePosition = new Point(45,44);
//	notePosition = new Point(8,40);
//	cityPosition = new Point(142,53);
//	chipsPosition = new Point(150,79);
//	statusPosition = new Point(85,79);
//	menuSize = new Point(700,50);
//	tourneyInfo = new Point(25,15);
//	lastHand = new Point(125,15);
//    options = new Point(225,15);
//    stats = new Point(325,15);
//    rebuy = new Point(425,15);
//    lobby = new Point(650,15);
//    chipsMenu = new Point(650,15);
//    handIdLabelSize = new Point(800,20);
//    handIdLabel = new Point(645,15);
    // _rsi= new ReSizeImage();
  }
  
  
//  //resize code
//  public class ReSizeImage extends Thread{
//	  ImageIcon imageicon;
//	  BufferedImage resizedImage;
//	  public void reSizeImage(ImageIcon imageicon){
//		  this.imageicon = imageicon;
//		  start();
//	  }
//	  @Override
//	  public void run(){
//		  resizedImage = new BufferedImage(
//				  	(int)(imageicon.getIconWidth()*_ratio_x),
//				  		(int) (imageicon.getIconHeight()*_ratio_y),
//				  			BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g = resizedImage.createGraphics();
//			g.drawImage(imageicon.getImage(), 0, 0, (int)(imageicon.getIconWidth()*_ratio_x),(int) (imageicon.getIconHeight()*_ratio_y), null);
//			g.dispose();
//			
//			send();
//	  }
//	  public ImageIcon send(){
//	  return new ImageIcon(resizedImage);
//	  }
//	  
//  }
  public  ImageIcon reSizeImage(ImageIcon imageicon){
	  BufferedImage resizedImage = new BufferedImage(
											  	(int)(imageicon.getIconWidth()*_ratio_x),
											  		(int) (imageicon.getIconHeight()*_ratio_y),
											  			BufferedImage.TYPE_INT_ARGB);
	  
	  //BufferedImage resizedImage1 = new BufferedImage(imageicon.getIconWidth(),imageicon.getIconHeight(),
	  //										  			BufferedImage.TYPE_INT_ARGB);
	  //BufferedImage resizedImage = resizeTrick(resizedImage1,(int)(imageicon.getIconWidth()*_ratio_x), (int) (imageicon.getIconHeight()*_ratio_y));
	  
		  Graphics2D g = resizedImage.createGraphics();
		  g.drawImage(imageicon.getImage(), 0, 0, (int)(imageicon.getIconWidth()*_ratio_x),(int) (imageicon.getIconHeight()*_ratio_y), null);
		  g.dispose();
		  return new ImageIcon(resizedImage);
  }
  
  public ImageIcon getScaledImage(ImageIcon imageicon){
	  Image image = imageicon.getImage();
	  /*Image */image = image.getScaledInstance((int)(imageicon.getIconWidth()*_ratio_x), (int) (imageicon.getIconHeight()*_ratio_y), 
				Image.SCALE_SMOOTH);
	  return new ImageIcon(image);
  }
  
  public ImageIcon getScaledImageFast(ImageIcon imageicon){
	  Image image = imageicon.getImage();
	  /*Image */image = image.getScaledInstance((int)(imageicon.getIconWidth()*_ratio_x), (int) (imageicon.getIconHeight()*_ratio_y), 
				Image.SCALE_FAST);
	  return new ImageIcon(image);
  }
    
  /**private static BufferedImage resize(BufferedImage image, int width, int height) {
	  BufferedImage resizedImage = new BufferedImage(width, height,
	  BufferedImage.TYPE_INT_ARGB);
	  Graphics2D g = resizedImage.createGraphics();
	  g.drawImage(image, 0, 0, width, height, null);
	  g.dispose();
	  return resizedImage;
  } 
  private static BufferedImage createCompatibleImage(BufferedImage image) {
	  //GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);
	  int w = image.getWidth();
	  int h = image.getHeight();
	  BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
	  Graphics2D g2 = result.createGraphics();
	  g2.drawRenderedImage(image, null);
	  g2.dispose();
	  return result;
	  } 
  private static BufferedImage resizeTrick(BufferedImage image, int width, int height) {
	  image = createCompatibleImage(image);
	  image = resize(image, image.getWidth(), image.getHeight());
	  image = blurImage(image);
	  image = resize(image, width, height);
	  return image;
  } 
  public static BufferedImage blurImage(BufferedImage image) {
	  float ninth = 1.0f/9.0f;
	  float[] blurKernel = {
	  ninth, ninth, ninth,
	  ninth, ninth, ninth,
	  ninth, ninth, ninth
	  };

	  Map map = new HashMap();

	  map.put(RenderingHints.KEY_INTERPOLATION,
	  RenderingHints.VALUE_INTERPOLATION_BILINEAR);

	  map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

	  map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	  RenderingHints hints = new RenderingHints(map);
	  BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
	  return op.filter(image, null);
  }*/
  
  /*public  ImageIcon plateReSizeImage(ImageIcon imageicon){
	  BufferedImage resizedImage = new BufferedImage(
			  	(int)(imageicon.getIconWidth()*_ratio_x),
			  		(int) (imageicon.getIconHeight()*_ratio_y),
			  			BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(imageicon.getImage(), 0, 0, (int)(imageicon.getIconWidth()*_ratio_x),(int) (imageicon.getIconHeight()*_ratio_y), null);
		g.dispose();
		
		return new ImageIcon(resizedImage);
				 Image img = imageicon.getImage();  
			     Image newimg = img.getScaledInstance((int)(imageicon.getIconWidth()*_ratio_x),(int) (imageicon.getIconHeight()*_ratio_y),  java.awt.Image.SCALE_FAST);  
			     imageicon = new ImageIcon(newimg);
			     return imageicon;
	  
	  
//		Scale f = new Scale(imageicon);
//		Image image = f.go();
//		return new ImageIcon(image);
		
		//find details------> g.setComposite(AlphaComposite.Src);

		*//****temp code for test
		BufferedImage img = new BufferedImage(imageicon.getIconWidth(), imageicon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = img.createGraphics();  
		tg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
		tg.drawImage(img, 0, 0, (int)(imageicon.getIconWidth()*_ratio_x), (int) (imageicon.getIconHeight()*_ratio_y), null);  
		tg.dispose();  
		return new ImageIcon(img); *//* 
	}*/
  /*****public  ImageIcon plateReSizeImage2(ImageIcon imageicon){
		BufferedImage resizedImage = new BufferedImage(
										  	(int)(imageicon.getIconWidth()*_ratio_x),
										  		(int) (imageicon.getIconHeight()*_ratio_y),
										  			BufferedImage.BITMASK);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(imageicon.getImage(), 0, 0, (int)(imageicon.getIconWidth()*_ratio_x),(int) (imageicon.getIconHeight()*_ratio_y), null);
		g.dispose();
		
		return new ImageIcon(resizedImage);
	 -Image img = imageicon.getImage();  
   Image newimg = img.getScaledInstance((int)(imageicon.getIconWidth()*_ratio_x),(int) (imageicon.getIconHeight()*_ratio_y),  java.awt.Image.SCALE_FAST);  
   imageicon = new ImageIcon(newimg);
   return imageicon;
   
  }*/
   
  public void setResizeRatio(double x,double y){
	//System.out.println("RoomSkin.setResizeRatio "+", "+x+", "+y);
	this._ratio_x = x; 
	this._ratio_y = y;
	this.roomWidth = (int)(ROOM_WIDTH*_ratio_x);
	this.roomHeight = (int)(ROOM_HEIGHT*_ratio_y);
    dialogBackground = reSizeImage(Utils.getIcon("images/moneyreq_dialog.jpg"));
    background = getScaledImage(Utils.getIcon("images/common/board_bg.jpg"));
    closeCard = reSizeImage(Utils.getIcon("images/common/closedCard.png"));
    namePlate = getScaledImage(Utils.getIcon("images/plate_main_black.png"));
    studCloseCard = reSizeImage(Utils.getIcon("images/common/studCloseCard.png"));
    //openCard = reSizeImage(openCard);
    //openColorCard = reSizeImage(openColorCard);
    speakIcon = reSizeImage(Utils.getIcon("images/speak.gif"));
    placeInaccessibleIcon = reSizeImage(Utils.getIcon("images/placeInaccessible.gif"));
    bottonPanelBackground = reSizeImage(Utils.getIcon("images/bg_copy.png"));
    //chips = reSizeImage(chips);
    //moveTimer = reSizeImage(moveTimer);
    dealerIcon = getScaledImage(Utils.getIcon("images/my_chip_dealer.png"));
    disconnected_plate = getScaledImage(Utils.getIcon("images/plate_disconnected.png"));
    reconnecting_plate_on = getScaledImage(Utils.getIcon("images/plate_connecting.png"));
    reconnecting_plate_off = getScaledImage(Utils.getIcon("images/plate_connecting_off.png"));
    icon_on = getScaledImage(Utils.getIcon(ClientConfig.COUNTER_BG_ON));
    icon_off = Utils.getIcon(ClientConfig.COUNTER_BG_OFF);
    StringBuilder sb = new StringBuilder("images/avatars/").append(ServerProxy._avatar).append(".png");
    _avatar = getScaledImage(Utils.getIcon(sb.toString()));
    sb = null;//by rk, for GC
    imageLobbyMenuBarBG = reSizeImage(Utils.getIcon("images/menubar_bg.png"));
    namePlateDisconnected = getScaledImage(Utils.getIcon("images/plate_disconnected.png"));
    namePlateActive = getScaledImage(Utils.getIcon("images/plate_active.png"));
    namePlateSmallBlind = getScaledImage(Utils.getIcon("images/plate_small_blind.png"));
    namePlateBigBlind = getScaledImage(Utils.getIcon("images/plate_big_blind.png"));
    namePlateCheck = getScaledImage(Utils.getIcon("images/plate_check.png"));
    namePlateCall = getScaledImage(Utils.getIcon("images/plate_call.png"));
    namePlateBet = getScaledImage(Utils.getIcon("images/plate_bet.png"));
    namePlateRaise = getScaledImage(Utils.getIcon("images/plate_raise.png"));
    namePlateFold = getScaledImage(Utils.getIcon("images/plate_fold.png"));
    namePlateAllIn = getScaledImage(Utils.getIcon("images/plate_all_in.png"));
    namePlateAnte = getScaledImage(Utils.getIcon("images/plate_ante.png"));
    namePlateTime = getScaledImage(Utils.getIcon("images/plate_time.png"));
    namePlateTimeOut = getScaledImage(Utils.getIcon("images/plate_time_out.png"));
    namePlateReserved = getScaledImage(Utils.getIcon("images/plate_reserved.png"));
    noteIconEmpty = getScaledImage(Utils.getIcon("images/note-empty.png"));
    noteIconPresent = getScaledImage(Utils.getIcon("images/note-present.png"));
    smallCloseCard = reSizeImage(Utils.getIcon("images/common/closedCard.png"));
    moveButton = reSizeImage(Utils.getIcon(ClientConfig.IMG_BUTTON_EN));
    checkBox_en = getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
    checkBox_de = getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
    
    chipWidth = (int)(CHIP_WIDTH*_ratio_x);
    chipHeight = (int)(CHIP_HEIGHT*_ratio_y);
    cardCloseWidth = (int)(CARD_CLOSE_WIDTH*_ratio_x);
    cardCloseHeight = (int)(CARD_CLOSE_HEIGHT*_ratio_y);
    cardOpenWidth = (int)((CARD_OPEN_WIDTH+0.3)*_ratio_x);
    cardOpenHeight = (int)(CARD_OPEN_HEIGHT*_ratio_y);
    cardExposedWidth = (int)(CARD_EXPOSED_WIDTH*_ratio_x);
    cardGap = (int)((CARD_GAP+0.2)*_ratio_x);
    namePlateWidth = (int)(NAME_PLATE_WIDTH*_ratio_x);
    namePlateHeight = (int)(NAME_PLATE_HEIGHT*_ratio_y);
    timerWidth = (int)(TIMER_WIDTH*_ratio_x);
    timerHeight = (int)(TIMER_HEIGHT*_ratio_y);
    dealerChipWidth = (int)(DEALER_CHIP_WIDTH*_ratio_x);
    dealerChipHeight = (int)(DEALER_CHIP_HEIGHT*_ratio_y);
    
    namePosition = new Point((int)(45*_ratio_x),(int)(44*_ratio_y));
	notePosition = new Point((int)(8*_ratio_x),(int)(40*_ratio_y));
	cityPosition = new Point((int)(142*_ratio_x),(int)(53*_ratio_y));
	chipsPosition = new Point((int)(150*_ratio_x),(int)(79*_ratio_y));
	statusPosition = new Point((int)(85*_ratio_x),(int)(79*_ratio_y));
	menuSize = new Point((int)(800*_ratio_x),(int)(50*_ratio_y));
	tourneyInfo = new Point((int)(25*_ratio_x),(int)(15*_ratio_y));
	lastHand = new Point((int)(125*_ratio_x),(int)(15*_ratio_y));
    options = new Point((int)(225*_ratio_x),(int)(15*_ratio_y));
    stats = new Point((int)(325*_ratio_x),(int)(15*_ratio_y));
    rebuy = new Point((int)(425*_ratio_x),(int)(15*_ratio_y));
    lobby = new Point((int)(550*_ratio_x),(int)(15*_ratio_y));
    exit = new Point((int)(750*_ratio_x),(int)(15*_ratio_y));
    chipsMenu = new Point((int)(650*_ratio_x),(int)(15*_ratio_y));
    handIdLabelSize = new Point((int)(800*_ratio_x),(int)(20*_ratio_y));
    handIdLabelPosion = new Point((int)(625*_ratio_x),(int)(15*_ratio_y));
    
    heapPlace = new Point((int)(325*_ratio_x), (int)(310*_ratio_y));
    centerCardsPlace = new Point((int)(250*_ratio_x), (int)(210*_ratio_y));
    dialerCardsPlace = new Point((int)(380*_ratio_x), (int)(175*_ratio_y));
    discardCardsPlace = new Point((int)(285*_ratio_x), (int)(220*_ratio_y));
    
    
  }
  
  public int getCycleCount() {
	  return cycleCount;
  }
  public int getChipWidth() {
		return chipWidth;
	}
	public int getChipHeight() {
		return chipHeight;
	}
	public int getCardCloseWidth() {
		return cardCloseWidth;
	}
	public int getCardCloseHeight() {
		return cardCloseHeight;
	}
	public int getCardOpenWidth() {
		return cardOpenWidth;
	}
	public int getCardOpenHeight() {
		return cardOpenHeight;
	}
	public int getCardExposedWidth() {
		return cardExposedWidth;
	}
	public int getCardGap() {
		return cardGap;
	}
	public int getNamePlateWidth() {
		return namePlateWidth;
	}
	public int getNamePlateHeight() {
		return namePlateHeight;
	}
	public int getTimerWidth() {
		return timerWidth;
	}
	public int getTimerHeight() {
		return timerHeight;
	}
	public int getDealerChipWidth() {
		return dealerChipWidth;
	}
	public int getDealerChipHeight() {
		return dealerChipHeight;
	}


	 public final Point getHeapPlace() {
		  return (heapPlace);
	  }
	  public final int getPotLength(int potlen) {
		  return (potlen*(int)(50*_ratio_y));
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

  public ImageIcon getNamePlateActive() {
	  return  namePlateActive;
  }
  public ImageIcon getNamePlateDisconnected() {
	  return namePlateDisconnected;
  }
  public ImageIcon getNamePlateSmallBlind() {
	  return namePlateSmallBlind;
  }
  public ImageIcon getNamePlateBigBlind() {
	  return namePlateBigBlind;
  }
  public ImageIcon getNamePlateAnte() {
	  return namePlateAnte;
  }
  public ImageIcon getNamePlateCheck() {
	  return namePlateCheck;
  }
  public ImageIcon getNamePlateCall() {
	  return namePlateCall;
  }
  public ImageIcon getNamePlateBet() {
	  return namePlateBet;
  }
  public ImageIcon getNamePlateRaise() {
	  return namePlateRaise;
  }
  public ImageIcon getNamePlateFold() {
	  return namePlateFold;
  }
  public ImageIcon getNamePlateAllIn() {
	  return namePlateAllIn;
  }
  public ImageIcon getNamePlateReserved() {
	  return namePlateReserved;
  }
  public ImageIcon getNamePlateTime() {
	  return namePlateTime;
  }
  public ImageIcon getNamePlateTimeOut() {
	  return namePlateTimeOut;
  }
  public ImageIcon getNoteIconEmptyImg() {
	  return noteIconEmpty;
  }
  public ImageIcon getNoteIconPresentImg() {
	  return noteIconPresent;
  }
  public ImageIcon getAvatar(){
	  return _avatar;
  }
  public final ImageIcon getBoard() {
      return background;
  }
  public final ImageIcon getCloseCard() {
      return closeCard;
  }
  public final static ImageIcon getSmallCardImage(Card c){
    return (ImageIcon)smallCards.get(c.toString());
  }
  public final static ImageIcon getSmallCloseCardImage(){
    return (ImageIcon)smallCloseCard;
  }
  public final ImageIcon getStudCloseCard() {
	  return (studCloseCard);
  }
  public final ImageIcon getThisPlayersSkin() {
	  return this._avatar;
	  //new Exception("getPlayersSkin() "+_avatar).printStackTrace();
	  
	  /*ImageIcon icon = null;
	  if(ServerProxy._avatar != null && !ServerProxy._avatar.equals(""))
	  {
		  if(ServerProxy._avatar.length() < 3)ServerProxy._avatar += ".png" ;
	  }
	  else ServerProxy._avatar = "1.png";
	  try {
		icon = Utils.getIcon("images/avatars/"+ServerProxy._avatar);
	  } 
	  catch (NullPointerException e) {
		return Utils.getIcon("images/avatars/1.png");
	}
	  new Exception("getPlayersSkin() "+icon).printStackTrace();
		 
	  return icon;*/
  }
  public final ImageIcon getPlayersSkin(String avatar) {
	  ImageIcon icon = null;
//	  if(avatar != null && !avatar.equals(""))
//	  {
//		  if(avatar.length() < 3)avatar += ".png" ;
//	  }
//	  else avatar = "1.png";
	  try {
		  if(avatar != null && !avatar.equals("") && !avatar.equals("null"))
		  {
			  if(avatar.length() < 3){
				  StringBuilder sb = new StringBuilder("images/avatars/").append(avatar).append(".png");
				  icon = Utils.getIcon(sb.toString());
				  sb = null;//by rk, for GC
			  }
		  }
		  else{
			  icon = Utils.getIcon("images/avatars/1.png");
			  //avatar = "1.png";
		  }
//			icon = Utils.getIcon("images/avatars/"+avatar);
	  } 
	  catch (NullPointerException e) {
		return getScaledImage(Utils.getIcon("images/avatars/1.png"));
	  }
	  //new Exception("getPlayersSkin(avtr) "+icon).printStackTrace();
	  //return icon;
	  return getScaledImage(icon);
  }
  /*public final ImageIcon getOpenCards() {
	try {
		return ServerProxy._settings.isFourColorCards()?openColorCard:openCard;
		// making normal cards default as per client requirement 
		//return ServerProxy._settings.isFourColorCards()?openCard:openColorCard;
	} catch (Exception e) {
		return openCard;
	}
  }*/
  public final ImageIcon getChips() {
	  return chips;
  }
  public final ImageIcon getNamePlate(){
	  return namePlate;
  }
  public final ImageIcon getMoveTimer() {
	  return moveTimer;
  }
  public final ImageIcon getDisconnectedPlate() {
	  return disconnected_plate;
  }
  public final ImageIcon getReconnecting_plate_on() {
	  return reconnecting_plate_on;
  }
  public final ImageIcon getReconnecting_plate_off() {
	  return reconnecting_plate_off;
  }
  public final ImageIcon getIcon_on() {
	  return icon_on;
  }
  public final ImageIcon getIcon_off() {
	  return icon_off;
  }
  
  public final ImageIcon getBublesIcon() {
    return bubles;
  }
  public final ImageIcon getSpeakIcon() {
    return speakIcon;
  }
  public final ImageIcon getPlaceInaccessibleIcon() {
    return placeInaccessibleIcon;
  }
  public final ImageIcon getBottonPanelBackground() {
    return bottonPanelBackground;
  }
  public final ImageIcon getDialogBackground() {
    return dialogBackground;
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
  public final ImageIcon getImageLobbyMenuBarBG(){
	  return imageLobbyMenuBarBG;
  }
  
   public ImageIcon getMoveButton(){
	  return moveButton;
   }
   public ImageIcon getCheckBoxEn(){
	   return checkBox_en;
   }
   public ImageIcon getCheckBoxDe(){
	   return checkBox_de;
   }
	public Point getNamePosition(){
		return namePosition;
	}
	public Point getNotePosition(){
		return notePosition;
	}
	public Point getCityPosition(){
		return cityPosition;
	}
	public Point getChipsPosition(){
		return chipsPosition;
	}
	public Point getStatusPosition(){
		return statusPosition;
	}
	
	public Point getMenuSize() {
		return menuSize;
	}
	public Point getTourneyInFo(){
		return tourneyInfo;
	}
    public Point getLastHand(){
		return lastHand;
    }
    public Point getOptions(){
		return options;
    }
    public Point getStats(){
		return stats;
    }
    public Point getRebuy(){
		return rebuy;
    }
    public Point getLobby(){
		return lobby;
    }
    public Point getExit(){
		return exit;
    }
    public Point getChipsMenu(){
    	return chipsMenu;
    }
    public Point getHandIdLabelSize() {
    	return handIdLabelSize;
	}
    public Point getHandIdLabelPosition() {
    	return handIdLabelPosion;
	}
  
    public static final int getDefalutPos(int maxplayers) {
	  if(maxplayers == 10)return 7;
	  else if(maxplayers == 9)return 6;//by rk, for 9 seat table
	  else if(maxplayers == 8)return 6;
	  else if(maxplayers == 6)return 4;
	  else if(maxplayers == 4)return 3;
	  else return 1;
    }
  
  public abstract Point getPlayerCardsPlaceOpen(int ps, char sex);

  public abstract Point getPlayerCardsPlaceClose(int ps, char sex);

  public abstract Point getPlayerPlace(int ps, char sex);

  public abstract Point getChipsPlace(int ps, char sex);

  public abstract Point getPlayersBublesCoords(int ps, char sex);

  public abstract Point getNamePos(int ps, char sex);
  
  public abstract Point getNamePosSize();
    
  public abstract Point getDealerPos(int ps);

  public abstract Point getOpenCardPos(int ps);
  
  public abstract Point getPlrNotePos(int ps);
  
  public abstract int getBublesOrientation(int ps);
  
  public final ImageIcon getTimer(int no) {
	  StringBuilder sb = new StringBuilder("images/timers/");
	  sb.append(no).append(".png");
	  //ImageIcon ic = Utils.getIcon(s.trim());
	  return getScaledImage(Utils.getIcon(sb.toString().trim()));
  }
  //by rk
  public final ImageIcon getOpenCard(int index) {
	  StringBuilder sb = new StringBuilder("images/cards/");
	  sb.append(isFourcolorcards == true ? "colorcards" : "bwcards");
	  sb.append("/").append(index).append(".png");
	  //ImageIcon ic = Utils.getIcon(sb.toString().trim());
	  //return reSizeImage(ic);
	  return getScaledImage(Utils.getIcon(sb.toString().trim()));
  }
  public final ImageIcon getChip(int index) {
	  StringBuilder sb = new StringBuilder("images/chips/");
	  sb.append(index).append(".png");
	  //ImageIcon ic = Utils.getIcon(sb.toString().trim());
	  return getScaledImageFast(Utils.getIcon(sb.toString().trim()));
	  //return getScaledImage(ic);
  }
}
