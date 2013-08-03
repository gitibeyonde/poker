package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.onlinepoker.skin.RoomSkin;

public class TimerMessage implements Painter {
	Logger _cat = Logger.getLogger(TimerMessage.class.getName());
	
	public static final int GRACE_TIME = 36;//20(GRACE TIMER) +11(RESPONSE_TIME)+5(MISCELLANIOUS)
	public String _name;
    RoomSkin _skin;
    JComponent _owner;
    int gracecounter = GRACE_TIME;
    int _x, _y;
    ImageIcon disconnected_plate = null;
    ImageIcon reconnecting_plate_on = null;
    ImageIcon reconnecting_plate_off = null;
    ImageIcon icon_on = null;
    ImageIcon icon_off = null;
    Timer _tmt = null;
    public boolean visible = true;
    public int _pos = -1;
    ClientPokerModel _cpm= null;

    public TimerMessage(int pos){
    	_pos = pos;
	}
	
	public void setView(RoomSkin s, JComponent own, ClientPokerModel cpm){
		
		_skin = s;
		_owner = own;
		_cpm = cpm;
		Point p = s.getNamePos(_pos, ClientPlayerModel.MALE);
		_x = (int)(p.x);//*_skin._ratio_x);//+ 150;
		_y = (int)(p.y);//*_skin._ratio_y);// + 35;
		 disconnected_plate = _skin.getDisconnectedPlate();
	     reconnecting_plate_on = _skin.getReconnecting_plate_on();
	     reconnecting_plate_off = _skin.getReconnecting_plate_off();
	     icon_on = _skin.getIcon_on();
	     icon_off = _skin.getIcon_off();
//		_x = 680;//_skin.getHeapPlace().x - 75;
//		_y = 450;//_skin.getHeapPlace().y - 8;
		//System.out.println("setView "+icon.getIconWidth()+", "+icon.getIconHeight());
	}
	
	public void resize(RoomSkin skin){
		_skin=skin;
		Point p = _skin.getNamePos(_pos, ClientPlayerModel.MALE);
		_x = (int)(p.x);//*_skin._ratio_x);//+ 150;
		_y = (int)(p.y);//*_skin._ratio_y);// + 35;
		 disconnected_plate = _skin.getDisconnectedPlate();
	     reconnecting_plate_on = _skin.getReconnecting_plate_on();
	     reconnecting_plate_off = _skin.getReconnecting_plate_off();
	     icon_on = _skin.getIcon_on();
	     icon_off = _skin.getIcon_off();
	}

	@Override
	public void paint(JComponent c, Graphics g) {
		if(_name != null){
			g.setColor(Color.WHITE);
	        g.setFont(new Font("Humanist", Font.BOLD, 16));
	        if(gracecounter%2 == 0){
	        	//_cat.severe("gc "+gracecounter);
	        	if(gracecounter == 0){
	        		g.drawImage(disconnected_plate.getImage(),_x , _y , disconnected_plate.getIconWidth(),disconnected_plate.getIconHeight(),null);
	        		g.drawImage(icon_on.getImage(),_x+(int)(150*_skin._ratio_x) , _y+(int)(35*_skin._ratio_y) , icon_on.getIconWidth(),icon_on.getIconHeight(),null);
	        	}else{
		        	g.drawImage(reconnecting_plate_on.getImage(),_x , _y , reconnecting_plate_on.getIconWidth(),reconnecting_plate_on.getIconHeight(),null);
		        	g.drawImage(icon_on.getImage(),_x+(int)(150*_skin._ratio_x) , _y+(int)(35*_skin._ratio_y) , icon_on.getIconWidth(),icon_on.getIconHeight(),null);
	        	}
        	}else{
	        	g.drawImage(reconnecting_plate_off.getImage(),_x , _y , reconnecting_plate_off.getIconWidth(),reconnecting_plate_off.getIconHeight(),null);
	        	g.drawImage(icon_off.getImage(),_x+(int)(150*_skin._ratio_x) , _y+(int)(35*_skin._ratio_y) , icon_off.getIconWidth(),icon_off.getIconHeight(),null);
	        }
	        g.drawString(_name, _x+(int)(150*_skin._ratio_x)+(int)(8*_skin._ratio_x), _y+(int)(35*_skin._ratio_y)+(int)(23*_skin._ratio_y));
		}
    }
	public void refresh(){
		_owner.repaint();
	}
	public void startTimer()
	{
		_tmt = new Timer();
		_tmt.schedule(new TimerTask() {
            public void run() {
            	//_cat.severe("counter in TimerMessage "+gracecounter);
            	if(gracecounter <= 20){
	            	if(gracecounter > 0 ){
	            		_cat.finest("counter in TimerMessage "+gracecounter);
	            		if(gracecounter <=10){
	            			gracecounter--;
	            			_name = "0"+gracecounter;
	            		}else{
	            			gracecounter--;
	            			_name = ""+gracecounter;
	            		}
	            	_owner.repaint();
	            	}else{
	            		//gracecounter = 20;
	            		//stopTimer();
	            		//_cat.severe("stop grace counter");
	            		_cpm.stopGraceCounterByPos(_pos);
	            		_cpm.stopNextMoveTimer(); //this is for avoiding running  MoveTimer thread, 
	            								//otherwise it will run continuously and cause game slow
	            	}
            	}else{
            		gracecounter--;
            		_name = null;
            	}
            }
        },0, 1000);
	}
	public void stopTimer(){
		//new Exception("stopTimer").printStackTrace();
		//_cat.severe("TimerMessage.stopTimer() ");
		 _name = "";
		if(_tmt != null){//by rk
			_tmt.cancel();
			_tmt = null;
    	}
		_owner.repaint();
		visible = false;
    }

}

