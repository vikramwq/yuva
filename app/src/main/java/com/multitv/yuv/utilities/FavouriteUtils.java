package com.multitv.yuv.utilities;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by naseeb on 10/6/2016.
 */

public class FavouriteUtils {
    private static ToggleInfo toggleInfo = new ToggleInfo();

    public static int initialize(int position, String favourite) {
        toggleInfo.tag = position;
        toggleInfo.favourite = favourite;

        int favouriteDrawableId;

        if (favourite.equals("1")) {
            favouriteDrawableId = R.mipmap.favorite_img_dark;
            toggleInfo.isEnabled = true;
        } else {
            favouriteDrawableId = R.mipmap.favorite_black;
            toggleInfo.isEnabled = false;
        }

        return favouriteDrawableId;
    }

    public static ToggleInfo getTag() {
        return toggleInfo;
    }

    public static int doNeedfulForFavourite(Context context, ToggleInfo toggleInfo,
                                            String user_id, String content_id, String content_type) {
        int favouriteDrawableId;

        if (toggleInfo.favourite.equals("0")) {
            favouriteDrawableId = R.mipmap.favorite_img_dark;
            toggleInfo.isEnabled = true;
            toggleInfo.favourite = "1";
            Toast.makeText(context, "Added to Favorite", Toast.LENGTH_LONG).show();
        } else {
            favouriteDrawableId = R.mipmap.favorite_black;
            toggleInfo.isEnabled = false;
            toggleInfo.favourite = "0";
            Toast.makeText(context, "Removed from Favorite", Toast.LENGTH_LONG).show();
        }

        addToFavorite(user_id, content_id, content_type, toggleInfo.favourite);

        return favouriteDrawableId;
    }

    public static class ToggleInfo {
        public int tag;
        public String favourite;
        public boolean isEnabled;
    }

    private static void addToFavorite(final String user_id, final String content_id, final String content_type,
                                      final String favourite) {

        Tracer.error("fav_url", "" + AppUtils.generateUrl(AppController.getInstance(), ApiRequest.FAVORITE_URL));
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(AppController.getInstance(), ApiRequest.FAVORITE_URL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error("api_add_fav", response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("video", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("user_id", user_id);
                params.put("content_type", content_type);
                params.put("content_id", content_id);
                params.put("favorite", favourite);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
