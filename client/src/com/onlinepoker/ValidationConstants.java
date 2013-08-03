package com.onlinepoker;

/**
 * Created by IntelliJ IDEA.
 * User: yuriy
 * Date: Nov 12, 2003
 * Time: 4:36:01 PM
 * To change this template use Options | File Templates.
 */
public interface ValidationConstants {

    public static final int IS_VALID                                    = 0;

    public static final int SHORT_RAKE_LT_LONG_RAKE                     = 1000;
    public static final int LOW_BET_LT_HIGH_BET                         = 1001;
    public static final int SMALL_BLIND_GT_LOW_BET_DIV_2                = 1002;
    public static final int BIG_BLIND_GT_LOW_BET                        = 1003;
    public static final int MIN_BUYIN_EQ_MAX_BUYIN_FOR_TOURNAMENT       = 1004;
    public static final int MIN_BUYIN_LT_MAX_BUYIN_FOR_NON_TOURNAMENT   = 1005;
    public static final int MIN_BUYIN_NE_0                              = 1006;
    public static final int MIN_BUYIN_GT_BIG_BLIND                      = 1007;

}
