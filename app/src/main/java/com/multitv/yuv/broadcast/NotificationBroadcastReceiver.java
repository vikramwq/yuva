package com.multitv.yuv.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.service.UpdatePushStatus;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.multitv.yuv.activity.SplashScreen;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.utilities.AppConfig;

/**
 * Created by cyberlinks on 28/12/16.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = AppConfig.BASE_TAG + ".NotificationBroadcastReceiver";
    private JSONObject mContentObject;

    @Override
    public void onReceive(Context context, Intent intent) {

        List<String> listdata=null;
        Tracer.error(TAG, "onReceive() : ");
        String contentInfo = intent.getStringExtra(Constant.EXTRA_CONTENT_INFO);
        //String contentObjectString = intent.getStringExtra(EXTRA_CONTENT_OBJECT_STRING);
        String thumbnailPath = intent.getStringExtra(Constant.EXTRA_THUMBNAIL_PATH);
        String notificationId = intent.getStringExtra(Constant.EXTRA_NOTIFICATION_ID);

        startService(context, notificationId);
       /* if (contentObjectString != null) {
            try {
                mContentObject = new JSONObject(contentObjectString);
            } catch (JSONException e) {
                e.printStackTrace();
                mContentObject = null;
            }
        }*/
        if (contentInfo.isEmpty() || contentInfo.equalsIgnoreCase("{}")) {
            Intent intentContent = new Intent(context, SplashScreen.class);
            intentContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentContent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Tracer.error(TAG, "onReceive() : starting Splash Activity ");
            context.startActivity(intentContent);
        } else {
            try {
                mContentObject = new JSONObject(contentInfo);
            } catch (JSONException e) {
                e.printStackTrace();
                mContentObject = null;
            }
            if (mContentObject != null) {
                if (mContentObject.optString("source").equalsIgnoreCase("sonyliv")) {
                    //intentContent = new Intent(context, SonyPlayerActivity.class);
                } else if (mContentObject.optString("source").equalsIgnoreCase("viu")) {
                    //intentContent = new Intent(context, VIUPlayerActivity.class);
                } else {
                    Intent intentContent = new Intent(context, MultiTvPlayerActivity.class);


                    Gson gson=new Gson();
                    Cat_cntn cat_cntn= gson.fromJson(contentInfo,Cat_cntn.class);


//                    Cat_cntn cat_cntn1=new Cat_cntn();
//                    cat_cntn1.id=mContentObject.optString("id");
//                    cat_cntn1.des=mContentObject.optString("des");
//                    cat_cntn1.url=mContentObject.optString("url");
//                    cat_cntn1.share_url=mContentObject.optString("share_url");
//                    cat_cntn1.title=mContentObject.optString("title");
//                    cat_cntn1.media_type=mContentObject.optString("media_type");
//                    cat_cntn1.category_id=mContentObject.optString("category_id");
//                    JSONArray category_ids = mContentObject.optJSONArray("category_ids");
//
//
//                    try {
//                        listdata = new ArrayList<String>();
//                        if (category_ids != null) {
//                            for (int i = 0; i < category_ids.length(); i++) {
//                                listdata.add(category_ids.getString(i));
//                            }
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

//                    cat_cntn1.category_ids=listdata;
                    intentContent.putExtra(Constant.CONTENT_TRANSFER_KEY, cat_cntn);
                    intentContent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                    intentContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentContent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Tracer.error(TAG, "onReceive() : starting Multiplayer Activity ");
                    context.startActivity(intentContent);
                }
            }
        }


    }

        /*Intent intentContent = new Intent(context, contentInfo.isEmpty() || contentInfo.equalsIgnoreCase("{}") ? SplashScreen.class : MultiTvPlayerActivity.class);

        if (mContentObject != null) {
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
        }
        intentContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentContent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Tracer.error(TAG, "onReceive() : starting Activity ");
        context.startActivity(intentContent);*/

    private void startService(Context context, String notificationId) {
        Tracer.error(TAG, "startService() : ");
        Intent intentDelete = new Intent(context, UpdatePushStatus.class);
        intentDelete.putExtra(Constant.EXTRA_NOTIFICATION_ID, notificationId);
        intentDelete.putExtra(Constant.EXTRA_ACKNOWLEDGE_TYPE, AppConstants.PUSH_STATUS_CLICK);
        intentDelete.putExtra(Constant.EXTRA_UPDATE_UNREAD_COUNT, true);
        context.startService(intentDelete);
    }
}
