package com.multitv.yuv.firebase;

import com.multitv.yuv.utilities.Tracer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by delhivery on 9/28/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Tracer.error(TAG, "Refreshed token: " + refreshedToken);
        FCMController.getInstance(getApplicationContext()).registerToken();
    }
}
