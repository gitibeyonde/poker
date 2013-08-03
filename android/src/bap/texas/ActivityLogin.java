package bap.texas;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.Timer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.ServiceState;
import android.text.Spannable;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.Response;
import bap.texas.common.message.ResponseLogin;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.PokerGameType;
import bap.texas.util.SoundManager;


public class ActivityLogin extends Activity {
	public static final String PREFS_NAME = "MyPrefsFile";
	public static ProgressDialog loadprogress = null;
	private ActivityManager manager;
	private ProgressDialog m_ProgressDialog = null,m_ProgressDialog2=null;
	static SoundManager mSoundManager;
	private SharedPreferences settings;
	private ServiceState serviceState; 
	private Dialog m_dialog = null;
	private Runnable viewOrders;
	private Proxy _proxy;
	private float _uw, _urw;

	private Button _loginButton;
	private TextView _signupButton;
	private Button _exitButton;

	private EditText _userText;
	private EditText _passText;
	private TextView _statusText;
	private CheckBox _remember;
	private String res;
	private Timer myTimer;
	private TextView _loginTV;
	private TextView _plrsonline;
	private TextView _playersview;
	private TextView _versionview;
	private TextView _remeberview;
	private TextView _registarview;
	private TextView _forgotpassword;
	private GameEvent ge;
	private ResponseTableList rt1;
	public  int totPlrs;
	private int count;
	private UnderlineSpan underline;
	private SubscriptSpan subscript;
	private SuperscriptSpan superscript;
	private URLSpan url;
	private ScaleXSpan scaleX;
	private RelativeSizeSpan relative;
	private Spannable spannable; 
	public static String language;
	public static boolean _soundAllowed = true;
	public static Properties properties;
	public static String usernameText="";
	public Handler _handler = new Handler(){

		public void handleMessage(Message msg){
			if(msg.what==1)
			{
				Log.i("next Activity","calling");
//				_plrsonline.setText(totPlrs+"");
				 NumberFormat format = new DecimalFormat("00000");
		    	 _plrsonline.setText(""+format.format(totPlrs));
				//m_ProgressDialog2.show();
				//    			 m_ProgressDialog2 = ProgressDialog.show(ActivityLogin.this,    
				//          	            "Please wait...", "Loading Next Activity", true);
			}
		}

	};
	private BackgroundThread login_bgThread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);
		
		try {
			_proxy = new Proxy(Command.IP,Command.PORT);
		}
		 catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		login_bgThread = new BackgroundThread();
		login_bgThread.setRunning(true);
		login_bgThread.start();
		
		_loginTV = (TextView)findViewById(R.id.loginid);
		_userText = (EditText)findViewById(R.id.userText);
		_passText = (EditText)findViewById(R.id.passText);
		_statusText = (TextView)findViewById(R.id.statusText);
		_loginButton = (Button)findViewById(R.id.loginButton);
		_remember = (CheckBox)findViewById(R.id.startCheckBox);
		_exitButton = (Button)findViewById(R.id.exitButton);
		_plrsonline=(TextView)findViewById(R.id.playersonline);
		_playersview=(TextView)findViewById(R.id.playersview);

		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(SoundManager.click, R.raw.click);
		mSoundManager.addSound(SoundManager.fold, R.raw.fold);

		//---- setting out of service to avoid telephone calls 
		serviceState = new ServiceState();
		serviceState.setState(ServiceState.STATE_OUT_OF_SERVICE);

		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);   
		final KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);   
		lock.disableKeyguard();   

		settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);       
		String user = settings.getString("user", "enter username");            
		String pass = settings.getString("pass", "enter password");
		boolean remember=settings.getBoolean("remember", false);
		_soundAllowed = settings.getBoolean("sound",false);
		
		language = settings.getString("languageLocal", "English");
		
		
		properties = new Properties();
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();
		
		// Read from the /assets directory
		try {
		    InputStream inputStream = assetManager.open(language+".properties");
		    properties.load(inputStream);
		} catch (IOException e) {
		    e.printStackTrace();
		}

		_loginTV.setText(properties.getProperty("login"));//getString(R.string.login));
		

		_plrsonline.setTypeface(ActivitySplash.paintobjects.get("dotstypeface")); 
		_playersview.setTypeface(ActivitySplash.paintobjects.get("myriadface")); 
		_remember.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_statusText.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_userText.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_passText.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_userText.setSelectAllOnFocus(true);
		_passText.setSelectAllOnFocus(true);

		if(remember)
		{
			_userText.setText(user);  
			_passText.setText(pass);
		}
		_remember.setChecked(remember);
		Log.i("Start Checked Value",""+remember);
		try
		{
			Bundle b = this.getIntent().getExtras();
			Log.i("bundle",b+"");
			if (b != null){

				String status = (String)b.getString("loginstatus");
				Log.i("status1",status);
				if (status != null)

				{_statusText.setText((String)b.getString("loginstatus"));
				Log.i("status2",status);
				}
			}
			/* _proxy = new Proxy("192.168.0.2", 8985);
        	 _proxy.pingBlocking();
        	 NumberFormat format = new DecimalFormat("00000");
        	 _plrsonline.setText(""+format.format(_proxy._active_players));*/
		}
		catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
		}
		
		_remember.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);  
				SharedPreferences.Editor editor = settings.edit();      
				if (_remember.isChecked()){
					editor.putString("user", _userText.getText().toString());    
					editor.putString("pass", _passText.getText().toString());
				}
				editor.putBoolean("remember",_remember.isChecked());
				editor.commit();    
				Log.i("Check box Values....",""+_remember.isChecked());

			}
		});

		_loginButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//login_bgThread.stop();
				Log.w("ActivityStart onCreate", _userText.getText().toString());
				Log.w("ActivityStart onCreate", _passText.getText().toString());
				Log.i("Check box status",ActivitySettings.colorsdeck+"");
				if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
				viewOrders = new Runnable(){
					@Override
					public void run() {
						res = validate(_userText.getText().toString(), _passText.getText().toString());
						if (res == "loggedin"){
							Intent i = new Intent(ActivityLogin.this, 
									ActivityMainLobby.class); 

							/* Create a bundle that will   
							 * hold will be passed to the 
							 * SubActivityover the Intent */ 
							Bundle bundle = new Bundle(); 
							bundle.putString("user", _userText.getText().toString()); 
							bundle.putString("pass", _passText.getText().toString()); 
							bundle.putFloat("uw", _uw); 
							bundle.putFloat("urw", _urw);
							bundle.putInt("onlineplayers", totPlrs);
							usernameText=_userText.getText().toString();
							i.putExtras(bundle); 
							//backgroundThread.stop();
							m_ProgressDialog.cancel();
							_handler.sendEmptyMessage(1);
							// We use SUB_ACTIVITY_REQUEST_CODE as an 'identifier' 
							ActivityLogin.this.startActivity(i); 

							

						}
						else {
							Intent i = new Intent(ActivityLogin.this, 
									ActivityLogin.class); 
							/* Create a bundle that will   
							 * hold will be passed to the 
							 * SubActivityover the Intent */ 
							Bundle bundle = new Bundle(); 
							if(res == "fail")bundle.putString("loginstatus", getString(R.string.Invaliduseridpassword)); 
							else if(res == "connFail")bundle.putString("loginstatus", "Connection failed, try again"); 
							i.putExtras(bundle); 
							// We use SUB_ACTIVITY_REQUEST_CODE as an 'identifier' 
							ActivityLogin.this.startActivity(i); 
						}
					}
				};
				Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
				thread.start();
				m_ProgressDialog = ProgressDialog.show(ActivityLogin.this,    
						"Please wait...", "Validating...", true);

			}
		});
		_exitButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(_soundAllowed)mSoundManager.playSound(SoundManager.fold);
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
	private Runnable returnRes = new Runnable() {
		@Override
		public void run() {
			m_ProgressDialog.dismiss();
		}
	};

	private String validate(String usr, String pass){
		String result = "";
		try{
			_proxy = new Proxy(Command.IP,Command.PORT);
			//_proxy = new Proxy("66.220.9.100",8985);
			//        	_proxy = new Proxy("192.168.0.2", 8985);
			Response rtl = _proxy.loginReturn(usr, pass);
			if (rtl != null){
				Log.w("Activity Start validate", rtl.toString());
				ResponseLogin rl = (ResponseLogin)rtl;
				Log.i("playworth",""+rl.getPlayWorth());
				Log.i("Realworth",""+rl.getRealWorth());
				_uw = (float)rl.getPlayWorth();
				_urw = (float)rl.getRealWorth();
				result = "loggedin";
				return result;
			}
		} catch (Exception e) { 
			result = "connFail";  
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
		if(result == "")result = "fail"; 
		return result;

	}


	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (_remember.isChecked()){
			// store the credentials 
			settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);      
			SharedPreferences.Editor editor = settings.edit();      
			editor.putString("user", _userText.getText().toString());    
			editor.putString("pass", _passText.getText().toString());
			editor.putBoolean("remember",_remember.isChecked());
			editor.commit();    
		}

	}
	@Override
	protected void onStop() {
		super.onStop();


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
			
			_handler.sendEmptyMessage(1);
			}
		}
}
