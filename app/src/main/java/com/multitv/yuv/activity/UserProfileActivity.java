package com.multitv.yuv.activity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.multitv.yuv.R;

import java.util.Calendar;

/**
 * Created by Lenovo on 17-07-2017.
 */

public class UserProfileActivity extends AppCompatActivity {
    private TextView selectedGenderTextview, selectedDateTextview;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        setContentView(R.layout.user_profile_activity);
        selectedDateTextview = (TextView) findViewById(R.id.selectedDateTextview);
        selectedGenderTextview = (TextView) findViewById(R.id.selectedGenderTextview);

        findViewById(R.id.genderSelectedImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UserProfileActivity.this, findViewById(R.id.genderSelectedImageView));
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedGenderTextview.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show();
            }
        });
        findViewById(R.id.dateListcollospeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UserProfileActivity.this,
                        mDateSetListener,
                        mYear, mMonth, mDay).show();
            }
        });
        setDate();
    }

    private void setDate() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR) - 20;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH); //Default DOB is (today - 20) years
        //date picker things
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                selectedDateTextview.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
            }
        };
    }
}
