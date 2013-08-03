package com.poker.client;

import com.agneya.util.Configuration;

import com.golconda.message.Command;
import com.golconda.message.Response;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.logging.Logger;


/**
 * Shill client
 *
 */

public class Reconnect {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Reconnect.class.getName());
  Selector _selector;
  String _password;
  String _name;
  public int _tid = -99;
  public int _pos = -99;
  Command _out;
  SocketChannel _channel;
  String _session = null;
  ByteBuffer _h, _b, _o;
  public int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
  String _comstr;
  protected boolean _dead;
  protected long _last_read_time;
  protected long _last_write_time;
  double _play_chips = 0, _real_chips = 0;
  protected static String _ctype="NORMAL";
  private final static byte[] TERMINATOR = {
      38, 84, 61, 90, 13, 10, 0};
  private final static int TERMINATOR_SIZE = 7;

  public Reconnect()  {
  }

  public void connect() throws Exception {
    Configuration _conf = Configuration.getInstance();
    String _server = (String) _conf.get("Network.server.ip");
    _channel = SocketChannel.open(new InetSocketAddress(_server, 80));
    _channel.configureBlocking(true);
  }


  public String sendConnect(String sid) throws Exception {
    //System.out.println("Connecting ....");
    Command ge = new Command(sid, Command.C_CONNECT);
    _cat.finest("Connect req " + ge.toString());
    write(ge.toString());
    String s = readFull();
    _cat.finest("Connect resp " + s);
    Response r = new Response(s);
    return r.session();
  }

  public void disconnect(String sid) throws Exception {
    _cat.finest("DISCONNECTING " + sid);
    _channel.close();
  }

  public static void main(String[] args) throws Exception {
    Reconnect r = new Reconnect();
    r.connect();
    String sid=r.sendConnect(null);
    r.disconnect(sid);
    Thread.currentThread().sleep(1000);
    r.connect();
    r.sendConnect(sid);

  }

  public String readFull() {
    _cat.finest("Read full");
    String s = read();
    while (s == null) {
      s = read();
      try {
        Thread.currentThread().sleep(200);
      }
      catch (Exception e) {
        //ignore
      }
    }
    return s;
  }

  public synchronized String read() {
    try {
      if (_dead) {
        return null;
      }
      _last_read_time = System.currentTimeMillis();
      if (readHeader() && readBody()) {
        return _comstr;
      }
    }
    catch (IOException e) {
      _cat.warning(_name + " Marking client as dead ");
      _dead = true;
      return null;
    }
    catch (Exception e) {
      _cat.warning(_name + " Garbled command ");
      _dead = true;
      return null;
    }
    return null;
  }

  public boolean readHeader() throws IOException {
    if (_ctype.equals("FLASH")) {
      return true;
    }

    int r = 0;
    if (_rlen != -1) {
      return true;
    }
    if (_h == null) {
      _h = ByteBuffer.allocate(8);
      _h.clear();
    }
    r = _channel.read(_h);
    if (_h.hasRemaining()) {
      _cat.finest(_name + "  Partial header read " + r);
      if (r == -1) {
        _dead = true;
        _cat.warning(_name +
                   " Marking the client dead as the channel is closed  ");
      }
      return false;
    }
    _h.flip();
    _rseq = _h.getInt();
    _rlen = _h.getInt();
    _h = null;
    _cat.finest(" Len = " + _rlen);
    return true;
  }

  public boolean readBody() throws IOException {
    int r = 0;
    if (_ctype.equals("FLASH")) {
      _b = ByteBuffer.allocate(1);
      _b.clear();
      byte[] buf = new byte[1024];
      int i = 0;
      r = 0;
      while ( (r = _channel.read(_b)) != -1) {
        if ( (buf[i] = _b.array()[0]) == (byte) 0) {
          break;
        }
        i++;
        _b.clear();
      }
      if (r == -1) {
        _dead = true;
      }
      if (i > 0) {
        _comstr = new String(buf, 0, i - 2, "UTF-8");
      }
      resetRead();
    }
    else {
      if (_b == null) {
        _b = ByteBuffer.allocate(_rlen);
        _b.clear();
      }
      r = _channel.read(_b);
      if (_b.hasRemaining()) {
        if (r == -1) {
          _dead = true;
        }
        return false;
      }
      _b.flip(); // read complete
      _comstr = new String(_b.array());
      resetRead();
    }

    return true;
  }

  private void resetRead() {
    _h = null;
    _b = null;
    _rlen = -1;
    _rseq = -1;
  }

  public synchronized boolean write(String str) {
    _cat.finest("In write");
    _cat.finest("Writing = " + str);
    try {
      if (_ctype.equals("FLASH")) {
        _o = ByteBuffer.allocate(str.length() + TERMINATOR_SIZE);
        _o.put(str.getBytes());
        _o.put(TERMINATOR);
      }
      else {
        _o = ByteBuffer.allocate(8 + str.length());
        _o.putInt(_wseq++);
        _o.putInt(str.length());
        _o.put(str.getBytes());
      }
      _o.flip();
      int l = _channel.write(_o);
      while (_o.remaining() != 0) {
        _channel.write(_o);
      }
      _cat.finest("Written = " + str);
      _o = null;
      _dead = false;
    }
    catch (IOException e) {
      _dead = true;
      _cat.warning(_name +
                " Marking client as dead because of IOException during write");
      e.printStackTrace();
      return false;
    }
    return true;
  }

}
