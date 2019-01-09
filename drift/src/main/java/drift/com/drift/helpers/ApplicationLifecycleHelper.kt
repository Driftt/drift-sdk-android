package drift.com.drift.helpers

import android.app.Activity
import android.app.Application
import android.os.Bundle

import drift.com.drift.managers.PresentationManager
import drift.com.drift.model.Auth


internal class ApplicationLifecycleHelper : Application.ActivityLifecycleCallbacks {

    private var started: Int = 0
    private var resumed: Int = 0
    private var transitionPossible: Boolean = false
    private var rotating = false

    var currentActivity: Activity? = null

    private fun onApplicationStarted(activity: Activity?) {
        LoggerHelper.logMessage(TAG, "Application Did Start")
    }


    private fun onApplicationResumed(activity: Activity?) {
        LoggerHelper.logMessage(TAG, "Application Did Resume")
        val orgId = Auth.instance?.endUser?.orgId ?: return
        val endUserId = Auth.instance?.endUser?.id ?: return
        PresentationManager.instance.checkForUnreadMessagesToShow(orgId, endUserId)
    }

    private fun onApplicationPaused() {
        currentActivity = null
        LoggerHelper.logMessage(TAG, "Application Did Pause")
    }

    private fun onApplicationStopped() {
        currentActivity = null
        LoggerHelper.logMessage(TAG, "Application Did Stop")
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }

    override fun onActivityResumed(activity: Activity?) {

        currentActivity = activity

        if (rotating) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Resuming")
            PresentationManager.instance.checkWeNeedToReshowMessagePopover()
            rotating = false
            return
        }

        if (resumed == 0 && !transitionPossible) {
            onApplicationResumed(activity)
        }
        transitionPossible = false
        resumed++
    }

    override fun onActivityPaused(activity: Activity?) {
        if (activity?.isChangingConfigurations == true) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Pause")
            rotating = true
            return
        }
        transitionPossible = true
        resumed--
    }

    override fun onActivityStopped(activity: Activity?) {
        PresentationManager.instance.closePopupView()

        if (activity?.isChangingConfigurations == true) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Stop")
            rotating = true
            return
        }

        if (started == 1) {
            // We only know the application was paused when it's stopped (because transitions always pause activities)
            // http://developer.android.com/guide/components/activities.html#CoordinatingActivities
            if (transitionPossible && resumed == 0) {
                onApplicationPaused()
            }
            onApplicationStopped()
        }
        transitionPossible = false
        started--
    }

    override fun onActivityStarted(activity: Activity?) {
        currentActivity = activity

        if (rotating) {
            LoggerHelper.logMessage(TAG, "Activity Rotating, ignoring Starting")
            return
        }

        if (started == 0) {
            onApplicationStarted(activity)
        }

        started++
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        LoggerHelper.logMessage(TAG, "Activity Save Instance State")
    }

    override fun onActivityDestroyed(activity: Activity?) {
        LoggerHelper.logMessage(TAG, "Activity Destroyed")
    }

    companion object {

        private val TAG = ApplicationLifecycleHelper::class.java.simpleName
    }
}
