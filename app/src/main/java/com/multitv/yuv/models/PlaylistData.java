package com.multitv.yuv.models;

import java.util.List;

/**
 * Created by arungoyal on 17/07/17.
 */

public class PlaylistData {

    private List<PlaylistItem> playlist;


    public List<PlaylistItem> getPlayList() {
        return playlist;
    }

    public void setPlayList(List<PlaylistItem> playList) {
        this.playlist = playList;
    }
}
