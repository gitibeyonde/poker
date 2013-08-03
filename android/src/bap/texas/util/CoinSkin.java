package bap.texas.util;

import android.graphics.Point;
import bap.texas.RoomSkin;

public class CoinSkin {
	
	public  Point getCoinCoordinates(int coordnate_x)
	{
		switch(coordnate_x)
		{
		case 196:
			return new Point(RoomSkin._width/2 - RoomSkin.AVATAR_WIDTH/2 - 32 + RoomSkin.BOX_WIDTH/2, RoomSkin._height - RoomSkin.AVATAR_HEIGHT - 55);
		case 78:
			return new Point(RoomSkin.AVATAR_WIDTH*2 + 28 + RoomSkin.BOX_WIDTH*2/3 , RoomSkin._height*2/3 - RoomSkin.AVATAR_HEIGHT + 60 - 30);
		case 5:
			return new Point(5 + RoomSkin.BOX_WIDTH + 15, RoomSkin._height/3 - RoomSkin.AVATAR_HEIGHT + 93 + RoomSkin.BOX_HEIGHT/4);
		case 6:
			return new Point(6 + RoomSkin.BOX_WIDTH + 15, RoomSkin._height/4 + 10 + RoomSkin.BOX_HEIGHT/4);
		case 74:
			return new Point(RoomSkin._width/5 - 22 + RoomSkin.BOX_WIDTH/2, 22 + RoomSkin.BOX_HEIGHT - 15);
		case 187:
			return new Point(RoomSkin._width *2/5 - 5 + RoomSkin.BOX_WIDTH/2, 18 + RoomSkin.BOX_HEIGHT - 15);
		case 180:
			return new Point(RoomSkin._width*3/6 - 60 + RoomSkin.BOX_WIDTH/2, 22 + RoomSkin.BOX_HEIGHT - 15);
		case 374:
			return new Point(RoomSkin._width*4/5 - 10 - 20 , RoomSkin._height/4 + 10 + RoomSkin.BOX_HEIGHT/2);
		case 373: 
			return new Point(RoomSkin._width*4/5 - 11 - 20,RoomSkin._height/3 - RoomSkin.AVATAR_HEIGHT + 93 + RoomSkin.BOX_HEIGHT/2);
		case 365:
			return new Point(RoomSkin._width - RoomSkin.AVATAR_WIDTH - 90 + RoomSkin.BOX_WIDTH/2,  RoomSkin._height*2/3 - RoomSkin.AVATAR_HEIGHT + 60 - 15);
		default:
			return new Point(-1, -1);	
		}
	}
	
	
}
