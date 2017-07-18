package com.multitv.yuv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.customview.RecyclerItemClickListener;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.adapter.FavoriteProgramAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.FavoriteModel;
import com.multitv.yuv.models.MyProgram;
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
public class FavoriteProgramFragment extends Fragment {
    private ArrayList<MyProgram> my_program_list;
    private FavoriteModel favoriteModel;
    @BindView(R.id.data_frame_layout)
    FrameLayout dataFrameLayout;
    @BindView(R.id.favorite_recylerviw)
    RecyclerView recycler_view;
    @BindView(R.id.nested_scrollview)
    NestedScrollView nestedScrollview;
    private String start_cast, title, des, thumbnail, rating, type, user_id, content_id_, duration;
    private long watchedDuration;
    @BindView(R.id.progressBar2)
    ProgressBar mProgressbar_top;
    @BindView(R.id.empty)
    LinearLayout noRecordFoundTV;

    public void setWatchedDuration(long watchedDuration) {
        this.watchedDuration = watchedDuration;
    }

    public FavoriteProgramFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.favorite_program_layout, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() == null)
            return null;
        user_id = new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null)
            return;
        if (ConnectionManager.getInstance(getActivity()).isConnected()) {
            getFavoriteList();
        }
    }

    public void getFavoriteList() {
        mProgressbar_top.setVisibility(View.VISIBLE);
        Tracer.error("***Filter-Url***", ""+AppUtils.generateUrl(getActivity(), ApiRequest.FAVORITE_LIST_URL));
        if (getActivity() == null)
            return;
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getActivity(), ApiRequest.FAVORITE_LIST_URL), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Tracer.error("api_get_watching", response.toString());
                mProgressbar_top.setVisibility(View.GONE);
                try {
                    JSONObject mObj = new JSONObject(response);

                    MultitvCipher mcipher = new MultitvCipher();
                    String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                    Tracer.error("favorite_list_show", str);
                    try {
                        JSONObject newObj = new JSONObject(str);
                        favoriteModel = Json.parse(newObj.toString(), FavoriteModel.class);

                        Tracer.error("favoriteList", newObj.toString());
                    } catch (JSONException e) {
                        Tracer.error("respone_error", "JSONException");
                        ExceptionUtils.printStacktrace(e);
                    }

                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
                favoriteListVideo();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("video", "Error: " + error.getMessage());
                mProgressbar_top.setVisibility(View.GONE);
                // hide the progress dialog

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("current_offset", "0");
                params.put("current_version", "0.0");
                params.put("max_counter", "10");
                params.put("c_id", user_id);
                params.put("device", "android");
                //params.put("user_id", user_id);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void favoriteListVideo() {
        if (favoriteModel != null && favoriteModel.content != null && favoriteModel.content.size() != 0) {
            for (int i = 0; i < favoriteModel.content.size(); i++) {
                if (getActivity() == null)
                    return;
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // Tracer.error("video-url---", ""+videoData.content.get(i).toString());
                favoriteModel.content.get(i).title.toString();
                favoriteModel.content.get(i).source.toString();
                favoriteModel.content.get(i).meta.type.toString();
                favoriteModel.content.get(i).thumbnail.large.toString();
                favoriteModel.content.get(i).url.toString();
                // Tracer.error("url_vod---", ""+videoData.content.get(i).url.toString());
                //  Tracer.error("tittle_vod---", ""+videoData.content.get(i).title);
                Tracer.error("image_vod---", "" + favoriteModel.content.get(i).thumbnail.large);
                Tracer.error("video_type---", "" + favoriteModel.content.get(i).type);
                Tracer.error("video_id---", "" + favoriteModel.content.get(i).id);


            }
            nestedScrollview.setFocusableInTouchMode(true);
            nestedScrollview.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            recycler_view.setHasFixedSize(true);
            if (getActivity() == null)
                return;
            FavoriteProgramAdapter adp = new FavoriteProgramAdapter(getActivity(), favoriteModel.content, recycler_view);
            recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setAdapter(adp);
            recycler_view.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // TODO Handle item click
                            String VIDEO_URL = favoriteModel.content.get(position).url.toString();
                            String CONTENT_TYPE = favoriteModel.content.get(position).source.toString();
                            Tracer.error("url", VIDEO_URL);
                            Tracer.error("url", CONTENT_TYPE);

                            if (favoriteModel.content.get(position).type != null && !favoriteModel.content.get(position).type.equals("null") && !favoriteModel.content.get(position).type.isEmpty()) {
                                type = favoriteModel.content.get(position).media_type.toString();
                            }

                            if (favoriteModel.content.get(position).des != null && !favoriteModel.content.get(position).des.equals("null") && !favoriteModel.content.get(position).des.isEmpty()) {
                                des = favoriteModel.content.get(position).des.toString();
                            }
                            if (favoriteModel.content.get(position).title != null && !favoriteModel.content.get(position).title.equals("null") && !favoriteModel.content.get(position).title.isEmpty()) {
                                title = favoriteModel.content.get(position).title.toString();
                            }

                            if (favoriteModel.content.get(position).duration != null && !favoriteModel.content.get(position).duration.equals("null") && !favoriteModel.content.get(position).duration.isEmpty()) {
                                duration = favoriteModel.content.get(position).duration;
                            }

                            if (favoriteModel.content.get(position).thumbnail.large != null && !favoriteModel.content.get(position).thumbnail.large.equals("null") && !favoriteModel.content.get(position).thumbnail.large.isEmpty()) {
                                thumbnail = favoriteModel.content.get(position).thumbnail.large;
                            }
                            String like = favoriteModel.content.get(position).likes + "";
                            String fav_item = favoriteModel.content.get(position).favorite.toString();
                            String video_id = favoriteModel.content.get(position).id;
                            //int content_id = favoriteModel.content.get(position).categoryIdsList.get(0);

                            if (favoriteModel.content.get(position).meta.star_cast != null && !favoriteModel.content.get(position).meta.star_cast.equals("null") && !favoriteModel.content.get(position).meta.star_cast.isEmpty()) {
                                start_cast = favoriteModel.content.get(position).meta.star_cast;
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
                                    videoIntent.putExtra("likes_count", favoriteModel.content.get(position).likes_count);
                                    //videoIntent.putExtra("content_id", content_id);
                                    videoIntent.putExtra("type", type);
                                    videoIntent.putExtra("video_id", video_id);
                                    videoIntent.putExtra("thumbnail", thumbnail);
                                    videoIntent.putExtra("content_type", CONTENT_TYPE);
                                    videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                                    videoIntent.putExtra("position", position);
                                    videoIntent.putExtra("fav_item", fav_item);
                                    videoIntent.putExtra("WATCHED_DURATION", watchedDuration);
                                    videoIntent.putExtra("SOCIAL_LIKES", favoriteModel.content.get(position).social_like);
                                    videoIntent.putExtra("SOCIAL_VIEWS", favoriteModel.content.get(position).social_view);
                                    startActivity(videoIntent);


                                    // PlayerUtils.startPlayerActivity(mContext, VIDEO_URL,thumbnail, content_id, title,CONTENT_TYPE, des, "VOD", 1);

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
        } else {
            dataFrameLayout.setVisibility(View.GONE);
            noRecordFoundTV.setVisibility(View.VISIBLE);
        }
    }

}
