package bap.texas;

import android.graphics.Point;

public class SkinSix extends RoomSkin{

	
	public SkinSix(int mp) {
		super(mp);
		// TODO Auto-generated constructor stub
		
		Point playerCoordinatesS[]={
				 new Point(127,206)
				,new Point(39,101)
				,new Point(127,07)
				,new Point(276,07)
				,new Point(377,101)
				,new Point(276,206)
		};
		playerCoordinates = playerCoordinatesS;
		
		
		Point coinCoordinatesS[]={
				 new Point(191,184)
				,new Point(142,139)
				,new Point(191,86)
				,new Point(293,86)
				,new Point(348,139)
				,new Point(293,184)
				
		};
		coinCoordinates = coinCoordinatesS;
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
