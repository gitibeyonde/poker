package bap.texas.util;

import android.util.Log;

public class NextMove {
	public int _pos=-1;
	public String _move[];
	public double _amount[];
	public double _amount_range[];
	public boolean _sliderRequired=false;
	public int _slider_move=-1;
	
	public NextMove(String s){ //next-move=3|small-blind|0.50`3|opt-out|0.00
		if (s.contains("none") ||s.startsWith("-1|wait"))return;
		Log.i("NextMove", s);
		String nms[] = s.split("`");
		_move = new String[nms.length];
		_amount = new double[nms.length];
		_amount_range = new double[nms.length];
		for (int i=0;i<nms.length;i++){
			String nm[] = nms[i].split("\\|");
			_pos = Integer.parseInt(nm[0]);
			_move[i] = nm[1];
			int dash_index = -1;
			if ((dash_index = nm[2].indexOf("-")) == -1){
				_amount[i] = Double.parseDouble(nm[2]);
				_amount_range[i] = -1;
			}
			else {
				_amount[i] = Double.parseDouble(nm[2].substring(0, dash_index));
				_amount_range[i] = Double.parseDouble(nm[2].substring(dash_index +1));
				_sliderRequired = true;
				_slider_move = _slider_move == -1 ? i : _slider_move;
			}
			//Log.w("NextMove", "Pos=" + _pos + " Move=" + _move[i] + " Amt 1=" + _amount[i] + " Amt 2=" + _amount_range[i]);
		}
	}
	
	public boolean equals(NextMove n){
		return _pos == n._pos;
	}
	
	public boolean hasFold(){
		for (int i=0;i<_move.length;i++){
			if (_move[i].equals("fold"))return true;
		}
		return false;
	}

	public boolean hasOptout(){
		for (int i=0;i<_move.length;i++){
			if (_move[i].equals("opt-out"))return true;
		}
		return false;
	}
	
	
}
