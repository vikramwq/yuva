package com.multitv.yuv.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.cipher.MultitvCipher;
import com.multitv.yuv.R;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.imagecrop.CropImage;
import com.multitv.yuv.imagecrop.CropImageView;
import com.multitv.yuv.imageprocess.ChooserType;
import com.multitv.yuv.imageprocess.FileUtils;
import com.multitv.yuv.imageprocess.ImageChooserManager;
import com.multitv.yuv.imageprocess.ImageProcessorListener;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.models.User;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.ui.CustomEditText;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.CustomTFSpan;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.Json;
import com.multitv.yuv.utilities.PermissionUtility;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.TypefaceSpan;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.multitv.yuv.utilities.Constant.AGEGROUP_KEY;
import static com.multitv.yuv.utilities.Constant.CONTACT_NUMBER;
import static com.multitv.yuv.utilities.Constant.DATE_OF_BIRTH;
import static com.multitv.yuv.utilities.Constant.EMAIL;
import static com.multitv.yuv.utilities.Constant.GENDER;
import static com.multitv.yuv.utilities.Constant.IMAGE_URL;
import static com.multitv.yuv.utilities.Constant.LOCATION;
import static com.multitv.yuv.utilities.Constant.MOBILE_NUMBER_KEY;
import static com.multitv.yuv.utilities.Constant.NAME;
import static com.multitv.yuv.utilities.Constant.foldername;


