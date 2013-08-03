package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.LookAndFeel;
//import javax.swing.plaf.ScrollBarUI;
//import javax.swing.plaf.basic.BasicArrowButton;
//import javax.swing.plaf.basic.BasicScrollBarUI;

import com.agneya.util.Configuration;
import com.onlinepoker.resources.Bundle;


public class Utils {

  static Logger _cat = Logger.getLogger(Utils.class.getName());
  
  public static Point cgFilterLablesStart[] = 
	{
		new Point(540, 95),
		new Point(540, 120),
		new Point(540, 145),
		new Point(540, 170)
	};
  public static Point mttFilterLablesStart[] = 
	{
		new Point(540, 92),
		new Point(540, 112),
		new Point(540, 132),
		new Point(540, 153),
		new Point(540, 173)
	};
  
  //public static String URL ="http://www.blueacepoker.com";// "http://localhost:8080/backoffice"; ////http://115.112.189.165:8080/blueace
  public static String getURL(){
	  ResourceBundle bundle = Bundle.getBundle();
	  return bundle.getString("url");
  }
  
  //private static String codebase = "http://agneya.game-host.org:8080";

  public static Font standartFont = new Font("SansSerif", Font.BOLD, 12);
  public static Font standartButtonFont = new Font("SansSerif", Font.BOLD, 12);
  public static Font bigButtonFont = new Font("Verdana", Font.BOLD, 14);

  public static Font smallButtonFont = new Font("Franklin Gothic", Font.BOLD, 9);
  public static Font chatFont = new Font("Verdana", Font.PLAIN, 10);
  public static Font smallChatFont = new Font("Verdana", Font.PLAIN, 10);
  

  public static Font sliderFont = new Font("Arial", Font.PLAIN, 11);
  //public static Font sliderLabel = new Font("Arial", Font.PLAIN, 11);

  public static Font namePlateFont = new Font("Humanst521 Bold BT", Font.BOLD, 18);
  public static Font moveButtonFont = new Font("Humanst521 Bold BT", Font.BOLD, 12);
  public static Font menuButtonFont = new Font("Humanst521 Bold BT", Font.BOLD, 12);
  //public static Font bubbleFont = new Font("SansSerif", Font.BOLD, 12);
  //public static Font normalFont = new Font("Verdana", Font.PLAIN, 12);
  public static Font boldFont = new Font("Verdana", Font.BOLD, 12);


  private static Color colors[][] = {
      {
      new Color(99, 59, 41), new Color(151, 102, 67), new Color(199, 161, 140),
      new Color(100, 54, 44), new Color(116, 50, 54), new Color(35, 84, 54)}
      , {
      new Color(19, 109, 31), new Color(141, 152, 57), new Color(189, 211, 130),
      new Color(90, 104, 34), new Color(106, 100, 44), new Color(25, 134, 44)}
  };


  public static Color lobbyCellBgrnd = new Color(164, 89, 84, 196);//158, 210, 114, 196);

//  public static ScrollBarUI getRoomScrollBarUI() {
//    return new MyScrollBarUI(0);
//  }

//  public static ScrollBarUI getLobbyScrollBarUI() {
//    return new MyScrollBarUI(1);
//  }

  public static ImageIcon getIcon(String path) {
	    return (new ImageIcon(Utils.class.getResource(path)));
	  }

  public static Rectangle getChipsArea(Chip[] chips) {
    if (chips == null || chips.length == 0) {
      return null;
    }
    Rectangle r = null;
    for (int i = 0; i < chips.length; i++) {
      Rectangle tmp = chips[i].getRealCoords();
      if (tmp != null) {
        if (r == null) {
          r = new Rectangle(tmp);
        }
        else {
          r.add(tmp);
        }
      }
    }
    return r;
  }

  /*static class MyScrollBarUI
      extends BasicScrollBarUI {

    private int no;

    public MyScrollBarUI(int no) {
      super();
      this.no = no;
      thumbColor = colors[no][0];
      thumbDarkShadowColor = colors[no][1];
      thumbHighlightColor = colors[no][2];
      thumbLightShadowColor = colors[no][3];
      trackColor = colors[no][4];
      trackHighlightColor = colors[no][5];
    }

    protected void configureScrollBarColors() {
      LookAndFeel.installColors(scrollbar, "ScrollBar.background",
                                "ScrollBar.foreground");
      thumbColor = colors[no][0];
      thumbDarkShadowColor = colors[no][1];
      thumbHighlightColor = colors[no][2];
      thumbLightShadowColor = colors[no][3];
      trackColor = colors[no][4];
      trackHighlightColor = colors[no][5];
    }

    protected JButton createDecreaseButton(int orientation) {
      return new BasicArrowButton(orientation,
                                  thumbColor,
                                  thumbLightShadowColor,
                                  thumbDarkShadowColor,
                                  thumbHighlightColor);
    }

    protected JButton createIncreaseButton(int orientation) {
      return new BasicArrowButton(orientation,
                                  thumbColor,
                                  thumbLightShadowColor,
                                  thumbDarkShadowColor,
                                  thumbHighlightColor);
    }
  }*/

  
  // LObby code addition
//    public static final String getCashierUrl(String userid, String session) {
//	    try {
//	      session = URLEncoder.encode(session, "UTF-8");
//	    }
//	    catch (Exception e) {}
//	    return Bundle.getBundle().getString("cashier.url") + "&" +
//	        SharedConstants.USERID + "=" + userid;//+ "&" + SharedConstants.SESSIONID + "=" + session;
//	  }
//
//	  public static final String getHelpUrl() {
//	    return Bundle.getBundle().getString("doc.url");
//	  }
//
//	  public static final String getRegisterUrl() {
//	    return Bundle.getBundle().getString("register.url");
//	  }
//	  public static boolean showUrl(String url) {
//		   return false;
//	  }

}
