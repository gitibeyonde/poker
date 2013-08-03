package bap.texas;

import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.SurfaceHolder;
import bap.texas.util.AnimTimer;

public class ConnectTimer extends TimerTask
{
	SurfaceHolder _holder;
Context _context;
	int _x, _y;
	AnimTimer _d;
	
	ConnectTimer(){
	
		_x = 20;
		_y = 30;
		Context _context = null;
		_d = new AnimTimer(_context);
	}
	
	@Override
	public void run() {
		Drawable d = _d.getNext();
		d.setBounds(_x, _y, _x + AnimTimer.WIDTH, _y + AnimTimer.HEIGHT);
		Canvas canvas = _holder.lockCanvas(new Rect(_x, _y, _x + AnimTimer.WIDTH, _y + AnimTimer.HEIGHT));
		d.draw(canvas);
		_holder.unlockCanvasAndPost(canvas);
	}
	
	public void clear(){
	}
}