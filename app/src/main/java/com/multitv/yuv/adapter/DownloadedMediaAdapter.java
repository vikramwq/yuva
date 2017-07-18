package com.multitv.yuv.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.DownloadedMediaListing;
import com.multitv.yuv.activity.MultiTvPlayerActivity;
import com.multitv.yuv.models.MediaItem;
import com.multitv.yuv.security.FileSecurityUtils;
import com.multitv.yuv.utilities.Constant;
import com.multitv.yuv.utilities.Tracer;
import com.google.gson.Gson;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.customview.GifImageView;
import com.multitv.yuv.download.DownloadUtils;
import com.multitv.yuv.models.home.Cat_cntn;

import static android.content.Context.DOWNLOAD_SERVICE;


public class DownloadedMediaAdapter extends RecyclerView.Adapter<DownloadedMediaAdapter.SingleItemRowHolder> {

    private List<MediaItem> itemsList;
    private Context mContext;
    private String layoutOrientation = "rectangle";
    private int size;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;






    public DownloadedMediaAdapter(Context context, List<MediaItem> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;




    }

    @Override
    public int getItemViewType(int position) {
        return ((layoutOrientation != null && layoutOrientation.equalsIgnoreCase("rectangle")) ? 0 : 1);
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_media_item, null);

        SingleItemRowHolder mh = new SingleItemRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int position) {
        final MediaItem mediaItem = itemsList.get(position);
        Cat_cntn singleItem = new Gson().fromJson(mediaItem.getType(), Cat_cntn.class);
        Log.d(this.getClass().getName(), "singleItem======" + singleItem.download_expiry);
        Log.d(this.getClass().getName(), "singleItem======data" + new Gson().toJson(singleItem));

        String url = null;
//
//        if (holder != null && holder.getItemViewType() == 0) {
//            holder.cardView.setLayoutParams(new RecyclerView.LayoutParams
//                    (ScreenUtils.getScreenWidth(mContext) / 2, RecyclerView.LayoutParams.WRAP_CONTENT));
//        } else {
//            holder.cardView.setLayoutParams(new RecyclerView.LayoutParams
//                    (ScreenUtils.getScreenWidth(mContext) / 3, RecyclerView.LayoutParams.WRAP_CONTENT));
//        }

        if (singleItem != null) {

            if (singleItem.title != null && singleItem.title.trim().length() > 0) {
                holder.contentTitle.setText(singleItem.title);
            }

            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    if (mediaItem.isDownloaded())
                        popupMenu.getMenu().add("Remove");
                    else
                        popupMenu.getMenu().add("Cancel");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getTitle().toString()) {
                                case "Remove":
                                    showConfirmationDialog(false, mediaItem, position);
                                    break;
                                case "Cancel":
                                    showConfirmationDialog(true, mediaItem, position);
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            if (mediaItem.isDownloaded()) {
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manageContentClick(position);
                    }
                });
            }

            if (mediaItem.getDownloadStatus() == DownloadManager.STATUS_RUNNING) {
                holder.progressBar.setVisibility(View.VISIBLE);
                DrawableCompat.setTint(holder.progressBar.getProgressDrawable(),
                        mContext.getResources().getColor(android.R.color.black));

                updateProgress(holder.progressBar, mediaItem.getDownloadReferenceId(), position);
                holder.download_image.setGifImageResource(R.drawable.down_update);
                holder.download_image.setVisibility(View.VISIBLE);
                holder.overlayLayout.setVisibility(View.VISIBLE);
                holder.img_play.setVisibility(View.GONE);
            } else {
                holder.overlayLayout.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                holder.download_image.setVisibility(View.GONE);
                holder.img_play.setVisibility(View.VISIBLE);
            }


            holder.downloadStatus.setText(getDownloadStatus(mediaItem.getDownloadStatus()));
            holder.downloadStatus.setTextColor(getDownloadStatusColor(mediaItem.getDownloadStatus()));

            String duration = String.valueOf(singleItem.duration);

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


