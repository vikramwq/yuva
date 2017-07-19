//package com.multitv.yuv.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.internal.view.SupportMenuItem;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.telephony.TelephonyManager;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.text.style.ForegroundColorSpan;
//import android.text.style.StyleSpan;
//import android.text.style.UnderlineSpan;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.multitv.yuv.R;
//import com.multitv.yuv.api.ApiRequest;
//import com.multitv.yuv.application.AppController;
//import com.multitv.yuv.controller.SignUpController;
//import com.multitv.yuv.interfaces.SignUpListener;
//import com.multitv.yuv.locale.LocaleHelper;
//import com.multitv.yuv.models.User;
//import com.multitv.yuv.sharedpreference.SharedPreference;
//import com.multitv.yuv.utilities.AppConstants;
//import com.multitv.yuv.utilities.AppNetworkAlertDialog;
//import com.multitv.yuv.utilities.AppSessionUtil1;
//import com.multitv.yuv.utilities.AppUtils;
//import com.multitv.yuv.utilities.Constant;
//import com.multitv.yuv.utilities.ExceptionUtils;
//import com.multitv.yuv.utilities.Json;
//import com.multitv.yuv.utilities.MultitvCipher;
//import com.multitv.yuv.utilities.Tracer;
//import com.multitv.yuv.utilities.Utilities;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.Scopes;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.Scope;
//import com.multitv.multitvcommonsdk.utils.GPSTracker;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
////import com.wamindia.dollywoodplay.utilities.AppSessionUtil;
//
///**
// * Created by arungoyal on 27/04/17.
// */
//
//public class SignupScreen extends AppCompatActivity implements SignUpListener, GoogleApiClient.OnConnectionFailedListener {
//
//    private static Context context;
//    private EditText email_et, phone_et;
//
//    private CallbackManager callbackManager;
//    private ProgressBar progressBar;
//    private GoogleApiClient mGoogleApiClient;
//    private GoogleSignInOptions gso;
//    private int RC_SIGN_IN = 3;
//    private int loginThrough;
//    private LoginButton loginButton;
//    //    private TextInputLayout input_email, input_phone_field;
//    private SharedPreference sharedPreference;
//    private int usedForLogin;
//    //    private Button skipBtn;
//    MenuInflater mInflater;
//    Menu menu1;
//
//    private String mGender = "", mFirstName = "", mLastName = "", mUserName = "", mDob = "", mEmail = "", mPhoneNum;
//
//    private Button signInBtn;
//    private Toolbar toolbar;
//    private TextView termsTxt;
//
//    public static Context getInstance() {
//        return context;
//    }
//
//    public void closeActivity() {
//        finish();
//    }
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        setContentView(R.layout.signup_screen);
//
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Sign in to " + getResources().getString(R.string.app_name));
//        toolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(toolbar);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Utilities.applyFontForToolbarTitle(SignupScreen.this);
//
////        input_email = (TextInputLayout) findViewById(R.id.input_email);
////        input_phone_field = (TextInputLayout) findViewById(R.id.input_phone_field);
//
//        email_et = (EditText) findViewById(R.id.email_et);
//        phone_et = (EditText) findViewById(R.id.phone_et);
//        sharedPreference = new SharedPreference();
//        progressBar = (ProgressBar) findViewById(R.id.progress_signin);
//        signInBtn = (Button) findViewById(R.id.signInBtn);
//        termsTxt = (TextView) findViewById(R.id.termsTxt);
//
//        if (sharedPreference.getPreferenceBoolean(this, Constant.ALREADY_VISITED)) {
//            toolbar.setTitle("Log In to " + getResources().getString(R.string.app_name));
//        } else {
//            toolbar.setTitle("Sign Up to " + getResources().getString(R.string.app_name));
//        }
//
//
//        addText();
//
//        initGoogleFb();
//
//
//        email_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
//
//                    Log.d(this.getClass().getName(), "enter pressed");
//
//                    SignInButtongoToHome(v);
//
//                    handled = true;
//
//                }
//                return handled;
//            }
//        });
//
//        phone_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
//
//                    Log.d(this.getClass().getName(), "enter pressed");
//
//                    SignInButtongoToHome(v);
//
//                    handled = true;
//
//                }
//                return handled;
//            }
//        });
//
//
//        Utilities.hideKeyboard(phone_et);
//
//    }
//
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//
//    public void loginFromFacebook(View v) {
//        if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//            Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            return;
//        } else {
//            fb_login();
//        }
//    }
//
//    public void loginFromGoogle(View v) {
//        if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//            Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            return;
//        } else {
//            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//            startActivityForResult(signInIntent, RC_SIGN_IN);
//        }
//    }
//
//    private void fb_login() {
//        progressBar.setVisibility(View.VISIBLE);
//        loginButton.setReadPermissions(new String[]{"email", "user_hometown", "user_likes", "public_profile"});
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
//                String accessToken = loginResult.getAccessToken().getToken();
//                Log.e("accessToken", accessToken);
//                final GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject obj,
//                                                    GraphResponse response) {
//
//                                progressBar.setVisibility(View.GONE);
//
//                                Log.e("facebookResponce===", response.toString());
//                                try {
//                                    final String firstName = obj.optString("first_name");
//                                    final String lastName = obj.optString("last_name");
//                                    final String gender = obj.optString("gender");
//                                    final String mail_id = obj.optString("email");
//                                    Log.e("id", obj.optString("id"));
//                                    String id = obj.optString("id");
//
//                                    if (!TextUtils.isEmpty(gender)) {
//                                        mGender = gender;
//                                    }
//                                    String name = firstName;
//                                    if (!TextUtils.isEmpty(lastName)) {
//                                        name = name.concat(" " + lastName);
//                                    }
//                                    mFirstName = firstName;
//                                    mLastName = lastName;
//                                    mEmail = mail_id;
//                                    mUserName = name;
//
//                                    loginThrough = AppConstants.LOGIN_THROUGH_FB;
//                                    updateInfoToServer(id, mFirstName, mLastName, mGender, "", "", mUserName, mEmail, "", mDob, loginThrough, mPhoneNum);
//
//                                    // profilePicUrlFromFb = data.getJSONObject("picture").getJSONObject("data").getString("url");
//                                    Log.e("first_name", obj.optString("first_name"));
//                                    Log.e("last_name", obj.optString("last_name"));
//                                    Log.e("gender", obj.optString("gender"));
//                                    Log.e("name", obj.optString("name"));
//                                    Log.e("link", obj.optString("link"));
//                                    Log.e("locale", obj.optString("locale"));
//                                    Log.e("Email:", obj.optString("email"));
//                                    if (!TextUtils.isEmpty(id)) {
//                                        final String URL_FB_IMAGE = "http://graph.facebook.com/" + id + "/picture?type=large&redirect=false";
//
//                                        AsyncTask.execute(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                getProfileFromFb(URL_FB_IMAGE, firstName, lastName, gender, mail_id);
//                                            }
//                                        });
//
//                                    }
//                                    //sharedPreference.setPhoneNumber(getActivity(), "phone", "");
//
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    Log.e("Fb-error: ", e.getMessage().toString());
//                                    progressBar.setVisibility(View.GONE);
//                                }
//
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,link,email,picture,gender,locale,first_name,last_name");
//                request.setParameters(parameters);
//                request.executeAsync();
//            }
//
//            @Override
//            public void onCancel() {
//                System.out.println("onCancel");
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                System.out.println("onError");
//                progressBar.setVisibility(View.GONE);
//
//                try {
//                    Log.e("SignUpActivity", exception.getCause().toString());
//                    Toast.makeText(SignupScreen.this, "" + exception.getCause(), Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    Log.e("SignUpActivity", e.getMessage());
//                }
//            }
//        });
//        loginButton.performClick();
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            progressBar.setVisibility(View.VISIBLE);
//            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(googleSignInResult);
//        }
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void initGoogleFb() {
//        loginButton = new LoginButton(this);
//        callbackManager = CallbackManager.Factory.create();
//        //=======google plus login from custom button click============================================================
//        //  ===========================================================================
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
//                /*.requestIdToken(getString(R.string.default_web_client_id))*/
//                .requestEmail()
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//    }
//
//
//    private void clearSharePrefernces() {
//
//        sharedPreference.setPreferenceBoolean(SignupScreen.this, sharedPreference.KEY_IS_OTP_VERIFIED, false);
//        sharedPreference.setPreferenceBoolean(SignupScreen.this, sharedPreference.KEY_IS_LOGGED_IN, false);
//        sharedPreference.setFromLogedIn(SignupScreen.this, "fromLogedin", "");
//        sharedPreference.setGender(SignupScreen.this, "gender_id", "");
//        sharedPreference.setEmailId(SignupScreen.this, "email_id", "");
//        sharedPreference.setUserName(SignupScreen.this, "first_name", "");
//        sharedPreference.setUserLastName(SignupScreen.this, "last_name", "");
//        sharedPreference.setPhoneNumber(SignupScreen.this, "phone", "");
//        sharedPreference.setPassword(SignupScreen.this, "password", "");
//        sharedPreference.setImageUrl(SignupScreen.this, "imgUrl", "");
//        sharedPreference.setDob(SignupScreen.this, "dob", "");
//        sharedPreference.setPreferencesString(SignupScreen.this, "user_id" + "_" + ApiRequest.TOKEN, "");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//
//    }
//
//
//    public void getProfileFromFb(String url, final String fname, final String lName, final String gender, final String email) {
//        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
//                url, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                try {
//                    Log.e("***FACEBOOK_IMAGE***", response);
//                    JSONObject mObj = new JSONObject(response);
//                    try {
//
//                        JSONObject newObj = mObj.getJSONObject("data");
//                        String profilePicUrlFromFb = newObj.optString("url");
//                        Log.e("***FB_IMAGE_URL***", profilePicUrlFromFb);
//                        if (!TextUtils.isEmpty(profilePicUrlFromFb))
//
//                            //updateLoginInformationListner.setProfileImage(profilePicUrlFromFb);
//                            sharedPreference.setImageUrl(SignupScreen.this, "imgUrl", profilePicUrlFromFb);
//                       /* sharedPreference.setEmailId(LoginActivity.this,"email",email);
//                        sharedPreference.setFirstName(LoginActivity.this,"email",fname);
//                        sharedPreference.setLastName(LoginActivity.this,"email",lName);
//                        sharedPreference.setGender(LoginActivity.this,"email",gender);*/
//
//                    } catch (JSONException e) {
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Error", "Error: " + error.getMessage());
//
//            }
//        });
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//
//    }
//
//
//    //==============google plus login handle result from getting user information==============================================
//    //=========================================================================================
//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.e("Result..... ", "handleSignInResult:" + result.getStatus().getStatusMessage());
//        //progressBar.setVisibility(View.GONE);
//        if (result.isSuccess()) {
//            GoogleSignInAccount information = result.getSignInAccount();
//            String personName = information.getDisplayName();
//            String gmail_id = information.getEmail();
//            String id = information.getId();
//            String first_google_name = "", last_google_name = "";
//            String[] splited = personName.split("\\s+");
//            if (splited.length > 0) {
//                first_google_name = splited[0];
//                if (splited.length > 1)
//                    last_google_name = splited[1];
//            }
//            if (!TextUtils.isEmpty(last_google_name)) {
//                sharedPreference.setGoogleLoginLastName(this, "googleLastName", last_google_name);
//            }
//
//            Log.e("person name", personName);
//            Log.e("email", gmail_id);
//
//            sharedPreference.setGoogleLoginUsername(this, "googleNAme", first_google_name);
//            sharedPreference.setGoogleLoginEmail(this, "emailFromGoogle", gmail_id);
//            String profilePic = null;
//            if (information.getPhotoUrl() != null) {
//                profilePic = information.getPhotoUrl().toString();
//                //updateLoginInformationListner.setProfileImage(profilePic);
//                sharedPreference.setGoogleLoginProfilePic(SignupScreen.this, "ImageGoogleProfile", profilePic);
//                sharedPreference.setImageUrl(SignupScreen.this, "imgUrl", profilePic);
//                Log.e("personPhotoUrl", profilePic);
//            } else {
//                Log.e("personPhotoUrl", "Empty");
//            }
//            mFirstName = first_google_name;
//            mLastName = last_google_name;
//            mUserName = personName;
//            mEmail = gmail_id;
//            loginThrough = AppConstants.LOGIN_THROUGH_GOOGLE;
//            updateInfoToServer(id, mFirstName, mLastName, mGender, "", "", mUserName, mEmail, "", mDob,
//                    loginThrough, mPhoneNum);
//
//        } else {
//            // Toast.makeText(getActivity(), "" + result.getStatus(), Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            Toast.makeText(SignupScreen.this, "Google Login failed", Toast.LENGTH_LONG).show();
//        }
//    }
//
//
//    private void updateInfoToServer(String id, String fName, String lName, String gender, String link, String locale, String name, String email, String location, String dob, int loginThrough, String phoneNo) {
//        if (!(progressBar.getVisibility() == View.VISIBLE))
//            progressBar.setVisibility(View.VISIBLE);
//        SignUpController signUpController = new SignUpController(this, this);
//        signUpController.sendInfoToServer(id, fName, lName, gender, link, locale, name, email, location, dob, loginThrough, phoneNo);
//    }
//
//
//    @Override
//    public void onError() {
//        sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.IS_SKIP_ENABLED, false);
//        signInBtn.setEnabled(true);
//        if ((progressBar.getVisibility() == View.VISIBLE))
//            progressBar.setVisibility(View.GONE);
//    }
//
//
//    @Override
//    public void onSuccess(String resultString) {
//        sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.IS_SKIP_ENABLED, false);
//        sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.ALREADY_VISITED, true);
//        signInBtn.setEnabled(true);
//        if ((progressBar.getVisibility() == View.VISIBLE))
//            progressBar.setVisibility(View.GONE);
//
//        try {
//            JSONObject mObj = new JSONObject(resultString);
//            if (mObj.optInt("code") == 1) {
//                MultitvCipher mcipher = new MultitvCipher();
//                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//                Log.e("LoginActivity", str);
//                User user = Json.parse(str.trim(), User.class);
//
//                if (!TextUtils.isEmpty(user.gender))
//                    mGender = user.gender;
//                if (!TextUtils.isEmpty(user.first_name))
//                    mFirstName = user.first_name;
//                else mFirstName = "";
//                if (!TextUtils.isEmpty(user.last_name))
//                    mLastName = user.last_name;
//                else mLastName = "";
//                if (!TextUtils.isEmpty(user.dob) && !user.dob.equals("0000-00-00"))
//                    mDob = user.dob;
//                if (!TextUtils.isEmpty(user.email))
//                    mEmail = user.email;
//
//
//                sharedPreference.setDob(this, "dob", mDob);
//
//
//                if (mGender.equalsIgnoreCase("male")) {
//                    sharedPreference.setGender(this, "gender_id", "" + 0);
//                } else if (mGender.equalsIgnoreCase("female")) {
//                    sharedPreference.setGender(this, "gender_id", "" + 1);
//                }
//
//                String provider = user.provider;
//                if (!TextUtils.isEmpty(provider)) {
//                    sharedPreference.setUserName(this, "first_name", mFirstName);
//                    sharedPreference.setUserLastName(this, "last_name", mLastName);
//
//                    sharedPreference.setEmailId(this, "email_id", mEmail);
//
//
//                } else {
//                    sharedPreference.setEmailId(this, "email_id", mEmail);
//                }
//
//
//                if (!TextUtils.isEmpty(user.image))
//                    sharedPreference.setImageUrl(this, "imgUrl", user.image);
//
//
//                Log.e("LOGIN", "onResponse: " + user.id);
//                //PreferenceData.setAppSessionId(this, user.app_session_id);
//                if (!TextUtils.isEmpty(user.id)) {
//                    sharedPreference.setPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN, "" + user.id);
//                    sharedPreference.setPreferenceBoolean(this, sharedPreference.KEY_IS_LOGGED_IN);
//
//
//                    if (loginThrough == 0) {
//
//
//                        if (user.otp != null) {
//
//                            Utilities.setWaitingForSms(true);
//
//                            Intent intent = new Intent(SignupScreen.this, OtpScreenActivity.class);
//                            intent.putExtra("getOtp", "RECEIVED");
//                            intent.putExtra("phone", mPhoneNum);
//                            intent.putExtra("provider", user.provider);
//                            intent.putExtra("usedForLogin", usedForLogin);
//                            startActivity(intent);
//                            SignupScreen.this.finish();
//
//                        } else {
//                            sendOtp(mPhoneNum, user.id, user.provider, usedForLogin);
//
//                        }
//                    } else {
//                        createAppSession();
//                        sharedPreference.setPreferenceBoolean(this, sharedPreference.KEY_IS_OTP_VERIFIED);
//                        Intent intent = new Intent(SignupScreen.this, HomeActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//
//
//                }
//                //FCMController.getInstance(getApplicationContext()).registerToken();
//            } else {
//                String error = mObj.optString("error");
//                if (!TextUtils.isEmpty(error))
//                    Toast.makeText(SignupScreen.this, error, Toast.LENGTH_LONG).show();
//
//            }
//        } catch (Exception e) {
//            Log.e("error", "" + e.getMessage());
//        }
//    }
//
//
//    public void SignInButtongoToHome(View v) {
//        hideKeyboard(v);
//        if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//
//            Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            return;
//        } else {
//
//
//            if (validatePhone()) {
//
////                input_email.setError(null);
//
//                usedForLogin = 2;
//                loginThrough = AppConstants.LOGIN_THROUGH_MULTITV;
//                if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//                    Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//                    progressBar.setVisibility(View.GONE);
//                    return;
//                } else {
//                    String phoneNumber = phone_et.getText().toString();
//                    if (!TextUtils.isEmpty(phoneNumber)) {
//                        mPhoneNum = phoneNumber;
//                    }
//                    signInBtn.setEnabled(false);
////                    input_email.setHintAnimationEnabled(false);
//
////                    input_email.setHintEnabled(false);
//                    sendInfoToServerAndLoginResponse(mEmail);
//                }
//
//
//            } else if (validateEmail()) {
//
//                phone_et.clearFocus();
//                usedForLogin = 1;
//                String email = email_et.getText().toString();
//                if (!TextUtils.isEmpty(email)) {
//                    mEmail = email;
//                }
//                //String password = etPassword.getText().toString();
//                loginThrough = AppConstants.LOGIN_THROUGH_MULTITV;
//                if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//                    Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//                    progressBar.setVisibility(View.GONE);
//                    return;
//                } else {
//                    signInBtn.setEnabled(false);
////                    input_email.setHintAnimationEnabled(false);
//                    email_et.requestFocus();
//                    sendInfoToServerAndLoginResponse(mEmail);
//                }
//
//            } else {
//                signInBtn.setEnabled(true);
//                Toast.makeText(SignupScreen.this, "Please enter proper email id or proper phone number", Toast.LENGTH_LONG).show();
//
//            }
//        }
//
//    }
//
//
//    private boolean validateEmail() {
//        boolean valid = true;
//        String email = email_et.getText().toString();
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//
//        if (TextUtils.isEmpty(email)) {
//            valid = false;
//            return valid;
//
//        } else {
////            input_email.setError(null);
//        }
//
//        if (!TextUtils.isEmpty(email) && !email.matches(emailPattern)) {
////            input_email.requestFocus();
////            input_email.setError("Please enter a valid Email Address");
//            valid = false;
//            return valid;
//
//        } else {
//
////            input_email.setError(null);
//
//        }
//
//
//        return valid;
//    }
//
//
//    private boolean validatePhone() {
//        boolean valid = true;
//
//        String phoneNumber = phone_et.getText().toString();
//
//
//        if (TextUtils.isEmpty(phoneNumber)) {
////            input_phone_field.requestFocus();
////            input_phone_field.setError("Phone Number can not be empty.");
//            valid = false;
//            return valid;
//        } else {
////            input_phone_field.setError(null);
//        }
//
//
//        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() < 10) {
////            input_phone_field.requestFocus();
////            input_phone_field.setError("Please enter proper phone number");
//            valid = false;
//            return valid;
//        } else {
////            input_phone_field.setError(null);
//        }
//
//
//        return valid;
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu1 = menu;
//        mInflater = getMenuInflater();
//        mInflater.inflate(R.menu.skip_menu, menu);
//
//
//        SupportMenuItem notification = (SupportMenuItem) menu
//                .findItem(R.id.skip);
//        View viewLayout = notification.getActionView();
//        TextView confirmBtn = (TextView) viewLayout.findViewById(R.id.textViewSave);
//
//        confirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                clearSharePrefernces();
//                Intent intent = new Intent(SignupScreen.this, HomeActivity.class);
//                startActivity(intent);
//
//                sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.IS_SKIP_ENABLED, true);
//                sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.ALREADY_VISITED, true);
//
//                finish();
//
//            }
//        });
//
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//
//                finish();
//                return true;
//            case R.id.skip:
//                clearSharePrefernces();
//                Intent intent = new Intent(SignupScreen.this, HomeActivity.class);
//                startActivity(intent);
//
//                sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.IS_SKIP_ENABLED, true);
//                sharedPreference.setPreferenceBoolean(SignupScreen.this, Constant.ALREADY_VISITED, true);
//
//                finish();
//
//
//                return true;
//
//            default:
//                return false;
//        }
//
//    }
//
//
//    private void hideKeyboard(View view) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
//
//
//    public void sendInfoToServerAndLoginResponse(final String email) {
//        if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//            Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            return;
//        }
//        progressBar.setVisibility(View.VISIBLE);
//        updateInfoToServer("", mFirstName, mLastName, mGender, "", "", mUserName, mEmail, "", mDob,
//                loginThrough, mPhoneNum);
//    }
//
//
//    private void sendOtp(final String mobileNumber, final String user_id, final String provider1, final int usedForLogin) {
//        if (!AppNetworkAlertDialog.isNetworkConnected(SignupScreen.this)) {
//            Toast.makeText(SignupScreen.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            return;
//        }
//        progressBar.setVisibility(View.VISIBLE);
//
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
//                AppUtils.generateUrl(getApplicationContext(), ApiRequest.GENERATE_OTP), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.e("GENRATE_otp_api", response);
//                progressBar.setVisibility(View.GONE);
//                try {
//                    JSONObject mObj = new JSONObject(response);
//                    if (mObj.optInt("code") == 1) {
//                        Log.e("OTP-FROM-VEQTA", "GENRATE_otp_api" + mObj.getString("result"));
//
//                        Toast.makeText(SignupScreen.this, "" + mObj.getString("result"), Toast.LENGTH_LONG).show();
//
//                        Utilities.setWaitingForSms(true);
//
//                        Intent intent = new Intent(SignupScreen.this, OtpScreenActivity.class);
//                        intent.putExtra("getOtp", "RECEIVED");
//                        intent.putExtra("phone", mobileNumber);
//                        intent.putExtra("provider", provider1);
//                        intent.putExtra("usedForLogin", usedForLogin);
//                        startActivity(intent);
//                        SignupScreen.this.finish();
//
//                    } else {
//                        String error = new String(mObj.optString("result"));
//                        progressBar.setVisibility(View.GONE);
//                        if (!TextUtils.isEmpty(error))
//                            Toast.makeText(SignupScreen.this, "" + error, Toast.LENGTH_LONG).show();
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
//                progressBar.setVisibility(View.GONE);
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//
//                if (usedForLogin == 1) {
//                    params.put("type", "email");
//                    params.put("value", mEmail);
//                } else {
//                    params.put("type", "mobile");
//                    params.put("value", mobileNumber);
//                }
//                params.put("user_id", user_id);
//
//
//                return params;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//    }
//
//
//    private void addText() {
//
//        SpannableString SpanString1 = new SpannableString(
//                "By clicking sign in, you agree to our terms and conditions and that you have read our Privacy Policy");
//
//        ClickableSpan tc = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//
//                Intent intent = new Intent(SignupScreen.this, WebViewActivity.class);
//                intent.putExtra("type", 1);
//                intent.putExtra("url", "http://automator.multitvsolution.com/cms/page?type=tc&token=" + ApiRequest.TOKEN);
//                startActivity(intent);
//
//
//            }
//        };
//
//        ClickableSpan privacy = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//
//
//                Intent intent = new Intent(SignupScreen.this, WebViewActivity.class);
//                intent.putExtra("type", 2);
//                intent.putExtra("url", "http://automator.multitvsolution.com/cms/page?type=privacy&token=" + ApiRequest.TOKEN);
//                startActivity(intent);
//
//
//            }
//        };
//
//        SpanString1.setSpan(tc, 38, 58, 0);
//        SpanString1.setSpan(privacy, 86, 100, 0);
//        SpanString1.setSpan(new ForegroundColorSpan(Color.BLUE), 38, 58, 0);
//        SpanString1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                38, 58, 0);
//        SpanString1.setSpan(new ForegroundColorSpan(Color.BLUE), 86, 100, 0);
//        SpanString1.setSpan(new UnderlineSpan(), 38, 58, 0);
//        SpanString1.setSpan(new UnderlineSpan(), 86, 100, 0);
//        SpanString1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                86, 100, 0);
//
//        termsTxt.setMovementMethod(LinkMovementMethod.getInstance());
//        termsTxt.setText(SpanString1, TextView.BufferType.SPANNABLE);
//        termsTxt.setSelected(true);
//
//    }
//
//
//    public void createAppSession() {
//        Log.d(this.getClass().getName(), " app session url---->>>" + AppController.getInstance().getAppSessionId());
//
//
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
//                AppUtils.generateUrl(getApplicationContext(), ApiRequest.APP_SESSION), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject mObj = new JSONObject(response);
//                    if (mObj.optInt("code") == 1) {
//                        com.multitv.cipher.MultitvCipher mcipher = new com.multitv.cipher.MultitvCipher();
//                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//                        Tracer.error("Create app session api ---", str);
//                        if (!TextUtils.isEmpty(str)) {
//                            AppController.getInstance().setAppSessionId(str.trim());
//                            AppSessionUtil1.sendHeartBeat(SignupScreen.this);
//                        }
//
//                    } else if (mObj.optInt("code") == 0) {
//                        Tracer.error("Create app session api ---", response);
//                    }
//                } catch (Exception e) {
//                    ExceptionUtils.printStacktrace(e);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Tracer.error("SplashScreen", "Error: " + error.getMessage());
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//
//                params.put("lang", LocaleHelper.getLanguage(getApplicationContext()));
//
//                params.put("device", "android");
//                params.put("token", ApiRequest.TOKEN);
//                params.put("customer_device", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
//                params.put("customer_id", new SharedPreference().getPreferencesString(SignupScreen.this, "user_id" + "_" + ApiRequest.TOKEN));
//
//
//                double lat = 0, lng = 0;
//                GPSTracker tracker = new GPSTracker(SignupScreen.this);
//                if (tracker.canGetLocation()) {
//                    lat = tracker.getLatitude();
//                    lng = tracker.getLongitude();
//                }
//                params.put("lat", "" + lat);
//                params.put("long", "" + lng);
//
//                Set<String> keys = params.keySet();
//                for (String key : keys) {
//                    Tracer.error("SignupScreen", "createAppSession().getParams: " + key + "      " + params.get(key));
//                }
//
//                return params;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//    }
//
//
//}
