package com.multitv.yuv.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.multitv.cipher.MultitvCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.interfaces.FragmentLifecycle;
import com.multitv.yuv.interfaces.ImageSenderInterface;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.multitv.yuv.R.id.radioGroup;

/**
 * Created by root on 9/12/16.
 */

public class EditProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, FragmentLifecycle {


    // ================== all view =========================================================================================
    //=================================================================================
    private SharedPreference sharedPreference;
    private TextView dateofbirth_editField;
    private RadioGroup radioSexGroup;
    private RadioButton male, female;
    private RelativeLayout progressRelativeLayout1;

    private RadioButton radioButton;
    private int selectedId;
    private Button intex_submit;
    private ImageView FROM_LOGIN_FACEBOOK, FROM_LOGIN_GOOGLE;
    private EditText firist_edit_textField, last_name_edit_textField, email_edit_textField, phone_edit_textField;

    // string object===========================================================================================================
    //===================================================================================
    private String firstName, lastName, dateOfBirth, gender, genderSaveId, phoneNumber, intex_email, fromLogedin, user_id,
            encodedImageSendFromFacebook, profilePicUrlFromFb, encodedImageSendFromGoogle;

    //  google plus view and call back ========================================================================================
    //==================================================================================
    private int RC_SIGN_IN = 3;
    private String lastGoogleUserName, firstGoogleUserName;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    //  facebook view and call back============================================================================================
    //=================================================================================
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    int FROM_DATA = 0;


    //================for-date ================================================================================================
    //==================================================================================
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;

    //================for interface listener =================================================================================
    //=====================================================================================
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ImageSenderInterface imageSenderInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (getActivity() == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }

        final View view = inflater.inflate(R.layout.edit_user_profile_fragment, container, false);


