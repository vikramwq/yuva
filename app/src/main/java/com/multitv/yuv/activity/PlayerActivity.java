package com.multitv.yuv.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.interfaces.HeartbeatInterface;
import com.multitv.yuv.utilities.AppSessionUtil1;
import com.multitv.yuv.utilities.SendAnalytics;
import com.multitv.yuv.utilities.Utilities;
import com.multitv.multitvcommonsdk.permission.PermissionChecker;
import com.multitv.multitvcommonsdk.utils.MultiTVException;
import com.multitv.multitvplayersdk.MultiTvPlayer;


public class PlayerActivity extends AppCompatActivity implements MultiTvPlayer.MultiTvPlayerListner, HeartbeatInterface {

    private String TAG = this.getClass().getSimpleName();

    private MultiTvPlayer mPlayer;
    private boolean isHandlerStarted;
    private String storedSessionID, contentID, contentURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_screen);


        Intent intent = getIntent();
        contentID = intent.getStringExtra("content_id");
        contentURL = intent.getStringExtra("url");

        initPlayer(contentID);


//        mPlayer.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        mPlayer.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;


    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        //here you can handle orientation change
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 3000);
        */

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions =   /*  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | */View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);


            mPlayer.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
            mPlayer.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {


            View decorView = getWindow().getDecorView();
            // Show Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);

            mPlayer.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, Utilities.getScreenHeight(this) / 3));
        }


        mPlayer.onConfigurationChanged(newConfig);


    }


    private void initPlayer(String url) {

        if (mPlayer == null) {
            mPlayer = (MultiTvPlayer) findViewById(R.id.multitvPlayer);
        }

        mPlayer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((Resources.getSystem().getDisplayMetrics().widthPixels) * 9 / 16) + 50));
        mPlayer.setContentType(MultiTvPlayer.ContentType.LIVE);
        mPlayer.setKeyToken(ApiRequest.TOKEN);
        mPlayer.setPreRollEnable(false);
        mPlayer.setContentID(url);
        mPlayer.setMultiTvPlayerListner(this);
        mPlayer.hideBrightnessIcon();
        try {
            mPlayer.setAdSkipEnable(false, 5000);
            mPlayer.preparePlayer();
        } catch (MultiTVException e) {
            Log.e("LiveActivity", e.getMessage());
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        storedSessionID = AppController.getInstance().getContentSessionId();
        AppController.getInstance().setContentSessionId("");

        if (mPlayer != null)
            mPlayer.setBackgrounded(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (storedSessionID != null && storedSessionID.trim().length() > 0)
            AppController.getInstance().setContentSessionId(storedSessionID);
        if (mPlayer != null)
            mPlayer.setBackgrounded(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        isHandlerStarted = false;
    }


    @Override
    public void onPlayerReady() {
        if (!TextUtils.isEmpty(contentID)) {
            new SendAnalytics(this, ApiRequest.TOKEN, contentID, null, null, 1)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (mPlayer != null)
            mPlayer.start();
    }

    @Override
    public void onPlayNextVideo() {

    }

    @Override
    public void onPlayClick() {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionChecker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        Log.e("##PLAYER-STATE###", "###PLAYER-PERMISSION####" + permissions.toString());
    }

    @Override
    public void callHeartbeatApi(String sessionId) {
        AppController.getInstance().setContentSessionId(sessionId);
        if (!isHandlerStarted) {
            isHandlerStarted = true;
            AppSessionUtil1.sendHeartBeat(PlayerActivity.this);
        }
    }


}