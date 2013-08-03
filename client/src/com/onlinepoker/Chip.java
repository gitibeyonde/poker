package com.onlinepoker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.onlinepoker.skin.RoomSkin;


/************    Chip    ****************/
public class Chip implements Painter {
  /** the component which proced painint */
  JComponent owner = null;

  /** How many cycles must passed before chip runs to exit_point */
// old values, cycle may be wrong for bigger chip size i made

  public static final int CYCLE_COUNT = 20;
  public static final int CHIP_WIDTH = 22;
  public static final int CHIP_HEIGHT = 22;

  static Logger _cat = Logger.getLogger(Chip.class.getName());


  /** current poition of chip */
  protected Point pos = null;


  //  protected Rectangle bounds = null;

  /** mapa whith icons */
  protected ImageIcon icon = null;


  /** number of map to this chip */
  protected int mapaNum = 0;


  /** Then chips end move valid set to false. It can be use to delete chips that end move */
  protected boolean valid = true;


  /** Position of chip higher then zero-level
   *  (for expample, lower chips has level = 0,
   *  most high - level = 1, and so....
   */

  private int level = 0;


  /** Start and end of moving chip */
  protected Point startPos = null;
  protected Point endPos = null;


  /** number of current tact. If ==0 then not move */
  protected int currentTact = 0;

  /** total moving chips amount */
  protected double movingChipsAmt = 0.0;
  
  public double getMovingChipsAmt() {
		return movingChipsAmt;
  }
  public void setMovingChipsAmt(double movingChipsAmt) {
		this.movingChipsAmt = movingChipsAmt;
  }

  public Chip(

      Point pos, ImageIcon icon, int mapaNum, int level, JComponent owner) {
    this.pos = new Point(pos);
    //System.out.println(pos.x+","+pos.y);
    startPos = new Point(pos);
    endPos = new Point(pos);
    this.icon = icon;
    this.mapaNum = mapaNum;
    //System.out.println("mapaNum "+mapaNum);
    this.level = level;
    this.owner = owner;
    refresh();
  }

  public double getMoneyValue() {
    return VALUES[mapaNum] / 100.0;
  }
  //resize code
  public  static  RoomSkin _skin;
  public void resize(RoomSkin skin){
	  _skin=skin;
	 setPos((int)(pos.x*_skin._ratio_x), (int)(pos.y*_skin._ratio_y));
	    refresh();
  }
  public void setSkin(RoomSkin skin) {
		// TODO Auto-generated method stub
		_skin=skin;
  }
  public void setPos(int x, int y) {
    pos.move(x, y);
    refresh();
  }

  public void startMove(int x, int y) {
    startPos.move(pos.x, pos.y);
    endPos.move(x, y);
    currentTact = CYCLE_COUNT;
    refresh();
    valid = true;
  }

  public void update() {
    if (currentTact == 0) {
      return;
    }
    Rectangle r1 = getRealCoords();
    if (currentTact > 0) {
      pos.move(endPos.x + (startPos.x - endPos.x) * (currentTact) / CYCLE_COUNT,
               endPos.y + (startPos.y - endPos.y) * (currentTact) / CYCLE_COUNT);
      if (--currentTact == 0) {
        pos.move(endPos.x, endPos.y);
        valid = false;
      }
    }
    owner.repaint(r1);
    //_cat.finest("BET: " + r1);
    refresh();
  }

  public boolean isValid() {
    return (valid);
  }

  public Point getPos() {
    return (pos.getLocation());
  }

  public int getSizeW() {
    return CHIP_WIDTH;
  }

  public int getSizeH() {
    return CHIP_HEIGHT;
  }

  public Rectangle getRealCoords() {
    return new Rectangle(pos.x, pos.y - level * 3, _skin.getChipWidth(), _skin.getChipHeight() + 20);
  }

  public int getLevel() {
    return (level);
  }

