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
public class RoomSkinNine  extends RoomSkin
    implements java.io.Serializable {

  static Logger _cat = Logger.getLogger(RoomSkinSix.class.getName());

  /** font color for room's labels */
  protected Color fontColor;
  
  public RoomSkinNine(int mp) {
	  super(mp);
	  
	 int [][] pmapt = {
	      { -1 }, //0
	      { -1 }, //1
	      //{ 2, 7}, //2
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
			 {  113, 141}
	         , { 303, 75}
	         , { 503, 75}
	         , { 665, 160}
	         , { 682, 302}
	         , { 570, 408}
	         , { 384, 448}
	         , { 189, 408}
	         , { 87, 292}
	     };
	 cardConstClose =cardConstCloset;

	 int cardConstOpent[][] = {
	    	 {  113, 141}
	         , { 303, 75}
	         , { 503, 75}
	         , { 665, 160}
	         , { 682, 302}
	         , { 570, 408}
	         , { 384, 448}
	         , { 189, 408}
	         , { 87, 292}
	     };
	 cardConstOpen = cardConstOpent;

	  int mansCoordst[][] = {
		  {
	        	 49, 104}
	          , {
	          239, 38}
	          , {
	          439, 38}
	          , {
	          601, 123}
	          , {
	          618, 265}
	          , {
	          506, 371}
	          , {
	          320, 411}
	          , {
	          125, 371}
	          , {
	          23, 255}
	      };
	  mansCoords = mansCoordst;

	  int girlsCoordst[][] = {
			  {
		        	 49, 104}
		          , {
		          239, 38}
		          , {
		          439, 38}
		          , {
		          601, 123}
		          , {
		          618, 265}
		          , {
		          506, 371}
		          , {
		          320, 411}
		          , {
		          125, 371}
		          , {
		          23, 255}
		      };
	  girlsCoords = girlsCoordst;

	   int chipsPlacest[][] = {
			   {
			          184, 180}
			          , {
			          316, 149}
			          , {
			          466, 149}
			          , {
			          538, 193}
			          , {
			          558, 264}
			          , {
			          466, 318}
			          , {
			          360, 349}
			          , {
			          245, 331}
			          , {
			         159, 269}
			      };
	    chipsPlaces = chipsPlacest;
	    int namesPlacest[][] = {
		 {
			  30, 130}
	          , {
	          220, 64}
	          , {
	          425, 64}
	          , {
	          582, 149}
	          , {
	          599, 291}
	          , {
	          487, 397}
	          , {
	          301, 437}
	          , {
	          106, 397}
	          , {
	          4, 281}
	     };
	    namesPlaces = namesPlacest;

	    int bublesPlacest[][] = {
	         {
	         71, 125, 0}
	         , {
	         198, 41, 0}
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
			  {162, 232}, 	//0
			     {270, 158},//1
			     {431, 145},//2
			     {591, 185},//3 
			     {591, 340},//4
			     {479, 386},//5 
			     {335, 383},//6
			     {214, 361},//7 
			     {174, 331},//8
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
		   { 4, 258}
		     , {
		     82, 111}
		     , {
		     300, 91}
		     ,  {
		     513, 111}
		     , {
		     623, 258}
		     , {
		     513, 407}
		     , {
		     300, 472}
		     , {
		     82, 407}
				    
	      };
		 playerNotePlace = playerNotePlacet;
	
   
    //below code written in super class RoomSkin as it is common to all skins
    /**heapPlace = new Point(325, 310);
    centerCardsPlace = new Point(250, 210);
    dialerCardsPlace = new Point(380, 175);
    discardCardsPlace = new Point(285, 220);*/
		 for (int i = 0; i < 9; i++) {
	         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],cardConstOpen[i][1] -20);
            playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],cardConstClose[i][1] -20);
            chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
            dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
            playersBubleCoords[i] = new Point(bublesPlaces[i][0],bublesPlaces[i][1]);
            bublesOrientation[i] = bublesPlaces[i][2];
            plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
	       }
		 /*for (int i = 0; i < 9; i++) {
	         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
	       }
	   
      for (int i = 0; i < 9; i++) {
            playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
          }
    for (int i = 0; i < 9; i++) {
        playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],
                                                cardConstOpen[i][1] -
                                                20);
        playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],
                                                 cardConstClose[i][1] -
                                                 20);
    }

    for (int i = 0; i < 9; i++) {
        chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
        dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
    }
    for (int i = 0; i < 9; i++) {
      playersBubleCoords[i] = new Point(bublesPlaces[i][0],
                                           bublesPlaces[i][1]);
      bublesOrientation[i] = bublesPlaces[i][2];
    }
  //by rk, for player note
    for (int i = 0; i < 9; i++) {
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
      if (ps >= 0 && ps < 9) {
        return sex == ClientPlayerModel.MALE ?
            new Point((int)(playersPlace[num][1].x*_ratio_x),(int)(playersPlace[num][1].y*_ratio_y)):
           	 new Point((int)(playersPlace[num][0].x*_ratio_x),(int)(playersPlace[num][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getPlayerPlace(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//       if (num >= 0 && num < 9) {
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
      if (ps >= 0 && ps < 9) {
        return sex == ClientPlayerModel.MALE ?
        		new Point((int)(namePlace[num][1].x*_ratio_x),(int)(namePlace[num][1].y*_ratio_y)):
               	 new Point((int)(namePlace[num][0].x*_ratio_x),(int)(namePlace[num][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getNamePos(int ps, char sex) {
//    int num = mapPositionToPlace(ps);
//      if (num >= 0 && num < 9) {
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
