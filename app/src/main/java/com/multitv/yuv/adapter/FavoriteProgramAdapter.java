package com.multitv.yuv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.utilities.Tracer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import com.multitv.yuv.models.video.Content;
import com.multitv.yuv.utilities.VideoSourceUtils;

/**
 * Created by root on 9/9/16.
 */
public class FavoriteProgramAdapter extends RecyclerView.Adapter {

    private List<Content> favorite_list = new ArrayList<>();
    private Context context;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    String thumbUrl;
    RecyclerView recyclerView;


    public FavoriteProgramAdapter(Context context1, List<Content> items,RecyclerView mrecyclerView) {
        this.favorite_list = items;
        this.context = context1;
        recyclerView = mrecyclerView;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = recyclerView.getAdapter().getItemCount();

                try {
                    lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
                } catch (Exception e) {
                    Tracer.error("Error", "onScrolled: EXCEPTION " + e.getMessage()); lastVisibleItem = 0;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.favorite_row, parent, false);

            return new FavoriteProgramAdapter.SingleItemRowHolder(v);
        } else if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.load_more_progress_item, parent, false);

            return new FavoriteProgramAdapter.ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return favorite_list.get(position) == null ? VIEW_PROG : VIEW_ITEM;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FavoriteProgramAdapter.SingleItemRowHolder) {
        final Content content = favorite_list.get(position);
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).tittle.setText(content.title);

        if (content.source != null &&!content.source.equals("null")&& !content.source.isEmpty())
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).type.setText(VideoSourceUtils.getVideoSource(content.source));
        else
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).type.setVisibility(View.GONE);

        if (content.duration != null && !content.duration.isEmpty()) {
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).duration_tv.setText(String.valueOf(content.duration));
            String splitTime[] = content.duration.split(":");
            String hours = splitTime[0];
            String minutes = splitTime[1];
            String seconds = splitTime[2];

            if (hours.equals("00")) {
                ((FavoriteProgramAdapter.SingleItemRowHolder) holder).duration_tv.setText(minutes + ":" + seconds + " " + context.getResources().getString(R.string.minute));
            } else {
                ((FavoriteProgramAdapter.SingleItemRowHolder) holder).duration_tv.setText(hours + ":" + minutes + " " + context.getResources().getString(R.string.hours));
            }

        } else {
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).duration_tv.setVisibility(View.GONE);
        }

        if (content.meta.type != null && !content.meta.type.isEmpty())
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).category.setText(content.meta.type);
        else
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).category.setVisibility(View.GONE);

        if (content.meta.genre != null && !content.meta.genre.isEmpty())
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).genre.setText(content.meta.genre);
        else
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).genre.setVisibility(View.GONE);

        if (content.thumbnail != null
                && content.thumbnail.large != null
                && !content.thumbnail.large.isEmpty()) {
            Picasso
                    .with(context)
                    .load(content.thumbnail.large)
                    .placeholder(R.mipmap.place_holder)
                    .error(R.mipmap.place_holder)
                    .resize(((FavoriteProgramAdapter.SingleItemRowHolder) holder).banner.getWidth(), (int) context.getResources().getDimension(R.dimen._100sdp))
                    .into(((FavoriteProgramAdapter.SingleItemRowHolder) holder).banner);
        } else
            ((FavoriteProgramAdapter.SingleItemRowHolder) holder).banner.setImageResource(R.mipmap.place_holder);
    }
    }

    @Override
    public int getItemCount() {
        return favorite_list.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tittle, duration_tv, subtitle_tv, category, genre;
        protected TextView type;
        private ImageView banner, favimg;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tittle = (TextView) view.findViewById(R.id.title_tv);
            this.duration_tv = (TextView) view.findViewById(R.id.duration_tv);

            this.type = (TextView) view.findViewById(R.id.type);
            this.banner = (ImageView) view.findViewById(R.id.video_thumbnail_iv);

            this.category = (TextView) view.findViewById(R.id.category);
            this.genre = (TextView) view.findViewById(R.id.content_type);
            //this.banner.setLayoutParams(new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT));

        }
    }
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);

            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            //progressBar1 = (ProgressBar) v.findViewById(R.id.progressBar2);
        }
    }

}