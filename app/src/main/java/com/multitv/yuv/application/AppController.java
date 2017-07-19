package com.multitv.yuv.application;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.multitv.yuv.BuildConfig;
import com.multitv.yuv.activity.LoginScreen;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.db.MediaDbConnector;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Tracer;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.DiskCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.fabric.sdk.android.Fabric;

public class AppController extends MultiDexApplication {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private CacheManager cacheManager;
    private FirebaseAnalytics firebaseAnalytics;
    private static AppController mInstance;
    private int triviaInprocess = 0;
    private String appSessionId, contentSessionId;
    private Handler sessionHandler = new Handler();
    private int triviaCompleted = 0;
    private Video videoData;

    public static Context applicationContext;

    public static Handler applicationHandler;


    public int getTriviaInprocess() {
        return triviaInprocess;
    }

    public void setTriviaInprocess(int triviaInprocess) {
        this.triviaInprocess = triviaInprocess;
    }

    public int getTriviaCompleted() {
        return triviaCompleted;
    }

    public void setTriviaCompleted(int triviaCompleted) {
        this.triviaCompleted = triviaCompleted;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        mInstance = this;
        applicationContext = getApplicationContext();
        // LeakCanary.install(this);
        MultiDex.install(this);
        LocaleHelper.onCreate(this);
        ConnectionManager.getInstance(this).startConnectionTracking();
        applicationHandler = new Handler(applicationContext.getMainLooper());
        Fresco.initialize(this);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        Tracer.error(TAG, "AppController.addToRequestQueue() " + req);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public CacheManager getCacheManager() {
        if (cacheManager == null) {
            try {
                File cacheFile = new File(getFilesDir() + File.separator + getPackageName());
                DiskCache diskCache = new DiskCache(cacheFile, BuildConfig.VERSION_CODE, 1024 * 1024 * 10);
                cacheManager = CacheManager.getInstance(diskCache);
            } catch (Exception e) {
                ExceptionUtils.printStacktrace(e);
            }
        }

        return this.cacheManager;
    }

    public FirebaseAnalytics getFirebaseInstance() {
        if (firebaseAnalytics == null)
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        return this.firebaseAnalytics;
    }


    public String getAppSessionId() {
        return appSessionId;
    }

    public void setAppSessionId(String appSessionId) {
        this.appSessionId = appSessionId;
    }

    public String getContentSessionId() {
        return contentSessionId;
    }

    public void setContentSessionId(String contentSessionId) {
        this.contentSessionId = contentSessionId;
    }

    public Handler getSessionHandler() {
        return sessionHandler;
    }


    public void logoutFromApp() {

        MediaDbConnector mediaDbConnector = new MediaDbConnector(this);
        SharedPreference sharedPreference = new SharedPreference();

        sharedPreference.setFromLogedIn(this, "fromLogedin", "");
        sharedPreference.setGender(this, "gender_id", "");
        sharedPreference.setEmailId(this, "email_id", "");
        sharedPreference.setUserName(this, "first_name", "");
        sharedPreference.setUserLastName(this, "last_name", "");
        sharedPreference.setPhoneNumber(this, "phone", "");
        sharedPreference.setPassword(this, "password", "");
        sharedPreference.setImageUrl(this, "imgUrl", "");
        sharedPreference.setDob(this, "dob", "");
        sharedPreference.setPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN, "");

        String LoginFrom = sharedPreference.getFromLogedIn(this, "fromLogedin");

        if (LoginFrom.equals("google")) {
            sharedPreference.setFromLogedIn(this, "fromLogedin", "");
        } else if (LoginFrom.equals("facebook")) {
            LoginManager.getInstance().logOut();
        }
        mediaDbConnector.dropAllTables();
        sharedPreference.setPreferenceBoolean(this, sharedPreference.KEY_IS_OTP_VERIFIED, false);
        sharedPreference.setPreferenceBoolean(this, sharedPreference.KEY_IS_LOGGED_IN, false);
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(getApplicationContext(), LoginScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("logout", "NoStatus");
        startActivity(intent);

    }


    public boolean saveObject(Serializable ser, String file) {
        // Log.d(this.getClass().getName(), "filename============  "+file);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    public Serializable readObject(String file) {
        // Log.d(this.getClass().getName(), "file path-==-====="+file);
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();

            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }


    public void setContentData(Video videoData) {
        this.videoData = videoData;
    }

    public Video getContentData() {
        return videoData;
    }


}