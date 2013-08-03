// Network-socket based communication class
// Used primarily to communicate with the admin client
//
package com.golconda.net;

import com.golconda.net.event.AdminEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import java.util.logging.Logger;


/**
 * socket-based listener
 */
public class NWServerThread
    extends Thread {
  static Logger _cat = Logger.getLogger(NWServerThread.class.getName());

  private Socket data;
  private DataInputStream in;
  private DataOutputStream out;
  private int sequenceNumber = 0;
    NWProcessor _nwp;

  private boolean _keepListening;

  public NWServerThread(Socket data, NWProcessor nwp) {
    super("NWServerThread");
    this.data = data;
    _keepListening = true;
    _nwp = nwp;
  }

  public void run() {
    try {
      in = new DataInputStream(new BufferedInputStream(data.getInputStream()));
      out = new DataOutputStream(new BufferedOutputStream(data.getOutputStream()));
      protocol(in, out);
      data.close();
    }
    catch (Exception e) {
      _cat.severe(e.getMessage());
    }
  }

  public void put(String str)
      throws IOException {
    out.writeInt(sequenceNumber++);
    out.writeInt(str.length());
    out.write(str.getBytes());
    out.flush();
  }

  public String get()
      throws IOException {
    int seq = in.readInt();
    int len = in.readInt();
    assert len < 256 : "Command length is more than 256 chars";
    byte[] buffer = new byte[len];
    in.read(buffer, 0, len);
    return new String(buffer);
  }

  public void protocol(DataInputStream in, DataOutputStream out) {
    try {
      while (_keepListening) {
        AdminEvent ae = new AdminEvent(get());
        _cat.finest("AdminCommand=" + ae);
        put( ( (AdminEvent) _nwp.process(ae)).toString());
      }
    }
    catch (Throwable e) {
      _cat.info("Closing protocol, client disconnected " + e.getMessage());
    }

  }
}
