package drift.com.drift.helpers


import drift.com.drift.model.Message
import drift.com.drift.wrappers.ConversationReadWrapper


object MessageReadHelper {

    var TAG = MessageReadHelper::class.java.simpleName

    fun markMessageAsRead(message: Message?) {

        if (message == null) {
            return
        }

        ConversationReadWrapper.markMessageAsRead(message.id!!) { response ->
            if (!response) {
                LoggerHelper.logMessage(TAG, "Failed to mark conversation as read: " + message.id!!)
            }
        }
    }

    fun markMessageAsReadAlongWithPrevious(message: Message?) {

        if (message == null || message.id == null) {
            return
        }

        ConversationReadWrapper.markMessageAsReadAlongWithPrevious(message.id!!) { response ->
            if (!response) {
                LoggerHelper.logMessage(TAG, "Failed to mark conversation as read: " + message.id!!)
            }
        }
    }

}
