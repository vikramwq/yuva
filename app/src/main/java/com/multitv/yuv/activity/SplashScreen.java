package com.multitv.yuv.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.AppPermissionController;
import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.controller.ContentController;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.db.MediaDbHelper;
import com.multitv.yuv.download.DownloadUtils;
import com.multitv.yuv.firebase.FCMController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.models.ModelSLAT;
import com.multitv.yuv.models.base_epg.BaseAndEpg;
import com.multitv.yuv.models.check_version.CheckVersion;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ConnectionDetector;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.CustomTFSpan;
import com.multitv.yuv.utilities.ExceptionHandler;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;


public class SplashScreen extends AppCompatActivity implements NotificationCenter.NotificationCenterDelegate/*, GetTriviaContentListener*/ {
    private final String TAG = "SplashScreen";
    private boolean isAppUpdateTaskStarted;
    private String newVersion, apkDownloadPath;
    private boolean isForcedDialog;
    private String message;
    public ProgressBar mProgressbar_top;
    //    private AppPermissionController mAppPermissionController;
    int triviaCounter = 0;
    Handler handler;
    Runnable updateTask;
    private final int TRIVIA_RECORD_TIME = 5000;
    private SharedPreference sharedPreference;
    String contentId;

    MediaDbConnector mediaDbConnector;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 9009;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }

        sharedPreference = new SharedPreference();

        MediaDbHelper.getInstance(SplashScreen.this).getWritableDatabase();

        mediaDbConnector = new MediaDbConnector(SplashScreen.this);

        setContentView(R.layout.splash);
        Tracer.error("MKR", "onCreate: " + AppUtils.getVersionNameAndCode(SplashScreen.this)[0]);
        ButterKnife.bind(this);

        checkForDownloadedMedia();
        ExceptionHandler.attach();
        mProgressbar_top = (ProgressBar) findViewById(R.id.progressBar2);
        mProgressbar_top.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
