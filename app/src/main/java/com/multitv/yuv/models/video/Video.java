
package com.multitv.yuv.models.video;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import com.multitv.yuv.models.home.Cat_cntn;

public class Video implements Parcelable,Serializable {

    public Integer offset;
    public String version;
    public int totalCount;
    public ArrayList<Cat_cntn> content = new ArrayList<>();
    public ArrayList<Cat_cntn> feature = new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.offset);
        dest.writeString(this.version);
        dest.writeInt(this.totalCount);
        dest.writeTypedList(this.content);
        dest.writeTypedList(this.feature);
    }

    public Video() {
    }

    protected Video(Parcel in) {
        this.offset = (Integer) in.readValue(Integer.class.getClassLoader());
        this.version = in.readString();
        this.totalCount = in.readInt();
        this.content = in.createTypedArrayList(Cat_cntn.CREATOR);
        this.feature = in.createTypedArrayList(Cat_cntn.CREATOR);
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
