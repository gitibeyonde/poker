package com.onlinepoker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.onlinepoker.lobby.LobbyUserImp;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.skin.RoomSkin;


// import com.onlinepoker.shared.models.

/** Desk for playing */
public class ClientPokerView
    implements Painter {

  /** money that are in pot(?) */
  protected int pot_x, pot_y;

  /** Poker model (data of the Game) */
  ClientPokerModel model;

  protected RoomSkin _skin = null;
  protected ResourceBundle bundle;
  

//  ImageIcon _status_on = Utils.getIcon(ClientConfig.ONLINE);
//  ImageIcon _status_off = Utils.getIcon(ClientConfig.OFFLINE);

  static Logger _cat = Logger.getLogger(ClientPokerView.class.getName());

//  public ClientPlayerView cplv;

  //  RoomSkin <- coordinats of players, coor_s of chips, images for table, ...
  public ClientPokerView(ClientPokerModel model, RoomSkin _skin) {
    this._skin = _skin;
    pot_x = _skin.getHeapPlace().x;
    pot_y = _skin.getHeapPlace().y;
    this.model = model;
    this.bundle = Bundle.getBundle();
    
  }
  //resize code
  public void resize(RoomSkin skin){
	  _skin=skin;
	    pot_x = _skin.getHeapPlace().x;
	    pot_y = _skin.getHeapPlace().y;
  }

  public void paint(JComponent c, Graphics g) {
    //   _cat.finest("playerAmount: " + model.getPlayerMoneyAtTable());
   
//    String gameId = model.name;
//    String handIdOld = SharedConstants.intTo3NumSeparetedString(model._prev_grid);
//    String handIdNew = SharedConstants.intTo3NumSeparetedString(model._grid);
	  //resize code
	  Graphics gcopy = g.create(0, 0, _skin.getMenuSize().x, _skin.getMenuSize().y);
    //Graphics gcopy = g.create(0, 0, 800, 50);
    Graphics handidLable = g.create(0, 30, _skin.getHandIdLabelSize().x, _skin.getHandIdLabelSize().y);
    //Graphics status = g.create(0, 30, 800, 20);
    
    
    try {
    	 _skin.getImageLobbyMenuBarBG().paintIcon(c, g, 0, 0);
		//Utils.getIcon(ClientConfig.IMG_LOBBY_MENUBAR_BG).paintIcon(c, g, 0, 0);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	}
    
    gcopy.setColor(Color.WHITE);
    if(LobbyUserImp.fromBrowser){
    	gcopy.setFont(new Font("Humanst521 Bold BT", Font.BOLD, 16));
    }else{
    	gcopy.setFont(Utils.menuButtonFont);
    }
    if(model.gameType.isSitnGo())
    {
    	//resize code
    	gcopy.drawString(bundle.getString("tourney.info"), _skin.getTourneyInFo().x, _skin.getTourneyInFo().y);
	    gcopy.drawString(bundle.getString("lasthand"), _skin.getLastHand().x, _skin.getLastHand().y);
	    gcopy.drawString(bundle.getString("options"), _skin.getOptions().x, _skin.getOptions().y);
	    gcopy.drawString(bundle.getString("stats"), _skin.getStats().x, _skin.getStats().y);
	    gcopy.drawString(bundle.getString("lobby"), _skin.getLobby().x, _skin.getLobby().y);
//    	gcopy.drawString(bundle.getString("tourney.info"), 25, 15);
//	    gcopy.drawString(bundle.getString("lasthand"), 125, 15);
//	    gcopy.drawString(bundle.getString("options"), 225, 15);
//	    gcopy.drawString(bundle.getString("stats"), 325, 15);
//	    gcopy.drawString(bundle.getString("lobby"), 650, 15);
    }
    else if(model.gameType.isTourny())
    {
    	//resize code
    	gcopy.drawString(bundle.getString("tourney.info"), _skin.getTourneyInFo().x, _skin.getTourneyInFo().y);
	    gcopy.drawString(bundle.getString("lasthand"), _skin.getLastHand().x, _skin.getLastHand().y);
	    gcopy.drawString(bundle.getString("options"), _skin.getOptions().x, _skin.getOptions().y);
	    gcopy.drawString(bundle.getString("stats"),_skin.getStats().x, _skin.getStats().y);
	    gcopy.drawString(bundle.getString("rebuy"), _skin.getRebuy().x, _skin.getRebuy().y);
	    gcopy.drawString(bundle.getString("lobby"), _skin.getLobby().x, _skin.getLobby().y);
//    	gcopy.drawString(bundle.getString("tourney.info"), 25, 15);
//	    gcopy.drawString(bundle.getString("lasthand"), 125, 15);
//	    gcopy.drawString(bundle.getString("options"), 225, 15);
//	    gcopy.drawString(bundle.getString("stats"), 325, 15);
//	    gcopy.drawString(bundle.getString("rebuy"), 425, 15);
//	    gcopy.drawString(bundle.getString("lobby"), 650, 15);
    }
    else
    {
    	//resize code
    	gcopy.drawString(bundle.getString("getchips"), _skin.getTourneyInFo().x, _skin.getTourneyInFo().y);
    	gcopy.drawString(bundle.getString("lasthand"), _skin.getLastHand().x, _skin.getLastHand().y);
    	if(!LobbyUserImp.fromBrowser)
	    gcopy.drawString(bundle.getString("options"), _skin.getOptions().x, _skin.getOptions().y);
	    gcopy.drawString(bundle.getString("stats"), _skin.getStats().x, _skin.getStats().y);
	    gcopy.drawString(bundle.getString("standup"), _skin.getRebuy().x, _skin.getRebuy().y);
	    if(!LobbyUserImp.fromBrowser)
	    gcopy.drawString(bundle.getString("lobby"), _skin.getLobby().x, _skin.getLobby().y);
	    if(LobbyUserImp.fromBrowser){
	    	 gcopy.setFont(new Font("Humanst521 Bold BT", Font.BOLD, 16));
	    	 gcopy.drawString(bundle.getString("table.exit"), _skin.getExit().x, _skin.getExit().y);
	    	    
	    }
//    	gcopy.drawString(bundle.getString("getchips"), 25, 15);
//    	gcopy.drawString(bundle.getString("lasthand"), 125, 15);
//	    gcopy.drawString(bundle.getString("standup"), 225, 15);
//	    gcopy.drawString(bundle.getString("options"), 325, 15);
//	    gcopy.drawString(bundle.getString("stats"), 425, 15);
//	    gcopy.drawString(bundle.getString("lobby"), 650, 15);
    }
    
    gcopy.dispose();
//    System.out.println("model._owner._serverProxy._disconnect_count "+model._owner._serverProxy._disconnect_count);
//    if(model._owner._serverProxy._disconnect_count < 2){
//    	status.drawImage(_status_on.getImage(),5 ,5 , _status_on.getIconWidth(),_status_on.getIconHeight(),null);
//    	System.out.println("online");
//    }else{
//    	status.drawImage(_status_off.getImage(),5 ,5 , _status_off.getIconWidth(),_status_off.getIconHeight(),null);
//    	System.out.println("offline");
//    }
//    status.dispose();
    
    handidLable.setColor(Color.WHITE);
    handidLable.setFont(Utils.boldFont);
    long handId = model._grid;
    if(handId != -1){
    	handidLable.drawString("Hand id : "+handId, _skin.getHandIdLabelPosition().x, _skin.getHandIdLabelPosition().y);
    	//handidLable.drawString("Hand id : "+handId, 645, 15);
    }
	  
	  
	  
    // for showing all empty name plates
    for (int j = 0; j < model.maxPlayer; j++) {
    	Point p=_skin.getNamePos(j, ClientPlayerModel.FEMALE);
//    	NamePlate np = new NamePlate(c, p.x, p.y);
//    	np.paint(c, g);
//    	model.movingNamePlates.add(np);
        _skin.getNamePlate().paintIcon(c, g, p.x, p.y);
        }
    if(model.fullscreen)
    Utils.getIcon(ClientConfig.IMG_WINDOW_BUTTONS).paintIcon(c, g, 700, 0);
    
  }
}
