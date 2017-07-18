
package com.multitv.yuv.models.movies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Content implements Parcelable{

    public String index;
    public String id;
    public String title;
    public String des;
    public String language_id;
    public String language;
    public String type;
    public String source;
    public String duration;
    public String price_type;
    public String url;
    public  String media_type;
    public Thumbnail thumbnail;
    public String likes;
    public String rating;
    public String watch;
    public Integer favorite;
    public Meta meta;
    public String likes_count;
    public String start_cast;
    public List<Object> keywords = new ArrayList<Object>();
    public ArrayList<String> package_info = new ArrayList<String>();
    public int social_like;
    public int social_view;

    protected Content(Parcel in) {
        index = in.readString();
        id = in.readString();
        title = in.readString();
        language_id = in.readString();
        language = in.readString();
        type = in.readString();
        source = in.readString();
        duration = in.readString();
        url = in.readString();
        watch = in.readString();
        package_info = in.createStringArrayList();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(index);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(language_id);
        dest.writeString(language);
        dest.writeString(type);
        dest.writeString(source);
        dest.writeString(duration);
        dest.writeString(url);
        dest.writeString(watch);
        dest.writeStringList(package_info);
    }
}
