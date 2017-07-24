package com.multitv.yuv.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.multitv.cipher.MultitvCipher;
import com.multitv.multitvcommonsdk.MultiTVCommonSdk;
import com.multitv.multitvcommonsdk.permission.PermissionChecker;
import com.multitv.multitvcommonsdk.utils.MultiTVException;
import com.multitv.multitvplayersdk.MultiTvPlayer;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.MyFragmentPageAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.controller.ContentController;
import com.multitv.yuv.customview.GifImageView;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.download.DownloadUtils;
import com.multitv.yuv.eventbus.UpdateWatchingHistorySection;
import com.multitv.yuv.fragment.MultiTVMoreFragment;
import com.multitv.yuv.fragment.MultiTVRecommendedFragment;
import com.multitv.yuv.interfaces.HeartbeatInterface;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.Channel;
import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.models.ModelSLAT;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.security.FileSecurityUtils;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.Adutils;
import com.multitv.yuv.utilities.AppNetworkAlertDialog;
import com.multitv.yuv.utilities.AppSessionUtil1;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ExceptionHandler;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.FirebaseUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.ScreenUtils;
import com.multitv.yuv.utilities.SendAnalytics;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.multitv.yuv.utilities.Constant.CONTENT_TRANSFER_KEY;

public class MultiTvPlayerActivity extends AppCompatActivity implements MultiTVCommonSdk.AdDetectionListner,
        MultiTvPlayer.MultiTvPlayerListner, HeartbeatInterface {

    private String TAG = "MultiTvPlayerActivity";
    private MultiTvPlayer player;
    int likes_count;
    private String video_Url, content_id, video_id, content_type_multitv, user_id, des, title, share_url, categoryID, download_path;
    private long total_duration, current_position;
    private int width, height;
    private String typePlayer,
            favorite, like;
    private Button btnPlay;
    private TextView tittle_text, like_txt;
    private ExpandableTextView desc_text;
    private Handler handler;
    private ImageView subscribeLayout;
    private FrameLayout framePlayerLayout;
    private ImageView player_image, btn_share, btn_like, btn_favorite, btn_download, dislikeImg;
    private Intent intent;

    private CoordinatorLayout coordinator_layout;
    private LinearLayout content_nestedlayout;


    @BindView(R.id.sliding_tabs)
    TabLayout slidingTabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.rl_before_play)
    RelativeLayout pbViewBeforePlay;


    private Adutils mAdutils;
    private boolean isActivityInitialized, isOnCreateCalled = true, isHorizontalOrientation;
    public boolean isYoutubePlayerInitializationGoingOn;
    MediaDbConnector mediaDbConnector;
    //    Cat_cntn contentData;
    private String local_path;
    SharedPreference sharedPreference;
    boolean isLoggedIn, isOTPVerified;

    private GifImageView downloadGifImg;
    private boolean isHandlerStarted;
    private String contentSessionId;
    private LinearLayout menuLayout;
    private Cat_cntn contentNewData;
    private ImageView channel_image, notificationBtn;
    private TextView channelName;
    private Channel channel;
    private LinearLayout channelLayout, dislikeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        setContentView(R.layout.activity_player_parent);
        sharedPreference = new SharedPreference();
        user_id = sharedPreference.getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);
        isLoggedIn = sharedPreference.getPreferenceBoolean(this, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(this, sharedPreference.KEY_IS_OTP_VERIFIED);

        Log.e("userId", user_id);
        ExceptionHandler.attach();
        if (!ModelSLAT.getInstance(this).isAppActive()) {
            finish();
            return;
        }
        ButterKnife.bind(this);

        handler = new Handler();
        mediaDbConnector = new MediaDbConnector(getApplicationContext());

        initialize();

        isActivityInitialized = true;


    }

    //==================================================================================find view id====================
    //===============================================================================================
    private void initialize() {
        tittle_text = (TextView) findViewById(R.id.tittle);
        like_txt = (TextView) findViewById(R.id.like_txt);
//        start_catTxt = (TextView) findViewById(R.id.start_catTxt);
        desc_text = (ExpandableTextView) findViewById(R.id.expand_text_view);
        framePlayerLayout = (FrameLayout) findViewById(R.id.sony_player_layout);
        player_image = (ImageView) findViewById(R.id.player_image);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_like = (ImageView) findViewById(R.id.like);
        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
        btn_favorite = (ImageView) findViewById(R.id.btn_favorite);
        btn_download = (ImageView) findViewById(R.id.btn_download);
        downloadGifImg = (GifImageView) findViewById(R.id.btn_download_gif);
        downloadGifImg.setGifImageResource(R.drawable.down_update);
        content_nestedlayout = (LinearLayout) findViewById(R.id.content_nestedlayout);
        channel_image = (ImageView) findViewById(R.id.channel_image);
        channelName = (TextView) findViewById(R.id.channelName);
        subscribeLayout = (ImageView) findViewById(R.id.subscribeLayout);
        btn_like.setVisibility(View.VISIBLE);
        notificationBtn = (ImageView) findViewById(R.id.notificationBtn);
        btnPlay = (Button) findViewById(R.id.btn_play);
        dislikeLayout = (LinearLayout) findViewById(R.id.dislikeLayout);
        dislikeImg = (ImageView) findViewById(R.id.dislikeImg);
//        btn_share.setVisibility(View.VISIBLE);

        channelLayout = (LinearLayout) findViewById(R.id.channelLayout);
        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        framePlayerLayout.setLayoutParams(new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight(this) / 3));

        pbViewBeforePlay.getLayoutParams().height = ScreenUtils.getScreenHeight(this) / 3;

        desc_text.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        //-------get data from server-----
        intent = getIntent();

        Cat_cntn contentData = intent.getExtras().getParcelable(CONTENT_TRANSFER_KEY);
        content_type_multitv = intent.getStringExtra("CONTENT_TYPE_MULTITV");
        local_path = intent.getStringExtra("local_path");


        if (contentData != null) {
//            video_Url = contentData.url;
//            share_url = contentData.share_url;
//            title = contentData.title;
//            des = contentData.des;
//            download_path = contentData.download_path;
//
//            typePlayer = contentData.media_type;
            content_id = contentData.id;
//            video_id = content_id;
//            if (contentData.category_id != null)
//                categoryID = contentData.category_id;
//            else {
//                if (contentData.category_ids != null && contentData.category_ids.size() > 0)
//                    categoryID = contentData.category_ids.get(0);
//            }


            getContentDetails(content_id);


            menuLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(MultiTvPlayerActivity.this, v);

                    popup.getMenuInflater().inflate(R.menu.player_menu, popup.getMenu());

                    MenuItem menu = popup.getMenu().getItem(0);
                    MenuItem menu1 = popup.getMenu().getItem(1);


                    SpannableString s = new SpannableString(menu.getTitle());
                    s.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, s.length(), 0);
                    menu.setTitle(s);


                    SpannableString s1 = new SpannableString(menu1.getTitle());
                    s1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, s1.length(), 0);
                    menu1.setTitle(s1);


                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().toString().equalsIgnoreCase("Report")) {
                                ContentController.getInstance().reportContent(content_id);
                            }


                            return true;
                        }
                    });

                    popup.show();//showing popup menu


                }
            });


            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (share_url != null && share_url.length() > 0) {
                            String appPackageName = getPackageName();
                            String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                            Log.d(this.getClass().getName(), "appLink============>>>" + appLink);
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("I am watching this awesome video " + share_url + " on YUV. \n\n Visit YUV app on " + appLink);
                            Uri shreVideoUrl = Uri.parse(share_url);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "YUV");
                            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());
                            startActivity(Intent.createChooser(intent, "choose one"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            dislikeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (contentNewData.is_disliked != null) {
                        if ("1".equals(contentNewData.is_disliked)) {
                            ContentController.getInstance().markContentDisliked(content_id, "0");
                            dislikeImg.setImageResource(R.mipmap.ic_like_disable);
                            contentNewData.is_disliked = "0";
                        } else {
                            ContentController.getInstance().markContentDisliked(content_id, "1");
                            dislikeImg.setImageResource(R.mipmap.ic_like);
                            contentNewData.is_disliked = "1";
                        }

                    }
                }
            });

            downloadGifImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MultiTvPlayerActivity.this, "This media is already downloading", Toast.LENGTH_LONG).show();
                }
            });

            btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppNetworkAlertDialog.isNetworkConnected(MultiTvPlayerActivity.this)) {
                        if (isOTPVerified) {
                            if (mediaDbConnector.checkExistingContent(content_id) > 0) {
                                Toast.makeText(MultiTvPlayerActivity.this, "This media is already downloaded", Toast.LENGTH_LONG).show();
                            } else {
                                if (download_path != null && download_path.length() > 0 && contentNewData.download_expiry > 0) {
                                    MediaItem mediaItem = mediaDbConnector.getMediaItem(content_id);
                                    if (mediaItem == null || mediaItem.getDownloadStatus() == DownloadManager.STATUS_FAILED) {
                                        btn_download.setVisibility(View.GONE);
                                        downloadGifImg.setVisibility(View.VISIBLE);

                                        DownloadUtils.download(MultiTvPlayerActivity.this, contentNewData, mediaDbConnector);

                                        loadImages();


                                        Snackbar.make(coordinator_layout, "Downloading video...",
                                                Snackbar.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(MultiTvPlayerActivity.this, "This media is already downloaded.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MultiTvPlayerActivity.this, "This media is not downloadable.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Utilities.showLoginDailog(MultiTvPlayerActivity.this);
                        }
                    } else {
                        Toast.makeText(MultiTvPlayerActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    }
                }
            });


            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        player.preparePlayer();
                        btnPlay.setVisibility(View.GONE);
                        player_image.setVisibility(View.GONE);
                        player.setVisibility(View.VISIBLE);
                    } catch (MultiTVException e) {
                        ExceptionUtils.printStacktrace(e);
                    }

                }
            });
            mAdutils = new Adutils(this, Adutils.getUsedSdk(this));
            showInterstitialAd();
            showBannerAd();

