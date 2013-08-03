package com.poker.common.message;

import com.golconda.message.Command;

import java.util.HashMap;


public class CommandMove
    extends Command {
  int _move;
  String _tid;
  int _grid;
  int _pPos;
  int _pTeam;
  double _mAmt;
  String _moveDetails;

  public CommandMove(String session, int move, double amt, String tid) {
    super(session, Command.C_MOVE);
    _move = move;
    _mAmt = amt;
    _tid = tid;
  }

  public CommandMove(String session, int move, double amt, String tid, int grid) {
    super(session, Command.C_MOVE);
    _move = move;
    _mAmt = amt;
    _tid = tid;
    _grid = grid;
  }

  public CommandMove(String session, int move, double amt, String tid, int grid,
                     String move_det) {
    super(session, Command.C_MOVE);
    _move = move;
    _mAmt = amt;
    _tid = tid;
    _grid = grid;
    _moveDetails = move_det;
  }

  public CommandMove(HashMap str) {
    super(str);
    _tid =  (String) _hash.get("TID");
    _grid = Integer.parseInt( (String) (_hash.get("GRID") == null ? "-1" :
                                        _hash.get("GRID")));
    _moveDetails = (String) (_hash.get("MD") == null ? "" : _hash.get("MD"));
    _move = Integer.parseInt( (String) _hash.get("MV"));
    _pPos = Integer.parseInt( (String) _hash.get("MPOS"));
    _pTeam = Integer.parseInt( (String) _hash.get("MTEAM") == null ? "0" :
                              (String) _hash.get("MTEAM"));
    _mAmt = Double.parseDouble( (String) _hash.get("MAMT"));
  }

  public void setPlayerPosition(int i) {
    _pPos = i;
  }

  public void setPlayerTeam(int i) {
    _pTeam = i;
  }

  public int getMove() {
    return _move;
  }

  public double getMoveAmount() {
    return _mAmt;
  }

  public String getTableId() {
    return _tid;
  }

  public int getHandId() {
    return _grid;
  }

  public String getMoveDetails() {
    return _moveDetails;
  }

  public int getPlayerPosition() {
    return _pPos;
  }

  public int getPlayerTeam() {
    return _pTeam;
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&MV=").append(_move).append("&MAMT=").append(_mAmt)
        .append("&MPOS=").append(_pPos);
    str.append("&MTEAM=").append(_pTeam);
    str.append("&TID=").append(_tid);
    str.append(
        "&GRID=").append(_grid);
    str.append(
        "&MD=").append(_moveDetails);
    return str.toString();
  }

}
