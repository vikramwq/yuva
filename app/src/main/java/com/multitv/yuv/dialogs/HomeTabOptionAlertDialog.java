package com.multitv.yuv.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.models.menu.MenuModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.multitv.yuv.animations.HorizontalFlipAnimation;
import com.multitv.yuv.animations.VerticallyFlipAnimation;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.Tracer;

/**
 * Created by mkr on 11/22/2016.
 */

public class HomeTabOptionAlertDialog extends AlertDialog {
    private static final String TAG = AppConfig.BASE_TAG + ".HomeTabOptionAlertDialog";
    private static final int DELAY = 50;
    private static final int DEMO_THRASH_HOLD = 1000;
    private ListView mListView;
    private OnHomeTabOptionAlertDialogListener mOnHomeTabOptionAlertDialogListener;
    private boolean mIsDemo;
    private MenuModel mMenuModel;
    private OptionListAdapter mOptionListAdapter;
    private int mLeftMargin, mTopMargin;

    public HomeTabOptionAlertDialog(Context context, int leftMargin, int topMargin, MenuModel menuModel, OnHomeTabOptionAlertDialogListener onHomeTabOptionAlertDialogListener) {
        super(context);
        mOnHomeTabOptionAlertDialogListener = onHomeTabOptionAlertDialogListener;
        mIsDemo = false;
        mMenuModel = menuModel;
        mLeftMargin = leftMargin;
        mTopMargin = topMargin;
    }

//    public HomeTabOptionAlertDialog(Context context, MenuModel menuModel) {
//        super(context);
//        mIsDemo = true;
//        mMenuModel = menuModel;
//    }

