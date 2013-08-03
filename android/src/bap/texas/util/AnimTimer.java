package bap.texas.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import bap.texas.R;

public class AnimTimer {

	public Drawable _timer[];
	public Drawable _trans;
	public int _curr;
	public static final int WIDTH=80;
	public static final int HEIGHT=7;
	
	public AnimTimer(Context context){
		_timer = new Drawable[16];
		_timer[0] = context.getResources().getDrawable(R.drawable.p1);
		_timer[1] = context.getResources().getDrawable(R.drawable.p2);
		_timer[2] = context.getResources().getDrawable(R.drawable.p3);
		_timer[3] = context.getResources().getDrawable(R.drawable.p4);
		_timer[4] = context.getResources().getDrawable(R.drawable.p5);
		_timer[5] = context.getResources().getDrawable(R.drawable.p6);
		_timer[6] = context.getResources().getDrawable(R.drawable.p7);
		_timer[7] = context.getResources().getDrawable(R.drawable.p8);
		_timer[8] = context.getResources().getDrawable(R.drawable.p9);
		_timer[9] = context.getResources().getDrawable(R.drawable.p10);
		_timer[10] = context.getResources().getDrawable(R.drawable.p11);
		_timer[11] = context.getResources().getDrawable(R.drawable.p12);
		_timer[12] = context.getResources().getDrawable(R.drawable.p13);
		_timer[13] = context.getResources().getDrawable(R.drawable.p14);
		_timer[14] = context.getResources().getDrawable(R.drawable.p15);
		_timer[15] = context.getResources().getDrawable(R.drawable.p16);
		
		_curr = 0;
	}
	
	public Drawable getNext(){
		Drawable d = _timer[_curr];
		Log.w("AnimTimer getNext ", "Curr="+  _curr);
		_curr++;
		if (_curr >= _timer.length) _curr = 0;
		return d;
	}
	
	
	public Drawable getErased(){
		return _trans;
	}
}
