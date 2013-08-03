package com.onlinepoker.util.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

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
import java.util.Timer;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.server.ServerProxy;

public class ReconnectingDialogTest extends JInternalFrame implements ActionListener 
{
	JComboBox  gameType;
	ImageIcon icon;
	int hinc = -1;
	int winc = -1;
	JTextField field;
	JButton bOk, bClose, bReset;
	String reply = null;
	Timer t=null;
	JLabel statsLabel = null;
	JPanel statusPanel = new JPanel();
	int count = 20;
	protected ServerProxy lobbyServer;
	protected boolean unique;
	protected static Dimension screenSize;
	protected static Dimension frameSize;
	protected static Point framePos;
	static {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = new Dimension(400, 200);
	}
	public ReconnectingDialogTest() {
		super();
		//Container pane = getContentPane();
		//setTitle("title");
	icon = Utils.getIcon("images/dialog_plain.jpg");
	hinc = icon.getIconHeight();
	winc = icon.getIconWidth();
	
	JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(400,200);
    f.setLocation(200,200);
    f.setVisible(true);
	JPanel panel = new JPanel()
	 {
//		public void paintComponent(Graphics g) {
//			int w = getWidth();
//			int h = getHeight();
//			for (int i = 0; i < h + hinc; i = i + hinc)
//				for (int j = 0; j < w + winc; j = j + winc)
//					icon.paintIcon(this, g, j, i);
//		}
	};
	panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
	panel.setOpaque(false);
	panel.setPreferredSize(new Dimension(400, 200));
	panel.setSize(400, 200);
	panel.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
	
	
	
	statusPanel.setOpaque(false);
	statsLabel = new JLabel("Trying to reconnect.....",JLabel.LEFT);
	statsLabel.setOpaque(false);
	statusPanel.add(statsLabel);
	statsLabel.setVisible(true);
	JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
	panel3.setOpaque(false);
    bClose = new JButton("close",Utils.getIcon(ClientConfig.BTN_BG));
	bClose.setForeground(Color.WHITE);
	bClose.setFocusPainted(false);
	bClose.setBorderPainted(false);
	bClose.setContentAreaFilled(false);
	bClose.setVerticalTextPosition(AbstractButton.CENTER);
    bClose.setHorizontalTextPosition(AbstractButton.CENTER);
	bClose.addActionListener(this);
	panel3.add(bClose);
	
	statsLabel.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(statusPanel);
	panel.add(panel3);
	f.getContentPane().add(panel);
	//---------
	//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
	setResizable(false);
	//---------
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		//System.out.println(source);
		if (source == bClose)
		{
			System.exit(-1);
		}
	}
	
	public static void main(String[] args){
		ReconnectingDialogTest rct = new ReconnectingDialogTest();
	}
	
}