  public void paint(JComponent c, Graphics g) {
    //if (icon != null) {
      if (!g.getClipBounds().intersects(pos.x, pos.y - level * 2, _skin.getChipWidth(),
    		  _skin.getChipHeight())) {
        
    	  return;
      }
      //commented by rk
      //Graphics gcopy = g.create(pos.x, pos.y - level * 2, _skin.getChipWidth(),
    	//	  _skin.getChipHeight());
      //icon.paintIcon(c, gcopy, 0, -_skin.getChipHeight() * mapaNum);
      Graphics gcopy = g.create(pos.x, pos.y, _skin.getChipWidth()+100,_skin.getChipHeight()+100);
      //g.setColor( Color.BLACK );
      //g.fillRect(pos.x, pos.y, _skin.getChipWidth(),_skin.getChipHeight());
      ImageIcon chipIcon = _skin.getChip(mapaNum);
      chipIcon.paintIcon(c, gcopy, 0, 0);
      gcopy.dispose();
    //commented by rk
      //gcopy = g.create(pos.x, pos.y - level * 2, _skin.getChipWidth()  + 50,
    	//	  _skin.getChipHeight() + 50);
      gcopy = g.create(pos.x, pos.y, _skin.getChipWidth()  + 50,
    		  _skin.getChipHeight() + 50);
      if(movingChipsAmt != 0)
	  {
    	  gcopy.setFont(Utils.boldFont);
    	  gcopy.setColor(Color.WHITE);
		  gcopy.drawString(SharedConstants.doubleToString(movingChipsAmt), 0, 40);
	  }
      
	  
    //}
  }

  protected void refresh() {
    owner.repaint(pos.x, pos.y - level * 3,  _skin.getChipWidth() +150, _skin.getChipHeight() +150);
  }

  static public Point toBasePoint(int num, int baseX, int baseY) {
    int x = (num % 2 * (( _skin.getChipWidth()) + 1)) + num / 2 * (( _skin.getChipWidth()) + 1) / 2 +
            baseX;
    int y = num / 2 * ((_skin.getChipHeight()) + 1) + baseY;
    return new Point(x, y);
  }

  static final int[] VALUES = {1, 5, 25, 100, 500, 2500, 10000, 50000, 100000, 500000, 2500000, 10000000, 50000000, 100000000, 500000000};
  public static Chip[] MoneyToChips(double money, int startX, int startY,
                                    ImageIcon mapa, JComponent owner, RoomSkin skin) {
    //		int rezult[][] = simplify24Columns(money);
	  _skin=skin;
    if (money == 0) {
      return new Chip[] {};
    }
    Vector[] chipVector = simplify24Columns((int) (money * 100));
    int sum = 0;
    for (int i = 0; i < chipVector.length; i++) {
      for (Iterator iter = chipVector[i].iterator(); iter.hasNext(); ) {
        sum += ((Point) iter.next()).y;
      }
    }
    Chip[] returnArray = new Chip[sum];
    int idx = 0;
    for (int i = 0; i < chipVector.length; i++) {
      int y = 0;
//	for each column
      for (Iterator iter = chipVector[i].iterator(); iter.hasNext(); ) {
        Point p = (Point) iter.next(); // heap chips with one nominal
        Point pToArray = toBasePoint(i, startX, startY); // coord. of heap
        for (int j = 0; j < p.y; j++) {
          returnArray[idx++] = new Chip(pToArray, mapa, p.x, y++, owner);
        }
      }
    }
    return returnArray;
  }

  public static Vector[] simplify24Columns(int money) {
    int chipsValue[] = new int[15]; // Chips amount at 1c 5c 25c 1$ 5$ 25$ 100$ 500$ 1k$ 5k$ 25$ 100k$ 500k$ 1M$ 5M$
    int chipsPosit[] = new int[15]; // Chips sort index position
    int columnValue[] = new int[15]; // Chips amount at column
    int oldMoney = money;
    for (int i = VALUES.length - 1; i >= 0; i--) {
      if (money / VALUES[i] != 0) {
        chipsValue[i] += money / VALUES[i];
        money -= (money / VALUES[i] * VALUES[i]);
      }
    }

    //		indexes of chips position
    int idx = 0;
    for (int i = 0; i < chipsValue.length; i++) {
      chipsPosit[i] = (chipsValue[i] > 0) ? idx++ : -1;
      if (chipsPosit[i] >= 0) {
        columnValue[idx - 1] = chipsValue[i];
      }
    }

    while (nonZeroElemsCount(columnValue) > 4) {

      int minIndex1 = getMinNonZeroElementIndex(columnValue);
      int minIndex2 = getSecondMinNonZeroElementIndex(columnValue, minIndex1);
      replaceInArray(chipsPosit, minIndex1, minIndex2);

      //			shift values in columnValue from [minIndex1] to [end]
      columnValue[minIndex2] += columnValue[minIndex1];
      for (int i = minIndex1; i < columnValue.length - 1; i++) {
        columnValue[i] = columnValue[i + 1];
      }
      columnValue[columnValue.length - 1] = 0;

      //			decrement values on chipsPosit
      for (int i = 0; i < chipsPosit.length; i++) {
        if (chipsPosit[i] >= minIndex1) {
          chipsPosit[i]--;
        }
      }
    }
    //		And now we must promptly move chips to two-dimensional array
    Vector returnValue[] = new Vector[nonZeroElemsCount(columnValue)];

    for (int i = 0; i < returnValue.length; i++) {

      int currIndex = getMaxNonZeroElementIndex(columnValue);
      returnValue[i] = new Vector();

      for (int j = 0; j < chipsPosit.length; j++) {
        if (chipsPosit[j] == currIndex) {
          returnValue[i].add(new Point(j, chipsValue[j]));

        }
      }
      columnValue[currIndex] = 0;
    }

    return returnValue;
  }

