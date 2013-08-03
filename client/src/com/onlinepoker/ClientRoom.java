package com.onlinepoker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.golconda.game.util.Card;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;
import com.onlinepoker.lobby.LobbyUserImp;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.server.ActionFactory;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.skin.RoomSkinFactory;
import com.onlinepoker.util.BrowserLaunch;
import com.onlinepoker.util.ClientFrameInterface;
import com.onlinepoker.util.MessageFactory;
import com.poker.game.PokerGameType;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;


public class ClientRoom
    extends JFrame
    implements ClientFrameInterface{

  static Logger _cat = Logger.getLogger(ClientRoom.class.getName());

  private ClientPokerController controller = null;
  private BottomPanel bottomPanel = null;
  protected ServerProxy _serverProxy = null;
  public PokerGameType _type;
  private boolean isClosed = true;
  public JFrame gameBoard;
  public boolean lobby_mode=false;
  public String _name;
  public int _game_type = -1;
  protected JButton jbFullScreen;
  public String WAITING_FOR_PLAYERS_BANNER_STR = "Waiting for players";
  Container _jf = this.getContentPane();
  //resize code
  double _width,_height;
	double ratio_x=1, ratio_y=1;
  protected static Dimension screenSize;
  protected static Dimension frameSize;
  protected static Point framePos;
  protected GameEvent ge;
  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(800, 626);//((int)screenSize.getWidth(), (int)screenSize.getHeight());//800, 626
    framePos = new Point((screenSize.width - frameSize.width) / 2,
				          (screenSize.height - frameSize.height) / 2);
    //framePos =new Point( 0, 0);
    
  }
  public ClientRoom(ServerProxy sp,LobbyTableModel ltm,JFrame owner) {
	super();
	StringBuilder sb = new StringBuilder();
	String ges=sb.append("name=").append(ltm.getName()).append(",max-players=").append(ltm.getPlayerCapacity())
				.append(",type=").append(ltm.getGameType()).append(",min-players=2,min-bet=").append(ltm.getMinBet())
				.append(",max-bet=").append(ltm.getMaxBet()).toString();
	  ge = new GameEvent();
	 ge.init(ges);
	 lobby_mode=true;
	 _name = ge.getGameName();
	 _game_type = ge.getType();
	 _cat.fine("GA=" + ge.toString());
	    super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
	    updateLookAndFeel();
	    setResizable(true);
	    this._serverProxy = sp;
	    this.gameBoard = owner;
	    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	    	    tryCloseRoom(); // safe exit
	        _cat.finest("windowClosing(WindowEvent e)");
	        bottomPanel.leavePlease();
	      }
	      public void windowActivated(WindowEvent arg0) {
	    	if (MessageFactory.dialog != null) {
	          MessageFactory.dialog.toFront();
	        }
	      }
	    });
	    
	    this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				//System.out.println(e);				
			}
			@Override
			public void ancestorResized(HierarchyEvent e) {
				//if(resize){
				    //_serverProxy.tableDetails(_name);
					//resize=false;
				//}
			}			
		});
	    
	    addWindowStateListener(new WindowAdapter() {
	    	public void windowStateChanged(WindowEvent e)
	        {
	    		//System.out.println("windowstatelistener "+convertStateToString(e.getNewState()));
	      	  if(convertStateToString(e.getNewState()).contains("MAXIMIZED_BOTH"))
	      	  {
	      		 System.out.println("Full screen");
	      		  //controller._model.fullscreen = true;
	      		  //setFullScreen(true);
	      	  }
	      	  //else
	      	  //{
	      		//  setFullScreen(false);
	      	  //}
	    	}
		});
	    
	    addKeyListener(new KeyAdapter() {
	    	public void keyPressed(KeyEvent e) {
	    		//System.out.println("keyPressed "+e.getKeyCode());
	    		new Exception().printStackTrace();
	            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	            	setFullScreen(false);
	            }
	    	}
		});
	    this.addComponentListener(new java.awt.event.ComponentAdapter() 
		{
			public void componentResized(ComponentEvent e)
			{
				resizeTable();
			}
		});
	    _type = new PokerGameType(ge.getType());
	    RoomSkin skin = null;
	    skin = RoomSkinFactory.getRoomSkin(ge.getMaxPlayers());
	    //--- Bottom Panel
	    this.bottomPanel = new BottomPanel(skin);
	    //--- Main class
	    controller = new ClientPokerController(ge, skin, bottomPanel, this);
	    // --- Add exception catch !!!
	    getContentPane().setLayout(new BorderLayout(0, 0));
	    //getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	    getContentPane().add(controller, BorderLayout.CENTER);
	    controller.setLayout(new BorderLayout(0, 0));
	    controller.add(bottomPanel, BorderLayout.SOUTH);
	    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
	    _serverProxy.addServerActionListener(ge.getGameName(), controller);
	    _serverProxy.addServerMessageListener(ge.getGameName(), controller);
	    //messageToRoom();//commented by rk
	    if(controller._model.getGameType().isTPoker()){
	    	controller._model.setMessageBannerOnTable(WAITING_FOR_PLAYERS_BANNER_STR);//by rk, to show banner till game start
	    }
	    isClosed = false;
	    setUndecorated(true);
	    setVisible(true);
	    _cat.finest("CREATED CLIENT ROOM " + controller.getHeight());
  }

  public GameEvent getGe(){
	  return this.ge;
  }
   	
  public ClientRoom(ServerProxy sp,
                    GameEvent ge,
                    JFrame owner) {
    super();
    super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
    updateLookAndFeel();
    this._serverProxy = sp;
    this.gameBoard = owner;
	 _name = ge.getGameName();
	 //resize code
	 this.ge = ge;
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        tryCloseRoom(); // safe exit
        _cat.finest("windowClosing(WindowEvent e)");
        bottomPanel.leavePlease();
      }
      
      
      public void windowActivated(WindowEvent arg0) {
        if (MessageFactory.dialog != null) {
          MessageFactory.dialog.toFront();
        }
      }
      
     });
    
    this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){

		@Override
		public void ancestorMoved(HierarchyEvent e) {
			System.out.println(" ancestorMoved");				
		}
		@Override
		public void ancestorResized(HierarchyEvent e) {
			System.out.println(" ancestorResized");
//			Component c = (Component)e.getSource();
//			Dimension d = c.getSize();
			
//			if(isResizable())
//	    	{
//				 _height = getHeight();
//	    		 _width = getWidth();
//	    		 
//	    		 ratio_x = _width/800.00;
//	    		 ratio_y = _height/600.00;
//	    		 
//	    		 controller._skin.setRatio(ratio_x,ratio_y);
//	    		 
//	    		System.out.println("_height:"+_height+",_width:"+_width);
//	    		//repaint();
//	    	}
			
			
		}			
	});
    addKeyListener(new KeyAdapter() {
    	public void keyPressed(KeyEvent e) {
//            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
//            	setFullScreen(false);
//            }
//            
//            if(e.getKeyCode() == KeyEvent.VK_F1){
//            	System.out.println("F1 pressed ");
//            	controller._bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            	//bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            }else if(e.getKeyCode() == KeyEvent.VK_F2){
//            	System.out.println("F2 pressed ");
//            	controller._bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            	//bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            }else if(e.getKeyCode() == KeyEvent.VK_F3){
//            	System.out.println("F3 pressed ");
//            	controller._bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            	//bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            }else if(e.getKeyCode() == KeyEvent.VK_F4){
//            	System.out.println("F4 pressed ");
//            	controller._bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            	//bottomPanel.performActionWhenKeyPressed(e.getKeyCode());
//            }else{
//            	System.out.println("keyPressed "+e.getKeyCode());
//            }
    	}
	});
    this.addComponentListener(new java.awt.event.ComponentAdapter() 
	{
		public void componentResized(ComponentEvent e)
		{
			//System.out.println("resizing "+getWidth()+"X"+getHeight()+","+_width+"X"+_height);
			if(getWidth() != _width || getHeight() != _height){
				resizeTable();
			}
		}
	});
    
    addWindowStateListener(new WindowAdapter() {
    	public void windowStateChanged(WindowEvent e)
        {
    		//System.out.println("windowstatelistener "+convertStateToString(e.getNewState()));
      	  if(convertStateToString(e.getNewState()).contains("MAXIMIZED_BOTH"))
      	  {
      		 System.out.println("Full screen mode");
      		  controller._model.fullscreen = true;
      		  //setFullScreen(true);
      	  }
      	  //else
      	  //{
      		//  setFullScreen(false);
      	  //}
    	}
	});
    
    // --- Room Skin
    _type = new PokerGameType(ge.getType());
    RoomSkin skin = RoomSkinFactory.getRoomSkin(ge.getMaxPlayers());

    //--- Bottom Panel
    this.bottomPanel = new BottomPanel(skin);

    //--- Main class
    controller = new ClientPokerController(ge, skin, bottomPanel, this);
    // --- Add exception catch !!!
    this.bottomPanel.addButtonPanel();//this method is need to add Button when room created newly
    getContentPane().setLayout(new BorderLayout(0, 0));
    //getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    getContentPane().add(controller, BorderLayout.CENTER);
    controller.setLayout(new BorderLayout(0, 0));
    controller.add(bottomPanel, BorderLayout.SOUTH);
    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    _serverProxy.addServerActionListener(ge.getGameName(), controller);
    _serverProxy.addServerMessageListener(ge.getGameName(), controller);
    //messageToRoom();
    if(controller._model.getGameType().isTPoker()){
    	controller._model.setMessageBannerOnTable(WAITING_FOR_PLAYERS_BANNER_STR);//by rk, to show banner till game start
    }
    isClosed = false;
    setResizable(true);
    setUndecorated(true);
    setVisible(true);
    _cat.finest("CREATED CLIENT ROOM " + controller.getHeight());
  }
  
  public void resizeTable(){
		if(isResizable() && screenSize.getWidth()>=getWidth()&&screenSize.getHeight()+40>=getHeight() &&650<=getWidth() && 500<=getHeight())
		{
			_width = getWidth();
			 _height = getHeight()+40;
			 ratio_x = _width/800.00;
			 ratio_y = _height/626.00;
  		     controller._skin.setResizeRatio(ratio_x,ratio_y);
			 RoomSkin skin = null;
			 ActionFactory AF = _serverProxy.getAF(_name);
			 GameEvent glob_ge = null;
			 if(AF != null){
				 glob_ge = AF.getGlobal_ge();
			 }
			 
			 if(glob_ge != null){
				 skin = RoomSkinFactory.getRoomSkin(glob_ge.getMaxPlayers());
				 skin.setResizeRatio(ratio_x,ratio_y);
				 controller.resize(glob_ge,skin);
				 //gameBoard.repaint();
			 }
			/** StringBuffer sb = new StringBuffer();
			 sb.append("##_height: ").append(_height).append(" , ")
			 .append("_width: ").append(_width).append(" , ")
			 .append("ratio_x: ").append(ratio_x).append(" , ")
			 .append("ratio_y: ").append(ratio_y);
			 System.out.println(sb.toString());*/
	    		//reposition player according to VIRTUAL POSITION
	    		if(glob_ge != null){
		    		//System.out.println(globalGE.toString());
					//by rk
					if(AF != null){
						String cpd[] = AF.getClientPlayer(glob_ge);
						ClientPlayerModel plrm = null;
						 if (cpd != null) {
				            plrm = new ClientPlayerModel(cpd);
				            plrm.setPlayerPosition(AF.VIRTUAL(plrm.getPlayerPosition()));
				            plrm.setPlayerRelPosition(AF.VIRTUAL(plrm.getPlayerPosition()));
						 }
						ClientPlayerModel[] cpm = AF.getPlayerModels(glob_ge);
		                if(cpm != null && cpd != null){
		                	//System.out.println("#########################################");
		                	controller.rePositionPlrs(cpm,plrm);
		                	controller._model.refreshMyHand(glob_ge.getHand());
		                	if(glob_ge.getPrevCommunityCards() != null){
		                		controller._model.refreshDeskCards(glob_ge.getPrevCommunityCards());
		                		//_serverProxy.setGlobal_ge(null);
		                	}
		                	
		                	if(cpm != null){
		        				for(int i=0;i<cpm.length;i++){
		        					if(cpm[i] != null){
		        						//System.out.println(i+" - "+_playersMod[i]._name+" - "+_playersMod[i].isSelected());
		        						String nm[][] = glob_ge.getMove();
		        						int sel_pos = -1;
		        				        if (nm != null) {
		        				        	sel_pos = AF.VIRTUAL(Integer.parseInt(nm[0][0]));
		        				        }
		        				        if(sel_pos == i){
		        				        	//System.out.println("in client room selected player "+ controller._model._players[i].getPlayerName()+" - "+controller._model._players[i].getPlayerPosition());
		        				        	//_playersMod[i].setSelected(true);
		        				        	controller._model._players[i].setSelectedResize(true);
		        				        }
		        					}else{
		        						//System.out.println("null came");
		        					}
		        				}
		        				}
		                	int delerPosition = AF.VIRTUAL(glob_ge.getDealerPosition());
		                	if (delerPosition >= 0 && delerPosition < 10) {
		                		//System.out.println("delerPosition "+delerPosition);
		                		controller._model.setDealerChip(delerPosition);
		                		controller._model.setTotalBetBanner(glob_ge);
		         	        }
							
		                	 gameBoard.repaint();
		                }
					}
	    		}
	    		
  	}else{
			if(getWidth()<=650 && getHeight()<=500){
				setSize(650, 500);
	    		 ratio_x = 650/800.00;
	    		 ratio_y =  500/626.00;
			}/*else if(getWidth()>=950 && getHeight()>=750){
				setSize(950, 750);
	    		 ratio_x = 950/800.00;
	    		 ratio_y =  750/626.00;
			}else{
				setSize(800, 626);
				ratio_x = 800/800.00;
				ratio_y =  626/626.00;
			}*/
			/*else{
				setSize((int)screenSize.getWidth(),(int)screenSize.getHeight()-20);
				ratio_x = screenSize.getWidth()/800.00;
				ratio_y = (screenSize.getHeight()-20)/626.00;
			}*/
  		 controller._skin.setResizeRatio(ratio_x,ratio_y);
  		 RoomSkin skin = null;
			 skin = RoomSkinFactory.getRoomSkin(getGe().getMaxPlayers());
			 skin.setResizeRatio(ratio_x,ratio_y);
			 ActionFactory AF = _serverProxy.getAF(_name);
			 GameEvent glob_ge = null;
			 if(AF != null){
				 glob_ge = AF.getGlobal_ge();
			 }
		}
  }

  public String getGameName(){
	  return _name;
  }
 
  public ClientPokerController getClientPokerController() {
    return controller;
  }

  public ClientPokerModel getClientPokerModel() {
    return controller.getModel();
  }

   public PokerGameType getGameType(){
       return _type;
   }


  public ServerProxy getLobbyServer() {
    return _serverProxy;
  }

  public void tryCloseRoom() {
	  controller.tryExit();
  }

  
	@Override
	public void closeOpenRoom(String old_tid, GameEvent ge) {
		//if cond by rk, some times GE came null from server, this cause game hanging
		if( (ge != null && ge.getGameName() != null) || !("".equals(ge.toString().trim()))){
			_serverProxy=_serverProxy == null ? ServerProxy.getInstance() : _serverProxy;
			_cat.finest("tableserver is not null");
			//leaving from old room
			try {
				_serverProxy.leaveTable(old_tid);
				_serverProxy.removeObserver(old_tid);
				ActionFactory oldAF = _serverProxy.getAF(old_tid);
				//by rk
				if(oldAF != null){
					oldAF.resetMoneyAtTable();
					oldAF._joined = false;// since we getting "you already joined table" while joining
					_serverProxy.removeActionFactory(old_tid);//by rk
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			controller._model.removeMessageBanner();//by rk, remove if any otherwise will appear on new table
			controller._model.moveBackground();
			
//			System.out.println("old ge: "+controller._model._prev_name+" | "+controller._model._prev_grid+" #new ge:  "+controller._model.name+" | "+controller._model._grid);
	
			//updating lasthand details
			if(controller._model._grid != -1){
				controller._model._prev_grid = controller._model._grid;
				controller._model._prev_name = controller._model.name;
			}
//			System.out.println(controller._model._prev_name+" | "+controller._model._prev_grid+" # "+controller._model.name+" | "+controller._model._grid);
			// refreshing the room
			_serverProxy.addServerActionListener(ge.getGameName(), controller);
		    _serverProxy.addServerMessageListener(ge.getGameName(), controller);
		    _name = ge.getGameName();
			_game_type = ge.getType();
			RoomSkin skin = RoomSkinFactory.getRoomSkin(ge.getMaxPlayers());
			this.bottomPanel = null;//by rk, for GC
			this.bottomPanel = new BottomPanel(skin);
			this.bottomPanel.quickFoldVisible(false);
			controller._model.doClearDesk();
			controller._model.doSetButton(ge.getDealerPosition());
			controller._model.hideDealerPos();
			controller.updateTitle(ge);
			if(controller._model._grid != -1){
				String _msgBanner = 
					 !(controller._model._infoMsgBanner.equals(""))? controller._model._infoMsgBanner :
					 												 WAITING_FOR_PLAYERS_BANNER_STR;
				if(ge.getNextMoveString().startsWith("-1|wait|"))//if game is not running then show message banner. by rk
					controller._model.setMessageBannerOnTable(_msgBanner);
				controller._model._infoMsgBanner = "";
					
			}else{
					String _msgBanner = 
						 !(controller._model._infoMsgBanner.equals(""))? controller._model._infoMsgBanner :
							 											 WAITING_FOR_PLAYERS_BANNER_STR;
					controller._model.setMessageBannerOnTable(_msgBanner);
					controller._model._infoMsgBanner = "";
			}
			//controller.setPokerModelWhenCloseOpen(ge);
			controller.setPokerModel(ge);
			//controller._realign_plrs = false;
			controller.repaint();
			repaint();
			isClosed = false;
		}else{
			System.out.println("GE="+ge.toString().trim()+"-"+ge);
			
			Timer t= new Timer();
			t.schedule(new TimerTask() {
				
				@Override
				public void run() {
					JOptionPane.showMessageDialog(_jf,
								                  "Server is unable to find a seat for you at this pool", "ERROR",
								                  JOptionPane.ERROR_MESSAGE);
		        	_cat.log(Level.FINE, "Server is unable to find a seat for you at this pool");
		        	_cat.fine("real closeRoom");
		  	      	_serverProxy=_serverProxy == null ? ServerProxy.getInstance() : _serverProxy;
		  	      	_cat.fine("leaving the table "+ controller.getModel().name);
		  	      	_cat.fine("clientroom._name "+_name);
		  	        _serverProxy.leaveTable( controller.getModel().name);
		  	        _serverProxy.stopWatchOnTable(controller.getModel().name);
		  	        _cat.finest("forcedCloseRoom");
		  	        isClosed = true;
		  	        dispose();
				}
			},3000);
			Runtime rt = Runtime.getRuntime();
		    System.out.println("Available Free Memory when close client room: " + rt.freeMemory());
		    System.runFinalization();
		    System.gc();
		    System.out.println("Available Free Memory when close client room: " + rt.freeMemory());
		}
	}

  public void messageToRoom() {
	  if(controller._model.getGameType().isTPoker())
      {
		  controller._model.setMessageBanner(WAITING_FOR_PLAYERS_BANNER_STR);
      }
  }
	  
  public void closeRoom() {
//	  new Exception("########closeRoom() "+controller.getModel().name+", isClosed= "+isClosed
//			  ).printStackTrace();
    _cat.fine("closeRoom");
    if(controller._model.gameType.isTourny())
    {
    	_serverProxy=_serverProxy == null ? ServerProxy.getInstance() : _serverProxy;
    	_cat.finest("tableserver is not null");
        _serverProxy.leaveTable( controller.getModel().name);
        _serverProxy.stopWatchOnTable(controller.getModel().name);
        isClosed = true;
        //controller._model.moveAllNamePlates();
        dispose();
    }
    else if (!isClosed)
    {
	    if (JOptionPane.showInternalConfirmDialog(this.getContentPane(),"Do you want to leave", "Leave", 
	                                              JOptionPane.YES_NO_OPTION) == 
	                                                JOptionPane.YES_OPTION) {
	       //BrowserLaunch.openURL("http://www.supergameasia.com/thanks.html");
	      _cat.fine("real closeRoom");
	      _serverProxy=_serverProxy == null ? ServerProxy.getInstance() : _serverProxy;
	      _cat.finest("tableserver is not null");
	        _serverProxy.leaveTable( controller.getModel().name);
	        _serverProxy.stopWatchOnTable(controller.getModel().name);
	        _serverProxy.removeServerMessageListener(controller.getModel().name);
	        _serverProxy.removeServerActionListener(controller.getModel().name);
	        _cat.finest("forcedCloseRoom");
	        isClosed = true;
	        this.ge = null;
	       // _serverProxy.setGlobal_ge(null);
	        dispose();
	        Runtime rt = Runtime.getRuntime();
		    System.out.println("Available Free Memory when close client room: " + rt.freeMemory());
		    System.runFinalization();
		    System.gc();
		    System.out.println("Available Free Memory when close client room: " + rt.freeMemory());
		    System.out.println("from Browser "+LobbyUserImp.fromBrowser);
		    
		    if(LobbyUserImp.fromBrowser){
		    	//withdraw money 
		    	System.out.println("money at table "+controller.getModel().getPlayerMoneyAtTable()+
		    						",type "+(_type.isReal()?"real" : "play")+
		    						",name "+_serverProxy._name);
		    	//applet to servelt communication takes place here
		    	//BrowserLaunch.openURL(LobbyUserImp.closeURL);
		    	URL url = null;
		    	URLConnection servletConnection = null;
		    	try{
//		    		String urlString = Utils.getURL()+"/s/RefundAmount?uid="+_serverProxy._name
//					+"&type="+(_type.isReal()?"real" : "play")
//					+"&amtOnTable="+controller.getModel().getPlayerMoneyAtTable();
//		    		url = new URL(urlString);
//		    		System.out.println("url** "+urlString);
//		    		servletConnection = url.openConnection();
//		    		servletConnection.setDoInput(true);
//		    		servletConnection.setDoOutput(true);
//		    		servletConnection.setUseCaches(false);
//		    		servletConnection.setDefaultUseCaches(false);
//		    		servletConnection.setRequestProperty("Content-Type","application/octet-stream");
//		    		
//		    		ObjectInputStream input = new ObjectInputStream(servletConnection.getInputStream());
//		    		String str = str = (String)input.readObject();
//		    		System.out.println("str "+str);
		    		BrowserLaunch.openURL(LobbyUserImp.closeURL);
		    		
	    		}
	    		catch(Exception e){
	    		e.printStackTrace();
	    		}
	    		//later remove timer
	    		Timer t= new Timer();
				t.schedule(new TimerTask() {
					
					@Override
					public void run() {
						System.exit(-1);
					}
				},1000);
		    	
		    }
//	        if (!lobby_mode)
//	        		System.exit(0);
	    }
    }
  }
  
  public void closeRoomForce() {
	  new Exception("closeRoomForce").printStackTrace();
    _cat.fine("closeRoom");
    	_serverProxy=_serverProxy == null ? ServerProxy.getInstance() : _serverProxy;
    	_cat.finest("tableserver is not null");
    	_serverProxy.leaveTable( controller.getModel().name);
    	_cat.fine("leaving the table "+ controller.getModel().name);
        _serverProxy.stopWatchOnTable(controller.getModel().name);
        isClosed = true;
        this.ge = null;
        //_serverProxy.setGlobal_ge(null);
        dispose();
        Runtime rt = Runtime.getRuntime();
	    System.out.println("Available Free Memory when close client room: " + rt.freeMemory());
	    System.runFinalization();
	    System.gc();
	    System.out.println("Available Free Memory when close client room: " + rt.freeMemory());
  }

  protected void finalize() throws Throwable {
	    try {
	    } finally {
	        super.finalize();
	    }
	}

  public boolean isWindowClosing() {
    return isClosed;
  }

  /**
   * Update the system color theme.
   */
  protected void updateLookAndFeel() {
    try {
//      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(
//          new PokerRoomColorTheme());
//      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//      //UIManager.setLookAndFeel(new SyntheticaBlackStarLookAndFeel());
    	
    	UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
    	UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
    }
    catch (Exception ex) {
      _cat.log(Level.WARNING, "Failed loading L&F: (ClientRoom)", ex);
    }
  }

  // FOR LOBBY
  public int getWaiterCount() {
	    return 0;
	  }

	  public void setWaiterCount(int waiterCount) {
	    ;
	  }
  
//     public static void main(String[] args) throws Exception {
//           JFrame jf = new JFrame();
//           if (args.length <= 4) {
//               JOptionPane.showInternalMessageDialog(jf.getContentPane(), "Illegal number of arguments.", 
//                                             "ERROR", JOptionPane.ERROR_MESSAGE);
//               _cat.fine("Illegal number of arguments " + args.length);
//               System.exit(-1);
//           }
//           System.setProperty("proxySet", "true");
//           System.setProperty("proxyHost", args[0]);
//           System.setProperty("proxyPort", args[1]);
//           System.setProperty("username", args[2]);
//           System.setProperty("password", args[3]);
//           System.setProperty("ge", args[4]);
//
//           _cat.finest("Game Event = " + args[4]);
//           ServerProxy _serverProxy = 
//               ServerProxy.getInstance(args[0], Integer.parseInt(args[1]), jf);
//           if (_serverProxy.login(args[2], args[3]) == null) {
//               JOptionPane.showInternalMessageDialog(jf.getContentPane(), 
//                                             "Login Failed for " + args[2] + ".", 
//                                             "ERROR", JOptionPane.ERROR_MESSAGE);
//               _cat.fine("Login Failed for " + args[2]);
//               System.exit(-1);
//           }
//           GameEvent ge = new GameEvent(args[4]);
//           ClientRoom cr = new ClientRoom(_serverProxy, ge, jf);
//          
//       }


	@Override
	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return gameBoard;
	}
	
	@Override
	public JFrame getClientRoomFrame() {
		// TODO Auto-generated method stub
		return this;
	}
	
	
	private String convertStateToString(int state) {
        if (state == Frame.NORMAL) {
            return "NORMAL";
        }
        String strState = " ";
        if ((state & Frame.ICONIFIED) != 0) {
            strState += "ICONIFIED";
        }
        //MAXIMIZED_BOTH is a concatenation of two bits, so
        //we need to test for an exact match.
        if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            strState += "MAXIMIZED_BOTH";
        } else {
            if ((state & Frame.MAXIMIZED_VERT) != 0) {
                strState += "MAXIMIZED_VERT";
            }
            if ((state & Frame.MAXIMIZED_HORIZ) != 0) {
                strState += "MAXIMIZED_HORIZ";
            }
        }
        if (" ".equals(strState)){
            strState = "UNKNOWN";
        }
        return strState.trim();
    }
	
	public void setFullScreen( boolean fullscreen )
    {
        //get a reference to the device.
        GraphicsDevice device  = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode dispMode = new DisplayMode(800,600,24,56); 
        //save the old display mode before changing it.
        DisplayMode dispModeOld = device.getDisplayMode();
        if( !fullscreen )
        {
        	device.setDisplayMode(dispModeOld);
        	setVisible(false);
            dispose();
            setUndecorated(false);
            device.setFullScreenWindow(null);
            setLocationRelativeTo(null);
            setAlwaysOnTop(false);
            setResizable(true);
            setSize(800, 626);
            setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    	    setVisible(true);
        }
        else
        { //change to fullscreen.
            setVisible(false);
            dispose();
            setUndecorated(true);
            //make the window fullscreen.
            //Window topLevel = (Window) getTopLevelAncestor();
            int xoffs = 0,yoffs=0;
            device.setFullScreenWindow(this);
            DisplayMode displayMode = device.getDisplayMode();
            dispMode = new DisplayMode(800, 600, 32, displayMode.getRefreshRate());
            device.setDisplayMode(dispMode);
            setResizable(true);
            setAlwaysOnTop(true);
            setPreferredSize(new Dimension(dispMode.getWidth(), dispMode.getHeight()));
            setSize(Toolkit.getDefaultToolkit().getScreenSize());
            //new Dimension(dispMode.getWidth(), dispMode.getHeight()));
            setLocation(xoffs, yoffs);
            setLocationRelativeTo(this);
            validate();
            pack();
            setVisible(true);
            
        }
        //make sure that the screen is refreshed.
        repaint();
    }


	
	
	

}
