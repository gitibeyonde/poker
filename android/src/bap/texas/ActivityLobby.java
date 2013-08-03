package bap.texas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.PokerGameType;
import bap.texas.util.SoundManager;

public class ActivityLobby extends Activity {
	//private ProgressDialog m_ProgressDialog = null; 
	private ArrayList<ListItem> m_tables = null;
	//    private TableListAdapter m_adapter;
	public static final String PREFS_NAME = "MyPrefsFileforLOBBY";
	private Runnable viewOrders;
	private Button _cancellobbyB;
	private Button _playlobbyB;
	private ResponseTableList rt_play,rt_real; 
	private Proxy _proxy;
	private String _user, _pass;
	private float _uw, _urw;
	private int _vacantPosition;
	private int totPlrs;
	private GameEvent ge;
	private ResponseTableList rt1;
	private int count;
	private ArrayAdapter cardgametype_adapter,gametype_adapter,bettype_adapter,tableseats_adapter,minopponent_adapter,flop_adapter,
	moneytype_adapter,moneyrange_adapterRingREAL,moneyrange_adapterRingPLAY,moneyrange_adapterSintREAL,moneyrange_adapterSintPLAY;
	private Spinner _cardgametype,_gametype,_bettype,_tableseats,_minopponent,_flop,_moneytype,_moneyrange;
	private Bundle tb;
	private AlertDialog.Builder alt_bld;
	private String gametypevalue,moneytypevalue;
	private TextView _usernamelobby,_lobby,_playersonline,_playersnumber;

