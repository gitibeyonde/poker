package com.onlinepoker;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.agneya.util.SharedConstants;
import com.golconda.game.PlayerStatus;
import com.golconda.game.util.ActionConstants;
import com.onlinepoker.actions.Action;
import com.onlinepoker.actions.BetRequestAction;
import com.onlinepoker.actions.BettingAction;
import com.onlinepoker.actions.ChatAction;
import com.onlinepoker.actions.InfoAction;
import com.onlinepoker.lobby.LobbyFrame;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.onlinepoker.util.MessageFactory;
import com.onlinepoker.util.MyJButton;
import com.onlinepoker.util.MyJCheckBox;
import com.onlinepoker.util.MyJToggleButton;
import com.onlinepoker.util.MyStatsDialog;
import com.onlinepoker.util.Statistics;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;


public class BottomPanel
    extends JPanel
    implements ActionListener, ItemListener, KeyListener {
  static Logger _cat = Logger.getLogger(BottomPanel.class.getName());
  private RoomSkin skin;
  int WAIT_INTERVAL = 10625; //for exact division of 17 frames, each frame move at rate 625ms //12000; //17000
  //--------------------------
  //  ...... /\/ ......
  //--------------------------
  private ImageIcon m_image = null;
  private int winc = -1;
  private int hinc = -1;

  //--------------------------
  protected ResourceBundle bundle;
  private Timer timer, timer2, timer3;

  //private boolean wantLeave  = false;
  //private boolean inPlay = false;
  //private JPanel chatPanel;
  private JPanel cardDisplayPanel;
  private JPanel intPanel;
  public JPanel centerPanel;
  protected JPanel chatFieldPanel=null;
  		JPanel checks;
  		JPanel panel;
  public JPanel sliderPanel;
  
  //private CardLayout cardLayout;
  public ServerProxy _serverProxy;
  protected ClientPokerController clientPokerController;

  public static final String CARD_BUTTON = "CARD_BUTTON";
  public static final String CARD_INPUTDIALOG = "CARD_INPUTDIALOG";
  public static final String CARD_INFO = "CARD_INFO";
  private int flag = -1;
  private static final int CARD_BTN = 0;
  private static final int CARD_INF = 1;
  
  private boolean showBetPanel = false;
  private boolean enableBetSlider = false;

  private static String getInfoLabel(String value) {
	  StringBuilder sb = new StringBuilder();
	  return sb.append(value).append(": ").toString();
    //return value + ": ";
  }

  protected MyJButton[] buttons = new MyJButton[4];
  protected MyJButton quickFold = null;
  protected ImageIcon btn_allin = Utils.getIcon("images/btn_allin.png");
  protected ImageIcon btn_raise = Utils.getIcon("images/btn_raise.png");
  protected ImageIcon btn_call = Utils.getIcon("images/btn_call.png");
  protected ImageIcon btn_fold = Utils.getIcon("images/btn_fold.png");
  protected ImageIcon btn_quickfold = Utils.getIcon("images/btn_quickfold.png");
  
  protected JButton speakButton = null;

  protected JCheckBox muckCardsCB = null;
  protected JCheckBox autoPostBlindCB = null;
  protected JCheckBox foldToAnyBetCB = null;
  protected JCheckBox sitOutNextHandCB = null;
  //protected JLabel labelAutoPostBlind, labelFoldToAny, labelSitOutNext;
  protected JToggleButton sitInOut;
  
  
  protected JToggleButton leftButtonSit = null;
  protected JToggleButton leftButtonLeave = null;
  protected JButton leftButtonCashier = null;
  
  protected JToggleButton rightButtonText = null;
  protected JToggleButton rightButtonChat = null;
  protected JToggleButton rightButtonBoth = null;
  protected JToggleButton muteButton = null;

  private DefaultListModel sampleModel;
  protected JTextField chatField = null; //new JTextField(20);
  protected JList textArea = null; //new JTextArea(5, 20);
  protected JLabel allInsCount = createLabel("all.in.remain");
  protected JLabel bet = createInfoLabel("bet");
  protected JLabel pot = createInfoLabel("pot");
  protected JLabel rake = createInfoLabel("rake");
  protected JLabel infoLabel = new JLabel("");//createLabel("sit.your.player");
  protected JLabel errorLabel = null;
  protected JCheckBox checkScrollList = null;
  protected JCheckBox checkCheckFold = null;
  protected JCheckBox checkFold = null;
  protected JCheckBox checkCheckCall = null;
  protected JCheckBox checkCheckCallAny = null;
  protected JCheckBox checkRaise = null;
  protected JCheckBox checkRaiseAny = null;
  protected Vector logVector = new Vector();
  protected JSlider slider;

  protected JLabel sliderMin, sliderX2, sliderX3, sliderX4, sliderX5, sliderPot, sliderMax,
  					labelEuro, limitAmt;
  protected JLabel slider_bg, sliderfield_bg, sliderpanel_bg, arrow_left, arrow_right;
  
  MouseListener x2,x3,x4,x5,xMax ;
  ChangeListener changeListener;
  ActionListener actionListener;
  //protected JLabel sliderLabel = new JLabel("€");
  protected JFormattedTextField sliderField = new JFormattedTextField("---");
  protected JScrollPane chatTextPane = null;
  protected boolean defaultCheckFold = false,
      defaultFold = false,
      defaultCheckCall = false,
      defaultCheckCallAny = false,
      defaultRaise = false,
      defaultRaiseAny = false,
      isShowChecks = false;
  protected double defaultCallValue, defaultRaiseValue;
  protected double[] amount, amount_limit;
  protected BetRequestAction _brq;
  protected long[] guid = null;
  protected boolean doSitin = false;
  protected boolean doFoldToAny = false;
  private String limitType;
  double minVal = 0.0;
  double maxVal = 0.0;
  double potVal = 0.0;
  double minBet = 0.0;//minBet means BigBling value
//for stats
  double betVal = 0.0;
  //boolean disableAutoScroll = false;
  boolean _rebuy_chips = false;
  
  protected double ratioX = 1.0;
  protected double ratioY = 1.0;
  protected int roomWidth = 0;
  protected int roomHeight = 0;
  protected int sliderPanelWidth = 278;
  protected int sliderPanelHeight = 46;
  protected int startx = 5, 
	w1 = 80, 
	w2 = 495,//495 = 400 + 90 + 35 - 10 - 20, 
	w3 = 245;//245 = 225 + 20;
  int starty = 10, 
	he = 120;
  
  int entCount = 0;//this variable used when enter amt in sliderField
  
  
  public BottomPanel(RoomSkin skin) {
    try {
      this.skin = skin;
      this.ratioX = skin._ratio_x;
      this.ratioY = skin._ratio_y;
      this.roomWidth = (int)skin.roomWidth;
      this.roomHeight = (int)skin.roomHeight;
      bundle = Bundle.getBundle();
      m_image = skin.getBottonPanelBackground();
      winc = m_image.getIconWidth();
      hinc = m_image.getIconHeight();
      panelInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void panelInit() throws Exception {
    _serverProxy = ServerProxy.getInstance();
    for(int i =0;i<4;i++){
    	buttons[i] = createMoveButton("");
    }
    createButtons();
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setVisible(false);
      buttons[i].addActionListener(this);
      buttons[i].addKeyListener(this);
    }

    KeyboardFocusManager keyboardFocusManager =
        KeyboardFocusManager.getCurrentKeyboardFocusManager();

    leftButtonSit.setEnabled(false);
    leftButtonSit.setVisible(false);
    leftButtonSit.addActionListener(this);
    leftButtonSit.setBounds(10, 0, 100, 20);
    leftButtonLeave.addActionListener(this);
    leftButtonLeave.setBounds(10, 30, 100, 20);
    leftButtonCashier.setEnabled(true);
    leftButtonCashier.addActionListener(this);
    leftButtonCashier.setBounds(10, 60, 100, 20);
    leftButtonCashier.addActionListener(this);
    
 // --- leftButtonPanel
    /*JPanel leftButtonPanel = new JPanel();
    leftButtonPanel.setOpaque(false);
    leftButtonPanel.setBounds(skin.getLeftButtonPanel());//startx+ 665, starty + 70, w1, he
    leftButtonPanel.add(leftButtonSit);
    leftButtonPanel.add(leftButtonLeave);
    leftButtonPanel.add(leftButtonCashier);
    */

    checkScrollList.getModel().setSelected(true);
    rightButtonBoth.getModel().setSelected(true);
    rightButtonText.addActionListener(this);
    rightButtonChat.addActionListener(this);
    rightButtonBoth.addActionListener(this);
    rightButtonText.setLocation(0, 0);
    rightButtonChat.setLocation(0, 25);
    rightButtonBoth.setLocation(0, 50);
    checkScrollList.setBounds(0, 70, 70, 20);
    
//  --- logsChatPanel
    JPanel logsChatPanel = new JPanel(null); /*new GridLayout(3, 1, 0, 0));*/
    logsChatPanel.setOpaque(false);
    logsChatPanel.setBounds(new Rectangle((int)((400/*+20-20*/)*ratioX),(int)(10*ratioY),(int)(65*ratioX),(int)(90*ratioY)));
    logsChatPanel.add(rightButtonText);
    logsChatPanel.add(rightButtonChat);
    logsChatPanel.add(rightButtonBoth);
    logsChatPanel.add(checkScrollList);
    

    /** PRE-MOVE CHECK BOXES */
    checkCheckFold.addActionListener(this);
    checkFold.addActionListener(this);
    checkCheckCall.addActionListener(this);
    checkCheckCallAny.addActionListener(this);
    checkRaise.addActionListener(this);
    checkRaiseAny.addActionListener(this);
    this.setLayout(null);
    
    checkCheckFold.setOpaque(false);
    checkFold.setOpaque(false);
    checkCheckCall.setOpaque(false);
    checkCheckCallAny.setOpaque(false);
    checkRaise.setOpaque(false);
    checkRaiseAny.setOpaque(false);
    
    checks = new JPanel(new GridLayout(2, 2));
    checks.setOpaque(false);
    checks.add(checkCheckFold);
    //checks.add(checkCheckCall);
    //checks.add(checkFold);
    checks.add(checkCheckCallAny);
    checks.add(checkRaise);
    checks.add(checkRaiseAny);
    disibleChecks();//to hide checks by default
    
    /** MOVE BUTTONS */
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
    panel.setOpaque(false);
    for (int i = 0; i < buttons.length; i++) {
      panel.add(buttons[i]);
    }
    /** INT PANEL */
    intPanel = new JPanel(new BorderLayout());
    intPanel.setOpaque(false);
    intPanel.setBounds(new Rectangle((int)(0*ratioX),(int)(0*ratioY),(int)((375)*ratioX),(int)(50*ratioY)));//
    //intPanel.setBorder(BorderFactory.createLineBorder((Color.WHITE)));
    intPanel.add(panel);
    
    
    /** BET SLIDER */
    /*sliderMin = new JLabel(bundle.getString("slider.min"),SwingConstants.CENTER);
    sliderMin.addMouseListener(new MouseAdapter() {
    	public void mousePressed(MouseEvent e) {
			double sliderVal = slider.getMinimum();
			sliderField.setText(SharedConstants.doubleToString((double) (sliderVal / 100)));
			slider.setValue((slider.getMinimum() ));
			_cat.fine("sliderField value= "+SharedConstants.doubleToString((double) (sliderVal / 100))
					+"slider value in MIn= "+(slider.getMinimum() ));
			sliderMin.setBackground(new Color(165, 165, 165));//Color.LIGHT_GRAY
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			sliderMin.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
		}
	});
    //sliderMin.setFont(new Font("Verdana", Font.BOLD, 10));
    sliderMin.setOpaque(true);
    sliderMin.setForeground(Color.BLACK);//WHITE
    sliderMin.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
    sliderMin.setBorder(LineBorder.createGrayLineBorder());*/
    
    /*sliderX2 = new JLabel("1/4",SwingConstants.CENTER);
    sliderX2.addMouseListener(x2=new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if(sliderX2.isEnabled()){
				double sliderVal = potVal;
				sliderField.setText(SharedConstants.doubleToString((double) (sliderVal /4)));
				slider.setValue((int)(potVal*100)/4);
				sliderX2.setBackground(new Color(165, 165, 165));//Color.LIGHT_GRAY
			}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if(sliderX2.isEnabled())
			sliderX2.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
		}
	});
    sliderX2.setFont(new Font("Verdana", Font.BOLD, 8));
    sliderX2.setOpaque(true);
    sliderX2.setForeground(Color.BLACK);//WHITE
    sliderX2.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
    sliderX2.setBorder(LineBorder.createGrayLineBorder());*/
    
    sliderX3 = new JLabel("1/2",SwingConstants.CENTER);
    sliderX3.addMouseListener(x3=new MouseAdapter() {
    	public void mousePressed(MouseEvent e) {
			if(sliderX3.isEnabled()){
				if(clientPokerController._model.getRound() != -1){
					double sliderVal = potVal;
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal /2)));
					slider.setValue((int)(potVal*100)/2);
				}else{
					double sliderVal = minBet;
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal*3)));
					slider.setValue((int)(minBet*100*3));
				}
				sliderX3.setBackground(new Color(165, 165, 165));//Color.LIGHT_GRAY
			}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if(sliderX3.isEnabled())
			sliderX3.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
		}
	});
    //sliderX3.setFont(new Font("Verdana", Font.BOLD, 8));
    sliderX3.setOpaque(true);
    sliderX3.setForeground(Color.BLACK);//WHITE
    sliderX3.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
    sliderX3.setBorder(LineBorder.createGrayLineBorder());
    
    sliderX4 = new JLabel("3/4",SwingConstants.CENTER);
    sliderX4.addMouseListener(x4=new MouseAdapter() {
    	public void mousePressed(MouseEvent e) {
			if(sliderX4.isEnabled()){
				if(clientPokerController._model.getRound() != -1){
					double sliderVal = potVal;
					sliderField.setText(SharedConstants.doubleToString((double) 3*(sliderVal /4)));
					slider.setValue((int)(potVal*100*3)/4);
				}else{
					double sliderVal = minBet;
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal*4)));
					slider.setValue((int)(minBet*100*4));
				}
				sliderX4.setBackground(new Color(165, 165, 165));//Color.LIGHT_GRAY
			}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if(sliderX4.isEnabled())
			sliderX4.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
		}
	});
    //sliderX4.setFont(new Font("Verdana", Font.BOLD, 8));
    sliderX4.setOpaque(true);
    sliderX4.setForeground(Color.BLACK);//WHITE
    sliderX4.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
    sliderX4.setBorder(LineBorder.createGrayLineBorder());
    
    sliderX5 = new JLabel(bundle.getString("slider.pot"),SwingConstants.CENTER);
    sliderX5.addMouseListener(x5=new MouseAdapter() {
    	public void mousePressed(MouseEvent e) {
			if(sliderX5.isEnabled()){
				if(clientPokerController._model.getRound() != -1){
					double sliderVal = potVal;
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal)));
					slider.setValue((int)(potVal*100));
				}else{
					double sliderVal = minBet;
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal*5)));
					slider.setValue((int)(minBet*100*5));
				}
				sliderX5.setBackground(new Color(165, 165, 165));//Color.LIGHT_GRAY
			}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			//if((double)slider.getMinimum()/100 <= potVal)
			if(sliderX5.isEnabled())
			sliderX5.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
		}
	});
    //sliderX5.setFont(new Font("Verdana", Font.BOLD, 10));
    sliderX5.setOpaque(true);
    sliderX5.setForeground(Color.BLACK);//WHITE
    sliderX5.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
    sliderX5.setBorder(LineBorder.createGrayLineBorder());
    
    /*sliderHalfPot = new JLabel("<html>&#189; "+bundle.getString("slider.pot")+"</html>");
    sliderHalfPot.setFont(new Font("Verdana", Font.BOLD, 10));
    sliderHalfPot.setForeground(Color.WHITE);
    sliderHalfPot.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			double sliderVal = 0;
			if(limitType.equals("PL"))
			{
				if(slider.getMaximum()/2 < slider.getMinimum())
					sliderVal = (slider.getMaximum() + (slider.getMinimum()/2))/2;
				else sliderVal = slider.getMaximum()/2;
				sliderField.setText( ("" + (double) (sliderVal / 100)));
				slider.setValue((int) (sliderVal ));
			}
		}
	});*/
    
    /*sliderPot = new JLabel(bundle.getString("slider.pot"),SwingConstants.CENTER);
    sliderPot.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			double sliderVal = 0;
			if(limitType.equals("NL"))
			{
				sliderVal = slider.getMaximum();
				sliderField.setText( SharedConstants.doubleToString((double) (sliderVal / 100)));
				slider.setValue((int) (sliderVal ));
			}
			else
			{
				sliderVal = potVal;
				sliderField.setText(SharedConstants.doubleToString((double) (sliderVal)));
				slider.setValue((int)(potVal*100));
			}
			
		}
	});
    sliderPot.setFont(new Font("Verdana", Font.BOLD, 10));
    sliderPot.setForeground(Color.WHITE);*/

    sliderMax = new JLabel(bundle.getString("slider.max"),SwingConstants.CENTER); //
    sliderMax.addMouseListener(xMax =new MouseAdapter() {
    	public void mousePressed(MouseEvent e) {
			if(sliderMax.isEnabled()){
				if(clientPokerController._model.getRound() != -1){
					double sliderVal = slider.getMaximum();
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal / 100)));
					slider.setValue((slider.getMaximum() ));
				}else{
					double sliderVal = minBet;
					sliderField.setText(SharedConstants.doubleToString((double) (sliderVal*6)));
					slider.setValue((int)(minBet*100*6));
				}
				sliderMax.setBackground(new Color(165, 165, 165));//Color.LIGHT_GRAY
			}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if(sliderMax.isEnabled())
			sliderMax.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
		}
	});
    //sliderMax.setFont(new Font("Verdana", Font.BOLD, 10));
    sliderMax.setOpaque(true);
    sliderMax.setForeground(Color.BLACK);//WHITE
    sliderMax.setBackground(new Color(195, 195, 195));//Color.LIGHT_GRAY
    sliderMax.setBorder(LineBorder.createGrayLineBorder());
    
    slider = new JSlider( JSlider.HORIZONTAL, 0, 190+8, 10);
    slider.setPaintTicks(false);
    slider.setPaintLabels(false);
    //slider.setUI(new MySliderUI(slider));
    slider.setOpaque(false);
    //slider.putClientProperty("Slider.background", Color.BLACK);
    slider.addChangeListener(changeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        double sliderVal = slider.getValue();
        //_cat.finest("sliderField.setText(double)(slider.getValue() / 100): " + slider.getValue());
        sliderField.setText(SharedConstants.doubleToString((double) (sliderVal / 100))); // changed label to field
        // also change the amount on the button
        for (int i = 0; i < buttons.length; i++) {
        	String name = buttons[i].getName();
        	if (bundle.getString("bet").equals(name)){
        		StringBuilder sb = new StringBuilder();
        		sb.append("<html>").append(bundle.getString("bet"))
        		.append("<br>€").append(sliderField.getText())
        		.append("</html>");
        		buttons[i].setText(sb.toString());
//        		buttons[i].setText("<html>"+bundle.getString("bet")+ "<br>€"+  sliderField.getText()+"</html>");
        		break;
        	}
        	if (bundle.getString("raise").equals(name)){
        		StringBuilder sb = new StringBuilder();
        		sb.append("<html>").append(bundle.getString("raise"))
        		.append("<br>€").append(sliderField.getText())
        		.append("</html>");
        		buttons[i].setText(sb.toString());
//        		buttons[i].setText("<html>"+bundle.getString("raise")+ "<br>€"+  sliderField.getText()+"</html>");
        		break;
        	}
        	
         }
      }
    });
    slider.addMouseListener(new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			sliderField.requestFocusInWindow();
		}
	});
    // set text filed for slider
    sliderField.addActionListener(actionListener = new ActionListener(){
    	  
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String text = sliderField.getText();
			int minBetCents = (int) slider.getMinimum();
			int maxBetCents = (int) slider.getMaximum();
			//_cat.finest("Slider field = " + text + " minBet=" + minBetCents + " maxbet =" + maxBetCents);
			try {
				double val = Double.parseDouble(text);
				int centval = (int)(val * 100.00);
				if (centval < minBetCents){
					val = minBetCents/100.0;
					sliderField.setText(Double.toString(val));
//					sliderField.setText(""+val);
					return;
				}
				if (centval > maxBetCents){
					val = maxBetCents/100.0;
					sliderField.setText(Double.toString(val));
					//sliderField.setText(""+val);
					return;
				}
				slider.setValue((int)(val * 100.00));
				 
			}
			catch(NumberFormatException e){
				sliderField.setText(Double.toString(minBetCents));
//				sliderField.setText(""+minBetCents);
				slider.setValue((int)minBetCents);
			}
			for (int i = 0; i < buttons.length; i++) {
	        	String name = buttons[i].getName();
	        	if (buttons[i].isVisible() && bundle.getString("bet").equals(name)){
	        		if(++entCount == 1){
		        		buttons[i].doClick();
		        		break;
	        		}
	        	}
	        	if (buttons[i].isVisible() && bundle.getString("raise").equals(name)){
	        		if(++entCount == 1){
        				buttons[i].doClick();
        				break;
	        		}
	        	}
			}
		 }
    });
    sliderField.addMouseListener(new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
		sliderField.requestFocusInWindow();
		sliderField.selectAll();
	}

	/*@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		super.mouseEntered(arg0);
		slider.addChangeListener(cl);
		sliderField.addActionListener(sf);
		slider.setRequestFocusEnabled(true);
		sliderField.setRequestFocusEnabled(true);
	}*/
    });
    
    
    slider_bg = new JLabel(""/*Utils.getIcon("images/jslider_bg.png")*/);
    slider_bg.setOpaque(true);
    slider_bg.setBackground(new Color(170,170,170));
    slider_bg.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    
    sliderfield_bg = new JLabel(skin.reSizeImage(Utils.getIcon("images/sliderfield_bg.png")));
    sliderpanel_bg = new JLabel("");//Utils.getIcon("images/sliderpanel_bg.png")
    sliderpanel_bg.setOpaque(true);
    sliderpanel_bg.setBackground(new Color(205,205,205));
    
    arrow_left = new JLabel(skin.reSizeImage(Utils.getIcon("images/arrow_left.png")));
    arrow_left.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			double sliderMinValue = (double)slider.getMinimum()/100.0;//slider.getMinimum()/100;
			double currentVal = (double)slider.getValue()/100.0;
			double showVal =currentVal - minBet;
			//System.out.println("minBet "+minBet+","+showVal+" >= "+sliderMinValue);
			arrow_left.setIcon(skin.reSizeImage(Utils.getIcon("images/arrow_left_click.png")));
			arrow_left.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			if(showVal >= sliderMinValue){
				sliderField.setText(SharedConstants.doubleToString((double) (showVal)));
				slider.setValue((int)(showVal*100));
			}else{
				sliderField.setText(SharedConstants.doubleToString((double) (sliderMinValue)));
				slider.setValue((int)(sliderMinValue*100));
			}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			arrow_left.setBorder(null);
			arrow_left.setIcon(skin.reSizeImage(Utils.getIcon("images/arrow_left.png")));
		}
	});
    
    arrow_right = new JLabel(skin.reSizeImage(Utils.getIcon("images/arrow_right.png"))); 
    arrow_right.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
				//double sliderVal = (double)slider.getMinimum()/100.0;//slider.getMinimum()/100;
			double sliderMaxValue = (double)slider.getMaximum()/100.0;	
			double currentVal = (double)slider.getValue()/100.0;
				double showVal = currentVal + minBet ;
				//System.out.println("slider current Value"+currentVal+",now "+showVal+" <= "+sliderMaxValue);
				arrow_right.setIcon(skin.reSizeImage(Utils.getIcon("images/arrow_right_click.png")));
				arrow_right.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
				if(showVal <= sliderMaxValue){
					sliderField.setText(SharedConstants.doubleToString((double) (showVal)));
					slider.setValue((int)(showVal*100));
				}else{
					sliderField.setText(SharedConstants.doubleToString((double) (sliderMaxValue)));
					slider.setValue((int)(sliderMaxValue*100));
				}
		}
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			arrow_right.setBorder(null);
			arrow_right.setIcon(skin.reSizeImage(Utils.getIcon("images/arrow_right.png")));
		}
	});
    
    labelEuro = new JLabel("€  ");
    labelEuro.setFont(Utils.standartFont);
    labelEuro.setForeground(Color.BLACK);

    sliderField.setForeground(Color.BLACK);
    sliderField.setBackground(Color.WHITE);
    sliderField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    sliderField.setHorizontalAlignment(JTextField.RIGHT);
    //sliderField.setFont(Utils.sliderFont); //written in addLeftPanel
    
    

    limitAmt = new JLabel("",SwingConstants.RIGHT);
	limitAmt.setOpaque(false);
	//limitAmt.setFont(new Font("Franklin Gothic", Font.BOLD, 11));//written in addLeftPanel
	limitAmt.setForeground(Color.BLACK);
    
	sliderPanel = new JPanel(null);
    sliderPanel.setOpaque(false);
    
    /**sliderPanel.add(slider);
    sliderPanel.add(sliderField);
    
    sliderPanel.add(arrow_left);
    sliderPanel.add(arrow_right);
    sliderPanel.add(labelEuro);
    sliderPanel.add(slider_bg);
    sliderPanel.add(sliderfield_bg);
    sliderPanel.add(sliderMin);
    sliderPanel.add(sliderX2);
    sliderPanel.add(sliderX3);
    sliderPanel.add(sliderX4);
    sliderPanel.add(sliderX5);
    //sliderPanel.add(sliderHalfPot);
    //sliderPanel.add(sliderPot);
    sliderPanel.add(sliderMax);
    sliderPanel.add(limitAmt);
    sliderPanel.add(sliderpanel_bg);*/
    
    /** INFO */
    /*JPanel info = new JPanel(new GridLayout(1, 4));
    info.setOpaque(false);
    //info.add(handId);
    info.add(bet);
    info.add(pot);
    info.add(rake);
    info.add(allInsCount);*/

    
    /*	JPanel intPanel = new JPanel(new BorderLayout());
     intPanel.setOpaque(false);
     intPanel.add(checks, BorderLayout.SOUTH);
     intPanel.add(panel, BorderLayout.CENTER);
     */
    //	--- chatPanel
    /**
     * Agneya  fix to show different colours in the chat box
     * FIX 13
     */
    /*chatPanel = new JPanel(null); new FlowLayout(FlowLayout.RIGHT)
    chatPanel.setOpaque(false);
    chatPanel.setBounds(skin.getChatPanel());
    chatPanel.setBorder(BorderFactory.createLineBorder(Color.RED));*/
    //chatPanel.add(chatField);
    //chatPanel.add(speakButton);
    
    chatField = new JTextField(20);
    chatField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        if (speakButton.isVisible()) {
          if(chatField.getText().length() > 0)speakButton.doClick();
        }
      }
    });
    chatField.setOpaque(false);
    chatField.setBorder(null);
    chatField.setBackground(Color.WHITE);
    chatField.setForeground(Color.BLACK);
    chatField.setBounds((int)(11*ratioX),(int)(3*ratioY),(int)(180*ratioX),(int)(12*ratioY));
    chatField.setFont(Utils.chatFont);
    
    speakButton.setLocation(310, 80);
    speakButton.addActionListener(new chatAdapter());

    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    infoPanel.setOpaque(false);
    infoPanel.add(infoLabel);
    
    //cardLayout = new CardLayout();
    cardDisplayPanel = new JPanel(null/*cardLayout*/);
    cardDisplayPanel.setOpaque(false);
    cardDisplayPanel.add(infoPanel, CARD_INFO);
    cardDisplayPanel.add(intPanel, CARD_BUTTON);
    cardShowInfo();
   
    //centerPanel
    centerPanel = new JPanel(null); /*new BorderLayout()*/
    centerPanel.setOpaque(false);
    centerPanel.add(cardDisplayPanel);
    //centerPanel.add(chatPanel);
    //centerPanel.add(intPanel, BorderLayout.CENTER);
    //centerPanel.add(logsChatPanel);
    //centerPanel.add(info);
    //centerPanel.add(muteButton);

    chatTextPane =
        new JScrollPane(
        textArea,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
      {
    	public void paintComponent(Graphics g)
  	    {
  		    // Scale image to size of component
  		    Dimension d = getSize();
  		    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_CHATPANEL_BG);
  		    g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
  		    setOpaque( false );
  		    super.paintComponent(g);
  		}	
  	  };
  	chatTextPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() 
  	{  
  		public void adjustmentValueChanged(AdjustmentEvent e) 
  		{  
  			//if(chatTextPane.getVerticalScrollBar().getValue() == 0 )chatTextPane.getVerticalScrollBar().setValue(chatTextPane.getVerticalScrollBar().getMaximum());
  			//if(chatTextPane.getVerticalScrollBar().getMaximum() == chatTextPane.getVerticalScrollBar().getValue() )
  		   // e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
  		}
  	}); 
    chatTextPane.setViewportView(textArea);
    chatTextPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    chatTextPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    /*chatTextPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
    {
        public void adjustmentValueChanged(AdjustmentEvent event)
        {
        	System.out.println("----");
            JScrollBar  vbar = (JScrollBar) event.getSource();

            if (!event.getValueIsAdjusting()) return;
        
            if ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum())
                disableAutoScroll = false;
            else if (!disableAutoScroll)
                disableAutoScroll = true;
        }
    });
    
    chatTextPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() 
    {  
    	public void adjustmentValueChanged(AdjustmentEvent e) 
    	{ 
    		JScrollBar  vbar = (JScrollBar) e.getSource();
    		System.out.println(vbar.getValue()+"--"+vbar.getVisibleAmount()+"**"+vbar.getMaximum());
    		e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
    	}
    }); */
    chatTextPane.getViewport().setOpaque(false);
  	chatTextPane.setOpaque(false);
  	chatTextPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
  	chatTextPane.setAutoscrolls(true);
  	chatTextPane.setViewportView(textArea);
  	
  	chatFieldPanel = new JPanel()
    /*JPanel chatFieldPanel = new JPanel()
    {
    	public void paintComponent(Graphics g)
  	    {
  		    // Scale image to size of component
  		    Dimension d = getSize();
  		    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_CHATFIELD_BG);
  		    g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
  		    setOpaque( false );
  		    super.paintComponent(g);
  		}
    }*/
    ;
    chatFieldPanel.setLayout(new BoxLayout(chatFieldPanel, BoxLayout.X_AXIS));
    chatFieldPanel.add(chatField);
    chatFieldPanel.add(new JLabel("  "));
    chatFieldPanel.add(speakButton);
    
    foldToAnyBetCB.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			doFoldToAny = !doFoldToAny;
			
		}
	});
    //sitOutNextHandCB.setBounds(214, 77, 105, 17);
    sitOutNextHandCB.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(doSitin)
			{
				doSitin = false;
				pressLeftSitIn();
			}
			else
			{
				doSitin = true;
				pressLeftSitOut();
			}
			sitInOut.setVisible(false);
		}
	});
    //autoPostBlindCB.setBounds(214, 93, 105, 17);
    //labelAutoPostBlind.setBounds(215, 107, 120, 16);
    //labelFoldToAny.setBounds(215, 77, 120, 16);
    //labelSitOutNext.setBounds(215, 92, 120, 16);
    //sitInOut.setBounds(214, 77, 105, 17);
    sitInOut.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(doSitin)
			{
				doSitin = false;
				pressLeftSitIn();
				sitOutNextHandCB.setSelected(false);
			}
			else 
			{
				doSitin = true;
				pressLeftSitOut();
			}
			sitInOut.setVisible(false);
		}
	});
    //muckCardsCB.setBounds(214, 127, 105, 17);
    muckCardsCB.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
