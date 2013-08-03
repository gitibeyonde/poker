package bap.texas;

public class ListItem {
	private String _tableName;
	private String _minBet, _maxBet;
	private String _minPlayer, _maxPlayer;
	private String _players;
	private String _pot;
	private String _playerDetails;
	
	public void setTableName(String tn){
		_tableName = tn;
	}
	public void setMinBet(double tn){
		_minBet = (int)tn+"";
	}
	public void setMaxBet(double tn){
		if (tn == -1){
			_maxBet = "NL";
		}
		else if (tn == 0){
			_maxBet = "PL";
		}
		else {
			_maxBet = (int)tn +"";
		}
	}
	public void setMinPlayers(String tn){
		_minPlayer = tn;
	}
	public void setMaxPlayers(String tn){
		_maxPlayer = tn;
	}
	public void setPlayers(String tn){
		_players = tn;
	}
	public void setPot(String tn){
		_pot = tn;
	}
	public void setPlayerDetails(String pd){
		_playerDetails = pd;
	}
	

	public String getTableName(){
		return _tableName;
	}
	public String getStakes(){
		if (_minBet==null)return "Stakes";
		return _minBet+"/"+_maxBet;
	}
	public String getPlayers(){
		return _players;
	}
	public String getAllowedPlayers(){
		return _minPlayer + "-" + _maxPlayer;
	}
	public String getPot(){
		return _pot;
	}
	public String getPlayerDetails(){
		return _playerDetails;
	}
	
}