    @Override
    public void onAttachedToWindow() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = getContext().getResources().getDisplayMetrics().widthPixels;
        lp.height = getContext().getResources().getDisplayMetrics().heightPixels;
        getWindow().setAttributes(lp);
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        setContentView(getLayoutInflater().inflate(R.layout.dialog_home_tab_options, null));
        try {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
            Tracer.error(TAG, "onCreate: " + e.getMessage());
        }
        mListView = (ListView) findViewById(R.id.dialog_home_tab_option_listView_menu_option);
        initTabs();
        findViewById(R.id.dialog_home_tab_option_empty_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracer.error(TAG, "onCreat().onClick: ");
                startListViewCloseAnimation(0, null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tracer.error(TAG, "onStart: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startListViewOpenAnimation();
            }
        }, 500);
    }

    private void initTabs() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
        layoutParams.setMargins(mLeftMargin, mTopMargin, 0, 0);
        mListView.getLayoutParams().width = (int) ((float) getContext().getResources().getDisplayMetrics().widthPixels * 0.45F);
        mListView.setAdapter(mOptionListAdapter = new OptionListAdapter(getContext(), mMenuModel.topMenuArrayList));
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                OptionListAdapter optionListAdapter = (OptionListAdapter) mListView.getAdapter();
                MenuModel.TopMenu item = optionListAdapter.getItem(position);
                startListViewCloseAnimation(position, item.identifier);
            }
        });
        mListView.setVisibility(View.INVISIBLE);
    }

    /**
     * Method to start the ListView opening animation
     *
     * @param
     */
    private void startListViewOpenAnimation() {
        Tracer.error(TAG, "startListViewOpenAnimation: " + mListView.getChildCount());
        if (mOnHomeTabOptionAlertDialogListener != null) {
            mOnHomeTabOptionAlertDialogListener.onHomeTabOptionAlertDialogShown();
        }
        mListView.setVisibility(View.VISIBLE);
        if (mListView.getChildCount() > 0) {
            for (int i = 0; i < mListView.getChildCount(); i++) {
                mListView.getChildAt(i).setVisibility(View.INVISIBLE);
            }
            long delay = 0;
            for (int i = 0; i < mListView.getChildCount(); i++) {
                try {
                    final View childAt = mListView.getChildAt(i);
                    childAt.setVisibility(View.VISIBLE);
                    childAt.findViewById(R.id.tab_item_txt).setVisibility(View.INVISIBLE);
                    // SET IMAGE ANIMATION
                    Animation verticallyFlipAnimation = ((i == 0) ? new HorizontalFlipAnimation(HorizontalFlipAnimation.HORIZONTAL_FLIP_OPEN) : new VerticallyFlipAnimation(VerticallyFlipAnimation.VERTICAL_FLIP_OPEN));
                    verticallyFlipAnimation.setDuration(DELAY);
                    verticallyFlipAnimation.setFillAfter(true);
                    verticallyFlipAnimation.setStartOffset(delay);
                    delay += DELAY;
                    verticallyFlipAnimation.setAnimationListener(new OpenMenuOptionAnimationListener(mListView, childAt, i));
                    childAt.findViewById(R.id.tab_item_view_layout).startAnimation(verticallyFlipAnimation);
                } catch (Exception e) {
                    Tracer.error(TAG, "startListViewOpenAnimation: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Method to start the ListView close animation
     */
    private void startListViewCloseAnimation(int clickChildPosition, String newTabIdentifire) {
        boolean bottomController = ((mListView.getChildCount() - 1) - clickChildPosition) > clickChildPosition;
        startListViewCloseAnimationBottom(clickChildPosition, bottomController, newTabIdentifire);
        startListViewCloseAnimationTop(clickChildPosition, !bottomController, newTabIdentifire);
    }

    /**
     * Method to start the ListView close animation
     */
    private void startListViewCloseAnimationTop(int clickChildPosition, boolean isController, String newTabIdentifire) {
        long delay = 0;
        for (int i = 0; i < clickChildPosition; i++) {
            final View childAt = mListView.getChildAt(i);
            try {
                // SET TEXT ANIMATION
                AnimationSet animationSet = new AnimationSet(false);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0F);
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setFillAfter(true);
                animationSet.setDuration(DELAY >> 1);
                animationSet.setStartOffset(delay);
                delay += DELAY;
                animationSet.setAnimationListener(new CloseTopMenuOptionAnimationListener(mListView, childAt, i, isController, clickChildPosition, newTabIdentifire));
                childAt.findViewById(R.id.tab_item_txt).startAnimation(animationSet);
            } catch (Exception e) {
                Tracer.error(TAG, "startListViewCloseAnimationTop: " + e.getMessage());
            }
        }
    }

    /**
     * Method to start the ListView close animation
     */
    private void startListViewCloseAnimationBottom(int clickChildPosition, boolean isController, String newTabIdentifire) {
        long delay = 0;
        for (int i = mListView.getChildCount() - 1; i > clickChildPosition; i--) {
            final View childAt = mListView.getChildAt(i);
            try {
                // SET TEXT ANIMATION
                AnimationSet animationSet = new AnimationSet(false);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0F);
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setFillAfter(true);
                animationSet.setDuration(DELAY >> 1);
                animationSet.setStartOffset(delay);
                delay += DELAY;
                animationSet.setAnimationListener(new CloseBottomMenuOptionAnimationListener(mListView, childAt, i, isController, clickChildPosition, newTabIdentifire));
                childAt.findViewById(R.id.tab_item_txt).startAnimation(animationSet);
            } catch (Exception e) {
                Tracer.error(TAG, "startListViewCloseAnimationBottom: " + e.getMessage());
            }
        }
    }

    /**
     * Method to hide the last visible child of the List View
     *
     * @param childClikcIndex
     * @param newTabIdentifire The Identifire of the New Fragment
     */
    private void hideLastChild(int childClikcIndex, final String newTabIdentifire) {
        try {
            if (mListView.getChildCount() > childClikcIndex) {
                View childAt = mListView.getChildAt(childClikcIndex);
                // SET TEXT ANIMATION
                AnimationSet animationSet = new AnimationSet(false);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0F);
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setFillAfter(true);
                animationSet.setDuration(DELAY >> 1);
                childAt.findViewById(R.id.tab_item_txt).startAnimation(animationSet);
                // SET IMAGE ANIMATION
                HorizontalFlipAnimation horizontalFlipAnimation = new HorizontalFlipAnimation(HorizontalFlipAnimation.HORIZONTAL_FLIP_CLOSE);
                horizontalFlipAnimation.setDuration(DELAY);
                horizontalFlipAnimation.setFillAfter(true);
                horizontalFlipAnimation.setStartOffset(DELAY);
                horizontalFlipAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mListView.setVisibility(View.INVISIBLE);
                        if (newTabIdentifire != null && mOnHomeTabOptionAlertDialogListener != null) {
                            mOnHomeTabOptionAlertDialogListener.onHomeTabOptionAlertDialogShowFragment(newTabIdentifire, mOptionListAdapter.getThumbPath(newTabIdentifire));
                        }
                        dismissDialog();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                childAt.findViewById(R.id.tab_item_view_layout).startAnimation(horizontalFlipAnimation);
            }
        } catch (Exception e) {
            Tracer.error(TAG, "onAnimationEnd: " + e.getMessage());
        }
    }

    private void dismissDialog() {
        if (mOnHomeTabOptionAlertDialogListener != null) {
            mOnHomeTabOptionAlertDialogListener.onHomeTabOptionAlertDialogDissmiss();
        }
        super.dismiss();
    }

    /**
     * List adapter
     */
    private class OptionListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<MenuModel.TopMenu> mTopMenuArrayList;

        /**
         * Constructor
         */
        public OptionListAdapter(Context context, ArrayList<MenuModel.TopMenu> topMenuArrayList) {
            mContext = context;
            mTopMenuArrayList = new ArrayList<>();
            if (topMenuArrayList != null) {
                mTopMenuArrayList.addAll(topMenuArrayList);
            }
        }

        @Override

        public int getCount() {
            return mTopMenuArrayList.size();
        }

        @Override
        public MenuModel.TopMenu getItem(int position) {
            return mTopMenuArrayList.get(position);
        }

        @Override
        public long getItemId(int positionIndex) {
            return positionIndex;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            // Given you have a custom layout in `res/layout/custom_tab_item.xml` with a TextView and ImageView
            view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_copy_popup, null);
            if (position == 0) {
                view.findViewById(R.id.tab_item_shadow).setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            }
            TextView tv = (TextView) view.findViewById(R.id.tab_item_txt);
            tv.setText(mTopMenuArrayList.get(position).pageTitle);
            ImageView img = (ImageView) view.findViewById(R.id.tab_item_view);
            int widthAndHeightOfIcon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics());
            Picasso.with(mContext)
                    .load(mTopMenuArrayList.get(position).thumbnail)
                    .placeholder(R.mipmap.place_holder).error(R.mipmap.place_holder)
                    .resize(widthAndHeightOfIcon, widthAndHeightOfIcon)
                    .into(img);

            return view;
        }

        /**
         * Method to get the Thumbnail URL based of the Identifire Name
         *
         * @param identifire
         * @return
         */
        private String getThumbPath(String identifire) {
            for (int i = 0; i < getCount(); i++) {
                MenuModel.TopMenu item = getItem(i);
                if (item.identifier.equalsIgnoreCase(identifire)) {
                    return item.thumbnail;
                }
            }
            return "";
        }
    }

    private class OpenMenuOptionAnimationListener implements Animation.AnimationListener {
        private View mParentView;
        private int mChildIndex;
        private ListView mListView;

        public OpenMenuOptionAnimationListener(ListView listView, View parentView, int childIndex) {
            mListView = listView;
            mParentView = parentView;
            mChildIndex = childIndex;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                mParentView.findViewById(R.id.tab_item_txt).setVisibility(View.VISIBLE);
                // SET TEXT ANIMATION
                AnimationSet animationSet = new AnimationSet(false);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0F, 1F);
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setFillAfter(true);
                animationSet.setDuration(DELAY >> 1);
                mParentView.findViewById(R.id.tab_item_txt).startAnimation(animationSet);
                Tracer.error(TAG, "OpenMenuOptionAnimationListener.onAnimationEnd: " + mIsDemo + "       " + mChildIndex + "     " + mListView.getChildCount());
                if (mIsDemo && mChildIndex == mListView.getChildCount() - 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startListViewCloseAnimation(0, null);
                        }
                    }, DEMO_THRASH_HOLD);
                }
            } catch (Exception e) {
                Tracer.error(TAG, "onAnimationEnd: " + e.getMessage());
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class CloseTopMenuOptionAnimationListener implements Animation.AnimationListener {
        private View mParentView;
        private int mChildIndex;
        private boolean mIsController;
        private ListView mListView;
        private int mSelectChildIndex;
        private String mNewTabIdentifire;

        public CloseTopMenuOptionAnimationListener(ListView listView, View parentView, int childIndex, boolean isController, int selectChildIndex, String newTabIdentifire) {
            mListView = listView;
            mParentView = parentView;
            mChildIndex = childIndex;
            mIsController = isController;
            mSelectChildIndex = selectChildIndex;
            mNewTabIdentifire = newTabIdentifire;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                // SET IMAGE ANIMATION
                VerticallyFlipAnimation verticallyFlipAnimation = new VerticallyFlipAnimation(VerticallyFlipAnimation.VERTICAL_FLIP_CLOSE_TOP);
                verticallyFlipAnimation.setDuration(DELAY);
                verticallyFlipAnimation.setFillAfter(true);
                mParentView.findViewById(R.id.tab_item_view_layout).startAnimation(verticallyFlipAnimation);
                if (mIsController && Math.abs(mSelectChildIndex - mChildIndex) == 1) {
                    hideLastChild(mSelectChildIndex, mNewTabIdentifire);
                }
            } catch (Exception e) {
                Tracer.error(TAG, "onAnimationEnd: " + e.getMessage());
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class CloseBottomMenuOptionAnimationListener implements Animation.AnimationListener {
        private View mParentView;
        private int mChildIndex;
        private boolean mIsController;
        private ListView mListView;
        private int mSelectChildIndex;
        private String mNewTabIdentifire;

        public CloseBottomMenuOptionAnimationListener(ListView listView, View parentView, int childIndex, boolean isController, int selectChildIndex, String newTabIdentifire) {
            mListView = listView;
            mParentView = parentView;
            mChildIndex = childIndex;
            mIsController = isController;
            mSelectChildIndex = selectChildIndex;
            mNewTabIdentifire = newTabIdentifire;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                // SET IMAGE ANIMATION
                VerticallyFlipAnimation verticallyFlipAnimation = new VerticallyFlipAnimation(VerticallyFlipAnimation.VERTICAL_FLIP_CLOSE_BOTTOM);
                verticallyFlipAnimation.setDuration(DELAY);
                verticallyFlipAnimation.setFillAfter(true);
                mParentView.findViewById(R.id.tab_item_view_layout).startAnimation(verticallyFlipAnimation);
            } catch (Exception e) {
                Tracer.error(TAG, "onAnimationEnd: " + e.getMessage());
            }
            if (mIsController && Math.abs(mSelectChildIndex - mChildIndex) == 1) {
                hideLastChild(mSelectChildIndex, mNewTabIdentifire);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    public void dismiss() {
        startListViewCloseAnimation(0, null);
    }

    /**
     * Callback to notify that User want to open a specific fragment
     */
    public interface OnHomeTabOptionAlertDialogListener {
        /**
         * Method to notify that User want to open a specific fragment
         *
         * @param fragmentId Unique Id of the Fragment
         */
        public void onHomeTabOptionAlertDialogShowFragment(String fragmentId, String iconUrl);

        /**
         * Method to notify that dialog is dissmiss
         */
        public void onHomeTabOptionAlertDialogDissmiss();

        /**
         * Method to notify that dialog is shown to user
         */
        public void onHomeTabOptionAlertDialogShown();
    }
}
