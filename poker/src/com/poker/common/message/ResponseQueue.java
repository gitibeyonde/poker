package com.poker.common.message;

import com.golconda.message.Event;
import com.golconda.message.Response;

import java.util.LinkedList;
import java.util.NoSuchElementException;


/**
 * Client queue is singleton. Clients which have received a GameEvent are
 * lined up in this queue for processing.
 * ClientQueue is created when the Poker server is started
 *
 **/

public class ResponseQueue {
  //private static Logger _cat = Logger.getLogger(ResponseQueue.class.getName());
  String _name; // queue name
  private LinkedList _items;

  public ResponseQueue() {
    _name = "Response Queue";
    _items = new LinkedList();
  }

  public synchronized int size() {
    return _items.size();
  }

  // add message to queue
  public synchronized void put(Response c) {
    //_cat.finest("Adding to queue= " + c);
    _items.add(c);
  }

  // get the first message in the queue in FIFO order
  public synchronized Response fetch() throws NoSuchElementException {
    //_cat.finest("Removing from queue= " + _items.getFirst());
    return (Response) _items.removeFirst();
  }

  public void clear(){
    _items.clear();
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    this._name = name;
  }

  public String toString() {
    String str = _name + ": " + size() + ":\n";
    Object cl[] = _items.toArray();
    for (int i = 0; i < cl.length; i++) {
      str += (Event) cl[i];
    }
    return str;
  }

}
