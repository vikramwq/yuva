package com.multitv.yuv.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.models.menu.MenuModel;
import com.multitv.yuv.utilities.Tracer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import com.multitv.yuv.utilities.AppConfig;

/**
 * Created by mkr on 11/22/2016.
 */

public class HomeTabOptionPopupMenu implements AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {
    private static final String TAG = AppConfig.BASE_TAG + ".HomeTabOptionAlertDialog";
    private Context mContext;
    private View mAnchorView;
    private MenuModel mMenuModel;
    private OnHomeTabOptionPopupMenuListener mOnHomeTabOptionPopupMenuListener;
    private ListPopupWindow mListPopupWindow;
    private OptionListAdapter mOptionListAdapter;

    public HomeTabOptionPopupMenu(Context context, View anchorView, MenuModel menuModel, OnHomeTabOptionPopupMenuListener onHomeTabOptionAlertDialogListener) {
        mContext = context;
        mOnHomeTabOptionPopupMenuListener = onHomeTabOptionAlertDialogListener;
        mMenuModel = menuModel;
        mAnchorView = anchorView;
        initView();
    }

    private void initView() {
        mListPopupWindow = new ListPopupWindow(mContext);
        mOptionListAdapter = new OptionListAdapter(mContext, mMenuModel.topMenuArrayList);
        mListPopupWindow.setAdapter(mOptionListAdapter);
        mListPopupWindow.setAnchorView(mAnchorView);
        mListPopupWindow.setOnItemClickListener(this);
        mListPopupWindow.setOnDismissListener(this);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setWidth(mContext.getResources().getDimensionPixelOffset(R.dimen.popup_width));
//        mListPopupWindow.setHeight(mOptionListAdapter.getCount() * mContext.getResources().getDimensionPixelOffset(R.dimen.popup_height_single_item));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Tracer.error(TAG, "onItemClick: ");
        if (mOptionListAdapter != null) {
            MenuModel.TopMenu item = mOptionListAdapter.getItem(i);
            mOnHomeTabOptionPopupMenuListener.onHomeTabOptionPopupMenuShowFragment(item.identifier, item.thumbnail);
        }
        dissmiss();
    }

    /**
     * Method to dismiss pup window
     */
    public void dissmiss() {
        if (mListPopupWindow != null) {
            mListPopupWindow.dismiss();
        }
    }

    /**
     * Method to check weather the popup window is shownig or not
     *
     * @return
     */
    public boolean isPopupWindowShown() {
        if (mListPopupWindow != null) {
            return mListPopupWindow.isShowing();
        }
        return false;
    }

    @Override
    public void onDismiss() {
        Tracer.error(TAG, "onDismiss: ");
        if (mOptionListAdapter != null) {
            mOnHomeTabOptionPopupMenuListener.onHomeTabOptionPopupMenuDissmiss();
        }
    }

    /**
     * Method to show the option menu
     */
    public void show() {
        if (mOptionListAdapter != null) {
            mOnHomeTabOptionPopupMenuListener.onHomeTabOptionPopupMenuShown();
        }
        if (mListPopupWindow != null && !mListPopupWindow.isShowing()) {
            mListPopupWindow.show();
            try {
                mListPopupWindow.getListView().getLayoutParams().height = AbsListView.LayoutParams.WRAP_CONTENT;
            } catch (Exception e) {
                Tracer.error(TAG, "show: " + e.getMessage());
            }
        }
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
            view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_copy, null);
            TextView tv = (TextView) view.findViewById(R.id.tab_item_txt);
            tv.setText(mTopMenuArrayList.get(position).pageTitle);
            try {
                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Ubuntu-regular.ttf");
                tv.setTypeface(tf);
            } catch (Exception e) {
                Tracer.error(TAG, "getView: " + e.getMessage());
            }
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

    /**
     * Callback to notify that User want to open a specific fragment
     */
    public interface OnHomeTabOptionPopupMenuListener {
        /**
         * Method to notify that User want to open a specific fragment
         *
         * @param fragmentId Unique Id of the Fragment
         */
        public void onHomeTabOptionPopupMenuShowFragment(String fragmentId, String iconUrl);

        /**
         * Method to notify that dialog is dissmiss
         */
        public void onHomeTabOptionPopupMenuDissmiss();

        /**
         * Method to notify that dialog is shown to user
         */
        public void onHomeTabOptionPopupMenuShown();
    }
}
