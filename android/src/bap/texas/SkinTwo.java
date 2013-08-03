package bap.texas;

import android.graphics.Point;
import android.util.Log;

public class SkinTwo extends RoomSkin{

	
	public SkinTwo(int mp) {
		super(mp);
		// TODO Auto-generated constructor stub
	
	Log.e("width,height",_width+","+_height);
	
		Point playerCoordinatesT[]={
				new Point(134,210)
				,new Point(246,8)
		};
		playerCoordinates = playerCoordinatesT;
		
		Point coinCoordinatesT[]={
				
				new Point(176,192)
				,new Point(270,83)
				
		};
		
		coinCoordinates = coinCoordinatesT;
	}
	
	@Override
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
	
	@Override
	public Point getPlayerCoordinates(int pos){
		return playerCoordinates[pos];
	}
	
	@Override
	public Point getCoinCoordinates(int pos) {
		// TODO Auto-generated method stub
		return coinCoordinates[pos];
	}
	
}
