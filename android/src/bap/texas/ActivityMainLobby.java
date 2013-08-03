package bap.texas;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.PokerGameType;
import bap.texas.util.SoundManager;


public class ActivityMainLobby extends Activity {

	private ProgressDialog m_ProgressDialog = null; 
    private Proxy _proxy;
    private String _user, _pass;
    private float _uw, _urw;
    private Button _startplaying;
    private Button _lobbyButton;
    private Button _settings;
    private Button _myaccount;
    private Button _referfriend;
    private Button _exitButton;
    private TextView _silver, _gold, _avgPot, _totPlrs, _name,_onlineplayers,_realsymbel;
    private TextView _playsymbel,_mainlobbyTV,_realmoneyTV,_playmoneyTV,_playersTV;
    private ResponseTableList rt_play,rt_real,rt1; 
    private double avgPot;
    private int totPlrs;
    private GameEvent ge ;
    private int count;
    private int droidTables;
    AlertDialog.Builder alt_bld;
    public static String _username;
    
    //private TextView _statusText;
    public Handler _mainlobbyhandler = new Handler(){
    	public void handleMessage(Message msg){
    		if(msg.what==1)
    		{
    			NumberFormat format = new DecimalFormat("00000");
    			_totPlrs.setText(""+format.format(totPlrs));
    		}
    	}
    };
	private BackgroundThread mainlobby_bgThread;
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 requestWindowFeature(Window.FEATURE_NO_TITLE);
    	  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
 		         WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	 
    	 setContentView(R.layout.mainlobby);
    	  
