package com.multitv.yuv.models;

/**
 * Created by arungoyal on 17/07/17.
 */

public class Channel {


    private String id;
    private String first_name;
    private String last_name;

    private String description;

    private String prfile_pic;
    private String banner_image;
    private String created_on;
    private String total_subscribers;
    private String is_subscriber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrfile_pic() {
        return prfile_pic;
    }

    public void setPrfile_pic(String prfile_pic) {
        this.prfile_pic = prfile_pic;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getTotal_subscribers() {
        return total_subscribers;
    }

    public void setTotal_subscribers(String total_subscribers) {
        this.total_subscribers = total_subscribers;
    }

    public String getIs_subscriber() {
        return is_subscriber;
    }

    public void setIs_subscriber(String is_subscriber) {
        this.is_subscriber = is_subscriber;
    }
}
