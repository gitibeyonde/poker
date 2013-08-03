package com.onlinepoker.skin;

import java.awt.Point;
import java.util.logging.Logger;

import com.onlinepoker.ClientPlayerModel;
import com.onlinepoker.Utils;


/**
 * This class holds graphical resources needed to draw the poker table.
 * It implements the Sigleton pattern.
 */
public class RoomSkinTwo extends RoomSkin
    implements java.io.Serializable {

  static Logger _cat = Logger.getLogger(RoomSkinSix.class.getName());
  public int _maxPlayer=2;
	
  public RoomSkinTwo(int mp) {
	  super(mp);
		  
		 int cardConstCloset[][] = {
				  { 375, 66}
		           , { 375, 452}
		       
		     };
		 cardConstClose =cardConstCloset;

		 int cardConstOpent[][] = {
				  { 375, 66}
		           , { 375, 452}
		       };
		 cardConstOpen = cardConstOpent;

		  int mansCoordst[][] = {
				  {
			           311, 25}
			           , 
			           {
			           311, 411}
			     };
		  mansCoords = mansCoordst;

		  int girlsCoordst[][] = {
			  {
		           311, 25}
		           , 
		           {
		           311, 411}
		     };
		  girlsCoords = girlsCoordst;

		   int chipsPlacest[][] = {
		         {
		        	 380, 140}
                 , {
                	 380, 362}
		         
		     };
		    chipsPlaces = chipsPlacest;
		    int namesPlacest[][] = {
		    	{
		    		 292, 51}
		           ,{
		           292, 437}
		        
		     };
		    namesPlaces = namesPlacest;

		    int bublesPlacest[][] = {
		        {
		         375, 22, 0}
		         ,  {
		         369, 396, 0}
		        
		     };
		    bublesPlaces = bublesPlacest;

		  int dealerChipPlacet[][] = {
				  { 322, 140}, {322, 385}
		   
		  };
		  dealerChipPlace = dealerChipPlacet;

		  //by rk
		 int playerNotePlacet[][] = 
		  {
			    { 300, 91}
			  , { 300, 477}
		  };
		 playerNotePlace = playerNotePlacet;
	
    
    
   
    //below code written in super class RoomSkin as it is common to all skins
    /**heapPlace = new Point(325, 310);
    centerCardsPlace = new Point(250, 210);
    dialerCardsPlace = new Point(380, 175);
    discardCardsPlace = new Point(235, 220);*/
		 for (int i = 0; i < 2; i++) {
	         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
	         playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
	         namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
	         namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
	         playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0], cardConstOpen[i][1] - 20);
	         playerCardsPlacesClose[i] = new Point(cardConstClose[i][0], cardConstClose[i][1] - 20);
        	 playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
             playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
             namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
             namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
             playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0], cardConstOpen[i][1] - 20);
             playerCardsPlacesClose[i] = new Point(cardConstClose[i][0], cardConstClose[i][1] - 20);
             chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
             dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
             playersBubleCoords[i] = new Point(bublesPlaces[i][0], bublesPlaces[i][1]);
             plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
             bublesOrientation[i] = bublesPlaces[i][2];
	       }	 
		 /*for (int i = 0; i < 2; i++) {
	         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	       }
	    
      for (int i = 0; i < 2; i++) {
            playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
          }
    for (int i = 0; i < 2; i++) {
        playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],
                                                cardConstOpen[i][1] -
                                                20);
        playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],
                                                 cardConstClose[i][1] -
                                                 20);
    }

    for (int i = 0; i < 2; i++) {
        chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
        dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
    }
    for (int i = 0; i < 2; i++) {
      playersBubleCoords[i] = new Point(bublesPlaces[i][0],
                                           bublesPlaces[i][1]);
      bublesOrientation[i] = bublesPlaces[i][2];
    }
  //by rk, for player note
    for (int i = 0; i < 2; i++) {
    	plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
    }*/
  }

//resize code
  public Point getPlayerCardsPlaceOpen(int ps, char sex) {
      return   new Point((int)(playerCardsPlacesOpen[ps].x*_ratio_x),(int)(playerCardsPlacesOpen[ps].y*_ratio_y));
  }
//  public Point getPlayerCardsPlaceOpen(int ps, char sex) {
//      return   playerCardsPlacesOpen[ps];
//  }

  //resize code
  public Point getPlayerCardsPlaceClose(int ps, char sex) {
      //System.out.println("Num=" + num);
      return   new Point((int)(playerCardsPlacesClose[ps].x*_ratio_x),(int)(playerCardsPlacesClose[ps].y*_ratio_y));
  }
//  public Point getPlayerCardsPlaceClose(int ps, char sex) {
//      //System.out.println("Num=" + num);
//      return  playerCardsPlacesClose[ps];
//  }

//resize code
  public Point getPlayerPlace(int ps, char sex) {
      if (ps >= 0 && ps < 2) {
        return sex == ClientPlayerModel.MALE ?
            new Point((int)(playersPlace[ps][1].x*_ratio_x),(int)(playersPlace[ps][1].y*_ratio_y)):
           	 new Point((int)(playersPlace[ps][0].x*_ratio_x),(int)(playersPlace[ps][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getPlayerPlace(int ps, char sex) {
//       if (ps >= 0 && ps < 2) {
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
//      return chipsPlace[ps];
//   
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
      if (ps >= 0 && ps < 2) {
        return sex == ClientPlayerModel.MALE ?
        		new Point((int)(namePlace[ps][1].x*_ratio_x),(int)(namePlace[ps][1].y*_ratio_y)):
               	 new Point((int)(namePlace[ps][0].x*_ratio_x),(int)(namePlace[ps][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getNamePos(int ps, char sex) {
//      if (ps >= 0 && ps < 2) {
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
//      return dialerChip[ps];
//  }

//resize code
  public Point getOpenCardPos(int ps) {
      return new Point((int)(playerCardsPlacesOpen[ps].x*_ratio_x),(int)(playerCardsPlacesOpen[ps].y*_ratio_y));

  }
//  public Point getOpenCardPos(int ps) {
//      return playerCardsPlacesOpen[ps];
//  }
  
  public Point getNamePosSize(){
      return new Point((int)(NAME_PLATE_WIDTH*_ratio_x),(int)(NAME_PLATE_HEIGHT*_ratio_y));
}
//  public Point getNamePosSize(){
//      return new Point((int)(NAME_PLATE_WIDTH*_ratio_x),(int)(NAME_PLATE_HEIGHT*_ratio_y));
//}
    
  public Point getPlrNotePos(int ps) {
	  return new Point((int)(plrNotePlace[ps].x*_ratio_x),(int)(plrNotePlace[ps].y*_ratio_y));
	  //return plrNotePlace[ps];
	}
  @Override
  public void setResizeRatio(double x, double y) {
	  super.setResizeRatio(x, y);
  }
  
}
