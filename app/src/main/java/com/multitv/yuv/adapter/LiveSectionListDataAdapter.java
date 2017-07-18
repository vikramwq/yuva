package com.multitv.yuv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.PlayerActivity;
import com.multitv.yuv.activity.PlaylistScreen;
import com.multitv.yuv.models.Channel;
import com.multitv.yuv.models.home.Live;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.ScreenUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arungoyal on 30/06/17.
 */

public class LiveSectionListDataAdapter extends RecyclerView.Adapter<LiveSectionListDataAdapter.SingleItemRowHolder> {

    private List<Channel> itemsList;
    private Context mContext;
    private String layoutOrientation;
    private int size;
    private String fragmentName;
    boolean isLoggedIn, isOTPVerified;
    SharedPreference sharedPreference;
    private String mediaBaseUrl;
    private String bannerbaseUrl;


    public LiveSectionListDataAdapter(Context context, List<Channel> itemsList, String mediaBaseUrl, String bannerbaseUrl) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.layoutOrientation = layoutOrientation;
        this.fragmentName = fragmentName;
        sharedPreference = new SharedPreference();
        this.mediaBaseUrl = mediaBaseUrl;
        this.bannerbaseUrl = bannerbaseUrl;

//        Log.d(this.getClass().getName(),"fragmentName========="+fragmentName);


    }

    @Override
    public int getItemViewType(int position) {
        return ((layoutOrientation != null && layoutOrientation.equalsIgnoreCase("rectangle")) ? 0 : 1);
    }

    @Override
    public LiveSectionListDataAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.channel_item, null);

        LiveSectionListDataAdapter.SingleItemRowHolder mh = new LiveSectionListDataAdapter.SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(LiveSectionListDataAdapter.SingleItemRowHolder holder, final int position) {

        Channel singleItem = itemsList.get(position);
        String url = null;
        holder.cardView.setLayoutParams(new LinearLayout.LayoutParams
                (ScreenUtils.getScreenWidth(mContext) / 4, ScreenUtils.getScreenWidth(mContext) / 4));

        if (singleItem != null) {
            if (singleItem.getFirst_name() != null && singleItem.getFirst_name().trim().length() > 0) {
                holder.contentTitle.setText(singleItem.getFirst_name());
            }

            int placeHolder;

            placeHolder = R.mipmap.place_holder;

            Log.d(this.getClass().getName(), "image url=======>>>> " + mediaBaseUrl + singleItem.getPrfile_pic());

            Picasso
                    .with(mContext)
                    .load((mediaBaseUrl + singleItem.getPrfile_pic()).replace("\\", ""))
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .resize(holder.mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        public CardView cardView;
        @BindView(R.id.thumbnail)
        public ImageView mImageView;
        @BindView(R.id.content_title)
        public TextView contentTitle;


        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PlaylistScreen.class);
                    intent.putExtra("channel", itemsList.get(getAdapterPosition()));
                    intent.putExtra("baseUrl",bannerbaseUrl);
                    intent.putExtra("profileBaseUrl",mediaBaseUrl);
                    mContext.startActivity(intent);

                }
            });
        }
    }


}