//        mAppPermissionController = new AppPermissionController(this, this);
//        mAppPermissionController.initializedAppPermission();


        if (checkAndRequestPermissions()) {
            FCMController.getInstance(getApplicationContext()).registerToken();
            proceed();

        }


    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Tracer.error(TAG, "onRequestPermissionsResult: ");
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if (mAppPermissionController != null) {
////            mAppPermissionController.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (mAppPermissionController != null) {
//            mAppPermissionController.onRequestPermissionsResult(requestCode);
//        }
    }

    public void moveToHomeScreen() {

        if (handler != null && updateTask != null) {
            handler.removeCallbacks(updateTask);
        }
        if (!ModelSLAT.getInstance(SplashScreen.this).isAppActive()) {
            finish();
            return;
        }
//        if (PreferenceData.isTCShown(getApplicationContext())) {
        //Intent intent;
        String preSelectedLanguage = LocaleHelper.getPersistedData(SplashScreen.this, "");
        if (!TextUtils.isEmpty(preSelectedLanguage)) {
            //intent = new Intent(SplashScreen.this, HomeActivity.class);
            goToLoginScreen();
        } else {
            Intent intent = new Intent(SplashScreen.this, LanguageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
//        } else {
//            Intent intent = new Intent(SplashScreen.this, TermAndConditionActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }
    }


    public void getBaseAndEPGURLs() {

        final String user_id = new SharedPreference().getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);

        if (!ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
            ConnectionDetector.showNetworkNotConnectedDialog(SplashScreen.this);
            return;
        }
        String masterUrl = "http://api.multitvsolution.com/automatorapi/master/url/token/" + ApiRequest.TOKEN;
        Tracer.error("API_REQUEST", "Base and EPG url : " + masterUrl);
        mProgressbar_top.setVisibility(View.VISIBLE);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                masterUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Tracer.error("Base and EPG api---", response.toString());
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Tracer.error("Base and EPG api---", str);
                        BaseAndEpg baseAndEpg = Json.parse(str.trim(), BaseAndEpg.class);
//                        String baseUrl = "http://api.multitvsolution.com/automatorapi/v2";
//                        baseAndEpg.base_url = baseUrl;
                        if (baseAndEpg != null && !TextUtils.isEmpty(baseAndEpg.base_url) && !TextUtils.isEmpty(baseAndEpg.epg_url) && !TextUtils.isEmpty(baseAndEpg.drive_url)) {
                            PreferenceData.setBaseAndEPGUrlAndDriveUrl(SplashScreen.this, baseAndEpg.base_url, baseAndEpg.epg_url, baseAndEpg.drive_url);

                            checkVersion();
                        }
                    } else if (mObj.optInt("code") == 0) {
                        finish();
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    // customProgressDialog.dismiss(this);
                    mProgressbar_top.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("SplashScreen", "Error: " + error.getMessage());
                mProgressbar_top.setVisibility(View.INVISIBLE);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("device", "android");

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void checkVersion() {
        Tracer.error("API_REQUEST", "Check version url : " + AppUtils.generateUrl(getApplicationContext(), ApiRequest.CHECK_VERSION_URL));

        mProgressbar_top.setVisibility(View.VISIBLE);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.CHECK_VERSION_URL), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                mProgressbar_top.setVisibility(View.INVISIBLE);
                Tracer.error("Check_version api---", response.toString());
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        Tracer.error("Check_version api ---", "Need to take action. Updated version available.");
                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Tracer.error("Check_version api ---", str);
                        CheckVersion checkVersion = Json.parse(str.trim(), CheckVersion.class);
                        newVersion = checkVersion.version;
                        Log.e("app-version", newVersion);
                        apkDownloadPath = checkVersion.path;

                        if (!isUserHavingLatestVersion(newVersion)) {
                            isForcedDialog = (checkVersion.type != null && checkVersion.type.trim().equalsIgnoreCase("force"));
                            message = (checkVersion.message != null) ? checkVersion.message.trim() : "Default message";
                            showUpdateDialog(isForcedDialog, message);
                        } else {
                            //Go for user registration
                            updateImeiOverAndroidId();
                        }
                    } else if (mObj.optInt("code") == 0) {
                        Tracer.error("Check_version api ---", "No need to take action. Updated version not available.");
                        Tracer.error("Check_version api ---", response);
                        //Go for user registration
                        updateImeiOverAndroidId();
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    // customProgressDialog.dismiss(this);
                    mProgressbar_top.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.error("SplashScreen", "Error: " + error.getMessage());
                mProgressbar_top.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                params.put("device", "android");
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showUpdateDialog(final boolean isForced, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message);
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
            CustomTFSpan tfSpan = new CustomTFSpan(tf);
            SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
            spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(spannableString);

            builder.setIcon(R.drawable.icon_launcher);
            builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    isAppUpdateTaskStarted = true;
//                    new ApkDownloadTask(SplashScreen.this, apkDownloadPath, newVersion, false).execute();

                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                }
            });
            builder.setPositiveButton(isForced ? "Exit" : "Later", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    if (isForced) {
                        finish();
                    } else {
                        updateImeiOverAndroidId();
                    }
                }
            });
            builder.setCancelable(false);
            if (!isFinishing())
                builder.create().show();
        } catch (Exception e) {
            Tracer.error(TAG, e.getMessage());
        }
    }

    private boolean isUserHavingLatestVersion(String newVersion) {
        Tracer.error(TAG, "isUserHavingLatestVersion: NEW " + newVersion);
        if (newVersion == null || newVersion.trim().isEmpty()) {
            return true;
        }
        try {
            String deviceVersionName = AppUtils.getVersionNameAndCode(SplashScreen.this)[0];
            Tracer.error(TAG, "isUserHavingLatestVersion: OLD " + deviceVersionName);
            Tracer.error(TAG, "isUserHavingLatestVersion: " + (Float.parseFloat(newVersion.trim()) + "    " + Float.parseFloat(deviceVersionName.trim())) + "     " + (Float.parseFloat(newVersion.trim()) > Float.parseFloat(deviceVersionName.trim())));
            if (Float.parseFloat(newVersion.trim()) <= Float.parseFloat(deviceVersionName.trim())) {
                Tracer.error(TAG, "isUserHavingLatestVersion: UPDATE APP ");
                return true;
            }
        } catch (Exception e) {
            Tracer.error(TAG, "isUserHavingLatestVersion: " + e.getMessage());
        }
        return false;
    }

    //Go for user registration
    private void registerUser() {
       /* String user_id = new SharedPreference().getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);
        if (user_id == null || user_id.trim().isEmpty()) {
            LoginUtils.getOTP(this);
        } else {
            moveToHomeScreen();
        }*/


        moveToHomeScreen();
    }

    //Go for User Login
    private void goToLoginScreen() {
        boolean isLoggedIn = sharedPreference.getPreferenceBoolean(this, sharedPreference.KEY_IS_LOGGED_IN);
        boolean isSkipEnabled = sharedPreference.getPreferenceBoolean(this, Constant.IS_SKIP_ENABLED);
        Intent intent;

        if (!isSkipEnabled) {
            if (isLoggedIn) {
                boolean isOTPVerfied = sharedPreference.getPreferenceBoolean(this, sharedPreference.KEY_IS_OTP_VERIFIED);
                if (isOTPVerfied) {
                    PreferenceData.setPreviousFragmentSelectedPosition(getApplicationContext(), 0);
                    intent = new Intent(SplashScreen.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, OtpScreenActivity.class);
                }
            } else {
                //LoginUtils.getOTP(this);
                intent = new Intent(this, SignupScreen.class);
            }

        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void updateImeiOverAndroidId() {
        mProgressbar_top.setVisibility(View.VISIBLE);

        Tracer.error(TAG, "updateImeiOverAndroidId: ");
        if (!PreferenceData.isAlreadyUpdateTokenDone(this)) {
            mProgressbar_top.setVisibility(View.VISIBLE);

            StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.UPDATE_IMEI), new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    mProgressbar_top.setVisibility(View.INVISIBLE);
                    PreferenceData.setAlreadyUpdateTokenDone(getApplicationContext());
                    try {
                        Log.d(TAG, "Response  data for updateimei" + response);
                        JSONObject mObj = new JSONObject(response);
                        registerUser();
//                        goToLoginScreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // customProgressDialog.dismiss(this);
                        mProgressbar_top.setVisibility(View.INVISIBLE);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Tracer.error(TAG, "updateImeiOverAndroidId: onErrorResponse: " + error.getMessage());
                    mProgressbar_top.setVisibility(View.INVISIBLE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("lan", LocaleHelper.getLanguage(SplashScreen.this));
                    params.put("m_filter", (PreferenceData.isMatureFilterEnable(SplashScreen.this) ? "" + 1 : "" + 0));
                    params.put("device", "android");
                    params.put("current_unique_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    params.put("new_unique_id", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());

                    Set<String> keys = params.keySet();
                    for (String key : keys) {
                        Log.d(TAG, "Request data" + key + "      " + params.get(key));
                    }


                    return Utilities.checkParams(params);
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } else {
            registerUser();
//            goToLoginScreen();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didContentReceived);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didContentReceivedFailed);

    }


    @Override
    protected void onPause() {
        super.onPause();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didContentReceived);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didContentReceivedFailed);
    }

//    @Override
//    public void onAppPermissionControllerListenerHaveAllRequiredPermission() {
//        Tracer.error(TAG, "onAppPermissionControllerListenerHaveAllRequiredPermission: ");
//
//
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        Uri data = intent.getData();
//
//        if (intent != null && data != null) {
//            Log.d(this.getClass().getName(), "action=======>> " + action + " data=======" + data.toString());
//            String url = data.toString();
//            contentId = url.substring(url.lastIndexOf("/") + 1).trim();
//            Log.d(this.getClass().getName(), "content id=======>> " + contentId);
//
//        }
//
//
//        if (PreferenceData.getBaseUrl(SplashScreen.this) != null && PreferenceData.getBaseUrl(SplashScreen.this).length() > 0) {
//
//
//            if (sharedPreference.getPreferenceBoolean(SplashScreen.this, sharedPreference.KEY_IS_OTP_VERIFIED)) {
//
//                if (contentId != null && contentId.length() > 0) {
//
//                    ContentController.getInstance().getContentDetails(contentId);
//
//                } else {
//
//                    goToLoginScreen();
//
//                }
//            } else {
//
//                if (ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
//                    checkVersion();
//                } else {
//
//                    goToLoginScreen();
//                }
//            }
//
//
//        } else {
//            if (ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
//                getBaseAndEPGURLs();
//                    /*checkVersion();*/
//            } else {
//                mProgressbar_top.setVisibility(View.INVISIBLE);
//                ConnectionDetector.showNetworkNotConnectedDialogSplash(SplashScreen.this);
////                goToLoginScreen();
//            }
//        }
//
//    }

    @Override
    public void didReceivedNotification(int id, final Object... args) {

        Log.d(this.getClass().getName(), "didReceivedNotification called");

        if (id == NotificationCenter.didContentReceived) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressbar_top.setVisibility(View.INVISIBLE);

                    try {
                        String str = (String) args[0];
                        Gson gson = new Gson();
                        Cat_cntn cat_cntn = gson.fromJson(str, Cat_cntn.class);
                        Intent intent = new Intent(SplashScreen.this, MultiTvPlayerActivity.class);
                        intent.putExtra(Constant.CONTENT_TRANSFER_KEY, cat_cntn);
                        intent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


        }

        if (id == NotificationCenter.didContentReceivedFailed) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressbar_top.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

    }


    private void checkForDownloadedMedia() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<MediaItem> listMedia = mediaDbConnector.getDownloadedMedia();

                for (int i = 0; i < listMedia.size(); i++) {

                    MediaItem mediaItem = listMedia.get(i);
                    String content = mediaItem.getType();
                    Gson gson = new Gson();
                    Cat_cntn cat_cntn = gson.fromJson(content, Cat_cntn.class);

                    long expiryInMills = Utilities.toMilliSeconds(cat_cntn.download_expiry);
                    long total = mediaItem.getTimeStamp() + expiryInMills;
                    long currentMillis = System.currentTimeMillis();
                    if (currentMillis > total) {
                        DownloadUtils.removeDownload(SplashScreen.this, listMedia.get(i));
                    } else {
                        Log.d(this.getClass().getName(), "no file to delete====>>> " + total);
                    }

                }

            }
        }).start();


    }


