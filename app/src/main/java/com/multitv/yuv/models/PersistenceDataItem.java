package com.multitv.yuv.models;

/**
 * Created by arungoyal on 10/05/17.
 */

public class PersistenceDataItem {

    private String contentId;
    private String data;
    private long duration;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
