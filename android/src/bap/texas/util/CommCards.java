package bap.texas.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import bap.texas.ActivityTable;
import bap.texas.Painter;
import bap.texas.RoomSkin;

public class CommCards extends Painter {
	Card _f1, _f2, _f3, _t, _r;
	Drawable _df1, _df2, _df3, _dt, _dr;
	Point _pf1, _pf2, _pf3, _pt, _pr;
	int _df1x, _df2x, _df3x, _dtx, _drx;
	int _df1y, _df2y, _df3y, _dty, _dry;
	public int _sf, _st, _sr;
	Point _pff1, _pff2, _pff3, _pft, _pfr;
	AnimCC _animcc;
	DCard _dcard;
	public int _length= -1;

	
	//Skin._ccx + i * Skin.CARD_WIDTH, Skin._ccy, Skin._ccx + (i +1) * Skin.CARD_WIDTH, Skin._ccy + Skin.CARD_HEIGHT);
	
	public CommCards(Card[] cv, RoomSkin s, Context c, ActivityTable p){
		super(s, c, p);
		_animcc = new AnimCC(c);
		_dcard = new DCard(c);
		_f1 = cv[0];
		_f2 = cv[1];
		_f3 = cv[2];
		_df1 =  _dcard._cards[_f1.index + 1];
		_df2 =  _dcard._cards[_f2.index + 1];
		_df3 =  _dcard._cards[_f3.index + 1];
		_pf1 = new Point(RoomSkin._dx, RoomSkin._dy);
		_pf2 = new Point(RoomSkin._dx, RoomSkin._dy);
		_pf3 = new Point(RoomSkin._dx, RoomSkin._dy);
		_pff1 = new Point(RoomSkin._ccx, RoomSkin._ccy);
		_pff2 = new Point(RoomSkin._ccx + RoomSkin.COMM_CARD_WIDTH, RoomSkin._ccy);
		_pff3 = new Point(RoomSkin._ccx + 2 * RoomSkin.COMM_CARD_WIDTH, RoomSkin._ccy);
		
		_df1x = (_pff1.x - _pf1.x)/8; _df1y = (_pff1.y - _pf1.y)/8;
		_df2x = (_pff2.x - _pf2.x)/8; _df2y = (_pff2.y - _pf2.y)/8;
		_df3x = (_pff3.x - _pf3.x)/8; _df3y = (_pff3.y - _pf3.y)/8;
		
		_sf = 0;
		_length = 3;
	}
	
	public void addTurnCard(Card c){
		_t = c;
		_dt =  _dcard._cards[_t.index + 1];
		_pt = new Point(RoomSkin._dx, RoomSkin._dy);
		_pft = new Point(RoomSkin._ccx + 3 * RoomSkin.COMM_CARD_WIDTH, RoomSkin._ccy);
		
		_dtx = (_pft.x - _pt.x)/8; _dty = (_pft.y - _pt.y)/8;
		
		_st = 0;
		_length = 4;
	}

	public void addRiverCard(Card c){
		_r = c;
		_dr =  _dcard._cards[_r.index + 1];
		_pr = new Point(RoomSkin._dx, RoomSkin._dy);
		_pfr = new Point(RoomSkin._ccx + 4 * RoomSkin.COMM_CARD_WIDTH, RoomSkin._ccy);
		
		_drx = (_pfr.x - _pr.x)/8; _dry = (_pfr.y - _pr.y)/8;
		
		_sr = 0;
		_length = 5;
	}
	
	public boolean nextFlop(){
		_sf++;
		return _sf == 8;
	}
	public boolean nextTurn(){
		_st++;
		return _st == 8;
	}
	public boolean nextRiver(){
		_sr++;
		return _sr == 8;
	}

	@Override
	public void paint(Canvas c){
		
		if (_sf < 8){
			Drawable d = _animcc.get(_sf);
			if (_f1 != null){
				int l = _pf1.x + _df1x * _sf;
				int t = _pf1.y + _df1y * _sf;
				d.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				d.draw(c);
			}
			if (_f2 != null){
				int l = _pf2.x + _df2x * _sf;
				int t = _pf2.y + _df2y * _sf;
				d.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				d.draw(c);
			}
			if (_f3 != null){
				int l = _pf3.x + _df3x * _sf;
				int t = _pf3.y + _df3y * _sf;
				d.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				d.draw(c);
			}
		}
		else {
			if (_f1 != null){
				int l = _pff1.x;
				int t = _pff1.y;
				_df1.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				_df1.draw(c);
			}
			if (_f2 != null){
				int l = _pff2.x;
				int t = _pff2.y;
				_df2.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				_df2.draw(c);
			}
			if (_f3 != null){
				int l = _pff3.x;
				int t = _pff3.y;
				_df3.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				_df3.draw(c);
			}
		}
		//TURN
		if (_st < 8){
			Drawable d = _animcc.get(_st);
			if (_t != null){
				int l = _pt.x + _dtx * _st;
				int t = _pt.y + _dty * _st;
				d.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				d.draw(c);
			}
		}
		else {
			if (_t != null){
				int l = _pft.x;
				int t = _pft.y;
				_dt.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				_dt.draw(c);
			}
		}
		//RIVER
		if (_sr < 8){
			if (_r != null){
				Drawable d = _animcc.get(_sr);
				int l = _pr.x + _drx * _sr;
				int t = _pr.y + _dry * _sr;
				d.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				d.draw(c);
			}
		}
		else {
			if (_r != null){
				int l = _pfr.x;
				int t = _pfr.y;
				_dr.setBounds(l, t, l + RoomSkin.COMM_CARD_WIDTH, t + RoomSkin.COMM_CARD_HEIGHT);
				_dr.draw(c);
			}
		}
		
		
	}

}