//            holder.subtitle_tv.setText(singleItem.des);

            if (singleItem.meta!=null && singleItem.meta.genre != null && !singleItem.meta.genre.trim().isEmpty()) {
                holder.genre_sony.setVisibility(View.VISIBLE);
                holder.genre_sony.setText(singleItem.meta.genre);
            }


            for (int i = 0; i < singleItem.thumbs.size(); i++) {
                if (singleItem.thumbs.get(i).getName().equalsIgnoreCase(layoutOrientation)) {
                    url = singleItem.thumbs.get(i).getThumb().getMedium();
                    break;
                } else if (singleItem.thumbs.get(i).getName().equalsIgnoreCase("default"))
                    url = singleItem.thumbs.get(i).getThumb().getMedium();
            }


            Log.d(this.getClass().getName(),"image path for download media==="+url);










            if (url != null && !url.equals(null) && !url.equals("")) {

//                loader.displayImage(url,holder.mImageView,options);


                Picasso
                        .with(mContext)
                        .load(url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.mipmap.place_holder_land)
                        .error(R.mipmap.place_holder_land)
                        .into(holder.mImageView);
            } else {
                Picasso.with(mContext)
                        .load(R.mipmap.place_holder_land)
                        .into(holder.mImageView);

            }
        }
    }

    private String getDownloadStatus(final int downloadStatus) {
        final String downloadStatusValue;
        switch (downloadStatus) {
            case DownloadManager.STATUS_PAUSED:
                downloadStatusValue = "Paused";
                break;
            case DownloadManager.STATUS_PENDING:
                downloadStatusValue = "Pending";
                break;
            case DownloadManager.STATUS_RUNNING:
                downloadStatusValue = "Downloading...";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                downloadStatusValue = "Downloaded";
                break;
            case DownloadManager.STATUS_FAILED:
                downloadStatusValue = "Failed";
                break;
            default:
                downloadStatusValue = "Cancelled";
                break;
        }
        return downloadStatusValue;
    }

    private int getDownloadStatusColor(final int downloadStatus) {
        final int colorId;
        switch (downloadStatus) {
            case DownloadManager.STATUS_RUNNING:
                colorId = R.color.orange;
                break;
            case DownloadManager.STATUS_FAILED:
                colorId = R.color.red;
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                colorId = R.color.green;
                break;
            default:
                colorId = android.R.color.white;
                break;
        }
        return mContext.getResources().getColor(colorId);
    }

    private void updateProgress(final ProgressBar progressBar, final long downloadReferenceId, final int position) {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(downloadReferenceId);

                            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
                            final Cursor cursor = downloadManager.query(query);
                            cursor.moveToFirst();

                            int bytes_downloaded = cursor.getInt(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                            int dl_progress = 0;
                            if (bytes_total != 0)
                                dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                            final int progress = dl_progress;

                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) != DownloadManager.STATUS_RUNNING) {
                                            if (progressRunnable != null)
                                                progressHandler.removeCallbacks(progressRunnable);
                                            progressHandler.removeCallbacksAndMessages(null);

                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            progressBar.setProgress(progress);
                                            if (progressRunnable != null)
                                                progressHandler.postDelayed(progressRunnable, 500);
                                        }

                                        if (cursor != null && !cursor.isClosed())
                                            cursor.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };

        progressHandler.postDelayed(progressRunnable, 500);
    }

    private void showConfirmationDialog(final boolean isCanceling, final MediaItem mediaItem, final int position) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_download);

        if (isCanceling)
            ((TextView) dialog.findViewById(R.id.title)).setText("Do you want to cancel " + mediaItem.getTitle() + " ?");
        else
            ((TextView) dialog.findViewById(R.id.title)).setText("Do you want to remove " + mediaItem.getTitle() + " ?");

        Button cancelOrRemoveBtn = (Button) dialog.findViewById(R.id.btn_cancel_remove);
        if (isCanceling)
            cancelOrRemoveBtn.setText("Cancel");
        else
            cancelOrRemoveBtn.setText("Remove");

        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cancelOrRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);

                if (isCanceling && mediaItem.getDownloadStatus() != DownloadManager.STATUS_PENDING) {

                    downloadManager.remove(mediaItem.getDownloadReferenceId());
                }

                DownloadUtils.removeDownload(mContext, mediaItem);

                itemsList.remove(position);
                /*notifyItemChanged(position);*/
                notifyDataSetChanged();

                if (isCanceling) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                    Cursor cursor = downloadManager.query(query);

                    if (!cursor.moveToFirst())
                        DownloadUtils.downloadNext(mContext);

                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                if (isCanceling)
                    Toast.makeText(mContext, (!TextUtils.isEmpty(mediaItem.getTitle()) ? mediaItem.getTitle() : "Item") + " cancelled", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, (!TextUtils.isEmpty(mediaItem.getTitle()) ? mediaItem.getTitle() : "Item") + " removed", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
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
        @BindView(R.id.video_thumbnail_iv)
        public ImageView mImageView;
        @BindView(R.id.title_tv)
        public TextView contentTitle;

     /*   @BindView(R.id.subtitle_tv)
        public TextView subtitle_tv;*/

        @BindView(R.id.genre_sony)
        public TextView genre_sony;


        @BindView(R.id.img_play)
        public ImageView img_play;


        @BindView(R.id.progress_bar)
        public ProgressBar progressBar;

        @BindView(R.id.download_status)
        public TextView downloadStatus;

        @BindView(R.id.cancel_iv)
        public ImageView cancel;

        @BindView(R.id.download_image)
        public GifImageView download_image;


        @BindView(R.id.overlayLayout)
        public LinearLayout overlayLayout;


        public SingleItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    private void manageContentClick(final int position) {
        if (mContext instanceof DownloadedMediaListing && ((DownloadedMediaListing) mContext).progressLayout != null)
            ((DownloadedMediaListing) mContext).progressLayout.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MediaItem mediaItem = itemsList.get(position);

                    final Cat_cntn singleItem = new Gson().fromJson(mediaItem.getType(), Cat_cntn.class);

                    final String VIDEO_URL = singleItem.url;

                    FileSecurityUtils.decryptFile(mContext, mediaItem.getName(), mediaItem.getPath(), new FileSecurityUtils.FileDecryptionListener() {
                        @Override
                        public void onFileDecrypted(final String filePath) {
                            ((DownloadedMediaListing) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mContext instanceof DownloadedMediaListing && ((DownloadedMediaListing) mContext).progressLayout != null)
                                        ((DownloadedMediaListing) mContext).progressLayout.setVisibility(View.GONE);

                                    if (!VIDEO_URL.equals("")) {
                                        Intent videoIntent = new Intent(mContext, MultiTvPlayerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        videoIntent.putExtra(Constant.CONTENT_TRANSFER_KEY, singleItem);

                                        videoIntent.putExtra("local_path", filePath);

                                        videoIntent.putExtra("CONTENT_TYPE_MULTITV", "VOD");

                                        mContext.startActivity(videoIntent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFileDecryptionError() {
                            ((DownloadedMediaListing) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mContext instanceof DownloadedMediaListing && ((DownloadedMediaListing) mContext).progressLayout != null)
                                        ((DownloadedMediaListing) mContext).progressLayout.setVisibility(View.GONE);
                                    Toast.makeText(mContext, "There is something wrong happening, please try with another content", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } catch (Exception ex) {
                    Tracer.error("error", ex.getMessage());
                    Toast.makeText(mContext, "There is something wrong happening, please try with another content", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
}