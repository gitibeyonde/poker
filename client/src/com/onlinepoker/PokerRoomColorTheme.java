package com.onlinepoker;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Kom
 * @version 1.0
 */

public class PokerRoomColorTheme extends DefaultMetalTheme {

  public String getName() { return "Aqua"; }
/*
  public static final Color  mainColor = new Color(127, 255, 85);
  public static final Color  secColor  = new Color(75, 127, 55);
 */
/*
  private final ColorUIResource primary1 = new ColorUIResource(96, 43, 39);
  private final ColorUIResource primary2 = new ColorUIResource(178, 105, 76);
  private final ColorUIResource primary3 = new ColorUIResource(122, 77, 82);

  private final ColorUIResource secondary1 = new ColorUIResource(35, 13, 18);
  private final ColorUIResource secondary2 = new ColorUIResource(88, 46, 51);
  private final ColorUIResource secondary3 = new ColorUIResource(75, 36, 40);

  private final ColorUIResource bl = new ColorUIResource(218, 138, 94);
  private final ColorUIResource wh = new ColorUIResource(60, 29, 32);
*//*
145, 155, 135
173, 185, 161
207, 222, 193
white
230, 241, 227
*/	private final ColorUIResource primary1 = new ColorUIResource(88, 132, 41);
	private final ColorUIResource primary2 = new ColorUIResource(124, 186 ,57);
	private final ColorUIResource primary3 = new ColorUIResource(182, 242, 113);

	private final ColorUIResource secondary1 = new ColorUIResource(125, 135, 115);
	private final ColorUIResource secondary2 = new ColorUIResource(153, 165, 141);
	private final ColorUIResource secondary3 = new ColorUIResource(187, 202, 173);

//	private final ColorUIResource secondary1 = new ColorUIResource(87, 105, 68);
//	private final ColorUIResource secondary2 = new ColorUIResource(123, 148, 96);
//	private final ColorUIResource secondary3 = new ColorUIResource(180, 204, 152);
		

//	private final ColorUIResource primary1 = new ColorUIResource(88, 118, 54);
//	private final ColorUIResource primary2 = new ColorUIResource(75, 127, 55);
//	private final ColorUIResource primary3 = new ColorUIResource(127, 255, 85);
//	
//	private final ColorUIResource secondary1 = new ColorUIResource(13, 27, 4);
//	private final ColorUIResource secondary2 = new ColorUIResource(106, 135, 82);
//	private final ColorUIResource secondary3 = new ColorUIResource(107, 147, 81);
//	
//	private final ColorUIResource bl = new ColorUIResource(0, 0, 0);
	private final ColorUIResource wh = new ColorUIResource(230, 241, 227);
//	private final ColorUIResource fo = new ColorUIResource(255, 255, 255);

  protected ColorUIResource getPrimary1() {
    return primary1;
  }

  protected ColorUIResource getPrimary2() {
    return primary2;
  }

  protected ColorUIResource getPrimary3() {
    return primary3;
  }

  protected ColorUIResource getSecondary1() {
	return secondary1;
  }

  protected ColorUIResource getSecondary2() {
	return secondary2;
  }
  protected ColorUIResource getSecondary3() {
	return secondary3;
  }

  protected ColorUIResource getWhite()
  {
	  return wh;
  }
/*  protected ColorUIResource getBlack()
  {
	  return bl;
  }
  public ColorUIResource getFocusColor()
  {
	  return fo;
  }


/*
  public ColorUIResource getDesktopColor()
  {
	  return primary3;
  }
  public ColorUIResource getControl()
  {
	  return primary3;
  }
  public ColorUIResource getControlShadow()
  {
	  return primary3;
  }
  public ColorUIResource getControlDarkShadow()
  {
	  return primary3;
  }
  public ColorUIResource getControlInfo()
  {
	  return primary3;
  }
  public ColorUIResource getControlHighlight()
  {
	  return primary3;
  }
  public ColorUIResource getControlDisabled()
  {
	  return primary3;
  }
  public ColorUIResource getPrimaryControl()
  {
	  return primary3;
  }
  public ColorUIResource getPrimaryControlShadow()
  {
	  return primary3;
  }
  public ColorUIResource getPrimaryControlDarkShadow()
  {
	  return primary3;
  }
  public ColorUIResource getPrimaryControlInfo()
  {
	  return primary3;
  }
  public ColorUIResource getPrimaryControlHighlight()
  {
	  return primary3;
  }
  public ColorUIResource getSystemTextColor()
  {
	  return primary3;
  }
  public ColorUIResource getInactiveControlTextColor()
  {
	  return primary3;
  }
  public ColorUIResource getInactiveSystemTextColor()
  {
	  return primary3;
  }
  public ColorUIResource getUserTextColor()
  {
	  return primary3;
  }
  public ColorUIResource getTextHighlightColor()
  {
	  return primary3;
  }
  public ColorUIResource getHighlightedTextColor()
  {
	  return primary3;
  }
  public ColorUIResource getWindowBackground()
  {
	  return primary3;
  }
  public ColorUIResource getWindowTitleBackground()
  {
	  return primary3;
  }
  public ColorUIResource getWindowTitleForeground()
  {
	  return primary3;
  }
  public ColorUIResource getWindowTitleInactiveBackground()
  {
	  return primary3;
  }
  public ColorUIResource getWindowTitleInactiveForeground()
  {
	  return primary3;
  }
  public ColorUIResource getMenuBackground()
  {
	  return primary3;
  }
  public ColorUIResource getMenuForeground()
  {
	  return primary3;
  }
  public ColorUIResource getMenuSelectedBackground()
  {
	  return primary3;
  }
  public ColorUIResource getMenuSelectedForeground()
  {
	  return primary3;
  }
  public ColorUIResource getMenuDisabledForeground()
  {
	  return primary3;
  }
  public ColorUIResource getSeparatorBackground()
  {
	  return primary3;
  }
  public ColorUIResource getSeparatorForeground()
  {
	  return primary3;
  }
  public ColorUIResource getAcceleratorForeground()
  {
	  return primary3;
  }
  public ColorUIResource getAcceleratorSelectedForeground()
  {
	  return primary3;
  }*/
}