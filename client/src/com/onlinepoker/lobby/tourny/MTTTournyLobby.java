package com.onlinepoker.lobby.tourny;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.golconda.message.GameEvent;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.ClientRoom;
import com.onlinepoker.PokerRoomColorTheme;
import com.onlinepoker.SharedConstants;
import com.onlinepoker.Utils;
import com.onlinepoker.lobby.TableSorter;
import com.onlinepoker.lobby.UserTableStringCellRenderer;
import com.onlinepoker.lobby.UsersComponentsFactory;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.LobbyTournyModel;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.skin.RoomSkinFactory;
import com.onlinepoker.util.BrowserLaunch;
import com.onlinepoker.util.MessageFactory;
import com.onlinepoker.util.TournamentStructure;
import com.poker.common.interfaces.TournyInterface;


public class MTTTournyLobby extends JFrame implements 
    MouseListener {
  static Logger _cat = Logger.getLogger(MTTTournyLobby.class.getName());

  public ServerProxy _serverProxy;
  private MTTTournyController _tournyController = null;
  private LobbyTournyModel _tournyTableModel = null;
  public static ConcurrentHashMap<String, ClientRoom> vTournyClientRooms = new ConcurrentHashMap<String, ClientRoom>();
  public JFrame _lobbyFrame;
  ClientRoom cr ;
  public String _tid;
  protected DefaultTableModel _tableListModel, _playerListModel, _regListModel;
  public ConcurrentHashMap _tes;
  protected JComponent _tableList, _playerList, _regList;
  protected String _focusedTable;
  public JButton _regButton, _myTableButton, _openTableButton, _observeTableButton, _satelliteLobbyButton, _cashierButton, _mainLobbyButton, _refreshButton;
  public JLabel _label_entrans, _label_remaining, _label_status, _label_start, _label_level, _label_next_level;
  public JLabel _label_largest_stack, _label_avarage_stack, _label_smalest_stack;
  public JPanel _topInfoPanel1, _topInfoPanel2, _mainInfoPanelPart1, _mainInfoPanelPart2, _mainInfoPanelPart3;
  String _limit_type, _game_type;
  int _capacity;
  private boolean isClosed = true;
  protected long _tourny_started = -1;
  private GameEvent select_table = null;
  protected ResourceBundle bundle;
  protected static Dimension screenSize;
  protected static Dimension frameSize;
  protected static Point framePos;
  
  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(ClientConfig.
    							DEFAULT_TOURNY_SCREEN_SIZE_X,
					            ClientConfig.
					            DEFAULT_TOURNY_SCREEN_SIZE_Y);
    framePos = new Point((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
  }

  public MTTTournyLobby(ServerProxy lobbyServer, LobbyTournyModel tournyTableModel,
                     JFrame owner) {
    super();
    super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
    setTitle(lobbyServer._name +" - MTT Tournament" );
    updateLookAndFeel();
    _tournyTableModel = tournyTableModel;
    _tid = tournyTableModel.getName();
    _serverProxy = lobbyServer;
    _lobbyFrame = owner;
    //_lobbyFrame.setTitle("play.tournament");
    _tes = new ConcurrentHashMap();
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
    	_tournyController._tdetailTimer.cancel();
        tryCloseRoom(); // safe exit
        dispose();
        isClosed = true;
        _cat.fine("windowClosing(WindowEvent e)");
      }
    });

    _tableListModel = new DefaultTableModel(null, new String[] {"Id", "Name",
                                            "Level"});
    _playerListModel = new DefaultTableModel(null, new String[] {"Table", "Players",
            								"Largest Stack", "Smallest Stack"});
    _regListModel = new DefaultTableModel(null, new String[] {"S No", "Name",
                                          	"Points"});

    //pitch list jtable
    _tableList = createJTable(_tableListModel, 0);
    _playerList = createJTable(_playerListModel, 1);
    _regList = createJTable(_regListModel, 2);

    // --- Room Skin
    RoomSkin skin = RoomSkinFactory.getRoomSkin(10);
    _tournyController = new MTTTournyController(this, tournyTableModel,
                                             lobbyServer, skin, _tid);
    _serverProxy.addWatchOnTourny(_tid, _tournyController);
	 

    // --- Add exception catch !!!
    getContentPane().setLayout(new BorderLayout(0, 0));
    getContentPane().add(_tournyController, BorderLayout.CENTER);

    _tournyController.add(_tableList);
    _tableList.setOpaque(false);
    _tableList.setBounds(17, 304, 207, 240);
    //_tableListModel.addRow(new String[] {"Id", "Name", "Level"});
    

    _tournyController.add(_playerList);
    _playerList.setBounds(232, 304, 318, 240);
    //_playerListModel.addRow(new String[] {"S No", "Name", "Points"});
    

    _tournyController.add(_regList);
    _regList.setBounds(559, 304, 182, 182);
    //_regListModel.addRow(new String[] {"S No", "Name", "Points"});
    

    ActionListener buttonListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JButton j = (JButton) e.getSource();
        _cat.fine(j.getName());
        if (j.getName().equals(_myTableButton.getName())) {
            myTable();
        }
        else if (j.getName().equals(_observeTableButton.getName())) {
            observeTable();
        }
        else if (j.getName().equals(_mainLobbyButton.getName())) {
          _lobbyFrame.toFront();
        }
        else if (j.getName().equals(_cashierButton.getName())) {
        	BrowserLaunch.openURL(bundle.getString("cashier.url"));
        }
        else if (j.getName().equals(_regButton.getName())) {
          doTournyRegistration();
        }
        
      }
    };
    

