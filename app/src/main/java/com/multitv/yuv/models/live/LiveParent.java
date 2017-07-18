package com.multitv.yuv.models.live;

import java.util.ArrayList;
import java.util.List;

import com.multitv.yuv.models.home.Live;

/**
 * Created by naseeb on 1/18/2017.
 */

public class LiveParent {
    public String version;
    public int offset;
    public int totalcount;
    public List<Live> live = new ArrayList<>();
}
