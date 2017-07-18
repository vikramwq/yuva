package com.multitv.yuv.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by root on 25/8/16.
 */
public class Item implements Parcelable {

    private int mDrawableRes;

    private String mTitle;
    String name;

    public Item(@DrawableRes int drawable, String name) {
        this.mDrawableRes = drawable;
        this.name=name;

    }



    public int getDrawableResource() {
        return mDrawableRes;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDrawableRes);
        dest.writeString(this.mTitle);
        dest.writeString(this.name);
    }

    protected Item(Parcel in) {
        this.mDrawableRes = in.readInt();
        this.mTitle = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}