package com.multitv.yuv.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.multitv.multitvcommonsdk.utils.GPSTracker;
import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.controller.SignUpController;
import com.multitv.yuv.firebase.FCMController;
import com.multitv.yuv.interfaces.SignUpListener;
import com.multitv.yuv.models.User;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.AppNetworkAlertDialog;
import com.multitv.yuv.utilities.AppSessionUtil1;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.multitv.yuv.utilities.Utilities.hideKeyboard;

public class LoginScreen extends AppCompatActivity implements SignUpListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;

    private SharedPreference sharedPreference;
    private TextView signupBtn, forgetPassTxt;
    private EditText email_field, passwordField;
    private int loginThrough;
    private Button signInBtn;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProgressBar progressBar;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private int RC_SIGN_IN = 3;
    private boolean isUpdatingInfoOnServer;
    private static Context context;


    private String mGender = "", mFirstName = "", mLastName = "", mUserName = "", mDob = "", mEmail = "", mPhoneNum, mPassword = "";


    private TextInputLayout input_email, input_password;

    public static Context getInstance() {
        return context;
    }

    public void closeActivity() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("LOGIN");
        setSupportActionBar(toolbar);
        context = this;

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(LoginScreen.this);
        FCMController.getInstance(getApplicationContext()).registerToken();
        sharedPreference = new SharedPreference();
        signupBtn = (TextView) findViewById(R.id.signupBtn);
        forgetPassTxt = (TextView) findViewById(R.id.forgetPassTxt);
        email_field = (EditText) findViewById(R.id.username);
        input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_password = (TextInputLayout) findViewById(R.id.input_password);
        passwordField = (EditText) findViewById(R.id.password);
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        progressBar = (ProgressBar) findViewById(R.id.progress_signin);
        signInBtn = (Button) findViewById(R.id.signInBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignupActivityNew.class);
                startActivity(intent);
            }
        });

        forgetPassTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, ForgetPassword.class);
                startActivity(intent);

            }
        });

        initGoogleFb();
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
                    Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    if (validate()) {
                        String password = passwordField.getText().toString();
                        if (!TextUtils.isEmpty(password)) {
                            mPassword = password;
                        }
                        String email = email_field.getText().toString();
                        if (!TextUtils.isEmpty(email)) {
                            mEmail = email;
                        }
                        //String password = etPassword.getText().toString();
                        loginThrough = AppConstants.LOGIN_THROUGH_MULTITV;
                        if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
                            Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        } else {
                            sendInfoToServerAndLoginResponse(mEmail, mPassword);
                        }

                    }
                }

            }
        });
    }

    private void initGoogleFb() {
        loginButton = new LoginButton(this);
        callbackManager = CallbackManager.Factory.create();
        //=======google plus login from custom button click============================================================
        //  ===========================================================================
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                /*.requestIdToken(getString(R.string.default_web_client_id))*/
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean validate() {
        boolean valid = true;
        String email = email_field.getText().toString();
        String password = passwordField.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(password)) {
            input_password.setError("Password field cannot be left blank");
            valid = false;
        }

        if (password.length() < 8 && password.length() > 0) {
            input_password.setError(" The password you entered is incorrect");
            valid = false;
        }

        if (!email.matches(emailPattern)) {
            input_email.setError("Please enter a valid Email Address");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            input_email.setError("Email field cannot be left blank");
            valid = false;
        }
        return valid;
    }


    public void sendInfoToServerAndLoginResponse(final String email, final String password) {
        if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
            Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + ApiRequest.LOGIN_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("LoginActivity", "LoginResponce-URL_:_" + response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {

                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        User user = Json.parse(str.trim(), User.class);
                        Log.e("LoginActivity", "***code-1**" + str);

                            if (!TextUtils.isEmpty(user.gender))
                             sharedPreference.setGender(LoginScreen.this, Constant.GENDER_KEY, user.gender);


                        if (!TextUtils.isEmpty(user.location))
                            sharedPreference.setUserLocation(LoginScreen.this, Constant.LOCATION_KEY, user.location);

                        sharedPreference.setEmailId(LoginScreen.this, Constant.EMAIL_KEY, user.email);
                        sharedPreference.setUserName(LoginScreen.this, Constant.USERNAME_KEY, user.first_name);
                        sharedPreference.setPhoneNumber(LoginScreen.this, Constant.MOBILE_NUMBER_KEY, user.contact_no);
                        sharedPreference.setPassword(LoginScreen.this, "password", user.app_session_id);

                        if (!TextUtils.isEmpty(user.image))
                            sharedPreference.setImageUrl(LoginScreen.this, Constant.IMAGE_URL_KEY, user.image);

                        if (!TextUtils.isEmpty(user.age_group))
                            sharedPreference.setDob(LoginScreen.this, Constant.AGEGROUP_KEY, user.age_group);

                        sharedPreference.setPreferencesString(LoginScreen.this, "user_id" + "_" + ApiRequest.TOKEN, "" + user.id);

                        String otpStatus = sharedPreference.getLoginOtpSentStatus(LoginScreen.this, "status");
                        if (!TextUtils.isEmpty(user.id)) {
                            if (otpStatus.equalsIgnoreCase("1")) {
                                sharedPreference.setFromLogedIn(LoginScreen.this, "fromLogedin", "veqta");
                                sharedPreference.setUserIfLoginVeqta(LoginScreen.this, "through", "1");
                                moveToHomeScreen();
                            } else {
                                if (!TextUtils.isEmpty(user.contact_no) && TextUtils.isEmpty(user.otp)) {
                                    sharedPreference.setFromLogedIn(LoginScreen.this, "fromLogedin", "veqta");
                                    sharedPreference.setUserIfLoginVeqta(LoginScreen.this, "through", "1");
                                    moveToHomeScreen();
                                } else {
                                    moveToOtpScreen();
                                }
                            }
                        } else {
                            Toast.makeText(LoginScreen.this, getString(R.string.login_error_msg), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = mObj.optString("result");
                        Log.e("LoginActivity", "***code-0**" + error);
                        Toast.makeText(LoginScreen.this, getString(R.string.login_error_msg), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (
                        Exception e
                        )

                {
                    Log.e("LoginActivity", "Error" + "" + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            }
        }

                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.e("LoginActivity", "****LoginApi****" + "Error: " + error.getMessage());
            }
        }

        )

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    final String model = Build.MODEL;
                    final String networkType = getNetType();

                    final String token = FirebaseInstanceId.getInstance().getToken();
                    if (token != null) {
                    }

                    final String android_id = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    final String carrierName = manager.getNetworkOperatorName();

                    final int os_version_code = android.os.Build.VERSION.SDK_INT;

                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;

                    final String resolution = height + "*" + width;

                    String dodjson = "";
                    String ddjson = "";

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("os_version", String.valueOf(os_version_code));
                        jsonObject.put("app_version", "1.0");
                        jsonObject.put("network_type", networkType);
                        jsonObject.put("network_provider", carrierName);
                        dodjson = jsonObject.toString();
                    } catch (JSONException e) {
                        Log.e("LOGIN_PARAM", "getParams:1 " + e.getMessage());
                    }

                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("make_model", model);
                        jsonObject1.put("os", "android");
                        jsonObject1.put("screen_resolution", resolution);
                        jsonObject1.put("push_device_token", token == null ? "" : token);
                        jsonObject1.put("device_type", "mobile");
                        jsonObject1.put("platform", "android");
                        jsonObject1.put("device_unique_id", android_id);

                        ddjson = jsonObject1.toString();
                    } catch (JSONException e) {
                        Log.e("LOGIN_PARAM", "getParams:2 " + e.getMessage());
                    }

                    Map<String, String> params = new HashMap<>();

                    params.put("email", email);
                    params.put("password", password);
                    params.put("devicedetail", ddjson);
                    params.put("device_other_detail", dodjson);
                    Set<String> keySet = params.keySet();
                    for (String key : keySet) {
                        Log.e("LOGIN_PARAM", "getParams: " + key + "   " + params.get(key));
                    }
                    return params;
                } catch (Exception e) {
                    Log.e("LoginApi1", e.getMessage());
                } catch (IncompatibleClassChangeError e) {
                    Log.e("LoginApi2", e.getMessage());
                }

                return null;
            }
        };
        jsonObjReq.setRetryPolicy(new

                DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );
        // Adding request to request queue
        AppController.getInstance().

                addToRequestQueue(jsonObjReq);

    }


    public String getNetType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetInfo) {
            Log.e("getNetType : ", activeNetInfo.toString());
            if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                int type = mTelephonyManager.getNetworkType();
                if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN || type == TelephonyManager.NETWORK_TYPE_GPRS || type == TelephonyManager.NETWORK_TYPE_EDGE) {
                    return "mobile";
                } else {
                    return "Other";
                }
            }
        }
        return "No Internet Network";
    }


    public void moveToOtpScreen() {
        Intent intent = new Intent(LoginScreen.this, OtpScreenActivity.class);
        startActivity(intent);
        finish();
    }


    public void moveToHomeScreen() {
        sharedPreference.setPreferenceBoolean(LoginScreen.this, sharedPreference.KEY_IS_OTP_VERIFIED);
        sharedPreference.setPreferenceBoolean(LoginScreen.this, sharedPreference.KEY_IS_LOGGED_IN);
        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    private void clearSharePrefernces() {
        sharedPreference.setFromLogedIn(LoginScreen.this, "fromLogedin", "");
        sharedPreference.setUserIfLoginVeqta(LoginScreen.this, "through", "");
        sharedPreference.setGender(LoginScreen.this, Constant.GENDER_KEY, "");
        sharedPreference.setEmailId(LoginScreen.this, Constant.EMAIL_KEY, "");
        sharedPreference.setUserName(LoginScreen.this, Constant.USERNAME_KEY, "");
        sharedPreference.setPhoneNumber(LoginScreen.this, Constant.MOBILE_NUMBER_KEY, "");
        sharedPreference.setPassword(LoginScreen.this, "password", "");
        sharedPreference.setImageUrl(LoginScreen.this, Constant.IMAGE_URL_KEY, "");
        sharedPreference.setDob(LoginScreen.this, Constant.AGEGROUP_KEY, "");
        sharedPreference.setLoginOtpSentStatus(this, "status", "");

        sharedPreference.setPreferencesString(LoginScreen.this, "user_id" + "_" + ApiRequest.TOKEN, "");


        AppController.getInstance().setAppSessionId("");
        AppController.getInstance().setContentSessionId("");
    }


    public void loginFromGoogle(View v) {
        if (isUpdatingInfoOnServer)
            return;
        if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
            Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }


    public void loginFromFacebook(View v) {
        if (isUpdatingInfoOnServer)
            return;
        if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
            Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        } else {
            fb_login();
        }
    }

    private void fb_login() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setReadPermissions(new String[]{"email", "user_hometown", "user_likes", "public_profile"});
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.e("accessToken", accessToken);
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject obj,
                                                    GraphResponse response) {
                                progressBar.setVisibility(View.GONE);
                                Log.e("facebookResponce===", response.toString());
                                try {
                                    final String firstName = obj.optString("first_name");
                                    final String lastName = obj.optString("last_name");
                                    final String gender = obj.optString("gender");
                                    final String mail_id = obj.optString("email");
                                    Log.e("id", obj.optString("id"));
                                    String id = obj.optString("id");

                                    if (!TextUtils.isEmpty(gender)) {
                                        mGender = gender;
                                    }
                                    String name = firstName;
                                    if (!TextUtils.isEmpty(lastName)) {
                                        name = name.concat(" " + lastName);
                                    }
                                    mFirstName = firstName;
                                    mLastName = lastName;
                                    mEmail = mail_id;
                                    mUserName = name;

                                    loginThrough = AppConstants.LOGIN_THROUGH_FB;

                                    if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
                                        Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

                                        progressBar.setVisibility(View.GONE);
                                        return;
                                    } else {
                                        updateInfoToServer(id, mFirstName, mLastName, mGender, "", "", mUserName, mEmail,
                                                "", mDob, loginThrough, mPhoneNum);
                                    }


                                    // profilePicUrlFromFb = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Log.e("first_name", obj.optString("first_name"));
                                    Log.e("last_name", obj.optString("last_name"));
                                    Log.e("gender", obj.optString("gender"));
                                    Log.e("name", obj.optString("name"));
                                    Log.e("link", obj.optString("link"));
                                    Log.e("locale", obj.optString("locale"));
                                    Log.e("Email:", obj.optString("email"));
                                    if (!TextUtils.isEmpty(id)) {
                                        final String URL_FB_IMAGE = "http://graph.facebook.com/" + id + "/picture?type=large&redirect=false";

                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                getProfileFromFb(URL_FB_IMAGE, firstName, lastName, gender, mail_id);
                                            }
                                        });

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("Fb-error: ", e.getMessage().toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(LoginScreen.this, getString(R.string.fb_error), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture,gender,locale,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException exception) {
                progressBar.setVisibility(View.GONE);
                exception.printStackTrace();
                try {
                    Toast.makeText(LoginScreen.this, getString(R.string.fb_error), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("SignUpActivity", e.getMessage());
                }
            }
        });
        loginButton.performClick();
    }


    private void updateInfoToServer(String id, String fName, String lName, String gender, String link, String locale, String name, String email, String location, String dob, int loginThrough, String phoneNo) {
        if (!(progressBar.getVisibility() == View.VISIBLE))
            progressBar.setVisibility(View.VISIBLE);
        isUpdatingInfoOnServer = true;
        SignUpController signUpController = new SignUpController(this, this);
        signUpController.sendInfoToServer(id, fName, lName, gender, link, locale, name, email, location, dob, loginThrough, phoneNo);
    }


    @Override
    public void onSuccess(String resultString) {
        isUpdatingInfoOnServer = false;

        if ((progressBar.getVisibility() == View.VISIBLE))
            progressBar.setVisibility(View.GONE);

        try {
            JSONObject mObj = new JSONObject(resultString);
            if (mObj.optInt("code") == 1) {
                com.multitv.cipher.MultitvCipher mcipher = new com.multitv.cipher.MultitvCipher();
                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                Log.e("#####Social-Login####", "#RESPONCE###" + str);
                User user = Json.parse(str.trim(), User.class);

                String provider = user.provider;
                if (!TextUtils.isEmpty(provider)) {
                    if (!TextUtils.isEmpty(user.gender))
                        sharedPreference.setGender(LoginScreen.this, Constant.GENDER_KEY, user.gender);



                    if (!TextUtils.isEmpty(user.location))
                        sharedPreference.setUserLocation(LoginScreen.this, Constant.LOCATION_KEY, user.location);

                    if (!TextUtils.isEmpty(user.email))
                        sharedPreference.setEmailId(LoginScreen.this, Constant.EMAIL_KEY, user.email);

                    if (!TextUtils.isEmpty(user.first_name))
                        sharedPreference.setUserName(LoginScreen.this, Constant.USERNAME_KEY, user.first_name);

                    if (!TextUtils.isEmpty(user.first_name) && !TextUtils.isEmpty(user.last_name))
                        sharedPreference.setUserName(LoginScreen.this, Constant.USERNAME_KEY, user.first_name + " " + user.last_name);


                    if (!TextUtils.isEmpty(user.contact_no))
                        sharedPreference.setPhoneNumber(LoginScreen.this, Constant.MOBILE_NUMBER_KEY, user.contact_no);


                    sharedPreference.setPassword(LoginScreen.this, "password", user.app_session_id);

                    if (!TextUtils.isEmpty(user.image))
                        sharedPreference.setImageUrl(LoginScreen.this, Constant.IMAGE_URL_KEY, user.image);

                    if (!TextUtils.isEmpty(user.age_group))
                        sharedPreference.setDob(LoginScreen.this, "dob", user.age_group);
                } else {
                    sharedPreference.setEmailId(this, "email_id", mEmail);
                }

                Log.e("LOGIN", "onResponse: " + user.id);

                if (!TextUtils.isEmpty(user.id)) {
                    String otpVerifie = sharedPreference.getLoginOtpSentStatus(LoginScreen.this, "status");
                    sharedPreference.setPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN, "" + user.id);
                    sharedPreference.setPreferenceBoolean(this, sharedPreference.KEY_IS_LOGGED_IN);
                    createAppSession(user, otpVerifie, user.otp);
                }

            } else {
                String error = mObj.optString("error");
                if (!TextUtils.isEmpty(error))
                    Toast.makeText(LoginScreen.this, error, Toast.LENGTH_LONG).show();


            }
        } catch (Exception e) {
            Log.e("error", "" + e.getMessage());
        }
    }


    public void getProfileFromFb(String url, final String fname, final String lName, final String gender, final String email) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("***FACEBOOK_IMAGE***", response);
                    JSONObject mObj = new JSONObject(response);
                    try {
                        JSONObject newObj = mObj.getJSONObject("data");
                        String profilePicUrlFromFb = newObj.optString("url");
                        Log.e("***FB_IMAGE_URL***", profilePicUrlFromFb);
                        if (!TextUtils.isEmpty(profilePicUrlFromFb))
                            sharedPreference.setImageUrl(LoginScreen.this, Constant.IMAGE_URL_KEY, profilePicUrlFromFb);
                    } catch (JSONException e) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error: " + error.getMessage());

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    private void createAppSession(final User user, final String otpVerifie, final String otp) {
        progressBar.setVisibility(View.VISIBLE);

        Log.e("API_REQUEST", "Create app session url : " + AppUtils.generateUrl(getApplicationContext(), ApiRequest.APP_SESSION));

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.APP_SESSION), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("Create app session api-", response.toString());
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        com.multitv.cipher.MultitvCipher mcipher = new com.multitv.cipher.MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Log.e("Create app session api-", str);

                        if (!TextUtils.isEmpty(str)) {
                            AppController.getInstance().setAppSessionId(str.trim());
                            if (!TextUtils.isEmpty(AppController.getInstance().getAppSessionId()))
                                AppSessionUtil1.sendHeartBeat(LoginScreen.this);
                        }

                        if (!TextUtils.isEmpty(user.contact_no) && !TextUtils.isEmpty(user.otp) && !TextUtils.isEmpty(user.provider)) {
                            sharedPreference.setPhoneNumber(LoginScreen.this, "phone", user.contact_no);
                            sendOtp(user.contact_no, user.id, user.provider);
                        } else if (!TextUtils.isEmpty(user.contact_no) && TextUtils.isEmpty(user.otp)) {
                            sharedPreference.setFromLogedIn(LoginScreen.this, "fromLogedin", user.provider);
                            sharedPreference.setUserIfLoginVeqta(LoginScreen.this, "through", "1");
                            sharedPreference.setPhoneNumber(LoginScreen.this, "phone", user.contact_no);
                            moveToHomeScreen();
                        } else if (TextUtils.isEmpty(user.contact_no) && !TextUtils.isEmpty(user.otp)) {
                            moveToOtpScreenforSocial();
                        } else if (TextUtils.isEmpty(user.contact_no) && TextUtils.isEmpty(user.otp)) {
                            moveToOtpScreenforSocial();
                        }


                    } else if (mObj.optInt("code") == 0) {
                        Log.e("Create app session api-", response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("####FB-CATCH", "" + e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoginActivity", "Error: " + error.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("device", "android");
                params.put("token", ApiRequest.TOKEN);
                params.put("customer_device", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                params.put("customer_id", new SharedPreference().getPreferencesString(LoginScreen.this, "user_id" + "_" + ApiRequest.TOKEN));

                double lat = 0, lng = 0;
                GPSTracker tracker = new GPSTracker(LoginScreen.this);
                if (tracker.canGetLocation()) {
                    lat = tracker.getLatitude();
                    lng = tracker.getLongitude();
                }
                params.put("lat", "" + lat);
                params.put("long", "" + lng);

                Set<String> keys = params.keySet();
                for (String key : keys) {
                    Log.e("LoginActivity", "createAppSession().getParams: " + key + "      " + params.get(key));
                }

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void sendOtp(final String mobileNumber, final String user_id, final String provider1) {
        if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
            Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + ApiRequest.GENERATE_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("GENRATE_otp_api", response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        Log.e("OTP-FROM-VEQTA", "GENRATE_otp_api" + mObj.getString("result"));

                        Toast.makeText(LoginScreen.this, "" + mObj.getString("result"), Toast.LENGTH_LONG).show();


                        Intent intent = new Intent(LoginScreen.this, OtpScreenActivity.class);
                        intent.putExtra("getOtp", "RECEIVED");
                        intent.putExtra("phone", mobileNumber);
                        intent.putExtra("provider", provider1);
                        startActivity(intent);
                        LoginScreen.this.finish();

                    } else {
                        String error = new String(mObj.optString("result"));
                        progressBar.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(error))
                            Toast.makeText(LoginScreen.this, error, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("OTP-FROM-VEQTA", "Error" + "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OTP-FROM-VEQTA", "Error: " + error.getMessage());

                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "mobile");
                params.put("user_id", user_id);
                params.put("value", mobileNumber);

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void moveToOtpScreenforSocial() {


        sharedPreference.setPreferenceBoolean(LoginScreen.this, sharedPreference.KEY_IS_OTP_VERIFIED);
        sharedPreference.setPreferenceBoolean(LoginScreen.this, sharedPreference.KEY_IS_LOGGED_IN);
        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        intent.putExtra("getOtp", "NOT_RECEIVED");
        startActivity(intent);
        LoginScreen.this.finish();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        logOutFromGoogle();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void logOutFromGoogle() {
        if (mGoogleApiClient != null && (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            progressBar.setVisibility(View.GONE);
                                            clearSharePrefernces();
                                            mGoogleApiClient.disconnect();
                                        }
                                    });
                        }
                    });

            /*mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    mGoogleApiClient.disconnect();
                    Log.e("Naseeb", "Status " + status.getStatusCode());
                }
            });*/
        }
    }


    @Override
    public void onError() {
        isUpdatingInfoOnServer = false;

        if ((progressBar.getVisibility() == View.VISIBLE))
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.VISIBLE);
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(googleSignInResult);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //==============google plus login handle result from getting user information==============================================
    //=========================================================================================
    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("Result..... ", "handleSignInResult:" + result.getStatus().getStatusMessage());
        //progressBar.setVisibility(View.GONE);
        if (result.isSuccess()) {
            GoogleSignInAccount information = result.getSignInAccount();
            String personName = information.getDisplayName();
            String gmail_id = information.getEmail();
            String first_google_name = "", last_google_name = "";
            String[] splited = personName.split("\\s+");
            String id = information.getId();
            if (splited.length > 0) {
                first_google_name = splited[0];
                if (splited.length > 1)
                    last_google_name = splited[1];
            }
            if (!TextUtils.isEmpty(last_google_name)) {
                sharedPreference.setGoogleLoginLastName(this, "googleLastName", last_google_name);
            }

            Log.e("person name", personName);
            Log.e("email", gmail_id);

            sharedPreference.setGoogleLoginUsername(this, "googleNAme", first_google_name);
            sharedPreference.setGoogleLoginEmail(this, "emailFromGoogle", gmail_id);
            String profilePic = null;
            if (information.getPhotoUrl() != null) {
                profilePic = information.getPhotoUrl().toString();
                sharedPreference.setGoogleLoginProfilePic(LoginScreen.this, "ImageGoogleProfile", profilePic);
                sharedPreference.setImageUrl(LoginScreen.this, Constant.IMAGE_URL_KEY, profilePic);
                Log.e("personPhotoUrl", profilePic);
            } else {
                Log.e("personPhotoUrl", "Empty");
            }
            mFirstName = first_google_name;
            mLastName = last_google_name;
            mUserName = personName;
            mEmail = gmail_id;
            loginThrough = AppConstants.LOGIN_THROUGH_GOOGLE;


            if (!AppNetworkAlertDialog.isNetworkConnected(LoginScreen.this)) {
                Toast.makeText(LoginScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return;
            } else {
                updateInfoToServer(id, mFirstName, mLastName, mGender, "", "", mUserName, mEmail, "", mDob,
                        loginThrough, mPhoneNum);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginScreen.this, "Google Login Failed", Toast.LENGTH_LONG).show();

        }
    }


}

