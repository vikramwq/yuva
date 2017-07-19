package com.multitv.yuv.utilities;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.activity.HomeActivity;
import com.multitv.yuv.activity.LoginScreen;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.sharedpreference.SharedPreference;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by naseeb on 4/27/2017.
 */

public class AppSessionUtil1 {

    public static void sendHeartBeat(final Activity activity) {
        Runnable sessionRunnable = new Runnable() {
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("App heart beat", "App session id : " + AppController.getInstance().getAppSessionId());
                        Log.e("App heart beat", "Content session id : " + AppController.getInstance().getContentSessionId());
                        Log.e("App heart beat", "is_app_in_background : " + CheckApplicationIsVisible.isAppIsInBackground(activity));

                        if (CheckApplicationIsVisible.isAppIsInBackground(activity) ||
                                (TextUtils.isEmpty(AppController.getInstance().getAppSessionId())
                                        && TextUtils.isEmpty(AppController.getInstance().getContentSessionId()))) {
                            return;
                        }
                        String url = AppUtils.generateUrl(getApplicationContext(), ApiRequest.HEARTBEAT1) +
                                (!TextUtils.isEmpty(AppController.getInstance().getAppSessionId())
                                        && !AppController.getInstance().getAppSessionId().equalsIgnoreCase(null)
                                        ? ApiRequest.HEARTBEAT2 + AppController.getInstance().getAppSessionId() : "") +
                                (!TextUtils.isEmpty(AppController.getInstance().getContentSessionId())
                                        ? ApiRequest.HEARTBEAT3 + AppController.getInstance().getContentSessionId() : "")
                                + ApiRequest.HEARTBEAT4;
                        Log.e("App heart beat", "App heart beat url : " + url);

                        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                                url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("App heart beat api---", response);
                                try {
                                    JSONObject mObj = new JSONObject(response);
                                    if (mObj.optInt("code") == 1) {
                                        MultitvCipher mcipher = new MultitvCipher();
                                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                        Log.e("App heart beat api ---", str);

                                        JSONObject jsonObject = new JSONObject(str.trim());

                                        String appSessionId = jsonObject.optString("asid");
                                        if (!TextUtils.isEmpty(appSessionId) && !appSessionId.equalsIgnoreCase("false")
                                                && !appSessionId.equalsIgnoreCase("0")
                                                && !appSessionId.equalsIgnoreCase("null")
                                                && !appSessionId.equalsIgnoreCase(null)) {
                                            AppController.getInstance().setAppSessionId(appSessionId);
                                        } else
                                            AppController.getInstance().setAppSessionId("");


                                        String contentSessionId = jsonObject.optString("sid");
                                        if (!TextUtils.isEmpty(contentSessionId) && !contentSessionId.equalsIgnoreCase("false")
                                                && !contentSessionId.equalsIgnoreCase("0")
                                                && !contentSessionId.equalsIgnoreCase("null")
                                                && !contentSessionId.equalsIgnoreCase(null)) {
                                            AppController.getInstance().setContentSessionId(contentSessionId);
                                        } else
                                            AppController.getInstance().setContentSessionId("");

                                        boolean isLoggedIn = new SharedPreference().getPreferenceBoolean(activity, SharedPreference.KEY_IS_OTP_VERIFIED);
                                        if (isLoggedIn) {
                                            String activeSession = jsonObject.optString("is_active_session");
                                            if (!TextUtils.isEmpty(activeSession) && activeSession.equalsIgnoreCase("0")) {
//                                                logoutFromApp();
                                            }
                                        }
                                    } else if (mObj.optInt("code") == 0) {
                                        Log.e("App heart beat api ---", response);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Log.e("App heart beat", "Error: " + error.getMessage());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();

                                params.put("device", "android");
                                return params;
                            }
                        };

                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jsonObjReq);
                    }
                }).start();

                AppController.getInstance().getSessionHandler().postDelayed(this, 1000 * 60);
            }
        };

        //Stopping previous handler
        AppController.getInstance().getSessionHandler().removeCallbacks(sessionRunnable);
        AppController.getInstance().getSessionHandler().removeCallbacksAndMessages(null);
        //Starting handler again
        AppController.getInstance().getSessionHandler().postDelayed(sessionRunnable, 0);
    }


    public static void logoutFromApp() {

        MediaDbConnector mediaDbConnector = new MediaDbConnector(AppController.applicationContext);
        SharedPreference sharedPreference = new SharedPreference();

        sharedPreference.setFromLogedIn(AppController.applicationContext, "fromLogedin", "");
        sharedPreference.setGender(AppController.applicationContext, "gender_id", "");
        sharedPreference.setEmailId(AppController.applicationContext, "email_id", "");
        sharedPreference.setUserName(AppController.applicationContext, "first_name", "");
        sharedPreference.setUserLastName(AppController.applicationContext, "last_name", "");
        sharedPreference.setPhoneNumber(AppController.applicationContext, "phone", "");
        sharedPreference.setPassword(AppController.applicationContext, "password", "");
        sharedPreference.setImageUrl(AppController.applicationContext, "imgUrl", "");
        sharedPreference.setDob(AppController.applicationContext, "dob", "");
        sharedPreference.setPreferencesString(AppController.applicationContext, "user_id" + "_" + ApiRequest.TOKEN, "");

        String LoginFrom = sharedPreference.getFromLogedIn(AppController.applicationContext, "fromLogedin");

        if (LoginFrom.equals("google")) {
            sharedPreference.setFromLogedIn(AppController.applicationContext, "fromLogedin", "");
        } else if (LoginFrom.equals("facebook")) {
            LoginManager.getInstance().logOut();
        }
        mediaDbConnector.dropAllTables();
        sharedPreference.setPreferenceBoolean(AppController.applicationContext, sharedPreference.KEY_IS_OTP_VERIFIED, false);
        sharedPreference.setPreferenceBoolean(AppController.applicationContext, sharedPreference.KEY_IS_LOGGED_IN, false);
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(getApplicationContext(), LoginScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("logout", "NoStatus");
        HomeActivity.getInstance().startActivity(intent);
        if (HomeActivity.getInstance() != null) {
            ((HomeActivity) HomeActivity.getInstance())
                    .closeActivity();
        }

    }
}
