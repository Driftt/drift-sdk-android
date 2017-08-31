package drift.com.drift.helpers;

import android.util.Log;

import drift.com.drift.Drift;



public class LoggerHelper {

    public static void logMessage(String TAG, String message) {
        if (Drift.isDebug()) {
            Log.d("DRIFTSDK", TAG + ": " + message);
        }
    }

}
