package com.multitv.yuv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.utilities.ScreenUtils;
import com.multitv.yuv.utilities.Tracer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.models.video.Content;

/**
 * Created by multitv on 24-04-2017.
 */

public class ContentRecyclerAdapter extends RecyclerView.Adapter<ContentRecyclerAdapter.ItemHolder> {


    private Context mContext;
    private ArrayList<Content> dataList;

    public ContentRecyclerAdapter(Context context, ArrayList<Content> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_card, null);
        ItemHolder mh = new ItemHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemRowHolder, final int position) {

        Content content = dataList.get(position);
        if (itemRowHolder instanceof ContentRecyclerAdapter.ItemHolder) {

            ((ContentRecyclerAdapter.ItemHolder) itemRowHolder).cardView.setLayoutParams(new RecyclerView.LayoutParams
                    (ScreenUtils.getScreenWidth(mContext) / 3, RecyclerView.LayoutParams.WRAP_CONTENT));


            String url = content.thumbnail.small;


//            ((SingleItemRowHolder) itemRowHolder).duration_tv.setText(content.duration);


            String duration = String.valueOf(content.duration);

            if (duration != null && !duration.equals(null)) {
                String splitTime[] = duration.split(":");
                String hours = splitTime[0];
                String minutes = splitTime[1];
                String seconds = splitTime[2];

                if (hours.equals("00")) {
                    ((ContentRecyclerAdapter.ItemHolder) itemRowHolder).contentTitle.setText(minutes + ":" + seconds + " " + mContext.getResources().getString(R.string.minute));
                } else {
                    ((ContentRecyclerAdapter.ItemHolder) itemRowHolder).contentTitle.setText(hours + ":" + minutes + " " + mContext.getResources().getString(R.string.hours));
                }
            } else {
                ((ContentRecyclerAdapter.ItemHolder) itemRowHolder).contentTitle.setVisibility(View.GONE);
            }


            if (url != null && !url.equals(null) && !url.equals("")) {
                Picasso
                        .with(mContext)
                        .load(url)
                        .placeholder(R.mipmap.place_holder)
                        .error(R.mipmap.place_holder)
                        .resize(((ContentRecyclerAdapter.ItemHolder) itemRowHolder).banner.getWidth(), (int) mContext.getResources().getDimension(R.dimen._85sdp))
                        .into(((ContentRecyclerAdapter.ItemHolder) itemRowHolder).banner);
            } else {
                Picasso.with(mContext)
                        .load(R.mipmap.place_holder)
                        .into(((ContentRecyclerAdapter.ItemHolder) itemRowHolder).banner);

            }


            ((ContentRecyclerAdapter.ItemHolder) itemRowHolder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String des = null, start_cast = null;

                    String VIDEO_URL = dataList.get(position).url.toString();
                    String CONTENT_TYPE = dataList.get(position).source.toString();
                    String title = dataList.get(position).title;
                    String type = dataList.get(position).media_type;
                    String content_id = "" + dataList.get(position).categoryIdsList.get(0);
                    if (dataList.get(position).des != null && dataList.get(position).des != null && !dataList.get(position).des.equals("null") && !dataList.get(position).des.isEmpty()) {
                        des = dataList.get(position).des;
                    }
                    if (dataList.get(position).thumbnail.large != null && !dataList.get(position).thumbnail.large.equals("null") && !dataList.get(position).thumbnail.large.isEmpty()) {
                        String thumbnail = dataList.get(position).thumbnail.large;
                    }
                    String fav_item = dataList.get(position).favorite.toString();
                    String like = dataList.get(position).likes + "";
                    String video_id = dataList.get(position).id;
                    if (dataList.get(position).meta.star_cast != null && !dataList.get(position).meta.star_cast.equals("null") && !dataList.get(position).meta.star_cast.isEmpty()) {
                        start_cast = dataList.get(position).meta.star_cast;
                    }
                    // Tracer.error("url",rating);
                    Tracer.error("***videofragment-url***", VIDEO_URL);
                    Tracer.error("***like***", like);
                    Tracer.error("***like-videofragment**", dataList.get(position).likes_count);

                    try {
                        if (!VIDEO_URL.equals("")) {
                            if (mContext == null)
                                return;
                            Intent videoIntent = new Intent(mContext, MultiTvPlayerActivity.class);
                            videoIntent.putExtra("VIDEO_URL", VIDEO_URL);
                            videoIntent.putExtra("descreption", des);
                            videoIntent.putExtra("title", title);
                            videoIntent.putExtra("like", like);
                            videoIntent.putExtra("start_cast", start_cast);
                            videoIntent.putExtra("type", type);
                            videoIntent.putExtra("video_id", video_id);
                            videoIntent.putExtra("likes_count", dataList.get(position).likes_count);
                            videoIntent.putExtra("content_id", content_id);
                            videoIntent.putExtra("position", position);
                            videoIntent.putExtra("fav_item", fav_item);
                            videoIntent.putExtra("thumbnail", dataList.get(position).thumbnail.large);
                            videoIntent.putExtra("content_type", CONTENT_TYPE);
                            videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
//                            videoIntent.putExtra("WATCHED_DURATION", watchedDuration);
                            videoIntent.putExtra("SOCIAL_LIKES", dataList.get(position).social_like);
                            videoIntent.putExtra("SOCIAL_VIEWS", dataList.get(position).social_view);
                            mContext.startActivity(videoIntent);
                        } else {
                            if (mContext == null)
                                return;
                            Toast.makeText(mContext, "No Record Found", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception ex) {
                        Tracer.error("error", ex.getMessage());
                    }


                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemImage)
        protected ImageView itemImage;
        @BindView(R.id.card_view)
        protected CardView cardView;
        @BindView(R.id.duration_tv)
        protected TextView contentDuration;
        @BindView(R.id.content_title)
        protected TextView contentTitle;
        @BindView(R.id.thumbnail)
        protected ImageView banner;


        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
