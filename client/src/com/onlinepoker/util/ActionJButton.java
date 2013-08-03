package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.onlinepoker.Utils;


public class ActionJButton extends JButton {

	private int h;
	private int w;
	private String label;

	public ActionJButton(String label,
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
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.BOTTOM);
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.CENTER);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (model.isPressed() && model.isArmed())
			g.setColor(Color.white);
		else
			g.setColor(Color.black);
		g.setFont(Utils.bigButtonFont);
		g.drawString(label, 18, 19/*w/3, h*3/5*/);
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

