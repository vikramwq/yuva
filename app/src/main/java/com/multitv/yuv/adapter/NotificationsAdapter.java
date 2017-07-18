package com.multitv.yuv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.multitv.yuv.R;
import com.multitv.yuv.dialogs.NotificationClickAlertDialog;
import com.multitv.yuv.interfaces.OnLoadMoreListener;
import com.multitv.yuv.models.notification_center.NotificationContent;
import com.multitv.yuv.service.UpdatePushStatus;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.AppConstants;
import com.multitv.yuv.utilities.AppUtils;
import com.multitv.yuv.utilities.Tracer;

import static com.multitv.yuv.utilities.Constant.EXTRA_ACKNOWLEDGE_TYPE;
import static com.multitv.yuv.utilities.Constant.EXTRA_NOTIFICATION_ID;
import static com.multitv.yuv.utilities.Constant.EXTRA_UPDATE_UNREAD_COUNT;

/**
 * Created by cyberlinks on 27/12/16.
 */

public class NotificationsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private OnLoadMoreListener onLoadMoreListener;
    private RecyclerView mRecyclerView;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<NotificationContent> notificationContentList = new ArrayList<>();
    SharedPreference sharedPreference;

    private static final String TAG = AppConfig.BASE_TAG + ".NotificationsAdapter";

    public NotificationsAdapter(Context context, List<NotificationContent> items, RecyclerView recyclerView) {
        this.mContext = context;
        this.notificationContentList = items;
        mRecyclerView = recyclerView;
        sharedPreference = new SharedPreference();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mRecyclerView.getAdapter().getItemCount();

                try {
                    lastVisibleItem = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_notification, parent, false);

            return new NotificationsAdapter.NotificationRowHolder(v);
        } else if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.load_more_progress_item, parent, false);

            return new NotificationsAdapter.ProgressViewHolder(v);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return notificationContentList.get(position) == null ? VIEW_PROG : VIEW_ITEM;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationsAdapter.NotificationRowHolder) {
            final NotificationContent content = notificationContentList.get(position);
            ((NotificationRowHolder) holder).tittle.setText(content.title);
            ((NotificationRowHolder) holder).desc.setText(content.description);
            ((NotificationRowHolder) holder).time.setText(AppUtils.getFormattedDate(content.send_date));

            if (!TextUtils.isEmpty(content.click_status) && Integer.parseInt(content.click_status) == 1) {

                ((NotificationRowHolder) holder).unreadIcon.setVisibility(View.INVISIBLE);

            } else if (!TextUtils.isEmpty(content.seen_status) && Integer.parseInt(content.seen_status) == 1) {

                ((NotificationRowHolder) holder).unreadIcon.setVisibility(View.INVISIBLE);

            } else {
                ((NotificationRowHolder) holder).unreadIcon.setVisibility(View.VISIBLE);
            }

            ((NotificationRowHolder) holder).llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(content.seen_status)) {
                        if (TextUtils.isEmpty(content.click_status)) {
                            startService(content._id);
                            view.findViewById(R.id.unread_icon).setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(content.click_status) != 1) {
                            startService(content._id);
                            view.findViewById(R.id.unread_icon).setVisibility(View.INVISIBLE);
                        }
                    }

                    new NotificationClickAlertDialog().showDialog(mContext, content);

                    /*Set<String> unreadSet = sharedPreference.getUnreadNotificationsList(mContext, sharedPreference.KEY_NOTIFICATION_SET);
                    Tracer.error(TAG, "Unread notification count : " + unreadSet.size());
                    if (unreadSet.contains(content._id)) {
                        unreadSet.remove(content._id);
                    }
                    Tracer.error(TAG, "Unread notification count after remove: " + unreadSet.size());
                    sharedPreference.setUnreadNotificationsList(mContext, sharedPreference.KEY_NOTIFICATION_SET, unreadSet);*/
                }
            });


            ((NotificationRowHolder) holder).menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popup = new PopupMenu(mContext, view);
                    popup.getMenuInflater().inflate(R.menu.notification_list_pop_up_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId == R.id.remove) {
                                //do something
                                return true;
                            } else {
                                return onMenuItemClick(item);
                            }
                        }
                    });

                    popup.show();
                }
            });
        }
    }

    private void startService(String notificationId) {
        Intent intentDelete = new Intent(mContext, UpdatePushStatus.class);
        intentDelete.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        intentDelete.putExtra(EXTRA_ACKNOWLEDGE_TYPE, AppConstants.PUSH_STATUS_CLICK);
        intentDelete.putExtra(EXTRA_UPDATE_UNREAD_COUNT, true);
        mContext.startService(intentDelete);
    }

    @Override
    public int getItemCount() {
        return notificationContentList.size();
    }

    public class NotificationRowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_notification_row_container)
        LinearLayout llContainer;

        @BindView(R.id.tv_notification_title)
        TextView tittle;

        @BindView(R.id.tv_notification_desc)
        TextView desc;

        @BindView(R.id.tv_notification_time)
        TextView time;

        @BindView(R.id.menu)
        ImageView menu;

        @BindView(R.id.unread_icon)
        ImageView unreadIcon;

        public NotificationRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }


}
