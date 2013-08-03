package com.onlinepoker.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

class ImagePanel extends JPanel {

		/**
		 * 
		 */
		//private static final long serialVersionUID = 7676569035800034980L;
		private Image img;

		@SuppressWarnings("unused")
		public ImagePanel(String img) {
			this(new ImageIcon(img).getImage());
		}
		
		public ImagePanel() {
			
		}

		public ImagePanel(Image img) {
			this.img = img;
			Dimension size = new Dimension(img.getWidth(null),
					img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}

		public void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}