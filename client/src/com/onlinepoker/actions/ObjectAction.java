package com.onlinepoker.actions;

/**
 * Created by IntelliJ IDEA.
 * User: yuriy
 * Date: Jul 14, 2003
 * Time: 1:31:58 PM
 * To change this template use Options | File Templates.
 */
public class ObjectAction extends TableServerAction {

    private Object obj;

    public ObjectAction(int id, int target, Object obj) {
        super(id, target);
        this.obj = obj;
    }

    public Object getObject() {
        return obj;
    }

}
