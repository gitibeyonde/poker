package com.agneya.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TimerTask;


// Important:
//  - do not use routines from Log.java here
//  - do not introduce any dependency on any other class
//



/**
 * Configuration object stores all configuration information about the
 * system.
 *
 * @version $Revision: 1.2 $
 */

public class Configuration
    extends Properties {

  static String _rootpath = System.getProperty("CONFDIR");
  static final String CONFIG_FILE = System.getProperty("CONFFILE",
      "main.properties");
  static Configuration _conf = null;
  static Object _dummy = new Object();
  protected File _confFile = null;

  public static Configuration instance() throws ConfigurationException {
    if (_conf == null) {
      synchronized (_dummy) {
        if (_conf == null) {
          _conf = new Configuration();
        }
      }
    }
    return _conf;

  }

  public static Configuration getInstance() throws ConfigurationException {
    if (_conf == null) {
      synchronized (_dummy) {
        if (_conf == null) {
          _conf = new Configuration();
        }
      }
    }
    return _conf;

  }

  Configuration() throws ConfigurationException {
    InputStream in = null;
    try {
      if (_rootpath != null) {
        _confFile = new File(_rootpath + CONFIG_FILE);
      }
      else {
        _confFile = new File(getClass().getClassLoader().getResource(
            "main.properties").getPath());
      }
      in = new FileInputStream(_confFile);
      load(in);
      in.close();

    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("Configuration file: Conf file cannot loaded " + _rootpath + CONFIG_FILE );
      throw new ConfigurationException("Cannot open config file: " +
                                       e.getMessage());
    }

  }

  public int getInt(String str) {
    return Integer.parseInt( (String) (get(str) == null ? "-1" : get(str)));
  }

  public double getDouble(String str) {
    return Double.parseDouble( (String) (String) (get(str) == null ? "-1" :
                                                  get(str)));
  }

  public boolean getBoolean(String str) {
    String bl = (String) get(str);
    if (bl==null) return false;
    return bl.equalsIgnoreCase("true") || bl.equalsIgnoreCase("yes") ? true : false;
  }

  public Date getDate(String str) throws java.text.ParseException {
    SimpleDateFormat df1 = new SimpleDateFormat( "HH:mm:ss" );
    return df1.parse(str);
  }

  public class ConfigWatch
      extends TimerTask {
    long _last_check_time = System.currentTimeMillis();

    public void run() {
      try {
        long lmt = _confFile.lastModified();
        if (lmt > _last_check_time) {
          InputStream in = null;
          in = new FileInputStream(_confFile);
          load(in);
          in.close();
        }
      }
      catch (Exception ex) {
        //do nothing
      }
    }
  } // end ConfigWatch class

  public void saveConfig() {

    try {
      String tmp_str = null;
      StringBuilder sb = new StringBuilder();
      String key = null;
      String value = null;
      Enumeration e = _conf.keys();
      for (; e.hasMoreElements(); ) {
        key = (String) e.nextElement();
        value = (String) _conf.getProperty(key);
        sb.append(key).append("=").append(value).append("\n");
      }
      //BufferedWriter bw = new BufferedWriter();
      BufferedWriter bw = new BufferedWriter(new FileWriter(_confFile, false));
      tmp_str = sb.toString();
      System.out.println(tmp_str);
      bw.write(sb.toString(), 0, tmp_str.length());
      bw.flush();
      //br.close();
      bw.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

}
