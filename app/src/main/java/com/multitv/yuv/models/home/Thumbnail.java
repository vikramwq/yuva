
package com.multitv.yuv.models.home;


import android.os.Parcel;
import android.os.Parcelable;

public class Thumbnail implements Parcelable {

    public String large;
    public String medium;
    public String small;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.large);
        dest.writeString(this.medium);
        dest.writeString(this.small);
    }

    public Thumbnail() {
    }

    protected Thumbnail(Parcel in) {
        this.large = in.readString();
        this.medium = in.readString();
        this.small = in.readString();
    }

    public static final Parcelable.Creator<Thumbnail> CREATOR = new Parcelable.Creator<Thumbnail>() {
        @Override
        public Thumbnail createFromParcel(Parcel source) {
            return new Thumbnail(source);
        }

        @Override
        public Thumbnail[] newArray(int size) {
            return new Thumbnail[size];
        }
    };
}
