package drift.com.drift_sdk_android;

import android.app.Application;
import android.util.Log;

import drift.com.drift.Drift;
import drift.com.drift.helpers.LoggerListener;

import com.google.android.gms.security.ProviderInstaller;


/**
 * Created by eoin on 31/08/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        updateAndroidSecurityProvider();
        Drift.setupDrift(this, "");

        Drift.setLoggerListener(new LoggerListener() {
            @Override
            public void logMessage(String message) {
                Log.d("DRIFT_SDK", message);
            }
        });
    }


    //Only needed for API 19
    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
