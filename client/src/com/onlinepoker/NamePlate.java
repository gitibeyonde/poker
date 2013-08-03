package com.onlinepoker;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class NamePlate implements Painter {
	public static final int CYCLE_COUNT = 20;
	public static final int CHIP_WIDTH = 22;
	public static final int CHIP_HEIGHT = 22;
	
	JComponent _owner;
    int _x, _y;
    ImageIcon icon = Utils.getIcon("images/plate_main_black.png");
	ImageIcon namePlateDisconnected = Utils.getIcon("images/plate_disconnected.png");
    ImageIcon namePlateActive = Utils.getIcon("images/plate_active.png");
    ImageIcon namePlateSmallBlind = Utils.getIcon("images/plate_small_blind.png");
    ImageIcon namePlateBigBlind = Utils.getIcon("images/plate_big_blind.png");
    ImageIcon namePlateCheck = Utils.getIcon("images/plate_check.png");
    ImageIcon namePlateCall = Utils.getIcon("images/plate_call.png");
    ImageIcon namePlateBet = Utils.getIcon("images/plate_bet.png");
    ImageIcon namePlateRaise = Utils.getIcon("images/plate_raise.png");
    ImageIcon namePlateFold = Utils.getIcon("images/plate_fold.png");
    ImageIcon namePlateAllIn = Utils.getIcon("images/plate_all_in.png");
    ImageIcon namePlateAnte = Utils.getIcon("images/plate_ante.png");
    ImageIcon namePlateTime = Utils.getIcon("images/plate_time.png");
    ImageIcon namePlateTimeOut = Utils.getIcon("images/plate_time_out.png");
    ImageIcon namePlateReserved = Utils.getIcon("images/plate_reserved.png");
	
    /** number of current tact. If ==0 then not move */
    protected int currentTact = 0;
    private int level = 0;
    
    /** Then chips end move valid set to false. It can be use to delete chips that end move */
    protected boolean valid = true;

    /** Start and end of moving name plate */
    protected Point startPos = null;
    protected Point endPos = null;
    
    /** current position of name plate */
    protected Point pos = null;
    
    public NamePlate(JComponent c, int x,int y){
    	_owner = c;
		_x = x ;
		_y = y;
	}

	@Override
	public void paint(JComponent c, Graphics g) {
		
        g.drawImage(icon.getImage(),_x , _y , icon.getIconWidth() + 10,icon.getIconHeight(),null);
        
    }
	public void startMove(int x, int y) {
	    startPos.move(pos.x, pos.y);
	    endPos.move(x, y);
	    currentTact = CYCLE_COUNT;
	    refresh();
	    valid = true;
	  }
	protected void refresh() {
		_owner.repaint(pos.x, pos.y - level * 3, CHIP_WIDTH, CHIP_HEIGHT);
	}

}

