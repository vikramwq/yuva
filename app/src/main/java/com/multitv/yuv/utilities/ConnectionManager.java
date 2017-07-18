package com.multitv.yuv.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

/**
 * Class used to Know the status of the Network Connection
 */
public class ConnectionManager implements ConnectionClassManager.ConnectionClassStateChangeListener {
    private static final String TAG = "ConnectionManager";
    private static ConnectionManager mConnectionManager;
    private ConnectionQuality mConnectionQuality;
    private boolean mIsFirstRun;
    private Context mContext;
    private NetworkRepoter mNetworkRepoter;

    /**
     * Constructor
     *
     * @param context
     */
    private ConnectionManager(Context context) {
        Tracer.error(TAG, "ConnectionManager: ");
        mContext = context.getApplicationContext();
    }

    /**
     * Method to get the Current Instance of this class
     *
     * @param context
     * @return
     */
    public static ConnectionManager getInstance(Context context) {
        Tracer.error(TAG, "getInstance: ");
        if (mConnectionManager == null) {
            mConnectionManager = new ConnectionManager(context.getApplicationContext());
            mConnectionManager.initConnectionListener();
        }
        return mConnectionManager;
    }

    @Override
    public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
        Tracer.error(TAG, "onBandwidthStateChange: " + bandwidthState);
        mConnectionQuality = bandwidthState;
    }

    /**
     * Method to check weather the Device is connected or not
     *
     * @return
     */
    public boolean isConnected() {
        Tracer.error(TAG, "isConnected: " + getConnectionQuality());
        return NetworkUtils.isNetworkAvailable(mContext);
//        return getConnectionQuality() != ConnectionQuality.UNKNOWN;
    }

    /**
     * Method to get the Quality of the Current Connection
     *
     * @return
     */
    public ConnectionQuality getConnectionQuality() {
        return ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
    }

    /**
     * Method to start the Connection tracking
     */
    public void startConnectionTracking() {
        Tracer.error(TAG, "startConnectionTracking: ");
        DeviceBandwidthSampler.getInstance().startSampling();
    }

    /**
     * Method to run the Process to listen the network state.<b> Method calling between from Activity.onStart()---Activity.onPause().<br>
     * Called stopNetworkStateListener().
     */
    public void runNetworkStateListener(Context context) {
        Tracer.error(TAG, "runNetworkStateListener: ");
        stopNetworkStateListener();
        mNetworkRepoter = new NetworkRepoter(context);
        mNetworkRepoter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Method to stop the network state listen process
     */
    public void stopNetworkStateListener() {
        Tracer.error(TAG, "stopNetworkStateListener: ");
        if (mNetworkRepoter != null) {
            mNetworkRepoter.stopTask();
        }
    }

    /**
     * Method to init Connection listener to listen the Connection state
     */
    private void initConnectionListener() {
        Tracer.error(TAG, "initConnectionListener: ");
        ConnectionClassManager.getInstance().register(this);
    }

    /**
     * Class used to show the network report
     */
    private class NetworkRepoter extends AsyncTask<Void, ConnectionQuality, Void> {
        private static final String TAG = "NetworkRepoter";
        private Context mContext;
        private boolean mIsRunning;

        /**
         * Constructor
         *
         * @param context
         */
        public NetworkRepoter(Context context) {
            mContext = context;
            mIsRunning = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (isRunning()) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(getConnectionQuality());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ConnectionQuality... values) {
            super.onProgressUpdate(values);
            try {
                switch (values[0]) {
                    case POOR:
                        Toast.makeText(mContext, "You have slow network", Toast.LENGTH_SHORT).show();
                        break;
                    case UNKNOWN:
                        Toast.makeText(mContext, "No Internet Access", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Method to stop the Task
         */
        public void stopTask() {
            synchronized (TAG) {
                mIsRunning = false;
            }
        }

        /**
         * Method to check weather the task is running or not
         *
         * @return
         */
        private boolean isRunning() {
            synchronized (TAG) {
                return mIsRunning;
            }
        }
    }
}
