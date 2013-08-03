/**
 * Object pool is a abstarct class and can be extended to provide a general
 * purpose object pool
 **/

package com.agneya.util;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;


public abstract class ObjectPool {
  private long expirationTime;
  private ConcurrentHashMap locked, unlocked;

  protected ObjectPool() {
    expirationTime = 30000; // 30 seconds
    locked = new ConcurrentHashMap();
    unlocked = new ConcurrentHashMap();
  }

  abstract Object create();

  abstract boolean validate(Object o);

  abstract void expire(Object o);

  synchronized Object checkOut() {
    long now = System.currentTimeMillis();
    Object o;
    if (unlocked.size() > 0) {
      Enumeration e = unlocked.keys();
      while (e.hasMoreElements()) {
        o = e.nextElement();
        if ( (now - ( (Long) unlocked.get(o)).longValue()) > expirationTime) {
          // object has expired
          unlocked.remove(o);
          expire(o);
          o = null;
        }
        else {
          if (validate(o)) {
            unlocked.remove(o);
            locked.put(o, new Long(now));
            return (o);
          }
          else {
            // object failed validation
            unlocked.remove(o);
            expire(o);
            o = null;
          }
        }
      }
    }
    // no objects available, create a new one
    o = create();
    locked.put(o, new Long(now));
    return (o);
  }

  synchronized void checkIn(Object o) {
    locked.remove(o);
    unlocked.put(o, new Long(System.currentTimeMillis()));
  }

}
