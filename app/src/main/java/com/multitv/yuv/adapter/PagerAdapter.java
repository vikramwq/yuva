package com.multitv.yuv.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.fragment.ContentFragment;
import com.multitv.yuv.fragment.LiveFragment;
import com.multitv.yuv.models.categories.Vod;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;

import java.util.ArrayList;


import com.multitv.yuv.fragment.LandingFragment;
import com.multitv.yuv.utilities.AppConfig;


/**
 * Created by Sunil .
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = AppConfig.BASE_TAG + ".PagerAdapter";

    private Context context;
    private ArrayList<Vod> topMenuArrayList;
    private boolean isDashVersionChanged;
    private boolean isContentVersionChanged;

    public PagerAdapter(FragmentManager fm, Context context, ArrayList<Vod> topMenuArrayList, boolean isDashVersionChanged, boolean isContentVersionChanged) {
        super(fm);
        this.context = context;
//        topMenuArrayList = new ArrayList<>();
        Vod vod = new Vod();
        vod.name = "Home";
        vod.id = "0";
        topMenuArrayList.add(0, vod);
        this.topMenuArrayList = topMenuArrayList;
        this.isDashVersionChanged = isDashVersionChanged;
        this.isContentVersionChanged = isContentVersionChanged;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        String tabTitle = topMenuArrayList.get(position).name;
        Bundle bundle = new Bundle();
//        bundle.putString(EXTRA_ICON_URL, getThumb(tabTitle));
        bundle.putString(Constant.EXTRA_NAME, getName(tabTitle));
        switch (tabTitle) {
            case "Home":
                frag = new LandingFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("isDataUpdated", isDashVersionChanged);
                frag.setArguments(bundle1);
                break;

            case "Channels":
                frag = new LiveFragment();


                break;
            default:
                frag = ContentFragment.newInstance(topMenuArrayList.get(position), tabTitle.trim(), isContentVersionChanged);
                break;
        }
        return frag;
    }

    private String getName(String fragmentId) {
        try {
            if (topMenuArrayList != null && topMenuArrayList.size() > 0) {
                for (int i = 0; i < topMenuArrayList.size(); i++) {
                    String title = topMenuArrayList.get(i).name;
                    if (title.trim().equalsIgnoreCase(fragmentId.trim())) {
                        return title;
                    }
                }
            }
        } catch (Exception e) {
            Tracer.error(TAG, "loadFragment: " + e.getMessage());
        }
        return "UNKNOWN";
    }


    @Override
    public int getCount() {
        return topMenuArrayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return topMenuArrayList.get(position).name;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);

        TextView tv = (TextView) v.findViewById(R.id.tab_item_txt);
        tv.setText(topMenuArrayList.get(position).name);

        ImageView img = (ImageView) v.findViewById(R.id.tab_item_view);
        img.setVisibility(View.GONE);


        return v;
    }
}
