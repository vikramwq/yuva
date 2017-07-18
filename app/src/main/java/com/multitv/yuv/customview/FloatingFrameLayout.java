package com.multitv.yuv.customview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.multitv.yuv.utilities.PreferenceData;

/**
 * Created by mkr on 11/22/2016.
 */

public class FloatingFrameLayout extends FrameLayout {

    private boolean mIsViewPressed;
    private int mXPadding, mYPadding;
    private long mPressTime;

    public FloatingFrameLayout(Context context) {
        super(context);
        init();
    }

    public FloatingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Method to init the View Members
     */
    private void init() {

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() == 1) {
            View childAt = getChildAt(0);
            int l = getWidth() - childAt.getWidth();
            int t = childAt.getHeight() * 2;
            if (PreferenceData.getFloatX(getContext()) < 0 || PreferenceData.getFloatY(getContext()) < 0) {
                // DO SOMETHING
            } else {
                l = PreferenceData.getFloatX(getContext());
                t = PreferenceData.getFloatY(getContext());
            }
            childAt.layout(l, t, l + childAt.getWidth(), t + childAt.getHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getChildCount() == 1) {
            View childAt = getChildAt(0);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPressTime = System.currentTimeMillis();
                    float x = event.getX();
                    float y = event.getY();
                    if (childAt.getLeft() <= x && x <= childAt.getRight() && childAt.getTop() <= y && y <= childAt.getBottom()) {
                        mIsViewPressed = true;
                        mXPadding = (int) x - childAt.getLeft();
                        mYPadding = (int) y - childAt.getTop();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mIsViewPressed) {
                        if (childAt instanceof FloatImageView) {
                            ((FloatImageView) childAt).holdClickEvent();
                        }
                        int left = (int) event.getX() - mXPadding;
                        int top = (int) event.getY() - mYPadding;
                        if (left < 0) {
                            left = 0;
                        }
                        if (left > (getWidth() - childAt.getWidth())) {
                            left = getWidth() - childAt.getWidth();
                        }
                        if (top < 0) {
                            top = 0;
                        }
                        if (top > (getHeight() - childAt.getHeight())) {
                            top = getHeight() - childAt.getHeight();
                        }
                        childAt.layout(left, top, left + childAt.getWidth(), top + childAt.getHeight());
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mIsViewPressed = false;
                    PreferenceData.setFloatX(getContext(), childAt.getLeft());
                    PreferenceData.setFloatY(getContext(), childAt.getTop());
                    if (childAt instanceof FloatImageView) {
                        if ((System.currentTimeMillis() - 200) > mPressTime) {
                            ((FloatImageView) childAt).releaseClickEvent();
                        } else {
                            ((FloatImageView) childAt).releaseClickEventHard();
                        }
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