        //=======================define sharePerfernces and view id========================================================
        //  ===========================================================================
        sharedPreference = new SharedPreference();
        user_id = new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);
        fromLogedin = sharedPreference.getFromLogedIn(getActivity(), "fromLogedin");
        Log.e("++fromLogedin++", fromLogedin);
        inIt(view);


        setInformationOnViews(view);


        //=======google plus login from custom button click============================================================
        //  ===========================================================================
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        intex_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (sharedPreference.getPreferenceBoolean(getActivity(), sharedPreference.KEY_IS_OTP_VERIFIED)) {
                    if (validate()) {
                        putInformationToServer(view);

                    }
                } else {
                    Utilities.showLoginDailog(getActivity());
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            imageSenderInterface = (ImageSenderInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        imageSenderInterface = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() == null)
            return;
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() == null)
            return;
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //====================find view by id=================================================================================
    // =========================================================================================
    private void inIt(View view) {
        radioSexGroup = (RadioGroup) view.findViewById(radioGroup);
        firist_edit_textField = (EditText) view.findViewById(R.id.firist_name);
        last_name_edit_textField = (EditText) view.findViewById(R.id.lastname);

        last_name_edit_textField.setTextColor(Color.parseColor("#FFFFFF"));
        email_edit_textField = (EditText) view.findViewById(R.id.email_txt);
        phone_edit_textField = (EditText) view.findViewById(R.id.phone_txt);
        dateofbirth_editField = (TextView) view.findViewById(R.id.dateofbirth);
        progressRelativeLayout1 = (RelativeLayout) view.findViewById(R.id.progressRelativeLayout1);
//        signWithGoogle = (Button) view.findViewById(R.id.signWithGoogle);
        intex_submit = (Button) view.findViewById(R.id.intex_submit);
        FROM_LOGIN_FACEBOOK = (ImageView) view.findViewById(R.id.login_hint_fb);
        FROM_LOGIN_GOOGLE = (ImageView) view.findViewById(R.id.login_hint_google);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        female = (RadioButton) view.findViewById(R.id.female);
        male = (RadioButton) view.findViewById(R.id.male);
//        fb_button_click = (Button) view.findViewById(R.id.fb_button_click);
        // submit_btn.setVisibility(View.GONE);
        if (selectedId > 0) {
            radioButton = (RadioButton) view.findViewById(selectedId);
        }
        //==============radio button select===================
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (radioSexGroup.getCheckedRadioButtonId() != -1) {
                    int id = radioSexGroup.getCheckedRadioButtonId();
                    View radioButton = radioSexGroup.findViewById(id);
                    int radioId = radioSexGroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radioSexGroup.getChildAt(radioId);
                    gender = (String) btn.getText();
                    //sharedPreference.setGender(getActivity(), "gender_id", "" + radioId);
                }
            }
        });

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR) - 20;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH); //Default DOB is (today - 20) years
        //date picker things
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                dateofbirth_editField.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
             /*   dateofbirth_editField.setText(new StringBuilder().append(mMonth + 1).append("/").append(mDay).append("/").append(mYear).append(" "));
            */
                dateOfBirth = dateofbirth_editField.getText().toString();
                Log.e("DateFromPicker", dateOfBirth);
            }
        };

        dateofbirth_editField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null)
                    return;
                new DatePickerDialog(getActivity(),
                        mDateSetListener,
                        mYear, mMonth, mDay).show();
            }
        });

    }


    //====================get detail fb and google from sharePerences===================================================================
    //================================================================================================

    private void getDatialsFromShareprefernce() {
        if (getActivity() == null)
            return;
        String lastName = sharedPreference.getUSerLastName(getActivity(), "last_name");
        String emailFrom = sharedPreference.getEmailID(getActivity(), "email_id");
        String firstName = sharedPreference.getUSerName(getActivity(), "first_name");
        Tracer.error("emailFrom", emailFrom);
        String myRadioId = sharedPreference.getGender(getActivity(), "gender_id");

//        if (firstName != null && !firstName.equals("null") && !firstName.isEmpty()) {
//            firist_edit_textField.setText(firstName);
//            firist_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            firist_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
//        }
//        if (emailFrom != null && !emailFrom.equals("null") && !emailFrom.isEmpty()) {
//            email_edit_textField.setText(emailFrom);
//            email_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            email_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
//        }
//        if (lastName != null && !lastName.equals("null") && !lastName.isEmpty()) {
//            last_name_edit_textField.setText(lastName);
//            last_name_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            last_name_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
//        }

    }

    //==========================edit text local data post to server====================================================
    //===================================================================================

    private void getInformationFromLocally(View view) {
        String lastName = sharedPreference.getUSerLastName(getActivity(), "last_name");
        String emailFrom = sharedPreference.getEmailID(getActivity(), "email_id");
        String dob = sharedPreference.getDob(getActivity(), "dob");
        String phoneNumber = sharedPreference.getPhoneNumber(getActivity(), "phone");
        String firstName = sharedPreference.getUSerName(getActivity(), "first_name");
        Tracer.error("Intex-user-profile", emailFrom);
        if (dob != null && !dob.equals("null") && !dob.isEmpty()) {
            dateofbirth_editField.setText(dob);
            dateofbirth_editField.setTextColor(getResources().getColor(R.color.white_bg));
            dateofbirth_editField.setHintTextColor(getResources().getColor(R.color.white_bg));
        }
        String myRadioId = sharedPreference.getGender(getActivity(), "gender_id");
        Log.e("GENDER", "" + myRadioId);
        if (myRadioId != null && !myRadioId.equals("") && !myRadioId.isEmpty()) {
            if (myRadioId.equals("0")) {
                male.setChecked(true);
                male.setTextColor(getResources().getColor(R.color.white_bg));
            } else if (myRadioId.equals("1")) {
                female.setChecked(true);
                female.setTextColor(getResources().getColor(R.color.white_bg));
            }
            selectedId = radioSexGroup.getCheckedRadioButtonId();
            if (selectedId > 0) {
                radioButton = (RadioButton) view.findViewById(selectedId);
            }
        }

        if (firstName != null && !firstName.equals("null") && !firstName.isEmpty()) {
            firist_edit_textField.setText(firstName);
//            firist_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            firist_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
        }
        if (emailFrom != null && !emailFrom.equals("null") && !emailFrom.isEmpty()) {
            email_edit_textField.setText(emailFrom);
//            email_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            email_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
        }
        if (lastName != null && !lastName.equals("null") && !lastName.isEmpty()) {
            last_name_edit_textField.setText(lastName);
//            last_name_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            last_name_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
        }

        if (phoneNumber != null && !phoneNumber.equals("null") && !phoneNumber.isEmpty()) {
            phone_edit_textField.setText(phoneNumber);
//            phone_edit_textField.setTextColor(getResources().getColor(R.color.black));
//            phone_edit_textField.setHintTextColor(getResources().getColor(R.color.black));
            imageSenderInterface.sendMobileNumber(phoneNumber);
        }
        fromLogedin = sharedPreference.getFromLogedIn(getActivity(), "fromLogedin");
        if (fromLogedin.equals("facebook")) {
//            FROM_LOGIN_FACEBOOK.setImageResource(R.mipmap.sign_up);
            FROM_LOGIN_GOOGLE.setImageResource(R.mipmap.no_sign_up);
//        } else if (fromLogedin.equals("google")) {
//            FROM_LOGIN_FACEBOOK.setImageResource(R.mipmap.no_sign_up);
//            FROM_LOGIN_GOOGLE.setImageResource(R.mipmap.sign_up);
        }
    }

    private void putInformationToServer(View view) {
        FROM_DATA = 0;
        sharedPreference.setPreferencesString(getActivity(), "intex_data", "intex");

        String date = dateofbirth_editField.getText().toString();
        String regEx = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d{2}$";
        Matcher matcherObj = Pattern.compile(regEx).matcher(date);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String mob = phone_edit_textField.getText().toString();
        String email = email_edit_textField.getText().toString();
        String lastname = last_name_edit_textField.getText().toString();
        String firstNametest = firist_edit_textField.getText().toString();

        if (!TextUtils.isEmpty(firstNametest)) {
            firstName = firist_edit_textField.getText().toString();
            sharedPreference.setUserName(getActivity(), "first_name", firstName);
            imageSenderInterface.sendUserFiristName(firstName);
        } else {
            Toast.makeText(getActivity(), "UserName Should not be blank", Toast.LENGTH_LONG).show();
        }
        if (!TextUtils.isEmpty(lastname)) {
            lastName = last_name_edit_textField.getText().toString();
            sharedPreference.setUserLastName(getActivity(), "last_name", lastName);
            imageSenderInterface.sendUserLastName(firstName, lastName);
        } else {
//            last_name_edit_textField.setError("UserName Should not be blank");
            Toast.makeText(getActivity(), "UserName Should not be blank", Toast.LENGTH_LONG).show();
        }
        if (!TextUtils.isEmpty(date)) {
            dateOfBirth = dateofbirth_editField.getText().toString();
            sharedPreference.setDob(getActivity(), "dob", date);
        }
        if (!TextUtils.isEmpty(mob) && isValidMobile(mob)) {
            phoneNumber = phone_edit_textField.getText().toString();
            sharedPreference.setPhoneNumber(getActivity(), "phone", phoneNumber);
            imageSenderInterface.sendMobileNumber(phoneNumber);
        }
        if (!TextUtils.isEmpty(email) && email.matches(emailPattern)) {
            intex_email = email_edit_textField.getText().toString();
            sharedPreference.setEmailId(getActivity(), "email_id", intex_email);
            imageSenderInterface.sendEmailId(intex_email);
        } else {
            sharedPreference.setEmailId(getActivity(), "email_id", "");
            imageSenderInterface.sendEmailId("");
        }
        selectedId = radioSexGroup.getCheckedRadioButtonId();
        if (radioSexGroup.getCheckedRadioButtonId() != -1) {
            int id = radioSexGroup.getCheckedRadioButtonId();
            View radioButton = radioSexGroup.findViewById(id);
            int radioId = radioSexGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) radioSexGroup.getChildAt(radioId);
            gender = (String) btn.getText();

            if (!TextUtils.isEmpty(gender))
                if (gender.equals("male") || gender.equals("Male"))
                    sharedPreference.setGender(getActivity(), "gender_id", "" + 0);
                else if (gender.equals("female") || gender.equals("Female"))
                    sharedPreference.setGender(getActivity(), "gender_id", "" + 1);
        }
        userEditProfile("", firstName, phoneNumber, lastName, dateOfBirth, gender, intex_email);
        getInformationFromLocally(view);

    }

    private void setInformationOnViews(View view) {
        String lastName = sharedPreference.getUSerLastName(getActivity(), "last_name");
        String emailFrom = sharedPreference.getEmailID(getActivity(), "email_id");
        String dob = sharedPreference.getDob(getActivity(), "dob");
        String phoneNumber = sharedPreference.getPhoneNumber(getActivity(), "phone");
        String firstName = sharedPreference.getUSerName(getActivity(), "first_name");
        Tracer.error("Intex-user-profile", emailFrom);
        if (!TextUtils.isEmpty(dob)) {
            dateofbirth_editField.setText(dob);
            dateofbirth_editField.setTextColor(getResources().getColor(R.color.white_bg));
            dateofbirth_editField.setHintTextColor(getResources().getColor(R.color.white_bg));
        }
        String myRadioId = sharedPreference.getGender(getActivity(), "gender_id");
        Log.e("GENDER", "" + myRadioId);
        if (!TextUtils.isEmpty(myRadioId)) {
            if (myRadioId.equals("0")) {
                male.setChecked(true);
                male.setTextColor(getResources().getColor(R.color.black));
            } else if (myRadioId.equals("1")) {
                female.setChecked(true);
                female.setTextColor(getResources().getColor(R.color.black));
            }
            selectedId = radioSexGroup.getCheckedRadioButtonId();
            if (selectedId > 0) {
                radioButton = (RadioButton) view.findViewById(selectedId);
            }
        }

        if (!TextUtils.isEmpty(firstName)) {
            firist_edit_textField.setText(firstName);
            firist_edit_textField.setTextColor(getResources().getColor(R.color.white_bg));
            firist_edit_textField.setHintTextColor(getResources().getColor(R.color.white_bg));
        }
        if (!TextUtils.isEmpty(emailFrom)) {
            email_edit_textField.setText(emailFrom);
            email_edit_textField.setTextColor(getResources().getColor(R.color.white_bg));
            email_edit_textField.setHintTextColor(getResources().getColor(R.color.white_bg));
        }
        if (!TextUtils.isEmpty(lastName)) {
            last_name_edit_textField.setText(lastName);
            last_name_edit_textField.setTextColor(getResources().getColor(R.color.white_bg));
            last_name_edit_textField.setHintTextColor(getResources().getColor(R.color.white_bg));
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            phone_edit_textField.setText(phoneNumber);
            phone_edit_textField.setTextColor(getResources().getColor(R.color.white_bg));
            phone_edit_textField.setHintTextColor(getResources().getColor(R.color.white_bg));
            imageSenderInterface.sendMobileNumber(phoneNumber);
        }
    }

    //=======================on activity result========================================================================
    //=============================================================================
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressRelativeLayout1.setVisibility(View.VISIBLE);
            FROM_DATA = 2;
            if (getActivity() == null)
                return;
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(googleSignInResult);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void updateDetailsFromGoogle(final String profilePicUrlFromGmail, final String emailFromGoogle, final String googleNAme, final String googleLastName) {

        sharedPreference.setImageUrl(getActivity(), "ImageURl", profilePicUrlFromGmail);
        sharedPreference.setEmailId(getActivity(), "email_id", emailFromGoogle);
        sharedPreference.setUserName(getActivity(), "first_name", googleNAme);
        sharedPreference.setUserLastName(getActivity(), "last_name", googleLastName);
        sharedPreference.setDob(getActivity(), "dob", "");
        sharedPreference.setPreferencesString(getActivity(), "intex_data", "");

        //===========================interface call back=======================
        imageSenderInterface.sendMobileNumber("");
        if (!TextUtils.isEmpty(profilePicUrlFromGmail))
            imageSenderInterface.sendImage(profilePicUrlFromGmail);

        if (!TextUtils.isEmpty(emailFromGoogle))
            imageSenderInterface.sendEmailId(emailFromGoogle);

        if (!TextUtils.isEmpty(googleNAme))
            imageSenderInterface.sendUserFiristName(googleNAme);

        if (!TextUtils.isEmpty(googleLastName))
            imageSenderInterface.sendUserLastName(googleNAme, googleLastName);
        //==================================================

        if (!TextUtils.isEmpty(googleNAme)) {
            Handler mainHandler = new Handler(getActivity().getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    last_name_edit_textField.setText("");
                    email_edit_textField.setText("");
                    phone_edit_textField.setText("");
                    firist_edit_textField.setText("");
                    dateofbirth_editField.setText("");
                    progressRelativeLayout1.setVisibility(View.GONE);
                    FROM_LOGIN_FACEBOOK.setImageResource(R.mipmap.no_sign_up);
                    FROM_LOGIN_GOOGLE.setImageResource(R.mipmap.sign_up);
                    getDatialsFromShareprefernce();
                }
            };
            mainHandler.post(myRunnable);


            /*  male.setChecked(false);
             female.setChecked(false);*/
            //  ======================================================
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (profilePicUrlFromGmail != null && !profilePicUrlFromGmail.equals("null") && !profilePicUrlFromGmail.isEmpty()) {
                        try {
                            URL url = new URL(profilePicUrlFromGmail);
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] byteArrayImage = baos.toByteArray();
                            encodedImageSendFromGoogle = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                        } catch (IOException e) {
                            System.out.println(e.getMessage().toString());
                        }
                    }

                    if (!TextUtils.isEmpty(encodedImageSendFromGoogle))
                        userEditProfile(encodedImageSendFromGoogle, googleNAme, "", googleLastName, "", "", emailFromGoogle);
                    else
                        userEditProfile("", googleNAme, "", googleLastName, "", "", emailFromGoogle);
                }
            });

        } else {
            Toast.makeText(getActivity(), "login failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    //==================hit edit user profile api and responce=================================================================
    //====================================================================================
    private void userEditProfile(final String imageEncodeSendTOServer, final String firistName,
                                 final String mobNumber, final String lastName, final String dob, final String gender,
                                 final String email) {
        Tracer.error("FROM_DATA", "" + FROM_DATA);
        if (FROM_DATA == 0)
            progressRelativeLayout1.setVisibility(View.VISIBLE);

        if (getActivity() == null)
            return;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                        AppUtils.generateUrl(getActivity(), ApiRequest.USER_EDIT), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tracer.error("api_responce---", response.toString());
                        progressRelativeLayout1.setVisibility(View.GONE);

                        try {
                            JSONObject mObj = new JSONObject(response);

                            if (mObj.optInt("code") == 1) {
                                MultitvCipher mcipher = new MultitvCipher();
                                String str = new String(mcipher.decryptmyapi(mObj.optString("result")));

                                Tracer.error("***user_data***", str);
                                try {
                                    JSONObject newObj = new JSONObject(str);
                                    String contact_no = newObj.getString("contact_no");

                                    Tracer.error("***userResponces***", "" + newObj.toString());
                                    Toast.makeText(getActivity(), "Profile successfully saved", Toast.LENGTH_LONG).show();

                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.USER_PROFILE_CHANGED,"");


                                } catch (JSONException e) {
                                    Tracer.error("***edit fail***", "JSONException");
                                    ExceptionUtils.printStacktrace(e);
                                    progressRelativeLayout1.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            ExceptionUtils.printStacktrace(e);
                            progressRelativeLayout1.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error.networkResponse != null) {
                                Tracer.error("etworkResponse", "" + error.networkResponse);
                                Toast.makeText(getActivity(), "" + error.networkResponse.toString(), Toast.LENGTH_LONG).show();
                            }
                            Tracer.error("user_edit_fail", "" + error.getMessage());
                            progressRelativeLayout1.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "" + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Tracer.error("EditProfileFragment", e.getMessage());
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                        params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));

                        if (FROM_DATA == 0) {
                            if (getActivity() == null)
                                return null;
                            sharedPreference.setStatusLogin(getActivity(), "status_login", "" + FROM_DATA);
                            params.put("id", user_id);
                            if (dob != null && !dob.equals("") && !dob.isEmpty()) {
                                params.put("dob", dob);
                                Log.e("****date***", dob);
                            }
                            if (email != null && !email.equals("") && !email.isEmpty()) {
                                params.put("about_me", email);
                            }
                            if (gender != null && !gender.equals("") && !gender.isEmpty()) {
                                params.put("gender", gender);
                            }
                            if (mobNumber != null && !mobNumber.equals("") && !mobNumber.isEmpty()) {
                                params.put("contact_no", mobNumber);
                            }
                            if (lastName != null && !lastName.equals("") && !lastName.isEmpty()) {
                                params.put("last_name", lastName);
                            }
                            if (firistName != null && !firistName.equals("") && !firistName.isEmpty()) {
                                params.put("first_name", firistName);
                            }

                        } else if (FROM_DATA == 1) {
                            sharedPreference.setStatusLogin(getActivity(), "", "" + FROM_DATA);
                            params.put("id", user_id);
                            if (lastName != null && !lastName.equals("") && !lastName.isEmpty()) {
                                params.put("last_name", lastName);
                            }
                            if (firistName != null && !firistName.equals("") && !firistName.isEmpty()) {
                                params.put("first_name", firistName);
                            }
                            if (gender != null && !gender.equals("") && !gender.isEmpty()) {
                                params.put("gender", gender);
                            }
                            if (email != null && !email.equals("") && !email.isEmpty()) {
                                params.put("about_me", email);
                            }

                            if (imageEncodeSendTOServer != null && !imageEncodeSendTOServer.equals(" ") && !imageEncodeSendTOServer.isEmpty()) {
                                params.put("pic", imageEncodeSendTOServer);
                                params.put("ext", "jpg");
                            }

                        } else if (FROM_DATA == 2) {
                            sharedPreference.setStatusLogin(getActivity(), "", "" + FROM_DATA);
                            params.put("id", user_id);
                            if (lastName != null && !lastName.equals("") && !lastName.isEmpty())
                                params.put("last_name", lastName);

                            if (firistName != null && !firistName.equals("") && !firistName.isEmpty())
                                params.put("first_name", firistName);

                            if (gender != null && !gender.equals("") && !gender.isEmpty())
                                params.put("gender", gender);

                            if (email != null && !email.equals("") && !email.isEmpty())
                                params.put("about_me", email);

                            if (imageEncodeSendTOServer != null && !imageEncodeSendTOServer.equals(" ") && !imageEncodeSendTOServer.isEmpty()) {
                                params.put("pic", imageEncodeSendTOServer);
                                params.put("ext", "jpg");
                            }

                        }
                        return params;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq);
            }
        });

    }

    //==============================facebook login======================================================================
    // ====================================================================================================
    private void fb_login() {

        progressRelativeLayout1.setVisibility(View.VISIBLE);
        FROM_DATA = 1;
        loginButton.setReadPermissions(new String[]{"email", "user_hometown", "user_likes", "public_profile"});
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
                String accessToken = loginResult.getAccessToken().getToken();
                Tracer.error("accessToken", accessToken);
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject obj,
                                                    GraphResponse response) {
                                last_name_edit_textField.setText("");
                                email_edit_textField.setText("");
                                phone_edit_textField.setText("");
                                firist_edit_textField.setText("");
                                dateofbirth_editField.setText("");
                                progressRelativeLayout1.setVisibility(View.GONE);
                                String LOGIN = sharedPreference.getFromLogedIn(getActivity(), "fromLogedin");
                                if (LOGIN.equals("google"))
                                    logOutFromGoogle();

                                Tracer.error("facebookResponce===", response.toString());
                                try {
                                    final String firstName = obj.optString("first_name");
                                    final String lastName = obj.optString("last_name");
                                    final String gender = obj.optString("gender");
                                    final String mail_id = obj.optString("email");
                                    Tracer.error("id", obj.optString("id"));
                                    String id = obj.optString("id");
                                    if (getActivity() == null)
                                        return;
                                    if (!TextUtils.isEmpty(gender))
                                        if (gender.equals("male") || gender.equals("Male"))
                                            sharedPreference.setGender(getActivity(), "gender_id", "" + 0);
                                        else if (gender.equals("female") || gender.equals("Female"))
                                            sharedPreference.setGender(getActivity(), "gender_id", "" + 1);

                                    if (!TextUtils.isEmpty(id)) {
                                        final String URL_FB_IMAGE = "http://graph.facebook.com/" + id + "/picture?type=large&redirect=false";

                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                getProfileFromFb(URL_FB_IMAGE, firstName, lastName, gender, mail_id);
                                            }
                                        });

                                    }

                                    // profilePicUrlFromFb = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Tracer.error("first_name", obj.optString("first_name"));
                                    Tracer.error("last_name", obj.optString("last_name"));
                                    Tracer.error("gender", obj.optString("gender"));
                                    Tracer.error("name", obj.optString("name"));
                                    Tracer.error("link", obj.optString("link"));
                                    Tracer.error("locale", obj.optString("locale"));
                                    Tracer.error("Email:", obj.optString("email"));

                                    sharedPreference.setEmailId(getActivity(), "email_id", mail_id);
                                    sharedPreference.setUserName(getActivity(), "first_name", firstName);
                                    sharedPreference.setUserLastName(getActivity(), "last_name", lastName);
                                    sharedPreference.setDob(getActivity(), "dob", "");
                                    //sharedPreference.setPhoneNumber(getActivity(), "phone", "");


                                    Handler mainHandler = new Handler(getActivity().getMainLooper());

                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            getDatialsFromShareprefernce();
                                            String myRadioId = sharedPreference.getGender(getActivity(), "gender_id");
                                            Log.e("GENDER", "" + myRadioId);
                                            if (myRadioId != null && !myRadioId.equals("") && !myRadioId.isEmpty()) {
                                                if (myRadioId.equals("0")) {
                                                    male.setChecked(true);
                                                    male.setTextColor(getResources().getColor(R.color.black));
                                                } else if (myRadioId.equals("1")) {
                                                    female.setChecked(true);
                                                    female.setTextColor(getResources().getColor(R.color.black));
                                                }
                                                /*selectedId = radioSexGroup.getCheckedRadioButtonId();
                                                if (selectedId > 0) {
                                                    radioButton = (RadioButton) view.findViewById(selectedId);
                                                }*/
                                            }
                                            FROM_LOGIN_FACEBOOK.setImageResource(R.mipmap.sign_up);
                                            FROM_LOGIN_GOOGLE.setImageResource(R.mipmap.no_sign_up);

                                            imageSenderInterface.sendMobileNumber("");
                                            if (!TextUtils.isEmpty(mail_id))
                                                imageSenderInterface.sendEmailId(mail_id);

                                            if (!TextUtils.isEmpty(firstName))
                                                imageSenderInterface.sendUserFiristName(firstName);

                                            if (!TextUtils.isEmpty(lastName))
                                                imageSenderInterface.sendUserLastName(firstName, lastName);

                                        } // This is your code
                                    };
                                    mainHandler.post(myRunnable);
                                    sharedPreference.setFromLogedIn(getActivity(), "fromLogedin", "facebook");
                                    sharedPreference.setPreferencesString(getActivity(), "intex_data", "");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Tracer.error("Fb-error: ", e.getMessage().toString());

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
                progressRelativeLayout1.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                progressRelativeLayout1.setVisibility(View.GONE);

                try {
                    Log.v("LoginActivity", exception.getCause().toString());
                    Toast.makeText(getActivity(), "" + exception.getCause(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Tracer.error("EditProfileFragment", e.getMessage());
                }
            }
        });
        loginButton.performClick();
    }

    public void getProfileFromFb(String url, final String fname, final String lName, final String gender, final String email) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Tracer.error("***FACEBOOK_IMAGE***", response);
                    JSONObject mObj = new JSONObject(response);
                    try {

                        JSONObject newObj = mObj.getJSONObject("data");
                        String profilePicUrlFromFb = newObj.optString("url");
                        Tracer.error("***FB_IMAGE_URL***", profilePicUrlFromFb);
                        if (!TextUtils.isEmpty(profilePicUrlFromFb))
                            imageSenderInterface.sendImage(profilePicUrlFromFb);

                        sharedPreference.setImageUrl(getActivity(), "ImageURl", profilePicUrlFromFb);
                        if (profilePicUrlFromFb != null && !profilePicUrlFromFb.equals("null") && !profilePicUrlFromFb.isEmpty()) {
                            try {
                                URL url = new URL(profilePicUrlFromFb);
                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                                byte[] byteArrayImage = baos.toByteArray();
                                encodedImageSendFromFacebook = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                            } catch (IOException e) {
                                System.out.println(e.getMessage().toString());
                            }
                        }

                        if (getActivity() == null)
                            return;
                        sharedPreference.setImageUrl(getActivity(), "ImageURl", profilePicUrlFromFb);
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                userEditProfile(encodedImageSendFromFacebook, fname, "", lName, "", gender, email);
                            }
                        });
                    } catch (JSONException e) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Tracer.debug("Error", "Error: " + error.getMessage());

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    //==============google plus login handle result from getting user information==============================================
    //=========================================================================================
    private void handleSignInResult(GoogleSignInResult result) {
        Tracer.error("Result..... ", "handleSignInResult:" + result.getStatus().getStatusMessage());

        if (result.isSuccess()) {
            GoogleSignInAccount information = result.getSignInAccount();
            String personName = information.getDisplayName();
            String gmail_id = information.getEmail();
            String first_google_name, last_google_name = null;
            if (getActivity() == null)
                return;
            //String firstUserName;
            String[] splited = personName.split("\\s+");
            if (splited.length > 0) {
                firstGoogleUserName = splited[0];
                lastGoogleUserName = splited[1];
                sharedPreference.setUserName(getActivity(), "first_name", firstGoogleUserName);
                sharedPreference.setUserLastName(getActivity(), "last_name", lastGoogleUserName);
                first_google_name = firstGoogleUserName;
                last_google_name = lastGoogleUserName;
            } else {
                firstGoogleUserName = personName;
                first_google_name = firstGoogleUserName;
                sharedPreference.setUserName(getActivity(), "first_name", firstGoogleUserName);
                Tracer.error("person name", firstGoogleUserName);
            }

            if (lastGoogleUserName != null && !lastGoogleUserName.equals("") && !lastGoogleUserName.isEmpty()) {
                sharedPreference.setGoogleLoginLastName(getActivity(), "googleLastName", lastGoogleUserName);
                sharedPreference.setUserLastName(getActivity(), "last_name", lastGoogleUserName);
                last_google_name = lastGoogleUserName;
                Tracer.error("person name", lastGoogleUserName);
            }
            if (!TextUtils.isEmpty(personName)) {
                String LOGIN = sharedPreference.getFromLogedIn(getActivity(), "fromLogedin");
                if (LOGIN.equals("facebook"))
                    logOutFromFacebook();
                sharedPreference.setFromLogedIn(getActivity(), "fromLogedin", "google");
            }

            Tracer.error("person name", personName);
            Tracer.error("email", gmail_id);

            sharedPreference.setGoogleLoginUsername(getActivity(), "googleNAme", firstGoogleUserName);
            sharedPreference.setGoogleLoginEmail(getActivity(), "emailFromGoogle", gmail_id);
            //sharedPreference.setEmailId(getActivity(), "email_id", gmail_id);
            //sharedPreference.setDob(getActivity(), "dob", "");
            //sharedPreference.setPhoneNumber(getActivity(), "phone", "");

            String profilePic = null;
            if (information.getPhotoUrl() != null) {
                profilePic = information.getPhotoUrl().toString();
                sharedPreference.setGoogleLoginProfilePic(getActivity(), "ImageGoogleProfile", profilePic);
                Tracer.error("personPhotoUrl", profilePic);
            } else {
                Tracer.error("personPhotoUrl", "Empty");
            }

            updateDetailsFromGoogle(profilePic, gmail_id, first_google_name, last_google_name);

        } else {
            // Toast.makeText(getActivity(), "" + result.getStatus(), Toast.LENGTH_LONG).show();
            progressRelativeLayout1.setVisibility(View.GONE);
        }
    }

    //========================google login for button click====================================================================
    // ==================================================================================================================
    private void looginGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //==============validation mobile=======================
    private boolean isValidMoblieNumber(String mobileno) {
        String MOBILE_NUMBER_PATTERN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
        Pattern pattern = Pattern.compile(MOBILE_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(mobileno);
        return matcher.matches();
    }

    //==============validation email=====================
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    //===========================validation all edit-text field==================================================================
    //=====================================================================================
    private boolean validate() {
        boolean valid = true;
        String fistNAme = firist_edit_textField.getText().toString();
        String lastName = last_name_edit_textField.getText().toString();
        String mobile = phone_edit_textField.getText().toString();
        String emails = email_edit_textField.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (fistNAme.isEmpty() || fistNAme.length() < 0) {
//            firist_edit_textField.setError("Enter first name");
            Toast.makeText(getActivity(), "Enter first name", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
//            firist_edit_textField.setError(null);
        }
        if (lastName.isEmpty() || lastName.length() < 0) {
            Toast.makeText(getActivity(), "Enter last name", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
//            last_name_edit_textField.setError(null);
        }
        if (!mobile.isEmpty() && !isValidMobile(mobile)) {
//            phone_edit_textField.setError("Invalid phone number");
            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
//            phone_edit_textField.setError(null);
        }
        if (!emails.isEmpty() && !emails.matches(emailPattern)) {
//            email_edit_textField.setError("Invalid email id");
            Toast.makeText(getActivity(), "Invalid email id", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
//            email_edit_textField.setError(null);
        }
        return valid;
    }

    //============================null value set all view=======================================================================
    // ====================================================================================================
    private void edittextNull() {
        if (getActivity() == null)
            return;
        Tracer.error("edittextMethodCall", "empty");
        last_name_edit_textField.setText("");
        email_edit_textField.setText("");
        phone_edit_textField.setText("");
        firist_edit_textField.setText("");
        dateofbirth_editField.setText("");
        female.setTextColor(getResources().getColor(R.color.search_shadow));
        male.setTextColor(getResources().getColor(R.color.search_shadow));
        radioSexGroup.clearCheck();

        sharedPreference.setDob(getActivity(), "dob", "");
        sharedPreference.setGender(getActivity(), "gender_id", "");
        sharedPreference.setEmailId(getActivity(), "email_id", "");
        sharedPreference.setPhoneNumber(getActivity(), "phone", "");
        sharedPreference.setImageUrl(getActivity(), "ImageURl", "");
        sharedPreference.setUserName(getActivity(), "first_name", "");
        sharedPreference.setUserLastName(getActivity(), "last_name", "");
        sharedPreference.setFromLogedIn(getActivity(), "fromLogedin", "");
        sharedPreference.setStatusLogin(getActivity(), "", "");
        imageSenderInterface.sendImage("");
        String image = sharedPreference.getImageUrl(getActivity(), "ImageURl");
        imageSenderInterface.emptyImage(image);

    }


    //========================logut from google plus================================================================================
    // ====================================================================================================
    private void logOutFromGoogle() {
        try {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    FROM_LOGIN_GOOGLE.setImageResource(R.mipmap.no_sign_up);
                                }
                            }
                        });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=====================logut from facebook======================================================================================
    // ====================================================================================================
    private void logOutFromFacebook() {
        LoginManager.getInstance().logOut();
        FROM_LOGIN_FACEBOOK.setImageResource(R.mipmap.no_sign_up);
        if (getActivity() == null)
            return;

    }


    @Override
    public void onPauseFragment() {
        if (getActivity() == null)
            return;
        Toast.makeText(getActivity(), "onPauseFragment():", Toast.LENGTH_SHORT);
    }

    @Override
    public void onResumeFragment(String dateStr, String formattedDate) {
        if (getActivity() == null)
            return;
        Toast.makeText(getActivity(), "onRcyberlinkesumeFragment():", Toast.LENGTH_SHORT);
    }


    //=====================mobile number vaildation ======================================================================
    // ====================================================================================================

    private boolean isValidMobile(String phone) {
        Tracer.error("vaildation mob", "isValidMobile: " + phone);
        String regexStr = "^[0-9]$";

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            return false;
        }
        if (phone.length() <= 9) {
            return false;
        }
        if (phone.length() == 14) {
            return phone.startsWith("0091");
        }
        if (phone.length() == 13) {
            return phone.startsWith("+91");
        }
        if (phone.length() == 11) {
            return phone.startsWith("0");
        }
        if (phone.length() > 14) {
            return false;
        }
        return true;
    }

}
