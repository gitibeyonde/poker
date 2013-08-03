package bap.texas;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.PokerGameType;
import bap.texas.util.SoundManager;

public class ActivitySettings extends Activity   {

	public static final String PREFS_NAME = "MyPrefsFile";
	private CheckBox _autopostblindCB;
	private CheckBox _colordeskCB;
	private CheckBox _soundCB;
	private Button _okButton,_cancelButton;
	private Spinner _languages;
	private Bundle tb ;
	private TextView _autopostblindTV,_soundTV,_colordeskTV,_settingsTV,_usernamesettings;
	private Proxy _proxy;
	private ResponseTableList rt1;
	private int count;
	private GameEvent ge;
	private int totPlrs;
	private TextView _plrsonline;
	private TextView _playersview;
	public static boolean autoblind = false; 
	public static boolean colorsdeck = true;
	Resources resources;
	AssetManager assetManager;
	private BackgroundThread settings_bgThread;
	
	 public Handler _settingshandler = new Handler(){
	    	public void handleMessage(Message msg){
	    		if(msg.what==1)
	    		{
	    			NumberFormat format = new DecimalFormat("00000");
	    			_plrsonline.setText(""+format.format(totPlrs));
	    		}
	    	}
	    };
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
   	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.settings);
		tb= this.getIntent().getExtras();

		_settingsTV = (TextView)findViewById(R.id.settingsTV);
		_usernamesettings = (TextView)findViewById(R.id.userNamesettings);
		_autopostblindTV = (TextView)findViewById(R.id.autopostblindTEXTVIEW);
		_soundTV = (TextView)findViewById(R.id.soundTEXTVIEW);
		_colordeskTV = (TextView)findViewById(R.id.colordeskTEXTVIEW);
		 _plrsonline=(TextView)findViewById(R.id.playersonlinesNumber);
    	 _playersview=(TextView)findViewById(R.id.playerstextviewsettings);
		
		_autopostblindCB = (CheckBox)findViewById(R.id.autopostblindCB);
		_soundCB = (CheckBox)findViewById(R.id.soundCB);
		_colordeskCB = (CheckBox)findViewById(R.id.colordeskCB);

		_okButton= (Button)findViewById(R.id.okB);
		_settingsTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_usernamesettings.setTypeface(ActivitySplash.paintobjects.get("dotstypeface"));
		_playersview.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_plrsonline.setTypeface(ActivitySplash.paintobjects.get("dotstypeface"));
		_autopostblindTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_soundTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_colordeskTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));

		_usernamesettings.setText(tb.getString("user"));
		
		try {
			//_proxy = new Proxy("67.211.101.97",8985);
			 //_proxy = new Proxy("66.220.9.100",8985);
			_proxy = new Proxy(Command.IP,Command.PORT);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		settings_bgThread = new BackgroundThread();
		settings_bgThread.setRunning(true);
		settings_bgThread.start();
		
		
		
		/*try
		{
		 _proxy = new Proxy("67.211.101.97",8986);
		 rt1 = _proxy.getTableList(PokerGameType.Play_Holdem);// Table type is Play 
		 count = rt1.getGameCount();
		 for (int i=0;i < count;i++)
		 {
			 ge = rt1.getGameEvent(i);
			 totPlrs += ge.getPlayerCount();
		
		 }
		 
		 rt1 = _proxy.getTableList(PokerGameType.Real_Holdem);// Table type is Play 
		 count = rt1.getGameCount();
		 for (int i=0;i<count;i++)
		 {
			 ge = rt1.getGameEvent(i);
			 totPlrs +=ge.getPlayerCount();
			
		 }
		}
		catch(Exception e)
		{
		}
		 NumberFormat format = new DecimalFormat("00000");
		 _plrsonline.setText(""+format.format(totPlrs));*/
		/*try {
			_proxy = new Proxy("192.168.0.2", 8985);
			 _proxy.pingBlocking();
	    	 NumberFormat format = new DecimalFormat("00000");
	    	 _plrsonline.setText(""+format.format(_proxy._active_players));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		_languages = (Spinner) findViewById(R.id.languagespinner);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.spinnerrowsettings);
		//adapter.setDropDownViewResource(android.R.layout.test_list_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		_languages.setAdapter(adapter);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);       
		_autopostblindCB.setChecked(settings.getBoolean("autoblind",false));            
		_soundCB.setChecked(settings.getBoolean("sound",false));   
		_colordeskCB.setChecked(settings.getBoolean("colordesk",false));
		_languages.setSelection(settings.getInt("language",0));

		if(settings.getBoolean("autoblind",false))
			_autopostblindTV.setTextColor(Color.BLACK);
		if(settings.getBoolean("sound",false))
			_soundTV.setTextColor(Color.BLACK);
		if(settings.getBoolean("colordesk",false))
			_colordeskTV.setTextColor(Color.BLACK);
		resources = this.getResources();
		assetManager = resources.getAssets();
		
		_languages.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
			
				// Read from the /assets directory
				try {
				    InputStream inputStream = assetManager.open((String)arg0.getSelectedItem()+".properties");
				    ActivityLogin.properties.load(inputStream);
//				    System.out.println("The properties are now loaded");
//				    System.out.println("properties: " + properties);
				} catch (IOException e) {
//				    System.err.println("Failed to open microlog property file");
				    e.printStackTrace();
				}
				_settingsTV.setText(ActivityLogin.properties.getProperty("settings"));
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		_settingsTV.setText(ActivityLogin.properties.getProperty("settings"));
		
		_soundCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (_soundCB.isChecked()) 
					_soundTV.setTextColor(Color.BLACK);
				else
					_soundTV.setTextColor(Color.WHITE);
			}
		});
		_autopostblindCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (_autopostblindCB.isChecked()) 
					_autopostblindTV.setTextColor(Color.BLACK);
				else
					_autopostblindTV.setTextColor(Color.WHITE);

			}
		});
		_colordeskCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (_colordeskCB.isChecked()) 
					_colordeskTV.setTextColor(Color.BLACK);
				else
					_colordeskTV.setTextColor(Color.WHITE);
			}
		});

		_okButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
				Intent i = new Intent(ActivitySettings.this, 
						ActivityMainLobby.class); 
				// store the credentials 
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);      
				SharedPreferences.Editor editor = settings.edit();      
				editor.putBoolean("autoblind", _autopostblindCB.isChecked());    
				editor.putBoolean("sound", _soundCB.isChecked());
				editor.putBoolean("colordesk",_colordeskCB.isChecked());
			    editor.putString("languageLocal", (String)_languages.getSelectedItem());
			    editor.putInt("language", _languages.getSelectedItemPosition());
				editor.commit();   
				ActivityLogin._soundAllowed = _soundCB.isChecked();
				colorsdeck = _colordeskCB.isChecked();
				autoblind = _autopostblindCB.isChecked();

				Bundle b = new Bundle(); 
				b.putString("user", tb.getString("user")); 
				b.putString("pass",tb.getString("pass")); 
				b.putFloat("uw", tb.getFloat("uw")); 
				b.putFloat("urw", tb.getFloat("urw"));
				i.putExtras(b);
				ActivitySettings.this.startActivity(i); 
			}
		});
//		_cancelButton.setOnClickListener(new Button.OnClickListener(){
//			public void onClick(View v)
//			{
//				if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
//				Intent i = new Intent(ActivitySettings.this,ActivityMainLobby.class); 
//				Bundle b = new Bundle(); 
//				b.putString("user", tb.getString("user")); 
//				b.putString("pass",tb.getString("pass")); 
//				b.putFloat("uw", tb.getFloat("uw")); 
//				b.putFloat("urw", tb.getFloat("urw"));
//				i.putExtras(b);
//				ActivitySettings.this.startActivity(i); 
//			}
//		});
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
			_settingshandler.sendEmptyMessage(1);
			}
		}
	
	
}
