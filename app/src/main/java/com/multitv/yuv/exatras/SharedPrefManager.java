package com.multitv.yuv.exatras;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.multitv.yuv.models.home.Home;
import com.google.gson.Gson;

public class SharedPrefManager {

    public static final String PREFS_NAME = "Syllo_pref";
    SharedPreferences sharedPref;
    Editor editor;


    public SharedPrefManager() {
        super();
    }

    public void setName(Context context, String limit) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("name", limit);
        editor.apply();
    }

    public String getName(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("name", null);
    }

    public void setAddress(Context context, String limit) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("address", limit);
        editor.commit();
    }


    public String getAddress(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("address", null);
    }

    public void setRating(Context context, String limit) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("rating", limit);
        editor.commit();
    }


    public String getRating(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("rating", null);
    }

    public void setResId(Context context, String id) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("res_id", id);
        editor.apply();
    }

    public String getResId(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("res_id", "");
    }

    public void setRoleId(Context context, String id) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("role_id", id);
        editor.apply();
    }

    public String getRoleId(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("role_id", "");
    }

    public void setLogin(Context context, boolean isLogin) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("is_login", isLogin);
        editor.apply();
    }

    public boolean getLogin(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean("is_login", false);
    }

    public void setUserId(Context context, String id) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("user_id", id);
        editor.apply();
    }

    public String getUserId(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("user_id", "");
    }



    //set live channal data
    public void setLiveChannal(Context context, Home home) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(home.live);
        editor.putString("livechannal", json);
        editor.commit();
    }

    public String getLiveChannal(Context context) {

        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPref.getString("livechannal", "");
      //  Home obj = gson.fromJson(json, Home.class);
        return json;
    }


    public void setDate(Context context, String date) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("date", date);
        editor.apply();
    }

    public String getDate(Context context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString("date", null);
    }
}