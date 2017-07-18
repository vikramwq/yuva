package com.multitv.yuv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.adapter.MyProgramAdapter;
import com.multitv.yuv.interfaces.OnShowPopupWindowListener;
import com.multitv.yuv.models.MyProgram;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.utilities.Constant;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
//import static Constant.HTMLTEXT;

/**
 * Created by root on 9/9/16.
 */
public class MyProgramsFragment extends Fragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private MyProgramAdapter adapter;
    public Video videoData;
    private MyProgram myProgram;

    public MyProgramsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_programs_screen, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() == null)
            return null;
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(HTMLTEXT);

        viewPager.setOffscreenPageLimit(1);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //LOADER
        view.findViewById(R.id.fragment_my_program_outer_option_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof OnShowPopupWindowListener) {
                    ((OnShowPopupWindowListener) getActivity()).onShowPopupWindowListenerShowMenuItemOptionPopup(view);
                }
            }
        });
        if (getArguments() != null && getArguments().getString(Constant.EXTRA_ICON_URL) != null && !getArguments().getString(Constant.EXTRA_ICON_URL).trim().isEmpty()) {
            int widthAndHeightOfIcon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
            Picasso.with(getActivity()).load(getArguments().getString(Constant.EXTRA_ICON_URL).trim())
                    .placeholder(R.mipmap.place_holder).error(R.mipmap.place_holder)
                    .resize(widthAndHeightOfIcon, widthAndHeightOfIcon)
                    .into((ImageView) view.findViewById(R.id.home_option_menu_icon_imageView));
            ((TextView) view.findViewById(R.id.home_option_menu_icon_textView)).setText(getArguments().getString(Constant.EXTRA_NAME));
        }
        init();
    }


    public void init() {
        FragmentManager manager = getChildFragmentManager();
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.favorite_tab_text)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.watching_tab_text)));

        adapter = new MyProgramAdapter(manager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(adapter);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
               /* if(tabLayout.isSelected()) {
                    tabLayout.setTabTextColors(getResources().getColorStateList(R.color.tab_background));
                }else{
                    tabLayout.setTabTextColors(getResources().getColorStateList(R.color.tab_text_color));

                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
