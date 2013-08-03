package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

import com.agneya.util.FormattingUtils;
import com.onlinepoker.skin.RoomSkin;

public class TotalBet implements Painter {
	public double _totalBet;
	public double _rake;
	RoomSkin _skin;
    JComponent _owner;
    int _x, _y;
    
	public TotalBet(double totalBet, double rake){
		_totalBet = totalBet;
		_rake = rake;
	}
	
	public void resize(RoomSkin skin){
		_skin=skin;
		_x = _skin.getHeapPlace().x;
		_y = _skin.getHeapPlace().y;
	}
	
	public void setView(RoomSkin s, JComponent own){
		_skin = s;
		_owner = own;  
		_x = _skin.getHeapPlace().x;
		_y = _skin.getHeapPlace().y;
	}

	@Override
	public void paint(JComponent c, Graphics g) {
		g.setColor(Color.WHITE);
        g.setFont(new Font("Humanist", Font.BOLD, 12));
        if(_totalBet > 0)g.drawString("Total Pot: € " + FormattingUtils.getRoundedDollarCent(_totalBet), _x -15, _y - 15);
        /* 
         * 
         * if(_rake > 0)g.drawString("Rake:€" + FormattingUtils.getRoundedDollarCent(_rake), _x -75, _y + 50);
         * 
         * */
    }

	public void refreshTotalBet(){
        _owner.repaint(); 
	}
	
	public double getRake() { return _rake; }
	public void clearRake() { _rake = 0; }
}

