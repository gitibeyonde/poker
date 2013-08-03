package com.onlinepoker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.onlinepoker.lobby.LobbyUserImp;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.poker.game.PokerGameType;


public class ClientPlayerView implements Painter {

  static Logger _cat = Logger.getLogger(ClientPlayerView.class.getName());
  JComponent com;
  Graphics gr;
  private float _alpha = 1.0f;
  private int visibleTact = 0;
  private String message = null;
  private ImageIcon playerIcon = null;
  private ImageIcon namePlate = null;
  private ImageIcon namePlateActive = null;
  private ImageIcon namePlateDisconnected = null;
  private ImageIcon namePlateConnecting = null;
  
  private ImageIcon namePlateSmallBlind = null;
  private ImageIcon namePlateBigBlind = null;
  private ImageIcon namePlateAnte = null;
  private ImageIcon namePlateCheck = null;
  private ImageIcon namePlateCall = null;
  private ImageIcon namePlateBet = null;
  private ImageIcon namePlateRaise = null;
  private ImageIcon namePlateFold = null;
  private ImageIcon namePlateAllIn = null;
  private ImageIcon namePlateReserved = null;
  private ImageIcon namePlateTime = null;
  private ImageIcon namePlateTimeOut = null;
  private ImageIcon bublesIcon = null;
  private ImageIcon speakIcon = null;
  //by rk
  private ImageIcon plrNoteIconEmpty = null;
  private ImageIcon plrNoteIconPresent = null;
  
  private int iconWidth = -1;
  private int iconHeight = -1;
  private int speakWidth = -1;
  private int speakHeight = -1;
  private int bubleOrientation = -1;
  private Point iconPos = null;
  public boolean namePlate_color = false;
  private boolean namePlate_message = false;
  private boolean namePlate_reseve = false;

//  private Point namePos  = null;
  private Point chipsPos = null;
  private Point bublePos = null;
  private ClientPlayerModel model = null;
  PokerGameType pgt;
  public RoomSkin _skin;
  

  // public ClientPlayerView(){}

  public ClientPlayerView(ImageIcon playerIcon, ImageIcon namePlate, Point iconPos, Point chipsPos,
                          ImageIcon speakIcon, ImageIcon bublesIcon,
                          Point bublePos, int bubleOrientation,
                          ClientPlayerModel model, PokerGameType pgt, RoomSkin skin) {
    this.speakIcon = speakIcon;
    this.playerIcon = playerIcon;
    this.bublesIcon = bublesIcon;
    this.namePlate = namePlate;
    //resize code
    this._skin = skin;
    this.namePlateDisconnected = _skin.getNamePlateDisconnected();
    this.namePlateActive = _skin.getNamePlateActive();
    this.namePlateSmallBlind = _skin.getNamePlateSmallBlind();
    this.namePlateBigBlind = _skin.getNamePlateBigBlind();
    this.namePlateCheck = _skin.getNamePlateCheck();
    this.namePlateCall = _skin.getNamePlateCall();
    this.namePlateBet = _skin.getNamePlateBet();
    this.namePlateRaise = _skin.getNamePlateRaise();
    this.namePlateFold = _skin.getNamePlateFold();
    this.namePlateAllIn = _skin.getNamePlateAllIn();
    this.namePlateAnte = _skin.getNamePlateAnte();
    this.namePlateTime = _skin.getNamePlateTime();
    this.namePlateTimeOut = _skin.getNamePlateTimeOut();
    this.namePlateReserved = _skin.getNamePlateReserved();
    this.plrNoteIconEmpty = _skin.getNoteIconEmptyImg();
    this.plrNoteIconPresent = _skin.getNoteIconPresentImg();
    if (playerIcon != null) {
      iconWidth = playerIcon.getIconWidth();
      iconHeight = playerIcon.getIconHeight();
    }

    this.iconPos = iconPos.getLocation();
    this.chipsPos = chipsPos.getLocation();
    this.bublePos = bublePos.getLocation();
    this.bubleOrientation = bubleOrientation;
    this.model = model;
    this.pgt = pgt;
  }

