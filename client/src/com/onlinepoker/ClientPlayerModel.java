package com.onlinepoker;

import java.awt.Rectangle;

import com.golconda.game.PlayerStatus;
import com.golconda.game.util.Card;
import com.golconda.game.util.Cards;


public class ClientPlayerModel {

    /** Value of property _sex when user is male */
    public static final char MALE = 'M';

    /** Value of property _sex when user is female */
    public static final char FEMALE = 'F';

    protected String _name = "";
    protected String _avatar = "1.png";
    protected int _rank;
    protected String _city = "";
    protected char _sex = MALE;
    protected double _roundBet = 0;
    protected double _amtAtTable = 0;

    protected long _status = PlayerStatus.NONE;
    private Cards _cards = new Cards(false);
    private java.util.Date _allInTimestamp;
    private int _allInCount;
    private int _position,_rel_pos ;

	/** bounds of player _name */
    private Rectangle _boundsName = null;
    /** isSelect */
    protected boolean _selected = false;
    /** is Player Muting */
    protected boolean _mute = false;
    /** is Player must paint speak icon */
    protected boolean _mustPaintSpeaker = false;

    /**
     * Copy constructor. Presumably will be used
     * only for copying from server to client.
     */
    public ClientPlayerModel(ClientPlayerModel player) {
        if (player != null) {
        	_position = player._position;  
        	_name = player._name;
            _rank = player._rank;
            _avatar = player._avatar;
            _city = player._city;
            _sex = player._sex;
            _amtAtTable = player._amtAtTable;
            _status = player._status;
            _roundBet = player._roundBet;
            _allInTimestamp = player._allInTimestamp;
            _allInCount = player._allInCount;
            _cards = player._cards;
        }
    }
    
    public void refreshPlayerModel(ClientPlayerModel player){
        _position = player._position;
        _name = player._name;
        _rank = player._rank;
        _avatar = player._avatar;
        _city = player._city;
        _sex = player._sex;
        _amtAtTable = player._amtAtTable;
        _status = player._status;
        _roundBet = player._roundBet;
        _allInTimestamp = player._allInTimestamp;
        _allInCount = player._allInCount;
        _cards = player._cards;
    }
    

    public ClientPlayerModel(String[] pd) {
    	_position = Integer.parseInt(pd[0]);
    	_amtAtTable = Double.parseDouble(pd[1]);
        _roundBet = Double.parseDouble(pd[2]);
        _name = pd[3];
        _status = Long.parseLong(pd[4]);
        if(_status == 0)_status = PlayerStatus.NEW;
        _sex = pd[5].equals("0") ? FEMALE : MALE;
        _rank = Integer.parseInt(pd[6]);
        _avatar = pd[7];
        //String city_arr[] = {"Paris","Japan","India","Macau","Vegas"};
        _city = pd[8].equals("null")?"Las Vegas":pd[8];
        if (pd[9] != null) {
            String[] c_str = pd[9].split("\\'");
            _cards.clear();
            for (int i = 0; i < c_str.length; i++) {
                //System.out.println(c_str[i].toString());
                _cards.addCard(new Card(c_str[i]));
            }
        }
    }


    public boolean isSelected() {
        return _selected;
    }

    public void setSelected(boolean _selected) {
    	if (this._selected != _selected) {
            this._selected = _selected;
        }
    }

    public void update() {
        //////////////////////////////////////////////////
        //////////////////// -UPDATE- ////////////////////
        //////////////////////////////////////////////////
    }

    public void setNameBounds(int x_name, int y_name, int w_name, int h_name) {
        this._boundsName = new Rectangle(x_name, y_name, w_name, h_name);
    }

    public Rectangle getNameBounds() {
        return _boundsName;
    }

    public boolean isMute() {
        return _mute;
    }

    public boolean isMustPaintSpeaker() {
        return _mustPaintSpeaker;
    }

    public void setMute(boolean _mute) {
        this._mute = _mute;
    }

    public void setMustPaintSpeaker(boolean _mustPaintSpeaker) {
        this._mustPaintSpeaker = _mustPaintSpeaker;
    }

    public Cards getCards() {
        return _cards;
    }

    public void setCards(Card[] crd) {
        _cards.clear();
        _cards.addCards(crd);
    }