//    private void checkContentVersion() {
//        Tracer.error("API_REQUEST", "Check version url : " + AppUtils.generateUrl(getApplicationContext(), ApiRequest.VERSION_URL));
//
//
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
//                AppUtils.generateUrl(getApplicationContext(), ApiRequest.VERSION_URL), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                mProgressbar_top.setVisibility(View.INVISIBLE);
//                Tracer.error("Check_version api---", response.toString());
//                try {
//                    JSONObject mObj = new JSONObject(response);
//                    if (mObj.optInt("code") == 1) {
//                        Tracer.error("Check_version api ---", "Need to take action. Updated version available.");
//                        MultitvCipher mcipher = new MultitvCipher();
//                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//
//
//
//
//
//                        Tracer.error("Check_version api ---", str);
//
//
//                    } else if (mObj.optInt("code") == 0) {
//                        Tracer.error("Check_version api ---", "No need to take action. Updated version not available.");
//                        Tracer.error("Check_version api ---", response);
//                        //Go for user registration
//
//                    }
//                } catch (Exception e) {
//                    ExceptionUtils.printStacktrace(e);
//                    // customProgressDialog.dismiss(this);
//                    mProgressbar_top.setVisibility(View.INVISIBLE);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Tracer.error("SplashScreen", "Error: " + error.getMessage());
//
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//
//                params.put("device", "android");
//                return params;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//    }


