package com.multitv.yuv.models;

import com.multitv.yuv.models.home.Live;

import java.util.ArrayList;

/**
 * Created by arungoyal on 30/06/17.
 */

public class SectionDataModel1 {

    private String headerTitle;
    private String sectionID;
    private ArrayList<Live> allItemsInSection;


    public SectionDataModel1(String headerTitle, String sectionID, ArrayList<Live> allItemsInSection) {
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


    public ArrayList<Live> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<Live> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }


}
