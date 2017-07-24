package com.multitv.yuv.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.multitv.yuv.models.recommended.Recommended;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LikedVideosScreen extends AppCompatActivity {

    private RecyclerView likeRecyclerView;
    private ProgressBar mProgress_bar_main;
    private Recommended recommended;
    private TextView emptyTxt;
    private String userID;
    private int typeOfFragment;
    private int count, totalCount = 0;
    private int lastVisibleItem, totalItemCount;
    private ContentAdapter adapter;
    protected ProgressBar bottomProgressBar;
    private boolean isLoading;
    private ArrayList<Cat_cntn> contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_videos_screen);

        likeRecyclerView = (RecyclerView) findViewById(R.id.likeRecyclerView);
        mProgress_bar_main = (ProgressBar) findViewById(R.id.mProgress_bar_main);
        emptyTxt = (TextView) findViewById(R.id.emptyTxt);
        typeOfFragment = getIntent().getIntExtra("typeOfFragment", -1);
        bottomProgressBar = (ProgressBar) findViewById(R.id.bottom_progress_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (typeOfFragment == 0) {
            toolbar.setTitle("Watch history");
        } else {
            toolbar.setTitle("Liked");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(LikedVideosScreen.this);
        SharedPreference sharedPreference = new SharedPreference();
        userID = sharedPreference.getPreferencesString(AppController.getInstance(), "user_id" + "_" + ApiRequest.TOKEN);
        likeRecyclerView.hasFixedSize();
        likeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getHomeCatData(false);


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
                    getHomeCatData(true);
                    isLoading = true;


                }

            }

        }

    };


    private void getHomeCatData(final boolean isRefrshed) {
        if (mProgress_bar_main != null) {
            mProgress_bar_main.setVisibility(View.VISIBLE);
        }
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(this, ApiRequest.LIKES_USER_CONTENT), new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                try {

                    mProgress_bar_main.setVisibility(View.GONE);
                    bottomProgressBar.setVisibility(View.GONE);
                    JSONObject mObj = new JSONObject(response);
                    MultitvCipher mcipher = new MultitvCipher();
                    String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                    Log.d(this.getClass().getName(), "response for Liked======>>>" + str);
                    isLoading = false;
                    try {
                        JSONObject newObj = new JSONObject(str);
                        recommended = Json.parse(newObj.toString(), Recommended.class);
                        Tracer.error("LIKES_LIST", newObj.toString());
                    } catch (JSONException e) {
                        Tracer.error("JSON_ERROR", "LIKES_LIST" + e.getMessage().toString());
                        ExceptionUtils.printStacktrace(e);
                    }

                    if (recommended == null || recommended.content == null || recommended.content.size() == 0) {
                        mProgress_bar_main.setVisibility(View.GONE);
                        likeRecyclerView.setVisibility(View.GONE);
                        bottomProgressBar.setVisibility(View.GONE);
                        emptyTxt.setVisibility(View.VISIBLE);
                    } else {

                        showLikedVideos(isRefrshed, recommended);


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.debug("Error", "Error: " + error.getMessage());

                mProgress_bar_main.setVisibility(View.GONE);
                bottomProgressBar.setVisibility(View.GONE);
                isLoading = false;
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


                params.put("current_version", "0.0");
                params.put("max_counter", "10");
                params.put("device", "android");
                params.put("user_id", userID);
                params.put("current_offset", String.valueOf(count));

                Tracer.error("current_version", "0.0");
                Tracer.error("max_counter", "10");
                Tracer.error("device", "android");
                Tracer.error("user_id", userID);
                Tracer.error("current_offset", String.valueOf(count));

                if (typeOfFragment == 0) {
                    params.put("type", "watching");
                    Tracer.error("***type****", "watching");
                } else if (typeOfFragment == 1) {
                    params.put("type", "favorite");
                    Tracer.error("***type****", "favorite");
                } else if (typeOfFragment == 2) {
                    params.put("type", "liked");
                    Tracer.error("***type****", "liked");
                }

                Set<String> keys = params.keySet();
                for (String key : keys) {
                    Tracer.error("getFaveContent", "getHomeContent().getParams: " + key + "      " + params.get(key));
                }
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }


    private void showLikedVideos(boolean isLoadMoreRequest, Recommended recommended) {


        if (!isLoadMoreRequest) {
            contents = recommended.content;
            if (recommended.content != null && recommended.content.size() > 0) {
                adapter = new ContentAdapter(LikedVideosScreen.this, recommended.content);
                likeRecyclerView.setAdapter(adapter);
            }
        } else {
            if (recommended.content != null && recommended.content.size() > 0) {
                contents.addAll(recommended.content);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        }


        if (mProgress_bar_main != null && mProgress_bar_main.isShown())
            mProgress_bar_main.setVisibility(View.GONE);
        if (bottomProgressBar != null && bottomProgressBar.isShown())
            bottomProgressBar.setVisibility(View.GONE);
        isLoading = false;
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
