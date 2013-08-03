package com.agneya.util;

import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * Utility class containing functions for Date Manipulations required for
 * web module.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author $AUTHOR$
 * @version $VERSION$
 */
public class DateUtilReports {

  public DateUtilReports() {
  }

  /**
   * Parses the date String according the format provided and
   * returns a Date Object.
   * @param date String
   * @param format String
   * @return Date
   */
  public static Date parseDate(String date, String format) {
    Date returnDate = null;
    SimpleDateFormat sdf = new SimpleDateFormat(format);

    // Get the Date Object from the startTime and endTime String.
    try {
      returnDate = (Date) sdf.parse(date);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    return returnDate;
  }

  /**
   * Compares two dates and returns a positive integer if date1>date2
   * else a -ve integer.
   * @param date1 Date
   * @param date2 Date
   * @return int
   */
  public static long compare(Date date1, Date date2) {

    return (date1.getTime() - date2.getTime());
  }

}
