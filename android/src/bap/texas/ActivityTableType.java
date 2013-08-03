package bap.texas;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.PokerGameType;
import bap.texas.util.SoundManager;
import bap.texas.util.Utils;


public class ActivityTableType extends Activity {

	private ProgressDialog m_ProgressDialog = null; 
    private Proxy _proxy;
    private String _user, _pass;
    private float _uw, _urw;
    private Button _type1PlayButton, _type2PlayButton, _type3PlayButton;
    private Button _type1RealButton, _type2RealButton, _type3RealButton;
    private Button _tableTypeBackButton;
    private TextView _silver, _gold, _avgPot, _totPlrs, _name;
    private ResponseTableList rt_play,rt_real; 
    private double avgPot;
    private int totPlrs;
    AlertDialog.Builder alt_bld;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 setContentView(R.layout.table_types);

    	 _type1PlayButton= (Button)findViewById(R.id.tableTypePlayC10NL);
    	 _type2PlayButton= (Button)findViewById(R.id.tableTypePlayC2NL);
    	 _type3PlayButton= (Button)findViewById(R.id.tableTypePlayC50NL);
    	 _type1RealButton= (Button)findViewById(R.id.tableTypeRealC10NL);
    	 _type2RealButton= (Button)findViewById(R.id.tableTypeRealC2NL);
    	 _type3RealButton= (Button)findViewById(R.id.tableTypeRealC50NL);
    	 _tableTypeBackButton = (Button)findViewById(R.id.tableTypeBack);
 		 
    	 _silver = (TextView)findViewById(R.id.tableTypeSilver);
    	 _gold = (TextView)findViewById(R.id.tableTypeGold);
    	 _name = (TextView)findViewById(R.id.tableTypeName);
    	 _avgPot = (TextView)findViewById(R.id.tableTypeAvgPot);
    	 _totPlrs = (TextView)findViewById(R.id.tableTypeTotPlrs);
    	 
