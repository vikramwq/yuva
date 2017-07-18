package com.multitv.yuv.models.genre;

import java.util.List;

/**
 * Created by arungoyal on 21/04/17.
 */

public class Response {

    private int code;
    private int total_count;
    private List<GenreItem> result;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<GenreItem> getResult() {
        return result;
    }

    public void setResult(List<GenreItem> result) {
        this.result = result;
    }
}
