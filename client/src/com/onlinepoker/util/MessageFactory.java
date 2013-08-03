/*
 * Created on Jul 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.onlinepoker.util;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.LobbyTournyModel;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;




/**
 * @author Halt
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MessageFactory {
	public static JDialog dialog = null;

	public static String getStringWindowOne (RoomSkin skin, String title,
									String message, String initialValue, JFrame frame, ServerProxy lobbyServer) {
		MyInputDialog myInputDialog = new MyInputDialog(skin, title, message, initialValue, frame, true, lobbyServer);
		myInputDialog.setLocationRelativeTo(frame);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}

	public static String getStringWindowOneYesNo (RoomSkin skin, String title,
									String message, String initialValue, JFrame frame, ServerProxy lobbyServer) {
		MyInputDialogYesNo myInputDialogYesNo = new MyInputDialogYesNo(skin, title, message, initialValue, frame, true, lobbyServer);
		myInputDialogYesNo.setLocationRelativeTo(frame);
		myInputDialogYesNo.setVisible(true);
		dialog = null;
		return myInputDialogYesNo.reply;
	}

	public static String getStringWindowMany (RoomSkin skin, String title,
									String message, String initialValue, JFrame frame, ServerProxy lobbyServer) {
		MyInputDialog myInputDialog = new MyInputDialog(skin, title, message, initialValue, frame, false, lobbyServer);
		myInputDialog.setVisible(true);
		return myInputDialog.reply;
	}
	
	public static MyHandHistoryDialog getHandHistoryWindow(JFrame frame, String title,
			String name, long grid ) {
		MyHandHistoryDialog myInputDialog = new MyHandHistoryDialog(frame, title, name, grid);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog;
	}
	
	public static MyStatsDialog getStatsWindow(RoomSkin skin, String title,
			String message, JFrame frame, ServerProxy lobbyServer, Statistics _sts) {
		MyStatsDialog myInputDialog = new MyStatsDialog(skin, title, message,  frame, true, lobbyServer, _sts);
		myInputDialog.setVisible(true);
		//myInputDialog.setLocationRelativeTo(frame);
		dialog = null;
		return myInputDialog;
	}
	
	public static String getSNGRegisterWindow(String title,
			LobbyTableModel ltm, double amt) {
		MySNGRegisterDialog myInputDialog = new MySNGRegisterDialog(title, ltm, amt);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}
	
	public static String getMTTRegisterWindow(String title,
			LobbyTournyModel ltm, double amt) {
		MyMTTRegisterDialog myInputDialog = new MyMTTRegisterDialog(title, ltm, amt);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}
	
	public static String getSNGPrizePoolWindow(String title,
			LobbyTableModel ltm) {
		MySNGPrizePoolDialog myInputDialog = new MySNGPrizePoolDialog(title, ltm);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}
	
	public static String getMTTPrizePoolWindow(String title,
			LobbyTournyModel ltm) {
		MyMTTPrizePoolDialog myInputDialog = new MyMTTPrizePoolDialog(title, ltm);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}
	
	public static String getRebuyInfoWindow(String title,
			String message) {
		MyRebuyInfoDialog myInputDialog = new MyRebuyInfoDialog(title, message);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}
	
	public static MyPlayerNoteDialog getPlayerNoteWindow(String title,
			String name) {
		MyPlayerNoteDialog myPlayerNoteDialog = new MyPlayerNoteDialog(title, name);
		myPlayerNoteDialog.setVisible(true);
		dialog = null;
		return myPlayerNoteDialog;
	}
}

	class MyInputDialog extends JDialog implements ActionListener {
	
		protected ServerProxy lobbyServer;
		protected boolean unique;
		protected static Dimension screenSize;
		protected static Dimension frameSize;
		protected static Point framePos;
		static {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frameSize = new Dimension(300, 400);
			framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
		}
		JComboBox  gameType;
		ImageIcon icon;
		int hinc = -1;
		int winc = -1;
		JTextField field;
		JButton bOk, bClose, bReset;
		ResourceBundle bundle = Bundle.getBundle();
		String reply = null;
	
		public MyInputDialog (RoomSkin skin, String title,
								String message, String initialValue, JFrame frame, boolean unique, ServerProxy lobbyServer) {
			//super (frame);
			super(new Frame(),title,false);
			this.unique = unique;
			this.lobbyServer    = lobbyServer;
			initComponents();
			setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
			setModalityType(ModalityType.TOOLKIT_MODAL);
			/*Frame frames[] = ClientRoom.getFrames();
			int j=-1;
			for (int i = 0; i < frames.length; i++) {
				System.out.println(frames[i].getName()+"--"+frames[i].getTitle()+"--"+frames[i].getSize());
				if(frames[i].getTitle().contains("hand id =")){j=i;break;}
			}
			if(j != -1)
			{
				System.out.println("frame matched"+frames[j].getName());
				setLocationRelativeTo(frames[j]);
			}
			setModalityType(ModalityType.APPLICATION_MODAL);*/
			setTitle(title);
			setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
			setModal(true);
			icon = skin.getDialogBackground();
			hinc = icon.getIconHeight();
			winc = icon.getIconWidth();
			Container pane = getContentPane();
	
			JPanel tmpPanel = new JPanel(new BorderLayout()) {
				public void paintComponent(Graphics g) {
					int w = getWidth();
					int h = getHeight();
					for (int i = 0; i < h + hinc; i = i + hinc)
						for (int j = 0; j < w + winc; j = j + winc)
							icon.paintIcon(this, g, j, i);
				}
			};
			
			JPanel northPanel = new JPanel(new BorderLayout());
			northPanel.setOpaque(false);
			northPanel.setPreferredSize(new Dimension(200, 150));
			JLabel label = new JLabel(message);
			label.setAlignmentX(CENTER_ALIGNMENT);
			label.setForeground(Color.BLACK);
			label.setFont(new Font("Verdana", Font.PLAIN, 11));
			northPanel.add(label, BorderLayout.CENTER);
			
	
			JPanel southPanel = new JPanel(new BorderLayout());
			southPanel.setOpaque(false);
			if (field != null) {
				field.addActionListener(this);
				field.setText(initialValue);
				field.setSelectionStart(0);
				field.setSelectionEnd(initialValue.length());
				field.setSelectionColor(Color.LIGHT_GRAY);
				field.setForeground(Color.WHITE);
			}
			JPanel panel = new JPanel(new BorderLayout());
			panel.setOpaque(false);
			if (field != null)
				panel.add(field, BorderLayout.CENTER);
			panel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
			panel.add(Box.createHorizontalStrut(20), BorderLayout.EAST);
	
			southPanel.add(panel, BorderLayout.NORTH);
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 35));
			panel.setOpaque(false);
			panel.add(bOk);
			panel.add(bClose);
			//panel.add(bCashier);
			southPanel.add(panel, BorderLayout.SOUTH);
	
			tmpPanel.add (northPanel, BorderLayout.CENTER);
			tmpPanel.add(southPanel, BorderLayout.SOUTH);
			tmpPanel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
			tmpPanel.add(Box.createHorizontalStrut(20), BorderLayout.EAST);
			pane.add(tmpPanel);
	//---------
			setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
			setResizable(false);
	//---------
			if (unique)
				MessageFactory.dialog = this;
	//////////////////////////////////////////////////////////////////		setVisible(true);
		}
		
		
	
		public MyInputDialog(RoomSkin skin, String title, String message,
				Component frame, boolean unique2, ServerProxy lobbyServer2) {
			// TODO Auto-generated constructor stub
			super((JFrame)frame);
			this.setLocationRelativeTo(frame);
			//setIgnoreRepaint(true);
		}
	
	
	
		protected void initComponents() {
			field = new JTextField ();
			field.setOpaque(false);
			field.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			field.setForeground(Color.BLACK);
			field.setSize(100, 20);
			
	
			/**bCashier = new JButton(bundle.getString("cashier.button"));
			bCashier.setOpaque(true);
			bCashier.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
			bCashier.setForeground(Color.WHITE);
			bCashier.setFocusPainted(false);
	//		bOk.setBorderPainted(false);
			bCashier.setContentAreaFilled(false);**/
	
			bOk = new JButton(bundle.getString("ok"),Utils.getIcon(ClientConfig.BTN_BG));
			bOk.setForeground(Color.WHITE);
			bOk.setFocusPainted(false);
			bOk.setBorderPainted(false);
			bOk.setContentAreaFilled(false);
			bOk.setVerticalTextPosition(AbstractButton.CENTER);
		    bOk.setHorizontalTextPosition(AbstractButton.CENTER);
		    
		    
		    
			bClose = new JButton(bundle.getString("cancel"),Utils.getIcon(ClientConfig.BTN_BG));
			bClose.setForeground(Color.WHITE);
			bClose.setFocusPainted(false);
			bClose.setBorderPainted(false);
			bClose.setContentAreaFilled(false);
			bClose.setVerticalTextPosition(AbstractButton.CENTER);
		    bClose.setHorizontalTextPosition(AbstractButton.CENTER);
	
			bOk.addActionListener(this);
			bClose.addActionListener(this);
			//bCashier.addActionListener(this);
			field.addActionListener(this);
		}
	
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bClose)
				reply = null;
			else if (source == bOk || source == field)
				reply = field.getText();
			
			if (unique)
				MessageFactory.dialog = null;
			dispose();
		}
	}

	class MyInputDialogYesNo extends MyInputDialog implements ActionListener {
	
		public MyInputDialogYesNo(RoomSkin skin, String title, String message, String initialValue, JFrame frame, boolean unique, ServerProxy lobbyServer) {
			super(skin, title, message, initialValue, frame, unique, lobbyServer);
			reply = initialValue;
		}
	
		protected void initComponents() {
			super.initComponents();
			field = null;
		}
	
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bClose) {
				reply = null;
			} else if (source == bOk || source == field) {
				reply = reply;
			} 
			if (unique)
				MessageFactory.dialog = null;
			dispose();
		}
	}
	

	
	
	class MySNGRegisterDialog extends JDialog implements ActionListener 
	{
		protected static Dimension screenSize;
		protected static Dimension frameSize;
		protected static Point framePos;
		ImageIcon icon;
		int hinc = -1;
		int winc = -1;
		JButton bBuyin,bClose;
		String reply = null;
		ResourceBundle bundle = Bundle.getBundle();
		  
		public MySNGRegisterDialog ( String title,
			LobbyTableModel ltm, double amt) {
			super();
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frameSize = new Dimension(500, 300);
				framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
				setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
					
		Container pane = getContentPane();
		setTitle(title);
		setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
		setModal(true);
		icon = Utils.getIcon("images/dialog_plain.jpg");
		hinc = icon.getIconHeight();
		winc = icon.getIconWidth();
				
		JPanel panel = new JPanel()
		 {
			public void paintComponent(Graphics g) {
				int w = getWidth();
				int h = getHeight();
				for (int i = 0; i < h + hinc; i = i + hinc)
					for (int j = 0; j < w + winc; j = j + winc)
						icon.paintIcon(this, g, j, i);
			}
		};
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(500, 300));
		panel.setSize(500, 300);
		panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setOpaque(false);
		JLabel label = new JLabel("Buy-In: "+ltm.getMinBuyIn()+(ltm.isRealMoneyTable()?"+ "+ltm.getFee()+" Real Chips":" Play Chips"));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Verdana", Font.BOLD, 16));
		panel1.add(label);
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setOpaque(false);
		
		Box panel3 = new Box(BoxLayout.X_AXIS);
		
		JPanel bi_panel = new JPanel(new BorderLayout());
		bi_panel.setBorder(new EmptyBorder(0,10,0,10));
		bi_panel.setOpaque(false);
		TitledBorder tb = new TitledBorder("Buy-In With");
		tb.setTitleColor(new Color(123,158,189));
		tb.setTitleFont(new Font("Verdana", Font.PLAIN, 11));
		bi_panel.setBorder(tb);
		bi_panel.setSize(200, 250);
		bBuyin = new JButton((ltm.isRealMoneyTable()?"€ "+(ltm.getMinBuyIn()+ltm.getFee()):ltm.getMinBuyIn())+"",Utils.getIcon(ClientConfig.BTN_BG));
		bBuyin.setForeground(Color.WHITE);
		bBuyin.setFocusPainted(false);
		bBuyin.setBorderPainted(false);
		bBuyin.setContentAreaFilled(false);
		bBuyin.setVerticalTextPosition(AbstractButton.CENTER);
		bBuyin.setHorizontalTextPosition(AbstractButton.CENTER);
		bBuyin.addActionListener(this);
	    JCheckBox cb = new JCheckBox("Register for another Sit&Go of this type ");
		Font newCheckBoxFont = new Font(cb.getFont().getName(),cb.getFont().getStyle(),12);  
		cb.setFont(newCheckBoxFont);  
		cb.setOpaque(false);
		bi_panel.add(new JLabel(ltm.isRealMoneyTable()?"  Real Chips: ":"  Play Chips: "),BorderLayout.WEST);
		bi_panel.add(bBuyin,BorderLayout.EAST);
		bi_panel.add(cb,BorderLayout.SOUTH);
		
		
		
		JPanel ab_panel = new JPanel(new BorderLayout());
		ab_panel.setBorder(new EmptyBorder(0,10,0,10));
		ab_panel.setOpaque(false);
		TitledBorder tb1 = new TitledBorder("Available Balance");
		tb1.setTitleColor(new Color(123,158,189));
		tb1.setTitleFont(new Font("Verdana", Font.PLAIN, 11));
		ab_panel.setBorder(tb1);
		ab_panel.setSize(200, 250);
		ab_panel.add(new JLabel(ltm.isRealMoneyTable()?"  Real Chips: ":"  Play Chips: "),BorderLayout.WEST);
		ab_panel.add(new JLabel(amt+(ltm.isRealMoneyTable()?" Real Chips":" Play Chips")),BorderLayout.EAST);
		
		
		panel3.add(bi_panel);
		panel3.add(ab_panel);
		
		JPanel panel4 = new JPanel(new BorderLayout());
		panel4.setOpaque(false);
		 
	    bClose = new JButton(bundle.getString("close"),Utils.getIcon(ClientConfig.BTN_BG));
		bClose.setForeground(Color.WHITE);
		bClose.setFocusPainted(false);
		bClose.setBorderPainted(false);
		bClose.setContentAreaFilled(false);
		bClose.setVerticalTextPosition(AbstractButton.CENTER);
	    bClose.setHorizontalTextPosition(AbstractButton.CENTER);
	    
	    bClose.addActionListener(this);
		panel4.add(bClose,BorderLayout.EAST);
		
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(panel4);
		pane.add(panel);
		//---------
		//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
		setResizable(false);
		//---------
		
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bBuyin)
				reply = "buyin";
			else if (source == bClose)
				reply = "close";
			
			
			
			dispose();
		}
		
		
		
	}
	
	class MyMTTRegisterDialog extends JDialog implements ActionListener 
	{
		protected static Dimension screenSize;
		protected static Dimension frameSize;
		protected static Point framePos;
		ImageIcon icon;
		int hinc = -1;
		int winc = -1;
		JButton bBuyin,bClose;
		String reply = null;
		ResourceBundle bundle = Bundle.getBundle();
		  
		public MyMTTRegisterDialog ( String title,
			LobbyTournyModel ltm, double amt) {
			super();
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frameSize = new Dimension(500, 300);
				framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
				setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
					
		Container pane = getContentPane();
		setTitle(title);
		setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
		setModal(true);
		icon = Utils.getIcon("images/dialog_plain.jpg");
		hinc = icon.getIconHeight();
		winc = icon.getIconWidth();
				
		JPanel panel = new JPanel()
		 {
			public void paintComponent(Graphics g) {
				int w = getWidth();
				int h = getHeight();
				for (int i = 0; i < h + hinc; i = i + hinc)
					for (int j = 0; j < w + winc; j = j + winc)
						icon.paintIcon(this, g, j, i);
			}
		};
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(500, 300));
		panel.setSize(500, 300);
		panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setOpaque(false);
		JLabel label = new JLabel("Buy-In: "+ltm.getTournamentBuyIn()+(ltm.isRealMoneyTable()?"+ "+ltm.getTournamentFee()+" Real Chips":" Play Chips"));
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Verdana", Font.BOLD, 16));
		panel1.add(label);
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setOpaque(false);
		
		Box panel3 = new Box(BoxLayout.X_AXIS);
		
		JPanel bi_panel = new JPanel(new BorderLayout());
		bi_panel.setBorder(new EmptyBorder(0,10,0,10));
		bi_panel.setOpaque(false);
		TitledBorder tb = new TitledBorder("Buy-In With");
		tb.setTitleColor(new Color(123,158,189));
		tb.setTitleFont(new Font("Verdana", Font.PLAIN, 11));
		bi_panel.setBorder(tb);
		bi_panel.setSize(200, 250);
		bBuyin = new JButton((ltm.isRealMoneyTable()?"€ "+(ltm.getTournamentBuyIn()+ltm.getTournamentFee()):ltm.getTournamentBuyIn())+"",Utils.getIcon(ClientConfig.BTN_BG));
		bBuyin.setForeground(Color.WHITE);
		bBuyin.setFocusPainted(false);
		bBuyin.setBorderPainted(false);
		bBuyin.setContentAreaFilled(false);
		bBuyin.setVerticalTextPosition(AbstractButton.CENTER);
		bBuyin.setHorizontalTextPosition(AbstractButton.CENTER);
		bBuyin.addActionListener(this);
	    JCheckBox cb = new JCheckBox("Register for another Sit&Go of this type ");
		Font newCheckBoxFont = new Font(cb.getFont().getName(),cb.getFont().getStyle(),12);  
		cb.setFont(newCheckBoxFont);  
		cb.setOpaque(false);
		bi_panel.add(new JLabel(ltm.isRealMoneyTable()?"  Real Chips: ":"  Play Chips: "),BorderLayout.WEST);
		bi_panel.add(bBuyin,BorderLayout.EAST);
		bi_panel.add(cb,BorderLayout.SOUTH);
		
		
		
		JPanel ab_panel = new JPanel(new BorderLayout());
		ab_panel.setBorder(new EmptyBorder(0,10,0,10));
		ab_panel.setOpaque(false);
		TitledBorder tb1 = new TitledBorder("Available Balance");
		tb1.setTitleColor(new Color(123,158,189));
		tb1.setTitleFont(new Font("Verdana", Font.PLAIN, 11));
		ab_panel.setBorder(tb1);
		ab_panel.setSize(200, 250);
		ab_panel.add(new JLabel(ltm.isRealMoneyTable()?"  Real Chips: ":"  Play Chips: "),BorderLayout.WEST);
		ab_panel.add(new JLabel(amt+(ltm.isRealMoneyTable()?" Real Chips":" Play Chips")),BorderLayout.EAST);
		
		
		panel3.add(bi_panel);
		panel3.add(ab_panel);
		
		JPanel panel4 = new JPanel(new BorderLayout());
		panel4.setOpaque(false);
		 
	    bClose = new JButton(bundle.getString("close"),Utils.getIcon(ClientConfig.BTN_BG));
		bClose.setForeground(Color.WHITE);
		bClose.setFocusPainted(false);
		bClose.setBorderPainted(false);
		bClose.setContentAreaFilled(false);
		bClose.setVerticalTextPosition(AbstractButton.CENTER);
	    bClose.setHorizontalTextPosition(AbstractButton.CENTER);
	    
	    bClose.addActionListener(this);
		panel4.add(bClose,BorderLayout.EAST);
		
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(panel4);
		pane.add(panel);
		//---------
		//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
		setResizable(false);
		//---------
		
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bBuyin)
				reply = "buyin";
			else if (source == bClose)
				reply = "close";
			
			
			
			dispose();
		}
		
		
		
	}
	
	class MySNGPrizePoolDialog extends JDialog implements ActionListener 
	{
		protected static Dimension screenSize;
		protected static Dimension frameSize;
		protected static Point framePos;
		ImageIcon icon;
		int hinc = -1;
		int winc = -1;
		JButton bOk;
		String reply = null;
		ResourceBundle bundle = Bundle.getBundle();
		protected DefaultTableModel prizesTableModel,bsTableModel;
		protected JTable prizesTable,bsTable;
		
		
		public MySNGPrizePoolDialog ( String title,
				LobbyTableModel ltm) {
			super();
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frameSize = new Dimension(500, 400);
				framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
				setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
					
		Container pane = getContentPane();
		setTitle(title);
		setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
		setModal(true);
		icon = Utils.getIcon("images/dialog_plain.jpg");
		hinc = icon.getIconHeight();
		winc = icon.getIconWidth();
		
//		
		JPanel panel = new JPanel()
		 {
			public void paintComponent(Graphics g) {
				int w = getWidth();
				int h = getHeight();
				for (int i = 0; i < h + hinc; i = i + hinc)
					for (int j = 0; j < w + winc; j = j + winc)
						icon.paintIcon(this, g, j, i);
			}
		};
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(250, 350));
		panel.setSize(250, 350);
		panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
			
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setOpaque(false);
		panel1.add(new JLabel("Prizes:"),BorderLayout.WEST);
		panel1.add(new JLabel("* Based On "+ltm.getPlayerCapacity()+" Players"),BorderLayout.EAST);
		
		double prize_pool = ltm.getMinBuyIn() * ltm.getPlayerCapacity();
		
		
	  
		Object[] columnNames = {"Places","Award"};
		prizesTableModel = new DefaultTableModel(null, columnNames){
			 
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			 
			// Returning the Class of each column will allow different
			// renderers to be used based on Class
			@Override
			public Class getColumnClass(int column)
			{
				return getValueAt(0, column).getClass();
			}
			 
			};
			if(ltm.getPlayerCapacity() == 10)
			{		
				prizesTableModel.addRow(new String[] {"1","€ "+(prize_pool * 50)/100});
				prizesTableModel.addRow(new String[] {"2","€ "+(prize_pool * 30)/100});
				prizesTableModel.addRow(new String[] {"3","€ "+(prize_pool * 20)/100});
			}
			else if(ltm.getPlayerCapacity() == 6)
			{		
				prizesTableModel.addRow(new String[] {"1","€ "+(prize_pool * 66.7)/100});
				prizesTableModel.addRow(new String[] {"2","€ "+(prize_pool * 33)/100});
			}
			else if(ltm.getPlayerCapacity() == 2)
			{		
				prizesTableModel.addRow(new String[] {"1","€ "+prize_pool });
			}
			
		prizesTable = new JTable(prizesTableModel);
		prizesTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
		prizesTable.setEnabled(true);
		prizesTable.setFont(new Font("Verdana", Font.PLAIN, 9));
		//Cell selection is enabled
		prizesTable.setCellSelectionEnabled(false);
		prizesTable.setRowSelectionAllowed(false);
		prizesTable.getTableHeader().setReorderingAllowed(false);
		prizesTable.setShowHorizontalLines(false);
		prizesTable.setShowVerticalLines(false);
		prizesTable.setShowGrid(false);
		prizesTable.setIntercellSpacing(new Dimension(0, 0));
		prizesTable.setDefaultRenderer(String.class, new JComponentTableCellRenderer());

		JScrollPane scrollPane_prizes = new JScrollPane(JScrollPane.
                VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.
                HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_prizes.setBackground(Color.WHITE);
		scrollPane_prizes.setViewportView(prizesTable);
		scrollPane_prizes.setBorder(new LineBorder(new Color(123,158,189)));
		scrollPane_prizes.setPreferredSize(new Dimension(450, 100));
		scrollPane_prizes.getViewport().setOpaque(false);	
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel2.add(scrollPane_prizes);	
		
		JPanel panel3 = new JPanel(new BorderLayout());
		panel3.setOpaque(false);
		panel3.add(new JLabel("Betting Structure:"),BorderLayout.WEST);
		panel3.add(new JLabel("Starting Chips: "+ltm.getTournyChips()),BorderLayout.EAST);
		
		  
		Object[] bs_columnNames = {"Level","Blinds","Ante","Minutes"};
		bsTableModel = new DefaultTableModel(null, bs_columnNames){
			 
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			 
			// Returning the Class of each column will allow different
			// renderers to be used based on Class
			@Override
			public Class getColumnClass(int column)
			{
				return getValueAt(0, column).getClass();
			}
			 
			};
		for (int i = 0; i < TournamentStructure._sng_blind.length; i++) {
			bsTableModel.addRow(new String[] {i+1+"",""+TournamentStructure._sng_blind[i][0],""+TournamentStructure._sng_blind[i][1],
					ltm.getTournyChips()==5000?"3":ltm.getTournyChips()==2000?"5":"10"});
			
		}
		bsTable = new JTable(bsTableModel);
		bsTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
		bsTable.setEnabled(true);
		bsTable.setFont(new Font("Verdana", Font.PLAIN, 9));
		//Cell selection is enabled
		bsTable.setCellSelectionEnabled(false);
		bsTable.setRowSelectionAllowed(false);
		bsTable.getTableHeader().setReorderingAllowed(false);
		bsTable.setShowHorizontalLines(false);
		bsTable.setShowVerticalLines(false);
		bsTable.setShowGrid(false);
		bsTable.setIntercellSpacing(new Dimension(0, 0));
		bsTable.setDefaultRenderer(String.class, new JComponentTableCellRenderer());
		
		JScrollPane bs_scrollPane = new JScrollPane(JScrollPane.
                VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.
                HORIZONTAL_SCROLLBAR_NEVER);
		bs_scrollPane.setBackground(Color.WHITE);
		bs_scrollPane.setViewportView(bsTable);
		bs_scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		bs_scrollPane.setPreferredSize(new Dimension(450, 100));
		bs_scrollPane.getViewport().setOpaque(false);	
		bs_scrollPane.setBorder(new LineBorder(new Color(123,158,189)));
		JPanel panel4 = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel4.add(bs_scrollPane);	
		
		JPanel panel5 = new JPanel(new BorderLayout());
		panel5.setOpaque(false);
		
	    bOk = new JButton(bundle.getString("ok"),Utils.getIcon(ClientConfig.BTN_BG));
		bOk.setForeground(Color.WHITE);
		bOk.setFocusPainted(false);
		bOk.setBorderPainted(false);
		bOk.setContentAreaFilled(false);
		bOk.setVerticalTextPosition(AbstractButton.CENTER);
	    bOk.setHorizontalTextPosition(AbstractButton.CENTER);
	    
	    bOk.addActionListener(this);
		panel5.add(bOk,BorderLayout.CENTER);
		
		
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(panel4);
		panel.add(panel5);
		pane.add(panel);
		//---------
		//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
		setResizable(false);
		//---------
		
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bOk)
				reply = "ok";
			dispose();
		}
	}
	
	class MyMTTPrizePoolDialog extends JDialog implements ActionListener 
	{
		protected static Dimension screenSize;
		protected static Dimension frameSize;
		protected static Point framePos;
		ImageIcon icon;
		int hinc = -1;
		int winc = -1;
		JButton bOk;
		String reply = null;
		ResourceBundle bundle = Bundle.getBundle();
		protected DefaultTableModel prizesTableModel,bsTableModel;
		protected JTable prizesTable,bsTable;
		
		
		public MyMTTPrizePoolDialog ( String title,
				LobbyTournyModel ltm) {
			super();
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frameSize = new Dimension(500, 400);
				framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
				setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
					
		Container pane = getContentPane();
		setTitle(title);
		setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
		setModal(true);
		icon = Utils.getIcon("images/dialog_plain.jpg");
		hinc = icon.getIconHeight();
		winc = icon.getIconWidth();
		
//		
		JPanel panel = new JPanel()
		 {
			public void paintComponent(Graphics g) {
				int w = getWidth();
				int h = getHeight();
				for (int i = 0; i < h + hinc; i = i + hinc)
					for (int j = 0; j < w + winc; j = j + winc)
						icon.paintIcon(this, g, j, i);
			}
		};
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(250, 350));
		panel.setSize(250, 350);
		panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
			
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setOpaque(false);
		panel1.add(new JLabel("Prizes:"),BorderLayout.WEST);
		panel1.add(new JLabel("* Based On "+ltm.getTournamentMaxPlayers()+" Players"),BorderLayout.EAST);
		
		
		
	  
		Object[] columnNames = {"Places","Award"};
		prizesTableModel = new DefaultTableModel(null, columnNames){
			 
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			 
			// Returning the Class of each column will allow different
			// renderers to be used based on Class
			@Override
			public Class getColumnClass(int column)
			{
				return getValueAt(0, column).getClass();
			}
			 
			};
		
		double prize_pool = ltm.getTournamentBuyIn() * ltm.getTournamentMaxPlayers();
		for (int i = 0; i < TournamentStructure._t_payout[10].length; i++) {
			prizesTableModel.addRow(new String[] {i+1+"","€ "+(prize_pool * TournamentStructure._t_payout[10][i])/100});
			
		}
		      
			
			
		prizesTable = new JTable(prizesTableModel);
		prizesTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
		prizesTable.setEnabled(true);
		prizesTable.setFont(new Font("Verdana", Font.PLAIN, 9));
		//Cell selection is enabled
		prizesTable.setCellSelectionEnabled(false);
		prizesTable.setRowSelectionAllowed(false);
		prizesTable.getTableHeader().setReorderingAllowed(false);
		prizesTable.setShowHorizontalLines(false);
		prizesTable.setShowVerticalLines(false);
		prizesTable.setShowGrid(false);
		prizesTable.setIntercellSpacing(new Dimension(0, 0));
		prizesTable.setDefaultRenderer(String.class, new JComponentTableCellRenderer());

		JScrollPane scrollPane_prizes = new JScrollPane(JScrollPane.
                VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.
                HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_prizes.setBackground(Color.WHITE);
		scrollPane_prizes.setViewportView(prizesTable);
		scrollPane_prizes.setBorder(new LineBorder(new Color(123,158,189)));
		scrollPane_prizes.setPreferredSize(new Dimension(450, 100));
		scrollPane_prizes.getViewport().setOpaque(false);	
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel2.add(scrollPane_prizes);	
		
		JPanel panel3 = new JPanel(new BorderLayout());
		panel3.setOpaque(false);
		panel3.add(new JLabel("Betting Structure:"),BorderLayout.WEST);
		panel3.add(new JLabel("Starting Chips: 1000"),BorderLayout.EAST);
		
				
	  
		Object[] bs_columnNames = {"Level","Blinds","Ante","Minutes"};
		bsTableModel = new DefaultTableModel(null, bs_columnNames){
			 
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			 
			// Returning the Class of each column will allow different
			// renderers to be used based on Class
			@Override
			public Class getColumnClass(int column)
			{
				return getValueAt(0, column).getClass();
			}
			 
			};
		
		for (int i = 0; i < TournamentStructure._mtt_blind.length; i++) {
			bsTableModel.addRow(new String[] {i+1+"",""+TournamentStructure._mtt_blind[i][0],""+TournamentStructure._mtt_blind[i][1],""});
			
		}
			
		bsTable = new JTable(bsTableModel);
		bsTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
		bsTable.setEnabled(true);
		bsTable.setFont(new Font("Verdana", Font.PLAIN, 9));
		//Cell selection is enabled
		bsTable.setCellSelectionEnabled(false);
		bsTable.setRowSelectionAllowed(false);
		bsTable.getTableHeader().setReorderingAllowed(false);
		bsTable.setShowHorizontalLines(false);
		bsTable.setShowVerticalLines(false);
		bsTable.setShowGrid(false);
		bsTable.setIntercellSpacing(new Dimension(0, 0));
		bsTable.setDefaultRenderer(String.class, new JComponentTableCellRenderer());
		
		JScrollPane bs_scrollPane = new JScrollPane(JScrollPane.
                VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.
                HORIZONTAL_SCROLLBAR_NEVER);
		bs_scrollPane.setBackground(Color.WHITE);
		bs_scrollPane.setViewportView(bsTable);
		bs_scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		bs_scrollPane.setPreferredSize(new Dimension(450, 100));
		bs_scrollPane.getViewport().setOpaque(false);	
		bs_scrollPane.setBorder(new LineBorder(new Color(123,158,189)));
		JPanel panel4 = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel4.add(bs_scrollPane);	
		
		JPanel panel5 = new JPanel(new BorderLayout());
		panel5.setOpaque(false);
		
	    bOk = new JButton(bundle.getString("ok"),Utils.getIcon(ClientConfig.BTN_BG));
		bOk.setForeground(Color.WHITE);
		bOk.setFocusPainted(false);
		bOk.setBorderPainted(false);
		bOk.setContentAreaFilled(false);
		bOk.setVerticalTextPosition(AbstractButton.CENTER);
	    bOk.setHorizontalTextPosition(AbstractButton.CENTER);
	    
	    bOk.addActionListener(this);
		panel5.add(bOk,BorderLayout.CENTER);
		
		
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(panel4);
		panel.add(panel5);
		pane.add(panel);
		//---------
		//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
		setResizable(false);
		//---------
		
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bOk)
				reply = "ok";
			dispose();
		}
	}
	
	 class JComponentTableCellRenderer extends DefaultTableCellRenderer  {
   	  
		 public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
    	      boolean hasFocus, int row, int column) {
			 Component component =
	                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	 
	            setHorizontalAlignment(SwingConstants.CENTER);
	        return component;
    	  }
   	}
	 
	class MyRebuyInfoDialog extends JDialog implements ActionListener 
	{
		protected static Dimension screenSize;
		protected static Dimension frameSize;
		protected static Point framePos;
		ImageIcon icon;
		int hinc = -1;
		int winc = -1;
		JButton bOk;
		String reply = null;
		ResourceBundle bundle = Bundle.getBundle();
		
		
		public MyRebuyInfoDialog ( String title,
			String message) {
			super();
					screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frameSize = new Dimension(230, 180);
				framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
				setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
					
		Container pane = getContentPane();
		setTitle(title);
		setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
		setModal(true);
		icon = Utils.getIcon("images/dialog_plain.jpg");
		hinc = icon.getIconHeight();
		winc = icon.getIconWidth();
		
//		
		JPanel panel = new JPanel()
		 {
			public void paintComponent(Graphics g) {
				int w = getWidth();
				int h = getHeight();
				for (int i = 0; i < h + hinc; i = i + hinc)
					for (int j = 0; j < w + winc; j = j + winc)
						icon.paintIcon(this, g, j, i);
			}
		};
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(230, 180));
		panel.setSize(250, 350);
		//createEmptyBorder(top,left,bottom,right)
		panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
			
		
			
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setOpaque(false);
		panel1.add(new JLabel("Rebuy Info",JLabel.CENTER));
		
		
		JPanel panel3 = new JPanel(new BorderLayout());
		panel3.setOpaque(false);
		
	    bOk = new JButton(bundle.getString("ok"),Utils.getIcon(ClientConfig.BTN_BG));
		bOk.setForeground(Color.WHITE);
		bOk.setFocusPainted(false);
		bOk.setBorderPainted(false);
		bOk.setContentAreaFilled(false);
		bOk.setVerticalTextPosition(AbstractButton.CENTER);
	    bOk.setHorizontalTextPosition(AbstractButton.CENTER);
	    
	    bOk.addActionListener(this);
		panel3.add(bOk,BorderLayout.CENTER);
		
		
		panel.add(panel1);
		panel.add(panel3);
		pane.add(panel);
		//---------
		//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
		setResizable(false);
		//---------
		
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == bOk)
				reply = "ok";
			dispose();
		}
	}
