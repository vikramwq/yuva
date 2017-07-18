
package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Feature_banner implements Parcelable {

    public String id;
    public String title;
    public String des;
    public String language_id;
    public String language;
    public String genre;
    public String category;
    public String category_id;
    public String media_type;
    public String source;
    public String duration;
    public String price_type;
    public String url;
    public List<String> package_info = new ArrayList<String>();
    public Thumbnail thumbnail;
    public Integer likes;
    public String rating;
    public String watch;
    public Integer favorite;
    public Meta meta;
    public List<Object> keywords = new ArrayList<Object>();
    public List<String> category_ids = new ArrayList<String>();
    public int social_like;
    public int social_view;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.des);
        dest.writeString(this.language_id);
        dest.writeString(this.language);
        dest.writeString(this.genre);
        dest.writeString(this.category);
        dest.writeString(this.category_id);
        dest.writeString(this.media_type);
        dest.writeString(this.source);
        dest.writeString(this.duration);
        dest.writeString(this.price_type);
        dest.writeString(this.url);
        dest.writeStringList(this.package_info);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeValue(this.likes);
        dest.writeString(this.rating);
        dest.writeString(this.watch);
        dest.writeValue(this.favorite);
        dest.writeParcelable(this.meta, flags);
        dest.writeList(this.keywords);
        dest.writeStringList(this.category_ids);
        dest.writeInt(this.social_like);
        dest.writeInt(this.social_view);
    }

    public Feature_banner() {
    }

    protected Feature_banner(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.des = in.readString();
        this.language_id = in.readString();
        this.language = in.readString();
        this.genre = in.readParcelable(Object.class.getClassLoader());
        this.category = in.readString();
        this.category_id = in.readString();
        this.media_type = in.readString();
        this.source = in.readString();
        this.duration = in.readString();
        this.price_type = in.readString();
        this.url = in.readString();
        this.package_info = in.createStringArrayList();
        this.thumbnail = in.readParcelable(Thumbnail.class.getClassLoader());
        this.likes = (Integer) in.readValue(Integer.class.getClassLoader());
        this.rating = in.readString();
        this.watch = in.readString();
        this.favorite = (Integer) in.readValue(Integer.class.getClassLoader());
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        this.keywords = new ArrayList<Object>();
        in.readList(this.keywords, Object.class.getClassLoader());
        this.category_ids = in.createStringArrayList();
        this.social_like = in.readInt();
        this.social_view = in.readInt();
    }

    public static final Parcelable.Creator<Feature_banner> CREATOR = new Parcelable.Creator<Feature_banner>() {
        @Override
        public Feature_banner createFromParcel(Parcel source) {
            return new Feature_banner(source);
        }

        @Override
        public Feature_banner[] newArray(int size) {
            return new Feature_banner[size];
        }
    };
}
