package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import com.agneya.util.FormattingUtils;
import com.onlinepoker.skin.RoomSkin;

public class Pot implements Painter {
	public double _value;
	public double _totalBet;
	public String _name;
    protected Chip[] chipsPot = null;
    RoomSkin _skin;
    JComponent _owner;
    int _x, _y;
    
	public Pot(String name, double val, double totalBet){
		_name=name;
		_value=val;
		_totalBet = totalBet;
	}
	
	public void setView(RoomSkin s, JComponent own){
		_skin = s;
		_owner = own;  
		_x = _skin.getHeapPlace().x;
		_y = _skin.getHeapPlace().y;
		if (_name.equals("side-1")){
			_x +=(int)(75*_skin._ratio_x);//75;
		}
		else if(_name.equals("side-2")){
			_x +=(int)(150*_skin._ratio_x);// 150;
		}
		else if(_name.equals("side-3")){
			_x += (int)(200*_skin._ratio_x);//200;
		}
		chipsPot =   Chip.MoneyToChips(_value, _x, _y, _skin.getChips(), _owner,_skin);
	}

	@Override
	public void paint(JComponent c, Graphics g) {
		if (chipsPot ==null)return;
        /** Draw pot chips and amount */
        for (int i = 0; i < chipsPot.length; i++) {
            chipsPot[i].paint(_owner, g);
        } 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Humanist", Font.BOLD, 12));
        //if(_totalBet + _value > 0)g.drawString("Total Pot: $ " + FormattingUtils.getRoundedDollarCent(_value + _totalBet), _x -15, _y - 15);
        if(_value >= 0.001)g.drawString("€ " + FormattingUtils.getRoundedDollarCent(_value), _x , _y + (int)(55*_skin._ratio_y));//55
    }

	public void refresh(){
        Rectangle r1 = Utils.getChipsArea(chipsPot);
        chipsPot = 
                Chip.MoneyToChips(_value, _x, _y, _skin.getChips(), _owner,_skin);
        Rectangle r2 = Utils.getChipsArea(chipsPot);
        if (r1 != null && r2 != null) {
            r1.add(r2);
            _owner.repaint(r1);
        } else if (r1 != null) {
            _owner.repaint(r1);
        } else if (r2 != null) {
            _owner.repaint(r2);
        }
	}
}

