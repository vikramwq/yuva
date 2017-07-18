
package com.multitv.yuv.models.categories;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.multitv.yuv.models.home.Thumbnail;

public class Child implements Parcelable {
    public String id;
    public String name;
    public Thumbnail thumbnail;
    public String index;
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
        dest.writeList(this.children);
    }

    public Child() {
    }

    protected Child(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.thumbnail = in.readParcelable(Object.class.getClassLoader());
        this.index = in.readString();
        this.children = new ArrayList<Child>();
        in.readList(this.children, Child.class.getClassLoader());
    }

    public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel source) {
            return new Child(source);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };
}
