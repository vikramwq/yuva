package com.multitv.yuv.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.multitv.yuv.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {

   /* private WebView webView;*/
    private static final String TAG = "Main";

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_help, container, false);
       /* webView=(WebView)view.findViewById(R.id.web_help);
        loadHelp();*/

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            String helpData = bundle.getString("HELP_DATA");

            if(getView() == null || getActivity() == null)
                return;

            TextView helpTextView = (TextView) getView().findViewById(R.id.textview_help);
            if(!TextUtils.isEmpty(helpData))
                helpTextView.setText(Html.fromHtml(helpData));
            else
                helpTextView.setText(getActivity().getResources().getString(R.string.no_record_found_txt));
        }
    }

    /*public void loadHelp(){
        customProgressDialog = CustomProgressDialog.getInstance(getActivity());
        customProgressDialog.show(getActivity());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " +url);
                customProgressDialog.dismiss(getActivity());
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Tracer.error(TAG, "Error: " + description);
                Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webView.loadUrl("http://www.intex.in/ContactUs/Index");

    }
*/
}
