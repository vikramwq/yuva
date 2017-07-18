
package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Dashboard implements Parcelable {
    public List<Cat_cntn> feature_banner = new ArrayList<>();
    public List<Home_category> home_category = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.feature_banner);
        dest.writeList(this.home_category);
    }

    public Dashboard() {
    }

    protected Dashboard(Parcel in) {
        this.feature_banner = new ArrayList<Cat_cntn>();
        in.readList(this.feature_banner, Feature_banner.class.getClassLoader());
        this.home_category = new ArrayList<Home_category>();
        in.readList(this.home_category, Home_category.class.getClassLoader());
    }

    public static final Parcelable.Creator<Dashboard> CREATOR = new Parcelable.Creator<Dashboard>() {
        @Override
        public Dashboard createFromParcel(Parcel source) {
            return new Dashboard(source);
        }

        @Override
        public Dashboard[] newArray(int size) {
            return new Dashboard[size];
        }
    };
}
