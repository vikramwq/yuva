package com.multitv.yuv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.adapter.WatchingProgramAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.customview.DividerItemDecoration;
import com.multitv.yuv.customview.RecyclerItemClickListener;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.watching_get.Content;
import com.multitv.yuv.models.watching_get.WatchingGet;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by root on 9/9/16.
 */
public class WatchingProgramFragment extends Fragment {
    private static final String TAG = "WatchingProgramFragment";
    @BindView(R.id.data_frame_layout)
    FrameLayout dataFrameLayout;
    @BindView(R.id.channel_recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.empty)
    LinearLayout noRecordFoundTV;
    private boolean isWatchingListApiCallGoingOn;
    private List<Content> watchingContentList;
    //private CustomProgressDialog progressDialog;
    // CustomProgressDialog customProgressDialog;
    @BindView(R.id.progressBar2)
    ProgressBar mProgressbar_top;
    private String start_cast, title, des, thumbnail, rating, type, user_id, content_id_, duration;

    public WatchingProgramFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.watching_program_layout, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() == null)
            return null;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recycler_view.setHasFixedSize(true);

        recycler_view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        String VIDEO_URL = watchingContentList.get(position).url.toString();
                        String CONTENT_TYPE = watchingContentList.get(position).source.toString();
                        Tracer.error("url", VIDEO_URL);
                        Tracer.error("url", CONTENT_TYPE);

                        if (watchingContentList.get(position).type != null && !watchingContentList.get(position).type.equals("null") && !watchingContentList.get(position).type.isEmpty()) {
                            type = watchingContentList.get(position).media_type.toString();
                        }

                        if (watchingContentList.get(position).des != null && !watchingContentList.get(position).des.equals("null") && !watchingContentList.get(position).des.isEmpty()) {
                            des = watchingContentList.get(position).des.toString();
                        }

                        if (watchingContentList.get(position).title != null && !watchingContentList.get(position).title.equals("null") && !watchingContentList.get(position).title.isEmpty()) {
                            title = watchingContentList.get(position).title.toString();
                        }

                        if (watchingContentList.get(position).duration != null && !watchingContentList.get(position).duration.equals("null") && !watchingContentList.get(position).duration.isEmpty()) {
                            duration = watchingContentList.get(position).duration;
                        }

                        if (watchingContentList.get(position).thumbnail.large != null && !watchingContentList.get(position).thumbnail.large.equals("null") && !watchingContentList.get(position).thumbnail.large.isEmpty()) {
                            thumbnail = watchingContentList.get(position).thumbnail.large;
                        }
                        String like = watchingContentList.get(position).likes.toString();
                        String fav_item = watchingContentList.get(position).favorite.toString();
                        String video_id = watchingContentList.get(position).id;

                        if (watchingContentList.get(position).meta.star_cast != null && !watchingContentList.get(position).meta.star_cast.equals("null") && !watchingContentList.get(position).meta.star_cast.isEmpty()) {
                            start_cast = watchingContentList.get(position).meta.star_cast;
                        }

                        try {
                            if (!VIDEO_URL.equals("")) {

                                if (getActivity() == null)
                                    return;
                                Intent videoIntent = new Intent(getActivity(), MultiTvPlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                videoIntent.putExtra("VIDEO_URL", VIDEO_URL);
                                videoIntent.putExtra("descreption", des);
                                videoIntent.putExtra("title", title);
                                videoIntent.putExtra("like", like);
                                videoIntent.putExtra("start_cast", start_cast);
                                videoIntent.putExtra("likes_count", watchingContentList.get(position).likes_count);
                                videoIntent.putExtra("type", type);
                                videoIntent.putExtra("video_id", video_id);
                                videoIntent.putExtra("thumbnail", thumbnail);
                                videoIntent.putExtra("content_type", CONTENT_TYPE);
                                videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                                videoIntent.putExtra("position", position);
                                videoIntent.putExtra("fav_item", fav_item);
                                videoIntent.putExtra("SOCIAL_LIKES", watchingContentList.get(position).social_like);
                                videoIntent.putExtra("SOCIAL_VIEWS", watchingContentList.get(position).social_view);
                                startActivity(videoIntent);
                            } else {
                                if (getActivity() == null)
                                    return;
                                Toast.makeText(getActivity(), "No Record Found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception ex) {
                            Tracer.error("error", ex.getMessage());
                        }

                    }
                })
        );

        return view;
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getWatchingData();
    }
*/

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!getUserVisibleHint()) {
            return;
        }

        getWatchingData();
    }

    private void getWatchingData() {
        if (getActivity() == null)
            return;
        user_id = new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);

        if (user_id != null && !user_id.isEmpty()) {
            if (!isWatchingListApiCallGoingOn) {
                isWatchingListApiCallGoingOn = true;
                recycler_view.setVisibility(View.VISIBLE);
                noRecordFoundTV.setVisibility(View.GONE);
                if (ConnectionManager.getInstance(getActivity()).isConnected()) {
                    getWatchingList();
                }
            }
        } else {
            recycler_view.setVisibility(View.GONE);
            noRecordFoundTV.setVisibility(View.VISIBLE);
            mProgressbar_top.setVisibility(View.GONE);
        }
    }

    public void getWatchingList() {
        if (watchingContentList == null || watchingContentList.size() == 0)
            mProgressbar_top.setVisibility(View.VISIBLE);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getActivity(), ApiRequest.WATCHING_GET_URL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   customProgressDialog.dismiss(this);
                mProgressbar_top.setVisibility(View.GONE);
                Tracer.error(TAG, "api_get_watching" + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    MultitvCipher mcipher = new MultitvCipher();
                    String str = new String(mcipher.decryptmyapi(jsonObject.optString("result")));
                    Tracer.debug(TAG, "output : " + str);

                    WatchingGet watchingGet = Json.parse(str.trim(), WatchingGet.class);
                    watchingContentList = watchingGet.getContent();

                    if (watchingGet != null && watchingContentList != null && watchingContentList.size() != 0) {
                        if (getActivity() == null)
                            return;
                        WatchingProgramAdapter watchingProgramAdapter = new WatchingProgramAdapter(getActivity(), watchingContentList);
                        recycler_view.setAdapter(watchingProgramAdapter);
                    } else {
                        dataFrameLayout.setVisibility(View.GONE);
                        noRecordFoundTV.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }

                isWatchingListApiCallGoingOn = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "Error: " + error.getMessage());

                isWatchingListApiCallGoingOn = false;
                mProgressbar_top.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("c_id", user_id);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
