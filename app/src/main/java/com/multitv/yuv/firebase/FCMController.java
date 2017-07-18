package com.multitv.yuv.firebase;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by mkr on 11/28/2016.
 */

public class FCMController {
    private static final String TAG = AppConfig.BASE_TAG + ".FCMController";
    private static FCMController mFcmController;
    private boolean mIsProcessAlreadyRunning;
    private Context mContext;

    public static FCMController getInstance(Context context) {
        if (mFcmController == null) {
            mFcmController = new FCMController(context);
        }
        return mFcmController;
    }

    /**
     * Constructor
     *
     * @param context
     */
    private FCMController(Context context) {
        Tracer.error(TAG, "FCMController: ");
        mContext = context.getApplicationContext();
    }

    /**
     * User call this method to registered the Token on the server and Generate the New token
     */
    public void registerToken() {
        Tracer.error(TAG, "registerToken: ");
        if (!mIsProcessAlreadyRunning && !PreferenceData.getAppSessionId(mContext).isEmpty()) {
            mIsProcessAlreadyRunning = true;
            new TokenGenerator(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Method Called to register the FCM Token on the Server
     */
    private void registerTokenOnTheServer() {
        Tracer.error(TAG, "registerTokenOnTheServer: ");
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.UPDATE_DEVICE), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error("****firebaseToken****", "registerTokenOnTheServer().onResponse: " + response);
                PreferenceData.setFCMTokenRegisteredOnServer(mContext);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "registerTokenOnTheServer().onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(mContext));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(mContext) ? "" + 1 : "" + 0));

                params.put("device_type", "android");
                params.put("app_session_id", PreferenceData.getAppSessionId(mContext));
                params.put("device_unique_id", ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                params.put("device_push_token", PreferenceData.getFCMToken(mContext));

                Log.e("device_unique_id", ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                Log.e("app_session_id", PreferenceData.getAppSessionId(mContext));
                Log.e("device_push_token", PreferenceData.getFCMToken(mContext));

                Set<String> keys = params.keySet();
                for (String key : keys) {
                    Tracer.error(TAG, "registerTokenOnTheServer().getParams: " + key + "      " + params.get(key));
                }
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Class use to Generate/Update the Firebase token
     */
    private class TokenGenerator extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;

        public TokenGenerator(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String newToken = FirebaseInstanceId.getInstance().getToken();
                while (newToken == null) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    newToken = FirebaseInstanceId.getInstance().getToken();
                }
                Tracer.error(TAG, "TokenGenerator.doInBackground: " + newToken + (PreferenceData.getFCMToken(mContext).equalsIgnoreCase(newToken)));
                if (!newToken.trim().isEmpty()) {
                    if (!PreferenceData.getFCMToken(mContext).equalsIgnoreCase(newToken)) {
                        PreferenceData.setFCMToken(mContext, newToken);
                        return true;
                    } else {
                        return !PreferenceData.isFCMTokenRegisteredOnServer(mContext);
                    }
                }
            } catch (Exception e) {
                Tracer.error(TAG, e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mIsProcessAlreadyRunning = false;
            if (result != null && result.booleanValue()) {
                registerTokenOnTheServer();
            }
        }
    }

}
