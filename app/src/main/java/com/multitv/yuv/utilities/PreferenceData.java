package com.multitv.yuv.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceData {

    private static final String TAG = AppConfig.BASE_TAG + ".PreferenceData";
    private static final String STORE = "STORE";
    private static final String IS_HOME_FLOAT_DEMO_DONE = "IS_HOME_FLOAT_DEMO_DONE";
    private static final String IS_TC_DONE = "IS_TC_DONE";
    private static final String IS_UPDATE_TOKEN = "IS_UPDATE_TOKEN";
    private static final String LANGUAGE_LIST = "LANGUAGE_LIST";
    private static final String FCM_TOKEN = "FCM_TOKEN";
    private static final String APP_SESSION_ID = "APP_SESSION_ID";
    private static final String APP_LANGUAGE = "APP_LANGUAGE";
    private static final String IS_FCM_TOKEN_REGISTER_ON_SERVER = "IS_FCM_TOKEN_REGISTER_ON_SERVER";
    private static final String FLOAT_X = "FLOAT_X";
    private static final String FLOAT_Y = "FLOAT_Y";
    private static final String NOTIFICATION_COUNTER = "NOTIFICATION_COUNTER";
    private static final String APP_STATE = "APP_STATE";
    private static final String IS_NOTIFICATION_ENABLE = "IS_NOTIFICATION_ENABLE";
    private static final String IS_MATURE_FILTER_ENABLE = "IS_MATURE_FILTER_ENABLE";
    private static final String PREVIOUSLY_SELECTED_FRAGMENT_POSITION = "PREVIOUSLY_SELECTED_FRAGMENT_POSITION";
    public static final String BASE_URL = "BASE_URL";
    private static final String EPG_URL = "EPG_URL";
    private static final String DRIVE_URL = "DRIVE_URL";
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================

    /**
     * Method to check weather the Float button demo is shown to user
     *
     * @param context
     * @return
     */
    public static boolean isHomeFloatDemoDone(Context context) {
        Tracer.debug(TAG, "isHomeFloatDemoDone: ");
        return getSharedPreference(context).getBoolean(IS_HOME_FLOAT_DEMO_DONE, false);
    }

    /**
     * Method to notify that Float button demo is shown to user
     *
     * @param context
     * @return
     */
    public static void setHomeFloatDemoDone(Context context) {
        Tracer.debug(TAG, "setHomeFloatDemoDone: ");
        getShearedPreferenceEditor(context).putBoolean(IS_HOME_FLOAT_DEMO_DONE, true).commit();
    }

    /**
     * Method to check weather the App is active or not
     *
     * @param context
     * @return
     */
    public static boolean isAppActive(Context context) {
        Tracer.error(TAG, "isAppActive: ");
        return getSharedPreference(context).getBoolean(APP_STATE, true);
    }

    /**
     * Method to notify that user set that App is active
     *
     * @param context
     * @return
     */
    public static void setAppActive(Context context) {
        Tracer.error(TAG, "setAppActive: ");
        getShearedPreferenceEditor(context).putBoolean(APP_STATE, true).commit();
    }


    /**
     * Method to notify that user set that App is inactive
     *
     * @param context
     * @return
     */
    public static void setAppInactive(Context context) {
        Tracer.error(TAG, "setAppInactive: ");
        getShearedPreferenceEditor(context).putBoolean(APP_STATE, false).commit();
    }

    /**
     * Method to check weather the TC is shown ornot
     *
     * @param context
     * @return
     */
    public static boolean isTCShown(Context context) {
        Tracer.debug(TAG, "isTCShown: ");
        return getSharedPreference(context).getBoolean(IS_TC_DONE, false);
    }

    /**
     * Method to notify that TC Shown to user
     *
     * @param context
     * @return
     */
    public static void setTCShown(Context context) {
        Tracer.debug(TAG, "setTCShown: ");
        getShearedPreferenceEditor(context).putBoolean(IS_TC_DONE, true).commit();
    }

    /**
     * Method to get the supported language List
     *
     * @param context
     * @return
     */
    public static String getLangaugeListJsonArray(Context context) {
        Tracer.debug(TAG, "getLangaugeListJsonArray: ");
        return getSharedPreference(context).getString(LANGUAGE_LIST, "[]");
    }

    /**
     * Method to set the supported language List
     *
     * @param context
     * @param languageList
     * @return
     */
    public static void setLangaugeListJsonArray(Context context, String languageList) {
        Tracer.debug(TAG, "setLangaugeListJsonArray: ");
        getShearedPreferenceEditor(context).putString(LANGUAGE_LIST, languageList).commit();
    }

    /**
     * Method to get the Float Button X
     *
     * @param context
     * @return
     */
    public static int getFloatX(Context context) {
        Tracer.debug(TAG, "getFloatX: ");
        return getSharedPreference(context).getInt(FLOAT_X, -1);
    }

    /**
     * Method to set the Float Button X
     *
     * @param context
     * @param floatX
     * @return
     */
    public static void setFloatX(Context context, int floatX) {
        Tracer.debug(TAG, "setFloatX: ");
        getShearedPreferenceEditor(context).putInt(FLOAT_X, floatX).commit();
    }


    /**
     * Method to get the Float Button Y
     *
     * @param context
     * @return
     */
    public static int getFloatY(Context context) {
        Tracer.debug(TAG, "getFloatY: ");
        return getSharedPreference(context).getInt(FLOAT_Y, -1);
    }

    /**
     * Method to set the Float Button Y
     *
     * @param context
     * @param floatY
     * @return
     */
    public static void setFloatY(Context context, int floatY) {
        Tracer.debug(TAG, "setFloatY: ");
        getShearedPreferenceEditor(context).putInt(FLOAT_Y, floatY).commit();
    }

    /**
     * Method to get the FCM TOKEN
     *
     * @param context
     * @return
     */
    public static String getFCMToken(Context context) {
        Tracer.debug(TAG, "getFCMToken: ");
        return getSharedPreference(context).getString(FCM_TOKEN, "");
    }

    /**
     * Method to set the New Fcm Token
     *
     * @param context
     * @param fcmToken
     * @return
     */
    public static void setFCMToken(Context context, String fcmToken) {
        Tracer.debug(TAG, "setFCMToken: ");
        getShearedPreferenceEditor(context).putString(FCM_TOKEN, fcmToken).commit();
        setNewFCMTokenGenerated(context);
    }

    /**
     * Method to check weather the FCM Token is regictered on the server or not
     *
     * @param context
     * @return
     */
    public static boolean isFCMTokenRegisteredOnServer(Context context) {
        Tracer.debug(TAG, "isFCMTokenRegisteredOnServer: ");
        return getSharedPreference(context).getBoolean(IS_FCM_TOKEN_REGISTER_ON_SERVER, false);
    }

    /**
     * Method to notify that the FCM Token is registered on the server
     *
     * @param context
     * @return
     */
    public static void setFCMTokenRegisteredOnServer(Context context) {
        Tracer.debug(TAG, "setFCMTokenRegisteredOnServer: ");
        getShearedPreferenceEditor(context).putBoolean(IS_FCM_TOKEN_REGISTER_ON_SERVER, true).commit();
    }

    /**
     * Method to notify that the New FCM Token is Generated
     *
     * @param context
     * @return
     */
    public static void setNewFCMTokenGenerated(Context context) {
        Tracer.debug(TAG, "setNewFCMTokenGenerated: ");
        getShearedPreferenceEditor(context).putBoolean(IS_FCM_TOKEN_REGISTER_ON_SERVER, false).commit();
    }

    /**
     * method to check is the update token query is run or not
     *
     * @param context
     * @return
     */
    public static boolean isAlreadyUpdateTokenDone(Context context) {
        return getSharedPreference(context).getBoolean(IS_UPDATE_TOKEN, false);
    }

    /**
     * Method to set the Update token query is run already
     *
     * @param context
     * @return
     */
    public static void setAlreadyUpdateTokenDone(Context context) {
        getShearedPreferenceEditor(context).putBoolean(IS_UPDATE_TOKEN, true).commit();
    }

    /**
     * Method to get the AppSessionId
     *
     * @param context
     * @return
     */
    public static String getAppSessionId(Context context) {
        Tracer.debug(TAG, "getAppSessionId: ");
        return getSharedPreference(context).getString(APP_SESSION_ID, "");
    }

    /**
     * Method to set the New AppSessionId
     *
     * @param context
     * @param appSessionId
     * @return
     */
    public static void setAppSessionId(Context context, String appSessionId) {
        Tracer.debug(TAG, "setAppSessionId: ");
        getShearedPreferenceEditor(context).putString(APP_SESSION_ID, appSessionId).commit();
        setNewFCMTokenGenerated(context);
    }

    /**
     * Method to get the Unique Notification Id
     *
     * @param context
     * @return
     */
    public static int getNotificationId(Context context) {
        Tracer.debug(TAG, "getNotificationId: ");
        incrementNotificationId(context);
        return getSharedPreference(context).getInt(NOTIFICATION_COUNTER, 0);
    }

    /**
     * Method to inc the Notification Id
     *
     * @param context
     * @return
     */
    public static void incrementNotificationId(Context context) {
        Tracer.debug(TAG, "incrementNotificationId: ");
        int notificationId = getSharedPreference(context).getInt(NOTIFICATION_COUNTER, 0);
        getShearedPreferenceEditor(context).putInt(NOTIFICATION_COUNTER, notificationId + 1).commit();
    }

    /**
     * Method to check weather the Notification is enable or not
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnable(Context context) {
        Tracer.debug(TAG, "isNotificationEnable: ");
        return getSharedPreference(context).getBoolean(IS_NOTIFICATION_ENABLE, true);
    }

    /**
     * Method to notify that user toggle the Notification
     *
     * @param context
     * @return
     */
    public static void toggleNotification(Context context) {
        Tracer.debug(TAG, "toggleNotification: ");
        if (isNotificationEnable(context)) {
            disableNotifiation(context);
        } else {
            enableNotifiation(context);
        }
    }

    /**
     * Method to notify that user disable the notification
     *
     * @param context
     * @return
     */
    private static void disableNotifiation(Context context) {
        Tracer.debug(TAG, "disableNotifiation: ");
        getShearedPreferenceEditor(context).putBoolean(IS_NOTIFICATION_ENABLE, false).commit();
    }

    /**
     * Method to notify that user enable the notification
     *
     * @param context
     * @return
     */
    private static void enableNotifiation(Context context) {
        Tracer.debug(TAG, "enableNotifiation: ");
        getShearedPreferenceEditor(context).putBoolean(IS_NOTIFICATION_ENABLE, true).commit();
    }

    /**
     * Method to check weather the MatureFilter is enable or not
     *
     * @param context
     * @return
     */
    public static boolean isMatureFilterEnable(Context context) {
        Tracer.debug(TAG, "isMatureFilterEnable: ");
        return getSharedPreference(context).getBoolean(IS_MATURE_FILTER_ENABLE, true);
    }

    /**
     * Method to notify that user disable the MatureFilter
     *
     * @param context
     * @return
     */
    public static void disableMatureFilter(Context context) {
        Tracer.debug(TAG, "disableMatureFilter: ");
        getShearedPreferenceEditor(context).putBoolean(IS_MATURE_FILTER_ENABLE, false).commit();
    }

    /**
     * Method to notify that user enable the MatureFilter
     *
     * @param context
     * @return
     */
    public static void enableMatureFilter(Context context) {
        Tracer.debug(TAG, "enableMatureFilter: ");
        getShearedPreferenceEditor(context).putBoolean(IS_MATURE_FILTER_ENABLE, true).commit();
    }

    /**
     * Method to get the App Language
     *
     * @param context
     * @return
     */
    public static String getAppLanguage(Context context) {
        Tracer.debug(TAG, "getAppLanguage: ");
        return getSharedPreference(context).getString(APP_LANGUAGE, "");
    }

    /**
     * Method to set the New App Language
     *
     * @param context
     * @param newAppLanguage
     * @return
     */
    public static void setAppLanguage(Context context, String newAppLanguage) {
        Tracer.debug(TAG, "setAppLanguage: ");
        getShearedPreferenceEditor(context).putString(APP_LANGUAGE, newAppLanguage).commit();
    }


    /**
     * Method to get position of previous fragment
     *
     * @param context
     * @return
     */
    public static int getPreviousFragmentSelectedPosition(Context context) {
        Tracer.debug(TAG, "getPreviousFragmentSelectedPosition: ");
        return getSharedPreference(context).getInt(PREVIOUSLY_SELECTED_FRAGMENT_POSITION, 0);
    }

    /**
     * Method to set position of previous fragment
     *
     * @param context
     * @param previousPosition
     * @return
     */
    public static void setPreviousFragmentSelectedPosition(Context context, int previousPosition) {
        Tracer.debug(TAG, "setPreviousFragmentSelectedPosition: ");
        getShearedPreferenceEditor(context).putInt(PREVIOUSLY_SELECTED_FRAGMENT_POSITION, previousPosition).commit();
    }

    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================

    /**
     * Method to clear the Data Store
     *
     * @param context
     */
    public static void clearStore(Context context) {
        Tracer.debug(TAG, "clearStore()");
        getShearedPreferenceEditor(context).clear().commit();
    }

    /**
     * Method to fetch base url from shared preference
     *
     * @param context
     * @return
     */
    public static String getBaseUrl(Context context) {
        Tracer.debug(TAG, "getBaseUrl: ");
        return getSharedPreference(context).getString(BASE_URL, "");
    }

    /**
     * Method to fetch EPG url from shared preference
     *
     * @param context
     * @return
     */
    public static String getEPGUrl(Context context) {
        Tracer.debug(TAG, "getEPGUrl: ");
        return getSharedPreference(context).getString(EPG_URL, "");
    }

    /**
     * Method to fetch DRIVE url from shared preference
     *
     * @param context
     * @return
     */
    public static String getDriveUrl(Context context) {
        Tracer.debug(TAG, "getDriveUrl: ");
        return getSharedPreference(context).getString(DRIVE_URL, "");
    }

    /**
     * Method to notify that the FCM Token is registered on the server
     *
     * @param context
     * @return
     */
    public static void setBaseAndEPGUrlAndDriveUrl(Context context, String baseUrl, String epgUrl, String driveUrl) {
        Tracer.debug(TAG, "setBaseAndEPGUrl: ");
        getShearedPreferenceEditor(context).putString(BASE_URL, baseUrl).commit();
        getShearedPreferenceEditor(context).putString(EPG_URL, epgUrl).commit();
        getShearedPreferenceEditor(context).putString(DRIVE_URL, driveUrl).commit();
    }


    /**
     * Method to return the Data Store Prefference
     *
     * @param context
     * @return
     */
    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(STORE, Context.MODE_PRIVATE);
    }

    /**
     * caller to commit this editor
     *
     * @param context
     * @return Editor
     */
    private static Editor getShearedPreferenceEditor(Context context) {
        return getSharedPreference(context).edit();
    }
}
