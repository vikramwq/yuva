package com.multitv.yuv.utilities;

import android.content.Context;

import com.multitv.yuv.models.categories.Child;
import com.multitv.yuv.models.categories.Vod;
import com.multitv.yuv.sharedpreference.SharedPreference;

import static com.multitv.yuv.utilities.Constant.TYPE_MOVIES;
import static com.multitv.yuv.utilities.Constant.TYPE_TV_SHOWS;
import static com.multitv.yuv.utilities.Constant.TYPE_VIDEO;

/**
 * Created by naseeb on 11/18/2016.
 */

public class CachingCategoryUtils {

    public static void saveCategories(Context context, String parentCategoryId, int totalCategories,
                                      int numberOfCategoriesShown) {
        SharedPreference sharedPreference = new SharedPreference();
        sharedPreference.setPreferencesInt(context, parentCategoryId + "_" + "NUMBER_OF_CATEGORIES_SHOWN", numberOfCategoriesShown);
        sharedPreference.setPreferencesInt(context, parentCategoryId + "_" + "NUMBER_OF_TOTAL_CATEGORIES", totalCategories);
    }

    public static int getNumberOfCategoriesShown(Context context, String parentCategoryId) {
        SharedPreference sharedPreference = new SharedPreference();
        return sharedPreference.getPreferencesInt(context, parentCategoryId + "_" + "NUMBER_OF_CATEGORIES_SHOWN");
    }

   /* public static boolean isNeedToFetchNewCategories(Context context, String parentCategoryId) {
        SharedPreference sharedPreference = new SharedPreference();
        int numberOfCategoriesShown = sharedPreference.getPreferencesInt(context, parentCategoryId + "_" +  "NUMBER_OF_CATEGORIES_SHOWN");
        int totalNumberOfCategories = sharedPreference.getPreferencesInt(context, parentCategoryId + "_" +  "NUMBER_OF_TOTAL_CATEGORIES");

        if(totalNumberOfCategories == 0 || numberOfCategoriesShown == 0)
            return true;
        else if(numberOfCategoriesShown < totalNumberOfCategories)
            return true;
        else
            return false;
    }*/

    public static boolean isNoNeedToFetchNewCategories(Context context, int contentType, String parentCategoryId,
                                                       Vod vod, Child child) {
        SharedPreference sharedPreference = new SharedPreference();
        int numberOfCategoriesShown = sharedPreference.getPreferencesInt(context, parentCategoryId + "_" + "NUMBER_OF_CATEGORIES_SHOWN");
        int totalNumberOfCategories = sharedPreference.getPreferencesInt(context, parentCategoryId + "_" + "NUMBER_OF_TOTAL_CATEGORIES");

      /*  if(numberOfCategoriesShown > totalNumberOfCategories)
            return true;
        else
            return false;*/

        switch (contentType) {
            case TYPE_VIDEO:
                if (vod == null || vod.children == null || vod.children.size() == 0)
                    return true;
                else if (numberOfCategoriesShown != 0 && totalNumberOfCategories != 0 && numberOfCategoriesShown >= totalNumberOfCategories)
                    return true;
                else
                    return false;
            case TYPE_MOVIES:
            case TYPE_TV_SHOWS:
                if (child == null || child.children == null || child.children.size() == 0)
                    return true;
                else if (numberOfCategoriesShown != 0 && totalNumberOfCategories != 0 && numberOfCategoriesShown >= totalNumberOfCategories)
                    return true;
                else
                    return false;
        }

        return false;
    }
}
