package com.multitv.yuv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.Utilities;

/**
 * Created by root on 24/10/16.
 */

public class MoreHomeDisplayCategoryAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    String thumbUrl;
    RecyclerView recyclerView;
    private List<Cat_cntn> itemsList;
    private Context mContext;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean isLoggedIn, isOTPVerified;
    SharedPreference sharedPreference;

    public MoreHomeDisplayCategoryAdapter(Context context, List<Cat_cntn> recomendedArrayList, RecyclerView mrecyclerView) {
        this.itemsList = recomendedArrayList;
        this.mContext = context;
        recyclerView = mrecyclerView;
        sharedPreference = new SharedPreference();
        isLoggedIn = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_OTP_VERIFIED);

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

    public static int getScreenWidth(Context context) {
        int screenWidth;
        //if (screenWidth == 0) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        System.out.println("=====screenWidth=========" + screenWidth);
        // }
        return screenWidth;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_more_data, parent, false);

            return new MoreHomeDisplayCategoryAdapter.SingleItemRowHolder(v);
        } else if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.load_more_progress_item, parent, false);

            return new MoreHomeDisplayCategoryAdapter.ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) {
            final Cat_cntn mContent = itemsList.get(position);

            if (mContent != null) {
                if (mContent.title != null && !mContent.title.isEmpty())
                    ((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).mTitle.setText(mContent.title);
                else
                    ((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).mTitle.setVisibility(View.GONE);
                String description = mContent.des;
                String createdDate = "";
                if (description != null && !description.isEmpty())
                    ((SingleItemRowHolder) holder).content_type.setText(description.trim());
                else
                    ((SingleItemRowHolder) holder).content_type.setVisibility(View.GONE);


//                if (mContent.meta.genre != null && !mContent.meta.genre.isEmpty()) {
//                    ((SingleItemRowHolder) holder).genre_sony.setVisibility(View.VISIBLE);
//                    ((SingleItemRowHolder) holder).genre_sony.setText(mContent.meta.genre);
//                } else
//                    ((SingleItemRowHolder) holder).genre_sony.setVisibility(View.GONE);


                if (createdDate != null && !createdDate.isEmpty())
                    ((SingleItemRowHolder) holder).date_created.setText(description.trim());
                else
                    ((SingleItemRowHolder) holder).date_created.setVisibility(View.GONE);

                String duration = mContent.duration;
                if (!TextUtils.isEmpty(duration)) {
                    System.out.println(duration);
                    String splitTime[] = duration.split(":");
                    String hours = splitTime[0];
                    String minutes = splitTime[1];
                    String seconds = splitTime[2];

                    if (hours.equals("00")) {
                        ((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).duration.setText(minutes + ":" + seconds + " " + mContext.getResources().getString(R.string.minute));
                    } else {
                        ((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).duration.setText(hours + ":" + minutes + " " + mContext.getResources().getString(R.string.hours));
                    }
                } else {
                    ((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).duration.setVisibility(View.GONE);
                }

                //((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).duration.setText(duration);


                if (mContent.thumbnail.large != null && !mContent.thumbnail.large.isEmpty()) {
                    thumbUrl = mContent.thumbnail.large;
                }


                if (thumbUrl != null && !thumbUrl.equals(null) && !thumbUrl.equals("")) {

                    Picasso
                            .with(mContext)
                            .load(thumbUrl)
                            .placeholder(R.mipmap.place_holder)
                            .error(R.mipmap.place_holder)
                            .resize(((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
                            .into(((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).mImageView);
                } else {
                    Picasso.with(mContext)
                            .load(R.mipmap.place_holder)
                            .into(((MoreHomeDisplayCategoryAdapter.SingleItemRowHolder) holder).mImageView);

                }


                ((SingleItemRowHolder) holder).card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (isOTPVerified) {
                            String videoUrl = mContent.url.toString();
                            try {
                                if (!videoUrl.equals("")) {
                                    Intent videoIntent = new Intent(mContext, MultiTvPlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, mContent);
                                    videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
//                                videoIntent.putExtra("WATCHED_DURATION", watchedDuration);
                                    mContext.startActivity(videoIntent);
                                } else {
                                    Toast.makeText(mContext, "No Record Found", Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception ex) {
                                Tracer.error("error", ex.getMessage());
                            }
                        } else {
                            Utilities.showLoginDailog(mContext);
                        }
                    }
                });


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

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;

    }

    public void setVideoList(List<Cat_cntn> recomendedMlist) {
        this.itemsList = recomendedMlist;
        Log.i("setVideoList", "setVideoList: " + itemsList.size());
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);

            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            //progressBar1 = (ProgressBar) v.findViewById(R.id.progressBar2);
        }
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
        protected TextView content_type;
        @BindView(R.id.genre_sony)
        protected TextView genre_sony;
        @BindView(R.id.date_created)
        protected TextView date_created;


        @BindView(R.id.card_view)
        protected CardView card_view;


        //  protected LinearLayout cardView;
        // protected ImageView itemImage;
        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

    }
}