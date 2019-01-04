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

class Drift private constructor(private val context: Context) {
    private val applicationLifecycleHelper = ApplicationLifecycleHelper()

    var mLoggerListener: LoggerListener

    companion object {

        private var _drift: Drift? = null

        fun setupDrift(application: Application, embedId: String): Drift {
            _drift = Drift(application)
            LoggerHelper.logMessage("LIFECYCLE", "Setup Drift SDK")

            JodaTimeAndroid.init(application)

            application.registerActivityLifecycleCallbacks(_drift!!.applicationLifecycleHelper)
            DriftManager.instance.getDataFromEmbeds(embedId)

            return _drift
        }

        fun registerUser(userId: String, email: String) {
            if (!isConnected) {
                LoggerHelper.logMessage("LIFECYCLE", "Registering User")
                DriftManager.instance.registerUser(userId, email)
            } else {
                LoggerHelper.logMessage("LIFECYCLE", "Not Registering User, already connected")
            }
        }

        val isConnected: Boolean
            get() = DriftManager.instance.loadingUser!! || SocketManager.instance.isConnected

        fun setLoggerListener(loggerListener: LoggerListener) {
            _drift!!.mLoggerListener = loggerListener
        }

        fun loggerListener(): LoggerListener? {
            return _drift!!.mLoggerListener
        }

        fun showConversationActivity() {
            ConversationListActivity.showFromContext(_drift!!.context)
        }

        fun showCreateConversationActivity() {
            ConversationActivity.showCreateConversationFromContext(_drift!!.context)
        }

        fun logout() {
            LoggerHelper.logMessage("LIFECYCLE", "Logout")
            LogoutHelper.logout()
        }

        fun getContext(): Context {
            return _drift!!.context
        }

        val currentActivity: Activity?
            get() = _drift!!.applicationLifecycleHelper.currentActivity
    }
}