    /**
     * Get a string representation of this Hand.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Name=").append(_name != null ? _name : "_")
        .append(", Cards=").append(_cards.toString())
        .append(", pos=").append(_position).append(", SEX=").append(_sex)
        .append(", roundBet=").append(_roundBet).append(", AmtAtTable=").append(_amtAtTable);
//        s.append("Name=" + _name != null ? _name : "_");
//        //s.append(", Avatar=" + _avatar);
//        s.append(", Cards=" + _cards.toString());
//        s.append(", pos=" + _position + ", SEX=" + _sex + ", roundBet=" + 
//                 _roundBet + ", AmtAtTable=" + _amtAtTable);
        return s.toString();
    }

    public boolean equals(ClientPlayerModel obj) {
        return _name == null ? 
               (obj == null || obj._name == null ? true : false) : 
               (obj == null || obj._name == null ? false : 
                _name.equals(obj._name));
    }

    public double getRoundBet() {
        return _roundBet;
    }

    public void setRoundBet(double worth) {
        this._roundBet = worth;
    }


    public boolean isActive(){
        return PlayerStatus.isActive(_status);
    }
    
    public boolean isFolded(){
        return PlayerStatus.isFolded(_status);
    }
    
    public boolean isAllIn(){
        return PlayerStatus.isAllIn(_status);
    }
    
    public boolean isNew(){
        return PlayerStatus.isNew(_status);
    }
    
//    public boolean isAnte(){
//        return PlayerStatus.isAnte(_status);
//    }
//    
//    public boolean isReconnected(){
//        return PlayerStatus.isReConnected(_status);
//    }
//    
    public boolean isDisconnected(){
        return PlayerStatus.isDisconnected(_status);
    }
//    
//    public boolean isWaitingForBlinds(){
//        return PlayerStatus.isWaitingForBlinds(_status);
//    }
    
    
    public boolean isOptOut(){
        return PlayerStatus.isOptOut(_status);
    }
    
    public boolean isSittingOut(){
        return PlayerStatus.isSittingOut(_status);
    }
   
    public  long getStatus() {
        return _status;
    }

  
    public void setStatus(long i) {
        _status = i;
    }

   
    public final String getPlayerName() {
        return _name;
    }
    
    public final int getPlayerPosition(){
        return _position;
    }
    
    public int setPlayerPosition(int pos){
        return _position = pos;
    }
    
    public int getPlayerRelPosition() {
		return _rel_pos;
	}

    public int setPlayerRelPosition(int pos){
        return _rel_pos = pos;
    }
    
	public final void setName(String _name) {
        this._name = _name;
    }
    
    public final String getAvatar() {
        return _avatar;
    }
    
   
    public final void setAvatar(String _av) {
        this._avatar = _av;
    }

   
    public final  String getCity() {
        return _city;
    }

   
    public final   void setCity(String _city) {
        this._city = _city;
    }

    /**
     * Gets the amount of player's chips on the table.
     */
    public final double getAmtAtTable() {
        return _amtAtTable;
    }

    /**
     * Gets the amount of player's chips on the table.
     */
    public final void setAmtAtTable(double chips) {
        this._amtAtTable = chips;
    }
  
    /**
     * Gets an user's _sex.
     * @return char - 'M' male, 'F' female.
     */
    public final char getSex() {
        return _sex;
    }

    /**
     * Sets an user's _sex.
     */
    public final void setSex(char _sex) {
        this._sex = _sex;
    }

    /**
     * Gets the fisrt all-in timestamp.
     */
    public final java.util.Date getAllInTimestamp() {
        return _allInTimestamp;
    }

    /**
     * Sets the fisrt all-in timestamp.
     */
    public final void setAllInTimestamp(java.util.Date _allInTimestamp) {
        this._allInTimestamp = _allInTimestamp;
    }

    /**
     * Gets the daily all-in count.
     */
    public final int getAllInCount() {
        return _allInCount;
    }

    /**
     * Sets the daily all-in count.
     */
    public final void setAllInCount(int newValue) {
        this._allInCount = newValue;
    }

    /**
     * Is the _state of this player a _state of player sitting at some table ?
     */
    public boolean isSitting() {
        return (PlayerStatus.isActive(_status) || 
                ((_status & (PlayerStatus.NEW)) > 0));
    }
}
