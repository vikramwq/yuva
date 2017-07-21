package com.multitv.yuv.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.CategoryAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.controller.SignUpController;
import com.multitv.yuv.interfaces.SignUpListener;
import com.multitv.yuv.models.User;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.AppNetworkAlertDialog;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import android.location.LocationManager;

import static com.multitv.yuv.utilities.Utilities.hideKeyboard;

public class SignupActivityNew extends AppCompatActivity implements SignUpListener{
    private EditText email_field, passwordField, mobileNumberField, firstNameField, /*lastNameField,*/
            confirmPasswordField;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView signUp_btn;
    private Button goToHomeActivityFromSignUp;
    private ProgressBar progressBar;
    private GoogleSignInOptions gso;
    private int RC_SIGN_IN = 3;
    SharedPreference sharedPreference;
    private int loginThrough;
    private GoogleApiClient mGoogleApiClient;
    PopupWindow popupWindow;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private String user_id, password, mPhoneNum, firstName, LastName, mGender = "", mFirstName = "", mLastName = "", mUserName = "", mDob = "", mEmail = "", confirmPasswordStr = "";
    private Toolbar toolbar;
    private TextInputLayout input_username, /*input_lastName,*/
            input_email_field, input_mobileNumber, input_password, input_confirm_password;
    private TextView genderTxt, ageTxt, locationTxt;
    private LinearLayout ageLayout, genderLayout;
    LocationRequest mLocationRequest;
    private LocationCallback mCallback;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 9009;
    private LocationManager locationManager;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private boolean mRequestingLocationUpdates = true;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private static Location sLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_new);
        FacebookSdk.sdkInitialize(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("SIGN UP");
        toolbar.setLogo(getResources().getDrawable(R.drawable.toolbar_icon));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(SignupActivityNew.this);

        signUp_btn = (TextView) findViewById(R.id.signUp_btn);
        firstNameField = (EditText) findViewById(R.id.firstName);
//        lastNameField = (EditText) findViewById(R.id.lastName);
        mobileNumberField = (EditText) findViewById(R.id.mobileNumber);
        email_field = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);
        confirmPasswordField = (EditText) findViewById(R.id.confirm_password);
        input_username = (TextInputLayout) findViewById(R.id.input_username);
