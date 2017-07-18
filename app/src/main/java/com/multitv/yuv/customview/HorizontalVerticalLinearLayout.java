package com.multitv.yuv.customview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.multitv.yuv.utilities.AppConfig;

/**
 * Created by mkr on 11/21/2016.
 */

public class HorizontalVerticalLinearLayout extends ViewGroup {
    private static final String TAG = AppConfig.BASE_TAG + ".HorizontalVerticalLinearLayout";
    boolean showAll = false;
    int rowCounter;
    ImageView ivMore;

    public HorizontalVerticalLinearLayout(Context context) {
        super(context);
    }

    public HorizontalVerticalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalVerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalVerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        int padding = (int) ((float) getResources().getDisplayMetrics().widthPixels * 0.02F);
        int rowCount = 1;
        int counter = 0;
        if (getChildCount() > 0) {
            int maxWidth = getWidth();
            int left = 0;
            int top = padding;
            int maxHeightInRow = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                if (left + measuredWidth <= maxWidth) {
                    childAt.layout(left, top, left + measuredWidth, top + measuredHeight);
                    left += measuredWidth + padding;
                    if (maxHeightInRow < measuredHeight) {
                        maxHeightInRow = measuredHeight;
                    }
                } else {
                    counter += 1;
                    rowCounter = counter;
                    if (showAll) {
                        if (maxHeightInRow == 0) {
                            if (i > 0) {
                                View oldChild = getChildAt(i - 1);
                                maxHeightInRow = oldChild.getMeasuredHeight();
                            }
                        }
                        top += maxHeightInRow + padding;
                        childAt.layout(0, top, measuredWidth, top + measuredHeight);
                        left = measuredWidth + padding;
                        maxHeightInRow = 0;
                    } else {
                        if (rowCount < 2) {
                            if (maxHeightInRow == 0) {
                                if (i > 0) {
                                    View oldChild = getChildAt(i - 1);
                                    maxHeightInRow = oldChild.getMeasuredHeight();
                                }
                            }
                            top += maxHeightInRow + padding;
                            childAt.layout(0, top, measuredWidth, top + measuredHeight);
                            left = measuredWidth + padding;
                            maxHeightInRow = 0;
                            rowCount = rowCount + 1;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (rowCounter >= 2) {
            ivMore.setVisibility(View.VISIBLE);
        } else {
            ivMore.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int padding = (int) ((float) getResources().getDisplayMetrics().widthPixels * 0.02F);
        int newHeightMeasureSpec;
        int rowCount = 1;
        if (getChildCount() > 0) {
            int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
            int caluclatedWidth = 0;
            int caluclatedHeight = padding;
            int maxHeightInRow = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                if (caluclatedWidth + measuredWidth <= maxWidth) {
                    caluclatedWidth += measuredWidth + padding;
                    if (maxHeightInRow < measuredHeight) {
                        maxHeightInRow = measuredHeight;
                    }
                } else {
                    if (showAll) {
                        int lastChildHeight = 0;
                        if (i > 0) {
                            View oldChild = getChildAt(i - 1);
                            measureChild(oldChild, widthMeasureSpec, heightMeasureSpec);
                            lastChildHeight = oldChild.getMeasuredHeight();
                        }
                        if (maxHeightInRow == 0) {
                            maxHeightInRow = lastChildHeight;
                        }
                        caluclatedWidth = measuredWidth + padding;
                        caluclatedHeight += maxHeightInRow + padding;
                        maxHeightInRow = 0;
                    } else {
                        if (rowCount < 2) {
                            int lastChildHeight = 0;
                            if (i > 0) {
                                View oldChild = getChildAt(i - 1);
                                measureChild(oldChild, widthMeasureSpec, heightMeasureSpec);
                                lastChildHeight = oldChild.getMeasuredHeight();
                            }
                            if (maxHeightInRow == 0) {
                                maxHeightInRow = lastChildHeight;
                            }
                            caluclatedWidth = measuredWidth + padding;
                            caluclatedHeight += maxHeightInRow + padding;
                            maxHeightInRow = 0;
                            rowCount = rowCount + 1;
                        } else {
                            break;
                        }
                    }
                }
            }
            View oldChild = getChildAt(getChildCount() - 1);
            measureChild(oldChild, widthMeasureSpec, heightMeasureSpec);
            caluclatedHeight += maxHeightInRow + oldChild.getMeasuredHeight() + padding;
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(caluclatedHeight, MeasureSpec.EXACTLY);
        } else {
            newHeightMeasureSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }

    public void showWholeView(boolean b1) {
        showAll = b1;
    }

    public void setMoreIcon(ImageView imageView) {
        ivMore = imageView;
    }
}
