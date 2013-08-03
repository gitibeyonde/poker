package com.onlinepoker.util;


import javax.swing.JFrame;

import com.golconda.message.GameEvent;
import com.onlinepoker.ClientPokerController;
import com.onlinepoker.server.ServerProxy;
import com.poker.game.PokerGameType;

public interface ClientFrameInterface  {

    public void tryCloseRoom();
    public void closeRoom();
    public void closeRoomForce();
    public void closeOpenRoom(String old_tid, GameEvent ges);
    public boolean isWindowClosing();
    public PokerGameType getGameType();
    public int getX();
    public int getY();
    public void setTitle(String str);
    public ServerProxy getLobbyServer();
    public JFrame getFrame();
    public void setFullScreen(boolean bool);
	public JFrame getClientRoomFrame();
    
}
