package com.onlinepoker;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.onlinepoker.skin.RoomSkin;


/** DialerChip */
public class DealerChip
    extends Chip {

  /** How many cycles must passed before chip runs to exit_point */
  public static final int CYCLE_COUNT = 20;

//  public static final int DIALER_CHIP_WIDTH  = 24;
//  public static final int DIALER_CHIP_HEIGHT = 18;
  public static final int DEALER_CHIP_WIDTH = 34;
  public static final int DEALER_CHIP_HEIGHT = 28;

  static Logger _cat = Logger.getLogger(DealerChip.class.getName());


  /** Motions vectors */

  /** skin */
  private RoomSkin roomSkin = null;

  public DealerChip(int posNum, RoomSkin roomSkin, JComponent owner) {
    super(roomSkin.getDealerPos(posNum), roomSkin.getDealerIcon(), 0, 0, owner);
    this.roomSkin = roomSkin;
  }

  public void moveToPos(int num) {
    Point p = roomSkin.getDealerPos(num);
    if (p != null) {
    	_cat.fine("moveToPos");
      startMove(p.x, p.y);
    }
  }

  public void startMove(int x, int y) {
    super.startMove(x, y);
  }

  public void update() {
    while (currentTact != 0) {
      int oldX = pos.x;
      int oldY = pos.y;
      if (currentTact > 0) {
        pos.move(endPos.x + (startPos.x - endPos.x) * currentTact / CYCLE_COUNT,
                 endPos.y + (startPos.y - endPos.y) * currentTact / CYCLE_COUNT);
        if (--currentTact == 0) {
          pos.move(endPos.x, endPos.y);
          valid = false;
        }
        owner.repaint(oldX, oldY, roomSkin.getDealerChipWidth(), roomSkin.getDealerChipHeight());
      }
      refresh();
      try { Thread.sleep(10); } catch(Exception e){}
    }
  }

  public Rectangle getRealCoords() {
    return new Rectangle(pos.x, pos.y, roomSkin.getDealerChipWidth(), roomSkin.getDealerChipHeight());
  }

  public void paint(JComponent c, Graphics g) {
    if (icon != null) {
      if (!g.getClipBounds().intersects(pos.x, pos.y, roomSkin.getDealerChipWidth(),
    		  roomSkin.getDealerChipHeight())) {
        return;
      }
      Graphics gcopy = g.create(pos.x, pos.y, roomSkin.getDealerChipWidth(),
    		  roomSkin.getDealerChipHeight());
      icon.paintIcon(c, gcopy, 0, 0);
      gcopy.dispose();
    }
  }

  protected void refresh() {
    owner.repaint(pos.x, pos.y, DEALER_CHIP_WIDTH, DEALER_CHIP_HEIGHT);
  }

}
