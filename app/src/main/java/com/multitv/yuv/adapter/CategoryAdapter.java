package com.multitv.yuv.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.multitv.yuv.R;

import java.util.List;




/**
 * Created by arungoyal on 01/06/17.
 */

public class CategoryAdapter extends ArrayAdapter<String> {
    LayoutInflater layoutInflater;

    public CategoryAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.category_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.categoryName_tv);


        textView.setText(getItem(position));

        return view;
    }
}
