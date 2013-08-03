package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.onlinepoker.Utils;


public class MyJButton extends JButton {

	private int h;
	private int w;
	private String label;

	public MyJButton(String label,
					ImageIcon defIcon,
					ImageIcon pressIcon,
					ImageIcon rollIcon,
					ImageIcon disIcon) {
		super (defIcon);
		this.label = label;
		w = defIcon.getIconWidth();
		h = defIcon.getIconHeight();

		if (pressIcon != null)
			setPressedIcon(pressIcon);

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
		setHorizontalAlignment(JButton.CENTER);
		setVerticalAlignment(JButton.CENTER);
                setVerticalTextPosition(JButton.CENTER);
                setHorizontalTextPosition(JButton.CENTER);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		if (model.isPressed() && model.isArmed())
//			g.setColor(Color.black);
//		else
			g.setColor(Color.white);
		g.setFont(Utils.moveButtonFont);
		//g.drawString(label, 15, h*3/5);
		FontMetrics fm = g.getFontMetrics();
        Rectangle2D area = fm.getStringBounds(label, g);
        g.drawString(label, (int)(getWidth() - area.getWidth())/2,(int)(getHeight() + area.getHeight())/2 - 4);
    
	}

	public void setLabel (String text) {
		label = text;
		repaint();
	}

	public String getLabel () {
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

