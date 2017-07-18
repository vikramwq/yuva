package com.multitv.yuv.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.MoreDataActivity;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.models.SectionDataModel;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.models.home.Cat_cntn;

/**
 * Created by multitv on 24-04-2017.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static String FEATURE_BANNER_DATA_KEY = "feature_key";
    private ArrayList<SectionDataModel> dataList;
    private Context mContext;
    private boolean isFeatureEnable;
    private String layoutOrientation;
    private String TAG = "MainRecyclerAdapter ";
    private String fragmentName;
    private boolean isLoggedIn, isOTPVerified;
    SharedPreference sharedPreference;


    public MainRecyclerAdapter(Context context, ArrayList<SectionDataModel> dataList, boolean isFeatureEnable, String layoutOrientation, String fragmentName) {
        this.dataList = dataList;
        this.mContext = context;
        this.layoutOrientation = layoutOrientation;
        this.isFeatureEnable = isFeatureEnable;
        this.fragmentName = fragmentName;
        sharedPreference = new SharedPreference();
        isLoggedIn = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_OTP_VERIFIED);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_slider_xml, null);
            SliderHolder holder = new SliderHolder(v);
            return holder;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recycler_row, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

    }

    @Override
    public int getItemViewType(int position) {


        if (position == 0 && isFeatureEnable)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if (holder != null && holder.getItemViewType() == 0) {
            initilizeBannerSlider(((SliderHolder) holder).featureBannerSlider);
            setFeatureBannerMeta(((SliderHolder) holder).featureBannerSlider, dataList.get(0).getAllItemsInSection());

        } else {
            final String sectionName = dataList.get(i).getHeaderTitle();

            final String sectionID = dataList.get(i).getSectionID();

            ArrayList singleSectionItems = dataList.get(i).getAllItemsInSection();


            ((ItemRowHolder) holder).itemTitle.setText(sectionName);

            SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems, layoutOrientation, fragmentName);


            ((ItemRowHolder) holder).mainRecyclerView.setHasFixedSize(true);
            ((ItemRowHolder) holder).mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            ((ItemRowHolder) holder).mainRecyclerView.setAdapter(itemListDataAdapter);
            if (sectionID != null && sectionID.equalsIgnoreCase("-2"))
                ((ItemRowHolder) holder).btnMore.setVisibility(View.GONE);


            ((ItemRowHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToMoreData(sectionName, sectionID);
                }
            });
        }
    }

    private void initilizeBannerSlider(SliderLayout featureBannerSlider) {
        featureBannerSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        featureBannerSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        featureBannerSlider.setCustomAnimation(new DescriptionAnimation());
        featureBannerSlider.setDuration(4000);
        featureBannerSlider.addOnPageChangeListener(this);
    }

    private void setFeatureBannerMeta(SliderLayout featureBannerSlider, ArrayList<Cat_cntn> datalist) {
        if (datalist != null
                && datalist.size() != 0) {
            Tracer.error(TAG, "feature_banner: FEATURE BANNER SIZE " + datalist.size());
            for (int i = 0; i < datalist.size(); i++) {
                if (mContext == null)
                    return;
                TextSliderView textSliderView = new TextSliderView(mContext);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putParcelable(FEATURE_BANNER_DATA_KEY, datalist.get(i));

                String url = datalist.get(i).thumbnail.large;
                if (url != null && !url.equals(null) && !url.equals("")) {
                    textSliderView
                            .description(datalist.get(i).title)
                            .image(url)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);
                } else {
                    textSliderView
                            .description(datalist.get(i).title)
                            .image(R.mipmap.place_holder)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);

                }
                featureBannerSlider.addSlider(textSliderView);
            }

            featureBannerSlider.setVisibility(View.VISIBLE);
        } else
            featureBannerSlider.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemTitle)
        protected TextView itemTitle;
        @BindView(R.id.main_recycler_view)
        protected RecyclerView mainRecyclerView;
        @BindView(R.id.btnMore)
        protected Button btnMore;


        public ItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class SliderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feature_slider)
        protected SliderLayout featureBannerSlider;

        public SliderHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void navigateToMoreData(String catName, String catID) {
        Intent intent = new Intent(mContext, MoreDataActivity.class);
        if (catID != null && !catID.equalsIgnoreCase("-1"))
            intent.putExtra("more_data_tag", 4);
        else
            intent.putExtra("more_data_tag", 1);

        intent.putExtra("cat_id", catID);
        intent.putExtra("catName", catName);
        mContext.startActivity(intent);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {


        if (isOTPVerified) {
            Cat_cntn contentObj = slider.getBundle().getParcelable(FEATURE_BANNER_DATA_KEY);
            Intent intent = new Intent(mContext, MultiTvPlayerActivity.class);
            intent.putExtra(Constant.CONTENT_TRANSFER_KEY, contentObj);
            intent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
            mContext.startActivity(intent);
        } else {
            if ("Movie Promos".equalsIgnoreCase(fragmentName)) {
                Cat_cntn contentObj = slider.getBundle().getParcelable(FEATURE_BANNER_DATA_KEY);
                Intent intent = new Intent(mContext, MultiTvPlayerActivity.class);
                intent.putExtra(Constant.CONTENT_TRANSFER_KEY, contentObj);
                intent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                mContext.startActivity(intent);
            } else {
                Utilities.showLoginDailog(mContext);

            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {

    }
}