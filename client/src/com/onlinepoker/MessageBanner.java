package com.onlinepoker;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.onlinepoker.skin.RoomSkin;

public class MessageBanner implements Painter {
	public String _name;
    RoomSkin _skin;
    JComponent _owner;
    int _x, _y;
    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_MESSAGE_BANNER_BG);
    //ImageIcon _animation = Utils.getIcon(ClientConfig.IMG_MESSAGE_BANNER_ANIMATION);
   // JLabel ajaxLoaderBar = null;
    Timer _mbt = null;
    public boolean visible = true;

    public MessageBanner(String name){
		_name=name;
		
	}
	
	public void setView(RoomSkin s, JComponent own){
		_skin = s;
		_owner = own;  
		_x = _skin.getHeapPlace().x - 75;
		_y = _skin.getHeapPlace().y - 8;
		//ajaxLoaderBar = new JLabel(_animation);
	}
	public void resize(RoomSkin skin){
		_skin=skin;
		_x = _skin.getHeapPlace().x - 75;
		_y = _skin.getHeapPlace().y - 8;
	}

	@Override
	public void paint(JComponent c, Graphics g) {
		g.setColor(Color.BLACK);
        g.setFont(new Font("Humanist", Font.BOLD, 12));
        if(_name.length() < 40)
        {
        	if(_name.startsWith("Waiting for players")){
        		g.drawImage(icon.getImage(),_x+80 , _y+5 , 140,20,null);
             	g.drawString(_name, _x + 95, _y + 20);
             	//g.drawImage(_animation.getImage(),_x+50 , _y+20 , _animation.getIconWidth(),_animation.getIconHeight(),_owner);
             	//ajaxLoaderBar.setBounds(_x+50, _skin.getHeapPlace().y+10, _animation.getIconWidth(), _animation.getIconHeight());
             	//_owner.add(ajaxLoaderBar);
        	}else{
        		g.drawImage(icon.getImage(),_x+50 , _y , icon.getIconWidth()-20,icon.getIconHeight()-20,null);
             	g.drawString(_name, _x + 60, _y + 12);
        	}
        }
        else
        {
            g.drawImage(icon.getImage(),_x , _y , icon.getIconWidth() + 10,icon.getIconHeight(),null);
        	g.drawString(_name.substring(0, _name.length()/2),_x + 5, _y + 15);
        	g.drawString(_name.substring(_name.length()/2),_x + 5, _y + 32);
        }
    }
	
	public void startTimer()
	{
		_mbt = new Timer();
    	_mbt.schedule(new TimerTask() {
            public void run() {
            	removeBanner();
            	if(_mbt != null){//if cond by rk
            		_mbt.cancel();
            		_mbt = null;
            	}
            	_owner.repaint();
            }
        }, 10000);
	}
	public void removeBanner(){
		//new Exception("removeBanner").printStackTrace();
		_name = "";
		//System.out.println("ajaxLoaderBar "+ajaxLoaderBar);
		if(_mbt != null){//by rk
    		_mbt.cancel();
    		_mbt = null;
    		
    	}
		//_owner.remove(ajaxLoaderBar);
		_owner.repaint();
		visible = false;
    }

}

