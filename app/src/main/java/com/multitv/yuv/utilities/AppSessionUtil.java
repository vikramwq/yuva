//package com.wamindia.dollywoodplay.utilities;
//
//
//import android.content.Intent;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.facebook.login.LoginManager;
//import com.multitv.cipher.MultitvCipher;
//
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.wamindia.dollywoodplay.activity.HomeActivity;
//import com.wamindia.dollywoodplay.activity.SignupScreen;
//import com.wamindia.dollywoodplay.api.ApiRequest;
//import com.wamindia.dollywoodplay.application.AppController;
//import com.wamindia.dollywoodplay.db.MediaDbConnector;
//import com.wamindia.dollywoodplay.sharedpreference.SharedPreference;
//
//import static com.facebook.FacebookSdk.getApplicationContext;
//
///**
// * Created by naseeb on 4/18/2017.
// */
//
//public class AppSessionUtil {
//
//    public static void sendHeartBeat() {
//
//        Runnable sessionRunnable = new Runnable() {
//            public void run() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String url = AppUtils.generateUrl(getApplicationContext(), ApiRequest.HEARTBEAT1 + AppController.getInstance().getAppSessionId() + (!TextUtils.isEmpty(AppController.getInstance().getContentSessionId())
//                                                ? ApiRequest.HEARTBEAT2 + AppController.getInstance().getContentSessionId() : "")
//                                        + ApiRequest.HEARTBEAT3);
//
//                        Log.d(this.getClass().getName(), "App heart beat url ==>>" + url);
//                        Tracer.error("API_REQUEST", "App heart beat url : " + url);
//
//                        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
//                                url, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Log.d(this.getClass().getName(), "App heart beat api--" + response);
//                                Tracer.error("App heart beat api---", response);
//                                try {
//                                    JSONObject mObj = new JSONObject(response);
//                                    if (mObj.optInt("code") == 1) {
//                                        MultitvCipher mcipher = new MultitvCipher();
//                                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//                                        Log.d(this.getClass().getName(), "response from heaert beat==" + str);
//                                        Tracer.error("App heart beat api ---", str);
//
//                                        JSONObject jsonObject = new JSONObject(str.trim());
//
//                                        String is_active_session = jsonObject.optString("is_active_session");
//                                        if (is_active_session.equals("1")) {
//
//                                        } else {
//
////                                            Utilities.runOnUIThread(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    Utilities.showLoginDailog(AppController.applicationContext);
////                                                }
////                                            });
//
//                                            logoutFromApp();
//                                        }
//
//                                        String appSessionId = jsonObject.optString("asid");
//                                        if (!TextUtils.isEmpty(appSessionId))
//                                            AppController.getInstance().setAppSessionId(appSessionId);
//
//                                        String contentSessionId = jsonObject.optString("sid");
//                                        if (!TextUtils.isEmpty(contentSessionId) && !contentSessionId.equalsIgnoreCase("false"))
//                                            AppController.getInstance().setContentSessionId(contentSessionId);
//                                    } else if (mObj.optInt("code") == 0) {
//                                        Tracer.error("App heart beat api ---", response);
//
//                                    }
//                                } catch (Exception e) {
//                                    ExceptionUtils.printStacktrace(e);
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Tracer.error("AppSessionUtil", "Error: " + error.getMessage());
//                            }
//                        }) {
//                            @Override
//                            protected Map<String, String> getParams() {
//                                Map<String, String> params = new HashMap<>();
//
//                                params.put("device", "android");
//                                return params;
//                            }
//                        };
//
//                        // Adding request to request queue
//                        AppController.getInstance().addToRequestQueue(jsonObjReq);
//                    }
//                }).start();
//
//                AppController.getInstance().getSessionHandler().postDelayed(this, !TextUtils.isEmpty(AppController.getInstance().getContentSessionId()) ?
//                        1000 * 60 : 1000 * 60 * 2);
//            }
//        };
//
//        //Stopping previous handler
//        AppController.getInstance().getSessionHandler().removeCallbacks(sessionRunnable);
//        AppController.getInstance().getSessionHandler().removeCallbacksAndMessages(null);
//        //Starting handler again
//        AppController.getInstance().getSessionHandler().postDelayed(sessionRunnable, 0);
//    }
//
//
//    public static void logoutFromApp() {
//
//        MediaDbConnector mediaDbConnector = new MediaDbConnector(AppController.applicationContext);
//        SharedPreference sharedPreference = new SharedPreference();
//
//        sharedPreference.setFromLogedIn(AppController.applicationContext, "fromLogedin", "");
//        sharedPreference.setGender(AppController.applicationContext, "gender_id", "");
//        sharedPreference.setEmailId(AppController.applicationContext, "email_id", "");
//        sharedPreference.setUserName(AppController.applicationContext, "first_name", "");
//        sharedPreference.setUserLastName(AppController.applicationContext, "last_name", "");
//        sharedPreference.setPhoneNumber(AppController.applicationContext, "phone", "");
//        sharedPreference.setPassword(AppController.applicationContext, "password", "");
//        sharedPreference.setImageUrl(AppController.applicationContext, "imgUrl", "");
//        sharedPreference.setDob(AppController.applicationContext, "dob", "");
//        sharedPreference.setPreferencesString(AppController.applicationContext, "user_id" + "_" + ApiRequest.TOKEN, "");
//        AppController.getInstance().setAppSessionId("");
//        AppController.getInstance().setContentSessionId("");
//
//        String LoginFrom = sharedPreference.getFromLogedIn(AppController.applicationContext, "fromLogedin");
//
//        if (LoginFrom.equals("google")) {
//            sharedPreference.setFromLogedIn(AppController.applicationContext, "fromLogedin", "");
//        } else if (LoginFrom.equals("facebook")) {
//            LoginManager.getInstance().logOut();
//        }
//        mediaDbConnector.dropAllTables();
//        sharedPreference.setPreferenceBoolean(AppController.applicationContext, sharedPreference.KEY_IS_OTP_VERIFIED, false);
//        sharedPreference.setPreferenceBoolean(AppController.applicationContext, sharedPreference.KEY_IS_LOGGED_IN, false);
//        LoginManager.getInstance().logOut();
//
//        Intent intent = new Intent(getApplicationContext(), SignupScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("logout", "NoStatus");
//        HomeActivity.getInstance().startActivity(intent);
//        if (HomeActivity.getInstance() != null) {
//            ((HomeActivity) HomeActivity.getInstance())
//                    .closeActivity();
//        }
//
//    }
//}
