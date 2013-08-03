package com.onlinepoker.util;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;

//import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;

public class NL_betSliderTest {
	
	public NL_betSliderTest() {
		// TODO Auto-generated constructor stub
		JFrame frame = new JFrame("Poker Bet sdsSlider Control Test");
		JPanel panel = new JPanel();

		panel.setBackground(Color.white);
		panel.setLayout(new BorderLayout()); //absolute positioning

		NL_betSlider betSlider = new NL_betSlider(0.05f, 2.04f, 5.04f);
		
		betSlider.setBounds(10, 10, 100, 100);
		panel.add(betSlider.getBetControl(), BorderLayout.CENTER);
		panel.add(createPanel(),BorderLayout.CENTER);
		
		frame.add(panel);
		frame.setSize(800, 600);
		// frame.setUndecorated(true);
		// frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public JComponent createPanel()
	{
		JPanel p = new JPanel();
		JLabel jlHand; 
		JLabel jlStack; 
		  JLabel jlText;
		  Object value = "6deedsfgpnl";
		  p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		  p.setOpaque(false);
		if( value.toString().startsWith("2")){
      		jlHand = new JLabel(Utils.getIcon(ClientConfig.ICON_2HANDED));
      	}
      	else if(value.toString().startsWith("6")){
      		jlHand = new JLabel(Utils.getIcon(ClientConfig.ICON_6HANDED));
      	}
      	else jlHand = new JLabel("   ");
		if( value.toString().contains("deep")){
      		jlStack = new JLabel(Utils.getIcon(ClientConfig.ICON_DEEPSTACK));
      	}
      	else if(value.toString().contains("shallow")){
      		jlStack = new JLabel(Utils.getIcon(ClientConfig.ICON_SPEED));
      	}
      	else jlStack = new JLabel("   ");
      	jlText = new JLabel(value.toString());
      	jlHand.setForeground(Color.BLACK);
      	jlText.setForeground(Color.BLACK);
      	p.add(jlHand);
      	p.add(jlStack);
      	p.add(jlText);
      	System.out.println("p"+p);
		return p;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
		try {
			UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		new NL_betSliderTest();
	}

}
