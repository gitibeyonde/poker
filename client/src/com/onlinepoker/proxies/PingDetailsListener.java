package com.onlinepoker.proxies;


/**
 * It is a Table Server message subscriber interface.
 * @author Kom
 */

public interface PingDetailsListener {

	
    public void pingDetailsReceived(long time, int at, int ap);

}
