package com.poker.game.poker;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;

import com.golconda.game.Game;

import com.poker.game.PokerGameType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Auditor {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(Auditor.class.getName());

  private static ConcurrentHashMap _writeLogger;
  private static ConcurrentHashMap _lastHand;
  private static String _clf;
  private static long _clfl;
  private static String _dirName;
  private static java.io.File _lf;

  private boolean _l = false;
  private static Object _dummy = new Object();
  private static Auditor _aud = null;

  public static Auditor instance() {
    if (_aud == null) {
      synchronized (_dummy) {
        if (_aud == null) {
          _aud = new Auditor();
        }
      }
    }
    return _aud;
  }

  private Auditor() {
    try {
      _clfl = Configuration.getInstance().getInt("Auditor.Logfile.length");
      _dirName = (String) Configuration.getInstance().get("Auditor.Logfile");
      _writeLogger = new ConcurrentHashMap();
      _lastHand = new ConcurrentHashMap();
    }
    catch (ConfigurationException e) {
      _cat.log(Level.WARNING, "Unable to configure AuditClient , Taking default values ", e);
      _clfl = 10000000;
      _dirName = "./logs/player";
    }
  }

  private PrintWriter initPrintWriter(String gid, long grid) {
    try {
      _clf = _dirName + "/" + gid;
      new java.io.File(_clf).mkdirs();
      _clf = _clf + "/" + grid + ".xml";
      _lf = new File(_clf);
      _cat.info("Client authenticated, setting up log file " + _clf);
      return new PrintWriter(new BufferedWriter(new FileWriter(_lf)));
    }
    catch (Exception e) {
      _cat.log(Level.WARNING, "Logging failed for client " + "AuditClient", e);
    }
    return null;
  }

  public synchronized void write(String gid, long grid, String log, boolean close) {
    _cat.finest(gid + ":" + grid + ":" + log);
    assert gid != null : "Game id is " + gid;
    assert grid > 0 : "GRID is " + grid;
    String lhand = (String) _lastHand.get(gid);
    String key;
    if (lhand != null) {
      if (! (lhand.equals(grid + ""))) {
        // previous hand did not close normally
        key = gid + "-" + lhand;
        PrintWriter prev = (PrintWriter) _writeLogger.get(key);
        if (prev!=null){
          prev.println("<error reason=\"unclean close\" />");
          prev.println("</game>");
          prev.flush();
          prev.close();
          _writeLogger.remove(key);
          _cat.finest("Unclean close");
        }
      }
    }
    else {
      _lastHand.put(gid + "", grid + "");
    }

    key = gid + "-" + grid;
    PrintWriter p = (PrintWriter) _writeLogger.get(key);
    if (p == null) {
      //create a new writer
      p = initPrintWriter(gid, grid);
      _writeLogger.put(key, p);
      p.println("<game  type=\"" + Game.game(gid).type() + "\"    hand_id=\"" + key + "\"  name=\""  +  Game.game(gid)._name + "\">");
    }
    assert p != null:"Failed to create a printwriter " + gid;
    p.println(log);
    if (close) {
      p.println("</game>");
      p.flush();
      _cat.finest("Flushed.........");
      p.close();
      _writeLogger.remove(key);
      _lastHand.remove(gid);
    }
  }

  public static void flush() {

  }

  public synchronized void write(PrintWriter p, String s) {
    _cat.finest(s);
    p.println(s);
  }

  public static void main(String args[]) {
    File f = new File("./log/player/1234/56789.xml");
    f.mkdirs();
  }

}
