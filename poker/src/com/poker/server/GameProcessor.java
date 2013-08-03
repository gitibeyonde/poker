package com.poker.server;

import com.agneya.util.Base64;
import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;

import com.golconda.db.DBException;
import com.golconda.db.DBPlayer;
import com.golconda.db.DBTransactionScratchPad;
import com.golconda.db.LoginSession;
import com.golconda.db.ModuleType;
import com.golconda.game.Game;
import com.golconda.game.Player;
import com.golconda.db.PlayerPreferences;
import com.golconda.message.Command;
import com.golconda.message.GameEvent;
import com.golconda.message.Response;

import com.poker.common.db.DBPokerPlayer;
import com.poker.common.interfaces.MTTInterface;
import com.poker.common.interfaces.SitnGoInterface;
import com.poker.common.message.CommandFactory;
import com.poker.common.message.CommandGetChipsIntoGame;
import com.poker.common.message.CommandInt;
import com.poker.common.message.CommandJoinPool;
import com.poker.common.message.CommandLogin;
import com.poker.common.message.CommandMessage;
import com.poker.common.message.CommandMove;
import com.poker.common.message.CommandProcessor;
import com.poker.common.message.CommandQueue;
import com.poker.common.message.CommandRegister;
import com.poker.common.message.CommandString;
import com.poker.common.message.CommandTableDetail;
import com.poker.common.message.CommandTableList;
import com.poker.common.message.CommandTablePing;
import com.poker.common.message.CommandTournyDetail;
import com.poker.common.message.CommandTournyMyTable;
import com.poker.common.message.CommandTournyRegister;
import com.poker.common.message.CommandTournyUnRegister;
import com.poker.common.message.CommandVote;
import com.poker.common.message.ResponseBuyChips;
import com.poker.common.message.ResponseConfig;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseGetChipsIntoGame;
import com.poker.common.message.ResponseInt;
import com.poker.common.message.ResponseLogin;
import com.poker.common.message.ResponseMessage;
import com.poker.common.message.ResponsePing;
import com.poker.common.message.ResponseString;
import com.poker.common.message.ResponseTableDetail;
import com.poker.common.message.ResponseTableList;
import com.poker.common.message.ResponseTableOpen;
import com.poker.common.message.ResponseTablePing;
import com.poker.common.message.ResponseTournyDetail;
import com.poker.common.message.ResponseTournyList;
import com.poker.common.message.ResponseTournyMyTable;
import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPlayer;
import com.poker.game.PokerPresence;
import com.poker.game.gamemsgimpl.GameSummaryImpl;
import com.poker.game.gamemsgimpl.MessagingImpl;
import com.poker.game.gamemsgimpl.MoveImpl;
import com.poker.game.poker.*;
import com.poker.game.poker.pokerimpl.HoldemSitnGo;
import com.poker.game.poker.pokerimpl.HoldemTourny;
import com.poker.game.poker.pokerimpl.OmahaSitnGo;
import com.poker.game.poker.pokerimpl.TermHoldem;
import com.poker.nio.Client;
import com.poker.nio.Handler;
import com.poker.shills.BotGame;
import com.poker.shills.BotMove;
import com.poker.shills.BotPlayer;

import java.io.IOException;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handler will work on the queued GameEvents and will invoke the handler method
 * on GameServer. The handler is blocking. Resulting responses are written back
 * to the queue
 */
public class

