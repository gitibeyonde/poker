package com.onlinepoker.util.test;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class FullscreenSwingTest extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FullscreenSwingTest test = new FullscreenSwingTest();
				test.activate(null);
			}
		});
	}

	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice gd = env.getDefaultScreenDevice();
	JButton showDialogButton = new JButton("Show Dialog");
	
	public FullscreenSwingTest() {
		super();
		
		showDialogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog( FullscreenSwingTest.this, "Message" );
				dialog.getContentPane().add(new JLabel("This is a label in a dialog."));
				dialog.setModal(true);
				dialog.pack();
				dialog.setLocationRelativeTo(FullscreenSwingTest.this);
				dialog.setVisible(true);
			}
		});
		getContentPane().add(showDialogButton);
	}
	
	public void activate(DisplayMode displayMode) {
		if(gd.isFullScreenSupported()==false)
			throw new UnsupportedOperationException();
		
		gd.setFullScreenWindow(this);
		Rectangle bounds = env.getMaximumWindowBounds();
		setBounds(bounds);
	}
}