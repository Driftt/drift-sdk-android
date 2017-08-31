package drift.com.drift;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import drift.com.drift.activities.ConversationListActivity;
import drift.com.drift.helpers.ApplicationLifecycleHelper;
import drift.com.drift.helpers.LogoutHelper;
import drift.com.drift.managers.DriftManager;

public class Drift {

    private static Drift _drift = null;
    private Context context;
    public Boolean debugMode = false;
    private ApplicationLifecycleHelper applicationLifecycleHelper = new ApplicationLifecycleHelper();

    Drift(Context context) {
        this.context = context;
    }

    public static Drift setupDrift(Application application, String embedId) {
        _drift = new Drift(application);

        application.registerActivityLifecycleCallbacks(_drift.applicationLifecycleHelper);
        DriftManager.getInstance().getDataFromEmbeds(embedId);

        return _drift;
    }

    public static void registerUser(String userId, String email) {
        DriftManager.getInstance().registerUser(userId, email);
    }

    public static Boolean isDebug() {
        return _drift.debugMode;
    }


    public static void showConversationActivity() {
        ConversationListActivity.showFromContext(_drift.context);
    }

    public static void logout(){
        LogoutHelper.logout();
    }

    public static Context getContext() {
        return _drift.context;
    }

    @Nullable
    public static Activity getCurrentActivity(){

        return _drift.applicationLifecycleHelper.currentActivity;

    }
}
