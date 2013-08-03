package bap.texas;

import android.graphics.Point;

public class CoinSkin {
	
	public  Point getCoinCoordinates(int pos)
	{
		switch(pos)
		{
		case 0:
			return new Point(Skin._width/2 - Skin.AVATAR_WIDTH/2 - 32 + Skin.BOX_WIDTH/2, Skin._height - Skin.AVATAR_HEIGHT - 55);
		case 1:
			return new Point(Skin.AVATAR_WIDTH*2 + 28 + Skin.BOX_WIDTH*2/3 , Skin._height*2/3 - Skin.AVATAR_HEIGHT + 60 - 30);
		case 2:
			return new Point(5 + Skin.BOX_WIDTH + 15, Skin._height/3 - Skin.AVATAR_HEIGHT + 93 + Skin.BOX_HEIGHT/4);
		case 3:
			return new Point(5 + Skin.BOX_WIDTH + 15, Skin._height/4 + 10 + Skin.BOX_HEIGHT/4);
		case 4:
			return new Point(Skin._width/5 - 22 + Skin.BOX_WIDTH/2, 22 + Skin.BOX_HEIGHT +5);
		case 5:
			return new Point(Skin._width *2/5 - 5 + Skin.BOX_WIDTH/2, 18 + Skin.BOX_HEIGHT + 5);
		case 6:
			return new Point(Skin._width*3/6 - 60 + Skin.BOX_WIDTH/2, 22 + Skin.BOX_HEIGHT + 5);
		case 7:
			return new Point(Skin._width*4/5 - 10 - 20 , Skin._height/4 + 10 + Skin.BOX_HEIGHT/2);
		case 8: 
			return new Point(Skin._width*4/5 - 10 - 20,Skin._height/3 - Skin.AVATAR_HEIGHT + 93 + Skin.BOX_HEIGHT/2);
		case 9:
			return new Point(Skin._width - Skin.AVATAR_WIDTH - 90 + Skin.BOX_WIDTH/2,  Skin._height*2/3 - Skin.AVATAR_HEIGHT + 60 - 15);
		default:
			return new Point(-1, -1);	
		}
	}
	
	
}
