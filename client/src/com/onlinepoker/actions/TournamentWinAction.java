package com.onlinepoker.actions;

/**
 * Created by IntelliJ IDEA.
 * User: yuriy
 * Date: Oct 9, 2003
 * Time: 4:57:55 PM
 * To change this template use Options | File Templates.
 */
public class TournamentWinAction extends StageAction {

    private String[] names;
    private double[] sums;

    public TournamentWinAction(int id, String[] names, double[] sums) {
        super(id);
        this.names = names;
        this.sums = sums;
    }

    public String[] getNames() {
        return names;
    }

    public double[] getSums() {
        return sums;
    }

}
