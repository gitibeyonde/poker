package com.onlinepoker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.golconda.game.util.Card;
import com.onlinepoker.skin.RoomSkin;


/** Player View Card (MVC all in one) */
public class PlCard implements Painter {
  /** the component which proced painint */
  JComponent owner = null;

  /** Constants */public static final int CYCLE_COUNT = 40;
//  public static final int CARD_CLOSE_WIDTH = 37;
//  public static final int CARD_CLOSE_HEIGHT = 54;
//  public static final int CARD_OPEN_WIDTH = 54;
//  public static final int CARD_OPEN_HEIGHT = 78;
//  public static final int CARD_EXPOSED_WIDTH = 40;
//  public static final int CARD_GAP = 1;


  /** Current position */
  protected Point pos = null;


  /** Icons */
  private ImageIcon closeIcon = null;
  //private ImageIcon openIcon = null;
  private ImageIcon bigCloseIcon = null;

  private boolean valid = true;


//  private boolean showAsSuit  = false;

  /** Is PlCard open ?*/
  private boolean isOpen = false;
  public boolean isSelected = false;


  /** If PlCard is open, then face take from card */
  private Card card = null;


  /** koefecients for moving cards */
  protected static double[] koef;
  static {
    koef = new double[CYCLE_COUNT];
    for (int i = 0; i < CYCLE_COUNT; i++) {
      koef[i] = (double) CYCLE_COUNT *
                (((double) i / (double) CYCLE_COUNT) *
                 ((double) i / (double) CYCLE_COUNT) *
                 ((double) i / (double) CYCLE_COUNT) *
                 ((double) i / (double) CYCLE_COUNT));
    }
  }

  private Point startPos = null;
  private Point endPos = null;

  private int currentTact = 0;

  private int level = 0;
  
  private RoomSkin _skin;
    public PlCard(Point pos/*, ImageIcon openIcon*/, Card card,
               int level, JComponent owner, RoomSkin skin) {
    	this._skin= skin;
    	this.pos = new Point(pos);
    	//System.out.println("in plcard pos "+pos.x+","+pos.y);
        startPos = new Point(pos);
        endPos = new Point(pos);
        //this.openIcon = openIcon;
        this.card = card;
        //System.out.println("index "+card.getIndex());
        this.level = level;
       // System.out.println("level= "+level);
        isOpen = true;
        this.pos.translate((int) ((_skin.getCardOpenWidth() + _skin.getCardGap()) * level), 0); 
        this.owner = owner;
    }
    
  public PlCard(Point pos/*, ImageIcon openIcon*/, ImageIcon closeIcon, Card card,
                 int level, JComponent owner,RoomSkin skin) {
	  this._skin= skin;
	  this.pos = new Point(pos);
    startPos = new Point(pos);
    endPos = new Point(pos);
    //this.openIcon = openIcon;
    this.closeIcon = closeIcon;
    this.card = card;
    this.level = level;
    //System.out.println("card index " + card.getIndex());
    if ((card == null) || (card.getIndex() == Card.CLOSED_CARD)) {
      isOpen = false;
      if (closeIcon == null){
        //new Exception().printStackTrace();
         this.pos.translate((int) ((_skin.getCardOpenWidth() - _skin.getCardExposedWidth()) * level), 0); 
      }
      else {
       //new Exception().printStackTrace();
        this.pos.translate( 15 * level, -1 * level);
      }
    }
    else {
      isOpen = true;
      this.pos.translate((int) ((_skin.getCardOpenWidth() - _skin.getCardExposedWidth()) * level), 0); 
    }
    this.owner = owner;
  }

// constructor for STUD cards
  public PlCard(Point pos/*, ImageIcon openIcon*/, ImageIcon closeIcon,
                ImageIcon bigClose, Card card, int level, JComponent owner,RoomSkin skin) {
    this(pos/*, openIcon*/, null, card, level, owner,skin);
    this._skin= skin;
    this.bigCloseIcon = closeIcon;
    this.closeIcon = null;
  }

