
package com.multitv.yuv.models.home;


import android.os.Parcel;
import android.os.Parcelable;

public class Version implements Parcelable {

    public String live_version;
    public String dash_version;
    public String epg_version;
    public String conf_version;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.live_version);
        dest.writeString(this.dash_version);
        dest.writeString(this.epg_version);
        dest.writeString(this.conf_version);
    }

    public Version() {
    }

    protected Version(Parcel in) {
        this.live_version = in.readString();
        this.dash_version = in.readString();
        this.epg_version = in.readString();
        this.conf_version = in.readString();
    }

    public static final Parcelable.Creator<Version> CREATOR = new Parcelable.Creator<Version>() {
        @Override
        public Version createFromParcel(Parcel source) {
            return new Version(source);
        }

        @Override
        public Version[] newArray(int size) {
            return new Version[size];
        }
    };
}
