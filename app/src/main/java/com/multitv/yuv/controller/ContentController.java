package com.multitv.yuv.controller;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.Tracer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by arungoyal on 22/05/17.
 */

public class ContentController {


    private static ContentController instance = null;
    private static Object mutex = new Object();

    private ContentController() {
    }

    public static ContentController getInstance() {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null) instance = new ContentController();
            }
        }
        return instance;
    }


    public void getContentDetails(final String contentCode) {
        Log.d(this.getClass().getName(), "getContentDetails called=====>" + contentCode);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.FETCHING_CONTENT_DATA), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        final String result = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Log.d(this.getClass().getName(), "result from content controller====>> " + result);

                        JSONObject jsonObject = new JSONObject(result);
                        String contentData = jsonObject.getString("content");

                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didContentReceived, contentData);


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Tracer.error("ContentController", "" + e.getMessage());

                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didContentReceivedFailed, null);


                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("****Get_otp_api****", "Error: " + error.getMessage());

                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didContentReceivedFailed, null);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    Map<String, String> params = new HashMap<>();

                    params.put("player_code", contentCode);
                    params.put("token", ApiRequest.TOKEN);


                    Set<String> keySet = params.keySet();
                    for (String key : keySet) {
                        Log.d(this.getClass().getName(), "parameters for content" + key + "   " + params.get(key));
                    }
                    return checkParams(params);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                } catch (IncompatibleClassChangeError e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private Map<String, String> checkParams(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }


    public void subscribeChannel(final String channelId, String isSubscribed) {
        String url;
        if (isSubscribed.equals("1")) {
            url = ApiRequest.BASE_URL_VERSION_3 + "channel/unsubscribe/token/" + ApiRequest.TOKEN;
        } else {
            url = ApiRequest.BASE_URL_VERSION_3 + "channel/subscribe/token/" + ApiRequest.TOKEN;
        }

        SharedPreference sharedPreference = new SharedPreference();
        final String userID = sharedPreference.getPreferencesString(AppController.getInstance(), "user_id" + "_" + ApiRequest.TOKEN);

        Log.d(this.getClass().getName(), "subscribeChannel called=====>" + channelId + "    " + url);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {

                        Log.d("ContentController", "Subscription done");


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tracer.error("ContentController", "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("****Get_otp_api****", "Error: " + error.getMessage());
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didContentReceivedFailed, null);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("channel_id", channelId);
                    params.put("customer_id", "" + userID);
                    params.put("token", ApiRequest.TOKEN);

                    return checkParams(params);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                } catch (IncompatibleClassChangeError e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    public void doNotificationTask(final String channelId, final String isNotified) {


        String url = ApiRequest.BASE_URL_VERSION_3 + "channel/subscribe/token/" + ApiRequest.TOKEN;


        SharedPreference sharedPreference = new SharedPreference();
        final String userID = sharedPreference.getPreferencesString(AppController.getInstance(), "user_id" + "_" + ApiRequest.TOKEN);

        Log.d(this.getClass().getName(), "subscribeChannel called=====>" + channelId + "    " + url);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        Log.d("ContentController", "Notification done");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tracer.error("ContentController", "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("****Get_otp_api****", "Error: " + error.getMessage());
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didContentReceivedFailed, null);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("channel_id", channelId);
                    params.put("customer_id", "" + userID);
                    params.put("token", ApiRequest.TOKEN);
                    params.put("donot_notify", "1");

                    return checkParams(params);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                } catch (IncompatibleClassChangeError e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    public void reportContent(final String contentId) {
        Log.d(this.getClass().getName(), "contentId =====>" + contentId);

        SharedPreference sharedPreference = new SharedPreference();
        final String userID = sharedPreference.getPreferencesString(AppController.getInstance(), "user_id" + "_" + ApiRequest.TOKEN);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.REPORT_ABUSE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        Toast.makeText(AppController.getInstance(), "Content Reported", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Tracer.error("ContentController", "" + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("****Get_otp_api****", "Error: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    Map<String, String> params = new HashMap<>();

                    params.put("content_id", contentId);
                    params.put("user_id", userID);


                    Set<String> keySet = params.keySet();
                    for (String key : keySet) {
                        Log.d(this.getClass().getName(), "parameters for content" + key + "   " + params.get(key));
                    }
                    return checkParams(params);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                } catch (IncompatibleClassChangeError e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}





