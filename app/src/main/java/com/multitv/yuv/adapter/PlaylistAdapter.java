package com.multitv.yuv.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.api.services.youtube.model.Playlist;
import com.multitv.yuv.R;
import com.multitv.yuv.activity.ContentScreen;
import com.multitv.yuv.models.PlaylistItem;

import java.net.URI;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 17-07-2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemViewHolder> {
    private Activity mContext;
    private List<PlaylistItem> displayCategoryList;

    public PlaylistAdapter(Activity context, List<PlaylistItem> disCategoryList) {
        mContext = context;
        this.displayCategoryList = disCategoryList;
    }

    @Override
    public PlaylistAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaylistAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaylistAdapter.ItemViewHolder holder, final int position) {
        PlaylistItem playlist = displayCategoryList.get(position);
        holder.titleTxt.setText(playlist.getTitle().trim());
        holder.genureTextView.setText(playlist.getDescription());
        holder.titleTxt.setLines(2);
        holder.titleTxt.setIncludeFontPadding(false);
        holder.countTxt.setText(playlist.getTotal_video());

        if (!TextUtils.isEmpty(playlist.getThumb()))
            holder.thumbnailImg.setImageURI(Uri.parse(playlist.getThumb()));


    }

    @Override
    public int getItemCount() {
        return displayCategoryList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        protected RelativeLayout cardView;

        @BindView(R.id.video_thumbnail_iv)
        protected SimpleDraweeView thumbnailImg;

        @BindView(R.id.title_tv)
        protected TextView titleTxt;

        @BindView(R.id.subtitle_tv)
        protected TextView genureTextView;

        @BindView(R.id.countTxt)
        protected TextView countTxt;

        public ItemViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, ContentScreen.class);
                    intent.putExtra("playlist_id", displayCategoryList.get(getAdapterPosition()).getId());
                    intent.putExtra("playlist_name", displayCategoryList.get(getAdapterPosition()).getTitle());
                    mContext.startActivity(intent);


                }
            });
        }
    }


}