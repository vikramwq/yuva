package com.multitv.yuv.api;


import com.multitv.yuv.utilities.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ApiRequest {
    private static final String TAG = AppConfig.BASE_TAG + ".ApiRequest";

    public static Date curDate = new Date();
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static String DateToStr = format.format(curDate);

    public static String TRIVIA_LIMIT = "5";


    public static String NOTIFICATION_APP_ID = "5825592fd778f00ba72fc4f1";
    public static String TOKEN = "59689749397fb";  //Mi Tivi Token
    public static String TV_GUIDE = DateToStr + "&token=5285sd5sdf&channel=";

    public static String UPDATE_DEVICE = "/user/udatedevice?token=" + TOKEN;
    public static String HOME_URL = "/content/home1/token/" + TOKEN;
    public static String LANGUAGES = "/content/langlist/token/" + TOKEN;
    public static String NEW_SEARCH_URL = "/content/clist/token/" + TOKEN;
    public static String SEARCH_SUGGESTION = "/content/autosuggest/token/" + TOKEN;
    public static String FAVORITE_LIST_URL = "/content/favlist/token/" + TOKEN;
    public static String RECOMMENDED_LIST = "/content/recomended/token/" + TOKEN;


    public static String SOCIAL_LOGIN_URL = "/user/social/token/" + TOKEN;
    public static String VERIFY_OTP = "/user/verify_otp/token/" + TOKEN;
    public static String VIDEO_CAT_URL_CLIST = "/content/clist/token/" + TOKEN;
    public static String FAVORITE_URL = "/content/favorite/token/" + TOKEN;
    public static String WATCHING_SET_DATA_URL = "/content/watchDurationSubs/token/" + TOKEN;
    public static String WATCHING_GET_URL = "/content/watching/token/" + TOKEN;
    public static String VIDEO_CATEGORY = "/content/catList/token/" + TOKEN;
    public static String LIKE_URL_Post = "/content/like/token/" + TOKEN;
    public static String LIKE_URL = "/content/rating_likes/token/" + TOKEN;
    public static String CHECK_VERSION_URL = "/user/version_check/token/" + TOKEN;
    public static String UPDATE_IMEI = "/content/update_uniqueId/token/" + TOKEN;
    public static String FETCHING_CONTENT_DATA = "/content/content_detail/token/" + TOKEN;
    public static String MENU_URL = "/cms/links/token/" + TOKEN;
    public static String VERSION_URL = "/content/version/token/" + TOKEN;
    public static String LIKES_USER_CONTENT = "/content/userrelated_content/token/" + TOKEN;
    public static String USER_EDIT = "/user/edit/token/" + TOKEN;
    public static String UPDATE_PUSH_STATUS = "update_push_status";
    public static String TRIVIA_CONTENT = "/content/trivia/token/" + TOKEN;
    public static String LIVE_CHANNEL = "/content/getLive/token/" + TOKEN;
    public static String GENERATE_OTP = "/user/OTP_generate/token/" + TOKEN;
    public static String GENRE_LIST_URL = "/content/getGenre/token/" + TOKEN;


    /*HeartBeat API*/
    public static String HEARTBEAT1 = "/stream/applicationheartbeat";
    public static String HEARTBEAT2 = "/asid/";
    public static String HEARTBEAT3 = "/sid/";
    public static String HEARTBEAT4 = "/token/" + TOKEN;


    public static String APP_SESSION = "/analytics/usersession/token/" + TOKEN;



    public static final String BASE_URL_VERSION_3="http://api.multitvsolution.com/automatorapi/v3/";


}