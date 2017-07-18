package com.multitv.yuv.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Tracer;

import static com.multitv.yuv.utilities.Constant.EXTRA_ACKNOWLEDGE_TYPE;
import static com.multitv.yuv.utilities.Constant.EXTRA_NOTIFICATION_ID;
import static com.multitv.yuv.utilities.Constant.EXTRA_UPDATE_UNREAD_COUNT;

/**
 * Created by cyberlinks on 28/12/16.
 */

public class UpdatePushStatus extends IntentService {

    private static final String TAG = AppConfig.BASE_TAG + ".UpdatePushStatus";

    private String user_id;
    private SharedPreference sharedPreference;

    public UpdatePushStatus() {
        super(UpdatePushStatus.class.getSimpleName());
        Tracer.error(TAG, "UpdatePushStatus() : ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Tracer.error(TAG, "onHandleIntent : ");
        sharedPreference = new SharedPreference();
        user_id = sharedPreference.getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);
        Tracer.error(TAG, "onHandleIntent() : user_id : " + user_id);

        String notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID);
        String acknowledgeType = intent.getStringExtra(EXTRA_ACKNOWLEDGE_TYPE);
        boolean updateCount = intent.getBooleanExtra(EXTRA_UPDATE_UNREAD_COUNT, false);
        Tracer.error(TAG, "onHandleIntent : ID : " + notificationId + " acknowledge : " + acknowledgeType);
        try {
            URL url = new URL(AppUtils.generateDriveUrl(getApplicationContext(), ApiRequest.UPDATE_PUSH_STATUS));
            /*String param = "user_id =" + URLEncoder.encode("1002", "UTF-8") +
                    "&notification_id =" + URLEncoder.encode("5853d9c9af904f603cb62021", "UTF-8") +
                    "&app_id =" + URLEncoder.encode("58340f79d778f04e4405eb71", "UTF-8") +
                    "&acknowledge_type =" + URLEncoder.encode("delivered", "UTF-8");*/

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            //set the output to true, indicating you are outputting(uploading) POST data
            conn.setDoOutput(true);
            //once you set the output to true, you don’t really need to set the request method to post, but I’m doing it anyway
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> params = new ArrayList<>();
            /*params.add(new BasicNameValuePair("user_id", "1002"));
            params.add(new BasicNameValuePair("notification_id", "5853d9c9af904f603cb62021"));
            params.add(new BasicNameValuePair("app_id", "58340f79d778f04e4405eb71"));
            params.add(new BasicNameValuePair("acknowledge_type", "delivered"));*/

            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("notification_id", notificationId));
            params.add(new BasicNameValuePair("app_id", ApiRequest.NOTIFICATION_APP_ID));
            params.add(new BasicNameValuePair("acknowledge_type", acknowledgeType));

            /*//send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();*/
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            Tracer.error(TAG, "Query : " + getQuery(params));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            String response = "";
            //start listening to the stream
            /*Scanner inStream = new Scanner(conn.getInputStream());
            //process the stream and store it in StringBuilder
            while (inStream.hasNextLine())
                response += (inStream.nextLine());*/
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
            Tracer.error(TAG, "onHandleIntent() : " + response);

            if (updateCount && response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int statusCode = jsonObject.optInt("statusCode");
                    if (statusCode == 1) {
                        Set<String> unreadSet = sharedPreference.getUnreadNotificationsList(this, sharedPreference.KEY_NOTIFICATION_SET);
                        Tracer.error(TAG, "Unread notification count : " + unreadSet.size());
                        if (unreadSet.contains(notificationId)) {
                            unreadSet.remove(notificationId);
                            Set<String> stringSet = new HashSet<>();
                            stringSet.addAll(unreadSet);
                            sharedPreference.setUnreadNotificationsList(this, sharedPreference.KEY_NOTIFICATION_SET, stringSet);
                        }
                        Tracer.error(TAG, "Unread notification count after remove: " + unreadSet.size());


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent intentUpdateBadgeCount = new Intent("UPDATE-BADGE-COUNT");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentUpdateBadgeCount);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}