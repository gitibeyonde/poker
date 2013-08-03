package com.onlinepoker.lobby.tourny;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import com.onlinepoker.lobby.UsersComponentsFactory;
import com.onlinepoker.models.LobbyListModel;
import com.onlinepoker.models.LobbySitnGoModel;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.SitnGoListModel;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.skin.RoomSkinFactory;
import com.onlinepoker.util.BrowserLaunch;
import com.onlinepoker.util.MessageFactory;
import com.onlinepoker.util.TournamentStructure;
import com.poker.common.interfaces.SitnGoInterface;
import com.poker.game.PokerGameType;

public class SNGTournyLobby extends JFrame implements ListSelectionListener,
    FocusListener {
  static Logger _cat = Logger.getLogger(SNGTournyLobby.class.getName());

  public ServerProxy _serverProxy;
  private SNGTournyController _tournyController = null;
  public LobbySitnGoModel _tableModel = null;
  protected static Vector vTournyClientRooms = new Vector();
  public JFrame _lobbyFrame;
  public String _tid;
  protected DefaultTableModel _tableListModel, _playerListModel, _regListModel;
  private ConcurrentHashMap _tes;
  protected JComponent _tableList, _playerList, _regList;
  protected String _focusedTable;
  public JButton _regButton, _myTableButton, _observeTableButton, _cashierButton, _mainLobbyButton;
  public JPanel _topInfoPanel1, _topInfoPanel2, _mainInfoPanelPart1, _mainInfoPanelPart2, _mainInfoPanelPart3;
  String _limit_type, _game_type;
  RoomSkin skin = null;
  int _capacity;
  protected ResourceBundle bundle;
  protected static Dimension screenSize;
  protected static Dimension frameSize;
  protected static Point framePos;
  public JLabel _label_entrans, _label_remaining, _label_status, _label_start, _label_level, _label_next_level;
  public JLabel _label_largest_stack, _label_avarage_stack, _label_smalest_stack;
  boolean _room_open = false;
  private boolean isClosed = true, mytable_open = false;
  ClientRoom cr = null;
  JFrame frame_cr;
  int _pos = 0;
  protected long _tourny_started = -1;
  
  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(ClientConfig.
    							DEFAULT_TOURNY_SCREEN_SIZE_X,
					            ClientConfig.
					            DEFAULT_TOURNY_SCREEN_SIZE_Y);
    framePos = new Point((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
  }

  public SNGTournyLobby(ServerProxy lobbyServer, LobbySitnGoModel tableModel,
                     JFrame owner) {
    super();
    super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
    setTitle(lobbyServer._name +" - Sit n Go Tournament" );
    updateLookAndFeel();
    _tableModel = tableModel;
    _tid = tableModel.getName();
    _serverProxy = lobbyServer;
    _lobbyFrame = owner;
    //_lobbyFrame.setTitle("play.tournament");
    //_tes = new ConcurrentHashMap();
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
//    	  if(_tournyController._present)
//    		  tryCloseRoom(); // safe exit
//    	  else
    	  {
    		  _serverProxy.removeLobbyModelChangeListener(_tournyController);
    		  //_serverProxy.leaveTable(_tid);
    		  isClosed = true;
    		  dispose();
    	  }
        _cat.fine("windowClosing(WindowEvent e)");
      }
    });

    _tableListModel = new DefaultTableModel(null, new String[] {"Rank", "Player",
                                            "Chips"});
    _playerListModel = new DefaultTableModel(null, new String[] {"Table", "Players",
                                             "Largest Stack", "Smallest Stack"});
    _regListModel = new DefaultTableModel(null, new String[] {"Player", "Chips"});

    //pitch list jtable
    _tableList = createJTable(_tableListModel, 0);
    _playerList = createJTable(_playerListModel, 1);
    _regList = createJTable(_regListModel, 2);

    // --- Room Skin
    skin = RoomSkinFactory.getRoomSkin(tableModel.getPlayerCapacity());
    _tournyController = new SNGTournyController(this, tableModel,
                                             lobbyServer, skin, _tid);
       

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
    _regList.setBounds(559, 304, 182, 180);
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
        	doTableRegistration();
        }
        
      }
    };
    