GameProcessor implements CommandProcessor {
	static Logger _cat = Logger.getLogger(GameProcessor.class.getName());
	private int _nThreads;
	private Thread _t[];
	private CommandQueue _com;
	private boolean _keepservicing;
	private static int _id = 0;
	private static Configuration _conf;
	private String _cm = "";

	{
		try {
			_conf = Configuration.instance();
			_nThreads = _conf.getInt("Server.processor.threadCount");
			_cm = (String) _conf.get("Network.client.config");
		} catch (ConfigurationException e) {
			_cat.warning(e.getMessage());
			System.exit(-1);
		}
	}

	public GameProcessor() throws Exception {
	}

	public void startProcessor(CommandQueue q) throws Exception {
		_com = q;
		_t = new Thread[_nThreads];
		_keepservicing = true;
		for (int i = 0; i < _nThreads; i++) {
			_t[i] = new Thread(this);
			_t[i].start();
			_t[i].setName("Queue-" + _id + " Processor-" + i);
			_t[i].setPriority(Thread.NORM_PRIORITY);
		}
		_id++;
		GameServer._state = GameServer.NORMAL;
	}

	public void stopProcessor() throws Exception {
		_keepservicing = false;
		for (int i = 0; i < _nThreads; i++) {
			// _t[i].stop(); TODO remove stop instead falsify the condition to
			// stop the thread
		}
		GameServer._state = GameServer.STOPPED;
	}

	public void wakeUp() {
		for (int i = 0; i < _nThreads; _t[i++].interrupt()) {
			;
		}
	}

	public void run() {
		while (_keepservicing) {
			Command c = null;
			try {
				c = CommandFactory.getCommand(_com.fetch());
				if (c != null) {
					process(c);
					// _cat.info(((Handler)c.handler())._id + "COUNT=" +
					// ((Handler)c.handler())._com_cnt);
				}
			} catch (NoSuchElementException e) {
				try {
					Thread.currentThread().sleep(1000000);
				} catch (InterruptedException ee) {
					// continue
				}
			}
		}
	}

	/**
	 * fetch a client from the queue and process it
	 */
	public void process(Command ge) {
		if (ge == null) {
			return;
		}
		try {
			//_cat.finest("Game Event " + ge);
			Handler handler = (Handler) ge.handler();

			switch (ge.getCommandName()) {
			case Command.C_CONNECT:
				switch (GameServer._state) {
				case GameServer.STARTING:
					starting(ge, handler);
					return;
				case GameServer.SHUTTING:
					shutting(ge, handler);
					return;
				case GameServer.SUSPEND:
					suspend(ge, handler);
					return;
				case GameServer.NORMAL:
					// fall thru
				}
				connect(ge, handler);
				break;
            case Command.C_REGISTER:
                register(ge, handler);
                break;
			case Command.C_LOGIN:
				login(ge, handler);
				break;
			case Command.C_LOGOUT:
				logout(ge, handler);
				break;
			case Command.C_CONFIG:
				config(ge, handler);
				break;
			case Command.C_TURN_DEAF:
				turnDeaf(ge, handler);
				break;
			case Command.C_PREFERENCES:
				preferences(ge, handler);
				break;
			case Command.C_RESET_ALL_IN:
				resetAllIn(ge, handler);
				break;
			case Command.C_ADMIN:
				break;
			case Command.C_HTBT:
				handler.setWriteTime();

				/*
				 * handler.putResponse(new com.golconda.message.Response(com.
				 * golconda.common.message.Response.SUCCESS,
				 * com.golconda.message.Response.R_HTBT));
				 */
				// ignore
				break;
			case Command.C_PING:
				ping(ge, handler);
				break;
			case Command.C_BUYCHIPS:
				buyChips(ge, handler);
				break;

			// GAME
			case Command.C_MESSAGE:
				message(ge, handler);
				break;
			case Command.C_SIT_OUT:
				sitOut(ge, handler);
				break;
			case Command.C_SIT_IN:
				sitIn(ge, handler);
				break;
			case Command.C_MUCK_CARDS:
				muckCards(ge, handler);
				break;
			case Command.C_DONT_MUCK:
				dontMuck(ge, handler);
				break;
			case Command.C_WAIT_FOR_BLINDS:
				waitForBlinds(ge, handler);
				break;
			case Command.C_GET_CHIPS_INTO_GAME:
				getChips(ge, handler);
				break;
			case Command.C_TABLELIST:
				gameList(ge, handler);
				break;
			case Command.C_TOURNYLIST:
				tournyList(ge, handler);
				break;
			case Command.C_TABLEPING:
				gamePing(ge, handler);
				break;
			case Command.C_TABLEDETAIL:
				gameDetail(ge, handler);
				break;
			case Command.C_WAITER:
				addWaiter(ge, handler);
				break;
			case Command.C_TOURNYDETAIL:
				tournyDetail(ge, handler);
				break;
			case Command.C_TOURNYMYTABLE:
				tournyMyTable(ge, handler);
				break;
			case Command.C_TOURNYREGISTER:
				tournyRegister(ge, handler);
				break;
			case Command.C_TOURNYUNREGISTER:
				tournyUnRegister(ge, handler);
				break;
			case Command.C_MOVE:
				move(ge, handler);
				break;
			case Command.C_VOTE:
				vote(ge, handler);
				break;
			case Command.C_PLAYER_SEARCH:
				playerSearch(ge, handler);
				break;
			case Command.C_JOIN_POOL:
				joinPool(ge, handler);
				break;
			case Command.C_QUICK_FOLD:
				quickFold(ge, handler);
				break;


			default:
				if (handler != null) {
					handler.putResponse(new com.golconda.message.Response(
							com.golconda.message.Response.E_FAILURE,
							com.golconda.message.Response.R_UNKNOWN));
					_cat.info("Unknown Command = " + ge);
				}
			}
		} catch (Throwable e) {
			// remove the player from the game
			_cat.log(Level.WARNING, "Error in processing " + ge);
			e.printStackTrace();
		}
	} // end process

	public synchronized static void deliverResponse(com.golconda.game.resp.Response r)
			throws IOException {
		if (r == null) {
			return;
		}
		// return a table detail response
		PokerPresence cl[] = (PokerPresence[]) r.recepients();
        boolean sbp = false;
		for (int i = 0; cl != null && i < cl.length; i++) {
			PokerPresence clnt = (PokerPresence) cl[i];
            if (clnt.player() instanceof BotPlayer && !sbp) {
                BotPlayer bp = ((BotPlayer)clnt.player());
                GameEvent ge = new GameEvent();
                ge.init(r.getBroadcast());
                String moves[][] = ge.getMove();
                _cat.info("Next Move=" + ge.getNextMoveString());
                int move_pos =  Integer.parseInt(moves[0][0]);
                //get poker presence at pos
                PokerPresence p  = ((Poker)r.getGame()).getPlayerAtPos(move_pos);
                _cat.info("Name " + (p!= null ? p.name() : "none") + " move=" + ge.getNextMoveString());
                bp._bg.addMove(new BotMove(p, bp._bg, (PokerResponse) r));
                sbp = true;
            }
            else  if (clnt.player() instanceof GamePlayer) {
                ResponseGameEvent rge = new ResponseGameEvent(1, r.getCommand(clnt));
                if (clnt.isQickFold()){
                    _cat.finest("->Player Proxy QF =" + clnt.name() + "; " + rge);
                    ((GamePlayer) clnt.player()).deliverProxy(rge);
                }
                else if (clnt.isDisconnected()) {
                    _cat.finest("->Player Proxy=" + clnt.name() + "; " + rge);
                    ((GamePlayer) clnt.player()).deliverProxy(rge);
                    clnt.unsetSingleMove();
                } else if (clnt.isSingleMove()) {
                    clnt.unsetSingleMove();
                    ResponseGameEvent rge2 = new ResponseGameEvent(1, r.getCommand(clnt));
                    GameEvent ge = new GameEvent(rge2.getGameEvent());
                    ge.setNextMove("-1|none|-101");
                    rge2.setGameEvent(ge.toString());
                    _cat.finest("->Player=" + clnt.name() + "; " + rge2);
                    ((GamePlayer) clnt.player()).deliver(rge2);
                    _cat.finest("->Player Proxy=" + clnt.name() + "; " + rge);
                    ((GamePlayer) clnt.player()).deliverProxy(rge);
                } else {
                    _cat.info("->Player=" + clnt.name() + "; " + rge);
                    ((GamePlayer) clnt.player()).deliver(rge);
                }
            }
        }
		cl = (PokerPresence[]) r.observers();
		for (int i = 0; cl != null && i < cl.length; i++) {
			PokerPresence clnt = (PokerPresence) cl[i];
			String broadcast = r.getBroadcast();
			if (broadcast != null) {
				ResponseGameEvent rge = new ResponseGameEvent(1, broadcast);
				_cat.finest("->Observer=" + clnt.name() + "; " + rge);
				((GamePlayer) clnt.player()).deliver(rge);
			}
		}
	}

	public static void deliverResponse(com.golconda.game.resp.Response r,
			GamePlayer gp) throws IOException {
		if (r == null) {
			return;
		}
		String broadcast = r.getBroadcast();
		if (broadcast != null) {
			ResponseGameEvent rge = new ResponseGameEvent(1, broadcast);
			_cat.info("->Observer=" + gp.name() + "; " + rge);
			gp.deliver(rge);
		}
	}

	public static void deliverResponse(com.golconda.game.resp.Response[] r)
			throws IOException {
		for (int i = 0; i < r.length; i++) {
			deliverResponse(r[i]);
		}
	}

	/**
	 * This method delivers appropriate responses to the players and observers
	 **/
	public synchronized static void deliverMessageResponse(com.golconda.game.resp.Response r)
			throws IOException {
		// return a table detail response
		PokerPresence cl[] = (PokerPresence[]) r.recepients();
		for (int i = 0; i < cl.length; i++) {
			PokerPresence clnt = (PokerPresence) cl[i];
			ResponseMessage rge = new ResponseMessage(1, r.getCommand(clnt)
					.toString());
			_cat.info("Message to Player=" + clnt.name() + "; " + rge);
			((GamePlayer) clnt.player()).deliver(rge);
		}
		cl = (PokerPresence[]) r.observers();
		for (int i = 0; i < cl.length; i++) {
			PokerPresence clnt = (PokerPresence) cl[i];
			ResponseMessage rge = new ResponseMessage(1, r.getBroadcast()
					.toString());
			_cat.info("Message to Observer=" + clnt.name() + "; " + rge);
			((GamePlayer) clnt.player()).deliver(rge);
		}
	}

	private void starting(Command ge, Handler handler) {
		GamePlayer pp = new GamePlayer(handler);
		handler.attachment((Client) pp);
		com.golconda.message.Response gr = new com.golconda.message.Response(
				com.golconda.message.Response.E_STARTING,
				com.golconda.message.Response.R_CONNECT);
		handler.putResponse(gr);
	}

	private void shutting(Command ge, Handler handler) {
		GamePlayer pp = new GamePlayer(handler);
		handler.attachment((Client) pp);
		com.golconda.message.Response gr = new com.golconda.message.Response(
				com.golconda.message.Response.E_SHUTTING,
				com.golconda.message.Response.R_CONNECT);
		handler.putResponse(gr);
	}

	private void suspend(Command ge, Handler handler) {
		GamePlayer pp = new GamePlayer(handler);
		handler.attachment((Client) pp);
		com.golconda.message.Response gr = new com.golconda.message.Response(
				com.golconda.message.Response.E_SUSPEND,
				com.golconda.message.Response.R_CONNECT);
		handler.putResponse(gr);
	}

	private void connect(Command ge, Handler handler) {
		if (handler != null && handler.attachment() == null) {
			if (_conf.getBoolean("Network.server.duplicateid")){
				// check if already a connection exists from same ip
				Enumeration<Handler> eh = Handler.registry().elements();
				while (eh.hasMoreElements()){
					Handler ch = eh.nextElement();
					if (ch._id == handler._id)continue;
					if (ch.inetAddress().equals(handler.inetAddress())){
						com.golconda.message.Response gr = new com.golconda.message.Response(
								com.golconda.message.Response.E_IP_REUSE,
								com.golconda.message.Response.R_CONNECT);
						handler.putResponse(gr);
						handler.kill();
					}
				}
			}
			// look at all the presences and send back info so that the client
			// can open those tables
			// TODO for reconnect
			
			GamePlayer pp = new GamePlayer(handler);
			handler.attachment(pp);
			com.golconda.message.Response gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_SUCCESS,
					com.golconda.message.Response.R_CONNECT);
			// look at all the presences and send back info so that the client
			// can open those tables
			// TODO for reconnect
			handler.putResponse(gr);
		} else { // new connection
			com.golconda.message.Response gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_SUCCESS,
					com.golconda.message.Response.R_CONNECT);
			handler.putResponse(gr);
		}
	}

	public static final long WEEK = 7 * 24 * 60 * 60 * 1000;
	public static final long DAY = 24 * 60 * 60 * 1000;

	private void config(Command ge, Handler handler) {
		GamePlayer pp = (GamePlayer) handler.attachment();
		ResponseConfig gr = new ResponseConfig(1, _cm);
		_cat.finest("Response config = " + gr);
		handler.putResponse(gr);
	}

	private void getChips(Command ge, Handler handler) throws Exception {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (pp == null) {
			return;
		}
		if (!pp.isAuthenticated()) {
			com.golconda.message.Response gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_AUTHENTICATE,
					com.golconda.message.Response.R_GET_CHIPS_INTO_GAME);

			handler.putResponse(gr);
			return;
		}
		CommandGetChipsIntoGame cbc = (CommandGetChipsIntoGame) ge;
		double chips = cbc.getChips();
		String tid = cbc.getTableId();
		Game tg = Game.game(tid);

		com.golconda.message.Response r = null;

		if (tg == null || tg instanceof HoldemSitnGo
				|| tg instanceof OmahaSitnGo || tg instanceof HoldemTourny) {
			_cat.info("This is a tourny");
			handler.putResponse(new com.golconda.message.Response(
					Response.E_BUY_IN_NOT_ALLOWED,
					Response.R_GET_CHIPS_INTO_GAME));
			return;
		}

		Poker pg = null;
		if (tg instanceof Poker) {
			pg = (Poker) tg;
		} else {
			handler.putResponse(new com.golconda.message.Response(
					Response.E_BUY_IN_NOT_ALLOWED,
					Response.R_GET_CHIPS_INTO_GAME));
			return;

		}

		PokerPresence p = (PokerPresence) pp.presence(tid);
		_cat.info("Progress=" + pg._inProgress + ", Valid Pres=" + pg.validatePresenceInGame(p.name()));
		boolean active_flag = pg._inProgress &  pg.validatePresenceInGame(p.name());
		if (!pg.onTable(p.name())) {
			handler.putResponse(new com.golconda.message.Response(
					Response.E_NOT_SITTING_ON_THIS_TABLE,
					Response.R_GET_CHIPS_INTO_GAME));
			return;
		}
		Game g = Game.game(tid);
		if (tg.type().isReal() && chips <= pp.realWorth()) {
			if (!p.buyIn(chips, active_flag)){
				handler.putResponse(new com.golconda.message.Response(
						Response.E_BUY_IN_NOT_ALLOWED,
						Response.R_GET_CHIPS_INTO_GAME));
			}
			p.unsetBroke();
			r = new com.poker.common.message.ResponseGetChipsIntoGame(
					com.golconda.message.Response.E_SUCCESS, tid, p.netWorth() + p.getGameAddedChips(), pp.playWorth(), 0, pp.realWorth(),
					0);
			_cat.info(p.name() + " Buying chips " + chips);

			if (g.grid() != -1) {
				Auditor.instance().write(
						tid,
						g.grid(),
						"<chips pos=\"" + p.pos() + "\" name=\"" + p.name()
								+ "\" amount=\"" + chips + "\"/>", false);
			}
			if (!pg._inProgress) {
				deliverResponse(pg.start());
			}
		} else if (chips <= pp.playWorth()) {
			if(!p.buyIn(chips, active_flag)){
				handler.putResponse(new com.golconda.message.Response(
						Response.E_BUY_IN_NOT_ALLOWED,
						Response.R_GET_CHIPS_INTO_GAME));
			}
			// pp.playWorth(pp.playWorth() - chips); TODO
			p.unsetBroke();
			r = new com.poker.common.message.ResponseGetChipsIntoGame(
					com.golconda.message.Response.E_SUCCESS, tid, p.netWorth()+ p.getGameAddedChips(), pp.playWorth() , 0, pp.realWorth(),
					0);

			if (g.grid() != -1) {
				Auditor.instance().write(
						tid,
						g.grid(),
						"<chips pos=\"" + p.pos() + "\" name=\"" + p.name()
								+ "\" amount=\"" + chips + "\"/>", false);
			}
			if (!pg._inProgress) {
				deliverResponse(pg.start());
			}
		} else {
			// error
			r = new com.poker.common.message.ResponseGetChipsIntoGame(
					com.golconda.message.Response.E_FAILURE, tid, -1, pp
							.playWorth(), 0, pp.realWorth(), 0);
		}
		_cat.finest("BUY IN RESP = " + r);
		// if the game is TermPoker try to move the player to active table
		if (g.type().isTPoker() && ((TermHoldem)g)._isBench){
			boolean result = ((TermHoldem)g).movePlayerToActiveTable(p, p.getAmtAtTable());
			if (!result){
				handler.putResponse(new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_BROKE,
						com.golconda.message.Response.R_MOVE, ((TermHoldem)g)._name));
				// set the game response in the clients out queue
			}
		}
		handler.putResponse(r);
	}

	/********
	 * PokerPresence p = pp.presence(tid); p.unsetBroke();
	 * 
	 * if (tg.type().isReal() && chips <= pp.realWorth()) { // check if win loss
	 * is violated if (pp.winLossViolated() != 0) {
	 * _cat.info("Win loss is violated " + pp.presence(tid));
	 * handler.putResponse(new com.golconda.message.Response(pp.
	 * winLossViolated() > 0 ? Response.E_WIN_VIOLATED :
	 * Response.E_LOSS_VIOLATED, Response.R_GET_CHIPS_INTO_GAME)); return; }
	 * 
	 * p.addToWorth(chips); _cat.info(p.name() + " Buying chips " + chips);
	 * Auditor.instance().write(tid, Game.game(tid).grid(), "<chips pos=\"" +
	 * p.pos() + "\" name=\"" + p.name() + "\" amount=\"" + chips + "\"/>",
	 * false); pp.realWorth(pp.realWorth() - chips); r = new
	 * com.golconda.message.ResponseGetChipsIntoGame(com.golconda.
	 * common.message. Response.E_SUCCESS, tid, pp.presence(tid).netWorth(),
	 * pp.playWorth(), pp.getDBPlayer(). getPlayBankroll(), pp.realWorth(),
	 * pp.getDBPlayer(). getRealBankroll()); if (tg instanceof Poker) { Poker pg
	 * = (Poker) tg; if (!pg._inProgress) { deliverResponse(pg.start()); } } }
	 * else if (chips <= pp.playWorth()) { pp.presence(tid).addToWorth(chips);
	 * pp.playWorth(pp.playWorth() - chips); r = new
	 * com.golconda.message.ResponseGetChipsIntoGame(com.golconda.
	 * common.message. Response.E_SUCCESS, tid, pp.presence(tid).netWorth(),
	 * pp.playWorth(), pp.getDBPlayer(). getPlayBankroll(), pp.realWorth(),
	 * pp.getDBPlayer(). getRealBankroll()); if (tg instanceof Poker) { Poker pg
	 * = (Poker) tg; if (!pg._inProgress) { deliverResponse(pg.start()); } }
	 * 
	 * } else { //error r = new
	 * com.golconda.message.ResponseGetChipsIntoGame(com.golconda.
	 * common.message. Response.E_FAILURE, tid, -1, pp.playWorth(),
	 * pp.getDBPlayer(). getPlayBankroll(), pp.realWorth(), pp.getDBPlayer().
	 * getRealBankroll());
	 * 
	 * }
	 * 
	 * handler.putResponse(r); }
	 ********/
	
	
	private void buyChips(Command ge, Handler handler) {
		GamePlayer pp = (GamePlayer) handler.attachment();
		/**
		 * if (!pp.isAuthenticated()) { com.golconda.message.Response gr = new
		 * com.golconda. message. Response(com.golconda.message.
		 * Response.E_AUTHENTICATE, com.golconda.message.Response. R_BUYCHIPS);
		 * 
		 * handler.putResponse(gr); return; } CommandBuyChips cbc =
		 * (CommandBuyChips) ge; double realChips = cbc.getRealChips(); double
		 * playChips = cbc.getPlayChips(); int tid = cbc.getGid(); try {
		 * DBPlayer dbp = pp.getDBPlayer(); int r1 = -1, r2 = -1; if (realChips
		 * > 0) { r1 = dbp.buyRealChipsCHANGETOCHIPS(handler._id, realChips, 1);
		 * } if (playChips > 0) { r2 =
		 * dbp.buyPlayChipsCHANGETOCHIPS(handler._id, playChips, 1); }
		 * ResponseBuyChips gr = new ResponseBuyChips(r1 == -1 && r2 == -1 ? 0 :
		 * 1, dbp.getPlayChips(), dbp.getPlayBankroll(), dbp.getRealChips(),
		 * dbp.getRealBankroll(), tid); _cat.info("Buying chips " +
		 * dbp.getDispName() + ", real=" + realChips + ", play=" + playChips);
		 * handler.putResponse(gr); } catch (DBException e) {
		 * com.golconda.message.Response gr = new com.golconda. message.
		 * Response(com.golconda.message. Response. E_FAILURE,
		 * com.golconda.message.Response. R_BUYCHIPS);
		 * 
		 * handler.putResponse(gr); } catch (Exception e) {
		 * com.golconda.message.Response gr = new com.golconda. message.
		 * Response(com.golconda.message. Response. E_FAILURE,
		 * com.golconda.message.Response. R_BUYCHIPS);
		 * 
		 * handler.putResponse(gr); }
		 **/
	}

	private synchronized void logout(Command ge, Handler handler) {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (pp == null) {
			return; // the player does not exist
		}
		try {
			com.golconda.message.Response gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_SUCCESS,
					com.golconda.message.Response.R_LOGOUT);
			handler.putResponse(gr);
		} catch (Exception e) {
			;
		}
		handler.kill();
	}

	private void sitIn(Command ge, Handler handler) throws IOException {
		com.golconda.message.Response gr = null;
		GamePlayer pp = (GamePlayer) handler.attachment();

		if (!pp.isAuthenticated()) {
			handler.putResponse(new ResponseString(Response.E_AUTHENTICATE,
					Response.R_SIT_IN, ""));
			return;
		}

		CommandString cint = (CommandString) ge;
		String tid = cint.getStringVal();

		PokerPresence p = (PokerPresence) pp.presence(tid);
		if (p == null) {
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_SIT_IN, tid);
			handler.putResponse(gr);
			return;
		}
        Game g = Game.game(tid);
		p.setSitin();
		_cat.finest("Player " + pp.name() + " is sitting in");
		gr = new com.poker.common.message.ResponseString(
				com.golconda.message.Response.E_SUCCESS,
				com.golconda.message.Response.R_SIT_IN, tid);
		handler.putResponse(gr);
		if (g instanceof Poker) {
			Poker pg = (Poker) g;
			if (!pg._inProgress) {
				deliverResponse(pg.start());
			}
		}
	}
	

	private void muckCards(Command ge, Handler handler) throws IOException {
		com.golconda.message.Response gr = null;
		GamePlayer pp = (GamePlayer) handler.attachment();

		if (!pp.isAuthenticated()) {
			handler.putResponse(new ResponseString(Response.E_AUTHENTICATE,
					Response.R_MUCK_CARDS, ""));
			return;
		}

		CommandString cint = (CommandString) ge;
		String tid = cint.getStringVal();

		PokerPresence p = (PokerPresence) pp.presence(tid);
		if (p == null) {
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_MUCK_CARDS, tid);
			handler.putResponse(gr);
			return;
		}
		p.unsetDontMuck();
		_cat.finest("Player " + pp.name() + " is mucking his cards");
		gr = new com.poker.common.message.ResponseString(
				com.golconda.message.Response.E_SUCCESS,
				com.golconda.message.Response.R_MUCK_CARDS, tid);
		handler.putResponse(gr);
	}


	private void dontMuck(Command ge, Handler handler) throws IOException {
		com.golconda.message.Response gr = null;
		GamePlayer pp = (GamePlayer) handler.attachment();

		if (!pp.isAuthenticated()) {
			handler.putResponse(new ResponseString(Response.E_AUTHENTICATE,
					Response.R_DONT_MUCK, ""));
			return;
		}

		CommandString cint = (CommandString) ge;
		String tid = cint.getStringVal();

		PokerPresence p = (PokerPresence) pp.presence(tid);
		if (p == null) {
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_DONT_MUCK, tid);
			handler.putResponse(gr);
			return;
		}
		p.setDontMuck();
		_cat.finest("Player " + pp.name() + " does not want to muck his cards");
		gr = new com.poker.common.message.ResponseString(
				com.golconda.message.Response.E_SUCCESS,
				com.golconda.message.Response.R_DONT_MUCK, tid);
		handler.putResponse(gr);
	}


	private void sitOut(Command ge, Handler handler) {
		com.golconda.message.Response gr = null;
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (!pp.isAuthenticated()) {
			handler.putResponse(new ResponseString(Response.E_AUTHENTICATE,
					Response.R_SIT_OUT, ""));
			return;
		}

		CommandString cint = (CommandString) ge;
		String tid = cint.getStringVal();
		Game g = Game.game(tid);
		if (g != null && g.type().isRegularGame()) {
			pp.presence(tid).setSitOutNextGame();
			_cat.info("Player " + pp.name() + " is sitting out " + tid);
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_SUCCESS,
					com.golconda.message.Response.R_SIT_OUT, tid);
		} else {
			_cat
					.warning("Player "
							+ pp.name()
							+ " is trying to sitout in a non regular poker game "
							+ tid);
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_SIT_OUT, tid);
		}
		handler.putResponse(gr);
	}


	private void waitForBlinds(Command ge, Handler handler) throws IOException {
		com.golconda.message.Response gr = null;
		GamePlayer pp = (GamePlayer) handler.attachment();

		if (!pp.isAuthenticated()) {
			handler.putResponse(new Response(Response.E_AUTHENTICATE,
					Response.R_WAIT_FOR_BLINDS));
			return;
		}

		CommandString cint = (CommandString) ge;
		String tid = cint.getStringVal();

		PokerPresence p = (PokerPresence) pp.presence(tid);
		if (p == null) {
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_WAIT_FOR_BLINDS, tid);
			handler.putResponse(gr);
			return;
		}
		Game g = Game.game(tid);
		if (g instanceof Poker) {
			Poker pg = (Poker) g;
			if (pg.allPlayers(0).length <= 3) {
				p.setSitin();
				if (!pg._inProgress) {
					deliverResponse(pg.start());
				}
			} else {
				p.setWaitForBlinds();
			}
		}
		_cat.finest("Player " + pp.name() + " is sitting in");
		gr = new com.poker.common.message.ResponseString(
				com.golconda.message.Response.E_SUCCESS,
				com.golconda.message.Response.R_WAIT_FOR_BLINDS, tid);
		handler.putResponse(gr);
	}
	 private synchronized void register(Command ge, Handler handler) {
	        GamePlayer pp = (GamePlayer)handler.attachment();
	        com.golconda.message.Response gr = null;

	        if (pp.isAuthenticated()) {
	            gr = 
	 new com.golconda.message.Response(com.golconda.message.Response.E_ALREADY_LOGGED, 
	                                   com.golconda.message.Response.R_REGISTER);
	            handler.putResponse(gr);
	            return;
	        }

	        String user = ((CommandRegister)ge).getUserName();
	        String password = ((CommandRegister)(ge)).getPassword();
	        String email = ((CommandRegister)(ge)).getEmail();
	        String city = ((CommandRegister)(ge)).getCity();
	        String country = ((CommandRegister)(ge)).getCountry();
	        String zip = ((CommandRegister)(ge)).getZip();
	        String bonus_code = ((CommandRegister)(ge)).getBonusCode();
	        String source = ((CommandRegister)(ge)).getSource();
	        String affiliate = ((CommandRegister)(ge)).getAffiliate();

	        if (user.length() > 2 && email.length() > 2 && password.length() > 2) {
	            int gender = ((CommandRegister)(ge)).getGender();
	            // check if the user exists
	            // regsiter the user
	            try {
	                DBPlayer dbp = new DBPlayer(user, password);
	                if (dbp.get(user)) {
	                    gr = 
	                    		new com.golconda.message.Response(com.golconda.message.Response.E_USER_EXISTS, 
	                                   com.golconda.message.Response.R_REGISTER);
	                    handler.putResponse(gr);
	                    return;
	                }
	                // check if bonus code is valid
	                dbp.setEmailId(email);
	                //dbp.setCity(city);
	                //dbp.setCountry(country);
	                //dbp.setZip(zip);
	                dbp.setGender(gender);
	                dbp.setPassword(password);
	                //dbp.setAllInTs(System.currentTimeMillis());
	                dbp.setPreferences(PlayerPreferences.DEFAULT_MASK);
	                dbp.setBonusCode(bonus_code);
	                dbp.setAffiliate(affiliate);
	                _cat.finest(dbp.toString());
	                int res = -1;
	                res = dbp.register(10000, 0, new ModuleType(ModuleType.POKER)); // ***********
	                if (res == 1) {
	                    pp.name(user);
	                    pp.gender(gender);
	                    pp.setAuthenticated();
	                    pp.setDBPlayer(dbp);

	                    gr = 
	                    	new ResponseLogin(true, com.golconda.message.Response.E_SUCCESS, pp.gender(), 
	                   dbp.getPlayChips(), dbp.getAvatar(), dbp.getRealChips(), 
	                   dbp.getCity(), dbp.getPreferences().intVal(), dbp.getRank(), 
	                   0, 0.0);
	                    
	                    LoginSession ls = new LoginSession(user);
	                    ls.setLoginTime(new Date());
	                    ls.setLogoutTime(new Date());
	                    ls.setSessionId(handler._id);
	                    ls.setAffiliateId(affiliate);
	                    ls.setBonusCode(bonus_code);
	                    ls.setIp(handler.inetAddress().getHostAddress());
	                    ls.setStartWorth(pp.realWorth());

	                    ls.save();
	                    pp.loginSession(ls);
	                    pp.setAuthenticated();
	                } else {
	                    gr = 
	 new com.golconda.message.Response(com.golconda.message.Response.E_USER_EXISTS, 
	                                   com.golconda.message.Response.R_REGISTER);
	                }
	            } catch (DBException e) {
	                _cat.log(Level.WARNING, "Registration failed ", e);
	                gr = 
	 new com.golconda.message.Response(com.golconda.message.Response.E_FAILURE, 
	                                   com.golconda.message.Response.R_REGISTER);
	            }

	        } else {
	            gr = 
	 new com.golconda.message.Response(com.golconda.message.Response.E_FAILURE, 
	                                   com.golconda.message.Response.R_REGISTER);
	        }
	        handler.putResponse(gr);
	    }
	
	
	private synchronized void login(Command ge, Handler handler) {
		com.golconda.message.Response gr = null;
		// com.golconda.message.ResponseMessage mr = null;
		GamePlayer pp = (GamePlayer) handler.attachment();
		String user = ((CommandLogin) ge).getUserName();
		String token = ((CommandLogin) ge).getToken();
		String provider = ((CommandLogin) ge).getProvider();
		String affiliate = ((CommandLogin) ge).getAffiliate();

		// Enforce IP restriction
		/***
		 * if (Utils.isRestricted(handler.inetAddress().getHostAddress())) { gr
		 * = new com.golconda.message.Response(com.golconda.message.
		 * Response.E_IP_RESTRICTED, com.golconda.message.Response. R_LOGIN);
		 * handler.putResponse(gr); return; }
		 ***/

		if (pp != null && pp.isAuthenticated()) {
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_ALREADY_LOGGED,
					com.golconda.message.Response.R_LOGIN);
			handler.putResponse(gr);
			return;
		}
		String password = ((CommandLogin) (ge)).getPassword();

		if (user.length() < 2) {
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_LOGIN);
		}

		// ////////// DEFAULT PLAYER AUTHETICATION
		boolean player_exists = false;
		DBPlayer dbp = new DBPlayer();
		try {
			player_exists = dbp.get(user, password, affiliate);
			/// GET chips out of games
	          DBTransactionScratchPad[] dbt = DBTransactionScratchPad.getTransaction(user);
	          for (int i=0;i<dbt.length;i++){
	              dbp.chipsHouseKeeping(dbt[i]._game_name, dbt[i]._game_type, dbt[i]._module, dbt[i]._play, dbt[i]._real, dbt[i]._session);
	          }
            /// END GET CHIPS OUT OF GAMES
			
			if (PlayerPreferences.isBannedPlayer(dbp.getPreferences().intVal())) {
				pp.unsetAuthenticated();
				gr = new com.golconda.message.Response(
						com.golconda.message.Response.E_BANNED,
						com.golconda.message.Response.R_LOGIN);
				handler.putResponse(gr);
				return;
			}
		} catch (DBException e) {
			_cat.warning(e.getMessage());

			pp.unsetAuthenticated();
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_AUTHENTICATE,
					com.golconda.message.Response.R_LOGIN);
			handler.putResponse(gr);
			return;
		} catch (IllegalStateException e) {
			_cat.warning(e.getMessage());

			pp.unsetAuthenticated();
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_AFFILIATE_MISMATCH,
					com.golconda.message.Response.R_LOGIN);
			handler.putResponse(gr);
			return;
		}

		try {
			if (player_exists) {
				_cat.finest(token + " Player exists = " + player_exists);
				
				boolean exists = false;
				// CHECK IF A GAMEPLAYER EXISTS
				 for (Enumeration e = GamePlayer.getGPList(); e.hasMoreElements(); ) { 
					 GamePlayer gp = (GamePlayer)e.nextElement();
					 //_cat.info("GP found " + gp.name());
					 if (gp.name().equals(user)){
						 _cat.info("Previous game player found " + pp);
						 // send a message to this player
						 com.golconda.message.Response r = new com.golconda.message.Response(com.golconda.message.Response.E_LOGGED_IN_AT_DIFF_LOCATION, com.golconda.message.Response.R_LOGOUT);
					     	 gp.deliver(r); 
					     handler.registry().remove(gp.handler()._id); // remove the old handler
						 handler.attachment(gp);
						 gp.setHandler(handler);
						 exists = true;
						 pp = gp;
				         for (Enumeration enumt = gp.getPresenceList(); enumt.hasMoreElements(); ) {
				            PokerPresence p = (PokerPresence)enumt.nextElement();
				            //_cat.info("Found presence " + p);
				         }
					 }
				 }
				
				 if (!exists){
					pp.attach(user, dbp);
					pp.setAuthenticated();
					pp.gender(dbp.getGender());
					pp.avatar(dbp.getAvatar());
					pp.city(dbp.getCity());
					pp.rank(dbp.getRank());
					// check shill
					pp.shill(token.equals("807") ? true : false);
				 }
				int cr = com.golconda.message.Response.E_SUCCESS;
				
				// set the user session information
				LoginSession ls = new LoginSession(user);
				ls.setDispName(user);
				ls.setLoginTime(new Date());
				ls.setLogoutTime(new Date());
				ls.setSessionId(handler._id);
				ls.setAffiliateId(affiliate);
				ls.setBonusCode(dbp.getBonusCode());
				ls.setIp(handler.inetAddress().getHostAddress());
				ls.setStartWorth(pp.realWorth()); // pp.worth());
				ls.setEndWorth(pp.realWorth()); // pp.worth());
				ls.setBonusCode(token);

				ls.save();
				pp.loginSession(ls);
				_cat.finest(pp.name() + "Successfully logged in " + user);
				_cat.info("GP list size = " + GamePlayer._gp_registry.size());
				
				int version = 10000;
				try {
					version = Integer.parseInt(token);
				}
				catch (Throwable e){}
			
				
				gr = new ResponseLogin(cr, pp.gender(), dbp.getPlayChips(), dbp
						.getAvatar(), dbp.getRealChips(), dbp.getCity(), dbp
						.getPreferences().intVal(), dbp.getRank(), 2,
						GameServer._client_version - version);
				// _cat.info("Response = " + gr + " Player=" + pp);
			} else { // player does not exist in the DB

				// no player matches the criteria
				_cat.finest("Player does not exist in the db " + user);
				gr = new com.golconda.message.Response(
						com.golconda.message.Response.E_REGISTER,
						com.golconda.message.Response.R_LOGIN);

			}
		} catch (DBException e) {
			_cat.log(Level.WARNING, "DBException during login " + e);
			pp.unsetAuthenticated();
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_LOGIN);
		}
		handler.putResponse(gr);
	}

	private void gameList(Command ge, Handler handler) throws Exception {
		Player pp = (Player) handler.attachment();
		int mask = ((CommandTableList) ge).getType();
		if (mask <= 0) {
			return;
		}
		String aff = ((CommandTableList) ge).getAffiliate();
		// String plyrs = ((CommandTableList)ge).getPlayer();
		int i = 0;
		Game[] g = Game.listAll();
		Vector<String> games = new Vector<String>();
		
		
		// _cat.finest("Players=" + plyrs + ", Affiliate=" + aff);
		boolean isPrivate = false; // ! (plyrs == null || plyrs.equals("null")
									// ||
		// plyrs.length() < 2);
		boolean isPreferred = !(aff == null || aff.equals("null") || aff
				.length() < 2)
				&& !aff.equals("admin");
		//_cat.finest("isPrivate=" + isPrivate + ", isPreferred=" + isPreferred);
		
		for (int k = 0; k < g.length; k++) {
			if ((g[k].type().intVal() & mask) == 0 || g[k].isSuspended()) {
				continue;
			}
			
			// TERM POOL
			if (g[k].type().isTPoker()){
				continue; // do not add TPoker
			}
			
			if (isPrivate && isPreferred) {
				games.add(g[k].details().getBroadcast());
			} else if (isPrivate) {
				games.add(g[k].details().getBroadcast());
			} else if (isPreferred) {
				if (g[k].isAffiliate(aff) || g[k].isAffiliate("admin")) {
					games.add(g[k].details().getBroadcast());
				}
			} else {
				games.add(g[k].details().getBroadcast());
			}
		}
		//POOLS
		ConcurrentHashMap<String, String> tmap = new ConcurrentHashMap<String, String>();
		for (int k = 0; k < g.length; k++) {
			if (g[k].type().isTPoker()){
				TermHoldem th = (TermHoldem)g[k];
				String pn = th.poolName();
				String eth = tmap.get(pn);
				if (eth == null){
					tmap.put(pn, th.poolDetails());
				}
			}
		}
		
		// ADD POOLS
		for (String pd: tmap.values()){
			games.add(pd);
		}
		
		ResponseTableList gr = new ResponseTableList(
				com.golconda.message.Response.E_SUCCESS, (String[]) games
						.toArray(new String[games.size()]));
		int gc = 0;
		for (int k = 0; k < g.length; k++) {
			if (g[k]._inProgress) {
				gc++;
			}
		}

		//_cat.info("Response TABLELIST = " + gr);
		// set the game response in the clients out queue
		handler.putResponse(gr);
	}

	private void tournyList(Command ge, Handler handler) throws IOException {
		Player pp = (Player) handler.attachment();
		TournyController tc = TournyController.instance();
		Tourny[] t = tc.listAll();
		String[] ts = new String[t.length];
		for (int k = 0; k < t.length; k++) {
			ts[k] = t[k].stringValue();
		}
		ResponseTournyList gr = new ResponseTournyList(com.golconda.message.Response.E_SUCCESS, ts);
		//_cat.finest("Response TournyList = " + gr);
		// set the game response in the clients out queue
		handler.putResponse(gr);
	}

	private synchronized void turnDeaf(Command ge, Handler handler)
			throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		CommandString cint = (CommandString) ge;
		String tid = cint.getStringVal();
		PokerPresence p=pp.presence(tid);
		if (pp != null && p != null && p.isQickFold()){
			return;
		}
		if (pp != null) {
			try {
				pp._loginSession.setEndWorth(pp.realWorth());
				pp._loginSession.updateLogout();
			} catch (Exception e) {
				_cat.log(Level.WARNING, "Unable to save login session "
						+ pp.name(), e);
			}
		}
		if (pp != null && p != null) {
			com.golconda.game.resp.Response r = pp
					.leaveGameAndWatch((PokerPresence) pp.presence(tid));
			if (r != null) {
				deliverResponse(r);
			}
			// try updating his login session
		}
	}

	private synchronized void gameDetail(Command ge, Handler handler)
			throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		String tid = ((CommandTableDetail) ge).getTableId();
		// query the game server for game details
		// get the table details and set
		Game g = Game.game(tid);
		if (pp != null && (tid == null || g == null)) {
			ResponseString rge = new ResponseString(Response.E_NONEXIST,
					Response.R_TABLEDETAIL, tid);
			handler.putResponse(rge);
			return;
		}
		com.golconda.game.resp.Response r = pp.addWatch(tid);
		_cat.finest("Table details = " + r.getCommand(pp.presence(tid)));
		if (r == null) {
			ResponseString rge = new ResponseString(
					Response.E_GAME_NOT_ALLOWED, Response.R_TABLEDETAIL, tid);
			handler.putResponse(rge);
			return;
		} else {
			ResponseTableDetail rge = new ResponseTableDetail(1, r
					.getCommand(pp.presence(tid)));
			handler.putResponse(rge);
		}
	}


	private synchronized void gamePing(Command ge, Handler handler)
			throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		String tid = ((CommandTablePing) ge).getTableId();
		// query the game server for game details
		// get the table details and set
		Game g = Game.game(tid);
		if (pp != null && (tid == null || g == null) && g instanceof Poker) {
			ResponseString rge = new ResponseString(Response.E_NONEXIST, Response.R_TABLEDETAIL, tid);
			handler.putResponse(rge);
			return;
		}
		com.golconda.game.resp.Response r = new GameDetailsResponse((Poker)g);
		_cat.finest("Table details for " + tid + " is " + r.getBroadcast());
		if (r == null) {
			ResponseString rge = new ResponseString(Response.E_GAME_NOT_ALLOWED, Response.R_TABLEDETAIL, tid);
			handler.putResponse(rge);
			return;
		} else {
			ResponseTablePing rge = new ResponseTablePing(1, r.getCommand(pp.presence(tid)));
			handler.putResponse(rge);
		}
	}
	
	private void addWaiter(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (!pp.isAuthenticated()) {
			com.golconda.message.Response gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_AUTHENTICATE,
					com.golconda.message.Response.R_WAITER);

			handler.putResponse(gr);
			return;
		}

		String tid = ((CommandString) ge).getStringVal();
		// query the game server for game details
		// get the table details and set
		if (tid == null || Game.game(tid) == null) {
			ResponseString rge = new ResponseString(0, Response.R_WAITER, tid);
			handler.putResponse(rge);
			return;
		}
		Poker pg = ((Poker) Game.game(tid));
		_cat.warning("Player count " + pg.allPlayers(-1).length
				+ " max players " + pg.maxPlayers());
		if (pg.allPlayers(-1).length < pg.maxPlayers()) {
			// table is partially filled/no waiting
			_cat.warning("There is no waiting on table " + pg);
			ResponseString rge = new ResponseString(
					Response.E_PARTIALLY_FILLED, Response.R_WAITER, tid);
			handler.putResponse(rge);
			return;
		}
		PokerPresence p = (PokerPresence) pp.createPresence(tid);
		boolean success = pg.waiterAdd(p);
		ResponseString rge = new ResponseString(success ? 1 : 0,
				Response.R_WAITER, tid);
		_cat.warning("Waiting resp = " + rge);
		handler.putResponse(rge);
	}

	private void tournyDetail(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		String tid = ((CommandTournyDetail) ge).getTournyId();
		TournyController tc = TournyController.instance();
		Tourny t = tc.getTourny(tid);
		_cat.finest(tid + " Tourny Details = " + t);
		Response gr = null;
		GameEvent gev = new GameEvent();
		if (t == null) {
			gr = new Response(Response.E_NONEXIST, Response.R_TOURNYDETAIL);
			handler.putResponse(gr);
			tournyList(ge, handler);
		} else {
			_cat.info("Tourny = " + t.stringValue());
			// if tourny has not yet started send reg details
			String te = t.stringValue();
			Vector games = new Vector();
			if (t.isRegOpen()) {
				_cat.info("Tournament is open for registration " + te);
			} else if (t.isWaiting() || t.isPlaying()) {
				// check which table is assigned to this player
				MTTInterface[] g = t.listAll();
				for (int k = 0; k < g.length; k++) {
					String gid = g[k].name();
					com.golconda.game.resp.Response r = com.golconda.game.Game
							.handle(new GameSummaryImpl(gid, null));
					if (r.getBroadcast().toString().equals("null")) {
						continue;
					}
					games.add(r.getBroadcast().toString());
					_cat.info("Tournament is open for waiting or running "
							+ r.getBroadcast().toString());
				}
			}
			gr = new ResponseTournyDetail(
					com.golconda.message.Response.E_SUCCESS, te,
					(String[]) games.toArray(new String[games.size()]));
			_cat.info("Response TOURNY DETAIL = " + gr);
			handler.putResponse(gr);
		}
	}
	
	

	private synchronized void tournyMyTable(Command ge, Handler handler)
			throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		String tid = ((CommandTournyMyTable) ge).getTournyId();
		TournyController tc = TournyController.instance();
		Tourny t = tc.getTourny(tid);
		_cat.info("Tourny = " + t.stringValue());
		// if tourny has not yet started send reg details
		Response gr = null;
		com.golconda.game.resp.Response r = null;
		PokerPresence p = null;
		try {
			r = t.myTable(pp);
			if (r == null) {
				gr = new Response(com.golconda.message.Response.E_FAILURE,
						com.golconda.message.Response.R_TOURNYMYTABLE);
			} else {
				_cat.info("My table resp = " + r.getBroadcast());
				String gid = r.getGame().name();
				p = (PokerPresence) pp.presence(gid);
				gr = new ResponseTournyMyTable(
						gid == null ? com.golconda.message.Response.E_FAILURE
								: com.golconda.message.Response.E_SUCCESS, gid,
						p.pos());
			}
		} catch (Exception e) {
			_cat.log(Level.WARNING, "Exception during goto my table " + tid, e);
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_TOURNYMYTABLE);
		}
		_cat.info("Response TOURNY MY TABLE = " + gr);
		// set the game response in the clients out queue
		handler.putResponse(gr);
	}

	private void tournyRegister(Command ge, Handler handler) throws IOException {
		// GamePlayer pp = (GamePlayer)handler.attachment();
		com.poker.common.message.ResponseString gr = null;
		String tid = ((CommandTournyRegister) ge).getTournyName();
		String uid = ((CommandTournyRegister) ge).getUserName();
		try {
			/**
			 * if (!pp.isAuthenticated()) { // authenticate the client first gr
			 * = new
			 * com.poker.common.message.ResponseString(com.golconda.message
			 * .Response.E_AUTHENTICATE,
			 * com.golconda.message.Response.R_TOURNYREGISTER, tid);
			 * _cat.finest("Response Tourny Register " + gr); // set the game
			 * response in the clients out queue } else { //player is
			 * authenticated
			 **/
			DBPlayer dbp = new DBPlayer();
			dbp.get(uid);
			TournyController tc = TournyController.instance();
			Tourny t = tc.getTourny(tid);
			if (!t.isRegOpen()) {
				// not open for registration
				gr = new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_REGISTERATION_CLOSED,
						com.golconda.message.Response.R_TOURNYREGISTER, tid);
				_cat.info("Tournament not open for Registeration " + gr);
				// set the game response in the clients out queue
			} else { // open for registration
				if (dbp.getPlayChips() < t.buyIn()) { // TODO --diff between
														// play and real chips
					// player is broke
					gr = new com.poker.common.message.ResponseString(
							com.golconda.message.Response.E_BROKE,
							com.golconda.message.Response.R_TOURNYREGISTER, tid);
				} else {
					boolean result = t.register(dbp.getDisplayName());
					if (!result) {
						// already registered
						gr = new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_ALREADY_REGISTERED,
								com.golconda.message.Response.R_TOURNYREGISTER,
								tid);
						_cat.info("Already registered " + gr);
					} else {

						// this will create tourny chips for the player
						int r = t.dbTourny().register(dbp, handler._id, t);
						_cat.info("Registering = " + r);
						if (r > 0) {
							gr = new com.poker.common.message.ResponseString(
									com.golconda.message.Response.E_SUCCESS,
									com.golconda.message.Response.R_TOURNYREGISTER,
									tid);
							t.register(dbp.getDisplayName());
						} else {
							gr = new com.poker.common.message.ResponseString(
									com.golconda.message.Response.E_FAILURE,
									com.golconda.message.Response.R_TOURNYREGISTER,
									tid);
							t.unRegister(dbp.getDisplayName());
						}
					}
					// }
					_cat.info("Registered DETAIL = " + gr);
					// set the game response in the clients out queue
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_TOURNYREGISTER, tid);
		}
		handler.putResponse(gr);
		handler.putResponse(this.getTournyDetail(tid));
	}

	private void tournyUnRegister(Command ge, Handler handler)
			throws IOException {
		// GamePlayer pp = (GamePlayer)handler.attachment();
		com.poker.common.message.ResponseString gr = null;
		String tid = ((CommandTournyUnRegister) ge).getTournyName();
		String uid = ((CommandTournyUnRegister) ge).getUserName();
		try {
			/**
			 * if (!pp.isAuthenticated()) { // authenticate the client first gr
			 * = new
			 * com.poker.common.message.ResponseString(com.golconda.message
			 * .Response.E_AUTHENTICATE,
			 * com.golconda.message.Response.R_TOURNYUNREGISTER, tid);
			 * _cat.finest("Response Tourny Register " + gr); // set the game
			 * response in the clients out queue } else { //player is
			 * authenticated
			 **/
			TournyController tc = TournyController.instance();
			Tourny t = tc.getTourny(tid);
			DBPlayer dbp = new DBPlayer();
			dbp.get(uid);
			if (!t.isRegOpen()) {
				// not open for registration
				gr = new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_REGISTERATION_CLOSED,
						com.golconda.message.Response.R_TOURNYUNREGISTER, tid);
				_cat.finest("Tournament not open for Registeration " + gr);
				// set the game response in the clients out queue
			} else { // open for un- registration

				boolean result = t.unRegister(dbp.getDisplayName());
				if (!result) {
					// not yet registered
					gr = new com.poker.common.message.ResponseString(
							com.golconda.message.Response.E_NOT_REGISTERED,
							com.golconda.message.Response.R_TOURNYREGISTER, tid);
					_cat.finest("Already registered " + gr);
				} else {
					// this will create tourny chips for the player
					int r = t.dbTourny().unRegister(dbp, handler._id, t);

					if (r > 0) {
						gr = new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_SUCCESS,
								com.golconda.message.Response.R_TOURNYUNREGISTER,
								tid);
					} else {
						gr = new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_FAILURE,
								com.golconda.message.Response.R_TOURNYUNREGISTER,
								tid);
					}

				}
				_cat.finest("Un Registered DETAIL = " + gr);
				// set the game response in the clients out queue
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
			gr = new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_TOURNYREGISTER, tid);
		}
		handler.putResponse(gr);
		handler.putResponse(this.getTournyDetail(tid));
	}
	

	private Response getTournyDetail(String tid) throws IOException {
		TournyController tc = TournyController.instance();
		Tourny t = tc.getTourny(tid);
		_cat.finest(tid + " Tourny Details = " + t);
		Response gr = null;
		GameEvent gev = new GameEvent();
		if (t == null) {
			gr = new Response(Response.E_NONEXIST, Response.R_TOURNYDETAIL);
		} else {
			_cat.info("Tourny = " + t.stringValue());
			// if tourny has not yet started send reg details
			String te = t.stringValue();
			Vector games = new Vector();
			if (t.isRegOpen()) {
				_cat.info("Tournament is open for registration " + te);
			} else if (t.isWaiting() || t.isPlaying()) {
				// check which table is assigned to this player
				MTTInterface[] g = t.listAll();
				for (int k = 0; k < g.length; k++) {
					String gid = g[k].name();
					com.golconda.game.resp.Response r = com.golconda.game.Game
							.handle(new GameSummaryImpl(gid, null));
					if (r.getBroadcast().toString().equals("null")) {
						continue;
					}
					games.add(r.getBroadcast().toString());
					_cat.info("Tournament is open for waiting or running "
							+ r.getBroadcast().toString());
				}
			}
			gr = new ResponseTournyDetail(
					com.golconda.message.Response.E_SUCCESS, te,
					(String[]) games.toArray(new String[games.size()]));
			_cat.info("Response TOURNY DETAIL = " + gr);
		}
		return gr;
	}

	private void message(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (PlayerPreferences.isDisableChat(pp.getPreferences())) {
			return; // chat is disabled
		}
		String msg = ((CommandMessage) ge).message();
		String tid = ((CommandMessage) ge).getTableId();
		PokerPresence p = (PokerPresence) pp.presence(tid);

		String unencodedmessage = new String(Base64.decode(msg));
		// look for curse words
		for (Enumeration e = GameServer._chat_subs.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (unencodedmessage.indexOf(key) > 0) {
				unencodedmessage.replaceAll(key, (String) GameServer._chat_subs
						.get(key));
			}
		}
		if (p.isObserver()) {
			unencodedmessage = "(observer) " + unencodedmessage;
		}
		msg = Base64.encodeString(unencodedmessage);

		_cat.finest(pp + msg);
		if (tid == null) {
			// lobby message
			lobbyMessage(msg);
			return;
		}
		deliverMessageResponse(com.golconda.game.Game.handle(new MessagingImpl(
				tid, p, msg)));
		Game g = Game.game(tid);
		//if (g.type().isReal()) {
			Auditor.instance().write(
					tid,
					Game.game(tid).grid(),
					"<chat pos=\"" + p.pos() + "\" name=\"" + p.name()
							+ "\" msg=\"" + msg + "\"/>", false);
		//}
	}

	private void preferences(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (!pp.isAuthenticated()) {
			handler.putResponse(new Response(Response.E_AUTHENTICATE,
					Response.R_PREFERENCES));
			return;
		}

		int pref = ((CommandInt) ge).getIntVal();
		pref &= PlayerPreferences.PLAYER_PREF_MASK;
		pp.getDBPlayer().setPreferences(pref);
		ResponseInt gr = new ResponseInt(1, Response.R_PREFERENCES, pref);
		handler.putResponse(gr);
	}

	private void resetAllIn(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (!pp.isAuthenticated()) {
			handler.putResponse(new Response(Response.E_AUTHENTICATE,
					Response.R_PREFERENCES));
			return;
		}

		ResponseInt gr = null;
		PokerPlayer ppl = pp;
		DBPokerPlayer dbppl = ppl._dbPokerPlayer;
		if (dbppl.resetAllIn()) {
			gr = new ResponseInt(1, Response.R_RESET_ALL_IN, dbppl.getAllIn());
		} else {
			gr = new ResponseInt(Response.E_RESET_ALL_IN_FAILED,
					Response.R_RESET_ALL_IN, dbppl.getAllIn());
		}
		handler.putResponse(gr);
	}

	private void playerSearch(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		String player_name = ((CommandString) ge).getStringVal();

		Vector pgl = new Vector();
		Game gl[] = Game.listAll();
		for (int i = 0; i < gl.length; i++) {
			if (gl[i] instanceof Poker) {
				if (((Poker) gl[i]).getPresenceInGame(player_name) != null) {
					pgl.add(gl[i].details().getBroadcast());
				}
			}
		}
		ResponseTableList gr = new ResponseTableList(
				com.golconda.message.Response.E_SUCCESS, (String[]) pgl
						.toArray(new String[pgl.size()]));
		_cat.info("Response TABLELIST = " + gr);
		// set the game response in the clients out queue
		handler.putResponse(gr);
	}

	private void vote(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (!pp.isAuthenticated()) {
			handler.putResponse(new Response(Response.E_AUTHENTICATE,
					Response.R_VOTE));
			return;
		}
		// check if votes are available
		if (!pp.areVotesAvailable()) {
			handler.putResponse(new Response(Response.E_VOTES_EXHAUSTED,
					Response.R_VOTE));
			return;
		}

		String for_player = ((CommandVote) ge).forPlayer();
		String tid = ((CommandVote) ge).getTid();
		int vt = ((CommandVote) ge).getVoteType();

		Game g = Game.game(tid);
		PokerPresence vp = null;
		PokerPresence[] v = (PokerPresence[]) g.allPlayers(-1);
		int vc = 0;
		for (int i = 0; i < v.length; i++) {
			if (v[i].name().compareToIgnoreCase(for_player) == 0) {
				pp.decrVote();
				if (vt < 0) {
					v[i].addNegativeVote();
				} else {
					v[i].addPositiveVote();
				}
				vc = v[i].vote();
				vp = v[i];
				break;
			}
		}

		if (vp != null && vp.player() instanceof GamePlayer) {
			GamePlayer gp = (GamePlayer) vp.player();
			gp.deliver(new ResponseMessage(1, "type=broadcast,name="
					+ tid
					+ ",message="
					+ Base64
							.encodeString(for_player
									+ "  has been voted against by "
									+ pp.name() + ". You have " + Math.abs(vc)
									+ " Negative votes."), tid));
		}

		Response gr = new Response(1, Response.R_VOTE);
		handler.putResponse(gr);
	}

	private void ping(Command ge, Handler handler) throws IOException {
		GamePlayer pp = (GamePlayer) handler.attachment();
		int ap=0, at=0;
		
		Game[] gm = Game.listAll();;
	    for (int j = 0; j < gm.length; j++) {
	      if (! (gm[j] instanceof Poker)) {
	        continue;
	      }
	      PokerPresence[] p = (PokerPresence[])gm[j].allPlayers( -1);
	      if (p.length > 0){
	    	  at++;
	    	  ap+=p.length;
	      }
	    }
		
		
		handler.putResponse(new ResponsePing(at, ap));
	}
	
	private synchronized void joinPool(Command ge, Handler handler) throws Exception {
		try {
			_cat.info(" joinPool-------- ");
			GamePlayer pp = (GamePlayer) handler.attachment();
			if (pp == null) {
				return;
			}
			_cat.info(pp.name() + " joinPool "+ ge);
			com.golconda.message.Response gr = null;
			if (!pp.isAuthenticated()) {
				// authenticate the client first
				gr = new com.golconda.message.Response(
						com.golconda.message.Response.E_AUTHENTICATE,
						com.golconda.message.Response.R_MOVE);
				// set the game response in the clients out queue
				handler.putResponse(gr);
				return;
			}
			String pool_name = ((CommandJoinPool)ge).getPoolName();
			double amt = ((CommandJoinPool)ge).getAmount();
			
			// check if the player is already sitting
			if(TermHoldem._tpp.onPool(pool_name, pp.name())){
				// authenticate the client first
				gr = new com.golconda.message.Response(
						com.golconda.message.Response.E_ALREADY_LOGGED,
						com.golconda.message.Response.R_MOVE);
				handler.putResponse(gr);
				return;
			}

			_cat.info(pp.name() + " looking for the table ");
			//check if the player is not already sitting
			TermHoldem th = TermHoldem._tpp.getBestTableToJoin(pool_name);
			_cat.info(pp.name() + " Found the table "+ th);

			if (th == null){
				gr = new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_GAME_NOT_ALLOWED,
						com.golconda.message.Response.R_MOVE, "Unable to find a suitable table in the pool !");
				// set the game response in the clients out queue
				handler.putResponse(gr);
			} else if (amt < th.minBet() * 2) {
				gr = new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_BROKE,
						com.golconda.message.Response.R_MOVE, th._name);
				// set the game response in the clients out queue
				handler.putResponse(gr);
				th._isAvailable = true;
			} else {
				com.golconda.game.resp.Response r = pp.addWatch(th._name); // this creates presence
				_cat.info(pp.name() + " add watch successful "+ r.getBroadcast());
				PokerPresence p = (PokerPresence) pp.presence(th._name);
				int pos = th.getNextVacantPosition();
				com.golconda.game.resp.Response r1 = pp.addGame(p, pos, amt);
				_cat.info(pp.name() + " add game successful "+ r1.getBroadcast());
			     ((GamePlayer)(p.player())).deliver(new ResponseTableOpen(th.name(), p.pos(), r1.getBroadcast()));
				handler.putResponse(new ResponseGetChipsIntoGame(1, th._name, amt, pp.playWorth(), 0, pp.realWorth(), 0));
				deliverResponse(r1);
				th._isAvailable = true;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	private synchronized void quickFold(Command ge, Handler handler)
		throws Exception {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (pp == null) {
			return;
		}
		com.golconda.message.Response gr = null;
		if (!pp.isAuthenticated()) {
			// authenticate the client first
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_AUTHENTICATE,
					com.golconda.message.Response.R_MOVE);
			// set the game response in the clients out queue
			handler.putResponse(gr);
			return;
		}
		String gname = ((CommandString)ge).getStringVal();
		Game g = Game.game(gname);
		if (g instanceof TermHoldem){
			TermHoldem th = (TermHoldem)g;
			PokerPresence p = (PokerPresence) pp.presence(gname);
			if(p!=null){
				_cat.info("quick-fold  " + p.name() + " Rcvd QF =" + ge);
				deliverResponse(th.quickFold(p));
			}
			else {
				handler.putResponse(new com.poker.common.message.ResponseString(
						com.golconda.message.Response.E_FAILURE,
						com.golconda.message.Response.R_MOVE, gname));
			}
		}
		else{
			handler.putResponse(new com.poker.common.message.ResponseString(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_MOVE, gname));
		}
	}

	private synchronized void move(Command ge, Handler handler)
			throws Exception {
		GamePlayer pp = (GamePlayer) handler.attachment();
		if (pp == null) {
			return;
		}
		com.golconda.message.Response gr = null;
		if (!pp.isAuthenticated()) {
			// authenticate the client first
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_AUTHENTICATE,
					com.golconda.message.Response.R_MOVE);
			// set the game response in the clients out queue
			handler.putResponse(gr);
			return;
		}
		int move = ((CommandMove) ge).getMove();
		String tid = ((CommandMove) ge).getTableId();
		int grid = ((CommandMove) ge).getHandId();
		String move_details = ((CommandMove) ge).getMoveDetails();
		double amt = ((CommandMove) ge).getMoveAmount();
		int pos = ((CommandMove) ge).getPlayerPosition();

		// check if table is there
		if (Game.game(tid) == null) {
			return;
		}

		// assert pos >= 0:"Postion = " + pos;
		PokerPresence p = (PokerPresence) pp.presence(tid);
		if (p == null) {
			return; // the presence has been removed form the table
		}
		// assert p != null:"For game move presence cannot be null " + pp;
		_cat.finest(p + " Move=" + ge);
		switch (move) {
		/**
		 * MOVE JOIN
		 **/
		case Command.M_JOIN:
		case Command.M_SIT_IN:
			p.unsetResponseReq();
			double cur_amt = p.getWorth();
			if (cur_amt == -1) {
				// failure
				_cat.warning("DB Player absent, unconnected presence "+ p.getWorth());
				handler.putResponse(new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_DB_FAILURE,
								com.golconda.message.Response.R_MOVE, tid));
				return;

			} else if (!((PokerGameType) Game.game(tid).type()).isTourny() && amt > cur_amt) {
				// failure
				_cat.warning("Bringing more amount to the table than in the wallet "+ p.getWorth());
				handler.putResponse(new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_OVER_SPENDING,
								com.golconda.message.Response.R_MOVE, tid));
				return;
				// set the game response in the clients out queue
			} else if (p.isWinLossViolated() && Game.game(tid).type().isReal()) {
				// failure
				_cat.warning("Win loss violated and sitting on a real game " + p);
				handler.putResponse(new com.poker.common.message.ResponseString(
								p.player().getLimitViolation() == 1 ? com.golconda.message.Response.E_WIN_VIOLATED
										: com.golconda.message.Response.E_LOSS_VIOLATED,
								com.golconda.message.Response.R_MOVE, tid));
				return;
				// set the game response in the clients out queue
			}

			double minBet = 0;
			if (((PokerGameType) Game.game(tid).type()).isSitnGo()) {
				SitnGoInterface sng = (SitnGoInterface) Game.game(tid);
				minBet = sng.buyIn() + sng.fees();

				if ((((PokerGameType) Game.game(tid).type()).isReal() && pp.realWorth() < minBet) 
						|| (((PokerGameType) Game.game(tid).type()).isPlay() && pp.playWorth() < minBet)){
					_cat.warning(amt + " Worth less than min required " + minBet);
					handler.putResponse(new com.poker.common.message.ResponseString(
									com.golconda.message.Response.E_BROKE,
									com.golconda.message.Response.R_MOVE, tid));
					// set the game response in the clients out queue
				} else {
					com.golconda.game.resp.Response r = pp.addGame(p, pos, amt);
					deliverResponse(r);
					if (r.success()) {
						handler.putResponse(new ResponseGetChipsIntoGame(1, tid, p.getAmtAtTable(), pp.playWorth(), 0, pp.realWorth(), 0));
						_cat.finest("Putting ResponseGetChipsIntoGame ");
					} else {
						handler.putResponse(new com.poker.common.message.ResponseString(
										com.golconda.message.Response.E_UNABLE_TO_JOIN,
										com.golconda.message.Response.R_MOVE,
										tid));
					}
				}
			}

			else if (((PokerGameType) Game.game(tid).type()).isMTTTourny()) {
				com.golconda.game.resp.Response r = pp.addGame(p, pos, amt);
				deliverResponse(r);
				if (r.success()) {
					handler.putResponse(new ResponseGetChipsIntoGame(1, tid, amt, pp.playWorth(), 0, pp.realWorth(), 0));
				}

			} else if (Game.game(tid) instanceof Poker) {
				Poker pg = (Poker) Game.game(tid);
				minBet = pg.minBet();

				if (amt < minBet * 2) {
					_cat.warning(p.getWorth() + " Worth less than min required " + minBet);
					new com.poker.common.message.ResponseString(
							com.golconda.message.Response.E_BROKE,
							com.golconda.message.Response.R_MOVE, tid);
					// set the game response in the clients out queue
					handler.putResponse(gr);
				} else {
					com.golconda.game.resp.Response r = pp.addGame(p, pos, amt);
					deliverResponse(r);
					if (r.success()) {
						handler.putResponse(new ResponseGetChipsIntoGame(1, tid, amt, pp.playWorth(), 0, pp.realWorth(), 0));
					}
				}
			}
			break;

		case Command.M_CANCEL_JOIN:
			p.unsetRaiseReq();
			if (Game.game(tid) instanceof Poker) {
				Poker pg = (Poker) Game.game(tid);
				pg.waiterRemove(p);
				handler
						.putResponse(new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_REMOVED_FROM_WAITING,
								com.golconda.message.Response.R_MOVE, tid));
			} else {
				handler
						.putResponse(new com.poker.common.message.ResponseString(
								com.golconda.message.Response.E_GAME_NOT_ALLOWED,
								com.golconda.message.Response.R_MOVE, tid));
			}

		
		/**
		 * MOVE ANTE
		 **/
		//
		case Command.M_ANTE:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.ANTE, amt)));

			break;
		/**
		 * MOVE BRINGIN
		 **/
		//
		case Command.M_BRING_IN:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.BRINGIN, amt)));

			break;

		case Command.M_BRING_IN_HIGH:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.BRINGINH, amt)));
			break;

		/**
		 * MOVE BIG BLIND
		 **/
		//
		case Command.M_BIGBLIND:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.BIG_BLIND, amt)));
			break;
		/**
		 * MOVE SMALL BLIND
		 ***/
		//
		case Command.M_SMALLBLIND:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.SMALL_BLIND, amt)));
			break;
		/**
		 * MOVE SMALL BIG
		 ***/
		//
		case Command.M_SBBB:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.SBBB, amt)));
			break;

		
		case Command.M_BET:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.BET, amt)));
			break;
		/**
		 * MOVE RAISE
		 ***/
		//
		case Command.M_RAISE:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.RAISE, amt)));

			break;
		/**
		 * MOVE FOLD
		 ***/
		//
		case Command.M_FOLD:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.FOLD, amt)));
			break;
		
		/**
		 * MOVE BET POT
		 ***/
		//
		case Command.M_BET_POT:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.BET_POT, amt)));

			break;
		/**
		 * /** MOVE ALL IN
		 ***/
		//
		case Command.M_ALL_IN:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.ALL_IN, amt)));

			break;
		/**
		 * MOVE OPT OUT
		 ****/
		//
		case Command.M_OPT_OUT:

			// create a db game player
			// call game server to add this player
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.OPT_OUT, amt)));
			break;
		/**
		 * MOVE RAISE
		 ***/
		//
		case Command.M_CHECK:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.CHECK, amt)));
			break;
		/**
		 * MOVE SEE/CALL
		 ***/
		//
		case Command.M_CALL:
			deliverResponse(com.golconda.game.Game.handle(new MoveImpl(tid, p,
					PokerMoves.CALL, amt)));
			break;

		/**
		 * MOVE LEAVE
		 ***/
		//
		case Command.M_LEAVE:
			com.golconda.game.resp.Response r = pp.leaveGameAndWatch(p);
			if (r != null) {
				deliverResponse(r); 
				handler.putResponse(new ResponseTableDetail(1, r.getCommand(pp.presence(tid))));
			}
			handler.putResponse(new ResponseBuyChips(1, pp.playWorth(), 0, pp.realWorth(), 0));

			_cat.finest("Removed from game " + p);
			break;

		default:

			// unknown move
			// send a failure
			gr = new com.golconda.message.Response(
					com.golconda.message.Response.E_FAILURE,
					com.golconda.message.Response.R_MOVE);
			_cat.warning("Unknown move " + ge);

			// set the game response in the clients out queue
			handler.putResponse(gr);
		}
	}

	/*
	 * Stop signals to suspend all runs of the game, till such time resume is
	 * called. If a run is in progress, the run is completed. Utility method,
	 * available at class level.
	 */

	public static synchronized void suspendAll() {
		Game[] games = Game.listAll();
		for (int i = 0; i < games.length; i++) {
			suspend(games[i].name());
		}
	}

	/*
	 * A previously suspended game can run now. Utility method at class level884
	 */

	public static synchronized void resumeAll() throws IOException {
		GameServer._state = GameServer.NORMAL;
		Game[] games = Game.listAll();
		for (int i = 0; i < games.length; i++) {
			resume(games[i].name());
		}
	}

	public static void suspend(String id) {
		Game.game(id).suspend();
	}

	public static void resume(String id) throws IOException {
		com.golconda.game.resp.Response r = Game.game(id).resume();
		// deliver the response to all players
		com.poker.server.GameProcessor.deliverResponse(r);
	}

	public static synchronized void destroyAll() throws IOException {
		Game[] games = Game.listAll();
		for (int i = 0; i < games.length; i++) {
			destroy(games[i].name());
		}
	}

	public static void destroy(String id) throws IOException {
		// destroy happens for poker game
		Game g = Game.game(id);
		if (g instanceof com.poker.game.poker.Poker) {
			com.poker.game.poker.Poker p = (com.poker.game.poker.Poker) g;
			// inform each player that game is going to be destroyed
			PokerPresence[] pp = p.allPlayers(-1);
			for (int i = 0; i < pp.length; i++) {
				removePresence(pp[i]);
			}
			Game.game(id).destroy();
		}
	}

	public static void removePresence(String name, String gid)
			throws IOException {
		Game g = Game.game(gid);
		if (g instanceof com.poker.game.poker.Poker) {
			com.poker.game.poker.Poker pg = (com.poker.game.poker.Poker) g;
			PokerPresence[] pp = pg.allPlayers(-1);
			for (int i = 0; i < pp.length; i++) {
				if (pp[i].name().equals(name)) {
					removePresence(pp[i]);
				}
			}
		}
	}

	public static void removePresence(PokerPresence p) throws IOException {
		GamePlayer gp = (GamePlayer) p.player();
		CommandMove cm = new CommandMove(gp.session(), Command.M_LEAVE, 0.0, p
				.getGameName());
		com.golconda.game.resp.Response r = gp.leaveGameOnly(p, false);
		if (r != null) {
			_cat.warning(r.getBroadcast());
			deliverResponse(r); // no need to send response as the player has
								// already left the game
		}

		gp.handler().putResponse(
				new ResponseBuyChips(1, gp.playWorth(), 0, gp.realWorth(), 0, p
						.getGameName()));

		gp.handler().putResponse(
				new ResponseString(Response.E_PLAYER_REMOVED, Response.R_ADMIN,
						p.getGameName()));

		_cat.finest("Removed from game " + p);
	}

	public static void broadcast(String message) {
		Iterator enumt = Handler.registry().values().iterator();
		for (; enumt.hasNext();) {
			Handler h = (Handler) enumt.next();
			StringBuilder sbuf = new StringBuilder("type=broadcast").append(
					",message=").append(message);
			ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
			rm.session(h._id);
			h.write(rm);
		}
	}

	public static void broadcastAffiliate(String message, String affiliate) {
		Iterator enumt = Handler.registry().values().iterator();
		for (; enumt.hasNext();) {
			Handler h = (Handler) enumt.next();
			Client c = h.attachment();
			if (c instanceof GamePlayer) {
				GamePlayer gp = (GamePlayer) c;
				if (gp.getDBPlayer().getAffiliate().equals(affiliate)) {
					StringBuilder sbuf = new StringBuilder("type=broadcast")
							.append(",message=").append(message);
					ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
					rm.session(h._id);
					h.write(rm);
				}
			}
		}
	}

	public static void lobbyMessage(String message) {
		Iterator enumt = Handler.registry().values().iterator();
		for (; enumt.hasNext();) {
			Handler h = (Handler) enumt.next();
			Client c = h.attachment();
			if (c instanceof GamePlayer) {
				GamePlayer gp = (GamePlayer) c;
				if (gp.isAuthenticated()) {
					StringBuilder sbuf = new StringBuilder("type=lobby,name=");
					sbuf.append(gp.name());
					sbuf.append(",message=").append(message);
					ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
					rm.session(h._id);
					h.write(rm);
				}
			}
		}
	}

	public static void broadcastGame(String gid, String message)
			throws IOException {
		deliverMessageResponse(com.golconda.game.Game.handle(new MessagingImpl(
				gid, null, message)));
	}

	public static void broadcastPlayer(String tid, String session, String message)
			throws IOException {
		Handler h = Handler.get(session);
		StringBuilder sbuf = new StringBuilder("type=broadcast");
		sbuf.append(",message=").append(message);
		sbuf.append(",tid=").append(tid);
		ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
		rm.session(h._id);
		h.write(rm);
	}

	public static void broadcastPlayer(String session, String message)
			throws IOException {
		Handler h = Handler.get(session);
		StringBuilder sbuf = new StringBuilder("type=broadcast");
		sbuf.append(",message=").append(message);
		ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
		rm.session(h._id);
		h.write(rm);
	}
} // end Poker Processor
