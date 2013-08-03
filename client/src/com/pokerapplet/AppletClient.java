package com.pokerapplet;


import java.awt.BorderLayout;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.golconda.message.GameEvent;
import com.onlinepoker.BottomPanel;
import com.onlinepoker.ClientPokerController;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.skin.RoomSkinFactory;
import com.onlinepoker.util.ClientFrameInterface;
import com.poker.game.PokerGameType;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;


public class AppletClient extends JApplet implements ClientFrameInterface {
    static Logger _cat = Logger.getLogger(AppletClient.class.getName());
    private ServerProxy _lobbyServer;
    private ClientPokerController controller = null;
    private BottomPanel bottomPanel = null;
    //private ClientCasinoModel _casinoModel = null;
    protected RoomSkin skin = null;
    public PokerGameType _type;
    private boolean isClosed = true;
    public Component _lobbyFrame;
        


    public void init() {
        System.out.println("<<<<<<<<<<<<<<INIT CALLED>>>>>>>>>>>>>>>>");
//        String port = "8985";
//        String user = "abhi";
//        String passwd = "abhi";
//        String ip = "66.220.9.100"; 
//        String sge="name=Green Diamonds,type=1,max-bet=0,max-players=10,min-players=2,min-bet=50";
        //String sge="name=Droid_Indy,max-players=9,type=1,min-players=2,min-bet=0.1,max-bet=0.2";
        String port = getParameter("PORT");
        String ip = getParameter("IP");
        String user = getParameter("USER");
        String passwd = getParameter("PASSWD");
        String sge = getParameter("ge");
        System.out.println("Params=" + ip + port + user + passwd + sge);
        GameEvent ge = new GameEvent(sge);
        try {
            _lobbyServer = 
                ServerProxy.getInstance(ip, Integer.parseInt(port), this);
            if (user==null || user.equals("null")){
                //watch mode
                _lobbyServer.startWatch(ge);
            }
            else if (_lobbyServer.login(user, passwd) == null) {
                JOptionPane.showMessageDialog(this, 
                  "Login Failed for " + user + ".", 
                  "ERROR", JOptionPane.ERROR_MESSAGE);
                _cat.severe("Login Failed for " + user);
            }
            
            updateLookAndFeel();
            _lobbyFrame = this;

            _type = new PokerGameType(ge.getType());
            // --- Room Skin
            RoomSkin skin = RoomSkinFactory.getRoomSkin(ge.getMaxPlayers());

            //--- Bottom Panel
            this.bottomPanel =  new BottomPanel(skin);
            //--- Main class
            controller = new ClientPokerController(ge, skin, bottomPanel, this);
            // --- Add exception catch !!!
    	    getContentPane().setLayout(new BorderLayout(0, 0));
    	    //getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    	    getContentPane().add(controller, BorderLayout.CENTER);
    	    controller.setLayout(null);
    	    bottomPanel.setBounds(0, 450, 800, 150);
    	    controller.add(bottomPanel);
    	    
            _lobbyServer.addServerMessageListener(ge.getGameName(), controller);
            _lobbyServer.addServerActionListener(ge.getGameName(), controller);
                                                 
            isClosed = false;
            setVisible(true);
            _cat.finest("CREATED CLIENT ROOM");
            
        }
        catch (Exception e){                
            JOptionPane.showMessageDialog(this, 
              "Unable to connect to server, please connect after some time.", 
              "ERROR", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
                _cat.severe("Unable to connect to server, please connect after some time. " + user);
        }
            
    }
    
    public void start(){
        
    }

    public void stop(){
        try {
            _cat.info("real close Room");
            isClosed=true;
            _cat.finest("tableserver is not null");
            _lobbyServer.stopWatchOnTable(controller.getModel().name);
            _lobbyServer.leaveTable(controller.getModel().name);
            _lobbyServer.logout();
            bottomPanel.stopTimers();
            //close server proxy
            ServerProxy.killInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    protected void updateLookAndFeel() {
        try {
        	UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
        } catch (Exception ex) {
            _cat.log(Level.SEVERE, "Failed loading L&F: (ClientRoom)", ex);
        }
    }


    public ServerProxy getLobbyServer() {
        return _lobbyServer;
    }

    public PokerGameType getGameType() {
        return _type;
    }

    public void tryCloseRoom() {
        controller.tryExit();
        stop();
    }
    
    public void closeRoom(){}


    public void markWindowAsClosed() {
        isClosed = true;
        //controller.bottomPanel.stopTimers();
    }
    
    public boolean isWindowClosing(){
        return isClosed;
    }
    
    public void setTitle(String str){
        
    }

	@Override
	public JFrame getClientRoomFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFullScreen(boolean bool) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void closeOpenRoom(String oldTid, GameEvent ges) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeRoomForce() {
		// TODO Auto-generated method stub
		
	}

}

