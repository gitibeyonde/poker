package com.onlinepoker.skin;

import java.awt.Color;
import java.awt.Point;
import java.util.logging.Logger;

import com.onlinepoker.ClientPlayerModel;
import com.onlinepoker.Utils;


/**
 * This class holds graphical resources needed to draw the poker table.
 * It implements the Sigleton pattern.
 */
public class RoomSkinTen  extends RoomSkin
    implements java.io.Serializable {

  static Logger _cat = Logger.getLogger(RoomSkinSix.class.getName());

  /** font color for room's labels */
  protected Color fontColor;
  
  public RoomSkinTen(int mp) {
	  super(mp);
	  
	 int [][] pmapt = {
	      { -1 }, //0
	      { -1 }, //1
	      { 2, 7}, //2
	      { 2, 6, 8 }, //3
	      { 1, 3, 6, 8  }, //4
	      { 0, 2, 5, 6, 8  }, //5
	      { 1, 2, 3, 6, 7, 8 }, //6
	      { 0, 1, 2, 3, 5, 6, 8 }, //7
	      { 0, 1, 2, 3, 5, 6, 7, 8 },//8
	      { 0, 1, 2, 3, 5, 6, 7, 8, 9 }, //9
	      { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, // 10
	  };
	 _pmap = pmapt;
	  
	 int cardConstCloset[][] = {
			 {  81, 166}
	         , { 201, 81}
	         , { 378, 63}
	         , { 552, 82}
	         , { 669, 166}
	         , { 670, 303}
	         , { 552, 414}
	         , { 378, 449}
	         , { 201, 415}
	         , { 81, 304}
	     };
	 cardConstClose =cardConstCloset;

	 int cardConstOpent[][] = {
	    	 {  81, 166}
	         , { 201, 81}
	         , { 378, 63}
	         , { 552, 82}
	         , { 669, 166}
	         , { 670, 303}
	         , { 552, 414}
	         , { 378, 449}
	         , { 201, 415}
	         , { 81, 304}
	     };
	 cardConstOpen = cardConstOpent;

	  int mansCoordst[][] = {
		  {
	        	 15, 129, 102, 126}
	          , {
	          134, 45, 100, 113}
	          , {
	          311, 26, 110, 122}
	          , {
	          485, 45, 119, 119}
	          , {
	          604, 129, 127, 135}
	          , {
	          604, 268, 120, 137}
	          , {
	          485, 378, 106, 140}
	          , {
	          311, 412, 117, 144}
	          , {
	          134, 378, 113, 140}
	          , {
	          15, 267, 91, 125}
	      };
	  mansCoords = mansCoordst;

	  int girlsCoordst[][] = {
	         {
	        	 15, 129, 102, 126}
	          , {
	          134, 45, 100, 113}
	          , {
	          311, 26, 110, 122}
	          , {
	          485, 45, 119, 119}
	          , {
	          604, 129, 127, 135}
	          , {
	          604, 268, 120, 137}
	          , {
	          485, 378, 106, 140}
	          , {
	          311, 412, 117, 144}
	          , {
	          134, 378, 113, 140}
	          , {
	          15, 267, 91, 125}
	      };
	  girlsCoords = girlsCoordst;

	   int chipsPlacest[][] = {

			   {
			          184, 215}
			          , {
			          230, 167}
			          , {
			          375, 150}
			          , {
			          518, 167}
			          , {
			          560, 245}
			          , {
			          540, 310}
			          , {
			          450, 340}
			          , {
			          376, 365}
			          , {
			          265, 360}
			          , {
			         188, 324}
			      };
	    chipsPlaces = chipsPlacest;
	    int namesPlacest[][] = {
		 {
	          -4, 154}
	          , {
	          115, 70}
	          , {
	          292, 51}
	          , {
	          466, 70}
	          , {
	          585, 154}
	          , {
	          585, 292}
	          , {
	          466, 403}
	          , {
	          292, 437}
	          , {
	          115, 403}
	          , {
	          -4, 292}
	     };
	    namesPlaces = namesPlacest;

	    int bublesPlacest[][] = {
	         {
	         71, 125, 0}
	         , {
	         198, 41, 0}
	         , {
	         375, 22, 0}
	         , {
	         550, 39, 0}
	         , {
	         660, 104, 0}
	         , {
	         660, 247, 0}
	         , {
	         541, 365, 0}
	         , {
	         369, 396, 0}
	         , {
	         191, 363, 0}
	         , {
	         73, 256, 0}
	     };
	    bublesPlaces = bublesPlacest;

	  int dealerChipPlacet[][] = {
			  {150, 235}, 	//0
			     {270, 142},//1
			     {328, 145},//2 
			     {473, 142},//3
			     {570, 210},//4 
			     {580, 280},//5
			     {532, 362},//6 
			     {325, 389},//7
			     {230, 358},//8 
			     {158, 297},//9
			   };
//	    {144, 231}, {265, 137},
//	    {319, 135}, {463, 137},
//	    {584, 231}, {571, 329},
//	    {465, 386}, {319, 386},
//	    {261, 386}, {164, 329},
//	  };
	  dealerChipPlace = dealerChipPlacet;
	//by rk
		 int playerNotePlacet[][] = 
		 {
		   {  
			 4, 194}
       	     , {
	          123, 110}
	          , {
	          300, 91}
	          , {
	          473, 110}
	          , {
	          593, 194}
	          , {
	          593, 332}
	          , {
	          473, 443}
	          , {
	          300, 477}
	          , {
	          123, 443}
	          , {
	          4, 332}
				    
	      };
		 playerNotePlace = playerNotePlacet;
	
   
    //below code written in super class RoomSkin as it is common to all skins
    /**heapPlace = new Point(325, 310);
    centerCardsPlace = new Point(250, 210);
    dialerCardsPlace = new Point(380, 175);
    discardCardsPlace = new Point(285, 220);*/
		 for (int i = 0; i < 10; i++) {
	         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0], cardConstOpen[i][1] - 20);
            playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],  cardConstClose[i][1] -  20);
            chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
            dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
            playersBubleCoords[i] = new Point(bublesPlaces[i][0],bublesPlaces[i][1]);
            bublesOrientation[i] = bublesPlaces[i][2];
            plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
       }
		 /*for (int i = 0; i < 10; i++) {
	         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	       }
	   
      for (int i = 0; i < 10; i++) {
            playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
          }
    for (int i = 0; i < 10; i++) {
        playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],
                                                cardConstOpen[i][1] -
                                                20);
        playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],
                                                 cardConstClose[i][1] -
                                                 20);
    }

    for (int i = 0; i < 10; i++) {
        chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
        dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
    }
    for (int i = 0; i < 10; i++) {
      playersBubleCoords[i] = new Point(bublesPlaces[i][0],
                                           bublesPlaces[i][1]);
      bublesOrientation[i] = bublesPlaces[i][2];
    }
  //by rk, for player note
    for (int i = 0; i < 10; i++) {
    	plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
    }*/
  }
  //resize code
  public Point getPlayerCardsPlaceOpen(int ps, char sex) {
	  int num = mapPositionToPlace(ps);
      return   new Point((int)(playerCardsPlacesOpen[num].x*_ratio_x),(int)(playerCardsPlacesOpen[num].y*_ratio_y));
  }
