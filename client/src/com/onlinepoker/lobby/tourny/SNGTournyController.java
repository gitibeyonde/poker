package com.onlinepoker.lobby.tourny;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.golconda.message.GameEvent;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.Utils;
import com.onlinepoker.models.LobbySitnGoModel;
import com.onlinepoker.models.LobbyTableModel;
import com.onlinepoker.models.LobbyTournyModel;
import com.onlinepoker.proxies.LobbyModelsChangeListener;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.poker.common.interfaces.SitnGoInterface;

public class SNGTournyController extends javax.swing.JPanel implements
LobbyModelsChangeListener {
  static Logger _cat = Logger.getLogger(SNGTournyController.class.getName());

  String _tid = "";
  LobbyTableModel _lobbyTournyModel;
  SNGTournyLobby _owner;
  /** Skin of this room */
  protected RoomSkin _skin = null;
  /** Input/Output manipulation organs */
  protected ResourceBundle _bundle;
  /** background */
  protected ImageIcon _tourny_background;
  ServerProxy _serverProxy;
  boolean _present;
  Vector<String> _pos = null;
  public double _largest_stack = 0;
  public double _smalest_stack = 0;
  public LobbySitnGoModel ltm;
  public GameEvent _ge ;
  public SNGTournyController(SNGTournyLobby owner, LobbyTableModel ltm,
                          ServerProxy serverProxy, RoomSkin skin, String tid) {
    _lobbyTournyModel = ltm;
    _owner = owner;
    _bundle = Bundle.getBundle();
    _serverProxy = serverProxy;
    _skin = skin;
    _tid = tid;
    _tourny_background = Utils.getIcon(ClientConfig.IMG_TOURNY_BACKGROUND);//skin.getournyBackround();
    setSize(_tourny_background.getIconWidth(), _tourny_background.getIconHeight());
    setBorder(null);
    setOpaque(true);
    setLayout(null);
    _present = false;
    repaint();
    _serverProxy.addLobbyModelChangeListener(this);

    _cat.fine("Created Tournament Controller ");
  }

  public void paintComponent(Graphics g) {
    //_cat.debug("Tourny controller paint");
    super.paintComponent(g);
    _tourny_background.paintIcon(this, g, 0, 0);


  }

  public void tableListUpdated(LobbyTableModel[] changes) {
	_present = false;
	int v = 0;
	for (v = 0; v < changes.length; v++) 
	{
	  GameEvent ge = _serverProxy._rtl.getGameEvent(v);
	  if(ge.getGameName().equals(_tid))
	  {
		if(!_owner.isVisible())return;  
		_ge = ge;
		ltm = new LobbySitnGoModel(ge);
		String[][] pstr = ge.getPlayerDetails();
		if (pstr != null) 
	    {
		  _pos = new Vector<String>();
		  _owner.resetRegPlayer();
		  if(ge.getWinner() == null)_owner.resetTable();
	      ArrayList<Double> chip_stack = new ArrayList<Double>();
	      for (int i = 0; i < pstr.length; i++) 
	      {
	    	chip_stack.add(Double.parseDouble(pstr[i][1]));
	    	_pos.add(pstr[i][0]);
        	if (pstr[i][3].equals(_owner._serverProxy._name))
        	{
        		_present = true;
        	}
        	if(ltm.getTournamentState() == SitnGoInterface.TABLE_OPEN)
        	{
        		_owner.addTable("", pstr[i][3], "");
            	
        	}
        	else if(ltm.getTournamentState() == SitnGoInterface.HAND_RUNNING)
        	{
        		_owner.addRegPlayer(pstr[i][3], pstr[i][1]);
            	_owner.addTable("", pstr[i][3], pstr[i][1] == "0"?"Finished":pstr[i][1]);
            }
      	      
	      }
	      Collections.sort(chip_stack);
	      _smalest_stack = chip_stack.get(0);
	      _largest_stack = chip_stack.get(chip_stack.size() - 1);
	    }
	    else
	    {
	      _owner.resetRegPlayer();
	      _owner.resetTable();
	    }
		_owner.refreshSNGTournyLobby(ltm);
		setButtonState();
	    break;
	  }
	  
	}
	
  }
  public void setButtonState() {
	  if (_present) {
          _owner._regButton.setText("     Unregister");
        }
        else {
          _owner._regButton.setText("     Register Now");
        }
  }

	@Override
	public void adUpdated() {
		
		
	}
	
	@Override
	public void holdemTableListUpdated(LobbyTableModel[] changes) {
		
		
	}
	
	@Override
	public void omahaTableListUpdated(LobbyTableModel[] changes) {
		
		
	}
	
	@Override
	public void sitnGoTableListUpdated(LobbyTableModel[] changes) {
		
	}
	
	@Override
	public void studTableListUpdated(LobbyTableModel[] changes) {
		
		
	}
	
	@Override
	public void tournyListUpdated(LobbyTournyModel[] changes) {
		
		
	}

	
	


  
}
