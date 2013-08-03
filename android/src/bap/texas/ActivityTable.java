package bap.texas;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import bap.texas.action.Action;
import bap.texas.common.message.Command;
import bap.texas.common.message.CommandBuyChips;
import bap.texas.common.message.CommandGetChipsIntoGame;
import bap.texas.common.message.CommandInt;
import bap.texas.common.message.CommandLogin;
import bap.texas.common.message.CommandMessage;
import bap.texas.common.message.CommandMove;
import bap.texas.common.message.CommandRegister;
import bap.texas.common.message.CommandString;
import bap.texas.common.message.CommandTableDetail;
import bap.texas.common.message.CommandTableList;
import bap.texas.common.message.GameEvent;
import bap.texas.common.message.Response;
import bap.texas.common.message.ResponseConfig;
import bap.texas.common.message.ResponseFactory;
import bap.texas.common.message.ResponseGameEvent;
import bap.texas.common.message.ResponseLogin;
import bap.texas.common.message.ResponseMessage;
import bap.texas.common.message.ResponseTableDetail;
import bap.texas.common.message.ResponseTableList;
import bap.texas.server.Proxy;
import bap.texas.util.Base64;
import bap.texas.util.NextMove;
import bap.texas.util.PlayerPreferences;
import bap.texas.util.PokerGameType;
import bap.texas.util.SoundManager;

public class ActivityTable extends Activity implements Runnable {

	public static int playerposition;
	public static boolean next_move=false;
	boolean call = false;
	private ProgressDialog m_ProgressDialog = null;
	public static SoundManager mSoundManager;
	public static final String PREFS_NAME = "MyPrefsFile";
	public SharedPreferences settings ;
	private String _pass;
	public String _user;
	private String _tid;
	private int _type;
	private int _pos = -1;
	public double _worth;
	public float _uw, _urw;
	public double _minBet;
	private boolean _dead;
	private long HTBT_INTERVAL = 30000;
	private Selector _selector;
	private SocketChannel _channel;
	private String _session = null;
	private ByteBuffer _h, _b, _o;
	private int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
	private String _comstr;
	private boolean keepLooking = true;
	public static boolean _soundAllowed = true;
	// -------------^^^^^^^^^^^^^PROXY^^^^^^^^^^^^^^^--------------------

	TableView _table;

	private Button _lobbyButton, _chatButton, _logoutButton;

	// move panel
	private Button _callButton;
	private Button _raiseButton;
	private Button _raiseAcceptButton;
	private Button _raisecancle;
	private Button _raiseAllInButton;
	private Button _foldButton;
	private Button _sitInButton;
	private SeekBar mSeekBar;
	private Button _minButton;
	private Button _potButton;
	private Button _maxButton;
	// private SeekBar _seekBar;
	private Button slideHandleButton;
	
	private double _seekBarValue;
	private TextView _mesgText,_seekvalue,_totalamount;
	private EditText _progText;
	public LinearLayout _moveHolder;
	public LinearLayout _sitInHolder;
	public LinearLayout _sliderLayout;
	public LinearLayout _chatHolder;
	private int _amount;
	private NextMove _move;

	private boolean showSeekBar = false;
	private LinkedList _actionQueue;
	private long _startDelay;
	public static DialogChat _cd;
	public static double _totalpot=0;
	public static String _lastmove=null;
	public static int _lastmoveposition;
	private SlidingDrawer slidingDrawer;
	public static TextView _chatText;
	private ScrollView _scrollview;
	private Button _sendButton;
	private EditText _sendText;
	public static TextView _chathead;

	double min;
	double max;
	static ResetAmountRoutine _resetr;
	static Timer _resett;
	ResponseFactory rf;
	Response gr;

