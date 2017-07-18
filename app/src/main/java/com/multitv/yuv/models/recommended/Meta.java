
package com.multitv.yuv.models.recommended;


import android.os.Parcel;
import android.os.Parcelable;

public class Meta implements Parcelable {

    public String star_cast;
    public String director;
    public String music_director;
    public String producer;
    public String year;
    public String rated;
    public String released;
    public String runtime;
    public String genre;
    public String writer;
    public String plot;
    public String language;
    public String country;
    public String award;
    public String poster;
    public String metascore;
    public String imdb_rating;
    public String imdb_vote;
    public String imbd_id;
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.star_cast);
        dest.writeString(this.director);
        dest.writeString(this.music_director);
        dest.writeString(this.producer);
        dest.writeString(this.year);
        dest.writeString(this.rated);
        dest.writeString(this.released);
        dest.writeString(this.runtime);
        dest.writeString(this.genre);
        dest.writeString(this.writer);
        dest.writeString(this.plot);
        dest.writeString(this.language);
        dest.writeString(this.country);
        dest.writeString(this.award);
        dest.writeString(this.poster);
        dest.writeString(this.metascore);
        dest.writeString(this.imdb_rating);
        dest.writeString(this.imdb_vote);
        dest.writeString(this.imbd_id);
        dest.writeString(this.type);
    }

    public Meta() {
    }

    protected Meta(Parcel in) {
        this.star_cast = in.readString();
        this.director = in.readString();
        this.music_director = in.readString();
        this.producer = in.readString();
        this.year = in.readString();
        this.rated = in.readString();
        this.released = in.readString();
        this.runtime = in.readString();
        this.genre = in.readString();
        this.writer = in.readString();
        this.plot = in.readString();
        this.language = in.readString();
        this.country = in.readString();
        this.award = in.readString();
        this.poster = in.readString();
        this.metascore = in.readString();
        this.imdb_rating = in.readString();
        this.imdb_vote = in.readString();
        this.imbd_id = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Meta> CREATOR = new Parcelable.Creator<Meta>() {
        @Override
        public Meta createFromParcel(Parcel source) {
            return new Meta(source);
        }

        @Override
        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };
}
