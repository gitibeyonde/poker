package com.onlinepoker.lobby;

import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import com.onlinepoker.exceptions.AuthenticationException;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;

public interface LobbyFrameInterface {

	/** background for frame */
	public ImageIcon getBackground();

	/** LookAndFeel for frame */
	public SyntheticaBlackEyeLookAndFeel getLookAndFeel();

	/** menu for frame */
	public JMenuBar getLobbyMenu();

	public JMenuBar getLoginMenu();

	
	/** all other visual components for frame */
	public Object[] getComponents();

	/**  window clising event  */
	public WindowListener getWindowCloseEvent();

	/** call after constructor */
	public void init(JFrame frame);

	/** for dummy login */
	public void dummyLogin();
	
	/** for login */
	public void login(String login, char[] password)
				throws AuthenticationException;

    public String getLoginMessage();

    public JFrame getFrame();
}
