package com.multitv.yuv.models.versions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by naseeb on 11/3/2016.
 */

public class Version extends RealmObject {
    @PrimaryKey
    private int id;
    public String live_version;
    public String dash_version;
    public String solyliv_version;
    public String content_version;
    public String category_version;
    public String menu_version;
    public String genre_version;
    public String epg_version;
    public String conf_version;
    public String package_version;
}
