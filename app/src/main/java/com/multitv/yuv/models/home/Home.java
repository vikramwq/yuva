
package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Home implements Parcelable {
    public Version version;
    public int display_offset;
    public int display_count;
    public Dashboard dashboard;
    public ArrayList<Live> live = new ArrayList<>();
    public ArrayList<Cat_cntn> recomended = new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.version, flags);
        dest.writeInt(this.display_offset);
        dest.writeInt(this.display_count);
        dest.writeParcelable(this.dashboard, flags);
        dest.writeList(this.live);
        dest.writeTypedList(this.recomended);
    }

    public Home() {
    }

    protected Home(Parcel in) {
        this.version = in.readParcelable(Version.class.getClassLoader());
        this.display_offset = in.readInt();
        this.display_count = in.readInt();
        this.dashboard = in.readParcelable(Dashboard.class.getClassLoader());
        this.live = new ArrayList<Live>();
        in.readList(this.live, Live.class.getClassLoader());
        this.recomended = in.createTypedArrayList(Cat_cntn.CREATOR);
    }

    public static final Parcelable.Creator<Home> CREATOR = new Parcelable.Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel source) {
            return new Home(source);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };
}
