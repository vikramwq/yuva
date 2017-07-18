package com.multitv.yuv.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.utilities.ScreenUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.multitv.yuv.models.home.Recommned_home;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.SingleItemRowHolder> {

    private List<Recommned_home> itemsList;
    private Context mContext;
    private String duration;
    private int size;
    private  long watchedDuration;

    public  void setWatchedDuration(long watchedDuration) {
        this.watchedDuration = watchedDuration;
    }

    public RecommendedAdapter(Context context, List<Recommned_home> itemsList, int size) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.size=size;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_card, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int position) {

        Recommned_home singleItem = itemsList.get(position);
        holder.mTitle.setText(singleItem.title);
        holder.cardView.setLayoutParams(new RecyclerView.LayoutParams
                (ScreenUtils.getScreenWidth(mContext) / 3, RecyclerView.LayoutParams.WRAP_CONTENT));

         //---genure---
        String source = itemsList.get(position).source;
        String genere = itemsList.get(position).meta.genre;
        if (genere != null && !genere.equals(null)&&!genere.isEmpty()) {
            holder.genure_tv.setVisibility(View.VISIBLE);
             holder.genure_tv.setText(genere);
        } else {
            if(source.equals("mp4")) {
                holder.content_type.setVisibility(View.GONE);
                holder.genure_tv.setVisibility(View.VISIBLE);
                holder.genure_tv.setText(mContext.getResources().getString(R.string.home_intex_item));
            }else {
                holder.content_type.setVisibility(View.GONE);
                holder.genure_tv.setVisibility(View.VISIBLE);
                holder.genure_tv.setText(source);
            }
        }

        //---source type-----

        if (source != null && !source.equals(null) && !source.isEmpty()) {
            if(source.equals("mp4"))
            holder.content_type.setText(mContext.getResources().getString(R.string.home_intex_item));
            else
                holder.content_type.setText(source);
        } else {
            holder.content_type.setVisibility(View.GONE);
        }

        //---duration of video----
        duration=String.valueOf(singleItem.duration);
        if (duration!=null&&!duration.equals(null)&&!TextUtils.isEmpty(duration)) {
            System.out.println(duration);
            String splitTime[]=duration.split(":");
            String hours=splitTime[0];
            String minutes=splitTime[1];
            String seconds=splitTime[2];

            if (hours.equals("00")){
              holder.duration_tv.setText(minutes+":"+seconds+" "+mContext.getResources().getString(R.string.minute));
            }else {
                holder.duration_tv.setText(hours + ":" + minutes + " " + mContext.getResources().getString(R.string.hours));
            }
        } else {
             holder.duration_tv.setVisibility(View.GONE);
        }


        //---thumbnail of video----
        if(singleItem != null && singleItem.thumbnail.getSmall() != null && !singleItem.thumbnail.getSmall().isEmpty()) {
            Picasso.with(mContext).load(singleItem.thumbnail.getSmall())
                    .placeholder(R.mipmap.place_holder).error(R.mipmap.place_holder)
                    .resize(holder.mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
                    .into(holder.mImageView);
        } else {
            Picasso.with(mContext).load(R.mipmap.place_holder)
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return (null != itemsList ? size : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)protected CardView cardView;

        @BindView(R.id.genure_tv)protected TextView genure_tv;
        @BindView(R.id.txt_tv_title)protected TextView mTitle;
        @BindView(R.id.thumbnail)protected ImageView mImageView;
        @BindView(R.id.duration_tv)protected TextView duration_tv;
        @BindView(R.id.img_play)protected ImageView mImgPlay;
        /*@BindView(R.id.img_fav)protected ImageView mImgFav;*/
        @BindView(R.id.content_type)protected TextView content_type;

        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}