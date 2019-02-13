package drift.com.drift

import android.app.Activity
import android.app.Application
import android.content.Context

import net.danlew.android.joda.JodaTimeAndroid

import drift.com.drift.activities.ConversationActivity
import drift.com.drift.activities.ConversationListActivity
import drift.com.drift.helpers.ApplicationLifecycleHelper
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.helpers.LoggerListener
import drift.com.drift.helpers.LogoutHelper
import drift.com.drift.managers.DriftManager
import drift.com.drift.managers.SocketManager

class Drift private constructor() {
    private val applicationLifecycleHelper = ApplicationLifecycleHelper()

    var mLoggerListener: LoggerListener? = null

    companion object {

        private lateinit var _drift: Drift

        fun setupDrift(application: Application, embedId: String): Drift {
            _drift = Drift()
            LoggerHelper.logMessage("LIFECYCLE", "Setup Drift SDK")

            JodaTimeAndroid.init(application)

            application.registerActivityLifecycleCallbacks(_drift.applicationLifecycleHelper)
            DriftManager.getDataFromEmbeds(embedId)

            return _drift
        }

        fun registerUser(userId: String, email: String? = null, userJwt: String? = null) {
            if (!isConnected) {
                LoggerHelper.logMessage("LIFECYCLE", "Registering User")
                DriftManager.registerUser(userId, email, userJwt)
            } else {
                LoggerHelper.logMessage("LIFECYCLE", "Not Registering User, already connected")
            }
        }

        val isConnected: Boolean
            get() = DriftManager.loadingUser == true || SocketManager.isConnected

        fun setLoggerListener(loggerListener: LoggerListener) {
            _drift.mLoggerListener = loggerListener
        }

        fun loggerListener(): LoggerListener? {
            return _drift.mLoggerListener
        }

        fun showConversationActivity(context: Context) {
            ConversationListActivity.showFromContext(context)
        }

        fun showCreateConversationActivity(context: Context) {
            ConversationActivity.showCreateConversationFromContext(context)
        }

        fun logout() {
            LoggerHelper.logMessage("LIFECYCLE", "Logout")
            LogoutHelper.logout()
        }

        internal fun getContext() : Context? {
            return currentActivity?.applicationContext
        }

        val currentActivity: Activity?
            get() = _drift.applicationLifecycleHelper.currentActivity
    }
}
