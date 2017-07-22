package com.multitv.yuv.db;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.models.PersistenceDataItem;
import com.multitv.yuv.utilities.Constant;

import java.util.ArrayList;
import java.util.List;

public class MediaDbHelper extends SQLiteOpenHelper {

    private SharedPreferences sharedpreferences;
    private static final String TAG = "ChatDBHelper";
    private static MediaDbHelper instance = null;

    public static MediaDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MediaDbHelper(context.getApplicationContext());
        }
        // Log.d("MediaDbHelper", "instance===== " + instance);

        return instance;
    }

    public MediaDbHelper(Context context) {
        super(context, Constant.DATABASE, null, Constant.DATABASE_VERSION);
    }

    public void dropAllTables() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.execSQL("DROP TABLE " + Constant.TABLE_MEDIA + ";");
            db.execSQL("DROP TABLE " + Constant.TABLE_PERSIST_PLAYBACK + ";");

            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                try {
                    // ////db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE " + Constant.TABLE_MEDIA + " ("
                + Constant.MEDIA_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constant.MEDIA_NAME + " TEXT,"
                + Constant.MEDIA_TITLE + " TEXT,"
                + Constant.MEDIA_TYPE + " TEXT,"
                + Constant.MEDIA_PATH + " TEXT,"
                + Constant.MEDIA_DURATION + " INTEGER,"
                + Constant.MEDIA_SIZE + " TEXT, "
                + Constant.MEDIA_THUMB + " TEXT, "
                + Constant.MEDIA_DESC + " TEXT, "
                + Constant.CONTENT_ID + " TEXT , "
                + Constant.TIME_STAMP + " LONG, "
                + Constant.MEDIA_EXPIRY + " INTEGER, "
                + Constant.DOWNLOAD_REFERENCE_ID + " LONG, "
                + Constant.IS_DOWNLOADED + " INTEGER, "
                + Constant.DOWNLOAD_STATUS + " INTEGER, "
                + Constant.DOWNLOAD_PATH + " TEXT , "
                + Constant.MEDIA_VALIDITY + " TEXT);"
        );


        db.execSQL("CREATE TABLE " + Constant.TABLE_PERSIST_PLAYBACK + " ("
                + Constant.TABLE_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constant.TABLE_FIELD_DATA + " TEXT,"
                + Constant.TABLE_FILED_CONTENT_ID + " TEXT ,"
                + Constant.TABLE_FEILD_DURATION + " LONG);"
        );


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        int version = oldVersion;
        if (newVersion > oldVersion) {


        }
    }


    public void addDataForPersistencePlayback(String contentId, String data, long duration) {


        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();


            ContentValues values = new ContentValues();


            if (checkAlreadyPersistnece(contentId) > 0) {
                values.put(Constant.TABLE_FIELD_DATA, data);
                values.put(Constant.TABLE_FEILD_DURATION, duration);

                int a = db.update(Constant.TABLE_PERSIST_PLAYBACK, values, Constant.TABLE_FILED_CONTENT_ID + " = ?",
                        new String[]{contentId});

//                Log.d(this.getClass().getName(), "data of video to update==" + data);
            } else {

                values.put(Constant.TABLE_FIELD_DATA, data);
                values.put(Constant.TABLE_FEILD_DURATION, duration);
                values.put(Constant.TABLE_FILED_CONTENT_ID, contentId);

//                Log.d(this.getClass().getName(), "data of video to insert==" + data);

                Long a = db.insert(Constant.TABLE_PERSIST_PLAYBACK, null, values);

                Log.d(TAG, "row affected for persistence for new --> " + a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private int checkAlreadyPersistnece(String contentId) {

        int number = 0;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(
                    "select content_id from persist_table where content_id = ?",
                    new String[]{String.valueOf(contentId)});

            if (cursor.getCount() != 0)
                number = cursor.getCount();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }


    public List<PersistenceDataItem> getPersistenceMedia() {

        List<PersistenceDataItem> persistenceDataItems = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        String table = Constant.TABLE_PERSIST_PLAYBACK;

        final String[] columns = {Constant.TABLE_ID, // 0
                Constant.TABLE_FIELD_DATA, // 1
                Constant.TABLE_FEILD_DURATION, // 2
                Constant.TABLE_FILED_CONTENT_ID, // 3

        };

        final String[] selectionArgs = null;
        final String groupBy = null;
        final String having = null;
        String whereClause = null;
        final String orderBy = Constant.TABLE_ID + " DESC";

        String limitnum = "10";


        Cursor c = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy, limitnum);

        Log.d(this.getClass().getName(), "Cursor size=====" + c.getCount());

        while (c.moveToNext()) {
            PersistenceDataItem persistenceDataItem = new PersistenceDataItem();
            persistenceDataItem.setData(c.getString(1));
            persistenceDataItem.setDuration(c.getLong(2));
            persistenceDataItem.setContentId(c.getString(3));

            persistenceDataItems.add(persistenceDataItem);
        }

        return persistenceDataItems;

    }


    public void addMedia(MediaItem mediaItem) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(Constant.MEDIA_DESC, mediaItem.getDesc());
            values.put(Constant.MEDIA_NAME, mediaItem.getName());
            values.put(Constant.MEDIA_TITLE, mediaItem.getTitle());
            values.put(Constant.MEDIA_PATH, mediaItem.getPath());
            values.put(Constant.MEDIA_DURATION, mediaItem.getDuration());
            values.put(Constant.MEDIA_VALIDITY, mediaItem.getValidity());
            values.put(Constant.MEDIA_TYPE, mediaItem.getType());
            values.put(Constant.CONTENT_ID, mediaItem.getContentId());
            values.put(Constant.TIME_STAMP, mediaItem.getTimeStamp());
            values.put(Constant.DOWNLOAD_REFERENCE_ID, mediaItem.getDownloadReferenceId());
            values.put(Constant.DOWNLOAD_STATUS, mediaItem.getDownloadStatus());
            values.put(Constant.DOWNLOAD_PATH, mediaItem.getDownloadPath());
            values.put(Constant.MEDIA_EXPIRY, mediaItem.getExpiry());
            values.put(Constant.IS_DOWNLOADED, mediaItem.isDownloaded() ? 1 : 0);

            Log.d(this.getClass().getName(), "description of video to insert==" + mediaItem.getDesc());

            Long a = db.insert(Constant.TABLE_MEDIA, null, values);

            Log.d(TAG, "row affected for media for new --> " + a);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    // //db.close();
                } catch (Exception e) {
                }
            }
        }
    }


    public List<MediaItem> getDownloadedMedia() {

        List<MediaItem> mediaItemList = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        String table = Constant.TABLE_MEDIA;

        final String[] columns = {Constant.MEDIA_ID, // 0
                Constant.MEDIA_TYPE, // 1
                Constant.MEDIA_NAME, // 2
                Constant.MEDIA_SIZE, // 3
                Constant.MEDIA_DURATION, // 4
                Constant.MEDIA_VALIDITY, // 5
                Constant.MEDIA_PATH, // 6
                Constant.MEDIA_DESC,// 7
                Constant.MEDIA_THUMB,// 8
                Constant.CONTENT_ID,// 9
                Constant.TIME_STAMP,// 10
                Constant.DOWNLOAD_REFERENCE_ID,// 11
                Constant.IS_DOWNLOADED,// 12
                Constant.DOWNLOAD_STATUS,// 13
                Constant.MEDIA_TITLE,// 14
                Constant.DOWNLOAD_PATH,// 15
                Constant.MEDIA_EXPIRY //16
        };

        final String[] selectionArgs = new String[]{String.valueOf(1), String.valueOf(DownloadManager.STATUS_PENDING)};
        ;
        final String groupBy = null;
        final String having = null;
        String whereClause = Constant.IS_DOWNLOADED + " = ?" + " OR " + Constant.DOWNLOAD_STATUS + " = ?";
        final String orderBy = null;

        String limitnum = "1000";


        Cursor c = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy, limitnum);

        Log.d(this.getClass().getName(), "Cursor size=====" + c.getCount());

        while (c.moveToNext()) {
            MediaItem mediaItem = new MediaItem();
            mediaItem.setMediaId(c.getString(0));
            mediaItem.setType(c.getString(1));
            mediaItem.setName(c.getString(2));
            mediaItem.setDuration(c.getString(3));
            mediaItem.setPath(c.getString(6));
            mediaItem.setDesc(c.getString(7));
            mediaItem.setThumbnail(c.getString(8));
            mediaItem.setContentId(c.getString(9));
            mediaItem.setTimeStamp(c.getLong(10));
            mediaItem.setDownloadReferenceId(c.getLong(11));
            mediaItem.setDownloaded(c.getInt(12) == 1 ? true : false);
            mediaItem.setDownloadStatus(c.getInt(13));
            mediaItem.setTitle(c.getString(14));
            mediaItem.setDownloadPath(c.getString(15));
            mediaItem.setExpiry(c.getInt(15));
            mediaItemList.add(mediaItem);
        }

        return mediaItemList;

    }

    public List<MediaItem> getPendingDownloads() {

        List<MediaItem> mediaItemList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String table = Constant.TABLE_MEDIA;

        final String[] columns = {Constant.MEDIA_ID, // 0
                Constant.MEDIA_TYPE, // 1
                Constant.MEDIA_NAME, // 2
                Constant.MEDIA_SIZE, // 3
                Constant.MEDIA_DURATION, // 4
                Constant.MEDIA_VALIDITY, // 5
                Constant.MEDIA_PATH, // 6
                Constant.MEDIA_DESC,// 7
                Constant.MEDIA_THUMB,// 8
                Constant.CONTENT_ID,// 9
                Constant.TIME_STAMP,// 10
                Constant.DOWNLOAD_REFERENCE_ID,// 11
                Constant.IS_DOWNLOADED,// 12
                Constant.DOWNLOAD_STATUS,// 13
                Constant.MEDIA_TITLE,// 14
                Constant.DOWNLOAD_PATH// 15
        };

        final String[] selectionArgs = new String[]{String.valueOf(DownloadManager.STATUS_PENDING)};

        final String groupBy = null;
        final String having = null;
        String whereClause = Constant.DOWNLOAD_STATUS + " = ?";
        final String orderBy = Constant.TIME_STAMP + " ASC";

        String limitnum = "1000";


        Cursor c = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy, limitnum);

        Log.d(this.getClass().getName(), "Cursor size=====" + c.getCount());

        while (c.moveToNext()) {
            MediaItem mediaItem = new MediaItem();
            mediaItem.setMediaId(c.getString(0));
            mediaItem.setType(c.getString(1));
            mediaItem.setName(c.getString(2));
            mediaItem.setDuration(c.getString(3));
            mediaItem.setPath(c.getString(6));
            mediaItem.setDesc(c.getString(7));
            mediaItem.setThumbnail(c.getString(8));
            mediaItem.setContentId(c.getString(9));
            mediaItem.setTimeStamp(c.getLong(10));
            mediaItem.setDownloadReferenceId(c.getLong(11));
            mediaItem.setDownloaded(c.getInt(12) == 1 ? true : false);
            mediaItem.setDownloadStatus(c.getInt(13));
            mediaItem.setTitle(c.getString(14));
            mediaItem.setDownloadPath(c.getString(15));
            mediaItemList.add(mediaItem);
        }

        return mediaItemList;

    }


    public MediaItem getMediaItem(long downloadReferenceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table = Constant.TABLE_MEDIA;

        final String[] columns = {Constant.MEDIA_ID, // 0
                Constant.MEDIA_TYPE, // 1
                Constant.MEDIA_NAME, // 2
                Constant.MEDIA_SIZE, // 3
                Constant.MEDIA_DURATION, // 4
                Constant.MEDIA_VALIDITY, // 5
                Constant.MEDIA_PATH, // 6
                Constant.MEDIA_DESC,// 7
                Constant.MEDIA_THUMB,// 8
                Constant.CONTENT_ID,// 9
                Constant.TIME_STAMP,// 10
                Constant.DOWNLOAD_REFERENCE_ID,// 11
                Constant.IS_DOWNLOADED,// 12
                Constant.DOWNLOAD_STATUS,//13
                Constant.MEDIA_TITLE,// 14
                Constant.DOWNLOAD_PATH// 15
        };

        final String[] selectionArgs = new String[]{String.valueOf(downloadReferenceId)};
        final String groupBy = null;
        final String having = null;
        String whereClause = Constant.DOWNLOAD_REFERENCE_ID + " = ?";
        final String orderBy = null;

        String limitnum = "1000";


        Cursor c = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy, limitnum);

        Log.d(this.getClass().getName(), "Cursor size=====" + c.getCount());

        MediaItem mediaItem = null;
        while (c.moveToNext()) {
            mediaItem = new MediaItem();
            mediaItem.setMediaId(c.getString(0));
            mediaItem.setType(c.getString(1));
            mediaItem.setName(c.getString(2));
            mediaItem.setDuration(c.getString(3));
            mediaItem.setPath(c.getString(6));
            mediaItem.setDesc(c.getString(7));
            mediaItem.setThumbnail(c.getString(8));
            mediaItem.setContentId(c.getString(9));
            mediaItem.setTimeStamp(c.getLong(10));
            mediaItem.setDownloadReferenceId(c.getLong(11));
            mediaItem.setDownloaded(c.getInt(12) == 1 ? true : false);
            mediaItem.setDownloadStatus(c.getInt(13));
            mediaItem.setTitle(c.getString(14));
            mediaItem.setDownloadPath(c.getString(15));
        }

        return mediaItem;
    }

    public MediaItem getMediaItem(String contentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table = Constant.TABLE_MEDIA;

        final String[] columns = {Constant.MEDIA_ID, // 0
                Constant.MEDIA_TYPE, // 1
                Constant.MEDIA_NAME, // 2
                Constant.MEDIA_SIZE, // 3
                Constant.MEDIA_DURATION, // 4
                Constant.MEDIA_VALIDITY, // 5
                Constant.MEDIA_PATH, // 6
                Constant.MEDIA_DESC,// 7
                Constant.MEDIA_THUMB,// 8
                Constant.CONTENT_ID,// 9
                Constant.TIME_STAMP,// 10
                Constant.DOWNLOAD_REFERENCE_ID,// 11
                Constant.IS_DOWNLOADED,// 12
                Constant.DOWNLOAD_STATUS,//13
                Constant.MEDIA_TITLE,// 14
                Constant.DOWNLOAD_PATH// 15
        };

        final String[] selectionArgs = new String[]{String.valueOf(contentId)};
        final String groupBy = null;
        final String having = null;
        String whereClause = Constant.CONTENT_ID + " = ?";
        final String orderBy = null;

        String limitnum = "1000";


        Cursor c = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy, limitnum);

        Log.d(this.getClass().getName(), "Cursor size=====" + c.getCount());

        MediaItem mediaItem = null;
        while (c.moveToNext()) {
            mediaItem = new MediaItem();
            mediaItem.setMediaId(c.getString(0));
            mediaItem.setType(c.getString(1));
            mediaItem.setName(c.getString(2));
            mediaItem.setDuration(c.getString(3));
            mediaItem.setPath(c.getString(6));
            mediaItem.setDesc(c.getString(7));
            mediaItem.setThumbnail(c.getString(8));
            mediaItem.setContentId(c.getString(9));
            mediaItem.setTimeStamp(c.getLong(10));
            mediaItem.setDownloadReferenceId(c.getLong(11));
            mediaItem.setDownloaded(c.getInt(12) == 1 ? true : false);
            mediaItem.setDownloadStatus(c.getInt(13));
            mediaItem.setTitle(c.getString(14));
            mediaItem.setDownloadPath(c.getString(15));
        }

        return mediaItem;
    }

    public void updateMediaItem(long downloadReferenceId, String encryptedFilePath) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.MEDIA_PATH, encryptedFilePath);
        values.put(Constant.IS_DOWNLOADED, 1);
        values.put(Constant.DOWNLOAD_STATUS, DownloadManager.STATUS_SUCCESSFUL);

        int update = db.update(Constant.TABLE_MEDIA, values, Constant.DOWNLOAD_REFERENCE_ID + " = ?",
                new String[]{String.valueOf(downloadReferenceId)});
    }

    public void updateMediaItem(String contentId, long downloadReferenceId, int downloadStatus) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.DOWNLOAD_REFERENCE_ID, downloadReferenceId);
        values.put(Constant.DOWNLOAD_STATUS, downloadStatus);

        int update = db.update(Constant.TABLE_MEDIA, values, Constant.CONTENT_ID + " = ?",
                new String[]{String.valueOf(contentId)});
    }

    public void updateMediaItemError(long downloadReferenceId) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.IS_DOWNLOADED, 0);
        values.put(Constant.DOWNLOAD_STATUS, DownloadManager.STATUS_FAILED);

        int update = db.update(Constant.TABLE_MEDIA, values, Constant.DOWNLOAD_REFERENCE_ID + " = ?",
                new String[]{String.valueOf(downloadReferenceId)});
    }

    public void removeMediaItem(String contentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        int delete = db.delete(Constant.TABLE_MEDIA, Constant.CONTENT_ID + " = ?",
                new String[]{String.valueOf(contentId)});
    }


    public int checkExistingContent(String contentId) {
        int number = 0;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "select content_id from media_table where content_id = ? AND is_downloaded = ?",
                    new String[]{String.valueOf(contentId), String.valueOf(1)});

            if (cursor.getCount() != 0)
                number = cursor.getCount();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return number;
    }


}
