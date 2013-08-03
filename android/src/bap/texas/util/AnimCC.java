package bap.texas.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import bap.texas.R;

public class AnimCC {


	static Drawable _ccanim[];
	public static final int WIDTH=71;
	public static final int HEIGHT=10;
	
	public AnimCC(Context context){
		_ccanim = new Drawable[9];
		_ccanim[0] = context.getResources().getDrawable(R.drawable.cos1);
		_ccanim[1] = context.getResources().getDrawable(R.drawable.cos2);
		_ccanim[2] = context.getResources().getDrawable(R.drawable.cos3);
		_ccanim[3] = context.getResources().getDrawable(R.drawable.cos4);
		_ccanim[4] = context.getResources().getDrawable(R.drawable.cos5);
		_ccanim[5] = context.getResources().getDrawable(R.drawable.cos6);
		_ccanim[6] = context.getResources().getDrawable(R.drawable.cos7);
		_ccanim[7] = context.getResources().getDrawable(R.drawable.cos8);
	}
	
	public Drawable get(int i){
		return _ccanim[i];
	}
	
	
}
