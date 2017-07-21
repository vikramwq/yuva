package com.multitv.yuv.fragment;

import android.app.Activity;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.GetCallback;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.MainRecyclerAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.ChannelsData;
import com.multitv.yuv.models.PersistenceDataItem;
import com.multitv.yuv.models.SectionDataModel;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.home.Home;
import com.multitv.yuv.models.home.Home_category;
import com.multitv.yuv.models.versions.Version;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.LiveChannelUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.multitv.yuv.utilities.VersionUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by multitv on 24-04-2017.
 */

public class LandingFragment extends Fragment implements LiveChannelUtils.LiveChannelInterface {

    private static String TAG = "LANDING_FRAGMENT";

    private static String DISPLAY_CATEGORY_OFFSET_KEY = "offset__home_category";

    private static String DISPLAY_CATEGORY_COUNT_KEY = "count_home_category";
    private static String FRAGMENT_NAME = "fragment";
    @BindView(R.id.main_content_recycler)
    protected RecyclerView homeContentRecycler;

    @BindView(R.id.main_progress_bar)
    protected ProgressBar progressBarMain;

    @BindView(R.id.bottom_progress_bar)
    protected ProgressBar bottomProgressBar;


    private SharedPreference sharedPreference;
    //    private SharedPrefManager sharedPrefManager;
    private MainRecyclerAdapter mainSectionAdapter;
    private ArrayList<SectionDataModel> sectionList;
    private Home homeDataModel;
    private boolean isLoading;
    private int lastVisibleItem, totalItemCount, offset;
    private String selectedGenre;
    private String userID;
    private ArrayList<Cat_cntn> persistanceDataList;
    private String fragmentName;
    private Context parentContext;
    private ChannelsData channelsData;
    private boolean isDataUpdated;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = new SharedPreference();


//        Utilities.saveDatabase();
        userID = new SharedPreference().getPreferencesString(parentContext, "user_id" + "_" + ApiRequest.TOKEN);
        selectedGenre = sharedPreference.getPreferencesString(parentContext, "GENRES");
        MediaDbConnector mediaDbConnector = new MediaDbConnector(parentContext);
        List<PersistenceDataItem> persistenceDataItems = mediaDbConnector.getPersistenceMedia();
        if (persistenceDataItems != null && persistenceDataItems.size() > 0) {
            persistanceDataList = new ArrayList<>();
            for (int i = 0; i < persistenceDataItems.size(); i++) {
                String persistenceObj = persistenceDataItems.get(i).getData();
//                Log.d(this.getClass().getName(),"persistenceObj=====>>>"+persistenceObj);
                if (persistenceObj != null) {
                    Cat_cntn content = Json.parse(persistenceObj.trim(), Cat_cntn.class);
                    content.seekDuration = persistenceDataItems.get(i).getDuration();
                    persistanceDataList.add(content);
                }
            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.content_layout, container, false);
        ButterKnife.bind(this, rootview);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isDataUpdated = bundle.getBoolean("isDataUpdated");
            fragmentName = bundle.getString(FRAGMENT_NAME);
        }

        Log.d(this.getClass().getName(), "fragmentName=====in landing fragment" + fragmentName);

        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
            getVersionData();
            //Getting live channels
            LiveChannelUtils.getLiveChannelResponse(userID, 0, this);
//            Home home = (Home) Utilities.readObjectFromFile("home.ser");
//
//            if(home!=null) {
//                Log.d(TAG, "count feature banner====>>> " + home.version.dash_version);
//            }

