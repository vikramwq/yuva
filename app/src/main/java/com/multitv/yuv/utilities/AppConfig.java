package com.multitv.yuv.utilities;


import com.multitv.yuv.BuildConfig;

/**
 * Created by delhivery on 21/3/16.
 */
public interface AppConfig {
    /**
     * Set this Boolean to show/hide logs
     */
    boolean SHOW_LOG = BuildConfig.IS_SHOW_LOG;

    /**
     * The basic tag for the application log
     */
    String BASE_TAG = "IntexTv";

    /**
     * Duration to find the user current location after this interval
     */
    long LOCATION_SEACRH_DURATION = 5 * 60 * 1000;

    /**
     * Duration to find the user current location after this interval, In case of fast Searching
     */
    long LOCATION_SEACRH_DURATION_FAST = 10 * 1000;


    /**
     * Duration to fetch the location of user other then this user, In case of Active screen
     */
    long LOCATION_FETCH_DURATION = 10 * 1000;
}
