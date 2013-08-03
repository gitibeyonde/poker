package com.golconda.db;

import com.agneya.util.DBUtils;

import java.util.logging.Logger;


// SQLSERVER/ORACLE

public class GameSequence {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(GameSequence.class.getName());
  final static String SEQ_NAME = "game_id_seq";

  private GameSequence() {
  }

  public static int getNextGameId() throws DBException {
    return DBUtils.getNextSeq(SEQ_NAME);
  }
}
