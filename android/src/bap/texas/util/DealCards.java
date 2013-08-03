package bap.texas.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import bap.texas.ActivityTable;
import bap.texas.Painter;
import bap.texas.RoomSkin;

public class DealCards extends Painter {
	
	int l,t,r,b;
	Drawable _d;
	DCard _dc;
	int _pos;
	public int _turn = 0;
	private static RoomSkin _skin;
	//Skin._ccx + i * Skin.CARD_WIDTH, Skin._ccy, Skin._ccx + (i +1) * Skin.CARD_WIDTH, Skin._ccy + Skin.CARD_HEIGHT);
	
	public DealCards(RoomSkin s, Context c, ActivityTable p){
		super(s, c, p);
		_skin = s;
		_dc = new DCard(c);
	}
	
	public void anim(int pos){
		_pos = pos;
		Point p = _skin.getPlayerCoordinates(_pos);
		
	}

	@Override
	public void paint(Canvas c){
		int cardPos = _turn == 0? 0 : 28;
		Drawable d = _dc._cards[0]; 
		d.setBounds(l,t,r,b);
		d.draw(c);
	}

}
