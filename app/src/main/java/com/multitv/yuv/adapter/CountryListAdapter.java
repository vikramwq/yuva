package com.multitv.yuv.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.models.CountryCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 22-07-2017.
 */

public class CountryListAdapter  extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<CountryCode> countryCodeList=new ArrayList<>();
    private Context context;

    public CountryListAdapter(Context context, ArrayList<CountryCode> countryCodeList) {
        layoutInflater = LayoutInflater.from(context);
        this.countryCodeList=countryCodeList;
        this.context=context;
    }


    @Override
    public int getCount() {
        return countryCodeList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.country_item, null);
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.categoryName_tv);
        TextView codeTextView = (TextView) view.findViewById(R.id.categoryCode_tv);

        nameTextView.setText(countryCodeList.get(position).name);
        codeTextView.setText(countryCodeList.get(position).code);

        return view;
    }
}
