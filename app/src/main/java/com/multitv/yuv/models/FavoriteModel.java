package com.multitv.yuv.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.multitv.yuv.models.video.Content;

import java.util.ArrayList;

/**
 * Created by delhivery on 9/27/2016.
 */
public class FavoriteModel implements Parcelable {

    public Integer offset;
    public int totalCount;
    public String version;
    public ArrayList<Content> content = new ArrayList<Content>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.offset);
        dest.writeInt(this.totalCount);
        dest.writeString(this.version);
        dest.writeTypedList(this.content);
    }

    public FavoriteModel() {
    }

    protected FavoriteModel(Parcel in) {
        this.offset = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalCount = in.readInt();
        this.version = in.readString();
        this.content = in.createTypedArrayList(Content.CREATOR);
    }

    public static final Parcelable.Creator<FavoriteModel> CREATOR = new Parcelable.Creator<FavoriteModel>() {
        @Override
        public FavoriteModel createFromParcel(Parcel source) {
            return new FavoriteModel(source);
        }

        @Override
        public FavoriteModel[] newArray(int size) {
            return new FavoriteModel[size];
        }
    };
}
