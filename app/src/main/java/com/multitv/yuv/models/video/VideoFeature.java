package com.multitv.yuv.models.video;

import java.util.ArrayList;
import java.util.List;

import com.multitv.yuv.models.home.Meta_;
import com.multitv.yuv.models.home.Thumbnail_;

/**
 * Created by root on 19/10/16.
 */

public class VideoFeature {
    public List<String> category_ids = new ArrayList<String>();
    public String id;
    public String title;
    public String des;
    public String language_id;
    public String language;
    public String genre;
    public String category_id;
    public String media_type;
    public String source;
    public String duration;
    public Integer price_type;
    public String url;
    public String likes;
    public Thumbnail_ thumbnail;
    public Integer likes_count;
    public String rating;
    public String watch;
    public Integer favorite;
    public Meta_ meta;
    public List<String> keywords = new ArrayList<String>();
    public int social_like;
    public int social_view;
}
