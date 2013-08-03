package com.onlinepoker.lobby;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.agneya.util.Base64;
import com.golconda.game.util.ActionConstants;
import com.golconda.message.GameEvent;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.ClientRoom;
import com.onlinepoker.ClientSettings;
import com.onlinepoker.SharedConstants;
import com.onlinepoker.Utils;
import com.onlinepoker.actions.Action;
import com.onlinepoker.exceptions.AuthenticationException;
import com.onlinepoker.lobby.tourny.MTTTournyLobby;
import com.onlinepoker.lobby.tourny.SNGTournyLobby;
import com.onlinepoker.models.HoldemListModel;
import com.onlinepoker.models.LobbyHoldemModel;
import com.onlinepoker.models.LobbyListModel;
import com.onlinepoker.models.LobbyOmahaModel;
import com.onlinepoker.models.LobbySitnGoModel;
import com.onlinepoker.models.LobbyStudModel;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.LobbyTournyModel;
import com.onlinepoker.models.LobbyTournyRealModel;
import com.onlinepoker.models.OmahaListModel;
import com.onlinepoker.models.PokerConstants;
import com.onlinepoker.models.SitnGoListModel;
import com.onlinepoker.models.StudListModel;
import com.onlinepoker.models.TableListModel;
import com.onlinepoker.models.TournamentListModel;
import com.onlinepoker.proxies.LobbyInfoListener;
import com.onlinepoker.proxies.LobbyModelsChangeListener;
import com.onlinepoker.proxies.PingDetailsListener;
import com.onlinepoker.proxies.broadcastMessageListener;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.util.BrowserLaunch;
import com.onlinepoker.util.CredentialManager;
import com.onlinepoker.util.GameTypes;
import com.onlinepoker.util.MessageFactory;
import com.poker.common.message.ResponseTableList;
import com.poker.common.message.TournyEvent;
import com.poker.game.PokerGameType;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;


