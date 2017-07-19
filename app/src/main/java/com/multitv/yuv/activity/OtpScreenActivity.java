package com.multitv.yuv.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.multitvcommonsdk.utils.GPSTracker;
import com.multitv.yuv.R;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.User;
import com.multitv.yuv.utilities.AppSessionUtil1;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppNetworkAlertDialog;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;


public class OtpScreenActivity extends AppCompatActivity implements NotificationCenter.NotificationCenterDelegate {
    private EditText otpField;
    private SharedPreference sharedPreference;
    private String user_id, phoneNumber, provider, package_name;
    private ProgressBar progressBar;
    private LinearLayout Verifie_bg;
    private Intent subscriptionIntent;
    private TextView titleTxt;
    private Button verifieBtn;
    private Toolbar toolbar;
    private TextView timerTxt;
    private int counter = 30;
    String[] permissions = {Manifest.permission.RECEIVE_SMS};
    public static int PERMISSION_SMS = 12222;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black_semi_transparent));
        }
        setContentView(R.layout.otp_screen);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        toolbar.setLogo(getResources().getDrawable(R.drawable.toolbar_icon));
        toolbar.setTitle("  " + getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(OtpScreenActivity.this);
        timerTxt = (TextView) findViewById(R.id.timerTxt);

        sharedPreference = new SharedPreference();

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);

        sharedPreference.setLoginOtpSentStatus(OtpScreenActivity.this, "status", "0");
        user_id = sharedPreference.getPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN);
        otpField = (EditText) findViewById(R.id.otp);
        progressBar = (ProgressBar) findViewById(R.id.progress_signin);
        Verifie_bg = (LinearLayout) findViewById(R.id.Verifie_bg);
        verifieBtn = (Button) findViewById(R.id.verifieBtn);


        subscriptionIntent = getIntent();
        String RECEIVED = subscriptionIntent.getStringExtra("getOtp");

        int usedForLogin = subscriptionIntent.getIntExtra("usedForLogin", 0);

        titleTxt = (TextView) findViewById(R.id.titleTxt);
        if (usedForLogin == 1) {
            titleTxt.setText(getResources().getString(R.string.confirm_msg_email));
        } else {


            if (ActivityCompat.checkSelfPermission(OtpScreenActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(OtpScreenActivity.this, permissions, PERMISSION_SMS);
            }
            titleTxt.setText(getResources().getString(R.string.confirm_msg));
        }

//        if (!TextUtils.isEmpty(RECEIVED)) {
//            if (RECEIVED.equalsIgnoreCase("RECEIVED")) {
//                Verifie_bg.setVisibility(View.VISIBLE);
//                phoneNumber = subscriptionIntent.getStringExtra("phone");
//            } else if (RECEIVED.equalsIgnoreCase("NOT_RECEIVED")) {
//                Verifie_bg.setVisibility(View.GONE);
//            }
//        }


        new CountDownTimer(32000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTxt.setText("00 : " + String.format("%02d", counter));
                counter--;
            }

            public void onFinish() {

            }
        }.start();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void verifieOtpBtnClick(View v) {
        String otp = otpField.getText().toString();
        if (!TextUtils.isEmpty(otp) && !TextUtils.isEmpty(user_id)) {
            verifieBtn.setEnabled(false);
            checkOtp(otp, user_id);
        } else {
            Toast.makeText(OtpScreenActivity.this, "Please Enter OTP", Toast.LENGTH_LONG).show();

        }
    }


    public void reSendOtpbtn(View v) {
//
        if (LoginScreen.getInstance() != null) {
            ((LoginScreen) LoginScreen.getInstance())
                    .closeActivity();
        }

        Intent intent = new Intent(OtpScreenActivity.this, LoginScreen.class);
        startActivity(intent);

        finish();


//        if (!AppNetworkAlertDialog.isNetworkConnected(OtpScreenActivity.this)) {
//            Toast.makeText(OtpScreenActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//
//            progressLayout.setVisibility(View.GONE);
//            return;
//        }
//        progressLayout.setVisibility(View.VISIBLE);
//
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
//                ApiRequest.BASE_URL_VERSION_3 + ApiRequest.GENERATE_OTP, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.e("GENRATE_otp_api", response);
//                progressLayout.setVisibility(View.GONE);
//                try {
//                    JSONObject mObj = new JSONObject(response);
//                    if (mObj.optInt("code") == 1) {
//                        Log.e("OTP-FROM-VEQTA", "GENRATE_otp_api" + mObj.getString("result"));
//                        Verifie_bg.setVisibility(View.VISIBLE);
//                        mobileNumber_bg.setVisibility(View.GONE);
//                        Toast.makeText(OtpScreenActivity.this,  "" + mObj.getString("result"), Toast.LENGTH_LONG).show();
//                    } else {
//                        String error = new String(mObj.optString("error"));
//                        progressLayout.setVisibility(View.GONE);
//                        if (!TextUtils.isEmpty(error))
//                            Toast.makeText(OtpScreenActivity.this,  "" + error, Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                    Log.e("OTP-FROM-VEQTA", "Error" + "" + e.getMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("OTP-FROM-VEQTA", "Error: " + error.getMessage());
//
//                progressLayout.setVisibility(View.GONE);
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("type", "mobile");
//                params.put("user_id", user_id);
//                params.put("value", mobileNumber);
//
//                return params;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }


    private void checkOtp(final String otp, final String user_id) {
        if (!AppNetworkAlertDialog.isNetworkConnected(OtpScreenActivity.this)) {
            Toast.makeText(OtpScreenActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            verifieBtn.setEnabled(true);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.VERIFY_OTP), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Verify_otp_api", response);
                progressBar.setVisibility(View.GONE);
                verifieBtn.setEnabled(true);

                try {
                    JSONObject mObj = new JSONObject(response);
                    MultitvCipher mcipher = new MultitvCipher();
                    if (mObj.optInt("code") == 1) {
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Log.e("SIGNUP-FROM-VEQTA", "Verify_otp_api" + str);


                        sharedPreference.setPreferenceBoolean(OtpScreenActivity.this, sharedPreference.KEY_IS_OTP_VERIFIED);
                        sharedPreference.setPreferenceBoolean(OtpScreenActivity.this, sharedPreference.KEY_IS_LOGGED_IN);

                        User user = Json.parse(str.trim(), User.class);

                        sharedPreference.setEmailId(OtpScreenActivity.this, "email_id", user.email);
                        sharedPreference.setUserName(OtpScreenActivity.this, "first_name", user.first_name);
                        sharedPreference.setUserLastName(OtpScreenActivity.this, "last_name", user.last_name);
                        if (!TextUtils.isEmpty(user.contact_no)) {
                            sharedPreference.setPhoneNumber(OtpScreenActivity.this, "phone", user.contact_no);
                        }
                        sharedPreference.setPreferencesString(OtpScreenActivity.this, "user_id" + "_" + ApiRequest.TOKEN, "" + user.id);
                        progressBar.setVisibility(View.GONE);

                        if (!TextUtils.isEmpty(user.provider)) {
                            sharedPreference.setFromLogedIn(OtpScreenActivity.this, "fromLogedin", user.provider);
                            String mobile = sharedPreference.getPhoneNumber(OtpScreenActivity.this, "phone");
                            if (!TextUtils.isEmpty(mobile)) {
                                sharedPreference.setPhoneNumber(OtpScreenActivity.this, "phone", mobile);
                            }

                        } else {
                            sharedPreference.setFromLogedIn(OtpScreenActivity.this, "fromLogedin", "veqta");
                        }
                        sharedPreference.setLoginOtpSentStatus(OtpScreenActivity.this, "status", "1");

                        createAppSession();

                        moveToHomeScreen();
                    } else {
                        String error = new String(mcipher.decryptmyapi(mObj.optString("error")));
                        if (!TextUtils.isEmpty(error))
                            Toast.makeText(OtpScreenActivity.this, "Incorrect OTP", Toast.LENGTH_LONG).show();

                        verifieBtn.setEnabled(true);
                    }
                } catch (Exception e) {
                    verifieBtn.setEnabled(true);
                    Log.e("SIGNUP-FROM-VEQTA", "Error" + "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SIGNUP-FROM-VEQTA", "Error: " + error.getMessage());
                verifieBtn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("user_id", user_id);

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void moveToHomeScreen() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(OtpScreenActivity.this, HomeActivity.class);


                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 10);

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

        if (id == NotificationCenter.didReceiveSmsCode) {

            Log.d(this.getClass().getName(), "OTP received===" + String.valueOf(args[0]));

            otpField.setText(String.valueOf(args[0]));

//            if (otpField.getText() != null && otpField.getText().toString().length() > 0) {
//                checkOtp(otpField.getText().toString(), user_id);
//            }

            Utilities.setWaitingForSms(false);

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;


            default:
                return false;
        }

    }


    public void createAppSession() {
        Log.d(this.getClass().getName(), " app session url---->>>" + AppController.getInstance().getAppSessionId());


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
                            AppSessionUtil1.sendHeartBeat(OtpScreenActivity.this);
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
                params.put("customer_id", new SharedPreference().getPreferencesString(OtpScreenActivity.this, "user_id" + "_" + ApiRequest.TOKEN));


                double lat = 0, lng = 0;
                GPSTracker tracker = new GPSTracker(OtpScreenActivity.this);
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
