package com.onlinepoker.util.test;
import java.awt.BorderLayout;

import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.onlinepoker.Utils;

public class TestJSlider {
	public static void main(String args[]) {
	    JFrame frame = new JFrame("Tick Slider");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    // No Ticks
	    JSlider jSliderOne = new JSlider();
	    Icon icon = Utils.getIcon("images/slider_line.png");
	    UIDefaults defaults = UIManager.getDefaults();
	    defaults.put("Slider.horizontalThumbIcon", icon);

	    frame.add(jSliderOne, BorderLayout.NORTH);
	    frame.setSize(300, 200);
	    frame.setVisible(true);
	  }
	}
