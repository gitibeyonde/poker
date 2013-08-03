package com.onlinepoker.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;

public class MyDialog extends JDialog {
    public MyDialog(JFrame frame) throws HeadlessException {
        super(frame, "Parent dialog", false);

        initComponents();
        setPreferredSize(new Dimension(640, 480));
        pack();
    }
    public MyDialog(RoomSkin skin, String title,
			String message, JFrame frame, ServerProxy lobbyServer)throws HeadlessException {
    	super(frame, title, false);
        initComponents();
        setPreferredSize(new Dimension(250, 375));
//        MyStatsDialog1 myInputDialog = new MyStatsDialog1(skin, title, message,  frame, true, lobbyServer);
//		myInputDialog.setVisible(true);
//		myInputDialog.setLocationRelativeTo(frame);
    }
    
    
    private void initComponents() {
        JPanel panel = new JPanel(new FlowLayout(0, 0, FlowLayout.CENTER));

        JButton jButton = new JButton("Show dialog");

        panel.add(jButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.NORTH);
    }
    
    class MyStatsDialog1 extends JInternalFrame implements ActionListener 
	{
		public MyStatsDialog1(RoomSkin skin, String title, String message,
				JFrame frame, boolean b, ServerProxy lobbyServer) {
			// TODO Auto-generated constructor stub
			JPanel p = new JPanel();
			p.add(new JLabel("dsafasdfsa"));
		}

		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
    
}


