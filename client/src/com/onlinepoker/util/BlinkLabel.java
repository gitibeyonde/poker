package com.onlinepoker.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;

public class BlinkLabel extends JLabel {
  private static final long serialVersionUID = 1L;
  protected static Dimension screenSize;
	protected static Dimension frameSize;
	protected static Point framePos;
	public boolean _open = false;
  int count = 40; //15 sec to dispose the frame
  JFrame frame = null;
  private static final int BLINKING_RATE = 1000; // in ms
  private boolean blinkingOn = true;
  Timer timer = null;
  JButton bClose;
  static {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = new Dimension(300, 150);
	}
 
  public BlinkLabel(String text) {
   super(text);
    framePos = new Point((int)(screenSize.getWidth() - frameSize.width)/2, (int)(screenSize.getHeight() - frameSize.height)/2);
    timer = new Timer( BLINKING_RATE , new TimerListener(this));
    timer.setInitialDelay(0);
    timer.start();
  }
  
  public void setBlinking(boolean flag) {
    this.blinkingOn = flag;
  }
  public boolean getBlinking(boolean flag) {
    return this.blinkingOn;
  }

  private class TimerListener implements ActionListener {
    private BlinkLabel bl;
    private Color bg;
    private Color fg;
    private boolean isForeground = true;
    
    public TimerListener(BlinkLabel bl) {
      this.bl = bl;
      fg = bl.getForeground();
      bg = bl.getBackground();
    }
 
    public void actionPerformed(ActionEvent e) {
      if (bl.blinkingOn) {
        if (isForeground) {
          bl.setForeground(fg);
        }
        else {
          bl.setForeground(new Color(255,0,0));
        }
        isForeground = !isForeground;
      }
      else {
        if (isForeground) {
          bl.setForeground(fg);
          isForeground = false;
        }
      }
      count--;
      if(count== 0)
      {
    	timer.stop();  
    	closeBox();
      }
    }
  }
  
  public void createAndShowUI() {
    frame = new JFrame("Blueace Poker");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setUndecorated(true);

    final BlinkLabel bl = new BlinkLabel("Trying to reconnect....");
    bl.setFont(new Font("Verdana", Font.BOLD, 16));
    JPanel panelmain = new JPanel();
    panelmain.setLayout(new BoxLayout(panelmain,BoxLayout.Y_AXIS));
    panelmain.setOpaque(false);
    panelmain.setPreferredSize(new Dimension(300, 150));
    panelmain.setSize(300, 150);
    panelmain.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
    
    JPanel panel = new JPanel();
	panel.setOpaque(false);
	panel.setPreferredSize(new Dimension(300, 150));
	panel.setSize(150, 100);
	panel.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
	panel.add(bl);
    
    JPanel btnPanel = new JPanel();
    btnPanel.setOpaque(false);
    bClose = new JButton("Cancel",Utils.getIcon(ClientConfig.BTN_BG));
	bClose.setFocusPainted(false);
	bClose.setBorderPainted(false);
	bClose.setContentAreaFilled(false);
	bClose.setVerticalTextPosition(AbstractButton.CENTER);
    bClose.setHorizontalTextPosition(AbstractButton.CENTER);
	bClose.addActionListener( new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.exit(-1);
			closeBox();
		}
	});
	btnPanel.add(bClose);
	
	panelmain.add(panel);
	panelmain.add(btnPanel);
	frame.add(panelmain);
	frame.setSize(300,150);
	frame.setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    this._open = true;

  }
  public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		//System.out.println(source);
		if (source == bClose)
		{
			_open = false;
			System.exit(-1);
		}
	}
  public void closeBox(){
	  this._open = false;
	  if(timer.isRunning()){
		  timer.stop();
	  }
	  //System.out.println("frame= "+frame);
	  if(frame != null)
	  frame.dispose();
	  System.exit(-1);
  }
  
  public void bringToFront(){
	  if(_open == true)
	  frame.toFront();
  }
  public static void main(String[] args)  {
//    java.awt.EventQueue.invokeLater(new Runnable(){
//      public void run(){
//        createAndShowUI();
//      }
//    });
	  new BlinkLabel("trying...").createAndShowUI();
  }
  // ---
}
