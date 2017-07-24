package com.multitv.yuv.activity;

import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.customview.RecyclerItemClickListener;
import com.multitv.yuv.models.video.Video;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.multitv.yuv.adapter.MoreRecommendedAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.home.Home;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;


public class GenreBasedContentScreen extends AppCompatActivity {

    private RecyclerView main_content_recycler;
    private Toolbar toolbar;
    private String TAG = this.getClass().getSimpleName();
    protected ProgressBar progressBarMain;
    private int lastVisibleItem, totalItemCount;
    private Video videoData;
    private List<Cat_cntn> cat_cntns;
    private boolean isLoading;
    private String selectedGenre, genreName;
    private SharedPreference sharedPreference;
    private String userID;
    private MoreRecommendedAdapter adapter;
    private int count, totalCount = 0;
    private boolean isLoggedIn, isOTPVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre_based_content_screen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(GenreBasedContentScreen.this);
        main_content_recycler = (RecyclerView) findViewById(R.id.main_content_recycler);
        progressBarMain = (ProgressBar) findViewById(R.id.main_progress_bar);
        selectedGenre = getIntent().getStringExtra("selectedGenre");
        genreName = getIntent().getStringExtra("genreName");
        getSupportActionBar().setTitle(genreName);
        sharedPreference = new SharedPreference();
        userID = sharedPreference.getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        main_content_recycler.setLayoutManager(gridLayoutManager);
        getContentData(false);

        isLoggedIn = sharedPreference.getPreferenceBoolean(GenreBasedContentScreen.this, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(GenreBasedContentScreen.this, sharedPreference.KEY_IS_OTP_VERIFIED);


        main_content_recycler.addOnScrollListener(scrollListener);


        main_content_recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (isOTPVerified) {
                            if (cat_cntns != null && cat_cntns.size() > 0) {
                                String VIDEO_URL = cat_cntns.get(position).url;
                                try {
                                    if (VIDEO_URL != null && !VIDEO_URL.equals("")) {
                                        Intent videoIntent = new Intent(GenreBasedContentScreen.this, MultiTvPlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, cat_cntns.get(position));
                                        videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");

                                        startActivity(videoIntent);
                                    } else {
                                        Toast.makeText(GenreBasedContentScreen.this, "No Record Found", Toast.LENGTH_LONG).show();
                                    }


                                } catch (Exception ex) {
                                    Tracer.error("error", ex.getMessage());
                                }

                            }

                        } else {
                            Utilities.showLoginDailog(GenreBasedContentScreen.this);
                        }

                    }
                })
        );


    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (count < totalCount) {


                totalItemCount = recyclerView.getAdapter().getItemCount();

                try {
                    lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
                } catch (Exception e) {
                    Tracer.error("Error", "onScrolled: EXCEPTION " + e.getMessage());
                    lastVisibleItem = 0;
                }

                if (!isLoading && totalItemCount == (lastVisibleItem + 1)) {


                    if (progressBarMain != null)
                        progressBarMain.setVisibility(View.VISIBLE);
                    getContentData(true);
                    isLoading = true;


                }

            }

        }
    };

    private void showDisplayPlaylistData(Video video, boolean isLoadMoreRequest) {
        if (videoData == null)
            return;
        if (!isLoadMoreRequest) {
            if (videoData.feature != null && videoData.feature.size() > 0) {
                cat_cntns = videoData.content;
                adapter = new MoreRecommendedAdapter(GenreBasedContentScreen.this, videoData.content, main_content_recycler, true);
                main_content_recycler.setAdapter(adapter);
            }
        } else {
            cat_cntns.addAll(video.content);
        }


        if (progressBarMain != null && progressBarMain.isShown())
            progressBarMain.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        isLoading = false;

    }


    private void getContentData(final boolean isLoadMoreRequest) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(GenreBasedContentScreen.this, ApiRequest.VIDEO_CAT_URL_CLIST), new Response.Listener<String>() {
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
                                Log.d(TAG, "response for genre id===" + response);

                                JSONObject jsonObject = new JSONObject(response);
                                count = jsonObject.getInt("offset");
                                totalCount = jsonObject.getInt("totalCount");

                                Log.d(this.getClass().getName(), "count======" + count);
                                videoData = Json.parse(response.trim(), Video.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        showDisplayPlaylistData(videoData, isLoadMoreRequest);

                                    }
                                });
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isLoading = false;
                                    if (progressBarMain != null && progressBarMain.isShown())
                                        progressBarMain.setVisibility(View.GONE);
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
                if (progressBarMain != null && progressBarMain.isShown())
                    progressBarMain.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("genre_id", selectedGenre);
                params.put("device", "android");
                params.put("user_id", userID);
                params.put("current_offset", String.valueOf(count));
                params.put("max_counter", "10");
                params.put("cat_type", "video");

                Log.d("Params:", "current_offset===" + String.valueOf(count) + "user_id===" + userID + " selectedGenre" + selectedGenre + " lan==" + LocaleHelper.getLanguage(getApplicationContext()) + "m_filter==" + (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));


                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    @Override
    public void onBackPressed() {


        if (GenreList.getInstance() != null) {
            ((GenreList) GenreList.getInstance())
                    .closeActivity();
        }

        super.onBackPressed();


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
