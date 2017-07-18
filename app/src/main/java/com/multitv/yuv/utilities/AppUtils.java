package com.multitv.yuv.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.multitv.yuv.application.AppController;

/**
 * Created by naseeb on 10/14/2016.
 */

public class AppUtils {
    private static final String TAG = AppConfig.BASE_TAG + ".AppUtils";
    private static Bitmap mUserBitmap;

    /**
     * Method to generate the URL by appending the base URL
     *
     * @param context
     * @param apiUrl
     * @return
     */
    public static String generateUrl(Context context, String apiUrl) {
        Tracer.error(TAG, "generateUrl: " + apiUrl);
        return PreferenceData.getBaseUrl(AppController.getInstance()) + apiUrl;
    }


    /**
     * Method to generate the URL by appending the EPG URL
     *
     * @param context
     * @param apiUrl
     * @return
     */
    public static String generateEpgUrl(Context context, String apiUrl) {
        Tracer.error(TAG, "generateEpgUrl: " + apiUrl);
        return PreferenceData.getEPGUrl(AppController.getInstance()) + apiUrl;
    }

    /**
     * Method to generate the URL by appending the DRIVE URL
     *
     * @param context
     * @param apiUrl
     * @return
     */
    public static String generateDriveUrl(Context context, String apiUrl) {
        Tracer.error(TAG, "generateEpgUrl: " + apiUrl);
        return PreferenceData.getDriveUrl(AppController.getInstance()) + apiUrl;
    }

    /**
     * Method to check weather the app is running on front
     *
     * @param context
     * @return TRUE if on front, else FALSE
     */
    public static boolean isAppOnFront(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(1);
        if (recentTasks != null && recentTasks.size() > 0) {
            String pkg = recentTasks.get(0).baseActivity.getPackageName();
            if (pkg != null && pkg.contains(context.getPackageName())) {
                Tracer.error(TAG, "AppUtils.isAppOnFront() APP ON FRONT TRUE");
                return true;
            }
        }
        Tracer.error(TAG, "AppUtils.isAppOnFront() APP ON FRONT FALSE");
        return false;
    }

    public static String[] getVersionNameAndCode(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            ExceptionUtils.printStacktrace(e);
        }

        String[] versionNameAndCodeArray = {pInfo.versionName, "" + pInfo.versionCode};
        return versionNameAndCodeArray;
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static Bitmap getUserImage(Context context) {
        if (mUserBitmap == null || mUserBitmap.isRecycled()) {
            createUserBitmap(context);
        }
        return mUserBitmap;
    }

    public static void createUserBitmap(Context context) {
        // CREATE USER BITMAP
    }

