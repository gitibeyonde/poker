package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.agneya.util.Base64;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.ClientRoom;
import com.onlinepoker.Utils;
import com.onlinepoker.lobby.LobbyUserImp;
import com.onlinepoker.resources.Bundle;

public class MyPlayerNoteDialog extends JFrame implements ActionListener
{
	static Logger _cat = Logger.getLogger(MyPlayerNoteDialog.class.getName());
	protected static Dimension screenSize;
	protected static Dimension frameSize;
	protected static Point framePos;
	ImageIcon icon;
	int hinc = -1;
	int winc = -1;
	JButton bComment;
	JTextArea jTextArea;
	public String reply = "";
	ResourceBundle bundle = Bundle.getBundle();
	String _playerName;
	//public Map<String, String> map;
    public String getPlayerName(){
    	return _playerName;
    }
	
	//JWebBrowser webBrowser = null;
	
	public MyPlayerNoteDialog ( String title,
		String name) {
		super();
				screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frameSize = new Dimension(300, 300);
			framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
			setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
			this._playerName = name;
	//map  = ServerProxy.map;			
	Container pane = getContentPane();
	//setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
	Frame frames[] = ClientRoom.getFrames();
	int j=-1;
	for (int i = 0; i < frames.length; i++) {
		//System.out.println(frames[i].getName()+"--"+frames[i].getTitle()+"--"+frames[i].getSize());
		if(frames[i].getTitle().contains("")){j=i;break;}
	}
	if(j != -1)
	{
		//System.out.println("frame matched"+frames[j].getName());
		this.setLocationRelativeTo(frames[j]);
	}
	//setModalityType(ModalityType.MODELESS);
	setTitle(title);
	setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
	//setModal(true);
	icon = Utils.getIcon("images/dialog_plain.jpg");
	hinc = icon.getIconHeight();
	winc = icon.getIconWidth();
	this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		  reply = "closed";
		  dispose();
		}
	});	
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
	panel.setLayout(new GridLayout(3,1));
	panel.setOpaque(false);
	panel.setPreferredSize(new Dimension(600, 700));
	panel.setSize(700, 700);
	panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
	
	JLabel jLabel = new JLabel("Player Name: "+ name);
	jLabel.setForeground(Color.black);
	jTextArea =  new JTextArea(6, 30);
	jTextArea.setBackground(Color.black);
	jTextArea.setForeground(Color.white);
	jTextArea.setLineWrap(true);
	String prev_note = CredentialManager.getPlayerNoteInfo(name);
	_cat.info("prev_note "+prev_note);
	jTextArea.setText(prev_note);
	bComment = new JButton("Comment", Utils.getIcon(ClientConfig.BTN_BG));
	JPanel panel3 = new JPanel(new FlowLayout());
	panel3.setOpaque(false);
	panel3.setSize(80, 22);
	bComment = new JButton(bundle.getString("comment"),Utils.getIcon(ClientConfig.BTN_BG));
	bComment.setForeground(Color.WHITE);
	bComment.setFocusPainted(false);
	bComment.setBorderPainted(false);
	bComment.setContentAreaFilled(false);
	bComment.setVerticalTextPosition(AbstractButton.CENTER);
	bComment.setHorizontalTextPosition(AbstractButton.CENTER);
	bComment.addActionListener(this);
	panel3.add(bComment);
	panel.add(jLabel);
	panel.add(jTextArea);
	panel.add(panel3);
	pane.add(panel);
	//---------
	//setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
	setResizable(false);
	//---------
	reply = "opened";
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		//System.out.println(((JButton)source).getName());
		if (source == bComment)
		{
			_cat.info("inside MyPlayerNogeDialog actionPerformed");
			String keyName = "note."+_playerName;
			if(LobbyUserImp.map.get(keyName) != null){
				LobbyUserImp.map.remove(keyName);
			}
			LobbyUserImp.map.put("note."+_playerName, Base64.encodeString(jTextArea.getText()));
//			CredentialManager.savePlayerNotes();
//			Map<String, String> tmap = LobbyUserImp.map;
//			Set propertySet = tmap.entrySet();
//	           for (Object o : propertySet) {
//	             Map.Entry entry = (Map.Entry) o;
//	             String com = (String)entry.getValue();
//	             System.out.printf("%s = %s%n", entry.getKey(), Base64.decodeString(com));
//	           }
	           reply = "closed";
	           dispose();
		}
	}
	
  

	      
	      
	      
	   
	
}