	private Handler _chathandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Bundle _bundle = msg.getData();
//			_cd.addChat(_bundle.getString("chatmessage"));
			String message = _bundle.getString("chatmessage");
			int position = message.indexOf(":");
			if(position==-1)
			{
				_chatText.append(Html.fromHtml(message+"<br>"));
			}
			else	
			{
				_chatText.append(Html.fromHtml("<b>"+message.substring(0,position)+": </b>"+message.substring(position+1, message.length())+"<br>"));
				_chathead.setText(Html.fromHtml("<b>"+message.substring(0,position)+": </b>"+message.substring(position+1, message.length())+"<br>"));	
			}
		}
	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		m_ProgressDialog = ProgressDialog.show(ActivityTable.this,
				"Please wait...", "Loading...", true);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.table);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		_cd = new DialogChat(ActivityTable.this, this);
		_cd.show();
		_cd.hide();

		_table = (TableView) findViewById(R.id.table);
		_table.setProxy(this);
		_table.reset();

		initPanel();

		Bundle b = this.getIntent().getExtras();
		_user = (String) b.get("user");
		_pass = (String) b.get("pass");
		_tid = (String) b.get("tid");
		if (b.get("pos") != null)
			_pos = Integer.parseInt((String) b.get("pos"));
		if(b.get("type")!=null)
			_type= b.getInt("type");
		if (b.get("_urw") != null)
			_worth = Double.parseDouble(b.getFloat("_urw")+"");
		Log.i("money",_worth+"");
		if(b.get("uw")!=null)
			_uw=b.getFloat("uw");
		Log.i("UW",_uw+"");
		if(b.get("urw") != null)
			_urw=b.getFloat("urw");
		Log.i("URW",_urw+"");
		if (b.get("minbet") != null)
			_minBet = Double.parseDouble((String) b.get("minbet"));
		Log.w("ActivityTable", "TID " + (String) b.get("tid") + " Userid="
				+ _user + " Pass=" + _pass + "Worth=" + _worth);
		/**
		 * _user = "abhi"; _pass = "abhi"; _tid = "Apollo";
		 **/

		Log.w("ActivityTable", _tid + ": starting...");

		try {
			//_channel = SocketChannel.open(new InetSocketAddress("67.211.101.97",8985));
			_channel = SocketChannel.open(new InetSocketAddress(Command.IP,Command.PORT));
			
			_channel.configureBlocking(true);

			Command ge = new Command("null", Command.C_CONNECT);
			Log.w("Proxy - connect ", "Connect req " + ge.toString());
			write(ge.toString());

			String s = readFull();
			Log.w("Proxy - connect ", "Connect resp " + s);
			Response r = new Response(s);
			_session = r.session();

			_selector = Selector.open();
			keepLooking = true;

			_actionQueue = new LinkedList();
			_channel.configureBlocking(true);
			CommandLogin gel = new CommandLogin(_session, _user, _pass, "",
			"admin");
			Log.w("Proxy - login ", _user + " Login Command " + gel);
			write(gel.toString());
			System.currentTimeMillis();
			rf = new ResponseFactory(readFull());
			gr = (Response) rf.getResponse();
			if (gr.getResult() == 1 || gr.getResult() == 12) {
				if(_type==PokerGameType.Play_Holdem)
				{	
					_worth = ((ResponseLogin) gr).getPlayWorth();
					
					
				}
				else
				{
					_worth=((ResponseLogin) gr).getRealWorth();
				}
				((ResponseLogin) gr).getGender();
				Log.w("Proxy - login ", _user + " Login Response " + gr);
				_channel.configureBlocking(false);
				setSelectorForRead();
				// start thread
				Thread _runner = new Thread(this);
				_runner.start();
				// start the event thread
				startTLThread();
			}
			addObserver(_tid);
			double bringin = 0.00;
			if (_minBet == 0.02 && _worth > 1)
				bringin = _worth;
			else if (_minBet == 0.10 && _worth > 2)
				bringin = _worth;
			else if (_minBet == 0.50 && _worth > 5)
				bringin = _worth;
			
//			if (bringin == 0)
//				;
//			else // if (_pos != -1 && _pos <=10)
				join(_tid, _pos, _worth);

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ActivityTable.this, "Connection failed",
					Toast.LENGTH_SHORT).show();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String ac_str = b.getString("action");
			Action a = new Action(ac_str);
			try
			{
				_table.handle(a);
			}
			catch(Exception e){}
		}
	};
	

	public void run() {
		Log.w("Proxy run", "Starting the run loop");
		ActivityTable p = null;
		SelectionKey key = null;
		while (keepLooking) {
			try {
				_selector.selectNow();
				// Now we deal with our incoming data / completed writes...
				Set keys = _selector.selectedKeys();
				Iterator i = keys.iterator();
				while (i.hasNext()) {
					key = (SelectionKey) i.next();
					i.remove();
					if (key.isReadable()) {
						p = (ActivityTable) key.attachment();
						String response = p.read();
						ResponseFactory rf = new ResponseFactory(response);
						Response r = rf.getResponse();
						//						Log.w("ActivityTable", r.getResult() + " Response = "+ r.toString());
						if (response == null) {
							continue;
						}
						if (r._response_name == Response.R_TABLELIST) {
							throw new IllegalStateException(
							"Table list not expected");
						} else if (r._response_name == Response.R_TABLE_OPEN) {
						} else if (r._response_name == Response.R_TABLE_CLOSED) {
						} else if (r._response_name == Response.R_PLAYER_REMOVED) {
						} else if (r._response_name == Response.R_TABLEDETAIL) {
							GameEvent ge = new GameEvent();
							if (r.getResult() == Response.E_NONEXIST) {
								addAction(new Action(Action.JOIN_FAILED_ACTION,
										ge));
							} else {
								ResponseTableDetail rtd = (ResponseTableDetail) r;
								ge.init(rtd.getGameEvent());
								addAction(new Action(Action.NEW_TABLE_ACTION, ge));
								addAction(new Action(Action.TABLE_DETAIL_ACTION, ge));
								addAction(new Action(Action.PLAYER_DETAIL_ACTION, ge));
								addAction(new Action(Action.COMMUNITY_CARD_ACTION, ge));
								addAction(new Action(Action.ROUND_ACTION, ge));
								addAction(new Action(Action.ROUND_ACTION, ge));
								addAction(new Action(Action.POT_ACTION, ge));
								m_ProgressDialog.cancel();

							}
						} else if (r._response_name == Response.R_MOVE) {
							GameEvent ge = new GameEvent();
							if (r.getResult() == Response.E_BROKE) {
								addAction(new Action(Action.JOIN_FAILED_ACTION,	ge));
							} else if (r.getResult() == Response.E_OVER_SPENDING) {
								addAction(new Action(Action.JOIN_FAILED_ACTION, ge));
							} else {
								ResponseGameEvent rge = (ResponseGameEvent) r;
								ge.init(rge.getGameEvent());
								if (ge.getWinnerString() == null) {
									addAction(new Action(Action.CARD_DEAL_ACTION, ge));
									addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.POCKET_CARD_ACTION, ge));
									// addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.LAST_MOVE_ACTION, ge));
									addAction(new Action(Action.PLAYER_DETAIL_ACTION, ge));
									// addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.POT_ACTION, ge));
									addAction(new Action(Action.COMMUNITY_CARD_ACTION, ge));
									addAction(new Action(Action.ROUND_ACTION, ge));
									// addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.NEXT_MOVE_ACTION, ge));
								} else {
									addAction(new Action(Action.LAST_MOVE_ACTION, ge));
									addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.POT_ACTION, ge));
									addAction(new Action(Action.OPEN_HAND_ACTION, ge));
									addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.WINNER_ACTION, ge));
									addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.NEW_GAME_ACTION, ge));
									addAction(new Action(Action.DELAY_ACTION));
									addAction(new Action(Action.PLAYER_DETAIL_ACTION, ge));
									addAction(new Action(Action.NEXT_MOVE_ACTION, ge));
								}
							}

						} else if (r._response_name == Response.R_MESSAGE) {
							ResponseMessage rchat = (ResponseMessage) r;
							String message = rchat.getMessage();
							
							Bundle chatbundle = new Bundle();
							Message chatmessage = new Message();
							chatbundle.putString("chatmessage",message);
							chatmessage.setData(chatbundle);
							_chathandler.sendMessage(chatmessage);

							addAction(new Action(Action.CHAT_MESSAGE_ACTION, message));

						} else if (r._response_name == Response.R_BUYCHIPS) {
							return;
						} else if (r._response_name == Response.R_GET_CHIPS_INTO_GAME) {
						} else if (r._response_name == Response.R_WAITER) {
						} else if (r._response_name == Response.R_LOGOUT) {
							return;
						} else
							return;
					}
				}
				Message m = new Message();
				Bundle b = new Bundle();
				// Log.w("ActivityTable b4 fetch", "");
				Action a = fetchAction();
				if (a != null) {
					// Log.w("ActivityTable Action", a.toNVPair());
					b.putString("action", a.toNVPair());
					m.setData(b);
					handler.sendMessage(m);
				}
				Thread.currentThread().sleep(200);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Log.w("Exceptional condition, will continue ", ex);
				// Toast.makeText(ActivityTable.this, "Connection failed",
				// Toast.LENGTH_SHORT).show();
			}
		}
	}

	public synchronized void addAction(Action action) {
		_actionQueue.add(action);
	}

	public Action fetchAction() {
		try {
			Action oa = (Action) _actionQueue.getFirst();
			if (oa._id == Action.DELAY_ACTION) {
				if (_startDelay == -1) {
					_startDelay = System.currentTimeMillis();
					// _cat.finest("Start delay ..." + _startDelay);
				}

				else if ((System.currentTimeMillis() - _startDelay) > 1000) {
					_actionQueue.removeFirst(); // discard the delay
					// _cat.finest("...............End delay ..." +
					// System.currentTimeMillis());
					_startDelay = -1;
				}
				return null;
			} else {
				return (Action) _actionQueue.removeFirst();
			}
		} catch (java.util.NoSuchElementException e) {
			// ignore
		}
		return null;
	}

	public void initPanel() {
		_lobbyButton = (Button) findViewById(R.id.tablesettingB);
//		_chatButton = (Button) findViewById(R.id.chatB);
		_logoutButton = (Button) findViewById(R.id.tableexitB);
		_chatHolder = (LinearLayout) findViewById(R.id.chatHolder);
		_moveHolder = (LinearLayout) findViewById(R.id.moveHolder);
		_sitInHolder = (LinearLayout) findViewById(R.id.sitInHolder);
		_callButton = (Button) findViewById(R.id.callB);
		_raiseButton = (Button) findViewById(R.id.raiseB);
		_foldButton = (Button) findViewById(R.id.foldB);
		_sitInButton = (Button) findViewById(R.id.sitInB);
		_mesgText = (TextView) findViewById(R.id.messageText);
		_progText = (EditText) findViewById(R.id.progressText);
		_sliderLayout = (LinearLayout) findViewById(R.id.sliderBar);
		_raiseAcceptButton = (Button) findViewById(R.id.raiseACCEPT);
		_raisecancle = (Button) findViewById(R.id.raisecancle);
		mSeekBar = (SeekBar)findViewById(R.id.betprogress);
		_seekvalue = (TextView)findViewById(R.id.seekvalue);
		_totalamount = (TextView)findViewById(R.id.totalamount);
		_minButton = (Button)findViewById(R.id.minButtton);
		_potButton = (Button)findViewById(R.id.potButtton);
		_maxButton = (Button)findViewById(R.id.maxButtton);
		//	_raiseAllInButton = (Button) findViewById(R.id.raiseALLIN);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);
		slideHandleButton = (Button) findViewById(R.id.slideHandleButton);
		_chathead = (TextView) findViewById(R.id.chatheadTV);
		 _chatText = (TextView) findViewById(R.id.chatChatText);
		 _sendButton = (Button)findViewById(R.id.chatSendB);
		 _sendText = (EditText) findViewById(R.id.chatSendText);
		 _scrollview = (ScrollView) findViewById(R.id.scrollview);
		 _chatText.setMovementMethod(new ScrollingMovementMethod());
//		 _scrollview.

		mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(SoundManager.allin, R.raw.allin);
		mSoundManager.addSound(SoundManager.bonus, R.raw.bonus);
		mSoundManager.addSound(SoundManager.click, R.raw.click);
		mSoundManager.addSound(SoundManager.check, R.raw.check);
		mSoundManager.addSound(SoundManager.chipsin, R.raw.chipsin);
		mSoundManager.addSound(SoundManager.deal, R.raw.deal);
		mSoundManager.addSound(SoundManager.flip, R.raw.flip);
		mSoundManager.addSound(SoundManager.fold, R.raw.fold);
		mSoundManager.addSound(SoundManager.cardsFlop, R.raw.cardsflop);
		mSoundManager.addSound(SoundManager.cardsTurn, R.raw.cardturn);
		mSoundManager.addSound(SoundManager.losinghand, R.raw.losinghand);
		mSoundManager.addSound(SoundManager.winner, R.raw.winninghand);

		mSeekBar.setThumbOffset(10);

		settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		_soundAllowed = settings.getBoolean("sound",false);
		_logoutButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.w("initPanel", "clicked " + _logoutButton.getText());
				try {
					if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
					if (TableView._tmvt != null)
						TableView._tmvt.cancel();
					if (TableView._tmvr != null)
						TableView._tmvr.cancel();
					if (TableView._tat != null)
						TableView._tat.cancel();
					if (TableView._tar != null)
						TableView._tar.cancel();
					if (TableView._dccr != null)
						TableView._dccr.cancel();
					if (TableView._dcct != null)
						TableView._dcct.cancel();
					makeMove("leave", 0, 0, 0);
					removeObserver(_tid);
					//_table.set
					if (_cd != null)
						_cd.cancel();
					_table.leave();
					finish();
					Intent i = new Intent(ActivityTable.this,
							ActivityLogin.class);
					ActivityTable.this.startActivity(i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		_minButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				_seekvalue.setText("$"+String.valueOf(min));
				mSeekBar.setProgress((int)min);
			}
		});

		_potButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				_seekvalue.setText("$"+String.valueOf(_totalpot));
				mSeekBar.setProgress((int)_totalpot);
			}
		});

		_maxButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				_seekvalue.setText("$"+String.valueOf(max));
				mSeekBar.setProgress((int)max);
			}
		});

		_callButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.w("initPanel", "clicked " + _callButton.getText());
				_chatHolder.setVisibility(View.VISIBLE);
				_moveHolder.setVisibility(View.INVISIBLE);
				showSeekBar = false;// do not show seek bar
				_sliderLayout.setVisibility(View.INVISIBLE);
				try {
					Log.i("Sound Allowed",_soundAllowed+"");
					if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
					TableView._tmvt.cancel();
					makeMove(_move._move[0], _move._amount[0],
							_move._amount_range[0], _seekBarValue);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		_raiseButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.w("initPanel", "clicked " + _raiseButton.getText());
				TableView._tmvt.cancel();
				Log.i("Raise Button", _move._amount_range[1] + "--"
						+ _seekBarValue);
				try {
					if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
					if (!_move._sliderRequired) {
						_chatHolder.setVisibility(View.VISIBLE);
						_sliderLayout.setVisibility(View.INVISIBLE);
						//						_progText.setVisibility(View.INVISIBLE);
						_moveHolder.setVisibility(View.INVISIBLE);
						makeMove(_move._move[1], _move._amount[1],
								_move._amount_range[1], _seekBarValue);
					} 
					else {
						_sliderLayout.setVisibility(View.VISIBLE);
						//	_progText.setVisibility(View.VISIBLE);
						min = _move._amount[_move._slider_move];
						max = _move._amount_range[1];
						_totalamount.setText("$"+_move._amount[0]+" to Call");
						mSeekBar.setMax((int) max);
						mSeekBar.setProgress((int)min*2);
						_seekvalue.setText(""+String.valueOf(min*2));
						//						_raiseSBButton.setText("" + min);
						//						_raiseBBButton.setText("" + (2 * min));
						//						_progText.setText("" + min);
						//						_progText.setHeight(25);
						//						_progText.setWidth(100);
						_seekBarValue = min;


					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		_sitInButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					if(_soundAllowed)mSoundManager.playSound(SoundManager.chipsin);
					_sitInHolder.setVisibility(View.INVISIBLE);
					makeMove("sit-in", _move._amount[1],
							_move._amount_range[1], Double.parseDouble(_progText.getText().toString()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});


		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

				_seekvalue.setText("$"+String.valueOf(arg1));
			}
		});
		_raisecancle.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
				//					_moveHolder.setVisibility(View.INVISIBLE);
				_sliderLayout.setVisibility(View.INVISIBLE);
				//					if(_seekBarValue + 2 * min < max)_seekBarValue += 2 * min;
				//					_progText.setText("" + Utils.getRounded(_seekBarValue));
			}
		});
		_raiseAcceptButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//Log.i("Raise Accept",_progText.getText().toString()+"--"+_move._amount_range[1]+"--"+_move._amount[1]);
				/*double amt = Double.parseDouble((_seekvalue.getText().subSequence(1, _seekvalue.length())).toString());
				if(amt<=0 || amt > _move._amount_range[1])
				{
					//_progText.setText("Wrng Amt");
					_resetr = new ResetAmountRoutine();
					_resett = new Timer();
					_resett.schedule(_resetr, 1000);
					return;
				}*/
				_chatHolder.setVisibility(View.VISIBLE);
				_moveHolder.setVisibility(View.INVISIBLE);
				_sliderLayout.setVisibility(View.INVISIBLE);
				try {
					if(_soundAllowed)mSoundManager.playSound(SoundManager.chipsin);
					TableView._tmvt.cancel();
					makeMove(_move._move[1], _move._amount[1],
							_move._amount_range[1], Double.parseDouble((_seekvalue.getText().subSequence(1, _seekvalue.length())).toString()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		/*_raiseBBButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(_seekBarValue + 2 * min < max)_seekBarValue += 2 * min;
				_progText.setText("" + Utils.getRounded(_seekBarValue));
			}
		});

		_raiseAllInButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				_seekBarValue = _move._amount_range[1];
				_progText.setText("" + Utils.getRounded(_seekBarValue));
			}
		});

		 */

		_foldButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.w("initPanel", "clicked " + _foldButton.getText());
				_chatHolder.setVisibility(View.VISIBLE);
				_moveHolder.setVisibility(View.INVISIBLE);
				_sliderLayout.setVisibility(View.INVISIBLE);
				try {
					if(_soundAllowed)mSoundManager.playSound(SoundManager.fold);
					TableView._tmvt.cancel();
					Log.i("Fold Command",""+_move._move[_move._move.length - 1]+"="+_move._amount[_move._move.length - 1]+"="+_move._amount_range[_move._move.length - 1]+"="+_seekBarValue);
					makeMove(_move._move[_move._move.length - 1],
							_move._amount[_move._move.length - 1],
							_move._amount_range[_move._move.length - 1],
							_seekBarValue);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		_lobbyButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.w("initPanel", "Lobby button clicked ");
				try {
					if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
					removeObserver(_tid);
					Intent i = new Intent(ActivityTable.this,
							ActivityMainLobby.class);

					Bundle b = new Bundle();
					b.putString("status", "Status: Return to lobby");
					b.putString("user", _user);
					b.putString("pass", _pass);
					if(_type==PokerGameType.Play_Holdem)
					{
						b.putFloat("uw",(float) ((ResponseLogin) gr).getPlayWorth());
						b.putFloat("urw", (float) ((ResponseLogin) gr).getRealWorth());
					}
					else
						if(_type== PokerGameType.Real_Holdem)
						{	
							b.putFloat("uw", (float) ((ResponseLogin) gr).getPlayWorth());
							b.putFloat("urw",(float) ((ResponseLogin) gr).getRealWorth());
							Log.i("worth",_worth+"");
						}
					i.putExtras(b);
					if (TableView._tmvt != null)
						TableView._tmvt.cancel();
					if (TableView._tmvr != null)
						TableView._tmvr.cancel();
					if (TableView._tat != null)
						TableView._tat.cancel();
					if (TableView._tar != null)
						TableView._tar.cancel();
					if (TableView._dccr != null)
						TableView._dccr.cancel();
					if (TableView._dcct != null)
						TableView._dcct.cancel();
					if (_cd != null)
						_cd.cancel();
					_table.leave();
					finish();
					// We use SUB_ACTIVITY_REQUEST_CODE as an 'identifier'
					ActivityTable.this.startActivity(i);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		/*_chatButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.w("initPanel", "Chat button clicked ");
				if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
				_cd.show();
				_cd.position();
			}
		});*/
		
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				slideHandleButton.setBackgroundResource(R.drawable.closechat_bg);
				_chathead.setText("");
				_chathead.setVisibility(View.INVISIBLE);
			}
		});

		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				slideHandleButton.setBackgroundResource(R.drawable.openchat_bg);
				_chathead.setText("");
				_chathead.setVisibility(View.VISIBLE);
			}
		});

   	 _sendButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
           	 String cm = _sendText.getText().toString();
           	 if(cm.length()>0)
           	 {
//       		_chatText.append(_user + ": " + cm+"\n");
       		sendChat(Base64.encodeString(Html.fromHtml("<b>"+_user+": </b>") +cm));
       		_sendText.setText("");
           	 }
            }
        });
	}

	class ResetAmountRoutine extends TimerTask
	{
		ResetAmountRoutine()
		{

		}
		@Override
		public void run() {
			_seekBarValue = 2 * min;
			_seekvalue.setText(_seekBarValue+"");
		}
	}
	public void setMoves(NextMove nm, String mesg) {
		if (mesg != null) {
			_mesgText.setText(mesg);
		}
		_move = nm;
		if((_move._move[0].contains("big-blind")&& ActivitySettings.autoblind)||(_move._move[0].contains("small-blind")&& ActivitySettings.autoblind))
		{
			//showSeekBar = false;// do not show seek bar
			try {
				Log.i("Sound Allowed",_soundAllowed+"");
				if(_soundAllowed)mSoundManager.playSound(SoundManager.click);
				//				TableView._tmvt.cancel();
				makeMove(_move._move[0], _move._amount[0],
						_move._amount_range[0], _seekBarValue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				_chatHolder.setVisibility(View.INVISIBLE);
				_moveHolder.setVisibility(View.VISIBLE);
				// set text on buttons
				for (int i = 0; i < _move._move.length; i++)
					Log.i("Move Strings", _move._move[i].toString());
				_callButton.setText(moveText(_move._move[0]));
				if (_move._move.length >= 2) {
					_raiseButton.setText(moveText(_move._move[1]));
				}
				if (_move._move.length >= 3) {
					_foldButton.setText(moveText(_move._move[_move._move.length - 1]));
					_foldButton.setVisibility(View.VISIBLE);
				} else {
					_foldButton.setVisibility(View.INVISIBLE);
				}
			}catch(Exception e)
			{}
		}
	}
	public void clearMoves() {
		Log.i("Move clear b4 called", _moveHolder.getVisibility() + "");
		showSeekBar = false;
		_sliderLayout.setVisibility(View.INVISIBLE);
		_moveHolder.setVisibility(View.INVISIBLE);
		Log.i("Move clear called", _moveHolder.getVisibility() + "");
	}

	// next-move=5|small-blind|0.50`5|opt-out|0.00
	//
	public void makeMove(String move, double val, double rlv, double sv)
	throws Exception {
		double value = rlv == -1 ? val : sv;

		int mvid = -1;
		if (move.equals("opt-out")) {
			mvid = Command.M_OPT_OUT;
		} else if (move.equals("sit-in")) {
			mvid = Command.M_SIT_IN;
		} else if (move.equals("small-blind")) {
			mvid = Command.M_SMALLBLIND;
		} else if (move.equals("big-blind")) {
			mvid = Command.M_BIGBLIND;
		} else if (move.equals("check")) {
			mvid = Command.M_CHECK;
		} else if (move.equals("bet")) {
			mvid = Command.M_BET;
		} else if (move.equals("call")) {
			mvid = Command.M_CALL;
		} else if (move.equals("raise")) {
			mvid = Command.M_RAISE;
		} else if (move.equals("all-in")) {
			mvid = Command.M_ALL_IN;
		} else if (move.equals("fold")) {
			mvid = Command.M_FOLD;
		} else if (move.equals("leave")) {
			mvid = Command.M_LEAVE;
		} else {
			Log.w("ActivityTable makeMove", "Unknown move " + move);
			mvid = Command.M_FOLD;
		}
		CommandMove ge = new CommandMove(_session, mvid, value, _tid);
		Log.w(_user, " Move Command " + ge);
		write(ge.toString());
		//		if(mvid == Command.M_OPT_OUT)
		//		_sitInHolder.setVisibility(View.VISIBLE);
	}

	public String moveText(String move) {
		String mv = move;
		if (move.equals("opt-out")) {
			mv = "O-Out";
		} else if (move.equals("sit-in")) {
			mv = "Sit-In";
		} else if (move.equals("small-blind")) {
			mv = "SB";
		} else if (move.equals("big-blind")) {
			mv = "BB";
		} else if (move.equals("check")) {
			mv = "Check";
		} else if (move.equals("bet")) {
			mv = "Bet";
		} else if (move.equals("call")) {
			mv = "Call";
		} else if (move.equals("raise")) {
			mv = "Raise";
		} else if (move.equals("fold")) {
			mv = "Fold";
		} else if (move.equals("all-in")) {
			mv = "All In";
		} else if (move.equals("leave")) {
			mv = "Leave";
		}
		return mv;
	}

	public void sendChat(String chat) {
		CommandMessage ge = new CommandMessage(_session, chat, _tid);
		Log.w(_user, " Message Command " + ge);
		write(ge.toString());
	}
	public void addChat(String s){
    	if (_chatText == null )return;
//    	if(s.contains(_proxy._user+""))return;//to avoid printing the same message sent by user
//    	_chatText.append(s+"\n");
    	Bundle chatbundle = new Bundle();
		Message chatmessage = new Message();
		chatbundle.putString("chatmessage",s);
		chatmessage.setData(chatbundle);
		_chathandler.sendMessage(chatmessage);
    	
    }

	/********************************************************************************
	 * Invoked when the Activity loses user focus.
	 *****************************************************************************/
	@Override
	protected void onResume()
	{
		super.onResume();

		//		super.onResume();
		//		if(call)
		//		{
		//			try
		//			{
		//				_table.handle((Action) _actionQueue.getLast());
		//			}
		//			catch(Exception e){}
		//			Canvas canvas = _table._holder.lockCanvas(null);
		//		_table.doDraw(canvas);
		//		_table._holder.unlockCanvasAndPost(canvas);
		//		}

		Log.i("resume","onresume");

		//		//TableView.repaint();
		//		Message m = new Message();
		//		Bundle b = new Bundle();
		//		// Log.w("ActivityTable b4 fetch", "");
		//		Action a = fetchActionLast();
		//		if (a != null) {
		//			// Log.w("ActivityTable Action", a.toNVPair());
		//			b.putString("action", a.toNVPair());
		//			m.setData(b);
		//			handler.sendMessage(m);
		//		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		//		call = true;
		//		try
		//		{
		//		Cursor c = getContentResolver().query(
		//				android.provider.CallLog.Calls.CONTENT_FILTER_URI,
		//				null, null, null, 
		//				android.provider.CallLog.Calls.DURATION + " DESC");
		//		startManagingCursor(c);
		//		int DuarationColumn = c.getColumnIndex(
		//				android.provider.CallLog.Calls.DURATION);
		//		// Will hold the calls, available to the cursor
		//		ArrayList<String> callList = new ArrayList<String>();
		//		
		//		// Loop through all entries the cursor provides to us.
		//		if(c.isFirst()){
		//			do{
		//				int callType = c.getInt(DuarationColumn);
		//				Log.i("duaration",callType+"");
		//				
		//			}while(c.moveToNext());
		//			
		//			}
		//		
		//		}
		//		catch(Exception e){}
		Log.i("pause","OnPause");
		//		_table.leave();
		//		_table.reset();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("stop","OnStop");
		//		_table.leave();
		//		_table.reset();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("destroy","OnDestroy");
		_table.leave();
		_table.reset();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// just have the View's thread save its state into our Bundle
		Log.w("SaveInstanceState","");
		super.onSaveInstanceState(outState);
		keepLooking = true;
	}

	/********************************************************************************
	 * Requests to the server
	 *****************************************************************************/

	public Response config() throws Exception {
		_channel.configureBlocking(true);
		Command ge = new Command(_session, Command.C_CONFIG);
		Log.w(_user, " Config Command " + ge);
		write(ge.toString());
		ResponseFactory rf = new ResponseFactory(readFull());
		ResponseConfig gr = (ResponseConfig) rf.getResponse();
		Log.w(_user, " Config Response " + gr);
		return gr;
	}

	public Response register() throws Exception {
		_channel.configureBlocking(true);
		CommandRegister ge = new CommandRegister(_session, _user, _pass, _user
				+ "@test.com", (byte) 0, "", "");
		Log.w(_user, " Register Command " + ge);
		write(ge.toString());
		Response gr = new Response(readFull());
		Log.w(_user, " Register Response " + gr);
		return gr;
	}

	public void addObserver(String tid) throws Exception {
		CommandTableDetail ge = new CommandTableDetail(_session, tid);
		Log.w(_user, " Add Observer Command " + ge);
		write(ge.toString());
	}

	public void removeObserver(String tid) throws Exception {
		CommandMove ge = new CommandMove(_session, Command.M_LEAVE, 0, tid);
		Log.w(_user, " Leave Command " + ge);
		write(ge.toString());
		CommandString ges = new CommandString(_session, Command.C_TURN_DEAF,
				tid);
		Log.w(_user, " Remove Observer Command " + ges);
		write(ges.toString());
	}

	public void join(String tid, int pos, double value) throws Exception {
		CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, value, tid);
		ge.setPlayerPosition(pos);
		Log.w(_user, " Join Command " + ge);
		write(ge.toString());
	}

	public void buyChips(double playchips, double realChips) {
		CommandBuyChips ge;
		ge = new CommandBuyChips(_session, playchips, realChips);
		// _requested_chips = value;
		Log.w(_user, " Buy Chips  Command " + ge);
		write(ge.toString());
	}

	public Response getMoneyIntoGame(String tid, double value) {
		CommandGetChipsIntoGame ge;
		ge = new CommandGetChipsIntoGame(_session, tid, value);
		Log.w(_user, " Get Chips  Command " + ge);
		write(ge.toString());
		Response gr = new Response(readFull());
		Log.w(_user, " Get Chips Response " + gr);
		return gr;
	}

	public void updateClientSettingsAtServer(int settings) {
		CommandInt ci = new CommandInt(_session, Command.C_PREFERENCES,
				settings);
		Log.w(_user, PlayerPreferences.stringValue(settings));
		write(ci.toString());
	}

	public void setSelectorForRead() throws Exception {
		_channel.configureBlocking(false);
		_channel.register(_selector, SelectionKey.OP_READ, this);
	}

	public ResponseTableList getTableList(int type) throws Exception {
		CommandTableList ge = new CommandTableList(_session, type);
		System.currentTimeMillis();
		write(ge.toString());
		ResponseFactory rf = new ResponseFactory(readFull());
		ResponseTableList gr = (ResponseTableList) rf.getResponse();
		Log.w("Proxy ", gr.toString());
		return gr;
	}

	public void heartBeat() throws Exception {
		if (_session != null) {
			Command ge = new Command(_session, Command.C_HTBT);
			// _cat.finest("HTBT" + ge.toString());
			write(ge.toString());
		}
	}

	/********************************************************************************
	 * Repetitive tasks
	 *****************************************************************************/

	public void startTLThread() {
		// start a heartbeat timer thread
		TLThread hb = new TLThread();
		Timer t = new Timer();
		t.schedule(hb, 0, HTBT_INTERVAL);
	}

	public class TLThread extends TimerTask {

		public void run() {
			try {
				heartBeat();
			} catch (Exception ex) {
				// do nothing
				Log.w("Timertsk", "Exception" + ex);
			}
		}
	} // end HeartBeat class

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
			// log.fatal(_name + " Partial header read " + r);
			if (r == -1) {
				_dead = true;
				Log.w(_user,
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
			} catch (Exception e) {
				// ignore
			}
		}
		return s;
	}

	public synchronized String read() {
		try {
			if (_dead) {
				return null;
			}
			System.currentTimeMillis();
			if (readHeader() && readBody()) {
				return _comstr;
			}
		} catch (IOException e) {
			Log.w(_user, " Marking client as dead ");
			_dead = true;
			return null;
		} catch (Exception e) {
			Log.w(_user, " Garbled command ");
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
		} catch (IOException e) {
			_dead = true;

			Log.w(_user," Marking client as dead because of IOException during write");

			return false;
		}
		return true;
	}

}
