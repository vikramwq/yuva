/**
 * ****************************************************************************
 * Copyright 2013 Kumar Bibek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.multitv.yuv.imageprocess;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import com.multitv.yuv.BuildConfig;
import com.multitv.yuv.utilities.Tracer;

import static com.multitv.yuv.utilities.Constant.foldername;

/**
 * Easy Image Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 *
 * @author Beanie
 */
public class ImageChooserManager {

    private final static String TAG = ImageChooserManager.class.getSimpleName();

    private Context mContext;
    private String filePathOriginal;
    private ImageProcessorListener mImageProcessorListener;
    private String mediaExtension = ".jpg";
    Uri imageUri;
    //String cameraImagePath = "";

    public ImageChooserManager(Context context, ImageProcessorListener imageProcessorListener, Uri uri) {
        mContext = context;
        mImageProcessorListener = imageProcessorListener;
        imageUri = uri;
        //this.cameraImagePath = cameraImagePath;
    }


    /*public void setMediaExtension(String extension) {
        this.mediaExtension = extension;
    }*/

    public Uri getImageFilePath(int requestCode, Intent data) {
        String imagePath = "";
        try {
            switch (requestCode) {
                case ChooserType.REQUEST_PICK_PICTURE:
                    imagePath = processImageFromGallery(data);
                    imagePath = compressAndSaveImage(imagePath, 1);
                    //imageUri = Uri.fromFile(new File(imagePath));
                    break;
                case ChooserType.REQUEST_CAPTURE_PICTURE:
                    //imageUri = processCameraImage();
                    /*Uri imageUri = Uri.parse(cameraImagePath);
                    imagePath = imageUri.getPath();*/
                    //if (!TextUtils.isEmpty(cameraImagePath)) {
                    //imageUri = Uri.parse(cameraImagePath);
                    imagePath = imageUri.getPath();
                    imagePath = compressAndSaveImage(imagePath, 1);
                    //}
                    //imageUri = Uri.fromFile(new File(imagePath));
                    break;
            }
        } catch (Exception e) {
            mImageProcessorListener.onError(e.getMessage());
        }

        if (imagePath == null)
            return null;
        File file = new File(imagePath);
        if (file.length() / 1024 > 50) {
            try {
                imagePath = compressAndSaveImage(imagePath, 1);
            } catch (ChooserException e) {
                e.printStackTrace();
            }
        }
        imageUri = Uri.fromFile(new File(imagePath));
        return imageUri;
    }

