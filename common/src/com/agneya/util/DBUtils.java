package com.agneya.util;


import com.golconda.db.DBException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class DBUtils {
	// set the category for logging
	transient static Logger _cat = Logger.getLogger(DBUtils.class.getName());

	private DBUtils() {
	}

	public static int getNextOracle(String SEQ_NAME) throws DBException {
		int seq = -1;
		Connection conn = null;
		Statement st = null;
		ResultSet r = null;
		try {
			conn = ConnectionManager.getConnection("GameEngine");
			st = conn.createStatement();
			r = st.executeQuery("select " + SEQ_NAME + ".nextval from dual");
			if (r.next()) {
				seq = r.getInt(1);
			}
			r.close();
			st.close();
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			_cat.log( Level.SEVERE, 
							"SQLException generated in game run sequence generation",
							e);
			try {
				if (st != null) {
					st.close();
				}
				if (r != null) {
					r.close();
				}

				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				//ignore
			}
			throw new DBException(e.getMessage());
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (r != null) {
					r.close();
				}

				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				//ignore
			}

		}

		_cat.finest("Getting sequence " + seq);
		return seq;
	}

	public static int getNextMicrosoft(String SEQ_NAME) throws DBException {
		int seq = -1;
		Connection conn = null;
		Statement st = null;
		ResultSet r = null;

		try {
			conn = ConnectionManager.getConnection("GameEngine");
			conn.setAutoCommit(false);
			st = conn.createStatement();
			r = st
					.executeQuery("select counter_value from T_COUNTERS where  counter_name='"
							+ SEQ_NAME + "'");
			if (r.next()) {
				seq = r.getInt(1) + 1;
			}
			st.close();
			st = conn.createStatement();
			st.executeUpdate("update T_COUNTERS set counter_value=" + seq
					+ " where  counter_name='" + SEQ_NAME + "'");

			r.close();
			st.close();
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			_cat
					.log( Level.SEVERE,
							"SQLException generated in game run sequence generation",
							e);
			try {
				if (st != null) {
					st.close();
				}
				if (r != null) {
					r.close();
				}

				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				//ignore
			}
			throw new DBException(e.getMessage());

		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (r != null) {
					r.close();
				}

				if (conn != null) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException se) {
				//ignore
			}

		}

		_cat.finest("Getting sequence " + seq);
		return seq;
	}


	public static int getNextOracle(Connection conn, String SEQ_NAME) throws SQLException {
		int seq = -1;
		Statement st = null;
		ResultSet r = null;

		st = conn.createStatement();
		r = st.executeQuery("select " + SEQ_NAME + ".nextval from dual");
		if (r.next()) {
			seq = r.getInt(1);
		}
		r.close();
		st.close();
		_cat.finest("Getting sequence " + seq);
		return seq;
	}



	public static int getNextMicrosoft(Connection conn, String SEQ_NAME)
			throws SQLException {
		int seq = -1;
		Statement st = null;
		ResultSet r = null;

		st = conn.createStatement();
		r = st
				.executeQuery("select counter_value from T_COUNTERS where  counter_name='"
						+ SEQ_NAME + "'");
		if (r.next()) {
			seq = r.getInt(1) + 1;
		}
		st.close();
		st = conn.createStatement();
		st.executeUpdate("update T_COUNTERS set counter_value=" + seq
				+ " where  counter_name='" + SEQ_NAME + "'");

		r.close();
		st.close();

		_cat.finest("Getting sequence " + seq);
		return seq;
	}

	public static int getNextSeq(String seq_name) throws DBException {
		if (ConnectionManager.isOracle()) {
			return getNextOracle(seq_name);
		} else {
			return getNextMicrosoft(seq_name);
		}
	}
	public static int getNextSeq(Connection conn, String seq_name) throws SQLException {
		if (ConnectionManager.isOracle()) {
			return getNextOracle(conn, seq_name);
		} else {
			return getNextMicrosoft(conn, seq_name);
		}
	}

}
