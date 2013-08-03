package com.poker.game.poker.pokerimpl;

import com.agneya.util.Base64;
import com.agneya.util.Utils;

import com.golconda.db.DBException;
import com.golconda.db.DBGame;
import com.golconda.db.DBPlayer;
import com.golconda.db.GameSequence;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.GameStateEvent;
import com.golconda.game.Player;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Cards;
import com.golconda.game.util.MyDeck;

import com.poker.common.interfaces.SitnGoInterface;
import com.poker.common.interfaces.TournyInterface;
import com.poker.common.interfaces.TournyTableInterface;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponseString;
import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.CollectABResponse;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.GameOverResponse;
import com.poker.game.poker.GameStartResponse;
import com.poker.game.poker.IllegalReqResponse;
import com.poker.game.poker.LeaveResponse;
import com.poker.game.poker.MessagingResponse;
import com.poker.game.poker.MoveResponse;
import com.poker.game.poker.Pot;
import com.poker.game.poker.SitInResponse;
import com.poker.game.poker.Table;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoldemSitnGo extends Holdem implements SitnGoInterface, Runnable,
		TournyTableInterface, TournyInterface {
	// set the category for logging
	static Logger _cat = Logger.getLogger(HoldemSitnGo.class.getName());

	@Override
	public Response start() {
		_state = HAND_RUNNING;
		PokerPresence[] pl; // in a resume of a poker, there might be players
							// with insufficient funds
		pl = _table.eligiblePlayers();
		// check if the players
		_cat.finest("Player count =" + pl.length);
		if (pl.length == _maxPlayers) { // maxPlayers ==8/10
			_inProgress = true; // @todo : cases where inprogress will be false.
			_currentPot = null;
			prepareForNewRun();
			_tournyId = _grid + "_" + _name;
			_cat.finest("STARTING " + name() + " grid =" + _grid);
			// register all
			update(this, GameStateEvent.SITNGO_START);
			
			_thread_state = this.THREAD_START;
			Thread t = new Thread(this);
			t.start();
			_cat.finest("The tournament is going to start in 1 minute !");

			return new GameDetailsResponse(this);
		} else {
			_inProgress = false;
			return new GameDetailsResponse(this);
		}
	}

	public HoldemSitnGo(String name, int minPlayers, int maxPlayers, int limit,
			int tourbo, String[] affiliate, Observer stateObserver) {
		super(name, minPlayers, maxPlayers, 0, 0, affiliate, null,
				stateObserver);
		_type = new PokerGameType(PokerGameType.HoldemSitnGo);
		_limit = limit;
		_tourbo = tourbo;
		_keepRunning = true;
		_inProgress = false;
		_winner = new Vector();
		_pre_winner = new Vector();
		_hand = 0;
		_hand_level = 0;
		this._grid = 0;
		_table = new Table(name(), _maxPlayers, this);
		_players = null;
		resetPartners();
		_currentPot = null;
		prepareForNewRun();
		_state = TABLE_OPEN;
		_tournyId = "";
	}

	@Override
	public void prepareForNewRun() {
		setupNewRun();
		_blindsPosted = false;
	}

	@Override
	public void setupNewRun() {
		_table.markEligiblesActive(_minBet);
		_players = _table.eligiblePlayers();
		for (int i = 0; i < _players.length; i++) {
			_cat.finest("Eligible " + _players[i]);
		}

		if (_players.length < _minPlayers) {
			_inProgress = false;
			return;
		}
		_flop[_flopIndex] = -1;
		_deck = new MyDeck(false);
		_communityCards = new Cards(false);
		_bettingRound = R_PREFLOP;
		_raiseCount = 0;
		_loops = 0;
		_currentRoundBet = 0;
		_inquirer = null;

		_table.setDealer();
		_cat.finest("Dealer = " + dealer());


		_table.setAB();
		setMarker(_table.dealer());
		_cat.finest("Setting up new run dealer = " + _table.dealer());
		
		// what about dealer designation ?
		update(this, GameStateEvent.GAME_BEGIN);
		
		
		if (_currentPot == null) {
			_pots = new ArrayList();
			_currentPot = new Pot("main", 0, 0, this);
			_pots.add(_currentPot);
			for (int i = 0; _players != null && i < _players.length; i++) {
				_currentPot.addContender(_players[i], 0); // only potential
															// contender. No
															// more.
				_cat.finest("Setup new Run Adding contenders " + _players[i]);
			    _players[i].gameStart(_grid);
			}
		}

		
		if (_keepRunning) {
			_inProgress = true;
		}
	}

	public void run() {
		if (_thread_state == this.THREAD_START) {
			_cat.finest("STARTING GAME sending broadcast start message.");

			try {
				Thread.currentThread().sleep(START_TIME1);
			} catch (InterruptedException e) {
				// ignore
			}
			try {
				com.poker.server.GameProcessor
						.deliverMessageResponse(new MessagingResponse(
								this,
								Base64
										.encodeString("Tournament starts in < 1 min !")));
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Thread.currentThread().sleep(START_TIME2);
			} catch (InterruptedException e) {
				// ignore
			}
			_time = System.currentTimeMillis();
			_cat.finest("STARTING GAME sending ABResponse");
			try {
				com.poker.server.GameProcessor
						.deliverResponse(new CollectABResponse(this));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (_thread_state == this.THREAD_REFRESH) {
			_cat.info("THREAD_REFRESH THREAD START------------------------------");
			_table.postRun();
			try {
				Thread.currentThread().sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				// ignore
			}
			// SEND THE WIN MESSAGE
			try {
				GameProcessor.deliverResponse(new GameStartResponse(this));
			} catch (Exception e) {

			}
			try {
				Thread.currentThread().sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				// ignore
			}

			_cat.info("THREAD_REFRESH SENDING BROADCAST------------------------------");
			_cat.finest("Pre Refreshing table ...");
			// send messages to eliminated players
			for (Iterator<PokerPresence> i = _pre_winner.iterator(); i
					.hasNext();) {
				PokerPresence pr = i.next();
				_winner.add(pr);
				Player p = (Player) pr.player();
				if (p instanceof GamePlayer) {
					GamePlayer gp = (GamePlayer) p;
					_cat.finest("SENDING WIN  MESSAGE TO "
									+ ("Congratulation "
											+ gp.name()
											+ " ! Your position is "
											+ (_maxPlayers - _winner.size() + 1)
											+ " among " + _maxPlayers + " players. The game logs will be mailed to you."));
					ResponseMessage rm = new ResponseMessage(
							1,
							Base64
									.encodeString("Congratulation "
											+ gp.name()
											+ " ! Your position is "
											+ (_maxPlayers - _winner.size() + 1)
											+ " among "
											+ _maxPlayers
											+ " players. The game logs will be mailed to you."),
							name(), "broadcast");
					gp.deliver(rm);
				}
			}
			_pre_winner.clear();

			update(this, GameStateEvent.SITNGO_OVER);
			
			try {
				Thread.currentThread().sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				// ignore
			}

			_left.clear();
			_communityCards = new Cards(false);
			_pots = new ArrayList();
			_currentPot = new Pot("main", 0, 0, this);
			_pots.add(_currentPot);

			for (int k = 0; _players != null && k < _players.length; k++) {
				_players[k].resetHand();
			}

			for (int k = 0; _players != null && k < _players.length; k++) {
				this.setInquirer(_players[k]);
				setCurrent(_players[k]);
				// update all the players/observer
				_players[k].lastMove(PokerMoves.LEAVE);
				SitInResponse gdr = new SitInResponse(this, -78);
				try {
					GameProcessor.deliverResponse(gdr);
				} catch (Exception e) {
				}
				_players[k].lastMove(PokerMoves.NONE);
			}
			try {
				Thread.currentThread().sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				// ignore
			}
			_cat.info("THREAD_REFRESH TABLE CLOSE------------------------------");

			ResponseString gdr = new ResponseString(1,
					com.golconda.message.Response.R_TABLE_CLOSED, name());
			for (int k = 0; _players != null && k < _players.length; k++) {
				((GamePlayer) _players[k].player()).deliver(gdr);
			}
			_state = TABLE_CLOSED;
			_table.postWinTourny();
			_observers.clear();
			
			try {
				Thread.currentThread().sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				// ignore
			}
			_cat.info("THREAD_REFRESH THREAD START  REFRESH TABLE------------------------------");
			_cat.finest("Refreshing table ...");
			_keepRunning = true;
			_inProgress = false;
			_winner = new Vector();
			_pre_winner = new Vector();
			_hand = 0;
			_hand_level = 0;
			this._grid = 0;
			setArgs(_buyIn, _fees, _chips, _don);

			_table = new Table(name(), _maxPlayers, this);
			resetPartners();
			_currentPot = null;
			prepareForNewRun();
			_state = TABLE_OPEN;

			_cat.info("THREAD_REFRESH THREAD START  REFRESH TABLE END------------------------------");
			_inProgress = false;
		}
	}

	// @todo : winners on a per pot basis
	@Override
	public Response gameOverResponse(Presence p) {
		_cat.warning("GAME OVER " + name());
		declarePotWinners();
		new GameOverResponse(this); // log winner
		update(this, GameStateEvent.GAME_OVER);
		_cat.finest("SITNGO GAME OVER -----------------------------POSTWIN");
		// get contenders from the main pot, "side-max"
		PokerPresence[] v = ((Pot) _pots.get(0)).contenders();

		for (int i = v.length - 1; i >= 0; i--) {
			_cat.finest("Refresh " + v[i]);
			if (v[i].getAmtAtTable() <= 0) {
				_cat.finest("BROKE " + v[i]);
				_pre_winner.add(v[i]);
			}
		}
		_table.markEligiblesActive(_minBet);

		// send messages to these players

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.currentThread().sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					// ignore
				}
				// send messages to eliminated players
				for (Iterator<PokerPresence> i = _pre_winner.iterator(); i
						.hasNext();) {
					PokerPresence pr = i.next();
					_winner.add(pr);
					Player p = (Player) pr.player();
					if (p instanceof GamePlayer) {
						GamePlayer gp = (GamePlayer) p;
						_cat.finest("SENDING WIN  MESSAGE TO "
										+ ("Congratulation "
												+ gp.name()
												+ " ! Your position is "
												+ (_maxPlayers - _winner.size() + 1)
												+ " among " + _maxPlayers + " players. The game logs will be mailed to you."));
						ResponseMessage rm = new ResponseMessage(
								1,
								Base64
										.encodeString("Congratulation "
												+ gp.name()
												+ " ! Your position is "
												+ (_maxPlayers - _winner.size() + 1)
												+ " among "
												+ _maxPlayers
												+ " players. The game logs will be mailed to you."),
								name(), "broadcast");
						gp.deliver(rm);
					}
				}

				_pre_winner.clear();
			}
		});

		PokerPresence wv[] = _table.eligiblePlayers();
		if (wv.length <= 1) {
			_cat.finest("TOURNAMENT OVER  " + this);
			if (wv.length == 1) {
				PokerPresence winner = wv[0];
				_pre_winner.add(winner);
			}
			
			_thread_state = this.THREAD_REFRESH;
			Thread t1 = new Thread(this);
			t1.start();

			_cat.finest("Thread started ..." + System.currentTimeMillis());
			return null;
		} // TOURNAMENT OVER
		else {
			_hand++;
			boolean _level_changed = false;
			if (_tourbo == TOURBO_HYPER
					&& time() > _hand_level * TOURBO_HYPER_TIME) {
				_hand_level++;
				_level_changed = true;
			} else if (_tourbo == TOURBO_TOURBO
					&& time() > _hand_level * TOURBO_TOURBO_TIME) {
				_hand_level++;
				_level_changed = true;
			} else if (time() > _hand_level * TOURBO_NORMAL_TIME) {
				_hand_level++;
				_level_changed = true;
			}
			_table.commitTotalBet(_players);
			_table.postRun();
			// set the stakes
			if (_hand_level > _level_limits.length - 1) {
				_hand_level = _level_limits.length - 1;
			}
			this._minBet = _level_limits[_hand_level][0];
			if (_limit >= 1) {
				this._maxBet = _level_limits[_hand_level][1];
			} else {
				this._maxBet = _limit;
			}

			double sb[] = Utils.integralDivide(_minBet, 2);
			this.smallBlind = sb[1];
			this.bigBlind = (int) _minBet;
			_cat.finest("HOLDEM START " + _minBet + ", " + _maxBet + ", "
					+ smallBlind + ", " + bigBlind);

			if (_level_changed) {
				try { // New Level: 20/40 - Limit Holdem
					String lh = "";
					if (_limit == -1) {
						lh = " NL Holdem";
					} else if (_limit == 0) {
						lh = " PL Holdem";
					} else {
						lh = " Limit Holdem";
					}
					com.poker.server.GameProcessor
							.deliverMessageResponse(new MessagingResponse(this,
									Base64.encodeString("New Level "
											+ smallBlind + "/" + bigBlind + lh)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			prepareForNewRun(); // if the game can be started else mark all as
								// new
			Response r = new GameStartResponse(this);
			postWin();
			return r;
		} // TOURNY NOT OVER

	}

	public void setArgs(double buyIn, double fees, double chips, boolean don) {
		this._minBet = _level_limits[_hand_level][0];
		if (_limit >= 1) {
			this._maxBet = _level_limits[_hand_level][1];
		} else {
			this._maxBet = _limit;
		}
		this.smallBlind = (int) _minBet / 2;
		this.bigBlind = (int) _minBet;
		this._maxRounds = 4;
		_buyIn = buyIn;
		_fees = fees;
		_chips = chips;
		_don = don;
		_cat.finest(this.toString());
	}

	@Override
	public Response allIn(Presence p, double amt) {
		p.setAllIn();
		p.setShowdown();
		// check if this all-in is during blinds
		if (p.isSB() && !p.isABPosted()) {
			// during SB
			post(p, amt, PokerMoves.ALL_IN);
			_cat.finest("Allin during SB");
			p.setABPosted();
			return new CollectABResponse(this);
		} else if (p.isBB() && !p.isABPosted()) {
			post(p, amt, PokerMoves.ALL_IN);
			p.setABPosted();
			_cat.finest("Allin during BB");
			_blindsPosted = true;
			if (abOver()) {
				_cat.finest("Blinds Over");
				return abOverResponse();
			} else {
				return new CollectABResponse(this);
			}
		}
		_cat.finest("Allin == " + p);
		return bettingResponse(p, amt, PokerMoves.ALL_IN);
	}

	@Override
	public Response abOverResponse() {
		PokerPresence[] active = activePlayersForMove(dealer().pos());
		PokerPresence[] apiain = activePlayersIncludingAllIns(dealer().pos());
		PokerPresence[] aip = allInPlayers(dealer().pos());

		double allInRefVal = 0;
		for (int k = 0; aip != null && k < aip.length; k++) {
			if (aip[k].currentRoundBet() > allInRefVal) {
				allInRefVal = aip[k].currentRoundBet();
				_cat.finest(" Max AllIn =" + allInRefVal);
			}
		}
		double refVal = 999999999999999999L;
		for (int k = 0; k < active.length; k++) {
			if (active[k].currentRoundBet() < refVal) {
				refVal = active[k].currentRoundBet();
				_cat.finest(" Min Bet Ref Val =" + refVal);
			}
		}

		// special case when all but one or all players are all in and game
		// cannot proceed
		if (apiain.length >= 2 && active.length <= 1
				&& ((refVal - allInRefVal) > -0.001)) {
			_cat
					.info("Special case when all but one or all players are all in");
			// check if the active player has more bet than all-in players
			procRoundOver();
			if (apiain[0].getHand().cardCount() == 0) {
				// deal hand card ASSUMES THE GAME IS HOLDEM
				dealCloseCards(activePlayersIncludingAllIns(), 2);
				try {
					GameProcessor.deliverResponse(new GameDetailsResponse(this, false));
				} catch (Exception e) {
					// ignore
				}

			}
			while (_bettingRound < (_maxRounds - 1)) {
				initNextBettingRound();
				try {
					GameProcessor.deliverResponse(new GameDetailsResponse(this, false));
				} catch (Exception e) {
					// ignore
				}

			}
			return gameOverResponse(_inquirer);
		}

		PokerPresence[] ap = activePlayers();
		if (ap == null) {
			return new GameDetailsResponse(this);
		}

		_cat.finest("Blinds-over");
		dealCloseCards((PokerPresence[]) activePlayersIncludingAllIns(), 2);
		return new MoveResponse(this);
	}

	/*
	 * PokerPresence joins a table. May or may not play a poker
	 */
	@Override
	public synchronized Response join(Presence p, double amount) {
		_cat.finest(_inProgress + ", " + _state + "Joining  " + p);
		_inquirer = p;
		if (_inProgress || _state == TABLE_CLOSED) {
			_cat.finest("TABLE_CLOSED");
			return new IllegalReqResponse(this);
		}
		// check if there is a waiting list
		if (_waiters.contains(p)) {
			_cat.warning("Waiter joins " + p);
			_waiters.remove(p);
		}
		if (!isInvited(p.name())) {
			p.lastMove(PokerMoves.NONE);
			return new SitInResponse(this, -7);
		}
		if (!_table.join(p)) { // couldnt be added for whatever reason
			p.lastMove(PokerMoves.NONE);
			return new GameDetailsResponse(this);
		}
		_observers.remove(p);
		p.setNew();
		p.resetPlayerForNewGame();
		p.unsetSitOutNextGame();
		p.unsetSittingOut();
		p.unsetRemoved();
		p.unsetMissedBB();
		p.unsetMissedSB();
		p.unsetBetweenBlinds();
		p.unsetDisconnected();
		p.resetIdleGC();
		p.lastMove(PokerMoves.JOIN);
		p.setPlayer();
		p.setGRID(_grid);
		p.lastMove(PokerMoves.JOIN);
		p.setPlayer();
		p.setSNGPresence(name());
		setCurrent(p);
		_cat.finest("Joined " + p);
		// deduct buyin+fees
		try {
			// deduct buyin+fees
			DBPlayer plr = p.player().getDBPlayer();
			plr.buyPlaySnGChips(p.player().session(), _fees + _buyIn, name(),
					type().toString());
		} catch (Exception dbe) {
			_cat.log(Level.WARNING, "SitnGoTourny " + this + " failed to join "
					+ p, dbe);
			p.lastMove(PokerMoves.NONE);
			return new SitInResponse(this, -79);
		}
		p.joinTable(chips(), new ModuleType(ModuleType.POKER));
		p.unsetBroke();

		if (!reRunCondition()) {
			_cat.finest("Not enough player " + _table.eligiblePlayers().length);
			return new SitInResponse(this, _inProgress ? 12 : 9);
		} else {
			return start();
		}
	}

	// a player cannot leave a tourny
	@Override
	public synchronized Response leave(Presence p, boolean timeout) {
		p.setDisconnected();
		_cat.finest("Leaving Tourny " + p);
		Response r = new LeaveResponse(this, p.pos());
		if (p.equals(this._nextMovePlayer) && _inProgress == true && p.isResponseReq()) {
			_cat.info("This is the next move player " + p);
			if ((PokerMoves.CHECK & this._nextMove) > 0) {
				_cat.info("Player checked " + p);
				r = check(p);
			} else if ((PokerMoves.FOLD & this._nextMove) > 0) {
				_cat.info("Player folded " + p);
				r = fold(p);
			} else if ((PokerMoves.SMALL_BLIND & this._nextMove) > 0) {
				_cat.info("Player small blind " + p);
				r = postSmallBlind(p, smallBlind);
			} else if ((PokerMoves.BIG_BLIND & this._nextMove) > 0) {
				_cat.info("Player big blind " + p);
				r = postBigBlind(p, bigBlind);
			} else if ((PokerMoves.ALL_IN & this._nextMove) > 0) {
				_cat.info("Player all-in " + p);
				r = allIn(p, bigBlind);
			}
			return r;
		} else if (_state == TABLE_OPEN || _state == TABLE_CLOSED) {
			if (!p.isRemoved()) {
				// return money
				try {
					// deduct buyin+fees
					DBPlayer plr = p.player().getDBPlayer();
					plr.returnPlaySnGChips(p.player().session(),
							_fees + _buyIn, name(), type().toString());
				} catch (Exception dbe) {
					_cat.log(Level.WARNING, "SitnGoTourny " + this
							+ " failed to join " + p, dbe);
					p.lastMove(PokerMoves.NONE);
					return new SitInResponse(this, -79);
				}
				_cat.finest("Removing player ");
				p.lastMove(PokerMoves.LEAVE);
				p.unsetResponseReq();
				_observers.remove(p);
				_waiters.remove(p);
				_table.remove(p);
				remove(p);
				p.setRemoved();
			}
		}
		return null;
	}

	@Override
	public boolean reRunCondition() {
		return _keepRunning
				&& (_table.eligiblePlayers().length == _maxPlayers || _inProgress);
		// any other validations
	}

	@Override
	public void postWin() {
		_table.postWinTourny();
		_left.clear();
		_communityCards = new Cards(false);
		_pots = new ArrayList();
		_currentPot = new Pot("main", 0, 0, this);
		_pots.add(_currentPot);
		for (int i = 0; _players != null && i < _players.length; i++) {
			_currentPot.addContender(_players[i], 0); // only potential
														// contender. No more.
			_cat.finest("Setup new Run Adding contenders " + _players[i]);
		}
	}

	@Override
	public void attachWinners(Pot p) {
		_contenders = (PokerPresence[]) p.contenders();
		if (_contenders.length == 0) {
			_cat.finest("No contenders");
			return;
		}
		if (_contenders.length == 1) {
			p.addHighWinners(_contenders[0], p.getVal(), 0L);
			_cat.finest("Single contenders " + _contenders[0]);
			return;
		}

		// sort the all-in players in descending order of their hand strength
		java.util.Arrays.sort(_contenders, new Comparator() {
			public int compare(Object o1, Object o2) {
				return (int) HandComparator.compareGameHand(
						((PokerPresence) o2).getHand().getCards(),
						((PokerPresence) o1).getHand().getCards(),
						communityCards(), PokerGameType.HOLDEM, true)[0];
			}
		});

		Vector winner = new Vector();
		_contenders[0].setShowdown();
		winner.add(_contenders[0]);
		for (int i = 0; i < _contenders.length - 1; i++) {
			if (HandComparator.compareGameHand(_contenders[i].getHand()
					.getCards(), _contenders[i + 1].getHand().getCards(),
					communityCards(), PokerGameType.HOLDEM, true)[0] != 0L) {
				break;
			} else {
				_contenders[i + 1].setShowdown();
				winner.add(_contenders[i + 1]);
			}
		}
		for (int i = 0; i < winner.size(); i++) {
			_cat.finest("Winner = " + winner.get(i));
		}
		double hwin[] = Utils.integralDivide(p.getVal(), winner.size());
		for (int i = 0; i < winner.size(); i++) {
			PokerPresence pw = (PokerPresence) winner.get(i);
			p.addHighWinners(pw, hwin[i], HandComparator.bestHandOf5(pw
					.getHand().getCards(), Hand
					.getHandFromCardArray(_communityCards.getCards()), type()
					.intVal())[0]);
			_cat.finest("Winner = " + winner.get(i));
		}

		_cat.info("Showdown Position=" + showdownPos());
		int l = _players.length;
		int st = 0;
		int e = l;
		for (int i = 0; i < l; i++) {
			if (_players[i].pos() == showdownPos()) {
				st = i;
				break;
			}
		}
		int high_index = _contenders.length;
		int j;
		for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
			j = high_index;
			for (j--; j >= 0; j--) {
				if (_contenders[j].pos() == _players[i].pos()) {
					_players[i].setShowdown();
					_cat.info("Showdown=" + _players[i]);
					high_index = j;
					break;
				}
			}
		}
	}

	public PokerPresence[] winners() {
		return (PokerPresence[]) _winner.toArray(new PokerPresence[_winner
				.size()]);
	}

	public boolean tournyOver() {
		return _state == TABLE_CLOSED;
	}

	public boolean tournyWaiting() {
		return _state == TABLE_OPEN;
	}

	public int limit() {
		return _limit;
	}

	public int state() {
		return _state;
	}

	public int level() {
		return _hand_level + 1;
	}

	public double buyIn() {
		return _buyIn;
	}

	public double fees() {
		return _fees;
	}

	public double chips() {
		return _chips;
	}

	public int tourbo() {
		return _tourbo;
	}
	
	public String tournyId(){
		return _tournyId;
	}

	public long time() {
		return System.currentTimeMillis() - _time;
	}

	public double prize(int i, int maxP) {
		if (don()) {
			if (i < maxP / 2) {
				return Utils.getRounded(2 * _buyIn);
			} else {
				return 0;
			}
		} else {
			if (_maxPlayers <= 4) {
				if (i == 1) {
					return Utils.getRounded(maxP * _buyIn);
				} else {
					return 0;
				}
			} else {
				if (i == 1) {
					return Utils.getRounded(maxP * _buyIn * 0.5);
				} else if (i == 2) {
					return Utils.getRounded(maxP * _buyIn * 0.3);
				} else if (i == 3) {
					return Utils.getRounded(maxP * _buyIn * 0.2);
				} else {
					return 0;
				}
			}
		}
	}

	public boolean don() {
		return _don;
	}

	public Vector<PokerPresence> _pre_winner, _winner;
	PokerPresence[] _contenders;
	double _buyIn, _fees;
	double _chips;
	boolean _don;
	int _state;
	long _time;
	int _limit, _tourbo;
	int _hand;
	int _hand_level;
	int _thread_state = -1;
	int[] _prize_distribution = { 50, 30, 20 };
	int[][] _level_limits = { { 30, 60 }, { 40, 80 }, { 50, 100 }, { 60, 120 },
			{ 80, 160 }, { 100, 200 }, { 120, 240 }, { 150, 300 },
			{ 200, 400 }, { 250, 500 }, { 300, 600 }, { 400, 800 },
			{ 500, 1000 }, { 600, 1200 }, { 800, 1600 }, { 1000, 2000 },
			{ 1200, 2400 }, { 1500, 3000 }, { 2000, 4000 }, { 2500, 5000 },
			{ 3000, 6000 }, { 4000, 8000 }, { 5000, 10000 }, { 6000, 12000 },
			{ 15000, 30000 }, { 20000, 40000 }, { 25000, 50000 },
			{ 30000, 60000 }, { 40000, 80000 }, { 50000, 100000 } };

	public String toString() {
		return super.toString() + ", tourbo=" + _tourbo + ", stack=" + _chips
				+ ", don" + _don;
	}
}
