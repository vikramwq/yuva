package com.multitv.yuv.controller;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.NotificationCenter;

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

                        JSONObject jsonObject=new JSONObject(result);
                        String contentData=jsonObject.getString("content");

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













}