//			System.out.println("muckCardsCB.isSelected() "+muckCardsCB.isSelected());
			muckCards(muckCardsCB.isSelected());
			
		}
	});
    //centerPanel.setBounds(startx+ 415 , starty + 16, w2, he);
    //sliderPanel.setBounds(startx+ 290 , starty , w2, he);
    //centerPanel.setBounds(startx + w1, starty, w2, he);
    //pane.setBounds(startx + w1 + w2, starty, w3, he);
    //chatTextPane.setBounds(3 , 49, 210, 78);
    
    quickFold.setIcon(skin.getScaledImage(btn_quickfold));
    quickFold.setPressedIcon(skin.getScaledImage(btn_quickfold));
    //quickFold.setBounds(320, 100, 120, 35);
    quickFold.addActionListener(this);
    quickFold.addKeyListener(this);
    /** MISCELLANIOUS BOUNDS **/
    //info.setBounds(skin.getInfo());//20, 0, 400 - 5, 20
    //muteButton.setBounds(3, 70, 16, 16);
    
    addLeftPanel();
    
    this.add(chatFieldPanel);
    //this.add(muckCardsCB);
    //this.add(autoPostBlindCB);
    //this.add(sitOutNextHandCB);
    //this.add(foldToAnyBetCB);
//    this.add(sitInOut);
    //this.add(quickFold);
    //this.add(leftButtonPanel); /*, BorderLayout.WEST*/
    
    //this.add(panel/*, BorderLayout.SOUTH*/);
    
    //this.add(centerPanel); /*, BorderLayout.CENTER*/
    //this.add(sliderPanel);
    //this.add(checks);
