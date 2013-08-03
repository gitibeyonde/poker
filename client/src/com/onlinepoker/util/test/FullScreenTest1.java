package com.onlinepoker.util.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;


public class FullScreenTest1 extends JFrame {
	boolean fullscreen;
	JFrame mainFrame;
	JPanel panel;
	JButton fs, md;
	
	public FullScreenTest1(){
	super("FullScreenTest");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	panel = new JPanel();
	panel.setBackground(Color.blue);
	panel.setPreferredSize(new java.awt.Dimension(640, 480));
	
	//add buttons
	fs = new JButton();
	fs.setText("Fullscreen");
	fs.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e){
	// stuff
	toggleFullscreen();
	}
	});
	panel.add(fs);
	
	md = new JButton();
	md.setText("Modal Dialog");
	md.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e){
	// show a dialog
	    JOptionPane.showMessageDialog(panel, "Modal");
	}
	});
	panel.add(md);
	
	getContentPane().setLayout(new GridBagLayout());
	getContentPane().add(panel, new GridBagConstraints());
	pack();
	setVisible(true);


}

private void toggleFullscreen() {
	if (fullscreen) {
	
	mainFrame.dispose();
	setVisible(false);
	getContentPane().removeAll();
	dispose();
	setUndecorated(false);
	GraphicsEnvironment ge =
	GraphicsEnvironment.getLocalGraphicsEnvironment(); 
	GraphicsDevice gd = ge.getDefaultScreenDevice();
	gd.setFullScreenWindow(null);
	
	
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(panel, BorderLayout.CENTER);
	pack();
	setVisible(true);
	
	fullscreen=false;
	
	} else {
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	GraphicsEnvironment ge =
	GraphicsEnvironment.getLocalGraphicsEnvironment(); 
	GraphicsDevice gd = ge.getDefaultScreenDevice();
	GraphicsConfiguration gc = gd.getDefaultConfiguration();
	
	getContentPane().setLayout(new GridBagLayout());
	getContentPane().add(panel, new GridBagConstraints());
	mainFrame = new JFrame(gc);
	mainFrame.setUndecorated(true);
	
	
	int menuButtonHeight=20;
	panel.setLocation(
	(screenSize.width - panel.getWidth())/2,
	Math.max(menuButtonHeight,
	(screenSize.height - menuButtonHeight - panel.getHeight())/2));
	
	mainFrame.getContentPane().setLayout(new GridBagLayout());
	mainFrame.getContentPane().add(panel, new
	GridBagConstraints());
	
	gd.setFullScreenWindow(mainFrame);
	if (gd.isDisplayChangeSupported()) {
	gd.setDisplayMode(new DisplayMode(screenSize.width,
	screenSize.height, 32, 0));
	}
	
	fullscreen=true;
	}
}

public static void main(String[] arguments) {
	new FullScreenTest();
	}
}
