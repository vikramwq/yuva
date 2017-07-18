package com.multitv.yuv.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.ContentAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentScreen extends AppCompatActivity {

    private String playlist_id, playlist_name;
    private String TAG = this.getClass().getSimpleName();
    private RecyclerView contentList;
    private int count, totalCount = 0;
    private int lastVisibleItem, totalItemCount;
    protected ProgressBar progressBarMain;
    protected ProgressBar bottomProgressBar;
    private boolean isLoading;
    private ContentAdapter adapter;
    private ArrayList<Cat_cntn> contentData;
    private String userID;
    private SharedPreference sharedPreference;
    private Toolbar toolbar;
    private String channelId;
    private Video videoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_screen);
        playlist_id = getIntent().getStringExtra("playlist_id");
        playlist_name = getIntent().getStringExtra("playlist_name");
        channelId = getIntent().getStringExtra("channelId");
        count = getIntent().getIntExtra("count", 0);
        videoData = AppController.getInstance().getContentData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(playlist_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(ContentScreen.this);
        contentList = (RecyclerView) findViewById(R.id.contentList);
        contentList.hasFixedSize();
        contentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        progressBarMain = (ProgressBar) findViewById(R.id.main_progress_bar);
        bottomProgressBar = (ProgressBar) findViewById(R.id.bottom_progress_bar);
        sharedPreference = new SharedPreference();
        userID = sharedPreference.getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);
        contentList.addOnScrollListener(scrollListener);

        if (channelId != null && count > 0) {
            contentData = videoData.content;
            adapter = new ContentAdapter(ContentScreen.this, contentData);
            contentList.setAdapter(adapter);
            progressBarMain.setVisibility(View.GONE);
            bottomProgressBar.setVisibility(View.VISIBLE);
            getContentVideos(true);
        } else {
            progressBarMain.setVisibility(View.VISIBLE);
            bottomProgressBar.setVisibility(View.GONE);
            getContentVideos(false);
        }
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Log.d(this.getClass().getName(), "count======>>>> " + count + " totalCount=======>>>" + totalCount);
            if (count < totalCount) {
                totalItemCount = recyclerView.getAdapter().getItemCount();
                try {
                    lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
                } catch (Exception e) {
                    Tracer.error("Error", "onScrolled: EXCEPTION " + e.getMessage());
                    lastVisibleItem = 0;
                }
                if (!isLoading && totalItemCount == (lastVisibleItem + 1)) {
                    if (bottomProgressBar != null)
                        bottomProgressBar.setVisibility(View.VISIBLE);
                    getContentVideos(true);
                    isLoading = true;


                }

            }

        }

    };

    private void getContentVideos(final boolean isLoadMoreRequest) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + "content/list/token/" + ApiRequest.TOKEN, new Response.Listener<String>() {
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
                                Log.d(TAG, "Api response====>>>> video  " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                count = jsonObject.getInt("offset");
                                totalCount = jsonObject.getInt("totalCount");
                                Video videoData = Json.parse(response.trim(), Video.class);
                                showDisplayPlaylistData(videoData, isLoadMoreRequest);
                                isLoading = false;

                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isLoading = false;
                                    if (progressBarMain != null && progressBarMain.isShown())
                                        progressBarMain.setVisibility(View.GONE);
                                    if (bottomProgressBar != null && bottomProgressBar.isShown())
                                        bottomProgressBar.setVisibility(View.GONE);
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
                isLoading = false;

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("max_counter", "10");
                params.put("user_id", userID);
                if (channelId != null) {
                    params.put(" content_partner_id", channelId);
                } else {
                    params.put("playlist_id", playlist_id);
                }
                params.put("current_offset", String.valueOf(count));
                params.put("max_counter", "10");
                params.put("device", "android");
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    private void showDisplayPlaylistData(final Video videoData, final boolean isLoadMoreRequest) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (videoData == null)
                    return;
                if (!isLoadMoreRequest) {
                    if (videoData.content != null && videoData.content.size() > 0) {
                        contentData = videoData.content;
                        adapter = new ContentAdapter(ContentScreen.this, contentData);
                        contentList.setAdapter(adapter);
                    }
                } else {
                    if (videoData.content != null && videoData.content.size() > 0) {
                        contentData.addAll(videoData.content);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                }
                if (progressBarMain != null && progressBarMain.isShown())
                    progressBarMain.setVisibility(View.GONE);
                if (bottomProgressBar != null && bottomProgressBar.isShown())
                    bottomProgressBar.setVisibility(View.GONE);
                isLoading = false;

            }
        });


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
