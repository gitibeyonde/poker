package com.onlinepoker.skin;

import java.awt.Point;
import java.util.logging.Logger;

import com.onlinepoker.ClientPlayerModel;
import com.onlinepoker.Utils;


/**
 * This class holds graphical resources needed to draw the poker table.
 * It implements the Sigleton pattern.
 */
public class RoomSkinFour  extends RoomSkin
    implements java.io.Serializable {

  static Logger _cat = Logger.getLogger(RoomSkinFour.class.getName());
  public int _maxPlayer=4;

   
  public RoomSkinFour(int mp) {
	  super(mp);
	  //System.out.println("in RSFour "+_ratio_x+", "+_ratio_y);
	  
	  
	  int cardConstCloset[][] = {
		    { 81, 230}
		  , { 375, 66}
		  , { 699, 230}
		  , { 375, 452}
		  
      };
	  cardConstClose =cardConstCloset;
	  
	  int cardConstOpent[][] = {
		    { 81, 230}
		  , { 375, 66}
		  , { 699, 230}
		  , { 375, 452}
		  
      };
	  cardConstOpen = cardConstOpent;

	  int mansCoordst[][] = {
		    { 15, 193}
		  , { 311, 25}
	      , { 634, 193}
	      , { 311, 411}
	  };
	  mansCoords = mansCoordst;

	  int girlsCoordst[][] = {
		    { 15, 193}
		  , { 311, 25}
	      , { 634, 193}
	      , { 311, 411}
      };
	  girlsCoords = girlsCoordst;

	  int chipsPlacest[][] = {
		    { 184, 279}
		  , { 380, 140}
          , { 590, 279} //245+64
          , { 380, 362}
             
	  };
	  chipsPlaces = chipsPlacest;
	    
	  int namesPlacest[][] = 
	  {
		    { -4, 218}
		  , { 292, 51}
          , { 615, 218}
          , { 292, 437}
          
	  };
	  namesPlaces= namesPlacest;

	  int bublesPlacest[][] = {
		    { 71, 125, 0}
		  , { 375, 22, 0}
    	  , { 690, 104, 0}
		  , { 369, 396, 0}
      	  
	  };
	  bublesPlaces=bublesPlacest;
	    
	  int dealerChipPlacet[][] = {
		    { 150, 299}
		  , { 322, 140}
		  , { 610, 235}
		  , { 322, 385}
		  
	   };
	 dealerChipPlace = dealerChipPlacet;

	 //by rk
	 int playerNotePlacet[][] = 
	  {
		    { 4, 258}
		  , { 300, 91}
         , { 623, 258}
         , { 300, 477}
         
	  };
	 playerNotePlace = playerNotePlacet;
	
    
    //below code written in super class RoomSkin as it is common to all skins
    /**heapPlace = new Point(325, 310);
    centerCardsPlace = new Point(250, 210);
    dialerCardsPlace = new Point(380, 175);
    discardCardsPlace = new Point(285, 220);*/
	 for (int i = 0; i < 4; i++) {
         playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
         playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
         namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
         namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
         playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],cardConstOpen[i][1] - 20);
     	playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],cardConstClose[i][1] - 20);
     	chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
         dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
         playersBubleCoords[i] = new Point(bublesPlaces[i][0], bublesPlaces[i][1]);
         bublesOrientation[i] = bublesPlaces[i][2];
         plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
       }
	 /*for (int i = 0; i < 4; i++) {
         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
       }
      for (int i = 0; i < 4; i++) {
            playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
          }
    for (int i = 0; i < 4; i++) {
        playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],
                                                cardConstOpen[i][1] -
                                                20);
        playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],
                                                 cardConstClose[i][1] -
                                                 20);
    }

    for (int i = 0; i < 4; i++) {
        chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
        dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
    }
    for (int i = 0; i < 4; i++) {
      playersBubleCoords[i] = new Point(bublesPlaces[i][0],
                                           bublesPlaces[i][1]);
      bublesOrientation[i] = bublesPlaces[i][2];
      plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
    }*/
  }

//resize code
  public Point getPlayerCardsPlaceOpen(int ps, char sex) {
      return   new Point((int)(playerCardsPlacesOpen[ps].x*_ratio_x),(int)(playerCardsPlacesOpen[ps].y*_ratio_y));
  }
