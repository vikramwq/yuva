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
import android.widget.TextView;
import android.widget.Toast;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.models.home.Cat_cntn;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.ConnectionManager;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.ScreenUtils;
import com.multitv.yuv.utilities.Tracer;
import com.multitv.yuv.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private List<Cat_cntn> itemsList;
    private Context mContext;
    private String layoutOrientation;
    private int size;
    private String fragmentName;
    boolean isLoggedIn,isOTPVerified;
    SharedPreference sharedPreference;


    public SectionListDataAdapter(Context context, List<Cat_cntn> itemsList, String layoutOrientation, String fragmentName) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.layoutOrientation = layoutOrientation;
        this.fragmentName = fragmentName;
        sharedPreference = new SharedPreference();
        isLoggedIn = sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_LOGGED_IN);
        isOTPVerified=sharedPreference.getPreferenceBoolean(mContext, sharedPreference.KEY_IS_OTP_VERIFIED);
//        Log.d(this.getClass().getName(),"fragmentName========="+fragmentName);





    }

    @Override
    public int getItemViewType(int position) {
        return ((layoutOrientation != null && layoutOrientation.equalsIgnoreCase("rectangle")) ? 0 : 1);
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        if (i == 0) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_card_landscape, null);
        } else {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_card, null);
        }
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int position) {

        Cat_cntn singleItem = itemsList.get(position);

//        Log.d(this.getClass().getName(), "tag in adapter===" + fragmentName);

        String url = null;

        if (holder != null && holder.getItemViewType() == 0) {
            holder.cardView.setLayoutParams(new RecyclerView.LayoutParams
                    (ScreenUtils.getScreenWidth(mContext) / 2, RecyclerView.LayoutParams.WRAP_CONTENT));
        } else {
            holder.cardView.setLayoutParams(new RecyclerView.LayoutParams
                    (ScreenUtils.getScreenWidth(mContext) / 3, RecyclerView.LayoutParams.WRAP_CONTENT));
        }

        if (singleItem != null) {

            if (singleItem.title != null && singleItem.title.trim().length() > 0) {
                holder.contentTitle.setText(singleItem.title);
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectionManager.getInstance(mContext).isConnected()) {
                        manageContentClick(position);
                    } else {
                        Toast.makeText(mContext, "No internet connection", Toast.LENGTH_LONG);
                    }
                }
            });

            String duration = String.valueOf(singleItem.duration);


            Log.d(this.getClass().getName(),"duration==========>>>> "+duration);

            if (duration != null && !duration.equals(null)) {
                String splitTime[] = duration.split(":");
                String hours = splitTime[0];
                String minutes = splitTime[1];
                String seconds = splitTime[2];

                if (hours.equals("00")) {
                    holder.durationTxt.setText(minutes + ":" + seconds + " " + mContext.getResources().getString(R.string.minute));
                } else {
                    holder.durationTxt.setText(hours + ":" + minutes + " " + mContext.getResources().getString(R.string.hours));
                }
            } else {
                holder.durationTxt.setVisibility(View.GONE);
            }

            for (int i = 0; i < singleItem.thumbs.size(); i++) {
//                Log.d(this.getClass().getName(),"layoutOrientation=======>>>>"+layoutOrientation);

//                Log.d(this.getClass().getName(),"layoutOrientation=======>>>>"+singleItem.thumbs.get(i).getName());

                if(singleItem.thumbs.get(i)!=null) {
                    if (singleItem.thumbs.get(i).getName().equalsIgnoreCase(layoutOrientation)) {
                        url = singleItem.thumbs.get(i).getThumb().getMedium();
                        break;
                    } else if (singleItem.thumbs.get(i).getName().equalsIgnoreCase("default"))
                        url = singleItem.thumbs.get(i).getThumb().getMedium();

                }
            }


//            Log.d(this.getClass().getName(),"url of image========>> "+url);

            int placeHolder;
            if(fragmentName.equalsIgnoreCase("Clip TV") || fragmentName.equalsIgnoreCase("Music Videos")){
                 placeHolder=R.mipmap.place_holder_land;
            }else{
                placeHolder= R.mipmap.place_holder;
            }




            if (url != null && !url.equals(null) && !url.equals("")) {


                Picasso
                        .with(mContext)
                        .load(url)
                        .placeholder(placeHolder)
                        .error(placeHolder)
                        .resize(holder.mImageView.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
                        .into(holder.mImageView);
            } else {
                Picasso.with(mContext)
                        .load(placeHolder)
                        .into(holder.mImageView);

            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        public CardView cardView;
        @BindView(R.id.duration_tv)
        public TextView durationTxt;
        @BindView(R.id.thumbnail)
        public ImageView mImageView;
        @BindView(R.id.content_title)
        public TextView contentTitle;



        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    private void manageContentClick(int position) {
        try {


            if (isLoggedIn && isOTPVerified) {
                String videoUrl = itemsList.get(position).url;
                if (!videoUrl.equals("")) {
                    Intent videoIntent = new Intent(mContext, MultiTvPlayerActivity.class);
                    videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, itemsList.get(position));
                    videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                    mContext.startActivity(videoIntent);
                }
            } else {
                if ("Movie Promos".equalsIgnoreCase(fragmentName)) {
                    String videoUrl = itemsList.get(position).url;
                    if (!videoUrl.equals("")) {
                        Intent videoIntent = new Intent(mContext, MultiTvPlayerActivity.class);
                        videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, itemsList.get(position));
                        videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");
                        mContext.startActivity(videoIntent);
                    }
                } else {
                    Utilities.showLoginDailog(mContext);

                }
            }
        } catch (Exception ex) {
            Tracer.error("error", ex.getMessage());
            Toast.makeText(mContext, "There is something wrong happening, please try with another content", Toast.LENGTH_LONG).show();
        }

    }
}