package com.poker.shills;

import com.golconda.game.util.ActionConstants;
import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;
import com.golconda.game.util.HandEvaluator;
import com.golconda.message.Command;
import com.poker.game.PokerGameType;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;
import com.poker.game.poker.Pot;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;

import java.util.Comparator;
import java.util.logging.Logger;

public class BotUtils implements ActionConstants {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotUtils.class.getName());

    public static int getMoveId(String mov) {
        int mov_id = -99;
        if (mov.equals("join")) {
            mov_id = Command.M_JOIN;
        }
        else if (mov.equals("open")) {
            mov_id = Command.M_OPEN;
        }
        else if (mov.equals("check")) {
            mov_id = Command.M_CHECK;
        }
        else if (mov.equals("call")) {
            mov_id = Command.M_CALL;
        }
        else if (mov.equals("raise")) {
            mov_id = Command.M_RAISE;
        }
        else if (mov.equals("fold")) {
            mov_id = Command.M_FOLD;
        }
        else if (mov.equals("draw-cards")) {
            mov_id = Command.M_PICK;
        }
        else if (mov.equals("drop-cards")) {
            mov_id = Command.M_DUMP;
        }
        else if (mov.equals("leave")) {
            mov_id = Command.M_LEAVE;
        }
        else if (mov.equals("sit-in")) {
            mov_id = Command.M_SIT_IN;
        }
        else if (mov.equals("opt-out")) {
            mov_id = Command.M_OPT_OUT;
        }
        else if (mov.equals("wait")) {
            mov_id = Command.M_WAIT;
        }
        else if (mov.equals("small-blind")) {
            mov_id = Command.M_SMALLBLIND;
        }
        else if (mov.equals("big-blind")) {
            mov_id = Command.M_BIGBLIND;
        }
        else if (mov.equals("sb-bb")) {
            mov_id = Command.M_SBBB;
        }
        else if (mov.equals("bet")) {
            mov_id = Command.M_BET;
        }
        else if (mov.equals("ante")) {
            mov_id = Command.M_ANTE;
        }
        else if (mov.equals("all-in")) {
            mov_id = Command.M_ALL_IN;
        }
        else if (mov.equals("bet-pot")) {
            mov_id = Command.M_BET_POT;
        }
        else if (mov.equals("bringin")) {
            mov_id = Command.M_BRING_IN;
        }
        else if (mov.equals("complete")) {
            mov_id = Command.M_COMPLETE;
        }
        else {
            mov_id = Command.M_ILLEGAL;
        }
        return mov_id;
    }

    public static int gameStage(Card comm_hand[]){
        int commCardCount = comm_hand == null ? 0 : comm_hand.length;
        //log.finest("commCardCount = " + commCardCount);
        int stage=-1;
        switch (commCardCount) {
            case 0:
                //log.finest("stage is PREFLOP");
                stage = PREFLOP;
                break;
            case 3:
                //log.finest("stage is FLOP");
                stage = FLOP;
                break;
            case 4:
                //log.finest("stage is TURN");
                stage = TURN;
                break;
            case 5:
                //log.finest("stage is RIVER");
                stage = RIVER;
                break;
            default:
                //log.finest("stage is UNKNOWN");
                stage = -1;
                break;
        }
        return -1;
    }

    public static int handRank(Poker pgm, PokerPresence me){
        Pot p = pgm.pots().get(0);
        PokerPresence[] v = p.contenders();
        final Card[] ch = pgm.getCommunityCards();
        final int gt = pgm._type.intVal();
        java.util.Arrays.sort(v, new Comparator() {
            public int compare(Object o1, Object o2) {
                Card[] pc2 = ((PokerPresence) o2).getHand().getCardsArray();
                Card[] pc1 = ((PokerPresence) o1).getHand().getCardsArray();
                Cards ph1 = new Cards(false);
                ph1.addCards(pc1);
                ph1.addCards(ch);
                Cards ph2 = new Cards(false);
                ph2.addCards(pc2);
                ph2.addCards(ch);
                return HandEvaluator.rankHand(ph2) - HandEvaluator.rankHand(ph1);
            }
        });
        //_cat.info("Community Cards = " + pgm.communityCardsString());
        for (int i=0;i<v.length;i++){
            //_cat.info("Better hands = " + v[i].name() + "=" + v[i].getHand().getAllCardsString());
            if (v[i].equals(me)){
                return (i * 100)/v.length;
            }
        }
        return 100;
    }

    public static void main(String args[]){
        Cards ch = new Cards("AS7D6C5H", false);
        Hand hch = new Hand(ch.getCards());
        Cards cv1 = new Cards("ADACAS6S4S4SAH", false);
        Hand hcv1 = new Hand(cv1.getCards());
        Cards cv2 = new Cards("6D8D9D5D4D3C2C", false);
        Hand hcv2 = new Hand(cv2.getCards());

        System.out.println(hch.getAllCardsString());

        System.out.println(HandEvaluator.rankHand(cv1));
        System.out.println(HandEvaluator.rankHand(cv2));


        long result = HandComparator.compareGameHand(hcv1.getCards(),
                hcv2.getCards(),
                hch.getCards(), PokerGameType.HOLDEM, true)[0];

        System.out.println("Result=" + result);

    }


}
