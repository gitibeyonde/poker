package com.poker.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agneya.util.ConnectionManager;
import com.golconda.db.DBException;
import com.poker.game.PokerGameType;

// SQLSERVER/ORACLE

public class GameRunSession {
	// set the category for logging
	transient static Logger _cat = Logger.getLogger(GameRunSession.class
			.getName());

	private long gameRunId;
	private String gameId;
	private int gameType;
	private String gameTypeString;
	private Timestamp startTime;
	private Timestamp endTime;
	private double rake;
	private double pot;
	private double start_worth;
	private double end_worth;
	private String sessionId;
	private String displayName;
	private int position;
	private double winAmount;
	private double winAmountSum;
	public static final double MIN_HAND_RAKE = 0.50;
	public static final String GAME_RUN_ID;
	public static final String GAME_NAME;
	public static final String GAME_TYPE;
	public static final String START_TIME;
	public static final String END_TIME;
	public static final String RAKE;
	public static final String POT;
	public static final String START_WORTH;
	public static final String END_WORTH;
	public static final String SESSION_ID;
	public static final String POSITION;
	public static final String WIN_AMOUNT;
	public static final String USER_ID;
	Connection _conn;

	static {
		GAME_RUN_ID = "GAME_RUN_ID_SEQ_PK";
		SESSION_ID = "SESSION_ID_FK";
		POSITION = "PLAYER_POSITION";
		WIN_AMOUNT = "WIN_AMOUNT";
		USER_ID = "USER_ID_FK";
		START_TIME = "START_TIME";
		END_TIME = "END_TIME";
		RAKE = "RAKE";
		POT = "POT";
		START_WORTH = "START_WORTH";
		END_WORTH = "END_WORTH";
		GAME_NAME = "GAME_NAME_FK";
		GAME_TYPE = "GAME_TYPE_ID_FK";
	}

	public GameRunSession() {

	}

	public GameRunSession(String gid, long grid, int type) {
		gameRunId = grid;
		gameId = gid;
		gameType = type;
	}

	public long getGameRunId() {
		return gameRunId;
	}

	public void setGameRunId(long v) {
		this.gameRunId = v;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String v) {
		this.gameId = v;
	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int v) {
		this.gameType = v;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp v) {
		this.startTime = v;
	}

	public String getStartTimeString() {
		return startTime.toString();
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp v) {
		this.endTime = v;
	}

	public String getEndTimeString() {
		return endTime.toString();
	}

	public double getRake() {
		return rake;
	}

	public void setRake(double v) {
		this.rake = v;
	}

	public double getPot() {
		return pot;
	}

	public void setPot(double v) {
		this.pot = v;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String v) {
		this.displayName = v;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String v) {
		this.sessionId = v;
	}

	public double getStartWorth() {
		return start_worth;
	}

	public void setStartWorth(double v) {
		this.start_worth = v;
	}

	public double getEndWorth() {
		return end_worth;
	}

	public void setEndWorth(double v) {
		this.end_worth = v;
	}

	public double getWinAmount() {
		return winAmount;
	}

	public void setWinAmount(double v) {
		this.winAmount = v;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int v) {
		this.position = v;
	}

	public double getWinAmountSum() {	return winAmountSum;	}
	public void setWinAmountSum(double winAmountSum) {	this.winAmountSum = winAmountSum;	}

	public String getGameTypeString() {
		switch (gameType) {
		case PokerGameType.Play_Holdem:
			return "Fun Holdem";
		case PokerGameType.Play_Badugi:
			return "Fun Badugi";
		case PokerGameType.Play_OmahaHi:
			return "Fun OmahaHi";
		case PokerGameType.Play_OmahaHiLo:
			return "Fun OmahaHiLo";
		case PokerGameType.Play_Stud:
			return "Fun Stud";
		case PokerGameType.Play_StudHiLo:
			return "Fun StudHiLo";
		case PokerGameType.Play_TermHoldem:
			return "Fun Terminal Holdem";
		case PokerGameType.Real_Holdem:
			return "Real Holdem";
		case PokerGameType.Real_Badugi:
			return "Real Badugi";
		case PokerGameType.Real_OmahaHi:
			return "Real OmahaHi";
		case PokerGameType.Real_OmahaHiLo:
			return "Real OmahaHiLo";
		case PokerGameType.Real_Stud:
			return "Real Stud";
		case PokerGameType.Real_StudHiLo:
			return "Real StudHiLo";
		case PokerGameType.Real_TermHoldem:
			return "Real Terminal Holdem";
		default:
			return "" + gameType;
		}
	}

