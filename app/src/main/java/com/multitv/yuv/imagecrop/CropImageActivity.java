// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.multitv.yuv.imagecrop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


import com.multitv.yuv.R;
import com.multitv.yuv.utilities.CustomTypefaceSpan;


public class CropImageActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {

    /**
     * The crop image view library widget used in the activity
     */
    private CropImageView mCropImageView;

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private Uri mCropImageUri;

    /**
     * the options that were set for the crop image
     */
    private CropImageOptions mOptions;
    private TextView crop_btn;

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activity);

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        crop_btn = (TextView) findViewById(R.id.crop_btn);
        Intent intent = getIntent();
        mCropImageUri = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_SOURCE);
        mOptions = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_OPTIONS);

        if (savedInstanceState == null) {
            if (mCropImageUri == null || mCropImageUri.equals(Uri.EMPTY)) {
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(this);
                }
            } else if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                mCropImageView.setImageUriAsync(mCropImageUri);
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        if (actionBar != null) {
            String title = mOptions.activityTitle != null && !mOptions.activityTitle.isEmpty()
                    ? mOptions.activityTitle
                    : getResources().getString(R.string.crop_image_activity_title);


            SpannableString mNewTitle = new SpannableString(title);
            mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(mNewTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/app_header.ttf");
        try {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                Tracer.error("vikram", "onCreate: " + toolbar.getChildAt(i));
                if (toolbar.getChildAt(i) instanceof TextView) {
                    ((TextView) toolbar.getChildAt(i)).setTypeface(tf);
                }
            }

        } catch (Exception e) {
            Tracer.error("vikram", "onCreate: " + e.getMessage());
        }
        getSupportActionBar().setTitle();*/
        crop_btn.setTypeface(font);
        crop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnCropImageCompleteListener(null);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;


            default:
                return false;
        }

    }


    // @Override
 /*   public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crop_image_menu, menu);

*//*
        if (!mOptions.allowRotation) {
            menu.removeItem(R.id.crop_image_menu_rotate_left);
            menu.removeItem(R.id.crop_image_menu_rotate_right);
        } else if (mOptions.allowCounterRotation) {
            menu.findItem(R.id.crop_image_menu_rotate_left).setVisible(true);
        }*//*
       *//* MenuItem menuItem = menu.findItem(R.id.crop_image_menu_crop);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/app_header.ttf");
        SpannableString mNewTitle = new SpannableString("CROP");
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle);*//*
        *//*Drawable cropIcon = null;
        try {
            cropIcon = ContextCompat.getDrawable(this, R.drawable.crop_image_menu_crop);
            if (cropIcon != null) {
                menu.findItem(R.id.crop_image_menu_crop).setIcon(cropIcon);
            }
        } catch (Exception e) {
        }

        if (mOptions.activityMenuIconColor != 0) {
            updateMenuItemIconColor(menu, R.id.crop_image_menu_rotate_left, mOptions.activityMenuIconColor);
            updateMenuItemIconColor(menu, R.id.crop_image_menu_rotate_right, mOptions.activityMenuIconColor);
            if (cropIcon != null) {
                updateMenuItemIconColor(menu, R.id.crop_image_menu_crop, mOptions.activityMenuIconColor);
            }
        }*//*

            getLayoutInflater().setFactory(new LayoutInflater.Factory() {
                public View onCreateView(String name, Context context,
                                         AttributeSet attrs) {

                    if (name.equalsIgnoreCase(
                            "com.android.internal.view.menu.IconMenuItemView")) {
                        try {
                            LayoutInflater li = LayoutInflater.from(context);
                            final View view = li.createView(name, null, attrs);
                            new Handler().post(new Runnable() {
                                public void run() {
                                    // set the background drawable if you want that
                                    //or keep it default -- either an image, border
                                    //gradient, drawable, etc.
                                    //view.setBackgroundResource(R.drawable.myimage);
                                    ((TextView) view).setTextSize(20);

                                    // set the text color
                                    Typeface face = Typeface.createFromAsset(
                                            getAssets(),"fonts/app_header.ttf");
                                    ((TextView) view).setTypeface(face);
                                    ((TextView) view).setTextColor(Color.WHITE);
                                }
                            });
                            return view;
                        } catch (InflateException e) {
                            //Handle any inflation exception here
                        } catch (ClassNotFoundException e) {
                            //Handle any ClassNotFoundException here
                        }
                    }
                    return null;
                }
            });
            return true;
    }*/

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.crop_image_menu_crop) {
            cropImage();
            return true;
        }
        if (item.getItemId() == R.id.crop_image_menu_rotate_left) {
            rotateImage(-mOptions.rotationDegrees);
            return true;
        }
        if (item.getItemId() == R.id.crop_image_menu_rotate_right) {
            rotateImage(mOptions.rotationDegrees);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            setResultCancel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancel();
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //User cancelled the picker. We don't have anything to crop
                setResultCancel();
            }

            if (resultCode == Activity.RESULT_OK) {
                mCropImageUri = CropImage.getPickImageResultUri(this, data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    mCropImageView.setImageUriAsync(mCropImageUri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                mCropImageView.setImageUriAsync(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                setResultCancel();
            }
        }

        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            //Irrespective of whether camera permission was given or not, we show the picker
            //The picker will not add the camera intent if permission is not available
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (mOptions.initialCropWindowRectangle != null) {
                mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
            }
            if (mOptions.initialRotation > -1) {
                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            }
        } else {
            setResult(null, error, 1);
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        setResult(result.getUri(), result.getError(), result.getSampleSize());
    }

    //region: Private methods

    /**
     * Execute crop image and save the result tou output uri.
     */
    protected void cropImage() {
        if (mOptions.noOutputImage) {
            setResult(null, null, 1);
        } else {
            Uri outputUri = getOutputUri();
            mCropImageView.saveCroppedImageAsync(outputUri,
                    mOptions.outputCompressFormat,
                    mOptions.outputCompressQuality,
                    mOptions.outputRequestWidth,
                    mOptions.outputRequestHeight,
                    mOptions.outputRequestSizeOptions);
        }
    }

    /**
     * Rotate the image in the crop image view.
     */
    protected void rotateImage(int degrees) {
        mCropImageView.rotateImage(degrees);
    }

    /**
     * Get Android uri to save the cropped image into.<br>
     * Use the given in options or create a temp file.
     */
    protected Uri getOutputUri() {
        Uri outputUri = mOptions.outputUri;
        if (outputUri.equals(Uri.EMPTY)) {
            try {
                String ext = mOptions.outputCompressFormat == Bitmap.CompressFormat.JPEG ? ".jpg" :
                        mOptions.outputCompressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".webp";
                outputUri = Uri.fromFile(File.createTempFile("cropped", ext, getCacheDir()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temp file for output image", e);
            }
        }
        return outputUri;
    }

    /**
     * Result with cropped image data or error if failed.
     */
    protected void setResult(Uri uri, Exception error, int sampleSize) {
        int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
        setResult(resultCode, getResultIntent(uri, error, sampleSize));
        finish();
    }

    /**
     * Cancel of cropping activity.
     */
    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Get intent instance to be used for the result of this activity.
     */
    protected Intent getResultIntent(Uri uri, Exception error, int sampleSize) {
        CropImage.ActivityResult result = new CropImage.ActivityResult(null,
                uri,
                error,
                mCropImageView.getCropPoints(),
                mCropImageView.getCropRect(),
                mCropImageView.getRotatedDegrees(),
                sampleSize);
        Intent intent = new Intent();
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result);
        return intent;
    }

    /**
     * Update the color of a specific menu item to the given color.
     */
    private void updateMenuItemIconColor(Menu menu, int itemId, int color) {
        MenuItem menuItem = menu.findItem(itemId);
        if (menuItem != null) {
            Drawable menuItemIcon = menuItem.getIcon();
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.mutate();
                    menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    menuItem.setIcon(menuItemIcon);
                } catch (Exception e) {
                }
            }
        }
    }
    //endregion
}

