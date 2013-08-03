package bap.texas.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import bap.texas.common.message.*;
import bap.texas.util.*;

import android.util.Log;


public class Proxy {
	/***
	   * Player's static identity declaration
	   */
	  static String _password;
	  static public String _name;
	  static boolean _authenticated;
	  static boolean _dead;
	  static long _last_read_time;
	  static long _last_write_time;
	  static double _play_worth, _real_worth;
	  static int _type;
	  static int _preferences = -1;
	  static public int _gender = -1;
	  static long HTBT_INTERVAL = 30000;


	  String _poll = "Holdem";
	public long _server_time;
	public int _active_players;
	public int _active_tables;

	  static Object _dummy = new Object();
	  static Selector _selector;
	  static SocketChannel _channel;
	  static String _session = null;
	  static ByteBuffer _h, _b, _o;
	  static int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
	  static String _comstr;
	  static Vector _waiters;

	  
	   static boolean keepLooking = true;
	   
	   public Proxy(String ip, int port) throws Exception {
		  
	    _channel = SocketChannel.open(new InetSocketAddress(ip, port));
	    _channel.configureBlocking(true);
	    _session = connect(_channel);
	    System.setProperty("java.net.preferIPv6Addresses", "false");
	    _selector = Selector.open();
	    _waiters = new Vector();
	    keepLooking=true;
		   
	  }

	
	  public String connect(SocketChannel _channel) throws Exception {
		    Command ge = new Command("null", Command.C_CONNECT);
		    Log.w("Proxy - connect ","Connect req " + ge.toString());
		    write(ge.toString());
		    String s = readFull();
		    Log.w("Proxy - connect ","Connect resp " + s);
		    Response r = new Response(s);
		    _authenticated = false;
		    return r.session();
	  }
	  
	  public Response loginReturn(String name, String pass) throws Exception {
	    _name = name;
	    _password = pass;
	    _channel.configureBlocking(true);
	    CommandLogin ge = new CommandLogin(_session, _name, _password, "",
	                                       "admin");
	    Log.w("Proxy - login ",_name + " Login Command " + ge);
	    write(ge.toString());
	    _last_write_time = System.currentTimeMillis();
	    ResponseFactory rf = new ResponseFactory(readFull());
	    Response gr = (Response) rf.getResponse();
	    if (gr.getResult() == 1 || gr.getResult() == 12) {
	      _play_worth = ((ResponseLogin) gr).getPlayWorth();
	      _real_worth = ((ResponseLogin) gr).getRealWorth();
	      _gender = ((ResponseLogin) gr).getGender();
	      _preferences = 0;//((ResponseLogin) gr).getPreferences();
	        //loadClientSettings();
	      _authenticated = true;
	      Log.w("Proxy - login ", _name + " Login Response " + gr);
	      return gr;
	    }
	    return null;
	  }

