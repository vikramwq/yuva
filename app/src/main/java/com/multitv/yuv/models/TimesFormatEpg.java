package com.multitv.yuv.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 17/11/16.
 */

public class TimesFormatEpg implements Parcelable {
    public String minutes;
    public String Hours;
    public String second;
    public String currentTimeFormat;
    public String CurrentDate;
    public String saveDateFormat;

    public String getSaveDateFormat() {
        return saveDateFormat;
    }

    public String getCurrentTimeFormat() {
        return currentTimeFormat;
    }

    public void setCurrentTimeFormat(String currentTimeFormat) {
        this.currentTimeFormat = currentTimeFormat;
    }

    public void setSaveDateFormat(String saveDateFormat) {
        this.saveDateFormat = saveDateFormat;
    }

    public TimesFormatEpg(){

    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }



    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getHours() {
        return Hours;
    }

    public void setHours(String hours) {
        Hours = hours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.minutes);
        dest.writeString(this.Hours);
        dest.writeString(this.second);
        dest.writeString(this.currentTimeFormat);
        dest.writeString(this.CurrentDate);
        dest.writeString(this.saveDateFormat);
    }

    protected TimesFormatEpg(Parcel in) {
        this.minutes = in.readString();
        this.Hours = in.readString();
        this.second = in.readString();
        this.currentTimeFormat = in.readString();
        this.CurrentDate = in.readString();
        this.saveDateFormat = in.readString();
    }

    public static final Parcelable.Creator<TimesFormatEpg> CREATOR = new Parcelable.Creator<TimesFormatEpg>() {
        @Override
        public TimesFormatEpg createFromParcel(Parcel source) {
            return new TimesFormatEpg(source);
        }

        @Override
        public TimesFormatEpg[] newArray(int size) {
            return new TimesFormatEpg[size];
        }
    };
}
