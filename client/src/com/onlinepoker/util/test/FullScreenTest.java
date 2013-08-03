package com.onlinepoker.util.test;

import javax.swing.*;

import com.onlinepoker.Painter;
import com.onlinepoker.util.MessageFactory;

import dj.nativeswing.swtimpl.NativeInterface;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;


public class FullScreenTest extends JFrame implements Painter{
boolean fullscreen;
static JFrame mainFrame;
JPanel panel;
JButton fs, md;

public FullScreenTest(){
super("FullScreenTest");
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

panel = new JPanel();
panel.setBackground(Color.blue);
panel.setPreferredSize(new java.awt.Dimension(640, 480));

//add buttons
fs = new JButton();
fs.setText("Fullscreen");
fs.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e){
// stuff
toggleFullscreen();
}
});
panel.add(fs);

md = new JButton();
md.setText("Modal Dialog");
md.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e){
// show a dialog
JOptionPane.showMessageDialog(panel, "Modal");
}
});
panel.add(md);

getContentPane().setLayout(new GridBagLayout());
getContentPane().add(panel, new GridBagConstraints());
pack();
setVisible(true);


}

private void toggleFullscreen() {
if (fullscreen) {

mainFrame.dispose();
setVisible(false);
getContentPane().removeAll();
dispose();
setUndecorated(false);
GraphicsEnvironment ge =
GraphicsEnvironment.getLocalGraphicsEnvironment(); 
GraphicsDevice gd = ge.getDefaultScreenDevice();
gd.setFullScreenWindow(null);


getContentPane().setLayout(new BorderLayout());
getContentPane().add(panel, BorderLayout.CENTER);
pack();
setVisible(true);

fullscreen=false;

} else {

Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
GraphicsEnvironment ge =
GraphicsEnvironment.getLocalGraphicsEnvironment(); 
GraphicsDevice gd = ge.getDefaultScreenDevice();
GraphicsConfiguration gc = gd.getDefaultConfiguration();

getContentPane().setLayout(new GridBagLayout());
getContentPane().add(panel, new GridBagConstraints());
mainFrame = new JFrame(gc);
mainFrame.setUndecorated(true);


int menuButtonHeight=20;
panel.setLocation(
(screenSize.width - panel.getWidth())/2,
Math.max(menuButtonHeight,
(screenSize.height - menuButtonHeight - panel.getHeight())/2));

mainFrame.getContentPane().setLayout(new GridBagLayout());
mainFrame.getContentPane().add(panel, new
GridBagConstraints());

gd.setFullScreenWindow(mainFrame);
if (gd.isDisplayChangeSupported()) {
gd.setDisplayMode(new DisplayMode(screenSize.width,
screenSize.height, 32, 0));
}

fullscreen=true;
}
}

public static void main(String[] arguments) {
//FullScreenTest fstest = new FullScreenTest();
	NativeInterface.open();   
    SwingUtilities.invokeLater(new Runnable() {   
      public void run() {   
    	  try {
    		  //String choice = MessageFactory.getHandHistoryWindow(null, "Instant Hand History", "Green Spades", 7059606);
    	  }
    	  catch (Exception e) {
			// TODO: handle exception
    	  }
      }
    });

}

@Override
public void paint(JComponent c, Graphics g) {
	// TODO Auto-generated method stub
	
}
}
