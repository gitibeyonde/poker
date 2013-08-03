package com.onlinepoker.lobby;

import java.awt.Checkbox;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.agneya.util.Base64;
import com.onlinepoker.server.ServerProxy;
import com.onlinepoker.util.MyPlayerNoteDialog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaLookAndFeel;

//import dj.nativeswing.swtimpl.NativeInterface;
public class BrowserVersion {
	private static File f;
    private static FileChannel channel;
    private static FileLock lock;
   
    private static ServerSocket socket;
    private static final int PORT = 9919;
    static Logger _cat = Logger.getLogger(Lobby.class.getName());
    public static void main(final String[] args) throws Exception {
    	start(args);
    }
  public static void start(String[] args) throws Exception {
   	  System.setProperty("proxyHost", args[0]);
	  	final SplashWindow splashWindow = new SplashWindow(null);
		SyntheticaLookAndFeel.setWindowsDecorated(false);
	    try {
			UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 *Single instance checking code 
		 **/
		String ss = checkIfRunning();
		_cat.fine(ss);
		if("Running".equals(ss)){
		     JOptionPane.showMessageDialog(null, "You have already open this application. This software supports only single client \n you can't open new client.");
		     System.exit(-1);
	    }
		
//		check();
		
//		if(!"exe".equals(args[2]) || args[2] == null){
//			checkExecutable();
//		}
		
		/**
		 * Server connection 
		 **/
//		NativeInterface.open();   
//	    SwingUtilities.invokeLater(new Runnable() {   
//	    public void run() { 
        	try {
				    ServerProxy _serverProxy  = ServerProxy.getInstance(args[0], Integer.parseInt(args[1]), null);
				    int i=0;
				    while (_serverProxy == null){
				    	Thread.sleep(2000);
				        i++;
				        if(i > 5)
				        {
				        	splashWindow.statusLabel.setText("Not able to connect! ");
				        	Thread.sleep(2000);
				        	System.exit(0);
				        }
				    }
				    splashWindow.statusLabel.setText("Server found");
				    _serverProxy.pingBlocking();
				    //_serverProxy.tableListBlocking();
				    long time=0;
				    i=0;
				    while (time == 0){
				        Thread.sleep(2000);
				        time = _serverProxy._server_time;
				        i++;
				        if(i > 5)
				        {
				        	splashWindow.statusLabel.setText("Server not responding");
				        	Thread.sleep(2000);
				        	System.exit(0);
				        }
				    }
				    splashWindow.statusLabel.setText("Connected.");
				    Thread.sleep(2000);
				    splashWindow._connected = true;
				    if (args.length > 0) {
				      LobbyUserImp lobbyUserImp = new LobbyUserImp(args[0]);
				      lobbyUserImp.fromBrowser = true;
				      lobbyUserImp.uid=Base64.decodeString(args[2]);
				      lobbyUserImp.pwd=Base64.decodeString(args[3]);
				      lobbyUserImp.poolName = args[4];
				      lobbyUserImp.amt = Double.parseDouble(args[5]);
				      lobbyUserImp.closeURL = args[6];
				      
				      
				      //System.out.println("in BrowserVersion "+lobbyUserImp.uid+","+lobbyUserImp.pwd);
				      new LobbyFrame(lobbyUserImp);
				    }
				    else {
				      System.err.println("Usage: Lobby URL");
				    }
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	      }   
//	    });   
	    //NativeInterface.runEventPump();     
    
  }
  
  private static String checkIfRunning() {
      try {
        socket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
      }
      catch (BindException e) {
        _cat.fine("Already running.");
        return "Running";      
      }
      catch (IOException e) {
        return "Running"; 
      }
    return null;
    }
  
  public static void check(){
		/**
		 * following code is for avoid opening two jnlp clients from one PC
		 */
		try {
          f = new File("RingOnRequest.lock");
          // Check if the lock exist
          if (f.exists()) {
              // if exist try to delete it
              f.delete();
          }
          // Try to get the lock
          channel = new RandomAccessFile(f, "rw").getChannel();
          lock = channel.tryLock();
          if(lock == null)
          {
              // File is lock by other application
              channel.close();
              //throw new RuntimeException("Only 1 instance of MyApp can run.");
              JOptionPane.showMessageDialog(null,"You have already open this application. This software supports only single client \n you can't open new client");
              System.exit(-1);
          }
          // Add shutdown hook to release lock when application shutdown
          ShutdownHook shutdownHook = new ShutdownHook();
          Runtime.getRuntime().addShutdownHook(shutdownHook);

          //Your application tasks here..
          //System.out.println("Running");
          
          
      }
      catch(IOException e)
      {
          throw new RuntimeException("Could not start process.", e);
      }
      
      
  }
  
  /**
   * following code is for avoiding open another jnlp client file
   */
  public static void checkExecutable(){
	    GetProcessList pl = new GetProcessList();
	    Vector bapProcess =pl.showProcessData();
	    System.out.println("bapProcess.length "+bapProcess.size());
	    String pid = System.getProperty("pid");
	    System.out.println("pid "+pid);
	    if(bapProcess.size() == 1){
	        if("BAP.exe".equalsIgnoreCase(((String[])bapProcess.get(0))[0])){
	        	JOptionPane.showMessageDialog(null,"You have already open this application. This software supports only single client \n you can't open new client.");
	        	System.exit(-1);
	        }
	    }
  }
  public static void unlockFile() {
      // release and delete file lock
      try {
          if(lock != null) {
        	  System.out.println("unlockFile");
              lock.release();
              channel.close();
              f.delete();
          }
      } catch(IOException e) {
          e.printStackTrace();
      }
  }

  static class ShutdownHook extends Thread {

      public void run() {
          unlockFile();
          System.out.println("in shutdownHook in Lobby");
      }
  }
}

