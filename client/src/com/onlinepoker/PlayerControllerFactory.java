package com.onlinepoker;

import javax.swing.JComponent;

import com.onlinepoker.skin.RoomSkin;
import com.poker.game.PokerGameType;


public class PlayerControllerFactory {
    public PlayerControllerFactory() {
    }

    public static ClientPlayerController getPlayerController(PokerGameType gt, ClientPokerModel pm, RoomSkin skin, int position,
                                  JComponent owner) {
            if (gt.isHoldem() || gt.isOmaha() || gt.isSitnGo() || gt.isTPoker()){
                return new HoldemPlayerController(pm, skin, position, owner);
            }
            else if (gt.isStud()){
                return new StudPlayerController(pm, skin, position, owner);
            }
//            else if (gt.isBadugi()){
//                return new BadugiPlayerController(pm, skin, position, owner);
//            }
//            else if (gt.isTeenPatti()){
//                return new TeenPattiPlayerController(pm, skin, position, owner);
//            }
            else {
                throw new IllegalStateException("Type not implemented yet " + gt);
            }
        
    }
    
    public static ClientPlayerController getPlayerController(PokerGameType gt, ClientPokerModel pm, ClientPlayerModel model, RoomSkin skin,
                                  JComponent owner, int position) {            
            if (gt.isHoldem() || gt.isOmaha() || gt.isSitnGo() || gt.isTPoker()){
                return new HoldemPlayerController(pm, model, skin, owner, position);
            }
            else if (gt.isStud()){
                return new StudPlayerController(pm, model, skin, owner, position);
            }
//            else if (gt.isBadugi()){
//                return new BadugiPlayerController(pm, model, skin, owner, position);
//            }
//            else if (gt.isTeenPatti()){
//                return new TeenPattiPlayerController(pm, model, skin, owner, position);
//            }
            else {
                throw new IllegalStateException("Type not implemented yet" + gt);
            }
    }

}
