package bap.texas;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.PokerGameType;

public class ActivitySplash extends Activity {

	
	private ImageView _image;
	int _timer[] = { R.drawable.frame1,R.drawable.frame2,R.drawable.frame3,R.drawable.frame4,
			R.drawable.frame5,R.drawable.frame6,R.drawable.frame7,R.drawable.frame8,
	};
	MyAnimationRoutine mar;
	TextView tv;
	private static int num;
	final int welcomeScreenDisplay = 1000;
	private ConnectivityManager cMgr;
	private NetworkInfo netInfo;
	private Proxy _proxy;
	private SocketChannel _channel;
	private boolean loadactivity = false;
	public static String connectstatus=null;
	public static String connectedstatus=null;
	public static String conncetionfailstatus=null;
	public static String servernotfoundstatus=null;
	private LinearLayout splash;
	int k=0;
	boolean keepalive=true;
	private Socket socketConn;
	public static ProgressDialog progressDialog;
	public static HashMap<String, Typeface> paintobjects;
	Typeface dotsface,myriadface,verdanaface;

	private  AlertDialog.Builder alt_bld;
	AlertDialog alert;
	AlertDialog.Builder connDialog;
	public static int totalplayers;
	public  Handler onlinecount = new Handler(){
		

		public void handleMessage(Message msg){
			if(msg.what == 1)
			{
				totalplayers = getonlineplayers();
			}
		}
	};
	
	public Handler splashHandler = new Handler() {
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			Bundle b= msg.getData();
			//	int k = b.getInt("action");
			switch(msg.what)
			{
			case 0: 
				drawText(getString(R.string.connecting));
				break;
			case 1:
				drawText(getString(R.string.connected));
				break;
			case 2:
				drawText(getString(R.string.notabletoconnect));
				break;
			case 3:
				connDialog.show();
				break;
			case 4:
				 progressDialog = ProgressDialog.show(getApplicationContext(),    
	                       "Please wait...", "Validating...", true);
				 break;
			case 5:
				progressDialog.dismiss();
				break;
			case 6:
				creatFonts();
				break;
			case 7:
				totalplayers = getonlineplayers();
				break;
			}
			super.handleMessage(msg);
		}
	};
	private ResponseTableList rt1;
	private int count;
	private int totPlrs;
	private GameEvent ge;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
   	 	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		tv = (TextView) findViewById(R.id.TextView01);
		splashHandler.sendEmptyMessage(6);	
		tv.setTypeface(myriadface);
		connDialog = new AlertDialog.Builder(this);
		connDialog.setTitle(getString(R.string.errortitle));
		connDialog.setMessage(getString(R.string.errormessage));
		connDialog.setPositiveButton(getString(R.string.okvalue),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				keepalive=false;
			}
		});
		
		//splashHandler.sendEmptyMessage(7);
        
		Thread welcomeThread = new Thread() {
			int wait = 0;

			@Override
			public void run() {
				try {
					super.run();
					/**
					 * use while to get the splash time. Use sleep() to increase
					 * the wait variable for every 100L.
					 */
					mar = new MyAnimationRoutine();
					Timer t = new Timer(false);
					t.schedule(mar, 100);
					int i=0;

					Message msg = new Message();
					Bundle b=new Bundle();
					while (keepalive) 
					{
						Log.i("loop","repeate");
						Log.i("Text","splash");
						i++;
						sleep(150);
						String result = validate();	
						Log.i("loop2"+wait,result);						
						if(result.equals("connecting"))
						{	
							num = 0; 
						}
						if(result.equals("connected"))
						{ 
							num = 1; 
						}
						if(result.equals("connectionfail"))
						{
							num = 2;
						}
						if(result.equals("startactivity"))
						{
							keepalive=false;
							//splashHandler.sendEmptyMessage(7);
							ActivitySplash.this.startActivity(new Intent(ActivitySplash.this,
									ActivityLogin.class));
						}	
						if(wait == welcomeScreenDisplay)
						{
							num = 3;

						}
						splashHandler.sendEmptyMessage(num);
						wait += 100;
					}
				} catch (Exception e) {
					System.out.println("EXc=" + e);
				} finally {
					/**
					 * Called after splash times up. Do some action after splash
					 * times up. Here we moved to another main activity class
					 */
					finish();
				}
			}
		};
		welcomeThread.start();
	}
	public int  getonlineplayers()
	{
		try{
			_proxy = new Proxy(Command.IP,Command.PORT);
	   		// _proxy = new Proxy("67.211.101.97",8985);
			// _proxy = new Proxy("66.220.9.100",8985);
	   		 rt1 = _proxy.getTableList(PokerGameType.Play_Holdem);// Table type is 1
	   		 count = rt1.getGameCount();
	   		 for (int i=0;i < count;i++)
	   		 {
	   			 ge = rt1.getGameEvent(i);
		    	 totPlrs += ge.getPlayerCount();
	   		 }
	   		 rt1 = _proxy.getTableList(PokerGameType.Real_Holdem);// Table type is 256
	   		 count = rt1.getGameCount();
	   		 for (int i=0;i < count;i++)
	   		 {
	   			 ge = rt1.getGameEvent(i);
		    	 totPlrs += ge.getPlayerCount();
	   		 }
  		 
    	 }
		 catch (Exception e) { 
	        Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
	     }
		return totPlrs;	
	}
	public void creatFonts()
	{
		paintobjects = new HashMap<String, Typeface>(); 
		 dotsface=Typeface.createFromAsset(getAssets(), "fonts/Dots.ttf");
        paintobjects.put("dotstypeface", dotsface);
		
         myriadface=Typeface.createFromAsset(getAssets(), "fonts/MYRIAD.TTF");
        paintobjects.put("myriadface", myriadface);
        
         verdanaface=Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
        paintobjects.put("verdanaface", verdanaface);
	}
	public void drawText(String status)
	{
		tv.setText(status);
	}
	private String validate(){
		String result = "";
		try{
			socketConn = new Socket();
			//InetSocketAddress isa = new InetSocketAddress("67.211.101.97",8985);
			InetSocketAddress isa = new InetSocketAddress(Command.IP,Command.PORT);
			socketConn.connect(isa, 2000);
			socketConn.setKeepAlive(false);
			socketConn.setTcpNoDelay(true);
			socketConn.setSoLinger(true, 1000);

			if ( socketConn.isConnected() )
			{
				if(_proxy==null)
				{
					//_proxy = new Proxy("67.211.101.97",8985);
					//_proxy = new Proxy("66.220.9.100",8985);
					_proxy = new Proxy(Command.IP,Command.PORT);
					result="connecting";
				}
				else
				if(_proxy!=null && _channel == null)
				{
					//_channel = SocketChannel.open(new InetSocketAddress("67.211.101.97",8985));
					_channel = SocketChannel.open(new InetSocketAddress(Command.IP,Command.PORT));
					result="connected";
				}
				else
				if(_proxy!=null && _channel !=null)
				{
					result="startactivity";
				}
			}
			else
			{
				result = "connectionfail";
			}
		} catch (Exception e) { 
			result = "connectionfail";  
			Log.e("BACKGROUND_PROC", e.getMessage());	
		}
		return result;

	}
	class MyAnimationRoutine extends TimerTask
	{
		MyAnimationRoutine()
		{
		}
		public void run()
		{
			ImageView img = (ImageView)findViewById(R.id.simple_anim);
			// Get the background, which has been compiled to an AnimationDrawable object.
			AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

			// Start the animation (looped playback by default).
			frameAnimation.start();
		}
	}
}