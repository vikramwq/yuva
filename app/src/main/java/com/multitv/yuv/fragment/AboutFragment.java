package com.multitv.yuv.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.multitv.yuv.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    private static final String TAG = "Main";
    private WebView webView;
    private String summary;
    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        webView = (WebView) view.findViewById(R.id.web_view);
        //webView.loadUrl();

        Bundle bundle = getArguments();
        if (bundle != null) {
            summary = bundle.getString("key");



            Log.e("***about-tag***", "_about_" + summary);

            String text = "<html><head>"
                    + "<style type=\"text/css\">body{color: #fff;}"
                    + "</style></head>"
                    + "<body>"
                    + summary
                    + "</body></html>";

            webView.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");

//            webView.loadData(text, "text/html", "UTF-8");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.getSettings().setLightTouchEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setBackgroundColor((Color.parseColor("#3A3A3A")));
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }



}
