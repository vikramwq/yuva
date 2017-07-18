package com.multitv.yuv.interfaces;

/**
 * Created by root on 15/12/16.
 */

public interface ImageSenderInterface {

    void sendImage(String url);
    void viewPagerPosition(int position);
    void emptyImage(String url);
    void sendUserFiristName(String user_name);
    void sendUserLastName(String first_name,String user_last_name);
    void sendEmailId(String user_email);
    void sendMobileNumber(String user_phone);
}
