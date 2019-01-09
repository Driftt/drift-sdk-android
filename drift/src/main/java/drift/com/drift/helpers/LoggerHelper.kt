package drift.com.drift.helpers

import drift.com.drift.Drift


internal object LoggerHelper {

    fun logMessage(TAG: String, message: String) {
        val loggerListener = Drift.loggerListener()
        loggerListener?.logMessage("$TAG: $message")
    }
}
