package com.onlinepoker;

import java.awt.Graphics;
import java.awt.Point;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.onlinepoker.skin.RoomSkin;

public class MoveTimer extends TimerTask implements Painter {
	  static Logger _cat = Logger.getLogger(MoveTimer.class.getName());

	public static final int STAGE_COUNT = 17;
	public static final int TIMER_WIDTH = 101;
	public static final int TIMER_HEIGHT = 8;
	public static final int NAME_PLATE_HEIGHT = 53;
	//ImageIcon _icon;
	ImageIcon _icon1;
	private ImageIcon namePlateActive = null;
	int _x, _y;
	int _currentFrame=-1;
	JComponent _owner;
	ClientPokerModel _cpm;//by rk
	int _plrPos = -1;//by rk
	//resize code
	public RoomSkin _skin;
	private char _sex;
	
	public MoveTimer(RoomSkin s, JComponent owner, int ps, char sex, ClientPokerModel _cpm){
		_skin=s;//resize code
		//_icon = s.getMoveTimer();
		_icon1 = _skin.getTimer(1);
		Point p = s.getNamePos(ps, sex);
		//System.out.println(p.x+", "+p.y);
		_x =(int) (p.x + (38*_skin._ratio_x));
		_y = (int)(p.y + (NAME_PLATE_HEIGHT + 5)*_skin._ratio_y);
		_currentFrame=-1;
		_owner = owner;
		this._cpm = _cpm;
		_plrPos = ps;
		_sex = sex;
		namePlateActive = _skin.getNamePlateActive();
	}
	public void resize(RoomSkin skin){
		_skin=skin;
		//_icon = _skin.getMoveTimer();
		_icon1 = _skin.getTimer(1);
		Point p = _skin.getNamePos(_plrPos,_sex);
		_x =(int) (p.x + (38*_skin._ratio_x));
		_y = (int)(p.y + (NAME_PLATE_HEIGHT + 5)*_skin._ratio_y);
		//System.out.println("resize() in MoveTimer ");
//		if(_cpm != null){
//			System.out.println("_cmp != null");
//			if(_cpm._playersMod[_plrPos] != null){
//				System.out.println("_cpm._playersMod[_plrPos] != null");
//				_cpm._playersMod[_plrPos].setSelected(true);
//			}
//		}
		//System.out.println("isSelected "+_cpm._playersMod[_plrPos].isSelected()+", "+_plrPos+", "+_cpm._playersMod[_plrPos]._name);
		//namePlateActive = _skin.reSizeImage(namePlateActive);
//	       {
//	    	   namePlateActive.paintIcon(c, gcopy, 0, 0);
//	    	   namePlate_color = false;
//	           //gcopy.drawString(model.getPlayerName(), 45, 48);
//	       }
	}
		
	@Override
	public void paint(JComponent c, Graphics g) {
	     if (!g.getClipBounds().intersects(_x, _y, _icon1.getIconWidth(), _icon1.getIconHeight())) {
	        return;
	     }
	    
	     //System.out.println("_currentFrame "+_currentFrame);
	    if(_currentFrame>-1 && _currentFrame <17){
		     _icon1 = _skin.getTimer(_currentFrame);
		     Graphics gcopy = g.create(_x, _y, _icon1.getIconWidth(), _icon1.getIconHeight());
//		     if(_cpm != null){
//					System.out.println("_cmp != null");
//					if(_cpm._playersMod[_plrPos] != null){
//						System.out.println("_cpm._playersMod[_plrPos] != null");
//						_cpm._playersMod[_plrPos].setSelected(true);
//					}
//				}
//		     if(namePlateActive != null){
//		    	// System.out.println("active plate painting");
//		     namePlateActive.paintIcon(c, gcopy, 0, 0);
//		     }
		     _icon1.paintIcon(c, gcopy, 0, 0);
		     gcopy.dispose();
	    }
	}

	  protected void refresh() {
	    _owner.repaint(_x, _y, _icon1.getIconWidth(), _icon1.getIconHeight());
	  }

	@Override
	public void run() {
		 if (_currentFrame < STAGE_COUNT){
			 _currentFrame++;
			 _cat.finest("Current timer frame " + _currentFrame);
		 }else{//by rk, for grace time period
//			 _cat.severe("Move timer.run() else "+_currentFrame);
			 //start the grace counter timer
			//_cpm.startGraceCounter(_plrPos);
			// _cpm.stopNextMoveTimer();
			// new Exception("Move timer run()").printStackTrace();
		 }
		 refresh();
	}
	
}
