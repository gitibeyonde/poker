package com.golconda.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class NWClient {
  private static Socket _data;
  private static DataInputStream _in;
  private static DataOutputStream _out;
  private int _seq;
  //private static  Log log = LogFactory.getLog("#### NWClient ####");
  public NWClient() {
  }

  public void connect(String addr, int port)
   throws IOException
   {
     _data = new Socket(addr, port);
     _in = new DataInputStream(new BufferedInputStream(_data.getInputStream()));
     _out = new DataOutputStream(new BufferedOutputStream(_data.getOutputStream()));
   }

   public void send(String cmd) throws IOException {
     _out.writeInt(_seq++);
     _out.writeInt(cmd.length());
     _out.write(cmd.getBytes());
     _out.flush();
   }

   public String get() throws IOException {
     int seq = _in.readInt();
     int len = _in.readInt();
     byte[] buffer = new byte[len];
     _in.read(buffer, 0, len);
     return new String(buffer);
   }

   public static boolean isConnected() throws IOException{
     //log.debug("isConnected*************"+_data.isConnected());
      return  _data.isConnected() ;
   }
   public void close() throws IOException {
     _data.close();
   }

}
