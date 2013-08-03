package bap.texas;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import bap.texas.action.Action;
import bap.texas.common.message.GameEvent;
import bap.texas.util.AnimTimer;
import bap.texas.util.Card;
import bap.texas.util.CommCards;
import bap.texas.util.DChip;
import bap.texas.util.DealCards;
import bap.texas.util.LastMove;
import bap.texas.util.NextMove;
import bap.texas.util.Pot;
import bap.texas.util.SoundManager;

public class TableView extends SurfaceView implements SurfaceHolder.Callback {

	private int mCanvasHeight = 1;
	private int mCanvasWidth = 1;
	SurfaceHolder _holder;
	Context _context;
	private Bitmap mBackgroundImage;
	private ClientPokerModel _model;
	private ClientPlayerModel _players[];
	private int[] _vacant_positions;
	public static ClientPlayerModel _me;
	public boolean _join_sent=false;
	
	private static RoomSkin _roomskin;
	int _dealer_pos = -1;
    public String _round;
    public Card[] _my_hand;
    public DealCards _dealCards;
	NextMove _next_move, _pnext_move;
	LastMove _last_move;
	Drawable _bubble_icon, _dbut_icon, _empty_seat_icon, _chatbg;
	StringBuilder _win_mesg;
	String _chat_mesg;
	boolean _chat_mesg_disable = false;
	
	static TimerAnimationRoutine _tar;
	static Timer _tat;

	static TimerMoveRoutine _tmvr;
	static Timer _tmvt;
	
	static WinnerAnimationRoutine _winr;
	static Timer _wint;

	static DealCommCardRoutine _dccr;
	static Timer _dcct;
	
	static DealCardRoutine _dcr;
	static Timer _dct;
	
	static ChatAnimationRoutine _chatr;
	static Timer _chatt;
	
	ActivityTable _proxy;
	
	// for maintaining state
	int _rnd=-1;
	
