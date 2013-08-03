package com.golconda.message;

import java.io.Serializable;

import java.util.HashMap;


public class Event
    implements Serializable {
  protected String _session;
  protected HashMap _hash;
  Object _handler;
  public String _cstr;

  public void handler(Object h) {
    _handler = h;
  }

  public Object handler() {
    return _handler;
  }

  public Event() {
    _hash = new HashMap();
  }

  public Event(String com) {
    _hash = parseNVPair(com);
    _cstr = com;
  }

  public Event(HashMap hash) {
    _hash = hash;
  }

  public HashMap getNVHash() {
    return _hash;
  }

  /**
   * parses the string representation of the game event
   *
   * @param url
   * @return
   */
  public static HashMap parseNVPair(String url) {
    HashMap h = new HashMap();
    if (url == null) {
      return h;
    }
    String decoded_url = url; //URLDecoder.decode(url);
    String[] nv = decoded_url.split("&");
    for (int i = 0; i < nv.length; i++) {
      int ind = nv[i].indexOf("=");
      h.put(nv[i].substring(0, ind), nv[i].substring(ind + 1));
    }
    return h;
  }

  public String toString() {
    return _cstr;
  }

}