	public Handler lobbyhandler = new Handler(){
    	public void handleMessage(Message msg){
    		if(msg.what==1)
    		{
    			NumberFormat format = new DecimalFormat("00000");
    			_playersnumber.setText(""+format.format(totPlrs));
    		}
    	}
    };
	private BackgroundThread _lobby_bgthread;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
    	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lobby);

		try{
			//_proxy = new Proxy("67.211.101.97",8985);
			 //_proxy = new Proxy("66.220.9.100",8985);
			_proxy = new Proxy(Command.IP,Command.PORT);
		}
		catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
		}
		
		_lobby_bgthread = new BackgroundThread();
		_lobby_bgthread.setRunning(true);
		_lobby_bgthread.start();
		
		tb= this.getIntent().getExtras();

		_playlobbyB = (Button)findViewById(R.id.playlobbyB);
		_cancellobbyB = (Button)findViewById(R.id.cancellobbyB);
		_cardgametype =(Spinner)findViewById(R.id.cardgametypespinner);
		_gametype =(Spinner)findViewById(R.id.gametypespinner);
		_moneytype = (Spinner)findViewById(R.id.moneytypespinner);
		_moneyrange = (Spinner)findViewById(R.id.moneyspinner);
		_bettype=(Spinner)findViewById(R.id.bettingtypespinner);
		_tableseats=(Spinner)findViewById(R.id.tableseatspinner);
		_minopponent=(Spinner)findViewById(R.id.minopponentspinner);
		_flop=(Spinner)findViewById(R.id.flopspinner);
		
		_lobby = (TextView) findViewById(R.id.lobbyTV);
		_usernamelobby = (TextView) findViewById(R.id.userNameLobby);
		_playersonline = (TextView) findViewById(R.id.playerstextviewlobby);
		_playersnumber = (TextView) findViewById(R.id.playersonlinelobby);
		
		_lobby.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_usernamelobby.setTypeface(ActivitySplash.paintobjects.get("dotstypeface"));
		_playersonline.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_playersnumber.setTypeface(ActivitySplash.paintobjects.get("dotstypeface"));
		String[] str={"aaa","bbbb","vvvv","33333"};
		
		//_moneytype.setPrompt("500");

		cardgametype_adapter = ArrayAdapter.createFromResource(this, R.array.CardGameType, R.layout.spinnerrow);
		_cardgametype.setAdapter(cardgametype_adapter);
		gametype_adapter = ArrayAdapter.createFromResource(this, R.array.GameType, R.layout.spinnerrow);
		_gametype.setAdapter(gametype_adapter);
		moneytype_adapter = ArrayAdapter.createFromResource(this, R.array.moneytypearray, R.layout.spinnerrow);
		_moneytype.setAdapter(moneytype_adapter);
		moneyrange_adapterRingREAL = ArrayAdapter.createFromResource(this, R.array.ringgamerealmoney, R.layout.spinnerrow);
		_moneyrange.setAdapter(moneyrange_adapterRingREAL);
		moneyrange_adapterRingPLAY = ArrayAdapter.createFromResource(this, R.array.ringgameplaymoney, R.layout.spinnerrow);
		moneyrange_adapterSintREAL = ArrayAdapter.createFromResource(this, R.array.sitngorealmoney, R.layout.spinnerrow);
		moneyrange_adapterSintPLAY = ArrayAdapter.createFromResource(this, R.array.sitngoplaymoney, R.layout.spinnerrow);
		bettype_adapter = ArrayAdapter.createFromResource(this, R.array.BettingType, R.layout.spinnerrow);
		_bettype.setAdapter(bettype_adapter);
		tableseats_adapter = ArrayAdapter.createFromResource(this, R.array.TableSeats, R.layout.spinnerrow);
		_tableseats.setAdapter(tableseats_adapter);
		minopponent_adapter = ArrayAdapter.createFromResource(this, R.array.minOpponents, R.layout.spinnerrow);
		_minopponent.setAdapter(minopponent_adapter);
		flop_adapter = ArrayAdapter.createFromResource(this, R.array.Flop, R.layout.spinnerrow);
		_flop.setAdapter(flop_adapter);

		alt_bld = new AlertDialog.Builder(this);
		alt_bld.setCancelable(false);
		alt_bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);       
		_cardgametype.setSelection(settings.getInt("cardgame",0));
		_gametype.setSelection(settings.getInt("gametype",0));
		_bettype.setSelection(settings.getInt("bettype",0));
		_tableseats.setSelection(settings.getInt("tableseats",0));
		_minopponent.setSelection(settings.getInt("minopponent",0));
		_flop.setSelection(settings.getInt("flop",0));
		_moneytype.setSelection(settings.getInt("moneytype",0));
		_moneyrange.setSelection(settings.getInt("moneyrange",0));
		
		/*try{
			//_proxy = new Proxy("66.220.9.100", 8986);
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
		}
		catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
		}*/
		
		Bundle b = this.getIntent().getExtras(); 
		if (b != null){
			_user = (String)b.getString("user");
			_pass = (String)b.getString("pass");
			_uw = (float)b.getFloat("uw");
			_urw = (float)b.getFloat("urw");
		}

		/*try {
			_proxy = new Proxy("192.168.0.2", 8985);
			 _proxy.pingBlocking();
	    	 NumberFormat format = new DecimalFormat("00000");
	    	 _playersnumber.setText(""+format.format(_proxy._active_players));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	*/
		_usernamelobby.setText(_user);
		
		
		_gametype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				gametypevalue = (String) parent.getItemAtPosition(pos);
				moneytypevalue = (String) _moneytype.getItemAtPosition(_moneytype.getSelectedItemPosition());
				if(gametypevalue.equals("Ring game"))
				{
					if(moneytypevalue.equals("Real Money"))
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterRingREAL);
					}
					else
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterRingPLAY);
					}
				}
				else
				{
					if(moneytypevalue.equals("Real Money"))
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterSintREAL);
					}
					else
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterSintPLAY);
					}
				}
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		_moneytype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				moneytypevalue = (String) parent.getItemAtPosition(pos);
				if(gametypevalue.equals("Ring game"))
				{
					if(moneytypevalue.equals("Real Money"))
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterRingREAL);
					}
					else
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterRingPLAY);
					}
				}
				else
				{
					if(moneytypevalue.equals("Real Money"))
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterSintREAL);
					}
					else
					{
						_moneyrange.removeAllViewsInLayout();
						_moneyrange.setAdapter(moneyrange_adapterSintPLAY);
					}
				}

			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		_playlobbyB.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try
				{
					if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
					
					
					Bundle b = new Bundle(); 
					b.putString("cardgame",(String)_cardgametype.getSelectedItem());
					b.putString("gametype", (String)_gametype.getSelectedItem());
					b.putString("moneytype", (String)_moneytype.getSelectedItem());
					b.putString("moneyrange", (String)_moneyrange.getSelectedItem());
					b.putString("bettype", (String)_bettype.getSelectedItem());
					b.putString("tableseats", (String)_tableseats.getSelectedItem());
					b.putString("minopponent", (String)_minopponent.getSelectedItem());
					b.putString("flop", (String)_flop.getSelectedItem());
					b.putString("user", _user); 
					b.putString("pass", _pass); 
					b.putFloat("uw", _uw);
					b.putFloat("urw", _urw);

					SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);      
					SharedPreferences.Editor editor = settings.edit();      
					editor.putInt("cardgame",_cardgametype.getSelectedItemPosition());
					editor.putInt("gametype", _gametype.getSelectedItemPosition());
					editor.putInt("moneytype", _moneytype.getSelectedItemPosition());
					editor.putInt("moneyrange", _moneyrange.getSelectedItemPosition());
					editor.putInt("bettype", _bettype.getSelectedItemPosition());
					editor.putInt("tableseats", _tableseats.getSelectedItemPosition());
					editor.putInt("minopponent", _minopponent.getSelectedItemPosition());
					editor.putInt("flop", _flop.getSelectedItemPosition());
					editor.commit();


					String s,str,minbet,maxbet,tableseats;
					int bettype;
					if(_bettype.getSelectedItem().equals("NL"))
					{
						bettype=-1;
					}
					else
					{
						bettype=0;
					}
					tableseats = (String)_tableseats.getSelectedItem();
					if(_gametype.getSelectedItem().equals("Ring game"))
					{
						if(_moneytype.getSelectedItem().equals("Real Money"))
						{
							s = (String)_moneyrange.getSelectedItem();
							StringTokenizer strton = new StringTokenizer(s, "$+/");
							minbet = strton.nextToken();
							maxbet = strton.nextToken();
							getVacantPosition(minbet, bettype,tableseats ,PokerGameType.Real_Holdem);
						}
						else
						{
							s = (String)_moneyrange.getSelectedItem();
							StringTokenizer strton = new StringTokenizer(s, "$+/");
							minbet = strton.nextToken();
							maxbet = strton.nextToken();
							getVacantPosition(minbet, bettype, tableseats ,PokerGameType.Play_Holdem);

						}
					}
					else
					{
						if(_moneytype.getSelectedItem().equals("Real Money"))
						{
							s = (String)_moneyrange.getSelectedItem();
							StringTokenizer strton = new StringTokenizer(s, "$+/");
							minbet = strton.nextToken();
							maxbet = strton.nextToken();
							getVacantPosition(minbet, bettype, tableseats ,PokerGameType.Real_Holdem);
						}
						else
						{
							s = (String)_moneyrange.getSelectedItem();
							StringTokenizer strton = new StringTokenizer(s, "$+/");
							minbet = strton.nextToken();
							maxbet = strton.nextToken();
							getVacantPosition(minbet, bettype, tableseats ,PokerGameType.Play_Holdem);

						}
					}
				}
				catch(Exception e){

				}

			}
		});
		_cancellobbyB.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
				Intent i = new Intent(ActivityLobby.this,ActivityMainLobby.class); 
				Bundle b = new Bundle(); 
				b.putString("user", tb.getString("user")); 
				b.putString("pass",tb.getString("pass")); 
				b.putFloat("uw", tb.getFloat("uw")); 
				b.putFloat("urw", tb.getFloat("urw"));
				i.putExtras(b);
				ActivityLobby.this.startActivity(i); 
			}
		});
	}

	private void getVacantPosition(String min,int bettype, String tableseats,int type_game)
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
			Log.i("---------","-------");
			Log.i("min bet",ge.getMinBet()+"");
			Log.i("max bet",ge.getMaxBet()+"");
			Log.i("getPlayerCount()",ge.getPlayerCount()+"");
			Log.i("getMaxPlayers()",ge.getMaxPlayers()+"");
			Log.i("tableseats",tableseats);
			Log.i("min",min);
			Log.i("bettype",bettype+"");
			Log.i("---------","-------");
			if(ge.getMinBet() == Double.parseDouble(min) && ge.getMaxBet() == bettype && ge.getPlayerCount() < 10)//ge.getPlayerCount() < Integer.parseInt(tableseats)&& ge.getMaxPlayers() == Integer.parseInt(tableseats))
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
				Intent i = new Intent(ActivityLobby.this, ActivityTable.class); 
				b.putInt("type",type_game);
				i.putExtras(b);
				ActivityLobby.this.startActivity(i);
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
	
	public class BackgroundThread extends Thread {

		boolean running = false;
//		private ResponseTableList rt_play;
//		private ResponseTableList rt_real;

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
			lobbyhandler.sendEmptyMessage(1);
			}
		}

}
