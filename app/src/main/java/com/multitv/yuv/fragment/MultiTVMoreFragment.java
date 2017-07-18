package com.multitv.yuv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.adapter.MoreRecommendedAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.customview.RecyclerItemClickListener;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.recommended.Recommended;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by naseeb on 11/29/2016.
 */

public class MultiTVMoreFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_top)
    ProgressBar progressBarTop;
    @BindView(R.id.progress_bar_bottom)
    ProgressBar progressBarBottom;
    @BindView(R.id.empty)
    LinearLayout noRecordFoundTV;

    private int totalCount = 0;
    private int count = 0, current_page = 0;
    private List<Cat_cntn> recommendArrayList = new ArrayList<>();
    private String contentId, categoryID;
    private Recommended recommended;
    private List<Cat_cntn> contents;
    private MoreRecommendedAdapter moreRecommendedAdapter;
    private SharedPreference sharedPreference;
    boolean isLoggedIn, isOTPVerified;
    private boolean isLoading;
    private String TAG = this.getClass().getSimpleName();

    private int lastVisibleItem, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multitv_more, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryID = bundle.getString("CATEGORY_ID");
            contentId = bundle.getString("CONTENT_ID");
        }


        ButterKnife.bind(this, rootView);
        sharedPreference = new SharedPreference();
        isLoggedIn = sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_OTP_VERIFIED);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);


        recyclerView.addOnScrollListener(scrollListener);

        getData(false);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {


                    if (isOTPVerified) {


                        String videoUrl = contents.get(position).url.toString();
                        if (!videoUrl.equals("")) {
                            Intent videoIntent = new Intent(getActivity(), MultiTvPlayerActivity.class);
                            videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, contents.get(position));
                            videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                            getActivity().finish();
                            startActivity(videoIntent);
                        }
                    } else {
                        Utilities.showLoginDailog(getActivity());
                    }
                } catch (Exception ex) {
                    Tracer.error("error", ex.getMessage());
                }
            }
        }));


        return rootView;
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


                    if (progressBarTop != null)
                        progressBarTop.setVisibility(View.VISIBLE);
                    getData(true);
                    isLoading = true;


                }

            }

        }
    };


    private void showDisplayPlaylistData(Recommended video, boolean isLoadMoreRequest) {
        if (video == null)
            return;
        if (!isLoadMoreRequest) {
            if (video != null && video.content.size() > 0) {
                contents = video.content;




                contents= Collections.synchronizedList(video.content);
                ListIterator<Cat_cntn> it = contents.listIterator();
                while (it.hasNext()){
                   Cat_cntn cat_cntn= it.next();
                    if (contentId.equalsIgnoreCase(cat_cntn.id)) {
                        contents.remove(cat_cntn);
                        break;
                    }
                }



                moreRecommendedAdapter = new MoreRecommendedAdapter(getActivity(), contents, recyclerView, true);
                recyclerView.setAdapter(moreRecommendedAdapter);
            }
        } else {
            contents.addAll(video.content);
        }


        if (progressBarTop != null && progressBarTop.isShown())
            progressBarTop.setVisibility(View.GONE);
        moreRecommendedAdapter.notifyDataSetChanged();
        isLoading = false;

    }


    private void getData(final boolean isLoadMore) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getActivity(), ApiRequest.VIDEO_CAT_URL_CLIST), new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                Tracer.error("Recommended_responce---", response.toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//                                Log.d(this.getClass().getName(), "recommnede response======>>" + str);
                                Tracer.error("Recommended response---", str);
                                try {
                                    JSONObject newObj = new JSONObject(str);
                                    recommended = Json.parse(newObj.toString(), Recommended.class);
                                    count = recommended.offset;
                                    totalCount = recommended.totalCount;

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showDisplayPlaylistData(recommended, isLoadMore);
                                        }
                                    });

                                    // Tracer.debug("Recommended", newObj.toString());
                                } catch (JSONException e) {
                                    Tracer.debug(TAG, "JSONException");
                                    ExceptionUtils.printStacktrace(e);
                                }


                            }

                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                        }
                    }
                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("Error", "Error: " + error.getMessage());

                if (current_page > 0)
                    progressBarBottom.setVisibility(View.GONE);
                else {
                    progressBarTop.setVisibility(View.GONE);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));

                params.put("device", "android");
                if (getActivity() == null)
                    return params;
                params.put("user_id", new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN));
                params.put("current_offset", String.valueOf(count));
                params.put("max_counter", "10");
                if (!TextUtils.isEmpty(categoryID))
                    params.put("cat_id", categoryID);


                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    Log.d("Recommned param", "parameter=======>>>>>  " + key + "   " + params.get(key));
                }


                return Utilities.checkParams(params);
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}
