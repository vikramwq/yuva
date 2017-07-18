package com.multitv.yuv.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.utilities.Constant;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.multitv.yuv.models.home.Cat_cntn;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by naseeb on 5/22/2017.
 */

public class DownloadUtils {


    public static void download(Activity activity, Cat_cntn contentData, MediaDbConnector mediaDbConnector) {
        try {
            if (contentData == null || TextUtils.isEmpty(contentData.download_path)) {
                Toast.makeText(activity, "This media is not downloadable.", Toast.LENGTH_LONG).show();
                return;
            }
            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(contentData.download_path));

            //Setting title of request
            request.setTitle(!TextUtils.isEmpty(contentData.title) ? contentData.title : "");

            //Setting description of request
            //request.setDescription(!TextUtils.isEmpty(contentData.des) ? contentData.des : "");

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String videoFileName = "VID_" + timeStamp + ".mp4";


            //Set the local destination for the downloaded file to a path
            //within the application's external files directory
            request.setDestinationInExternalPublicDir("",
                    Constant.DIRECTORY_NAME + File.separator + videoFileName);

            if (mediaDbConnector != null) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                Cursor cursor = downloadManager.query(query);

                boolean isDownloading = false;
                long downloadReferenceId = -1;

                if (!cursor.moveToFirst()) {
                    isDownloading = true;

                    //Enqueue download and save into referenceId
                    downloadReferenceId = downloadManager.enqueue(request);
                }

                if (cursor != null && !cursor.isClosed())
                    cursor.close();

                MediaItem mediaItem = new MediaItem();
                mediaItem.setDesc(contentData.des);
                mediaItem.setTitle(contentData.title);
                mediaItem.setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constant.DIRECTORY_NAME + File.separator + videoFileName);
                mediaItem.setName(videoFileName);
                mediaItem.setContentId(contentData.id);
                mediaItem.setExpiry(contentData.download_expiry);
                Log.d("DownloadUtils","data to insert===>>> "+new Gson().toJson(contentData));
                mediaItem.setType(new Gson().toJson(contentData));
                Calendar lCDateTime = Calendar.getInstance();
                long timeStamp1 = lCDateTime.getTimeInMillis();
                mediaItem.setTimeStamp(timeStamp1);
                mediaItem.setDownloadReferenceId(downloadReferenceId);
                if (isDownloading)
                    mediaItem.setDownloadStatus(DownloadManager.STATUS_RUNNING);
                else
                    mediaItem.setDownloadStatus(DownloadManager.STATUS_PENDING);
                mediaItem.setDownloaded(false);
                mediaItem.setDownloadPath(contentData.download_path);

                mediaDbConnector.addMedia(mediaItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadNext(Context context) {
        MediaDbConnector mediaDbConnector = new MediaDbConnector(context.getApplicationContext());
        List<MediaItem> pendingDownloadsList = mediaDbConnector.getPendingDownloads();
        if (pendingDownloadsList != null && !pendingDownloadsList.isEmpty()) {
            MediaItem mediaItem = pendingDownloadsList.get(0);
            if (mediaItem == null || TextUtils.isEmpty(mediaItem.getDownloadPath()))
                return;

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mediaItem.getDownloadPath()));

            //Setting title of request
            request.setTitle(!TextUtils.isEmpty(mediaItem.getTitle()) ? mediaItem.getTitle() : "");

            //Set the local destination for the downloaded file to a path
            //within the application's external files directory
            request.setDestinationInExternalPublicDir("",
                    Constant.DIRECTORY_NAME + File.separator + mediaItem.getName());

            long downloadReferenceId = downloadManager.enqueue(request);
            mediaDbConnector.updateMediaItem(mediaItem.getContentId(), downloadReferenceId, DownloadManager.STATUS_RUNNING);
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("DOWNLOAD_UI_UPDATE").putExtra("DOWNLOAD_REFERENCE_ID", downloadReferenceId));
        }
    }

    public static void removeDownload(Context context, MediaItem mediaItem) {
        MediaDbConnector mediaDbConnector = new MediaDbConnector(context.getApplicationContext());
        mediaDbConnector.removeMediaItem(mediaItem.getContentId());

        File downloadedFile = new File(mediaItem.getPath());
        if (downloadedFile.exists())
            downloadedFile.delete();
    }
}
