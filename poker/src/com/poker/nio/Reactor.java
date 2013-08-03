package com.poker.nio;

import com.poker.common.message.CommandQueue;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;


public class Reactor
    extends Thread {
  static Logger _cat = Logger.getLogger(Reactor.class.getName());
  int _id;
  Selector _selector;
  boolean _keepServicingRequests = true;
  private CommandQueue _com;

  /**
    * queue: CommandQueue , the clients which have received an event which
    * requires processing are set in this queue
    */

  public Reactor(int i, Selector sel, String proc)
  throws Exception
  {
    _id = i;
    _selector = sel;
    _cat.finest("Starting a Reactor " + i);
    // start the work queues which will serve the incoming requests
    _com = new CommandQueue(proc);
  }

  public void stopReactor() throws Exception {
    _com.stopProcessor();
    _keepServicingRequests = false;
  }

  public void run() {
    while (_keepServicingRequests) {
      try {
        Thread.currentThread().sleep(10);
        _selector.selectNow();
        // Now we deal with our incoming data / completed writes...
        Set keys = _selector.selectedKeys();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
          SelectionKey key = (SelectionKey) i.next();
          i.remove();
          if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel c = server.accept();
            if (c!=null) {
              // Set the new channel non blocking
              c.configureBlocking(false);
              Handler h = new Handler(c, _selector,  _com);
              // attach the client object with this selector
              c.register(_selector, SelectionKey.OP_READ, h);
              h.inetAddress(c.socket().getInetAddress());
              _cat.info("Received a connection from " + c.socket().getInetAddress());
            }
          }
          else if (key.isReadable()) {
            Handler h = (Handler) key.attachment();
            h.read();
          } // end else if read
          else if (key.isWritable()) {
            Handler h = (Handler) key.attachment();
            h.write();
          } // end else if write
        }
        Thread.currentThread().sleep(10);
      }
      catch (java.lang.ThreadDeath e) {
        _keepServicingRequests = false;
      }
      catch (Throwable e) {
        // MUST catch Exception, otherwise the main-thread will
        // stop processing incoming requests!!!
        _cat.warning("Unexpected Throwable in outer loop -  " + e);
      }
    }
  }
} // end NIOHandlerPool class
