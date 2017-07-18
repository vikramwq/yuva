package com.multitv.yuv.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.multitv.yuv.utilities.Tracer;

import static com.multitv.yuv.utilities.Constant.EXTRA_DATE;

/**
 * Created by Laxmi on 10/18/2016.
 */
public class DateChangeLocalBroadcast extends BroadcastReceiver {
    public static final String ACTION = "DateChangeLocalBroadcast";
    private OnDateChangeLocalBroadCastListener mOnDateChangeLocalBroadCastListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.error("MKR", "DateChangeLocalBroadcast.onReceive() " + mOnDateChangeLocalBroadCastListener);
        if (mOnDateChangeLocalBroadCastListener != null) {
            mOnDateChangeLocalBroadCastListener.onDateChangeLocalBroadCastListener(intent.getStringExtra(EXTRA_DATE));
        }
    }

    /**
     * Method to set the Listener to listen event
     *
     * @param onDateChangeLocalBroadCastListener
     */
    public void setOnDateChangeLocalBroadCastListener(OnDateChangeLocalBroadCastListener onDateChangeLocalBroadCastListener) {
        this.mOnDateChangeLocalBroadCastListener = onDateChangeLocalBroadCastListener;
    }

    /**
     * Callback To listen event occur inside this listener
     */
    public interface OnDateChangeLocalBroadCastListener {
        public void onDateChangeLocalBroadCastListener(String date);
    }
}
