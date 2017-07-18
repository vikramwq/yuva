package com.multitv.yuv.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.customview.RecyclerItemClickListener;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.utilities.Adutils;
import com.multitv.yuv.utilities.ExceptionHandler;
import com.multitv.yuv.utilities.VersionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.GetCallback;
import com.iainconnor.objectcache.PutCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.multitv.yuv.adapter.MoreHomeDisplayCategoryAdapter;
import com.multitv.yuv.adapter.MoreLiveAdapter;
import com.multitv.yuv.adapter.MoreRecommendedAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.home.Live;
import com.multitv.yuv.models.live.LiveParent;
import com.multitv.yuv.models.recommended.Recommended;
import com.multitv.yuv.models.sony.Sony;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

import static com.multitv.yuv.R.id.progressBar1;
import static com.multitv.yuv.utilities.Constant.EXTRA_CATEGORY_TYPE;
import static com.multitv.yuv.utilities.Constant.EXTRA_SEARCH_KEYWOARD;
import static com.multitv.yuv.utilities.Constant.IS_FROM_SEARCH;

public class MoreDataActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private final int SPEECP_REQUEST_CODE = 12345;
    private final String TAG = "MoreDataActivity";
    @BindView(R.id.top_relative_layout)
    RelativeLayout topRelativeLayout;
    @BindView(R.id.load_recyler_view)
    RecyclerView moreRecylerView;
    @BindView(R.id.activity_more_data_dropdown_lang)
    TextView mTextViewLanguage;
    @BindView(R.id.activity_more_data_dropdown_price)
    TextView mTextViewPricing;
    @BindView(R.id.activity_more_data_dropdown_provider)
    TextView mTextViewContentProvider;
    @BindView(R.id.progressBar2)
    ProgressBar mProgress_bar_top;
    @BindView(R.id.progress_bar_search)
    ProgressBar mProgress_bar_search;
    @BindView(R.id.more_frame)
    ViewGroup more_frame_layout;
    @BindView(R.id.search_frame)
    ViewGroup search_frame_layout;
    @BindView(R.id.search_count_textview)
    TextView searchCountTextview;
    @BindView(R.id.search_recylerview)
    RecyclerView searchRecylerview;
    @BindView(R.id.empty)
    LinearLayout noRecordFoundTextview;
    private int tag;
    private String query = "";
    private int current_page = 0;
    private Video video;
    private MoreHomeDisplayCategoryAdapter moreHomeDisplayCategoryAdapter;
    private LiveParent liveParent;
    private long watchedDuration;

    private List<Live> liveSearchArrayList = new ArrayList<>();

    private Recommended recommended;
    private MoreRecommendedAdapter recommendedAdapter;
    private List<Cat_cntn> recommendArrayList = new ArrayList<>();

    private List<Cat_cntn> displayArrayList = new ArrayList<>();

    private Sony sony;
    private String user_id;
    private Intent intent;
    private String cat_id;
    private String cat_type;
    private int count = 0;
    private ProgressBar progressbar1;
    private SharedPreference sharedPreference;
    private int itemsLeft = -1, startCount = 0, endCount = 0;
    private boolean hasSearch_focus = false;
    private Video videoDataSaved;
    private Recommended recommendedSaved;
    private Adutils mAdutils;
    private MoreLiveAdapter moreLiveAdapter, moreLiveSearchAdapter;
    private boolean isComingFromSearchActivity;
    private ListPopupWindow mPopupWindowLangauge, mPopupWindowContentProvider, mPopupWindowPricing, mListPopupWindowCurrent;
    private boolean mIsFirstTime;
    private SearchView searchView;
    private boolean isLoggedIn, isOTPVerified;

    public void setWatchedDuration(long watchedDuration) {
        this.watchedDuration = watchedDuration;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tracer.error("MKR", "MoreDataActivity.onCreate() ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }

        setContentView(R.layout.activity_more_data_parent);

        ExceptionHandler.attach();

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utilities.applyFontForToolbarTitle(MoreDataActivity.this);

        isComingFromSearchActivity = getIntent() != null && getIntent().getBooleanExtra(IS_FROM_SEARCH, false);

        if (isComingFromSearchActivity) {
            initDropDownPopup();
            query = getIntent().getStringExtra(EXTRA_SEARCH_KEYWOARD);
        } else {
            hideDropDownPopup();
        }

        user_id = new SharedPreference().getPreferencesString(MoreDataActivity.this, "user_id" + "_" + ApiRequest.TOKEN);
        sharedPreference = new SharedPreference();

        isLoggedIn = sharedPreference.getPreferenceBoolean(MoreDataActivity.this, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(MoreDataActivity.this, sharedPreference.KEY_IS_OTP_VERIFIED);

        progressbar1 = (ProgressBar) findViewById(progressBar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        noRecordFoundTextview = (LinearLayout) findViewById(R.id.empty);
        intent = getIntent();
        tag = intent.getIntExtra("more_data_tag", 0);
        Tracer.error(TAG, "onCreate: TAG " + tag);
        switch (tag) {
            case 1:
                getSupportActionBar().setTitle("Recommended");
                String offsetString = sharedPreference.getPreferencesString(this, "offset_" + "More_ Recommended");
                if (!TextUtils.isEmpty(offsetString))
                    count = Integer.parseInt(offsetString);

                getRecommended(current_page);

                LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                moreRecylerView.setLayoutManager(gridLayoutManager);
                recommendedAdapter = new MoreRecommendedAdapter(MoreDataActivity.this, recommendArrayList, moreRecylerView, true);
                moreRecylerView.setAdapter(recommendedAdapter);
                recommendtdShowData(false);
                recommendedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        current_page = current_page + 1;
                        Tracer.debug("more data", "" + current_page);
                        count = recommended.offset;

                        if (!hasSearch_focus) {
                            getRecommended(current_page);
                        }
                    }
                });
                break;

            case 4:
                Tracer.error(TAG, " SWITCH.onCreate: " + isComingFromSearchActivity);
                cat_id = intent.getStringExtra("cat_id");
                cat_type = intent.getStringExtra(EXTRA_CATEGORY_TYPE);
                String catName = intent.getStringExtra("catName");
                getSupportActionBar().setTitle(catName);
                if (!isComingFromSearchActivity) {
                    LinearLayoutManager gridLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    moreRecylerView.setLayoutManager(gridLayoutManager1);
                    moreHomeDisplayCategoryAdapter = new MoreHomeDisplayCategoryAdapter(MoreDataActivity.this, displayArrayList, moreRecylerView);
                    moreRecylerView.setAdapter(moreHomeDisplayCategoryAdapter);
//                    displayCategoryDataShow();

                    offsetString = sharedPreference.getPreferencesString(this, "offset_" + "More_Home_Category" + cat_id);
                    if (!TextUtils.isEmpty(offsetString))
                        count = Integer.parseInt(offsetString);

                    getHomeCatData(cat_id, current_page);

                    moreHomeDisplayCategoryAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            current_page = current_page + 1;
                            Tracer.debug("more data", "" + current_page);
                            count = video.offset;

                            if (!hasSearch_focus)
                                getHomeCatData(cat_id, current_page);
                        }
                    });
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(MoreDataActivity.this, LinearLayoutManager.VERTICAL, false);
                    searchRecylerview.setLayoutManager(mLayoutManager);
                    recommendedAdapter = new MoreRecommendedAdapter(MoreDataActivity.this, recommendArrayList, searchRecylerview, true);
                    searchRecylerview.setAdapter(recommendedAdapter);
                    recommendedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            current_page = current_page + 1;
                            Tracer.debug("more data", "" + current_page);
                            if (recommended != null) {
                                count = recommended.offset;
                            } else {
                                count = 0;
                            }
                            searchDataLoad(query);
                        }
                    });
                    recommendtdShowData(true);
                } else {
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(MoreDataActivity.this, LinearLayoutManager.VERTICAL, false);
                    searchRecylerview.setLayoutManager(mLayoutManager);
                    recommendedAdapter = new MoreRecommendedAdapter(MoreDataActivity.this, recommendArrayList, searchRecylerview, true);
                    searchRecylerview.setAdapter(recommendedAdapter);
                    recommendedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            current_page = current_page + 1;
                            Tracer.debug("more data", "" + current_page);
                            if (recommended != null) {
                                count = recommended.offset;
                            } else {
                                count = 0;
                            }
                            searchDataLoad(query);
                        }
                    });
                    recommendtdShowData(true);
                    searchDataLoad(query);
                }

                break;


        }
        mAdutils = new Adutils(this, Adutils.getUsedSdk(this));
        showBannerAd();
        mTextViewLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListPopupWindowCurrent != null && mListPopupWindowCurrent.isShowing()) {
                    if (mListPopupWindowCurrent.equals(mPopupWindowLangauge)) {
                        mListPopupWindowCurrent.dismiss();
                        mListPopupWindowCurrent = null;
                    } else {
                        mListPopupWindowCurrent.dismiss();
                        mListPopupWindowCurrent = mPopupWindowLangauge;
                        mListPopupWindowCurrent.show();
                    }
                } else {
                    mListPopupWindowCurrent = mPopupWindowLangauge;
                    mListPopupWindowCurrent.show();
                }
            }
        });
        mTextViewPricing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListPopupWindowCurrent != null && mListPopupWindowCurrent.isShowing()) {
                    if (mListPopupWindowCurrent.equals(mPopupWindowPricing)) {
                        mListPopupWindowCurrent.dismiss();
                        mListPopupWindowCurrent = null;
                    } else {
                        mListPopupWindowCurrent.dismiss();
                        mListPopupWindowCurrent = mPopupWindowPricing;
                        mListPopupWindowCurrent.show();
                    }
                } else {
                    mListPopupWindowCurrent = mPopupWindowPricing;
                    mListPopupWindowCurrent.show();
                }
            }
        });
        mTextViewContentProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListPopupWindowCurrent != null && mListPopupWindowCurrent.isShowing()) {
                    if (mListPopupWindowCurrent.equals(mPopupWindowContentProvider)) {
                        mListPopupWindowCurrent.dismiss();
                        mListPopupWindowCurrent = null;
                    } else {
                        mListPopupWindowCurrent.dismiss();
                        mListPopupWindowCurrent = mPopupWindowContentProvider;
                        mListPopupWindowCurrent.show();
                    }
                } else {
                    mListPopupWindowCurrent = mPopupWindowContentProvider;
                    mListPopupWindowCurrent.show();
                }
            }
        });
