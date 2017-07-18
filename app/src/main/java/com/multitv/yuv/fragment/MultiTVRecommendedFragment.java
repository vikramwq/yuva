package com.multitv.yuv.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.customview.RecyclerItemClickListener;
import com.multitv.yuv.utilities.Json;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.GetCallback;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.adapter.MoreRecommendedAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.home.Home;
import com.multitv.yuv.models.recommended.Recommended;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by arungoyal on 22/05/17.
 */

public class MultiTVRecommendedFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_top)
    ProgressBar progressBarTop;
    @BindView(R.id.progress_bar_bottom)
    ProgressBar progressBarBottom;


    private SharedPreference sharedPreference;
    private boolean isLoggedIn, isOTPVerified;
    private MoreRecommendedAdapter moreRecommendedAdapter;
    private Bundle bundle;
    private int contentType;
    private String categoryID;

    private String contentId;
    private int lastVisibleItem, totalItemCount;
    private List<Cat_cntn> contents;
    private int count, totalCount = 0;
    private Context parentContext;
    private String TAG = this.getClass().getSimpleName();
    private boolean isLoading;
    Recommended recommended;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multitv_more, container, false);
        ButterKnife.bind(this, rootView);
        sharedPreference = new SharedPreference();

        isLoggedIn = sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_OTP_VERIFIED);
        bundle = getArguments();
        contentType = bundle.getInt("CONTENT_TYPE");
        categoryID = bundle.getString("CATEGORY_ID");
        contentId = bundle.getString("CONTENT_ID");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.addOnScrollListener(scrollListener);

        getContentData(false);


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
                    getContentData(true);
                    isLoading = true;


                }

            }

        }
    };


    private void getContentData(final boolean isLoadMoreRequest) {

        final Type homeObjectType = new TypeToken<Home>() {
        }.getType();

        String key = "home";
        AppController.getInstance().getCacheManager().getAsync(key, Home.class, homeObjectType, new GetCallback() {
            @Override
            public void onSuccess(final Object object) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(this.getClass().getName(), "content api==" + AppUtils.generateUrl(parentContext, ApiRequest.RECOMMENDED_LIST));

                        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                                AppUtils.generateUrl(parentContext, ApiRequest.RECOMMENDED_LIST), new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject mObj = new JSONObject(response);
                                            if (mObj.optInt("code") == 1) {
                                                com.multitv.cipher.MultitvCipher mcipher = new com.multitv.cipher.MultitvCipher();
                                                String response = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                                Log.d(this.getClass().getName(), "response for genre id===" + response);

                                                JSONObject newObj = new JSONObject(response);
                                                 recommended = Json.parse(newObj.toString(), Recommended.class);


                                                 count = recommended.offset;
                                                 totalCount = recommended.totalCount;


                                                Log.d(this.getClass().getName(), "count======" + count);


                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showDisplayPlaylistData(recommended, isLoadMoreRequest);
                                                    }
                                                });


                                            }
                                        } catch (Exception e) {
                                            ExceptionUtils.printStacktrace(e);

                                            Utilities.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    isLoading = false;
                                                    if (progressBarBottom != null && progressBarBottom.isShown())
                                                        progressBarBottom.setVisibility(View.GONE);
                                                }
                                            });


                                        }
                                    }
                                }).start();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Tracer.error(TAG, "Error: " + error.getMessage());
                                isLoading = false;
                                if (progressBarBottom != null && progressBarBottom.isShown())
                                    progressBarBottom.setVisibility(View.GONE);
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();


                                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                                params.put("device", "android");
                                params.put("user_id", new SharedPreference().getPreferencesString(parentContext, "user_id" + "_" + ApiRequest.TOKEN));
                                params.put("current_offset", String.valueOf(count));
                                params.put("max_counter", "10");


//                                Set<String> keySet = params.keySet();
//                                for (String key : keySet) {
//                                    Log.d("Recommned param", "parameter=======>>>>>  " + key + "   " + params.get(key));
//                                }

                                return Utilities.checkParams(params);

                            }
                        };

                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jsonObjReq);

                    }
                }).start();
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionUtils.printStacktrace(e);
            }
        });
    }


    private void showDisplayPlaylistData(Recommended video, boolean isLoadMoreRequest) {
        if (video == null)
            return;
        if (!isLoadMoreRequest) {
            if (video != null && video.content.size() > 0) {

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


}
