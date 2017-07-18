package com.multitv.yuv.utilities;

import android.content.Context;
import android.text.TextUtils;

import com.multitv.yuv.models.categories.Category;
import com.multitv.yuv.models.channels_by_cat.ChannelsByCat;
import com.multitv.yuv.models.menu.MenuModel;
import com.multitv.yuv.models.video.Video;
import com.multitv.yuv.application.AppController;

import com.multitv.yuv.models.FavoriteModel;
import com.multitv.yuv.models.home.Home;
import com.multitv.yuv.models.live.LiveParent;
import com.multitv.yuv.models.movies.Movies;
import com.multitv.yuv.models.recommended.Recommended;
import com.multitv.yuv.models.versions.Version;
import com.multitv.yuv.sharedpreference.SharedPreference;

/**
 * Created by naseeb on 11/3/2016.
 */

public class VersionUtils {

    public static boolean getIsHomeVersionChanged(Context context, Home home, String key) {
        Version version = getSavedVersion(context);
        if (version != null && home != null && home.version != null
                && home.version.dash_version != null
                && !home.version.dash_version.equalsIgnoreCase(version.dash_version)) {
            /*AppController.getInstance().getCacheManager().put(key, null);*/

            return true;
        }

        return false;
    }


    public static boolean getIsContentVersionChanged(Context context, Version updatedVersion) {
        Version version = getSavedVersion(context);
        if (version == null || updatedVersion == null) {
            return true;
        } else {
            if (updatedVersion.content_version != null
                    && !updatedVersion.content_version.equalsIgnoreCase(version.content_version)) {
                return true;
            }
        }
        return false;
    }


    public static boolean getIsCategoryVersionChanged(Context context, Version updatedVersion) {
        Version version = getSavedVersion(context);
        if (version == null || updatedVersion == null) {
            return true;
        } else {
            if (updatedVersion.category_version != null
                    && !updatedVersion.category_version.equalsIgnoreCase(version.category_version)) {
                return true;
            }
        }
        return false;
    }


    public static boolean getIsHomeVersionChanged1(Context context, Version updatedVersion) {
        Version version = getSavedVersion(context);
        if (version == null || updatedVersion == null) {
            return true;
        } else {
            if (updatedVersion.dash_version != null
                    && !updatedVersion.dash_version.equalsIgnoreCase(version.dash_version)) {
                return true;
            }
        }

        return false;
    }


    public static boolean getIsLiveVersionChanged(Context context, LiveParent liveParent, String key) {
        Version version = getSavedVersion(context);
        if (version != null && liveParent != null && liveParent.version != null
                && !liveParent.version.equalsIgnoreCase(version.live_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    public static boolean getIsChannelsByCatVersionChanged(Context context, ChannelsByCat channelsByCat, String key) {
        Version version = getSavedVersion(context);
        if (version != null && channelsByCat != null && channelsByCat.version != null
                && !channelsByCat.version.equalsIgnoreCase(version.live_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    public static boolean getIsContentVersionChanged(Context context, Video video, String key) {
        Version version = getSavedVersion(context);
        if (version != null && video != null && video.version != null
                && !video.version.equalsIgnoreCase(version.content_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    public static boolean getIsContentVersionChanged(Context context, FavoriteModel favoriteModel, String key) {
        Version version = getSavedVersion(context);
        if (version != null && favoriteModel != null && favoriteModel.version != null
                && !favoriteModel.version.equalsIgnoreCase(version.content_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }


    public static boolean getIsContentVersionChanged(Context context, Movies movies, String key) {
        Version version = getSavedVersion(context);
        if (version != null && movies != null && movies.version != null
                && !movies.version.equalsIgnoreCase(version.content_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    public static boolean getIsContentVersionChanged(Context context, Recommended recommended, String key) {
        Version version = getSavedVersion(context);
        if (version != null && recommended != null && recommended.version != null
                && !recommended.version.equalsIgnoreCase(version.content_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    public static boolean getIsMenuVersionChanged(Context context, MenuModel menuModel, String key) {
        Version version = getSavedVersion(context);
        if (version != null && menuModel != null && menuModel.version != null
                && !menuModel.version.equalsIgnoreCase(version.menu_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    public static boolean getIsCategoryVersionChanged(Context context, Category category, String key) {
        Version version = getSavedVersion(context);
        if (version != null && category != null && category.version != null
                && !category.version.equalsIgnoreCase(version.category_version)) {
            AppController.getInstance().getCacheManager().put(key, null);

            return true;
        }

        return false;
    }

    private static Version getSavedVersion(Context context) {
        SharedPreference sharedPreference = new SharedPreference();
        String versionApiResponse = sharedPreference.getPreferencesString(context, "VERSION");
        if (!TextUtils.isEmpty(versionApiResponse)) {
            Version version = Json.parse(versionApiResponse, Version.class);
            return version;
        }

        return null;
    }
}
