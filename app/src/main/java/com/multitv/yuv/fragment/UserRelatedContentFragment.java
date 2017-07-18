package com.multitv.yuv.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.recommended.Recommended;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.models.FavoriteModel;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by root on 22/11/16.
 */

public class UserRelatedContentFragment extends Fragment {
    private int typeOfFragment;
    private FavoriteModel favoriteModel;
    private Recommended recommended;
    private int itemsLeft = -1, startCount = 0, endCount = 0, count = 0;
    private MoreRecommendedAdapter profileContentAdapter;
    private String offsetString;
    private int current_page = 0;
    private boolean hasSearch_focus = false;

    private SharedPreference sharedPreference;
    @BindView(R.id.empty_text)
    TextView empty_text;
    @BindView(R.id.favorite_recylerviw)
    RecyclerView moreRecylerView;
    @BindView(R.id.data_frame_layout)
    LinearLayout dataFrameLayout;
    private String start_cast, title, des, thumbnail, type, user_id, content_id_, duration;
    private long watchedDuration;
    private List<Cat_cntn> displayArrayList = new ArrayList<>();
    @BindView(R.id.mProgress_bar_top)
    ProgressBar mProgress_bar_top;
    @BindView(R.id.empty)
    LinearLayout noRecordFoundTV;
    @BindView(R.id.progress_bottom_loadmore)
    ProgressBar progress_bottom_loadmore;
    private boolean isLoggedIn, isOTPVerified;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview;
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_user_related_content, container, false);
        ButterKnife.bind(this, rootview);
        sharedPreference = new SharedPreference();
        if (getActivity() == null)
            return null;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mProgress_bar_top.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.status_bar_color), PorterDuff.Mode.MULTIPLY);
        user_id = new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);


        isLoggedIn = sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_OTP_VERIFIED);


        Bundle arguments = getArguments();
        if (arguments != null) {
            typeOfFragment = arguments.getInt(Constant.EXTRA_KEY);
            Log.e("typeOfFragment", "" + typeOfFragment);

            LinearLayoutManager gridLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            moreRecylerView.setLayoutManager(gridLayoutManager1);
            profileContentAdapter = new MoreRecommendedAdapter(getActivity(), displayArrayList, moreRecylerView, true);
            moreRecylerView.setAdapter(profileContentAdapter);
            favoriteListVideo();
            Log.e("retrieved Key", "profilelikes" + "_" + typeOfFragment);
            offsetString = sharedPreference.getPreferencesString(getActivity(), "profilelikes" + "_" + typeOfFragment);
            Log.e("***offsetString****", offsetString);

            if (!TextUtils.isEmpty(offsetString))
                count = Integer.parseInt(offsetString);
            getHomeCatData(current_page);
            profileContentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    current_page = current_page + 1;
                    Tracer.error("more data", "" + current_page);
                    count = recommended.offset;
                    getHomeCatData(current_page);
                    Log.e("load more data", "" + current_page);
                }
            });

            if (typeOfFragment == 0) {
                empty_text.setText(getResources().getString(R.string.empty_watching));
            } else if (typeOfFragment == 1) {
                empty_text.setText(getResources().getString(R.string.empty_faveroite));
            } else if (typeOfFragment == 2) {
                empty_text.setText(getResources().getString(R.string.empty_likes));
            }
        }


        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /*==============================get user data from user_related api================================================================
         ==========================================================================================================
              ============================================================================*/

    private void getHomeCatData(final int current_page) {
        if (current_page > 0) {
            progress_bottom_loadmore.setIndeterminate(true);
            progress_bottom_loadmore.setVisibility(View.VISIBLE);
        } else {
            mProgress_bar_top.setIndeterminate(true);
            mProgress_bar_top.setVisibility(View.VISIBLE);
        }

        if (recommended == null || count < recommended.totalCount) {
            if (getActivity() == null)
                return;
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    AppUtils.generateUrl(getActivity(), ApiRequest.LIKES_USER_CONTENT), new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        if (current_page > 0)
                            progress_bottom_loadmore.setVisibility(View.GONE);
                        else
                            mProgress_bar_top.setVisibility(View.GONE);
                        Tracer.error("***userlike***", response.toString());
                        JSONObject mObj = new JSONObject(response);

//                        Log.d(this.getClass().getName(),"typeOfFragment======"+typeOfFragment);

                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Tracer.error("**profile**data**", str);
                        try {
                            JSONObject newObj = new JSONObject(str);
                            recommended = Json.parse(newObj.toString(), Recommended.class);
                            Tracer.error("LIKES_LIST", newObj.toString());
                        } catch (JSONException e) {
                            Tracer.error("JSON_ERROR", "LIKES_LIST" + e.getMessage().toString());
                            ExceptionUtils.printStacktrace(e);
                        }
                        if (recommended == null || recommended.content == null || recommended.content.size() == 0) {
                            mProgress_bar_top.setVisibility(View.GONE);
                            moreRecylerView.setVisibility(View.GONE);
                            noRecordFoundTV.setVisibility(View.VISIBLE);
                        } else {
                            displayArrayList.addAll(recommended.content);
                        }
                        if (displayArrayList != null && displayArrayList.size() != 0) {
                            profileContentAdapter.setLoaded();
                            profileContentAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Tracer.debug("Error", "Error: " + error.getMessage());

                    if (current_page > 0)
                        progress_bottom_loadmore.setVisibility(View.GONE);
                    else
                        mProgress_bar_top.setVisibility(View.GONE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();


                    Log.d(this.getClass().getName(), "mature filter" + (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                    params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
//                    params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                    params.put("current_version", "0.0");
                    params.put("max_counter", "10");
                    params.put("device", "android");
                    params.put("user_id", user_id);
                    params.put("current_offset", String.valueOf(count));

                    Tracer.error("current_version", "0.0");
                    Tracer.error("max_counter", "10");
                    Tracer.error("device", "android");
                    Tracer.error("user_id", user_id);
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

        } else {
            if (displayArrayList != null && displayArrayList.size() != 0) {
                profileContentAdapter.setLoaded();
                profileContentAdapter.notifyDataSetChanged();
            }

            if (current_page > 0)
                progress_bottom_loadmore.setVisibility(View.GONE);
            else
                mProgress_bar_top.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        current_page = 0;
        itemsLeft = -1;
        startCount = 0;
        endCount = 0;
    }

    /*==============================on Item click on recyclerview================================================================
         ==========================================================================================================
              ============================================================================*/
    private void favoriteListVideo() {
        moreRecylerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            // TODO Handle item click

                            if (isOTPVerified) {


                                String VIDEO_URL = displayArrayList.get(position).url.toString();
                                if (!VIDEO_URL.equals("")) {
                                    if (getActivity() == null)
                                        return;
                                    Intent videoIntent = new Intent(getActivity(), MultiTvPlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, displayArrayList.get(position));
                                    videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                                    startActivity(videoIntent);

                                } else {
                                    if (getActivity() == null)
                                        return;
                                    Toast.makeText(getActivity(), "No Record Found", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Utilities.showLoginDailog(getActivity());
                            }


                        } catch (Exception ex) {
                            Tracer.error("error", ex.getMessage());
                        }

                    }
                })
        );

    }

}