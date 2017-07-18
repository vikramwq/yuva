package com.multitv.yuv.imageprocess;

/**
 * Created by cyberlinks on 12/1/17.
 */

public interface ImageProcessorListener {

    void onSuccess();

    void onError(String error);
}
