package com.multitv.yuv.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.multitv.yuv.R;


/**
 * Created by mkr on 11/24/2016.
 */

public class DropDownTextView extends TextView {
    private RectF mRectF;
    private static Bitmap mBitmap;

    public DropDownTextView(Context context) {
        super(context);
        init();
    }

    public DropDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DropDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DropDownTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mRectF = new RectF();
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int l = getWidth() - (int) (dim * 2);
        int t = (getHeight() >> 1) - (dim >> 1);
        mRectF.set(l, t, l + dim, t + dim);
        setPadding(getPaddingLeft(), getPaddingTop(), (dim * 3), getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, null, mRectF, null);
        }
    }
}