//        initSuggestionList();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search); // get my MenuItem with placeholder submenu
        searchMenuItem.setVisible(true);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (!mIsFirstTime) {
                    mIsFirstTime = true;
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (isComingFromSearchActivity) {
                    finish();
                }
                return true;
            }
        });

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        /*if (tag != 5) {*/
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            ExceptionUtils.printStacktrace(e);
        }

        searchTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    more_frame_layout.setVisibility(View.GONE);
                    hasSearch_focus = true;
                } else {
                    if (!isComingFromSearchActivity) {
                        searchMenuItem.collapseActionView();
                        more_frame_layout.setVisibility(View.VISIBLE);
                        hasSearch_focus = false;
                    }
                }
            }
        });

        searchTextView.setOnEditorActionListener(this);

        if (isComingFromSearchActivity) {
            searchMenuItem.setVisible(true);
            searchMenuItem.setEnabled(true);
            searchMenuItem.expandActionView();
            searchView.setQuery(query, false);
            if (query != null && query.trim().length() > 0) {
                searchView.clearFocus();
            } else {
                searchView.requestFocus();
            }
        } else {
            searchView.requestFocus();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String searchquery) {
                // query=searchquery;

                System.out.println("=========searchquery======" + searchquery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                System.out.println("=========searchquery======" + searchQuery);
                query = searchQuery.trim();

                if (searchQuery.length() == 0) {
                    if (recommended != null && recommended.content != null && !recommended.content.isEmpty()) {
                        recommended.content.clear();
                        if (recommendedAdapter != null) {
                            recommendedAdapter.notifyDataSetChanged();
                        }
                    }

                    if (liveSearchArrayList != null && !liveSearchArrayList.isEmpty()) {
                        liveSearchArrayList.clear();
                        moreLiveAdapter.notifyDataSetChanged();
                    }


                    searchCountTextview.setVisibility(View.GONE);
                    noRecordFoundTextview.setVisibility(View.GONE);
                }

                if (query.length() > 2) {
                    requestSuggestion();
                } else {
                    hideListViewSuggestion();
                }
                return true;
            }
        });
       /* } else {
            searchMenuItem.setVisible(false);
            searchMenuItem.setEnabled(false);
        }*/
        Tracer.error(TAG, "onCreateOptionsMenu:2 " + getIntent().getStringExtra(EXTRA_SEARCH_KEYWOARD));
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard();
            count = 0;
            current_page = 0;
            if (recommended != null) {
                recommended.offset = 0;
                recommendArrayList.clear();
                recommended = null;
            }
            if (liveParent != null) {
                liveParent.offset = 0;
                liveSearchArrayList.clear();
                liveParent = null;
            }
            searchCountTextview.setVisibility(View.GONE);
            noRecordFoundTextview.setVisibility(View.GONE);
            searchRecylerview.setVisibility(View.GONE);

            switch (tag) {

                case 1:
                    //  get Recommended Search
                    searchDataLoad(query);
                    break;

                case 2:
                    //  get Live Channal Data Search
                    searchLiveData(query);
                    break;

                default:
                    searchDataLoad(query);
                    break;
            }


            return true;
        }
        return false;
    }


    /**
     * Method to hide keyboard
     */
    private void hideKeyboard() {
      /*  try {
            searchView.clearFocus();
        } catch (Exception e) {
            Tracer.error(TAG, "run: " + e.getMessage());
        }*/
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        } catch (Exception e) {
            Tracer.error(TAG, "run: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECP_REQUEST_CODE && resultCode == RESULT_OK && searchView != null) {
            ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
                searchView.setQuery(stringArrayListExtra.get(0), false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdutils.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdutils.onPause();
    }

    /**
     * Method to show the Banner Ad
     */
    private void showBannerAd() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.banner_ad_container);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mAdutils.showBanner(relativeLayout, layoutParams);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


    private void getRecommended(final int current_page) {
        if (!ConnectionManager.getInstance(MoreDataActivity.this).isConnected()) {
            return;
        }
        if (current_page > 0) {
            progressbar1.setIndeterminate(true);
            progressbar1.setVisibility(View.VISIBLE);
        } else
            mProgress_bar_top.setVisibility(View.VISIBLE);

        final String key = "More" + "_" + "Recommended";
        final Type objectType = new TypeToken<Recommended>() {
        }.getType();
        AppController.getInstance().getCacheManager().getAsync(key, Recommended.class, objectType, new GetCallback() {
            @Override
            public void onSuccess(Object object) {
                recommendedSaved = (Recommended) object;

                if (VersionUtils.getIsContentVersionChanged(MoreDataActivity.this, recommendedSaved, key)) {
                    recommendedSaved = null;
                    count = 0;
                }

                if (recommendedSaved != null && recommendedSaved.content != null && recommendedSaved.content.size() != 0 &&
                        (count <= recommendedSaved.content.size() || count > recommendedSaved.totalCount)
                        && ((itemsLeft == -1 && recommendArrayList.size() == 0) || itemsLeft > 0)) {
                    Tracer.error("CacheManager", key + " object retrieved successfully");

                    recommended = recommendedSaved;

                    if (current_page > 0)
                        progressbar1.setVisibility(View.GONE);
                    else
                        mProgress_bar_top.setVisibility(View.GONE);

                    if (recommendArrayList.size() < recommendedSaved.content.size()) {
                        if (itemsLeft == -1) {
                            recommendArrayList.clear();
                            itemsLeft = recommendedSaved.content.size();
                        }

                        if (itemsLeft == recommendedSaved.content.size())
                            startCount = 0;
                        else
                            startCount = startCount + 10;

                        endCount = startCount + 10;
                        if (endCount > recommendedSaved.content.size())
                            endCount = recommendedSaved.content.size();

                        for (int i = startCount; i < endCount; i++) {
                            recommendArrayList.add(recommendedSaved.content.get(i));
                        }

                        itemsLeft = itemsLeft - 10;

                        handleRecommendedPagination(false);
                    }
                } else if (!ConnectionManager.getInstance(MoreDataActivity.this).isConnected()) {
                    Tracer.error(TAG, "ConnectionManager Not Connected");
                } else if (recommendedSaved == null || count <= recommendedSaved.totalCount) {
                    Tracer.debug("Recommended Url", AppUtils.generateUrl(getApplicationContext(), ApiRequest.RECOMMENDED_LIST));
                    StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                            AppUtils.generateUrl(getApplicationContext(), ApiRequest.RECOMMENDED_LIST), new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Tracer.error("Recommended_responce---", response.toString());

                            if (current_page > 0) {
                                progressbar1.setVisibility(View.GONE);
                            } else {
                                mProgress_bar_top.setVisibility(View.GONE);
                            }

                            try {
                                JSONObject mObj = new JSONObject(response);
                                if (mObj.optInt("code") == 1) {
                                    MultitvCipher mcipher = new MultitvCipher();
                                    String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                    Tracer.error("Recommended response---", str);
                                    try {
                                        JSONObject newObj = new JSONObject(str);
                                        recommended = Json.parse(newObj.toString(), Recommended.class);

                                        sharedPreference.setPreferencesString(MoreDataActivity.this, "offset_" + "More_ Recommended", "" + recommended.offset);

                                        if (recommendedSaved != null) {
                                            recommendedSaved.offset = recommended.offset;
                                            recommendedSaved.content.addAll(recommended.content);
                                            AppController.getInstance().getCacheManager().putAsync(key, recommendedSaved, new PutCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Tracer.error("CacheManager", key + " object saved successfully");
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    ExceptionUtils.printStacktrace(e);
                                                }
                                            });
                                        } else {
                                            AppController.getInstance().getCacheManager().putAsync(key, recommended, new PutCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Tracer.error("CacheManager", key + " object saved successfully");
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    ExceptionUtils.printStacktrace(e);
                                                }
                                            });
                                        }

                                        // Tracer.debug("Recommended", newObj.toString());
                                    } catch (JSONException e) {
                                        Tracer.debug("Recommended", "JSONException");
                                        ExceptionUtils.printStacktrace(e);
                                    }

                                    //recommendtdShowData(recommended.content);
                                    handleRecommendedPagination(true);
                                }

                            } catch (Exception e) {
                                ExceptionUtils.printStacktrace(e);
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Tracer.debug("Error", "Error: " + error.getMessage());

                            if (current_page > 0)
                                progressbar1.setVisibility(View.GONE);
                            else
                                mProgress_bar_top.setVisibility(View.GONE);

                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();

                            params.put("lan", LocaleHelper.getLanguage(MoreDataActivity.this));
//                            params.put("m_filter", (PreferenceData.isMatureFilterEnable(MoreDataActivity.this) ? "" + 1 : "" + 0));

                            params.put("device", "android");
                            params.put("user_id", user_id);
                            params.put("current_offset", String.valueOf(count));
                            params.put("max_counter", "10");
                            return params;
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq);
                } else {
                    if (current_page > 0)
                        progressbar1.setVisibility(View.GONE);
                    else
                        mProgress_bar_top.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleRecommendedPagination(boolean isFromApiRequest) {
        refreshListRecemended(recommended.content);

        if (isFromApiRequest) {
            if (recommended != null && recommended.content != null && recommended.content.size() != 0) {
                recommendArrayList.addAll(recommended.content);
            }
        }

        if (recommendArrayList != null && recommendArrayList.size() != 0) {
           /* recommendedAdapter.setVideoList(recommendArrayList);
            recommendedAdapter.setLoaded();*/
            recommendedAdapter.setLoaded();
            recommendedAdapter.notifyDataSetChanged();
        }
    }

    private void refreshListRecemended(List<Cat_cntn> recommendArrayList) {

        if (recommendArrayList.size() > 0) {
            int lastIndex = recommendArrayList.size() - 1;
            if (recommendArrayList.get(lastIndex) == null) {
                recommendArrayList.remove(lastIndex);
                recommendedAdapter.notifyItemRemoved(lastIndex);
            }

        }
    }

    private void recommendtdShowData(boolean isFromSearching) {
        RecyclerView recyclerView;
        if (!isFromSearching)
            recyclerView = moreRecylerView;
        else
            recyclerView = searchRecylerview;

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (isOTPVerified) {
                            if (recommendArrayList != null && recommendArrayList.size() > 0) {
                                String VIDEO_URL = recommendArrayList.get(position).url;
                                try {
                                    if (VIDEO_URL != null && !VIDEO_URL.equals("")) {
                                        Intent videoIntent = new Intent(MoreDataActivity.this, MultiTvPlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, recommendArrayList.get(position));
                                        videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                                        videoIntent.putExtra("WATCHED_DURATION", watchedDuration);

                                        startActivity(videoIntent);
                                    } else {
                                        Toast.makeText(MoreDataActivity.this, "No Record Found", Toast.LENGTH_LONG).show();
                                    }


                                } catch (Exception ex) {
                                    Tracer.error("error", ex.getMessage());
                                }

                            }

                        } else {
                            Utilities.showLoginDailog(MoreDataActivity.this);
                        }

                    }
                })
        );

    }


    private void getHomeCatData(final String cat_id, final int current_page) {

        if (current_page > 0) {
            progressbar1.setIndeterminate(true);
            progressbar1.setVisibility(View.VISIBLE);
        } else {
            mProgress_bar_top.setVisibility(View.VISIBLE);
        }
        Tracer.error("home_Cat_Data", AppUtils.generateUrl(getApplicationContext(), ApiRequest.NEW_SEARCH_URL));
        final String key = "More" + "_" + "cat" + "_" + cat_id;
        final Type objectType = new TypeToken<Video>() {
        }.getType();
        AppController.getInstance().getCacheManager().getAsync(key, Video.class, objectType, new GetCallback() {
            @Override
            public void onSuccess(Object object) {
                videoDataSaved = (Video) object;

                if (VersionUtils.getIsContentVersionChanged(MoreDataActivity.this, videoDataSaved, key)) {
                    videoDataSaved = null;
                    count = 0;
                }

                if (videoDataSaved != null && videoDataSaved.content != null && videoDataSaved.content.size() != 0 &&
                        (count <= videoDataSaved.content.size() || count > videoDataSaved.totalCount)
                        && ((itemsLeft == -1 && displayArrayList.size() == 0) || itemsLeft > 0)) {
                    Tracer.error("CacheManager", key + " object retrieved successfully");

                    video = videoDataSaved;

                    if (current_page > 0)
                        progressbar1.setVisibility(View.GONE);
                    else
                        mProgress_bar_top.setVisibility(View.GONE);

                    if (displayArrayList.size() < videoDataSaved.content.size()) {
                        if (itemsLeft == -1) {
                            displayArrayList.clear();
                            itemsLeft = videoDataSaved.content.size();
                        }

                        if (itemsLeft == videoDataSaved.content.size())
                            startCount = 0;
                        else
                            startCount = startCount + 10;

                        endCount = startCount + 10;
                        if (endCount > videoDataSaved.content.size())
                            endCount = videoDataSaved.content.size();

                        for (int i = startCount; i < endCount; i++) {
                            displayArrayList.add(videoDataSaved.content.get(i));
                        }

                        itemsLeft = itemsLeft - 10;

                        handleCategoryPagination(false);
                    }
                } else if (!ConnectionManager.getInstance(MoreDataActivity.this).isConnected()) {
                    Tracer.error(TAG, "ConnectionManager Not Connected");
                } else if (videoDataSaved == null || count <= videoDataSaved.totalCount) {
                    StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                            AppUtils.generateUrl(getApplicationContext(), ApiRequest.NEW_SEARCH_URL), new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                if (current_page > 0)
                                    progressbar1.setVisibility(View.GONE);
                                else
                                    mProgress_bar_top.setVisibility(View.GONE);

                                JSONObject mObj = new JSONObject(response);

                                MultitvCipher mcipher = new MultitvCipher();
                                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                Tracer.error("more display category", str);
                                Log.d(this.getClass().getName(),"data from server==="+str);
                                video = Json.parse(str.trim(), Video.class);

                                sharedPreference.setPreferencesString(MoreDataActivity.this, "offset_" + "More_Home_Category" + cat_id, "" + video.offset);

                                if (videoDataSaved != null) {
                                    videoDataSaved.offset = video.offset;
                                    videoDataSaved.content.addAll(video.content);
                                    AppController.getInstance().getCacheManager().putAsync(key, videoDataSaved, new PutCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Tracer.error("CacheManager", key + " object saved successfully");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            ExceptionUtils.printStacktrace(e);
                                        }
                                    });
                                } else {
                                    AppController.getInstance().getCacheManager().putAsync(key, video, new PutCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Tracer.error("CacheManager", key + " object saved successfully");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            ExceptionUtils.printStacktrace(e);
                                        }
                                    });
                                }

                                handleCategoryPagination(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Tracer.debug("Error", "Error: " + error.getMessage());

                            if (current_page > 0)
                                progressbar1.setVisibility(View.GONE);
                            else
                                mProgress_bar_top.setVisibility(View.GONE);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();

                            params.put("lan", LocaleHelper.getLanguage(MoreDataActivity.this));
//                            params.put("m_filter", (PreferenceData.isMatureFilterEnable(MoreDataActivity.this) ? "" + 1 : "" + 0));

                            params.put("device", "android");
                            params.put("user_id", user_id);
                            params.put("current_offset", String.valueOf(count));
                            params.put("cat_id", cat_id);
                            params.put("max_counter", "10");
                            params.put("search_tag", query);
                            Set<String> keys = params.keySet();
                            for (String key : keys) {
                                Tracer.error(TAG, "getHomeContent().getParams: " + key + "      " + params.get(key));
                            }
                            return params;
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq);
                } else {
                    if (current_page > 0)
                        progressbar1.setVisibility(View.GONE);
                    else
                        mProgress_bar_top.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleCategoryPagination(boolean isFromApiRequest) {
        refreshHomeList(video.content);

        if (isFromApiRequest) {
            if (video != null && video.content != null && video.content.size() != 0) {
                displayArrayList.addAll(video.content);
            }
        }

        if (displayArrayList != null && displayArrayList.size() != 0) {
           /* moreHomeDisplayCategoryAdapter.setVideoList(displayArrayList);
            moreHomeDisplayCategoryAdapter.setLoaded();*/
            moreHomeDisplayCategoryAdapter.setLoaded();
            moreHomeDisplayCategoryAdapter.notifyDataSetChanged();
        }
    }

    private void refreshHomeList(ArrayList<Cat_cntn> displasyList) {

        if (displasyList.size() > 0) {
            int lastIndex = displasyList.size() - 1;
            if (displasyList.get(lastIndex) == null) {
                displasyList.remove(lastIndex);
                moreHomeDisplayCategoryAdapter.notifyItemRemoved(lastIndex);
            }

        }
    }


    private void searchDataLoad(final String searchQuery) {
        if (TextUtils.isEmpty(searchQuery))
            return;
        if (current_page > 0) {
            progressbar1.setVisibility(View.VISIBLE);
        } else {
            mProgress_bar_search.setVisibility(View.VISIBLE);
        }
        hideListViewSuggestion();
        hideKeyboard();
        if (recommended == null || count < recommended.totalCount) {
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    AppUtils.generateUrl(getApplicationContext(), ApiRequest.NEW_SEARCH_URL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Tracer.error("search response---", response.toString());

                    progressbar1.setVisibility(View.GONE);
                    mProgress_bar_search.setVisibility(View.GONE);

                    try {
                        JSONObject mObj = new JSONObject(response);
                        if (mObj.optInt("code") == 1) {
                            MultitvCipher mcipher = new MultitvCipher();
                            String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                            Tracer.error("search response---", str);
                            try {
                                JSONObject newObj = new JSONObject(str);
                                recommended = Json.parse(newObj.toString(), Recommended.class);

                                if (recommended != null && recommended.content != null && recommended.content.size() != 0)
                                    recommendArrayList.addAll(recommended.content);

                                if (recommendArrayList != null && recommendArrayList.size() != 0) {
                                    searchCountTextview.setVisibility(View.VISIBLE);
                                    searchRecylerview.setVisibility(View.VISIBLE);
                                    noRecordFoundTextview.setVisibility(View.GONE);

                                    searchCountTextview.setText(getResources().getString(R.string.total_result) + recommended.totalCount);
                                } else {
                                    searchCountTextview.setVisibility(View.GONE);
                                    searchRecylerview.setVisibility(View.GONE);
                                    noRecordFoundTextview.setVisibility(View.VISIBLE);
                                }
                                if (recommendedAdapter != null) {
                                    recommendedAdapter.setLoaded();
                                    recommendedAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                Tracer.debug("Recommended", "JSONException");
                                ExceptionUtils.printStacktrace(e);
                            }
                        }

                    } catch (Exception e) {
                        ExceptionUtils.printStacktrace(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Tracer.debug("Error", "Error: " + error.getMessage());

                    progressbar1.setVisibility(View.GONE);
                    mProgress_bar_search.setVisibility(View.GONE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("lan", LocaleHelper.getLanguage(MoreDataActivity.this));
//                    params.put("m_filter", (PreferenceData.isMatureFilterEnable(MoreDataActivity.this) ? "" + 1 : "" + 0));

                    params.put("device", "android");
                    params.put("device_unique_id", "android");
                    params.put("max_counter", "10");
                    params.put("current_offset", "" + count);
                    params.put("cat_id", cat_id != null ? cat_id : "");
                    params.put("cat_type", cat_type != null ? cat_type : "");
                    params.put("search_tag", !TextUtils.isEmpty(searchQuery) ? searchQuery : "");
                    String mSelectedLanguage = mTextViewLanguage.getText().toString().trim();
                    String mSelectedPriceType = mTextViewPricing.getText().toString().trim();
                    String mSelectedContentProvider = mTextViewContentProvider.getText().toString().trim();
                    params.put("lang", mSelectedLanguage == null || mSelectedLanguage.trim().equalsIgnoreCase("ALL") ? "" : mSelectedLanguage.toLowerCase());
                    params.put("cp", mSelectedContentProvider == null || mSelectedContentProvider.trim().equalsIgnoreCase("ALL") ? "" : mSelectedContentProvider.toLowerCase());
                    params.put("price", mSelectedPriceType == null || mSelectedPriceType.trim().equalsIgnoreCase("ALL") ? "" : mSelectedPriceType.toLowerCase());
                    Set<String> keys = params.keySet();
                    for (String key : keys) {
                        Tracer.error(TAG, "searchDataLoad().getParams: " + key + "      " + params.get(key));
                    }
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } else {
            progressbar1.setVisibility(View.GONE);
            mProgress_bar_search.setVisibility(View.GONE);
        }

    }

    private void searchLiveData(final String query) {
        if (TextUtils.isEmpty(query))
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current_page > 0) {
                            progressbar1.setVisibility(View.VISIBLE);
                        } else {
                            mProgress_bar_search.setVisibility(View.VISIBLE);
                        }
                    }
                });

                if (liveParent == null || count <= liveParent.totalcount) {
                    StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                            AppUtils.generateUrl(getApplicationContext(), ApiRequest.LIVE_CHANNEL), new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressbar1.setVisibility(View.GONE);
                                                mProgress_bar_search.setVisibility(View.GONE);
                                            }
                                        });

                                        JSONObject mObj = new JSONObject(response);

                                        MultitvCipher mcipher = new MultitvCipher();
                                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                        Tracer.error("Live channel data", str);
                                        liveParent = Json.parse(str.trim(), LiveParent.class);

                                        if (liveParent != null || liveParent.live != null && liveParent.live.size() == 0) {
                                            liveSearchArrayList.addAll(liveParent.live);
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (liveSearchArrayList != null && liveSearchArrayList.size() != 0) {
                                                    searchCountTextview.setVisibility(View.VISIBLE);
                                                    searchRecylerview.setVisibility(View.VISIBLE);
                                                    noRecordFoundTextview.setVisibility(View.GONE);

                                                    searchCountTextview.setText(getResources().getString(R.string.total_result) + liveSearchArrayList.size());

                                                    moreLiveAdapter.setLoaded();
                                                    moreLiveAdapter.notifyDataSetChanged();
                                                } else {
                                                    searchCountTextview.setVisibility(View.GONE);
                                                    searchRecylerview.setVisibility(View.GONE);
                                                    noRecordFoundTextview.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Tracer.debug("Error", "Error: " + error.getMessage());

                            progressbar1.setVisibility(View.GONE);
                            mProgress_bar_search.setVisibility(View.GONE);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();

                            params.put("lan", LocaleHelper.getLanguage(MoreDataActivity.this));
                            params.put("m_filter", (PreferenceData.isMatureFilterEnable(MoreDataActivity.this) ? "" + 1 : "" + 0));

                            params.put("device", "android");
                            params.put("search", query);
                            params.put("live_offset", liveParent != null ? "" + liveParent.offset : "" + 0);
                            params.put("live_limit", "10");

                            return params;
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (liveSearchArrayList != null && liveSearchArrayList.size() != 0) {
                                moreLiveAdapter.setLoaded();
                                moreLiveAdapter.notifyDataSetChanged();
                            }

                            progressbar1.setVisibility(View.GONE);
                            mProgress_bar_search.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }


    //====================================================================================================================

    /**
     * Method to init the DropDown list
     */
    private void initDropDownPopup() {
        Tracer.error(TAG, "initDropDownPopup: ");
        String langaugeListJsonArray = PreferenceData.getLangaugeListJsonArray(this);
        ArrayList<String> jsonString = new Gson().fromJson(langaugeListJsonArray, new TypeToken<List<String>>() {
        }.getType());
        if (jsonString.size() > 0) {
            initDropDownLanguage(jsonString);
            ArrayList<String> pricingList = new ArrayList<>();
            pricingList.add("Free");
            pricingList.add("Paid");
            initDropDownPricing(pricingList);
            ArrayList<String> contentProviderList = new ArrayList<>();
            contentProviderList.add("Youtube");
            contentProviderList.add("Dailymotion");
            contentProviderList.add("Vimeo");
            initDropDownContentProvider(contentProviderList);
        } else {
            hideDropDownContentProvider();
            hideDropDownLanguage();
            hideDropDownPricing();
        }
    }

    /**
     * Method to init the DropDown list
     */
    private void hideDropDownPopup() {
        Tracer.debug(TAG, "hideDropDownPopup: ");
        hideDropDownContentProvider();
        hideDropDownLanguage();
        hideDropDownPricing();
    }

    /**
     * Method to show the Langauge Dropdown
     *
     * @param languageList {@link List of the Supported Langauge}
     */
    private void initDropDownLanguage(ArrayList<String> languageList) {
        if (languageList.size() > 0) {
            if (languageList.size() > 1) {
                languageList.add(0, "ALL");
            }
            mTextViewLanguage.setVisibility(View.VISIBLE);
            if (mPopupWindowLangauge != null) {
                mPopupWindowLangauge.dismiss();
                mPopupWindowLangauge = null;
            }
            final PopupAdapter popupAdapter = new PopupAdapter(this);
            mPopupWindowLangauge = new ListPopupWindow(this);
            mPopupWindowLangauge.setAdapter(popupAdapter);
            popupAdapter.setSuggestion(languageList);
            mPopupWindowLangauge.setAnchorView(mTextViewLanguage);
            mPopupWindowLangauge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = popupAdapter.getItem(i);
                    mTextViewLanguage.setText(item);
                    mListPopupWindowCurrent.dismiss();
                    mListPopupWindowCurrent = null;
                    count = 0;
                    if (recommended != null) {
                        recommended.offset = 0;
                        recommendArrayList.clear();
                        recommended = null;
                    }

                    hideKeyboard();

                    searchCountTextview.setVisibility(View.GONE);
                    noRecordFoundTextview.setVisibility(View.GONE);
                    searchRecylerview.setVisibility(View.GONE);

                    current_page = 0;

                    mProgress_bar_search.setVisibility(View.VISIBLE);

                    searchDataLoad(query);
                }
            });
            mPopupWindowLangauge.setModal(true);
            mTextViewLanguage.setText(languageList.get(0));
        } else {
            hideDropDownLanguage();
        }
    }

    /**
     * Method to hide the Langauge Dropdown
     */
    private void hideDropDownLanguage() {
        mTextViewLanguage.setVisibility(View.GONE);
    }

    /**
     * Method to show the Pricing Dropdown
     *
     * @param pricingList {@link List of the Supported Langauge}
     */
    private void initDropDownPricing(ArrayList<String> pricingList) {
        if (pricingList.size() > 0) {
            if (pricingList.size() > 1) {
                pricingList.add(0, "ALL");
            }
            mTextViewPricing.setVisibility(View.VISIBLE);
            if (mPopupWindowPricing != null) {
                mPopupWindowPricing.dismiss();
                mPopupWindowPricing = null;
            }
            final PopupAdapter popupAdapter = new PopupAdapter(this);
            mPopupWindowPricing = new ListPopupWindow(this);
            mPopupWindowPricing.setAdapter(popupAdapter);
            popupAdapter.setSuggestion(pricingList);
            mPopupWindowPricing.setAnchorView(mTextViewPricing);
            mPopupWindowPricing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = popupAdapter.getItem(i);
                    mTextViewPricing.setText(item);
                    mListPopupWindowCurrent.dismiss();
                    mListPopupWindowCurrent = null;
                    count = 0;
                    if (recommended != null) {
                        recommended.offset = 0;
                        recommendArrayList.clear();
                        recommended = null;
                    }

                    hideKeyboard();

                    searchCountTextview.setVisibility(View.GONE);
                    noRecordFoundTextview.setVisibility(View.GONE);
                    searchRecylerview.setVisibility(View.GONE);

                    current_page = 0;

                    mProgress_bar_search.setVisibility(View.VISIBLE);

                    searchDataLoad(query);
                }
            });
            mPopupWindowPricing.setModal(true);
            mTextViewPricing.setText(pricingList.get(0));
        } else {
            hideDropDownPricing();
        }
    }

    /**
     * Method to hide the Pricing Dropdown
     */
    private void hideDropDownPricing() {
        mTextViewPricing.setVisibility(View.GONE);
    }

    /**
     * Method to show the Content Provider Dropdown
     *
     * @param contentProviderList {@link List of the Supported Langauge}
     */
    private void initDropDownContentProvider(ArrayList<String> contentProviderList) {
        if (contentProviderList.size() > 0) {
            if (contentProviderList.size() > 1) {
                contentProviderList.add(0, "ALL");
            }
            mTextViewContentProvider.setVisibility(View.VISIBLE);
            if (mPopupWindowContentProvider != null) {
                mPopupWindowContentProvider.dismiss();
                mPopupWindowContentProvider = null;
            }
            final PopupAdapter popupAdapter = new PopupAdapter(this);
            mPopupWindowContentProvider = new ListPopupWindow(this);
            mPopupWindowContentProvider.setAdapter(popupAdapter);
            popupAdapter.setSuggestion(contentProviderList);
            mPopupWindowContentProvider.setAnchorView(mTextViewContentProvider);
            mPopupWindowContentProvider.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = popupAdapter.getItem(i);
                    mTextViewContentProvider.setText(item);
                    mListPopupWindowCurrent.dismiss();
                    mListPopupWindowCurrent = null;
                    count = 0;
                    if (recommended != null) {
                        recommended.offset = 0;
                        recommendArrayList.clear();
                        recommended = null;
                    }

                    searchCountTextview.setVisibility(View.GONE);
                    noRecordFoundTextview.setVisibility(View.GONE);
                    searchRecylerview.setVisibility(View.GONE);

                    current_page = 0;

                    mProgress_bar_search.setVisibility(View.VISIBLE);

                    searchDataLoad(query);
                }
            });
            mPopupWindowContentProvider.setModal(true);
            mTextViewContentProvider.setText(contentProviderList.get(0));
        } else {
            hideDropDownContentProvider();
        }
    }

    /**
     * Method to hide the Content Provider Dropdown
     */
    private void hideDropDownContentProvider() {
        mTextViewContentProvider.setVisibility(View.GONE);
    }

    /**
     * Adapter class to hold the Item
     */
    private class PopupAdapter extends BaseAdapter {
        private ArrayList<String> mSuggestionList;
        private LayoutInflater mLayoutInflater;

        public PopupAdapter(Context context) {
            mSuggestionList = new ArrayList<>();
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Method to set the Suggestion List
         *
         * @param suggestionList
         */
        public void setSuggestion(ArrayList<String> suggestionList) {
            mSuggestionList.clear();
            if (suggestionList != null) {
                mSuggestionList.addAll(suggestionList);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSuggestionList.size();
        }

        @Override
        public String getItem(int position) {
            return mSuggestionList.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.drop_down_list_item, null);
            }
            ((TextView) view.findViewById(R.id.drop_down_list_item_textView)).setText(getItem(position));
            return view;
        }
    }


    /**
     * Method to show the suggestions
     *
     * @param suggestions
     */
    private void showListViewSuggestion(ArrayList<String> suggestions) {
        try {
            ListView listView = (ListView) findViewById(R.id.activity_more_data_listView_suggestiion);
            if (listView != null && listView.getAdapter() instanceof SuggestionAdapter) {
                ((SuggestionAdapter) listView.getAdapter()).setSuggestion(suggestions);
                listView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Tracer.error(TAG, "showListViewSuggestion: " + e.getMessage());
        }
    }

    /**
     * Method to hide the suggestions
     */
    private void hideListViewSuggestion() {
        try {
            findViewById(R.id.activity_more_data_listView_suggestiion).setVisibility(View.GONE);
        } catch (Exception e) {
            Tracer.error(TAG, "hideListViewSuggestion: " + e.getMessage());
        }
    }

    private void requestSuggestion() {
        Tracer.error(TAG, "requestSuggestion: ");
        final String currentQuery = query;
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.SEARCH_SUGGESTION), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error("liveTv-responce---", response.toString());
                try {
                    Tracer.error(TAG, "requestSuggestion().onResponse: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray suggestionJsonArray = jsonObject.optJSONArray("result");
                    if (query.toString().trim().equalsIgnoreCase(currentQuery)) {
                        ArrayList<String> suggestionList = new ArrayList<>();
                        for (int i = 0; i < suggestionJsonArray.length(); i++) {
                            suggestionList.add(suggestionJsonArray.optString(i));
                        }
                        showListViewSuggestion(suggestionList);
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "requestSuggestion().onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(MoreDataActivity.this));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(MoreDataActivity.this) ? "" + 1 : "" + 0));

                params.put("title", currentQuery);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Adapter class to hold the Item
     */
    private class SuggestionAdapter extends BaseAdapter {
        private ArrayList<String> mSuggestionList;
        private LayoutInflater mLayoutInflater;

        public SuggestionAdapter(Context context) {
            mSuggestionList = new ArrayList<>();
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Method to set the Suggestion List
         *
         * @param suggestionList
         */
        public void setSuggestion(ArrayList<String> suggestionList) {
            mSuggestionList.clear();
            if (suggestionList != null) {
                mSuggestionList.addAll(suggestionList);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSuggestionList.size();
        }

        @Override
        public String getItem(int position) {
            return mSuggestionList.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.suggestion_list_item, null);
            }
            ((TextView) view.findViewById(R.id.suggestion_list_item_textView)).setText(getItem(position));
            return view;
        }
    }
}
