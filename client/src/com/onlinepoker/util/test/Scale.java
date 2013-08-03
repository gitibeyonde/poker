package com.onlinepoker.util.test;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.onlinepoker.Utils;

public class Scale extends JFrame {

  Image image;

  Insets insets;
  ImageIcon ic = null;
  double ratioX,ratioY;

  public Scale(ImageIcon icc,double rX, double rY) {
    //super();
    ImageIcon icon = icc;//Utils.getIcon("images/scale_test.png");
    ic = icon;
    this.ratioX = rX;
    this.ratioY = rY;
    image = icon.getImage();
  }

//  public void paint(Graphics g) {
//    super.paint(g);
//    if (insets == null) {
//      insets = getInsets();
//    }
//    g.drawImage(image, insets.left, insets.top, this);
//  }

  public Image go() {
    Image original = image;
    image = original.getScaledInstance((int)(ic.getIconWidth()*ratioX), (int)(ic.getIconHeight()*ratioY), 
    					Image.SCALE_SMOOTH);
    //repaint();
    return image;
  } 

  
 
  
//  public static void main(String args[]) {
//    Scale f = new Scale(Utils.getIcon("images/scale_test.png"));
//    f.setSize(400, 400);
//    
//    //f.add(jl);
//    f.show();
//    f.go();
//  }
}
