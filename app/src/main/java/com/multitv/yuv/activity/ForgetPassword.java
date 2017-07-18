package com.multitv.yuv.activity;

import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.multitv.yuv.R;
import com.multitv.yuv.utilities.Utilities;

public class ForgetPassword extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText email_et;
    private TextInputLayout input_layout_email;

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
}
