package drift.com.drift.helpers;

import drift.com.drift.Drift;



public class LoggerHelper {

    public static void logMessage(String TAG, String message) {
        LoggerListener loggerListener = Drift.loggerListener();
        if (loggerListener != null) {
            loggerListener.logMessage(TAG + ": " + message);
        }
    }
}
