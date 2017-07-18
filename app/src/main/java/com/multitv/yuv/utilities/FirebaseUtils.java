package com.multitv.yuv.utilities;

import android.os.Bundle;

import com.multitv.yuv.application.AppController;

/**
 * Created by naseeb on 11/7/2016.
 */

public class FirebaseUtils {

    public static void logVideoPlayData(String videoId, String videoTitle, String videoType) {
        Bundle params = new Bundle();
        params.putString("VIDEO_ID", videoId);
        params.putString("VIDEO_TITLE", videoTitle);
        params.putString("VIDEO_TYPE", videoType);

        AppController.getInstance().getFirebaseInstance().logEvent("VIDEO_PLAY", params);
    }
}
