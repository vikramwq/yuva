package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.multitv.yuv.models.recommended.Thumbnail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/10/16.
 */

public class Recommned_home implements Parcelable {

    public String index;
    public String id;
    public String title;
    public String des;
    public String language_id;
    public String language;
    public String type;
    public String media_type;
    public String source;
    public String duration;
    public Integer price_type;
    public String url;
    public String playerUrl;
    public com.multitv.yuv.models.recommended.Thumbnail thumbnail;
    public List<String> category_ids = new ArrayList<String>();
    public Integer likes;
    public String likes_count;
    public int social_like;
    public int social_view;

    public Recommned_home(Parcel in) {
        index = in.readString();
        id = in.readString();
        title = in.readString();
        des = in.readString();
        language_id = in.readString();
        language = in.readString();
        type = in.readString();
        source = in.readString();
        url = in.readString();
        watch = in.readString();
        keywords = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Recommned_home> CREATOR = new Parcelable.Creator<Recommned_home>() {
        @Override
        public Recommned_home createFromParcel(Parcel in) {
            return new Recommned_home(in);
        }

        @Override
        public Recommned_home[] newArray(int size) {
            return new Recommned_home[size];
        }
    };

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getPrice_type() {
        return price_type;
    }

    public void setPrice_type(Integer price_type) {
        this.price_type = price_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Object getRating() {
        return rating;
    }

    public void setRating(Object rating) {
        this.rating = rating;
    }

    public String getWatch() {
        return watch;
    }

    public void setWatch(String watch) {
        this.watch = watch;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Object rating;
    public String watch;
    public Integer favorite;
    public Meta meta;
    public List<String> keywords = new ArrayList<String>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(index);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(des);
        dest.writeString(language_id);
        dest.writeString(language);
        dest.writeString(type);
        dest.writeString(source);
        dest.writeString(url);
        dest.writeString(watch);
        dest.writeStringList(keywords);
    }
}