//  public Point getPlayerCardsPlaceOpen(int ps, char sex) {
//	  //ps = getRelativePos(ps);
//      return   playerCardsPlacesOpen[ps];
//  }
//resize code
  public Point getPlayerCardsPlaceClose(int ps, char sex) {
      //System.out.println("Num=" + num);
      return   new Point((int)(playerCardsPlacesClose[ps].x*_ratio_x),(int)(playerCardsPlacesClose[ps].y*_ratio_y));
  }
//  public Point getPlayerCardsPlaceClose(int ps, char sex) {
//      //System.out.println("Num=" + num);
//	  //ps = getRelativePos(ps);
//      return  playerCardsPlacesClose[ps];
//  }

//resize code
  public Point getPlayerPlace(int ps, char sex) {
      if (ps >= 0 && ps < 4) {
        return sex == ClientPlayerModel.MALE ?
            new Point((int)(playersPlace[ps][1].x*_ratio_x),(int)(playersPlace[ps][1].y*_ratio_y)):
           	 new Point((int)(playersPlace[ps][0].x*_ratio_x),(int)(playersPlace[ps][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getPlayerPlace(int ps, char sex) {
//    	//ps = getRelativePos(ps);
//        if (ps >= 0 && ps < 4) {
//         return sex == ClientPlayerModel.MALE ?
//             playersPlace[ps][1] :
//             playersPlace[ps][0];
//       }
//       return null;
//     }

  //resize code
  public Point getChipsPlace(int ps, char sex) {
      return new Point((int)(chipsPlace[ps].x*_ratio_x),(int)(chipsPlace[ps].y*_ratio_y));
   
  }
//  public Point getChipsPlace(int ps, char sex) {
//	  //ps = getRelativePos(ps);
//      return chipsPlace[ps];
//  }

//resize code
  public Point getPlayersBublesCoords(int ps, char sex) {
      return new Point((int)(playersBubleCoords[ps].x*_ratio_x),(int)(playersBubleCoords[ps].y*_ratio_y));
  }
//  public Point getPlayersBublesCoords(int ps, char sex) {
//      return playersBubleCoords[ps];
//  }

//resize code
  public Point getNamePos(int ps, char sex) {
      if (ps >= 0 && ps < 4) {
        return sex == ClientPlayerModel.MALE ?
        		new Point((int)(namePlace[ps][1].x*_ratio_x),(int)(namePlace[ps][1].y*_ratio_y)):
               	 new Point((int)(namePlace[ps][0].x*_ratio_x),(int)(namePlace[ps][0].y*_ratio_y));
      }
      return null;
    }
  
//    public Point getNamePos(int ps, char sex) {
//      //ps = getRelativePos(ps);
//      if (ps >= 0 && ps < 4) {
//        return sex == ClientPlayerModel.MALE ?
//            namePlace[ps][1] :
//            namePlace[ps][0];
//      }
//      return null;
//    }
  
//resize code
  public int getBublesOrientation(int ps) {
      return bublesOrientation[ps];
  }
//  public int getBublesOrientation(int ps) {
//      return bublesOrientation[ps];
//  }

  //resize code
  public Point getDealerPos(int ps) {
      return new Point((int)(dialerChip[ps].x*_ratio_x),(int)(dialerChip[ps].y*_ratio_y));
  }
//  public Point getDealerPos(int ps) {
//	  //ps = getRelativePos(ps);
//      return dialerChip[ps];
//  }

  public Point getOpenCardPos(int ps) {
      return new Point((int)(playerCardsPlacesOpen[ps].x*_ratio_x),(int)(playerCardsPlacesOpen[ps].y*_ratio_y));

  }
//  public Point getOpenCardPos(int ps) {
//	  ps = getRelativePos(ps);
//      return playerCardsPlacesOpen[ps];
//  }

  public int getRelativePos(int pos) {
	  return pos;
  }
  
  public Point getNamePosSize(){
      return new Point((int)(NAME_PLATE_WIDTH*_ratio_x),(int)(NAME_PLATE_HEIGHT*_ratio_y));
}
  
  public Point getPlrNotePos(int ps) {
	  return new Point((int)(plrNotePlace[ps].x*_ratio_x),(int)(plrNotePlace[ps].y*_ratio_y));
	  //return plrNotePlace[ps];
	}
  @Override
  public void setResizeRatio(double x, double y) {
	  super.setResizeRatio(x, y);
  }
  
  
}
