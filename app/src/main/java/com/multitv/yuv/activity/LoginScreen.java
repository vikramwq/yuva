package com.multitv.yuv.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.multitv.yuv.R;
import com.multitv.yuv.firebase.FCMController;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.Utilities;

public class LoginScreen extends AppCompatActivity {
    private Toolbar toolbar;

    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(LoginScreen.this);
        FCMController.getInstance(getApplicationContext()).registerToken();
        sharedPreference = new SharedPreference();


    }
}
