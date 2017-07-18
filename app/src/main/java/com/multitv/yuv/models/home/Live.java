package com.multitv.yuv.models.home;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 24/9/16.
 */
public class Live {
    public String id;
    public String version;
    public String price;

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (des != null ? des.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (category_id != null ? category_id.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (_package != null ? _package.hashCode() : 0);
        result = 31 * result + (Subcriptions_ != null ? Subcriptions_.hashCode() : 0);
        result = 31 * result + (package_info != null ? package_info.hashCode() : 0);
        result = 31 * result + (likes != null ? likes.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (watch != null ? watch.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        return result;
    }

    public String title;
    public String des;
    public String genre;
    public String category_id;
    public String category;
    public String playerUrl;
    public String url;
    public String type;
    public String media_type;
    public Thumbnail thumbnail;
    public String _package;
    public String Subcriptions_;
    public String package_info;
    public String likes;
    public String rating;
    public String watch;
    public String channel;
    public List<String> keywords = new ArrayList<String>();

    public class Thumbnail {
        public String large;
        public String medium;
        public String small;
    }
}


