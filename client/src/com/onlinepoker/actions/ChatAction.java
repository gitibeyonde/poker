package com.onlinepoker.actions;

public class ChatAction extends StageAction {

	private String chatString;
	
	public ChatAction(int target, String chatString) { 
		super(CHAT, target);
		this.chatString = chatString;
	}

	public ChatAction(String chatString) { 
		super(CHAT, -1);
		this.chatString = chatString;
	}

	public String getChatString() { return chatString; }
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("CHAT: ").append(chatString).
			append(" > ").append(target);
		return s.toString();
	}
	
}