    @SuppressLint("NewApi")
    private String processImageFromGallery(Intent data) {
        if (data != null && data.getDataString() != null) {
            String uri = data.getData().toString();
            sanitizeURI(uri);

            if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
                mImageProcessorListener.onError("File path was null");
                return "";
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "File: " + filePathOriginal);
                }
                String path = filePathOriginal;
                // Picasa on Android >= 3.0
                if (path != null && path.startsWith("content:")) {
                    filePathOriginal = getAbsoluteImagePathFromUri(Uri.parse(path));
                }
                if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
                    if (mImageProcessorListener != null) {
                        mImageProcessorListener.onError("Couldn't process a null file");
                    }
                    Tracer.debug("PROFILE_PIC", "FILE PATH is NULL");
                    return "";
                } else if (filePathOriginal.startsWith("http")) {
                    try {
                        filePathOriginal = downloadAndProcessNew(filePathOriginal);
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }
                } else if (filePathOriginal
                        .startsWith("content://com.google.android.gallery3d")
                        || filePathOriginal
                        .startsWith("content://com.microsoft.skydrive.content")) {
                    try {
                        filePathOriginal = processPicasaMediaNew(filePathOriginal, ".jpg");
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }
                } else if (filePathOriginal
                        .startsWith("content://com.google.android.apps.photos.content")
                        || filePathOriginal
                        .startsWith("content://com.android.providers.media.documents")
                        || filePathOriginal
                        .startsWith("content://com.google.android.apps.docs.storage")
                        || filePathOriginal
                        .startsWith("content://com.android.externalstorage.documents")
                        || filePathOriginal
                        .startsWith("content://com.android.internalstorage.documents") ||
                        filePathOriginal.startsWith("content://")) {
                    try {
                        filePathOriginal = processGooglePhotosMediaNew(filePathOriginal, ".jpg");
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filePathOriginal;
    }

    @SuppressLint("NewApi")
    protected String getAbsoluteImagePathFromUri(Uri imageUri) {
        String[] proj = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Image Uri: " + imageUri.toString());
        }

        if (imageUri.toString().startsWith(
                "content://com.android.gallery3d.provider")) {
            imageUri = Uri.parse(imageUri.toString().replace(
                    "com.android.gallery3d", "com.google.android.gallery3d"));
        }

        String filePath = "";
        String imageUriString = imageUri.toString();
        if (imageUriString.startsWith("content://com.google.android.gallery3d")
                || imageUriString
                .startsWith("content://com.google.android.apps.photos.content")
                || imageUriString
                .startsWith("content://com.android.providers.media.documents")
                || imageUriString
                .startsWith("content://com.google.android.apps.docs.storage")
                || imageUriString
                .startsWith("content://com.microsoft.skydrive.content.external")
                || imageUriString
                .startsWith("content://com.android.externalstorage.documents")
                || imageUriString
                .startsWith("content://com.android.internalstorage.documents") ||
                imageUriString.startsWith("content://")) {
            filePath = imageUri.toString();
        } else {
            Cursor cursor = mContext.getContentResolver().query(imageUri, proj,
                    null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            cursor.close();
        }

        if (filePath == null && isDownloadsDocument(imageUri)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                filePath = getPath(imageUri);
        }
        return filePath;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(mContext, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*private Void processCameraImage() {
        String path = filePathOriginal;
        ImageProcessorThread thread = new ImageProcessorThread(path,
                foldername, shouldCreateThumbnails);
        thread.setListener(this);
        thread.start();
    }*/

    protected void checkDirectory() throws ChooserException {
        File directory;
        directory = new File(FileUtils.getDirectory(foldername));
        if (!directory.exists()) {
            if (!directory.mkdirs() && !directory.isDirectory()) {
                throw new ChooserException("Error creating directory: " + directory);
            }
        }
    }

    // Change the URI only when the returned string contains "file:/" prefix.
    // For all the other situations the URI doesn't need to be changed
    protected void sanitizeURI(String uri) {
        filePathOriginal = uri;
        // Picasa on Android < 3.0
        if (uri.matches("https?://\\w+\\.googleusercontent\\.com/.+")) {
            filePathOriginal = uri;
        }
        // Local storage
        if (uri.startsWith("file://")) {
            filePathOriginal = uri.substring(7);
        }
    }

    protected String downloadAndProcessNew(String url) throws ChooserException {
        String downloadedFilePath = downloadFile(url);
        return downloadedFilePath;
    }

    protected String downloadFile(String url) {
        String localFilePath = "";
        try {
            URL u = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedInputStream bStream = new BufferedInputStream(stream);

            localFilePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + "."
                    + mediaExtension;
            File localFile = new File(localFilePath);

            FileOutputStream fileOutputStream = new FileOutputStream(localFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = bStream.read(buffer)) > 0)
                fileOutputStream.write(buffer, 0, len);
            fileOutputStream.flush();
            fileOutputStream.close();
            bStream.close();

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Image saved: " + localFilePath.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localFilePath;
    }

    protected String processPicasaMediaNew(String path, String extension) throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Started");
        }
        String outFile;
        //ChosenImage image;

        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;

        try {
            InputStream inputStream = mContext.getContentResolver()
                    .openInputStream(Uri.parse(path));

            bStream = new BufferedInputStream(inputStream);

            StreamHelper.verifyStream(path, bStream);

            outFile = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;

            outStream = new BufferedOutputStream(new FileOutputStream(outFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            StreamHelper.flush(outStream);
            //image = processImage(path);
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            StreamHelper.close(bStream);
            StreamHelper.close(outStream);
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }

        return outFile;
    }

    protected String processGooglePhotosMediaNew(String path, String extension)
            throws ChooserException {
        String outFile;
        //ChosenImage image;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Google photos Started");
            Log.i(TAG, "URI: " + path);
            Log.i(TAG, "Extension: " + extension);
        }
        String retrievedExtension = checkExtension(Uri.parse(path));
        if (retrievedExtension != null
                && !TextUtils.isEmpty(retrievedExtension)) {
            extension = "." + retrievedExtension;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outStream = null;
        ParcelFileDescriptor parcelFileDescriptor = null;

        try {

            outFile = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;
            parcelFileDescriptor = mContext
                    .getContentResolver().openFileDescriptor(Uri.parse(path),
                            "r");

            StreamHelper.verifyStream(path, parcelFileDescriptor);

            FileDescriptor fileDescriptor = parcelFileDescriptor
                    .getFileDescriptor();

            inputStream = new BufferedInputStream(new FileInputStream(fileDescriptor));

            BufferedInputStream reader = new BufferedInputStream(inputStream);

            outStream = new BufferedOutputStream(
                    new FileOutputStream(outFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            StreamHelper.flush(outStream);
            //image = processImage(outFile);
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            StreamHelper.flush(outStream);
            StreamHelper.close(outStream);
            StreamHelper.close(inputStream);
            StreamHelper.close(parcelFileDescriptor);
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }

        return outFile;
    }

    public String checkExtension(Uri uri) {

        String extension = "";

        // The query, since it only applies to a single document, will only
        // return
        // one row. There's no need to filter, sort, or select fields, since we
        // want
        // all fields for one document.
        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
                null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy
            // for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file
                // name.
                String displayName = cursor.getString(cursor
                        .getColumnIndex(OpenableColumns.DISPLAY_NAME));
                int position = displayName.indexOf(".");
                extension = displayName.substring(position + 1);
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But since
                // an
                // int can't be null in Java, the behavior is
                // implementation-specific,
                // which is just a fancy term for "unpredictable". So as
                // a rule, check if it's null before assigning to an int. This
                // will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but
                    // cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return extension;
    }

    private String getRealPathFromURI(Uri contentURI) {
        //Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(index);
            cursor.close();
            return path;
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /*public String getFilePathAndCompressImage(Uri imageUri, String filePath) {

        String filePath = getRealPathFromURI(imageUri);
        if (filePath == null)
            return "";
        File file = new File(filePath);
        if (file.length() / 1024 > 40) {
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;
        } else {
            return filePath;
        }
    }*/

    public String getFilename() {

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //File output = new File(dir, System.currentTimeMillis() + ".jpg");
        String uriSting = (dir + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String compressAndSaveImage(String fileImage, int scale) throws ChooserException {

        FileOutputStream stream = null;
        BufferedInputStream bstream = null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options optionsForGettingDimensions = new BitmapFactory.Options();
            optionsForGettingDimensions.inJustDecodeBounds = true;
            BufferedInputStream boundsOnlyStream = new BufferedInputStream(new FileInputStream(fileImage));
            bitmap = BitmapFactory.decodeStream(boundsOnlyStream, null, optionsForGettingDimensions);
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (boundsOnlyStream != null) {
                boundsOnlyStream.close();
            }
            int w, l;
            w = optionsForGettingDimensions.outWidth;
            l = optionsForGettingDimensions.outHeight;

            ExifInterface exif = new ExifInterface(fileImage);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            int what = w > l ? w : l;

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (what > 3000) {
                options.inSampleSize = scale * 6;
            } else if (what > 2000 && what <= 3000) {
                options.inSampleSize = scale * 5;
            } else if (what > 1500 && what <= 2000) {
                options.inSampleSize = scale * 4;
            } else if (what > 1000 && what <= 1500) {
                options.inSampleSize = scale * 3;
            } else if (what > 400 && what <= 1000) {
                options.inSampleSize = scale * 2;
            } else {
                options.inSampleSize = scale;
            }

            options.inJustDecodeBounds = false;
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Scale: " + (what / options.inSampleSize));
                Log.i(TAG, "Rotate: " + rotate);
            }
            // TODO: Sometime the decode File Returns null for some images
            // For such cases, thumbnails can't be created.
            // Thumbnails will link to the original file
            BufferedInputStream scaledInputStream = new BufferedInputStream(new FileInputStream(fileImage));
            bitmap = BitmapFactory.decodeStream(scaledInputStream, null, options);
//            verifyBitmap(fileImage, bitmap);
            scaledInputStream.close();
            if (bitmap == null) {
                return fileImage;
            }
            File original = new File(fileImage);
            File file = new File(
                    (original.getParent() + File.separator + original.getName()
                            .replace(".", "_fact_" + scale + ".")));
            stream = new FileOutputStream(file);
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, false);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            return file.getAbsolutePath();

        } catch (IOException e) {
            return fileImage;
//            throw new ChooserException(e);
        } catch (Exception e) {
            return fileImage;
        } finally {
            StreamHelper.close(bstream);
            StreamHelper.flush(stream);
            StreamHelper.close(stream);
        }
    }

}
