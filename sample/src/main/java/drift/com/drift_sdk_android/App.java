package drift.com.drift_sdk_android;

import android.app.Application;

import drift.com.drift.Drift;

/**
 * Created by eoin on 31/08/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Drift.setupDrift(this, "teergsg9bb5d");
//        Drift.setupDrift(this, "x2akzxwz6gun");

    }
}
