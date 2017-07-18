package com.multitv.yuv.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.broadcast.DateChangeLocalBroadcast;
import com.multitv.yuv.customview.HomeTabOptionPopupMenu;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.db.MediaDbHelper;
import com.multitv.yuv.dialogs.HomeTabOptionAlertDialog;
import com.multitv.yuv.exatras.SharedPrefManager;
import com.multitv.yuv.fragment.AboutFragment;
import com.multitv.yuv.fragment.BaseFragment;
import com.multitv.yuv.interfaces.OnShowPopupWindowListener;
import com.multitv.yuv.interfaces.onCategorySelceton;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.User;
import com.multitv.yuv.models.menu.MenuModel;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.trivia.TriviaContentController;
import com.multitv.yuv.ui.CustomTextView;
import com.multitv.yuv.utilities.Adutils;
import com.multitv.yuv.utilities.AppSessionUtil1;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.CustomTFSpan;
import com.multitv.yuv.utilities.CustomTypefaceSpan;
import com.multitv.yuv.utilities.ExceptionHandler;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.multitv.yuv.utilities.VersionUtils;
import com.facebook.login.LoginManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.iainconnor.objectcache.GetCallback;
import com.iainconnor.objectcache.PutCallback;
import com.multitv.multitvcommonsdk.utils.GPSTracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.multitv.yuv.R.id.drawer_layout;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseFragment.OnTabselectedListener, View.OnClickListener, onCategorySelceton, HomeTabOptionAlertDialog.OnHomeTabOptionAlertDialogListener,
        OnShowPopupWindowListener, HomeTabOptionPopupMenu.OnHomeTabOptionPopupMenuListener, NotificationCenter.NotificationCenterDelegate {

    private static final String TAG = "HomeActivity";
    public User user;
    @BindView(R.id.calander)
    ImageView mCalander;
    @BindView(R.id.search)
    ImageView mSearch;
    @BindView(R.id.filter)
    ImageView mFilter;

    @BindView(R.id.genre)
    ImageView mGenre;
    @BindView(R.id.rl_notification)
    RelativeLayout mNotifybell;
    @BindView(R.id.tv_notification_count)
    TextView tvBadgeCount;

    @BindView(R.id.emptyView)
    LinearLayout emptyView;


    private String search_category_id;
    public DatePickerDialog fromDatePickerDialog;
    private AlertDialog dialog;
    private SharedPrefManager sharedPrefManager = new SharedPrefManager();
    private String mCategoryType = "all";
    ImageView profile_image;

    private MenuModel menuModel;
    private ProgressBar progressbar;
    private SharedPreference sharedPreference;
    private final Set<Target> protectedFromGarbageCollectorTargets = new HashSet<>();
    private Adutils mAdutils;
    private DrawerLayout drawer;
    private HomeTabOptionPopupMenu mHomeTabOptionPopupMenu;

    private MediaDbConnector mediaDbConnector;
    NavigationView navigationView;

    private UpdateBadgerNotification updateBadgerNotification;

    private static Context context;
    private boolean isOTPVerified;
    ActionBarDrawerToggle toggle;

    public static Context getInstance() {
        return context;
    }


    public void closeActivity() {
        finish();
    }

    Toolbar toolbar;
    Fragment homeFragment;
    TextView txtName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        context = this;

        setContentView(R.layout.activity_main_parent);
        MediaDbHelper.getInstance(HomeActivity.this).getWritableDatabase();

        mediaDbConnector = new MediaDbConnector(HomeActivity.this);
        ExceptionHandler.attach();
        sharedPreference = new SharedPreference();

        isOTPVerified = sharedPreference.getPreferenceBoolean(HomeActivity.this, sharedPreference.KEY_IS_OTP_VERIFIED);

        if (isOTPVerified) {
            createAppSession();
        }


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Tracer.debug(TAG, "Key: " + key + " Value: " + value);
            }
        }
        ButterKnife.bind(this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(getResources().getDrawable(R.drawable.toolbar_icon));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Utilities.applyFontForToolbarTitle(HomeActivity.this);

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.USER_PHOTO_CHANGED);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.USER_PROFILE_CHANGED);

        progressbar = (ProgressBar) findViewById(R.id.progressBar);


        mFilter.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mGenre.setOnClickListener(this);
        mCalander.setOnClickListener(this);
        mNotifybell.setOnClickListener(this);
        drawer = (DrawerLayout) findViewById(drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);

        txtName = (TextView) headerLayout.findViewById(R.id.txtName);
        profile_image = (ImageView) headerLayout.findViewById(R.id.profile_image);

        if (sharedPreference.getUSerName(this, "first_name") != null) {
            txtName.setText(sharedPreference.getUSerName(this, "first_name"));
        }


        if (sharedPreference.getImageUrl(HomeActivity.this, "imgUrl") != null && sharedPreference.getImageUrl(HomeActivity.this, "imgUrl").length() > 0) {

            Picasso.with(HomeActivity.this)
                    .load(sharedPreference.getImageUrl(HomeActivity.this, "imgUrl").replace("\\", ""))
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_image);
        }


        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (mHomeTabOptionPopupMenu != null && mHomeTabOptionPopupMenu.isPopupWindowShown()) {
                    mHomeTabOptionPopupMenu.dissmiss();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mHomeTabOptionPopupMenu != null && mHomeTabOptionPopupMenu.isPopupWindowShown()) {
                    mHomeTabOptionPopupMenu.dissmiss();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //=======get detail from sharePerences===================


        getMenuItems(navigationView);

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserProfileScreen.class);
                intent.putExtra("header_click", "header_click");
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        updateBadgerNotification = new UpdateBadgerNotification();

        mAdutils = new Adutils(this, Adutils.getUsedSdk(this));
        showBannerAd();


        if (!ConnectionManager.getInstance(getApplicationContext()).isConnected()) {

            Intent intent = new Intent(HomeActivity.this, DownloadedMediaListing.class);
            startActivity(intent);
            finish();

        }


        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuItems(navigationView);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        mAdutils.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateBadgerNotification, new IntentFilter("UPDATE-BADGE-COUNT"));
        updateNotificationCount();
        showSearchMice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdutils.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateBadgerNotification);
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

    /**
     * Method to show the Interstitial Ad
     */
    private void showInterstitialAd() {
        Tracer.error(TAG, "showInterstitialAd: ");
        mAdutils.showInterstitial();
    }


    public void showSearchMice() {
        mGenre.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
    }

    private void hideSearchMice() {
        mGenre.setVisibility(View.GONE);
        mSearch.setVisibility(View.GONE);
        mFilter.setVisibility(View.GONE);
        //mNotifybell.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (mHomeTabOptionPopupMenu != null && mHomeTabOptionPopupMenu.isPopupWindowShown()) {
            mHomeTabOptionPopupMenu.dissmiss();
            mHomeTabOptionPopupMenu = null;
            return;
        }
        Fragment ft = getSupportFragmentManager().findFragmentById(R.id.container1);
        if (ft != null) {

            if (ft instanceof BaseFragment) {

                if (!((BaseFragment) ft).isHomeTabOnTop()) {

                    ((BaseFragment) ft).loadHomeFragment();
//                    toolbar.setTitle(" " + getString(R.string.app_name));
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    showSearchMice();
                    return;
                }
            } else {

                loadHomeFragment();
//                toolbar.setTitle(" " + getString(R.string.app_name));
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                return;
            }

        }

        String msg = "Are you sure you want to exit from " + getResources().getString(R.string.app_name);
        showUpdateDialog(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final String title = item.getTitle().toString();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(this.getClass().getName(), "title========>>>> " + title);
                    displayView(title);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
            }
        }, 250);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constant.EXTRA_OPEN_HOME_SCREEN, false)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment homeFragment = new BaseFragment();
            ft.replace(R.id.container1, homeFragment).addToBackStack(null);
            ft.commitAllowingStateLoss();
            showSearchMice();
        }
    }

    private void displayView(String title) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        Log.d(this.getClass().getName(), "menu identifier======>> " + getMenuIdentifier(title.trim()));

        switch (getMenuIdentifier(title.trim())) {
            case "home":
                Fragment ft = getSupportFragmentManager().findFragmentById(R.id.container1);
                if (ft instanceof BaseFragment && !((BaseFragment) ft).isHomeTabOnTop()) {
                    ((BaseFragment) ft).loadHomeFragment();
                    return;
                } else {
                    loadHomeFragment();
                }
                getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.toolbar_icon));
                getSupportActionBar().setTitle(" " + getString(R.string.app_name));
                showSearchMice();
                break;
            case "myaccount":
                Intent editProfile = new Intent(HomeActivity.this, UserProfileActivity.class);
                editProfile.putExtra("ClickType", "1");
                startActivity(editProfile);
                //getSupportActionBar().setTitle(title);
