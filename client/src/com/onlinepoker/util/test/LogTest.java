package com.onlinepoker.util.test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.agneya.util.Base64;

public class LogTest {
	static Logger _log = Logger.getLogger(LogTest.class.getName());
	
	public static void main(String []a){
/*      * SEVERE (highest value)
	    * WARNING
	    * INFO
	    * CONFIG
	    * FINE
	    * FINER
	    * FINEST (lowest value) */
//		_log.finest("finest");
//		_log.fine("fine");
//		_log.info("info");
//		_log.warning("warning");
//		_log.severe("severe");
//		 int delay = 5000;// in ms 

//         Timer timer = new Timer();
//
//         timer.schedule( new TimerTask(){
//            public void run() { 
//                System.out.println("Wait, what..:");
//             }
//          }, 5000);
//
//          System.out.println("Would it run?");
		//System.out.println(Base64.decodeString("WW91IGFyZSBtb3ZlZCB0byBlbXB0eSB0YWJsZSBiZWNhdXNlIHlvdSByYW4gb3V0IG9mIG1vbmV5 LiBZb3UgY2FuIGpvaW4gYWdhaW4gIQ=="));
		double ab = 10.9;
		System.out.println(Double.toString(ab));

	}
}