//    this.add(chatTextPane); /*, BorderLayout.EAST*/
    this.setOpaque(false);
    this.setBorder(null);

    //int delay, ActionListener listener
    //PLAYER RESPONSE TIME IS LESS SO NO NEED THIS TIMER
    /*timer = new Timer(4000, new ActionListener() {//10000
      public void actionPerformed(ActionEvent e) {
        clientPokerController.tryPlayEffect(SoundManager.PIG);
        logItsYourTurn(_serverProxy._name);
        timer.stop();
      }
    });
    timer.setCoalesce(false);
    timer.setRepeats(false);*/
    					
    timer2 = new Timer(5000, new ActionListener() {//12000  7000
      public void actionPerformed(ActionEvent e) {
        clientPokerController.tryPlayEffectRep(SoundManager.YOUR_TURN);
        logItsYourTurn(_serverProxy._name);
        timer2.stop();
      }
    });
    timer2.setCoalesce(false);
    timer2.setRepeats(false);

    timer3 = new Timer(WAIT_INTERVAL, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        _cat.fine("timer3 :: actionPerformed()");
        showChecks();
        deselectOtherCheckBoxes(null);
        setSliderVisible(false);
        //normalizePanel();
        timer3.stop();
        //send fold to server
        /**	
         * if in actions[] player have check and fold options.. send check to server
         **/
        int actionVal = ActionConstants.FOLD;
        int actions[] = _brq.getAction();
        int position = _brq.getTarget();
        for (int i = 0; i < actions.length; i++) {
          _cat.fine("actions[i] = " + actions[i]);
          if(brqContains(actions, ActionConstants.CHECK)){
        	  _cat.fine("sending check to server");
        	  actionVal = ActionConstants.CHECK;
        	  break;
          }else if ( (actions[i] == ActionConstants.FOLD) ||
              (actions[i] == ActionConstants.PLAYER_SITOUT)) {
            actionVal = actions[i];
            appendLog(MessageFormat.format(bundle.getString("do.timeout"), new Object[] {_serverProxy._name}));
            break;
          }
        }
        BettingAction ba =
            new BettingAction(
            actionVal,
            position);
        //In case of timed out sending sit out
        if(!clientPokerController._model.gameType.isTourny() && !clientPokerController._model.gameType.isTPoker())
    	{
        	//if(actionVal == ActionConstants.FOLD){ 
        		_serverProxy.sitOutTable(clientPokerController._model.name);
        		doSitin = true;
        	//}
    	}
        _cat.fine(ba.toString()+" sending to server, timed out,");
        sendToServer(ba);
        //_cat.severe("after sent to server in Bottompanel");
        setInfoLabelText("");
        quickFoldVisible(false);
        cardShowInfo();
        //chatPanelSetVisible(true);
       // _cat.info("timer3 :: JOptionPane.showMessageDialog)");
//        JOptionPane.showMessageDialog(BottomPanel.this,
//                                      Bundle.getBundle().getString("timeout"),
//                                      "Error", JOptionPane.ERROR_MESSAGE);
        
        //by rk
        if(!clientPokerController._model.gameType.isTourny() && !clientPokerController._model.gameType.isTPoker())
        {
        	JFrame f = (JFrame)clientPokerController._clientRoom.getClientRoomFrame();
        	if(actionVal == ActionConstants.PLAYER_SITOUT){
		        if (JOptionPane.showInternalConfirmDialog(f.getContentPane(),"Do you want to leave", "Leave", 
		                JOptionPane.YES_NO_OPTION) == 
		                  JOptionPane.YES_OPTION) {
		        	_cat.fine("close the room");
		        	clientPokerController._clientRoom.closeRoomForce();
		        }else{
		        	_cat.fine("sit-out the player");
		        }
        	}
        }
        
      }
    });
    timer3.setCoalesce(false);
    timer3.setRepeats(false);

  
    //chatPanelSetVisible(false);
  }

  public void stopTimers() {
	//timer.stop(); commented by rk, because RESPONSE_TIME IS 10SEC
    timer2.stop();
    timer3.stop();
  }


  
  
  public void logItsYourTurn(String clientName) {
    appendLog(MessageFormat.format(
        bundle.getString("do.invited"), new Object[] {clientName}));
  }

