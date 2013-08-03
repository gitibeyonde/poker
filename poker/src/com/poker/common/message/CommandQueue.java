package com.poker.common.message;

import com.golconda.message.Command;
import com.golconda.message.Event;

import com.poker.nio.Handler;

import java.util.LinkedList;
import java.util.NoSuchElementException;


/**
 * Client queue is singleton. Clients which have received a GameEvent are
 * lined up in this queue for processing.
 * ClientQueue is created when the Poker server is started
 *
 **/

public class CommandQueue {
  String _name; // queue name
  private CommandProcessor _wq;
  private LinkedList _items;

  public CommandQueue(String wq) throws Exception {
    _name = "Command Queue";
    _items = new LinkedList();
    _wq = (CommandProcessor) Class.forName(wq).newInstance();
    _wq.startProcessor(this);
  }

  public void stopProcessor() throws Exception {
    _wq.stopProcessor();
  }

  public synchronized int size() {
    return _items.size();
  }

  // add message to queue
  public synchronized void put(Command c) {
    //System.out.println("Adding to queue= " + c);
    ((Handler)c.handler())._com_cnt++;
    //         _cat.info(((Handler)c.handler())._id + "COUNT=" + ((Handler)c.handler())._com_cnt);
    _items.add(c);
    // wake up the worker
    _wq.wakeUp();
  }

  public void clear(){
    _items.clear();
  }

  // get the first message in the queue in FIFO order
  public synchronized Command fetch() throws NoSuchElementException {
    //System.out.println("Removing from queue= " + _items.getFirst());
     Command c = (Command) _items.removeFirst();
     if (c!=null){
        ((Handler)c.handler())._com_cnt--;
     }
    return c;
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
