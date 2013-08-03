package bap.texas;

import android.content.Context;
import android.graphics.Canvas;

public abstract class Painter {
	RoomSkin _skin;
	Context _context;
	ActivityTable _proxy;
	
	public Painter(RoomSkin s, Context c, ActivityTable p){
		_skin = s;
		_context = c;
		_proxy = p;
	}
	
	
	public abstract void paint(Canvas c);
	

}
