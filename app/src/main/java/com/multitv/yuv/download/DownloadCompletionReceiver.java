package com.multitv.yuv.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.multitv.yuv.activity.DownloadedMediaListing;
import com.multitv.yuv.download.queue.DownloadTask;
import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.security.FileSecurityUtils;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.db.MediaDbConnector;

/**
 * Created by naseeb on 5/22/2017.
 */

public class DownloadCompletionReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            final long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId != -1) {
                final MediaDbConnector mediaDbConnector = new MediaDbConnector(context.getApplicationContext());
                final MediaItem mediaItem = mediaDbConnector.getMediaItem(referenceId);
                if (mediaItem != null) {
                    FileSecurityUtils.encryptFile(context, mediaItem.getTitle(), mediaItem.getName(), mediaItem.getPath(), new FileSecurityUtils.FileEncryptionListener() {
                        @Override
                        public void onFileEncrypted(String filePath) {
                            mediaDbConnector.updateMediaItem(referenceId, filePath);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("DOWNLOAD_UI_UPDATE").putExtra("DOWNLOAD_REFERENCE_ID", referenceId));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("DOWNLOAD_UI_UPDATE_PLAYER").putExtra("DOWNLOAD_REFERENCE_ID", referenceId));
                            showDownloadStatusToUser(mediaItem);
                            DownloadUtils.downloadNext(context);
                        }

                        @Override
                        public void onFileEncryptionError() {
                            Log.e(TAG, "Something went wrong in encrypting video file");
                            mediaDbConnector.updateMediaItemError(referenceId);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("DOWNLOAD_UI_UPDATE").putExtra("DOWNLOAD_REFERENCE_ID", referenceId));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("DOWNLOAD_UI_UPDATE_PLAYER").putExtra("DOWNLOAD_REFERENCE_ID", referenceId));
                            DownloadUtils.downloadNext(context);
                        }
                    });
                }
            }
        } else if (intent.getAction() == DownloadManager.ACTION_NOTIFICATION_CLICKED) {
            context.startActivity(new Intent(context, DownloadedMediaListing.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

  /*  private void showEncryptionStatusToUser(final MediaItem mediaItem) {
        if (mediaItem != null && !TextUtils.isEmpty(mediaItem.getTitle())) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppController.applicationContext, "Saving " + mediaItem.getTitle(), Toast.LENGTH_LONG).show();

                    DownloadTask.generateNotification("Saving " + mediaItem.getTitle(), mediaItem.getTitle());
                }
            });
        }
    }*/

    private void showDownloadStatusToUser(final MediaItem mediaItem) {
        if (mediaItem != null && !TextUtils.isEmpty(mediaItem.getTitle())) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppController.applicationContext, "Download completed " + mediaItem.getTitle(), Toast.LENGTH_LONG).show();

                    DownloadTask.generateNotification("Download Successfully", mediaItem.getTitle());
                }
            });
        }
    }
}
