
package com.multitv.yuv.models.home;


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


    public String getStar_cast() {
        return star_cast;
    }

    public void setStar_cast(String star_cast) {
        this.star_cast = star_cast;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getMusic_director() {
        return music_director;
    }

    public void setMusic_director(String music_director) {
        this.music_director = music_director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getMetascore() {
        return metascore;
    }

    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdb_rating() {
        return imdb_rating;
    }

    public void setImdb_rating(String imdb_rating) {
        this.imdb_rating = imdb_rating;
    }

    public String getImdb_vote() {
        return imdb_vote;
    }

    public void setImdb_vote(String imdb_vote) {
        this.imdb_vote = imdb_vote;
    }

    public String getImbd_id() {
        return imbd_id;
    }

    public void setImbd_id(String imbd_id) {
        this.imbd_id = imbd_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
