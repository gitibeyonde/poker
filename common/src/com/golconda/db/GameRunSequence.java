package com.golconda.db;

import com.agneya.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class GameRunSequence {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(GameRunSequence.class.getName());
  static int _seq = -1;
  static int _count = 0;
  final static String SEQ_NAME = "game_run_id_seq";
  final static int INCR = 100;

  public GameRunSequence() throws DBException {
    if (ConnectionManager.isOracle()) {
        _seq = getNextOracle(SEQ_NAME);
      }
      else {
        _seq = getNextMicrosoft(SEQ_NAME);
      }

  }

  public int getNextOracle(String SEQ_NAME) throws DBException {
    int seq = -1;
    Connection conn = null;
    Statement st = null;
    ResultSet r = null;
    try {
      conn = ConnectionManager.getConnection("GameEngine");
      st = conn.createStatement();
      r = st.executeQuery(
          "select " + SEQ_NAME + ".nextval from dual");
      if (r.next()) {
        seq = r.getInt(1);
      }
      r.close();
      st.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.log( Level.SEVERE,"SQLException generated in game run sequence generation", e);
      try {
        if (st != null) {
          st.close();
          st=null;
        }
        if (r != null) {
          r.close();
          r=null;
        }

        if (conn != null) {
          conn.rollback();
          conn.close();
          conn=null;
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage());
    }
    finally {
      try {
        if (st != null) {
          st.close();
          st=null;
        }
        if (r != null) {
          r.close();
          r=null;
        }

        if (conn != null) {
          conn.rollback();
          conn.close();
          conn=null;
        }
      }
      catch (SQLException se) {
        //ignore
      }

    }

    _cat.finest("Getting sequence " + seq);
    return seq;
  }

  public  int getNextMicrosoft(String SEQ_NAME) throws DBException {
    int seq = -1;
    Connection conn = null;
    Statement st = null;
    ResultSet r = null;

    try {
      conn = ConnectionManager.getConnection("GameEngine");
      conn.setAutoCommit(false);
      st = conn.createStatement();
      r = st.executeQuery(
          "select counter_value from T_COUNTERS where  counter_name='" +
          SEQ_NAME + "'");
      if (r.next()) {
        seq = r.getInt(1) + 100;
      }
      st.close();
      st = conn.createStatement();
      st.executeUpdate("update T_COUNTERS set counter_value=" + seq +
                       " where  counter_name='" + SEQ_NAME + "'");

      r.close();
      st.close();
      conn.commit();
      conn.close();
    }
    catch (SQLException e) {
      _cat.log( Level.SEVERE,"SQLException generated in game run sequence generation", e);
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
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage());

    }
    finally {
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
      }
      catch (SQLException se) {
        //ignore
      }

    }

    _cat.finest("Getting sequence " + seq);
    return seq;
  }

  public int getNextGameRunId() throws DBException {
    if (_count > INCR - 1) {
      _count = 0;
      if (ConnectionManager.isOracle()) {
        return _seq = getNextOracle(SEQ_NAME);
      }
      else {
        return _seq = getNextMicrosoft(SEQ_NAME);
      }
    }
    return _seq + _count++;
  }
}
