package com.multitv.yuv.sharedpreference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.multitv.yuv.activity.HomeActivity;
import com.multitv.yuv.models.User;
import com.multitv.yuv.models.video.Content;
import com.multitv.yuv.utilities.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.multitv.yuv.exatras.SharedPrefManager.PREFS_NAME;


public class SharedPreference {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;


    public final String KEY_NOTIFICATION_SET = "unread_notifications_set";
    public final String KEY_IS_LOGGED_IN = "is_logged_in";
    public final static String KEY_IS_OTP_VERIFIED = "is_otp_verified";
    //public final String KEY_OTP_CODE = "otp_code";

    public SharedPreference() {
        super();
    }

    public Set<String> getUnreadNotificationsList(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<String>(prefs.getStringSet(key, new HashSet<String>()));
        return set;
    }

    public void setUnreadNotificationsList(Context context, String key, Set<String> value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putStringSet(key, value);
        editor.commit();
    }


    public void setPreferencesString(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }






    public String getPreferencesString(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public boolean getPreferenceBoolean(Context _context, String key) {
        if (_context == null)
            return false;
        SharedPreferences prefs = _context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        boolean b = prefs.getBoolean(key, false);
        return b;
    }


    public void setPreferenceBoolean(Context _context, String key, boolean bool) {
        if (_context == null)
            return;
        SharedPreferences.Editor editor = _context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, bool);
        editor.commit();
    }

    public void setPreferenceBoolean(Context _context, String key) {
        if (_context == null)
            return;
        SharedPreferences.Editor editor = _context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, true);
        editor.commit();
    }

    public void setLoginOtpSentStatus(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public void setPassword(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getLoginOtpSentStatus(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setPreferencesLikes(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreferencesLikes(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }


    public void setUserLocation(Context context,String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getUserLocation(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }


    public void setPreferencesInt(Context context, String key, int value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getPreferencesInt(Context context, String key) {
        if (context == null)
            return 0;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        int value = prefs.getInt(key, 0);
        return value;
    }

    public void setPreferencesLong(Context context, String key, long value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getPreferencesLong(Context context, String key) {
        if (context == null)
            return 0;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        long value = prefs.getLong(key, 0);
        return value;
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Content> favorites) {
        if (context == null)
            return;
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(Constant.PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(Constant.FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, Content content) {
        List<Content> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Content>();
        favorites.add(content);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Content content) {
        ArrayList<Content> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(content);
            saveFavorites(context, favorites);
        }
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            // Closing all the Activities
            // Add new Flag to start new Activity
            // Staring Login Activity
            _context.startActivity(new Intent(_context, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constant.IS_LOGIN, false);
    }

    public ArrayList<Content> getFavorites(Context context) {
        if (context == null)
            return null;
        SharedPreferences settings;
        List<Content> favorites;

        settings = context.getSharedPreferences(Constant.PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(Constant.FAVORITES)) {
            String jsonFavorites = settings.getString(Constant.FAVORITES, null);
            Gson gson = new Gson();
            Content[] favoriteItems = gson.fromJson(jsonFavorites,
                    Content[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Content>(favorites);
        } else
            return null;

        return (ArrayList<Content>) favorites;
    }


    public String getprefDatw(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setperfDate(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getEmailID(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setEmailId(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public void setUserPreferences(Context context, User user) {

        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();


        editor.putString("email_id", user.email);
        editor.putString("first_name", user.first_name);
        editor.putString("last_name", user.last_name);
        editor.putString("phone", user.contact_no);
        editor.putString("password", user.app_session_id);
        editor.putString("imgUrl", user.image);

        if (!TextUtils.isEmpty(user.dob) && !user.dob.equals("0000-00-00"))
            editor.putString("dob", user.dob);


        if (!TextUtils.isEmpty(user.gender))
            if (user.gender.equalsIgnoreCase("male"))
                editor.putString("gender_id", "" + 0);
            else if (user.gender.equalsIgnoreCase("female"))
                editor.putString("gender_id", "" + 1);


        editor.commit();

    }


    public String getImageUrl(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setImageUrl(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getUSerName(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setUserName(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getUSerLastName(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setUserLastName(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPhoneNumber(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setPhoneNumber(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getDob(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setDob(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getGender(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setGender(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getGenderId(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setGenderId(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getStatusLogin(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setStatusLogin(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getFromLogedIn(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setFromLogedIn(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getGoogleLoginEmail(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setGoogleLoginEmail(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getGoogleLoginUSername(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setGoogleLoginUsername(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getGoogleLoginLastName(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setGoogleLoginLastName(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getGoogleLoginProfilePic(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    public void setGoogleLoginProfilePic(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public void setUserIfLoginVeqta(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getUserIfLoginVeqta(Context context, String key) {
        if (context == null)
            return null;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }


}
