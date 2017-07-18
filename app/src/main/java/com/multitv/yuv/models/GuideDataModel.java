package com.multitv.yuv.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by delhivery on 9/8/2016.
 */

public class GuideDataModel implements Parcelable {

    private String name;
    private String url;
    private String description;


    public GuideDataModel() {
    }

    public GuideDataModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.description);
    }

    protected GuideDataModel(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<GuideDataModel> CREATOR = new Parcelable.Creator<GuideDataModel>() {
        @Override
        public GuideDataModel createFromParcel(Parcel source) {
            return new GuideDataModel(source);
        }

        @Override
        public GuideDataModel[] newArray(int size) {
            return new GuideDataModel[size];
        }
    };
}
