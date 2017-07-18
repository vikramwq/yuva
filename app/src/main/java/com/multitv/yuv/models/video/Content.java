
package com.multitv.yuv.models.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Content implements Parcelable {
    @SerializedName("category_ids")
    public List<Integer> categoryIdsList;
    public String index;
    public String id;
    public String title;
    public String des;
    public String language_id;
    public String language;
    public String type;
    public String source;
    public String duration;
    public int price_type;
    public String url;
    public String likes_count;
    public int social_like;
    public int social_view;
    public String media_type;
    public Thumbnail thumbnail;
    public String date_created;
    public int likes;
    public String rating;
    public String watch;
    public String favorite;
    public Meta meta;
    public List<String> keywords = new ArrayList<>();
    public List<String> package_info = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        if (index != null ? !index.equals(content.index) : content.index != null) return false;
        if (id != null ? !id.equals(content.id) : content.id != null) return false;
        if (title != null ? !title.equals(content.title) : content.title != null) return false;
        if (des != null ? !des.equals(content.des) : content.des != null) return false;
        if (language_id != null ? !language_id.equals(content.language_id) : content.language_id != null)
            return false;
        if (language != null ? !language.equals(content.language) : content.language != null)
            return false;
        if (type != null ? !type.equals(content.type) : content.type != null) return false;
        if (source != null ? !source.equals(content.source) : content.source != null) return false;
        if (duration != null ? !duration.equals(content.duration) : content.duration != null)
            return false;
        if (date_created != null ? !date_created.equals(content.date_created) : content.date_created != null)
            return false;
       /* if (price_type != null ? !price_type.equals(content.price_type) : content.price_type != null)
            return false;*/
        if (url != null ? !url.equals(content.url) : content.url != null) return false;
        if (thumbnail != null ? !thumbnail.equals(content.thumbnail) : content.thumbnail != null)
            return false;
       /* if (likes != null ? !likes.equals(content.likes) : content.likes != null) return false;*/
        if (rating != null ? !rating.equals(content.rating) : content.rating != null) return false;
        if (watch != null ? !watch.equals(content.watch) : content.watch != null) return false;
        if (favorite != null ? !favorite.equals(content.favorite) : content.favorite != null)
            return false;
        if (meta != null ? !meta.equals(content.meta) : content.meta != null) return false;
        if (keywords != null ? !keywords.equals(content.keywords) : content.keywords != null)
            return false;
        return !(package_info != null ? !package_info.equals(content.package_info) : content.package_info != null);

    }

    @Override
    public int hashCode() {
        int result = index != null ? index.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (des != null ? des.hashCode() : 0);
        result = 31 * result + (language_id != null ? language_id.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
       /* result = 31 * result + (price_type != null ? price_type.hashCode() : 0);*/
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
       /* result = 31 * result + (likes != null ? likes.hashCode() : 0);*/
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (watch != null ? watch.hashCode() : 0);
        result = 31 * result + (favorite != null ? favorite.hashCode() : 0);
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        result = 31 * result + (package_info != null ? package_info.hashCode() : 0);
        return result;
    }

    protected Content(Parcel in) {
        id = in.readString();
        title = in.readString();
        des = in.readString();
        language_id = in.readString();
        language = in.readString();
        type = in.readString();
        source = in.readString();
        watch = in.readString();
        package_info = in.createStringArrayList();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(des);
        dest.writeString(language_id);
        dest.writeString(language);
        dest.writeString(type);
        dest.writeString(source);
        dest.writeString(watch);
        dest.writeStringList(package_info);
    }
}

