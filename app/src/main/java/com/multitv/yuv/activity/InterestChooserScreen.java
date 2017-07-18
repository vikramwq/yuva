package com.multitv.yuv.activity;

import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.multitv.yuv.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.models.Interest;
import com.multitv.yuv.models.InterestData;
import com.multitv.yuv.utilities.Json;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Lenovo on 17-07-2017.
 */

public class InterestChooserScreen extends AppCompatActivity {
    String bubbleListUrl = "http://api.multitvsolution.com/automatorapi/v3/interest/list/token/59689749397fb";
    private ProgressBar progress_bar_bottom_home;
    private List<Interest> interestsArraylist = new ArrayList<>();
    private BubblePicker bubblePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_chooser_screen);
        bubblePicker = (BubblePicker) findViewById(R.id.picker);
        progress_bar_bottom_home = (ProgressBar) findViewById(R.id.progress_bar_bottom_home);
//        getBubbleInterestListCall(bubbleListUrl);
    }

//    private void getBubbleInterestListCall(String url) {
//        progress_bar_bottom_home.setVisibility(View.VISIBLE);
//        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
//                url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(final String response) {
//                try {
//                    JSONObject mObj = new JSONObject(response);
//                    if (mObj.optInt("code") == 1) {
//                        // MultitvCipher mcipher = new MultitvCipher();
//                        //String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//                        Log.e("Home_api_response", mObj.optString("result"));
//                        InterestData interestModel = Json.parse(mObj.optString("result").trim(), InterestData.class);
//                        progress_bar_bottom_home.setVisibility(View.GONE);
//                        handlePagitionForInterestList(interestModel);
//                    }
//                } catch (Exception e) {
//                    Log.e("code==0", "" + e.getMessage());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progress_bar_bottom_home.setVisibility(View.GONE);
//                        }
//                    });
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("HomeFragment", "Error: " + error.getMessage());
//                progress_bar_bottom_home.setVisibility(View.GONE);
//            }
//        });
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//    }
//
//    private void handlePagitionForInterestList(InterestData interestModel) {
//        if (interestModel != null && interestModel.getInterests() != null && interestModel.getInterests().size() != 0) {
//            interestsArraylist.addAll(interestModel.getInterests());
//        }
//        if (interestsArraylist != null && interestsArraylist.size() != 0) {
//            showPickerContent(interestsArraylist);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        bubblePicker.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        bubblePicker.onPause();
//    }
//
//    private void showPickerContent(final List<Interest> interestsList) {
//        bubblePicker.setVisibility(View.VISIBLE);
//        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);
//        bubblePicker.setAdapter(new BubblePickerAdapter() {
//            @Override
//            public int getTotalCount() {
//                return interestsList.size();
//            }
//
//            @NotNull
//            @Override
//            public PickerItem getItem(int position) {
//                PickerItem item = new PickerItem();
//                item.setTitle(interestsList.get(position).getTitle());
//
//                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/app_regular.ttf");
//
//                item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
//                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
//                item.setTypeface(tf);
//                item.setTextColor(ContextCompat.getColor(BubblePickerActivity.this, android.R.color.white));
//                //item.setBackgroundImage(ContextCompat.getDrawable(BubblePickerActivity.this, images.getResourceId(position, 0)));
//                return item;
//            }
//        });
//    }
}
