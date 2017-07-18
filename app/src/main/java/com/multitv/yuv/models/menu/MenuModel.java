package com.multitv.yuv.models.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by naseeb on 10/15/2016.
 */

public class MenuModel {

     @SerializedName("ad_sdk")
     @Expose
     public String mAdSdk;

     @SerializedName("left_menu_thmb")
     @Expose
     public String leftMenuThumb;

     @SerializedName("version")
     @Expose
     public String version;

     @SerializedName("theme_color")
     @Expose
     public String themeColor;

     @SerializedName("left_menu")
     @Expose
     public ArrayList<LeftMenu> leftMenuArrayList;

     @SerializedName("top_menu")
     @Expose
     public ArrayList<TopMenu> topMenuArrayList;

     public class LeftMenu {
          @SerializedName("id")
          @Expose
          public String id;

          @SerializedName("page_title")
          @Expose
          public String pageTitle;

          @SerializedName("page_description")
          @Expose
          public String pageDescription;

          @SerializedName("created")
          @Expose
          public String created;

          @SerializedName("url")
          @Expose
          public String url;

          @SerializedName("identifier")
          @Expose
          public String identifier;

          @SerializedName("thumbnail")
          @Expose
          public String thumbnail;
     }

     public class TopMenu {
          @SerializedName("id")
          @Expose
          public String id;

          @SerializedName("page_title")
          @Expose
          public String pageTitle;

          @SerializedName("page_description")
          @Expose
          public String pageDescription;

          @SerializedName("created")
          @Expose
          public String created;

          @SerializedName("url")
          @Expose
          public String url;

          @SerializedName("identifier")
          @Expose
          public String identifier;

          @SerializedName("thumbnail")
          @Expose
          public String thumbnail;
     }
}

