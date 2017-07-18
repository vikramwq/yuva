package com.multitv.yuv.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.multitv.yuv.R;
import com.multitv.yuv.imagecrop.CropImage;
import com.multitv.yuv.imageprocess.FileUtils;
import com.multitv.yuv.utilities.TypefaceSpan;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;
import com.multitv.yuv.adapter.UserProfilePagerAdapter;
import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.application.AppController;
import com.multitv.yuv.imagecrop.CropImageView;
import com.multitv.yuv.imageprocess.ChooserType;
import com.multitv.yuv.imageprocess.ImageChooserManager;
import com.multitv.yuv.imageprocess.ImageProcessorListener;
import com.multitv.yuv.interfaces.ImageSenderInterface;
import com.multitv.yuv.locale.LocaleHelper;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.CustomTFSpan;
import com.multitv.yuv.utilities.ExceptionUtils;
import com.multitv.yuv.utilities.MultitvCipher;
import com.multitv.yuv.utilities.NotificationCenter;
import com.multitv.yuv.utilities.PermissionUtility;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

//import static Constant.HTMLTEXT;
import static com.multitv.yuv.utilities.Constant.foldername;

/**
 * Created by root on 22/11/16.
 */

public class UserProfileActivity extends AppCompatActivity implements ImageSenderInterface {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private FloatingActionButton fab_edit_profile;
    private ImageView editProfileImage;
    private final int SPLASH_DISPLAY_LENGTH = 100;
    private Bitmap bitmap;
    private TabLayout tabLayout;
    private SharedPreference sharedPreference;
    private TextView email;
    private TextView phoneNumberTxt;
    private TextView name;
    private CircleImageView profile_image;
    private ImageView profile_buller;
    private LinearLayout transparent_img_bg;
    private ListPopupWindow popupWindowLangauge;
    private UserProfilePagerAdapter adapter;
    //private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask, encodedImageSendToServerFromLocal, user_id, header, setting;
    private Bitmap thumbnail;
    private AlertDialog selectImageAlertDialog;
    private ImageSenderInterface imageSenderInterface;
    private Uri cameraImageUri;
    private ProgressBar pbProfilePic;
    String cameraImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.user_profile_activity);
        sharedPreference = new SharedPreference();
        user_id = new SharedPreference().getPreferencesString(UserProfileActivity.this, "user_id" + "_" + ApiRequest.TOKEN);
        //=============find view id====================
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        editProfileImage = (ImageView) findViewById(R.id.editProfileUser);
        email = (TextView) findViewById(R.id.emailTxt);
        phoneNumberTxt = (TextView) findViewById(R.id.phoneNumberTxt);
        name = (TextView) findViewById(R.id.title_name);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_buller = (ImageView) findViewById(R.id.profile_buller);
        transparent_img_bg = (LinearLayout) findViewById(R.id.transparent_img_bg);
        pbProfilePic = (ProgressBar) findViewById(R.id.profile_pic_pb);




        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(5);
            }
        });

        //=============get value from share prefernces====================
        sharedPreference = new SharedPreference();
        setupToolbar();
        setupCollapsingToolbar();
        setupViewPager();
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        updateDetails();
        changeImage();

        Intent intent = getIntent();
        header = intent.getStringExtra("ClickType");
        if (!TextUtils.isEmpty(header) && header.equalsIgnoreCase("1")) {
            viewPager.setCurrentItem(3);
        }
        if (!TextUtils.isEmpty(header) && header.equalsIgnoreCase("2")) {
            viewPager.setCurrentItem(0);
        }
        if (!TextUtils.isEmpty(header) && header.equalsIgnoreCase("3")) {
            viewPager.setCurrentItem(1);
        }
        // receiveDataFromOtherApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String selectedLanguage = AppUtils.getLanguageNameOrignal(AppUtils.getLanguageName(AppUtils.getLanguageName(Locale.getDefault().getLanguage())));
        PreferenceData.setAppLanguage(getApplicationContext(), selectedLanguage);
    }



    //==========setup view Collapsing toolbar================
    private void setupCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.collapsing_toolbar);

        collapsingToolbar.setTitleEnabled(false);
    }

    //==========setup view pager with tool bar================
    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(tf);
                }
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Tracer.debug("position", String.valueOf(position));

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    //==========setup adapter on view pager================
    private void setupViewPager(ViewPager viewPager) {
        String[] stringArray = getResources().getStringArray(R.array.title);
        adapter = new UserProfilePagerAdapter(getSupportFragmentManager(), UserProfileActivity.this, stringArray, stringArray.length);
        viewPager.setAdapter(adapter);
    }

    //==========setup tool bar================
    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User Profile");
        setSupportActionBar(toolbar);
        Utilities.applyFontForToolbarTitle(UserProfileActivity.this);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreference != null)
