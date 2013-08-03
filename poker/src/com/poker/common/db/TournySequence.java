package com.poker.common.db;

import com.agneya.util.DBUtils;

import com.golconda.db.DBException;

import java.util.logging.Logger;


public class TournySequence {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(TournySequence.class.getName());
  final static String SEQ_NAME="tourny_id_seq";

  private TournySequence() {
  }


 public static int getNextGameId()throws DBException {
   return DBUtils.getNextSeq(SEQ_NAME);
 }

}
