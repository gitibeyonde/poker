package bap.texas;

import android.graphics.Point;

public class Skin {
	public int _maxPlayer=9;
	public static int _height;
	public static int _width;

	public static final int BOX_WIDTH = 97;
	public static final int BOX_HEIGHT = 73;
	public static final int AVATAR_WIDTH = 67;
	public static final int AVATAR_HEIGHT = 77;
	public static final int COMM_CARD_WIDTH = 37;
	public static final int COMM_CARD_HEIGHT = 54;
	public static final int CARD_WIDTH = 33;
	public static final int CARD_HEIGHT = 40;
	public static final int POT_WIDTH = 46;
	public static final int POT_HEIGHT = 43;
	public final static int CHIP_WIDTH=18;
	public final static int CHIP_HEIGHT=15;
	public static final int BUBBLE_WIDTH = 87;
	public static final int BUBBLE_HEIGHT = 14;
	public static final int BANG_WIDTH = 20;
	public static final int BANG_HEIGHT = 30;
	public static final int DBUT_WIDTH = 17;
	public static final int DBUT_HEIGHT = 17;
	public static final int SEAT_WIDTH = 50;
	public static final int SEAT_HEIGHT = 50;

	public static int _dx;
	public static int _dy;
	
	public static int _ccx;
	public static int _ccy;
	
	public static int _potx;
	public static int _poty;
	
	public static int _roundx;
	public static int _roundy;

	public static int _winx;
	public static int _winy;
	
	public Skin(int mp){
		_maxPlayer = mp;
	}
	

	public int getSP(int pos){
		int start;
		if (TableView._me != null){
			start = TableView._me._pos;
			int rel_pos = pos - start;
			if (rel_pos < 0)rel_pos += _maxPlayer;
			return rel_pos;
		}
		else {
			return pos;
		}
	}
	
	public void setDimensions(int w, int h){
		_height = h;
		_width = w;
		_dx =  _width/2;
		_dy = _height/8;
		_ccx = (int)(_width/2 - COMM_CARD_WIDTH * 2.5);
		_ccy = _height/2 - COMM_CARD_HEIGHT*2/3 ;
		_potx = _width/2 - POT_WIDTH -15;
		_poty = _height/2 + POT_HEIGHT/2 ;
		_roundx = _ccx + 125;
		_roundy = _ccy - 5;
		_winx = _potx - 50;
		_winy = _poty + 30;
	}
	
	public Point getPlayerCoordinates(int pos){
		switch(getSP(pos)){
			case 0:
				return new Point(_width/2 - AVATAR_WIDTH/2 - 32, _height - AVATAR_HEIGHT - 25);
			case 1:
				return new Point(AVATAR_WIDTH*2 + 28 , _height*2/3 - AVATAR_HEIGHT + 60);
			case 2:
				return new Point(5, _height/3 - AVATAR_HEIGHT + 93);
			case 3:
				return new Point(5, _height/4 + 10);
			case 4:
				return new Point(_width/5 - 22, 22);
			case 5:
				return new Point(_width *2/5 - 5, 18);
			case 6:
				return new Point(_width*3/6 - 60 , 22);
			case 7:
				return new Point(_width*4/5 - 10, _height/4 + 10);
			case 8: 
				return new Point(_width*4/5 - 10, _height/3 - AVATAR_HEIGHT + 93);
			case 9:
				return new Point(_width - AVATAR_WIDTH -90,  _height*2/3 - AVATAR_HEIGHT + 60);
			default:
				return new Point(-1, -1);
		}
	}
	
	
}
