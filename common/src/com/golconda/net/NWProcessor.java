package com.golconda.net;

import com.golconda.net.event.AdminEvent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface NWProcessor {
  public AdminEvent process(AdminEvent ae);
}
