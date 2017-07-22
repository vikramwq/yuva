package com.multitv.yuv.models;

import com.multitv.yuv.models.home.Cat_cntn;

import java.util.ArrayList;

/**
 * Created by Created by Sunil on 09-08-2016.
 */
public class SectionDataModel {


    private String headerTitle;
    private String sectionID;
    private ArrayList<Cat_cntn> allItemsInSection;

    public int icon;

    public SectionDataModel(String headerTitle, String sectionID, ArrayList<Cat_cntn> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.sectionID = sectionID;
        this.allItemsInSection = allItemsInSection;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }


    public ArrayList<Cat_cntn> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<Cat_cntn> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }


}
