
package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

public class Thumb_ implements Parcelable {

    private String small;

    private String medium;

    private String large;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.small);
        dest.writeString(this.medium);
        dest.writeString(this.large);
    }

    public Thumb_() {
    }

    protected Thumb_(Parcel in) {
        this.small = in.readString();
        this.medium = in.readString();
        this.large = in.readString();
    }

    public static final Parcelable.Creator<Thumb_> CREATOR = new Parcelable.Creator<Thumb_>() {
        @Override
        public Thumb_ createFromParcel(Parcel source) {
            return new Thumb_(source);
        }

        @Override
        public Thumb_[] newArray(int size) {
            return new Thumb_[size];
        }
    };
}
