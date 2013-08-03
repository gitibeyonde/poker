package com.onlinepoker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSliderUI;
/**
 * 
 * currently not using, it is for Bet slider on table
 *
 */
public class ThemesSliderUI extends MetalSliderUI
{
  /*//public static final ThemesSliderUI sliderUI = new ThemesSliderUI ();  // creates a nice bug.. %-p
 
  protected static ImageIcon horiz_knob_enabled  = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon horiz_knob_disabled = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon vert_knob_enabled   = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon vert_knob_disabled  = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
 
  protected static ImageIcon hor_track_left_enabled    = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon hor_track_center_enabled  = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon hor_track_right_enabled   = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon hor_track_left_disabled   = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon hor_track_center_disabled = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon hor_track_right_disabled  = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
 
  protected static ImageIcon ver_track_left_enabled    = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon ver_track_center_enabled  = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon ver_track_right_enabled   = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon ver_track_left_disabled   = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon ver_track_center_disabled = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
  protected static ImageIcon ver_track_right_disabled  = Utils.getIcon(ClientConfig.IMG_SLIDER_TRIANGLE);
 
 
  public static ComponentUI createUI (JComponent c)
  {
    // Don't use the sliderUI variable from above, instead create each time a new instance!!!
    return new ThemesSliderUI ();
  }
 
  public void installUI (JComponent c)
  {
    if (UIManager.get ("Slider.trackWidth") == null)
      UIManager.put ("Slider.trackWidth", new Integer (7));       // default value from MetalLAF
 
    if (UIManager.get ("Slider.majorTickLength") == null)
      UIManager.put ("Slider.majorTickLength", new Integer (6));  // default value from MetalLAF
 
    super.installUI (c);
  }
 
  public void update (Graphics g, JComponent c)
  {
 
    if (c != null && c instanceof JSlider)
    {
      JSlider sl = (JSlider)c;
 
      if (sl.isOpaque())
        sl.setOpaque(false);
 
      //g.clearRect (0, 0, sl.getSize().width, sl.getSize().height);
 
      if (sl.getOrientation() == JSlider.HORIZONTAL && horiz_knob_enabled != null && horiz_knob_disabled != null)
        horizThumbIcon = sl.isEnabled() && sl.hasFocus()? horiz_knob_enabled : horiz_knob_disabled;
      else if (sl.getOrientation() == JSlider.VERTICAL && vert_knob_enabled != null && vert_knob_disabled != null)
        vertThumbIcon = sl.isEnabled()  && sl.hasFocus()? vert_knob_enabled : vert_knob_disabled;
    }
 
    paint (g, c);
    g.dispose();
  }
 
  public void paintTrack (Graphics g)
  {
    Rectangle r = trackRect;
 
    if (slider.getOrientation() == JSlider.HORIZONTAL)
    {
      if (hor_track_left_enabled != null  && hor_track_center_enabled != null  && hor_track_right_enabled != null &&
          hor_track_left_disabled != null && hor_track_center_disabled != null && hor_track_right_disabled != null)
      {
        g.translate (r.x, r.y+getThumbSize().height/3);
 
        g.drawImage ((slider.isEnabled()? hor_track_left_enabled : hor_track_left_disabled).getImage(), 0, 0, null);
 
        for (int x=hor_track_left_enabled.getIconWidth(); x<r.width-hor_track_right_enabled.getIconWidth(); x++)
          g.drawImage ((slider.isEnabled()? hor_track_center_enabled : hor_track_center_disabled).getImage(), x, 0, null);
 
        g.drawImage ((slider.isEnabled()? hor_track_right_enabled : hor_track_right_disabled).getImage(), r.width-hor_track_left_enabled.getIconWidth(), 0, null);
 
        g.translate (-r.x, -(r.y+getThumbSize().height/3));
      }
        else super.paintTrack (g);
    }
    else
    {
      if (ver_track_left_enabled != null  && ver_track_center_enabled != null  && ver_track_right_enabled != null &&
          ver_track_left_disabled != null && ver_track_center_disabled != null && ver_track_right_disabled != null)
      {
        g.translate (r.x+getThumbSize().width/3, r.y);
 
        g.drawImage ((slider.isEnabled()? ver_track_left_enabled : ver_track_left_disabled).getImage(), 0, 0, null);
 
        for (int x=ver_track_left_enabled.getIconHeight(); x<r.height-ver_track_right_enabled.getIconHeight(); x++)
          g.drawImage ((slider.isEnabled()? ver_track_center_enabled : ver_track_center_disabled).getImage(), 0, x, null);
 
        g.drawImage ((slider.isEnabled()? ver_track_right_enabled : ver_track_right_disabled).getImage(), 0, r.height-ver_track_left_enabled.getIconHeight(), null);
 
        g.translate (-r.x-getThumbSize().width/3, -r.y);
      }
        else super.paintTrack (g);
    }
 
    g.dispose();
  }
 
  protected Dimension getThumbSize()
  {
    Dimension size = new Dimension();
 
    if ( slider.getOrientation() == JSlider.VERTICAL )
    {
      if (vert_knob_enabled != null)
        return new Dimension (vert_knob_enabled.getIconWidth(), vert_knob_enabled.getIconHeight());
      else  return super.getThumbSize();
    }
    else
    {
      if (horiz_knob_enabled != null)
        return new Dimension (horiz_knob_enabled.getIconWidth(), horiz_knob_enabled.getIconHeight());
      else  return super.getThumbSize ();
    }
  }*/
}
