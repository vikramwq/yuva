package com.multitv.yuv.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;


import com.multitv.yuv.R;
import com.multitv.yuv.utilities.Utilities;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar toolbar;
    private String url;
    private int type;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.applyFontForToolbarTitle(WebViewActivity.this);

        url = getIntent().getStringExtra("url");
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            getSupportActionBar().setTitle("Terms of use");
        } else {
            getSupportActionBar().setTitle("Privacy Policy");
        }
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

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