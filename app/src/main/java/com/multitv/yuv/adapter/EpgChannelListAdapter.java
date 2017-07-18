package com.multitv.yuv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.multitv.yuv.R;
import com.multitv.yuv.models.channels_by_cat.Channel;

/**
 * Created by root on 12/12/16.
 */

public class EpgChannelListAdapter extends BaseAdapter {
    private List<Channel> channelArrayList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public EpgChannelListAdapter(Context context, List<Channel> channelArrayList) {
        this.channelArrayList = channelArrayList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return channelArrayList.size();
    }

    @Override
    public String getItem(int position) {
        return channelArrayList.get(position).name;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.suggestion_list_item, null);
        }
        TextView category_tv = (TextView) convertView.findViewById(R.id.suggestion_list_item_textView);
        category_tv.setText(channelArrayList.get(position).name);
        return convertView;
    }
}
