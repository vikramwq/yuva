package com.multitv.yuv.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.multitv.yuv.adapter.ContentAdapter;
import com.multitv.yuv.models.SectionDataModel;
import com.multitv.yuv.models.categories.Child;
import com.multitv.yuv.models.home.Home;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.GetCallback;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.adapter.MainRecyclerAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.categories.Vod;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.Utilities;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by multitv on 25-04-2017.
 */

public class ContentFragment extends Fragment implements NotificationCenter.NotificationCenterDelegate {

    private static String TAG = "ContentFragment";
    private static String CATEGORY_TYPE_KEY = "video";
    private static String FRAGMENT_NAME = "fragment";

    @BindView(R.id.main_content_recycler)
    protected RecyclerView contentRecycler;

    @BindView(R.id.main_progress_bar)
    protected ProgressBar progressBarMain;

    @BindView(R.id.bottom_progress_bar)
    protected ProgressBar bottomProgressBar;

    private int count, totalCount = 0;
    private Context parentContext;

    private boolean isLoading;
    private int lastVisibleItem, totalItemCount, offset;
    private Vod vodCategoryObj;
    private int totalPalylistCount, currentShownPlaylistCount, showPlaylistCount = 3;
    private ArrayList<SectionDataModel> sectionList;
    ContentAdapter adapter;
    private Video videoData;
    private SharedPreference sharedPreference;
    private String userID;
    private HashMap<String, String> catHashMap;
    private String selectedGenre, fragmentName;
    private boolean isDataUpdated;
    ArrayList<Cat_cntn> contentData = null;

    public static ContentFragment newInstance(Vod vodObj, String fragmentName, boolean isDataUpdated) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CATEGORY_TYPE_KEY, vodObj);
        bundle.putString(FRAGMENT_NAME, fragmentName);
        bundle.putBoolean("isDataUpdated", isDataUpdated);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.content_layout, container, false);
        ButterKnife.bind(this, rootview);

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didContentNeedRefresh);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            vodCategoryObj = bundle.getParcelable(CATEGORY_TYPE_KEY);
            fragmentName = bundle.getString(FRAGMENT_NAME);
            isDataUpdated = bundle.getBoolean("isDataUpdated");
        }
        sharedPreference = new SharedPreference();
        userID = sharedPreference.getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);
        if (vodCategoryObj != null) {
            if (vodCategoryObj.children != null)
                totalPalylistCount = vodCategoryObj.children.size();
        }
        selectedGenre = sharedPreference.getPreferencesString(getActivity(), "GENRES");
        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        catHashMap = calculateCatInfo();
        if (ConnectionManager.getInstance(getApplicationContext()).isConnected()) {

            if (isDataUpdated || Utilities.readFromFile(getActivity(), fragmentName + ".txt") == null) {
                getContentData(false, catHashMap);
            } else {
                String storedData = Utilities.readFromFile(getActivity(), fragmentName + ".txt");
                if (storedData != null && storedData.trim().length() > 0) {
                    videoData = Json.parse(storedData.trim(), Video.class);
                    count=videoData.content.size();
                    totalCount=videoData.totalCount;
                    showDisplayPlaylistData(videoData, false);
                }
            }
        } else {

            String storedData = Utilities.readFromFile(getActivity(), fragmentName + ".txt");
            if (storedData != null && storedData.trim().length() > 0) {
                videoData = Json.parse(storedData.trim(), Video.class);
                count=videoData.content.size();
                totalCount=videoData.totalCount;
                showDisplayPlaylistData(videoData, false);
            }
        }
    }

    private void initilizeMainRecyclerView(boolean isFeatureAvailable) {
        sectionList = new ArrayList<SectionDataModel>();
        contentRecycler.setHasFixedSize(true);
        contentRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        contentRecycler.addOnScrollListener(scrollListener);

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
//                    catHashMap = calculateCatInfo();
                    getContentData(true, catHashMap);
                    isLoading = true;


                }

            }

        }


