package drift.com.drift.helpers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.Nullable;

import drift.com.drift.managers.PresentationManager;
import drift.com.drift.model.Auth;


public class ApplicationLifecycleHelper implements Application.ActivityLifecycleCallbacks {

    private static String TAG = ApplicationLifecycleHelper.class.getSimpleName();

    private int started;
    private int resumed;
    private boolean transitionPossible;
    private boolean rotating = false;

    @Nullable
    public Activity currentActivity;

    public ApplicationLifecycleHelper() {
    }


    void onApplicationStarted(Activity activity){
        LoggerHelper.logMessage(TAG, "Application Did Start");
    }

    void onApplicationResumed(Activity activity){
        LoggerHelper.logMessage(TAG, "Application Did Resume");
        Auth auth = Auth.getInstance();
        if (auth != null && auth.endUser != null && auth.endUser.id != null) {
            PresentationManager.getInstance().checkForUnreadMessagesToShow(auth.endUser.orgId, auth.endUser.id);
        }
    }

    void onApplicationPaused() {
        currentActivity = null;
        LoggerHelper.logMessage(TAG, "Application Did Pause");
    }

    void onApplicationStopped(){
        currentActivity = null;
        LoggerHelper.logMessage(TAG, "Application Did Stop");
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

        currentActivity = activity;

        if (rotating) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Resuming");
            PresentationManager.getInstance().checkWeNeedToReshowMessagePopover();
            rotating = false;
            return;
        }

        if (resumed == 0 && !transitionPossible) {
            onApplicationResumed(activity);
        }
        transitionPossible = false;
        resumed++;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity.isChangingConfigurations()) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Pause");
            rotating = true;
            return;
        }
        transitionPossible = true;
        resumed--;
    }

    @Override
    public void onActivityStopped(Activity activity) {

        PresentationManager.getInstance().closePopupView();

        if (activity.isChangingConfigurations()) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Stop");
            rotating = true;
            return;
        }

        if (started == 1) {
            // We only know the application was paused when it's stopped (because transitions always pause activities)
            // http://developer.android.com/guide/components/activities.html#CoordinatingActivities
            if (transitionPossible && resumed == 0) {
                onApplicationPaused();
            }
            onApplicationStopped();
        }
        transitionPossible = false;
        started--;
    }

    @Override
    public void onActivityStarted(Activity activity) {

        currentActivity = activity;

        if (rotating) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Starting");
            return;
        }

        if (started == 0){
            onApplicationStarted(activity);
        }

        started++;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        LoggerHelper.logMessage(TAG, "Activity Save Instance State");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LoggerHelper.logMessage(TAG, "Activity Destroyed");
    }
}
