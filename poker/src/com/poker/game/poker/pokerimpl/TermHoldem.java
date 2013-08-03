package com.poker.game.poker.pokerimpl;

import com.agneya.util.Base64;
import com.agneya.util.Utils;

import com.golconda.db.DBException;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Cards;

import com.poker.common.db.GameRunSession;
import com.poker.common.message.ResponseString;
import com.poker.common.message.ResponseTableCloseOpen;
import com.poker.common.message.ResponseTableOpen;
import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.CollectABResponse;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.GameOverResponse;
import com.poker.game.poker.GameStartResponse;
import com.poker.game.poker.LeaveResponse;
import com.poker.game.poker.MessagingResponse;
import com.poker.game.poker.MoveResponse;
import com.poker.game.poker.Poker;
import com.poker.game.poker.Pot;
import com.poker.game.poker.SitInResponse;
import com.poker.game.poker.Poker.LastLeft;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;

import java.io.IOException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TermHoldem extends Holdem implements Runnable {

	// set the category for logging
	static Logger _cat = Logger.getLogger(TermHoldem.class.getName());

	public static TermPokerPool _tpp;
	public boolean _isBench = false;
	public boolean _isAvailable = true;

	static {
		_tpp = new TermPokerPool();
	}

	public String poolDetails() {
		_inquirer = null;
		return _tpp.getPoolDetails(this);
    }

	public String poolName() {
		return _name.substring(4);
	}

	public TermHoldem(String name, int minPlayers, int maxPlayers, int rake,
			double[] maxRake, String[] affiliate, String[] partner,
			Observer stateObserver) {
		super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
				stateObserver);
		_type = new PokerGameType(PokerGameType.Play_TermHoldem);
		_tpp.addTermPool(this);
	}

	public TermHoldem(String name, int minPlayers, int maxPlayers, int rake,
			double maxRake, String[] affiliate, String[] partner,
			Observer stateObserver) {
		super(name, minPlayers, maxPlayers, rake, maxRake, affiliate, partner,
				stateObserver);
		_type = new PokerGameType(PokerGameType.Play_TermHoldem);
		_tpp.addTermPool(this);
	}

	@Override
	public synchronized Response quickFold(Presence op) {
		if (op.isResponseReq() || this.allPlayers(0).length <= 2){ // player has a move already sent
			_cat.info("QF pending move " + op);
			return null;
		}
		op.addAddedChips(); // log the added chips if any
		double amount = op.getAmtAtTable();
		GamePlayer gp = (GamePlayer)op.player();
		
		// have this player leave the table now
		if (amount < _minBet * 2.00) {
			_cat.finest("QF Playe FOLDED due to less balance " + op);
			return fold(op);
		}
		
		// return this money to wallet
		op.leaveTable(new ModuleType(ModuleType.POKER));
		// set this presence to disconnected and quickFold
		op.setQickFold();
		op.buyIn(amount);
	
		_cat.info("QF Looking for another table === " + op);
		// look for another table !!! if this table will stop then don't move
		// create a presence on the new table
		TermHoldem th = _tpp.getBestTableToJoin(this, ((PokerPresence)op));
		// have this player join another table
		// create a new presence
		if (th != null) {
			int pos = th.getNextVacantPosition();
			if ( pos != -1){
				_cat.finest("QF position found on game " + pos);
				Presence pp = gp.createPresence(th._name);
				pp.setPos(pos);
				Response r = th.join(pp, amount);
				if (r.getBroadcast()==null || r.getBroadcast().length() < 10){
					_cat.warning("QF Join failed for player " + pp + " on table " + th);
					PokerPresence pvd[] = _table.allPlayers(0);
					for (PokerPresence pd: pvd){
						_cat.info("QF Players on table = "+ pd);
					}
				}
				// open the table
				gp.deliver(new ResponseTableCloseOpen(name(), th.name(), pp.pos(), r.getBroadcast()));
				_cat.finest("QF Player SEND CLOSE OPEN COMMAND " + pp);
				try {
					com.poker.server.GameProcessor.deliverResponse(th.start());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				th._isAvailable = true;
				return r;
			}
			else {
				ResponseString rs = new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_GAME_NOT_ALLOWED,
						com.golconda.message.Response.R_MOVE, "Unable to find a suitable table in the pool !");
				gp.deliver(rs);
				// set the game response in the clients out queue
				_cat.warning(op.name() + " QF No suitable position available");
				th._isAvailable = true;
				return null;
			}
		}
		else {
			ResponseString rs = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_POS_NOT_ALLOWED,
					com.golconda.message.Response.R_MOVE, "Unable to find a suitable pos on table !");
			gp.deliver(rs);
			// set the game response in the clients out queue
			_cat.warning(op.name() + " QF No suitable table available");
			return null;
		}
	}

	@Override
	public synchronized Response fold(Presence p) {
		if (p.isQickFold()){
			_cat.finest("QFQF start player quick folding on his move " + p);
			// call coming from proxy just fold 
			Response r =  super.fold(p);
			try {
				GameProcessor.deliverResponse(r);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// remove him	
			_cat.finest("QFQF Proxy quick FOLDS " + p);
			// send a message to the table that this player is leaving
			_inquirer = p;
			p.lastMove(PokerMoves.LEAVE);
			p.setGameEndWorth();
			p.leaveTable(new ModuleType(ModuleType.POKER));
			p.unsetResponseReq();
			p.unsetQickFold();
			_observers.remove(p);
			_waiters.remove(p);
			flushLastLeft(p);
			if (_currentPot != null) {
				_cat.finest("QFQF-Removing player form the live game " + p);
				_currentPot.addVal(p.currentRoundBet());
				p.resetRoundBet();
				_currentPot.remove(p);
				p.setRemoved();
			}
			

			GameRunSession grs = new GameRunSession(this.name(), this.grid(), this.type().intVal());
            grs.setEndTime(new Timestamp(System.currentTimeMillis()));
            grs.setStartTime(new Timestamp(this.startTime().getTimeInMillis()));
            //TODO Rake rounding
            double rake[] = Utils.integralDivide3Precs(this.rake(), _players == null ? 1 :_players.length);
            double winAmt = Utils.getRounded(p.getGameEndWorth() - p.getGameStartWorth() - p.getGameAddedChips());
            GamePlayer gp = (GamePlayer) p.player();
            grs.setDisplayName(gp.name());
            grs.setPosition(p.pos());
            grs.setPot(this.totalPot());
            grs.setStartWorth(p.getGameStartWorth());
            grs.setEndWorth(p.getGameEndWorth());
            grs.setWinAmount(winAmt);
            grs.setSessionId(gp.session());
            grs.setRake(rake[0]);
            try {
				grs.save();
			} catch (DBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Response lr = new LeaveResponse(this, p.pos());
			_table.remove(p);
			remove(p);
			p.player().removePresence(p);
			p.postRun();
			p.postWin();

			
			return lr;
		}
		
		
		((PokerPresence) p).resetHand();
		p.setFolded();
		// look for another table !!! if this table will stop then don't move
		p.addAddedChips(); // log the added chips if any
		double amount = p.getAmtAtTable();
		try {
			_cat.finest("Playe FOLDS " + p);
			// send a message to the table that this player is leaving
			_inquirer = p;
			p.lastMove(PokerMoves.LEAVE);
			p.setGameEndWorth();
			p.leaveTable(new ModuleType(ModuleType.POKER));
			p.unsetResponseReq();
			_observers.remove(p);
			_waiters.remove(p);
			flushLastLeft(p);
			if (_currentPot != null) {
				_cat.finest("Removing player form the live game");
				_currentPot.addVal(p.currentRoundBet());
				p.resetRoundBet();
				_currentPot.remove(p);
				p.setRemoved();
			}

			GameRunSession grs = new GameRunSession(this.name(), this.grid(), this.type().intVal());
            grs.setEndTime(new Timestamp(System.currentTimeMillis()));
            grs.setStartTime(new Timestamp(this.startTime().getTimeInMillis()));
            //TODO Rake rounding
            double rake[] = Utils.integralDivide3Precs(this.rake(), _players.length);
            double winAmt = Utils.getRounded(p.getGameEndWorth() - p.getGameStartWorth() - p.getGameAddedChips());
            GamePlayer gp = (GamePlayer) p.player();
            grs.setDisplayName(gp.name());
            grs.setPosition(p.pos());
            grs.setPot(this.totalPot());
            grs.setStartWorth(p.getGameStartWorth());
            grs.setEndWorth(p.getGameEndWorth());
            grs.setWinAmount(winAmt);
            grs.setSessionId(gp.session());
            grs.setRake(rake[0]);
            try {
				grs.save();
			} catch (DBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Response r = new LeaveResponse(this, p.pos());
			_table.remove(p);
			remove(p);
			p.player().removePresence(p);
			p.postRun();
			p.postWin();
			// make a p/grs entry
			
			com.poker.server.GameProcessor.deliverResponse(bettingResponse(p, 0, PokerMoves.FOLD));

			_cat.finest("Playe FOLDED " + p);
			// have this player leave the table now
			if (amount < _minBet * 2.00) {
				try {
					// have this player leave the table now
					com.poker.server.GameProcessor.deliverResponse(r);
				} catch (IOException ie) {
					ie.printStackTrace();
				}
				_cat.finest("Player moved to empty table due to less funds "
						+ p);
				((GamePlayer) (p.player()))
						.deliver(new com.poker.common.message.ResponseMessage(
								1, Base64.encodeString("You are moved to empty table because you ran out of money. You can join again !"),
								name(), "lobby"));
				// move the player to a empty table
				TermHoldem bth = _tpp.getBrokeWaitingTable(this);
				int pos = bth.getNextVacantPosition();
				_cat.finest("Most eligible game for bench found " + bth	+ " pos=" + pos);
				// have this player join another table
				// create a new presence
				Presence new_pp = p.player().createPresence(bth._name);
				new_pp.setPos(pos);
				Response rb = bth.join(new_pp, 0);
				if (r.getBroadcast()==null || r.getBroadcast().length() < 10){
					_cat.warning("FOLD Join failed for player " + new_pp + " on table " + bth);
					PokerPresence pvd[] = _table.allPlayers(0);
					for (PokerPresence pd: pvd){
						_cat.info(" Players on table = "+ pd);
					}
				}
				// open the table
				((GamePlayer) (p.player())).deliver(new ResponseTableCloseOpen(name(), bth.name(), new_pp.pos(), rb.getBroadcast()));
				((GamePlayer) (p.player()))
				.deliver(new com.poker.common.message.ResponseGetChipsIntoGame(
						com.golconda.message.Response.E_SUCCESS, bth.name(),0, p.player().playWorth(), 0, p.player().realWorth(),	0));
				return r;
			}
			_cat.finest("Playe MADE TO LEAVE TABLE " + p);
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	
		TermHoldem th = _tpp.getBestTableToJoin(this, ((PokerPresence)p));
		// have this player join another table
		// create a new presence
		_cat.finest("Most eligible game found " + th + ", for folded player=" + p);
		if (th != null) {
			int pos = th.getNextVacantPosition();
			if ( pos != -1){
				_cat.finest("position found on game " + pos);
				Presence pp = p.player().createPresence(th._name);
				pp.setPos(pos);
				Response r = th.join(pp, amount);
				if (r.getBroadcast()==null || r.getBroadcast().length() < 10){
					_cat.warning("FOLD Join failed for player " + pp + " on table " + th);
					PokerPresence pvd[] = _table.allPlayers(0);
					for (PokerPresence pd: pvd){
						_cat.info(" Players on table = "+ pd);
					}
				}
				// open the table
				((GamePlayer) (p.player())).deliver(new ResponseTableCloseOpen(	name(), th.name(), pp.pos(), r.getBroadcast()));
				try {
					com.poker.server.GameProcessor.deliverResponse(th.start());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				th._isAvailable = true;
				return r;
			}
			else {
				ResponseString rs = new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_GAME_NOT_ALLOWED,
						com.golconda.message.Response.R_MOVE, "Unable to find a suitable table in the pool !");
				((GamePlayer)p.player()).deliver(rs);
				// set the game response in the clients out queue
				_cat.warning(p.name() + " FOLD No suitable position available");
				th._isAvailable = true;
				return null;
			}
		}
		else {
			ResponseString rs = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_POS_NOT_ALLOWED,
					com.golconda.message.Response.R_MOVE, "Unable to find a suitable pos on table !");
			((GamePlayer)p.player()).deliver(rs);
			// set the game response in the clients out queue
			_cat.warning(p.name() + " FOLD No suitable table available");
			return null;
		}
	}

	public synchronized boolean movePlayerToActiveTable(Presence p, double amount) {
		_cat.finest("movePlayerToActiveTable---------- " + p);
		try {
			// have this player leave the table now
			com.poker.server.GameProcessor.deliverResponse(leaveTable(p));
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		_cat.finest("Player moved to active table " + p);
		((GamePlayer) (p.player()))
				.deliver(new com.poker.common.message.ResponseMessage(1, Base64
						.encodeString("You are moved to an active table !"),
						name(), "lobby"));
		
		// move the player to a empty table
		TermHoldem bth = _tpp.getBestTableToJoin(this, ((PokerPresence)p));
		if (bth != null){
			int pos = bth.getNextVacantPosition();
			if (pos != -1 ){
				_cat.finest("Most eligible active game found " + bth + " pos=" + pos);
				// have this player join another table
				// create a new presence
				Presence new_pp = p.player().createPresence(bth._name);
				new_pp.setPos(pos);
				Response rb = bth.join(new_pp, amount);
				if (rb.getBroadcast()==null || rb.getBroadcast().length() < 10){
					_cat.warning("Join failed for player " + new_pp + " on table " + bth);
					PokerPresence pvd[] = _table.allPlayers(0);
					for (PokerPresence pd: pvd){
						_cat.info(" Players on table = "+ pd);
					}
				}
				// open the table
				((GamePlayer) (p.player())).deliver(new ResponseTableCloseOpen(name(), bth.name(), new_pp.pos(), rb.getBroadcast()));
				try {
					com.poker.server.GameProcessor.deliverResponse(bth.start());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				bth._isAvailable = true;
				return true;
			}
			bth._isAvailable = true;
		}
		
		return false;
	}

	/*
	 * As a result of leaving, the marker does not change. Only current changes
	 */
	@Override
	public synchronized Response leave(Presence p, boolean timeout) {
		_inquirer = p;
		p.lastMove(PokerMoves.LEAVE);
		p.setGameEndWorth();
		p.unsetResponseReq();
		_observers.remove(p);
		_waiters.remove(p);
		flushLastLeft(p);

		Response r;

		if (maxBet() <= 0) {
			_last_left.add(new LastLeft(p, p.getAmtAtTable()));
		}

		if (!validatePresenceInGame(p.name())) {
			_table.remove(p);
			_cat.finest("Removing player not in game " + p);
			return new LeaveResponse(this, p.pos());
		}

		_cat.info("Next move player " + this._nextMovePlayer);
		if (p.equals(this._nextMovePlayer) && _inProgress == true) {
			_cat.info("This is the next move player " + p);
			// If the left player was supposed to make a move
			if ((PokerMoves.FOLD & this._nextMove) > 0) {
				/**
				 * if (timeout && ((PokerPresence)p).isAllInAvailable()) {
				 * p.setDisconnected(); r = allInDisconnect(p, 0); // do a
				 * all-in instead of fold _cat.info("Player did a ALLIN 0 " +
				 * p); } else {
				 **/
				p.setFolded();
				((PokerPresence) p).resetHand();
				r = bettingResponse(p, 0, PokerMoves.FOLD);
				_table.remove(p);
				remove(p);
				// }
			} else {
				_cat.fine("Unrecognized move " + new PokerMoves(_nextMove).stringValue());
				throw new IllegalStateException("Unrecognized move at leave " + new PokerMoves(_nextMove).stringValue());
				// r = new LeaveResponse(this, p.pos());
			}
			return r;
		}
		if (_inProgress) {
			if (_currentPot != null) {
				// _cat.finest("Removing player form the live game");
				_currentPot.addVal(p.currentRoundBet());
				p.resetRoundBet();
				_currentPot.remove(p);
				p.setRemoved();
			}
			PokerPresence[] v = (PokerPresence[]) activePlayers();
			if (v == null) {
				return new GameDetailsResponse(this);
			}
			if (v.length < 2) {
				_inProgress = false;
				if (abOver() && ((PokerPresence) p).getHand().cardCount() > 0
						&& _currentPot != null) {
					_cat.info("Leaving blinds over " + p);
					procRoundOver();
				} else {
					// blinds are not over or cards are not distributed
					// return the bets and stop the game
					for (int i = 0; i < v.length; i++) {
						v[i].returnRoundBet();
						v[i].unsetResponseReq();
						_cat.info("Returning bets blind not over " + v[i]);
					}
				}
				r = gameOverResponse(p);
			} else {
				r = new LeaveResponse(this, p.pos());
			}
			_table.remove(p);
			remove(p);
			return r;
		} // In active
		else if (type().isRegularGame()) { // game is not in progress
			gameCleanup();
			_table.remove(p);
			remove(p);
			_cat.finest("Removing player game not in progress");
			return new LeaveResponse(this, p.pos());
		} else {
			// tourny..
			// _cat.finest("Tourny presence keeping it " + p);
			return new LeaveResponse(this, p.pos());
		}
	}

	private synchronized Response leaveTable(Presence p) {
		_inquirer = p;
		p.lastMove(PokerMoves.LEAVE);
		p.setGameEndWorth();
		p.leaveTable(new ModuleType(ModuleType.POKER));
		p.unsetResponseReq();
		_observers.remove(p);
		_waiters.remove(p);
		flushLastLeft(p);

		Response r;

		if (!validatePresenceInGame(p.name())) {
			_table.remove(p);
			_cat.finest("Removing player not in game " + p);
			return new LeaveResponse(this, p.pos());
		}

		_cat.info("Next move player " + this._nextMovePlayer);

		if (_inProgress) {
			if (_currentPot != null) {
				_cat.finest("Removing player form the live game");
				_currentPot.addVal(p.currentRoundBet());
				p.resetRoundBet();
				_currentPot.remove(p);
				p.setRemoved();
			}
			PokerPresence[] v = (PokerPresence[]) activePlayersIncludingAllIns();
			if (v == null) {
				return new GameDetailsResponse(this);
			}
			if (v.length < 2) {
				_inProgress = false;
				if (abOver() && ((PokerPresence) p).getHand().cardCount() > 0
						&& _currentPot != null) {
					_cat.info("Leaving blinds over " + p);
					procRoundOver();
				} else {
					// blinds are not over or cards are not distributed
					// return the bets and stop the game
					for (int i = 0; i < v.length; i++) {
						v[i].returnRoundBet();
						v[i].unsetResponseReq();
						_cat.info("Returning bets blind not over " + v[i]);
					}
				}
				r = gameOverResponse(p);
			} else {
				r = new LeaveResponse(this, p.pos());
			}
			_table.remove(p);
			remove(p);
			return r;
		} // In active
		else if (type().isRegularGame()) { // game is not in progress
			gameCleanup();
			_table.remove(p);
			remove(p);
			_cat.finest("Removing player game not in progress");
			return new LeaveResponse(this, p.pos());
		} else {
			// tourny..
			_cat.finest("Tourny presence keeping it " + p);
			return new LeaveResponse(this, p.pos());
		}
	}

	/*
	 * PokerPresence joins a table. May or may not play a poker
	 */
	@Override
	public synchronized Response join(Presence p, double amount) {
		_cat.finest("Joining  " + p);
		_inquirer = p;
		if (!_table.join(p)) { // couldnt be added for whatever reason
			p.lastMove(PokerMoves.JOIN_FAILED);
			_cat.severe("Join failed for "+ p);
			return new SitInResponse(this, -8);
		}

		_observers.remove(p);
		p.unsetNew();
		p.resetPlayerForNewGame();
		p.unsetSitOutNextGame();
		p.unsetSittingOut();
		p.unsetRemoved();
		p.unsetMissedBB();
		p.unsetMissedSB();
		p.unsetBetweenBlinds();
		p.unsetDisconnected();
		p.unsetQickFold();
		p.resetIdleGC();
		p.lastMove(PokerMoves.JOIN);
		p.setPlayer();
		p.setGRID(_grid);
		p.joinTable(amount, new ModuleType(ModuleType.POKER));
		p.unsetBroke();
		setCurrent(p);
		_cat.finest("Joined " + p);
		if (_inProgress || !reRunCondition()) {
			_cat.finest(_inProgress + ", ReRun = " + reRunCondition());
			return new SitInResponse(this, _inProgress ? 12 : 9);
		} else {
			return start();
		}
	}

	// @todo : winners on a per pot basis
	@Override
	public Response gameOverResponse(Presence p) {
		_isAvailable = false;
		_cat.finest("Entering game over response " + p);
		declarePotWinners();
		Response ra = new GameOverResponse(this); // / for loggin winner
		_cat.finest("gameOverResponse " + ra.getBroadcast());
		postRun();
		_inProgress = false;
		prepareForNewRun(); // if the game can be started else mark all as new
		Response r = new GameStartResponse(this);
		postWin();
		try {
			com.poker.server.GameProcessor.deliverResponse(r);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Thread t = new Thread(this);
		t.start();

		return null;
	}

	public void run() {
		try {
			Thread.currentThread().sleep(8000);
		} catch (Exception ei) {
			ei.printStackTrace();
		}

		PokerPresence pv[] = _table.allPlayers(0);
		for (PokerPresence pp : pv) {
			if (pp.isBroke()) {
				try {
					// have this player leave the table now
					com.poker.server.GameProcessor.deliverResponse(leaveTable(pp));
				} catch (IOException ie) {
					ie.printStackTrace();
				}
				_cat.finest("Player moved to empty table due to less funds " + pp);
				((GamePlayer) (pp.player()))
						.deliver(new com.poker.common.message.ResponseMessage(1, 
								Base64.encodeString("You are moved to empty table because you ran out of money. You can join again !"),
								name(), "lobby"));
				// move the player to a empty table
				TermHoldem bth = _tpp.getBrokeWaitingTable(this);
				int pos = bth.getNextVacantPosition();
				_cat.finest("Most eligible game for bench found " + bth	+ " pos=" + pos);
				// have this player join another table
				// create a new presence
				Presence new_pp = pp.player().createPresence(bth._name);
				new_pp.setPos(pos);
				Response rb = bth.join(new_pp, 0);
				if (rb.getBroadcast()==null || rb.getBroadcast().length() < 10){
					_cat.warning("Join failed for player " + new_pp + " on table " + bth);
					PokerPresence pvd[] = _table.allPlayers(0);
					for (PokerPresence pd: pvd){
						_cat.info(" Players on table = "+ pd);
					}
				}
				// open the table
				((GamePlayer) (pp.player())).deliver(new ResponseTableCloseOpen(name(), bth.name(),new_pp.pos(), rb.getBroadcast()));
				((GamePlayer) (pp.player()))
				.deliver(new com.poker.common.message.ResponseGetChipsIntoGame(
						com.golconda.message.Response.E_SUCCESS, bth.name(),0, pp.player().playWorth(), 0, pp.player().realWorth(),	0));
			}
		}

		// win message sent
		// check if less than max player are left
		pv = _table.eligiblePlayers();
		if (pv.length < this._minPlayers) {
			// move all the players
			_cat.finest(" No of players =" + pv.length);
			for (PokerPresence pp : pv) {
				_cat.finest(" Moving players =" + pp);
				// look for another table !!! if this table will stop then don't
				// move
				TermHoldem th = _tpp.getBestTableToJoin(this, pp);
				if (th != null) {
					int pos = th.getNextVacantPosition();
					if (pos != -1){
						_cat.finest("Most eligible game found " + th + " for game over player=" + pp);
						pp.addAddedChips(); // log the added chips if any
						double amount = pp.getAmtAtTable();
						try {
							// have this player leave the table now
							com.poker.server.GameProcessor.deliverResponse(leaveTable(pp));
						} catch (IOException ie) {
							ie.printStackTrace();
						}
						// have this player join another table
						// create a new presence
						if (amount < 2 * minBet()){
							th._isAvailable = true;
							continue;
						}
						Presence new_pp = pp.player().createPresence(th._name);
						new_pp.setPos(pos);
						Response rb = th.join(new_pp, amount);
						if (rb.getBroadcast()==null || rb.getBroadcast().length() < 10){
							_cat.warning("Join failed for player " + new_pp + " on table " + th);
							PokerPresence pvd[] = _table.allPlayers(0);
							for (PokerPresence pd: pvd){
								_cat.info(" Players on table = "+ pd);
							}
						}
						// open the table
						((GamePlayer) (pp.player())).deliver(new ResponseTableCloseOpen(name(), th.name(), new_pp.pos(), rb.getBroadcast()));
					}
					else {
						ResponseString rs = new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_GAME_NOT_ALLOWED,
								com.golconda.message.Response.R_MOVE, "Unable to find a suitable table in the pool !");
						((GamePlayer)pp.player()).deliver(rs);
						// set the game response in the clients out queue
						_cat.warning(pp.name() + " No suitable position available");
					}
					_cat.finest("Game Over Trying to start " + th);
					try {
						com.poker.server.GameProcessor.deliverResponse(th.start());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					th._isAvailable = true;
				}
				else {
					ResponseString rs = new com.poker.common.message.ResponseString(
							com.golconda.message.Response.E_POS_NOT_ALLOWED,
							com.golconda.message.Response.R_MOVE, "Unable to find a suitable pos on table !");
					((GamePlayer)pp.player()).deliver(rs);
					// set the game response in the clients out queue
					_cat.warning(pp.name() + " No suitable table available");
				}
			}
		}
		_isAvailable = true;
	}

}
