package com.multitv.yuv.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;

import com.multitv.yuv.R;


/**
 * Created by root on 28/9/16.
 */
public class ConnectionDetector {
    public static final String TAG = AppConfig.BASE_TAG + ".ConnectionDetector";

    /**
     * Checking for all possible internet providers
     **/
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }
        // Toast.makeText(mContext,mContext.getString(R.string.please_connect_to_internet), Toast.LENGTH_SHORT).show();
        return false;
    }


    public static void showNetworkNotConnectedDialogSplash(final Activity activity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("You are not connected to internet .");
            Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/Ubuntu-regular.ttf");
            CustomTFSpan tfSpan = new CustomTFSpan(tf);
            SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.app_name));
            spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(spannableString);
            builder.setIcon(R.drawable.toolbar_icon);

            builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        } catch (Exception e) {
            Tracer.error(TAG, "showNetworkNotConnectedDialog: " + e.getMessage());
        }
    }


    public static void showNetworkNotConnectedDialog(final Activity activity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("You are not connected to internet .");
            Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/Ubuntu-regular.ttf");
            CustomTFSpan tfSpan = new CustomTFSpan(tf);
            SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.app_name));
            spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(spannableString);
            builder.setIcon(R.drawable.toolbar_icon);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        } catch (Exception e) {
            Tracer.error(TAG, "showNetworkNotConnectedDialog: " + e.getMessage());
        }
    }


}