//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//
//            totalItemCount = recyclerView.getAdapter().getItemCount();
//
//            try {
////                int visible item =  recyclerView.getC
////                lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getFocusedChild());
//                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//            } catch (Exception e) {
//                Tracer.error("Error", "onScrolled: EXCEPTION " + e.getMessage());
//                lastVisibleItem = 0;
//            }
//
//            if (!isLoading && totalItemCount == (lastVisibleItem + 1)) {
//
//
//                if (currentShownPlaylistCount < totalPalylistCount) {
//                    if (bottomProgressBar != null)
//                        bottomProgressBar.setVisibility(View.VISIBLE);
//                    catHashMap = calculateCatInfo();
//                    getContentData(true, catHashMap);
//                    isLoading = true;
//                }
//
//
//            }
//        }
    };

    private void getContentData(final boolean isLoadMoreRequest, final HashMap<String, String> catIdMap) {
        if (getActivity() == null)
            return;
        final Type homeObjectType = new TypeToken<Home>() {
        }.getType();

        String key = "home";
        AppController.getInstance().getCacheManager().getAsync(key, Home.class, homeObjectType, new GetCallback() {
            @Override
            public void onSuccess(final Object object) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                                AppUtils.generateUrl(getActivity(), ApiRequest.VIDEO_CAT_URL_CLIST), new Response.Listener<String>() {
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

                                                JSONObject jsonObject = new JSONObject(response);
                                                count = jsonObject.getInt("offset");
                                                totalCount = jsonObject.getInt("totalCount");


                                                Log.d(TAG, "Api " + fragmentName + "response====>>>> " + response);
                                                if (!isLoadMoreRequest) {

                                                    Utilities.writeToFile(response, fragmentName + ".txt");

                                                }
                                                videoData = Json.parse(response.trim(), Video.class);
                                                if (getActivity() == null)
                                                    return;
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showDisplayPlaylistData(videoData, isLoadMoreRequest);
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
                                String categoryIds = "";

                                for (String key : catIdMap.keySet()) {
                                    categoryIds = categoryIds + catIdMap.get(key) + ",";
                                }
                                categoryIds = trimCatIDs(categoryIds);

                                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));

                                params.put("device", "android");
                                params.put("user_id", userID);
                                params.put("current_offset", String.valueOf(count));
                                params.put("cat_id", categoryIds);
                                params.put("max_counter", "8");
                                params.put("cat_type", "video");
                                Log.d("Params:", "paramter===" + userID + " cat==" + categoryIds + " lan==" + LocaleHelper.getLanguage(getApplicationContext()) + "m_filter==" + (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
                                return params;
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

    private void showDisplayPlaylistData(Video videoData, boolean isLoadMoreRequest) {


        if (videoData == null)
            return;
        if (!isLoadMoreRequest) {
            if (videoData.content != null && videoData.content.size() > 0) {
                initilizeMainRecyclerView(true);
                contentData = videoData.content;
                adapter = new ContentAdapter(parentContext, contentData);
                contentRecycler.setAdapter(adapter);
//                SectionDataModel dataModel = new SectionDataModel("", "", new ArrayList<>(videoData.feature));
//                sectionList.add(dataModel);
            } else {
                initilizeMainRecyclerView(false);
            }
        } else {

            if (videoData.content != null && videoData.content.size() > 0) {
                contentData.addAll(videoData.content);

                adapter.notifyDataSetChanged();

            }


        }


        if (videoData.content != null && videoData.content.size() > 0) {
            ArrayList<Cat_cntn> contentList;
            for (String key : catHashMap.keySet()) {
                contentList = new ArrayList<>();
                for (int i = 0; i < videoData.content.size(); i++) {
                    Cat_cntn contentInfo = videoData.content.get(i);
                    if (contentInfo != null && contentInfo.category_ids.contains(catHashMap.get(key))) {
                        contentList.add(contentInfo);
                    }
                }
//                if (contentList != null && contentList.size() > 0) {
//                    sectionList.add(new SectionDataModel(key, catHashMap.get(key), contentList));
//                }
            }
        }

        if (progressBarMain != null && progressBarMain.isShown())
            progressBarMain.setVisibility(View.GONE);
        if (bottomProgressBar != null && bottomProgressBar.isShown())
            bottomProgressBar.setVisibility(View.GONE);


//        mainAdapter.notifyDataSetChanged();
        isLoading = false;

    }

    private HashMap<String, String> calculateCatInfo() {
        HashMap<String, String> catHashMap = new HashMap<>();
        if (vodCategoryObj.children != null && vodCategoryObj.children.size() > 0)
            for (int i = 0; i < vodCategoryObj.children.size(); i++) {
                if (i < vodCategoryObj.children.size()) {
                    Child catChild = vodCategoryObj.children.get(i);
                    catHashMap.put(catChild.name, catChild.id);
                }
            }
        currentShownPlaylistCount = currentShownPlaylistCount + catHashMap.size();
        return catHashMap;

    }

    private String trimCatIDs(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parentContext = context;
    }


    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didContentNeedRefresh) {

            catHashMap = calculateCatInfo();

            getContentData(false, catHashMap);

        }


    }
}
