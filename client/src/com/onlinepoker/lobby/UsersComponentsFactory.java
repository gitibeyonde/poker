package com.onlinepoker.lobby;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;

/** Factory for creating all user's component on Lobby
 * 
 * @author Halt
 */
public class UsersComponentsFactory {

//  public static JButton createJBCashier(ActionListener listener) {
//	ImageIcon anImage;
//	JButton jbCashier;
//	
//	anImage =
//		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_UP);
//	jbCashier = new JButton(anImage);
//		
//	jbCashier.setRolloverEnabled(true);
//	anImage =
//		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_PRESSED);
//	jbCashier.setPressedIcon(anImage);
//	anImage =
//		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_DOWN);
//	jbCashier.setRolloverIcon(anImage);
//	anImage =
//		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_DE);
//	jbCashier.setDisabledIcon(anImage);
//	jbCashier.setFocusPainted(false);
//	jbCashier.setBorderPainted(false);
//	jbCashier.setContentAreaFilled(false);
//	jbCashier.setMargin(new Insets(0, 0, 0, 0));
//	jbCashier.addActionListener(listener);
//	jbCashier.setBounds(360, 502, 100, 30);
//	
//	return jbCashier;
//  }


	/*public static JButton createJBSitTable(ActionListener listener) {
		ImageIcon anImage;
		JButton jbSitTable;

		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_UP);
		jbSitTable = new JButton(anImage);
		jbSitTable.setRolloverEnabled(true);
		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_PRESSED);
		jbSitTable.setPressedIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_DOWN);
		jbSitTable.setRolloverIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_DE);
		jbSitTable.setDisabledIcon(anImage);
		jbSitTable.setFocusPainted(false);
		jbSitTable.setBorderPainted(false);
		jbSitTable.setContentAreaFilled(false);
		jbSitTable.setMargin(new Insets(0, 0, 0, 0));
		jbSitTable.addActionListener(listener);
		jbSitTable.setBounds(100, 502, 100, 30);
		jbSitTable.setEnabled(false);
		
		return jbSitTable;
	}*/
	
	/*public static JToggleButton createJBWait(ActionListener listener) {
		ImageIcon anImage;
		JToggleButton jbWait;

		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_EN);
		jbWait = new JToggleButton(anImage);
		jbWait.setRolloverEnabled(true);
		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_MO);
		jbWait.setRolloverIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_PR);
		jbWait.setPressedIcon(anImage);
		jbWait.setSelectedIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_DE);
		jbWait.setDisabledIcon(anImage);
		jbWait.setFocusPainted(false);
		jbWait.setBorderPainted(false);
		jbWait.setContentAreaFilled(false);
		jbWait.setMargin(new Insets(0, 0, 0, 0));
		jbWait.setBounds(230, 502, 100, 30);
		jbWait.addActionListener(listener);
		jbWait.setEnabled(false);

		return jbWait;
	}*/
	
	public static JButton createTournyRegButton(ActionListener listener) {
        ImageIcon anImage;
        JButton jb;

        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_UP);
        jb = new JButton("     Register Now",anImage);
        //jb.setRolloverEnabled(true);
//        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_PRESSED);
//        jb.setPressedIcon(anImage);
//        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_DOWN);
//        jb.setRolloverIcon(anImage);
//        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_DE);
//        jb.setDisabledIcon(anImage);
        jb.setForeground(Color.WHITE);
        jb.setHorizontalTextPosition(JButton.CENTER );
        jb.setFont(new Font("Myriad Web", Font.BOLD, 22));
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.addActionListener(listener);
        jb.setBounds(15, 10, 207, 49);
        jb.setEnabled(true);

        return jb;
	}


	public static JButton createMyTableButton(ActionListener listener) {
        ImageIcon anImage;
        JButton jb;

        anImage = Utils.getIcon(ClientConfig.IMG_MYTABLE_UP);
        jb = new JButton(anImage);
        jb.setRolloverEnabled(true);
        anImage = Utils.getIcon(ClientConfig.IMG_MYTABLE_PRESSED);
        jb.setPressedIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_MYTABLE_DOWN);
        jb.setRolloverIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_MYTABLE_DE);
        jb.setDisabledIcon(anImage);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.addActionListener(listener);
        jb.setBounds(570, 495, 159, 27);
        jb.setEnabled(true);

        return jb;
	}
	
	public static JButton createMainLobbyButton(ActionListener listener) {
        ImageIcon anImage;
        JButton jb;

        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_MAIN_LOBBY_BG);
        jb = new JButton(anImage);
        jb.setRolloverEnabled(true);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_MAIN_LOBBY_BG);
        jb.setPressedIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_MAIN_LOBBY_BG);
        jb.setRolloverIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_MAIN_LOBBY_BG);
        jb.setDisabledIcon(anImage);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.addActionListener(listener);
        jb.setBounds(630, 138, 112, 30);
        jb.setEnabled(true);

        return jb;
	}
	
	public static JButton createCashierButton(ActionListener listener) {
        ImageIcon anImage;
        JButton jb;

        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_CASHIER_BG);
        jb = new JButton(anImage);
        jb.setRolloverEnabled(true);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_CASHIER_BG);
        jb.setPressedIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_CASHIER_BG);
        jb.setRolloverIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_CASHIER_BG);
        jb.setDisabledIcon(anImage);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.addActionListener(listener);
        jb.setBounds(500, 138, 112, 30);
        jb.setEnabled(true);

        return jb;
	}
	
	public static JButton createObserveTableButton(ActionListener listener) {
        ImageIcon anImage;
        JButton jb;

        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OBSERVE_TABLE_BG);
        jb = new JButton(anImage);
        jb.setRolloverEnabled(true);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OBSERVE_TABLE_BG);
        jb.setPressedIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OBSERVE_TABLE_BG);
        jb.setRolloverIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OBSERVE_TABLE_BG);
        jb.setDisabledIcon(anImage);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.addActionListener(listener);
        jb.setBounds(570, 525, 159, 27);
        jb.setEnabled(true);

        return jb;
	}
	
	public static JButton createSatelliteLobbyButton(ActionListener listener) {
        ImageIcon anImage;
        JButton jb;

        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OPEN_SATELLITE_LOBBY_BG);
        jb = new JButton(anImage);
        jb.setRolloverEnabled(true);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OPEN_SATELLITE_LOBBY_BG);
        jb.setPressedIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OPEN_SATELLITE_LOBBY_BG);
        jb.setRolloverIcon(anImage);
        anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_OPEN_SATELLITE_LOBBY_BG);
        jb.setDisabledIcon(anImage);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.addActionListener(listener);
        jb.setBounds(540, 502, 112, 30);
        jb.setEnabled(true);

        return jb;
	}
	
	
}
