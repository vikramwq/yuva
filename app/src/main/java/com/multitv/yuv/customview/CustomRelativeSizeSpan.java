package com.multitv.yuv.customview;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;

/**
 * Created by mkr on 12/20/2016.
 */

public class CustomRelativeSizeSpan extends RelativeSizeSpan {
    private Typeface mTypeFace;

    public CustomRelativeSizeSpan(float proportion, Typeface typeFace) {
        this(proportion);
        mTypeFace = typeFace;
    }

    public CustomRelativeSizeSpan(float proportion) {
        super(proportion);
    }

    public CustomRelativeSizeSpan(Parcel src) {
        super(src);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, mTypeFace);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, mTypeFace);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
//        int oldStyle;
//        Typeface old = paint.getTypeface();
//        if (old == null) {
//            oldStyle = 0;
//        } else {
//            oldStyle = old.getStyle();
//        }
//
//        int fake = oldStyle & ~tf.getStyle();
//        if ((fake & Typeface.BOLD) != 0) {
//            paint.setFakeBoldText(true);
//        }
//
//        if ((fake & Typeface.ITALIC) != 0) {
//            paint.setTextSkewX(-0.25f);
//        }
        paint.setTypeface(tf);
    }
}
