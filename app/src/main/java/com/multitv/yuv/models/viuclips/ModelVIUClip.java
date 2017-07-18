//package mobito.com.intextv.models.viuclips;
//
//import android.content.Context;
//
//import com.viu.player.sdk.api.model.ViuClip;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.io.StreamCorruptedException;
//import java.util.ArrayList;
//
//import Tracer;
//
///**
// * Created by mkr on 11/2/2016.
// */
//
//public class ModelVIUClip implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//    private static final String TAG = "ModelVIUClip";
//    private static final String FILE_NAME = "MKR_Call_Reminder";
//    private static ModelVIUClip data;
//    private static Context context;
//    private int mCurrentPage;
//
//    private ViuClip[] mViuClips;
//
//    // =========================================================================================================
//    // MODEL METHODS RELATED TO CREATE AND SAVED OBJECT
//    // =========================================================================================================
//
//    // GET INSTANCE
//    public static ModelVIUClip getInstance(Context ctx) {
//        context = ctx;
//        if (data == null) {
//            data = getsavedInstance();
//            if (data == null) {
//                data = new ModelVIUClip();
//                data.saveInstance();
//            }
//        }
//        return data;
//    }
//
//    // GET SAVED INSTANCE FROM FILE
//    private static ModelVIUClip getsavedInstance() {
//        try {
//            FileInputStream fin = context.openFileInput(FILE_NAME);
//            ObjectInputStream in = new ObjectInputStream(fin);
//            return (ModelVIUClip) in.readObject();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        } catch (StreamCorruptedException e) {
//            e.printStackTrace();
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // SAVE INSTANCE INTO FILE
//    private boolean saveInstance() {
//        try {
//            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
//            ObjectOutputStream out = new ObjectOutputStream(fos);
//            out.writeObject(data);
//            out.flush();
//            return true;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public void setViewClipData(ViuClip[] viuClips) {
//        Tracer.error(TAG, "setViewClipData: " + viuClips);
//        mViuClips = viuClips;
//        saveInstance();
//    }
//
//    public ViuClip[] getViewClips() {
//        Tracer.error(TAG, "getViewClips: " + mViuClips);
//        return mViuClips;
//    }
//
//    public void setCurrentPage(int currentPage) {
//        Tracer.error(TAG, "setCurrentPage: " + currentPage);
//        mCurrentPage = currentPage;
//        saveInstance();
//    }
//
//    public int getCurrentPage() {
//        Tracer.error(TAG, "getCurrentPage: " + mViuClips);
//        return mCurrentPage;
//    }
//
//    public void setViewClipData(ArrayList<ViuClip> viuClips) {
//        ViuClip[] viuClipsArray = new ViuClip[viuClips.size()];
//        for (int i = 0; i < viuClips.size(); i++) {
//            viuClipsArray[i] = viuClips.get(i);
//        }
//        mViuClips = viuClipsArray;
//        Tracer.error(TAG, "setViewClipData: " + viuClips.size() + "    " + mViuClips.length);
//    }
//}
