package com.multitv.yuv.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.multitv.yuv.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RateAppFragment extends Fragment {

    private Button btnRate;


    public RateAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_rate_app, container, false);

        btnRate=(Button)view.findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingDailog();
            }
        });

        return view;
    }

    private void ratingDailog() {
        if(getActivity() == null)
            return;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout_app);


        final RatingBar rating_bar = (RatingBar) dialog.findViewById(R.id.dailog_rating);

        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);
        String rating_count;
        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating_for,
                                        boolean fromUser) {
                rating_for = rating_bar.getRating();


            }
        });
        dialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                String rateFromUser = String.valueOf(rating_bar.getRating());
                String[] parts = rateFromUser.split("\\.");
                String rateingFromUser = parts[0];
                //sendRatingToServer(rateingFromUser);
                //  Toast.makeText(getApplicationContext(),"Your Selected Ratings  : " + part1,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

}
