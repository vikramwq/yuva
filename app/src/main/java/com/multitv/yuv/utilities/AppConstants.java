package com.multitv.yuv.utilities;

import android.util.Log;

/**
 * Created by mkr on 10/25/2016.
 */

public class AppConstants {

    public static String PUSH_STATUS_DELIVERED = "delivered";
    public static String PUSH_STATUS_SEEN = "seen";
    public static String PUSH_STATUS_CANCELED = "canceled";
    public static String PUSH_STATUS_CLICK = "click";


    public static String CACHE_TRIVIA_KEY = "Trivia";

   public static int LOGIN_THROUGH_MULTITV = 0;
    public static int LOGIN_THROUGH_FB = 1;
    public static int LOGIN_THROUGH_GOOGLE = 2;



    public static boolean isValidMobile(String phone) {
        Log.e("vaildation mob", "isValidMobile: " + phone);
        String regexStr = "^[0-9]$";

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            return false;
        }
        if (phone.length() <= 9) {
            return false;
        }
        if (phone.length() == 14) {
            return phone.startsWith("0091");
        }
        if (phone.length() == 13) {
            return phone.startsWith("+91");
        }
        if (phone.length() == 11) {
            return phone.startsWith("0");
        }
        if (phone.length() > 14) {
            return false;
        }
        if(phone.startsWith("0")&&phone.length()<11){
            return false;
        }
        return true;
    }
}
