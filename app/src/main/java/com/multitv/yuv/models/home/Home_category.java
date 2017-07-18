package com.multitv.yuv.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 24/10/16.
 */
public class Home_category implements Parcelable {
    public String cat_id;
    public String cat_name;
    public List<Cat_cntn> cat_cntn = new ArrayList<Cat_cntn>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cat_id);
        dest.writeString(this.cat_name);
        dest.writeList(this.cat_cntn);
    }

    public Home_category() {
    }

    protected Home_category(Parcel in) {
        this.cat_id = in.readString();
        this.cat_name = in.readString();
        this.cat_cntn = new ArrayList<Cat_cntn>();
        in.readList(this.cat_cntn, Cat_cntn.class.getClassLoader());
    }

    public static final Parcelable.Creator<Home_category> CREATOR = new Parcelable.Creator<Home_category>() {
        @Override
        public Home_category createFromParcel(Parcel source) {
            return new Home_category(source);
        }

        @Override
        public Home_category[] newArray(int size) {
            return new Home_category[size];
        }
    };
}
