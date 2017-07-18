package com.multitv.yuv.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by naseeb on 5/25/2017.
 */

public class GifImageView extends AppCompatImageView {

    private InputStream mInputStream;
    private Movie mMovie;
    private int mWidth, mHeight;
    private long mStart;
    private Context mContext;

    public GifImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (attrs.getAttributeName(1).equals("background")) {
            int id = Integer.parseInt(attrs.getAttributeValue(1).substring(1));
            setGifImageResource(id);
        }
    }


    private void init() {
        setFocusable(true);
        mMovie = Movie.decodeStream(mInputStream);
        mWidth = mMovie.width();
        mHeight = mMovie.height();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        if (mStart == 0) {
            mStart = now;
        }
        if (mMovie != null) {
            int duration = mMovie.duration();

            if (duration == 0) {
                duration = 1000;
            }
            float scale = 1f;

//            if (mMovie.height() / canvas.getHeight() >= 2 || mMovie.width() / canvas.getWidth() >= 2) {
//                scale = (Math.min(canvas.getHeight() / mMovie.height(), canvas.getWidth() / mMovie.width())) + 0.35f;
//            } else if (mMovie.height() / canvas.getHeight() > 1 || mMovie.width() / canvas.getWidth() > 1) {
//                scale = (Math.min(canvas.getHeight() / mMovie.height(), canvas.getWidth() / mMovie.width())) + 0.50f;
//            } else {
//                scale = Math.min(canvas.getHeight() / mMovie.height(), canvas.getWidth() / mMovie.width());
//            }
            canvas.scale(scale, scale);
            canvas.translate(((float) getWidth() / scale - (float) mMovie.width()) / 2f, ((float) getHeight() / scale - (float) mMovie.height()) / 2f);
            int relTime = (int) ((now - mStart) % duration);
            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }

    public void setGifImageResource(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

    public void setGifImageUri(Uri uri) {
        try {
            mInputStream = mContext.getContentResolver().openInputStream(uri);
            init();
        } catch (FileNotFoundException e) {
            Log.e("GIfImageView", "File not found");
        }
    }
}
