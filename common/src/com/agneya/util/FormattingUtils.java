package com.agneya.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

//import com.agneya.web.Constants;

//import net.sf.javainetlocator.InetAddressLocator;
//import net.sf.javainetlocator.InetAddressLocatorException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FormattingUtils {

  public static final String restrict_countries[] = {
      "AU", "SW"};

  public FormattingUtils() {
  }

  public static double[] integralDivide(double amt, int count) {
    double part[] = new double[count];
    int amount = (int) (amt * 100);
    int int_part = amount / count;
    int remainder = (amount % count);
    for (int i = 0; i < count; i++) {
      part[i] = int_part / 100.00;
    }
    for (int r = remainder, i = 0; r > 0; r--, i++) {
      part[i] += 0.01;
    }
    return part;
  }

  public static double getRounded(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(2, bg.ROUND_HALF_UP);
    val = bg.doubleValue();
    return val;
  }

  public static BigDecimal getRoundedBigDecimal(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(2, bg.ROUND_HALF_UP);
    return bg;
  }
  public static String getRoundedString(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(2, bg.ROUND_HALF_UP);
    return bg.toString();
  }

  public static String getTruncatedString(double val) {
    BigDecimal bg = new BigDecimal(Double.toString(val));
    bg = bg.setScale(0, bg.ROUND_HALF_DOWN);
    return bg.toString();
  }

  public static String getRoundedDollarCent(double val) {
    int iv = (int) val;
    if (iv * 100 == val * 100) {
      return iv + "";
    }
    else {
      return getRoundedString(val);
    }
  }

  /**public static String getCountry(String ip) throws InetAddressLocatorException {
    Locale l = InetAddressLocator.getLocale(ip);
    return l.getCountry();
  }**/

 /** public static boolean isRestricted(String ip) {
    try {
      String c = Utils.getCountry(ip);
      for (int i = 0; i < restrict_countries.length; i++) {
        if (c.equals(restrict_countries[i])) {
          return true;
        }
      }
    }
    catch (Exception e) {
      //
    }
    return false;
  }**/

  public static String getFormattedNumber(Locale locale, int maxFractionDigits,
                                          String numberIn) throws
      ParseException {
    if (numberIn == null || numberIn.trim().equals("")) {
      return "";
    }
    String numberOut = "";
    NumberFormat numberFormatter = NumberFormat.getInstance();
    numberFormatter.setMaximumFractionDigits(maxFractionDigits);
    numberOut = numberFormatter.format(numberFormatter.parse(numberIn).
                                       doubleValue());
    return numberOut;
  }

  public static String getFormattedNumber(String numberIn) {
    if (numberIn == null || numberIn.trim().equals("")) {
      return "";
    }
    String numberOut = "";
    NumberFormat numberFormatter = NumberFormat.getInstance();
    numberFormatter.setMaximumFractionDigits(2);
    try {
      numberOut = numberFormatter.format(numberFormatter.parse(numberIn).
                                         doubleValue());
    }
    catch (ParseException pe) {
      //pe.printStackTrace();
    }
    return numberOut;
  }

  public static String getFormattedCurrency(String currencyIn) {
    if (currencyIn == null || currencyIn.trim().equals("")) {
      return "";
    }
    String currencyOut = "";
    try {
      Locale locale = Locale.US;
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
      NumberFormat numberFormatter = NumberFormat.getInstance();
      currencyOut = currencyFormatter.format(numberFormatter.parse(currencyIn).
                                             doubleValue());
    }
    catch (ParseException pe) {
      //pe.printStackTrace();
    }
    return currencyOut;
  }
  
  public static String getFormattedDate(Calendar calendar){
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      return simpleDateFormat.format(calendar.getTime());
  }

  public static Date getDateFromString(String str){
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = new Date();
        try {
                d = simpleDateFormat.parse(str);
        }
        catch(java.text.ParseException e) {
                e.printStackTrace();
        }
        return d;
  }

  public static void main(String args[]) throws Exception {
    /**System.out.println(Utils.getRoundedDollarCent(2.00));
    System.out.println(Utils.getRoundedDollarCent(.5));
    System.out.println(Utils.getRoundedDollarCent(1.5));

    byte[] bt = {
        '&', '#', '5', '4', '6', '2', '0', ';'};
    String hangul = new String(new String(
        "\u1112\u1161\u11ab\u1100\u1173\u11af").getBytes(), "EUC-KR");

    String str = new String(bt, "EUC-KR");

    System.out.println(str + hangul);

**/
    double[] parts = FormattingUtils.integralDivide(0.25, 3);

    for (int i = 0; i < parts.length; i++) {
      System.out.println(parts[i]);
    }

    parts = FormattingUtils.integralDivide(0.02, 2);

    for (int i = 0; i < parts.length; i++) {
      System.out.println(parts[i]);
    }

    System.out.println(FormattingUtils.getRoundedString(2.39850934));

    //System.out.println(getCountry("202.153.40.47"));
    String input = "38.5455555";

    Locale loc = Locale.ITALY;
    int digits = 2;
    String formattedNumber = getFormattedNumber(loc, digits, input);
    String formattedCurrency = getFormattedCurrency(input);
    System.out.println(" formatted  currency is: " + formattedCurrency);
    System.out.println(" formatted  number   is: " + formattedNumber);
  }

}
