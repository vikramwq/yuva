package com.multitv.yuv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.utilities.Tracer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.models.home.Live;

/**
 * Created by naseeb on 10/10/2016.
 */

public class MoreLiveAdapter extends RecyclerView.Adapter<MoreLiveAdapter.SingleItemRowHolder> {

    private Context mContext;
    private List<Live> liveList;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public MoreLiveAdapter(Context context, List<Live> liveList, RecyclerView recyclerView) {
        this.liveList = liveList;
        this.mContext = context;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = recyclerView.getAdapter().getItemCount();

                try {
                    lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
                } catch (Exception e) {
                    Tracer.error("Error", "onScrolled: EXCEPTION " + e.getMessage());
                    lastVisibleItem = 0;
                }

                if (!loading && totalItemCount == (lastVisibleItem + 1)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }

                    loading = true;
                }
            }
        });
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_more_data, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        Live live = liveList.get(i);

        if (!TextUtils.isEmpty(live.title))
            holder.mTitle.setText(live.title);

        if (!TextUtils.isEmpty(live.category))
            holder.category.setText(live.category);

        holder.duration.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(live.thumbnail.small)) {
            Picasso.with(mContext).load(live.thumbnail.small)
                    .placeholder(R.mipmap.place_holder_land).error(R.mipmap.place_holder_land)
                    .resize(holder.mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
                    .into(holder.mImageView);
        } else {
            Picasso.with(mContext).load(R.mipmap.place_holder_land)
                    .resize(holder.mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._70sdp))
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return (null != liveList ? liveList.size() : 0);
    }

    static class SingleItemRowHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_tv)
        protected TextView mTitle;
        @BindView(R.id.duration_tv)
        protected TextView duration;
        @BindView(R.id.video_thumbnail_iv)
        protected ImageView mImageView;
        @BindView(R.id.subtitle_tv)
        protected TextView category;

        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

}
