package com.multitv.yuv.utilities;

/**
 * Created by naseeb on 10/9/2016.
 */

public class VideoSourceUtils {

    public static String getVideoSource(String genre) {
        switch (genre) {
            case "dailymotion":
            case "youtube":
            case "vimeo":
                return genre;
            default:
                return "Intex";
        }
    }
}