//                    PreferenceData.setPreviousFragmentSelectedPosition(getApplicationContext(), 0);
//                Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
//                intent.putExtra("home_activity", "profile_activity");
//                intent.putExtra(EXTRA_OPEN_HOME_SCREEN, true);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                UserProfileActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (selectImageAlertDialog != null && selectImageAlertDialog.isShowing()) {
            selectImageAlertDialog.dismiss();
            return;
        }

//        PreferenceData.setPreviousFragmentSelectedPosition(getApplicationContext(), 0);
//        Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
//        intent.putExtra(EXTRA_OPEN_HOME_SCREEN, true);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        UserProfileActivity.this.finish();
        super.onBackPressed();
    }

    private void changeImage() {
        String ImageUrl = sharedPreference.getImageUrl(UserProfileActivity.this, "imgUrl");
        if (ImageUrl != null && !ImageUrl.equals("") && !ImageUrl.isEmpty()) {
            sharedPreference.setImageUrl(UserProfileActivity.this, "imgUrl", ImageUrl);
            Picasso
                    .with(UserProfileActivity.this)
                    .load(ImageUrl.replace("\\",""))
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_image);

            Picasso
                    .with(UserProfileActivity.this)
                    .load(ImageUrl.replace("\\",""))
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_buller);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Blurry.with(UserProfileActivity.this)
                            .radius(25)
                            .sampling(2)
                            .async()
                            .capture(findViewById(R.id.profile_buller))
                            .into((ImageView) findViewById(R.id.profile_buller));
                }
            }, SPLASH_DISPLAY_LENGTH);

        } else {
            // transparent_img_bg.setVisibility(View.VISIBLE);
            Picasso.with(UserProfileActivity.this)
                    .load(R.mipmap.intex_profile)
                    .into(profile_image);

         /*  Picasso.with(UserProfileActivity.this)
                    .load(R.mipmap.intex_profile)
                    .into(profile_buller);*/
            profile_buller.setImageResource(R.mipmap.intex_profile);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Blurry.with(UserProfileActivity.this)
                            .radius(25)
                            .sampling(2)
                            .async()
                            .capture(findViewById(R.id.profile_buller))
                            .into((ImageView) findViewById(R.id.profile_buller));
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

    }

    //==========update details when activity resume================
    private void updateDetails() {
        String lastName = sharedPreference.getUSerLastName(UserProfileActivity.this, "last_name");
        String emailId = sharedPreference.getEmailID(UserProfileActivity.this, "email_id");
        String phoneNumber = sharedPreference.getPhoneNumber(UserProfileActivity.this, "phone");
        String firstName = sharedPreference.getUSerName(UserProfileActivity.this, "first_name");
        //String googleLastName=sharedPreference.getGoogleLoginLastName(UserProfileActivity.this,"googleLastName");


        if (sharedPreference.getImageUrl(UserProfileActivity.this, "imgUrl") != null && sharedPreference.getImageUrl(UserProfileActivity.this, "imgUrl").length() > 0) {
            Picasso
                    .with(UserProfileActivity.this)
                    .load(sharedPreference.getImageUrl(UserProfileActivity.this, "imgUrl").replace("\\",""))
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_image);
        }


