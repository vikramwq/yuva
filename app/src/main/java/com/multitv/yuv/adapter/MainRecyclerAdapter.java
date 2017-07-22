package com.multitv.yuv.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.multitv.yuv.R;
import com.multitv.yuv.activity.MoreDataActivity;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.eventbus.MoveToLiveChannelSection;
import com.multitv.yuv.models.ChannelsData;
import com.multitv.yuv.models.SectionDataModel;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.models.home.Thumb;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by multitv on 24-04-2017.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private static String FEATURE_BANNER_DATA_KEY = "feature_key";
    private ArrayList<SectionDataModel> dataList;
    private Context mContext;
    private boolean isFeatureEnable;
    private String layoutOrientation;
    private String TAG = "MainRecyclerAdapter ";
    private String fragmentName;
    private boolean isLoggedIn, isOTPVerified;
    private SharedPreference sharedPreference;
    private ChannelsData channelsData;

    public MainRecyclerAdapter(Context context, ArrayList<SectionDataModel> dataList,
                               boolean isFeatureEnable, String layoutOrientation, String fragmentName) {
        this.dataList = dataList;
        this.mContext = context;
        this.layoutOrientation = layoutOrientation;
        this.isFeatureEnable = isFeatureEnable;
        this.fragmentName = fragmentName;
        sharedPreference = new SharedPreference();
        isLoggedIn = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_OTP_VERIFIED);
    }

    public void setLiveChannels(ChannelsData channelsData) {
        this.channelsData = channelsData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_slider_xml, null);
                return new SliderHolder(view);
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.live_recycler_row, null);
                return new LiveItemRowHolder(view);
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recycler_row, null);
                return new ItemRowHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isFeatureEnable)
            return 0;
        else if (position == 1 && channelsData != null)
            return 1;
        else
            return 2;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        try {
            if (holder != null && holder.getItemViewType() == 0) {
                initilizeBannerSlider(((SliderHolder) holder).featureBannerSlider);
                setFeatureBannerMeta(((SliderHolder) holder).featureBannerSlider, dataList.get(0).getAllItemsInSection());
            } else if (holder != null && holder.getItemViewType() == 1) {
                ((LiveItemRowHolder) holder).liveRecyclerView.setHasFixedSize(true);
                ((LiveItemRowHolder) holder).liveRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                LiveSectionListDataAdapter liveSectionListDataAdapter = new LiveSectionListDataAdapter(mContext,
                        channelsData.getChannels(), channelsData.getProfile_base(),
                        channelsData.getBanner_page());
                ((LiveItemRowHolder) holder).liveRecyclerView.setAdapter(liveSectionListDataAdapter);
                ((LiveItemRowHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new MoveToLiveChannelSection());
                    }
                });
            } else {
                SectionDataModel sectionDataModel = channelsData != null ? dataList.get(i - 1) : dataList.get(i);
                final String sectionName = sectionDataModel.getHeaderTitle();
                final String sectionID = sectionDataModel.getSectionID();
                ArrayList singleSectionItems = sectionDataModel.getAllItemsInSection();
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
        } catch (Exception e) {
            e.printStackTrace();
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

                String url = "";
                if (datalist.get(i).thumbs != null && !datalist.get(i).thumbs.isEmpty()) {
                    Thumb thumb = datalist.get(i).thumbs.get(0);
                    if (thumb != null && thumb.getThumb() != null && !TextUtils.isEmpty(thumb.getThumb().getMedium()))
                        url = thumb.getThumb().getMedium();
                }

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
        int itemCount = dataList != null ? dataList.size() : 0;
        if (channelsData != null && dataList != null && !dataList.isEmpty()) {
            itemCount++;
        }
        return itemCount;
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

    public class LiveItemRowHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemTitle)
        protected TextView itemTitle;
        @BindView(R.id.live_recycler_view)
        protected RecyclerView liveRecyclerView;
        @BindView(R.id.btnMore)
        protected Button btnMore;
        @BindView(R.id.live_progress_bar)
        ProgressBar liveProgressBar;

        public LiveItemRowHolder(View view) {
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