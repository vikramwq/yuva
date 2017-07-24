package com.multitv.yuv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.multitv.yuv.R;
import com.multitv.yuv.adapter.CategoryAdapter;
import com.multitv.yuv.adapter.CountryListAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.models.CountryCode;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.AppNetworkAlertDialog;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//----location ---
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;

public class SignupActivityNew extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    private EditText email_field, passwordField, mobileNumberField, firstNameField, /*lastNameField,*/
            confirmPasswordField;
    private Button goToHomeActivityFromSignUp;
    private ProgressBar progressBar;
    SharedPreference sharedPreference;
    PopupWindow popupWindow;
    private String user_id, password, mPhoneNum, firstName, LastName, mGender = "", mFirstName = "", mLastName = "", mUserName = "", mDob = "", mEmail = "", confirmPasswordStr = "";
    private Toolbar toolbar;
    private TextInputLayout input_username, /*input_lastName,*/
            input_email_field, input_mobileNumber, input_password, input_confirm_password;
    private TextView genderTxt, ageTxt, locationTxt, codeSelectedTextview;
    private LinearLayout ageLayout, genderLayout;

    //--for Location update -----
    protected static final String TAG = "MainActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;
    private RelativeLayout containerLayout;
    private String ageGroup;

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

        firstNameField = (EditText) findViewById(R.id.firstName);
        mobileNumberField = (EditText) findViewById(R.id.mobileNumber);
        email_field = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);
        confirmPasswordField = (EditText) findViewById(R.id.confirm_password);
        input_username = (TextInputLayout) findViewById(R.id.input_username);
        input_email_field = (TextInputLayout) findViewById(R.id.input_email_field);
        input_mobileNumber = (TextInputLayout) findViewById(R.id.input_mobileNumber);
        input_password = (TextInputLayout) findViewById(R.id.input_password);
        containerLayout = (RelativeLayout) findViewById(R.id.containerLayout);

        ageLayout = (LinearLayout) findViewById(R.id.ageLayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderLayout);
        locationTxt = (TextView) findViewById(R.id.locationTxt);

        input_confirm_password = (TextInputLayout) findViewById(R.id.input_confirm_password);
        genderTxt = (TextView) findViewById(R.id.genderTxt);
        ageTxt = (TextView) findViewById(R.id.ageTxt);
        codeSelectedTextview = (TextView) findViewById(R.id.codeSelectedTextview);

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
        codeSelectedTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonForCountryList(v);
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
                getAddress(locationTxt);
            }
        });


        //---for location view -----
        mRequestingLocationUpdates = false;
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
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
                if (!TextUtils.isEmpty(genderTxt.getText())) {
                    ageGroup = genderTxt.getText().toString();
                }
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAsDropDown(view);
    }

    private void showDropdown(View view) {
        popupWindow = new PopupWindow(this);
        ListView listView = new ListView(this);
        String[] arr = {"Below 18", "18-24", "25-35", "Above 35"};
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
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAsDropDown(view);
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
        String confirmPassword = confirmPasswordField.getText().toString();
        String genderString = genderTxt.getText().toString();
        String ageString = ageTxt.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(ageString)) {
            Toast.makeText(SignupActivityNew.this, "Age field cannot be left blank", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (TextUtils.isEmpty(genderString)) {
            Toast.makeText(SignupActivityNew.this, "Gender field cannot be left blank", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (TextUtils.isEmpty(firstName)) {
            input_username.setError("First Name field cannot be left blank");
            valid = false;
        }


        if (TextUtils.isEmpty(password)) {
            input_password.setError("Password field cannot be left blank");
            valid = false;
        }

        if (password.length() < 8) {
            input_password.setError("Your password should be atleast 8 character long");
            valid = false;
        }

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


    private void sendInfoToServer(final String first_name, final String last_name, final String email, final String password, final String phone, final String ageGroup, final String gender) {
        if (!AppNetworkAlertDialog.isNetworkConnected(SignupActivityNew.this)) {

            Toast.makeText(SignupActivityNew.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        goToHomeActivityFromSignUp.setEnabled(false);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(this,ApiRequest.SIGNUP_URL) , new Response.Listener<String>() {

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

                       // String statusVerifieOtp = sharedPreference.getLoginOtpSentStatus(SignupActivityNew.this, "status");
                       /* if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(id) && TextUtils.isEmpty(statusVerifieOtp)) {
                            comeFromSubscription(phone, id);
                        } else {
                            sharedPreference.setUserIfLoginVeqta(SignupActivityNew.this, "through", "1");
                            comeFromSubscriptionToHOme(phone, id);
                        }*/
                        goToHomeActivityFromSignUp.setEnabled(true);
                        comeFromSubscription(phone, id);
                    } else if (mObj.optInt("code") == 0) {
                        String error = mObj.optString("error");
                        Toast.makeText(SignupActivityNew.this, "This Email Id or phone number is already registered.Please sign in using your account\n", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        goToHomeActivityFromSignUp.setEnabled(false);
                    }

                } catch (Exception e) {
                    Log.e("SIGNUPACTIVITY", "Error" + "" + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    goToHomeActivityFromSignUp.setEnabled(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Verify_otp_api", "Error: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
                goToHomeActivityFromSignUp.setEnabled(false);
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
                    if (!TextUtils.isEmpty(ageGroup)) {
                        if (ageGroup.equalsIgnoreCase("below 18")) {
                            params.put("age_group", "0-18");
                        } else if (ageGroup.equalsIgnoreCase("above 35")) {
                            params.put("age_group", "35-100");
                        } else {
                            params.put("age_group", ageGroup);
                        }
                    }

                    params.put("gender", gender);

                    params.put("phone", phone);

                    if (mCurrentLocation != null) {
                        params.put("lat", "" + mCurrentLocation.getLatitude());
                        params.put("long", "" + mCurrentLocation.getLongitude());
                    }

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

    //----------------------------------------------------------------------------------------------------------------
    //-----------------------LOCATION UPDATE CODE ------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        checkPermission();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {

                    status.startResolutionForResult(SignupActivityNew.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission granted
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;

            }
        });

    }

    private static final int PERMISSION_REQUEST = 100;

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(containerLayout, "You need to grant SEND SMS permission to send sms",
                            Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                        }
                    }).show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                }
            } else {
                checkLocationSettings();
            }
        } else {
            checkLocationSettings();
        }
    }

    //-----for get address from lat and long---------------
    public void getAddress(TextView locationTextView) {
        if (mCurrentLocation != null) {
            Double latitude = mCurrentLocation.getLatitude();
            Double longitude = mCurrentLocation.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder();
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    if (!TextUtils.isEmpty(strReturnedAddress)) {
                        locationTextView.setText(strReturnedAddress.toString());
                        Log.e("LocationAddress", "" + strReturnedAddress.toString());
                        if (mCurrentLocation != null) {
                            sharedPreference.setUserLocation(this, Constant.LOCATION_KEY, "" + strReturnedAddress);
                        }
                    }
                } else {
                    Log.e("LocationAddress", "Canont get Address!");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("LocationAddress", "Canont get Address!");
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //permission granted---

                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------country code list--------------------------
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("countrycode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void getJsonForCountryList(View view) {
        try {
            JSONObject jsonobject = new JSONObject(loadJSONFromAsset());
            ArrayList<CountryCode> countryCodeList = new ArrayList<CountryCode>();
            JSONArray jsonArray = jsonobject.getJSONArray("countries");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                CountryCode countryCode = new CountryCode();
                countryCode.setName(jsonObj.getString("name"));
                countryCode.setCode(jsonObj.getString("code"));
                countryCodeList.add(countryCode);
            }
            setCountryList(countryCodeList, view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCountryList(final ArrayList<CountryCode> countryCodeList, View view) {
        if (countryCodeList != null && countryCodeList.size() != 0) {
            popupWindow = new PopupWindow(this);
            ListView listView = new ListView(this);
            final CountryListAdapter adapter = new CountryListAdapter(SignupActivityNew.this, countryCodeList);
            listView.setAdapter(adapter);
            popupWindow.setContentView(listView);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    codeSelectedTextview.setText(countryCodeList.get(position).name + " " + countryCodeList.get(position).code);
                    popupWindow.dismiss();
                }
            });

            popupWindow.setFocusable(true);
            popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.showAsDropDown(view);
        }

    }

}
