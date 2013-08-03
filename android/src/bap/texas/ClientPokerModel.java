package bap.texas;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import bap.texas.util.Card;
import bap.texas.util.CommCards;
import bap.texas.util.DCard;
import bap.texas.util.Pot;



/// DDDDDDD	import com.onlinepoker.client.util.AdminUtil;

public class ClientPokerModel extends Painter {
    public long _grid = 0;
    public long _prev_grid;
    public String _tid;
    public double _minBet, _maxBet;
    public int _minPlayer, _maxPlayer; 

    public ClientPlayerModel[] _playersMod;
    public double _bet = 0;
    public Pot _pot[] = new Pot[1];
    public double _rake = 0;

    /** to make winning combination available in paint() */
    public Card[] _winnerCards;
    public CommCards _communityCards;
    RoomSkin _skin;
	DCard _dc;

	
    public ClientPokerModel(RoomSkin s, Context c, ActivityTable p) {
    	super(s, c, p);
    	_skin = s;
    	_dc = new DCard(c);
    }

	@Override
	public void paint(Canvas canvas) {
//		Paint paint = new Paint();
//		paint.setTextSize(12);
//		paint.setTypeface(Typeface.SERIF);
//		paint.setColor(Color.YELLOW);
//		canvas.drawText(_tid, 0, 14,paint);
//		canvas.drawText("$" + _minBet +"/NL", 0, 24,paint);
		
		//community cards
		if (_communityCards != null)
			_communityCards.paint(canvas);
		
		// draw pot
		/*if (_pot != null && _pot[0] != null && _pot[0]._value > 0.001){
			for (int i=0;i<_pot.length;i++){
				_pot[i].paint(canvas);
			}
		}*/
	}


    public double getMinBuyIn() {
        return _minBet * 2;
    }

    public double getMaxBuyIn() {
        return _minBet * 200;
    }

    public double getCurrentBet() {
        return _bet;
    }

    public void setCurrentBet(double bet) {
        this._bet = bet;
    }

    public Pot[] getPots() {
        return _pot;
    }


    public double getRake() {
        return (double)_rake;
    }

    public void setRake(double rake) {
        this._rake = rake;
    }



    public String toString(){
        return _tid + ", " + _grid;
    }


}
