package com.golconda.net.event;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;


public class AdminEvent {
 public ConcurrentHashMap _hash;

 public AdminEvent(String com) {
   _hash = parseNVPair(com);
 }

 public String get(String key){
   return (String)_hash.get(key);
 }

 public long getLong(String key){
    return Long.parseLong((String)_hash.get(key));
  }

  public int getInt(String key){
     return Integer.parseInt((String)_hash.get(key));
   }

 public String toString() {
   StringBuilder str = new StringBuilder();
   Enumeration enumer= _hash.keys();
   String name;
   for (;enumer.hasMoreElements();){
     name = (String)enumer.nextElement();
     str.append(name).append("=").append((String)_hash.get(name)).append("&");
   }
   str.deleteCharAt(str.length()-1);
   return str.toString();
 }

 /**
    * parses the string representation of the game event
    *
    * @param url
    * @return
    */
   public static ConcurrentHashMap parseNVPair(String url) {
	   ConcurrentHashMap h = new ConcurrentHashMap();
     if (url == null) {
       return h;
     }
     String decoded_url = url; //URLDecoder.decode(url);
     String[] nv = decoded_url.split("&");
     for (int i = 0; i < nv.length; i++) {
       String token = nv[i];
       int ind = nv[i].indexOf("=");
       String name = nv[i].substring(0, ind).trim();
       String value = nv[i].substring(ind + 1).trim();
       h.put(name, value);
     }
     return h;
   }

}