//        try {
//            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
//            name.setTypeface(tf);
//            phoneNumberTxt.setTypeface(tf);
//            email.setTypeface(tf);
//        } catch (Exception e) {
//            Tracer.error("UserProfileActivity", "phoneNumber_text: " + e.getMessage());
//        }

        if (firstName != null && !firstName.equals("") && !firstName.isEmpty()) {
            name.setText(firstName);
        } else {
            name.setText(getResources().getString(R.string.user_details));
        }
        if (lastName != null && !lastName.equals("null") && !lastName.isEmpty()) {
            name.setText(firstName + " " + lastName);
        }
        if (phoneNumber != null && !phoneNumber.equals("") && !phoneNumber.isEmpty()) {
            phoneNumberTxt.setText("(" + phoneNumber + ")");
        } else {
            phoneNumberTxt.setVisibility(View.GONE);
        }
        if (emailId != null && !emailId.equals("null") && !emailId.isEmpty()) {
            email.setText(emailId);
        } else {
            email.setVisibility(View.GONE);
        }

        //==========click profile image then open edit profile screen activity================

    }

    private void userEditProfile(final String imageEncodeSendTOServer) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                AppUtils.generateUrl(getApplicationContext(), ApiRequest.USER_EDIT), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Tracer.error("api_responce---", response.toString());
                try {
                    JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {


                        sharedPreference.setImageUrl(UserProfileActivity.this, "imgUrl", "");
                        MultitvCipher mcipher = new MultitvCipher();
                        String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
                        Tracer.error("***user_data***", str);
                        try {
                            JSONObject newObj = new JSONObject(str);
                            Tracer.error("***userResponces***", "" + newObj.toString());
                            String image_url = newObj.getString("image");
                            sharedPreference.setImageUrl(UserProfileActivity.this, "imgUrl", image_url);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.USER_PHOTO_CHANGED,image_url);

                            Toast.makeText(UserProfileActivity.this, "Profile successfully saved", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Tracer.error("***edit fail***", "JSONException");
                            ExceptionUtils.printStacktrace(e);
                        }
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Tracer.error("etworkResponse", "" + error.networkResponse);
                }
                Tracer.error("user_edit_fail", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("lan", LocaleHelper.getLanguage(getApplicationContext()));
                params.put("m_filter", (PreferenceData.isMatureFilterEnable(getApplicationContext()) ? "" + 1 : "" + 0));
                params.put("id", user_id);
                if (imageEncodeSendTOServer != null && !imageEncodeSendTOServer.equals(" ") && !imageEncodeSendTOServer.isEmpty()) {
                    params.put("pic", imageEncodeSendTOServer);
                    params.put("ext", "jpg");
                }

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    //==============selectImage dailog===================
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileActivity.this);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialogBuilder.setTitle(spannableString);
        alertDialogBuilder.setIcon(R.drawable.toolbar_icon);
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtility.checkPermission(UserProfileActivity.this);

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
        selectImageAlertDialog = alertDialogBuilder.create();
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

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bitmap2 = null;
        if (data != null) {
            try {
                Bitmap bm = null;
                bm = MediaStore.Images.Media.getBitmap(UserProfileActivity.this.getContentResolver(), data.getData());
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytearrayoutputstream);

                byte[] BYTE = bytearrayoutputstream.toByteArray();
                bitmap2 = BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int width = profile_image.getWidth();
        int height = profile_image.getHeight();
        profile_image.setImageBitmap(Bitmap.createScaledBitmap(bitmap2, width, height, false));

        Bitmap bitmap = ((BitmapDrawable) profile_image.getDrawable()).getBitmap();
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImageSendToServerFromLocal = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            userEditProfile(encodedImageSendToServerFromLocal);
        }
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

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        Tracer.error("filePath", "" + destination);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profile_image.setImageBitmap(thumbnail);

        if (thumbnail != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImageSendToServerFromLocal = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            Tracer.error("pic", "" + encodedImageSendToServerFromLocal);
        }
        Bitmap bitmap = ((BitmapDrawable) profile_image.getDrawable()).getBitmap();
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImageSendToServerFromLocal = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            userEditProfile(encodedImageSendToServerFromLocal);
        }
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
                Bitmap croppedBitmap = null;
                try {
                    croppedBitmap = MediaStore.Images.Media.getBitmap(UserProfileActivity.this.getContentResolver(), resultUri);
                    //Bitmap croppedBitmap = result.getBitmap();
                    profile_image.setImageBitmap(croppedBitmap);
                    profile_buller.setImageBitmap(croppedBitmap);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Blurry.with(UserProfileActivity.this)
                                    .radius(25)
                                    .sampling(2)
                                    .async()
                                    .capture(findViewById(R.id.profile_buller))
                                    .into((ImageView) findViewById(R.id.profile_buller));
                        }
                    }, SPLASH_DISPLAY_LENGTH);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (croppedBitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] byteArrayImage = baos.toByteArray();
                    encodedImageSendToServerFromLocal = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    userEditProfile(encodedImageSendToServerFromLocal);
                }
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


    @Override
    public void sendImage(String url) {

        if (url != null && !url.equals("") && !url.isEmpty()) {
            Picasso
                    .with(UserProfileActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_image);

            Picasso
                    .with(UserProfileActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_buller);
            Picasso
                    .with(UserProfileActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_buller);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Blurry.with(UserProfileActivity.this)
                            .radius(25)
                            .sampling(2)
                            .async()
                            .capture(findViewById(R.id.profile_buller))
                            .into((ImageView) findViewById(R.id.profile_buller));
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    @Override
    public void viewPagerPosition(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void emptyImage(String url) {
        if (TextUtils.isEmpty(url)) {
            profile_image.setImageResource(R.mipmap.intex_profile);
            profile_buller.setImageResource(R.mipmap.intex_profile);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Blurry.with(UserProfileActivity.this)
                            .radius(25)
                            .sampling(2)
                            .async()
                            .capture(findViewById(R.id.profile_buller))
                            .into((ImageView) findViewById(R.id.profile_buller));

                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            Picasso
                    .with(UserProfileActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.intex_profile)
                    .error(R.mipmap.intex_profile)
                    .into(profile_image);
        }
    }

    @Override
    public void sendUserFiristName(String user_name) {
        if (user_name != null && !user_name.equals("") && !user_name.isEmpty()) {
            name.setText(user_name);
            name.setVisibility(View.VISIBLE);
            try {
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
                name.setTypeface(tf);
            } catch (Exception e) {
                Tracer.error("UserProfileActivity", "first_name: " + e.getMessage());
            }
        } else {
            name.setText(getResources().getString(R.string.user_details));
            try {
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
                name.setTypeface(tf);
            } catch (Exception e) {
                Tracer.error("UserProfileActivity", "phoneNumber_text: " + e.getMessage());
            }
        }
    }

    @Override
    public void sendUserLastName(String user_first_name, String user_last_name) {
        if (!TextUtils.isEmpty(user_first_name) || !TextUtils.isEmpty(user_last_name)) {
            name.setText(user_first_name + " " + user_last_name);
            name.setVisibility(View.VISIBLE);
            try {
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
                name.setTypeface(tf);
            } catch (Exception e) {
                Tracer.error("UserProfileActivity", "last_name: " + e.getMessage());
            }
        }
    }

    @Override
    public void sendEmailId(String user_email) {
        if (user_email != null && !user_email.equals("null") && !user_email.isEmpty()) {
            email.setText(user_email);
            email.setVisibility(View.VISIBLE);
            try {
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
                email.setTypeface(tf);
            } catch (Exception e) {
                Tracer.error("UserProfileActivity", "email:text " + e.getMessage());
            }
        } else {
            email.setVisibility(View.GONE);
        }
    }

    @Override
    public void sendMobileNumber(String user_phone) {

        if (!TextUtils.isEmpty(user_phone)) {
            phoneNumberTxt.setText("(" + user_phone + ")");
            phoneNumberTxt.setVisibility(View.VISIBLE);
            try {
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-regular.ttf");
                phoneNumberTxt.setTypeface(tf);
            } catch (Exception e) {
                Tracer.error("UserProfileActivity", "phoneNumber_text: " + e.getMessage());
            }
        } else {
            phoneNumberTxt.setVisibility(View.GONE);
        }
    }

    class GetImagePath extends AsyncTask<Void, Void, Uri> {
        int requestCode;
        ImageProcessorListener mImageProcessorListener;
        Intent data;
        Uri imageUri;

        GetImagePath(int reqCode, ImageProcessorListener imageProcessorListener, Intent intent) {
            this.requestCode = reqCode;
            this.mImageProcessorListener = imageProcessorListener;
            data = intent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbProfilePic.setVisibility(View.VISIBLE);
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
            pbProfilePic.setVisibility(View.GONE);
            if (uri != null) {
                startCropImageActivity(uri);
            }
        }
    }

}
