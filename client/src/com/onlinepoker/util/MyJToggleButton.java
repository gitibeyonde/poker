package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.onlinepoker.Utils;


public class MyJToggleButton extends JToggleButton {

	private int h;
	private int w;
	private String label;
	
	public MyJToggleButton (String label, 
							ImageIcon defIcon, 
							ImageIcon pressIcon, 
							ImageIcon rollIcon, 
							ImageIcon disIcon) {
		super (defIcon);
		this.label = label;
		w = defIcon.getIconWidth();
		h = defIcon.getIconHeight();
		
		if (pressIcon != null) {
			setPressedIcon(pressIcon);
			setSelectedIcon(pressIcon);
		}
		
		if (rollIcon != null) {
			setRolloverEnabled(true);
			setRolloverIcon(rollIcon);
		}
		
		if (disIcon != null)
			setDisabledIcon(disIcon);

		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setMargin(new Insets(0, 0, 0, 0));
		setSize(w, h);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

		
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		if ( (model.isPressed() && model.isArmed() ) || model.isSelected()) 
//			g.setColor(Color.black);
//		else
			g.setColor(Color.white);
		g.setFont(Utils.moveButtonFont);
		FontMetrics fm = g.getFontMetrics();
        Rectangle2D area = fm.getStringBounds(label, g);
        g.drawString(label, (int)(getWidth() - area.getWidth())/2,(int)(getHeight() + area.getHeight())/2 - 4);
    
	}
	
	public void setText (String text) {
		label = text;
		repaint();	
	}
	
	public String getTText () {
		return label;
	}
/*	public String getText () {
		return label;
	}
*/
	public Dimension getPreferredSize() {
		return new Dimension(w, h);
	}
		
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
			
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	public void setPreferredSize() {}
	public void setMinimumSize() {}
	public void setMaximumSize() {}
}
