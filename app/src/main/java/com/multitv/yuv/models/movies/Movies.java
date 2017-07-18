
package com.multitv.yuv.models.movies;

import java.util.ArrayList;
import java.util.List;

public class Movies {

    public Integer offset;
    public String version;
    public int totalCount;
    public ArrayList<Content> content = new ArrayList<Content>();
    public List<Fearture_movie_banner> feature = new ArrayList<Fearture_movie_banner>();

}
