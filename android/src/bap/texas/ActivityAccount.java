package bap.texas;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import bap.texas.common.message.Command;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.SoundManager;

//Gallery g;
public class ActivityAccount extends Activity implements OnItemSelectedListener {
	private ProgressDialog m_ProgressDialog = null;
	private Proxy _proxy;
	private Button _saveButton, _cancelButton, _leftmoveB, _rightmoveB, _changepassword;
	private EditText _userpassAC, _emailAC;
	private TextView _usernameAC, _statusACC,_myaccountTV,_youTV;
	private Bundle tb;
	private String _user;
	private Gallery g;
	private String res;
	private GameEvent ge;
	private ResponseTableList rt1;
	private int totPlrs;
	private int count;
	private float _uw, _urw;
	private Runnable viewOrders;
	private ImageAdapter _imageadapter;
	String PREFERENCE_NAME="accountProfile";
	int i = 0;
	private SoundManager mSoundManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
    	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.account);
		tb = this.getIntent().getExtras();

		_myaccountTV=(TextView)findViewById(R.id.myaccountTV);
		_youTV = (TextView)findViewById(R.id.youTV);
		_saveButton = (Button) findViewById(R.id.savebuttonMYACC);
		_cancelButton = (Button) findViewById(R.id.cancelbuttonMYACC);
		_usernameAC = (TextView) findViewById(R.id.usernameMYACC);
		_userpassAC = (EditText) findViewById(R.id.userpassMYACC);
		_emailAC = (EditText) findViewById(R.id.emailidMYACC);
		_leftmoveB = (Button) findViewById(R.id.leftmove);
		_rightmoveB = (Button) findViewById(R.id.rightmove);
		_statusACC = (TextView) findViewById(R.id.statusACC);
		_changepassword = (Button)findViewById(R.id.changepassword);
		Bundle b = this.getIntent().getExtras();
		_user = (String) b.getString("user");
		_usernameAC.setText(_user);

		_myaccountTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_youTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_saveButton.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_cancelButton.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_userpassAC.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_emailAC.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_changepassword.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		_usernameAC.setTypeface(ActivitySplash.paintobjects.get("dotstypeface"));
		_emailAC.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
		
		
		if (b.getString("status") != null) {
			_statusACC.setText(b.getString("status"));
			_statusACC.setTextColor(Color.rgb(190, 30, 45));
		}
		g = (Gallery) findViewById(R.id.gallery1);
		g.setOnItemSelectedListener(this);
		_imageadapter = new ImageAdapter(this);
		g.setAdapter(_imageadapter);
		SharedPreferences settings = getSharedPreferences(PREFERENCE_NAME,Activity.MODE_PRIVATE);
		i = settings.getInt("avatar_number", 0);
		g.setSelection(i);
		Log.i("selcted position is:", "" + i);
		
		
		/*try {
			_proxy = new Proxy("66.220.9.100", 8986);
			rt1 = _proxy.getTableList(PokerGameType.Play_Holdem);// Table type
			// is Play
			count = rt1.getGameCount();
			for (int i = 0; i < count; i++) {
				ge = rt1.getGameEvent(i);
				totPlrs += ge.getPlayerCount();
			}

			rt1 = _proxy.getTableList(PokerGameType.Real_Holdem);// Table type
			// is Play
			count = rt1.getGameCount();
			for (int i = 0; i < count; i++) {
				ge = rt1.getGameEvent(i);
				totPlrs += ge.getPlayerCount();
			}

		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getStackTrace().toString());
		}
*/
		/*_userpassAC.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				_userpassAC.setTransformationMethod(new PasswordTransformationMethod());

				return false;
			}
		});*/

		_leftmoveB.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.i("ImageAdapter SIze", "i LeftValue=" + i);

				if (i > 0) {
					i--;
					g.setSelection(i);
				}
			}
		});
		_rightmoveB.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.i("ImageAdapter SIze", _imageadapter.getCount() + ""
						+ "i RightValues=" + i);

				if (i < _imageadapter.getCount() - 1) {
					i++;
					g.setSelection(i);
				}
			}
		});
		_saveButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (ActivityLogin._soundAllowed)
					ActivityLogin.mSoundManager.playSound(SoundManager.click);
				Intent i = new Intent(ActivityAccount.this,
						ActivityMainLobby.class);
				
				//String url = "http://67.211.101.97:8080/bluespade/servlet/changeAvatar?uid="+_emailAC.getText().toString()+"&pwd="+_userpassAC.getText().toString()+"&selAvtr="+(g.getSelectedItemPosition()+1);
				String url = "http://www.blueacepoker.com/servlet/changeAvatar?uid="+_emailAC.getText().toString()+"&pwd="+_userpassAC.getText().toString()+"&selAvtr="+(g.getSelectedItemPosition()+1);
				
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpGet httppost = new HttpGet(url);
					HttpResponse response = httpclient.execute(httppost);
					String responsestatulline = response.getStatusLine().toString();
					Log.i("response",responsestatulline);
 					if(responsestatulline.contains("OK")){
 						Log.e("excecuted","succesfully"+responsestatulline);
 						SharedPreferences settings = getSharedPreferences(PREFERENCE_NAME,Activity.MODE_PRIVATE);
 						SharedPreferences.Editor editor = settings.edit();
 						editor.putInt("avatar_number", g.getSelectedItemPosition());
 						editor.commit();
 					}
 					else
 						Log.e("Not excecuted","succesfully"+responsestatulline);;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bundle b = new Bundle();
				b.putString("user", tb.getString("user"));
				b.putString("pass", tb.getString("pass"));
				b.putFloat("uw", tb.getFloat("uw"));
				b.putFloat("urw", tb.getFloat("urw"));
				i.putExtras(b);
				ActivityAccount.this.startActivity(i);
			}
		});
		_changepassword.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				if (ActivityLogin._soundAllowed)
					ActivityLogin.mSoundManager.playSound(SoundManager.click);
				Intent i = new Intent(ActivityAccount.this,
						ActivityChangePassword.class);
				Bundle b = new Bundle();
				b.putString("user", tb.getString("user"));
				b.putString("pass", tb.getString("pass"));
				b.putFloat("uw", tb.getFloat("uw"));
				b.putFloat("urw", tb.getFloat("urw"));
				i.putExtras(b);
				ActivityAccount.this.startActivity(i);
			}
		});
		_cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (ActivityLogin._soundAllowed)
					ActivityLogin.mSoundManager.playSound(SoundManager.click);
				Intent i = new Intent(ActivityAccount.this,
						ActivityMainLobby.class);
				Bundle b = new Bundle();
				b.putString("user", tb.getString("user"));
				b.putString("pass", tb.getString("pass"));
				b.putFloat("uw", tb.getFloat("uw"));
				b.putFloat("urw", tb.getFloat("urw"));
				i.putExtras(b);
				ActivityAccount.this.startActivity(i);

			}
		});

	}

	private View mPrev;
	private LayoutParams mParams[] = { 
										new Gallery.LayoutParams(80, 80),
										new Gallery.LayoutParams(90, 90),
										new Gallery.LayoutParams(100, 100),
										new Gallery.LayoutParams(110, 110),

									};

	private static final int MSG_ZOOM_OUT = 1;
	private static final int MSG_ZOOM_IN = 2;
	protected static final long DELAY = 100;
	Handler mGalleryHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			ImageView view = (ImageView) msg.obj;
			LayoutParams param = view.getLayoutParams();
			Log.i("LayoutParamiters", "height" + param.height + "width"
					+ param.width + param.WRAP_CONTENT);
			int index = -1;
			for (int i = 0; i < mParams.length; i++) {
				if (mParams[i] == param) {
					index = i;
					break;
				}
			}
			if (index != -1) {
				index += msg.what == MSG_ZOOM_IN ? 1 : -1;
				if (index >= 0 && index < mParams.length) {
					view.setLayoutParams(mParams[index]);
				}
				index += msg.what == MSG_ZOOM_IN ? 1 : -1;
				if (index >= 0 && index < mParams.length) {
					sendMessageDelayed(Message.obtain(msg), DELAY);
				}
			}
		}
	};

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (mPrev != null) {
			mGalleryHandler.removeCallbacksAndMessages(mPrev);
			Message msg = Message.obtain(mGalleryHandler, MSG_ZOOM_OUT, mPrev);
			mGalleryHandler.sendMessageDelayed(msg, DELAY);
		}
		mPrev = view;
		mGalleryHandler.removeCallbacksAndMessages(view);
		Message msg = Message.obtain(mGalleryHandler, MSG_ZOOM_IN, view);
		mGalleryHandler.sendMessageDelayed(msg, DELAY);
		i = g.getSelectedItemPosition();
		
		// Toast.makeText(ActivityAccount.this, "" + position,
		// Toast.LENGTH_SHORT).show();
	}

	private Runnable returnRes = new Runnable() {
		@Override
		public void run() {
			m_ProgressDialog.dismiss();
		}
	};

	

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		int mGalleryItemBackground;
		private Context mContext;

		private Integer[] avatarsIds = {

		R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
				R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6,
				R.drawable.avatar7, R.drawable.avatar8, R.drawable.avatar9,
				R.drawable.avatar10, R.drawable.avatar11, R.drawable.avatar12,
				R.drawable.avatar13, R.drawable.avatar14, R.drawable.avatar15,
				R.drawable.avatar16, R.drawable.avatar17, R.drawable.avatar18,
				R.drawable.avatar19, R.drawable.avatar20, R.drawable.avatar21,
				R.drawable.avatar22, R.drawable.avatar23, R.drawable.avatar24,
				R.drawable.avatar25, R.drawable.avatar26, R.drawable.avatar27,
				R.drawable.avatar28, R.drawable.avatar29, R.drawable.avatar30 };

		public int getCount() {
			return avatarsIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(mParams[0]);
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(25, 15, 15, 15);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(avatarsIds[position]);

			return imageView;
		}

		// private Context mContext;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