    	 try {
 			//_proxy = new Proxy("67.211.101.97",8985);
    		// _proxy = new Proxy("66.220.9.100",8985);
    		 _proxy = new Proxy(Command.IP,Command.PORT);
 		} catch (Exception e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
    	 mainlobby_bgThread = new BackgroundThread();
    	 mainlobby_bgThread.setRunning(true);
    	 mainlobby_bgThread.start();
    	 _startplaying = (Button)findViewById(R.id.startplayingbutton);
//    	 _lobbyButton = (Button)findViewById(R.id.actionLobbyB);
    	 _settings = (Button)findViewById(R.id.settingbutton);
    	 _myaccount=(Button)findViewById(R.id.myaccountbutton);
    	 _referfriend= (Button)findViewById(R.id.referfriendbutton);
		 _exitButton = (Button)findViewById(R.id.exitButton);
    	
		 _mainlobbyTV = (TextView)findViewById(R.id.mainlobbyTV);
		 _realmoneyTV = (TextView)findViewById(R.id.realmoneytextview);
		 _playmoneyTV = (TextView)findViewById(R.id.playmoneytextview);
		 _silver = (TextView)findViewById(R.id.playmoney);
    	 _gold = (TextView)findViewById(R.id.realmoney);
    	 _realsymbel = (TextView)findViewById(R.id.realmoneysymbel);
    	 _playsymbel = (TextView)findViewById(R.id.playmoneysymbel);
    	 _name = (TextView)findViewById(R.id.actionName);
    	 _playersTV = (TextView)findViewById(R.id.playersview);
    	 _totPlrs=(TextView)findViewById(R.id.playersonline);
    	 
    	 Typeface face=Typeface.createFromAsset(getAssets(), "fonts/Dots.ttf");
    	 _totPlrs.setTypeface(face);
         _gold.setTypeface(face);
         _silver.setTypeface(face);
         _realsymbel.setTypeface(face);
         _playsymbel.setTypeface(face);
         _name.setTypeface(face);
         _name.setTextSize(16);
         
         Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/MYRIAD.TTF");
         _mainlobbyTV.setTypeface(face2);
         _playersTV.setTypeface(face2);
         _realmoneyTV.setTypeface(face2);
         _playmoneyTV.setTypeface(face2);
         _startplaying.setTypeface(face2);
         _settings.setTypeface(face2);
         _myaccount.setTypeface(face2);
         _referfriend.setTypeface(face2);
         _exitButton.setTypeface(face2);
         
         _mainlobbyTV.setText(ActivityLogin.properties.getProperty("mainlobby"));
         
         SharedPreferences shared = getSharedPreferences(ActivitySettings.PREFS_NAME, Activity.MODE_PRIVATE);
         ActivitySettings.colorsdeck = shared.getBoolean("colordesk", false);
         ActivitySettings.autoblind = shared.getBoolean("autoblind", false);
         Log.i("Check box Status value",ActivitySettings.autoblind+"");
         if(ActivitySettings.colorsdeck)
        	 Log.i("Success fully Excecuted",ActivitySettings.colorsdeck+"");
         else
        	 Log.i("Excecution Failed",ActivitySettings.colorsdeck+"");
         
         Log.i("Conditional operator",ActivitySettings.colorsdeck ? "R.drawable.card5c" : "R.drawable.card_5c");
         
        /* try {
			_proxy = new Proxy("192.168.0.2", 8985);
			 _proxy.pingBlocking();
	    	 NumberFormat format = new DecimalFormat("00000");
	    	 _totPlrs.setText(""+format.format(_proxy._active_players));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	*/
         
         
    	 
		 
		 
         Bundle b = this.getIntent().getExtras(); 
    	 if (b != null){
	         String status = (String)b.getString("status");
	         _user = (String)b.getString("user");
	         _pass = (String)b.getString("pass");
	         _uw = (float)b.getFloat("uw");
	         _urw = (float)b.getFloat("urw");
	         _name.setText(_user);
	         _silver.setText(_uw+" $");
	         _gold.setText(_urw+" $");
	         _username = _user;
	       
    	 }
    	 
    	 
 		alt_bld = new AlertDialog.Builder(this);
		alt_bld.setCancelable(false);
		alt_bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
		// Action for 'Yes' Button
			dialog.cancel();
		}
		});

  	  _startplaying.setOnClickListener(new Button.OnClickListener() {
  		   public void onClick(View v) {
  			// mainlobby_bgThread.stop();
          	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
          	 Intent i = new Intent(ActivityMainLobby.this, ActivityLobby.class); 
          	//int color = Color.rgb(RedProgress, GreenProgress, BlueProgress);
          	 	_startplaying.setTextColor(Color.rgb(20,111,192));
				 Bundle b = new Bundle(); 
				 b.putString("user", _user); 
				 b.putString("pass", _pass); 
				 b.putFloat("uw", _uw);
				 b.putFloat("urw", _urw);
				 b.putInt("onlineplayers", totPlrs);
				 i.putExtras(b); 
               ActivityMainLobby.this.startActivity(i); 
  		   		}
  	  		});
    	 _settings.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 Intent i = new Intent(ActivityMainLobby.this,ActivitySettings.class); 
            	 _settings.setTextColor(Color.rgb(20,111,192));
            	 Bundle b = new Bundle(); 
 				 b.putString("user", _user); 
 				 b.putString("pass", _pass); 
 				 b.putFloat("uw", _uw);
 				 b.putFloat("urw", _urw);
 				 b.putInt("onlineplayers", totPlrs);
 				 i.putExtras(b); 
				 ActivityMainLobby.this.startActivity(i); 
             }
         });

    	 _myaccount.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 Intent i = new Intent(ActivityMainLobby.this,ActivityAccount.class);
            	 _myaccount.setTextColor(Color.rgb(20,111,192));
            	 Bundle b = new Bundle(); 
 				 b.putString("user", _user); 
 				 b.putString("pass", _pass); 
 				 b.putFloat("uw", _uw);
 				 b.putFloat("urw", _urw);
 				 b.putInt("onlineplayers", totPlrs);
 				 i.putExtras(b); 
				 ActivityMainLobby.this.startActivity(i);
             }
         });
    	 _referfriend.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 Intent i = new Intent(ActivityMainLobby.this, 
                         ActivityRefer.class); 
            	 _referfriend.setTextColor(Color.rgb(20,111,192));
            	 Bundle b = new Bundle(); 
 				 b.putString("user", _user); 
 				 b.putString("pass", _pass); 
 				 b.putFloat("uw", _uw);
 				 b.putFloat("urw", _urw);
 				 b.putInt("onlineplayers", totPlrs);
 				 i.putExtras(b); 
				 ActivityMainLobby.this.startActivity(i);
                 }
         });
    	 _exitButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.fold);
            	 try{
            		 finish();
            		 moveTaskToBack(true); 

            	 }catch(Exception e)
            	 {
            		 Log.e("ACTIVITY_START_PROC", e.getStackTrace().toString());
            	 }
             }
         });
    	 
    	 
    	 
    	
     }
   
    
	
     /**
      * Invoked when the Activity loses user focus.
      */
     @Override
     protected void onPause() {
         super.onPause();
     }
     
     @Override
     protected void onDestroy() {
         super.onDestroy();
     }
     
     @Override
     protected void onSaveInstanceState(Bundle outState) {
         // just have the View's thread save its state into our Bundle
         super.onSaveInstanceState(outState);
     }
 	public class BackgroundThread extends Thread {

		boolean running = false;
		private ResponseTableList rt_play;
		private ResponseTableList rt_real;

		void setRunning(boolean b){
			running = b;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
				totPlrs=0;
			try{
				rt_play = _proxy.getTableList(PokerGameType.Play_Holdem);// Table type is 1
				int count = rt_play.getGameCount();
				int droidTables = 0;
				for (int i=0;i < count;i++)
				{
					GameEvent ge = rt_play.getGameEvent(i);
					{
						totPlrs += ge.getPlayerCount();
						droidTables++;
					}
				}
				rt_real = _proxy.getTableList(PokerGameType.Real_Holdem);// Table type is 256
				count = rt_real.getGameCount();
				for (int i=0;i < count;i++)
				{
					GameEvent ge = rt_real.getGameEvent(i);
					{
						totPlrs += ge.getPlayerCount();
						droidTables++;
					}
				}
				//return(totPlrs);
			}
			catch (Exception e) { 
				Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
			}
			_mainlobbyhandler.sendEmptyMessage(1);
			}
		}
     
 }
