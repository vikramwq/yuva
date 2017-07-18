package com.multitv.yuv.models.notification_center;

import java.util.ArrayList;

/**
 * Created by cyberlinks on 27/12/16.
 */

public class NotificationsModel {

    public int offset;
    public int total_count;
    public String version;
    public ArrayList<NotificationContent> content = new ArrayList<NotificationContent>();

}
