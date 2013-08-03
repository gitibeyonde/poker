package com.onlinepoker.actions;

/**
 * Created by IntelliJ IDEA.
 * User: yuriy
 * Date: Oct 8, 2003
 * Time: 12:22:22 PM
 * To change this template use Options | File Templates.
 */
public class NewHandAction extends BettingAction {

    private int[] states;

    public NewHandAction(int id, int handId, int[] states) {
        super(id, 0, handId);
        this.states = states;
    }

    public int[] getStates() {
        return states;
    }

}