/**
 * Created by Lenovo on 17-07-2017.
 */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.selectedGenderTextview)
    TextView selectedGenderTextview;
    @BindView(R.id.selectedDateTextview)
    TextView selectedDateTextview;
    @BindView(R.id.genderSelectedImageView)
    ImageView genderSelectedImageView;
    @BindView(R.id.dateListcollospeButton)
    ImageView dateListcollospeButton;
    @BindView(R.id.usernameEditText)
    CustomEditText usernameEditText;
    @BindView(R.id.clearUserNameImageView)
    ImageView clearUserNameImageView;
    @BindView(R.id.emailEditText)
    CustomEditText emailEditText;
    @BindView(R.id.emailClearImageView)
    ImageView emailClearImageView;
    @BindView(R.id.contactNumberGenderEditText)
    CustomEditText contactNumberGenderEditText;
    @BindView(R.id.contactNumberClearImageView)
    ImageView contactNumberClearImageView;
    @BindView(R.id.locationEditText)
    CustomEditText locationEditText;
    @BindView(R.id.locationClearImageView)
    ImageView locationClearImageView;
    @BindView(R.id.interestLinearLayout)
    LinearLayout interestLinearLayout;
    @BindView(R.id.clearInterestImageView)
    ImageView clearInterestImageView;
    @BindView(R.id.fab_edit_profile)
    FloatingActionButton editProfileButton;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.progress_bar_layout)
    LinearLayout progressBarLayout;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int mYear, mMonth, mDay, genderCode;
    private String userChoosenTask, cameraImagePath, encodedImageSendToServerFromLocal;
    private Uri cameraImageUri;
    private SharedPreference sharedPreference = new SharedPreference();
    private Bitmap croppedBitmap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        setContentView(R.layout.user_profile_activity);

        ButterKnife.bind(this);

        genderSelectedImageView.setOnClickListener(this);
        dateListcollospeButton.setOnClickListener(this);
        clearUserNameImageView.setOnClickListener(this);
        emailClearImageView.setOnClickListener(this);
        contactNumberClearImageView.setOnClickListener(this);
        locationClearImageView.setOnClickListener(this);
        clearInterestImageView.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        updateViewFromSharedPreference();
        setDate();
    }

    private void setDate() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH); //Default DOB is (today - 20) years
        //date picker things
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                selectedDateTextview.setText(new StringBuilder().append(mYear)
                        .append("-").append(mMonth + 1).append("-").append(mDay));
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.genderSelectedImageView:
                PopupMenu popup = new PopupMenu(UserProfileActivity.this, genderSelectedImageView);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedGenderTextview.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show();
                break;
            case R.id.dateListcollospeButton:
                new DatePickerDialog(UserProfileActivity.this,
                        mDateSetListener,
                        mYear, mMonth, mDay).show();
                break;
            case R.id.clearUserNameImageView:
                usernameEditText.setText("");
                break;
            case R.id.emailClearImageView:
                emailEditText.setText("");
                break;
            case R.id.contactNumberClearImageView:
                contactNumberGenderEditText.setText("");
                break;
            case R.id.locationClearImageView:
                locationEditText.setText("");
                break;
            case R.id.clearInterestImageView:
                interestLinearLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_edit_profile:
                selectImage();
                break;
            case R.id.saveTextView:
                sendUserInfoToServer();
                break;
        }
    }

    private void updateViewFromSharedPreference() {
        String name = sharedPreference.getUSerName(this, Constant.USERNAME_KEY);
        if (!TextUtils.isEmpty(name))
            usernameEditText.setText(name);

        String email = sharedPreference.getEmailID(this, Constant.EMAIL_KEY);
        if (!TextUtils.isEmpty(email))
            emailEditText.setText(email);

        String gender = sharedPreference.getGender(this, Constant.GENDER_KEY);
        if (!TextUtils.isEmpty(gender)) {
            if (gender.equalsIgnoreCase("1"))
                selectedGenderTextview.setText("Female");
            else if (gender.equalsIgnoreCase("0"))
                selectedGenderTextview.setText("Male");
            else
                selectedGenderTextview.setText(gender);

        }
        selectedGenderTextview.setText(gender);

        String dateOfBirth = sharedPreference.getDob(this, Constant.AGEGROUP_KEY);
        if (!TextUtils.isEmpty(dateOfBirth))
            selectedDateTextview.setText(dateOfBirth);

        String contactNumber = sharedPreference.getPhoneNumber(this, Constant.MOBILE_NUMBER_KEY);
        if (!TextUtils.isEmpty(contactNumber))
            contactNumberGenderEditText.setText(contactNumber);

        String location = sharedPreference.getUserLocation(this, Constant.LOCATION_KEY);
        if (!TextUtils.isEmpty(location))
            locationEditText.setText(location);

        String imageUrl = sharedPreference.getImageUrl(this, Constant.IMAGE_URL_KEY);
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(this).load(imageUrl)
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profileImage);
        }
    }

    private void sendUserInfoToServer() {
        final String name = usernameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            usernameEditText.setError("User name should not be blank");
            usernameEditText.requestFocus();
            return;
        }
        final String email = emailEditText.getText().toString();
        String genderText = selectedGenderTextview.getText().toString();
        if (!TextUtils.isEmpty(genderText)) {
            switch (genderText) {
                case "Male":
                    genderCode = 0;
                case "Female":
                    genderCode = 1;
            }
        }
        final String dateOfBirth = selectedDateTextview.getText().toString();
        final String contactNumber = contactNumberGenderEditText.getText().toString();
        final String location = locationEditText.getText().toString();

        progressBarLayout.setVisibility(View.VISIBLE);

        if (croppedBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImageSendToServerFromLocal = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        }

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                ApiRequest.BASE_URL_VERSION_3+ApiRequest.USER_EDIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error("api_responce---", response);
                progressBarLayout.setVisibility(View.GONE);

                try {
                    JSONObject mObj = new JSONObject(response);

                    if (mObj.optInt("code") == 1) {
                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));

                        Tracer.error("***user_data***", str);

                        User user = Json.parse(str.trim(), User.class);
                        if (user != null) {
                            if (!TextUtils.isEmpty(user.gender)) {
                                try {
                                    sharedPreference.setGender(UserProfileActivity.this, Constant.GENDER_KEY, user.gender);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                            sharedPreference.setPreferencesString(UserProfileActivity.this, IMAGE_URL, user.image);

                            if (!TextUtils.isEmpty(user.location))
                                sharedPreference.setUserLocation(UserProfileActivity.this, Constant.LOCATION_KEY, user.location);

                            sharedPreference.setUserName(UserProfileActivity.this, Constant.USERNAME_KEY, user.first_name);
                            sharedPreference.setPhoneNumber(UserProfileActivity.this, Constant.MOBILE_NUMBER_KEY, user.contact_no);
                            sharedPreference.setDob(UserProfileActivity.this, Constant.AGEGROUP_KEY, user.dob);
                            sharedPreference.setEmailId(UserProfileActivity.this, Constant.EMAIL_KEY, user.email);

                            if (!TextUtils.isEmpty(user.image))
                                sharedPreference.setImageUrl(UserProfileActivity.this, Constant.IMAGE_URL_KEY, user.image);

                            if (!TextUtils.isEmpty(user.age_group))
                                sharedPreference.setDob(UserProfileActivity.this, Constant.AGEGROUP_KEY, user.age_group);
                        }

                        Toast.makeText(UserProfileActivity.this, "Profile successfully saved", Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    progressBarLayout.setVisibility(View.GONE);
                    error.printStackTrace();
                } catch (Exception e) {
                    Tracer.error("EditProfileFragment", e.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
                params.put("id", new SharedPreference().getPreferencesString
                        (UserProfileActivity.this, "user_id" + "_" + ApiRequest.TOKEN));
                params.put("first_name", name);
                params.put("gender", "" + genderCode);
                if (!TextUtils.isEmpty(email))
                    params.put("email", email);
                if (!TextUtils.isEmpty(dateOfBirth))
                    params.put("dob", dateOfBirth);
                if (!TextUtils.isEmpty(contactNumber))
                    params.put("contact_no", contactNumber);
                if (!TextUtils.isEmpty(location))
                    params.put("location", location);
                if (!TextUtils.isEmpty(encodedImageSendToServerFromLocal)) {
                    params.put("pic", encodedImageSendToServerFromLocal);
                    params.put("ext", "jpg");
                }

                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileActivity.this);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialogBuilder.setTitle(spannableString);
        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtility.checkPermission(UserProfileActivity.this);
                if (!result)
                    return;
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog selectImageAlertDialog = alertDialogBuilder.create();
        selectImageAlertDialog.setCancelable(true);
        selectImageAlertDialog.setCanceledOnTouchOutside(false);
        selectImageAlertDialog.show();
    }

    //==============selectImage from gallery===================
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), ChooserType.REQUEST_PICK_PICTURE);

    }


    //==============selectImage from camera===================
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output = new File(dir, System.currentTimeMillis() + ".jpg");*/
        String output = FileUtils.getDirectory(foldername) + File.separator
                + Calendar.getInstance().getTimeInMillis() + ".jpg";
        File fileOutput = new File(output);
        //cameraImageUri = Uri.fromFile(fileOutput);
        cameraImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", fileOutput);
        cameraImagePath = "file:" + fileOutput.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        //startActivityForResult(intent, REQUEST_CAMERA);
        startActivityForResult(intent, ChooserType.REQUEST_CAPTURE_PICTURE);
    }

    //==============premission dialog===================
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    //============on activity result=====================
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (((requestCode == ChooserType.REQUEST_PICK_PICTURE) || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE) && resultCode == Activity.RESULT_OK) {
            cropImage(data, requestCode);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    croppedBitmap = MediaStore.Images.Media.getBitmap(UserProfileActivity.this.getContentResolver(), resultUri);
                    //Bitmap croppedBitmap = result.getBitmap();
                    profileImage.setImageBitmap(croppedBitmap);
                    /*profile_buller.setImageBitmap(croppedBitmap);*/
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Blurry.with(UserProfileActivity.this)
                                    .radius(25)
                                    .sampling(2)
                                    .async()
                                    .capture(findViewById(profile_buller))
                                    .into((ImageView) findViewById(profile_buller));
                        }
                    }, SPLASH_DISPLAY_LENGTH);*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*if (croppedBitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] byteArrayImage = baos.toByteArray();
                    encodedImageSendToServerFromLocal = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    userEditProfile(encodedImageSendToServerFromLocal);
                }*/
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

           /* if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }*/
    }

    private void cropImage(Intent data, int requestCode) {
        ImageProcessorListener imageProcessorListener = new ImageProcessorListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String error) {

            }
        };

        GetImagePath getImagePath = new GetImagePath(requestCode, imageProcessorListener, data);
        getImagePath.execute();

    }

    private class GetImagePath extends AsyncTask<Void, Void, Uri> {
        private int requestCode;
        private ImageProcessorListener mImageProcessorListener;
        private Intent data;
        private Uri imageUri;

        GetImagePath(int reqCode, ImageProcessorListener imageProcessorListener, Intent intent) {
            this.requestCode = reqCode;
            this.mImageProcessorListener = imageProcessorListener;
            data = intent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pbProfilePic.setVisibility(View.VISIBLE);
        }

        @Override
        protected Uri doInBackground(Void... voids) {

            if (requestCode == ChooserType.REQUEST_CAPTURE_PICTURE) {
                if (!TextUtils.isEmpty(cameraImagePath))
                    cameraImageUri = Uri.parse(cameraImagePath);
            }

            ImageChooserManager imageChooserManager = new ImageChooserManager(UserProfileActivity.this, mImageProcessorListener, cameraImageUri);
            imageUri = imageChooserManager.getImageFilePath(requestCode, data);

            return imageUri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            //pbProfilePic.setVisibility(View.GONE);
            if (uri != null) {
                startCropImageActivity(uri);
            }
        }

    }

    private void startCropImageActivity(Uri imageUri) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        SpannableString string = new SpannableString("Profile Image");
        string.setSpan(new TypefaceSpan(tf), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowRotation(false)
                .setFixAspectRatio(true)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMultiTouchEnabled(false)
                .setActivityTitle(String.valueOf(string))
                .setAutoZoomEnabled(false)
                .setInitialCropWindowPaddingRatio(0)
                .start(this);
    }
}
