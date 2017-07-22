package com.multitv.yuv.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by arungoyal on 17/07/17.
 */

public class ChannelsData extends RealmObject {
    @PrimaryKey
    private int id;
    private int cur_page;
    private int total_page;
    private String next_page;
    private String previous_page;
    private String profile_base;
    private String banner_page;
    private String version;
    private RealmList<Channel> channels;


    public int getCur_page() {
        return cur_page;
    }

    public void setCur_page(int cur_page) {
        this.cur_page = cur_page;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public String getNext_page() {
        return next_page;
    }

    public void setNext_page(String next_page) {
        this.next_page = next_page;
    }

    public String getPrevious_page() {
        return previous_page;
    }

    public void setPrevious_page(String previous_page) {
        this.previous_page = previous_page;
    }

    public String getProfile_base() {
        return profile_base;
    }

    public void setProfile_base(String profile_base) {
        this.profile_base = profile_base;
    }

    public String getBanner_page() {
        return banner_page;
    }

    public void setBanner_page(String banner_page) {
        this.banner_page = banner_page;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(RealmList<Channel> channels) {
        this.channels = channels;
    }

    public String getVersion() {
        return version;
    }
}
