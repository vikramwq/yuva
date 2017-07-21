package com.multitv.yuv.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppNetworkAlertDialog;
import com.multitv.yuv.utilities.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassword extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText email_et;
    private TextInputLayout input_layout_email;

    private SharedPreference sharedPreference;
    private ProgressBar progressBar;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Forgot Password");
        toolbar.setLogo(getResources().getDrawable(R.drawable.toolbar_icon));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(ForgetPassword.this);
        email_et = (EditText) findViewById(R.id.email_et);
        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        progressBar = (ProgressBar) findViewById(R.id.progress_signin);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if (validate()) {
                    String emailId = email_et.getText().toString();
                    if (!TextUtils.isEmpty(emailId)) {
                        sendPasswordOnMail(emailId);
                    } else {
                        Toast.makeText(ForgetPassword.this, getResources().getString(R.string.email_forgot_passowrd), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }


    private boolean validate() {
        boolean valid = true;
        String email = email_et.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern)) {
            email_et.setError("Please enter a registered Email Id");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            email_et.setError("Please enter a registered Email Id");
            valid = false;
        }


        return valid;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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


    private boolean validateEmail() {
        boolean valid = true;
        String email = email_et.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (TextUtils.isEmpty(email)) {
            valid = false;
            return valid;

        } else {
//            input_email.setError(null);
        }

        if (!TextUtils.isEmpty(email) && !email.matches(emailPattern)) {
            input_layout_email.requestFocus();
            input_layout_email.setError("Please enter a valid Email Address");
            valid = false;
            return valid;

        } else {

//            input_email.setError(null);

        }


        return valid;
    }


    private void sendPasswordOnMail(final String email) {
        if (!AppNetworkAlertDialog.isNetworkConnected(ForgetPassword.this)) {
            Toast.makeText(ForgetPassword.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3 + ApiRequest.FORGET_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("LoginActivity", "FORGOT-URL_:_" + response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
                        String str = mObj.optString("result");
                        //customToast.showToastMessage(ForgotPasswordActivity.this,str);
                        Intent intent = new Intent(ForgetPassword.this, LoginScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        ForgetPassword.this.finish();
                        Toast.makeText(ForgetPassword.this, getResources().getString(R.string.forgot_password_successful_message), Toast.LENGTH_LONG).show();
                        Log.e("LOGINACTIVITY", "***FORGOT-URL-RESPONCE**" + str);

                    } else {
                        String error = mObj.optString("result");
                        if (!TextUtils.isEmpty(error))
                            Toast.makeText(ForgetPassword.this, "Please enter a registered Email Id", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        email_et.setError("Please enter a registered Email Id");
                    }
                } catch (Exception e) {
                    Log.e("LoginActivity", "FORGOT---****--Error" + "" + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgetPassword.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoginActivity", "FORGOT-Error: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}
