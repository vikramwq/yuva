package com.multitv.yuv.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.ExceptionHandler;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;

import static com.multitv.yuv.R.id.languageList;

/**
 * Created by naseeb on 12/13/2016.
 */

public class LanguageActivity extends AppCompatActivity {
    private static final String TAG = AppConfig.BASE_TAG + ".LanguageActivity";

    @BindView(languageList)
    ListView languageListView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        setContentView(R.layout.activity_language);
        ExceptionHandler.attach();
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        String languageListJsonArray = PreferenceData.getLangaugeListJsonArray(this);
        if (TextUtils.isEmpty(languageListJsonArray))
            requestLanguageData();
        else {
            ArrayList<String> languageList = new Gson().fromJson(languageListJsonArray, new TypeToken<List<String>>() {
            }.getType());

            if (languageList != null && !languageList.isEmpty())
                setLanguageList(languageList);
            else
                requestLanguageData();
        }
    }

    private ArrayList<String> orignalLangList(ArrayList<String> languageList) {
        ArrayList<String> newLanguageList = new ArrayList<>();
        for (int i = 0; i < languageList.size(); i++) {
            newLanguageList.add(AppUtils.getLanguageNameOrignal(languageList.get(i)));
        }
        return newLanguageList;
    }

    private void setLanguageList(ArrayList<String> languageList) {
        languageList = orignalLangList(languageList);
        languageList.clear();
        languageList.add("English");
        languageListView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, languageList));

        languageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long mylng) {
                selectedLanguage = (String) (languageListView.getItemAtPosition(position));
            }
        });
        setDeviceLanguageAsPreferred(languageList);
        progressBar.setVisibility(View.GONE);
        languageListView.setVisibility(View.VISIBLE);
    }

    private void setDeviceLanguageAsPreferred(ArrayList<String> languageList) {
        String deviceLanguageName = AppUtils.getLanguageName(Locale.getDefault().getLanguage());
        for (int i = 0; i < languageList.size(); i++) {
            if (deviceLanguageName.equalsIgnoreCase(languageList.get(i))) {
                languageListView.setItemChecked(i, true);
                selectedLanguage = languageList.get(i);
                /*languageListView.setSelection(i);*/
                break;
            }
        }
    }


    public void moveToHomeActivity(View view) {
        if (TextUtils.isEmpty(selectedLanguage)) {
            Toast.makeText(this, "Please select your preferred language", Toast.LENGTH_SHORT).show();
            return;
        }
        PreferenceData.setAppLanguage(getApplicationContext(), selectedLanguage);
        LocaleHelper.setLocale(LanguageActivity.this, AppUtils.getLanguageCodeByOriginalName(selectedLanguage), true);
        PreferenceData.setPreviousFragmentSelectedPosition(getApplicationContext(), 0);
        Intent intent = new Intent(LanguageActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void requestLanguageData() {
        Tracer.error(TAG, "requestLanguageData: ");
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getApplicationContext(), ApiRequest.LANGUAGES), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    Tracer.error(TAG, "requestLanguageData().onResponse: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray languageJsonArray = jsonObject.optJSONArray("result");
                    ArrayList<String> languageList = new ArrayList<>();
                    for (int i = 0; i < languageJsonArray.length(); i++) {
                        languageList.add(languageJsonArray.optString(i));
                    }
                    String jsonString = new Gson().toJson(languageList, new com.google.gson.reflect.TypeToken<List<String>>() {
                    }.getType());
                    PreferenceData.setLangaugeListJsonArray(getApplicationContext(), jsonString);

                    setLanguageList(languageList);
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                    Tracer.error(TAG, "requestLanguageData().onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Tracer.error(TAG, "requestLanguageData().onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