//            progressBar.setVisibility(View.GONE);

        }
    }

    private void setupViewPager() {
        viewPager.setOffscreenPageLimit(1);

        ArrayList<String> titleArrayList = new ArrayList<>();
        titleArrayList.add(getResources().getString(R.string.more_items_player));
        titleArrayList.add(getResources().getString(R.string.related_items_player));


        Bundle bundle = new Bundle();
        bundle.putString("CATEGORY_ID", categoryID);
        bundle.putString("CONTENT_ID", content_id);


        MultiTVMoreFragment moreFragment = new MultiTVMoreFragment();
        moreFragment.setArguments(bundle);


        MultiTVRecommendedFragment recommendedFragment = new MultiTVRecommendedFragment();
        recommendedFragment.setArguments(bundle);


        List<Fragment> list = new ArrayList<>();
        list.add(moreFragment);
        list.add(recommendedFragment);


        MyFragmentPageAdapter adapter = new MyFragmentPageAdapter(MultiTvPlayerActivity.this, getSupportFragmentManager(), list, titleArrayList);


        viewPager.setAdapter(adapter);

        slidingTabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(slidingTabLayout));
        viewPager.setOffscreenPageLimit(1);


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
        Constant.mTabSelectedCount++;
        if (Constant.mTabSelectedCount % Constant.TAB_SELECT_THARASH_HOLD == 0) {
            Constant.mTabSelectedCount = 0;
            mAdutils.showInterstitial();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionManager.getInstance(this).runNetworkStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionManager.getInstance(this).stopNetworkStateListener();
    }


    private void playVideo() {

        Tracer.error(TAG, "playVideo: " + (intent.getStringExtra("video_id")) + "   " + typePlayer);

        try {
            player.preparePlayer();
            btnPlay.setVisibility(View.GONE);
            player_image.setVisibility(View.GONE);
            player.setVisibility(View.VISIBLE);
            FirebaseUtils.logVideoPlayData(video_id, title, "MULTITV");
         /*   if (video_id != null && !video_id.isEmpty()) {
                new SendAnalytics(MultiTvPlayerActivity.this, ApiRequest.TOKEN, video_id, null, null, 5).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }*/
        } catch (MultiTVException e) {
            ExceptionUtils.printStacktrace(e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (width == 0 || height == 0) {
            width = framePlayerLayout.getLayoutParams().width;
            height = framePlayerLayout.getLayoutParams().height;
        }


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isHorizontalOrientation = true;

            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);

            content_nestedlayout.setVisibility(View.GONE);

            player_image.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
            player_image.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;

            framePlayerLayout.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
            framePlayerLayout.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isHorizontalOrientation = false;
            /*nested_scroll_view.setVisibility(View.VISIBLE);*/

            View decorView = getWindow().getDecorView();
            // Show Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);

            content_nestedlayout.setVisibility(View.VISIBLE);
            framePlayerLayout.setLayoutParams(new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight(this) / 3));
        }


        player.onConfigurationChanged(newConfig);


    }

    @Override
    public void onAdCallback(String value, String session) {
        player.onAdCallback(value, session);
    }

    @Override
    public void onPlayerReady() {
        try {
            if (contentNewData != null && contentNewData.seekDuration > 0) {
                player.resumeFromPosition(contentNewData.seekDuration);
            }
            player.start();

            if (!TextUtils.isEmpty(content_id)) {
                new SendAnalytics(this, ApiRequest.TOKEN, content_id, null, null, 3)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }


        } catch (Exception e) {
            Tracer.error(TAG, e.getMessage()
            );
        }
    }

    @Override
    public void onPlayNextVideo() {

    }

    @Override
    public void onPlayClick() {

    }


    @Override
    public void onBackPressed() {
        if (isYoutubePlayerInitializationGoingOn)
            return;
        if (isHorizontalOrientation) {
            isHorizontalOrientation = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        storedMediaVolume = 7;
        user_id = new SharedPreference().getPreferencesString(MultiTvPlayerActivity.this, "user_id" + "_" + ApiRequest.TOKEN);

        if (player != null) {
            player.release();
        }

        FileSecurityUtils.onPlayerEnded(local_path);

        Tracer.error(TAG, "Player total duration : " + total_duration +
                " current position : " + current_position);
        sendWatchingDataToServer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionChecker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void sendWatchingDataToServer() {
        if (!ConnectionManager.getInstance(MultiTvPlayerActivity.this).isConnected()) {
            return;
        }
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.WATCHING_SET_DATA_URL), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Tracer.error("api_get_watching", response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(MultiTvPlayerActivity.this));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(MultiTvPlayerActivity.this) ? "" + 1 : "" + 0));

                params.put("c_id", user_id); //user id
                params.put("content_id", content_id);
                params.put("duration", "" + current_position);
                params.put("total_duration", "" + total_duration);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        } catch (Exception ignored) {
            return true;
        }
    }

    private void setMultitvPlayer(String video_Url, String content_type_multitv) {

        if (player != null) {
            player.release();
            player = null;
        }

        player = (MultiTvPlayer) findViewById(R.id.player);
        player.setVisibility(View.GONE);

        if (local_path != null) {
            player.setContentType(MultiTvPlayer.ContentType.VOD);
            player.setContentFilePath(local_path);
        } else {
            if (content_type_multitv.equalsIgnoreCase("VOD"))
                player.setContentType(MultiTvPlayer.ContentType.VOD);
            else if (content_type_multitv.equalsIgnoreCase("LIVE"))
                player.setContentType(MultiTvPlayer.ContentType.LIVE);

            player.setContentID(content_id);
        }
        player.setKeyToken(ApiRequest.TOKEN);
        player.setPreRollEnable(true);
        if (contentNewData.subtitle != null && contentNewData.subtitle.size() > 0) {
            Uri subtitleUrl = Uri.parse(contentNewData.subtitle.get(0));
            player.setSubtitleUri(subtitleUrl);
        }

        player.setNeedToRepeatVOD(true);

//        player.setContentFilePath(video_Url);
        player.setMultiTvPlayerListner(this);
        if (current_position != 0)
            player.resumeFromPosition((int) current_position);
        try {
            player.setAdSkipEnable(true, 5000);
            // player.preparePlayer();
        } catch (MultiTVException e) {
            ExceptionUtils.printStacktrace(e);
        }

    }

    //----send like to server---
    private void getLikeToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Tracer.debug("===LIKE_URL==", AppUtils.generateUrl(getApplicationContext(), ApiRequest.LIKE_URL));
                if (!ConnectionManager.getInstance(MultiTvPlayerActivity.this).isConnected()) {
                    return;
                }
                StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                        AppUtils.generateUrl(getApplicationContext(), ApiRequest.LIKE_URL), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tracer.error("===likes_responce==", response);

                        try {
                            JSONObject mObj = new JSONObject(response);
                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                                Tracer.error("===Like_response===", str);
                                JSONObject mObj1 = new JSONObject(str);
                                like = mObj1.getString("like");
                                likes_count = Integer.parseInt(mObj1.getString("total_likes"));
                                Tracer.error("===total_likes===", "" + likes_count);
                                like_txt.setText(getResources().getString(R.string.likes_hint) + likes_count);
                                Tracer.error("===like==", like);
                                sharedPreference.setPreferencesString(MultiTvPlayerActivity.this, "like_count" + video_id + "_" + user_id, "" + likes_count);
                                sharedPreference.setPreferencesString(MultiTvPlayerActivity.this, "like_" + video_id + "_" + user_id, like);
                                if (like.equalsIgnoreCase("0")) {
                                    btn_like.setImageResource(R.mipmap.unlike_bg);
                                } else {
                                    btn_like.setImageResource(R.mipmap.like_fill);
                                }
                                like_txt.setText(getResources().getString(R.string.likes_hint) + likes_count);
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tracer.error("Error", "Error: " + error.getMessage());
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("lan", LocaleHelper.getLanguage(MultiTvPlayerActivity.this));
                        params.put("m_filter", (PreferenceData.isMatureFilterEnable(MultiTvPlayerActivity.this) ? "" + 1 : "" + 0));

                        params.put("device", "android");
                        params.put("content_id", video_id);
                        params.put("type", "video");
                        params.put("user_id", user_id);
                        Tracer.error("===user_id==", user_id);
                        Tracer.error("===content_id===", video_id);

                        return params;
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq);
            }
        }).start();
    }


    private void markContentFvrtOrLike(final int requestType, final int requestedOption) {
        if (!ConnectionManager.getInstance(MultiTvPlayerActivity.this).isConnected()) {
            return;
        }

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.LIKE_URL_Post), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {

                    } else {
                        Toast.makeText(MultiTvPlayerActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    Toast.makeText(MultiTvPlayerActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("", "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("lan", LocaleHelper.getLanguage(MultiTvPlayerActivity.this));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(MultiTvPlayerActivity.this) ? "" + 1 : "" + 0));
                params.put("device", "android");
                params.put("content_id", content_id);
                params.put("user_id", user_id);
                if (requestType == 0) {
                    params.put("type", "video");
                    params.put("like", String.valueOf(requestedOption));
                } else {
                    params.put("content_type", "video");
                    params.put("favorite", String.valueOf(requestedOption));
                }
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void handleGifImageVisibility(MediaItem mediaItem) {
        if (mediaItem != null && (mediaItem.getDownloadStatus() == DownloadManager.STATUS_RUNNING
                || mediaItem.getDownloadStatus() == DownloadManager.STATUS_PENDING)) {
            btn_download.setVisibility(View.GONE);
            downloadGifImg.setVisibility(View.VISIBLE);
        } else {
            btn_download.setVisibility(View.VISIBLE);
            downloadGifImg.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        contentSessionId = AppController.getInstance().getContentSessionId();
        AppController.getInstance().setContentSessionId("");

        if (player != null) {
            total_duration = player.getDuration();
            current_position = player.getCurrentPosition();
            player.setBackgrounded(true);
        }


        LocalBroadcastManager.getInstance(this).unregisterReceiver(uiUpdateReceiver);

        if (!isActivityInitialized || isOnCreateCalled) {
            isOnCreateCalled = false;
            return;
        }

        mAdutils.onPause();

        mediaDbConnector.addDataForPersistencePlayback(content_id, new Gson().toJson(contentNewData), current_position);
        EventBus.getDefault().postSticky(new UpdateWatchingHistorySection());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(contentSessionId))
            AppController.getInstance().setContentSessionId(contentSessionId);


        if (player != null) {
            player.setBackgrounded(false);
        }
        if (mediaDbConnector != null && !TextUtils.isEmpty(content_id)) {
            MediaItem mediaItem = mediaDbConnector.getMediaItem(content_id);
            handleGifImageVisibility(mediaItem);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(uiUpdateReceiver,
                new IntentFilter("DOWNLOAD_UI_UPDATE_PLAYER"));

        if (!isActivityInitialized || isOnCreateCalled) {
            isOnCreateCalled = false;
            return;
        }

        mAdutils.onResume();
    }


    private BroadcastReceiver uiUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadReferenceId = intent.getLongExtra("DOWNLOAD_REFERENCE_ID", -1);
            if (downloadReferenceId != -1) {
                MediaItem mediaItem = mediaDbConnector.getMediaItem(downloadReferenceId);
                if (mediaItem != null) {
                    handleGifImageVisibility(mediaItem);
                }
            }
        }
    };


    private void loadImages() {

        for (int i = 0; i < contentNewData.thumbs.size(); i++) {
            if (contentNewData.thumbs.get(i).getName().equalsIgnoreCase("rectangle")) {
                final String url = contentNewData.thumbs.get(i).getThumb().getMedium();

                Picasso.with(MultiTvPlayerActivity.this)
                        .load(url).fetch();

                Picasso.with(MultiTvPlayerActivity.this).load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // cache is now warmed up
                        Log.d(this.getClass().getName(), "url is cached" + url);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            } else if (contentNewData.thumbs.get(i).getName().equalsIgnoreCase("verticle_rectangle")) {
                final String url = contentNewData.thumbs.get(i).getThumb().getMedium();
                Picasso.with(MultiTvPlayerActivity.this)
                        .load(url).fetch();

                Picasso.with(MultiTvPlayerActivity.this).load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // cache is now warmed up
                        Log.d(this.getClass().getName(), "url is cached" + url);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }
        }
    }


    @Override
    public void callHeartbeatApi(String sessionId) {
        AppController.getInstance().setContentSessionId(sessionId);
        if (!isHandlerStarted) {
            isHandlerStarted = true;
            AppSessionUtil1.sendHeartBeat(MultiTvPlayerActivity.this);
        }
    }


    private void getContentDetails(String contentId) {

        progressBar.setVisibility(View.VISIBLE);

        if (contentId != null) {
            String url;
            url = ApiRequest.CONTENT_DETAILS + "content_id=" + contentId + "&token=" + ApiRequest.TOKEN + "&device=android&owner_info=1&user_id=" + user_id;
            Log.d(this.getClass().getName(), "url=========details===  " + url);


            StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject mObj = new JSONObject(response.trim());
                        if (mObj.optInt("code") == 1) {
                            MultitvCipher mcipher = new MultitvCipher();


                            String result = new String(mcipher.decryptmyapi(mObj.optString("result")));

                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject contentJson = jsonObject.getJSONObject("content");


                            Log.d("Content details", "" + contentJson.toString());

                            contentNewData = Json.parse(contentJson.toString(), Cat_cntn.class);

                            Log.d(this.getClass().getName(), "des======" + contentNewData.des);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    addDataToUI(contentNewData);
                                }
                            });


                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        Tracer.error("ContentController", "" + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Tracer.error("****Get_otp_api****", "Error: " + error.getMessage());

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    try {
                        Map<String, String> params = new HashMap<>();


                        return Utilities.checkParams(params);
                    } catch (Exception e) {
                        ExceptionUtils.printStacktrace(e);
                    } catch (IncompatibleClassChangeError e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            };
            // Adding request to request queue
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq);

        }

    }


    private void addDataToUI(final Cat_cntn content) {


        if (content != null) {
            like = content.likes;

            video_Url = content.url;
            share_url = content.share_url;
            title = content.title;
            des = content.des;
            download_path = content.download_path;

            typePlayer = content.media_type;
            content_id = content.id;
            video_id = content_id;
            if (content.category_id != null)
                categoryID = content.category_id;
            else {
                if (content.category_ids != null && content.category_ids.size() > 0)
                    categoryID = content.category_ids.get(0);
            }

            if (download_path != null && download_path.length() > 0 && content.download_expiry > 0) {
                btn_download.setImageResource(R.mipmap.ic_download_player);
            } else {
                btn_download.setImageResource(R.mipmap.ic_download_disabled);
            }


            setupViewPager();


            if (title != null && !title.isEmpty()) {
                tittle_text.setText(title.trim());
            } else {
                tittle_text.setText(" N/A");
            }

            if (des != null && !des.isEmpty()) {
                desc_text.setText(des.trim());
            } else {
                desc_text.setText(getResources().getString(R.string.player_decs) + " N/A");
            }


            if (!TextUtils.isEmpty(content_type_multitv)) {
                setMultitvPlayer(video_Url, content_type_multitv);
            } else {
                setMultitvPlayer(video_Url, "VOD");
            }
            try {
                playVideo();
            } catch (Exception e) {
                ExceptionUtils.printStacktrace(e);
            }


            if (content.likes_count != null && content.likes_count.length() > 0)
                likes_count = Integer.parseInt(content.likes_count);


            if (like.equalsIgnoreCase("0"))
                btn_like.setImageResource(R.mipmap.ic_like);
            else
                btn_like.setImageResource(R.mipmap.ic_like_disable);


            like_txt.setText(content.likes_count);


            btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOTPVerified) {
                        if (like != null && like.equalsIgnoreCase("1")) {
                            like = "0";
                            likes_count--;
                            btn_like.setImageResource(R.mipmap.ic_like_disable);
                            markContentFvrtOrLike(0, 0);
                        } else {
                            like = "1";
                            likes_count++;
                            btn_like.setImageResource(R.mipmap.ic_like);
                            markContentFvrtOrLike(0, 1);
                        }
                        like_txt.setText("" + likes_count);


                    } else {
                        Utilities.showLoginDailog(MultiTvPlayerActivity.this);
                    }

                }
            });


            if (content.owner_info != null) {
                channelLayout.setVisibility(View.VISIBLE);
                channel = content.owner_info;
                channelName.setText(content.owner_info.getFirst_name());
                Picasso.with(MultiTvPlayerActivity.this)
                        .load(content.owner_info.getPrfile_pic())
                        .placeholder(R.mipmap.intex_profile)
                        .error(R.mipmap.intex_profile)
                        .into(channel_image);

                if (channel.getIs_subscriber().equals("1")) {
                    subscribeLayout.setImageResource(R.mipmap.ic_subsd_diable);
                    notificationBtn.setVisibility(View.VISIBLE);
                } else {
                    subscribeLayout.setImageResource(R.mipmap.ic_subscription);
                    notificationBtn.setVisibility(View.GONE);
                }


                Log.d(this.getClass().getName(), "subscribeLayout=====>>>> " + subscribeLayout);


                subscribeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentController.getInstance().subscribeChannel(channel.getId(), channel.getIs_subscriber());
                        if (channel.getIs_subscriber().equals("0")) {
                            channel.setIs_subscriber("1");
                            channel.setNotification("1");
                            subscribeLayout.setImageResource(R.mipmap.ic_subsd_diable);
                            notificationBtn.setVisibility(View.VISIBLE);
                            notificationBtn.setImageResource(R.mipmap.ic_notification);
                        } else {
                            channel.setIs_subscriber("0");
                            subscribeLayout.setImageResource(R.mipmap.ic_subscription);
                            notificationBtn.setVisibility(View.GONE);
                        }
                    }
                });
                notificationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ("1".equals(channel.getNotification())) {
                            channel.setNotification("0");
                            notificationBtn.setImageResource(R.mipmap.ic_notification_disabled);
                            ContentController.getInstance().doNotificationTask(channel.getId(), "2");
                        } else {
                            channel.setNotification("1");
                            notificationBtn.setImageResource(R.mipmap.ic_notification);
                            ContentController.getInstance().doNotificationTask(channel.getId(), "1");
                        }

                    }
                });

            } else {
                channelLayout.setVisibility(View.GONE);
            }

        }


        findViewById(R.id.player_top_layout).setVisibility(View.VISIBLE);
    }


}





