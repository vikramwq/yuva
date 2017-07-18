package com.multitv.yuv.adapter;



import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.net.InternetDomainName;
import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentAdapter extends RecyclerView.Adapter {
    private List<Cat_cntn> itemsList;
    private Context mContext;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean isLoggedIn, isOTPVerified;
    SharedPreference sharedPreference;

    public ContentAdapter(Context context, List<Cat_cntn> recomendedArrayList
    ) {
        this.itemsList = recomendedArrayList;
        this.mContext = context;
        sharedPreference = new SharedPreference();
        isLoggedIn = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_OTP_VERIFIED);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_more_data, parent, false);

            return new ContentAdapter.SingleItemRowHolder(v);
        } else if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.load_more_progress_item, parent, false);

            return new ContentAdapter.ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentAdapter.SingleItemRowHolder) {
            final Cat_cntn content = itemsList.get(position);
            if (content != null) {
                if (content.title != null && !content.title.isEmpty())
                    ((ContentAdapter.SingleItemRowHolder) holder).mTitle.setText(content.title);
                else
                    ((ContentAdapter.SingleItemRowHolder) holder).mTitle.setVisibility(View.GONE);

                String description = content.des;
                if (description != null && !TextUtils.isEmpty(description)) {

                    Log.d(this.getClass().getName(),"description==========>>>>  "+content.title+"        "+description);
                    ((ContentAdapter.SingleItemRowHolder) holder).source_type.setText(description.trim());
                }
//                else
//                    ((ContentAdapter.SingleItemRowHolder) holder).source_type.setVisibility(View.GONE);

//                if (content.meta.genre != null && !content.meta.genre.trim().isEmpty()) {
//                    ((SingleItemRowHolder) holder).genre_sony.setVisibility(View.VISIBLE);
//                    ((SingleItemRowHolder) holder).genre_sony.setText(content.meta.genre);
//                } else
//                    ((SingleItemRowHolder) holder).genre_sony.setVisibility(View.GONE);

                String duration = content.duration;
                if (duration != null && !duration.equals(null) && !TextUtils.isEmpty(duration)) {

                    String splitTime[] = duration.split(":");
                    String hours = splitTime[0];
                    String minutes = splitTime[1];
                    String seconds = splitTime[2];

                    if (hours.equals("00")) {
                        ((ContentAdapter.SingleItemRowHolder) holder).duration.setText(minutes + ":" + seconds + " " + mContext.getResources().getString(R.string.minute));
                    } else {
                        ((ContentAdapter.SingleItemRowHolder) holder).duration.setText(hours + ":" + minutes + " " + mContext.getResources().getString(R.string.hours));
                    }
                } else {
                    ((ContentAdapter.SingleItemRowHolder) holder).duration.setVisibility(View.GONE);
                }
                String url = null;

                if (content.thumbs != null && content.thumbs.size() > 0)
                    for (int i = 0; i < content.thumbs.size(); i++) {
                        if (content.thumbs.get(i).getName() != null && content.thumbs.get(i).getName().equalsIgnoreCase("rectangle")) {
                            url = content.thumbs.get(i).getThumb().getMedium();
                            break;
                        } else {
                            if (content.thumbs.get(i).getName() != null && content.thumbs.get(i).getName().equalsIgnoreCase("default"))
                                url = content.thumbs.get(i).getThumb().getMedium();
                        }
                    }
                else
                    url = content.thumbnail.medium;

                if (url != null && !url.isEmpty()) {
                    Picasso
                            .with(mContext)
                            .load(url)
                            .placeholder(R.mipmap.place_holder_land)
                            .error(R.mipmap.place_holder_land)
                            .resize(((ContentAdapter.SingleItemRowHolder) holder).mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
                            .into(((ContentAdapter.SingleItemRowHolder) holder).mImageView);

                } else {
                    Picasso.with(mContext)
                            .load(R.mipmap.place_holder_land)
                            .into(((ContentAdapter.SingleItemRowHolder) holder).mImageView);

                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return itemsList == null ? 0 : itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position) == null ? VIEW_PROG : VIEW_ITEM;
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        // @BindView(R.id.txt_tv_time)protected TextView mTime;
        @BindView(R.id.title_tv)
        protected TextView mTitle;
        @BindView(R.id.duration_tv)
        protected TextView duration;
        @BindView(R.id.video_thumbnail_iv)
        protected ImageView mImageView;
        @BindView(R.id.subtitle_tv)
        protected TextView source_type;
        @BindView(R.id.genre_sony)
        protected TextView genre_sony;
        @BindView(R.id.date_created)
        protected TextView date_created;

        @BindView(R.id.card_view)
        protected CardView card_view;


        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent videoIntent = new Intent(mContext, MultiTvPlayerActivity.class);
                    videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, itemsList.get(getAdapterPosition()));
                    videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                    mContext.startActivity(videoIntent);


                }
            });
        }

    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
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