	Handler _tableviewhandler = new Handler(){
		@Override
		public void handleMessage(Message msg){ //public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				_tat.schedule(_tar, 3, 1900);
				break;
				
			}
		}
	};
	
	public TableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		// register our interest in hearing about changes to our surface
		_holder = getHolder();
		_holder.addCallback(this);
		Resources res = context.getResources();
	//	if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.enteringgame);
		mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.table_bg);
		_bubble_icon = res.getDrawable(R.drawable.seat_name_plate_action_yellow);
    	_dbut_icon = res.getDrawable(R.drawable.dealer);
    	_empty_seat_icon = res.getDrawable(R.drawable.sit);
    	_chatbg = res.getDrawable(R.drawable.chatbg);
    	
		this.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mouseClick(v, event);
				return true;
			}
		    });
		
		setFocusable(true);
	}
	
	public void setProxy(ActivityTable p){
		_proxy = p;
		
	}
	
	
	public void reset(){
		_model = null;
		_players = null;
		_vacant_positions = null;
		_me = null;
		_join_sent=false;
		_roomskin = null;
		_dealer_pos = -1;
	    _round = null;
		_next_move = _pnext_move = null;
		_last_move = null;
		_win_mesg = null;
		_chat_mesg = null;
	}
	
	public void leave(){
		if (_join_sent){
			try {
				_proxy.removeObserver(_model._tid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_join_sent = false;
		}
		reset();
		if(_tmvt != null)_tmvt.cancel();
		if(_tmvr != null)_tmvr.cancel();
		if(_tat != null)_tat.cancel();
		if(_tar != null)_tar.cancel();
		if(_dccr != null)_dccr.cancel();
		if(_dcct != null)_dcct.cancel();
	}
	
	
	public void handle(Action a){
		 GameEvent ge = a.getGameEvent();
		 Log.i("Game Event",a.toNVPair()+"");
		 //Log.w(a._id + "Tableview handle ", a._data);
		 switch (a.getId()){
		 	case Action.NEW_TABLE_ACTION:	
		 		_roomskin = RoomSkinFactory.getRoomSkin(ge.getMaxPlayers());
				_model = new ClientPokerModel(_roomskin, _context, _proxy);
				_model._minBet = ge.getMinBet(); //,min-bet=1,max-bet=2,max-players=6,min-players=2,
				_model._maxBet = ge.getMaxBet();
				_model._minPlayer = ge.getMinPlayers();
				_model._maxPlayer = ge.getMaxPlayers();
				_model._grid = ge.getGameRunId();//grid=9035001,name=Apollo,type=1
				_model._tid = ge.getGameName();
				_dealer_pos = ge.getDealerPosition();
				break;
		 	case Action.NEW_GAME_ACTION:
		 		ActivityTable._lastmove="";
		 		ActivityTable._lastmoveposition=-1;
		 		// clear table
		 		ActivityTable._lastmove="";
				_model._grid = ge.getGameRunId();//grid=9035001,name=Apollo,type=1
				_model._pot = null;
				_model._communityCards = null;
				_dealer_pos = ge.getDealerPosition();
				_win_mesg = null;
				_my_hand = null;
				_proxy.addChat("dealer: New hand " + _model._grid);
				ActivityTable._totalpot=0;
		 		break;
			case Action.ROUND_ACTION:
		 		String rnds = ge.get("round");
		 		if (rnds != null){
		 			int rnd =  Integer.parseInt(rnds);
		 			if (rnd==_rnd)break;
		 			_rnd = rnd;
		 			switch(rnd){
		 				case 0:
		 			 		_round = "PRE-FLOP";
		 					break;
		 				case 1:
		 			 		_round = "FLOP";
		 			 		_proxy.addChat("dealer: FLOP Pot=$" + _model._pot[0]._value);
		 					break;
		 				case 2:
		 			 		_round = "TURN";
		 			 		_proxy.addChat("dealer: TURN Pot=$" + _model._pot[0]._value);
		 					break;
		 				case 3:
		 			 		_round = "RIVER";
		 			 		_proxy.addChat("dealer: RIVER Pot=$" + _model._pot[0]._value);
		 					break;
		 				case 4:
		 			 		_round = "----";
		 					break;
		 			}
		 		}
		 		repaint();
		 		break;
		 	case Action.COMMUNITY_CARD_ACTION:	
		 		//,community-cards=KC'JD'QH,round=1
	 			Card ccv[] = ge.getCommunityCards();
	 			if (ccv == null)break;
	 			if (_model._communityCards == null){
	 				if (ccv.length == 3){
		 				// flop
		 				_model._communityCards = new CommCards(ccv, _roomskin, _context, _proxy);
		 				_dccr = new DealCommCardRoutine(0);
	 					_dcct = new Timer();
	 					_dcct.schedule(_dccr, 1, 50);
	 					if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.cardsFlop);
		 			}
		 			else if (ccv.length == 4){ // game start in between game
		 				_model._communityCards = new CommCards(ccv, _roomskin, _context, _proxy);
		 				_model._communityCards.addTurnCard(ccv[3]);
		 				_model._communityCards._sf = 8;
		 				_model._communityCards._st = 8;
		 				if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.cardsTurn);
		 			}
		 			else if (ccv.length == 5){ // game start in between game
		 				_model._communityCards = new CommCards(ccv, _roomskin, _context, _proxy);
		 				_model._communityCards.addTurnCard(ccv[3]);
		 				_model._communityCards.addRiverCard(ccv[4]);
		 				_model._communityCards._sf = 8;
		 				_model._communityCards._st = 8;
		 				_model._communityCards._sr = 8;
		 				if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.cardsTurn);
		 			}
		 		}
		 		else {
		 			if (_model._communityCards._length == 3 && ccv.length == 4){
		 				// turn
		 				_model._communityCards.addTurnCard(ccv[3]);
		 				_dccr = new DealCommCardRoutine(1);
	 					_dcct = new Timer();
	 					_dcct.schedule(_dccr, 1, 50);
	 					if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.cardsTurn);
		 			}
		 			else if (_model._communityCards._length == 4 && ccv.length == 5){
		 				//river
		 				_model._communityCards.addRiverCard(ccv[4]);
		 				_dccr = new DealCommCardRoutine(2);
	 					_dcct = new Timer();
	 					_dcct.schedule(_dccr, 1, 50);
	 					if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.cardsTurn);
		 			}
		 		}
	 			_proxy.addChat("dealer: Dealing " + ge.get("community-cards"));
		 		break;
		 	case Action.DELAY_ACTION:
		 		break;
		 	case Action.LAST_MOVE_ACTION:
		 		if (_tat != null){
					_tar.clear();
					_tat.cancel();
				}
		 		_next_move = null;
		 		String lm = ge.getLastMoveString();
		 		if (lm != null && !lm.contains("none")){
		 			_last_move = new LastMove(lm);
		 			_proxy.addChat("dealer: Player " + _last_move._name + " " + _last_move._move + "  $" + _last_move._amount);
					if(!_last_move._move.equals("join"))
					ActivityTable._totalpot = getTotalBetAction(ge);//ActivityTable._totalpot+_last_move._amount;
					ActivityTable._lastmove=_last_move._move;
					ActivityTable._lastmoveposition=_last_move._pos;
					
		 		}
		 		else {
		 			_last_move = null;
		 		}
		 		break;
		 	case Action.NEXT_MOVE_ACTION:
		 		String nm = ge.getNextMoveString();
		 		if (nm != null)
		 		{
		 			_next_move = new NextMove(nm);
		 			Point rel_nm_pos = new Point();
		 			//if(_next_move._pos != 0)
		 			rel_nm_pos = _roomskin.getPlayerCoordinates(_roomskin.getSP(_next_move._pos));// _roomskin.getSP(_next_move._pos));
		 			///ActivityTable.playerposition = _next_move._pos;
//		 			ActivityTable.next_move=_next_move._move[0];
		 			if(_next_move._move[0].contains("call")||
		 					_next_move._move[0].contains("Check")||
		 					_next_move._move[0].contains("big-blind")||
		 					_next_move._move[0].contains("small-blind"))
		 			ActivityTable.next_move=true;
		 			/*for(int i=0;i<_next_move._move.length;i++)
		 			{
		 				ActivityTable.next_move += _next_move._move[i];
		 			}*/
//		 			Log.e("nextmove string",ActivityTable.next_move);
		 			if (_pnext_move == null || !_pnext_move.equals(_next_move) || _pnext_move.equals(_next_move)){
		 				_pnext_move = _next_move;
//		 				if(!nm.contains("wait") && !nm.contains("none") && !nm.contains("opt"))
//		 				{
		 				_tar = new TimerAnimationRoutine(rel_nm_pos.x + 9, rel_nm_pos.y + 7);
	 					_tat = new Timer();
//	 					_tat.schedule(_tar, 3, 1900);
	 					_tableviewhandler.sendEmptyMessage(1);
//		 				}
		 			}
		 		}
		 		else 
		 		{
		 			_next_move = null;
		 		}
		 		// show next moves if they are for me
		 		if (_me != null)
		 			Log.w("Table View ", "My pos = " + _me._pos + " Mv pos=" + _next_move._pos);
		 		if (_me != null && _next_move._pos == _me._pos) 
		 		{
		 				// show moves
		 			if((_next_move._move[0].contains("big-blind")&& ActivitySettings.autoblind)||(_next_move._move[0].contains("small-blind")&& ActivitySettings.autoblind))
		 				_proxy.setMoves(_next_move, null); //when bb or small blind that time timer not need to run for autoblinde
		 			else
		 			{
		 				//when bigblind or smallblinde that time timer will runs 
		 				_proxy.setMoves(_next_move, null);
		 				// start a timer thread
		 				_tmvr = new TimerMoveRoutine(_next_move);
		 				_tmvt = new Timer();
		 				_tmvt.schedule(_tmvr, 30000);
		 			}
		 		}
		 		repaint();
		 		break;
		 	case Action.POCKET_CARD_ACTION:
		 		Card[] my = ge.getHand();
		 		if (my!= null && my.length == 2 && _my_hand == null){
			 		_my_hand = ge.getHand();
			 		
			 		String[][] plrs = ge.getPlayerDetails();
					if (plrs != null){
						_players = new ClientPlayerModel[plrs.length];
						
						for (int i=0;i<plrs.length;i++){
							_players[i] = new ClientPlayerModel(_roomskin, _context, _proxy, new LastMove(ge.getLastMoveString()), new NextMove(ge.getNextMoveString()), plrs[i]);
							//Log.w(_players[i]._name, _proxy._name);
							if (_players[i]._name.equals(_proxy._user)){
								_me = _players[i];
								_me._hand = _my_hand;
								_me._me = true;
							}
						}
					}	
			 		for (int i=0;i<_players.length;i++){
			 			ClientPlayerModel cpm  = _players[i];
			 			if (cpm._hand != null && cpm._hand.length==2){
			 				_players[i].setDealPocketCards();
			 			}
			 		}
		 		}
		 		break;
		 	case Action.PLAYER_DETAIL_ACTION:
				 //Log.w(a._id + "Tableview handle ", a._data);
		 		// see if I am sitting
				int tp = ge.getTargetPosition();
				Card[] my_hand = ge.getHand();
				//player-details=0|96.00|2.00|lewisVus|16777217|0|__'__`1|91.00|3.00|shainaVus|2050|0|__'__`2|93.00|3.00|barbieVus|2052|0|__'__
				//`3|99.00|0.00|spidermanVus|2308|0|`4|93.00|3.00|amyVus|2052|0|__'__
				
				// for BSP : 0|10582.90|0.00|nagaraju|2052|0|0|null|__'__`2|474.20|0.00|superpoker|16779271|1|0|5|__'__
				// values:position|worth|bet|name|status|gender|rank|avatar|card1'card2`
				String[][] plrs = ge.getPlayerDetails();
				
				/*for(int k=0;k<plrs.length;k++)
					for(int l=0;l<plrs[k].length;l++)
						Log.e("player details last",""+plrs[k][l]);*/
				if(_model == null)break;
				_vacant_positions = new int[_model._maxPlayer];
				if (plrs != null){
					_players = new ClientPlayerModel[plrs.length];
					for (int i=0;i<plrs.length;i++){
						_players[i] = new ClientPlayerModel(_roomskin, _context, _proxy, new LastMove(ge.getLastMoveString()), new NextMove(ge.getNextMoveString()), plrs[i]);
						_vacant_positions[_players[i]._pos] = 1;
//						Log.w(_players[i]._name, _proxy._user);
						if (_players[i]._name.equals(_proxy._user)){
							_me = _players[i];
							_me._hand = my_hand;
							_me._me = true; 
						}
					}
				}	
				_dealer_pos = ge.getDealerPosition();
				_win_mesg = null;			
		 		break;
		 	case Action.POT_ACTION:
		 		// set pots
				String pots[][] = ge.getPot();//,pots=main|17.00
				if (pots != null && pots.length > 0){
					_model._pot = new Pot[pots.length];
					for (int i=0;i<pots.length;i++){
						//Log.w("TableView init" , i + " Pots length=" + pots.length );
						_model._pot[i] = new Pot(_roomskin, _context, _proxy);
						_model._pot[i]._name = pots[i][0];
						_model._pot[i]._value = Double.parseDouble(pots[i][1]);
						_model._pot[i]._x = RoomSkin._potx + i*60;
						_model._pot[i]._y = RoomSkin._poty;
					}
				}
				else {
					_model._pot = null;
				}
		 		break;
		 	case Action.TABLE_DETAIL_ACTION:
		 		
		 		break;
		 	case Action.OPEN_HAND_ACTION:
		 		//pots=main|26.00,prev_community-cards=5H'9S'TS'6D'QC,
		 		//open-hands=3|TH'QS,target-position=3,
		 		String ohstr[][] = ge.getOpenHands();
		 		if (ohstr == null) break;
		 		for (int i=0;i<ohstr.length;i++){
		 			int pos = Integer.parseInt(ohstr[i][0]);
		 			Card[] cv = ClientPlayerModel.getHand(ohstr[i][1]);
		 			for (int j=0;j<_players.length;j++){
			 			if (_players[j]._pos == pos){
			 				_players[j]._hand = cv;
			 			}
			 		}
		 		}
		 		break;
		 	case Action.WINNER_ACTION:
		 		//pots=main|26.00,prev_community-cards=5H'9S'TS'6D'QC,
		 		//winner=main|3|abhi|26.00|TH'QS|QC'TH'9S'TS'QS| two pairs: Queens and tens: with a nine kicker,open-hands=3|TH'QS,target-position=3,
		 		String ws[][] = ge.getWinner();
		 		_win_mesg = new StringBuilder();
		 		for (int i=0;i<ws.length;i++){
		 			_win_mesg.append(ws[i][2]).append(" wins $").append(ws[i][3]);
		 			if(ws[i][6] != null)
		 				if(!ws[i][6].contains("-na-"))_win_mesg.append(" with ").append(ws[i][6]).append(" ");
		 				else _win_mesg.append(" by opposite player folding");
		 			int pos = Integer.parseInt(ws[i][1]);
		 			_proxy.addChat("dealer: " + ws[i][2]+ " wins $"+ ws[i][3]+ " with "+ws[i][6]);
				 	ClientPlayerModel _winner=null;
				 	for (int k=0;k<_players.length;k++){
				 		if (_players[k]._pos == pos){
				 			_winner = _players[k];
				 			_winner.setWinner();
				 			break;
				 		}
				 	}
				 	if (_winner != null && _model._pot != null){
					 	_winr = new WinnerAnimationRoutine(_model._pot[0], _winner);
	 					_wint = new Timer();
	 					_wint.schedule(_winr, i*1000, 100);
	 					
	 					if(ActivityLogin.usernameText.equals(_winner._name)&& _winner.getWinner()){
	 						if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.winner);
	 					}else{
	 						if(ActivityTable._soundAllowed)ActivityTable.mSoundManager.playSound(SoundManager.losinghand);
	 					}	 			        
				 	}
				 	else {
				 		Log.w("Winner Action", "No winner at " + pos);
				 	}
		 		}
		 		break;
		 	case Action.CLEAR_MOVES_ACTION:
		 		_proxy.clearMoves();
		 		break;
		 	case Action.CHAT_MESSAGE_ACTION:	
		 		if (a.getData() != null){
		 			_chat_mesg = a.getData();
		 			Log.i("CHAT MESSAGE ACTION: ",_chat_mesg);
			 	}   
		 		repaint();
		 		break;
		 	case Action.CARD_DEAL_ACTION:

