package com.multitv.yuv.models.channels_by_cat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naseeb on 1/23/2017.
 */

public class ChannelsByCat {
    public String version;
    public int total_count;
    @SerializedName("data")
    public List<Channel> channelArrayList = new ArrayList<>();
}
