package com.multitv.yuv.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.notification_center.NotificationContent;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by cyberlinks on 30/12/16.
 */

public class NotificationClickAlertDialog {

    private static final String TAG = AppConfig.BASE_TAG + ".NotificationClickAlertDialog";

    public void showDialog(final Context context, final NotificationContent notificationContent) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.dialog_notification_alert, null);
        alertDialogBuilder.setView(dialogView);

        ImageView ivClose = (ImageView) dialogView.findViewById(R.id.iv_close);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tvDesc = (TextView) dialogView.findViewById(R.id.tv_desc);
        ImageView ivFeatured = (ImageView) dialogView.findViewById(R.id.iv_featured);
        Button btnPlayNow = (Button) dialogView.findViewById(R.id.btn_play_now);

        if (!TextUtils.isEmpty(notificationContent.thumb)) {
            ivFeatured.setVisibility(View.VISIBLE);
            displayPicture(ivFeatured, notificationContent.thumb, "", "");
        } else {
            ivFeatured.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(notificationContent.content_id)) {
            btnPlayNow.setVisibility(View.VISIBLE);
        } else {
            btnPlayNow.setVisibility(View.GONE);
        }
        tvTitle.setText(notificationContent.title);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-regular.ttf");
        tvTitle.setTypeface(tf);


        tvDesc.setText(notificationContent.description);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideoByFetchingContentInfo(context, notificationContent.thumb, notificationContent.content_id);
                alertDialog.dismiss();
            }
        });
    }

    private void displayPicture(final ImageView ivFeatured, final String thumbnailPath, final String contentInfo, final String notificationId) {
        Tracer.error(TAG, "displayPicture: " + contentInfo);
        new AsyncTask<Void, Void, Bitmap>() {
            //private JSONObject mContentObject;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               /* if (contentInfo.trim().isEmpty() || contentInfo.trim().equalsIgnoreCase("{}")) {
                    return;
                }
                try {
                    mContentObject = new JSONObject(contentInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mContentObject = null;
                }*/
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Tracer.error(TAG, "displayPicture: doInBackground: 1: " + thumbnailPath);
                try {
                    String thumbURL = thumbnailPath;
                    /*if (mContentObject != null) {
                        if (mContentObject.has("thumbnail")) {
                            try {
                                JSONObject thumbnailJsonObject = mContentObject.getJSONObject("thumbnail");
                                if (thumbnailJsonObject.has("large")) {
                                    String largThumbPath = thumbnailJsonObject.getString("large");
                                    if (!largThumbPath.trim().isEmpty()) {
                                        thumbURL = largThumbPath.trim();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }*/

                    URL url = new URL(thumbURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    Tracer.error(TAG, "displayPicture: doInBackground: 2: " + myBitmap);
                    return myBitmap;
                } catch (IOException e) {
                    Tracer.error(TAG, "displayPicture: doInBackground: E: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Tracer.error(TAG, "notifyUser: onPostExecute: " + bitmap);
                /*Intent intent = new Intent(getApplicationContext(), contentInfo.isEmpty() || contentInfo.equalsIgnoreCase("{}") ? SplashScreen.class : MultiTvPlayerActivity.class);
                if (mContentObject != null) {
                    intent.putExtra("VIDEO_URL", mContentObject.optString("url"));
                    intent.putExtra("descreption", mContentObject.optString("des"));
                    intent.putExtra("title", mContentObject.optString("title"));
                    intent.putExtra("like", mContentObject.optString("likes"));
                    intent.putExtra("type", mContentObject.optString("media_type"));
                    if (mContentObject.has("meta")) {
                        try {
                            JSONObject meta = mContentObject.getJSONObject("meta");
                            intent.putExtra("type", meta.optString("type").trim().isEmpty() ? mContentObject.optString("media_type") : meta.optString("type").trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    intent.putExtra("rating", mContentObject.optString("rating"));
                    JSONArray category_ids = mContentObject.optJSONArray("category_ids");
                    if (category_ids != null && category_ids.length() > 0) {
                        intent.putExtra("content_id", category_ids.optString(0));
                    }
                    intent.putExtra("fav_item", mContentObject.optString("favorite"));
                    String thumbURL = thumbnailPath;
                    if (mContentObject.has("thumbnail")) {
                        try {
                            JSONObject thumbnailJsonObject = mContentObject.getJSONObject("thumbnail");
                            if (thumbnailJsonObject.has("large")) {
                                String largThumbPath = thumbnailJsonObject.getString("large");
                                if (!largThumbPath.trim().isEmpty()) {
                                    thumbURL = largThumbPath.trim();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    intent.putExtra("thumbnail", thumbURL);
                    intent.putExtra("content_type", mContentObject.optString("source"));
                    intent.putExtra("position", 0);
                    intent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                    intent.putExtra("WATCHED_DURATION", 0);
                    intent.putExtra("SOCIAL_LIKES", mContentObject.optString("social_like"));
                    intent.putExtra("SOCIAL_VIEWS", mContentObject.optString("social_view"));
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);*/

                if (bitmap != null && !bitmap.isRecycled()) {
                    ivFeatured.setVisibility(View.VISIBLE);
                    ivFeatured.setImageBitmap(bitmap);
                } else {
                    ivFeatured.setVisibility(View.GONE);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void playVideoByFetchingContentInfo(final Context context, final String thumbnailPath, final String contentId) {
        Tracer.error(TAG, "playVideoByFetchingContentInfo: ");
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(context, ApiRequest.FETCHING_CONTENT_DATA), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error(TAG, "playVideoByFetchingContentInfo: onResponse: " + response);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        String result = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        JSONObject jsonObject = new JSONObject(result);
                        String contentInfo = jsonObject.optString("content");
                        JSONObject mContentObject;
                        if (!contentInfo.trim().isEmpty() || !contentInfo.trim().equalsIgnoreCase("{}")) {
                            try {
                                mContentObject = new JSONObject(contentInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mContentObject = null;
                            }
                            if (mContentObject != null) {
                                if (mContentObject.optString("source").equalsIgnoreCase("sonyliv")) {

                                } else if (mContentObject.optString("source").equalsIgnoreCase("viu")) {
                                } else {
                                    Intent intentContent = new Intent(context, MultiTvPlayerActivity.class);
                                    intentContent.putExtra("VIDEO_URL", mContentObject.optString("url"));
                                    intentContent.putExtra("descreption", mContentObject.optString("des"));
                                    intentContent.putExtra("title", mContentObject.optString("title"));
                                    intentContent.putExtra("like", mContentObject.optString("likes"));
                                    intentContent.putExtra("type", mContentObject.optString("media_type"));
                                    if (mContentObject.has("meta")) {
                                        try {
                                            JSONObject meta = mContentObject.getJSONObject("meta");
                                            intentContent.putExtra("type", meta.optString("type").trim().isEmpty() ? mContentObject.optString("media_type") : meta.optString("type").trim());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    intentContent.putExtra("rating", mContentObject.optString("rating"));
                                    JSONArray category_ids = mContentObject.optJSONArray("category_ids");
                                    if (category_ids != null && category_ids.length() > 0) {
                                        intentContent.putExtra("content_id", category_ids.optString(0));
                                    }
                                    intentContent.putExtra("fav_item", mContentObject.optString("favorite"));
                                    String thumbURL = thumbnailPath;
                                    if (mContentObject.has("thumbnail")) {
                                        try {
                                            JSONObject thumbnailJsonObject = mContentObject.getJSONObject("thumbnail");
                                            if (thumbnailJsonObject.has("large")) {
                                                String largThumbPath = thumbnailJsonObject.getString("large");
                                                if (!largThumbPath.trim().isEmpty()) {
                                                    thumbURL = largThumbPath.trim();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    intentContent.putExtra("thumbnail", thumbURL);
                                    intentContent.putExtra("content_type", mContentObject.optString("source"));
                                    intentContent.putExtra("position", 0);
                                    intentContent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                                    intentContent.putExtra("WATCHED_DURATION", 0);
                                    intentContent.putExtra("SOCIAL_LIKES", mContentObject.optString("social_like"));
                                    intentContent.putExtra("SOCIAL_VIEWS", mContentObject.optString("social_view"));
                                    Tracer.error(TAG, "onReceive() : starting Activity ");
                                    context.startActivity(intentContent);
                                }
                            }
                            /*intentContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentContent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/


                        }

                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    Tracer.error(TAG, "notifyUserByFetchingContentInfo: onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "notifyUserByFetchingContentInfo: onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(context));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(context) ? "" + 1 : "" + 0));

                params.put("device", "android");
                params.put("content_id", contentId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
