//package mobito.com.intextv.fragment;
//
//import android.app.AlarmManager;
//import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Locale;
//
//import mobito.com.intextv.R;
//import mobito.com.intextv.broadcast.LiveTvReminderBroadIntentService;
//import mobito.com.intextv.database.DataBaseController;
//import mobito.com.intextv.database.ReminderData;
//import AppConfig;
//import CustomTFSpan;
//import Tracer;
//
//import static Constant.EXTRA_LIVE_TV_ID;
//import static Constant.URI_KEY;
//
///**
// * Created by mkr on 12/12/2016.
// */
//
//public class ReminderListFragment extends Fragment {
//    private FavoriteProgramAdapter mFavoriteProgramAdapter;
//    private static final String TAG = AppConfig.BASE_TAG + ".ReminderListFragment";
//    private LinearLayout empty;
//    private TextView empty_text;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Tracer.error(TAG, "onCreateView: ");
//
//        View view = inflater.inflate(R.layout.reminder_list_fragment, container, false);
//        empty = (LinearLayout) view.findViewById(R.id.empty);
//        empty_text = (TextView) view.findViewById(R.id.empty_text);
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Tracer.error(TAG, "onViewCreated: ");
//        if (getActivity() == null)
//            return;
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.reminder_list_fragment_listView);
//        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setAdapter(mFavoriteProgramAdapter = new FavoriteProgramAdapter(getActivity()));
//        ArrayList<ReminderData> reminderList = DataBaseController.getInstance(getActivity()).getReminderList();
//        Tracer.error(TAG, "onViewCreated: " + reminderList.size());
//        mFavoriteProgramAdapter.setReminderData(reminderList);
//
//        if (reminderList == null || reminderList.size() == 0) {
//            recyclerView.setVisibility(View.GONE);
//            empty.setVisibility(View.VISIBLE);
//            Tracer.error(TAG, "onViewCreated: " + "empty");
//        }
//        empty_text.setText(getResources().getString(R.string.empty_reminders));
//
//    }
//
//    private String getDoubleNumber(int count) {
//        if (count > 9) {
//            return "" + count;
//        }
//        return "0" + count;
//    }
//
//    /**
//     * Class to hold the Reminder Data
//     */
//    private class FavoriteProgramAdapter extends RecyclerView.Adapter {
//
//        private ArrayList<ReminderData> mReminderDataList = new ArrayList<>();
//        private Context mContext;
//        private Calendar mCalendar;
//
//        public FavoriteProgramAdapter(Context context) {
//            mContext = context;
//            mReminderDataList = new ArrayList<>();
//            mCalendar = Calendar.getInstance();
//        }
//
//        public void setReminderData(ArrayList<ReminderData> reminderDataList) {
//            mReminderDataList.clear();
//            if (reminderDataList != null) {
//                mReminderDataList.addAll(reminderDataList);
//            }
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new SingleItemRowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_reminder_row, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//            if (holder instanceof SingleItemRowHolder) {
//                final ReminderData reminderData = mReminderDataList.get(position);
//                ((SingleItemRowHolder) holder).title.setText(reminderData.getMTitle());
//                ((SingleItemRowHolder) holder).channel.setText(reminderData.getMChannelName());
//                mCalendar.setTimeInMillis(reminderData.getMTime());
//                String hour = "";
//                int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
//                String ampm = "";
//                if (hourOfDay >= 12) {
//                    hourOfDay = hourOfDay % 12;
//                    ampm = "PM";
//                    if (hourOfDay == 0) {
//                        hourOfDay = 12;
//                    }
//                } else {
//                    ampm = "AM";
//                }
//
//                /*mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) - 1);*/
//                ((SingleItemRowHolder) holder).duration_tv.setText(mCalendar.get(Calendar.DAY_OF_MONTH) + " " + mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
//                        + " " + getDoubleNumber(hourOfDay) + ":" + getDoubleNumber(mCalendar.get(Calendar.MINUTE)) + ":" + getDoubleNumber(mCalendar.get(Calendar.SECOND)) + " " + ampm);
//
//                if (reminderData.getMThumbnail() != null && !reminderData.getMThumbnail().trim().isEmpty()) {
//                    Picasso.with(mContext)
//                            .load(reminderData.getMThumbnail().trim())
//                            .placeholder(R.mipmap.place_holder)
//                            .error(R.mipmap.place_holder)
//                            .resize(((SingleItemRowHolder) holder).banner.getWidth(), (int) mContext.getResources().getDimension(R.dimen._100sdp))
//                            .into(((SingleItemRowHolder) holder).banner);
//                } else {
//                    ((SingleItemRowHolder) holder).banner.setImageResource(R.mipmap.place_holder);
//                }
//                ((SingleItemRowHolder) holder).mRoot.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        showCancelReminderDialog(mReminderDataList.get(position));
//                        return true;
//                    }
//                });
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return mReminderDataList.size();
//        }
//
//        private class SingleItemRowHolder extends RecyclerView.ViewHolder {
//            protected TextView title, duration_tv, channel;
//            private ImageView banner;
//            private View mRoot;
//
//
//            public SingleItemRowHolder(View view) {
//                super(view);
//                mRoot = view.findViewById(R.id.adapter_reminder_row_root);
//                this.title = (TextView) view.findViewById(R.id.adapter_reminder_row_textView_title);
//                this.duration_tv = (TextView) view.findViewById(R.id.adapter_reminder_row_textView_duration);
//                this.channel = (TextView) view.findViewById(R.id.adapter_reminder_row_textView_channel);
//                this.banner = (ImageView) view.findViewById(R.id.adapter_reminder_row_imageView_thumb);
//            }
//        }
//
//        /**
//         * Method to show the calncel epg reminder
//         *
//         * @param reminderData
//         */
//        private void showCancelReminderDialog(final ReminderData reminderData) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            builder.setMessage("     " + mContext.getString(R.string.remove_reminder_mess) + " " + reminderData.getMTitle());
//            builder.setIcon(R.mipmap.ic_launcher);
//            if (getActivity() == null)
//                return;
//            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/app_header.ttf");
//            CustomTFSpan tfSpan = new CustomTFSpan(tf);
//            SpannableString spannableString = new SpannableString(getResources().getString(R.string.app_name));
//            spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setTitle(spannableString);
//            builder.setPositiveButton(mContext.getString(R.string.remove_reminder_ok), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    cancelReminder(reminderData);
//                    mFavoriteProgramAdapter.setReminderData(DataBaseController.getInstance(getActivity()).getReminderList());
//                    dialog.dismiss();
//                }
//            });
//            builder.setNegativeButton(mContext.getString(R.string.remove_reminder_cancel), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.dismiss();
//                }
//            });
//            builder.create().show();
//        }
//
//        /**
//         * Method to cancel the Reminder
//         *
//         * @param reminderData
//         */
//        private void cancelReminder(ReminderData reminderData) {
//            Tracer.error(TAG, "cancelReminder: ");
//            int requestCode = 0;
//            try {
//                requestCode = (int) reminderData.getMLiveTvId().longValue();
//            } catch (Exception e) {
//                Tracer.error(TAG, "setReminder: " + e.getMessage());
//                requestCode = (int) System.currentTimeMillis();
//            }
//            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(mContext, LiveTvReminderBroadIntentService.class);
//            intent.putExtra(EXTRA_LIVE_TV_ID, DataBaseController.getInstance(mContext).getReminderId(reminderData.getMLiveTvId(), reminderData.getMTime()));
//            intent.setData(Uri.parse(URI_KEY + DataBaseController.getInstance(mContext).getReminderId(reminderData.getMLiveTvId(), reminderData.getMTime())));
//            PendingIntent sender = PendingIntent.getService(mContext, requestCode, intent, 0);
//            alarmManager.cancel(sender);
//            DataBaseController.getInstance(mContext).removeReminder(reminderData.getMLiveTvId(), reminderData.getMTime());
//        }
//    }
//}
