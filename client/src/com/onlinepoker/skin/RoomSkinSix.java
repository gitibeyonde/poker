package com.onlinepoker.skin;

import java.awt.Point;
import java.util.logging.Logger;

import com.onlinepoker.ClientPlayerModel;
import com.onlinepoker.Utils;


/**
 * This class holds graphical resources needed to draw the poker table.
 * It implements the Sigleton pattern.
 */
public class RoomSkinSix  extends RoomSkin
    implements java.io.Serializable {

  static Logger _cat = Logger.getLogger(RoomSkinSix.class.getName());
  public int _maxPlayer=6;

   
  public RoomSkinSix(int mp) {
	  super(mp);
	  
	  
	  int cardConstCloset[][] = {
			  { 112, 112}
	          , { 374, 62}
	          , { 641, 111}
	          , { 650, 340}
	          , { 375, 448}
	          , { 112, 340}
	      };
	  cardConstClose =cardConstCloset;
	  
	  int cardConstOpent[][] = {
			  { 112, 112}
	          , { 374, 62}
	          , { 641, 111}
	          , { 650, 340}
	          , { 375, 448}
	          , { 112, 340}
	      };
	  
//	  { 112, 97}
//      , { 374, 47}
//      , { 641, 96}
//      , { 650, 325}
//      , { 375, 433}
//      , { 112, 325}
//  };
//  
//	  
//	  { 112, 110}
//      , { 374, 63}
//      , { 641, 110}
//      , { 650, 350}
//      , { 375, 445}
//      , { 112, 350}
//  };
	  cardConstOpen = cardConstOpent;

	  int mansCoordst[][] = {
         {
             48, 76, 100, 113}
             , {
             310, 26, 110, 122}
             , {
             577, 75, 119, 119}
             , {
             586, 304, 106, 140}
             , {
             311, 412, 117, 144}
             , {
             48, 304, 113, 140}
             
         };
	  mansCoords = mansCoordst;

	   int girlsCoordst[][] = {
	     {
             48, 76, 100, 113}
             , {
             310, 26, 110, 122}
             , {
             577, 75, 119, 119}
             , {
             586, 304, 106, 140}
             , {
             311, 412, 117, 144}
             , {
             48, 304, 113, 140}
             
         };
	   girlsCoords = girlsCoordst;

	    int chipsPlacest[][] = {
	         {
	         215, 155}
	         , {
	         365, 145}
	         , {
	         515, 165}
	         ,{
	         515, 323}
	         , {
	         375, 360}
	         , {
	         200, 335}
	        
	     };
	    chipsPlaces = chipsPlacest;
	    
	    int namesPlacest[][] = 
		{
	          {
	          29, 102}
	          , {
	          291, 51}
	          , {
	          558, 101}
	          ,  {
	          567, 329}
	          , {
	          291, 437}
	          , {
	          29, 329}
	    
	      };
	    namesPlaces= namesPlacest;


	    int bublesPlacest[][] = {
	        {
	         198, 41, 0}
	         , {
	         375, 22, 0}
	         , {
	         550, 39, 0}
	         , {
	         541, 365, 0}
	         , {
	         369, 396, 0}
	         , {
	         191, 363, 0}
	     };
	    bublesPlaces=bublesPlacest;
	    
	 int dealerChipPlacet[][] = {
		     {170, 190},
		     {325, 145},
		     {580, 200},
		     {585, 285}, 
		     {315, 387},
		     {147, 290}
		   };
	 dealerChipPlace = dealerChipPlacet;
	//by rk
	 int playerNotePlacet[][] = 
	 {
	          {
	          37, 142}
	          , {
	          299, 91}
	          , {
	          566, 141}
	          ,  {
	          575, 369}
	          , {
	          299, 477}
	          , {
	          37, 369}
	    
      };
	 playerNotePlace = playerNotePlacet;
	 
	
    
    //below code written in super class RoomSkin as it is common to all skins
    /**heapPlace = new Point(325, 310);
    centerCardsPlace = new Point(250, 210);
    dialerCardsPlace = new Point(380, 175);
    discardCardsPlace = new Point(285, 220);*/
	 for (int i = 0; i < 6; i++) {
         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
         playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
         playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
         namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
         namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
         playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0], cardConstOpen[i][1] - 20);
         playerCardsPlacesClose[i] = new Point(cardConstClose[i][0], cardConstClose[i][1] - 20);
         chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
         dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
         playersBubleCoords[i] = new Point(bublesPlaces[i][0],
         bublesPlaces[i][1]);
         bublesOrientation[i] = bublesPlaces[i][2];
         plrNotePlace[i] = new Point(playerNotePlace[i][0], playerNotePlace[i][1]);
       } 
	 /*for (int i = 0; i < 6; i++) {
         players[i][0] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
         players[i][1] = Utils.getIcon("images/avatars/" + (i + 1) + ".png");
       }
 
      for (int i = 0; i < 6; i++) {
            playersPlace[i][1] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            playersPlace[i][0] = new Point(girlsCoords[i][0], girlsCoords[i][1]);
            namePlace[i][0] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
            namePlace[i][1] = new Point(namesPlaces[i][0], namesPlaces[i][1]);
          }
    for (int i = 0; i < 6; i++) {
        playerCardsPlacesOpen[i] = new Point(cardConstOpen[i][0],
                                                cardConstOpen[i][1] -
                                                20);
        playerCardsPlacesClose[i] = new Point(cardConstClose[i][0],
                                                 cardConstClose[i][1] -
                                                 20);
    }

    for (int i = 0; i < 6; i++) {
        chipsPlace[i] = new Point(chipsPlaces[i][0], chipsPlaces[i][1]);
        dialerChip[i] = new Point(dealerChipPlace[i][0], dealerChipPlace[i][1]);
    }
    for (int i = 0; i < 6; i++) {
      playersBubleCoords[i] = new Point(bublesPlaces[i][0],
                                           bublesPlaces[i][1]);
      bublesOrientation[i] = bublesPlaces[i][2];
    }
  //by rk, for player note
    for (int i = 0; i < 6; i++) {
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
      if (ps >= 0 && ps < 6) {
        return sex == ClientPlayerModel.MALE ?
            new Point((int)(playersPlace[ps][1].x*_ratio_x),(int)(playersPlace[ps][1].y*_ratio_y)):
           	 new Point((int)(playersPlace[ps][0].x*_ratio_x),(int)(playersPlace[ps][0].y*_ratio_y));
      }
      return null;
    }

//    public Point getPlayerPlace(int ps, char sex) {
//    	//ps = getRelativePos(ps);
//        if (ps >= 0 && ps < 6) {
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
      if (ps >= 0 && ps < 6) {
        return sex == ClientPlayerModel.MALE ?
        		new Point((int)(namePlace[ps][1].x*_ratio_x),(int)(namePlace[ps][1].y*_ratio_y)):
               	 new Point((int)(namePlace[ps][0].x*_ratio_x),(int)(namePlace[ps][0].y*_ratio_y));
      }
      return null;
    }
//    public Point getNamePos(int ps, char sex) {
//      //ps = getRelativePos(ps);
//      if (ps >= 0 && ps < 6) {
//        return sex == ClientPlayerModel.MALE ?
//            namePlace[ps][1] :
//            namePlace[ps][0];
//      }
//      return null;
//    }
  public Point getNamePosSize(){
      return new Point((int)(NAME_PLATE_WIDTH*_ratio_x),(int)(NAME_PLATE_HEIGHT*_ratio_y));
}
    
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

  //resize code
  public Point getOpenCardPos(int ps) {
      return new Point((int)(playerCardsPlacesOpen[ps].x*_ratio_x),(int)(playerCardsPlacesOpen[ps].y*_ratio_y));

  }
//  public Point getOpenCardPos(int ps) {
//	  ps = getRelativePos(ps);
//      return playerCardsPlacesOpen[ps];
//  }

  public int getRelativePos(int pos) {
	  return pos;
	 /*int start;
	if (ClientPokerModel._me != null){
		start = ClientPokerModel._me.getPlayerPosition();
		int rel_pos = pos - start;
		if (rel_pos < 0)rel_pos += _maxPlayer;
		rel_pos += 4;
		if(rel_pos >5)rel_pos-=6;
		//System.out.println("RoomSkinSix getRelPos: "+rel_pos);
		return rel_pos;
	}
	else {
		return pos;
	}*/
	
  }
  
  /*public int getRelativePos(int pos) {
	if (ClientPokerModel._me != null){
		int start = ClientPokerModel._me.getPlayerPosition();
		int rel_pos = pos + start;
		if(rel_pos > 5)rel_pos -=6;
		System.out.println("RoomSkinSix getRelPos: "+rel_pos);
		return rel_pos;
	}
	else {
		return pos;
	}
	
  }*/
  public Point getPlrNotePos(int ps) {
	  return new Point((int)(plrNotePlace[ps].x*_ratio_x),(int)(plrNotePlace[ps].y*_ratio_y));
	  //return plrNotePlace[ps];
	}
  public static void main(String[] args) {
      RoomSkinSix ra = new RoomSkinSix(6);
      System.out.println(ra.getRelativePos(5));
  }
  @Override
  public void setResizeRatio(double x, double y) {
	  super.setResizeRatio(x, y);
  }
  
}
