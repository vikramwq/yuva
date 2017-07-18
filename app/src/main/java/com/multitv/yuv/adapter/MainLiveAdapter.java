//package com.barwin.app.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.barwin.app.R;
//import com.barwin.app.activity.MoreDataActivity;
//import com.barwin.app.activity.MultiTvPlayerActivity;
//import com.barwin.app.models.SectionDataModel;
//import com.barwin.app.models.SectionDataModel1;
//import com.barwin.app.models.home.Cat_cntn;
//import com.barwin.app.sharedpreference.SharedPreference;
//import com.barwin.app.utilities.Constant;
//import com.barwin.app.utilities.Tracer;
//import com.barwin.app.utilities.Utilities;
//import com.daimajia.slider.library.Animations.DescriptionAnimation;
//import com.daimajia.slider.library.Indicators.PagerIndicator;
//import com.daimajia.slider.library.SliderLayout;
//import com.daimajia.slider.library.SliderTypes.BaseSliderView;
//import com.daimajia.slider.library.SliderTypes.TextSliderView;
//import com.daimajia.slider.library.Tricks.ViewPagerEx;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by arungoyal on 30/06/17.
// */
//
//public class MainLiveAdapter extends RecyclerView.Adapter {
//
//    private static String FEATURE_BANNER_DATA_KEY = "feature_key";
//    private ArrayList<SectionDataModel1> dataList;
//    private Context mContext;
//    private boolean isFeatureEnable;
//    private String layoutOrientation;
//    private String TAG = "MainRecyclerAdapter ";
//    private String fragmentName;
//    SharedPreference sharedPreference;
//
//
//    public MainLiveAdapter(Context context, ArrayList<SectionDataModel1> dataList, boolean isFeatureEnable, String fragmentName) {
//        this.dataList = dataList;
//        this.mContext = context;
//        this.isFeatureEnable = isFeatureEnable;
//        this.fragmentName = fragmentName;
//        sharedPreference = new SharedPreference();
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recycler_row, null);
//        MainLiveAdapter.ItemRowHolder mh = new MainLiveAdapter.ItemRowHolder(v);
//        return mh;
//
//
//    }
//
//
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
//
//
//        final String sectionName = dataList.get(i).getHeaderTitle();
//
//        final String sectionID = dataList.get(i).getSectionID();
//
//        ArrayList singleSectionItems = dataList.get(i).getAllItemsInSection();
//
//
//        ((MainRecyclerAdapter.ItemRowHolder) holder).itemTitle.setText(sectionName);
//
//        LiveSectionListDataAdapter itemListDataAdapter = new LiveSectionListDataAdapter(mContext, singleSectionItems, layoutOrientation, fragmentName);
//
//
//        ((MainLiveAdapter.ItemRowHolder) holder).mainRecyclerView.setHasFixedSize(true);
//        ((MainLiveAdapter.ItemRowHolder) holder).mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//        ((MainLiveAdapter.ItemRowHolder) holder).mainRecyclerView.setAdapter(itemListDataAdapter);
//        if (sectionID != null && sectionID.equalsIgnoreCase("-2"))
//            ((MainLiveAdapter.ItemRowHolder) holder).btnMore.setVisibility(View.GONE);
//
//
//
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return (null != dataList ? dataList.size() : 0);
//    }
//
//    public class ItemRowHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.itemTitle)
//        protected TextView itemTitle;
//        @BindView(R.id.main_recycler_view)
//        protected RecyclerView mainRecyclerView;
//        @BindView(R.id.btnMore)
//        protected Button btnMore;
//
//
//        public ItemRowHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//
//
//}