//		 		try
//		 		{
//			 		for (int i=0;i<_players.length;i++){
//			 			Point p =  _roomskin.getPlayerCoordinates(_players[i]._pos);
//			 			TranslateAnimation trans = new TranslateAnimation(0, 522, 0, 500);
//			 			trans.setStartOffset(0);
//			 			trans.setDuration(200);
//			 			trans.setFillAfter(true);
//			 		
//			 			ImageView img = (ImageView)findViewById(R.id.closeCard);
//			 			img.setVisibility(View.VISIBLE);
//			 			img.startAnimation(trans);
//			 			_proxy.addAction(new Action(Action.DELAY_ACTION));
//			 			_proxy.addAction(new Action(Action.DELAY_ACTION));
//			 		}
//		 		}
//		 		catch(Exception e)
//		 		{
//		 			e.printStackTrace();
//		 		}
//		 		_dealCards = new DealCards(_roomskin, _context, _proxy);
//		 		_dcr = new DealCardRoutine(_players);
//				_dct = new Timer();
//				_dct.schedule(_dcr, _players.length*2, 50);
		 		break;
		 	case Action.JOIN_FAILED_ACTION:
		 		if(_me!=null)
		 		_chat_mesg = _me._name+": Join failed - insufficient money";
		 		break;
		 	case Action.NONEXISTENT_GAME_ACTION:
		 		_win_mesg = new StringBuilder();
		 		_win_mesg.append("Game does not exist on the server !");
		 		break;
		 	default:
		 		Log.w("TableView handle", "UNKNOWN ACTION " + a);
		 }
		 //repaint();
	}


	
	
	 public void mouseClick(View v, MotionEvent event){
		 if (_me == null && _vacant_positions != null){
				for (int i=0;i<_vacant_positions.length;i++){
					if (_vacant_positions[i] ==0){
						Point p =  _roomskin.getPlayerCoordinates(_roomskin.getSP(i));
						if(i == 7)p.x = p.x + 30;
						Rect r = new Rect(p.x, p.y, p.x + RoomSkin.SEAT_WIDTH, p.y + RoomSkin.SEAT_HEIGHT);
						if (r.contains((int)event.getX(), (int)event.getY()) && !_join_sent){
							try {
								// send sit command
								_proxy.join(_model._tid, i, _proxy._worth);
								_join_sent = true;
							}
							catch (Exception e){
								Log.w("TableView mouseClick ", e.getMessage());
							}
						}
					}
				}
			}
	 }
	 public double getTotalBetAction(GameEvent ge) {
	     String[][] plrs = ge.getPlayerDetails();
	     double total_amt = 0;
	        for (int i = 0; plrs != null && i < plrs.length; i++) {
	         total_amt += Double.parseDouble(plrs[i][2]);
	        }
	        
	        String pots[][] = ge.getPot();//,pots=main|17.00
	        if (pots != null){
	         for (int i=0;i<pots.length;i++){
	          total_amt += Double.parseDouble(pots[i][1]);
	         }
	        }
	              
	        /*TotalBetAction ia = 
	            new TotalBetAction(new TotalBet(total_amt));*/
	        return total_amt;

	    }
	void doDraw(Canvas canvas) {
		if (canvas == null)return;
		canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		//canvas.
		Paint paint = new Paint();
		if (_roomskin != null){
			_roomskin.setDimensions(mCanvasWidth, mCanvasHeight);
		}
		if (_win_mesg == null && _round != null){
			paint.setTextSize(12);
			paint.setColor(Color.GRAY);
			paint.setTypeface(ActivitySplash.paintobjects.get("verdanaface"));
			canvas.drawText(_round, RoomSkin._roundx , RoomSkin._roundy, paint);
		}
		if (_players != null){
			try
			{
			for (int j=0;j<_players.length;j++){
				//_players[j]._rel_pos = _players[j]._pos;//_roomskin.getSP(_players[j]._pos);
				_players[j]._screen_pos =  _roomskin.getPlayerCoordinates(_roomskin.getSP(_players[j]._pos));
//				_players[j].last_move =(_last_move._pos==j)? _last_move._move : null;
			}
			for (int j=0;j<_players.length;j++){
				if(_players[j]._name!=null)
				_players[j].paint(canvas);
			}
			}catch(Exception e){}
		}
		if (_model != null){
			_model.paint(canvas);
		}
//		if (_me == null && _vacant_positions != null){
//			for (int i=0;i<_vacant_positions.length;i++){
//				if (_vacant_positions[i] ==0){
//					Point p =  _roomskin.getPlayerCoordinates(i);
//					if(i == 7)p.x = p.x + 30;
//					_empty_seat_icon.setBounds(p.x, p.y - 10, p.x + Skin.SEAT_WIDTH, p.y + Skin.SEAT_HEIGHT - 10);
//					_empty_seat_icon.draw(canvas);
//				}
//			}
//		}
		
		
		if (_last_move != null ){
			Point _lmp = _roomskin.getPlayerCoordinates(_roomskin.getSP(this._last_move._pos));// _roomskin.getSP(this._last_move._pos));
			paint.setTextSize(12);
			
			paint.setTypeface(ActivitySplash.paintobjects.get("verdanaface"));
			paint.setColor(Color.BLACK);
//			_bubble_icon.setBounds(_lmp.x - 2+2, _lmp.y - 15+2, _lmp.x + RoomSkin.BUBBLE_WIDTH + 8 - 2, _lmp.y + RoomSkin.BUBBLE_HEIGHT -13 );
//			_bubble_icon.draw(canvas);
			if(_last_move._move.equals("join"));
//				canvas.drawText(_last_move._move, _lmp.x + 14, _lmp.y + , paint);
			else
			{
				Paint paintpot = new Paint();
				paintpot.setTypeface(Typeface.SERIF);
				paintpot.setColor(Color.WHITE);
				paintpot.setTextSize(12);
				canvas.drawText("Total Pot: $"+ActivityTable._totalpot, 191, 174, paintpot);
//				canvas.drawText(_last_move._move + " $" + Utils.getRounded(_last_move._amount), _lmp.x +14, _lmp.y +14, paint);
//			_bubble_icon.setBounds(_lmp.x - 2, _lmp.y - 15, _lmp.x + Skin.BUBBLE_WIDTH -2, _lmp.y + Skin.BUBBLE_HEIGHT -15);
//			_bubble_icon.draw(canvas);
//			canvas.drawText(_last_move._move + " $" + Utils.getRounded(_last_move._amount), _lmp.x + 13, _lmp.y - 3, paint);
			}
		}
		if (_dealer_pos != -1){
			Point sp = _roomskin.getPlayerCoordinates(_roomskin.getSP( _dealer_pos));//_roomskin.getSP(_dealer_pos));
			//_dbut_icon.setBounds(sp.x - 5, sp.y - 18, sp.x + Skin.DBUT_WIDTH -2, sp.y + Skin.DBUT_HEIGHT -15);
			_dbut_icon.setBounds(sp.x + 2, sp.y + 20, sp.x + RoomSkin.DBUT_WIDTH + 2, sp.y + RoomSkin.DBUT_HEIGHT + 20);
			_dbut_icon.draw(canvas);
		}
		if (_win_mesg != null){
			paint.setTextSize(12);
			paint.setTypeface(ActivitySplash.paintobjects.get("verdanaface"));
			paint.setColor(Color.GRAY);
			//canvas.drawText(_win_mesg.toString(), Skin._roundx - 100, Skin._roundy, paint);
			if(_win_mesg.length() < 50)
				canvas.drawText(_win_mesg.toString(), RoomSkin._roundx - 145 , RoomSkin._roundy , paint);
			else
			{
				canvas.drawText(_win_mesg.substring(0, 50), RoomSkin._roundx - 145 , RoomSkin._roundy - 10, paint);
			}
		}
		if (_chat_mesg != null){
			int _position = -1;
			for (int j=0;j<_players.length;j++){
				if(_players[j]._name.equals(_chat_mesg.substring(0, _chat_mesg.indexOf(":"))))
				{
					_position = j;
					break;
				}
			}
			Point p = _roomskin.getPlayerCoordinates(_roomskin.getSP(_players[_position]._pos));// _roomskin.getSP(_players[_position]._pos));
			paint.setTextSize(12);
		//	paint.setTypeface(Typeface.SERIF);
			paint.setTypeface(ActivitySplash.paintobjects.get("verdanaface"));
			paint.setColor(Color.BLACK);
			_chatbg.setBounds(p.x + 15 , p.y -10, p.x + 54 , p.y + 22);
			_chatbg.draw(canvas);
//			_chat_mesg = _chat_mesg.substring(0,_chat_mesg.indexOf(":"))+" has written some text";
//			if(_chat_mesg.length() < 15)
//				canvas.drawText(_chat_mesg, p.x , p.y + 10, paint);
//			else
//			{
//				canvas.drawText(_chat_mesg.substring(0, 15), p.x , p.y + 10, paint);
//				canvas.drawText(_chat_mesg.substring(15, _chat_mesg.length()), p.x , p.y + 25, paint);
//			}
			_chatr = new ChatAnimationRoutine();
			_chatt = new Timer();
			_chatt.schedule(_chatr, 6000);
			
		}
		if(_chat_mesg_disable)
		{
			_chatbg.setVisible(false, true);
			_chat_mesg_disable = false;
		}
		canvas.save();
	}
	
	
	class TimerAnimationRoutine extends TimerTask
	{
		int _x, _y;
		AnimTimer _d;
		
		TimerAnimationRoutine(int x, int y){
			_x = x;
			_y = y;
			_d = new AnimTimer(_context);
		}
		
		@Override
		public void run() {
			Drawable d = _d.getNext();
			d.setBounds(_x, _y, _x + AnimTimer.WIDTH, _y + AnimTimer.HEIGHT);
			Canvas canvas = _holder.lockCanvas(new Rect(_x, _y, _x + AnimTimer.WIDTH, _y + AnimTimer.HEIGHT));
			d.draw(canvas);
			_holder.unlockCanvasAndPost(canvas);
		}
		
		public void clear(){
		}
	}
	
	class ChatAnimationRoutine extends TimerTask
	{
		ChatAnimationRoutine()
		{
			
		}
		@Override
		public void run() {
			_chat_mesg = null;
			_chat_mesg_disable = true;
			_chatt.cancel();
			repaint();
		}
	}

	class TimerMoveRoutine extends TimerTask
	{
		NextMove _nm;
		
		TimerMoveRoutine(NextMove nm){
			_nm = nm;
		}
		
		@Override
		public void run() {
			// make move
			if (_nm.hasOptout()){ //do sit-out
				try {
					_proxy.makeMove("opt-out", 0.0, -1, 0.0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					_proxy.makeMove("fold", 0.0, -1, 0.0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		_proxy.addAction(new Action(Action.CLEAR_MOVES_ACTION));
    		_tmvt.cancel();
    		_tat.cancel();
        }
	}
	
	

	class WinnerAnimationRoutine extends TimerTask
	{
		Pot _p;
		ClientPlayerModel _cp;
		DChip _dp;
		Point _path[];
		int _iter=0;
		
		WinnerAnimationRoutine(Pot p, ClientPlayerModel cp){
			if(p == null || cp == null)return;
			_p=p;
			_cp=cp;
			_dp = new DChip(_context);
			int sx = p._x;
			int sy = p._y;
			int ex = cp._screen_pos.x + 25;
			int ey = cp._screen_pos.y + 25;
			
			int dx = (ex - sx)/10;
			int dy = (ey - sy)/10;
			
			_path = new Point[11];
			for (int i=0;i<10;i++){
				_path[i] = new Point();
				_path[i].x = sx + i*dx;
				_path[i].y = sy + i*dy;
			}
			_path[10] = new Point();
			_path[10].x = sy + 10*dx;
			_path[10].y = sy + 10*dy;
		}
		
		int _px, _py;
		@Override
		public void run() {
			if (_iter >= 10)cancel();
			_px = _path[_iter].x;
			_py = _path[_iter].y;
			
			_p._x = _px;
			_p._y = _py;
			
			repaint();
			_iter++;
		}

	}
	
	class DealCardRoutine extends TimerTask
	{
		ClientPlayerModel _cpm[];
		int index = 0;		
		DealCardRoutine(ClientPlayerModel cpm[]){
			_cpm = cpm;
		}
		
		@Override
		public void run() {
			
			_dealCards.anim(_cpm[index]._pos);
			repaint();
			index++;
			if(index > _players.length/2)_dealCards._turn = 1;
			
		}
	}
	
	class DealCommCardRoutine extends TimerTask
	{
		int _rnd;
		
		DealCommCardRoutine(int rnd){
			_rnd = rnd;
		}
		
		@Override
		public void run() {
			if (_rnd == 0){ // flop
				if (_model._communityCards.nextFlop()){
					cancel();
				}
			}
			else if (_rnd==1){// turn
				if (_model._communityCards.nextTurn()){
					cancel();
				}
			}
			else if (_rnd==2){// turn
				if (_model._communityCards.nextRiver()){
					cancel();
				}
			}

			repaint();
		}
	
	}
	
	
	/* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mCanvasWidth = width;
		mCanvasHeight = height;
		if (_roomskin != null){
			_roomskin.setDimensions(mCanvasWidth, mCanvasHeight);
		}
		// don't forget to resize the background image
		mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width,
				height, true);
		
		Canvas canvas = holder.lockCanvas(null);
		doDraw(canvas);
		holder.unlockCanvasAndPost(canvas);
		//repaint();
	}

	public void repaint() {
		Canvas canvas = _holder.lockCanvas(null);
		doDraw(canvas);
		_holder.unlockCanvasAndPost(canvas);
	//	_holder.removeCallback(this);
	//  _holder.
	}
	
	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas canvas = holder.lockCanvas(null);
		doDraw(canvas);
		holder.unlockCanvasAndPost(canvas);
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		//
		
//		reset();
//		if(_tmvt != null)_tmvt.cancel();
//		if(_tmvr != null)_tmvr.cancel();
//		if(_tat != null)_tat.cancel();
//		if(_tar != null)_tar.cancel();
//		if(_dccr != null)_dccr.cancel();
//		if(_dcct != null)_dcct.cancel();		
	}

}
