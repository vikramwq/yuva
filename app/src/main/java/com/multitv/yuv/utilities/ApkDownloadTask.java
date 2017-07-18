package com.multitv.yuv.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.Toast;

import com.multitv.yuv.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by naseeb on 10/14/2016.
 */

public class ApkDownloadTask extends AsyncTask<Void, Integer, String> {
    private static final String TAG = "ApkDownloadTask";
    private ProgressDialog progressDialog;
    private Context mContext;
    private String url, newVersion, apkStoragePath;
    private String mFinalFileName;
    private String mTempFileName = "Temp.apk";
    private String LOCATION = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MKRANDROID";
    private File apkDownloadedFile;
    private boolean mIsFromPush;

    public ApkDownloadTask(Context mContext, String url, String newVersion, boolean isFromPush) {
        Tracer.error(TAG, "ApkDownloadTask:");
        this.mContext = mContext;
        this.url = url;
        this.newVersion = newVersion;
        mIsFromPush = isFromPush;
        mFinalFileName = AppUtils.getApplicationName(mContext) + "_" + newVersion + ".apk";
        File fileDir = new File(LOCATION);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
    }

    protected void onPreExecute() {
        Tracer.error(TAG, "onPreExecute: ");
        if (mContext instanceof Activity) {
            try {
                progressDialog = new ProgressDialog(mContext);

                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Ubuntu-regular.ttf");
                CustomTFSpan tfSpan = new CustomTFSpan(tf);
                SpannableString spannableString = new SpannableString(mContext.getResources().getString(R.string.app_name));
                spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                progressDialog.setTitle(spannableString);
                progressDialog.setIcon(R.drawable.toolbar_icon);
                progressDialog.setMessage("Downloading apk version " + newVersion + "...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.show();
            } catch (Exception e) {
                Tracer.error(TAG, e.getMessage());
            }
        }
    }

    private String downloadApk() {
        Tracer.error(TAG, "downloadApk: ");
        String result = "";
        try {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            long fileLength = urlConnection.getContentLength();

            FileOutputStream fileOutput = new FileOutputStream(apkDownloadedFile);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[4096];
            int bufferLength;
            long total = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                if (isCancelled()) {
                    inputStream.close();
                    return null;
                }
                total += bufferLength;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));

                fileOutput.write(buffer, 0, bufferLength);
                Tracer.error(TAG, "downloadApk: length " + total);
            }
            fileOutput.close();
            File fileFinalFile = new File(LOCATION, mFinalFileName);
            Tracer.error(TAG, "downloadApk: " + apkDownloadedFile.renameTo(fileFinalFile));
            result = "done";

        } catch (MalformedURLException e) {
            ExceptionUtils.printStacktrace(e);
        } catch (IOException e) {
            ExceptionUtils.printStacktrace(e);
        }
        return result;
    }

    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (mContext instanceof Activity) {
            progressDialog.setProgress(progress[0]);
        }
    }


    protected String doInBackground(Void... params) {
        Tracer.error(TAG, "doInBackground: ");
        apkDownloadedFile = new File(LOCATION, mTempFileName);
        apkStoragePath = apkDownloadedFile.getAbsolutePath();
        File fileFinalFile = new File(LOCATION, mFinalFileName);
        String result;
        if (fileFinalFile.exists()) {
            Tracer.error(TAG, "doInBackground: DELETE FINAL APK " + fileFinalFile.delete());
        }
        if (apkDownloadedFile.exists()) {
            Tracer.error(TAG, "doInBackground: DELETE TEMP " + apkDownloadedFile.delete());
        }
        try {
            apkDownloadedFile.createNewFile();
            Tracer.error(TAG, "doInBackground: CREATE TEMP " + apkDownloadedFile.delete());
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = downloadApk();

        return result;
    }

    protected void onPostExecute(String result) {
        Tracer.error(TAG, "onPostExecute: " + result);
        if (mContext instanceof Activity) {
            if (progressDialog != null && progressDialog.isShowing() && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                progressDialog.dismiss();
            }
        }
        if (result.equals("done")) {
            if (mIsFromPush) {
                try {
                    final File destFile = new File(LOCATION, mFinalFileName);
                    if (destFile.exists()) {
                        Tracer.error(TAG, "onPostExecute: INSTALL " + destFile.getAbsolutePath());
                        if (AppUtils.isAppOnFront(mContext)) {
                            new AsyncTask<Void, Void, Boolean>() {

                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    while (AppUtils.isAppOnFront(mContext)) {
                                        try {
                                            Thread.sleep(30000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            Tracer.error(TAG, "doInBackground: APP RUN IN FRONT CHECK EXCEPTION  " + e.getMessage());
                                        }
                                    }
                                    return true;
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    super.onPostExecute(result);
                                    try {
                                        Process su = Runtime.getRuntime().exec("pm install -r " + destFile.getAbsolutePath());
                                        su.waitFor();
                                    } catch (Exception e) {
                                        installApk();
                                        Tracer.error(TAG, "onPostExecute: INSTALL APK AFTER CHECK WHEN APP IS NOT ON FRONT " + e.getMessage());
                                    }
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            Process su = Runtime.getRuntime().exec("pm install -r " + destFile.getAbsolutePath());
                            su.waitFor();
                        }
                    }
                } catch (Exception e) {
                    Tracer.error(TAG, "onPostExecute: 1 " + e.getMessage());
                    installApk();
                }
            } else {
                installApk();
            }
        } else {
            Toast.makeText(mContext, "Error while downloading. Trying again to download apk version " + newVersion,
                    Toast.LENGTH_LONG).show();

            new ApkDownloadTask(mContext, url, newVersion, mIsFromPush).execute();
        }
    }

    private void installApk() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File fileFinalFile = new File(LOCATION, mFinalFileName);
            Uri uri = Uri.fromFile(fileFinalFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Tracer.error(TAG, "installApk: " + e.getMessage());
        }
    }

}