  public void setPos(int x, int y) {
    pos.move(x, y);
    refresh();
  }

  public void startMove(int x, int y) {
    refresh();
    startPos.move(pos.x, pos.y);
    endPos.move(x, y);

    if (isOpen) {
      endPos.translate((int) (_skin.getCardOpenWidth() * 1.2 * level), 0);
    }
    else {
      endPos.translate((int) (_skin.getCardCloseWidth() * 0.2 * level),
                       (int) (_skin.getCardCloseWidth() * 0.2 * level));

    }
    currentTact = CYCLE_COUNT;
    valid = true;
  }

  public void toggleSelected(){
    if (isSelected){
      isSelected = false;
    }
    else {
      isSelected = true;
    }
  }
  
  public void update() {
    if (currentTact > 0) {
      int oldX = pos.x;
      int oldY = pos.y;
      refresh();
      pos.move(endPos.x + (startPos.x - endPos.x) * (int) koef[currentTact -
               1] / CYCLE_COUNT,
               endPos.y + (startPos.y - endPos.y) * (int) koef[currentTact -
               1] / CYCLE_COUNT);
      if (--currentTact == 0) {
        pos.move(endPos.x, endPos.y);
        valid = false;
      }
      if (isOpen) {
        owner.repaint(oldX-30, oldY-30, _skin.getCardOpenWidth()+30, _skin.getCardOpenHeight()+30);
      }
      else {
          if (bigCloseIcon !=null){
              owner.repaint(oldX-30, oldY-30, _skin.getCardOpenWidth()+30, _skin.getCardOpenHeight()+30);
          }
          else {
            owner.repaint(oldX-30, oldY-30, _skin.getCardOpenWidth()+30, _skin.getCardOpenHeight()+30);
          }
      }
    }
    else
    {
    	refresh();
    }
  }

  public boolean isValid() {
    return (valid);
  }

  public Point getPos() {
    return (pos.getLocation());
  }

  public void paint(JComponent c, Graphics g) {
    int selectedy=pos.y;
    if (isSelected){
      selectedy-=8;
    }
    if (isOpen) { // paint open card
      //if (openIcon != null) {
        //System.out.println("#->"+pos.x + ", " + pos.y + " , " + _skin.getCardOpenWidth() + ", " + _skin.getCardOpenHeight()+", "+_skin.getCardGap());
        Graphics gcopy = g.create(pos.x, selectedy, (_skin.getCardOpenWidth() + _skin.getCardGap()),_skin.getCardOpenHeight());
        
        /**BufferedImage resizedImage = new BufferedImage(54,78,BufferedImage.BITMASK);
		Graphics2D g2 = resizedImage.createGraphics();
		g2.drawImage(openIcon.getImage(), -_skin.getCardOpenWidth() * getRank(card.getRank()),
										-_skin.getCardOpenHeight() * getSuit(card.getSuit()), 
										(int)(openIcon.getIconWidth()),
										(int) (openIcon.getIconHeight()), null);
		g2.dispose();

		ImageIcon ic = _skin.reSizeImage(new ImageIcon(resizedImage));
		//System.out.println("new img size "+ic.getIconWidth()+","+ic.getIconHeight());
		ic.paintIcon(c, gcopy, 0,0);*/
//        g.setColor( Color.BLACK );
//        g.fillRect( pos.x, selectedy, (_skin.getCardOpenWidth() + _skin.getCardGap()), _skin.getCardOpenHeight());
//
//        System.out.println(card.getRank()+","+getRank(card.getRank())+",x "+-_skin.getCardOpenWidth() * getRank(card.getRank()));
//        System.out.println(card.getSuit()+","+getSuit(card.getSuit())+",y "+-_skin.getCardOpenHeight() * getSuit(card.getSuit()));
//        openIcon.paintIcon(c, gcopy, -(_skin.getCardOpenWidth()) * getRank(card.getRank()),
//                           			-_skin.getCardOpenHeight() * getSuit(card.getSuit()));
       ImageIcon imgic = _skin.getOpenCard(card.index);
       imgic.paintIcon(c, gcopy,0,0);
        gcopy.dispose();
      //}
    }
    else { // paint closed card
      if (bigCloseIcon !=null){
          //System.out.println("Big close " + pos.x + ", " + pos.y + " , " + CARD_OPEN_WIDTH + ", " + CARD_OPEN_HEIGHT);
        Graphics gcopy = g.create(pos.x, selectedy, _skin.getCardOpenWidth(),
        		_skin.getCardOpenHeight());
       bigCloseIcon.paintIcon(c, gcopy, 0, 0);
       gcopy.dispose();
      }
      else if (closeIcon != null) {
          //System.out.println("Small close " + pos.x + ", " + pos.y + " , " + CARD_OPEN_WIDTH + ", " + CARD_OPEN_HEIGHT);
        Graphics gcopy = g.create(pos.x, pos.y, _skin.getCardCloseWidth(),
        		_skin.getCardCloseHeight());
        closeIcon.paintIcon(c, gcopy, 0, 0);
        gcopy.dispose();
      }
    }
  }
    public void paintSmall(JComponent c, Graphics g) {  
      if (isOpen) {
        RoomSkin.getSmallCardImage(card).paintIcon(c, g, pos.x, pos.y);
      }
      else {
          RoomSkin.getSmallCloseCardImage().paintIcon(c, g, pos.x, pos.y);
      }
    }

