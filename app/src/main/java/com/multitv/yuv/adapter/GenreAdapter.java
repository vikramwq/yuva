package com.multitv.yuv.adapter;

/**
 * Created by arungoyal on 21/04/17.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.multitv.yuv.R;
import com.multitv.yuv.activity.GenreBasedContentScreen;
import com.multitv.yuv.models.genre.GenreItem;
import com.multitv.yuv.sharedpreference.SharedPreference;
import com.multitv.yuv.utilities.ScreenUtils;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.MyViewHolder> {

    private List<GenreItem> moviesList;
    private Context context;
    private SharedPreference sharedPreference;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public ImageView genreImg;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.genreItem);
            genreImg = (ImageView) view.findViewById(R.id.genreImg);
            cardView = (CardView) view.findViewById(R.id.card_view);

        }
    }


    public GenreAdapter(Context context, List<GenreItem> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
        sharedPreference = new SharedPreference();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final GenreItem movie = moviesList.get(position);

        holder.cardView.setLayoutParams(new RecyclerView.LayoutParams
                (ScreenUtils.getScreenWidth(context) / 3, RecyclerView.LayoutParams.WRAP_CONTENT));


        holder.title.setText(movie.getGenre_name());

        if (movie.getThumb() != null && !movie.getThumb().equals(null) && !movie.getThumb().equals("")) {
            Picasso
                    .with(context)
                    .load(movie.getThumb())
                    .placeholder(R.mipmap.place_holder)
                    .error(R.mipmap.place_holder)
                    .into(holder.genreImg);

        } else {

            if (movie.getGenre_name() != null && movie.getGenre_name().equalsIgnoreCase("All")) {
                holder.genreImg.setImageResource(R.mipmap.all_genre);
            } else {

                Picasso.with(context)
                        .load(R.mipmap.place_holder)
                        .placeholder(R.mipmap.place_holder)
                        .error(R.mipmap.place_holder)
                        .fit() // will explain later
                        .into(holder.genreImg);
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(context, GenreBasedContentScreen.class);
                intent.putExtra("selectedGenre", movie.getId());
                intent.putExtra("genreName", movie.getGenre_name());

                context.startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}