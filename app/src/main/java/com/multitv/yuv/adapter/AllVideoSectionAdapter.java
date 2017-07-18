package com.multitv.yuv.adapter;

/**
 * Created by Created by Sunil on 19-09-2016.
 */

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.multitv.yuv.R;
import com.multitv.yuv.models.SingleItemModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllVideoSectionAdapter extends RecyclerView.Adapter<AllVideoSectionAdapter.SingleItemRowHolder> {

    private ArrayList<SingleItemModel> itemsList;
    private Context mContext;

    public AllVideoSectionAdapter(Context context, ArrayList<SingleItemModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
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
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.allvideo_section_row, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        SingleItemModel singleItem = itemsList.get(i);

        // holder.mTitle.setText(singleItem.getName());

//        holder.cardView.getLayoutParams().width = getScreenWidth(mContext);
        //holder.cardView.getLayoutParams().width = (int) (getScreenWidth(mContext) / 2);
        //holder.cardView.getLayoutParams().height = (int) (getScreenWidth(mContext) / 2);


       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        /// @BindView(R.id.txt_tv_time)
        //  protected TextView mTime;
        //  @BindView(R.id.txt_tv_title)
        //  protected TextView mTitle;
        @BindView(R.id.itemImage)
        protected ImageView mImageView;

        //  protected LinearLayout cardView;
        // protected ImageView itemImage;
        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    // Toast.makeText(v.getContext(), mTitle.getText(), Toast.LENGTH_SHORT).show();

                }
            });


        }

    }
}