//////////////// JPanel parameters ////////////////
  public void paintComponent(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    for (int i = 0; i < h + hinc; i = i + hinc) {
      for (int j = 0; j < w + winc; j = j + winc) {
        m_image.paintIcon(this, g, j, i);
      }
    }
  }

  public Dimension getPreferredSize() {
    return new Dimension(
    		(int)(ClientConfig.DEFAULT_BOTTON_PANEL_SIZE_W*skin._ratio_x),
            (int)(ClientConfig.DEFAULT_BOTTON_PANEL_SIZE_H*skin._ratio_y));
//        ClientConfig.DEFAULT_BOTTON_PANEL_SIZE_W,
//        ClientConfig.DEFAULT_BOTTON_PANEL_SIZE_H);
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

//////////////// JPanel parametrs ////////////////
  public boolean isMuting() {
    return muteButton.getModel().isSelected();
  }

  public void normalizePanel() {
    if (flag == CARD_BTN) {
      cardShowButton();
    }
    else if (flag == CARD_INF) {
      cardShowInfo();
    }
  }

  public void cardShowInfo() {
    flag = CARD_INF;
    //cardLayout.show(cardDisplayPanel, CARD_INFO);
    setSliderVisible(false);
  }

  public void cardShowButton() {
    //flag = CARD_BTN;
    //cardLayout.show(cardDisplayPanel, CARD_BUTTON);
    //chatPanelSetVisible(true);
    //	setSliderVisible(false);
  }

  protected boolean inviteToSeat(int place,
                                 double amtAtTable,
                                 double worth,
                                 double moneyMin,
                                 double moneyMax,
                                 double fee,
                                 boolean tournament, boolean limitGame) {
    //chatPanelSetVisible(false);
    String choice = null;
    while (!newMoneyRequest(place,  amtAtTable, worth, moneyMin, moneyMax, fee,
                            tournament, limitGame, choice)) {
      if (choice == null) {
        return false;
      }
    }
    return true;
  }

  public void clientRoomToFront() {
	  try {
		JFrame frame_cr = (ClientRoom) clientPokerController._clientRoom;
		  frame_cr.setVisible(true);
		  //frame_cr.setExtendedState(JFrame.NORMAL);
		  if(frame_cr.getState()!=Frame.NORMAL) { frame_cr.setState(Frame.NORMAL); }

		  frame_cr.toFront();
		  frame_cr.requestFocus();
		  frame_cr.repaint();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	}
  }
  public void frameToFront() {
	  try {
		Frame[] frame = LobbyFrame.getFrames();
		  Frame frame_lobby = null;
		  for(int i=0;i<frame.length ; i++)
		  {
			if(LobbyFrame.getFrames()[i].getTitle().contains("- Lobby"))
			{
				frame_lobby = LobbyFrame.getFrames()[i];
				break;
			}
		  }
		  frame_lobby.setExtendedState(frame_lobby.getExtendedState() & ~JFrame.ICONIFIED);
		  frame_lobby.setVisible(true);
		  frame_lobby.toFront();
		  frame_lobby.requestFocus();
	} catch (Exception e) {
	}
	  
  }
  public void crToFront() {
	  Frame[] frame = LobbyFrame.getFrames();
	  Frame frame_lobby = null;
	  for(int i=0;i<frame.length ; i++)
	  {
		if(LobbyFrame.getFrames()[i].getTitle().contains("hand id ="))
		{
			frame_lobby = LobbyFrame.getFrames()[i];
			break;
		}
	  }
	  frame_lobby.setExtendedState(frame_lobby.getExtendedState() & ~JFrame.ICONIFIED);
	  frame_lobby.setVisible(true);
	  frame_lobby.toFront();
	  frame_lobby.requestFocus();
	  
  }


  public void haveJoin(int allInsCount) {
//    leftButtonSit.setEnabled(true);
//    leftButtonSit.setVisible(false);
//    leftButtonSit.setText(bundle.getString("sitout"));
	if(clientPokerController._model.gameType.isTourny())
	{
	  sitInOut.setVisible(false);
	}
	if(!clientPokerController._model.gameType.isTPoker())
	setInfoLabelText(Bundle.getBundle().getString("info.joined"));
    this.allInsCount.setText(
        bundle.getString("all.in.remain") + Integer.toString(allInsCount));
    _cat.finest("Have Join !!!");
  }

  private boolean brqContains(int[] moves, int move) {
    for (int i = 0; i < moves.length; i++) {
      if (moves[i] == move) {
        return true;
      }
    }
    return false;
  }

  boolean checkfold_flag = false;

  public void getBetRequest(BetRequestAction brq) {
    //_cat.severe("Bet request action = " + brq + " Auto =" + _serverProxy._settings.isAutoPostBlind());
    /**
     * If autopost blind then donot render buttons and make the move automatically
     */
	  _brq = null;
    _brq = brq;
    entCount = 0;//reset enter count for sliderField
    this.showBetPanel = true;
    //by rk, if player quickFold don't render move buttons, send FOLD to server.
    if(clientPokerController._model.gameType.isTPoker() && clientPokerController._model.isquickFolded){
		 BettingAction ba = new BettingAction(ActionConstants.FOLD, _brq.getTarget(),
                 wantVal(_brq.getAmount(ActionConstants.FOLD),
               		  clientPokerController.getMyWorth()));
		 sendToServer(ba);
		 clientPokerController._model.isquickFolded = false;
		 _cat.fine("player quickFolded so sending FOLD");
		 appendLog(MessageFormat.format(bundle.getString("do.quickfold"), new Object[] {_serverProxy._name}));
		 return;
	  }
    int[] actionsMas = brq.getAction();
//    for(int i=0; i<actionsMas.length;i++){
//    	System.out.println(i+" - "+actionsMas[i]);
//    }

    // to prompt when user select fold even if he had chance to check.
    if (actionsMas.length == 3 || actionsMas.length == 4) {
    	if (actionsMas[0] == ActionConstants.FOLD && actionsMas[1] == ActionConstants.CHECK) { //if (actionsMas[0] == 102 && actionsMas[2] == 100) {
        checkfold_flag = true;
        _cat.finest(">>>>>>>>>>>> checkfold_flag setted");
      }
    }
    double[] amt = brq.getAmount();
    int position = brq.getTarget();
    BettingAction ba = null;
    
    if (autoPostBlindCB.isSelected()) {
      for (int i = 0; i < actionsMas.length; i++) {
        switch (actionsMas[i]) {
          case ActionConstants.BIG_BLIND:
            ba = new BettingAction(ActionConstants.BIG_BLIND, position,
                                   _brq.getAmount(ActionConstants.BIG_BLIND));
            betVal = _brq.getAmount(ActionConstants.BIG_BLIND);
            break;
          case ActionConstants.SMALL_BLIND:
            ba = new BettingAction(ActionConstants.SMALL_BLIND, position,
                                   _brq.getAmount(ActionConstants.SMALL_BLIND));
            break;
          case ActionConstants.SB_BB:
            ba = new BettingAction(ActionConstants.SB_BB, position,
                                   _brq.getAmount(ActionConstants.SB_BB));
            break;
          default:
            break;
        }
      }
      if (ba != null) {
        sendToServer(ba);
        setInfoLabelText("");
        deselectOtherCheckBoxes(null);
        return;
      }
    }
    
    if(doFoldToAny)
    {
    	if(brqContains(brq.getAction(), ActionConstants.CHECK)) {
        	  //send Check
		 	ba = new BettingAction(ActionConstants.CHECK,
	                          position,
	                          wantVal(_brq.getAmount(ActionConstants.CHECK),
	                        		  clientPokerController.getMyWorth()));
//		 	System.out.println("BottomPanel doFoldToAny CHECK cond "+clientPokerController.getMyWorth());
		 	sendToServer(ba);
		 	deselectOtherCheckBoxes(null);
		 	return;
        }
	    if((brqContains(brq.getAction(), ActionConstants.FOLD))) 
	    {
		   //send Fold
	       ba = new BettingAction(ActionConstants.FOLD,
		                          position,
		                          wantVal(_brq.getAmount(ActionConstants.FOLD),
		                        		  clientPokerController.getMyWorth()));
//	       System.out.println("BottomPanel doFoldToAny FOLD cond "+clientPokerController.getMyWorth());
		 	
		   doFoldToAny = false;
		   foldToAnyBetCB.setSelected(false);
		   sendToServer(ba);
		   deselectOtherCheckBoxes(null);
		    return;
	    }
    }

    /**
     * END Auto Actions
     */
    amount = brq.getAmount();
    amount_limit = brq.getAmountLimit();
    guid = brq.getGuid();
    //TODO - currently this is inefficient..change to bit flags...-sn-
    double playerChips = clientPokerController.getMyWorth();
    
    if(defaultCheckCallAny)
    {
    	if(brqContains(brq.getAction(), ActionConstants.CHECK)) {
    		//send check toserver
    		ba = new BettingAction(ActionConstants.CHECK,
                             position,
                             wantVal(_brq.getAmount(ActionConstants.CHECK),
                                     playerChips));
    	}
    	else if(brqContains(brq.getAction(), ActionConstants.CALL)) {
    		//send check toserver
    		ba = new BettingAction(ActionConstants.CALL,
                             position,
                             wantVal(_brq.getAmount(ActionConstants.CALL),
                                     playerChips));
    	}
//    	System.out.println("playerChips defaultCheckCallAny cond "+playerChips);
     	
    }
    else if(defaultCheckFold)
    {
    	if(brqContains(brq.getAction(), ActionConstants.CHECK)) {
        	 //send Check
        	  ba = new BettingAction(ActionConstants.CHECK,
                         position,
                         wantVal(_brq.getAmount(ActionConstants.CHECK),
                                 playerChips));
         }
         else if(brqContains(brq.getAction(), ActionConstants.FOLD)) {
        	 //send Fold
        	  ba = new BettingAction(ActionConstants.FOLD,
                         position,
                         wantVal(_brq.getAmount(ActionConstants.FOLD),
                                 playerChips));
         }
    }
    
    else if (defaultRaise && brqContains(brq.getAction(), ActionConstants.RAISE)) {
      //send Raise
    	ba = new BettingAction(ActionConstants.RAISE,
                             position,
                             wantVal(_brq.getAmount(ActionConstants.RAISE),
                                     playerChips));
    }
    else if (defaultRaiseAny )
    {
    	if(brqContains(brq.getAction(), ActionConstants.RAISE)) {
    		 ba = new BettingAction(ActionConstants.RAISE,
                             position,
                             wantVal(_brq.getAmount(ActionConstants.RAISE),
                                     playerChips));
      }
      else if(brqContains(brq.getAction(), ActionConstants.BET)) {
    	ba = new BettingAction(ActionConstants.BET,
                  position,
                  wantVal(_brq.getAmount(ActionConstants.BET),
                          playerChips));
      }
    }
    if (ba != null) {
//    	System.out.println("preaction defaultCheckCall:"+defaultCheckCall
//    			+" defaultCheckCallAny: "+defaultCheckCallAny
//    			+" defaultCheckFold: "+defaultCheckFold
//    			+" defaultFold: "+defaultFold
//    			+" defaultRaise: "+defaultRaise
//    			);
//    	System.out.println("ba.getId(): "+ba.getId());
      ba.setGuid(guid);
      sendToServer(ba);
      _cat.fine("Send to server " + ba);
      unCheckAll();
      deselectOtherCheckBoxes(null);
      return;
    }
    deselectOtherCheckBoxes(null);
    

    clientRoomToFront();
    //cardShowButton();
    setupSlider();
    //cardLayout.show(cardDisplayPanel, CARD_BUTTON);
    disibleChecks();
    if(sitInOut.isVisible())sitInOut.setVisible(false);
    quickFoldVisible(false);
    for (int i = 0; i < actionsMas.length; i++) {
      switch (actionsMas[i]) {
        case ActionConstants.FOLD:
          buttons[i].setText(bundle.getString("fold"));
          buttons[i].setName(bundle.getString("fold"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setIcon(btn_fold);
          buttons[i].setPressedIcon(btn_fold);
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          buttons[i].addKeyListener(this);
          break;
        case ActionConstants.CALL:
    	  StringBuilder sb1 = new StringBuilder();
    	  sb1.append("<html>").append(bundle.getString("call")).append(" <br> €").append((String.valueOf(amt[i]))).append("</html>");
    	  buttons[i].setText(sb1.toString());
//    	  buttons[i].setText("<html>"+bundle.getString("call")+ " <br> €" +  (String.valueOf(amt[i]))+"</html>");
          buttons[i].setName(bundle.getString("call"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setIcon(btn_call);
          buttons[i].setPressedIcon(btn_call);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          buttons[i].addKeyListener(this);
          break;
        case ActionConstants.CHECK:
          buttons[i].setText(bundle.getString("check"));
          buttons[i].setName(bundle.getString("check"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setIcon(btn_call);
          buttons[i].setPressedIcon(btn_call);
          buttons[i].setToolTipText("0.0");
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.ALLIN:
          StringBuilder sb2 = new StringBuilder();
          sb2.append("<html>").append(bundle.getString("allin")).append(" <br> €").append((String.valueOf(amt[i]))).append("</html>");
          buttons[i].setText(sb2.toString());
//        buttons[i].setText("<html>"+bundle.getString("allin")+ " <br> €"+  (String.valueOf(amt[i]))+"</html>");
          buttons[i].setName(bundle.getString("allin"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setIcon(btn_allin);
          buttons[i].setPressedIcon(btn_allin);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.RAISE:
          StringBuilder sb3 = new StringBuilder();
          sb3.append("<html>").append(bundle.getString("raise")).append(" <br> €").append((String.valueOf(amt[i]))).append("</html>");
          buttons[i].setText(sb3.toString());
//        buttons[i].setText("<html>"+bundle.getString("raise")+ " <br> €"+  (String.valueOf(amt[i]))+"</html>");
          buttons[i].setName(bundle.getString("raise"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setIcon(btn_raise);
          buttons[i].setPressedIcon(btn_raise);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.BET:
          StringBuilder sb4 = new StringBuilder();
          sb4.append("<html>").append(bundle.getString("bet")).append(" <br> €").append((String.valueOf(amt[i]))).append("</html>");
          buttons[i].setText(sb4.toString());
//        buttons[i].setText("<html>"+bundle.getString("bet")+ " <br> €"+  (String.valueOf(amt[i]))+"</html>");
          buttons[i].setName(bundle.getString("bet"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setIcon(btn_raise);
          buttons[i].setPressedIcon(btn_raise);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.BIG_BLIND:
        	//temp code later remove it when fix on server
        	if(clientPokerController._model.gameType.isTPoker()){
        		 ba = new BettingAction(ActionConstants.BIG_BLIND, position,
                         				_brq.getAmount(ActionConstants.BIG_BLIND));
        		 betVal = _brq.getAmount(ActionConstants.BIG_BLIND);
        		 if (ba != null) {
    		        sendToServer(ba);
    		        //new Exception("omit BIG_BLINDin TPoker ").printStackTrace();
    		        setInfoLabelText("");
    		        deselectOtherCheckBoxes(null);
    		        return;
    		      }
        	}
        	
        	/////////////////
        	
          StringBuilder sb5 = new StringBuilder();
          sb5.append(bundle.getString("bigblind")).append(" €").append((String.valueOf(amt[i])));
          buttons[i].setText(sb5.toString());
//          buttons[i].setText(bundle.getString("bigblind")+ " €"+  (String.valueOf(amt[i])));
          buttons[i].setName(bundle.getString("bigblind"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.SMALL_BLIND:
        	//temp code later remove it when fix on server
        	if(clientPokerController._model.gameType.isTPoker()){
        		ba = new BettingAction(ActionConstants.SMALL_BLIND, position,
                        _brq.getAmount(ActionConstants.SMALL_BLIND));
        		if (ba != null) {
    		        sendToServer(ba);
    		        //new Exception("omit SMALL_BLIND in TPoker").printStackTrace();
    		        setInfoLabelText("");
    		        deselectOtherCheckBoxes(null);
    		        return;
    		      }
        	}
        	
        	/////////////////
        	
          StringBuilder sb6 = new StringBuilder();
          sb6.append(bundle.getString("smallblind")).append(" €").append((String.valueOf(amt[i])));
          buttons[i].setText(sb6.toString());
//          buttons[i].setText(bundle.getString("smallblind")+ " €" +  (String.valueOf(amt[i])));
          buttons[i].setName(bundle.getString("smallblind"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.SB_BB:
          StringBuilder sb7 = new StringBuilder();
          sb7.append(bundle.getString("sbbb")).append(" €").append((String.valueOf(amt[i])));
          buttons[i].setText(sb7.toString());
//          buttons[i].setText(bundle.getString("sbbb")+ " €"+  (String.valueOf(amt[i])));
          buttons[i].setName(bundle.getString("sbbb"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.PLAYER_SITOUT:
          buttons[i].setText(bundle.getString("optout"));
          buttons[i].setName(bundle.getString("optout"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        case ActionConstants.ANTE:
          StringBuilder sb8 = new StringBuilder();
          sb8.append("<html>").append(bundle.getString("ante")).append("<br> €").append((String.valueOf(amt[i]))).append("</html>");
          buttons[i].setText(sb8.toString());
//          buttons[i].setText("<html>"+bundle.getString("ante")+ "<br> €" + (String.valueOf(amt[i]))+"</html>");
          buttons[i].setName(bundle.getString("ante"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          break;

      case ActionConstants.MORNING:
        buttons[i].setText(bundle.getString("morning"));
        buttons[i].setName(bundle.getString("morning"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        buttons[i].setToolTipText( (String.valueOf(amt[i])));
        break;
      case ActionConstants.AFTERNOON:
        buttons[i].setText(bundle.getString("afternoon"));
        buttons[i].setName(bundle.getString("afternoon"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        buttons[i].setToolTipText( (String.valueOf(amt[i])));
        break;
      case ActionConstants.EVENING:
        buttons[i].setText(bundle.getString("evening"));
        buttons[i].setName(bundle.getString("evening"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        buttons[i].setToolTipText( (String.valueOf(amt[i])));
        break;
        case ActionConstants.BRINGIN:
          buttons[i].setText(bundle.getString("bringin"));
          buttons[i].setName(bundle.getString("bringin"));
          buttons[i].setMargin(new Insets( -5, -5, -5, -5));
          buttons[i].setBorderPainted(false);
          buttons[i].setOpaque(false);
          buttons[i].setToolTipText( (String.valueOf(amt[i])));
          buttons[i].setVisible(true);
          buttons[i].setEnabled(true);
          break;
        default:
          buttons[i].setVisible(false);
          break;
      }
      buttons[i].setForeground(Color.WHITE);
    }
    
    if (actionsMas.length > 0) {
      //timer.stop(); commented by rk
      //timer.start(); commented by rk
      timer2.stop();
      timer2.start();
      _cat.fine("--Before starting timer3");
      timer3.stop(); //hack...
      timer3.start(); //timeout timer
    }
    
    clientPokerController._model.stopNextMoveTimer();
  }

  private void disibleChecks() {
	checkCheckFold.setVisible(false);
    checkFold.setVisible(false);
    checkCheckCall.setVisible(false);
    checkCheckCallAny.setVisible(false);
    checkRaise.setVisible(false);
    checkRaiseAny.setVisible(false);
    unCheckAll();
  }
//  public void disableAutoPostChecks(){
//	  disibleChecks();
//	  new Exception("uncheckAll").printStackTrace();
//  }

  public void showButtons() {
	if(quickFold.isVisible())quickFoldVisible(false);
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setVisible(true);
      buttons[i].setEnabled(true);
      sliderField.setText("--");
    }
    disibleChecks();
  }

  public void showChecks() {
	  hideMoveButtons();
    if(sitOutNextHandCB.isEnabled())enableChecks();
  }
  public void hideMoveButtons() {
	 for (int i = 0; i < buttons.length; i++) {
      buttons[i].setVisible(false);
    }
  }
  
  public void quickFoldVisible(boolean bool){
	  quickFold.setVisible(bool);
  }
  
//  public void showChecks() {
//    for (int i = 0; i < buttons.length; i++) {
//      buttons[i].setVisible(false);
//    }
//    if(sitOutNextHandCB.isEnabled())enableChecks();
//  }

  private void enableChecks() {
	checkCheckFold.setVisible(true);
    checkFold.setVisible(true);
    checkCheckCall.setVisible(true);
    checkCheckCallAny.setVisible(true);
    checkRaise.setVisible(true);
    checkRaiseAny.setVisible(true);
  }
  
  public void actionPerformed(ActionEvent action) {
    SoundManager.loopTest();
    if (action.getSource() == checkCheckFold ||
        action.getSource() == checkFold ||
        action.getSource() == checkCheckCall ||
        action.getSource() == checkCheckCallAny ||
        action.getSource() == checkRaise ||
        action.getSource() == checkRaiseAny) {
      //Logger.log("CHECKED PERFORMED ACTION");
      JCheckBox check = (JCheckBox) action.getSource();
      itemStateChanged(new ItemEvent(check, 0, null,
                                     check.getModel().isSelected() ?
                                     ItemEvent.SELECTED : ItemEvent.DESELECTED));
      return;
    }

    // TOGGLE BUTTONS
    if (action.getSource() instanceof JToggleButton) {
      processChatButtonsClick( (JToggleButton) action.getSource());
      return;
    }
    

    BettingAction ba = null;
//    String buttonName = ( (ActionJButton) action.getSource()).getTText();
    String buttonName = ( (JButton) action.getSource()).getName();
//    int sliderValue = slider.getValue();

    int localPlayerNo = clientPokerController.getMyPlayerNo();
    double playerChips = clientPokerController.getMyWorth();
    if(buttonName.equalsIgnoreCase(bundle.getString("quick.fold")))
    {
    	_serverProxy.quickFold(clientPokerController._model.name);
    	clientPokerController._model.isquickFolded = true;//by rk
    	quickFoldVisible(false);
    	appendLog(MessageFormat.format(bundle.getString("do.quickfold"), new Object[] {_serverProxy._name}));
    	return;
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("fold"))) {
      if (checkfold_flag && brqContains(_brq.getAction(), ActionConstants.CHECK)
    		  && !clientPokerController._model.gameType.isTPoker()) {
    	 
    	  JFrame f = (JFrame)clientPokerController._clientRoom.getClientRoomFrame();
        if (JOptionPane.showInternalConfirmDialog(f.getContentPane(),
                                          "You can check. Do you still want to fold?",
                                          "Fold Alert",
                                          JOptionPane.YES_NO_OPTION) ==
            JOptionPane.YES_OPTION) {
          ba =
              new BettingAction(
              ActionConstants.FOLD,
              localPlayerNo);
          checkfold_flag = false;
          _cat.finest("YES: checkfold_flag cleared: " + checkfold_flag);
        }
        else {
          ba = null;
        }
      }
      else {
        ba =
            new BettingAction(
            ActionConstants.FOLD,
            localPlayerNo);
      }
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("call"))) {
      ba =
          new BettingAction(
          ActionConstants.CALL,
          localPlayerNo,
          _brq.getAmount(ActionConstants.CALL));
      checkfold_flag = false;
      _cat.finest("call: >>>>>>>>> checkfold_flag cleared: ");
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("check"))) {
      ba =
          new BettingAction(
          ActionConstants.CHECK,
          localPlayerNo);
      checkfold_flag = false;
      _cat.finest("check: >>>>>>>>> checkfold_flag cleared: ");
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("allin"))) {
      ba =
          new BettingAction(
          ActionConstants.ALLIN,
          localPlayerNo,
          _brq.getAmount(ActionConstants.ALLIN));
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("raise"))) {
      int sv = 0;
      int a = slider.getValue();
      if (a != 0) {
        sv = processSliderValue();
      }
      ba =
          new BettingAction(
          ActionConstants.RAISE,
          localPlayerNo,
          ( (slider.isVisible()) ? (double) sv / 100 :
           wantVal(_brq.getAmount(ActionConstants.RAISE), playerChips)));
      checkfold_flag = false;
      _cat.finest("raise: >>>>>>>>> checkfold_flag cleared: ");
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("bet"))) {
      int sv = 0;
      int a = slider.getValue();
      if (a != 0) {
        sv = processSliderValue();
      }

      ba =
          new BettingAction(
          ActionConstants.BET,
          localPlayerNo,
          ( (slider.isVisible()) ? (double) sv / 100 :
           wantVal(_brq.getAmount(ActionConstants.BET), playerChips)));
      checkfold_flag = false;
      _cat.finest("bet: >>>>>>>>> checkfold_flag cleared: ");
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("bigblind"))) {
      ba =
          new BettingAction(
          ActionConstants.BIG_BLIND,
          localPlayerNo,
          _brq.getAmount(ActionConstants.BIG_BLIND));
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("smallblind"))) {
      ba =
          new BettingAction(
          ActionConstants.SMALL_BLIND,
          localPlayerNo,
          _brq.getAmount(ActionConstants.SMALL_BLIND));

    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("sbbb"))) {
      ba =
          new BettingAction(
          ActionConstants.SB_BB,
          localPlayerNo,
          _brq.getAmount(ActionConstants.SB_BB));

    }

  else if (buttonName.equalsIgnoreCase(bundle.getString("morning"))) {
    // get selected cards
    PlCard crd[] = clientPokerController.getMyPlayer().getPocketCards();
    StringBuilder sbuf = new StringBuilder();
    for (int i=0;i<crd.length;i++){
      if (crd[i].isSelected){
        sbuf.append(crd[i].getCard().toString()).append("'");
      }
      else {
          sbuf.append("__'");
      }
    }
    if (sbuf.length() > 2){
      sbuf.deleteCharAt(sbuf.length() - 1);
    }

    _cat.finest("Selected cards = " + sbuf.toString());
    ba =
        new BettingAction(
        ActionConstants.MORNING,
        localPlayerNo,
        0, sbuf.toString());
  }

  else if (buttonName.equalsIgnoreCase(bundle.getString("afternoon"))) {
  // get selected cards
    PlCard crd[] = clientPokerController.getMyPlayer().getPocketCards();
    StringBuilder sbuf = new StringBuilder();
    for (int i=0;i<crd.length;i++){
      if (crd[i].isSelected){
        sbuf.append(crd[i].getCard().toString()).append("'");
      }
      else {
          sbuf.append("__'");
      }
    }
    if (sbuf.length() > 2){
      sbuf.deleteCharAt(sbuf.length() - 1);
    }

    _cat.finest("Selected cards = " + sbuf.toString());

    ba =
        new BettingAction(
        ActionConstants.AFTERNOON,
        localPlayerNo,
        0, sbuf.toString());
  }

  else if (buttonName.equalsIgnoreCase(bundle.getString("evening"))) {// get selected cards
    PlCard crd[] = clientPokerController.getMyPlayer().getPocketCards();
    StringBuilder sbuf = new StringBuilder();
    for (int i=0;i<crd.length;i++){
      if (crd[i].isSelected){
        sbuf.append(crd[i].getCard().toString()).append("'");
      }
      else {
          sbuf.append("__'");
      }
    }
    if (sbuf.length() > 2){
      sbuf.deleteCharAt(sbuf.length() - 1);
    }
    _cat.finest("Selected cards = " + sbuf.toString());

    ba =
        new BettingAction(
        ActionConstants.EVENING,
        localPlayerNo,
        0, sbuf.toString());
  }

    else if (buttonName.equalsIgnoreCase(bundle.getString("ante"))) {
        ba =
            new BettingAction(
            ActionConstants.ANTE,
            localPlayerNo,
            _brq.getAmount(ActionConstants.ANTE));
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("bringin"))) {
        ba =
            new BettingAction(
            ActionConstants.BRINGIN,
            localPlayerNo,
            _brq.getAmount(ActionConstants.BRINGIN));
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("optout"))) {
      ba =
          new BettingAction(
          ActionConstants.PLAYER_SITOUT,
          localPlayerNo);
      pressLeftSitOut();
    }
    if (ba != null) {
      ba.setGuid(guid);
      clientPokerController._model._me.setSelected(false);
      //clientPokerController._model.setSayMessage(localPlayerNo, ba.getId(), ba.getBet());
      clientPokerController._model.stopNextMoveTimer();
      sendToServer(ba);
      _cat.fine("Send to server " + ba + "localPlayerNo:" + localPlayerNo);

      if (localPlayerNo >= 0) {
        EventQueue.invokeLater(
            new ForHaveFoldAction(
            ba.getId(),
            localPlayerNo,
            ba.getBet(),
            ba.isAllIn()));
      }
    }
    stopTimers();
    setSliderVisible(false);
  }

  public int processSliderValue() {
    int sv = slider.getValue();
    return sv;

  }

  private double wantVal(double want, double have) {
    if (have < 0) {
      return want;
    }
    _cat.finest("want Val  have =" + have + ", want = " + want);
    return want < have ? want : have;
  }

  protected void muckCards(boolean muck) {
    if (muck) {
    	_cat.fine("muck cards checked");
      _serverProxy.muckCards(clientPokerController._model.name);
    }
    else {
    	_cat.fine("muck cards unchecked");
      _serverProxy.dontMuck(clientPokerController._model.name);
      
    }
  }
  
  protected void pressLeftSitOut() {
    _cat.fine("Sitting out");
    /*if (leftButtonLeave.getModel().isSelected() &&
        !leftButtonSit.getModel().isSelected()) {
      leftButtonLeave.doClick();
    }
    else {*/
    System.out.println("pressLeftSitOut");
      _cat.finest("button is in selected state.......sending sitot to server");
      _serverProxy.sitOutTable(clientPokerController._model.name);
      // change the button to sitin
      leftButtonSit.setText(bundle.getString("sitin"));
    
  }

  protected void pressLeftSitIn() {
    _cat.fine("Sitting in ");
    /*if (leftButtonLeave.getModel().isSelected() &&
        !leftButtonSit.getModel().isSelected()) {
      leftButtonLeave.doClick();
    }
    else  {*/
    System.out.println("pressLeftSitIn");
      _cat.finest("button is in selected state.......sending sitin to server");
      _serverProxy.sitInTable(clientPokerController._model.name);
      // change the button to sitout
      leftButtonSit.setText(bundle.getString("sitout"));
      sitOutNextHandCB.setSelected(false);
    
  }

  protected void chatPanelSetVisible(boolean chatVisible) {
    // chatPane.setVisible replaced by chatPaneSetVisible
    //chatPanel.setVisible(chatVisible);
  }

  class ForHaveFoldAction
      implements Runnable {
    BettingAction ba;
    ForHaveFoldAction(int id, int target, double bet, boolean isAllIn) {
      ba = new BettingAction(id, target , bet, isAllIn);
      ba.setGuid(guid);
    }

    public void run() {
      clientPokerController.haveBetAction(ba);
    }
  };

  /**
   *
   */
  private void processChatButtonsClick(JToggleButton button) {
    boolean isChat = false;
    boolean isMess = false;
    if (button == rightButtonBoth) {
      rightButtonBoth.getModel().setSelected(true);
      rightButtonChat.getModel().setSelected(false);
      rightButtonText.getModel().setSelected(false);
      isChat = true;
      isMess = true;

    }
    else if (button == rightButtonChat) {
      rightButtonBoth.getModel().setSelected(false);
      rightButtonChat.getModel().setSelected(true);
      rightButtonText.getModel().setSelected(false);
      isChat = true;

    }
    else if (button == rightButtonText) {
      rightButtonBoth.getModel().setSelected(false);
      rightButtonChat.getModel().setSelected(false);
      rightButtonText.getModel().setSelected(true);
      isMess = true;

    }
    sampleModel.clear();//textArea.setText("");
    StringBuilder sb = new StringBuilder();
    sb.append("t").append(HAND_SEPARATOR).append("\n");
    String HendSeparator = sb.toString();
//    String HendSeparator = "t" + HAND_SEPARATOR + "\n";
    for (int i = 0; i < logVector.size(); i++) {
      String element = (String) logVector.get(i);
      if (element.charAt(0) == 'c' && isChat) {
    	  StringBuilder sb1 = new StringBuilder();
    	  textAreaAppend(sb1.append(" Dlr: ").append(element.substring(1, element.length())).toString(),
                  Utils.smallChatFont);
//        textAreaAppend(" Dlr: "+element.substring(1, element.length()),
//                       Utils.smallChatFont);
      }
      if ( (element.charAt(0) == 't' && isMess)
          || (element.equals(HendSeparator))) {
    	  StringBuilder sb2 = new StringBuilder();
    	  textAreaAppend(sb2.append(" Dlr: ").append(element.substring(1, element.length())).toString(),
                  Utils.smallChatFont);
//        textAreaAppend(" Dlr: "+element.substring(1, element.length()),
//                       Utils.smallChatFont);
      }
    }
////////////////////////		scrollTextAreaAsNeeded();
  }

  public void sendToServer(BettingAction ba) {
    if (ba != null) {
	  // to disable the player after made any move..
    	if(clientPokerController._model._me != null)
    		clientPokerController._model.stopNextMoveTimer();
        clientPokerController._model._me.setSelected(false);
      //clientPokerController._model._players[clientPokerController.getMyPlayerNo()].refreshNamePlate();
	  //clientPokerController._model.setSayMessage(clientPokerController.getMyPlayerNo(), ba.getId(), ba.getBet());
        
      //----------------------------------------------
      tableProxySendToServer(clientPokerController._model.name, ba);
      clientPokerController._model.setLastSendBetAction(ba);
      if ( (ba.getId() == ActionConstants.FOLD ||
            ba.getId() == ActionConstants.PLAYER_SITOUT) &&
          ba.getType() == ActionConstants.ACTION_TYPE_BETTING) {
        int no = clientPokerController.getMyPlayerNo();
        if (no >= 0) {
          clientPokerController._model._players[no].getPocketCards();
        }
      }
      _cat.finest(" !!! tableProxy.sendToServer   = " + ba);
      disibleChecks();
      showChecks();
      showBetPanel = false;
      if(clientPokerController._model.gameType.isTPoker()){
    	  if(ba.getId() != ActionConstants.FOLD ){
    		//player goes all-In don't render quickfold
    		  if(ba.getId() == ActionConstants.ALLIN){
    			  quickFoldVisible(false);
    		  }else{
    			  if(clientPokerController._model.plrCount <= 2)
            		  quickFoldVisible(false);
    			  else
    				  quickFoldVisible(true);
    		  }
    		//by rk, when player amount on table is <= 2 * BB then don't show quickFold button
     		 // System.out.println("amt on table "+clientPokerController._model.getPlayerMoneyAtTable()+", "+ba.getBet());
     		  if(clientPokerController._model.getPlayerMoneyAtTable()-ba.getBet() < _brq.getMinBet() * 2){
     			  quickFoldVisible(false);
     		  }
    	  }
      }
    }
  }

  public void setInfoStrip(Action action, double atTable) {
    InfoAction ia = (InfoAction) action;
    bet.setText(
        getInfoLabel(bundle.getString("bet"))
        +
        ia.getBet());
    pot.setText(
        getInfoLabel(bundle.getString("pot"))
        + ia.getPot());

    rake.setText(
        getInfoLabel(bundle.getString("rake"))
        + ia.getRake());
  }

  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    BettingAction action = null;
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (src == checkCheckFold) {
        defaultCheckFold = true;
        action = new BettingAction(ActionConstants.CHECK_FOLD,
                                   clientPokerController.getMyPlayerNo());
      }
      else if (src == checkFold) {
        defaultFold = true;
      }
      else if (src == checkCheckCall) {
        defaultCheckCall = true;
      }
      else if (src == checkCheckCallAny) {
        defaultCheckCallAny = true;
      }
      else if (src == checkRaise) {
        defaultRaise = true;
      }
      else if (src == checkRaiseAny) {
        defaultRaiseAny = true;
      }
      deselectOtherCheckBoxes(src);
    }
    else {
      if (src == checkCheckFold) {
        defaultCheckFold = false;
      }
      else if (src == checkFold) {
        defaultFold = false;
      }
      else if (src == checkCheckCall) {
        defaultCheckCall = false;
      }
      else if (src == checkCheckCallAny) {
        defaultCheckCallAny = false;
      }
      else if (src == checkRaise) {
        defaultRaise = false;
      }
      else if (src == checkRaiseAny) {
        defaultRaiseAny = false;
      }
    }
    /*
         if (action != null) {
      action.setType(ActionConstants.ACTION_TYPE_PREBETTING);
      sendToServer(action);
         }
     */
  }

  void deselectOtherCheckBoxes(Object src) {
    checkCheckFold.setSelected(src == checkCheckFold);
    checkFold.setSelected(src == checkFold);
    checkCheckCall.setSelected(src == checkCheckCall);
    checkCheckCallAny.setSelected(src == checkCheckCallAny);
    checkRaise.setSelected(src == checkRaise);
    checkRaiseAny.setSelected(src == checkRaiseAny);
  }

  private void setupSlider() {
    if (clientPokerController == null) {
      _cat.finest("clientPokerController is null ");
      return;
    }
    boolean enableSlider = false;
    limitType = _brq.getMaxBet() != 0?"NL" :"PL";
    int move_type = -1;
    if (_brq.isInActions(ActionConstants.BET)) {
      _cat.finest("bet amt limit = " + _brq.getAmountLimit(ActionConstants.BET));
      move_type = ActionConstants.BET;
      enableSlider = (_brq.getAmountLimit(ActionConstants.BET) >
                      _brq.getAmount(ActionConstants.BET)) ? true : false;
    }
    else if (_brq.isInActions(ActionConstants.RAISE)) {
      _cat.finest("raise amt limit = " + _brq.getAmount(ActionConstants.RAISE) +
                 ":" +
                 _brq.getAmountLimit(ActionConstants.RAISE));
      move_type = ActionConstants.RAISE;
      enableSlider = (_brq.getAmountLimit(ActionConstants.RAISE) >
                      _brq.getAmount(ActionConstants.RAISE)) ? true : false;
    }
    enableBetSlider = enableSlider;
    if (!enableSlider) {
      _cat.finest("No slider");
      slider.setVisible(false);
      //sliderMin.setVisible(false);
      //sliderX2.setVisible(false);
      sliderX3.setVisible(false);
      sliderX4.setVisible(false);
      sliderX5.setVisible(false);
      //sliderHalfPot.setVisible(false);
      //sliderPot.setVisible(false);
      sliderMax.setVisible(false);
      return;
    }

    Pot[] totalPot = null;
    
    /** @todo  FIX SLIDER VALUES FOR DIFFERENT MOVES*/
    minVal = _brq.getAmount(move_type);
    maxVal = _brq.getAmountLimit(move_type);
    minBet = _brq.getMinBet();//BIG_BLIND value
    totalPot = clientPokerController._model.getPot();
   
    for(int i = 0;totalPot != null && i < totalPot.length;i++)
    {
    	potVal = (totalPot[i]._value + totalPot[i]._totalBet);
    }
    //_cat.finest("minVal = " + minVal);
    //_cat.finest("maxVal = " + maxVal);
    //_cat.finest("potVal = " + potVal);
    slider.setMinimum( (int) (minVal * 100));
    slider.setMaximum(limitType.equals("PL") ? (int)(potVal * 100) : (int) (maxVal * 100));
    slider.setValue( (int) (minVal * 100));
    setSliderVisible(minVal != maxVal);
    sliderField.setText( Double.toString(minVal));
    
    StringBuilder sb = new StringBuilder();
    sb.append((double)slider.getMinimum()/100)
    .append(" - ").append((double)slider.getMaximum()/100);
    limitAmt.setText(sb.toString());
    
   // System.out.println("bet round "+clientPokerController._model.getRound());
    
  }

  private void setSliderVisible(boolean visible) {
	//jlSliderBG.setVisible(visible);
	slider.setVisible(visible);
	slider_bg.setVisible(visible);
	arrow_left.setVisible(visible);
	arrow_right.setVisible(visible);
    sliderField.setVisible(visible);
    labelEuro.setVisible(visible);
    sliderfield_bg.setVisible(visible);
    sliderpanel_bg.setVisible(visible);
    
	//sliderMin.setVisible(visible);
	//sliderX2.setVisible(visible);
	sliderX3.setVisible(visible);
	sliderX4.setVisible(visible);
	sliderX5.setVisible(visible);
	//sliderHalfPot.setVisible(visible);
    //sliderPot.setVisible(visible);
    sliderMax.setVisible(visible);
    limitAmt.setVisible(visible);
    
    
    if (visible) {
    	if(limitType.equals("NL"))
    	{
    		//sliderPot.setVisible(false);
    		setSLiderXButtons(potVal*100, minBet*100);//slider.getMaximum()
    		//resize code
    		sliderMax.setLocation(sliderX3.getLocation().x+(int)(129*ratioX), (int)((28)*ratioY));
    		//sliderMax.setLocation(sliderMin.getLocation().x + 108, 0);
    	}
    	else
    	{
    		//resize code
    		//sliderPot.setLocation(skin.getSliderMinLocation(108).x, 0);
    		//sliderPot.setLocation(sliderMin.getLocation().x + 108, 0);
    		setSLiderXButtons(potVal*100, minBet*100);
    		sliderMax.setVisible(false);
    		//sliderX5.setVisible(false);
    	}
    }
    
  }

  public void setSLiderXButtons(double ref, double min)
  {
	  if(clientPokerController._model.getRound() >= 0){
			 //System.out.println((double)slider.getMinimum()+" < -- >"+ref); 
			/*if((double)slider.getMinimum() > ref/4 || (double)slider.getMaximum() < ref/4)
			{
				sliderX2.setEnabled(false);
				sliderX2.setText("1/4");
				sliderX2.removeMouseListener(x2);
			}else{
				sliderX2.setEnabled(true);
				sliderX2.setText("1/4");
				sliderX2.addMouseListener(x2);
			}*/
			
			if((double)slider.getMinimum() > ref/2 || (double)slider.getMaximum() < ref/2){
				sliderX3.setEnabled(false);
				sliderX3.setText("1/2");
				sliderX3.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
				sliderX3.removeMouseListener(x3);
			}else{
				sliderX3.setEnabled(true);
				sliderX3.setText("1/2");
				sliderX3.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
			    sliderX3.addMouseListener(x3);
			}
			
			if((double)slider.getMinimum() > (ref*3)/4 || (double)slider.getMaximum() < (ref*3)/4){
				sliderX4.setEnabled(false);
				sliderX4.setText("3/4");
				sliderX4.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
			    sliderX4.removeMouseListener(x4);
			}else{
				sliderX4.setEnabled(true);
				sliderX4.setText("3/4");
				sliderX4.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
			    sliderX4.addMouseListener(x4);
			}
			if((double)slider.getMinimum() > ref || (double)slider.getMaximum() < ref){
				sliderX5.setEnabled(false);
				sliderX5.setText(bundle.getString("slider.pot"));
				sliderX5.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
			    sliderX5.removeMouseListener(x5);
			}else{
				sliderX5.setEnabled(true);
				sliderX5.setText(bundle.getString("slider.pot"));
				sliderX5.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
			    sliderX5.addMouseListener(x5);
			}
				sliderMax.setText(bundle.getString("slider.max"));//set name for Max label
				sliderMax.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
			    
			}else if(clientPokerController._model.getRound() == -1){
				//System.out.println("bet round "+clientPokerController._model.getRound()+","+min+" - "+ (double)slider.getMinimum()+","+(double)slider.getMaximum());
				/*if((double)(3*min) <(double)slider.getMinimum() || (double)slider.getMaximum() < (double)(3*min))
				{
					sliderX2.setEnabled(false);
					sliderX2.setText(SharedConstants.doubleToString((double)(3*min)/100));
					sliderX2.removeMouseListener(x2);
				}else{
					sliderX2.setEnabled(true);
					sliderX2.setText(SharedConstants.doubleToString((double)(3*min)/100));
					sliderX2.addMouseListener(x2);
				}*/
				if((double)(3*min) <(double)slider.getMinimum() || (double)slider.getMaximum() < (double)(3*min)){
					sliderX3.setEnabled(false);
					sliderX3.setText("€"+SharedConstants.doubleToString((double)(3*min)/100));
					sliderX3.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderX3.removeMouseListener(x3);
				}else{
					sliderX3.setEnabled(true);
					sliderX3.setText("€"+SharedConstants.doubleToString((double)(3*min)/100));
					sliderX3.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderX3.addMouseListener(x3);
				}
				
				if((double)(4*min) <(double)slider.getMinimum() || (double)slider.getMaximum() < (double)(4*min)){
					sliderX4.setEnabled(false);
					sliderX4.setText("€"+SharedConstants.doubleToString((double)(4*min/100)));
					sliderX4.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderX4.removeMouseListener(x4);
				}else{
					sliderX4.setEnabled(true);
					sliderX4.setText("€"+SharedConstants.doubleToString((double)(4*min)/100));
					sliderX4.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderX4.addMouseListener(x4);
				}
				if((double)(5*min) <(double)slider.getMinimum() || (double)slider.getMaximum() < (double)(5*min)){
					sliderX5.setEnabled(false);
					sliderX5.setText("€"+SharedConstants.doubleToString((double)(5*min)/100));
					sliderX5.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderX5.removeMouseListener(x5);
				}else{
					sliderX5.setEnabled(true);
					sliderX5.setText("€"+SharedConstants.doubleToString((double)(5*min)/100));
					sliderX5.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderX5.addMouseListener(x5);
				}
				if((double)(6*min) <(double)slider.getMinimum() || (double)slider.getMaximum() < (double)(6*min)){
					sliderMax.setEnabled(false);
					sliderMax.setText("€"+SharedConstants.doubleToString((double)(6*min)/100));
					sliderMax.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderMax.removeMouseListener(xMax);
				}else{
					sliderMax.setEnabled(true);
					sliderMax.setText("€"+SharedConstants.doubleToString((double)(6*min)/100));
					sliderMax.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
					sliderMax.addMouseListener(xMax);
				}
			}
  }
  
  
  //java.util.logging.Logger logger;
  private int chatLimit = 300;
  int sepCount = 0;
  

  public void appendLog(String str) {
	//  new Exception("appendLog> Dlr: "+str + "\n").printStackTrace();
    if (HAND_SEPARATOR.equals(str)) {
      removeBlocks(2);
    }
    StringBuilder sb = new StringBuilder();
    //logVector.add(sb.append("t").append(str).append("\n"));
    if (rightButtonBoth.getModel().isSelected()
        || rightButtonText.getModel().isSelected()
        /*|| HAND_SEPARATOR.equals(str)*/) {

     // _cat.severe(str + "***********************************************************\n");
    	 if (!HAND_SEPARATOR.equals(str)){
    		 StringBuilder sb1 = new StringBuilder();
    		 textAreaAppend(sb1.append("Dlr: ").append(str).append("\n").toString(), Utils.smallChatFont);
    	 }
    }
    //textArea.ensureIndexIsVisible(textArea.getModel().getSize()-1);
  }

  public void unCheckAll() {
    defaultCheckFold = false;
    defaultFold = false;
    defaultCheckCall = false;
    defaultCheckCallAny = false;
    defaultRaise = false;
    defaultRaiseAny = false;

  }

  public void appendChat(String str) {
	  StringBuilder sb1 = new StringBuilder();
	  //logVector.add(sb1.append("c>>>").append(str).append("\n"));
    if (rightButtonBoth.getModel().isSelected()
        || rightButtonChat.getModel().isSelected()) {
    	StringBuilder sb2 = new StringBuilder();
    	textAreaAppend(sb2.append(str).append("\n").toString(), Utils.chatFont);
    }
  }
  
  private void textAreaAppend(String text, Font f) {
    textArea.setFont(f);
    sampleModel.addElement(text);
    scrollToListBottom();
    //this if cond. not working when table is resized.
    /*if (chatTextPane.getVerticalScrollBar().getMaximum() - chatTextPane.getVerticalScrollBar().getValue() < 110 ) exact difference is 76
    {
      sampleModel.addElement(text);
      scrollToListBottom();
    }
    else {
      sampleModel.addElement(text);
    }*/
    //System.out.println("sampleModel size "+sampleModel.size()+", "+sampleModel.get(0)+",logVector size "+logVector.size());
    
  }
  private void scrollToListBottom() {

	  SwingUtilities.invokeLater(new Runnable() {

	  public void run() {

	  DefaultListModel DLM_model = (DefaultListModel)textArea.getModel();
	  textArea.ensureIndexIsVisible(DLM_model.getSize()-1);

	  }

	  });
  }
  public static final String HAND_SEPARATOR = "====================";

  public void appendSep() {
    appendLog(HAND_SEPARATOR);
  }

  public void removeBlocks(int count) {
    //sepCount++;
    //--to unselect for new hand
    foldToAnyBetCB.setSelected(false);
    doFoldToAny = false;
    sampleModel.addElement(HAND_SEPARATOR+"\n");//textArea.setText(text.toString());
    //----------------------------
    /*if (sepCount > count) {
   	
      sampleModel.addElement(HAND_SEPARATOR+"\n");//textArea.setText(text.toString());
      sepCount--;

      int allSepCount = 0;
      int p = 0;
      StringBuilder sb = new StringBuilder();
      String strToCompare = sb.append("t").append(HAND_SEPARATOR).append("\n").toString();
      while ( (p = logVector.indexOf(strToCompare, p)) >= 0
             && logVector.size() > 0) {
        allSepCount++;
        p++;
      }
      while (allSepCount >= count) {
        if (strToCompare.equals(logVector.remove(0))) {
          allSepCount--;
        }
      }
      if (!checkScrollList.getModel().isSelected()) {
      }
    }*/
    
  //by rk,
  	/*not using right now
  	 * if(logVector.size() >= chatLimit){
  		Vector v = new Vector(logVector.subList(chatLimit/2, logVector.size()-1));
  		logVector.removeAllElements();
  		logVector =v;
  		v = null;
  	}*/
    //by rk
    if(sampleModel.size() >= chatLimit){
    	sampleModel.removeRange(0, chatLimit/2);
    	int ind = sampleModel.indexOf(HAND_SEPARATOR+"\n", 0);
    	if(ind != -1 && ind != 0){
    		sampleModel.removeRange(0, ind);
    		scrollToListBottom();
    	}
    }
  }

  /**
   *
   */
  public void leavePlease() {
    _cat.finest("leavePlease()");
    stopTimers();
  }

  public void setClientPokerController(ClientPokerController controller) {
    this.clientPokerController = controller;
  }

  public void updateRebuyChipsStatus() {
    _rebuy_chips = false;
  }
  
  public void updatePlayerState(long state) {
	_cat.fine("updatePlayerState " + PlayerStatus.stringValue(state));
    if (clientPokerController == null) {
      return;
    }
    if (PlayerStatus.isNew(state) && !doSitin && !clientPokerController._clientRoom.getGameType().isTPoker())
    {
    	sitInOut.setText(bundle.getString("sitout"));
		sitInOut.setVisible(true);
		quickFoldVisible(false);
		disibleChecks();
	}
    else if(clientPokerController.getMyPlayer() != null && PlayerStatus.isActive(state))
    {
    	sitInOut.setVisible(false);
    	sitOutNextHandCB.setEnabled(true);
		if(!clientPokerController._model.gameType.isTPoker())
		{
			if(!clientPokerController._model.gameType.isTourny())
			{
				sitOutNextHandCB.setVisible(true);
				autoPostBlindCB.setVisible(true);
			}
    		
		}
    	//labelSitOutNext.setEnabled(true);
    }
    if((state & PlayerStatus.SITTINGOUT) > 0 && doSitin)
    {
		sitInOut.setText(bundle.getString("sitin"));
		sitInOut.setVisible(true);
		sitOutNextHandCB.setEnabled(false);
		quickFoldVisible(false);
		disibleChecks();
    }
    //normalizePanel();
    SoundManager.loopTest();
    //to avoid stopping timer in case of leaving the player in TPOker games
    if(clientPokerController!=null && clientPokerController._model!= null 
    		&& !clientPokerController._model.gameType.isTPoker())stopTimers();
  }

  public void leaveIfNeeded() {
    if (leftButtonLeave.getModel().isSelected()) {
      clientPokerController.tryExit();
    }
  }

  public void checkTournamentButtons(boolean isTournament,
                                     boolean isTournamentRun) {
    if (!isTournament) {
      return;
    }
    if (isTournamentRun) {
      _cat.fine("  isTournamentRun ----!");
      if (sitInOut.isVisible()) {
    	  sitInOut.setVisible(false);
      }
      
    }
  }

  protected void leftButtonCasherSetEnabled(boolean state) {
    if (clientPokerController == null) {
      leftButtonCashier.setEnabled(state);
      return;
    }

  }

  public boolean haveToSitOut() {
    return (
        ( (MyJToggleButton) leftButtonSit).getText().equals(
        bundle.getString("sitout"))
        && leftButtonSit.getModel().isSelected());
  }

  public boolean haveToLeave() {
    return (leftButtonLeave.getModel().isSelected());
  }

  protected MyJButton createMoveButton(String label) {
    try {
      MyJButton button =
          new MyJButton(
          label,
          skin.getMoveButton(),
          skin.getMoveButton(),
          //Utils.getIcon(ClientConfig.IMG_BUTTON_EN),
          //Utils.getIcon(ClientConfig.IMG_BUTTON_EN),
          null,
          skin.getMoveButton()
          //Utils.getIcon(ClientConfig.IMG_BUTTON_EN)
          );
      	button.addKeyListener(this);
     
      return button;
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "BottonPanel.createButton", e);
    }
    return null;
  }

  private JButton createSpeakButton(ImageIcon icon1) {
    JButton button = new JButton(icon1);
    int w = icon1.getIconWidth();
    int h = icon1.getIconHeight();
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setSize(w, h);
    button.setForeground(new Color(0xFFFFFF));
    return button;
  }
  
  protected MyJButton createNormalButton(String label) {
    try {
      MyJButton button =
          new MyJButton(
          label,
          Utils.getIcon(ClientConfig.IMG_BUTTON_EN),  //def icon
          Utils.getIcon(ClientConfig.IMG_BUTTON_PR), //press icon
          Utils.getIcon(ClientConfig.IMG_BUTTON_OV), // roll icon
          Utils.getIcon(ClientConfig.IMG_BUTTON_DE)); //dis icon
      button.addKeyListener(this);
      return button;
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "BottonPanel.createButton", e);
    }
    return null;
  }
 
  protected JToggleButton createToggleButton(String label) {
    try {
      JToggleButton button =
          new MyJToggleButton(
          label,
          Utils.getIcon(ClientConfig.IMG_BUTTON_EN),
          Utils.getIcon(ClientConfig.IMG_BUTTON_PR),
          Utils.getIcon(ClientConfig.IMG_BUTTON_OV), // roll icon
          Utils.getIcon(ClientConfig.IMG_BUTTON_DE));
      button.addKeyListener(this);
      return button;
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "BottonPanel.createToggleButton", e);
    }
    return null;
  }

  private JCheckBox createCheckBox(String label) {
    try {
      JCheckBox checkBox = //new JCheckBox();
          new MyJCheckBox(
          label,
          skin.getCheckBoxEn(),
          skin.getCheckBoxDe()
          /*Utils.getIcon(ClientConfig.IMG_CHECK_EN),
          Utils.getIcon(ClientConfig.IMG_CHECK_DE)*/);
      checkBox.addKeyListener(this);
      checkBox.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
      checkBox.setBorderPaintedFlat(false);
      checkBox.setBorderPainted(true);
      checkBox.setBorder(null);
      checkBox.setOpaque(false);
      checkBox.setSize(20, 17);
      checkBox.setFocusPainted(false);
      checkBox.setContentAreaFilled(false);
      checkBox.setMargin(null);
      checkBox.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          JCheckBox box = (JCheckBox) e.getSource();
          if (box.isSelected()) {

          }
          else {

          }
        }
      });
      return checkBox;
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "BottonPanel.createToggleButton", e);
    }
    return null;
  }

  private static JLabel createLabel(String boundleLabel) {
    ResourceBundle bundle = Bundle.getBundle();
    String text = bundle.getString(boundleLabel);
    JLabel label = new JLabel(text);
    if (text.equalsIgnoreCase(bundle.getString("sit.your.player"))) {
      label.setForeground(Color.YELLOW);
    }
    else {
      label.setForeground(new Color(196, 196, 196));
    }
    return label;
  }

  private static JLabel createNonBoundleLabel() {
    JLabel label = new JLabel();
    label.setForeground(new Color(196, 196, 196));
    return label;
  }

  private static JLabel createInfoLabel(String boundleLabel) {
    JLabel label =
        new JLabel(
        getInfoLabel(Bundle.getBundle().getString(boundleLabel)));
    label.setForeground(new Color(196, 196, 196));
    return label;
  }

  private void createButtons() {
    speakButton = createSpeakButton(Utils.getIcon(ClientConfig.IMG_CHATBUTTON_BG));
    
    autoPostBlindCB = createCheckBox(bundle.getString("auto.post.blind"));
    autoPostBlindCB.setSelected(_serverProxy._settings.isAutoPostBlind());
    muckCardsCB = createCheckBox(bundle.getString("muck.cards"));;
    muckCardsCB.setSelected(_serverProxy._settings.isMuckLosingCards());
    foldToAnyBetCB = createCheckBox(bundle.getString("fold.to.any.bet"));
    sitOutNextHandCB = createCheckBox(bundle.getString("sitout.in.nexthand"));
    sitOutNextHandCB.setEnabled(false);
    autoPostBlindCB.setVisible(false);
    sitOutNextHandCB.setVisible(false);
    
    quickFold = createMoveButton("Quick Fold");
    quickFold.setName(bundle.getString("quick.fold"));
    quickFold.setVisible(false);
    
    sitInOut = createToggleButton(Bundle.getBundle().getString("sitout"));
    sitInOut.setHorizontalTextPosition(AbstractButton.CENTER);
    sitInOut.setVerticalAlignment(AbstractButton.CENTER);
    sitInOut.setVisible(false);
    
    leftButtonSit =
        createToggleButton(Bundle.getBundle().getString("sitin"));
    leftButtonLeave =
        createToggleButton(Bundle.getBundle().getString("leave"));
    leftButtonCashier =
        createNormalButton(Bundle.getBundle().getString("buychips"));
    leftButtonCashier.setFont(Utils.boldFont);

    rightButtonText =
        createToggleButton(
        Bundle.getBundle().getString("text").toUpperCase());
    rightButtonChat =
        createToggleButton(
        Bundle.getBundle().getString("chat").toUpperCase());
    rightButtonBoth =
        createToggleButton(
        Bundle.getBundle().getString("both").toUpperCase());
    muteButton =
        new MyJToggleButton(
        "",
        Utils.getIcon(ClientConfig.IMG_BUTTON_MUTE_ON),
        Utils.getIcon(ClientConfig.IMG_BUTTON_MUTE_OFF),
        null,
        null);
    checkScrollList = createCheckBox(Bundle.getBundle().getString("auto.scroll"));
    checkScrollList.setOpaque(false);

    checkCheckFold = createCheckBox(Bundle.getBundle().getString("check.fold"));
    checkFold = createCheckBox(Bundle.getBundle().getString("fold"));
    checkCheckCall = createCheckBox(Bundle.getBundle().getString("check.call"));
    checkCheckCallAny = createCheckBox(
        Bundle.getBundle().getString("check.call")
        //+ " "
        //+ Bundle.getBundle().getString("any")
        );
    checkRaise = createCheckBox(Bundle.getBundle().getString("raise"));
    checkRaiseAny = createCheckBox(
        Bundle.getBundle().getString("raise")
        + " "
        + Bundle.getBundle().getString("any"));

    /**for (int i = 0; i < buttons.length; i++) {
      buttons[i] = createButton1("");
    }**/
    sampleModel = new DefaultListModel();
    textArea = new MyJList(sampleModel);
    textArea.setVisibleRowCount(6);
    textArea.setCellRenderer(new MyCellRenderer());
    textArea.setFocusable(true);
    textArea.setOpaque(false);
    textArea.setBorder(null);
  }

  protected void updateLookAndFeel() {
    try {
    	
    	UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
    	UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
    }
    catch (Exception ex) {
      _cat.log(Level.WARNING, "Failed loading L&F: (BottonPanel)", ex);
    }
  }

  protected void userSendChat(int playerNo) {
    if (playerNo >= 0) {
    	StringBuilder sb = new StringBuilder();
    	 tableProxySendToServer(clientPokerController._model.name,
                 new ChatAction(
						playerNo,
						sb.append(_serverProxy._name).append(": ").append(chatField.getText()).toString()));
//      tableProxySendToServer(clientPokerController._model.name,
//                             new ChatAction(
//          playerNo,
//         _serverProxy._name
//          + ": "
//          + chatField.getText()));
    }
  }

  class chatAdapter
      implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object o = e.getSource();
      if (! (o instanceof JButton)) {
        return;
      }
      JButton button = (JButton) o;
      if (button == speakButton && chatField.getText().length() > 0) {
        int playerNo = clientPokerController.getMyPlayerNo();
        userSendChat(playerNo);
        chatField.setText("");
        //button.requestFocusInWindow();
      }
    }
  }

  /*class MySliderUI
      extends BasicSliderUI {
	//private Color firstColor = new Color(106, 64, 44);
    private Color firstColor = new Color(153, 102, 51);
    private Color secondColor = new Color(87, 44, 26);
    private Color lineColor = new Color(124, 126, 124);
    public MySliderUI(JSlider b) {
    	
      super(b);
      //		b.setForeground(Color.blue);
      //b.setBackground(firstColor);
      b.setOpaque(false);
    }

    
      public Color getFillColorAt(int index) {
        return null;
      }
      public Color getTrackFillColor() {
        return null;
      }
    
    public void paintThumb(Graphics g) {
    	Dimension d = getSize();
	    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
	    Rectangle thumb = thumbRect;
	    g.drawImage(icon.getImage(),thumb.x,thumb.y, 8, 8, null);
	    //g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
	    setOpaque( false );
	    
	}
    public void paintTrack(Graphics g) {
    	Dimension d = getSize();
	    ImageIcon icon = Utils.getIcon(ClientConfig.IMG_SLIDER_LINE);
	    Rectangle thumb = thumbRect;
	    g.drawImage(icon.getImage(),thumb.x + 3, thumb.y + 9, 90, 3, null);
	    //g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
	    setOpaque( false );
    }

  }*/

  public void keyTyped(KeyEvent e) {
	  //System.out.println("key typed");
    char ch = e.getKeyChar();
    /*if (ch == '1' && buttons[0].isVisible()) {
      buttons[0].doClick();
    }
    else if (ch == '2' && buttons[1].isVisible()) {
      buttons[1].doClick();
    }
    else if (ch == '3' && buttons[2].isVisible()) {
      buttons[2].doClick();
    }
    else if (ch == '4' && buttons[3].isVisible()) {
      buttons[3].doClick();
    }*/
//    if (!buttons[0].isVisible()) {
//      //chatField.requestFocusInWindow();
//      chatField.setForeground(Color.yellow);
//      chatField.setText(chatField.getText() + ch);
//      chatField.setForeground(Color.white);
//    }
  }

  public void keyPressed(KeyEvent e) {
	//int keyValue = e.getKeyCode();
//	    if(e.getKeyCode() == KeyEvent.VK_F1){
//	  	  for (int i = 0; i < buttons.length; i++) {
//	      	String name = buttons[i].getName();
//	      	if (buttons[i].isVisible() && bundle.getString("fold").equals(name)){
//	      		buttons[i].doClick();
//	      		//appendLog(MessageFormat.format(bundle.getString("do.fold"), new Object[] {_serverProxy._name}));
//	      		break;
//	      	}
//			}
//	    }else if(e.getKeyCode() == KeyEvent.VK_F2){
//	  	  for (int i = 0; i < buttons.length; i++) {
//	      	String name = buttons[i].getName();
//	      	if (buttons[i].isVisible() && bundle.getString("check").equals(name)){
//	      		buttons[i].doClick();
//	      		break;
//	      	}else if (buttons[i].isVisible() && bundle.getString("call").equals(name)){
//	      		buttons[i].doClick();
//	      		break;
//	      	}
//			}
//	    }else if(e.getKeyCode() == KeyEvent.VK_F3){
//	  	  for (int i = 0; i < buttons.length; i++) {
//	      	String name = buttons[i].getName();
//	      	if (buttons[i].isVisible() && bundle.getString("bet").equals(name)){
//	      		buttons[i].doClick();
//	      		break;
//	      	}
//	      	if (buttons[i].isVisible() && bundle.getString("raise").equals(name)){
//					buttons[i].doClick();
//					break;
//	      	}
//			  }
//	    }else if(e.getKeyCode() == KeyEvent.VK_F4){
//	  	  for (int i = 0; i < buttons.length; i++) {
//	      	String name = buttons[i].getName();
//	      	if (buttons[i].isVisible() && bundle.getString("allin").equals(name)){
//	      		buttons[i].doClick();
//	      		break;
//	      	}
//			  }
//	    }
  }

  public void keyReleased(KeyEvent e) {
	  //System.out.println("key released");
  }

  private void setInfoLabelText(String text) {
	if(clientPokerController._model.gameType.isTourny())return;
	infoLabel.setText(text);
    if (text.equalsIgnoreCase(bundle.getString("sit.your.player"))) {
      infoLabel.setForeground(Color.YELLOW);
    }
    else {
      infoLabel.setForeground(new Color(196, 196, 196));
    }
  }

  /**
   * Statistics  REQUEST
   * 
   */
  public void openStats(Statistics _sts)
  {
	  StringBuilder sb = new StringBuilder().append("<html>").append("Statistics For 7 Hands");
   
	  MyStatsDialog d = MessageFactory.getStatsWindow(skin,
			  (clientPokerController._model.gameType.isTourny()?"Stats For Ring Games":""),
	            sb.toString(),
	            clientPokerController._clientRoom.getClientRoomFrame(),
	            clientPokerController.
	            _clientRoom.getLobbyServer(),
	            _sts
	            
	            );
//	  d.setLocationRelativeTo(clientPokerController._clientRoom.getClientRoomFrame());
//      d.setVisible(true);
	  clientPokerController._clientRoom.getClientRoomFrame().getLayeredPane().add(d, JLayeredPane.POPUP_LAYER);	  
  }
  
  
  
  
  /**
   * BUY IN MONEY REQUEST
   * This is called when player wants to bring in more money to game
   */
  public boolean buyInRequest(double buyin, double amtAtTable, double worth, 
                                  double moneyMin,  double moneyMax, double fee,
                                  boolean tournamentGame, boolean limitGame,
                                  String choice, String gameName) {
    _cat.finest("Before buy in.....");
    _cat.finest("worth = " + worth + 
               "; moneyMin = " +
               moneyMin + "; moneyMax = " + moneyMax + "; fee = " + fee +
               "; tournamentGame = " + tournamentGame);
    JFrame f = (JFrame)clientPokerController._clientRoom.getClientRoomFrame();
	if (amtAtTable >= moneyMax ) {
      setInfoLabelText("You already have enough money in on the table");
      		JOptionPane.showInternalMessageDialog(f.getContentPane(),
          "You already have enough money in on the table", "INFO",
                        JOptionPane.INFORMATION_MESSAGE);
      		return false;
    }
	if(_rebuy_chips)
	{
		JOptionPane.showInternalMessageDialog(f.getContentPane(),
		          "You rebuy chips request is under processing", "INFO",
		                        JOptionPane.INFORMATION_MESSAGE);
		return false;
	}
    if (MessageFactory.dialog != null) {
    	MessageFactory.dialog.setLocationRelativeTo(clientPokerController._clientRoom.getClientRoomFrame());
      MessageFactory.dialog.toFront();
      return false;
    }
    if (tournamentGame) { // if tournament no need to send a specific buy in command
        // as server will do a buy in automatically before allowing the player to sit
        //_serverProxy.buyChips(playchips, realChips); // depedning on the type of game server will buy real
        // or play chips
        return true;
      }
    /** @todo replace the buyin chips condition with 10 times max_bet..etc */
    else  {
      if ( (amtAtTable < moneyMax)) {
        StringBuilder sb = new StringBuilder().append("<html>").append(bundle.
            getString("how.much.money"));
        sb.append(" ").append(SharedConstants.chipToMoneyString(worth)).
            append(
            " ");
        sb.append(bundle.getString("you.want.use")).append("?<br>").append(
            bundle.
            getString("min.buyin"));
        sb.append(" ").append(SharedConstants.chipToMoneyString(moneyMin));
        if (moneyMax > 0 && !limitGame) {
          sb.append("<br>").append(bundle.getString("max.buyin")).append(" ").
              append(SharedConstants.chipToMoneyString(moneyMax));
        }
        else {
          sb.append("<br>").append(bundle.getString("max.buyin")).append(" ").
              append(SharedConstants.chipToMoneyString(worth));
        }

        sb.append("</html>");
        choice = (String) JOptionPane.showInternalInputDialog(f.getContentPane(), sb.toString(),
        		bundle.getString("money.request"),1,null,null,
        		SharedConstants.decimal2digits((moneyMax > 0 && worth > moneyMax) ? (moneyMax-amtAtTable) :worth));
        /*choice = MessageFactory.getStringWindowOne(skin,
            bundle.getString("money.request"),
            sb.toString(),
            Double.toString(
            (moneyMax > ! 0 &&
             worth > moneyMax) ? (moneyMax-amtAtTable) :
            worth),
            f,
            clientPokerController.
            _clientRoom.getLobbyServer()
            );*/
        //_rebuy_chips = false;
      }
    }
    double value = 0;
    if (choice == null) {
      _cat.finest("Choice is null ... ");
      return false;
    }
    try {
      value = (Double.parseDouble(choice));
      _cat.finest("value = " + value);
      if (value < moneyMin) {
        _cat.finest("<html>" + bundle.getString("too.little") +  "</html>");
        setInfoLabelText("<html>" + bundle.getString("too.little") + "</html>");
        return false;
      }
      if (value > worth) {
        _cat.finest("<html>" +   bundle.getString("you.cant.afford")
                   + ": "   + SharedConstants.chipToMoneyString(worth) +   "</html>");

        setInfoLabelText("<html>" + bundle.getString("you.cant.afford")
                         + ": "   + SharedConstants.chipToMoneyString(worth) +    "</html>");
        JOptionPane.showInternalMessageDialog(f.getContentPane(),
  			  bundle.getString("you.cant.afford"), "INFO",
  	                        JOptionPane.INFORMATION_MESSAGE);
        return false;
      }
      if ( (moneyMax + fee) > /*!*/ 0 && value > (moneyMax + fee)) {
        _cat.finest(
            bundle.getString("you.cant.afford.max")
            + ": " + SharedConstants.chipToMoneyString(moneyMax));
        setInfoLabelText(
            bundle.getString("you.cant.afford.max")
            + ": " + SharedConstants.chipToMoneyString(moneyMax));
        JOptionPane.showInternalMessageDialog(f.getContentPane(),
  			  bundle.getString("you.cant.afford.max"), "INFO",
  	                        JOptionPane.INFORMATION_MESSAGE);
        return false;
      }
      if(amtAtTable + value > moneyMax)
      {
    	  JOptionPane.showInternalMessageDialog(f.getContentPane(),
    			  bundle.getString("you.cant.afford.max"), "INFO",
    	                        JOptionPane.INFORMATION_MESSAGE);
    	  return false;
      }
      if(amtAtTable > 0)
      {
    	  JOptionPane.showInternalMessageDialog(f.getContentPane(),
    			  bundle.getString("rebuy.info"), "INFO",
    	                        JOptionPane.INFORMATION_MESSAGE);
      }
      //if (!tournamentGame) { // if tournament no need to send a specific buy in command
       if(!_rebuy_chips){
    	   _serverProxy.getMoneyIntoGame(gameName,value);
    	   _rebuy_chips = true;
       }
      //}
    }
    catch (NumberFormatException nfe) {
      setInfoLabelText(bundle.getString("invalid.number"));
      _cat.log(Level.WARNING, "Invalid number ", nfe);
      return false;
      //cardLayout.show(cardDisplayPanel, CARD_INFO);
    }
//    cardShowInfo();
//    chatPanelSetVisible(true);
    return true;
  }
  /**
   * MONEY REQUEST
   * This is called when player wants to join a game
   */
  private boolean newMoneyRequest(int place, double amtAtTable, double worth, 
                                  double moneyMin,  double moneyMax, double fee,
                                  boolean tournamentGame, boolean limitGame,
                                  String choice) {
    _cat.finest("Before buy in.....");
    _cat.finest("place = " + place + "; worth = " + worth + 
               "; moneyMin = " +
               moneyMin + "; moneyMax = " + moneyMax + "; fee = " + fee +
               "; tournamentGame = " + tournamentGame);
    if (amtAtTable >= moneyMax) {
      setInfoLabelText("You already have enough money in on the table");
    }
    if (MessageFactory.dialog != null) {
      MessageFactory.dialog.toFront();
      return false;
    }
    if (tournamentGame) { // if tournament no need to send a specific buy in command
        // as server will do a buy in automatically before allowing the player to sit
        try {
			_serverProxy.joinTable(clientPokerController._model.name,  place, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // depedning on the type of game server will buy real
        // or play chips
        return true;
      }
    /** @todo replace the buyin chips condition with 10 times max_bet..etc */
    else  {
      if ( (amtAtTable < moneyMin)) {
        StringBuilder sb = new StringBuilder().append("<html>").append(bundle.
            getString("how.much.money"));
        sb.append(" ").append(SharedConstants.chipToMoneyString(worth)).
            append(
            " ");
        sb.append(bundle.getString("you.want.use")).append("?<br>").append(
            bundle.
            getString("min.buyin"));
        sb.append(" ").append(SharedConstants.chipToMoneyString(moneyMin));
        if (moneyMax > 0 && !limitGame) {
          sb.append("<br>").append(bundle.getString("max.buyin")).append(" ").
              append(SharedConstants.chipToMoneyString(moneyMax));
        }
        else {
          sb.append("<br>").append(bundle.getString("max.buyin")).append(" ").
              append(SharedConstants.chipToMoneyString(worth));
        }

        sb.append("</html>");
        JFrame f = (JFrame)clientPokerController._clientRoom.getClientRoomFrame();
		
        choice = (String) JOptionPane.showInternalInputDialog(f.getContentPane(), sb.toString(),
        		bundle.getString("money.request"),1,null,null,
        		SharedConstants.decimal2digits((moneyMin > 0 && worth > moneyMin * 3) ? moneyMin * 3 :worth));
        /*choice = MessageFactory.getStringWindowOne(skin,
            bundle.getString(
            "money.request"),
            sb.toString(),
            //    		Min. buy-in   Max. buy-in           By default offered buy-in 
        	//PL:    20BB           100BB                       60BB 
        	//FL:    20BB       Whole user's amount             60BB 
        	//NL:    20BB           100BB                       60BB 
            // moneyMin is already 20BB so to get 60BB moneyMin*3
            Double.toString(
            (moneyMin > 0 &&
             worth > moneyMin * 3) ? moneyMin * 3 :
            worth),
            clientPokerController._clientRoom.getClientRoomFrame(),
            clientPokerController.
            _clientRoom.getLobbyServer()
            );*/
      }
    }
    double value = 0;
    if (choice == null) {
      _cat.finest("Choice is null ... ");
      return false;
    }
    try {
      value = (Double.parseDouble(choice));
      _cat.finest("value = " + value);
      if (value < moneyMin) {
        _cat.finest("<html>" + bundle.getString("too.little") +  "</html>");
        setInfoLabelText("<html>" + bundle.getString("too.little") + "</html>");
        return false;
      }
      if (value > worth) {
        _cat.finest("<html>" +   bundle.getString("you.cant.afford")
                   + ": "   + SharedConstants.chipToMoneyString(worth) +   "</html>");

        setInfoLabelText("<html>" + bundle.getString("you.cant.afford")
                         + ": "   + SharedConstants.chipToMoneyString(worth) +    "</html>");
        return false;
      }
      if ( (moneyMax + fee) > /*!*/ 0 && value > (moneyMax + fee)) {
        _cat.finest(
            bundle.getString("you.cant.afford.max")
            + ": " + SharedConstants.chipToMoneyString(moneyMax));
        setInfoLabelText(
            bundle.getString("you.cant.afford.max")
            + ": " + SharedConstants.chipToMoneyString(moneyMax));
        return false;
      }
      if (!tournamentGame) { // if tournament no need to send a specific buy in command
        // as server will do a buy in automatically before allowing the player to sit
        try {
			_serverProxy.joinTable(clientPokerController._model.name,  place, value); // depedning on the type of game server will buy real
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // or play chips
      }
    }
    catch (NumberFormatException nfe) {
      setInfoLabelText(bundle.getString("invalid.number"));
      _cat.log(Level.WARNING, "Invalid number ", nfe);
      return false;
      //cardLayout.show(cardDisplayPanel, CARD_INFO);
    }
    //cardShowInfo();
    //chatPanelSetVisible(true);
    return true;
  }

  
  private class MyJList extends JList {

	  int itemIndexUnderMouse;

	 
	  public MyJList(DefaultListModel sampleModel) {
		// TODO Auto-generated constructor stub
		  super(sampleModel);
		  itemIndexUnderMouse = -1;
		  addMouseMotionListener(new MouseAdapter() {
			  @Override
			  public void mouseMoved(MouseEvent e) 
			  {
				  Point point = indexToLocation(0);
				  int index = locationToIndex(e.getPoint());
				  if (index != itemIndexUnderMouse ||
					  itemIndexUnderMouse == -1) {
					  try {
						updateToolTip(index);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				  }
			  }
			  
		  });
	}
	private void updateToolTip(int index) 
	{
		if(index == -1)return;
		String newToolTip = (String) (getModel().getElementAt(index));
		setToolTipText(newToolTip);
	}
  }
  
  public class MyCellRenderer extends DefaultListCellRenderer  
  {  
    public Component getListCellRendererComponent(JList list,Object value,  
                        int index,boolean isSelected,boolean cellHasFocus)  
    {  
      JLabel lbl = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);  
      String str = value.toString();
      if(str.length() < 35)
      {
  		setText(str);
  		lbl.setPreferredSize(new Dimension(165,15));
	  }
      else
	  {
    	  int row_cnt = 1;
    	  int row_width = 225;
    	  FontMetrics fm = lbl.getFontMetrics(lbl.getFont());
    	  StringBuilder trial = new StringBuilder();
    	  StringBuilder real = new StringBuilder("<html>");
    	  BreakIterator boundary = BreakIterator.getWordInstance();
    	  boundary.setText(str);
    	  int start = boundary.first();
    	  for (int end = boundary.next(); end != BreakIterator.DONE;
			start = end, end = boundary.next()) {
			String word = str.substring(start,end);
			trial.append(word);
			int trialWidth = SwingUtilities.computeStringWidth(fm,
				trial.toString());
			if (trialWidth > row_width) {
				trial = new StringBuilder(word);
				real.append("<br>");
				row_cnt++;
			}
			real.append(word);
    	  }
    	  real.append("</html>");
	  	  lbl.setText(real.toString());
	  	  lbl.setPreferredSize(new Dimension(row_width, row_cnt * 15));
	  }
  	if (index % 2 == 0) setBackground(new Color(231,231,231));
    else setBackground(Color.WHITE);
    setForeground(Color.BLACK);
    
    if(lbl.getText().contains(bundle.getString("wins"))&&lbl.getText().contains("Dlr: ")){
    	setForeground(Color.BLACK);
    	lbl.setFont(new Font("Serif", Font.BOLD, 12));
    } else if(lbl.getText().contains("Dlr: ")){
    	lbl.setFont(new Font("Serif", Font.PLAIN, 12));
    }else if(lbl.getText().equals((HAND_SEPARATOR)+"\n")){
    	lbl.setForeground(new Color(52,130,0));
    }else{
    	lbl.setForeground(Color.BLUE);
    }
   
//    else if(lbl.getText().contains(_serverProxy._name+": ")){
//    	lbl.setForeground(Color.BLUE);
//    }
//    else{
//    	lbl.setFont(new Font("Serif", Font.PLAIN, 12));
//    }
    return lbl;  
    }  
  }
  
  protected void tableProxySendToServer(String tid, Object o) {
    _serverProxy.sendToServer(tid, o);
    _cat.fine("Send response to server " + o.getClass().getName() + ", " + o);
  }
  //this method is used when table is created from lobby
  public void addButtonPanel(){
	  cardDisplayPanel.setBounds((int)(0),(int)(0),(int)(380),(int)(50));
		 centerPanel.setBounds((int)(skin.roomWidth - cardDisplayPanel.getWidth()), (int)((150-45)*ratioY),//150 is bottom panel visible height
								(int)(380), (int)(60));
  }
  
  public void resize(RoomSkin skin){
	  //new Exception("resize bottompannel").printStackTrace();
	  clientRoomToFront();
	//ListModel model=  textArea.getModel();
	 removeLeftPanel();
	 this.skin=skin;
	 this.ratioX = skin._ratio_x;
	 this.ratioY = skin._ratio_y;
	 //intPanel.setBounds(skin.getIntPanel());
	 //cardDisplayPanel.setBorder(BorderFactory.createLineBorder((Color.GREEN)));
	 cardDisplayPanel.setBounds((int)(0),(int)(0),(int)(380),(int)(50));
	 centerPanel.setBounds((int)(skin.roomWidth - cardDisplayPanel.getWidth()), (int)((150-45)*ratioY),//150 is bottom panel visible height
							(int)(380), (int)(60));//380 is buttonPanelWidth + 10px, 60 is buttonPanelHeight
	 //centerPanel.setBorder(BorderFactory.createLineBorder((Color.BLUE)));
	    
	 try {
		 addLeftPanel();
		 if(showBetPanel && enableBetSlider)
		    	setSliderVisible(true);
		//panelInit();
		//textArea.setModel(model);
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
  public void removeLeftPanel(){
	  //this.remove(chatFieldPanel);
	  this.remove(muckCardsCB);
	  this.remove(autoPostBlindCB);
	  this.remove(sitOutNextHandCB);
	  this.remove(foldToAnyBetCB);
	  //this.remove(chatTextPane);
	  this.remove(checks);
	  //this.remove(sliderPanel);
	  sliderPanel.remove(slider_bg);
  }
  
  public void addLeftPanel(){
	  //panel.setBounds(skin.getPanel());
	  //panel.move(skin.getPanel().x, skin.getPanel().y);
	  chatFieldPanel.setBounds(new Rectangle((int)(4*ratioX),(int)(129*ratioY),(int)(206*ratioX),(int)(18*ratioY)));
	  muckCardsCB.setBounds(new Rectangle((int)(214*ratioX),(int)(127*ratioY),(int)(105*ratioX),(int)(17*ratioY)));
	 if(ratioX > 1 || ratioY > 1){
	 muckCardsCB.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, (int)(11*ratioX)));
	  muckCardsCB.setIcon(skin.getCheckBoxEn());
	  muckCardsCB.setSelectedIcon(skin.getCheckBoxDe());
	  //muckCardsCB.setDisabledIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
	  //muckCardsCB.setDisabledSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
	  //muckCardsCB.setRolloverSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE)));
	  muckCardsCB.setPressedIcon(skin.getCheckBoxDe());
	  if(muckCardsCB.isSelected())
		  muckCardsCB.setRolloverIcon(skin.getCheckBoxDe());
	  else
		  muckCardsCB.setRolloverIcon(skin.getCheckBoxEn());
	  }else{
		  muckCardsCB.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
		  muckCardsCB.setIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  muckCardsCB.setSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  //muckCardsCB.setDisabledIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //muckCardsCB.setDisabledSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //muckCardsCB.setRolloverSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  muckCardsCB.setPressedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  if(muckCardsCB.isSelected())
			  muckCardsCB.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  else
			  muckCardsCB.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
	  }
	  autoPostBlindCB.setBounds(new Rectangle((int)(214*ratioX),(int)(93*ratioY),(int)(105*ratioX),(int)(17*ratioY)));
	  sitOutNextHandCB.setBounds(new Rectangle((int)(214*ratioX),(int)(77*ratioY),(int)(105*ratioX),(int)(17*ratioY)));
	  
	  foldToAnyBetCB.setBounds(new Rectangle((int)(214*ratioX),(int)(109*ratioY),(int)(115*ratioX),(int)(17*ratioY)));
	  if(ratioX > 1 || ratioY > 1){
		  foldToAnyBetCB.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, (int)(11*ratioX)));
		  foldToAnyBetCB.setIcon(skin.getCheckBoxEn());
		  foldToAnyBetCB.setSelectedIcon(skin.getCheckBoxDe());
		  //foldToAnyBetCB.setDisabledIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //foldToAnyBetCB.setDisabledSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //foldToAnyBetCB.setRolloverSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE)));
		  foldToAnyBetCB.setPressedIcon(skin.getCheckBoxDe());
		  if(foldToAnyBetCB.isSelected())
			  foldToAnyBetCB.setRolloverIcon(skin.getCheckBoxDe());
		  else
			  foldToAnyBetCB.setRolloverIcon(skin.getCheckBoxEn());
	  }else{
		  foldToAnyBetCB.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
		  foldToAnyBetCB.setIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  foldToAnyBetCB.setSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  //foldToAnyBetCB.setDisabledIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //foldToAnyBetCB.setDisabledSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //foldToAnyBetCB.setRolloverSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  foldToAnyBetCB.setPressedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  if(foldToAnyBetCB.isSelected())
			  foldToAnyBetCB.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  else
			  foldToAnyBetCB.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
	  }
	  chatTextPane.setBounds(new Rectangle((int)(3*ratioX),(int)(49*ratioY),(int)(210*ratioX),(int)(78*ratioY)));
	  //quickFold.setBounds(new Rectangle((int)(323*ratioX),(int)(100*ratioY),(int)(120*ratioX),(int)(35*ratioY)));
	  quickFold.setBounds(new Rectangle((int)(323*ratioX),(int)(100*ratioY),(int)(120),(int)(35)));
	  sitInOut.setBounds(new Rectangle((int)(680*ratioX),(int)(100*ratioY),(int)(120*ratioX),(int)(35*ratioY)));
	  if(ratioX > 1 || ratioY > 1){
		  checkCheckFold.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, (int)(11*ratioX)));
		  checkCheckFold.setIcon(skin.getCheckBoxEn());
		  checkCheckFold.setSelectedIcon(skin.getCheckBoxDe());
		  //checkCheckFold.setDisabledIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkCheckFold.setDisabledSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkCheckFold.setRolloverSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE)));
		  checkCheckFold.setPressedIcon(skin.getCheckBoxDe());
		  if(checkCheckFold.isSelected())
			  checkCheckFold.setRolloverIcon(skin.getCheckBoxDe());
		  else
			  checkCheckFold.setRolloverIcon(skin.getCheckBoxEn());
		  
		  checkCheckCallAny.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, (int)(11*ratioX)));
		  checkCheckCallAny.setIcon(skin.getCheckBoxEn());
		  checkCheckCallAny.setSelectedIcon(skin.getCheckBoxDe());
		  //checkCheckCallAny.setDisabledIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkCheckCallAny.setDisabledSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkCheckCallAny.setRolloverSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE)));
		  checkCheckCallAny.setPressedIcon(skin.getCheckBoxDe());
		  if(checkCheckCallAny.isSelected())
			  checkCheckCallAny.setRolloverIcon(skin.getCheckBoxDe());
		  else
			  checkCheckCallAny.setRolloverIcon(skin.getCheckBoxEn());
	  
		  checkRaise.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, (int)(11*ratioX)));
		  checkRaise.setIcon(skin.getCheckBoxEn());
		  checkRaise.setSelectedIcon(skin.getCheckBoxDe());
		  //checkRaise.setDisabledIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkRaise.setDisabledSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkRaise.setRolloverSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE)));
		  checkRaise.setPressedIcon(skin.getCheckBoxDe());
		  if(checkRaise.isSelected())
			  checkRaise.setRolloverIcon(skin.getCheckBoxDe());
		  else
			  checkRaise.setRolloverIcon(skin.getCheckBoxEn());
	  
		  checkRaiseAny.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, (int)(11*ratioX)));
		  checkRaiseAny.setIcon(skin.getCheckBoxEn());
		  checkRaiseAny.setSelectedIcon(skin.getCheckBoxDe());
		  //checkRaiseAny.setDisabledIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkRaiseAny.setDisabledSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_EN)));
		  //checkRaiseAny.setRolloverSelectedIcon(skin.getScaledImage(Utils.getIcon(ClientConfig.IMG_CHECK_DE)));
		  checkRaiseAny.setPressedIcon(skin.getCheckBoxDe());
		  if(checkRaiseAny.isSelected())
			  checkRaiseAny.setRolloverIcon(skin.getCheckBoxDe());
		  else
			  checkRaiseAny.setRolloverIcon(skin.getCheckBoxEn());
	  }else{
		  checkCheckFold.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
		  checkCheckFold.setIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  checkCheckFold.setSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  //checkCheckFold.setDisabledIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkCheckFold.setDisabledSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkCheckFold.setRolloverSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  checkCheckFold.setPressedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  if(checkCheckFold.isSelected())
			  checkCheckFold.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  else
			  checkCheckFold.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  
		  checkCheckCallAny.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
		  checkCheckCallAny.setIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  checkCheckCallAny.setSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  //checkCheckCallAny.setDisabledIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkCheckCallAny.setDisabledSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkCheckCallAny.setRolloverSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  checkCheckCallAny.setPressedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  if(checkCheckCallAny.isSelected())
			  checkCheckCallAny.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  else
			  checkCheckCallAny.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
	  
		  checkRaise.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
		  checkRaise.setIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  checkRaise.setSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  //checkRaise.setDisabledIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkRaise.setDisabledSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkRaise.setRolloverSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  checkRaise.setPressedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  if(checkRaise.isSelected())
			  checkRaise.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  else
			  checkRaise.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
	  
		  checkRaiseAny.setFont(new Font("Humanst521 Bold BT", Font.PLAIN, 11));
		  checkRaiseAny.setIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  checkRaiseAny.setSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  //checkRaiseAny.setDisabledIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkRaiseAny.setDisabledSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
		  //checkRaiseAny.setRolloverSelectedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  checkRaiseAny.setPressedIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  if(checkRaiseAny.isSelected())
			  checkRaiseAny.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_DE));
		  else
			  checkRaiseAny.setRolloverIcon(Utils.getIcon(ClientConfig.IMG_CHECK_EN));
	  }
	  checks.setBounds(new Rectangle((int)(610*ratioX),(int)(85*ratioY),(int)(180*ratioX),(int)(60*ratioY)));
	  slider.setBounds(new Rectangle((int)(4*ratioX), (int)(6*ratioY), (int)(168*ratioX), (int)(15*ratioY)));
	  slider_bg.setBounds(new Rectangle((int)(3*ratioX), (int)(4*ratioY),(int)(170*ratioX),(int)(21*ratioY)));
	  
	  sliderfield_bg.setIcon(skin.getScaledImage(Utils.getIcon("images/sliderfield_bg.png")));
	  sliderfield_bg.setBounds(new Rectangle((int)((205 + 7)*ratioX),(int)(5*ratioY),(int)(64*ratioX),(int)(23*ratioY)));
	  sliderField.setBounds(new Rectangle((sliderfield_bg.getLocation().x+(int)(20*ratioX))/*+ (int)(70*ratioX)*/ , (int)(9*ratioY), (int)(40*ratioX), (int)(15*ratioY)));
	  sliderField.setFont(new Font("Arial", Font.PLAIN, (int)(11*ratioX)));
	    
	  sliderpanel_bg.setBounds(new Rectangle(0, 0,(int)(278*ratioX),(int)(46*ratioY)));//227,47,274,29
	  
	  arrow_left.setIcon(skin.getScaledImage(Utils.getIcon("images/arrow_left.png")));
	  arrow_right.setIcon(skin.getScaledImage(Utils.getIcon("images/arrow_right.png")));
	  
	  arrow_left.setBounds(new Rectangle((int)((176)*ratioX),(int)(6*ratioY), (int)(16*ratioX), (int)(18*ratioY)));
	  arrow_right.setBounds(new Rectangle(arrow_left.getLocation().x + (int)(18*ratioX),(int)(6*ratioY), (int)(16*ratioX), (int)(18*ratioY)));
	  labelEuro.setBounds(new Rectangle(sliderfield_bg.getLocation().x+ (int)(5*ratioX) , (int)(8*ratioY), (int)(20*ratioX), (int)(15*ratioY)));
	  
	  //sliderMin.setBounds(new Rectangle((int)((10 -6)*ratioX), (int)((25+3)*ratioY), (int)(25*ratioX), (int)(15*ratioY)));//
	  //sliderX2.setBounds(new Rectangle((int)((10 + 24)*ratioX), (int)((25+3)*ratioY), (int)(25*ratioX), (int)(15*ratioY)));//  
	  sliderX3.setBounds(new Rectangle((int)(3*ratioX), (int)((28)*ratioY), (int)(40*ratioX), (int)(15*ratioY)));//  label width= 40, space=3
	  sliderX4.setBounds(new Rectangle((int)(46*ratioX), (int)((28)*ratioY), (int)(40*ratioX), (int)(15*ratioY)));//  46 = 3+43
	  sliderX5.setBounds(new Rectangle((int)(89*ratioX), (int)((28)*ratioY), (int)(40*ratioX), (int)(15*ratioY)));// 89 = 3+43+43
	  sliderMax.setBounds(new Rectangle((int)(132*ratioX), (int)((28)*ratioY), (int)(40*ratioX), (int)(15*ratioY)));//132= 3+43+43+43
	  sliderMax.setLocation(sliderX3.getLocation().x+(int)(129*ratioX), (int)((28)*ratioY));
	  limitAmt.setBounds(new Rectangle((int)((10 + 195)*ratioX), (int)(25*ratioY), (int)(70*ratioX), (int)(20*ratioY)));
	  limitAmt.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(11*ratioX)));
		
	  sliderPanel.setBounds((int)(skin.roomWidth - (sliderPanelWidth*ratioX)-10), (int)(55*ratioY)  ,(int)(sliderPanelWidth*ratioX) , (int)(sliderPanelHeight*ratioY));//290 -> 190 , + 100
	  //sliderPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
	   
	  //sliderMin.setFont(new Font("Verdana", Font.BOLD, (int)(10*ratioX)));
	  //sliderX5.setFont(new Font("Verdana", Font.BOLD, (int)(10*ratioX)));
	  //sliderMax.setFont(new Font("Verdana", Font.BOLD, (int)(10*ratioX)));
	  
	  sliderPanel.add(slider);
      sliderPanel.add(sliderField);
	  sliderPanel.add(arrow_left);
	  sliderPanel.add(arrow_right);
	  sliderPanel.add(labelEuro);
	  sliderPanel.add(slider_bg);
	  sliderPanel.add(sliderfield_bg);
	  //sliderPanel.add(sliderMin);
	  //sliderPanel.add(sliderX2);
	  sliderPanel.add(sliderX3);
	  sliderPanel.add(sliderX4);
	  sliderPanel.add(sliderX5);
	  //sliderPanel.add(sliderHalfPot);
	  //sliderPanel.add(sliderPot);
	  sliderPanel.add(sliderMax);
	  sliderPanel.add(limitAmt);
	  sliderPanel.add(sliderpanel_bg);
	  
	  //this.add(chatFieldPanel);
	  this.add(sitInOut);
	  this.add(chatTextPane);
      this.add(muckCardsCB);
      this.add(autoPostBlindCB);
      this.add(sitOutNextHandCB);
      this.add(foldToAnyBetCB);
      //this.add(chatTextPane);
      this.add(quickFold);
      this.add(centerPanel, BorderLayout.WEST);
	  this.add(sliderPanel,BorderLayout.WEST);
      this.add(checks);
  }
  
  /** this method is using from ClientRoom class for Keyboard shortcuts */
  public void performActionWhenKeyPressed(int keyValue){
      if(keyValue == 112){
    	  for (int i = 0; i < buttons.length; i++) {
        	String name = buttons[i].getName();
        	if (buttons[i].isVisible() && bundle.getString("fold").equals(name)){
        		buttons[i].doClick();
        		//appendLog(MessageFormat.format(bundle.getString("do.fold"), new Object[] {_serverProxy._name}));
        		break;
        	}
		}
      }else if(keyValue == 113){
    	  for (int i = 0; i < buttons.length; i++) {
        	String name = buttons[i].getName();
        	if (buttons[i].isVisible() && bundle.getString("check").equals(name)){
        		buttons[i].doClick();
        		break;
        	}else if (buttons[i].isVisible() && bundle.getString("call").equals(name)){
        		buttons[i].doClick();
        		break;
        	}
		}
      }else if(keyValue == 114){
    	  for (int i = 0; i < buttons.length; i++) {
        	String name = buttons[i].getName();
        	if (buttons[i].isVisible() && bundle.getString("bet").equals(name)){
        		buttons[i].doClick();
        		break;
        	}
        	if (buttons[i].isVisible() && bundle.getString("raise").equals(name)){
  				buttons[i].doClick();
  				break;
        	}
		  }
      }else if(keyValue == 115){
    	  for (int i = 0; i < buttons.length; i++) {
        	String name = buttons[i].getName();
        	if (buttons[i].isVisible() && bundle.getString("allin").equals(name)){
        		buttons[i].doClick();
        		break;
        	}
		  }
      }
  }
   
}