			alt_bld = new AlertDialog.Builder(this);
			alt_bld.setCancelable(false);
			alt_bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			// Action for 'Yes' Button
				dialog.cancel();
			}
			});
    	 
    	 
    	 try{
    		// _proxy = new Proxy("67.211.101.97",8985);
    		 //_proxy = new Proxy("66.220.9.100",8985);
    		 _proxy = new Proxy(Command.IP,Command.PORT);
    		 rt_play = _proxy.getTableList(PokerGameType.Play_Holdem);// Table type is 1
    		 int count = rt_play.getGameCount();
    		 int droidTables = 0;
    		 for (int i=0;i < count;i++)
    		 {
    			 GameEvent ge = rt_play.getGameEvent(i);
    			 //if(ge.getGameName().startsWith("Droid"))
    			 {
    				 avgPot += ge.getAveragePot();
    				 totPlrs += ge.getPlayerCount();
    				 droidTables++;
    			 }
    		 }
    		 rt_real = _proxy.getTableList(PokerGameType.Real_Holdem);// Table type is 256
    		 count = rt_real.getGameCount();
    		 for (int i=0;i < count;i++)
    		 {
    			 GameEvent ge = rt_real.getGameEvent(i);
    			 //if(ge.getGameName().startsWith("Droid"))
    			 {
    				 avgPot += ge.getAveragePot();
    				 totPlrs += ge.getPlayerCount();
    				 droidTables++;
    			 }
    		 }
    		 avgPot /= droidTables;
    		 avgPot = Utils.getRounded(avgPot);
    	 }
		 catch (Exception e) { 
	        Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
	     }
		 _avgPot.setText(avgPot+"");
		 _totPlrs.setText(totPlrs+"");
		 
         Bundle b = this.getIntent().getExtras(); 
    	 if (b != null){
	         _user = (String)b.getString("user");
	         _pass = (String)b.getString("pass");
	         _uw = (float)b.getFloat("uw");
	         _urw = (float)b.getFloat("urw");
	         
	         _name.setText(_user);
	         _silver.setText("" + _uw);
	         _gold.setText("" + _urw);
    	 }
    	 

    	 
    	 _type1PlayButton.setOnClickListener(new Button.OnClickListener() {
    		 
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 getVacantPosition("0.02", PokerGameType.Play_Holdem);
            }
         });
    	 
    	 _type2PlayButton.setOnClickListener(new Button.OnClickListener() {
    		 
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 getVacantPosition("0.10", PokerGameType.Play_Holdem);
            }
         });
    	 _type3PlayButton.setOnClickListener(new Button.OnClickListener() {
    		 
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 getVacantPosition("0.50", PokerGameType.Play_Holdem);
            }
         });
    	 
    	 _type1RealButton.setOnClickListener(new Button.OnClickListener() {
    		 
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 getVacantPosition("0.02", PokerGameType.Real_Holdem);
            }
         });
    	 
    	 _type2RealButton.setOnClickListener(new Button.OnClickListener() {
    		 
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 getVacantPosition("0.10", PokerGameType.Real_Holdem);
            }
         });
    	 _type3RealButton.setOnClickListener(new Button.OnClickListener() {
    		 
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 getVacantPosition("0.50", PokerGameType.Real_Holdem);
            }
         });
    	 
    	 
    	         
    	 _tableTypeBackButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 try{
            	 Intent i = new Intent(ActivityTableType.this, 
                         ActivityMainLobby.class); 
            	 Bundle b = new Bundle(); 
 				 b.putString("user", _user); 
 				 b.putString("pass", _pass); 
 				 b.putFloat("uw", _uw);
				 b.putFloat("urw", _urw);
				 i.putExtras(b);
				 ActivityTableType.this.startActivity(i); 
				 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.fold);
				 }catch (Exception e) { 
         	        Log.e("ACTIVITY_TABLETYPE_PROC", e.getStackTrace().toString());
        	     }
             }
         });
     }
   
    private void getVacantPosition(String min, int type_game)
    {
    	String vacPosFound = "";
    	
	    	int gc = 0;
	    	if(type_game == PokerGameType.Real_Holdem)gc = rt_real.getGameCount();
	    	else if(type_game == PokerGameType.Play_Holdem)gc = rt_play.getGameCount();
	    	for (int i=0;i<gc ;i++)
	        {
	    		GameEvent ge = null;
	    		if(type_game == PokerGameType.Real_Holdem)ge = rt_real.getGameEvent(i);
		    	else if(type_game == PokerGameType.Play_Holdem)ge = rt_play.getGameEvent(i);
	    		Log.i("Table Details: ",ge.getGameName());
	        }
	        for (int i=0;i<gc && vacPosFound == "";i++)
	        {
	        	GameEvent ge = null;
	        	if(type_game == PokerGameType.Real_Holdem)ge = rt_real.getGameEvent(i);
		    	else if(type_game == PokerGameType.Play_Holdem)ge = rt_play.getGameEvent(i);
	    		
	    		if(ge.getMinBet() == Double.parseDouble(min) && ge.getMaxBet() == -1.0 && ge.getPlayerCount() < 9)
	    		{
	    			if(ge.getPlayerDetailsString().equals("none"))
	    			{
	    				vacPosFound = ge.getGameName()+","+0+"-"+ge.getMinBet();
	    			}
	    			else
	    			{
		    			String details[][] = ge.getPlayerDetails();
		    			Vector<String> ve = new Vector<String>();
		    			for (int j=0;j<details.length;j++)//filling vector with existed player positions
		    			{
		    				ve.add(details[j][0]);
		    				Log.i("Player Positions: ",details[j][0]);
		    			}
		    			for (int j=0;j<10;j++)//checking for the vacant position
		    			{
		    				if(!ve.contains(""+j))
		    				{
		    					vacPosFound =ge.getGameName()+","+j+"-"+ge.getMinBet();
		    					break;
		    				}
		    			}
	    			}
		    	}
	        }
	        Log.i("After loop Table Name: ",vacPosFound);
    		if(!vacPosFound.equals(""))
	        {
		        Bundle b = new Bundle(); 
		         
				 b.putString("user", _user); 
				 b.putString("pass", _pass); 
				 b.putFloat("uw", _uw);
				 b.putFloat("urw", _urw);
				 b.putString("pos", vacPosFound.substring(vacPosFound.indexOf(",")+1,vacPosFound.indexOf("-"))); 
 				 b.putString("tid", vacPosFound.substring(0, vacPosFound.indexOf(","))); 
				 b.putString("minbet", vacPosFound.substring(vacPosFound.indexOf("-")+1)); 
				 AlertDialog alert = alt_bld.create();
				 Log.i("min in String",min);
				 Log.i("min in Float",""+Float.parseFloat(min));
				 Log.i("Bundle value",""+b.getFloat("uw"));
				
				 if(type_game == PokerGameType.Play_Holdem && _uw >= Double.parseDouble(min)*100||
						 ( type_game == PokerGameType.Real_Holdem && _urw >= Double.parseDouble(min)*100))
		 		{
					Intent i = new Intent(ActivityTableType.this, ActivityTable.class); 
					b.putInt("type",type_game);
					i.putExtras(b);
					ActivityTableType.this.startActivity(i);
				 }
				 else
				 {
				    String s=(type_game==PokerGameType.Play_Holdem)?"Play":"Real";
				 	alert.setMessage("You Don't have sufficient "+s+" money");
					alert.show();
				 }
			}
    		else
    		{
				AlertDialog alert = alt_bld.create();
				alert.setMessage("All Tables are Full, Try Again...");
				alert.show();
       		}
      }
	
     /**
      * Invoked when the Activity loses user focus.
      */
@Override
     protected void onPause() {
         super.onPause();
     }
     
     @Override
     protected void onSaveInstanceState(Bundle outState) {
         // just have the View's thread save its state into our Bundle
         super.onSaveInstanceState(outState);
     }
     
 }