	  public Response registerReturn(String name, String passwd, String email, int gender, String bc, String dob) throws Exception {
		    _channel.configureBlocking(true);
		    CommandRegister ge = new CommandRegister(_session, name, passwd, email, gender, bc, dob);
		    Log.w("Proxy - register ", _name + " Register Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		    Response gr = new Response(readFull());
		    Log.w("Proxy - register ", _name + " Register Response " + gr);
		    return gr;
		  }
		  
	  public void startTLThread() {
	    // start a heartbeat timer thread
	    TLThread hb = new TLThread();
	    Timer t = new Timer();
	    t.schedule(hb, 0, HTBT_INTERVAL);
	  }
 
      /********************************************************************************
       * Repetitive tasks
       *****************************************************************************/

      public class TLThread extends TimerTask {

        public void run() {
          try {
            heartBeat();
          }
          catch (Exception ex) {
            //do nothing
            Log.w("Timertsk", "Exception" + ex);
          }
        }
      } // end HeartBeat class

	  public String toString() {
	    StringBuffer sb = new StringBuffer("Player=[Name=");
	    sb.append(_name);
	    sb.append(", Session=");
	    sb.append(_session);
	    sb.append(", Worth=");
	    sb.append(_play_worth);
	    sb.append(":");
	    sb.append(_real_worth);
	    return sb.toString();
	  }

	  public void pingBlocking() {
		    Command ge = new Command(_session, Command.C_PING );
		    //_cat.finest(" Ping Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		    ResponseFactory rf = new ResponseFactory(readFull());
		    ResponsePing rs = (ResponsePing) rf.getResponse();
		 Log.i(rs.toString(),"onlineplayers"+rs.getTime()+","+rs.getActivePlayers()+","+rs.getActiveTables());
		 _server_time = rs.getTime();
		 _active_players = rs.getActivePlayers();
		 _active_tables = rs.getActiveTables();
//		 firePingDetailsEvent(_server_time, _active_tables, _active_players);
		  }
	  
	  /**************************************************************************
	   * Various listeners and related methods
	   **************************************************************************/

	 

		  /********************************************************************************
		   * Requests to the server
		   *****************************************************************************/

		  public Response config() throws Exception {
		    _channel.configureBlocking(true);
		    Command ge = new Command(_session, Command.C_CONFIG);
		    Log.w(_name, " Config Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		    ResponseFactory rf = new ResponseFactory(readFull());
		    ResponseConfig gr = (ResponseConfig) rf.getResponse();
		    Log.w(_name, " Config Response " + gr);
		    return gr;
		  }

		  public Response register() throws Exception {
		    _channel.configureBlocking(true);
		    CommandRegister ge = new CommandRegister(_session, _name, _password,
		                                             _name + "@test.com", (byte) 0, "",
		                                             "");
		    Log.w(_name, " Register Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		    Response gr = new Response(readFull());
		    Log.w(_name,  " Register Response " + gr);
		    return gr;
		  }

		  public void addObserver(String tid) throws Exception {
		    CommandTableDetail ge = new CommandTableDetail(_session, tid);
		    Log.w(_name, " Add Observer Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		  }

		  public void removeObserver(String tid) throws Exception {
		    CommandString ge = new CommandString(_session, Command.C_TURN_DEAF, tid);
		    Log.w(_name,  " Remove Observer Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		  }

		  public void join(String tid, int pos, double value) throws Exception {
		    CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, value, tid);
		    ge.setPlayerPosition(pos);
		    Log.w(_name,  " Join Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		  }

		  public void buyChips(double playchips, double realChips) {
		    CommandBuyChips ge;
		    ge = new CommandBuyChips(_session, playchips, realChips);
		    //_requested_chips = value;
		    Log.w(_name, " Buy Chips  Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		  }

		  public Response getMoneyIntoGame(String tid, double value) {
		    CommandGetChipsIntoGame ge;
		    ge = new CommandGetChipsIntoGame(_session, tid, value);
		    Log.w(_name, " Get Chips  Command " + ge);
		    _last_write_time = System.currentTimeMillis();
		    write(ge.toString());
		    Response gr = new Response(readFull());
		    Log.w(_name, " Get Chips Response " + gr);
		    return gr;
		  }

		  public void updateClientSettingsAtServer(int settings) {
		    CommandInt ci = new CommandInt(_session, Command.C_PREFERENCES, settings);
		    Log.w(_name, PlayerPreferences.stringValue(settings));
		    write(ci.toString());
		  }

		  public void setSelectorForRead() throws Exception {
		    _channel.configureBlocking(false);
		    _channel.register(_selector, SelectionKey.OP_READ, this);
		  }

	    public ResponseTableList getTableList() throws Exception {
            CommandTableList ge = new CommandTableList(_session);
            _last_write_time = System.currentTimeMillis();
            write(ge.toString());
            ResponseFactory rf = new ResponseFactory(readFull());
            ResponseTableList gr = (ResponseTableList)rf.getResponse();
            Log.w("Proxy ", gr.toString());
            return gr;
        }
		
        public ResponseTableList getTableList(int type) throws Exception {
            CommandTableList ge = new CommandTableList(_session, type);
            _last_write_time = System.currentTimeMillis();
            write(ge.toString());
            ResponseFactory rf = new ResponseFactory(readFull());
            ResponseTableList gr = (ResponseTableList)rf.getResponse();
            Log.w("Proxy ", gr.toString());
            return gr;
        }
	        
		  public void heartBeat() throws Exception {
		    if (_session != null) {
		      Command ge = new Command(_session, Command.C_HTBT);
//			      _cat.finest("HTBT" + ge.toString());
		      write(ge.toString());
		    }
		  }

		  /********************************************************************************
		   * Channel read write methods
		   *****************************************************************************/

		  public boolean readHeader() throws IOException {
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
		      //log.fatal(_name + " Partial header read " + r);
		      if (r == -1) {
		        _dead = true;
		        Log.w(_name,
		                   " Marking the client dead as the channel is closed  ");
		      }
		      return false;
		    }
		    _h.flip();
		    _rseq = _h.getInt();
		    _rlen = _h.getInt();
		    _h = null;
		    return true;
		  }

		  public boolean readBody() throws IOException {
		    int r = 0;
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

		    return true;
		  }

		  public String readFull() {
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
		      Log.w(_name, " Marking client as dead ");
		      _dead = true;
		      return null;
		    }
		    catch (Exception e) {
		      Log.w(_name,  " Garbled command ");
		      _dead = true;
		      return null;
		    }
		    return null;
		  }

		  private void resetRead() {
		    _h = null;
		    _b = null;
		    _rlen = -1;
		    _rseq = -1;
		  }

		  public synchronized boolean write(String str) {
		    try {
		     
		        _o = ByteBuffer.allocate(8 + str.length());
		        _o.putInt(_wseq++);
		        _o.putInt(str.length());
		        _o.put(str.getBytes());

		      _o.flip();
		      int l = _channel.write(_o);
		      while (_o.remaining() != 0) {
		        _channel.write(_o);
		      }
		      _o = null;
		      _dead = false;
		    }
		    catch (IOException e) {
		      _dead = true;

		      Log.w(_name,
		                " Marking client as dead because of IOException during write");

		      return false;
		    }
		    return true;
		  }

		  
		 
}
