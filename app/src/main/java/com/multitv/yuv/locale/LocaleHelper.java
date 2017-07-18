package com.multitv.yuv.locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.PreferenceData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by naseeb on 12/2/2016.
 */

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static void onCreate(Context context) {
        String deviceLanguageName = AppUtils.getLanguageName(Locale.getDefault().getLanguage());
        String selectedLanguageCode = "";
        if (!TextUtils.isEmpty(deviceLanguageName)) {
            String languageListJsonArray = PreferenceData.getLangaugeListJsonArray(context);
            if (!TextUtils.isEmpty(languageListJsonArray)) {
                ArrayList<String> languageList = new Gson().fromJson(languageListJsonArray, new TypeToken<List<String>>() {
                }.getType());
                for (int i = 0; i < languageList.size(); i++) {
                    if (deviceLanguageName.equalsIgnoreCase(languageList.get(i))) {
                        String selectedLanguageName = languageList.get(i);
                        selectedLanguageCode = AppUtils.getLanguageCode(selectedLanguageName);
                        break;
                    }
                }
            }
        }

        if (TextUtils.isEmpty(selectedLanguageCode)) {
            selectedLanguageCode = getPersistedData(context, Locale.getDefault().getLanguage());
        }

        setLocale(context, selectedLanguageCode, true);
    }

    public static void onCreate(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        setLocale(context, lang, false);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static void setLocale(Context context, String language, boolean isNeedToSaveLanguage) {
        if (isNeedToSaveLanguage)
            persist(context, language);
        updateResources(context, language);
    }

    public static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
