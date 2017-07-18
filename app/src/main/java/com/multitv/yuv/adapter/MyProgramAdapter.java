package com.multitv.yuv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.multitv.yuv.fragment.FavoriteProgramFragment;
import com.multitv.yuv.fragment.WatchingProgramFragment;


public class MyProgramAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs=2;
    Context context;
    public MyProgramAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FavoriteProgramFragment tab1 = new FavoriteProgramFragment();
                return tab1;
            case 1:
                WatchingProgramFragment tab2 = new WatchingProgramFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="Favorite";
                break;
            case 1:
                title="Watching";
                break;
        }

        return title;
    }

}