  public void setAlpha(float al) {
    _alpha = al;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setVisibleTack(int visibleTact) {
    this.visibleTact = visibleTact;
  }

  public static final Color MIDDLE_COLOR = Color.WHITE; /*new Color(

                 (Color.GRAY.getRed()  +Color.WHITE.getRed()  )/2,
                 (Color.GRAY.getGreen()+Color.WHITE.getGreen())/2,
                 (Color.GRAY.getBlue() +Color.WHITE.getBlue() )/2);*/
  //resize code
  /*public void resize_temp(RoomSkin skin){
	  System.out.println("ClientPlayerView resize()");
	  _skin=skin;
	    this.namePlateDisconnected = _skin.getNamePlateDisconnected();
	    this.namePlateActive = _skin.getNamePlateActive();
	    this.namePlateSmallBlind = _skin.getNamePlateSmallBlind();
	    this.namePlateBigBlind = _skin.getNamePlateBigBlind();
	    this.namePlateCheck = _skin.getNamePlateCheck();
	    this.namePlateCall = _skin.getNamePlateCall();
	    this.namePlateBet = _skin.getNamePlateBet();
	    this.namePlateRaise = _skin.getNamePlateRaise();
	    this.namePlateFold = _skin.getNamePlateFold();
	    this.namePlateAllIn = _skin.getNamePlateAllIn();
	    this.namePlateAnte = _skin.getNamePlateAnte();
	    this.namePlateTime = _skin.getNamePlateTime();
	    this.namePlateTimeOut = _skin.getNamePlateTimeOut();
	    this.namePlateReserved = _skin.getNamePlateReserved();
	    this.plrNoteIconEmpty = _skin.getNoteIconEmptyImg();
	    this.plrNoteIconPresent = _skin.getNoteIconPresentImg();
	    //this.playerIcon=_skin.reSizeImage(playerIcon); by rk
	    this.playerIcon=_skin.getScaledImage(playerIcon);
	    if (playerIcon != null) {
	        iconWidth = playerIcon.getIconWidth();
	        iconHeight = playerIcon.getIconHeight();
	      }
  }*/

 public void paint(JComponent c, Graphics g) {
   /** Paint players icon */
   com = c; gr = g;
	 if (playerIcon != null && iconWidth > 0 && iconHeight > 0) {
     if (g.getClipBounds().intersects(iconPos.x, iconPos.y, iconWidth, iconHeight)) {
        //_cat.finest("alpha=" + _alpha + "Player pos = " + iconPos.x + ", " +  iconPos.y);
       if (_alpha < 0.9f) {
            Graphics2D g2d = (Graphics2D) g.create(iconPos.x, iconPos.y, iconWidth, iconHeight);
            int type = AlphaComposite.SRC_OVER;
            AlphaComposite rule = AlphaComposite.getInstance(type, _alpha);
            g2d.setComposite(rule);
            playerIcon.paintIcon(c, g2d, 0, 0);
            g2d.dispose();
       }
       else {
           Graphics gcopy = g.create(iconPos.x, iconPos.y, iconWidth, iconHeight);
           playerIcon.paintIcon(c, gcopy, 0, 0);
           gcopy.dispose();
       }
     }
   }
   /** Paint players name and money */
   if (model != null) {
     g.setFont(Utils.standartFont);
     Rectangle r = model.getNameBounds();
     if (r != null) {
       Graphics gcopy = g.create(r.x , r.y , r.width, r.height);
       gr = g.create(r.x , r.y , r.width, r.height);
       //_cat.finest((model==null ? "" : model.name) + " Name pos " + r.x + ", " + r.y);
       //gcopy.setColor(Color.black);
       int name_shift = (10 - model.getPlayerName().length() ) *3;
       String amt = "";
       if(!pgt.isTourny())amt = SharedConstants.chipToMoneyString$Right(((double) model.getAmtAtTable()));
       else amt = SharedConstants.tourneyPointsToString(((double) model.getAmtAtTable()));
       namePlate_color = true;
       //System.out.println(model.getPlayerName()+" --- "+ model.isActive()+"---"+message);
       
       //if(message != null && message.startsWith("Fold"))namePlate_message = true;
//       if(model.isAnte())
//	   {
//    	   namePlateAnte.paintIcon(c, gcopy, 0, 0);
//	   }
       if(model.isFolded())
	   {
    	   //System.out.println(pgt);
    	   //if(!pgt.isTPoker()){ /** condition written by rk for avoid to show fold plate 2 times in TPOKER */
    		   namePlateFold.paintIcon(c, gcopy, 0, 0);
    	   //}
	   }
       else if(model.isDisconnected())
	   {
    	   namePlateDisconnected.paintIcon(c, gcopy, 0, 0);
	   }
       else if(model.isAllIn())
	   {
    	   namePlateAllIn.paintIcon(c, gcopy, 0, 0);
	   }
       else if(model.isNew() && !model.isSittingOut() && !model.isSelected() && !namePlate_reseve && !pgt.isTPoker())
	   {
    	   namePlateReserved.paintIcon(c, gcopy, 0, 0);
	   }
       else if (model.isSelected() ) 
       {
    	   //new Exception("active plt").printStackTrace();
    	   namePlateActive.paintIcon(c, gcopy, 0, 0);
    	   namePlate_color = false;
           //gcopy.drawString(model.getPlayerName(), 45, 48);
       }
       else {
    	   //new Exception("#########").printStackTrace();
    	   namePlate.paintIcon(c, gcopy, 0, 0);
    	   namePlate_color = false;
    	   namePlate_message = false;
       }
       if (model.isActive()) 
       {
    	   //System.out.println("isSelected "+model._name+" - "+model.isSelected());
         if (model.isSelected()) {
           gcopy.setColor(Color.BLACK);
         }
         else {
           gcopy.setColor(Color.white);
         }
         //by rk
         String keyName = "note."+model._name;
         if(!model._name.equals(ServerProxy._name)){
        	 Point p=_skin.getNotePosition();
			if(LobbyUserImp.map.get(keyName) != null){ 
//				Point p = model._skin.getPlrNotePos(model.getPlayerPosition());
//				System.out.println(model.getPlayerPosition()+", "+p.x+", "+p.y);
				plrNoteIconPresent.paintIcon(c, gcopy, p.x, p.y);
			}else{
				plrNoteIconEmpty.paintIcon(c, gcopy, p.x, p.y);
			}
         }
       }
       
       if(model.isSittingOut() && !model.isActive() && !namePlate_color)
       {
    	   //resize code
    	   Point p=_skin.getNamePosition();
    	   gcopy.setColor(Color.white);
    	   gcopy.drawString(model.getPlayerName(), p.x, p.y);
    	   gcopy.setFont(Utils.smallButtonFont);
    	   
    	   p=_skin.getCityPosition();
    	   gcopy.drawString(model._city, p.x - model._city.length() * 5 , p.y);
    	   gcopy.setFont(Utils.standartFont);
    	   //resize code
    	   p=_skin.getStatusPosition();
    	   gcopy.drawString("Sitting out", p.x , p.y);
    	  // gcopy.drawString("Sitting out", p.x+40 , p.y+35);
//    	   gcopy.setColor(Color.white);
//    	   gcopy.drawString(model.getPlayerName(), 45, 44);
//    	   gcopy.setFont(Utils.smallButtonFont);
//    	   gcopy.drawString(model._city, 142 - model._city.length() * 5 , 53);
//    	   gcopy.setFont(Utils.standartFont);
//    	   gcopy.drawString("Sitting out", 85 , 79);
       }
       else if(!namePlate_color)
       {
    	  //resize code
    	   Point p=_skin.getNamePosition();
    	   if(_skin._ratio_x > 1){
    		   gcopy.setFont( new Font("SansSerif", Font.BOLD, (int)(12*_skin._ratio_x))); 
    	   }else{
    		   gcopy.setFont(Utils.standartFont);
    	   }
    	   gcopy.drawString(model.getPlayerName(), p.x, p.y);
    	   
    	   p=_skin.getCityPosition();
    	   if(_skin._ratio_x > 1){
    		   gcopy.setFont(new Font("Franklin Gothic", Font.BOLD, (int)(9*_skin._ratio_x)));
    	   		gcopy.drawString(model._city, p.x - (int)(model._city.length() * 5*_skin._ratio_x), p.y);
    	   }else{
    		   gcopy.setFont(Utils.smallButtonFont);
    		   gcopy.drawString(model._city, p.x - model._city.length() * 5, p.y);
    	   }
    	   
    	   p=_skin.getChipsPosition();
    	   if(_skin._ratio_x > 1){
    		   gcopy.setFont( new Font("SansSerif", Font.BOLD, (int)(12*_skin._ratio_x)));
    		   gcopy.drawString(amt, p.x - (int)(amt.length() * 7*_skin._ratio_x) , p.y);
    	   }else{
    		   gcopy.setFont(Utils.standartFont);
    		   gcopy.drawString(amt, p.x - amt.length() * 7 , p.y);
    	   }
    	   
    	   
//    	 //(145 - amt.length() * 7)145 is end point and amt is string for each character 7 px width
//    	   gcopy.drawString(model.getPlayerName(), 45, 44);
//    	   gcopy.setFont(Utils.smallButtonFont);
//    	   gcopy.drawString(model._city, 142 - model._city.length() * 5, 53);
//    	   gcopy.setFont(Utils.standartFont);
//    	   gcopy.drawString(amt, 150 - amt.length() * 7 , 79);
       }
       gcopy.dispose();
     }
   }

   if (visibleTact > 0) {
	   Rectangle r = model.getNameBounds();
     Graphics gcopy = g.create(r.x , r.y , r.width, r.height);
//     if (visibleTact < 10) {
//       Graphics2D g2d = (Graphics2D) gcopy;
//       int type = AlphaComposite.SRC_OVER;
//       AlphaComposite rule = AlphaComposite.getInstance(type,
//           visibleTact / 10.0f);
//       g2d.setComposite(rule);
//     }
     //System.out.println("Status of "+model.getPlayerName()!= null?model.getPlayerName():""+message);
	 gcopy.setColor(Color.WHITE);
	 gcopy.setFont(Utils.namePlateFont);
	 //to deactivate the active_whiite panel
	 //namePlate.paintIcon(c, gcopy, 0, 0);
	 if(message == null);
	 else if(message.startsWith("S Blnd"))
	 {
    	 namePlateSmallBlind.paintIcon(c, gcopy, 0, 0);
    	 namePlate_reseve = true;
     }
     else if(message.startsWith("B Blnd"))
	 {
    	 namePlateBigBlind.paintIcon(c, gcopy, 0, 0);
    	 namePlate_reseve = true;
     }
     else if(message.startsWith("Call"))
     {
    	 namePlateCall.paintIcon(c, gcopy, 0, 0);
     }
     else if(message.startsWith("Check"))
     {
    	 namePlateCheck.paintIcon(c, gcopy, 0, 0);
     }
     else if(message.startsWith("Raise"))
     {
    	 namePlateRaise.paintIcon(c, gcopy, 0, 0);
     }
     else if(message.startsWith("Bet"))
     {
    	 namePlateBet.paintIcon(c, gcopy, 0, 0);
     }
//	 else if(message.startsWith("AllIn"))
//     {
//    	 namePlateAllIn.paintIcon(c, gcopy, 0, 0);
//     }
     else if(message.startsWith("Fold"))
     {
    	//new Exception(message+" ClientPlayerView.paint() ####### Fold").printStackTrace();
    	 namePlateFold.paintIcon(c, gcopy, 0, 0);
     }
     //message= null;
     gcopy.dispose();
   }

 }

 public Point getChipsPos() {
    return (chipsPos);
  }
 
 public void refresh() {
    
  }
}
