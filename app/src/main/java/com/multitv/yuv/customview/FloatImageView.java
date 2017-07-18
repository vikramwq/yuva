package com.multitv.yuv.customview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.Tracer;

/**
 * Created by mkr on 11/23/2016.
 */

public class FloatImageView extends ImageView implements View.OnClickListener {
    private static final String TAG = AppConfig.BASE_TAG + ".FloatImageView";
    private OnClickListener mOnClickListener;
    private boolean mIsClickOn = true;

    public FloatImageView(Context context) {
        super(context);
        init();
    }

    public FloatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener(this);
    }

    /**
     * Method to take care that the View Will not take click event
     */
    public void holdClickEvent() {
        Tracer.error(TAG, "holdClickEvent: ");
        mIsClickOn = false;
    }

    /**
     * Method to take care that the View Will take click event
     */
    public void releaseClickEvent() {
        Tracer.error(TAG, "releaseClickEvent: 1");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsClickOn = true;
                Tracer.error(TAG, "releaseClickEvent: 2");
            }
        }, 250);
    }

    /**
     * Method to take care that the View Will take click event
     */
    public void releaseClickEventHard() {
        Tracer.debug(TAG, "releaseClickEventHard: ");
        mIsClickOn = true;
    }

    @Override
    public void onClick(View view) {
        Tracer.error(TAG, "onClick: " + mIsClickOn);
        if (mIsClickOn) {
            mOnClickListener.onClick(this);
        }
    }
}
