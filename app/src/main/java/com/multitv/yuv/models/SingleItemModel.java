package com.multitv.yuv.models;

/**
 * Created by Sunil on 09-08-2016.
 */
public class SingleItemModel {


    private String name;
    private String url;
    private String description;
    private int channelicon;

    public int getChannelicon() {
        return channelicon;
    }

    public void setChannelicon(int channelicon) {
        this.channelicon = channelicon;
    }

    public SingleItemModel() {
    }

    public SingleItemModel(String name, String url) {
        this.name = name;
        this.url = url;
        this.channelicon=channelicon;
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


}