//    private boolean checkAndRequestPermissions() {
//
//
//        int permissionNetworkAccess = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_NETWORK_STATE);
//
//        int permissionReadSMS = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECEIVE_SMS);
//        int permissionReadContact = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_CONTACTS);
//
//
//        int storagePermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//
//
//        List<String> listPermissionsNeeded = new ArrayList<>();
//
//        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//        if (permissionNetworkAccess != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
//        }
//
//        if (permissionReadSMS != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
//        }
//
//        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
//        }
//
//
//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(this,
//
//
//                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
//            return false;
//        }
//
//        return true;
//    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted

                        FCMController.getInstance(getApplicationContext()).registerToken();
                        proceed();


                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {


                            showDialogOK("Without these permissions app is unable to login. Are you sure you want to deny these permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_alert_permission)
                .setTitle("Permissions")
                .setMessage(message)
                .setPositiveButton("RE-TRY", okListener)
                .setNegativeButton("YES, IM SURE", okListener)
                .create()
                .show();


    }


    private boolean checkAndRequestPermissions() {
//        int permissionReceiveMessage = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECEIVE_SMS);

        int permissionExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionReadPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


//        if (permissionReceiveMessage != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
//        }

        if (permissionReadPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void proceed() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if (intent != null && data != null) {
            Log.d(this.getClass().getName(), "action=======>> " + action + " data=======" + data.toString());
            String url = data.toString();
            contentId = url.substring(url.lastIndexOf("/") + 1).trim();
            Log.d(this.getClass().getName(), "content id=======>> " + contentId);

        }


        if (PreferenceData.getBaseUrl(SplashScreen.this) != null && PreferenceData.getBaseUrl(SplashScreen.this).length() > 0) {


            if (sharedPreference.getPreferenceBoolean(SplashScreen.this, sharedPreference.KEY_IS_OTP_VERIFIED)) {

                if (contentId != null && contentId.length() > 0) {

                    ContentController.getInstance().getContentDetails(contentId);

                } else {

                    goToLoginScreen();

                }
            } else {

                if (ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
                    checkVersion();
                } else {

                    goToLoginScreen();
                }
            }


        } else {
            if (ConnectionManager.getInstance(getApplicationContext()).isConnected()) {
                getBaseAndEPGURLs();
                    /*checkVersion();*/
            } else {
                mProgressbar_top.setVisibility(View.INVISIBLE);
                ConnectionDetector.showNetworkNotConnectedDialogSplash(SplashScreen.this);
//                goToLoginScreen();
            }
        }
    }

}