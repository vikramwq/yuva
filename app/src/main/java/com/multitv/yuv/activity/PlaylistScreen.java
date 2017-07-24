package com.multitv.yuv.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.ContentAdapter;
import com.multitv.yuv.adapter.PlaylistAdapter;
import com.multitv.yuv.adapter.VideoPlaylistAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.controller.ContentController;
import com.multitv.yuv.models.Channel;
import com.multitv.yuv.models.ChannelsData;
import com.multitv.yuv.models.Interest;
import com.multitv.yuv.models.PlaylistData;
import com.multitv.yuv.models.PlaylistItem;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistScreen extends AppCompatActivity {

    private String id;

    private ImageView bannerImg;
    private String TAG = this.getClass().getSimpleName();
    private PlaylistData playListData;
    private RecyclerView playlistRecyclerview, videoRecyclerview;
    private TextView moreVideoButton, subscribedCount, titleTxt, morePlayListButton;
    private Video videoData;
    private ProgressBar main_progress_bar;
    private FloatingActionButton fabButton;
    private Toolbar toolbar;
    private Channel channel;
    private ImageView subscribeLayout;
    private ImageView notificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_screen);

        channel = (Channel) getIntent().getSerializableExtra("channel");
        String baseUrl = getIntent().getStringExtra("baseUrl");
        String profileBaseURL = getIntent().getStringExtra("profileBaseUrl");

        id = channel.getId();


        bannerImg = (ImageView) findViewById(R.id.bannerImg);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        subscribeLayout = (ImageView) findViewById(R.id.subscribeLayout);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("" + channel.getFirst_name());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(PlaylistScreen.this);
        Log.d(this.getClass().getName(), "bbanner=======" + baseUrl + channel.getBanner_image());

        playlistRecyclerview = (RecyclerView) findViewById(R.id.playlistRecyclerview);
        videoRecyclerview = (RecyclerView) findViewById(R.id.videoRecyclerview);
        fabButton = (FloatingActionButton) findViewById(R.id.fabButton);
        subscribedCount = (TextView) findViewById(R.id.subscribedCount);
        playlistRecyclerview.setVisibility(View.VISIBLE);
        moreVideoButton = (TextView) findViewById(R.id.moreVideoButton);
        main_progress_bar = (ProgressBar) findViewById(R.id.main_progress_bar);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        morePlayListButton = (TextView) findViewById(R.id.morePlayListButton);
        notificationBtn = (ImageView) findViewById(R.id.notificationBtn);
        moreVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistScreen.this, ContentScreen.class);
                intent.putExtra("channelId", id);
                intent.putExtra("count", videoData.content.size());
                AppController.getInstance().setContentData(videoData);
                startActivity(intent);
            }
        });


        setFabButton(profileBaseURL + channel.getPrfile_pic());

        subscribedCount.setText(channel.getTotal_subscribers() + " " + getResources().getString(R.string.subscribers));

        Picasso.with(PlaylistScreen.this)
                .load((baseUrl + channel.getBanner_image()).replace("\\", ""))
                .into(bannerImg);

        playlistRecyclerview.setHasFixedSize(true);
        playlistRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        videoRecyclerview.setHasFixedSize(true);
        videoRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        titleTxt.setText(channel.getFirst_name() + " " + channel.getLast_name());
        getContentData(false);

        subscribeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentController.getInstance().subscribeChannel(id, channel.getIs_subscriber());
                if (channel.getIs_subscriber().equals("0")) {
                    channel.setIs_subscriber("1");
                    subscribeLayout.setImageResource(R.mipmap.ic_subsd_diable);
                    notificationBtn.setVisibility(View.VISIBLE);
                    notificationBtn.setImageResource(R.mipmap.ic_notification);
                } else {
                    channel.setIs_subscriber("0");
                    subscribeLayout.setImageResource(R.mipmap.ic_subscription);
                    notificationBtn.setVisibility(View.GONE);
                }
            }
        });

        if (channel.getIs_subscriber().equals("1")) {
            subscribeLayout.setImageResource(R.mipmap.ic_subsd_diable);
            notificationBtn.setVisibility(View.VISIBLE);
        } else {
            subscribeLayout.setImageResource(R.mipmap.ic_subscription);
            notificationBtn.setVisibility(View.GONE);
        }


        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("1".equals(channel.getNotification())) {
                    channel.setNotification("0");
                    notificationBtn.setImageResource(R.mipmap.ic_notification_disabled);
                    ContentController.getInstance().doNotificationTask(channel.getId(), "2");
                } else {
                    channel.setNotification("1");
                    notificationBtn.setImageResource(R.mipmap.ic_notification);
                    ContentController.getInstance().doNotificationTask(channel.getId(), "1");
                }

            }
        });

    }


    private void getContentData(final boolean isLoadMoreRequest) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                ApiRequest.BASE_URL_VERSION_3 + "content/Playlist/token/" + ApiRequest.TOKEN + "?content_partner=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String response = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                Log.d(TAG, "Api response====>>>> channel  " + response);
                                Gson gson = new Gson();
                                playListData = gson.fromJson(response.trim(), PlaylistData.class);
                                showDisplayPlaylistData(playListData, isLoadMoreRequest);
                                getContentVideos();

                            } else {
                                getContentVideos();
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getContentVideos();

//                                    isLoading = false;
//                                    if (progressBarMain != null && progressBarMain.isShown())
//                                        progressBarMain.setVisibility(View.GONE);
//                                    if (bottomProgressBar != null && bottomProgressBar.isShown())
//                                        bottomProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("HomeFragment", "Error: " + error.getMessage());
//                isLoading = false;

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    private void showDisplayPlaylistData(final PlaylistData playListData, boolean isRefresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                List<PlaylistItem> playlistItems = playListData.getPlayList();

                if (playlistItems != null && playlistItems.size() > 6) {
                    List<PlaylistItem> playlListItemList = playlistItems.subList(0, 5);

                    morePlayListButton.setVisibility(View.VISIBLE);
                    playlistRecyclerview.setNestedScrollingEnabled(false);
                    playlistRecyclerview.setAdapter(new PlaylistAdapter(PlaylistScreen.this, playlListItemList));
                } else {
                    morePlayListButton.setVisibility(View.GONE);
                    playlistRecyclerview.setNestedScrollingEnabled(false);
                    playlistRecyclerview.setAdapter(new PlaylistAdapter(PlaylistScreen.this, playListData.getPlayList()));
                }


            }
        });


    }


    private void showDisplayVideoData(final Video videoData, boolean isRefresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (videoData != null && videoData.content.size() > 0) {
                    if (videoData.content.size() > 6) {
                        moreVideoButton.setVisibility(View.VISIBLE);
                        videoRecyclerview.setNestedScrollingEnabled(false);
                        videoRecyclerview.setAdapter(new VideoPlaylistAdapter(PlaylistScreen.this, videoData.content.subList(0, 5)));
                    } else {
                        moreVideoButton.setVisibility(View.GONE);
                        videoRecyclerview.setNestedScrollingEnabled(false);
                        videoRecyclerview.setAdapter(new VideoPlaylistAdapter(PlaylistScreen.this, videoData.content));
                    }
                }
            }
        });


    }


    private void getContentVideos() {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + "content/list/token/" + ApiRequest.TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (main_progress_bar != null && main_progress_bar.isShown())
                                        main_progress_bar.setVisibility(View.GONE);
                                }
                            });

                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String response = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                Log.d(TAG, "Api response====>>>> video  " + response);
                                videoData = Json.parse(response.trim(), Video.class);
                                showDisplayVideoData(videoData, false);
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (main_progress_bar != null && main_progress_bar.isShown())
                                                main_progress_bar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("HomeFragment", "Error: " + error.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (main_progress_bar != null && main_progress_bar.isShown())
                            main_progress_bar.setVisibility(View.GONE);
                    }
                });
//                isLoading = false;

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


                params.put("content_partner_id", id);
                params.put("max_counter", "10");
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

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

    private void setFabButton(String imageUrl) {

        int widthAndHeightOfIcon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                fabButton.setImageBitmap(bitmap);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
        };

        Picasso.with(this)
                .load(imageUrl)
                .resize(widthAndHeightOfIcon, widthAndHeightOfIcon)

                .into(target);


    }
}
