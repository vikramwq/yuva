
package com.multitv.yuv.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.GetCallback;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.PagerAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.ChannelsData;
import com.multitv.yuv.models.categories.Category;
import com.multitv.yuv.models.categories.Vod;
import com.multitv.yuv.models.menu.MenuModel;
import com.multitv.yuv.models.versions.Version;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.multitv.yuv.utilities.VersionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.multitv.yuv.utilities.Constant.EXTRA_SHOW_LIVE_TAB;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {
    private static final String TAG = AppConfig.BASE_TAG + ".BaseFragment";
    private static final int DELAY = 1500;
    private static final int DEMO_THRASH_HOLD = 1500;
    private OnTabselectedListener mListener;
    private Category category;
    String str;

    public BaseFragment() {
        // Required empty public constructor
    }

    private PagerAdapter adapter;
    private ViewPager pager;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

//    @BindView(R.id.base_fragment_listView_menu_option)
//    ListView mListView;

    @BindView(R.id.base_fragment_shadow)
    View mDarkBackground;


    private Context parentContext;
    private Version version;

    private MenuModel menuModel;
    private String userID;
    private SharedPreference sharedPreference;
  /*  private static int previouslySelectedFragmentPosition = -1;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_base, container, false);

        userID = new SharedPreference().getPreferencesString(parentContext, "user_id" + "_" + ApiRequest.TOKEN);
        ButterKnife.bind(this, rootview);
        pager = (ViewPager) rootview.findViewById(R.id.view_pager);
        sharedPreference = new SharedPreference();

        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getVersionData();
    }


    private void handleDataVersions(final String versionApiResponse) {
        SharedPreference sharedPreference = new SharedPreference();

        sharedPreference.setPreferencesString(parentContext, "VERSION", versionApiResponse.trim());
    }


    private void getVersionData() {

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
                                com.multitv.cipher.MultitvCipher mcipher = new com.multitv.cipher.MultitvCipher();
                                str = new String(mcipher.decryptmyapi(mObj.optString("result")));

                                Tracer.error("Version_api_response", str);

                                version = new Version();

                                JSONObject jsonObject = new JSONObject(str);
                                version.dash_version = jsonObject.getString("dash_version");
                                version.content_version = jsonObject.getString("content_version");
                                version.menu_version = jsonObject.getString("menu_version");
                                version.category_version = jsonObject.getString("category_version");


                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                        }


                        Log.d(this.getClass().getName(), "dash version======" + version.dash_version);

                        Log.d(this.getClass().getName(), "dash version changed or not==>>> " + VersionUtils.getIsHomeVersionChanged1(parentContext, version));

                        getCategories(VersionUtils.getIsHomeVersionChanged1(parentContext, version), VersionUtils.getIsContentVersionChanged(parentContext, version));

                        handleDataVersions(str);
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("HomeFragment", "Error: " + error.getMessage());
                getCategories(true, true);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
                params.put("device", "android");
                params.put("user_id", userID);
                return Utilities.checkParams(params);
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentContext = context;
        if (context instanceof OnTabselectedListener) {
            mListener = (OnTabselectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Subscribe
    public void onEvent(ChannelsData channelsData) {
        if (pager != null)
            pager.setCurrentItem(5);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void intializeViewPager(ArrayList<Vod> menuModel, boolean isDashVersionChanged, boolean isContentVersionChanged) {
        FragmentManager manager = getChildFragmentManager();

        if (menuModel != null && menuModel.size() != 0) {
            if (getActivity() == null)
                return;

            adapter = new PagerAdapter(manager, getActivity(), menuModel, isDashVersionChanged, isContentVersionChanged);
            pager.setAdapter(adapter);
            tabLayout.setupWithViewPager(pager);
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setTabsFromPagerAdapter(adapter);
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");
            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();
                for (int i = 0; i < tabChildsCount; i++) {
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof TextView) {
                        ((TextView) tabViewChild).setTypeface(tf);
                    }
                }
            }
            pager.setOffscreenPageLimit(menuModel.size());


            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    Tracer.error(TAG, "onPageSelected: " + position);



                    /*previouslySelectedFragmentPosition = position;*/
                    if (getActivity() == null)
                        return;
                    PreferenceData.setPreviousFragmentSelectedPosition(getActivity(), position);

                    adapter.notifyDataSetChanged(); //t
                    Tracer.debug("position", String.valueOf(position));
                    mListener.onTabselected(position);
                    switch (position) {

                        case 1:
                            //like a example
                            //  setViewPagerByIndex(0);
                            break;
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            if (getActivity() == null)
                return;
            int previouslySelectedFragmentPosition = PreferenceData.getPreviousFragmentSelectedPosition(getActivity());

            /*if (previouslySelectedFragmentPosition != -1) {*/
            pager.setCurrentItem(previouslySelectedFragmentPosition);
                /*previouslySelectedFragmentPosition = -1;*/
                /*return;*/
            /*}*/

            if (getArguments() != null && getArguments().getBoolean(EXTRA_SHOW_LIVE_TAB, false)) {
                loadFragment("live", null);
            }
        }
    }


    /**
     * Method to load the current selecetd fragment
     *
     * @param fragmentId
     * @param iconUrl
     */
    public void loadFragment(String fragmentId, String iconUrl) {
        try {
            int fragmentIndex = findFragmentIndex(fragmentId);
            pager.setCurrentItem(fragmentIndex);
            tabLayout.setVisibility(fragmentIndex == 0 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            Tracer.error(TAG, "loadFragment: " + e.getMessage());
        }
    }

    public boolean isHomeTabOnTop() {
        try {
            return pager.getCurrentItem() == 0;
        } catch (Exception e) {
            Tracer.error(TAG, "isHomeTabOnTop: " + e.getMessage());
        }
        return false;
    }

    public void loadHomeFragment() {
        try {
            pager.setCurrentItem(0, false);
        } catch (Exception e) {
            Tracer.error(TAG, "isHomeTabOnTop: " + e.getMessage());
        }
    }

    private int findFragmentIndex(String fragmentId) {
        try {
            if (menuModel != null && menuModel.topMenuArrayList != null && menuModel.topMenuArrayList.size() > 0) {
                for (int i = 0; i < menuModel.topMenuArrayList.size(); i++) {
                    MenuModel.TopMenu item = menuModel.topMenuArrayList.get(i);
                    if (item.identifier.trim().equalsIgnoreCase(fragmentId.trim())) {
                        return i;
                    }
                }
            }
        } catch (Exception e) {
            Tracer.error(TAG, "loadFragment: " + e.getMessage());
        }
        return 0;
    }

    public interface OnTabselectedListener {
        // TODO: Update argument type and name
        void onTabselected(int position);

        void onCategory(String categoryid);
    }

    private void getCategories(final boolean isDashVersionChanged, final boolean isContentVersionChanged) {

        final String key = "VideoTab";
        final Type homeObjectType = new TypeToken<Category>() {
        }.getType();
        AppController.getInstance().getCacheManager().getAsync(key, Category.class, homeObjectType, new GetCallback() {
            @Override
            public void onSuccess(final Object object) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        category = (Category) object;
                        if (getActivity() == null)
                            return;
                        if (VersionUtils.getIsCategoryVersionChanged(getActivity(), category, key))
                            category = null;

                        if (category != null) {
                            Tracer.error("CacheManager", key + " object retrieved successfully");

                            if (getActivity() == null)
                                return;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<Vod> list = ((Category) object).vod;

                                    Vod vod = new Vod();
                                    vod.name = getString(R.string.channels);
                                    list.add(vod);


                                    intializeViewPager(new ArrayList<Vod>(list), isDashVersionChanged, isContentVersionChanged);
                                    mListener.onTabselected(0);

                                    mDarkBackground.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else if (!ConnectionManager.getInstance(getActivity()).isConnected()) {
                            Tracer.error(TAG, "ConnectionManager Not Connected");
                        } else {
                            getCategoriesFromServer(isDashVersionChanged, isContentVersionChanged);
                        }
                    }
                }).start();
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionUtils.printStacktrace(e);
            }
        });
    }


    private void getCategoriesFromServer(final boolean isDashVersionChanged, final boolean isContentVersionChanged) {
        final String key = "VideoTab";
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.VIDEO_CATEGORY), new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Tracer.error("categoryName---", response.toString());
                        try {
                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));

                                Log.d(this.getClass().getName(), "category data=======>>> " + str);
                                Tracer.error("decode_category---", str);

                                category = Json.parse(str.trim(), Category.class);
                                Tracer.error(TAG, "onResponse:LIST SIZE : " + category.vod.size());
                                Tracer.debug("category_list--", str.toString());

                                AppController.getInstance().getCacheManager().put(key, category);

                                if (getActivity() == null)
                                    return;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (category != null) {
                                            List<Vod> list = category.vod;

                                            Vod vod = new Vod();
                                            vod.name = getString(R.string.channels);
                                            list.add(vod);


                                            intializeViewPager(new ArrayList<Vod>(list), isDashVersionChanged, isContentVersionChanged);
                                            mListener.onTabselected(0);
                                            mDarkBackground.setVisibility(View.INVISIBLE);
                                        } else {
                                            getActivity().finish();
                                        }
                                    }
                                });
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
                Tracer.error("video", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("device", "android");

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}