public class LobbyUserImp
    implements LobbyFrameInterface,
    ListSelectionListener,
    LobbyModelsChangeListener,
    PingDetailsListener,broadcastMessageListener,
    ActionListener,
    MouseListener,
    FocusListener,
    LobbyInfoListener {

  static Logger _cat = Logger.getLogger(LobbyUserImp.class.getName());

////	IDs for automatic open if frame is open to open
  private ResponseTableList _table_list;

  // menu
  protected JMenuBar lobbyMenuBar,loginMenuBar;

//	private JMenu jmSettings;

  protected JCheckBoxMenuItem jmiAutoLogin;
  protected JCheckBoxMenuItem jmiAutoBigBlind;
  protected JCheckBoxMenuItem jmiWaitBigBlind;
  protected JCheckBoxMenuItem jmiShowBestCards;
  protected JCheckBoxMenuItem jmiMuckLoosingCards;
  protected JCheckBoxMenuItem jmiFourColorCards;
  protected JRadioButtonMenuItem jmEnglish, jmDeutsch, jmMagyar, jmSpanish;

//@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
  protected JCheckBoxMenuItem jmiSound;

//  protected JCheckBoxMenuItem jmiRandomDelay;
  ClientSettings settings ;
//---
  protected JTabbedPane tabbedPane = null;
  protected InfoJLabel infoTextArea;
  protected InfoGameTypes infoGameTypes;
  protected ShowTablesFilter showTableFilters;
  protected BtnAddWLorTourney btnAddWLorTourney;
  protected TabPlayerorWL tabplayerorWL;
  protected JLabel pingTextArea;
  protected JLabel adTextArea;

  public static LobbySitnGoModel _sitngoList[] = null;
  public static LobbyTournyModel _tournyList[] = null;
  //Vector<GameEvent> tplist = new Vector<GameEvent>();
  
  public static LobbyTableModel _tableList[];
  public LobbyOmahaModel opltm[];
  public LobbyOmahaModel ohiltm[];
  public LobbyHoldemModel hpltm[];
  public LobbyStudModel spltm[];
  public LobbyStudModel shiltm[];
  public LobbyOmahaModel orltm[];
  public LobbyTournyRealModel trltm[];
  
//--- tables
  protected LobbyListModel mixed_model;
  protected LobbyListModel cg_model;
  protected LobbyListModel tp_model;
  protected LobbyListModel holdem_model;
  protected LobbyListModel omaha_model;
  protected LobbyListModel stud_model;
  protected LobbyListModel sitngo_model;
  protected LobbyListModel holdem_real_model;
  protected LobbyListModel omaha_real_model;
  protected LobbyListModel stud_real_model;
  protected TournamentListModel tournament_model;
  protected TournamentListModel tournament_real_model;
  protected TableSorter holdem_sorter;
  protected JTable holdemTable = null;
  protected JTable omahaTable = null;
  protected JTable studTable = null;
  protected JTable tournamentTable = null;

// settings
  protected LoginSettings loginSettings = new LoginSettings();

// proxy_s
  protected ServerProxy _serverProxy;
  protected static LobbyTableModel[] _latest_changes;
  protected boolean refreshTableList = false;
  protected static String selected_tid = "";
  protected int selected_row = 0;
  protected LobbyTableModel selected_table = null;

// bundle
  protected static ResourceBundle bundle;
  public static Map<String, String> map;

// components
  protected JPanel main_panel;
  protected JScrollPane table_pane,tableScrollPane;
  protected JToggleButton jbWait;
  protected JButton jbSitTable;
  protected JButton jbCashier;
  protected JButton jbFlashAdv;
  protected JLabel jlEasternTime;
  protected JLabel jlPlayersOnline,jlActiveTables;
  protected JLabel jlRealWorth,jlPlayWorth,jlMoneyInPlay;
  protected JLabel jbFFreeRoll,jbFMicro,jbFLow,jbFMedium,jbFHigh;
  protected JRadioButton jrPlay, jrReal, jrPublic, jrPrivate;
  protected boolean isCGFullTables,isSNGMTTRunning,isSNGMTTCmpleted;
  protected JCheckBox isSNGFreerolSel, isMTTFreerolSel;
  protected JCheckBox isCGMicroSel, isSNGMicroSel, isMTTMicroSel;
  protected JCheckBox isCGLowSel, isSNGLowSel, isMTTLowSel;
  protected JCheckBox isCGMediumSel, isSNGMediumSel, isMTTMediumSel;
  protected JCheckBox isCGHighSel, isSNGHighSel, isMTTHighSel;
  protected boolean isRBPlay;
  protected Vector components = new Vector();
  public int gameType_col1, gameType_cg_col2, gameType_cg_col3;
  public int gameType_tp_col2, gameType_tp_col3;
  public int gameType_sng_col2, gameType_sng_col3, gameType_sng_col4;
  public int gameType_mtt_col2,gameType_mtt_col3, gameType_mtt_col4;
  protected static JButton btnAddToWL;
  JToolBar bar1 = new JToolBar(SwingConstants.VERTICAL);
  JToolBar bar2 = new JToolBar(SwingConstants.VERTICAL);
  JToolBar bar3 = new JToolBar(SwingConstants.VERTICAL);
  JToolBar bar4 = new JToolBar(SwingConstants.VERTICAL);
  
  ButtonGroup group1, group2, group3, group4;
  Vector<JCheckBox> vCB = new Vector<JCheckBox>();
  int[] cgFArr = null, sngFArr = null, mttFArr = null;
//private int temp_gameTYpe_col1;

// theme
  protected SyntheticaBlackEyeLookAndFeel blueTheme;

// background
  protected ImageIcon background;

  // Agneya FIX 16
  protected ImageIcon pwIcon;

//--- Vector with all opened windows
  protected Vector vClientRooms = new Vector();
  public static Vector vTPokerRooms = new Vector();
  protected Vector vSNGRooms = new Vector();
  protected Vector vMTTRooms = new Vector();
//	exit listener
  protected WindowListener windowExitListener;
  protected WindowFocusListener windowFocusListener;
  protected CredentialManager credentialManager = new CredentialManager();
  protected JFrame frame;
  protected TableModel cgTableModel, sngTableModel, mttTableModel;
  protected TableSorter sorter;
  protected tableModelListener tml;
  protected JTable jTable;
  UserTableStringCellRenderer cellRenderer;
  final ImageIcon tableBackgroundImage =
      Utils.getIcon(ClientConfig.IMG_TABLE_LIST_BG);
  public LobbyUserImp(String ip) throws Exception {
    //prepare();
    init_player(ip);

    this.bundle = Bundle.getBundle();
    this.blueTheme = createLookAndFeel();
    this.lobbyMenuBar = createMenuBar("lobby");
    this.loginMenuBar = createMenuBar("login");
    this.background = Utils.getIcon(ClientConfig.IMG_LOBBY_BACKGROUND);

    windowExitListener = createExitListener();
    windowFocusListener = createFocusListener();
// --- create components
    initFilters();
    initPlayerInfo();
    initRadioButtons();
    initEasternTime();
    initJBCashier();
    initFlashAdv();
    createMainPanelWithContents();
    
    _serverProxy.addPingDetailsListener(this);
    _serverProxy.addbroadcastMessageListener(this);
    
       // --- create components
  }

  // Executed when the players closes the window
  private WindowListener createExitListener() {
    WindowListener windowExitListener = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        _cat.fine("====================== Lobby frame close");
        if (jmiAutoLogin != null) {
          loginSettings.setAutoLogin(jmiAutoLogin.isSelected());
        }
        loginSettings.saveSettings();
        lobbyExit();
      }

      public void windowActivated(WindowEvent arg0) {
        if (MessageFactory.dialog != null) {
          MessageFactory.dialog.toFront();
        }
      }
    };
    return windowExitListener;
  }
  
  
  private WindowFocusListener createFocusListener() {
  WindowFocusListener windowFocusListener = new WindowFocusListener() {
	
    	@Override
		public void windowLostFocus(WindowEvent arg0) {
//			if(_serverProxy._rtlTimer == null)
//				_serverProxy.startRefreshTableListThread();
		}
		
		@Override
		public void windowGainedFocus(WindowEvent arg0) {
			//_serverProxy.stopRefreshTableListThread();
		}
    };
	      
	    
	    return windowFocusListener;
	  }

  private SyntheticaBlackEyeLookAndFeel createLookAndFeel() {
	  SyntheticaBlackEyeLookAndFeel metalTheme = null;
	try {
		metalTheme = new SyntheticaBlackEyeLookAndFeel();
		setLookAndFeel(metalTheme);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    return metalTheme;
  }

//  private void prepare() {
//    UIManager.put("ScrollBar.width", new Integer(18));
//    this.bundle = Bundle.getBundle();
//  }
  public static boolean fromBrowser= false;
  public String uid, pwd, poolName;
  public static String closeURL;
  double amt;
  public void init(JFrame frame) {
    this.frame = frame;
    // Agneya FIX 16
    pwIcon = Utils.getIcon(ClientConfig.PW_ICON);
    frame.setIconImage(pwIcon.getImage());
  //if cond by rk, for login from browser
    if(fromBrowser == true){
    	try {
            int result = this.loginFromBrowser(this.uid,this.pwd.toCharArray());
            //frame.setVisible(true);
            //System.out.println("login result "+result);
            //System.out.println(poolName);
            if(result != 0){
            _serverProxy.joinPool(poolName, amt); // depedning on the type of game server will buy real
            }
          }
          catch (Exception ex) {
            newLoginFrame();
          }
    }else{
    	createLoginFrameAsNeeded();
    }
  }

  public ImageIcon getBackground() {
    return background;
  }

  public SyntheticaBlackEyeLookAndFeel getLookAndFeel() {
    return blueTheme;
  }

  public JMenuBar getLobbyMenu() {
	return lobbyMenuBar;
  }

  public JMenuBar getLoginMenu() {
  	return loginMenuBar;
  }
  
  public WindowListener getWindowCloseEvent() {
    return windowExitListener;
  }
  
  public WindowFocusListener getWindowFocusEvent() {
    return windowFocusListener;
  }

  public Object[] getComponents() {
    return components.toArray();
  }

  private JMenu jmLobby, jmAccount, jmCashier, jmOptions;//, jmSecurity;
  private JMenu jmLanguages, jmRequests, jmReferFriend, jmHelp;
  
  
  protected JMenuBar createMenuBar(String str) {
    JMenuBar menuBar;
    //JMenuItem menuItem;

    menuBar = new JMenuBar();
    jmLobby = createLobbyMenu(true);
    jmAccount = creteAccountMenu();
    jmOptions = createOptionMenu(true);
    jmLanguages = creteLanguageMenu();
    jmRequests = creteRequestsMenu();
    jmHelp = createHelpMenu();

    menuBar.add(jmLobby);
    menuBar.add(jmAccount);
    if(str.equals("lobby"))
	{
		menuBar.add(jmOptions);
		menuBar.add(jmLanguages);
    }
    menuBar.add(jmRequests);
    menuBar.add(jmHelp);
    menuBar.addMouseListener(new MouseAdapter() {
    	@Override
		public void mouseClicked(MouseEvent arg0) {
    		MenuSelectionManager.defaultManager().clearSelectedPath();
    	      
		}
	});
    return menuBar;
  }

  private JMenu creteAccountMenu() {
    JMenu menu;
    JMenuItem menuItem;
    menu = new JMenu(bundle.getString("account"));
    menu.setBackground(ClientConfig.menuColor);
    menu.getAccessibleContext().setAccessibleDescription(
        bundle.getString("account"));
    
    menuItem = new JMenuItem(bundle.getString("change.email.add"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("change.email.add"));
    menuItem.getAccessibleContext().setAccessibleName(
        bundle.getString("change.email.add"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menuItem = new JMenuItem(bundle.getString("change.personal.details"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("change.personal.details"));
    menuItem.getAccessibleContext().setAccessibleName(
        bundle.getString("change.personal.details"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menuItem = new JMenuItem(bundle.getString("change.password"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("change.password"));
    menuItem.getAccessibleContext().setAccessibleName(
        bundle.getString("change.password"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menuItem = new JMenuItem(bundle.getString("change.avatar"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("change.avatar"));
    menuItem.getAccessibleContext().setAccessibleName(
        bundle.getString("change.avatar"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menuItem = new JMenuItem(bundle.getString("change.email.notif"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("change.email.notif"));
    menuItem.getAccessibleContext().setAccessibleName(
        bundle.getString("change.email.notif"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    return menu;
  }
  
 
  private JMenu creteLanguageMenu() {
    JMenu menu;
    menu = new JMenu(bundle.getString("languages"));
    menu.setBackground(ClientConfig.menuColor);
    menu.getAccessibleContext().setAccessibleDescription(
        bundle.getString("languages"));
    
    ButtonGroup group = new ButtonGroup();

    jmEnglish = new JRadioButtonMenuItem(bundle.getString("english"));
    jmEnglish.getAccessibleContext().setAccessibleDescription(bundle.getString("english"));
    jmEnglish.getAccessibleContext().setAccessibleName(bundle.getString("english"));
    jmEnglish.addActionListener(this);
    group.add(jmEnglish);
    menu.add(jmEnglish);

    jmDeutsch = new JRadioButtonMenuItem(bundle.getString("deutsch"));
    jmDeutsch.getAccessibleContext().setAccessibleDescription(bundle.getString("deutsch"));
    jmDeutsch.getAccessibleContext().setAccessibleName(bundle.getString("deutsch"));
    jmDeutsch.addActionListener(this);
    group.add(jmDeutsch);
    menu.add(jmDeutsch);

    jmMagyar = new JRadioButtonMenuItem(bundle.getString("magyar"));
    jmMagyar.getAccessibleContext().setAccessibleDescription(bundle.getString("magyar"));
    jmMagyar.getAccessibleContext().setAccessibleName(bundle.getString("magyar"));
    jmMagyar.addActionListener(this);
    group.add(jmMagyar);
    menu.add(jmMagyar);
    
    jmSpanish = new JRadioButtonMenuItem(bundle.getString("spanish"));
    jmSpanish.getAccessibleContext().setAccessibleDescription(bundle.getString("spanish"));
    jmSpanish.getAccessibleContext().setAccessibleName(bundle.getString("spanish"));
    jmSpanish.addActionListener(this);
    group.add(jmSpanish);
    menu.add(jmSpanish);
    
    String language = credentialManager.getLanguageInfo();
    if(language.toLowerCase().equals("english"))
    {
    	jmEnglish.setSelected(true);
    }
    else if(language.toLowerCase().equals("deutsch"))
    {
    	jmDeutsch.setSelected(true);
    }
    else if(language.toLowerCase().equals("magyar"))
    {
    	jmMagyar.setSelected(true);
    }
    else if(language.toLowerCase().equals("spanish"))
    {
    	jmSpanish.setSelected(true);
    }
    return menu;
  }
  
  private JMenu creteRequestsMenu() {
    JMenu menu;
    JMenuItem menuItem;
    menu = new JMenu(bundle.getString("requests"));
    menu.setBackground(ClientConfig.menuColor);
    menu.getAccessibleContext().setAccessibleDescription(
        bundle.getString("requests"));
    
    menuItem = new JMenuItem(bundle.getString("principles"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString("principles"));
    menuItem.getAccessibleContext().setAccessibleName(bundle.getString("principles"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
  
    menuItem = new JMenuItem(bundle.getString("selfexclusion"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString("selfexclusion"));
    menuItem.getAccessibleContext().setAccessibleName(bundle.getString("selfexclusion"));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    return menu;
  }
  
 
  private JMenu createHelpMenu() {
    JMenu menuHelp;
    JMenuItem menuItem;
    menuHelp = new JMenu(bundle.getString("help"));
    menuHelp.setBackground(ClientConfig.menuColor);
    menuHelp.getAccessibleContext().setAccessibleDescription(
        bundle.getString("help.desc"));
    
    menuItem = new JMenuItem(bundle.getString("game.help"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("game.help"));
    menuItem.addActionListener(this);
    menuHelp.add(menuItem);
    
    menuItem = new JMenuItem(bundle.getString("game.rules"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("game.rules"));
    menuItem.addActionListener(this);
    menuHelp.add(menuItem);
    
    return menuHelp;
  }

  protected JMenu createOptionMenu(boolean hasAutoLogin) {
	  int pref = credentialManager.getPreferences();
	  settings = new ClientSettings(pref);
    JMenu jmSettings;
    jmSettings = new JMenu(bundle.getString("options"));
    jmSettings.setBackground(ClientConfig.menuColor);
    jmSettings.getAccessibleContext().setAccessibleDescription(
        bundle.getString("settings.desc"));
    jmiAutoBigBlind = createSettingsMenuItem(bundle.getString("auto.post.blind"),
    					settings.isAutoPostBlind());
    jmSettings.add(jmiAutoBigBlind);
    jmiWaitBigBlind =
        createSettingsMenuItem(
        bundle.getString("wait.for.blind"),
        settings.isWaitForBigBlind());
    jmSettings.add(jmiWaitBigBlind);
    //@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
    jmiSound =
        createSettingsMenuItem(bundle.getString("sound"), settings.isSound());
    _cat.fine("createSettingsMenu: " + settings.isSound());
    jmSettings.add(jmiSound);

   //@@@@@@@@@@@@@@@@@@   4 color cards   @@@@@@@@@@@@@@@@@@@@@@@@@@
    jmiFourColorCards =
        createSettingsMenuItem(bundle.getString("four.color.cards"), settings.isFourColorCards());
    jmSettings.add(jmiFourColorCards);

    return jmSettings;

  }

  protected JMenu createLobbyMenu(boolean hasCashierMenu) {
    JMenu menu;
    JMenuItem menuItem;
    menu = new JMenu(bundle.getString("lobby"));
    menu.setBackground(ClientConfig.menuColor);
    menu.getAccessibleContext().setAccessibleDescription("menu item");

    if (hasCashierMenu) {
      menuItem = new JMenuItem(bundle.getString("cashier"));
      menuItem.getAccessibleContext().setAccessibleDescription(
          bundle.getString("cashier.desc"));
      menuItem.getAccessibleContext().setAccessibleName(
          bundle.getString("cashier"));
      menuItem.addActionListener(this);
      menu.add(menuItem);
    }
    menuItem = new JMenuItem(bundle.getString("exit"));
    menuItem.getAccessibleContext().setAccessibleDescription(
        bundle.getString("exit.desc"));
    menuItem.getAccessibleContext().setAccessibleName(
        bundle.getString("exit"));
    menuItem.addActionListener(this);
    menu.addSeparator();
    menu.add(menuItem);
    return menu;
  }

  protected void initJBCashier() {
    jbCashier = createButton("   "+bundle.getString("cashier"), 590, 203,
    		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER), 
    		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_OVR));
    jbCashier.setFont(new Font("Myriad Web", Font.BOLD, 22));
    jbCashier.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			openBrowser("cashier.url");
		}
	});
    components.add(jbCashier);
  }
  
  
  
  protected void initFlashAdv() {
	  
	  JPanel p = new JPanel(new BorderLayout());
	  p.setBounds(590, 280, 205, 313);
	  p.setOpaque(false);

	  p.add(new JLabel(Utils.getIcon(ClientConfig.IMG_LOBBY_FLASH_ADV)));
	 /* JFlashPlayer flashPlayer = new JFlashPlayer();   
	  flashPlayer.load("http://www.blueacepoker.com/adv/adv.swf");
	  new Exception("init flash").printStackTrace();
	  flashPlayer.setOpaque(false);
	  p.add(flashPlayer);*/
	  
      components.add(p);
  }
  
  
  protected void newTableList() {
	  //System.out.println(gameType_col1);
	  
	  if(gameType_col1 == GameTypes.TYPE_TOURNAMENT)
	  {
		  filterTournyList(_tournyList);
	  }
	  else 
	  {
		  filterTableList(_tableList);
	  }
    
	  if(gameType_col1 == GameTypes.TYPE_RINGGAME)
	  {
		  main_panel.remove(table_pane);
		  table_pane = (JScrollPane) createJTable(cg_model, "CG");
		  infoGameTypes.changeGameTypes("CG");
		  btnAddWLorTourney.change("CG");
		  tabplayerorWL.panel.setVisible(true);
		  infoTextArea.change("CG");
		  showTableFilters.change("CG");
		  //changeFilterButtons("CG");
		  changeLabels("CG");
		 
	  }
	  if(gameType_col1 == GameTypes.TYPE_SITNGO)
	  {
		  main_panel.remove(table_pane);
		  table_pane = (JScrollPane) createJTable(sitngo_model, "SNG");
		  infoGameTypes.changeGameTypes("SNG");
		  btnAddWLorTourney.change("SNG");
		  tabplayerorWL.panel.setVisible(true);
		  infoTextArea.change("SNG");
		  showTableFilters.change("SNG");
		  //changeFilterButtons("SNG");
		  changeLabels("SNG");
	  }
	  if(gameType_col1 == GameTypes.TYPE_TOURNAMENT)
	  {
		  main_panel.remove(table_pane);
		  table_pane = (JScrollPane) createJTable(tournament_model, "MTT");
		  infoGameTypes.changeGameTypes("MTT");
		  btnAddWLorTourney.change("MTT");
		  tabplayerorWL.panel.setVisible(false);
		  infoTextArea.change("MTT");
		  showTableFilters.change("MTT");
		  //changeFilterButtons("MTT");
		  changeLabels("MTT");
	  }
	  if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER)
	  {
		  main_panel.remove(table_pane);
		  table_pane = (JScrollPane) createJTable(tp_model, "CG");
		  infoGameTypes.changeGameTypes("CG");
		  btnAddWLorTourney.change("CG");
		  tabplayerorWL.panel.setVisible(true);
		  infoTextArea.change("CG");
		  showTableFilters.change("CG");
		  //changeFilterButtons("CG");
		  changeLabels("TP");
	  }
	  table_pane.setBounds(6, 238, 420, 351);
	  table_pane.setPreferredSize(new Dimension(420, 351));
	  main_panel.add(table_pane);
	  
  }
  
  
  public LobbyTableModel[] getCGLimitedList(LobbyTableModel[] model)
  {
	  Vector<LobbyTableModel> list = new Vector<LobbyTableModel>();
	  for (int v = 0; v < model.length; v++) 
	  {
		 if(gameType_cg_col3 == GameTypes.TYPE_RINGGAME_1_NOLIMIT)
		 {
			  if(model[v].getGameLimitType() == PokerConstants.NO_LIMIT)
				  list.add(model[v]);
		 }
		 else if(gameType_cg_col3 == GameTypes.TYPE_RINGGAME_1_POTLIMIT)
		 {
			 if(model[v].getGameLimitType() == PokerConstants.POT_LIMIT)
				 list.add(model[v]);
		 }
		 else if(gameType_cg_col3 == GameTypes.TYPE_RINGGAME_1_FIXEDLIMIT)
		 {
			 if(model[v].getGameLimitType() == PokerConstants.REGULAR)
				 list.add(model[v]);
		 }
		 else
		 {
			 list.add(model[v]);
		 }
		 //System.out.println("Temp List1: "+v+newltm[v].getGameType()!=null?newltm[v].getGameType():"");
      }
	  
	  Vector<LobbyTableModel> filterList = new Vector<LobbyTableModel>();
	  for (int v = 0; v < list.size(); v++) 
	  {
		 if(isCGHighSel.isSelected())
		 {
			 if(list.get(v)!= null && (list.get(v).getMinBet()/2 >= (isRBPlay?GameTypes.CG_PLAY_Filter_HIGH_MIN:GameTypes.CG_REAL_Filter_HIGH_MIN)) )
				 filterList.add(list.get(v));
		 }
		 if(isCGMediumSel.isSelected())
		 {
			 if(list.get(v)!= null && list.get(v).getMinBet()/2 >= (isRBPlay?GameTypes.CG_PLAY_Filter_MEDIUM_MIN:GameTypes.CG_REAL_Filter_MEDIUM_MIN) && 
					  list.get(v).getMinBet()/2 < (isRBPlay?GameTypes.CG_PLAY_Filter_MEDIUM_MAX:GameTypes.CG_REAL_Filter_MEDIUM_MAX))
				 filterList.add(list.get(v));//newFilterlist[v] = list.get(v);
		 }
		 if(isCGLowSel.isSelected())
		 {
			 if(list.get(v)!= null && list.get(v).getMinBet()/2 >= (isRBPlay?GameTypes.CG_PLAY_Filter_LOW_MIN:GameTypes.CG_REAL_Filter_LOW_MIN) && 
					  list.get(v).getMinBet()/2 < (isRBPlay?GameTypes.CG_PLAY_Filter_LOW_MAX:GameTypes.CG_REAL_Filter_LOW_MAX))
				 filterList.add(list.get(v));
		 }
		 if(isCGMicroSel.isSelected())
		 {
			  if(list.get(v)!= null && list.get(v).getMinBet()/2 < (isRBPlay?GameTypes.CG_PLAY_Filter_MICRO_MAX:GameTypes.CG_REAL_Filter_MICRO_MAX))
				  filterList.add(list.get(v));
		 }
      }
	  LobbyTableModel[] newFilterlist = new LobbyTableModel[filterList.size()];
	  for (int v = 0; v < filterList.size(); v++) 
	  {
		  newFilterlist[v] = filterList.elementAt(v);
	  }
	  return newFilterlist;
  }
  
  public LobbyTableModel[] getTPLimitedList(LobbyTableModel[] model)
  {
	  Vector<LobbyTableModel> list = new Vector<LobbyTableModel>();
	  for (int v = 0; v < model.length; v++) 
	  {
		 if(gameType_tp_col3 == GameTypes.TYPE_RINGGAME_1_NOLIMIT)
		 {
			  if(model[v].getGameLimitType() == PokerConstants.NO_LIMIT)
				  list.add(model[v]);
		 }
		 else if(gameType_tp_col3 == GameTypes.TYPE_RINGGAME_1_POTLIMIT)
		 {
			 if(model[v].getGameLimitType() == PokerConstants.POT_LIMIT)
				 list.add(model[v]);
		 }
		 else if(gameType_tp_col3 == GameTypes.TYPE_RINGGAME_1_FIXEDLIMIT)
		 {
			 if(model[v].getGameLimitType() == PokerConstants.REGULAR)
				 list.add(model[v]);
		 }
		 else
		 {
			 list.add(model[v]);
		 }
		 //System.out.println("Temp List1: "+v+newltm[v].getGameType()!=null?newltm[v].getGameType():"");
      }
	  
	  Vector<LobbyTableModel> filterList = new Vector<LobbyTableModel>();
	  for (int v = 0; v < list.size(); v++) 
	  {
		 if(isCGHighSel.isSelected())
		 {
			 if(list.get(v)!= null && (list.get(v).getMinBet()/2 >= (isRBPlay?GameTypes.CG_PLAY_Filter_HIGH_MIN:GameTypes.CG_REAL_Filter_HIGH_MIN)) )
				 filterList.add(list.get(v));
		 }
		 if(isCGMediumSel.isSelected())
		 {
			 if(list.get(v)!= null && list.get(v).getMinBet()/2 >= (isRBPlay?GameTypes.CG_PLAY_Filter_MEDIUM_MIN:GameTypes.CG_REAL_Filter_MEDIUM_MIN) && 
					  list.get(v).getMinBet()/2 < (isRBPlay?GameTypes.CG_PLAY_Filter_MEDIUM_MAX:GameTypes.CG_REAL_Filter_MEDIUM_MAX))
				 filterList.add(list.get(v));//newFilterlist[v] = list.get(v);
		 }
		 if(isCGLowSel.isSelected())
		 {
			 if(list.get(v)!= null && list.get(v).getMinBet()/2 >= (isRBPlay?GameTypes.CG_PLAY_Filter_LOW_MIN:GameTypes.CG_REAL_Filter_LOW_MIN) && 
					  list.get(v).getMinBet()/2 < (isRBPlay?GameTypes.CG_PLAY_Filter_LOW_MAX:GameTypes.CG_REAL_Filter_LOW_MAX))
				 filterList.add(list.get(v));
		 }
		 if(isCGMicroSel.isSelected())
		 {
			  if(list.get(v)!= null && list.get(v).getMinBet()/2 < (isRBPlay?GameTypes.CG_PLAY_Filter_MICRO_MAX:GameTypes.CG_REAL_Filter_MICRO_MAX))
				  filterList.add(list.get(v));
		 }
      }
	  LobbyTableModel[] newFilterlist = new LobbyTableModel[filterList.size()];
	  for (int v = 0; v < filterList.size(); v++) 
	  {
		  newFilterlist[v] = filterList.elementAt(v);
	  }
	  return newFilterlist;
  }
  
  public LobbySitnGoModel[] getSNGLimitedList(LobbySitnGoModel[] model)
  {
	  Vector<LobbySitnGoModel> list = new Vector<LobbySitnGoModel>();
	  for (int v = 0; v < model.length; v++) 
	  {
		 if(gameType_sng_col4 == GameTypes.TYPE_SITNGO_2_NOLIMIT)
		 {
			  if(model[v].getGameLimitType() == PokerConstants.NO_LIMIT)
				  list.add(model[v]);
		 }
		 else if(gameType_sng_col4 == GameTypes.TYPE_SITNGO_2_POTLIMIT)
		 {
			 if(model[v].getGameLimitType() == PokerConstants.POT_LIMIT)
				 list.add(model[v]);
		 }
		 else if(gameType_sng_col4 == GameTypes.TYPE_SITNGO_2_FIXEDLIMIT)
		 {
			 if(model[v].getGameLimitType() == PokerConstants.REGULAR)
				 list.add(model[v]);
		 }
		 else
		 {
			 list.add(model[v]);
		 }
		 //System.out.println("Temp List1: "+v+newltm[v].getGameType()!=null?newltm[v].getGameType():"");
      }
	  
	  Vector<LobbySitnGoModel> filterList = new Vector<LobbySitnGoModel>();
	  for (int v = 0; v < list.size() ; v++) 
	  {
		  if(isSNGHighSel.isSelected())
		  {
			 if(list.get(v)!= null && (list.get(v).getMinBuyIn() >= (isRBPlay?GameTypes.SNG_PLAY_Filter_HIGH_MIN:GameTypes.SNG_REAL_Filter_HIGH_MIN)) )
				 filterList.add(list.get(v));
		  }
		  if(isSNGMediumSel.isSelected())
		  {
			 if(list.get(v)!= null && list.get(v).getMinBuyIn() > (isRBPlay?GameTypes.SNG_PLAY_Filter_MEDIUM_MIN:GameTypes.SNG_REAL_Filter_MEDIUM_MIN) && 
					 list.get(v).getMinBuyIn() <= (isRBPlay?GameTypes.SNG_PLAY_Filter_MEDIUM_MAX:GameTypes.SNG_REAL_Filter_MEDIUM_MAX))
				 filterList.add(list.get(v));//newFilterlist[v] = newltm[v];
		  }
		  if(isSNGLowSel.isSelected())
		  {
			 if(list.get(v)!= null && list.get(v).getMinBuyIn() > (isRBPlay?GameTypes.SNG_PLAY_Filter_LOW_MIN:GameTypes.SNG_REAL_Filter_LOW_MIN) && 
					 list.get(v).getMinBuyIn() <= (isRBPlay?GameTypes.SNG_PLAY_Filter_LOW_MAX:GameTypes.SNG_REAL_Filter_LOW_MAX))
				 filterList.add(list.get(v));
		  }
		  if(isSNGMicroSel.isSelected())
		  {
			  if(list.get(v)!= null &&  
					  list.get(v).getMinBuyIn() <= (isRBPlay?GameTypes.SNG_PLAY_Filter_MICRO_MAX:GameTypes.SNG_REAL_Filter_MICRO_MAX))
				  filterList.add(list.get(v));
		  }
      }
	  LobbySitnGoModel[] newFilterlist = new LobbySitnGoModel[filterList.size()];
	  for (int v = 0; v < filterList.size(); v++) 
	  {
		  newFilterlist[v] = filterList.elementAt(v);
	  }
	  return newFilterlist;
  }
  
  public LobbyTournyModel[] getMTTLimitedList(LobbyTournyModel[] model)
  {
	  Vector<LobbyTournyModel> list = new Vector<LobbyTournyModel>();
	  for (int v = 0; v < model.length; v++) 
	  {
		 if(gameType_mtt_col4 == GameTypes.TYPE_TOURNAMENT_2_NOLIMIT)
		 {
			 if(model[v].getTournamentLimit() == -1)
			 {
				 list.add(model[v]);
			 }
		 }
		 else if(gameType_mtt_col4 == GameTypes.TYPE_TOURNAMENT_2_POTLIMIT)
		 {
			 if(model[v].getTournamentLimit() == 0)
				 list.add(model[v]);
		 }
		 else if(gameType_mtt_col4 == GameTypes.TYPE_TOURNAMENT_2_FIXEDLIMIT)
		 {
			 if(model[v].getTournamentLimit() > 0)
				 list.add(model[v]);
		 }
		 else
		 {
			 list.add(model[v]);
		 }
      }
	  Vector<LobbyTournyModel> filterList = new Vector<LobbyTournyModel>();
	  for (int v = 0; v < list.size() ; v++) 
	  {
		  if(isMTTHighSel.isSelected())
		  {
			 if(list.get(v)!= null && (list.get(v).getMinBuyIn() >= (isRBPlay?GameTypes.MTT_PLAY_Filter_HIGH_MIN:GameTypes.MTT_REAL_Filter_HIGH_MIN)) )
				 filterList.add(list.get(v));
		  }
		  if(isMTTMediumSel.isSelected())
		  {
			 if(list.get(v)!= null && list.get(v).getMinBuyIn() > (isRBPlay?GameTypes.MTT_PLAY_Filter_MEDIUM_MIN:GameTypes.MTT_REAL_Filter_MEDIUM_MIN) && 
					 list.get(v).getMinBuyIn() <= (isRBPlay?GameTypes.MTT_PLAY_Filter_MEDIUM_MAX:GameTypes.MTT_REAL_Filter_MEDIUM_MAX))
				 filterList.add(list.get(v));//newFilterlist[v] = newltm[v];
		  }
		  if(isMTTLowSel.isSelected())
		  {
			 if(list.get(v)!= null && list.get(v).getMinBuyIn() > (isRBPlay?GameTypes.MTT_PLAY_Filter_LOW_MIN:GameTypes.MTT_REAL_Filter_LOW_MIN) && 
					 list.get(v).getMinBuyIn() <= (isRBPlay?GameTypes.MTT_PLAY_Filter_LOW_MAX:GameTypes.MTT_REAL_Filter_LOW_MAX))
				 filterList.add(list.get(v));
		  }
		  if(isMTTMicroSel.isSelected())
		  {
			  if(list.get(v)!= null &&  
					  list.get(v).getMinBuyIn() <= (isRBPlay?GameTypes.MTT_PLAY_Filter_MICRO_MAX:GameTypes.MTT_REAL_Filter_MICRO_MAX))
				  filterList.add(list.get(v));
		  }
      }
	  LobbyTournyModel[] newFilterlist = new LobbyTournyModel[filterList.size()];
	  for (int v = 0; v < filterList.size(); v++) 
	  {
		  newFilterlist[v] = filterList.elementAt(v);
	  }
	  return newFilterlist;
  }
  
  
  
  
  protected void changeFilters(String type)
  {
	 if(type.equals("CG") || type.equals("TP"))
     {
		 isCGMicroSel.setVisible(true);
		 isCGLowSel.setVisible(true);
		 isCGMediumSel.setVisible(true);
		 isCGHighSel.setVisible(true);
		 
		 jbFMicro.setLocation(Utils.cgFilterLablesStart[0]);
		 jbFLow.setLocation(Utils.cgFilterLablesStart[1]);
		 jbFMedium.setLocation(Utils.cgFilterLablesStart[2]);
		 jbFHigh.setLocation(Utils.cgFilterLablesStart[3]);
		 
		 jbFFreeRoll.setVisible(false);
		 isSNGFreerolSel.setVisible(false);
		 isMTTFreerolSel.setVisible(false);
		 
		 
     }
	 else 
	 {
		 if(type.equals("SNG"))
		 {
			 isSNGFreerolSel.setVisible(true);
			 isSNGMicroSel.setVisible(true);
			 isSNGLowSel.setVisible(true);
			 isSNGMediumSel.setVisible(true);
			 isSNGHighSel.setVisible(true);
		 }
		 else
		 {
			 isMTTFreerolSel.setVisible(true);
			 isMTTMicroSel.setVisible(true);
			 isMTTLowSel.setVisible(true);
			 isMTTMediumSel.setVisible(true);
			 isMTTHighSel.setVisible(true);
		 }
		 
		 jbFFreeRoll.setVisible(true);
		 jbFFreeRoll.setLocation(Utils.mttFilterLablesStart[0]);
		 jbFMicro.setLocation(Utils.mttFilterLablesStart[1]);
		 jbFLow.setLocation(Utils.mttFilterLablesStart[2]);
		 jbFMedium.setLocation(Utils.mttFilterLablesStart[3]);
		 jbFHigh.setLocation(Utils.mttFilterLablesStart[4]);
	 }
  }
  
  	 protected void disableAllCheckBoxes()
	 {
  		 isCGMicroSel.setVisible(false);
	     isCGLowSel.setVisible(false);
	     isCGMediumSel.setVisible(false);
	     isCGHighSel.setVisible(false);
	     isSNGFreerolSel.setVisible(false);
	     isSNGMicroSel.setVisible(false);
	     isSNGLowSel.setVisible(false);
	     isSNGMediumSel.setVisible(false);
	     isSNGHighSel.setVisible(false);
	     isMTTFreerolSel.setVisible(false);
	     isMTTMicroSel.setVisible(false);
	     isMTTLowSel.setVisible(false);
	     isMTTMediumSel.setVisible(false);
	     isMTTHighSel.setVisible(false);
		
		 
	 }
 
	 protected void removeAllLabels()
	 {
		 bar1.removeAll();
		 bar2.removeAll();
		 bar3.removeAll();
		 bar4.removeAll();
		 frame.remove(bar4);
		 frame.repaint();
		 
	 }
	 
	 protected void changeLabels(String type)
	 {
		 try
		 {
			 JToggleButton  but1 = (JToggleButton) bar1.getComponentAtIndex(gameType_col1);
			 but1.setSelected(true);
			 if(type.equals("CG"))
			 {
				 JToggleButton  but2 = (JToggleButton) bar2.getComponentAtIndex(gameType_cg_col2);
				 but2.setSelected(true);
				 JToggleButton  but3 = (JToggleButton) bar3.getComponentAtIndex(gameType_cg_col3);
				 but3.setSelected(true);
			 }
			 else if(type.equals("SNG"))
			 {
				 JToggleButton  but2 = (JToggleButton) bar2.getComponentAtIndex(gameType_sng_col2);
				 but2.setSelected(true);
				 JToggleButton  but3 = (JToggleButton) bar3.getComponentAtIndex(gameType_sng_col3);
				 but3.setSelected(true);
				 JToggleButton  but4 = (JToggleButton) bar4.getComponentAtIndex(gameType_sng_col4);
				 but4.setSelected(true);
			 }
			 else if(type.equals("MTT"))
			 {
				 JToggleButton  but2 = (JToggleButton) bar2.getComponentAtIndex(gameType_mtt_col2);
				 but2.setSelected(true);
				 JToggleButton  but3 = (JToggleButton) bar3.getComponentAtIndex(gameType_mtt_col3);
				 but3.setSelected(true);
				 JToggleButton  but4 = (JToggleButton) bar4.getComponentAtIndex(gameType_mtt_col4);
				 but4.setSelected(true);
			 }
			 else if(type.equals("TP"))
			 {
				 JToggleButton  but2 = (JToggleButton) bar2.getComponentAtIndex(gameType_tp_col2);
				 but2.setSelected(true);
				 JToggleButton  but3 = (JToggleButton) bar3.getComponentAtIndex(gameType_tp_col3);
				 but3.setSelected(true);
			 }
		}catch (Exception e) {
		// TODO: handle exception
		}
		 
	 }
	 protected void initLabels(String type)
	 {
		String[] game_types;
		String[][] cg_types;
		String[][] sng_types;
		String[][] mtt_types;
		String[][] tp_types;
		
		game_types = new String[] {bundle.getString("ringgame"), bundle.getString("sitngo"), 
								bundle.getString("tournament"), bundle.getString("terminalpoker")};
			String[][] cg = {
					{bundle.getString("holdem"), bundle.getString("omaha.hi"), bundle.getString("omaha.hilo")},
					{bundle.getString("alltypes"), bundle.getString("fixlimit"), 
					 bundle.getString("potlimit"), bundle.getString("nolimit")}
			};
			cg_types = cg;
			tp_types = cg;
			cg = null;
			
			String[][] sng = {
					{bundle.getString("sng.all"), bundle.getString("sng.one.table"), 
					 bundle.getString("sng.six.handed"), bundle.getString("sng.headsup")},
					{bundle.getString("holdem"), bundle.getString("omaha.hi"), bundle.getString("omaha.hilo")},
					{bundle.getString("alltypes"), bundle.getString("fixlimit"), 
					 bundle.getString("potlimit"), bundle.getString("nolimit")}
			};
			sng_types = sng;
			sng = null;
			
			String[][] mtt = {
					{bundle.getString("sng.all"), bundle.getString("mtt.cash"), 
					 bundle.getString("mtt.guarantee"), bundle.getString("mtt.satellite")},
					{bundle.getString("holdem"), bundle.getString("omaha.hi"), bundle.getString("omaha.hilo")},
					{bundle.getString("alltypes"), bundle.getString("fixlimit"), 
					 bundle.getString("potlimit"), bundle.getString("nolimit")}
			};
			mtt_types = mtt;
			mtt = null;
			
			bar1.setFloatable(false);
			bar2.setFloatable(false);
			bar3.setFloatable(false);
			bar4.setFloatable(false);
			
			//panel 1
			bar1.setBounds(5, 93, 120, 100);
			bar1.setLayout(null);
			bar1.setOpaque(false);
			
			//panel 2
			bar2.setBounds(125, 93, 95, 100);
			bar2.setLayout(null);
			bar2.setOpaque(false);
			
			//panel 3
			bar3.setBounds(225, 93, 95, 100);
			bar3.setLayout(null);
			bar3.setOpaque(false);
			
			//panel 4
			bar4.setBounds(335, 93, 95, 100);
			bar4.setLayout(null);
			bar4.setOpaque(false);
			bar4.setVisible(false);
			
			//button group
			group1 = new ButtonGroup();
			group2 = new ButtonGroup();
			group3 = new ButtonGroup();
			group4 = new ButtonGroup();
			
			//Column 1
			for(int i = 0; i < game_types.length; i++)
			{
				AbstractButton tmpButton = new JToggleButton(game_types[i]);
				tmpButton.setBounds(0, i * 22, 120, 22);
				tmpButton.setFont(new Font("Verdana", Font.BOLD, 10));
				tmpButton.setHorizontalAlignment(SwingConstants.LEFT);
				tmpButton.setName("Filter");
				tmpButton.setActionCommand("col1_" + Integer.toString(i));
				tmpButton.addActionListener(this);
				bar1.add(tmpButton);
				group1.add(tmpButton);
			}
			
			//Column 2
			for(int i=0; i< (type.equals("CG")?cg_types[0].length:type.equals("SNG")?sng_types[0].length:type.equals("MTT")?mtt_types[0].length:tp_types[0].length); i++)
			{
				AbstractButton tmpButton = new JToggleButton((type.equals("CG")?cg_types[0][i]:type.equals("SNG")?sng_types[0][i]:type.equals("MTT")?mtt_types[0][i]:tp_types[0][i]));
				tmpButton.setBounds(0, i * 22, 95, 22);
				tmpButton.setFont(new Font("Verdana", Font.BOLD, 10));
				tmpButton.setHorizontalAlignment(SwingConstants.LEFT);
				tmpButton.setName("Filter");
				tmpButton.setActionCommand((type.equals("CG")?"cg":type.equals("SNG")?"sng":type.equals("MTT")?"mtt":"tp")+"-col2_" + Integer.toString(i));
				tmpButton.addActionListener(this);
				bar2.add(tmpButton);
				group2.add(tmpButton);
			}
			
			//Column 3
			for(int i=0; i< (type.equals("CG")?cg_types[1].length:type.equals("SNG")?sng_types[1].length:type.equals("MTT")?mtt_types[1].length:tp_types[1].length); i++)
			{
				AbstractButton tmpButton = new JToggleButton((type.equals("CG")?cg_types[1][i]:type.equals("SNG")?sng_types[1][i]:type.equals("MTT")?mtt_types[1][i]:tp_types[1][i]));
				tmpButton.setBounds(0, i * 22, 95, 22);
				tmpButton.setFont(new Font("Verdana", Font.BOLD, 10));
				tmpButton.setHorizontalAlignment(SwingConstants.LEFT);
				tmpButton.setName("Filter");
				tmpButton.setActionCommand((type.equals("CG")?"cg":type.equals("SNG")?"sng":type.equals("MTT")?"mtt":"tp")+"-col3_" + Integer.toString(i));
				tmpButton.addActionListener(this);
				bar3.add(tmpButton);
				group3.add(tmpButton);
			}
			
			//Column 4
			if(!type.equals("CG") && !type.equals("TP"))
			{
				for(int i=0; i< (type.equals("SNG")?sng_types[2].length:mtt_types[2].length); i++)
				{
					AbstractButton tmpButton = new JToggleButton(type.equals("SNG")?sng_types[2][i]:mtt_types[2][i]);
					tmpButton.setBounds(0, i * 22, 95, 22);
					tmpButton.setFont(new Font("Verdana", Font.BOLD, 10));
					tmpButton.setHorizontalAlignment(SwingConstants.LEFT);
					tmpButton.setName("Filter");
					tmpButton.setActionCommand((type.equals("SNG")?"sng":"mtt")+"-col4_" + Integer.toString(i));
					tmpButton.addActionListener(this);
					bar4.add(tmpButton);
					group4.add(tmpButton);
					
				}
				bar4.setVisible(true);
			}
			components.add(bar1);
			components.add(bar2);
			components.add(bar3);
			if(bar4.isVisible())components.add(bar4);
			if(frame!= null)
			{
				if(bar4.isVisible())frame.add(bar4);
				frame.repaint();
			}
			changeLabels(type);
			
	 }
	 protected void tableListUpdatedBySelectedLabel(String str)
	 {
		// System.out.println(str);
		if(str.startsWith("cg-col2_"))
    	{
			gameType_cg_col2 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("cg-col3_"))
    	{
			gameType_cg_col3 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("sng-col2_"))
    	{
			gameType_sng_col2 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("sng-col3_"))
    	{
			gameType_sng_col3 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("sng-col4_"))
    	{
			gameType_sng_col4 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("mtt-col2_"))
    	{
			gameType_mtt_col2 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("mtt-col3_"))
    	{
			gameType_mtt_col3 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("mtt-col4_"))
    	{
			gameType_mtt_col4 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("tp-col2_"))
    	{
			gameType_tp_col2 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.startsWith("tp-col3_"))
    	{
			gameType_tp_col3 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    	}
		else if(str.contains("col1_"))
    	{
    		gameType_col1 = Integer.parseInt(str.substring(str.indexOf("_")+1));
    		removeAllLabels();
    		initLabels(gameType_col1 == 0?"CG":gameType_col1 == 1?"SNG":gameType_col1 == 2?"MTT":"TP");
    		disableAllCheckBoxes();
    		changeFilters(gameType_col1 == 0?"CG":gameType_col1 == 1?"SNG":gameType_col1 == 2?"MTT":"TP");
    	}
		newTableList();
	 }
	 
	 protected void tableListUpdatedBySelectedCheckBox(String str)
	 {
		 newTableList();
	 }
	 
	 
	 protected void initFilters() 
	 {
		 
		isCGMicroSel = createCheckBox(Utils.cgFilterLablesStart[0]);
		isCGLowSel = createCheckBox(Utils.cgFilterLablesStart[1]);
	    isCGMediumSel = createCheckBox(Utils.cgFilterLablesStart[2]);
	    isCGHighSel = createCheckBox(Utils.cgFilterLablesStart[3]);
	    
	    isSNGFreerolSel = createCheckBox(Utils.mttFilterLablesStart[0]);
	    isSNGMicroSel = createCheckBox(Utils.mttFilterLablesStart[1]);
	    isSNGLowSel  = createCheckBox(Utils.mttFilterLablesStart[2]);
	    isSNGMediumSel = createCheckBox(Utils.mttFilterLablesStart[3]);
	    isSNGHighSel = createCheckBox(Utils.mttFilterLablesStart[4]);
	    
	    isMTTFreerolSel = createCheckBox(Utils.mttFilterLablesStart[0]);
	    isMTTMicroSel = createCheckBox(Utils.mttFilterLablesStart[1]);
	    isMTTLowSel  = createCheckBox(Utils.mttFilterLablesStart[2]);
	    isMTTMediumSel = createCheckBox(Utils.mttFilterLablesStart[3]);
	    isMTTHighSel = createCheckBox(Utils.mttFilterLablesStart[4]);
	    
	    
	    disableAllCheckBoxes();// to disable all checkboxes
	    
	    components.add(isCGMicroSel);
	    components.add(isCGLowSel);
	    components.add(isCGMediumSel);
	    components.add(isCGHighSel);
	    components.add(isSNGFreerolSel);
	    components.add(isSNGMicroSel);
	    components.add(isSNGLowSel);
	    components.add(isSNGMediumSel);
	    components.add(isSNGHighSel);
	    components.add(isMTTFreerolSel);
	    components.add(isMTTMicroSel);
	    components.add(isMTTLowSel);
	    components.add(isMTTMediumSel);
	    components.add(isMTTHighSel);
	    
	    
	    jbFFreeRoll = createFilterButton(bundle.getString("filter.freeroll"), 
				  Utils.mttFilterLablesStart[0], 
				  Utils.getIcon(ClientConfig.IMG_LOBBY_FILTER_BG));
		  jbFFreeRoll.setVisible(false);
		  
		    components.add(jbFFreeRoll);
		    
		    
		  jbFMicro = createFilterButton(bundle.getString("filter.micro"), 
				  Utils.cgFilterLablesStart[0],
				  Utils.getIcon(ClientConfig.IMG_LOBBY_FILTER_BG));
		  
		    components.add(jbFMicro);
		  jbFLow = createFilterButton(bundle.getString("filter.low"), 
				  Utils.cgFilterLablesStart[1],
				  Utils.getIcon(ClientConfig.IMG_LOBBY_FILTER_BG));
		  
		    components.add(jbFLow);
		  jbFMedium = createFilterButton(bundle.getString("filter.medium"), 
				  Utils.cgFilterLablesStart[2],
				  Utils.getIcon(ClientConfig.IMG_LOBBY_FILTER_BG));
		  
		    components.add(jbFMedium);
		  jbFHigh = createFilterButton(bundle.getString("filter.high"), 
				  Utils.cgFilterLablesStart[3],
				  Utils.getIcon(ClientConfig.IMG_LOBBY_FILTER_BG));
		  
		    components.add(jbFHigh);
	    
  }
	 protected void initPlayerInfo() {
		 Font dotsFont = new Font("Myriad Web", Font.PLAIN, 14);
		 JLabel jlPlayersOnlineLabel = new JLabel(bundle.getString("login.players.online"));
		  jlPlayersOnlineLabel.setForeground(new Color(0x6B696C));
		  jlPlayersOnlineLabel.setFont(new Font("Myriad Web", Font.PLAIN, 14));
		  jlPlayersOnlineLabel.setBounds(680, 90, 150, 20);
		  
		  jlPlayersOnline = new JLabel("25.616");
		  try {
			  InputStream instrm = ClassLoader.getSystemResourceAsStream("fonts/dots.ttf");
		      dotsFont= Font.createFont(Font.TRUETYPE_FONT, instrm).deriveFont((float)15).deriveFont(Font.BOLD);
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		  jlPlayersOnline.setFont(dotsFont);
		  jlPlayersOnline.setForeground(new Color(0xFFFFFF));
		  jlPlayersOnline.setBounds(680, 107, 175, 35);
		  
		  JLabel jlActiveTablesLabel = new JLabel(bundle.getString("login.activetables"));
		  jlActiveTablesLabel.setForeground(new Color(0x6B696C));
		  jlActiveTablesLabel.setFont(new Font("Myriad Web", Font.PLAIN, 14));
		  jlActiveTablesLabel.setBounds(680, 145, 140, 20);
		  
		  jlActiveTables = new JLabel("5.616");
		  jlActiveTables.setFont(new Font("Dots All For Now JL", Font.PLAIN, 21));
		  jlActiveTables.setForeground(new Color(0xFFFFFF));
		  jlActiveTables.setBounds(680, 160, 175, 35);
		  
		  JLabel jlRealWorthLabel = new JLabel(bundle.getString("lobby.real.worth"));
		  jlRealWorthLabel.setFont(new Font("Dots All For Now JL", Font.PLAIN, 13));
		  jlRealWorthLabel.setForeground(new Color(0xFFFFFF));
		  jlRealWorthLabel.setBounds(550, 10, 125, 35);
		  
		  jlRealWorth = new JLabel("<html>0.00 €</html>");
		  jlRealWorth.setFont(new Font("Dots All For Now JL", Font.PLAIN, 14));
		  jlRealWorth.setForeground(new Color(0xFFFFFF));
		  jlRealWorth.setBounds(680, 10, 125, 35);
		  
		  JLabel jlPlayWorthLabel = new JLabel(bundle.getString("lobby.play.worth"));
		  jlPlayWorthLabel.setFont(new Font("Dots All For Now JL", Font.PLAIN, 13));
		  jlPlayWorthLabel.setForeground(new Color(0xFFFFFF));
		  jlPlayWorthLabel.setBounds(550, 30, 125, 35);
		  
		  jlPlayWorth = new JLabel(SharedConstants.doubleToString(_serverProxy.playWorth()));
		  jlPlayWorth.setFont(new Font("Dots All For Now JL", Font.PLAIN, 14));
		  jlPlayWorth.setForeground(new Color(0xFFFFFF));
		  jlPlayWorth.setBounds(680, 30, 125, 35);
		  
		  JLabel jlMoneyInPlayLabel = new JLabel(bundle.getString("lobby.money.inplay"));
		  jlMoneyInPlayLabel.setFont(new Font("Dots All For Now JL", Font.PLAIN, 12));
		  jlMoneyInPlayLabel.setForeground(new Color(0xFFFFFF));
		  jlMoneyInPlayLabel.setBounds(555, 55, 150, 35);
		  
		  jlMoneyInPlay = new JLabel(SharedConstants.moneyToString$Right(_serverProxy.realWorth()),JLabel.RIGHT);
		  jlMoneyInPlay.setFont(new Font("Dots All For Now JL", Font.PLAIN, 12));
		  jlMoneyInPlay.setForeground(new Color(0xFFFFFF));
		  jlMoneyInPlay.setBounds(680, 55, 75, 35);
		  
		  components.add(jlPlayersOnline);
		  components.add(jlPlayersOnlineLabel);
		  components.add(jlActiveTables);
		  components.add(jlActiveTablesLabel);
		  components.add(jlRealWorthLabel);
		  components.add(jlPlayWorthLabel);
		  components.add(jlMoneyInPlayLabel);
		  components.add(jlRealWorth);
		  components.add(jlPlayWorth);
		  components.add(jlMoneyInPlay);

	  }
  
  protected void initRadioButtons()
  {
	  jrReal = createRadioButton(bundle.getString("real.money"),15, 198);
	  jrReal.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
				jrReal.setSelected(true);
				jrPlay.setSelected(false);
				isRBPlay = false;
				newTableList();
			}
		});
	  
	  jrPlay = createRadioButton(bundle.getString("play.money"),115, 198);
	  jrPlay.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				jrReal.setSelected(false);
				jrPlay.setSelected(true);
				isRBPlay = true;
				newTableList();			
			}
		});
	  
//	  jrPublic = createRadioButton("Public Tables",300, 198);
//	  jrPublic.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				jrPrivate.setSelected(false);
//				jrPublic.setSelected(true);
//				//isRBPlay = true;
//				newTableList();			
//			}
//		});
//	  
//	  jrPrivate = createRadioButton("Private Tables",425, 198);
//	  jrPrivate.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				jrPrivate.setSelected(true);
//				jrPublic.setSelected(false);
//				//isRBPlay = true;
//				newTableList();			
//			}
//		});
	  
	  
	  components.add(jrPlay);
	  components.add(jrReal);
//	  components.add(jrPublic);
//	  components.add(jrPrivate);
  }
 
  
  

  protected void initEasternTime() {
	  
    jlEasternTime = new JLabel(bundle.getString("eastern.time")+" ");
    jlEasternTime.setFont(new Font("Verdana", Font.BOLD, 18));
    jlEasternTime.setBounds(450, 215, 125, 40);
    jlEasternTime.setForeground(Color.WHITE);
    components.add(jlEasternTime);
  }

  

  protected JTable getSelectedJTable() {
    JTable jTable = null;
    //System.out.println(omaha_model.getColumnCount()+"--"+holdem_model.getRowCount()+"--"+tournament_model.getColumnCount());
    System.out.println(table_pane.getName());

    return jTable;
  }

  /**
   * Listen to server responses --- errors, info updates etc
   * @param e ListSelectionEvent
   */
  public void serverLobbyResponse(Action act) {
    JOptionPane.showMessageDialog(frame,
                                  bundle.getString("Server Error " + act),
                                  bundle.getString("error"),
                                  JOptionPane.ERROR_MESSAGE);
  }

  // ListSelectionListener Interface
  public void valueChanged(ListSelectionEvent e) {
	  if (e.getSource() instanceof ListSelectionModel
        && frame.getFocusOwner() instanceof JTable) {
      JTable jTable = (JTable) frame.getFocusOwner();
      selected_row = jTable.getSelectedRow() != -1? jTable.getSelectedRow():selected_row;	
      if (jTable.getSelectionModel() != e.getSource()) {
        return;
      }
      //System.out.println("value change Event type: "+jTable.getName());
	  updateSelectedLobbyTable(jTable);
    }
  }

  // end of ListSelectionListener Interface

  private JCheckBoxMenuItem createSettingsMenuItem(String menuLabel, boolean isArmed) {
	JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(menuLabel);
    menuItem.setState(isArmed);
    menuItem.getAccessibleContext().setAccessibleDescription("");
    menuItem.addActionListener(this);
//		fillComponent(menuItem);
    return menuItem;
  }

  public void adUpdated() {
    //pingTextArea.setVisible(false);
    if (_serverProxy != null) {
      adTextArea.setText(_serverProxy.getAd());
    }
    //pingTextArea.setText("Ping:   " +
    //                    SharedConstants.doubleToString(_player.
    //   getAveragePing()) + " ms");
    //pingTextArea.setVisible(true);
  }

  private InfoJLabel createInfoTextArea() {
	  InfoJLabel panel = new InfoJLabel();
	  //panel.setBackground(new Color(24,89,140));
	  panel.setOpaque(false);
	  panel.setBounds(432, 250, 145, 175);
	 
    return panel;
  }
  
  private TabPlayerorWL createTabPlayerorWL() {
	  TabPlayerorWL panel = new TabPlayerorWL();
	  panel.setOpaque(false);
	  panel.setBounds(432, 401, 145, 19);
	  
	  return panel;
  }
  
  private BtnAddWLorTourney createAddtoWLButton() {
	  BtnAddWLorTourney panel = new BtnAddWLorTourney();
	  panel.setOpaque(false);
	  panel.setBounds(432, 430, 145, 27);
	  
	  return panel;
  }
  
  private InfoGameTypes createInfoGameTypes() {
	  InfoGameTypes panel = new InfoGameTypes();
	  panel.setOpaque(false);
	  panel.setBounds(432, 460, 145, 140);
	  
	  return panel;
  }
  
  private ShowTablesFilter createShowTableFilters() {
	  ShowTablesFilter panel = new ShowTablesFilter();
	  panel.setOpaque(false);
	  panel.setBounds(0, 215, 250, 30);
	  
	  return panel;
  }
  
  private void createMainPanelWithContents() {
    _cat.fine("Creating main panel with content ");
    
    /* Creating tables	*/
    _cat.fine("createMainPanelWithContents : Creating tables ");
    cg_model = getCGListModel();
    _serverProxy.addLobbyModelChangeListener(cg_model);
    tp_model = getCGListModel();
    _serverProxy.addLobbyModelChangeListener(tp_model);
	
    holdem_model = getHoldemListModel();
    _serverProxy.addLobbyModelChangeListener(holdem_model);
	omaha_model = getOmahaListModel();
	_serverProxy.addLobbyModelChangeListener(omaha_model);
    stud_model = getStudListModel();
    _serverProxy.addLobbyModelChangeListener(stud_model);
    sitngo_model = getSitnGoListModel();
    _serverProxy.addLobbyModelChangeListener(sitngo_model);
    tournament_model = getTournamentListModel();
    _serverProxy.addLobbyModelChangeListener(tournament_model);
    
    tournament_real_model = getTournamentRealListModel();
    _serverProxy.addLobbyModelChangeListener(tournament_real_model);
    
    
    if(gameType_col1 == GameTypes.TYPE_TOURNAMENT)
    	table_pane = (JScrollPane) createJTable(tournament_model, "MTT");
    else if(gameType_col1 == GameTypes.TYPE_SITNGO)
    	table_pane = (JScrollPane) createJTable(sitngo_model, "SNG");
    else if(gameType_col1 == GameTypes.TYPE_RINGGAME)
    	table_pane = (JScrollPane) createJTable(holdem_model, "CG");
    else if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER)
    	table_pane = (JScrollPane) createJTable(tp_model, "CG");
     
    
    
    infoTextArea = createInfoTextArea();
    tabplayerorWL =  createTabPlayerorWL();
    btnAddWLorTourney =  createAddtoWLButton();
    infoGameTypes = createInfoGameTypes();
    showTableFilters = createShowTableFilters();
    main_panel = new JPanel();

    //loading defaults 
    int av[] = null;
    av = credentialManager.getGameStatusInfo();
    if(av == null)
    {
    	gameType_col1 = GameTypes.TYPE_RINGGAME;
	    gameType_sng_col2 = GameTypes.TYPE_SITNGO_0_ALL;
	    gameType_mtt_col2 = GameTypes.TYPE_TOURNAMENT_0_ALL;
	    gameType_cg_col2 = GameTypes.TYPE_RINGGAME_0_HOLDEM;
	    gameType_sng_col3 = GameTypes.TYPE_RINGGAME_0_HOLDEM;
	    gameType_mtt_col3 = GameTypes.TYPE_RINGGAME_0_HOLDEM;
	    gameType_cg_col3 = GameTypes.TYPE_RINGGAME_1_ALLTYPES;
	    gameType_sng_col4 = GameTypes.TYPE_RINGGAME_1_NOLIMIT;
	    gameType_mtt_col4 = GameTypes.TYPE_RINGGAME_1_NOLIMIT;
	    gameType_tp_col2 = GameTypes.TYPE_RINGGAME_0_HOLDEM;
	    gameType_tp_col3 = GameTypes.TYPE_RINGGAME_1_ALLTYPES;
	    isSNGFreerolSel.setSelected(true);isMTTFreerolSel.setSelected(true);
	    isCGMicroSel.setSelected(true); isSNGMicroSel.setSelected(true); isMTTMicroSel.setSelected(true);
	    isCGLowSel.setSelected(true); isSNGLowSel.setSelected(true); isMTTLowSel.setSelected(true);
	    isCGMediumSel.setSelected(true); isSNGMediumSel.setSelected(true); isMTTMediumSel.setSelected(true);
	    isCGHighSel.setSelected(true); isSNGHighSel.setSelected(true); isMTTHighSel.setSelected(true);
	    isRBPlay = true;jrPlay.setSelected(true); 
	}
    else
    {
    	gameType_col1 = av[0];
        gameType_cg_col2 = av[1];
        gameType_cg_col3 = av[2];
        gameType_sng_col2 = av[3];
        gameType_sng_col3 = av[4];
        gameType_sng_col4 = av[5];
        gameType_mtt_col2 = av[6];
        gameType_mtt_col3 = av[7];
        gameType_mtt_col4 = av[8];
       	
        gameType_tp_col2 = av[24];
	    gameType_tp_col3 = av[25];
	    
        isCGMicroSel.setSelected(av[9] == 1?true:false);
       	isCGLowSel.setSelected(av[10] == 1?true:false);
       	isCGMediumSel.setSelected(av[11] == 1?true:false); 
       	isCGHighSel.setSelected(av[12] == 1?true:false); 
       	
       	isSNGFreerolSel.setSelected(av[13] == 1?true:false);
  	    isSNGMicroSel.setSelected(av[14] == 1?true:false); 
  	    isSNGLowSel.setSelected(av[15] == 1?true:false); 
  	    isSNGMediumSel.setSelected(av[16] == 1?true:false); 
  	    isSNGHighSel.setSelected(av[17] == 1?true:false); 
  	    
  	    isMTTFreerolSel.setSelected(av[18] == 1?true:false);
	    isMTTMicroSel.setSelected(av[19] == 1?true:false);
	    isMTTLowSel.setSelected(av[20] == 1?true:false);
	    isMTTMediumSel.setSelected(av[21] == 1?true:false);
	    isMTTHighSel.setSelected(av[22] == 1?true:false);
	    
	    if(av[23] == 1)
       	{
       		isRBPlay = true;
       		jrReal.setSelected(false);
			jrPlay.setSelected(true);
       	}
	    else
	    {
	    	isRBPlay = false;
	    	jrReal.setSelected(true);
			jrPlay.setSelected(false);
			
	    }
	    
	    
	 }
    	
       	if(gameType_col1 == GameTypes.TYPE_SITNGO)
  	    {
       		initLabels("SNG");
       		changeFilters("SNG");
       		infoTextArea.change("SNG");
       		showTableFilters.change("SNG");
       		infoGameTypes.changeGameTypes("SNG");
  		    btnAddWLorTourney.change("SNG");
  		    
  		    
	  	    
	  	    
  	    }
       	else if(gameType_col1 == GameTypes.TYPE_TOURNAMENT)
	  	{
       		initLabels("MTT");
       		changeFilters("MTT");
       		infoTextArea.change("MTT");
       		showTableFilters.change("MTT");
       		infoGameTypes.changeGameTypes("MTT");
  		    btnAddWLorTourney.change("MTT");
  		    
  		    
	  	  	
	  	}	
       	else if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER)
   		{
       		initLabels("TP");
       		changeFilters("TP");
       		
        }
       	else 
   		{
       		initLabels("CG");
       		changeFilters("CG");
       		
        }
       	
       	
	        
       	
    
    
    isCGFullTables = true;
    isSNGMTTRunning = true;
    isSNGMTTCmpleted = true;
    //--------
    ImageIcon icon0 = Utils.getIcon(ClientConfig.IMG_HEADDER_TOP_BG);
    JLabel jlHeaderTopBg = new JLabel(icon0);
    jlHeaderTopBg.setBounds(6, 239, 421, 6);
    components.add(jlHeaderTopBg);
    components.add(main_panel);
    components.add(infoTextArea);
    components.add(tabplayerorWL);
    components.add(btnAddWLorTourney);
    components.add(infoGameTypes);
    components.add(showTableFilters);
//    components.add(adTextArea);

    main_panel.setBounds(6, 238, 420, 361);
    //main_panel.setLayout(new BorderLayout());
    main_panel.setBorder(new EmptyBorder(0, 0, 0, 0));
  
    table_pane.setBounds(6, 238, 420, 351);
    table_pane.setPreferredSize(new Dimension(420, 351));
    

    main_panel.add(table_pane);
    main_panel.setOpaque(false);
  }

  protected LobbyListModel getTableListModel() {
    LobbyListModel holdem_model = new TableListModel();
    return holdem_model;
  }
  
  protected TournamentListModel getTournamentListModel() {
	  TournamentListModel tournament_model = new TournamentListModel();
    return tournament_model;
  }
  
  protected LobbyListModel getCGListModel() {
    LobbyListModel holdem_model = new TableListModel();
    return holdem_model;
  }
  
  protected LobbyListModel getHoldemListModel() {
    LobbyListModel holdem_model = new HoldemListModel();
    return holdem_model;
  }
  
  protected LobbyListModel getStudListModel() {
    LobbyListModel stud_model = new StudListModel();
    return stud_model;
  }
  
  protected LobbyListModel getOmahaListModel() {
    LobbyListModel omaha_model = new OmahaListModel();
    return omaha_model;
  }
  
  protected LobbyListModel getSitnGoListModel() {
    LobbyListModel sitngo_model = new SitnGoListModel();
    return sitngo_model;
  }
  
  protected TournamentListModel getTournamentRealListModel() {
	  TournamentListModel tournament_real_model = new TournamentListModel();
    return tournament_real_model;
  }

 
//  private void updateJTable(TableModel tableModel, String str) {
//	  if(str.equals("CG"))cgTableModel = tableModel;
//	  else if(str.equals("SNG"))sngTableModel = tableModel;
//	  else if(str.equals("MTT"))mttTableModel = tableModel;
//  }
	  
  

  //private Color tableSelectionBackground;
  private JComponent createJTable(TableModel tableModel, String str) {
	//cgTableModel = tableModel;
    //_cat.fine("createJTable : Creating tables ");
    JPanel p = new JPanel();
	p.setLayout(new BorderLayout());

    UIManager.getDefaults().put("TableHeader.cellBorder",BorderFactory.createEmptyBorder()); 
    sorter = new TableSorter(tableModel);
    tml = new tableModelListener(sorter);
    tableModel.addTableModelListener(tml);
    jTable = new JTable(sorter);
    tml.setJTable(jTable);

    cellRenderer = new UserTableStringCellRenderer();
    jTable.getSelectionModel().addListSelectionListener(this);
    jTable.addFocusListener(this);
    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  
    
//		=== Table Headers
    JTableHeader header = new JTableHeader( jTable.getColumnModel() );
//    {
//	    public void paintComponent(Graphics g)
//	    {
//		    // Scale image to size of component
//		    Dimension d = getSize();
//		    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_HEADDER_BG);
//		    g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
//		    setOpaque( false );
//		    super.paintComponent(g);
//		    }	
//	    };
	    
    ((JComponent)header.getDefaultRenderer()).setOpaque(false);
   
//    header.setFont(new Font("Verdana", Font.BOLD, 12));
//    header.setForeground(Color.WHITE);
    header.setReorderingAllowed(false);
    header.setResizingAllowed(true);
    header.setBorder(new EmptyBorder(0, 0, 0, 0));
    header.setAlignmentX(SwingConstants.CENTER);
    jTable.setTableHeader( header );
    ((DefaultTableCellRenderer)jTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

//    jTable.setGridColor(Color.BLACK);
    
    //jTable.getColumnModel().getColumn(0).setHeaderValue(new txtIcon(name, icon));
    
    jTable.setIntercellSpacing(new Dimension(0,0));
    jTable.setFont(new Font("Verdana", Font.PLAIN, 9));
    jTable.setShowHorizontalLines(false);
    jTable.setShowVerticalLines(false);
    jTable.setRowSelectionAllowed(true);
    jTable.setColumnSelectionAllowed(false);
    jTable.setDragEnabled(false);
    jTable.setDefaultRenderer(String.class, cellRenderer);
    jTable.setOpaque(false);
    jTable.setBackground(new Color(0xFFFFFF));
    jTable.setForeground(Color.WHITE);
    jTable.setShowGrid(false);
    jTable.setRowHeight(14);
    //
    
    if(str.equals("CG"))
    {
	    jTable.getColumnModel().getColumn(0).setPreferredWidth(75);
	    jTable.getColumnModel().getColumn(1).setPreferredWidth(62);
	    jTable.getColumnModel().getColumn(2).setPreferredWidth(47);
	    jTable.getColumnModel().getColumn(3).setPreferredWidth(37);
	    jTable.getColumnModel().getColumn(4).setPreferredWidth(59);
	    jTable.getColumnModel().getColumn(5).setPreferredWidth(53);
	    jTable.getColumnModel().getColumn(6).setPreferredWidth(34);
    }
    else if(str.equals("SNG"))
    {
    	jTable.getColumnModel().getColumn(0).setPreferredWidth(75);
	    jTable.getColumnModel().getColumn(1).setPreferredWidth(62);
	    jTable.getColumnModel().getColumn(2).setPreferredWidth(61);
	    jTable.getColumnModel().getColumn(3).setPreferredWidth(47);
	    jTable.getColumnModel().getColumn(4).setPreferredWidth(82);
	    jTable.getColumnModel().getColumn(5).setPreferredWidth(43);
    }
    else if(str.equals("MTT"))
    {
    	jTable.getColumnModel().getColumn(0).setPreferredWidth(123);
	    jTable.getColumnModel().getColumn(1).setPreferredWidth(60);
	    jTable.getColumnModel().getColumn(2).setPreferredWidth(37);
	    jTable.getColumnModel().getColumn(3).setPreferredWidth(62);
	    jTable.getColumnModel().getColumn(4).setPreferredWidth(61);
	    jTable.getColumnModel().getColumn(5).setPreferredWidth(24);
	}
    // mouse Listener
    jTable.addMouseListener(this);
    //For Sorting 
    //jTable.setAutoCreateRowSorter(true);
	sorter.addMouseListenerToHeaderInTable(jTable);
    
    if(jTable != null && jTable.getRowCount() > 0)
    		jTable.changeSelection(0, 0, false, false);
	

    tableScrollPane = new JScrollPane(JScrollPane.
                                             VERTICAL_SCROLLBAR_ALWAYS,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
		//scrollPane.getVerticalScrollBar().setUI(Utils.getLobbyScrollBarUI());
    tableScrollPane.setViewport(new JViewport() {
      public void paintComponent(Graphics g) {
        g.drawImage(
            tableBackgroundImage.getImage(),
            0,
            0,
            
            tableBackgroundImage.getIconWidth(),
            tableBackgroundImage.getIconHeight(),
            this);
      }
    });
    JLabel upper_right_corner_label = new JLabel(Utils.getIcon(ClientConfig.IMG_HEADDER_UPPER_RIGHT_CORNER_BG));
    tableScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, upper_right_corner_label);

      
    //scrollPane.setRowHeaderView( );
    tableScrollPane.getHorizontalScrollBar().setBackground(Color.black);
    tableScrollPane.getHorizontalScrollBar().setForeground(Color.white);
    tableScrollPane.setViewportView(jTable);
    tableScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    tableScrollPane.addMouseListener(this);
    
    //    scrollPane.setOpaque(false);
    tableScrollPane.getViewport().setOpaque(false);
    // add the infoarea as listener for details

//    p.add(jTable.getTableHeader(), BorderLayout.NORTH);
//    p.add(scrollPane, BorderLayout.CENTER);
//    p.add(scrollPane.getVerticalScrollBar(), BorderLayout.EAST);
    //_serverProxy = ServerProxy.getInstance();
    return tableScrollPane;
  }
  

  
  public LobbyListModel getHoldemModel() {
    return (holdem_model);
  }
  
  public LobbyListModel getOmahaModel() {
    return (omaha_model);
  }
 
  public LobbyListModel getStudModel() {
    return (stud_model);
  }
  public LobbyListModel getTournamentModel() {
    return (tournament_model);
  }
  public int getActivePage() {
    return (tabbedPane.getSelectedIndex());
  }

  private boolean isErrorOccured = false;
  public void serverErrorOccured(String info) {
    if (!isErrorOccured) {
      jmOptions.setEnabled(false);
      JOptionPane.showMessageDialog(
          frame, bundle.getString("server.error"), bundle.getString("error"),
          JOptionPane.ERROR_MESSAGE);
      isErrorOccured = true;
      holdem_model.clear();
      omaha_model.clear();
      stud_model.clear();
      sitngo_model.clear();
      tournament_model.clear();
      infoTextArea.setInfo(null);
      //new PingAndConnect(this);
    }
  }

//	protected void fillComponent (JComponent component) {
//00		component.setForeground(ClientConfig.mainColor);
//00		component.setBackground(ClientConfig.secColor);
//	}
  protected LobbyTableModel getSelectedTableByJTable(JTable jTable) {
    if (jTable.getModel() instanceof TableSorter) {
      TableSorter sorter = (TableSorter) jTable.getModel();
      LobbyListModel lobbyListModel =
          (LobbyListModel) (sorter.getModel());
      int visibleNo = jTable.getSelectedRow();
      if (visibleNo < 0) {
        return null;
      }
      int realNo = sorter.getRealNo(visibleNo);
      if (realNo >= 0) {
        LobbyTableModel table = lobbyListModel.getTableModelAtRow(realNo);
        return table;
      }
    }
    return null;
  }
  
//  protected LobbyTournyModel getSelectedTableByJTableTournament(JTable jTable) {
//	    if (jTable.getModel() instanceof TableSorter) {
//	      TableSorter sorter = (TableSorter) jTable.getModel();
//	      LobbyListModel lobbyListModel =
//	          (LobbyListModel) (sorter.getModel());
//	      int visibleNo = jTable.getSelectedRow();
//	      if (visibleNo < 0) {
//	        return null;
//	      }
//	      int realNo = sorter.getRealNo(visibleNo);
//	      if (realNo >= 0) {
//	        LobbyTableModel table = lobbyListModel.getTableModelAtRow(realNo);
//	        return (LobbyTournyModel)table;
//	      }
//	    }
//	    return null;
//	  }

  // MouseListner Interface :
//  public void mouseClicked(MouseEvent e) {
//	if (e.getSource() instanceof JTable) {
//      JTable jTable = (JTable) e.getSource();
//      
//      System.out.println("-----"+jTable.getName());
//      LobbyTableModel table = getSelectedTableByJTable(jTable);
//      System.out.println("table= "+table.getGameType()+",name= "+table.getName()+
//    		  ",state= "+table.getState()+",plr_count= "+table.getPlayerCount());
//      if (e.getClickCount() > 1 && table != null) {
//        doTableRegistration(table);
//      }
//    }
//  }
  public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JTable) {
	      JTable jTable = (JTable) e.getSource();
//	      try {
//	    	  //System.out.println("click count"+e.getClickCount());
//	    	  if(e.getClickCount() == 2){
//				  _serverProxy.getTableList();
//				  try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
//	    	  }
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
	      //System.out.println("-----"+jTable.getName());
	      LobbyTableModel table = getSelectedTableByJTable(jTable);
//	      System.out.println("table= "+table.getGameType()+",name= "+table.getName()+
//	    		  ",state= "+table.getState()+",plr_count= "+table.getPlayerCount());
	      if (e.getClickCount() > 1 && table != null) {
	    	  //_cat.severe("mouse clicked "+table.getName());
	        doTableRegistration(table);
	      }
	    }
	  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popup(getSelectedTable(), e);
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popup(getSelectedTable(), e);
    }
  }

  protected void popup(LobbyTableModel model, MouseEvent e) {

  }

  protected LobbyTableModel getSelectedTable() {
    return getSelectedTableByJTable(getSelectedJTable());
  }

  public void focusGained(FocusEvent e) {
	  //System.out.println("Event type: "+e.getComponent()+"----"+e.getSource());
    if (e.getSource() instanceof JTable) {
      updateSelectedLobbyTable( (JTable) e.getSource());
    }
    
  }

  public void focusLost(FocusEvent e) {
  }

  private void updateSelectedLobbyTable(JTable jTable) {
    LobbyTableModel table = getSelectedTableByJTable(jTable);
    if (table == null) {
      return;
    }
    String tid = table.getName();
    stopCheckOnTable();
    if (table != null) {
      checkOnTable(tid);
      selected_table = table;
//      if(jbSitTable != null)jbSitTable.setEnabled(true);
//      updateJBWait(table); 
    }
    else {
      jbSitTable.setEnabled(false);
    }
    if(gameType_col1 == GameTypes.TYPE_RINGGAME)
    {
	    for (int i = 0; i < _latest_changes.length; i++) {
	    	//System.out.println(selected_tid+"----"+_latest_changes[i].getName());
	    	if (selected_table.getName().equals(_latest_changes[i].getName())) {
	        infoTextArea.lobbyInfoUpdated(_latest_changes[i]);
	      }
	    }
    }
    else if(gameType_col1 == GameTypes.TYPE_SITNGO)
    {
    	for (int i = 0; i < _sitngoList.length; i++) {
	    	//System.out.println(selected_tid+"----"+_latest_changes[i].getName());
	    	if (selected_table.getName().equals(_sitngoList[i].getName())) {
	        infoTextArea.updateSitnGoDetails(_sitngoList[i]);
	      }
	    }
    }
    else if(gameType_col1 == GameTypes.TYPE_TOURNAMENT)
    {
    	for (int i = 0; i < _tournyList.length; i++) {
	    	//System.out.println(selected_tid+"----"+_latest_changes[i].getName());
	    	if (selected_table.getName().equals(_tournyList[i].getName())) {
	        infoTextArea.lobbyInfoUpdated(_tournyList[i]);
	      }
	    }
    }
    else if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER)
    {
    	
    	 for (int i = 0; i < _latest_changes.length; i++) {
 	    	//System.out.println(selected_tid+"----"+_latest_changes[i].getName());
 	    	if (selected_table.getName().equals(_latest_changes[i].getName())) {
 	        infoTextArea.lobbyInfoUpdated(_latest_changes[i]);
 	      }
 	    }
    	
/* commented by rk as term poker table list coming as normal ring game table list   	
 * String plr_det = "";
    	String temp = "";
    	GameEvent ge = null;
    	for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();) {
    		GameEvent curr_ge = (GameEvent) l.next();
			if (selected_tid.substring(3).contains(curr_ge.getGameName().substring(3))) 
    		{
				System.out.println(curr_ge.getGameName());
				if(ge == null)ge = curr_ge;
    			String curr_player_details = curr_ge.getPlayerDetailsString();
				if(!curr_player_details.equals("none"))
				{
					ge.setPlayerDetails(curr_player_details+"`");
				}
    		}
    	}
    	int t = 0;
        for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();) {
          ge = l.next();
          if (selected_table.getName().substring(3).contains(ge.getGameName().substring(3))) {
	    		String str="";
	    		if(ge.get("player-details") != null)
	    		{
	    		  if(!ge.get("player-details").contains("`"))str = "`";
	        	  if(!plr_det.contains(ge.get("player-details")+str))
	        	  {
	        		  plr_det += ge.get("player-details")+str;
	        	  }
	    		}
	    		else if(temp.equals(""))
	    		{
	    			temp = ge.toString();
	    		}
	        
	      }
        }
        
        for (int i = 0; _serverProxy._rtl != null && i < _serverProxy._rtl.getGameCount(); i++) 
		{
    		if (selected_table.getName().substring(3).contains(_serverProxy._rtl.getGameEvent(i).getGameName().substring(3)))
    		{
    			String str="";
    			ge = new GameEvent(_serverProxy._rtl.getGameEvent(i));
	    		if(ge.get("player-details") != null)
	    		{
	    		  if(!ge.get("player-details").contains("`"))str = "`";
	        	  if(!plr_det.contains(ge.get("player-details")+str))
	        	  {
	        		  //filete Termpoker game players
	        		  if(ge.getType() == PokerGameType.Real_TermHoldem ||
	        			 ge.getType() == PokerGameType.Play_TermHoldem)	  
	        		  
	        			  plr_det += ge.get("player-details")+str;
	        	  }
	    		}
	    		else if(temp.equals(""))
	    		{
	    			temp = ge.toString();
	    		}
	        }
        }
        
        //plr_det = removeDuplicatePlayerDetails(plr_det);
        //System.out.println("temp= "+temp);
        //new Exception().printStackTrace();
        //System.out.println("plr_det= "+plr_det);
        GameEvent temp1 = new GameEvent(temp+"player-details="+plr_det+",");
        //System.out.println("temp1= "+temp1.toString());
        //System.out.println("temp1.getGameName()= "+temp1.getGameName());
        if(temp1.getGameName() != null){
	        String name = temp1.getGameName();
	        //System.out.println("temp1.toString()= "+temp1.toString());
	        String newge = temp1.toString().replaceAll(name, name.substring(3));
	        GameEvent newGE = new GameEvent(newge);
	        infoTextArea.lobbyInfoUpdated(new LobbyTableModel(newGE));
        }
*/
    }

  }

  private void updateJBWait(LobbyTableModel table) {
    if (jbWait != null) {
      jbWait.setEnabled(!table.isAcceptingPlayers());
      jbWait.setSelected(
          _serverProxy != null && _serverProxy.isWait(table.getName()));
    }
  }

  /**
   *
   */
  public synchronized void waitMessageRecieved(ServerProxy proxy,
                                               Object message) {
    _cat.fine(">>>> MESSAGE: " + message);
    if (message instanceof com.onlinepoker.actions.Action) {
      com.onlinepoker.actions.Action action =
          (com.onlinepoker.actions.Action) message;
      if (action.getType()
          == ActionConstants.ACTION_TYPE_ERROR) {
        JOptionPane.showMessageDialog(
            frame,
            action.toString(),
            bundle.getString("waiting.list.error"),
            JOptionPane.ERROR_MESSAGE);
        if (jbWait != null) {
          jbWait.getModel().setSelected(false);
        }
        return;
      }
    }
    else {
      return;
    }
    LobbyTableModel lobbyTable = null;
    Component[] tabs = tabbedPane.getComponents();
    for (int i = 0; i < tabs.length; i++) {
      if (tabs[i] instanceof JScrollPane) {
        JScrollPane jScrollPane = (JScrollPane) tabs[i];
        JTable jTable = (JTable) jScrollPane.getViewport().getView();
        if (jTable.getModel() instanceof TableSorter) {
          TableSorter sorter = (TableSorter) jTable.getModel();
          if (sorter.getModel() instanceof LobbyListModel) {
            LobbyListModel lobbyList =
                (LobbyListModel) sorter.getModel();
            // lobbyTable =
            //   lobbyList.getModelByTableId(proxy.getTableId());
            if (lobbyTable != null) {
              break;
            }
          }
        }
      }
    }
    if (lobbyTable == null) {
      _cat.fine(">>>> REMOVE 0");
      proxy.removeFromWaiters();
      return;
    }
    String dialogMessage =
        bundle.getString("thereis.vacant.places") + " \""
        + lobbyTable.getName()
        + "\".\n"
        + bundle.getString("you.have.minute") + ".";

    frame.setState(Frame.NORMAL);
    frame.toFront();
    if (proxy != null && proxy.isRegistered()) {

      ClientRoom room = findClientRoom(lobbyTable);
      JFrame messageOwner = (room == null) ? frame : room;

      if (room != null) {
        room.setWaiterCount(0);
        _cat.fine("room.setWaiterCount(0)");
      }

      if (messageOwner.getState() == Frame.ICONIFIED) {
        messageOwner.setState(Frame.NORMAL);
      }
      messageOwner.toFront();

      JOptionPane.showMessageDialog(messageOwner, dialogMessage);

    }
    else {

      if (frame.getState() == Frame.ICONIFIED) {
        frame.setState(Frame.NORMAL);

      }
      frame.toFront();

      dialogMessage += "\n" + bundle.getString("would.you.like.register");
      int result =
          JOptionPane.showConfirmDialog(
          frame,
          dialogMessage,
          bundle.getString("proposition"),
          JOptionPane.YES_NO_OPTION);

      if (result == JOptionPane.YES_OPTION) {
        lobbyTable.setWaiterCount(0);
        doTableRegistration(lobbyTable);
      }
      else {
        _cat.fine(">>>> REMOVE 1");
        proxy.removeFromWaiters();
      }
    }
    if (jbWait != null) {
      jbWait.setSelected(false);
    }
  }

  private ClientRoom findClientRoom(LobbyTableModel lobbyTable) {
    for (Iterator iter = vClientRooms.iterator(); iter.hasNext(); ) {
      ClientRoom element = (ClientRoom) iter.next();
      if (element.isWindowClosing() == false &&
          lobbyTable.getName() == element.getName()) {
        return element;
      }
    }
    return null;
  }
  
  private SNGTournyLobby findSNGClientRoom(LobbyTableModel lobbyTable) {
    for (Iterator iter = vSNGRooms.iterator(); iter.hasNext(); ) {
    	SNGTournyLobby sng = (SNGTournyLobby) iter.next();
      if (sng.isWindowClosing() == false &&
          lobbyTable.getName().equals(sng._tid)) {
    		  return sng;
      }
    }
    return null;
  }
  
  private MTTTournyLobby findMTTClientRoom(LobbyTableModel lobbyTable) {
    for (Iterator iter = vMTTRooms.iterator(); iter.hasNext(); ) {
    	MTTTournyLobby mtt = (MTTTournyLobby) iter.next();
      if (mtt.isWindowClosing() == false &&
          lobbyTable.getName().equals(mtt._tid)) {
    		  return mtt;
      }
    }
    return null;
  }
  
  private ClientRoom findTPOKERClientRoom(LobbyTableModel lobbyTable) {
    for (Iterator iter = vTPokerRooms.iterator(); iter.hasNext(); ) {
      ClientRoom element = (ClientRoom) iter.next();
      //System.out.println(lobbyTable.getName().contains(element.getGameName().substring(3))+"--"+element.isWindowClosing());
      if (element.isWindowClosing() == false &&
          lobbyTable.getName().contains(element.getGameName().substring(4))) {
        return element;
      }
    }
    return null;
  }

  protected void createLoginFrameAsNeeded() {
    _cat.fine("Creating login frame ");
    if (isAutoLogin()) {
      try {
        this.login(
            loginSettings.getLogin(),
            loginSettings.getPassword().toCharArray());
        frame.setVisible(true);
      }
      catch (Exception ex) {
        newLoginFrame();
      }
    }
    else {
      newLoginFrame();
    }
  }

  protected boolean isAutoLogin() {
    return loginSettings.isAutoLogin();
  }

  protected void newLoginFrame() {
    new LoginFrame(frame, this, loginSettings, bundle.getString("please.login"), true, _serverProxy);
  }

  /**
   * Opens the window with play table.
   */
  private void doTableRegistration(LobbyTableModel lobbyTable) {
    _cat.fine("Table registration " + lobbyTable.getName());
    int openRoomCount = 0;
    try {
      for (Iterator iter = vClientRooms.iterator(); iter.hasNext(); ) {
        ClientRoom element = (ClientRoom) iter.next();
        if (!element.isWindowClosing()) {
          openRoomCount++;
        }
        //_cat.info(element.isWindowClosing() + " Opening " + lobbyTable.getName() + ", iter=" + element.getGameName());
        if (element.isWindowClosing() == false &&  lobbyTable.getName().equals(element.getGameName())) {
          if (element.getState() == Frame.ICONIFIED) {
            element.setState(Frame.NORMAL);
          }
          element.toFront();
          _cat.fine("try push table to front");
          return;
        }
        //throw new Exception (bundle.getString("table.already.opened"));
      }
      if (openRoomCount < 5) {
        _cat.fine("try call openTable openRoomCount = " + openRoomCount);
        openTable(lobbyTable);
      }
      else {
        JOptionPane.showMessageDialog(frame,
                                      bundle.getString("too.many.tables.open"),
                                      bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);
        // END DEBUG !!!
      }
    }
    catch (Exception ex) {
    	ex.printStackTrace();
      _cat.severe("exception in do table registration " + ex.getMessage());
      JOptionPane.showMessageDialog(
          frame,
          ex.getMessage() == null || ex.getMessage().length() == 0 ?
          "" : ex.getMessage(),
          bundle.getString("error"),
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void openTable(LobbyTableModel lobbyTable) {
	 //System.out.println("Creating a client room--" + lobbyTable.getName());
	    if (lobbyTable instanceof LobbyTournyModel) {
	      _cat.fine("Open the tourny lobby");
	      MTTTournyLobby room = findMTTClientRoom(lobbyTable);
	      if(room != null)
	      {
	    	  JFrame messageOwner = (room == null) ? frame : room;
	
		      if (messageOwner.getState() == Frame.ICONIFIED) {
		        messageOwner.setState(Frame.NORMAL);
		      }
		      messageOwner.toFront();
	      }
	      else
	      {
	    	  MTTTournyLobby cr = createTournyLobby((LobbyTournyModel) lobbyTable);
	    	  vMTTRooms.add(cr);
	      }
	    }
	    else if (gameType_col1 == GameTypes.TYPE_SITNGO) {
	      _cat.fine("Open the table lobby");
	      SNGTournyLobby room = findSNGClientRoom(lobbyTable);
	      if(room != null)
	      {
	    	  JFrame messageOwner = (room == null) ? frame : room;
	
		      if (messageOwner.getState() == Frame.ICONIFIED) {
		        messageOwner.setState(Frame.NORMAL);
		      }
		      messageOwner.toFront();
	      }
	      else
	      {
		      SNGTournyLobby cr = createSNGTournyLobby((LobbySitnGoModel) lobbyTable);
		      vSNGRooms.add(cr);
	      }
	    }
	    else if (gameType_col1 == GameTypes.TYPE_TERMINALPOKER) {
	      ClientRoom room = findTPOKERClientRoom(lobbyTable);
	      _cat.finest("lobbyTable "+lobbyTable);
	      if(room != null)
	      {
	    	  JFrame messageOwner = (room == null) ? frame : room;
	
		      if (messageOwner.getState() == Frame.ICONIFIED) {
		        messageOwner.setState(Frame.NORMAL);
		      }
		      messageOwner.toFront();
	      }
	      else
	      {
	    	  //LobbyTableModel ltm = getTableFromPool(lobbyTable.getName());
	    	  double moneyMin = lobbyTable.getMinBuyIn();
	    	  double moneyMax = lobbyTable.getMaxBuyIn();
	    	  String poolName = lobbyTable.getName(); //.substring(4);
	    	  double worth = lobbyTable.isRealMoneyTable()?_serverProxy.realWorth() : _serverProxy.playWorth();
	    	  String choice = null;
	    	  //_cat.severe("getMaxBet "+lobbyTable.getMaxBet());
	    	  boolean res = newMoneyRequest(poolName, frame, worth, moneyMin, moneyMax, lobbyTable.getMaxBet() > 1?true:false, choice);
	    	  
	    	  
//	    	  LobbyTableModel ltm = getTableFromPool(lobbyTable.getName());
//	    	  if(ltm != null)
//	    	  {
//	    		  ClientRoom cr = createClientRoom(ltm);
//	    		  vTPokerRooms.add(cr);
//	    	  }
//	    	  else
//	    	  {
//	    		  JOptionPane.showInternalMessageDialog(frame.getContentPane(), 
//	    				  "Pool is full, please try again", 
//                          "INFO", JOptionPane.INFORMATION_MESSAGE);
//	    	  }
	      }
	    }
	    else if (lobbyTable instanceof LobbyTableModel) {
	      ClientRoom cr = createClientRoom(lobbyTable);
	      vClientRooms.add(cr);
	    }
	    else {
	      throw new IllegalStateException(lobbyTable.toString());
	    } 
  }

  protected ClientRoom createClientRoom(LobbyTableModel lobbyTable) {
    _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
        _serverProxy;
    ClientRoom cr = new ClientRoom(_serverProxy, lobbyTable, frame);
    return cr;
  }
  
  protected SNGTournyLobby createSNGTournyLobby(LobbySitnGoModel lobbyTable) {
    _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
                   _serverProxy;
    SNGTournyLobby cr = new SNGTournyLobby(_serverProxy, lobbyTable, frame);
    return cr;
  }
  
  protected MTTTournyLobby createTournyLobby(LobbyTournyModel lobbyTable) {
    _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
                   _serverProxy;
    MTTTournyLobby cr = new MTTTournyLobby(_serverProxy, lobbyTable, frame);
    return cr;
  }
  
  public void dummyLogin()
  {
	  frame.setTitle(_serverProxy._name + " - " + bundle.getString("lobby"));
      
  }
		  
		  
  public void login(String login, char[] password) throws
      AuthenticationException {
    if (_serverProxy.isLoggedIn()) {
      try {
        _serverProxy.logout();
      }
      catch (Exception e) {}
    }
    wantLogin(login, password);
    if (_serverProxy.isLoggedIn()) {
      jmOptions.setEnabled(true);
      frame.setTitle(login + " - " + bundle.getString("lobby"));
      _cat.fine("!!!_____connected_____!!!");

      int pref = credentialManager.getPreferences();
      settings = new ClientSettings(pref);
      updateSettingsMenuItems(settings);
      map = CredentialManager.getPlayerNotes();
//      ClientSettings storedSettings = _serverProxy.loadClientSettings();
//      if (storedSettings != null) {
//        updateSettingsMenuItems(storedSettings);
//      }
      
    }
    isErrorOccured = false;
  }
//by rk
	public int loginFromBrowser(String login, char[] password)
			throws AuthenticationException {
		int result = 0;
		if (_serverProxy.isLoggedIn()) {
			try {
				_serverProxy.logout();
			} catch (Exception e) {
			}
		}
		result = wantLoginFromBrowser(login, password);
		if (_serverProxy.isLoggedIn()) {
			//jmOptions.setEnabled(true);
			//frame.setTitle(login + " - " + bundle.getString("lobby"));
			//_cat.fine("!!!_____connected_____!!!");

			int pref = credentialManager.getPreferences();
			settings = new ClientSettings(pref);
			updateSettingsMenuItems(settings);
			map = CredentialManager.getPlayerNotes();
			// ClientSettings storedSettings =
			// _serverProxy.loadClientSettings();
			// if (storedSettings != null) {
			// updateSettingsMenuItems(storedSettings);
			// }

		}
		isErrorOccured = false;
		return result;
	}
  
  
  public String getLoginMessage() {
    return null;
  }

  protected void wantLogin(String login, char[] password) throws
      AuthenticationException {
    int r = 0;
    try {
      r = _serverProxy.login(login, new String(password)).getResult();
    }
    catch (Exception e) {
      _cat.severe("Login failed " + e.getMessage());
    }

    if (r != 1) {
      throw new AuthenticationException("Login Failed");
    }
    try {
    	// refreshes the table and tournament lists 
    	_serverProxy.startRefreshTableListThread();
//    	_serverProxy.ping();
//    	_serverProxy.getTounamentList();
//    	_serverProxy.getTableList();
    }
    catch (Exception e) {
      _cat.severe("Getting Table list " + e.getMessage());
    }

    _cat.fine("+++_____We_connected_____+++");
  }

//by rk
	protected int wantLoginFromBrowser(String login, char[] password)
			throws AuthenticationException {
		int r = 0;
		try {
			r = _serverProxy.login(login, new String(password)).getResult();
		} catch (Exception e) {
			_cat.severe("Login failed " + e.getMessage());
		}

		if (r != 1) {
			throw new AuthenticationException("Login Failed");
		}
		try {
			// refreshes the table and tournament lists
			//_serverProxy.startRefreshTableListThread();
			// _serverProxy.ping();
			// _serverProxy.getTounamentList();
			// _serverProxy.getTableList();
		} catch (Exception e) {
			_cat.severe("Getting Table list " + e.getMessage());
		}

		_cat.fine("+++_____We_connected_____+++");
		return r;
	}
  
  private void updateSettingsMenuItems(ClientSettings settings) {
	//System.out.println("updateSettingsMenuItems: "+settings);
	//jmiBigSymbols.setState(settings.isBigSymbols());
    jmiAutoBigBlind.setState(settings.isAutoPostBlind());
    jmiWaitBigBlind.setState(settings.isWaitForBigBlind());
    //jmiShowBestCards.setState(settings.isShowBestCards());
    //jmiMuckLoosingCards.setState(settings.isMuckLosingCards());
    jmiSound.setState(settings.isSound());
    jmiFourColorCards.setState(settings.isFourColorCards());
    _serverProxy._settings.copy(settings);
    jmOptions.setEnabled(true);
  }

  private void lobbyExit() {
    ClientRoom cr;
    boolean flag = true;
    
    credentialManager.saveGameStatusInfo(gameType_col1, gameType_cg_col2, gameType_cg_col3,gameType_tp_col2, gameType_tp_col3,
										 gameType_sng_col2, gameType_sng_col3, gameType_sng_col4,
										 gameType_mtt_col2,gameType_mtt_col3, gameType_mtt_col4,
										 isCGMicroSel.isSelected(), isCGLowSel.isSelected(), isCGMediumSel.isSelected(), isCGHighSel.isSelected(),
										 isSNGFreerolSel.isSelected(), isSNGMicroSel.isSelected(), isSNGLowSel.isSelected(), isSNGMediumSel.isSelected(), isSNGHighSel.isSelected(),
										 isMTTFreerolSel.isSelected(), isMTTMicroSel.isSelected(), isMTTLowSel.isSelected(), isMTTMediumSel.isSelected(), isMTTHighSel.isSelected(),
										 isRBPlay);
    credentialManager.savePlayerNotes();
    for (Iterator i = vClientRooms.iterator(); i.hasNext(); ) {
      Object object = i.next();
      if (object instanceof ClientRoom) {
        cr = (ClientRoom) object;
        cr.tryCloseRoom();
        if (cr.isWindowClosing() == false) {
          flag = false;
        }
      }
    }
    if (flag) {
      _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
          _serverProxy;
      if (cg_model != null) {
        _serverProxy.removeLobbyModelChangeListener(cg_model);
      }
      if (tp_model != null) {
        _serverProxy.removeLobbyModelChangeListener(tp_model);
      }
      if (holdem_model != null) {
        _serverProxy.removeLobbyModelChangeListener(holdem_model);
      }
      if (omaha_model != null) {
          _serverProxy.removeLobbyModelChangeListener(omaha_model);
        }
      if (stud_model != null) {
          _serverProxy.removeLobbyModelChangeListener(stud_model);
        }
      if (sitngo_model != null) {
          _serverProxy.removeLobbyModelChangeListener(sitngo_model);
        }
      if (tournament_model != null) {
        _serverProxy.removeLobbyModelChangeListener(tournament_model);
      }
      
      try {
        _serverProxy.logout();
      }
      catch (Exception e) {}

      if (_serverProxy != null) {
        _serverProxy.stopWatchOnTable();
      }
      ServerProxy.removeAllWaiters();
      System.exit(0);
    }
    else {
      JOptionPane.showMessageDialog(
          frame,
          bundle.getString("close.all.tables"),
          bundle.getString("proposition"),
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  protected void init_player(String ip) throws
      MalformedURLException {
    try {
      _serverProxy = ServerProxy.getInstance(ip, 8985, frame);
    }
    catch (Exception e) {
      _cat.severe("Unable to connect to " + ip);
      System.exit( -1);
    }
    _serverProxy.addLobbyModelChangeListener(this);
  }

  /**
   * Wait list button pressed.
   */
  private void waitingButtonPressed() {
	  if(selected_table.getName().equals(""))return;
	  if (_serverProxy == null) {
      _serverProxy = ServerProxy.getInstance();
    }
    // check if we already waiting for this table ..
    if (!_serverProxy.isWait(selected_table.getName())) {
      _serverProxy.addToWaiters(selected_table.getName());
      JOptionPane.showMessageDialog(
          frame,
          "Waiting on table " + selected_table.getName(),
          "OK",
          JOptionPane.INFORMATION_MESSAGE);
    }
    else {
      JOptionPane.showMessageDialog(
          frame,
          "Already waiting on this table " + selected_table.getName(),//this.getSelectedTable().getName(),
          "ERROR",
          JOptionPane.ERROR_MESSAGE);

    }
  }

  private void updateSettings(JRadioButtonMenuItem menuItem) {
	  credentialManager.saveLanguage(menuItem.getLabel());
  }
  
  private void updateSettings(JCheckBoxMenuItem menuItem) {
    //ClientSettings settings = _serverProxy.loadClientSettings();
	String command = ( menuItem.getAccessibleContext().getAccessibleName());
	if (command.equals(bundle.getString("auto.post.blind"))) {
	  settings.setAutoPostBlind(menuItem.getState());
    }
    else if (command.equals(bundle.getString("wait.for.blind"))) {        
      settings.setWaitForBigBlind(menuItem.getState());
    }
    else if (command.equals(bundle.getString("show.best.cards"))) {        
      settings.setShowBestCards(menuItem.getState());
    }
    else if (command.equals(bundle.getString("muck.losing.cards"))) {        
      settings.setMuckLosingCards(menuItem.getState());
    }
    else if (command.equals(bundle.getString("four.color.cards"))) { 
    	System.out.println("menuItem.getState() "+menuItem.getState());
      settings.setFourColorCards(menuItem.getState());
	}
    //@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
    else if (command.equals(bundle.getString("sound"))) {        
          settings.setSound(menuItem.getState());
    }
//    else if (menuItem == jmiRandomDelay) {
//      settings.setRandomDelay(menuItem.getState());
//    }
    credentialManager.savePreferences(settings.intVal());
    updateSettingsMenuItems(settings);
  }

  public void openBrowser(String str)
  {
	  if(_serverProxy._name == null || _serverProxy._password == null)
		  BrowserLaunch.openURL(MessageFormat.format(bundle.getString(str), new Object[]{null, null}));
	  else
	  {
		  String name = Base64.encode(_serverProxy._name.getBytes());
		  String pass = Base64.encode(_serverProxy._password.getBytes());
		  BrowserLaunch.openURL(MessageFormat.format(bundle.getString(str), new Object[] {name, pass}));
	  }
  }
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source instanceof JRadioButtonMenuItem) {
    	updateSettings( (JRadioButtonMenuItem) source);
    	return;
    }
    else if (source instanceof JCheckBoxMenuItem) {
        updateSettings( (JCheckBoxMenuItem) source);
        return;
      }
    else if (source instanceof JCheckBox) {
    	tableListUpdatedBySelectedCheckBox(e.getActionCommand());
        return;
      }
    else if (source instanceof JToggleButton) {
      tableListUpdatedBySelectedLabel(e.getActionCommand());
      if (source == jbWait && jbWait != null) {
        waitingButtonPressed();
        return;
      }
      return;
    }
    String command =
        ( (JMenuItem) e.getSource())
        .getAccessibleContext()
        .getAccessibleName();
    if (command == null) {
      return;
    }
    if (command.equals(bundle.getString("login"))) {
      newLoginFrame();
    }
    else if (command.equals(bundle.getString("logout"))) {
      infoTextArea.setInfo(null);
      try {
        _serverProxy.logout();
      }
      catch (Exception ex) {}
    }
    else if (command.equals(bundle.getString("exit"))) {
      if (jmiAutoLogin != null) {
        loginSettings.setAutoLogin(jmiAutoLogin.isSelected());
      }
      loginSettings.saveSettings();
      lobbyExit();
    }
    else if (command.equals(bundle.getString("cashier"))) {
      _cat.info(">>> Call to cashier <<<");
      	openBrowser("cashier.url");
    }
    else if (command.equals(bundle.getString("change.email.add"))) {
    	openBrowser("change.email.add.url");
      }
    else if (command.equals(bundle.getString("change.personal.details"))) {
    	openBrowser("change.personal.details.url");
      }
    else if (command.equals(bundle.getString("change.password"))) {
    	openBrowser("change.password.url");
      }
    else if (command.equals(bundle.getString("change.avatar"))) {
    	openBrowser("change.avatar.url");
      }
    else if (command.equals(bundle.getString("change.email.notif"))) {
    	openBrowser("change.email.notif.url");
      }
    else if (command.equals(bundle.getString("principles"))) {
    	BrowserLaunch.openURL(bundle.getString("principles.url"));
      }
    else if (command.equals(bundle.getString("selfexclusion"))) {
    	BrowserLaunch.openURL(bundle.getString("selfexclusion.url"));
      }
    
    else if (command.equals(bundle.getString("game.help"))) {
        BrowserLaunch.openURL(bundle.getString("game.help.url"));
      }
    else if (command.equals(bundle.getString("game.rules"))) {
        BrowserLaunch.openURL(bundle.getString("game.rules.url"));
      }
  }

  private void openTablesAsNeed(LobbyTableModel[] changes) {
    if (_table_list != null && _table_list.getGameCount() > 0) {
      for (int changesIndex = 0; changesIndex < changes.length; changesIndex++) {
        for (int idsIndex = 0; idsIndex < _table_list.getGameCount();
             idsIndex++) {
          if (_table_list.getGameEvent(idsIndex) != null &&
              changes[changesIndex].getName() ==
              _table_list.getGameEvent(idsIndex).getGameName()) {
            //openTable(changes[changesIndex]);
            //_table_list.getGameEvent(idsIndex) = -1;
          }
        }
      }
    }
  }

  
  
  public void tableListUpdateBySelectedLabel(LobbyTableModel[] changes) {
    //_cat.fine("LobbyUser Impl tableList update by selecting tab");
    _latest_changes = changes;
    if(changes == null) return;
    for (int i = 0; i < changes.length; i++) {
      // update the info panel if required
    	if (selected_tid.equals(changes[i].getName())) {
    		if(gameType_col1 == GameTypes.TYPE_SITNGO)infoTextArea.updateSitnGoDetails((LobbySitnGoModel)changes[i]);
    		else infoTextArea.lobbyInfoUpdated(changes[i]);
      }
    }
    
  }
  
  public void tableListUpdateBySelectedLabel(LobbyTournyModel[] changes) {
	if(changes == null && changes.length == 0) return;
	for (int i = 0; i < changes.length; i++) {
      if (selected_table.getName().equals(changes[i].getName())) {
    		infoTextArea.lobbyInfoUpdated(changes[i]);
      }
    }
    
  }
  
  class tableModelListener
      implements TableModelListener {

    private TableSorter sorter;
    private JTable jTable = null;
    
    

    tableModelListener(TableSorter sorter) {
      this.sorter = sorter;
    }
    
//    tableModelListener(RowSorter<M> sorter) {
//        this.sorter = sorter;
//      }

    public void setJTable(JTable jTable) {
      //_cat.fine("Setting table ....");
      this.jTable = jTable;
    }

    public void tableChanged(TableModelEvent e) {
      sorter.sort(this);
      if (jTable != null) {
        jTable.repaint();
      }
    }
  }

  private void setLookAndFeel(SyntheticaBlackEyeLookAndFeel theme) {
    try {
//      if (theme != null) {
//        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(theme);
//      }
//    	UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
//    	UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
		
      //UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaSkyMetallicLookAndFeel");  
//UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
      //UIManager.setLookAndFeel(new SyntheticabStandardLookAndFeel());
    }
    catch (Exception ex) {
      _cat.severe("Failed loading L&F: :-@" + ex.getMessage());
    }
  }

  /**
   * Agneya NEW CLASS
   * FIX 6
   */

  public void stopCheckOnTable() {
    selected_tid = "";
  }

  public void checkOnTable(String tid) {
    selected_tid = tid;
  }

  private static class InfoJLabel
      extends JPanel {

    //protected JFrame frame;
    //protected JPanel topPane, bottomPane;
    protected JPanel mainPane;
    //DefaultTableCellRenderer renderer ;
    //protected TableColumnModel columnNames;
    public SortableTableModel activeTableModel, waitingTableModel;
    public JScrollPane activeTablePane, waitingTablePane;
    protected JTable activeTable, waitingTable;
    protected JLabel playersListLable,tourneyDetailsLable;
    protected JTextPane taTourneyDetails;
    protected JPanel p;
    SimpleAttributeSet aSet = new SimpleAttributeSet(); 
    //final SimpleAttributeSet BLACK1 = new SimpleAttributeSet();

    
    public InfoJLabel() {
      super(new BorderLayout());
      super.setOpaque(false);
      //the main JPanel where u add all the components
      
      p = new JPanel();
      p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
      
      playersListLable = new JLabel(bundle.getString("play.chips"),
      		(Icon)Utils.getIcon(ClientConfig.IMG_LOBBY_PLAYERLISTBOX_TOP_BG),
      		SwingConstants.LEFT);
      
      tourneyDetailsLable = new JLabel(bundle.getString("tourney.details"),
      		(Icon)Utils.getIcon(ClientConfig.IMG_LOBBY_PLAYERLISTBOX_TOP_BG),
      		SwingConstants.LEFT);
      
      taTourneyDetails = new JTextPane();
//      {
//    	  public void paintComponent(Graphics g)
//	    {
//		    // Scale image to size of component
//		    Dimension d = getSize();
//		    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_LOBBY_TOURNAMENT_INFO_BG);
//		    g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
//		    setOpaque( false );
//		    super.paintComponent(g);
//		}	
//	  };
	  taTourneyDetails.setEditable(false);
	  StyleConstants.setAlignment(aSet, StyleConstants.ALIGN_CENTER);
	  StyledDocument doc = taTourneyDetails.getStyledDocument();  
      doc.setCharacterAttributes(105, doc.getLength()-105, aSet, false);  
      doc.setParagraphAttributes(0, 104, aSet, false);
      mainPane = new JPanel(new BorderLayout());
      mainPane.setOpaque(false);
      mainPane.add(getPlayerDetailsPanel(), BorderLayout.NORTH);
      add(mainPane);
    }
    private void changeTab(String type)
    {
    	 if(type.equals("AT"))
	     {
    		 waitingTablePane.setVisible(false);
    		 waitingTablePane.setEnabled(false);
    		 activeTablePane.setVisible(true);
    		 p.add(activeTablePane);
   			 p.remove(waitingTablePane);
	     }
    	 else if(type.equals("WT"))
		 {
    		 waitingTablePane.setVisible(true);
    		 waitingTablePane.setEnabled(true);
    		 activeTablePane.setVisible(false);
    		 p.remove(activeTablePane);
   			 p.add(waitingTablePane);
   			 junkFuntionToEnableWaitingTablePane();
   	      
   		}
    }
    private void change(String type)
    {
    	 if(type.equals("CG"))
	     {
    		 tourneyDetailsLable.setVisible(false);
    		 taTourneyDetails.setVisible(false);
    		 playersListLable.setVisible(true);
    		 activeTablePane.setVisible(true);
    		 if(waitingTablePane.isEnabled())waitingTablePane.setVisible(true);
	     }
		 else
		 {
			 tourneyDetailsLable.setVisible(true);
			 taTourneyDetails.setVisible(true);
    		 playersListLable.setVisible(false);
    		 activeTablePane.setVisible(false);
    		 waitingTablePane.setVisible(false);
    		 try {
				if(type.equals("SNG"))
				{
					if(_sitngoList != null && _sitngoList.length > 0)updateSitnGoDetails(_sitngoList[0]);
					else updateSitnGoDetails(null);
				}
				if(type.equals("MTT"))
				{
					if(_tournyList != null && _tournyList.length > 0)updateTournamentDetails(_tournyList[0]);
					else updateTournamentDetails(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	 }
    }

    public void junkFuntionToEnableWaitingTablePane()
    {
	  btnAddToWL.setVisible(true);
	  btnAddToWL.setVisible(false);
    }
    
    public void showAddToWLButton()
    {
	  btnAddToWL.setVisible(true);
	}
    public void removeAddToWLButton()
    {
	  btnAddToWL.setVisible(false);
    }
    
    public void updateSitnGoDetails(LobbySitnGoModel _sitngoList)
    {
    	if(_sitngoList == null)
    	{
    		StringBuilder sb = new StringBuilder();
    		sb.append("Sit & Go").append("\n\nStarts when   players")
    		.append("\nare registered")
    		.append("\n\n").append("\n\n\n")
    		.append("\nSee you at the tables! ")
    		.append("\n");
    		tourneyDetailsLable.setText("");
	    	taTourneyDetails.setText(sb.toString());
	    	sb = null;
//	    	"Sit & Go"
//	    	+"\n\nStarts when   players"
//	    	+"\nare registered"
//	    	+"\n\n"
//	    	+"\n\n\n"
//	    	+"\nSee you at the tables! "
//	    	+"\n");
    	}
    	else
    	{
    		StringBuilder sb = new StringBuilder();
    		sb.append("Sit & Go").append("\n\nStarts when  ")
    		.append(_sitngoList.getPlayerCapacity())
    		.append(" players")
    		.append("\nare registered\n\n\n\n\n")
    		.append("\nSee you at the tables! ").append("\n");
	    	tourneyDetailsLable.setText("" + _sitngoList.getName());
	    	taTourneyDetails.setText(sb.toString());
	    	sb = null;
//	    	"Sit & Go"
//	    	+"\n\nStarts when  "+_sitngoList.getPlayerCapacity()+" players"
//	    	+"\nare registered"
//	    	+"\n\n"
//	    	+"\n\n\n"
//	    	+"\nSee you at the tables! "
//	    	+"\n");
    	}
        
    }
    
    public void updateTournamentDetails(LobbyTournyModel te)
    {
    	if(te == null)
    	{
    		StringBuilder sb = new StringBuilder();
    		sb.append("Tournament Details: \n   \n  ")
    		.append("\nStarts in ")
    		.append("\nMax Players: ")
    		.append("\n\nThis one rebuy and one addon ")
    		.append("\nGuarenteed Prize pool: \n")
    		.append("\nSee you at the tables! ");
    		
          taTourneyDetails.setText(sb.toString());
          sb = null;
//        	"Tournament Details: \n   \n  "
//	    	+"\nStarts in "
//	    	+"\nMax Players: "
//	    	+"\n\nThis one rebuy and one addon "
//	    	+"\nGuarenteed Prize pool: \n"
//	    	+"\nSee you at the tables! ");
    	}
    	else
    	{
    		StringBuilder sb = new StringBuilder();
    		String time = te.getDelta() > 0 ? sb.append(te.getDelta() / 60).append(" hrs ").append(te.getDelta() % 60).append(" mins ").toString()
    										:"Running";
    		sb = null;
	    	tourneyDetailsLable.setText("" + te.getName());
	    	StringBuilder sb1 = new StringBuilder();
	    	sb1.append("Tournament Details: \n")
	    	.append(te.getSchedule()).append(" \n  ")
	    	.append(te.getName()).append("\nStarts in ")
	    	.append(time).append("\nMax Players: ")
	    	.append(te.getPlayerCapacity())
	    	.append("\n\nThis one rebuy and one addon ")
	    	.append("\nGuarenteed Prize pool: \n")
	    	.append(te.getPrizePool()).append("\nSee you at the tables! ");
	    	taTourneyDetails.setText(sb1.toString());
	    	sb1 = null;
//	    	"Tournament Details: \n"+te.getSchedule()+" \n  "+te.getName()
//	    	+"\nStarts in "+ time
//	    	+"\nMax Players: "+te.getPlayerCapacity()
//	    	+"\n\nThis one rebuy and one addon "
//	    	+"\nGuarenteed Prize pool: \n"+te.getPrizePool()
//	    	+"\nSee you at the tables! ");
    	}
        
    }
    private JPanel getPlayerDetailsPanel() 
    {
    	playersListLable.setForeground(new Color(0xFFFFFF));
        playersListLable.setVerticalTextPosition(AbstractButton.CENTER);
        playersListLable.setHorizontalTextPosition(AbstractButton.CENTER);
        playersListLable.setBounds(433, 235, 133, 18);
        playersListLable.setPreferredSize(new Dimension(145, 18));
        
        
        tourneyDetailsLable.setForeground(new Color(0xFFFFFF));
        tourneyDetailsLable.setVerticalTextPosition(AbstractButton.CENTER);
        tourneyDetailsLable.setHorizontalTextPosition(AbstractButton.CENTER);
        tourneyDetailsLable.setBounds(433, 235, 145, 18);
        tourneyDetailsLable.setPreferredSize(new Dimension(145, 18));
        tourneyDetailsLable.setVisible(false);
        
        //taTourneyDetails.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        taTourneyDetails.setBounds(433, 253, 145, 164);
        taTourneyDetails.setAlignmentX(SwingConstants.CENTER);
        taTourneyDetails.setMargin(new Insets(2, 0, 2, 0));
        taTourneyDetails.setVisible(false);
        taTourneyDetails.setFont(new Font("Verdana", Font.PLAIN, 10));
        tourneyDetailsLable.setText("" );
    	
        		  
        //UIManager.getDefaults().put("TableHeader.cellBorder",BorderFactory.createEmptyBorder()); 
        
        String[] headerStr = {bundle.getString("tab.players"),bundle.getString("chips")};
	    int[] columnWidth = {75,70};
	    
	    activeTableModel = new SortableTableModel() {
	      public Class getColumnClass(int col) {
	        switch (col) {
	          case  0: return Object.class;
	          case  1: return Object.class;
//	          case  2: return String.class;
//	          case  3: return Boolean.class;
	          default: return Object.class;
	        }
	      }
	      public boolean isCellEditable(int row, int col) {
	        return false;
	      }  
	      public void setValueAt(Object obj, int row, int col) {
	        switch (col) {
	          //case  2: super.setValueAt(new Integer(obj.toString()), row, col); return;
	          default: super.setValueAt(obj, row, col); return;
	        }
	      }
	    };
	    activeTableModel.setDataVector(null,headerStr);
	     
	    activeTable = new JTable(activeTableModel){
		public Component prepareRenderer(TableCellRenderer renderer,int row, int col) {
      
  				try {
					Component comp = super.prepareRenderer(renderer, row, col);
					//even index, selected or not selected
					if (row % 2 != 0){// && !isCellSelected(Index_row, Index_col)) {
					  comp.setBackground(new Color(243, 243, 244));
					} 
					else {
					  comp.setBackground(new Color(231, 232, 233));
					}
					return comp;
				} catch (Exception e) {
					return null;
				}
  		      }
  		    };
	    activeTable.setRowHeight(12);
	    activeTable.setShowVerticalLines(false);
	    activeTable.setShowHorizontalLines(false);
	    activeTable.setShowGrid(false);
	    activeTable.setOpaque(false);
	    activeTable.setPreferredSize(new Dimension(145, 133));
	    activeTable.setBounds(442, 270, 145, 133);
	    activeTable.setFillsViewportHeight(true);
	    activeTable.setAutoCreateRowSorter(true);
	    ((DefaultTableCellRenderer)activeTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

	    
	    JTableHeader headerAT = new JTableHeader( activeTable.getColumnModel());
		    
	    ((JComponent)headerAT.getDefaultRenderer()).setOpaque(true);
	    activeTable.setEnabled(true);
	       headerAT.setPreferredSize(new Dimension(145, 20)); 
	       headerAT.setSize(new Dimension(145, 20));
	    activeTablePane = new JScrollPane(JScrollPane.
                VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.
                HORIZONTAL_SCROLLBAR_NEVER);
	    activeTablePane.setViewport(new JViewport() {
	        public void paintComponent(Graphics g) {
	          g.drawImage(
	        		  Utils.getIcon(ClientConfig.IMG_PLAYER_LIST_BG).getImage(),
	              0,
	              0,
	              
	              Utils.getIcon(ClientConfig.IMG_PLAYER_LIST_BG).getIconWidth(),
	              Utils.getIcon(ClientConfig.IMG_PLAYER_LIST_BG).getIconHeight(),
	              this);
	        }
	      });
	    activeTablePane.setViewportView(activeTable);
	    activeTablePane.setBorder(new EmptyBorder(0, 0, 0, 0));
	    activeTablePane.setPreferredSize(new Dimension(145,133));
	    activeTablePane.setBounds(442, 270, 145, 133);
	    JLabel upper_right_corner_label1 = new JLabel(Utils.getIcon(ClientConfig.IMG_HEADDER_UPPER_RIGHT_CORNER_BG));
	    activeTablePane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, upper_right_corner_label1);
	    activeTablePane.setEnabled(true);
        
	    waitingTableModel = new SortableTableModel() {
	      public Class getColumnClass(int col) {
	        switch (col) {
	          case  0: return Object.class;
	          case  1: return Object.class;
	          //case  2: return String.class;
//	          case  3: return Boolean.class;
	          default: return Object.class;
	        }
	      }
	      public boolean isCellEditable(int row, int col) {
	        return false;
	        
	      }  
	      public void setValueAt(Object obj, int row, int col) {
	        switch (col) {
	          //case  2: super.setValueAt(new Integer(obj.toString()), row, col); return;
	          default: super.setValueAt(obj, row, col); return;
	        }
	      }
	    };
	    waitingTableModel.setDataVector(null,headerStr);
	     
	    waitingTable = new JTable(waitingTableModel){
		public Component prepareRenderer(TableCellRenderer renderer,int row, int col) {
      
  				Component comp = super.prepareRenderer(renderer, row, col);
  		        //even index, selected or not selected
  		        if (row % 2 != 0){// && !isCellSelected(Index_row, Index_col)) {
  		          comp.setBackground(new Color(243, 243, 244));
  		        } 
  		        else {
  		          comp.setBackground(new Color(231, 232, 233));
  		        }
  		        return comp;
  		      }
  		    };
  		waitingTable.setVisible(true);
  		waitingTable.setRowHeight(12);
  		waitingTable.setShowVerticalLines(false);
  		waitingTable.setShowHorizontalLines(false);
  		waitingTable.setShowGrid(false);
  		waitingTable.setOpaque(false);
  		waitingTable.setPreferredSize(new Dimension(145, 137));
  		waitingTable.setBounds(442, 270, 145, 137);
  		waitingTable.setFillsViewportHeight(true);
	    waitingTable.setAutoCreateRowSorter(true);
	    ((DefaultTableCellRenderer)waitingTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

    
	    JTableHeader headerWT = new JTableHeader( waitingTable.getColumnModel() );
	    ((JComponent)headerWT.getDefaultRenderer()).setOpaque(false);
	    waitingTablePane = new JScrollPane(JScrollPane.
                VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.
                HORIZONTAL_SCROLLBAR_NEVER);
	    waitingTablePane.setViewport(new JViewport() {
	        public void paintComponent(Graphics g) {
	          g.drawImage(
	        		  Utils.getIcon(ClientConfig.IMG_PLAYER_LIST_BG).getImage(),
	              0,
	              0,
	              
	              Utils.getIcon(ClientConfig.IMG_PLAYER_LIST_BG).getIconWidth(),
	              Utils.getIcon(ClientConfig.IMG_PLAYER_LIST_BG).getIconHeight(),
	              this);
	        }
	      });
	    waitingTablePane.setViewportView(waitingTable);
	    waitingTablePane.setBorder(new EmptyBorder(0, 0, 0, 0));
	    waitingTablePane.setPreferredSize(new Dimension(145,137));
	    waitingTablePane.setBounds(442, 270, 145, 137);
	    JLabel upper_right_corner_label = new JLabel(Utils.getIcon(ClientConfig.IMG_HEADDER_UPPER_RIGHT_CORNER_BG));
	    waitingTablePane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, upper_right_corner_label);
	    waitingTablePane.setVisible(false);
	    waitingTablePane.setEnabled(false);
	    
        playersListLable.setAlignmentX(CENTER_ALIGNMENT);
        tourneyDetailsLable.setAlignmentX(CENTER_ALIGNMENT);
        taTourneyDetails.setAlignmentX(CENTER_ALIGNMENT);
        activeTablePane.setAlignmentX(CENTER_ALIGNMENT);
        waitingTablePane.setAlignmentX(CENTER_ALIGNMENT);
        p.add(playersListLable);
        p.add(tourneyDetailsLable);
        p.add(taTourneyDetails);
        p.add(activeTablePane);
        p.add(waitingTablePane);
        return p;
     }
           
  

    class JComponentTableCellRenderer implements TableCellRenderer {
    	  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
    	      boolean hasFocus, int row, int column) {
    	    return (JComponent) value;
    	  }
    	}
    
    class HeaderListener extends MouseAdapter {
	    JTableHeader   header ;
		    
	    //((JComponent)headerAT.getDefaultRenderer()).setOpaque(false);
	    SortButtonRenderer renderer;
	  
	    HeaderListener(JTableHeader header,SortButtonRenderer renderer) {
	      this.header   = header;
	      
	      this.renderer = renderer;
	    }
	  
	    public void mousePressed(MouseEvent e) {
	      int col = header.columnAtPoint(e.getPoint());
	      int sortCol = header.getTable().convertColumnIndexToModel(col);
	      renderer.setPressedColumn(col);
	      renderer.setSelectedColumn(col);
	      header.repaint();
	      
	      if (header.getTable().isEditing()) {
	        header.getTable().getCellEditor().stopCellEditing();
	      }
	      //System.out.println("mouse pressed in row header");
	      boolean isAscent;
	      if (SortButtonRenderer.DOWN == renderer.getState(col)) {
	        isAscent = true;
	      } else {
	        isAscent = false;
	      }
	      ((SortableTableModel)header.getTable().getModel())
	        .sortByColumn(sortCol, isAscent);    
	    }
	  
	    public void mouseReleased(MouseEvent e) {
	      int col = header.columnAtPoint(e.getPoint());
	      renderer.setPressedColumn(-1);                // clear
	      header.repaint();
	    }
	  }
    
    /**
     * Recieves and redraws detailed info about selected poker table.
     */
    public void lobbyInfoUpdated(LobbyTableModel ge) {
    	setInfo(ge);
    }
    
    public void lobbyInfoUpdated(LobbyTournyModel te) {
    	setInfo(te);
    }

    public void setInfo(LobbyTableModel ge) {
      //setVisible(false);
      // clear the table
      int rc = activeTableModel.getRowCount();
      //written by rk to list all players in info box for TPoker
      activeTable.setPreferredSize(new Dimension(145, rc*12));
      for (int i = 0; i < rc; i++) {
        try {
          activeTableModel.removeRow(0);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
          //ignore
        }

      }
      
      rc = waitingTableModel.getRowCount();
      for (int i = 0; i < rc; i++) {
        try {
          waitingTableModel.removeRow(0);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
          //ignore
        }
      }

      if (ge == null) {
        return;
      }
      
      if (ge.getName() != null) {
    	  PokerGameType pgt = new PokerGameType(ge.getGameType());
    	  playersListLable.setText("" + (pgt.isTPoker()&&(ge.getName().startsWith("1") || ge.getName().startsWith("1") || 
						ge.getName().startsWith("2") || ge.getName().startsWith("3") ||
					    ge.getName().startsWith("4") || ge.getName().startsWith("5"))?ge.getName().substring(4):ge.getName()));
      }
      String[][] pl = ge.getPlayerDetails();
      if (pl != null) {
        //activeTableModel.addRow(new String[] {"Pos", "Name", "Chips"});
        for (int i = 0; i < pl.length; i++) {
         	if(pl[i][3]!= null && pl[i][1]!= null)
        	activeTableModel.addRow(new String[] {pl[i][3], pl[i][1]});
        	
        }
        
        if(pl.length >= ge.getPlayerCapacity())
          	showAddToWLButton();
          else
          	removeAddToWLButton();
      }
     
      String[] wt = ge.getWaitersDetails();
      if (wt != null) {
        for (int i = 0; i < wt.length; i++) {
          waitingTableModel.addRow(new String[] {"" + i, wt[i]});
        }
        
      }
      ge = null;//by rk, for GC
    }
    
    
    public void setInfo(LobbyTournyModel te) {
        updateTournamentDetails(te);
    }
    
  }

    
  private class ShowTablesFilter extends JPanel
  {
	  protected JPanel p;
	  protected JLabel jlCGFullTables,jlShowText, jlRunning, jlCompleted; 
	    public ShowTablesFilter() {
	      super(new BorderLayout());
	      super.setOpaque(false);
	      
	      p = new JPanel(); 
	      p.setBounds(0, 197, 250, 30);
	      p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
	      p.setFont(new Font("Verdana", Font.PLAIN, 11));
	      p.setOpaque(false);
		  
	            
	      jlCGFullTables = new JLabel("     "+bundle.getString("show")+"      "+bundle.getString("full.tables"),Utils.getIcon(ClientConfig.ICON_ON),SwingConstants.CENTER);
	  	  jlCGFullTables.setFont(new Font("Verdana", Font.PLAIN, 11));
	  	  jlCGFullTables.setHorizontalTextPosition(SwingConstants.CENTER);
	  	  jlCGFullTables.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	  	  jlCGFullTables.setForeground(Color.WHITE);
	  	  jlCGFullTables.addMouseListener(new MouseAdapter() {
	  		
	  		@Override
	  		public void mouseClicked(MouseEvent arg0) {
	  			isCGFullTables = !isCGFullTables;
	  			jlCGFullTables.setIcon(isCGFullTables?Utils.getIcon(ClientConfig.ICON_ON):Utils.getIcon(ClientConfig.ICON_OFF));
	  			refreshTableList = true; // to stop the first row selection
	  			newTableList();
	  		}
	  	  });
	  	
	  	  
	      jlShowText = new JLabel("     "+bundle.getString("show"));
	      setLabelsInvisible(jlShowText);
	      
	      
	      jlRunning = new JLabel(bundle.getString("running")+" ",Utils.getIcon(ClientConfig.ICON_ON),SwingConstants.LEFT);
	      setLabelsInvisible(jlRunning);
	      jlRunning.addMouseListener(new MouseAdapter() {
		  		
		  		@Override
		  		public void mouseClicked(MouseEvent arg0) {
		  			isSNGMTTRunning = !isSNGMTTRunning;
		  			jlRunning.setIcon(isSNGMTTRunning?Utils.getIcon(ClientConfig.ICON_ON):Utils.getIcon(ClientConfig.ICON_OFF));
		  			newTableList();
		  		}
		  	  });
	      
	      jlCompleted = new JLabel(bundle.getString("completed"),Utils.getIcon(ClientConfig.ICON_ON),SwingConstants.LEFT);
	      setLabelsInvisible(jlCompleted);
	      jlCompleted.addMouseListener(new MouseAdapter() {
		  		
		  		@Override
		  		public void mouseClicked(MouseEvent arg0) {
		  			isSNGMTTCmpleted = !isSNGMTTCmpleted;
		  			jlCompleted.setIcon(isSNGMTTCmpleted?Utils.getIcon(ClientConfig.ICON_ON):Utils.getIcon(ClientConfig.ICON_OFF));
		  			newTableList();
		  		}
		  	  });
	      
	      p.add(jlCGFullTables);    
          p.add(jlShowText);
          p.add(jlRunning);
          p.add(jlCompleted);
          add(p);
	      
	  }
      private void change(String type)
	  {
		  if(type.equals("CG"))
	      {
			  jlCGFullTables.setVisible(true);
			  jlRunning.setVisible(false);
			  jlCompleted.setVisible(false);
			  jlShowText.setVisible(false);
	      }
		  else
		  {
			  jlCGFullTables.setVisible(false);
			  jlRunning.setVisible(true);
			  jlCompleted.setVisible(true);
			  jlShowText.setVisible(true);
		  }
	  }
      private void setLabelsInvisible(JLabel lab)
      {
    	  lab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	  lab.setFont(new Font("Verdana", Font.PLAIN, 11));
    	  lab.setForeground(Color.WHITE);
    	  lab.setVisible(false);
      }
  }
  
  private class TabPlayerorWL extends JPanel
  {
	  protected JPanel panel;
	  protected JButton tabPlayers, tabWaitingList; 
	    public TabPlayerorWL() {
	      super(new BorderLayout());
	      super.setOpaque(false);
	      
	      panel = new JPanel();
	      panel.setSize(145, 19);
	      panel.setBorder(null);
	      panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
	        
	        tabPlayers = createTabs("<html><center>"+bundle.getString("tab.players")+"</center></html>", 420, 387,
	      		  Utils.getIcon(ClientConfig.IMG_LOBBY_TAB_PLAYERS_SEL_BG));
	        tabPlayers.setFont(new Font("Verdana", Font.PLAIN, 10));
	        tabPlayers.setVerticalTextPosition(AbstractButton.CENTER);
	        tabPlayers.setHorizontalTextPosition(AbstractButton.CENTER);
	        tabPlayers.setBackground(null);
	        tabPlayers.setBounds(433, 387, 62, 19);
	        tabPlayers.setPreferredSize(new Dimension(62, 19));
	        tabPlayers.addMouseListener(new MouseAdapter() {
	    		
	    		@Override
	    		public void mouseClicked(MouseEvent arg0) {
	    			// TODO Auto-generated method stub
	    			tabPlayers.setIcon(Utils.getIcon(ClientConfig.IMG_LOBBY_TAB_PLAYERS_SEL_BG));
	    			tabWaitingList.setIcon(Utils.getIcon(ClientConfig.IMG_LOBBY_TAB_WLIST_BG));
	    			infoTextArea.changeTab("AT");
	      		}
	    	  });
	        tabWaitingList = createTabs("<html><center>"+bundle.getString("tab.waiting.list")+"</center></html>", 433, 387,
	        		  Utils.getIcon(ClientConfig.IMG_LOBBY_TAB_WLIST_BG));
	        tabWaitingList.setFont(new Font("Verdana", Font.PLAIN, 10));
	        tabWaitingList.setVerticalTextPosition(AbstractButton.CENTER);
	        tabWaitingList.setHorizontalTextPosition(AbstractButton.CENTER);
	        tabWaitingList.setBackground(null);
	        tabWaitingList.setBounds(433, 387, 83, 19);
	        tabWaitingList.setPreferredSize(new Dimension(83, 19));
	        tabWaitingList.addMouseListener(new MouseAdapter() {
	      		
	      		@Override
	      		public void mouseClicked(MouseEvent arg0) {
	      			// TODO Auto-generated method stub
	      			tabPlayers.setIcon(Utils.getIcon(ClientConfig.IMG_LOBBY_TAB_PLAYERS_BG));
	    			tabWaitingList.setIcon(Utils.getIcon(ClientConfig.IMG_LOBBY_TAB_WLIST_SEL_BG));
	    			infoTextArea.changeTab("WT");
	      		}
	      	  });
	          
	          
	        panel.add(tabPlayers);
	        panel.add(tabWaitingList);
	        panel.setBackground(null);
	       add(panel);
	  }
  }
  
  private class BtnAddWLorTourney extends JPanel
  {
	  protected JPanel panel;
	  protected JButton btnGotoTourny; 
	  public BtnAddWLorTourney() {
	      super(new BorderLayout());
	      super.setOpaque(false);
	      
	      panel = new JPanel(new BorderLayout());
	      panel.setOpaque(false);
	      
	      btnAddToWL = createButton("<html>"+bundle.getString("add.me.to.the")+"<br><center>"+bundle.getString("tab.waiting.list")+"</center></html>", 433, 440,   
	      		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL), 
	      		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL));
	      btnAddToWL.setFont(new Font("Verdana", Font.BOLD, 9));
	      btnAddToWL.setVerticalTextPosition(AbstractButton.CENTER);
	      btnAddToWL.setHorizontalTextPosition(AbstractButton.CENTER);
	      btnAddToWL.setBackground(null);
	      btnAddToWL.setVisible(false);
	      btnAddToWL.setBounds(433, 440, 134, 27);
	      btnAddToWL.setPreferredSize(new Dimension(134, 27));
	      btnAddToWL.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				waitingButtonPressed();
			}
		  	});
	      btnGotoTourny = createButton("<html>"+bundle.getString("goto.tourney")+"<br><center>Lobby</center></html>", 433, 440,   
	      		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL), 
	      		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL));
	      btnGotoTourny.setFont(new Font("Verdana", Font.BOLD, 9));
	      btnGotoTourny.setVerticalTextPosition(AbstractButton.CENTER);
	      btnGotoTourny.setHorizontalTextPosition(AbstractButton.CENTER);
	      btnGotoTourny.setBackground(null);
	      btnGotoTourny.setBounds(433, 440, 134, 27);
	      btnGotoTourny.setPreferredSize(new Dimension(134, 27));
	      btnGotoTourny.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(!selected_table.getName().equals("") && selected_table != null)
					//System.out.println("Selected Table: "+selected_table.getName());
				    doTableRegistration(selected_table);
			}
		  	});
	      panel.add(btnAddToWL, BorderLayout.CENTER);
	      add(panel);
	  }
	  private void change(String type)
	  {
		  if(type.equals("CG"))
	      {
			  panel.remove(btnGotoTourny);
			  panel.add(btnAddToWL, BorderLayout.CENTER);
	      }
		  else
		  {
			  panel.remove(btnAddToWL);
			  panel.add(btnGotoTourny, BorderLayout.CENTER);
		  }
	  }
	  
  }
  
  
  private class InfoGameTypes extends JPanel
  {
	  public JPanel mainPane;
	  protected JTable gameTypeTable;
	  protected JLabel gameTypeLable;
	  protected JButton btnViewTable,btnRegisterNow; 
	  protected DefaultTableModel gameTypesTableModel;
	  protected JLabel gameTableFooterBg; 
      
	  Object[][] CG_Data = {
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_ANTE).getImage()),"= "+bundle.getString("ante")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_CAP).getImage()),"= "+bundle.getString("cap")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_DEEPSTACK).getImage()),"= "+bundle.getString("deepstack")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_EDUCATION).getImage()),"= "+bundle.getString("education")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_SPEED).getImage()),"= "+bundle.getString("speed")},
				{null,""},
				{null,""},
	        	//{"",new JLabel("nte", Utils.getIcon(ClientConfig.ICON_ANTE),JLabel.LEFT)},
	        	};
	  Object[][] SNG_MTT_Data = {
			    {new ImageIcon(Utils.getIcon(ClientConfig.ICON_ANTE).getImage()),"= "+bundle.getString("addon.rebuy")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_CAP).getImage()),"= "+bundle.getString("knockout")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_DEEPSTACK).getImage()),"= "+bundle.getString("deepstack")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_EDUCATION).getImage()),"= "+bundle.getString("extra.stack")},
				{new ImageIcon(Utils.getIcon(ClientConfig.ICON_SPEED).getImage()),"= "+bundle.getString("speed")},
	        	{null,""},
        		{null,""},
        		};
	  
	  public InfoGameTypes() 
	  {
	  	super(new BorderLayout());
  		super.setOpaque(false);
  		//the main JPanel where u add all the components
	  
  		mainPane = new JPanel(new BorderLayout());
  		mainPane.setOpaque(false);
	
  		btnViewTable = createButton(bundle.getString("view.table"), 433, 500,   
	  		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL), 
	  		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL));
  		btnViewTable.setFont(new Font("Verdana", Font.BOLD, 9));
		btnViewTable.setAlignmentX(CENTER_ALIGNMENT);
		btnViewTable.setBounds(433, 500, 145, 25);
		btnViewTable.setBackground(null);
		btnViewTable.setPreferredSize(new Dimension(145, 25));
		btnViewTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(!selected_tid.equals("") )
					//System.out.println("Selected Table: "+selected_table.getName());
				doTableRegistration(selected_table);
			}
		  });
		
		btnRegisterNow = createButton(bundle.getString("register.now"), 433, 500,   
		  		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL), 
		  		  Utils.getIcon(ClientConfig.IMG_LOBBY_ADDME_TOWL));
		btnRegisterNow.setFont(new Font("Verdana", Font.BOLD, 9));
		btnRegisterNow.setAlignmentX(CENTER_ALIGNMENT);
		btnRegisterNow.setBounds(433, 500, 145, 25);
		btnRegisterNow.setBackground(null);
		btnRegisterNow.setPreferredSize(new Dimension(145, 25));
		btnRegisterNow.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		  });
		mainPane.add(getGameTypes(), BorderLayout.NORTH);
		mainPane.add(btnViewTable, BorderLayout.SOUTH);
		add(mainPane);
	  }
	  private void changeGameTypes(String type)
	  {
		  
		  int rc = gameTypesTableModel.getRowCount();
	      for (int i = 0; i < rc; i++) {
	        try {
	        	gameTypesTableModel.removeRow(0);
	        }
	        catch (java.lang.ArrayIndexOutOfBoundsException e) {
	          //ignore
	        }
	      }
	      if(type.equals("CG"))
	      {
		      for (int i = 0; i < CG_Data.length; i++) {
		    	  gameTypesTableModel.addRow(CG_Data[i]);
		        }
		      gameTypeLable.setText(bundle.getString("game.types"));
		      mainPane.add(btnViewTable, BorderLayout.SOUTH);
	    	  mainPane.remove(btnRegisterNow);
	      }
	      else
	      {
	    	  for (int i = 0; i < SNG_MTT_Data.length; i++) {
	        	  gameTypesTableModel.addRow(SNG_MTT_Data[i]);
		        }
	    	  gameTypeLable.setText(bundle.getString("tourney.types"));
	    	  mainPane.remove(btnViewTable);
		      mainPane.add(btnRegisterNow, BorderLayout.SOUTH);
	      }
	  }
	  private JPanel getGameTypes() 
	  {
		JPanel gameTypePanel = new JPanel();
		gameTypePanel.setLayout(new BorderLayout(0,-1));
		
		Object[] columnNames = {"",""};
		gameTypesTableModel = new DefaultTableModel(CG_Data, columnNames){
			 
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			 
			// Returning the Class of each column will allow different
			// renderers to be used based on Class
			@Override
			public Class getColumnClass(int column)
			{
				//return getValueAt(0, column).getClass();
				Object value = getValueAt(0, column);
				return value != null ? value.getClass() : String.class;
			}
			 
			};
		
		gameTypeTable = new JTable(gameTypesTableModel){
		public Component prepareRenderer(TableCellRenderer renderer,int row, int col) {
		
			Component comp = super.prepareRenderer(renderer, row, col);
		    //even index, selected or not selected
		    if (row % 2 != 0){// && !isCellSelected(Index_row, Index_col)) {
		      comp.setBackground(new Color(243, 243, 244));
		    } 
		    else {
		      comp.setBackground(new Color(231, 232, 233));
		    }
		    return comp;
		  }
		};
		
		//gameTypeTable.setPreferredSize(new Dimension(145, 82));
		gameTypeTable.setEnabled(true);
		gameTypeTable.setFont(new Font("Verdana", Font.PLAIN, 9));
		gameTypeTable.setRowHeight(12);
		//Cell selection is enabled
		gameTypeTable.setCellSelectionEnabled(false);
		gameTypeTable.setRowSelectionAllowed(false);
		//gameTypeTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
		gameTypeTable.getColumnModel().getColumn(0).setPreferredWidth(0);
		gameTypeTable.getColumnModel().getColumn(1).setPreferredWidth(145);
		//Making sure columns can't be dragged and dropped
		gameTypeTable.getTableHeader().setReorderingAllowed(false);
		gameTypeTable.setShowHorizontalLines(false);
		gameTypeTable.setShowVerticalLines(false);
		gameTypeTable.setShowGrid(false);
		gameTypeTable.setIntercellSpacing(new Dimension(0, 0));

		//gameTypeTable.setIntercellSpacing(new Dimension(5,0));	        		
		gameTypeLable = new JLabel(bundle.getString("game.types"),
				(Icon)Utils.getIcon(ClientConfig.IMG_LOBBY_GAMETYPE_TOP_BG),
				SwingConstants.LEFT);
		//gameTypeLable = new JLabel(Utils.getIcon(ClientConfig.IMG_LOBBY_GAMETYPE_TOP_BG));
		//gameTypeLable.setBounds(430, 400, 145, 19);		
		gameTypeLable.setForeground(new Color(0xFFFFFF));
		gameTypeLable.setVerticalTextPosition(AbstractButton.CENTER);
		gameTypeLable.setHorizontalTextPosition(AbstractButton.CENTER);
		
		gameTableFooterBg = new JLabel(Utils.getIcon(ClientConfig.IMG_LOBBY_GAMETYPE_FOOTER_BG));	        
		//gameTableFooterBg.setBounds(430, 510, 145, 6);		
		gameTableFooterBg.setAlignmentX(CENTER_ALIGNMENT);
		gameTypeLable.setAlignmentX(CENTER_ALIGNMENT);
		gameTypeTable.setAlignmentX(CENTER_ALIGNMENT);
		gameTypePanel.add(gameTypeLable,BorderLayout.NORTH);
		gameTypePanel.add(gameTypeTable,BorderLayout.CENTER);
		gameTypePanel.add(gameTableFooterBg,BorderLayout.SOUTH);
		gameTypePanel.setBackground(null);
		    return gameTypePanel;
		 }
	  
  }
  
  
  private JButton createButton(String str, int x, int y, ImageIcon icon1, ImageIcon icon2) {
	    JButton button = new JButton(str,icon1);
	    int w = icon1.getIconWidth();
	    int h = icon1.getIconHeight();

	    //button.setPressedIcon(icon3);

	    button.setRolloverEnabled(true);
	    button.setRolloverIcon(icon2);
	    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    button.setVerticalTextPosition(AbstractButton.CENTER);
	    button.setHorizontalTextPosition(AbstractButton.CENTER);
	    button.setFocusPainted(false);
	    button.setBorderPainted(false);
	    button.setContentAreaFilled(false);
	    button.setMargin(new Insets(0, 0, 0, 0));
	    button.setBounds(x, y, w, h);
	    button.setForeground(new Color(0xFFFFFF));
	    return button;
	  }
  
  private JButton createTabs(String str, int x, int y, ImageIcon icon1) {
	    JButton button = new JButton(str,icon1);
	    int w = icon1.getIconWidth();
	    int h = icon1.getIconHeight();

	    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    button.setVerticalTextPosition(AbstractButton.CENTER);
	    button.setHorizontalTextPosition(AbstractButton.CENTER);
	    button.setFocusPainted(false);
	    button.setBorderPainted(false);
	    button.setContentAreaFilled(false);
	    button.setMargin(new Insets(0, 0, 0, 0));
	    button.setBounds(x, y, w, h);
	    button.setForeground(new Color(0xFFFFFF));
	    return button;
	  }
  private JRadioButton createRadioButton(String str, int x, int y) 
  {
	    JRadioButton button = new JRadioButton(str);
	    button.setFont(new Font("Verdana", Font.PLAIN, 12));
	    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    button.setFocusPainted(false);
	    button.setBorderPainted(false);
	    button.setContentAreaFilled(false);
	    button.setMargin(null);
	    button.setBounds(x, y, 120, 20);
	    button.setForeground(new Color(0xFFFFFF));
	    return button;
	  }
  
  private JCheckBox createCheckBox(Point p) 
  {
	    JCheckBox cb = new JCheckBox();
	    cb.setFocusPainted(false);
	    cb.setBorderPainted(false);
	    cb.setContentAreaFilled(false);
	    cb.setMargin(null);
	    cb.setBounds(p.x, p.y + 1, 20, 17);
	    cb.addActionListener(this);
		
	    return cb;
  }
  private JLabel createFilterButton(String str, Point p, ImageIcon icon1) 
  {
	    JLabel button = new JLabel(str,icon1,JLabel.CENTER);
	    int w = icon1.getIconWidth();
	    int h = icon1.getIconHeight();
	    button.setFont(new Font("Verdana", Font.PLAIN, 12));
	    button.setVerticalTextPosition(AbstractButton.CENTER);
	    button.setHorizontalTextPosition(AbstractButton.CENTER);
	    button.setBounds(p.x, p.y, w, h);
	    button.setForeground(new Color(0xFFFFFF));
	    return button;
	  }
  private JLabel createLabel(String str, Point p) {
	    JLabel label = new JLabel(str, JLabel.LEFT);
	    label.setFont(new Font("Verdana", Font.PLAIN, 11));
	    //label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    label.setBounds(p.x, p.y, 92, 16);
	    label.setForeground(new Color(0x6B696C));
	    return label;
	  }
     private JLabel createLabelwithBG(String str, int x, int y, Icon icon) {
	    JLabel label = new JLabel(str, icon ,SwingConstants.CENTER);
	    label.setHorizontalTextPosition(AbstractButton.CENTER);
	    label.setVerticalTextPosition(AbstractButton.CENTER);
	    label.setFont(new Font("Verdana", Font.PLAIN, 11));
	    //label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    label.setBounds(x, y, 92, 16);
	    label.setForeground(new Color(0xFFFFFF));
	    return label;
	  }


	
	protected void filterTournyList(LobbyTournyModel[] changes) {
		//System.out.println("changes lengh "+changes.length);
		//new Exception().printStackTrace();
	  LobbyTournyModel[] newMTTList = null;
	  Vector<LobbyTournyModel> tournylist = new Vector<LobbyTournyModel>();
	  //System.out.println("gameType_col1 in filterTournyList" +gameType_col1);
		if (gameType_col1 == GameTypes.TYPE_TOURNAMENT) {
			//if cond. written by rk.
			if (changes != null && changes.length == 0) {
				Vector<TournyEvent> temp = new Vector<TournyEvent>();
				for (int i = 0; i < _serverProxy._rtyl.getTournyCount(); i++) {
					temp.add(_serverProxy._rtyl.getTourny(i));
				}
				LobbyTournyModel[] tournyList = new LobbyTournyModel[temp
						.size()];
				int m = 0;
				for (Iterator<TournyEvent> l = temp.iterator(); l.hasNext(); m++) {
					TournyEvent te = l.next();
					tournyList[m] = new LobbyTournyModel(te);
				}
				changes = tournyList;
			}
  		  
  		if(changes == null)return;  
  		for (int v = 0; v < changes.length; v++) 
  		{
  			String tourny_type = changes[v].getTournamentType();
	  		//System.out.println("Tourny type: "+changes[v].getName()+"--"+changes[v].getTournamentType());
	  		if(isRBPlay && !tourny_type.contains("Real-"))
			{
				if(gameType_mtt_col3 == GameTypes.TYPE_TOURNAMENT_1_HOLDEM)
				{
					if(tourny_type.contains("Holdem"))tournylist.add(changes[v]);
				}
				else if(gameType_mtt_col3 == GameTypes.TYPE_TOURNAMENT_1_OMAHAHI)
				{
					if(tourny_type.contains("Omaha"))tournylist.add(changes[v]);
				}
			}
	  		if(!isRBPlay && tourny_type.contains("Real-"))
			{
				if(gameType_mtt_col3 == GameTypes.TYPE_TOURNAMENT_1_HOLDEM)
				{
					if(tourny_type.contains("Holdem"))tournylist.add(changes[v]);
				}
				else if(gameType_mtt_col3 == GameTypes.TYPE_TOURNAMENT_1_OMAHAHI)
				{
					if(tourny_type.contains("Omaha"))tournylist.add(changes[v]);
				}
			}
  		}
  		newMTTList = new LobbyTournyModel[tournylist.size()];
  		for (int v = 0; v < tournylist.size(); v++) 
  	    {
  			newMTTList[v] = tournylist.elementAt(v);
  	    }
  		if(jTable != null && jTable.getRowCount() > 0)
	    {
		    //for first row selection while getting the new list
	    	if(!refreshTableList && newMTTList.length > 0)
	    	{
	    		selected_tid = newMTTList[0].getName();
	    		selected_table = newMTTList[0];
	    		selected_row = 0;
	    	}
	    }
  		newMTTList = getMTTLimitedList(newMTTList);
		tournamentTableListUpdated(newMTTList);
	  }  
	}
	protected void filterTableList(LobbyTableModel[] changes) {
      LobbyTableModel[] newList = null, tempList = null;
	  LobbyTableModel[] newSNGList = null;
	  Vector<GameEvent> list = new Vector<GameEvent>();
	  Vector<GameEvent> sitngolist = new Vector<GameEvent>();
	  Vector<GameEvent> tplist = new Vector<GameEvent>();
	  
	  for (int v = 0; changes != null && v < changes.length; v++) 
	  {
		GameEvent ge = _serverProxy._rtl.getGameEvent(v);
		PokerGameType pgt = new PokerGameType(ge.getType());
		//if(pgt.isTPoker())System.out.println("TPOKER "+ge.getGameName());
		  
		if(gameType_col1 == GameTypes.TYPE_SITNGO && pgt.isSitnGo())//----for SitnGo----
		{
			if(isRBPlay && pgt.isPlay())
			{
				if(gameType_sng_col3 == GameTypes.TYPE_SITNGO_1_HOLDEM)
				{
					if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_HEADSUP)
					{
						if(pgt.isHoldem() && ge.getMaxPlayers() == 2)sitngolist.add(ge);
					}
					else if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_6HANDED)
					{
						if(pgt.isHoldem() && ge.getMaxPlayers() == 6)sitngolist.add(ge);
					}
					else if (pgt.isHoldem())sitngolist.add(ge);
				}
				else if(gameType_sng_col3 == GameTypes.TYPE_SITNGO_1_OMAHAHI)
				{
					if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_HEADSUP)
					{
						if (pgt.isOmahaHi() && ge.getMaxPlayers() == 2)sitngolist.add(ge);
					}
					else if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_6HANDED)
					{
						if(pgt.isHoldem() && ge.getMaxPlayers() == 6)sitngolist.add(ge);
					}
					else if (pgt.isOmahaHi())sitngolist.add(ge);
				}
			}
			else if(!isRBPlay && pgt.isReal())
			{
				if(gameType_sng_col3 == GameTypes.TYPE_SITNGO_1_HOLDEM)
				{
					if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_HEADSUP)
					{
						if(pgt.isHoldem() && ge.getMaxPlayers() == 2)sitngolist.add(ge);
					}
					else if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_6HANDED)
					{
						if(pgt.isHoldem() && ge.getMaxPlayers() == 6)sitngolist.add(ge);
					}
					else if (pgt.isHoldem())sitngolist.add(ge);
				}
				else if(gameType_sng_col3 == GameTypes.TYPE_SITNGO_1_OMAHAHI)
				{
					if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_HEADSUP)
					{
						if (pgt.isOmahaHi() && ge.getMaxPlayers() == 2)sitngolist.add(ge);
					}
					else if(gameType_sng_col2 == GameTypes.TYPE_SITNGO_0_6HANDED)
					{
						if(pgt.isHoldem() && ge.getMaxPlayers() == 6)sitngolist.add(ge);
					}
					else if (pgt.isOmahaHi())sitngolist.add(ge);
				}
			}
		}
		else if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER && pgt.isTPoker())// for terminal poker
		{
			if(isRBPlay && pgt.isPlay())
			{	
	    		if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
				{
					if (pgt.isHoldem()){
						tplist.add(ge);
					}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI)
				{
					if (pgt.isOmahaHi()){
						tplist.add(ge);
			  		}
				}
			    else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
				{
					if (pgt.isOmahaHiLo()){
						tplist.add(ge);
			  		}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUD)
				{
					if (pgt.isStudHi()){
						tplist.add(ge);
			  		}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
				{
					if(pgt.isStudHiLo() )tplist.add(ge);
			  	}
			}
			else if(!isRBPlay && pgt.isReal())
		    {
				if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
				{
					if (pgt.isHoldem()){
						tplist.add(ge);
			  		}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI)
				{
					if (pgt.isOmahaHi() ){
						tplist.add(ge);
			  		}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
				{
					if (pgt.isOmahaHiLo()){
						tplist.add(ge);
			  		}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUD)
				{
					if (pgt.isStudHi()){
						tplist.add(ge);
			  		}
				}
				else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
				{
					if(pgt.isStudHiLo())tplist.add(ge);
			  	}
		    }
			
			
		}
		else if(gameType_col1 == GameTypes.TYPE_RINGGAME &&  !pgt.isTourny() &&  !pgt.isTPoker())// for normal tables
		{
			if(isRBPlay && pgt.isPlay())
			{	
				if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
				{
					if (pgt.isHoldem()){
						list.add(ge);
					}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI)
				{
					if (pgt.isOmahaHi()){
						list.add(ge);
			  		}
				}
			    else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
				{
					if (pgt.isOmahaHiLo()){
						list.add(ge);
			  		}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUD)
				{
					if (pgt.isStudHi()){
						list.add(ge);
			  		}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
				{
					if(pgt.isStudHiLo() )list.add(ge);
			  	}
			}
			else if(!isRBPlay && pgt.isReal())
		    {
				if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
				{
					if (pgt.isHoldem()){
						list.add(ge);
			  		}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI)
				{
					if (pgt.isOmahaHi() ){
						list.add(ge);
			  		}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
				{
					if (pgt.isOmahaHiLo()){
						list.add(ge);
			  		}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUD)
				{
					if (pgt.isStudHi()){
						list.add(ge);
			  		}
				}
				else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
				{
					if(pgt.isStudHiLo())list.add(ge);
			  	}
		    }
	    	
	    	
		}
	  }//END for loop
        GameEvent ge;
        if(gameType_col1 == GameTypes.TYPE_SITNGO)
  	  	{
  		    _sitngoList = new LobbySitnGoModel[sitngolist.size()];
    		  int t = 0;
            for (Iterator<GameEvent> l=sitngolist.iterator(); l.hasNext();) {
              ge = l.next();
              _sitngoList[t++] = new LobbySitnGoModel(ge);
            }
            newSNGList = getSNGLimitedList(_sitngoList);
            sitnGoTableListUpdated(newSNGList);
        //    selected_tid = newSNGList[0].getName();
        }
        else if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER)
		{
			//Vector<GameEvent> tpfilterlist = new Vector<GameEvent>();
			//tpfilterlist = getPlayerDetFromPool(tplist);
        	//and tpfilterlist is replaced by tplist
			if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
			{
				hpltm = new LobbyHoldemModel[tplist.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();m++) {
			        ge = (GameEvent)l.next();
			        hpltm[m] = new LobbyHoldemModel(ge);
			        //System.out.println("Table: "+hpltm[m].getName()+"--"+hpltm[m].getStack()+"--"+hpltm[m].getHandsPerHour()+"--"+hpltm[m].getAveragePot());
			      }
			      newList = getTPLimitedList(hpltm);
			      hpltm = null;//by rk
			}
			else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI)
			{
				opltm = new LobbyOmahaModel[tplist.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        opltm[m] = new LobbyOmahaModel(ge);
			        //System.out.println("Table: "+opltm[m].getName()+"--"+opltm[m].getStack());
				  }
			      newList = getTPLimitedList(opltm);
			      opltm = null;//by rk
				  
			}
			else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
			{
				ohiltm = new LobbyOmahaModel[tplist.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        ohiltm[m] = new LobbyOmahaModel(ge);
			      }
			      newList = getTPLimitedList(ohiltm);
			      ohiltm = null;//by rk
			}
			else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUD)
			{
				spltm = new LobbyStudModel[tplist.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        spltm[m] = new LobbyStudModel(ge);
			        //System.out.println("Stud: "+m);
			      }
			      newList = getTPLimitedList(spltm);
			      spltm = null;//by rk
			}
			else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
			{
				//System.out.println("Lobbymixed model size: "+list.size());
				shiltm = new LobbyStudModel[tplist.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=tplist.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        shiltm[m] = new LobbyStudModel(ge);
			        //System.out.println("mixed: "+m);
			      }
			      newList = getTPLimitedList(shiltm);
			      shiltm = null;
			}
			tplist.removeAllElements();
			tplist = null;//by rk
		}
		else if(gameType_col1 == GameTypes.TYPE_RINGGAME)
		{
			if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
			{
			  hpltm = new LobbyHoldemModel[list.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=list.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        hpltm[m] = new LobbyHoldemModel(ge);
			        //System.out.println("Table: "+hpltm[m].getName()+"--"+hpltm[m].getStack()+"--"+hpltm[m].getHandsPerHour()+"--"+hpltm[m].getAveragePot());
			      }
			      newList = getCGLimitedList(hpltm);
			      hpltm = null;//by rk
			      
			}
			else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI)
			{
				opltm = new LobbyOmahaModel[list.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=list.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        opltm[m] = new LobbyOmahaModel(ge);
			        //System.out.println("Table: "+opltm[m].getName()+"--"+opltm[m].getStack());
				  }
			      newList = getCGLimitedList(opltm);
			      opltm = null;//by rk
				  
			}
			else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
			{
				ohiltm = new LobbyOmahaModel[list.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=list.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        ohiltm[m] = new LobbyOmahaModel(ge);
			      }
			      newList = getCGLimitedList(ohiltm);
			      ohiltm = null;//by rk
				  
			}
			else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUD)
			{
				spltm = new LobbyStudModel[list.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=list.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        spltm[m] = new LobbyStudModel(ge);
			        //System.out.println("Stud: "+m);
			      }
			      newList = getCGLimitedList(spltm);
			      spltm = null;//by rk
			}
			else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
			{
				//System.out.println("Lobbymixed model size: "+list.size());
				shiltm = new LobbyStudModel[list.size()];
				 int m=0;
			      for (Iterator<GameEvent> l=list.iterator(); l.hasNext();m++) {
			        ge = l.next();
			        shiltm[m] = new LobbyStudModel(ge);
			        //System.out.println("mixed: "+m);
			      }
			      newList = getCGLimitedList(shiltm);
			      shiltm = null;//by rk
			}
		}
	  
	  
	//--- show full tables Filtering code
	  if(newList == null)return;
	  LobbyTableModel[] newList1 = new LobbyTableModel[newList.length];
	  for (int v = 0; v < newList.length; v++) 
	  {
		 if(newList[v].getPlayerCount() == newList[v].getPlayerCapacity())
		 {
			  if(isCGFullTables)
			  newList1[v] = newList[v];
		 }
		 else
			 newList1[v] = newList[v];
		 newList[v] = null;//by rk
	  }
	  newList = null;//by rk
	  
	  if(jTable != null && jTable.getRowCount() > 0)
	    {
		    //for first row selection while getting the new list
	    	if(!refreshTableList && newList1.length > 0)
	    	{
	    		selected_tid = newList1[0].getName();
	    		selected_table = newList1[0];
	    		selected_row = 0;
	    	}
	    }
	  if(gameType_col1 == GameTypes.TYPE_TERMINALPOKER)
	  {
          if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
		  {
			 terminalholdemTableListUpdated(newList1);
			//  selected_tid = newList1[0].getName();
		  }
		  else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI || 
				  gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
		  {
			  terminalomahaTableListUpdated(newList1);
			//  selected_tid = newList1[0].getName();
		  }
		  else if(gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUD ||
				  gameType_tp_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
		  {	
			  terminalstudTableListUpdated(newList1);
		  //	  selected_tid = newList1[0].getName();
		  }
	  }
	  else if(gameType_col1 == GameTypes.TYPE_RINGGAME)
	  {
		  if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_HOLDEM)
		  {
			  holdemTableListUpdated(newList1);
			  selected_tid = newList1[0].getName();
		  }
		  else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHI || 
				  gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_OMAHAHILO)
		  {
			  omahaTableListUpdated(newList1);
			//  selected_tid = newList1[0].getName();
		  }
		  else if(gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUD ||
				  gameType_cg_col2 == GameTypes.TYPE_RINGGAME_0_STUDHILO)
		  {	
			  studTableListUpdated(newList1);
		  //	  selected_tid = newList1[0].getName();
		  }
	  }
	  if(jTable.getRowCount() > 0)
	  {
		  jTable.changeSelection(selected_row, 0, false, false);
		  tableListUpdateBySelectedLabel(newList1);
	  }
	  else
	  {
		  infoTextArea.playersListLable.setText("Play Chips");
		  infoTextArea.setInfo(new LobbyTableModel());
	  }
	  newList1 = null;
	}

	public void terminalholdemTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("LobbyUser Impl holdem tableList update");
	    _latest_changes = changes;
	    /*if(_latest_changes != null && _latest_changes.length > 0){
	    	selected_tid = _latest_changes[selected_row].getName();
	    }*/
	    holdem_model.clear();
	    //jTable.setAutoCreateRowSorter(false);
	    holdem_model.holdemTableListUpdated(changes);
	    //tableListUpdateBySelectedLabel(changes);
	    //jTable.getTableHeader().setAlignmentX(SwingConstants.CENTER);
	    //jTable.setAutoCreateRowSorter(true);
	    tp_model = holdem_model;
        //sorter.setModel(tp_model);
	    //jTable.setSelectionModel((ListSelectionModel) cg_model);
	    //System.out.println("Selected Row: "+selected_row);
		
		
	}
	public void terminalomahaTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("LobbyUser Impl omaha tableList update");
	    _latest_changes = changes;
	    /*if(_latest_changes != null && _latest_changes.length > 0){
	    	selected_tid = _latest_changes[selected_row].getName();
	    }*/
	    omaha_model.clear();
	    omaha_model.omahaTableListUpdated(changes);
	    //tableListUpdateBySelectedLabel(changes);
	    tp_model = omaha_model;
	    //sorter.setModel(tp_model);
	    //jTable.setSelectionModel((ListSelectionModel) cg_model);
	    //System.out.println("Selected Row: "+selected_row);
	    //if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	}
	public void terminalstudTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("LobbyUser Impl stud tableList update");
	    _latest_changes = changes;
	    /*if(_latest_changes != null && _latest_changes.length > 0){
	    	selected_tid = _latest_changes[selected_row].getName();
	    }*/
	    stud_model.clear();
	    stud_model.studTableListUpdated(changes);
	    //tableListUpdateBySelectedLabel(changes);
	    tp_model = stud_model;
	    //sorter.setModel(tp_model);
	    //jTable.setSelectionModel((ListSelectionModel) cg_model);
	    //System.out.println("Selected Row: "+selected_row);
	    //if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	}
	public void holdemTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("LobbyUser Impl holdem tableList update");
	    _latest_changes = changes;
	    /*if(_latest_changes != null && _latest_changes.length > 0){
	    	selected_tid = _latest_changes[selected_row].getName();
	    }*/
	    holdem_model.clear();
	    //jTable.setAutoCreateRowSorter(false);
        holdem_model.holdemTableListUpdated(changes);
	    tableListUpdateBySelectedLabel(changes);
	    //jTable.getTableHeader().setAlignmentX(SwingConstants.CENTER);
	    //jTable.setAutoCreateRowSorter(true);
        cg_model = holdem_model;
	    //sorter.setModel(cg_model);
       // cg_model.setSelModel((ListSelectionModel) cg_model);
        //jTable.setSelectionModel((ListSelectionModel) cg_model);
        //tml = new tableModelListener(sorter);
