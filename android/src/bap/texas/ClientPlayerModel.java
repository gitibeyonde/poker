package bap.texas;

import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import bap.texas.util.BetPosition;
import bap.texas.util.Card;
import bap.texas.util.DCard;
import bap.texas.util.LastMove;
import bap.texas.util.Move;
import bap.texas.util.NextMove;
import bap.texas.util.PlayerStatus;

public class ClientPlayerModel extends Painter {
	public String _name;
	public int _pos, _rel_pos;
	public Point _screen_pos;
	public Point _bet_pos;
	public double _worth;
	public double _bet;
	public int _gender;
	public int _currBet;
	public long _status;
	public Card[] _hand;
	public Move[] _mv;
	public Move _lastMove;
	public boolean _active;
	public boolean _dealer;
	public boolean _me;
	private boolean _winner;
	private boolean _deal_cards;
	public static int _deal_stage;
	public int _iq;
	Drawable _icon,_chip_icon,_betbackground; 
	Drawable _box; 
	Drawable _whitebox;
	Drawable _callbox;
	Drawable _foldbox;
	Drawable _raisbox;
	DCard _dc;
	static Random _r= new Random();
	AssetManager mgr ;
	public static CoinSkin _coinskin;
	 private Matrix  mMatrix;
	 private Paint   mPaint;
	 private Paint.FontMetrics mFontMetrics;
	 private BetPosition _BP;
	 RoomSkin _roomskin;
	 String last_move;
	 boolean show_name;
	 LastMove _lastmove;
	 NextMove _nextmove;
	public static final int _PLAYER_ICON[] = {
												  R.drawable.avatar1,R.drawable.avatar2,
												  R.drawable.avatar3,R.drawable.avatar4,
												  R.drawable.avatar5,R.drawable.avatar6,
												  R.drawable.avatar7,R.drawable.avatar8,
												  R.drawable.avatar9,R.drawable.avatar10,
												  R.drawable.avatar11,R.drawable.avatar12,
												  R.drawable.avatar13,R.drawable.avatar14,
												  R.drawable.avatar15,R.drawable.avatar16,
												  R.drawable.avatar17,R.drawable.avatar18,
												  R.drawable.avatar19,R.drawable.avatar20,
												  R.drawable.avatar21,R.drawable.avatar22,
												  R.drawable.avatar23,R.drawable.avatar24,
												  R.drawable.avatar25,R.drawable.avatar26,
												  R.drawable.avatar27,R.drawable.avatar28,
												  R.drawable.avatar29,R.drawable.avatar30,
											};

	public ClientPlayerModel(RoomSkin s, Context c, ActivityTable p, LastMove lm, NextMove nm, String cpd[]){ //0|96.00|2.00|lewisVus|16777217|0|__'__
		super(s, c, p);
		
		//0|1016.00|0.00|nagaraju|16779268|1|0|1|bhongir|__'__
		show_name=true;
		_roomskin=s;
		_lastmove=lm;
		_nextmove=nm;
		 mMatrix = new Matrix();
		 mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
         mFontMetrics = mPaint.getFontMetrics();
         _BP = new BetPosition(s, c, p);
        Log.i("Status-----",cpd[0] +"|"+cpd[1]+"|"+cpd[2]+"|"+cpd[3]+"|"+cpd[4]+"|"+cpd[5]+"|"+cpd[6]+"|"+cpd[7]+"|"+cpd[8]);
		_coinskin = new CoinSkin();
		_pos = Integer.parseInt(cpd[0]);
		_worth = Double.parseDouble(cpd[1]);
		_bet = Double.parseDouble(cpd[2]);
		_name = cpd[3];
		_status = Long.parseLong(cpd[4]);
		_gender = Integer.parseInt(cpd[5]);
		_hand = getHand(cpd[9]);
		//_icon = c.getResources().getDrawable(_PLAYER_ICON[0]);
		_icon = c.getResources().getDrawable(_PLAYER_ICON[!cpd[7].equals("null")?Integer.parseInt(cpd[7].length()>2?cpd[7].substring(0, 2):cpd[7])-1:0]);
		//Log.i
		
		_box = c.getResources().getDrawable(R.drawable.default_bg);
		_whitebox = c.getResources().getDrawable(R.drawable.active_bg);
		_callbox = c.getResources().getDrawable(R.drawable.call_bg);
		_foldbox = c.getResources().getDrawable(R.drawable.fold_bg);
		_raisbox = c.getResources().getDrawable(R.drawable.raise_bg);
		_betbackground = c.getResources().getDrawable(R.drawable.seat_name_plate_action_yellow);
		_chip_icon = c.getResources().getDrawable(R.drawable.coin);
    	_dc = new DCard(c);
    	_winner = false;
	}
	
	 
	public void reset(){
		_hand = null;
		_bet=0;
		_currBet=-1;
		_active = true;
		_winner = false;
	}
	
