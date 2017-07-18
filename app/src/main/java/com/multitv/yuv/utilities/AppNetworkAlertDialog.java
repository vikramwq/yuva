package com.multitv.yuv.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

import com.multitv.yuv.R;


/**
 * Created by Lenovo on 07-03-2017.
 */

public class AppNetworkAlertDialog {

    public static void showNetworkNotConnectedDialog(final Activity activity) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("You are not connected to internet .");
            Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/app_customfonts.ttf");
            CustomTFSpan tfSpan = new CustomTFSpan(tf);
            SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.app_name));
            spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(spannableString);
            builder.setIcon(R.drawable.toolbar_icon);
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(isNetworkConnected(activity)){
                        dialog.dismiss();

                    }else{
                        builder.create().show();
                    }

                }
            });
            builder.setCancelable(false);
            builder.create().show();
        } catch (Exception e) {
            Log.e("Network", "showNetworkNotConnectedDialog: " + e.getMessage());
        }
    }

    public static boolean isNetworkConnected(final Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
