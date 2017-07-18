package com.multitv.yuv.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arungoyal on 17/07/17.
 */

public class InterestData {

    private String total_page;
    private String current_page;
    private String nextPage_url;
    private List<Interest> interests = new ArrayList<>();


    public String getTotal_page() {
        return total_page;
    }

    public void setTotal_page(String total_page) {
        this.total_page = total_page;
    }

    public String getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }

    public String getNextPage_url() {
        return nextPage_url;
    }

    public void setNextPage_url(String nextPage_url) {
        this.nextPage_url = nextPage_url;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }
}
