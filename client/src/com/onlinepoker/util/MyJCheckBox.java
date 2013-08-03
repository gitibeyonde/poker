package com.onlinepoker.util;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;


public class MyJCheckBox extends JCheckBox {
	private String label;
	
	public MyJCheckBox(String label,
					ImageIcon defIcon,
					ImageIcon selIcon) {
		super (label, defIcon);

		if (selIcon != null) {
			setPressedIcon(selIcon);
			setSelectedIcon(selIcon);
		}
		
		setForeground(new Color(255, 255, 255 ));//(196, 196, 196));
		
	}
}

