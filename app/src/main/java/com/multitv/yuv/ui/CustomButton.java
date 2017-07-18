package com.multitv.yuv.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Lenovo on 02-02-2017.
 */

public class CustomButton extends Button {


    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }
    public CustomButton(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }
    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }
    private void init(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Montserrat-Regular.ttf");
        setTypeface(tf);
    }
}
