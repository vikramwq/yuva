package com.multitv.yuv.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.multitv.yuv.fragment.EditProfileFragment;
import com.multitv.yuv.utilities.Tracer;

import java.util.Arrays;

import com.multitv.yuv.fragment.UserRelatedContentFragment;

import static com.multitv.yuv.utilities.Constant.EXTRA_KEY;
import static com.multitv.yuv.utilities.Constant.EXTRA_VALUE_FAV;
import static com.multitv.yuv.utilities.Constant.EXTRA_VALUE_LIKE;
import static com.multitv.yuv.utilities.Constant.EXTRA_VALUE_WATCH;


public class UserProfilePagerAdapter extends FragmentItemIdStatePagerAdapterWithSupport {

    private Context context;
    private int slideCount;
    private String[] data;

    public UserProfilePagerAdapter(FragmentManager manager, Context mContext, String[] data, int count) {
        super(manager);
        this.context = mContext;
        this.data = data;
        this.slideCount = count;
    }


    @Override
    public Fragment getItem(int position) {
        Tracer.error("MKR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> getItem: " + position);
//        if (position == 3) {
//            return new ReminderListFragment();
//        }
//        if (position == 4) {
//            return new FragmentSetting();
//        }
        if (position == 3) {
            return new EditProfileFragment();
        }
        Fragment frag;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                bundle.putInt(EXTRA_KEY, EXTRA_VALUE_WATCH);
                break;
            case 1:
                bundle.putInt(EXTRA_KEY, EXTRA_VALUE_FAV);
                break;
            case 2:
                bundle.putInt(EXTRA_KEY, EXTRA_VALUE_LIKE);
                break;

            default:

        }
        frag = new UserRelatedContentFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return slideCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        /*switch (position) {

            case 0:
                return   context.getResources().getString(R.string.watch_tab);

            case 1:
                return   context.getResources().getString(R.string.favorite_tab);

            case 2:
                return   context.getResources().getString(R.string.like_tab);
        }*/
        return data[position];
    }

    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

    public void setData(String[] data) {
        Arrays.fill(data, null);
        this.data = data;
        notifyDataSetChanged();
    }

}