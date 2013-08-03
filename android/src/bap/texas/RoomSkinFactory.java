package bap.texas;

public class RoomSkinFactory {

	public static RoomSkin getRoomSkin(int mp){
		if (mp==2) return new SkinTwo(mp);
		else if (mp==6) return new SkinSix(mp);
		else  return new SkinTen(mp);
	}
	
}