//    _openTableButton = UsersComponentsFactory.createJBSitTable(buttonListener);
//    //_tournyController.add(_openTableButton);
//    _openTableButton.setEnabled(false);
//    _openTableButton.setName("open_table");

    _regButton = UsersComponentsFactory.createTournyRegButton(buttonListener);
    _regButton.setEnabled(true);
    _regButton.setName("register");
    if(!(_tableModel.getTournamentState() ==  SitnGoInterface.HAND_RUNNING))
    _tournyController.add(_regButton);
    

    _myTableButton = UsersComponentsFactory.createMyTableButton(buttonListener);
    _tournyController.add(_myTableButton);
    _myTableButton.setVisible(false);
    _myTableButton.setName("my_table");

    
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
    _tournyController.add(_observeTableButton);
    _observeTableButton.setName("observe_table");
    if(!(_tableModel.getTournamentState() ==  SitnGoInterface.HAND_RUNNING) || _tournyController._present)
        _observeTableButton.setVisible(false);
    
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
  
  public void refreshSNGTournyLobby(LobbySitnGoModel ltm) 
  {
	  SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd 'at' HH:mm z", Locale.US);
	  updateLabel(_label_entrans, "<B>Entrants: </B> ", " "+ltm.getPlayerCount());
      updateLabel(_label_status, "<B>Status: </B> ", " "+ltm.getStateString());
	  if(ltm.getTournamentState() ==  SitnGoInterface.HAND_RUNNING )
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
		  if(_tournyController._present)_myTableButton.setVisible(true);
		  else _observeTableButton.setVisible(true);
	  }
	  else if(ltm.getTournamentState() ==  SitnGoInterface.TABLE_OPEN )
	  {
		  _regButton.setVisible(true);
		  _room_open = false;
	  }
	  else if(ltm.getTournamentState() ==  SitnGoInterface.TABLE_CLOSED )
	  {
		  _tournyController._pos = new Vector<String>();
		  _observeTableButton.setVisible(false);
		  _myTableButton.setVisible(false);
//		  JOptionPane.showMessageDialog(this,
//                  "Tournament completed, Winners are declared", "INFO",
//                                JOptionPane.INFORMATION_MESSAGE);
	  }
	  //opens the client room when all users seated
	  //System.out.println(_room_open+" Present "+_tournyController._present+"and "+ltm.getPlayerCount()+"--"+ltm.getPlayerCapacity());
	  if(!_room_open && ltm.getPlayerCount() == ltm.getPlayerCapacity() && _tournyController._present)
	  {
		  //System.out.println("Nxt move "+ltm.getNextMoveString());
		  System.out.println("open SNG "+_tournyController._ge.getNextMoveString());
		  cr =  new ClientRoom(_serverProxy, _tournyController._ge, _lobbyFrame); 
		  updateTitle(_tournyController._ge);
		  frame_cr = cr.getFrame();
		  frame_cr.setExtendedState(frame_cr.getExtendedState() | JFrame.ICONIFIED);
		  frame_cr.setExtendedState(frame_cr.getExtendedState() & ~JFrame.ICONIFIED);
		  frame_cr.toFront();
		  frame_cr.requestFocus();
		  _lobbyFrame.toBack();
		  _room_open = true;
		  _regButton.setVisible(false);
		  _tourny_started = System.currentTimeMillis();
		  updateLabel(_label_start, "<B>Stated: </B> ", " "+sdf.format(_tourny_started));
		  try {
				_serverProxy.createActionFactory(_tid);
				  _serverProxy.addObserver(_tid);
				  //_serverProxy.addServerMessageListener(_tid,_tournyController);
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
  }
  
  public JPanel createTopInfoPanel1() 
  {
	//REGULAR = 0; NO_LIMIT = 1; POT_LIMIT = 2; TOURNAMENT = 3;
	_limit_type = _tableModel.getGameLimitType() == 0 ? "FL":
	  				  _tableModel.getGameLimitType() == 1 ? "NL":"PL";	
	_game_type = new PokerGameType(_tableModel.getGameType()).isHoldem()? "Hold'em":"Omaha";
	_capacity = _tableModel.getPlayerCapacity();
	JPanel p= new JPanel();
	p.setLayout(new BorderLayout());
	p.setOpaque(false);
	p.setBounds(280, 25, 200, 100);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JLabel label;
	label = new JLabel("<html><FONT COLOR=WHITE>"+_limit_type+" "+_game_type+"<br> [" +_tableModel.getName()+"] </FONT> </html>",JLabel.CENTER); 
	label.setFont(new Font("Myriad Web", Font.BOLD, 18));
	p.add(label, BorderLayout.NORTH);
	label = new JLabel("<html><FONT COLOR=WHITE>Buy-In: </FONT><FONT COLOR=#00A99D>"+ (_tableModel.isRealMoneyTable()?"€ "+(_tableModel.getMinBuyIn()+" + "+_tableModel.getFee()):_tableModel.getMinBuyIn())+"</FONT></html>",JLabel.CENTER);
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
	label = createLabel("","<B>This is a play money Sit <br> & Go Tournament</B>", 10, 165);
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
	double prize_pool = _tableModel.getMinBuyIn() * _tableModel.getPlayerCapacity();
	label = createLabel("<B>Prize Pool: </B>",+prize_pool+"", 10, 165);p.add(label);
	if(_tableModel.getPlayerCapacity() == 10)
	{
		label = createLabel("Places Paid: ","3", 10, 180);p.add(label);
		label = createLabel("<B>1st</B> "," € "+(prize_pool * 50)/100 , 10, 200);p.add(label);
		label = createLabel("<B>2nd</B> "," € "+(prize_pool * 30)/100, 10, 215);p.add(label);
		label = createLabel("<B>3rd</B> "," € "+(prize_pool * 20)/100, 10, 230);p.add(label);
	}
	else if(_tableModel.getPlayerCapacity() == 6)
	{
		label = createLabel("Places Paid: ","2", 10, 180);p.add(label);
		label = createLabel("<br><B>1st</B> "," € "+(prize_pool * 66.7)/100 , 10, 200);p.add(label);
		label = createLabel("<B>2nd</B> "," € "+(prize_pool * 33.3)/100 +"<br>", 10, 215);p.add(label);
	}
	else if(_tableModel.getPlayerCapacity() == 2)
	{
		label = createLabel("Places Paid: ","1", 10, 180);p.add(label);
		label = createLabel("<br><B>1st</B> "," € "+prize_pool+"<br><br>" , 10, 200);p.add(label);
	}
	but = createButton("<B><I>Prizes/Structure</I></B>");
	but.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	but.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String choice = MessageFactory.getSNGPrizePoolWindow("SNG Prizes / Structure ", _tableModel);
	    	
			
		}
	});
	p.add(but);
	return p;
  }
  
  public JPanel createMainInfoPanelPart2() {
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));  
	p.setOpaque(false);
	p.setBounds(185, 170, 330, 120);
	
	JPanel p1= new JPanel();
	p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
	p1.setOpaque(false);
	p1.setBounds(185, 170, 160, 120);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	JLabel label1,label2;
	label1 = new JLabel("<html><FONT COLOR=WHITE><B>Game: </B></FONT><FONT COLOR=9A9FA3>"+_game_type+"  "+_limit_type+"</FONT> </html>");
	label2 = new JLabel("",_capacity == 2?Utils.getIcon(ClientConfig.ICON_2HANDED):_capacity == 6?Utils.getIcon(ClientConfig.ICON_6HANDED):null,JLabel.LEFT);
	//label.setBounds(10, 165, 150, 15);
	label1.setFont(new Font("Verdana", Font.PLAIN, 11));
	JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	box.setPreferredSize(new Dimension(75, 10));
	box.add(label1);
	box.add(label2);
	p1.add(label1);
	_label_entrans = createLabel("<B>Entrants: </B>",_tableModel.getPlayerCount()+"", 10, 180);p1.add(_label_entrans);
	_label_remaining = createLabel("<B>Max: </B> "," "+_tableModel.getPlayerCapacity(), 10, 200);p1.add(_label_remaining);
	
	JPanel p2= new JPanel();
	p2.setLayout(new BoxLayout(p2,BoxLayout.Y_AXIS));
	p2.setOpaque(false);
	p2.setBounds(320, 170, 170, 120);
    //p.setFont(new Font("Verdana", Font.PLAIN, 11));
	_label_status = createLabel("<B>Status: </B>",_tableModel.getStateString()+"", 10, 165);p2.add(_label_status);
	_label_start = createLabel("<B>Starts: </B>","When "+_tableModel.getPlayerCapacity()+" Register", 10, 180);p2.add(_label_start);
	p1.setAlignmentY(TOP_ALIGNMENT);
	p2.setAlignmentY(TOP_ALIGNMENT);
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
	  label.setBounds(x, y, 150, 15);
	  label.setFont(new Font("Verdana", Font.PLAIN, 11));
	  return label;
  }
  
  public void updateLabel(JLabel label , String str1, String str2)
  {
	  label.setText("<html><FONT COLOR=WHITE>"+str1+" </FONT><FONT COLOR=9A9FA3>"+str2+" </FONT> </html>");
	  label.setVisible(true);
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
    return _tableModel;
  }
  protected LobbyListModel getSitnGoListModel() {
    LobbyListModel sitngo_model = new SitnGoListModel();
    return sitngo_model;
  }
  protected ClientRoom createClientRoom(LobbyTableModel tableModel) {
    ClientRoom cr = new ClientRoom(_serverProxy, tableModel, _lobbyFrame);
    return cr;
  }
  public JComponent createJTable(DefaultTableModel tableModel, int index) {
    _cat.fine("createJTable : Creating tables ");
    TableSorter sorter;
    tableModelListener tml;
    sorter = new TableSorter(tableModel);
    tml = new tableModelListener(sorter);
    tableModel.addTableModelListener(tml);
    
    JTable jtable = new JTable(sorter)
	{
    	public Component prepareRenderer(TableCellRenderer renderer,int row, int col) 
    	{
			Component comp = super.prepareRenderer(renderer, row, col);
	        //even index, selected or not selected
	        if (row % 2 == 0){// && !isCellSelected(Index_row, Index_col)) {
	          comp.setBackground(new Color(243, 243, 244));
	        } 
	        else {
	          comp.setBackground(new Color(231, 232, 233));
	        }
	        return comp;
	    }
    	public boolean isCellEditable(int row, int col) {
			return false;
		}
    };
    
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
        break;
      default:
        throw new IllegalStateException("Bad table");
    }

    JTableHeader header = new JTableHeader( jtable.getColumnModel() );
	    
    ((JComponent)header.getDefaultRenderer()).setOpaque(false);
    header.setFont(new Font("Verdana", Font.PLAIN, 10));
    header.setForeground(Color.WHITE);
    header.setReorderingAllowed(false);
    header.setResizingAllowed(true);
    header.setBorder(new EmptyBorder(0, 0, 0, 0));
    //header.addMouseListener(new HeaderListener(header,rendererAT));
    
    jtable.setTableHeader( header );
    jtable.setFont(new Font("Arial", Font.PLAIN, 11));
    
    //jtable.getSelectionModel().addListSelectionListener(this);
    jtable.addFocusListener(this);
    jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jtable.setRowSelectionAllowed(true);
    jtable.setColumnSelectionAllowed(false);
    jtable.setCellSelectionEnabled(false);
    jtable.setDragEnabled(false);
    jtable.setOpaque(false);
    jtable.setShowHorizontalLines(false);
    jtable.setShowVerticalLines(false);
    jtable.setShowGrid(false);
    jtable.setIntercellSpacing(new Dimension(0,0));
    tml.setJTable(jtable);
    sorter.addMouseListenerToHeaderInTable(jtable);
    //jtable.addMouseListener(this);
    
    JScrollPane scrollPane = new JScrollPane(JScrollPane.
                                             VERTICAL_SCROLLBAR_ALWAYS,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
    //		scrollPane.getVerticalScrollBar().setUI(Utils.getLobbyScrollBarUI());
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

    //    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    // add the infoarea as listener for details
   
    return scrollPane;
  }


 
  
  
  // ListSelectionListener Interface
  public void valueChanged(ListSelectionEvent e) {
    _cat.fine("Foucs owner " + _focusedTable);
    if (e.getSource() instanceof DefaultListSelectionModel) {
      DefaultListSelectionModel lsm = (DefaultListSelectionModel) e.getSource();
      int index = lsm.getLeadSelectionIndex();
      _cat.fine("Selection=" + lsm.getLeadSelectionIndex());
      if (_focusedTable.equals("Games")) {
        _cat.fine("Game list selected " + _tableListModel.getValueAt(index, 0));
         GameEvent ge = (GameEvent) _tes.get((String)_tableListModel.getValueAt(index, 0));
         if (ge != null){
           LobbyTableModel lobbyModel = new LobbyTableModel(ge);
           // create a action registry entry
           _serverProxy.createActionFactory(ge.getGameName(), -1);
           ClientRoom cr = new ClientRoom(_serverProxy, lobbyModel, _lobbyFrame);
         }
         else {
           throw new IllegalStateException("missing ge " + _tableListModel.getValueAt(index, 0));
         }
      }
      else if (_focusedTable.equals("Game_Players")) {

      }
      else if (_focusedTable.equals("Player_Listing")) {
        _cat.fine("Player list selected ");
      }

    }
  }
  public boolean isWindowClosing() {
	    return isClosed;
	  }

  public void focusGained(FocusEvent e) {
    if (e.getSource() instanceof JTable) {
      JTable st = (JTable) e.getSource();
      _focusedTable = st.getName();
    }
  }

  public void focusLost(FocusEvent e) {
  }

  public void doTableRegistration() {
    try {
    	if(!_tournyController._present)
    	{
	    	String choice = MessageFactory.getSNGRegisterWindow("SNG Registration", _tableModel,
	    			_tableModel.isRealMoneyTable()?_serverProxy.realWorth():_serverProxy.playWorth());
			if(choice.equals("buyin"))
			{
				_pos = 0;
				for (int i = 0; _tournyController._pos != null && i < 10; i++) 
		        {
					if(_tournyController._pos.contains(""+i));
					else 
					{
						_pos = i;
						break;
					}
		        }
				System.out.println("player join position: "+_pos);
				_serverProxy.createActionFactory(_tid);
				_serverProxy.addObserver(_tid);
				_serverProxy.joinTable(_tid, _pos, _tableModel.getTournyChips());
				_serverProxy.getTableList();
			}	
    	}
    	else
    	{
    		//clear the position vector when click on unregister
    		_tournyController._pos.clear();
    		_serverProxy.leaveTable(_tid);
        	_serverProxy.stopWatchOnTable(_tid);
    		_serverProxy.getTableList();
    	}
    }
    catch (Exception e) {
    	_cat.severe("Unable to regiseter " +  e.getMessage());
    }
    _cat.fine("Register " + _tid);
  }


  public void observeTable() {
    try 
    {
    	if(cr == null || cr.isWindowClosing())
    	{
    		_serverProxy.createActionFactory(_tid);
            _serverProxy.addObserver(_tid);
	      	cr = new ClientRoom(_serverProxy, _tournyController._ge, _lobbyFrame);
	      	mytable_open = true;
    	}
    }
    catch (Exception e) {
      _cat.severe("Unable to Observe table " + e.getMessage());
      _cat.fine("My Table " + _tid);
    }

    
  }
  
  public void myTable() {
    try 
    {
    	if(cr == null || cr.isWindowClosing())
        {
    		_serverProxy.addObserver(_tid);
    		_serverProxy.createActionFactory(_tid);
            _serverProxy.joinTable(_tid, _pos, _tableModel.getTournyChips());
			cr = new ClientRoom(_serverProxy, _tournyController._ge, _lobbyFrame);
	      	mytable_open = true;
    	}
    }
    catch (Exception e) {
      _cat.severe("Unable to goto my table " + e.getMessage());
      _cat.fine("My Table " + _tid);
    }

    
  }

  public void addTable(String rank, String player, String chips) {
    _tableListModel.addRow(new String[] {rank, player, chips});
    
  }

  public void resetTable() {
    for (int i = _tableListModel.getRowCount() - 1; i >= 0; i--) {
      try {
		_tableListModel.removeRow(i);
	} catch (Exception e) {
		
	}
    }
  }

  public void addPlayer(String id, String name, String largest_stack, String small_stack) {
    _playerListModel.addRow(new String[] {id, name, largest_stack, small_stack});
  }

  public void resetPlayer() {
    for (int i = _playerListModel.getRowCount() - 1; i >= 0; i--) {
      _playerListModel.removeRow(i);
    }
  }

  public void addRegPlayer(String name, String points) {
    try {
		_regListModel.addRow(new String[] {name, points});
	} catch (Exception e) {
		
	}
    
  }

  public void resetRegPlayer() {
    for (int i = _regListModel.getRowCount() - 1; i >= 0; i--) {
      try {
		_regListModel.removeRow(i);
		} catch (Exception e) {
			
		}
    }
  }

  public void tryCloseRoom() {
	  JOptionPane.showMessageDialog(this,
              "You can not close the tournament while running, if you close you may not able to join", "WARNING",
      JOptionPane.WARNING_MESSAGE);
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
