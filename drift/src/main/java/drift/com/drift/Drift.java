package drift.com.drift;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import net.danlew.android.joda.JodaTimeAndroid;

import drift.com.drift.activities.ConversationActivity;
import drift.com.drift.activities.ConversationListActivity;
import drift.com.drift.helpers.ApplicationLifecycleHelper;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.LoggerListener;
import drift.com.drift.helpers.LogoutHelper;
import drift.com.drift.managers.DriftManager;
import drift.com.drift.managers.SocketManager;

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
        LoggerHelper.logMessage("LIFECYCLE", "Setup Drift SDK");

        JodaTimeAndroid.init(application);

        application.registerActivityLifecycleCallbacks(_drift.applicationLifecycleHelper);
        DriftManager.getInstance().getDataFromEmbeds(embedId);

        return _drift;
    }

    public static void registerUser(String userId) {
        registerUser(userId, null, null);
    }

    public static void registerUser(String userId, String email) {
        registerUser(userId, email, null);
    }

    public static void registerUser(String userId, String email, String userJwt) {
        if (!isConnected()) {
            LoggerHelper.logMessage("LIFECYCLE", "Registering User");
            DriftManager.getInstance().registerUser(userId, email, userJwt);
        } else {
            LoggerHelper.logMessage("LIFECYCLE", "Not Registering User, already connected");
        }
    }

    public static Boolean isConnected() {
        return DriftManager.getInstance().loadingUser || SocketManager.getInstance().isConnected();
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
        LoggerHelper.logMessage("LIFECYCLE", "Logout");
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