  public static int nonZeroElemsCount(int[] array) {
    int value = 0;
    for (int i = 0; i < array.length; i++) {
      if (array[i] != 0) {
        ++value;
      }
    }
    return value;
  }

  public static int getMinNonZeroElementIndex(int[] array) {
    int minIndex = 0;
    for (int i = 0; i < array.length; i++) {
      if ((array[minIndex] == 0) || (array[i] < array[minIndex] && array[i] > 0)) {
        minIndex = i;
      }
    }
    return minIndex;
  }

  public static int getMaxNonZeroElementIndex(int[] array) {
    int maxIndex = 0;
    for (int i = 0; i < array.length; i++) {
      if ((array[maxIndex] == 0) || (array[i] > array[maxIndex] && array[i] > 0)) {
        maxIndex = i;
      }
    }
    return maxIndex;
  }

  public static int getSecondMinNonZeroElementIndex(int[] array,
      int firstMinNonZeroElementIndex) {
    int minIndex = 0;
    for (int i = 0; i < array.length; i++) {
      if (((array[minIndex] == 0) ||
           (array[i] <= array[minIndex] && array[i] > 0)) &&
          (i != firstMinNonZeroElementIndex)) {
        minIndex = i;
      }
    }
    return minIndex;
  }

  public static void replaceInArray(int[] array, int from, int to) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == from) {
        array[i] = to;
      }
    }
  }

  public static Chip[] MoneyToOneColumnChips(double money, int startX,
      int startY, ImageIcon mapa, JComponent owner) {
    Vector chipVector = new Vector();
    int dy = 0;

    // convert money to chips
    money *= 100;

    while (money > 0) {
	  if (money / 500000000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 14, dy, owner));
        money -= 500000000;
        dy++;
      }
      else if (money / 100000000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 13, dy, owner));
        money -= 100000000;
        dy++;
      }
      else if (money / 50000000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 12, dy, owner));
        money -= 50000000;
        dy++;
      }
      else if (money / 10000000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 11, dy, owner));
        money -= 10000000;
        dy++;
      }
      else if (money / 2500000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 10, dy, owner));
        money -= 2500000;
        dy++;
      }
      else if (money / 500000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 9, dy, owner));
        money -= 500000;
        dy++;
      }
      else if (money / 100000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 8, dy, owner));
        money -= 100000;
        dy++;
      }
      else if (money / 50000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 7, dy, owner));
        money -= 50000;
        dy++;
      }
      else if (money / 10000 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 6, dy, owner));
        money -= 10000;
        dy++;
      }
      else if (money / 2500 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 5, dy, owner));
        money -= 2500;
        dy++;
      }
      else if (money / 500 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 4, dy, owner));
        money -= 500;
        dy++;
      }
      else if (money / 100 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 3, dy, owner));
        money -= 100;
        dy++;
      }
      else if (money / 25 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 2, dy, owner));
        money -= 25;
        dy++;
      }
      else if (money / 5 >= 1) {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 1, dy, owner));
        money -= 5;
        dy++;
      }
      else {
        chipVector.add(new Chip(new Point(startX, startY), mapa, 0, dy, owner));
        money -= 1;
        dy++;
      }
    }
    return (Chip[]) chipVector.toArray(new Chip[chipVector.size()]);
  }
}
