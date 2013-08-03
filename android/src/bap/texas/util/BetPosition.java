package bap.texas.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import bap.texas.ActivityTable;
import bap.texas.Painter;
import bap.texas.RoomSkin;

public class BetPosition extends Painter{
	

		public String _name;
		public double _value;
		public int _x, _y;
		DChip _dp;

		public static double _VALUES[]={ 1, 5, 25, 100, 500, 2500, 10000, 50000, 100000, 500000, 2500000, 10000000, 50000000, 100000000, 500000000}; 
		
		public BetPosition(RoomSkin s, Context c, ActivityTable p){
			super(s, c, p);
			_name="";
			_value=0.0;
	    	_dp = new DChip(c);
		}
		
		// calculate chips in the pot
		public int[] getValue(){
			int value = (int)(_value * 100.00);
			Log.i("pot values",value +", "+_value );
			int chipsValue[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Chips amount at 1c 5c 25c 1$ 5$ 25$ 100$ 500$ 100k$ 500k$ 2500k$ 10000k$ 50000k$ 100000k$ 500000$k		
			for (int i = _VALUES.length - 1; i >= 0; i--) {
		      if ((int)(value / _VALUES[i]) != 0) {
		        chipsValue[i] += (int)(value / _VALUES[i]);
		        value -= (int)(value / _VALUES[i]) * _VALUES[i];
		        Log.i("pot","Chipvalue"+chipsValue[i]+"VALUE"+value);
		      }
		    }
			return chipsValue;
		}

		@Override
		public void paint(Canvas c) {
			int px = _x;
			int py = _y;
			int ca[] = getValue();
			Log.i("coins length Total",""+ca.length);
			for (int i=0;i<ca.length;i++){
				
				int cnt = ca[i];
				if (cnt > 0){
					Log.i("coins length",""+i);
					int ppy = py;
					for (int j=0;j<cnt;j++){ //draw that many chips
						_dp._chip[i].setBounds(px, ppy, px + RoomSkin.CHIP_WIDTH, ppy + RoomSkin.CHIP_HEIGHT);
						//if()
						ppy -= 4;
						_dp._chip[i].draw(c);
					}
//					if(i%2==0 && i!=0)
//					{
//						py -= Skin.CHIP_HEIGHT;
//						px -= (Skin.CHIP_WIDTH)*2;
//					}
//					else
//					{
						px += RoomSkin.CHIP_WIDTH;
//					}
				}
			}
			Paint paint = new Paint();
			paint.setTypeface(Typeface.SERIF);
			paint.setColor(Color.WHITE);
			paint.setTextSize(12);
			c.drawText("$" + _value, px + 2, py + 10, paint);
			//c.drawText("$" + _value, px - 10, py + 22, paint);
		}

		
	}

