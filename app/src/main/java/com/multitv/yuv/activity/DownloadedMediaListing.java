package com.multitv.yuv.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.models.MediaItem;

import java.util.List;

import com.multitv.yuv.adapter.DownloadedMediaAdapter;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.download.DownloadUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.Utilities;

public class DownloadedMediaListing extends AppCompatActivity implements NotificationCenter.NotificationCenterDelegate {

    private RecyclerView downloaded_recycler;
    private DownloadedMediaAdapter mainAdapter;

    private MediaDbConnector mediaDbConnector;
    private List<MediaItem> mediaItems;
    private Toolbar toolbar;
    private TextView empty_view;
    public LinearLayout progressLayout;
    private CoordinatorLayout coordinator_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_media_listing);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.receivedNetwork);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Downloaded Media");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(DownloadedMediaListing.this);
        empty_view = (TextView) findViewById(R.id.empty_view);
        downloaded_recycler = (RecyclerView) findViewById(R.id.downloaded_recycler);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);

        mediaDbConnector = new MediaDbConnector(DownloadedMediaListing.this);
        mediaItems = mediaDbConnector.getDownloadedMedia();
        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        createDownloadManagerList();

//        Utilities.saveDatabase();

        if (mediaItems != null && mediaItems.size() > 0) {


            empty_view.setVisibility(View.GONE);
            mainAdapter = new DownloadedMediaAdapter(DownloadedMediaListing.this, mediaItems);
            downloaded_recycler.setHasFixedSize(true);
            downloaded_recycler.setLayoutManager(new LinearLayoutManager(DownloadedMediaListing.this, LinearLayoutManager.VERTICAL, false));
            downloaded_recycler.setAdapter(mainAdapter);

        } else {
            empty_view.setVisibility(View.VISIBLE);
        }

        if (!ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
            Snackbar.make(coordinator_layout, "Offline Mode",
                    Snackbar.LENGTH_INDEFINITE).show();
        }


    }

    private void createDownloadManagerList() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        while (cursor.moveToNext()) {
            long referenceId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            MediaItem mediaItem = mediaDbConnector.getMediaItem(referenceId);
            if (mediaItem != null) {
                mediaItem.setDownloadStatus(downloadStatus);
                mediaItems.add(mediaItem);
            }
        }

        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(uiUpdateReceiver,
                new IntentFilter("DOWNLOAD_UI_UPDATE"));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(uiUpdateReceiver);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mediaItems = mediaDbConnector.getDownloadedMedia();

        createDownloadManagerList();

//        Utilities.saveDatabase();

        if (mediaItems != null && mediaItems.size() > 0) {


            empty_view.setVisibility(View.GONE);
            mainAdapter = new DownloadedMediaAdapter(DownloadedMediaListing.this, mediaItems);
            downloaded_recycler.setHasFixedSize(true);
            downloaded_recycler.setLayoutManager(new LinearLayoutManager(DownloadedMediaListing.this, LinearLayoutManager.VERTICAL, false));
            downloaded_recycler.setAdapter(mainAdapter);

        } else {
            empty_view.setVisibility(View.VISIBLE);
        }


    }

    private BroadcastReceiver uiUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mainAdapter != null && mediaDbConnector != null && mediaItems != null && !mediaItems.isEmpty() && intent != null) {
                long downloadReferenceId = intent.getLongExtra("DOWNLOAD_REFERENCE_ID", -1);
                if (downloadReferenceId != -1) {
                    MediaItem mediaItem = mediaDbConnector.getMediaItem(downloadReferenceId);
                    if (mediaItem != null) {
                        int position;
                        for (position = 0; position < mediaItems.size(); position++) {
                            if (mediaItems.get(position).getDownloadReferenceId() == downloadReferenceId ||
                                    (!TextUtils.isEmpty(mediaItem.getContentId())
                                            && !TextUtils.isEmpty(mediaItems.get(position).getContentId()) &&
                                            mediaItems.get(position).getContentId().equalsIgnoreCase(mediaItem.getContentId()))) {
                                break;
                            }
                        }
                        if (position < mediaItems.size())
                            mediaItems.set(position, mediaItem);
                        else
                            mediaItems.add(mediaItem);

                        if (mediaItem.getDownloadStatus() == DownloadManager.STATUS_FAILED)
                            DownloadUtils.removeDownload(context, mediaItem);
                        mainAdapter.notifyItemChanged(position);
                    }
                }
            }
        }
    };

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.receivedNetwork) {
            if (1 == (int) args[0]) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Snackbar.make(coordinator_layout, "Connection established",
                                Snackbar.LENGTH_INDEFINITE).setAction("Go online", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (HomeActivity.getInstance() != null) {
                                    ((HomeActivity) HomeActivity.getInstance())
                                            .closeActivity();
                                }

                                Intent intent = new Intent(DownloadedMediaListing.this, HomeActivity.class);
                                startActivity(intent);

                                finish();

                            }
                        }).show();

                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Snackbar.make(coordinator_layout, "Offline Mode",
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                });

            }

        }

    }
}
