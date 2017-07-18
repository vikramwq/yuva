package com.multitv.yuv.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.AppConfig;

/**
 * Created by mkr on 12/14/2016.
 */

public class MKRRecyclerView extends RecyclerView {
    private static final String TAG = AppConfig.BASE_TAG + ".MKRRecyclerView";
    private OnItemTouchListener mOnItemTouchListener;

    public MKRRecyclerView(Context context) {
        super(context);
    }

    public MKRRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MKRRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addOnItemTouchListener(OnItemTouchListener listener) {
        Tracer.error(TAG, "addOnItemTouchListener: " + mOnItemTouchListener);
        try {
            if (mOnItemTouchListener != null) {
                removeOnItemTouchListener(mOnItemTouchListener);
            }
        } catch (Exception e) {
            Tracer.error(TAG, "addOnItemTouchListener: " + e.getMessage());
        }
        super.addOnItemTouchListener(mOnItemTouchListener = listener);
    }
}
