
package com.multitv.yuv.models.categories;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Category implements Parcelable {

    public String version;
    public List<Vod> vod = new ArrayList<>();
    @SerializedName("live")
    public List<LiveCategory> liveCategoryArrayList = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
        dest.writeList(this.vod);
        dest.writeList(this.liveCategoryArrayList);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.version = in.readString();
        this.vod = new ArrayList<Vod>();
        in.readList(this.vod, Vod.class.getClassLoader());
        this.liveCategoryArrayList = new ArrayList<LiveCategory>();
        in.readList(this.liveCategoryArrayList, LiveCategory.class.getClassLoader());
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