//                hideSearchMice();
                break;


            case "downloads":

                Intent downloadIntent = new Intent(HomeActivity.this, DownloadedMediaListing.class);
                startActivity(downloadIntent);
                //getSupportActionBar().setTitle(title);


                break;

            case "wishlist":
                Intent wishListIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                wishListIntent.putExtra("ClickType", "2");
                startActivity(wishListIntent);
                //getSupportActionBar().setTitle(title);
//                hideSearchMice();
                break;

            case "favourite":
                Intent favoriteIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                favoriteIntent.putExtra("ClickType", "3");
                startActivity(favoriteIntent);
                //getSupportActionBar().setTitle(title);
//                hideSearchMice();
                break;


            case "disclaimer":
                fragment = new AboutFragment();
                bundle = new Bundle();
                /*bundle.putString("T&C_DATA", getMenuData("T&C"));
                bundle.putString("PRIVACY_POLICY", getMenuData("Privacy Policy"));*/
                bundle.putString("key", getMenuData("disclaimer"));
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle(" " + title);
                hideSearchMice();

                break;

            case "tc":
                fragment = new AboutFragment();
                bundle = new Bundle();
                bundle.putString("key", getMenuData("tc"));
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle(" " + title);
                //getSupportActionBar().setTitle(title);
                hideSearchMice();
                break;

            case "privacy":
                fragment = new AboutFragment();
                bundle = new Bundle();
                bundle.putString("key", getMenuData("privacy"));
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle(" " + title);
                //getSupportActionBar().setTitle(title);
                hideSearchMice();
                break;
            case "rateapp":
                Toast.makeText(HomeActivity.this, "Coming Soon", Toast.LENGTH_LONG).show();
                //ratingDailog();
                break;
            case "settings":
                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                intent.putExtra("setting_click", "setting_click");
                startActivity(intent);
