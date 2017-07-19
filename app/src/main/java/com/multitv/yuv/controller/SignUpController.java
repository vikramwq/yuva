package com.multitv.yuv.controller;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.interfaces.SignUpListener;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by cyberlinks on 19/1/17.
 */

public class SignUpController {

    private Context mContext;
    private SignUpListener mSignUpListener;
    private final String TAG = ".SignUpController";


    public SignUpController(Context context, SignUpListener signUpListener) {
        mContext = context;
        mSignUpListener = signUpListener;
    }

    public void sendInfoToServer(final String id, final String firstName, final String lastName, final String gender, final String link, final String locale, final String name, final String email, final String location, final String dob, final int loginThrough, final String phoneNum) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + ApiRequest.LOGIN_URL1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                mSignUpListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SIGNUPACTIVITY", "****SIGNUP-SocialApi****" + "Error: " + error.getMessage());
                mSignUpListener.onError();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    final String model = Build.MODEL;
                    final String networkType = getNetType(mContext);

                    final String token = FirebaseInstanceId.getInstance().getToken();

                    final String android_id = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                    final String carrierName = manager.getNetworkOperatorName();

                    final int os_version_code = android.os.Build.VERSION.SDK_INT;

                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;

                    final String resolution = height + "*" + width;

                    String dodjson = "";
                    String ddjson = "";
                    String socialJson = "";

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("os_version", String.valueOf(os_version_code));
                        jsonObject.put("app_version", "1.0");
                        jsonObject.put("network_type", networkType);
                        jsonObject.put("network_provider", carrierName);
                        dodjson = jsonObject.toString();
                    } catch (JSONException e) {
                        //ExceptionUtils.printStacktrace(e);
                        Log.e("LOGIN_PARAM", "getParams:1 " + e.getMessage());
                    }

                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("make_model", model);
                        jsonObject1.put("os", "android");
                        jsonObject1.put("screen_resolution", resolution);
                        jsonObject1.put("push_device_token", token == null ? "" : token);
                        jsonObject1.put("device_type", "mobile");
                        jsonObject1.put("platform", "android");
                        jsonObject1.put("device_unique_id", android_id);

                        ddjson = jsonObject1.toString();
                    } catch (JSONException e) {
                        //ExceptionUtils.printStacktrace(e);
                        Log.e("LOGIN_PARAM", "getParams:2 " + e.getMessage());
                    }

                    String provider = "";
                    if (loginThrough == 1) {
                        provider = "facebook";
                    } else if (loginThrough == 2) {
                        provider = "google";
                    }
                    String type = "";
                    if (loginThrough == 0) {
                        if (!TextUtils.isEmpty(phoneNum))
                            type = "phone";
                        else if (!TextUtils.isEmpty(email))
                            type = "email";
                    } else {
                        type = "social";
                    }

                    String phone = "";
                    if (loginThrough == 0) {
                        if (!TextUtils.isEmpty(phoneNum))
                            phone = phoneNum;
                        else if (!TextUtils.isEmpty(email))
                            phone = email;
                    } else {
                        phone = "";
                    }

                    JSONObject socialJsonObject = new JSONObject();
                    try {

                        socialJsonObject.put("id", id);
                        socialJsonObject.put("first_name", firstName);
                        socialJsonObject.put("last_name", lastName);
                        socialJsonObject.put("gender", gender);
                        socialJsonObject.put("link", link);
                        socialJsonObject.put("locale", locale);
                        socialJsonObject.put("name", name);
                        socialJsonObject.put("email", email);
                        socialJsonObject.put("location", location);
                        socialJsonObject.put("dob", dob);

                        socialJson = socialJsonObject.toString();

                    } catch (JSONException e) {
                        // ExceptionUtils.printStacktrace(e);
                        Log.e("LOGIN_PARAM", "getParams:2 " + e.getMessage());
                    }


                    Map<String, String> params = new HashMap<>();

                    params.put("phone", phone);
                    params.put("type", type);
                    params.put("dd", ddjson);
                    params.put("dod", dodjson);
                    if (!TextUtils.isEmpty(provider))
                        params.put("provider", provider);
                    if (type.equalsIgnoreCase("social") && provider.equalsIgnoreCase("facebook") || provider.equalsIgnoreCase("google"))
                        params.put("social", socialJson);

                    Set<String> keySet = params.keySet();
                    for (String key : keySet) {
                        Log.e("LOGIN_PARAM", "getParams: " + key + "   " + params.get(key));
                    }
                    return params;
                } catch (Exception e) {
                    Log.e("SignUp-Error1", e.getMessage());
                } catch (IncompatibleClassChangeError e) {
                    Log.e("SignUp-Error2", e.getMessage());
                }

                return null;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public String getNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetInfo) {
            Log.e("getNetType : ", activeNetInfo.toString());
            if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int type = mTelephonyManager.getNetworkType();
                if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN || type == TelephonyManager.NETWORK_TYPE_GPRS || type == TelephonyManager.NETWORK_TYPE_EDGE) {
                    return "mobile";
                } else {
                    return "Other";
                }
            }
        }
        return "No Internet Network";
    }


}
