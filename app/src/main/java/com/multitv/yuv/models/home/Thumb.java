
package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

public class Thumb implements Parcelable {


    private String name;

    private Thumb_ thumb;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Thumb_ getThumb() {
        return thumb;
    }

    public void setThumb(Thumb_ thumb) {
        this.thumb = thumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable(this.thumb, flags);
    }

    public Thumb() {
    }

    protected Thumb(Parcel in) {
        this.name = in.readString();
        this.thumb = in.readParcelable(Thumb_.class.getClassLoader());
    }

    public static final Parcelable.Creator<Thumb> CREATOR = new Parcelable.Creator<Thumb>() {
        @Override
        public Thumb createFromParcel(Parcel source) {
            return new Thumb(source);
        }

        @Override
        public Thumb[] newArray(int size) {
            return new Thumb[size];
        }
    };
}