//        cg_model.addTableModelListener(tml);
//        //jTable = new JTable(sorter);
//        tml.setJTable(jTable);
        //jTable.getModel().addTableModelListener(tml);
	    //System.out.println("Selected Row: "+jTable.getSelectedRow());
		//if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	}

	public void omahaTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("LobbyUser Impl omaha tableList update");
	    _latest_changes = changes;
	    /*if(_latest_changes != null && _latest_changes.length > 0){
	    	selected_tid = _latest_changes[selected_row].getName();
	    }*/
	    omaha_model.clear();
	    omaha_model.omahaTableListUpdated(changes);
	    //tableListUpdateBySelectedLabel(changes);
	    cg_model = omaha_model;
//	    sorter.setModel(cg_model);
	    //jTable.setSelectionModel((ListSelectionModel) cg_model);
	    //System.out.println("Selected Row: "+selected_row);
	    //if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	}

	public void studTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("LobbyUser Impl stud tableList update");
	    _latest_changes = changes;
	    /*if(_latest_changes != null && _latest_changes.length > 0){
	    	selected_tid = _latest_changes[selected_row].getName();
	    }*/
	    stud_model.clear();
	    stud_model.studTableListUpdated(changes);
	    //tableListUpdateBySelectedLabel(changes);
	    cg_model = stud_model;
	    //sorter.setModel(cg_model);
	    //jTable.setSelectionModel((ListSelectionModel) cg_model);
	    //System.out.println("Selected Row: "+selected_row);
	    //if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	}

	public void sitnGoTableListUpdated(LobbyTableModel[] changes) {
		//_cat.fine("sitnGoTableListUpdated "+changes.length);
	  	_sitngoList = (LobbySitnGoModel[]) changes;
	  	/*if(changes != null && changes.length > 0){
	    	try {
				selected_tid = changes[selected_row].getName();
			} catch (Exception e) {
				selected_row = 0;
				selected_tid = changes[0].getName();
			}
	    }*/
	    //_latest_changes = changes;
	    sitngo_model.clear();
	    sitngo_model.sitnGoTableListUpdated(changes);
	    //tableListUpdateBySelectedLabel(changes);
	    //sorter.setModel(sitngo_model);
	    //jTable.setSelectionModel((ListSelectionModel) sitngo_model);
	    //System.out.println("Selected Row: "+selected_row);
	    //if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	
	}
	
	public void tournamentTableListUpdated(LobbyTournyModel[] changes) {
		//new Exception("elem "+_tournyList.length).printStackTrace();
		_tournyList = changes;
		/*if(changes != null && changes.length > 0){
	    	selected_tid = changes[selected_row].getName();
	    }*/
	    tournament_model.clear();
	  	tournament_model.tournamentListUpdated(changes);
		//tableListUpdateBySelectedLabel(changes);
		//sorter.setModel(tournament_model);
	    //jTable.setSelectionModel((ListSelectionModel) sitngo_model);
	    //System.out.println("Selected Row: "+selected_row);
	    //if(jTable.getRowCount() > 0)jTable.changeSelection(selected_row, 0, false, false);
		
	
	}
	
	public void tableListUpdated(LobbyTableModel[] changes) {
		_tableList = changes;
		if(changes != null && changes.length > 0 && gameType_col1 != GameTypes.TYPE_TOURNAMENT)
		{
		  refreshTableList = true;
		  _tableList = changes;// loading the total table list to global variable
		  filterTableList(changes);
	      refreshTableList = false;
		}
	  
	}

	public void tournyListUpdated(LobbyTournyModel[] changes) {
		_tournyList = changes;
		if(gameType_col1 == GameTypes.TYPE_TOURNAMENT)
		{
			refreshTableList = true;
			_tournyList = changes;// loading the total table list to global variable
			filterTournyList(changes);
			refreshTableList = false;
		}
	}

	@Override
	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return frame;
	}

	@Override
	public void pingDetailsReceived(long time, int at, int ap) {
		//reset the disconnect count variable 
	  _serverProxy._disconnect_count = 0;
	  if(_serverProxy.bl != null){
		  _serverProxy.bl.closeBox();
		  _serverProxy.bl = null;
	  }
	  Date d = new Date(time);
	  SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	  jlEasternTime.setText("ET "+format.format(d));
	  jlActiveTables.setText(SharedConstants.activeTablesString(at));
	  jlPlayersOnline.setText(SharedConstants.activePlayersString(ap));
	  jlRealWorth.setText("<html>"+SharedConstants.moneyToString$Right(_serverProxy.realWorth())+"</html>");
	  jlPlayWorth.setText(SharedConstants.doubleToString(_serverProxy.playWorth()));
	  jlMoneyInPlay.setText("<html>"+SharedConstants.moneyToString$Right(_serverProxy.moneyInPlay() )+"</html>");
//	  _cat.severe("rw= "+SharedConstants.moneyToString$Right(_serverProxy.realWorth())+",pw= "+
//			  									SharedConstants.doubleToString(_serverProxy.playWorth())+",on table= "+
//			  									SharedConstants.moneyToString$Right(_serverProxy.moneyInPlay() )+" PING RECEVED");
	}
	
	@Override
	public void broadcastMessageReceived(String msg) {
		// TODO Auto-generated method stub
		System.out.println("broadcastMessage Rcesieved "+msg);
		if (this.getFrame().getState() == Frame.ICONIFIED) {
			this.getFrame().setState(Frame.NORMAL);
          }
		this.getFrame().toFront();
		JOptionPane.showInternalMessageDialog(this.getFrame().getContentPane(), msg);
	}

	public static Vector<GameEvent> getPlayerDetFromPool(Vector<GameEvent> tp_list)
	{
		Vector<GameEvent> pool_list = new Vector<GameEvent>();
		ConcurrentHashMap<String, GameEvent> ht = new ConcurrentHashMap<String, GameEvent>();
		for (Iterator<GameEvent> l=tp_list.iterator(); l.hasNext();) {
			GameEvent curr_ge = (GameEvent) l.next();
			//System.out.println(curr_ge.toString());
			String pool_name = curr_ge.getGameName().substring(4);
			GameEvent ge = ht.get(pool_name);
			if(ge == null)
			{
				ht.put(pool_name, curr_ge);
			}
			String curr_player_details = curr_ge.getPlayerDetailsString();
			if(!curr_player_details.equals("none") && ge != null)
			{
				// add curr_player_details to the existing ge
				String plr_dets = ge.getPlayerDetailsString();
				ge.setPlayerDetails((!plr_dets.contains("none")?plr_dets+ "`" :"")+ curr_player_details);
			}
			//System.out.println("--> "+curr_player_details);
		}
		
	       
        for( Enumeration<GameEvent> e = ht.elements(); e. hasMoreElements(); ){
        	GameEvent nge = e.nextElement();
            //System.out.println(nge.toString());
            pool_list.add(nge);
        }
		
		return pool_list;
	}
	
	public class SortByName implements Comparator<GameEvent>{

	    public int compare(GameEvent ge1, GameEvent ge2) {
	        return ge1.getGameName().substring(3).compareTo(ge2.getGameName().substring(3));
	    }
	}
	public class SortByPlayerCount implements Comparator<LobbyTableModel>
	{
		public int compare(LobbyTableModel arg0, LobbyTableModel arg1) {
			return arg1.getPlayerCount()-arg0.getPlayerCount(); 
		}
	}
	
	/*public LobbyTableModel getTableFromPool(String s)
	{
		//new Exception("getTableFromPool").printStackTrace();
		System.out.println("s= "+s);
		LobbyTableModel ltm = null;
		Vector<LobbyTableModel> v = new Vector<LobbyTableModel>();
		int player_cnt = 0;
		for (int i = 0; i < _tableList.length; i++) 
		{
			if(_tableList[i].getName().contains(s.substring(3)))
			{
				v.add(_tableList[i]);
				player_cnt += _tableList[i].getPlayerCount();
				
			}
		}
		Collections.sort(v, new SortByPlayerCount());
		System.out.println("player count in pool "+player_cnt);
		for (int i = 0; i < v.size(); i++) 
		{ 
			System.out.println(v.get(i).getName()+"----"+v.get(i).getPlayerCount());
//			if(player_cnt > v.get(i).getPlayerCapacity()?v.get(i).getPlayerCount() ==0: v.get(i).getPlayerCount() < v.get(i).getPlayerCapacity()
//					&& !v.get(i).isGameStatus() && !v.get(i).isBench())
//			{
//				ltm = v.get(i);
//				System.out.println(v.get(i).getName()+"----"+v.get(i).getPlayerCount());
//				break;
//			}
			if(!v.get(i).isGameStatus() && !v.get(i).isBench()){
				if(v.get(i).getPlayerCount() < v.get(i).getPlayerCapacity())
				{
					ltm = v.get(i);
					System.out.println("\n"+v.get(i).getName()+"----"+v.get(i).getPlayerCount());
					break;
				}
			}
					
		}
		return ltm;
	}*/
	
	//by rk, for implementing JoinPoolCommand
	private boolean newMoneyRequest(String gameName, JFrame _ow, double worth, 
			double moneyMin,  double moneyMax,
            boolean limitGame, String choice
            ) {
		//new Exception("new money request").printStackTrace();
		_cat.fine("Pool buyin......."+gameName+", worth= "+worth+", moneyMin= "+moneyMin+", moneyMax= "+moneyMax+", limitGame= "+limitGame);
		if (MessageFactory.dialog != null) {
			System.out.println("");
		MessageFactory.dialog.toFront();
		return false;
		}
		/** @todo replace the buyin chips condition with 10 times max_bet..etc */
		else  {
//		if ( (amtAtTable < moneyMin)) {
		StringBuilder sb = new StringBuilder().append("<html>").append(bundle.getString("how.much.money"));
		sb.append(" ").append(com.agneya.util.SharedConstants.chipToMoneyString(worth)).append(" ");
		sb.append(bundle.getString("you.want.use")).append("?<br>");
		sb.append(bundle.getString("min.buyin"));
		sb.append(" ").append(com.agneya.util.SharedConstants.chipToMoneyString(moneyMin));
		if (moneyMax > 0 && !limitGame) {
			sb.append("<br>").append(bundle.getString("max.buyin")).append(" ").
			append(com.agneya.util.SharedConstants.chipToMoneyString(moneyMax));
		}else {
			sb.append("<br>").append(bundle.getString("max.buyin")).append(" ").
			append(com.agneya.util.SharedConstants.chipToMoneyString(worth));
		}
		sb.append("</html>");
		JFrame f = (JFrame)_ow;
		
		choice = (String)JOptionPane.showInternalInputDialog(f.getContentPane(), sb.toString(),
		bundle.getString("money.request"),1,null,null,
		com.agneya.util.SharedConstants.decimal2digits((moneyMin > 0 && worth > moneyMin * 3) ? moneyMin * 3 :worth));
//		}
		}
		//_cat.severe("Choice is ... "+choice);
		double value = 0;
		if (choice == null) {
		_cat.severe("Choice is null ... ");
		return false;
		}
		try {
		value = (Double.parseDouble(choice));
		_cat.fine("value = " + value);
		if (value < moneyMin) {
		_cat.severe("<html>" + bundle.getString("too.little") +  "</html>");
		JOptionPane.showInternalMessageDialog(_ow.getContentPane(), bundle.getString("too.little"));
				
		//setInfoLabelText("<html>" + bundle.getString("too.little") + "</html>");
		return false;
		}
		if (value > worth) {
		_cat.severe("<html>" +   bundle.getString("you.cant.afford")
		+ ": "   + SharedConstants.chipToMoneyString(worth) +   "</html>");
		JOptionPane.showInternalMessageDialog(_ow.getContentPane(), bundle.getString("you.cant.afford"));
		
//		setInfoLabelText("<html>" + bundle.getString("you.cant.afford")
//		   + ": "   + SharedConstants.chipToMoneyString(worth) +    "</html>");
		return false;
		}
		if ( (moneyMax ) > /*!*/ 0 && value > (moneyMax)) {
		_cat.severe(
		bundle.getString("you.cant.afford.max")
		+ ": " + SharedConstants.chipToMoneyString(moneyMax));
		JOptionPane.showInternalMessageDialog(_ow.getContentPane(), bundle.getString("you.cant.afford.max")+" : "+SharedConstants.chipToMoneyString(moneyMax));
		
//		setInfoLabelText(
//		bundle.getString("you.cant.afford.max")
//		+ ": " + SharedConstants.chipToMoneyString(moneyMax));
		return false;
		}
		try {
		_serverProxy.joinPool(gameName, value); // depedning on the type of game server will buy real
		} catch (Exception e) {
		e.printStackTrace();
		}
		}
		catch (NumberFormatException nfe) {
		//setInfoLabelText(bundle.getString("invalid.number"));
		_cat.log(Level.WARNING, "Invalid number ", nfe);
		JOptionPane.showInternalMessageDialog(_ow.getContentPane(), "Invalid amount");
		return false;
		//cardLayout.show(cardDisplayPanel, CARD_INFO);
		}
//		cardShowInfo();
//		chatPanelSetVisible(true);
		return true;
		}

// END CLASS	
}
