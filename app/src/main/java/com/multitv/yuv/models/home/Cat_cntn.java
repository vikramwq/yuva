package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by root on 24/10/16.
 */
public class Cat_cntn implements Parcelable {

    public String category;
    public String category_id;
    public String index;
    public String id;
    public String title;
    public String des;
    public String language_id;
    public String language;
    public String media_type;
    public String source;
    public String duration;
    public Integer price_type;
    public String url;
    public String likes;
    public Thumbnail thumbnail;
    public ArrayList<String> category_ids = new ArrayList<String>();

    public ArrayList<Thumb> thumbs = null;
    public Integer likes_count;
    public Integer rating;
    public String watch;
    public Integer favorite;
    public String start_cast;
    public Meta meta;
    public ArrayList<String> keywords = new ArrayList<String>();
    public int social_like;
    public int social_view;
    public String share_url;
    public String download_path;
    public String subtitle;
    public long seekDuration;
    public int download_expiry;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.category);
        dest.writeString(this.category_id);
        dest.writeString(this.index);
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.des);
        dest.writeString(this.language_id);
        dest.writeString(this.language);
        dest.writeString(this.media_type);
        dest.writeString(this.source);
        dest.writeString(this.duration);
        dest.writeValue(this.price_type);
        dest.writeString(this.url);
        dest.writeString(this.likes);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeStringList(this.category_ids);
        dest.writeList(this.thumbs);
        dest.writeValue(this.likes_count);
        dest.writeValue(this.rating);
        dest.writeString(this.watch);
        dest.writeValue(this.favorite);
        dest.writeString(this.start_cast);
        dest.writeParcelable(this.meta, flags);
        dest.writeStringList(this.keywords);
        dest.writeInt(this.social_like);
        dest.writeInt(this.social_view);
        dest.writeString(this.share_url);
        dest.writeString(this.download_path);
        dest.writeString(this.subtitle);
        dest.writeLong(this.seekDuration);
        dest.writeInt(this.download_expiry);
    }

    public Cat_cntn() {
    }

    protected Cat_cntn(Parcel in) {
        this.category = in.readString();
        this.category_id = in.readString();
        this.index = in.readString();
        this.id = in.readString();
        this.title = in.readString();
        this.des = in.readString();
        this.language_id = in.readString();
        this.language = in.readString();
        this.media_type = in.readString();
        this.source = in.readString();
        this.duration = in.readString();
        this.price_type = (Integer) in.readValue(Integer.class.getClassLoader());
        this.url = in.readString();
        this.likes = in.readString();
        this.thumbnail = in.readParcelable(Thumbnail.class.getClassLoader());
        this.category_ids = in.createStringArrayList();
        this.thumbs = new ArrayList<Thumb>();
        in.readList(this.thumbs, Thumb.class.getClassLoader());
        this.likes_count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.rating = (Integer) in.readValue(Integer.class.getClassLoader());
        this.watch = in.readString();
        this.favorite = (Integer) in.readValue(Integer.class.getClassLoader());
        this.start_cast = in.readString();
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        this.keywords = in.createStringArrayList();
        this.social_like = in.readInt();
        this.social_view = in.readInt();
        this.share_url = in.readString();
        this.download_path = in.readString();
        this.subtitle = in.readString();
        this.seekDuration = in.readLong();
        this.download_expiry=in.readInt();
    }

    public static final Parcelable.Creator<Cat_cntn> CREATOR = new Parcelable.Creator<Cat_cntn>() {
        @Override
        public Cat_cntn createFromParcel(Parcel source) {
            return new Cat_cntn(source);
        }

        @Override
        public Cat_cntn[] newArray(int size) {
            return new Cat_cntn[size];
        }
    };
}