            if (isDataUpdated || Utilities.readFromFile(getActivity(), "home.txt") == null) {
                getContentData(false);
            } else {
                String storedData = Utilities.readFromFile(getActivity(), "home.txt");
                if (storedData != null && storedData.trim().length() > 0) {
                    homeDataModel = Json.parse(storedData.trim(), Home.class);
                    showDisplayPlaylistData(homeDataModel, false);
                }
            }
        } else {
            String storedData = Utilities.readFromFile(getActivity(), "home.txt");
            if (storedData != null && storedData.trim().length() > 0) {
                homeDataModel = Json.parse(storedData.trim(), Home.class);
                showDisplayPlaylistData(homeDataModel, false);
            }
        }
    }


    private void getVersionData() {
        //progressBarMain.setVisibility(View.VISIBLE);
        if (getActivity() == null)
            return;
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getActivity(), ApiRequest.VERSION_URL), new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));

                                Tracer.error("Version_api_response", str);

                                final Version version = new Gson().fromJson(str.trim(), Version.class);
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(version);
                                    }
                                });
                                if (!realm.isClosed())
                                    realm.close();

                                Log.d(this.getClass().getName(), "dash version changed or not==>>> " + VersionUtils.getIsHomeVersionChanged1(parentContext, version));
                                //handleDataVersions(str);
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                        }

                        //getContentData(false);
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("HomeFragment", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
                params.put("device", "android");
                params.put("user_id", userID);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void getContentData(final boolean isLoadMoreRequest) {
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
                                AppUtils.generateUrl(getActivity(), ApiRequest.HOME_URL), new Response.Listener<String>() {
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
//                                                Log.d(this.getClass().getName(), "response for home=======" + response);
//                                                Log.d(TAG, response);
                                                if (!isLoadMoreRequest) {
                                                    Utilities.writeToFile(response, "home.txt");
                                                }
                                                homeDataModel = Json.parse(response.trim(), Home.class);

//                                                Utilities.writeObjectToFile(homeDataModel, "home.ser");


                                                sharedPreference.setPreferencesString(getActivity(), DISPLAY_CATEGORY_OFFSET_KEY, "" + homeDataModel.display_offset);
                                                sharedPreference.setPreferencesString(getActivity(), DISPLAY_CATEGORY_COUNT_KEY, "" + homeDataModel.display_count);

                                                ((Activity) parentContext).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showDisplayPlaylistData(homeDataModel, isLoadMoreRequest);
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

                                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
//                                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
//                                params.put("m_filter", "");

                                params.put("device", "android");
                                params.put("content_count", "15");
                                params.put("display_offset", "" + offset);
                                params.put("display_limit", "3");
                                params.put("user_id", userID);
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

  /*  private void handleDataVersions(final String versionApiResponse) {
        SharedPreference sharedPreference = new SharedPreference();

        sharedPreference.setPreferencesString(parentContext, "VERSION", versionApiResponse.trim());
    }*/

    private void showDisplayPlaylistData(Home homeMetaData, boolean isLoadMoreRequest) {
        if (homeMetaData == null)
            return;
        if (!isLoadMoreRequest) {
            SectionDataModel dataModel;
            if (homeMetaData.dashboard != null && homeMetaData.dashboard.feature_banner != null && homeMetaData.dashboard.feature_banner.size() > 0) {
                initilizeMainRecyclerView(true);
                dataModel = new SectionDataModel("", "", new ArrayList<>(homeMetaData.dashboard.feature_banner));
                sectionList.add(dataModel);

            } else {
                initilizeMainRecyclerView(false);
            }
            if (homeMetaData.recomended != null && homeMetaData.recomended.size() > 0) {
                dataModel = new SectionDataModel("Recommended", "-1", homeMetaData.recomended);
                sectionList.add(dataModel);
            }
            if (persistanceDataList != null && persistanceDataList.size() > 0) {
                dataModel = new SectionDataModel("Continue Watching", "-2", persistanceDataList);
                sectionList.add(dataModel);
            }


        }
        if (homeMetaData.dashboard != null && homeMetaData.dashboard.home_category != null && homeMetaData.dashboard.home_category.size() > 0)
            for (int i = 0; i < homeMetaData.dashboard.home_category.size(); i++) {
                Home_category playlistParentObj = homeMetaData.dashboard.home_category.get(i);
                SectionDataModel dataModel = new SectionDataModel(playlistParentObj.cat_name, playlistParentObj.cat_id, new ArrayList<>(playlistParentObj.cat_cntn));
                sectionList.add(dataModel);
            }
        if (progressBarMain != null && progressBarMain.isShown())
            progressBarMain.setVisibility(View.GONE);
        if (bottomProgressBar != null && bottomProgressBar.isShown())
            bottomProgressBar.setVisibility(View.GONE);
        mainSectionAdapter.notifyDataSetChanged();
        isLoading = false;

    }

    private void initilizeMainRecyclerView(boolean isFeatureAvailable) {
        sectionList = new ArrayList<>();
        mainSectionAdapter = new MainRecyclerAdapter(parentContext, sectionList, isFeatureAvailable,
                "verticle_rectangle", "Home");
        if (channelsData != null) {
            mainSectionAdapter.setLiveChannels(channelsData);
        }
        homeContentRecycler.setHasFixedSize(true);
        homeContentRecycler.setLayoutManager(new LinearLayoutManager(parentContext, LinearLayoutManager.VERTICAL, false));
        homeContentRecycler.setAdapter(mainSectionAdapter);
        homeContentRecycler.addOnScrollListener(scrollListener);
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            totalItemCount = recyclerView.getAdapter().getItemCount();

            try {
//                lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            } catch (Exception e) {
                Tracer.error("Error", "onScrolled: EXCEPTION " + e.getMessage());
                lastVisibleItem = 0;
            }

            if (!isLoading && totalItemCount == (lastVisibleItem + 1)) {
                String offsetTemp = sharedPreference.getPreferencesString(parentContext, DISPLAY_CATEGORY_OFFSET_KEY);
                String displayCount = sharedPreference.getPreferencesString(parentContext, DISPLAY_CATEGORY_COUNT_KEY);
                if (offsetTemp != null && offsetTemp.trim().length() > 0) {
                    offset = Integer.parseInt(offsetTemp);
                }
                if (displayCount != null && displayCount.trim().length() > 0) {
                    if (offset < Integer.parseInt(displayCount)) {
                        if (bottomProgressBar != null)
                            bottomProgressBar.setVisibility(View.VISIBLE);
                        getContentData(true);
                        isLoading = true;
                    }
                }


            }
        }
    };


    @Override
    public void onLiveChannelApiResponse(final ChannelsData channelsData) {
        this.channelsData = channelsData;
        if (mainSectionAdapter != null && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainSectionAdapter.setLiveChannels(channelsData);
                    mainSectionAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onLiveChannelApiFailure() {

    }
}
