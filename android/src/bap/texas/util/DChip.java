package bap.texas.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import bap.texas.R;

public class DChip {
	
	public Drawable _chip[];
	public int _x, _y;
	
	public DChip(Context context){
		_chip = new Drawable[55];
		_chip[0] = context.getResources().getDrawable(R.drawable.chip1c);
		_chip[1] = context.getResources().getDrawable(R.drawable.chip5c);
		_chip[2] = context.getResources().getDrawable(R.drawable.chip25c);
		_chip[3] = context.getResources().getDrawable(R.drawable.chip1);
		_chip[4] = context.getResources().getDrawable(R.drawable.chip5);
		_chip[5] = context.getResources().getDrawable(R.drawable.chip25);
		_chip[6] = context.getResources().getDrawable(R.drawable.chip100);
		_chip[7] = context.getResources().getDrawable(R.drawable.chip500);
		_chip[8] = context.getResources().getDrawable(R.drawable.chip1k);
		_chip[9] = context.getResources().getDrawable(R.drawable.chip5k);
		_chip[10] = context.getResources().getDrawable(R.drawable.chip25k);
		_chip[11] = context.getResources().getDrawable(R.drawable.chip100k);
		_chip[12] = context.getResources().getDrawable(R.drawable.chip500k);
		_chip[13] = context.getResources().getDrawable(R.drawable.chip1m);
		_chip[14] = context.getResources().getDrawable(R.drawable.chip5m);
		
	}
	
	
}
