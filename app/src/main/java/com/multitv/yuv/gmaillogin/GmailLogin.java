package com.multitv.yuv.gmaillogin;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.multitv.yuv.utilities.Tracer;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Class used to login user via user, User must call onActivityResult() method
 * of this class from its Login Activity. User must implement GooglePlayService.
 */
public class GmailLogin implements OnGmailLoginAsyncTaskListener {

    static final String SERVER_CLIENT_ID = "529726091213-e54r6b170opljmgci4bkspavq9eksp4c.apps.googleusercontent.com";
    static final String SERVER_SECREAT = "DeFmaVlEleRzBowkpynkzTjX";
    private static final String LOGIN_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String READ_MAIL_SCOPE = "";
    private static final String SCOPES = "oauth2:server:client_id:" + SERVER_CLIENT_ID + ":api_scope:" + LOGIN_SCOPE + " " + READ_MAIL_SCOPE;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 999;
    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    private String mEmail;
    private Activity mActivity;
    private OnGmailLoginListener mOnGmailLoginListener;
    private boolean mIsStartLogin = false;

    /**
     * Constructor
     *
     * @param activity
     * @param onGmailLoginListener Callback to listen the Events occur at the time of Gmail Login
     */
    public GmailLogin(Activity activity, OnGmailLoginListener onGmailLoginListener) {
        mActivity = activity;
        mOnGmailLoginListener = onGmailLoginListener;
    }

    /**
     * Method to start login process
     */
    public void startLogin() {
        mIsStartLogin = true;
        getUsername();
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account
     */
    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        mActivity.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /**
     * Method to attempt to get the user name. If the email address isn't known
     * yet, then call pickUserAccount() method so the user can pick an account.
     */
    private void getUsername() {
        Tracer.error("MKR", "GmailLogin.getUsername() " + mEmail);
        if (mEmail == null || mEmail.trim().isEmpty()) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                getTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Toast.makeText(mActivity, "No network connection available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method to checks whether the device currently has a network connection
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Method to need to be called from the onActivityResult method of the
     * activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIsStartLogin) {
            if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
                if (resultCode == Activity.RESULT_OK) {
                    Tracer.error("MKR", "GmailLogin.onActivityResult() Result OK " + data);
                    Tracer.error("MKR", "GmailLogin.onActivityResult() Result OK " + data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                    mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    getUsername();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Tracer.error("MKR", "GmailLogin.onActivityResult() Result canceled");
                    Toast.makeText(mActivity, "You must pick an account", Toast.LENGTH_SHORT).show();
                }
            } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) && resultCode == Activity.RESULT_OK) {
                Tracer.error("MKR", "GmailLogin.onActivityResult() Handle Authorization error");
                handleAuthorizeResult(resultCode, data);
                return;
            }
        }
    }

    /**
     * Method to get the Asynctask to login via GMAIL
     *
     * @return
     */
    private GmailLoginAsyncTask getTask() {
        return new GmailLoginAsyncTask(mActivity, this, mEmail, SCOPES);
    }

    /**
     * Method to handle the Authorization result
     *
     * @param resultCode
     * @param data
     */
    private void handleAuthorizeResult(int resultCode, Intent data) {
        if (data == null) {
            onGmailLoginAsyncTaskListenerShowMesage("Unknown error, click the button again");
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            getTask().execute();
            return;
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            onGmailLoginAsyncTaskListenerShowMesage("User rejected authorization.");
            return;
        }
        onGmailLoginAsyncTaskListenerShowMesage("Unknown error, click the button again");
    }

    @Override
    public void onGmailLoginAsyncTaskListenerShowMesage(String msg) {
        Toast.makeText(mActivity, "" + msg, Toast.LENGTH_SHORT).show();
        Tracer.error("MKR", "GmailLogin.onGmailLoginAsyncTaskListenerShowMesage() " + msg);
    }

    @Override
    public void onGmailLoginAsyncTaskListenerUserData(Object object) {
        Tracer.error("MKR", "GmailLogin.onGmailLoginAsyncTaskListenerUserData() " + (object instanceof ProfilePojo));
        if (object instanceof ProfilePojo && mOnGmailLoginListener != null) {
            mOnGmailLoginListener.onGmailLoginListenerUserData((ProfilePojo) object);
            return;
        }
        if (object instanceof Exception) {
            if (object instanceof GooglePlayServicesAvailabilityException) {
                // The Google Play services APK is old, disabled, or not
                // present.
                // Show a dialog created by Google Play services that allows
                // the user to update the APK
                int statusCode = ((GooglePlayServicesAvailabilityException) object).getConnectionStatusCode();
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, mActivity, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                dialog.show();
                onGmailLoginAsyncTaskListenerShowMesage("Google Play Service is Old or Disable or Not Present");
            } else if (object instanceof UserRecoverableAuthException) {
                // Unable to authenticate, such as when the user has not yet
                // granted
                // the app access to the account, but the user can fix this.
                // Forward the user to an activity in Google Play services.
                Intent intent = ((UserRecoverableAuthException) object).getIntent();
                mActivity.startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
            }
            if (mOnGmailLoginListener != null) {
                onGmailLoginAsyncTaskListenerShowMesage(((Exception) object).getMessage());
                mOnGmailLoginListener.onGmailLoginListenerException((Exception) object);
            }
            return;
        }
        if (mOnGmailLoginListener != null) {
            onGmailLoginAsyncTaskListenerShowMesage("Unknown Error");
            mOnGmailLoginListener.onGmailLoginListenerException(new MKRException("Unknown Error"));
        }
    }
}
