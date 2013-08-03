package com.onlinepoker.util.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
public class DialogTest {
public static void main(String[] args) 
{
	
//	JFrame.setDefaultLookAndFeelDecorated(true);
//	JDialog.setDefaultLookAndFeelDecorated(true);
	// Determine if full-screen mode is supported directly
	GraphicsEnvironment ge = GraphicsEnvironment
	.getLocalGraphicsEnvironment();
	GraphicsDevice gs = ge.getDefaultScreenDevice();
	// Create a window for full-screen mode; add a button to leave
	// full-screen mode
	final JFrame frame = new JFrame(gs.getDefaultConfiguration());
	// creating panels
	JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	// inserts into bottomPanel
	JButton dialogButton = new JButton("Show dialog");
	JButton exitButton = new JButton("Exit");
	bottomPanel.add(dialogButton);
	bottomPanel.add(exitButton);
	// inserts panels
	frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	// create a button listener to show a dialog
	dialogButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent evt) {
		//JDialog d = new JDialog(frame,"sdfgdfs");

		JOptionPane.showInternalMessageDialog(frame.getContentPane(), "Error",
		"Here is an error", JOptionPane.ERROR_MESSAGE);
		frame.toFront();
		frame.setIgnoreRepaint(true);
		frame.requestFocus();
		frame.setExtendedState(frame.getExtendedState() & ~JFrame.ICONIFIED);
		  frame.setVisible(true);
		}
	});
	// Create a button listener that leaves full-screen mode
	exitButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent evt) {
	// Return to normal windowed mode
		GraphicsEnvironment ge = GraphicsEnvironment
		.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		gs.setFullScreenWindow(null);
		System.exit(0);
		}
		});
		try {
		if (gs.isFullScreenSupported()) {
		gs.setFullScreenWindow(frame);
		}
		} catch (Exception ex) {
		System.out.println(ex);
		}
		frame.setVisible(true);
	}
}
