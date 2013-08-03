package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.SharedConstants;
import com.onlinepoker.Utils;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;

public class MyStatsDialog extends JInternalFrame implements ActionListener 
{
	JComboBox  gameType;
	ImageIcon icon;
	int hinc = -1;
	int winc = -1;
	JTextField field;
	JButton bOk, bClose, bReset;
	ResourceBundle bundle = Bundle.getBundle();
	String reply = null;
	protected ServerProxy lobbyServer;
	protected boolean unique;
	protected static Dimension screenSize;
	protected static Dimension frameSize;
	protected static Point framePos;
	private Statistics _stats;
	static {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = new Dimension(300, 400);
	}
	public MyStatsDialog (RoomSkin skin, String title,
		String message,JFrame frame, boolean unique, ServerProxy lobbyServer, Statistics _sts) {
		super();
		framePos = new Point((frame.getWidth() - frameSize.width)/2, (frame.getHeight() - frameSize.height)/2);
		this._stats = _sts;
//		setLocationRelativeTo(frame);
//		setIgnoreRepaint(false);
	Container pane = getContentPane();
	setTitle(title);
//	setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
//	setModal(true);
	icon = Utils.getIcon("images/dialog_plain.jpg");
	hinc = icon.getIconHeight();
	winc = icon.getIconWidth();
	
	Object[] listData = {"Hold'em","Omaha","Stud"};
	
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
	panel.setPreferredSize(new Dimension(250, 375));
	panel.setSize(250, 375);
	panel.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
	
	JPanel gameTypePanel = new JPanel();
	gameTypePanel.setLayout(new BoxLayout(gameTypePanel, BoxLayout.X_AXIS));
	gameTypePanel.setOpaque(false);
	gameTypePanel.add(new JLabel("Game Type: "));
	gameType= new JComboBox(listData);
	gameType.setSelectedIndex(0);
	//gameType.addActionListener(this);
	gameTypePanel.setSize(150, 30);
	gameType.setOpaque(false);
	gameTypePanel.add(gameType);
	
	JPanel statusPanel = new JPanel();
	statusPanel.setOpaque(false);
	JLabel statsLabel = new JLabel("Statistics for this table",JLabel.LEFT);
	statsLabel.setOpaque(false);
	statsLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
	statusPanel.add(statsLabel);
	
	JLabel space1 = new JLabel(" ");
	JLabel space2 = new JLabel(" ");
	JLabel space3 = new JLabel(" ");
	
	
	JPanel panel1 = new JPanel(new GridLayout(5, 3));
	panel1.setOpaque(false);
	panel1.add(new JLabel("Street"));
	panel1.add(new JLabel("Saw"));
	panel1.add(new JLabel("Saw/Total"));
	panel1.add(new JLabel("Flop"));
	panel1.add(new JLabel(""+_sts.saw_flop));
	panel1.add(new JLabel(_sts.saw_flop+"%"));
	panel1.add(new JLabel("Turn"));
	panel1.add(new JLabel(""+_sts.saw_turn));
	panel1.add(new JLabel(_sts.saw_turn+"%"));
	panel1.add(new JLabel("River"));
	panel1.add(new JLabel(""+_sts.saw_river));
	panel1.add(new JLabel(_sts.saw_river+"%"));
	panel1.add(new JLabel("Showdown"));
	panel1.add(new JLabel(""+_sts.saw_showdown));
	panel1.add(new JLabel("0%"));
	
	JPanel panel2 = new JPanel(new GridLayout(6, 4));
	panel2.setOpaque(false);
	panel2.add(new JLabel("Street"));
	panel2.add(new JLabel("Won"));
	panel2.add(new JLabel("Won/Loss"));
	panel2.add(new JLabel("Won/Total"));
	panel2.add(new JLabel("Pre-Flop"));
	panel2.add(new JLabel(""+_sts.won_preflop));
	panel2.add(new JLabel(""+SharedConstants.doubleToString(_sts.won_amt_preflop)));
	panel2.add(new JLabel("0%"));
	panel2.add(new JLabel("Flop"));
	panel2.add(new JLabel(""+_sts.won_flop));
	panel2.add(new JLabel(""+SharedConstants.doubleToString(_sts.won_amt_flop)));
	panel2.add(new JLabel("0%"));
	panel2.add(new JLabel("Turn"));
	panel2.add(new JLabel(""+_sts.won_turn));
	panel2.add(new JLabel(""+SharedConstants.doubleToString(_sts.won_amt_turn)));
	panel2.add(new JLabel("0%"));
	panel2.add(new JLabel("River"));
	panel2.add(new JLabel(""+_sts.won_river));
	panel2.add(new JLabel(""+SharedConstants.doubleToString(_sts.won_amt_river)));
	panel2.add(new JLabel("0%"));
	panel2.add(new JLabel("Showdown"));
	panel2.add(new JLabel(""+_sts.won_showdown));
	panel2.add(new JLabel(""+SharedConstants.doubleToString(_sts.won_amt_showdown)));
	panel2.add(new JLabel("0%"));
	
	
	JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
	panel3.setOpaque(false);
	bReset = new JButton(bundle.getString("reset"),Utils.getIcon(ClientConfig.BTN_BG));
    bReset.setForeground(Color.WHITE);
    bReset.setFocusPainted(false);
    bReset.setBorderPainted(false);
    bReset.setContentAreaFilled(false);
    bReset.setVerticalTextPosition(AbstractButton.CENTER);
    bReset.setHorizontalTextPosition(AbstractButton.CENTER);
    
    bClose = new JButton(bundle.getString("close"),Utils.getIcon(ClientConfig.BTN_BG));
	bClose.setForeground(Color.WHITE);
	bClose.setFocusPainted(false);
	bClose.setBorderPainted(false);
	bClose.setContentAreaFilled(false);
	bClose.setVerticalTextPosition(AbstractButton.CENTER);
    bClose.setHorizontalTextPosition(AbstractButton.CENTER);
    
    bReset.addActionListener(this);
	bClose.addActionListener(this);
	panel3.add(bReset);
	panel3.add(bClose);
	
	statsLabel.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(gameTypePanel);
	panel.add(space1);
	panel.add(statusPanel);
	panel.add(space2);
	panel.add(panel1);
	panel.add(space3);
	panel.add(panel2);
	panel.add(panel3);
	pane.add(panel);
	//---------
	setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
	setResizable(false);
	//---------
//	if (unique)
//	MessageFactory.dialog = this;
	//////////////////////////////////////////////////////////////////setVisible(true);
//	frameSize = new Dimension(350, 400);
//	framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (unique)
			MessageFactory.dialog = null;
		if (source == bReset)
		{
			reply = "reset";
			_stats.ressetStats();
		}
		else if (source == bClose)
		{
			reply = "close";
			
		}
		dispose();
		
		
	}
	
}
