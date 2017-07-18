
package com.multitv.yuv.models.categories;

import android.os.Parcel;
import android.os.Parcelable;

import com.multitv.yuv.models.home.Thumbnail;

import java.util.ArrayList;
import java.util.List;


public class Vod implements Parcelable {
    public String id;
    public String name;
    public Thumbnail thumbnail;
    public String index;
    public String layout;
    public List<Child> children = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeString(this.index);
        dest.writeString(this.layout);
        dest.writeList(this.children);
    }

    public Vod() {
    }

    protected Vod(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.thumbnail = in.readParcelable(Object.class.getClassLoader());
        this.index = in.readString();
        this.layout = in.readString();
        this.children = new ArrayList<Child>();
        in.readList(this.children, Child.class.getClassLoader());
    }

    public static final Parcelable.Creator<Vod> CREATOR = new Parcelable.Creator<Vod>() {
        @Override
        public Vod createFromParcel(Parcel source) {
            return new Vod(source);
        }

        @Override
        public Vod[] newArray(int size) {
            return new Vod[size];
        }
    };
}
