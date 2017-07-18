package com.multitv.yuv.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.multitv.yuv.models.MediaItem;

import java.util.List;

import com.multitv.yuv.models.PersistenceDataItem;

public class MediaDbConnector {

    private static MediaDbHelper dbHelper = null;

    public MediaDbConnector(Context context) {
        dbHelper = MediaDbHelper.getInstance(context.getApplicationContext());
    }

    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }


    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public void dropAllTables() {
        dbHelper.dropAllTables();
    }


    public void addMedia(MediaItem mediaItem) {
        dbHelper.addMedia(mediaItem);
    }

    public List<MediaItem> getDownloadedMedia() {
        return dbHelper.getDownloadedMedia();
    }

    public List<MediaItem> getPendingDownloads() {
        return dbHelper.getPendingDownloads();
    }

    public MediaItem getMediaItem(long referenceId) {
        return dbHelper.getMediaItem(referenceId);
    }

    public MediaItem getMediaItem(String contentID) {
        return dbHelper.getMediaItem(contentID);
    }

    public void updateMediaItem(long referenceId, String encryptedFilePath) {
        dbHelper.updateMediaItem(referenceId, encryptedFilePath);
    }

    public void updateMediaItem(String contentId, long referenceId, int downloadStatus) {
        dbHelper.updateMediaItem(contentId, referenceId, downloadStatus);
    }

    public void updateMediaItemError(long referenceId) {
        dbHelper.updateMediaItemError(referenceId);
    }

    public void removeMediaItem(String contentId) {
        dbHelper.removeMediaItem(contentId);
    }

    public void addDataForPersistencePlayback(String contentId, String data, long duration) {
        dbHelper.addDataForPersistencePlayback(contentId, data, duration);
    }

    public List<PersistenceDataItem> getPersistenceMedia() {
        return dbHelper.getPersistenceMedia();
    }

    public int checkExistingContent(String contentId) {
        return dbHelper.checkExistingContent(contentId);
    }


}
