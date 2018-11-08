package drift.com.drift;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import net.danlew.android.joda.JodaTimeAndroid;

import drift.com.drift.activities.ConversationActivity;
import drift.com.drift.activities.ConversationListActivity;
import drift.com.drift.helpers.ApplicationLifecycleHelper;
import drift.com.drift.helpers.LoggerListener;
import drift.com.drift.helpers.LogoutHelper;
import drift.com.drift.managers.DriftManager;

public class Drift {

    private static Drift _drift = null;
    private Context context;
    private ApplicationLifecycleHelper applicationLifecycleHelper = new ApplicationLifecycleHelper();

    public LoggerListener mLoggerListener;

    private Drift(Context context) {
        this.context = context;
    }

    public static Drift setupDrift(Application application, String embedId) {
        _drift = new Drift(application);

        JodaTimeAndroid.init(application);

        application.registerActivityLifecycleCallbacks(_drift.applicationLifecycleHelper);
        DriftManager.getInstance().getDataFromEmbeds(embedId);

        return _drift;
    }

    public static void registerUser(String userId, String email) {
        DriftManager.getInstance().registerUser(userId, email);
    }

    public static void setLoggerListener(LoggerListener loggerListener){
        _drift.mLoggerListener = loggerListener;
    }

    @Nullable
    public static LoggerListener loggerListener() {
        return _drift.mLoggerListener;
    }

    public static void showConversationActivity() {
        ConversationListActivity.showFromContext(_drift.context);
    }

    public static void showCreateConversationActivity() {
        ConversationActivity.showCreateConversationFromContext(_drift.context);
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
