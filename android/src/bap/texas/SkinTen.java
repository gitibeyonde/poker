package bap.texas;

import android.graphics.Point;

public class SkinTen extends RoomSkin{

	public SkinTen(int mp) {
		super(mp);
		// TODO Auto-generated constructor stub
		
		Point playerCoordinatesT[] = {
				new Point(134,210)
				,new Point(19,187)
				,new Point(8,105)
				,new Point(25,27)
				,new Point(136,8)
				,new Point(246,8)
				,new Point(359,27)
				,new Point(379,105)
				,new Point(364,187)
				,new Point(249,210)
		};
		playerCoordinates = playerCoordinatesT;
		
		
		Point coinCoordinatesT[]= {
				new Point(176,192)
				,new Point(120,176)
				,new Point(112,135)
				,new Point(128,85)
				,new Point(195,83)
				,new Point(270,83)
				,new Point(334,89)
				,new Point(352,136)
				,new Point(336,176)
				,new Point(280,192)
		};
		
		coinCoordinates = coinCoordinatesT;
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