//    _openTableButton = UsersComponentsFactory.createJBSitTable(buttonListener);
//    //_tournyController.add(_openTableButton);
//    _openTableButton.setEnabled(false);
//    _openTableButton.setName("open_table");

    _regButton = UsersComponentsFactory.createTournyRegButton(buttonListener);
    _regButton.setName("register");
    _tournyController.add(_regButton);
    

    _myTableButton = UsersComponentsFactory.createMyTableButton(buttonListener);
    _myTableButton.setVisible(false);
    _myTableButton.setName("my_table");
    _tournyController.add(_myTableButton);
    
    
    _cashierButton = UsersComponentsFactory.createCashierButton(buttonListener);
    _tournyController.add(_cashierButton);
    _cashierButton.setEnabled(true);
    _cashierButton.setName("cashier");
    
    _mainLobbyButton = UsersComponentsFactory.createMainLobbyButton(buttonListener);
    _tournyController.add(_mainLobbyButton);
    _mainLobbyButton.setEnabled(true);
    _mainLobbyButton.setName("mainLobby");
    
//    _satelliteLobbyButton = UsersComponentsFactory.createSatelliteLobbyButton(buttonListener);
//    _tournyController.add(_satelliteLobbyButton);
//    _satelliteLobbyButton.setEnabled(true);
    
    _observeTableButton = UsersComponentsFactory.createObserveTableButton(buttonListener);
    _observeTableButton.setVisible(false);
    _observeTableButton.setName("observe_table");
    _tournyController.add(_observeTableButton);
    
    _topInfoPanel1 = createTopInfoPanel1();
    _tournyController.add(_topInfoPanel1);
    
    _topInfoPanel2 = createTopInfoPanel2();
    _tournyController.add(_topInfoPanel2);
    
    _mainInfoPanelPart1 = createMainInfoPanelPart1();
    _tournyController.add(_mainInfoPanelPart1);
    
    _mainInfoPanelPart2 = createMainInfoPanelPart2();
    _tournyController.add(_mainInfoPanelPart2);
    
    _mainInfoPanelPart3 = createMainInfoPanelPart3();
    _tournyController.add(_mainInfoPanelPart3);
    
    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);

    setResizable(false);
    setVisible(true);
    _cat.fine("TOURNY LOBBY CREATED");
    isClosed = false;
    
    repaint();
  }
  
  
  public void refreshMTTTournyLobby(LobbyTournyModel ltm) 
  {
	  SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd 'at' HH:mm z", Locale.US);
	  updateLabel(_label_entrans, "<B>Entrants: </B> ", " "+ltm.getTournamentPlayerCount());
      updateLabel(_label_remaining, "<B>Reamining: </B> ", " "+(ltm.getTournamentMaxPlayers() - ltm.getTournamentPlayerCount()));
	  updateLabel(_label_status, "<B>Status: </B> ", " "+ltm.getStateString());
	  if(ltm.getState() ==  TournyInterface.RUNNING)
	  {
		  if(ltm.getTournamentLevel() != -1)
		  {
			  updateLabel(_label_level, "<B>Level "+ltm.getTournamentLevel()+":</B> ", " "+TournamentStructure._sng_blind[ltm.getTournamentLevel()][0]);
			  updateLabel(_label_next_level, "<B>Next Level: </B> ", " "+TournamentStructure._sng_blind[ltm.getTournamentLevel()+1][0]);
		  }
		  if(_tournyController._largest_stack > 0)
		  {
			  updateLabel(_label_largest_stack, "<br><br><B>Largest: </B> ", " "+_tournyController._largest_stack);
		      updateLabel(_label_avarage_stack, "<B>Avarage: </B> ", " "+(_tournyController._largest_stack +_tournyController._smalest_stack) / 2);
			  updateLabel(_label_smalest_stack, "<B>Smallest: </B> ", " "+_tournyController._smalest_stack);
		  }
		  updateLabel(_label_remaining, "<B>Reamining: </B> ", " "+(ltm.getPlayerCapacity() - ltm.getPlayerCount()));
		  if(_tourny_started != -1)updateLabel(_label_start, "<B>Stated: </B> ", " "+sdf.format(_tourny_started));
			
	  }
  }
  
  
  public JPanel createTopInfoPanel1() 
  {
	_limit_type = _tournyTableModel.getTournamentLimit() == 0 ? "PL":
		_tournyTableModel.getTournamentLimit() == -1 ? "NL":"FL";	
	_game_type = _tournyTableModel.getTournamentType();
	_capacity = _tournyTableModel.getPlayerCapacity();
	JPanel p= new JPanel();
	p.setLayout(new BorderLayout());
	p.setOpaque(false);
	p.setBounds(280, 25, 200, 100);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JLabel label;
	label = new JLabel("<html><FONT COLOR=WHITE>"+_limit_type+" "+_game_type+"<br> [" +_tournyTableModel.getName()+"] </FONT> </html>",JLabel.CENTER); 
	label.setFont(new Font("Myriad Web", Font.BOLD, 18));
	p.add(label, BorderLayout.NORTH);
	label = new JLabel("<html><FONT COLOR=WHITE>Buy-In: </FONT><FONT COLOR=#00A99D>"+ _tournyTableModel.getTournamentBuyIn()+" + €"+_tournyTableModel.getTournamentFee()+"</FONT></html>");
	label.setFont(new Font("Myriad Web", Font.BOLD, 16));
	p.add(label,BorderLayout.SOUTH);
	return p;
  }
  
  public JPanel createTopInfoPanel2() {
	JPanel p= new JPanel();
	//p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
	p.setOpaque(false);
	p.setBounds(520, 40, 200, 80);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JLabel label;
	label = createLabel("","<B>Description from Back-office </B>", 10, 165);
	label.setFont(new Font("Verdana", Font.PLAIN, 12));
	p.add(label);
	return p;
  }

  public JPanel createMainInfoPanelPart1() {
	JPanel p= new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
	p.setOpaque(false);
	p.setBounds(10, 170, 170, 120);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JButton but;
	JLabel label;
	double prize_pool = _tournyTableModel.getTournamentBuyIn() * _tournyTableModel.getTournamentMaxPlayers();
	label = createLabel("<B>Prize Pool: </B>",+prize_pool+"", 10, 165);p.add(label);
	//if(_tournyTableModel.getMaxPLayers() == 10)
	{
		label = createLabel("Places Paid: ","3", 10, 180);p.add(label);
		label = createLabel("<B>1st</B> "," € "+(prize_pool * 50)/100 , 10, 200);p.add(label);
		label = createLabel("<B>2nd</B> "," € "+(prize_pool * 30)/100, 10, 215);p.add(label);
		label = createLabel("<B>3rd</B> "," € "+(prize_pool * 20)/100, 10, 230);p.add(label);
	}
	but = createButton("<B><I>Prizes/Structure</I></B>");
	but.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	but.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String choice = MessageFactory.getMTTPrizePoolWindow("Tournament Prizes/ Structure ", _tournyTableModel);
	    	
		}
	});
	p.add(but);
	return p;
  }
  
  public JPanel createMainInfoPanelPart2() {
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));  
	p.setOpaque(false);
	p.setBounds(185, 167, 330, 120);
	
	JPanel p1= new JPanel();
	p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
	p1.setOpaque(false);
	p1.setBounds(185, 167, 160, 110);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JButton but;
	JLabel label;
	//label = createLabel("<B>Game: </B>",_tournyTableModel.getName().substring(_tournyTableModel.getName().indexOf("_")+1)+_tournyTableModel.getGameLimitType()+"", 10, 165);p1.add(label);
	label = new JLabel("<html><FONT COLOR=WHITE><B>Game: </B></FONT><FONT COLOR=9A9FA3>"+_game_type+"  "+_limit_type+"</FONT> </html>", _capacity == 2?Utils.getIcon(ClientConfig.ICON_2HANDED):_capacity == 6?Utils.getIcon(ClientConfig.ICON_6HANDED):null,JLabel.LEFT);
	label.setFont(new Font("Verdana", Font.PLAIN, 11));
	p1.add(label);
	_label_entrans = createLabel("<B>Entrants: </B>",_tournyTableModel.getTournamentPlayerCount()+"", 10, 180);p1.add(_label_entrans);
	_label_remaining = createLabel("<B>Remaining: </B> ",(_tournyTableModel.getTournamentMaxPlayers() - _tournyTableModel.getTournamentPlayerCount())+"" , 10, 200);p1.add(_label_remaining);
	label = createLabel("<B>Max: </B> ","3000 ", 10, 215);p1.add(label);
	label = createLabel("<br><B>Total Rebuys: </B> "," 0", 10, 230);p1.add(label);
	label = createLabel("<B>Total Add-Ons: </B> "," 0", 10, 225);p1.add(label);
	
	JPanel p2= new JPanel();
	p2.setLayout(new BoxLayout(p2,BoxLayout.Y_AXIS));
	p2.setOpaque(false);
	p2.setBounds(320, 167, 170, 120);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	_label_status = createLabel("<B>Status: </B>",_tournyTableModel.getStateString()+"", 10, 165);p2.add(_label_status);
	label = createLabel("<B>Start: </B>",_tournyTableModel.getSchedule()+"", 10, 180);p2.add(label);
	label = createLabel("<B>Late Reg: </B> "," 0", 10, 200);p2.add(label);
	label = createLabel("<B>Running: </B> "," 0", 10, 215);p2.add(label);
	label = createLabel("<B>Ended: </B> "," 0", 10, 230);p2.add(label);
	but = createButton("<B><I>Rebuy Info</I></B>");
	but.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	but.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String choice = MessageFactory.getRebuyInfoWindow("Tournament Rebuy Info ", "sdfsd");
	    	
		}
	});
	p2.add(but);
	
	p.add(p1);
	p.add(p2);
	return p;
  }
  
  public JPanel createMainInfoPanelPart3() {
	JPanel p= new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
	p.setOpaque(false);
	p.setBounds(530, 170, 170, 120);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JButton but;
	JLabel label;
	_label_level = createLabel("<B>Level 1: </B>",""+TournamentStructure._sng_blind[0][0], 10, 165);p.add(_label_level);
	_label_next_level = createLabel("<B>Next Level</B> "," ", 10, 180);p.add(_label_next_level);
	_label_next_level.setVisible(false);
	_label_largest_stack = createLabel("<br><br><B>Largest: </B> "," ", 10, 245);p.add(_label_largest_stack);
	_label_largest_stack.setVisible(false);
	_label_avarage_stack = createLabel("<B>Avarage: </B> "," ", 10, 260);p.add(_label_avarage_stack);
	_label_avarage_stack.setVisible(false);
	_label_smalest_stack = createLabel("<B>Smallest: </B> "," ", 10, 275);p.add(_label_smalest_stack);
	_label_smalest_stack.setVisible(false);
	return p;
  }
  
  public JLabel createLabel(String str1, String str2, int x, int y)
  {
	  JLabel label =new JLabel("<html><FONT COLOR=WHITE>"+str1+" </FONT><FONT COLOR=9A9FA3>"+str2+" </FONT> </html>"); 
	  label.setBounds(x, y, 150, 10);
	  label.setFont(new Font("Verdana", Font.PLAIN, 11));
	  return label;
  }
  
  public void updateLabel(JLabel label , String str1, String str2)
  {
	 label.setText("<html><FONT COLOR=WHITE>"+str1+" </FONT><FONT COLOR=9A9FA3>"+str2+" </FONT> </html>");
	
  }
  
  
  public JButton createButton(String str)
  {
	  JButton button = new JButton("<html><FONT COLOR=00A99D>"+str+" </FONT></html>",null);
	  button.setFont(new Font("Verdana", Font.PLAIN, 11));
	  button.setOpaque(false);
	  button.setBorderPainted(false);
	  button.setFocusPainted(false);
	  button.setContentAreaFilled(false);
	  return button;
  }
  
  public LobbyTableModel getLobbyTableModel() {
    return _tournyTableModel;
  }

  
  public JComponent createJTable(DefaultTableModel tableModel, int index) {
    _cat.fine("createJTable : Creating tables ");
    JTable jtable = null;
    TableSorter sorter;
    tableModelListener tml;
    sorter = new TableSorter(tableModel);
    tml = new tableModelListener(sorter);
    tableModel.addTableModelListener(tml);
    //JTable jtable = new JTable(sorter);
    
    jtable = new JTable(sorter)
	{
    	public boolean isCellEditable(int rowIndex, int colIndex) {
    		return false;   //Disallow the editing of any cell
        }
    	public Component prepareRenderer(TableCellRenderer renderer,int row, int col) 
    	{
			Component comp = null;
			try {
				comp = super.prepareRenderer(renderer, row, col);
				if (row % 2 != 0){// && !isCellSelected(Index_row, Index_col)) {
				  comp.setBackground(new Color(243, 243, 244));
				} 
				else {
				  comp.setBackground(new Color(231, 232, 233));
				}
				String type = (String)getModel().getValueAt(row, 1);
				if(type.equals(_serverProxy._name))
				{
					comp.setForeground(Color.BLUE);
				}
				else 
				{
					comp.setForeground(Color.BLACK);
				}
			} catch (Exception e) {
				
			}
	        return comp;
	    }
    	
    	
    };
    
    
    UserTableStringCellRenderer cellRenderer = new UserTableStringCellRenderer();
    jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    switch (index) {
      case 0:
        jtable.setName("Games");
        break;
      case 1:
        jtable.setName("Game_Players");
        jtable.getColumnModel().getColumn(0).setPreferredWidth(60);
        jtable.getColumnModel().getColumn(1).setPreferredWidth(60);
        jtable.getColumnModel().getColumn(2).setPreferredWidth(99);
        jtable.getColumnModel().getColumn(3).setPreferredWidth(99);
        break;
      case 2:
        jtable.setName("Player_Listing");
        jtable.getColumnModel().getColumn(0).setPreferredWidth(45);
        jtable.getColumnModel().getColumn(1).setPreferredWidth(85);
        jtable.getColumnModel().getColumn(2).setPreferredWidth(52);
        break;
      default:
        throw new IllegalStateException("Bad table");
    }

    JTableHeader header = new JTableHeader( jtable.getColumnModel() )
    {
	    public void paintComponent(Graphics g)
	    {
		    // Scale image to size of component
		    Dimension d = getSize();
		    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_TLOBBY_TABLE_HEADER_BG);
		    g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
		    setOpaque( false );
		    super.paintComponent(g);
		    }	
	    };
	    
    ((JComponent)header.getDefaultRenderer()).setOpaque(false);
    header.setFont(new Font("Verdana", Font.PLAIN, 10));
    header.setForeground(Color.WHITE);
    header.setReorderingAllowed(false);
    header.setResizingAllowed(true);
    header.setBorder(new EmptyBorder(0, 0, 0, 0));
    //header.addMouseListener(new HeaderListener(header,rendererAT));
    
    //jtable.getSelectionModel().addListSelectionListener(this);
    jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jtable.setTableHeader( header );
    jtable.setFont(new Font("Arial", Font.PLAIN, 11));
    jtable.setDefaultRenderer(String.class, cellRenderer);
    jtable.setRowSelectionAllowed(true);
    jtable.setColumnSelectionAllowed(false);
    jtable.setCellSelectionEnabled(false);
