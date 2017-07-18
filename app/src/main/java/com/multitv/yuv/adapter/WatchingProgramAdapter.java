package com.multitv.yuv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.multitv.yuv.R;
import com.multitv.yuv.models.watching_get.Content;
import com.multitv.yuv.utilities.Tracer;

/**
 * Created by root on 9/9/16.
 */
public class WatchingProgramAdapter extends RecyclerView.Adapter<WatchingProgramAdapter.ViewHolder> {

    private List<Content> watchingContentList = new ArrayList<>();
    private Context context;

    public WatchingProgramAdapter(Context context, List<Content> watchingContentList) {
        this.watchingContentList = watchingContentList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watching_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Content content = watchingContentList.get(position);

        if (content.getThumbnail() != null && !content.getThumbnail().getSmall().isEmpty()) {
            Picasso.with(context).load(content.getThumbnail().getSmall()).
                    placeholder(R.mipmap.place_holder).error(R.mipmap.place_holder)
                    .into(holder.videoThumbnail);
        }

        if (content.getTotalDuration() != null && !content.getTotalDuration().isEmpty()) {
            long watchedDuration = Long.parseLong(content.getWatchedDuration());
            long totalDuration = Long.parseLong(content.getTotalDuration());

            String watchedDurationString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(watchedDuration),
                    TimeUnit.MILLISECONDS.toMinutes(watchedDuration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(watchedDuration) % TimeUnit.MINUTES.toSeconds(1));

            String totalDurationString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalDuration),
                    TimeUnit.MILLISECONDS.toMinutes(totalDuration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(totalDuration) % TimeUnit.MINUTES.toSeconds(1));

            holder.duration.setText(totalDurationString);
            holder.watched.setText(context.getResources().getString(R.string.watch_text) + watchedDurationString);
        }

        if (content.getTitle() != null && !content.getTitle().isEmpty())
            holder.title.setText(content.getTitle());
        else
            holder.title.setText(context.getResources().getString(R.string.not_available_text));

        if (content.getDes() != null && !content.getDes().isEmpty())
            holder.subtitle.setText(content.getDes());
        else
            holder.subtitle.setText(context.getResources().getString(R.string.not_available_text));

        Tracer.error("MKR", "onBindViewHolder: content.getMeta().getType()  " + content.getMeta().getType());
        Tracer.error("MKR", "onBindViewHolder: content.getMeta().getGenre()  " + content.getMeta().getGenre());
        if (content.getSource() != null && !content.getSource().isEmpty())
            holder.category.setText(content.getSource());
        else
            holder.category.setText(context.getResources().getString(R.string.not_available_text));

        if (content.getMeta().getGenre() != null && !content.getMeta().getGenre().isEmpty())
            holder.genre.setText(content.getMeta().getGenre());
        else
            holder.genre.setText("");
    }

    @Override
    public int getItemCount() {
        return watchingContentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView videoThumbnail;
        public TextView duration, title, subtitle, watched, category, genre;

        public ViewHolder(View itemView) {
            super(itemView);

            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail_iv);
            duration = (TextView) itemView.findViewById(R.id.duration_tv);
            title = (TextView) itemView.findViewById(R.id.title_tv);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle_tv);
            watched = (TextView) itemView.findViewById(R.id.watched);
            category = (TextView) itemView.findViewById(R.id.category);
            genre = (TextView) itemView.findViewById(R.id.content_type);
        }
    }
}