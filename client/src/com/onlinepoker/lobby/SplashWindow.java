package com.onlinepoker.lobby;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.resources.Bundle;




public class SplashWindow
    extends JWindow {
	public static final String VERSION_LABEL = "v1.01.17";
	  
	int i=0;
	Timer timer;
	boolean _connected = false;
	final static int interval = 100;
	public JLabel statusLabel;
	private JLabel labelVersion;
	private ResourceBundle bundle;
	private DocumentBuilderFactory factory;
	private DocumentBuilder parser;
	private org.w3c.dom.Document dom;
	
  public SplashWindow(Frame f) {
    super(f);
    
    final ImageIcon m_image =
        Utils.getIcon(ClientConfig.IMG_SPLASH);
    final ImageIcon m_splash_loaderbar_bg =
        Utils.getIcon(ClientConfig.IMG_SPLASH_LOADERBARBG);
    final ImageIcon m_splash_loaderbar =
        Utils.getIcon(ClientConfig.IMG_SPLASH_LOADERBAR);

    bundle = Bundle.getBundle();
    JLabel labelLoaderBarbg = new JLabel(m_splash_loaderbar_bg);

    
    labelLoaderBarbg.setBounds(35, 247, 205, 16);
    //getContentPane().add(labelLoaderBarbg);
    JLabel labelLoaderBar = new JLabel(m_splash_loaderbar);
    labelLoaderBar.setBounds(35, 247, 205, 16);
    getContentPane().add(labelLoaderBar);
    statusLabel = new JLabel(bundle.getString("splash.trying.to.connect"));
    statusLabel.setForeground(new Color(0xFFFFFF));
    statusLabel.setFont(new Font("Myriad Web", Font.PLAIN, 18));
    statusLabel.setBounds(35, 212 , 200, 20);
    labelVersion = new JLabel();
    try {
    	
    	/*Properties p = new Properties();
    	InputStream instrm = null;
    	instrm = ClassLoader.getSystemResourceAsStream("version.properties");
    	p.load(instrm);
    	System.out.println (p.get ("version"));*/
//    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		dom = db.parse("xml/version.xml");
//		Element docEle = dom.getDocumentElement();
//		labelVersion.setText(docEle.getAttribute("value"));
    	labelVersion.setText(VERSION_LABEL);
		
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	labelVersion.setForeground(new Color(0xFFFFFF));
    labelVersion.setFont(new Font("Myriad Web", Font.PLAIN, 21));
    labelVersion.setBounds(35, 340 , 100, 25);
    getContentPane().add(statusLabel);
    getContentPane().add(labelVersion);
    try {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	//UIManager.setLookAndFeel(UIManager.getLookAndFeel());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    
//    pb = new JProgressBar(0, waitTime);
//    pb.setBorderPainted(false);
//    pb.setIndeterminate(true);
//    pb.setBorderPainted(false);
////    Image img = m_splash_cursor.getImage();
////    pb.imageUpdate(img, 0, 35, 310, 300, 18);
//    //pb.setBackground(new Color(0x000000));
//    pb.setOpaque(false);
//    pb.setForeground(new Color(0XFFFFFF));
//    pb.setBounds(35, 241, 210, 18);
//    pb.setValue(0);
//    pb.setStringPainted(false);
    JLabel l = new JLabel("");
    l = new JLabel() {
      public void paintComponent(Graphics g) {
        m_image.paintIcon(this, g, 0, 0);
        
      }
    };
    //getContentPane().add(pb);
    getContentPane().add(l);
    setSize(368, 368);
    Dimension screenSize =
        Toolkit.getDefaultToolkit().getScreenSize();
    Dimension labelSize = l.getPreferredSize();
    setLocation(screenSize.width / 2 - 200,
                screenSize.height / 2 - 225);
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        setVisible(false);
        dispose();
      }
    });
  //Create a timer.
    timer = new Timer(interval, new ActionListener() {
        public void actionPerformed(ActionEvent evt) 
        {
		    if (_connected ){
		      Toolkit.getDefaultToolkit().beep();
		      timer.stop();
		      setVisible(false);
		      dispose();
		    }
        }
    });
    setVisible(true);
    //Thread splashThread = new Thread(waitRunner, "SplashThread");
    timer.start();
    
  
  }

}