	public void setWinner(){
		_winner = true;
	}
	
	public boolean getWinner(){
		return _winner;
	}
	
	public void setDealPocketCards(){
		_deal_cards = true;
		_deal_stage = 10;
	}
	
	@Override
	public void paint(Canvas canvas) {
		//for the player at pos 0 increasing the dimensions larger than the others 
		
			_box.setBounds(_screen_pos.x , _screen_pos.y, _screen_pos.x + RoomSkin.BOX_WIDTH , _screen_pos.y + RoomSkin.BOX_HEIGHT );
			_callbox.setBounds(_screen_pos.x , _screen_pos.y, _screen_pos.x + RoomSkin.BOX_WIDTH , _screen_pos.y + RoomSkin.BOX_HEIGHT );
			_foldbox.setBounds(_screen_pos.x , _screen_pos.y, _screen_pos.x + RoomSkin.BOX_WIDTH , _screen_pos.y + RoomSkin.BOX_HEIGHT );
			_raisbox.setBounds(_screen_pos.x, _screen_pos.y, _screen_pos.x + RoomSkin.BOX_WIDTH , _screen_pos.y + RoomSkin.BOX_HEIGHT );
			_whitebox.setBounds(_screen_pos.x, _screen_pos.y, _screen_pos.x + RoomSkin.BOX_WIDTH , _screen_pos.y + RoomSkin.BOX_HEIGHT );
			_icon.setBounds(_screen_pos.x - 2 , _screen_pos.y , _screen_pos.x + RoomSkin.AVATAR_WIDTH , _screen_pos.y + RoomSkin.AVATAR_HEIGHT);

			
			if (PlayerStatus.isActive(_status))
				_icon.setAlpha(255);
			else
				_icon.setAlpha(100);
		
			
			if(PlayerStatus.isSitOut(_status)||PlayerStatus.isAllIn(_status))			
				_box.draw(canvas);
			else if(_lastmove._pos==_pos && _lastmove._move.contains("fold") && _nextmove._pos!=_pos)
				_foldbox.draw(canvas);
			else if(_lastmove._pos==_pos && _lastmove._move.contains("raise") && _nextmove._pos!=_pos)
				_raisbox.draw(canvas);
			else if(_lastmove._pos==_pos && _lastmove._move.contains("call") && _nextmove._pos!=_pos)
				_callbox.draw(canvas);
			else if(_nextmove._pos == _pos ){
					_whitebox.draw(canvas);
					show_name=false;
				}	
			else
				_box.draw(canvas);
			_icon.draw(canvas);
			
			Paint paint = new Paint();
			paint.setFakeBoldText(false);
			paint.setTextSize(11);
			paint.setColor(Color.WHITE);
			paint.setTypeface(ActivitySplash.paintobjects.get("verdanaface"));
			
			if(show_name)
				if (_name.length() > 10)
						canvas.drawText(_name.subSequence(0, 10).toString(), _screen_pos.x + 14, _screen_pos.y + 14,paint);
				else
						canvas.drawText(_name, _screen_pos.x + 14, _screen_pos.y + 14,paint);
			
				paint.setTextSize(11);
				paint.setFakeBoldText(false);
				paint.setTypeface(ActivitySplash.paintobjects.get("verdanaface"));
				paint.setColor(_nextmove._pos == _pos && !_lastmove._move.contains("join")?Color.BLACK:Color.WHITE);
				paint.setTextAlign(Align.LEFT);
				canvas.drawText(" $" + _worth,(_screen_pos.x + RoomSkin.BOX_WIDTH )-(6*(String.valueOf(_worth).length()+3)), _screen_pos.y +RoomSkin.AVATAR_HEIGHT - 10, paint);
			
			if (_bet > 0 && _winner==false){
				Point _coin = _roomskin.getCoinCoordinates(_roomskin.getSP(_pos));// _roomskin.getSP(_pos));
				_chip_icon.setBounds(_coin.x,_coin.y, _coin.x + RoomSkin.CHIP_WIDTH, _coin.y + RoomSkin.CHIP_HEIGHT);
				_chip_icon.draw(canvas);
			}
			
			if (_hand != null && _hand.length == 2){
				
				boolean _show = true;
				int winraise = _winner ? 5 : 2;
				Card c = _hand[0];
				BitmapDrawable bmp = (BitmapDrawable) _dc._cards[_show ? c.index+1 : 0];
				int w = bmp.getMinimumWidth();
				int h = bmp.getMinimumHeight();
				Matrix mtx = new Matrix();
				mtx.postRotate(345);
				Bitmap rotatedBMP = Bitmap.createBitmap(bmp.getBitmap(), 0, 0, w, h, mtx, true);
				BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);
				bmd.setBounds(_screen_pos.x+ RoomSkin.AVATAR_WIDTH - 25, _screen_pos.y - winraise + 20, 
						_screen_pos.x + RoomSkin.AVATAR_WIDTH + RoomSkin.CARD_WIDTH - 25, _screen_pos.y + RoomSkin.CARD_HEIGHT - winraise + 20);
				bmd.draw(canvas);

				
				c = _hand[1];
				bmp = (BitmapDrawable) _dc._cards[_show ? c.index+1 : 0];
				w = bmp.getMinimumWidth();
				h = bmp.getMinimumHeight();
				mtx = new Matrix();
				mtx.postRotate(15);
				rotatedBMP = Bitmap.createBitmap(bmp.getBitmap(), 0, 0, w, h, mtx, true);
				bmd = new BitmapDrawable(rotatedBMP);
				bmd.setBounds(_screen_pos.x+ RoomSkin.AVATAR_WIDTH+ RoomSkin.CARD_WIDTH  - 40  , _screen_pos.y - winraise + 16, 
						_screen_pos.x + RoomSkin.AVATAR_WIDTH + 2*RoomSkin.CARD_WIDTH - 40, _screen_pos.y + RoomSkin.CARD_HEIGHT  - winraise + 16 );
				bmd.draw(canvas);
				
			/*	boolean _show = true;
				int winraise = _winner ? 5 : 2;
				Card c = _hand[0];
				Drawable d = _dc._pocketCards[_show ? c.index+1 : 0];
				d.setBounds(_screen_pos.x+ Skin.AVATAR_WIDTH+ 1 + 10, _screen_pos.y - winraise+ 2 - 1, 
						_screen_pos.x + Skin.AVATAR_WIDTH+ Skin.CARD_WIDTH + 10, _screen_pos.y + Skin.CARD_HEIGHT - winraise - 1);
				d.draw(canvas);
				canvas.save();
				
				c = _hand[1];
				d = _dc._pocketCards[_show ? c.index+1 : 0];
				d.setBounds(_screen_pos.x+ Skin.AVATAR_WIDTH+ Skin.CARD_WIDTH + 2 + 10 , _screen_pos.y - winraise+ 2 - 1, 
						_screen_pos.x + Skin.AVATAR_WIDTH + 2*Skin.CARD_WIDTH + 2 + 10, _screen_pos.y + Skin.CARD_HEIGHT  - winraise - 1);
				canvas.translate(0, 0);
				mMatrix.setPolyToPoly(new float[] { 32, 32, 75, 5 }, 0, new float[] { 32, 32, 64, 7 }, 0, new float[] { 32, 32, 64, 32 }.length >> 1);
				canvas.concat(mMatrix);
				mPaint.setStrokeWidth(4);
				mPaint.setColor(Color.GRAY);
				mPaint.setStyle(Paint.Style.STROKE);
				d.draw(canvas);
				canvas.restore();*/
			}
		
			
	}


	public boolean equals(ClientPlayerModel p) {
		return _name.equals(p._name);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(_name).append("'").append(_pos).append("=").append(_currBet);
		
		if (_mv != null && _mv.length > 0){
			buf.append(" moves=");
			for (int i=0;i<_mv.length;i++){
				buf.append(_mv[i]).append(",");
			}
		}
		
		if (_hand.length > 0){
			buf.append(_hand[0]).append("`").append(_hand[1]);
		}
		return buf.toString();
	}
	

	 public static Card[] getHand(String str) {
	    Card c[] = null;
	    try {
	      if (str.length() < 2) {
	        return null;
	      }
	      String[] c_str = str.split("'");
	      c = new Card[c_str.length];
	      for (int i = 0; i < c_str.length; i++) {
	        c[i] = new Card(c_str[i]);
	        c[i].setIsOpened(true);
	      }
	    }
	    catch (Exception e) {
	        //e.printStackTrace();
	    }
	    return c;
	  }
}
