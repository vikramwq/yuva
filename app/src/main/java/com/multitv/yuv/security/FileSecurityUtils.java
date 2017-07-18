package com.multitv.yuv.security;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.multitv.yuv.application.AppController;
import com.multitv.yuv.download.queue.DownloadTask;
import com.multitv.yuv.utilities.Constant;

/**
 * Created by naseeb on 5/22/2017.
 */

public class FileSecurityUtils {
    public static void downloadAndEncryptFile(final Activity activity, final String fileName, final String downloadUrl,
                                              final FileEncryptionListener fileEncryptionListener) {
        if (activity == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(downloadUrl)) {
            if (fileEncryptionListener == null)
                throw new NullPointerException();
            else {
                fileEncryptionListener.onFileEncryptionError();
            }

            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(downloadUrl);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();

                    File file = new File(Environment.getExternalStorageDirectory()
                            + File.separator, fileName + "_downloaded.mp4");
                    if (file.exists())
                        file.delete();

                    file.createNewFile();

                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[8192];
                    int bufferLength;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }
                    inputStream.close();
                    fileOutputStream.close();

                    ////-------------------------------------------------------------------------------------------
                    final File encyptedFile = new File(Environment.getExternalStorageDirectory()
                            + File.separator, fileName + "_encrypted.mp4");
                    if (encyptedFile.exists())
                        encyptedFile.delete();

                    encyptedFile.createNewFile();

                    inputStream = new FileInputStream(file.getAbsolutePath());

                    fileOutputStream = new FileOutputStream(encyptedFile);

                    buffer = new byte[8192];

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        buffer = SimpleCrypto.encrypt(SimpleCrypto.getKey(), SimpleCrypto.getIv(), buffer);
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }

                    inputStream.close();
                    fileOutputStream.close();

                    if (file != null)
                        file.delete();

                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fileEncryptionListener.onFileEncrypted(encyptedFile.getAbsolutePath());
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fileEncryptionListener.onFileEncryptionError();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public static void encryptFile(final Context context, final String title, final String fileName, final String downloadedFilePath,
                                   final FileEncryptionListener fileEncryptionListener) {
        final File downloadedFile = new File(downloadedFilePath);
        if (context == null || TextUtils.isEmpty(fileName) || !downloadedFile.exists()) {
            if (fileEncryptionListener == null)
                throw new NullPointerException();
            else {
                fileEncryptionListener.onFileEncryptionError();
            }

            return;
        }

        //Notifying user that encryption of video is going on
        String titleText = !TextUtils.isEmpty(title) ? title : "media";
        Toast.makeText(AppController.applicationContext, "Saving " + titleText, Toast.LENGTH_LONG).show();
        DownloadTask.generateNotification("Saving " + titleText, title);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File encryptedFile = new File(Environment.getExternalStorageDirectory()
                            + File.separator + Constant.DIRECTORY_NAME, fileName + "_encrypted.mp4");
                    if (encryptedFile.exists())
                        encryptedFile.delete();

                    encryptedFile.createNewFile();

                    InputStream inputStream = new FileInputStream(downloadedFile.getAbsolutePath());

                    FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile);

                    int bufferLength;
                    byte[] buffer = new byte[8192];

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        buffer = SimpleCrypto.encrypt(SimpleCrypto.getKey(), SimpleCrypto.getIv(), buffer);
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }

                    inputStream.close();
                    fileOutputStream.close();

                    if (downloadedFile != null)
                        downloadedFile.delete();

                    fileEncryptionListener.onFileEncrypted(encryptedFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();

                    fileEncryptionListener.onFileEncryptionError();
                }

            }
        }).start();
    }


    public static void decryptFile(final Context context, final String fileName, final String encryptedFilePath,
                                   final FileDecryptionListener fileDecryptionListener) {
        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(encryptedFilePath)) {
            if (fileDecryptionListener == null)
                throw new NullPointerException();
            else
                fileDecryptionListener.onFileDecryptionError();

            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File downloadsFolder = new File(context.getFilesDir(), "downloads");
                    if (!downloadsFolder.exists())
                        downloadsFolder.mkdirs();

                    final File decryptedFile = new File(downloadsFolder, fileName + "_decrypted.mp4");
                    if (decryptedFile.exists())
                        decryptedFile.delete();

                    decryptedFile.createNewFile();

                    FileInputStream inputStream = new FileInputStream(encryptedFilePath);

                    FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);

                    byte[] buffer = new byte[8192];
                    int bufferLength;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        buffer = SimpleCrypto.decrypt(SimpleCrypto.getKey(), SimpleCrypto.getIv(), buffer);
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }

                    inputStream.close();
                    fileOutputStream.close();

                    fileDecryptionListener.onFileDecrypted(decryptedFile.getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();

                    fileDecryptionListener.onFileDecryptionError();
                }
            }
        }).start();
    }

    public static void onPlayerEnded(final String decryptedFilePath) {
        if (!TextUtils.isEmpty(decryptedFilePath)) {
            File file = new File(decryptedFilePath);
            if (file.exists())
                file.delete();
        }
    }

    public interface FileEncryptionListener {
        void onFileEncrypted(String filePath);

        void onFileEncryptionError();
    }

    public interface FileDecryptionListener {
        void onFileDecrypted(String filePath);

        void onFileDecryptionError();
    }
}
