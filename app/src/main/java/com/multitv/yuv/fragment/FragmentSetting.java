//package com.wamindia.dollywoodplay.fragment;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.InputType;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Patterns;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListPopupWindow;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.multitv.cipher.MultitvCipher;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//
//import com.wamindia.dollywoodplay.R;
//import com.wamindia.dollywoodplay.activity.SplashScreen;
//import com.wamindia.dollywoodplay.api.ApiRequest;
//import com.wamindia.dollywoodplay.application.AppController;
//import com.wamindia.dollywoodplay.locale.LocaleHelper;
//import com.wamindia.dollywoodplay.sharedpreference.SharedPreference;
//import com.wamindia.dollywoodplay.utilities.AppConfig;
//import com.wamindia.dollywoodplay.utilities.AppUtils;
//import com.wamindia.dollywoodplay.utilities.CustomTFSpan;
//import com.wamindia.dollywoodplay.utilities.ExceptionUtils;
//import com.wamindia.dollywoodplay.utilities.PreferenceData;
//import com.wamindia.dollywoodplay.utilities.Tracer;
//
//import static com.facebook.FacebookSdk.getApplicationContext;
//
///**
// * Created by mkr on 12/13/2016.
// */
//
//public class FragmentSetting extends Fragment implements View.OnClickListener {
//    private static final int MAX_RETRY_COUNT = 3;
//    private int mDialogCount;
//    private static final String TAG = AppConfig.BASE_TAG + ".FragmentSetting";
//    private ListPopupWindow mPopupWindowLangauge;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_setting, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        view.findViewById(R.id.fragment_setting_imageView_mature_filter).setOnClickListener(this);
//        view.findViewById(R.id.fragment_setting_imageView_notification).setOnClickListener(this);
//        view.findViewById(R.id.fragment_setting_textView_language).setOnClickListener(this);
//        view.findViewById(R.id.fragment_setting_layout_progress_bar).setOnClickListener(this);
//        view.findViewById(R.id.fragment_setting_layout_progress_bar).setVisibility(View.GONE);
//        /*String selectedLanguage = AppUtils.getLanguageNameOrignal(AppUtils.getLanguageName(Locale.getDefault().getLanguage()));
//        PreferenceData.setAppLanguage(getApplicationContext(), selectedLanguage);*/
//        initLanguageDropDown();
//        setMetureFilterUI();
//        setNotificationFilterUI();
//        /*setLanguageUI();*/
//    }
//
//    @Override
//    public void onClick(View view) {
//        Tracer.error(TAG, "onClick: " + view);
//        switch (view.getId()) {
//            case R.id.fragment_setting_imageView_mature_filter:
//                if (getActivity() == null)
//                    return;
//                if (PreferenceData.isMatureFilterEnable(getActivity())) {
//                    new MaturityConfirmationDialog(getActivity()).show();
//                } else {
//                    PreferenceData.enableMatureFilter(getActivity());
//                    setMetureFilterUI();
//                }
//                break;
//            case R.id.fragment_setting_imageView_notification:
//                PreferenceData.toggleNotification(getActivity());
//                setNotificationFilterUI();
//                break;
//           /* case R.id.fragment_setting_textView_language:
//                if (mPopupWindowLangauge != null) {
//                    if (mPopupWindowLangauge.isShowing()) {
//                        mPopupWindowLangauge.dismiss();
//                    } else {
//                        mPopupWindowLangauge.show();
//                    }
//                }
//                break;*/
//        }
//    }
//
//    /**
//     * Method to show the progress bar
//     */
//    private void showProgressBar() {
//        Tracer.error(TAG, "showProgressBar: ");
//        if (getView() != null) {
//            getView().findViewById(R.id.fragment_setting_layout_progress_bar).setVisibility(View.VISIBLE);
//        }
//    }
//
//    /**
//     * Method to hide the progress bar
//     */
//    private void hideProgressBar() {
//        Tracer.error(TAG, "hideProgressBar: ");
//        if (getView() != null) {
//            getView().findViewById(R.id.fragment_setting_layout_progress_bar).setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * Method to set the UI of the Mature Filter
//     */
//    private void setMetureFilterUI() {
//        if (getActivity() == null)
//            return;
//        Tracer.error(TAG, "setMetureFilterUI: " + (getView() != null) + "   " + (PreferenceData.isMatureFilterEnable(getActivity())));
//        if (getView() != null) {
//            if (PreferenceData.isMatureFilterEnable(getActivity())) {
//                ((ImageView) getView().findViewById(R.id.fragment_setting_imageView_mature_filter)).setImageResource(R.drawable.toggle_on);
//            } else {
//                ((ImageView) getView().findViewById(R.id.fragment_setting_imageView_mature_filter)).setImageResource(R.drawable.toggle_off);
//            }
//        }
//    }
//
//    /**
//     * Method to set the UI of the Notification status
//     */
//    private void setNotificationFilterUI() {
//        Tracer.error(TAG, "setNotificationFilterUI: ");
//        if (getView() != null) {
//            if (getActivity() == null)
//                return;
//            ((ImageView) getView().findViewById(R.id.fragment_setting_imageView_notification)).setImageResource(PreferenceData.isNotificationEnable(getActivity()) ? R.drawable.toggle_on : R.drawable.toggle_off);
//        }
//    }
//
//    /**
//     * Method to set the UI of the Notification status
//     */
//    private void setLanguageUI() {
//        Tracer.error(TAG, "setLanguageUI: ");
//        if (getView() != null) {
//            if (getActivity() == null)
//                return;
//            ((TextView) getView().findViewById(R.id.fragment_setting_textView_language)).setText(PreferenceData.getAppLanguage(getActivity()));
//        }
//    }
//
//    /**
//     * Method to set the Lang-List
//     */
//    private void initLanguageDropDown() {
//        Tracer.error(TAG, "initLanguageDropDown: ");
//        if (getView() != null) {
//            if (getActivity() == null)
//                return;
//            String langaugeListJsonArray = PreferenceData.getLangaugeListJsonArray(getActivity());
//            ArrayList<String> languageList = new Gson().fromJson(langaugeListJsonArray, new TypeToken<List<String>>() {
//            }.getType());
//
//            if (mPopupWindowLangauge != null) {
//                mPopupWindowLangauge.dismiss();
//                mPopupWindowLangauge = null;
//            }
//            final PopupAdapter suggestionAdapter = new PopupAdapter(getActivity());
//            mPopupWindowLangauge = new ListPopupWindow(getActivity());
//            mPopupWindowLangauge.setAdapter(suggestionAdapter);
//            languageList.clear();
//            languageList.add("English");
//            suggestionAdapter.setSuggestion(languageList);
//            mPopupWindowLangauge.setAnchorView(getView().findViewById(R.id.fragment_setting_textView_language));
//            mPopupWindowLangauge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    String newLanguage = suggestionAdapter.getItem(i);
//                    showLanguageConfirmationDialog(newLanguage);
//                    mPopupWindowLangauge.dismiss();
//                }
//            });
//            mPopupWindowLangauge.setModal(true);
//        }
//    }
//
//    /**
//     * Method to show the Language confirmation Dialog
//     *
//     * @param newLanguage New language selected by the user
//     */
//    private void showLanguageConfirmationDialog(final String newLanguage) {
//        if (getActivity() == null)
//            return;
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(getString(R.string.dialog_lang_message) + " " + newLanguage);
//        builder.setIcon(R.drawable.toolbar_icon);
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-regular.ttf");
//        CustomTFSpan tfSpan = new CustomTFSpan(tf);
//        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
//        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.setTitle(spannableString);
//
//        builder.setPositiveButton(getString(R.string.dialog_lang_button_ok), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                PreferenceData.setAppLanguage(getContext(), AppUtils.getLanguageNameOrignal(newLanguage));
//                LocaleHelper.setLocale(getContext(), AppUtils.getLanguageCode(newLanguage), true);
//                setLanguageUI();
//                Intent intent = new Intent(getActivity(), SplashScreen.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                if (getActivity() == null)
//                    return;
//                getActivity().finish();
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton(getString(R.string.dialog_lang_button_cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//            }
//        });
//        builder.setCancelable(false);
//        builder.create().show();
//    }
//
//    /**
//     * Method to show the dialog to get OTP received by the user
//     */
//    private void showOTPEnteredDialog() {
//        mDialogCount--;
//        if (getActivity() == null)
//            return;
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setIcon(R.drawable.toolbar_icon);
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-regular.ttf");
//        CustomTFSpan tfSpan = new CustomTFSpan(tf);
//        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
//        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.setTitle(spannableString);
//        View parentEditTExtView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edittext, null);
//        final EditText input = (EditText) parentEditTExtView.findViewById(R.id.dialog_maturiry_otp_editText);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(parentEditTExtView);
//        builder.setPositiveButton(getString(R.string.dialog_maturiry_otp_input_ok), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String otp = input.getText().toString().trim();
//                if (otp.length() == 0) {
//                    if (getActivity() == null)
//                        return;
//                    Toast.makeText(getActivity(), getString(R.string.dialog_maturiry_content_invalid_phone_otp), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                hideProgressBar();
//                requestToRegisterOTP(otp);
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton(getString(R.string.dialog_maturiry_otp_input_cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                hideProgressBar();
//                dialog.cancel();
//            }
//        });
//        builder.setCancelable(false);
//        builder.show();
//    }
//
//    /**
//     * Method to request to disable the maturity filter
//     */
//    private void requestToRegisterOTP(final String OTP) {
//        Tracer.error(TAG, "requestToRegisterOTP: ");
//        showProgressBar();
//        if (getActivity() == null)
//            return;
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getActivity(), ApiRequest.VERIFY_OTP), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Tracer.error(TAG, "requestToRegisterOTP: onResponse: ");
//                hideProgressBar();
//                try {
//                    JSONObject mObj = new JSONObject(response);
//                    if (mObj.optInt("code") == 1) {
//                        MultitvCipher mcipher = new MultitvCipher();
//                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
//                        Tracer.error(TAG, "requestToRegisterOTP: onResponse: " + str);
//                        if (getActivity() == null)
//                            return;
//                        PreferenceData.disableMatureFilter(getActivity());
//                        setMetureFilterUI();
//                    } else if (mObj.optInt("code") == 0) {
//                        if (mDialogCount > 0) {
//                            showOTPEnteredDialog();
//                        }
//                        if (getActivity() == null)
//                            return;
//                        Toast.makeText(getActivity(), "You have entered wrong otp. Please check it and try again.", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    ExceptionUtils.printStacktrace(e);
//                    if (mDialogCount > 0) {
//                        showOTPEnteredDialog();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Tracer.error(TAG, "requestToRegisterOTP: onErrorResponse: " + error.getMessage());
//                hideProgressBar();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
//                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
//
//                if (getActivity() == null)
//                    return null;
//                String user_id = new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);
//                params.put("otp", OTP);
//                params.put("user_id", user_id);
//                Set<String> keys = params.keySet();
//                for (String key : keys) {
//                    Tracer.error(TAG, "requestToRegisterOTP().getParams: " + key + "      " + params.get(key));
//                }
//                return params;
//            }
//        };
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//    }
//
//    /**
//     * Method to request to disable the maturity filter
//     *
//     * @param value
//     * @param isVeryfyByEmail
//     */
//    private void requestToDisableMaturityFilter(final String value, final boolean isVeryfyByEmail) {
//        Tracer.error(TAG, "requestToDisableMaturityFilter: ");
//        showProgressBar();
//        if (getActivity() == null)
//            return;
//        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUtils.generateUrl(getActivity(), ApiRequest.MATURITY_FILTER), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Tracer.error(TAG, "requestToDisableMaturityFilter: onResponse: ");
//                try {
//                    JSONObject responseJson = new JSONObject(response);
//                    Tracer.error(TAG, "requestToDisableMaturityFilter: onResponse: ENC :  " + responseJson);
//                    if (responseJson.optInt("code") == 1) {
//                        mDialogCount = MAX_RETRY_COUNT;
//                        showOTPEnteredDialog();
//                    } else {
//
//                        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-regular.ttf");
//                        CustomTFSpan tfSpan = new CustomTFSpan(tf);
//                        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
//                        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        hideProgressBar();
//                        if (getActivity() == null)
//                            return;
//                        new AlertDialog.Builder(getActivity())
//                                .setIcon(R.drawable.toolbar_icon)
//                                .setTitle(spannableString)
//                                .setMessage(isVeryfyByEmail ? getString(R.string.dialog_wrong_message_email) : getString(R.string.dialog_wrong_message_phone_number))
//                                .setPositiveButton(getString(R.string.dialog_wrong_message_ok), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();
//                    }
//
//                } catch (Exception e) {
//                    hideProgressBar();
//                    ExceptionUtils.printStacktrace(e);
//                    Tracer.debug(TAG, "requestToDisableMaturityFilter:onResponse: 2 " + e.getMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Tracer.error(TAG, "requestToDisableMaturityFilter: onErrorResponse: " + error.getMessage());
//                hideProgressBar();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//
//                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
//                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
//
//                if (getActivity() == null)
//                    return null;
//                String user_id = new SharedPreference().getPreferencesString(getActivity(), "user_id" + "_" + ApiRequest.TOKEN);
//                params.put("user_id", user_id);
//                params.put("type", isVeryfyByEmail ? "email" : "mobile");
//                params.put("value", value);
//                Set<String> keys = params.keySet();
//                for (String key : keys) {
//                    Tracer.error(TAG, "requestToDisableMaturityFilter().getParams: " + key + "      " + params.get(key));
//                }
//                return params;
//            }
//        };
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//    }
//
//    /**
//     * Class to show tthe language confirmation dialog
//     */
//    private class MaturityConfirmationDialog extends AlertDialog implements View.OnClickListener {
//        private static final String TAG = FragmentSetting.TAG + ".MaturityConfirmationDialog";
//
//        public MaturityConfirmationDialog(Context context) {
//            super(context);
//            setCancelable(false);
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//            setContentView(R.layout.dialog_maturity_content);
//            findViewById(R.id.dialog_maturity_content_textView_ok).setOnClickListener(this);
//            findViewById(R.id.dialog_maturity_content_textView_cancel).setOnClickListener(this);
//            final EditText editTextEmail = (EditText) findViewById(R.id.dialog_maturity_content_editText_email);
//            final EditText editTextPhoneNumber = (EditText) findViewById(R.id.dialog_maturity_content_editText_phone_number);
//            editTextEmail.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    String text = charSequence.toString().trim();
//                    Tracer.error(TAG, "onTextChanged: EMAIL " + text.length());
//                    if (text.length() > 0) {
//                        editTextPhoneNumber.setFocusable(false);
//                        editTextPhoneNumber.setClickable(false);
//                    } else {
//                        editTextPhoneNumber.setClickable(true);
//                        editTextPhoneNumber.setFocusable(true);
//                        editTextPhoneNumber.setFocusableInTouchMode(true);
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });
//            editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    String text = charSequence.toString().trim();
//                    Tracer.error(TAG, "onTextChanged: PHONE NUMBER " + text.length());
//                    if (text.length() > 0) {
//                        editTextEmail.setFocusable(false);
//                        editTextEmail.setClickable(false);
//                    } else {
//                        editTextEmail.setClickable(true);
//                        editTextEmail.setFocusable(true);
//                        editTextEmail.setFocusableInTouchMode(true);
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });
//        }
//
//
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.dialog_maturity_content_textView_ok:
//                    if (getActivity() == null)
//                        return;
//                    String email = ((EditText) findViewById(R.id.dialog_maturity_content_editText_email)).getText().toString().trim();
//                    String phoneNumber = ((EditText) findViewById(R.id.dialog_maturity_content_editText_phone_number)).getText().toString().trim();
//                    if (!findViewById(R.id.dialog_maturity_content_editText_email).isFocusable() && !findViewById(R.id.dialog_maturity_content_editText_phone_number).isFocusable()) {
//                        Toast.makeText(getActivity(), getString(R.string.dialog_maturiry_content_valid_value), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (findViewById(R.id.dialog_maturity_content_editText_email).isFocusable()) {
//                        if (email.isEmpty() || !isValidEmail(email)) {
//                            Toast.makeText(getActivity(), getString(R.string.dialog_maturiry_content_invalid_email), Toast.LENGTH_SHORT).show();
//                        } else {
//                            requestToDisableMaturityFilter(email, true);
//                            dismiss();
//                        }
//                        return;
//                    }
//                    if (findViewById(R.id.dialog_maturity_content_editText_phone_number).isFocusable()) {
//                        if (phoneNumber.isEmpty() || !isValidMobile(phoneNumber)) {
//                            Toast.makeText(getActivity(), getString(R.string.dialog_maturiry_content_invalid_phone_number), Toast.LENGTH_SHORT).show();
//                        } else {
//                            requestToDisableMaturityFilter(phoneNumber, false);
//                            dismiss();
//                        }
//                        return;
//                    }
//                    dismiss();
//                    break;
//                case R.id.dialog_maturity_content_textView_cancel:
//                    dismiss();
//                    break;
//            }
//        }
//
//        private boolean isValidMobile(String phone) {
//            Tracer.error(TAG, "isValidMobile: " + phone);
//            if (!Patterns.PHONE.matcher(phone).matches()) {
//                return false;
//            }
//            if (!(phone.length() == 14 || phone.length() == 13 || phone.length() == 10 || phone.length() == 11)) {
//                return false;
//            }
//            if (phone.length() == 14) {
//                return phone.startsWith("0091");
//            }
//            if (phone.length() == 13) {
//                return phone.startsWith("+91");
//            }
//            if (phone.length() == 11) {
//                return phone.startsWith("0");
//            }
//            return true;
//        }
//
//        private boolean isValidEmail(CharSequence target) {
//            Tracer.error(TAG, "isValidEmail: " + target);
//            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
//        }
//
//    }
//
//    /**
//     * Adapter class to hold the Item
//     */
//    private class PopupAdapter extends BaseAdapter {
//        private ArrayList<String> mSuggestionList;
//        private LayoutInflater mLayoutInflater;
//
//        public PopupAdapter(Context context) {
//            mSuggestionList = new ArrayList<>();
//            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        /**
//         * Method to set the Suggestion List
//         *
//         * @param suggestionList
//         */
//        public void setSuggestion(ArrayList<String> suggestionList) {
//            mSuggestionList.clear();
//            if (suggestionList != null) {
//                mSuggestionList.addAll(suggestionList);
//            }
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return mSuggestionList.size();
//        }
//
//        @Override
//        public String getItem(int position) {
//            return mSuggestionList.get(position);
//        }
//
//        @Override
//        public long getItemId(int id) {
//            return id;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            if (view == null) {
//                view = mLayoutInflater.inflate(R.layout.drop_down_list_item, null);
//            }
//            ((TextView) view.findViewById(R.id.drop_down_list_item_textView)).setText(AppUtils.getLanguageNameOrignal(getItem(position)));
//            return view;
//        }
//    }
//
//    @Override
//    public void onResume() {
//        String selectedLanguage = AppUtils.getLanguageNameOrignal(AppUtils.getLanguageName(Locale.getDefault().getLanguage()));
//        PreferenceData.setAppLanguage(getApplicationContext(), selectedLanguage);
//        setLanguageUI();
//
//        super.onResume();
//    }
//}
