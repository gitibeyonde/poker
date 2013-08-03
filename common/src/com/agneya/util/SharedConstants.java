/*
 * Created on 23.05.2003
 *
 */
package com.agneya.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * @author yuriy
 *
 */
public final class SharedConstants {

    public static final boolean IS_LOGGED = true;

    public static final String SESSIONID = "sessionid";
    public static final String USERID = "userid";


    public static final int CHIP_VALUE = 1; // cents ?
    public static final int TIMER_TACT = 1000 / 48;
    public static final int GAME_PAUSE = 5*1000;
    /**
     * The one day in milliseconds.
     */
    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 1000 * 60;
    public static final long ONE_HOUR = 1000 * 60 * 60;
    public static final long ONE_DAY = 1000 * 60 * 60 * 24;
    public static final long ONE_YEAR = (long) 1000 * 60 * 60 * 24 * 365;
    public static final int MAX_PLAYER_COUNT = 2;

	public static final Integer[] INT_ARRAY = {
			new Integer(0),	new Integer(1),
			new Integer(2),	new Integer(3),
			new Integer(4),	new Integer(5),
			new Integer(6),	new Integer(7),
			new Integer(8),	new Integer(9)};

    public static double getHoursBetween(Date d1, Date d2) {
        return (double) Math.abs(d2.getTime() - d1.getTime()) / ONE_HOUR;
    }


    private static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

    public static String chipToString(double chip) {
        return new DecimalFormat("###", dfs).format(Math.round(chip)).toString();
    }

    public static String chipToMoneyString(double chip) {
        return moneyToString(chip);
    }

    public static String doubleToString(double value) {
        return new DecimalFormat("###,##0.00", dfs).format(value).toString();
    }

    public static String intToString(double value) {
        return new DecimalFormat("###,###", dfs).format(value).toString();
    }

	public static String intTo3NumSeparetedString(double value) {
		return new DecimalFormat("###,###", dfs).format(value).toString();
	}

    public static String percentsToString(double percents) {
        return doubleToString(percents) + "%";
    }

    public static String percentsToIntString(double percents) {
        return intToString(percents) + "%";
    }

    public static String moneyToString(double money) {
        return new DecimalFormat("$###,###,##0.00", dfs).format(money).toString();
    }

    public static String decimal2digits(double value) {
        return new DecimalFormat("#####0.00", dfs).format(value).toString();
    }

    public static void clear(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    public static Date getTodayStart() {
        Calendar c = Calendar.getInstance();
        clear(c);
        return c.getTime();
    }

    public static Date getDateStart(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        clear(c);
        return c.getTime();
    }

    public static Date getDateEnd(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        clear(c);
        return new Date(c.getTimeInMillis() + ONE_DAY - 1);
    }

    public static double getHoursElapsedToday() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60 + c.get(Calendar.SECOND) / 3600;
    }

    public static boolean equals(Object o1, Object o2) {
        return
                (o1 == null && o2 == null) ||
                (o1 != null && o1.equals(o2));
    }

    public static String getLikeString(String s) {
        return s == null ? "%" : "%" + s + "%";
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }

    public static String timeToPeriodString(long millis) {
        long year = millis / SharedConstants.ONE_YEAR;
        millis -= year * SharedConstants.ONE_YEAR;
        long day = millis / SharedConstants.ONE_DAY;
        millis -= day * SharedConstants.ONE_DAY;
        long hour = millis / SharedConstants.ONE_HOUR;
        millis -= hour * SharedConstants.ONE_HOUR;
        long min = millis / SharedConstants.ONE_MINUTE;
        millis -= min * SharedConstants.ONE_MINUTE;
        long sec = millis / SharedConstants.ONE_SECOND;
        millis -= sec * SharedConstants.ONE_SECOND;
        StringBuilder s = new StringBuilder();
        if (year != 0) s.append(year + " y ");
        if (day != 0) s.append(day + " d ");
        if (hour != 0) s.append(hour + " h ");
        if (min != 0) s.append(min + " m ");
        if (sec != 0) s.append(sec + " s");
        return s.toString();
    }

    public static String fillString(String s, int len) {
        int slen = s.length();
        if (len <= slen) return s;
        char[] c = new char[len - s.length()];
        Arrays.fill(c, ' ');
        StringBuilder sb = new StringBuilder(s);
        sb.append(c);
        return sb.toString();
    }

    public static String objAddress(Object obj) {
        String s = obj.toString();
        return s.substring(s.indexOf("@"), s.length());
    }

    public static boolean checkPassword(String password) {
        return  password != null && password.length() > 0 &&
                password.charAt(0) != ' ' && password.charAt(password.length() - 1) != ' ';
    }

    public static boolean checkEmail(String email) {
        return email != null && email.indexOf('@') != -1;
    }

}
