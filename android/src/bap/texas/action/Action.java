package bap.texas.action;

import java.util.HashMap;

import bap.texas.common.message.GameEvent;


public class Action {
	public static final int COMMUNITY_CARD_ACTION=0;
	public static final int DELAY_ACTION=1;
	public static final int LAST_MOVE_ACTION=2;
	public static final int NEW_GAME_ACTION=3;
	public static final int NEXT_MOVE_ACTION=4;
	public static final int PLAYER_DETAIL_ACTION=5;
	public static final int POT_ACTION=6;
	public static final int ROUND_ACTION=7;
	public static final int TABLE_DETAIL_ACTION=8;
	public static final int OPEN_HAND_ACTION=9;
	public static final int WINNER_ACTION=10;
	public static final int NEW_TABLE_ACTION=11;
	public static final int JOIN_FAILED_ACTION=12;
	public static final int NONEXISTENT_GAME_ACTION=13;
	public static final int POCKET_CARD_ACTION=14;
	public static final int CLEAR_MOVES_ACTION=15;
	public static final int CARD_DEAL_ACTION=16;
	public static final int CHAT_MESSAGE_ACTION=17;
	
	public int _id;
	public String _data;
	
	public Action(int id, GameEvent ge){
		_id = id;
		_data = ge.toString();
	}
	public Action(int id, String s){
		_id = id;
		_data = s;
	}
	public Action(int id){
		_id = id;
		_data = "NONE";
	}
	public Action(String s){
		this(parseNVPair(s));
	}
	
	public Action(HashMap<String, String> hm){
		_id = Integer.parseInt(hm.get("ID"));
		_data = hm.get("DATA");
	}
	
	public int getId(){
		return _id;
	}
	
	public String getData(){
		return _data;
	}
	
	public GameEvent getGameEvent(){
		return new GameEvent(_data);
	}
	
	public String toNVPair(){
		return "ID=" + _id + "&DATA=" + _data;
	}

	 public static HashMap<String, String> parseNVPair(String url) {
	    HashMap<String, String> h = new HashMap<String, String>();
	    if (url == null) {
	      return h;
	    }
	    String decoded_url = url; //URLDecoder.decode(url);
	    String[] nv = decoded_url.split("&");
	    for (int i = 0; i < nv.length; i++) {
	      int ind = nv[i].indexOf("=");
	      h.put(nv[i].substring(0, ind), nv[i].substring(ind + 1));
	    }
	    return h;
	 }
	 
	 public String toString(){
		 return toNVPair();
	 }
	 
}
