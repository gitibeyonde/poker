package bap.texas.util;

public class LastMove {
	public int _pos;
	public String _name="";
	public String _move="none";
	public double _amount=0;
	
	public LastMove(String s){ //last-move=2|barbieVus|call|8.0
		if (s.contains("none") || s.startsWith("-1|wait"))return;
		String lma[] = s.split("\\|");
		_pos = Integer.parseInt(lma[0]);
		_name = lma[1];
		_move = lma[2];
		if (_move.equals("small-blind")){
			_move = "SB";
		}
		else if (_move.equals("big-blind")){
			_move = "BB";
		}
		_amount = Double.parseDouble(lma[3]);
	}
}
