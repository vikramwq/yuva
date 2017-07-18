package com.multitv.yuv.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.multitv.yuv.models.ChannelsData;
import com.multitv.yuv.models.Interest;
import com.multitv.yuv.models.PlaylistData;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistScreen extends AppCompatActivity {

    private String id;

    private ImageView bannerImg;
    private String TAG = this.getClass().getSimpleName();
    private PlaylistData playListData;
    private RecyclerView playlistRecyclerview, videoRecyclerview;
    private TextView moreVideoButton;
    private Video videoData;
    private ProgressBar main_progress_bar;
    private FloatingActionButton fabButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_screen);
        id = getIntent().getStringExtra("id");
        String banner = getIntent().getStringExtra("banner");
        String channelName = getIntent().getStringExtra("channelName");


        bannerImg = (ImageView) findViewById(R.id.bannerImg);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(PlaylistScreen.this);

        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(channelName);

        Log.d(this.getClass().getName(), "bbanner=======" + banner);
        playlistRecyclerview = (RecyclerView) findViewById(R.id.playlistRecyclerview);
        videoRecyclerview = (RecyclerView) findViewById(R.id.videoRecyclerview);
        fabButton = (FloatingActionButton) findViewById(R.id.fabButton);
        playlistRecyclerview.setVisibility(View.VISIBLE);
        moreVideoButton = (TextView) findViewById(R.id.moreVideoButton);
        main_progress_bar = (ProgressBar) findViewById(R.id.main_progress_bar);
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


        Picasso.with(PlaylistScreen.this)
                .load(banner.replace("\\", ""))
                .into(bannerImg);

        playlistRecyclerview.setHasFixedSize(true);
        playlistRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        videoRecyclerview.setHasFixedSize(true);
        videoRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        getContentData(false);


    }


    private void getContentData(final boolean isLoadMoreRequest) {


        Log.d(this.getClass().getName(), "playlist api====" + ApiRequest.BASE_URL_VERSION_3 + "content/Playlist/token/" + ApiRequest.TOKEN + "?content_partner=" + id);

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

                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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
                playlistRecyclerview.setNestedScrollingEnabled(false);
                playlistRecyclerview.setAdapter(new PlaylistAdapter(PlaylistScreen.this, playListData.getPlayList()));
            }
        });


    }


    private void showDisplayVideoData(final Video videoData, boolean isRefresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                ArrayList<Cat_cntn> arrayList = new ArrayList<Cat_cntn>(6);
                arrayList.addAll(videoData.content);
                videoRecyclerview.setNestedScrollingEnabled(false);
                videoRecyclerview.setAdapter(new VideoPlaylistAdapter(PlaylistScreen.this, arrayList));

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
}
