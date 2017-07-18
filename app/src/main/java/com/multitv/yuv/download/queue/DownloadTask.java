package com.multitv.yuv.download.queue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.DownloadedMediaListing;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.models.home.Cat_cntn;


public class DownloadTask implements Runnable {

    private static final String TAG = DownloadTask.class.getSimpleName();

    int id = 1;
    private MediaDbConnector mediaDbConnector = new MediaDbConnector(AppController.applicationContext);

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    private Cat_cntn contentData;

    long startTime;
    long elapsedTime = 0L;

    public DownloadTask(Cat_cntn contentData) {
        this.contentData = contentData;

    }

    @Override
    public void run() {
        try {

            int count;

            Log.d(this.getClass().getName(), "contentData in download task" + contentData.title);
            URL url = new URL(contentData.download_path);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            FileOutputStream outputStream;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String videoFileName = "VID_" + timeStamp + ".mp4";
            outputStream = AppController.applicationContext.openFileOutput(videoFileName, Context.MODE_PRIVATE);

            byte data[] = new byte[1024];

            long total = 0;

            showNotification(contentData.title);

            while ((count = input.read(data)) != -1) {
                total += count;
                Log.d(this.getClass().getName(), "total=======>>>" + total);

                updateProgress((int) ((total * 100) / lenghtOfFile));

                outputStream.write(data, 0, count);
            }

            Toast.makeText(AppController.applicationContext, "Download completed " + contentData.title, Toast.LENGTH_LONG).show();

            MediaItem mediaItem = new MediaItem();
            mediaItem.setDesc(contentData.des);
            mediaItem.setDuration("3:00 min");
            mediaItem.setTitle("This is title");
            mediaItem.setPath(videoFileName);
            mediaItem.setName(videoFileName);
            mediaItem.setContentId(contentData.id);
            mediaItem.setType(new Gson().toJson(contentData));
            Calendar lCDateTime = Calendar.getInstance();
            long timeStamp1 = lCDateTime.getTimeInMillis();
            mediaItem.setTimeStamp(timeStamp1);

            mediaDbConnector.addMedia(mediaItem);

            generateNotification("Download Successfully", contentData.title);
            // flushing output
            outputStream.flush();
            // closing streams
            outputStream.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            generateNotification("Download cancelled", contentData.title);
        }


    }


    public static void generateNotification(String message, String title) {

        int icon = R.drawable.toolbar_icon;
        long when = System.currentTimeMillis();

        Intent intent = new Intent(AppController.applicationContext, DownloadedMediaListing.class);
        PendingIntent contentIntent = PendingIntent.getActivity(AppController.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(AppController.applicationContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            b.setColor(AppController.applicationContext.getResources().getColor(R.color.tab_background));
            b.setSmallIcon(R.drawable.notification_icon_transparent);
        } else {
            b.setSmallIcon(R.drawable.ic_notification_bg);
        }

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) AppController.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }

    private void showNotification(String title) {
        mNotifyManager =
                (NotificationManager) AppController.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(AppController.applicationContext);
        mBuilder.setContentTitle(title)
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.toolbar_icon);
    }


    private void publishProgress(final int progress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBuilder.setProgress(100, progress, false);
                // Displays the progress bar for the first time.
//                mNotifyManager.notify(0, mBuilder.build());
            }
        }).start();
    }

    private void updateProgress(final int progress) {


        if (elapsedTime > 1000) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mBuilder.setProgress(100, progress, false);
                    mNotifyManager.notify(1, mBuilder.build());

                    startTime = System.currentTimeMillis();
                    elapsedTime = 0;
                }
            });

            Log.d("Andrognito", progress + "Progress");
        } else
            elapsedTime = new Date().getTime() - startTime;
    }
}