//        input_lastName = (TextInputLayout) findViewById(R.id.input_lastName);
        input_email_field = (TextInputLayout) findViewById(R.id.input_email_field);
        input_mobileNumber = (TextInputLayout) findViewById(R.id.input_mobileNumber);
        input_password = (TextInputLayout) findViewById(R.id.input_password);

        ageLayout = (LinearLayout) findViewById(R.id.ageLayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderLayout);
        locationTxt = (TextView) findViewById(R.id.locationTxt);

        input_confirm_password = (TextInputLayout) findViewById(R.id.input_confirm_password);
        genderTxt = (TextView) findViewById(R.id.genderTxt);
        ageTxt = (TextView) findViewById(R.id.ageTxt);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        goToHomeActivityFromSignUp = (Button) findViewById(R.id.signInBtn);
        progressBar = (ProgressBar) findViewById(R.id.progress_signin);
        sharedPreference = new SharedPreference();




        goToHomeActivityFromSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
                    Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    if (validate()) {
                        String mpassword = passwordField.getText().toString();
                        mFirstName = firstNameField.getText().toString();

                        String ageGroup = ageTxt.getText().toString();

                        String gender = genderTxt.getText().toString();

//                        mLastName = lastNameField.getText().toString();

                        String[] nameArr = mFirstName.split(" ");
                        if (nameArr.length >= 2) {
                            mFirstName = nameArr[0];
                            mLastName = nameArr[1];
                        } else {

                        }

                        String number = mobileNumberField.getText().toString();

                        String confirmPassword = confirmPasswordField.getText().toString();
                        if (!TextUtils.isEmpty(confirmPassword)) {
                            confirmPasswordStr = confirmPassword;
                        }
                        if (!TextUtils.isEmpty(number)) {
                            mPhoneNum = number;
                        }

                        if (!TextUtils.isEmpty(mpassword)) {
                            password = mpassword;
                        }
                        String email = email_field.getText().toString();
                        if (!TextUtils.isEmpty(email)) {
                            mEmail = email;
                        }

                        if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
                            Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        } else {
                            sendInfoToServer(mFirstName, mLastName, mEmail, password, mPhoneNum, ageGroup, gender);
                        }

                    }
                }

            }
        });


        ageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDropdown(v);

            }
        });

        genderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropdownGender(v);
            }
        });


        locationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkAndRequestPermissions()) {

                }
            }
        });





    }


    private boolean checkAndRequestPermissions() {


        int permissionExternalStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionReadPhone = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionReadPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


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



    private void showDropdownGender(View view) {

        popupWindow = new PopupWindow(this);

        ListView listView = new ListView(this);
        String[] arr = {"Male", "Female"};
        List<String> list = Arrays.asList(arr);
        final CategoryAdapter adapter = new CategoryAdapter(SignupActivityNew.this, R.layout.category_item, list);
        listView.setAdapter(adapter);
        popupWindow.setContentView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();

                genderTxt.setText(adapter.getItem(position));
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(400);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAsDropDown(view);

    }

    private void showDropdown(View view) {

        popupWindow = new PopupWindow(this);

        ListView listView = new ListView(this);
        String[] arr = {"11-20", "21-30", "31-40", "41-50", "51-60"};
        List<String> list = Arrays.asList(arr);
        final CategoryAdapter adapter = new CategoryAdapter(SignupActivityNew.this, R.layout.category_item, list);
        listView.setAdapter(adapter);
        popupWindow.setContentView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ageTxt.setText(adapter.getItem(position));

                popupWindow.dismiss();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(400);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAsDropDown(view);

    }


    public void signUpFromFacebook(View v) {
        if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
            Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            fb_login();
        }
    }

    public void signUpFromGoogle(View v) {
        if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
            Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }


    private void sendOtp(final String mnUMBER, final String userId, final String provider) {
        sharedPreference.setPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN, "" + userId);
        if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
            Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

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
                    final JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        Log.e("OTP-FROM-VEQTA", "GENRATE_otp_api" + mObj.getString("result"));

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {


                                if (!TextUtils.isEmpty(mnUMBER))
                                    sharedPreference.setPhoneNumber(SignupActivityNew.this, "phone", mnUMBER);

                                try {
                                    Toast.makeText(SignupActivityNew.this, "" + mObj.getString("result"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(SignupActivityNew.this, OtpScreenActivity.class);
                                intent.putExtra("getOtp", "RECEIVED");
                                intent.putExtra("phone", mnUMBER);
                                intent.putExtra("provider", provider);

                                startActivity(intent);
                                finish();
                            }
                        }, 100);

                    } else {
                        String error = new String(mObj.optString("error"));
                        progressBar.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(error))
                            Toast.makeText(SignupActivityNew.this, error, Toast.LENGTH_LONG).show();

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
                params.put("user_id", userId);
                params.put("value", mnUMBER);

                Log.e("phone", mnUMBER);
                //Log.e("user_id", userId);

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq);
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


    private boolean validate() {
        boolean valid = true;
        String email = email_field.getText().toString();
        String password = passwordField.getText().toString();
        String firstName = firstNameField.getText().toString();
//        String lastName = lastNameField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (TextUtils.isEmpty(firstName)) {
            input_username.setError("FirstName field cannot be left blank");
            valid = false;
        }
