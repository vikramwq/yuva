package com.multitv.yuv.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.service.UpdatePushStatus;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.Json;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.broadcast.NotificationBroadcastReceiver;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.ModelSLAT;
import com.multitv.yuv.models.check_version.CheckVersion;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.ApkDownloadTask;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;

import static com.multitv.yuv.utilities.Constant.EXTRA_ACKNOWLEDGE_TYPE;
import static com.multitv.yuv.utilities.Constant.EXTRA_CONTENT_INFO;
import static com.multitv.yuv.utilities.Constant.EXTRA_NOTIFICATION_ID;
import static com.multitv.yuv.utilities.Constant.EXTRA_THUMBNAIL_PATH;
import static com.multitv.yuv.utilities.Constant.EXTRA_UPDATE_UNREAD_COUNT;

/**
 * Created by delhivery on 9/28/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppConfig.BASE_TAG + ".MyFirebaseMessagingService";
    private static final String PUSH_TYPE_UPDATE = "UPDATE";
    private static final String PUSH_TYPE_NOTIFY = "NOTIFY";
    private static final String PUSH_TYPE_WIPE = "WIPE";
    private static final String PUSH_TYPE_UNINSTALL = "UNINSTALL";
    private static final String PUSH_TYPE_SLATIN = "SLATIN";
    private static final String PUSH_TYPE_SLATOUT = "SLATOUT";
    private static final String PUSH_KEY_TYPE = "type";
    private static final String PUSH_KEY_TITLE = "title";
    private static final String PUSH_KEY_MESSAGE = "message";
    private static final String PUSH_KEY_THUMBNAIL = "thumb";
    private static final String PUSH_KEY_CONTENT_INFO = "content_info";
    private static final String PUSH_KEY_NOTIFICATION_ID = "notification_id";
    private static final String PUSH_KEY_APK_PATH = "path";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Tracer.error(TAG, "onMessageReceived: ");
        printPushData(remoteMessage);

        Map<String, String> dataList = remoteMessage.getData();
        String type = dataList.get(PUSH_KEY_TYPE);
        if (type == null) {
            return;
        }

        Intent intent = new Intent(this, UpdatePushStatus.class);
        String notificationId = getOptString(dataList.get(PUSH_KEY_NOTIFICATION_ID)).trim();
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        intent.putExtra(EXTRA_ACKNOWLEDGE_TYPE, AppConstants.PUSH_STATUS_DELIVERED);
        intent.putExtra(EXTRA_UPDATE_UNREAD_COUNT, false);
        startService(intent);

        SharedPreference sharedPreference = new SharedPreference();
        Set<String> unreadSet = sharedPreference.getUnreadNotificationsList(this, sharedPreference.KEY_NOTIFICATION_SET);
        Tracer.error(TAG, "Unread notification count : " + unreadSet.size());
        unreadSet.add(notificationId);
        Tracer.error(TAG, "Unread notification count after update: " + unreadSet.size());
        Set<String> stringSet = new HashSet<>();
        stringSet.addAll(unreadSet);
        sharedPreference.setUnreadNotificationsList(this, sharedPreference.KEY_NOTIFICATION_SET, stringSet);

        Intent intentUpdateBadgeCount = new Intent("UPDATE-BADGE-COUNT");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentUpdateBadgeCount);

        switch (type.toUpperCase().trim()) {
            case PUSH_TYPE_UPDATE:
                //requestToLoadNewApk();
                break;
            case PUSH_TYPE_NOTIFY:
                if (!PreferenceData.isNotificationEnable(this)) {
                    return;
                }
                notificationId = getOptString(dataList.get(PUSH_KEY_NOTIFICATION_ID)).trim();
                String title = getOptString(dataList.get(PUSH_KEY_TITLE)).trim();
                String message = getOptString(dataList.get(PUSH_KEY_MESSAGE)).trim();
                String thumbnailPath = getOptString(dataList.get(PUSH_KEY_THUMBNAIL)).trim();
                String contentInfo = getOptString(dataList.get(PUSH_KEY_CONTENT_INFO)).trim();
                Tracer.error(TAG, "onMessageReceived: NOTIFY " + contentInfo);
                if (contentInfo.isEmpty()) {
                    notifyUser(title, message, thumbnailPath, contentInfo, notificationId);
                } else {
                    notifyUserByFetchingContentInfo(title, message, thumbnailPath, contentInfo, notificationId);
                }
                break;
            case PUSH_TYPE_UNINSTALL:
                break;
            case PUSH_TYPE_WIPE:
                try {
                    AppController.getInstance().getCacheManager().clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PUSH_TYPE_SLATIN:
                ModelSLAT.getInstance(this).setAppActive();
                break;
            case PUSH_TYPE_SLATOUT:
                ModelSLAT.getInstance(this).setAppInactive();
                break;
        }
    }

    /**
     * Method to return the String from Old String by trimming it, Or Return EmptyString if this String is null
     *
     * @return
     */
    private String getOptString(String string) {
        if (string == null) {
            return "";
        }
        return string.trim();
    }

    /**
     * Method called to load the new updated Apk from the server
     */
    private void requestToLoadNewApk() {
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.CHECK_VERSION_URL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error(TAG, "requestToLoadNewApk: onResponse: " + response);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        CheckVersion checkVersion = Json.parse(str.trim(), CheckVersion.class);
                        String newVersion = checkVersion.version;
                        String apkDownloadPath = checkVersion.path;
                        if (!isUserHavingLatestVersion(newVersion)) {
                            Tracer.error(TAG, "requestToLoadNewApk: onResponse: NEED TO UPDATE");
                            new ApkDownloadTask(getApplicationContext(), apkDownloadPath, newVersion, true).execute();
                        }
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    Tracer.error(TAG, "requestToLoadNewApk: onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "requestToLoadNewApk: onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("device", "android");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Method to check weather the user have new app or not
     *
     * @param newVersion
     * @return
     */
    private boolean isUserHavingLatestVersion(String newVersion) {
        Tracer.error(TAG, "isUserHavingLatestVersion: NEW " + newVersion);
        if (newVersion == null || newVersion.trim().isEmpty()) {
            return true;
        }
        try {
            String deviceVersionName = AppUtils.getVersionNameAndCode(getApplicationContext())[0];
            Tracer.error(TAG, "isUserHavingLatestVersion: OLD " + deviceVersionName);
            Tracer.error(TAG, "isUserHavingLatestVersion: " + (Float.parseFloat(newVersion.trim()) + "    " + Float.parseFloat(deviceVersionName.trim())) + "     " + (Float.parseFloat(newVersion.trim()) > Float.parseFloat(deviceVersionName.trim())));
            if (Float.parseFloat(newVersion.trim()) <= Float.parseFloat(deviceVersionName.trim())) {
                Tracer.error(TAG, "isUserHavingLatestVersion: UPDATE APP ");
                return true;
            }
        } catch (Exception e) {
            Tracer.error(TAG, "isUserHavingLatestVersion: " + e.getMessage());
        }
        return false;
    }

    /**
     * Metod to print the push data
     *
     * @param remoteMessage
     */
    private void printPushData(RemoteMessage remoteMessage) {
        Tracer.error(TAG, "printPushData: ");
        Map<String, String> dataList = remoteMessage.getData();
        Set<String> keySet = dataList.keySet();
        for (String key : keySet) {

            Tracer.error(TAG, "printPushData: " + key + "    " + dataList.get(key));
        }
    }

    /**
     * Method to notify user by fetching the content
     *
     * @param title
     * @param message
     * @param thumbnailPath
     * @param contentId
     */
    private void notifyUserByFetchingContentInfo(final String title, final String message, final String thumbnailPath, final String contentId, final String notificationId) {
        Tracer.error(TAG, "notifyUserByFetchingContentInfo: ");
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.FETCHING_CONTENT_DATA), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error(TAG, "notifyUserByFetchingContentInfo: onResponse: " + response);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        String result = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        JSONObject jsonObject = new JSONObject(result);
                        String contentInfo = jsonObject.optString("content");
                        notifyUser(title, message, thumbnailPath, contentInfo, notificationId);
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

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("device", "android");
                params.put("content_id", contentId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Method to Notify user
     *
     * @param title
     * @param message
     * @param thumbnailPath
     * @param contentInfo
     */
    private void notifyUser(final String title, final String message, final String thumbnailPath, final String contentInfo, final String notificationId) {
        Tracer.error(TAG, "notifyUser: " + contentInfo);
        new AsyncTask<Void, Void, Bitmap>() {
            private JSONObject mContentObject;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (contentInfo.trim().isEmpty() || contentInfo.trim().equalsIgnoreCase("{}")) {
                    return;
                }
                try {
                    mContentObject = new JSONObject(contentInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mContentObject = null;
                }
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Tracer.error(TAG, "notifyUser: doInBackground: 1: " + thumbnailPath);
                try {
                    String thumbURL = thumbnailPath;
                    if (mContentObject != null) {
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
                    }

                    URL url = new URL(thumbURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    Tracer.error(TAG, "notifyUser: doInBackground: 2: " + myBitmap);
                    return myBitmap;
                } catch (IOException e) {
                    Tracer.error(TAG, "notifyUser: doInBackground: E: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Tracer.error(TAG, "notifyUser: onPostExecute: " + bitmap);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.toolbar_icon));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setColor(getResources().getColor(R.color.tab_background));
                    notificationBuilder.setSmallIcon(R.drawable.notification_icon_transparent);
                } else {
                    notificationBuilder.setSmallIcon(R.drawable.ic_notification_bg);
                }

                notificationBuilder.setContentTitle(title);
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSound(defaultSoundUri);

                if (bitmap != null && !bitmap.isRecycled()) {
                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                    bigPictureStyle.bigPicture(bitmap);
                    bigPictureStyle.setSummaryText(message);
                    notificationBuilder.setStyle(bigPictureStyle);
                } else {
                    notificationBuilder.setContentText(message);
                }
                //notificationBuilder.setContentIntent(pendingIntent);
                notificationBuilder.setContentIntent(getContentPendingIntent(contentInfo, mContentObject, thumbnailPath, notificationId));

                notificationBuilder.setDeleteIntent(getDeletePendingIntent(notificationId));
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(PreferenceData.getNotificationId(getApplicationContext()), notificationBuilder.build());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private PendingIntent getDeletePendingIntent(String notificationId) {
        Intent intentDelete = new Intent(MyFirebaseMessagingService.this, UpdatePushStatus.class);
        intentDelete.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        intentDelete.putExtra(EXTRA_ACKNOWLEDGE_TYPE, AppConstants.PUSH_STATUS_CANCELED);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), (int) System.currentTimeMillis(), intentDelete, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    private PendingIntent getContentPendingIntent(String contentInfo, JSONObject contentObject, String thumbnailPath, String notificationId) {
        Intent intentContent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        intentContent.putExtra(EXTRA_CONTENT_INFO, contentInfo);
        /*if (contentObject != null) {
            intentContent.putExtra(NotificationBroadcastReceiver.EXTRA_CONTENT_OBJECT_STRING, contentObject.toString());
        }*/
        intentContent.putExtra(EXTRA_THUMBNAIL_PATH, thumbnailPath);
        intentContent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(), intentContent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }
}