    /**public void paintSmall(JComponent c, Graphics g) {
        Graphics gcopy = g.create(pos.x, pos.y, 19, 35);
        openIcon.paintIcon(c, gcopy, -CARD_OPEN_WIDTH * getRank(card.getRank()),
                           -CARD_OPEN_HEIGHT * getSuit(card.getSuit()));
        gcopy.dispose();
    }**/

  /** // to paint winner cards a bit up to normal desk cards */
  public void paint(JComponent c, Graphics g, int x) {
    Graphics gcopy = g.create(pos.x, pos.y - 20, _skin.getCardOpenWidth() ,
    											_skin.getCardOpenHeight() );
//    openIcon.paintIcon(c, gcopy, -_skin.getCardOpenWidth() * getRank(card.getRank()),
//                       -_skin.getCardOpenHeight() * getSuit(card.getSuit()));
    ImageIcon imgic = _skin.getOpenCard(card.index);
    imgic.paintIcon(c, gcopy,0,0);
    gcopy.dispose();
  }

  public void refresh() {
  
      if (isOpen) {
        owner.repaint(pos.x-30, pos.y-30, _skin.getCardOpenWidth()+30, _skin.getCardOpenHeight()+30);
      }
      else {
          if (bigCloseIcon !=null){
              owner.repaint(pos.x-30, pos.y-30, _skin.getCardOpenWidth()+30, _skin.getCardOpenHeight()+30);
          }
          else {
            owner.repaint(pos.x-30, pos.y-30, _skin.getCardCloseWidth()+30, _skin.getCardCloseHeight()+30);
          }
      }
  
  }

  public int getIndex() {
    if (card == null) {
      return Card.CLOSED_CARD;
    }
    return card.getIndex();
  }

  public int getRank(int rank) {
    if (rank >= 0 && rank <= 8) {
      return 8 - rank;
    }
    if (rank >= 9 && rank <= 12) {
      return 12 - rank + 9;
    }
    return -1;
  }

  public Rectangle getBounds() {
    return (isOpen) ?
        new Rectangle(pos.x, pos.y, _skin.getCardOpenWidth(), _skin.getCardOpenHeight()) :
        new Rectangle(pos.x, pos.y, _skin.getCardCloseWidth(), _skin.getCardCloseHeight());
  }

  public int getSuit(int suit) {
    switch (suit) {
      case 1:
        return 3;
      case 2:
        return 1;
      case 3:
        return 2;
    }
    return suit;
  }

  public Card getCard() {
    return card;
  }

}
