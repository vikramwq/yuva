package com.multitv.yuv.models.watching_get;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Content {

    @SerializedName("watched_duration")
    @Expose
    public String watchedDuration;
    @SerializedName("total_duration")
    @Expose
    public String totalDuration;
    @SerializedName("index")
    @Expose
    public String index;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("des")
    @Expose
    public String des;
    @SerializedName("category_id")
    @Expose
    public String categoryId;
    @SerializedName("language_id")
    @Expose
    public String languageId;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("price_type")
    @Expose
    public Integer priceType;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("thumbnail")
    @Expose
    public Thumbnail thumbnail;
    @SerializedName("likes")
    @Expose
    public Integer likes;
    @SerializedName("rating")
    @Expose
    public Object rating;
    @SerializedName("watch")
    @Expose
    public String watch;
    @SerializedName("favorite")
    @Expose
    public Integer favorite;
    @SerializedName("meta")
    @Expose
    public Meta meta;
    @SerializedName("keywords")
    @Expose
    public List<String> keywords = new ArrayList<String>();
    public String likes_count;
    public int social_like;
    public int social_view;
    public String media_type;

    /**
     * @return The watchedDuration
     */
    public String getWatchedDuration() {
        return watchedDuration;
    }

    /**
     * @param watchedDuration The watched_duration
     */
    public void setWatchedDuration(String watchedDuration) {
        this.watchedDuration = watchedDuration;
    }

    /**
     * @return The totalDuration
     */
    public String getTotalDuration() {
        return totalDuration;
    }

    /**
     * @param totalDuration The total_duration
     */
    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    /**
     * @return The index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index The index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The des
     */
    public String getDes() {
        return des;
    }

    /**
     * @param des The des
     */
    public void setDes(String des) {
        this.des = des;
    }

    /**
     * @return The categoryId
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId The category_id
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return The languageId
     */
    public String getLanguageId() {
        return languageId;
    }

    /**
     * @param languageId The language_id
     */
    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    /**
     * @return The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return The duration
     */
    public Object getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * @return The priceType
     */
    public Integer getPriceType() {
        return priceType;
    }

    /**
     * @param priceType The price_type
     */
    public void setPriceType(Integer priceType) {
        this.priceType = priceType;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The thumbnail
     */
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail The thumbnail
     */
    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return The likes
     */
    public Integer getLikes() {
        return likes;
    }

    /**
     * @param likes The likes
     */
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * @return The rating
     */
    public Object getRating() {
        return rating;
    }

    /**
     * @param rating The rating
     */
    public void setRating(Object rating) {
        this.rating = rating;
    }

    /**
     * @return The watch
     */
    public String getWatch() {
        return watch;
    }

    /**
     * @param watch The watch
     */
    public void setWatch(String watch) {
        this.watch = watch;
    }

    /**
     * @return The favorite
     */
    public Integer getFavorite() {
        return favorite;
    }

    /**
     * @param favorite The favorite
     */
    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    /**
     * @return The meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * @param meta The meta
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * @return The keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * @param keywords The keywords
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
