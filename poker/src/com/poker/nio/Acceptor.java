package com.poker.nio;

import com.agneya.util.Configuration;

import java.io.IOException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Acceptor {
  static Logger _cat = Logger.getLogger(Acceptor.class.getName());
  final private Selector[] _selectors;
  private final Reactor[] _threads;
  private int _thread_count;
  private int _port;
  private ServerSocketChannel[] _ssc;

  public Acceptor() throws Exception {
    Configuration conf = Configuration.instance();
    _thread_count = Integer.parseInt( (String) conf.get(
        "Network.server.threadcount"));
    _selectors = new Selector[_thread_count];
    _threads = new Reactor[_thread_count];
    _port = Integer.parseInt( (String) conf.get("Network.port"));
  }

  public void startServer(String proc) throws Exception {
    InetAddress ia = null;
    Vector allServerSocketChannels = new Vector();
    try {
      // get all the network interfaces
      Enumeration en1 = NetworkInterface.getNetworkInterfaces();
      while (en1.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) en1.nextElement();
        _cat.info("Network Interface " + ni);
        Enumeration en2 = ni.getInetAddresses();
        while (en2.hasMoreElements()) {
          try {
            ia = (InetAddress) en2.nextElement();
              _cat.info("Binding to port " + _port + " on InetAddress " +
                        ia.getHostAddress() + " canonical address " +
                        ia.getCanonicalHostName());
              InetSocketAddress isa = new InetSocketAddress(ia,  _port);

              ServerSocketChannel ssc = ServerSocketChannel.open();
              ssc.configureBlocking(false);
              ssc.socket().bind(isa);
              allServerSocketChannels.add(ssc);
              _cat.info("Opening a non-blocking ServerSocketChannel on port " +
                        _port + " on InetAddress " + ia.getHostAddress());

          }
          catch (java.net.BindException be) {
            _cat.log(Level.WARNING, be.getMessage() + ia.getHostAddress());
            continue;
          }
          catch (java.net.SocketException be) {
            _cat.log(Level.WARNING, be.getMessage() + ia.getHostAddress());
            continue;
          }
        }
      }
    }
    catch (IOException e) {
      _cat.log(Level.WARNING, 
          "Error while opening a non-blocking ServerSocketChannel on port " +
          _port + " on InetAddress " + ia, e);
      System.exit(-1);
    }
    _ssc = (ServerSocketChannel[]) allServerSocketChannels.toArray(new
        ServerSocketChannel[allServerSocketChannels.size()]);
    for (int i = 0; i < _thread_count; i++) {
      _selectors[i] = Selector.open();
      for (int j = 0; j < _ssc.length; j++) {
        ServerSocketChannel ssc = (ServerSocketChannel) _ssc[j];
        try {
          ssc.register(_selectors[i], SelectionKey.OP_ACCEPT);
        }
        catch (IOException e) {
          _cat.log(Level.WARNING, "IOException trying to register new SSC = " + ssc, e);
        }
      }
    }
    // start threads for each of selector
    for (int i = 0; i < _thread_count; i++) {
      _threads[i] = new Reactor(i, _selectors[i], proc);
      _threads[i].start();
      _threads[i].setName("Reactor " + i);
      _threads[i].setPriority(Thread.MAX_PRIORITY);
    }

  } // end startServer

  public void stopServer() throws Exception {
    // close all the open socket connections
    for (int i = 0; i < _ssc.length; i++) {
      _ssc[i].close();
    }
    for (int i = 0; i < _thread_count; i++) {
      _selectors[i].close();
      _threads[i].stopReactor();
      //_threads[i].stop();TODO remove stop instead falsify the condition to stop the thread
    }
  }

} // end Aceptor class
