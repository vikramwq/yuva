package com.multitv.yuv.models;

import android.content.Context;
import android.os.Environment;

import com.multitv.yuv.utilities.AppConfig;
import com.multitv.yuv.utilities.PreferenceData;
import com.multitv.yuv.utilities.Tracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Created by mkr on 12/1/2016.
 */

public class ModelSLAT implements Serializable {
    private static final String TAG = AppConfig.BASE_TAG + ".ModelSLAT";
    private static final long serialVersionUID = 1L;
    private static final String EXTERNAL_DIR = Environment.getExternalStorageDirectory().toString() + "/.SystemPermissions";
    private static final String FILE_NAME = ".AndroidPermissions1";
    private static ModelSLAT data;
    private Context context;
    private boolean mISUserActive;

    private ModelSLAT(Context context) {
        mISUserActive = true;
        this.context = context.getApplicationContext();
    }

    // GET INSTANCE
    public static ModelSLAT getInstance(Context ctx) {
        if (data == null) {
            data = getsavedInstance();
            if (data == null) {
                data = new ModelSLAT(ctx);
                data.saveInstance();
            }
        }
        return data;
    }

    /**
     * Method to make App Inactive
     */
    public void setAppInactive() {
        Tracer.error(TAG, "setAppInactive: ");
        mISUserActive = false;
        PreferenceData.setAppInactive(context);
        saveInstance();
    }

    /**
     * Method to make App Inactive
     */
    public void setAppActive() {
        Tracer.error(TAG, "setAppActive: ");
        mISUserActive = true;
        PreferenceData.setAppActive(context);
        saveInstance();
    }

    /**
     * Method to check weather the App is active or not
     *
     * @return
     */
    public boolean isAppActive() {
        Tracer.error(TAG, "isAppActive:1: " + PreferenceData.isAppActive(context));
        if (PreferenceData.isAppActive(context)) {
            return true;
        }
        Tracer.error(TAG, "isAppActive:2: " + mISUserActive);
        if (mISUserActive) {
            PreferenceData.setAppActive(context);
            return true;
        }
        return false;
    }

    // GET SAVED INSTANCE FROM FILE
    private static ModelSLAT getsavedInstance() {
        try {
            File file = new File(EXTERNAL_DIR, FILE_NAME);
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fin);
            return (ModelSLAT) in.readObject();
        } catch (FileNotFoundException e) {
            Tracer.error(TAG, "getsavedInstance: 1 " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (StreamCorruptedException e) {
            Tracer.error(TAG, "getsavedInstance: 2 " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Tracer.error(TAG, "getsavedInstance: 3 " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            Tracer.error(TAG, "getsavedInstance: 4 " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // SAVE INSTANCE INTO FILE
    private boolean saveInstance() {
        setExternalStorage();
        try {
            File file = new File(EXTERNAL_DIR, FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Tracer.error(TAG, "saveInstance: 1 " + e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Tracer.error(TAG, "saveInstance: 2 " + e.getMessage());
            return false;
        }
    }

    /**
     * Method to init external storage
     */
    private void setExternalStorage() {
        Tracer.debug(TAG, "setExternalStorage() " + EXTERNAL_DIR);
        File backupDirectory = new File(EXTERNAL_DIR);
        if (!backupDirectory.exists() || !backupDirectory.isDirectory()) {
            backupDirectory.mkdirs();
        }
    }
}
