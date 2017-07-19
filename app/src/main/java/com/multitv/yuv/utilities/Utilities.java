package com.multitv.yuv.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.multitv.yuv.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;


import com.multitv.yuv.activity.LoginScreen;
import com.multitv.yuv.application.AppController;


/**
 * Created by arungoyal on 24/04/17.
 */

public class Utilities {

    private static boolean waitingForSms = false;
    private static final Integer smsLock = 2;

    public static boolean isWaitingForSms() {
        boolean value = false;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }


    public static int getScreenHeight(Context context) {
        int screenHeight;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        return screenHeight;
    }


    public static boolean writeObjectToFile(Object object, String filename) {

        boolean success = true;

        ObjectOutputStream objectOut = null;
        try {

            FileOutputStream fileOut = AppController.applicationContext.openFileOutput(filename, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();

        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    // do nothing
                }

            }// end of if
        }// End of try/catch/finally block

        return success;
    }


// end of writeObjectToFile method


    public static Object readObjectFromFile(String filename) {

        ObjectInputStream objectIn = null;
        Object object = null;
        try {

            FileInputStream fileIn = AppController.applicationContext.openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }

        return object;
    }


    public static Map<String, String> checkParams(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
        }
    }


    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            AppController.applicationHandler.post(runnable);
        } else {
            AppController.applicationHandler.postDelayed(runnable, delay);
        }
    }


    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.dollywood/";

    public static void applyFontForToolbarTitle(Activity context) {

        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;

                Typeface tf = null;

                tf = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat-Regular.ttf");

                tv.setTypeface(tf);
            }
        }
    }

    public static String readFromFile(Context context, String fileName) {
        String line = null;

        try {
            FileInputStream fileInputStream = AppController.applicationContext.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            Log.d("File", ex.getMessage());
        } catch (IOException ex) {
            Log.d("File", ex.getMessage());
        }
        return line;
    }


    public static boolean writeToFile(String data, String fileName) {
        try {
            String path = AppController.applicationContext.getFilesDir().getAbsolutePath() + fileName;
            File file = new File(path);

            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream fileOutputStream = AppController.applicationContext.openFileOutput(fileName, Activity.MODE_PRIVATE);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            return true;
        } catch (FileNotFoundException ex) {
            Log.e("Utilities File", ex.getMessage());
        } catch (IOException ex) {
            Log.e("Utilities File", ex.getMessage());
        }
        return false;

    }


    public static long toMilliSeconds(int day) {
        return day * 24 * 60 * 60 * 1000;
    }

    public static void saveDatabase() {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + AppController.applicationContext.getPackageName()
                        + "//databases//" + Constant.DATABASE;
                String backupDBPath = "media_android.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public static void showLoginDailog(final Context ctxt) {
        final Dialog dialog = new Dialog(ctxt);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        Button btn_login = (Button) dialog.findViewById(R.id.btn_login);
        ImageView close_btn = (ImageView) dialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(ctxt, LoginScreen.class);
                ctxt.startActivity(intent);

            }
        });


        dialog.show();

    }


    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