//    jtable.getColumnModel().getColumn(0).setPreferredWidth(25);
//    jtable.getColumnModel().getColumn(2).setPreferredWidth(30);
    //jtable.setBackground(new Color(9, 40, 71));// blue bg
    //jtable.setForeground(new Color(0xFFFFFF));
    jtable.setDragEnabled(false);
    jtable.setOpaque(false);
    jtable.setShowHorizontalLines(false);
    jtable.setShowVerticalLines(false);
    jtable.setShowGrid(false);
    jtable.setIntercellSpacing(new Dimension(0,0));
    tml.setJTable(jtable);
    sorter.addMouseListenerToHeaderInTable(jtable);
    jtable.addMouseListener(this);
    
    
    
    JScrollPane scrollPane = new JScrollPane(JScrollPane.
                                             VERTICAL_SCROLLBAR_ALWAYS,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewport(new JViewport() {
    public void paintComponent(Graphics g) {
      g.drawImage(
    	  Utils.getIcon(ClientConfig.IMG_MTT_TABLE_BG).getImage(),
          0,
          0,
          
          Utils.getIcon(ClientConfig.IMG_MTT_TABLE_BG).getIconWidth(),
          Utils.getIcon(ClientConfig.IMG_MTT_TABLE_BG).getIconHeight(),
          this);
    }
    });
    scrollPane.setViewportView(jtable);
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

    JLabel upper_right_corner_label = new JLabel(Utils.getIcon(ClientConfig.IMG_TLOBBY_TABLE_HEADER_BG));
    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, upper_right_corner_label);
    //scrollPane.addMouseListener(this);
	scrollPane.getViewport().setOpaque(false);
    return scrollPane;
  }

  
  //MouseListner Interface :
  public void mouseClicked(MouseEvent e) {
	  if (e.getSource() instanceof JTable) {
	  JTable jTable = (JTable) e.getSource();
	  String table = getSelectedTableByJTable(jTable);
	  _serverProxy.createActionFactory(table);
	  try {
		_serverProxy.addWatchOnTourny(_tid, _tournyController);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	  GameEvent ge = (GameEvent)_tes.get(table);
	  if (e.getClickCount() > 1) {
		 if(ge != null)
		 {
			if(vTournyClientRooms.get(_tid) == null)
			{
	    		_serverProxy.addWatchOnTourny(_tid, _tournyController);
	    		cr = new ClientRoom(_serverProxy, ge, _lobbyFrame); 
	    		vTournyClientRooms.put(_tid, cr);
	    	}
	    	else
	    	{
	    		Frame frames[] = vTournyClientRooms.get(_tid).getFrames();
	    		int j=-1;
	    		for (int i = 0; i < frames.length; i++) {
	    			if(frames[i].getTitle().contains(table)){j=i;break;}
	    		}
	    		if(j != -1)
	    		{
	    			System.out.println("frame matched"+frames[j].getName());
	    			frames[j].toFront();
	    		}
	    	}
	    	if(cr != null)updateTitle(ge);
		 }
 	  }
	  else
	  {
		  select_table = ge;
	  }
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
    
  }

  public void mouseReleased(MouseEvent e) {
    
  }
  
  protected String getSelectedTableByJTable(JTable jTable) {
    if (jTable.getModel() instanceof TableSorter) {
      TableSorter sorter = (TableSorter) jTable.getModel();
      DefaultTableModel lobbyListModel = (DefaultTableModel) (sorter.getModel());
      int visibleNo = jTable.getSelectedRow();
      if (visibleNo < 0) {
        return null;
      }
      int realNo = sorter.getRealNo(visibleNo);
      if (realNo >= 0) {
    	String table = (String) lobbyListModel.getValueAt(realNo, 0);
        return table;
      }
    }
    return null;
  }
  
  public boolean isWindowClosing() {
    return isClosed;
  }


  public void doTournyRegistration() {
    try {
      if(!_tournyController._present)
      {
    	String choice = MessageFactory.getMTTRegisterWindow("MTT Registration", _tournyTableModel,
    			_tournyTableModel.isRealMoneyTable()?_serverProxy.realWorth():_serverProxy.playWorth());
		if(choice.equals("buyin"))
			  	  _serverProxy.tournyRegister(_tid);
      }
      else
        _serverProxy.tournyUnRegister(_tid);
        
    }
    catch (Exception e) {
      _cat.severe("Unable to regiseter " +  e.getMessage());
    }
    _cat.fine("Register " + _tid);
  }

  public void observeTable() {
    try {
    	if(vTournyClientRooms.get(_tid) == null)
		{
    		_serverProxy.createActionFactory(_tid);
    		_serverProxy.addWatchOnTourny(_tid, _tournyController);
    		cr = new ClientRoom(_serverProxy, select_table, _lobbyFrame); 
    		vTournyClientRooms.put(_tid, cr);
    	}
    	else
    	{
    		Frame frames[] = vTournyClientRooms.get(_tid).getFrames();
    		int j=-1;
    		for (int i = 0; i < frames.length; i++) {
    			if(frames[i].getTitle().contains(_tid)){j=i;break;}
    		}
    		if(j != -1)
    		{
    			System.out.println("frame matched"+frames[j].getName());
    			frames[j].toFront();
    		}
    	}
	}
    catch (Exception e) {
      _cat.severe("Unable to goto my table " + e.getMessage());
    }

    _cat.fine("My Table " + _tid);
  }
  
  public void myTable() {
    try {
    		_serverProxy.addWatchOnTourny(_tid, _tournyController);
    		 _serverProxy.createActionFactory(_tid);
   		  _serverProxy.tournyMyTable(_tid);
   		
    }
    catch (Exception e) {
      _cat.severe("Unable to goto my pitch " + e.getMessage());
    }

    _cat.fine("My Table " + _tid);
  }

  public void refresh() {
    try {
      _serverProxy.tournyDetails(_tid);
    }
    catch (Exception e) {
      _cat.severe("Unable to get tourny details " + e.getMessage());
    }

    _cat.fine("My Table " + _tid);
  }

  public void addTable(String id, String name, String level, GameEvent ge) {
    _tableListModel.addRow(new String[] {id, name, level});
    if(ge != null)
	{
    	//System.out.println("ht put: "+id+"-----"+ge.getGameName());
    	_tes.put(id, ge);
	}
  }

  public void resetTable() {
	while(_tableListModel.getRowCount() > 0)
	{
	  _tableListModel.removeRow(0);
	}
    _tes.clear();
  }

  public void addPlayer(String id, String name, String points) {
    _playerListModel.addRow(new String[] {id, name, points});
  }

  public void resetPlayer() {
	while(_playerListModel.getRowCount() > 0)
	{
		_playerListModel.removeRow(0);
	}
  }

  public void addRegPlayer(String id, String name, String points) {
    try {
		_regListModel.addRow(new String[] {id, name, points});
	} catch (Exception e) {
		
	}
    
  }

  public void resetRegPlayer() {
    while(_regListModel.getRowCount() > 0)
	{
    	try {
			_regListModel.removeRow(0);
		} catch (Exception e) {
			
		}
	}
  }

  public void tryCloseRoom() {
    _cat.info("closeRoom");
  }


  /**
   * Update the system color theme.
   */
  protected void updateLookAndFeel() {
    try {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new
    		  PokerRoomColorTheme());
      //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
    catch (Exception ex) {
      _cat.severe("Failed loading L&F: (ClientRoom)" + ex.getMessage());
    }
  }

    class tableModelListener
  implements TableModelListener {

	private TableSorter sorter;
	private JTable jTable = null;
	
	
	
	tableModelListener(TableSorter sorter) {
	  this.sorter = sorter;
	}
	
	//tableModelListener(RowSorter<M> sorter) {
	//    this.sorter = sorter;
	//  }
	
	public void setJTable(JTable jTable) {
	  _cat.fine("Setting table ....");
	  this.jTable = jTable;
	}
	
	public void tableChanged(TableModelEvent e) {
		  try {
			sorter.sort(this);
		} catch (Exception e1) {
			
		}
		if (jTable != null) {
		    jTable.repaint();
		}
	}
}
    public void updateTitle(GameEvent ge) {
      	
      	String name = ge.getGameName();
      	int _grid = ge.getGameRunId();
      	double minBet = ge.getMinBet();
      	double maxBet = ge.getMaxBet();
    	  StringBuilder sbuf = new StringBuilder();
    	  sbuf.append(ge.getTournyId()).append(" : hand id = [").append(name).append( "-").append(_grid).append("], Stakes=");
    	  sbuf.append("€" ).append(SharedConstants.doubleToString(minBet / 2))
    	  .append("/"+SharedConstants.doubleToString(minBet));
    	  
    	  if (maxBet ==0){
    	      sbuf.append(  " PL,"); 
    	  }
    	  else if (maxBet == -1){
    	      sbuf.append( " NL,");
    	  }
    	  cr.setTitle(sbuf.toString() + "   Good luck " +  _serverProxy._name);
      }
 
}
