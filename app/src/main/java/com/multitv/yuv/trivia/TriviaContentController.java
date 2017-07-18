package com.multitv.yuv.trivia;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.Tracer;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.GetCallback;
import com.iainconnor.objectcache.PutCallback;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.interfaces.GetTriviaContentListener;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.trivia_data.Trivia;
import com.multitv.yuv.models.trivia_data.TriviaContent;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.MultitvCipher;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by cyberlinks on 12/1/17.
 */

public class TriviaContentController {

    private final String TAG = AppConfig.BASE_TAG + ".TriviaContentController";
    //private static TriviaContentController mTriviaController;
    private Context mContext;
    //    private Trivia trivia;
    private GetTriviaContentListener getTriviaContentListener;

    /**
     * Constructor
     *
     * @param context
     */
    public TriviaContentController(Context context) {
        Tracer.error(TAG, "TriviaContentController: ");
        mContext = context;
    }

    public void setTriviaListener(GetTriviaContentListener triviaListener) {
        getTriviaContentListener = triviaListener;
    }

    public void getTriviaData() {
        final Type triviaObjectType = new TypeToken<Trivia>() {
        }.getType();
        AppController.getInstance().getCacheManager().getAsync(AppConstants.CACHE_TRIVIA_KEY, Trivia.class, triviaObjectType, new GetCallback() {
            @Override
            public void onSuccess(final Object object) {
                if (object != null) {
                    Trivia trivia = (Trivia) object;
                    if (getTriviaContentListener != null) {
                        getTriviaContentListener.onTriviaSuccess(trivia.triviaContents);
                    }
                } else {
                    getTriviaDataFromServer();
                }
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionUtils.printStacktrace(e);
            }
        });
    }

    public void getTriviaDataFromServer() {
        if (ConnectionManager.getInstance(mContext).isConnected()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    AppUtils.generateUrl(getApplicationContext(), ApiRequest.TRIVIA_CONTENT), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject mObj = new JSONObject(response);
                        if (mObj.optInt("code") == 1) {
                            MultitvCipher mcipher = new MultitvCipher();
                            String str = new String(mcipher.decryptmyapi(mObj.optString("result")));


                            Log.d(this.getClass().getName(),"trivia response======="+str);

                            if (str != null) Tracer.error(TAG, "Trivia Response : " + str);
                            List<TriviaContent> triviaList = Json.parseList(str.trim(), TriviaContent.class);

                            if (getTriviaContentListener != null) {
                                getTriviaContentListener.onTriviaSuccess(triviaList);
                                return;
                            }

                            if (triviaList != null && triviaList.size() > 0) {
                                Tracer.error(TAG, "Fresh Trivia List count : " + triviaList.size());
                                Trivia trivia = new Trivia();
                                trivia.triviaContents = triviaList;

                                AppController.getInstance().getCacheManager().putAsync(AppConstants.CACHE_TRIVIA_KEY, trivia, new PutCallback() {
                                    @Override
                                    public void onSuccess() {
                                        new SharedPreference().setPreferenceBoolean(mContext, Constant.IS_TRIVIA_EXIST);
                                        Tracer.error("CacheManager", AppConstants.CACHE_TRIVIA_KEY + " object saved successfully");
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        ExceptionUtils.printStacktrace(e);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Tracer.info(TAG, "Got error : " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("limit", ApiRequest.TRIVIA_LIMIT);
                    params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));

                    Set<String> keys = params.keySet();
                    for (String key : keys) {
                        Tracer.error(TAG, "getContent().getParams: " + key + "      " + params.get(key));
                    }
                    return params;
                }
            };
         /*   stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
    }
}
