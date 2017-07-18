package com.multitv.yuv.utilities;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by delhivery on 21/3/16.
 */
public class Tracer {
    private final static Boolean LOG_ENABLE = AppConfig.SHOW_LOG;

    /**
     * Method to print debug log<br>
     * {@link Log} Information
     *
     * @param TAG
     * @param message
     */
    public static void debug(String TAG, String message) {
        if (LOG_ENABLE && !TextUtils.isEmpty(message)) {
            Log.d(TAG, message);
        }
    }

    /**
     * Method to write data in Disk
     *
     * @param TAG
     * @param num
     * @param caller
     * @param data
     */
    public static void writeOnDisk(String TAG, int num, String caller, String data) {
        if (LOG_ENABLE) {
            Tracer.debug(TAG, "Tracer.writeOnDisk() " + caller);
            File fileDir = new File(Environment.getExternalStorageDirectory(), "CTA");
            Tracer.debug(TAG, "Tracer.writeOnDisk() " + fileDir.getAbsolutePath());
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            File file = new File(fileDir.getAbsolutePath(), "" + caller + "_" + num + "_" + System.currentTimeMillis() + ".txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method to print error log<br>
     * {@link Log} Error
     *
     * @param TAG
     * @param message
     */
    public static void error(String TAG, String message) {
        if (LOG_ENABLE && !TextUtils.isEmpty(message)) {
            Log.e(TAG, message);
        }
    }

    /**
     * Method to print information log<br>
     * {@link Log} Debug
     *
     * @param TAG
     * @param message
     */
    public static void info(String TAG, String message) {
        if (LOG_ENABLE && !TextUtils.isEmpty(message)) {
            Log.i(TAG, message);
        }
    }

    /**
     * Show Toast<br>
     *
     * @param TAG
     * @param message
     */
    public static void showToastUnderTesting(Context context, String TAG, String message) {
        if (LOG_ENABLE && !TextUtils.isEmpty(message)) {
            Toast toast = Toast.makeText(context, TAG + "\n" + message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * Show Toast
     *
     * @param context
     * @param message
     * @param isLong  TRUE if show long toast else FALSE
     */
    public static void showToastProduction(Context context, String message, boolean isLong) {
        Toast toast = Toast.makeText(context, message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
