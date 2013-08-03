//extends NWChannel class and implements the clonable and runnable interface
// Network-socket based communication class
// Used primarily to communicate with the admin client
//
package com.golconda.net;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.logging.Logger;


public class NWServer implements Runnable {
  static Logger _cat = Logger.getLogger(NWServer.class.getName());

    Thread runner = null;
    ServerSocket server = null;
    Socket data = null;
    NWProcessor _nwp;

    public NWServer(String procName){
      try {
        _nwp = (NWProcessor) Class.forName(procName).newInstance();
      }catch (Exception e){
        e.printStackTrace();
      }
    }

    public NWServer(NWProcessor nwp){
      _nwp = nwp;
    }

    public void startServer(int port) throws IOException {
  	if (runner==null) {
	    server = new ServerSocket(port);
	    runner = new Thread(this);
	    runner.start();
            _cat.info("Admin server started");
	}
    }

   public void stopServer() throws IOException {
  	if (server != null) {
	    runner = null;
	    server.close();
	}
    }

  public void stop() throws IOException {
	stopServer();
    }

    public void run() {
	Thread thisThread = Thread.currentThread();
	while (runner == thisThread) {
	    try {
		Socket datasocket = server.accept();
		NWServerThread newSocket = new NWServerThread(datasocket, _nwp);
		newSocket.start();
                Thread.currentThread().sleep(200);;
	    } catch (Exception e){
		_cat.severe(e.getMessage());
	    }
	}
    }

}

