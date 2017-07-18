package com.multitv.yuv.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.GenreAdapter;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Utilities;
import com.google.gson.Gson;


import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;


public class GenreList extends AppCompatActivity {


    RecyclerView genreList;
    private Toolbar toolbar;
    private ProgressBar main_progress_bar;

    private static Context context;

    public static Context getInstance() {
        return context;
    }


    public void closeActivity() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        toolbar.setTitle("Genres");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(GenreList.this);

        genreList = (RecyclerView) findViewById(R.id.genreList);
        main_progress_bar = (ProgressBar) findViewById(R.id.main_progress_bar);


        genreList.setLayoutManager(new GridLayoutManager(GenreList.this, 3));

        getGenreList();
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

    public void getGenreList() {
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.GENRE_LIST_URL), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                main_progress_bar.setVisibility(View.GONE);
                if (response != null) {
                    try {
                        Gson gson = new Gson();
                        com.multitv.yuv.models.genre.Response response1 = gson.fromJson(response, com.multitv.yuv.models.genre.Response.class);
                        if (1 == response1.getCode()) {

                            GenreAdapter adapter = new GenreAdapter(GenreList.this, response1.getResult());
                            genreList.setAdapter(adapter);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                main_progress_bar.setVisibility(View.GONE);
                Log.e("Error", "Error: " + error.getMessage());

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


}
