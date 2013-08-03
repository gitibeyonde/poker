package com.poker.game.poker;


public class SitInResponse
    extends PokerResponse {

 //ERROR CODES
 // 6 SITIN NORMAL
 // -7 UNINVITED
 // -8 OCCUPIED
 // -9 WAITERS are waiting cannot join now
 // -11 sitting within 1/2 hour of leaving with less worth
 // 9  GAME CANNOT START
 // -10 BROKE SO CANNOT SIT
 // -78  TOURNY SITIN
 // -79 sitin failed as failed to save the fees to DB
  public SitInResponse(Poker g, int err_code) {
    super(g);
    _err_code=err_code;
    buf = new StringBuilder().append(miniHeader()).append(lastMoveDetails()).
        append(playerDetails());
    buf.append(potDetails()).append(communityCards());
    buf.append("next-move=-1|wait|").append(err_code).append(",");
    if (err_code==-7 || err_code==-8){
      setCommand(g.inquirer(), buf.toString());
    }else {
      broadcast(_allPlayers, buf.toString());
      for (int j = 0; j < _allPlayers.length; j++) {
        setCommand(_allPlayers[j],
                   playerTargetPosition(_allPlayers[j]).toString());
        if (g._inProgress) {
          setCommand(_allPlayers[j],
                     playerHandDetails(_allPlayers[j]).toString());

        }
      }
    }
  }

  public SitInResponse(Poker g, boolean flag) {
    super(g);
    buf = new StringBuilder().append(miniHeader()).append(lastMoveDetails()).
        append(playerDetails());
    broadcast(_allPlayers, buf.toString());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                 playerTargetPosition(_allPlayers[j]).toString());
    }
  }

  public boolean success(){
    return _err_code >=0;
  }

  int _err_code=0;
  StringBuilder buf;
}
