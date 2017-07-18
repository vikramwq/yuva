//package com.barwin.app.activity;
//
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//
//import com.barwin.app.R;
//import com.barwin.app.api.ApiRequest;
//import com.barwin.app.application.AppController;
//import com.barwin.app.interfaces.HeartbeatInterface;
//import com.barwin.app.utilities.AppSessionUtil1;
//import com.barwin.app.utilities.ExceptionUtils;
//import com.barwin.app.utilities.ScreenUtils;
//import com.barwin.app.utilities.SendAnalytics;
//import com.multitv.multitvcommonsdk.MultiTVCommonSdk;
//import com.multitv.multitvcommonsdk.utils.MultiTVException;
//import com.multitv.multitvplayersdk.MultiTvPlayer;
//
///**
// * Created by arungoyal on 30/06/17.
// */
//
//public class LivePlayerScreen extends AppCompatActivity implements MultiTVCommonSdk.AdDetectionListner,
//        MultiTvPlayer.MultiTvPlayerListner, HeartbeatInterface {
//    private MultiTvPlayer player;
//    private FrameLayout framePlayerLayout;
//    private String contentID, storedSessionID;
//    private boolean isHandlerStarted;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
//        }
//        setContentView(R.layout.landscape_player);
//        framePlayerLayout = (FrameLayout) findViewById(R.id.main_frame);
//        contentID = getIntent().getStringExtra("content_id");
//
//        framePlayerLayout.setLayoutParams(new FrameLayout.LayoutParams
//                (FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight(this) / 3));
//
//
//        setMultitvPlayer(contentID, "Live");
//    }
//
//    private void setMultitvPlayer(String video_Url, String content_type_multitv) {
//
//        if (player != null) {
//            player.release();
//            player = null;
//        }
//
//        player = (MultiTvPlayer) findViewById(R.id.player);
//        player.setVisibility(View.GONE);
//
//        if (contentID != null) {
//            player.setContentType(MultiTvPlayer.ContentType.LIVE);
//            player.setContentID(contentID);
//        } else {
//            if (content_type_multitv.equalsIgnoreCase("VOD"))
//                player.setContentType(MultiTvPlayer.ContentType.VOD);
//            else if (content_type_multitv.equalsIgnoreCase("LIVE"))
//                player.setContentType(MultiTvPlayer.ContentType.LIVE);
//
//
//        }
//        player.setKeyToken(ApiRequest.TOKEN);
//        player.setPreRollEnable(false);
//
//        player.setNeedToRepeatVOD(true);
//
////        player.setContentFilePath(video_Url);
//        player.setMultiTvPlayerListner(this);
//
//        try {
//            player.setAdSkipEnable(true, 5000);
//            // player.preparePlayer();
//        } catch (MultiTVException e) {
//            ExceptionUtils.printStacktrace(e);
//        }
//
//    }
//
//
//    @Override
//    public void onAdCallback(String value, String session) {
//        player.onAdCallback(value, session);
//    }
//
//
//    @Override
//    public void onPlayerReady() {
//        try {
//
//            if (!TextUtils.isEmpty(contentID)) {
//                new SendAnalytics(this, ApiRequest.TOKEN, contentID, null, null, 1)
//                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//
//            player.start();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }
//
//    @Override
//    public void onPlayNextVideo() {
//
//    }
//
//    @Override
//    public void callHeartbeatApi(String sessionId) {
//        AppController.getInstance().setContentSessionId(sessionId);
//        if (!isHandlerStarted) {
//            isHandlerStarted = true;
//            AppSessionUtil1.sendHeartBeat(RLivePlayerScreen.this);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        isHandlerStarted = false;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        storedSessionID = AppController.getInstance().getContentSessionId();
//        AppController.getInstance().setContentSessionId("");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (storedSessionID != null && storedSessionID.trim().length() > 0)
//            AppController.getInstance().setContentSessionId(storedSessionID);
//    }
//}