//  public Point getPlayerCardsPlaceOpen(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//      return   playerCardsPlacesOpen[num];
//  }

  //resize code
  public Point getPlayerCardsPlaceClose(int ps, char sex) {
      //System.out.println("Num=" + num);
	  int num = mapPositionToPlace(ps);
      return   new Point((int)(playerCardsPlacesClose[num].x*_ratio_x),(int)(playerCardsPlacesClose[num].y*_ratio_y));
  }
//  public Point getPlayerCardsPlaceClose(int ps, char sex) {
//      int num = mapPositionToPlace(ps);
//      //System.out.println("Num=" + num);
//      return  playerCardsPlacesClose[num];
//  }

//resize code
  public Point getPlayerPlace(int ps, char sex) {
	  int num = mapPositionToPlace(ps);
      if (ps >= 0 && ps < 10) {
        return sex == ClientPlayerModel.MALE ?
            new Point((int)(playersPlace[num][1].x*_ratio_x),(int)(playersPlace[num][1].y*_ratio_y)):
           	 new Point((int)(playersPlace[num][0].x*_ratio_x),(int)(playersPlace[num][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getPlayerPlace(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//       if (num >= 0 && num < 10) {
//         return sex == ClientPlayerModel.MALE ?
//             playersPlace[num][1] :
//             playersPlace[num][0];
//       }
//       return null;
//     }

//resize code
  public Point getChipsPlace(int ps, char sex) {
	  int num = mapPositionToPlace(ps);
      return new Point((int)(chipsPlace[num].x*_ratio_x),(int)(chipsPlace[num].y*_ratio_y));
   
  }
//  public Point getChipsPlace(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//      return chipsPlace[num];
//   
//  }

//resize code
  public Point getPlayersBublesCoords(int ps, char sex) {
	  int num = mapPositionToPlace(ps);
      return new Point((int)(playersBubleCoords[num].x*_ratio_x),(int)(playersBubleCoords[num].y*_ratio_y));
  }
//  public Point getPlayersBublesCoords(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//      return playersBubleCoords[num];
//  }

//resize code
  public Point getNamePos(int ps, char sex) {
	  int num = mapPositionToPlace(ps);
      if (num >= 0 && num < 10) {
        return sex == ClientPlayerModel.MALE ?
        		new Point((int)(namePlace[num][1].x*_ratio_x),(int)(namePlace[num][1].y*_ratio_y)):
               	 new Point((int)(namePlace[num][0].x*_ratio_x),(int)(namePlace[num][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getNamePos(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//      if (num >= 0 && num < 10) {
//        return sex == ClientPlayerModel.MALE ?
//            namePlace[num][1] :
//            namePlace[num][0];
//      }
//      return null;
//    }
   
//resize code
  public int getBublesOrientation(int ps) {
	  int num = mapPositionToPlace(ps);
      return bublesOrientation[num];
  }
//  public int getBublesOrientation(int ps) {
//    int num = mapPositionToPlace(ps);
//      return bublesOrientation[num];
//  }

  //resize code
  public Point getDealerPos(int ps) {
	  int num = mapPositionToPlace(ps);
      return new Point((int)(dialerChip[num].x*_ratio_x),(int)(dialerChip[num].y*_ratio_y));
  }
//  public Point getDealerPos(int ps) {
//    int num = mapPositionToPlace(ps);
//      return dialerChip[num];
//  }

  //resize code
  public Point getOpenCardPos(int ps) {
	  int num = mapPositionToPlace(ps);
      return new Point((int)(playerCardsPlacesOpen[num].x*_ratio_x),(int)(playerCardsPlacesOpen[num].y*_ratio_y));

  }
//  public Point getOpenCardPos(int ps) {
//    int num = mapPositionToPlace(ps);
//      return playerCardsPlacesOpen[num];
//  }
  
 
  
  public int mapPositionToPlace(int position){
	  //getRelativePosition(position);
      //_cat.finest("room Size = " + _roomSize + " , pos " + position);
      if (position > _roomSize) throw new IllegalStateException("Wrong position " + position + " table size is " + _roomSize);
      int place = _pmap[_roomSize][position];
      //_cat.finest("Position " + position + " Place=" + place);
      return place;
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
