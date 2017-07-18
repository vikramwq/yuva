package com.multitv.yuv.models.watching_get;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WatchingGet {

    @SerializedName("offset")
    @Expose
    private Boolean offset;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("content")
    @Expose
    private List<Content> content = new ArrayList<>();

    /**
     *
     * @return
     * The offset
     */
    public Boolean getOffset() {
        return offset;
    }

    /**
     *
     * @param offset
     * The offset
     */
    public void setOffset(Boolean offset) {
        this.offset = offset;
    }

    /**
     *
     * @return
     * The version
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param version
     * The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     *
     * @return
     * The content
     */
    public List<Content> getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(List<Content> content) {
        this.content = content;
    }

}