package com.onlinepoker.skin;

public class RoomSkinFactory {

	public static RoomSkin getRoomSkin(int mp){
		if (mp==2) return new RoomSkinTwo(mp);
		else if (mp==4) return new RoomSkinFour(mp);
		else if (mp==6) return new RoomSkinSix(mp);
		else if (mp==8) return new RoomSkinEight(mp);
		else if (mp==9) return new RoomSkinNine(mp);
		else  return new RoomSkinTen(mp);
	}
	/****TEST FOR SINGLETON OBJECT
	 * public static RoomSkin rs2;
	public static RoomSkin rs4;
	public static RoomSkin rs6;
	public static RoomSkin rs8;
	public static RoomSkin rs9;
	public static RoomSkin rs10;
	private RoomSkinFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public static RoomSkin getRoomSkin(int mp){
		if (mp==2){if(rs2 == null){ rs2 = new RoomSkinTwo(mp);} return rs2;}
		else if (mp==4){if(rs4 == null) { rs4 = new RoomSkinFour(mp);} return rs4;}
		else if (mp==6){if(rs6 == null) { rs6 = new RoomSkinSix(mp);} return rs6;}
		else if (mp==8) {if(rs8 == null){ rs8 = new RoomSkinEight(mp);} return rs8;}
		else if (mp==9) {if(rs9 == null){ rs9 = new RoomSkinNine(mp);} return rs9;}
		else{if(rs10 == null)  { rs10 = new RoomSkinTen(mp);} return rs10;}
	}*/

}
