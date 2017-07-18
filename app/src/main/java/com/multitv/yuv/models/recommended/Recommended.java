
package com.multitv.yuv.models.recommended;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import com.multitv.yuv.models.home.Cat_cntn;

public class Recommended implements Parcelable {

    public int offset;
    public int totalCount;
    public int current_count;
    public String version;
    public ArrayList<Cat_cntn> content = new ArrayList<Cat_cntn>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.offset);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.current_count);
        dest.writeString(this.version);
        dest.writeTypedList(this.content);
    }

    public Recommended() {
    }

    protected Recommended(Parcel in) {
        this.offset = in.readInt();
        this.totalCount = in.readInt();
        this.current_count = in.readInt();
        this.version = in.readString();
        this.content = in.createTypedArrayList(Cat_cntn.CREATOR);
    }

    public static final Parcelable.Creator<Recommended> CREATOR = new Parcelable.Creator<Recommended>() {
        @Override
        public Recommended createFromParcel(Parcel source) {
            return new Recommended(source);
        }

        @Override
        public Recommended[] newArray(int size) {
            return new Recommended[size];
        }
    };
}
