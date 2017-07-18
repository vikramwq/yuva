package com.multitv.yuv.models.home;

import java.util.List;

/**
 * Created by arungoyal on 30/06/17.
 */

public class LiveData {

    private String version;
    private int totalcount;
    private int offset;
    private List<Live> live;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<Live> getLive() {
        return live;
    }

    public void setLive(List<Live> live) {
        this.live = live;
    }
}
