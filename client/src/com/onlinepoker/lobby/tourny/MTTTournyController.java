package com.onlinepoker.lobby.tourny;

import java.awt.Frame;
import java.awt.Graphics;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.golconda.message.GameEvent;
import com.golconda.message.Response;
import com.onlinepoker.ClientConfig;
import com.onlinepoker.ClientRoom;
import com.onlinepoker.Utils;
import com.onlinepoker.models.LobbyTournyModel;
import com.onlinepoker.resources.Bundle;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.skin.RoomSkin;
import com.poker.common.message.ResponseInt;
import com.poker.common.message.ResponseTournyDetail;
import com.poker.common.message.ResponseTournyMyTable;
import com.poker.common.message.TournyEvent;

public class MTTTournyController extends javax.swing.JPanel implements
    TournyMessagesListener {
  static Logger _cat = Logger.getLogger(MTTTournyController.class.getName());

  String _tid = "";
  LobbyTournyModel _lobbyTournyModel;
  MTTTournyLobby _owner;
  /** Skin of this room */
  protected RoomSkin _skin = null;
  /** Input/Output manipulation organs */
  protected ResourceBundle _bundle;
  /** background */
  protected ImageIcon _tourny_background;
  ServerProxy _serverProxy;
  boolean _present;
  public double _largest_stack = 0;
  public double _smalest_stack = 0;
  public Timer _tdetailTimer = new Timer();
  static long REFRESH_INTERVAL = 5000;
  
  
  public MTTTournyController(MTTTournyLobby owner, LobbyTournyModel ltm,
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
    TDLThread rtl = new TDLThread();
    _tdetailTimer.schedule(rtl, 0, REFRESH_INTERVAL);
    _cat.fine("Created Tournament Controller ");
  }

  public void paintComponent(Graphics g) {
    //_cat.debug("Tourny controller paint");
    super.paintComponent(g);
    _tourny_background.paintIcon(this, g, 0, 0);


  }

  
  public void tournyMessageReceived(Object obj) {
    //System.out.println("tournyMessageReceived "+obj.toString());
	  if(!_owner.isVisible())return;  
	  if (obj instanceof TournyEvent) {
      TournyEvent actions = (TournyEvent) obj;
      LobbyTournyModel ltm = new LobbyTournyModel((TournyEvent) obj);
      _lobbyTournyModel.update(actions);
      _present = false;
      // PLAYERS REG/WINNERS/PLAYING
      String pstr = actions.get("player");
      if (pstr != null) {
        _owner.resetRegPlayer();
        String[] players = pstr.split("\\|");
        _cat.fine("Players= " + actions.get("player"));
        for (int i = 0; players != null && i < players.length; i++) {
          _cat.fine("Adding " + players[i]);
          _owner.addRegPlayer((i+1)+ "", players[i].substring(0,players[i].indexOf("`")), players[i].substring(players[i].indexOf("`")+1));
          if (players[i].substring(0,players[i].indexOf("`")).equals(_owner._serverProxy._name)) {
            _cat.fine("Player is present");
            _present = true;
            //System.out.println("present: "+_present);
          }
        }
      }
      _owner.refreshMTTTournyLobby(ltm);
	  setButtonState(actions);
    }
    else if (obj instanceof ResponseTournyDetail) {
      ResponseTournyDetail rtd = (ResponseTournyDetail) obj;
      String[] games = rtd.getTournyGameEvent();
      _owner.resetTable();
      if (games != null && games.length > 0) {
        for (int i = 0; i < games.length; i++) {
        	GameEvent ge = new GameEvent();
            ge.init(games[i]);
          _cat.fine(ge.toString());
          _owner.addTable(ge.getGameName() + "", "" + ge.get("name"), "", ge);
        }
        _owner._myTableButton.setVisible(true);
        _owner._observeTableButton.setVisible(true);
      }
      else
      {
    	  _owner._myTableButton.setVisible(false);
          _owner._observeTableButton.setVisible(false);
      }
      tournyMessageReceived(new TournyEvent(rtd.getTournyEvent()));
    }
    else if (obj instanceof ResponseInt) {
      ResponseInt ri = (ResponseInt) obj;
      if (ri.responseName() == Response.R_TOURNYREGISTER) {
        _owner._regButton.setEnabled(false);
      }
    }
    else if (obj instanceof ResponseTournyMyTable) {
      ResponseTournyMyTable ri = (ResponseTournyMyTable) obj;
      // open the table
      if (ri.getResult() != 1) {
        JOptionPane.showMessageDialog(this,
            "Unable to goto your table.\nThere is no table allocated to you.",
                                      "ERROR", JOptionPane.ERROR_MESSAGE);
      }
      if(_present)
      {
    	  if(_owner.vTournyClientRooms.get(_tid) == null)
  		  {
    		  GameEvent ge = (GameEvent) _owner._tes.get(ri.getGameTid());
    		  ClientRoom cr = new ClientRoom(_owner._serverProxy, ge, _owner);
    		  _owner.vTournyClientRooms.put(_tid, cr);
    		  _serverProxy.createActionFactory(ri.getGameTid(), ri.getPosition());
  		  }
    	  else
    	  {
    		Frame frames[] = _owner.vTournyClientRooms.get(_tid).getFrames();
    		int j=-1;
    		for (int i = 0; i < frames.length; i++) {
    			if(frames[i].getTitle().contains(_tid)){j=i;break;}
    		}
    		if(j != -1)
    		{
    			System.out.println("frame matched"+frames[j].getName());
    			frames[j].toFront();
    		}
    	  }
	  }
    }
    else {
      throw new IllegalStateException("Unrecognized command " + obj);
    }
  }

  public void setButtonState(TournyEvent te) {
	  if(_owner._regButton == null)return;
	  if (_present) {
          _owner._regButton.setText("     Unregister");
        }
        else {
          _owner._regButton.setText("     Register Now");
        }

//    switch (te.state()) {
//      case TournyInterface.NOEXIST:
//      case TournyInterface.CREATED:
//      case TournyInterface.DECL:
//        //_owner._regButton.setEnabled(false);
//        //_owner._myTableButton.setEnabled(false);
//        //_owner._openTableButton.setEnabled(false);
//        break;
//      case TournyInterface.REG:
////        if (_present) {
////          _owner._regButton.setText("     Un Register");
////        }
////        else {
////          _owner._regButton.setText("     Register Now");
////        }
//
//        break;
//      case TournyInterface.JOIN:
//      case TournyInterface.START:
//      case TournyInterface.RUNNING:
//        //_owner._myTableButton.setEnabled(true);
//        //_owner._openTableButton.setEnabled(true);
//        break;
//      case TournyInterface.END:
//      case TournyInterface.FINISH:
//      case TournyInterface.CLEAR:
//        //_owner._regButton.setEnabled(false);
//        //_owner._myTableButton.setEnabled(false);
//        //_owner._openTableButton.setEnabled(false);
//        break;
//      default:
//        break;
//    }
    repaint();
  }
  
  public class TDLThread extends TimerTask {
	  public void run() {
        try {
        	_serverProxy.tournyDetails(_tid);
        	
        }
        catch (Exception ex) {
          //do nothing
          _cat.warning("Exception" + ex);
        }
      }
    } // end tourny detail thread class
}
