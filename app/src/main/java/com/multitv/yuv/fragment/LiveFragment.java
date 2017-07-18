package com.multitv.yuv.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.LiveSectionListDataAdapter;
//import com.barwin.app.adapter.MainLiveAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.models.ChannelsData;
import com.multitv.yuv.models.SectionDataModel1;
import com.multitv.yuv.models.home.LiveData;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.google.gson.Gson;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arungoyal on 30/06/17.
 */

public class LiveFragment extends Fragment {

    private Context parentContext;
    private String TAG = this.getClass().getSimpleName();

    @BindView(R.id.main_content_recycler)
    protected RecyclerView contentRecycler;

    @BindView(R.id.main_progress_bar)
    protected ProgressBar progressBarMain;

    @BindView(R.id.bottom_progress_bar)
    protected ProgressBar bottomProgressBar;

    private SharedPreference sharedPreference;

    private String userID;
    private boolean isLoading;

    private String fragmentName;
    private ChannelsData channelsData;
    private String mediaBaseUrl,bannerbaseUrl;
//    private MainLiveAdapter mainAdapter;


    private ArrayList<SectionDataModel1> sectionList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentContext = context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.content_layout, container, false);
        ButterKnife.bind(this, rootview);


        sharedPreference = new SharedPreference();
        userID = sharedPreference.getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);

        fragmentName = "Channels";
        getContentData(false);
        return rootview;

    }


    private void getContentData(final boolean isLoadMoreRequest) {


        Log.d(this.getClass().getName(), "channels api====" + "http://api.multitvsolution.com/automatorapi/v3/channel/list/token/" + ApiRequest.TOKEN);

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                "http://api.multitvsolution.com/automatorapi/v3/channel/list/token/" + ApiRequest.TOKEN, new Response.Listener<String>() {
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


//                                String response = mObj.optString("result");
                                Log.d(TAG, "Api response====>>>> live" + response);

                                Gson gson = new Gson();


                                channelsData = gson.fromJson(response.trim(), ChannelsData.class);

                                mediaBaseUrl = channelsData.getProfile_base();
                                bannerbaseUrl = channelsData.getBanner_page();
                                Log.d(TAG, "Live data size=======>>> " + channelsData.getChannels().size());


                                if (!isLoadMoreRequest) {
                                    Utilities.writeToFile(response, fragmentName + ".txt");
                                }
//                                                videoData = Json.parse(response.trim(), Video.class);
////                                                boolean dataAdded = Utilities.writeObjectToFile(videoData, fragmentName + ".ser");
////                                                Log.d(TAG, "Data set for serialable====>>> " + dataAdded);
////                                                sharedPreference.setPreferencesString(getActivity(), DISPLAY_CATEGORY_OFFSET_KEY, "" + homeDataModel.display_offset);
////                                                sharedPreference.setPreferencesString(getActivity(), DISPLAY_CATEGORY_COUNT_KEY, "" + homeDataModel.display_count);
//                                                if (getActivity() == null)
//                                                    return;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDisplayPlaylistData(channelsData, isLoadMoreRequest);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            getActivity().runOnUiThread(new Runnable() {
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
                if (progressBarMain != null && progressBarMain.isShown())
                    progressBarMain.setVisibility(View.GONE);
                if (bottomProgressBar != null && bottomProgressBar.isShown())
                    bottomProgressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("device", "android");
                params.put("live_offset", "0");
                params.put("live_limit", "10");

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    private void showDisplayPlaylistData(ChannelsData videoData, boolean isLoadMoreRequest) {
        if (videoData == null)
            return;
        if (!isLoadMoreRequest) {
            if (videoData.getChannels() != null && videoData.getChannels().size() > 0) {
                initilizeMainRecyclerView(true);
//                SectionDataModel1 dataModel = new SectionDataModel1("", "", new ArrayList<>(videoData.getChannels()));
//                sectionList.add(dataModel);


                LiveSectionListDataAdapter adapter = new LiveSectionListDataAdapter(parentContext, videoData.getChannels(), mediaBaseUrl,bannerbaseUrl);
                contentRecycler.setAdapter(adapter);


            } else {
                initilizeMainRecyclerView(false);
            }
        }


        if (progressBarMain != null && progressBarMain.isShown())
            progressBarMain.setVisibility(View.GONE);
        if (bottomProgressBar != null && bottomProgressBar.isShown())
            bottomProgressBar.setVisibility(View.GONE);


        isLoading = false;

    }

    private void initilizeMainRecyclerView(boolean isFeatureAvailable) {
        sectionList = new ArrayList<SectionDataModel1>();
//        mainAdapter = new MainLiveAdapter(getActivity(), sectionList, isFeatureAvailable, fragmentName);
        contentRecycler.setHasFixedSize(true);
        contentRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
//        contentRecycler.setAdapter(mainAdapter);
//        contentRecycler.addOnScrollListener(scrollListener);

    }


}