//        if (TextUtils.isEmpty(lastName)) {
//            input_lastName.setError("LastName field cannot be left blank");
//            valid = false;
//        }


        if (TextUtils.isEmpty(password)) {
            input_password.setError("Password field cannot be left blank");
            valid = false;
        }

        if (password.length() < 8) {
            input_password.setError("Your password should be atleast 8 character long");
            valid = false;
        }
       /* if (password.length() > 8) {
            passwordField.setError("Your password should be atleast 8 character long");
            valid = false;
        }*/

        if (TextUtils.isEmpty(confirmPassword)) {
            input_confirm_password.setError("Your passwords do not match");
            valid = false;
        }
        if (!confirmPassword.equalsIgnoreCase(password)) {
            input_confirm_password.setError("Your passwords do not match");
            valid = false;
        }


        if (!email.matches(emailPattern)) {
            input_email_field.setError("Please enter a valid Email Address");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            input_email_field.setError("Email field cannot be left blank");
            valid = false;
        }

        String mobile = mobileNumberField.getText().toString();


        if (TextUtils.isEmpty(mobile)) {
            input_mobileNumber.setError("Mobile Number can not be blank");
            valid = false;
        }
        if (!AppConstants.isValidMobile(mobile)) {
            input_mobileNumber.setError("Please enter a valid phone number");
            valid = false;
        }


        return valid;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

                                    if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
                                        Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

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
                                            Toast.makeText(SignupActivityNew.this, getString(R.string.fb_error), Toast.LENGTH_LONG).show();
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
                System.out.println("onError");
                progressBar.setVisibility(View.GONE);

                try {
                    Log.e("SignUpActivity", exception.getCause().toString());
                    Toast.makeText(SignupActivityNew.this, getString(R.string.fb_error), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("SignUpActivity", e.getMessage());
                }
            }
        });
        loginButton.performClick();
    }


    private void sendInfoToServer(final String first_name, final String last_name, final String email, final String password, final String phone, final String ageGroup, final String gender) {
        if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {

            Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + ApiRequest.SIGNUP_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("SIGNUPACTIVITY", "SignUp-URL_:_" + response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        String str = mObj.optString("result");
                        String otp = mObj.optString("otp");
                        String id = mObj.optString("id");
                        Log.e("***SIGNUP-URL**", str);
                        String statusVerifieOtp = sharedPreference.getLoginOtpSentStatus(SignupActivityNew.this, "status");
                        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(id) && TextUtils.isEmpty(statusVerifieOtp)) {
                            comeFromSubscription(phone, id);
                        } else {
                            sharedPreference.setUserIfLoginVeqta(SignupActivityNew.this, "through", "1");
                            comeFromSubscriptionToHOme(phone, id);
                        }

                    } else if (mObj.optInt("code") == 0) {
                        String error = mObj.optString("error");
                        Toast.makeText(SignupActivityNew.this, "This Email Id or phone number is already registered.Please sign in using your account\n", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Log.e("SIGNUPACTIVITY", "Error" + "" + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Verify_otp_api", "Error: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        }) {

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
                    params.put("first_name", first_name);
                    params.put("last_name", last_name);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("age_group", ageGroup);
                    params.put("gender", gender);
                    params.put("phone", phone);
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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
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



    @Override
    public void onSuccess(String resultString) {
        if ((progressBar.getVisibility() == View.VISIBLE))
            progressBar.setVisibility(View.GONE);

        try {
            JSONObject mObj = new JSONObject(resultString);
            if (mObj.optInt("code") == 1) {
                MultitvCipher mcipher = new MultitvCipher();
                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                Log.e("SignUpActivity", str);
                User user = Json.parse(str.trim(), User.class);

                if (!TextUtils.isEmpty(user.gender))
                    mGender = user.gender;
                if (!TextUtils.isEmpty(user.first_name))
                    mFirstName = user.first_name;
                else mFirstName = "";
                if (!TextUtils.isEmpty(user.last_name))
                    mLastName = user.last_name;
                else mLastName = "";
                if (!TextUtils.isEmpty(user.dob) && !user.dob.equals("0000-00-00"))
                    mDob = user.dob;
                if (!TextUtils.isEmpty(user.email))
                    mEmail = user.email;

                String provider = user.provider;
                if (!TextUtils.isEmpty(provider)) {
                    sharedPreference.setUserName(this, "first_name", mFirstName);
                    sharedPreference.setUserLastName(this, "last_name", mLastName);
                    sharedPreference.setDob(this, "dob", mDob);
                    sharedPreference.setEmailId(this, "email_id", mEmail);


                    if (!TextUtils.isEmpty(user.image))
                        sharedPreference.setImageUrl(this, "imgUrl", user.image);

                    if (mGender.equalsIgnoreCase("male")) {
                        sharedPreference.setGender(this, "gender_id", "" + 0);
                    } else if (mGender.equalsIgnoreCase("female")) {
                        sharedPreference.setGender(this, "gender_id", "" + 1);
                    }
                }
                if (!TextUtils.isEmpty(user.id)) {
                    String otpVerifie = sharedPreference.getUserIfLoginVeqta(SignupActivityNew.this, "through");
                    sharedPreference.setPreferencesString(this, "user_id" + "_" + ApiRequest.TOKEN, "" + user.id);
                    sharedPreference.setPreferenceBoolean(this, sharedPreference.KEY_IS_LOGGED_IN);


                    //==============move to dynamic way
                    if (!TextUtils.isEmpty(user.contact_no) && !TextUtils.isEmpty(user.otp) && !TextUtils.isEmpty(user.provider)) {
                        sendOtp(user.contact_no, user.id, user.provider);
                    } else if (!TextUtils.isEmpty(user.contact_no) && TextUtils.isEmpty(user.otp)) {
                        sharedPreference.setFromLogedIn(SignupActivityNew.this, "fromLogedin", "veqta");
                        sharedPreference.setUserIfLoginVeqta(SignupActivityNew.this, "through", "1");
                        sharedPreference.setPhoneNumber(SignupActivityNew.this, "phone", user.contact_no);
                        moveToHomeScreen();
                    } else if (TextUtils.isEmpty(user.contact_no) && !TextUtils.isEmpty(user.otp)) {
                        moveToOtpScreenforSocial();
                    } else if (TextUtils.isEmpty(user.contact_no) && TextUtils.isEmpty(user.otp)) {
                        moveToOtpScreenforSocial();
                    }
                }

            } else {
                String error = mObj.optString("error");
                if (!TextUtils.isEmpty(error))
                    Toast.makeText(SignupActivityNew.this, error, Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Log.e("error", "" + e.getMessage());
        }
    }


    @Override
    public void onError() {
        if ((progressBar.getVisibility() == View.VISIBLE))
            progressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }




    private void updateInfoToServer(String id, String fName, String lName, String gender, String link, String locale, String name, String email, String location, String dob, int loginThrough, String phoneNo) {

        if (!(progressBar.getVisibility() == View.VISIBLE))
            progressBar.setVisibility(View.VISIBLE);
        SignUpController signUpController = new SignUpController(this, this);
        signUpController.sendInfoToServer(id, fName, lName, gender, link, locale, name, email, location, dob, loginThrough, phoneNo);
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
                            sharedPreference.setImageUrl(SignupActivityNew.this, "imgUrl", profilePicUrlFromFb);
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
                sharedPreference.setGoogleLoginProfilePic(SignupActivityNew.this, "ImageGoogleProfile", profilePic);
                sharedPreference.setImageUrl(SignupActivityNew.this, "imgUrl", profilePic);
                Log.e("personPhotoUrl", profilePic);
            } else {
                Log.e("personPhotoUrl", "Empty");
            }
            mFirstName = first_google_name;
            mLastName = last_google_name;
            mUserName = personName;
            mEmail = gmail_id;
            loginThrough = AppConstants.LOGIN_THROUGH_GOOGLE;


            if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {
                Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return;
            } else {
                updateInfoToServer(id, mFirstName, mLastName, mGender, "", "", mUserName, mEmail, "", mDob,
                        loginThrough, mPhoneNum);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SignupActivityNew.this, "Google Login Failed", Toast.LENGTH_LONG).show();

        }
    }


    public void moveToOtpScreenforSocial() {


        if (LoginScreen.getInstance() != null) {
            ((LoginScreen) LoginScreen.getInstance())
                    .closeActivity();
        }


        sharedPreference.setPreferenceBoolean(SignupActivityNew.this, sharedPreference.KEY_IS_OTP_VERIFIED);
        sharedPreference.setPreferenceBoolean(SignupActivityNew.this, sharedPreference.KEY_IS_LOGGED_IN);

        Intent intent = new Intent(SignupActivityNew.this, HomeActivity.class);
        intent.putExtra("getOtp", "NOT_RECEIVED");
        startActivity(intent);
        SignupActivityNew.this.finish();
    }

    public void moveToHomeScreen() {
        if (LoginScreen.getInstance() != null) {
            ((LoginScreen) LoginScreen.getInstance())
                    .closeActivity();
        }
        Intent intent = new Intent(SignupActivityNew.this, HomeActivity.class);
        startActivity(intent);
        SignupActivityNew.this.finish();
    }


    private void comeFromSubscription(String phone, String id) {
        sharedPreference.setPreferencesString(SignupActivityNew.this, "user_id" + "_" + ApiRequest.TOKEN, "" + id);


        Intent intent = new Intent(SignupActivityNew.this, OtpScreenActivity.class);
        intent.putExtra("getOtp", "RECEIVED");
        intent.putExtra("phone", phone);
        intent.putExtra("provider", "");

        startActivity(intent);
        if (LoginScreen.getInstance() != null) {
            ((LoginScreen) LoginScreen.getInstance())
                    .closeActivity();
        }

        SignupActivityNew.this.finish();
    }


    private void comeFromSubscriptionToHOme(String phone, String id) {
        sharedPreference.setPreferencesString(SignupActivityNew.this, "user_id" + "_" + ApiRequest.TOKEN, "" + id);

        if (LoginScreen.getInstance() != null) {
            ((LoginScreen) LoginScreen.getInstance())
                    .closeActivity();
        }
        Intent intent = new Intent(SignupActivityNew.this, HomeActivity.class);
        intent.putExtra("getOtp", "RECEIVED");
        intent.putExtra("phone", phone);
        intent.putExtra("provider", "");


        startActivity(intent);
        SignupActivityNew.this.finish();
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





}