    public static String getFormattedDate(String dateToFormat) {

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("MMM dd hh:mm a");
        try {
            Date date = inputFormat.parse(dateToFormat);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Method to return the code of the language
     *
     * @param languageName Name of the language
     * @return Code of the language if find in the list else return the code of English language
     */
    public static String getLanguageCode(String languageName) {
        if (languageName == null) {
            return "en";
        }
        if (languageName.trim().equalsIgnoreCase("Urdu")) {
            return "ur";
        } else if (languageName.trim().equalsIgnoreCase("Telugu")) {
            return "te";
        } else if (languageName.trim().equalsIgnoreCase("Tamil")) {
            return "ta";
        } else if (languageName.trim().equalsIgnoreCase("Sindhi")) {
            return "sd";
        } else if (languageName.trim().equalsIgnoreCase("Sanskrit")) {
            return "sa";
        } else if (languageName.trim().equalsIgnoreCase("Russian")) {
            return "ru";
        } else if (languageName.trim().equalsIgnoreCase("Panjabi")) {
            return "pa";
        } else if (languageName.trim().equalsIgnoreCase("Marathi")) {
            return "mr";
        } else if (languageName.trim().equalsIgnoreCase("Malayalam")) {
            return "ml";
        } else if (languageName.trim().equalsIgnoreCase("Kannada")) {
            return "kn";
        } else if (languageName.trim().equalsIgnoreCase("Hindi")) {
            return "hi";
        } else if (languageName.trim().equalsIgnoreCase("Gujarati")) {
            return "gu";
        } else if (languageName.trim().equalsIgnoreCase("English")) {
            return "en";
        } else if (languageName.trim().equalsIgnoreCase("Bengali")) {
            return "bn";
        }
        return "en";
    }

    /**
     * Method to return the code of the language
     *
     * @param languageName Name of the language
     * @return Code of the language if find in the list else return the code of English language
     */
    public static String getLanguageNameOrignal(String languageName) {
        if (languageName == null) {
            return "en";
        }
        if (languageName.trim().equalsIgnoreCase("Urdu")) {
            return "اردو";
        } else if (languageName.trim().equalsIgnoreCase("Telugu")) {
            return "తెలుగు";
        } else if (languageName.trim().equalsIgnoreCase("Tamil")) {
            return "தமிழ்";
        } else if (languageName.trim().equalsIgnoreCase("Sindhi")) {
            return "سنڌي";
        } else if (languageName.trim().equalsIgnoreCase("Sanskrit")) {
            return "sa";
        } else if (languageName.trim().equalsIgnoreCase("Russian")) {
            return "русский";
        } else if (languageName.trim().equalsIgnoreCase("Panjabi")) {
            return "ਪੰਜਾਬੀ";
        } else if (languageName.trim().equalsIgnoreCase("Marathi")) {
            return "मराठी";
        } else if (languageName.trim().equalsIgnoreCase("Malayalam")) {
            return "മലയാളം";
        } else if (languageName.trim().equalsIgnoreCase("Kannada")) {
            return "ಕನ್ನಡ";
        } else if (languageName.trim().equalsIgnoreCase("Hindi")) {
            return "हिंदी";
        } else if (languageName.trim().equalsIgnoreCase("Gujarati")) {
            return "ગુજરાતી";
        } else if (languageName.trim().equalsIgnoreCase("English")) {
            return "English";
        } else if (languageName.trim().equalsIgnoreCase("Bengali")) {
            return "বাঙালি";
        }
        return languageName;
    }

    /**
     * Method to return the code of the language
     *
     * @param languageName Name of the language
     * @return Code of the language if find in the list else return the code of English language
     */
    public static String getLanguageCodeByOriginalName(String languageName) {
        if (languageName == null) {
            return "en";
        }
        if (languageName.trim().equalsIgnoreCase("اردو")) {
            return "ur";
        } else if (languageName.trim().equalsIgnoreCase("తెలుగు")) {
            return "te";
        } else if (languageName.trim().equalsIgnoreCase("தமிழ்")) {
            return "ta";
        } else if (languageName.trim().equalsIgnoreCase("سنڌي")) {
            return "sd";
        } else if (languageName.trim().equalsIgnoreCase("Sanskrit")) {
            return "sa";
        } else if (languageName.trim().equalsIgnoreCase("русский")) {
            return "ru";
        } else if (languageName.trim().equalsIgnoreCase("ਪੰਜਾਬੀ")) {
            return "pa";
        } else if (languageName.trim().equalsIgnoreCase("मराठी")) {
            return "mr";
        } else if (languageName.trim().equalsIgnoreCase("മലയാളം")) {
            return "ml";
        } else if (languageName.trim().equalsIgnoreCase("ಕನ್ನಡ")) {
            return "kn";
        } else if (languageName.trim().equalsIgnoreCase("हिंदी")) {
            return "hi";
        } else if (languageName.trim().equalsIgnoreCase("ગુજરાતી")) {
            return "gu";
        } else if (languageName.trim().equalsIgnoreCase("English")) {
            return "en";
        } else if (languageName.trim().equalsIgnoreCase("বাঙালি")) {
            return "bn";
        }
        return "en";
    }

    /**
     * Method to retur the code of the language
     *
     * @param languageCode Name of the language
     * @return Code of the language if find in the list else return the code of English language
     */
    public static String getLanguageName(String languageCode) {
        if (languageCode == null) {
            return "english";
        }
        if (languageCode.trim().equalsIgnoreCase("ur")) {
            return "Urdu";
        } else if (languageCode.trim().equalsIgnoreCase("te")) {
            return "Telugu";
        } else if (languageCode.trim().equalsIgnoreCase("ta")) {
            return "Tamil";
        } else if (languageCode.trim().equalsIgnoreCase("sd")) {
            return "Sindhi";
        } else if (languageCode.trim().equalsIgnoreCase("sa")) {
            return "Sanskrit";
        } else if (languageCode.trim().equalsIgnoreCase("ru")) {
            return "Russian";
        } else if (languageCode.trim().equalsIgnoreCase("pa")) {
            return "Punjabi";
        } else if (languageCode.trim().equalsIgnoreCase("mr")) {
            return "Marathi";
        } else if (languageCode.trim().equalsIgnoreCase("ml")) {
            return "Malayalam";
        } else if (languageCode.trim().equalsIgnoreCase("kn")) {
            return "Kannada";
        } else if (languageCode.trim().equalsIgnoreCase("hi")) {
            return "Hindi";
        } else if (languageCode.trim().equalsIgnoreCase("gu")) {
            return "Gujarati";
        } else if (languageCode.trim().equalsIgnoreCase("en")) {
            return "English";
        } else if (languageCode.trim().equalsIgnoreCase("bn")) {
            return "Bengali";
        }
        return "English";
    }


    //=====================mobile number vaildation ======================================================================
    // ====================================================================================================

    public static boolean isValidMobile(String phone) {
        Tracer.error("vaildation mob", "isValidMobile: " + phone);
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
        return true;
    }
}