//                hideSearchMice();
                break;

            case "tvguide":
            /*    menuMultipleActions.setVisibility(View.GONE);
                fragment = new GuideFragment();
                getSupportActionBar().setTitle(" " + title);
                hideSearchMice();*/
                break;

            case "about":
                fragment = new AboutFragment();
                bundle = new Bundle();
                /*bundle.putString("T&C_DATA", getMenuData("T&C"));
                bundle.putString("PRIVACY_POLICY", getMenuData("Privacy Policy"));*/
                bundle.putString("key", getMenuData("about"));
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle(" " + title);
                hideSearchMice();
                break;

            case "notification":
//                intent = new Intent(HomeActivity.this, NotificationsActivity.class);
//                startActivity(intent);
                break;


            case "signout":

                if (isOTPVerified) {
                    String msg = "Are you sure you want to logout from " + getResources().getString(R.string.app_name);

                    showLogoutDialog(msg);
                } else {
                    Intent intent1 = new Intent(HomeActivity.this, SignupScreen.class);
                    startActivity(intent1);
                    finish();

                }
                break;


            default:
                Intent favorite1Intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                favorite1Intent.putExtra("ClickType", "3");
                startActivity(favorite1Intent);
                hideSearchMice();
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container1, fragment).addToBackStack(null);
            ft.commit();
        }

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
    }

    private String getMenuIdentifier(String menuTitle) {
        if (menuModel != null && menuModel.leftMenuArrayList.size() != 0) {
            for (MenuModel.LeftMenu leftMenu : menuModel.leftMenuArrayList) {
                if (leftMenu.pageTitle.equalsIgnoreCase(menuTitle))
                    return leftMenu.identifier;
                else if ("Sign in".equalsIgnoreCase(menuTitle)) {
                    return "signout";
                }
            }
        }

        return null;
    }


    private String getMenuData(String menuIdentifier) {
        if (menuModel != null && menuModel.leftMenuArrayList.size() != 0) {
            for (MenuModel.LeftMenu leftMenu : menuModel.leftMenuArrayList) {
                Log.e("***TITLE***", leftMenu.pageTitle);
                if (leftMenu.identifier.equalsIgnoreCase(menuIdentifier))
                    return leftMenu.pageDescription;
            }
        }

        return null;
    }

    private boolean mIsFirstLoadHome = true;

    private void loadHomeFragment() {
        try {
            Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.container1);
            if (fragmentById != null && fragmentById instanceof BaseFragment) {
                return;
            }
            homeFragment = new BaseFragment();
            if (!mIsFirstLoadHome) {
                try {
                    String stringExtra = getIntent().getStringExtra(Constant.EXTRA_REMINDER_DATA_KEY);
                    if (stringExtra != null && !stringExtra.trim().isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constant.EXTRA_SHOW_LIVE_TAB, true);
                        homeFragment.setArguments(bundle);
                    }
                } catch (Exception e) {
                    Tracer.error(TAG, "onCreate: " + e.getMessage());
                }
            }
            mIsFirstLoadHome = false;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container1, homeFragment).addToBackStack(null);
            ft.commitAllowingStateLoss();
            showSearchMice();
        } catch (Exception e) {
            Tracer.error(TAG, "onCreate: " + e.getMessage());
        }
    }


    private void getMenuItems(final NavigationView navigationView) {
        Tracer.debug("MENU_API", "Menu URL is " + AppUtils.generateUrl(getApplicationContext(), ApiRequest.MENU_URL));
        Tracer.error(TAG, "getMenuItems: ");
        final String key = "Menu";
        final Type objectType = new TypeToken<MenuModel>() {
        }.getType();
        AppController.getInstance().getCacheManager().getAsync(key, MenuModel.class, objectType, new GetCallback() {
            @Override
            public void onSuccess(Object object) {
                menuModel = (MenuModel) object;

                if (VersionUtils.getIsMenuVersionChanged(HomeActivity.this, menuModel, key))
                    menuModel = null;

                if (menuModel != null && menuModel.leftMenuArrayList != null && menuModel.leftMenuArrayList.size() != 0
                        && menuModel.topMenuArrayList != null && menuModel.topMenuArrayList.size() != 0) {
                    Tracer.error("CacheManager", key + " object retrieved successfully");
                    progressbar.setVisibility(View.GONE);
                    addLeftMenuItemsRunTime(navigationView, menuModel);
                    loadHomeFragment();

                    if (!PreferenceData.isHomeFloatDemoDone(HomeActivity.this)) {
                        PreferenceData.setHomeFloatDemoDone(HomeActivity.this);

                    }
                } else {


                    StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                            AppUtils.generateUrl(getApplicationContext(), ApiRequest.MENU_URL), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Tracer.error("Menu_api", response);


                            Log.d(this.getClass().getName(), "response=========>>>" + response);

                            progressbar.setVisibility(View.GONE);

                            try {
                                JSONObject mObj = new JSONObject(response);
                                if (mObj.optInt("code") == 1) {
                                    MultitvCipher mcipher = new MultitvCipher();
                                    String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                    Tracer.error("Menu_api", str);

                                    menuModel = Json.parse(str.trim(), MenuModel.class);

                                    AppController.getInstance().getCacheManager().putAsync(key, menuModel, new PutCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Tracer.error("CacheManager", key + " object saved successfully");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            e.printStackTrace();
                                        }
                                    });

                                    try {
                                        if (menuModel.mAdSdk != null) {
                                            Adutils.setUsedSdk(getApplicationContext(), menuModel.mAdSdk);
                                        }
                                    } catch (Exception e) {
                                        Tracer.error(TAG, "onResponse: " + e.getMessage());
                                    }


                                    addLeftMenuItemsRunTime(navigationView, menuModel);
                                    loadHomeFragment();

                                    if (!PreferenceData.isHomeFloatDemoDone(HomeActivity.this)) {
                                        PreferenceData.setHomeFloatDemoDone(HomeActivity.this);
                                    }
                                } else if (mObj.optInt("code") == 0)
                                    Tracer.error("Error", response);
                            } catch (Exception e) {
                                ExceptionUtils.printStacktrace(e);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Tracer.error("Menu_api", "Error: " + error.getMessage());

                            progressbar.setVisibility(View.GONE);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();

                            params.put("lan", LocaleHelper.getLanguage(HomeActivity.this));
                            params.put("m_filter", (PreferenceData.isMatureFilterEnable(HomeActivity.this) ? "" + 1 : "" + 0));
                            params.put("device", "android");
                            Tracer.info(TAG, "cms links params : lan : " + LocaleHelper.getLanguage(HomeActivity.this) + " m_filter : " + (PreferenceData.isMatureFilterEnable(HomeActivity.this) ? "" + 1 : "" + 0) + " device : android");
                            return params;
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq);
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addLeftMenuItemsRunTime(NavigationView navigationView, MenuModel menuModel) {
        if (menuModel != null && menuModel.leftMenuArrayList != null) {
            //adding items run time
            final Menu menu = navigationView.getMenu();
            for (int i = 0; i < menuModel.leftMenuArrayList.size(); i++) {
                String menuTitle = menuModel.leftMenuArrayList.get(i).pageTitle;

                if (!isOTPVerified && "Sign out".equalsIgnoreCase(menuTitle)) {
                    menuTitle = "Sign in";
                }


                MenuItem menuItem = menu.add(menuTitle);

                applyFontToMenuItem(menuItem);
                setLeftMenuIcon(menuItem, menuModel.leftMenuArrayList.get(i).thumbnail, navigationView, menuModel.leftMenuThumb);
            }
            refreshNavigationDrawerAdapter(navigationView);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mNewTitle.setSpan(new ForegroundColorSpan(Color.BLACK), 0, mi.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    private void refreshNavigationDrawerAdapter(NavigationView navigationView) {
        // refreshing navigation drawer adapter
        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
            final View child = navigationView.getChildAt(i);
            Log.d(TAG, "parent======>>> " + child.getParent());

            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

    private void setLeftMenuIcon(final MenuItem menuItem, String imageUrl, final NavigationView navigationView, String drawerImageUrl) {
        int widthAndHeightOfIcon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        menuItem.setIcon(getResources().getDrawable(R.drawable.or));

//        Log.d(this.getClass().getName(), "imageUrl=========>>>>" + imageUrl + "  widthAndHeightOfIcon===" + widthAndHeightOfIcon);

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {


//                profile_image.setImageBitmap(bitmap);


                Log.d(this.getClass().getName(), "bitmap hight===" + bitmap.getHeight());

//                BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);
                menuItem.setIcon(new BitmapDrawable(getResources(), bitmap));

                protectedFromGarbageCollectorTargets.remove(this);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                menuItem.setIcon(placeHolderDrawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                menuItem.setIcon(errorDrawable);

                protectedFromGarbageCollectorTargets.remove(this);
            }
        };

        //Using this set to avoid target objects from getting garbage collected.
        //Otherwise, onBitmapLoaded() will never be called as target object is weakly referenced object
        protectedFromGarbageCollectorTargets.add(target);

        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.place_holder).error(R.mipmap.place_holder)
                .resize(widthAndHeightOfIcon, widthAndHeightOfIcon)
                .into(target);

//        //Set Background image to navigation drawer
//        final Target targetDrawer = new Target() {
//            @Override
//            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                navigationView.setBackground(new BitmapDrawable(bitmap));
//
//                protectedFromGarbageCollectorTargets.remove(this);
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                navigationView.setBackground(placeHolderDrawable);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//                navigationView.setBackground(errorDrawable);
//
//                protectedFromGarbageCollectorTargets.remove(this);
//            }
//        };
//
//        //Using this set to avoid target objects from getting garbage collected.
//        //Otherwise, onBitmapLoaded() will never be called as target object is weakly referenced object
//        protectedFromGarbageCollectorTargets.add(targetDrawer);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.search:
                Tracer.error(TAG, "onClick:SEARCH BUTTON CLICK : " + search_category_id);
                Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.container1);
                if (fragmentById != null && fragmentById instanceof BaseFragment) {
                    Intent intent = new Intent(HomeActivity.this, MoreDataActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constant.IS_FROM_SEARCH, true);
                    intent.putExtra("more_data_tag", 4);
                    intent.putExtra("cat_id", search_category_id);
                    intent.putExtra(Constant.EXTRA_CATEGORY_TYPE, mCategoryType);
                    Tracer.error(TAG, "SEARCH.onClick: " + search_category_id);
                    startActivity(intent);
                }
                break;
            case R.id.genre:

//                refreshFragment();


                if (ConnectionManager.getInstance(this).isConnected()) {
                    Intent intent = new Intent(HomeActivity.this, GenreList.class);
                    startActivity(intent);
                } else {
                    Tracer.showToastProduction(this, "No Network Access", false);
                }
                break;


            case R.id.calander:
                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String DateToStr = format.format(curDate);

                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar newCalendar = Calendar.getInstance();
                fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        final Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);

                        String dateStr = String.valueOf(dateFormatter.format(newDate.getTime()));
                        Intent intent = new Intent(DateChangeLocalBroadcast.ACTION);
                        intent.putExtra(Constant.EXTRA_DATE, dateStr);
                        sendBroadcast(intent);
                        sharedPrefManager.setDate(HomeActivity.this, dateStr);

                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                fromDatePickerDialog.show();
                break;


            case R.id.filter:
//                loadFilterFragment();
                break;

        }
    }

    private boolean mIsFirstRun = true;

    @Override
    public void onTabselected(int position) {
        if (mIsFirstRun) {
            mIsFirstRun = false;
            return;
        }
        showInterstitialAd();
    }

    @Override
    public void onCategory(String categoryid) {
        Tracer.debug("Category_id", categoryid);
        search_category_id = categoryid;
    }


    @Override
    public void onHomeTabOptionAlertDialogShowFragment(String fragmentId, String iconUrl) {
        Tracer.error(TAG, "onHomeTabOptionAlertDialogShowFragment: ");
        Fragment ft = getSupportFragmentManager().findFragmentById(R.id.container1);
        if (ft instanceof BaseFragment) {
            ((BaseFragment) ft).loadFragment(fragmentId, iconUrl);
            showSearchMice();
        } else {
            loadHomeFragment();
        }
    }

    @Override
    public void onHomeTabOptionAlertDialogDissmiss() {
//        mImageViewFloatButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHomeTabOptionAlertDialogShown() {
//        mImageViewFloatButton.setVisibility(View.INVISIBLE);
    }

    //=====================================================================================================================
    //=====================================================================================================================
    //=====================================================================================================================
    //=====================================================================================================================
    //=====================================================================================================================
    //=====================================================================================================================
    //=====================================================================================================================

    /**
     * Method to request Language Data
     */
    private void requestLanguageData() {
        Tracer.error(TAG, "requestLanguageData: ");
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.LANGUAGES), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Tracer.error(TAG, "requestLanguageData().onResponse: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray suggestionJsonArray = jsonObject.optJSONArray("result");
                    ArrayList<String> suggestionList = new ArrayList<>();
                    for (int i = 0; i < suggestionJsonArray.length(); i++) {
                        suggestionList.add(suggestionJsonArray.optString(i));
                    }
                    String jsonString = new Gson().toJson(suggestionList, new com.google.gson.reflect.TypeToken<List<String>>() {
                    }.getType());
                    PreferenceData.setLangaugeListJsonArray(getApplicationContext(), jsonString);
                    Tracer.error("****language***", jsonString);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    Tracer.error(TAG, "requestLanguageData().onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "requestLanguageData().onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onShowPopupWindowListenerShowMenuItemOptionPopup(View anchorView) {
        if (mHomeTabOptionPopupMenu != null) {
            mHomeTabOptionPopupMenu.dissmiss();
            mHomeTabOptionPopupMenu = null;
        }
        mHomeTabOptionPopupMenu = new HomeTabOptionPopupMenu(this, anchorView, menuModel, this);
        mHomeTabOptionPopupMenu.show();
    }

    @Override
    public void onHomeTabOptionPopupMenuShowFragment(String fragmentId, String iconUrl) {
        showInterstitialAd();
        Fragment ft = getSupportFragmentManager().findFragmentById(R.id.container1);
        if (ft instanceof BaseFragment) {
            ((BaseFragment) ft).loadFragment(fragmentId, iconUrl);
            showSearchMice();
        } else {
            loadHomeFragment();
        }
    }

    @Override
    public void onHomeTabOptionPopupMenuDissmiss() {

    }

    @Override
    public void onHomeTabOptionPopupMenuShown() {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    public Action getIndexApiAction0() {
//        Thing object = new Thing.Builder()
//                .setName("Home Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }
    @Override
    public void onDestroy() {
        PreferenceData.setPreviousFragmentSelectedPosition(getApplicationContext(), 0);
        super.onDestroy();
    }

    private void updateNotificationCount() {
        Set<String> unreadNotifications = sharedPreference.getUnreadNotificationsList(this, sharedPreference.KEY_NOTIFICATION_SET);
        int badgeCount = unreadNotifications.size();
        if (badgeCount > 0) {
            tvBadgeCount.setVisibility(View.VISIBLE);
            if (badgeCount > 99) {
                tvBadgeCount.setText("99+");
            } else {
                tvBadgeCount.setText(badgeCount + "");
            }
        } else {
            tvBadgeCount.setText("");
            tvBadgeCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.USER_PHOTO_CHANGED) {

            if (sharedPreference.getImageUrl(HomeActivity.this, "imgUrl") != null && sharedPreference.getImageUrl(HomeActivity.this, "imgUrl").length() > 0) {
                Picasso
                        .with(HomeActivity.this)
                        .load(sharedPreference.getImageUrl(HomeActivity.this, "imgUrl"))
                        .placeholder(R.mipmap.intex_profile)
                        .error(R.mipmap.intex_profile)
                        .into(profile_image);
            }

        }

        if (id == NotificationCenter.USER_PROFILE_CHANGED) {

            if (sharedPreference.getUSerName(this, "first_name") != null) {
                txtName.setText("Welcome " + sharedPreference.getUSerName(this, "first_name"));
            }


        }


    }


    class UpdateBadgerNotification extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotificationCount();
        }
    }

    private void showUpdateDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);
        builder.setIcon(R.drawable.toolbar_icon);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void refreshTriviaContent() {
        if (((AppController) getApplication()).getTriviaInprocess() == 0 && ((AppController) getApplication()).getTriviaCompleted() == 0) {
            TriviaContentController triviaContentController = new TriviaContentController(HomeActivity.this);
            triviaContentController.setTriviaListener(null);
            triviaContentController.getTriviaDataFromServer();
            ((AppController) getApplication()).setTriviaInprocess(1);
            ((AppController) getApplication()).setTriviaCompleted(1);
        }
    }


    private void showLogoutDialog(String message) {

        final String LoginFrom = sharedPreference.getFromLogedIn(HomeActivity.this, "fromLogedin");
        Log.e("HomeActivity", "Login-status" + "_:_" + LoginFrom);

        SpannableString spannableMessage = new SpannableString(message);
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_backgorung_layout, null);


        final CustomTextView titleTxt = (CustomTextView) dialogView.findViewById(R.id.appNameTitle);
        final CustomTextView messageTxt = (CustomTextView) dialogView.findViewById(R.id.messageTxt);
        final CustomTextView okBtn = (CustomTextView) dialogView.findViewById(R.id.okBtn);
        final CustomTextView cancelBtn = (CustomTextView) dialogView.findViewById(R.id.cancelBtn);

        titleTxt.setText(spannableString);
        messageTxt.setText(spannableMessage);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mediaDbConnector.dropAllTables();

                sharedPreference.setPreferenceBoolean(HomeActivity.this, sharedPreference.KEY_IS_OTP_VERIFIED, false);
                sharedPreference.setPreferenceBoolean(HomeActivity.this, sharedPreference.KEY_IS_LOGGED_IN, false);

                LoginManager.getInstance().logOut();

                if (LoginFrom.equals("google")) {
                    sharedPreference.setFromLogedIn(HomeActivity.this, "fromLogedin", "");


                    goToLoginActivity();
                } else if (LoginFrom.equals("facebook")) {
                    LoginManager.getInstance().logOut();
                    goToLoginActivity();

                } else if (LoginFrom.equals("veqta")) {


                    goToLoginActivity();
                } else {
                    goToLoginActivity();
                }
            }
        });

        builder.setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void goToLoginActivity() {
        clearSharePrefernces();
        Intent intent = new Intent(HomeActivity.this, SignupScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("logout", "NoStatus");
        startActivity(intent);
        HomeActivity.this.finish();
    }

    private void clearSharePrefernces() {
        AppController.getInstance().setAppSessionId("");
        AppController.getInstance().setContentSessionId("");

        SharedPreferences prefs = getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("fromLogedin", "");
        editor.putString("gender_id", "");
        editor.putString("email_id", "");
        editor.putString("first_name", "");
        editor.putString("last_name", "");
        editor.putString("phone", "");
        editor.putString("password", "");
        editor.putString("imgUrl", "");
        editor.putString("dob", "");
        editor.putString("VERSION", "");
        editor.putString("user_id" + "_" + ApiRequest.TOKEN, "");

        editor.commit();


    }


    public void createAppSession() {
        Log.d(this.getClass().getName(), " app session url---->>>" + AppUtils.generateUrl(getApplicationContext(), ApiRequest.APP_SESSION));


        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.APP_SESSION), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        com.multitv.cipher.MultitvCipher mcipher = new com.multitv.cipher.MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Tracer.error("Create app session api ---", str);
                        if (!TextUtils.isEmpty(str)) {
                            AppController.getInstance().setAppSessionId(str.trim());
                            AppSessionUtil1.sendHeartBeat(HomeActivity.this);
                        }

                    } else if (mObj.optInt("code") == 0) {
                        Tracer.error("Create app session api ---", response);
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("SplashScreen", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lang", LocaleHelper.getLanguage(getApplicationContext()));

                params.put("device", "android");
                params.put("token", ApiRequest.TOKEN);
                params.put("customer_device", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                params.put("customer_id", new SharedPreference().getPreferencesString(HomeActivity.this, "user_id" + "_" + ApiRequest.TOKEN));


                double lat = 0, lng = 0;
                GPSTracker tracker = new GPSTracker(HomeActivity.this);
                if (tracker.canGetLocation()) {
                    lat = tracker.getLatitude();
                    lng = tracker.getLongitude();
                }
                params.put("lat", "" + lat);
                params.put("long", "" + lng);

                Set<String> keys = params.keySet();
                for (String key : keys) {
                    Tracer.error("SignupScreen", "createAppSession().getParams: " + key + "      " + params.get(key));
                }

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}
