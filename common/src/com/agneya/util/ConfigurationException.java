// $Revision: 1.1 $
//
package com.agneya.util;

public class ConfigurationException
    extends Exception {

  public ConfigurationException(String msg) {
    super("config: " + msg);
  }

  public ConfigurationException() {
    super("Unable to read configuration");
  }
}
