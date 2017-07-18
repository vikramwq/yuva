package com.multitv.yuv.utilities;

/**
 * Created by root on 28/9/16.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;

/**
 * Created by cyberlinks on 3/7/16.
 */
public class NetworkAlertDialog {

    Context context;



    public NetworkAlertDialog(Context con){
        this.context=con;
    }



    public void showAlertDialog(String title, String message, boolean status) {
        //  alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);



        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-regular.ttf");
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder1.setTitle(spannableString);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        // Setting alert dialog icon
        // builder1.setIcon((status) ? R.drawable.success : R.drawable.fail);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

       /* builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });*/

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
