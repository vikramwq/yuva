package com.multitv.yuv.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import com.multitv.yuv.models.video.Content;

/**
 * Created by root on 9/9/16.
 */
public class MyProgram implements Parcelable {
    int image;
    String tittle;
    String epName;
    String session;
    String count;
    public ArrayList<Content> content = new ArrayList<Content>();


    public MyProgram(int image,String tittle,String epName,String session,String count)
    {
        this.image=image;
        this.epName=epName;
        this.tittle=tittle;
        this.session=session;
        this.count=count;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getEpName() {
        return epName;
    }

    public void setEpName(String epName) {
        this.epName = epName;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.image);
        dest.writeString(this.tittle);
        dest.writeString(this.epName);
        dest.writeString(this.session);
        dest.writeString(this.count);
        dest.writeTypedList(this.content);
    }

    protected MyProgram(Parcel in) {
        this.image = in.readInt();
        this.tittle = in.readString();
        this.epName = in.readString();
        this.session = in.readString();
        this.count = in.readString();
        this.content = in.createTypedArrayList(Content.CREATOR);
    }

    public static final Parcelable.Creator<MyProgram> CREATOR = new Parcelable.Creator<MyProgram>() {
        @Override
        public MyProgram createFromParcel(Parcel source) {
            return new MyProgram(source);
        }

        @Override
        public MyProgram[] newArray(int size) {
            return new MyProgram[size];
        }
    };
}
