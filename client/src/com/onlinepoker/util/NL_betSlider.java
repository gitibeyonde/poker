package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;

public class NL_betSlider extends Component {
	JLabel min, pot, max;
	JFormattedTextField t;
	JSlider betSlider;
	
	private double smallBlind, bigBlind; //NOT IN USE
	private double minBet, potBet, maxBet; //REQUIRED to initialize
	private boolean isIntegerBet = false; //the bet control values are integer based or not (usually play money use int)
	
	private float sliderCalcVal; //slider calculator helper variable
	private static final int sliderMultipl = 100; // make sure the slider values are integer
	
	private ImagePanel betControl;
	
	public NL_betSlider(double minB, double potB, double maxB){
		super();

		minBet = minB;
		potBet = potB;
		maxBet = maxB;
		
		createControl();
	}
	
	public NL_betSlider(double minB, double potB, double maxB, boolean isIntegerValues){
		super();

		minBet = minB;
		potBet = potB;
		maxBet = maxB;
		
		isIntegerBet = isIntegerValues;
		
		createControl();
	}
	
	public NL_betSlider(double sblind, double bblind, double minB, double potB, double maxB, boolean isIntegerValues){
		super();
		
		smallBlind = sblind;
		bigBlind = bblind;

		minBet = minB;
		potBet = potB;
		maxBet = maxB;
		
		isIntegerBet = isIntegerValues;
		
		createControl();
	}
	
	private void createControl() {
		System.out.println("---in create Control----");
		ImagePanel imgPanel = new ImagePanel(
				Utils.getIcon("images/btn_add_me_waiting_list.png").getImage());//ClientConfig.IMG_SLIDER_BG
		imgPanel.setBounds(100, 100, 100, 60);

		// BETSLIDER
		betSlider = new JSlider(JSlider.HORIZONTAL,
				(int) (minBet * sliderMultipl), (int) (maxBet * sliderMultipl),
				(int) (minBet * sliderMultipl));
		// betSlider.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		betSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				sliderCalcVal = betSlider.getValue();
				sliderCalcVal = sliderCalcVal / 100;

				if (isIntegerBet)
					t.setText(formatInteger(sliderCalcVal));
				else
					t.setText(formatDecimal(sliderCalcVal));
			}
		});
		
		betSlider.setBounds(0, 45, 100, 10);


		//TextField
		if (isIntegerBet)
			t = new JFormattedTextField();
		else
			t = new JFormattedTextField();

		t.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				// handle the case if t value: < minBet || > maxBet
				sliderCalcVal = Float.parseFloat(t.getText());
				betSlider.setValue((int) (sliderCalcVal * sliderMultipl));
			}
		});

		t.setFont(new Font("Verdana", Font.PLAIN, 10));
		t.setForeground(Color.WHITE);
		t.setBackground(Color.BLACK);
		t.setHorizontalAlignment(JTextField.RIGHT);
		t.setBounds(2, 22, 95, 18);
		if (isIntegerBet)
			t.setText(formatInteger(minBet));
		else
			t.setText(formatDecimal(minBet));

		
		//MIN BETTING LABEL
		min = new JLabel("Min");
		min.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (isIntegerBet)
					t.setText(formatInteger(minBet));
				else
					t.setText(formatDecimal(minBet));
				betSlider.setValue((int) (minBet * sliderMultipl));
			}
		});
		min.setFont(new Font("Verdana", Font.BOLD, 10));
		min.setForeground(Color.WHITE);
		min.setBounds(7, -2, 25, 20);
		// min.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

		//POT BETTING LABEL
		pot = new JLabel("Pot");
		pot.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (isIntegerBet)
					t.setText(Integer.toString((int) potBet));
				else
					t.setText(formatDecimal(potBet));
				betSlider.setValue((int) (potBet * sliderMultipl));
			}
		});
		pot.setFont(new Font("Verdana", Font.BOLD, 10));
		pot.setForeground(Color.WHITE);
		pot.setBounds(38, -2, 25, 20);
		// pot.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

		//MAX BETTING LABEL
		max = new JLabel("Max"); //
		max.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (isIntegerBet)
					t.setText(Integer.toString((int) maxBet));
				else
					t.setText(formatDecimal(maxBet));
				betSlider.setValue((int) (maxBet * sliderMultipl));
			}
		});
		max.setFont(new Font("Verdana", Font.BOLD, 10));
		max.setForeground(Color.WHITE);
		max.setBounds(70, -2, 25, 20);
		// max.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

		imgPanel.add(betSlider);
		imgPanel.add(t);
		imgPanel.add(min);
		imgPanel.add(pot);
		imgPanel.add(max);
		
		betControl = imgPanel;
	}
	
	protected String formatDecimal(double value) {
		NumberFormat doubleFormat = new DecimalFormat("#0.00");
		String s = doubleFormat.format(value);
		return s;
	}

	protected String formatInteger(double value) {
		int i = (int) Math.round(value);
		NumberFormat integerFormat = new DecimalFormat("##");
		String s = integerFormat.format(i);
		return s;
	}
	
	public ImagePanel getBetControl() {
		return betControl;
	}
	
	public int getSliderValue() {
		return betSlider.getValue();
	}
	
	public void setSliderValue(int i) {
		betSlider.setValue(i);
	}
	
	public double getSliderMinBet() {
		return minBet;
	}
	
	public double getSliderMaxBet() {
		return maxBet;
	}
	
	public double getSliderPotBet() {
		return potBet;
	}
}