	public void setGameTypeString(String gameTypeString) {
		this.gameTypeString = gameTypeString;
	}

	public Statement startBatch() throws DBException {
		try {
			// _cat.finest("Starting transaction ---");
			_conn = ConnectionManager.getConnection("GameEngine");
			_conn.setAutoCommit(false);
			return _conn.createStatement();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to get Connection" + e.getMessage(),
					e);
			throw new DBException(e.getMessage()
					+ " --Unable to get Connection");
		}
	}

	public void save(Statement ps) throws DBException {
		StringBuilder sb = new StringBuilder();
		// _cat.finest("Adding batch ---" + this);
		try {
			sb.append("insert into T_PLAYER_PER_GRS (");
			sb.append(GAME_RUN_ID).append(",");
			sb.append(GAME_NAME).append(",");
			sb.append(GAME_TYPE).append(",");
			sb.append(SESSION_ID).append(",");
			sb.append(USER_ID).append(",");
			sb.append(POSITION).append(",");
			sb.append(WIN_AMOUNT).append(",");
			sb.append(POT).append(",");
			sb.append(START_WORTH).append(",");
			sb.append(END_WORTH).append(",");
			sb.append(START_TIME).append(",");
			sb.append(END_TIME).append(",");
			sb.append(RAKE).append(")");
			sb.append(" values (");
			sb.append(gameRunId).append(" ,'");
			sb.append(gameId).append("' ,'");
			sb.append(gameType).append("' ,'");
			sb.append(sessionId).append("' ,'");
			sb.append(displayName).append("' ,");
			sb.append(position).append(" ,");
			sb.append(winAmount).append(" ,");
			sb.append(pot).append(" ,");
			sb.append(start_worth).append(" ,");
			sb.append(end_worth).append(" ,'");
			sb.append(new java.sql.Timestamp(startTime.getTime())).append(
					"' ,'");
			sb.append(new java.sql.Timestamp(endTime.getTime())).append("' ,");
			sb.append(rake).append(")");
			_cat.finest(sb.toString());
			ps.addBatch(sb.toString());
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to save  GRS" + e.getMessage(), e);
			_cat.severe(sb.toString());

			try {
				if (ps != null) {
					ps.getConnection().rollback();
					ps.close();
					ps = null;
				}
			} catch (SQLException se) {
				// ignore
			}
			throw new DBException(e.getMessage() + " ---Unable to save  GRS");
		} finally {
			try {
				if (ps != null) {
					ps.getConnection().rollback();
					ps.close();
					ps = null;
				}
			} catch (SQLException se) {
				// ignore
			}
		}
	}

	public void save() throws DBException {
		StringBuilder sb = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;

		// _cat.finest("Saving batch ---" + this);
		try {
			sb.append("insert into T_PLAYER_PER_GRS (");
			sb.append(GAME_RUN_ID).append(",");
			sb.append(GAME_NAME).append(",");
			sb.append(GAME_TYPE).append(",");
			sb.append(SESSION_ID).append(",");
			sb.append(USER_ID).append(",");
			sb.append(POSITION).append(",");
			sb.append(WIN_AMOUNT).append(",");
			sb.append(POT).append(",");
			sb.append(START_WORTH).append(",");
			sb.append(END_WORTH).append(",");
			sb.append(START_TIME).append(",");
			sb.append(END_TIME).append(",");
			sb.append(RAKE).append(")");
			sb.append(" values (");
			sb.append(gameRunId).append(" ,'");
			sb.append(gameId).append("' ,'");
			sb.append(gameType).append("' ,'");
			sb.append(sessionId).append("' ,'");
			sb.append(displayName).append("' ,");
			sb.append(position).append(" ,");
			sb.append(winAmount).append(" ,");
			sb.append(pot).append(" ,");
			sb.append(start_worth).append(" ,");
			sb.append(end_worth).append(" ,'");
			sb.append(new java.sql.Timestamp(startTime.getTime())).append(
					"' ,'");
			sb.append(new java.sql.Timestamp(endTime.getTime())).append("' ,");
			sb.append(rake).append(")");
			_cat.finest(sb.toString());
			conn = ConnectionManager.getConnection("GameEngine");
			ps = conn.prepareStatement(sb.toString());
			ps.executeUpdate();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to save  GRS" + e.getMessage(), e);
			_cat.severe(sb.toString());

			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}

			} catch (SQLException se) {
				// ignore
			}
			throw new DBException(e.getMessage() + " ---Unable to save  GRS");
		} finally {
			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException se) {
				// ignore
			}
		}
	}

	public int commitBatch(Statement ps) throws DBException {
		try {
			_cat.finest("Committting batch ---");
			int r[] = ps.executeBatch();
			_conn.commit();
			ps.close();
			_conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to save  GRS" + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.getConnection().rollback();
					ps.close();
					ps = null;
				}
			} catch (SQLException se) {
				// ignore
			}
			throw new DBException(e.getMessage() + " ---Unable to save  GRS");
		} finally {
			try {
				if (ps != null) {
					ps.getConnection().rollback();
					ps.close();
					ps = null;
				}
			} catch (SQLException se) {
				// ignore
			}
		}

		return 0;
	}

	public static GameRunSession[] getPlayerGrs(String userid, int count) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where user_id_fk ='"
							+ userid + "' order by  end_time desc limit "
							+ count);
			ps = conn.prepareStatement(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public static GameRunSession[] getLogsForSession(String userid,
			String session) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where SESSION_ID_FK='"
							+ session + "' and user_id_fk ='" + userid
							+ "' order by  end_time desc");
			ps = conn.prepareStatement(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public static GameRunSession[] getLogsForDuration(Timestamp t1,
			Timestamp t2, String userid) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where end_time between date(?) and date(?)  and user_id_fk =? order by  end_time desc");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			ps.setString(3, userid);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public double rakeSum;
	public double potSum;
	public int handCount;

	public double getRakeSum() {
		return rakeSum;
	}

	public void setRakeSum(double rakeSum) {
		this.rakeSum = rakeSum;
	}

	public double getPotSum() {
		return potSum;
	}

	public void setPotSum(double potSum) {
		this.potSum = potSum;
	}

	public int getHandCount() {
		return handCount;
	}

	public void setHandCount(int handCount) {
		this.handCount = handCount;
	}

	public static synchronized GameRunSession getSumForDuration(Timestamp t1,
			Timestamp t2, String userid) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select sum(POT), sum(RAKE) from T_PLAYER_PER_GRS");
			sb.append(" where ");
			sb.append("end_time between date(?) and date(?)");
			sb.append(" and ");
			sb.append("GAME_TYPE_ID_FK between ").append(
					PokerGameType.Real_Holdem);
			sb.append(" and ").append(PokerGameType.RU);
			sb.append(" and ");
			sb.append("user_id_fk =?");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			ps.setString(3, userid);
			System.out.println(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				// if(PokerGameType.isReal(r.getInt(3))){//it is isReal()
				// _grs.potSum = _grs.potSum + r.getDouble(1);
				// _grs.rakeSum = _grs.rakeSum + r.getDouble(2);
				// }
				_grs.potSum = r.getDouble(1);
				_grs.rakeSum = r.getDouble(2);
			}
			// System.out.println("_grs.rakeSum="+_grs.rakeSum);
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to get Pot, Rake " + e.getMessage(),
					e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession getSumForModule(Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select sum(POT), sum(RAKE) from T_PLAYER_PER_GRS");
			sb.append(" where ");
			sb.append("end_time between date(?) and date(?)");
			sb.append(" and ");
			sb.append("GAME_TYPE_ID_FK between ").append(
					PokerGameType.Real_Holdem);
			sb.append(" and ").append(PokerGameType.RU);
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			System.out.println(sb.toString());
			ResultSet r = ps.executeQuery();
			double _pot = 0, _rake = 0;
			while (r.next()) {
				// if(PokerGameType.isReal(r.getInt(3))){
				// _pot = _pot + r.getDouble(1);
				// _rake = _rake + r.getDouble(2);
				// }
				_grs.potSum = r.getDouble(1);
				_grs.rakeSum = r.getDouble(2);
			}

			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to get Pot, Rake " + e.getMessage(),
					e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession[] getLogsForDuration(Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where end_time between date(?) and date(?) order by  end_time desc");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			System.out.println(t1 + "........" + t2);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public GameRunSession[] getPlayerGrs(String userid, String session) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where user_id_fk ='"
							+ userid + "'  SESSION_ID_FK='" + session
							+ "' order by  end_time desc limit " + 200);
			ps = conn.prepareStatement(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs" + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("GameRunSession:\n");
		str.append("GameRunId = ").append(getGameRunId()).append("\n");
		str.append("GameName = ").append(getGameId()).append("\n");
		str.append("StartTime = ").append(getStartTime()).append("\n");
		str.append("EndTime = ").append(getEndTime()).append("\n");
		str.append("Rake = ").append(getRake()).append("\n");
		str.append("Pot = ").append(getPot()).append("\n");
		str.append("Start = ").append(getStartWorth()).append("\n");
		str.append("End = ").append(getEndWorth()).append("\n");
		str.append("SessionId = ").append(getSessionId()).append("\n");
		str.append("DisplayName = ").append(getDisplayName()).append("\n");
		str.append("Position = ").append(getPosition()).append("\n");
		str.append("WinAmount = ").append(getWinAmount()).append("\n");
		return (str.toString());
	}

	public static GameRunSession[] getLogsForTable(String tableName) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where GAME_NAME_FK='"
							+ tableName + "' group by  GAME_RUN_ID_SEQ_PK desc");
			ps = conn.prepareStatement(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public static GameRunSession[] getLogsForDuration(String tableName,
			Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();

		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where ");
			if (!tableName.equals(""))
				sb.append("GAME_NAME_FK='" + tableName + "' and ");
			if (t1 != null && t2 != null)
				sb.append("end_time between date(?) and date(?) ");
			sb.append("group by GAME_RUN_ID_SEQ_PK");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				GameRunSession dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);
				dbp.add(dp);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return (GameRunSession[]) dbp.toArray(new GameRunSession[dbp.size()]);
	}

	public static GameRunSession getUserRakeSum(String userid, Timestamp t1,
			Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dto = new Vector();
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select sum(RAKE) from T_PLAYER_PER_GRS where user_id_fk like binary '%"
							+ userid + "%'");
			sb.append(" and ");
			sb.append("END_TIME between date(?) and date(?)");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			System.out.println(sb.toString());
			ResultSet r = ps.executeQuery();
			double sum = 0;
			if (r.next()) {
				_grs.rakeSum = r.getDouble(1);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get User Rakesum " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession getRakeSum(String userid, Timestamp t1,
			Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dto = new Vector();
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select GAME_TYPE_ID_FK, rake from T_PLAYER_PER_GRS where user_id_fk =?");
			sb.append(" and ");
			sb.append("END_TIME between date(?) and date(?)");
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, userid);
			ps.setTimestamp(2, t1);
			ps.setTimestamp(3, t2);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			double sum = 0;
			// int count = 0;
			while (r.next()) {
				if (PokerGameType.isPlay(r.getInt(1))) { // it is isReal()
					if (r.getDouble(2) >= MIN_HAND_RAKE) {
						sum += r.getDouble(2);
						// count++;
					}
				}
			}
			_grs.rakeSum = sum;
			// _grs.handCount = count;
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to get Rakesum " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession getTotalRake() {
		Connection conn = null;
		PreparedStatement ps = null;
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select sum(RAKE) from T_PLAYER_PER_GRS");
			ps = conn.prepareStatement(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			if (r.next()) {
				_grs.rakeSum = r.getDouble(1);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get Total Rake " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession getRakeForDuration(Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select sum(RAKE) RAKESUM from T_PLAYER_PER_GRS where");
			sb.append(" END_TIME between date(?) and date(?) and rake<>0");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			if (r.next()) {
				_grs.rakeSum = r.getDouble("RAKESUM");
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get User Rakesum " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession getDailyRakeReport(Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		GameRunSession _grs = new GameRunSession();
		int pref = -1;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select sum(pot) TOTAL_POT, sum(rake) TOTAL_RAKE  from T_PLAYER_PER_GRS where");
			sb.append(" END_TIME between date(?) and date(?)");
			sb.append(" and ");
			sb.append("GAME_TYPE_ID_FK between ").append(
					PokerGameType.Real_Holdem);
			sb.append(" and ").append(PokerGameType.RU);
			sb.append("");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			if (r.next()) {
				_grs.potSum = _grs.rakeSum = r.getDouble("RAKESUM");
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get User Rakesum " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return _grs;
	}

	public static GameRunSession getHandLogs(long handId) {
		Connection conn = null;
		PreparedStatement ps = null;
		GameRunSession dp = null;
		int pref = -1;
		try {
			/**
			 * Create a transactio in the DBWallet
			 */
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select * from T_PLAYER_PER_GRS where GAME_RUN_ID_SEQ_PK="
							+ handId + " group by  GAME_RUN_ID_SEQ_PK desc");
			ps = conn.prepareStatement(sb.toString());
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			while (r.next()) {
				dp = new GameRunSession();
				dp.gameRunId = r.getInt(GAME_RUN_ID);
				dp.gameId = r.getString(GAME_NAME);
				dp.position = r.getInt(POSITION);
				dp.winAmount = r.getDouble(WIN_AMOUNT);
				dp.startTime = r.getTimestamp(START_TIME);
				dp.endTime = r.getTimestamp(END_TIME);
				dp.rake = r.getDouble(RAKE);
				dp.pot = r.getDouble(POT);
				dp.start_worth = r.getDouble(START_WORTH);
				dp.end_worth = r.getDouble(END_WORTH);
				dp.gameType = r.getInt(GAME_TYPE);

			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE,
					"Unable to get top players per grs " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return dp;
	}

	public static int getNoOfRecords(Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		int rowCount = 0;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			StringBuilder sb = new StringBuilder(
					"select count(*)from T_PLAYER_PER_GRS");
			sb.append(" where ");
			sb.append("end_time between date(?) and date(?)");
			// sb.append(" and ");
			// sb.append("GAME_TYPE_ID_FK between ").append(PokerGameType.Real_Holdem);
			// sb.append(" and ").append(PokerGameType.RU);
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			ResultSet r = ps.executeQuery();
			double _pot = 0, _rake = 0;
			if (r.next()) {
				rowCount = r.getInt(1);
			}
			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to get Pot, Rake " + e.getMessage(),
					e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return rowCount;
	}

	// for get the list of played users for the day
	public static Vector getPlayedUsers(Timestamp t1, Timestamp t2) {
		Connection conn = null;
		PreparedStatement ps = null;
		Vector dbp = new Vector();
		int pref = -1;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			// conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder(
					"select user_id_fk from T_PLAYER_PER_GRS");
			sb.append(" where ");
			sb.append("end_time between date(?) and date(?)");
			sb.append(" group by user_id_fk");
			ps = conn.prepareStatement(sb.toString());
			ps.setTimestamp(1, t1);
			ps.setTimestamp(2, t2);
			_cat.finest(sb.toString());
			// System.out.println(sb.toString());
			ResultSet r = ps.executeQuery();

			while (r.next()) {

				dbp.add(r.getString(1));
			}

			r.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			_cat.log(Level.SEVERE, "Unable to get users " + e.getMessage(), e);

			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				// ignore
			}
		}
		return dbp;
	}

}
