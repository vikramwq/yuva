package com.multitv.yuv.utilities;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.models.ChannelsData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by naseeb on 7/21/2017.
 */

public class LiveChannelUtils {
    public static void getLiveChannelResponse(String userID, final int offset,
                                              final LiveChannelInterface liveChannelInterface) {
        final Realm realm = Realm.getDefaultInstance();
        ChannelsData channelsData = realm.where(ChannelsData.class).findFirst();
        if (channelsData != null) {
            channelsData = realm.copyFromRealm(channelsData);
            if (!realm.isClosed())
                realm.close();
            boolean isLiveVersionChanged = VersionUtils.getIsLiveVersionChanged(channelsData);
            if (!isLiveVersionChanged) {
                liveChannelInterface.onLiveChannelApiResponse(channelsData);
                return;
            }
        }
        if (!realm.isClosed())
            realm.close();

        String url;
        if (userID != null && userID.length() > 0) {
            url = ApiRequest.LIVE_URL + "?customer_id=" + userID;
        } else {
            url = ApiRequest.LIVE_URL;
        }
        Log.d("LiveChannelUtils", "channels api====" + url);
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String response = new String(mcipher.decryptmyapi(mObj.optString("result")));

                                Log.d("LiveChannelUtils", "Api response====>>>> live" + response);
                                final ChannelsData channelsData = new Gson().fromJson(response.trim(), ChannelsData.class);
                                if (channelsData != null) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.copyToRealmOrUpdate(channelsData);
                                        }
                                    });
                                    if (!realm.isClosed())
                                        realm.close();

                                    liveChannelInterface.onLiveChannelApiResponse(channelsData);
                                } else
                                    liveChannelInterface.onLiveChannelApiFailure();
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            liveChannelInterface.onLiveChannelApiFailure();
                        }
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                liveChannelInterface.onLiveChannelApiFailure();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device", "android");
                params.put("live_offset", "" + offset);
                params.put("live_limit", "10");

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public interface LiveChannelInterface {
        void onLiveChannelApiResponse(ChannelsData channelsData);

        void onLiveChannelApiFailure();
    }
}
